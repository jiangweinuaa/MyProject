package com.dsc.spos.service.imp.json;

import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_MachineDeleteReq;
import com.dsc.spos.json.cust.req.DCP_MachineDeleteReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_MachineDeleteRes;
public class DCP_MachineDelete  extends SPosAdvanceService<DCP_MachineDeleteReq,DCP_MachineDeleteRes> {

	@Override
	protected void processDUID(DCP_MachineDeleteReq req, DCP_MachineDeleteRes res) throws Exception {
		// TODO 自动生成的方法存根
		String eId = req.geteId();
		try 
		{
			List<level1Elm> jsonDatas = req.getRequest().getDatas();	
			for (level1Elm par : jsonDatas) {
				String shopId = par.getShopId();
				String machineId = par.getMachineId();		
				DelBean db1 = new DelBean("PLATFORM_MACHINE");
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
				db1.addCondition("MACHINE", new DataValue(machineId, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));
			}

			this.doExecuteDataToDB();	
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		}
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_MachineDeleteReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_MachineDeleteReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_MachineDeleteReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_MachineDeleteReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		List<level1Elm> jsonDatas = req.getRequest().getDatas();	
		for (level1Elm par : jsonDatas) {
			String shopId = par.getShopId();
			String machineId = par.getMachineId();		
			if (Check.Null(shopId)) {
				errMsg.append("门店编号不可为空值, ");
				isFail = true;
			}
			if (Check.Null(machineId)) {
				errMsg.append("机台编号不可为空值, ");
				isFail = true;
			}

			if (isFail){
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_MachineDeleteReq> getRequestType() {
		// TODO 自动生成的方法存根
		return  new TypeToken<DCP_MachineDeleteReq>(){};
	}

	@Override
	protected DCP_MachineDeleteRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_MachineDeleteRes();
	}


}
