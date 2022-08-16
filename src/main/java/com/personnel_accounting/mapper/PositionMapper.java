package com.personnel_accounting.mapper;

import com.personnel_accounting.entity.domain.Position;
import com.personnel_accounting.entity.dto.PositionDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PositionMapper {

    PositionDTO toDto(Position position);
    Position toModal(PositionDTO positionDTO);

    List<PositionDTO> toDtoList(List<Position> positions);
    List<Position> toModalList(List<PositionDTO> positionDTOS);
}
