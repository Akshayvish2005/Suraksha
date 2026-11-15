package com.suraksha.suraksha.emergency;

import jakarta.persistence.*;

@Entity
@Table(name = "checklist_item")
public class ChecklistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hazard_id")
    private Long hazardId;

    @Column(name = "item_text", nullable = false, length = 300)
    private String itemText;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder = 0;

    public ChecklistItem() {
    }

    public ChecklistItem(Long hazardId, String itemText, int sortOrder) {
        this.hazardId = hazardId;
        this.itemText = itemText;
        this.sortOrder = sortOrder;
    }

    public Long getId() {
        return id;
    }

    public Long getHazardId() {
        return hazardId;
    }

    public String getItemText() {
        return itemText;
    }

    public int getSortOrder() {
        return sortOrder;
    }
}

