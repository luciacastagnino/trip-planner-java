package com.example.demo.repositories;

import com.example.demo.entities.ActivityEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.enums.ActivityCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<ActivityEntity, Long>, JpaSpecificationExecutor<ActivityEntity> {
    Page<ActivityEntity> findByUsers_Id(Long userId, Pageable pageable);
    Page<ActivityEntity> findByCompanyId(Long companyId, Pageable pageable);
    Page<ActivityEntity> findByCategory(ActivityCategory category, Pageable pageable);
    Page<ActivityEntity> findByDateBetween(LocalDate start, LocalDate end, Pageable pageable);
    Page<ActivityEntity> findByCategoryAndDateBetween(ActivityCategory category, LocalDate start, LocalDate end, Pageable pageable);
    Page<ActivityEntity> findAllByAvailableTrue(Pageable pageable);
    Page<ActivityEntity> findAllByAvailableFalse(Pageable pageable);
}

