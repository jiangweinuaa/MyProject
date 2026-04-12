package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_RegisterDeleteReq;
import com.dsc.spos.json.cust.res.DCP_RegisterDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.google.gson.reflect.TypeToken;

public class DCP_RegisterDelete extends SPosAdvanceService<DCP_RegisterDeleteReq, DCP_RegisterDeleteRes>
{

	@Override
	protected void processDUID(DCP_RegisterDeleteReq req, DCP_RegisterDeleteRes res) throws Exception {
	// TODO Auto-generated method stub
	//查询注册号是否有被注册过，如果没有被注册是可以删除的
			String sql="select * from Platform_SregisterHead where TerminalLicence='"+req.getTerminalLicence()+"' ";
			List<Map<String, Object>> listhead=this.doQueryData(sql, null);
			if(listhead!=null&&!listhead.isEmpty())
			{
				String isregister=listhead.get(0).get("ISREGISTER").toString();
				if(isregister.equals("Y"))
				{
					res.setSuccess(false);
					res.setServiceDescription("该注册号已经使用，不能删除！");
					return;
				}
			}
			DelBean db1 = new DelBean("Platform_SregisterHead");
			db1.addCondition("TerminalLicence", new DataValue(req.getTerminalLicence(), Types.VARCHAR));
			//this.addProcessData(new DataProcessBean(db2));
			this.pData.add(new DataProcessBean(db1));
			
			DelBean db2 = new DelBean("Platform_SregisterDetail");
			db2.addCondition("TerminalLicence", new DataValue(req.getTerminalLicence(), Types.VARCHAR));
			//this.addProcessData(new DataProcessBean(db2));
			this.pData.add(new DataProcessBean(db2));
			
			DelBean db3 = new DelBean("Platform_SDetailinfo");
			db3.addCondition("TerminalLicence", new DataValue(req.getTerminalLicence(), Types.VARCHAR));
			//this.addProcessData(new DataProcessBean(db2));
			this.pData.add(new DataProcessBean(db3));
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_RegisterDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_RegisterDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_RegisterDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_RegisterDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return false;
	}

	@Override
	protected TypeToken<DCP_RegisterDeleteReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_RegisterDeleteReq>(){};
	}

	@Override
	protected DCP_RegisterDeleteRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_RegisterDeleteRes();
	}

}

