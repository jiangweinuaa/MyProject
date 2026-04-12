package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;

import java.util.List;

/**
 * 服務函數：DCP_ProductionOrderDetailQuery_Open
 *   說明：生产订单商品查询
 * 服务说明：生产订单商品查询
 * @author wangzyc
 * @since  2021-4-25
 */
@Data
public class DCP_ProductionOrderDetailQuery_OpenRes extends JsonBasicRes {
    private List<level1Elm> datas;    // 商品列表

    @Data
    public class level1Elm{
        private String orderNo;             // 订单号
        private String loadDocType;         // 来源单据类型
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
        private String oQty;                // 來源数量
        private List<level2Elm> messages;   // 商品备注
        private String goodsStatus;         // 商品状态，0.待制作 1.制作中 2.已制作
        private level3Elm shipInfo;         // 收货信息
        private String memo;                // 单据备注

        // ADD 20210611 增加 proQty 生产任务明细条数
        private String proQty;              // 生产任务明细条数
    }

    @Data
    public class level2Elm{
        private String msgType;             // 备注类型编码  默认text
        private String msgName;             // 备注名称
        private String message;             // 备注内容
    }

    @Data
    public class level3Elm{
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
