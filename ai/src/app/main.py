from fastapi import FastAPI
from app.api.v1.endpoint.embedding import router as embedding_router

# 로컬에서 가져오는 경우
# from app.lifespan import lifespan, ml_models

# API 호출
from app.lifespan import lifespan

app = FastAPI(
    title="SeSAC AI Book Recommendation API",
    lifespan=lifespan
)

@app.get("/")
async def root():
    return {"message": "AI 서버가 정상 작동 중입니다."}

# 로컬일 때, 모델이 있는지 확인하는 API
# @app.get("/check-model")
# async def check_model():
#     # lifespan에서 로드한 모델이 잘 있는지 확인하는 엔드포인트
#     model_status = "Loaded" if "book_model" in ml_models else "Not Loaded"
#     return {"model_status": model_status}

app.include_router(embedding_router, prefix="/api/v1", tags=["Embedding"])