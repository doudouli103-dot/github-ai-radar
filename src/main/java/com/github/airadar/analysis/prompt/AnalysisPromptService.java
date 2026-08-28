package com.github.airadar.analysis.prompt;

import com.github.airadar.repository.model.GithubRepository;
import com.github.airadar.github.model.GithubRepositoryContext;
import com.github.airadar.github.model.GithubRepositorySourceContext;
import com.github.airadar.github.model.GithubSourceFile;
import com.github.airadar.scoring.model.RepositoryScore;
import org.springframework.stereotype.Service;

import java.util.StringJoiner;

@Service
public class AnalysisPromptService {

    public AnalysisPrompt lightPrompt(RepositoryScore score) {
        return lightPrompt(score, GithubRepositoryContext.empty());
    }

    public AnalysisPrompt lightPrompt(RepositoryScore score, GithubRepositoryContext context) {
        GithubRepository repository = score.getRepository();
        String systemPrompt = "You are an AI open source intelligence analyst. "
                + "Return only valid JSON. Do not wrap the JSON in markdown.";
        String userPrompt = new StringBuilder()
                .append("Analyze this GitHub AI repository for a daily technical intelligence report.\n\n")
                .append(repositoryBlock(repository, score))
                .append(contextBlock(context))
                .append("\nReturn JSON with these fields:\n")
                .append("{\n")
                .append("  \"category\": \"Agent | RAG | MCP | Coding | Workflow | Model Infra | Data | DevTool | Other\",\n")
                .append("  \"summary\": \"what it does\",\n")
                .append("  \"problemSolved\": \"problem it solves\",\n")
                .append("  \"growthReason\": \"why it may be growing now\",\n")
                .append("  \"technicalInnovation\": \"technical innovation\",\n")
                .append("  \"businessValue\": \"commercial value\",\n")
                .append("  \"learningValue\": \"learning value\",\n")
                .append("  \"worthFurtherStudy\": true\n")
                .append("}")
                .toString();
        return new AnalysisPrompt(systemPrompt, userPrompt);
    }

    public AnalysisPrompt deepSourcePrompt(RepositoryScore score) {
        return deepSourcePrompt(score, GithubRepositorySourceContext.empty());
    }

    public AnalysisPrompt deepSourcePrompt(RepositoryScore score, GithubRepositorySourceContext sourceContext) {
        GithubRepository repository = score.getRepository();
        String systemPrompt = "You are a senior code intelligence agent. Analyze source architecture "
                + "and return only valid JSON.";
        String userPrompt = new StringBuilder()
                .append("Deeply analyze this repository as one of today's Top 3 AI projects.\n\n")
                .append(repositoryBlock(repository, score))
                .append(sourceBlock(sourceContext))
                .append("\nFocus on: Agent Loop, Tool Calling, Memory, Context Management, RAG, MCP, ")
                .append("Workflow, Planning, Model Router, and Multi-Agent.\n\n")
                .append("Return JSON with these fields:\n")
                .append("{\n")
                .append("  \"category\": \"Agent | RAG | MCP | Coding | Workflow | Model Infra | Data | DevTool | Other\",\n")
                .append("  \"architectureSummary\": \"architecture summary\",\n")
                .append("  \"growthReason\": \"why it may be growing now\",\n")
                .append("  \"technicalInnovation\": \"technical innovation\",\n")
                .append("  \"businessValue\": \"commercial value\",\n")
                .append("  \"learningSuggestions\": [\"what to study\"],\n")
                .append("  \"keyFiles\": [{\"path\": \"path/to/file\", \"reason\": \"why it matters\"}]\n")
                .append("}")
                .toString();
        return new AnalysisPrompt(systemPrompt, userPrompt);
    }

    private String sourceBlock(GithubRepositorySourceContext sourceContext) {
        StringBuilder builder = new StringBuilder();
        builder.append("\nSelected source files:\n");
        for (GithubSourceFile file : sourceContext.getFiles()) {
            builder.append("\nFile: ").append(file.getPath()).append("\n")
                    .append("```")
                    .append("\n")
                    .append(truncate(file.getContent(), 4000))
                    .append("\n")
                    .append("```")
                    .append("\n");
        }
        return builder.toString();
    }

    private String repositoryBlock(GithubRepository repository, RepositoryScore score) {
        return new StringBuilder()
                .append("Repository: ").append(value(repository.getFullName())).append("\n")
                .append("Description: ").append(value(repository.getDescription())).append("\n")
                .append("Language: ").append(value(repository.getLanguage())).append("\n")
                .append("Topics: ").append(topics(repository)).append("\n")
                .append("Star growth: ").append(score.getStarGrowth()).append("\n")
                .append("Fork growth: ").append(score.getForkGrowth()).append("\n")
                .append("Commit activity: ").append(score.getCommitActivity()).append("\n")
                .append("Issue activity: ").append(score.getIssueActivity()).append("\n")
                .append("AI relevance: ").append(score.getAiRelevance()).append("\n")
                .append("Freshness: ").append(score.getFreshness()).append("\n")
                .append("HotScore: ").append(String.format("%.2f", score.getHotScore())).append("\n")
                .toString();
    }

    private String contextBlock(GithubRepositoryContext context) {
        return new StringBuilder()
                .append("\nREADME excerpt:\n")
                .append(truncate(context.getReadme(), 3000)).append("\n")
                .append("Recent commits:\n")
                .append(lines(context.getRecentCommits())).append("\n")
                .append("Recent issues:\n")
                .append(lines(context.getRecentIssues())).append("\n")
                .toString();
    }

    private String lines(Iterable<String> values) {
        StringBuilder builder = new StringBuilder();
        for (String value : values) {
            builder.append("- ").append(value).append("\n");
        }
        return builder.toString();
    }

    private String truncate(String value, int maxLength) {
        if (value == null) {
            return "";
        }
        if (value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }

    private String topics(GithubRepository repository) {
        StringJoiner joiner = new StringJoiner(", ");
        for (String topic : repository.getTopics()) {
            joiner.add(topic);
        }
        return joiner.toString();
    }

    private String value(String value) {
        return value == null || value.trim().isEmpty() ? "Unknown" : value;
    }
}
