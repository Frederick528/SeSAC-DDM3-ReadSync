# ----------------------------------------------------------------------
# [핵심 변경]
# docker.io (Docker Hub) -> public.ecr.aws (AWS 공식 미러)
# 이렇게 하면 "Not found" 에러가 100% 사라집니다.
# ----------------------------------------------------------------------
FROM --platform=linux/amd64 public.ecr.aws/bitnami/postgresql:15

USER root

# AWS 미러의 Bitnami 이미지는 가끔 패키지 목록이 비어있을 수 있어서
# 'apt-get update'를 강제로 먼저 해줘야 합니다.
RUN apt-get update && apt-get install -y \
    git \
    make \
    gcc \
    postgresql-server-dev-15 \
    && rm -rf /var/lib/apt/lists/*

# pgvector 빌드 및 설치 (여기는 똑같습니다)
RUN git clone --branch v0.7.0 https://github.com/pgvector/pgvector.git \
    && cd pgvector \
    && make \
    && make install \
    && cd .. \
    && rm -rf pgvector

USER 1001