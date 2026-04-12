package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

/**
 * 服务函数：DCP_ISVWeComTagQuery
 * 服务说明：企微标签查询
 * @author jinzma
 * @since  2023-09-14
 */
@Data
public class DCP_ISVWeComTagQueryRes extends JsonRes {
    private List<Group> datas;
    @Data
    public class Group{
        private String groupId;
        private String groupName;
        private String groupOrder;
        private List<Tag> tagList;
    }
    @Data
    public class Tag{
        private String tagId;
        private String tagName;
        private String tagOrder;
    }
}
