package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_JobSetDeleteReq;
import com.dsc.spos.json.cust.res.DCP_JobSetDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_JobSetDelete extends SPosAdvanceService<DCP_JobSetDeleteReq,DCP_JobSetDeleteRes>
{

	@Override
	protected void processDUID(DCP_JobSetDeleteReq req, DCP_JobSetDeleteRes res) throws Exception 
	{		
		String sql = this.getClassNO_SQL(req);			

		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		if (getQData != null && getQData.isEmpty() == false) 
		{
			//JOB_QUARTZ
			DelBean db1 = new DelBean("JOB_QUARTZ");
			db1.addCondition("JOB_NAME", new DataValue(req.getRequest().getJobName(), Types.VARCHAR));
						
			this.addProcessData(new DataProcessBean(db1)); // 
			this.doExecuteDataToDB();		
			
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");	
		}
		else
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("找不到此JOB名称信息！");			
		}
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_JobSetDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_JobSetDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_JobSetDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_JobSetDeleteReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		String JobName =req.getRequest().getJobName();

		if (Check.Null(JobName)) 
		{
			errMsg.append("JOB名称不可为空值, ");
			isFail = true;
		} 

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_JobSetDeleteReq> getRequestType() 
	{
		return new TypeToken<DCP_JobSetDeleteReq>(){};
	}

	@Override
	protected DCP_JobSetDeleteRes getResponseType() 
	{
		return new DCP_JobSetDeleteRes();
	}

	
	protected String getClassNO_SQL(DCP_JobSetDeleteReq req)
	{		
		String sql = null;

		StringBuffer sqlbuf = new StringBuffer("");

		sqlbuf.append("SELECT * FROM JOB_QUARTZ WHERE JOB_NAME='"+req.getRequest().getJobName()+"' ");

		sql = sqlbuf.toString();

		return sql;	
	}
	
	
	
}
