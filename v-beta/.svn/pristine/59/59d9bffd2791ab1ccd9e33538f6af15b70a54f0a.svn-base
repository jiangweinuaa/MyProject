package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

/**
 * @description: KDS菜品流程控制修改
 * @author: wangzyc
 * @create: 2021-09-13
 */
@Data
public class DCP_DishControlUpdate_OpenReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private String shopId;
        private List<level2Elm> goodsList;
    }

    @Data
    public class level2Elm{
        private String categoryId;
        private String pluNo;
        private String unSide;
        private String unCook;
        private String unCall;
    }

}
