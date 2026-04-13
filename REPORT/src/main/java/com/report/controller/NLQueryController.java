package com.report.controller;

import com.report.service.NLQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 自然语言查询 Controller
 */
@RestController
@RequestMapping("/api/nl-query")
@CrossOrigin(origins = "*")
public class NLQueryController {
    
    @Autowired
    private NLQueryService nlQueryService;
    
    /**
     * 自然语言查询（AI 版）
     */
    @PostMapping("/query")
    public Map<String, Object> query(@RequestParam String question) {
        return nlQueryService.query(question);
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
