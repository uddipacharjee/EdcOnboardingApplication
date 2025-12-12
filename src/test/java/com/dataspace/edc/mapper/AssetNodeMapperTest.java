package com.dataspace.edc.mapper;

import com.dataspace.edc.api.AssetView;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AssetNodeMapperTest {

    private final AssetNodeMapper mapper = new AssetNodeMapper();
    private final ObjectMapper om = new ObjectMapper();

    @Test
    void toView_mapsFields_andReadsCreatedAtFromProperties() throws Exception {
        String json = """
            {
              "@id": "asset-1",
              "properties": {
                "name": "Car Data",
                "description": "desc",
                "contenttype": "application/json",
                "createdAt": "2025-12-10T12:00:00Z",
                "allowedCompanies": ["BPN1", "BPN2"]
              }
            }
            """;

        JsonNode node = om.readTree(json);
        AssetView view = mapper.toView(node);

        assertNotNull(view);
        assertEquals("asset-1", view.getAssetId());
        assertEquals("Car Data", view.getName());
        assertEquals("desc", view.getDescription());
        assertEquals("application/json", view.getContentType());
        assertEquals("2025-12-10T12:00:00Z", view.getCreatedAt());
        assertEquals("restricted", view.getPolicy().getType());
        assertEquals(2, view.getPolicy().getAllowedCompanies().size());
    }

    @Test
    void toView_whenAllowedCompaniesMissing_policyUnrestricted() throws Exception {
        String json = """
            {
              "@id": "asset-2",
              "properties": {
                "name": "X",
                "contenttype": "application/json"
              }
            }
            """;

        AssetView view = mapper.toView(om.readTree(json));
        assertNotNull(view);
        assertEquals("unrestricted", view.getPolicy().getType());
        assertTrue(view.getPolicy().getAllowedCompanies().isEmpty());
    }
}
