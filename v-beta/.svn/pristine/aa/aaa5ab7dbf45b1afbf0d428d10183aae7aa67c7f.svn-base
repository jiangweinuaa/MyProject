package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import java.util.List;

/**
 * 服务函数：DCP_ISVWeComMediaLinkDelete
 * 服务说明：网页链接删除
 * @author jinzma
 * @since  2024-03-07
 */
@Data
public class DCP_ISVWeComMediaLinkDeleteReq extends JsonBasicReq {

    private Request request;
    @Data
    public class Request {
        private List<Link> linkIdList;
    }
    @Data
    public class Link {
        private String linkId;
    }
}
