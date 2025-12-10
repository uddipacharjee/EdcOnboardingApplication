package com.dataspace.edc.service;

import com.dataspace.edc.api.CatalogSearchResponse;
import com.dataspace.edc.dto.QuerySpecDto;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CatalogSearchService {

    private final EdcClientService edcManagementClient;

    // Simple in-memory cache
    private List<CatalogSearchResponse.CatalogAssetItem> cachedItems = Collections.emptyList();
    private Instant cacheTimestamp = null;

    // TTL for cache (adjust as you like)
    private static final Duration CACHE_TTL = Duration.ofMinutes(5);

    public CatalogSearchResponse search(String keyword) {
        // Normalize keyword (can be null/empty)
        String normalizedKeyword = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);

        // Refresh cache if needed
        refreshCacheIfNeeded();

        // Filter cached items
        List<CatalogSearchResponse.CatalogAssetItem> filtered = new ArrayList<>();
        for (CatalogSearchResponse.CatalogAssetItem item : cachedItems) {
            if (normalizedKeyword.isEmpty()) {
                filtered.add(item);
            } else {
                if ((item.getName() != null && item.getName().toLowerCase(Locale.ROOT).contains(normalizedKeyword)) ||
                        (item.getDescription() != null && item.getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword))) {
                    filtered.add(item);
                }
            }
        }

        return new CatalogSearchResponse(filtered.size(), filtered);
    }

    private void refreshCacheIfNeeded() {
        Instant now = Instant.now();
        if (cacheTimestamp == null || Duration.between(cacheTimestamp, now).compareTo(CACHE_TTL) > 0) {
            // Cache is empty or expired -> refresh from EDC

            QuerySpecDto spec = new QuerySpecDto();
            spec.setOffset(0);
            spec.setLimit(100); // adjust as needed

            JsonNode response = edcManagementClient.queryAssets(spec);

            // /assets/request usually returns an array of assets
            List<CatalogSearchResponse.CatalogAssetItem> items = new ArrayList<>();

            if (response.isArray()) {
                for (JsonNode assetNode : response) {
                    String assetId = assetNode.path("@id").asText(null);
                    JsonNode props = assetNode.path("properties");

                    String name = props.path("name").asText(null);
                    String description = props.path("description").asText(null);
                    String contentType = props.path("contenttype").asText(null);

                    items.add(new CatalogSearchResponse.CatalogAssetItem(assetId, name, description, contentType));
                }
            }

            cachedItems = items;
            cacheTimestamp = now;
        }
    }
}
