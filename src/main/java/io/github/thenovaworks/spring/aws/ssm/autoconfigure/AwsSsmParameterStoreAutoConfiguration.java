package io.github.thenovaworks.spring.aws.ssm.autoconfigure;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.SsmClientBuilder;

@EnableConfigurationProperties(AwsSsmClientProperties.class)
@ConditionalOnClass(SsmClientBuilder.class)
@AutoConfiguration
public class AwsSsmParameterStoreAutoConfiguration {

    private final AwsSsmClientProperties properties;

    private SsmClient ssmClient() {
        final SsmClientBuilder builder = SsmClient.builder();
        if (properties.getRegion() != null) {
            builder.region(Region.of(properties.getRegion()));
        }
        final ProviderType providerType = properties.getProviderType();
        switch (providerType) {
            case PROFILE -> builder.credentialsProvider(ProfileCredentialsProvider.create(properties.getProfile()));
            case ENVIRONMENT -> builder.credentialsProvider(EnvironmentVariableCredentialsProvider.create());
            default -> builder.credentialsProvider(DefaultCredentialsProvider.create());

        }
        return builder.build();
    }

    public AwsSsmParameterStoreAutoConfiguration(AwsSsmClientProperties properties) {
        this.properties = properties;
    }

    @Bean
    SsmParameterStoreValueResolver autowireCandidateResolver() {
        return new SsmParameterStoreValueResolver(ssmClient());
    }

    @Bean
    public AwsSsmParameterValueBeanPostProcessor awsSsmParameterValueBeanPostProcessor(final ConfigurableListableBeanFactory configurableBeanFactory, final SsmParameterStoreValueResolver candidateResolver, final AwsSsmClientProperties properties) {
        return new AwsSsmParameterValueBeanPostProcessor(configurableBeanFactory, candidateResolver, properties);
    }

}
