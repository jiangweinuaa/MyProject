package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

/**
 * 服务函数：DCP_ISVWeComCodeQuery_Open
 * 服务说明：商城查询二维码
 * @author jinzma
 * @since  2024-03-14
 */
@Data
public class DCP_ISVWeComCodeQuery_OpenRes extends JsonRes {
    private Datas datas;
    @Data
    public class Datas{
        private String codeType;        //二维码类型qrCode个人活码
        private String codeUrl;         //二维码链接
        private String externalUserId;  //外部客户ID
    }
}
