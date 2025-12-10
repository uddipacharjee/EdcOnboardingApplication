package com.dataspace.edc.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class AccessPolicyDto {
    @NotEmpty
    private List<@NotBlank String> allowedCompanies;

    @NotBlank
    private String usagePurpose;

}
