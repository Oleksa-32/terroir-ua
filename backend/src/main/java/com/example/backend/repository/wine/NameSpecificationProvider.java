package com.example.backend.repository.wine;

import com.example.backend.model.Wine;
import com.example.backend.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class NameSpecificationProvider implements SpecificationProvider<Wine> {
    private static final String NAME_KEY = "name";

    @Override
    public String getKey() {
        return NAME_KEY;
    }

    @Override
    public Specification<Wine> getSpecification(String[] params) {
        String search = params[0].toLowerCase();
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("name")), "%" + search + "%");
    }
}
