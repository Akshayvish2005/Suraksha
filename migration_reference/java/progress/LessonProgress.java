package com.suraksha.suraksha.progress;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// Maps to the lesson_progress table
@Entity
@Table(name = "lesson_progress")
@IdClass(LessonProgressId.class)
public class LessonProgress {

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Id
    @Column(name = "lesson_id", nullable = false)
    private Long lessonId;

    @Column(name = "completed_at", nullable = false)
    private LocalDateTime completedAt = LocalDateTime.now();

    protected LessonProgress() {
    }

    public LessonProgress(Long userId, Long lessonId) {
        this.userId = userId;
        this.lessonId = lessonId;
        this.completedAt = LocalDateTime.now();
    }

    public LessonProgress(Long userId, Long lessonId, LocalDateTime completedAt) {
        this.userId = userId;
        this.lessonId = lessonId;
        this.completedAt = completedAt != null ? completedAt : LocalDateTime.now();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getLessonId() {
        return lessonId;
    }

    public void setLessonId(Long lessonId) {
        this.lessonId = lessonId;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
