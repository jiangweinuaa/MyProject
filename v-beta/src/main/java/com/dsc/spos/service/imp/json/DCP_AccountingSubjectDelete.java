package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_AccountingSubjectDeleteReq;
import com.dsc.spos.json.cust.res.DCP_AccountingSubjectDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.sql.Types;
import java.util.List;

public class DCP_AccountingSubjectDelete extends SPosAdvanceService<DCP_AccountingSubjectDeleteReq, DCP_AccountingSubjectDeleteRes>
{


    @Override
    protected void processDUID(DCP_AccountingSubjectDeleteReq req, DCP_AccountingSubjectDeleteRes res) throws Exception
    {
        String[] subjectList = req.getRequest().getSubjectList();

        for (int i = 0; i < subjectList.length; i++)
        {
            DelBean db1 = new DelBean("DCP_ACCOUNTINGSUBJECT");
            db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
            db1.addCondition("SUBJECTID", new DataValue(subjectList[i], Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1));
        }

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_AccountingSubjectDeleteReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_AccountingSubjectDeleteReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_AccountingSubjectDeleteReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_AccountingSubjectDeleteReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if(req.getRequest()==null)
        {
            errMsg.append("requset不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        String[] subjectList = req.getRequest().getSubjectList();

        if(subjectList.length==0)
        {
            errMsg.append("科目列表不能为空值 ");
            isFail = true;
        }
        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_AccountingSubjectDeleteReq> getRequestType()
    {
        return new TypeToken<DCP_AccountingSubjectDeleteReq>(){};
    }

    @Override
    protected DCP_AccountingSubjectDeleteRes getResponseType()
    {
        return new DCP_AccountingSubjectDeleteRes();
    }



}
