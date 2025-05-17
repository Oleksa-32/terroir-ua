package com.example.backend.repository;

import com.example.backend.dto.wine.WineSearchParametersDto;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T> {
    Specification<T> build(WineSearchParametersDto searchParametersDto);
}
