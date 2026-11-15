import secrets
from flask import Flask, g, render_template, request, session, abort
import bleach
from sqlalchemy import inspect
from config import Config
from .extensions import db, migrate
from .auth import load_current_user

def create_app(config_class=Config):
    app = Flask(
        __name__,
        instance_relative_config=True,
        template_folder="../templates",
        static_folder="../static"
    )
    app.config.from_object(config_class)
    if hasattr(config_class, "validate"):
        config_class.validate()
    db.init_app(app)
    migrate.init_app(app, db)

    @app.context_processor
    def inject_csrf_token():
        if "_csrf_token" not in session:
            session["_csrf_token"] = secrets.token_urlsafe(32)
        return {"csrf_token": session["_csrf_token"]}

    @app.before_request
    def protect_state_changing_requests():
        if request.method not in {"POST", "PUT", "PATCH", "DELETE"}:
            return
        supplied = request.form.get("_csrf_token") or request.headers.get("X-CSRFToken")
        expected = session.get("_csrf_token")
        if not expected or not supplied or not secrets.compare_digest(expected, supplied):
            abort(400, description="Invalid or missing CSRF token.")

    @app.before_request
    def before_request():
        # Existing schema is authoritative; reflect it rather than silently changing it.
        if not db.metadata.tables:
            try:
                db.metadata.reflect(bind=db.engine)
                from .models import prepare_models
                prepare_models()
            except Exception:
                pass
        try:
            load_current_user()
        except Exception:
            g.user = None

    app.jinja_env.filters["sanitize_html"] = lambda value: bleach.clean(
        value or "",
        tags={"p","br","strong","b","em","i","u","ul","ol","li","h2","h3","h4","blockquote","a"},
        attributes={"a": ["href", "title"]},
        protocols={"http","https","mailto"},
        strip=True
    )
    from .routes import register_routes
    register_routes(app)

    @app.errorhandler(403)
    def forbidden(_):
        return render_template("error.html", code=403, message="You do not have permission to view this page."), 403

    @app.errorhandler(404)
    def not_found(_):
        return render_template("error.html", code=404, message="The requested page could not be found."), 404

    @app.errorhandler(500)
    def server_error(_):
        db.session.rollback()
        return render_template("error.html", code=500, message="An unexpected error occurred. Please try again."), 500

    return app
