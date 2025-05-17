package com.example.backend.repository.wine;

import com.example.backend.model.Wine;
import com.example.backend.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class YearSpecificationProvider implements SpecificationProvider<Wine> {
    private static final String YEAR_KEY = "year";
    @Override
    public String getKey() {
        return YEAR_KEY;
    }

    @Override
    public Specification<Wine> getSpecification(String[] params) {
        Integer min = Integer.valueOf(params[0]);
        Integer max = Integer.valueOf(params[1]);
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get(YEAR_KEY), min, max);
    }
}
