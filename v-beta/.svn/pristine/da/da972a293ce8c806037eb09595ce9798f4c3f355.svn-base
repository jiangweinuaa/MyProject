package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CRegisterCreateReq;
import com.dsc.spos.json.cust.res.DCP_CRegisterCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_CRegisterCreate extends SPosAdvanceService<DCP_CRegisterCreateReq, DCP_CRegisterCreateRes>
{

	@Override
	protected void processDUID(DCP_CRegisterCreateReq req, DCP_CRegisterCreateRes res) throws Exception {
		// TODO Auto-generated method stub
		String sql="select * from Platform_CregisterDetail where Producttype='"+req.getRequest().getProducttype()+"' and TerminalLicence='"+req.getRequest().getTerminalLicence()+"' and IsRegister='N' "
				+ "  and SHOPID is null and machine is null  ";

		List<Map<String, Object>> liscanmachine=this.doQueryData(sql, null);
		if(liscanmachine!=null&&!liscanmachine.isEmpty())
		{
			if(liscanmachine.size()<req.getRequest().getDatas().size())
			{
				res.setSuccess(false);
				res.setServiceDescription("注册数量不足，剩余注册数:"+liscanmachine.size()+" 请求数:"+req.getRequest().getDatas().size());
				return;
			}
		}
		//开始注册进来
		int i=0;
		String isregsql="select * from Platform_CregisterDetail where Producttype='"+req.getRequest().getProducttype()+"'  "
				+ " and EID is not null and SHOPID is not null and machine is not null  ";
		if(req.getRequest().getProducttype().equals("1"))
		{
			isregsql="select * from Platform_CregisterDetail where Producttype='"+req.getRequest().getProducttype()+"'  "
					+ " and EID is not null and SHOPID is not null   ";
		}

		List<Map<String, Object>> isregsqldate=this.doQueryData(isregsql, null);

		for (DCP_CRegisterCreateReq.level1Elm map : req.getRequest().getDatas()) 
		{
			if(isregsqldate!=null&&!isregsqldate.isEmpty())
			{
				String rEId = map.getrEId();
				String rShopId = map.getrShopId();
				String rmachine = map.getRmachine();	

				if (Check.Null(rEId))
					rEId="";
				if (Check.Null(rShopId))
					rShopId="";
				if (Check.Null(rmachine))
					rmachine="";


				for (Map<String, Object> mapdate : isregsqldate) 
				{
					if(rEId.equals(mapdate.get("EID").toString())&&rShopId.equals(mapdate.get("SHOPID").toString())&&rmachine.equals(mapdate.get("MACHINE").toString()))
					{
						res.setSuccess(false);
						res.setServiceDescription("门店号:"+map.getrShopId()+"机台号："+map.getRmachine()+"已存在！");
						return;
					}
				}
			}

			UptBean up=new UptBean("Platform_CregisterDetail");
			up.addUpdateValue("EID", new DataValue(map.getrEId(), Types.VARCHAR));
			up.addUpdateValue("SHOPID", new DataValue(map.getrShopId(), Types.VARCHAR));
			up.addUpdateValue("machine", new DataValue(map.getRmachine(), Types.VARCHAR));
			up.addUpdateValue("IsRegister", new DataValue("N", Types.VARCHAR));

			up.addCondition("MachineCode", new DataValue(liscanmachine.get(i).get("MACHINECODE").toString(), Types.VARCHAR));
			up.addCondition("Producttype", new DataValue(req.getRequest().getProducttype(), Types.VARCHAR));
			this.pData.add(new DataProcessBean(up));
			i++;

		}
		this.doExecuteDataToDB();

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_CRegisterCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_CRegisterCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_CRegisterCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_CRegisterCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_CRegisterCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_CRegisterCreateReq>(){};
	}

	@Override
	protected DCP_CRegisterCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_CRegisterCreateRes();
	}

}

