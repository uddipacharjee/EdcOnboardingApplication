package com.dataspace.edc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PolicyDto {

    @JsonProperty("@type")
    private String type = "odrl:Set";

    private List<PermissionDto> permission;
}

