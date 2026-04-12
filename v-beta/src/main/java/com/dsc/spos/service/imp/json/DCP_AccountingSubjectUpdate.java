package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_AccountingSubjectUpdateReq;
import com.dsc.spos.json.cust.res.DCP_AccountingSubjectUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DCP_AccountingSubjectUpdate extends SPosAdvanceService<DCP_AccountingSubjectUpdateReq, DCP_AccountingSubjectUpdateRes>
{

    @Override
    protected void processDUID(DCP_AccountingSubjectUpdateReq req, DCP_AccountingSubjectUpdateRes res) throws Exception
    {
        String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        String subjectId = req.getRequest().getSubjectId();
        String subjectName = req.getRequest().getSubjectName();
        String direction = req.getRequest().getDirection();

        UptBean ub1 = null;
        ub1 = new UptBean("DCP_ACCOUNTINGSUBJECT");
        ub1.addUpdateValue("SUBJECTNAME", new DataValue(subjectName, Types.VARCHAR));
        ub1.addUpdateValue("DIRECTION", new DataValue(direction, Types.VARCHAR));
        ub1.addUpdateValue("AUXILIARYTYPE", new DataValue(req.getRequest().getAuxiliaryType(), Types.VARCHAR));
        ub1.addUpdateValue("MEMO", new DataValue(req.getRequest().getMemo(), Types.VARCHAR));
        ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
        //condition
        ub1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        ub1.addCondition("SUBJECTID", new DataValue(subjectId, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(ub1));

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceDescription("服务执行成功");
        res.setServiceStatus("000");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_AccountingSubjectUpdateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_AccountingSubjectUpdateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_AccountingSubjectUpdateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_AccountingSubjectUpdateReq req) throws Exception
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
    protected TypeToken<DCP_AccountingSubjectUpdateReq> getRequestType()
    {
        return new TypeToken<DCP_AccountingSubjectUpdateReq>(){};
    }

    @Override
    protected DCP_AccountingSubjectUpdateRes getResponseType()
    {
        return new DCP_AccountingSubjectUpdateRes();
    }
}
