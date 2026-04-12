package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.dao.DataValue.DataExpression;
import com.dsc.spos.json.cust.req.DCP_PayMentNRCCreateReq;
import com.dsc.spos.json.cust.res.DCP_PayMentNRCCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_PayMentNRCCreate extends SPosAdvanceService<DCP_PayMentNRCCreateReq, DCP_PayMentNRCCreateRes> {

	@Override
	protected void processDUID(DCP_PayMentNRCCreateReq req, DCP_PayMentNRCCreateRes res) throws Exception {
		// TODO Auto-generated method stub
		
		try {
			String platformPayCode = req.getPlatformPayCode();
			String platformPayName = req.getPlatformPayName();
			String crmPayCode = req.getCrmPayCode();
			String crmPayName = req.getCrmPayName();
			String priority = req.getPriority();
			String eId = req.geteId();
			
			String onSale = "Y";
			if(!Check.Null(req.getOnSale())){
				onSale = req.getOnSale();
			}
			
			if(!isRepeat(req)){
				
				//大于用户输入的优先级的部分，全部 + 1
				UptBean ub1 = null;	
				ub1 = new UptBean("DCP_PAYMENT_NRC");
				//add Value
				ub1.addUpdateValue("PRIORITY", new DataValue(1, Types.VARCHAR,DataExpression.UpdateSelf));
				//condition
				ub1.addCondition("PRIORITY", new DataValue(priority, Types.VARCHAR,DataExpression.GreaterEQ));
				ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));		
				this.addProcessData(new DataProcessBean(ub1));
				
				String[] columnsName = {
						"EID","PLATFORM_PAYCODE","PLATFORM_PAYNAME","CRM_PAYCODE","CRM_PAYNAME",
						"STATUS","PRIORITY","CREATEBY","ONSALE"
				};
				DataValue[] insValue1 = null;

				insValue1 = new DataValue[]{
						new DataValue(req.geteId(), Types.VARCHAR), 
						new DataValue(platformPayCode, Types.VARCHAR), 
						new DataValue(platformPayName, Types.VARCHAR),
						new DataValue(crmPayCode, Types.VARCHAR), 
						new DataValue(crmPayName, Types.VARCHAR), 
						new DataValue(req.getStatus(), Types.VARCHAR),
						new DataValue(req.getPriority(), Types.VARCHAR),
						new DataValue(req.getOpNO(), Types.VARCHAR),
						new DataValue(onSale, Types.VARCHAR) 
				};

				InsBean ib1 = new InsBean("DCP_PAYMENT_NRC", columnsName);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1)); 
				
				this.doExecuteDataToDB();
			}
			else{
				res.setSuccess(false);
				res.setServiceStatus("200");
				res.setServiceDescription("编码："+platformPayCode + "对应"+crmPayCode + "的支付方式已存在，请勿重复添加！！");
			}
			
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(false);
		}
		
		
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PayMentNRCCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PayMentNRCCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PayMentNRCCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PayMentNRCCreateReq req) throws Exception {
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
	protected TypeToken<DCP_PayMentNRCCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PayMentNRCCreateReq>(){};
	}

	@Override
	protected DCP_PayMentNRCCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_PayMentNRCCreateRes();
	}
	
	/**
	 * 验证新增支付方式是否重复
	 * @param req
	 * @return
	 * @throws Exception
	 */
	private boolean isRepeat(DCP_PayMentNRCCreateReq req) throws Exception{
		boolean isRepeat = false;
		
		try {
			String sql = "select * from DCP_PAYMENT_NRC where EID = '"+req.geteId()+"' "
					+ " and platform_payCode = '"+req.getPlatformPayCode()+"'";
			
			List<Map<String ,Object>> resDatas = this.doQueryData(sql, null);
			if(resDatas.size() > 0){
				isRepeat = true;
			}
		} catch (Exception e) {

		}
		
		return isRepeat;
	}
	

}
