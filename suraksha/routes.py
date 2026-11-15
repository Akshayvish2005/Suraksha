import json, csv, io
from urllib.parse import urlparse
from datetime import datetime, date
from flask import render_template, request, redirect, url_for, session, flash, jsonify, abort, Response, g
from sqlalchemy import select, and_, func
from .extensions import db
from .db_helpers import table, rows, one, add, change, remove
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
        mt = table("learning_module")
        m = one("learning_module", and_(mt.c.id == module_id, mt.c.status == "PUBLISHED"))
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
        lesson_table = table("lesson")
        lesson = one("lesson", and_(lesson_table.c.id == lesson_id, lesson_table.c.status == "PUBLISHED"))
        if not lesson: abort(404)
        module_table = table("learning_module")
        published_module = one("learning_module", and_(module_table.c.id == lesson["module_id"], module_table.c.status == "PUBLISHED"))
        if not published_module: abort(404)
        tx = one("lesson_text", and_(table("lesson_text").c.lesson_id == lesson_id,
                                     table("lesson_text").c.language == (g.user["language"] or "en")))
        return render_template("lesson.html", lesson=lesson, lesson_text=tx)

    @app.post("/lessons/<int:lesson_id>/complete")
    @login_required
    def complete_lesson(lesson_id):
        lp = table("lesson_progress")
        mp = table("module_progress")
        lt = table("lesson")

        lesson = one("lesson", and_(lt.c.id == lesson_id, lt.c.status == "PUBLISHED"))
        if not lesson:
            abort(404)
        module_table = table("learning_module")
        if not one("learning_module", and_(module_table.c.id == lesson["module_id"], module_table.c.status == "PUBLISHED")):
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
                started_at=existing_module["started_at"] or datetime.now(),
                completed_at=datetime.now() if all_completed else None
            )
        else:
            add(
                "module_progress",
                user_id=uid,
                module_id=module_id,
                status=new_status,
                started_at=datetime.now(),
                completed_at=datetime.now() if all_completed else None
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
        module_table = table("learning_module")
        if not one("learning_module", and_(module_table.c.id == module_id,
                                           module_table.c.status == "PUBLISHED")):
            abort(404)
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

            expected_questions = rows("question", and_(
                q.c.module_id == module_id, q.c.status == "PUBLISHED",
                q.c.purpose.in_([attempt_type, "BOTH"])
            ))
            expected_qids = {int(item["id"]) for item in expected_questions}
            if not expected_qids or set(qids) != expected_qids:
                abort(400, description="Submit an answer for every published question.")
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
        scenario_table = table("scenario")
        sc = one("scenario", and_(scenario_table.c.id == scenario_id,
                                  scenario_table.c.status == "PUBLISHED"))
        if not sc: abort(404)
        try:
            definition = json.loads(sc["definition"] or "{}")
        except (TypeError, ValueError):
            app.logger.exception("Invalid scenario definition for scenario %s", scenario_id)
            abort(500, description="This scenario is temporarily unavailable.")
        if not isinstance(definition, dict) or not isinstance(definition.get("steps", []), list):
            abort(500, description="This scenario is temporarily unavailable.")
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
            previous_decisions = db.session.execute(
                select(decisions_table).where(decisions_table.c.attempt_id == attempt_id)
                .order_by(decisions_table.c.id)
            ).mappings().all()
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
            if g.user["institution_id"] is None:
                flash("A system administrator must select an institution before creating a drill.", "error")
                return redirect(url_for("drills"))
            did = add("drill", institution_id=g.user["institution_id"], hazard_id=scenario_row["hazard_id"],
                scenario_id=scenario_row["id"], title=form["title"], created_by=session["user_id"],
                start_time=start, end_time=end, campus_wide=bool(form.get("campus_wide")),
                status="SCHEDULED", mode="SELF_PACED")
            # The creator is automatically enrolled as a floor warden.
            participant_table = table("drill_participant")
            existing_creator = one("drill_participant", and_(
                participant_table.c.drill_id == did,
                participant_table.c.user_id == session["user_id"]
            ))
            if not existing_creator:
                add("drill_participant", drill_id=did, user_id=session["user_id"],
                    drill_role="FLOOR_WARDEN", status="ASSIGNED")
            # Campus-wide drills create participant assignments for active users in the institution.
            if bool(form.get("campus_wide")):
                users_table = table("app_user")
                institution_users = rows("app_user", and_(
                    users_table.c.institution_id == g.user["institution_id"],
                    users_table.c.active.is_(True)
                ))
                for member in institution_users:
                    if member["id"] == session["user_id"]:
                        continue
                    if not one("drill_participant", and_(
                        participant_table.c.drill_id == did,
                        participant_table.c.user_id == member["id"]
                    )):
                        add("drill_participant", drill_id=did, user_id=member["id"],
                            drill_role="STUDENT", status="ASSIGNED")
                    add("notification", user_id=member["id"], notification_type="DRILL_SCHEDULED",
                        message=f"New campus-wide drill scheduled: {form['title']}",
                        link=url_for("drill_detail", drill_id=did))
            db.session.commit()
            flash("Drill created and participants assigned.", "success")
            return redirect(url_for("drill_detail", drill_id=did))
        scenarios = rows("scenario", table("scenario").c.status == "PUBLISHED")
        return render_template("drill_create.html", scenarios=scenarios)

    @app.get("/drills/<int:drill_id>")
    @login_required
    def drill_detail(drill_id):
        dt = table("drill")
        drill = one("drill", dt.c.id == drill_id)
        if not drill:
            abort(404)
        if g.user["role"] != "SYSTEM_ADMIN" and drill["institution_id"] != g.user["institution_id"]:
            abort(403)
        pt = table("drill_participant")
        participant = one("drill_participant", and_(
            pt.c.drill_id == drill_id, pt.c.user_id == session["user_id"]
        ))
        routes = rows("evacuation_route", table("evacuation_route").c.institution_id == drill["institution_id"])
        return render_template("drill_detail.html", drill=drill, participant=participant, routes=routes)

    @app.post("/drills/<int:drill_id>/join")
    @login_required
    def join_drill(drill_id):
        dt, pt = table("drill"), table("drill_participant")
        drill = one("drill", dt.c.id == drill_id)
        if not drill:
            abort(404)
        if g.user["role"] != "SYSTEM_ADMIN" and drill["institution_id"] != g.user["institution_id"]:
            abort(403)
        now = datetime.now()
        if drill["status"] in ("CLOSED", "CANCELLED") or now < drill["start_time"] or now > drill["end_time"]:
            flash("This drill is not currently open for participation.", "error")
            return redirect(url_for("drill_detail", drill_id=drill_id))
        participant = one("drill_participant", and_(
            pt.c.drill_id == drill_id, pt.c.user_id == session["user_id"]
        ))
        if not participant and not drill["campus_wide"]:
            abort(403)
        if not participant:
            add("drill_participant", drill_id=drill_id, user_id=session["user_id"],
                drill_role="STUDENT", status="JOINED", joined_at=now)
        elif participant["status"] not in ("COMPLETED", "MISSED"):
            change("drill_participant", and_(pt.c.drill_id == drill_id,
                pt.c.user_id == session["user_id"]), status="JOINED", joined_at=participant["joined_at"] or now)
        db.session.commit()
        return redirect(url_for("drill_detail", drill_id=drill_id))

    @app.post("/drills/<int:drill_id>/complete")
    @login_required
    def complete_drill(drill_id):
        dt, pt = table("drill"), table("drill_participant")
        drill = one("drill", dt.c.id == drill_id)
        if not drill:
            abort(404)
        if g.user["role"] != "SYSTEM_ADMIN" and drill["institution_id"] != g.user["institution_id"]:
            abort(403)
        participant = one("drill_participant", and_(
            pt.c.drill_id == drill_id, pt.c.user_id == session["user_id"]
        ))
        if not participant or participant["status"] != "JOINED":
            abort(403)
        route_id = request.form.get("evacuation_route_id") or None
        try:
            route_id = int(route_id) if route_id else None
            evacuation_seconds = max(0, min(int(request.form.get("evacuation_time_seconds", "120")), 86400))
        except (TypeError, ValueError):
            abort(400)
        if route_id:
            rt = table("evacuation_route")
            route = one("evacuation_route", and_(rt.c.id == route_id,
                rt.c.institution_id == drill["institution_id"]))
            if not route:
                abort(400)
        change("drill_participant", and_(pt.c.drill_id == drill_id,
            pt.c.user_id == session["user_id"]), status="COMPLETED",
            completed_at=datetime.now(), evacuation_route_id=route_id,
            evacuation_time_seconds=evacuation_seconds)
        db.session.commit()
        flash("Drill completion recorded.", "success")
        return redirect(url_for("drill_detail", drill_id=drill_id))

    @app.get("/drills/<int:drill_id>/report")
    @roles_required("TEACHER", "INSTITUTION_ADMIN", "SYSTEM_ADMIN")
    def drill_report(drill_id):
        dt, pt = table("drill"), table("drill_participant")
        drill = one("drill", dt.c.id == drill_id)
        if not drill:
            abort(404)
        if g.user["role"] != "SYSTEM_ADMIN" and drill["institution_id"] != g.user["institution_id"]:
            abort(403)
        participants = db.session.execute(select(pt, table("app_user").c.full_name,
                table("app_user").c.username, table("evacuation_route").c.name.label("route_name"))
            .join(table("app_user"), table("app_user").c.id == pt.c.user_id)
            .outerjoin(table("evacuation_route"), table("evacuation_route").c.id == pt.c.evacuation_route_id)
            .where(pt.c.drill_id == drill_id).order_by(table("app_user").c.full_name)).mappings().all()
        total = len(participants)
        completed = sum(1 for p in participants if p["status"] == "COMPLETED")
        times = [p["evacuation_time_seconds"] for p in participants if p["evacuation_time_seconds"]]
        average = round(sum(times) / len(times)) if times else 0
        return render_template("drill_report.html", drill=drill, participants=participants,
            total=total, completed=completed, participation_pct=round(completed * 100 / total) if total else 0,
            average_evacuation_seconds=average)

    @app.get("/emergency")
    @login_required
    def emergency():
        institution_id = g.user["institution_id"]
        if institution_id is None:
            contacts = rows("emergency_contact", table("emergency_contact").c.is_national.is_(True),
                            table("emergency_contact").c.sort_order)
            plan = None
            routes = []
            checklist_items = rows("checklist_item", order=table("checklist_item").c.sort_order)
            status_map = {}
        else:
            ct = table("emergency_contact")
            contacts = db.session.execute(select(ct).where(
                (ct.c.institution_id == institution_id) | ct.c.is_national.is_(True)
            ).order_by(ct.c.sort_order, ct.c.id)).mappings().all()
            plan = one("emergency_plan", table("emergency_plan").c.institution_id == institution_id)
            routes = rows("evacuation_route", table("evacuation_route").c.institution_id == institution_id,
                          table("evacuation_route").c.sort_order)
            checklist_items = rows("checklist_item", order=table("checklist_item").c.sort_order)
            st = table("checklist_status")
            statuses = rows("checklist_status", st.c.institution_id == institution_id)
            status_map = {s["item_id"]: s for s in statuses}
        checked_count = sum(1 for item in checklist_items
                            if status_map.get(item["id"]) and status_map[item["id"]]["is_checked"])
        checklist = [{"item": item, "checked": bool(status_map.get(item["id"]) and
                      status_map[item["id"]]["is_checked"])} for item in checklist_items]
        return render_template("emergency.html", contacts=contacts, plan=plan, routes=routes,
            checklist=checklist, checklist_checked_count=checked_count,
            checklist_total_count=len(checklist_items))

    @app.post("/emergency/checklist/toggle")
    @login_required
    def toggle_checklist():
        institution_id = g.user["institution_id"]
        if institution_id is None:
            abort(403)
        try:
            item_id = int(request.form.get("item_id", ""))
        except (TypeError, ValueError):
            abort(400)
        item = one("checklist_item", table("checklist_item").c.id == item_id)
        if not item:
            abort(404)
        checked = request.form.get("checked") == "true"
        st = table("checklist_status")
        existing = one("checklist_status", and_(st.c.institution_id == institution_id,
                                                  st.c.item_id == item_id))
        values = dict(is_checked=checked, checked_by=session["user_id"],
                      checked_on=date.today() if checked else None)
        if existing:
            change("checklist_status", and_(st.c.institution_id == institution_id,
                                             st.c.item_id == item_id), **values)
        else:
            add("checklist_status", institution_id=institution_id, item_id=item_id, **values)
        db.session.commit()
        return redirect(url_for("emergency"))

    @app.post("/emergency/plan")
    @roles_required("INSTITUTION_ADMIN", "SYSTEM_ADMIN")
    def save_plan():
        institution_id = g.user["institution_id"]
        if institution_id is None:
            abort(400, description="Select an institution before editing its emergency plan.")
        content = request.form.get("content", "").strip()
        if len(content) > 20000:
            abort(400, description="Emergency plan is too long.")
        t = table("emergency_plan")
        current = one("emergency_plan", t.c.institution_id == institution_id)
        values = dict(content=content, updated_by=session["user_id"], updated_at=datetime.now())
        if current:
            change("emergency_plan", t.c.id == current["id"], **values)
        else:
            add("emergency_plan", institution_id=institution_id, **values)
        db.session.commit()
        flash("Emergency plan saved.", "success")
        return redirect(url_for("emergency"))

    @app.post("/emergency/contacts")
    @roles_required("INSTITUTION_ADMIN", "SYSTEM_ADMIN")
    def create_emergency_contact():
        institution_id = g.user["institution_id"]
        if institution_id is None:
            abort(400, description="Select an institution before adding a contact.")
        name = request.form.get("name", "").strip()
        phone = request.form.get("phone", "").strip()
        category = request.form.get("category", "").strip()
        if not name or not phone or len(name) > 150 or len(phone) > 30 or len(category) > 50:
            abort(400, description="Enter a valid contact name, phone and category.")
        add("emergency_contact", institution_id=institution_id, name=name, phone=phone,
            category=category or "Emergency", is_national=False, sort_order=100)
        db.session.commit()
        flash("Emergency contact added.", "success")
        return redirect(url_for("emergency"))

    @app.post("/emergency/contacts/<int:contact_id>/delete")
    @roles_required("INSTITUTION_ADMIN", "SYSTEM_ADMIN")
    def delete_emergency_contact(contact_id):
        institution_id = g.user["institution_id"]
        if institution_id is None:
            abort(400)
        ct = table("emergency_contact")
        deleted = remove("emergency_contact", and_(ct.c.id == contact_id,
            ct.c.institution_id == institution_id, ct.c.is_national.is_(False)))
        if not deleted:
            abort(404)
        db.session.commit()
        flash("Emergency contact deleted.", "success")
        return redirect(url_for("emergency"))

    @app.post("/emergency/routes")
    @roles_required("INSTITUTION_ADMIN", "SYSTEM_ADMIN")
    def create_evacuation_route():
        institution_id = g.user["institution_id"]
        if institution_id is None:
            abort(400, description="Select an institution before adding a route.")
        name = request.form.get("name", "").strip()
        assembly_point = request.form.get("assembly_point", "").strip()
        description = request.form.get("description", "").strip()
        if not name or len(name) > 150 or len(assembly_point) > 200 or len(description) > 1000:
            abort(400, description="Enter valid evacuation route details.")
        add("evacuation_route", institution_id=institution_id, name=name,
            description=description, assembly_point=assembly_point, image_path=None, sort_order=100)
        db.session.commit()
        flash("Evacuation route added.", "success")
        return redirect(url_for("emergency"))

    @app.post("/emergency/routes/<int:route_id>/delete")
    @roles_required("INSTITUTION_ADMIN", "SYSTEM_ADMIN")
    def delete_evacuation_route(route_id):
        institution_id = g.user["institution_id"]
        if institution_id is None:
            abort(400)
        rt = table("evacuation_route")
        deleted = remove("evacuation_route", and_(rt.c.id == route_id,
            rt.c.institution_id == institution_id))
        if not deleted:
            abort(404)
        db.session.commit()
        flash("Evacuation route deleted.", "success")
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
        uid = session["user_id"]
        language = g.user["language"] or "en"
        lm, hz, mt = table("learning_module"), table("hazard"), table("module_text")
        modules = db.session.execute(select(lm.c.id.label("module_id"), lm.c.pass_mark_percent,
            hz.c.code.label("hazard_code"), hz.c.name_en.label("hazard_name"),
            mt.c.title.label("module_title")).join(hz, hz.c.id == lm.c.hazard_id)
            .join(mt, mt.c.module_id == lm.c.id)
            .where(lm.c.status == "PUBLISHED", mt.c.language == language)
            .order_by(lm.c.sort_order)).mappings().all()
        attempts = table("assessment_attempt")
        items = []
        for module in modules:
            post = db.session.execute(select(attempts).where(
                attempts.c.user_id == uid, attempts.c.module_id == module["module_id"],
                attempts.c.attempt_type == "POST", attempts.c.status == "SUBMITTED"
            ).order_by(attempts.c.started_at.desc()).limit(1)).mappings().first()
            passed = bool(post and post["score_percent"] is not None and
                          float(post["score_percent"]) >= module["pass_mark_percent"])
            items.append(dict(module_id=module["module_id"], module_title=module["module_title"],
                hazard_code=module["hazard_code"], hazard_name=module["hazard_name"],
                is_unlocked=passed, score_percent=round(float(post["score_percent"])) if post and
                post["score_percent"] is not None else None,
                issued_at=post["submitted_at"] if passed else None,
                certificate_id=f"SURAKSHA-{module['hazard_code']}-{uid:04d}"))
        return render_template("certificates.html", certificates=items)

    @app.get("/certificates/<int:module_id>/view")
    @login_required
    def view_certificate(module_id):
        uid = session["user_id"]
        lm, hz, mt = table("learning_module"), table("hazard"), table("module_text")
        module = db.session.execute(select(lm.c.id.label("module_id"), lm.c.pass_mark_percent,
            hz.c.code.label("hazard_code"), hz.c.name_en.label("hazard_name"),
            mt.c.title.label("module_title")).join(hz, hz.c.id == lm.c.hazard_id)
            .join(mt, mt.c.module_id == lm.c.id)
            .where(lm.c.id == module_id, lm.c.status == "PUBLISHED",
                   mt.c.language == (g.user["language"] or "en"))).mappings().first()
        if not module:
            abort(404)
        at = table("assessment_attempt")
        post = db.session.execute(select(at).where(
            at.c.user_id == uid, at.c.module_id == module_id,
            at.c.attempt_type == "POST", at.c.status == "SUBMITTED"
        ).order_by(at.c.started_at.desc()).limit(1)).mappings().first()
        if not post or post["score_percent"] is None or float(post["score_percent"]) < module["pass_mark_percent"]:
            abort(403, description="Passing the post-test is required to view this certificate.")
        return render_template("certificate_view.html", module=module,
            score=round(float(post["score_percent"])), issued_at=post["submitted_at"],
            certificate_id=f"SURAKSHA-{module['hazard_code']}-{uid:04d}")

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
        modules_table = table("learning_module")
        published = db.session.execute(select(modules_table).where(modules_table.c.status == "PUBLISHED")).mappings().all()
        return jsonify([dict(x) for x in published])

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
