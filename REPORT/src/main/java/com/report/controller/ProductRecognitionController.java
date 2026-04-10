package com.report.controller;

import com.report.dto.ProductRecognitionRequest;
import com.report.dto.ProductRecognitionResult;
import com.report.dto.ServiceResponse;
import com.report.service.ProductRecognitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 商品识别控制器
 */
@RestController
@RequestMapping("/api/product")
@CrossOrigin(origins = "*")
public class ProductRecognitionController {
    
    @Autowired
    private ProductRecognitionService productRecognitionService;
    
    /**
     * 识别商品
     */
    @PostMapping("/recognize")
    public Map<String, Object> recognizeProduct(@RequestParam("image") MultipartFile image) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (image.isEmpty()) {
                response.put("success", false);
                response.put("message", "图片不能为空");
                return response;
            }
            
            ProductRecognitionResult result = productRecognitionService.recognize(image);
            
            if (result.getPluno() != null) {
                response.put("success", true);
                response.put("datas", result);
                response.put("message", "识别成功");
            } else {
                response.put("success", false);
                response.put("message", "未找到匹配的商品");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "识别失败：" + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * 提交训练数据
     */
    @PostMapping("/submit-training")
    public Map<String, Object> submitTrainingData(
            @RequestParam("image") MultipartFile image,
            @RequestParam("pluno") String pluno,
            @RequestParam("productName") String productName,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "metadata", required = false) String metadata) {
        
        try {
            if (image.isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "图片不能为空");
                return error;
            }
            
            ProductRecognitionRequest request = new ProductRecognitionRequest();
            request.setImage(image);
            request.setPluno(pluno);
            request.setProductName(productName);
            request.setCategory(category);
            request.setMetadata(metadata);
            
            Map<String, Object> result = productRecognitionService.submitTrainingData(request);
            return result;
            
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "提交失败：" + e.getMessage());
            return error;
        }
    }
    
    /**
     * 获取训练统计
     */
    @GetMapping("/training-stats")
    public Map<String, Object> getTrainingStats() {
        return productRecognitionService.getTrainingStats();
    }
}
