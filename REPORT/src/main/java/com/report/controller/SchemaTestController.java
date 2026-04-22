package com.report.controller;

import com.report.service.SchemaEmbeddingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Schema 向量检索 Controller
 */
@RestController
@RequestMapping("/api/schema")
@CrossOrigin(origins = "*")
public class SchemaTestController {

    @Autowired
    private SchemaEmbeddingService schemaEmbeddingService;

    /**
     * 测试 Schema 检索
     * GET /api/schema/test?question=xxx
     */
    @GetMapping("/test")
    public Map<String, Object> testRetrieval(@RequestParam String question) {
        return schemaEmbeddingService.findRelevantTables(question);
    }

    /**
     * 重建向量库
     * POST /api/schema/rebuild
     */
    @PostMapping("/rebuild")
    public Map<String, Object> rebuild() {
        return schemaEmbeddingService.rebuildAll();
    }

    /**
     * 获取向量库状态
     * GET /api/schema/status
     */
    @GetMapping("/status")
    public Map<String, Object> status() {
        return schemaEmbeddingService.getEmbeddingStatus();
    }
}
