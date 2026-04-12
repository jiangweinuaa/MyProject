package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;
import java.util.List;

/**
 * 服务函数：DCP_ISVWeComCustomTagUpdate
 * 服务说明：批量企微客户打标签
 * @author jinzma
 * @since  2024-01-25
 */
@Data
public class DCP_ISVWeComCustomTagUpdateRes extends JsonRes {
    private Datas datas;
    @Data
    public class Datas{
        private List<ExternalUserId> externalUserIdList;
    }
    @Data
    public class ExternalUserId {
        private String externalUserId;
        private String externalUserName;
        private String errorMsg;
    }
}
