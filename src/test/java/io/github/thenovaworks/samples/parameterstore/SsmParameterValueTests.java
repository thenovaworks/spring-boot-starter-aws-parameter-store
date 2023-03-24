package io.github.thenovaworks.samples.parameterstore;

import io.github.thenovaworks.samples.parameterstore.mybean.HelloSpringBean;
import io.github.thenovaworks.samples.parameterstore.mybean.WorldSpringBean;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("local")
@SpringBootTest
class SsmParameterValueTests {

    final Logger log = LoggerFactory.getLogger(SsmParameterValueTests.class);

    @Autowired
    private HelloSpringBean helloBean;


    @Test
    void contextLoads() {
        assertNotNull(helloBean);
    }

    @Test
    public void test_ssm_string_parameter() {
        String username = helloBean.getUsername();
        Assertions.assertNotNull(username);
        log.info("username: {}", username);
    }

    @Test
    public void test_ssm_map_parameters() {
        Map<String, String> info = helloBean.getInfo();
        Assertions.assertNotNull(info);
        log.info("info: {}", info);
    }


    @Autowired
    private WorldSpringBean worldBean;

    @Test
    public void test_ssm_string_parameter_from_world() {
        String username = worldBean.getUsername();
        Assertions.assertNotNull(username);
        log.info("username: {}", username);
    }

    @Test
    public void test_ssm_map_parameters_from_world() {
        Map<String, String> info = worldBean.getInfo();
        Assertions.assertNotNull(info);
        log.info("info: {}", info);
    }


}

