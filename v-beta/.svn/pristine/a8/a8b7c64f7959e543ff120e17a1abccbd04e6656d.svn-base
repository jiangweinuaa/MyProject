package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_RouteDeleteReq;
import com.dsc.spos.json.cust.res.DCP_RouteDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;

public class DCP_RouteDelete extends SPosAdvanceService<DCP_RouteDeleteReq, DCP_RouteDeleteRes> {

    @Override
    protected boolean isVerifyFail(DCP_RouteDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if (isFail)
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }


    /**
     * 查询多语言信息
     */
    @Override
    protected String getQuerySql(DCP_RouteDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    protected void processDUID(DCP_RouteDeleteReq req, DCP_RouteDeleteRes res) throws Exception {
        // TODO Auto-generated method stub

        DelBean db1 = new DelBean("MES_ROUTE");
        db1.addCondition("ROUTENO", new DataValue(req.getRequest().getRouteNo(), Types.VARCHAR));
        db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db1));

        DelBean db2 = new DelBean("MES_ROUTE_DETAIL");
        db2.addCondition("ROUTENO", new DataValue(req.getRequest().getRouteNo(), Types.VARCHAR));
        db2.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db2));

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_RouteDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_RouteDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_RouteDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    protected TypeToken<DCP_RouteDeleteReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_RouteDeleteReq>(){};
    }
    @Override
    protected DCP_RouteDeleteRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_RouteDeleteRes();
    }

}
