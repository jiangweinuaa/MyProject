package com.report.dto;

import java.util.List;

/**
 * 商品识别结果
 */
public class ProductRecognitionResult {
    
    private String pluno;                  // 商品品号
    private String productName;            // 商品名称
    private String category;               // 商品类别
    private Double confidence;             // 识别置信度
    private String recognitionSource;      // 识别来源：ALIYUN(阿里云) / LOCAL(本地)
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
}
