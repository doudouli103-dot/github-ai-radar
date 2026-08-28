package com.github.airadar.report.service;

import com.github.airadar.analysis.model.RepositoryAnalysis;
import com.github.airadar.scoring.model.RepositoryScore;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
public class MarkdownReportService {

    public String render(LocalDate reportDate, List<RepositoryScore> topScores,
                         List<RepositoryAnalysis> analyses) {
        StringBuilder markdown = new StringBuilder();
        markdown.append("# GitHub AI Daily - ").append(reportDate).append("\n\n");
        markdown.append("## Today's Trend\n\n");
        markdown.append("Fast-growing AI repositories are ranked by daily growth, activity, AI relevance, and freshness.\n\n");

        markdown.append("## Top 10 Projects\n\n");
        markdown.append("| Rank | Repository | Category | HotScore | Star Growth | Fork Growth |\n");
        markdown.append("| ---: | --- | --- | ---: | ---: | ---: |\n");

        for (int i = 0; i < topScores.size(); i++) {
            RepositoryScore score = topScores.get(i);
            RepositoryAnalysis analysis = findAnalysis(analyses, score.getRepository().getId());
            markdown.append("| ").append(i + 1)
                    .append(" | ").append(score.getRepository().getFullName())
                    .append(" | ").append(analysis == null ? "Unknown" : analysis.getCategory())
                    .append(" | ").append(String.format("%.2f", score.getHotScore()))
                    .append(" | ").append(score.getStarGrowth())
                    .append(" | ").append(score.getForkGrowth())
                    .append(" |\n");
        }

        markdown.append("\n## Top 3 Deep Analysis\n\n");
        analyses.stream()
                .sorted(Comparator.comparing(RepositoryAnalysis::getRepositoryId))
                .limit(3)
                .forEach(analysis -> appendAnalysis(markdown, analysis));

        RepositoryScore best = topScores.stream()
                .max(Comparator.comparingDouble(RepositoryScore::getHotScore))
                .orElse(null);
        if (best != null) {
            markdown.append("## Today's Most Worth Studying\n\n");
            markdown.append(best.getRepository().getFullName()).append("\n");
        }

        return markdown.toString();
    }

    private void appendAnalysis(StringBuilder markdown, RepositoryAnalysis analysis) {
        markdown.append("### Repository ").append(analysis.getRepositoryId()).append("\n\n");
        markdown.append("- Category: ").append(value(analysis.getCategory())).append("\n");
        markdown.append("- Summary: ").append(value(analysis.getSummary())).append("\n");
        markdown.append("- Why It Is Growing: ").append(value(analysis.getGrowthReason())).append("\n");
        markdown.append("- Technical Innovation: ").append(value(analysis.getTechnicalInnovation())).append("\n");
        markdown.append("- Business Value: ").append(value(analysis.getBusinessValue())).append("\n");
        markdown.append("- Learning Value: ").append(value(analysis.getLearningValue())).append("\n");
        markdown.append("- Suggested Source Files: ").append(String.join(", ", analysis.getKeyFiles())).append("\n\n");
    }

    private RepositoryAnalysis findAnalysis(List<RepositoryAnalysis> analyses, Long repositoryId) {
        for (RepositoryAnalysis analysis : analyses) {
            if (analysis.getRepositoryId().equals(repositoryId)) {
                return analysis;
            }
        }
        return null;
    }

    private String value(String value) {
        return value == null || value.trim().isEmpty() ? "Unknown" : value;
    }
}
