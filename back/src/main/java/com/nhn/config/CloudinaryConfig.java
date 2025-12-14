package com.nhn.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;
import com.nhn.properties.CloudinarySetting;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class CloudinaryConfig {
    private final CloudinarySetting cloudinarySetting;

    @Bean
    public Cloudinary cloudinary() {
        final Map<String, String> config = new HashMap<>();
        config.put("cloud_name", this.cloudinarySetting.getCloudName());
        config.put("api_key", this.cloudinarySetting.getApiKey());
        config.put("api_secret", this.cloudinarySetting.getApiSecret());
        return new Cloudinary(config);
    }
}
