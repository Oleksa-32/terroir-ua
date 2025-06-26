package com.example.backend.service.wine;

import com.example.backend.dto.wine.WineSearchParametersDto;
import com.example.backend.model.Wine;
import com.example.backend.repository.SpecificationBuilder;
import com.example.backend.repository.WineRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WineRecommendationService {
    private static final int MAX_TOTAL = 30;
    private static final int MAX_PER_STAGE = 10;
    private final WineRepository wineRepository;
    private final SpecificationBuilder<Wine> specBuilder;

    public List<Wine> recommend(Wine base) {
        List<Wine> picked = new ArrayList<>();
        for (WineSearchParametersDto stageDto : List.of(
                buildExactDto(base),
                buildTypeProducerDto(base),
                buildYearRangeDto(base),
                buildTypeOnlyDto(base),
                new WineSearchParametersDto()
        )) {
            if (picked.size() >= MAX_TOTAL) {
                break;
            }
            collectStage(stageDto, base, picked);
        }
        return picked;
    }

    private void collectStage(WineSearchParametersDto dto, Wine base, List<Wine> acc) {
        int remaining = MAX_TOTAL - acc.size();
        if (remaining <= 0) {
            return;
        }
        int pageSize = Math.min(MAX_PER_STAGE, remaining);
        var page = wineRepository.findAll(
                specBuilder.build(dto),
                PageRequest.of(0, pageSize)
        );
        page.getContent().stream()
                .filter(w -> !w.getId().equals(base.getId()))
                .filter(w -> acc.stream().noneMatch(x -> x.getId().equals(w.getId())))
                .forEach(acc::add);
    }

    private WineSearchParametersDto buildExactDto(Wine base) {
        var dto = new WineSearchParametersDto();
        dto.setTypes(new String[]{ base.getType().getLabel() });
        dto.setProducers(new String[]{ base.getProducer() });
        dto.setMinYear(base.getYear());
        dto.setMaxYear(base.getYear());
        return dto;
    }

    private WineSearchParametersDto buildYearRangeDto(Wine base) {
        var dto = new WineSearchParametersDto();
        dto.setTypes(new String[]{ base.getType().getLabel() });
        dto.setProducers(new String[]{ base.getProducer() });
        dto.setMinYear(base.getYear() - 5);
        dto.setMaxYear(base.getYear() + 5);
        return dto;
    }

    private WineSearchParametersDto buildTypeProducerDto(Wine base) {
        var dto = new WineSearchParametersDto();
        dto.setTypes(new String[]{ base.getType().getLabel() });
        dto.setProducers(new String[]{ base.getProducer() });
        return dto;
    }

    private WineSearchParametersDto buildTypeOnlyDto(Wine base) {
        var dto = new WineSearchParametersDto();
        dto.setTypes(new String[]{ base.getType().getLabel() });
        return dto;
    }
}
