package com.suraksha.suraksha.drill;

import jakarta.persistence.*;

@Entity
@Table(name = "evacuation_route")
public class EvacuationRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "institution_id", nullable = false)
    private Long institutionId;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(name = "assembly_point", length = 200)
    private String assemblyPoint;

    @Column(name = "image_path", length = 300)
    private String imagePath;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder = 0;

    public EvacuationRoute() {
    }

    public EvacuationRoute(Long institutionId, String name, String description, String assemblyPoint, String imagePath,
            int sortOrder) {
        this.institutionId = institutionId;
        this.name = name;
        this.description = description;
        this.assemblyPoint = assemblyPoint;
        this.imagePath = imagePath;
        this.sortOrder = sortOrder;
    }

    public Long getId() {
        return id;
    }

    public Long getInstitutionId() {
        return institutionId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getAssemblyPoint() {
        return assemblyPoint;
    }

    public String getImagePath() {
        return imagePath;
    }

    public int getSortOrder() {
        return sortOrder;
    }
}
