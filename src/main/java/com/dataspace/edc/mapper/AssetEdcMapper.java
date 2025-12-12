package com.dataspace.edc.mapper;

import com.dataspace.edc.api.AssetRegistrationRequest;
import com.dataspace.edc.dto.AssetDto;
import com.dataspace.edc.dto.DataAddressDto;
import com.dataspace.edc.dto.PropertiesDto;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class AssetEdcMapper {

    public AssetDto toEdcAsset(String assetId, AssetRegistrationRequest req) {
        AssetDto asset = new AssetDto();
        asset.setId(assetId);

        PropertiesDto props = new PropertiesDto();
        props.setName(req.getName());
        props.setDescription(req.getDescription());
        props.setContenttype(req.getContentType());
        props.setCreatedAt(Instant.now().toString());

        if (req.getAccessPolicy() != null) {
            props.setAllowedCompanies(req.getAccessPolicy().getAllowedCompanies());
            props.setUsagePurpose(req.getAccessPolicy().getUsagePurpose());
        }

        asset.setProperties(props);

        DataAddressDto da = new DataAddressDto();
        da.setType(req.getDataAddress().getType());
        da.setName(req.getName());
        da.setBaseUrl(req.getDataAddress().getBaseUrl());
        da.setProxyPath("true");
        asset.setDataAddress(da);

        return asset;
    }
}
