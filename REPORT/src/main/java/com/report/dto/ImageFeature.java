package com.report.dto;

import lombok.Data;

/**
 * 图像特征 DTO
 */
@Data
public class ImageFeature {
    
    /** 特征 ID */
    private String featureId;
    
    /** 商品品号 */
    private String pluno;
    
    /** 图片 URL */
    private String imageUrl;
    
    /** OSS 图片 URL */
    private String ossImageUrl;
    
    /** 特征向量 */
    private float[] featureVector;
    
    /** 向量维度 */
    private int featureDimension = 512;
    
    /** 模型版本 */
    private String modelVersion = "resnet50-v1";
    
    /** 创建人 */
    private String createdBy;
    
    /** 备注 */
    private String remark;
    
    /** 创建时间 */
    private java.util.Date createdTime;
    
    // 显式添加所有 getter
    public String getFeatureId() { return featureId; }
    public String getPluno() { return pluno; }
    public String getImageUrl() { return imageUrl; }
    public String getOssImageUrl() { return ossImageUrl; }
    public float[] getFeatureVector() { return featureVector; }
    public int getFeatureDimension() { return featureDimension; }
    public String getModelVersion() { return modelVersion; }
    public String getRemark() { return remark; }
    public String getCreatedBy() { return createdBy; }
    public java.util.Date getCreatedTime() { return createdTime; }
    
    // 显式添加所有 setter
    public void setFeatureId(String featureId) { this.featureId = featureId; }
    public void setPluno(String pluno) { this.pluno = pluno; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setOssImageUrl(String ossImageUrl) { this.ossImageUrl = ossImageUrl; }
    public void setFeatureVector(float[] featureVector) { this.featureVector = featureVector; }
    public void setFeatureDimension(int featureDimension) { this.featureDimension = featureDimension; }
    public void setModelVersion(String modelVersion) { this.modelVersion = modelVersion; }
    public void setRemark(String remark) { this.remark = remark; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public void setCreatedTime(java.util.Date createdTime) { this.createdTime = createdTime; }
}
