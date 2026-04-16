package com.report.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * AI 模型管理服务
 */
@Service
public class AIModelService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    /**
     * 获取当前可用的模型（按排序，返回第一个状态为 100 的）
     * @return 可用模型 ID，如果没有可用模型则返回 null
     */
    public String getAvailableModel() {
        return getAvailableModel(null);
    }
    
    /**
     * 获取当前可用的模型（排除指定模型）
     * @param excludeModelId 要排除的模型 ID（当前模型）
     * @return 可用模型 ID，如果没有可用模型则返回 null
     */
    public String getAvailableModel(String excludeModelId) {
        String sql;
        List<Map<String, Object>> list;
        
        if (excludeModelId != null && !excludeModelId.isEmpty()) {
            // 排除条件放在 WHERE 子句中，ORDER BY 之前
            sql = "SELECT MODEL_ID FROM AI_MODEL_LIST WHERE (STATUS = '100' OR STATUS = 100) AND MODEL_ID != ? ORDER BY SORT_ORDER ASC";
            list = jdbcTemplate.queryForList(sql, excludeModelId);
        } else {
            sql = "SELECT MODEL_ID FROM AI_MODEL_LIST WHERE (STATUS = '100' OR STATUS = 100) ORDER BY SORT_ORDER ASC";
            list = jdbcTemplate.queryForList(sql);
        }
        
        if (list != null && !list.isEmpty()) {
            String modelId = (String) list.get(0).get("MODEL_ID");
            System.out.println("🔍 获取到的可用模型（排除 " + excludeModelId + "）：" + modelId);
            return modelId;
        }
        
        System.err.println("❌ AI_MODEL_LIST 表中没有可用模型（排除 " + excludeModelId + "）");
        return null;
    }
    
    /**
     * 标记模型为不可用（403）
     */
    public void markModelUnavailable(String modelId) {
        // 支持 VARCHAR2 和 NUMBER 两种类型
        String sql = "UPDATE AI_MODEL_LIST SET STATUS = 403 WHERE MODEL_ID = ?";
        jdbcTemplate.update(sql, modelId);
        System.out.println("✅ 已标记模型 " + modelId + " 为不可用（STATUS=403）");
    }
    
    /**
     * 切换模型（完整流程）
     * @param oldModel 旧模型 ID
     * @return 切换结果信息（包含新模型 ID）
     */
    public String switchModel(String oldModel) {
        // 步骤 1：标记旧模型为不可用
        markModelUnavailable(oldModel);
        
        // 步骤 2：获取下一个可用模型（排除旧模型）
        String newModel = getAvailableModel(oldModel);
        if (newModel == null) {
            throw new RuntimeException("所有模型都不可用，请充值或更换 API Key");
        }
        
        // 步骤 3：更新 PRODUCT_APPKEY 表
        String updateAppKeySql = "UPDATE PRODUCT_APPKEY SET ACCESSKEYID = ? WHERE PLATFORM = 'ALI_QWEN'";
        jdbcTemplate.update(updateAppKeySql, newModel);
        
        return oldModel + "模型 token 耗尽，已自动切换到" + newModel + "模型，请重试";
    }
    
    /**
     * 切换模型（指定新模型）
     * @param oldModel 旧模型 ID
     * @param newModel 新模型 ID
     * @return 切换结果信息
     */
    public String switchModel(String oldModel, String newModel) {
        // 标记旧模型为不可用
        markModelUnavailable(oldModel);
        
        // 更新 PRODUCT_APPKEY 表
        String updateAppKeySql = "UPDATE PRODUCT_APPKEY SET ACCESSKEYID = ? WHERE PLATFORM = 'ALI_QWEN'";
        jdbcTemplate.update(updateAppKeySql, newModel);
        
        return oldModel + "模型 token 耗尽，已自动切换到" + newModel + "模型，请重试";
    }
    
    /**
     * 获取所有模型列表
     */
    public List<Map<String, Object>> getModelList() {
        String sql = "SELECT MODEL_ID, MODEL_NAME, SORT_ORDER, STATUS FROM AI_MODEL_LIST ORDER BY SORT_ORDER ASC";
        return jdbcTemplate.queryForList(sql);
    }
    
    /**
     * 检查模型是否可用
     */
    public boolean isModelAvailable(String modelId) {
        // 支持 VARCHAR2 和 NUMBER 两种类型
        String sql = "SELECT COUNT(*) FROM AI_MODEL_LIST WHERE MODEL_ID = ? AND (STATUS = '100' OR STATUS = 100)";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, modelId);
        return count != null && count > 0;
    }
}
