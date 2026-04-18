package com.report.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 上下文变量
 */
@Data
public class ContextVariables implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 当前门店 ID
     */
    private String shopId;
    
    /**
     * 当前日期范围
     */
    private String startDate;
    private String endDate;
    
    /**
     * 当前商品品号
     */
    private String pluno;
    
    /**
     * 其他变量
     */
    private Map<String, String> extra = new HashMap<>();
    
    /**
     * 更新变量
     */
    public void update(String key, String value) {
        switch (key) {
            case "shopId":
                shopId = value;
                break;
            case "startDate":
                startDate = value;
                break;
            case "endDate":
                endDate = value;
                break;
            case "pluno":
                pluno = value;
                break;
            default:
                extra.put(key, value);
        }
    }
    
    // 显式添加 getter 方法（避免 Lombok 问题）
    public String getShopId() { return shopId; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public String getPluno() { return pluno; }
    public Map<String, String> getExtra() { return extra; }
    
    /**
     * 获取变量文本（用于 Prompt 增强）
     */
    public String getText() {
        StringBuilder sb = new StringBuilder();
        sb.append("当前上下文：");
        if (shopId != null) sb.append("门店=").append(shopId).append("，");
        if (startDate != null) sb.append("开始日期=").append(startDate).append("，");
        if (endDate != null) sb.append("结束日期=").append(endDate).append("，");
        if (pluno != null) sb.append("品号=").append(pluno).append("，");
        
        String text = sb.toString();
        return text.endsWith("，") ? text.substring(0, text.length() - 1) : text;
    }
}
