package com.suraksha.suraksha.emergency;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmergencyContactRepository extends JpaRepository<EmergencyContact, Long> {

    @Query(value = "SELECT * FROM emergency_contact WHERE is_national = TRUE ORDER BY sort_order ASC, name ASC", nativeQuery = true)
    List<EmergencyContact> findNationalContacts();

    @Query(value = "SELECT * FROM emergency_contact WHERE (institution_id = :institutionId OR is_national = TRUE) ORDER BY is_national DESC, sort_order ASC, name ASC", nativeQuery = true)
    List<EmergencyContact> findContactsForInstitution(@Param("institutionId") Long institutionId);
}

