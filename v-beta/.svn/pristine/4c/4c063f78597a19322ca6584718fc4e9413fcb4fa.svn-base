package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

/**
 * 服务函数：DCP_ISVWeComGroupChatQuery
 * 服务说明：查询企微客户群列表
 * @author jinzma
 * @since  2024-01-15
 */
@Data
public class DCP_ISVWeComGroupChatQueryRes extends JsonRes {

    private List<Datas> datas;

    @Data
    public class Datas {
        private String chatId;
        private String name;
        private String status;
        private String userId;
        private String userName;
        private String memberCnt;
        private String notice;
        private String createTime;
    }
}
