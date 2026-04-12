package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * 服务函数：DCP_ISVWeComCodeQuery_Open
 * 服务说明：商城查询二维码
 * @author jinzma
 * @since  2024-03-14
 */
@Data
public class DCP_ISVWeComCodeQuery_OpenReq extends JsonBasicReq {
    private Request request;
    @Data
    public class Request {
        private String codeType;             //二维码类型qrCode个人活码
        private String shopId;               //门店ID
        private String memberId;             //会员ID
        private String unionId;              //微信unionid
        private String openId;               //微信openid
        private String fissionBillNo;        //裂变营销活动单号
        private String isFissionCoupon;      //是否裂变营销
    }
}
