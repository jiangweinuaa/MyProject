package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

/**
 * @description: 菜品状态修改
 * @author: wangzyc
 * @create: 2021-09-18
 */
@Data
public class DCP_KdsDishesStatusUpdate_OpenReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private String shopId; // 门店编号
        private String machineId; // 机台编号
        private String oprType; // 操作类型 1-催菜
        private String billNo; // 单号
        private List<level2Elm> goodsList; // 商品
    }

    @Data
    public class level2Elm{
        private String oItem; // 项次
        private String pluNo; // 商品编号
    }
}
