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
import com.dsc.spos.json.cust.req.DCP_PayFuncClassCreateReq;
import com.dsc.spos.json.cust.res.DCP_PayFuncClassCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_PayFuncClassCreate extends SPosAdvanceService<DCP_PayFuncClassCreateReq, DCP_PayFuncClassCreateRes> 
{

	@Override
	protected void processDUID(DCP_PayFuncClassCreateReq req, DCP_PayFuncClassCreateRes res) throws Exception 
	{
		Calendar cal = Calendar.getInstance();//获得当前时间
		SimpleDateFormat df=new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String mySysTime = df.format(cal.getTime());

		String sql = null;
		sql = this.getFuncNO_SQL(req);

		List<Map<String, Object>> getQData_check = this.doQueryData(sql,null);		

		if(getQData_check==null || getQData_check.isEmpty())
		{

			//DCP_PAYFUNCCLASS
			String[] columnsModular = 
				{
						"EID",
						"CLASSNO",
						"CLASSNAME",
						"PRIORITY",
						"STATUS",
						"TRAN_TIME"
				};


			DataValue[] insValue1 = null;

			insValue1 = new DataValue[] 
					{ 
							new DataValue(req.geteId(), Types.VARCHAR),
							new DataValue(req.getClassNO(), Types.INTEGER), 
							new DataValue(req.getClassName(), Types.VARCHAR),
							new DataValue(req.getPriority(), Types.INTEGER),				
							new DataValue(req.getStatus(), Types.VARCHAR),
							new DataValue(mySysTime, Types.VARCHAR)
					};

			InsBean ib1 = new InsBean("DCP_PAYFUNCCLASS", columnsModular);
			ib1.addValues(insValue1);
			this.addProcessData(new DataProcessBean(ib1)); // 新增單頭

			this.doExecuteDataToDB();

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");			
		}
		else
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("功能编码已经存在！");			
		}		
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PayFuncClassCreateReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PayFuncClassCreateReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PayFuncClassCreateReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PayFuncClassCreateReq req) throws Exception 
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
		
		if (PosPub.isNumeric(ClassNO)==false) 
		{
			errMsg.append("类别编码必须为数字类型, ");
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
	protected TypeToken<DCP_PayFuncClassCreateReq> getRequestType() 
	{
		return new TypeToken<DCP_PayFuncClassCreateReq>(){};
	}

	@Override
	protected DCP_PayFuncClassCreateRes getResponseType()
	{
		return new DCP_PayFuncClassCreateRes();
	}
	
	
	protected String getFuncNO_SQL(DCP_PayFuncClassCreateReq req)
	{		
		String sql = null;

		StringBuffer sqlbuf = new StringBuffer("");

		sqlbuf.append("SELECT * FROM DCP_PAYFUNCCLASS WHERE EID='"+req.geteId()+"' and CLASSNO='"+req.getClassNO()+"' ");

		sql = sqlbuf.toString();

		return sql;	
	}

	
	

}
