package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OTakeClassShopUpdateReq;
import com.dsc.spos.json.cust.res.DCP_OTakeClassShopUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.google.gson.reflect.TypeToken;

public class DCP_OTakeClassShopUpdate extends SPosAdvanceService<DCP_OTakeClassShopUpdateReq, DCP_OTakeClassShopUpdateRes>
{
	@Override
	protected void processDUID(DCP_OTakeClassShopUpdateReq req, DCP_OTakeClassShopUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
		try
		{
			for (DCP_OTakeClassShopUpdateReq.level1Elm map : req.getDatas()) 
			{			
				//更新OC_MAPPINGCATEGORY
				UptBean ub = null;	
				ub = new UptBean("OC_MAPPINGCATEGORY");
				//add Value
				ub.addUpdateValue("CATEGORYNO", new DataValue(map.getClassNO(), Types.VARCHAR));
				ub.addUpdateValue("CATEGORYNAME", new DataValue(map.getClassName(), Types.VARCHAR));
				//condition
				ub.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
				ub.addCondition("SHOPID", new DataValue(map.getErpShopNO(), Types.VARCHAR));
				ub.addCondition("ORDER_CATEGORYNO", new DataValue(map.getOrderClassNO(), Types.VARCHAR));
				ub.addCondition("LOAD_DOCTYPE", new DataValue(req.getDocType(), Types.VARCHAR));
				this.addProcessData(new DataProcessBean(ub));
			}

			this.doExecuteDataToDB();

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		}
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OTakeClassShopUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OTakeClassShopUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OTakeClassShopUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OTakeClassShopUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_OTakeClassShopUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OTakeClassShopUpdateReq>(){};
	}

	@Override
	protected DCP_OTakeClassShopUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OTakeClassShopUpdateRes();
	}

}
