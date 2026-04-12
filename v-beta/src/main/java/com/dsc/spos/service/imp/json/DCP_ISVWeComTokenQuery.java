package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ISVWeComTokenQueryReq;
import com.dsc.spos.json.cust.res.DCP_ISVWeComTokenQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.thirdpart.wecom.DCPWeComUtils;
import com.google.gson.reflect.TypeToken;
import java.util.Map;

/**
 * 服务函数：DCP_ISVWeComTokenQuery
 * 服务说明：获取企微Token
 * @author jinzma
 * @since  2024-02-22
 */
public class DCP_ISVWeComTokenQuery extends SPosBasicService<DCP_ISVWeComTokenQueryReq, DCP_ISVWeComTokenQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_ISVWeComTokenQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_ISVWeComTokenQueryReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComTokenQueryReq>(){};
    }

    @Override
    protected DCP_ISVWeComTokenQueryRes getResponseType() {
        return new DCP_ISVWeComTokenQueryRes();
    }

    @Override
    protected DCP_ISVWeComTokenQueryRes processJson(DCP_ISVWeComTokenQueryReq req) throws Exception {
        DCP_ISVWeComTokenQueryRes res = this.getResponse();

        DCPWeComUtils dcpWeComUtils = new DCPWeComUtils();
        String accessToken = dcpWeComUtils.getAccessToken(dao,false);

        res.setAccessToken(accessToken);

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_ISVWeComTokenQueryReq req) throws Exception {
        return null;
    }
}
