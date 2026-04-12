package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PayMentNRCDeleteReq;
import com.dsc.spos.json.cust.res.DCP_PayMentNRCDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_PayMentNRCDelete extends SPosAdvanceService<DCP_PayMentNRCDeleteReq, DCP_PayMentNRCDeleteRes> {

	@Override
	protected void processDUID(DCP_PayMentNRCDeleteReq req, DCP_PayMentNRCDeleteRes res) throws Exception {
		// TODO Auto-generated method stub
		
		try {
			String eId = req.geteId();
			
			String platformPayCode = req.getPlatformPayCode();
			String crmPayCode = req.getCrmPayCode();
			
			DelBean del = new DelBean("DCP_PAYMENT_NRC");
			del.addCondition("EID",  new DataValue(eId, Types.VARCHAR));
			del.addCondition("PLATFORM_PAYCODE",  new DataValue(platformPayCode, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(del));
			
			this.doExecuteDataToDB();
			res.setSuccess(true);
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(false);
		}
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PayMentNRCDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PayMentNRCDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PayMentNRCDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PayMentNRCDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		String platformPayCode = req.getPlatformPayCode();
		String crmPayCode = req.getCrmPayCode();
		

		if (Check.Null(platformPayCode)) 
		{
			errMsg.append("平台支付编码不可为空值, ");
			isFail = true;
		} 
		
//		if (Check.Null(crmPayCode)) 
//		{
//			errMsg.append("CRM支付编码不可为空值, ");
//			isFail = true;
//		} 

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_PayMentNRCDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PayMentNRCDeleteReq>(){};
	}

	@Override
	protected DCP_PayMentNRCDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_PayMentNRCDeleteRes();
	}
	
	
	

}
