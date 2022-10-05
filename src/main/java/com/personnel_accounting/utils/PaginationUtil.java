package com.personnel_accounting.utils;

import com.personnel_accounting.entity.enums.DirectionEnum;
import com.personnel_accounting.entity.pagination.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public class PaginationUtil {

    public static Pageable convertRequestToPageable(DataTablePagingRequest dataTablePagingRequest) {
        Order order = dataTablePagingRequest.getOrder().get(0);
        Column column = dataTablePagingRequest.getColumns().get(order.getColumn());

        return PageRequest.of(
                dataTablePagingRequest.getStart()/dataTablePagingRequest.getLength(),
                dataTablePagingRequest.getLength(),
                getSort(column.getData(), order.getDir()));

    }

    public static DataTablePage<?> getPageFromResponse(Page<?> page, int draw){
        DataTablePage<?> dataTablePage = new DataTablePage<>(page.getContent());
        dataTablePage.setRecordsFiltered((int) page.getTotalElements());
        dataTablePage.setDraw(draw);
        dataTablePage.setRecordsTotal(page.getTotalElements());
        return dataTablePage;
    }

    public static DataTablePage<?> getPageFromListResponse(List<?> page, int draw){
        DataTablePage<?> dataTablePage = new DataTablePage<>(page);
        dataTablePage.setDraw(draw);
        return dataTablePage;
    }

    public static Sort getSortFromRequest(DataTablePagingRequest dataTablePagingRequest){
        Order order = dataTablePagingRequest.getOrder().get(0);
        Column column = dataTablePagingRequest.getColumns().get(order.getColumn());

        return Sort.by(getSortDirection(order.getDir()), column.getData());
    }

    private static Sort getSort(String column, DirectionEnum directionEnum) {
        return directionEnum.equals(DirectionEnum.asc)
                ? Sort.by(column).ascending()
                : Sort.by(column).descending();
    }

    private static Sort.Direction getSortDirection(DirectionEnum directionEnum) {
        return directionEnum.equals(DirectionEnum.asc)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
    }
}
