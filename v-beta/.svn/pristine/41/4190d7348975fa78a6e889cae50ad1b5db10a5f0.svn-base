package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_UnitConvertDeleteReq;
import com.dsc.spos.json.cust.req.DCP_UnitConvertDeleteReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_UnitConvertDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 删除单位换算信息
 * @author yuanyy
 *
 */
public class DCP_UnitConvertDelete extends SPosAdvanceService<DCP_UnitConvertDeleteReq, DCP_UnitConvertDeleteRes> {

	@Override
	protected void processDUID(DCP_UnitConvertDeleteReq req, DCP_UnitConvertDeleteRes res) throws Exception {
		// TODO Auto-generated method stub
		try {
			String eId = req.geteId();
			for (level1Elm par : req.getRequest().getUnitList())
			{
				String oUnit = par.getoUnit();
				String unit = par.getUnit();
				
				DelBean db1 = new DelBean("DCP_UNITCONVERT");
				db1.addCondition("UNIT", new DataValue(unit, Types.VARCHAR));
				db1.addCondition("OUNIT", new DataValue(oUnit, Types.VARCHAR));
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				//db1.addCondition("STATUS", new DataValue("-1", Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));
			}
			
			
			this.doExecuteDataToDB();
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");

		} 
		catch (Exception e) 
		{
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行异常:"+e.getMessage());

		}

		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_UnitConvertDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_UnitConvertDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_UnitConvertDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		List<DelBean> data = new ArrayList<DelBean>();
		
		return data;
	}

	@Override
	protected boolean isVerifyFail(DCP_UnitConvertDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		if(req.getRequest()==null)
    {
    	errMsg.append("requset不能为空值 ");
    	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
    }
		// 必传值不为空
		for (level1Elm par : req.getRequest().getUnitList())
		{
			String unit = par.getUnit();
			String oUnit = par.getoUnit();
			if (Check.Null(unit)) {
				errMsg.append("目标单位不能为空值 ");
				isFail = true;
			}
			if (Check.Null(oUnit)) {
				errMsg.append("来源单位不能为空值 ");
				isFail = true;
			}
			
		}
			
		
		
		
		
		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_UnitConvertDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_UnitConvertDeleteReq>(){};
	}

	@Override
	protected DCP_UnitConvertDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_UnitConvertDeleteRes();
	}}
