package com.dataspace.edc.dto;
// src/main/java/com/example/onboarding/edc/dto/ContextDto.java

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ContextDto {

    @JsonProperty("@vocab")
    private String vocab = "https://w3id.org/edc/v0.0.1/ns/";
}

