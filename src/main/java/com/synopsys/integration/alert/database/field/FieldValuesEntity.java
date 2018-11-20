package com.synopsys.integration.alert.database.field;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.synopsys.integration.alert.database.entity.DatabaseEntity;

@Entity
@Table(schema = "alert", name = "field_values")
public class FieldValuesEntity extends DatabaseEntity {

    @Column(name = "key")
    private final String key;

    @Column(name = "value")
    private final String value;

    @Column(name = "secure")
    private final Boolean secure;

    public FieldValuesEntity(final String key, final String value, final Boolean secure) {
        this.key = key;
        this.value = value;
        this.secure = secure;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public Boolean isSecure() {
        return secure;
    }
}
