package com.nhn.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.mail")
public class MailSetting {
    private String host;

    private int port;

    private String username;

    private String password;

    private String template;
}
