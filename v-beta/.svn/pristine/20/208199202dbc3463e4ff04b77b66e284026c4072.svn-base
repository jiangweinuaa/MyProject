package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_GoodsSetGoodpriceDeleteReq;
import com.dsc.spos.json.cust.res.DCP_GoodsSetGoodpriceDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsSetGoodpriceDelete extends SPosAdvanceService<DCP_GoodsSetGoodpriceDeleteReq,DCP_GoodsSetGoodpriceDeleteRes>
{

	@Override
	protected void processDUID(DCP_GoodsSetGoodpriceDeleteReq req, DCP_GoodsSetGoodpriceDeleteRes res) throws Exception 
	{

		//DCP_GOODS_PRICE
		DelBean db1 = new DelBean("DCP_GOODS_PRICE");
		db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
		db1.addCondition("PLUNO", new DataValue(req.getPluNO(), Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db1)); // 


		//DCP_GOODS_PRICE_ORG
		db1 = new DelBean("DCP_GOODS_PRICE_ORG");
		db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
		db1.addCondition("PLUNO", new DataValue(req.getPluNO(), Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db1)); // 
		
		this.doExecuteDataToDB();		

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");	


	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_GoodsSetGoodpriceDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_GoodsSetGoodpriceDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_GoodsSetGoodpriceDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_GoodsSetGoodpriceDeleteReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		String PLUNO =req.getPluNO();

		if (Check.Null(PLUNO)) 
		{
			errMsg.append("商品编码不可为空值, ");
			isFail = true;
		} 


		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;

	}

	@Override
	protected TypeToken<DCP_GoodsSetGoodpriceDeleteReq> getRequestType() 
	{
		return new TypeToken<DCP_GoodsSetGoodpriceDeleteReq>(){};
	}

	@Override
	protected DCP_GoodsSetGoodpriceDeleteRes getResponseType() 
	{
		return new DCP_GoodsSetGoodpriceDeleteRes();
	}


}
