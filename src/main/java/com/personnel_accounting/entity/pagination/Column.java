package com.personnel_accounting.entity.pagination;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class Column {

    private String data;
    private String name;
    private Boolean searchable;
    private Boolean orderable;
    private Search search;

    public Column(String data) {
        this.data = data;
    }

    public Column(String data, Boolean searchable, Boolean orderable, Search search) {
        this.data = data;
        this.searchable = searchable;
        this.orderable = orderable;
        this.search = search;
    }
}
