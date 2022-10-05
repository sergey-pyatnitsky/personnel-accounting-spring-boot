package com.personnel_accounting.entity.pagination;

import com.personnel_accounting.entity.enums.DirectionEnum;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Order {

    private Integer column;
    private DirectionEnum dir;

}
