package com.report.service.impl;

import com.report.dto.ProductRecognitionRequest;
import com.report.dto.ProductRecognitionResult;
import com.report.service.ProductRecognitionService;
import com.report.util.AliyunVisionClient;
import com.report.util.TencentCloudClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * 商品识别服务实现（支持多平台）
 */
@Service("productRecognitionService")
public class ProductRecognitionServiceImpl implements ProductRecognitionService {
    
    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private AliyunVisionClient aliyunVisionClient;
    
    @Autowired(required = false)
    private TencentCloudClient tencentCloudClient;
    
    @Override
    public ProductRecognitionResult recognize(MultipartFile image) {
        ProductRecognitionResult result = new ProductRecognitionResult();
        result.setConfidence(0.0);
        
        try {
            // 1. 获取平台类型，决定调用哪个 API
            String platform = getRecognitionPlatform();
            System.out.println("🔍 识别平台类型：" + platform);
            
            Map<String, Object> recognitionResult = null;
            
            if ("TENCENT".equalsIgnoreCase(platform)) {
                // 调用腾讯云商品识别 API（直接使用 MultipartFile）
                recognitionResult = tencentCloudClient.recognizeProductFromMultipart(image);
            } else {
                // 默认调用阿里云商品识别 API（直接使用 MultipartFile）
                recognitionResult = aliyunVisionClient.recognizeProductFromMultipart(image);
            }
            
            // 3. 解析识别结果
            if (recognitionResult != null && Boolean.TRUE.equals(recognitionResult.get("success"))) {
                String categoryName = (String) recognitionResult.get("category");
                String productName = (String) recognitionResult.get("productName");
                String categoryId = (String) recognitionResult.get("sourcePluno");  // 阿里返回的 CategoryId，腾讯云为 null
                Double confidence = (Double) recognitionResult.get("confidence");
                String platformSource = (String) recognitionResult.get("platform");  // 识别平台
                
                System.out.println("🎯 识别结果：类目=" + categoryName + ", CategoryId=" + categoryId + ", 置信度=" + confidence + ", 平台=" + platformSource);
                
                // 4. 匹配本地训练库
                String localPluno = matchLocalProduct(categoryName, productName);
                
                if (localPluno != null) {
                    // 匹配到本地商品
                    System.out.println("✅ 匹配到本地商品：PLUNO=" + localPluno);
                    
                    // 查询本地商品信息
                    Map<String, Object> localProduct = getProductInfo(localPluno);
                    
                    result.setPluno(localPluno);
                    result.setSourcePluno(categoryId);  // 保存识别平台返回的原始 ID
                    result.setProductName(localProduct != null ? (String) localProduct.get("PRODUCT_NAME") : productName);
                    result.setCategory(localProduct != null ? (String) localProduct.get("CATEGORY") : categoryName);
                    result.setConfidence(confidence != null ? confidence : 0.85);
                    result.setRecognitionSource("LOCAL_MATCH");
                } else {
                    // 未匹配到本地商品，使用识别平台结果（品号为空，表示没有真实品号）
                    System.out.println("⚠️ 未匹配到本地商品，使用识别平台结果（无真实品号）");
                    
                    result.setPluno(null);  // 品号为空，表示没有真实品号
                    result.setSourcePluno(categoryId);  // 保存识别平台返回的原始 ID
                    result.setProductName(productName != null ? productName : "识别商品");
                    result.setCategory(categoryName != null ? categoryName : "通用商品");
                    result.setConfidence(confidence != null ? confidence : 0.85);
                    result.setRecognitionSource(platformSource != null ? platformSource : platform);
                }
                
                System.out.println("🎯 最终识别结果：品号=" + result.getPluno() + ", 来源=" + result.getRecognitionSource());
            }
            
        } catch (Exception e) {
            System.err.println("识别异常：" + e.getMessage());
            e.printStackTrace();
        }
        
        // 【新增】5. 自动记录识别日志
        logRecognition(result);
        
        return result;
    }
    
    /**
     * 自动记录识别日志
     * @param result 识别结果
     */
    private void logRecognition(ProductRecognitionResult result) {
        if (jdbcTemplate == null) {
            return;
        }
        
        try {
            String logId = UUID.randomUUID().toString().replace("-", "");
            
            // 计算识别耗时（从开始到现在的毫秒数）
            long recognitionTime = System.currentTimeMillis();
            
            // 插入识别日志
            String sql = "INSERT INTO PRODUCT_RECOGNITION_LOGS " +
                "(LOG_ID, IMAGE_URL, RECOGNIZED_PLUNO, RECOGNIZED_NAME, CONFIDENCE, " +
                "USERCONFIRMEDPLUNO, IS_CORRECT, RECOGNITION_TIME, DEVICE_TYPE, USER_ID, CREATED_TIME) " +
                "VALUES (?, ?, ?, ?, ?, ?, 'U', ?, 'WEB', 'system', SYSDATE)";
            
            jdbcTemplate.update(sql,
                logId,
                "local-image",  // 不使用 OSS，使用本地标识
                result.getSourcePluno(),      // RECOGNIZED_PLUNO: 阿里返回的 sourcePluno（CategoryId）
                result.getProductName(),
                result.getConfidence(),
                result.getPluno(),            // USERCONFIRMEDPLUNO: 匹配到的真实品号
                recognitionTime
            );
            
            System.out.println("📝 识别日志已记录：" + logId + 
                ", 识别品号=" + result.getSourcePluno() + 
                ", 匹配品号=" + result.getPluno());
            
        } catch (Exception e) {
            System.err.println("记录识别日志失败：" + e.getMessage());
            e.printStackTrace();
            // 日志记录失败不影响识别结果
        }
    }
    
    /**
     * 匹配本地训练库商品
     * @param categoryName 阿里云识别的类目名称
     * @param productName 阿里云识别的商品名称
     * @return 匹配的本地 PLUNO，未匹配返回 null
     */
    private String matchLocalProduct(String categoryName, String productName) {
        if (jdbcTemplate == null) {
            return null;
        }
        
        try {
            // 策略 1: 精确匹配商品名称
            if (productName != null && !productName.trim().isEmpty()) {
                String sql = "SELECT PLUNO FROM PRODUCT_FEATURES WHERE PRODUCT_NAME = ?";
                List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, productName);
                if (list != null && !list.isEmpty()) {
                    return (String) list.get(0).get("PLUNO");
                }
            }
            
            // 策略 2: 模糊匹配商品名称（包含匹配）
            if (productName != null && !productName.trim().isEmpty()) {
                String sql = "SELECT PLUNO FROM PRODUCT_FEATURES WHERE PRODUCT_NAME LIKE ?";
                List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, "%" + productName + "%");
                if (list != null && !list.isEmpty()) {
                    return (String) list.get(0).get("PLUNO");
                }
            }
            
            // 策略 3: 匹配类目名称
            if (categoryName != null && !categoryName.trim().isEmpty()) {
                String sql = "SELECT PLUNO FROM PRODUCT_FEATURES WHERE CATEGORY = ?";
                List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, categoryName);
                if (list != null && !list.isEmpty()) {
                    return (String) list.get(0).get("PLUNO");
                }
            }
            
            return null;
            
        } catch (Exception e) {
            System.err.println("匹配本地商品失败：" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 查询商品信息
     * @param pluno 商品品号
     * @return 商品信息 Map
     */
    private Map<String, Object> getProductInfo(String pluno) {
        if (jdbcTemplate == null || pluno == null) {
            return null;
        }
        
        try {
            String sql = "SELECT PLUNO, PRODUCT_NAME, CATEGORY FROM PRODUCT_FEATURES WHERE PLUNO = ?";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, pluno);
            if (list != null && !list.isEmpty()) {
                return list.get(0);
            }
            return null;
            
        } catch (Exception e) {
            System.err.println("查询商品信息失败：" + e.getMessage());
            return null;
        }
    }
    
    /**
     * 获取识别平台类型
     * @return 平台类型：ALI（默认）或 TENCENT
     */
    private String getRecognitionPlatform() {
        if (jdbcTemplate == null) {
            return "ALI";  // 默认使用阿里云
        }
        
        try {
            // 从 PRODUCT_APPKEY 表读取平台配置（专用配置记录）
            String sql = "SELECT ACCESSKEYID FROM PRODUCT_APPKEY WHERE PLATFORM = 'RECOGNIZE_PLATFORM'";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
            
            if (list != null && !list.isEmpty()) {
                String platform = (String) list.get(0).get("ACCESSKEYID");
                System.out.println("📋 读取识别平台配置：" + platform);
                return platform != null ? platform : "ALI";
            }
            
            return "ALI";  // 默认使用阿里云
            
        } catch (Exception e) {
            System.err.println("读取识别平台配置失败：" + e.getMessage());
            return "ALI";  // 出错时使用阿里云
        }
    }
    
    @Override
    public ProductRecognitionResult recognizeWithConfirmation(MultipartFile image, String confirmedPluno) {
        // 先调用识别
        ProductRecognitionResult result = recognize(image);
        
        // 记录识别日志
        if (jdbcTemplate != null && confirmedPluno != null) {
            try {
                String logId = UUID.randomUUID().toString().replace("-", "");
                // 根据实际表结构调整 SQL
                // 表字段：LOG_ID, IMAGE_URL, RECOGNIZED_PLUNO, RECOGNIZED_NAME, CONFIDENCE, 
                //        USERCONFIRMEDPLUNO, IS_CORRECT, RECOGNITION_TIME, DEVICE_TYPE, USER_ID, CREATED_TIME
                String sql = "INSERT INTO PRODUCT_RECOGNITION_LOGS " +
                    "(LOG_ID, IMAGE_URL, RECOGNIZED_PLUNO, RECOGNIZED_NAME, CONFIDENCE, " +
                    "USERCONFIRMEDPLUNO, IS_CORRECT, RECOGNITION_TIME, DEVICE_TYPE, USER_ID, CREATED_TIME) " +
                    "VALUES (?, ?, ?, ?, ?, ?, " +
                    "CASE WHEN ? = ? THEN 'Y' ELSE 'N' END, " +
                    "?, 'WEB', 'system', SYSDATE)";
                
                long recognitionTime = System.currentTimeMillis() - System.currentTimeMillis(); // 实际应该计算识别耗时
                
                jdbcTemplate.update(sql, 
                    logId,
                    "oss-image", // 实际应该是 OSS URL
                    result.getPluno(),
                    result.getProductName(),
                    result.getConfidence(),
                    confirmedPluno,
                    result.getPluno(),
                    confirmedPluno,
                    recognitionTime
                );
                
                System.out.println("📝 识别日志已记录：" + logId);
            } catch (Exception e) {
                System.err.println("记录识别日志失败：" + e.getMessage());
                e.printStackTrace();
            }
        }
        
        return result;
    }
    
    @Override
    public Map<String, Object> submitTrainingData(ProductRecognitionRequest request) {
        Map<String, Object> result = new HashMap<>();
        
        if (jdbcTemplate == null) {
            result.put("success", false);
            result.put("message", "数据库未连接");
            return result;
        }
        
        try {
            String sampleId = UUID.randomUUID().toString().replace("-", "");
            String fileName = System.currentTimeMillis() + ".jpg";
            String uploadDir = "/home/admin/.openclaw/workspace/REPORT/upload/products/" + request.getPluno();
            
            // 创建上传目录
            java.io.File dir = new java.io.File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            // 【重要】先上传到阿里云 OSS（在 transferTo 之前，MultipartFile 还在内存/临时文件中）
            String ossImageUrl = null;
            try {
                ossImageUrl = aliyunVisionClient.uploadImageToOSS(request.getImage(), request.getPluno());
                System.out.println("✅ 图片已上传到阿里云 OSS: " + ossImageUrl);
            } catch (Exception e) {
                e.printStackTrace();
                // OSS 上传失败不影响本地保存，记录日志即可
                System.err.println("⚠️ 阿里云 OSS 上传失败：" + e.getMessage());
            }
            
            // 保存图片文件到本地（在 OSS 上传之后）
            String filePath = uploadDir + "/" + fileName;
            request.getImage().transferTo(new java.io.File(filePath));
            
            String localImageUrl = "/upload/products/" + request.getPluno() + "/" + fileName;
            
            // 插入训练样本记录（包含 OSS_IMAGE_URL 字段）
            String sql = "INSERT INTO PRODUCT_TRAINING_SAMPLES " +
                "(SAMPLE_ID, PLUNO, PRODUCT_NAME, IMAGE_URL, OSS_IMAGE_URL, METADATA, CREATED_BY, CREATED_TIME) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, SYSDATE)";
            
            jdbcTemplate.update(sql, 
                sampleId,
                request.getPluno(),
                request.getProductName(),
                localImageUrl,
                ossImageUrl != null ? ossImageUrl : localImageUrl,  // OSS URL，如果为空则用本地 URL
                request.getMetadata(),
                "system"
            );
            
            // 更新商品特征表
            updateProductFeatures(request);
            
            result.put("success", true);
            result.put("sampleId", sampleId);
            result.put("imageUrl", localImageUrl);
            result.put("ossImageUrl", ossImageUrl);  // 【新增】返回 OSS URL
            result.put("message", "提交成功");
            
            return result;
            
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "提交失败：" + e.getMessage());
            return result;
        }
    }
    
    @Override
    public Map<String, Object> getProductFeatures(String pluno) {
        Map<String, Object> result = new HashMap<>();
        
        if (jdbcTemplate == null) {
            result.put("success", false);
            result.put("message", "数据库未连接");
            return result;
        }
        
        try {
            String sql = "SELECT FEATURE_ID, PLUNO, PRODUCT_NAME, CATEGORY, " +
                "IMAGE_COUNT, QUALITY_SCORE, CREATED_TIME " +
                "FROM PRODUCT_FEATURES WHERE PLUNO = ?";
            
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, pluno);
            
            if (list.isEmpty()) {
                result.put("success", false);
                result.put("message", "未找到该商品的特征数据");
                return result;
            }
            
            result.put("success", true);
            result.put("data", list.get(0));
            return result;
            
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
            return result;
        }
    }
    
    @Override
    public Map<String, Object> getTrainingStats() {
        Map<String, Object> stats = new HashMap<>();
        
        if (jdbcTemplate == null) {
            stats.put("success", false);
            stats.put("message", "数据库未连接");
            return stats;
        }
        
        try {
            // 已采集商品数
            String productCountSql = "SELECT COUNT(DISTINCT PLUNO) AS COUNT FROM PRODUCT_FEATURES";
            Integer productCount = jdbcTemplate.queryForObject(productCountSql, Integer.class);
            stats.put("productCount", productCount != null ? productCount : 0);
            
            // 图片总数
            String imageCountSql = "SELECT NVL(SUM(IMAGE_COUNT), 0) AS COUNT FROM PRODUCT_FEATURES";
            Integer imageCount = jdbcTemplate.queryForObject(imageCountSql, Integer.class);
            stats.put("imageCount", imageCount != null ? imageCount : 0);
            
            // 平均每个商品的图片数
            if (productCount != null && productCount > 0) {
                stats.put("avgImagesPerProduct", Math.round((double) imageCount / productCount * 100) / 100.0);
            } else {
                stats.put("avgImagesPerProduct", 0);
            }
            
            // 待训练样本数
            String pendingSql = "SELECT COUNT(*) AS COUNT FROM PRODUCT_TRAINING_SAMPLES WHERE USEDFORTRAINING = 'N'";
            Integer pendingSamples = jdbcTemplate.queryForObject(pendingSql, Integer.class);
            stats.put("pendingSamples", pendingSamples != null ? pendingSamples : 0);
            
            // 最后训练时间
            try {
                String lastTrainingSql = "SELECT TO_CHAR(MAX(TRAINING_DATE), 'YYYY-MM-DD HH24:MI') AS LAST_TRAINING FROM MODEL_VERSIONS";
                String lastTraining = jdbcTemplate.queryForObject(lastTrainingSql, String.class);
                stats.put("lastTrainingDate", lastTraining != null ? lastTraining : "未训练");
            } catch (Exception e) {
                stats.put("lastTrainingDate", "未训练");
            }
            
            // 当前模型准确率
            try {
                String accuracySql = "SELECT NVL(ACCURACY, 0) AS ACCURACY FROM MODEL_VERSIONS WHERE IS_ACTIVE = 'Y'";
                Double accuracy = jdbcTemplate.queryForObject(accuracySql, Double.class);
                stats.put("accuracy", accuracy != null ? Math.round(accuracy * 10000) / 100.0 : 0);
            } catch (Exception e) {
                stats.put("accuracy", 0);
            }
            
            stats.put("success", true);
            stats.put("message", "查询成功");
            
            return stats;
            
        } catch (Exception e) {
            e.printStackTrace();
            stats.put("success", false);
            stats.put("message", "查询失败：" + e.getMessage());
            return stats;
        }
    }
    
    @Override
    public String startTraining(Map<String, Object> config) {
        // TODO: 实现模型训练逻辑
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    @Override
    public Map<String, Object> getTrainingStatus(String taskId) {
        Map<String, Object> status = new HashMap<>();
        status.put("taskId", taskId);
        status.put("progress", 0);
        status.put("status", "pending");
        return status;
    }
    
    /**
     * 更新商品特征
     */
    private void updateProductFeatures(ProductRecognitionRequest request) {
        try {
            String checkSql = "SELECT COUNT(*) FROM PRODUCT_FEATURES WHERE PLUNO = ?";
            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, request.getPluno());
            
            if (count != null && count > 0) {
                String updateSql = "UPDATE PRODUCT_FEATURES " +
                    "SET IMAGE_COUNT = IMAGE_COUNT + 1, UPDATED_TIME = SYSDATE " +
                    "WHERE PLUNO = ?";
                jdbcTemplate.update(updateSql, request.getPluno());
            } else {
                String featureId = UUID.randomUUID().toString().replace("-", "");
                String insertSql = "INSERT INTO PRODUCT_FEATURES " +
                    "(FEATURE_ID, PLUNO, PRODUCT_NAME, CATEGORY, IMAGE_COUNT, CREATED_TIME, UPDATED_TIME) " +
                    "VALUES (?, ?, ?, ?, 1, SYSDATE, SYSDATE)";
                jdbcTemplate.update(insertSql, 
                    featureId,
                    request.getPluno(),
                    request.getProductName(),
                    request.getCategory()
                );
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
