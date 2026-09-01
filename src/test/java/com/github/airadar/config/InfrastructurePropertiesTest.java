package com.github.airadar.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class InfrastructurePropertiesTest {

    @Test
    void bindsLocalDeviceTopologyEndpoints() {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("infrastructure.macbook.role", "development-terminal");
        values.put("infrastructure.mac-studio.model-gateway-url", "http://macstudio.tentest.cn:8088/v1");
        values.put("infrastructure.windows.rag-api-url", "http://windows.tentest.cn:8000");
        values.put("infrastructure.windows.elasticsearch-url", "http://windows.tentest.cn:9200");
        values.put("infrastructure.windows.redis-url", "redis://windows.tentest.cn:6379");

        StandardEnvironment environment = new StandardEnvironment();
        environment.getPropertySources().addFirst(new MapPropertySource("test", values));

        InfrastructureProperties properties = new Binder(ConfigurationPropertySources.get(environment))
                .bind("infrastructure", Bindable.of(InfrastructureProperties.class))
                .get();

        assertThat(properties.getMacbook().getRole()).isEqualTo("development-terminal");
        assertThat(properties.getMacStudio().getModelGatewayUrl()).isEqualTo("http://macstudio.tentest.cn:8088/v1");
        assertThat(properties.getWindows().getRagApiUrl()).isEqualTo("http://windows.tentest.cn:8000");
        assertThat(properties.getWindows().getElasticsearchUrl()).isEqualTo("http://windows.tentest.cn:9200");
        assertThat(properties.getWindows().getRedisUrl()).isEqualTo("redis://windows.tentest.cn:6379");
    }
}
