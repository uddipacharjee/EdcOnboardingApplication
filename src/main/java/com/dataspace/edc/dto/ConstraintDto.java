package com.dataspace.edc.dto;

import lombok.Data;

// com.dataspace.edc.edc.dto.ConstraintDto
@Data
public class ConstraintDto {
    private String leftOperand;   // e.g. "BusinessPartnerNumber"
    private String operator;      // e.g. "in"
    private Object rightOperand;  // list or string
}
