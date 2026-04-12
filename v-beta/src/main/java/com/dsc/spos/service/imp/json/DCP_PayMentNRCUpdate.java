package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.dao.DataValue.DataExpression;
import com.dsc.spos.json.cust.req.DCP_PayMentNRCUpdateReq;
import com.dsc.spos.json.cust.res.DCP_PayMentNRCUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_PayMentNRCUpdate extends SPosAdvanceService<DCP_PayMentNRCUpdateReq, DCP_PayMentNRCUpdateRes> {

	@Override
	protected void processDUID(DCP_PayMentNRCUpdateReq req, DCP_PayMentNRCUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
		
		try {
			String eId = req.geteId();			
			String platformPayCode = req.getPlatformPayCode();
			String platformPayName = req.getPlatformPayName();
			String crmPayCode = req.getCrmPayCode();
			String crmPayName = req.getCrmPayName();
			
			String priority = req.getPriority();
			String toPriority = req.getToPriority();
			
			UptBean ub2 = null;	
			ub2 = new UptBean("DCP_PAYMENT_NRC");
			if(!Check.Null(toPriority)){
				int toPt = Integer.parseInt(toPriority);
				int pt = Integer.parseInt(priority);
			
				if(toPt > pt){  //相当 于   下移
					
					String[] priorityList = new String[toPt-pt];
					for(int i=0; i < toPt-pt ; i++) {
						priorityList[i] = String.valueOf(pt +i+ 1);
					}
					String str1 = StringUtils.join(priorityList,",");
					//add Value
					ub2.addUpdateValue("PRIORITY", new DataValue(1, Types.VARCHAR,DataExpression.SubSelf));
					//condition
					ub2.addCondition("PRIORITY", new DataValue(str1, Types.VARCHAR,DataExpression.IN));
				}
				else if(toPt < pt){  //  上移
					
					String[] priorityList = new String[pt-toPt];
					for(int i=0; i < pt- toPt ; i++) {
						priorityList[i] = String.valueOf(toPt + i);
					}
					String str1 = StringUtils.join(priorityList,",");
					
					//add Value
					ub2.addUpdateValue("PRIORITY", new DataValue(1, Types.VARCHAR,DataExpression.UpdateSelf));
					//condition
					ub2.addCondition("PRIORITY", new DataValue(str1, Types.VARCHAR,DataExpression.IN));
				}
			}
			
			ub2.addUpdateValue("PLATFORM_PAYNAME", new DataValue(platformPayName, Types.VARCHAR));
			ub2.addUpdateValue("CRM_PAYNAME", new DataValue(crmPayName, Types.VARCHAR));
			ub2.addUpdateValue("STATUS", new DataValue(req.getStatus(), Types.VARCHAR));
			ub2.addUpdateValue("UPDATEBY", new DataValue(req.getOpNO(), Types.VARCHAR));
			ub2.addUpdateValue("PRIORITY", new DataValue(req.getPriority(),Types.VARCHAR));
			ub2.addUpdateValue("CRM_PAYCODE", new DataValue(req.getCrmPayCode(), Types.VARCHAR));
			
			if(!Check.Null(req.getOnSale())){ //兼容新字段
				ub2.addUpdateValue("ONSALE", new DataValue(req.getOnSale(), Types.VARCHAR));
			}
			
			ub2.addCondition("PLATFORM_PAYCODE", new DataValue(platformPayCode, Types.VARCHAR));
//			ub2.addCondition("CRM_PAYCODE", new DataValue(crmPayCode, Types.VARCHAR));
			ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));		
			this.addProcessData(new DataProcessBean(ub2));
			
			this.doExecuteDataToDB();
			res.setSuccess(true);
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(false);
		}
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PayMentNRCUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PayMentNRCUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PayMentNRCUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PayMentNRCUpdateReq req) throws Exception {
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
	protected TypeToken<DCP_PayMentNRCUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PayMentNRCUpdateReq>(){};
	}

	@Override
	protected DCP_PayMentNRCUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_PayMentNRCUpdateRes();
	}
	
	
	

}
