package com.report.controller;

import com.report.dto.ServiceRequest;
import com.report.dto.ServiceResponse;
import com.report.service.LoginService;
import com.report.service.ReportService;
import com.report.service.impl.ServiceRouter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 统一服务入口 Controller
 * 提供 RESTful API 接口
 */
@RestController
@RequestMapping("/api")
public class ServiceController {

    @Autowired
    private ServiceRouter serviceRouter;

    @Autowired(required = false)
    private LoginService loginService;

    /**
     * 统一服务入口
     * POST /api/service
     * 
     * @param request 服务请求 {"serviceId":"xxx","request":{},"sign":{}}
     * @return 服务响应
     */
    @PostMapping("/service")
    public ServiceResponse<?> service(
            @RequestBody ServiceRequest request,
            @RequestHeader(value = "X-Forwarded-For", required = false) String xForwardedFor,
            @RequestHeader(value = "X-Real-IP", required = false) String xRealIp,
            javax.servlet.http.HttpServletRequest httpRequest) {
        System.out.println("[DEBUG] /service 请求，serviceId=" + request.getServiceId());
        if (request.getServiceId() == null || request.getServiceId().isEmpty()) {
            return ServiceResponse.error("400", "serviceId 不能为空");
        }
        
        // 如果是 UserLogin 服务，直接调用 loginService 以获取客户端 IP
        if ("UserLogin".equals(request.getServiceId())) {
            System.out.println("[DEBUG] UserLogin 请求，loginService=" + (loginService != null ? "not null" : "null"));
            if (loginService != null) {
                String clientIp = getClientIp(xForwardedFor, xRealIp, httpRequest);
                System.out.println("[DEBUG] 获取到客户端 IP: " + clientIp);
                
                // 从 request 中提取参数
                String eid = "";
                String opno = "";
                String password = "";
                if (request.getRequest() instanceof Map) {
                    Map<?, ?> requestMap = (Map<?, ?>) request.getRequest();
                    eid = requestMap.get("eid") != null ? requestMap.get("eid").toString() : "";
                    opno = requestMap.get("username") != null ? requestMap.get("username").toString() : "";
                    password = requestMap.get("password") != null ? requestMap.get("password").toString() : "";
                }
                
                return loginService.login(eid, opno, password, clientIp);
            }
        }
        
        return serviceRouter.route(request);
    }

    /**
     * 用户登录
     * POST /api/login
     * 
     * @param params 登录参数 {"eid":"xxx","opno":"xxx","password":"xxx"}
     * @return 包含 token 的响应
     */
    @PostMapping("/login")
    public ServiceResponse<?> login(
            @RequestBody Map<String, Object> params,
            @RequestHeader(value = "X-Forwarded-For", required = false) String xForwardedFor,
            @RequestHeader(value = "X-Real-IP", required = false) String xRealIp,
            javax.servlet.http.HttpServletRequest request) {
        if (loginService == null) {
            return ServiceResponse.error("500", "登录服务未初始化");
        }
        
        String eid = params.get("eid") != null ? params.get("eid").toString() : null;
        String opno = params.get("opno") != null ? params.get("opno").toString() : null;
        String password = params.get("password") != null ? params.get("password").toString() : null;
        
        // 获取客户端 IP
        String clientIp = getClientIp(xForwardedFor, xRealIp, request);
        
        return loginService.login(eid, opno, password, clientIp);
    }
    
    /**
     * 获取客户端 IP 地址
     */
    private String getClientIp(String xForwardedFor, String xRealIp, javax.servlet.http.HttpServletRequest request) {
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            // X-Forwarded-For 可能有多个 IP，取第一个
            return xForwardedFor.split(",")[0].trim();
        }
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        return request != null ? request.getRemoteAddr() : "unknown";
    }

    /**
     * 验证 token
     * POST /api/token/verify
     * 
     * @param params 验证参数 {"token":"xxx"}
     * @return 验证结果
     */
    @PostMapping("/token/verify")
    public ServiceResponse<?> verifyToken(@RequestBody Map<String, Object> params) {
        if (loginService == null) {
            return ServiceResponse.error("500", "登录服务未初始化");
        }
        
        String token = params.get("token") != null ? params.get("token").toString() : null;
        
        return loginService.verifyToken(token);
    }

    /**
     * 退出登录
     * POST /api/logout
     * 
     * @param params 退出参数 {"token":"xxx"}
     * @return 退出结果
     */
    @PostMapping("/logout")
    public ServiceResponse<?> logout(@RequestBody Map<String, Object> params) {
        if (loginService == null) {
            return ServiceResponse.error("500", "登录服务未初始化");
        }
        
        String token = params.get("token") != null ? params.get("token").toString() : null;
        
        return loginService.logout(token);
    }

    /**
     * 健康检查
     * GET /api/health
     */
    @GetMapping("/health")
    public ServiceResponse<?> health() {
        return ServiceResponse.success("OK", "服务运行正常");
    }

    /**
     * 内部请求参数类 (用于 GET 请求)
     */
    public static class QueryMemberRequest {
        private String mobile;
        private String withAccount;

        public QueryMemberRequest() {}

        public QueryMemberRequest(String mobile, String withAccount) {
            this.mobile = mobile;
            this.withAccount = withAccount;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getWithAccount() {
            return withAccount;
        }

        public void setWithAccount(String withAccount) {
            this.withAccount = withAccount;
        }
    }
}
