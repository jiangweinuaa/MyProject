package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PGoodsGroupDeleteReq;
import com.dsc.spos.json.cust.res.DCP_PGoodsGroupDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_PGoodsGroupDelete extends SPosAdvanceService<DCP_PGoodsGroupDeleteReq,DCP_PGoodsGroupDeleteRes>
{

	@Override
	protected void processDUID(DCP_PGoodsGroupDeleteReq req, DCP_PGoodsGroupDeleteRes res) throws Exception 
	{
		String sql = this.getPGoodsGroup_SQL(req);			

		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		if (getQData != null && getQData.isEmpty() == false) 
		{		

			//DCP_PGOODSCLASS
			DelBean db1 = new DelBean("DCP_PGOODSCLASS");
			db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
			db1.addCondition("PLUNO", new DataValue(req.getPluNO(), Types.VARCHAR));
			db1.addCondition("PCLASSNO", new DataValue(req.getPclassNO(), Types.VARCHAR));

			this.addProcessData(new DataProcessBean(db1)); // 

			//DCP_PGOODSCLASS_DETAIL
			db1 = new DelBean("DCP_PGOODSCLASS_DETAIL");
			db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
			db1.addCondition("PLUNO", new DataValue(req.getPluNO(), Types.VARCHAR));
			db1.addCondition("PCLASSNO", new DataValue(req.getPclassNO(), Types.VARCHAR));

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
			res.setServiceDescription("找不到此类别信息！");		

		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PGoodsGroupDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PGoodsGroupDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PGoodsGroupDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PGoodsGroupDeleteReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		String PclassNO =req.getPclassNO();
		String PluNO =req.getPluNO();	

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

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_PGoodsGroupDeleteReq> getRequestType() 
	{
		return new TypeToken<DCP_PGoodsGroupDeleteReq>(){};
	}

	@Override
	protected DCP_PGoodsGroupDeleteRes getResponseType() 
	{
		return new DCP_PGoodsGroupDeleteRes();
	}

	protected String getPGoodsGroup_SQL(DCP_PGoodsGroupDeleteReq req)
	{		
		String sql = null;

		StringBuffer sqlbuf = new StringBuffer("");

		sqlbuf.append("SELECT * FROM DCP_PGOODSCLASS WHERE EID='"+req.geteId()+"' AND PLUNO='"+req.getPluNO()+"' AND PCLASSNO='"+req.getPclassNO()+"' ");

		sql = sqlbuf.toString();

		return sql;	
	}

}
