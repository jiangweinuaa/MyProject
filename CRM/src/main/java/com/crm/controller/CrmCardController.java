package com.crm.controller;

import com.crm.dto.QueryCardRequest;
import com.crm.dto.QueryCardResponse;
import com.crm.service.CrmCardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CRM 卡片查询控制器
 */
@Slf4j
@RestController
@RequestMapping("/card")
@RequiredArgsConstructor
public class CrmCardController {

    private final CrmCardService crmCardService;

    /**
     * 查询卡片信息
     * @param request 查询请求（JSON 格式）
     * @return 卡片信息响应（JSON 格式）
     * 
     * 请求示例:
     * {
     *   "cardNo": "000001"
     * }
     */
    @PostMapping("/query")
    public ResponseEntity<QueryCardResponse> queryCard(@Valid @RequestBody QueryCardRequest request) {
        log.info("收到卡片查询请求，卡号：{}", request.getCardNo());
        
        QueryCardResponse response = crmCardService.queryCard(request);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 查询所有卡片信息（GET 方式，无参数）
     * @return 所有卡片信息列表（JSON 格式）
     * 
     * 响应示例:
     * [
     *   {
     *     "cardId": 1,
     *     "cardNo": "000001",
     *     "memberName": "测试会员",
     *     "amount": 10000.0,
     *     ...
     *   }
     * ]
     */
    @GetMapping("/queryAll")
    public ResponseEntity<List<QueryCardResponse>> queryAllCards() {
        log.info("收到查询所有卡片请求");
        
        List<QueryCardResponse> cards = crmCardService.queryAllCards();
        
        log.info("返回 {} 张卡片信息", cards.size());
        
        return ResponseEntity.ok(cards);
    }

    /**
     * 健康检查接口
     * @return 服务状态
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("CRM Card Service is running");
    }
}
