package com.dsc.spos.service.imp.json;

import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_MachineUpdateReq;
import com.dsc.spos.json.cust.res.DCP_MachineUpdateRes;
public class DCP_MachineUpdate extends SPosAdvanceService <DCP_MachineUpdateReq,DCP_MachineUpdateRes> {

	@Override
	protected void processDUID(DCP_MachineUpdateReq req, DCP_MachineUpdateRes res) throws Exception {
	// TODO 自动生成的方法存根
		StringBuffer errMsg = new StringBuffer("");
		String eId = req.geteId();
		String shopId = req.getRequest().getShopId();
		String machineNO=req.getRequest().getMachineId();
		String machineName=req.getRequest().getMachineName();
		String status =req.getRequest().getStatus();
		String businessType =req.getRequest().getBusinessType();
		String apiUsercode =req.getRequest().getApiUserCode();
		String appNo =req.getRequest().getAppNo();
		String channelId =req.getRequest().getChannelId();
		try 
		{
			String sql = null;
			sql = this.getQuerySql(req);
			String[] conditionValues = {eId,shopId,machineNO}; //查詢條件
			List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);
			if (getQData != null && getQData.isEmpty() == false) {

				UptBean ub1 = null;	
				ub1 = new UptBean("PLATFORM_MACHINE");
				ub1.addUpdateValue("MACHINENAME", new DataValue(machineName, Types.VARCHAR));
				ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
				ub1.addUpdateValue("BUSINESSTYPE", new DataValue(businessType, Types.VARCHAR));
				ub1.addUpdateValue("APIUSERCODE", new DataValue(apiUsercode, Types.VARCHAR));
				ub1.addUpdateValue("APPTYPE", new DataValue(appNo, Types.VARCHAR));
				ub1.addUpdateValue("CHANNELID", new DataValue(channelId, Types.VARCHAR));

				// condition
				ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
				ub1.addCondition("MACHINE", new DataValue(machineNO, Types.VARCHAR));

				this.addProcessData(new DataProcessBean(ub1));
				this.doExecuteDataToDB();
				
				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");
			}
			else
			{
				errMsg.append("机台不存在，请重新输入！");
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}	
	
		}
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_MachineUpdateReq req) throws Exception {
	// TODO 自动生成的方法存根
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_MachineUpdateReq req) throws Exception {
	// TODO 自动生成的方法存根
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_MachineUpdateReq req) throws Exception {
	// TODO 自动生成的方法存根
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_MachineUpdateReq req) throws Exception {
	// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		//必传值不为空		
		String shopId = req.getRequest().getShopId();
		String machineNO = req.getRequest().getMachineId();
		String status = req.getRequest().getStatus();

		if (Check.Null(shopId)) {
			errMsg.append("门店编号不可为空值, ");
			isFail = true;
		}

		if (Check.Null(machineNO)) {
			errMsg.append("机台编号不可为空值, ");
			isFail = true;
		}

		if (Check.Null(status)) {
			errMsg.append("状态不可为空值, ");
			isFail = true;
		}			

		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;

	}

	@Override
	protected TypeToken<DCP_MachineUpdateReq> getRequestType() {
	// TODO 自动生成的方法存根
	return new TypeToken<DCP_MachineUpdateReq>(){};
	}

	@Override
	protected DCP_MachineUpdateRes getResponseType() {
	// TODO 自动生成的方法存根
	return new DCP_MachineUpdateRes();
	}

	@Override
	protected String getQuerySql(DCP_MachineUpdateReq req) throws Exception {
		String sql = null;
		sql= " select *  from PLATFORM_MACHINE  where EID=? and SHOPID=? and machine=? ";
		return sql;
	}
	
}
