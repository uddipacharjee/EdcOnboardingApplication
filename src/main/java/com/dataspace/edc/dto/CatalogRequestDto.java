package com.dataspace.edc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class CatalogRequestDto {

    @JsonProperty("@context")
    private Map<String, Object> context = Map.of(
            "edc", "https://w3id.org/edc/v0.0.1/ns/"
    );

    @JsonProperty("@type")
    private String type = "CatalogRequest";

    private String counterPartyAddress;

    // protocol name (for DSP HTTP)
    private String protocol;

    private QuerySpecDto querySpec;
}
