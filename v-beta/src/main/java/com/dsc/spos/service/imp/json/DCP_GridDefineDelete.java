package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_GridDefineDeleteReq;
import com.dsc.spos.json.cust.res.DCP_GridDefineDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.google.gson.reflect.TypeToken;

/**
 * 表格自定义删除
 * @author yuanyy 2019-02-16
 *
 */
public class DCP_GridDefineDelete extends SPosAdvanceService<DCP_GridDefineDeleteReq, DCP_GridDefineDeleteRes> {

	@Override
	protected void processDUID(DCP_GridDefineDeleteReq req, DCP_GridDefineDeleteRes res) throws Exception {
		// TODO Auto-generated method stub
		
		try {
			
			String EID = req.geteId();
			String modularNo = req.getRequest().getModularNo();
			
			DelBean db1 = new DelBean("DCP_GRIDDEFINE");
			db1.addCondition("EID", new DataValue(EID, Types.VARCHAR));
			db1.addCondition("MODULARNO", new DataValue(modularNo, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));
			
			this.doExecuteDataToDB();
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败！");
		}
		
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_GridDefineDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_GridDefineDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_GridDefineDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_GridDefineDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_GridDefineDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_GridDefineDeleteReq>(){};
	}

	@Override
	protected DCP_GridDefineDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_GridDefineDeleteRes();
	}

}
