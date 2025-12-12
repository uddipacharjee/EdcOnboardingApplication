// src/main/java/com/example/onboarding/api/AssetRegistrationResponse.java
package com.dataspace.edc.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class AssetRegistrationResponse {
    private String assetId;
    private String status;
    private String catalogUrl;
    private String message;
}
