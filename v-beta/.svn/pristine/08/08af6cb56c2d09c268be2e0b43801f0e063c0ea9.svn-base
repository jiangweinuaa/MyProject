package com.dsc.spos.scheduler.job;

import java.sql.SQLSyntaxErrorException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.ExecBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderPlatformGoodsQueryReq;
import com.dsc.spos.json.cust.req.DCP_OrderPlatformOnshelfUpdateReq.level1goodsElm;
import com.dsc.spos.json.cust.res.DCP_OrderQueryRes;
import com.dsc.spos.json.cust.res.DCP_OrderPlatformGoodsQueryRes;
import com.dsc.spos.json.cust.res.DCP_OrderPlatformOnshelfUpdateRes;
import com.dsc.spos.json.cust.res.DCP_OrderPlatformGoodsQueryRes.level1Elm;
import com.dsc.spos.json.cust.res.DCP_OrderPlatformGoodsQueryRes.level2Attribute;
import com.dsc.spos.json.cust.res.DCP_OrderPlatformGoodsQueryRes.level2Spec;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.WMELMProductService;
import com.dsc.spos.waimai.WMJBPProductService;
import com.dsc.spos.waimai.model.WMJBPGoodsUpdate;
import com.dsc.spos.waimai.model.WMJBPQueryListByEPoiId;
import com.dsc.spos.waimai.model.WMJBPQueryListByEPoiId.Data;

import eleme.openapi.sdk.api.entity.product.OBatchModifiedResult;
import eleme.openapi.sdk.api.entity.product.OItem;
import eleme.openapi.sdk.api.entity.product.OItemAttribute;
import eleme.openapi.sdk.api.entity.product.OItemSellingTime;
import eleme.openapi.sdk.api.entity.product.OItemTime;
import eleme.openapi.sdk.api.entity.product.OMaterial;
import eleme.openapi.sdk.api.entity.product.OSpec;
import eleme.openapi.sdk.api.enumeration.product.IdType;
import eleme.openapi.sdk.api.enumeration.product.OItemWeekEnum;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class OrderPlatformGoodsGet extends InitJob  
{
	Logger logger = LogManager.getLogger(OrderPlatformGoodsGet.class.getName());
	
	static boolean bRun=false;//标记此服务是否正在执行中
	String goodsLogFileName = "GoodsSaveLocalJob";
	
	public OrderPlatformGoodsGet()
	{
		
	}
	
	public String doExe() throws Exception
	{
		String sReturnInfo = "";
		logger.info("\r\n***************JDDJOrderGet同步START****************\r\n");
		HelpTools.writelog_fileName("【OrderPlatformGoodsGet同步商品资料】同步START！",goodsLogFileName);
		try 
		{
			Calendar cal = Calendar.getInstance();// 获得当前时间
			SimpleDateFormat dfDate = new SimpleDateFormat("yyyyMMdd");
		
			String curDate=dfDate.format(cal.getTime());	
			//时间
			SimpleDateFormat dfTime=new SimpleDateFormat("HHmmss");
			String mySysTime = dfTime.format(cal.getTime());	
			
			String edateTimeStart="000000"; //默认开始时间
			String edateTimeEnd="015959";   //默认结束时间
			//当前时间在开始-结束中间，才能执行
			if(mySysTime.compareTo(edateTimeStart)>=0 && mySysTime.compareTo(edateTimeEnd)<0) 
			{
				
			}
			else
			{
				HelpTools.writelog_fileName("【OrderPlatformGoodsGet同步商品资料】同步正在执行中,本次调用取消！当前时间："+mySysTime+" 不在执行时间范围内("+edateTimeStart+"-"+edateTimeEnd+")",goodsLogFileName);
				logger.info("\r\n*********OrderPlatformGoodsGet同步正在执行中,本次调用取消:不在执行时间范围内("+edateTimeStart+"-"+edateTimeEnd+")************\r\n");
				return "";
						
			}
			
			
		  //此服务是否正在执行中
			if (bRun)
			{		
				logger.info("\r\n*********OrderPlatformGoodsGet同步正在执行中,本次调用取消:************\r\n");
				HelpTools.writelog_fileName("【OrderPlatformGoodsGet同步商品资料】同步正在执行中,本次调用取消！",goodsLogFileName);
				return sReturnInfo;
			}

			bRun=true;//
			
			/*RedisPosPub redis = new RedisPosPub();	
			String redis_key = "ELM_Token";
			String accessTokenStr = redis.getString(redis_key);//包含了过期时间戳
			redis.Close();		*/
			try 
			{
				String sql = this.getQuerySql("");
				
				List<Map<String, Object>> getQData = this.doQueryData(sql, null);
				if (getQData != null && getQData.isEmpty() == false)
				{
					for (Map<String, Object> mapShop : getQData)
					{
						try 
						{
							String eId = mapShop.get("EID").toString();
							String belFirm = mapShop.get("BELFIRM2").toString();
							String loadDocType = mapShop.get("LOAD_DOCTYPE").toString();
							String erpShopNO = mapShop.get("SHOPID").toString();
							String orderShopNO = mapShop.get("ORDERSHOPNO").toString();	
							String orderShopName = mapShop.get("ORDERSHOPNAME").toString();						
							String elmAPPKey = mapShop.get("APPKEY").toString();;
							String elmAPPSecret = mapShop.get("APPSECRET").toString();;
							String elmAPPName = mapShop.get("APPNAME").toString();;		
							String elmIsTest = mapShop.get("ISTEST").toString();
							
							DCP_OrderPlatformGoodsQueryRes res = new DCP_OrderPlatformGoodsQueryRes();
							res.setDatas(new ArrayList<DCP_OrderPlatformGoodsQueryRes.level1Elm>());
							HelpTools.writelog_fileName("【同步商品资料到本地】获取当前门店商品资料开始！ 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+erpShopNO, goodsLogFileName);
							//查询菜品池商品，
							String	sql_tvGoods = "select b.pluno,b.pluname,a.specno,a.specname,b.filename,a.netweight,b.categoryno,b.belfirm from OC_goods_spec a "
										+ " left join OC_goods b on a.EID=b.EID and a.pluno=b.pluno and a.belfirm=b.belfirm"
										+ " where a.EID='"+ eId+"'";	
								
							if (belFirm != null && belFirm.trim().length() > 0)
							{
								sql_tvGoods +=" and a.belfirm='"+belFirm+"'";
							}
							HelpTools.writelog_fileName("【同步商品资料到本地】 获取菜品池SQL:"+sql_tvGoods,  goodsLogFileName);
							List<Map<String, Object>> getGoodsQData = this.doQueryData(sql_tvGoods, null);
							
							try 
							{
								if(loadDocType.equals("1"))//饿了么
								{
									long shopId =	Long.parseLong(orderShopNO);
									long pageIndex = 0;
									long pageSize = 200;//
									StringBuilder errorMessage = new StringBuilder("");
									boolean isExist = false;//商品个数是否大于pageSize								
									Boolean isGoNewFunction = false;//是否走新的接口//多个总店账号							
									boolean elmIsSandbox = false;
									if (elmAPPKey != null && elmAPPKey.trim().length() > 0 && elmAPPSecret != null && elmAPPSecret.trim().length() > 0)
									{														
										if (elmIsTest != null && elmIsTest.equals("Y"))
										{
											elmIsSandbox = true;
										}
										isGoNewFunction = true;
									}

									List<OItem>	products = null;
									if(isGoNewFunction)
									{
										products = WMELMProductService.queryItemByPage(elmIsSandbox,elmAPPKey,elmAPPSecret,elmAPPName,shopId, pageIndex, pageSize, errorMessage);
									}
									else
									{
										products = WMELMProductService.queryItemByPage(shopId, pageIndex, pageSize, errorMessage);
									}
									if (products == null || products.size() == 0)
									{
										HelpTools.writelog_fileName("【同步商品资料到本地】获取该门店商品资料完成！该门店没有商品资料！ 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+erpShopNO, goodsLogFileName);
										continue;
									}

									if (products.size() > pageSize)
									{
										isExist = true;
									}
									//如果pageIndex=0 查询的结果大于pageSize，那么进行循环
									while(isExist)
									{	
										try 
										{
											pageIndex++;
											List<OItem>	oItems = null;
											if(isGoNewFunction)
											{
												oItems = WMELMProductService.queryItemByPage(elmIsSandbox,elmAPPKey,elmAPPSecret,elmAPPName,shopId, pageIndex, pageSize, errorMessage);							
											}
											else
											{
												oItems = WMELMProductService.queryItemByPage(shopId, pageIndex, pageSize, errorMessage);							
											}

											if (oItems == null || oItems.size() == 0)
											{
												isExist = false;
												break;
											}
											//添加到商品列表
											for (OItem oItem : oItems) 
											{
												products.add(oItem);			
											}										
											if (oItems.size() > pageSize)
											{
												isExist = true;
											}
											else 
											{
												isExist = false;
												break;			
											}					
										} 
										catch (Exception e) 
										{
											isExist = false;
											break;		
										}							
									}

									HelpTools.writelog_fileName("【同步商品资料到本地】获取该门店商品资料完成！ 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+erpShopNO+" 获取的商品资料总个数："+products.size(), goodsLogFileName);	
									HelpTools.writelog_fileName("【同步商品资料到本地】解析该门店商品资料并保存到本地开始！ 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+erpShopNO+" 获取的商品资料总个数："+products.size(), goodsLogFileName);
									for (OItem oItem : products) 
									{
										try 
										{
											DCP_OrderPlatformGoodsQueryRes.level1Elm oneLv1 = res.new level1Elm();
											oneLv1.setSpecs(new ArrayList<DCP_OrderPlatformGoodsQueryRes.level2Spec>());
											oneLv1.setAttributes(new ArrayList<DCP_OrderPlatformGoodsQueryRes.level2Attribute>());
											String orderPluNO = String.valueOf(oItem.getId());//商品Id
											String orderPluName = oItem.getName();//商品名
											String orderCategoryNO = String.valueOf(oItem.getCategoryId());//饿了么分类ID
											String orderDescription = oItem.getDescription();
											String orderUnit = oItem.getUnit();
											String orderImageUrl = oItem.getImageUrl();

											oneLv1.setOrderPluNO(orderPluNO);
											oneLv1.setOrderPluName(orderPluName);
											oneLv1.setOrderCategoryNO(orderCategoryNO);
											oneLv1.setOrderCategoryName("");//没有返回分类名称
											oneLv1.setOrderDescription(orderDescription);
											oneLv1.setOrderImageUrl(orderImageUrl);
											oneLv1.setOrderUnit(orderUnit);



											List<OMaterial> oMaterials = oItem.getMaterials();
											if (oMaterials != null && oMaterials.size() > 0)
											{
												int i=0;
												for (OMaterial oMaterial : oMaterials) 
												{
													if(i==0)
													{
														oneLv1.setMaterialID1(oMaterial.getId()+"");
														oneLv1.setMaterial1(oMaterial.getName());
													}
													if(i==1)
													{
														oneLv1.setMaterialID2(oMaterial.getId()+"");
														oneLv1.setMaterial2(oMaterial.getName());
													}
													if(i==2)
													{
														oneLv1.setMaterialID3(oMaterial.getId()+"");
														oneLv1.setMaterial3(oMaterial.getName());
													}
													if(i==3)
													{
														oneLv1.setMaterialID4(oMaterial.getId()+"");
														oneLv1.setMaterial4(oMaterial.getName());
													}
													if(i==4)
													{
														oneLv1.setMaterialID5(oMaterial.getId()+"");
														oneLv1.setMaterial5(oMaterial.getName());
													}
													if(i==5)
													{
														oneLv1.setMaterialID6(oMaterial.getId()+"");
														oneLv1.setMaterial6(oMaterial.getName());
													}
													if(i==6)
													{
														oneLv1.setMaterialID7(oMaterial.getId()+"");
														oneLv1.setMaterial7(oMaterial.getName());
													}
													if(i==7)
													{
														oneLv1.setMaterialID8(oMaterial.getId()+"");
														oneLv1.setMaterial8(oMaterial.getName());
													}
													if(i==8)
													{
														oneLv1.setMaterialID9(oMaterial.getId()+"");
														oneLv1.setMaterial9(oMaterial.getName());
													}
													if(i==9)
													{
														oneLv1.setMaterialID10(oMaterial.getId()+"");
														oneLv1.setMaterial10(oMaterial.getName());
													}
													i++;
												}

											}


											List<OSpec> oSpecs = oItem.getSpecs();
											if (oSpecs != null && oSpecs.size() > 0)
											{
												for (OSpec oSpec : oSpecs) 
												{	
													DCP_OrderPlatformGoodsQueryRes.level2Spec orderSpec = res.new level2Spec();
													String orderSpecID = String.valueOf(oSpec.getSpecId());//规格Id
													String orderSpecName = oSpec.getName();//名称
													String orderPrice = String.valueOf(oSpec.getPrice());
													String orderStock = String.valueOf(oSpec.getStock());
													String orderPackingFee = String.valueOf(oSpec.getPackingFee());
													float  netWeight = oSpec.getWeight(); 
													int onShelf =oSpec.getOnShelf();//0-不上架；1-上架	
													String orderOnShelf = "N";
													if (onShelf == 1)
													{
														orderOnShelf = "Y";
													}

													String orderSpecpluBarcode = oSpec.getExtendCode();//商品扩展码 (ERP的商品条码)真正映射的商品条码

													orderSpec.setOrderSpecID(orderSpecID);
													orderSpec.setOrderSpecName(orderSpecName);
													orderSpec.setOrderPrice(orderPrice);
													orderSpec.setOrderStock(orderStock);
													orderSpec.setOrderPackingFee(orderPackingFee);
													orderSpec.setOrderOnShelf(orderOnShelf);
													orderSpec.setPluBarcode(orderSpecpluBarcode);
													orderSpec.setPluSpecName("");
													orderSpec.setNetWeight(netWeight);

													oneLv1.getSpecs().add(orderSpec);	
													orderSpec=null;
												}				

											}

											//属性
											List<OItemAttribute> oAttributes = oItem.getAttributes();
											if (oAttributes != null && oAttributes.size() > 0)
											{
												for (OItemAttribute oItemAttribute : oAttributes) 
												{
													DCP_OrderPlatformGoodsQueryRes.level2Attribute orderAttribute = res.new level2Attribute();
													String attributeName = oItemAttribute.getName();
													List<String> attributeDetails = oItemAttribute.getDetails();
													orderAttribute.setName(attributeName);
													orderAttribute.setDetails(attributeDetails);
													oneLv1.getAttributes().add(orderAttribute);
													orderAttribute=null;				
								        }
												
											}
											
											//新增时间段

											String sellweek="" ;
											String selltime="" ;
											OItemSellingTime SellingTime = oItem.getSellingTime();

											if (SellingTime==null)
											{
												oneLv1.setIsAllTimeSell("Y");
												oneLv1.setSellWeek("");
												oneLv1.setSellTime("");	
												oneLv1.setBeginDate("");
												oneLv1.setEndDate("");
											}
											else
											{
												if (Check.Null( SellingTime.getBeginDate())|| Check.Null( SellingTime.getEndDate()))
												{
													oneLv1.setBeginDate("1970-01-01");
													oneLv1.setEndDate("2070-01-01");
												}
												else
												{
													oneLv1.setBeginDate(SellingTime.getBeginDate());
													oneLv1.setEndDate(SellingTime.getEndDate());
												}

												List <OItemWeekEnum> weeks = SellingTime.getWeeks();
												if (weeks != null && weeks.size()>0) 
												{
													for (OItemWeekEnum week :weeks )
													{
														if (week==OItemWeekEnum.MONDAY) 
															sellweek=sellweek+"1,";
														else if (week==OItemWeekEnum.TUESDAY) 
															sellweek=sellweek+"2,";
														else if (week==OItemWeekEnum.WEDNESDAY) 
															sellweek=sellweek+"3,";
														else if (week==OItemWeekEnum.THURSDAY) 
															sellweek=sellweek+"4,";
														else if (week==OItemWeekEnum.FRIDAY) 
															sellweek=sellweek+"5,";
														else if (week==OItemWeekEnum.SATURDAY) 
															sellweek=sellweek+"6,";
														else if (week==OItemWeekEnum.SUNDAY) 
															sellweek=sellweek+"7,";									
													}
													sellweek=sellweek.substring(0, sellweek.length()-1);
												}
												else
												{
													sellweek ="1,2,3,4,5,6,7";
												}

												List <OItemTime> times = SellingTime.getTimes();
												if (times != null && times.size()>0) 
												{
													for (OItemTime time :times )
													{
														try 
														{
															selltime=selltime+time.getBeginTime().substring(0,5)+"-"+ time.getEndTime().substring(0,5)+",";   //格式：HH:mm															
														} 
														catch (Exception e) 
														{

														}

													}
													selltime=selltime.substring(0, selltime.length()-1);
												}
												else
												{
													selltime="00:00-23:59";
												}


												oneLv1.setSellWeek(sellweek);
												oneLv1.setSellTime(selltime);	
												oneLv1.setIsAllTimeSell("N");
											}

											res.getDatas().add(oneLv1);		
											oneLv1=null;

										} 
										catch (Exception e) 
										{		    	
											continue;		
										}
									}
									
								}
								else if(loadDocType.equals("2"))
								{
									
									StringBuilder errorMessage = new StringBuilder("");
									String resultJson = WMJBPProductService.queryItemByEPoiId(eId, erpShopNO, errorMessage);
									if(resultJson == null ||resultJson.isEmpty()||resultJson.length()==0)
									{					
										HelpTools.writelog_fileName("【同步商品资料到本地】获取该门店商品资料完成！该门店没有商品资料！ 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+erpShopNO, goodsLogFileName);
										continue;					
									}
									HelpTools.writelog_fileName("【同步商品资料到本地】获取该门店商品资料完成！ 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+erpShopNO+" 返回的Respone:\n"+resultJson, goodsLogFileName);
									JSONObject jsonObject = new JSONObject(resultJson);			   
									try 
									{
										JSONArray array = jsonObject.getJSONArray("data");
										if(array !=null && array.length()>0)
										{	  	    	
											for (int i = 0; i < array.length(); i++)
											{
												DCP_OrderPlatformGoodsQueryRes.level1Elm oneLv1 = res.new level1Elm();
												oneLv1.setSpecs(new ArrayList<DCP_OrderPlatformGoodsQueryRes.level2Spec>());
												try 
												{						
													JSONObject item = array.getJSONObject(i);

													String orderPluNO = item.get("dishId").toString();     //美团方菜品id
													String orderPluName = item.get("dishName").toString(); //美团方商品名
													String orderCategoryNO = item.get("categoryName").toString();    // 美团分类ID 和名称 一样
													String orderCategoryName = item.get("categoryName").toString();  //美团分类名称
													String pluNO=item.get("eDishCode").toString();         //ERP方菜品id		


													oneLv1.setOrderPluNO(orderPluNO);
													oneLv1.setOrderPluName(orderPluName);
													oneLv1.setPluNO(pluNO);
													oneLv1.setOrderCategoryNO(orderCategoryNO);//美团分类ID 和名称 一样
													oneLv1.setOrderCategoryName(orderCategoryName);
													oneLv1.setOrderImageUrl("");  //此接口调用不提供图片和单位
													oneLv1.setOrderUnit("份");
													//oneLv1.setOrderDescription(description);

													try 
													{
														JSONArray array_sku = item.getJSONArray("waiMaiDishSkuBases");
														if (array_sku != null && array_sku.length() > 0)
														{				    		
															for (int j = 0; j < array_sku.length(); j++)
															{
																DCP_OrderPlatformGoodsQueryRes.level2Spec oneLv2 = res.new level2Spec();
																try 
																{
																	JSONObject item_sku = array_sku.getJSONObject(j);
																	String orderSpecID = item_sku.get("dishSkuId").toString();//美团方菜品sku id	
																	String orderSpecpluBarcode = item_sku.get("eDishSkuCode").toString();//商品扩展码 (ERP的商品条码)真正映射的商品条码
																	String orderSpecName = item_sku.get("spec").toString();//美团方菜品名称
																	String orderPrice = item_sku.get("price").toString();

																	float boxPrice = 0;
																	float boxNum = 0;
																	if(item_sku.isNull("boxPrice")&&item_sku.isNull("boxNum"))
																	{
																		try 
																		{
																			boxPrice = Float.parseFloat(item_sku.get("boxPrice").toString());							
																		} 
																		catch (Exception e) 
																		{
																			boxPrice = 0;								
																		}		
																		try 
																		{
																			boxNum = Float.parseFloat(item_sku.get("boxNum").toString());							
																		} 
																		catch (Exception e) 
																		{
																			boxNum = 0;								
																		}		
																	}
																	float orderPackingFee = boxPrice * boxNum;

																	oneLv2.setOrderSpecID(orderSpecID);
																	oneLv2.setOrderSpecName(orderSpecName);
																	oneLv2.setOrderPrice(orderPrice);
																	oneLv2.setOrderStock("9999");											
																	oneLv2.setOrderOnShelf("Y");
																	oneLv2.setOrderPackingFee(String.valueOf(orderPackingFee));
																	oneLv2.setPluBarcode(orderSpecpluBarcode);
																	oneLv2.setPluSpecName("");
																	oneLv1.getSpecs().add(oneLv2);		
																} 
																catch (Exception e) 
																{
																	continue;					
																}					    							    							    			
															}
														}

													} 
													catch (Exception e) 
													{
														continue;			
													}

													res.getDatas().add(oneLv1);
													oneLv1=null;
												} 
												catch (Exception e) 
												{							
													//res.getDatas().add(oneLv1);
													continue;						
												}					
											}	 	    	    											
										}
										else
										{ 	    	
											HelpTools.writelog_fileName("【同步商品资料到本地】获取该门店商品资料完成！【美团聚宝盆】没有data节点解析不了！ 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+erpShopNO, goodsLogFileName);
											continue;
										}

									} 
									catch (Exception e) 
									{
										HelpTools.writelog_fileName("【同步商品资料到本地】获取该门店商品资料完成！【美团聚宝盆】解析返回的内容异常："+e.getMessage()+" 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+erpShopNO, goodsLogFileName);
										continue;
									}											
									//在查一次接口获取其他信息
									HelpTools.writelog_fileName("【同步商品资料到本地】获取该门店商品资料完成！【美团聚宝盆】平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+erpShopNO+" 添加到RES商品资料总个数："+res.getDatas().size(), goodsLogFileName);
									try 
									{
										//int pageIndex = 1;
										int pageCount = 0;
										int pageSize = 200;
										List<Data> products = WMJBPProductService.queryListByEPoiId(eId, erpShopNO, pageCount, pageSize, errorMessage);
										if(products==null||products.isEmpty())
										{
											continue;
										}
										boolean isExist = false;//商品个数是否大于pageSize
										if(products.size()>pageSize)
										{
											isExist = true;
										}
										//如果pageIndex=0 查询的结果大于pageSize，那么进行循环
										while(isExist)
										{	
											try 
											{
												//pageIndex++;
												pageCount +=200;
												List<Data>	oItems = WMJBPProductService.queryListByEPoiId(eId, erpShopNO, pageCount, pageSize, errorMessage);

												if (oItems == null || oItems.size() == 0)
												{
													isExist = false;
													break;
												}
												//添加到商品列表
												for (Data oItem : oItems) 
												{
													products.add(oItem);			
												}										
												if (oItems.size() > pageSize)
												{
													isExist = true;
												}
												else 
												{
													isExist = false;
													break;			
												}					
											} 
											catch (Exception e) 
											{
												isExist = false;
												break;		
											}							
										}

										HelpTools.writelog_fileName("【同步商品资料到本地】【美团聚宝盆查询菜品详细接口】获取该门店商品资料完成！ 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+erpShopNO+" 获取的详细商品资料总个数："+products.size(), goodsLogFileName);	
										HelpTools.writelog_fileName("【同步商品资料到本地】【美团聚宝盆查询菜品详细接口】解析该门店商品资料并保存到本地开始！ 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+erpShopNO+" 获取的商品资料总个数："+products.size(), goodsLogFileName);
										for(DCP_OrderPlatformGoodsQueryRes.level1Elm oneData : res.getDatas())
										{
											String orderPluNO = oneData.getOrderPluNO();     //美团方菜品id
											String orderPluName = oneData.getOrderPluName(); //美团方商品名
											String orderCategoryNO = oneData.getOrderCategoryName();    // 美团分类ID 和名称 一样
											String orderCategoryName = orderCategoryNO;  //美团分类名称
											String pluNO=oneData.getPluNO(); //不一定有值。因为可能还没有做映射

											for (Data data : products) 
											{
												if	(orderCategoryName.equals(data.getCategoryName())&&orderPluName.equals(data.getDishName()))
												{
													int isSoldOut = data.getIsSoldOut();//0-未售完，1-售完
													String sequence = String.valueOf(data.getSequence());
													String isOnShelf = "Y";
													if(isSoldOut==1)
													{
														isOnShelf = "N";
													}
													String description = data.getDescription();
													oneData.setOrderPriority(sequence);
													oneData.setOrderDescription(description);
													oneData.setOrderUnit(data.getUnit());

													for (DCP_OrderPlatformGoodsQueryRes.level2Spec oneData_spec : oneData.getSpecs()) 
													{
														oneData_spec.setOrderOnShelf(isOnShelf);
														for (WMJBPQueryListByEPoiId.Skus data_sku : data.getSkus()) 
														{
															if(oneData_spec.getOrderSpecName().equals(data_sku.getSpec()))
															{
																oneData_spec.setOrderStock(data_sku.getStock());
																break;
															}

														}

													}

													break;								 
												}

											}

										}


									} 
									catch (Exception e) 
									{
										HelpTools.writelog_fileName("【同步商品资料到本地】获取该门店商品资料完成！【美团聚宝盆】解析返回的内容异常："+e.getMessage()+" 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+erpShopNO, goodsLogFileName);
										continue;

									}
									
								}
								else 
								{
									continue;			
								}
				
							} 
							catch (Exception e) 
							{
								HelpTools.writelog_fileName("【同步商品资料到本地】获取当前门店商品资料异常:"+e.getMessage()+" 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+erpShopNO, goodsLogFileName);
								continue;	
							}
							
							 //region 开始保存数据库
							this.SaveOnlineGoods(mapShop, res, erpShopNO, orderShopNO,orderShopName,getGoodsQData );
							//endregion
							
			      } 
						catch (Exception e) 
						{
							HelpTools.writelog_fileName("【OrderPlatformGoodsGet同步商品资料】异常："+e.getMessage(),goodsLogFileName);
							continue;			
			      }
						
					 
					}
					
		
					
				
					//region 开始处理异常商品
					getNoMappingGoods();
					//endregion					
					
				}
				else 
				{
					//
					sReturnInfo="无符合要求的数据！";
					HelpTools.writelog_fileName("【OrderPlatformGoodsGet同步商品资料】没有需要处理的订单消息！",goodsLogFileName);
					logger.info("\r\n******OrderPlatformGoodsGet没有需要获取的订单ID******\r\n");
				}
				
		  } 
			catch (Exception e) 
			{
				logger.error("\r\n******OrderPlatformGoodsGet同步商品资料报错信息" + e.getMessage() + "******\r\n");
				HelpTools.writelog_fileName("【OrderPlatformGoodsGet同步商品资料】异常："+e.getMessage(),goodsLogFileName);
				sReturnInfo="错误信息:" + e.getMessage();
		
		  }
			
		
	  } 
		catch (Exception e) 
		{
			logger.error("\r\n***************OrderPlatformGoodsGet同步商品资料异常"+e.getMessage()+"****************\r\n");
			sReturnInfo="错误信息:" + e.getMessage();
	  }
		finally 
		{
			bRun=false;//
		}
		
		logger.info("\r\n***************OrderPlatformGoodsGet同步商品资料END****************\r\n");
		
		return sReturnInfo;
	}
	
	
	protected String getQuerySql(String loadDocType)
	{
		String sql = "select * from (";
		sql += "select A.*,B.BELFIRM as BELFIRM2 from OC_mappingshop A inner join DCP_ORG B on A.EID=B.EID and A.SHOPID=B.Organizationno ";
		sql +=" where A.businessid='2' ";
		if (loadDocType != null && loadDocType.isEmpty() == false)
		{
			sql +=" and A.Load_Doctype='"+loadDocType+"'";
		}
		sql +=" order by A.Load_Doctype, A.SHOPID";
		sql +=")";
		return sql;
	}
	
	private void SaveOnlineGoods(Map<String, Object> req,DCP_OrderPlatformGoodsQueryRes res,String erpShopNO,String orderShopNO,String orderShopName,List<Map<String, Object>> getGoodsQData) throws Exception
	{
		String eId = req.get("EID").toString();
		String loadDocType = req.get("LOAD_DOCTYPE").toString();
		
		ArrayList<DataProcessBean> DPB = new ArrayList<DataProcessBean>();
	  	
		List<DCP_OrderPlatformGoodsQueryRes.level1Elm> goods = res.getDatas();
		
		if (goods != null && goods.size() > 0) 
		{
		  //三张表先删后插
			String execsql1 = "delete from OC_MAPPINGGOODS where EID='"+eId+"' and LOAD_DOCTYPE='"+loadDocType+"' and SHOPID='"+erpShopNO+"'";
			String execsql2 = "delete from OC_MAPPINGGOODS_SPEC where EID='"+eId+"' and LOAD_DOCTYPE='"+loadDocType+"' and SHOPID='"+erpShopNO+"'";
			String execsql3 = "delete from OC_MAPPINGGOODS_ATTR where EID='"+eId+"' and LOAD_DOCTYPE='"+loadDocType+"' and SHOPID='"+erpShopNO+"'";
			ExecBean exc1 = new ExecBean(execsql1);
			ExecBean exc2 = new ExecBean(execsql2);
			ExecBean exc3 = new ExecBean(execsql3);
			DPB.add(new DataProcessBean(exc1));
			DPB.add(new DataProcessBean(exc2));
			DPB.add(new DataProcessBean(exc3));
	
		}
		
		HelpTools.writelog_fileName("【同步商品资料到本地】开始组装SQL语句！ 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+erpShopNO+" 获取的商品资料总个数："+goods.size(), goodsLogFileName);
		for (level1Elm oneData1 : goods) 
		{
			String orderPluNO = oneData1.getOrderPluNO();
			String orderPlunName = oneData1.getOrderPluName() == null ? "" : oneData1.getOrderPluName();
			try 
			{	  				  
				String orderCategoryNO = oneData1.getOrderCategoryNO()== null ? "" : oneData1.getOrderCategoryNO();
				String orderCategoryName = oneData1.getOrderCategoryName()== null ? "" : oneData1.getOrderCategoryName();
				String orderDescription = oneData1.getOrderDescription()== null ? "" : oneData1.getOrderDescription();
				String orderImageUrl = oneData1.getOrderImageUrl()== null ? "" : oneData1.getOrderImageUrl();
				String orderUnit = oneData1.getOrderUnit()== null ? "" : oneData1.getOrderUnit();
				String pluNO = oneData1.getPluNO()== null ? " " : oneData1.getPluNO();
				String pluName = oneData1.getPluName()== null ? "" : oneData1.getPluName();	
				String categoryNO =" ";

				if(pluNO.length()==0)
				{
					pluNO = " ";//主键
				}

				List<DCP_OrderPlatformGoodsQueryRes.level2Spec> specs = oneData1.getSpecs();
				for (level2Spec oneDataSpec : specs) 
				{
					String orderSpecID = oneDataSpec.getOrderSpecID();
					String orderSpecName = oneDataSpec.getOrderSpecName() == null ? "" : oneDataSpec.getOrderSpecName();
					String orderPrice = 	oneDataSpec.getOrderPrice() == null?"0":	oneDataSpec.getOrderPrice();
					String orderStock = oneDataSpec.getOrderStock() == null?"0": oneDataSpec.getOrderStock() ;
					String orderPackingFee = oneDataSpec.getOrderPackingFee()== null?"0": oneDataSpec.getOrderPackingFee();
					String orderOnShelf = oneDataSpec.getOrderOnShelf()== null?"N": oneDataSpec.getOrderOnShelf();
					String pluBarcode = oneDataSpec.getPluBarcode() == null?" ":oneDataSpec.getPluBarcode();//主键不能为空
					String pluSpecName = oneDataSpec.getPluSpecName() == null?"":oneDataSpec.getPluSpecName();

					if(orderStock.length()==0)
					{
						orderStock = "9999";
					}

					for (Map<String,Object> map : getGoodsQData )
					{
						if(map.get("SPECNO").equals(pluBarcode))
						{
							String q_pluNO=map.get("PLUNO").toString();						
							String q_pluName=map.get("PLUNAME").toString();
							//String q_specNO=map.get("SPECNO").toString();
							String q_specName=map.get("SPECNAME").toString();
							//String q_fileName=map.get("FILENAME").toString();
							//String q_netweight=map.get("NETWEIGHT").toString();
							String q_categoryNO=map.get("CATEGORYNO").toString();

							if  (!Check.Null(q_specName))   pluSpecName=q_specName ;

							if(loadDocType.equals("2") )
							{
								//if  (!Check.Null(q_pluNO))   pluNO=q_pluNO ;
							}
							else
							{
								if  (!Check.Null(q_pluNO))   pluNO=q_pluNO ;
							}

							if  (!Check.Null(q_pluName))   pluName=q_pluName ;
							if  (!Check.Null(q_categoryNO))   categoryNO=q_categoryNO ;
							break;
						}
					}

					if(pluBarcode.length()==0)
					{
						pluBarcode = " ";
					}

					if(categoryNO.length()==0)
					{
						categoryNO = " ";
					}


					String[] columns2 = { "EID", "LOAD_DOCTYPE", "SHOPID", "ORGANIZATIONNO", "PLUNO", "SPECNO","SPECNAME",
							"ORDER_SHOP","ORDER_SHOPNAME", "ORDER_PLUNO", "ORDER_SPECNO", "ORDER_SPECNAME","PRICE", "STOCKQTY","PACKAGEFEE","ISONSHELF", "STATUS","NETWEIGHT" };
					DataValue[] insValue2 = null;
					insValue2 = new DataValue[]{
							new DataValue(eId, Types.VARCHAR),
							new DataValue(loadDocType, Types.VARCHAR),//1.饿了么 2.美团外卖 3.微商城
							new DataValue(erpShopNO, Types.VARCHAR),//组织编号=门店编号
							new DataValue(erpShopNO, Types.VARCHAR),//ERP门店				
							new DataValue(pluNO, Types.VARCHAR),//商品编码
							new DataValue(pluBarcode, Types.VARCHAR),//ERP条码
							new DataValue(pluSpecName, Types.VARCHAR),//规格名称
							new DataValue(orderShopNO, Types.VARCHAR),//外卖平台门店ID
							new DataValue(orderShopName, Types.VARCHAR),//外卖平台门店名称
							new DataValue(orderPluNO, Types.VARCHAR),//外卖平台商品ID
							new DataValue(orderSpecID, Types.VARCHAR),//外卖平台商品名称 
							new DataValue(orderSpecName, Types.VARCHAR),//
							new DataValue(orderPrice, Types.VARCHAR),//	
							new DataValue(orderStock, Types.VARCHAR),//
							new DataValue(orderPackingFee, Types.VARCHAR),//
							new DataValue(orderOnShelf, Types.VARCHAR),//					
							new DataValue("100", Types.VARCHAR),
							new DataValue(oneDataSpec.getNetWeight(), Types.DOUBLE)
					};

					InsBean ib2 = new InsBean("OC_MAPPINGGOODS_SPEC", columns2);
					ib2.addValues(insValue2);
					DPB.add(new DataProcessBean(ib2));	
				}


				List<DCP_OrderPlatformGoodsQueryRes.level2Attribute> attributes = oneData1.getAttributes();
				if (attributes != null && attributes.size() > 0)
				{
					HelpTools.writelog_fileName("【同步商品资料到本地(有属性值)】开始组装SQL语句！ 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+erpShopNO+" 获取的商品资料总个数："+goods.size(), goodsLogFileName);
					for (level2Attribute oneDataAttribute : attributes) 
					{
						String attriName = oneDataAttribute.getName();
						List<String> attriDetails = oneDataAttribute.getDetails();
						String attriValue = "";
						for (String string_attri : attriDetails) 
						{
							attriValue += string_attri +",";			
				    }
						if (attriValue.length()>0)
						{
							attriValue = attriValue.substring(0,attriValue.length()-1);
						}
							
						String[] columns2 = { "EID", "LOAD_DOCTYPE", "SHOPID", "ORGANIZATIONNO", "PLUNO", "ATTRNAME","ATTRVALUE",
								"ORDER_SHOP","ORDER_SHOPNAME", "ORDER_PLUNO", "ORDER_ATTRNAME", "ORDER_ATTRVALUE", "STATUS"};
						DataValue[] insValue2 = null;
						insValue2 = new DataValue[]{
								new DataValue(eId, Types.VARCHAR),
								new DataValue(loadDocType, Types.VARCHAR),//1.饿了么 2.美团外卖 3.微商城
								new DataValue(erpShopNO, Types.VARCHAR),//组织编号=门店编号
								new DataValue(erpShopNO, Types.VARCHAR),//ERP门店				
								new DataValue(pluNO, Types.VARCHAR),//商品编码
								new DataValue("", Types.VARCHAR),//属性名称
								new DataValue("", Types.VARCHAR),//属性值
								new DataValue(orderShopNO, Types.VARCHAR),//外卖平台门店ID
								new DataValue(orderShopName, Types.VARCHAR),//外卖平台门店名称
								new DataValue(orderPluNO, Types.VARCHAR),//外卖平台商品ID
								new DataValue(attriName, Types.VARCHAR),//外卖平台属性名称
								new DataValue(attriValue, Types.VARCHAR),//外卖平台属性值
								new DataValue("100", Types.VARCHAR)						
						};

						InsBean ib2 = new InsBean("OC_MAPPINGGOODS_ATTR", columns2);
						ib2.addValues(insValue2);
						DPB.add(new DataProcessBean(ib2));	
						//this.doExecuteDataToDB();
					}
				
				}

				String[] columns1 = { "EID", "LOAD_DOCTYPE", "SHOPID", "ORGANIZATIONNO", "PLUNO", "PLUNAME","CATEGORYNO",
						"ORDER_SHOP", "ORDER_SHOPNAME","ORDER_PLUNO", "ORDER_PLUNAME", "ORDER_CATEGORYNO", "ORDER_CATEGORYNAME","DESCRIPTION","FILENAME","UNIT","PRIORITY", "STATUS"
						, "MATERIALID1", "MATERIALID2", "MATERIALID3", "MATERIALID4", "MATERIALID5", "MATERIALID6", "MATERIALID7", "MATERIALID8", "MATERIALID9", "MATERIALID10"
						, "MATERIAL1", "MATERIAL2", "MATERIAL3", "MATERIAL4", "MATERIAL5", "MATERIAL6", "MATERIAL7", "MATERIAL8", "MATERIAL9", "MATERIAL10",
						"ISALLTIMESELL","BEGINDATE","ENDDATE","SELLWEEK","SELLTIME"};
				DataValue[] insValue1 = null;
				insValue1 = new DataValue[]{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(loadDocType, Types.VARCHAR),//1.饿了么 2.美团外卖 3.微商城
						new DataValue(erpShopNO, Types.VARCHAR),//组织编号=门店编号
						new DataValue(erpShopNO, Types.VARCHAR),//ERP门店				
						new DataValue(pluNO, Types.VARCHAR),//ERP商品编码
						new DataValue(pluName, Types.VARCHAR),//ERP商品名称
						new DataValue(categoryNO, Types.VARCHAR),//ERP商品分类编码
						new DataValue(orderShopNO, Types.VARCHAR),//外卖平台门店ID
						new DataValue(orderShopName, Types.VARCHAR),//外卖平台门店名称
						new DataValue(orderPluNO, Types.VARCHAR),//外卖平台商品ID
						new DataValue(orderPlunName, Types.VARCHAR),//外卖平台商品名称 
						new DataValue(orderCategoryNO, Types.VARCHAR),//
						new DataValue(orderCategoryName, Types.VARCHAR),//
						new DataValue(orderDescription, Types.VARCHAR),//	
						new DataValue(orderImageUrl, Types.VARCHAR),//					
						new DataValue(orderUnit, Types.VARCHAR),//
						new DataValue("0", Types.VARCHAR),//	
						new DataValue("100", Types.VARCHAR),//	
						new DataValue(oneData1.getMaterialID1(), Types.VARCHAR),
						new DataValue(oneData1.getMaterialID2(), Types.VARCHAR),
						new DataValue(oneData1.getMaterialID3(), Types.VARCHAR),
						new DataValue(oneData1.getMaterialID4(), Types.VARCHAR),
						new DataValue(oneData1.getMaterialID5(), Types.VARCHAR),
						new DataValue(oneData1.getMaterialID6(), Types.VARCHAR),
						new DataValue(oneData1.getMaterialID7(), Types.VARCHAR),
						new DataValue(oneData1.getMaterialID8(), Types.VARCHAR),
						new DataValue(oneData1.getMaterialID9(), Types.VARCHAR),
						new DataValue(oneData1.getMaterialID10(), Types.VARCHAR),
						new DataValue(oneData1.getMaterial1(), Types.VARCHAR),
						new DataValue(oneData1.getMaterial2(), Types.VARCHAR),
						new DataValue(oneData1.getMaterial3(), Types.VARCHAR),
						new DataValue(oneData1.getMaterial4(), Types.VARCHAR),
						new DataValue(oneData1.getMaterial5(), Types.VARCHAR),
						new DataValue(oneData1.getMaterial6(), Types.VARCHAR),
						new DataValue(oneData1.getMaterial7(), Types.VARCHAR),
						new DataValue(oneData1.getMaterial8(), Types.VARCHAR),
						new DataValue(oneData1.getMaterial9(), Types.VARCHAR),
						new DataValue(oneData1.getMaterial10(), Types.VARCHAR),						
						new DataValue(oneData1.getIsAllTimeSell(), Types.VARCHAR),
						new DataValue(oneData1.getBeginDate(), Types.VARCHAR),
						new DataValue(oneData1.getEndDate(), Types.VARCHAR),
						new DataValue(oneData1.getSellWeek(), Types.VARCHAR),
						new DataValue(oneData1.getSellTime(), Types.VARCHAR)						
				};

				InsBean ib1 = new InsBean("OC_MAPPINGGOODS", columns1);
				ib1.addValues(insValue1);
				DPB.add(new DataProcessBean(ib1));

			} 

			catch (Exception e) 
			{
				HelpTools.writelog_fileName("【同步商品资料到本地】开始组装SQL语句！有异常:"+e.getMessage()+" 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+erpShopNO+" 异常的商品平台ID/名称："+orderPluNO+"/"+orderPlunName, goodsLogFileName);
				continue;		
			}


		}

		try 
		{
			HelpTools.writelog_fileName("【同步商品资料到本地】开始执行SQL语句！ 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+erpShopNO+" 获取的商品资料总个数："+goods.size(), goodsLogFileName);  
			this.doExecuteDataToDB(DPB);
			HelpTools.writelog_fileName("【同步商品资料到本地】开始执行SQL语句！成功！ 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+erpShopNO+" 获取的商品资料总个数："+goods.size(), goodsLogFileName);  

		} 
		catch (SQLSyntaxErrorException e)
		{
			HelpTools.writelog_fileName("【同步商品资料到本地】开始执行SQL语句！异常："+e.getMessage()+" 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+erpShopNO+" 获取的商品资料总个数："+goods.size(), goodsLogFileName); 		
		}
		catch (Exception e) 
		{
			HelpTools.writelog_fileName("【同步商品资料到本地】开始执行SQL语句！异常："+e.getMessage()+" 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+erpShopNO+" 获取的商品资料总个数："+goods.size(), goodsLogFileName); 		
		}
		

	}


	protected void doExecuteDataToDB(List<DataProcessBean> pData) throws Exception {
		if (pData == null || pData.size() == 0) {
			return;
		}
		StaticInfo.dao.useTransactionProcessData(pData);
		
	}
	
	/**
	 * 查询没有或者异常的映射商品，并且下架处理
	 * @throws Exception
	 */
	private void getNoMappingGoods() throws Exception
	{
		DCP_OrderPlatformGoodsQueryRes res = new DCP_OrderPlatformGoodsQueryRes();
		res.setDatas(new ArrayList<DCP_OrderPlatformGoodsQueryRes.level1Elm>());
		try 
		{
		
			String sql = "select * from (";
			sql+="select A.*,C.PLUNAME,C.ORDER_PLUNAME,C.ORDER_CATEGORYNO,C.ORDER_CATEGORYNAME,C.CATEGORYNO,C.DESCRIPTION,UNIT,PRIORITY from OC_mappinggoods_spec A";
			sql+=" left join DCP_GOODS_shop B on A.EID=B.EID and A.Specno=B.Pluno and A.SHOPID=B.Organizationno";
			sql+=" left join OC_mappinggoods C on A.EID=C.EID and A.order_shop=C.order_shop and A.Order_Pluno=C.Order_Pluno";
			sql+=" where B.pluno is null order by A.load_doctype,A.SHOPID,A.Order_Pluno";
			sql+=" )";
			HelpTools.writelog_fileName("【同步查询没有或异常的映射商品】开始组装SQL语句！sql="+ sql, goodsLogFileName);
			List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);
			if(getQDataDetail==null||getQDataDetail.isEmpty())
			{
				return;
			}
			
		  //单头主键字段
			Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
			condition.put("EID", true);
			condition.put("LOAD_DOCTYPE", true);	
			condition.put("SHOPID", true);	
			condition.put("ORDER_PLUNO", true);	
			//调用过滤函数
			List<Map<String, Object>> getQHeader=MapDistinct.getMap(getQDataDetail, condition);
			for (Map<String, Object> map : getQHeader) 
			{
				try 
				{
					DCP_OrderPlatformGoodsQueryRes.level1Elm oneLv1 = res.new level1Elm();

					oneLv1.setSpecs(new ArrayList<DCP_OrderPlatformGoodsQueryRes.level2Spec>());
					oneLv1.setAttributes(new ArrayList<DCP_OrderPlatformGoodsQueryRes.level2Attribute>());

					String eId = map.get("EID").toString();
					String loadDocType = map.get("LOAD_DOCTYPE").toString();
					String erpShopNO = map.get("SHOPID").toString();
					String orderShopNO = map.get("ORDER_SHOP").toString();
					String orderPluNO = map.get("ORDER_PLUNO").toString();
					String orderPluName = map.get("ORDER_PLUNAME").toString();//商品名
					String orderCategoryNO = map.get("ORDER_CATEGORYNO").toString();//饿了么分类ID
					String orderCategoryName = map.get("ORDER_CATEGORYNAME").toString();//饿了么分类ID
					String orderDescription = map.get("DESCRIPTION").toString();
					String orderUnit = map.get("UNIT").toString();
					String orderImageUrl = "";//map.get("FILENAME").toString();
					String orderPriority= map.get("PRIORITY").toString();
					String pluNO =  map.get("PLUNO").toString();
					String pluName =  map.get("PLUNAME").toString();
					String spuOnShelf = map.get("ISONSHELF").toString();
					String categoryNO = "";
					String categoryName = "";

					oneLv1.seteId(eId);
					oneLv1.setLoadDocType(loadDocType);
					oneLv1.setShopId(erpShopNO);
					oneLv1.setOrderShopNO(orderShopNO);
					
					oneLv1.setOrderPluNO(orderPluNO);
					oneLv1.setOrderPluName(orderPluName);
					oneLv1.setOrderCategoryNO(orderCategoryNO);
					oneLv1.setOrderCategoryName(orderCategoryName);//没有返回分类名称
					oneLv1.setOrderDescription(orderDescription);
					oneLv1.setOrderImageUrl(orderImageUrl);
					oneLv1.setOrderUnit(orderUnit);
					oneLv1.setOrderPriority(orderPriority);
					oneLv1.setPluNO(pluNO);
					oneLv1.setPluName(pluName);
					oneLv1.setCategoryNO(categoryNO);
					oneLv1.setCategoryName(categoryName);
					oneLv1.setSpuOnShelf(spuOnShelf);

					for (Map<String, Object> oneLv2 : getQDataDetail) 
					{
						String companyNO2 = map.get("EID").toString();
						String loadDocType2 = map.get("LOAD_DOCTYPE").toString();
						String erpShopNO2 = map.get("SHOPID").toString();
						String orderPluNO2 = oneLv2.get("ORDER_PLUNO").toString();
						if(orderPluNO2.equals(orderPluNO)==false||companyNO2.equals(eId)==false||loadDocType2.equals(loadDocType)==false||erpShopNO2.equals(erpShopNO)==false)
						{
							continue;
						}
						DCP_OrderPlatformGoodsQueryRes.level2Spec orderSpec = res.new level2Spec();
						String orderSpecID = oneLv2.get("ORDER_SPECNO").toString();//规格Id
						String orderSpecName = oneLv2.get("ORDER_SPECNAME").toString();//名称
						String orderPrice =  oneLv2.get("PRICE").toString();
						String orderStock = oneLv2.get("STOCKQTY").toString();
						String orderPackingFee = oneLv2.get("PACKAGEFEE").toString();			
						String orderOnShelf = oneLv2.get("ISONSHELF").toString();

						String pluBarcode = oneLv2.get("SPECNO").toString();//商品扩展码 (ERP的商品条码)真正映射的商品条码
						String pluSpecName = oneLv2.get("SPECNAME").toString();

					/*	if (orderStock.equals("0")||Integer.valueOf(orderStock)==0 ) 
						{
							orderOnShelf="N";
						}*/

						orderSpec.setOrderSpecID(orderSpecID);
						orderSpec.setOrderSpecName(orderSpecName);
						orderSpec.setOrderPrice(orderPrice);
						orderSpec.setOrderStock(orderStock);
						orderSpec.setOrderPackingFee(orderPackingFee);
						orderSpec.setOrderOnShelf(orderOnShelf);
						orderSpec.setPluBarcode(pluBarcode);
						orderSpec.setPluSpecName(pluSpecName);

						oneLv1.getSpecs().add(orderSpec);	
						orderSpec=null;

					}
			
					res.getDatas().add(oneLv1);
			
		
				} 
				catch (Exception e) 
				{
					continue;					      
				}
				
				

			}


			
			
		} 
		catch (Exception e) 
		{
		// TODO: handle exception
	
		}
		
		//开始处理异常映射商品  下架
		processNoMappingGoods(res,false);
		
	}
	
	/**
	 * 没有映射/映射错的商品 上下架
	 * @param res
	 * @param IsOnShelf true=上架，false=下架
	 * @throws Exception
	 */
	private void processNoMappingGoods(DCP_OrderPlatformGoodsQueryRes res,boolean IsOnShelf) throws Exception
	{
		String IsOnShelfDes="下架";
		if(IsOnShelf)
		{
			IsOnShelfDes="上架";
		}
		HelpTools.writelog_fileName("【没有或异常的映射商品上下架处理】【"+IsOnShelfDes+"】开始！",goodsLogFileName);
		if(res==null||res.getDatas()==null||res.getDatas().size()==0)
		{
			HelpTools.writelog_fileName("【没有或异常的映射商品上下架处理】没有需要处理的映射商品！", goodsLogFileName);
			return;
		}
			
		List<DCP_OrderPlatformGoodsQueryRes.level1Elm>	goodsdatas =	res.getDatas();
		for (level1Elm req : goodsdatas) 
		{
			String eId=req.geteId();
			String loadDocType = req.getLoadDocType();
			String shopId = req.getShopId();	
			String orderShopNO = req.getOrderShopNO();
			String spuOnShelf = req.getSpuOnShelf();
			
			String orderPluNO = req.getOrderPluNO();
			String pluNO = req.getPluNO();
			
			String orderCategoryName = req.getOrderCategoryName();
		  String orderPluName = req.getOrderPluName();
		  String unit = req.getOrderUnit();
			
			try 
			{
				if(IsOnShelf)//如果是上架
				{
					if(spuOnShelf.equals("Y"))//已经上架的，不用管了
					{
						HelpTools.writelog_fileName("【批量上下架商品】【"+IsOnShelfDes+"】【已经上架了，不用处理】, 平台类型LoadDocType:"+loadDocType+" 当前门店shopId:"+shopId+" 平台门店ID:"+orderShopNO+" 平台商品Order_Pluno:"+orderPluNO, goodsLogFileName);
						continue;
					}
				}
				else//如果下架
				{
					if(spuOnShelf.equals("N"))//已经下架的，不用管了
					{
						HelpTools.writelog_fileName("【批量上下架商品】【"+IsOnShelfDes+"】【已经下架了，不用处理】, 平台类型LoadDocType:"+loadDocType+" 当前门店shopId:"+shopId+" 平台门店ID:"+orderShopNO+" 平台商品Order_Pluno:"+orderPluNO, goodsLogFileName);
						continue;
					}
					
				}
				
				if(loadDocType.equals("1"))
				{
					Boolean isGoNewFunction = false;//是否走新的接口
					String elmAPPKey = "";
					String elmAPPSecret = "";
					String elmAPPName = "";			
					boolean elmIsSandbox = false;
					StringBuilder errorMessage = new StringBuilder();
					List<Long> goodslong = new ArrayList<Long>();
					try
					{
						Map<String, Object> map = PosPub.getWaimaiAppConfigByShopNO_New(StaticInfo.dao, eId, shopId, "1","");
						if (map != null)
						{
							elmAPPKey = map.get("APPKEY").toString();
							elmAPPSecret = map.get("APPSECRET").toString();
							elmAPPName = map.get("APPNAME").toString();
							String	elmIsTest = map.get("ISTEST").toString();					
							if (elmIsTest != null && elmIsTest.equals("Y"))
							{
								elmIsSandbox = true;
							}
							isGoNewFunction = true;
						}

						
					  goodslong.add(Long.valueOf(orderPluNO));
						
						HelpTools.writelog_fileName("【批量上下架商品】【"+IsOnShelfDes+"】开始, 平台类型LoadDocType:"+loadDocType+" 当前门店shopId:"+shopId+" 平台门店ID:"+orderShopNO+" 平台商品Order_Pluno:"+orderPluNO, goodsLogFileName);

						OBatchModifiedResult nRet= new OBatchModifiedResult();
						if(isGoNewFunction)  //新的接口
						{
							if (IsOnShelf) //上架
							{
								nRet = WMELMProductService.batchListItems(elmIsSandbox, elmAPPKey, elmAPPSecret, elmAPPName,goodslong,errorMessage  );
							}
							else  //下架
							{
								nRet = WMELMProductService.batchDelistItems(elmIsSandbox, elmAPPKey, elmAPPSecret, elmAPPName,goodslong,errorMessage  );	
							}
						}
						else   //饿了么旧的接口
						{
							if (IsOnShelf) //上架
							{
								nRet = WMELMProductService.batchListItems(goodslong, errorMessage);
							}
							else //下架
							{
								nRet = WMELMProductService.batchDelistItems(goodslong, errorMessage);
							}
						}
						if (nRet.getFailures()==null || nRet.getFailures().isEmpty()) 
						{
							HelpTools.writelog_fileName("【批量上下架商品】【"+IsOnShelfDes+"】结束,成功！   平台类型LoadDocType:"+loadDocType+" 当前门店shopId:"+shopId+" 平台门店ID:"+orderShopNO+" 平台商品Order_Pluno:"+orderPluNO, goodsLogFileName);
						}	
						else
						{
							HelpTools.writelog_fileName("【批量上下架商品】【"+IsOnShelfDes+"】结束,失败 ！ "+errorMessage.toString()+ "平台类型LoadDocType:" + loadDocType  , goodsLogFileName);
						
							continue;
						}

						ArrayList<DataProcessBean> DPB = new ArrayList<DataProcessBean>();
						//回写商品映射资料档状态
						List<Long> modifications = nRet.getModifications();
						IdType iDType = nRet.getType();   // ITEM_ID: 商品ID   SPEC_ID:规格ID   CATEGORY_ID:分类ID
						for (Long par : modifications) 
						{										
							UptBean up1=new UptBean("OC_MAPPINGGOODS_SPEC");
							up1.addUpdateValue("ISONSHELF", new DataValue(spuOnShelf, Types.VARCHAR));					
							up1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
							up1.addCondition("LOAD_DOCTYPE", new DataValue(loadDocType, Types.VARCHAR));
							up1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));					
							up1.addCondition("ORDER_PLUNO", new DataValue(par, Types.VARCHAR));
							DPB.add(new DataProcessBean(up1));
							
						}	
						
						if(DPB.size()>0)
						{
							HelpTools.writelog_fileName("【批量上下架商品】【"+IsOnShelfDes+"】回写数据库,开始！   平台类型LoadDocType:"+loadDocType+" 当前门店shopId:"+shopId+" 平台门店ID:"+orderShopNO+" 平台商品Order_Pluno:"+orderPluNO, goodsLogFileName);
							this.doExecuteDataToDB(DPB);
							HelpTools.writelog_fileName("【批量上下架商品】【"+IsOnShelfDes+"】回写数据库,成功！   平台类型LoadDocType:"+loadDocType+" 当前门店shopId:"+shopId+" 平台门店ID:"+orderShopNO+" 平台商品Order_Pluno:"+orderPluNO, goodsLogFileName);
						}
						
						
					}
					catch (Exception e) 
					{
						HelpTools.writelog_fileName("【批量上下架商品】【"+IsOnShelfDes+"】异常:"+e.getMessage()+ "平台类型LoadDocType:" + loadDocType+ " 当前门店shopId:"+shopId+" 平台门店ID:"+orderShopNO+" 平台商品Order_Pluno:"+orderPluNO , goodsLogFileName);
						continue;
					}	
					
					
				}
				else if (loadDocType.equals("2")) 
				{
					try
					{
						StringBuilder errorMessage = new StringBuilder();
						List<WMJBPGoodsUpdate>  disheslist = new ArrayList<WMJBPGoodsUpdate>();
						WMJBPGoodsUpdate dishes = new WMJBPGoodsUpdate();
						dishes.setePoiId(eId+"_"+shopId);
						dishes.setEDishCode(pluNO);
						dishes.setCategoryName(orderCategoryName);  
						dishes.setDishName(orderPluName);				
						dishes.setUnit(unit);
						if (IsOnShelf) //上架
						{
							dishes.setIsSoldOut(0);
						}
						else  //下架
						{
							dishes.setIsSoldOut(1);
						}	
						disheslist.add(dishes);
						
						boolean nRet = false;
						HelpTools.writelog_fileName("【批量上下架商品】【"+IsOnShelfDes+"】开始, 平台类型LoadDocType:"+loadDocType+" 当前门店shopId:"+shopId+" 平台门店ID:"+orderShopNO+" 平台商品Order_Pluno:"+orderPluNO, goodsLogFileName);
						nRet = WMJBPProductService.batchUpload(eId,shopId,disheslist,errorMessage  );

						if (nRet) 
						{
							
							HelpTools.writelog_fileName("【批量上下架商品】【"+IsOnShelfDes+"】结束,成功！   平台类型LoadDocType:"+loadDocType+" 当前门店shopId:"+shopId+" 平台门店ID:"+orderShopNO+" 平台商品Order_Pluno:"+orderPluNO, goodsLogFileName);
						}	
						else
						{
							HelpTools.writelog_fileName("【批量上下架商品】【"+IsOnShelfDes+"】结束,失败 ！ "+errorMessage.toString()+ "平台类型LoadDocType:" + loadDocType  , goodsLogFileName);
							continue;
						}
						
						ArrayList<DataProcessBean> DPB = new ArrayList<DataProcessBean>();
																				
						UptBean up1=new UptBean("OC_MAPPINGGOODS_SPEC");
						up1.addUpdateValue("ISONSHELF", new DataValue(spuOnShelf, Types.VARCHAR));					
						up1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
						up1.addCondition("LOAD_DOCTYPE", new DataValue(loadDocType, Types.VARCHAR));
						up1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));					
						up1.addCondition("ORDER_PLUNO", new DataValue(orderPluNO, Types.VARCHAR));
						DPB.add(new DataProcessBean(up1));
																
						if(DPB.size()>0)
						{
							HelpTools.writelog_fileName("【批量上下架商品】【"+IsOnShelfDes+"】回写数据库,开始！   平台类型LoadDocType:"+loadDocType+" 当前门店shopId:"+shopId+" 平台门店ID:"+orderShopNO+" 平台商品Order_Pluno:"+orderPluNO, goodsLogFileName);
							this.doExecuteDataToDB(DPB);
							HelpTools.writelog_fileName("【批量上下架商品】【"+IsOnShelfDes+"】回写数据库,成功！   平台类型LoadDocType:"+loadDocType+" 当前门店shopId:"+shopId+" 平台门店ID:"+orderShopNO+" 平台商品Order_Pluno:"+orderPluNO, goodsLogFileName);
						}
										
					} 
					catch (Exception e) 
					{
						HelpTools.writelog_fileName("【批量上下架商品】【"+IsOnShelfDes+"】异常:"+e.getMessage()+ "平台类型LoadDocType:" + loadDocType+ " 当前门店shopId:"+shopId+" 平台门店ID:"+orderShopNO+" 平台商品Order_Pluno:"+orderPluNO , goodsLogFileName);
						continue;
			
			
					}
					
					
		
				}
				else 
				{					
					continue;			
		   
				}
		
		
			} 
			catch (Exception e) 
			{
				HelpTools.writelog_fileName("【批量上下架商品】【"+IsOnShelfDes+"】异常:"+e.getMessage()+" 平台类型LoadDocType:"+loadDocType+" 当前门店shopId:"+shopId+" 平台门店ID:"+orderShopNO+" 平台商品Order_Pluno:"+orderPluNO, goodsLogFileName);
        continue;	
			}
	
		}
	
	
		
		
	}

}
