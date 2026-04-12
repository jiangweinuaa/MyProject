package com.dsc.spos.waimai;

import java.util.List;
import java.util.Map;

import com.dsc.spos.scheduler.job.StaticInfo;

import eleme.openapi.sdk.config.Config;
import eleme.openapi.sdk.config.ElemeSdkLogger;
import eleme.openapi.sdk.oauth.response.Token;
import eleme.openapi.sdk.api.entity.order.OOrder;
import eleme.openapi.sdk.api.entity.order.OrderList;
import eleme.openapi.sdk.api.entity.product.MaterialTree;
import eleme.openapi.sdk.api.entity.product.OBatchModifiedResult;
import eleme.openapi.sdk.api.entity.product.OCategory;
import eleme.openapi.sdk.api.entity.product.OItem;
import eleme.openapi.sdk.api.entity.product.QueryPage;
import eleme.openapi.sdk.api.enumeration.product.OItemCreateProperty;
import eleme.openapi.sdk.api.enumeration.product.OItemUpdateProperty;
import eleme.openapi.sdk.api.exception.ServiceException;
import eleme.openapi.sdk.api.service.OrderService;
import eleme.openapi.sdk.api.service.ProductService;

public class WMELMProductService 
{
	public final static Config config = new Config(StaticInfo.waimaiELMIsSandbox, StaticInfo.waimaiELMAPPKey, StaticInfo.waimaiELMSecret);
	static	String goodsLogFileName = "MappingGoodsSaveLocal";
	/**
	 * 分页获取店铺下的商品
	 * @param shopId    店铺Id
	 * @param pageIndex 分页起始 0开始
	 * @param pageSize  一页个数 最大300
	 * @param errorMeassge 错误信息
	 * @return
	 * @throws Exception
	 */
	public static List<OItem> queryItemByPage(long shopId, long pageIndex, long pageSize, StringBuilder errorMessage) throws Exception
	{
		Token token = WMELMUtilTools.GetTokenInRedis();
		if (token == null) 
		{
			errorMessage.append("【获取Token】失败！");
			return null;
		}
		try 
		{
			ProductService productService = new ProductService(config, token);
			QueryPage queryPage = new QueryPage();
			queryPage.setShopId(shopId);
			queryPage.setOffset(pageIndex);
			queryPage.setLimit(pageSize);
			List<OItem> products = productService.queryItemByPage(queryPage);
			return products;

		} 
		catch (ServiceException e) 
		{
			if (e.getCode().equals("UNAUTHORIZED")) 
			{
				WMELMUtilTools.GetTokenInClientCredentials();
				errorMessage.append(e.getMessage() + "，请重试！");
			} 
			else 
			{
				errorMessage.append(e.getMessage()+"("+e.getCode()+")");
			}
			return null;
		} 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			return null;
		}				
	}

	/**
	 * 查询商品详情
	 * @param itemId  商品Id
	 * @param errorMeassge 错误信息
	 * @return
	 * @throws Exception
	 */
	public static OItem getItem(long itemId, StringBuilder errorMessage) throws Exception
	{
		Token token = WMELMUtilTools.GetTokenInRedis();
		if (token == null) 
		{
			errorMessage.append("【获取Token】失败！");
			return null;
		}
		try 
		{

			ProductService productService = new ProductService(config, token);
			OItem product = productService.getItem(itemId);	

			return product;		
		} 
		catch (ServiceException e) 
		{
			if (e.getCode().equals("UNAUTHORIZED")) 
			{
				WMELMUtilTools.GetTokenInClientCredentials();
				errorMessage.append(e.getMessage() + "，请重试！");
			} 
			else 
			{
				errorMessage.append(e.getMessage()+"("+e.getCode()+")");
			}
			return null;
		} 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			return null;
		}				
	}
	/**
	 * 更新商品
	 * @param itemId     商品Id
	 * @param categoryId 商品分类Id
	 * @param properties 商品属性
	 * @param errorMeassge 
	 * @return
	 * @throws Exception
	 */
	public static boolean updateItem(long itemId,long categoryId, Map<OItemUpdateProperty,Object> properties, StringBuilder errorMessage) throws Exception
	{
		
		Token token = WMELMUtilTools.GetTokenInRedis();
		if (token == null) 
		{
			errorMessage.append("【获取Token】失败！");
			return false;
		}
		try 
		{
			ProductService productService = new ProductService(config, token);

			config.setLog(new ElemeSdkLogger() {

				@Override
				public void info(String message)  {
					// TODO Auto-generated method stub
					try {
						HelpTools.writelog_fileName("更新ELM商品message:"+message,goodsLogFileName);

					} catch (Exception e) {
						// TODO: handle exception
					}

				}

				@Override
				public void error(String message) {
					// TODO Auto-generated method stub
					try {
						HelpTools.writelog_fileName("更新ELM商品error:"+message,goodsLogFileName);

					} catch (Exception e) {
						// TODO: handle exception
					}

				}
			});


			productService.updateItem(itemId, categoryId, properties);	
			return true;

		} 
		catch (ServiceException e) 
		{
			if (e.getCode().equals("UNAUTHORIZED")) 
			{
				WMELMUtilTools.GetTokenInClientCredentials();
				errorMessage.append(e.getMessage() + "，请重试！");
			} 
			else 
			{
				HelpTools.writelog_waimai("更新ELM商品itemId："+itemId+" categoryId:"+categoryId+" 异常:"+e.getMessage()+"("+e.getCode()+")");
				errorMessage.append(e.getMessage()+"("+e.getCode()+")");
			}
			return false;
		} 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			return false;
		}				
	}
	
	public static OItem updateItemTransTask(long itemId,long categoryId, Map<OItemUpdateProperty,Object> properties, StringBuilder errorMessage) throws Exception
	{
		Token token = WMELMUtilTools.GetTokenInRedis();
		if (token == null) 
		{
			errorMessage.append("【获取Token】失败！");
			return null;
		}
		try 
		{
			ProductService productService = new ProductService(config, token);
		  OItem nRet = productService.updateItem(itemId, categoryId, properties);	
			return nRet;

		} 
		catch (ServiceException e) 
		{
			if (e.getCode().equals("UNAUTHORIZED")) 
			{
				WMELMUtilTools.GetTokenInClientCredentials();
				errorMessage.append(e.getMessage() + "，请重试！");
			} 
			else 
			{
				HelpTools.writelog_waimai("更新ELM商品itemId："+itemId+" categoryId:"+categoryId+" 异常:"+e.getMessage()+"("+e.getCode()+")");
				errorMessage.append(e.getMessage()+"("+e.getCode()+")");
			}
			return null;
		} 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			return null;
		}				
	}

	/**
	 * 批量商品上架
	 * @param itemId     商品Id
	 * @param errorMeassge 
	 * @return OBatchModifiedResult
	 * @throws Exception
	 */
	public static OBatchModifiedResult  batchListItems( List<Long> itemId, StringBuilder errorMessage) throws Exception
	{
		Token token = WMELMUtilTools.GetTokenInRedis();
		if (token == null) 
		{
			errorMessage.append("【获取Token】失败！");
			return null;
		}		
		try 
		{
			ProductService productService = new ProductService(config, token);
			OBatchModifiedResult product= productService.batchListItems(itemId);				  
			return product;
		} 
		catch (ServiceException e) 
		{
			if (e.getCode().equals("UNAUTHORIZED")) 
			{
				WMELMUtilTools.GetTokenInClientCredentials();
				errorMessage.append(e.getMessage() + "，请重试！");
			} 
			else 
			{
				HelpTools.writelog_waimai("批量更新ELM商品上架异常:"+e.getMessage()+"("+e.getCode()+")");
				errorMessage.append(e.getMessage()+"("+e.getCode()+")");
			}
			return null;
		} 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			return null;
		}				

	}

	/**
	 * 批量商品下架
	 * @param itemId     商品Id
	 * @param errorMeassge 
	 * @return OBatchModifiedResult
	 * @throws Exception
	 */
	public static OBatchModifiedResult batchDelistItems( List<Long> itemId, StringBuilder errorMessage) throws Exception
	{
		Token token = WMELMUtilTools.GetTokenInRedis();
		if (token == null) 
		{
			errorMessage.append("【获取Token】失败！");
			return null;
		}		
		try 
		{
			ProductService productService = new ProductService(config, token);
			OBatchModifiedResult product= productService.batchDelistItems(itemId);				  
			return product;
		} 
		catch (ServiceException e) 
		{
			if (e.getCode().equals("UNAUTHORIZED")) 
			{
				WMELMUtilTools.GetTokenInClientCredentials();
				errorMessage.append(e.getMessage() + "，请重试！");
			} 
			else 
			{
				HelpTools.writelog_waimai("批量更新ELM商品下架 异常:"+e.getMessage()+"("+e.getCode()+")");
				errorMessage.append(e.getMessage()+"("+e.getCode()+")");
			}
			return null;
		} 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			return null;
		}			
	}

	/**
	 * 获取店铺的商品分类
	 * @param shopId
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static List<OCategory> getShopCategories(long shopId, StringBuilder errorMessage) throws Exception
	{
		Token token = WMELMUtilTools.GetTokenInRedis();
		if (token == null) 
		{
			errorMessage.append("【获取Token】失败！");
			return null;
		}
		try 
		{

			ProductService productService = new ProductService(config, token);
			List<OCategory> product = productService.getShopCategories(shopId);
			return product;		
		} 
		catch (ServiceException e) 
		{
			if (e.getCode().equals("UNAUTHORIZED")) 
			{
				WMELMUtilTools.GetTokenInClientCredentials();
				errorMessage.append(e.getMessage() + "，请重试！");
			} 
			else 
			{
				errorMessage.append(e.getMessage()+"("+e.getCode()+")");
			}
			return null;
		} 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			return null;
		}				
	}

	/**
	 * 通过远程URL上传图片，返回图片的hash值
	 * @param url
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static String uploadImageWithRemoteUrl (String url, StringBuilder errorMessage) throws Exception
	{
		Token token = WMELMUtilTools.GetTokenInRedis();
		if (token == null) 
		{
			errorMessage.append("【获取Token】失败！");
			return null;
		}
		try 
		{
			ProductService productService = new ProductService(config, token);
			String result = productService.uploadImageWithRemoteUrl(url);
			return result  ;		
		} 
		catch (ServiceException e) 
		{
			if (e.getCode().equals("UNAUTHORIZED")) 
			{
				WMELMUtilTools.GetTokenInClientCredentials();
				errorMessage.append(e.getMessage() + "，请重试！");
			} 
			else 
			{
				errorMessage.append(e.getMessage()+"("+e.getCode()+")");
			}
			return null;
		} 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			return null;
		}				
	}

	/**
	 * 批量添加商品
	 * @param categoryId
	 * @param items
	 * @param errorMessage
	 * @return Map<Long,OItem>
	 * @throws Exception
	 */
	public static Map<Long,OItem> batchCreateItems (long categoryId, List<Map<OItemCreateProperty,Object>> items,StringBuilder errorMessage) throws Exception
	{
		Token token = WMELMUtilTools.GetTokenInRedis();
		if (token == null) 
		{
			errorMessage.append("【获取Token】失败！");
			return null;
		}	

		try 
		{
			ProductService productService = new ProductService(config, token);
			Map<Long,OItem> product = productService.batchCreateItems(categoryId,items);	  
			return product;
		} 

		catch (ServiceException e) 
		{
			if (e.getCode().equals("UNAUTHORIZED")) 
			{
				WMELMUtilTools.GetTokenInClientCredentials();
				errorMessage.append(e.getMessage() + "，请重试！");
			} 
			else 
			{
				HelpTools.writelog_waimai("批量添加商品   categoryId:"+categoryId+" 异常:"+e.getMessage()+"("+e.getCode()+")");
				errorMessage.append(e.getMessage()+"("+e.getCode()+")");
			}
			return null;
		} 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			return null;
		}		

	}

	/**
	 * 添加商品分类
	 * @param shopId
	 * @param name
	 * @param description
	 * @param errorMessage
	 * @return OCategory
	 * @throws Exception
	 */
	public static OCategory createCategory( long shopId,String name,String description,StringBuilder errorMessage ) throws Exception
	{
		Token token = WMELMUtilTools.GetTokenInRedis();
		if (token == null) 
		{
			errorMessage.append("【获取Token】失败！");
			return null;
		}	
		try 
		{
			ProductService productService = new ProductService(config, token);
			OCategory  product = productService.createCategory (shopId,name,description);	  
			return product;
		} 

		catch (ServiceException e) 
		{
			if (e.getCode().equals("UNAUTHORIZED")) 
			{
				WMELMUtilTools.GetTokenInClientCredentials();
				errorMessage.append(e.getMessage() + "，请重试！");
			} 
			else 
			{
				HelpTools.writelog_waimai("添加分类 :"+name+" 异常:"+e.getMessage()+"("+e.getCode()+")");
				errorMessage.append(e.getMessage()+"("+e.getCode()+")");
			}
			return null;
		} 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			return null;
		}		
	}

	/**
	 * 更新商品分类
	 * @param categoryId
	 * @param name
	 * @param description
	 * @param errorMessage
	 * @return OCategory
	 * @throws Exception
	 */
	public static OCategory updateCategory (long categoryId,String name,String description,StringBuilder errorMessage ) throws Exception
	{
		Token token = WMELMUtilTools.GetTokenInRedis();
		if (token == null) 
		{
			errorMessage.append("【获取Token】失败！");
			return null;
		}	
		try 
		{
			ProductService productService = new ProductService(config, token);
			OCategory  product = productService.updateCategory (categoryId,name,description);	  
			return product;
		} 

		catch (ServiceException e) 
		{
			if (e.getCode().equals("UNAUTHORIZED")) 
			{
				WMELMUtilTools.GetTokenInClientCredentials();
				errorMessage.append(e.getMessage() + "，请重试！");
			} 
			else 
			{
				HelpTools.writelog_waimai("更新分类 :"+name+" 异常:"+e.getMessage()+"("+e.getCode()+")");
				errorMessage.append(e.getMessage()+"("+e.getCode()+")");
			}
			return null;
		} 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			return null;
		}		
	}
	
	/**
	 * 删除商品分类
	 * @param categoryId
	 * @param errorMessage
	 * @return null
	 * @throws Exception
	 */
	public static boolean invalidCategory( long categoryId , StringBuilder errorMessage )throws Exception
	{
		Token token = WMELMUtilTools.GetTokenInRedis();
		if (token == null) 
		{
			errorMessage.append("【获取Token】失败！");
			return false;
		}	
		try 
		{
			ProductService productService = new ProductService(config, token);
			productService.invalidCategory (categoryId);	  
			return true;
		} 

		catch (ServiceException e) 
		{
			if (e.getCode().equals("UNAUTHORIZED")) 
			{
				WMELMUtilTools.GetTokenInClientCredentials();
				errorMessage.append(e.getMessage() + "，请重试！");
			} 
			else 
			{
				HelpTools.writelog_waimai("删除分类categoryId :"+categoryId+" 异常:"+e.getMessage()+"("+e.getCode()+")");
				errorMessage.append(e.getMessage()+"("+e.getCode()+")");
			}
			return false;
		} 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			return false;
		}		
	}
	
	/**
	 * 批量删除商品
	 * @param itemIds
	 * @param errorMessage
	 * @return Map<Long,OItem>
	 * @throws Exception
	 */
	public static Map<Long,OItem> batchRemoveItems (List<Long> itemIds, StringBuilder errorMessage )throws Exception
	{
		Token token = WMELMUtilTools.GetTokenInRedis();
		if (token == null) 
		{
			errorMessage.append("【获取Token】失败！");
			return null;
		}	
		try 
		{
			ProductService productService = new ProductService(config, token);
			Map<Long,OItem>  product = productService.batchRemoveItems (itemIds);	  
			return product;
		} 

		catch (ServiceException e) 
		{
			if (e.getCode().equals("UNAUTHORIZED")) 
			{
				WMELMUtilTools.GetTokenInClientCredentials();
				errorMessage.append(e.getMessage() + "，请重试！");
			} 
			else 
			{
				HelpTools.writelog_waimai("批量删除商品 异常:"+e.getMessage()+"("+e.getCode()+")");
				errorMessage.append(e.getMessage()+"("+e.getCode()+")");
			}
			return null;
		} 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			return null;
		}			
	}
	
	public static void getItemMaterialTree() throws Exception
	{
		Token token = new Token();
		token.setAccessToken("06466308ee2af340de788e0815027679");
		Config configCur = new Config(false, "sLp2ckzFBb", "0df7503c6da64b916a8266cd50a0470a182052eb");
		ProductService productService = new ProductService(configCur, token);
		long shopID =1968141;
		configCur.setLog(new ElemeSdkLogger() {
		
		@Override
		public void info(String message) {
		// TODO Auto-generated method stub
			try {
				HelpTools.writelog_waimai("获取原料请求req:"+message);
		
		} catch (Exception e) {
		// TODO: handle exception
		}
			
		}
		
		@Override
		public void error(String message) {
		// TODO Auto-generated method stub
			try {
				HelpTools.writelog_waimai("获取原料返回res:"+message);
		
		} catch (Exception e) {
		// TODO: handle exception
		}
		}
	});
		
		try 
		{
			List<MaterialTree> products = productService.getItemMaterialTree(shopID);
		} 

		catch (ServiceException e) 
		{
			HelpTools.writelog_waimai("获取原料返回异常:"+e.getCode());
			
			
		} 
		catch (Exception e) 
		{
			HelpTools.writelog_waimai("获取原料返回异常:"+e.getMessage());
		}	
		
	}
	
	/**
	 * 设置分类排序(新版)
	 * @param Long shopId
	 * @param List<Long categoryIds> 
	 * @param errorMessage
	 * @return null
	 * @throws Exception
	 */
	public static boolean setCategorySequence (Long shopId,List<Long> categoryIds,StringBuilder errorMessage )throws Exception
	{

		Token token = WMELMUtilTools.GetTokenInRedis();
		if (token == null) 
		{
			errorMessage.append("【获取Token】失败！");
			return false;
		}	
		try 
		{
			ProductService productService = new ProductService(config, token);
			productService.setCategorySequence(shopId,categoryIds)	 ; 
			return true;
		} 

		catch (ServiceException e) 
		{
			if (e.getCode().equals("UNAUTHORIZED")) 
			{
				WMELMUtilTools.GetTokenInClientCredentials();
				errorMessage.append(e.getMessage() + "，请重试！");
			} 
			else 
			{
				HelpTools.writelog_waimai("设置分类排序异常:"+e.getMessage()+"("+e.getCode()+")");
				errorMessage.append(e.getMessage()+"("+e.getCode()+")");
			}
			return false;
		} 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			return false;
		}		

	}
	
	/**
	 * 查询全部订单
	 * @param shopId
	 * @param pageNo 页码。取值范围:大于零的整数最大限制为100
	 * @param pageSize 每页获取条数。最小值1，最大值50
	 * @param dateString
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static OrderList getAllOrders (long shopId, int pageNo, int pageSize,String dateString, StringBuilder errorMessage )throws Exception
	{
		Token token = WMELMUtilTools.GetTokenInRedis();
		if (token == null) 
		{
			errorMessage.append("【获取Token】失败！");
			return null;
		}	
		try 
		{
					
			OrderService  orderService  = new OrderService (config, token);
			OrderList	 orderList =orderService.getAllOrders(shopId, pageNo, pageSize, dateString);
			return orderList;
		} 

		catch (ServiceException e) 
		{
			if (e.getCode().equals("UNAUTHORIZED")) 
			{
				WMELMUtilTools.GetTokenInClientCredentials();
				errorMessage.append(e.getMessage() + "，请重试！");
			} 
			else 
			{
				errorMessage.append(e.getMessage()+"("+e.getCode()+")");
			}
			return null;
		} 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			return null;
		}			
	}
	
	/**
	 * 获取订单详情；订单ID不会重复，全局唯一
	 * @param orderId
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static OOrder getOrder (String orderId, StringBuilder errorMessage )throws Exception
	{
		Token token = WMELMUtilTools.GetTokenInRedis();
		if (token == null) 
		{
			errorMessage.append("【获取Token】失败！");
			return null;
		}	
		try 
		{
					
			OrderService  orderService  = new OrderService (config, token);
			OOrder order = orderService.getOrder(orderId);
			return order;
		} 

		catch (ServiceException e) 
		{
			if (e.getCode().equals("UNAUTHORIZED")) 
			{
				WMELMUtilTools.GetTokenInClientCredentials();
				errorMessage.append(e.getMessage() + "，请重试！");
			} 
			else 
			{
				errorMessage.append(e.getMessage()+"("+e.getCode()+")");
			}
			return null;
		} 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			return null;
		}			
	}
	

	/**
	 * 批量更新商品库存
	 * @param Map<Long,Integer> 商品规格ID和库存设值的映射
	 * @param errorMeassge 
	 * @return OBatchModifiedResult
	 * @throws Exception
	 */
	public static OBatchModifiedResult batchUpdateStock( Map<Long,Integer> stockMap, StringBuilder errorMessage) throws Exception
	{
		Token token = WMELMUtilTools.GetTokenInRedis();
		if (token == null) 
		{
			errorMessage.append("【获取Token】失败！");
			return null;
		}		
		try 
		{
			ProductService productService = new ProductService(config, token);
			OBatchModifiedResult product= productService.batchUpdateStock(stockMap);				  
			return product;
		} 
		catch (ServiceException e) 
		{
			if (e.getCode().equals("UNAUTHORIZED")) 
			{
				WMELMUtilTools.GetTokenInClientCredentials();
				errorMessage.append(e.getMessage() + "，请重试！");
			} 
			else 
			{
				HelpTools.writelog_waimai("批量更新ELM商品库存异常:"+e.getMessage()+"("+e.getCode()+")");
				errorMessage.append(e.getMessage()+"("+e.getCode()+")");
			}
			return null;
		} 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			return null;
		}				
	}
	
	/**
	 * 分类排序
	 * @param shopId
	 * @param categoryIds
	 * @param errorMessage
	 * @throws Exception
	 */
	public static void setCategorySequence (long shopId, List<Long> categoryIds, StringBuilder errorMessage) throws Exception
	{
		Token token = WMELMUtilTools.GetTokenInRedis();
		if (token == null) 
		{
			errorMessage.append("【获取Token】失败！");
			return;
		}		
		try 
		{
			ProductService productService = new ProductService(config, token);
			productService.setCategorySequence(shopId, categoryIds);				  
			return;
		} 
		catch (ServiceException e) 
		{
			if (e.getCode().equals("UNAUTHORIZED")) 
			{
				WMELMUtilTools.GetTokenInClientCredentials();
				errorMessage.append(e.getMessage() + "，请重试！");
			} 
			else 
			{
				HelpTools.writelog_waimai("批量更新ELM商品库存异常:"+e.getMessage()+"("+e.getCode()+")");
				errorMessage.append(e.getMessage()+"("+e.getCode()+")");
			}
			return;
		} 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			return;
		}			
	}
	
	/**
	 * 设置商品排序
	 * @param categoryId 分类ID
	 * @param itemIds 商品排序列表ID
	 * @param errorMessage
	 * @throws Exception
	 */
	public static void setItemPositions  (long categoryId, List<Long> itemIds, StringBuilder errorMessage) throws Exception
	{
		Token token = WMELMUtilTools.GetTokenInRedis();
		if (token == null) 
		{
			errorMessage.append("【获取Token】失败！");
			return;
		}		
		try 
		{
			ProductService productService = new ProductService(config, token);
			productService.setCategorySequence(categoryId, itemIds);				  
			return;
		} 
		catch (ServiceException e) 
		{
			if (e.getCode().equals("UNAUTHORIZED")) 
			{
				WMELMUtilTools.GetTokenInClientCredentials();
				errorMessage.append(e.getMessage() + "，请重试！");
			} 
			else 
			{
				HelpTools.writelog_waimaiException("批量更新ELM商品排序异常:"+e.getMessage()+"("+e.getCode()+")");
				errorMessage.append(e.getMessage()+"("+e.getCode()+")");
			}
			return;
		} 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			return;
		}			
	}
	
	
	//region 重载上面的方法


	/**
	 * 根据传入APPKey分页获取店铺下的商品
	 * @param waimaiELMIsSandbox
	 * @param waimaiELMAPPKey
	 * @param waimaiELMSecret
	 * @param waimaiELMAPPName
	 * @param shopId
	 * @param pageIndex
	 * @param pageSize
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static List<OItem> queryItemByPage(boolean waimaiELMIsSandbox,String waimaiELMAPPKey,String waimaiELMSecret,String waimaiELMAPPName,long shopId, long pageIndex, long pageSize, StringBuilder errorMessage) throws Exception
	{
		Token token = WMELMUtilTools.GetTokenInRedis(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret, waimaiELMAPPName);
		if (token == null) 
		{
			errorMessage.append("【获取Token】失败！");
			return null;
		}
		try 
		{
			Config configCur = new Config(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret);
			ProductService productService = new ProductService(configCur, token);
			QueryPage queryPage = new QueryPage();
			queryPage.setShopId(shopId);
			queryPage.setOffset(pageIndex);
			queryPage.setLimit(pageSize);
			List<OItem> products = productService.queryItemByPage(queryPage);
			return products;

		} 
		catch (ServiceException e) 
		{
			if (e.getCode().equals("UNAUTHORIZED")) 
			{
				WMELMUtilTools.GetTokenInClientCredentials(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret,waimaiELMAPPName);
				errorMessage.append(e.getMessage() + "，请重试！");
			} 
			else 
			{
				errorMessage.append(e.getMessage()+"("+e.getCode()+")");
			}
			return null;
		} 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			return null;
		}				
	}

	/**
	 * 根据传入APPKey查询商品详情
	 * @param waimaiELMIsSandbox
	 * @param waimaiELMAPPKey
	 * @param waimaiELMSecret
	 * @param waimaiELMAPPName
	 * @param itemId
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static OItem getItem(boolean waimaiELMIsSandbox,String waimaiELMAPPKey,String waimaiELMSecret,String waimaiELMAPPName,long itemId, StringBuilder errorMessage) throws Exception
	{
		Token token = WMELMUtilTools.GetTokenInRedis(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret,waimaiELMAPPName);
		if (token == null) 
		{
			errorMessage.append("【获取Token】失败！");
			return null;
		}
		try 
		{
			Config configCur = new Config(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret);
			ProductService productService = new ProductService(configCur, token);
			OItem product = productService.getItem(itemId);		
			return product;		
		} 
		catch (ServiceException e) 
		{
			if (e.getCode().equals("UNAUTHORIZED")) 
			{
				WMELMUtilTools.GetTokenInClientCredentials(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret,waimaiELMAPPName);
				errorMessage.append(e.getMessage() + "，请重试！");
			} 
			else 
			{
				errorMessage.append(e.getMessage()+"("+e.getCode()+")");
			}
			return null;
		} 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			return null;
		}				
	}

	/**
	 * 根据传入APPKey更新商品
	 * @param waimaiELMIsSandbox
	 * @param waimaiELMAPPKey
	 * @param waimaiELMSecret
	 * @param waimaiELMAPPName
	 * @param itemId
	 * @param categoryId
	 * @param properties
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static boolean updateItem(boolean waimaiELMIsSandbox,String waimaiELMAPPKey,String waimaiELMSecret,String waimaiELMAPPName,long itemId,long categoryId, Map<OItemUpdateProperty,Object> properties, StringBuilder errorMessage) throws Exception
	{
		Token token = WMELMUtilTools.GetTokenInRedis(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret,waimaiELMAPPName);
		if (token == null) 
		{
			errorMessage.append("【获取Token】失败！");
			return false;
		}
		try 
		{
			Config configCur = new Config(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret);
			configCur.setLog(new ElemeSdkLogger() {

				@Override
				public void info(String message)  {
					// TODO Auto-generated method stub
					try {
						HelpTools.writelog_fileName("更新ELM商品message:"+message,goodsLogFileName);

					} catch (Exception e) {
						// TODO: handle exception
					}

				}

				@Override
				public void error(String message) {
					// TODO Auto-generated method stub
					try {
						HelpTools.writelog_fileName("更新ELM商品error:"+message,goodsLogFileName);

					} catch (Exception e) {
						// TODO: handle exception
					}

				}
			});
			ProductService productService = new ProductService(configCur, token);			
			productService.updateItem(itemId, categoryId, properties);
			return true;

		} 
		catch (ServiceException e) 
		{
			if (e.getCode().equals("UNAUTHORIZED")) 
			{
				WMELMUtilTools.GetTokenInClientCredentials(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret,waimaiELMAPPName);
				errorMessage.append(e.getMessage() + "，请重试！");
			} 
			else 
			{
				errorMessage.append(e.getMessage()+"("+e.getCode()+")");
			}
			return false;
		} 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			return false;
		}				
	}
	
	
	public static OItem updateItemTransTask(boolean waimaiELMIsSandbox,String waimaiELMAPPKey,String waimaiELMSecret,String waimaiELMAPPName,long itemId,long categoryId, Map<OItemUpdateProperty,Object> properties, StringBuilder errorMessage) throws Exception
	{
		Token token = WMELMUtilTools.GetTokenInRedis(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret,waimaiELMAPPName);
		if (token == null) 
		{
			errorMessage.append("【获取Token】失败！");
			return null;
		}
		try 
		{
			Config configCur = new Config(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret);
			ProductService productService = new ProductService(configCur, token);			
			return	productService.updateItem(itemId, categoryId, properties);
			

		} 
		catch (ServiceException e) 
		{
			if (e.getCode().equals("UNAUTHORIZED")) 
			{
				WMELMUtilTools.GetTokenInClientCredentials(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret,waimaiELMAPPName);
				errorMessage.append(e.getMessage() + "，请重试！");
			} 
			else 
			{
				errorMessage.append(e.getMessage()+"("+e.getCode()+")");
			}
			return null;
		} 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			return null;
		}				
	}
	

	/**
	 * 获取店铺的商品分类
	 * @param waimaiELMIsSandbox
	 * @param waimaiELMAPPKey
	 * @param waimaiELMSecret
	 * @param waimaiELMAPPName
	 * @param shopId
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static List<OCategory> getShopCategories(boolean waimaiELMIsSandbox,String waimaiELMAPPKey,String waimaiELMSecret,String waimaiELMAPPName,long shopId, StringBuilder errorMessage) throws Exception
	{
		Token token = WMELMUtilTools.GetTokenInRedis(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret,waimaiELMAPPName);
		if (token == null) 
		{
			errorMessage.append("【获取Token】失败！");
			return null;
		}
		try 
		{
			Config configCur = new Config(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret);
			ProductService productService = new ProductService(configCur, token);			
			List<OCategory> product = productService.getShopCategories(shopId);
			return product;		
		} 
		catch (ServiceException e) 
		{
			if (e.getCode().equals("UNAUTHORIZED")) 
			{
				WMELMUtilTools.GetTokenInClientCredentials(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret,waimaiELMAPPName);
				errorMessage.append(e.getMessage() + "，请重试！");
			} 
			else 
			{
				errorMessage.append(e.getMessage()+"("+e.getCode()+")");
			}
			return null;
		} 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			return null;
		}				
	}

	/**
	 * 商品上架
	 * @param waimaiELMIsSandbox
	 * @param waimaiELMAPPKey
	 * @param waimaiELMSecret
	 * @param waimaiELMAPPName
	 * @param itemId  商品Id
	 * @param errorMeassge 
	 * @return OBatchModifiedResult
	 * @throws Exception
	 */
	public static OBatchModifiedResult batchListItems( boolean waimaiELMIsSandbox,String waimaiELMAPPKey,String waimaiELMSecret,String waimaiELMAPPName,List<Long> itemId, StringBuilder errorMessage) throws Exception
	{
		Token token = WMELMUtilTools.GetTokenInRedis(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret,waimaiELMAPPName);
		if (token == null) 
		{
			errorMessage.append("【获取Token】失败！");
			return null;
		}		
		try 
		{
			Config configCur = new Config(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret);
			ProductService productService = new ProductService(configCur, token);
			OBatchModifiedResult product = productService.batchListItems(itemId);				  
			return product;
		} 
		catch (ServiceException e) 
		{
			if (e.getCode().equals("UNAUTHORIZED")) 
			{
				WMELMUtilTools.GetTokenInClientCredentials(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret,waimaiELMAPPName);
				errorMessage.append(e.getMessage() + "，请重试！");
			} 
			else 
			{
				errorMessage.append(e.getMessage()+"("+e.getCode()+")");
			}
			return null;
		} 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			return null;
		}			
	}

	/**
	 * 商品下架
	 * @param waimaiELMIsSandbox
	 * @param waimaiELMAPPKey
	 * @param waimaiELMSecret
	 * @param waimaiELMAPPName
	 * @param itemId     商品Id
	 * @param errorMeassge 
	 * @return
	 * @throws Exception
	 */
	public static OBatchModifiedResult batchDelistItems( boolean waimaiELMIsSandbox,String waimaiELMAPPKey,String waimaiELMSecret,String waimaiELMAPPName,List<Long> itemId, StringBuilder errorMessage) throws Exception
	{
		Token token = WMELMUtilTools.GetTokenInRedis(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret,waimaiELMAPPName);
		if (token == null) 
		{
			errorMessage.append("【获取Token】失败！");
			return null;
		}		
		try 
		{
			Config configCur = new Config(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret);
			ProductService productService = new ProductService(configCur, token);
			OBatchModifiedResult product= productService.batchDelistItems(itemId);				  
			return product;
		} 
		catch (ServiceException e) 
		{
			if (e.getCode().equals("UNAUTHORIZED")) 
			{
				WMELMUtilTools.GetTokenInClientCredentials(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret,waimaiELMAPPName);
				errorMessage.append(e.getMessage() + "，请重试！");
			} 
			else 
			{
				errorMessage.append(e.getMessage()+"("+e.getCode()+")");
			}
			return null;
		} 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			return null;
		}			
	}

	/**
	 * 通过远程URL上传图片，返回图片的hash值
	 * @param waimaiELMIsSandbox
	 * @param waimaiELMAPPKey
	 * @param waimaiELMSecret
	 * @param waimaiELMAPPName
	 * @param url
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static String uploadImageWithRemoteUrl (boolean waimaiELMIsSandbox,String waimaiELMAPPKey,String waimaiELMSecret,String waimaiELMAPPName,String url, StringBuilder errorMessage) throws Exception
	{
		Token token = WMELMUtilTools.GetTokenInRedis(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret,waimaiELMAPPName);
		if (token == null) 
		{
			errorMessage.append("【获取Token】失败！");
			return null;
		}		
		try 
		{
			Config configCur = new Config(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret);
			ProductService productService = new ProductService(configCur, token);
			String result = 	productService.uploadImageWithRemoteUrl(url);
			return result  ;		
		} 
		catch (ServiceException e) 
		{
			if (e.getCode().equals("UNAUTHORIZED")) 
			{
				WMELMUtilTools.GetTokenInClientCredentials(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret,waimaiELMAPPName);
				errorMessage.append(e.getMessage() + "，请重试！");
			} 
			else 
			{
				errorMessage.append(e.getMessage()+"("+e.getCode()+")");
			}
			return null;
		} 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			return null;
		}			
	}

	/**
	 * 批量添加商品
	 * @param waimaiELMIsSandbox
	 * @param waimaiELMAPPKey
	 * @param waimaiELMSecret
	 * @param waimaiELMAPPName
	 * @param categoryId
	 * @param items
	 * @param errorMessage
	 * @return Map<Long,OItem>
	 * @throws Exception
	 */
	public static Map<Long,OItem> batchCreateItems (boolean waimaiELMIsSandbox,String waimaiELMAPPKey,String waimaiELMSecret,String waimaiELMAPPName,long categoryId, List<Map<OItemCreateProperty,Object>> items,StringBuilder errorMessage) throws Exception
	{
		Token token = WMELMUtilTools.GetTokenInRedis(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret,waimaiELMAPPName);
		if (token == null) 
		{
			errorMessage.append("【获取Token】失败！");
			return null;
		}			
		try 
		{
			Config configCur = new Config(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret);
			ProductService productService = new ProductService(configCur, token);
			/*configCur.setLog(new ElemeSdkLogger() {
			
			@Override
			public void info(String message) {
			// TODO Auto-generated method stub
				try {
					HelpTools.writelog_fileName("新增ELM商品message:"+message,"addELMProduct");
		
				} catch (Exception e) {
			// TODO: handle exception
		
				}
				
			}
			
			@Override
			public void error(String message) {
			// TODO Auto-generated method stub
			
				try {
					HelpTools.writelog_fileName("新增ELM商品error:"+message,"addELMProduct");
		
				} catch (Exception e) {
			// TODO: handle exception
		
				}
			}
		});*/
			Map<Long,OItem> product = productService.batchCreateItems(categoryId,items);	  
			return product;
		} 

		catch (ServiceException e) 
		{
			if (e.getCode().equals("UNAUTHORIZED")) 
			{
				WMELMUtilTools.GetTokenInClientCredentials(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret,waimaiELMAPPName);
				errorMessage.append(e.getMessage() + "，请重试！");
			} 
			else 
			{
				errorMessage.append(e.getMessage()+"("+e.getCode()+")");
			}
			return null;
		} 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			return null;
		}		
	}

	/**
	 * 设置分类排序(新版)
	 * @param Long shopId
	 * @param List<Long categoryIds> 
	 * @param errorMessage
	 * @return null
	 * @throws Exception
	 */
	public static boolean setCategorySequence(boolean waimaiELMIsSandbox,String waimaiELMAPPKey,String waimaiELMSecret,String waimaiELMAPPName,Long shopId,List<Long> categoryIds ,StringBuilder errorMessage) throws Exception
	{
		
		Token token = WMELMUtilTools.GetTokenInRedis(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret,waimaiELMAPPName);
		if (token == null) 
		{
			errorMessage.append("【获取Token】失败！");
			return false;
		}			
		try 
		{
			Config configCur = new Config(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret);
			ProductService productService = new ProductService(configCur, token);
			productService.setCategorySequence(shopId,categoryIds)	 ; 			
			return true;
		} 

		catch (ServiceException e) 
		{
			if (e.getCode().equals("UNAUTHORIZED")) 
			{
				WMELMUtilTools.GetTokenInClientCredentials(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret,waimaiELMAPPName);
				errorMessage.append(e.getMessage() + "，请重试！");
			} 
			else 
			{
			  errorMessage.append(e.getMessage()+"("+e.getCode()+")");			
			}
			return false;
		} 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			return false;
		}				
	}
	
	/**
	 * 添加商品分类
	 * @param waimaiELMIsSandbox
	 * @param waimaiELMAPPKey
	 * @param waimaiELMSecret
	 * @param waimaiELMAPPName
	 * @param shopId
	 * @param name
	 * @param description
	 * @param errorMessage
	 * @return OCategory
	 * @throws Exception
	 */
	public static OCategory createCategory(boolean waimaiELMIsSandbox,String waimaiELMAPPKey,String waimaiELMSecret,String waimaiELMAPPName, long shopId,String name,String description,StringBuilder errorMessage ) throws Exception
	{
		Token token = WMELMUtilTools.GetTokenInRedis(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret,waimaiELMAPPName);
		if (token == null) 
		{
			errorMessage.append("【获取Token】失败！");
			return null;
		}	
		try 
		{
			Config configCur = new Config(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret);
			ProductService productService = new ProductService(configCur, token);
			OCategory  product = productService.createCategory (shopId,name,description);	  
			return product;
		} 

		catch (ServiceException e) 
		{
			if (e.getCode().equals("UNAUTHORIZED")) 
			{
				WMELMUtilTools.GetTokenInClientCredentials(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret,waimaiELMAPPName);
				errorMessage.append(e.getMessage() + "，请重试！");
			} 
			else 
			{
				errorMessage.append(e.getMessage()+"("+e.getCode()+")");
			}
			return null;
		} 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			return null;
		}		
	}

	/**
	 * 更新商品分类
	 * @param waimaiELMIsSandbox
	 * @param waimaiELMAPPKey
	 * @param waimaiELMSecret
	 * @param waimaiELMAPPName
	 * @param categoryId
	 * @param name
	 * @param description
	 * @param errorMessage
	 * @return OCategory
	 * @throws Exception
	 */
	public static OCategory updateCategory (boolean waimaiELMIsSandbox,String waimaiELMAPPKey,String waimaiELMSecret,String waimaiELMAPPName, long categoryId,String name,String description,StringBuilder errorMessage ) throws Exception
	{
		Token token = WMELMUtilTools.GetTokenInRedis(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret,waimaiELMAPPName);
		if (token == null) 
		{
			errorMessage.append("【获取Token】失败！");
			return null;
		}	
		try 
		{
			Config configCur = new Config(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret);
			ProductService productService = new ProductService(configCur, token);
			OCategory  product = productService.updateCategory (categoryId,name,description);	  
			return product;
		} 

		catch (ServiceException e) 
		{
			if (e.getCode().equals("UNAUTHORIZED")) 
			{
				WMELMUtilTools.GetTokenInClientCredentials(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret,waimaiELMAPPName);
				errorMessage.append(e.getMessage() + "，请重试！");
			} 
			else 
			{
				errorMessage.append(e.getMessage()+"("+e.getCode()+")");
			}
			return null;
		} 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			return null;
		}		
	}
	
	/**
	 * 删除商品分类
	 * @param waimaiELMIsSandbox
	 * @param waimaiELMAPPKey
	 * @param waimaiELMSecret
	 * @param waimaiELMAPPName
	 * @param categoryId
	 * @param errorMessage
	 * @return null
	 * @throws Exception
	 */
	public static boolean invalidCategory(boolean waimaiELMIsSandbox,String waimaiELMAPPKey,String waimaiELMSecret,String waimaiELMAPPName, long categoryId , StringBuilder errorMessage )throws Exception
	{
		Token token = WMELMUtilTools.GetTokenInRedis(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret,waimaiELMAPPName);
		if (token == null) 
		{
			errorMessage.append("【获取Token】失败！");
			return false;
		}	
		try 
		{
			Config configCur = new Config(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret);
			ProductService productService = new ProductService(configCur, token);
			productService.invalidCategory (categoryId);	  
			return true;
		} 

		catch (ServiceException e) 
		{
			if (e.getCode().equals("UNAUTHORIZED")) 
			{
				WMELMUtilTools.GetTokenInClientCredentials(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret,waimaiELMAPPName);
				errorMessage.append(e.getMessage() + "，请重试！");
			} 
			else 
			{
				errorMessage.append(e.getMessage()+"("+e.getCode()+")");
			}
			return false;
		} 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			return false;
		}		
	}
	
	/**
	 * 批量删除商品
	 * @param waimaiELMIsSandbox
	 * @param waimaiELMAPPKey
	 * @param waimaiELMSecret
	 * @param waimaiELMAPPName
	 * @param itemIds
	 * @param errorMessage
	 * @return Map<Long,OItem>
	 * @throws Exception
	 */
	public static Map<Long,OItem> batchRemoveItems (boolean waimaiELMIsSandbox,String waimaiELMAPPKey,String waimaiELMSecret,String waimaiELMAPPName,List<Long> itemIds, StringBuilder errorMessage )throws Exception
	{
		Token token = WMELMUtilTools.GetTokenInRedis(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret,waimaiELMAPPName);
		if (token == null) 
		{
			errorMessage.append("【获取Token】失败！");
			return null;
		}	
		try 
		{
			Config configCur = new Config(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret);		
			ProductService productService = new ProductService(configCur, token);
			Map<Long,OItem>  product = productService.batchRemoveItems (itemIds);	  
			return product;
		} 

		catch (ServiceException e) 
		{
			if (e.getCode().equals("UNAUTHORIZED")) 
			{
				WMELMUtilTools.GetTokenInClientCredentials(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret,waimaiELMAPPName);
				errorMessage.append(e.getMessage() + "，请重试！");
			} 
			else 
			{
				errorMessage.append(e.getMessage()+"("+e.getCode()+")");
			}
			return null;
		} 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			return null;
		}			
	}
	

	/**
	 * 查询全部订单
	 * @param waimaiELMIsSandbox
	 * @param waimaiELMAPPKey
	 * @param waimaiELMSecret
	 * @param waimaiELMAPPName
	 * @param shopId
	 * @param pageNo 页码。取值范围:大于零的整数最大限制为100
	 * @param pageSize 每页获取条数。最小值1，最大值50
	 * @param dateString
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static OrderList getAllOrders (boolean waimaiELMIsSandbox,String waimaiELMAPPKey,String waimaiELMSecret,String waimaiELMAPPName,long shopId, int pageNo, int pageSize,String dateString, StringBuilder errorMessage )throws Exception
	{
		Token token = WMELMUtilTools.GetTokenInRedis(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret,waimaiELMAPPName);
		if (token == null) 
		{
			errorMessage.append("【获取Token】失败！");
			return null;
		}	
		try 
		{
			Config configCur = new Config(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret);		
			OrderService  orderService  = new OrderService (configCur, token);
			OrderList	 orderList =orderService.getAllOrders(shopId, pageNo, pageSize, dateString);
			return orderList;
		} 

		catch (ServiceException e) 
		{
			if (e.getCode().equals("UNAUTHORIZED")) 
			{
				WMELMUtilTools.GetTokenInClientCredentials(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret,waimaiELMAPPName);
				errorMessage.append(e.getMessage() + "，请重试！");
			} 
			else 
			{
				errorMessage.append(e.getMessage()+"("+e.getCode()+")");
			}
			return null;
		} 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			return null;
		}			
	}
	
	
	/**
	 * 获取订单详情；订单ID不会重复，全局唯一
	 * @param waimaiELMIsSandbox
	 * @param waimaiELMAPPKey
	 * @param waimaiELMSecret
	 * @param waimaiELMAPPName
	 * @param orderId
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static OOrder getOrder (boolean waimaiELMIsSandbox,String waimaiELMAPPKey,String waimaiELMSecret,String waimaiELMAPPName,String orderId, StringBuilder errorMessage )throws Exception
	{
		Token token = WMELMUtilTools.GetTokenInRedis(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret,waimaiELMAPPName);
		if (token == null) 
		{
			errorMessage.append("【获取Token】失败！");
			return null;
		}	
		try 
		{
			Config configCur = new Config(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret);		
			OrderService  orderService  = new OrderService (configCur, token);
			OOrder	 order =orderService.getOrder(orderId);
			return order;
		} 

		catch (ServiceException e) 
		{
			if (e.getCode().equals("UNAUTHORIZED")) 
			{
				WMELMUtilTools.GetTokenInClientCredentials(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret,waimaiELMAPPName);
				errorMessage.append(e.getMessage() + "，请重试！");
			} 
			else 
			{
				errorMessage.append(e.getMessage()+"("+e.getCode()+")");
			}
			return null;
		} 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			return null;
		}			
	}
	
	
	/**
	 * 批量更新商品库存
	 * @param Map<Long,Integer> 商品规格ID和库存设值的映射
	 * @param errorMeassge 
	 * @return OBatchModifiedResult
	 * @throws Exception
	 */
	public static OBatchModifiedResult batchUpdateStock(boolean waimaiELMIsSandbox,String waimaiELMAPPKey,String waimaiELMSecret,String waimaiELMAPPName, Map<Long,Integer> stockMap, StringBuilder errorMessage) throws Exception
	{
		Token token = WMELMUtilTools.GetTokenInRedis(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret,waimaiELMAPPName);
		if (token == null) 
		{
			errorMessage.append("【获取Token】失败！");
			return null;
		}		
		try 
		{			
			Config configCur = new Config(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret);		
      ProductService productService = new ProductService(configCur, token);
			OBatchModifiedResult product= productService.batchUpdateStock(stockMap);				  
			return product;
		} 
		catch (ServiceException e) 
		{
			if (e.getCode().equals("UNAUTHORIZED")) 
			{
				WMELMUtilTools.GetTokenInClientCredentials(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret,waimaiELMAPPName);
				errorMessage.append(e.getMessage() + "，请重试！");
			} 
			else 
			{
				HelpTools.writelog_waimai("批量更新ELM商品库存异常:"+e.getMessage()+"("+e.getCode()+")");
				errorMessage.append(e.getMessage()+"("+e.getCode()+")");
			}
			return null;
		} 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			return null;
		}				
	
	}
	
	/**
	 * 设置分类顺序
	 * @param waimaiELMIsSandbox
	 * @param waimaiELMAPPKey
	 * @param waimaiELMSecret
	 * @param waimaiELMAPPName
	 * @param shopId
	 * @param categoryIds
	 * @param errorMessage
	 * @throws Exception
	 */
	public static void setCategorySequence (boolean waimaiELMIsSandbox,String waimaiELMAPPKey,String waimaiELMSecret,String waimaiELMAPPName,long shopId, List<Long> categoryIds, StringBuilder errorMessage) throws Exception
	{
		Token token = WMELMUtilTools.GetTokenInRedis(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret,waimaiELMAPPName);
		if (token == null) 
		{
			errorMessage.append("【获取Token】失败！");
			return ;
		}		
		try 
		{			
			Config configCur = new Config(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret);		
      ProductService productService = new ProductService(configCur, token);
			productService.setCategorySequence(shopId, categoryIds);		  
			return ;
		} 
		catch (ServiceException e) 
		{
			if (e.getCode().equals("UNAUTHORIZED")) 
			{
				WMELMUtilTools.GetTokenInClientCredentials(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret,waimaiELMAPPName);
				errorMessage.append(e.getMessage() + "，请重试！");
			} 
			else 
			{
				HelpTools.writelog_waimai("批量更新ELM商品库存异常:"+e.getMessage()+"("+e.getCode()+")");
				errorMessage.append(e.getMessage()+"("+e.getCode()+")");
			}
			return;
		} 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			return;
		}			
		
	}
	
	/**
	 * 设置分类下面商品排序
	 * @param waimaiELMIsSandbox
	 * @param waimaiELMAPPKey
	 * @param waimaiELMSecret
	 * @param waimaiELMAPPName
	 * @param categoryId 分类ID
	 * @param itemIds 商品排序列表ID
	 * @param errorMessage
	 * @throws Exception
	 */
	public static void setItemPositions (boolean waimaiELMIsSandbox,String waimaiELMAPPKey,String waimaiELMSecret,String waimaiELMAPPName,long categoryId, List<Long> itemIds, StringBuilder errorMessage) throws Exception
	{
		Token token = WMELMUtilTools.GetTokenInRedis(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret,waimaiELMAPPName);
		if (token == null) 
		{
			errorMessage.append("【获取Token】失败！");
			return ;
		}		
		try 
		{			
			Config configCur = new Config(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret);		
      ProductService productService = new ProductService(configCur, token);
			productService.setItemPositions(categoryId, itemIds);		  
			return ;
		} 
		catch (ServiceException e) 
		{
			if (e.getCode().equals("UNAUTHORIZED")) 
			{
				WMELMUtilTools.GetTokenInClientCredentials(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret,waimaiELMAPPName);
				errorMessage.append(e.getMessage() + "，请重试！");
			} 
			else 
			{
				//HelpTools.writelog_waimai("批量更新ELM商品库存异常:"+e.getMessage()+"("+e.getCode()+")");
				HelpTools.writelog_waimaiException("批量更新ELM商品排序异常:"+e.getMessage()+"("+e.getCode()+")");
				errorMessage.append(e.getMessage()+"("+e.getCode()+")");
			}
			return;
		} 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			return;
		}			
		
	}
	
	
	//endregion

	//region 服务商模式-重载方法
	/**
	 * 获取订单详情；订单ID不会重复，全局唯一
	 * @param waimaiELMIsSandbox
	 * @param waimaiELMAPPKey
	 * @param waimaiELMSecret
	 * @param waimaiELMAPPName
	 * @param orderId
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static OOrder getOrder (String userId,boolean waimaiELMIsSandbox,String waimaiELMAPPKey,String waimaiELMSecret,String waimaiELMAPPName,String orderId, StringBuilder errorMessage )throws Exception
	{
		Token token = WMELMUtilTools.ISV_GetTokenByUserId(userId);
		if (token == null)
		{
			errorMessage.append("【获取Token】失败！");
			return null;
		}
		try
		{
			Config configCur = new Config(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret);
			OrderService  orderService  = new OrderService (configCur, token);
			OOrder	 order =orderService.getOrder(orderId);
			return order;
		}

		catch (ServiceException e)
		{
			if (e.getCode().equals("UNAUTHORIZED"))
			{
				WMELMUtilTools.GetTokenInClientCredentials(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret,waimaiELMAPPName);
				errorMessage.append(e.getMessage() + "，请重试！");
			}
			else
			{
				errorMessage.append(e.getMessage()+"("+e.getCode()+")");
			}
			return null;
		}
		catch (Exception e)
		{
			errorMessage.append(e.getMessage());
			return null;
		}
	}
	//endregion

}
