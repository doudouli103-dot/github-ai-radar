package com.github.airadar.github.dto;

import java.util.Collections;
import java.util.List;

public class GithubTreeResponse {

    private List<GithubTreeItem> tree = Collections.emptyList();

    public List<GithubTreeItem> getTree() {
        return tree;
    }

    public void setTree(List<GithubTreeItem> tree) {
        this.tree = tree == null ? Collections.<GithubTreeItem>emptyList() : tree;
    }
}
