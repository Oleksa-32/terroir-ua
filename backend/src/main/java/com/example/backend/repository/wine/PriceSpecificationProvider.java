package com.example.backend.repository.wine;

import com.example.backend.model.Wine;
import com.example.backend.repository.SpecificationProvider;
import java.math.BigDecimal;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class PriceSpecificationProvider implements SpecificationProvider<Wine> {
    private static final String PRICE_KEY = "price";

    @Override
    public String getKey() {
        return PRICE_KEY;
    }

    @Override
    public Specification<Wine> getSpecification(String[] params) {
        BigDecimal min = new BigDecimal(params[0]);
        BigDecimal max = new BigDecimal(params[1]);
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get(PRICE_KEY), min, max);
    }
}
