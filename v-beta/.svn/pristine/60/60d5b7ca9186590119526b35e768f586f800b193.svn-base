package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

/**
 * 服務函數：DCP_ProductionOrderDetailQuery_Open
 *   說明：生产订单商品
 * 服务说明：生产订单商品查询
 * @author wangzyc
 * @since  2021-4-25
 */
@Data
public class DCP_ProductionOrderDetailQuery_OpenReq extends JsonBasicReq {

    private level1Elm request;

    @Data
    public class level1Elm{
        private String eId;         // 企业编号
        private String machShopNo;  // 生产机构编号
        private String stallId;     // 档口编号
        private List<level2Elm> goodsList;  // 商品列表
    }

    @Data
    public class level2Elm{
        private String orderNo;     // 订单号
        private String oItem;     // 来源商品项次，KDS必传
        private String item;     // 商品项次，数量层级，KDS必传
    }

}
