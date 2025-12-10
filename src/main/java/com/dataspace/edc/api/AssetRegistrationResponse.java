// src/main/java/com/example/onboarding/api/AssetRegistrationResponse.java
package com.dataspace.edc.api;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AssetRegistrationResponse {
    private String assetId;
    private String status;
    private String catalogUrl;
    private String message;
}
