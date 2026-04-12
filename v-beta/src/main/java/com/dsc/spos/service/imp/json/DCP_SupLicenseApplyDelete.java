package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_SupLicenseApplyDeleteReq;
 
import com.dsc.spos.json.cust.res.DCP_SupLicenseApplyRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;
import java.util.Map;

/**
 * 结算日条件 DCP_SupLicenseApply
 * 
 * @date 2024-09-21
 * @author 01029
 */
public class DCP_SupLicenseApplyDelete extends SPosAdvanceService<DCP_SupLicenseApplyDeleteReq, DCP_SupLicenseApplyRes> {

	@Override
	protected void processDUID(DCP_SupLicenseApplyDeleteReq req, DCP_SupLicenseApplyRes res) throws Exception {
		// TODO Auto-generated method stub
		try {

			String eId = req.geteId();
			String billNo = req.getRequest().getBillNo();

			DelBean db1 = new DelBean("DCP_SUPLICENSECHANGE");
			DelBean db2 = new DelBean("DCP_SUPLICENSECHANGE_DETAIL");
 

			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db1.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
			db1.addCondition("STATUS", new DataValue("0", Types.VARCHAR));

			db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db2.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
 

			this.addProcessData(new DataProcessBean(db1));
			this.addProcessData(new DataProcessBean(db2)); 

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
	protected List<InsBean> prepareInsertData(DCP_SupLicenseApplyDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_SupLicenseApplyDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_SupLicenseApplyDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_SupLicenseApplyDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		if (req.getRequest() == null) {
			errMsg.append("request不能为空值 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		String iCode = req.getRequest().getBillNo();

		if (Check.Null(iCode)) {
			errMsg.append("单据编号不能为空值 ");
			isFail = true;
		}

		 
		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_SupLicenseApplyDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_SupLicenseApplyDeleteReq>() {
		};
	}

	@Override
	protected DCP_SupLicenseApplyRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_SupLicenseApplyRes();
	}

}
