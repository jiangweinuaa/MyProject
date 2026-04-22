package com.report.controller;

import com.report.service.DashVectorSchemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * DashVector Schema 检索测试 Controller（独立路由，不影响现有功能）
 */
@RestController
@RequestMapping("/api/dashvector")
@CrossOrigin(origins = "*")
public class DashVectorTestController {

    @Autowired
    private DashVectorSchemaService dashVectorSchemaService;

    /**
     * 获取配置和统计信息
     * GET /api/dashvector/status
     */
    @GetMapping("/status")
    public Map<String, Object> status() {
        return dashVectorSchemaService.getStats();
    }

    /**
     * 重建向量库
     * POST /api/dashvector/rebuild
     */
    @PostMapping("/rebuild")
    public Map<String, Object> rebuild() {
        return dashVectorSchemaService.rebuildAll();
    }

    /**
     * 检索相关表
     * GET /api/dashvector/retrieve?question=xxx
     */
    @GetMapping("/retrieve")
    public Map<String, Object> retrieve(@RequestParam String question) {
        return dashVectorSchemaService.retrieve(question);
    }
}
