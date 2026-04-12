package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ProcessPlanDeleteReq;
import com.dsc.spos.json.cust.res.DCP_ProcessPlanDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.sql.Types;
import java.util.List;

public class DCP_ProcessPlanDelete extends SPosAdvanceService<DCP_ProcessPlanDeleteReq, DCP_ProcessPlanDeleteRes>
{


    @Override
    protected void processDUID(DCP_ProcessPlanDeleteReq req, DCP_ProcessPlanDeleteRes res) throws Exception
    {

        List<DCP_ProcessPlanDeleteReq.level2> dataList = req.getRequest().getDataList();

        for (DCP_ProcessPlanDeleteReq.level2 level2 : dataList)
        {
            DelBean db1 = new DelBean("dcp_processplan");
            db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
            db1.addCondition("ORGANIZATIONNO", new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR));
            db1.addCondition("SHOPID", new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR));
            db1.addCondition("PROCESSPLANNO", new DataValue(level2.getProcessPlanNo(), Types.VARCHAR));

            this.addProcessData(new DataProcessBean(db1));

            db1 = new DelBean("dcp_processplan_detail");
            db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
            db1.addCondition("ORGANIZATIONNO", new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR));
            db1.addCondition("SHOPID", new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR));
            db1.addCondition("PROCESSPLANNO", new DataValue(level2.getProcessPlanNo(), Types.VARCHAR));

            this.addProcessData(new DataProcessBean(db1));

        }

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ProcessPlanDeleteReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ProcessPlanDeleteReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ProcessPlanDeleteReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ProcessPlanDeleteReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if(req.getRequest()==null)
        {
            errMsg.append("requset不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        if (Check.Null(req.getRequest().geteId()))
        {
            errMsg.append("eId不能为空 ");
            isFail = true;
        }
        if (Check.Null(req.getRequest().getOrganizationNo()))
        {
            errMsg.append("organizationNo不能为空 ");
            isFail = true;
        }

        List<DCP_ProcessPlanDeleteReq.level2> dataList = req.getRequest().getDataList();

        if(dataList==null || dataList.size()==0)
        {
            errMsg.append("dataList必须有记录 ");
            isFail = true;
        }
        else
        {
            for (DCP_ProcessPlanDeleteReq.level2 level2 : dataList)
            {
                if (Check.Null(level2.getProcessPlanNo()))
                {
                    errMsg.append("processPlanNo不能为空 ");
                    isFail = true;
                }
            }
        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_ProcessPlanDeleteReq> getRequestType()
    {
        return new TypeToken<DCP_ProcessPlanDeleteReq>(){};
    }

    @Override
    protected DCP_ProcessPlanDeleteRes getResponseType()
    {
        return new DCP_ProcessPlanDeleteRes();
    }
}
