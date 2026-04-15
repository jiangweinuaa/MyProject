package com.report.service;

import com.report.dto.ServiceResponse;

/**
 * 登录服务接口
 */
public interface LoginService {
    
    /**
     * 用户登录
     * @param eid 企业编号
     * @param opno 操作员编号
     * @param password 密码
     * @param ip 客户端 IP 地址
     * @return 包含 token 的响应
     */
    ServiceResponse<?> login(String eid, String opno, String password, String ip);
    
    /**
     * 验证 token
     * @param token token 字符串
     * @return 验证结果
     */
    ServiceResponse<?> verifyToken(String token);
    
    /**
     * 退出登录
     * @param token token 字符串
     * @return 退出结果
     */
    ServiceResponse<?> logout(String token);
}
