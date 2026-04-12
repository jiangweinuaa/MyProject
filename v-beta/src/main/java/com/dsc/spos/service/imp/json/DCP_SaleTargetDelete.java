package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_SaleTargetDeleteReq;
import com.dsc.spos.json.cust.res.DCP_SaleTargetDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.google.gson.reflect.TypeToken;

public class DCP_SaleTargetDelete extends SPosAdvanceService<DCP_SaleTargetDeleteReq, DCP_SaleTargetDeleteRes>
{
	@Override
	protected void processDUID(DCP_SaleTargetDeleteReq req, DCP_SaleTargetDeleteRes res) throws Exception {
		// TODO Auto-generated method stub
		try 
		{
			for (DCP_SaleTargetDeleteReq.level1Elm map : req.getRequest().getDatas())
			{
				DelBean db1 = new DelBean("DCP_SALETARGET_SHOP");
				db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
				db1.addCondition("SHOPID", new DataValue(map.getShopId(), Types.VARCHAR));
				db1.addCondition("SALETYPE", new DataValue(map.getSaleType(), Types.VARCHAR));
				db1.addCondition("DATETYPE", new DataValue(map.getDateType(), Types.VARCHAR));
				db1.addCondition("DATEVALUE", new DataValue(map.getDateValue(), Types.VARCHAR));

				this.addProcessData(new DataProcessBean(db1));
				
				this.doExecuteDataToDB();
				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");
			}
		} catch (Exception e) {
			// TODO: handle exception
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_SaleTargetDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_SaleTargetDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_SaleTargetDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_SaleTargetDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_SaleTargetDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_SaleTargetDeleteReq>() {};
	}

	@Override
	protected DCP_SaleTargetDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_SaleTargetDeleteRes();
	}

}
