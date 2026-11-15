import os
from pathlib import Path
from dotenv import load_dotenv

load_dotenv()
BASE_DIR = Path(__file__).resolve().parent


class Config:
    SECRET_KEY = os.getenv("SECRET_KEY")
    SQLALCHEMY_DATABASE_URI = os.getenv(
        "DATABASE_URL",
        "mysql+pymysql://root:password@127.0.0.1:3306/suraksha?charset=utf8mb4",
    )
    SQLALCHEMY_TRACK_MODIFICATIONS = False
    SQLALCHEMY_ENGINE_OPTIONS = {"pool_pre_ping": True, "pool_recycle": 280}

    SESSION_COOKIE_HTTPONLY = True
    SESSION_COOKIE_SAMESITE = "Lax"
    SESSION_COOKIE_SECURE = os.getenv("SESSION_COOKIE_SECURE", "false").lower() == "true"
    PERMANENT_SESSION_LIFETIME = 60 * 60 * 8

    # Enable only after HTTPS is configured on the deployment.
    PREFERRED_URL_SCHEME = "https" if SESSION_COOKIE_SECURE else "http"

    @classmethod
    def validate(cls):
        if not cls.SECRET_KEY or cls.SECRET_KEY == "CHANGE-ME-before-deployment":
            raise RuntimeError(
                "SECRET_KEY is missing. Add a long random SECRET_KEY to the project-root .env file."
            )
