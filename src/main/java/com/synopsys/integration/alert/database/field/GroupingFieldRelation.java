package com.synopsys.integration.alert.database.field;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.synopsys.integration.alert.database.field.key.GroupingFieldRelationPK;
import com.synopsys.integration.alert.database.relation.DatabaseRelation;

@Entity
@IdClass(GroupingFieldRelationPK.class)
@Table(schema = "alert", name = "grouping_field_relation")
public class GroupingFieldRelation extends DatabaseRelation {

    @Column(name = "grouping_id")
    private Long groupingId;

    @Column(name = "field_id")
    private Long fieldId;

    public GroupingFieldRelation() {
    }

    public GroupingFieldRelation(final Long groupingId, final Long fieldId) {
        this.groupingId = groupingId;
        this.fieldId = fieldId;
    }

    public Long getGroupingId() {
        return groupingId;
    }

    public Long getFieldId() {
        return fieldId;
    }
}
