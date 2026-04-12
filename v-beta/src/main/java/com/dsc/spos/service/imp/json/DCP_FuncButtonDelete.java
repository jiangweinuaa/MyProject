package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_FuncButtonDeleteReq;
import com.dsc.spos.json.cust.req.DCP_TGCommissionDeleteReq;
import com.dsc.spos.json.cust.res.DCP_FuncButtonDeleteRes;
import com.dsc.spos.json.cust.res.DCP_TGCommissionDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 服務函數：FuncButtonDeleteDCP
 * 服务说明：功能按键删除
 * @author jinzma 
 * @since  2019-02-20
 */
public class DCP_FuncButtonDelete  extends SPosAdvanceService<DCP_FuncButtonDeleteReq,DCP_FuncButtonDeleteRes> {

	@Override
	protected void processDUID(DCP_FuncButtonDeleteReq req, DCP_FuncButtonDeleteRes res) throws Exception {
	// TODO 自动生成的方法存根
		String eId = req.geteId();
		String ptemplateNO = req.getRequest().getPtemplateNo();
		try 
		{
		  //DCP_FUNCBUTTON
			DelBean db1 = new DelBean("DCP_FUNCBUTTON");
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db1.addCondition("PTEMPLATENO", new DataValue(ptemplateNO, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));
		  
			//DCP_FUNCBUTTON_SHOP
			DelBean db2 = new DelBean("DCP_FUNCBUTTON_SHOP");
			db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db2.addCondition("PTEMPLATENO", new DataValue(ptemplateNO, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db2));
			
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
	protected List<InsBean> prepareInsertData(DCP_FuncButtonDeleteReq req) throws Exception {
	// TODO 自动生成的方法存根
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_FuncButtonDeleteReq req) throws Exception {
	// TODO 自动生成的方法存根
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_FuncButtonDeleteReq req) throws Exception {
	// TODO 自动生成的方法存根
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_FuncButtonDeleteReq req) throws Exception {
	// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		if (Check.Null(req.getRequest().getPtemplateNo()) ) 
		{
			errMsg.append("模板编号不可为空值, ");
			isFail = true;
		}
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;	
	}

	@Override
	protected TypeToken<DCP_FuncButtonDeleteReq> getRequestType() {
	// TODO 自动生成的方法存根
		return new TypeToken<DCP_FuncButtonDeleteReq>(){};
	}

	@Override
	protected DCP_FuncButtonDeleteRes getResponseType() {
	// TODO 自动生成的方法存根
		return new DCP_FuncButtonDeleteRes();
	}

}
