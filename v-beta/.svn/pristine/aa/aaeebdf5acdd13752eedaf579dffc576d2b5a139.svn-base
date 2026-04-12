package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_StaffDeleteReq;
import com.dsc.spos.json.cust.res.DCP_StaffDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_StaffDelete extends SPosAdvanceService<DCP_StaffDeleteReq, DCP_StaffDeleteRes>{

	@Override
	protected void processDUID(DCP_StaffDeleteReq req, DCP_StaffDeleteRes res) throws Exception 
	{
		// TODO Auto-generated method stub
		String eId = req.geteId();
		String staffNO = req.getRequest().getStaffNo();
		
		//Platform_staffs_Role
		DelBean db1 = new DelBean("PLATFORM_STAFFS_ROLE");
		db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		db1.addCondition("opNO", new DataValue(staffNO, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db1));
		
		//Platform_staffs_shop
		DelBean db2 = new DelBean("PLATFORM_STAFFS_SHOP");
		db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		db2.addCondition("opNO", new DataValue(staffNO, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db2));

		//platform_staffs_lang
		DelBean db3 = new DelBean("PLATFORM_STAFFS_LANG");
		db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		db3.addCondition("opNO", new DataValue(staffNO, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db3));

		//Platform_staffs
		DelBean db4 = new DelBean("PLATFORM_STAFFS");
		db4.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		db4.addCondition("opNO", new DataValue(staffNO, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db4));
		
		this.doExecuteDataToDB();
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_StaffDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_StaffDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_StaffDeleteReq req) throws Exception {
		// TODO Auto-generated method stub			
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_StaffDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  
		if(req.getRequest()==null)
	    {
	    	errMsg.append("requset不能为空值 ");
	    	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
	    }
		String staffNO = req.getRequest().getStaffNo();
		if (Check.Null(staffNO)) {
			isFail = true;
			errCt++;
			errMsg.append("用户编码不可为空值, ");
		}  

		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		return isFail;
	}

	@Override
	protected TypeToken<DCP_StaffDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_StaffDeleteReq>(){};
	}

	@Override
	protected DCP_StaffDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_StaffDeleteRes();
	}

}
