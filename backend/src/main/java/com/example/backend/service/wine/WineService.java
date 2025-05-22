package com.example.backend.service.wine;

import com.example.backend.dto.wine.CreateWineRequestDto;
import com.example.backend.dto.wine.UpdateWineRequestDto;
import com.example.backend.dto.wine.WineDto;
import com.example.backend.dto.wine.WineItemDto;
import com.example.backend.dto.wine.WineSearchParametersDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface WineService {
    WineDto save(CreateWineRequestDto requestDto, MultipartFile image) throws IOException;

    WineDto getWineById(Long id);

    Page<WineDto> findAll(Pageable pageable);

    WineDto updateWine(Long id, UpdateWineRequestDto updateWineRequestDto);

    Page<WineItemDto> findItems(Pageable pageable);

    Page<WineDto> search(WineSearchParametersDto searchParametersDto, Pageable pageable);

    List<WineItemDto> findRecommendations(Long id);

    void deleteWine(Long id);
}
