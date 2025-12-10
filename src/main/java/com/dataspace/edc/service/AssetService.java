package com.dataspace.edc.service;

import com.dataspace.edc.api.AssetRegistrationRequest;
import com.dataspace.edc.api.AssetRegistrationResponse;
import com.dataspace.edc.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final EdcClientService edcClient;

    public AssetRegistrationResponse register(AssetRegistrationRequest request) {

        // -----------------------------
        // 1) Generate IDs
        // -----------------------------
        String assetId = "asset-" + UUID.randomUUID();
        String policyId = "policy-" + assetId;
        String contractId = "contract-" + assetId;

        // -----------------------------
        // 2) Build EDC Asset
        // -----------------------------
        AssetDto assetDto = new AssetDto();
        assetDto.setId(assetId);

        PropertiesDto props = new PropertiesDto();
        props.setName(request.getName());
        props.setDescription(request.getDescription());
        props.setContenttype(request.getContentType());
        props.setCreatedAt(Instant.now().toString());
        AssetRegistrationRequest.AccessPolicyInput ap = request.getAccessPolicy();
        if (ap != null) {
            props.setAllowedCompanies(ap.getAllowedCompanies());
            props.setUsagePurpose(ap.getUsagePurpose());
        }

        assetDto.setProperties(props);

        AssetRegistrationRequest.DataAddressInput daIn = request.getDataAddress();
        DataAddressDto da = new DataAddressDto();
        da.setType(daIn.getType());                 // "HttpData"
        da.setName(request.getName());              // or separate label
        da.setBaseUrl(daIn.getBaseUrl());           // IMPORTANT: must be non-null/valid
        da.setProxyPath("true");                    // like the samples

        assetDto.setDataAddress(da);

        AssetDto createdAsset = edcClient.createAsset(assetDto);

        // -----------------------------
        // 3) Build PolicyDefinition from accessPolicy
        // -----------------------------
        PolicyDefinitionDto policyDef = new PolicyDefinitionDto();
        policyDef.setId(policyId);

        PolicyDto policy = new PolicyDto();

        PermissionDto permission = new PermissionDto();
        permission.setTarget(assetId);
        permission.setAction("USE");

        // optional constraint from accessPolicy
        if (ap != null &&
                ap.getAllowedCompanies() != null &&
                !ap.getAllowedCompanies().isEmpty()) {

            ConstraintDto constraint = new ConstraintDto();
            constraint.setLeftOperand("BusinessPartnerNumber");
            constraint.setOperator("in");  // "in" list of BPNs
            constraint.setRightOperand(ap.getAllowedCompanies()); // send as array

            permission.setConstraint(constraint);
        }

        policy.setPermission(List.of(permission));
        policyDef.setPolicy(policy);

        PolicyDefinitionDto createdPolicy = edcClient.createPolicyDefinition(policyDef);

        // -----------------------------
        // 4) Build ContractDefinition linking asset + policy
        // -----------------------------
        ContractDefinitionDto contractDef = new ContractDefinitionDto();
        contractDef.setId(contractId);
        contractDef.setAccessPolicyId(createdPolicy.getId());
        contractDef.setContractPolicyId(createdPolicy.getId());

        CriterionDto criterion = new CriterionDto();
        // match by asset ID (this is how samples usually do it)
        criterion.setOperandLeft("asset:prop:id");
        criterion.setOperator("=");
        criterion.setOperandRight(assetId);

        contractDef.setAssetsSelector(List.of(criterion));

        edcClient.createContractDefinition(contractDef);

        // -----------------------------
        // 5) Return API response as in PDF
        // -----------------------------
        return new AssetRegistrationResponse(
                createdAsset.getId(),
                "published",
                "http://localhost:8080/api/v1/catalog/search",  // or /api/catalog as in PDF
                "Asset successfully registered and published"
        );
    }
}
