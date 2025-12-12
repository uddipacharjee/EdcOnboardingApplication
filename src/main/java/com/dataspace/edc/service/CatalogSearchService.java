package com.dataspace.edc.service;

import com.dataspace.edc.api.CatalogSearchResponse;
import com.dataspace.edc.dto.QuerySpecDto;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class CatalogSearchService {

    private final EdcClientService edcManagementClient;

    private List<CatalogSearchResponse.CatalogAssetItem> cachedItems = Collections.emptyList();
    private Instant cacheTimestamp = null;

    private static final Duration CACHE_TTL = Duration.ofMinutes(5);

    public CatalogSearchResponse search(String keyword) {

        String normalizedKeyword = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);

        refreshCacheIfNeeded();

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

            QuerySpecDto spec = new QuerySpecDto();
            spec.setOffset(0);
            spec.setLimit(100);

            log.info("Refreshing catalog search results");
            JsonNode response = edcManagementClient.queryAssets(spec);

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
