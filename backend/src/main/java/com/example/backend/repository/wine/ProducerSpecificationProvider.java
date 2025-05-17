package com.example.backend.repository.wine;

import com.example.backend.model.Wine;
import com.example.backend.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class ProducerSpecificationProvider implements SpecificationProvider<Wine> {
    private static final String PRODUCER_KEY = "producer";
    @Override
    public String getKey() {
        return PRODUCER_KEY;
    }

    @Override
    public Specification<Wine> getSpecification(String[] producers) {
        return (root, query, criteriaBuilder) ->
                root.get(PRODUCER_KEY).in((Object[]) producers);
    }
}
