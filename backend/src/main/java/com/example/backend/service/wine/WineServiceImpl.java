package com.example.backend.service.wine;

import com.example.backend.dto.wine.CreateWineRequestDto;
import com.example.backend.dto.wine.UpdateWineRequestDto;
import com.example.backend.dto.wine.WineDto;
import com.example.backend.dto.wine.WineItemDto;
import com.example.backend.dto.wine.WineSearchParametersDto;
import com.example.backend.mapper.WineMapper;
import com.example.backend.model.Wine;
import com.example.backend.repository.SpecificationBuilder;
import com.example.backend.repository.WineRepository;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class WineServiceImpl implements WineService {
    private static final int MAX_TOTAL = 30;
    private static final int MAX_PER_STAGE = 10;
    private final WineRepository wineRepository;
    private final WineMapper wineMapper;
    private final SpecificationBuilder<Wine> specificationBuilder;

    private final SpecificationBuilder<Wine> specBuilder;

    @Value("${file.upload-dir}")
    private String uploadDir;

    private void collectStage(WineSearchParametersDto dto, Wine base, List<Wine> acc) {
        int remaining = MAX_TOTAL - acc.size();
        if (remaining <= 0) {
            return;
        }

        int pageSize = Math.min(MAX_PER_STAGE, remaining);
        Page<Wine> page = wineRepository.findAll(
                specBuilder.build(dto),
                PageRequest.of(0, pageSize)
        );

        for (Wine w : page.getContent()) {
            if (w.getId().equals(base.getId())) {
                continue;
            }
            boolean already = acc.stream()
                    .anyMatch(x -> x.getId().equals(w.getId()));
            if (already) {
                continue;
            }

            acc.add(w);
            if (acc.size() >= MAX_TOTAL) {
                break;
            }
        }
    }

    private WineSearchParametersDto buildExactDto(Wine base) {
        WineSearchParametersDto dto = new WineSearchParametersDto();
        dto.setTypes(new String[]{ base.getType().getLabel() });
        dto.setProducers(new String[]{ base.getProducer() });
        dto.setMinYear(base.getYear());
        dto.setMaxYear(base.getYear());
        return dto;
    }

    private WineSearchParametersDto buildYearRangeDto(Wine base) {
        WineSearchParametersDto dto = new WineSearchParametersDto();
        dto.setTypes(new String[]{ base.getType().getLabel() });
        dto.setProducers(new String[]{ base.getProducer() });
        dto.setMinYear(base.getYear() - 5);
        dto.setMaxYear(base.getYear() + 5);
        return dto;
    }

    private WineSearchParametersDto buildTypeProducerDto(Wine base) {
        WineSearchParametersDto dto = new WineSearchParametersDto();
        dto.setTypes(new String[]{ base.getType().getLabel() });
        dto.setProducers(new String[]{ base.getProducer() });
        return dto;
    }

    private WineSearchParametersDto buildTypeOnlyDto(Wine base) {
        WineSearchParametersDto dto = new WineSearchParametersDto();
        dto.setTypes(new String[]{ base.getType().getLabel() });
        return dto;
    }

    @Override
    public WineDto save(CreateWineRequestDto requestDto, MultipartFile image) throws IOException {
        String ext = StringUtils.getFilenameExtension(image.getOriginalFilename());
        String filename = UUID.randomUUID() + (ext != null ? "." + ext : "");
        Path target = Paths.get(uploadDir).resolve(filename);
        Files.createDirectories(target.getParent());
        try (InputStream in = image.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }
        Wine wine = wineMapper.toModel(requestDto);
        wine = wineRepository.save(wine);

        wine.setImageUrl("/images/" + filename);
        wine = wineRepository.save(wine);

        return wineMapper.toDto(wine);
    }

    @Override
    public WineDto getWineById(Long id) {
        Wine wine = wineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Wine with id " + id + "not found"));
        return wineMapper.toDto(wine);
    }

    @Override
    public Page<WineDto> findAll(Pageable pageable) {
        return wineRepository.findAll(pageable)
                .map(wineMapper::toDto);
    }

    @Override
    public WineDto updateWine(Long id, UpdateWineRequestDto updateWineRequestDto) {
        Wine existingWine = wineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Wine with id " + id + "not found"));
        wineMapper.updateWineFromDto(updateWineRequestDto, existingWine);
        return wineMapper.toDto(wineRepository.save(existingWine));
    }

    @Override
    public Page<WineItemDto> findItems(Pageable pageable) {
        return wineRepository.findAll(pageable).map(wineMapper::toItem);
    }

    @Override
    public Page<WineDto> search(WineSearchParametersDto searchParametersDto, Pageable pageable) {
        Specification<Wine> specification = specificationBuilder.build(searchParametersDto);
        return wineRepository.findAll(specification, pageable)
                .map(wineMapper::toDto);
    }

    @Override
    public List<WineItemDto> findRecommendations(Long id) {
        Wine base = wineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Wine not found: " + id));
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

        return picked.stream()
                .map(wineMapper::toItem)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteWine(Long id) {
        if (!wineRepository.existsById(id)) {
            throw new EntityNotFoundException("Wine with id " + id + "not found");
        }
        wineRepository.deleteById(id);
    }
}
