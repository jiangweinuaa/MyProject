package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ConcModDetailDeleteReq;
import com.dsc.spos.json.cust.res.DCP_ConcModDetailDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;

public class DCP_ConcModDetailDelete extends SPosAdvanceService<DCP_ConcModDetailDeleteReq,DCP_ConcModDetailDeleteRes>
{

	@Override
	protected void processDUID(DCP_ConcModDetailDeleteReq req, DCP_ConcModDetailDeleteRes res) throws Exception
	{
        for(DCP_ConcModDetailDeleteReq.levelElm par:req.getRequest())
        {
            UptBean ub1 = new UptBean("PLATFORM_CREGISTERDETAIL");
           // ub1.addCondition("PRODUCTTYPE",new DataValue( par.getRFuncNo(), Types.VARCHAR));
            ub1.addCondition("TOKEN", new DataValue(par.getToken(), Types.VARCHAR));
            ub1.addUpdateValue("TOKEN",new DataValue("", Types.VARCHAR));
            ub1.addUpdateValue("LASTMODITIME",new DataValue("", Types.VARCHAR));
            ub1.addUpdateValue("EID",new DataValue("", Types.VARCHAR));
            ub1.addUpdateValue("OPNO",new DataValue("", Types.VARCHAR));
            ub1.addUpdateValue("OPNAME",new DataValue("", Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));
            DelBean db1 = new DelBean("DCP_MODULAR_WORKING_AUTH");
            db1.addCondition("TOKEN", new DataValue(par.getToken(), Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1));
            DelBean db2 = new DelBean("PLATFORM_TOKEN");
            db2.addCondition("KEY", new DataValue(par.getToken(), Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db2));
        }
        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

	}
    @Override
    protected boolean AuthCheck(DCP_ConcModDetailDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return true;
    }
	@Override
	protected List<InsBean> prepareInsertData(DCP_ConcModDetailDeleteReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_ConcModDetailDeleteReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_ConcModDetailDeleteReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_ConcModDetailDeleteReq req) throws Exception
	{
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        if(req.getRequest()==null || req.getRequest().size()==0)
        {
            isFail = true;
            errMsg.append("request不能为空 ");
        }else
        {
            for (int i = 0; i < req.getRequest().size(); i++)
            {
                if(req.getRequest().get(i)==null)
                {
                    isFail = true;
                    errMsg.append("第"+String.valueOf(i)+"条数据不可为空, " );
                }else
                {
                    if(Check.isEmpty(req.getRequest().get(i).getRFuncNo())) {
                        isFail = true;
                        errMsg.append("第"+String.valueOf(i)+"条[rFuncNo]不可为空, " );
                    }
                    if(Check.isEmpty(req.getRequest().get(i).getToken())) {
                        isFail = true;
                        errMsg.append("第"+String.valueOf(i)+"条[token]不可为空, " );
                    }
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
	protected TypeToken<DCP_ConcModDetailDeleteReq> getRequestType()
	{
		return new TypeToken<DCP_ConcModDetailDeleteReq>(){};
	}

	@Override
	protected DCP_ConcModDetailDeleteRes getResponseType()
	{
		return new DCP_ConcModDetailDeleteRes();
	}



}
