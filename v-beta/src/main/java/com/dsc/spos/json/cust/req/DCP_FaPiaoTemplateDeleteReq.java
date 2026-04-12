package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 服务函数：DCP_FaPiaoTemplateDelete
 * 服务说明：发票模板删除
 *
 * @author wangzyc
 * @since 2021-2-03
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DCP_FaPiaoTemplateDeleteReq extends JsonBasicReq {

    private level1Elm request;

    @Data
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class level1Elm{
        private String templateId; // 模板编码
    }
}
