package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;
import java.util.List;

/**
 * 服务函数：DCP_ISVWeComGpCodeQuery
 * 服务说明：社群活码查询
 * @author jinzma
 * @since  2024-02-28
 */
@Data
public class DCP_ISVWeComGpCodeQueryRes extends JsonRes {
    private List<Datas> datas;
    @Data
    public class Datas{
        private String gpCodeId;
        private String name;
        private String gpCodeUrl;
        private String autoCreate;
        private String baseName;
        private String baseId;
        private String remark;
        private String logo;
        private String logoUrl;
        private String createTime;
        private String lastModiTime;

        private List<Shop> shopList;
        private List<Chat> chatList;
    }
    @Data
    public class Shop{
        private String shopId;
        private String shopName;
    }
    @Data
    public class Chat{
        private String chatId;
        private String chatName;
    }
}
