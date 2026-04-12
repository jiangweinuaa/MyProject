package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_ParaSetQueryNewReq;
import com.dsc.spos.json.cust.res.DCP_ParaSetQueryNewRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_ParaSetQueryNew extends SPosBasicService<DCP_ParaSetQueryNewReq,DCP_ParaSetQueryNewRes>
{
	@Override
	protected boolean isVerifyFail(DCP_ParaSetQueryNewReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_ParaSetQueryNewReq> getRequestType() 
	{	
		return new TypeToken<DCP_ParaSetQueryNewReq>(){};
	}

	@Override
	protected DCP_ParaSetQueryNewRes getResponseType() 
	{		
		return new DCP_ParaSetQueryNewRes();
	}

	@Override
	protected DCP_ParaSetQueryNewRes processJson(DCP_ParaSetQueryNewReq req) throws Exception 
	{	
		String sql=null;
		DCP_ParaSetQueryNewRes res = null;
		res = this.getResponse();
		try {

			String langType = req.getLangType();
			
			sql=this.getQuerySql(req);
			List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, null);
			DCP_ParaSetQueryNewRes.level1Elm lv1 = res.new level1Elm();
			
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
			{
				
				lv1.setClassList(new ArrayList<DCP_ParaSetQueryNewRes.ClassList>());
				//ClassList 节点
				Map<String, Boolean> ClassCon = new HashMap<String, Boolean>(); //查詢條件
				ClassCon.put("CLASSNO", true);	
				//调用过滤函数
				List<Map<String, Object>> classDatas = MapDistinct.getMap(getQDataDetail, ClassCon);
				
//				//单头主键字段
//				Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
//				condition.put("CLASSNO", true);
//				condition.put("ITEM", true);	
//				//调用过滤函数
//				List<Map<String, Object>> getQHeader=MapDistinct.getMap(getQDataDetail, condition);
				
				//单头主键字段
				Map<String, Boolean> condition2 = new HashMap<String, Boolean>(); //查詢條件
				condition2.put("CLASSNO", true);
				condition2.put("ITEM", true);	
				//调用过滤函数
				List<Map<String, Object>> itemListDatas = MapDistinct.getMap(getQDataDetail, condition2);
				
				//单头主键字段
				Map<String, Boolean> condition3 = new HashMap<String, Boolean>(); //查詢條件
				condition3.put("CLASSNO", true);
				condition3.put("ITEM", true);	
				condition3.put("PARAITEMVALUE", true);
				//调用过滤函数
				List<Map<String, Object>> paraDatas = MapDistinct.getMap(getQDataDetail, condition3);
				
				
				Map<String, Boolean> condition4 = new HashMap<String, Boolean>(); //查詢條件
				condition4.put("CLASSNO", true);
				condition4.put("ITEM", true);	
				condition4.put("PARAITEMVALUE", true);
				condition4.put("LANGTYPE", true);
				//调用过滤函数
				List<Map<String, Object>> paraLangDatas = MapDistinct.getMap(getQDataDetail, condition4);
				
				//单头主键字段
				Map<String, Boolean> condition5 = new HashMap<String, Boolean>(); //查詢條件
//				condition5.put("TEMPLATEID", true);
				condition5.put("CLASSNO", true);
				condition5.put("ITEM", true);	
				condition5.put("PARALANGTYPE", true);
				//调用过滤函数
				List<Map<String, Object>> itemLangDatas = MapDistinct.getMap(getQDataDetail, condition5);
				
				
				if(classDatas != null && !classDatas.isEmpty()){
					for(Map<String, Object> classMap : classDatas){
						DCP_ParaSetQueryNewRes.ClassList lvClass = res.new ClassList();
						String classNo = classMap.get("CLASSNO").toString();
						String className = classMap.get("CLASSNAME").toString();
						lvClass.setClassName(className);
						lvClass.setClassNo(classNo);
						
						lvClass.setItemList(new ArrayList<DCP_ParaSetQueryNewRes.ItemList>());
						
						for (Map<String, Object> itemMap : itemListDatas) {
							
							DCP_ParaSetQueryNewRes.ItemList itemLv = res.new ItemList();
							String item = itemMap.get("ITEM").toString();
							if(Check.Null(item)){
								continue;
							}
							if( itemMap.get("CLASSNO").equals(classNo) &&  item.equals(itemMap.get("ITEM" ).toString())){
								
								
								String conType = itemMap.get("CONTYPE").toString();
								String itemValue = itemMap.get("DEFITEMVALUE").toString();
								String remark = itemMap.get("REMARK").toString();
								
								String m_itemName = "";
								itemLv.setPara(new ArrayList<DCP_ParaSetQueryNewRes.level2Elm>());
								itemLv.setItemlang(new ArrayList<DCP_ParaSetQueryNewRes.level2ElmLang>()); 
								
								// para层,参数值, 参数值名称
								if(paraDatas != null && !paraDatas.isEmpty()){
									for (Map<String, Object> paraMap : paraDatas) {
										if( paraMap.get("CLASSNO").equals(classNo) && 
												item.equals(paraMap.get("ITEM" ).toString())){
											
											DCP_ParaSetQueryNewRes.level2Elm lv2 = res.new level2Elm();
											String paraItemValue = paraMap.get("PARAITEMVALUE").toString();
											
											if(Check.Null(paraItemValue)){
												continue;
											}
											
//											itemValue = paraItemValue;
											String m_valueName="";
											String status = paraMap.get("STATUS").toString();
											String onSale = paraMap.get("ONSALE").toString();
//												String cnfflg = "N";
//												if(!Check.Null(status) && status.equals("100")) {
//													cnfflg = "Y";
//												}
											
											lv2.setValuelang(new ArrayList<DCP_ParaSetQueryNewRes.level3ElmLang>());
											
											if(paraLangDatas != null && !paraLangDatas.isEmpty() ){
												
												for (Map<String, Object> paraLang : paraLangDatas) {
													
													if(paraLang.get("ITEM").equals("EnableMultiLang")){
														System.out.println("到EnableMultiLang了");
													}
													
													if(item.equals(paraLang.get("ITEM").toString()) 
															&& classNo.equals(paraLang.get("CLASSNO").toString()) 
															&& paraItemValue.equals(paraLang.get("PARAITEMVALUE").toString() )){
														
														DCP_ParaSetQueryNewRes.level3ElmLang lv3Lang = res.new level3ElmLang(); 
														
														String valueLangType = paraLang.get("LANGTYPE").toString();
														String paraValueName = paraLang.get("VALUENAME").toString();

														if(Check.Null(valueLangType) ){
															continue;
														}
														
														if(langType.equals(valueLangType)){
															m_valueName = paraValueName;
														}
														
														lv3Lang.setLangType(valueLangType);
														lv3Lang.setValueName(paraValueName);
														lv2.getValuelang().add(lv3Lang);
														
													}
													
												}
												
											}
											
											lv2.setItemValue(paraItemValue);
											lv2.setValueName( m_valueName );
											lv2.setStatus(status);
											lv2.setOnSale(onSale);
											itemLv.getPara().add(lv2);
											
										}
										
									}
									
								}
								
								if(itemLangDatas != null && !itemLangDatas.isEmpty() ){
									
									for (Map<String, Object> itemLangMap : itemLangDatas) {
										
										if(itemLangMap.get("CLASSNO").equals(classNo) && item.equals(itemLangMap.get("ITEM").toString())){
											
											DCP_ParaSetQueryNewRes.level2ElmLang lv2Lang = res.new level2ElmLang();
											String itemLangType = itemLangMap.get("PARALANGTYPE").toString();
											String itemName = itemLangMap.get("ITEMNAME").toString();
											lv2Lang.setLangType(itemLangType);
											lv2Lang.setItemName(itemName);
											if(langType.equals(itemLangType)){
												m_itemName = itemName;
											}
											
											itemLv.getItemlang().add(lv2Lang);
										}	
										
									}
								}
								
								itemLv.setItem(item);
								itemLv.setItemName(m_itemName);
								itemLv.setConType(conType);
								itemLv.setItemValue(itemValue);
								itemLv.setRemark(remark);
								
								lvClass.getItemList().add(itemLv);
							}
					
						}
						
						lv1.getClassList().add(lvClass);
						
					}
				}
				
//				res.getDatas().add(lv1);

				res.setDatas(lv1);
			}
			else
			{
				res.setDatas(lv1);
			}
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功！");	
		}
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_ParaSetQueryNewReq req) throws Exception 
	{

		String sql=null;
		String langType=req.getLangType();
		String eId=req.geteId();
		
		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();

		if(pageNumber == 0 || pageSize == 0){
			pageSize = 99999;
			pageNumber = 1;
		}
		
		//分页起始位置
		int startRow=(pageNumber-1) * pageSize;
		
		String paraShop = req.getRequest().getParaShop();
		String paraMachine = req.getRequest().getParaMachine();
		String modularId = req.getRequest().getModularId();
		String keyTxt = req.getRequest().getKeyTxt();
		
		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append(" "
				+ " select * from ( "
				+ " SELECT  "
				+ " count(DISTINCT priority )over() num ,dense_rank() OVER(ORDER BY f.priority , F.CLASSNO) AS RN, f.itemvalue AS defItemValue , "
				+ " f.item ,  "
				+ " f.contype , g.lang_Type AS paraLangType   ,  g.item_name AS itemName , f.remark ,   f.classNo , j.className ,  "
				+ " h.itemValue AS paraItemValue , h.status , nvl(h.onSale , 'N') AS onSale ,"
				+ " i.lang_type AS LangType , i.value_name AS ValueName ,"
				+ " l.modularId , k.modularName "
				+ " FROM  Platform_BaseSetTemp f  "
				+ " LEFT JOIN Platform_BaseSetTemp_lang g ON f.eid = g.eid AND f.item = g.item "
				+ "  AND g.lang_type  = '"+req.getLangType()+"' "
				+ " LEFT JOIN Platform_BaseSetTemp_Para h ON f.eid = h.eid AND f.item = h.item "
				+ " LEFT JOIN Platform_BaseSetTemp_Para_lang i ON h.eid = i.eid AND h.item = i.item  and h.itemValue = i.itemValue "
				+ "  AND i.lang_type  = '"+req.getLangType()+"' "
				+ " LEFT JOIN PLATFORM_PARAMCLASS j ON f.classNo = j.classNo "
				+ " "
				+ " LEFT JOIN platform_basesettemp_modular l on f.eId = l.eId and f.item = l.item " 
				+ " LEFT JOIN PLATFORM_MODULAR_FIXED k ON l.modularID = k.modularID "
				
				+ " WHERE f.eId = '"+eId+"'  and f.status != '0' " 
				+ " ");
		
		if (keyTxt != null && keyTxt.length()>0)
		{
			sqlbuf.append("  AND  ( g.item_name  like '%%"+keyTxt+"%%' OR F.Item  like '%%"+keyTxt+"%%'  )");
		}
		
//		if (paraShop != null && paraShop.length()>0)
//		{
//			sqlbuf.append("  AND  ( c.shopid = '"+paraShop+"' OR o1.org_Name LIKE '%%"+paraShop+"%%' )");
//		}
//		
//		if (paraMachine != null && paraMachine.length()>0)
//		{
//			sqlbuf.append("  AND  ( d.machineid = '"+paraMachine+"' OR e.machinename LIKE '%%"+paraMachine+"%%' )");
//		}
		
		if(!Check.Null(req.getRequest().getModularId())){
			sqlbuf.append("  AND  ( l.modularId = '"+modularId+"' )");
		}
		
		
		sqlbuf.append(" order by priority ) ");
		sqlbuf.append(" where rn>"+startRow+" and rn<="+(startRow+pageSize));
		
		sqlbuf.append("  ");
		
		sql=sqlbuf.toString();
		return sql;

	}

}
