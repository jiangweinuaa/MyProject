package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * @description: 订单状态更新
 * @author: wangzyc
 * @create: 2022-01-25
 */
@Data
public class DCP_OrderStatusModifyReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private String eId; // 企业编号
        private String orderNo; // 订单编号
        private String status; // 单据状态 当单据状态<>ALL时，必传，否则不传 1.订单开立 2.已接单 3.已拒单  8.待提货 9.待配送 10.已发货 11.已完成 12.已退单
        //枚举: -1-物流预下单；,0-物流已下单；,1-物流已接单；,2-物流已取件；,3-用户签收；,4-物流异常或取消；,5-手动撤销；,6-配送员到店；,7-物流重下单；,8-货到物流中心；,9-消费者七天未取件；--开发中
        private String deliverystatus; 
        private String opNo; // 操作人编码
        private String opName; // 操作人姓名
        
        private String delName;     // 配送员姓名
        private String delTelephone;// 配送员电话
        private String deliveryType;// 物流类型
        private String deliveryNo;  // 物流单号
    }
}
