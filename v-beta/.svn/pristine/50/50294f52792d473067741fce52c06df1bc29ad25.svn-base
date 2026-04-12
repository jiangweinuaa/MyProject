package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_RoleDeleteReq;
import com.dsc.spos.json.cust.res.DCP_RoleDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_RoleDelete extends SPosAdvanceService<DCP_RoleDeleteReq, DCP_RoleDeleteRes> 
{
	@Override
	protected void processDUID(DCP_RoleDeleteReq req, DCP_RoleDeleteRes res) throws Exception {
	// TODO Auto-generated method stub	
		String eId = req.geteId();;
		String opGroup = req.getRequest().getOpGroup();
		
	//PLATFORM_POWER
		DelBean db1 = new DelBean("PLATFORM_POWER");
		db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		db1.addCondition("OPGROUP", new DataValue(opGroup, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db1));
	//PLATFORM_BILLPOWER
		DelBean db2 = new DelBean("PLATFORM_BILLPOWER");
		db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		db2.addCondition("OPGROUP", new DataValue(opGroup, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db2));
	//PLATFORM_ROLE
		DelBean db3 = new DelBean("PLATFORM_ROLE");
		db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		db3.addCondition("OPGROUP", new DataValue(opGroup, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db3));
		
		this.doExecuteDataToDB();
		
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_RoleDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_RoleDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_RoleDeleteReq req) throws Exception {
	// TODO Auto-generated method stub  
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_RoleDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  
		if(req.getRequest()==null)
		{
		  	errMsg.append("request不能为空 ");
		  	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		String opGroup = req.getRequest().getOpGroup();
		if (Check.Null(opGroup)) {
			isFail = true;
			errCt++;
			errMsg.append("角色编码不可为空值, ");
		}
        //判断角色是否绑定用户
		if(opGroup.length()>0&&isRoleBindUser(opGroup)){
			isFail = true;
			errCt++;
			errMsg.append("该角色已绑定用户,不可删除 ");
		}

		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_RoleDeleteReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_RoleDeleteReq>(){};
	}

	@Override
	protected DCP_RoleDeleteRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_RoleDeleteRes();
	}

	public boolean isRoleBindUser(String opGroup) throws Exception{
		String sql=String.format("select * from PLATFORM_STAFFS_ROLE where OPGROUP='%s'",opGroup);
		List<Map<String, Object>> list = this.doQueryData(sql, null);

		return list.size()>0;
	}

}
