package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CategoryControlDeleteReq;
import com.dsc.spos.json.cust.res.DCP_CategoryControlDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_CategoryControlDelete  extends SPosAdvanceService<DCP_CategoryControlDeleteReq,DCP_CategoryControlDeleteRes> {

	@Override
	protected void processDUID(DCP_CategoryControlDeleteReq req, DCP_CategoryControlDeleteRes res) throws Exception {
	// TODO Auto-generated method stub
		
		try 
		{
			String eId = req.geteId();;
			//String categoryType = req.getRequest().getCategoryType();
		  String category = req.getRequest().getCategory();

			DelBean db1 = new DelBean("DCP_BFEE_DETAIL");
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			//db1.addCondition("CATEGORYTYPE", new DataValue(categoryType, Types.VARCHAR));
			db1.addCondition("CATEGORY", new DataValue(category, Types.VARCHAR));
			
			this.addProcessData(new DataProcessBean(db1));
			
      this.doExecuteDataToDB();
			
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		
	
		} 
		catch (Exception e) 
		{
		// TODO: handle exception
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("服务执行异常:"+e.getMessage());
	
		}
		
		
		
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_CategoryControlDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_CategoryControlDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_CategoryControlDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_CategoryControlDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	
	boolean isFail = false;
	StringBuffer errMsg = new StringBuffer("");

  if(req.getRequest()==null)
  {
  	errMsg.append("requset不能为空值 ");
  	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
  }
  
  String category = req.getRequest().getCategory();
       
  if(Check.Null(category)){
   	errMsg.append("分类编码不能为空值 ");
   	isFail = true;

  }
   
 if (isFail)
 {
	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
 }
 
 return isFail;
	
	}

	@Override
	protected TypeToken<DCP_CategoryControlDeleteReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_CategoryControlDeleteReq>(){};
	}

	@Override
	protected DCP_CategoryControlDeleteRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_CategoryControlDeleteRes();
	}

}
