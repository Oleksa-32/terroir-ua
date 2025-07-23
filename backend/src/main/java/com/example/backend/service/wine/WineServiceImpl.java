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
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class WineServiceImpl implements WineService {
    private final WineRepository wineRepository;
    private final WineMapper wineMapper;
    private final SpecificationBuilder<Wine> specificationBuilder;
    private final WineRecommendationService recommendationService;

    @Value("${file.upload-dir}")
    private String uploadDir;

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
    public WineDto updateWine(Long id,
                              UpdateWineRequestDto dto,
                              MultipartFile image) throws IOException {
        Wine wine = wineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Wine with id " + id
                        + " not found"));

        if (image != null && !image.isEmpty()) {
            String ext = StringUtils.getFilenameExtension(image.getOriginalFilename());
            String filename = UUID.randomUUID() + (ext != null ? "." + ext : "");
            Path target = Paths.get(uploadDir).resolve(filename);
            Files.createDirectories(target.getParent());
            try (InputStream in = image.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }
            wine.setImageUrl("/images/" + filename);
        }
        wineMapper.updateWineFromDto(dto, wine);

        Wine updated = wineRepository.save(wine);
        return wineMapper.toDto(updated);
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

        return recommendationService.recommend(base).stream()
                .map(wineMapper::toItem)
                .collect(Collectors.toList());
    }

    @Override
    public Page<WineDto> findRecent(Pageable pageable) {
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        return wineRepository
                .findAllByDateAddedAfter(weekAgo, pageable)
                .map(wineMapper::toDto);
    }

    @Override
    public void deleteWine(Long id) {
        if (!wineRepository.existsById(id)) {
            throw new EntityNotFoundException("Wine with id " + id + "not found");
        }
        wineRepository.deleteById(id);
    }
}
