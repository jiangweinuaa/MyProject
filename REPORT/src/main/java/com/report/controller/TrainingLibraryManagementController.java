package com.report.controller;

import com.report.service.ProductRecognitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 训练库管理 Controller
 */
@RestController
@RequestMapping("/api/training")
@CrossOrigin(origins = "*")
public class TrainingLibraryManagementController {
    
    @Autowired
    private ProductRecognitionService productRecognitionService;
    
    /**
     * 批量重新训练所有商品特征
     * @return 任务 ID
     */
    @PostMapping("/retrain-all")
    public Map<String, Object> retrainAllFeatures() {
        System.out.println("🔄 开始批量重新训练所有商品特征...");
        
        // 异步执行训练任务
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            return productRecognitionService.retrainAllFeatures();
        });
        
        Map<String, Object> result = Map.of(
            "success", true,
            "message", "批量训练任务已启动",
            "taskId", future.toString()
        );
        
        return result;
    }
    
    /**
     * 查询训练任务进度
     * @param taskId 任务 ID
     * @return 训练进度信息
     */
    @GetMapping("/retrain-progress/{taskId}")
    public Map<String, Object> getRetrainProgress(@PathVariable String taskId) {
        return productRecognitionService.getRetrainProgress(taskId);
    }
    
    /**
     * 取消训练任务
     * @param taskId 任务 ID
     * @return 操作结果
     */
    @PostMapping("/retrain-cancel/{taskId}")
    public Map<String, Object> cancelRetrainTask(@PathVariable String taskId) {
        return productRecognitionService.cancelRetrainTask(taskId);
    }
}
