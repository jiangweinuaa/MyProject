package com.report.controller;

import com.report.service.NLQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 自然语言查询 Controller（支持会话记忆）
 */
@RestController
@RequestMapping("/api/nl-query")
@CrossOrigin(origins = "*")
public class NLQueryController {
    
    @Autowired
    private NLQueryService nlQueryService;
    
    /**
     * 自然语言查询（支持 sessionId 和 token）
     */
    @PostMapping(value = "/query", consumes = "application/json", produces = "application/json")
    public Map<String, Object> query(@RequestBody Map<String, Object> request) {
        String sessionId = request.get("sessionId") != null ? request.get("sessionId").toString() : null;
        String question = request.get("question") != null ? request.get("question").toString() : null;
        
        // 获取 token（从 request 或 sign 对象）
        String token = null;
        if (request.get("token") != null) {
            token = request.get("token").toString();
        } else if (request.get("sign") instanceof Map) {
            Map<?, ?> sign = (Map<?, ?>) request.get("sign");
            token = sign.get("token") != null ? sign.get("token").toString() : null;
        }
        
        if (sessionId != null && !sessionId.trim().isEmpty()) {
            return nlQueryService.query(sessionId, question, token);
        } else {
            return nlQueryService.query(question, token);
        }
    }
    
    /**
     * 测试接口
     */
    @GetMapping("/examples")
    public Map<String, Object> getExamples() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("examples", new String[]{
            "今天销售额是多少？",
            "本月销售额是多少？",
            "哪个商品卖得最好？",
            "A 门店的库存还有多少？",
            "上周的销量趋势如何？"
        });
        return response;
    }
}
