package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_ParaTemplateQueryReq;
import com.dsc.spos.json.cust.res.DCP_ParaTemplateQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

/**
 * V3模版参数设置列表查询#add
 * @author yuanyy 2020-06-01
 *
 */
public class DCP_ParaTemplateQuery extends SPosBasicService<DCP_ParaTemplateQueryReq, DCP_ParaTemplateQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_ParaTemplateQueryReq req) throws Exception {
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

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_ParaTemplateQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_ParaTemplateQueryReq>(){};
	}

	@Override
	protected DCP_ParaTemplateQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_ParaTemplateQueryRes();
	}

	@Override
	protected DCP_ParaTemplateQueryRes processJson(DCP_ParaTemplateQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_ParaTemplateQueryRes res = null;
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

			res.setDatas(new ArrayList<DCP_ParaTemplateQueryRes.level1Elm>());
			//返回给值	
			if (getQData != null && getQData.isEmpty() == false)
			{
				//单头主键字段
				Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
				condition.put("TEMPLATEID", true);		
				//调用过滤函数
				List<Map<String, Object>> getQHeader=MapDistinct.getMap(getQData, condition);
				
				
				//单头主键字段
				Map<String, Boolean> condition2 = new HashMap<String, Boolean>(); //查詢條件
				condition2.put("TEMPLATEID", true);
				condition2.put("ITEM", true);	
				//调用过滤函数
				List<Map<String, Object>> itemListDatas = MapDistinct.getMap(getQData, condition2);
				
				
				//单头主键字段
				Map<String, Boolean> condition3 = new HashMap<String, Boolean>(); //查詢條件
				condition3.put("TEMPLATEID", true);
				condition3.put("ITEM", true);	
				condition3.put("PARAITEMVALUE", true);
				//调用过滤函数
				List<Map<String, Object>> paraDatas = MapDistinct.getMap(getQData, condition3);
				
				
				Map<String, Boolean> condition4 = new HashMap<String, Boolean>(); //查詢條件
				condition4.put("TEMPLATEID", true);
				condition4.put("ITEM", true);	
				condition4.put("PARAITEMVALUE", true);
				condition4.put("LANGTYPE", true);
				//调用过滤函数
				List<Map<String, Object>> paraLangDatas = MapDistinct.getMap(getQData, condition4);
				
				//单头主键字段
				Map<String, Boolean> condition5 = new HashMap<String, Boolean>(); //查詢條件
				condition5.put("TEMPLATEID", true);
				condition5.put("ITEM", true);	
				condition5.put("PARALANGTYPE", true);
				//调用过滤函数
				List<Map<String, Object>> itemLangDatas = MapDistinct.getMap(getQData, condition5);
				
				for (Map<String, Object> oneData : getQHeader) 
				{
					DCP_ParaTemplateQueryRes.level1Elm lv1 = res.new level1Elm();
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
					
					lv1.setItemList(new ArrayList<DCP_ParaTemplateQueryRes.ItemList>());
				
					for (Map<String, Object> itemMap : itemListDatas) 
					{
						if(itemMap.get("TEMPLATEID").equals(templateId )){
							
							DCP_ParaTemplateQueryRes.ItemList itemLv = res.new ItemList();
							String item = itemMap.get("ITEM").toString();
							String conType = itemMap.get("CONTYPE").toString();
							String itemValue = itemMap.get("TRUEITEMVALUE").toString();
							String remark = itemMap.get("REMARK").toString();
							
							String m_itemName = "";
							itemLv.setPara(new ArrayList<DCP_ParaTemplateQueryRes.level2Elm>());
							itemLv.setItemlang(new ArrayList<DCP_ParaTemplateQueryRes.level2ElmLang>()); 
							
							// para层,参数值, 参数值名称
							if(paraDatas != null && !paraDatas.isEmpty()){
								for (Map<String, Object> paraMap : paraDatas) {
									if(paraMap.get("TEMPLATEID").equals(templateId) && item.equals(paraMap.get("ITEM").toString())){
										
										DCP_ParaTemplateQueryRes.level2Elm lv2 = res.new level2Elm();
										String paraItemValue = paraMap.get("PARAITEMVALUE").toString();
										String status = paraMap.get("STATUS").toString();
										String onSale = paraMap.get("ONSALE").toString();
										String valueName = "";
										String cnfflg = "N";
										if(!Check.Null(status) && status.equals("100")) {
											cnfflg = "Y";
										}
										
										lv2.setValuelang(new ArrayList<DCP_ParaTemplateQueryRes.level3ElmLang>());
										
										if(paraLangDatas != null && !paraLangDatas.isEmpty() ){
											
											for (Map<String, Object> paraLang : paraLangDatas) {
												if(item.equals(paraLang.get("ITEM").toString()) 
														&& templateId.equals(paraLang.get("TEMPLATEID").toString()) 
														&& paraItemValue.equals(paraLang.get("PARAITEMVALUE").toString() )){
													
													DCP_ParaTemplateQueryRes.level3ElmLang lv3Lang = res.new level3ElmLang(); 
													
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
										lv2.setCnfflg( cnfflg );
										lv2.setOnSale(onSale);
										itemLv.getPara().add(lv2);
										
									}
									
								}
								
							}
							
							
							
							
							if(itemLangDatas != null && !itemLangDatas.isEmpty() ){
								
								for (Map<String, Object> itemLangMap : itemLangDatas) {
									
									if(itemLangMap.get("TEMPLATEID").equals(templateId) && item.equals(itemLangMap.get("ITEM").toString())){
										DCP_ParaTemplateQueryRes.level2ElmLang lv2Lang = res.new level2ElmLang();
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
							lv1.getItemList().add(itemLv);
//							
							
						}
						
					}
					res.getDatas().add(lv1);
				}
			}
		
			res.setSuccess(true);
			res.setServiceStatus("000");
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
	protected String getQuerySql(DCP_ParaTemplateQueryReq req) throws Exception {
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
		String paraItem = req.getRequest().getParaItem();
		
		String langType = req.getLangType();
		
		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append("select * from (  "
				+ " SELECT  "
				+ " count(DISTINCT a.templateId )over() num , dense_rank() over( order BY a.templateid  ) as rn,  "
				+ " A.TEMPLATEID, A.TEMPLATETYPE, A.TEMPLATENAME, A.RESTRICTSHOP, "
                + " A.RESTRICTMACHINE,  "
				+ " C.SHOPID, "
                + " O1.ORG_NAME AS SYSHOPNAME, D.SHOPID AS MACHINESHOPID, "
                + " O2.ORG_NAME AS MACHINESHOPNAME, D.MACHINEID, E.MACHINENAME, "
				+ " F.CONTYPE, G.LANG_TYPE AS PARALANGTYPE, "
                + " F.REMARK, F.CLASSNO, J.CLASSNAME, "
				+ " G.ITEM_NAME AS ITEMNAME,  B.item AS tempITEM, L.item , B.itemvalue AS TEMPITEMVALUE, "
                + " H.ITEMVALUE AS PARAITEMVALUE, H.STATUS, NVL(b.itemvalue,H.Itemvalue) AS trueItemValue ,  "
                + " NVL(H.ONSALE, 'N') AS ONSALE, I.LANG_TYPE AS LANGTYPE, "
                + " I.VALUE_NAME AS VALUENAME, L.MODULARID, K.MODULARNAME "
								
                + " FROM PLATFORM_PARATEMPLATE A  "
                + " LEFT JOIN PLATFORM_PARATEMPLATE_SHOP C ON A.EID = C.EID AND A.TEMPLATEID = C.TEMPLATEID "
                + " LEFT JOIN PLATFORM_PARATEMPLATE_MACHINE D  ON A.EID = D.EID AND A.TEMPLATEID = D.TEMPLATEID "
                + " LEFT JOIN DCP_ORG_LANG O1  ON C.EID = O1.EID AND C.SHOPID = O1.ORGANIZATIONNO AND O1.LANG_TYPE = '"+langType+"' "
                + " LEFT JOIN DCP_ORG_LANG O2  ON D.EID = O2.EID AND D.SHOPID = O2.ORGANIZATIONNO AND O2.LANG_TYPE = '"+langType+"' "
                + " LEFT JOIN PLATFORM_MACHINE E ON D.EID = E.EID AND D.SHOPID = E.SHOPID AND D.MACHINEID = E.MACHINE  "
                
                + " LEFT JOIN Platform_Basesettemp_Modular L ON a.eId = L.eid AND a.templatetype = L.modularid   "
                + " LEFT JOIN PLATFORM_PARATEMPLATE_ITEM B ON A.EID = B.EID  AND b.eid = l.eId AND A.TEMPLATEID = B.TEMPLATEID AND L.item = b.item  "
                + " LEFT JOIN PLATFORM_BASESETTEMP F ON F.EID = L.EID AND F.ITEM = L.ITEM 	"	
				+ " and f.status = '100' " //2020-07-28 根据SA黄玲霞的需求， 只返回已启用的参数 "
                + " LEFT JOIN PLATFORM_BASESETTEMP_LANG G ON F.EID = G.EID AND F.ITEM = G.ITEM AND G.LANG_TYPE = '"+langType+"' "
                + " LEFT JOIN PLATFORM_BASESETTEMP_PARA H ON F.EID = H.EID AND F.ITEM = H.ITEM  "
                + " LEFT JOIN PLATFORM_BASESETTEMP_PARA_LANG I ON H.EID = I.EID AND H.ITEM = I.ITEM AND H.ITEMVALUE = I.ITEMVALUE  AND I.LANG_TYPE = '"+langType+"'  "
                + " LEFT JOIN PLATFORM_PARAMCLASS J  ON F.CLASSNO = J.CLASSNO  "
                + " LEFT JOIN PLATFORM_MODULAR_FIXED K  ON L.MODULARID = K.MODULARID "
				+ " WHERE a.eId = '"+eId+"' " 
				
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
		
		if (paraItem != null && paraItem.length()>0)
		{
			sqlbuf.append("  AND  ( L.item = '"+paraItem+"'   OR G.Item_Name LIKE '%%"+paraItem+"%%' )");
		}

		if(!Check.Null(req.getRequest().getModularId())){
			sqlbuf.append("  AND a.templatetype = '"+modularId+"' ");
		}
		
		sqlbuf.append(" ) ");
		sqlbuf.append(" where rn>"+startRow+" and rn<="+(startRow+pageSize));
		
		sqlbuf.append("  ");
		
		sql = sqlbuf.toString();
		return sql;
	}

}
