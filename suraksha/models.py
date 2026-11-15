"""Reflected SQLAlchemy ORM mappings for the existing Suraksha MySQL schema.

The schema is deliberately reflected instead of recreated: src/database/schema.sql
is the source of truth and may already contain production/pilot data.
"""
from sqlalchemy.ext.automap import automap_base
from .extensions import db

Base = automap_base(metadata=db.metadata)
_mapped = False

def prepare_models():
    global _mapped
    if not _mapped:
        Base.prepare(autoload_with=db.engine)
        _mapped = True
    return Base.classes
