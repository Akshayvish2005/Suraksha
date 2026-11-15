from sqlalchemy import select, insert, update, delete, text
from .extensions import db

def table(name):
    if name not in db.metadata.tables:
        raise RuntimeError(f"Database table '{name}' is not available. Run src/database/schema.sql first.")
    return db.metadata.tables[name]

def rows(name, where=None, order=None, limit=None):
    t = table(name)
    stmt = select(t)
    if where is not None: stmt = stmt.where(where)
    if order is not None: stmt = stmt.order_by(order)
    if limit is not None: stmt = stmt.limit(limit)
    return db.session.execute(stmt).mappings().all()

def one(name, where):
    t = table(name)
    return db.session.execute(select(t).where(where)).mappings().first()

def add(name, **values):
    t = table(name)
    result = db.session.execute(insert(t).values(**values))
    return result.inserted_primary_key[0] if result.inserted_primary_key else None

def change(name, where, **values):
    return db.session.execute(update(table(name)).where(where).values(**values)).rowcount

def remove(name, where):
    return db.session.execute(delete(table(name)).where(where)).rowcount

def col(name, column):
    return table(name).c[column]
