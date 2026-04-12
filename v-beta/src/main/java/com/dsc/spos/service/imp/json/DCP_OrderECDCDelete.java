package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderECDCDeleteReq;
import com.dsc.spos.json.cust.res.DCP_OrderECDCDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 大物流信息新增
 * @author yuanyy 2019-03-27
 *
 */
public class DCP_OrderECDCDelete extends SPosAdvanceService<DCP_OrderECDCDeleteReq , DCP_OrderECDCDeleteRes> {

	@Override
	protected void processDUID(DCP_OrderECDCDeleteReq req, DCP_OrderECDCDeleteRes res) throws Exception {
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
	protected List<InsBean> prepareInsertData(DCP_OrderECDCDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderECDCDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderECDCDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		String eId = req.geteId();
	    String dcNo = req.getDcNo();
		
	    List<DelBean> data = new ArrayList<DelBean>();
		try {
			DelBean db1 = new DelBean("OC_LOGISTICS_DC");
			db1.addCondition("DCNO", new DataValue(dcNo,Types.VARCHAR));
			db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
			data.add(db1);
			
		} catch (Exception e) {

		}
		return data;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderECDCDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
	    StringBuffer errMsg = new StringBuffer("");
	    
	    String dcNo = req.getDcNo();
	    
		if (Check.Null(dcNo)) 
		{
			errMsg.append("大物流中心编码不能为空值 ");
			isFail = true;
		}
		
	    if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
	    
		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderECDCDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderECDCDeleteReq>(){};
	}

	@Override
	protected DCP_OrderECDCDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderECDCDeleteRes();
	}
	


}
