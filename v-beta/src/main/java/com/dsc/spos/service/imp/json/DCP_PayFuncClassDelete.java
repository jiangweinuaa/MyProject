package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PayFuncClassDeleteReq;
import com.dsc.spos.json.cust.res.DCP_PayFuncClassDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_PayFuncClassDelete extends SPosAdvanceService<DCP_PayFuncClassDeleteReq,DCP_PayFuncClassDeleteRes>
{

	@Override
	protected void processDUID(DCP_PayFuncClassDeleteReq req, DCP_PayFuncClassDeleteRes res) throws Exception 
	{
		
		String sql = this.getFuncNO_SQL(req);			

		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		if (getQData != null && getQData.isEmpty() == false) 
		{		
			//DCP_PAYFUNCCLASS
			DelBean db1 = new DelBean("DCP_PAYFUNCCLASS");
			db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
			db1.addCondition("CLASSNO", new DataValue(req.getClassNO(), Types.VARCHAR));
			
			this.addProcessData(new DataProcessBean(db1)); // 
						

			//DCP_PAYFUNCNOINFO
			db1 = new DelBean("DCP_PAYFUNCNOINFO");
			db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
			db1.addCondition("CLASSNO", new DataValue(req.getClassNO(), Types.VARCHAR));
			
			this.addProcessData(new DataProcessBean(db1)); // 
			
			sql = this.getFunc_shop_SQL(req);	
			getQData = this.doQueryData(sql, null);
			if (getQData != null && getQData.isEmpty() == false) 
			{
				for (Map<String, Object> oneData : getQData) 
				{
					//DCP_PAYFUNCNOINFO_SHOP
					db1 = new DelBean("DCP_PAYFUNCNOINFO_SHOP");
					db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
					db1.addCondition("FUNCNO", new DataValue(oneData.get("FUNCNO"), Types.INTEGER));		
					this.addProcessData(new DataProcessBean(db1)); // 
				}
				
			}
			
			
			
			this.doExecuteDataToDB();		
			
			getQData=null;
			
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");	
		}
		else
		{
			
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("找不到此POS支付类别信息！");		
			
		}
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PayFuncClassDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PayFuncClassDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PayFuncClassDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PayFuncClassDeleteReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		String ClassNO =req.getClassNO();

		if (Check.Null(ClassNO)) 
		{
			errMsg.append("类别编码不可为空值, ");
			isFail = true;
		} 

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_PayFuncClassDeleteReq> getRequestType() 
	{
		return new TypeToken<DCP_PayFuncClassDeleteReq>(){};
	}

	@Override
	protected DCP_PayFuncClassDeleteRes getResponseType() 
	{
		return new DCP_PayFuncClassDeleteRes();
	}

	
	protected String getFuncNO_SQL(DCP_PayFuncClassDeleteReq req)
	{		
		String sql = null;

		StringBuffer sqlbuf = new StringBuffer("");

		sqlbuf.append("SELECT * FROM DCP_PAYFUNCCLASS WHERE EID='"+req.geteId()+"' AND  CLASSNO='"+req.getClassNO()+"' ");

		sql = sqlbuf.toString();

		return sql;	
	}
	
	/**
	 * 款别明细适用门店
	 * @param req
	 * @return
	 */
	protected String getFunc_shop_SQL(DCP_PayFuncClassDeleteReq req)
	{		
		String sql = null;

		StringBuffer sqlbuf = new StringBuffer("");

		sqlbuf.append("SELECT * FROM DCP_PAYFUNCNOINFO WHERE EID='"+req.geteId()+"' AND  CLASSNO='"+req.getClassNO()+"' ");

		sql = sqlbuf.toString();

		return sql;	
	}
	
	
	
	
	
}
