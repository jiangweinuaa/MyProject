package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_SpecUpdateReq;
import com.dsc.spos.json.cust.res.DCP_SpecUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 修改规格信息 2018-10-22
 * @author yuanyy
 *
 */
public class DCP_SpecUpdate extends SPosAdvanceService<DCP_SpecUpdateReq, DCP_SpecUpdateRes> {

	@Override
	protected void processDUID(DCP_SpecUpdateReq req, DCP_SpecUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
		try {

			String specNO = req.getRequest().getSpecNo();
			String specName = req.getRequest().getSpecName();
			String status = req.getRequest().getStatus();
			String eId = req.geteId();

			UptBean ub1 = null;	
			ub1 = new UptBean("DCP_SPEC");
			//add Value
			ub1.addUpdateValue("SPEC_NAME", new DataValue(specName, Types.VARCHAR));
			ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
			//condition
			ub1.addCondition("SPEC_NO", new DataValue(specNO, Types.VARCHAR));
			ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));		
			this.addProcessData(new DataProcessBean(ub1));

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
	protected List<InsBean> prepareInsertData(DCP_SpecUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_SpecUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_SpecUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_SpecUpdateReq req) throws Exception {
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
	protected TypeToken<DCP_SpecUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_SpecUpdateReq>(){};
	}

	@Override
	protected DCP_SpecUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_SpecUpdateRes();
	}

}
