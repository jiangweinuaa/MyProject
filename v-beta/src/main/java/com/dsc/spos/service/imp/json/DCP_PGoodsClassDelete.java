package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PGoodsClassDeleteReq;
import com.dsc.spos.json.cust.res.DCP_PGoodsClassDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_PGoodsClassDelete extends SPosAdvanceService<DCP_PGoodsClassDeleteReq,DCP_PGoodsClassDeleteRes>
{

	@Override
	protected void processDUID(DCP_PGoodsClassDeleteReq req, DCP_PGoodsClassDeleteRes res) throws Exception 
	{
		String sql = this.getPGoodsClassNO_SQL(req);			

		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		if (getQData != null && getQData.isEmpty() == false) 
		{		
			//DCP_PACKAGECLASS
			DelBean db1 = new DelBean("DCP_PACKAGECLASS");
			db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
			db1.addCondition("PCLASSNO", new DataValue(req.getRequest().getPclassNo(), Types.VARCHAR)); 
			
			this.addProcessData(new DataProcessBean(db1)); // 
						

			//DCP_PGOODSCLASS
			db1 = new DelBean("DCP_PGOODSCLASS");
			db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
			db1.addCondition("PCLASSNO", new DataValue(req.getRequest().getPclassNo(), Types.VARCHAR));
			
			this.addProcessData(new DataProcessBean(db1)); // 
			
			//DCP_PGOODSCLASS_DETAIL
			db1 = new DelBean("DCP_PGOODSCLASS_DETAIL");
			db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
			db1.addCondition("PCLASSNO", new DataValue(req.getRequest().getPclassNo(), Types.VARCHAR));
			
			this.addProcessData(new DataProcessBean(db1)); // 
									
			
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
			res.setServiceDescription("找不到此套餐类别编码信息！");		
			
		}
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PGoodsClassDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PGoodsClassDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PGoodsClassDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PGoodsClassDeleteReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		String PclassNO =req.getRequest().getPclassNo();

		if (Check.Null(PclassNO)) 
		{
			errMsg.append("套餐类别编码不可为空值, ");
			isFail = true;
		} 

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_PGoodsClassDeleteReq> getRequestType() 
	{
		return new TypeToken<DCP_PGoodsClassDeleteReq>(){};
	}

	@Override
	protected DCP_PGoodsClassDeleteRes getResponseType() 
	{
		return new DCP_PGoodsClassDeleteRes();
	}

	protected String getPGoodsClassNO_SQL(DCP_PGoodsClassDeleteReq req)
	{		
		String sql = null;

		StringBuffer sqlbuf = new StringBuffer("");

		sqlbuf.append("SELECT * FROM DCP_PACKAGECLASS WHERE EID='"+req.geteId()+"' AND PCLASSNO='"+req.getRequest().getPclassNo()+"' ");

		sql = sqlbuf.toString();

		return sql;	
	}
	
}
