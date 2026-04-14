package com.report.controller;

import com.report.dto.NLQueryLogDTO;
import com.report.service.NLQueryLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 智问日志管理 Controller
 */
@RestController
@RequestMapping("/api/nl-query")
@CrossOrigin(origins = "*")
public class NLQueryLogController {
    
    @Autowired
    private NLQueryLogService nlQueryLogService;
    
    /**
     * 记录对话日志
     */
    @PostMapping("/log")
    public Map<String, Object> logQuery(@RequestBody NLQueryLogDTO logDTO) {
        return nlQueryLogService.logQuery(logDTO);
    }
    
    /**
     * 查询日志列表
     */
    @GetMapping("/logs")
    public Map<String, Object> getLogs(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        return nlQueryLogService.getLogs(page, size);
    }
    
    /**
     * 查询统计数据（按天）
     */
    @GetMapping("/stats/daily")
    public Map<String, Object> getDailyStats(
        @RequestParam String startDate,
        @RequestParam String endDate
    ) {
        return nlQueryLogService.getDailyStats(startDate, endDate);
    }
    
    /**
     * 查询统计数据（按问题类型）
     */
    @GetMapping("/stats/question")
    public Map<String, Object> getQuestionStats() {
        return nlQueryLogService.getQuestionStats();
    }
    
    /**
     * 查询会话详情
     */
    @GetMapping("/session/{sessionId}")
    public Map<String, Object> getSessionDetail(@PathVariable String sessionId) {
        return nlQueryLogService.getSessionDetail(sessionId);
    }
}
