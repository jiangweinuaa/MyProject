package com.report.config;

import org.springframework.stereotype.Component;

/**
 * AI 参数配置常量
 */
@Component
public class AIParams {
    // PRODUCT_APPKEY 表中的 PLATFORM 枚举值
    public static final String PLATFORM_AI_VERSION = "AI_VERSION";       // 版本切换
    public static final String PLATFORM_ALI_MODEL = "ALI_QWEN";          // V1 大模型配置
    public static final String PLATFORM_ALI_AGENT = "ALI_AGENT";         // V2 智能体配置
    
    // 版本枚举值
    public static final String VERSION_ALI_MODEL = "ALI_MODEL";          // 阿里大模型版
    public static final String VERSION_ALI_AGENT = "ALI_AGENT";          // 阿里智能体版
}
