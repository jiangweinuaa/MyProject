package com.dsc.spos.waimai;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.sankuai.meituan.waimai.opensdk.exception.ApiOpException;
import com.sankuai.meituan.waimai.opensdk.exception.ApiSysException;
import com.sankuai.meituan.waimai.opensdk.factory.APIFactory;
import com.sankuai.meituan.waimai.opensdk.vo.FoodCatParam;
import com.sankuai.meituan.waimai.opensdk.vo.FoodParam;
import com.sankuai.meituan.waimai.opensdk.vo.FoodProperty;
import com.sankuai.meituan.waimai.opensdk.vo.FoodPropertyWithFoodCode;
import com.sankuai.meituan.waimai.opensdk.vo.FoodSkuStockParam;
import com.sankuai.meituan.waimai.opensdk.vo.SystemParam;


public class WMMTProductService 
{	
	static String jbpLogFileName = "mtProductlog";
	//public final static SystemParam sysPram = new SystemParam(StaticInfo.waimaiMTAPPID, StaticInfo.waimaiMTSignKey);
	
	/**
	 * 查询门店的菜品分类。
	 * @param app_poi_code
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static  List<FoodCatParam> queryCatList(String app_poi_code,StringBuilder errorMessage) throws Exception
	{	
		try 
		{
			String ePoiId = app_poi_code;
			/*if (companyNo != null && companyNo.trim().length() > 0)
			{
				ePoiId = companyNo + "_" + erpShopNo;
			}
			else
			{
				ePoiId = erpShopNo;
			}*/
			 
			
			try 
			{	
				HelpTools.writelog_fileName("【MT】查询门店菜品分类请求：app_poi_code=" +app_poi_code,jbpLogFileName);	
			  List<FoodCatParam> foodCatList =	APIFactory.getFoodAPI().foodCatList(getSystemParam(), ePoiId);
				HelpTools.writelog_fileName("【MT】查询门店菜品分类返回成功！总个数：" +foodCatList.size(),jbpLogFileName);
				return foodCatList;

			} 
			catch (ApiOpException e) 
	  	{
	       
	       errorMessage.append(e.getMsg());
	       return null;
	    }
	  	catch (ApiSysException e) 
	  	{ 		 
	              
	       errorMessage.append(e.getExceptionEnum().getMsg());    
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
	 * 批量创建或更新菜品
	 * @param app_poi_code
	 * @param dishes
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static boolean batchUpload(String app_poi_code,List<FoodParam> foodParams,StringBuilder errorMessage) throws Exception
	{
		try 
		{
			String ePoiId = app_poi_code;
			/*if (companyNo != null && companyNo.trim().length() > 0)
			{
				ePoiId = companyNo + "_" + erpShopNo;
			}
			else
			{
				ePoiId = erpShopNo;
			}*/
			 
			
			try 
			{	
				HelpTools.writelog_fileName("【MT】创建/更新门店菜品请求：app_poi_code=" +app_poi_code,jbpLogFileName);	
			  String resultJson =	APIFactory.getFoodAPI().foodBatchInitData(getSystemParam(), ePoiId, foodParams);
				HelpTools.writelog_fileName("【MT】创建/更新门店菜品返回：" +resultJson,jbpLogFileName);
				if(resultJson !=null && resultJson.toUpperCase().equals("OK"))
				{
					return true;
				} 
				else 
				{
					return false;
			
		    }

			} 
			catch (ApiOpException e) 
	  	{
	       
	       errorMessage.append(e.getMsg());
	       return false;
	    }
	  	catch (ApiSysException e) 
	  	{ 		 
	              
	       errorMessage.append(e.getExceptionEnum().getMsg());    
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
	 * @param app_poi_code
	 * @param oldCatName
	 * @param catName
	 * @param sequence
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static boolean updateCat(String app_poi_code,String oldCatName,String catName,Integer sequence, StringBuilder errorMessage) throws Exception
	{
		try 
		{
			String ePoiId = app_poi_code;
			/*if (companyNo != null && companyNo.trim().length() > 0)
			{
				ePoiId = companyNo + "_" + erpShopNo;
			}
			else
			{
				ePoiId = erpShopNo;
			}*/
			 
			
			try 
			{	
				HelpTools.writelog_fileName("【MT】创建/更新门店菜品分类请求：app_poi_code=" +app_poi_code+"  oldCatName="+oldCatName+" catName="+catName,jbpLogFileName);	
				String resultJson =	APIFactory.getFoodAPI().foodCatUpdate(getSystemParam(), ePoiId, oldCatName, catName, sequence);
				HelpTools.writelog_fileName("【MT】创建/更新门店菜品分类返回：" +resultJson,jbpLogFileName);
				if(resultJson !=null && resultJson.toUpperCase().equals("OK"))
				{
					return true;
				} 
				else 
				{
					errorMessage.append(resultJson);
					return false;
			
		    }

			} 
			catch (ApiOpException e) 
	  	{
	       
	       errorMessage.append(e.getMsg());
	       return false;
	    }
	  	catch (ApiSysException e) 
	  	{ 		 
	              
	       errorMessage.append(e.getExceptionEnum().getMsg());    
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
	 * @param app_poi_code
	 * @param catName
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static boolean deleteCat(String app_poi_code,String catName,StringBuilder errorMessage) throws Exception
	{
		try 
		{
			String ePoiId = app_poi_code;
			/*if (companyNo != null && companyNo.trim().length() > 0)
			{
				ePoiId = companyNo + "_" + erpShopNo;
			}
			else
			{
				ePoiId = erpShopNo;
			}*/
			 
			
			try 
			{	
				HelpTools.writelog_fileName("【MT】删除门店菜品分类请求：app_poi_code=" +app_poi_code+" category_name="+catName,jbpLogFileName);	
				String resultJson =	APIFactory.getFoodAPI().foodCatDelete(getSystemParam(), ePoiId, catName);
				HelpTools.writelog_fileName("【MT】删除门店菜品分类返回：" +resultJson,jbpLogFileName);
				if(resultJson !=null && resultJson.toUpperCase().equals("OK"))
				{
					return true;
				} 
				else 
				{
					errorMessage.append(resultJson);
					return false;
			
		    }

			} 
			catch (ApiOpException e) 
	  	{
	       
	       errorMessage.append(e.getMsg());
	       return false;
	    }
	  	catch (ApiSysException e) 
	  	{ 		 
	              
	       errorMessage.append(e.getExceptionEnum().getMsg());    
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
	 * @param app_poi_code
	 * @param eDishCode
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static boolean deletePlu(String app_poi_code,String eDishCode,StringBuilder errorMessage) throws Exception
	{
		try 
		{
			String ePoiId = app_poi_code;
			/*if (companyNo != null && companyNo.trim().length() > 0)
			{
				ePoiId = companyNo + "_" + erpShopNo;
			}
			else
			{
				ePoiId = erpShopNo;
			}*/
			 
			
			try 
			{	
				HelpTools.writelog_fileName("【MT】删除门店菜品请求：app_poi_code=" +app_poi_code+" app_food_code="+eDishCode,jbpLogFileName);	
				String resultJson =	APIFactory.getFoodAPI().foodDelete(getSystemParam(), ePoiId, eDishCode);
				HelpTools.writelog_fileName("【MT】删除门店菜品返回：" +resultJson,jbpLogFileName);
				if(resultJson !=null && resultJson.toUpperCase().equals("OK"))
				{
					return true;
				} 
				else 
				{
					errorMessage.append(resultJson);
					return false;
			
		    }

			} 
			catch (ApiOpException e) 
	  	{
	       
	       errorMessage.append(e.getMsg());
	       return false;
	    }
	  	catch (ApiSysException e) 
	  	{ 		 
	              
	       errorMessage.append(e.getExceptionEnum().getMsg());    
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
	 * 查询该门店的菜品列表。
	 * @param app_poi_code
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static List<FoodParam> queryListByEPoiId(String app_poi_code,StringBuilder errorMessage) throws Exception
	{
		try 
		{
			String ePoiId = app_poi_code;
			/*if (companyNo != null && companyNo.trim().length() > 0)
			{
				ePoiId = companyNo + "_" + erpShopNo;
			}
			else
			{
				ePoiId = erpShopNo;
			}*/
			 
			
			try 
			{	
				HelpTools.writelog_fileName("【MT】查询门店菜品请求：app_poi_code=" +app_poi_code,jbpLogFileName);	
			  List<FoodParam> foodList =	APIFactory.getFoodAPI().foodList(getSystemParam(), ePoiId);
				HelpTools.writelog_fileName("【MT】查询门店菜品返回成功！总个数：" +foodList.size(),jbpLogFileName);
				return foodList;

			} 
			catch (ApiOpException e) 
	  	{
	       
	       errorMessage.append(e.getMsg());
	       return null;
	    }
	  	catch (ApiSysException e) 
	  	{ 		 
	              
	       errorMessage.append(e.getExceptionEnum().getMsg());    
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
	 * @param app_poi_code
	 * @param dishes
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static boolean updateStock(String app_poi_code, List<FoodSkuStockParam> foodSkuStockParams,StringBuilder errorMessage) throws Exception
	{
		try 
		{
			String ePoiId = app_poi_code;
			/*if (companyNo != null && companyNo.trim().length() > 0)
			{
				ePoiId = companyNo + "_" + erpShopNo;
			}
			else
			{
				ePoiId = erpShopNo;
			}*/
			 
			
			try 
			{	
				HelpTools.writelog_fileName("【MT】更新菜品库存请求：app_poi_code=" +app_poi_code+" foodSkuStockParams="+foodSkuStockParams.toString(),jbpLogFileName);	
				String resultJson =	APIFactory.getFoodAPI().updateFoodSkuStock(getSystemParam(), ePoiId, foodSkuStockParams);
				HelpTools.writelog_fileName("【MT】更新菜品库存返回：" +resultJson,jbpLogFileName);
				if(resultJson !=null && resultJson.toUpperCase().equals("OK"))
				{
					return true;
				} 
				else 
				{
					errorMessage.append(resultJson);
					return false;
			
		    }

			} 
			catch (ApiOpException e) 
	  	{
	       
	       errorMessage.append(e.getMsg());
	       return false;
	    }
	  	catch (ApiSysException e) 
	  	{ 		 
	              
	       errorMessage.append(e.getExceptionEnum().getMsg());    
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
	 * 获取菜品属性
	 * @param app_poi_code
	 * @param appFoodCode APP方菜品id
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static List<FoodProperty> queryFoodProperty(String app_poi_code,String appFoodCode,StringBuilder errorMessage) throws Exception
	{
		try 
		{
			String ePoiId = app_poi_code;
			/*if (companyNo != null && companyNo.trim().length() > 0)
			{
				ePoiId = companyNo + "_" + erpShopNo;
			}
			else
			{
				ePoiId = erpShopNo;
			}*/
			 
			
			try 
			{	
				HelpTools.writelog_fileName("【MT】查询菜品属性请求：app_poi_code=" +app_poi_code+" appFoodCode="+appFoodCode,jbpLogFileName);	
				List<FoodProperty> foodPropertyList =	APIFactory.getFoodAPI().foodPropertyList(getSystemParam(), ePoiId, appFoodCode);
				HelpTools.writelog_fileName("【MT】查询菜品属性返回成功！总个数：" +foodPropertyList.size(),jbpLogFileName);
				return foodPropertyList;

			} 
			catch (ApiOpException e) 
	  	{
	       
	       errorMessage.append(e.getMsg());
	       return null;
	    }
	  	catch (ApiSysException e) 
	  	{ 		 
	              
	       errorMessage.append(e.getExceptionEnum().getMsg());    
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
	 * 绑定菜品属性
	 * @param app_poi_code
	 * @param dishPropertys
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static boolean batchUpProperty(String app_poi_code,List<FoodPropertyWithFoodCode> foodPropertyWithFoodCodes,StringBuilder errorMessage) throws Exception
	{
		try 
		{
			String ePoiId = app_poi_code;
			/*if (companyNo != null && companyNo.trim().length() > 0)
			{
				ePoiId = companyNo + "_" + erpShopNo;
			}
			else
			{
				ePoiId = erpShopNo;
			}*/
			 
			
			try 
			{	
				HelpTools.writelog_fileName("【MT】绑定菜品属性请求：app_poi_code=" +app_poi_code+" FoodPropertyWithFoodCode="+foodPropertyWithFoodCodes.toString(),jbpLogFileName);	
				String resultJson =	APIFactory.getFoodAPI().foodBindProperty(getSystemParam(), ePoiId, foodPropertyWithFoodCodes);
				HelpTools.writelog_fileName("【MT】绑定菜品属性返回：" +resultJson,jbpLogFileName);
				if(resultJson !=null && resultJson.toUpperCase().equals("OK"))
				{
					return true;
				} 
				else 
				{
					errorMessage.append(resultJson);
					return false;
			
		    }

			} 
			catch (ApiOpException e) 
	  	{
	       
	       errorMessage.append(e.getMsg());
	       return false;
	    }
	  	catch (ApiSysException e) 
	  	{ 		 
	              
	       errorMessage.append(e.getExceptionEnum().getMsg());    
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
	 * 通过接口上传图片获取图片ID
	 * @param app_poi_code
	 * @param imageName
	 * @param file
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static String imageUpload(String app_poi_code,String imageName,File file,StringBuilder errorMessage) throws Exception
	{
		try 
		{
			String ePoiId = app_poi_code;
			/*if (companyNo != null && companyNo.trim().length() > 0)
			{
				ePoiId = companyNo + "_" + erpShopNo;
			}
			else
			{
				ePoiId = erpShopNo;
			}*/
			 
			
			try 
			{	
				HelpTools.writelog_fileName("【MT】图片上传请求：app_poi_code=" +app_poi_code+" file="+file.getName(),jbpLogFileName);	
				String resultJson =	APIFactory.getImageApi().imageUpload(getSystemParam(), ePoiId, file, imageName);
				HelpTools.writelog_fileName("【MT】图片上传返回：" +resultJson,jbpLogFileName);
				return resultJson;

			} 
			catch (ApiOpException e) 
	  	{
	       
	       errorMessage.append(e.getMsg());
	       return null;
	    }
	  	catch (ApiSysException e) 
	  	{ 		 
	              
	       errorMessage.append(e.getExceptionEnum().getMsg());    
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
	 * 批量更新售卖状态
	 * @param app_poi_code
	 * @param foodParams 只需要传这几个节点[{"app_food_code":"abcd135","skus":[{"sku_id":"abcd135"}]}]
	 * @param sellStatus 售卖状态，1表下架，0表上架
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static boolean batchUpSellStatus(String app_poi_code,List<FoodParam> foodParams,int sellStatus,StringBuilder errorMessage) throws Exception
	{
		try 
		{
			String ePoiId = app_poi_code;
			/*if (companyNo != null && companyNo.trim().length() > 0)
			{
				ePoiId = companyNo + "_" + erpShopNo;
			}
			else
			{
				ePoiId = erpShopNo;
			}*/
			 
			
			try 
			{	
				HelpTools.writelog_fileName("【MT】批量更新售卖状态请求： sellStatus="+sellStatus+" app_poi_code=" +app_poi_code+" FoodParam="+foodParams.toString(),jbpLogFileName);	
				String resultJson =	APIFactory.getFoodAPI().foodSkuSellStatusUpdate(getSystemParam(), ePoiId, foodParams, sellStatus);
				HelpTools.writelog_fileName("【MT】批量更新售卖状态返回：" +resultJson,jbpLogFileName);
				if(resultJson !=null && resultJson.toUpperCase().equals("OK"))
				{
					return true;
				} 
				else 
				{
					errorMessage.append(resultJson);
					return false;
			
		    }

			} 
			catch (ApiOpException e) 
	  	{
	       
	       errorMessage.append(e.getMsg());
	       return false;
	    }
	  	catch (ApiSysException e) 
	  	{ 		 
	              
	       errorMessage.append(e.getExceptionEnum().getMsg());    
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
	 * 根据商品名称和规格名称更换新的商品编码和SKU
	 * @param app_poi_code
	 * @param orderGoodName 商品名称，如变更商品编码，商品名称需同线上完全一致，如不一致将无法匹配
	 * @param orderCategoryName 分类名称，如变更商品编码，分类名称需同线上完全一致，如不一致将无法匹配
	 * @param orderSpecName 规格，如变更商品编码，规格需同线上完全一致，如不一致将无法匹配
	 * @param pluNo 新app_food_code，为商品的APP方商品id，不同门店可以重复，同一门店内不能重复)最大长度128
	 * @param pluBarcode 新sku_id，为商品sku的唯一标示
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static boolean updateAppFoodCodeByNameAndSpec(String app_poi_code,String orderGoodName,String orderCategoryName,String orderSpecName,String pluNo,String pluBarcode,StringBuilder errorMessage)throws Exception
	{
		try 
		{
			String ePoiId = app_poi_code;
			/*if (companyNo != null && companyNo.trim().length() > 0)
			{
				ePoiId = companyNo + "_" + erpShopNo;
			}
			else
			{
				ePoiId = erpShopNo;
			}*/
			 
			
			try 
			{	
				HelpTools.writelog_fileName("【MT】根据商品名称和规格名称更换新的商品编码和SKU请求： app_poi_code=" +app_poi_code+" 平台商品名称="+orderGoodName+" 平台分类名称="+orderCategoryName+" 平台规格名称spec="+orderSpecName+" app_food_code	="+pluNo+" sku_id="+pluBarcode,jbpLogFileName);	
				String resultJson =	APIFactory.getFoodAPI().updateAppFoodCodeByNameAndSpec(getSystemParam(), ePoiId, orderGoodName, orderCategoryName, pluNo, pluBarcode, orderSpecName);
				HelpTools.writelog_fileName("【MT】根据商品名称和规格名称更换新的商品编码和SKU返回：" +resultJson,jbpLogFileName);
				if(resultJson !=null && resultJson.toUpperCase().equals("OK"))
				{
					return true;
				} 
				else 
				{
					errorMessage.append(resultJson);
					return false;
			
		    }

			} 
			catch (ApiOpException e) 
	  	{
	       
	       errorMessage.append(e.getMsg());
	       return false;
	    }
	  	catch (ApiSysException e) 
	  	{ 		 
	              
	       errorMessage.append(e.getExceptionEnum().getMsg());    
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
	 * 根据原商品编码/SKU更换新商品编码/SKU
	 * @param app_poi_code 
	 * @param app_food_code_origin 原app_food_code，为商品的APP方商品id
	 * @param app_food_code 新app_food_code，为商品的APP方商品id，不同门店可以重复，同一门店内不能重复)最大长度128
	 * @param sku_id_origin 原sku_id，为商品sku的唯一标示
	 * @param sku_id 新sku_id，为商品sku的唯一标示
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static boolean updateAppFoodCodeByOrigin(String app_poi_code,String app_food_code_origin,String app_food_code,String sku_id_origin,String sku_id,StringBuilder errorMessage)throws Exception
	{
		try 
		{
			String ePoiId = app_poi_code;
			/*if (companyNo != null && companyNo.trim().length() > 0)
			{
				ePoiId = companyNo + "_" + erpShopNo;
			}
			else
			{
				ePoiId = erpShopNo;
			}*/
			 
			
			try 
			{	
				HelpTools.writelog_fileName("【MT】根据原商品编码/SKU更换新商品编码/SKU请求： app_poi_code=" +app_poi_code+" 原app_food_code_origin="+app_food_code_origin+" 新app_food_code="+app_food_code+" 原sku_id_origin="+sku_id_origin+" 新sku_id	="+sku_id,jbpLogFileName);	
				String resultJson =	APIFactory.getFoodAPI().updateAppFoodCodeByOrigin(getSystemParam(), ePoiId, app_food_code_origin, app_food_code, sku_id_origin, sku_id);
				HelpTools.writelog_fileName("【MT】根据原商品编码/SKU更换新商品编码/SKU返回：" +resultJson,jbpLogFileName);
				if(resultJson !=null && resultJson.toUpperCase().equals("OK"))
				{
					return true;
				} 
				else 
				{
					errorMessage.append(resultJson);
					return false;
			
		    }

			} 
			catch (ApiOpException e) 
	  	{
	       
	       errorMessage.append(e.getMsg());
	       return false;
	    }
	  	catch (ApiSysException e) 
	  	{ 		 
	              
	       errorMessage.append(e.getExceptionEnum().getMsg());    
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
	
	 private static SystemParam getSystemParam() throws Exception
	  {
			String appId = "";
			String appSecret = "";
			String eId = "";
			List<Map<String, Object>> elmAppKeyList = PosPub.getWaimaiAppConfig(StaticInfo.dao, eId,
					orderLoadDocType.MEITUAN);
			if (elmAppKeyList != null && elmAppKeyList.size() > 0)
			{
				appId = elmAppKeyList.get(0).get("APIKEY").toString();
				appSecret = elmAppKeyList.get(0).get("APISECRET").toString();
			} else
			{
				appId = StaticInfo.waimaiMTAPPID;
				appSecret = StaticInfo.waimaiMTSignKey;
			}

			SystemParam sysPram = new SystemParam(appId, appSecret);

			return sysPram;
		}
	  
	
}
