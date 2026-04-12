package com.dsc.spos.waimai.jddj;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.waimai.HelpTools;
import com.google.gson.reflect.TypeToken;

public class JDDJProductService 
{
	public static String jddjLogFileName = "jddjlog";
	//京东到家商品新增,修改也用一样的
	/**
	 * @param outSkuId 商家商品编码，商家系统中唯一编
	 * @param categoryId 京东的分类编号，这个需要用一个接口获取
	 * @param shopCategories 门店分类编号
	 * @param brandId 京东的品牌编号，这个也需要一个接口获取
	 * @param skuName 商品名称(名称+规格显示)
	 * @param skuPrice 价格门店价格是到分的需要*100转long
	 * @param weight 重量
	 * @param filename 图片地址
	 * @param fixedStatus 上下架   fixedStatus为4.删除
	 * @param isSale 是否可售
	 * @param productDesc 商品描述
	 * @param type 1新建  2 修改  3删除   fixedStatus为4.删除
	 * @return
	 * @throws Exception 
	 *需要返回一个京东到家的商品编码
	 */
	public static String createupdategoods(String outSkuId,String categoryId,String shopCategories,String brandId,String skuName,float skuPrice,float weight,String filename,String fixedStatus,String isSale,String productDesc,StringBuilder errorMessage,String type ) throws Exception 
	{ 
		if (errorMessage == null) 
		{
			errorMessage = new StringBuilder();		
	  }
		
		cgoodsmodel cgm=new cgoodsmodel();
		cgm.setOutSkuId(outSkuId);
		//常温蛋糕
		cgm.setCategoryId("20375");
		cgm.setBrandId("17659");
		cgm.setSkuName(skuName);
		//这2个字段有可能为long
	  //新建才传这个节点
		if(type.equals("1"))//后续修改商品门店价格需要通过价格类接口修改。
		{
		  cgm.setSkuPrice( (int)(Math.round(skuPrice*100))+"");
		}
		cgm.setWeight(weight+"");
		List<String> ilist=new ArrayList<String>();
		if(filename!=null&&filename.trim().length()>0)
		{
			ilist.add(filename);
			cgm.setImages(ilist);
		}
		
		//上下架状态 1.上架 2.下架，4.删除
		cgm.setFixedStatus(fixedStatus);
		
		//新建才传这个节点
		if(type.equals("1"))
		{
		  cgm.setIsSale(isSale);
		}
		if(shopCategories!=null&&shopCategories.trim().length()>0)
		{
			ilist=new ArrayList<String>();
			ilist.add(shopCategories);
			cgm.setShopCategories(ilist);
			
		}
		
		
		cgm.setIfViewDesc("1");
		cgm.setProductDesc(productDesc);
		
		//开始组返送京东的语句
		String url="";
		if(type.equals("1"))
		{
		 url = "pms/sku/addSku";//创建商品
		}
		else
		{
			url = "pms/sku/updateSku";//修改商品
		}
		ParseJson pj = new ParseJson();
		String jd_json=pj.beanToJson(cgm);
		pj=null;
		
		HelpTools.writelog_fileName("【JDDJ商品新增接口："+url+" 请求内容："+jd_json, jddjLogFileName);
		try
		{
			String responseStr = HelpJDDJHttpUtil.sendSimplePostRequest(url, jd_json);
			HelpTools.writelog_fileName("【JDDJ获取商品新增返回】 返回内容："+responseStr, jddjLogFileName);
			//下面解析开始
			JSONObject jsonObject = JSONObject.parseObject(responseStr);
			OData oResponse =	JSONObject.toJavaObject(jsonObject, OData.class);
			if (oResponse.getCode().equals("0") == false)//到家平台状态码说明0表示成功，其他均为失败
			{
				errorMessage.append(oResponse.getMsg());
				HelpTools.writelog_fileName("【JDDJ创建商品】失败："+oResponse.getMsg()+" 平台状态码="+oResponse.getCode(), jddjLogFileName);
				return null;
			}
			else
			{
				//需要解析data内容
				JSONObject jsdata = JSONObject.parseObject(oResponse.getData());
				if(jsdata.getString("code").equals("0"))
				{
					//成功
					return jsdata.getJSONObject("result").getString("skuId");
				}
				else 
				{
					//失败
					errorMessage.append(jsdata.getString("msg"));
					HelpTools.writelog_fileName("【JDDJ创建商品】失败："+oResponse.getMsg()+" 平台状态码="+oResponse.getCode(), jddjLogFileName);
					return null;
				}
			}
			
		}
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			HelpTools.writelog_fileName("【JDDJ创建商品】异常内容："+e.getMessage(), jddjLogFileName);
			return null;	
	  }
		
		
	}
	
	//查询京东到家品类
	public static String queryChildCategories(String id,StringBuilder errorMessage) throws IOException
	{
		if (errorMessage == null) 
		{
			errorMessage = new StringBuilder();		
	  }
		String url = "api/queryChildCategoriesForOP";//创建商品
		JSONObject jsob=new JSONObject();
		jsob.put("id", id);
		List<String> ilist=new ArrayList<String>();
		ilist.add("ID");
		ilist.add("PID");
		ilist.add("CATEGORY_NAME");
		ilist.add("CATEGORY_LEVEL");
		ilist.add("CATEGORY_STATUS");
		ilist.add("FULLPATH");
		jsob.put("fields", ilist);
		String jd_json=jsob.toJSONString();
		
		HelpTools.writelog_fileName("【JDDJ查询京东品类："+url+" 请求内容："+jd_json, jddjLogFileName);
		try
		{
			String responseStr = HelpJDDJHttpUtil.sendSimplePostRequest(url, jd_json);
			HelpTools.writelog_fileName("【JDDJ查询京东品类返回】 返回内容："+responseStr, jddjLogFileName);
			//下面解析开始
			JSONObject jsonObject = JSONObject.parseObject(responseStr);
			OData oResponse =	JSONObject.toJavaObject(jsonObject, OData.class);
			if (oResponse.getCode().equals("0") == false)//到家平台状态码说明0表示成功，其他均为失败
			{
				errorMessage.append(oResponse.getMsg());
				HelpTools.writelog_fileName("【JDDJ查询京东品类】失败："+oResponse.getMsg()+" 平台状态码="+oResponse.getCode(), jddjLogFileName);
				return null;
			}
			else
			{
				//需要解析data内容
				JSONObject jsdata = JSONObject.parseObject(oResponse.getData());
				if(jsdata.getString("code").equals("0"))
				{
					//成功
					return jsdata.getJSONObject("result").getString("id");
				}
				else 
				{
					//失败
					errorMessage.append(jsdata.getString("msg"));
					HelpTools.writelog_fileName("【JDDJ查询京东品类】失败："+oResponse.getMsg()+" 平台状态码="+oResponse.getCode(), jddjLogFileName);
					return null;
				}
			}
			
		}
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			HelpTools.writelog_fileName("【JDDJ查询京东品类】异常内容："+e.getMessage(), jddjLogFileName);
			return null;	
	  }
		
	}
	
	//查询京东到家品牌
	public static String queryPageBrand(String pageNo,StringBuilder errorMessage) throws IOException
	{
		if (errorMessage == null) 
		{
			errorMessage = new StringBuilder();		
	  }
		String url = "pms/queryPageBrandInfo";//创建商品
		queryPageBrandmodel qpbm=new queryPageBrandmodel();
		List<String> ilist=new ArrayList<String>();
		ilist.add("BRAND_ID");
		ilist.add("BRAND_NAME");
		ilist.add("BRAND_STATUS");
		qpbm.setFields(ilist);
		qpbm.setPageNo(pageNo);
		qpbm.setPageSize("50");
		
		ParseJson pj = new ParseJson();
		String jd_json=pj.beanToJson(qpbm);
		pj=null;
		
		HelpTools.writelog_fileName("【JDDJ查询京东品牌】："+url+" 请求内容："+jd_json, jddjLogFileName);
		try
		{
			String responseStr = HelpJDDJHttpUtil.sendSimplePostRequest(url, jd_json);
			HelpTools.writelog_fileName("【JDDJ查询京东品牌返回】 返回内容："+responseStr, jddjLogFileName);
			//下面解析开始
			JSONObject jsonObject = JSONObject.parseObject(responseStr);
			OData oResponse =	JSONObject.toJavaObject(jsonObject, OData.class);
			if (oResponse.getCode().equals("0") == false)//到家平台状态码说明0表示成功，其他均为失败
			{
				errorMessage.append(oResponse.getMsg());
				HelpTools.writelog_fileName("【JDDJ查询京东品牌】失败："+oResponse.getMsg()+" 平台状态码="+oResponse.getCode(), jddjLogFileName);
				return null;
			}
			else
			{
				//需要解析data内容
				JSONObject jsdata = JSONObject.parseObject(oResponse.getData());
				if(jsdata.getString("code").equals("0"))
				{
					//成功
					return jsdata.getJSONObject("result").getString("count");
				}
				else 
				{
					//失败
					errorMessage.append(jsdata.getString("msg"));
					HelpTools.writelog_fileName("【JDDJ查询京东品牌】失败："+oResponse.getMsg()+" 平台状态码="+oResponse.getCode(), jddjLogFileName);
					return null;
				}
			}
			
		}
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			HelpTools.writelog_fileName("【JDDJ查询京东品牌】异常内容："+e.getMessage(), jddjLogFileName);
			return null;	
	  }
		
	}
	
//新增京东分类
	/**
	 * @param shopCategoryName 门店分类名称
	 * @param sort    排序
	 * @param createPin  创建人
	 * @param errorMessage
	 * @return
	 * @throws IOException
	 */
	public static String addShopCategory(String shopCategoryName,String sort,String createPin ,StringBuilder errorMessage) throws IOException
	{
		if (errorMessage == null) 
		{
			errorMessage = new StringBuilder();		
	  }
		String url = "pms/addShopCategory";//新增商家店内分类信息接口	
		JSONObject jsobject=new JSONObject();
		jsobject.put("pid", 0);
		jsobject.put("shopCategoryName", shopCategoryName);
		if(createPin!=null&&createPin.trim().length()>0)
		{
			jsobject.put("createPin", createPin);
		}
		String jd_json= jsobject.toJSONString();
		
		HelpTools.writelog_fileName("【JDDJ新增分类】："+url+" 请求内容："+jd_json, jddjLogFileName);
		try
		{
			String responseStr = HelpJDDJHttpUtil.sendSimplePostRequest(url, jd_json);
			HelpTools.writelog_fileName("【JDDJ新增分类】 返回内容："+responseStr, jddjLogFileName);
			//下面解析开始
			JSONObject jsonObject = JSONObject.parseObject(responseStr);
			OData oResponse =	JSONObject.toJavaObject(jsonObject, OData.class);
			if (oResponse.getCode().equals("0") == false)//到家平台状态码说明0表示成功，其他均为失败
			{
				errorMessage.append(oResponse.getMsg());
				HelpTools.writelog_fileName("【JDDJ新增分类】失败："+oResponse.getMsg()+" 平台状态码="+oResponse.getCode(), jddjLogFileName);
				return null;
			}
			else
			{
				//需要解析data内容
				JSONObject jsdata = JSONObject.parseObject(oResponse.getData());
				if(jsdata.getString("code").equals("0"))
				{
					//成功
					return jsdata.getJSONObject("result").getString("id");
				}
				else 
				{
					//失败
					errorMessage.append(jsdata.getString("msg"));
					HelpTools.writelog_fileName("【JDDJ新增分类】失败："+oResponse.getMsg()+" 平台状态码="+oResponse.getCode(), jddjLogFileName);
					return null;
				}
			}
			
		}
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			HelpTools.writelog_fileName("【JDDJ新增品类】异常内容："+e.getMessage(), jddjLogFileName);
			return null;	
	  }
		
	}
	
	//修改品类信息
	public static boolean updateShopCategory(String id, String shopCategoryName,StringBuilder errorMessage) throws IOException
	{
		if (errorMessage == null) 
		{
			errorMessage = new StringBuilder();		
	  }
		String url = "pms/updateShopCategory";//修改品类
		JSONObject jsobject=new JSONObject();
		jsobject.put("id", id);
		jsobject.put("shopCategoryName", shopCategoryName);
		
		String jd_json=jsobject.toJSONString();
		
		HelpTools.writelog_fileName("【JDDJ修改分类】："+url+" 请求内容："+jd_json, jddjLogFileName);
		try
		{
			String responseStr = HelpJDDJHttpUtil.sendSimplePostRequest(url, jd_json);
			HelpTools.writelog_fileName("【JDDJ修改分类】 返回内容："+responseStr, jddjLogFileName);
			//下面解析开始
			JSONObject jsonObject = JSONObject.parseObject(responseStr);
			OData oResponse =	JSONObject.toJavaObject(jsonObject, OData.class);
			if (oResponse.getCode().equals("0") == false)//到家平台状态码说明0表示成功，其他均为失败
			{
				errorMessage.append(oResponse.getMsg());
				HelpTools.writelog_fileName("【JDDJ修改分类】失败："+oResponse.getMsg()+" 平台状态码="+oResponse.getCode(), jddjLogFileName);
				return false;
			}
			else
			{
				//需要解析data内容
				JSONObject jsdata = JSONObject.parseObject(oResponse.getData());
				if(jsdata.getString("code").equals("0"))
				{
					//成功
					return true;
				}
				else 
				{
					//失败
					errorMessage.append(jsdata.getString("msg"));
					HelpTools.writelog_fileName("【JDDJ修改分类】失败："+oResponse.getMsg()+" 平台状态码="+oResponse.getCode(), jddjLogFileName);
					return false;
				}
			}
			
		}
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			HelpTools.writelog_fileName("【JDDJ修改分类】异常内容："+e.getMessage(), jddjLogFileName);
			return false;	
	  }
		
	}
	
//删除品类信息
	public static boolean delShopCategory(String id,StringBuilder errorMessage) throws IOException
	{
		if (errorMessage == null) 
		{
			errorMessage = new StringBuilder();		
	  }
		String url = "pms/delShopCategory";//修改品类
		JSONObject jsobject=new JSONObject();
		jsobject.put("id", id);
		
		String jd_json=jsobject.toJSONString();
		
		HelpTools.writelog_fileName("【JDDJ删除品类】："+url+" 请求内容："+jd_json, jddjLogFileName);
		try
		{
			String responseStr = HelpJDDJHttpUtil.sendSimplePostRequest(url, jd_json);
			HelpTools.writelog_fileName("【JDDJ删除品类】 返回内容："+responseStr, jddjLogFileName);
			//下面解析开始
			JSONObject jsonObject = JSONObject.parseObject(responseStr);
			OData oResponse =	JSONObject.toJavaObject(jsonObject, OData.class);
			if (oResponse.getCode().equals("0") == false)//到家平台状态码说明0表示成功，其他均为失败
			{
				errorMessage.append(oResponse.getMsg());
				HelpTools.writelog_fileName("【JDDJ删除品类】失败："+oResponse.getMsg()+" 平台状态码="+oResponse.getCode(), jddjLogFileName);
				return false;
			}
			else
			{
				//需要解析data内容
				JSONObject jsdata = JSONObject.parseObject(oResponse.getData());
				if(jsdata.getString("code").equals("0"))
				{
					//成功
					return true;
				}
				else 
				{
					//失败
					errorMessage.append(jsdata.getString("msg"));
					HelpTools.writelog_fileName("【JDDJ删除品类】失败："+oResponse.getMsg()+" 平台状态码="+oResponse.getCode(), jddjLogFileName);
					return false;
				}
			}
			
		}
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			HelpTools.writelog_fileName("【JDDJ删除品类】异常内容："+e.getMessage(), jddjLogFileName);
			return false;	
	  }
		
	}
	
//修改门店商品价格 不要用这个 用第三方门店ID 2020-01-06
	public static boolean updateStationPriceBySingle(String outStationNo,String outSkuId,String price,StringBuilder errorMessage) throws IOException
	{
		if (errorMessage == null) 
		{
			errorMessage = new StringBuilder();		
	  }
		String url = "venderprice/updateStationPriceBySingle";//修改品类
		JSONObject jsobject=new JSONObject();
		jsobject.put("outStationNo", outStationNo);
		jsobject.put("outSkuId", outSkuId);
		//price价格到分所以需要处理下
		jsobject.put("price",(int)(Double.parseDouble(price)*100)+"" );
		jsobject.put("serviceNo", UUID.randomUUID().toString());
		
		String jd_json=jsobject.toJSONString();
		
		HelpTools.writelog_fileName("【JDDJ修改门店价格】："+url+" 请求内容："+jd_json, jddjLogFileName);
		try
		{
			String responseStr = HelpJDDJHttpUtil.sendSimplePostRequest(url, jd_json);
			HelpTools.writelog_fileName("【JDDJ修改门店价格】 返回内容："+responseStr, jddjLogFileName);
			//下面解析开始
			JSONObject jsonObject = JSONObject.parseObject(responseStr);
			OData oResponse =	JSONObject.toJavaObject(jsonObject, OData.class);
			if (oResponse.getCode().equals("0") == false)//到家平台状态码说明0表示成功，其他均为失败
			{
				errorMessage.append(oResponse.getMsg());
				HelpTools.writelog_fileName("【JDDJ修改门店价格】失败："+oResponse.getMsg()+" 平台状态码="+oResponse.getCode(), jddjLogFileName);
				return false;
			}
			else
			{
				//需要解析data内容
				JSONObject jsdata = JSONObject.parseObject(oResponse.getData());
				if(jsdata.getString("code").equals("0"))
				{
					//成功
					return true;
				}
				else 
				{
					//失败
					errorMessage.append(jsdata.getString("msg"));
					HelpTools.writelog_fileName("【JDDJ修改门店价格】失败："+oResponse.getMsg()+" 平台状态码="+oResponse.getCode(), jddjLogFileName);
					return false;
				}
			}
			
		}
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			HelpTools.writelog_fileName("【JDDJ修改门店价格】异常内容："+e.getMessage(), jddjLogFileName);
			return false;	
	  }
		
	}
	
	
	/**
	 * 根据到家商品编码和门店编码修改价格
	 * @param stationNo
	 * @param skuId
	 * @param price
	 * @param errorMessage
	 * @return
	 * @throws IOException
	 */
	public static boolean updateStationPrice(String stationNo,long skuId,float price,StringBuilder errorMessage) throws IOException
	{
		if (errorMessage == null) 
		{
			errorMessage = new StringBuilder();		
	  }
		String url = "price/updateStationPrice";//根据到家商品编码和到家门店编码修改门店价格接口
		JSONObject jsobject=new JSONObject();
		jsobject.put("stationNo", stationNo);
		jsobject.put("skuId", skuId);
		//price价格到分所以需要处理下
		jsobject.put("price",(int)(Math.round(price*100)));
		
		
		String jd_json=jsobject.toJSONString();
		
		HelpTools.writelog_fileName("【JDDJ修改门店价格】："+url+" 请求内容："+jd_json, jddjLogFileName);
		try
		{
			String responseStr = HelpJDDJHttpUtil.sendSimplePostRequest(url, jd_json);
			HelpTools.writelog_fileName("【JDDJ修改门店价格】 返回内容："+responseStr, jddjLogFileName);
			//下面解析开始
			JSONObject jsonObject = JSONObject.parseObject(responseStr);
			OData oResponse =	JSONObject.toJavaObject(jsonObject, OData.class);
			if (oResponse.getCode().equals("0") == false)//到家平台状态码说明0表示成功，其他均为失败
			{
				errorMessage.append(oResponse.getMsg());
				HelpTools.writelog_fileName("【JDDJ修改门店价格】失败："+oResponse.getMsg()+" 平台状态码="+oResponse.getCode(), jddjLogFileName);
				return false;
			}
			else
			{
				//需要解析data内容
				JSONObject jsdata = JSONObject.parseObject(oResponse.getData());
				if(jsdata.getString("code").equals("0"))
				{
					//成功
					return true;
				}
				else 
				{
					//失败
					errorMessage.append(jsdata.getString("msg"));
					HelpTools.writelog_fileName("【JDDJ修改门店价格】失败："+oResponse.getMsg()+" 平台状态码="+oResponse.getCode(), jddjLogFileName);
					return false;
				}
			}
			
		}
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			HelpTools.writelog_fileName("【JDDJ修改门店价格】异常内容："+e.getMessage(), jddjLogFileName);
			return false;	
	  }
		
	}
	
	
//京东修改门店商品的可售
	/** 不要使用这个，后续会删了，使用第三方门店ID 2020-01-06
	 * @param outStationNo  ERP门店编号
	 * @param userPin       操作人员
	 * @param pluno         ERP商品编号，后续可以改成list
	 * @param tyep          1可售 2 不可售
	 * @param errorMessage
	 * @return
	 * @throws IOException
	 */
	public static boolean batchUpdateVendibility(String outStationNo,String userPin,List<String> pluno,String tyep,StringBuilder errorMessage) throws IOException
	{
		if (errorMessage == null) 
		{
			errorMessage = new StringBuilder();		
	  }
		String url = "stock/batchUpdateVendibility";//修改品类
		batchUpdateVendibilitymodel buvm=new batchUpdateVendibilitymodel();
		buvm.setOutStationNo(outStationNo);
		buvm.setUserPin(userPin);
		List<StockVendibilityList> lisstovend=new ArrayList<StockVendibilityList>();
		for (String splu : pluno) {
			StockVendibilityList svb=new StockVendibilityList();
			svb.setOutSkuId(splu);
			if(tyep.equals("1"))
			{
				svb.setDoSale("true");
			}
			else
			{
				svb.setDoSale("false");
			}
			lisstovend.add(svb);
		}
		buvm.setStockVendibilityList(lisstovend);
		
		ParseJson pj = new ParseJson();
		
		String jd_json=pj.beanToJson(buvm);
		pj=null;
		
		HelpTools.writelog_fileName("【JDDJ修改门店可售】："+url+" 请求内容："+jd_json, jddjLogFileName);
		try
		{
			String responseStr = HelpJDDJHttpUtil.sendSimplePostRequest(url, jd_json);
			HelpTools.writelog_fileName("【JDDJ修改门店可售】 返回内容："+responseStr, jddjLogFileName);
			//下面解析开始
			JSONObject jsonObject = JSONObject.parseObject(responseStr);
			OData oResponse =	JSONObject.toJavaObject(jsonObject, OData.class);
			if (oResponse.getCode().equals("0") == false)//到家平台状态码说明0表示成功，其他均为失败
			{
				errorMessage.append(oResponse.getMsg());
				HelpTools.writelog_fileName("【JDDJ修改门店可售】失败："+oResponse.getMsg()+" 平台状态码="+oResponse.getCode(), jddjLogFileName);
				return false;
			}
			else
			{
				//需要解析data内容
				JSONObject jsdata = JSONObject.parseObject(oResponse.getData());
				if(jsdata.getString("retCode").equals("0"))
				{
					//成功
					JSONObject retdata = jsdata.getJSONObject("data");
					if(retdata.getString("code").equals("0"))
					{
						return true;
					}
					else
					{
						errorMessage.append(retdata.getString("msg"));
						HelpTools.writelog_fileName("【JDDJ修改门店可售】失败："+retdata.getString("msg")+" 平台状态码="+retdata.getString("msg"), jddjLogFileName);
						return false;
					}
					
				}
				else 
				{
					//失败
					errorMessage.append(jsdata.getString("msg"));
					HelpTools.writelog_fileName("【JDDJ修改门店可售】失败："+jsdata.getString("retMsg")+" 平台状态码="+jsdata.getString("retCode"), jddjLogFileName);
					return false;
				}
			}
			
		}
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			HelpTools.writelog_fileName("【JDDJ修改门店可售】异常内容："+e.getMessage(), jddjLogFileName);
			return false;	
	  }
		
	}
	
	/**
	 * 根据到家商品编码和门店编码批量修改门店商品可售状态
	 * @param stationNo
	 * @param skuIds
	 * @param doSale
	 * @param errorMessage
	 * @throws IOException
	 */
	public static boolean updateVendibility(String stationNo,List<Long> skuIds,int doSale,StringBuilder errorMessage) throws IOException
	{
		if (errorMessage == null) 
		{
			errorMessage = new StringBuilder();		
	  }
		String url = "stock/updateVendibility";//根据到家商品编码和到家门店编码批量修改门店商品可售状态接口
		JSONObject jsobject = new JSONObject();
		JSONArray jsarray = new JSONArray();
		for (Long sku : skuIds) 
		{
			JSONObject skuObj = new JSONObject();
			skuObj.put("stationNo", stationNo);
			skuObj.put("skuId", sku);
			skuObj.put("doSale", doSale);
			jsarray.add(skuObj);
	  }
		jsobject.put("listBaseStockCenterRequest", jsarray);
		
		String jd_json=jsobject.toJSONString();
		
		HelpTools.writelog_fileName("【JDDJ修改门店可售】："+url+" 请求内容："+jd_json, jddjLogFileName);
		try
		{
			String responseStr = HelpJDDJHttpUtil.sendSimplePostRequest(url, jd_json);
			HelpTools.writelog_fileName("【JDDJ修改门店可售】 返回内容："+responseStr, jddjLogFileName);
			//下面解析开始
			JSONObject jsonObject = JSONObject.parseObject(responseStr);
			OData oResponse =	JSONObject.toJavaObject(jsonObject, OData.class);
			if (oResponse.getCode().equals("0") == false)//到家平台状态码说明0表示成功，其他均为失败
			{
				errorMessage.append(oResponse.getMsg());
				HelpTools.writelog_fileName("【JDDJ修改门店可售】失败："+oResponse.getMsg()+" 平台状态码="+oResponse.getCode(), jddjLogFileName);
				return false ;
			}
			else
			{				
				String data = oResponse.getData();							
				//换一种写法 fastejson有可能会有特殊字符转义的问题
				ParseJson pj=new ParseJson();				
				OUpdateVendibilityResult oResult = pj.jsonToBean(data, new TypeToken<OUpdateVendibilityResult>(){});
				pj=null;
				
				if(oResult.getRetCode().equals("0"))
				{
					//成功
					return true;										
				}
				else 
				{
					//失败
					errorMessage.append(oResult.getRetMsg());
					HelpTools.writelog_fileName("【JDDJ修改门店可售】失败："+oResult.getRetMsg()+" 平台状态码="+oResult.getRetCode(), jddjLogFileName);
					return false;
				}
			}
			
		}
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			HelpTools.writelog_fileName("【JDDJ修改门店可售】异常内容："+e.getMessage(), jddjLogFileName);
			return  false;	
	  }
	}
	
	
//修改门店商品库存
	public static boolean stockupdate(String stationNo,long skuId,int currentQty,StringBuilder errorMessage) throws IOException
	{
		if (errorMessage == null) 
		{
			errorMessage = new StringBuilder();		
	  }
		String url = "update/currentQty";//根据到家商品编码、到家门店编码更新门店现货库存
		JSONObject jsobject=new JSONObject();
		jsobject.put("stationNo", stationNo);
		jsobject.put("skuId", skuId);
		//price价格到分所以需要处理下
		jsobject.put("currentQty",currentQty );
		
		String jd_json=jsobject.toJSONString();
		
		HelpTools.writelog_fileName("【JDDJ修改门店库存】："+url+" 请求内容："+jd_json, jddjLogFileName);
		try
		{
			String responseStr = HelpJDDJHttpUtil.sendSimplePostRequest(url, jd_json);
			HelpTools.writelog_fileName("【JDDJ修改门店库存】 返回内容："+responseStr, jddjLogFileName);
			//下面解析开始
			JSONObject jsonObject = JSONObject.parseObject(responseStr);
			OData oResponse =	JSONObject.toJavaObject(jsonObject, OData.class);
			if (oResponse.getCode().equals("0") == false)//到家平台状态码说明0表示成功，其他均为失败
			{
				errorMessage.append(oResponse.getMsg());
				HelpTools.writelog_fileName("【JDDJ修改门店库存】失败："+oResponse.getMsg()+" 平台状态码="+oResponse.getCode(), jddjLogFileName);
				return false;
			}
			else
			{
				//需要解析data内容
				JSONObject jsdata = JSONObject.parseObject(oResponse.getData());
				if(jsdata.getString("retCode").equals("0"))
				{
					//成功
					return true;
				}
				else 
				{
					//失败
					errorMessage.append(jsdata.getString("msg"));
					HelpTools.writelog_fileName("【JDDJ修改门店库存】失败："+jsdata.getString("retMsg")+" 平台状态码="+jsdata.getString("retMsg"), jddjLogFileName);
					return false;
				}
			}
			
		}
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			HelpTools.writelog_fileName("【JDDJ修改门店库存】异常内容："+e.getMessage(), jddjLogFileName);
			return false;	
	  }
		
	}
	
	
}



 class batchUpdateVendibilitymodel {

   private String outStationNo;
   private String stationNo;
   private String userPin;
   private List<StockVendibilityList> stockVendibilityList;
   public void setOutStationNo(String outStationNo) {
        this.outStationNo = outStationNo;
    }
    public String getOutStationNo() {
        return outStationNo;
    }

   public void setStationNo(String stationNo) {
        this.stationNo = stationNo;
    }
    public String getStationNo() {
        return stationNo;
    }

   public void setUserPin(String userPin) {
        this.userPin = userPin;
    }
    public String getUserPin() {
        return userPin;
    }

   public void setStockVendibilityList(List<StockVendibilityList> stockVendibilityList) {
        this.stockVendibilityList = stockVendibilityList;
    }
    public List<StockVendibilityList> getStockVendibilityList() {
        return stockVendibilityList;
    }

}

  class StockVendibilityList {

    private String outSkuId;
    private String doSale;
    public void setOutSkuId(String outSkuId) {
         this.outSkuId = outSkuId;
     }
     public String getOutSkuId() {
         return outSkuId;
     }

    public void setDoSale(String doSale) {
         this.doSale = doSale;
     }
     public String getDoSale() {
         return doSale;
     }

}
 

class addShopCategorymodel {

   private String pid;
   private String shopCategoryName;
   private String shopCategoryLevel;
   private String sort;
   private String createPin;
   public void setPid(String pid) {
        this.pid = pid;
    }
    public String getPid() {
        return pid;
    }

   public void setShopCategoryName(String shopCategoryName) {
        this.shopCategoryName = shopCategoryName;
    }
    public String getShopCategoryName() {
        return shopCategoryName;
    }

   public void setShopCategoryLevel(String shopCategoryLevel) {
        this.shopCategoryLevel = shopCategoryLevel;
    }
    public String getShopCategoryLevel() {
        return shopCategoryLevel;
    }

   public void setSort(String sort) {
        this.sort = sort;
    }
    public String getSort() {
        return sort;
    }

   public void setCreatePin(String createPin) {
        this.createPin = createPin;
    }
    public String getCreatePin() {
        return createPin;
    }

}


class queryPageBrandmodel {

  private String brandName;
  private String brandId;
  private String pageNo;
  private String pageSize;
  private List<String> fields;
  public void setBrandName(String brandName) {
       this.brandName = brandName;
   }
   public String getBrandName() {
       return brandName;
   }

  public void setBrandId(String brandId) {
       this.brandId = brandId;
   }
   public String getBrandId() {
       return brandId;
   }

  public void setPageNo(String pageNo) {
       this.pageNo = pageNo;
   }
   public String getPageNo() {
       return pageNo;
   }

  public void setPageSize(String pageSize) {
       this.pageSize = pageSize;
   }
   public String getPageSize() {
       return pageSize;
   }

  public void setFields(List<String> fields) {
       this.fields = fields;
   }
   public List<String> getFields() {
       return fields;
   }

}




class cgoodsmodel {

	private String outSkuId;
  private String categoryId;
  private String brandId;
  private String skuName;
  private String weight;
  private String upcCode;
  private String fixedStatus;
  private String productDesc;
  private String ifViewDesc;
  private String length;
  private String width;
  private String height;
  private String slogan;
  private String sloganStartTime;
  private String sloganEndTime;
  private String prefixKeyId;
  private String prefixKey;
  private String preKeyStartTime;
  private String preKeyEndTime;
  private String transportAttribute;
  private String liquidStatue;
  private String prescripition;
  private String skuPrice;
  private String isSale;
  
  private List<String> shopCategories;
  private List<String> images;
  private List<String> sellCities;
  public void setOutSkuId(String outSkuId) {
       this.outSkuId = outSkuId;
   }
   public String getOutSkuId() {
       return outSkuId;
   }

  public void setCategoryId(String categoryId) {
       this.categoryId = categoryId;
   }
   public String getCategoryId() {
       return categoryId;
   }

  public void setBrandId(String brandId) {
       this.brandId = brandId;
   }
   public String getBrandId() {
       return brandId;
   }

  public void setSkuName(String skuName) {
       this.skuName = skuName;
   }
   public String getSkuName() {
       return skuName;
   }

  public void setWeight(String weight) {
       this.weight = weight;
   }
   public String getWeight() {
       return weight;
   }

  public void setUpcCode(String upcCode) {
       this.upcCode = upcCode;
   }
   public String getUpcCode() {
       return upcCode;
   }

  public void setFixedStatus(String fixedStatus) {
       this.fixedStatus = fixedStatus;
   }
   public String getFixedStatus() {
       return fixedStatus;
   }

  public void setProductDesc(String productDesc) {
       this.productDesc = productDesc;
   }
   public String getProductDesc() {
       return productDesc;
   }

  public void setIfViewDesc(String ifViewDesc) {
       this.ifViewDesc = ifViewDesc;
   }
   public String getIfViewDesc() {
       return ifViewDesc;
   }

  public void setLength(String length) {
       this.length = length;
   }
   public String getLength() {
       return length;
   }

  public void setWidth(String width) {
       this.width = width;
   }
   public String getWidth() {
       return width;
   }

  public void setHeight(String height) {
       this.height = height;
   }
   public String getHeight() {
       return height;
   }

  public void setSlogan(String slogan) {
       this.slogan = slogan;
   }
   public String getSlogan() {
       return slogan;
   }

  public void setSloganStartTime(String sloganStartTime) {
       this.sloganStartTime = sloganStartTime;
   }
   public String getSloganStartTime() {
       return sloganStartTime;
   }

  public void setSloganEndTime(String sloganEndTime) {
       this.sloganEndTime = sloganEndTime;
   }
   public String getSloganEndTime() {
       return sloganEndTime;
   }

  public void setPrefixKeyId(String prefixKeyId) {
       this.prefixKeyId = prefixKeyId;
   }
   public String getPrefixKeyId() {
       return prefixKeyId;
   }

  public void setPrefixKey(String prefixKey) {
       this.prefixKey = prefixKey;
   }
   public String getPrefixKey() {
       return prefixKey;
   }

  public void setPreKeyStartTime(String preKeyStartTime) {
       this.preKeyStartTime = preKeyStartTime;
   }
   public String getPreKeyStartTime() {
       return preKeyStartTime;
   }

  public void setPreKeyEndTime(String preKeyEndTime) {
       this.preKeyEndTime = preKeyEndTime;
   }
   public String getPreKeyEndTime() {
       return preKeyEndTime;
   }

  public void setTransportAttribute(String transportAttribute) {
       this.transportAttribute = transportAttribute;
   }
   public String getTransportAttribute() {
       return transportAttribute;
   }

  public void setLiquidStatue(String liquidStatue) {
       this.liquidStatue = liquidStatue;
   }
   public String getLiquidStatue() {
       return liquidStatue;
   }

  public void setPrescripition(String prescripition) {
       this.prescripition = prescripition;
   }
   public String getPrescripition() {
       return prescripition;
   }

  public void setShopCategories(List<String> shopCategories) {
       this.shopCategories = shopCategories;
   }
   public List<String> getShopCategories() {
       return shopCategories;
   }

  public void setImages(List<String> images) {
       this.images = images;
   }
   public List<String> getImages() {
       return images;
   }

  public void setSellCities(List<String> sellCities) {
       this.sellCities = sellCities;
   }
   public List<String> getSellCities() {
       return sellCities;
   }
	public String getSkuPrice() {
	return skuPrice;
	}
	public void setSkuPrice(String skuPrice) {
	this.skuPrice = skuPrice;
	}
	public String getIsSale() {
	return isSale;
	}
	public void setIsSale(String isSale) {
	this.isSale = isSale;
	}

}




