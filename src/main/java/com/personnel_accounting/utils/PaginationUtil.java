package com.personnel_accounting.utils;

import com.personnel_accounting.entity.pagination.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PaginationUtil {

    public static Pageable convertRequestToPageable(DataTablePagingRequest dataTablePagingRequest) {
        Order order = dataTablePagingRequest.getOrder().get(0);
        Column column = dataTablePagingRequest.getColumns().get(order.getColumn());

        return PageRequest.of(
                dataTablePagingRequest.getStart(),
                dataTablePagingRequest.getLength(),
                getSort(column.getData(), order.getDir()));

    }

    public static DataTablePage<?> getPageFromResponse(Page<?> page){
        DataTablePage<?> dataTablePage = new DataTablePage<>(page.getContent());
        dataTablePage.setRecordsFiltered(page.getNumberOfElements());
        dataTablePage.setDraw(page.getNumber());
        dataTablePage.setRecordsTotal(page.getTotalElements());
        return dataTablePage;
    }

    private static Sort getSort(String column, Direction direction) {
        return direction.equals(Direction.asc)
                ? Sort.by(column).ascending()
                : Sort.by(column).descending();
    }
}
