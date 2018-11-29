package com.synopsys.integration.alert.database.field;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.synopsys.integration.alert.database.entity.DatabaseEntity;

@Entity
@Table(schema = "alert", name = "configuration")
public class ConfigurationEntity extends DatabaseEntity {

}
