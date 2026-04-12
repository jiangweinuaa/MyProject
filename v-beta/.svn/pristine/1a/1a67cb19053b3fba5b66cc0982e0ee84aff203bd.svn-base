package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * @description: 原因查询
 * @author: wangzyc
 * @create: 2022-03-08
 */
@Data
public class DCP_ReasonQuery_OpenReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        // 0退货(销售) 1报损 2退回(差异申诉/退货出库) 3生产报废 4其他出库 5其他入库 6积分调整  7金额调整 8福豆调整 9赠品 10免单
        // 11投诉建议 12试吃出库 13赠送出库  14拼胚入库  15拼胚出库 16备注信息 17采购退货  18评价理由
        private String reasonType; //
    }
}
