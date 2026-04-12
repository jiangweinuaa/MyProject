package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderPlatformCategoryBatchMappingReq;
import com.dsc.spos.json.cust.res.DCP_OrderPlatformCategoryBatchMappingRes;
import com.dsc.spos.json.cust.res.DCP_OrderPlatformCategoryQueryRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.waimai.HelpTools;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderPlatformCategoryBatchMapping extends SPosAdvanceService<DCP_OrderPlatformCategoryBatchMappingReq,DCP_OrderPlatformCategoryBatchMappingRes>{

	static String goodsLogFileName = "MappingCategorySaveLocal";
	@Override
	protected void processDUID(DCP_OrderPlatformCategoryBatchMappingReq req, DCP_OrderPlatformCategoryBatchMappingRes res)
		throws Exception {
	// TODO Auto-generated method stub
		String loadDocType = req.getLoadDocType();
		String eId = req.geteId();
		DCP_OrderPlatformCategoryQueryRes tempBaseCategory = this.GetTempCategory(req);
		if(tempBaseCategory==null||tempBaseCategory.getDatas()==null||tempBaseCategory.getDatas().size()==0)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "映射的分类模板（ 菜品池分类）没有资料！");
		}
		HelpTools.writelog_fileName("【批量映射商品分类】开始, 平台类型LoadDocType:"+loadDocType+" 映射的门店总数："+req.getErpShopNO().length, goodsLogFileName);
		int i=0;
		for (String erpShopNO : req.getErpShopNO()) 
		{
			i++;
			HelpTools.writelog_fileName("【批量映射商品分类】循环第【"+i+"】个门店开始, 平台类型LoadDocType:"+loadDocType+" 当前门店shopId："+erpShopNO, goodsLogFileName);			
			DCP_OrderPlatformCategoryQueryRes needMappingCategory = this.GetNeedMappingCategory(loadDocType, eId, erpShopNO);
		  if(needMappingCategory==null||needMappingCategory.getDatas()==null||needMappingCategory.getDatas().size()==0)
		  {
		  	HelpTools.writelog_fileName("【批量映射商品分类】循环第【"+i+"】个门店结束,该门店没有同步商品分类资料到本地！ 平台类型LoadDocType:"+loadDocType+" 当前门店shopId："+erpShopNO, goodsLogFileName);
		  	continue;
		  }
		  
		  HelpTools.writelog_fileName("【批量映射商品分类】开始同步映射当前门店商品, 平台类型LoadDocType:"+loadDocType+" 当前门店shopId："+erpShopNO+" 同步的商品分类总数："+needMappingCategory.getDatas().size(), goodsLogFileName);
			int j = 0;
			boolean IsMatch = false;
			for(DCP_OrderPlatformCategoryQueryRes.level1Elm oneData : needMappingCategory.getDatas())
			{
				j++;
				String categoryNO = oneData.getCategoryNO();
		  	String categoryName = oneData.getCategoryName();
		  	String orderCategoryNO = oneData.getOrderCategoryNO();
		  	String orderCategoryName = oneData.getOrderCategoryName();
		  	String orderPriority = oneData.getOrderPriority();
		  	HelpTools.writelog_fileName("【批量映射商品分类】同步循环第【"+j+"】个分类开始, 【没有匹配上菜品池匹配相应商品名称+规格名称】无需调用第三方接口！ 当前平台分类ID/名称："+orderCategoryNO+"/"+orderCategoryName, goodsLogFileName);
		    //region 开始与模板对比
		  	for(DCP_OrderPlatformCategoryQueryRes.level1Elm temp : tempBaseCategory.getDatas())
				{
		  		String temp_categoryNO = temp.getCategoryNO();
		  		String temp_categoryName = temp.getCategoryName();
		  		String temp_orderPriority = temp.getOrderPriority();
		  		if(temp_categoryName.isEmpty()==false&&temp_categoryName.equals(orderCategoryName))
		  		{
		  			categoryNO = temp_categoryNO;
		  			categoryName = temp_categoryName;
		  			oneData.setCategoryNO(temp_categoryNO);
		  			oneData.setCategoryName(temp_categoryName);
		  			oneData.setOrderPriority(temp_orderPriority);
		  			oneData.setResultMapping("Y");
		  			IsMatch = true;
		  			HelpTools.writelog_fileName("【批量映射商品分类】同步循环第【"+j+"】个分类！ 【匹配分类成功】！ 当前菜品池分类ID/名称："+categoryNO+"/"+categoryName, goodsLogFileName);
		  			break;		  			
		  		}
		  		  		
				}
		  	//endregion			
				
			}
			
			if(!IsMatch)
			{
				HelpTools.writelog_fileName("【批量映射商品分类】循环第【"+i+"】个门店结束！ 【没有匹配上菜品池商品分类】！ ", goodsLogFileName);
				continue;
			}
			//
			this.UpdateLocalDB(loadDocType, eId, erpShopNO, needMappingCategory);
		  
		}
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderPlatformCategoryBatchMappingReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderPlatformCategoryBatchMappingReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderPlatformCategoryBatchMappingReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderPlatformCategoryBatchMappingReq req) throws Exception {
	// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
					
		if(Check.Null(req.getLoadDocType()))
		{
			errCt++;
			errMsg.append("平台类型LoadDocType不可为空值, ");
			isFail = true;
		}
					
		if(req.getErpShopNO()==null||req.getErpShopNO().length ==0)
		{
			errCt++;
			errMsg.append("门店编码不能为空, ");
			isFail = true;
		}
						
		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
    
		return isFail;
	
	}

	@Override
	protected TypeToken<DCP_OrderPlatformCategoryBatchMappingReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_OrderPlatformCategoryBatchMappingReq>(){};
	}

	@Override
	protected DCP_OrderPlatformCategoryBatchMappingRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_OrderPlatformCategoryBatchMappingRes();
	}
	
	
	/**
	 * 获取平台每个门店的分类
	 * @param loadDocType
	 * @param eId
	 * @param erpShopNO
	 * @return
	 * @throws Exception
	 */
	private DCP_OrderPlatformCategoryQueryRes GetNeedMappingCategory(String loadDocType,String eId,String erpShopNO) throws Exception
	{
		String sql="select * from OC_MAPPINGCATEGORY where status='100' and EID='"+eId+"' "
				+ " and LOAD_DOCTYPE='"+loadDocType+"' and  SHOPID='"+erpShopNO+"'  order by PRIORITY  ";
		
		List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);
		if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
		{
			DCP_OrderPlatformCategoryQueryRes res = new DCP_OrderPlatformCategoryQueryRes();
			res.setDatas(new ArrayList<DCP_OrderPlatformCategoryQueryRes.level1Elm>());
		  for (Map<String, Object> map : getQDataDetail) 
		  {
		  	DCP_OrderPlatformCategoryQueryRes.level1Elm oneLv1 = res.new level1Elm();
		  	String categoryNO = map.get("CATEGORYNO").toString();
		  	String categoryName = map.get("CATEGORYNAME").toString();
		  	String orderCategoryNO = map.get("ORDER_CATEGORYNO").toString();
		  	String orderCategoryName = map.get("ORDER_CATEGORYNAME").toString();
		  	String orderPriority = map.get("PRIORITY").toString();
		  			  	
		  	oneLv1.setCategoryNO(categoryNO);
		  	oneLv1.setCategoryName(categoryName);
		  	oneLv1.setOrderCategoryNO(orderCategoryNO);
		  	oneLv1.setOrderCategoryName(orderCategoryName);
		  	oneLv1.setOrderPriority(orderPriority);
		  	
		  	res.getDatas().add(oneLv1);
		  	oneLv1 =null;
		
		  }
		  return res;
					 
		}
		
		return null;
	}
	
	/**
	 * 获取菜品池的分类
	 * @param req
	 * @return
	 * @throws Exception
	 */
	private DCP_OrderPlatformCategoryQueryRes GetTempCategory(DCP_OrderPlatformCategoryBatchMappingReq req) throws Exception
	{
		
		String sql = "  select * from OC_category where status='100' and EID='"+req.geteId()+"' order by priority";
		
		
		List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);
		if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
		{
			DCP_OrderPlatformCategoryQueryRes res = new DCP_OrderPlatformCategoryQueryRes();
			res.setDatas(new ArrayList<DCP_OrderPlatformCategoryQueryRes.level1Elm>());
		  for (Map<String, Object> map : getQDataDetail) 
		  {
		  	DCP_OrderPlatformCategoryQueryRes.level1Elm oneLv1 = res.new level1Elm();
		  	String categoryNO = map.get("CATEGORYNO").toString();
		  	String categoryName = map.get("CATEGORYNAME").toString();
		  	String orderPriority = map.get("PRIORITY").toString();
		  			  	
		  	oneLv1.setCategoryNO(categoryNO);
		  	oneLv1.setCategoryName(categoryName);
		  	oneLv1.setOrderPriority(orderPriority);
		  	res.getDatas().add(oneLv1);
		  	oneLv1 = null;
		
		  }
		  return res;
					 
		}
		return null;
		
		
	}

	/**
	 * 映射上了更新本地
	 * @param loadDocType
	 * @param eId
	 * @param erpShopNO
	 * @param res
	 * @throws Exception
	 */
	private void UpdateLocalDB(String loadDocType,String eId,String erpShopNO,DCP_OrderPlatformCategoryQueryRes res) throws Exception
	{
		if(res==null||res.getDatas()==null||res.getDatas().size()==0)
		{
			return;
		}
		
		for (DCP_OrderPlatformCategoryQueryRes.level1Elm oneLv1 : res.getDatas()) 
		{
			if(oneLv1.getResultMapping()==null||oneLv1.getResultMapping().equals("N"))
			{
				continue;
			}
			String categoryNO = oneLv1.getCategoryNO();
	  	String categoryName = oneLv1.getCategoryName();
	  	String orderCategoryNO = oneLv1.getOrderCategoryNO();
	  	String orderCategoryName = oneLv1.getOrderCategoryName();
	  	String orderPriority = oneLv1.getOrderPriority();
			
			UptBean up1 = new UptBean("OC_mappingcategory");
			up1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			up1.addCondition("LOAD_DOCTYPE", new DataValue(loadDocType, Types.VARCHAR));
			up1.addCondition("SHOPID", new DataValue(erpShopNO, Types.VARCHAR));
			up1.addCondition("ORDER_CATEGORYNO", new DataValue(orderCategoryNO, Types.VARCHAR));
			
			up1.addUpdateValue("CATEGORYNO", new DataValue(categoryNO, Types.VARCHAR));
			up1.addUpdateValue("CATEGORYNAME", new DataValue(categoryName, Types.VARCHAR));
			up1.addUpdateValue("PRIORITY", new DataValue(orderPriority, Types.VARCHAR));
			
			this.addProcessData(new DataProcessBean(up1));
			HelpTools.writelog_fileName("【批量映射商品分类】【回写本地数据】循环开始,  当前平台分类ID/名称："+orderCategoryNO+"/"+orderCategoryName, goodsLogFileName);
		}
				
		if(this.pData.size()>0)
		{
			try 
			{
				HelpTools.writelog_fileName("【批量映射商品分类】【回写本地数据】开始执行Update！  平台类型LoadDocType:"+loadDocType+" 当前门店shopId："+erpShopNO, goodsLogFileName);		
				this.doExecuteDataToDB();
				HelpTools.writelog_fileName("【批量映射商品分类】【回写本地数据】执行Update成功！  平台类型LoadDocType:"+loadDocType+" 当前门店shopId："+erpShopNO, goodsLogFileName);		
				
	    } 
			catch (Exception e) 
			{
				HelpTools.writelog_fileName("【批量映射商品分类】【回写本地数据】执行Update异常："+e.getMessage()+"  平台类型LoadDocType:"+loadDocType+" 当前门店shopId："+erpShopNO, goodsLogFileName);					
	    }
			this.pData.clear();
		}
		
	}
}
