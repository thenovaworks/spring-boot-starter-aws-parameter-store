package io.github.thenovaworks.samples.parameterstore.mybean;

import io.github.thenovaworks.spring.aws.ssm.autoconfigure.SsmParameterValue;
import io.github.thenovaworks.spring.aws.ssm.autoconfigure.ValueType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class WorldSpringBean {


    private String username;


    private Map<String, String> info;

    public String getUsername() {
        return username;
    }

    @SsmParameterValue("/dev/rds/cms/username")
    public void setUsername(String username) {
        this.username = username;
    }

    public Map<String, String> getInfo() {
        return info;
    }

    @SsmParameterValue(value = "/dev/rds/cms", type = ValueType.MAP)
    public void setInfo(Map<String, String> info) {
        this.info = info;
    }
}
