package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PFOrderDeleteReq;
import com.dsc.spos.json.cust.res.DCP_PFOrderDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
 
/**
 * 营业预估删除
 * @author yuanyy
 *
 */
public class DCP_PFOrderDelete extends SPosAdvanceService<DCP_PFOrderDeleteReq, DCP_PFOrderDeleteRes> {

	@Override
	protected void processDUID(DCP_PFOrderDeleteReq req, DCP_PFOrderDeleteRes res) throws Exception {
		// TODO Auto-generated method stub
		
		String eId = req.geteId();
		String shopId = req.getShopId();
		String PFNO = req.getRequest().getPfNo();
		
		DelBean db1 = new DelBean("DCP_PORDER_FORECAST_MATERIAL");
		db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
		db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
		db1.addCondition("PFNO", new DataValue(PFNO, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db1)); // 
		
		DelBean db2 = new DelBean("DCP_PORDER_FORECAST_DETAIL");
		db2.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
		db2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
		db2.addCondition("PFNO", new DataValue(PFNO, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db2)); // 
		
		DelBean db3 = new DelBean("DCP_PORDER_FORECAST");
		db3.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
		db3.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
		db3.addCondition("PFNO", new DataValue(PFNO, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db3)); //
		
		DelBean db4 = new DelBean("DCP_PORDER_FORECAST_DINNERTIME");
		db4.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
		db4.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
		db4.addCondition("PFNO", new DataValue(PFNO, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db4)); //
		
		DelBean db5 = new DelBean("DCP_PORDER_FORECASTCALCULATE");
		db5.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
		db5.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
		db5.addCondition("PFNO", new DataValue(PFNO, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db5)); //
		
		this.doExecuteDataToDB();		
		
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");	
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PFOrderDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PFOrderDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PFOrderDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PFOrderDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		  if(req.getRequest()==null)
		  {
		  	errMsg.append("request不能为空 ");
		  	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		  }
		  
        
		String pfNo = req.getRequest().getPfNo();
		
		if (Check.Null(pfNo)) 
		{
			errMsg.append("单据编号不可为空 ");
			isFail = true;
		} 
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_PFOrderDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PFOrderDeleteReq>(){};
	}

	@Override
	protected DCP_PFOrderDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_PFOrderDeleteRes();
	}
	
}
