package com.dataspace.edc.mapper;

import com.dataspace.edc.api.AssetView;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AssetNodeMapper {

    public AssetView toView(JsonNode assetNode) {
        if (assetNode == null || assetNode.isNull() || assetNode.isMissingNode()) {
            return null;
        }

        String assetId = assetNode.path("@id").asText(null);

        JsonNode props = assetNode.path("properties");

        String name = props.path("name").asText(null);
        String description = props.path("description").asText(null);
        String contentType = props.path("contenttype").asText(null);


        String createdAt = props.path("createdAt").asText(null);

        List<String> allowedCompanies = new ArrayList<>();
        JsonNode allowedNode = props.path("allowedCompanies");
        if (allowedNode.isArray()) {
            for (JsonNode bpn : allowedNode) {
                allowedCompanies.add(bpn.asText());
            }
        }

        String policyType = allowedCompanies.isEmpty() ? "unrestricted" : "restricted";
        AssetView.PolicyView policyView = new AssetView.PolicyView(policyType, allowedCompanies);

        return new AssetView(assetId, name, description, contentType, policyView, createdAt);
    }
}

