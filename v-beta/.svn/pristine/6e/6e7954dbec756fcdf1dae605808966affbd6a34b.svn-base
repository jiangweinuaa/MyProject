package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_TGCommissionDeleteReq;
import com.dsc.spos.json.cust.res.DCP_TGCommissionDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 服務函數：TGCommissionDeleteDCP
 * 服务说明：团务拆账删除
 * @author jinzma 
 * @since  2019-02-12
 */
public class DCP_TGCommissionDelete extends SPosAdvanceService <DCP_TGCommissionDeleteReq,DCP_TGCommissionDeleteRes > {

	@Override
	protected void processDUID(DCP_TGCommissionDeleteReq req, DCP_TGCommissionDeleteRes res) throws Exception {
		// TODO 自动生成的方法存根
		String eId = req.geteId();
		String travelNO = req.getTravelNO();
		String tgCategoryNO =req.getTgCategoryNO();
		try 
		{
		  //DCP_TGCOMMISSION
			DelBean db1 = new DelBean("DCP_TGCOMMISSION");
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db1.addCondition("TRAVELNO", new DataValue(travelNO, Types.VARCHAR));
			db1.addCondition("TGCATEGORYNO", new DataValue(tgCategoryNO, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));
		  
			//DCP_TGCOMMISSION_DETAIL
			DelBean db2 = new DelBean("DCP_TGCOMMISSION_DETAIL");
			db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db2.addCondition("TRAVELNO", new DataValue(travelNO, Types.VARCHAR));
			db2.addCondition("TGCATEGORYNO", new DataValue(tgCategoryNO, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db2));
			
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
	protected List<InsBean> prepareInsertData(DCP_TGCommissionDeleteReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_TGCommissionDeleteReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_TGCommissionDeleteReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_TGCommissionDeleteReq req) throws Exception {
		// TODO 自动生成的方法存根

		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if (Check.Null(req.getTravelNO()) ) 
		{
			errMsg.append("旅行社编号不可为空值, ");
			isFail = true;
		}
		if (Check.Null(req.getTgCategoryNO()) ) 
		{
			errMsg.append("分类编号不可为空值, ");
			isFail = true;
		}
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;	
	}

	@Override
	protected TypeToken<DCP_TGCommissionDeleteReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_TGCommissionDeleteReq>(){};
	}

	@Override
	protected DCP_TGCommissionDeleteRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_TGCommissionDeleteRes();
	}


}
