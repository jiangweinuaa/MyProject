package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.UUID;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_RegisterCreateReq;
import com.dsc.spos.json.cust.res.DCP_RegisterCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.google.gson.reflect.TypeToken;

public class DCP_RegisterCreate extends SPosAdvanceService<DCP_RegisterCreateReq, DCP_RegisterCreateRes>
{

	@Override
	protected void processDUID(DCP_RegisterCreateReq req, DCP_RegisterCreateRes res) throws Exception {
	// TODO Auto-generated method stub
		//生成一个注册的GUID号
		String nguid=UUID.randomUUID().toString();
		
		String[] colname={"CustomerNo","TerminalLicence","IsRegister","MEMO"};
		DataValue[] insValue={new DataValue(req.getCustomerNo(), Types.VARCHAR)
				,new DataValue(nguid, Types.VARCHAR)
				,new DataValue("N", Types.VARCHAR)
				,new DataValue(req.getMemo(), Types.VARCHAR)};
		InsBean ins=new InsBean("Platform_SregisterHead", colname);
		ins.addValues(insValue);
		this.pData.add(new DataProcessBean(ins));
		for (DCP_RegisterCreateReq.level1Elm registerdetail : req.getDatas()) 
		{
			int scount=Integer.parseInt(registerdetail.getScount());
			if(scount==0)
			{
				scount=1;
			}
			
			String[] colname1={"CustomerNo","TerminalLicence","RegisterType","Producttype","Scount","Bdate","Edate","MEMO"};
			DataValue[] insValue1={new DataValue(req.getCustomerNo(), Types.VARCHAR)
					,new DataValue(nguid, Types.VARCHAR)
					,new DataValue(registerdetail.getRegisterType(), Types.VARCHAR)
					,new DataValue(registerdetail.getProducttype(), Types.VARCHAR)
					,new DataValue(scount, Types.INTEGER)
					,new DataValue(registerdetail.getBdate(), Types.VARCHAR)
					,new DataValue(registerdetail.getEdate(), Types.VARCHAR)
					,new DataValue(registerdetail.getMemo(), Types.VARCHAR)};
			InsBean ins1=new InsBean("Platform_SregisterDetail", colname1);
			ins1.addValues(insValue1);
			this.pData.add(new DataProcessBean(ins1));
			
			if(registerdetail.getSDetailinfo()==null)
			{
				continue;
			}
			
			for (DCP_RegisterCreateReq.level2Elm sdinfo : registerdetail.getSDetailinfo()) 
			{
				String[] colname2={"CustomerNo","TerminalLicence","RegisterType","Producttype","Scount","Bdate","Edate","MEMO"
						,"SDetailType","SDetailmodular"};
				DataValue[] insValue2={new DataValue(req.getCustomerNo(), Types.VARCHAR)
						,new DataValue(nguid, Types.VARCHAR)
						,new DataValue(registerdetail.getRegisterType(), Types.VARCHAR)
						,new DataValue(registerdetail.getProducttype(), Types.VARCHAR)
						,new DataValue(Integer.parseInt(sdinfo.getScount()), Types.INTEGER)
						,new DataValue(registerdetail.getBdate(), Types.VARCHAR)
						,new DataValue(registerdetail.getEdate(), Types.VARCHAR)
						,new DataValue(registerdetail.getMemo(), Types.VARCHAR)
						,new DataValue(sdinfo.getSDetailType(), Types.VARCHAR)
						,new DataValue(sdinfo.getSDetailmodular(), Types.VARCHAR)};
				InsBean ins2=new InsBean("Platform_SDetailinfo", colname2);
				ins1.addValues(insValue2);
				this.pData.add(new DataProcessBean(ins2));
			}
			
	  }
		res.setTerminalLicence(nguid);
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_RegisterCreateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_RegisterCreateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_RegisterCreateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_RegisterCreateReq req) throws Exception {
	// TODO Auto-generated method stub
	return false;
	}

	@Override
	protected TypeToken<DCP_RegisterCreateReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_RegisterCreateReq>(){};
	}

	@Override
	protected DCP_RegisterCreateRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_RegisterCreateRes();
	}

}
