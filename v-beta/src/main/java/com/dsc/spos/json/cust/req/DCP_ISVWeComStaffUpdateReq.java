package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

/**
 * 服务函数：DCP_ISVWeComStaffUpdate
 * 服务说明：更新企微员工列表
 * @author jinzma
 * @since  2023-09-13
 */
@Data
public class DCP_ISVWeComStaffUpdateReq extends JsonBasicReq {
    private levelElm request;
    @Data
    public class levelElm{
        private String opNo;
        private String opName;
        private String telephone;
        private String type;
        private List<Shop> shopList;
    }
    @Data
    public class Shop{
        private String shopId;
    }
}
