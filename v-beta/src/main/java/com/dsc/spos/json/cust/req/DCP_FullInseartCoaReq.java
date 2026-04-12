package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_FullInseartCoaReq  extends JsonBasicReq {

    private LevelElm request;

    @Data
    public class LevelElm{

        private String accountId;
        private String oprType;//1 启用 2 禁用
    }

}
