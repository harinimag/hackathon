from sqlalchemy import Boolean, Column, Integer, String
from database import Base
from sqlalchemy import create_engine, Column, Integer, String, ForeignKey
from sqlalchemy.orm import relationship, sessionmaker
from sqlalchemy.ext.declarative import declarative_base

Base = declarative_base()

class User(Base):
    __tablename__ = 'users'
    id = Column(Integer,primary_key = True, index = True)
    username = Column(String(50),unique = True)
    password = Column(String(50))
    email = Column(String(50))
    year = Column(String(2))

class Meeting(Base):
    __tablename__ = 'meetings'
    id = Column(Integer,primary_key = True, index = True)
    details = Column(String(50))
    year = Column(String(2))