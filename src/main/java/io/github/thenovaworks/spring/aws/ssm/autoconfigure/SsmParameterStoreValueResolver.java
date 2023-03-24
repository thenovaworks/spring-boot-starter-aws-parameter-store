package io.github.thenovaworks.spring.aws.ssm.autoconfigure;

import org.springframework.beans.factory.support.AutowireCandidateResolver;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;
import software.amazon.awssdk.services.ssm.model.GetParametersByPathRequest;
import software.amazon.awssdk.services.ssm.model.Parameter;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collector;

import static java.util.stream.Collectors.toMap;

public class SsmParameterStoreValueResolver implements AutowireCandidateResolver {

    private final Class<? extends Annotation> valueAnnotationType = SsmParameterValue.class;

    private final SsmClient ssmClient;

    private final ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<>();

    public SsmParameterStoreValueResolver(SsmClient ssmClient) {
        this.ssmClient = ssmClient;
    }

    private Collector<Parameter, ?, Map<String, String>> parametersToMap(final String path, final boolean fullname) {
        if (fullname) {
            return toMap(p -> p.name(), Parameter::value);
        }
        return toMap(p -> p.name().substring(path.length() + 1), Parameter::value);
    }

    private Map<String, String> getParameter(final String path, final boolean fullname) {
        final String value = getParameter(path);
        final Map<String, String> result = new HashMap<>();
        if (fullname) {
            result.put(path, value);
        } else {
            int pos = path.lastIndexOf("/") + 1;
            result.put(path.substring(pos), value);
        }
        return result;
    }

    private Map<String, String> getPathParameters(final String path, final boolean fullname) {
        final GetParametersByPathRequest request = GetParametersByPathRequest.builder().path(path).withDecryption(true).build();
        final List<Parameter> values = ssmClient.getParametersByPath(request).parameters();
        if (values == null || values.size() < 1) {
            return getParameter(path, fullname);
        }
        return ssmClient.getParametersByPath(request).parameters().stream().collect(parametersToMap(path, fullname));
    }

    private String getParameter(final String name) {
        final GetParameterRequest request = GetParameterRequest.builder().name(name).withDecryption(true).build();
        final GetParameterResponse response = ssmClient.getParameter(request);
        return response.parameter().value();
    }

    public Object getValue(final SsmParameterValue parameterValue) {
        final ValueType type = parameterValue.type();
        return getValue(parameterValue, type);
    }

    public Object getValue(final SsmParameterValue parameterValue, final ValueType type) {
        final String name = parameterValue.value();
        final boolean fullname = parameterValue.fullname();

        final String cacheKey = name + "." + type + "." + fullname;
        if (cache.containsValue(cacheKey)) {
            return cache.get(cacheKey);
        }
        synchronized (this.cache) {
            if (cache.get(cacheKey) != null) {
                return cache.get(cacheKey);
            }
            switch (type) {
                case STRING -> {
                    final Object value = getParameter(name);
                    assert value != null;
                    cache.put(cacheKey, value);
                    return value;
                }
                case MAP -> {
                    final Object value = getPathParameters(name, fullname);
                    cache.put(cacheKey, value);
                    return value;
                }
            }
        }
        return null;
    }

}
