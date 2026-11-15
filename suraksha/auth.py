from functools import wraps

import bcrypt
from flask import abort, flash, g, redirect, request, session, url_for
from sqlalchemy import select

from .db_helpers import table
from .extensions import db


ROLE_LEVEL = {
    "STUDENT": 1,
    "TEACHER": 2,
    "INSTITUTION_ADMIN": 3,
    "SYSTEM_ADMIN": 4,
}


def load_current_user():
    """Load an active user for this request and discard stale sessions."""
    uid = session.get("user_id")
    g.user = None
    if uid is None:
        return

    try:
        users = table("app_user")
        user = db.session.execute(
            select(users).where(users.c.id == uid, users.c.active.is_(True))
        ).mappings().first()
        if user is None:
            session.clear()
            return
        g.user = user
    except Exception:
        # Do not let a broken/stale session become an authenticated request.
        session.clear()
        raise


def login_required(fn):
    @wraps(fn)
    def wrapped(*args, **kwargs):
        if getattr(g, "user", None) is None:
            session.clear()
            flash("Please sign in to continue.", "error")
            return redirect(url_for("login", next=request.full_path.rstrip("?")))
        return fn(*args, **kwargs)
    return wrapped


def roles_required(*roles):
    allowed_roles = frozenset(roles)

    def deco(fn):
        @wraps(fn)
        @login_required
        def wrapped(*args, **kwargs):
            user = getattr(g, "user", None)
            if user is None or user["role"] not in allowed_roles:
                abort(403)
            return fn(*args, **kwargs)
        return wrapped
    return deco


def verify_password(plain, stored):
    if not isinstance(plain, str) or not isinstance(stored, str) or not stored:
        return False
    try:
        return bcrypt.checkpw(plain.encode("utf-8"), stored.encode("utf-8"))
    except (ValueError, TypeError):
        return False
