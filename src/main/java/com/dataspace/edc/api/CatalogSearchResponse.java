package com.dataspace.edc.api;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CatalogSearchResponse {

    private int total;
    private List<CatalogAssetItem> items;

    @Data
    @AllArgsConstructor
    public static class CatalogAssetItem {
        private String assetId;
        private String name;
        private String description;
        private String contentType;
    }
}
