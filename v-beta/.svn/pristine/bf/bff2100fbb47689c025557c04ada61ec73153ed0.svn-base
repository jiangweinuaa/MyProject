package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import com.dsc.spos.waimai.entity.order;
import lombok.Data;
import java.util.List;

/**
 * 服务函数：DCP_OrderTempQuery_Open
 * 服务说明：订单挂单数据查询
 * @author jinzma
 * @since  2023-12-26
 */
@Data
public class DCP_OrderTempQuery_OpenRes extends JsonRes {
    private Datas datas;
    @Data
    public class Datas{
        private String total;
        private List<order> orderList;
    }
}
