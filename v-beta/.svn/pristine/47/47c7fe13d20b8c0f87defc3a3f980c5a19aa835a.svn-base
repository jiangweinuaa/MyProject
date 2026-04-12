package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ROrderDeleteReq;
import com.dsc.spos.json.cust.res.DCP_ROrderDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;
import java.util.Map;

public class DCP_ROrderDelete extends SPosAdvanceService<DCP_ROrderDeleteReq, DCP_ROrderDeleteRes> {

    @Override
    protected boolean isVerifyFail(DCP_ROrderDeleteReq req) throws Exception {
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
    protected String getQuerySql(DCP_ROrderDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    protected void processDUID(DCP_ROrderDeleteReq req, DCP_ROrderDeleteRes res) throws Exception {
        // TODO Auto-generated method stub

        DelBean db1 = new DelBean("DCP_RORDER");
        db1.addCondition("ORGANIZATIONNO", new DataValue(req.getOrganizationNO(), Types.VARCHAR));
        db1.addCondition("RORDERNO", new DataValue(req.getRequest().getROrderNo(), Types.VARCHAR));
        db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db1));

        DelBean db2 = new DelBean("DCP_RORDER_DETAIL");
        db2.addCondition("ORGANIZATIONNO", new DataValue(req.getOrganizationNO(), Types.VARCHAR));
        db2.addCondition("RORDERNO", new DataValue(req.getRequest().getROrderNo(), Types.VARCHAR));
        db2.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db2));

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ROrderDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ROrderDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ROrderDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    protected TypeToken<DCP_ROrderDeleteReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_ROrderDeleteReq>(){};
    }
    @Override
    protected DCP_ROrderDeleteRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_ROrderDeleteRes();
    }

}
