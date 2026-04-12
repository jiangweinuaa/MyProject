package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;
import org.omg.CORBA.PUBLIC_MEMBER;

import java.util.List;

/**
 * 服务函数：DCP_ISVWeComQrCodeQuery
 * 服务说明：个人活码查询
 * @author jinzma
 * @since  2024-02-26
 */
@Data
public class DCP_ISVWeComQrCodeQueryRes extends JsonRes {
    private List<Datas> datas;
    @Data
    public class Datas {
        private String qrCodeId;
        private String name;
        private String remark;
        private String qrCodeUrl;
        private String createTime;
        private String lastModiTime;
        private String autoPass;
        private String logo;
        private String logoUrl;

        private List<User> userList;
    }
    @Data
    public class User {
        private String userId;
        private String userName;
    }

}
