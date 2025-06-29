package com.example.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.backend.dto.wine.CreateWineRequestDto;
import com.example.backend.dto.wine.WineDto;
import com.example.backend.dto.wine.WineItemDto;
import com.example.backend.mapper.WineMapper;
import com.example.backend.model.Types;
import com.example.backend.model.Wine;
import com.example.backend.repository.WineRepository;
import com.example.backend.service.wine.WineRecommendationService;
import com.example.backend.service.wine.WineServiceImpl;
import com.example.backend.utils.TestDataUtil;
import jakarta.persistence.EntityNotFoundException;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class WineServiceTest {

    @Mock private WineRepository wineRepository;
    @Mock private WineMapper wineMapper;
    @Mock private WineRecommendationService recommendationService;
    @Mock private MultipartFile image;
    @InjectMocks private WineServiceImpl wineService;

    private List<Wine> wineList;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        wineList = TestDataUtil.createWineList();
        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("save() creates new wine with image")
    void save_ValidRequest_ReturnsWineDto() throws Exception {
        CreateWineRequestDto requestDto = TestDataUtil.createWineRequestDto();
        Wine wine = TestDataUtil.createWine(1L, "New Wine", 2020, Types.RED,
                BigDecimal.valueOf(29.99), "New Producer");
        WineDto expectedDto = TestDataUtil.createWineDto(1L);

        when(image.getOriginalFilename()).thenReturn("test.jpg");
        when(image.getInputStream()).thenReturn(new ByteArrayInputStream("test".getBytes()));
        when(wineMapper.toModel(requestDto)).thenReturn(wine);
        when(wineRepository.save(any(Wine.class))).thenAnswer(invocation -> {
            Wine savedWine = invocation.getArgument(0);
            savedWine.setId(1L);
            savedWine.setImageUrl("/images/test.jpg");
            return savedWine;
        });
        when(wineMapper.toDto(any(Wine.class))).thenReturn(expectedDto);
        ReflectionTestUtils.setField(wineService, "uploadDir",
                System.getProperty("java.io.tmpdir"));
        WineDto result = wineService.save(requestDto, image);
        assertThat(result).isEqualTo(expectedDto);
        verify(wineRepository, times(2)).save(any(Wine.class));
        verify(image).getInputStream();
    }

    @Test
    @DisplayName("getWineById() returns wine when exists")
    void getWineById_ValidId_ReturnsWineDto() {
        Wine wine = wineList.get(0);
        WineDto expectedDto = TestDataUtil.createWineDto(1L);
        when(wineRepository.findById(1L)).thenReturn(Optional.of(wine));
        when(wineMapper.toDto(wine)).thenReturn(expectedDto);
        WineDto result = wineService.getWineById(1L);
        assertThat(result).isEqualTo(expectedDto);
    }

    @Test
    @DisplayName("getWineById() throws exception when not found")
    void getWineById_InvalidId_ThrowsException() {
        when(wineRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> wineService.getWineById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Wine with id 99not found");
    }

    @Test
    @DisplayName("findAll() returns page of wines")
    void findAll_ReturnsPageOfWines() {
        Page<Wine> winePage = new PageImpl<>(wineList, pageable, wineList.size());
        List<WineDto> dtoList = List.of(
                TestDataUtil.createWineDto(1L),
                TestDataUtil.createWineDto(2L),
                TestDataUtil.createWineDto(3L)
        );
        when(wineRepository.findAll(pageable)).thenReturn(winePage);
        when(wineMapper.toDto(wineList.get(0))).thenReturn(dtoList.get(0));
        when(wineMapper.toDto(wineList.get(1))).thenReturn(dtoList.get(1));
        when(wineMapper.toDto(wineList.get(2))).thenReturn(dtoList.get(2));

        Page<WineDto> result = wineService.findAll(pageable);
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent()).isEqualTo(dtoList);
    }

    @Test
    @DisplayName("findRecommendations() returns recommended wines")
    void findRecommendations_ValidId_ReturnsRecommendations() {
        Wine baseWine = wineList.get(0);
        List<Wine> recommendations = List.of(wineList.get(1), wineList.get(2));
        List<WineItemDto> expectedDtos = TestDataUtil.createWineItemDtoList();

        when(wineRepository.findById(1L)).thenReturn(Optional.of(baseWine));
        when(recommendationService.recommend(baseWine)).thenReturn(recommendations);
        when(wineMapper.toItem(recommendations.get(0))).thenReturn(expectedDtos.get(0));
        when(wineMapper.toItem(recommendations.get(1))).thenReturn(expectedDtos.get(1));

        List<WineItemDto> result = wineService.findRecommendations(1L);
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(expectedDtos.subList(0, 2));
    }

    @Test
    @DisplayName("deleteWine() deletes existing wine")
    void deleteWine_ValidId_DeletesWine() {
        when(wineRepository.existsById(1L)).thenReturn(true);
        wineService.deleteWine(1L);
        verify(wineRepository).deleteById(1L);
    }
}
