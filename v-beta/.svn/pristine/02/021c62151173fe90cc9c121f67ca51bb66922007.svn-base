package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_SaleRecordDeleteReq;
import com.dsc.spos.json.cust.res.DCP_SaleRecordDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 面销记录
 * @author yuanyy 2019-12-31
 *
 */
public class DCP_SaleRecordDelete extends SPosAdvanceService<DCP_SaleRecordDeleteReq, DCP_SaleRecordDeleteRes> {

	@Override
	protected void processDUID(DCP_SaleRecordDeleteReq req, DCP_SaleRecordDeleteRes res) throws Exception {
		// TODO Auto-generated method stub
		
		try {
			String eId = req.geteId();
			String shopId = req.getShopId();
			String salesRecordNo = req.getRequest().getSalesRecordNo();
			
			DelBean db1 = new DelBean("DCP_SALESRECORD_DETAIL");
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
			db1.addCondition("SALESRECORDNO", new DataValue(salesRecordNo, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));
			
			DelBean db2 = new DelBean("DCP_SALESRECORD");
			db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
			db2.addCondition("SALESRECORDNO", new DataValue(salesRecordNo, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db2));
			
			this.doExecuteDataToDB();
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败！");
		}
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_SaleRecordDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_SaleRecordDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_SaleRecordDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_SaleRecordDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String salesRecordNo = req.getRequest().getSalesRecordNo();
		
		if (Check.Null(salesRecordNo)) 
		{
			errMsg.append("单号不可为空值, ");
			isFail = true;
		}
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;


	}

	@Override
	protected TypeToken<DCP_SaleRecordDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_SaleRecordDeleteReq>(){};
	}

	@Override
	protected DCP_SaleRecordDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_SaleRecordDeleteRes();
	}
	
	
}
