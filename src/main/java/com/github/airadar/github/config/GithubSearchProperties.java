package com.github.airadar.github.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "github.search")
public class GithubSearchProperties {

    private int minStars = 50;
    private int pushedAfterDays = 30;
    private List<String> topics = new ArrayList<String>();

    public int getMinStars() {
        return minStars;
    }

    public void setMinStars(int minStars) {
        this.minStars = minStars;
    }

    public int getPushedAfterDays() {
        return pushedAfterDays;
    }

    public void setPushedAfterDays(int pushedAfterDays) {
        this.pushedAfterDays = pushedAfterDays;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }
}
