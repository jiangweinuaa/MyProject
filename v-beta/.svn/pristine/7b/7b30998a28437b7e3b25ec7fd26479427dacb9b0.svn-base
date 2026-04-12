package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

/**
 * 服务函数：DCP_ISVWeComStaffQuery
 * 服务说明：查询企微员工列表
 * @author jinzma
 * @since  2023-09-13
 */
@Data
public class DCP_ISVWeComStaffQueryRes extends JsonRes {
    private List<level1Elm> datas;
    @Data
    public class level1Elm{
        private String opNo;
        private String opName;
        private String userId;
        private String telephone;
        private String accountType;
        private String activeTime;
        private String expireTime;
        private List<Shop> shopList;
    }
    @Data
    public class Shop{
        private String shopId;
        private String shopName;
    }
}
