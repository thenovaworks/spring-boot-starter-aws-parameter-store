package io.github.thenovaworks.spring.aws.ssm.autoconfigure;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * org.springframework.beans.factory.config.BeanFactoryPostProcessor
 */
public class AwsSsmParameterValueBeanPostProcessor implements BeanPostProcessor {

    private final ConfigurableListableBeanFactory configurableBeanFactory;

    private final SsmParameterStoreValueResolver candidateResolver;

    private final List<String> packages;

    private static boolean doScanPackage(final List<String> packages, final String packageName) {
        if (packages == null) {
            return false;
        }
        for (final String pkg : packages) {
            if (pkg != null && packageName.startsWith(pkg)) {
                return true;
            }
        }
        return false;
    }

    private String getBasePackage() {
        final Map<String, Object> boot = configurableBeanFactory.getBeansWithAnnotation(SpringBootApplication.class);
        return boot.values().stream().findFirst().map(v -> v.getClass().getPackageName()).orElse(null);
    }

    public AwsSsmParameterValueBeanPostProcessor(ConfigurableListableBeanFactory configurableBeanFactory, SsmParameterStoreValueResolver candidateResolver, AwsSsmClientProperties properties) {
        this.configurableBeanFactory = configurableBeanFactory;
        this.candidateResolver = candidateResolver;
        String basePackage = getBasePackage();
        List<String> values = properties.getPackages();
        if (values != null) {
            values.add(basePackage);
            this.packages = values;
        } else {
            this.packages = new ArrayList<>();
            this.packages.add(basePackage);
        }
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!doScanPackage(this.packages, bean.getClass().getPackageName())) {
            return bean;
        }
        if (configurableBeanFactory.containsBeanDefinition(beanName)) {
            ReflectionUtils.doWithLocalFields(bean.getClass(), field -> {
                SsmParameterValue ssmParameterValue = field.getAnnotation(SsmParameterValue.class);
                if (ssmParameterValue != null) {
                    final Object value = candidateResolver.getValue(ssmParameterValue);
                    if (value != null) {
                        ReflectionUtils.makeAccessible(field);
                        field.set(bean, value);
                    }
                }
            });
            ReflectionUtils.doWithLocalMethods(bean.getClass(), m -> {
                SsmParameterValue ssmParameterValue = m.getAnnotation(SsmParameterValue.class);
                if (ssmParameterValue != null) {
                    final Object value = candidateResolver.getValue(ssmParameterValue);
                    if (value != null) {
                        try {
                            m.invoke(bean, new Object[]{value});
                        } catch (InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
        }
        return bean;
    }

}
