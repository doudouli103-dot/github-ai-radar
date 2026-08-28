package com.github.airadar.github.service;

import com.github.airadar.github.dto.GithubTreeItem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Component
public class GithubSourceFileSelector {

    public List<GithubTreeItem> select(List<GithubTreeItem> treeItems, int limit) {
        if (treeItems == null || limit <= 0) {
            return Collections.emptyList();
        }
        List<GithubTreeItem> candidates = new ArrayList<GithubTreeItem>();
        for (GithubTreeItem item : treeItems) {
            if (isCandidate(item)) {
                candidates.add(item);
            }
        }
        Collections.sort(candidates, Comparator.comparingInt(this::priority)
                .thenComparing(GithubTreeItem::getPath));
        if (candidates.size() <= limit) {
            return candidates;
        }
        return new ArrayList<GithubTreeItem>(candidates.subList(0, limit));
    }

    private boolean isCandidate(GithubTreeItem item) {
        if (item == null || !"blob".equals(item.getType()) || item.getPath() == null) {
            return false;
        }
        String path = item.getPath().toLowerCase(Locale.ROOT);
        return hasSourceExtension(path)
                && !isNoisy(path)
                && (isLikelySourceRoot(path) || isAiKeywordPath(path));
    }

    private boolean hasSourceExtension(String path) {
        return path.endsWith(".java")
                || path.endsWith(".py")
                || path.endsWith(".ts")
                || path.endsWith(".tsx")
                || path.endsWith(".js")
                || path.endsWith(".go")
                || path.endsWith(".rs")
                || path.endsWith(".kt");
    }

    private boolean isNoisy(String path) {
        return path.contains("/test/")
                || path.contains("_test.")
                || path.contains(".test.")
                || path.contains(".spec.")
                || path.startsWith("test/")
                || path.startsWith("tests/")
                || path.startsWith("docs/")
                || path.startsWith("examples/")
                || path.contains("/examples/")
                || path.contains("/node_modules/")
                || path.startsWith("node_modules/")
                || path.startsWith("target/")
                || path.startsWith("dist/")
                || path.startsWith("build/")
                || path.contains("/generated/");
    }

    private boolean isLikelySourceRoot(String path) {
        return path.startsWith("src/")
                || path.startsWith("packages/")
                || path.startsWith("apps/")
                || path.startsWith("lib/")
                || path.startsWith("core/");
    }

    private boolean isAiKeywordPath(String path) {
        return path.contains("agent")
                || path.contains("rag")
                || path.contains("mcp")
                || path.contains("workflow")
                || path.contains("tool")
                || path.contains("memory")
                || path.contains("planner")
                || path.contains("router");
    }

    private int priority(GithubTreeItem item) {
        String path = item.getPath().toLowerCase(Locale.ROOT);
        if (path.contains("agent") || path.contains("tool")) {
            return 0;
        }
        if (path.contains("rag") || path.contains("mcp")) {
            return 1;
        }
        if (path.contains("core") || path.contains("workflow") || path.contains("router")) {
            return 2;
        }
        return 3;
    }
}
