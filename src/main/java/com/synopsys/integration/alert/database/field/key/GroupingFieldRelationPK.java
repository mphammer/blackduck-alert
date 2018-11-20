package com.synopsys.integration.alert.database.field.key;

import java.io.Serializable;

public class GroupingFieldRelationPK implements Serializable {
    private Long groupID;
    private Long fieldID;

    public GroupingFieldRelationPK() {
    }

    public Long getGroupID() {
        return groupID;
    }

    public Long getFieldID() {
        return fieldID;
    }
}
