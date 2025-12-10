package com.dataspace.edc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PolicyDefinitionDto {

    @JsonProperty("@context")
    private PolicyContextDto context = new PolicyContextDto();

    @JsonProperty("@id")
    private String id;  // e.g. "policy-<assetId>"

    private PolicyDto policy;
}
