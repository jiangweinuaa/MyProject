package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PasswordUpdateReq;
import com.dsc.spos.json.cust.res.DCP_PasswordUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_PasswordUpdate extends SPosAdvanceService<DCP_PasswordUpdateReq, DCP_PasswordUpdateRes> {

	Logger logger = LogManager.getLogger(DCP_PasswordUpdate.class);

	@Override
	protected void processDUID(DCP_PasswordUpdateReq req, DCP_PasswordUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
		try
		{
			String opNO = "" ;

			String eId = req.geteId();;
			String oldPassword = req.getRequest().getOldPassword();
			String newPassword = req.getRequest().getNewPassword();
			String updateType = req.getRequest().getUpdateType();	

			// "updateType":"0",	必传且非空，密码更新类型	0-修改密码，需要原密码  1-重置密码，不需要原密码
			if(updateType.equals("0")){
				opNO = req.getOpNO();//取basicJSON里的用户
				String sql = null;
				sql = this.getPasswordSql(req, oldPassword);
				List<Map<String , Object>> getPasswordDatas = this.doQueryData(sql, null);
				//当返回的结果大于0 ， 则说明输入的旧密码是正确的， 当结果不于0 ，说明密码不正确
				//当根据密码查出来的结果值size <=0
				if(getPasswordDatas.size() <= 0){
					res.setSuccess(false);
					//res.setServiceDescription("输入的旧密码错误！");
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"输入的旧密码错误！"  );
				}
			}
			else {
				opNO = req.getRequest().getO_opNo() ;//取前端给的用户
			}

			UptBean	ub_code = new UptBean("PLATFORM_STAFFS");		
			ub_code.addUpdateValue("PASSWORD", new DataValue(newPassword, Types.VARCHAR));	
			ub_code.addUpdateValue("ISNEW", new DataValue("N", Types.VARCHAR));	
			ub_code.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			ub_code.addCondition("OPNO", new DataValue(opNO, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(ub_code));
			this.doExecuteDataToDB();
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		}
		catch(Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());	
		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PasswordUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PasswordUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PasswordUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PasswordUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String updateType = req.getRequest().getUpdateType();
		String oldPassword = req.getRequest().getOldPassword();
		String newPassword = req.getRequest().getNewPassword();
    String o_opNO = req.getRequest().getO_opNo();
		if (Check.Null(updateType)) {
			isFail = true;
			errMsg.append("密码修改类型不可为空值, ");
		}
		else{

			if(updateType.equals("0")){
				if (Check.Null(oldPassword)) {
					isFail = true;
					errMsg.append("原密码不可为空值, ");
				} 
			}
			else
			{
				if (Check.Null(o_opNO)) {
					isFail = true;
					errMsg.append("用户编号不可为空值, ");
				} 
			}

		}

		if (Check.Null(newPassword)) {
			isFail = true;
			errMsg.append("新密码不可为空值, ");
		}  

		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_PasswordUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PasswordUpdateReq>(){};
	}

	@Override
	protected DCP_PasswordUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_PasswordUpdateRes();
	}

	protected String getPasswordSql(DCP_PasswordUpdateReq req, String oldPassword){
		String eId = req.geteId();
		String OPNO = req.getOpNO();

		String sql = "SELECT PASSWORD  FROM platform_staffs "
				+ " WHERE  OPNO = '"+OPNO+"' "
				//+ " AND organizationNO = '"+organizationNO+"' "
				+ " AND EID = '"+eId+"' "
				+ " and password = '"+oldPassword+"'" ;
		return sql ;
	}

}
