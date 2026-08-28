package com.github.airadar.github.dto;

import java.util.ArrayList;
import java.util.List;

public class GithubSearchResponse {

    private List<GithubSearchRepositoryItem> items = new ArrayList<GithubSearchRepositoryItem>();

    public List<GithubSearchRepositoryItem> getItems() {
        return items;
    }

    public void setItems(List<GithubSearchRepositoryItem> items) {
        this.items = items;
    }
}
