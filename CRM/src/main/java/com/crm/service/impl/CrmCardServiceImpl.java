package com.crm.service.impl;

import com.crm.dto.QueryCardRequest;
import com.crm.dto.QueryCardResponse;
import com.crm.entity.CrmCard;
import com.crm.repository.CrmCardRepository;
import com.crm.service.CrmCardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * CRM 卡片服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CrmCardServiceImpl implements CrmCardService {

    private final CrmCardRepository crmCardRepository;

    @Override
    public QueryCardResponse queryCard(QueryCardRequest request) {
        log.info("查询卡片信息，卡号：{}", request.getCardNo());

        QueryCardResponse response = new QueryCardResponse();

        try {
            // 根据卡号查询
            Optional<CrmCard> cardOpt = crmCardRepository.findByCardNo(request.getCardNo());

            if (cardOpt.isPresent()) {
                CrmCard card = cardOpt.get();
                // 复制属性
                BeanUtils.copyProperties(card, response);
                response.setCode(200);
                response.setMessage("查询成功");
                log.info("卡片查询成功，卡号：{}, 会员：{}", card.getCardNo(), card.getMemberName());
            } else {
                response.setCode(404);
                response.setMessage("卡片不存在");
                log.warn("卡片不存在，卡号：{}", request.getCardNo());
            }
        } catch (Exception e) {
            log.error("查询卡片失败，卡号：{}", request.getCardNo(), e);
            response.setCode(500);
            response.setMessage("查询失败：" + e.getMessage());
        }

        return response;
    }

    @Override
    public List<QueryCardResponse> queryAllCards() {
        log.info("查询所有卡片信息");

        try {
            // 查询所有卡片
            List<CrmCard> cards = crmCardRepository.findAll();
            
            log.info("查询到 {} 张卡片", cards.size());

            // 转换为响应 DTO
            return cards.stream()
                .map(card -> {
                    QueryCardResponse response = new QueryCardResponse();
                    BeanUtils.copyProperties(card, response);
                    response.setCode(200);
                    response.setMessage("查询成功");
                    return response;
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查询所有卡片失败", e);
            throw new RuntimeException("查询失败：" + e.getMessage(), e);
        }
    }
}
