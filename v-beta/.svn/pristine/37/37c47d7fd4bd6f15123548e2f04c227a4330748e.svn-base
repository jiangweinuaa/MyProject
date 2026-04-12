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
import com.dsc.spos.json.cust.req.DCP_OrderPlatformMappingGoodsCreateReq;
import com.dsc.spos.json.cust.res.DCP_OrderPlatformMappingGoodsCreateRes;
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
import com.dsc.spos.waimai.WMMTProductService;
import com.dsc.spos.waimai.jddj.HelpJDDJHttpUtil;
import com.google.gson.reflect.TypeToken;

import eleme.openapi.sdk.api.entity.product.OItemAttribute;
import eleme.openapi.sdk.api.entity.product.OItemSellingTime;
import eleme.openapi.sdk.api.entity.product.OItemTime;
import eleme.openapi.sdk.api.entity.product.OMaterial;
import eleme.openapi.sdk.api.entity.product.OSpec;
import eleme.openapi.sdk.api.enumeration.product.OItemUpdateProperty;
import eleme.openapi.sdk.api.enumeration.product.OItemWeekEnum;

public class DCP_OrderPlatformMappingGoodsCreate extends SPosAdvanceService <DCP_OrderPlatformMappingGoodsCreateReq,DCP_OrderPlatformMappingGoodsCreateRes> {

	@Override
	protected void processDUID(DCP_OrderPlatformMappingGoodsCreateReq req, DCP_OrderPlatformMappingGoodsCreateRes res) throws Exception {

		// TODO Auto-generated method stub
		String eId = req.geteId();		
		String erpShopNO = req.getErpShopNO();
		String orderType = req.getDocType();
		String sql="";
		res.setDatas(new ArrayList<DCP_OrderPlatformMappingGoodsCreateRes.level1Elm>());
		try 
		{
			if(orderType.equals("1"))//饿了么
			{
				try 
				{
					//查询下当前门店的对应的饿了么APPKEY
					Map<String, Object> map = PosPub.getWaimaiAppConfigByShopNO_New(this.dao, eId, erpShopNO, orderType,"");
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
					//从数据库里面取平台资料
					sql=" select a.*,b.specno,b.specname,b.order_specno,b.order_specname,b.price,b.stockqty,b.packagefee,b.isonshelf,b.netweight "
							+ " from OC_mappinggoods a inner join OC_mappinggoods_spec b "
							+ " on a.EID=b.EID and a.load_doctype=b.load_doctype and a.SHOPID=b.SHOPID and a.order_pluno=b.order_pluno "
							+ "  where a.EID='"+eId+"' and a.load_doctype='"+orderType+"' and a.SHOPID='"+erpShopNO+"'  ";
					String[] condCountValues={};
					List<Map<String, Object>> getQData=this.doQueryData(sql,condCountValues);
					Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查询条件
					condition.put("ORDER_PLUNO", true);
					//调用过滤函数
					List<Map<String, Object>> getQHeader=MapDistinct.getMap(getQData, condition);
					String orderPLUNO="";
					String orderCategoryNO="" ;
					for (DCP_OrderPlatformMappingGoodsCreateReq.level1Elm reqOneData : req.getDatas()) 
					{	
						StringBuilder errorMessage = new StringBuilder();
						DCP_OrderPlatformMappingGoodsCreateRes.level1Elm oneLv1 = res.new level1Elm();
						Map<OItemUpdateProperty,Object> properties = new HashMap<OItemUpdateProperty,Object>();
						String req_orderPluNO = reqOneData.getOrderPluNO();
						String req_orderPluName = reqOneData.getOrderPluName();
						//String req_orderCategoryNO = reqOneData.getOrderCategoryNO();
						//String req_orderCategoryName =reqOneData.getOrderCategoryName();
						String req_pluBarcode = reqOneData.getPluBarcode();
						//String req_pluNO = reqOneData.getPluNO();
						//String req_pluName = reqOneData.getPluName();
						//String req_category = reqOneData.getCategory();
						//String req_categoryName = reqOneData.getCategoryName();
				
						for (Map<String, Object> oneData : getQHeader) 
						{	
							orderPLUNO = oneData.get("ORDER_PLUNO").toString();
							String orderPluName =oneData.get("ORDER_PLUNAME").toString();
							orderCategoryNO=oneData.get("ORDER_CATEGORYNO").toString();
							String description = oneData.get("DESCRIPTION").toString();
							String isAllTimeSell =oneData.get("ISALLTIMESELL").toString();
							String beginDate =oneData.get("BEGINDATE").toString();
							String endDate =oneData.get("ENDDATE").toString();
							String sellWeek =oneData.get("SELLWEEK").toString();
							String sellTime =oneData.get("SELLTIME").toString();
							String material1 =oneData.get("MATERIAL1").toString();
							String materialID1 =oneData.get("MATERIALID1").toString();
							String material2 =oneData.get("MATERIAL2").toString();
							String materialID2 =oneData.get("MATERIALID2").toString();
							String material3 =oneData.get("MATERIAL3").toString();
							String materialID3 =oneData.get("MATERIALID3").toString();
							String material4 =oneData.get("MATERIAL4").toString();
							String materialID4 =oneData.get("MATERIALID4").toString();
							String material5 =oneData.get("MATERIAL5").toString();
							String materialID5 =oneData.get("MATERIALID5").toString();
							String material6 =oneData.get("MATERIAL6").toString();
							String materialID6 =oneData.get("MATERIALID6").toString();
							String material7 =oneData.get("MATERIAL7").toString();
							String materialID7 =oneData.get("MATERIALID7").toString();
							String material8 =oneData.get("MATERIAL8").toString();
							String materialID8 =oneData.get("MATERIALID8").toString();
							String material9 =oneData.get("MATERIAL9").toString();
							String materialID9 =oneData.get("MATERIALID9").toString();
							String material10 =oneData.get("MATERIAL10").toString();
							String materialID10 =oneData.get("MATERIALID10").toString();

							if 	(req_orderPluNO.equals(orderPLUNO))  //REQ和数据库匹配
							{
								properties.put(OItemUpdateProperty.name,orderPluName);
								properties.put(OItemUpdateProperty.description,description);
								OItemSellingTime sellingtime = new OItemSellingTime();
								if (isAllTimeSell.equals("N"))
								{
									sellingtime.setBeginDate(beginDate);
									sellingtime.setEndDate(endDate);
									if(sellWeek!=null&&sellWeek.isEmpty()==false)
									{
										List <OItemWeekEnum > weekEnum = new ArrayList<>();							
										for (String week: sellWeek.split(",")){
											if (week.equals("1")) weekEnum.add(OItemWeekEnum.MONDAY);
											else if (week.equals("2")) weekEnum.add(OItemWeekEnum.TUESDAY);
											else if (week.equals("3")) weekEnum.add(OItemWeekEnum.WEDNESDAY);
											else if (week.equals("4")) weekEnum.add(OItemWeekEnum.THURSDAY);
											else if (week.equals("5")) weekEnum.add(OItemWeekEnum.FRIDAY);
											else if (week.equals("6")) weekEnum.add(OItemWeekEnum.SATURDAY);
											else if (week.equals("7")) weekEnum.add(OItemWeekEnum.SUNDAY);		           
										}
										sellingtime.setWeeks(weekEnum);
									}
									if(sellTime!=null&&sellTime.isEmpty()==false)
									{
										List<OItemTime> times = new ArrayList<OItemTime>();
										String[] ssTime = sellTime.split(",");//16:00-19:00,20:00-23:00
										for (String timeStr : ssTime) 
										{
											String[] ss1 = timeStr.split("-");//16:00-19:00						  			
											OItemTime oItemTime = new OItemTime();
											oItemTime.setBeginTime(ss1[0]);
											oItemTime.setEndTime(ss1[1]);
											times.add(oItemTime);	
										}					  		
										sellingtime.setTimes(times);
									}
									properties.put(OItemUpdateProperty.sellingTime, sellingtime);
								}

								List<OMaterial> oMaterials = new ArrayList<>();
								
								if  (!Check.Null(material1)&&!Check.Null(materialID1)) 
								{
									OMaterial omaterial = new OMaterial();
									omaterial.setId(Long.valueOf(materialID1) );
									omaterial.setName(material1);
									oMaterials.add(omaterial);
								}
								if  (!Check.Null(material2)&&!Check.Null(materialID2)) 
								{
									OMaterial omaterial = new OMaterial();
									omaterial.setId(Long.valueOf(materialID2) );
									omaterial.setName(material2);
									oMaterials.add(omaterial);
								}									
								if  (!Check.Null(material3)&&!Check.Null(materialID3)) 
								{
									OMaterial omaterial = new OMaterial();
									omaterial.setId(Long.valueOf(materialID3) );
									omaterial.setName(material3);
									oMaterials.add(omaterial);
								}
								if  (!Check.Null(material4)&&!Check.Null(materialID4)) 
								{
									OMaterial omaterial = new OMaterial();
									omaterial.setId(Long.valueOf(materialID4) );
									omaterial.setName(material4);
									oMaterials.add(omaterial);
								}
								if  (!Check.Null(material5)&&!Check.Null(materialID5)) 
								{
									OMaterial omaterial = new OMaterial();
									omaterial.setId(Long.valueOf(materialID5) );
									omaterial.setName(material5);
									oMaterials.add(omaterial);
								}
								if  (!Check.Null(material6)&&!Check.Null(materialID6)) 
								{
									OMaterial omaterial = new OMaterial();
									omaterial.setId(Long.valueOf(materialID6) );
									omaterial.setName(material6);
									oMaterials.add(omaterial);
								}
								if  (!Check.Null(material7)&&!Check.Null(materialID7)) 
								{
									OMaterial omaterial = new OMaterial();
									omaterial.setId(Long.valueOf(materialID7) );
									omaterial.setName(material7);
									oMaterials.add(omaterial);
								}
								if  (!Check.Null(material8)&&!Check.Null(materialID8)) 
								{
									OMaterial omaterial = new OMaterial();
									omaterial.setId(Long.valueOf(materialID8) );
									omaterial.setName(material8);
									oMaterials.add(omaterial);
								}
								if  (!Check.Null(material9)&&!Check.Null(materialID9)) 
								{
									OMaterial omaterial = new OMaterial();
									omaterial.setId(Long.valueOf(materialID9) );
									omaterial.setName(material9);
									oMaterials.add(omaterial);
								}
								if  (!Check.Null(material10)&&!Check.Null(materialID10)) 
								{
									OMaterial omaterial = new OMaterial();
									omaterial.setId(Long.valueOf(materialID10) );
									omaterial.setName(material10);
									oMaterials.add(omaterial);
								}
								
								if ( oMaterials ==null||oMaterials.isEmpty()||oMaterials.size()==0  )
								{
									 OMaterial omaterial = new OMaterial();
									 omaterial.setId(Long.valueOf("10042") );
									 omaterial.setName("小麦");
									 oMaterials.add(omaterial);
								}
								
								properties.put(OItemUpdateProperty.materials, oMaterials);

								oneLv1.setOrderPluNO(orderPLUNO);
								oneLv1.setOrderPluName(req_orderPluName);
								oneLv1.setPluBarcode(req_pluBarcode );
								break;
							}
						}

						List<OSpec> oSpecs = new ArrayList<OSpec>();
						for (DCP_OrderPlatformMappingGoodsCreateReq.level2Elm specs : reqOneData.getSpecs()) 
						{								
							String reqSpecs_orderSpecID = specs.getOrderSpecID();
							String reqSpecs_orderSpecName = specs.getOrderSpecName();
							String reqSpecs_pluBarcode = specs.getPluBarcode();
							//String reqSpecs_pluName = specs.getPluName();
							String reqSpecs_pluSpecName = specs.getPluSpecName();
							

							OSpec oSpec = new OSpec();
							oSpec.setSpecId(new Long(reqSpecs_orderSpecID));
							oSpec.setExtendCode(reqSpecs_pluBarcode);
							oSpec.setName(reqSpecs_orderSpecName);
							
							oSpec.setMaxStock(10000);
							for (Map<String, Object> oneDatadetail : getQData) 								
							{
								String specId = oneDatadetail.get("ORDER_SPECNO").toString();
								//String extendCode = oneDatadetail.get("SPECNO").toString();//饿了么映射的是商品扩展码extendCode
								//String specName =  oneDatadetail.get("SPECNAME").toString();//饿了么映射的是商品规格名称
								String price =  oneDatadetail.get("PRICE").toString();
								String stockQty =  oneDatadetail.get("STOCKQTY").toString();
								String isonshelf =  oneDatadetail.get("ISONSHELF").toString();
								String netWeight =  oneDatadetail.get("NETWEIGHT").toString();
								String packageFee =  oneDatadetail.get("PACKAGEFEE").toString();
								int onshelf =0 ;
								if (isonshelf.equals("Y")) onshelf=1;			
								if (Check.Null(price )) price="0";
								if (Check.Null(stockQty )) stockQty="9999";
								if (Check.Null(netWeight )) netWeight="0";
								if (Check.Null(packageFee )) packageFee="0";
								if(orderPLUNO.equals(oneDatadetail.get("ORDER_PLUNO"))&&reqSpecs_orderSpecID.equals(specId) )
								{								
									oSpec.setStock(Integer.valueOf(stockQty));
									oSpec.setOnShelf(onshelf);
									oSpec.setStockStatus(1);
									oSpec.setWeight(Integer.valueOf(netWeight));
									oSpec.setPackingFee(Double.valueOf(packageFee));
									oSpec.setPrice(Double.valueOf(price));
									oSpecs.add(oSpec);
									break;
								}
								//continue;
							}
							properties.put(OItemUpdateProperty.specs,oSpecs);		
						}
				
					//属性
						try 
						{
							if (reqOneData.getAttributes() != null && reqOneData.getAttributes().size() > 0)
							{
								List<OItemAttribute> oItemAttributes = new ArrayList<OItemAttribute>();
								for(DCP_OrderPlatformMappingGoodsCreateReq.level2Attribute oneDataAttri : reqOneData.getAttributes())
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

					try 
					{	
						boolean nRet = false;
						if(isGoNewFunction)
						{
							nRet = WMELMProductService.updateItem(elmIsSandbox,elmAPPKey,elmAPPSecret,elmAPPName,new Long(orderPLUNO), new Long(orderCategoryNO), properties, errorMessage);
						}
						else
						{
							nRet = WMELMProductService.updateItem(new Long(orderPLUNO), new Long(orderCategoryNO), properties, errorMessage);
						}

						String result = "N";
						if (nRet) 
						{
							result = "Y";
						}		

						oneLv1.setResult(result);
						oneLv1.setDescription(errorMessage.toString());
						reqOneData.setResult(result);
					} 
					catch (Exception e) 
					{
						oneLv1.setResult("N");
						oneLv1.setDescription(e.getMessage());
						reqOneData.setResult("N");
						continue;		
					}
					res.getDatas().add(oneLv1);
				}

			
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");

		} 
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}

	}
	else if(orderType.equals("2"))//美团聚宝盆
	{
		try 
		{	
			if (StaticInfo.waimaiMTIsJBP != null && StaticInfo.waimaiMTIsJBP.equals("Y"))//聚宝盆
			{
				for (DCP_OrderPlatformMappingGoodsCreateReq.level1Elm oneData : req.getDatas()) 
				{	
					StringBuilder errorMessage = new StringBuilder();
					DCP_OrderPlatformMappingGoodsCreateRes.level1Elm oneLv1 = res.new level1Elm();
					oneLv1.setOrderPluNO(oneData.getOrderPluNO());
					oneLv1.setOrderPluName(oneData.getOrderPluName());
					oneLv1.setPluBarcode(oneData.getPluBarcode());
					try 
					{										
						JSONArray array = new JSONArray();
						JSONObject object = new JSONObject();
						object.put("dishId", new Long(oneData.getOrderPluNO()));
						//object.put("eDishCode", oneData.getPluBarcode());//为什么当初映射的是这个？？？
						object.put("eDishCode", oneData.getPluNO());
						JSONArray array_sku = new JSONArray();
						for (DCP_OrderPlatformMappingGoodsCreateReq.level2Elm oneDate2 : oneData.getDatas()) 
						{
							JSONObject object_sku = new JSONObject();
							object_sku.put("dishSkuId", new Long(oneDate2.getOrderSpecID()));
							object_sku.put("eDishSkuCode", oneDate2.getPluBarcode());
							array_sku.put(object_sku);
						}
						object.put("waiMaiDishSkuMappings", array_sku);
						array.put(object);
						String dishMappings = array.toString();
						boolean nRet = WMJBPProductService.dishMapping(eId, erpShopNO, dishMappings, errorMessage);
						String result = "N";
						if (nRet) 
						{
							result = "Y";
						}		
						oneLv1.setResult(result);
						oneLv1.setDescription(errorMessage.toString());
						oneData.setResult(result);
					} 
					catch (Exception e) 
					{
						oneLv1.setResult("N");
						oneLv1.setDescription(e.getMessage());
						oneData.setResult("N");
						continue;		
					}

					res.getDatas().add(oneLv1);

				}
				
			}			
			else//美团
			{
				Map<String, Object> mappingShopInfo = PosPub.getWaimaiMappingShopByErpShopNo(this.dao, eId, erpShopNO, orderType);
				if(mappingShopInfo==null||mappingShopInfo.isEmpty())
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "没有获取到对应的平台门店信息!");
				}
				
				String orderShopNo = mappingShopInfo.get("ORDERSHOPNO").toString();
				
				
				for (DCP_OrderPlatformMappingGoodsCreateReq.level1Elm oneData : req.getDatas()) 
				{	
					StringBuilder errorMessage = new StringBuilder();
					DCP_OrderPlatformMappingGoodsCreateRes.level1Elm oneLv1 = res.new level1Elm();
					oneLv1.setOrderPluNO(oneData.getOrderPluNO());
					oneLv1.setOrderPluName(oneData.getOrderPluName());
					oneLv1.setPluBarcode(oneData.getPluBarcode());
					String result = "N";
					try 
					{										
						
						for (DCP_OrderPlatformMappingGoodsCreateReq.level2Elm oneDate2 : oneData.getDatas()) 
						{
							boolean nRet = WMMTProductService.updateAppFoodCodeByNameAndSpec(orderShopNo, oneData.getOrderPluName(), oneData.getOrderCategoryNO(), oneDate2.getOrderSpecName(), oneData.getPluNO(), oneDate2.getPluBarcode(), errorMessage);						
							if (nRet) 
							{
								result = "Y";
							}	
						}
						
							
						oneLv1.setResult(result);
						oneLv1.setDescription(errorMessage.toString());
						oneData.setResult(result);
					} 
					catch (Exception e) 
					{
						oneLv1.setResult("N");
						oneLv1.setDescription(e.getMessage());
						oneData.setResult("N");
						continue;		
					}

					res.getDatas().add(oneLv1);

				}
				
			}
			
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");

		} 
		catch (Exception e) 
		{
			// TODO: handle exception			
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}								

	}
	else if(orderType.equals("3"))//京东到家
	{
		try 
		{			
			for (DCP_OrderPlatformMappingGoodsCreateReq.level1Elm oneData : req.getDatas()) 
			{	
				StringBuilder errorMessage = new StringBuilder();
				DCP_OrderPlatformMappingGoodsCreateRes.level1Elm oneLv1 = res.new level1Elm();
				oneLv1.setOrderPluNO(oneData.getOrderPluNO());
				oneLv1.setOrderPluName(oneData.getOrderPluName());
				oneLv1.setPluBarcode(oneData.getPluBarcode());
				try 
				{										
					
					JSONObject body = new JSONObject();
					JSONArray skuInfoListMap = new JSONArray();
					for (DCP_OrderPlatformMappingGoodsCreateReq.level2Elm oneDate2 : oneData.getDatas()) 
					{
											
						JSONObject skuInfoMap_item = new JSONObject();			
						skuInfoMap_item.put("skuId", Long.valueOf(oneDate2.getOrderSpecID()) );      //节点值类型Long
						skuInfoMap_item.put("outSkuId", oneDate2.getPluBarcode()  ); //节点值类型String
						skuInfoListMap.put(skuInfoMap_item);									
						
					}
					body.put("skuInfoList", skuInfoListMap);							
					String skuInfoListJson = body.toString();
					boolean nRet = HelpJDDJHttpUtil.batchUpdateOutSkuId(skuInfoListJson, errorMessage);
					String result = "N";
					if (nRet) 
					{
						result = "Y";
					}		
					oneLv1.setResult(result);
					oneLv1.setDescription(errorMessage.toString());
					oneData.setResult(result);
				} 
				catch (Exception e) 
				{
					oneLv1.setResult("N");
					oneLv1.setDescription(e.getMessage());
					oneData.setResult("N");
					continue;		
				}

				res.getDatas().add(oneLv1);

			}

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");

		} 
		catch (Exception e) 
		{
			// TODO: handle exception			
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}

	}

	else 
	{
		throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"还没做！！！");
	}

} 
catch (Exception e) 
{
	// TODO: handle exception
	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
}

//region 映射成功后，保存到本地
this.updateDBMappingGoods(req);
//endregion
}

@Override
protected List<InsBean> prepareInsertData(DCP_OrderPlatformMappingGoodsCreateReq req) throws Exception {
	// TODO 自动生成的方法存根
	return null;
}

@Override
protected List<UptBean> prepareUpdateData(DCP_OrderPlatformMappingGoodsCreateReq req) throws Exception {
	// TODO 自动生成的方法存根
	return null;
}

@Override
protected List<DelBean> prepareDeleteData(DCP_OrderPlatformMappingGoodsCreateReq req) throws Exception {
	// TODO 自动生成的方法存根
	return null;
}

@Override
protected boolean isVerifyFail(DCP_OrderPlatformMappingGoodsCreateReq req) throws Exception {

	// TODO Auto-generated method stub
	boolean isFail = false;
	StringBuffer errMsg = new StringBuffer("");
	String orderType = req.getDocType();
	if(Check.Null(orderType))
	{
		errMsg.append("平台类型不可为空值, ");
		isFail = true;
	}

	if(Check.Null(req.getErpShopNO()))
	{
		errMsg.append("门店编号不可为空值, ");
		isFail = true;
	}

	if (isFail)
	{
		throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
	}
	for(DCP_OrderPlatformMappingGoodsCreateReq.level1Elm level1Elm : req.getDatas())
	{
		if(Check.Null(level1Elm.getOrderPluNO()))
		{
			errMsg.append("外卖平台商品编号不可为空值, ");
			isFail = true;
		}
		if (orderType.equals("1")) 
		{
			if(Check.Null(level1Elm.getOrderCategoryNO()))
			{
				errMsg.append("饿了么的商品分类编号不可为空值, ");
				isFail = true;
			}

		}  	  	
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		if(level1Elm.getSpecs()!=null&&level1Elm.getSpecs().size()>0)
		{
			level1Elm.setDatas(level1Elm.getSpecs());
		}

		for(DCP_OrderPlatformMappingGoodsCreateReq.level2Elm level2Elm : level1Elm.getDatas())
		{
			if(Check.Null(level2Elm.getOrderSpecID()))
			{
				errMsg.append("外卖平台规格编号不可为空值, ");
				isFail = true;
			}  			  	
			if (isFail)
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}	  		
		}	  	

	}
	if (isFail){
		throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
	}
	return isFail;


}

@Override
protected TypeToken<DCP_OrderPlatformMappingGoodsCreateReq> getRequestType() {
	// TODO 自动生成的方法存根
	return new TypeToken<DCP_OrderPlatformMappingGoodsCreateReq>(){};
}

@Override
protected DCP_OrderPlatformMappingGoodsCreateRes getResponseType() {
	// TODO 自动生成的方法存根
	return new DCP_OrderPlatformMappingGoodsCreateRes() ;
}

private void updateDBMappingGoods(DCP_OrderPlatformMappingGoodsCreateReq req) throws Exception
{
	ArrayList<DataProcessBean> DataPB = new ArrayList<DataProcessBean>();//用来更新数据库
	//这里商品映射要存一下OC_MAPPINGGOODS 和 OC_MAPPINGGOODS_SPEC
	for (DCP_OrderPlatformMappingGoodsCreateReq.level1Elm data1 : req.getDatas()) 
	{
		if (data1.getResult() == null || data1.getResult().equals("Y") == false)
		{
			continue;
		}
		UptBean up1=new UptBean("OC_MAPPINGGOODS");
		up1.addUpdateValue("PLUNO", new DataValue(data1.getPluNO(), Types.VARCHAR));
		up1.addUpdateValue("CATEGORYNO", new DataValue(data1.getCategory(), Types.VARCHAR));
		up1.addUpdateValue("PLUNAME", new DataValue(data1.getPluName(), Types.VARCHAR));

		up1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
		up1.addCondition("SHOPID", new DataValue(req.getErpShopNO(), Types.VARCHAR));
		up1.addCondition("LOAD_DOCTYPE", new DataValue(req.getDocType(), Types.VARCHAR));
		up1.addCondition("ORDER_PLUNO", new DataValue(data1.getOrderPluNO(), Types.VARCHAR));
		DataPB.add(new DataProcessBean(up1));

		if(data1.getSpecs()!=null&&data1.getSpecs().size()>0)
		{
			data1.setDatas(data1.getSpecs());
		}

		for (DCP_OrderPlatformMappingGoodsCreateReq.level2Elm data2 : data1.getSpecs()) 
		{
			UptBean up2=new UptBean("OC_MAPPINGGOODS_SPEC"); 
			up2.addUpdateValue("PLUNO", new DataValue(data1.getPluNO(), Types.VARCHAR));
			up2.addUpdateValue("SPECNO", new DataValue(data2.getPluBarcode(), Types.VARCHAR));
			up2.addUpdateValue("SPECNAME", new DataValue(data2.getPluSpecName(), Types.VARCHAR));

			up2.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
			up2.addCondition("SHOPID", new DataValue(req.getErpShopNO(), Types.VARCHAR));
			up2.addCondition("LOAD_DOCTYPE", new DataValue(req.getDocType(), Types.VARCHAR));
			up2.addCondition("ORDER_PLUNO", new DataValue(data1.getOrderPluNO(), Types.VARCHAR));
			up2.addCondition("ORDER_SPECNO", new DataValue(data2.getOrderSpecID(), Types.VARCHAR));
			DataPB.add(new DataProcessBean(up2));
		}
	}

	//事务update语句
	if (DataPB != null && DataPB.size() > 0) 
	{
		try 
		{
			for (DataProcessBean dataProcessBean : DataPB) 
			{
				this.addProcessData(dataProcessBean);			
			}	
			HelpTools.writelog_waimai("【商品映射后更新之前映射表数据库】开始！");
			this.doExecuteDataToDB();
			HelpTools.writelog_waimai("【商品映射后更新之前映射表数据库】成功！");

		} 
		catch (Exception e) 
		{
			HelpTools.writelog_waimai("【商品映射后更新之前映射表数据库】异常！"+e.getMessage());
		}										

	}



}





}
