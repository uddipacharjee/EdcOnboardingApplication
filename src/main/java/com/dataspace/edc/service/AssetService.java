package com.dataspace.edc.service;

import com.dataspace.edc.api.AssetRegistrationRequest;
import com.dataspace.edc.api.AssetRegistrationResponse;
import com.dataspace.edc.dto.AssetDto;
import com.dataspace.edc.dto.ContractDefinitionDto;
import com.dataspace.edc.dto.CriterionDto;
import com.dataspace.edc.dto.PolicyDefinitionDto;
import com.dataspace.edc.mapper.AssetEdcMapper;
import com.dataspace.edc.mapper.OdrlPolicyBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.dataspace.edc.util.Constants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssetService {

    private final EdcClientService edcClient;
    private final AssetEdcMapper assetMapper;
    private final OdrlPolicyBuilder policyBuilder;

    public AssetRegistrationResponse register(AssetRegistrationRequest request) {

        validateRequest(request);

        String assetId = "asset-" + UUID.randomUUID();
        String policyId = "policy-" + assetId;
        String contractId = "contract-" + assetId;

        AssetDto assetDto = assetMapper.toEdcAsset(assetId, request);

        PolicyDefinitionDto policyDto = policyBuilder.buildPolicy(policyId, assetId, request.getAccessPolicy());

        log.info("Calling EDC: createAsset");
        AssetDto createdAsset = edcClient.createAsset(assetDto);
        log.info("EDC asset created: edcAssetId='{}'", createdAsset.getId());

        log.info("Calling EDC: createPolicyDefinition");
        PolicyDefinitionDto createdPolicy = edcClient.createPolicyDefinition(policyDto);
        log.info("EDC policy created: policyId='{}'", createdPolicy.getId());

        ContractDefinitionDto contractDef = buildContractDefinition(contractId, assetId, createdPolicy.getId());

        log.info("Calling EDC: createContractDefinition");
        edcClient.createContractDefinition(contractDef);
        log.info("EDC contract definition created: contractId='{}'", contractId);

        // 7) Return confirmation
        log.info("Asset registration completed successfully");

        return AssetRegistrationResponse.builder()
                .assetId(createdAsset.getId())
                .status(PUBLISHED)
                .catalogUrl(CATALOG_URL)
                .message(SUCCESS_MSG)
                .build();

    }

    private void validateRequest(AssetRegistrationRequest req) {
        // extra rules beyond annotations
        if (req.getDataAddress() == null) {
            throw new IllegalArgumentException("dataAddress is required");
        }
        String type = req.getDataAddress().getType();
        if (type == null || (!type.equals("HttpData"))) {
            throw new IllegalArgumentException("dataAddress.type must be 'HttpData'");
        }
        // uniqueness
        if (req.getAccessPolicy() != null && req.getAccessPolicy().getAllowedCompanies() != null) {
            var list = req.getAccessPolicy().getAllowedCompanies();
            var unique = new java.util.HashSet<>(list);
            if (unique.size() != list.size()) {
                throw new IllegalArgumentException("accessPolicy.allowedCompanies contains duplicates");
            }
        }
    }

    private ContractDefinitionDto buildContractDefinition(String contractId, String assetId, String policyId) {
        ContractDefinitionDto cd = new ContractDefinitionDto();
        cd.setId(contractId);
        cd.setAccessPolicyId(policyId);
        cd.setContractPolicyId(policyId);

        CriterionDto criterion = new CriterionDto();
        criterion.setOperandLeft("asset:prop:id");
        criterion.setOperator("=");
        criterion.setOperandRight(assetId);

        cd.setAssetsSelector(List.of(criterion));
        return cd;
    }
}
