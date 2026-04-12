package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

/**
 * 服务函数：ISV_WeComStaffActiveInfoSync
 * 服务说明：更新企微员工激活信息
 * @author jinzma
 * @since  2023-09-12
 */
@Data
public class ISV_WeComStaffActiveInfoSyncRes extends JsonRes {
    
    private List<level1Elm> datas;  //已激活成员列表，已激活过期的也会返回
    @Data
    public class level1Elm{
        private String userid;   //企业的成员userid。返回加密的userid
        private String type;     //激活码账号类型：1:基础账号，2:互通账号
        private String expire_time;   //过期时间
        private String active_time;   //激活时间
    }
}
