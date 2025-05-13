package com.example.backend.service.wine;

import com.example.backend.dto.wine.CreateWineRequestDto;
import com.example.backend.dto.wine.UpdateWineRequestDto;
import com.example.backend.dto.wine.WineDto;
import com.example.backend.dto.wine.WineItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WineService {
    WineDto save(CreateWineRequestDto requestDto);

    WineDto getWineById(Long id);

    Page<WineDto> findAll(Pageable pageable);

    WineDto updateWine(Long id, UpdateWineRequestDto updateWineRequestDto);

    Page<WineItemDto> findItems(Pageable pageable);

    void deleteWine(Long id);
}
