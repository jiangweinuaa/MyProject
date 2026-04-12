package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ProcessPlanCancelReq;
import com.dsc.spos.json.cust.res.DCP_ProcessPlanCancelRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DCP_ProcessPlanCancel extends SPosAdvanceService<DCP_ProcessPlanCancelReq, DCP_ProcessPlanCancelRes>
{


    @Override
    protected void processDUID(DCP_ProcessPlanCancelReq req, DCP_ProcessPlanCancelRes res) throws Exception
    {
        String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        String sysDate =	new SimpleDateFormat("yyyyMMdd").format(new Date());
        String sysTime =	new SimpleDateFormat("HHmmss").format(new Date());


        List<DCP_ProcessPlanCancelReq.level2> dataList = req.getRequest().getDataList();

        for (DCP_ProcessPlanCancelReq.level2 level2 : dataList)
        {

            UptBean ub1 = null;
            ub1 = new UptBean("dcp_processplan");
            ub1.addUpdateValue("STATUS", new DataValue("2", Types.VARCHAR));
            ub1.addUpdateValue("CONFIRMBY", new DataValue(req.getRequest().getOpNo(), Types.VARCHAR));
            ub1.addUpdateValue("CONFIRM_DATE", new DataValue(sysDate, Types.VARCHAR));
            ub1.addUpdateValue("CONFIRM_TIME", new DataValue(sysTime, Types.VARCHAR));


            //condition
            ub1.addCondition("EID", new DataValue(req.getRequest().geteId(), Types.VARCHAR));
            ub1.addCondition("ORGANIZATIONNO", new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR));
            ub1.addCondition("SHOPID", new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR));
            ub1.addCondition("PROCESSPLANNO", new DataValue(level2.getProcessPlanNo(), Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));

        }

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ProcessPlanCancelReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ProcessPlanCancelReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ProcessPlanCancelReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ProcessPlanCancelReq req) throws Exception
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

        if (Check.Null(req.getRequest().getOpNo()))
        {
            errMsg.append("opNo不能为空 ");
            isFail = true;
        }



        List<DCP_ProcessPlanCancelReq.level2> dataList = req.getRequest().getDataList();

        if(dataList==null || dataList.size()==0)
        {
            errMsg.append("dataList必须有记录 ");
            isFail = true;
        }
        else
        {
            for (DCP_ProcessPlanCancelReq.level2 level2 : dataList)
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
    protected TypeToken<DCP_ProcessPlanCancelReq> getRequestType()
    {
        return new TypeToken<DCP_ProcessPlanCancelReq>(){};
    }

    @Override
    protected DCP_ProcessPlanCancelRes getResponseType()
    {
        return new DCP_ProcessPlanCancelRes();
    }
}
