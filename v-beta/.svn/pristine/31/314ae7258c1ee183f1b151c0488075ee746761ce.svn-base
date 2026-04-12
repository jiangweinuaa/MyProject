package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ParaDefineQueryReq;
import com.dsc.spos.json.cust.res.DCP_ParaDefineQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服務函數：ParaDefineGet
 *   說明：参数定义查询
 * 服务说明：参数定义查询
 * @author Jinzma 
 * @since  2017-03-03
 */
public class DCP_ParaDefineQuery extends SPosBasicService<DCP_ParaDefineQueryReq, DCP_ParaDefineQueryRes> 
{
	@Override
	protected boolean isVerifyFail(DCP_ParaDefineQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		return false;
	}
	
	@Override
	protected TypeToken<DCP_ParaDefineQueryReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_ParaDefineQueryReq>(){};
	}

	@Override
	protected DCP_ParaDefineQueryRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_ParaDefineQueryRes();
	}

	@Override
	protected DCP_ParaDefineQueryRes processJson(DCP_ParaDefineQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		String sql=null;
		DCP_ParaDefineQueryRes res = null;
		res = this.getResponse();
		try{
			sql=this.getQuerySql(req);
			String[] conditionValues = null; //查詢條件
			List<Map<String, Object>> getQData=this.doQueryData(sql, conditionValues);

			int totalRecords;		//总笔数				
			int totalPages;   	//总页数
			if (getQData != null && getQData.isEmpty() == false)
			{ 
				Map<String, Object> oneData_Count = getQData.get(0);
				String num = oneData_Count.get("NUM").toString();
				totalRecords=Integer.parseInt(num);
				//算總頁數
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
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

			res.setDatas(new ArrayList<DCP_ParaDefineQueryRes.level1Elm>());
			//返回给值	
			if (getQData != null && getQData.isEmpty() == false)
			{
				//单头主键字段
				Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
				condition.put("ITEM", true);		

				//调用过滤函数
				List<Map<String, Object>> getQHeader=MapDistinct.getMap(getQData, condition);
				for (Map<String, Object> oneData : getQHeader) 
				{
					DCP_ParaDefineQueryRes.level1Elm oneLv1 = res.new level1Elm();
					oneLv1.setPara(new ArrayList<DCP_ParaDefineQueryRes.level2Elm>());
					oneLv1.setItemlang(new ArrayList<DCP_ParaDefineQueryRes.level2ElmLang>());
					oneLv1.setModulars(new ArrayList<DCP_ParaDefineQueryRes.level3Elm>());
					// 取出第一层		
					String item = oneData.get("ITEM").toString();
					String paraType = oneData.get("TYPE").toString();
					String conType = oneData.get("CONTYPE").toString();
					String defValue = oneData.get("ITEMVALUE").toString();
					String initdefValue = oneData.get("INITDEF").toString();
					String a_status = oneData.get("STATUS").toString();
					String classNO = oneData.get("CLASSNO").toString();
					String priority = oneData.get("PRIORITY").toString();
					String onSale = oneData.get("ONSALE").toString();
					String remark = oneData.get("REMARK").toString();
					String className_CN = oneData.get("CLASSNAME_CN").toString();
					String className_TW = oneData.get("CLASSNAME_TW").toString();
					String className_EN = oneData.get("CLASSNAME_EN").toString();
					
					String m_itemName="";

					String className = "";
					if(req.getLangType().equals("zh_CN")){
						className = className_CN;
					}
					if(req.getLangType().equals("zh_TW")){
						className = className_TW;
					}
					if(req.getLangType().equals("zh_EN")){
						className = className_EN;
					}
					
					
					//调用过滤函数 itemlang 赋值
					condition.clear();
					condition.put("ITEM", true);	
					condition.put("LANG_TYPE", true);	
					List<Map<String, Object>> getQitemlang=MapDistinct.getMap(getQData, condition);
					for (Map<String, Object> itemlang : getQitemlang) 
					{
						if(item.equals(itemlang.get("ITEM")))
						{
							DCP_ParaDefineQueryRes.level2ElmLang oneLv2ElmLang = res.new level2ElmLang();
							String langType=itemlang.get("LANG_TYPE").toString();
							if (Check.Null(langType))
								 continue;
							String itemName=itemlang.get("ITEM_NAME").toString();
							if (langType.equals(req.getLangType()))   //前端未开启多语言参数时，单头还要返回当前多语言对应的参数名
								m_itemName=itemName;
							oneLv2ElmLang.setLangType(langType);
							oneLv2ElmLang.setItemName(itemName);
							oneLv1.getItemlang().add(oneLv2ElmLang);
							oneLv2ElmLang=null;
						}
					}
					
					//设置响应
					oneLv1.setStatus(a_status);
					oneLv1.setDefValue(defValue);
					oneLv1.setInitdefValue(initdefValue);
					oneLv1.setItem(item);	
					oneLv1.setParaType(paraType);				
					oneLv1.setConType(conType);
					oneLv1.setClassNO(classNO);
					oneLv1.setClassName(className);
					oneLv1.setPriority(priority);
					oneLv1.setOnSale(onSale);
					oneLv1.setRemark(remark);
					oneLv1.setItemName(m_itemName);

					//调用过滤函数 para赋值
					condition.clear();
					condition.put("ITEM", true);	
					condition.put("PARAITEMVALUE", true);	
					List<Map<String, Object>> getQpara=MapDistinct.getMap(getQData, condition);
					for (Map<String, Object> oneData2 : getQpara) 
					{
						//过滤属于此单头的明细
						if(item.equals(oneData2.get("ITEM").toString()))
						{
							DCP_ParaDefineQueryRes.level2Elm oneLv2 = res.new level2Elm();
							String itemValue = oneData2.get("PARAITEMVALUE").toString();
							if(Check.Null(itemValue)) continue;
							String status = oneData2.get("PARASTATUS").toString();
							String paraOnSale =oneData2.get("PARAONSALE").toString(); 
							String m_valueName="";

							oneLv2.setValuelang(new ArrayList<DCP_ParaDefineQueryRes.level3ElmLang>());
							//调用过滤函数 valuelang 赋值
							condition.clear();
							condition.put("ITEM", true);
							condition.put("PARAITEMVALUE", true);	
							condition.put("PARALANGTYPE", true);	
							List<Map<String, Object>> getQvaluelang=MapDistinct.getMap(getQData, condition);
							for (Map<String, Object> valuelang : getQvaluelang) 
							{ 
								if(item.equals(valuelang.get("ITEM").toString())&&itemValue.equals(valuelang.get("PARAITEMVALUE").toString()) )
								{
									DCP_ParaDefineQueryRes.level3ElmLang oneLv3ElmLang = res.new level3ElmLang();
									String langType=valuelang.get("PARALANGTYPE").toString();
									if (Check.Null(langType))
										 continue;
									String valueName=valuelang.get("PARANAME").toString();
									if (langType.equals(req.getLangType()))  //前端未开启多语言参数时，单头还要返回当前多语言对应的参数名
										m_valueName=valueName;
									oneLv3ElmLang.setLangType(langType);
									oneLv3ElmLang.setValueName(valueName);
									oneLv2.getValuelang().add(oneLv3ElmLang);
									oneLv3ElmLang=null;
								}
							}
							oneLv2.setStatus(status);
							oneLv2.setItemValue(itemValue);
							oneLv2.setOnSale(paraOnSale);
							oneLv2.setValueName(m_valueName);
							//添加
							oneLv1.getPara().add(oneLv2);
							oneLv2=null;
						}
					}
					
					
					//*************** 增加 modular 节点开始 **************
					
					//调用过滤函数 para赋值
					condition.clear();
					condition.put("ITEM", true);	
					condition.put("MODULARID", true);	
					List<Map<String, Object>> modularDatas =MapDistinct.getMap(getQData, condition);
					for (Map<String, Object> modMap : modularDatas) 
					{
						//过滤属于此单头的明细
						if(item.equals(modMap.get("ITEM").toString()))
						{
							DCP_ParaDefineQueryRes.level3Elm oneLv3 = res.new level3Elm();
							String modularId = modMap.getOrDefault("MODULARID","").toString();
							if(Check.Null(modularId)){
								continue;
							}
							String modularName = modMap.getOrDefault("MODULARNAME","").toString();
							oneLv3.setModularId(modularId);
							oneLv3.setModularName(modularName);
							oneLv1.getModulars().add(oneLv3);
						}
						
					}
					
					//*************** 增加modular 节点结束 ************
					
					
					
					//添加
					res.getDatas().add(oneLv1);
					oneLv1=null;
				}
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
		// TODO 自动生成的方法存根

	}

	@Override
	protected String getQuerySql(DCP_ParaDefineQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		String sql=null;		
		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();
		String Type= req.getRequest().getParaType();
		String keyTxt=req.getRequest().getKeyTxt();
		String langType = req.getLangType();
		String eId = req.geteId();
		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;    
		StringBuffer sqlbuf=new StringBuffer("");

		sqlbuf.append(" select c.*,d.lang_type,d.item_name,e.itemvalue as paraitemvalue,e.onsale as paraOnsale,e.status as parastatus,"
				+ " f.lang_type as paralangtype,f.value_name as paraname ,"
				+ "  className_CN , className_TW,  className_EN "
				+ " from ("
				+ " select num,EID ,item ,ITEMVALUE,initdef,"
//				+ "case when type='2' then 1 else type end as type,"
				+ "type,"
				+ "contype,classno,"
				+ " status ,priority,Onsale,REMARK, modularId, modularName "
				+ " , className as className_CN , className_TW,  className_EN"
				+ " from ( "
				+ " select count(*)over() num ,row_number() over( order by  a.priority, a.classNO , a.item ) as rn, a.* "
				+ " , g.MODULARID , h.modularName , i.className , i.className_TW, i.className_EN   "
				+ " from Platform_BaseSetTemp a "
				+ " left join platform_basesettemp_lang b on a.EID=b.EID and a.item=b.item and b.lang_type='"+langType+"'  "

				// 2020-04-13 增加 platform_basesettemp_modular 和 modular_fixed 
				+ " LEFT JOIN platform_basesettemp_modular g ON a.EID = g.EID AND a.item = g.item "
				+ " LEFT JOIN PLATFORM_MODULAR_FIXED h ON g.modularID = h.modularID "
				+ " LEFT JOIN PLATFORM_PARAMCLASS i ON  a.classNo = i.classNO "
				
				+ " where a.EID='"+eId+"' " ) ;
		if (!Check.Null(Type))
		{
//			if(Type.equals("1"))
//			{
//				sqlbuf.append( " and a.type in ( '1','2') ");
//			}else
//			{
//				sqlbuf.append(" and a.type='"+Type +"'  ");
//			}
			
			sqlbuf.append(" and a.type='"+Type +"'  ");
			
		}
		if (keyTxt != null && keyTxt.length()>0)
		{
			sqlbuf.append(" and (upper(b.item) like '%%"+ keyTxt.toUpperCase() +"%%' or b.item_name like '%%"+ keyTxt +"%%' )");
		}  
		
		if (!Check.Null(req.getRequest().getModularId()))
		{
			sqlbuf.append(" and g.modularid = '"+req.getRequest().getModularId()+"' ");
		}
		
		sqlbuf.append(" order by priority ) b  where rn>" + startRow + "  and rn <= " + (startRow+pageSize) + ") c "
				+ " left join Platform_BaseSetTemp_lang d on c.EID=d.EID and c.item=d.item "
				+ " left join Platform_BaseSetTemp_Para e on c.EID=e.EID and c.item=e.item "
				+ " Left join Platform_BaseSetTemp_Para_lang f on c.EID=f.EID and c.item=f.item and d.lang_type=f.lang_type and e.itemvalue=f.itemvalue "
//				// 2020-04-13 增加 platform_basesettemp_modular 和 modular_fixed 
//				+ " LEFT JOIN platform_basesettemp_modular g ON c.EID = g.EID AND c.item = g.item "
//				+ " LEFT JOIN PLATFORM_MODULAR_FIXED h ON g.modularID = h.modularID "
				+ "  where 1=1 " );
		
		sqlbuf.append( " order by   c.priority , classNo , c.item ");	      

		sql = sqlbuf.toString();
		return sql;
	}
}
