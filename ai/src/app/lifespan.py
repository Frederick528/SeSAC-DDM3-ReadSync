from contextlib import asynccontextmanager
from fastapi import FastAPI

@asynccontextmanager
async def lifespan(app: FastAPI):
    # TODO: 서버 시작 시 모델 로드 로직 작성
    yield
    # TODO: 서버 종료 시 리소스 정리