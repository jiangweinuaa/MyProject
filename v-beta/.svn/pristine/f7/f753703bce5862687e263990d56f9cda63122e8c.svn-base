package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderECExpFormatDeleteReq;
import com.dsc.spos.json.cust.res.DCP_OrderECExpFormatDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 电商订单导出格式查询
 * @author yuanyy 2019-03-13
 *
 */
public class DCP_OrderECExpFormatDelete extends SPosAdvanceService<DCP_OrderECExpFormatDeleteReq, DCP_OrderECExpFormatDeleteRes> {

	@Override
	protected void processDUID(DCP_OrderECExpFormatDeleteReq req, DCP_OrderECExpFormatDeleteRes res) throws Exception {
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
	protected List<InsBean> prepareInsertData(DCP_OrderECExpFormatDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderECExpFormatDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderECExpFormatDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		List<DelBean> data = new ArrayList<DelBean>();
		try {
			String eId = req.geteId(); 
			String expOrderFormatNo = req.getExpOrderFormatNo();
			DelBean db1 = new DelBean("OC_ECEXPORDERFORMAT");
			db1.addCondition("EXPFORMATNO", new DataValue(expOrderFormatNo, Types.VARCHAR));
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			data.add(db1);
			
		    DelBean db2 = new DelBean("OC_ECEXPORDERFORMAT_DETAIL");
			db2.addCondition("EXPFORMATNO", new DataValue(expOrderFormatNo, Types.VARCHAR));
			db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			data.add(db2);
			
		} catch (Exception e) {

		}
		return data;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderECExpFormatDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
	    StringBuffer errMsg = new StringBuffer("");
	    int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
	    String expOrderFormatNo = req.getExpOrderFormatNo();
	    if(Check.Null(expOrderFormatNo))
    	{
	      errCt++;
	      errMsg.append("格式编号不可为空值, ");
	      isFail = true;
	    }
		
	    if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderECExpFormatDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderECExpFormatDeleteReq>(){};
	}

	@Override
	protected DCP_OrderECExpFormatDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderECExpFormatDeleteRes();
	}


}
