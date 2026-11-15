from functools import wraps
from flask import session, redirect, url_for, flash, request, abort, g
from sqlalchemy import select
from .extensions import db
from .db_helpers import table
import bcrypt

ROLE_LEVEL = {"STUDENT": 1, "TEACHER": 2, "INSTITUTION_ADMIN": 3, "SYSTEM_ADMIN": 4}

def load_current_user():
    uid = session.get("user_id")
    g.user = None
    if uid:
        t = table("app_user")
        g.user = db.session.execute(select(t).where(t.c.id == uid, t.c.active == True)).mappings().first()

def login_required(fn):
    @wraps(fn)
    def wrapped(*args, **kwargs):
        if not session.get("user_id"):
            flash("Please sign in to continue.", "error")
            return redirect(url_for("login", next=request.path))
        return fn(*args, **kwargs)
    return wrapped

def roles_required(*roles):
    def deco(fn):
        @wraps(fn)
        @login_required
        def wrapped(*args, **kwargs):
            user = getattr(g, "user", None)
            if not user or user["role"] not in roles:
                abort(403)
            return fn(*args, **kwargs)
        return wrapped
    return deco

def verify_password(plain, stored):
    if not stored: return False
    try:
        return bcrypt.checkpw(plain.encode("utf-8"), stored.encode("utf-8"))
    except (ValueError, TypeError):
        return False
