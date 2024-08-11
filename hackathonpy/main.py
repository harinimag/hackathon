from fastapi import FastAPI, HTTPException, Depends, status
from pydantic import BaseModel
import models
from database import engine, SessionLocal
from sqlalchemy.orm import Session
from typing import Annotated
from typing import List
from typing import Dict

app = FastAPI()
models.Base.metadata.create_all(bind=engine)

class UserBase(BaseModel):
    username: str
    password: str
    email: str
    year: str

class MeetingBase(BaseModel):
    details: str
    year: str

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally: 
        db.close()

@app.post("/post_users", status_code=status.HTTP_201_CREATED)
async def create_user(user: UserBase, db: Session = Depends(get_db)):
    db_user = models.User(**user.model_dump())
    db.add(db_user)
    db.commit()


@app.get("/users/{username}", status_code=status.HTTP_200_OK)
async def read_user(username: str, db: Session = Depends(get_db)):
    user = db.query(models.User).filter(models.User.username == username).first()
    if user is None:
        raise HTTPException(status_code=404, detail="User not found")
    return {"username": user.username,"password": user.password, "email":user.email, "year":user.year}

@app.post("/post_meetings", status_code=status.HTTP_201_CREATED)
async def create_meeting(meeting: MeetingBase, db: Session = Depends(get_db)):
    db_meeting = models.Meeting(**meeting.model_dump())
    db.add(db_meeting)
    db.commit()


@app.get("/meetings/{year}", status_code=status.HTTP_200_OK)
async def read_meeting(year: str, db: Session = Depends(get_db)):
    meetings = db.query(models.Meeting).filter(models.Meeting.year == year).all()
    if meetings is None:
        raise HTTPException(status_code=404, detail="not found")
    meetingdetails = [meeting.details for meeting in meetings]
    return meetingdetails