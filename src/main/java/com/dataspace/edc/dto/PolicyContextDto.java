package com.dataspace.edc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PolicyContextDto {

    @JsonProperty("edc")
    private String edc = "https://w3id.org/edc/v0.0.1/ns/";

    @JsonProperty("odrl")
    private String odrl = "http://www.w3.org/ns/odrl/2/";
}

