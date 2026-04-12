package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_UnitConvertUpdateReq;
import com.dsc.spos.json.cust.res.DCP_UnitConvertUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.DateFormatUtils;
import com.google.gson.reflect.TypeToken;

public class DCP_UnitConvertUpdate extends SPosAdvanceService<DCP_UnitConvertUpdateReq, DCP_UnitConvertUpdateRes>{

	@Override
	protected void processDUID(DCP_UnitConvertUpdateReq req, DCP_UnitConvertUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
		
		String eId = req.geteId();
		String oUnit = req.getRequest().getoUnit();
		String unit = req.getRequest().getUnit();
		String unitRatio = req.getRequest().getUnitRatio();
		String status = req.getRequest().getStatus();
		String oPId = req.getEmployeeNo();
		//String oPName = req.getOpName();
		String lastmoditime = DateFormatUtils.getNowDateTime();
		
		UptBean ub1 = null;	
		ub1 = new UptBean("DCP_UNITCONVERT");
		//add Value
		ub1.addUpdateValue("UNIT_RATIO", new DataValue(unitRatio, Types.VARCHAR));
		ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
		//condition
		ub1.addCondition("UNIT", new DataValue(unit, Types.VARCHAR));
		ub1.addCondition("OUNIT", new DataValue(oUnit, Types.VARCHAR));
		ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));		 
		ub1.addUpdateValue("LASTMODIOPID", new DataValue(oPId, Types.VARCHAR));
		ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
		this.addProcessData(new DataProcessBean(ub1));
		
		this.doExecuteDataToDB();
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_UnitConvertUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_UnitConvertUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_UnitConvertUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_UnitConvertUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		if(req.getRequest()==null)
    {
    	errMsg.append("requset不能为空值 ");
    	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
    }
		// 必传值不为空
		String unit = req.getRequest().getUnit();
		String oUnit = req.getRequest().getoUnit();
		if (Check.Null(unit)) {
			errMsg.append("目标单位不能为空值 ");
			isFail = true;
		}
		
		if (Check.Null(oUnit)) {
			errMsg.append("来源单位不能为空值 ");
			isFail = true;
		}
		
		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_UnitConvertUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_UnitConvertUpdateReq>(){};
	}

	@Override
	protected DCP_UnitConvertUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_UnitConvertUpdateRes();
	}}
