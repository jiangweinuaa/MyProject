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
import com.dsc.spos.json.cust.req.DCP_PGoodsClassUpdateReq;
import com.dsc.spos.json.cust.res.DCP_PGoodsClassUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_PGoodsClassUpdate  extends SPosAdvanceService<DCP_PGoodsClassUpdateReq,DCP_PGoodsClassUpdateRes>
{

	@Override
	protected void processDUID(DCP_PGoodsClassUpdateReq req, DCP_PGoodsClassUpdateRes res) throws Exception 
	{
		
		Calendar cal = Calendar.getInstance();//获得当前时间
		SimpleDateFormat df=new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String mySysTime = df.format(cal.getTime());
		
		String sql = null;
		sql = this.getPGoodsClassNO_SQL(req);

		List<Map<String, Object>> getQData_check = this.doQueryData(sql,null);		

		if(getQData_check==null || getQData_check.isEmpty())
		{			
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("找不到此套餐类别编码信息！");			
		}
		else
		{
			//更新单头
			UptBean ub1 = new UptBean("DCP_PACKAGECLASS");		
			ub1.addCondition("EID",new DataValue(req.geteId(), Types.VARCHAR));
			ub1.addCondition("PCLASSNO",new DataValue(req.getRequest().getPclassNo(), Types.VARCHAR));
			
			ub1.addUpdateValue("PCLASSNAME",new DataValue(req.getRequest().getPclassName(), Types.VARCHAR));
			ub1.addUpdateValue("UPDATE_TIME",new DataValue(mySysTime, Types.VARCHAR));
			ub1.addUpdateValue("STATUS",new DataValue(req.getRequest().getStatus(), Types.VARCHAR));
			
			this.addProcessData(new DataProcessBean(ub1));		
					
			
			this.doExecuteDataToDB();

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");			
		}
		
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PGoodsClassUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PGoodsClassUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PGoodsClassUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PGoodsClassUpdateReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		String PclassNO =req.getRequest().getPclassNo();
		String PclassName =req.getRequest().getPclassName();
		String status =req.getRequest().getStatus();

		if (Check.Null(PclassNO)) 
		{
			errMsg.append("套餐类别编码不可为空值, ");
			isFail = true;
		} 
		

		if (Check.Null(PclassName)) 
		{
			errMsg.append("套餐类别名称不可为空值, ");
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
	protected TypeToken<DCP_PGoodsClassUpdateReq> getRequestType() 
	{
		return new TypeToken<DCP_PGoodsClassUpdateReq>(){};
	}

	@Override
	protected DCP_PGoodsClassUpdateRes getResponseType() 
	{
		return new DCP_PGoodsClassUpdateRes();
	}
	
	
	protected String getPGoodsClassNO_SQL(DCP_PGoodsClassUpdateReq req)
	{		
		String sql = null;

		StringBuffer sqlbuf = new StringBuffer("");

		sqlbuf.append("SELECT * FROM DCP_PACKAGECLASS WHERE EID='"+req.geteId()+"' AND PCLASSNO='"+req.getRequest().getPclassNo()+"' ");

		sql = sqlbuf.toString();

		return sql;	
	}
	
	

}
