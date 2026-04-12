package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_GoodsSetUpdateCNFFLGReq;
import com.dsc.spos.json.cust.res.DCP_GoodsSetUpdateCNFFLGRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsSetUpdateCNFFLG extends SPosAdvanceService<DCP_GoodsSetUpdateCNFFLGReq,DCP_GoodsSetUpdateCNFFLGRes>
{

	@Override
	protected void processDUID(DCP_GoodsSetUpdateCNFFLGReq req, DCP_GoodsSetUpdateCNFFLGRes res) throws Exception 
	{
		Calendar cal = Calendar.getInstance();//获得当前时间
		SimpleDateFormat df=new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String mySysTime = df.format(cal.getTime());

		//
		res.setSuccess(false);
		res.setServiceStatus("100");
		res.setServiceDescription("服务执行失败！");			
		
		//更新单头
		UptBean ub1 = new UptBean("DCP_GOODS");			
		ub1.addCondition("EID",new DataValue(req.geteId(), Types.VARCHAR));
		ub1.addCondition("PLUNO", new DataValue(req.getPLUNO(), Types.VARCHAR));

		ub1.addUpdateValue("STATUS",new DataValue(req.getStatus(), Types.VARCHAR)); 
		ub1.addUpdateValue("UPDATE_TIME",new DataValue(mySysTime, Types.VARCHAR)); 

		this.addProcessData(new DataProcessBean(ub1));			


		this.doExecuteDataToDB();

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");
		
		
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_GoodsSetUpdateCNFFLGReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_GoodsSetUpdateCNFFLGReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_GoodsSetUpdateCNFFLGReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_GoodsSetUpdateCNFFLGReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		String pluno =req.getPLUNO();
		String status =req.getStatus();		
		
		if (Check.Null(pluno)) 
		{
			errMsg.append("商品编码不可为空值, ");
			isFail = true;
		} 
		
		if (Check.Null(status)) 
		{
			errMsg.append("商品状态不可为空值, ");
			isFail = true;
		} 
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_GoodsSetUpdateCNFFLGReq> getRequestType() 
	{
		return new TypeToken<DCP_GoodsSetUpdateCNFFLGReq>(){};
	}

	@Override
	protected DCP_GoodsSetUpdateCNFFLGRes getResponseType()
	{
		return new DCP_GoodsSetUpdateCNFFLGRes();
	}
	
	

}
