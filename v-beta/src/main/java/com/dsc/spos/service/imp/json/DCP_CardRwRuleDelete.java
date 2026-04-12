package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_CardRwRuleDeleteReq;
import com.dsc.spos.json.cust.res.DCP_CardRwRuleDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.sql.Types;
import java.util.List;

public class DCP_CardRwRuleDelete extends SPosAdvanceService<DCP_CardRwRuleDeleteReq, DCP_CardRwRuleDeleteRes>
{


    @Override
    protected void processDUID(DCP_CardRwRuleDeleteReq req, DCP_CardRwRuleDeleteRes res) throws Exception
    {

        String[] ruleIdList=req.getRequest().getRuleIdList();

        for (String ruleId : ruleIdList)
        {
            //删除适用门店
            DelBean db1 = new DelBean("DCP_CARDRWRULE_SHOP");
            db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
            db1.addCondition("RULEID", new DataValue(ruleId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1));

            db1 = new DelBean("DCP_CARDRWRULE");
            db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
            db1.addCondition("RULEID", new DataValue(ruleId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1));
        }

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_CardRwRuleDeleteReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CardRwRuleDeleteReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CardRwRuleDeleteReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_CardRwRuleDeleteReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if(req.getRequest()==null)
        {
            errMsg.append("requset不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        String[] ruleIdList=req.getRequest().getRuleIdList();


        if(ruleIdList==null)
        {
            errMsg.append("请求参数ruleIdList不能为空值 ");
            isFail = true;
        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_CardRwRuleDeleteReq> getRequestType()
    {
        return new TypeToken<DCP_CardRwRuleDeleteReq>(){};
    }

    @Override
    protected DCP_CardRwRuleDeleteRes getResponseType()
    {
        return new DCP_CardRwRuleDeleteRes();
    }


}
