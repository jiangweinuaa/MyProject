package com.dsc.spos.service.imp.json;


import com.dsc.spos.json.cust.req.DCP_CostLevelDetailUpdateReq;
import com.dsc.spos.json.cust.res.DCP_CostLevelDetailUpdateRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;
import java.util.Map;

public class DCP_CostLevelDetailUpdate extends SPosBasicService<DCP_CostLevelDetailUpdateReq, DCP_CostLevelDetailUpdateRes> {

    @Override
    protected boolean isVerifyFail(DCP_CostLevelDetailUpdateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CostLevelDetailUpdateReq> getRequestType() {
        return new TypeToken<DCP_CostLevelDetailUpdateReq>(){};
    }

    @Override
    protected DCP_CostLevelDetailUpdateRes getResponseType() {
        return new DCP_CostLevelDetailUpdateRes();
    }

    @Override
    protected DCP_CostLevelDetailUpdateRes processJson(DCP_CostLevelDetailUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_CostLevelDetailUpdateReq req) throws Exception {
        return "";
    }
}
