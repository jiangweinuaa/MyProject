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
import com.dsc.spos.json.cust.req.DCP_MachineCreateReq;
import com.dsc.spos.json.cust.res.DCP_MachineCreateRes;

public class DCP_MachineCreate extends SPosAdvanceService <DCP_MachineCreateReq,DCP_MachineCreateRes> {

	@Override
	protected void processDUID(DCP_MachineCreateReq req, DCP_MachineCreateRes res) throws Exception {
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
		
		DataValue[] insValue = null;
		try 
		{
			if (checkGuid(req) == false){
				String[] columns = {"EID", "SHOPID","MACHINE","MACHINENAME","STATUS","BUSINESSTYPE"
						,"APIUSERCODE","APPTYPE","CHANNELID"};
				insValue = new DataValue[]{
						new DataValue(eId, Types.VARCHAR), 
						new DataValue(shopId, Types.VARCHAR), 
						new DataValue(machineNO, Types.VARCHAR), 
						new DataValue(machineName, Types.VARCHAR), 
						new DataValue(status, Types.VARCHAR),
						new DataValue(businessType, Types.VARCHAR),
						new DataValue(apiUsercode, Types.VARCHAR),
						new DataValue(appNo, Types.VARCHAR),
						new DataValue(channelId, Types.VARCHAR)
				};
				
				InsBean ib1 = new InsBean("PLATFORM_MACHINE", columns);
				ib1.addValues(insValue);
				this.addProcessData(new DataProcessBean(ib1)); // 新增單頭		
				this.doExecuteDataToDB();	

			}
			else {
				errMsg.append("机台已存在，请重新输入！");
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}


		}
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_MachineCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_MachineCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_MachineCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_MachineCreateReq req) throws Exception {
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
	protected TypeToken<DCP_MachineCreateReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_MachineCreateReq>(){};
	}

	@Override
	protected DCP_MachineCreateRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_MachineCreateRes();
	}

	private boolean checkGuid(DCP_MachineCreateReq req) throws Exception {
		String sql = null;
		boolean existGuid;
		String eId = req.geteId();
		String shopId = req.getRequest().getShopId();
		String machineNO = req.getRequest().getMachineId();
		String[] conditionValues = {eId,shopId,machineNO}; 		
		sql = "select * from PLATFORM_MACHINE  where EID = ? and SHOPID=? and machine=?  ";
		List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);
		if (getQData != null && getQData.isEmpty() == false) 
			existGuid = true;
		 else 
			existGuid =  false;
		return existGuid;
	}

}
