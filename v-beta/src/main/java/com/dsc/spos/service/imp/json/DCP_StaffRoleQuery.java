package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_StaffRoleQueryReq;
import com.dsc.spos.json.cust.res.DCP_StaffRoleQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

public class DCP_StaffRoleQuery extends SPosBasicService<DCP_StaffRoleQueryReq, DCP_StaffRoleQueryRes>
{
	@Override
	protected boolean isVerifyFail(DCP_StaffRoleQueryReq req) throws Exception
	{
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if (req.getRequest() == null)
		{
			errMsg.append("requset不能为空值 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_StaffRoleQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_StaffRoleQueryReq>(){};
	}

	@Override
	protected DCP_StaffRoleQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_StaffRoleQueryRes();
	}

	@Override
	protected DCP_StaffRoleQueryRes processJson(DCP_StaffRoleQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql1 = null;
		String sql2 = null;
		//查询条件
		String eId = req.geteId();;
		String staffNO = req.getRequest().getStaffNo();
		
		//查询资料
		DCP_StaffRoleQueryRes res = null;
		res = this.getResponse();
		
		//查询用户未选择角色
		StringBuffer sqlbuf1 = new StringBuffer("");
		String[] condCountValues1 = {eId,eId,staffNO};		
		sqlbuf1.append("select opGroup,opgName from platform_role where EID=? and status='100'"
					+" and opGroup not in (select opgroup from platform_staffs_role where EID=? and opno=?)");
		sql1 = sqlbuf1.toString();
		List<Map<String, Object>> unselectRoles = this.doQueryData(sql1, condCountValues1);
		if (unselectRoles != null && unselectRoles.isEmpty() == false)
		{
			res.setUnselectRoles(new ArrayList<DCP_StaffRoleQueryRes.level1Elm>());
			for (Map<String, Object> unselectRole : unselectRoles) 
			{
				DCP_StaffRoleQueryRes.level1Elm oneLv1 = res.new level1Elm();
				
				String opGroup = unselectRole.get("OPGROUP").toString();
				String opgName = unselectRole.get("OPGNAME").toString();
				
				oneLv1.setOpGroup(opGroup);
				oneLv1.setOpgName(opgName);
				
				res.getUnselectRoles().add(oneLv1);
			}
		}
		
		//查询用户已选角色
		StringBuffer sqlbuf2 = new StringBuffer("");
		String[] condCountValues2 = {eId,eId,staffNO};
		sqlbuf2.append("select opGroup,opgName from platform_role where EID=? and status='100'"
				+" and opGroup in (select opgroup from platform_staffs_role where EID=? and opno=?)");
		sql2 = sqlbuf2.toString();
		List<Map<String, Object>> selectedRoles = this.doQueryData(sql2, condCountValues2);
		if (selectedRoles != null && selectedRoles.isEmpty() == false)
		{
			res.setSelectedRoles(new ArrayList<DCP_StaffRoleQueryRes.level1Elm>());
			for (Map<String, Object> selectedRole : selectedRoles)
			{
				DCP_StaffRoleQueryRes.level1Elm oneLv1 = res.new level1Elm();

				String opGroup = selectedRole.get("OPGROUP").toString();
				String opgName = selectedRole.get("OPGNAME").toString();
				
				oneLv1.setOpGroup(opGroup);
				oneLv1.setOpgName(opgName);
				
				res.getSelectedRoles().add(oneLv1);
			}
		}
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_StaffRoleQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
