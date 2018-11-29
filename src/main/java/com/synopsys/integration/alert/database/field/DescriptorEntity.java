package com.synopsys.integration.alert.database.field;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.synopsys.integration.alert.database.entity.DatabaseEntity;

@Entity
@Table(schema = "alert", name = "descriptor")
public class DescriptorEntity extends DatabaseEntity {

    @Column(name = "descriptor_name")
    private String descriptorName;

    @Column(name = "descriptor_type")
    private String descriptorType;

    public DescriptorEntity() {
    }

    public DescriptorEntity(final String descriptorName, final String descriptorType) {
        this.descriptorName = descriptorName;
        this.descriptorType = descriptorType;
    }

    public String getDescriptorName() {
        return descriptorName;
    }

    public String getDescriptorType() {
        return descriptorType;
    }
}
