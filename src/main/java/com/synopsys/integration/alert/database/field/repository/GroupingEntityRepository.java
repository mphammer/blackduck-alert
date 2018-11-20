package com.synopsys.integration.alert.database.field.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;

import com.synopsys.integration.alert.database.field.GroupingEntity;

public interface GroupingEntityRepository extends JpaRepository<GroupingEntity, Long> {
    Collection<GroupingEntity> findByDescriptorType(String descriptorType);

    Collection<GroupingEntity> findByDescriptorTypeAndDisplayType(String descriptorType, String displayType);
}
