package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderECFormatDeleteReq;
import com.dsc.spos.json.cust.res.DCP_OrderECFormatDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 电商订单导入格式删除
 * @author yuanyy 2019-03-08
 *
 */
public class DCP_OrderECFormatDelete extends SPosAdvanceService<DCP_OrderECFormatDeleteReq, DCP_OrderECFormatDeleteRes> {

	@Override
	protected void processDUID(DCP_OrderECFormatDeleteReq req, DCP_OrderECFormatDeleteRes res) throws Exception {
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
	protected List<InsBean> prepareInsertData(DCP_OrderECFormatDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderECFormatDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderECFormatDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		List<DelBean> data = new ArrayList<DelBean>();
		try {
			String eId = req.geteId(); 
			String orderFormatNo = req.getOrderFormatNo();
			  
			DelBean db1 = new DelBean("OC_ECORDERFORMAT");
			db1.addCondition("ORDERFORMATNO", new DataValue(orderFormatNo, Types.VARCHAR));
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			data.add(db1);
			
		    DelBean db2 = new DelBean("OC_ECORDERFORMAT_DETAIL");
			db2.addCondition("ORDERFORMATNO", new DataValue(orderFormatNo, Types.VARCHAR));
			db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			data.add(db2);
			
		} catch (Exception e) {
		
		}
		return data;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderECFormatDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
	    StringBuffer errMsg = new StringBuffer("");
	    int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
	    String orderFormatNo = req.getOrderFormatNo();
	    if(Check.Null(orderFormatNo))
    	{
	      errCt++;
	      errMsg.append("格式编号不可为空值, ");
	      isFail = true;
	    }
		
		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderECFormatDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderECFormatDeleteReq>(){};
	}

	@Override
	protected DCP_OrderECFormatDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderECFormatDeleteRes();
	}
	
}	
