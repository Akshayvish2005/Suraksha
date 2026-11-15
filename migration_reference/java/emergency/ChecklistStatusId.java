package com.suraksha.suraksha.emergency;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ChecklistStatusId implements Serializable {

    @Column(name = "institution_id")
    private Long institutionId;

    @Column(name = "item_id")
    private Long itemId;

    public ChecklistStatusId() {
    }

    public ChecklistStatusId(Long institutionId, Long itemId) {
        this.institutionId = institutionId;
        this.itemId = itemId;
    }

    public Long getInstitutionId() {
        return institutionId;
    }

    public Long getItemId() {
        return itemId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChecklistStatusId that)) return false;
        return Objects.equals(institutionId, that.institutionId) &&
               Objects.equals(itemId, that.itemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(institutionId, itemId);
    }
}

