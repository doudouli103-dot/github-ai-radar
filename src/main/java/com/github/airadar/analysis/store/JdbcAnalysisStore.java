package com.github.airadar.analysis.store;

import com.github.airadar.analysis.model.RepositoryAnalysis;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JdbcAnalysisStore implements AnalysisStore {

    private final NamedParameterJdbcOperations jdbc;

    public JdbcAnalysisStore(NamedParameterJdbcOperations jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void save(RepositoryAnalysis analysis) {
        String sql = "INSERT INTO repository_analysis (repository_id, analysis_date, analysis_type, "
                + "model_role, category, summary, growth_reason, technical_innovation, "
                + "business_value, learning_value, key_files, raw_output) "
                + "VALUES (:repositoryId, :analysisDate, :analysisType, :modelRole, :category, "
                + ":summary, :growthReason, :technicalInnovation, :businessValue, :learningValue, "
                + "CAST(:keyFiles AS jsonb), CAST(:rawOutput AS jsonb))";
        jdbc.update(sql, new MapSqlParameterSource()
                .addValue("repositoryId", analysis.getRepositoryId())
                .addValue("analysisDate", analysis.getAnalysisDate())
                .addValue("analysisType", analysis.getAnalysisType())
                .addValue("modelRole", analysis.getModelRole())
                .addValue("category", analysis.getCategory())
                .addValue("summary", analysis.getSummary())
                .addValue("growthReason", analysis.getGrowthReason())
                .addValue("technicalInnovation", analysis.getTechnicalInnovation())
                .addValue("businessValue", analysis.getBusinessValue())
                .addValue("learningValue", analysis.getLearningValue())
                .addValue("keyFiles", toJsonArray(analysis.getKeyFiles()))
                .addValue("rawOutput", analysis.getRawOutput() == null ? "{}" : analysis.getRawOutput()));
    }

    private String toJsonArray(List<String> values) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                json.append(",");
            }
            json.append("\"").append(values.get(i).replace("\"", "\\\"")).append("\"");
        }
        json.append("]");
        return json.toString();
    }
}
