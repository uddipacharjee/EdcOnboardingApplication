package com.dataspace.edc.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AssetDto {

    @JsonProperty("@context")
    private ContextDto context = new ContextDto();

    @JsonProperty("@id")
    private String id;

    private PropertiesDto properties;

    private DataAddressDto dataAddress;
}

