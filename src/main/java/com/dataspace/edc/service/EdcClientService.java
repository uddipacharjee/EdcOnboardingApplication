package com.dataspace.edc.service;

import com.dataspace.edc.dto.AssetDto;
import com.dataspace.edc.dto.ContractDefinitionDto;
import com.dataspace.edc.dto.PolicyDefinitionDto;
import com.dataspace.edc.dto.QuerySpecDto;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;

@Service
@RequiredArgsConstructor
public class EdcClientService {

    private final RestClient edcProviderRestClient;

    public AssetDto createAsset(AssetDto assetDto) {
        return edcProviderRestClient.post()
                .uri("/assets")
                .contentType(MediaType.APPLICATION_JSON)
                .body(assetDto)
                .retrieve()
                .body(AssetDto.class);
    }

    public PolicyDefinitionDto createPolicyDefinition(PolicyDefinitionDto policyDefinitionDto) {
        return edcProviderRestClient.post()
                .uri("/policydefinitions")
                .contentType(MediaType.APPLICATION_JSON)
                .body(policyDefinitionDto)
                .retrieve()
                .body(PolicyDefinitionDto.class);
    }

    public ContractDefinitionDto createContractDefinition(ContractDefinitionDto contractDefinitionDto) {
        return edcProviderRestClient.post()
                .uri("/contractdefinitions")
                .contentType(MediaType.APPLICATION_JSON)
                .body(contractDefinitionDto)
                .retrieve()
                .body(ContractDefinitionDto.class);
    }

    public JsonNode queryAssets(QuerySpecDto querySpec) {
        return edcProviderRestClient.post()
                .uri("/assets/request")
                .contentType(MediaType.APPLICATION_JSON)
                .body(querySpec)
                .retrieve()
                .body(JsonNode.class);
    }

    public JsonNode getAssetById(String assetId) {
        return edcProviderRestClient.get()
                .uri("/assets/{id}", assetId)
                .retrieve()
                .body(JsonNode.class);
    }

    public JsonNode queryPolicyDefinitions(QuerySpecDto querySpec) {
        return edcProviderRestClient.post()
                .uri("/policydefinitions/request")
                .contentType(MediaType.APPLICATION_JSON)
                .body(querySpec)
                .retrieve()
                .body(JsonNode.class);
    }


    public boolean isManagementApiUp() {
        try {
            // cheapest "ping": small QuerySpec (limit 1)
            QuerySpecDto spec = new QuerySpecDto();
            spec.setLimit(1);
            queryAssets(spec);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}

