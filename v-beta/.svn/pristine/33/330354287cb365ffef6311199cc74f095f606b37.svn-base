package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_GoodsUnitConvertDeleteReq;
import com.dsc.spos.json.cust.res.DCP_GoodsUnitConvertDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 删除商品单位换算信息
 * @author yuanyy
 *
 */
public class DCP_GoodsUnitConvertDelete extends SPosAdvanceService<DCP_GoodsUnitConvertDeleteReq, DCP_GoodsUnitConvertDeleteRes> {

	@Override
	protected void processDUID(DCP_GoodsUnitConvertDeleteReq req, DCP_GoodsUnitConvertDeleteRes res) throws Exception {
		// TODO Auto-generated method stub
		try {

			String eId = req.geteId();
			String pluNO = req.getRequest().getPluNo();
			String ounit = req.getRequest().getoUnit();
			String unit = req.getRequest().getUnit();

			DelBean db1 = new DelBean("DCP_UNITCONVERT_GOODS");
			db1.addCondition("PLUNO", new DataValue(pluNO,Types.VARCHAR));
			db1.addCondition("OUNIT", new DataValue(ounit,Types.VARCHAR));
			db1.addCondition("UNIT", new DataValue(unit,Types.VARCHAR));
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));

			this.doExecuteDataToDB();	
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");

		} catch (Exception e) {
			// TODO: handle exception
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_GoodsUnitConvertDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_GoodsUnitConvertDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_GoodsUnitConvertDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_GoodsUnitConvertDeleteReq req) throws Exception {
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
	protected TypeToken<DCP_GoodsUnitConvertDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_GoodsUnitConvertDeleteReq>(){};
	}

	@Override
	protected DCP_GoodsUnitConvertDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_GoodsUnitConvertDeleteRes();
	}


}
