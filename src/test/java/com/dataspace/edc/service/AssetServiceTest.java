package com.dataspace.edc.service;

import com.dataspace.edc.api.AssetRegistrationRequest;
import com.dataspace.edc.api.AssetRegistrationResponse;
import com.dataspace.edc.dto.*;
import com.dataspace.edc.mapper.AssetEdcMapper;
import com.dataspace.edc.mapper.OdrlPolicyBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AssetServiceTest {

    private EdcClientService edcClient;
    private AssetEdcMapper assetMapper;
    private OdrlPolicyBuilder policyBuilder;

    private AssetService assetService;

    @BeforeEach
    void setup() {
        edcClient = mock(EdcClientService.class);
        assetMapper = mock(AssetEdcMapper.class);
        policyBuilder = mock(OdrlPolicyBuilder.class);

        assetService = new AssetService(edcClient, assetMapper, policyBuilder);
    }

    @Test
    void register_happyPath_callsEdcInOrder_andReturnsResponse() {
        // Arrange request
        AssetRegistrationRequest req = new AssetRegistrationRequest();
        req.setName("Tata");
        req.setDescription("desc");
        req.setContentType("application/json");

        AssetRegistrationRequest.DataAddressInput da = new AssetRegistrationRequest.DataAddressInput();
        da.setType("HttpData");
        da.setBaseUrl("https://example.com");
        req.setDataAddress(da);

        AssetRegistrationRequest.AccessPolicyInput ap = new AssetRegistrationRequest.AccessPolicyInput();
        ap.setAllowedCompanies(List.of("BPN1", "BPN2"));
        req.setAccessPolicy(ap);

        // Stubs: mapper & builder outputs
        AssetDto mappedAsset = new AssetDto();
        mappedAsset.setId("asset-any"); // service generates real id, but mapper is mocked

        PolicyDefinitionDto builtPolicy = new PolicyDefinitionDto();
        builtPolicy.setId("policy-any");

        when(assetMapper.toEdcAsset(any(String.class), any(AssetRegistrationRequest.class)))
                .thenReturn(mappedAsset);
        when(policyBuilder.buildPolicy(any(String.class), any(String.class), any()))
                .thenReturn(builtPolicy);

        // Stubs: EDC client returns
        AssetDto createdAsset = new AssetDto();
        createdAsset.setId("asset-123");

        PolicyDefinitionDto createdPolicy = new PolicyDefinitionDto();
        createdPolicy.setId("policy-123");

        when(edcClient.createAsset(any(AssetDto.class))).thenReturn(createdAsset);
        when(edcClient.createPolicyDefinition(any(PolicyDefinitionDto.class))).thenReturn(createdPolicy);
        when(edcClient.createContractDefinition(any(ContractDefinitionDto.class))).thenReturn(new ContractDefinitionDto());

        // Act
        AssetRegistrationResponse resp = assetService.register(req);

        // Assert response
        assertNotNull(resp);
        assertEquals("asset-123", resp.getAssetId());
        assertNotNull(resp.getStatus());
        assertNotNull(resp.getCatalogUrl());
        assertNotNull(resp.getMessage());

        // Verify flow: map -> build policy -> create asset -> create policy -> create contract def
        InOrder inOrder = inOrder(assetMapper, policyBuilder, edcClient);
        inOrder.verify(assetMapper).toEdcAsset(any(String.class), eq(req));
        inOrder.verify(policyBuilder).buildPolicy(any(String.class), any(String.class), eq(ap));
        inOrder.verify(edcClient).createAsset(eq(mappedAsset));
        inOrder.verify(edcClient).createPolicyDefinition(eq(builtPolicy));
        inOrder.verify(edcClient).createContractDefinition(any(ContractDefinitionDto.class));

        verifyNoMoreInteractions(edcClient, assetMapper, policyBuilder);
    }

    @Test
    void register_whenDataAddressMissing_throws_andDoesNotCallEdc() {
        AssetRegistrationRequest req = new AssetRegistrationRequest();
        req.setName("X");
        req.setDescription("Y");
        req.setContentType("application/json");
        req.setDataAddress(null); // invalid

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> assetService.register(req));

        assertTrue(ex.getMessage().contains("dataAddress"));

        verifyNoInteractions(edcClient, assetMapper, policyBuilder);
    }

    @Test
    void register_whenDataTypeNotHttpData_throws_andDoesNotCallEdc() {
        AssetRegistrationRequest req = new AssetRegistrationRequest();
        req.setName("X");
        req.setDescription("Y");
        req.setContentType("application/json");

        AssetRegistrationRequest.DataAddressInput da = new AssetRegistrationRequest.DataAddressInput();
        da.setType("BlobStore"); // invalid for your rules
        da.setBaseUrl("https://example.com");
        req.setDataAddress(da);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> assetService.register(req));

        assertTrue(ex.getMessage().contains("HttpData"));

        verifyNoInteractions(edcClient, assetMapper, policyBuilder);
    }

    @Test
    void register_whenAllowedCompaniesDuplicated_throws_andDoesNotCallEdc() {
        AssetRegistrationRequest req = new AssetRegistrationRequest();
        req.setName("X");
        req.setDescription("Y");
        req.setContentType("application/json");

        AssetRegistrationRequest.DataAddressInput da = new AssetRegistrationRequest.DataAddressInput();
        da.setType("HttpData");
        da.setBaseUrl("https://example.com");
        req.setDataAddress(da);

        AssetRegistrationRequest.AccessPolicyInput ap = new AssetRegistrationRequest.AccessPolicyInput();
        ap.setAllowedCompanies(List.of("BPN1", "BPN1")); // duplicate
        req.setAccessPolicy(ap);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> assetService.register(req));

        assertTrue(ex.getMessage().toLowerCase().contains("duplicates"));

        verifyNoInteractions(edcClient, assetMapper, policyBuilder);
    }
}
