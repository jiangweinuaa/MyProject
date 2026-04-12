package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_WarehouseDeleteReq;
import com.dsc.spos.json.cust.res.DCP_WarehouseDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;

public class DCP_WarehouseDelete extends SPosAdvanceService<DCP_WarehouseDeleteReq, DCP_WarehouseDeleteRes>
{

    @Override
    protected void processDUID(DCP_WarehouseDeleteReq req, DCP_WarehouseDeleteRes res) throws Exception
    {

        DelBean db1 = new DelBean("DCP_WAREHOUSE");
        db1.addCondition("ORGANIZATIONNO", new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR));
        db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        db1.addCondition("WAREHOUSE", new DataValue(req.getRequest().getWarehouseNo(), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db1));

        DelBean db2 = new DelBean("DCP_WAREHOUSE_LANG");
        db2.addCondition("ORGANIZATIONNO", new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR));
        db2.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        db2.addCondition("WAREHOUSE", new DataValue(req.getRequest().getWarehouseNo(), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db2));

        DelBean db3 = new DelBean("DCP_WAREHOUSE_RANGE");
        db3.addCondition("ORGANIZATIONNO", new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR));
        db3.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        db3.addCondition("WAREHOUSE", new DataValue(req.getRequest().getWarehouseNo(), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db3));

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_WarehouseDeleteReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_WarehouseDeleteReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_WarehouseDeleteReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_WarehouseDeleteReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if(req.getRequest()==null)
        {
            errMsg.append("requset不能为空值 ");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }


        if (isFail)
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_WarehouseDeleteReq> getRequestType()
    {
        return new TypeToken<DCP_WarehouseDeleteReq>(){};
    }

    @Override
    protected DCP_WarehouseDeleteRes getResponseType()
    {
        return new DCP_WarehouseDeleteRes();
    }


}
