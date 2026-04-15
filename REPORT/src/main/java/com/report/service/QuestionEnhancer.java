package com.report.service;

import com.report.dto.ContextVariables;
import org.springframework.stereotype.Service;

/**
 * 问题增强器
 * 
 * 根据上下文变量增强用户问题
 */
@Service
public class QuestionEnhancer {
    
    /**
     * 增强问题
     */
    public String enhance(String question, ContextVariables variables) {
        String enhanced = question;
        
        // 替换代词
        if (variables.getShopId() != null) {
            enhanced = enhanced.replace("这个门店", "门店" + variables.getShopId());
            enhanced = enhanced.replace("该门店", "门店" + variables.getShopId());
            enhanced = enhanced.replace("它", "门店" + variables.getShopId());
        }
        
        if (variables.getPluno() != null) {
            enhanced = enhanced.replace("这个商品", "商品" + variables.getPluno());
            enhanced = enhanced.replace("该商品", "商品" + variables.getPluno());
        }
        
        // 补充省略的条件（TODO：未来实现日期计算）
        if (question.contains("上周") && variables.getStartDate() == null) {
            // 计算上周日期
        }
        
        if (question.contains("今天") && variables.getStartDate() == null) {
            // 使用今天日期
        }
        
        return enhanced;
    }
}
