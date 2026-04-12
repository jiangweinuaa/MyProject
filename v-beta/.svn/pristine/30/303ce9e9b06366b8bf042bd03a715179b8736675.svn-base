package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ServiceChargeDeleteReq;
import com.dsc.spos.json.cust.res.DCP_ServiceChargeDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 服务费删除
 * @author yuanyy 2019-03-01
 *
 */
public class DCP_ServiceChargeDelete extends SPosAdvanceService<DCP_ServiceChargeDeleteReq, DCP_ServiceChargeDeleteRes> {

	@Override
	protected void processDUID(DCP_ServiceChargeDeleteReq req, DCP_ServiceChargeDeleteRes res) throws Exception {
		// TODO Auto-generated method stub

		try {
			String eId = req.geteId(); 
			String serviceChargeNO = req.getRequest().getServiceChargeNo();

			DelBean db1 = new DelBean("DCP_SERVICECHARGE");
			db1.addCondition("SERVICECHARGENO", new DataValue(serviceChargeNO, Types.VARCHAR));
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));

			DelBean db2 = new DelBean("DCP_SERVICECHARGE_DETAIL");
			db2.addCondition("SERVICECHARGENO", new DataValue(serviceChargeNO, Types.VARCHAR));
			db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db2));

			this.doExecuteDataToDB();	

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");

		} catch (Exception e) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}


	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_ServiceChargeDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_ServiceChargeDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_ServiceChargeDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_ServiceChargeDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String serviceChargeNO = req.getRequest().getServiceChargeNo();
		if (Check.Null(serviceChargeNO)) 
		{
			errMsg.append("服务费编码不能为空值 ");
			isFail = true;
		}
		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_ServiceChargeDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_ServiceChargeDeleteReq>(){};
	}

	@Override
	protected DCP_ServiceChargeDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_ServiceChargeDeleteRes();
	}


}
