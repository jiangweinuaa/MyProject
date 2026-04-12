package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
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
public class DCP_ProductionOrderQuery_OpenReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private String eId;                  // 企业编号
        private String machShopNo;           // 生产机构编码
        private String stallId;              // 档口编号KDS必传
        private List<String> status;         // 订单状态 0.待审核1.订单开立 2.已接单 3. 已取消
        private List<String> productStatus;  // 生产状态，生产状态 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨
        private List<String> goodsStatus;    // 商品状态，0.待制作 1.制作中 2.已完成
        private String keyTxt;               // 关键字，支持查单号/联系人姓名/手机号
        private List<String> specList;       // 规格列表，根据规格名称反查匹配
        private level3Elm shipDate;          // Add 2021/6/1 配送日期区间
        private List<level2Elm> shipTime;    // 配送时段
        private String pluNo;                //
        private List<String> featureList;    // 多特征列表
        private String isModifyShip;         // 是否修改配送信息N.已修改，未提醒 Y.已提醒

    }

    @Data
    public class level2Elm{
        private String beginTime;             //  开始时间
        private String endTime;               //  结束时间
    }

    @Data
    public class level3Elm{
        private String beginDate;              // 开始日期
        private String endDate;                // 截止日期
    }
}
