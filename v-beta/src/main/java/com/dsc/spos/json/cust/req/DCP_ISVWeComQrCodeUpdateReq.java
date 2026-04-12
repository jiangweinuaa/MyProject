package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import java.util.List;

/**
 * 服务函数：DCP_ISVWeComQrCodeUpdate
 * 服务说明：个人活码更新
 * @author jinzma
 * @since  2024-02-27
 */
@Data
public class DCP_ISVWeComQrCodeUpdateReq extends JsonBasicReq {
    private Request request;
    @Data
    public class Request{
        private String qrCodeId;
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
