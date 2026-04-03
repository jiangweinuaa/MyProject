package com.crm.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

/**
 * 查询卡片请求 DTO
 */
@Data
public class QueryCardRequest {

    /**
     * 卡号（必填）
     */
    @NotBlank(message = "卡号不能为空")
    private String cardNo;
}
