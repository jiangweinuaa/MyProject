package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_SpecDeleteReq;
import com.dsc.spos.json.cust.res.DCP_SpecDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_SpecDelete extends SPosAdvanceService<DCP_SpecDeleteReq, DCP_SpecDeleteRes> {

	@Override
	protected void processDUID(DCP_SpecDeleteReq req, DCP_SpecDeleteRes res) throws Exception {
		// TODO Auto-generated method stub

		try 
		{
			String specNO = req.getRequest().getSpecNo();
			String eId = req.geteId();

			DelBean db1 = new DelBean("DCP_SPEC");
			db1.addCondition("SPEC_NO", new DataValue(specNO,Types.VARCHAR));
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));

			this.doExecuteDataToDB();

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");

		} catch (Exception e) {
			// TODO: handle exception
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_SpecDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_SpecDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_SpecDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		List<DelBean> data = new ArrayList<DelBean>();

		return data;
	}

	@Override
	protected boolean isVerifyFail(DCP_SpecDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		if(req.getRequest()==null)
		{
			errMsg.append("requset不能为空值 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		String specNO = req.getRequest().getSpecNo();

		if(Check.Null(specNO)){
			errMsg.append("规格编码不能为空值 ");
			isFail = true;
		}
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_SpecDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_SpecDeleteReq>(){};
	}

	@Override
	protected DCP_SpecDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_SpecDeleteRes();
	}

}
