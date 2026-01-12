package com.zaw.workflow.repository;

import com.zaw.workflow.entity.Flow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FlowRepository extends JpaRepository<Flow, Long> {
    List<Flow> findByNameContaining(String name);

    Optional<Flow> findTopByCodeOrderByFlowVersionDesc(String code);
}
