import json, csv, io
from urllib.parse import urlparse
from datetime import datetime, date
from flask import render_template, request, redirect, url_for, session, flash, jsonify, abort, Response, g
from sqlalchemy import select, and_, func
from .extensions import db
from .db_helpers import table, rows, one, add, change
from .auth import login_required, roles_required, verify_password

def register_routes(app):
    @app.get("/")
    def index():
        return redirect(url_for("dashboard") if session.get("user_id") else url_for("login"))

    @app.route("/login", methods=["GET", "POST"])
    def login():
        if request.method == "POST":
            username = request.form.get("username", "").strip()
            password = request.form.get("password", "")
            t = table("app_user")
            user = db.session.execute(select(t).where(t.c.username == username, t.c.active == True)).mappings().first()
            if user and verify_password(password, user["password_hash"]):
                session.clear()
                session["user_id"] = user["id"]
                change("app_user", t.c.id == user["id"], last_login_at=datetime.now())
                db.session.commit()
                next_url = request.args.get("next", "")
                parsed_next = urlparse(next_url)
                if parsed_next.scheme or parsed_next.netloc or not next_url.startswith("/"):
                    next_url = url_for("dashboard")
                return redirect(next_url or url_for("dashboard"))
            flash("Invalid username or password.", "error")
        return render_template("login.html")

    @app.post("/logout")
    @login_required
    def logout():
        session.clear()
        return redirect(url_for("login"))

    @app.get("/dashboard")
    @login_required
    def dashboard():
        uid = session["user_id"]
        modules = db.session.execute(
            select(table("learning_module"), table("hazard").c.name_en.label("hazard_name"),
                   table("module_text").c.title, table("module_text").c.description)
            .join(table("hazard"), table("learning_module").c.hazard_id == table("hazard").c.id)
            .outerjoin(table("module_text"), and_(table("module_text").c.module_id == table("learning_module").c.id,
                                                   table("module_text").c.language == (g.user["language"] or "en")))
            .where(table("learning_module").c.status == "PUBLISHED")
            .order_by(table("learning_module").c.sort_order)
        ).mappings().all()
        progress = rows("module_progress", table("module_progress").c.user_id == uid)
        completed = sum(1 for p in progress if p["status"] == "COMPLETED")
        return render_template("dashboard.html", modules=modules, completed=completed, total=len(modules))

    @app.get("/modules")
    @login_required
    def modules():
        return redirect(url_for("dashboard"))

    @app.get("/modules/<int:module_id>")
    @login_required
    def module_detail(module_id):
        m = one("learning_module", table("learning_module").c.id == module_id)
        if not m: abort(404)
        tx = one("module_text", and_(table("module_text").c.module_id == module_id,
                                     table("module_text").c.language == (g.user["language"] or "en")))
        lessons = db.session.execute(
            select(table("lesson"), table("lesson_text").c.title)
            .join(table("lesson_text"), table("lesson_text").c.lesson_id == table("lesson").c.id)
            .where(table("lesson").c.module_id == module_id,
                   table("lesson_text").c.language == (g.user["language"] or "en"),
                   table("lesson").c.status == "PUBLISHED")
            .order_by(table("lesson").c.sort_order)
        ).mappings().all()
        return render_template("module.html", module=m, module_text=tx, lessons=lessons)

    @app.get("/lessons/<int:lesson_id>")
    @login_required
    def lesson_detail(lesson_id):
        lesson = one("lesson", table("lesson").c.id == lesson_id)
        if not lesson: abort(404)
        tx = one("lesson_text", and_(table("lesson_text").c.lesson_id == lesson_id,
                                     table("lesson_text").c.language == (g.user["language"] or "en")))
        return render_template("lesson.html", lesson=lesson, lesson_text=tx)

    @app.post("/lessons/<int:lesson_id>/complete")
    @login_required
    def complete_lesson(lesson_id):
        lp = table("lesson_progress")
        mp = table("module_progress")
        lt = table("lesson")

        lesson = one("lesson", lt.c.id == lesson_id)
        if not lesson:
            abort(404)

        uid = session["user_id"]
        module_id = lesson["module_id"]

        # Mark current lesson as completed
        existing_lesson = one(
            "lesson_progress",
            and_(lp.c.user_id == uid, lp.c.lesson_id == lesson_id)
        )

        if not existing_lesson:
            add(
                "lesson_progress",
                user_id=uid,
                lesson_id=lesson_id,
                completed_at=datetime.now()
            )

        # Get all published lessons in this module
        module_lessons = rows(
            "lesson",
            and_(
                lt.c.module_id == module_id,
                lt.c.status == "PUBLISHED"
            )
        )

        # Get completed lessons for this user
        completed_lessons = rows(
            "lesson_progress",
            lp.c.user_id == uid
        )

        completed_ids = {x["lesson_id"] for x in completed_lessons}
        total_lessons = len(module_lessons)

        all_completed = (
                total_lessons > 0
                and all(x["id"] in completed_ids for x in module_lessons)
        )

        # Update module progress
        existing_module = one(
            "module_progress",
            and_(mp.c.user_id == uid, mp.c.module_id == module_id)
        )

        new_status = "COMPLETED" if all_completed else "IN_PROGRESS"

        if existing_module:
            change(
                "module_progress",
                and_(mp.c.user_id == uid, mp.c.module_id == module_id),
                status=new_status,
                started_at=existing_module["started_at"] or datetime.now()
            )
        else:
            add(
                "module_progress",
                user_id=uid,
                module_id=module_id,
                status=new_status,
                started_at=datetime.now()
            )

        db.session.commit()

        if all_completed:
            flash("Congratulations! Module completed.", "success")
        else:
            flash("Lesson marked as complete.", "success")

        return redirect(url_for("module_detail", module_id=module_id))

    @app.get("/assessments")
    @login_required
    def assessments():
        items = db.session.execute(select(table("learning_module"), table("module_text").c.title)
            .join(table("module_text"), table("module_text").c.module_id == table("learning_module").c.id)
            .where(table("learning_module").c.status == "PUBLISHED",
                   table("module_text").c.language == (g.user["language"] or "en"))).mappings().all()
        return render_template("assessments.html", items=items)

    @app.route("/modules/<int:module_id>/assessment/<attempt_type>", methods=["GET", "POST"])
    @login_required
    def assessment(module_id, attempt_type):
        attempt_type = attempt_type.upper()
        if attempt_type not in ("PRE", "POST"): abort(404)
        uid = session["user_id"]
        if request.method == "POST":
            try:
                aid = int(request.form.get("attempt_id", ""))
            except (TypeError, ValueError):
                abort(400)
            at = table("assessment_attempt")
            attempt = one("assessment_attempt", and_(
                at.c.id == aid, at.c.user_id == uid, at.c.module_id == module_id,
                at.c.attempt_type == attempt_type
            ))
            if not attempt or attempt["status"] != "IN_PROGRESS":
                abort(404)

            q = table("question")
            ao = table("answer_option")
            raw_qids = request.form.getlist("question_id")
            try:
                qids = [int(value) for value in raw_qids]
            except (TypeError, ValueError):
                abort(400)
            if not qids or len(qids) != len(set(qids)):
                abort(400)

            valid_questions = rows("question", and_(
                q.c.id.in_(qids), q.c.module_id == module_id,
                q.c.status == "PUBLISHED", q.c.purpose.in_([attempt_type, "BOTH"])
            ))
            if len(valid_questions) != len(qids):
                abort(400)

            correct_count = 0
            for qid in qids:
                selected_raw = request.form.getlist(f"answer_{qid}")
                try:
                    selected_ids = {int(value) for value in selected_raw}
                except (TypeError, ValueError):
                    abort(400)
                if not selected_ids:
                    abort(400)

                valid_options = rows("answer_option", and_(
                    ao.c.question_id == qid, ao.c.id.in_(selected_ids)
                ))
                if len(valid_options) != len(selected_ids):
                    abort(400)

                correct_options = rows("answer_option", and_(
                    ao.c.question_id == qid, ao.c.is_correct == True
                ))
                correct_set = {int(option["id"]) for option in correct_options}
                is_correct = selected_ids == correct_set
                correct_count += int(is_correct)
                answer_id = add(
                    "attempt_answer", attempt_id=aid,
                    question_id=qid, is_correct=is_correct
                )
                for option_id in selected_ids:
                    add("attempt_answer_option", attempt_answer_id=answer_id, option_id=option_id)

            total = len(qids)
            pct = round(100 * correct_count / total, 2)
            change("assessment_attempt", at.c.id == aid,
                   status="SUBMITTED", total_questions=total, correct_count=correct_count,
                   score_percent=pct, submitted_at=datetime.now())
            db.session.commit()
            return render_template(
                "assessment_result.html", score=pct,
                correct=correct_count, total=total
            )
        aid = add("assessment_attempt", user_id=uid, module_id=module_id, attempt_type=attempt_type,
                  status="IN_PROGRESS", started_at=datetime.now())
        db.session.commit()
        q = table("question")
        qt = table("question_text")
        opt, ot = table("answer_option"), table("answer_option_text")
        questions = db.session.execute(select(q.c.id, qt.c.question_text, qt.c.explanation,
                opt.c.id.label("option_id"), ot.c.option_text)
            .join(qt, and_(qt.c.question_id == q.c.id, qt.c.language == (g.user["language"] or "en")))
            .join(opt, opt.c.question_id == q.c.id)
            .join(ot, and_(ot.c.option_id == opt.c.id, ot.c.language == (g.user["language"] or "en")))
            .where(q.c.module_id == module_id, q.c.status == "PUBLISHED",
                   q.c.purpose.in_([attempt_type, "BOTH"]))
            .order_by(q.c.sort_order, opt.c.sort_order)).mappings().all()
        grouped = {}
        for item in questions:
            grouped.setdefault(item["id"], {"id": item["id"], "text": item["question_text"], "options": []})
            grouped[item["id"]]["options"].append({"id": item["option_id"], "text": item["option_text"]})
        return render_template("assessment.html", attempt_id=aid, questions=list(grouped.values()),
                               module_id=module_id, attempt_type=attempt_type)

    @app.get("/simulations")
    @login_required
    def simulations():
        items = db.session.execute(select(table("scenario"), table("hazard").c.name_en.label("hazard_name"))
            .join(table("hazard"), table("hazard").c.id == table("scenario").c.hazard_id)
            .where(table("scenario").c.status == "PUBLISHED")).mappings().all()
        return render_template("simulations.html", items=items)

    @app.route("/scenarios/<int:scenario_id>", methods=["GET", "POST"])
    @login_required
    def scenario(scenario_id):
        sc = one("scenario", table("scenario").c.id == scenario_id)
        if not sc: abort(404)
        definition = json.loads(sc["definition"] or "{}")
        steps = definition.get("steps", [])
        if request.method == "POST":
            attempt_id = int(request.form["attempt_id"])
            step_id = request.form.get("step_id")
            option_id = request.form.get("option_id")
            attempt = one("scenario_attempt", and_(
                table("scenario_attempt").c.id == attempt_id,
                table("scenario_attempt").c.user_id == session["user_id"],
                table("scenario_attempt").c.scenario_id == scenario_id
            ))
            if not attempt or attempt["completed_at"] is not None:
                abort(404)

            decisions_table = table("scenario_decision")
            previous_decisions = rows(
                "scenario_decision",
                decisions_table.c.attempt_id == attempt_id
            )
            if any(str(item["step_id"]) == str(step_id) for item in previous_decisions):
                abort(400)

            # Only allow the first step initially, then the exact step reached
            # through the previously selected option's nextStepId.
            expected_step = steps[0] if not previous_decisions else None
            if previous_decisions:
                previous_step_id = str(previous_decisions[-1]["step_id"])
                previous_step = next(
                    (item for item in steps if str(item.get("id")) == previous_step_id), None
                )
                previous_option = next(
                    (item for item in (previous_step or {}).get("options", [])
                     if str(item.get("id")) == str(previous_decisions[-1]["option_id"])), None
                )
                next_step_id = (previous_option or {}).get("nextStepId")
                expected_step = next(
                    (item for item in steps if str(item.get("id")) == str(next_step_id)), None
                )
            if not expected_step or str(expected_step.get("id")) != str(step_id):
                abort(400)

            step = expected_step
            option = next(
                (item for item in step.get("options", [])
                 if str(item.get("id")) == str(option_id)), None
            )
            if not option:
                abort(400)

            try:
                points = int(option.get("points", 0))
            except (TypeError, ValueError):
                abort(400)

            add("scenario_decision", attempt_id=attempt_id, step_id=str(step_id),
                option_id=str(option_id), points=points,
                is_correct=bool(option.get("correct", False)), response_time_ms=0)
            db.session.commit()
            next_id = option.get("nextStepId")
            if next_id:
                next_step = next(
                    (item for item in steps if str(item.get("id")) == str(next_id)), None
                )
                if not next_step:
                    abort(400)
                return render_template("scenario_step.html", scenario=sc, attempt_id=attempt_id,
                    step=next_step, feedback=option.get("feedback", ""))
            decisions = rows("scenario_decision", table("scenario_decision").c.attempt_id == attempt_id)
            score = sum(d["points"] for d in decisions)
            max_score = sum(max([int(o.get("points", 0)) for o in s.get("options", [])] or [0]) for s in steps)
            change("scenario_attempt", table("scenario_attempt").c.id == attempt_id,
                   score=score, max_score=max_score, completed_at=datetime.now())
            db.session.commit()
            return render_template("scenario_result.html", scenario=sc, score=score, max_score=max_score,
                                   percent=round(score * 100 / max_score) if max_score else 0)
        first = steps[0] if steps else None
        if not first: return render_template("message.html", title=sc["title"], message="This scenario has no steps yet.")
        aid = add("scenario_attempt", user_id=session["user_id"], scenario_id=scenario_id, started_at=datetime.now())
        db.session.commit()
        return render_template("scenario_step.html", scenario=sc, attempt_id=aid, step=first, feedback=None)

    @app.get("/drills")
    @login_required
    def drills():
        items = rows("drill", table("drill").c.institution_id == g.user["institution_id"],
                     table("drill").c.start_time.desc())
        return render_template("drills.html", items=items)

    @app.route("/drills/create", methods=["GET", "POST"])
    @roles_required("TEACHER", "INSTITUTION_ADMIN", "SYSTEM_ADMIN")
    def create_drill():
        if request.method == "POST":
            form = request.form
            start = datetime.fromisoformat(form["start_time"])
            end = datetime.fromisoformat(form["end_time"])
            if end <= start:
                flash("End time must be after start time.", "error")
                return redirect(url_for("create_drill"))
            scenario_row = one("scenario", table("scenario").c.id == int(form["scenario_id"]))
            if not scenario_row: abort(400)
            did = add("drill", institution_id=g.user["institution_id"], hazard_id=scenario_row["hazard_id"],
                scenario_id=scenario_row["id"], title=form["title"], created_by=session["user_id"],
                start_time=start, end_time=end, campus_wide=bool(form.get("campus_wide")),
                status="SCHEDULED", mode="SELF_PACED")
            db.session.commit()
            flash("Drill created. Add group participants through the drill administration workflow.", "success")
            return redirect(url_for("drills"))
        scenarios = rows("scenario", table("scenario").c.status == "PUBLISHED")
        return render_template("drill_create.html", scenarios=scenarios)

    @app.get("/emergency")
    @login_required
    def emergency():
        contacts = db.session.execute(select(table("emergency_contact")).where(
            (table("emergency_contact").c.institution_id == g.user["institution_id"]) |
            (table("emergency_contact").c.is_national == True)).order_by(table("emergency_contact").c.sort_order)).mappings().all()
        plan = one("emergency_plan", table("emergency_plan").c.institution_id == g.user["institution_id"])
        routes = rows("evacuation_route", table("evacuation_route").c.institution_id == g.user["institution_id"])
        return render_template("emergency.html", contacts=contacts, plan=plan, routes=routes)

    @app.post("/emergency/plan")
    @roles_required("INSTITUTION_ADMIN", "SYSTEM_ADMIN")
    def save_plan():
        content = request.form.get("content", "")
        t = table("emergency_plan")
        current = one("emergency_plan", t.c.institution_id == g.user["institution_id"])
        if current:
            change("emergency_plan", t.c.id == current["id"], content=content,
                   updated_by=session["user_id"], updated_at=datetime.now())
        else:
            add("emergency_plan", institution_id=g.user["institution_id"], content=content,
                updated_by=session["user_id"], updated_at=datetime.now())
        db.session.commit()
        flash("Emergency plan saved.", "success")
        return redirect(url_for("emergency"))

    @app.get("/progress")
    @login_required
    def progress():
        items = db.session.execute(select(table("module_progress"), table("module_text").c.title)
            .join(table("module_text"), table("module_text").c.module_id == table("module_progress").c.module_id)
            .where(table("module_progress").c.user_id == session["user_id"],
                   table("module_text").c.language == (g.user["language"] or "en"))).mappings().all()
        return render_template("progress.html", items=items)

    @app.get("/certificates")
    @login_required
    def certificates():
        return render_template("message.html", title="Certificates", message="Certificate eligibility is based on completed learning and assessment requirements. The certificate page is ready for the issuing policy to be configured.")

    @app.get("/profile")
    @login_required
    def profile():
        return render_template("profile.html")

    # Versioned JSON endpoints used by browser clients and future integrations.
    @app.get("/api/v1/me")
    @login_required
    def api_me():
        u = dict(g.user); u.pop("password_hash", None)
        return jsonify(u)

    @app.get("/api/v1/modules")
    @login_required
    def api_modules():
        return jsonify([dict(x) for x in db.session.execute(select(table("learning_module"))).mappings().all()])

    @app.get("/api/v1/notifications")
    @login_required
    def api_notifications():
        t = table("notification")
        data = rows("notification", t.c.user_id == session["user_id"], t.c.created_at.desc(), 100)
        return jsonify([dict(x) for x in data])

    @app.post("/api/v1/notifications/<int:notification_id>/read")
    @login_required
    def api_notification_read(notification_id):
        t = table("notification")
        change("notification", and_(t.c.id == notification_id, t.c.user_id == session["user_id"]), is_read=True)
        db.session.commit()
        return "", 204

    @app.get("/health")
    def health():
        try:
            db.session.execute(select(1))
            return jsonify(status="ok", database="connected")
        except Exception:
            db.session.rollback()
            app.logger.exception("Health check database connection failed")
            return jsonify(status="error", database="unavailable"), 503
