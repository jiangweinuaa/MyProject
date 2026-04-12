package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import java.util.List;

/**
 * 服务函数：DCP_CustomerUpdate
 * 服务说明：大客户修改
 * @author jinzma
 * @since  2023-12-27
 */
@Data
public class DCP_CustomerUpdateReq extends JsonBasicReq {

    private Request request;
    @Data
    public class Request{
        private String customerNo;
        private String copyWriting;
        private List<ServiceStaff> serviceStaffList;
    }
    @Data
    public class ServiceStaff {
        private String opNo;
    }
}
