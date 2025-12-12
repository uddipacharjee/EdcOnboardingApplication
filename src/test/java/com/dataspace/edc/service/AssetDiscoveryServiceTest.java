package com.dataspace.edc.service;

import com.dataspace.edc.api.AssetListResponse;
import com.dataspace.edc.api.AssetView;
import com.dataspace.edc.dto.QuerySpecDto;
import com.dataspace.edc.mapper.AssetNodeMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AssetDiscoveryServiceTest {

    private EdcClientService edcClient;
    private AssetNodeMapper mapper;
    private AssetDiscoveryService service;

    private final ObjectMapper om = new ObjectMapper();

    @BeforeEach
    void setup() {
        edcClient = mock(EdcClientService.class);
        mapper = mock(AssetNodeMapper.class);
        service = new AssetDiscoveryService(edcClient, mapper);
    }

    @Test
    void listAssets_appliesPagination_andMapsResults() throws Exception {
        JsonNode arr = om.readTree("""
            [
              {"@id":"asset-1","properties":{"name":"A"}},
              {"@id":"asset-2","properties":{"name":"B"}}
            ]
            """);

        when(edcClient.queryAssets(any(QuerySpecDto.class))).thenReturn(arr);

        when(mapper.toView(any(JsonNode.class)))
                .thenReturn(new AssetView("asset-1", "A", null, null,
                        new AssetView.PolicyView("unrestricted", java.util.List.of()), null))
                .thenReturn(new AssetView("asset-2", "B", null, null,
                        new AssetView.PolicyView("unrestricted", java.util.List.of()), null));

        AssetListResponse resp = service.listAssets(0, 20);

        assertEquals(2, resp.getCount());
        assertEquals(0, resp.getPage());
        assertEquals(20, resp.getSize());
        assertFalse(resp.isHasNext());

        verify(edcClient).queryAssets(any(QuerySpecDto.class));
        verify(mapper, times(2)).toView(any(JsonNode.class));
    }

    @Test
    void getAsset_whenNotFound_throws() {
        when(edcClient.getAssetById("missing")).thenReturn(null);
        when(mapper.toView(null)).thenReturn(null);

        assertThrows(NoSuchElementException.class, () -> service.getAsset("missing"));
    }
}

