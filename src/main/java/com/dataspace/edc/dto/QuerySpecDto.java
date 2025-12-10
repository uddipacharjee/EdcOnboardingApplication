package com.dataspace.edc.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuerySpecDto {

    @JsonProperty("@context")
    private Map<String, Object> context = Map.of(
            "edc", "https://w3id.org/edc/v0.0.1/ns/"
    );

    @JsonProperty("@type")
    private String type = "QuerySpec";

    private Integer offset;
    private Integer limit;

    @JsonProperty("filterExpression")
    private List<CriterionDto> filterExpression;
}

