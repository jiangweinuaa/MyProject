package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderStatusLogCreateReq;
import com.dsc.spos.json.cust.req.DCP_OrgOrderUpdateReq;
import com.dsc.spos.json.cust.res.DCP_OrgOrderUpdateRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.waimai.HelpTools;
import com.google.gson.reflect.TypeToken;

public class DCP_OrgOrderUpdate extends SPosAdvanceService<DCP_OrgOrderUpdateReq, DCP_OrgOrderUpdateRes>
{

	@Override
	protected void processDUID(DCP_OrgOrderUpdateReq req, DCP_OrgOrderUpdateRes res) throws Exception {
	// TODO Auto-generated method stub
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");
		return;
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrgOrderUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrgOrderUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrgOrderUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrgOrderUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return false;
	}

	@Override
	protected TypeToken<DCP_OrgOrderUpdateReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_OrgOrderUpdateReq>(){};
	}

	@Override
	protected DCP_OrgOrderUpdateRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_OrgOrderUpdateRes();
	}

}
