package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class DCP_GetAI_AccessTokenRes extends JsonBasicRes {
    private Datas data;

    @Data
    public static class Datas {
        private String accessToken;
    }

}
