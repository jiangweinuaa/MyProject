package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_ParaTemplateRetrieveReq;
import com.dsc.spos.json.cust.res.DCP_ParaTemplateRetrieveRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

/**
 * V3模版参数设置详情查询#add
 * @author 2020-06-01
 *
 */
public class DCP_ParaTemplateRetrieve extends SPosBasicService<DCP_ParaTemplateRetrieveReq, DCP_ParaTemplateRetrieveRes> {

	@Override
	protected boolean isVerifyFail(DCP_ParaTemplateRetrieveReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；	
		if (Check.Null(req.getRequest().getModularId())) 
		{
			errCt++;
			errMsg.append("作业代号modularId不可为空值, ");
			isFail = true;
		} 
		
		if (Check.Null(req.getRequest().getTemplateId())) 
		{
			errCt++;
			errMsg.append("模板代号templateId不可为空值, ");
			isFail = true;
		} 

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
		
	}

	@Override
	protected TypeToken<DCP_ParaTemplateRetrieveReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_ParaTemplateRetrieveReq>(){};
	}

	@Override
	protected DCP_ParaTemplateRetrieveRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_ParaTemplateRetrieveRes();
	}

	@Override
	protected DCP_ParaTemplateRetrieveRes processJson(DCP_ParaTemplateRetrieveReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_ParaTemplateRetrieveRes res = null;
		res = this.getResponse();
		
		try {
			
			String langType = req.getLangType();
			
			String sql = this.getQuerySql(req);
			List<Map<String, Object>> getQData=this.doQueryData(sql, null);

			int totalRecords = 0;		//总笔数				
			int totalPages = 0;   	//总页数
			if (getQData != null && getQData.isEmpty() == false)
			{ 
				Map<String, Object> oneData_Count = getQData.get(0);
				String num = oneData_Count.get("NUM").toString();
				totalRecords=Integer.parseInt(num);
				//算總頁數
				if(req.getPageSize() != 0 && req.getPageNumber() != 0){
					totalPages = totalRecords / req.getPageSize();
					totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
				}
				
			}
			else
			{
				totalRecords = 0;
				totalPages = 0;
			}
			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);
			
			DCP_ParaTemplateRetrieveRes.level1Elm lv1 = res.new level1Elm(); 
			
			//返回给值	
			if (getQData != null && getQData.isEmpty() == false)
			{
				//datas 节点
				Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
				condition.put("TEMPLATEID", true);		
				//调用过滤函数
				List<Map<String, Object>> getQHeader=MapDistinct.getMap(getQData, condition);
				
				//ClassList 节点
				Map<String, Boolean> ClassCon = new HashMap<String, Boolean>(); //查詢條件
				ClassCon.put("TEMPLATEID", true);
				ClassCon.put("CLASSNO", true);	
				//调用过滤函数
				List<Map<String, Object>> classDatas = MapDistinct.getMap(getQData, ClassCon);
				
				
				//shopList 节点
				Map<String, Boolean> shopCon = new HashMap<String, Boolean>(); //查詢條件
				shopCon.put("TEMPLATEID", true);
				shopCon.put("SHOPID", true);	
				//调用过滤函数
				List<Map<String, Object>> shopDatas = MapDistinct.getMap(getQData, shopCon);
				
				
				//machineList 节点
				Map<String, Boolean> machineCon = new HashMap<String, Boolean>(); //查詢條件
				machineCon.put("TEMPLATEID", true);
				machineCon.put("MACHINEID", true);	
				//调用过滤函数
				List<Map<String, Object>> machineDatas = MapDistinct.getMap(getQData, machineCon);
				
				//单头主键字段
				Map<String, Boolean> condition2 = new HashMap<String, Boolean>(); //查詢條件
				condition2.put("TEMPLATEID", true);
				condition2.put("CLASSNO", true);
				condition2.put("ITEM", true);	
				//调用过滤函数
				List<Map<String, Object>> itemListDatas = MapDistinct.getMap(getQData, condition2);
				
				//单头主键字段
				Map<String, Boolean> condition3 = new HashMap<String, Boolean>(); //查詢條件
				condition3.put("TEMPLATEID", true);
				condition3.put("CLASSNO", true);
				condition3.put("ITEM", true);	
				condition3.put("PARAITEMVALUE", true);
				//调用过滤函数
				List<Map<String, Object>> paraDatas = MapDistinct.getMap(getQData, condition3);
				
				
				Map<String, Boolean> condition4 = new HashMap<String, Boolean>(); //查詢條件
				condition4.put("TEMPLATEID", true);
				condition4.put("CLASSNO", true);
				condition4.put("ITEM", true);	
				condition4.put("PARAITEMVALUE", true);
				condition4.put("LANGTYPE", true);
				//调用过滤函数
				List<Map<String, Object>> paraLangDatas = MapDistinct.getMap(getQData, condition4);
				
				//单头主键字段
				Map<String, Boolean> condition5 = new HashMap<String, Boolean>(); //查詢條件
				condition5.put("TEMPLATEID", true);
				condition5.put("CLASSNO", true);
				condition5.put("ITEM", true);	
				condition5.put("PARALANGTYPE", true);
				//调用过滤函数
				List<Map<String, Object>> itemLangDatas = MapDistinct.getMap(getQData, condition5);
				
				for (Map<String, Object> oneData : getQHeader) 
				{
					String templateId = oneData.get("TEMPLATEID").toString();
					if(Check.Null(templateId)){
						continue;
					}
					
					String templateName = oneData.get("TEMPLATENAME").toString();
					String templateType = oneData.get("TEMPLATETYPE").toString();
					String restrictShop = oneData.get("RESTRICTSHOP").toString();
					String restrictMachine = oneData.get("RESTRICTMACHINE").toString();
					lv1.setTemplateId(templateId);
					lv1.setTemplateName(templateName);
					lv1.setTemplateType(templateType);
					lv1.setRestrictShop(restrictShop);
					lv1.setRestrictMachine(restrictMachine);
					
					lv1.setShopList(new ArrayList<DCP_ParaTemplateRetrieveRes.ShopList>());

					lv1.setMachineList(new ArrayList<DCP_ParaTemplateRetrieveRes.MachineList>());
					
					lv1.setClassList(new ArrayList<DCP_ParaTemplateRetrieveRes.ClassList>() );
//					lv1.setItemList(new ArrayList<DCP_ParaTemplateRetrieveRes.ItemList>());
					
					if(shopDatas != null && !shopDatas.isEmpty()){
						for (Map<String, Object> shopMap : shopDatas) {
							
							if(templateId.equals(shopMap.get("TEMPLATEID").toString())){
								DCP_ParaTemplateRetrieveRes.ShopList lvShop = res.new ShopList();
								String shopId = shopMap.get("SHOPID").toString();
								String shopName = shopMap.get("SYSHOPNAME").toString();
								if(Check.Null(shopId)){
									continue;
								}
								
								lvShop.setShopId(shopId);
								lvShop.setShopName(shopName);
								lv1.getShopList().add(lvShop);
							}
							
						}
						
					}
					
					if(machineDatas != null && !machineDatas.isEmpty()){
						for (Map<String, Object> machineMap : machineDatas) {
							
							if(templateId.equals(machineMap.get("TEMPLATEID").toString())){
								DCP_ParaTemplateRetrieveRes.MachineList lvMachine = res.new MachineList();
								String machineId = machineMap.get("MACHINEID").toString();
								String machineName = machineMap.get("MACHINENAME").toString();
								
								if(Check.Null(machineId)){
									continue;
								}
								
								String machineShopId = machineMap.get("MACHINESHOPID").toString();
								String machineShopName = machineMap.get("MACHINESHOPNAME").toString();
								
								
								lvMachine.setMachineId(machineId);
								lvMachine.setMachineName(machineName);
								lvMachine.setShopId(machineShopId);
								lvMachine.setShopName(machineShopName);
								lv1.getMachineList().add(lvMachine);
							}
							
						}
						
					}
					
					
					/**
					 * classList 节点
					 */
					if(classDatas != null && !classDatas.isEmpty()){
						for (Map<String, Object> classMap : classDatas) {
							if(templateId.equals(classMap.get("TEMPLATEID").toString())){
								DCP_ParaTemplateRetrieveRes.ClassList lvClass = res.new ClassList();
								String classNo = classMap.get("CLASSNO").toString();
								String className = classMap.get("CLASSNAME").toString();
								lvClass.setClassName(className);
								lvClass.setClassNo(classNo);
								
								lvClass.setItemList(new ArrayList<DCP_ParaTemplateRetrieveRes.ItemList>());
								
								for (Map<String, Object> itemMap : itemListDatas) {

									if(itemMap.get("TEMPLATEID").equals(templateId ) && classNo.equals(itemMap.get("CLASSNO").toString())){
										
										DCP_ParaTemplateRetrieveRes.ItemList itemLv = res.new ItemList();
										String item = itemMap.get("ITEM").toString();
										String conType = itemMap.get("CONTYPE").toString();
										String itemValue = itemMap.get("ITEMVALUE").toString();
										String remark = itemMap.get("REMARK").toString();
										
										String m_itemName = "";
										itemLv.setPara(new ArrayList<DCP_ParaTemplateRetrieveRes.level2Elm>());
										itemLv.setItemlang(new ArrayList<DCP_ParaTemplateRetrieveRes.level2ElmLang>()); 
										
										// para层,参数值, 参数值名称
										if(paraDatas != null && !paraDatas.isEmpty()){
											for (Map<String, Object> paraMap : paraDatas) {
												if(paraMap.get("TEMPLATEID").equals(templateId) && classNo.equals(paraMap.get("CLASSNO").toString()) &&  item.equals(paraMap.get("ITEM").toString())){
													
													DCP_ParaTemplateRetrieveRes.level2Elm lv2 = res.new level2Elm();
													String paraItemValue = paraMap.get("PARAITEMVALUE").toString();
													String status = paraMap.get("STATUS").toString();
													String onSale = paraMap.get("ONSALE").toString();
													String valueName = "";
//													String cnfflg = "N";
//													if(!Check.Null(status) && status.equals("100")) {
//														cnfflg = "Y";
//													}
													
													lv2.setValuelang(new ArrayList<DCP_ParaTemplateRetrieveRes.level3ElmLang>());
													
													if(paraLangDatas != null && !paraLangDatas.isEmpty() ){
														
														for (Map<String, Object> paraLang : paraLangDatas) {
															if(item.equals(paraLang.get("ITEM").toString()) 
																	&& templateId.equals(paraLang.get("TEMPLATEID").toString()) 
																	&& paraItemValue.equals(paraLang.get("PARAITEMVALUE").toString() )){
																
																DCP_ParaTemplateRetrieveRes.level3ElmLang lv3Lang = res.new level3ElmLang(); 
																
																String valueLangType = paraLang.get("LANGTYPE").toString();
																String paraValueName = paraLang.get("VALUENAME").toString();
																
																if(langType.equals(valueLangType)){
																	valueName = paraValueName;
																}
																lv3Lang.setLangType(valueLangType);
																lv3Lang.setValueName(paraValueName);
																lv2.getValuelang().add(lv3Lang);
																
															}
															
														}
														
													}
													
													lv2.setItemValue(paraItemValue);
													lv2.setValueName( valueName );
													lv2.setStatus(status);
													lv2.setOnSale(onSale);
													itemLv.getPara().add(lv2);
													
												}
												
											}
											
										}
										
										if(itemLangDatas != null && !itemLangDatas.isEmpty() ){
											
											for (Map<String, Object> itemLangMap : itemLangDatas) {
												
												if(itemLangMap.get("TEMPLATEID").equals(templateId) && item.equals(itemLangMap.get("ITEM").toString())){
													DCP_ParaTemplateRetrieveRes.level2ElmLang lv2Lang = res.new level2ElmLang();
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
						
					}
					
//					res.getDatas().add(lv1);
					res.setDatas(lv1);
				}
				// getQHeader 结束
			}
			
			res.setServiceStatus("000");
			res.setSuccess(true);
			res.setServiceDescription("服务执行成功");
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setServiceDescription("服务执行失败!"+e.getMessage());
			res.setServiceStatus("200");
			res.setSuccess(false);
		}
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_ParaTemplateRetrieveReq req) throws Exception {
		// TODO Auto-generated method stub
		
		String sql = "";
		
		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();

		if(pageNumber ==0 || pageSize == 0 ){
			pageNumber = 1;
			pageSize = 99999;
		}
		
		//分页起始位置
		int startRow=(pageNumber-1) * pageSize;
		String eId = req.geteId();
		
		String paraShop = req.getRequest().getParaShop();
		String paraMachine = req.getRequest().getParaMachine();
		String modularId = req.getRequest().getModularId();
//		String paraItem = req.getRequest().getParaItem();
		String templateId = req.getRequest().getTemplateId();
		
		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append(" "
				+ " select * from ( "
				+ " SELECT  "
				+ "  COUNT(DISTINCT l.item ) OVER() NUM , dense_rank() OVER(ORDER BY A.TEMPLATEID , l.item) AS RN, "
				+ " a.eId, a.templateid ,  a.templatetype , a.templatename , a.restrictshop , a.restrictmachine , "
				+ " F.item , NVL(b.itemValue,f.itemValue) AS itemValue, c.shopid ,  o1.org_name AS syShopName   , d.shopid as machineShopId, o2.org_name AS machineShopName , d.machineid , e.machinename  ,  "
				+ " f.contype , g.lang_Type AS paraLangType   ,  g.item_name AS itemName , f.remark ,   f.classNo , j.className ,  "
				+ " h.itemValue AS paraItemValue , h.status , nvl(h.onSale , 'N') AS onSale ,"
				+ " i.lang_type AS LangType , i.value_name AS ValueName ,"
				+ " l.modularId , k.modularName "
				+ " FROM PLATFORM_PARATEMPLATE a "
				//+ " LEFT JOIN  PLATFORM_PARATEMPLATE_ITEM b ON a.eId = b.eId AND a.templateid = b.templateid "
				+ " LEFT JOIN PLATFORM_PARATEMPLATE_SHOP c ON a.eId = c.Eid AND a.templateid = c.templateid "
				+ " LEFT JOIN PLATFORM_PARATEMPLATE_MACHINE d ON a.eid = d.eid AND a.templateid = d.templateid "
				+ " LEFT JOIN DCP_ORG_lang o1 ON c.eid = o1.eid AND c.shopid = o1.organizationno AND o1.lang_type = '"+req.getLangType()+"' "
				+ " LEFT JOIN DCP_ORG_lang o2 ON d.eid = o2.eid AND d.shopid = o2.organizationno AND o2.lang_type = '"+req.getLangType()+"' "
				+ " LEFT JOIN platform_machine e ON d.eid = e.eid AND d.shopid = e.shopid AND d.machineid = e.machine "
				
				+ " LEFT JOIN PLATFORM_BASESETTEMP_MODULAR L  ON A.EID = L.EID AND A.TEMPLATETYPE = L.MODULARID "
				+ " LEFT JOIN PLATFORM_PARATEMPLATE_ITEM B  ON A.EID = B.EID  AND B.EID = L.EID AND A.TEMPLATEID = B.TEMPLATEID AND L.ITEM = B.ITEM "
				
				+ " LEFT JOIN Platform_BaseSetTemp f ON f.eId = L.eId AND f.item = L.item "
				+ " LEFT JOIN Platform_BaseSetTemp_lang g ON f.eid = g.eid AND f.item = g.item AND g.lang_type = '"+req.getLangType()+"' "
				+ " LEFT JOIN Platform_BaseSetTemp_Para h ON f.eid = h.eid AND f.item = h.item "
				+ " LEFT JOIN Platform_BaseSetTemp_Para_lang i ON h.eid = i.eid AND h.item = i.item and h.itemValue = i.itemValue  AND i.lang_type  = '"+req.getLangType()+"'"
				+ " LEFT JOIN PLATFORM_PARAMCLASS j ON f.classNo = j.classNo "
				
				+ " LEFT JOIN PLATFORM_MODULAR_FIXED k ON l.modularID = k.modularID "
				
				+ " WHERE a.eId = '"+eId+"' "
				+ " and f.status = '100' " //2020-07-28 根据SA黄玲霞的需求， 只返回已启用的参数 
//				+ " AND  ( c.shopid = '1001' OR o1.org_Name LIKE '%%门店%%' ) "
//				+ " AND  ( d.machineid = 'M01' OR e.machinename LIKE '%%机台%%' ) "
//				+ " AND  ( b.item = '%%参数%%' )  "
				+ " ");
		

		if (paraShop != null && paraShop.length()>0)
		{
			sqlbuf.append(" AND ( a.restrictshop = '0' OR ( a.restrictshop = '1' AND c.shopid = '"+paraShop+"' ) OR (a.restrictshop = '2' AND c.shopid !='"+paraShop+"' )   )  ");
		}
		
		if (paraMachine != null && paraMachine.length()>0)
		{
			sqlbuf.append(" AND ( a.restrictMachine = '0' OR ( a.restrictMachine = '1' AND d.MACHINEID = '"+paraMachine+"' ) OR (a.restrictMachine = '2' AND d.MACHINEID != '"+paraMachine+"' )   )   ");
		}
		
//		if (paraItem != null && paraItem.length()>0)
//		{
//			sqlbuf.append("  AND  ( L.item = '"+paraItem+"' )");
//		}
		
		if (templateId != null && templateId.length()>0)
		{
			sqlbuf.append("  AND  ( a.templateId = '"+templateId+"' )");
		}
		
		if(!Check.Null(req.getRequest().getModularId())){
			sqlbuf.append("  AND  ( l.modularId = '"+modularId+"' )");
		}
		
		sqlbuf.append(" ) ");
		sqlbuf.append(" where rn>"+startRow+" and rn<="+(startRow+pageSize));
		
		sqlbuf.append("  ");
		
		sql = sqlbuf.toString();
		return sql;
	}

}
