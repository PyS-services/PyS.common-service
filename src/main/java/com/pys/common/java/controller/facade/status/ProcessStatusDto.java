package com.pys.common.java.controller.facade.status;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProcessStatusDto {

    private String status;
    private int progress;

}
