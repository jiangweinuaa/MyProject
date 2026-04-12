package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_HorseLampCheckReq;
import com.dsc.spos.json.cust.res.DCP_HorseLampCheckRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.google.gson.reflect.TypeToken;

/**
 * 跑马灯启用/禁用 
 * @author yuanyy
 *
 */
public class DCP_HorseLampCheck extends SPosAdvanceService<DCP_HorseLampCheckReq, DCP_HorseLampCheckRes> {

	@Override
	protected void processDUID(DCP_HorseLampCheckReq req, DCP_HorseLampCheckRes res) throws Exception {
		// TODO Auto-generated method stub
		
		try {
			String eId = req.geteId();
			String billNo = req.getRequest().getBillNo();
			String status = req.getRequest().getActionType();
			
			UptBean ub1 = null;
			ub1 = new UptBean("DCP_HORSELAMP");
			ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
			
			// condition
			ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			ub1.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));

			this.addProcessData(new DataProcessBean(ub1));
			
			this.doExecuteDataToDB();
			
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功！");
			
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败！");
		}
		
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_HorseLampCheckReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_HorseLampCheckReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_HorseLampCheckReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_HorseLampCheckReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_HorseLampCheckReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_HorseLampCheckReq>(){};
	}

	@Override
	protected DCP_HorseLampCheckRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_HorseLampCheckRes();
	}
	
}
