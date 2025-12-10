package com.dataspace.edc.api;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class HealthResponse {

    private String status;          // e.g. "healthy", "degraded"
    private String timestamp;       // ISO-8601 string
    private Map<String, String> checks;
    private Statistics statistics;

    @Data
    @AllArgsConstructor
    public static class Statistics {
        private long totalAssets;
        private long totalPolicies;
    }
}

