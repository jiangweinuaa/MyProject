package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_DinnerDeleteReq;
import com.dsc.spos.json.cust.req.DCP_GoodsSetShopDeleteReq;
import com.dsc.spos.json.cust.res.DCP_DinnerDeleteRes;
import com.dsc.spos.json.cust.res.DCP_GoodsSetShopDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsSetShopDelete extends SPosAdvanceService<DCP_GoodsSetShopDeleteReq,DCP_GoodsSetShopDeleteRes>
{

	@Override
	protected void processDUID(DCP_GoodsSetShopDeleteReq req, DCP_GoodsSetShopDeleteRes res) throws Exception 
	{
		//DCP_GOODS_SHOP
		DelBean db1 = new DelBean("DCP_GOODS_SHOP");
		db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
		db1.addCondition("ORGANIZATIONNO", new DataValue(req.getSELSHOP(), Types.VARCHAR));
		db1.addCondition("PLUNO", new DataValue(req.getPLUNO(), Types.VARCHAR));

		this.addProcessData(new DataProcessBean(db1)); // 


		this.doExecuteDataToDB();		

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");	
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_GoodsSetShopDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_GoodsSetShopDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_GoodsSetShopDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_GoodsSetShopDeleteReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		String SELSHOP =req.getSELSHOP();
		String PLUNO =req.getPLUNO();

		if (Check.Null(SELSHOP)) 
		{
			errMsg.append("门店编码不可为空值, ");
			isFail = true;
		} 
		
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
	protected TypeToken<DCP_GoodsSetShopDeleteReq> getRequestType() 
	{
		return new TypeToken<DCP_GoodsSetShopDeleteReq>(){};
	}

	@Override
	protected DCP_GoodsSetShopDeleteRes getResponseType() 
	{
		return new DCP_GoodsSetShopDeleteRes();
	}
	
	

}
