package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

/**
 * @description: 预约项目查询
 * @author: wangzyc
 * @create: 2021-07-21
 */
@Data
public class DCP_ReserveItemsQueryRes extends JsonRes {
    private List<level1Elm> datas;

    @Data
    public class level1Elm{
        private String itemsNo;
        private String itemsName;
        private String shopId;
        private String shopName;
        private String serviceTime;
        private String status;
        private List<level2Elm> opList;
     }

     @Data
     public class level2Elm {
        private String opNo;
        // add 20211103
        private String opName;
     }


}
