package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ProcessTask0DeleteReq;
import com.dsc.spos.json.cust.res.DCP_ProcessTask0DeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import java.sql.Types;
import java.util.List;

public class DCP_ProcessTask0Delete extends SPosAdvanceService<DCP_ProcessTask0DeleteReq, DCP_ProcessTask0DeleteRes>
{


    @Override
    protected void processDUID(DCP_ProcessTask0DeleteReq req, DCP_ProcessTask0DeleteRes res) throws Exception
    {
        List<DCP_ProcessTask0DeleteReq.level2> dataList = req.getRequest().getDataList();

        for (DCP_ProcessTask0DeleteReq.level2 level2 : dataList)
        {
            DelBean db1 = new DelBean("DCP_PROCESSTASK0");
            db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
            db1.addCondition("ORGANIZATIONNO", new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR));
            db1.addCondition("SHOPID", new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR));
            db1.addCondition("PROCESSTASKNO", new DataValue(level2.getProcessTaskNo(), Types.VARCHAR));

            this.addProcessData(new DataProcessBean(db1));

            db1 = new DelBean("DCP_PROCESSTASK0_DETAIL");
            db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
            db1.addCondition("ORGANIZATIONNO", new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR));
            db1.addCondition("SHOPID", new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR));
            db1.addCondition("PROCESSTASKNO", new DataValue(level2.getProcessTaskNo(), Types.VARCHAR));

            this.addProcessData(new DataProcessBean(db1));
        }

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ProcessTask0DeleteReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ProcessTask0DeleteReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ProcessTask0DeleteReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ProcessTask0DeleteReq req) throws Exception
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

        List<DCP_ProcessTask0DeleteReq.level2> dataList = req.getRequest().getDataList();

        if(dataList==null || dataList.size()==0)
        {
            errMsg.append("dataList必须有记录 ");
            isFail = true;
        }
        else
        {
            for (DCP_ProcessTask0DeleteReq.level2 level2 : dataList)
            {
                if (Check.Null(level2.getProcessTaskNo()))
                {
                    errMsg.append("processTaskNo不能为空 ");
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
    protected TypeToken<DCP_ProcessTask0DeleteReq> getRequestType()
    {
        return new TypeToken<DCP_ProcessTask0DeleteReq>(){};
    }

    @Override
    protected DCP_ProcessTask0DeleteRes getResponseType()
    {
        return new DCP_ProcessTask0DeleteRes();
    }
}
