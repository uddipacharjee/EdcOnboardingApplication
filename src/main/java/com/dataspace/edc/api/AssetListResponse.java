package com.dataspace.edc.api;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AssetListResponse {

    private List<AssetView> assets;

    // pagination metadata
    private int page;       // 0-based
    private int size;       // requested page size
    private int count;      // actual number of items in this page
    private boolean hasNext;
}

