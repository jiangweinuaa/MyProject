package com.dsc.spos.scheduler.job;

import java.io.File;
import java.sql.SQLSyntaxErrorException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
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
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.WMJBPProductService;
import com.dsc.spos.waimai.model.WMJBPGoodsUpdate;
import com.sankuai.meituan.waimai.opensdk.vo.AvailableTimeParam;
import com.sankuai.sjst.platform.developer.domain.WaiMaiDishProperty;
import com.sankuai.sjst.platform.developer.domain.WaiMaiDishPropertyVO;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class OrderTransTaskJBPProcess extends InitJob
{
  Logger logger = LogManager.getLogger(OrderTransTaskJBPProcess.class.getName());
	static boolean bRun=false;//标记此服务是否正在执行中
	String jbpLogFileName = "orderTransTaskJBPlog";
	
	ArrayList<String> UpdateCategoryGoodsSequenceList = new ArrayList<String>();//商品更新时，用于标记更新商品所属分类下所有商品排序（只需要更新一次）
	public OrderTransTaskJBPProcess ()
	{
		
	}
	
	public String doExe() throws Exception
	{
    String sReturnInfo = "";
		
		HelpTools.writelog_fileName("【同步任务orderTranTasklog】同步START！",jbpLogFileName);
		try 
		{
			 //此服务是否正在执行中
			if (bRun)
			{		
				logger.info("\r\n*********同步任务orderTranTasklog同步正在执行中,本次调用取消:************\r\n");
				HelpTools.writelog_fileName("【同步任务orderTranTasklog】同步正在执行中,本次调用取消！",jbpLogFileName);
				return sReturnInfo;
			}

			bRun=true;//			
			String loadDocType = "2";//聚宝盆
			List<Map<String, Object>> transTask = this.GetTransType(loadDocType);		

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
								+ transTask_transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+transTask_ShopNO+" ", jbpLogFileName);
						//1、分类新增   2、分类修改 3、分类删除
						if(transTask_transType.equals("1")||transTask_transType.equals("2")||transTask_transType.equals("3"))
						{

							if (transTask_Category == null || transTask_Category.isEmpty() )
							{
								HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步分类】需要同步的数据为空！任务ID："
										+ transTask_transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+transTask_ShopNO+" ", jbpLogFileName);
								continue;
							}
							else
							{
								String categoryNO="";
								String categoryName="";
								String priority ="";
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
										priority =transTask_CategoryMap.get("PRIORITY").toString();
										orderCategoryNO=transTask_CategoryMap.get("ORDER_CATEGORYNO").toString();
										orderCategoryName=transTask_CategoryMap.get("ORDER_CATEGORYNAME").toString();
										break;
									}	
								}
								if(categoryNO ==null||categoryNO.isEmpty()||categoryNO.trim().length()==0)
								{
									HelpTools.writelog_fileName("【同步任务orderTranTasklog】同步当前任务异常！任务类型：" + transTask_transType + " 商品分类categoryNO："+" 为空！",jbpLogFileName);
									continue;
								}
								DCP_OrderPlatformCategoryQueryRes res = new DCP_OrderPlatformCategoryQueryRes();
								DCP_OrderPlatformCategoryQueryRes.level1Elm catesModel = res.new level1Elm();
								catesModel.setCategoryNO(categoryNO);
								catesModel.setCategoryName(categoryName);
								catesModel.setOrderCategoryNO(orderCategoryNO);
								catesModel.setOrderCategoryName(orderCategoryName);
								catesModel.setOrderDescription("");
								catesModel.setOrderPriority(priority);
								catesModel.setOrderShopNO(transTask_OrderShopNO);

								//调用第三方接口 并且更新任务表
								if(transTask_transType.equals("1"))
								{
									//分类新增							
									this.CallCatesAddAPIFun(transTask_transType,transTask_transID, transTask_eId, transTask_ShopNO, loadDocType, transTask_OrderShopNO, catesModel);
								}
								else if(transTask_transType.equals("2"))
								{
									//分类更新
									this.CallCatesUpdateAPIFun(transTask_transType,transTask_transID, transTask_eId, transTask_ShopNO, loadDocType, transTask_OrderShopNO, catesModel);
								}
								else 
								{
									//分类删除
									this.CallCatesDeleteAPIFun(transTask_transType,transTask_transID, transTask_eId, transTask_ShopNO, loadDocType, transTask_OrderShopNO, catesModel);
								}
							}
						}
						//4、商品新增   5、商品修改 6、商品删除
						else if (transTask_transType.equals("4")||transTask_transType.equals("5")||transTask_transType.equals("6")) //4、商品新增  5、商品修改 6、商品删除
						{
							if (transTask_Goods == null || transTask_Goods.isEmpty() )
							{
								HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步商品】需要同步的数据为空！任务ID："
										+ transTask_transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+transTask_ShopNO+" ", jbpLogFileName);
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
									HelpTools.writelog_fileName("【同步任务orderTranTasklog】同步当前任务异常！任务类型：" + transTask_transType + " 商品pluNO："+" 为空！",jbpLogFileName);
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
											oneDataAttr=null;
										}
									}
								}

								//调用第三方接口 并且更新任务表
								if(transTask_transType.equals("4"))
								{
									//商品新增							
									this.CallGoodsAddAPIFun(transTask_transType,transTask_transID, transTask_eId, transTask_ShopNO, loadDocType, transTask_OrderShopNO,goodsModel);
								}
								else if(transTask_transType.equals("5"))
								{
									//商品更新
									this.CallGoodsUpdateAPIFun(transTask_transType,transTask_transID, transTask_eId, transTask_ShopNO, loadDocType, transTask_OrderShopNO,goodsModel);
								}
								else 
								{
									//商品删除
									this.CallGoodsDeleteAPIFun(transTask_transType,transTask_transID, transTask_eId, transTask_ShopNO, loadDocType, transTask_OrderShopNO,goodsModel);
								}
							}
						}
						else //其他类型 暂不支持
						{
							HelpTools.writelog_fileName("【同步任务orderTranTasklog】同步当前任务异常【任务类型未知】！任务类型：" + transTask_transType + " 商品pluNO："+" 为空！",jbpLogFileName);
							continue;											
						}
					} 
					catch (Exception e) 
					{
						HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步商品】 异常:"+e.getMessage()+" 任务ID："
								+ transTask_transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+transTask_ShopNO , jbpLogFileName);
						UpdateTransTask("1",e.getMessage(),transTask_eId,transTask_ShopNO,loadDocType,transTask_transID) ;	
					}
				}
			}
			else
			{			
				sReturnInfo="无符合要求的数据！";
				HelpTools.writelog_fileName("【同步任务orderTranTasklog】没有需要处理任务！",jbpLogFileName);
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

	
	private List<Map<String, Object>> GetTransType(String loadDocType) throws Exception
	{
		String sql = " SELECT *  FROM OC_TRANSTASK WHERE status='100' and trans_flg='0' and load_doctype='"+loadDocType+"'  order by  trans_date,trans_time,trans_type";
		List<Map<String, Object>> transTypes = this.doQueryData(sql, null);
		
		return transTypes;
		
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
		String orderPriority = catesModel.getOrderPriority();
		
		
	  //聚宝盆新增的时候 ;回写平台的分类=菜品池分类，聚宝盆分类编号=分类名称
			
		catesModel.setOrderCategoryNO(categoryName);				
		catesModel.setOrderCategoryName(categoryName);
			
		
		
		String description="";
		String trans_flg = "1";
		String transTaskDescription="";
		int sequence = 0;//菜品排序【数字越小，排名越靠前,不同分类顺序可以相同 】
		try 
		{
			sequence = Integer.parseInt(orderPriority);
		
	  } 
		catch (Exception e) 
		{
			sequence = 0;
	  }
		boolean nRet = false;
		try 
		{
			if ( Check.Null(orderCategoryNO ) && Check.Null(orderCategoryName) )
			{			
				StringBuilder errorMessage = new StringBuilder();
				try
				{	
					//美团分类没有编号，只有名称，默认一样就好
					nRet = WMJBPProductService.updateCat(eId, shopId, "", categoryName, sequence, errorMessage);
					if (nRet) 
					{					
					  trans_flg = "2";
					  this.SaveCatesLocal(eId, shopId, loadDocType, orderShopNO, catesModel);
					} 
					else 
					{
					  transTaskDescription = errorMessage.toString();
					}

				}
				catch (Exception e)
				{
					trans_flg = "1";
					transTaskDescription= "聚宝盆接口调用失败:"+e.getMessage() ;
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
					+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+shopId+" 新增的商品分类ID/名称："+categoryNO+"/"+categoryName, jbpLogFileName);
			transTaskDescription=e.getMessage();
		}
		finally 
		{
			UpdateTransTask(trans_flg,transTaskDescription,eId,shopId,loadDocType,transID) ;
			HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步分类】【新增】完成!任务状态TRANS_FLG:"+trans_flg+" 任务ID："
					+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+shopId+" 新增的商品分类ID/名称："+categoryNO+"/"+categoryName, jbpLogFileName);
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
		String orderCategoryName = catesModel.getOrderCategoryName();	//原始的菜品分类名称	更新时必须
		String categoryNO = catesModel.getCategoryNO();
		String categoryName = catesModel.getCategoryName();
		String description = catesModel.getOrderDescription();
		String orderPriority = catesModel.getOrderPriority();
		String trans_flg = "1";
		String transTaskDescription="";
		int sequence = 0;//菜品排序【数字越小，排名越靠前,不同分类顺序可以相同 】
		try 
		{
			sequence = Integer.parseInt(orderPriority);
		
	  } 
		catch (Exception e) 
		{
			sequence = 0;
	  }
		StringBuilder errorMessage = new StringBuilder();
		boolean nRet = false;
		try 
		{
			if (!Check.Null(orderCategoryNO) && !Check.Null(orderCategoryName))
			{
				
				try
				{
				  //美团分类没有编号，只有名称，默认一样就好
					nRet = WMJBPProductService.updateCat(eId, shopId, orderCategoryName, categoryName, sequence, errorMessage);
					
					if(nRet)
					{
						trans_flg = "2";
					  //修改后， 回写平台的分类=菜品池分类，聚宝盆分类编号=分类名称
						
						catesModel.setOrderCategoryNO(categoryName);				
						catesModel.setOrderCategoryName(categoryName);
						this.SaveCatesLocal(eId, shopId, loadDocType, orderShopNO, catesModel);
					}
					else
					{
						transTaskDescription = errorMessage.toString();
					}
				}
				catch (Exception e)
				{
					trans_flg = "1";
					transTaskDescription= "聚宝盆接口调用失败:"+e.getMessage();
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
					+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+shopId+" 修改前商品分类ID/名称："+orderCategoryNO+"/"+orderCategoryName+" 修改后商品分类ID/名称："+categoryNO+"/"+categoryName, jbpLogFileName);
			transTaskDescription=e.getMessage();
		}
		finally 
		{
			//更新任务状态
			UpdateTransTask(trans_flg,transTaskDescription,eId,shopId,loadDocType,transID) ;
			HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步分类】【修改】完成!任务状态TRANS_FLG:"+trans_flg+" 任务ID："
					+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+shopId+" 修改前商品分类ID/名称："+orderCategoryNO+"/"+orderCategoryName+" 修改后商品分类ID/名称："+categoryNO+"/"+categoryName, jbpLogFileName);
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
				
				try
				{
					nRet = WMJBPProductService.deleteCat(eId, shopId, orderCategoryName, errorMessage);
					if(nRet) 
					{
						trans_flg = "2";
						this.DeleteCatesLocal(eId, shopId, loadDocType, orderShopNO, catesModel);
					}
					if(!nRet) 
					{
						transTaskDescription = errorMessage.toString();
					}
				}
				catch (Exception e)
				{
					trans_flg = "1";
					transTaskDescription= "聚宝盆接口调用失败:"+e.getMessage();
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
					+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+shopId+" 删除的商品分类ID/名称："+orderCategoryNO+"/"+orderCategoryName, jbpLogFileName);
			transTaskDescription=e.getMessage();
		}
		finally 
		{
			UpdateTransTask(trans_flg,transTaskDescription,eId,shopId,loadDocType,transID) ;
			HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步分类】【删除】完成!任务状态TRANS_FLG:"+trans_flg+" 任务ID："
					+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+shopId+" 删除的商品分类ID/名称："+orderCategoryNO+"/"+orderCategoryName, jbpLogFileName);
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
		String orderCategoryName = goodsModel.getOrderCategoryName();
		String description = goodsModel.getDescription();
		String fileName= goodsModel.getFileName(); 
		String jbpHash = goodsModel.getJbpHash(); 
		String unit = goodsModel.getUnit();
		String isAllTimeSell = goodsModel.getIsAllTimeSell();
		String beginDate = goodsModel.getBeginDate();
		String endDate = goodsModel.getEndDate();
		String sellWeek = goodsModel.getSellWeek();
		String sellTime = goodsModel.getSellTime();
		String priority = goodsModel.getPriority();
		String trans_flg = "1";
		String transTaskDescription="";
		
		//聚宝盆新增的时候 ;暂时回写平台的id=ERP的pluno
		if(orderPluNO==null||orderPluNO.trim().length()==0)
		{
			goodsModel.setOrderPluNO(pluNO);
		}
		if(orderPluName==null||orderPluName.trim().length()==0)
		{
			goodsModel.setOrderPluName(pluName);
		}
		
		
		
		String epoiId = eId + "_" + shopId;//映射后的门店其实是 eId_shopno;99_10001
		int sequence = 0;
		boolean isSequence = false;
		try 
		{
			sequence = Integer.parseInt(priority);
			isSequence = true;
		
	  } 
		catch (Exception e) 
		{
		
	  }
		String surl= "";
			
		try 
		{
			if (Check.Null(orderPluNO) &&Check.Null(orderPluName)&&!Check.Null(orderCategoryNO))
			{
				
			 //新增商品的时候，如果有图片，先判断有没有获取过图片hash值了，有了就不用调用接口
				if(!Check.Null(fileName) && Check.Null(jbpHash))
				{
					jbpHash = GetHash(eId, shopId,loadDocType,pluNO,fileName) ;
				}	
				
				
				List<WMJBPGoodsUpdate> goodsList = new ArrayList<WMJBPGoodsUpdate>();
				WMJBPGoodsUpdate goods = new WMJBPGoodsUpdate();
				
				/*goods.setBoxNum(1);
				goods.setBoxPrice(0);*/
				goods.setCategoryName(orderCategoryName);
				goods.setDescription(description);
				goods.setDishName(pluName);
				goods.setEDishCode(pluNO);
				goods.setePoiId(epoiId);//映射后的门店其实是 eId_shopno;99_10001
				goods.setIsSoldOut(0);//	0-未售完，1-售完，创建时请设置为0，否则C端不显示。当isSoldOut=0,而且库存>0时,菜品才可以售卖。
				goods.setMinOrderCount(1);
				goods.setUnit(unit);
				if (jbpHash != null && jbpHash.isEmpty() == false)
				{
					goods.setPicture(jbpHash);
				}
				if(isSequence)
				{
					goods.setSequence(sequence);
				}
				goods.setUnit(unit);
				goods.setSkus(new ArrayList<WMJBPGoodsUpdate.Skus>());
								
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
					
					String orderSpecNO = oneData.getOrderSpecNO();
					String orderSpecName = oneData.getOrderSpecName();
									
					if(orderSpecNO==null||orderSpecNO.trim().length()==0)
					{
						oneData.setOrderSpecNO(specNO);
					}
					if(orderSpecName==null||orderSpecName.trim().length()==0)
					{
						oneData.setOrderSpecName(specName);
					}

					int onShelf_i = 1;							
					if (isOnShelf != null && isOnShelf.equals("N"))
					{
						onShelf_i = 0;
					}
					float price_d = 0;
					try
					{
						price_d = Float.parseFloat(price);
					}
					catch(Exception e)
					{
						price_d = 0;
					}
					float packageFee_d = 0;
					try
					{
						packageFee_d = Float.parseFloat(packageFee);
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
						
					}

					WMJBPGoodsUpdate.Skus spec = new WMJBPGoodsUpdate.Skus();
					spec.setSkuId(specNO);
					spec.setSpec(specName);
					spec.setStock(stockQty_i);
					spec.setPrice(price_d);
					spec.setBoxNum(1);//默认 1个
					spec.setBoxPrice(packageFee_d);//价格 = 打包费 数量默认1个
					//售卖时间
					if (isAllTimeSell.equals("N"))
					{
						if (sellWeek != null && sellWeek.isEmpty() == false)
						{
							String ssTime ="00:00-23:59";//默认
							if (sellTime != null && sellTime.isEmpty() == false)
							{					
								//这里需要解析一下，数据库存的是00:00:00-23:59:59
								ssTime = "";//16:00:00-19:00:00,20:00:00-23:00:00
								
								String[] ss = sellTime.split(",");//16:00-19:00,20:00-23:00
								for (String timeStr : ss) 
								{
									String[] ss1 = timeStr.split("-");//16:00-19:00						  			
									ssTime += ss1[0].substring(0, 5) + "-" + ss1[1].substring(0, 5) + ",";								
								}		
								if (ssTime != null && ssTime.length() > 0)
								{
									ssTime = ssTime.substring(0,ssTime.length()-1);
								}
								
							}
							AvailableTimeParam availableTimes = new AvailableTimeParam();
						  //必须要有值，要不然调用更新接口报错
							availableTimes.setMonday("00:00-00:01");
							availableTimes.setTuesday("00:00-00:01");
							availableTimes.setWednesday("00:00-00:01");
							availableTimes.setThursday("00:00-00:01");
							availableTimes.setFriday("00:00-00:01");
							availableTimes.setSaturday("00:00-00:01");
							availableTimes.setSunday("00:00-00:01");	
							for (String week: sellWeek.split(","))
							{
								if (week.equals("1")) availableTimes.setMonday(ssTime);
								else if (week.equals("2")) availableTimes.setTuesday(ssTime);
								else if (week.equals("3")) availableTimes.setWednesday(ssTime);
								else if (week.equals("4")) availableTimes.setThursday(ssTime);
								else if (week.equals("5")) availableTimes.setFriday(ssTime);
								else if (week.equals("6")) availableTimes.setSaturday(ssTime);
								else if (week.equals("7")) availableTimes.setSunday(ssTime);	           
							}
							spec.setAvailableTimes(availableTimes);
							
						}													
					}							
					goods.getSkus().add(spec);
				}
				
				goodsList.add(goods);
				
				StringBuilder errorMessage = new StringBuilder();
				boolean nRet = false;
				try
				{
					nRet = WMJBPProductService.batchUpload(eId, shopId, goodsList, errorMessage);
					if(nRet) 
					{
						trans_flg = "2";
						//调用成功之后，还得 调用创建属性接口
						if (goodsModel.getAttr()!= null && goodsModel.getAttr().size()>0)
						{
							List<WaiMaiDishPropertyVO> dishPropertys =new ArrayList<WaiMaiDishPropertyVO>();
							
							WaiMaiDishPropertyVO oItemAttribute = new WaiMaiDishPropertyVO();
							List<WaiMaiDishProperty> properties = new ArrayList<WaiMaiDishProperty>();
							for (DCP_OrderTransTaskGoodsRes.level2Attr oneData : goodsModel.getAttr()) 
							{
								WaiMaiDishProperty Property= new WaiMaiDishProperty();
								List<String> attrValueList = new ArrayList<String>();
								String attrName = oneData.getAttrName();
								String attrValue = oneData.getAttrValue();
								String[] ssAttrValue = attrValue.split(","); //7分甜，6分甜，8分甜
								for (String attrValueStr : ssAttrValue) 
								{
									attrValueList.add(attrValueStr);
								}	
								
								Property.setPropertyName(attrName);
								Property.setValues(attrValueList);
								
								properties.add(Property);
								
							}
							oItemAttribute.seteDishCode(pluNO);
							oItemAttribute.setProperties(properties);
							
							dishPropertys.add(oItemAttribute);
							StringBuilder errorMessage_attri = new StringBuilder();
							boolean nRet_attri= WMJBPProductService.batchUpProperty(eId, shopId, dishPropertys, errorMessage_attri);
							if(nRet_attri)
							{
								
							}
							else
							{
								trans_flg = "1";//属性没成功，也不算成功
								transTaskDescription ="商品新增成功，属性新增失败！"+ errorMessage_attri.toString();
							}
							
							
						}
																	
					
						this.SaveGoodsLocal(eId, shopId, loadDocType, orderShopNO, goodsModel);
					}
					else
					{
						transTaskDescription = errorMessage.toString();
					}
				}
				catch (Exception e)
				{
					trans_flg = "1";
					transTaskDescription= "聚宝盆接口调用失败:"+e.getMessage();
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
					+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+shopId+" 商品pluNO："+pluNO, jbpLogFileName);
			transTaskDescription=e.getMessage();
		}
		finally 
		{
			UpdateTransTask(trans_flg,transTaskDescription,eId,shopId,loadDocType,transID) ;	
			HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步商品】【新增】任务完成！【更新任务表】完成!任务状态TRANS_FLG:"+trans_flg+" 任务ID："
					+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+shopId+" 商品pluNO："+pluNO, jbpLogFileName);
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
		String orderCategoryName = goodsModel.getOrderCategoryName();
		String description = goodsModel.getDescription();
		String fileName= goodsModel.getFileName(); 
		String jbpHash = goodsModel.getJbpHash(); 
		String unit = goodsModel.getUnit();
		String isAllTimeSell = goodsModel.getIsAllTimeSell();
		String beginDate = goodsModel.getBeginDate();
		String endDate = goodsModel.getEndDate();
		String sellWeek = goodsModel.getSellWeek();
		String sellTime = goodsModel.getSellTime();
		String priority = goodsModel.getPriority();
		String trans_flg = "1";
		String transTaskDescription="";
		
		String epoiId = eId + "_" + shopId;//映射后的门店其实是eId_shopno;99_10001
		int sequence = 0;
		boolean isSequence = false;
		try 
		{
			sequence = Integer.parseInt(priority);
			isSequence = true;
		
	  } 
		catch (Exception e) 
		{
		
	  }
		String surl= "";
			
		try 
		{
			if (!Check.Null(orderPluNO) && !Check.Null(orderPluName)&&!Check.Null(orderCategoryNO))
			{
				 //新增商品的时候，如果有图片，先判断有没有获取过图片hash值了，有了就不用调用接口
				if(!Check.Null(fileName) && Check.Null(jbpHash))
				{
					jbpHash = GetHash(eId, shopId,loadDocType,pluNO,fileName) ;
				}	
				
				List<WMJBPGoodsUpdate> goodsList = new ArrayList<WMJBPGoodsUpdate>();
				WMJBPGoodsUpdate goods = new WMJBPGoodsUpdate();
				
				/*goods.setBoxNum(1);
				goods.setBoxPrice(0);*/
				goods.setCategoryName(orderCategoryName);
				goods.setDescription(description);
				goods.setDishName(pluName);
				goods.setEDishCode(pluNO);
				goods.setePoiId(epoiId);//映射后的门店其实是eId_shopno;99_10001
				//goods.setIsSoldOut(0);//	0-未售完，1-售完，创建时请设置为0，否则C端不显示。当isSoldOut=0,而且库存>0时,菜品才可以售卖。
				//goods.setMinOrderCount(1);
				goods.setUnit(unit);
				if (jbpHash != null && jbpHash.isEmpty() == false)
				{
					goods.setPicture(jbpHash);
				}
				if(isSequence)
				{
					goods.setSequence(sequence);
				}
				goods.setUnit(unit);
				goods.setSkus(new ArrayList<WMJBPGoodsUpdate.Skus>());
								
				for (DCP_OrderTransTaskGoodsRes.level2Spec oneData : goodsModel.getSpecs()) 
				{					
					String orderSpecNO = oneData.getOrderSpecNO();
					String orderSpecName = oneData.getOrderSpecName();
					String specNO = oneData.getSpecNO();
					String specName = oneData.getSpecName();
					String price = oneData.getPrice();
					String stockQty = oneData.getStockQty();
					String packageFee = oneData.getPackageFee();
					String isOnShelf = oneData.getIsOnShelf();
					String maxStockQty =oneData.getMaxStockQty();
					String netWeight =oneData.getNetWeight();
					
					//修改有可能是新增sku
					if(orderSpecNO==null||orderSpecNO.trim().length()==0)
					{
						oneData.setOrderSpecNO(specNO);
					}
					if(orderSpecName==null||orderSpecName.trim().length()==0)
					{
						oneData.setOrderSpecName(specName);
					}

					int onShelf_i = 1;							
					if (isOnShelf != null && isOnShelf.equals("N"))
					{
						onShelf_i = 0;
					}
					float price_d = 0;
					try
					{
						price_d = Float.parseFloat(price);
					}
					catch(Exception e)
					{
						price_d = 0;
					}
					float packageFee_d = 0;
					try
					{
						packageFee_d = Float.parseFloat(packageFee);
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
						
					}

					WMJBPGoodsUpdate.Skus spec = new WMJBPGoodsUpdate.Skus();
					spec.setSkuId(specNO);
					spec.setSpec(specName);
					spec.setStock(stockQty_i);
					spec.setPrice(price_d);
					spec.setBoxNum(1);//默认 1个
					spec.setBoxPrice(packageFee_d);//价格 = 打包费 数量默认1个
					//售卖时间
					if (isAllTimeSell.equals("N"))
					{
						if (sellWeek != null && sellWeek.isEmpty() == false)
						{
							String ssTime ="0:00-23:59";//默认
							if (sellTime != null && sellTime.isEmpty() == false)
							{
															
							  //这里需要解析一下，数据库存的是00:00:00-23:59:59
								ssTime = "";//16:00:00-19:00:00,20:00:00-23:00:00
								
								String[] ss = sellTime.split(",");//16:00-19:00,20:00-23:00
								for (String timeStr : ss) 
								{
									String[] ss1 = timeStr.split("-");//16:00-19:00						  			
									ssTime += ss1[0].substring(0, 5) + "-" + ss1[1].substring(0, 5) + ",";								
								}		
								if (ssTime != null && ssTime.length() > 0)
								{
									ssTime = ssTime.substring(0,ssTime.length()-1);
								}
								
							}
							AvailableTimeParam availableTimes = new AvailableTimeParam();
							//必须要有值，要不然调用更新接口报错
							availableTimes.setMonday("00:00-00:01");
							availableTimes.setTuesday("00:00-00:01");
							availableTimes.setWednesday("00:00-00:01");
							availableTimes.setThursday("00:00-00:01");
							availableTimes.setFriday("00:00-00:01");
							availableTimes.setSaturday("00:00-00:01");
							availableTimes.setSunday("00:00-00:01");	
							
							
							for (String week: sellWeek.split(","))
							{
								if (week.equals("1")) availableTimes.setMonday(ssTime);
								else if (week.equals("2")) availableTimes.setTuesday(ssTime);
								else if (week.equals("3")) availableTimes.setWednesday(ssTime);
								else if (week.equals("4")) availableTimes.setThursday(ssTime);
								else if (week.equals("5")) availableTimes.setFriday(ssTime);
								else if (week.equals("6")) availableTimes.setSaturday(ssTime);
								else if (week.equals("7")) availableTimes.setSunday(ssTime);	           
							}
							spec.setAvailableTimes(availableTimes);
							
						}													
					}							
					goods.getSkus().add(spec);
				}
				
				goodsList.add(goods);
				
				StringBuilder errorMessage = new StringBuilder();
				boolean nRet = false;
				try
				{
					nRet = WMJBPProductService.batchUpload(eId, shopId, goodsList, errorMessage);
					if(nRet) 
					{
						trans_flg = "2";
						//调用成功之后，还得 调用创建属性接口
						if (goodsModel.getAttr()!= null && goodsModel.getAttr().size()>0)
						{
							List<WaiMaiDishPropertyVO> dishPropertys =new ArrayList<WaiMaiDishPropertyVO>();
							
							WaiMaiDishPropertyVO oItemAttribute = new WaiMaiDishPropertyVO();
							List<WaiMaiDishProperty> properties = new ArrayList<WaiMaiDishProperty>();
							for (DCP_OrderTransTaskGoodsRes.level2Attr oneData : goodsModel.getAttr()) 
							{
								WaiMaiDishProperty Property= new WaiMaiDishProperty();
								List<String> attrValueList = new ArrayList<String>();
								String attrName = oneData.getAttrName();
								String attrValue = oneData.getAttrValue();
								String[] ssAttrValue = attrValue.split(","); //7分甜，6分甜，8分甜
								for (String attrValueStr : ssAttrValue) 
								{
									attrValueList.add(attrValueStr);
								}	
								
								Property.setPropertyName(attrName);
								Property.setValues(attrValueList);
								
								properties.add(Property);
								
							}
							oItemAttribute.seteDishCode(pluNO);
							oItemAttribute.setProperties(properties);
							
							dishPropertys.add(oItemAttribute);
							StringBuilder errorMessage_attri = new StringBuilder();
							boolean nRet_attri= WMJBPProductService.batchUpProperty(eId, shopId, dishPropertys, errorMessage_attri);
							if(nRet_attri)
							{
								
							}
							else
							{
								trans_flg = "1";//属性没成功，也不算成功
								transTaskDescription ="商品更新成功，属性更新失败！"+ errorMessage_attri.toString();
							}
							
							
						}
						
						this.SaveGoodsLocal(eId, shopId, loadDocType, orderShopNO, goodsModel);
					}
					else
					{
						transTaskDescription = errorMessage.toString();
					}
				}
				catch (Exception e)
				{
					trans_flg = "1";
					transTaskDescription= "聚宝盆接口调用失败:"+e.getMessage();
				}
			}
			else
			{
				trans_flg = "1";
				transTaskDescription="("+pluName+")商品不存在或找不到对应的平台分类";			
			}
		}
		catch (Exception e) 
		{
			HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步商品】【修改】异常:"+e.getMessage()+" 任务ID："
					+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+shopId+" 商品pluNO："+pluNO, jbpLogFileName);
			transTaskDescription=e.getMessage();
		}
		finally 
		{
			UpdateTransTask(trans_flg,transTaskDescription,eId,shopId,loadDocType,transID) ;	
			HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步商品】【修改】任务完成！【更新任务表】完成!任务状态TRANS_FLG:"+trans_flg+" 任务ID："
					+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+shopId+" 商品pluNO："+pluNO, jbpLogFileName);
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
				StringBuilder errorMessage = new StringBuilder();
				boolean nRet = false;
				try 
				{
					nRet = WMJBPProductService.deletePlu(eId, shopId, pluNO, errorMessage);
					if(nRet) 
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
					transTaskDescription= "聚宝盆接口调用失败:"+e.getMessage();
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
					+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+shopId+" 商品pluNO："+pluNO+" 平台商品orderpluNO："+orderPluNO, jbpLogFileName);
			transTaskDescription=e.getMessage();
		}
		finally 
		{
			UpdateTransTask(trans_flg,transTaskDescription,eId,shopId,loadDocType,transID) ;	
			HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步商品】【删除】任务完成！【更新任务表】完成!任务状态TRANS_FLG:"+trans_flg+" 任务ID："
					+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+shopId+" 商品pluNO："+pluNO+" 平台商品orderpluNO："+orderPluNO, jbpLogFileName);
		}
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

		if (description != null && description.length() > 255)
		{
			
			description = description.substring(0,255);
		}
		
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
			String sql = " select * from OC_GOODS  where JBPHASH IS NOT NULL AND EID='"+eId+"' and pluno='"+ pluNO +"' and FILENAME='"+fileName+"'";
			List<Map<String, Object>> imageHash_db = this.doQueryData(sql, null);
			if(imageHash_db!=null&&imageHash_db.isEmpty()==false)
			{
				imageHash = imageHash_db.get(0).get("JBPHASH").toString();
				HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步商品】【新增】【【数据库】获取商品图片的HASH值】成功，图片Hash值："+imageHash+" 文件名："
						+ fileName + " 商品pluNO："+pluNO, jbpLogFileName);
				return imageHash;
			}
		
		} 
		catch (Exception e) 
		{
		// TODO: handle exception
	
		}
		
		
		try 
		{
			//获取图片hash值
			String imageName = pluNO+".jpg";//图片名称 文件名只能是字母或数字,且必须以.jpg结尾
			String dirpath= System.getProperty("catalina.home")+"\\webapps\\ordergoods\\"+fileName;
			File file =new File(dirpath);
			StringBuilder errorMessageImage = new StringBuilder();

			HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步商品】【新增】【获取商品图片的HASH值】开始，图片路径："+dirpath+" 本地文件名："
					+ fileName +" 美团文件名imageName："+imageName+" 商品pluNO："+pluNO, jbpLogFileName);

			imageHash = WMJBPProductService.imageUpload(eId, shopId, imageName, file, errorMessageImage);
			
			if (imageHash != null && imageHash.isEmpty() == false)				
			{
				HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步商品】【新增】【获取商品图片的HASH值】成功，图片Hash值："+imageHash+" 本地文件名："
						+ fileName +" 美团文件名imageName："+imageName+ " 商品pluNO："+pluNO, jbpLogFileName);
				try 
				{
					UptBean ub_imageHash = null;	
					ub_imageHash = new UptBean("OC_GOODS");
					ub_imageHash.addUpdateValue("JBPHASH", new DataValue(imageHash,Types.VARCHAR));
					ub_imageHash.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

					ub_imageHash.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					ub_imageHash.addCondition("PLUNO", new DataValue(pluNO, Types.VARCHAR));
					ub_imageHash.addCondition("FILENAME", new DataValue(fileName, Types.VARCHAR));//公司别不一样，同一个pluno对应的图片名称加上了公司别
					ArrayList<DataProcessBean> DPBImageHash = new ArrayList<DataProcessBean>();
					DPBImageHash.add(new DataProcessBean(ub_imageHash));

					HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步商品】【新增】【获取商品图片的HASH值后回写OC_goods】开始，图片Hash值："+imageHash+" 文件名："
							+ fileName + " 商品pluNO："+pluNO, jbpLogFileName);
					this.doExecuteDataToDB(DPBImageHash);
					HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步商品】【新增】【获取商品图片的HASH值后回写OC_goods】成功，图片Hash值："+imageHash+" 文件名："
							+ fileName + " 商品pluNO："+pluNO, jbpLogFileName);						
				} 
				catch (Exception e) 
				{
					HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步商品】【新增】【获取商品图片的HASH值回写OC_goods】异常："+e.getMessage()+" 图片路径："+dirpath+" 本地文件名："
							+ fileName +" 美团文件名imageName："+imageName+ " 商品pluNO："+pluNO, jbpLogFileName);										
				}
			}
			else
			{
				HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步商品】【新增】【获取商品图片的HASH值】异常："+errorMessageImage.toString()+" 图片路径："+dirpath+" 本地文件名："
						+ fileName +" 美团文件名imageName："+imageName+" 商品pluNO："+pluNO, jbpLogFileName);				
			}
							
		}
		catch (Exception e) 
		{
			HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步商品】【新增】【获取商品图片的HASH值】异常："+e.getMessage()+" 本地文件名："
					+ fileName+ " 商品pluNO："+pluNO, jbpLogFileName);		
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
						new DataValue(shopId, Types.VARCHAR),//组织编号=门店编号
						new DataValue(shopId, Types.VARCHAR),//ERP门店				
						new DataValue(pluNO, Types.VARCHAR),//商品编码
						new DataValue(pluBarcode, Types.VARCHAR),//ERP条码
						new DataValue(pluSpecName, Types.VARCHAR),//规格名称
						new DataValue(orderShopNO, Types.VARCHAR),//外卖平台门店ID
						new DataValue("", Types.VARCHAR),//外卖平台门店名称
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
							new DataValue(shopId, Types.VARCHAR),//组织编号=门店编号
							new DataValue(shopId, Types.VARCHAR),//ERP门店				
							new DataValue(pluNO, Types.VARCHAR),//商品编码
							new DataValue("", Types.VARCHAR),//属性名称
							new DataValue("", Types.VARCHAR),//属性值
							new DataValue(orderShopNO, Types.VARCHAR),//外卖平台门店ID
							new DataValue("", Types.VARCHAR),//外卖平台门店名称
							new DataValue(orderPluNO, Types.VARCHAR),//外卖平台商品ID
							new DataValue(attriName, Types.VARCHAR),//外卖平台属性名称
							new DataValue(attriValue, Types.VARCHAR),//外卖平台属性值
							new DataValue("100", Types.VARCHAR)						
					};

					InsBean ib3 = new InsBean("OC_MAPPINGGOODS_ATTR", columns3);
					ib3.addValues(insValue2);
					DPB.add(new DataProcessBean(ib3));		
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
					new DataValue(shopId, Types.VARCHAR),//组织编号=门店编号
					new DataValue(shopId, Types.VARCHAR),//ERP门店				
					new DataValue(pluNO, Types.VARCHAR),//ERP商品编码
					new DataValue(pluName, Types.VARCHAR),//ERP商品名称
					new DataValue(categoryNO, Types.VARCHAR),//ERP商品分类编码
					new DataValue(orderShopNO, Types.VARCHAR),//外卖平台门店ID
					new DataValue("", Types.VARCHAR),//外卖平台门店名称
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
			HelpTools.writelog_fileName("【同步商品】【新增/修改】开始组装SQL语句！有异常:"+e.getMessage()+" 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+shopId+" 异常的商品平台ID/名称："+orderPluNO+"/"+orderPlunName, jbpLogFileName);
			return;
		}


		

		try 
		{
			HelpTools.writelog_fileName("【同步商品】【新增/修改】开始执行SQL语句！ 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+shopId+" 商品pluNO："+pluNO+" 平台商品orderpluNO："+orderPluNO, jbpLogFileName);  
			this.doExecuteDataToDB(DPB);
			HelpTools.writelog_fileName("【同步商品】【新增/修改】开始执行SQL语句！成功！ 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+shopId+" 商品pluNO："+pluNO+" 平台商品orderpluNO："+orderPluNO, jbpLogFileName);  

		} 
		catch (SQLSyntaxErrorException e)
		{
			HelpTools.writelog_fileName("【同步商品】【新增/修改】开始执行SQL语句！异常："+e.getMessage()+" 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+shopId+" 商品pluNO："+pluNO+" 平台商品orderpluNO："+orderPluNO, jbpLogFileName); 		
		}
		catch (Exception e) 
		{
			HelpTools.writelog_fileName("【同步商品】【新增/修改】开始执行SQL语句！异常："+e.getMessage()+" 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+shopId+" 商品pluNO："+pluNO+" 平台商品orderpluNO："+orderPluNO, jbpLogFileName); 		
		}
		
		
		
		
	}

	
	private void DeleteGoodsLocal(String eId,String shopId,String loadDocType,String orderShopNO,DCP_OrderTransTaskGoodsRes.level1Elm goodsModel) throws Exception
	{
		ArrayList<DataProcessBean> DPB = new ArrayList<DataProcessBean>();
		String orderPluNO = goodsModel.getOrderPluNO();
		//String orderPlunName = goodsModel.getOrderPluName() == null ? "" : goodsModel.getOrderPluName();
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
			HelpTools.writelog_fileName("【同步商品】【删除】开始执行SQL语句！ 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+shopId+" 商品pluNO："+pluNO+" 平台商品orderpluNO："+orderPluNO, jbpLogFileName);  
			this.doExecuteDataToDB(DPB);
			HelpTools.writelog_fileName("【同步商品】【删除】开始执行SQL语句！成功！ 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+shopId+" 商品pluNO："+pluNO+" 平台商品orderpluNO："+orderPluNO, jbpLogFileName);  

		} 
		catch (SQLSyntaxErrorException e)
		{
			HelpTools.writelog_fileName("【同步商品】【删除】开始执行SQL语句！异常："+e.getMessage()+" 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+shopId+" 商品pluNO："+pluNO+" 平台商品orderpluNO："+orderPluNO, jbpLogFileName); 		
		}
		catch (Exception e) 
		{
			HelpTools.writelog_fileName("【【同步商品】【删除】开始执行SQL语句！异常："+e.getMessage()+" 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+shopId+" 商品pluNO："+pluNO+" 平台商品orderpluNO："+orderPluNO, jbpLogFileName); 		
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
					new DataValue(shopId, Types.VARCHAR),//组织编号=门店编号
					new DataValue(shopId, Types.VARCHAR),//ERP门店				
					new DataValue(categoryNO, Types.VARCHAR),//ERP商品编码
					new DataValue(categoryName, Types.VARCHAR),//ERP商品名称
					new DataValue(orderShopNO, Types.VARCHAR),//ERP商品分类编码
					new DataValue(orderCategoryNO, Types.VARCHAR),//ERP商品分类编码
					new DataValue(orderCategoryName, Types.VARCHAR),//外卖平台门店ID				
					new DataValue(orderPriority, Types.VARCHAR),//	
					new DataValue("100", Types.VARCHAR)	
			};

			InsBean ib1 = new InsBean("OC_MAPPINGCATEGORY", columns1);
			ib1.addValues(insValue1);
			DPB.add(new DataProcessBean(ib1));
		}

		catch (Exception e) 
		{
			HelpTools.writelog_fileName("【同步分类】【新增/修改】开始组装SQL语句！有异常:"+e.getMessage()+" 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+shopId+" 异常的平台分类ID/名称："+orderCategoryNO+"/"+orderCategoryName, jbpLogFileName);
			return;		
		}
	
		try 
		{
			HelpTools.writelog_fileName("【同步分类】【新增/修改】开始执行SQL语句！ 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+shopId, jbpLogFileName);  
			this.doExecuteDataToDB(DPB);
			HelpTools.writelog_fileName("【同步分类】【新增/修改】开始执行SQL语句！成功！ 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+shopId, jbpLogFileName);  

		} 
		catch (SQLSyntaxErrorException e)
		{
			HelpTools.writelog_fileName("【同步分类】【新增/修改】开始执行SQL语句！异常："+e.getMessage()+" 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+shopId, jbpLogFileName); 		
		}
		catch (Exception e) 
		{
			HelpTools.writelog_fileName("【同步分类】【新增/修改】开始执行SQL语句！异常："+e.getMessage()+" 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+shopId, jbpLogFileName); 		
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
			HelpTools.writelog_fileName("【同步分类】【删除】开始执行SQL语句！ 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+shopId, jbpLogFileName);  
			this.doExecuteDataToDB(DPB);
			HelpTools.writelog_fileName("【同步分类】【删除】开始执行SQL语句！成功！ 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+shopId, jbpLogFileName);  

		} 
		catch (SQLSyntaxErrorException e)
		{
			HelpTools.writelog_fileName("【同步分类】【删除】开始执行SQL语句！异常："+e.getMessage()+" 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+shopId, jbpLogFileName); 		
		}
		catch (Exception e) 
		{
			HelpTools.writelog_fileName("【同步分类】【删除】开始执行SQL语句！异常："+e.getMessage()+" 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+shopId, jbpLogFileName); 		
		}
	}
	
	private void SetCategorySequence(String eId,String shopId,String loadDocType,String orderShopNO,DCP_OrderPlatformCategoryQueryRes.level1Elm catesModel) throws Exception
	{
		
	}
	
	private void SetCategoryGoodsSequence(String eId,String shopId,String loadDocType,String orderShopNO,DCP_OrderTransTaskGoodsRes.level1Elm goodsModel) throws Exception
	{
		String orderCategoryNO = goodsModel.getOrderCategoryNO();
		String updateKey = shopId+"&"+orderCategoryNO;
		if(UpdateCategoryGoodsSequenceList.contains(updateKey))//是否已经做过更新
		{
			HelpTools.writelog_fileName("【已经执行过】设置分类下商品排序！门店="+shopId+" 平台分类编号="+orderCategoryNO, "SetCategoryGoodsSequence");	 
			return;
		}
		
		
		String sql = " SELECT * FROM (";		
		sql +=" select A.*,B.PRIORITY AS PRIORITY2 from OC_mappinggoods A left join OC_goods B on A.EID=B.EID and A.PLUNO=B.PLUNO ";
		sql +=" and B.belfirm in (select belfirm from DCP_ORG C  where  C.EID='"+eId+"' and  C.Organizationno='"+shopId+"')";
		sql +=" where A.EID='"+eId+"' and  A.SHOPID='"+shopId+"' and A.load_doctype='"+loadDocType+"' and  A.order_categoryno='"+orderCategoryNO+"' order BY B.PRIORITY,A.Priority";
		sql +=")";
		HelpTools.writelog_fileName("排序SQL："+sql, "SetCategoryGoodsSequence");
		
		List<Map<String, Object>> getData = this.doQueryData(sql, null);
		if(getData==null||getData.isEmpty())
			return;
		
		int sequence = 0;
		List<WMJBPGoodsUpdate> disheslist = new ArrayList<WMJBPGoodsUpdate>();
		
		for (Map<String, Object> map : getData) 
		{
			sequence++;
			WMJBPGoodsUpdate dishes = new WMJBPGoodsUpdate();
			dishes.setePoiId(eId+"_"+shopId);
			dishes.setEDishCode(map.get("PLUNO").toString());
			dishes.setCategoryName(map.get("ORDER_CATEGORYNO").toString());  
			dishes.setDishName(map.get("ORDER_PLUNAME").toString());				
			dishes.setSequence(sequence);
			disheslist.add(dishes);			
		}
		
		StringBuilder errorMessage = new StringBuilder();	
	  boolean	nRet = WMJBPProductService.batchUpload(eId, shopId, disheslist, errorMessage);
	  if(nRet)
	  {
	  	HelpTools.writelog_fileName("设置分类下商品排序成功！门店="+shopId+" 平台分类编号="+orderCategoryNO, "SetCategoryGoodsSequence");	  	
	  }
	  else
	  {
	  	HelpTools.writelog_fileName("设置分类下商品排序失败！异常："+errorMessage.toString()+" 门店="+shopId+" 平台分类编号="+orderCategoryNO, "SetCategoryGoodsSequence");	  	
	  }
	  UpdateCategoryGoodsSequenceList.add(updateKey);
	  
	}
	
}
