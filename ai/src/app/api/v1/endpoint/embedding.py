# 모델 로드하는 경우(hugging face api가 아닌 로컬 다운)
# from fastapi import APIRouter, HTTPException, status
# from pydantic import BaseModel, Field
# from app.lifespan import ml_models
# import logging

# # 로그 기록 설정
# logger = logging.getLogger(__name__)
# router = APIRouter()

# # 1. 자바에서 보낼 데이터 규격 (유효성 검사 포함)
# class EmbeddingRequest(BaseModel):
#     text: str = Field(..., min_length=1, description="임베딩할 도서의 제목이나 줄거리")

# # 2. 자바에 돌려줄 응답 규격
# class EmbeddingResponse(BaseModel):
#     embedding: list[float] = Field(..., description="KURE-v1 모델이 생성한 1024차원 벡터")

# @router.post(
#     "/embed", 
#     response_model=EmbeddingResponse,
#     status_code=status.HTTP_200_OK,
#     summary="실시간 단일 텍스트 임베딩 생성"
# )
# async def get_embedding(request: EmbeddingRequest):
#     """
#     자바 서버로부터 받은 텍스트를 KURE-v1 모델을 사용하여 벡터로 변환합니다.
#     """
#     # [체크] 모델 로드 여부 확인
#     if "embedding_model" not in ml_models:
#         logger.error("AI Model (KURE-v1) is not loaded in ml_models.")
#         raise HTTPException(
#             status_code=status.HTTP_503_SERVICE_UNAVAILABLE, 
#             detail="AI 모델이 아직 준비되지 않았습니다. 서버 로그를 확인하세요."
#         )

#     try:
#         # [실행] 임베딩 생성
#         model = ml_models["embedding_model"]
        
#         # SentenceTransformer의 encode는 기본적으로 CPU/GPU 자원을 사용하므로 
#         # 단일 텍스트 처리 시 매우 빠릅니다.
#         # .tolist()를 호출하여 JSON 응답이 가능한 파이썬 리스트로 변환합니다.
#         vector = model.encode(request.text).tolist()
        
#         logger.info(f"Successfully generated embedding for text: {request.text[:20]}...")
#         return EmbeddingResponse(embedding=vector)

#     except Exception as e:
#         logger.error(f"Error during embedding generation: {str(e)}")
#         raise HTTPException(
#             status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
#             detail=f"임베딩 생성 중 서버 내부 오류가 발생했습니다: {str(e)}"
#         )

import os
import json
import numpy as np
import aioboto3
from fastapi import APIRouter, HTTPException
from typing import List
from urllib.parse import urlparse
from pydantic import BaseModel
from huggingface_hub import AsyncInferenceClient
from dotenv import load_dotenv

# .env 로드 및 설정
load_dotenv()
router = APIRouter()
TOKEN_LIMIT = 3000 # 청크 분절 기준

HF_TOKEN = os.getenv("HF_TOKEN")
MODEL_ID = os.getenv("MODEL_ID")
client = AsyncInferenceClient(model=MODEL_ID, token=HF_TOKEN)

class S3EmbeddingRequest(BaseModel):
    s3_url: str

class EmbeddingResponse(BaseModel):
    embedding: List[float]

@router.post("/embed-from-s3", response_model=EmbeddingResponse)
async def get_embedding(request: S3EmbeddingRequest):
    try:
        # 1. S3 URL 파싱
        parsed_url = urlparse(request.s3_url)
        bucket_name = parsed_url.netloc.split('.')[0]
        key = parsed_url.path.lstrip('/')

        # 2. aioboto3를 이용한 비동기 파일 다운로드
        session = aioboto3.Session()
        async with session.client('s3') as s3:
            # S3 오브젝트 메타데이터 가져오기
            response = await s3.get_object(Bucket=bucket_name, Key=key)
            
            # 스트림 데이터 비동기 읽기
            async with response['Body'] as stream:
                file_content = await stream.read()
                book_data = json.loads(file_content.decode('utf-8'))

        # 3. 텍스트 추출 및 청크 분절
        texts = [node['text'] for node in book_data.get('content', []) if 'text' in node]
        # chunks: List[str] = []
        embedding_list = [] # 임베딩된 벡터값들만 담을 리스트
        current_chunk = ""
        
        for text in texts:
            if len(current_chunk) + len(text) > TOKEN_LIMIT:
                if current_chunk.strip():
                    # [즉시 임베딩] 지금까지 모인 청크를 임베딩하여 벡터 저장
                    vector = await client.feature_extraction(current_chunk.strip())
                    embedding_list.append(vector)
                    current_chunk = ""

                # 1-2. [중요] 새로 들어온 text 자체가 limit보다 크다면? 
                # 이 text를 limit 단위로 쪼개서 즉시 임베딩 리스트에 넣음
                if len(text) > TOKEN_LIMIT:
                    sub_chunks = [text[i : i + TOKEN_LIMIT] for i in range(0, len(text), TOKEN_LIMIT)]
                    # 마지막 조각은 다음 text와 합치기 위해 남겨두고 나머지는 즉시 임베딩
                    for sub in sub_chunks[:-1]:
                        vector = await client.feature_extraction(sub.strip())
                        embedding_list.append(vector)
                    current_chunk = sub_chunks[-1] # 마지막 조각만 유지
                else:
                    current_chunk = text
            else:
                current_chunk += " " + text
        
        if current_chunk.strip():
            vector = await client.feature_extraction(current_chunk.strip())
            embedding_list.append(vector)
            # chunks.append(current_chunk.strip())

        # if not chunks:
        if not embedding_list:
            raise HTTPException(status_code=400, detail="임베딩할 텍스트 내용이 없습니다.")

        # 4. 허깅페이스 API 호출 (비동기 배치 처리)
        # vectors = await client.feature_extraction(chunks) # type: ignore
        
        # 산술 평균 계산: $$V_{integrated} = \frac{1}{n} \sum_{i=1}^{n} V_i$$
        # integrated_vector = np.mean(vectors, axis=0)
        integrated_vector = np.mean(embedding_list, axis=0)

        print(f"총 임베딩된 청크 수: {len(embedding_list)}")
        
        # 5. 안전한 타입 변환 후 반환
        result_list = integrated_vector.tolist() if hasattr(integrated_vector, "tolist") else integrated_vector
        return {"embedding": result_list}

    except Exception as e:
        print(f"❌ S3 비동기 임베딩 처리 실패: {e}")
        raise HTTPException(status_code=500, detail=str(e))

import re
import requests

class DriveEmbeddingRequest(BaseModel):
    google_drive_url: str

@router.post("/embed-from-drive")
async def get_embedding_from_drive(request: DriveEmbeddingRequest):
    try:
        # 1. 구글 드라이브 링크에서 파일 ID 추출
        # 링크 형식: https://drive.google.com/file/d/FILE_ID/view?usp=sharing
        file_id_match = re.search(r'/d/([^/]+)', request.google_drive_url)
        if not file_id_match:
            raise HTTPException(status_code=400, detail="유효하지 않은 구글 드라이브 링크입니다.")
        
        file_id = file_id_match.group(1)
        
        # 2. 직속 다운로드 URL 생성
        download_url = f'https://drive.google.com/uc?export=download&id={file_id}'
        
        # 3. 파일 다운로드
        response = requests.get(download_url)
        if response.status_code != 200:
            raise HTTPException(status_code=500, detail="구글 드라이브 파일을 가져오지 못했습니다. 공유 설정을 확인하세요.")
            
        book_data = response.json()

        # 4. 텍스트 추출 및 청크 분절
        texts = [node['text'] for node in book_data.get('content', []) if 'text' in node]
        # chunks: List[str] = []
        embedding_list = [] # 임베딩된 벡터값들만 담을 리스트
        current_chunk = ""
        
        for text in texts:
            if len(current_chunk) + len(text) > TOKEN_LIMIT:
                if current_chunk.strip():
                    # [즉시 임베딩] 지금까지 모인 청크를 임베딩하여 벡터 저장
                    vector = await client.feature_extraction(current_chunk.strip())
                    embedding_list.append(vector)
                    current_chunk = ""

                # 1-2. [중요] 새로 들어온 text 자체가 limit보다 크다면? 
                # 이 text를 limit 단위로 쪼개서 즉시 임베딩 리스트에 넣음
                if len(text) > TOKEN_LIMIT:
                    sub_chunks = [text[i : i + TOKEN_LIMIT] for i in range(0, len(text), TOKEN_LIMIT)]
                    # 마지막 조각은 다음 text와 합치기 위해 남겨두고 나머지는 즉시 임베딩
                    for sub in sub_chunks[:-1]:
                        vector = await client.feature_extraction(sub.strip())
                        embedding_list.append(vector)
                    current_chunk = sub_chunks[-1] # 마지막 조각만 유지
                else:
                    current_chunk = text

            else:
                current_chunk += " " + text
        
        if current_chunk.strip():
            vector = await client.feature_extraction(current_chunk.strip())
            embedding_list.append(vector)
            # chunks.append(current_chunk.strip())

        # if not chunks:
        if not embedding_list:
            raise HTTPException(status_code=400, detail="임베딩할 텍스트 내용이 없습니다.")

        integrated_vector = np.mean(embedding_list, axis=0)

        print(f"총 임베딩된 청크 수: {len(embedding_list)}")
        
        return {"embedding": integrated_vector.tolist()}

    except Exception as e:
        raise HTTPException(status_code=500, detail=f"처리 중 오류 발생: {str(e)}")
    

@router.post("/text-from-drive")
async def get_text_from_drive(request: DriveEmbeddingRequest) -> List[str]:
    # 1. 구글 드라이브 공유 링크 -> 직속 다운로드 링크로 변환
    # 링크 예시: https://drive.google.com/file/d/1A2B3C.../view?usp=sharing
    file_id_match = re.search(r'/d/([^/]+)', request.google_drive_url)
    if not file_id_match:
        raise HTTPException(status_code=400, detail="유효하지 않은 구글 드라이브 링크입니다.")
    
    file_id = file_id_match.group(1)
    
    # 2. 직속 다운로드 URL 생성
    download_url = f'https://drive.google.com/uc?export=download&id={file_id}'
    
    # 3. 파일 다운로드
    response = requests.get(download_url)
    if response.status_code != 200:
        raise HTTPException(status_code=500, detail="구글 드라이브 파일을 가져오지 못했습니다. 공유 설정을 확인하세요.")
        
    book_data = response.json()


    # 3. 데이터 구조에서 'text' 필드만 추출 (정우님의 JSON 규격 기준)
    # book_data['content'] 리스트를 돌며 'text' 키가 있는 것만 수집합니다.
    texts = [
        node['text'] 
        for node in book_data.get('content', []) 
        if 'text' in node
    ]

    return texts