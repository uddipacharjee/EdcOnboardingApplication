package com.dataspace.edc.mapper;

import com.dataspace.edc.api.AssetRegistrationRequest;
import com.dataspace.edc.dto.PolicyDefinitionDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OdrlPolicyBuilderTest {

    private final OdrlPolicyBuilder builder = new OdrlPolicyBuilder();

    @Test
    void buildPolicy_withAllowedCompanies_createsConstraint() {
        AssetRegistrationRequest.AccessPolicyInput ap = new AssetRegistrationRequest.AccessPolicyInput();
        ap.setAllowedCompanies(List.of("BPN1", "BPN2"));

        PolicyDefinitionDto pd = builder.buildPolicy("policy-1", "asset-1", ap);

        assertEquals("policy-1", pd.getId());
        assertNotNull(pd.getPolicy());

        assertEquals("odrl:Set", pd.getPolicy().getType());
        assertNotNull(pd.getPolicy().getPermission());
        assertEquals(1, pd.getPolicy().getPermission().size());

        var perm = pd.getPolicy().getPermission().get(0);
        assertEquals("asset-1", perm.getTarget());
        assertEquals("USE", perm.getAction());

        assertNotNull(perm.getConstraint());
        assertEquals("BusinessPartnerNumber", perm.getConstraint().getLeftOperand());
        assertEquals("in", perm.getConstraint().getOperator());
        assertEquals(List.of("BPN1", "BPN2"), perm.getConstraint().getRightOperand());
    }

    @Test
    void buildPolicy_withoutAllowedCompanies_createsNoConstraint() {
        PolicyDefinitionDto pd = builder.buildPolicy("policy-2", "asset-2", null);

        var perm = pd.getPolicy().getPermission().get(0);
        assertNull(perm.getConstraint());
    }
}
