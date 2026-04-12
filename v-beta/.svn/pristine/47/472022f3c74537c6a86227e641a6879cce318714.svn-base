package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

/**
 * 服务函数：DCP_ISVWeComTagCreate
 * 服务说明：企微标签创建
 * @author jinzma
 * @since  2023-09-14
 */
@Data
public class DCP_ISVWeComTagCreateReq extends JsonBasicReq {
    private Request request;
    @Data
    public class Request{
        private String groupId;
        private String groupName;
        private List<Tag> tagList;
    }
    @Data
    public class Tag{
        private String tagId;
        private String tagName;
    }

}
