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
}
