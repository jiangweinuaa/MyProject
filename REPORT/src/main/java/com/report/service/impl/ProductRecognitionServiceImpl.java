package com.report.service.impl;

import com.report.dao.ImageFeatureDAO;
import com.report.dto.ImageFeature;
import com.report.dto.MatchResult;
import com.report.dto.ProductRecognitionRequest;
import com.report.dto.ProductRecognitionResult;
import com.report.service.FeatureSearchService;
import com.report.service.RecognitionConfigService;
import com.report.service.ImageFeatureExtractor;
import com.report.service.ProductRecognitionService;
import com.report.util.AliyunVisionClient;
import com.report.util.TencentCloudClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

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
    
    @Autowired(required = false)
    private ImageFeatureDAO imageFeatureDAO;
    
    @Autowired(required = false)
    private ImageFeatureExtractor featureExtractor;
    
    @Autowired(required = false)
    private FeatureSearchService featureSearchService;
    
    @Autowired(required = false)
    private RecognitionConfigService configService;
    
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
                
                // 4. 匹配本地训练库（使用特征向量匹配）
                // 【修复】先提取特征，获取实际使用的算法
                String actualAlgorithm = extractAndGetAlgorithm(image);
                result.setFeatureAlgorithm(actualAlgorithm);
                
                MatchResult matchResult = matchLocalProduct(image, categoryName, productName);
                
                if (matchResult != null && matchResult.getPluno() != null) {
                    // 匹配到本地商品
                    System.out.println("✅ 匹配到本地商品：PLUNO=" + matchResult.getPluno() + 
                        ", 匹配方式=" + matchResult.getMatchType() + 
                        (matchResult.getVectorSimilarity() != null ? ", 相似度=" + matchResult.getVectorSimilarity() : ""));
                    
                    // 查询本地商品信息
                    Map<String, Object> localProduct = getProductInfo(matchResult.getPluno());
                    
                    result.setPluno(matchResult.getPluno());
                    result.setSourcePluno(categoryId);  // 保存识别平台返回的原始 ID
                    result.setProductName(localProduct != null ? (String) localProduct.get("PRODUCT_NAME") : productName);
                    result.setCategory(localProduct != null ? (String) localProduct.get("CATEGORY") : categoryName);
                    
                    // 【优化】CONFIDENCE 取值逻辑：
                    // 1. 有向量相似度 → 使用向量相似度
                    // 2. 无向量相似度（名称匹配） → 使用阿里云置信度
                    // 3. 阿里云也未返回 → 使用默认值 0.85
                    if (matchResult.getVectorSimilarity() != null) {
                        result.setConfidence(matchResult.getVectorSimilarity());
                    } else if (confidence != null) {
                        result.setConfidence(confidence);
                    } else {
                        result.setConfidence(0.85);
                    }
                    
                    result.setRecognitionSource("LOCAL_MATCH");
                    // 【修复】VECTOR_FALLBACK 实际是名称匹配成功，但记录了向量相似度
                    if ("VECTOR_FALLBACK".equals(matchResult.getMatchType())) {
                        result.setMatchType("NAME_EXACT");  // 实际匹配置信度
                    } else {
                        result.setMatchType(matchResult.getMatchType());
                    }
                    result.setVectorSimilarity(matchResult.getVectorSimilarity());  // 始终记录向量相似度
                    result.setMatchedFeatureId(matchResult.getFeatureId());  // 始终记录特征 ID
                } else {
                    // 未匹配到本地商品，使用识别平台结果（品号为空，表示没有真实品号）
                    System.out.println("⚠️ 未匹配到本地商品，使用识别平台结果（无真实品号）");
                    
                    result.setPluno(null);  // 品号为空，表示没有真实品号
                    result.setSourcePluno(categoryId);  // 保存识别平台返回的原始 ID
                    result.setProductName(productName != null ? productName : "识别商品");
                    result.setCategory(categoryName != null ? categoryName : "通用商品");
                    result.setConfidence(confidence != null ? confidence : 0.85);
                    result.setRecognitionSource(platformSource != null ? platformSource : platform);
                    
                    // 【修复】记录匹配失败原因
                    if (matchResult == null) {
                        result.setMatchType("NO_TRAINING_DATA");  // 特征库为空
                    } else {
                        result.setMatchType("NO_MATCH");  // 所有策略都失败
                    }
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
            
            // 插入识别日志（包含匹配方式、相似度、特征 ID 和算法）
            String sql = "INSERT INTO PRODUCT_RECOGNITION_LOGS " +
                "(LOG_ID, IMAGE_URL, RECOGNIZED_PLUNO, RECOGNIZED_NAME, CONFIDENCE, " +
                "USERCONFIRMEDPLUNO, IS_CORRECT, RECOGNITION_TIME, DEVICE_TYPE, USER_ID, " +
                "MATCH_TYPE, VECTOR_SIMILARITY, MATCHED_FEATURE_ID, FEATURE_ALGORITHM, CREATED_TIME) " +
                "VALUES (?, ?, ?, ?, ?, ?, 'U', ?, 'WEB', 'system', ?, ?, ?, ?, SYSDATE)";
            
            jdbcTemplate.update(sql,
                logId,
                "local-image",  // 不使用 OSS，使用本地标识
                result.getSourcePluno(),      // RECOGNIZED_PLUNO: 阿里返回的 sourcePluno（CategoryId）
                result.getProductName(),
                result.getConfidence(),
                result.getPluno(),            // USERCONFIRMEDPLUNO: 匹配到的真实品号
                recognitionTime,
                result.getMatchType(),        // 匹配方式：VECTOR/NAME_EXACT/NAME_FUZZY/CATEGORY
                result.getVectorSimilarity(), // 向量相似度
                result.getMatchedFeatureId(), // 匹配到的特征 ID
                result.getFeatureAlgorithm()  // 使用的特征算法
            );
            
            System.out.println("📝 识别日志已记录：" + logId + 
                ", 识别品号=" + result.getSourcePluno() + 
                ", 匹配品号=" + result.getPluno() +
                ", 匹配方式=" + result.getMatchType() +
                (result.getVectorSimilarity() != null ? ", 相似度=" + result.getVectorSimilarity() : ""));
            
        } catch (Exception e) {
            System.err.println("记录识别日志失败：" + e.getMessage());
            e.printStackTrace();
            // 日志记录失败不影响识别结果
        }
    }
    
    /**
     * 提取特征并获取实际使用的算法
     * @param imageFile 图片文件
     * @return 实际使用的算法名称
     */
    private String extractAndGetAlgorithm(MultipartFile imageFile) {
        if (featureExtractor == null) {
            return "HISTOGRAM";
        }
        
        try {
            // 读取配置
            String configuredAlgorithm = configService != null ? 
                configService.getConfig("FEATURE_ALGORITHM", "HISTOGRAM") : "HISTOGRAM";
            
            // 提取特征（ImageFeatureExtractor 会处理降级逻辑）
            byte[] imageBytes = imageFile.getBytes();
            float[] features = featureExtractor.extractFeatures(imageBytes);
            
            // 根据特征维度判断实际使用的算法
            if (features != null) {
                if (features.length == 512) {
                    return "RESNET50";
                } else if (features.length == 6912) {
                    return "HISTOGRAM_GRID";
                } else {
                    return "HISTOGRAM";
                }
            }
            
            return configuredAlgorithm;
            
        } catch (Exception e) {
            System.err.println("⚠️ 提取特征失败：" + e.getMessage());
            return "HISTOGRAM";
        }
    }
    
    /**
     * 匹配本地训练库商品（使用特征向量相似度匹配）
     * @param imageFile 上传的图片文件
     * @param categoryName 阿里云识别的类目名称
     * @param productName 阿里云识别的商品名称
     * @return MatchResult 包含匹配品号、匹配方式和相似度，未匹配返回 null
     */
    private MatchResult matchLocalProduct(MultipartFile imageFile, String categoryName, String productName) {
        if (jdbcTemplate == null || featureSearchService == null || featureExtractor == null) {
            return null;
        }
        
        try {
            // 1. 提取上传图片的特征向量
            byte[] imageBytes = imageFile.getBytes();
            float[] queryFeatures = featureExtractor.extractFeatures(imageBytes);
            
            if (queryFeatures == null) {
                System.out.println("⚠️ 特征提取失败，使用名称匹配");
                return matchLocalProductByName(categoryName, productName);
            }
            
            // 2. 使用特征向量检索最相似的商品
            List<Map<String, Object>> similarProducts = featureSearchService.searchSimilarProducts(queryFeatures, 1);
            
            if (similarProducts != null && !similarProducts.isEmpty()) {
                Map<String, Object> topMatch = similarProducts.get(0);
                double similarity = (Double) topMatch.get("similarity");
                String pluno = (String) topMatch.get("pluno");
                String featureId = (String) topMatch.get("featureId");
                
                System.out.println("✅ 特征匹配成功：PLUNO=" + pluno + ", 相似度=" + similarity + ", 特征 ID=" + featureId);
                
                // 【配置化】从配置表读取阈值（每次识别前刷新配置）
                if (configService != null) {
                    configService.refreshConfig();  // 重新加载配置
                }
                double highThreshold = configService != null ? 
                    configService.getDoubleConfig("VECTOR_HIGH_THRESHOLD", 0.85) : 0.85;
                double minThreshold = configService != null ? 
                    configService.getDoubleConfig("VECTOR_MIN_THRESHOLD", 0.60) : 0.60;
                
                if (similarity > highThreshold) {
                    // 向量匹配成功（高置信度）
                    System.out.println("✅ 向量匹配成功：PLUNO=" + pluno + ", 相似度=" + similarity + " (阈值=" + highThreshold + ")");
                    return MatchResult.vectorMatch(pluno, similarity, featureId);
                    
                } else if (similarity >= minThreshold) {
                    // 相似度中等（minThreshold-highThreshold），尝试名称匹配
                    System.out.println("⚠️ 相似度中等 (" + similarity + ", 阈值范围=" + minThreshold + "-" + highThreshold + ")，尝试名称匹配");
                    MatchResult nameMatch = matchLocalProductByName(categoryName, productName);
                    
                    if (nameMatch != null) {
                        // 名称匹配成功，返回名称匹配结果，但记录向量信息
                        System.out.println("✅ 名称匹配成功：PLUNO=" + nameMatch.getPluno() + ", 匹配方式=" + nameMatch.getMatchType());
                        return new MatchResult(
                            nameMatch.getPluno(),
                            "VECTOR_FALLBACK",
                            similarity,
                            featureId
                        );
                    } else {
                        // 名称匹配失败，返回 null（不强行匹配）
                        System.out.println("⚠️ 名称匹配也失败，不强行匹配");
                        return null;
                    }
                    
                } else {
                    // 相似度太低（< minThreshold），特征库中没有相似商品
                    System.out.println("⚠️ 相似度太低 (" + similarity + " < " + minThreshold + ")，特征库中无相似商品");
                    // 尝试名称匹配，但**保留向量信息用于记录**
                    MatchResult nameMatch = matchLocalProductByName(categoryName, productName);
                    
                    if (nameMatch != null) {
                        // 名称匹配成功，返回名称匹配结果，但记录向量信息
                        System.out.println("✅ 名称匹配成功：PLUNO=" + nameMatch.getPluno() + ", 向量相似度=" + similarity);
                        return new MatchResult(
                            nameMatch.getPluno(),
                            nameMatch.getMatchType(),  // NAME_EXACT/NAME_FUZZY/CATEGORY
                            similarity,  // 保留向量相似度
                            featureId    // 保留特征 ID
                        );
                    } else {
                        // 名称匹配也失败
                        return null;
                    }
                }
            } else {
                System.out.println("⚠️ 特征库为空，使用名称匹配");
                return matchLocalProductByName(categoryName, productName);
            }
            
        } catch (Exception e) {
            System.err.println("特征匹配失败：" + e.getMessage());
            e.printStackTrace();
            return matchLocalProductByName(categoryName, productName);
        }
    }
    
    /**
     * 匹配本地训练库商品（使用名称匹配，备用方案）
     * @param categoryName 阿里云识别的类目名称
     * @param productName 阿里云识别的商品名称
     * @return MatchResult 包含匹配品号和匹配方式，未匹配返回 null
     */
    private MatchResult matchLocalProductByName(String categoryName, String productName) {
        if (jdbcTemplate == null) {
            return null;
        }
        
        try {
            // 策略 1: 精确匹配商品名称
            if (productName != null && !productName.trim().isEmpty()) {
                String sql = "SELECT PLUNO FROM PRODUCT_FEATURES WHERE PRODUCT_NAME = ?";
                List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, productName);
                if (list != null && !list.isEmpty()) {
                    String pluno = (String) list.get(0).get("PLUNO");
                    System.out.println("✅ 名称精确匹配成功：PLUNO=" + pluno);
                    return MatchResult.nameExactMatch(pluno);
                }
            }
            
            // 策略 2: 模糊匹配商品名称（包含匹配）
            if (productName != null && !productName.trim().isEmpty()) {
                String sql = "SELECT PLUNO FROM PRODUCT_FEATURES WHERE PRODUCT_NAME LIKE ?";
                List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, "%" + productName + "%");
                if (list != null && !list.isEmpty()) {
                    String pluno = (String) list.get(0).get("PLUNO");
                    System.out.println("✅ 名称模糊匹配成功：PLUNO=" + pluno);
                    return MatchResult.nameFuzzyMatch(pluno);
                }
            }
            
            // 策略 3: 匹配类目名称
            if (categoryName != null && !categoryName.trim().isEmpty()) {
                String sql = "SELECT PLUNO FROM PRODUCT_FEATURES WHERE CATEGORY = ?";
                List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, categoryName);
                if (list != null && !list.isEmpty()) {
                    String pluno = (String) list.get(0).get("PLUNO");
                    System.out.println("✅ 类目匹配成功：PLUNO=" + pluno + ", 类目=" + categoryName);
                    return MatchResult.categoryMatch(pluno);
                }
            }
            
            System.out.println("⚠️ 所有匹配策略都失败了");
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
            
            // 【新增】提取并保存图像特征
            boolean featureExtracted = false;
            try {
                System.out.println("🔍 开始提取图像特征...");
                extractAndSaveFeatures(request.getPluno(), localImageUrl, ossImageUrl, filePath);
                featureExtracted = true;
            } catch (Exception e) {
                System.err.println("⚠️ 特征提取失败：" + e.getMessage());
                e.printStackTrace();
                // 特征提取失败不影响主流程
            }
            
            // 更新训练样本表的特征提取标记
            if (featureExtracted) {
                try {
                    // 查询刚保存的特征 ID（使用 OSS_IMAGE_URL 查询）
                    String featureIdSql = "SELECT FEATURE_ID FROM PRODUCT_IMAGE_FEATURES " +
                        "WHERE PLUNO = ? AND OSS_IMAGE_URL = ? ORDER BY CREATED_TIME DESC";
                    String featureId = jdbcTemplate.queryForObject(featureIdSql, String.class, 
                        request.getPluno(), ossImageUrl);
                    
                    String updateSql = "UPDATE PRODUCT_TRAINING_SAMPLES SET " +
                        "FEATURE_EXTRACTED = 'Y', " +
                        "FEATURE_ID = ?, " +
                        "FEATURE_EXTRACT_TIME = SYSDATE " +
                        "WHERE SAMPLE_ID = ?";
                    jdbcTemplate.update(updateSql, featureId, sampleId);
                    System.out.println("✅ 训练样本特征提取标记已更新，FEATURE_ID=" + featureId);
                } catch (Exception e) {
                    System.err.println("⚠️ 更新特征提取标记失败：" + e.getMessage());
                    e.printStackTrace();
                }
            }
            
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
            
            // 计算平均相似度（图片质量指标）
            try {
                String sql = "SELECT " +
                    "ROUND(AVG(VECTOR_SIMILARITY), 4) AS AVG_SIMILARITY, " +
                    "COUNT(*) AS TOTAL " +
                    "FROM PRODUCT_RECOGNITION_LOGS " +
                    "WHERE VECTOR_SIMILARITY IS NOT NULL";
                
                List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
                
                if (list != null && !list.isEmpty()) {
                    Map<String, Object> row = list.get(0);
                    Number avgSim = (Number) row.get("AVG_SIMILARITY");
                    Number total = (Number) row.get("TOTAL");
                    
                    // 平均相似度作为准确率显示（0-100）
                    stats.put("accuracy", avgSim != null ? Math.round(avgSim.doubleValue() * 10000) / 100.0 : 0);
                    stats.put("totalRecognitions", total != null ? total.intValue() : 0);
                    stats.put("correctRecognitions", 0);
                    stats.put("avgSimilarity", avgSim != null ? avgSim.doubleValue() : 0);
                    
                    System.out.println("📊 平均相似度：" + avgSim + " (总数：" + total + ")");
                } else {
                    stats.put("accuracy", 0);
                    stats.put("totalRecognitions", 0);
                    stats.put("correctRecognitions", 0);
                    stats.put("avgSimilarity", 0);
                }
                
            } catch (Exception e) {
                System.err.println("⚠️ 计算相似度失败：" + e.getMessage());
                stats.put("accuracy", 0);
                stats.put("totalRecognitions", 0);
                stats.put("correctRecognitions", 0);
                stats.put("avgSimilarity", 0);
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
     * 获取指定商品的平均相似度（图片质量指标）
     * @param pluno 商品品号
     * @return 相似度统计
     */
    public Map<String, Object> getProductAccuracy(String pluno) {
        Map<String, Object> result = new HashMap<>();
        
        if (jdbcTemplate == null || pluno == null) {
            result.put("accuracy", 0);
            result.put("total", 0);
            result.put("correct", 0);
            result.put("avgSimilarity", 0);
            return result;
        }
        
        try {
            String sql = "SELECT " +
                "ROUND(AVG(VECTOR_SIMILARITY), 4) AS AVG_SIMILARITY, " +
                "COUNT(*) AS TOTAL " +
                "FROM PRODUCT_RECOGNITION_LOGS " +
                "WHERE VECTOR_SIMILARITY IS NOT NULL " +
                "AND USERCONFIRMEDPLUNO = ?";
            
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, pluno);
            
            if (list != null && !list.isEmpty()) {
                Map<String, Object> row = list.get(0);
                Number avgSim = (Number) row.get("AVG_SIMILARITY");
                Number total = (Number) row.get("TOTAL");
                
                // 平均相似度作为准确率显示（0-100）
                result.put("accuracy", avgSim != null ? Math.round(avgSim.doubleValue() * 10000) / 100.0 : 0);
                result.put("total", total != null ? total.intValue() : 0);
                result.put("correct", 0);
                result.put("avgSimilarity", avgSim != null ? avgSim.doubleValue() : 0);
                
            } else {
                result.put("accuracy", 0);
                result.put("total", 0);
                result.put("correct", 0);
                result.put("avgSimilarity", 0);
            }
            
        } catch (Exception e) {
            System.err.println("⚠️ 查询商品相似度失败：" + e.getMessage());
            result.put("accuracy", 0);
            result.put("total", 0);
            result.put("correct", 0);
            result.put("avgSimilarity", 0);
        }
        
        return result;
    }
    
    // 批量训练任务管理
    private final Map<String, RetrainTask> retrainTasks = new ConcurrentHashMap<>();
    
    /**
     * 批量重新训练所有商品特征
     */
    @Override
    public String retrainAllFeatures() {
        String taskId = UUID.randomUUID().toString().replace("-", "");
        
        RetrainTask task = new RetrainTask();
        task.taskId = taskId;
        task.status = "RUNNING";
        task.total = 0;
        task.processed = 0;
        task.success = 0;
        task.failed = 0;
        task.startTime = System.currentTimeMillis();
        
        retrainTasks.put(taskId, task);
        
        // 异步执行训练任务
        CompletableFuture.runAsync(() -> {
            try {
                // 1. 查询所有训练样本
                String sql = "SELECT SAMPLE_ID, PLUNO, PRODUCT_NAME, OSS_IMAGE_URL, FEATURE_EXTRACTED, FEATURE_ID " +
                    "FROM PRODUCT_TRAINING_SAMPLES " +
                    "WHERE OSS_IMAGE_URL IS NOT NULL " +
                    "ORDER BY PLUNO, CREATED_TIME";
                
                List<Map<String, Object>> samples = jdbcTemplate.queryForList(sql);
                task.total = samples.size();
                
                System.out.println("🔄 开始批量训练，共 " + task.total + " 个样本");
                
                // 2. 逐个处理样本
                for (Map<String, Object> sample : samples) {
                    if (!"RUNNING".equals(task.status)) {
                        break;  // 任务被取消
                    }
                    
                    String sampleId = (String) sample.get("SAMPLE_ID");
                    String pluno = (String) sample.get("PLUNO");
                    String ossImageUrl = (String) sample.get("OSS_IMAGE_URL");
                    String featureId = (String) sample.get("FEATURE_ID");
                    
                    try {
                        // 3. 从 OSS 下载图片
                        byte[] imageData = aliyunVisionClient.downloadImageFromOSS(ossImageUrl);
                        
                        if (imageData == null || imageData.length == 0) {
                            task.failed++;
                            task.processed++;
                            System.err.println("⚠️ 下载 OSS 图片失败：" + ossImageUrl);
                            continue;
                        }
                        
                        // 4. 提取特征
                        float[] features = featureExtractor.extractFeatures(imageData);
                        
                        if (features == null) {
                            task.failed++;
                            task.processed++;
                            System.err.println("⚠️ 特征提取失败：" + pluno);
                            continue;
                        }
                        
                        // 5. 更新 PRODUCT_IMAGE_FEATURES 表
                        if (featureId != null && !featureId.trim().isEmpty()) {
                            updateFeatureVector(featureId, features);
                        } else {
                            // 如果没有 featureId，创建新记录
                            featureId = createNewFeature(pluno, features, ossImageUrl);
                        }
                        
                        // 6. 更新 PRODUCT_TRAINING_SAMPLES 表
                        updateSampleFeatureExtracted(sampleId, featureId);
                        
                        task.success++;
                        task.processed++;
                        
                        System.out.println("✅ 训练成功：" + pluno + ", 特征维度=" + features.length);
                        
                    } catch (Exception e) {
                        task.failed++;
                        task.processed++;
                        System.err.println("❌ 训练失败：" + pluno + ", " + e.getMessage());
                        e.printStackTrace();
                    }
                }
                
                // 7. 任务完成
                task.status = "COMPLETED";
                task.endTime = System.currentTimeMillis();
                task.duration = task.endTime - task.startTime;
                
                System.out.println("✅ 批量训练完成，总计=" + task.total + 
                    ", 成功=" + task.success + ", 失败=" + task.failed + 
                    ", 耗时=" + (task.duration / 1000) + "秒");
                
            } catch (Exception e) {
                task.status = "FAILED";
                task.endTime = System.currentTimeMillis();
                System.err.println("❌ 批量训练异常：" + e.getMessage());
                e.printStackTrace();
            }
        });
        
        return taskId;
    }
    
    /**
     * 获取批量训练进度
     */
    @Override
    public Map<String, Object> getRetrainProgress(String taskId) {
        RetrainTask task = retrainTasks.get(taskId);
        
        if (task == null) {
            return Map.of(
                "success", false,
                "message", "任务不存在"
            );
        }
        
        int progress = task.total > 0 ? (int)(task.processed * 100.0 / task.total) : 0;
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("taskId", task.taskId);
        result.put("status", task.status);
        result.put("total", task.total);
        result.put("processed", task.processed);
        result.put("trainSuccess", task.success);  // 改名避免冲突
        result.put("failed", task.failed);
        result.put("progress", progress);
        result.put("startTime", task.startTime);
        result.put("endTime", task.endTime != null ? task.endTime : 0);
        result.put("duration", task.duration != null ? task.duration : 0);
        
        return result;
    }
    
    /**
     * 取消批量训练任务
     */
    @Override
    public Map<String, Object> cancelRetrainTask(String taskId) {
        RetrainTask task = retrainTasks.get(taskId);
        
        if (task == null) {
            return Map.of(
                "success", false,
                "message", "任务不存在"
            );
        }
        
        if ("RUNNING".equals(task.status)) {
            task.status = "CANCELLED";
            return Map.of(
                "success", true,
                "message", "任务已取消"
            );
        }
        
        return Map.of(
            "success", false,
            "message", "任务已结束，无法取消"
        );
    }
    
    /**
     * 更新特征向量
     */
    private void updateFeatureVector(String featureId, float[] features) {
        String sql = "UPDATE PRODUCT_IMAGE_FEATURES SET " +
            "FEATURE_VECTOR = ?, " +
            "FEATURE_DIMENSION = ?, " +
            "MODEL_VERSION = ?, " +
            "CREATED_TIME = SYSDATE " +
            "WHERE FEATURE_ID = ?";
        
        try {
            // float[] 转 byte[]
            byte[] featureBytes = new byte[features.length * 4];
            java.nio.ByteBuffer buffer = java.nio.ByteBuffer.wrap(featureBytes);
            buffer.order(java.nio.ByteOrder.LITTLE_ENDIAN);
            for (float f : features) {
                buffer.putFloat(f);
            }
            
            // 确定模型版本
            String modelVersion;
            if (features.length == 512) {
                modelVersion = "RESNET50-v1";
            } else if (features.length == 6912) {
                modelVersion = "HISTOGRAM_GRID-3x3";
            } else {
                modelVersion = "HISTOGRAM-v1";
            }
            
            jdbcTemplate.update(sql, featureBytes, features.length, modelVersion, featureId);
            
        } catch (Exception e) {
            System.err.println("❌ 更新特征向量失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 创建新的特征记录
     */
    private String createNewFeature(String pluno, float[] features, String ossImageUrl) {
        String newFeatureId = UUID.randomUUID().toString().replace("-", "");
        
        String sql = "INSERT INTO PRODUCT_IMAGE_FEATURES (" +
            "FEATURE_ID, PLUNO, OSS_IMAGE_URL, FEATURE_VECTOR, FEATURE_DIMENSION, " +
            "MODEL_VERSION, CREATED_TIME, CREATED_BY) " +
            "VALUES (?, ?, ?, ?, ?, ?, SYSDATE, 'system')";
        
        try {
            // float[] 转 byte[]
            byte[] featureBytes = new byte[features.length * 4];
            java.nio.ByteBuffer buffer = java.nio.ByteBuffer.wrap(featureBytes);
            buffer.order(java.nio.ByteOrder.LITTLE_ENDIAN);
            for (float f : features) {
                buffer.putFloat(f);
            }
            
            // 确定模型版本
            String modelVersion;
            if (features.length == 512) {
                modelVersion = "RESNET50-v1";
            } else if (features.length == 6912) {
                modelVersion = "HISTOGRAM_GRID-3x3";
            } else {
                modelVersion = "HISTOGRAM-v1";
            }
            
            jdbcTemplate.update(sql, newFeatureId, pluno, ossImageUrl, featureBytes, features.length, modelVersion);
            
            return newFeatureId;
            
        } catch (Exception e) {
            System.err.println("❌ 创建特征记录失败：" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 更新训练样本的特征提取标记
     */
    private void updateSampleFeatureExtracted(String sampleId, String featureId) {
        String sql = "UPDATE PRODUCT_TRAINING_SAMPLES SET " +
            "FEATURE_EXTRACTED = 'Y', " +
            "FEATURE_ID = ?, " +
            "FEATURE_EXTRACT_TIME = SYSDATE " +
            "WHERE SAMPLE_ID = ?";
        
        try {
            jdbcTemplate.update(sql, featureId, sampleId);
        } catch (Exception e) {
            System.err.println("❌ 更新训练样本标记失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 批量训练任务
     */
    private static class RetrainTask {
        String taskId;
        String status;  // RUNNING, COMPLETED, FAILED, CANCELLED
        int total;
        int processed;
        int success;
        int failed;
        Long startTime;
        Long endTime;
        Long duration;
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
    
    /**
     * 提取并保存图像特征
     */
    private void extractAndSaveFeatures(String pluno, String localImageUrl, String ossImageUrl, String filePath) {
        try {
            // 读取图片文件
            byte[] imageData = Files.readAllBytes(Paths.get(filePath));
            
            // 提取特征向量
            float[] features = featureExtractor.extractFeatures(imageData);
            
            if (features != null) {
                // 【修复】根据特征维度确定真实的模型版本
                String modelVersion;
                if (features.length == 512) {
                    modelVersion = "RESNET50-v1";
                } else if (features.length == 6912) {
                    modelVersion = "HISTOGRAM_GRID-3x3";
                } else {
                    modelVersion = "HISTOGRAM-v1";
                }
                
                // 保存特征
                ImageFeature feature = new ImageFeature();
                feature.setFeatureId(UUID.randomUUID().toString().replace("-", ""));
                feature.setPluno(pluno);
                feature.setImageUrl(localImageUrl);
                feature.setOssImageUrl(ossImageUrl);
                feature.setFeatureVector(features);
                feature.setFeatureDimension(features.length);
                feature.setModelVersion(modelVersion);  // 【修复】记录真实模型版本
                feature.setCreatedBy("system");
                
                imageFeatureDAO.saveFeature(feature);
                
                // 更新商品特征数量
                imageFeatureDAO.updateFeatureCount(pluno);
                
                System.out.println("✅ 特征提取并保存成功，模型版本=" + modelVersion);
            }
        } catch (Exception e) {
            System.err.println("⚠️ 特征提取失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
}
