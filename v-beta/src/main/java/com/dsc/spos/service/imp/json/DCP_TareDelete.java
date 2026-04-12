package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_TareDeleteReq;

import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import com.dsc.spos.json.cust.res.DCP_TareDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;

public class DCP_TareDelete extends SPosAdvanceService<DCP_TareDeleteReq, DCP_TareDeleteRes>
{

    @Override
    protected void processDUID(DCP_TareDeleteReq req, DCP_TareDeleteRes res) throws Exception
    {
        String eId = req.geteId();
        String[] tareList = req.getRequest().getTareList();

        for (int i = 0; i < tareList.length; i++)
        {
            //删除
            DelBean db1 = new DelBean("DCP_TARESET");
            db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db1.addCondition("TAREID", new DataValue(tareList[i], Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1));
        }

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_TareDeleteReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_TareDeleteReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_TareDeleteReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_TareDeleteReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        if(req.getRequest()==null)
        {
            errMsg.append("requset不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        String[] tareList = req.getRequest().getTareList();


        if (tareList==null && tareList.length==0)
        {
            errMsg.append("皮重商品编码不可为空值, ");
            isFail = true;
        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_TareDeleteReq> getRequestType()
    {
        return new TypeToken<DCP_TareDeleteReq>(){};
    }

    @Override
    protected DCP_TareDeleteRes getResponseType()
    {
        return new DCP_TareDeleteRes();
    }
}
