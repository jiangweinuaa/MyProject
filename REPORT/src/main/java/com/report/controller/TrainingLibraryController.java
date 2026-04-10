package com.report.controller;

import com.report.dto.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * 训练库管理控制器
 */
@RestController
@RequestMapping("/api/training")
@CrossOrigin(origins = "*")
public class TrainingLibraryController {
    
    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;
    
    /**
     * 获取训练库统计数据
     */
    @GetMapping("/stats")
    public Map<String, Object> getTrainingStats() {
        Map<String, Object> result = new HashMap<>();
        
        if (jdbcTemplate == null) {
            result.put("success", false);
            result.put("message", "数据库未连接");
            return result;
        }
        
        try {
            // 训练商品数
            String productCountSql = "SELECT COUNT(DISTINCT PLUNO) AS COUNT FROM PRODUCT_FEATURES";
            Integer productCount = jdbcTemplate.queryForObject(productCountSql, Integer.class);
            result.put("productCount", productCount != null ? productCount : 0);
            
            // 训练图片数
            String imageCountSql = "SELECT NVL(SUM(IMAGE_COUNT), 0) AS COUNT FROM PRODUCT_FEATURES";
            Integer imageCount = jdbcTemplate.queryForObject(imageCountSql, Integer.class);
            result.put("imageCount", imageCount != null ? imageCount : 0);
            
            // 平均每商品图片数
            if (productCount != null && productCount > 0) {
                result.put("avgImagesPerProduct", Math.round((double) imageCount / productCount * 100) / 100.0);
            } else {
                result.put("avgImagesPerProduct", 0);
            }
            
            // 模型准确率（从 MODEL_VERSIONS 表获取）
            String accuracySql = "SELECT NVL(ACCURACY, 0) AS ACCURACY FROM MODEL_VERSIONS WHERE IS_ACTIVE = 'Y'";
            Double accuracy = jdbcTemplate.queryForObject(accuracySql, Double.class);
            result.put("accuracy", accuracy != null ? Math.round(accuracy * 10000) / 100.0 : 0);
            
            result.put("success", true);
            result.put("message", "查询成功");
            
            return result;
            
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
            return result;
        }
    }
    
    /**
     * 获取训练数据列表
     */
    @GetMapping("/list")
    public Map<String, Object> getTrainingList(
            @RequestParam(value = "pluno", required = false) String pluno,
            @RequestParam(value = "productName", required = false) String productName,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {
        
        Map<String, Object> result = new HashMap<>();
        
        if (jdbcTemplate == null) {
            result.put("success", false);
            result.put("message", "数据库未连接");
            return result;
        }
        
        try {
            // 构建查询条件
            StringBuilder whereClause = new StringBuilder();
            List<Object> params = new ArrayList<>();
            
            if (pluno != null && !pluno.trim().isEmpty()) {
                whereClause.append(" AND PLUNO LIKE ?");
                params.add("%" + pluno + "%");
            }
            
            if (productName != null && !productName.trim().isEmpty()) {
                whereClause.append(" AND PRODUCT_NAME LIKE ?");
                params.add("%" + productName + "%");
            }
            
            if (category != null && !category.trim().isEmpty()) {
                whereClause.append(" AND CATEGORY = ?");
                params.add(category);
            }
            
            // 查询总数
            String countSql = "SELECT COUNT(DISTINCT PLUNO) AS COUNT FROM PRODUCT_FEATURES WHERE 1=1" + whereClause;
            Integer totalRecords = jdbcTemplate.queryForObject(countSql, params.toArray(), Integer.class);
            totalRecords = totalRecords != null ? totalRecords : 0;
            
            // 分页查询
            int startRow = (pageNumber - 1) * pageSize + 1;
            int endRow = pageNumber * pageSize;
            
            String sql = "SELECT * FROM ( " +
                "SELECT ROWNUM AS NUM, t.* FROM ( " +
                "SELECT PLUNO, PRODUCT_NAME, CATEGORY, " +
                "NVL(IMAGE_COUNT, 0) AS IMAGE_COUNT, " +
                "NVL(QUALITY_SCORE, 0) AS QUALITY_SCORE, " +
                "TO_CHAR(CREATED_TIME, 'YYYY-MM-DD HH24:MI:SS') AS CREATED_TIME " +
                "FROM PRODUCT_FEATURES WHERE 1=1 " +
                whereClause +
                "ORDER BY CREATED_TIME DESC " +
                ") t WHERE ROWNUM <= ? " +
                ") WHERE NUM >= ?";
            
            List<Object> queryParams = new ArrayList<>(params);
            queryParams.add(endRow);
            queryParams.add(startRow);
            
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, queryParams.toArray());
            
            result.put("success", true);
            result.put("data", list);
            result.put("totalRecords", totalRecords);
            result.put("pageNumber", pageNumber);
            result.put("pageSize", pageSize);
            result.put("message", "查询成功");
            
            return result;
            
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
            return result;
        }
    }
    
    /**
     * 获取商品训练图片
     */
    @GetMapping("/images")
    public Map<String, Object> getProductImages(@RequestParam("pluno") String pluno) {
        Map<String, Object> result = new HashMap<>();
        
        if (jdbcTemplate == null) {
            result.put("success", false);
            result.put("message", "数据库未连接");
            return result;
        }
        
        try {
            String sql = "SELECT SAMPLE_ID, IMAGE_URL, METADATA, " +
                "TO_CHAR(CREATED_TIME, 'YYYY-MM-DD HH24:MI:SS') AS CREATED_TIME " +
                "FROM PRODUCT_TRAINING_SAMPLES " +
                "WHERE PLUNO = ? " +
                "ORDER BY CREATED_TIME DESC";
            
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, pluno);
            
            result.put("success", true);
            result.put("data", list);
            result.put("message", "查询成功");
            
            return result;
            
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
            return result;
        }
    }
    
    /**
     * 删除商品训练数据
     */
    @DeleteMapping("/delete")
    public Map<String, Object> deleteProduct(@RequestParam("pluno") String pluno) {
        Map<String, Object> result = new HashMap<>();
        
        if (jdbcTemplate == null) {
            result.put("success", false);
            result.put("message", "数据库未连接");
            return result;
        }
        
        try {
            // 删除训练样本
            String deleteSamplesSql = "DELETE FROM PRODUCT_TRAINING_SAMPLES WHERE PLUNO = ?";
            jdbcTemplate.update(deleteSamplesSql, pluno);
            
            // 删除特征数据
            String deleteFeaturesSql = "DELETE FROM PRODUCT_FEATURES WHERE PLUNO = ?";
            jdbcTemplate.update(deleteFeaturesSql, pluno);
            
            result.put("success", true);
            result.put("message", "删除成功");
            
            return result;
            
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "删除失败：" + e.getMessage());
            return result;
        }
    }
    
    /**
     * 上传训练图片
     */
    @PostMapping("/upload")
    public Map<String, Object> uploadTrainingImage(
            @RequestParam("image") MultipartFile image,
            @RequestParam("pluno") String pluno,
            @RequestParam("productName") String productName,
            @RequestParam("category") String category,
            @RequestParam(value = "metadata", required = false) String metadata) {
        
        Map<String, Object> result = new HashMap<>();
        
        if (jdbcTemplate == null) {
            result.put("success", false);
            result.put("message", "数据库未连接");
            return result;
        }
        
        try {
            String sampleId = UUID.randomUUID().toString().replace("-", "");
            String fileName = System.currentTimeMillis() + ".jpg";
            String uploadDir = "/home/admin/.openclaw/workspace/REPORT/upload/products/" + pluno;
            
            // 创建上传目录
            java.io.File dir = new java.io.File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            // 保存图片文件
            String filePath = uploadDir + "/" + fileName;
            image.transferTo(new java.io.File(filePath));
            
            String imageUrl = "/upload/products/" + pluno + "/" + fileName;
            
            // 插入训练样本
            String sql = "INSERT INTO PRODUCT_TRAINING_SAMPLES " +
                "(SAMPLE_ID, PLUNO, PRODUCT_NAME, IMAGE_URL, METADATA, CREATED_TIME, CREATED_BY) " +
                "VALUES (?, ?, ?, ?, ?, SYSDATE, ?)";
            
            jdbcTemplate.update(sql, sampleId, pluno, productName, imageUrl, metadata, "system");
            
            // 更新或创建特征记录
            String checkSql = "SELECT COUNT(*) FROM PRODUCT_FEATURES WHERE PLUNO = ?";
            Integer count = jdbcTemplate.queryForObject(checkSql, new Object[]{pluno}, Integer.class);
            
            if (count != null && count > 0) {
                String updateSql = "UPDATE PRODUCT_FEATURES SET IMAGE_COUNT = IMAGE_COUNT + 1, UPDATED_TIME = SYSDATE WHERE PLUNO = ?";
                jdbcTemplate.update(updateSql, pluno);
            } else {
                String insertSql = "INSERT INTO PRODUCT_FEATURES " +
                    "(FEATURE_ID, PLUNO, PRODUCT_NAME, CATEGORY, IMAGE_COUNT, CREATED_TIME, UPDATED_TIME) " +
                    "VALUES (?, ?, ?, ?, 1, SYSDATE, SYSDATE)";
                String featureId = UUID.randomUUID().toString().replace("-", "");
                jdbcTemplate.update(insertSql, featureId, pluno, productName, category);
            }
            
            result.put("success", true);
            result.put("sampleId", sampleId);
            result.put("imageUrl", imageUrl);
            result.put("message", "上传成功");
            
            return result;
            
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "上传失败：" + e.getMessage());
            return result;
        }
    }
}
