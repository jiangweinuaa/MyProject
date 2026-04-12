package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ParaTemplateDeleteReq;
import com.dsc.spos.json.cust.res.DCP_ParaTemplateDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.google.gson.reflect.TypeToken;

/**
 * 参数模板删除
 * @author 2020-06-11
 *
 */
public class DCP_ParaTemplateDelete extends SPosAdvanceService<DCP_ParaTemplateDeleteReq, DCP_ParaTemplateDeleteRes> {

	@Override
	protected void processDUID(DCP_ParaTemplateDeleteReq req, DCP_ParaTemplateDeleteRes res) throws Exception {
		// TODO Auto-generated method stub
		
		try {
			String eId = req.geteId();
			String templateId = req.getRequest().getTemplateId();
			
			//PLATFORM_PARATEMPLATE
			DelBean db1 = new DelBean("PLATFORM_PARATEMPLATE");
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db1.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1)); // 删除
			
			DelBean db4 = new DelBean("PLATFORM_PARATEMPLATE_ITEM");
			db4.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db4.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db4));
			
			DelBean db2 = new DelBean("PLATFORM_PARATEMPLATE_SHOP");
			db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db2.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db2));
			
			DelBean db3 = new DelBean("PLATFORM_PARATEMPLATE_MACHINE");
			db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db3.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db3));
			
			this.doExecuteDataToDB();
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败"+e.getMessage());
			
		}
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_ParaTemplateDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_ParaTemplateDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_ParaTemplateDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_ParaTemplateDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_ParaTemplateDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_ParaTemplateDeleteReq> (){};
	}

	@Override
	protected DCP_ParaTemplateDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_ParaTemplateDeleteRes();
	}
	
}
