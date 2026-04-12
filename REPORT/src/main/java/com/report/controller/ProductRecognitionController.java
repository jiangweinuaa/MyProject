package com.report.controller;

import com.report.dto.ProductRecognitionRequest;
import com.report.dto.ProductRecognitionResult;
import com.report.dto.ProductRecognitionResponse;
import com.report.dto.ServiceResponse;
import com.report.service.ProductRecognitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

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
     * 识别商品（返回多商品数组）
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
            
            // 调用识别服务
            ProductRecognitionResult result = productRecognitionService.recognize(image);
            
            // 构建多商品响应数组（目前支持单商品，预留多商品扩展）
            List<ProductRecognitionResponse> products = new ArrayList<>();
            
            ProductRecognitionResponse product = new ProductRecognitionResponse();
            product.setPluno(result.getPluno());  // 匹配到的真实品号（可能为 null）
            // sourcePluno: 阿里云返回的原始识别结果（商品名称），作为临时品号显示
            product.setSourcePluno(result.getProductName());  
            product.setProductName(result.getProductName());
            product.setCategory(result.getCategory());
            product.setConfidence(result.getConfidence());
            product.setRecognitionSource(result.getRecognitionSource());
            product.setMatchSource(result.getRecognitionSource());  // LOCAL_MATCH 或 ALIYUN
            product.setQuantity(1);
            product.setImageUrl(null);  // 预留图片 URL
            product.setRecognitionTime(System.currentTimeMillis());
            
            products.add(product);
            
            // 构建响应
            response.put("success", true);
            response.put("message", "识别成功，共识别到 " + products.size() + " 种商品");
            response.put("productCount", products.size());
            
            Map<String, Object> datas = new HashMap<>();
            datas.put("products", products);
            datas.put("imageUrl", null);  // 预留
            datas.put("recognitionTime", System.currentTimeMillis());
            datas.put("success", true);
            datas.put("message", "识别成功，共识别到 " + products.size() + " 种商品");
            datas.put("quantity", 1);
            
            response.put("datas", datas);
            
            return response;
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "识别失败：" + e.getMessage());
            return response;
        }
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
