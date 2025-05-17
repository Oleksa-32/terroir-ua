package com.example.backend.mapper;

import com.example.backend.config.MapperConfig;
import com.example.backend.dto.wine.CreateWineRequestDto;
import com.example.backend.dto.wine.UpdateWineRequestDto;
import com.example.backend.dto.wine.WineDto;
import com.example.backend.dto.wine.WineItemDto;
import com.example.backend.model.Types;
import com.example.backend.model.Wine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface WineMapper {
    WineDto toDto(Wine wine);

    Wine toModel(CreateWineRequestDto requestDto);

    @Mapping(target = "id", ignore = true)
    void updateWineFromDto(UpdateWineRequestDto requestDto, @MappingTarget Wine wine);

    WineItemDto toItem(Wine wine);

    default Types map(String label) {
        return label == null
                ? null
                : Types.fromLabel(label);
    }
}
