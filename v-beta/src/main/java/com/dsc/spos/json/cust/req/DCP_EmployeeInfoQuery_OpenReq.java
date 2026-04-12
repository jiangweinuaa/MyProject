package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * 服務函數：DCP_EmployeeInfoQuery_Open
 *   說明：员工信息查询
 * 服务说明：员工信息查询
 * @author wangzyc
 * @since  2021-4-14
 */
@Data
public class DCP_EmployeeInfoQuery_OpenReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private String machShopNo;      // 生产门店编号
        private String opNo;            // 员工编号
        private String activation;      // 是否激活Y/N，空表示查全部
        private String beginDate;       // 开始日期  格式yyyyMMdd
        private String endDate;         // 截止日期  格式yyyyMMdd
    }
}
