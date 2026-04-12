package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_MoDeleteReq;
import com.dsc.spos.json.cust.res.DCP_MoDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;
import java.util.Map;

public class DCP_MoDelete extends SPosAdvanceService<DCP_MoDeleteReq, DCP_MoDeleteRes>
{

    @Override
    protected void processDUID(DCP_MoDeleteReq req, DCP_MoDeleteRes res) throws Exception
    {

        String eId = req.geteId();
        String moNo = req.getRequest().getMoNo();
        String organizationNO = req.getOrganizationNO();

        DelBean db1 = new DelBean("MES_MO");
        db1.addCondition("MONO", new DataValue(moNo, Types.VARCHAR));
        db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db1.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db1));

        DelBean db2 = new DelBean("MES_MO_DETAIL");
        db2.addCondition("MONO", new DataValue(moNo, Types.VARCHAR));
        db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db2.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db2));

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_MoDeleteReq req) throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_MoDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_MoDeleteReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_MoDeleteReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_MoDeleteReq.levelElm request = req.getRequest();

        if (isFail)
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_MoDeleteReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_MoDeleteReq>(){};
    }

    @Override
    protected DCP_MoDeleteRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_MoDeleteRes();
    }

}

