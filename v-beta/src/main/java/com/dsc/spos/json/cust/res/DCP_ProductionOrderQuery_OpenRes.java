package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

/**
 * 服務函數：DCP_ProductionOrderQuery_Open
 *   說明：生产订单查询
 * 服务说明：生产订单查询
 * @author wangzyc
 * @since  2021-4-14
 */
@Data
public class DCP_ProductionOrderQuery_OpenRes extends JsonRes {
    private level1Elm datas;

    @Data
    public class level1Elm{
        private String uOrderNum;           // 订单待完成数
        private String orderNum;            // 订单已完成数
        private List<level2Elm> goodsNum;   // 商品制作统计
        private List<level3Elm> goodsList;  // 商品列表

    }

    @Data
    public class level2Elm{
        private String goodsStatus;         // 商品状态，0.待制作 1.制作中 2.已完成
        private String goodsNum;            // 商品数量
    }

    @Data
    public class level3Elm{
        private String orderNo;             // 订单号
        private String loadDocType;         // 来源渠道类型
        private String createDatetime;      // 下单时间 格式 yyyyMMddHHmmssSSS
        private String oItem;               // 来源项次
        private String item;                // 项次
        private String pluNo;               // 商品编号
        private String pluName;             // 商品名称
        private String pluBarcode;          // 商品条码
        private String featureNo;           // 商品特征码
        private String featureName;         // 商品特征名称
        private String sUnit;               // 销售单位
        private String sUnitName;           // 销售单位名称
        private String specName;            // 规格名称
        private String price;               // 零售价
        private String qty;                 // 数量
        private String oQty;                // 来源数量
        private String amt;                 // 成交金额
        private List<level4Elm> messages;   // 商品备注
        private String goodsStatus;         // 商品状态，0.待制作 1.制作中 2.已制作
        private level5Elm shipInfo;         // 收货信息
        private String memo;                // 单据备注
        private String refundReasonName;    // 退货原因名称
        private String proQty;              // 生产任务明细条数
    }

    @Data
    public class level4Elm{
        private String msgType;             // 备注类型编码  默认text
        private String msgName;             // 备注名称
        private String message;             // 备注内容
    }

    @Data
    public class level5Elm{
        private String contMan;             // 联系人/下单人
        private String contTel;             // 联系电话
        private String getMan;              // 收货人
        private String getManTel;           // 收货人电话
        private String address;             // 配送地址
        private String shipDate;            // 配送日期
        private String shipStartTime;       // 配送开始时间
        private String shipEndTime;         // 配送截止时间
    }
}
