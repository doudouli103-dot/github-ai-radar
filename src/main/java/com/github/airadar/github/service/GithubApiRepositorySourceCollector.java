package com.github.airadar.github.service;

import com.github.airadar.github.dto.GithubReadmeResponse;
import com.github.airadar.github.dto.GithubTreeItem;
import com.github.airadar.github.dto.GithubTreeResponse;
import com.github.airadar.github.model.GithubRepositorySourceContext;
import com.github.airadar.github.model.GithubSourceFile;
import com.github.airadar.repository.model.GithubRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Component
public class GithubApiRepositorySourceCollector implements GithubRepositorySourceCollector {

    private static final int MAX_SOURCE_FILES = 30;
    private static final int MAX_FILE_CHARS = 4000;

    private final WebClient webClient;
    private final GithubSourceFileSelector sourceFileSelector;

    public GithubApiRepositorySourceCollector(WebClient.Builder webClientBuilder,
                                              @Value("${github.token:}") String githubToken,
                                              GithubSourceFileSelector sourceFileSelector) {
        WebClient.Builder builder = webClientBuilder
                .baseUrl("https://api.github.com")
                .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .defaultHeader("X-GitHub-Api-Version", "2022-11-28");
        if (githubToken != null && !githubToken.trim().isEmpty()) {
            builder.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken.trim());
        }
        this.webClient = builder.build();
        this.sourceFileSelector = sourceFileSelector;
    }

    GithubApiRepositorySourceCollector(WebClient webClient, GithubSourceFileSelector sourceFileSelector) {
        this.webClient = webClient;
        this.sourceFileSelector = sourceFileSelector;
    }

    @Override
    public GithubRepositorySourceContext collect(GithubRepository repository) {
        GithubTreeResponse tree = webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/repos/{owner}/{repo}/git/trees/{branch}")
                        .queryParam("recursive", 1)
                        .build(repository.getOwner(), repository.getName(), branch(repository)))
                .retrieve()
                .bodyToMono(GithubTreeResponse.class)
                .onErrorReturn(new GithubTreeResponse())
                .block();
        List<GithubTreeItem> selected = sourceFileSelector.select(
                tree == null ? null : tree.getTree(), MAX_SOURCE_FILES);
        List<GithubSourceFile> files = new ArrayList<GithubSourceFile>();
        for (GithubTreeItem item : selected) {
            String content = content(repository, item.getPath());
            if (!content.trim().isEmpty()) {
                files.add(new GithubSourceFile(item.getPath(), truncate(content, MAX_FILE_CHARS)));
            }
        }
        return new GithubRepositorySourceContext(files);
    }

    private String content(GithubRepository repository, String path) {
        GithubReadmeResponse response = webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/repos/{owner}/{repo}/contents/{path}")
                        .queryParam("ref", branch(repository))
                        .build(repository.getOwner(), repository.getName(), path))
                .retrieve()
                .bodyToMono(GithubReadmeResponse.class)
                .onErrorReturn(new GithubReadmeResponse())
                .block();
        if (response == null || response.getContent() == null) {
            return "";
        }
        if ("base64".equalsIgnoreCase(response.getEncoding())) {
            byte[] decoded = Base64.getMimeDecoder().decode(response.getContent());
            return new String(decoded, StandardCharsets.UTF_8);
        }
        return response.getContent();
    }

    private String branch(GithubRepository repository) {
        if (repository.getDefaultBranch() == null || repository.getDefaultBranch().trim().isEmpty()) {
            return "HEAD";
        }
        return repository.getDefaultBranch();
    }

    private String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }
}
