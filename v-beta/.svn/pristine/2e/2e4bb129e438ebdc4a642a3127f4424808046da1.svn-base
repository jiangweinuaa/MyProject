package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CategoryControlUpdateReq;
import com.dsc.spos.json.cust.res.DCP_CategoryControlUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_CategoryControlUpdate extends SPosAdvanceService<DCP_CategoryControlUpdateReq,DCP_CategoryControlUpdateRes> {

	@Override
	protected void processDUID(DCP_CategoryControlUpdateReq req, DCP_CategoryControlUpdateRes res) throws Exception {
	// TODO Auto-generated method stub
		String eId = req.geteId();		
	  String category = req.getRequest().getCategory();

    String lastmoditime = null;//req.getRequest().getLastmoditime();
		if(lastmoditime==null||lastmoditime.isEmpty())
		{
			lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		}
		
		String canSale = req.getRequest().getCanSale();
		String canFree = req.getRequest().getCanFree();
		String canStatistics = req.getRequest().getCanStatistics();
    String canOrder = req.getRequest().getCanOrder();
		String canReturn = req.getRequest().getCanReturn();
		String canRequire = req.getRequest().getCanRequire();
		String canRequireBack = req.getRequest().getCanRequireBack();
		String canProduce = req.getRequest().getCanProduce();
		String canPurchase = req.getRequest().getCanPurchase();
		String canWeight = req.getRequest().getCanWeight();
		String canEstimate = req.getRequest().getCanEstimate();
		String canMinusSale = req.getRequest().getCanMinusSale();
		String clearType = req.getRequest().getClearType();
		
		
		if(isExist(category, eId))
		{
			UptBean ub1 = null;	
			ub1 = new UptBean("DCP_CATEGORY_CONTROL");
			//add Value
			
			if (canSale!=null&&canSale.length()>0) 
			{
				ub1.addUpdateValue("CANSALE", new DataValue(canSale, Types.VARCHAR));	
			}
			if (canFree!=null&&canFree.length()>0) 
			{
				ub1.addUpdateValue("canFree", new DataValue(canFree, Types.VARCHAR));	
			}
			if (canStatistics!=null&&canStatistics.length()>0) 
			{
				ub1.addUpdateValue("canStatistics", new DataValue(canStatistics, Types.VARCHAR));	
			}
			if (canOrder!=null&&canOrder.length()>0) 
			{
				ub1.addUpdateValue("canOrder", new DataValue(canOrder, Types.VARCHAR));	
			}
			if (canReturn!=null&&canReturn.length()>0) 
			{
				ub1.addUpdateValue("canReturn", new DataValue(canReturn, Types.VARCHAR));	
			}
			if (canRequire!=null&&canRequire.length()>0) 
			{
				ub1.addUpdateValue("canRequire", new DataValue(canRequire, Types.VARCHAR));	
			}
			if (canRequireBack!=null&&canRequireBack.length()>0) 
			{
				ub1.addUpdateValue("canRequireBack", new DataValue(canRequireBack, Types.VARCHAR));	
			}
			if (canProduce!=null&&canProduce.length()>0) 
			{
				ub1.addUpdateValue("canProduce", new DataValue(canProduce, Types.VARCHAR));	
			}
			if (canPurchase!=null&&canPurchase.length()>0) 
			{
				ub1.addUpdateValue("canPurchase", new DataValue(canPurchase, Types.VARCHAR));	
			}
			if (canWeight!=null&&canWeight.length()>0) 
			{
				ub1.addUpdateValue("canWeight", new DataValue(canWeight, Types.VARCHAR));	
			}
			if (canEstimate!=null&&canEstimate.length()>0) 
			{
				ub1.addUpdateValue("canEstimate", new DataValue(canEstimate, Types.VARCHAR));	
			}
			if (canMinusSale!=null&&canMinusSale.length()>0) 
			{
				ub1.addUpdateValue("canMinusSale", new DataValue(canMinusSale, Types.VARCHAR));	
			}
			if (clearType!=null&&clearType.length()>0) 
			{
				ub1.addUpdateValue("CLEARTYPE", new DataValue(clearType, Types.VARCHAR));	
			}
			
			
			ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
			//condition
			ub1.addCondition("CATEGORY", new DataValue(category, Types.VARCHAR));		
			ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));		
			this.addProcessData(new DataProcessBean(ub1));
			this.doExecuteDataToDB();
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功！");
			
		}
		else 
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
		
		
	
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_CategoryControlUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_CategoryControlUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_CategoryControlUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_CategoryControlUpdateReq req) throws Exception {
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
	protected TypeToken<DCP_CategoryControlUpdateReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_CategoryControlUpdateReq>(){};
	}

	@Override
	protected DCP_CategoryControlUpdateRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_CategoryControlUpdateRes();
	}
	
	private boolean isExist(String category, String eId) throws Exception {
		boolean nRet = false;
		String sql = null;
		sql = "SELECT * FROM DCP_CATEGORY_CONTROL WHERE "
				+ " CATEGORY = '"+category+"' "
				+ " and EID = '"+eId+"'" ;	
		List<Map<String, Object>> getData = this.doQueryData(sql, null);
		if(getData!=null&&getData.isEmpty()==false)
		{
			nRet = true;
		}		
		return nRet;	
	}

}
