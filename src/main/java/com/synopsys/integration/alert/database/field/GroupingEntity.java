package com.synopsys.integration.alert.database.field;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(schema = "alert", name = "grouping")
public class GroupingEntity {

    @Column(name = "descriptor_type")
    private String descriptorType;

    @Column(name = "display_type")
    private String displayType;
}
