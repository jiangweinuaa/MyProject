package com.report.dto;

import java.util.List;

/**
 * 商品识别结果
 */
public class ProductRecognitionResult {
    
    private String pluno;                  // 商品品号（匹配到的真实品号）
    private String sourcePluno;            // 临时品号（阿里云返回的 CategoryId）
    private String productName;            // 商品名称
    private String category;               // 商品类别
    private Double confidence;             // 识别置信度
    private String recognitionSource;      // 识别来源：ALIYUN(阿里云) / LOCAL(本地)
    private String matchType;              // 匹配方式：VECTOR(向量匹配)/NAME_EXACT(名称精确)/NAME_FUZZY(名称模糊)/CATEGORY(类目匹配)
    private Double vectorSimilarity;       // 向量相似度（仅向量匹配时有值）
    private String matchedFeatureId;       // 匹配到的特征 ID（PRODUCT_IMAGE_FEATURES 表主键）
    private String featureAlgorithm;       // 使用的特征提取算法（HISTOGRAM/HISTOGRAM_GRID/RESNET50）
    private List<SimilarProduct> similarProducts;  // 相似商品列表
    
    /**
     * 相似商品信息
     */
    public static class SimilarProduct {
        private String pluno;
        private String productName;
        private String category;
        private Double similarity;
        
        public SimilarProduct(String pluno, String productName, String category, Double similarity) {
            this.pluno = pluno;
            this.productName = productName;
            this.category = category;
            this.similarity = similarity;
        }
        
        // Getters and Setters
        public String getPluno() { return pluno; }
        public void setPluno(String pluno) { this.pluno = pluno; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public Double getSimilarity() { return similarity; }
        public void setSimilarity(Double similarity) { this.similarity = similarity; }
    }
    
    // Getters and Setters
    public String getPluno() {
        return pluno;
    }
    
    public void setPluno(String pluno) {
        this.pluno = pluno;
    }
    
    public String getSourcePluno() {
        return sourcePluno;
    }
    
    public void setSourcePluno(String sourcePluno) {
        this.sourcePluno = sourcePluno;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public Double getConfidence() {
        return confidence;
    }
    
    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }
    
    public List<SimilarProduct> getSimilarProducts() {
        return similarProducts;
    }
    
    public void setSimilarProducts(List<SimilarProduct> similarProducts) {
        this.similarProducts = similarProducts;
    }
    
    public String getRecognitionSource() {
        return recognitionSource;
    }
    
    public void setRecognitionSource(String recognitionSource) {
        this.recognitionSource = recognitionSource;
    }
    
    public String getMatchType() {
        return matchType;
    }
    
    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }
    
    public Double getVectorSimilarity() {
        return vectorSimilarity;
    }
    
    public void setVectorSimilarity(Double vectorSimilarity) {
        this.vectorSimilarity = vectorSimilarity;
    }
    
    public String getMatchedFeatureId() {
        return matchedFeatureId;
    }
    
    public void setMatchedFeatureId(String matchedFeatureId) {
        this.matchedFeatureId = matchedFeatureId;
    }
    
    public String getFeatureAlgorithm() {
        return featureAlgorithm;
    }
    
    public void setFeatureAlgorithm(String featureAlgorithm) {
        this.featureAlgorithm = featureAlgorithm;
    }
}
