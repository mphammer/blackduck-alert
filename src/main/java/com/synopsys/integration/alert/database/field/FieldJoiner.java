package com.synopsys.integration.alert.database.field;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.synopsys.integration.alert.database.field.repository.FieldValuesEntityRepository;
import com.synopsys.integration.alert.database.field.repository.GroupingFieldRepository;

@Component
public class FieldJoiner {
    private final FieldValuesEntityRepository fieldValuesEntityRepository;
    private final GroupingFieldRepository groupingFieldRepository;

    @Autowired
    public FieldJoiner(final FieldValuesEntityRepository fieldValuesEntityRepository, final GroupingFieldRepository groupingFieldRepository) {
        this.fieldValuesEntityRepository = fieldValuesEntityRepository;
        this.groupingFieldRepository = groupingFieldRepository;
    }

    public List<FieldValuesEntity> retrieveFieldsFromGroupID(final Long groupId) {
        return groupingFieldRepository.findByGroupingId(groupId).stream()
                   .map(groupingFieldRelation -> fieldValuesEntityRepository.findById(groupingFieldRelation.getFieldId()))
                   .filter(Optional::isPresent)
                   .map(Optional::get)
                   .collect(Collectors.toList());
    }
}