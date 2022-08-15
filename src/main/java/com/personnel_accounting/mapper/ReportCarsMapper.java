package com.personnel_accounting.mapper;

import com.personnel_accounting.entity.domain.ReportCard;
import com.personnel_accounting.entity.dto.ReportCardDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReportCarsMapper {

    ReportCardDTO reportCarsToReportCartDto(ReportCard reportCard);
    ReportCard reportCardDtoToReportCard(ReportCardDTO reportCardDTO);

    List<ReportCardDTO> reportCardListToReportCardDtoList(List<ReportCard> reportCardList);
    List<ReportCard> reportCardDtoListToReportCardList(List<ReportCardDTO> reportCardDTOS);
}
