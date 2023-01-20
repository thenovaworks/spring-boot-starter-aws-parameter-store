package io.symplesims.spring.aws.ssm.autoconfigure;

import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.AutowireCandidateResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.*;

import java.lang.annotation.Annotation;
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

    private Collector<Parameter, ?, Map<String, String>> parametersToMap(String parametersPath) {
        return toMap(p -> p.name().substring(parametersPath.length() + 1), Parameter::value);
    }

    private Map<String, String> getPathParameters(final String path) {
        final GetParametersByPathRequest request = GetParametersByPathRequest.builder().path(path).withDecryption(true).build();
        return ssmClient.getParametersByPath(request).parameters().stream().collect(parametersToMap(path));
    }

    private String getParameter(final String name) {
        final GetParameterRequest request = GetParameterRequest.builder().name(name).withDecryption(true).build();
        final GetParameterResponse response = ssmClient.getParameter(request);
        return response.parameter().value();
    }

    public Object getProperty(String parameter) throws SsmException {
        if (parameter.startsWith("/")) {
            return getParameter(parameter);
        } else if (parameter.startsWith("L/")) {
            return getPathParameters(parameter.substring(1));
        }
        return null;
    }

    public Object getSuggestedValue(DependencyDescriptor descriptor) {
        Object value = findValue(descriptor.getAnnotations());
        if (value == null) {
            MethodParameter methodParam = descriptor.getMethodParameter();
            if (methodParam != null) {
                value = findValue(methodParam.getMethodAnnotations());
            }
        }
        return value;
    }

    protected Object findValue(Annotation[] annotationsToSearch) {
        if (annotationsToSearch.length > 0) {
            AnnotationAttributes attr = AnnotatedElementUtils.getMergedAnnotationAttributes(AnnotatedElementUtils.forAnnotations(annotationsToSearch), this.valueAnnotationType);
            if (attr != null) {
                return extractValue(attr);
            }
        }
        return null;
    }

    private Object extractValue(AnnotationAttributes attr) {
        final ValueType type = (ValueType) attr.get("type");
        final String key = (String) attr.get("value");

        if (cache.containsValue(key)) {
            return cache.get(key);
        }
        synchronized (this.cache) {
            if (cache.get(key) != null) {
                return cache.get(key);
            }
            switch (type) {
                case STRING -> {
                    final Object value = getParameter(key);
                    cache.put(key, value);
                    return value;
                }
                case MAP -> {
                    final Object value = getPathParameters(key);
                    cache.put(key, value);
                    return value;
                }
            }
        }
        return null;
    }

}
