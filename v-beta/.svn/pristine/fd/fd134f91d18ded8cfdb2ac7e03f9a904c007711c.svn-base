package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_TGArrivalDeleteReq;
import com.dsc.spos.json.cust.res.DCP_TGArrivalDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 服務函數：TGArrivalDeleteDCP
 * 服务说明：预计到团删除
 * @author jinzma 
 * @since  2019-02-14
 */
public class DCP_TGArrivalDelete extends SPosAdvanceService<DCP_TGArrivalDeleteReq,DCP_TGArrivalDeleteRes> {

	@Override
	protected void processDUID(DCP_TGArrivalDeleteReq req, DCP_TGArrivalDeleteRes res) throws Exception {
		// TODO 自动生成的方法存根
		String eId = req.geteId();
		String oShopId = req.getoShopId();
		String orderNO =req.getOrderNO();

		try 
		{
			//DCP_TGARRIVAL
			DelBean db1 = new DelBean("DCP_TGARRIVAL");
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db1.addCondition("SHOPID", new DataValue(oShopId, Types.VARCHAR));
			db1.addCondition("ORDERNO", new DataValue(orderNO, Types.VARCHAR));

			this.addProcessData(new DataProcessBean(db1));
			this.doExecuteDataToDB();	

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		}
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_TGArrivalDeleteReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_TGArrivalDeleteReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_TGArrivalDeleteReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_TGArrivalDeleteReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if (Check.Null(req.getoShopId()) ) 
		{
			errMsg.append("门店编号不可为空值, ");
			isFail = true;
		}
		if (Check.Null(req.getOrderNO()) ) 
		{
			errMsg.append("预约编号不可为空值, ");
			isFail = true;
		}
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;	
	}

	@Override
	protected TypeToken<DCP_TGArrivalDeleteReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_TGArrivalDeleteReq>(){};
	}

	@Override
	protected DCP_TGArrivalDeleteRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_TGArrivalDeleteRes();
	}

}
