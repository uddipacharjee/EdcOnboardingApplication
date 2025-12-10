package com.dataspace.edc.api;

import com.dataspace.edc.dto.DataAddressDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssetRequestDto {
    @NotBlank
    private String name;

    private String description;

    @NotBlank
    private String contentType;

    @NotNull
    private DataAddressDto dataAddress;

    @NotNull
    private AccessPolicyDto accessPolicy;
}
