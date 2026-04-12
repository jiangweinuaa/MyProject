package com.dsc.spos.waimai;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import org.json.JSONObject;
import org.springframework.jdbc.core.ResultSetSupportingSqlParameter;

import com.alibaba.fastjson.JSONArray;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.waimai.model.WMJBPGoodsUpdate;
import com.dsc.spos.waimai.model.WMJBPQueryListByEPoiId;
import com.dsc.spos.waimai.model.WMJBPQueryListByEPoiId.Data;
import com.google.gson.reflect.TypeToken;
import com.sankuai.sjst.platform.developer.domain.RequestSysParams;
import com.sankuai.sjst.platform.developer.domain.WaiMaiDishPropertyVO;
import com.sankuai.sjst.platform.developer.request.CipCaterTakeoutDishBaseQueryByEPoiIdRequest;
import com.sankuai.sjst.platform.developer.request.CipCaterTakeoutDishBatchUploadRequest;
import com.sankuai.sjst.platform.developer.request.CipCaterTakeoutDishCatDeleteRequest;
import com.sankuai.sjst.platform.developer.request.CipCaterTakeoutDishCatListQueryRequest;
import com.sankuai.sjst.platform.developer.request.CipCaterTakeoutDishCatUpdateRequest;
import com.sankuai.sjst.platform.developer.request.CipCaterTakeoutDishDeleteRequest;
import com.sankuai.sjst.platform.developer.request.CipCaterTakeoutDishMapRequest;
import com.sankuai.sjst.platform.developer.request.CipCaterTakeoutDishQueryByEPoiIdRequest;
import com.sankuai.sjst.platform.developer.request.CipCaterTakeoutDishStockUpdateRequest;
import com.sankuai.sjst.platform.developer.request.CipCaterTakeoutDishUpdatePropertyRequest;
import com.sankuai.sjst.platform.developer.request.CipCaterTakeoutImageUploadRequest;

public class WMJBPProductService 
{	
	static String jbpLogFileName = "jbpProductlog";
	public static String queryItemByEPoiId(String companyNo,String erpShopNo,StringBuilder errorMessage) throws Exception
	{
		try 
		{
			String appAuthToken = WMJBPOrderProcess.getToken(companyNo, erpShopNo);
			String ePoiId = companyNo + "_" + erpShopNo;

		/*	if(erpShopNo.isEmpty() || erpShopNo.length()==0)//为了测试 
			{
				ePoiId = "tqsp05005";
				appAuthToken = "a1ded8725409d8e6c8b2e82cbb4e1363d7b4a1f4e917e6257074f5a00fb6eab86eb8e1117d2d2569aa6e9faf4f2ed3f7";
			}*/

			RequestSysParams requestSysParams = new RequestSysParams(StaticInfo.waimai_digiwin_ISV_MT_signKey, appAuthToken);
			CipCaterTakeoutDishBaseQueryByEPoiIdRequest request = new CipCaterTakeoutDishBaseQueryByEPoiIdRequest();
			request.setRequestSysParams(requestSysParams);
			request.setePoiId(ePoiId);
			try 
			{			
				// 发送请求，接收Json ，所有Request都有doRequest方法直接调用即可
				HelpTools.writelog_fileName("【聚宝盆】查询门店下商品信息请求：companyNo=" +companyNo+" shopNo="+erpShopNo+" getParams："+request.getParams().toString(),jbpLogFileName);		
				String resultJson = request.doRequest();
				//System.out.println(resultJson);
				//HelpTools.writelog_fileName("【聚宝盆】查询门店下商品信息返回：" + resultJson,jbpLogFileName);
				//JSONObject jsonObject = new JSONObject(resultJson);
				return resultJson;	   	    
			} 
			catch (IOException e) 
			{
				// 处理IO异常
				
				errorMessage.append(e.getMessage());
				return null ;
			} 
			catch (URISyntaxException e) 
			{
				// 处理URI语法异常   
				
				errorMessage.append(e.getMessage());
				return null ;
			}
			catch (Exception e) 
			{
				
				errorMessage.append(e.getMessage());
				return null ;
			}  	

		} 
		catch (Exception e) 
		{
			
			errorMessage.append(e.getMessage());
			return null ;	
		}

	}

	public static boolean dishMapping(String companyNo,String erpShopNo,String dishMappings,StringBuilder errorMessage) throws Exception
	{
		try 
		{
			String appAuthToken = WMJBPOrderProcess.getToken(companyNo, erpShopNo);
			String ePoiId = companyNo + "_" + erpShopNo;
		
			RequestSysParams requestSysParams = new RequestSysParams(StaticInfo.waimaiMTSignKey, appAuthToken);
			CipCaterTakeoutDishMapRequest request = new CipCaterTakeoutDishMapRequest();
			request.setRequestSysParams(requestSysParams);
			request.setePoiId(ePoiId);
			request.setDishMappings(dishMappings);		
			try 
			{			
				// 发送请求，接收Json ，所有Request都有doRequest方法直接调用即可
				HelpTools.writelog_fileName("【聚宝盆】商品映射请求：companyNo=" +companyNo+" shopNo="+erpShopNo+" getParams："+request.getParams().toString(),jbpLogFileName);		
				String resultJson = request.doRequest();
				//HelpTools.writelog_fileName("【聚宝盆】商品映射返回：" + resultJson,jbpLogFileName);
				JSONObject jsonObject = new JSONObject(resultJson);
				try 
				{
					String res = jsonObject.get("data").toString();
					if(res !=null && res.toUpperCase().equals("OK"))
					{
						return true;
					} 
				} 
				catch (Exception e) 
				{
					// TODO: handle exception
				}
				//这里可以解析error
				errorMessage.append(resultJson);
				return false;

			} 
			catch (IOException e) 
			{
				// 处理IO异常
				
				errorMessage.append(e.getMessage());
				return false;
			} 
			catch (URISyntaxException e) 
			{
				// 处理URI语法异常   
				
				errorMessage.append(e.getMessage());
				return false;
			}
			catch (Exception e) 
			{
				
				errorMessage.append(e.getMessage());
				return false;
			}  
		} 
		catch (Exception e) 
		{
			
			errorMessage.append(e.getMessage());
			return false;
		}
	}

	/**
	 * 美团商品分类获取
	 * @param companyNo
	 * @param erpShopNo
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static String queryCatList(String companyNo,String erpShopNo,StringBuilder errorMessage) throws Exception
	{
		/*{
		  "data": [
		    {
		      "ePoiId": "4211",
		      "name": "素菜",
		      "sequence": 0
		    },
		    {
		      "ePoiId": "4211",
		      "name": "new分类",
		      "sequence": 0
		    },
		    {
		      "ePoiId": "4211",
		      "name": "砂锅",
		      "sequence": 2
		    }
		  ]
		}*/
		try 
		{
			String appAuthToken = WMJBPOrderProcess.getToken(companyNo, erpShopNo);
			String ePoiId = companyNo + "_" + erpShopNo;

			RequestSysParams requestSysParams = new RequestSysParams(StaticInfo.waimaiMTSignKey, appAuthToken);		
			CipCaterTakeoutDishCatListQueryRequest request = new CipCaterTakeoutDishCatListQueryRequest();
			request.setRequestSysParams(requestSysParams);

			try 
			{			
				// 发送请求，接收Json ，所有Request都有doRequest方法直接调用即可
				HelpTools.writelog_fileName("【聚宝盆】商品分类请求：companyNo=" +companyNo+" shopNo="+erpShopNo+" getParams："+request.getParams().toString(),jbpLogFileName);		
				String resultJson = request.doRequest();
				HelpTools.writelog_fileName("【聚宝盆】商品分类返回：" + resultJson,jbpLogFileName);		 	    
				return resultJson;

			} 
			catch (IOException e) 
			{
				// 处理IO异常
				
				errorMessage.append(e.getMessage());
				return null;
			} 
			catch (URISyntaxException e) 
			{
				// 处理URI语法异常   
				
				errorMessage.append(e.getMessage());
				return null;
			}
			catch (Exception e) 
			{
				
				errorMessage.append(e.getMessage());
				return null;
			}  

		} 
		catch (Exception e) 
		{
			
			errorMessage.append(e.getMessage());
			return null;

		}

	}

	/**
	 * 美团商品批量上传和更新
	 * @param companyNo
	 * @param erpShopNo
	 * @param dishes
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static boolean batchUpload(String companyNo,String erpShopNo,List<WMJBPGoodsUpdate> dishes,StringBuilder errorMessage) throws Exception
	{
		try 
		{
			String appAuthToken = WMJBPOrderProcess.getToken(companyNo, erpShopNo);
			String ePoiId = companyNo + "_" + erpShopNo;		

//			JSONArray dishesArray = new JSONArray();
//			for(WMJBPGoodsUpdate par : dishes)
//			{
//				ParseJson pj = ParseJson.getInstance();			
//				String par_string  =pj.beanToJson(par);
//				dishesArray.add(par_string);
//			}
			ParseJson pj=new ParseJson();
			
			//String mt_dishesjson=dishesArray.toString();
			
			String mt_dishesjson=pj.beanToJson(dishes);
			pj=null;
			
			RequestSysParams requestSysParams = new RequestSysParams(StaticInfo.waimaiMTSignKey, appAuthToken);		
			CipCaterTakeoutDishBatchUploadRequest request = new CipCaterTakeoutDishBatchUploadRequest();
			request.setRequestSysParams(requestSysParams);
			request.setDishes(mt_dishesjson);
			request.setePoiId(ePoiId);			
			
			try 
			{			
				// 发送请求，接收Json ，所有Request都有doRequest方法直接调用即可
				//HelpTools.writelog_fileName("【聚宝盆】批量更新商品请求：companyNo=" +companyNo+" shopNo="+erpShopNo+" getParams："+request.getParams().toString());		
				HelpTools.writelog_fileName("【聚宝盆】批量更新商品请求：companyNo=" +companyNo+" shopNo="+erpShopNo+" getParams："+request.getParams().toString(),jbpLogFileName);
				String resultJson = request.doRequest();
				//HelpTools.writelog_fileName("【聚宝盆】批量更新商品返回：" + resultJson);		
				HelpTools.writelog_fileName("【聚宝盆】批量更新商品返回：companyNo=" +companyNo+" shopNo="+erpShopNo+" response："+resultJson,jbpLogFileName);
				JSONObject jsonObject = new JSONObject(resultJson);
				try 
				{
					String res = jsonObject.get("data").toString();
					if(res !=null && res.toUpperCase().equals("OK"))
					{
						return true;
					} 
				} 
				catch (Exception e) 
				{
					// TODO: handle exception
				}
				//这里可以解析error
				errorMessage.append(resultJson);
				return false;
			} 
			catch (IOException e) 
			{
				// 处理IO异常
				
				errorMessage.append(e.getMessage());
				return false;
			} 
			catch (URISyntaxException e) 
			{
				// 处理URI语法异常   
				
				errorMessage.append(e.getMessage());
				return false;
			}
			catch (Exception e) 
			{
				
				errorMessage.append(e.getMessage());
				return false;
			}  
			
			
		} 
		catch (Exception e) 
		{
			
			errorMessage.append(e.getMessage());
			return false;
		}
	}

	/**
	 * 美团分类新增/更新
	 * @param companyNo
	 * @param erpShopNo
	 * @param oldCatName
	 * @param catName
	 * @param sequence
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static boolean updateCat(String companyNo,String erpShopNo,String oldCatName,String catName,Integer sequence, StringBuilder errorMessage) throws Exception
	{
		try 
		{
			String appAuthToken = WMJBPOrderProcess.getToken(companyNo, erpShopNo);
			//String ePoiId = companyNo + "_" + erpShopNo;
			RequestSysParams requestSysParams = new RequestSysParams(StaticInfo.waimaiMTSignKey, appAuthToken);		
			CipCaterTakeoutDishCatUpdateRequest request = new CipCaterTakeoutDishCatUpdateRequest();
			request.setRequestSysParams(requestSysParams);		
			request.setOldCatName(oldCatName);
			request.setCatName(catName);
			request.setSequence(sequence);

			try 
			{			
				// 发送请求，接收Json ，所有Request都有doRequest方法直接调用即可
				HelpTools.writelog_fileName("【聚宝盆】分类新增更新请求：companyNo=" +companyNo+" shopNo="+erpShopNo+" getParams："+request.getParams().toString(),jbpLogFileName);		
				String resultJson = request.doRequest();
				HelpTools.writelog_fileName("【聚宝盆】分类新增更新返回：" + resultJson,jbpLogFileName);		 	    
				JSONObject jsonObject = new JSONObject(resultJson);
				try 
				{
					String res = jsonObject.get("data").toString();
					if(res !=null && res.toUpperCase().equals("OK"))
					{
						return true;
					} 
				} 
				catch (Exception e) 
				{
					// TODO: handle exception
				}
				//这里可以解析error
				errorMessage.append(resultJson);
				return false;	
			} 
			catch (IOException e) 
			{
				// 处理IO异常
				
				errorMessage.append(e.getMessage());
				return false;
			} 
			catch (URISyntaxException e) 
			{
				// 处理URI语法异常   
				
				errorMessage.append(e.getMessage());
				return false;
			}
			catch (Exception e) 
			{
				
				errorMessage.append(e.getMessage());
				return false;
			}  
		} 
		catch (Exception e) 
		{
			
			errorMessage.append(e.getMessage());
			return false;
		}	
	}

	/**
	 * 美团分类删除
	 * @param companyNo
	 * @param erpShopNo
	 * @param catName
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static boolean deleteCat(String companyNo,String erpShopNo,String catName,StringBuilder errorMessage) throws Exception
	{
		try 
		{
			String appAuthToken = WMJBPOrderProcess.getToken(companyNo, erpShopNo);
			//String ePoiId = companyNo + "_" + erpShopNo;
			RequestSysParams requestSysParams = new RequestSysParams(StaticInfo.waimaiMTSignKey, appAuthToken);		
			CipCaterTakeoutDishCatDeleteRequest request = new CipCaterTakeoutDishCatDeleteRequest();
			request.setRequestSysParams(requestSysParams);		
			request.setCatName(catName);
			try 
			{			
				// 发送请求，接收Json ，所有Request都有doRequest方法直接调用即可
				HelpTools.writelog_fileName("【聚宝盆】分类删除请求：companyNo=" +companyNo+" shopNo="+erpShopNo+" getParams："+request.getParams().toString(),jbpLogFileName);		
				String resultJson = request.doRequest();
				HelpTools.writelog_fileName("【聚宝盆】分类删除返回：" + resultJson,jbpLogFileName);		 	    
				JSONObject jsonObject = new JSONObject(resultJson);
				try 
				{
					String res = jsonObject.get("data").toString();
					if(res !=null && res.toUpperCase().equals("OK"))
					{
						return true;
					} 
				} 
				catch (Exception e) 
				{
					// TODO: handle exception
				}
				//这里可以解析error
				errorMessage.append(resultJson);
				return false;	
			} 
			catch (IOException e) 
			{
				// 处理IO异常
				
				errorMessage.append(e.getMessage());
				return false;
			} 
			catch (URISyntaxException e) 
			{
				// 处理URI语法异常   
				
				errorMessage.append(e.getMessage());
				return false;
			}
			catch (Exception e) 
			{
				
				errorMessage.append(e.getMessage());
				return false;
			}  
		} 
		catch (Exception e) 
		{
			
			errorMessage.append(e.getMessage());
			return false;
		}	
	}

	/**
	 * 美团商品删除
	 * @param companyNo
	 * @param erpShopNo
	 * @param eDishCode
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static boolean deletePlu(String companyNo,String erpShopNo,String eDishCode,StringBuilder errorMessage) throws Exception
	{
		try 
		{
			String appAuthToken = WMJBPOrderProcess.getToken(companyNo, erpShopNo);
			String ePoiId = companyNo + "_" + erpShopNo;
			RequestSysParams requestSysParams = new RequestSysParams(StaticInfo.waimaiMTSignKey, appAuthToken);	
			CipCaterTakeoutDishDeleteRequest request = new CipCaterTakeoutDishDeleteRequest();
			request.setRequestSysParams(requestSysParams);		
			request.setePoiId(ePoiId);
			request.seteDishCode(eDishCode);
			try 
			{			
				// 发送请求，接收Json ，所有Request都有doRequest方法直接调用即可
				HelpTools.writelog_fileName("【聚宝盆】商品删除请求：companyNo=" +companyNo+" shopNo="+erpShopNo+" getParams："+request.getParams().toString(),jbpLogFileName);		
				String resultJson = request.doRequest();
				HelpTools.writelog_fileName("【聚宝盆】商品删除返回：" + resultJson,jbpLogFileName);		 	    
				JSONObject jsonObject = new JSONObject(resultJson);
				try 
				{
					String res = jsonObject.get("data").toString();
					if(res !=null && res.toUpperCase().equals("OK"))
					{
						return true;
					} 
				} 
				catch (Exception e) 
				{
					// TODO: handle exception
				}
				//这里可以解析error
				errorMessage.append(resultJson);
				return false;	
			} 
			catch (IOException e) 
			{
				// 处理IO异常
				
				errorMessage.append(e.getMessage());
				return false;
			} 
			catch (URISyntaxException e) 
			{
				// 处理URI语法异常   
				
				errorMessage.append(e.getMessage());
				return false;
			}
			catch (Exception e) 
			{
				
				errorMessage.append(e.getMessage());
				return false;
			}  
		} 
		catch (Exception e) 
		{
			
			errorMessage.append(e.getMessage());
			return false;
		}	
	}

	/**
	 * 根据ERP的门店id查询门店下的菜品【不包含美团的菜品Id】
	 * @param companyNo
	 * @param erpShopNo
	 * @param int offset 起始条目数
	 * @param int limit  每页大小，须小于200
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static List<Data> queryListByEPoiId(String companyNo,String erpShopNo,int offset,int limit,StringBuilder errorMessage) throws Exception
	{
		try 
		{
			String appAuthToken = WMJBPOrderProcess.getToken(companyNo, erpShopNo);
			String ePoiId = companyNo + "_" + erpShopNo;
			RequestSysParams requestSysParams = new RequestSysParams(StaticInfo.waimaiMTSignKey, appAuthToken);	
			CipCaterTakeoutDishQueryByEPoiIdRequest request = new CipCaterTakeoutDishQueryByEPoiIdRequest();
			request.setRequestSysParams(requestSysParams);		
			request.setePoiId(ePoiId);	
			request.setOffset(offset);
			request.setLimit(limit);
			try 
			{			
				// 发送请求，接收Json ，所有Request都有doRequest方法直接调用即可
				HelpTools.writelog_fileName("【聚宝盆】查询门店菜品请求：companyNo=" +companyNo+" shopNo="+erpShopNo+" getParams："+request.getParams().toString(),jbpLogFileName);		
				String resultJson = request.doRequest();
				HelpTools.writelog_fileName("【聚宝盆】查询门店菜品返回：" + " 成功（内容多了，不写日志）！",jbpLogFileName);
				//HelpTools.writelog_fileName("【聚宝盆】查询门店菜品返回：" + resultJson,jbpLogFileName);		 	       

				//下面解析开始  20190320  BY JINZMA  注释：陶大爷教我写下面的代码 
				//com.alibaba.fastjson.JSONObject jsonObject =com.alibaba.fastjson.JSONObject.parseObject(resultJson);
				//WMJBPQueryListByEPoiId oResponse =	com.alibaba.fastjson.JSONObject.toJavaObject(jsonObject, WMJBPQueryListByEPoiId.class);
				//return oResponse.getData();		
				
				//下面解析开始  20190321  BY JINZMA  注释：王大爷教我写下面的代码 
				ParseJson pj = new ParseJson();
				WMJBPQueryListByEPoiId resserver=pj.jsonToBean(resultJson, new TypeToken<WMJBPQueryListByEPoiId>(){});
				pj=null;
				
				return resserver.getData();

			} 
			catch (IOException e) 
			{
				// 处理IO异常
				
				errorMessage.append(e.getMessage());
				return null;
			} 
			catch (URISyntaxException e) 
			{
				// 处理URI语法异常   
				
				errorMessage.append(e.getMessage());
				return null;
			}
			catch (Exception e) 
			{
				
				errorMessage.append(e.getMessage());
				return null;
			}  
		} 
		catch (Exception e) 
		{
			
			errorMessage.append(e.getMessage());
			return null;
		}	




	}

	/**
	 * 美团更新菜品库存【sku的库存】
	 * @param companyNo
	 * @param erpShopNo
	 * @param dishes
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static boolean updateStock(String companyNo,String erpShopNo,List<WMJBPGoodsUpdate> dishSkuStocks,StringBuilder errorMessage) throws Exception
	{
		try 
		{
			String appAuthToken = WMJBPOrderProcess.getToken(companyNo, erpShopNo);
			String ePoiId = companyNo + "_" + erpShopNo;		
			ParseJson pj=new ParseJson();

			String mt_dishSkuStocksJson=pj.beanToJson(dishSkuStocks);
			pj=null;
			
			RequestSysParams requestSysParams = new RequestSysParams(StaticInfo.waimaiMTSignKey, appAuthToken);		
			
			CipCaterTakeoutDishStockUpdateRequest request = new CipCaterTakeoutDishStockUpdateRequest();
			request.setRequestSysParams(requestSysParams);
			request.setDishSkuStocks(mt_dishSkuStocksJson);
			request.setePoiId(ePoiId);		

			try 
			{			
				// 发送请求，接收Json ，所有Request都有doRequest方法直接调用即可
				//HelpTools.writelog_fileName("【聚宝盆】更新商品库存请求：companyNo=" +companyNo+" shopNo="+erpShopNo+" getParams："+request.getParams().toString());		
				HelpTools.writelog_fileName("【聚宝盆】更新商品库存请求：Dishes:companyNo=" +companyNo+" shopNo="+erpShopNo+" getParams："+request.getParams().toString(),jbpLogFileName);
				String resultJson = request.doRequest();
				//HelpTools.writelog_fileName("【聚宝盆】更新商品库存返回：" + resultJson);		
				HelpTools.writelog_fileName("【聚宝盆】更新商品库存返回：Dishes:companyNo=" +companyNo+" shopNo="+erpShopNo+" response："+resultJson,jbpLogFileName);
				JSONObject jsonObject = new JSONObject(resultJson);
				try 
				{
					String res = jsonObject.get("data").toString();
					if(res !=null && res.toUpperCase().equals("OK"))
					{
						return true;
					} 
				} 
				catch (Exception e) 
				{
					// TODO: handle exception
				}
				//这里可以解析error
				errorMessage.append(resultJson);
				return false;
			} 
			catch (IOException e) 
			{
				// 处理IO异常
				
				errorMessage.append(e.getMessage());
				return false;
			} 
			catch (URISyntaxException e) 
			{
				// 处理URI语法异常   
				
				errorMessage.append(e.getMessage());
				return false;
			}
			catch (Exception e) 
			{
				
				errorMessage.append(e.getMessage());
				return false;
			}  
		} 
		catch (Exception e) 
		{
			
			errorMessage.append(e.getMessage());
			return false;
		}
	}


	/**
	 * 创建/更新菜品属性
	 * @param companyNo
	 * @param erpShopNo
	 * @param dishPropertys
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static boolean batchUpProperty(String companyNo,String erpShopNo,List<WaiMaiDishPropertyVO> dishPropertys,StringBuilder errorMessage) throws Exception
	{
		try 
		{
			String appAuthToken = WMJBPOrderProcess.getToken(companyNo, erpShopNo);
			String ePoiId = companyNo + "_" + erpShopNo;		
	
			RequestSysParams requestSysParams = new RequestSysParams(StaticInfo.waimaiMTSignKey, appAuthToken);		
			CipCaterTakeoutDishUpdatePropertyRequest request = new CipCaterTakeoutDishUpdatePropertyRequest();
			request.setRequestSysParams(requestSysParams);
			request.setDishProperty(dishPropertys);			

			try 
			{			
				// 发送请求，接收Json ，所有Request都有doRequest方法直接调用即可
				//HelpTools.writelog_fileName("【聚宝盆】批量更新商品属性请求：companyNo=" +companyNo+" shopNo="+erpShopNo+" getParams："+request.getParams().toString());		
				HelpTools.writelog_fileName("【聚宝盆】批量更新商品属性请求：companyNo=" +companyNo+" shopNo="+erpShopNo+" getParams："+request.getParams().toString(),jbpLogFileName);
				String resultJson = request.doRequest();
				//HelpTools.writelog_fileName("【聚宝盆】批量更新商品属性返回：" + resultJson);		
				HelpTools.writelog_fileName("【聚宝盆】批量更新商品属性返回：companyNo=" +companyNo+" shopNo="+erpShopNo+" response："+resultJson,jbpLogFileName);
				JSONObject jsonObject = new JSONObject(resultJson);
				try 
				{
					String res = jsonObject.get("data").toString();
					if(res !=null && res.toUpperCase().equals("OK"))
					{
						return true;
					} 
				} 
				catch (Exception e) 
				{
					// TODO: handle exception
				}
				//这里可以解析error
				errorMessage.append(resultJson);
				return false;
			} 
			catch (IOException e) 
			{
				// 处理IO异常
				
				errorMessage.append(e.getMessage());
				return false;
			} 
			catch (URISyntaxException e) 
			{
				// 处理URI语法异常   
				
				errorMessage.append(e.getMessage());
				return false;
			}
			catch (Exception e) 
			{
				
				errorMessage.append(e.getMessage());
				return false;
			}  
		} 
		catch (Exception e) 
		{
			
			errorMessage.append(e.getMessage());
			return false;
		}
		
	}

	
	public static String imageUpload(String companyNo,String erpShopNo,String imageName,File file,StringBuilder errorMessage) throws Exception
	{
		try 
		{
			String appAuthToken = WMJBPOrderProcess.getToken(companyNo, erpShopNo);
			String ePoiId = companyNo + "_" + erpShopNo;

			RequestSysParams requestSysParams = new RequestSysParams(StaticInfo.waimaiMTSignKey, appAuthToken);		
			CipCaterTakeoutImageUploadRequest request = new CipCaterTakeoutImageUploadRequest();
			
			request.setRequestSysParams(requestSysParams);
			request.setePoiId(ePoiId);
			request.setFile(file);
			request.setImageName(imageName);
		
			

			try 
			{			
				// 发送请求，接收Json ，所有Request都有doRequest方法直接调用即可
				HelpTools.writelog_fileName("【聚宝盆】上传图片请求：companyNo=" +companyNo+" shopNo="+erpShopNo+" getParams："+request.getParams().toString(),jbpLogFileName);		
				String resultJson = request.doRequest();
				HelpTools.writelog_fileName("【聚宝盆】上传图片返回：" + resultJson,jbpLogFileName);		 	    				
				JSONObject jsonObject = new JSONObject(resultJson);
				try 
				{
					String res = jsonObject.get("data").toString();
					return res;
				} 
				catch (Exception e) 
				{
					// TODO: handle exception
				}
				//这里可以解析error
				errorMessage.append(resultJson);
				return null;
				
			} 
			catch (IOException e) 
			{
				// 处理IO异常
				
				errorMessage.append(e.getMessage());
				return null;
			} 
			catch (URISyntaxException e) 
			{
				// 处理URI语法异常   
				
				errorMessage.append(e.getMessage());
				return null;
			}
			catch (Exception e) 
			{
				
				errorMessage.append(e.getMessage());
				return null;
			}  

		} 
		catch (Exception e) 
		{
			
			errorMessage.append(e.getMessage());
			return null;

		}
		
	}
	
	
}
