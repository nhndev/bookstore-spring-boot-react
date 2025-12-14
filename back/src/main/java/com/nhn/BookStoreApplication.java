package com.nhn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableJpaAuditing
public class BookStoreApplication {

    public static void main(final String[] args) {
        SpringApplication.run(BookStoreApplication.class, args);
    }
}
