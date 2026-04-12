package com.report.dto;

import java.util.List;

/**
 * 商品识别响应（支持多商品）
 */
public class ProductRecognitionResponse {
    
    private String pluno;                  // 商品品号（匹配到的真实品号，可能为 null）
    private String productName;            // 商品名称
    private String category;               // 商品类别
    private Double confidence;             // 识别置信度
    private String recognitionSource;      // 识别来源：ALIYUN(阿里云) / LOCAL_MATCH(本地匹配)
    private Integer quantity;              // 识别数量
    private String matchSource;            // 匹配来源：LOCAL_MATCH / ALIYUN
    private String imageUrl;               // 图片 URL
    private Long recognitionTime;          // 识别耗时（毫秒）
    
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
    
    public String getRecognitionSource() {
        return recognitionSource;
    }
    
    public void setRecognitionSource(String recognitionSource) {
        this.recognitionSource = recognitionSource;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public String getMatchSource() {
        return matchSource;
    }
    
    public void setMatchSource(String matchSource) {
        this.matchSource = matchSource;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public Long getRecognitionTime() {
        return recognitionTime;
    }
    
    public void setRecognitionTime(Long recognitionTime) {
        this.recognitionTime = recognitionTime;
    }
}
