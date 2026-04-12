package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_SaleTargetCreateReq;
import com.dsc.spos.json.cust.res.DCP_SaleTargetCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.google.gson.reflect.TypeToken;

public class DCP_SaleTargetCreate extends SPosAdvanceService<DCP_SaleTargetCreateReq, DCP_SaleTargetCreateRes>
{
	@Override
	protected void processDUID(DCP_SaleTargetCreateReq req, DCP_SaleTargetCreateRes res) throws Exception 
	{
		// TODO Auto-generated method stub
		try 
		{
			for (DCP_SaleTargetCreateReq.level1Elm map : req.getRequest().getDatas()) 
			{
				DelBean db1 = new DelBean("DCP_SALETARGET_SHOP");
				db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
				db1.addCondition("SHOPID", new DataValue(map.getShopId(), Types.VARCHAR));
				db1.addCondition("SALETYPE", new DataValue(map.getSaleType(), Types.VARCHAR));
				db1.addCondition("DATETYPE", new DataValue(map.getDateType(), Types.VARCHAR));
				db1.addCondition("DATEVALUE", new DataValue(map.getDateValue(), Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));

				String[] columns2 = {"EID","SHOPID","SALETYPE","DATETYPE","DATEVALUE","SALEAMT","STATUS"};
				DataValue[] insValue2 = null;
				insValue2 = new DataValue[] 
						{
								new DataValue(req.geteId(), Types.VARCHAR),
								new DataValue(map.getShopId(), Types.VARCHAR),
								new DataValue(map.getSaleType(), Types.VARCHAR)
								,new DataValue(map.getDateType(), Types.VARCHAR)
								,new DataValue(map.getDateValue(), Types.VARCHAR)
								,new DataValue(map.getSaleAMT(), Types.VARCHAR)
								,new DataValue("100", Types.VARCHAR)
						};
				InsBean ib2 = new InsBean("DCP_SALETARGET_SHOP", columns2);
				ib2.addValues(insValue2);
				this.addProcessData(new DataProcessBean(ib2)); // 新增单身
			}

			this.doExecuteDataToDB();
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");

		} 
		catch (Exception e) {
			// TODO: handle exception
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_SaleTargetCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_SaleTargetCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_SaleTargetCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_SaleTargetCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_SaleTargetCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_SaleTargetCreateReq>() {};
	}

	@Override
	protected DCP_SaleTargetCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_SaleTargetCreateRes();
	}

}
