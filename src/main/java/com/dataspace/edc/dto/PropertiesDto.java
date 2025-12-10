package com.dataspace.edc.dto;

import lombok.Data;

import java.util.List;

@Data
public class PropertiesDto {
    private String name;
    private String description;
    private String contenttype;
    private List<String> allowedCompanies;
    private String usagePurpose;
    private String createdAt;

}
