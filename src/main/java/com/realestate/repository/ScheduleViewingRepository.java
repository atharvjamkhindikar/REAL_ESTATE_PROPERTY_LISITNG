package com.realestate.repository;

import com.realestate.model.ScheduleViewing;
import com.realestate.model.ViewingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleViewingRepository extends JpaRepository<ScheduleViewing, Long> {

    List<ScheduleViewing> findByUserId(Long userId);

    Page<ScheduleViewing> findByUserId(Long userId, Pageable pageable);

    List<ScheduleViewing> findByPropertyId(Long propertyId);

    List<ScheduleViewing> findByUserIdAndStatus(Long userId, ViewingStatus status);

    List<ScheduleViewing> findByPropertyIdAndStatus(Long propertyId, ViewingStatus status);

    List<ScheduleViewing> findByStatus(ViewingStatus status);

    List<ScheduleViewing> findByUserIdOrderByViewingDateAsc(Long userId);

    List<ScheduleViewing> findByPropertyIdOrderByViewingDateAsc(Long propertyId);

    @Query("SELECT s FROM ScheduleViewing s WHERE s.property.owner.id = :ownerId ORDER BY s.viewingDate ASC")
    List<ScheduleViewing> findAllViewingsForPropertyOwner(@Param("ownerId") Long ownerId);

    @Query("SELECT s FROM ScheduleViewing s WHERE s.property.owner.id = :ownerId AND s.status = :status ORDER BY s.viewingDate ASC")
    List<ScheduleViewing> findViewingsForPropertyOwnerByStatus(@Param("ownerId") Long ownerId, @Param("status") ViewingStatus status);

    @Query("SELECT s FROM ScheduleViewing s WHERE s.viewingDate >= :startDate AND s.viewingDate <= :endDate ORDER BY s.viewingDate ASC")
    List<ScheduleViewing> findViewingsInDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT s FROM ScheduleViewing s WHERE s.property.id = :propertyId AND s.viewingDate = :viewingDate AND s.status IN ('PENDING', 'CONFIRMED')")
    List<ScheduleViewing> findConflictingViewings(@Param("propertyId") Long propertyId, @Param("viewingDate") LocalDate viewingDate);

    @Query("SELECT COUNT(s) FROM ScheduleViewing s WHERE s.property.id = :propertyId AND s.status = 'CONFIRMED'")
    long countConfirmedViewingsForProperty(@Param("propertyId") Long propertyId);

    boolean existsByUserIdAndPropertyIdAndStatus(Long userId, Long propertyId, ViewingStatus status);

    Optional<ScheduleViewing> findByUserIdAndPropertyIdAndViewingDate(Long userId, Long propertyId, LocalDate viewingDate);
}
