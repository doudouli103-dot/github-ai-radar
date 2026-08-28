package com.github.airadar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class GithubAiRadarApplication {

    public static void main(String[] args) {
        SpringApplication.run(GithubAiRadarApplication.class, args);
    }
}
