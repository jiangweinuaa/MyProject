package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import java.util.List;

/**
 * 服务函数：DCP_ISVWeComQrCodeCreate
 * 服务说明：个人活码创建
 * @author jinzma
 * @since  2024-02-26
 */
@Data
public class DCP_ISVWeComQrCodeCreateReq extends JsonBasicReq {
    private Request request;
    @Data
    public class Request{
        private String name;
        private String remark;
        private String autoPass;
        private String logo;
        private List<User> userList;
    }
    @Data
    public class User {
        private String userId;
    }
}
