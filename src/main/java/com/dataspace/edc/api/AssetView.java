package com.dataspace.edc.api;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AssetView {

    private String assetId;
    private String name;
    private String description;
    private String contentType;
    private PolicyView policy;
    private String createdAt; // ISO-8601 string, nullable

    @Data
    @AllArgsConstructor
    public static class PolicyView {
        private String type;                // e.g. "restricted" / "unrestricted"
        private List<String> allowedCompanies;
    }
}

