package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PayMappingDeleteReq;
import com.dsc.spos.json.cust.res.DCP_PayMappingDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_PayMappingDelete extends SPosAdvanceService<DCP_PayMappingDeleteReq,DCP_PayMappingDeleteRes>
{

	@Override
	protected void processDUID(DCP_PayMappingDeleteReq req, DCP_PayMappingDeleteRes res) throws Exception 
	{
	    String eId = req.geteId();
        List<DCP_PayMappingDeleteReq.level1Elm> infoList =  req.getRequest().getInfoList();
        for (DCP_PayMappingDeleteReq.level1Elm par : infoList)
        {
            DelBean db1 = new DelBean("DCP_PAYMENTMAPPING");
            db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db1.addCondition("CHANNELTYPE", new DataValue(par.getChannelType(), Types.VARCHAR));
            db1.addCondition("CHANNELID", new DataValue(par.getChannelId(), Types.VARCHAR));
            db1.addCondition("ORDER_PAYCODE", new DataValue(par.getOrder_paycode(), Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1)); //
        }
		this.doExecuteDataToDB();		
		
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");	
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PayMappingDeleteReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PayMappingDeleteReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PayMappingDeleteReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PayMappingDeleteReq req) throws Exception 
	{
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if (req.getRequest() == null)
        {
            errMsg.append("request不能为空值！");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        List<DCP_PayMappingDeleteReq.level1Elm> infoList =  req.getRequest().getInfoList();
        if (infoList==null||infoList.isEmpty())
        {
            errMsg.append("删除列表不能为空值！");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        for (DCP_PayMappingDeleteReq.level1Elm par : infoList)
        {
            if (Check.Null(par.getChannelType()))
            {
                errMsg.append("渠道类型不能为空值，");
                isFail = true;
            }
            if (Check.Null(par.getChannelId()))
            {
                errMsg.append("渠道编码不能为空值，");
                isFail = true;
            }
            if (Check.Null(par.getOrder_paycode()))
            {
                errMsg.append("第三方平台支付编码不能为空值，");
                isFail = true;
            }

        }
        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

	@Override
	protected TypeToken<DCP_PayMappingDeleteReq> getRequestType() 
	{
		return new TypeToken<DCP_PayMappingDeleteReq>(){};
	}

	@Override
	protected DCP_PayMappingDeleteRes getResponseType() 
	{
		return new DCP_PayMappingDeleteRes();
	}
	

	
	

}
