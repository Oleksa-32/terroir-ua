package com.example.backend.controller;

import com.example.backend.dto.wine.CreateWineRequestDto;
import com.example.backend.dto.wine.UpdateWineRequestDto;
import com.example.backend.dto.wine.WineDto;
import com.example.backend.dto.wine.WineItemDto;
import com.example.backend.dto.wine.WineSearchParametersDto;
import com.example.backend.service.wine.WineService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/wines")
@RequiredArgsConstructor
public class WineController {
    private final WineService wineService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('MANAGER')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a new wine",
            description = "Create a new wine along with its image"
    )
    public WineDto save(
            @RequestPart("wine") @Valid CreateWineRequestDto requestDto,
            @RequestPart("image") MultipartFile image
    ) throws IOException {
        return wineService.save(requestDto, image);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get wine by ID",
            description = "Retrieve details of a specific wine by its ID"
    )
    public WineDto getWineById(@PathVariable Long id) {
        return wineService.getWineById(id);
    }

    @GetMapping("/{id}/recommendations")
    @Operation(
            summary = "Get wine recommendations",
            description = "Retrieve recommended wines related to the given wine ID"
    )
    public List<WineItemDto> recommend(@PathVariable Long id) {
        return wineService.findRecommendations(id);
    }

    @GetMapping
    @Operation(
            summary = "List all wines",
            description = "Retrieve a paginated list of all wines"
    )
    public Page<WineDto> findAll(Pageable pageable) {
        return wineService.findAll(pageable);
    }

    @PutMapping(
            value = "/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @PreAuthorize("hasRole('MANAGER')")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Update wine",
            description = "Update the details of an existing wine by its ID,"
                    + " and optionally replace its image"
    )
    public WineDto updateWine(
            @PathVariable Long id,
            @RequestPart("wine") @Valid UpdateWineRequestDto updateWineRequestDto,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {
        return wineService.updateWine(id, updateWineRequestDto, image);
    }

    @GetMapping("/items")
    @Operation(
            summary = "List wine items",
            description = "Retrieve a paginated list of wine items"
    )
    public Page<WineItemDto> findItems(Pageable pageable) {
        return wineService.findItems(pageable);
    }

    @GetMapping("/search")
    @Operation(
            summary = "Search wines",
            description = "Search for wines using filter criteria and pagination"
    )
    public Page<WineDto> search(
            WineSearchParametersDto parametersDto,
            Pageable pageable
    ) {
        return wineService.search(parametersDto, pageable);
    }

    @GetMapping("/recent")
    @Operation(
            summary = "List recent wines",
            description = "Retrieve a paginated list of the most recently added wines"
    )
    public Page<WineDto> recent(Pageable pageable) {
        return wineService.findRecent(pageable);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete wine",
            description = "Delete a wine by its ID"
    )
    public void deleteWine(@PathVariable Long id) {
        wineService.deleteWine(id);
    }
}
