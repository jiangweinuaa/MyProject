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
import org.apache.xml.resolver.apps.resolver;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.ExecBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.res.DCP_OrderPlatformCategoryQueryRes;
import com.dsc.spos.json.cust.res.DCP_OrderTransTaskGoodsRes;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.WMELMProductService;
import eleme.openapi.sdk.api.entity.product.OCategory;
import eleme.openapi.sdk.api.entity.product.OItem;
import eleme.openapi.sdk.api.entity.product.OItemAttribute;
import eleme.openapi.sdk.api.entity.product.OItemSellingTime;
import eleme.openapi.sdk.api.entity.product.OItemTime;
import eleme.openapi.sdk.api.entity.product.OMaterial;
import eleme.openapi.sdk.api.entity.product.OSpec;
import eleme.openapi.sdk.api.enumeration.product.OItemCreateProperty;
import eleme.openapi.sdk.api.enumeration.product.OItemUpdateProperty;
import eleme.openapi.sdk.api.enumeration.product.OItemWeekEnum;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class OrderTransTaskELMProcess extends InitJob
{
	Logger logger = LogManager.getLogger(OrderTransTaskELMProcess.class.getName());
	static boolean bRun=false;//标记此服务是否正在执行中
	String elmLogFileName = "orderTranTaskELMlog";

	public OrderTransTaskELMProcess ()
	{

	}

	public String doExe() throws Exception
	{
		String sReturnInfo = "";
		HelpTools.writelog_fileName("【同步任务orderTranTasklog】同步START！",elmLogFileName);
		try 
		{
			//此服务是否正在执行中
			if (bRun)
			{		
				logger.info("\r\n*********同步任务orderTranTasklog同步正在执行中,本次调用取消:************\r\n");
				HelpTools.writelog_fileName("【同步任务orderTranTasklog】同步正在执行中,本次调用取消！",elmLogFileName);
				return sReturnInfo;
			}
			bRun=true;//			
			String loadDocType = "1";//饿了么					
			List<Map<String, Object>> transTask = this.GetTransTask(loadDocType);		

			if(transTask!=null && transTask.isEmpty()==false)
			{
				//查任务表分类和任务表商品
				List<Map<String, Object>> transTask_Category = this.GetTransTask_Category(loadDocType);
				List<Map<String, Object>> transTask_Goods = this.GetTransTask_Goods(loadDocType);

				for (Map<String, Object> transTaskMap : transTask) 
				{

					String transTask_eId = transTaskMap.get("EID").toString();
					String transTask_ShopNO = transTaskMap.get("SHOPID").toString();
					String transTask_OrderShopNO = transTaskMap.get("ORDER_SHOP").toString();
					String transTask_transID =  transTaskMap.get("TRANS_ID").toString();						
					String transTask_transType = transTaskMap.get("TRANS_TYPE").toString();
					try 
					{
						HelpTools.writelog_fileName("【同步任务orderTranTasklog】开始循环任务表OC_TRANSTASK！任务类型：" + transTask_transType + " 任务ID："
								+ transTask_transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+transTask_ShopNO+" ", elmLogFileName);
						//1、分类新增   2、分类修改 3、分类删除
						if(transTask_transType.equals("1")||transTask_transType.equals("2")||transTask_transType.equals("3"))
						{

							if (transTask_Category == null || transTask_Category.isEmpty() )
							{
								HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步分类】需要同步的数据为空！任务ID："
										+ transTask_transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+transTask_ShopNO+" ", elmLogFileName);
								continue;
							}
							else
							{
								String categoryNO="";
								String categoryName="";
								//String priority ="";
								String orderCategoryNO="";
								String orderCategoryName="";

								for (Map<String, Object> transTask_CategoryMap:transTask_Category )
								{
									String transTask_Category_eId = transTask_CategoryMap.get("EID").toString();
									String transTask_Category_ShopNO = transTask_CategoryMap.get("SHOPID").toString();
									String transTask_Category_TransID =  transTask_CategoryMap.get("TRANS_ID").toString();	
									if (transTask_eId.equals(transTask_Category_eId) && transTask_ShopNO.equals(transTask_Category_ShopNO) && transTask_transID.equals(transTask_Category_TransID))   
									{
										categoryNO=transTask_CategoryMap.get("CATEGORYNO").toString();
										categoryName=transTask_CategoryMap.get("CATEGORYNAME").toString();
										//priority =transTask_CategoryMap.get("PRIORITY").toString();
										orderCategoryNO=transTask_CategoryMap.get("ORDER_CATEGORYNO").toString();
										orderCategoryName=transTask_CategoryMap.get("ORDER_CATEGORYNAME").toString();
										break;
									}	
								}
								if(categoryNO ==null||categoryNO.isEmpty()||categoryNO.trim().length()==0)
								{
									HelpTools.writelog_fileName("【同步任务orderTranTasklog】同步当前任务异常！任务类型：" + transTask_transType + " 商品分类categoryNO："+" 为空！",elmLogFileName);
									continue;
								}
								DCP_OrderPlatformCategoryQueryRes res = new DCP_OrderPlatformCategoryQueryRes();
								DCP_OrderPlatformCategoryQueryRes.level1Elm catesModel =  res.new level1Elm();
								catesModel.setCategoryNO(categoryNO);
								catesModel.setCategoryName(categoryName);
								catesModel.setOrderCategoryNO(orderCategoryNO);
								catesModel.setOrderCategoryName(orderCategoryName);
								catesModel.setOrderDescription("");
								catesModel.setOrderPriority("");
								catesModel.setOrderShopNO(transTask_OrderShopNO);

								//调用第三方接口 并且更新任务表
								if(transTask_transType.equals("1"))
								{
									//分类新增							
									this.CallCatesAddAPIFun(transTask_transType,transTask_transID, transTask_eId, transTask_ShopNO, loadDocType,transTask_OrderShopNO, catesModel);
								}
								else if(transTask_transType.equals("2"))
								{
									//分类更新
									this.CallCatesUpdateAPIFun(transTask_transType,transTask_transID, transTask_eId, transTask_ShopNO, loadDocType,transTask_OrderShopNO, catesModel);
								}
								else 
								{
									//分类删除
									this.CallCatesDeleteAPIFun(transTask_transType,transTask_transID, transTask_eId, transTask_ShopNO, loadDocType, transTask_OrderShopNO,catesModel);
								}
							}
						}
						//1、商品新增   2、商品修改 3、商品删除
						else if (transTask_transType.equals("4")||transTask_transType.equals("5")||transTask_transType.equals("6")) //4、商品新增  5、商品修改 6、商品删除
						{
							if (transTask_Goods == null || transTask_Goods.isEmpty() )
							{
								HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步商品】需要同步的数据为空！任务ID："
										+ transTask_transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+transTask_ShopNO+" ", elmLogFileName);
								continue;
							}
							else
							{
								DCP_OrderTransTaskGoodsRes res = new DCP_OrderTransTaskGoodsRes();
								DCP_OrderTransTaskGoodsRes.level1Elm goodsModel = res.new level1Elm();
								String pluNO="";
								Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查询条件
								condition.put("EID", true);
								condition.put("SHOPID", true);
								condition.put("TRANS_ID", true);
								//condition.put("PLUNO", true);										
								//调用过滤函数
								List<Map<String, Object>> getTransTask_GoodsQData =MapDistinct.getMap(transTask_Goods, condition);
								for (Map<String, Object> oneTransTask_GoodsQData : getTransTask_GoodsQData) 
								{									
									String transTask_Goods_eId = oneTransTask_GoodsQData.get("EID").toString();
									String transTask_Goods_ShopNO = oneTransTask_GoodsQData.get("SHOPID").toString();
									String transTask_Goods_TransID =  oneTransTask_GoodsQData.get("TRANS_ID").toString();	
									if (transTask_eId.equals(transTask_Goods_eId) && transTask_ShopNO.equals(transTask_Goods_ShopNO) && transTask_transID.equals(transTask_Goods_TransID))   
									{
										pluNO = oneTransTask_GoodsQData.get("PLUNO").toString();
										String pluName = oneTransTask_GoodsQData.get("PLUNAME").toString();
										String orderPluNO = oneTransTask_GoodsQData.get("ORDER_PLUNO").toString();
										String orderPluName = oneTransTask_GoodsQData.get("ORDER_PLUNAME").toString();										
										String categoryNO = oneTransTask_GoodsQData.get("CATEGORYNO").toString();
										String categoryName = oneTransTask_GoodsQData.get("CATEGORYNAME").toString();
										String orderCategoryNO = oneTransTask_GoodsQData.get("ORDER_CATEGORYNO").toString();
										String orderCategoryName = oneTransTask_GoodsQData.get("ORDER_CATEGORYNAME").toString();										
										String description =oneTransTask_GoodsQData.get("DESCRIPTION").toString();
										String fileName =oneTransTask_GoodsQData.get("FILENAME").toString();
										String elmHash=oneTransTask_GoodsQData.get("ELMHASH").toString();
										String jbpHash = "";
										try 
										{
											jbpHash = oneTransTask_GoodsQData.get("JBPHASH").toString();
						
					          } 
										catch (Exception e) 
										{
											jbpHash ="";
					          }
										String unit=oneTransTask_GoodsQData.get("UNIT").toString();
										String priority=oneTransTask_GoodsQData.get("PRIORITY").toString();										
										String materialID1 = oneTransTask_GoodsQData.get("MATERIALID1").toString();
										String materialID2 = oneTransTask_GoodsQData.get("MATERIALID2").toString();
										String materialID3 = oneTransTask_GoodsQData.get("MATERIALID3").toString();
										String materialID4 = oneTransTask_GoodsQData.get("MATERIALID4").toString();
										String materialID5 = oneTransTask_GoodsQData.get("MATERIALID5").toString();
										String materialID6 = oneTransTask_GoodsQData.get("MATERIALID6").toString();
										String materialID7 = oneTransTask_GoodsQData.get("MATERIALID7").toString();
										String materialID8 = oneTransTask_GoodsQData.get("MATERIALID8").toString();
										String materialID9 = oneTransTask_GoodsQData.get("MATERIALID9").toString();
										String materialID10 = oneTransTask_GoodsQData.get("MATERIALID10").toString();
										String material1 = oneTransTask_GoodsQData.get("MATERIAL1").toString();
										String material2 = oneTransTask_GoodsQData.get("MATERIAL2").toString();
										String material3 = oneTransTask_GoodsQData.get("MATERIAL3").toString();
										String material4 = oneTransTask_GoodsQData.get("MATERIAL4").toString();
										String material5 = oneTransTask_GoodsQData.get("MATERIAL5").toString();
										String material6 = oneTransTask_GoodsQData.get("MATERIAL6").toString();
										String material7 = oneTransTask_GoodsQData.get("MATERIAL7").toString();
										String material8 = oneTransTask_GoodsQData.get("MATERIAL8").toString();
										String material9 = oneTransTask_GoodsQData.get("MATERIAL9").toString();
										String material10 = oneTransTask_GoodsQData.get("MATERIAL10").toString();
										String isAllTimeSell = oneTransTask_GoodsQData.get("ISALLTIMESELL").toString();
										String beginDate = oneTransTask_GoodsQData.get("BEGINDATE").toString();
										String endDate =oneTransTask_GoodsQData.get("ENDDATE").toString();
										String sellWeek = oneTransTask_GoodsQData.get("SELLWEEK").toString();
										String sellTime = oneTransTask_GoodsQData.get("SELLTIME").toString();
										if (isAllTimeSell.equals("Y"))
										{
											beginDate = "";
											endDate ="";
											sellWeek = "";
											sellTime = "";
										}										
										goodsModel.setOrderPluNO(orderPluNO);
										goodsModel.setOrderPluName(orderPluName);
										goodsModel.setOrderCategoryNO(orderCategoryNO);
										goodsModel.setOrderCategoryName(orderCategoryName);
										goodsModel.setPluNO(pluNO);	
										goodsModel.setPluName(pluName);
										goodsModel.setCategoryNO(categoryNO);
										goodsModel.setCategoryName(categoryName);
										goodsModel.setUnit(unit);
										goodsModel.setPriority(priority);										
										goodsModel.setMaterialID1(materialID1);
										goodsModel.setMaterialID2(materialID2);
										goodsModel.setMaterialID3(materialID3);
										goodsModel.setMaterialID4(materialID4);
										goodsModel.setMaterialID5(materialID5);
										goodsModel.setMaterialID6(materialID6);
										goodsModel.setMaterialID7(materialID7);
										goodsModel.setMaterialID8(materialID8);
										goodsModel.setMaterialID9(materialID9);
										goodsModel.setMaterialID10(materialID10);							
										goodsModel.setMaterial1(material1);
										goodsModel.setMaterial2(material2);
										goodsModel.setMaterial3(material3);
										goodsModel.setMaterial4(material4);
										goodsModel.setMaterial5(material5);
										goodsModel.setMaterial6(material6);
										goodsModel.setMaterial7(material7);
										goodsModel.setMaterial8(material8);
										goodsModel.setMaterial9(material9);
										goodsModel.setMaterial10(material10);
										goodsModel.setFileName(fileName);
										goodsModel.setElmHash(elmHash);
										goodsModel.setJbpHash(jbpHash);
										goodsModel.setDescription(description);
										goodsModel.setIsAllTimeSell(isAllTimeSell);
										goodsModel.setBeginDate(beginDate);
										goodsModel.setEndDate(endDate);
										goodsModel.setSellWeek(sellWeek);
										goodsModel.setSellTime(sellTime);
										break;
									}	
								}
								if(pluNO ==null||pluNO.isEmpty()||pluNO.trim().length()==0)
								{								
									HelpTools.writelog_fileName("【同步任务orderTranTasklog】同步当前任务异常！任务类型：" + transTask_transType + " 商品pluNO："+" 为空！",elmLogFileName);
									continue;						
								}

								goodsModel.setSpecs(new ArrayList<DCP_OrderTransTaskGoodsRes.level2Spec>());
								condition.clear();
								condition.put("EID", true);
								condition.put("SHOPID", true);
								condition.put("TRANS_ID", true);
								condition.put("PLUNO", true);	
								condition.put("SPECNO", true);	

								//调用过滤函数
								List<Map<String, Object>> getTransTask_GoodsSpecQData =MapDistinct.getMap(transTask_Goods, condition);
								for (Map<String, Object> oneTransTask_GoodsSpecQData : getTransTask_GoodsSpecQData) 
								{									
									String transTask_GoodsSpes_eId = oneTransTask_GoodsSpecQData.get("EID").toString();
									String transTask_GoodsSpes_ShopNO = oneTransTask_GoodsSpecQData.get("SHOPID").toString();
									String transTask_GoodsSpes_TransID =  oneTransTask_GoodsSpecQData.get("TRANS_ID").toString();	
									String transTask_GoodsSpes_pluNO =  oneTransTask_GoodsSpecQData.get("PLUNO").toString();	
									if (transTask_eId.equals(transTask_GoodsSpes_eId) && transTask_ShopNO.equals(transTask_GoodsSpes_ShopNO) && transTask_transID.equals(transTask_GoodsSpes_TransID)&& pluNO.equals(transTask_GoodsSpes_pluNO))   
									{
										String specNO = oneTransTask_GoodsSpecQData.get("SPECNO").toString();
										String specName = oneTransTask_GoodsSpecQData.get("SPECNAME").toString();
										String orderSpecNO = oneTransTask_GoodsSpecQData.get("ORDER_SPECNO").toString();
										String orderSpecName = oneTransTask_GoodsSpecQData.get("ORDER_SPECNAME").toString();										
										String price = oneTransTask_GoodsSpecQData.get("PRICE").toString();
										String stockQty = oneTransTask_GoodsSpecQData.get("STOCKQTY").toString();
										String packageFee = oneTransTask_GoodsSpecQData.get("PACKAGEFEE").toString();
										String isOnshelf = oneTransTask_GoodsSpecQData.get("ISONSHELF").toString();
										String netWeight = oneTransTask_GoodsSpecQData.get("NETWEIGHT").toString();

										DCP_OrderTransTaskGoodsRes.level2Spec oneDataSpec = res.new level2Spec();
										oneDataSpec.setOrderSpecNO(orderSpecNO);
										oneDataSpec.setOrderSpecName(orderSpecName);
										oneDataSpec.setSpecNO(specNO);
										oneDataSpec.setSpecName(specName);
										oneDataSpec.setPrice(price);
										oneDataSpec.setStockQty(stockQty);										
										oneDataSpec.setMaxStockQty("10000");
										oneDataSpec.setPackageFee(packageFee);
										oneDataSpec.setIsOnShelf(isOnshelf);
										oneDataSpec.setNetWeight(netWeight);
										goodsModel.getSpecs().add(oneDataSpec);
										oneDataSpec=null;
									}									
								}

								goodsModel.setAttr(new ArrayList<DCP_OrderTransTaskGoodsRes.level2Attr>());
								condition.clear();
								condition.put("EID", true);
								condition.put("SHOPID", true);
								condition.put("TRANS_ID", true);
								condition.put("PLUNO", true);	
								condition.put("ATTRNAME", true);	
								//condition.put("ATTRVALUE", true);	
								//调用过滤函数
								List<Map<String, Object>> getTransTask_GoodsAttrQData =MapDistinct.getMap(transTask_Goods, condition);
								for (Map<String, Object> oneTransTask_GoodsAttrQData : getTransTask_GoodsAttrQData) 
								{	
									String transTask_GoodsAttr_eId = oneTransTask_GoodsAttrQData.get("EID").toString();
									String transTask_GoodsAttr_ShopNO = oneTransTask_GoodsAttrQData.get("SHOPID").toString();
									String transTask_GoodsAttr_TransID = oneTransTask_GoodsAttrQData.get("TRANS_ID").toString();	
									String transTask_GoodsAttr_pluNO = oneTransTask_GoodsAttrQData.get("PLUNO").toString();	
									if (transTask_eId.equals(transTask_GoodsAttr_eId) && transTask_ShopNO.equals(transTask_GoodsAttr_ShopNO) && transTask_transID.equals(transTask_GoodsAttr_TransID)&& pluNO.equals(transTask_GoodsAttr_pluNO))   
									{
										String attrName = oneTransTask_GoodsAttrQData.get("ATTRNAME").toString();
										String attrValue = oneTransTask_GoodsAttrQData.get("ATTRVALUE").toString();
										String orderAttrName = oneTransTask_GoodsAttrQData.get("ORDER_ATTRNAME").toString();
										String orderAttrValue = oneTransTask_GoodsAttrQData.get("ORDER_ATTRVALUE").toString();					
										if ((Check.Null(attrName)&&Check.Null(attrValue) &&Check.Null(orderAttrName) &&Check.Null(orderAttrValue))==false)
										{
											DCP_OrderTransTaskGoodsRes.level2Attr oneDataAttr = res.new level2Attr();
											oneDataAttr.setAttrName(attrName);
											oneDataAttr.setAttrValue(attrValue);
											oneDataAttr.setOrderAttrName(orderAttrName);
											oneDataAttr.setOrderAttrValue(orderAttrValue);									
											goodsModel.getAttr().add(oneDataAttr);
											oneDataAttr = null;
										}
									}
								}

								//调用第三方接口 并且更新任务表
								if(transTask_transType.equals("4"))
								{
									//商品新增							
									this.CallGoodsAddAPIFun(transTask_transType,transTask_transID, transTask_eId, transTask_ShopNO, loadDocType,transTask_OrderShopNO,goodsModel);
								}
								else if(transTask_transType.equals("5"))
								{
									//商品更新
									this.CallGoodsUpdateAPIFun(transTask_transType,transTask_transID, transTask_eId, transTask_ShopNO, loadDocType,transTask_OrderShopNO,goodsModel);
								}
								else 
								{
									//商品删除
									this.CallGoodsDeleteAPIFun(transTask_transType,transTask_transID, transTask_eId, transTask_ShopNO, loadDocType,transTask_OrderShopNO,goodsModel);
								}
							}
						}
						else //其他类型 暂不支持
						{
							HelpTools.writelog_fileName("【同步任务orderTranTasklog】同步当前任务异常【任务类型未知】！任务类型：" + transTask_transType + " 商品pluNO："+" 为空！",elmLogFileName);
							continue;											
						}
					} 
					catch (Exception e) 
					{
						HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步商品】 异常:"+e.getMessage()+" 任务ID："
								+ transTask_transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+transTask_ShopNO , elmLogFileName);
						UpdateTransTask("1",e.getMessage(),transTask_eId,transTask_ShopNO,loadDocType,transTask_transID) ;	
					}
				}
			}
			else
			{			
				sReturnInfo="无符合要求的数据！";
				HelpTools.writelog_fileName("【同步任务orderTranTasklog】没有需要处理任务！",elmLogFileName);
				logger.info("\r\n******同步任务orderTranTasklog没有需要处理的任务******\r\n");
			}
		}
		catch (Exception e) 
		{

		}
		finally 
		{
			bRun=false;//
		}
		logger.info("\r\n***************同步任务orderTranTasklog同步END****************\r\n");
		return sReturnInfo;
	}

	protected void doExecuteDataToDB(List<DataProcessBean> pData) throws Exception {
		if (pData == null || pData.size() == 0) {
			return;
		}
		StaticInfo.dao.useTransactionProcessData(pData);
	}

	/**
	 * 添加分类
	 * @param transType
	 * @param transID
	 * @param eId
	 * @param shopId
	 * @param loadDocType
	 * @param catesModel
	 * @throws Exception
	 */
	private void CallCatesAddAPIFun(String transType,String transID,String eId,String shopId,String loadDocType,String orderShopNO,DCP_OrderPlatformCategoryQueryRes.level1Elm catesModel) throws Exception
	{
		//String orderShopNO = catesModel.getOrderShopNO();
		String categoryNO = catesModel.getCategoryNO();
		String categoryName = catesModel.getCategoryName();
		String orderCategoryNO= catesModel.getOrderCategoryNO();
		String orderCategoryName=catesModel.getOrderCategoryName();
		String description="";
		String trans_flg = "1";
		String transTaskDescription="";
		OCategory nRet = null;
		try 
		{
			if ( Check.Null(orderCategoryNO ) && Check.Null(orderCategoryName) )
			{
				//查询下当前门店shopId的对应的饿了么APPKEY
				Map<String, Object> mapAppKey = PosPub.getWaimaiAppConfigByShopNO_New(StaticInfo.dao, eId, shopId, loadDocType,"");
				Boolean isGoNewFunction = false;//是否走新的接口
				String elmAPPKey = "";
				String elmAPPSecret = "";
				String elmAPPName = "";			
				boolean elmIsSandbox = false;
				if (mapAppKey != null)
				{
					elmAPPKey = mapAppKey.get("APPKEY").toString();
					elmAPPSecret = mapAppKey.get("APPSECRET").toString();
					elmAPPName = mapAppKey.get("APPNAME").toString();
					String	elmIsTest = mapAppKey.get("ISTEST").toString();					
					if (elmIsTest != null && elmIsTest.equals("Y"))
					{
						elmIsSandbox = true;
					}
					isGoNewFunction = true;
				}
				StringBuilder errorMessage = new StringBuilder();
				try
				{
					if(isGoNewFunction)
					{
						nRet = WMELMProductService.createCategory(elmIsSandbox,elmAPPKey,elmAPPSecret,elmAPPName,new Long(orderShopNO),categoryName,description, errorMessage);
					}
					else
					{
						nRet = WMELMProductService.createCategory(new Long(orderShopNO),categoryName,description, errorMessage);
					}
					if(nRet!=null) 
					{
						trans_flg = "2";
						//回写平台ID
						catesModel.setOrderCategoryNO(String.valueOf(nRet.getId()));
						catesModel.setOrderCategoryName(nRet.getName());
						
						this.SaveCatesLocal(eId, shopId, loadDocType, orderShopNO, catesModel);	
						//新建的时候，饿了么分类排序
						this.SetCategorySequence(eId, shopId, loadDocType, orderShopNO, catesModel);
						
					}
					else
					{
						 transTaskDescription = errorMessage.toString();
					}
					

				}
				catch (Exception e)
				{
					trans_flg = "1";
					transTaskDescription= "饿了么接口调用失败:"+e.getMessage() ;
				}
			}
			else
			{
				trans_flg = "1";//分类已经存在
				transTaskDescription="("+categoryName+")分类已经存在,无法新增";			
			}
		}
		catch (Exception e) 
		{
			HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步分类】【新增】异常:"+e.getMessage()+" 任务ID："
					+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+shopId+" 商品分类categoryNO："+categoryNO, elmLogFileName);
			transTaskDescription=e.getMessage();
		}
		finally 
		{
			UpdateTransTask(trans_flg,transTaskDescription,eId,shopId,loadDocType,transID) ;
			HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步分类】【新增】完成!任务状态TRANS_FLG:"+trans_flg+" 任务ID："
					+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+shopId+" 商品分类categoryNO："+categoryNO, elmLogFileName);
		}
	}

	/**
	 * 分类修改
	 * @param transType
	 * @param transID
	 * @param eId
	 * @param shopId
	 * @param loadDocType
	 * @param catesModel
	 * @throws Exception
	 */
	private void CallCatesUpdateAPIFun(String transType,String transID,String eId,String shopId,String loadDocType,String orderShopNO,DCP_OrderPlatformCategoryQueryRes.level1Elm catesModel) throws Exception
	{
		String orderCategoryNO = catesModel.getOrderCategoryNO();	
		String orderCategoryName = catesModel.getOrderCategoryName();	
		String categoryNO = catesModel.getCategoryNO();
		String categoryName = catesModel.getCategoryName();
		String description = catesModel.getOrderDescription();
		String trans_flg = "1";
		String transTaskDescription="";
		StringBuilder errorMessage = new StringBuilder();
		OCategory nRet = null;
		try 
		{
			if (!Check.Null(orderCategoryNO) && !Check.Null(orderCategoryName))
			{
				//查询下当前门店shopId的对应的饿了么APPKEY
				Map<String, Object> mapAppKey = PosPub.getWaimaiAppConfigByShopNO_New(StaticInfo.dao, eId, shopId, loadDocType,"");
				Boolean isGoNewFunction = false;//是否走新的接口
				String elmAPPKey = "";
				String elmAPPSecret = "";
				String elmAPPName = "";			
				boolean elmIsSandbox = false;
				if (mapAppKey != null)
				{
					elmAPPKey = mapAppKey.get("APPKEY").toString();
					elmAPPSecret = mapAppKey.get("APPSECRET").toString();
					elmAPPName = mapAppKey.get("APPNAME").toString();
					String	elmIsTest = mapAppKey.get("ISTEST").toString();					
					if (elmIsTest != null && elmIsTest.equals("Y"))
					{
						elmIsSandbox = true;
					}
					isGoNewFunction = true;
				}

				try
				{
					if(isGoNewFunction)
					{
						nRet = WMELMProductService.updateCategory(elmIsSandbox,elmAPPKey,elmAPPSecret,elmAPPName,new Long(orderCategoryNO),categoryName,description, errorMessage);
					}
					else
					{
						nRet = WMELMProductService.updateCategory(new Long(orderCategoryNO),categoryName,description, errorMessage);
					}		
					if(nRet!=null)
					{
						trans_flg = "2";
						//回写平台ID
						catesModel.setOrderCategoryNO(String.valueOf(nRet.getId()));
						catesModel.setOrderCategoryName(nRet.getName());
						
						this.SaveCatesLocal(eId, shopId, loadDocType, orderShopNO, catesModel);	
					//新建的时候，饿了么分类排序
						this.SetCategorySequence(eId, shopId, loadDocType, orderShopNO, catesModel);
						
					}
					else
					{
						transTaskDescription = errorMessage.toString();
					}
				}
				catch (Exception e)
				{
					trans_flg = "1";
					transTaskDescription= "饿了么接口调用失败:"+e.getMessage();
				}
			}
			else
			{
				trans_flg = "1";
				transTaskDescription="("+orderCategoryName+")平台分类未查询到，无法修改分类";
			}
		}
		catch (Exception e) 
		{
			HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步分类】【修改】异常:"+e.getMessage()+" 任务ID："
					+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+shopId+" 商品分类categoryNO："+categoryNO, elmLogFileName);
			transTaskDescription=e.getMessage();
		}
		finally 
		{
			//更新任务状态
			UpdateTransTask(trans_flg,transTaskDescription,eId,shopId,loadDocType,transID) ;
			HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步分类】【修改】完成!任务状态TRANS_FLG:"+trans_flg+" 任务ID："
					+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+shopId+" 商品分类categoryNO："+categoryNO, elmLogFileName);
		}

	}

	/**
	 * 分类删除
	 * @param transType
	 * @param transID
	 * @param eId
	 * @param shopId
	 * @param loadDocType
	 * @param catesModel
	 * @throws Exception
	 */
	private void CallCatesDeleteAPIFun(String transType,String transID,String eId,String shopId,String loadDocType,String orderShopNO,DCP_OrderPlatformCategoryQueryRes.level1Elm catesModel) throws Exception
	{
		String orderCategoryNO = catesModel.getOrderCategoryNO();	
		String orderCategoryName = catesModel.getOrderCategoryName();	
		String categoryNO = catesModel.getCategoryNO();
		String trans_flg = "1";
		String transTaskDescription="";
		StringBuilder errorMessage = new StringBuilder();
		boolean nRet = false;
		try
		{
			if (!Check.Null(orderCategoryNO) && !Check.Null(orderCategoryName))
			{
				//查询下当前门店shopId的对应的饿了么APPKEY
				Map<String, Object> mapAppKey = PosPub.getWaimaiAppConfigByShopNO_New(StaticInfo.dao, eId, shopId, loadDocType,"");
				Boolean isGoNewFunction = false;//是否走新的接口
				String elmAPPKey = "";
				String elmAPPSecret = "";
				String elmAPPName = "";			
				boolean elmIsSandbox = false;
				if (mapAppKey != null)
				{
					elmAPPKey = mapAppKey.get("APPKEY").toString();
					elmAPPSecret = mapAppKey.get("APPSECRET").toString();
					elmAPPName = mapAppKey.get("APPNAME").toString();
					String	elmIsTest = mapAppKey.get("ISTEST").toString();					
					if (elmIsTest != null && elmIsTest.equals("Y"))
					{
						elmIsSandbox = true;
					}
					isGoNewFunction = true;
				}
				try
				{
					if(isGoNewFunction)
					{
						nRet = WMELMProductService.invalidCategory(elmIsSandbox,elmAPPKey,elmAPPSecret,elmAPPName,new Long(orderCategoryNO), errorMessage);
					}
					else
					{
						nRet = WMELMProductService.invalidCategory(new Long(orderCategoryNO), errorMessage);
					}
					if(nRet) 
					{
						trans_flg = "2";
						this.DeleteCatesLocal(eId, shopId, loadDocType, orderShopNO, catesModel);	
					}
					else
					{
						transTaskDescription = errorMessage.toString();
					}
				}
				catch (Exception e)
				{
					trans_flg = "1";
					transTaskDescription= "饿了么接口调用失败:"+e.getMessage();
				}
			}
			else
			{
				trans_flg = "1";
				transTaskDescription= "("+orderCategoryName+")平台分类未查询到，无法删除分类";
			}
		}
		catch (Exception e) 
		{
			HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步分类】【删除】异常:"+e.getMessage()+" 任务ID："
					+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+shopId+" 商品分类categoryNO："+categoryNO, elmLogFileName);
			transTaskDescription=e.getMessage();
		}
		finally 
		{
			UpdateTransTask(trans_flg,transTaskDescription,eId,shopId,loadDocType,transID) ;
			HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步分类】【删除】完成!任务状态TRANS_FLG:"+trans_flg+" 任务ID："
					+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+shopId+" 商品分类categoryNO："+categoryNO, elmLogFileName);
		}
	}


	/**
	 * 商品新增
	 * @param transType
	 * @param transID
	 * @param eId
	 * @param shopId
	 * @param loadDocType
	 * @param goodsModel
	 * @throws Exception
	 */
	private void CallGoodsAddAPIFun(String transType,String transID,String eId,String shopId,String loadDocType,String orderShopNO,DCP_OrderTransTaskGoodsRes.level1Elm goodsModel) throws Exception
	{	
		String pluNO = goodsModel.getPluNO();
		String pluName = goodsModel.getPluName();
		String orderPluNO = goodsModel.getOrderPluNO();
		String orderPluName = goodsModel.getOrderPluName();			
		String orderCategoryNO = goodsModel.getOrderCategoryNO();	
		String description = goodsModel.getDescription();
		String fileName= goodsModel.getFileName(); 
		String elmHash = goodsModel.getElmHash(); 
		String unit = goodsModel.getUnit();
		String isAllTimeSell = goodsModel.getIsAllTimeSell();
		String beginDate = goodsModel.getBeginDate();
		String endDate = goodsModel.getEndDate();
		String sellWeek = goodsModel.getSellWeek();
		String sellTime = goodsModel.getSellTime();
		String trans_flg = "1";
		String transTaskDescription="";
		try 
		{
			if (Check.Null(orderPluNO) && Check.Null(orderPluName)&&!Check.Null(orderCategoryNO))
			{
				List<Map<OItemCreateProperty,Object>> propertiesList = new ArrayList<Map<OItemCreateProperty,Object>>();
				Map<OItemCreateProperty,Object> properties = new HashMap<OItemCreateProperty,Object>();
				List<OMaterial> oMaterials = new ArrayList<OMaterial>();
				if ( !Check.Null(goodsModel.getMaterial1()) && !Check.Null(goodsModel.getMaterialID1()))
				{
					OMaterial oMaterial = new OMaterial();
					oMaterial.setId(Long.valueOf(goodsModel.getMaterialID1()));
					oMaterial.setName(goodsModel.getMaterial1());
					oMaterials.add(oMaterial);
				}
				if ( !Check.Null(goodsModel.getMaterial2()) && !Check.Null(goodsModel.getMaterialID2()))
				{
					OMaterial oMaterial = new OMaterial();
					oMaterial.setId(Long.valueOf(goodsModel.getMaterialID2()));
					oMaterial.setName(goodsModel.getMaterial2());
					oMaterials.add(oMaterial);
				}
				if ( !Check.Null(goodsModel.getMaterial3()) && !Check.Null(goodsModel.getMaterialID3()))
				{
					OMaterial oMaterial = new OMaterial();
					oMaterial.setId(Long.valueOf(goodsModel.getMaterialID3()));
					oMaterial.setName(goodsModel.getMaterial3());
					oMaterials.add(oMaterial);
				}
				if ( !Check.Null(goodsModel.getMaterial4()) && !Check.Null(goodsModel.getMaterialID4()))
				{
					OMaterial oMaterial = new OMaterial();
					oMaterial.setId(Long.valueOf(goodsModel.getMaterialID4()));
					oMaterial.setName(goodsModel.getMaterial4());
					oMaterials.add(oMaterial);
				}
				if ( !Check.Null(goodsModel.getMaterial5()) && !Check.Null(goodsModel.getMaterialID5()))
				{
					OMaterial oMaterial = new OMaterial();
					oMaterial.setId(Long.valueOf(goodsModel.getMaterialID5()));
					oMaterial.setName(goodsModel.getMaterial5());
					oMaterials.add(oMaterial);
				}
				if ( !Check.Null(goodsModel.getMaterial6()) && !Check.Null(goodsModel.getMaterialID6()))
				{
					OMaterial oMaterial = new OMaterial();
					oMaterial.setId(Long.valueOf(goodsModel.getMaterialID6()));
					oMaterial.setName(goodsModel.getMaterial6());
					oMaterials.add(oMaterial);
				}
				if ( !Check.Null(goodsModel.getMaterial7()) && !Check.Null(goodsModel.getMaterialID7()))
				{
					OMaterial oMaterial = new OMaterial();
					oMaterial.setId(Long.valueOf(goodsModel.getMaterialID7()));
					oMaterial.setName(goodsModel.getMaterial7());
					oMaterials.add(oMaterial);
				}
				if ( !Check.Null(goodsModel.getMaterial8()) && !Check.Null(goodsModel.getMaterialID8()))
				{
					OMaterial oMaterial = new OMaterial();
					oMaterial.setId(Long.valueOf(goodsModel.getMaterialID8()));
					oMaterial.setName(goodsModel.getMaterial8());
					oMaterials.add(oMaterial);
				}
				if ( !Check.Null(goodsModel.getMaterial9()) && !Check.Null(goodsModel.getMaterial9()))
				{
					OMaterial oMaterial = new OMaterial();
					oMaterial.setId(Long.valueOf(goodsModel.getMaterialID9()));
					oMaterial.setName(goodsModel.getMaterial9());
					oMaterials.add(oMaterial);
				}
				if ( !Check.Null(goodsModel.getMaterial10()) && !Check.Null(goodsModel.getMaterialID10()))
				{
					OMaterial oMaterial = new OMaterial();
					oMaterial.setId(Long.valueOf(goodsModel.getMaterialID10()));
					oMaterial.setName(goodsModel.getMaterial10());
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
				properties.put(OItemCreateProperty.materials,oMaterials);	

				//新增商品的时候，如果有图片，先判断有没有获取过图片hash值了，有了就不用调用接口
				if(!Check.Null(fileName) && Check.Null(elmHash))
				{
					elmHash=GetHash(eId, shopId,loadDocType,pluNO,fileName) ;
				}			
				properties.put(OItemCreateProperty.name,pluName);
				properties.put(OItemCreateProperty.description,description);
				properties.put(OItemCreateProperty.imageHash,elmHash);
				properties.put(OItemCreateProperty.unit,unit);

				if (isAllTimeSell.equals("N"))
				{
					OItemSellingTime oItemSellingTime = new OItemSellingTime();
					if (Check.Null(beginDate)) beginDate="2000-12-31";
					if (Check.Null(endDate)) endDate="2060-12-31";
					oItemSellingTime.setBeginDate(beginDate);
					oItemSellingTime.setEndDate(endDate);				
					List <OItemWeekEnum > weekEnum = new ArrayList<>();
					if(sellWeek!=null&&sellWeek.isEmpty()==false)
					{									
						for (String week: sellWeek.split(",")){
							if (week.equals("1")) weekEnum.add(OItemWeekEnum.MONDAY);
							else if (week.equals("2")) weekEnum.add(OItemWeekEnum.TUESDAY);
							else if (week.equals("3")) weekEnum.add(OItemWeekEnum.WEDNESDAY);
							else if (week.equals("4")) weekEnum.add(OItemWeekEnum.THURSDAY);
							else if (week.equals("5")) weekEnum.add(OItemWeekEnum.FRIDAY);
							else if (week.equals("6")) weekEnum.add(OItemWeekEnum.SATURDAY);
							else if (week.equals("7")) weekEnum.add(OItemWeekEnum.SUNDAY);		           
						}
					}
					else
					{
						weekEnum.add(OItemWeekEnum.MONDAY);
						weekEnum.add(OItemWeekEnum.TUESDAY);
						weekEnum.add(OItemWeekEnum.WEDNESDAY);
						weekEnum.add(OItemWeekEnum.THURSDAY);
						weekEnum.add(OItemWeekEnum.FRIDAY);
						weekEnum.add(OItemWeekEnum.SATURDAY);
						weekEnum.add(OItemWeekEnum.SUNDAY);					 
					}
					oItemSellingTime.setWeeks(weekEnum);
					List<OItemTime> times = new ArrayList<OItemTime>();
					if(sellTime!=null&&sellTime.isEmpty()==false)
					{					
						String[] ssTime = sellTime.split(",");//16:00-19:00,20:00-23:00
						for (String timeStr : ssTime) 
						{
							String[] ss1 = timeStr.split("-");//16:00-19:00						  			
							OItemTime oItemTime = new OItemTime();
							oItemTime.setBeginTime(ss1[0].substring(0,5));
							oItemTime.setEndTime(ss1[1].substring(0,5));
							times.add(oItemTime);	
						}					  		
					}
					else
					{
						OItemTime oItemTime = new OItemTime();
						oItemTime.setBeginTime("00:00");
						oItemTime.setEndTime("23:59");
						times.add(oItemTime);	
					}
					oItemSellingTime.setTimes(times);
					properties.put(OItemCreateProperty.sellingTime, oItemSellingTime);
				}

				List<OSpec> oSpecs = new ArrayList<OSpec>();

				for (DCP_OrderTransTaskGoodsRes.level2Spec oneData : goodsModel.getSpecs()) 
				{																
					String specNO = oneData.getSpecNO();
					String specName = oneData.getSpecName();
					String price = oneData.getPrice();
					String stockQty = oneData.getStockQty();
					String packageFee = oneData.getPackageFee();
					String isOnShelf = oneData.getIsOnShelf();
					String maxStockQty =oneData.getMaxStockQty();
					String netWeight =oneData.getNetWeight();

					int onShelf_i = 1;							
					if (isOnShelf != null && isOnShelf.equals("N"))
					{
						onShelf_i = 0;
					}
					double price_d = 0;
					try
					{
						price_d = Double.parseDouble(price);
					}
					catch(Exception e)
					{
						price_d = 0;
					}
					double packageFee_d = 0;
					try
					{
						packageFee_d = Double.parseDouble(packageFee);
					}
					catch(Exception e)
					{
						packageFee_d = 0;
					}
					int stockQty_i = 0;
					try
					{
						stockQty_i = Integer.parseInt(stockQty);
					}
					catch(Exception e)
					{
						stockQty_i = 0;
					}
					int maxStockQty_i = 0;
					try
					{
						maxStockQty_i = Integer.parseInt(maxStockQty);
					}
					catch(Exception e)
					{
						maxStockQty_i = 10000;
					}
					int netWeight_i = 0;
					try
					{
						netWeight_i = Integer.parseInt(netWeight);
					}
					catch(Exception e)
					{
						netWeight_i = 0;
					}

					OSpec oSpec = new OSpec();				
					oSpec.setSpecId(0);        //饿了么接口说明：此处创建的时候，specId不需要填写，如果填写0，也可以正常创建
					oSpec.setExtendCode(specNO);
					oSpec.setName(specName);		
					oSpec.setPrice(price_d);				
					oSpec.setStock(stockQty_i);
					oSpec.setPackingFee(packageFee_d);
					oSpec.setMaxStock(maxStockQty_i);
					oSpec.setOnShelf(onShelf_i);
					oSpec.setWeight(netWeight_i);
					oSpecs.add(oSpec);
				}
				properties.put(OItemCreateProperty.specs,oSpecs);	

				///属性添加： 甜度    6分甜，7分甜，8分甜     
				if (goodsModel.getAttr()!= null && goodsModel.getAttr().size()>0)
				{
					List<OItemAttribute> oItemAttributes = new ArrayList<OItemAttribute>();		
					for (DCP_OrderTransTaskGoodsRes.level2Attr oneData : goodsModel.getAttr()) 
					{		
						OItemAttribute oItemAttribute = new OItemAttribute();
						List<String> attrValueList = new ArrayList<String>();
						String attrName = oneData.getAttrName();
						String attrValue = oneData.getAttrValue();
						String[] ssAttrValue = attrValue.split(","); //7分甜，6分甜，8分甜
						for (String attrValueStr : ssAttrValue) 
						{
							attrValueList.add(attrValueStr);
						}					  		
						oItemAttribute.setName(attrName);
						oItemAttribute.setDetails(attrValueList);
						oItemAttributes.add(oItemAttribute);
					}
					properties.put(OItemCreateProperty.attributes,oItemAttributes);	
				}
				propertiesList.add(properties);


				//查询下当前门店shopId的对应的饿了么APPKEY
				Map<String, Object> mapAppKey = PosPub.getWaimaiAppConfigByShopNO_New(StaticInfo.dao, eId, shopId, loadDocType,"");
				Boolean isGoNewFunction = false;//是否走新的接口
				String elmAPPKey = "";
				String elmAPPSecret = "";
				String elmAPPName = "";			
				boolean elmIsSandbox = false;
				if (mapAppKey != null)
				{
					elmAPPKey = mapAppKey.get("APPKEY").toString();
					elmAPPSecret = mapAppKey.get("APPSECRET").toString();
					elmAPPName = mapAppKey.get("APPNAME").toString();
					String	elmIsTest = mapAppKey.get("ISTEST").toString();					
					if (elmIsTest != null && elmIsTest.equals("Y"))
					{
						elmIsSandbox = true;
					}
					isGoNewFunction = true;
				}

				StringBuilder errorMessage = new StringBuilder();
				Map<Long,OItem> nRet = null;
				try
				{
					if(isGoNewFunction)
					{
						nRet = WMELMProductService.batchCreateItems(elmIsSandbox,elmAPPKey,elmAPPSecret,elmAPPName, new Long(orderCategoryNO), propertiesList, errorMessage);
					}
					else
					{
						nRet = WMELMProductService.batchCreateItems(new Long(orderCategoryNO), propertiesList, errorMessage);
					}
					if(nRet != null) 
					{
						trans_flg = "2";
						//回写返回的平台ID
						OItem nRetGoods = null;
						for (OItem oitem : nRet.values()) 
						{
							nRetGoods = oitem;//目前只有一个
							break;		
						}	
						if (nRetGoods != null)
						{
							goodsModel.setOrderPluNO(String.valueOf(nRetGoods.getId()));
							goodsModel.setOrderPluName(nRetGoods.getName());	
							for (DCP_OrderTransTaskGoodsRes.level2Spec oSpec_goods : goodsModel.getSpecs()) 
							{
								String specNO = oSpec_goods.getSpecNO();
								for (OSpec oSpec : nRetGoods.getSpecs()) 
								{
									String extendCode = oSpec.getExtendCode();
									if(specNO.equals(extendCode))
									{
										oSpec_goods.setOrderSpecNO(String.valueOf(oSpec.getSpecId()));
										oSpec_goods.setOrderSpecName(oSpec.getName());
										break;
									}
						
					      }
								
					
				      }
							
							
							this.SaveGoodsLocal(eId, shopId, loadDocType, orderShopNO, goodsModel);
							
						}
										 									
					}
					else
					{
						transTaskDescription = errorMessage.toString();
					}
				}
				catch (Exception e)
				{
					trans_flg = "1";
					transTaskDescription= "饿了么接口调用失败:"+e.getMessage();
				}
			}
			else
			{
				trans_flg = "1";
				transTaskDescription="("+pluName+")商品已经存在或找不到对应的平台分类";			
			}
		}
		catch (Exception e) 
		{
			HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步商品】【新增】异常:"+e.getMessage()+" 任务ID："
					+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+shopId+" 商品pluNO："+pluNO, elmLogFileName);
			transTaskDescription=e.getMessage();
		}
		finally 
		{
			UpdateTransTask(trans_flg,transTaskDescription,eId,shopId,loadDocType,transID) ;	
			HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步商品】【新增】任务完成！【更新任务表】完成!任务状态TRANS_FLG:"+trans_flg+" 任务ID："
					+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+shopId+" 商品pluNO："+pluNO, elmLogFileName);
		}

	}

	/**
	 * 商品更新
	 * @param transType
	 * @param transID
	 * @param eId
	 * @param shopId
	 * @param loadDocType
	 * @param goodsModel
	 * @throws Exception
	 */
	private void CallGoodsUpdateAPIFun(String transType,String transID,String eId,String shopId,String loadDocType,String orderShopNO,DCP_OrderTransTaskGoodsRes.level1Elm goodsModel) throws Exception
	{		
		String pluNO = goodsModel.getPluNO();
		String pluName = goodsModel.getPluName();
		String orderPluNO = goodsModel.getOrderPluNO();
		String orderPluName = goodsModel.getOrderPluName();			
		String orderCategoryNO = goodsModel.getOrderCategoryNO();
		String description = goodsModel.getDescription();
		String fileName= goodsModel.getFileName(); 
		String elmHash = goodsModel.getElmHash(); 
		String unit = goodsModel.getUnit();
		String isAllTimeSell = goodsModel.getIsAllTimeSell();
		String beginDate = goodsModel.getBeginDate();
		String endDate = goodsModel.getEndDate();
		String sellWeek = goodsModel.getSellWeek();
		String sellTime = goodsModel.getSellTime();
		String trans_flg = "1";
		String transTaskDescription="";
		try 
		{
			if (!Check.Null(orderPluNO) &&!Check.Null(orderPluName)&&!Check.Null(orderCategoryNO))
			{
				List<Map<OItemUpdateProperty,Object>> propertiesList = new ArrayList<Map<OItemUpdateProperty,Object>>();
				Map<OItemUpdateProperty,Object> properties = new HashMap<OItemUpdateProperty,Object>();
				List<OMaterial> oMaterials = new ArrayList<OMaterial>();
				if ( !Check.Null(goodsModel.getMaterial1()) && !Check.Null(goodsModel.getMaterialID1()))
				{
					OMaterial oMaterial = new OMaterial();
					oMaterial.setId(Long.valueOf(goodsModel.getMaterialID1()));
					oMaterial.setName(goodsModel.getMaterial1());
					oMaterials.add(oMaterial);
				}
				if ( !Check.Null(goodsModel.getMaterial2()) && !Check.Null(goodsModel.getMaterialID2()))
				{
					OMaterial oMaterial = new OMaterial();
					oMaterial.setId(Long.valueOf(goodsModel.getMaterialID2()));
					oMaterial.setName(goodsModel.getMaterial2());
					oMaterials.add(oMaterial);
				}
				if ( !Check.Null(goodsModel.getMaterial3()) && !Check.Null(goodsModel.getMaterialID3()))
				{
					OMaterial oMaterial = new OMaterial();
					oMaterial.setId(Long.valueOf(goodsModel.getMaterialID3()));
					oMaterial.setName(goodsModel.getMaterial3());
					oMaterials.add(oMaterial);
				}
				if ( !Check.Null(goodsModel.getMaterial4()) && !Check.Null(goodsModel.getMaterialID4()))
				{
					OMaterial oMaterial = new OMaterial();
					oMaterial.setId(Long.valueOf(goodsModel.getMaterialID4()));
					oMaterial.setName(goodsModel.getMaterial4());
					oMaterials.add(oMaterial);
				}
				if ( !Check.Null(goodsModel.getMaterial5()) && !Check.Null(goodsModel.getMaterialID5()))
				{
					OMaterial oMaterial = new OMaterial();
					oMaterial.setId(Long.valueOf(goodsModel.getMaterialID5()));
					oMaterial.setName(goodsModel.getMaterial5());
					oMaterials.add(oMaterial);
				}
				if ( !Check.Null(goodsModel.getMaterial6()) && !Check.Null(goodsModel.getMaterialID6()))
				{
					OMaterial oMaterial = new OMaterial();
					oMaterial.setId(Long.valueOf(goodsModel.getMaterialID6()));
					oMaterial.setName(goodsModel.getMaterial6());
					oMaterials.add(oMaterial);
				}
				if ( !Check.Null(goodsModel.getMaterial7()) && !Check.Null(goodsModel.getMaterialID7()))
				{
					OMaterial oMaterial = new OMaterial();
					oMaterial.setId(Long.valueOf(goodsModel.getMaterialID7()));
					oMaterial.setName(goodsModel.getMaterial7());
					oMaterials.add(oMaterial);
				}
				if ( !Check.Null(goodsModel.getMaterial8()) && !Check.Null(goodsModel.getMaterialID8()))
				{
					OMaterial oMaterial = new OMaterial();
					oMaterial.setId(Long.valueOf(goodsModel.getMaterialID8()));
					oMaterial.setName(goodsModel.getMaterial8());
					oMaterials.add(oMaterial);
				}
				if ( !Check.Null(goodsModel.getMaterial9()) && !Check.Null(goodsModel.getMaterial9()))
				{
					OMaterial oMaterial = new OMaterial();
					oMaterial.setId(Long.valueOf(goodsModel.getMaterialID9()));
					oMaterial.setName(goodsModel.getMaterial9());
					oMaterials.add(oMaterial);
				}
				if ( !Check.Null(goodsModel.getMaterial10()) && !Check.Null(goodsModel.getMaterialID10()))
				{
					OMaterial oMaterial = new OMaterial();
					oMaterial.setId(Long.valueOf(goodsModel.getMaterialID10()));
					oMaterial.setName(goodsModel.getMaterial10());
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

				//新增商品的时候，如果有图片，先判断有没有获取过图片hash值了，有了就不用调用接口
				if(!Check.Null(fileName) && Check.Null(elmHash))
				{
					elmHash=GetHash(eId, shopId,loadDocType,pluNO,fileName) ;
				}			
				properties.put(OItemUpdateProperty.name,pluName);
				properties.put(OItemUpdateProperty.description,description);
				properties.put(OItemUpdateProperty.imageHash,elmHash);
				properties.put(OItemUpdateProperty.unit,unit);

				if (isAllTimeSell.equals("N"))
				{
					OItemSellingTime oItemSellingTime = new OItemSellingTime();
					if (Check.Null(beginDate)) beginDate="2000-12-31";
					if (Check.Null(endDate)) endDate="2060-12-31";
					oItemSellingTime.setBeginDate(beginDate);
					oItemSellingTime.setEndDate(endDate);				
					List <OItemWeekEnum > weekEnum = new ArrayList<>();
					if(sellWeek!=null&&sellWeek.isEmpty()==false)
					{									
						for (String week: sellWeek.split(",")){
							if (week.equals("1")) weekEnum.add(OItemWeekEnum.MONDAY);
							else if (week.equals("2")) weekEnum.add(OItemWeekEnum.TUESDAY);
							else if (week.equals("3")) weekEnum.add(OItemWeekEnum.WEDNESDAY);
							else if (week.equals("4")) weekEnum.add(OItemWeekEnum.THURSDAY);
							else if (week.equals("5")) weekEnum.add(OItemWeekEnum.FRIDAY);
							else if (week.equals("6")) weekEnum.add(OItemWeekEnum.SATURDAY);
							else if (week.equals("7")) weekEnum.add(OItemWeekEnum.SUNDAY);		           
						}
					}
					else
					{
						weekEnum.add(OItemWeekEnum.MONDAY);
						weekEnum.add(OItemWeekEnum.TUESDAY);
						weekEnum.add(OItemWeekEnum.WEDNESDAY);
						weekEnum.add(OItemWeekEnum.THURSDAY);
						weekEnum.add(OItemWeekEnum.FRIDAY);
						weekEnum.add(OItemWeekEnum.SATURDAY);
						weekEnum.add(OItemWeekEnum.SUNDAY);					 
					}
					oItemSellingTime.setWeeks(weekEnum);
					List<OItemTime> times = new ArrayList<OItemTime>();
					if(sellTime!=null&&sellTime.isEmpty()==false)
					{					
						String[] ssTime = sellTime.split(",");//16:00-19:00,20:00-23:00
						for (String timeStr : ssTime) 
						{
							String[] ss1 = timeStr.split("-");//16:00-19:00						  			
							OItemTime oItemTime = new OItemTime();
							oItemTime.setBeginTime(ss1[0].substring(0,5));
							oItemTime.setEndTime(ss1[1].substring(0,5));
							times.add(oItemTime);	
						}					  		
					}
					else
					{
						OItemTime oItemTime = new OItemTime();
						oItemTime.setBeginTime("00:00");
						oItemTime.setEndTime("23:59");
						times.add(oItemTime);	
					}
					oItemSellingTime.setTimes(times);
					properties.put(OItemUpdateProperty.sellingTime, oItemSellingTime);
				}

				List<OSpec> oSpecs = new ArrayList<OSpec>();

				for (DCP_OrderTransTaskGoodsRes.level2Spec oneData : goodsModel.getSpecs()) 
				{																
					String specNO = oneData.getSpecNO();
					String specName = oneData.getSpecName();
					String orderSpecNO=oneData.getOrderSpecNO();
					String price = oneData.getPrice();
					String stockQty = oneData.getStockQty();
					String packageFee = oneData.getPackageFee();
					String isOnShelf = oneData.getIsOnShelf();
					String maxStockQty =oneData.getMaxStockQty();
					String netWeight =oneData.getNetWeight();

					int onShelf_i = 1;							
					if (isOnShelf != null && isOnShelf.equals("N"))
					{
						onShelf_i = 0;
					}
					double price_d = 0;
					try
					{
						price_d = Double.parseDouble(price);
					}
					catch(Exception e)
					{
						price_d = 0;
					}
					double packageFee_d = 0;
					try
					{
						packageFee_d = Double.parseDouble(packageFee);
					}
					catch(Exception e)
					{
						packageFee_d = 0;
					}
					int stockQty_i = 0;
					try
					{
						stockQty_i = Integer.parseInt(stockQty);
					}
					catch(Exception e)
					{
						stockQty_i = 0;
					}
					int maxStockQty_i = 0;
					try
					{
						maxStockQty_i = Integer.parseInt(maxStockQty);
					}
					catch(Exception e)
					{
						maxStockQty_i = 10000;
					}
					int netWeight_i = 0;
					try
					{
						netWeight_i = Integer.parseInt(netWeight);
					}
					catch(Exception e)
					{
						maxStockQty_i = 10000;
					}

					OSpec oSpec = new OSpec();				
					if (Check.Null(orderSpecNO))
					{
						oSpec.setSpecId(0);  //饿了么接口说明：此处创建的时候，specId不需要填写，如果填写0，也可以正常创建		  
					}
					else 
					{
						oSpec.setSpecId(Long.valueOf(orderSpecNO));       
					}
					oSpec.setExtendCode(specNO);
					oSpec.setName(specName);		
					oSpec.setPrice(price_d);				
					oSpec.setStock(stockQty_i);
					oSpec.setPackingFee(packageFee_d);
					oSpec.setMaxStock(maxStockQty_i);
					oSpec.setOnShelf(onShelf_i);
					oSpec.setWeight(netWeight_i);
					oSpecs.add(oSpec);
				}
				properties.put(OItemUpdateProperty.specs,oSpecs);	

				///属性添加： 甜度    6分甜，7分甜，8分甜     
				if (goodsModel.getAttr()!= null && goodsModel.getAttr().size()>0)
				{
					List<OItemAttribute> oItemAttributes = new ArrayList<OItemAttribute>();		
					for (DCP_OrderTransTaskGoodsRes.level2Attr oneData : goodsModel.getAttr()) 
					{		
						OItemAttribute oItemAttribute = new OItemAttribute();
						List<String> attrValueList = new ArrayList<String>();
						String attrName = oneData.getAttrName();
						String attrValue = oneData.getAttrValue();
						String[] ssAttrValue = attrValue.split(","); //7分甜，6分甜，8分甜
						for (String attrValueStr : ssAttrValue) 
						{
							attrValueList.add(attrValueStr);
						}					  		
						oItemAttribute.setName(attrName);
						oItemAttribute.setDetails(attrValueList);
						oItemAttributes.add(oItemAttribute);
					}
					properties.put(OItemUpdateProperty.attributes,oItemAttributes);	
				}
				propertiesList.add(properties);


				//查询下当前门店shopId的对应的饿了么APPKEY
				Map<String, Object> mapAppKey = PosPub.getWaimaiAppConfigByShopNO_New(StaticInfo.dao, eId, shopId, loadDocType,"");
				Boolean isGoNewFunction = false;//是否走新的接口
				String elmAPPKey = "";
				String elmAPPSecret = "";
				String elmAPPName = "";			
				boolean elmIsSandbox = false;
				if (mapAppKey != null)
				{
					elmAPPKey = mapAppKey.get("APPKEY").toString();
					elmAPPSecret = mapAppKey.get("APPSECRET").toString();
					elmAPPName = mapAppKey.get("APPNAME").toString();
					String	elmIsTest = mapAppKey.get("ISTEST").toString();					
					if (elmIsTest != null && elmIsTest.equals("Y"))
					{
						elmIsSandbox = true;
					}
					isGoNewFunction = true;
				}

				StringBuilder errorMessage = new StringBuilder();
				OItem nRet = null;
				try
				{
					if(isGoNewFunction)
					{
						nRet = WMELMProductService.updateItemTransTask(elmIsSandbox,elmAPPKey,elmAPPSecret,elmAPPName,new Long(orderPluNO), new Long(orderCategoryNO), properties, errorMessage);
					}
					else
					{
						nRet = WMELMProductService.updateItemTransTask(new Long(orderPluNO), new Long(orderCategoryNO), properties, errorMessage);
					}

					if(nRet != null) 
					{
						trans_flg = "2";
					  //回写返回的平台ID
						
						
						goodsModel.setOrderPluNO(String.valueOf(nRet.getId()));
						goodsModel.setOrderPluName(nRet.getName());	
						for (DCP_OrderTransTaskGoodsRes.level2Spec oSpec_goods : goodsModel.getSpecs()) 
						{						
							String specNO = oSpec_goods.getSpecNO();
							for (OSpec oSpec : nRet.getSpecs()) 
							{
								String extendCode = oSpec.getExtendCode();
								if(specNO.equals(extendCode))
								{
									oSpec_goods.setOrderSpecNO(String.valueOf(oSpec.getSpecId()));
									oSpec_goods.setOrderSpecName(oSpec.getName());
									break;
								}
					
				      }				
			      }
						
						this.SaveGoodsLocal(eId, shopId, loadDocType, orderShopNO, goodsModel);
						//排序
						this.SetCategoryGoodsSequence(eId, shopId, loadDocType, orderShopNO, goodsModel);
																								
					}
					else
					{
						transTaskDescription = errorMessage.toString();
					}
				}
				catch (Exception e)
				{
					trans_flg = "1";
					transTaskDescription= "饿了么接口调用失败:"+e.getMessage();
				}
			}
			else
			{
				trans_flg = "1";
				transTaskDescription="("+pluName+")平台商品不存在或找不到对应的平台分类";			
			}
		}
		catch (Exception e) 
		{
			HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步商品】【修改】异常:"+e.getMessage()+" 任务ID："
					+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+shopId+" 商品pluNO："+pluNO, elmLogFileName);
			transTaskDescription=e.getMessage();
		}
		finally 
		{
			UpdateTransTask(trans_flg,transTaskDescription,eId,shopId,loadDocType,transID) ;	
			HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步商品】【修改】任务完成！【更新任务表】完成!任务状态TRANS_FLG:"+trans_flg+" 任务ID："
					+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+shopId+" 商品pluNO："+pluNO, elmLogFileName);
		}
	}

	/**
	 * 商品删除
	 * @param transType
	 * @param transID
	 * @param eId
	 * @param shopId
	 * @param loadDocType
	 * @param goodsModel
	 * @throws Exception
	 */
	private void CallGoodsDeleteAPIFun(String transType,String transID,String eId,String shopId,String loadDocType,String orderShopNO,DCP_OrderTransTaskGoodsRes.level1Elm goodsModel) throws Exception
	{
		List<Long> itemIds = new ArrayList<Long>();
		String pluNO = goodsModel.getPluNO();
		String pluName = goodsModel.getPluName();
		String orderPluNO = goodsModel.getOrderPluNO();
		String orderPluName = goodsModel.getOrderPluName();			
		String orderCategoryNO = goodsModel.getOrderCategoryNO();
		String trans_flg = "1";
		String transTaskDescription="";
		try 
		{
			if (!Check.Null(orderPluNO) &&!Check.Null(orderPluName)&&!Check.Null(orderCategoryNO))
			{
				itemIds.add(Long.parseLong(orderPluNO));
				//查询下当前门店shopId的对应的饿了么APPKEY
				Map<String, Object> mapAppKey = PosPub.getWaimaiAppConfigByShopNO_New(StaticInfo.dao, eId, shopId, loadDocType,"");
				Boolean isGoNewFunction = false;//是否走新的接口
				String elmAPPKey = "";
				String elmAPPSecret = "";
				String elmAPPName = "";			
				boolean elmIsSandbox = false;
				if (mapAppKey != null)
				{
					elmAPPKey = mapAppKey.get("APPKEY").toString();
					elmAPPSecret = mapAppKey.get("APPSECRET").toString();
					elmAPPName = mapAppKey.get("APPNAME").toString();
					String	elmIsTest = mapAppKey.get("ISTEST").toString();					
					if (elmIsTest != null && elmIsTest.equals("Y"))
					{
						elmIsSandbox = true;
					}
					isGoNewFunction = true;
				}

				StringBuilder errorMessage = new StringBuilder();
				Map<Long,OItem> nRet = null;
				try 
				{
					if(isGoNewFunction)
					{
						nRet = WMELMProductService.batchRemoveItems(elmIsSandbox,elmAPPKey,elmAPPSecret,elmAPPName,itemIds, errorMessage);
					}
					else
					{
						nRet = WMELMProductService.batchRemoveItems(itemIds, errorMessage);
					}
					if(nRet!= null ) 
					{
						trans_flg = "2";
						this.DeleteGoodsLocal(eId, shopId, loadDocType, orderShopNO, goodsModel);
					}
          else
	        {       	 
	          transTaskDescription = errorMessage.toString();
	        }
				}
				catch (Exception e)
				{
					trans_flg = "1";
					transTaskDescription= "饿了么接口调用失败:"+e.getMessage();
				}
			}
			else {			
				trans_flg = "1";
				transTaskDescription="("+pluName+")平台商品不存在或找不到对应的平台分类";			
			}
		}
		catch (Exception e) 
		{
			HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步商品】【删除】异常:"+e.getMessage()+" 任务ID："
					+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+shopId+" 商品pluNO："+pluNO, elmLogFileName);
			transTaskDescription=e.getMessage();
		}
		finally 
		{
			UpdateTransTask(trans_flg,transTaskDescription,eId,shopId,loadDocType,transID) ;	
			HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步商品】【删除】任务完成！【更新任务表】完成!任务状态TRANS_FLG:"+trans_flg+" 任务ID："
					+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+shopId+" 商品pluNO："+pluNO, elmLogFileName);
		}
	}


	private List<Map<String, Object>> GetTransTask(String loadDocType) throws Exception
	{
		String sql = " SELECT * FROM OC_TRANSTASK WHERE status='100' and trans_flg='0' and load_doctype='"+loadDocType+"'  "
				+ " order by trans_date,trans_time,trans_type ";
		List<Map<String, Object>> transTask = this.doQueryData(sql, null);
		return transTask;
	}

	private List<Map<String, Object>> GetTransTask_Category(String loadDocType) throws Exception
	{
		String sql = " select a.*,b.categoryno,b.categoryname,b.priority,b.order_categoryno,b.order_categoryname  "
				+ " from OC_transtask a "
				+ " inner join OC_transtask_category b on a.EID=b.EID and a.SHOPID=b.SHOPID and a.trans_id=b.trans_id "
				+ " where a.trans_flg='0' and a.load_doctype='"+loadDocType+"' and a.TRANS_TYPE in ('1','2','3') "
				+ " order by  a.trans_date,a.trans_time,a.trans_type " ;

		List<Map<String, Object>> transTask_Category = this.doQueryData(sql, null);
		return transTask_Category;
	}

	private List<Map<String, Object>> GetTransTask_Goods(String loadDocType) throws Exception{
		String sql = "select a.trans_type,a.order_shop,a.order_shopname, "
				+ " b.*,c.specno,c.specname,c.order_specno,c.order_specname,c.price,c.stockqty,c.packagefee,c.isonshelf,c.netweight, "
				+ " d.attrname,d.attrvalue,d.order_attrname,d.order_attrvalue "
				+ " from OC_transtask a "
				+ " inner join OC_transtask_goods b      on a.EID=b.EID and a.SHOPID=b.SHOPID and a.trans_id=b.trans_id "
				+ " inner join OC_transtask_goods_spec c on a.EID=c.EID and a.SHOPID=c.SHOPID and a.trans_id=c.trans_id "
				+ " left join OC_transtask_goods_attr d  on a.EID=d.EID and a.SHOPID=d.SHOPID and a.trans_id=d.trans_id "
				+ " where a.trans_flg='0' and a.load_doctype='"+loadDocType+"' and a.trans_type in ('4','5','6') " ;

		List<Map<String, Object>> GetTransTask_Goods = this.doQueryData(sql, null);
		return GetTransTask_Goods;
	}

	private void UpdateTransTask(String trans_flg,String description,String eId,String shopId,String loadDocType,String transID) throws Exception{

		Map<String, DataValue> values = new HashMap<String, DataValue>();
		values.put("TRANS_FLG", new DataValue(trans_flg, Types.VARCHAR));
		values.put("DESCRIPTION", new DataValue(description, Types.VARCHAR));
		values.put("UPDATE_TIME",  new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()) , Types.VARCHAR));		
		// condition
		Map<String, DataValue> conditions = new HashMap<String, DataValue>();
		conditions.put("EID", new DataValue(eId, Types.VARCHAR));
		conditions.put("SHOPID", new DataValue(shopId, Types.VARCHAR));
		conditions.put("LOAD_DOCTYPE", new DataValue(loadDocType, Types.VARCHAR));
		conditions.put("TRANS_ID", new DataValue(transID, Types.VARCHAR));

		this.doUpdate("OC_TRANSTASK", values, conditions);


	}


	private String GetHash(String eId,String shopId,String loadDocType,String pluNO,String fileName) throws Exception
	{	
		String imageHash = "";
		try 
		{
			String sql = " select * from OC_GOODS  where  ELMHASH IS NOT NULL AND EID='"+eId+"' and pluno='"+ pluNO +"' and FILENAME='"+fileName+"'";
			List<Map<String, Object>> imageHash_db = this.doQueryData(sql, null);
			if(imageHash_db!=null&&imageHash_db.isEmpty()==false)
			{
				imageHash = imageHash_db.get(0).get("ELMHASH").toString();
				HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步商品】【新增】【【数据库】获取商品图片的HASH值】成功，图片Hash值："+imageHash+" 文件名："
						+ fileName + " 商品pluNO："+pluNO, elmLogFileName);
				return imageHash;
			}
		
		} 
		catch (Exception e) 
		{
		// TODO: handle exception
	
		}
		
		
		Map<String, Object> mapAppKey = PosPub.getWaimaiAppConfigByShopNO_New(StaticInfo.dao, eId, shopId, loadDocType,"");
		Boolean isGoNewFunction = false;//是否走新的接口
		String elmAPPKey = "";
		String elmAPPSecret = "";
		String elmAPPName = "";			
		boolean elmIsSandbox = false;
		if (mapAppKey != null)
		{
			elmAPPKey = mapAppKey.get("APPKEY").toString();
			elmAPPSecret = mapAppKey.get("APPSECRET").toString();
			elmAPPName = mapAppKey.get("APPNAME").toString();
			String	elmIsTest = mapAppKey.get("ISTEST").toString();					
			if (elmIsTest != null && elmIsTest.equals("Y"))
			{
				elmIsSandbox = true;
			}
			isGoNewFunction = true;
		}

		try 
		{
			//获取图片hash值
			String surl=PosPub.getDCP_URL(eId);
			surl=surl.replace("sposWeb/services/jaxrs/sposService/invoke", "ordergoods");
			surl+="/"+fileName;
			StringBuilder errorMessageImage = new StringBuilder();

			HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步商品】【新增】【获取商品图片的HASH值】开始，图片URL地址："+surl+" 文件名："
					+ fileName +" 商品pluNO："+pluNO, elmLogFileName);

			if(isGoNewFunction)
			{
				imageHash = WMELMProductService.uploadImageWithRemoteUrl(elmIsSandbox,elmAPPKey,elmAPPSecret,elmAPPName, surl, errorMessageImage);
			}
			else
			{
				imageHash =	WMELMProductService.uploadImageWithRemoteUrl(surl, errorMessageImage);
			}
			if (imageHash != null && imageHash.isEmpty() == false)				
			{
				HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步商品】【新增】【获取商品图片的HASH值】成功，图片Hash值："+imageHash+" 文件名："
						+ fileName + " 商品pluNO："+pluNO, elmLogFileName);
				try 
				{
					UptBean ub_imageHash = null;	
					ub_imageHash = new UptBean("OC_GOODS");
					ub_imageHash.addUpdateValue("ELMHASH", new DataValue(imageHash,Types.VARCHAR));
					ub_imageHash.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

					ub_imageHash.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					ub_imageHash.addCondition("PLUNO", new DataValue(pluNO, Types.VARCHAR));
					ub_imageHash.addCondition("FILENAME", new DataValue(fileName, Types.VARCHAR));//公司别不一样，同一个pluno对应的图片名称加上了公司别
					ArrayList<DataProcessBean> DPBImageHash = new ArrayList<DataProcessBean>();
					DPBImageHash.add(new DataProcessBean(ub_imageHash));

					HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步商品】【新增】【获取商品图片的HASH值后回写OC_goods】开始，图片Hash值："+imageHash+" 文件名："
							+ fileName + " 商品pluNO："+pluNO, elmLogFileName);
					this.doExecuteDataToDB(DPBImageHash);
					HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步商品】【新增】【获取商品图片的HASH值后回写OC_goods】成功，图片Hash值："+imageHash+" 文件名："
							+ fileName + " 商品pluNO："+pluNO, elmLogFileName);						
				} 
				catch (Exception e) 
				{
					HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步商品】【新增】【获取商品图片的HASH值回写OC_goods】异常："+e.getMessage()+" 图片URL地址："+surl+" 文件名："
							+ fileName + " 商品pluNO："+pluNO, elmLogFileName);										
				}
			}
		}
		catch (Exception e) 
		{
			HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步商品】【新增】【获取商品图片的HASH值】异常："+e.getMessage()+" 文件名："
					+ fileName + " 商品pluNO："+pluNO, elmLogFileName);		
		}
		return 	imageHash ;
	}


	private void SaveGoodsLocal(String eId,String shopId,String loadDocType,String orderShopNO,DCP_OrderTransTaskGoodsRes.level1Elm goodsModel) throws Exception
	{
		
	  ArrayList<DataProcessBean> DPB = new ArrayList<DataProcessBean>();
		String orderPluNO = goodsModel.getOrderPluNO();
		String orderPlunName = goodsModel.getOrderPluName() == null ? "" : goodsModel.getOrderPluName();
		String pluNO = goodsModel.getPluNO()== null ? " " : goodsModel.getPluNO();
		try 
		{	  						
			String orderCategoryNO = goodsModel.getOrderCategoryNO()== null ? "" : goodsModel.getOrderCategoryNO();
			String orderCategoryName = goodsModel.getOrderCategoryName()== null ? "" : goodsModel.getOrderCategoryName();
			String orderDescription = goodsModel.getDescription()== null ? "" : goodsModel.getDescription();
			String orderImageUrl = goodsModel.getFileName()== null ? "" : goodsModel.getFileName();
			String orderUnit = goodsModel.getUnit()== null ? "" : goodsModel.getUnit();		
			String pluName = goodsModel.getPluName()== null ? "" : goodsModel.getPluName();	
			String categoryNO =goodsModel.getCategoryNO();
			String categoryName =goodsModel.getCategoryName();
			
			
			
			
			
			if (orderPluNO == null || orderPluNO.length() == 0)
			{
				orderPluNO = " ";//主键
			}
			
			if(pluNO.length()==0)
			{
				pluNO = " ";//主键
			}
			
			if(categoryNO.length()==0)
			{
				categoryNO = " ";
			}
			
			if(orderCategoryNO.length()==0)
			{
				orderCategoryNO = " ";
			}
			
		  //先删 在插入
			String execsql1 = "delete from OC_MAPPINGGOODS where EID='"+eId+"' and SHOPID='"+shopId+"' and LOAD_DOCTYPE='"+loadDocType+"' and PLUNO='"+pluNO+"'";
			String execsql2 = "delete from OC_MAPPINGGOODS_SPEC where EID='"+eId+"' and SHOPID='"+shopId+"' and LOAD_DOCTYPE='"+loadDocType+"' and PLUNO='"+pluNO+"'";
			String execsql3 = "delete from OC_MAPPINGGOODS_ATTR where EID='"+eId+"' and SHOPID='"+shopId+"' and LOAD_DOCTYPE='"+loadDocType+"' and PLUNO='"+pluNO+"'";

			ExecBean exc1 = new ExecBean(execsql1);
			ExecBean exc2 = new ExecBean(execsql2);
			ExecBean exc3 = new ExecBean(execsql3);
			DPB.add(new DataProcessBean(exc1));
			DPB.add(new DataProcessBean(exc2));
			DPB.add(new DataProcessBean(exc3));
			
			

			List<DCP_OrderTransTaskGoodsRes.level2Spec> specs = goodsModel.getSpecs();
			for (DCP_OrderTransTaskGoodsRes.level2Spec oneDataSpec : specs) 
			{
				String orderSpecID = oneDataSpec.getOrderSpecNO();
				String orderSpecName = oneDataSpec.getOrderSpecName() == null ? "" : oneDataSpec.getOrderSpecName();
				String orderPrice = 	oneDataSpec.getPrice() == null?"0":	oneDataSpec.getPrice();
				String orderStock = oneDataSpec.getStockQty() == null?"0": oneDataSpec.getStockQty() ;
				String orderPackingFee = oneDataSpec.getPackageFee()== null?"0": oneDataSpec.getPackageFee();
				String orderOnShelf = oneDataSpec.getIsOnShelf()== null?"N": oneDataSpec.getIsOnShelf();
				String pluBarcode = oneDataSpec.getSpecNO() == null?" ":oneDataSpec.getSpecNO();//主键不能为空
				String pluSpecName = oneDataSpec.getSpecName() == null?"":oneDataSpec.getSpecName();

				if(orderStock.length()==0)
				{
					orderStock = "9999";
				}

				if(orderSpecID.length()==0)
				{
					orderSpecID = " ";
				}

				if(pluBarcode.length()==0)
				{
					pluBarcode = " ";
				}

				


				String[] columns2 = { "EID", "LOAD_DOCTYPE", "SHOPID", "ORGANIZATIONNO", "PLUNO", "SPECNO","SPECNAME",
						"ORDER_SHOP","ORDER_SHOPNAME", "ORDER_PLUNO", "ORDER_SPECNO", "ORDER_SPECNAME","PRICE", "STOCKQTY","PACKAGEFEE","ISONSHELF", "STATUS","NETWEIGHT" };
				DataValue[] insValue2 = null;
				insValue2 = new DataValue[]{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(loadDocType, Types.VARCHAR),//1.饿了么 2.美团外卖 3.微商城
						new DataValue(shopId, Types.VARCHAR),//组织编号=门店shopId编号
						new DataValue(shopId, Types.VARCHAR),//ERP门店shopId				
						new DataValue(pluNO, Types.VARCHAR),//商品编码
						new DataValue(pluBarcode, Types.VARCHAR),//ERP条码
						new DataValue(pluSpecName, Types.VARCHAR),//规格名称
						new DataValue(orderShopNO, Types.VARCHAR),//外卖平台门店shopIdID
						new DataValue("", Types.VARCHAR),//外卖平台门店shopId名称
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


			List<DCP_OrderTransTaskGoodsRes.level2Attr> attributes = goodsModel.getAttr();
			if (attributes != null && attributes.size() > 0)
			{				
				for (DCP_OrderTransTaskGoodsRes.level2Attr oneDataAttribute : attributes) 
				{
					String attriName = oneDataAttribute.getAttrName();
					String attriValue  = oneDataAttribute.getAttrValue();
																
					String[] columns3 = { "EID", "LOAD_DOCTYPE", "SHOPID", "ORGANIZATIONNO", "PLUNO", "ATTRNAME","ATTRVALUE",
							"ORDER_SHOP","ORDER_SHOPNAME", "ORDER_PLUNO", "ORDER_ATTRNAME", "ORDER_ATTRVALUE", "STATUS"};
					DataValue[] insValue2 = null;
					insValue2 = new DataValue[]{
							new DataValue(eId, Types.VARCHAR),
							new DataValue(loadDocType, Types.VARCHAR),//1.饿了么 2.美团外卖 3.微商城
							new DataValue(shopId, Types.VARCHAR),//组织编号=门店shopId编号
							new DataValue(shopId, Types.VARCHAR),//ERP门店shopId				
							new DataValue(pluNO, Types.VARCHAR),//商品编码
							new DataValue("", Types.VARCHAR),//属性名称
							new DataValue("", Types.VARCHAR),//属性值
							new DataValue(orderShopNO, Types.VARCHAR),//外卖平台门店shopIdID
							new DataValue("", Types.VARCHAR),//外卖平台门店shopId名称
							new DataValue(orderPluNO, Types.VARCHAR),//外卖平台商品ID
							new DataValue(attriName, Types.VARCHAR),//外卖平台属性名称
							new DataValue(attriValue, Types.VARCHAR),//外卖平台属性值
							new DataValue("100", Types.VARCHAR)						
					};

					InsBean ib3 = new InsBean("OC_MAPPINGGOODS_ATTR", columns3);
					ib3.addValues(insValue2);
					DPB.add(new DataProcessBean(ib3));		
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
					new DataValue(shopId, Types.VARCHAR),//组织编号=门店shopId编号
					new DataValue(shopId, Types.VARCHAR),//ERP门店shopId				
					new DataValue(pluNO, Types.VARCHAR),//ERP商品编码
					new DataValue(pluName, Types.VARCHAR),//ERP商品名称
					new DataValue(categoryNO, Types.VARCHAR),//ERP商品分类编码
					new DataValue(orderShopNO, Types.VARCHAR),//外卖平台门店shopIdID
					new DataValue("", Types.VARCHAR),//外卖平台门店shopId名称
					new DataValue(orderPluNO, Types.VARCHAR),//外卖平台商品ID
					new DataValue(orderPlunName, Types.VARCHAR),//外卖平台商品名称 
					new DataValue(orderCategoryNO, Types.VARCHAR),//
					new DataValue(orderCategoryName, Types.VARCHAR),//
					new DataValue(orderDescription, Types.VARCHAR),//	
					new DataValue(orderImageUrl, Types.VARCHAR),//					
					new DataValue(orderUnit, Types.VARCHAR),//
					new DataValue("0", Types.VARCHAR),//	
					new DataValue("100", Types.VARCHAR),//	
					new DataValue(goodsModel.getMaterialID1(), Types.VARCHAR),
					new DataValue(goodsModel.getMaterialID2(), Types.VARCHAR),
					new DataValue(goodsModel.getMaterialID3(), Types.VARCHAR),
					new DataValue(goodsModel.getMaterialID4(), Types.VARCHAR),
					new DataValue(goodsModel.getMaterialID5(), Types.VARCHAR),
					new DataValue(goodsModel.getMaterialID6(), Types.VARCHAR),
					new DataValue(goodsModel.getMaterialID7(), Types.VARCHAR),
					new DataValue(goodsModel.getMaterialID8(), Types.VARCHAR),
					new DataValue(goodsModel.getMaterialID9(), Types.VARCHAR),
					new DataValue(goodsModel.getMaterialID10(), Types.VARCHAR),
					new DataValue(goodsModel.getMaterial1(), Types.VARCHAR),
					new DataValue(goodsModel.getMaterial2(), Types.VARCHAR),
					new DataValue(goodsModel.getMaterial3(), Types.VARCHAR),
					new DataValue(goodsModel.getMaterial4(), Types.VARCHAR),
					new DataValue(goodsModel.getMaterial5(), Types.VARCHAR),
					new DataValue(goodsModel.getMaterial6(), Types.VARCHAR),
					new DataValue(goodsModel.getMaterial7(), Types.VARCHAR),
					new DataValue(goodsModel.getMaterial8(), Types.VARCHAR),
					new DataValue(goodsModel.getMaterial9(), Types.VARCHAR),
					new DataValue(goodsModel.getMaterial10(), Types.VARCHAR),						
					new DataValue(goodsModel.getIsAllTimeSell(), Types.VARCHAR),
					new DataValue(goodsModel.getBeginDate(), Types.VARCHAR),
					new DataValue(goodsModel.getEndDate(), Types.VARCHAR),
					new DataValue(goodsModel.getSellWeek(), Types.VARCHAR),
					new DataValue(goodsModel.getSellTime(), Types.VARCHAR)						
			};

			InsBean ib1 = new InsBean("OC_MAPPINGGOODS", columns1);
			ib1.addValues(insValue1);
			DPB.add(new DataProcessBean(ib1));	

		} 

		catch (Exception e) 
		{
			HelpTools.writelog_fileName("【同步商品】【新增/修改】开始组装SQL语句！有异常:"+e.getMessage()+" 平台类型LoadDocType:"+loadDocType+" 平台门店shopIdID："+orderShopNO+" 门店shopId:"+shopId+" 异常的商品平台ID/名称："+orderPluNO+"/"+orderPlunName, elmLogFileName);
			return;
		}


		

		try 
		{
			HelpTools.writelog_fileName("【同步商品】【新增/修改】开始执行SQL语句！ 平台类型LoadDocType:"+loadDocType+" 平台门店shopIdID："+orderShopNO+" 门店shopId:"+shopId+" 商品pluNO："+pluNO+" 平台商品orderpluNO："+orderPluNO, elmLogFileName);  
			this.doExecuteDataToDB(DPB);
			HelpTools.writelog_fileName("【同步商品】【新增/修改】开始执行SQL语句！成功！ 平台类型LoadDocType:"+loadDocType+" 平台门店shopIdID："+orderShopNO+" 门店shopId:"+shopId+" 商品pluNO："+pluNO+" 平台商品orderpluNO："+orderPluNO, elmLogFileName);  

		} 
		catch (SQLSyntaxErrorException e)
		{
			HelpTools.writelog_fileName("【同步商品】【新增/修改】开始执行SQL语句！异常："+e.getMessage()+" 平台类型LoadDocType:"+loadDocType+" 平台门店shopIdID："+orderShopNO+" 门店shopId:"+shopId+" 商品pluNO："+pluNO+" 平台商品orderpluNO："+orderPluNO, elmLogFileName); 		
		}
		catch (Exception e) 
		{
			HelpTools.writelog_fileName("【同步商品】【新增/修改】开始执行SQL语句！异常："+e.getMessage()+" 平台类型LoadDocType:"+loadDocType+" 平台门店shopIdID："+orderShopNO+" 门店shopId:"+shopId+" 商品pluNO："+pluNO+" 平台商品orderpluNO："+orderPluNO, elmLogFileName); 		
		}
		
		
		
		
	}

	
	private void DeleteGoodsLocal(String eId,String shopId,String loadDocType,String orderShopNO,DCP_OrderTransTaskGoodsRes.level1Elm goodsModel) throws Exception
	{
		ArrayList<DataProcessBean> DPB = new ArrayList<DataProcessBean>();
		String orderPluNO = goodsModel.getOrderPluNO();
		String pluNO = goodsModel.getPluNO();
		
		try 
		{		
		 //先删 在插入
			String execsql1 = "delete from OC_MAPPINGGOODS where EID='"+eId+"' and SHOPID='"+shopId+"' and LOAD_DOCTYPE='"+loadDocType+"' and PLUNO='"+pluNO+"'";
			String execsql2 = "delete from OC_MAPPINGGOODS_SPEC where EID='"+eId+"' and SHOPID='"+shopId+"' and LOAD_DOCTYPE='"+loadDocType+"' and PLUNO='"+pluNO+"'";
			String execsql3 = "delete from OC_MAPPINGGOODS_ATTR where EID='"+eId+"' and SHOPID='"+shopId+"' and LOAD_DOCTYPE='"+loadDocType+"' and PLUNO='"+pluNO+"'";

			ExecBean exc1 = new ExecBean(execsql1);
			ExecBean exc2 = new ExecBean(execsql2);
			ExecBean exc3 = new ExecBean(execsql3);
			DPB.add(new DataProcessBean(exc1));
			DPB.add(new DataProcessBean(exc2));
			DPB.add(new DataProcessBean(exc3));
			HelpTools.writelog_fileName("【同步商品】【删除】开始执行SQL语句！ 平台类型LoadDocType:"+loadDocType+" 平台门店shopIdID："+orderShopNO+" 门店shopId:"+shopId+" 商品pluNO："+pluNO+" 平台商品orderpluNO："+orderPluNO, elmLogFileName);  
			this.doExecuteDataToDB(DPB);
			HelpTools.writelog_fileName("【同步商品】【删除】开始执行SQL语句！成功！ 平台类型LoadDocType:"+loadDocType+" 平台门店shopIdID："+orderShopNO+" 门店shopId:"+shopId+" 商品pluNO："+pluNO+" 平台商品orderpluNO："+orderPluNO, elmLogFileName);  

		} 
		catch (SQLSyntaxErrorException e)
		{
			HelpTools.writelog_fileName("【同步商品】【删除】开始执行SQL语句！异常："+e.getMessage()+" 平台类型LoadDocType:"+loadDocType+" 平台门店shopIdID："+orderShopNO+" 门店shopId:"+shopId+" 商品pluNO："+pluNO+" 平台商品orderpluNO："+orderPluNO, elmLogFileName); 		
		}
		catch (Exception e) 
		{
			HelpTools.writelog_fileName("【【同步商品】【删除】开始执行SQL语句！异常："+e.getMessage()+" 平台类型LoadDocType:"+loadDocType+" 平台门店shopIdID："+orderShopNO+" 门店shopId:"+shopId+" 商品pluNO："+pluNO+" 平台商品orderpluNO："+orderPluNO, elmLogFileName); 		
		}
		
	}

	
	private void SaveCatesLocal(String eId,String shopId,String loadDocType,String orderShopNO,DCP_OrderPlatformCategoryQueryRes.level1Elm catesModel) throws Exception
	{
		String categoryNO = catesModel.getCategoryNO();
		String categoryName = catesModel.getCategoryName();
		String orderCategoryNO= catesModel.getOrderCategoryNO();
		String orderCategoryName=catesModel.getOrderCategoryName();
		String orderPriority = catesModel.getOrderPriority();
		ArrayList<DataProcessBean> DPB = new ArrayList<DataProcessBean>();
		//先删 在插入
		String execsql1 = "delete from OC_MAPPINGCATEGORY where EID='"+eId+"' and LOAD_DOCTYPE='"+loadDocType+"' and ORDER_SHOP='"+orderShopNO+"' and CATEGORYNO='"+categoryNO+"'";		  	
		ExecBean exc1 = new ExecBean(execsql1);	  	
		DPB.add(new DataProcessBean(exc1));	  			
		try 
		{			
			if(categoryNO.length()==0)
			{
				categoryNO = " ";//主键
			}
	
			if(orderPriority.length()==0)
			{
				orderPriority = "0";//主键
			}

			String[] columns1 = { "EID", "LOAD_DOCTYPE", "SHOPID", "ORGANIZATIONNO", "CATEGORYNO", "CATEGORYNAME","ORDER_SHOP","ORDER_CATEGORYNO",
					"ORDER_CATEGORYNAME", "PRIORITY", "STATUS" };
			DataValue[] insValue1 = null;
			insValue1 = new DataValue[]{
					new DataValue(eId, Types.VARCHAR),
					new DataValue(loadDocType, Types.VARCHAR),//1.饿了么 2.美团外卖 3.
					new DataValue(shopId, Types.VARCHAR),//组织编号=门店shopId编号
					new DataValue(shopId, Types.VARCHAR),//ERP门店shopId				
					new DataValue(categoryNO, Types.VARCHAR),//ERP商品编码
					new DataValue(categoryName, Types.VARCHAR),//ERP商品名称
					new DataValue(orderShopNO, Types.VARCHAR),//ERP商品分类编码
					new DataValue(orderCategoryNO, Types.VARCHAR),//ERP商品分类编码
					new DataValue(orderCategoryName, Types.VARCHAR),//外卖平台门店shopIdID				
					new DataValue(orderPriority, Types.VARCHAR),//	
					new DataValue("100", Types.VARCHAR)	
			};

			InsBean ib1 = new InsBean("OC_MAPPINGCATEGORY", columns1);
			ib1.addValues(insValue1);
			DPB.add(new DataProcessBean(ib1));
		}

		catch (Exception e) 
		{
			HelpTools.writelog_fileName("【同步分类】【新增/修改】开始组装SQL语句！有异常:"+e.getMessage()+" 平台类型LoadDocType:"+loadDocType+" 平台门店shopIdID："+orderShopNO+" 门店shopId:"+shopId+" 异常的平台分类ID/名称："+orderCategoryNO+"/"+orderCategoryName, elmLogFileName);
			return;		
		}
	
		try 
		{
			HelpTools.writelog_fileName("【同步分类】【新增/修改】开始执行SQL语句！ 平台类型LoadDocType:"+loadDocType+" 平台门店shopIdID："+orderShopNO+" 门店shopId:"+shopId, elmLogFileName);  
			this.doExecuteDataToDB(DPB);
			HelpTools.writelog_fileName("【同步分类】【新增/修改】开始执行SQL语句！成功！ 平台类型LoadDocType:"+loadDocType+" 平台门店shopIdID："+orderShopNO+" 门店shopId:"+shopId, elmLogFileName);  

		} 
		catch (SQLSyntaxErrorException e)
		{
			HelpTools.writelog_fileName("【同步分类】【新增/修改】开始执行SQL语句！异常："+e.getMessage()+" 平台类型LoadDocType:"+loadDocType+" 平台门店shopIdID："+orderShopNO+" 门店shopId:"+shopId, elmLogFileName); 		
		}
		catch (Exception e) 
		{
			HelpTools.writelog_fileName("【同步分类】【新增/修改】开始执行SQL语句！异常："+e.getMessage()+" 平台类型LoadDocType:"+loadDocType+" 平台门店shopIdID："+orderShopNO+" 门店shopId:"+shopId, elmLogFileName); 		
		}
	}
	
	
	private void DeleteCatesLocal(String eId,String shopId,String loadDocType,String orderShopNO,DCP_OrderPlatformCategoryQueryRes.level1Elm catesModel) throws Exception
	{
		String categoryNO = catesModel.getCategoryNO();
		String categoryName = catesModel.getCategoryName();
		String orderCategoryNO= catesModel.getOrderCategoryNO();
		String orderCategoryName=catesModel.getOrderCategoryName();
		String orderPriority = catesModel.getOrderPriority();
		ArrayList<DataProcessBean> DPB = new ArrayList<DataProcessBean>();
		//先删 在插入
		String execsql1 = "delete from OC_MAPPINGCATEGORY where EID='"+eId+"' and LOAD_DOCTYPE='"+loadDocType+"' and ORDER_SHOP='"+orderShopNO+"' and ORDER_CATEGORYNO='"+orderCategoryNO+"'";		  	
		ExecBean exc1 = new ExecBean(execsql1);	  	
		DPB.add(new DataProcessBean(exc1));	  			
		
		try 
		{
			HelpTools.writelog_fileName("【同步分类】【删除】开始执行SQL语句！ 平台类型LoadDocType:"+loadDocType+" 平台门店shopIdID："+orderShopNO+" 门店shopId:"+shopId, elmLogFileName);  
			this.doExecuteDataToDB(DPB);
			HelpTools.writelog_fileName("【同步分类】【删除】开始执行SQL语句！成功！ 平台类型LoadDocType:"+loadDocType+" 平台门店shopIdID："+orderShopNO+" 门店shopId:"+shopId, elmLogFileName);  

		} 
		catch (SQLSyntaxErrorException e)
		{
			HelpTools.writelog_fileName("【同步分类】【删除】开始执行SQL语句！异常："+e.getMessage()+" 平台类型LoadDocType:"+loadDocType+" 平台门店shopIdID："+orderShopNO+" 门店shopId:"+shopId, elmLogFileName); 		
		}
		catch (Exception e) 
		{
			HelpTools.writelog_fileName("【同步分类】【删除】开始执行SQL语句！异常："+e.getMessage()+" 平台类型LoadDocType:"+loadDocType+" 平台门店shopIdID："+orderShopNO+" 门店shopId:"+shopId, elmLogFileName); 		
		}
	}
	
	private void SetCategorySequence(String eId,String shopId,String loadDocType,String orderShopNO,DCP_OrderPlatformCategoryQueryRes.level1Elm catesModel) throws Exception
	{
		String sql = " SELECT * FROM (";		
		sql +=" select A.*,B.PRIORITY AS PRIORITY2 from OC_mappingcategory A left join OC_category B on A.EID=B.EID and A.CATEGORYNO=B.CATEGORYNO ";
		sql +=" where A.EID='"+eId+"' and  A.SHOPID='"+shopId+"' and A.load_doctype='"+loadDocType+"' order BY B.PRIORITY ";
		sql +=")";
		
		List<Map<String, Object>> getData = this.doQueryData(sql, null);
		if(getData==null||getData.isEmpty())
			return;
		List<Long> categoryIds = new ArrayList<Long>();
		for (Map<String, Object> map : getData) 
		{
			String orderCategoryNO = map.get("ORDER_CATEGORYNO").toString();
			if (orderCategoryNO != null && orderCategoryNO.trim().length() > 0)
			{
				
				try 
				{
					long categoryId = Long.parseLong(orderCategoryNO);
					categoryIds.add(categoryId);			
		    } 
				catch (Exception e) 
				{
			
		    }
				
			}
			
		}
		
		//开始调用排序接口
	  if (categoryIds == null && categoryIds.size() == 0)	
		{
			return;
		}
	  
	  Map<String, Object> mapAppKey = PosPub.getWaimaiAppConfigByShopNO_New(StaticInfo.dao, eId, shopId, loadDocType,"");
		Boolean isGoNewFunction = false;//是否走新的接口
		String elmAPPKey = "";
		String elmAPPSecret = "";
		String elmAPPName = "";			
		boolean elmIsSandbox = false;
		if (mapAppKey != null)
		{
			elmAPPKey = mapAppKey.get("APPKEY").toString();
			elmAPPSecret = mapAppKey.get("APPSECRET").toString();
			elmAPPName = mapAppKey.get("APPNAME").toString();
			String	elmIsTest = mapAppKey.get("ISTEST").toString();					
			if (elmIsTest != null && elmIsTest.equals("Y"))
			{
				elmIsSandbox = true;
			}
			isGoNewFunction = true;
		}
		
		long shopIdLong =	Long.parseLong(orderShopNO);
		StringBuilder errorMessage = new StringBuilder("");
		
		if(isGoNewFunction)
		{
			
			WMELMProductService.setCategorySequence(elmIsSandbox,elmAPPKey,elmAPPSecret,elmAPPName, shopIdLong,categoryIds, errorMessage);
		}
		else
		{
			 WMELMProductService.setCategorySequence(shopIdLong,categoryIds, errorMessage);
		}
		
		
	}
	

	/*
	 * 设置分类下商品排序
	 */
	private void SetCategoryGoodsSequence(String eId,String shopId,String loadDocType,String orderShopNO,DCP_OrderTransTaskGoodsRes.level1Elm goodsModel) throws Exception
	{
		String orderCategoryNO = goodsModel.getOrderCategoryNO();
		String sql = " SELECT * FROM (";		
		sql +=" select A.*,B.PRIORITY AS PRIORITY2 from OC_mappinggoods A left join OC_goods B on A.EID=B.EID and A.PLUNO=B.PLUNO ";
		sql +=" and B.belfirm in (select belfirm from DCP_ORG C  where  C.EID='"+eId+"' and  C.Organizationno='"+shopId+"')";
		sql +=" where A.EID='"+eId+"' and  A.SHOPID='"+shopId+"' and A.load_doctype='"+loadDocType+"' and  A.order_categoryno='"+orderCategoryNO+"' order BY B.PRIORITY ";
		sql +=")";
		HelpTools.writelog_fileName("排序SQL："+sql, "SetCategoryGoodsSequence");
		List<Map<String, Object>> getData = this.doQueryData(sql, null);
		if(getData==null||getData.isEmpty())
			return;
		List<Long> itemIds = new ArrayList<Long>();
		for (Map<String, Object> map : getData) 
		{
			String orderPLUNO = map.get("ORDER_PLUNO").toString();
			if (orderPLUNO != null && orderPLUNO.trim().length() > 0)
			{
				
				try 
				{
					long itemId = Long.parseLong(orderPLUNO);
					itemIds.add(itemId);			
		    } 
				catch (Exception e) 
				{
			
		    }
				
			}
			
		}
		
		//开始调用排序接口
	  if (itemIds == null && itemIds.size() == 0)	
		{
			return;
		}
	  
	  Map<String, Object> mapAppKey = PosPub.getWaimaiAppConfigByShopNO_New(StaticInfo.dao, eId, shopId, loadDocType,"");
		Boolean isGoNewFunction = false;//是否走新的接口
		String elmAPPKey = "";
		String elmAPPSecret = "";
		String elmAPPName = "";			
		boolean elmIsSandbox = false;
		if (mapAppKey != null)
		{
			elmAPPKey = mapAppKey.get("APPKEY").toString();
			elmAPPSecret = mapAppKey.get("APPSECRET").toString();
			elmAPPName = mapAppKey.get("APPNAME").toString();
			String	elmIsTest = mapAppKey.get("ISTEST").toString();					
			if (elmIsTest != null && elmIsTest.equals("Y"))
			{
				elmIsSandbox = true;
			}
			isGoNewFunction = true;
		}
		
		long categoryId =	Long.parseLong(orderCategoryNO);
		StringBuilder errorMessage = new StringBuilder("");
		
		if(isGoNewFunction)
		{
			
			WMELMProductService.setItemPositions(elmIsSandbox,elmAPPKey,elmAPPSecret,elmAPPName, categoryId,itemIds, errorMessage);
		}
		else
		{
			 WMELMProductService.setCategorySequence(categoryId,itemIds, errorMessage);
		}
		
		
	}
}
