package com.dataspace.edc.service;

import com.dataspace.edc.api.AssetListResponse;
import com.dataspace.edc.api.AssetView;
import com.dataspace.edc.dto.QuerySpecDto;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AssetDiscoveryService {

    private final EdcClientService edcClient;

    public AssetListResponse listAssets(int page, int size) {
        if (page < 0) page = 0;
        if (size <= 0) size = 20;

        int offset = page * size;

        QuerySpecDto spec = new QuerySpecDto();
        spec.setOffset(offset);
        spec.setLimit(size);

        JsonNode result = edcClient.queryAssets(spec);

        List<AssetView> assets = new ArrayList<>();

        if (result != null && result.isArray()) {
            for (JsonNode assetNode : result) {
                assets.add(mapAssetNode(assetNode));
            }
        }

        int count = assets.size();
        boolean hasNext = (count == size);

        return new AssetListResponse(assets, page, size, count, hasNext);
    }

    public AssetView getAsset(String assetId) {
        JsonNode node = edcClient.getAssetById(assetId);
        if (node == null || node.isMissingNode() || node.isNull()) {
            throw new NoSuchElementException("Asset not found: " + assetId);
        }
        return mapAssetNode(node);
    }

    private AssetView mapAssetNode(JsonNode assetNode) {
        String assetId = assetNode.path("@id").asText(null);
        JsonNode props = assetNode.path("properties");

        String name = props.path("name").asText(null);
        String description = props.path("description").asText(null);
        String contentType = props.path("contenttype").asText(null);

        String createdAt = null;
        if (assetNode.has("createdAt")) {
            long epoch = assetNode.path("createdAt").asLong(-1);
            if (epoch > 0) {
                createdAt = Instant.ofEpochMilli(epoch).toString();
            }
        }

        List<String> allowedCompanies = new ArrayList<>();
        if (props.has("allowedCompanies") && props.get("allowedCompanies").isArray()) {
            for (JsonNode bpn : props.get("allowedCompanies")) {
                allowedCompanies.add(bpn.asText());
            }
        }
        String policyType = allowedCompanies.isEmpty() ? "unrestricted" : "restricted";
        AssetView.PolicyView policyView = new AssetView.PolicyView(policyType, allowedCompanies);

        return new AssetView(
                assetId,
                name,
                description,
                contentType,
                policyView,
                createdAt
        );
    }
}
