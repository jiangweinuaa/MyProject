package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

/**
 * 服务函数：DCP_ChannelStockDetail
 * 服务说明：渠道分配库存查询
 * @author wangzyc 2021-03-16
 */
@Data
public class DCP_ChannelStockDetailRes extends JsonRes {
    private level1Elm datas;

    @Data
    public class level1Elm{
        private List<level2Elm> pluList;       // 商品列表
    }

    @Data
    public class level2Elm {
        private String organizationNo;          // 机构
        private String organizationName;        // 机构名称
        private String pluNo;                   // 品号
        private String pluName;                 // 品名
        private String featureNo;               // 特征码
        private String featureName;             // 特征码名称
        private String sUnit;                   // 交易单位
        private String sUnitName;               // 交易单位名称
        private String baseUnit;                // 基准单位
        private String warehouse;               // 仓位编码
        private String warehouseName;           // 仓位名称
        private String listImage;               // 图片名称
        private String avalibleQty;             // 总剩余可用=实际库存-总预留-总锁定（表DCP_STOCK： QTY-LOCKQTY-ONLINEQTY）
        private List<level3Elm> channelList;    // 渠道列表
    }

    @Data
    public class level3Elm{
        private String channelId;               // 渠道ID
        private String channelName;             // 渠道名称
        private String onlineQty;               //  渠道销售库存：渠道预留数（表DCP_STOCK_CHANNEL：ONLINEQTY）；渠道共享数（等于avalibleQty）
    }
}
