package com.dataspace.edc.mapper;

import com.dataspace.edc.api.AssetRegistrationRequest;
import com.dataspace.edc.dto.ConstraintDto;
import com.dataspace.edc.dto.PermissionDto;
import com.dataspace.edc.dto.PolicyDefinitionDto;
import com.dataspace.edc.dto.PolicyDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OdrlPolicyBuilder {

    public PolicyDefinitionDto buildPolicy(String policyId, String assetId, AssetRegistrationRequest.AccessPolicyInput ap) {
        PolicyDefinitionDto pd = new PolicyDefinitionDto();
        pd.setId(policyId);

        PolicyDto policy = new PolicyDto();
        // IMPORTANT: must be "odrl:Set" (not edc:Set)
        policy.setType("odrl:Set");

        PermissionDto perm = new PermissionDto();
        perm.setTarget(assetId);
        perm.setAction("USE");

        if (ap != null && ap.getAllowedCompanies() != null && !ap.getAllowedCompanies().isEmpty()) {
            ConstraintDto c = new ConstraintDto();
            c.setLeftOperand("BusinessPartnerNumber");
            c.setOperator("in");
            c.setRightOperand(ap.getAllowedCompanies());
            perm.setConstraint(c);
        }

        policy.setPermission(List.of(perm));
        pd.setPolicy(policy);
        return pd;
    }
}
