package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * @description: 预约列表查询
 * @author: wangzyc
 * @create: 2021-07-28
 */
@Data
public class DCP_ReserveOrderQueryReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm {
        private String shopId;          // 所属门店
        private String reserveNo;          // 预约单号
        private String status;          // 单据状态 0待审核 1待消费 2已消费 3已失效
        private String createOpId;       // 创建人ID，若为商城渠道传openId
        private String keyTxt;          // 关键词，项目名称+编号模糊查询
        private String memberKeyTxt;    // 关键词2，客户姓名+会员号+手机号模糊搜索
        private String AdvisorKeyTxt;    // 关键词3，顾问姓名+工号模糊搜索
        private String loadDocType;    // 来源渠道类型 商城小程序：MINI POS收银：POS
        private String beginDate;    // 预约开始日期
        private String endDate;      // 预约结束日期
    }
}
