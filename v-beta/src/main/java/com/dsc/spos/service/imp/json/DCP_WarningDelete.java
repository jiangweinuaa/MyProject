package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_WarningDeleteReq;
import com.dsc.spos.json.cust.req.DCP_WarningDeleteReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_WarningDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.google.gson.reflect.TypeToken;

public class DCP_WarningDelete extends SPosAdvanceService<DCP_WarningDeleteReq,DCP_WarningDeleteRes> {

	@Override
	protected void processDUID(DCP_WarningDeleteReq req, DCP_WarningDeleteRes res) throws Exception {
	// TODO Auto-generated method stub
		String eId = req.geteId();
		level1Elm requestModel =	req.getRequest();
		String billNo = requestModel.getBillNo();
		//判断下有没有生成过日志
		String sql =" select * from (select count(*) as num from DCP_Warninglog where eid='"+eId+"' and billno='"+billNo+"' )";
		
		List<Map<String, Object>> getQDate = this.doQueryData(sql, null);
		if(getQDate!=null&&getQDate.isEmpty()==false)
		{
			int num = 0;
			try 
			{
				num = Integer.parseInt(getQDate.get(0).get("NUM").toString());
		
			} 
			catch (Exception e) 
			{
		// TODO: handle exception
		
			}
			
			if(num>0)
			{
				res.setSuccess(false);
				res.setServiceStatus("100");
				res.setServiceDescription("该监控编号已监控到异常，不允许删除！");
				return;
				
			}
			
		}
		
		DelBean db1 = null;	
		db1 = new DelBean("DCP_Warning");		
	  // condition
		db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		db1.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
		
		this.addProcessData(new DataProcessBean(db1));
		
	  //推送门店
		DelBean del_warning_pushShop = new DelBean("DCP_WARNING_PICKSHOP");
		del_warning_pushShop.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		del_warning_pushShop.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));	
		this.addProcessData(new DataProcessBean(del_warning_pushShop));
		
		//推送人
		DelBean del_warning_pushMan = new DelBean("DCP_WARNING_PUSHMAN");
		del_warning_pushMan.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		del_warning_pushMan.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));		
		this.addProcessData(new DataProcessBean(del_warning_pushMan));
		
		//推送 方式
		DelBean del_warning_pushWay = new DelBean("DCP_WARNING_PUSHWAY");
		del_warning_pushWay.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		del_warning_pushWay.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));		
		this.addProcessData(new DataProcessBean(del_warning_pushWay));
		
					
		this.doExecuteDataToDB();
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");
	
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_WarningDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_WarningDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_WarningDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_WarningDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return false;
	}

	@Override
	protected TypeToken<DCP_WarningDeleteReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_WarningDeleteReq>(){};
	}

	@Override
	protected DCP_WarningDeleteRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_WarningDeleteRes();
	}

}
