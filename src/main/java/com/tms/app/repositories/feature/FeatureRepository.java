package com.tms.app.repositories.feature;

import com.tms.app.entities.feature.Feature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface FeatureRepository extends JpaRepository<Feature, UUID> {

    @Query("Select f from Feature f " +
            "JOIN RoleFeature rf ON f.id = rf.feature.id " +
            "where rf.role.id = :roleId and rf.isActive = true and f.isActive = true")
    List<Feature> findFeaturesByRoleId(UUID roleId);
}