package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import java.util.List;
/**
 * 服务函数：DCP_ISVWeComCustomQuery
 * 服务说明：查询企微客户列表
 * @author jinzma
 * @since  2024-01-24
 */
@Data
public class DCP_ISVWeComCustomQueryReq extends JsonBasicReq {
    private Request request;
    @Data
    public class Request {
        private String keyTxt;
        private String mobile;
        private String userId;
        private String status;
        private String beginDate;
        private String endDate;
        private List<Tag> tagList;
    }
    @Data
    public class Tag {
        private String tagId;
    }
}

