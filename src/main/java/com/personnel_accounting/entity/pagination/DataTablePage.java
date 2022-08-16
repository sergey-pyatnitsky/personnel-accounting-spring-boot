package com.personnel_accounting.entity.pagination;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DataTablePage<T> {

    public DataTablePage(List<T> data) {
        this.data = data;
    }

    private List<T> data;
    private int recordsFiltered;
    private Long recordsTotal;
    private int draw;

}
