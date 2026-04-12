package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_HorseLampDeleteReq;
import com.dsc.spos.json.cust.res.DCP_HorseLampDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.google.gson.reflect.TypeToken;

/**
 * 跑马灯删除 
 * @author yuanyy
 *
 */
public class DCP_HorseLampDelete extends SPosAdvanceService<DCP_HorseLampDeleteReq, DCP_HorseLampDeleteRes> {

	@Override
	protected void processDUID(DCP_HorseLampDeleteReq req, DCP_HorseLampDeleteRes res) throws Exception {
		// TODO Auto-generated method stub
		
		try {
			String eId = req.geteId();
			String billNo = req.getRequest().getBillNo();
			
			DelBean db1 = new DelBean("DCP_HORSELAMP_PICKSHOP");
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db1.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));
			
			DelBean db2 = new DelBean("DCP_HORSELAMP");
			db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db2.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db2));
			
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
	protected List<InsBean> prepareInsertData(DCP_HorseLampDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_HorseLampDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_HorseLampDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_HorseLampDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_HorseLampDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_HorseLampDeleteReq>(){};
	}

	@Override
	protected DCP_HorseLampDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_HorseLampDeleteRes();
	}
	
}
