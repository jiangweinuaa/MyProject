package com.dsc.spos.service.imp.json;

import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_CategoryControlQueryReq;
import com.dsc.spos.json.cust.res.DCP_CategoryControlQueryRes;
import com.dsc.spos.json.cust.res.DCP_CategoryControlQueryRes.level1Elm;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_CategoryControlQuery extends SPosBasicService<DCP_CategoryControlQueryReq,DCP_CategoryControlQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_CategoryControlQueryReq req) throws Exception {
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
	protected TypeToken<DCP_CategoryControlQueryReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_CategoryControlQueryReq>(){} ;
	}

	@Override
	protected DCP_CategoryControlQueryRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_CategoryControlQueryRes();
	}

	@Override
	protected DCP_CategoryControlQueryRes processJson(DCP_CategoryControlQueryReq req) throws Exception {
	// TODO Auto-generated method stub
		
		DCP_CategoryControlQueryRes res = null;
		res = this.getResponse();
		String eId = req.geteId();		
	  String category = req.getRequest().getCategory();
		
		String sql = "select * from DCP_CATEGORY_CONTROL where EID='"+eId+"' and CATEGORY='"+category+"' ";
		
		List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, null);
		DCP_CategoryControlQueryRes.level1Elm data = res.new level1Elm();
		
		if(getQDataDetail!=null&&getQDataDetail.isEmpty()==false)
		{
			Map<String, Object> oneData = getQDataDetail.get(0);	
			String canSale = oneData.get("CANSALE").toString();
			String canFree = oneData.get("CANFREE").toString();
			String canStatistics = oneData.get("CANSTATISTICS").toString();
	    String canOrder = oneData.get("CANORDER").toString();
			String canReturn = oneData.get("CANRETURN").toString();
			String canRequire = oneData.get("CANREQUIRE").toString();
			String canRequireBack = oneData.get("CANREQUIREBACK").toString();
			String canProduce = oneData.get("CANPRODUCE").toString();
			String canPurchase = oneData.get("CANPURCHASE").toString();
			String canWeight = oneData.get("CANWEIGHT").toString();
			String canEstimate = oneData.get("CANESTIMATE").toString();
			String canMinusSale = oneData.get("CANMINUSSALE").toString();
			String clearType = oneData.get("CLEARTYPE").toString();
			
			data.setCanSale(canSale);
			data.setCanFree(canFree);
			data.setCanStatistics(canStatistics);
			data.setCanOrder(canOrder);
			data.setCanReturn(canReturn);
			data.setCanRequire(canRequire);
			data.setCanRequireBack(canRequireBack);
			data.setCanProduce(canProduce);
			data.setCanPurchase(canPurchase);
			data.setCanWeight(canWeight);
			data.setCanEstimate(canEstimate);
			data.setCanMinusSale(canMinusSale);
			data.setClearType(clearType);
			data.setCategory(category);		
			
		}
		res.setDatas(data);		
		
	
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	// TODO Auto-generated method stub
	
	}

	@Override
	protected String getQuerySql(DCP_CategoryControlQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

}
