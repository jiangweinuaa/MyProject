package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_DingTemplateDeleteReq;
import com.dsc.spos.json.cust.res.DCP_DingTemplateDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：DingTemplateDeleteDCP
 * 服务说明： 钉钉审批模板删除
 * @author jinzma
 * @since  2019-10-31
 */
public class DCP_DingTemplateDelete extends SPosAdvanceService<DCP_DingTemplateDeleteReq,DCP_DingTemplateDeleteRes>{

	@Override
	protected void processDUID(DCP_DingTemplateDeleteReq req, DCP_DingTemplateDeleteRes res) throws Exception {
		// TODO 自动生成的方法存根
		String eId = req.geteId();
		String templateNO =req.getRequest().getTemplateNo();
		try
		{
			//DCP_DING_TEMPLATE
			DelBean db = new DelBean("DCP_DING_TEMPLATE");
			db.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db.addCondition("TEMPLATENO", new DataValue(templateNO, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db));

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
	protected List<InsBean> prepareInsertData(DCP_DingTemplateDeleteReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_DingTemplateDeleteReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_DingTemplateDeleteReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_DingTemplateDeleteReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		if (Check.Null(req.getRequest().getTemplateNo())) 
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
	protected TypeToken<DCP_DingTemplateDeleteReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_DingTemplateDeleteReq>(){};
	}

	@Override
	protected DCP_DingTemplateDeleteRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_DingTemplateDeleteRes();
	}

}
