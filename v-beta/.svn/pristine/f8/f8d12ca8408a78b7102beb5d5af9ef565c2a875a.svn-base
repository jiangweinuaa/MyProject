package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_TareEnableReq;
import com.dsc.spos.json.cust.res.DCP_TareEnableRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.sql.Types;
import java.util.List;

public class DCP_TareEnable extends SPosAdvanceService<DCP_TareEnableReq, DCP_TareEnableRes>
{


    @Override
    protected void processDUID(DCP_TareEnableReq req, DCP_TareEnableRes res) throws Exception
    {
        String eId = req.geteId();
        String[] tareList = req.getRequest().getTareList();
        //修改状态，1启用 2禁用
        String oprType = req.getRequest().getOprType();


        for (int i = 0; i < tareList.length; i++)
        {
            //修改
            UptBean ub1 = new UptBean("DCP_TARESET");
            if (oprType.equals("1"))
            {
                ub1.addUpdateValue("STATUS", new DataValue("100", Types.VARCHAR));
            }
            else
            {
                ub1.addUpdateValue("STATUS", new DataValue("0", Types.VARCHAR));
            }

            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("TAREID", new DataValue(tareList[i], Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));
        }

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_TareEnableReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_TareEnableReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_TareEnableReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_TareEnableReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        if(req.getRequest()==null)
        {
            errMsg.append("requset不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        String[] tareList = req.getRequest().getTareList();
        //修改状态，1启用 2禁用
        String oprType = req.getRequest().getOprType();

        if (tareList==null && tareList.length==0)
        {
            errMsg.append("皮重商品编码不可为空值, ");
            isFail = true;
        }

        if (Check.Null(oprType))
        {
            errMsg.append("启用/禁用传参不可为空值, ");
            isFail = true;
        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_TareEnableReq> getRequestType()
    {
        return new TypeToken<DCP_TareEnableReq>(){};
    }

    @Override
    protected DCP_TareEnableRes getResponseType()
    {
        return new DCP_TareEnableRes();
    }

}
