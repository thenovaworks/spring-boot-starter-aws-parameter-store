package io.github.thenovaworks.samples.parameterstore.mybean;

import io.github.thenovaworks.spring.aws.ssm.autoconfigure.SsmParameterValue;
import io.github.thenovaworks.spring.aws.ssm.autoconfigure.ValueType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class HelloSpringBean {

    @SsmParameterValue("/dev/rds/cms/username")
    private String username;

    @SsmParameterValue(value = "/dev/rds/cms", type = ValueType.MAP)
    private Map<String, String> info;

    public String getUsername() {
        return username;
    }

    public Map<String, String> getInfo() {
        return info;
    }

}
