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
import com.dsc.spos.json.cust.req.DCP_ReqCustomerProcessReq;
import com.dsc.spos.json.cust.res.DCP_ReqCustomerProcessRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_ReqCustomerProcess extends SPosAdvanceService<DCP_ReqCustomerProcessReq,DCP_ReqCustomerProcessRes> 
{
	@Override
	protected void processDUID(DCP_ReqCustomerProcessReq req, DCP_ReqCustomerProcessRes res) throws Exception {
	// TODO Auto-generated method stub
		String eId=req.geteId();;
		String submitBy = req.getOpNO();
		Calendar cal = Calendar.getInstance();// 获得当前时间
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String submitDate = df.format(cal.getTime());
		df = new SimpleDateFormat("HHmmss");
		String submitTime = df.format(cal.getTime());
		UptBean ub1 = null;	
		ub1 = new UptBean("DCP_REQCUSTOMER");
		ub1.addUpdateValue("STATUS", new DataValue(req.getStatus(),Types.VARCHAR));
		ub1.addUpdateValue("SUBMITBY", new DataValue(submitBy,Types.VARCHAR));
		ub1.addUpdateValue("SUBMIT_DATE", new DataValue(submitDate,Types.VARCHAR));
		ub1.addUpdateValue("SUBMIT_TIME", new DataValue(submitTime,Types.VARCHAR));
		
		ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		ub1.addCondition("CUSTOMER_ID", new DataValue(req.getCustomerID(), Types.VARCHAR));
    this.addProcessData(new DataProcessBean(ub1));
    this.doExecuteDataToDB();
		
    if (res.isSuccess()) 
		{
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		}	
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_ReqCustomerProcessReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_ReqCustomerProcessReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_ReqCustomerProcessReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_ReqCustomerProcessReq req) throws Exception {
	// TODO Auto-generated method stub
		boolean isFail = false;
    StringBuffer errMsg = new StringBuffer("");
    int errCt = 0;    
    if (Check.Null(req.getCustomerID())) {
      isFail = true;
      errCt++;
      errMsg.append("客户ID不可为空值, ");
    }  
    if (Check.Null(req.getStatus())) {
      isFail = true;
      errCt++;
      errMsg.append("状态(status)不可为空值, ");
    }  
    if (isFail){
      throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
    }
    
    return isFail;
	
	}

	@Override
	protected TypeToken<DCP_ReqCustomerProcessReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_ReqCustomerProcessReq>(){};
	}

	@Override
	protected DCP_ReqCustomerProcessRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_ReqCustomerProcessRes();
	}

}
