package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_ParaSetQueryReq;
import com.dsc.spos.json.cust.res.DCP_ParaSetQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_ParaSetQuery extends SPosBasicService<DCP_ParaSetQueryReq,DCP_ParaSetQueryRes>
{
	@Override
	protected boolean isVerifyFail(DCP_ParaSetQueryReq req) throws Exception 
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
	protected TypeToken<DCP_ParaSetQueryReq> getRequestType() 
	{	
		return new TypeToken<DCP_ParaSetQueryReq>(){};
	}

	@Override
	protected DCP_ParaSetQueryRes getResponseType() 
	{		
		return new DCP_ParaSetQueryRes();
	}

	@Override
	protected DCP_ParaSetQueryRes processJson(DCP_ParaSetQueryReq req) throws Exception 
	{	
		String sql=null;
		DCP_ParaSetQueryRes res = null;
		res = this.getResponse();
		try {

			sql=this.getQuerySql(req);
			String[] conditionValues1 = {}; //查詢條件
			List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, conditionValues1);
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
			{
				//单头主键字段
				Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
				condition.put("ITEM", true);		
				//调用过滤函数
				List<Map<String, Object>> getQHeader=MapDistinct.getMap(getQDataDetail, condition);
				res.setDatas(new ArrayList<DCP_ParaSetQueryRes.level1Elm>());
				for (Map<String, Object> oneData : getQHeader) 
				{
					DCP_ParaSetQueryRes.level1Elm oneLv1 = res.new level1Elm();
					oneLv1.setPara(new ArrayList<DCP_ParaSetQueryRes.level2Elm>());
					oneLv1.setItemlang(new ArrayList<DCP_ParaSetQueryRes.level2ElmLang>());
					// 取出第一层
					//String machine = oneData.get("MACHINE").toString();
					String item = oneData.get("ITEM").toString();
					String itemName = oneData.get("ITEM_NAME").toString();
					String def = oneData.get("DEF").toString();
					String type = oneData.get("TYPE").toString();
					String contype = oneData.get("CONTYPE").toString();
					String defined = oneData.get("DEFINED").toString();	
					String classNO = oneData.get("CLASSNO").toString();			


//					for (Map<String, Object> oneData2 : getQDataDetail) 
//					{
//						//过滤属于此单头的明细
//						if( item.equals(oneData2.get("ITEM"))==true )
//						{
//							ParaSetGetDCPRes.level2Elm oneLv2 = res.new level2Elm();
//							String itemValue = oneData2.get("ITEMVALUE").toString();
//							String valueName = oneData2.get("VALUE_NAME").toString();
//
//							oneLv2.setItemValue(itemValue);
//							oneLv2.setValueName(valueName);
//							//添加
//							oneLv1.getPara().add(oneLv2);
//							oneLv2=null;
//						}						
//					}
					
					
					//调用过滤函数 itemlang 赋值
					String m_itemName="";
					
					condition.clear();
					condition.put("ITEM", true);	
					condition.put("LANG_TYPE", true);	
					List<Map<String, Object>> getQitemlang=MapDistinct.getMap(getQDataDetail, condition);
					for (Map<String, Object> itemlang : getQitemlang) 
					{
						if(item.equals(itemlang.get("ITEM")))
						{
							DCP_ParaSetQueryRes.level2ElmLang oneLv2ElmLang = res.new level2ElmLang();
							String langType=itemlang.get("LANG_TYPE").toString();
							if (Check.Null(langType))
								 continue;
							String itemName_lang = itemlang.get("ITEM_NAME").toString();
							if (langType.equals(req.getLangType()))   //前端未开启多语言参数时，单头还要返回当前多语言对应的参数名
								m_itemName=itemName_lang;
							oneLv2ElmLang.setLangType(langType);
							oneLv2ElmLang.setItemName(itemName_lang);
							oneLv1.getItemlang().add(oneLv2ElmLang);
							oneLv2ElmLang=null;
						}
					}
					
					//调用过滤函数 para赋值
					condition.clear();
					condition.put("ITEM", true);	
					condition.put("PARAITEMVALUE", true);	
					List<Map<String, Object>> getQpara=MapDistinct.getMap(getQDataDetail, condition);
					for (Map<String, Object> oneData2 : getQpara) 
					{
						//过滤属于此单头的明细
						if(item.equals(oneData2.get("ITEM").toString()))
						{
							DCP_ParaSetQueryRes.level2Elm oneLv2 = res.new level2Elm();
							String itemValue = oneData2.get("PARAITEMVALUE").toString();
							if(Check.Null(itemValue)) continue;
							String status = oneData2.get("PARASTATUS").toString();
							String paraOnSale =oneData2.get("PARAONSALE").toString(); 
							String m_valueName="";

							oneLv2.setValuelang(new ArrayList<DCP_ParaSetQueryRes.level3ElmLang>());
							//调用过滤函数 valuelang 赋值
							condition.clear();
							condition.put("ITEM", true);
							condition.put("PARAITEMVALUE", true);	
							condition.put("PARALANGTYPE", true);	
							List<Map<String, Object>> getQvaluelang=MapDistinct.getMap(getQDataDetail, condition);
							for (Map<String, Object> valuelang : getQvaluelang) 
							{ 
								if(item.equals(valuelang.get("ITEM").toString())&&itemValue.equals(valuelang.get("PARAITEMVALUE").toString()) )
								{
									DCP_ParaSetQueryRes.level3ElmLang oneLv3ElmLang = res.new level3ElmLang();
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
					
					
					oneLv1.setItem(item);
					oneLv1.setItemName(m_itemName);
					oneLv1.setDef(def);
					oneLv1.setParaType(type);
					oneLv1.setConType(contype);
					oneLv1.setDefined(defined);
					oneLv1.setClassNO(classNO);
					
					//添加
					res.getDatas().add(oneLv1);
					oneLv1=null;
				}
			}
			else
			{
				res.setDatas(new ArrayList<DCP_ParaSetQueryRes.level1Elm>());
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
	protected String getQuerySql(DCP_ParaSetQueryReq req) throws Exception 
	{

		String sql=null;
		String langType=req.getLangType();
		String eId=req.geteId();
		String paraType= req.getParaType();
		String paraShop= req.getParaShop();
		String paraMachine= req.getParaMachine();
		
		if(paraType==null) paraType="";
		if(paraShop==null) paraShop="";
		if(paraMachine==null) paraMachine=" ";

		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append("select SHOPID,machine,item,item_name,def,classno,itemvalue ,value_name ,case when type='2' then 1 else type end as type ,contype,defined,priority "
				+ " ,  modularid , modularName "
				+ " , paralangtype , paraname , lang_type , paraitemvalue ,  paraOnsale , parastatus "
				+ " from ( "
				+ " select  c.SHOPID,c.machine,a.item,b.item_name,nvl(c.itemValue,a.def) def, a.classno , d.itemvalue ,e.value_name ,a.type , "
				+ " a.contype,  case when  c.itemValue is null  THEN 'N' ELSE 'Y' end  as defined,a.priority  "
				+ " ,  g.modularid , h.modularName "
				+ " , e.lang_type as paralangtype,e.value_name as paraname "
				+ " , b.lang_type "
				+ " , d.itemvalue as paraitemvalue,d.onsale as paraOnsale,d.status as parastatus  "
				+ " from  platform_basesettemp a "
				+ " left join Platform_BaseSetTemp_lang b on a.EID=b.EID and a.item=b.item and b.lang_type='" + langType +"' "
				+ " left join Platform_BaseSet c on a.item=c.item and a.EID=c.EID and c.SHOPID='" + paraShop +"'" );
		
		if (!Check.Null(paraMachine))
		{
			sqlbuf.append( " and c.machine='" + paraMachine +"'  ");
		}

		sqlbuf.append( " and c.status='100' "	
				+ " left join Platform_BaseSetTemp_Para d on a.item=d.item and a.EID=d.EID "	
				+ " left join Platform_BaseSetTemp_Para_lang e on d.item=e.item and d.itemvalue=e.itemvalue and e.lang_type='" + langType +"' "
				+ " and d.EID=e.EID  "
				+ " LEFT JOIN platform_basesettemp_modular g ON a.EID = g.EID AND a.item = g.item  "
				+ " LEFT JOIN PLATFORM_MODULAR_FIXED h ON g.modularID = h.modularID  "
				+ " where a.EID='" + eId +"' and a.status='100' " );
		if (!Check.Null(paraType))
		{
			if(paraType.equals("1"))
			{
				sqlbuf.append( " and a.TYPE in ( '1','2') ");
			}
			else
			{
				sqlbuf.append( " and a.TYPE='" + paraType +"' ");
			}
		}
		sqlbuf.append( " ) where 1=1  ");
		
		if(req.getKeyTxt() != null && req.getKeyTxt().trim().length() > 0 ){
			sqlbuf.append("  AND (  ITEM LIKE  '%%"+req.getKeyTxt()+"%%'  OR item_Name LIKE '%%"+req.getKeyTxt()+"%%'  )  ");
		}
		
		if(req.getModularId() != null && req.getModularId().trim().length() > 0 ){
			sqlbuf.append("  AND modularId = '"+req.getModularId()+"' ");
		}
		
		sqlbuf.append(  "order by CAST (classno as int),priority,item,itemvalue ");	
		
		sql=sqlbuf.toString();
		return sql;

	}

}
