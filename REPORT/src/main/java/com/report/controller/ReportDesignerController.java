package com.report.controller;

import com.report.service.NLQueryService;
import com.report.service.ReportSaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 报表设计器 Controller
 */
@RestController
@RequestMapping("/api/designer")
@CrossOrigin(origins = "*")
public class ReportDesignerController {
    
    @Autowired
    private NLQueryService nlQueryService;
    
    @Autowired
    private ReportSaveService reportSaveService;
    
    /**
     * 对话生成（复用智问 API）
     */
    @PostMapping("/chat")
    public Map<String, Object> chat(@RequestBody Map<String, Object> request) {
        String sessionId = request.get("sessionId") != null ? request.get("sessionId").toString() : null;
        String question = request.get("question") != null ? request.get("question").toString() : null;
        
        // 获取 token
        String token = null;
        if (request.get("token") != null) {
            token = request.get("token").toString();
        } else if (request.get("sign") instanceof Map) {
            Map<?, ?> sign = (Map<?, ?>) request.get("sign");
            token = sign.get("token") != null ? sign.get("token").toString() : null;
        }
        
        // 复用智问服务
        if (sessionId != null && !sessionId.trim().isEmpty()) {
            return nlQueryService.query(sessionId, question, token);
        } else {
            return nlQueryService.query(question, token);
        }
    }
    
    /**
     * 保存报表
     */
    @PostMapping("/save")
    public Map<String, Object> save(@RequestBody Map<String, Object> request) {
        return reportSaveService.saveReport(request);
    }
    
    /**
     * 获取菜单树
     */
    @GetMapping("/menu")
    public Map<String, Object> getMenu() {
        return reportSaveService.getMenuTree();
    }
    
    /**
     * 获取报表详情
     */
    @GetMapping("/report/{reportId}")
    public Map<String, Object> getReport(@PathVariable String reportId) {
        return reportSaveService.getReport(reportId);
    }
    
    /**
     * 删除报表
     */
    @DeleteMapping("/report/{reportId}")
    public Map<String, Object> deleteReport(@PathVariable String reportId) {
        return reportSaveService.deleteReport(reportId);
    }
    
    /**
     * 获取报表列表
     */
    @GetMapping("/reports")
    public Map<String, Object> getReports() {
        return reportSaveService.getReportList();
    }
}
