package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * 服務函數：POrderCreate
 *   說明：要货单列表查询
 * 服务说明：要货单列表查询
 * @author wangzyc
 * @since  2021-05-11
 */
@Data
public class DCP_POrderListQueryReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private String keyTxt;          // 关键字（支持要货单单号、品号、品名搜索）
        private String status;          // 状态（空：全部；0.新建 2.已提交 3.已审核  5.已驳回）
        private String dateType;        // 日期类型（bDate：单据日期（默认）  rDate：需求日期）
        private String beginDate;       // 开始日期（yymmdd）前端默认查询3个月
        private String endDate;         // 截止日期（yymmdd）前端默认查询3个月
    }
}
