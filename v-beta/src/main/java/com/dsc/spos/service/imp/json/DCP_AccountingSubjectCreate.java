package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_AccountingSubjectCreateReq;
import com.dsc.spos.json.cust.res.DCP_AccountingSubjectCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DCP_AccountingSubjectCreate extends SPosAdvanceService<DCP_AccountingSubjectCreateReq, DCP_AccountingSubjectCreateRes>
{

    @Override
    protected void processDUID(DCP_AccountingSubjectCreateReq req, DCP_AccountingSubjectCreateRes res) throws Exception
    {
        String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        String subjectId = req.getRequest().getSubjectId();
        String subjectName = req.getRequest().getSubjectName();
        String direction = req.getRequest().getDirection();

        String[] columnsName = {
                "EID","SUBJECTID","SUBJECTNAME","DIRECTION","AUXILIARYTYPE","MEMO","LASTMODITIME"
        };
        DataValue[] insValueDetail = new DataValue[]
                {
                        new DataValue(req.geteId(), Types.VARCHAR),
                        new DataValue(subjectId, Types.VARCHAR),
                        new DataValue(subjectName, Types.VARCHAR),
                        new DataValue(direction, Types.VARCHAR),
                        new DataValue(req.getRequest().getAuxiliaryType(), Types.VARCHAR),
                        new DataValue(req.getRequest().getMemo(), Types.VARCHAR),
                        new DataValue(lastmoditime, Types.DATE)
                };
        InsBean ib2 = new InsBean("DCP_ACCOUNTINGSUBJECT", columnsName);
        ib2.addValues(insValueDetail);
        this.addProcessData(new DataProcessBean(ib2));

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_AccountingSubjectCreateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_AccountingSubjectCreateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_AccountingSubjectCreateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_AccountingSubjectCreateReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if(req.getRequest()==null)
        {
            errMsg.append("requset不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        String subjectId = req.getRequest().getSubjectId();
        String subjectName = req.getRequest().getSubjectName();
        String direction = req.getRequest().getDirection();

        if(Check.Null(subjectId))
        {
            errMsg.append("会计科目编码不能为空值 ");
            isFail = true;
        }
        if(Check.Null(subjectName))
        {
            errMsg.append("会计科目名称不能为空值 ");
            isFail = true;
        }
        if(Check.Null(direction))
        {
            errMsg.append("余额方向不能为空值 ");
            isFail = true;
        }
        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_AccountingSubjectCreateReq> getRequestType()
    {
        return new TypeToken<DCP_AccountingSubjectCreateReq> (){};
    }

    @Override
    protected DCP_AccountingSubjectCreateRes getResponseType()
    {
        return new DCP_AccountingSubjectCreateRes();
    }


}
