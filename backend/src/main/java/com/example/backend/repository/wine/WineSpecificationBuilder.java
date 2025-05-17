package com.example.backend.repository.wine;

import com.example.backend.dto.wine.WineSearchParametersDto;
import com.example.backend.model.Wine;
import com.example.backend.repository.SpecificationBuilder;
import com.example.backend.repository.SpecificationProviderManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WineSpecificationBuilder implements SpecificationBuilder<Wine> {
    private static final String NAME_KEY = "name";
    private static final String TYPE_KEY = "type";
    private static final String PRICE_KEY = "price";
    private static final String YEAR_KEY = "year";
    private static final String PRODUCER_KEY = "producer";
    private final SpecificationProviderManager<Wine> manager;

    @Override
    public Specification<Wine> build(WineSearchParametersDto param) {
        Specification<Wine> specification = Specification.where(null);

        if (param.getName() != null && !param.getName().isBlank()) {
            specification = specification.and(
                    manager.getSpecificationProvider(NAME_KEY)
                            .getSpecification(new String[]{ param.getName() })
            );
        }

        if (param.getTypes() != null && param.getTypes().length > 0) {
            specification = specification.and(manager
                    .getSpecificationProvider(TYPE_KEY)
                    .getSpecification(param.getTypes()));
        }

        if (param.getMinPrice() != null && param.getMaxPrice() != null) {
            String[] range = {
                    param.getMinPrice().toString(),
                    param.getMaxPrice().toString()
            };
            specification = specification.and(manager
                    .getSpecificationProvider(PRICE_KEY)
                    .getSpecification(range));
        }

        if (param.getMinYear() != null && param.getMaxYear() != null) {
            String[] range = {
                    param.getMinYear().toString(),
                    param.getMaxYear().toString()
            };
            specification = specification.and(manager
                    .getSpecificationProvider(YEAR_KEY)
                    .getSpecification(range));
        }

        if (param.getProducers() != null && param.getProducers().length > 0) {
            specification = specification.and(manager
                    .getSpecificationProvider(PRODUCER_KEY)
                    .getSpecification(param.getProducers()));
        }
        return specification;
    }
}
