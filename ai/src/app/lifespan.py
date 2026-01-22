# 로컬로 모델 저장
# from contextlib import asynccontextmanager
# from fastapi import FastAPI
# from sentence_transformers import SentenceTransformer
# import torch

# # 모델을 담아둘 공간
# ml_models = {}

# @asynccontextmanager
# async def lifespan(app: FastAPI):
#     # 1. [Startup] 모델 로딩
#     model_id = "nlpai-lab/KURE-v1"
#     print(f"🚀 {model_id} 임베딩 모델 로딩 시작...")

#     device = "cuda" if torch.cuda.is_available() else "cpu"
    
#     # SentenceTransformer를 사용하면 매우 쉽게 로드 가능합니다.
#     # KURE-v1은 Hugging Face에서 자동으로 다운로드됩니다.
#     ml_models["embedding_model"] = SentenceTransformer(model_id, device=device)
    
#     print(f"✅ 모델 로드 완료! (실행 장치: {device})")
    
#     yield
    
#     # 2. [Shutdown] 메모리 정리
#     ml_models.clear()
#     if torch.cuda.is_available():
#         torch.cuda.empty_cache()
#     print("🧹 서버 종료: 모델 메모리 해제 완료")


# API로 호출
# src/app/lifespan.py
from contextlib import asynccontextmanager
from fastapi import FastAPI
import logging

logger = logging.getLogger(__name__)

@asynccontextmanager
async def lifespan(app: FastAPI):
    # 서버가 켜질 때 실행되는 부분
    logger.info("🚀 API 서버가 시작되었습니다. (Hugging Face Inference API 모드)")
    
    yield
    
    # 서버가 꺼질 때 실행되는 부분
    logger.info("🛑 API 서버가 종료됩니다.")