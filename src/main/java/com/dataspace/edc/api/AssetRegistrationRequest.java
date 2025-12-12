package com.dataspace.edc.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class AssetRegistrationRequest {

    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "description is required")
    private String description;

    @NotBlank(message = "contentType is required")
    private String contentType;

    @NotNull(message = "dataAddress is required")
    @Valid
    private DataAddressInput dataAddress;

    @Valid
    private AccessPolicyInput accessPolicy;

    @Data
    public static class DataAddressInput {
        @NotBlank(message = "dataAddress.type is required")
        private String type;

        @NotBlank(message = "dataAddress.baseUrl is required")
        @jakarta.validation.constraints.Pattern(
                regexp = "https?://.*",
                message = "dataAddress.baseUrl must be a valid http/https URL"
        )
        private String baseUrl;
    }

    @Data
    public static class AccessPolicyInput {
        private List<@NotBlank(message = "allowedCompanies cannot contain blank values") String> allowedCompanies;

        @Size(max = 200, message = "usagePurpose too long")
        private String usagePurpose;
    }
}

