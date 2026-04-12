package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_SupPriceTemplateDeleteReq;
import com.dsc.spos.json.cust.res.DCP_SupPriceTemplateDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_SupPriceTemplateDelete extends SPosAdvanceService<DCP_SupPriceTemplateDeleteReq,DCP_SupPriceTemplateDeleteRes> {

	@Override
	protected void processDUID(DCP_SupPriceTemplateDeleteReq req, DCP_SupPriceTemplateDeleteRes res) throws Exception {
		String eId = req.geteId();
		for (DCP_SupPriceTemplateDeleteReq.Template par : req.getRequest().getTemplateList()) {
			String templateId = par.getTemplateId();
			//只删除 状态：-1未启用
			String sqlStatus="select status from DCP_SUPPRICETEMPLATE where eid='"+eId+"' and TEMPLATEID='"+templateId+"' and status=-1 ";
			List<Map<String , Object>> getData=this.doQueryData(sqlStatus, null);
			
			if (getData!=null && !getData.isEmpty()) {
				//
				DelBean db1 = new DelBean("DCP_SUPPRICETEMPLATE");
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db1.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));
				
				//
				db1 = new DelBean("DCP_SUPPRICETEMPLATE_LANG");
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db1.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));
				
				db1 = new DelBean("DCP_SUPPRICETEMPLATE_RANGE");
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db1.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));
				
				db1 = new DelBean("DCP_SUPPRICETEMPLATE_PRICE");
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db1.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));
			}else{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E422,"模板:"+templateId+" 状态必须是未启用才可以删除");
			}
			
		}

		
		this.doExecuteDataToDB();

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_SupPriceTemplateDeleteReq req) throws Exception {
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_SupPriceTemplateDeleteReq req) throws Exception {
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_SupPriceTemplateDeleteReq req) throws Exception {
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_SupPriceTemplateDeleteReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();

		if(req.getRequest()==null) {
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		for (DCP_SupPriceTemplateDeleteReq.Template par : req.getRequest().getTemplateList()) {
			String templateId = par.getTemplateId();
			if(Check.Null(templateId)) {
				errMsg.append("模板编码不能为空值 ");
				isFail = true;
			}		
		}

		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return false;
	}

	@Override
	protected TypeToken<DCP_SupPriceTemplateDeleteReq> getRequestType() {
		return new TypeToken<DCP_SupPriceTemplateDeleteReq>(){};
	}

	@Override
	protected DCP_SupPriceTemplateDeleteRes getResponseType() {
		return new DCP_SupPriceTemplateDeleteRes();
	}



}
