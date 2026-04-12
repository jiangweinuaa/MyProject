package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import java.util.List;

/**
 * 服务函数：DCP_ISVWeComGpCodeUpdate
 * 服务说明：社群活码更新
 * @author jinzma
 * @since  2024-02-28
 */
@Data
public class DCP_ISVWeComGpCodeUpdateReq extends JsonBasicReq {
    private Request request;
    @Data
    public class Request{
        private String gpCodeId;
        private String name;
        private String remark;
        private String autoCreate;
        private String baseName;
        private String baseId;
        private String logo;
        private List<Chat> chatList;
        private List<Shop> shopList;
    }
    @Data
    public class Chat {
        private String chatId;
    }
    @Data
    public class Shop {
        private String shopId;
    }
}
