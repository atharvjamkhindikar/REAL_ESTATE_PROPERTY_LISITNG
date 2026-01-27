package com.realestate.repository;

import com.realestate.model.BuilderGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BuilderGroupRepository extends JpaRepository<BuilderGroup, Long> {

    Optional<BuilderGroup> findByName(String name);

    Optional<BuilderGroup> findByNameIgnoreCase(String name);

    List<BuilderGroup> findByActive(Boolean active);

    List<BuilderGroup> findAllByOrderByNameAsc();
}
