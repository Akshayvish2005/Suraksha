package com.suraksha.suraksha.learning;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface LearningModuleRepository extends JpaRepository<LearningModule, Long> {

        @Query(value = "SELECT lm.id AS id, h.code AS hazardCode, h.name_en AS hazardName, " +
                        "mt.title AS title, mt.description AS description " +
                        "FROM learning_module lm " +
                        "JOIN hazard h ON h.id = lm.hazard_id " +
                        "JOIN module_text mt ON mt.module_id = lm.id AND mt.language = 'en' " +
                        "WHERE lm.status = 'PUBLISHED' " +
                        "ORDER BY h.id, lm.sort_order", nativeQuery = true)
        List<ModuleSummary> findPublishedModulesEnglish();

        @Query(value = "SELECT lm.id AS id, h.code AS hazardCode, h.name_en AS hazardName, " +
                        "mt.title AS title, mt.description AS description " +
                        "FROM learning_module lm " +
                        "JOIN hazard h ON h.id = lm.hazard_id " +
                        "JOIN module_text mt ON mt.module_id = lm.id AND mt.language = 'en' " +
                        "WHERE lm.id = :moduleId", nativeQuery = true)
        java.util.Optional<ModuleSummary> findModuleById(
                        @org.springframework.data.repository.query.Param("moduleId") Long moduleId);
}
