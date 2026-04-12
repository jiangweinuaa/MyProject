package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

/**
 * 服務函數：DCP_EmployeeInfoUpdate_Open
 *   說明：员工信息修改
 * 服务说明：员工信息修改
 * @author wangzyc
 * @since  2021-4-25
 */
@Data
public class DCP_EmployeeInfoUpdate_OpenReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private String machShopNo;  // 生产门店编号
        private String departNo;    // 所属部门
        private List<String> opList; //员工编号
        private String activation;  // 是否激活Y/N
    }

}
