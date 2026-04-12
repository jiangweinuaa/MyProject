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
import com.dsc.spos.json.cust.req.DCP_BFeeProcessReq;
import com.dsc.spos.json.cust.res.DCP_BFeeProcessRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_BFeeProcess extends SPosAdvanceService<DCP_BFeeProcessReq, DCP_BFeeProcessRes>
{
	@Override
	protected void processDUID(DCP_BFeeProcessReq req, DCP_BFeeProcessRes res) throws Exception {
		// TODO Auto-generated method stub
		String shopId = req.getShopId();
		String eId = req.geteId();
		String confirmBy = req.getOpNO();
		String submitBy = req.getOpNO();
		String accountBy = req.getOpNO();
		Calendar cal = Calendar.getInstance();//获得当前时间
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String confirmDate = df.format(cal.getTime());
		String submitDate = df.format(cal.getTime());	
		String accountDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
		df = new SimpleDateFormat("HHmmss");
		String confirmTime = df.format(cal.getTime());
		String submitTime = df.format(cal.getTime());
		String accountTime = df.format(cal.getTime());
		String bFeeNO = req.getRequest().getbFeeNo();
		String status = req.getRequest().getStatus();

		try 
		{
			String sql = "select status from DCP_BFEE "
					+ " where eId='"+eId+"' and organizationno='"+shopId+"' and BFEENO='"+bFeeNO+"' and status<>'"+status+"' ";
			List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)	
			{
				UptBean ub1 =  new UptBean("DCP_BFEE");
				//add Value
				ub1.addUpdateValue("status", new DataValue(status, Types.VARCHAR));
				ub1.addUpdateValue("ACCOUNT_CHATUSERID", new DataValue(req.getChatUserId(), Types.VARCHAR));
				ub1.addUpdateValue("ConfirmBy", new DataValue(confirmBy, Types.VARCHAR));
				ub1.addUpdateValue("Confirm_Date", new DataValue(confirmDate, Types.VARCHAR));
				ub1.addUpdateValue("Confirm_Time", new DataValue(confirmTime, Types.VARCHAR));
				ub1.addUpdateValue("submitBy", new DataValue(submitBy, Types.VARCHAR));
				ub1.addUpdateValue("submit_Date", new DataValue(submitDate, Types.VARCHAR));
				ub1.addUpdateValue("submit_Time", new DataValue(submitTime, Types.VARCHAR));
				ub1.addUpdateValue("ACCOUNTBY", new DataValue(accountBy, Types.VARCHAR));
				ub1.addUpdateValue("ACCOUNT_DATE", new DataValue(accountDate, Types.VARCHAR));
				ub1.addUpdateValue("ACCOUNT_TIME", new DataValue(accountTime, Types.VARCHAR));
				ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
				ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
				//condition
				ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
				ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				ub1.addCondition("BFEENO", new DataValue(bFeeNO, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(ub1));

				this.doExecuteDataToDB();

				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");
			}
			else 
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据不存在或已确认！");
			}
		}
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_BFeeProcessReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_BFeeProcessReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_BFeeProcessReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_BFeeProcessReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String bFeeNO = req.getRequest().getbFeeNo();
		String status = req.getRequest().getStatus();

		if(Check.Null(bFeeNO)){
			errMsg.append("费用编码不可为空值, ");
			isFail = true;
		}

		if(Check.Null(status)){
			errMsg.append("状态不可为空值, ");
			isFail = true;
		}

		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_BFeeProcessReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_BFeeProcessReq>(){};
	}

	@Override
	protected DCP_BFeeProcessRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_BFeeProcessRes();
	}

}
