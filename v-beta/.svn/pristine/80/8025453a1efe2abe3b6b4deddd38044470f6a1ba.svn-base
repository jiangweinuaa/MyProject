package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_RCustomerCreateReq;
import com.dsc.spos.json.cust.res.DCP_RCustomerCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.google.gson.reflect.TypeToken;

public class DCP_RCustomerCreate extends SPosAdvanceService<DCP_RCustomerCreateReq, DCP_RCustomerCreateRes>
{
	
	@Override
	protected void processDUID(DCP_RCustomerCreateReq req, DCP_RCustomerCreateRes res) throws Exception {
	// TODO Auto-generated method stub
		//客户号不取前台的，后台自己生成一个
		String result="";
		while(true)
		{
			Random random = new Random();
			for (int i=0;i<10;i++)
			{
				result+=random.nextInt(10);
			}
			String sql="select * from Platform_RCustomer where CustomerNo='"+result+"' ";
			List<Map<String, Object>> list=this.doQueryData(sql, null);
			if(list==null||list.isEmpty())
			{
			  break;
			}
		}
		
		String[] colname={"CustomerNo","CustomerName","MEMO"};
		
		DataValue[] insValue={new DataValue(result, Types.VARCHAR)
				,new DataValue(req.getCustomerName(), Types.VARCHAR)
				,new DataValue(req.getMemo(), Types.VARCHAR)};
		InsBean ins=new InsBean("Platform_RCustomer", colname);
		ins.addValues(insValue);
		this.pData.add(new DataProcessBean(ins));
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_RCustomerCreateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_RCustomerCreateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_RCustomerCreateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_RCustomerCreateReq req) throws Exception {
	// TODO Auto-generated method stub
	return false;
	}

	@Override
	protected TypeToken<DCP_RCustomerCreateReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_RCustomerCreateReq>(){};
	}

	@Override
	protected DCP_RCustomerCreateRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_RCustomerCreateRes();
	}

}


