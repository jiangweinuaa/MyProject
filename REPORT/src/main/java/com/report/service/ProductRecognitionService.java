package com.report.service;

import com.report.dto.ProductRecognitionRequest;
import com.report.dto.ProductRecognitionResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 商品识别服务接口
 */
public interface ProductRecognitionService {
    
    /**
     * 识别商品
     * @param image 图片文件
     * @return 识别结果
     */
    ProductRecognitionResult recognize(MultipartFile image);
    
    /**
     * 识别商品（带用户确认）
     * @param image 图片文件
     * @param confirmedPluno 用户确认的商品品号
     * @return 识别结果
     */
    ProductRecognitionResult recognizeWithConfirmation(MultipartFile image, String confirmedPluno);
    
    /**
     * 提交训练数据
     * @param request 训练数据请求
     * @return 结果
     */
    Map<String, Object> submitTrainingData(ProductRecognitionRequest request);
    
    /**
     * 查询商品特征
     * @param pluno 商品品号
     * @return 特征数据
     */
    Map<String, Object> getProductFeatures(String pluno);
    
    /**
     * 获取训练样本统计
     * @return 统计数据
     */
    Map<String, Object> getTrainingStats();
    
    /**
     * 开始训练模型
     * @param config 训练配置
     * @return 训练任务 ID
     */
    String startTraining(Map<String, Object> config);
    
    /**
     * 查询训练任务状态
     * @param taskId 任务 ID
     * @return 任务状态
     */
    Map<String, Object> getTrainingStatus(String taskId);
    
    /**
     * 批量重新训练所有商品特征
     * @return 任务 ID
     */
    String retrainAllFeatures();
    
    /**
     * 获取批量训练进度
     * @param taskId 任务 ID
     * @return 进度信息
     */
    Map<String, Object> getRetrainProgress(String taskId);
    
    /**
     * 取消批量训练任务
     * @param taskId 任务 ID
     * @return 操作结果
     */
    Map<String, Object> cancelRetrainTask(String taskId);
}
