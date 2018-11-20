package com.synopsys.integration.alert.database.field.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.synopsys.integration.alert.database.field.FieldValuesEntity;

public interface FieldValuesEntityRepository extends JpaRepository<FieldValuesEntity, Long> {

}
