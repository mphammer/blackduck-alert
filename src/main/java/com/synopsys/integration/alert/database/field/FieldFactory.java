package com.synopsys.integration.alert.database.field;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

@Component
public class FieldFactory {
    private final Gson gson;
    private final FieldAccessor fieldAccessor;

    @Autowired
    public FieldFactory(final Gson gson, final FieldAccessor fieldAccessor) {
        this.gson = gson;
        this.fieldAccessor = fieldAccessor;
    }

    //    public FieldValuesEntity saveFieldWithValue(final Long groupingId, final String key, final Boolean secure, final String value) {
    //        final String jsonRepresentation = convertStringToJson(value);
    //        final FieldValuesEntity fieldValuesEntity = new FieldValuesEntity(groupingId, key, jsonRepresentation, secure);
    //        return fieldAccessor.saveField(fieldValuesEntity);
    //    }

    public FieldValuesEntity saveFieldWithValues(final Long groupingId, final String key, final Boolean secure, final Collection<String> values) {
        final String jsonRepresentation = convertStringsToJson(values);
        final FieldValuesEntity fieldValuesEntity = new FieldValuesEntity(groupingId, key, jsonRepresentation, secure);
        return fieldAccessor.saveField(fieldValuesEntity);
    }

    public FieldGroupingWrapper getFieldGrouping(final Long groupingId) {
        final Optional<GroupingEntity> groupingEntityOptional = fieldAccessor.findGroupingById(groupingId);
        if (groupingEntityOptional.isPresent()) {
            final GroupingEntity groupingEntity = groupingEntityOptional.get();
            final Collection<FieldValuesEntity> fieldValuesEntities = fieldAccessor.findFieldsOfGrouping(groupingEntity.getId());
        }
    }

    public FieldValue getFieldValue(final FieldValuesEntity fieldValuesEntity) {
        final Long id = fieldValuesEntity.getId();
        final Boolean secure = fieldValuesEntity.getSecure();
        String parsedJson =
    }

    private Collection<String> convertFromJson(final String json) {

    }

    //    private String convertStringToJson(final String value) {
    //        final Map<String, String> valueMap = new HashMap();
    //        valueMap.put(FieldValuesEntity.COLUMN_NAME_VALUE, value);
    //        return gson.toJson(valueMap);
    //    }

    private String convertStringsToJson(final Collection<String> values) {
        final Map<String, Collection<String>> valueMap = new HashMap();
        valueMap.put(FieldValuesEntity.COLUMN_NAME_VALUE, values);
        return gson.toJson(valueMap);
    }
}
