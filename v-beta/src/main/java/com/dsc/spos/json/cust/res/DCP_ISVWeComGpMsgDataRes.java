package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

/**
 * 服务函数：DCP_ISVWeComGpMsgData
 * 服务说明：群发消息结果查询
 * @author jinzma
 * @since  2024-03-04
 */
@Data
public class DCP_ISVWeComGpMsgDataRes extends JsonRes {
    private Datas datas;
    @Data
    public class Datas {
        private String sendUser;
        private String sendExternalUser;
        private String sendChat;
        private String notSendUser;
        private String notSendExternalUser;
        private String notSendChat;
    }
}
