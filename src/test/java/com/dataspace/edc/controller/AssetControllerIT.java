package com.dataspace.edc.controller;

import com.dataspace.edc.dto.AssetDto;
import com.dataspace.edc.dto.ContractDefinitionDto;
import com.dataspace.edc.dto.PolicyDefinitionDto;
import com.dataspace.edc.service.EdcClientService;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AssetControllerIT {

    @Autowired
    private MockMvc mockMvc;

    // Mock the network-facing component (EDC Management API client)
    @MockBean
    private EdcClientService edcClientService;

    @Test
    void registerAsset_shouldReturn201_andRegisterAssetPolicyContract() throws Exception {
        // Arrange: stub EDC client responses
        AssetDto createdAsset = new AssetDto();
        createdAsset.setId("asset-123");

        PolicyDefinitionDto createdPolicy = new PolicyDefinitionDto();
        createdPolicy.setId("policy-asset-123");

        when(edcClientService.createAsset(any(AssetDto.class))).thenReturn(createdAsset);
        when(edcClientService.createPolicyDefinition(any(PolicyDefinitionDto.class))).thenReturn(createdPolicy);
        when(edcClientService.createContractDefinition(any(ContractDefinitionDto.class)))
                .thenReturn(new ContractDefinitionDto()); // return value unused by service

        String body = """
            {
              "name": "Tata Telematics Data",
              "description": "Real-time vehicle sensor data",
              "contentType": "application/json",
              "dataAddress": {
                "type": "HttpData",
                "baseUrl": "https://supplier.example.com/api/telematics"
              },
              "accessPolicy": {
                "allowedCompanies": ["BPNL000000000001","BPNL000000000002"],
                "usagePurpose": "quality.analysis"
              }
            }
            """;

        // Act + Assert
        mockMvc.perform(post("/api/v1/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.assetId").value("asset-123"))
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.catalogUrl").exists())
                .andExpect(jsonPath("$.message").exists());

        // Verify EDC calls happened (and in order)
        InOrder inOrder = inOrder(edcClientService);
        inOrder.verify(edcClientService).createAsset(any(AssetDto.class));
        inOrder.verify(edcClientService).createPolicyDefinition(any(PolicyDefinitionDto.class));
        inOrder.verify(edcClientService).createContractDefinition(any(ContractDefinitionDto.class));

        verifyNoMoreInteractions(edcClientService);
    }

    @Test
    void registerAsset_missingDataAddress_shouldReturn400() throws Exception {
        // Missing dataAddress -> should fail in validateRequest()
        String body = """
            {
              "name": "Bad Request",
              "description": "Missing dataAddress",
              "contentType": "application/json"
            }
            """;

        mockMvc.perform(post("/api/v1/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        // EDC should not be called
        verifyNoInteractions(edcClientService);
    }
}
