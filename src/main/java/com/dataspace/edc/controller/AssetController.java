package com.dataspace.edc.controller;

import com.dataspace.edc.api.AssetRegistrationRequest;
import com.dataspace.edc.api.AssetRegistrationResponse;
import com.dataspace.edc.api.AssetRequestDto;
import com.dataspace.edc.service.AssetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/assets")
public class AssetController {
    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @PostMapping
    public ResponseEntity<AssetRegistrationResponse> registerAsset(
            @Valid @RequestBody AssetRegistrationRequest request) {

        AssetRegistrationResponse response = assetService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
