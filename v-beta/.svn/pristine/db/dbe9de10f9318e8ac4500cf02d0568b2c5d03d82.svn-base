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
import com.dsc.spos.json.cust.req.DCP_PGoodsGroupUpdateReq;
import com.dsc.spos.json.cust.res.DCP_PGoodsGroupUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_PGoodsGroupUpdate extends SPosAdvanceService<DCP_PGoodsGroupUpdateReq,DCP_PGoodsGroupUpdateRes>
{

	@Override
	protected void processDUID(DCP_PGoodsGroupUpdateReq req, DCP_PGoodsGroupUpdateRes res) throws Exception 
	{
		Calendar cal = Calendar.getInstance();//获得当前时间
		SimpleDateFormat df=new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String mySysTime = df.format(cal.getTime());
		//String priority = req.getPriority();
		int priority_i = 0;
		try
		{
			priority_i = Integer.parseInt(req.getPriority());		
		} 
		catch (Exception e) 
		{
		// TODO: handle exception
			priority_i = 0;
		}
		

		String sql = null;
		sql = this.getPGoodsGroup_SQL(req);

		List<Map<String, Object>> getQData_check = this.doQueryData(sql,null);		

		if(getQData_check==null || getQData_check.isEmpty())
		{			
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("找不到此类别信息！");			
		}
		else
		{
			//更新单头
			UptBean ub1 = new UptBean("DCP_PGOODSCLASS");		
			ub1.addCondition("EID",new DataValue(req.geteId(), Types.VARCHAR));
			ub1.addCondition("PLUNO",new DataValue(req.getPluNO(), Types.VARCHAR));
			ub1.addCondition("PCLASSNO",new DataValue(req.getPclassNO(), Types.VARCHAR));

			ub1.addUpdateValue("INVOWAY",new DataValue(req.getInvoWay(), Types.INTEGER));
			ub1.addUpdateValue("CONDCOUNT",new DataValue(req.getCondCount(), Types.VARCHAR));
			ub1.addUpdateValue("UPDATE_TIME",new DataValue(mySysTime, Types.VARCHAR));
			ub1.addUpdateValue("STATUS",new DataValue("100", Types.VARCHAR));
			ub1.addUpdateValue("PRIORITY",new DataValue(priority_i, Types.INTEGER));
			
			this.addProcessData(new DataProcessBean(ub1));		


			this.doExecuteDataToDB();

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");			
		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PGoodsGroupUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PGoodsGroupUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PGoodsGroupUpdateReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PGoodsGroupUpdateReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		String PclassNO =req.getPclassNO();
		String PluNO =req.getPluNO();	
		String InvoWay =req.getInvoWay();	
		String CondCount =req.getCondCount();	
		

		if (Check.Null(PluNO)) 
		{
			errMsg.append("套餐商品编码不可为空值, ");
			isFail = true;
		}

		if (Check.Null(PclassNO)) 
		{
			errMsg.append("套餐类别编码不可为空值, ");
			isFail = true;
		} 
		
		if (Check.Null(InvoWay)) 
		{
			errMsg.append("条件不可为空值, ");
			isFail = true;
		} 
		
		if (PosPub.isNumeric(InvoWay)==false) 
		{
			errMsg.append("条件必须为数字, ");
			isFail = true;
		} 
		
		if (Check.Null(CondCount)) 
		{
			errMsg.append("数量不可为空值, ");
			isFail = true;
		} 

		if (PosPub.isNumeric(CondCount)==false) 
		{
			errMsg.append("数量必须为数字, ");
			isFail = true;
		} 
		

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_PGoodsGroupUpdateReq> getRequestType() 
	{
		return new TypeToken<DCP_PGoodsGroupUpdateReq>(){};
	}

	@Override
	protected DCP_PGoodsGroupUpdateRes getResponseType() 
	{
		return new DCP_PGoodsGroupUpdateRes();
	}


	protected String getPGoodsGroup_SQL(DCP_PGoodsGroupUpdateReq req)
	{		
		String sql = null;

		StringBuffer sqlbuf = new StringBuffer("");

		sqlbuf.append("SELECT * FROM DCP_PGOODSCLASS WHERE EID='"+req.geteId()+"' AND PLUNO='"+req.getPluNO()+"' AND PCLASSNO='"+req.getPclassNO()+"' ");

		sql = sqlbuf.toString();

		return sql;	
	}



}
