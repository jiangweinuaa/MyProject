package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_BasicSetDeleteReq;
import com.dsc.spos.json.cust.res.DCP_BasicSetDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.google.gson.reflect.TypeToken;

/**
 * 基础设置新增
 * @author yuanyy 2020-03-03
 *
 */
public class DCP_BasicSetDelete extends SPosAdvanceService<DCP_BasicSetDeleteReq, DCP_BasicSetDeleteRes> {

	@Override
	protected void processDUID(DCP_BasicSetDeleteReq req, DCP_BasicSetDeleteRes res) throws Exception {
		// TODO Auto-generated method stub
		String eId = req.geteId();
		try {
			String templateNo = req.getRequest().getTemplateNo();

			DelBean db1 = new DelBean("DCP_PADGUIDE_BASESET_PICKSHOP");
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db1.addCondition("TEMPLATENO", new DataValue(templateNo, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));
			
			DelBean db2 = new DelBean("DCP_PADGUIDE_BASESET");
			db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db2.addCondition("TEMPLATENO", new DataValue(templateNo, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db2));
			
			this.doExecuteDataToDB();
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setServiceDescription("服务执行失败！");
			res.setSuccess(false);
			res.setServiceStatus("200");
		}
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_BasicSetDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_BasicSetDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_BasicSetDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_BasicSetDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_BasicSetDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_BasicSetDeleteReq>(){};
	}

	@Override
	protected DCP_BasicSetDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_BasicSetDeleteRes();
	}

	
}
