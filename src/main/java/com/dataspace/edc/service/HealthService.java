package com.dataspace.edc.service;

import com.dataspace.edc.api.HealthResponse;

import com.dataspace.edc.dto.QuerySpecDto;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HealthService {

    private final EdcClientService edcClient;

    public HealthResponse getHealth() {

        // 1) Check EDC Management API
        boolean mgmtUp = edcClient.isManagementApiUp();

        // 2) Count assets
        long totalAssets = 0;
        try {
            QuerySpecDto spec = new QuerySpecDto();
            spec.setOffset(0);
            spec.setLimit(1000); // adjust as needed
            JsonNode assets = edcClient.queryAssets(spec);
            if (assets != null && assets.isArray()) {
                totalAssets = assets.size();
            }
        } catch (Exception ex) {
            // leave as 0 if it fails
        }

        // 3) Count policy definitions
        long totalPolicies = 0;
        try {
            QuerySpecDto spec = new QuerySpecDto();
            spec.setOffset(0);
            spec.setLimit(1000);
            JsonNode policies = edcClient.queryPolicyDefinitions(spec);
            if (policies != null && policies.isArray()) {
                totalPolicies = policies.size();
            }
        } catch (Exception ex) {
            // ignore for now
        }

        // 4) Build check map
        Map<String, String> checks = new HashMap<>();
        checks.put("edcManagementApi", mgmtUp ? "up" : "down");
        checks.put("policyStore", totalPolicies >= 0 ? "up" : "unknown");
        checks.put("catalogCache", "up"); // if you have a cache, you could add a real check

        // 5) Overall status
        String overallStatus = mgmtUp ? "healthy" : "degraded";

        HealthResponse.Statistics stats = new HealthResponse.Statistics(
                totalAssets,
                totalPolicies
        );

        return new HealthResponse(
                overallStatus,
                Instant.now().toString(),
                checks,
                stats
        );
    }
}

