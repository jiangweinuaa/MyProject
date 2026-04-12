package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_GoodsUnitConvertUpdateReq;
import com.dsc.spos.json.cust.res.DCP_GoodsUnitConvertUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsUnitConvertUpdate extends SPosAdvanceService<DCP_GoodsUnitConvertUpdateReq, DCP_GoodsUnitConvertUpdateRes> {

	@Override
	protected void processDUID(DCP_GoodsUnitConvertUpdateReq req, DCP_GoodsUnitConvertUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
		try {

			String eId = req.geteId();
			String pluNO = req.getRequest().getPluNo();
			String ounit = req.getRequest().getoUnit();
			String unit = req.getRequest().getUnit();
			String oqty = req.getRequest().getOqty();
			String qty = req.getRequest().getQty();
			String status = req.getRequest().getStatus();

			UptBean ub1 = null;	
			ub1 = new UptBean("DCP_UNITCONVERT_GOODS");
			//add Value
			ub1.addUpdateValue("OQTY", new DataValue(oqty, Types.VARCHAR));
			ub1.addUpdateValue("QTY", new DataValue(qty, Types.VARCHAR));
			ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
			//condition
			ub1.addCondition("PLUNO", new DataValue(pluNO, Types.VARCHAR));
			ub1.addCondition("OUNIT", new DataValue(ounit, Types.VARCHAR));
			ub1.addCondition("UNIT", new DataValue(unit, Types.VARCHAR));
			ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));		
			this.addProcessData(new DataProcessBean(ub1));
			
			this.doExecuteDataToDB();
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_GoodsUnitConvertUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_GoodsUnitConvertUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_GoodsUnitConvertUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_GoodsUnitConvertUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		String pluNO = req.getRequest().getPluNo();
		String ounit = req.getRequest().getoUnit();
		String unit = req.getRequest().getUnit();

		if(Check.Null(pluNO)){
			errMsg.append("商品编码不能为空值 ");
			isFail = true;
		}
		if(Check.Null(ounit)){
			errMsg.append("来源单位不能为空值 ");
			isFail = true;
		}
		if(Check.Null(unit)){
			errMsg.append("目标单位不能为空值 ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_GoodsUnitConvertUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_GoodsUnitConvertUpdateReq>(){};
	}

	@Override
	protected DCP_GoodsUnitConvertUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_GoodsUnitConvertUpdateRes();
	}


}
