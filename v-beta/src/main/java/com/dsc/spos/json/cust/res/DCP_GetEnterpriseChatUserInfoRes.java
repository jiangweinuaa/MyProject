package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;

@Data
public class DCP_GetEnterpriseChatUserInfoRes extends JsonBasicRes {
    private DCP_GetEnterpriseChatUserInfoRes.Level1Elm datas;
    @Data
    public static class Level1Elm {
        private String userId;
        private String status;
    }
}
