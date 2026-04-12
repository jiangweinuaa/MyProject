package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ECStockDeleteReq;
import com.dsc.spos.json.cust.req.DCP_ECStockDeleteReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_ECStockDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 服務函數：DCP_ECStockDelete
 * 服务说明：电商平台库存上下架删除
 * @author jinzma 
 * @since  2020-02-16
 */
public class DCP_ECStockDelete extends SPosAdvanceService<DCP_ECStockDeleteReq,DCP_ECStockDeleteRes>{

	@Override
	protected void processDUID(DCP_ECStockDeleteReq req, DCP_ECStockDeleteRes res) throws Exception {
	// TODO 自动生成的方法存根
		String eId = req.geteId();
		String shopId = req.getShopId();
		String ecStockNo = req.getRequest().getEcStockNo();
		
		try 
		{
		  //DCP_ECSTOCK_DETAIL
			DelBean db = new DelBean("DCP_ECSTOCK_DETAIL");
			db.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
			db.addCondition("ECSTOCKNO", new DataValue(ecStockNo, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db));
		  
			//DCP_ECSTOCK
			DelBean db2 = new DelBean("DCP_ECSTOCK");
			db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
			db2.addCondition("ECSTOCKNO", new DataValue(ecStockNo, Types.VARCHAR));
			db2.addCondition("STATUS", new DataValue("0", Types.VARCHAR));
			
			this.addProcessData(new DataProcessBean(db2));
			
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
	protected List<InsBean> prepareInsertData(DCP_ECStockDeleteReq req) throws Exception {
	// TODO 自动生成的方法存根
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_ECStockDeleteReq req) throws Exception {
	// TODO 自动生成的方法存根
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_ECStockDeleteReq req) throws Exception {
	// TODO 自动生成的方法存根
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_ECStockDeleteReq req) throws Exception {
	// TODO 自动生成的方法存根

		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		level1Elm request = req.getRequest();
		String ecStockNo = request.getEcStockNo();

		if (Check.Null(ecStockNo)) 
		{
			errMsg.append("上下架编号不可为空值, ");
			isFail = true;
		}
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_ECStockDeleteReq> getRequestType() {
	// TODO 自动生成的方法存根
	return new TypeToken<DCP_ECStockDeleteReq>(){} ;
	}

	@Override
	protected DCP_ECStockDeleteRes getResponseType() {
	// TODO 自动生成的方法存根
	return new DCP_ECStockDeleteRes();
	}

}
