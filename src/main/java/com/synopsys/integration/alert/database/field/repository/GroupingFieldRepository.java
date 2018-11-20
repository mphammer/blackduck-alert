package com.synopsys.integration.alert.database.field.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;

import com.synopsys.integration.alert.database.field.GroupingFieldRelation;
import com.synopsys.integration.alert.database.field.key.GroupingFieldRelationPK;

public interface GroupingFieldRepository extends JpaRepository<GroupingFieldRelation, GroupingFieldRelationPK> {
    Collection<GroupingFieldRelation> findByGroupingId(Long groupingId);
}
