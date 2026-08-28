package com.github.airadar.github.service;

import com.github.airadar.github.config.GithubSearchProperties;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GithubSearchQueryFactoryTest {

    @Test
    void buildsTopicQueriesWithPushedDateAndMinimumStars() {
        GithubSearchProperties properties = new GithubSearchProperties();
        properties.setMinStars(50);
        properties.setPushedAfterDays(30);
        properties.setTopics(Arrays.asList("agent", "rag", "mcp"));

        List<String> queries = new GithubSearchQueryFactory(properties)
                .buildQueries(LocalDate.of(2026, 8, 27));

        assertThat(queries).containsExactly(
                "topic:agent pushed:>2026-07-28 stars:>50",
                "topic:rag pushed:>2026-07-28 stars:>50",
                "topic:mcp pushed:>2026-07-28 stars:>50"
        );
    }
}
