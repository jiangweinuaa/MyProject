package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_DingUserSetMappingCreateReq;
import com.dsc.spos.json.cust.res.DCP_DingUserSetMappingCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_DingUserSetMappingCreate extends SPosAdvanceService<DCP_DingUserSetMappingCreateReq,DCP_DingUserSetMappingCreateRes> {

	@Override
	protected void processDUID(DCP_DingUserSetMappingCreateReq req, DCP_DingUserSetMappingCreateRes res)
			throws Exception {
		// TODO 自动生成的方法存根
		String eId = req.geteId();
		String opNO =req.getRequest().getO_opNo();
		String status =req.getRequest().getStatus();
		String userId = req.getRequest().getUserId();
		String userName = req.getRequest().getUserName();
		String deptId = req.getRequest().getDeptId();

		try
		{
			//删除DCP_DING_USERSET
			DelBean db = new DelBean("DCP_DING_USERSET");
			db.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db.addCondition("OPNO", new DataValue(opNO, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db));

			//userId 是否已存在判断
			String sql =" select *  from DCP_DING_USERSET where userid='"+userId+"' and EID='"+eId+"' ";
			List<Map<String, Object>> getQData=this.doQueryData(sql, null);
			if (getQData == null || getQData.isEmpty()) 
			{
				//绑定
				if (!Check.Null(userId))
				{
					String[] columns = {
							"EID", "OPNO","USERID","USERNAME","DEPTID", "STATUS"											
					};
					DataValue[]	insValue = new DataValue[]{
							new DataValue(eId, Types.VARCHAR), 
							new DataValue(opNO, Types.VARCHAR), 
							new DataValue(userId, Types.VARCHAR),
							new DataValue(userName, Types.VARCHAR),								
							new DataValue(deptId, Types.VARCHAR),
							new DataValue(status, Types.VARCHAR),
					};
					InsBean ib = new InsBean("DCP_DING_USERSET", columns);
					ib.addValues(insValue);
					this.addProcessData(new DataProcessBean(ib)); 
				}
			}
			else
			{
				String opno_q =getQData.get(0).get("OPNO").toString();
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, " USERID:"+userId+" 已被用户:" +opno_q+"绑定，请重新查询");		
			}

			this.doExecuteDataToDB();

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");

		}
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());	
		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_DingUserSetMappingCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_DingUserSetMappingCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_DingUserSetMappingCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_DingUserSetMappingCreateReq req) throws Exception {
		// TODO 自动生成的方法存根	
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String opNO =req.getRequest().getO_opNo();
		String status =req.getRequest().getStatus();

		if (Check.Null(opNO)) 
		{
			errMsg.append("用户编号不可为空值, ");
			isFail = true;
		} 

		if (Check.Null(status)) 
		{
			errMsg.append("状态不可为空值, ");
			isFail = true;
		} 		

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;

	}

	@Override
	protected TypeToken<DCP_DingUserSetMappingCreateReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_DingUserSetMappingCreateReq>(){};
	}

	@Override
	protected DCP_DingUserSetMappingCreateRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_DingUserSetMappingCreateRes();
	}

}
