package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import java.util.List;

/**
 * 服务函数：DCP_ISVWeComMediaMiniDelete
 * 服务说明：小程序页面路径删除
 * @author jinzma
 * @since  2024-03-07
 */
@Data
public class DCP_ISVWeComMediaMiniDeleteReq extends JsonBasicReq {

    private Request request;
    @Data
    public class Request {
        private List<Mini> miniIdList;
    }
    @Data
    public class Mini {
        private String miniId;
    }
}
