package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;
import java.util.List;
/**
 * 服务函数：DCP_ISVWeComCustomQuery
 * 服务说明：查询企微客户列表
 * @author jinzma
 * @since  2024-01-24
 */
@Data
public class DCP_ISVWeComCustomQueryRes extends JsonRes {
    private List<Datas> datas;
    @Data
    public class Datas{
        private String externalUserId;
        private String name;
        private String status;
        private String lossTime;
        private List<Tag> tagList;
    }
    @Data
    public class Tag {
        private String tagName;
        private String tagId;
    }
}
