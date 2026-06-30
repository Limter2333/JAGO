package org.example.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RuntimeProfilePropertiesTest {

    @Test
    void localProfileConfigShouldBeLoadable() {
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource("application-local.yml"));

        Properties properties = yaml.getObject();
        assertNotNull(properties);
        assertEquals("local", properties.getProperty("money-tree.runtime.profile"));
        assertEquals("true", properties.getProperty("money-tree.runtime.mock-llm"));
    }
}
