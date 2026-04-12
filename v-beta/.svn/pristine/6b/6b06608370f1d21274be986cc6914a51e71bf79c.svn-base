package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_OrderGoodsStatusUpdate_OpenRes extends JsonBasicRes {
    private List<level1Elm> datas;

    @Data
    public class level1Elm{
        private String orderNo;         // 订单号
        private String shopNo;          // 下订门店编号
        private String shopName;        // 下订门店名称
        private List<level2Elm> goodsList;  // 商品列表
    }

    @Data
    public class level2Elm{
        private String item;            // 项次
        private String pluNo;           // 品号
        private String pluName;         // 品名
        private String specName;        // 规格名称
        private String featureName;     // 特征码名称
        private String qty;             // 数量
        private List<level3Elm> messages;           // 商品备注
    }

    @Data
    public class level3Elm{
        private String msgType;         // 备注类型  默认text
        private String msgName;         // 备注名称
        private String message;         // 备注内容
    }
}
