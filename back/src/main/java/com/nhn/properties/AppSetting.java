package com.nhn.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app")
public class AppSetting {
    @NestedConfigurationProperty
    private Origin origin = new Origin();

    @Data
    public static class Origin {
        private String api;
    }
}
