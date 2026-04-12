package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_OrderQueryReq;
import com.dsc.spos.json.cust.req.DCP_ThridOrderQuery_OpenReq;
import com.dsc.spos.json.cust.res.DCP_OrderQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.util.Map;

public class DCP_ThridOrderQuery_Open  extends SPosBasicService<DCP_ThridOrderQuery_OpenReq, DCP_OrderQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_ThridOrderQuery_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；

        if(req.getRequest()==null)
        {
            isFail = true;
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (Check.Null(req.getRequest().getOrderNo()))
        {
            errCt++;
            errMsg.append("单号orderNo不可为空值, ");
            isFail = true;
        }
        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_ThridOrderQuery_OpenReq> getRequestType() {
        return new TypeToken<DCP_ThridOrderQuery_OpenReq>(){};
    }

    @Override
    protected DCP_OrderQueryRes getResponseType() {
        return new DCP_OrderQueryRes();
    }

    @Override
    protected DCP_OrderQueryRes processJson(DCP_ThridOrderQuery_OpenReq req) throws Exception {

        DCP_OrderQueryRes res=this.getResponse();
        DCP_OrderQueryReq queryReq = new DCP_OrderQueryReq();
        queryReq.setPageSize(10);
        queryReq.setPageNumber(1);
        queryReq.seteId(req.geteId());
        queryReq.setApiUser(req.getApiUser());
        queryReq.setLangType(req.getLangType());
        DCP_OrderQueryReq.levelRequest queryReq_quest = queryReq.new levelRequest();
        queryReq_quest.seteId(req.geteId());
        queryReq_quest.setOrderNo(req.getRequest().getOrderNo());
        queryReq.setRequest(queryReq_quest);

        DCP_OrderQuery orderQuery = new DCP_OrderQuery();
        orderQuery.setDao(this.dao);
        res = orderQuery.processJson(queryReq);

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_ThridOrderQuery_OpenReq req) throws Exception {
        return null;
    }
}
