package com.example.demo.repositories;

import com.example.demo.entities.CheckListEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CheckListRepository extends JpaRepository <CheckListEntity, Long> {
    List<CheckListEntity> findByUserId(Long userId);


}
