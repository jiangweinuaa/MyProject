package com.report.dto;

import org.springframework.web.multipart.MultipartFile;

/**
 * 商品识别请求
 */
public class ProductRecognitionRequest {
    
    private MultipartFile image;           // 图片文件
    private String pluno;                  // 商品品号（扫码或手动输入）
    private String productName;            // 商品名称
    private String category;               // 商品类别
    private String metadata;               // 拍摄元数据（JSON）
    
    // Getters and Setters
    public MultipartFile getImage() {
        return image;
    }
    
    public void setImage(MultipartFile image) {
        this.image = image;
    }
    
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
    
    public String getMetadata() {
        return metadata;
    }
    
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
}
