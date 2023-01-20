package io.symplesims.spring.aws.ssm.parameterstore.cmpnt;

import io.symplesims.spring.aws.ssm.autoconfigure.SsmParameterValue;
import io.symplesims.spring.aws.ssm.autoconfigure.ValueType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Component
public class SampleCatalogueService {

    @Value("${spring.cloud.aws.ssm.region:us-east-1}")
    private String awsRegion;

    @SsmParameterValue("/ASSET/DEV/DB/MAIN/USERNAME")
    private String username;
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

}
