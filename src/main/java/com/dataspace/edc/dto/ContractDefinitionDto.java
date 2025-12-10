package com.dataspace.edc.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ContractDefinitionDto {

    @JsonProperty("@context")
    private ContextDto context = new ContextDto();

    @JsonProperty("@id")
    private String id; // e.g. "contract-<assetId>"

    private String accessPolicyId;
    private String contractPolicyId;

    // Simple selector: match assetId
    private List<CriterionDto> assetsSelector;
}
