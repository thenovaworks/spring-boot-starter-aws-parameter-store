package io.symplesims.spring.aws.ssm.parameterstore.cmpnt;

import io.symplesims.spring.aws.ssm.autoconfigure.SsmParameterValue;
import io.symplesims.spring.aws.ssm.autoconfigure.ValueType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SampleAssetService {

    @Value("${spring.cloud.aws.ssm.region:ap-northeast-2}")
    private String awsRegion;

    @SsmParameterValue("/ASSET/DEV/DB/MAIN/USERNAME")
    private String username;

    @SsmParameterValue(value = "/ASSET/DEV/DB/MAIN", type = ValueType.MAP)
    private Map<String, String> assetRdsInfo;

    @SsmParameterValue(value = "/DATA/DEV/DB/CATALOG/ENDPOINT", type = ValueType.MAP)
    private Map<String, String> catalogueEndpoint;

    public Map<String, String> getCatalogueEndpoint() {
        return catalogueEndpoint;
    }

    public String getAwsRegion() {
        return this.awsRegion;
    }

    public String getUsername() {
        return this.username;
    }

    public Map<String, String> getAssetRdsInfo() {
        return assetRdsInfo;
    }


}
