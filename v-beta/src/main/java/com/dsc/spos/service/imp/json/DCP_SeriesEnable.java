package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_SeriesEnableReq;
import com.dsc.spos.json.cust.req.DCP_SeriesEnableReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_SeriesEnableRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_SeriesEnable extends SPosAdvanceService<DCP_SeriesEnableReq,DCP_SeriesEnableRes> {

	@Override
	protected void processDUID(DCP_SeriesEnableReq req, DCP_SeriesEnableRes res) throws Exception {
	// TODO Auto-generated method stub
		try 
		{
			String status = "100";//状态：-1未启用100已启用 0已禁用
			
			if(req.getRequest().getOprType().equals("1"))//操作类型：1-启用2-禁用
			{
				status = "100";
			}
			else
			{
				status = "0";
			}
			
			for (level1Elm par : req.getRequest().getSeriesNoList()) 
			{
				String pluNo = par.getSeriesNo();
				UptBean up1 = new UptBean("DCP_SERIES");
				up1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
				up1.addCondition("SERIESNO", new DataValue(pluNo, Types.VARCHAR));
				if(status.equals("0"))//只能禁用，已启用的。
				{
					up1.addCondition("STATUS", new DataValue("100", Types.VARCHAR));
				}
				
				up1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(up1));
			}
			
			this.doExecuteDataToDB();		
			
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");	
		
	
		} 
		catch (Exception e) 
		{
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行异常："+e.getMessage());

		}
	
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_SeriesEnableReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_SeriesEnableReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_SeriesEnableReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_SeriesEnableReq req) throws Exception {
	// TODO Auto-generated method stub
	boolean isFail = false;
	StringBuffer errMsg = new StringBuffer("");
	
	if(req.getRequest()==null)
  {
  	errMsg.append("requset不能为空值 ");
  	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
  }

	if(Check.Null(req.getRequest().getOprType()))
	{
		errMsg.append("操作类型不可为空值, ");
		isFail = true;
		throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
	}
			
	
	List<level1Elm> stausList = req.getRequest().getSeriesNoList();
	
	if (stausList==null || stausList.isEmpty()) 
	{
		errMsg.append("品牌编码不可为空, ");
		isFail = true;
		throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
	} 
	for(level1Elm par : stausList)
	{
		if(Check.Null(par.getSeriesNo()))
		{
			errMsg.append("品牌编码不可为空值, ");
			isFail = true;
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
	}


	
	
	if (isFail)
	{
		throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
	}

	return isFail;
	}

	@Override
	protected TypeToken<DCP_SeriesEnableReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_SeriesEnableReq>(){};
	}

	@Override
	protected DCP_SeriesEnableRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_SeriesEnableRes();
	}

}
