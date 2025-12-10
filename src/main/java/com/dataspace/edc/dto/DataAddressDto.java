package com.dataspace.edc.dto;

import lombok.Data;

@Data
public class DataAddressDto {
    private String type;      // "HttpData"
    private String name;
    private String baseUrl;
    private String proxyPath;
}
