package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderPlatformGoodsBatchMappingReq;
import com.dsc.spos.json.cust.res.DCP_OrderGoodsQueryRes;
import com.dsc.spos.json.cust.res.DCP_OrderPlatformGoodsBatchMappingRes;
import com.dsc.spos.json.cust.res.DCP_OrderPlatformGoodsQueryRes;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.WMELMProductService;
import com.dsc.spos.waimai.WMJBPProductService;
import com.dsc.spos.waimai.jddj.HelpJDDJHttpUtil;
import com.google.gson.reflect.TypeToken;

import eleme.openapi.sdk.api.entity.product.OItemAttribute;
import eleme.openapi.sdk.api.entity.product.OItemSellingTime;
import eleme.openapi.sdk.api.entity.product.OItemTime;
import eleme.openapi.sdk.api.entity.product.OMaterial;
import eleme.openapi.sdk.api.entity.product.OSpec;
import eleme.openapi.sdk.api.enumeration.product.OItemUpdateProperty;
import eleme.openapi.sdk.api.enumeration.product.OItemWeekEnum;

public class DCP_OrderPlatformGoodsBatchMapping extends SPosAdvanceService<DCP_OrderPlatformGoodsBatchMappingReq,DCP_OrderPlatformGoodsBatchMappingRes> {

	static String goodsLogFileName = "MappingGoodsSaveLocal";
	
	@Override
	protected void processDUID(DCP_OrderPlatformGoodsBatchMappingReq req, DCP_OrderPlatformGoodsBatchMappingRes res)
			throws Exception {
		// TODO Auto-generated method stub
		String loadDocType = req.getLoadDocType();
		String eId = req.geteId();
		String isEmptyPluNO = req.getIsEmptyPluNO();
			
		//获取同步的菜品池 商品资料
		DCP_OrderGoodsQueryRes tempBaseGoods = this.GetTempGoods(req);
		if(tempBaseGoods==null||tempBaseGoods.getDatas()==null||tempBaseGoods.getDatas().size()==0)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "同步的模板（ 菜品池）没有资料！");
		}
		HelpTools.writelog_fileName("【批量同步商品映射】开始, 平台类型LoadDocType:"+loadDocType+" 同步的门店总数："+req.getErpShopNO().length, goodsLogFileName);
		//region 循环同步的门店
		int i=0;
		for (String erpShopNO : req.getErpShopNO()) 
		{
			i++;
			HelpTools.writelog_fileName("【批量同步商品映射】循环第【"+i+"】个门店开始, 平台类型LoadDocType:"+loadDocType+" 当前门店shopId："+erpShopNO, goodsLogFileName);
			DCP_OrderPlatformGoodsQueryRes needMappingGoods = this.GetNeedMappingGoods(loadDocType, eId, erpShopNO,isEmptyPluNO);
			if(needMappingGoods==null||needMappingGoods.getDatas()==null||needMappingGoods.getDatas().size()==0)
			{
				HelpTools.writelog_fileName("【批量同步商品映射】循环第【"+i+"】个门店结束,该门店没有同步商品资料到本地！ 平台类型LoadDocType:"+loadDocType+" 当前门店shopId："+erpShopNO, goodsLogFileName);
				continue;
			}

			if(loadDocType.equals("1"))//饿了么
			{
				try
				{
					//查询下当前门店的对应的饿了么APPKEY
					Map<String, Object> map = PosPub.getWaimaiAppConfigByShopNO_New(StaticInfo.dao, eId, erpShopNO, loadDocType,"");
					Boolean isGoNewFunction = false;//是否走新的接口
					String elmAPPKey = "";
					String elmAPPSecret = "";
					String elmAPPName = "";			
					boolean elmIsSandbox = false;
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
					HelpTools.writelog_fileName("【批量同步商品映射】开始同步当前门店商品, 平台类型LoadDocType:"+loadDocType+" 当前门店shopId："+erpShopNO+" 同步的商品总数："+needMappingGoods.getDatas().size(), goodsLogFileName);
					int j = 0;
					for(DCP_OrderPlatformGoodsQueryRes.level1Elm oneData : needMappingGoods.getDatas())
					{
						j++;
						StringBuilder errorMessage = new StringBuilder();
						String orderPLUNO = oneData.getOrderPluNO().trim();
						String orderPluName = oneData.getOrderPluName().trim();
						String orderCategoryNO = oneData.getOrderCategoryNO();
						String pluNO = oneData.getPluNO();
						String pluName = oneData.getPluName();
						String description = oneData.getOrderDescription();
						boolean IsMatch = false;
						Map<OItemUpdateProperty,Object> properties = new HashMap<OItemUpdateProperty,Object>();
						properties.put(OItemUpdateProperty.name,orderPluName);
						properties.put(OItemUpdateProperty.description,description);
						
						List<OMaterial> oMaterials = new ArrayList<OMaterial>();
						
						if ( !Check.Null(oneData.getMaterial1()) && !Check.Null(oneData.getMaterialID1()))
						{
							OMaterial oMaterial = new OMaterial();
							oMaterial.setId(Long.valueOf(oneData.getMaterialID1()));
							oMaterial.setName(oneData.getMaterial1());
							oMaterials.add(oMaterial);
						}
						if ( !Check.Null(oneData.getMaterial2()) && !Check.Null(oneData.getMaterialID2()))
						{
							OMaterial oMaterial = new OMaterial();
							oMaterial.setId(Long.valueOf(oneData.getMaterialID2()));
							oMaterial.setName(oneData.getMaterial2());
							oMaterials.add(oMaterial);
						}
						if ( !Check.Null(oneData.getMaterial3()) && !Check.Null(oneData.getMaterialID3()))
						{
							OMaterial oMaterial = new OMaterial();
							oMaterial.setId(Long.valueOf(oneData.getMaterialID3()));
							oMaterial.setName(oneData.getMaterial3());
							oMaterials.add(oMaterial);
						}
						if ( !Check.Null(oneData.getMaterial4()) && !Check.Null(oneData.getMaterialID4()))
						{
							OMaterial oMaterial = new OMaterial();
							oMaterial.setId(Long.valueOf(oneData.getMaterialID4()));
							oMaterial.setName(oneData.getMaterial4());
							oMaterials.add(oMaterial);
						}
						if ( !Check.Null(oneData.getMaterial5()) && !Check.Null(oneData.getMaterialID5()))
						{
							OMaterial oMaterial = new OMaterial();
							oMaterial.setId(Long.valueOf(oneData.getMaterialID5()));
							oMaterial.setName(oneData.getMaterial5());
							oMaterials.add(oMaterial);
						}
						if ( !Check.Null(oneData.getMaterial6()) && !Check.Null(oneData.getMaterialID6()))
						{
							OMaterial oMaterial = new OMaterial();
							oMaterial.setId(Long.valueOf(oneData.getMaterialID6()));
							oMaterial.setName(oneData.getMaterial6());
							oMaterials.add(oMaterial);
						}
						if ( !Check.Null(oneData.getMaterial7()) && !Check.Null(oneData.getMaterialID7()))
						{
							OMaterial oMaterial = new OMaterial();
							oMaterial.setId(Long.valueOf(oneData.getMaterialID7()));
							oMaterial.setName(oneData.getMaterial7());
							oMaterials.add(oMaterial);
						}
						if ( !Check.Null(oneData.getMaterial8()) && !Check.Null(oneData.getMaterialID8()))
						{
							OMaterial oMaterial = new OMaterial();
							oMaterial.setId(Long.valueOf(oneData.getMaterialID8()));
							oMaterial.setName(oneData.getMaterial8());
							oMaterials.add(oMaterial);
						}
						if ( !Check.Null(oneData.getMaterial9()) && !Check.Null(oneData.getMaterial9()))
						{
							OMaterial oMaterial = new OMaterial();
							oMaterial.setId(Long.valueOf(oneData.getMaterialID9()));
							oMaterial.setName(oneData.getMaterial9());
							oMaterials.add(oMaterial);
						}
						if ( !Check.Null(oneData.getMaterial10()) && !Check.Null(oneData.getMaterialID10()))
						{
							OMaterial oMaterial = new OMaterial();
							oMaterial.setId(Long.valueOf(oneData.getMaterialID10()));
							oMaterial.setName(oneData.getMaterial10());
							oMaterials.add(oMaterial);
						}
						//给个默认原料
					 if (oMaterials == null || oMaterials.isEmpty())
						{						 
						  OMaterial oMaterial = new OMaterial();
							oMaterial.setId(10042L);
							oMaterial.setName("小麦");
							oMaterials.add(oMaterial);							
						}
						
						properties.put(OItemUpdateProperty.materials,oMaterials);	
					
						
						List<OSpec> oSpecs = new ArrayList<OSpec>();
						try 
						{	
							for(DCP_OrderPlatformGoodsQueryRes.level2Spec oneDataSpec : oneData.getSpecs())
							{
								String specId = oneDataSpec.getOrderSpecID().trim();							
								String specName = oneDataSpec.getOrderSpecName().trim();//
								String extendCode = oneDataSpec.getPluBarcode().trim();//
								String orderGoodsName = orderPluName + specName;
								String stock = oneDataSpec.getOrderStock();
								String price = oneDataSpec.getOrderPrice();
								String onShelf = oneDataSpec.getOrderOnShelf();
								int onShelf_i = 1;//默认上架
								if (onShelf != null && onShelf.equals("N"))
								{
									onShelf_i = 0;
								}
																					
								int stock_i = 9999;
								try 
								{
									stock_i = Integer.parseInt(stock);
					
				        } 
								catch (Exception e) 
								{
									stock_i = 9999;
					
				        }
								int netWeight = 0;
								try 
								{
									netWeight =	(int)(oneDataSpec.getNetWeight());				
					      } 
								catch (Exception e) 
								{
									netWeight = 0;						
					      }
								
								double price_d = 0;
								
								try 
								{
									price_d = Double.parseDouble(price);
					
				        } 
								catch (Exception e) 
								{
					
				        }
										
								//region 开始与模板对比
								for(DCP_OrderGoodsQueryRes.level1Elm temp : tempBaseGoods.getDatas())
								{
									String temp_pluname = temp.getPluName().trim();							
									String temp_pluno = temp.getPluNO().trim();								
									for(DCP_OrderGoodsQueryRes.level2Spec tempSpec : temp.getSpecDatas())
									{
										String temp_specno = tempSpec.getSpecNO().trim();//菜品池条码
										String temp_specname = tempSpec.getSpecName().trim();//菜品池规格名称
										String temp_goodname = temp_pluname + temp_specname;

										//资料比对，IF spec只有一笔，直接比对商品名称，不比对商品规格
										if (oneData.getSpecs().size()==1)
										{
											if ( orderPluName.equals(temp_pluname))
											{
												IsMatch =true;
												extendCode = temp_specno;		
												pluNO = temp_pluno;//后面回写
												pluName = temp_pluname;
												oneDataSpec.setPluBarcode(extendCode);//重新赋值，为了后面update本地数据
												break;
											}
											else
											{
												//资料比对 菜品池商品+菜品池规格
												if (temp_goodname != null && temp_goodname.isEmpty() == false && orderGoodsName.equals(temp_goodname))
												{
													IsMatch =true;
													extendCode = temp_specno;		
													pluNO = temp_pluno;//后面回写
													pluName = temp_pluname;
													oneDataSpec.setPluBarcode(extendCode);//重新赋值，为了后面update本地数据
													break;
												}	
											}
										}
										else
										{
											//资料比对 菜品池商品+菜品池规格
											if (temp_goodname != null && temp_goodname.isEmpty() == false && orderGoodsName.equals(temp_goodname))
											{
												IsMatch =true;
												extendCode = temp_specno;		
												pluNO = temp_pluno;//后面回写
												pluName = temp_pluname;
												oneDataSpec.setPluBarcode(extendCode);//重新赋值，为了后面update本地数据
												break;
											}	
										}
									}
								}							
								//endregion

								OSpec oSpec = new OSpec();
								oSpec.setSpecId(new Long(specId));
								oSpec.setExtendCode(extendCode);
								oSpec.setPrice(price_d);
								oSpec.setName(specName);
								oSpec.setMaxStock(10000);
								oSpec.setStock(stock_i);
								oSpec.setWeight(netWeight);
								oSpec.setOnShelf(onShelf_i);
								oSpecs.add(oSpec);
							}

							if(!IsMatch)
							{
								HelpTools.writelog_fileName("【批量同步商品映射】同步循环第【"+j+"】个商品结束, 【没有匹配上菜品池匹配相应商品名称+规格名称】无需调用第三方接口！ 当前平台商品ID/名称："+orderPLUNO+"/"+orderPluName, goodsLogFileName);
								continue;
							}
							properties.put(OItemUpdateProperty.specs,oSpecs);	
							String isAllTimeSell = oneData.getIsAllTimeSell();
							if (isAllTimeSell != null && isAllTimeSell.equals("N")) // 不是全时段销售
							{
								OItemSellingTime oItemSellingTime = new OItemSellingTime();
								oItemSellingTime.setBeginDate(oneData.getBeginDate());
								oItemSellingTime.setEndDate(oneData.getEndDate());
								List<OItemWeekEnum> weeks = new ArrayList<OItemWeekEnum>();
							  String sellWeek =	oneData.getSellWeek();						  
						  	if(sellWeek!=null&&sellWeek.isEmpty()==false)
						  	{
						  		String[] ss = sellWeek.split(",");
						  		for (String weekStr : ss) 
						  		{
						  			if(weekStr.equals("1"))
						  			{
						  				weeks.add(OItemWeekEnum.MONDAY);
						  			}
						  			else if(weekStr.equals("2"))
						  			{
						  				weeks.add(OItemWeekEnum.TUESDAY);
						  			}
						  			else if(weekStr.equals("3"))
						  			{
						  				weeks.add(OItemWeekEnum.WEDNESDAY);
						  			}
						  			else if(weekStr.equals("4"))
						  			{
						  				weeks.add(OItemWeekEnum.THURSDAY);
						  			}
						  			else if(weekStr.equals("5"))
						  			{
						  				weeks.add(OItemWeekEnum.FRIDAY);
						  			}
						  			else if(weekStr.equals("6"))
						  			{
						  				weeks.add(OItemWeekEnum.SATURDAY);
						  			}
						  			else if(weekStr.equals("7"))
						  			{
						  				weeks.add(OItemWeekEnum.SUNDAY);
						  			}
					
					        }
						  		oItemSellingTime.setWeeks(weeks);
						  	}
							  
						  	List<OItemTime> times = new ArrayList<OItemTime>();
						  	
						  	String sellTime = oneData.getSellTime();
						  	if(sellTime!=null&&sellTime.isEmpty()==false)
						  	{
						  		String[] ssTime = sellTime.split(",");//16:00-19:00,20:00-23:00
						  		for (String timeStr : ssTime) 
						  		{
						  			String[] ss1 = timeStr.split("-");//16:00-19:00						  			
					  				OItemTime oItemTime = new OItemTime();
										oItemTime.setBeginTime(ss1[0]);
										oItemTime.setEndTime(ss1[1]);
										times.add(oItemTime);					
					          					  							
					        }					  		
						  		oItemSellingTime.setTimes(times);
						  		
						  	}
						  	properties.put(OItemUpdateProperty.sellingTime,oItemSellingTime);
							}
							//属性
							try 
							{
								if (oneData.getAttributes() != null && oneData.getAttributes().size() > 0)
								{
									List<OItemAttribute> oItemAttributes = new ArrayList<OItemAttribute>();
									for(DCP_OrderPlatformGoodsQueryRes.level2Attribute oneDataAttri : oneData.getAttributes())
									{
										OItemAttribute oItemAttribute = new OItemAttribute();
										oItemAttribute.setName(oneDataAttri.getName());									
										oItemAttribute.setDetails(oneDataAttri.getDetails());
										
										oItemAttributes.add(oItemAttribute);
										
									}
									
									properties.put(OItemUpdateProperty.attributes,oItemAttributes);
									
								}
								
								
							}
							catch(Exception e)
							{
								
							}
							
							
							boolean nRet = false;
							if(isGoNewFunction)
							{
								nRet = WMELMProductService.updateItem(elmIsSandbox,elmAPPKey,elmAPPSecret,elmAPPName,new Long(orderPLUNO), new Long(orderCategoryNO), properties, errorMessage);
							}
							else
							{
								nRet = WMELMProductService.updateItem(new Long(orderPLUNO), new Long(orderCategoryNO), properties, errorMessage);
							}

							Thread.sleep(100);//分类ID 18次/秒
							String result = "N";
							if (nRet) 
							{
								result = "Y";
								oneData.setResultMapping(result);								
								oneData.setPluNO(pluNO);
								oneData.setPluName(pluName);
								oneData.setResultMappingDescription("同步成功！");
								HelpTools.writelog_fileName("【批量同步商品映射】同步循环第【"+j+"】个商品结束, 同步成功:"+result+" 当前平台商品ID/名称："+orderPLUNO+"/"+orderPluName, goodsLogFileName);
							}	
							else
							{
								oneData.setResultMapping(result);
								oneData.setResultMappingDescription(errorMessage.toString());
								HelpTools.writelog_fileName("【批量同步商品映射】同步循环第【"+j+"】个商品结束, 同步失败:"+errorMessage.toString()+" 当前平台商品ID/名称："+orderPLUNO+"/"+orderPluName, goodsLogFileName);
							}

						}
						catch (Exception e) 
						{
							oneData.setResultMapping("N");
							oneData.setResultMappingDescription(e.getMessage());
							HelpTools.writelog_fileName("【批量同步商品映射】同步循环第【"+j+"】个商品，异常:"+e.getMessage()+" 当前平台商品ID/名称："+orderPLUNO+"/"+orderPluName, goodsLogFileName);
							continue;		
						}	
					}
				}
				catch (Exception e) 
				{
					//throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
				}
			}
			else if(loadDocType.equals("2"))
			{
				try
				{
					HelpTools.writelog_fileName("【批量同步商品映射】开始同步当前门店商品, 平台类型LoadDocType:"+loadDocType+" 当前门店shopId："+erpShopNO+" 同步的商品总数："+needMappingGoods.getDatas().size(), goodsLogFileName);
					int j = 0;
					for(DCP_OrderPlatformGoodsQueryRes.level1Elm oneData : needMappingGoods.getDatas())
					{
						j++;
						StringBuilder errorMessage = new StringBuilder();
						String orderPLUNO = oneData.getOrderPluNO().trim();
						String orderPluName = oneData.getOrderPluName().trim();
						String orderCategoryNO = oneData.getOrderCategoryNO();
						String pluNO = oneData.getPluNO();
						String pluName = oneData.getPluName();
						boolean IsMatch = false;
						JSONArray array = new JSONArray();
						JSONObject object = new JSONObject();
						object.put("dishId", new Long(orderPLUNO));
						object.put("eDishCode", pluNO);
						JSONArray array_sku = new JSONArray();				
						try 
						{	
							for(DCP_OrderPlatformGoodsQueryRes.level2Spec oneDataSpec : oneData.getSpecs())
							{
								String specId = oneDataSpec.getOrderSpecID().trim();							
								String specName = oneDataSpec.getOrderSpecName().trim();//
								String extendCode = oneDataSpec.getPluBarcode().trim();//
								String orderGoodsName = orderPluName + specName;
								//region 开始与模板对比
								for(DCP_OrderGoodsQueryRes.level1Elm temp : tempBaseGoods.getDatas())
								{
									String temp_pluname = temp.getPluName().trim();							
									String temp_pluno = temp.getPluNO().trim();								
									for(DCP_OrderGoodsQueryRes.level2Spec tempSpec : temp.getSpecDatas())
									{
										String temp_specno = tempSpec.getSpecNO().trim();//菜品池条码
										String temp_specname = tempSpec.getSpecName().trim();//菜品池规格名称
										String temp_goodname = temp_pluname + temp_specname;

										//资料比对，IF spec只有一笔，直接比对商品名称，不比对商品规格
										if (oneData.getSpecs().size()==1)
										{
											if ( orderPluName.equals(temp_pluname))
											{
												IsMatch =true;
												extendCode = temp_specno;		
												pluNO = temp_pluno;//后面回写
												pluName = temp_pluname;
												oneDataSpec.setPluBarcode(extendCode);//重新赋值，为了后面update本地数据
												break;
											}
											else
											{
												//资料比对 菜品池商品+菜品池规格
												if (temp_goodname != null && temp_goodname.isEmpty() == false && orderGoodsName.equals(temp_goodname))
												{
													IsMatch =true;
													extendCode = temp_specno;		
													pluNO = temp_pluno;//后面回写
													pluName = temp_pluname;
													oneDataSpec.setPluBarcode(extendCode);//重新赋值，为了后面update本地数据
													break;
												}	
											}
										}
										else
										{
											if (temp_goodname != null && temp_goodname.isEmpty() == false&& orderGoodsName.equals(temp_goodname))
											{
												extendCode = temp_specno;		
												pluNO = temp_pluno;//后面回写
												pluName = temp_pluname;
												oneDataSpec.setPluBarcode(extendCode);//重新赋值，为了后面update本地数据
												IsMatch = true;
												break;
											}	
										}
									}
								}							
								//endregion

								JSONObject object_sku = new JSONObject();
								object_sku.put("dishSkuId", new Long(specId));
								object_sku.put("eDishSkuCode", extendCode);
								array_sku.put(object_sku);							
							}
							object.put("eDishCode", pluNO);
							object.put("waiMaiDishSkuMappings", array_sku);
							array.put(object);
							String dishMappings = array.toString();
							if(!IsMatch)
							{
								HelpTools.writelog_fileName("【批量同步商品映射】同步循环第【"+j+"】个商品结束, 【没有匹配上菜品池匹配相应商品名称+规格名称】无需调用第三方接口！ 当前平台商品ID/名称："+orderPLUNO+"/"+orderPluName, goodsLogFileName);
								continue;
							}

							boolean nRet = false;
							nRet = WMJBPProductService.dishMapping(eId, erpShopNO, dishMappings, errorMessage);

							String result = "N";
							if (nRet) 
							{
								result = "Y";
								oneData.setResultMapping(result);								
								oneData.setPluNO(pluNO);
								oneData.setPluName(pluName);
								oneData.setResultMappingDescription("同步成功！");
								HelpTools.writelog_fileName("【批量同步商品映射】同步循环第【"+j+"】个商品结束, 同步成功:"+result+" 当前平台商品ID/名称："+orderPLUNO+"/"+orderPluName, goodsLogFileName);
							}	
							else
							{
								oneData.setResultMapping(result);
								oneData.setResultMappingDescription(errorMessage.toString());
								HelpTools.writelog_fileName("【批量同步商品映射】同步循环第【"+j+"】个商品结束, 同步失败:"+errorMessage.toString()+" 当前平台商品ID/名称："+orderPLUNO+"/"+orderPluName, goodsLogFileName);
							}

						}
						catch (Exception e) 
						{
							oneData.setResultMapping("N");
							oneData.setResultMappingDescription(e.getMessage());
							HelpTools.writelog_fileName("【批量同步商品映射】同步循环第【"+j+"】个商品，异常:"+e.getMessage()+" 当前平台商品ID/名称："+orderPLUNO+"/"+orderPluName, goodsLogFileName);
							continue;		
						}	

					}

				}
				catch (Exception e) 
				{
					//throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
				}
			}
			else 
			{

				//京东到家
				try
				{
					HelpTools.writelog_fileName("【批量同步商品映射】开始同步当前门店商品, 平台类型LoadDocType:"+loadDocType+" 当前门店shopId："+erpShopNO+" 同步的商品总数："+needMappingGoods.getDatas().size(), goodsLogFileName);
					int j = 0;
					for(DCP_OrderPlatformGoodsQueryRes.level1Elm oneData : needMappingGoods.getDatas())
					{
						j++;
						StringBuilder errorMessage = new StringBuilder();
						String orderPLUNO = oneData.getOrderPluNO().trim();
						String orderPluName = oneData.getOrderPluName().trim();
						String orderCategoryNO = oneData.getOrderCategoryNO();
						String pluNO = oneData.getPluNO();
						String pluName = oneData.getPluName();
						boolean IsMatch = false;
						JSONObject body = new JSONObject();
						JSONArray skuInfoListMap = new JSONArray();						

						try 
						{	
							for(DCP_OrderPlatformGoodsQueryRes.level2Spec oneDataSpec : oneData.getSpecs())
							{
								String specId = oneDataSpec.getOrderSpecID().trim();							
								String specName = oneDataSpec.getOrderSpecName().trim();//
								String extendCode = oneDataSpec.getPluBarcode().trim();//
								String orderGoodsName = orderPluName + specName;
								//region 开始与模板对比
								for(DCP_OrderGoodsQueryRes.level1Elm temp : tempBaseGoods.getDatas())
								{
									String temp_pluname = temp.getPluName().trim();							
									String temp_pluno = temp.getPluNO().trim();								
									for(DCP_OrderGoodsQueryRes.level2Spec tempSpec : temp.getSpecDatas())
									{
										String temp_specno = tempSpec.getSpecNO().trim();//菜品池条码
										String temp_specname = tempSpec.getSpecName().trim();//菜品池规格名称
										String temp_goodname = temp_pluname + temp_specname;

										//资料比对，IF spec只有一笔，直接比对商品名称，不比对商品规格
										if (oneData.getSpecs().size()==1)
										{
											if ( orderPluName.equals(temp_pluname))
											{
												IsMatch =true;
												extendCode = temp_specno;		
												pluNO = temp_pluno;//后面回写
												pluName = temp_pluname;
												oneDataSpec.setPluBarcode(extendCode);//重新赋值，为了后面update本地数据
												break;
											}
											else
											{
												//资料比对 菜品池商品+菜品池规格
												if (temp_goodname != null && temp_goodname.isEmpty() == false && orderGoodsName.equals(temp_goodname))
												{
													IsMatch =true;
													extendCode = temp_specno;		
													pluNO = temp_pluno;//后面回写
													pluName = temp_pluname;
													oneDataSpec.setPluBarcode(extendCode);//重新赋值，为了后面update本地数据
													break;
												}	
											}
										}
										else
										{
											if (temp_goodname != null && temp_goodname.isEmpty() == false&& orderGoodsName.equals(temp_goodname))
											{
												extendCode = temp_specno;		
												pluNO = temp_pluno;//后面回写
												pluName = temp_pluname;
												oneDataSpec.setPluBarcode(extendCode);//重新赋值，为了后面update本地数据
												IsMatch = true;
												break;
											}	
										}
									}
								}							
								//endregion

								JSONObject skuInfoMap_item = new JSONObject();			
								skuInfoMap_item.put("skuId", Long.valueOf(orderPLUNO) );      //节点值类型Long
								skuInfoMap_item.put("outSkuId", extendCode  ); //节点值类型String
								skuInfoListMap.put(skuInfoMap_item);					
							}

							body.put("skuInfoList", skuInfoListMap);							
							String skuInfoListJson = body.toString();
							if(!IsMatch)
							{
								HelpTools.writelog_fileName("【批量同步商品映射】同步循环第【"+j+"】个商品结束, 【没有匹配上菜品池匹配相应商品名称+规格名称】无需调用第三方接口！ 当前平台商品ID/名称："+orderPLUNO+"/"+orderPluName, goodsLogFileName);
								continue;
							}

							boolean nRet = false;
							nRet = HelpJDDJHttpUtil.batchUpdateOutSkuId(skuInfoListJson, errorMessage);
							String result = "N";
							if (nRet) 
							{
								result = "Y";
								oneData.setResultMapping(result);								
								oneData.setPluNO(pluNO);
								oneData.setPluName(pluName);
								oneData.setResultMappingDescription("同步成功！");
								HelpTools.writelog_fileName("【批量同步商品映射】同步循环第【"+j+"】个商品结束, 同步成功:"+result+" 当前平台商品ID/名称："+orderPLUNO+"/"+orderPluName, goodsLogFileName);
							}	
							else
							{
								oneData.setResultMapping(result);
								oneData.setResultMappingDescription(errorMessage.toString());
								HelpTools.writelog_fileName("【批量同步商品映射】同步循环第【"+j+"】个商品结束, 同步失败:"+errorMessage.toString()+" 当前平台商品ID/名称："+orderPLUNO+"/"+orderPluName, goodsLogFileName);
							}

						}
						catch (Exception e) 
						{
							oneData.setResultMapping("N");
							oneData.setResultMappingDescription(e.getMessage());
							HelpTools.writelog_fileName("【批量同步商品映射】同步循环第【"+j+"】个商品，异常:"+e.getMessage()+" 当前平台商品ID/名称："+orderPLUNO+"/"+orderPluName, goodsLogFileName);
							continue;		
						}	

					}

				}
				catch (Exception e) 
				{
					//throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
				}













			}

			//region 同步成功的回写 OC_mappinggoods表		  
			this.UpdateLocalDB(loadDocType, eId, erpShopNO, needMappingGoods);	  
			//endregion

		}

		//endregion

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderPlatformGoodsBatchMappingReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderPlatformGoodsBatchMappingReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderPlatformGoodsBatchMappingReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderPlatformGoodsBatchMappingReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；

		if(Check.Null(req.getLoadDocType()))
		{
			errCt++;
			errMsg.append("平台类型LoadDocType不可为空值, ");
			isFail = true;
		}

		if(req.getErpShopNO()==null||req.getErpShopNO().length ==0)
		{
			errCt++;
			errMsg.append("门店编码不能为空, ");
			isFail = true;
		}

		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderPlatformGoodsBatchMappingReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderPlatformGoodsBatchMappingReq>(){};
	}

	@Override
	protected DCP_OrderPlatformGoodsBatchMappingRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderPlatformGoodsBatchMappingRes();
	}

	/**
	 * 查询菜品池里面商品信息
	 * @return
	 */
	private DCP_OrderGoodsQueryRes GetTempGoods(DCP_OrderPlatformGoodsBatchMappingReq req) throws Exception
	{
		String eId = req.geteId();
		String belFirm = null;
		if(req.getOrg_Form().equals("0"))
		{
			belFirm = req.getOrganizationNO();
		}
		else if(req.getOrg_Form().equals("2"))
		{
			belFirm = req.getBELFIRM();
		}
		HelpTools.writelog_fileName("【公司别的取值】belFirm="+belFirm, "belFirm");
		
		String sql = " SELECT * FROM (";
		sql +=" select A.*,B.SPECNO,B.SPECNAME,B.PRICE,B.STOCKQTY,B.PACKAGEFEE,B.ISONSHELF from OC_goods A inner join OC_goods_spec B on A.EID=B.EID and A.PLUNO=B.PLUNO and A.BELFIRM=B.BELFIRM";
		sql +=" where A.EID='"+eId+"'";	
		
		if (belFirm != null && belFirm.trim().length() > 0)
		{
			sql +=" and A.BELFIRM='"+belFirm+"'";
		}
		
		sql +=")";
		HelpTools.writelog_fileName("【批量同步商品映射】 获取菜品池SQL:"+sql,  goodsLogFileName);
		List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);
		if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
		{
			DCP_OrderGoodsQueryRes res = new DCP_OrderGoodsQueryRes();
			res.setDatas(new ArrayList<DCP_OrderGoodsQueryRes.level1Elm>());
			//单头主键字段
			Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
			condition.put("PLUNO", true);		
			//调用过滤函数
			List<Map<String, Object>> getQHeader=MapDistinct.getMap(getQDataDetail, condition);						
			for (Map<String, Object> oneData : getQHeader) 
			{
				DCP_OrderGoodsQueryRes.level1Elm oneLv1 = res.new level1Elm();
				String pluNO = oneData.get("PLUNO").toString();
				String pluName = oneData.get("PLUNAME").toString();
				String categoryNO = oneData.get("CATEGORYNO").toString();
				//String categoryName = oneData.get("CATEGORYNAME").toString();
				String description = oneData.get("DESCRIPTION").toString();
				String fileName = oneData.get("FILENAME").toString();
				String unit = oneData.get("UNIT").toString();
				String priority = oneData.get("PRIORITY").toString();
				String status = oneData.get("STATUS").toString();

				oneLv1.setPluNO(pluNO);
				oneLv1.setPluName(pluName);
				oneLv1.setCategoryNO(categoryNO);
				//oneLv1.setCategoryName(categoryName);
				oneLv1.setDescription(description);
				oneLv1.setFileName(fileName);
				oneLv1.setUnit(unit);
				oneLv1.setPriority(priority);
				oneLv1.setStatus(status);

				oneLv1.setSpecDatas(new ArrayList<DCP_OrderGoodsQueryRes.level2Spec>());	
				//oneLv1.setAttrDatas(new ArrayList<OrderGoodsGetRes.level2Attr>());	

				//region SPEC
				Map<String, Boolean> condition_spec = new HashMap<String, Boolean>(); //查詢條件
				condition_spec.put("PLUNO", true);
				condition_spec.put("SPECNO", true);
				//调用过滤函数
				List<Map<String, Object>> getQHeader_SPEC = MapDistinct.getMap(getQDataDetail, condition_spec);							
				for (Map<String, Object> oneData_SPEC : getQHeader_SPEC) 
				{
					DCP_OrderGoodsQueryRes.level2Spec onelv2_SPEC = res.new level2Spec();
					String pluNo_SPEC = oneData_SPEC.get("PLUNO").toString();
					if(pluNo_SPEC.equals(pluNO))
					{						
						String specNO = oneData_SPEC.get("SPECNO").toString();
						String specName = oneData_SPEC.get("SPECNAME").toString();
						String price = oneData_SPEC.get("PRICE").toString();
						String stockQty = oneData_SPEC.get("STOCKQTY").toString();
						String packageFee = oneData_SPEC.get("PACKAGEFEE").toString();
						String isOnshelf = oneData_SPEC.get("ISONSHELF").toString();

						onelv2_SPEC.setSpecNO(specNO);
						onelv2_SPEC.setSpecName(specName);
						onelv2_SPEC.setPrice(price);
						onelv2_SPEC.setStockQty(stockQty);
						onelv2_SPEC.setPackageFee(packageFee);
						onelv2_SPEC.setIsOnshelf(isOnshelf);
						onelv2_SPEC.setStatus("100");
						oneLv1.getSpecDatas().add(onelv2_SPEC);						
					}
				}

				res.getDatas().add(oneLv1);
			}	

			return res;
		}

		return null;
	}


	/**
	 * 查询需要同步的门店的商品资料
	 * @param loadDocType
	 * @param eId
	 * @param erpShopNO
	 * @return
	 * @throws Exception
	 */
	private DCP_OrderPlatformGoodsQueryRes GetNeedMappingGoods(String loadDocType,String eId,String erpShopNO,String isEmptyPluNO) throws Exception
	{		
		String sql =" select * from (";
		sql += " select A.*,B.SPECNO,B.Specname,B.ORDER_SPECNO,B.ORDER_SPECNAME,B.Price,B.STOCKQTY,B.PACKAGEFEE,B.ISONSHELF,B.NETWEIGHT,C.ATTRNAME,C.ATTRVALUE,C.ORDER_ATTRNAME,C.ORDER_ATTRVALUE from OC_mappinggoods A inner join OC_mappinggoods_spec B on A.EID=B.EID and A.SHOPID=B.SHOPID and A.Load_Doctype=B.LOAD_DOCTYPE and A.Order_Pluno=B.Order_Pluno";
		sql += " left join OC_mappinggoods_attr C on A.EID=C.EID and A.SHOPID=C.SHOPID and A.Load_Doctype=C.LOAD_DOCTYPE and A.Order_Pluno=C.Order_Pluno";
		sql += " where A.EID='"+eId+"' and A.LOAD_DOCTYPE='"+loadDocType+"' and A.SHOPID='"+erpShopNO+"'";
		if(loadDocType.equals("1"))
		{
			if(isEmptyPluNO==null||isEmptyPluNO.equals("Y"))
			{
				sql += " and A.PLUNO=' '";
			}
		}
		
				
		sql +=")"; 

		List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);
		if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
		{
			DCP_OrderPlatformGoodsQueryRes res = new DCP_OrderPlatformGoodsQueryRes();
			res.setDatas(new ArrayList<DCP_OrderPlatformGoodsQueryRes.level1Elm>());
			//单头主键字段
			Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
			condition.put("ORDER_PLUNO", true);	
			//调用过滤函数
			List<Map<String, Object>> getQHeader=MapDistinct.getMap(getQDataDetail, condition);
			
			condition.put("ORDER_SPECNO", true);
			List<Map<String, Object>> getSpecDetail=MapDistinct.getMap(getQDataDetail, condition);
			
		//单头主键字段
			Map<String, Boolean> condition_Attri = new HashMap<String, Boolean>(); //查詢條件
			condition_Attri.put("ORDER_PLUNO", true);	
			condition_Attri.put("ORDER_ATTRNAME", true);	
			List<Map<String, Object>> getAttributeDetail=MapDistinct.getMap(getQDataDetail, condition_Attri);
			
			for (Map<String, Object> map : getQHeader) 
			{
				DCP_OrderPlatformGoodsQueryRes.level1Elm oneLv1 = res.new level1Elm();

				oneLv1.setSpecs(new ArrayList<DCP_OrderPlatformGoodsQueryRes.level2Spec>());
				oneLv1.setAttributes(new ArrayList<DCP_OrderPlatformGoodsQueryRes.level2Attribute>());

				String orderPluNO = map.get("ORDER_PLUNO").toString();
				String orderPluName = map.get("ORDER_PLUNAME").toString();//商品名
				String orderCategoryNO = map.get("ORDER_CATEGORYNO").toString();//饿了么分类ID
				String orderDescription = map.get("DESCRIPTION").toString();
				String orderUnit = map.get("UNIT").toString();
				String orderImageUrl = map.get("FILENAME").toString();
				String orderPriority= map.get("PRIORITY").toString();
				String isAllTimeSell = map.get("ISALLTIMESELL").toString();
				String beginDate = map.get("BEGINDATE").toString();
				String endDate = map.get("ENDDATE").toString();
				String sellWeek = map.get("SELLWEEK").toString();
				String sellTime = map.get("SELLTIME").toString();
				
				String materialID1 = map.get("MATERIALID1").toString();
				String materialID2 = map.get("MATERIALID2").toString();
				String materialID3 = map.get("MATERIALID3").toString();
				String materialID4 = map.get("MATERIALID4").toString();
				String materialID5 = map.get("MATERIALID5").toString();
				String materialID6 = map.get("MATERIALID6").toString();
				String materialID7 = map.get("MATERIALID7").toString();
				String materialID8 = map.get("MATERIALID8").toString();
				String materialID9 = map.get("MATERIALID9").toString();
				String materialID10 = map.get("MATERIALID10").toString();

				String material1 = map.get("MATERIAL1").toString();
				String material2 = map.get("MATERIAL2").toString();
				String material3 = map.get("MATERIAL3").toString();
				String material4 = map.get("MATERIAL4").toString();
				String material5 = map.get("MATERIAL5").toString();
				String material6 = map.get("MATERIAL6").toString();
				String material7 = map.get("MATERIAL7").toString();
				String material8 = map.get("MATERIAL8").toString();
				String material9 = map.get("MATERIAL9").toString();
				String material10 = map.get("MATERIAL10").toString();

				oneLv1.setOrderPluNO(orderPluNO);
				oneLv1.setOrderPluName(orderPluName);
				oneLv1.setOrderCategoryNO(orderCategoryNO);
				oneLv1.setOrderCategoryName("");//没有返回分类名称
				oneLv1.setOrderDescription(orderDescription);
				oneLv1.setOrderImageUrl(orderImageUrl);
				oneLv1.setOrderUnit(orderUnit);
				oneLv1.setOrderPriority(orderPriority);
				oneLv1.setIsAllTimeSell(isAllTimeSell);
				oneLv1.setBeginDate(beginDate);
				oneLv1.setEndDate(endDate);
				oneLv1.setSellWeek(sellWeek);
				oneLv1.setSellTime(sellTime);
				
				oneLv1.setMaterialID1(materialID1);
				oneLv1.setMaterialID2(materialID2);
				oneLv1.setMaterialID3(materialID3);
				oneLv1.setMaterialID4(materialID4);
				oneLv1.setMaterialID5(materialID5);
				oneLv1.setMaterialID6(materialID6);
				oneLv1.setMaterialID7(materialID7);
				oneLv1.setMaterialID8(materialID8);
				oneLv1.setMaterialID9(materialID9);
				oneLv1.setMaterialID10(materialID10);							
				oneLv1.setMaterial1(material1);
				oneLv1.setMaterial2(material2);
				oneLv1.setMaterial3(material3);
				oneLv1.setMaterial4(material4);
				oneLv1.setMaterial5(material5);
				oneLv1.setMaterial6(material6);
				oneLv1.setMaterial7(material7);
				oneLv1.setMaterial8(material8);
				oneLv1.setMaterial9(material9);
				oneLv1.setMaterial10(material10);
				

				for (Map<String, Object> oneLv2 : getSpecDetail) 
				{
					String orderPluNO2 = oneLv2.get("ORDER_PLUNO").toString();
					if(orderPluNO2.equals(orderPluNO)==false)
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
					String netWeight = oneLv2.get("NETWEIGHT").toString();
					int netWeight_d = 0;
					try 
					{
						netWeight_d = Integer.parseInt(netWeight);
			
			    } 
					catch (Exception e) 
					{
						netWeight_d = 0;
			
			    }

					orderSpec.setOrderSpecID(orderSpecID);
					orderSpec.setOrderSpecName(orderSpecName);
					orderSpec.setOrderPrice(orderPrice);
					orderSpec.setOrderStock(orderStock);
					orderSpec.setOrderPackingFee(orderPackingFee);
					orderSpec.setOrderOnShelf(orderOnShelf);
					orderSpec.setPluBarcode(pluBarcode);
					orderSpec.setPluSpecName(pluSpecName);
					orderSpec.setNetWeight(netWeight_d);
					orderSpec.setOrdermaxStock("9999");

					oneLv1.getSpecs().add(orderSpec);	

				}
				
				for (Map<String, Object> oneLv2_Attri : getAttributeDetail) 
				{
					String orderPluNO2 = oneLv2_Attri.get("ORDER_PLUNO").toString();
					String orderAttributeName = oneLv2_Attri.get("ORDER_ATTRNAME").toString();
					String orderAttributeValue = oneLv2_Attri.get("ORDER_ATTRVALUE").toString();
					if (orderAttributeName == null || orderAttributeName.isEmpty()||orderAttributeValue == null || orderAttributeValue.isEmpty())
					{
						continue;
					}
					if(orderPluNO2.equals(orderPluNO)==false)
					{
						continue;
					}
					DCP_OrderPlatformGoodsQueryRes.level2Attribute orderAttribute = res.new level2Attribute();
					List<String> attrValueList = new ArrayList<String>();			
					String[] ssAttrValue = orderAttributeValue.split(","); //7分甜，6分甜，8分甜
					for (String attrValueStr : ssAttrValue) 
					{
						attrValueList.add(attrValueStr);
					}	
					orderAttribute.setName(orderAttributeName);
					orderAttribute.setDetails(attrValueList);
					
					oneLv1.getAttributes().add(orderAttribute);
					
				}
				

				res.getDatas().add(oneLv1);

			}

			return res;
		}

		return null;
	}

	/**
	 * 
	 * @param loadDocType
	 * @param erpShopNO
	 * @param res
	 */
	private void UpdateLocalDB(String loadDocType,String eId,String erpShopNO,DCP_OrderPlatformGoodsQueryRes res) throws Exception
	{
		if(res==null||res.getDatas()==null||res.getDatas().size()==0)
		{
			return;
		}

		for (DCP_OrderPlatformGoodsQueryRes.level1Elm oneLv1 : res.getDatas()) 
		{
			if(oneLv1.getResultMapping()==null||oneLv1.getResultMapping().equals("N"))
			{
				continue;
			}
			String orderPluNO = oneLv1.getOrderPluNO();
			String orderPluName = oneLv1.getOrderPluName();
			String pluNO = oneLv1.getPluNO();
			String pluName = oneLv1.getPluName();

			UptBean up1 = new UptBean("OC_mappinggoods");
			up1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			up1.addCondition("LOAD_DOCTYPE", new DataValue(loadDocType, Types.VARCHAR));
			up1.addCondition("SHOPID", new DataValue(erpShopNO, Types.VARCHAR));
			up1.addCondition("ORDER_PLUNO", new DataValue(orderPluNO, Types.VARCHAR));

			up1.addUpdateValue("PLUNO", new DataValue(pluNO, Types.VARCHAR));
			up1.addUpdateValue("PLUNAME", new DataValue(pluName, Types.VARCHAR));

			this.addProcessData(new DataProcessBean(up1));

			for (DCP_OrderPlatformGoodsQueryRes.level2Spec oneLv1Spec : oneLv1.getSpecs()) 
			{
				String orderSpecNO = oneLv1Spec.getOrderSpecID();				
				String pluBarcode = oneLv1Spec.getPluBarcode();	

				UptBean up2 = new UptBean("OC_mappinggoods_spec");

				up2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				up2.addCondition("LOAD_DOCTYPE", new DataValue(loadDocType, Types.VARCHAR));
				up2.addCondition("SHOPID", new DataValue(erpShopNO, Types.VARCHAR));
				up2.addCondition("ORDER_PLUNO", new DataValue(orderPluNO, Types.VARCHAR));
				up2.addCondition("ORDER_SPECNO", new DataValue(orderSpecNO, Types.VARCHAR));

				up2.addUpdateValue("SPECNO", new DataValue(pluBarcode, Types.VARCHAR));
				up2.addUpdateValue("PLUNO", new DataValue(pluNO, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(up2));							
			}

			HelpTools.writelog_fileName("【批量同步商品映射】【回写本地数据】循环开始,  当前平台商品ID/名称："+orderPluNO+"/"+orderPluName, goodsLogFileName);

			if(this.pData.size()>0)
			{
				try 
				{
					HelpTools.writelog_fileName("【批量同步商品映射】【回写本地数据】开始执行Update！  平台类型LoadDocType:"+loadDocType+" 当前门店shopId："+erpShopNO, goodsLogFileName);		
					this.doExecuteDataToDB();
					HelpTools.writelog_fileName("【批量同步商品映射】【回写本地数据】执行Update成功！  平台类型LoadDocType:"+loadDocType+" 当前门店shopId："+erpShopNO, goodsLogFileName);		

				} 
				catch (Exception e) 
				{
					HelpTools.writelog_fileName("【批量同步商品映射】【回写本地数据】执行Update异常："+e.getMessage()+"  平台类型LoadDocType:"+loadDocType+" 当前门店shopId："+erpShopNO, goodsLogFileName);					
				}
				this.pData.clear();
			}



		}




	}



}
