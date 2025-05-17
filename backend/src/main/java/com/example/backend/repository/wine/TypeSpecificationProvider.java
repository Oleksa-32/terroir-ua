package com.example.backend.repository.wine;

import com.example.backend.model.Types;
import com.example.backend.model.Wine;
import com.example.backend.repository.SpecificationProvider;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TypeSpecificationProvider implements SpecificationProvider<Wine> {
    private static final String TYPES_KEY = "type";

    @Override
    public String getKey() {
        return TYPES_KEY;
    }

    @Override
    public Specification getSpecification(String[] labels) {
        Set<Types> set = Arrays.stream(labels)
                .map(Types::fromLabel)
                .collect(Collectors.toSet());
        return ((root, query, criteriaBuilder) -> root.get(TYPES_KEY).in(set));
    }
}
