package com.dataspace.edc.dto;

import lombok.Data;

// com.dataspace.edc.edc.dto.PermissionDto
@Data
public class PermissionDto {
    private String target;      // assetId
    private String action;      // "USE"
    private ConstraintDto constraint; // optional
}


