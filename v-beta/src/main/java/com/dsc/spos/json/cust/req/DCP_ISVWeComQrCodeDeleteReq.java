package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import java.util.List;

/**
 * 服务函数：DCP_ISVWeComQrCodeDelete
 * 服务说明：个个人活码删除
 * @author jinzma
 * @since  2024-02-26
 */
@Data
public class DCP_ISVWeComQrCodeDeleteReq extends JsonBasicReq {
    private Request request;
    @Data
    public class Request{
        private List<QrCode> qrCodeList;
    }
    @Data
    public class QrCode{
        private String qrCodeId;
    }
}
