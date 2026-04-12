package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderECCommerceDeleteReq;
import com.dsc.spos.json.cust.res.DCP_OrderECCommerceDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 电商平台信息删除
 * @author yuanyy 2019-03-13
 *
 */
public class DCP_OrderECCommerceDelete extends SPosAdvanceService<DCP_OrderECCommerceDeleteReq, DCP_OrderECCommerceDeleteRes> {

	@Override
	protected void processDUID(DCP_OrderECCommerceDeleteReq req, DCP_OrderECCommerceDeleteRes res) throws Exception {
		// TODO Auto-generated method stub
		this.doDelete(req);	
		
		if(res.isSuccess())
		{
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");			
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderECCommerceDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderECCommerceDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderECCommerceDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		List<DelBean> data = new ArrayList<DelBean>();
		String eId = req.geteId();
		String platFormId = req.getPlatformID().toString();
		DelBean db1 = new DelBean("OC_ECOMMERCE");
		db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		db1.addCondition("OTHER_NO", new DataValue(platFormId, Types.VARCHAR));
		data.add(db1);	
		return data;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderECCommerceDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
		String PlatformID = req.getPlatformID();
		
		if (Check.Null(PlatformID)) 
		{
			errCt++;
			errMsg.append("电商平台标识不可为空值  ");
			isFail = true;
		}
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderECCommerceDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderECCommerceDeleteReq>(){};
	}

	@Override
	protected DCP_OrderECCommerceDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderECCommerceDeleteRes();
	}
	

}
