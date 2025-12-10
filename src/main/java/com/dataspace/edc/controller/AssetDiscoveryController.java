package com.dataspace.edc.controller;

import com.dataspace.edc.api.AssetListResponse;
import com.dataspace.edc.api.AssetView;
import com.dataspace.edc.service.AssetDiscoveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/assets")
@RequiredArgsConstructor
public class AssetDiscoveryController {

    private final AssetDiscoveryService discoveryService;

    // GET /api/v1/assets?page=0&size=20
    @GetMapping
    public ResponseEntity<AssetListResponse> listAssets(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {

        AssetListResponse response = discoveryService.listAssets(page, size);
        return ResponseEntity.ok(response);
    }

    // GET /api/v1/assets/{assetId}
    @GetMapping("/{assetId}")
    public ResponseEntity<AssetView> getAsset(@PathVariable String assetId) {
        AssetView view = discoveryService.getAsset(assetId);
        return ResponseEntity.ok(view);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNotFound(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}

