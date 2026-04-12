package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_RefundSetUpdateReq;
import com.dsc.spos.json.cust.res.DCP_RefundSetUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_RefundSetUpdate extends SPosAdvanceService<DCP_RefundSetUpdateReq,DCP_RefundSetUpdateRes>
{
	@Override
	protected void processDUID(DCP_RefundSetUpdateReq req, DCP_RefundSetUpdateRes res) throws Exception 
	{		
		String eId = req.geteId();
		DCP_RefundSetUpdateReq.Level1Elm data = req.getRequest();
		
		String opNO = req.getOpNO();

		Calendar cal = Calendar.getInstance();// 获得当前时间
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String createTime = df.format(cal.getTime());
		
		if(checkExist(req)){
			UptBean ub1 = new UptBean("CRM_REFUNDSET");
			ub1.addUpdateValue("APPLYREFUND", new DataValue(data.getApplyRefund(), Types.INTEGER));//申请退款
			ub1.addUpdateValue("APPLYREFUNDDELIVERY", new DataValue(data.getApplyRefundDelivery(), Types.INTEGER));//发货后申请退款
			ub1.addUpdateValue("APPLYREFUNDLIMIT", new DataValue(data.getApplyRefundLimit(), Types.INTEGER));//申请退单期限
			ub1.addUpdateValue("AUTOREFUND", new DataValue(data.getAutoRefund(), Types.INTEGER));//自动审核退单
			ub1.addUpdateValue("CANCELLIMIT", new DataValue(data.getCancelLimit(), Types.INTEGER));//自动取消退货期限
			ub1.addUpdateValue("REFUNDMEMBER", new DataValue(data.getRefundMember(), Types.INTEGER));//申请退单次数
			ub1.addUpdateValue("LASTMODIOPID", new DataValue(opNO, Types.VARCHAR));
			ub1.addUpdateValue("LASTMODITIME", new DataValue(createTime, Types.DATE));
			ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));	
			ub1.addCondition("APPID", new DataValue(data.getAppId(), Types.VARCHAR));
			this.addProcessData(new DataProcessBean(ub1));
		}else{
			String[] columns1 = { "EID","APPID","APPLYREFUND","APPLYREFUNDDELIVERY","APPLYREFUNDLIMIT",
					"AUTOREFUND","CANCELLIMIT","REFUNDMEMBER","LASTMODIOPID","LASTMODITIME"};
			
			DataValue[] insValue1 = null;

			insValue1 = new DataValue[]{
					new DataValue(eId, Types.VARCHAR),
					new DataValue(data.getAppId(), Types.VARCHAR),
					new DataValue(data.getApplyRefund(), Types.INTEGER),
					new DataValue(data.getApplyRefundDelivery(), Types.INTEGER),
					new DataValue(data.getApplyRefundLimit(), Types.INTEGER),
					
					new DataValue(data.getAutoRefund(), Types.INTEGER),
					new DataValue(data.getCancelLimit(), Types.INTEGER),
					new DataValue(data.getRefundMember(), Types.INTEGER),
					new DataValue(opNO, Types.VARCHAR),
					new DataValue(createTime, Types.DATE),
			};

			InsBean ib1 = new InsBean("CRM_REFUNDSET", columns1);
			ib1.addValues(insValue1);
			this.addProcessData(new DataProcessBean(ib1)); // 新增單頭
		}
		this.doExecuteDataToDB();
		res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
			
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_RefundSetUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_RefundSetUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_RefundSetUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_RefundSetUpdateReq req) throws Exception 
	{	
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		DCP_RefundSetUpdateReq.Level1Elm request = req.getRequest();

		

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		if (request == null)
		{
			errMsg.append("request不可为空, ");
			isFail = true;
			
		}else{
			if (Check.Null(request.getAppId())) 
			{
				errMsg.append("公众号代号不可为空值, ");
				isFail = true;
			} 
		}
		

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}	

		return isFail;	
	}

	@Override
	protected TypeToken<DCP_RefundSetUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_RefundSetUpdateReq>(){};
	}

	@Override
	protected DCP_RefundSetUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_RefundSetUpdateRes();
	}

	private boolean checkExist(DCP_RefundSetUpdateReq req) throws Exception {
		String sql;
		boolean existGuid;
		sql = "select 1 from CRM_REFUNDSET where EID = ? AND APPID = ?";
		String[] conditionValues = { req.geteId(), req.getRequest().getAppId() };
		List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);
		existGuid = getQData != null && !getQData.isEmpty();
		return existGuid;
	}
}
