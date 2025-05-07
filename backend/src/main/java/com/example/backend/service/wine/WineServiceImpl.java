package com.example.backend.service.wine;

import com.example.backend.dto.wine.CreateWineRequestDto;
import com.example.backend.dto.wine.UpdateWineRequestDto;
import com.example.backend.dto.wine.WineDto;
import com.example.backend.mapper.WineMapper;
import com.example.backend.model.Wine;
import com.example.backend.repository.WineRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WineServiceImpl implements WineService {
    private final WineRepository wineRepository;
    private final WineMapper wineMapper;

    @Override
    public WineDto save(CreateWineRequestDto requestDto) {
        Wine wine = wineMapper.toModel(requestDto);
        return wineMapper.toDto(wineRepository.save(wine));
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
    public void deleteWine(Long id) {
        if (!wineRepository.existsById(id)) {
            throw new EntityNotFoundException("Wine with id " + id + "not found");
        }
        wineRepository.deleteById(id);
    }
}
