package io.symplesims.spring.aws.ssm.parameterstore;

import io.symplesims.spring.aws.ssm.parameterstore.cmpnt.SampleAssetService;
import io.symplesims.spring.aws.ssm.parameterstore.cmpnt.SampleCatalogueService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

@ActiveProfiles("local")
@SpringBootTest
class SsmParameterValueAnnotationTests {

    @Autowired
    private SampleAssetService assetService;


    @Autowired
    private SampleCatalogueService catalogueService;

    @Test
    void contextLoads() {
        System.out.println("sampleSsmParameterResolver: " + catalogueService);
    }

    @Test
    public void test_assetService() {
        String region = assetService.getAwsRegion();
        Assertions.assertEquals("ap-northeast-2", region);
        System.out.println("region: " + region);

        String username = assetService.getUsername();
        Assertions.assertEquals("costSpark", username);
        System.out.println("username: " + username);

        Map<String, String> assetRdsInfo = assetService.getAssetRdsInfo();
        Assertions.assertEquals("ASSET", assetRdsInfo.get("DATABASE"));
        System.out.println("assetRdsInfo: " + assetRdsInfo);

        Map<String, String> catalogueEndpoint = assetService.getCatalogueEndpoint();
        System.out.println("catalogueEndpoint: " + catalogueEndpoint);
    }

    @Test
    public void test_catalogueService() {
        String region = catalogueService.getAwsRegion();
        Assertions.assertEquals("us-east-1", region);
        System.out.println("region: " + region);

        String username = catalogueService.getUsername();
        Assertions.assertEquals("costSpark", username);
        System.out.println("username: " + username);

        Map<String, String> catalogueEndpoint = catalogueService.getCatalogueEndpoint();
        System.out.println("catalogueEndpoint: " + catalogueEndpoint);
    }

}

