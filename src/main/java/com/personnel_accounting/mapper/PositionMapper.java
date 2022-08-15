package com.personnel_accounting.mapper;

import com.personnel_accounting.entity.domain.Position;
import com.personnel_accounting.entity.dto.PositionDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PositionMapper {

    PositionDTO positionToPositionDto(Position position);
    Position positionDtoToPosition(PositionDTO positionDTO);

    List<PositionDTO> positionListToPositionDtoList(List<Position> positions);
    List<Position> positionDtoListToPositionList(List<PositionDTO> positionDTOS);
}
