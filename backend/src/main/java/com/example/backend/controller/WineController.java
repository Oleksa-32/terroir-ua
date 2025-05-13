package com.example.backend.controller;

import com.example.backend.dto.wine.CreateWineRequestDto;
import com.example.backend.dto.wine.UpdateWineRequestDto;
import com.example.backend.dto.wine.WineDto;
import com.example.backend.dto.wine.WineItemDto;
import com.example.backend.service.wine.WineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wines")
@RequiredArgsConstructor
public class WineController {
    private final WineService wineService;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    @ResponseStatus(HttpStatus.CREATED)
    public WineDto save(@RequestBody @Valid CreateWineRequestDto requestDto) {
        return wineService.save(requestDto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'CUSTOMER')")
    public WineDto getWineById(@PathVariable Long id) {
        return wineService.getWineById(id);
    }

    @GetMapping
    public Page<WineDto> findAll(Pageable pageable) {
        return wineService.findAll(pageable);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public WineDto updateWine(
            @PathVariable Long id,
            @RequestBody @Valid UpdateWineRequestDto updateWineRequestDto) {
        return wineService.updateWine(id, updateWineRequestDto);
    }

    @GetMapping("/items")
    public Page<WineItemDto> findItems(Pageable pageable) {
        return wineService.findItems(pageable);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWine(@PathVariable Long id) {
        wineService.deleteWine(id);
    }

}
