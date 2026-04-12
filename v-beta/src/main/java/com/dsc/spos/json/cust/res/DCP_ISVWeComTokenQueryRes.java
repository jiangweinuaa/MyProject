package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 服务函数：DCP_ISVWeComTokenQuery
 * 服务说明：获取企微Token
 * @author jinzma
 * @since  2024-02-22
 */
public class DCP_ISVWeComTokenQueryRes extends JsonRes {
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
