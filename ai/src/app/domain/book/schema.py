from pydantic import BaseModel

class BookRecommendationRequest(BaseModel):
    title: str