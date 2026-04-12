package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PlanDeleteReq;
import com.dsc.spos.json.cust.res.DCP_PlanDeleteRes;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 生产计划删除
 * 
 * @author yuanyy 2019-10-21
 *
 */
public class DCP_PlanDelete extends SPosAdvanceService<DCP_PlanDeleteReq, DCP_PlanDeleteRes> {

	@Override
	protected void processDUID(DCP_PlanDeleteReq req, DCP_PlanDeleteRes res) throws Exception {
		// TODO Auto-generated method stub
		
		String planNo = req.getPlanNo();
		try {
			
			DelBean db3 = new DelBean("DCP_PLAN");
			db3.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
			db3.addCondition("SHOPID", new DataValue(req.getShopId(), Types.VARCHAR));
			db3.addCondition("PLANNO", new DataValue(planNo, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db3)); // 
			
			DelBean db1 = new DelBean("DCP_PLAN_DETAIL");
			db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
			db1.addCondition("SHOPID", new DataValue(req.getShopId(), Types.VARCHAR));
			db1.addCondition("PLANNO", new DataValue(planNo, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1)); // 
			
			DelBean db2 = new DelBean("DCP_PLAN_MATERIAL");
			db2.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
			db2.addCondition("SHOPID", new DataValue(req.getShopId(), Types.VARCHAR));
			db2.addCondition("PLANNO", new DataValue(planNo, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db2)); // 
			
			RedisPosPub RP = new RedisPosPub();
			RP.DeleteKey("SaleExpect:" + req.geteId() + ":" + req.getShopId());					
			RP.Close();
			
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败："+e.getMessage());
		}
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PlanDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PlanDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PlanDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PlanDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
	    StringBuffer errMsg = new StringBuffer("");
	    int errCt = 0;
	    
	    if (Check.Null(req.getPlanNo())) {
	    	isFail = true;
	    	errCt++;
	    	errMsg.append("单据编号不可为空值, ");
	    }
	    if (isFail){
	    	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}		    
	    return isFail;
	}

	@Override
	protected TypeToken<DCP_PlanDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PlanDeleteReq>(){};
	}

	@Override
	protected DCP_PlanDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_PlanDeleteRes();
	}	
	
}
