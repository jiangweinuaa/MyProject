package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

/**
 * 服务函数：DCP_N_GoodsShelfQuery
 * 服务说明：N-商品上下架查询
 * @author jinzma
 * @since  2024-04-22
 */
@Data
public class DCP_N_GoodsShelfQueryReq extends JsonBasicReq {
    private Request request;
    @Data
    public class Request {
        private String pluNo;
        private List<Shop> shopList;
    }
    @Data
    public class Shop {
        private String shopId;
    }
}
