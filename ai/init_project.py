import os

def create_final_singular_structure():
    # 1. 단수형 원칙이 적용된 폴더 목록
    folders = [
        "src/app/api/v1/endpoint",
        "src/app/core",
        "src/app/domain/aichat",
        "src/app/domain/book",
        "src/app/domain/audio",
        "src/app/infrastructure/database",
        "src/app/global/exception",
        "src/app/global/util",
        "script",
        "test"
    ]

    # 2. 구조에 맞는 기본 보일러플레이트 파일
    files = {
        ".env": "HF_TOKEN=your_token_here\nMODEL_ID=gpt2",
        "README.md": "# SeSAC AI Backend\n\nJava 백엔드와 구조를 맞춘 단수형 기반 AI 서비스입니다.",
        "Dockerfile": "FROM python:3.10-slim\nWORKDIR /app\nCOPY . .",
        
        # 시스템 진입점
        "src/app/main.py": "from fastapi import FastAPI\nfrom app.lifespan import lifespan\n\napp = FastAPI(title='SeSAC AI Service', lifespan=lifespan)",
        "src/app/lifespan.py": "from contextlib import asynccontextmanager\nfrom fastapi import FastAPI\n\n@asynccontextmanager\nasync def lifespan(app: FastAPI):\n    # TODO: 서버 시작 시 모델 로드 로직 작성\n    yield\n    # TODO: 서버 종료 시 리소스 정리",
        
        # 설정 및 의존성
        "src/app/core/config.py": "from pydantic_settings import BaseSettings\n\nclass Settings(BaseSettings):\n    PROJECT_NAME: str = 'SeSAC AI Service'\n\nsettings = Settings()",
        "src/app/api/v1/api.py": "from fastapi import APIRouter\napi_router = APIRouter()",
        "src/app/api/dep.py": "# Dependency Injection Utility",
        
        # 도메인 샘플 (book 도메인)
        "src/app/domain/book/service.py": "class BookService:\n    def __init__(self):\n        pass",
        "src/app/domain/book/schema.py": "from pydantic import BaseModel\n\nclass BookRecommendationRequest(BaseModel):\n    title: str"
    }

    print("🏗️  단수형(Singular) 구조 프로젝트 생성을 시작합니다...")

    # 폴더 생성 및 .gitkeep 추가
    for folder in folders:
        os.makedirs(folder, exist_ok=True)
        with open(os.path.join(folder, ".gitkeep"), "w") as f:
            pass
        print(f"✅ 폴더 생성 완료: {folder}")

    # 파일 생성
    for path, content in files.items():
        if not os.path.exists(path):
            with open(path, "w", encoding="utf-8") as f:
                f.write(content)
            print(f"📝 파일 생성 완료: {path}")

    print("\n🎉 모든 구조가 성공적으로 재구성되었습니다!")

if __name__ == "__main__":
    create_final_singular_structure()