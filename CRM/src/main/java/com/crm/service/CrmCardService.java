package com.crm.service;

import com.crm.dto.QueryCardRequest;
import com.crm.dto.QueryCardResponse;

import java.util.List;

/**
 * CRM 卡片服务接口
 */
public interface CrmCardService {

    /**
     * 查询卡片信息
     * @param request 查询请求
     * @return 卡片信息响应
     */
    QueryCardResponse queryCard(QueryCardRequest request);

    /**
     * 查询所有卡片信息
     * @return 卡片列表
     */
    List<QueryCardResponse> queryAllCards();
}
