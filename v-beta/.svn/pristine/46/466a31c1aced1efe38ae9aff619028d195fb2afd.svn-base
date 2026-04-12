package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

/**
 * 服務函數：POrderCreate
 *   說明：要货单列表查询
 * 服务说明：要货单列表查询
 * @author wangzyc
 * @since  2021-05-11
 */
@Data
public class DCP_POrderListQueryRes extends JsonRes {
    private level1Elm datas;
    private String sysDate; // 系统时间

    @Data
    public class level1Elm{
        private List<level2Elm> orderList;
    }

    @Data
    public class level2Elm {
        private String processERPNo;             // ERP单号
        private String porderNo;                 // 单据编号
        private String oType;                    // 来源单据类型：0: 普通要货 1：订单转要货 2:计划报单转要货 3蛋糕要货 4 千元用量周期要货  5节日要货
        private String ofNo;                     // 来源单号
        private String bDate;                    // 单据日期
        private String pTemplateNo;              // 模板编码
        private String pTemplateName;            // 模板名称
        private String isAdd;                    // 追加要货
        private String memo;                     // 备注
        private String status;                  // 状态
        private String createByName;             // 创建人明细
        private String rDate;                    // 需求日期
        private String rTime;                    // 需求时间
        private String totPqty;                  // 数量合计
        private String totCqty;                  // 品种合计
        private String totAmt;                   // 金额合计
        private String totDistriAmt;             // 进货金额合计
        private String isUrgentOrder;            // 是否紧急要货
        private String receiptOrgNo;             // 发货组织
        private String revoke_Day;
        private String revoke_Time;
    }
}
