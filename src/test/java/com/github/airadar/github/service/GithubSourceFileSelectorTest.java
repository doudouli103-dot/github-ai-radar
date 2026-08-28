package com.github.airadar.github.service;

import com.github.airadar.github.dto.GithubTreeItem;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GithubSourceFileSelectorTest {

    @Test
    void selectsCoreAiSourceFilesAndSkipsNoise() {
        List<GithubTreeItem> treeItems = Arrays.asList(
                tree("README.md", "blob", 800),
                tree("src/agent/loop.py", "blob", 1200),
                tree("src/agent/tools.py", "blob", 900),
                tree("src/core/router.ts", "blob", 1000),
                tree("src/rag/retriever.py", "blob", 1000),
                tree("src/agent/loop_test.py", "blob", 700),
                tree("docs/architecture.md", "blob", 3000),
                tree("examples/demo.py", "blob", 800),
                tree("node_modules/pkg/index.js", "blob", 800),
                tree("target/classes/App.class", "blob", 2000),
                tree("src/generated/schema.json", "blob", 2000),
                tree("src/ui/button.css", "blob", 600),
                tree("src/agent", "tree", 0)
        );

        List<GithubTreeItem> selected = new GithubSourceFileSelector().select(treeItems, 30);

        assertThat(paths(selected)).containsExactly(
                "src/agent/loop.py",
                "src/agent/tools.py",
                "src/rag/retriever.py",
                "src/core/router.ts"
        );
    }

    @Test
    void limitsSelectedFiles() {
        List<GithubTreeItem> treeItems = new ArrayList<GithubTreeItem>();
        for (int i = 0; i < 40; i++) {
            treeItems.add(tree("src/agent/file" + i + ".py", "blob", 500));
        }

        List<GithubTreeItem> selected = new GithubSourceFileSelector().select(treeItems, 30);

        assertThat(selected).hasSize(30);
    }

    private static List<String> paths(List<GithubTreeItem> items) {
        List<String> paths = new ArrayList<String>();
        for (GithubTreeItem item : items) {
            paths.add(item.getPath());
        }
        return paths;
    }

    private static GithubTreeItem tree(String path, String type, int size) {
        GithubTreeItem item = new GithubTreeItem();
        item.setPath(path);
        item.setType(type);
        item.setSha("sha-" + path);
        item.setSize(size);
        return item;
    }
}
