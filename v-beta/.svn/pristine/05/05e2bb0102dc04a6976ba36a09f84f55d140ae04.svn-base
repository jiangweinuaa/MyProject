package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CRegisterDeleteReq;
import com.dsc.spos.json.cust.res.DCP_CRegisterDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.google.gson.reflect.TypeToken;

public class DCP_CRegisterDelete extends SPosAdvanceService<DCP_CRegisterDeleteReq, DCP_CRegisterDeleteRes>
{
	@Override
	protected void processDUID(DCP_CRegisterDeleteReq req, DCP_CRegisterDeleteRes res) throws Exception {
	// TODO Auto-generated method stub
		//通过机器码去做更新失效的动作
		//开始删除
		for (DCP_CRegisterDeleteReq.level1Elm lv1list : req.getRequest().getDatas()) 
		{
			UptBean up=new UptBean("Platform_CregisterDetail");
			up.addUpdateValue("IsRegister", new DataValue("N", Types.VARCHAR));
			if(req.getRequest().getOpType()!=null&&req.getRequest().getOpType().equals("2"))
			{}
			else
			{
				up.addUpdateValue("EID", new DataValue("", Types.VARCHAR));
				up.addUpdateValue("SHOPID", new DataValue("", Types.VARCHAR));
				up.addUpdateValue("machine", new DataValue("", Types.VARCHAR));
			}
			//up.addCondition("MachineCode", new DataValue(lv1list.getMachincode(), Types.VARCHAR));
			up.addCondition("Producttype", new DataValue(req.getRequest().getProducttype(), Types.VARCHAR));
			up.addCondition("EID", new DataValue(lv1list.getrEId(), Types.VARCHAR));
			up.addCondition("SHOPID", new DataValue(lv1list.getrShopId(), Types.VARCHAR));
			//只有POS会传机台
			if(req.getRequest().getProducttype().equals("3"))
			{
			  up.addCondition("machine", new DataValue(lv1list.getRmachine(), Types.VARCHAR));
			}
			
			this.pData.add(new DataProcessBean(up));
			this.doExecuteDataToDB();		
	  }
		
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_CRegisterDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_CRegisterDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_CRegisterDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_CRegisterDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return false;
	}

	@Override
	protected TypeToken<DCP_CRegisterDeleteReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_CRegisterDeleteReq>(){};
	}

	@Override
	protected DCP_CRegisterDeleteRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_CRegisterDeleteRes();
	}
	
}

