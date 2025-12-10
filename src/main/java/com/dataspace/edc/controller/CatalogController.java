package com.dataspace.edc.controller;

import com.dataspace.edc.api.CatalogSearchResponse;
import com.dataspace.edc.service.CatalogSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/catalog")
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogSearchService catalogSearchService;

    @GetMapping("/search")
    public CatalogSearchResponse search(@RequestParam(required = false) String keyword) {
        return catalogSearchService.search(keyword);
    }
}

