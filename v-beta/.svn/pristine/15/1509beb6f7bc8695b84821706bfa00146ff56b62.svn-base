package com.dsc.spos.service.imp.json;

import java.sql.SQLSyntaxErrorException;
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
import com.dsc.spos.dao.ExecBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderPlatformGoodsQueryReq;
import com.dsc.spos.json.cust.res.DCP_OrderPlatformGoodsQueryRes;
import com.dsc.spos.json.cust.res.DCP_OrderPlatformGoodsQueryRes.level1Elm;
import com.dsc.spos.json.cust.res.DCP_OrderPlatformGoodsQueryRes.level2Attribute;
import com.dsc.spos.json.cust.res.DCP_OrderPlatformGoodsQueryRes.level2Spec;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.WMELMProductService;
import com.dsc.spos.waimai.WMJBPProductService;
import com.dsc.spos.waimai.jddj.HelpJDDJHttpUtil;
import com.dsc.spos.waimai.jddj.JDDJProductService;
import com.dsc.spos.waimai.jddj.OPriceInfo;
import com.dsc.spos.waimai.jddj.OQueryStockResponse;
import com.dsc.spos.waimai.jddj.OSkuMain;
import com.dsc.spos.waimai.jddj.OSkuMainResultList;
import com.dsc.spos.waimai.model.WMJBPQueryListByEPoiId;
import com.dsc.spos.waimai.model.WMJBPQueryListByEPoiId.Data;
import com.dsc.spos.waimai.WMMTProductService;
import com.google.gson.reflect.TypeToken;
import com.sankuai.meituan.waimai.opensdk.vo.FoodParam;
import com.sankuai.meituan.waimai.opensdk.vo.FoodSkuParam;

import eleme.openapi.sdk.api.entity.product.OItem;
import eleme.openapi.sdk.api.entity.product.OItemAttribute;
import eleme.openapi.sdk.api.entity.product.OItemSellingTime;
import eleme.openapi.sdk.api.entity.product.OItemTime;
import eleme.openapi.sdk.api.entity.product.OMaterial;
import eleme.openapi.sdk.api.entity.product.OSpec;
import eleme.openapi.sdk.api.enumeration.product.OItemWeekEnum;

public class DCP_OrderPlatformGoodsQuery extends SPosAdvanceService<DCP_OrderPlatformGoodsQueryReq,DCP_OrderPlatformGoodsQueryRes> {

	static String goodsLogFileName = "GoodsSaveLocal";
	@Override
	protected void processDUID(DCP_OrderPlatformGoodsQueryReq req, DCP_OrderPlatformGoodsQueryRes res) throws Exception {
		// TODO Auto-generated method stub
		String isOnline = req.getIsOnline();
		if(isOnline.equals("N"))//取本地
		{	
			this.GetShopFromDB(req,res);
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功！");
		}
		else
		{
			this.GetOnlineGoods(req);
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功！");

		}



	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderPlatformGoodsQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderPlatformGoodsQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderPlatformGoodsQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderPlatformGoodsQueryReq req) throws Exception {
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

		if(Check.Null(req.getIsOnline()))
		{
			errCt++;
			errMsg.append("请求类型IsOnline不可为空值, ");
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
	protected TypeToken<DCP_OrderPlatformGoodsQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderPlatformGoodsQueryReq>(){};
	}

	@Override
	protected DCP_OrderPlatformGoodsQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderPlatformGoodsQueryRes();
	}


	private DCP_OrderPlatformGoodsQueryRes GetOnlineGoods(DCP_OrderPlatformGoodsQueryReq req) throws Exception
	{
		String loadDocType = req.getLoadDocType();
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
		
		String sql = null;	
		String[] conditionValues = {eId}; 				
		sql = "select b.pluno,b.pluname,a.specno,a.specname,b.filename,a.netweight,b.categoryno,b.belfirm from OC_goods_spec a "
				+ " left join OC_goods b on a.EID=b.EID and a.pluno=b.pluno and a.belfirm=b.belfirm"
				+ " where a.EID= ? " ;	
		
		if (belFirm != null && belFirm.trim().length() > 0)
		{
			sql +=" and a.belfirm='"+belFirm+"'";
		}

		
		HelpTools.writelog_fileName("【同步商品资料到本地】 获取菜品池SQL:"+sql,  goodsLogFileName);
		List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);
		List<Map<String, Object>> mappingShops = this.GetMappingShops(req);

		if (mappingShops == null || mappingShops.isEmpty())
		{
			return null;
		}
		DCP_OrderPlatformGoodsQueryRes baseRes = new DCP_OrderPlatformGoodsQueryRes();
		if(loadDocType.equals("1")||loadDocType.equals("2"))
		{

		}
		else//京东到家的 商品信息是全局的，但是价格和库存是到门店的，
		{
			baseRes.setDatas(new ArrayList<DCP_OrderPlatformGoodsQueryRes.level1Elm>());
			StringBuilder errorMessage = new StringBuilder();
			int pagaSize = 20;//分页大小
			int pageCur = 1;//当前页码
			HelpTools.writelog_fileName("【同步商品资料到本地】分页获取【京东到家】商品资料开始！ 平台类型LoadDocType:"+loadDocType+" 当前分页："+pageCur+" 每页大小:"+pagaSize, goodsLogFileName);
			OSkuMainResultList jddjSkus = HelpJDDJHttpUtil.querySkuInfos(pageCur, pagaSize, "", errorMessage);
			if(jddjSkus==null||jddjSkus.getSkuMains()==null||jddjSkus.getSkuMains().size()==0)
			{
				HelpTools.writelog_fileName("【同步商品资料到本地】获取【京东到家】商品资料结束 没有商品资料！ 平台类型LoadDocType:"+loadDocType+" 当前分页："+pageCur+" 每页大小:"+pagaSize, goodsLogFileName);
				return null;
			}
			List<OSkuMain>  products =	jddjSkus.getSkuMains();
			pageCur += 1;
			int totalCount = jddjSkus.getCount();
			int pageCount = totalCount/pagaSize +1;

			for (int i = 2; i <= pageCount; i++)
			{
				HelpTools.writelog_fileName("【同步商品资料到本地】分页获取【京东到家】商品资料开始！ 平台类型LoadDocType:"+loadDocType+" 当前分页："+i+" 每页大小:"+pagaSize, goodsLogFileName);
				OSkuMainResultList jddjSkusResulit = HelpJDDJHttpUtil.querySkuInfos(i, pagaSize, "", errorMessage);
				if(jddjSkusResulit==null||jddjSkusResulit.getSkuMains()==null||jddjSkusResulit.getSkuMains().size()==0)
				{
					HelpTools.writelog_fileName("【同步商品资料到本地】获取【京东到家】商品资料结束 没有商品资料！ 平台类型LoadDocType:"+loadDocType+" 当前分页："+i+" 每页大小:"+pagaSize, goodsLogFileName);
					continue;
				}
				//products.addAll(jddjSkusResulit.getSkuMains());
				for (OSkuMain oSkuMain : jddjSkusResulit.getSkuMains()) 
				{
					products.add(oSkuMain);			
				}

			}

			for (OSkuMain oSkuMain : products) 
			{
				DCP_OrderPlatformGoodsQueryRes.level1Elm oneLv1_jddj = baseRes.new level1Elm();
				oneLv1_jddj.setSpecs(new ArrayList<DCP_OrderPlatformGoodsQueryRes.level2Spec>());
				String orderPluNO = String.valueOf(oSkuMain.getSkuId());//商品Id
				String orderPluName = oSkuMain.getSkuName();//商品名
				String orderCategoryNO = "";
				int skuPrice =oSkuMain.getSkuPrice();//到分
				double orderPrice = (double)skuPrice/100;


				try 
				{
					orderCategoryNO = String.valueOf(oSkuMain.getShopCategories().get(0));//到家分类ID			
				} 
				catch (Exception e) 
				{

				}

				String orderDescription = oSkuMain.getSlogan() ;
				String orderUnit = "";
				String orderImageUrl = "";
				String plubarcode = oSkuMain.getOutSkuId();
				int onShelf =	oSkuMain.getFixedStatus();//商家商品上下架状态(1:上架;2:下架;4:删除;)
				String IsOnShelf = "Y";
				if (onShelf != 1)
				{
					IsOnShelf = "N";
				}

				oneLv1_jddj.setOrderPluNO(orderPluNO);
				oneLv1_jddj.setOrderPluName(orderPluName);
				oneLv1_jddj.setOrderCategoryNO(orderCategoryNO);
				oneLv1_jddj.setOrderCategoryName("");//没有返回分类名称
				oneLv1_jddj.setOrderDescription(orderDescription);
				oneLv1_jddj.setOrderImageUrl(orderImageUrl);
				oneLv1_jddj.setOrderUnit(orderUnit);
				oneLv1_jddj.setPluNO(plubarcode);


				DCP_OrderPlatformGoodsQueryRes.level2Spec oneLv2_jddj = baseRes.new level2Spec();

				oneLv2_jddj.setOrderSpecID(orderPluNO);
				oneLv2_jddj.setOrderSpecName("");
				oneLv2_jddj.setOrderPrice(String.valueOf(orderPrice));//后面修改取每个门店的价格
				oneLv2_jddj.setOrderStock("9999");//
				oneLv2_jddj.setOrderPackingFee("0");
				oneLv2_jddj.setPluBarcode(plubarcode);
				oneLv2_jddj.setPluSpecName("");
				oneLv2_jddj.setOrderOnShelf(IsOnShelf);

				oneLv1_jddj.getSpecs().add(oneLv2_jddj);

				baseRes.getDatas().add(oneLv1_jddj);

			}

		}

		//region 循环获取门店下面内容
		for (Map<String, Object> mapShop : mappingShops) 
		{
			DCP_OrderPlatformGoodsQueryRes res = new DCP_OrderPlatformGoodsQueryRes();
			res.setDatas(new ArrayList<DCP_OrderPlatformGoodsQueryRes.level1Elm>());
			String erpShopNO = mapShop.get("SHOPID").toString();
			String orderShopNO = mapShop.get("ORDERSHOPNO").toString();	
			String orderShopName = mapShop.get("ORDERSHOPNAME").toString();	
			HelpTools.writelog_fileName("【同步商品资料到本地】获取当前门店商品资料开始！ 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+erpShopNO, goodsLogFileName);
			try 
			{

				//三张表先删后插
				String execsql1 = "delete from OC_MAPPINGGOODS where EID='"+eId+"' and LOAD_DOCTYPE='"+loadDocType+"' and SHOPID='"+erpShopNO+"'";
				String execsql2 = "delete from OC_MAPPINGGOODS_SPEC where EID='"+eId+"' and LOAD_DOCTYPE='"+loadDocType+"' and SHOPID='"+erpShopNO+"'";
				String execsql3 = "delete from OC_MAPPINGGOODS_ATTR where EID='"+eId+"' and LOAD_DOCTYPE='"+loadDocType+"' and SHOPID='"+erpShopNO+"'";
				ExecBean exc1 = new ExecBean(execsql1);
				ExecBean exc2 = new ExecBean(execsql2);
				ExecBean exc3 = new ExecBean(execsql3);
				this.addProcessData(new DataProcessBean(exc1));
				this.addProcessData(new DataProcessBean(exc2));
				this.addProcessData(new DataProcessBean(exc3));

				if(loadDocType.equals("1"))//饿了么
				{
					long shopId =	Long.parseLong(orderShopNO);
					long pageIndex = 0;
					long pageSize = 200;//
					StringBuilder errorMessage = new StringBuilder("");
					boolean isExist = false;//商品个数是否大于pageSize
					Map<String, Object> map = PosPub.getWaimaiAppConfigByShopNO_New(this.dao, eId, erpShopNO, loadDocType,"");
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


							/*List<OItemAttribute> oAttributes = oItem.getAttributes();//商品属性暂时不做
							if (oAttributes != null && oAttributes.size() > 0)
							{
								for (OItemAttribute oItemAttribute : oAttributes) 
								{
									OrderPlatformGoodsGetRes.level2Attribute orderAttribute = new OrderPlatformGoodsGetRes.level2Attribute();
									String name = oItemAttribute.getName();
									orderAttribute.setName(name);
									oneLv1.getAttributes().add(orderAttribute);

				        }
							}*/

							res.getDatas().add(oneLv1);		

						} 
						catch (Exception e) 
						{		    	
							continue;		
						}
					}
				}
				else if(loadDocType.equals("2"))//美团聚宝盆
				{
					if(StaticInfo.waimaiMTIsJBP.equals("Y"))//聚宝盆
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

						}
						
					}
					else
					{
						StringBuilder errorMessage = new StringBuilder("");
						List<FoodParam> products = WMMTProductService.queryListByEPoiId(orderShopNO, errorMessage);
						if(products == null ||products.isEmpty())
						{					
							HelpTools.writelog_fileName("【同步商品资料到本地】获取该门店商品资料完成！该门店没有商品资料！ 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+erpShopNO, goodsLogFileName);
							continue;					
						}
						HelpTools.writelog_fileName("【同步商品资料到本地】【MT查询门店菜品列表】获取该门店商品资料完成！ 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+erpShopNO+" 获取的详细商品资料总个数："+products.size(), goodsLogFileName);	
						HelpTools.writelog_fileName("【同步商品资料到本地】【MT查询门店菜品列表】解析该门店商品资料并保存到本地开始！ 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+erpShopNO+" 获取的商品资料总个数："+products.size(), goodsLogFileName);
						for (FoodParam oItem : products) 
						{
							try 
							{
								DCP_OrderPlatformGoodsQueryRes.level1Elm oneLv1 = res.new level1Elm();
								oneLv1.setSpecs(new ArrayList<DCP_OrderPlatformGoodsQueryRes.level2Spec>());
								oneLv1.setAttributes(new ArrayList<DCP_OrderPlatformGoodsQueryRes.level2Attribute>());
								String orderPluNO = oItem.getApp_food_code();//String.valueOf(oItem.getFood_id());//菜品id
								String PluNO = oItem.getApp_food_code();//APP方菜品id
								String orderPluName = oItem.getName();//商品名
								String orderCategoryNO = oItem.getCategory_name();//菜品分类名
								String orderDescription = oItem.getDescription();
								String orderUnit = oItem.getUnit();
								String orderImageUrl = oItem.getPicture();
								int onShelf = oItem.getIs_sold_out();//0-上架；1-下架	
								String orderOnShelf = "Y";
								if (onShelf == 1)
								{
									orderOnShelf = "N";
								}
								//美团没有返回商品在平台的ID，只返回了第三方的ID，如果没有通过接口或者工具去绑定，那么值为空,所以默认给商品名称
								//if(orderPluNO==null||orderPluNO.trim().isEmpty())
								{
									orderPluNO = orderPluName+"[分类]"+orderCategoryNO;//不同的分类下面存在相同的商品名称
								}
								
								oneLv1.setOrderPluNO(orderPluNO);
								oneLv1.setOrderPluName(orderPluName);
								oneLv1.setOrderCategoryNO(orderCategoryNO);
								oneLv1.setOrderCategoryName(orderCategoryNO);//MT分类编号=分类名称
								oneLv1.setOrderDescription(orderDescription);
								oneLv1.setOrderImageUrl(orderImageUrl);
								oneLv1.setOrderUnit(orderUnit);
								oneLv1.setPluNO(PluNO);

			
								List<FoodSkuParam> oSpecs = oItem.getSkus();
								if (oSpecs != null && oSpecs.size() > 0)
								{
									for (FoodSkuParam oSpec : oSpecs) 
									{	
										DCP_OrderPlatformGoodsQueryRes.level2Spec orderSpec = res.new level2Spec();
										String orderSpecID = oSpec.getSku_id();//规格Id
										String orderSpecpluBarcode = oSpec.getSku_id();//商品扩展码 (ERP的商品条码)真正映射的商品条码
										String orderSpecName = oSpec.getSpec();
										String orderPrice = String.valueOf(oSpec.getPrice());
										String orderStock = String.valueOf(oSpec.getStock());
										
									 //美团没有返回商品在平台的SKUID，只返回了第三方的SKUID，如果没有通过接口或者工具去绑定，那么值为空,所以默认给规格名称
										//if(orderSpecID==null||orderSpecID.trim().isEmpty())
										{
											orderSpecID = orderSpecName;
										}
										
										float boxPrice = 0;
										float boxNum = 0;
										try {
											boxPrice = Float.parseFloat(oSpec.getBox_price());
										} catch (Exception e) {
											boxPrice = 0;
										}
										try {
											boxNum = Float.parseFloat(oSpec.getBox_num());
										} catch (Exception e) {
											boxNum = 0;
										}
										
										float orderPackingFee = boxPrice * boxNum;
										
										
										float  netWeight = oSpec.getWeight()==null?0: oSpec.getWeight(); 
										
				
										orderSpec.setOrderSpecID(orderSpecID);
										orderSpec.setOrderSpecName(orderSpecName);
										orderSpec.setOrderPrice(orderPrice);
										orderSpec.setOrderStock(orderStock);
										orderSpec.setOrderPackingFee(String.valueOf(orderPackingFee));
										orderSpec.setOrderOnShelf(orderOnShelf);
										orderSpec.setPluBarcode(orderSpecpluBarcode);
										orderSpec.setPluSpecName("");
										orderSpec.setNetWeight(netWeight);

										oneLv1.getSpecs().add(orderSpec);																						
									}				

								}

								//属性
								/*List<OItemAttribute> oAttributes = oItem.getAttributes();
								if (oAttributes != null && oAttributes.size() > 0)
								{
									for (OItemAttribute oItemAttribute : oAttributes) 
									{
										OrderPlatformGoodsGetRes.level2Attribute orderAttribute = new OrderPlatformGoodsGetRes.level2Attribute();
										String attributeName = oItemAttribute.getName();
										List<String> attributeDetails = oItemAttribute.getDetails();
										orderAttribute.setName(attributeName);
										orderAttribute.setDetails(attributeDetails);
										oneLv1.getAttributes().add(orderAttribute);
														
					        }
									
								}*/
								
								//新增时间段
							
								res.getDatas().add(oneLv1);		

							} 
							catch (Exception e) 
							{		    	
								continue;		
							}
						}
					}
					
				}
				else //京东到家
				{					
					//res = baseRes;//这里最好需要 深拷贝	
					ArrayList<Long> skuIds = new ArrayList<Long>();//用来存储京东到家的skuid，后面调用价格接口
					for (DCP_OrderPlatformGoodsQueryRes.level1Elm oneData : baseRes.getDatas()) 
					{
						DCP_OrderPlatformGoodsQueryRes.level1Elm oneLv1 = res.new level1Elm();
						oneLv1.setSpecs(new ArrayList<DCP_OrderPlatformGoodsQueryRes.level2Spec>());
						oneLv1.setAttributes(new ArrayList<DCP_OrderPlatformGoodsQueryRes.level2Attribute>());
						String orderPluNO = oneData.getOrderPluNO();
						try 
						{
							skuIds.add(Long.parseLong(orderPluNO));//所有ID
						} 
						catch (Exception e) 
						{

						}

						oneLv1.setOrderCategoryName(oneData.getOrderCategoryName());
						oneLv1.setOrderCategoryNO(oneData.getOrderCategoryNO());
						oneLv1.setOrderDescription(oneData.getOrderDescription());
						oneLv1.setOrderImageUrl(oneData.getOrderImageUrl());
						oneLv1.setOrderPluName(oneData.getOrderPluName());
						oneLv1.setOrderPluNO(oneData.getOrderPluNO());
						oneLv1.setOrderPriority(oneData.getOrderPriority());
						oneLv1.setOrderUnit(oneData.getOrderUnit());
						oneLv1.setPluName(oneData.getPluName());

						DCP_OrderPlatformGoodsQueryRes.level2Spec oneLv1_spec = res.new level2Spec();
						DCP_OrderPlatformGoodsQueryRes.level2Spec oneData_spec = oneData.getSpecs().get(0);
						oneLv1_spec.setOrderSpecID(oneData_spec.getOrderSpecID());
						oneLv1_spec.setOrderSpecName(oneData_spec.getOrderSpecName());
						oneLv1_spec.setOrderPrice(oneData_spec.getOrderPrice());//后面修改
						oneLv1_spec.setOrderStock(oneData_spec.getOrderStock());//
						oneLv1_spec.setOrderPackingFee(oneData_spec.getOrderPackingFee());
						oneLv1_spec.setPluBarcode(oneData_spec.getPluBarcode());
						oneLv1_spec.setPluSpecName(oneData_spec.getPluSpecName());
						oneLv1_spec.setOrderOnShelf(oneData_spec.getOrderOnShelf());

						oneLv1.getSpecs().add(oneLv1_spec);

						res.getDatas().add(oneLv1);

					}
					//开始调用接口更新
					if (skuIds != null || skuIds.isEmpty() == false)
					{
						//接口最多 50个
						int totalCount =skuIds.size();//总skuid数
						int totalPage = totalCount/50+1;//调用接口次数
						HelpTools.writelog_fileName("【同步商品资料到本地】获取JDDJ商品价格开始！ 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+erpShopNO+" 获取的商品价格总个数："+totalCount+" 需要调用JDDJ获取价格接口总次数："+totalPage, goodsLogFileName);
						for (int i = 0; i < totalPage; i++)
						{
							ArrayList<Long> reqSkuIds = new ArrayList<Long>();
							int j_start = i*50;//0
							int j_end = j_start +50;//50
							if (j_end > totalCount)
							{
								j_end = totalCount;
							}
							for (int j = j_start; j < j_end; j++)
							{
								reqSkuIds.add(skuIds.get(j));
							}
							HelpTools.writelog_fileName("【同步商品资料到本地】获取JDDJ商品价格开始！当前调用获取价格接口第【"+i+1+"】次，  获取的商品价格总个数："+totalCount+" 需要调用JDDJ获取价格接口总次数："+totalPage, goodsLogFileName);
							StringBuilder errorMessage = new StringBuilder();
							List<OPriceInfo> skuIdsPrice =	HelpJDDJHttpUtil.getStationInfoList(orderShopNO, reqSkuIds, errorMessage);
							HelpTools.writelog_fileName("【同步商品资料到本地】获取JDDJ商品价格开始！当前调用获取价格接口第【"+i+1+"】次，  获取的商品价格总个数："+totalCount+" 需要调用JDDJ获取价格接口总次数："+totalPage+" 返回结果："+errorMessage.toString(), goodsLogFileName);
							//回写之前的价格
							if (skuIdsPrice != null && skuIdsPrice.size() > 0)
							{
								for (OPriceInfo oPriceInfo : skuIdsPrice) 
								{
									double skuId_price = (double) oPriceInfo.getPrice()/100;//平台返回的 单位到分
									String skuId_str = String.valueOf(oPriceInfo.getSkuId());
									for (DCP_OrderPlatformGoodsQueryRes.level1Elm oneData : res.getDatas()) 
									{
										String skuName = oneData.getOrderPluName();
										DCP_OrderPlatformGoodsQueryRes.level2Spec oneData_spec =oneData.getSpecs().get(0);
										if(oneData_spec.getOrderSpecID().equals(skuId_str))
										{
											oneData_spec.setOrderPrice(String.valueOf(skuId_price));
											HelpTools.writelog_fileName("【同步商品资料到本地】获取价格成功后，回写 平台商品ID/名称："+skuId_str+"/"+skuName+" 价格price："+skuId_price, goodsLogFileName);
											break;
										}
									}					
								}
							}	

							//回写可售以及库存
							HelpTools.writelog_fileName("【同步商品资料到本地】获取JDDJ商品库存开始！当前调用获取库存接口第【"+i+1+"】次，  获取的商品库存总个数："+totalCount+" 需要调用JDDJ获取库存接口总次数："+totalPage, goodsLogFileName);
							StringBuilder errorMessage2 = new StringBuilder();
							List<OQueryStockResponse> skuIdsStock =	HelpJDDJHttpUtil.queryOpenUseable(orderShopNO, reqSkuIds, errorMessage2);
							HelpTools.writelog_fileName("【同步商品资料到本地】获取JDDJ商品库存开始！当前调用获取库存接口第【"+i+1+"】次，  获取的商品库存总个数："+totalCount+" 需要调用JDDJ获取库存接口总次数："+totalPage+" 返回结果："+errorMessage.toString(), goodsLogFileName);
							//回写之前的价格
							if (skuIdsStock != null && skuIdsStock.size() > 0)
							{
								for (OQueryStockResponse oStockInfo : skuIdsStock) 
								{
									int UsableQty = oStockInfo.getUsableQty();//可用库存数量：客户可以购买的数量=现货库存-预占库存-锁定库存。
									int vendibility = oStockInfo.getVendibility();//可售状态(0:可售 1:不可售)
									String isOnShelf ="N";//上下架
									if (vendibility == 0 && UsableQty > 0)//可售且库存大于0
									{
										isOnShelf = "Y";
									}

									String skuId_str = String.valueOf(oStockInfo.getSkuId());
									for (DCP_OrderPlatformGoodsQueryRes.level1Elm oneData : res.getDatas()) 
									{
										String skuName = oneData.getOrderPluName();
										DCP_OrderPlatformGoodsQueryRes.level2Spec oneData_spec =oneData.getSpecs().get(0);
										if(oneData_spec.getOrderSpecID().equals(skuId_str))
										{
											oneData_spec.setOrderOnShelf(isOnShelf);
											oneData_spec.setOrderStock(String.valueOf(UsableQty));
											HelpTools.writelog_fileName("【同步商品资料到本地】获取库存成功后，回写 平台商品ID/名称："+skuId_str+"/"+skuName+" 库存："+UsableQty+" 可售状态(0:可售 1:不可售)："+vendibility, goodsLogFileName);
											break;
										}
									}					
								}
							}	


						}
					}
				}
			} 
			catch (Exception e) 
			{
				HelpTools.writelog_fileName("【同步商品资料到本地】循环门店开始！异常:"+e.getMessage()+" 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+erpShopNO, goodsLogFileName);
				continue;
			}

			//region 开始保存数据库
			this.SaveOnlineGoods(req, res, erpShopNO, orderShopNO,orderShopName,getQData );
			//endregion
		}
		//endregion
		return null;

	}



	private void SaveOnlineGoods(DCP_OrderPlatformGoodsQueryReq req,DCP_OrderPlatformGoodsQueryRes res,String erpShopNO,String orderShopNO,String orderShopName,List<Map<String, Object>> getQData) throws Exception
	{
		String eId = req.geteId();
		String loadDocType = req.getLoadDocType();
		List<DCP_OrderPlatformGoodsQueryRes.level1Elm> goods = res.getDatas();
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

					for (Map<String,Object> map : getQData )
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

					if(orderSpecID.length()==0)
					{
						orderSpecID = " ";
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
					this.addProcessData(new DataProcessBean(ib2));	
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
						this.addProcessData(new DataProcessBean(ib2));	
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
				this.addProcessData(new DataProcessBean(ib1));


				/*try 
				{
					HelpTools.writelog_fileName("【同步商品资料到本地】开始执行SQL语句！ 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+erpShopNO+" 获取的商品资料总个数："+goods.size(), goodsLogFileName);  
					this.doExecuteDataToDB();
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
				this.pData.clear();*/

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
			this.doExecuteDataToDB();
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
		this.pData.clear();

	}


	private List<Map<String, Object>> GetMappingShops(DCP_OrderPlatformGoodsQueryReq req) throws Exception
	{
		try 
		{
			String loadDocType = req.getLoadDocType();
			String[] erpShopNO = req.getErpShopNO();
			String erpShopNOStr = this.getString(erpShopNO);
			if(erpShopNOStr==null||erpShopNOStr.isEmpty())
			{
				return null;
			}

			String sql = " select * from OC_MAPPINGSHOP where LOAD_DOCTYPE='"+loadDocType+"'";		
			sql += " and SHOPID in ("+erpShopNOStr+")";

			List<Map<String, Object>> mappingShops = this.doQueryData(sql, null);
			if (mappingShops != null && mappingShops.isEmpty() == false)
			{
				return mappingShops;
			}

		} 
		catch (Exception e) 
		{

		}
		return null;			
	}

	/**
	 * 
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	private void GetShopFromDB(DCP_OrderPlatformGoodsQueryReq req, DCP_OrderPlatformGoodsQueryRes res) throws Exception
	{
		String loadDocType = req.getLoadDocType();
		String erpShopNO = req.getErpShopNO()[0];
		String eId = req.geteId();
		//这里加一段官网平台调用商品的逻辑
		if(loadDocType.equals("4"))
		{
			//从味多美官网上查询信息，然后保存到数据库,直接从这里取
			String method="";
			method="salesDeliver";
			JSONObject reqJsonObject=new JSONObject();
			reqJsonObject.put("cmd", "wdmwaimai_get_md_menu");
			reqJsonObject.put("channel", "mall");
			reqJsonObject.put("erp_code", req.getShopId());
			//reqJsonObject.put("erp_code", "BJZ0085");
			reqJsonObject.put("msg", "");
			reqJsonObject.put("opno", "");
			String resbody=HttpSend.SendWuXiang(method, reqJsonObject.toString(), "http://www.wdmcake.cn/api/erp-wdmwaimai_get_md_menu.html");
			JSONObject resJsonObject=new JSONObject(resbody);
			String code= resJsonObject.getString("code");
			String message= resJsonObject.getString("msg");
			String memoStr = message;
			if(code.equals("0"))
			{
				//需要插入数据库OC_mappinggoods  OC_mappinggoods_spec
				//先删 在插入
				String execsql1 = "delete from OC_MAPPINGGOODS where EID='"+eId+"' and LOAD_DOCTYPE='"+loadDocType+"' and SHOPID='"+erpShopNO+"'";
				String execsql2 = "delete from OC_MAPPINGGOODS_SPEC where EID='"+eId+"' and LOAD_DOCTYPE='"+loadDocType+"' and SHOPID='"+erpShopNO+"'";
				String execsql3 = "delete from OC_MAPPINGGOODS_ATTR where EID='"+eId+"' and LOAD_DOCTYPE='"+loadDocType+"' and SHOPID='"+erpShopNO+"'";
				ExecBean exc=new ExecBean(execsql1); 
				this.pData.add(new DataProcessBean(exc));
				exc=new ExecBean(execsql2); 
				this.pData.add(new DataProcessBean(exc));
				exc=new ExecBean(execsql3); 
				this.pData.add(new DataProcessBean(exc));
				
				for (int i = 0; i < resJsonObject.getJSONArray("categories").length(); i++) 
				{
					JSONObject map=resJsonObject.getJSONArray("categories").getJSONObject(i);
					
					if(map.has("dishes")==false)
					{
						continue;
					}
					for (int j = 0; j < map.getJSONArray("dishes").length(); j++) 
					{
						JSONObject mapdetail=map.getJSONArray("dishes").getJSONObject(j);
						String pluNO="";
						try
						{
							pluNO=mapdetail.getString("sn");
						}
						catch(Exception ex)
						{
							continue;
						}
						
						String pluSpecName=mapdetail.getString("name");
						String orderShopNO=req.getShopId();
						String orderShopName=req.getShopName();
						String orderPrice=mapdetail.getString("price");
						String status=mapdetail.getInt("status")+"";
						if(status.equals("1"))
						{
							status="Y";
						}
						else
						{
							status="N";
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
								new DataValue(pluNO, Types.VARCHAR),//ERP条码
								new DataValue(pluSpecName, Types.VARCHAR),//规格名称
								new DataValue(orderShopNO, Types.VARCHAR),//外卖平台门店ID
								new DataValue(orderShopName, Types.VARCHAR),//外卖平台门店名称
								new DataValue(pluNO, Types.VARCHAR),//外卖平台商品ID
								new DataValue(pluNO, Types.VARCHAR),//外卖平台商品名称 
								new DataValue(pluSpecName, Types.VARCHAR),//
								new DataValue(orderPrice, Types.VARCHAR),//	
								new DataValue("9999", Types.VARCHAR),//
								new DataValue("0", Types.VARCHAR),//
								new DataValue(status, Types.VARCHAR),//					
								new DataValue("100", Types.VARCHAR),
								new DataValue(1, Types.DOUBLE)
						};

						InsBean ib2 = new InsBean("OC_MAPPINGGOODS_SPEC", columns2);
						ib2.addValues(insValue2);
						this.addProcessData(new DataProcessBean(ib2));	
						
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
								new DataValue(pluSpecName, Types.VARCHAR),//ERP商品名称
								new DataValue(" ", Types.VARCHAR),//ERP商品分类编码
								new DataValue(orderShopNO, Types.VARCHAR),//外卖平台门店ID
								new DataValue(orderShopName, Types.VARCHAR),//外卖平台门店名称
								new DataValue(pluNO, Types.VARCHAR),//外卖平台商品ID
								new DataValue(pluSpecName, Types.VARCHAR),//外卖平台商品名称 
								new DataValue(" ", Types.VARCHAR),//
								new DataValue("", Types.VARCHAR),//
								new DataValue("", Types.VARCHAR),//	
								new DataValue("", Types.VARCHAR),//					
								new DataValue("", Types.VARCHAR),//
								new DataValue("0", Types.VARCHAR),//	
								new DataValue("100", Types.VARCHAR),//	
								new DataValue("", Types.VARCHAR),
								new DataValue("", Types.VARCHAR),
								new DataValue("", Types.VARCHAR),
								new DataValue("", Types.VARCHAR),
								new DataValue("", Types.VARCHAR),
								new DataValue("", Types.VARCHAR),
								new DataValue("", Types.VARCHAR),
								new DataValue("", Types.VARCHAR),
								new DataValue("", Types.VARCHAR),
								new DataValue("", Types.VARCHAR),
								new DataValue("", Types.VARCHAR),
								new DataValue("", Types.VARCHAR),
								new DataValue("", Types.VARCHAR),
								new DataValue("", Types.VARCHAR),
								new DataValue("", Types.VARCHAR),
								new DataValue("", Types.VARCHAR),
								new DataValue("", Types.VARCHAR),
								new DataValue("", Types.VARCHAR),
								new DataValue("", Types.VARCHAR),
								new DataValue("", Types.VARCHAR),						
								new DataValue("", Types.VARCHAR),
								new DataValue("", Types.VARCHAR),
								new DataValue("", Types.VARCHAR),
								new DataValue("", Types.VARCHAR),
								new DataValue("", Types.VARCHAR)						
						};

						InsBean ib1 = new InsBean("OC_MAPPINGGOODS", columns1);
						ib1.addValues(insValue1);
						this.addProcessData(new DataProcessBean(ib1));
						
						
					}
					this.doExecuteDataToDB();
					
				  }
				}			
		}
		
		res.setDatas(new ArrayList<DCP_OrderPlatformGoodsQueryRes.level1Elm>());
		String sql =" select * from (";
		sql += " select A.*,B.SPECNO,B.Specname,B.ORDER_SPECNO,B.ORDER_SPECNAME,B.Price,B.STOCKQTY,B.PACKAGEFEE,B.ISONSHELF,C.CATEGORYNO as CATEGORYNO2, C.CATEGORYNAME as CATEGORYNAME2,D.ATTRNAME,D.ATTRVALUE,D.ORDER_ATTRNAME,D.ORDER_ATTRVALUE from OC_mappinggoods A inner join OC_mappinggoods_spec B on A.EID=B.EID and A.SHOPID=B.SHOPID and A.Load_Doctype=B.LOAD_DOCTYPE and A.Order_Pluno=B.Order_Pluno";
		sql += " left join OC_mappingcategory C ON A.EID=C.EID and A.SHOPID=C.SHOPID  and A.ORDER_CATEGORYNO=C.ORDER_CATEGORYNO";
		sql += " left join OC_mappinggoods_attr D on A.EID=D.EID and A.SHOPID=D.SHOPID and A.Load_Doctype=D.LOAD_DOCTYPE and A.Order_Pluno=D.Order_Pluno";
		sql += " where A.EID='"+eId+"' and A.LOAD_DOCTYPE='"+loadDocType+"' and A.SHOPID='"+erpShopNO+"' order by A.PLUNO,A.order_PLUNO";
		sql +=")"; 

		List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);
		if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
		{
			//单头主键字段
			Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
			condition.put("ORDER_PLUNO", true);	
			//调用过滤函数
			List<Map<String, Object>> getQHeader=MapDistinct.getMap(getQDataDetail, condition);
			condition.put("ORDER_SPECNO", true);
			List<Map<String, Object>> getSpecDetail=MapDistinct.getMap(getQDataDetail, condition);
			
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
				String pluNO =  map.get("PLUNO").toString();
				String pluName =  map.get("PLUNAME").toString();
				String categoryNO = map.get("CATEGORYNO2").toString();
				String categoryName = map.get("CATEGORYNAME2").toString();

				oneLv1.setOrderPluNO(orderPluNO);
				oneLv1.setOrderPluName(orderPluName);
				oneLv1.setOrderCategoryNO(orderCategoryNO);
				oneLv1.setOrderCategoryName("");//没有返回分类名称
				oneLv1.setOrderDescription(orderDescription);
				oneLv1.setOrderImageUrl(orderImageUrl);
				oneLv1.setOrderUnit(orderUnit);
				oneLv1.setOrderPriority(orderPriority);
				oneLv1.setPluNO(pluNO);
				oneLv1.setPluName(pluName);
				oneLv1.setCategoryNO(categoryNO);
				oneLv1.setCategoryName(categoryName);



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

					if (orderStock.equals("0")||Integer.valueOf(orderStock)==0 ) 
					{
						orderOnShelf="N";
					}

					orderSpec.setOrderSpecID(orderSpecID);
					orderSpec.setOrderSpecName(orderSpecName);
					orderSpec.setOrderPrice(orderPrice);
					orderSpec.setOrderStock(orderStock);
					orderSpec.setOrderPackingFee(orderPackingFee);
					orderSpec.setOrderOnShelf(orderOnShelf);
					orderSpec.setPluBarcode(pluBarcode);
					orderSpec.setPluSpecName(pluSpecName);

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


		}


	}

	protected String getString(String[] str)
	{
		String str2 = "";

		for (String s:str)
		{
			if(s.isEmpty()||s.trim().isEmpty()||s.trim().length()==0)
			{
				continue;
			}
			str2 = str2 + "'" + s + "'"+ ",";
		}
		if (str2.length()>0)
		{
			str2=str2.substring(0,str2.length()-1);
		}

		//System.out.println(str2);

		return str2;
	}

}
