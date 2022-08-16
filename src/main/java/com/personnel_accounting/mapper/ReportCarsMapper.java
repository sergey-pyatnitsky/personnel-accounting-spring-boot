package com.personnel_accounting.mapper;

import com.personnel_accounting.entity.domain.ReportCard;
import com.personnel_accounting.entity.dto.ReportCardDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReportCarsMapper {

    ReportCardDTO toDto(ReportCard reportCard);
    ReportCard toModal(ReportCardDTO reportCardDTO);

    List<ReportCardDTO> toDtoList(List<ReportCard> reportCardList);
    List<ReportCard> toModalList(List<ReportCardDTO> reportCardDTOS);
}
