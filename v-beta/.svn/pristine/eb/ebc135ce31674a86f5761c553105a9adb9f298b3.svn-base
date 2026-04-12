package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;
import java.util.List;

/**
 * 服务函数：DCP_N_OrderParaQuery
 * 服务说明：N_订单参数查询 代码全部继承DCP_OrderParaQuery
 * @author jinzma
 * @since  2024-05-23
 */
@Data
public class DCP_N_OrderParaQueryRes extends JsonRes {

    private responseDatas datas;

    @Data
    public class responseDatas {
        private List<level1Elm> orgList;
    }

    @Data
    public class level1Elm {
        private String organizationNo;
        private String organizationName;
        private String orgForm;
        private String shopBeginTime;
        private String shopEndTime;
        private String province;
        private String city;
        private String county;
        private String street;
        private String address;
        private String phone;
    }
}
