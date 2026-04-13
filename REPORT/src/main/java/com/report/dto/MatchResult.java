package com.report.dto;

import lombok.Data;

/**
 * 商品匹配结果
 */
@Data
public class MatchResult {
    
    /** 匹配的品号 */
    private String pluno;
    
    /** 匹配方式：VECTOR(向量匹配)/NAME_EXACT(名称精确)/NAME_FUZZY(名称模糊)/CATEGORY(类目匹配) */
    private String matchType;
    
    /** 向量相似度（仅向量匹配时有值） */
    private Double vectorSimilarity;
    
    /** 匹配到的特征 ID（仅向量匹配时有值） */
    private String featureId;
    
    public MatchResult(String pluno, String matchType, Double vectorSimilarity, String featureId) {
        this.pluno = pluno;
        this.matchType = matchType;
        this.vectorSimilarity = vectorSimilarity;
        this.featureId = featureId;
    }
    
    public static MatchResult vectorMatch(String pluno, double similarity, String featureId) {
        return new MatchResult(pluno, "VECTOR", similarity, featureId);
    }
    
    public static MatchResult nameExactMatch(String pluno) {
        return new MatchResult(pluno, "NAME_EXACT", null, null);
    }
    
    public static MatchResult nameFuzzyMatch(String pluno) {
        return new MatchResult(pluno, "NAME_FUZZY", null, null);
    }
    
    public static MatchResult categoryMatch(String pluno) {
        return new MatchResult(pluno, "CATEGORY", null, null);
    }
}
