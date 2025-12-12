package com.dataspace.edc.service;

import com.dataspace.edc.api.AssetListResponse;
import com.dataspace.edc.api.AssetView;
import com.dataspace.edc.dto.QuerySpecDto;
import com.dataspace.edc.mapper.AssetNodeMapper;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssetDiscoveryService {

    private final EdcClientService edcClient;
    private final AssetNodeMapper mapper;

    public AssetListResponse listAssets(int page, int size) {
        log.info("listAssets page: {}, size: {}", page, size);
        int safePage = Math.max(0, page);
        int safeSize = size <= 0 ? 20 : Math.min(size, 200); // optional cap
        int offset = safePage * safeSize;

        QuerySpecDto spec = new QuerySpecDto();
        spec.setOffset(offset);
        spec.setLimit(safeSize);

        JsonNode result = edcClient.queryAssets(spec);

        List<AssetView> assets = new ArrayList<>();
        if (result != null && result.isArray()) {
            for (JsonNode node : result) {
                AssetView view = mapper.toView(node);
                if (view != null) assets.add(view);
            }
        }

        boolean hasNext = assets.size() == safeSize;
        return new AssetListResponse(assets, safePage, safeSize, assets.size(), hasNext);
    }

    public AssetView getAsset(String assetId) {
        JsonNode node = edcClient.getAssetById(assetId);
        AssetView view = mapper.toView(node);
        if (view == null || view.getAssetId() == null) {
            throw new NoSuchElementException("Asset not found: " + assetId);
        }
        return view;
    }
}
