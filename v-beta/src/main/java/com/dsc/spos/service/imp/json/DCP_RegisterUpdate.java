package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_RegisterCreateReq;
import com.dsc.spos.json.cust.req.DCP_RegisterUpdateReq;
import com.dsc.spos.json.cust.res.DCP_RegisterUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.google.gson.reflect.TypeToken;

public class DCP_RegisterUpdate extends SPosAdvanceService<DCP_RegisterUpdateReq, DCP_RegisterUpdateRes>
{

	@Override
	protected void processDUID(DCP_RegisterUpdateReq req, DCP_RegisterUpdateRes res) throws Exception {
	// TODO Auto-generated method stub
	//生成一个注册的GUID号
			String nguid=req.getTerminalLicence();	
			String sql="select * from Platform_SregisterHead where TerminalLicence='"+req.getTerminalLicence()+"' ";
			List<Map<String, Object>> listhead=this.doQueryData(sql, null);
			if(listhead!=null&&!listhead.isEmpty())
			{
				String isregister=listhead.get(0).get("ISREGISTER").toString();
				if(isregister.equals("Y"))
				{
					res.setSuccess(false);
					res.setServiceDescription("该注册号已经使用，不能修改！");
					return;
				}
			}
			
			//先删除Platform_SregisterHead表与Platform_SregisterDetail表
			DelBean del1=new DelBean("Platform_SregisterHead");
			del1.addCondition("TerminalLicence", new DataValue(nguid, Types.VARCHAR));
			this.pData.add(new DataProcessBean(del1));
			
			DelBean del2=new DelBean("Platform_SregisterDetail");
			del2.addCondition("TerminalLicence", new DataValue(nguid, Types.VARCHAR));
			this.pData.add(new DataProcessBean(del2));
			
			String[] colname={"CustomerNo","TerminalLicence","IsRegister","MEMO"};
			DataValue[] insValue={new DataValue(req.getCustomerNo(), Types.VARCHAR)
					,new DataValue(nguid, Types.VARCHAR)
					,new DataValue("N", Types.VARCHAR)
					,new DataValue(req.getMemo(), Types.VARCHAR)};
			InsBean ins=new InsBean("Platform_SregisterHead", colname);
			ins.addValues(insValue);
			this.pData.add(new DataProcessBean(ins));
			for (DCP_RegisterUpdateReq.level1Elm registerdetail : req.getDatas()) 
			{
				String[] colname1={"CustomerNo","TerminalLicence","RegisterType","Producttype","Scount","Bdate","Edate","MEMO"};
				DataValue[] insValue1={new DataValue(req.getCustomerNo(), Types.VARCHAR)
						,new DataValue(nguid, Types.VARCHAR)
						,new DataValue(registerdetail.getRegisterType(), Types.VARCHAR)
						,new DataValue(registerdetail.getProducttype(), Types.VARCHAR)
						,new DataValue(Integer.parseInt(registerdetail.getScount()), Types.INTEGER)
						,new DataValue(registerdetail.getBdate(), Types.VARCHAR)
						,new DataValue(registerdetail.getEdate(), Types.VARCHAR)
						,new DataValue(registerdetail.getMemo(), Types.VARCHAR)};
				InsBean ins1=new InsBean("Platform_SregisterDetail", colname1);
				ins1.addValues(insValue1);
				this.pData.add(new DataProcessBean(ins1));
		  }
			
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_RegisterUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_RegisterUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_RegisterUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_RegisterUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return false;
	}

	@Override
	protected TypeToken<DCP_RegisterUpdateReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_RegisterUpdateReq>(){};
	}

	@Override
	protected DCP_RegisterUpdateRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_RegisterUpdateRes();
	}

}
