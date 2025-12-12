package com.dataspace.edc.service;

import com.dataspace.edc.api.HealthResponse;

import com.dataspace.edc.dto.QuerySpecDto;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
@Slf4j
@Service
@RequiredArgsConstructor
public class HealthService {

    private final EdcClientService edcClient;

    public HealthResponse getHealth() {

        boolean mgmtUp = edcClient.isManagementApiUp();

        long totalAssets = 0;
        try {
            QuerySpecDto spec = new QuerySpecDto();
            spec.setOffset(0);
            spec.setLimit(1000);
            JsonNode assets = edcClient.queryAssets(spec);
            if (assets != null && assets.isArray()) {
                totalAssets = assets.size();
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

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
            log.error(ex.getMessage(), ex);
        }

        Map<String, String> checks = new HashMap<>();
        checks.put("edcManagementApi", mgmtUp ? "up" : "down");
        checks.put("policyStore", totalPolicies >= 0 ? "up" : "unknown");
        checks.put("catalogCache", "up");

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

