package com.dataspace.edc.mapper;

import com.dataspace.edc.api.AssetRegistrationRequest;
import com.dataspace.edc.dto.AssetDto;
import com.dataspace.edc.dto.DataAddressDto;
import com.dataspace.edc.dto.PropertiesDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AssetEdcMapperTest {

    private final AssetEdcMapper mapper = new AssetEdcMapper();

    @Test
    void toEdcAsset_mapsAllFields_andSetsCreatedAt() {
        AssetRegistrationRequest req = new AssetRegistrationRequest();
        req.setName("Tata Telematics Data");
        req.setDescription("Real-time vehicle sensor data");
        req.setContentType("application/json");

        AssetRegistrationRequest.DataAddressInput da = new AssetRegistrationRequest.DataAddressInput();
        da.setType("HttpData");
        da.setBaseUrl("https://supplier.example.com/api/telematics");
        req.setDataAddress(da);

        AssetRegistrationRequest.AccessPolicyInput ap = new AssetRegistrationRequest.AccessPolicyInput();
        ap.setAllowedCompanies(List.of("BPN1", "BPN2"));
        ap.setUsagePurpose("quality.analysis");
        req.setAccessPolicy(ap);

        AssetDto out = mapper.toEdcAsset("asset-123", req);

        assertEquals("asset-123", out.getId());

        PropertiesDto props = out.getProperties();
        assertNotNull(props);
        assertEquals("Tata Telematics Data", props.getName());
        assertEquals("Real-time vehicle sensor data", props.getDescription());
        assertEquals("application/json", props.getContenttype());
        assertNotNull(props.getCreatedAt());
        assertFalse(props.getCreatedAt().isBlank());

        assertEquals(List.of("BPN1", "BPN2"), props.getAllowedCompanies());
        assertEquals("quality.analysis", props.getUsagePurpose());

        DataAddressDto dataAddress = out.getDataAddress();
        assertNotNull(dataAddress);
        assertEquals("HttpData", dataAddress.getType());
        assertEquals("Tata Telematics Data", dataAddress.getName());
        assertEquals("https://supplier.example.com/api/telematics", dataAddress.getBaseUrl());
        assertEquals("true", dataAddress.getProxyPath());
    }

    @Test
    void toEdcAsset_whenAccessPolicyNull_doesNotFail() {
        AssetRegistrationRequest req = new AssetRegistrationRequest();
        req.setName("X");
        req.setDescription("Y");
        req.setContentType("application/json");

        AssetRegistrationRequest.DataAddressInput da = new AssetRegistrationRequest.DataAddressInput();
        da.setType("HttpData");
        da.setBaseUrl("https://example.com");
        req.setDataAddress(da);

        req.setAccessPolicy(null);

        AssetDto out = mapper.toEdcAsset("asset-1", req);

        assertNotNull(out.getProperties());
        assertNull(out.getProperties().getAllowedCompanies());
        assertNull(out.getProperties().getUsagePurpose());
    }
}
