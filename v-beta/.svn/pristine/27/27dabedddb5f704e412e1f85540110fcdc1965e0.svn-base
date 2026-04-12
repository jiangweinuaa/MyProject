package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_WarehouseEnableReq;
import com.dsc.spos.json.cust.res.DCP_WarehouseEnableRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DCP_WarehouseEnable extends SPosAdvanceService<DCP_WarehouseEnableReq, DCP_WarehouseEnableRes>
{

    @Override
    protected void processDUID(DCP_WarehouseEnableReq req, DCP_WarehouseEnableRes res) throws Exception
    {
        String createTime = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());
        String employeeNo = req.getEmployeeNo();
        String eId = req.geteId();
        String organizationNO = req.getRequest().getOrganizationNo();
        String warehouseNo = req.getRequest().getWarehouseNo();
        String opType = req.getRequest().getOpType();

        UptBean ub1 = new UptBean("DCP_WAREHOUSE");
        if("1".equals(opType)) {
            ub1.addUpdateValue("STATUS", new DataValue("100", Types.VARCHAR));
        }
        if("2".equals(opType)) {
            ub1.addUpdateValue("STATUS", new DataValue("0", Types.VARCHAR));
        }
        ub1.addUpdateValue("MODIFYBY", new DataValue(employeeNo, Types.VARCHAR));
        ub1.addUpdateValue("MODIFY_DTIME", new DataValue(createTime, Types.DATE));

        //condition
        ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        ub1.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
        ub1.addCondition("WAREHOUSE", new DataValue(warehouseNo, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(ub1));


        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_WarehouseEnableReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_WarehouseEnableReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_WarehouseEnableReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_WarehouseEnableReq req) throws Exception
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
    protected TypeToken<DCP_WarehouseEnableReq> getRequestType()
    {
        return new TypeToken<DCP_WarehouseEnableReq>(){};
    }

    @Override
    protected DCP_WarehouseEnableRes getResponseType()
    {
        return new DCP_WarehouseEnableRes();
    }


}
