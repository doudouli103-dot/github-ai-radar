package com.github.airadar.github.service;

import com.github.airadar.github.config.GithubSearchProperties;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class GithubSearchQueryFactory {

    private final GithubSearchProperties properties;

    public GithubSearchQueryFactory(GithubSearchProperties properties) {
        this.properties = properties;
    }

    public List<String> buildQueries(LocalDate reportDate) {
        LocalDate pushedAfter = reportDate.minusDays(properties.getPushedAfterDays());
        List<String> queries = new ArrayList<String>();
        for (String topic : properties.getTopics()) {
            queries.add("topic:" + topic
                    + " pushed:>" + pushedAfter
                    + " stars:>" + properties.getMinStars());
        }
        return queries;
    }
}
