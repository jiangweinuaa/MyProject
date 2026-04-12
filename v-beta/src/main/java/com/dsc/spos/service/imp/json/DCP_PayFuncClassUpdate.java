package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PayFuncClassUpdateReq;
import com.dsc.spos.json.cust.res.DCP_PayFuncClassUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_PayFuncClassUpdate extends SPosAdvanceService<DCP_PayFuncClassUpdateReq,DCP_PayFuncClassUpdateRes>
{

	@Override
	protected void processDUID(DCP_PayFuncClassUpdateReq req, DCP_PayFuncClassUpdateRes res) throws Exception 
	{
		Calendar cal = Calendar.getInstance();//获得当前时间
		SimpleDateFormat df=new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String mySysTime = df.format(cal.getTime());

		String sql = null;
		sql = this.getFuncNO_SQL(req);

		List<Map<String, Object>> getQData_check = this.doQueryData(sql,null);		

		if(getQData_check==null || getQData_check.isEmpty())
		{			
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("找不到此支付功能编码信息！");			
		}
		else
		{
			//更新单头
			UptBean ub1 = new UptBean("DCP_PAYFUNCCLASS");			
			ub1.addCondition("EID",new DataValue(req.geteId(), Types.VARCHAR));
			ub1.addCondition("CLASSNO",new DataValue(req.getClassNO(), Types.INTEGER));
			
			ub1.addUpdateValue("CLASSNAME",new DataValue(req.getClassName(), Types.VARCHAR)); 
			ub1.addUpdateValue("PRIORITY",new DataValue(req.getPriority(), Types.INTEGER));
			ub1.addUpdateValue("STATUS",new DataValue(req.getStatus(), Types.VARCHAR));
			ub1.addUpdateValue("TRAN_TIME",new DataValue(mySysTime, Types.VARCHAR));
			
			this.addProcessData(new DataProcessBean(ub1));		
			
			
			this.doExecuteDataToDB();

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");			
		}
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PayFuncClassUpdateReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PayFuncClassUpdateReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PayFuncClassUpdateReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PayFuncClassUpdateReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		String ClassNO =req.getClassNO();
		String ClassName =req.getClassName();
		String Priority =req.getPriority();
		String status =req.getStatus();
		

		if (Check.Null(ClassNO)) 
		{
			errMsg.append("类别编码不可为空值, ");
			isFail = true;
		} 
		

		if (Check.Null(ClassName)) 
		{
			errMsg.append("类别名称不可为空值, ");
			isFail = true;
		} 
		
		if (PosPub.isNumeric(Priority)==false) 
		{
			errMsg.append("优先级必须为数字类型, ");
			isFail = true;
		} 

		if (Check.Null(status)) 
		{
			errMsg.append("状态不可为空值, ");
			isFail = true;
		}
		

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_PayFuncClassUpdateReq> getRequestType() 
	{
		return new TypeToken<DCP_PayFuncClassUpdateReq>(){};
	}

	@Override
	protected DCP_PayFuncClassUpdateRes getResponseType()
	{
		return new DCP_PayFuncClassUpdateRes();
	}
	
	protected String getFuncNO_SQL(DCP_PayFuncClassUpdateReq req)
	{		
		String sql = null;

		StringBuffer sqlbuf = new StringBuffer("");

		sqlbuf.append("SELECT * FROM DCP_PAYFUNCCLASS WHERE EID='"+req.geteId()+"' and CLASSNO='"+req.getClassNO()+"' ");

		sql = sqlbuf.toString();

		return sql;	
	}

	
}
