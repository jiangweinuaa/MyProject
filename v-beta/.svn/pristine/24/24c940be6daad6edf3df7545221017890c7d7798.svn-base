package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CategoryControlCreateReq;
import com.dsc.spos.json.cust.res.DCP_CategoryControlCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_CategoryControlCreate extends SPosAdvanceService<DCP_CategoryControlCreateReq,DCP_CategoryControlCreateRes> {

	@Override
	protected void processDUID(DCP_CategoryControlCreateReq req, DCP_CategoryControlCreateRes res) throws Exception {
	// TODO Auto-generated method stub
		String eId = req.geteId();
		//String categoryType = req.getRequest().getCategoryType();
	  String category = req.getRequest().getCategory();

    String lastmoditime = null;//req.getRequest().getLastmoditime();
		if(lastmoditime==null||lastmoditime.isEmpty())
		{
			lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		}
	  
	  try 
	  {
	  	String[] columns_hm ={"EID","CATEGORY","CANSALE","CANFREE" ,
					"CANSTATISTICS","CANORDER","CANRETURN", "CANREQUIRE","CANREQUIREBACK","CANPRODUCE","CANPURCHASE","CANWEIGHT",
					"CANESTIMATE","CANMINUSSALE","CLEARTYPE","LASTMODITIME"
			};
			DataValue[] insValue_hm = new DataValue[] 
					{
						new DataValue(eId, Types.VARCHAR),						
						new DataValue(category, Types.VARCHAR),
						new DataValue(req.getRequest().getCanSale()==null?"0":req.getRequest().getCanSale(), Types.VARCHAR),
						new DataValue(req.getRequest().getCanFree()==null?"0":req.getRequest().getCanFree(),Types.VARCHAR),
						new DataValue(req.getRequest().getCanStatistics()==null?"0":req.getRequest().getCanStatistics(), Types.VARCHAR),
						new DataValue(req.getRequest().getCanOrder()==null?"0":req.getRequest().getCanOrder(), Types.VARCHAR),
						new DataValue(req.getRequest().getCanReturn()==null?"0":req.getRequest().getCanReturn() ,Types.VARCHAR),
						new DataValue(req.getRequest().getCanRequire()==null?"0":req.getRequest().getCanRequire() ,Types.VARCHAR),
						new DataValue(req.getRequest().getCanRequireBack()==null?"0":req.getRequest().getCanRequireBack(), Types.VARCHAR),
						new DataValue(req.getRequest().getCanProduce()==null?"0":req.getRequest().getCanProduce(), Types.VARCHAR),
						new DataValue(req.getRequest().getCanPurchase()==null?"0":req.getRequest().getCanPurchase() ,Types.VARCHAR),
						new DataValue(req.getRequest().getCanWeight()==null?"0":req.getRequest().getCanWeight() , Types.VARCHAR),
						new DataValue(req.getRequest().getCanEstimate()==null?"0":req.getRequest().getCanEstimate(), Types.VARCHAR), //修改人、时间等信息
						new DataValue(req.getRequest().getCanMinusSale()==null?"0":req.getRequest().getCanMinusSale(), Types.VARCHAR),
						new DataValue(req.getRequest().getClearType()==null?"0":req.getRequest().getClearType() ,Types.VARCHAR),
						new DataValue(lastmoditime , Types.DATE) 						
					};
			
			InsBean ib_hm = new InsBean("DCP_CATEGORY_CONTROL", columns_hm);
			ib_hm.addValues(insValue_hm);
			this.addProcessData(new DataProcessBean(ib_hm)); 
			
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
	protected List<InsBean> prepareInsertData(DCP_CategoryControlCreateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_CategoryControlCreateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_CategoryControlCreateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_CategoryControlCreateReq req) throws Exception {
		
	boolean isFail = false;
  StringBuffer errMsg = new StringBuffer("");

  if(req.getRequest()==null)
  {
  	errMsg.append("requset不能为空值 ");
  	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
  }
  //String categoryType = req.getRequest().getCategoryType();
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
	protected TypeToken<DCP_CategoryControlCreateReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_CategoryControlCreateReq>(){};
	}

	@Override
	protected DCP_CategoryControlCreateRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_CategoryControlCreateRes();
	}

}
