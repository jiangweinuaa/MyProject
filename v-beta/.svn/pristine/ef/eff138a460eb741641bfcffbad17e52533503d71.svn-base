package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_UnitMsgDeleteReq;
import com.dsc.spos.json.cust.req.DCP_UnitMsgDeleteReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_UnitMsgDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 单位信息删除 
 * @author yuanyy
 *
 */
public class DCP_UnitMsgDelete extends SPosAdvanceService<DCP_UnitMsgDeleteReq, DCP_UnitMsgDeleteRes> {

	@Override
	protected void processDUID(DCP_UnitMsgDeleteReq req, DCP_UnitMsgDeleteRes res) throws Exception {
		// TODO Auto-generated method stub
		String eId = req.geteId();
		
	
		try 
		{
			for (level1Elm par : req.getRequest().getUnitList())
			{
				String unit = par.getUnit();
				DelBean db1 = new DelBean("DCP_UNIT");
				db1.addCondition("UNIT", new DataValue(unit, Types.VARCHAR));
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));
				
				DelBean db2 = new DelBean("DCP_UNIT_LANG");
				db2.addCondition("UNIT", new DataValue(unit, Types.VARCHAR));
				db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db2));
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
	protected List<InsBean> prepareInsertData(DCP_UnitMsgDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_UnitMsgDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_UnitMsgDeleteReq req) throws Exception {
		// TODO Auto-generated method stub		
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_UnitMsgDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		if(req.getRequest()==null)
    {
    	errMsg.append("requset不能为空值 ");
    	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
    }
		for (level1Elm par : req.getRequest().getUnitList())
		{
			String unit = par.getUnit();
			
			if (Check.Null(unit)) {
				errMsg.append("单位编码不能为空值 ");
				isFail = true;
			}
			
		}
		
		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_UnitMsgDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_UnitMsgDeleteReq>(){};
	}

	@Override
	protected DCP_UnitMsgDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_UnitMsgDeleteRes();
	}
	
}
