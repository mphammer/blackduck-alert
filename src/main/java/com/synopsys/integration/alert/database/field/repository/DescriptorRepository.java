package com.synopsys.integration.alert.database.field.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.synopsys.integration.alert.database.field.DescriptorEntity;

public interface DescriptorRepository extends JpaRepository<DescriptorEntity, Long> {
}
