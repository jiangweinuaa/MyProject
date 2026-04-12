package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 服务函数：DCP_FaPiaoTemplateDetail
 * 服务说明：发票模板详情
 *
 * @author wangzyc
 * @since 2021-1-27
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DCP_FaPiaoTemplateDetailReq extends JsonBasicReq {
    private level1ELm request;

    @Data
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class level1ELm{
        private String templateId; // 模板编码
    }
}
