package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_CardRwRuleEnableReq;
import com.dsc.spos.json.cust.res.DCP_CardRwRuleEnableRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.sql.Types;
import java.util.List;

public class DCP_CardRwRuleEnable extends SPosAdvanceService<DCP_CardRwRuleEnableReq, DCP_CardRwRuleEnableRes>
{

    @Override
    protected void processDUID(DCP_CardRwRuleEnableReq req, DCP_CardRwRuleEnableRes res) throws Exception
    {
        String[] ruleIdList=req.getRequest().getRuleIdList();
        String oprType=req.getRequest().getOprType();

        for (String ruleId : ruleIdList)
        {
            int  status=oprType.equals("1")?100:0;
            //修改
            UptBean ub1 = new UptBean("DCP_CARDRWRULE");
            ub1.addUpdateValue("STATUS", new DataValue(status, Types.INTEGER));
            //condition
            ub1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
            ub1.addCondition("RULEID", new DataValue(ruleId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));
        }

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_CardRwRuleEnableReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CardRwRuleEnableReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CardRwRuleEnableReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_CardRwRuleEnableReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if(req.getRequest()==null)
        {
            errMsg.append("requset不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        String[] ruleIdList=req.getRequest().getRuleIdList();
        String oprType=req.getRequest().getOprType();

        if(ruleIdList==null)
        {
            errMsg.append("请求参数ruleIdList不能为空值 ");
            isFail = true;
        }

        if(oprType==null)
        {
            errMsg.append("请求参数oprType不能为空值 ");
            isFail = true;
        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_CardRwRuleEnableReq> getRequestType()
    {
        return new TypeToken<DCP_CardRwRuleEnableReq>(){};
    }

    @Override
    protected DCP_CardRwRuleEnableRes getResponseType()
    {
        return new DCP_CardRwRuleEnableRes();
    }
}
