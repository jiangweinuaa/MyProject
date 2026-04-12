package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import java.util.List;

/**
 * 服务函数：DCP_N_MinQtyTemplateQuery
 * 服务说明：N-起售量模板查询
 * @author jinzma
 * @since  2024-04-18
 */
@Data
public class DCP_N_MinQtyTemplateQueryReq extends JsonBasicReq {

    private Request request;
    @Data
    public class Request{
        private String status;   // 状态：-1未启用100已启用 0已禁用
        private String keyTxt;   // 编码/名称模糊搜索
        private List<Shop> shop; // 适用门店
    }
    @Data
    public class Shop{
        private String shopId;   // 门店编码
    }

}
