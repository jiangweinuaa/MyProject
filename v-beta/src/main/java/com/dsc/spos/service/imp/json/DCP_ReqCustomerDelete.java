package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ReqCustomerDeleteReq;
import com.dsc.spos.json.cust.res.DCP_ReqCustomerDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_ReqCustomerDelete extends SPosAdvanceService<DCP_ReqCustomerDeleteReq, DCP_ReqCustomerDeleteRes> 
{

	@Override
	protected void processDUID(DCP_ReqCustomerDeleteReq req, DCP_ReqCustomerDeleteRes res) throws Exception {
	// TODO Auto-generated method stub
		this.doDelete(req);
		if (res.isSuccess()) 
		{
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_ReqCustomerDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_ReqCustomerDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_ReqCustomerDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
		List<DelBean> data=new ArrayList<DelBean>();
		String eId = req.geteId();;
		String customerID = req.getCustomerID();
		
	//TA_REQCUSTOMER_SHOP
		DelBean db1 = new DelBean("DCP_REQCUSTOMER_SHOP");
		db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		db1.addCondition("CUSTOMER_ID", new DataValue(customerID, Types.VARCHAR));
		data.add(db1);
	//TA_REQCUSTOMER
		DelBean db2 = new DelBean("DCP_REQCUSTOMER");
		db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		db2.addCondition("CUSTOMER_ID", new DataValue(customerID, Types.VARCHAR));
		data.add(db2);
	
	  
		return data;
	}

	@Override
	protected boolean isVerifyFail(DCP_ReqCustomerDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；	
		if (Check.Null(req.getCustomerID())) 
		{
			errCt++;
			errMsg.append("客户ID不可为空值!");
			isFail = true;
		} 
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_ReqCustomerDeleteReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_ReqCustomerDeleteReq>(){};
	}

	@Override
	protected DCP_ReqCustomerDeleteRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_ReqCustomerDeleteRes();
	}

}
