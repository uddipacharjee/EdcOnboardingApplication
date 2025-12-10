package com.dataspace.edc.api;

import lombok.Data;

import java.util.List;

@Data
public class AssetRegistrationRequest {
    private String name;
    private String description;
    private String contentType;

    private DataAddressInput dataAddress;
    private AccessPolicyInput accessPolicy;

    @Data
    public static class DataAddressInput {
        private String type;      // "HttpData"
        private String baseUrl;   // required
    }

    @Data
    public static class AccessPolicyInput {
        private List<String> allowedCompanies;
        private String usagePurpose;
    }
}
