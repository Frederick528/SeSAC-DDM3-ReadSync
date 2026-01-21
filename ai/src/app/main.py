from fastapi import FastAPI
from app.lifespan import lifespan

app = FastAPI(title='SeSAC AI Service', lifespan=lifespan)