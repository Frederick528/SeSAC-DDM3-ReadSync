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
from fastapi import APIRouter, HTTPException, status
from pydantic import BaseModel, Field
from huggingface_hub import AsyncInferenceClient # 비동기 클라이언트 사용

router = APIRouter()

# 1. 클라이언트 설정 (API 키는 환경변수에서 로드)
# 토큰은 https://huggingface.co/settings/tokens 에서 발행 가능합니다.
from dotenv import load_dotenv

# .env 파일 로드
load_dotenv()

HF_TOKEN = os.getenv("HF_TOKEN")
MODEL_ID = os.getenv("MODEL_ID")

client = AsyncInferenceClient(model=MODEL_ID, token=HF_TOKEN)

class EmbeddingRequest(BaseModel):
    text: str = Field(..., min_length=1)

class EmbeddingResponse(BaseModel):
    embedding: list[float]

@router.post("/embed", response_model=EmbeddingResponse)
async def get_embedding(request: EmbeddingRequest):
    try:
        # 2. feature_extraction 메서드 사용
        # KURE-v1 같은 임베딩 모델은 이 메서드가 딱 맞습니다.
        vector = await client.feature_extraction(request.text)
        
        # 결과가 numpy array 형태일 수 있으므로 list로 변환
        return {"embedding": vector.tolist() if hasattr(vector, "tolist") else vector}

    except Exception as e:
        # 모델 로딩 중(503) 등 에러 발생 시 처리
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Hugging Face API 호출 중 오류 발생: {str(e)}"
        )