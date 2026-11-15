package com.suraksha.suraksha.emergency;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "checklist_status")
public class ChecklistStatus {

    @EmbeddedId
    private ChecklistStatusId id;

    @Column(name = "is_checked", nullable = false)
    private boolean isChecked = false;

    @Column(name = "checked_by")
    private Long checkedBy;

    @Column(name = "checked_on")
    private LocalDate checkedOn;

    public ChecklistStatus() {
    }

    public ChecklistStatus(ChecklistStatusId id, boolean isChecked, Long checkedBy, LocalDate checkedOn) {
        this.id = id;
        this.isChecked = isChecked;
        this.checkedBy = checkedBy;
        this.checkedOn = checkedOn;
    }

    public ChecklistStatusId getId() {
        return id;
    }

    public void setId(ChecklistStatusId id) {
        this.id = id;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public Long getCheckedBy() {
        return checkedBy;
    }

    public void setCheckedBy(Long checkedBy) {
        this.checkedBy = checkedBy;
    }

    public LocalDate getCheckedOn() {
        return checkedOn;
    }

    public void setCheckedOn(LocalDate checkedOn) {
        this.checkedOn = checkedOn;
    }
}

