package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PlanWorkDeleteReq;
import com.dsc.spos.json.cust.res.DCP_PlanWorkDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.google.gson.reflect.TypeToken;

public class DCP_PlanWorkDelete extends SPosAdvanceService<DCP_PlanWorkDeleteReq, DCP_PlanWorkDeleteRes> {

	@Override
	protected void processDUID(DCP_PlanWorkDeleteReq req, DCP_PlanWorkDeleteRes res) throws Exception {
		// TODO Auto-generated method stub
		String[] workNO = req.getRequest().getWorkNo();
		String eId = req.geteId();
		try {
			for (int i =0;i<workNO.length;i++) 
			{
				DelBean db1 = new DelBean("DCP_PLANWORK");
				db1.addCondition("WORKNO", new DataValue(workNO[i],Types.VARCHAR));
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));
			}
			this.doExecuteDataToDB();	
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");

		} catch (Exception e) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PlanWorkDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PlanWorkDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PlanWorkDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PlanWorkDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		//必传值不为空
		String[] workNO = req.getRequest().getWorkNo();

		if(workNO == null || workNO.length == 0){
			errMsg.append("班次编码不可为空值 ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400 , errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_PlanWorkDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PlanWorkDeleteReq>(){};
	}

	@Override
	protected DCP_PlanWorkDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_PlanWorkDeleteRes();
	}

}
