package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * 服务函数：DCP_ISVWeComMediaMiniCreate
 * 服务说明：小程序页面路径创建
 * @author jinzma
 * @since  2024-03-07
 */
@Data
public class DCP_ISVWeComMediaMiniCreateReq extends JsonBasicReq {

    private Request request;

    @Data
    public class Request {
        private String title;
        private String appId;
        private String miniUrl;
        private String mediaId;

    }

}
