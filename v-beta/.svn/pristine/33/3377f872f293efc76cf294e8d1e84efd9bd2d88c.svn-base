package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_DingFuncDeleteReq;
import com.dsc.spos.json.cust.res.DCP_DingFuncDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
/**
 * 服务函数：DingFuncDeleteDCP
 * 服务说明：钉钉功能审批删除
 * @author jinzma
 * @since  2019-10-31
 */
public class DCP_DingFuncDelete  extends SPosAdvanceService<DCP_DingFuncDeleteReq,DCP_DingFuncDeleteRes>{

	@Override
	protected void processDUID(DCP_DingFuncDeleteReq req, DCP_DingFuncDeleteRes res) throws Exception {
	// TODO 自动生成的方法存根
		String eId = req.geteId();
		String funcNO = req.getRequest().getFuncNo();

		try 
		{
			//DCP_DING_FUNC
			DelBean db1 = new DelBean("DCP_DING_FUNC");
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db1.addCondition("FUNCNO", new DataValue(funcNO, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));
			
			//DCP_DING_FUNC_SHOP
			DelBean db2 = new DelBean("DCP_DING_FUNC_SHOP");
			db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db2.addCondition("FUNCNO", new DataValue(funcNO, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db2));
			
			//DCP_DING_FUNC_SHOP_APPROVEDBY
			DelBean db3 = new DelBean("DCP_DING_FUNC_SHOP_APPROVEDBY");
			db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db3.addCondition("FUNCNO", new DataValue(funcNO, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db3));
			
			
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
	protected List<InsBean> prepareInsertData(DCP_DingFuncDeleteReq req) throws Exception {
	// TODO 自动生成的方法存根
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_DingFuncDeleteReq req) throws Exception {
	// TODO 自动生成的方法存根
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_DingFuncDeleteReq req) throws Exception {
	// TODO 自动生成的方法存根
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_DingFuncDeleteReq req) throws Exception {
	// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String funcNO = req.getRequest().getFuncNo();
		if (Check.Null(funcNO)) 
		{
			errMsg.append("功能编号不可为空值, ");
			isFail = true;
		}
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_DingFuncDeleteReq> getRequestType() {
	// TODO 自动生成的方法存根
	return new TypeToken<DCP_DingFuncDeleteReq>(){};
	}

	@Override
	protected DCP_DingFuncDeleteRes getResponseType() {
	// TODO 自动生成的方法存根
	return new DCP_DingFuncDeleteRes();
	}

}
