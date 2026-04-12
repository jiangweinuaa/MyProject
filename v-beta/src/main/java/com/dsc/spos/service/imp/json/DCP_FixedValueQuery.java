package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_FixedValueQueryReq;
import com.dsc.spos.json.cust.res.DCP_FixedValueQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.ConvertUtils;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

public class DCP_FixedValueQuery extends SPosBasicService<DCP_FixedValueQueryReq, DCP_FixedValueQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_FixedValueQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected DCP_FixedValueQueryRes processJson(DCP_FixedValueQueryReq req) throws Exception {

        String sql =
                "SELECT\n" +
                "VALUEID, VALUENAME\n" +
                "FROM DCP_FIXEDVALUE\n" +
                "WHERE KEY = ':KEY'";

        sql = sql.replace(":KEY", req.getRequest().getKey());

        List<Map<String, Object>> searchResults = this.doQueryData(sql, null);

        List<DCP_FixedValueQueryRes.Level1Elm> data = ConvertUtils.convertValue(searchResults, DCP_FixedValueQueryRes.Level1Elm.class);

        DCP_FixedValueQueryRes response = getResponse();
        response.setDatas(data);
        return response;
    }

    @Override
    protected TypeToken<DCP_FixedValueQueryReq> getRequestType() {
        return new TypeToken<DCP_FixedValueQueryReq>(){};
    }

    @Override
    protected DCP_FixedValueQueryRes getResponseType() {
        return new DCP_FixedValueQueryRes();
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_FixedValueQueryReq req) throws Exception {
        return null;
    }
}
