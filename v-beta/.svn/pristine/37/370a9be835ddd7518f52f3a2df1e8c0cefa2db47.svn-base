package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_GoodsCategoryQueryReq;
import com.dsc.spos.json.cust.res.DCP_GoodsCategoryQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsCategoryQuery extends SPosBasicService<DCP_GoodsCategoryQueryReq, DCP_GoodsCategoryQueryRes> {
  
	@Override
	protected boolean isVerifyFail(DCP_GoodsCategoryQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		if(req.getRequest()==null)
    {
    	errMsg.append("requset不能为空值 ");
    	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
    }
			
	

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
			
	}

	@Override
	protected TypeToken<DCP_GoodsCategoryQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_GoodsCategoryQueryReq>(){};
	}

	@Override
	protected DCP_GoodsCategoryQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_GoodsCategoryQueryRes();
	}

	List<Map<String, Object>> allLangDatas = new ArrayList<Map<String, Object>>();
	
	@Override
	protected DCP_GoodsCategoryQueryRes processJson(DCP_GoodsCategoryQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql = null;
		DCP_GoodsCategoryQueryRes res = null;
		res = this.getResponse();
		String eId = req.geteId();
		//String categoryType = req.getRequest().getCategoryType();
		String funcSql="select * from DCP_CATEGORY_LANG where EID='"+req.geteId()+"' ";
	    allLangDatas = this.doQueryData(funcSql, null);
		
		sql = this.getQuerySql(req);
		List<Map<String, Object>> allDatas = this.doQueryData(sql, null);
		res.setDatas(new ArrayList<DCP_GoodsCategoryQueryRes.level1Elm>());
		if(allDatas != null && !allDatas.isEmpty()){
			Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
			condition.put("CATEGORY", true);		
			//调用过滤函数
			List<Map<String, Object>> catDatas=MapDistinct.getMap(allDatas, condition);
			
			for (Map<String, Object> oneData : catDatas) 
			{
				DCP_GoodsCategoryQueryRes.level1Elm oneLv1 = res.new level1Elm();
				
				String category = oneData.get("CATEGORY").toString();
				String categoryName = oneData.get("CATEGORYNAME").toString();
				String upCategory = oneData.get("UPCATEGORY").toString();
				String upCategoryName = oneData.get("UPCATEGORYNAME").toString();
				String topCategory = oneData.get("TOPCATEGORY").toString();
				String topCategoryName = oneData.get("TOPCATEGORYNAME").toString();
				String downCategoryQty = oneData.get("DOWNCATEGORYQTY").toString();
				
				// 新增商品分类图片
				String categoryImage = oneData.get("CATEGORYIMAGE").toString();
				
				String categoryLevel = oneData.get("CATEGORYLEVEL").toString();
				String status = oneData.get("STATUS").toString();
                String preFixCode = oneData.get("PREFIXCODE").toString();


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
				
				//if (categoryLevel != null && categoryLevel.equals("1"))
				{
					oneLv1.setCategory(category);
					// oneLv1.setCategoryType(categoryType);
					oneLv1.setCategoryName(categoryName);
					oneLv1.setUpCategory(upCategory);
					oneLv1.setUpCategoryName(upCategoryName);
					oneLv1.setTopCategory(topCategory);
					oneLv1.setTopCategoryName(topCategoryName);
					oneLv1.setDownCategoryQty(downCategoryQty);
                    oneLv1.setPreFixCode(preFixCode);
					
					if(!Check.Null(categoryImage)){
						// 拼接返回图片路径
						String ISHTTPS=PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
						String httpStr=ISHTTPS.equals("1")?"https://":"http://";
						
						String DomainName=PosPub.getPARA_SMS(dao, eId, "", "DomainName");
						if (DomainName.endsWith("/")) 
						{
							oneLv1.setCategoryImageUrl(httpStr+DomainName+"resource/image/" +categoryImage);	
						}
						else 
						{
							oneLv1.setCategoryImageUrl(httpStr+DomainName+"/resource/image/" +categoryImage);	
						}	
					}else{
						oneLv1.setCategoryImageUrl("");
					}
					oneLv1.setCategoryLevel(categoryLevel);
					oneLv1.setStatus(status);

					oneLv1.setCanSale(canSale);
					oneLv1.setCanFree(canFree);
					oneLv1.setCanStatistics(canStatistics);
					oneLv1.setCanOrder(canOrder);
					oneLv1.setCanReturn(canReturn);
					oneLv1.setCanRequire(canRequire);
					oneLv1.setCanRequireBack(canRequireBack);
					oneLv1.setCanProduce(canProduce);
					oneLv1.setCanPurchase(canPurchase);
					oneLv1.setCanWeight(canWeight);
					oneLv1.setCanEstimate(canEstimate);
					oneLv1.setCanMinusSale(canMinusSale);
					oneLv1.setClearType(clearType);

					oneLv1.setChildren(new ArrayList<DCP_GoodsCategoryQueryRes.level1Elm>());
					oneLv1.setCategoryName_lang(new ArrayList<DCP_GoodsCategoryQueryRes.level2Elm>());
					// 接下来插入多语言信息
					List<Map<String, Object>> langList = getLangDatas(allLangDatas, category);
					if (langList != null && !langList.isEmpty())
					{

						for (Map<String, Object> langDatas : langList)
						{
							// 过滤属于此单头的明细
							if (category.equals(langDatas.get("CATEGORY")) == false)
								continue;
							// 在这里过滤除属于第一级的func
							DCP_GoodsCategoryQueryRes.level2Elm fstLang = res.new level2Elm();

							String langType = langDatas.get("LANG_TYPE").toString();
							String lCategoryName = langDatas.get("CATEGORY_NAME").toString();

							fstLang.setLangType(langType);
							fstLang.setName(lCategoryName);

							oneLv1.getCategoryName_lang().add(fstLang);
						}
					}

					//setChildrenDatas(oneLv1, catDatas);
					res.getDatas().add(oneLv1);
				}

			}
			
		}
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_GoodsCategoryQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		String loadLangType = req.getLangType();
		
		String eId = req.geteId();
		//String categoryType = req.getRequest().getCategoryType();
		String keyTxt = req.getRequest().getKeyTxt();
		String status = req.getRequest().getStatus();
		String category = req.getRequest().getCategory();
		
		sqlbuf.append(" select  DISTINCT * from (SELECT  a.CATEGORY, b.category_Name AS CATEGORYName , case when a.CATEGORY=up_CateGory then N'' else up_CateGory  end  AS upCategory ,"
				 + "  c.category_name as upCategoryname,a.preFixCode,"
				 + " a.categorylevel  ,a.status, a.top_category AS topCategory , a.down_categoryQty AS  downCategoryQty ,  "
				 + " d.category_name AS topCategoryName , "
				 + " b.lang_type  AS langType,  b.category_Name AS LCategoryname ,a.EID, "
				 + " E.CANSALE,E.CANFREE,E.CANSTATISTICS,E.CANORDER,E.CANRETURN,E.CANREQUIRE,E.CANREQUIREBACK,E.CANPRODUCE,E.CANPURCHASE,E.CANWEIGHT,E.CANMINUSSALE,E.CANESTIMATE,E.CLEARTYPE,F.categoryImage,a.createopid,g.op_name as createopname "
				 + " FROM dcp_category a "
				 + " LEFT JOIN dcp_category_lang b  ON a.EID = b.EID AND a.category = b.category  "
				 + " LEFT JOIN  dcp_category_lang c ON a.EID = c.EID AND a.up_CateGory = c.category "
				 + " AND c.lang_Type = '"+loadLangType+"' "
				 + " LEFT JOIN   dcp_category_lang  d ON a.EID = d.EID AND  a.top_category = d.category "
				 + " AND d.lang_Type = '"+loadLangType+"'"
				 + " left join dcp_category_control e ON a.EID = E.EID AND a.category = e.category "
				 + " LEFT JOIN dcp_category_image f ON a.EID = f.EID AND a.category = f.category " +
                " left join PLATFORM_STAFFS_LANG g on g.eid=a.eid and g.opno=a.createopid and g.lang_type='"+req.getLangType()+"' "
				 + " ) "
				 + " where EID = '"+eId+"' ");
		/*if( category!= null && category.length()>0)
		{
			sqlbuf.append(" and (CATEGORY='" + category + "' OR upCategory='"+category+"') ");
		}*/
		if( category!= null && category.length()>0)
		{
			sqlbuf.append(" and (CATEGORY='" + category + "') ");
		}
		
		if(status != null && status.length()>0)
		{
			sqlbuf.append(" and status='" + status + "' ");
		}
		if (keyTxt != null && keyTxt.length()>0)
		{
			sqlbuf.append(" AND (CATEGORY LIKE '%%"+keyTxt+"%%' "
					+ " OR categoryname LIKE '%%"+keyTxt+"%%'  "
					+ " OR  upcategory LIKE '%%"+keyTxt+"%%' "
					+ " OR upCategoryname LIKE '%%"+keyTxt+"%%' ) ");
//			sqlbuf.append( " START WITH (CATEGORY LIKE  '%%"+keyTxt+"%%'  OR categoryName  LIKE '%%"+keyTxt+"%%' )  "
//					+ "CONNECT BY PRIOR  upCategory = CATEGORY " );
		}
		
		sqlbuf.append( " order by category");	
		sql = sqlbuf.toString();
		return sql;
	}
	
	
	/**
	 * 获取下一层级信息
	 * @param allDatas
	 * @param category
	 * @return
	 */
	protected List<Map<String, Object>> getChildDatas (List<Map<String, Object>> allDatas,String category)
	{
		List<Map<String, Object>> datas =new ArrayList<>();
		for (Map<String, Object> map : allDatas) 
		{
			if(map.get("UPCATEGORY").toString().equals(category))
			{
				datas.add(map);
			}
		}
		return datas;
	}
	
	
	
	/**
	 * 过滤出该主键对应的多语言信息
	 * @param
	 * @param primaryNO
	 * @return
	 */
	protected List<Map<String, Object>> getLangDatas(List<Map<String, Object>> allLangDatas,String primaryNO)
	{
		List<Map<String, Object>> langDatas =new ArrayList<>();
		for (Map<String, Object> map : allLangDatas) 
		{
			if(map.get("CATEGORY").toString().equals(primaryNO))
			{
				langDatas.add(map);
			}
		}
		return langDatas;
	}

	
	/**
	 * 循环添加层级 
	 * @param oneLv2
	 * @param allDatas
	 * @throws Exception
	 */
	protected void setChildrenDatas(DCP_GoodsCategoryQueryRes.level1Elm oneLv2,List<Map<String, Object>> allDatas) throws Exception
	{
		try {
			List<Map<String, Object>> nextDatas  = getChildDatas(allDatas,oneLv2.getCategory());
			if(nextDatas != null && !nextDatas.isEmpty())
			{
				for (Map<String, Object> datas : nextDatas) 
				{
					DCP_GoodsCategoryQueryRes.level1Elm oneLv1 = new DCP_GoodsCategoryQueryRes().new level1Elm();
					
					String category = datas.get("CATEGORY").toString();
					//String categoryType = datas.get("CATEGORYTYPE").toString();
					String categoryName = datas.get("CATEGORYNAME").toString();
					String upCategory = datas.get("UPCATEGORY").toString();
					String upCategoryName = datas.get("UPCATEGORYNAME").toString();
					String topCategory = datas.get("TOPCATEGORY").toString();
					String topCategoryName = datas.get("TOPCATEGORYNAME").toString();
					String downCategoryQty = datas.get("DOWNCATEGORYQTY").toString();
					String categoryLevel = datas.get("CATEGORYLEVEL").toString();
					String status = datas.get("STATUS").toString();
					
					String canSale = datas.get("CANSALE").toString();
					String canFree = datas.get("CANFREE").toString();
					String canStatistics = datas.get("CANSTATISTICS").toString();
			    String canOrder = datas.get("CANORDER").toString();
					String canReturn = datas.get("CANRETURN").toString();
					String canRequire = datas.get("CANREQUIRE").toString();
					String canRequireBack = datas.get("CANREQUIREBACK").toString();
					String canProduce = datas.get("CANPRODUCE").toString();
					String canPurchase = datas.get("CANPURCHASE").toString();
					String canWeight = datas.get("CANWEIGHT").toString();
					String canEstimate = datas.get("CANESTIMATE").toString();
					String canMinusSale = datas.get("CANMINUSSALE").toString();
					String clearType = datas.get("CLEARTYPE").toString();
				
					oneLv1.setCategory(category);					
					oneLv1.setCategoryName(categoryName);
					oneLv1.setUpCategory(upCategory);
					oneLv1.setUpCategoryName(upCategoryName);
					oneLv1.setTopCategory(topCategory);
					oneLv1.setTopCategoryName(topCategoryName);
					oneLv1.setDownCategoryQty(downCategoryQty);
					oneLv1.setCategoryLevel(categoryLevel);
					oneLv1.setStatus(status);
					
					oneLv1.setCanSale(canSale);
					oneLv1.setCanFree(canFree);
					oneLv1.setCanStatistics(canStatistics);
					oneLv1.setCanOrder(canOrder);
					oneLv1.setCanReturn(canReturn);
					oneLv1.setCanRequire(canRequire);
					oneLv1.setCanRequireBack(canRequireBack);
					oneLv1.setCanProduce(canProduce);
					oneLv1.setCanPurchase(canPurchase);
					oneLv1.setCanWeight(canWeight);
					oneLv1.setCanEstimate(canEstimate);
					oneLv1.setCanMinusSale(canMinusSale);
					oneLv1.setClearType(clearType);
					
					oneLv1.setChildren(new ArrayList<DCP_GoodsCategoryQueryRes.level1Elm>());
					oneLv1.setCategoryName_lang(new ArrayList<DCP_GoodsCategoryQueryRes.level2Elm>());
					//接下来插入多语言信息
					List<Map<String, Object>> langList = getLangDatas(allLangDatas,category);
					if(langList != null && !langList.isEmpty())
					{
						for (Map<String, Object> langDatas : langList) 
						{
							//过滤属于此单头的明细
							if(category.equals(langDatas.get("CATEGORY")) == false)
								continue;
							
							DCP_GoodsCategoryQueryRes.level2Elm fstLang = new DCP_GoodsCategoryQueryRes().new level2Elm();
							
							String langType = langDatas.get("LANG_TYPE").toString();
							String lCategoryName = langDatas.get("CATEGORY_NAME").toString();
							
							
							fstLang.setLangType(langType);
							fstLang.setName(lCategoryName);
							
							
							oneLv1.getCategoryName_lang().add(fstLang);
							fstLang = null;
						}
					}
					
					setChildrenDatas(oneLv1,allDatas);
					oneLv2.getChildren().add(oneLv1);
					
					oneLv1 = null;
					
				}
			}
			
			
		} catch (Exception e) {

		}
		
		
		
	}
	
}
