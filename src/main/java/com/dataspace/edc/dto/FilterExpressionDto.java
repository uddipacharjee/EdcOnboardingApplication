package com.dataspace.edc.dto;

import lombok.Data;

@Data
public class FilterExpressionDto {
    private String operandLeft;
    private String operator;
    private String operandRight;
}
