package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 服务函数：DCP_FaPiaoTemplateQuery
 * 服务说明：发票模板查询
 * @author wangzyc
 * @since  2021-1-27
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DCP_FaPiaoTemplateQueryReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class level1Elm{
        private String status; // -1未启用100已启用0已禁用
        private String keyTxt; // 编码/名称模糊搜索
        private String shopId; // 适用门店编码
    }
}
