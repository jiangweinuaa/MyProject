package com.dsc.spos.scheduler.job;

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
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.res.DCP_OrderPlatformCategoryQueryRes;
import com.dsc.spos.json.cust.res.DCP_OrderPlatformGoodsQueryRes;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.WMELMProductService;
import com.dsc.spos.waimai.WMJBPProductService;
import com.dsc.spos.waimai.model.WMJBPGoodsUpdate;

import eleme.openapi.sdk.api.entity.product.OCategory;
import eleme.openapi.sdk.api.entity.product.OItem;
import eleme.openapi.sdk.api.entity.product.OSpec;
import eleme.openapi.sdk.api.enumeration.product.OItemCreateProperty;
import eleme.openapi.sdk.api.enumeration.product.OItemUpdateProperty;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class OrderTransTaskJBPProcess_old extends InitJob
{
  Logger logger = LogManager.getLogger(OrderTransTaskJBPProcess_old.class.getName());
	
	static boolean bRun=false;//标记此服务是否正在执行中
	String jddjLogFileName = "orderTransTaskJBPlog";
	
	public OrderTransTaskJBPProcess_old ()
	{
		
	}
	
	public String doExe() throws Exception
	{
    String sReturnInfo = "";
		
		HelpTools.writelog_fileName("【同步任务orderTranTasklog】同步START！",jddjLogFileName);
		try 
		{
			 //此服务是否正在执行中
			if (bRun)
			{		
				logger.info("\r\n*********同步任务orderTranTasklog同步正在执行中,本次调用取消:************\r\n");
				HelpTools.writelog_fileName("【同步任务orderTranTasklog】同步正在执行中,本次调用取消！",jddjLogFileName);
				return sReturnInfo;
			}

			bRun=true;//			
			String loadDocType = "2";//饿了么
			List<Map<String, Object>> transTask = this.GetTransType(loadDocType);			
			if(transTask!=null&&transTask.isEmpty()==false)
			{
				for (Map<String, Object> map : transTask) 
				{
					try 
					{
						String transID =  map.get("TRANS_ID").toString();
						String eId = map.get("EID").toString();
						String transType = map.get("TRANS_TYPE").toString();
						/*String loadDocType = map.get("LOAD_DOCTYPE").toString();*/
						String erpShopNO = map.get("SHOPID").toString();
						String pluNO = map.get("PLUNO").toString();
						String categoryNO = map.get("CATEGORYNO").toString();
						HelpTools.writelog_fileName("【同步任务orderTranTasklog】开始循环任务表OC_TRANSTASK！任务类型：" + transType + " 任务ID："
							+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+erpShopNO+" 商品分类categoryNO："+categoryNO+" 商品pluNO："+pluNO, jddjLogFileName);
																
						if(transType.equals("1")||transType.equals("2")||transType.equals("3"))//1、分类新增   2、分类修改 3、分类删除
						{
							if(categoryNO ==null||categoryNO.isEmpty()||categoryNO.trim().length()==0)
							{
								HelpTools.writelog_fileName("【同步任务orderTranTasklog】同步当前任务异常！任务类型：" + transType + " 商品分类categoryNO："+" 为空！",jddjLogFileName);
								continue;
							}
						  //查询分类的SQL
							StringBuffer sqlCategoryNO = new StringBuffer( " select * from OC_mappingcategory where EID='"+eId+"' and LOAD_DOCTYPE='"+loadDocType+"' and SHOPID='"+erpShopNO+"' and CATEGORYNO='"+categoryNO+"' ");
						  if(transType.equals("3"))
						  {
						  	sqlCategoryNO .append( " and STATUS='0' ");
						  }
						  else
						  {
						  	sqlCategoryNO .append( " and status='100' ");
						  }
							
						  List<Map<String, Object>> cates = this.doQueryData(sqlCategoryNO.toString(), null);
						  if (cates == null || cates.isEmpty() )
							{
								HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步分类】需要同步的数据为空！任务ID："
										+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+erpShopNO+" 商品分类categoryNO："+categoryNO, jddjLogFileName);
								continue;
							}
						  DCP_OrderPlatformCategoryQueryRes res = new DCP_OrderPlatformCategoryQueryRes();
						  DCP_OrderPlatformCategoryQueryRes.level1Elm catesModel = res.new level1Elm();
						  //String categoryNO = map.get("CATEGORYNO").toString();//主键 为空格
							String categoryName = cates.get(0).get("CATEGORYNAME").toString();;
							String orderCategoryNO = cates.get(0).get("ORDER_CATEGORYNO").toString();
							String orderCategoryName = cates.get(0).get("ORDER_CATEGORYNAME").toString();
							String orderDescription = "";
							String orderPriority = cates.get(0).get("PRIORITY").toString();;
							
							catesModel.setCategoryNO(categoryNO);
							catesModel.setCategoryName(categoryName);
							catesModel.setOrderCategoryNO(orderCategoryNO);
							catesModel.setOrderCategoryName(orderCategoryName);
							catesModel.setOrderDescription(orderDescription);
							catesModel.setOrderPriority(orderPriority);
							
						  //调用第三方接口 并且更新任务表
							if(transType.equals("1"))
							{
								//分类新增							
								this.CallCatesAddAPIFun(transType,transID, eId, erpShopNO, loadDocType, categoryNO, catesModel);
								
							}
							else if(transType.equals("2"))
							{
								//分类更新
								this.CallCatesUpdateAPIFun(transType,transID, eId, erpShopNO, loadDocType,  categoryNO, catesModel);
							}
							else 
							{
								//分类删除
								this.CallCatesDeleteAPIFun(transType,transID, eId, erpShopNO, loadDocType, categoryNO, catesModel);
					
				      }
							
						}					
						else if (transType.equals("4")||transType.equals("5")||transType.equals("6")) //4、商品新增  5、商品修改 6、商品删除
						{
							if(pluNO ==null||pluNO.isEmpty()||pluNO.trim().length()==0)
							{								
								HelpTools.writelog_fileName("【同步任务orderTranTasklog】同步当前任务异常！任务类型：" + transType + " 商品pluNO："+" 为空！",jddjLogFileName);
								continue;						
							}
						  //查询PLUNO，暂时不关联属性表，后续需要关联，需要过滤规格表的重复数据
							StringBuffer	sqlPluNO = new StringBuffer("select A.*,B.SPECNO,B.SPECNAME,B.ORDER_SPECNO,B.ORDER_SPECNAME,B.Price,B.STOCKQTY,B.PACKAGEFEE,B.ISONSHELF from OC_mappinggoods A inner join OC_mappinggoods_spec B on A.EID=B.EID and A.Load_Doctype=B.LOAD_DOCTYPE and A.SHOPID=B.SHOPID and A.PLUNO=B.PLUNO");
							sqlPluNO .append( " where A.EID='"+eId+"' and A.LOAD_DOCTYPE='"+loadDocType+"' and A.SHOPID='"+erpShopNO+"' and A.PLUNO='"+pluNO+"' ");
							
							if(transType.equals("6"))
						  {
								sqlPluNO .append(" AND A.STATUS='0' ");
						  }
							else
							{
								if(transType.equals("4"))//商品新增SQL特殊点， 需要关联下OC_mappingcategory规格表
								{
                                    sqlPluNO.setLength(0);
									sqlPluNO.append( "select A.*,B.SPECNO,B.SPECNAME,B.ORDER_SPECNO,B.ORDER_SPECNAME,B.Price,B.STOCKQTY,B.PACKAGEFEE,B.ISONSHELF,C.Order_Categoryno as Order_Categoryno2,C.Order_Categoryname as Order_Categoryname2 from OC_mappinggoods A inner join OC_mappinggoods_spec B on A.EID=B.EID and A.Load_Doctype=B.LOAD_DOCTYPE and A.SHOPID=B.SHOPID and A.PLUNO=B.PLUNO");
									sqlPluNO .append( " left join OC_mappingcategory C on A.EID =C.EID and A.SHOPID=C.SHOPID AND A.Load_Doctype=C.LOAD_DOCTYPE AND A.CATEGORYNO=C.CATEGORYNO ");
									sqlPluNO .append( " where A.EID='"+eId+"' and A.LOAD_DOCTYPE='"+loadDocType+"' and A.SHOPID='"+erpShopNO+"' and A.PLUNO='"+pluNO+"' ");
																	
								}
								
								
								sqlPluNO .append(" AND A.status='100'");
							}
							HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步商品】需要同步的数据sql:"+sqlPluNO.toString()+" 任务ID："
									+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+erpShopNO+" 商品分类categoryNO："+categoryNO+" 商品pluNO："+pluNO, jddjLogFileName);
												
							List<Map<String, Object>> goods = this.doQueryData(sqlPluNO.toString(), null);
							if (goods == null || goods.isEmpty() )
							{
								HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步商品】需要同步的数据为空！任务ID："
										+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+erpShopNO+" 商品分类categoryNO："+categoryNO+" 商品pluNO："+pluNO, jddjLogFileName);
								continue;
							}
							DCP_OrderPlatformGoodsQueryRes res =new DCP_OrderPlatformGoodsQueryRes();
							DCP_OrderPlatformGoodsQueryRes.level1Elm goodsModel = res.new level1Elm();
							String orderPluNO = goods.get(0).get("ORDER_PLUNO").toString();
							String orderPluName = goods.get(0).get("ORDER_PLUNAME").toString();;
							String orderCategoryNO = goods.get(0).get("ORDER_CATEGORYNO").toString();
							String orderCategoryName = goods.get(0).get("ORDER_CATEGORYNAME").toString();
							String unit = goods.get(0).get("UNIT").toString();
							String pricture = goods.get(0).get("FILENAME").toString();
							String description = goods.get(0).get("DESCRIPTION").toString();
							if(transType.equals("4"))//商品新增 的时候，分类关联下
							{
								orderCategoryNO = goods.get(0).get("ORDER_CATEGORYNO2").toString();
								orderCategoryName = goods.get(0).get("ORDER_CATEGORYNAME2").toString();
							}
							String pluName = goods.get(0).get("PLUNAME").toString();
							goodsModel.setOrderPluNO(orderPluNO);
							goodsModel.setOrderPluName(orderPluName);
							goodsModel.setOrderCategoryNO(orderCategoryNO);
							goodsModel.setOrderCategoryName(orderCategoryName);
							goodsModel.setPluName(pluName);
							goodsModel.setPluNO(pluNO);
							goodsModel.setOrderUnit(unit);
							goodsModel.setOrderImageUrl(pricture);
							goodsModel.setOrderDescription(description);
					
							goodsModel.setSpecs(new ArrayList<DCP_OrderPlatformGoodsQueryRes.level2Spec>());
							for (Map<String, Object> oneData : goods) 
							{
								DCP_OrderPlatformGoodsQueryRes.level2Spec oneDataSpec = res.new level2Spec();
								String orderSpecNO = oneData.get("ORDER_SPECNO").toString();
								String orderSpecName = oneData.get("ORDER_SPECNAME").toString();
								String price = oneData.get("PRICE").toString();
								String stockQty = oneData.get("STOCKQTY").toString();
								String packageFee = oneData.get("PACKAGEFEE").toString();
								String onShelf = oneData.get("ISONSHELF").toString();
								String specNO = oneData.get("SPECNO").toString();
								String specName = oneData.get("SPECNAME").toString();
								
								oneDataSpec.setOrderSpecID(orderSpecNO);
								oneDataSpec.setOrderSpecName(orderSpecName);
								oneDataSpec.setOrderPrice(price);
								oneDataSpec.setOrderStock(stockQty);
								oneDataSpec.setOrderOnShelf(onShelf);
								oneDataSpec.setOrderPackingFee(packageFee);
								oneDataSpec.setPluBarcode(specNO);
								oneDataSpec.setPluSpecName(specName);
								
								goodsModel.getSpecs().add(oneDataSpec);
								oneDataSpec=null;
								
							}
							
							//调用第三方接口 并且更新任务表
							if(transType.equals("4"))
							{
								//商品新增							
								this.CallGoodsAddAPIFun(transType,transID, eId, erpShopNO, loadDocType, pluNO, orderCategoryNO, goodsModel);
								
							}
							else if(transType.equals("5"))
							{
								//商品更新
								this.CallGoodsUpdateAPIFun(transType,transID, eId, erpShopNO, loadDocType, orderPluNO, orderCategoryNO, goodsModel);
							}
							else 
							{
								//商品删除
								this.CallGoodsDeleteAPIFun(transType,transID, eId, erpShopNO, loadDocType, orderPluNO, orderCategoryNO, goodsModel);
					
				      }
																		
				
			      }
						else //其他类型 暂不支持
						{
							
							HelpTools.writelog_fileName("【同步任务orderTranTasklog】同步当前任务异常【任务类型未知】！任务类型：" + transType + " 商品pluNO："+" 为空！",jddjLogFileName);
							continue;											
			      }
									
			    } 
					catch (Exception e) 
					{
			
			    }
			
		    }
				
			}
			else
			{			
				sReturnInfo="无符合要求的数据！";
				HelpTools.writelog_fileName("【同步任务orderTranTasklog】没有需要处理任务！",jddjLogFileName);
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
	 * @param erpShopNO
	 * @param loadDocType
	 * @param categoryNO
	 * @param catesModel
	 * @throws Exception
	 */
	private void CallCatesAddAPIFun(String transType,String transID,String eId,String erpShopNO,String loadDocType,String categoryNO,DCP_OrderPlatformCategoryQueryRes.level1Elm catesModel) throws Exception
	{
		//String orderCategoryNO = catesModel.getOrderCategoryNO();	
		String orderShopNO = catesModel.getOrderShopNO();

		String categoryName = catesModel.getCategoryName();
		String description = catesModel.getOrderDescription();
		String priority = catesModel.getOrderPriority();
		int sequence = 0;
		try 
		{
			
			sequence = Integer.parseInt(priority);
	  } 
		catch (Exception e) 
		{
		
	  }
   
		
		StringBuilder errorMessage = new StringBuilder();
		boolean nRet = false;
		String trans_flg = "2";
		
		nRet = WMJBPProductService.updateCat(eId, erpShopNO, "", categoryName, sequence, errorMessage);
		
		HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步分类】调用第三方接口完成!第三方返回:"+errorMessage.toString()+" 任务ID："
				+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+erpShopNO+" 商品分类categoryNO："+categoryNO, jddjLogFileName);
		if(nRet)
		{
			trans_flg = "1";
		}
				
		//更新任务状态
	  // values
		Map<String, DataValue> values = new HashMap<String, DataValue>();
		DataValue v = new DataValue(trans_flg, Types.VARCHAR);
		values.put("TRANS_FLG", v);
		DataValue v1 = new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()) , Types.VARCHAR);
		values.put("UPDATE_TIME", v1);

		// condition
		Map<String, DataValue> conditions = new HashMap<String, DataValue>();
		DataValue c1 = new DataValue(eId, Types.VARCHAR);
		conditions.put("EID", c1);
		DataValue c2 = new DataValue(loadDocType, Types.VARCHAR);
		conditions.put("LOAD_DOCTYPE", c2);
		DataValue c3 = new DataValue(transType, Types.VARCHAR);
		conditions.put("TRANS_TYPE", c3);
		DataValue c4 = new DataValue(transID, Types.VARCHAR);
		conditions.put("TRANS_ID", c4);
		DataValue c5 = new DataValue(erpShopNO, Types.VARCHAR);
		conditions.put("SHOPID", c5);
		DataValue c6 = new DataValue(categoryNO, Types.VARCHAR);
		conditions.put("CATEGORYNO", c6);

		this.doUpdate("OC_transtask", values, conditions);
		
		HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步分类更新任务表】完成!任务状态TRANS_FLG:"+trans_flg+" 任务ID："
				+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+erpShopNO+" 商品分类categoryNO："+categoryNO, jddjLogFileName);
		
		
		if(nRet)
		{
			HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步分类回写分类映射表】开始!任务状态TRANS_FLG:"+trans_flg+" 任务ID："
					+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+erpShopNO+" 商品分类categoryNO："+categoryNO, jddjLogFileName);
			
			String orderCategoryNO = categoryName;//美团分类编码=名称
			String orderCategoryName = categoryName;
			
		 // values
			Map<String, DataValue> values1 = new HashMap<String, DataValue>();
			
			values1.put("ORDER_CATEGORYNAME",  new DataValue(orderCategoryName, Types.VARCHAR));
			values1.put("ORDER_CATEGORYNO",  new DataValue(orderCategoryNO, Types.VARCHAR));
			//values1.put("ORDER_CATEGORYNO",  new DataValue(orderDescription, Types.VARCHAR));
			values1.put("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

			// condition
			Map<String, DataValue> conditions1 = new HashMap<String, DataValue>();
			conditions1.put("EID", new DataValue(eId, Types.VARCHAR));
			conditions1.put("LOAD_DOCTYPE", new DataValue(loadDocType, Types.VARCHAR));				
			conditions1.put("SHOPID", new DataValue(erpShopNO, Types.VARCHAR));					
			conditions1.put("CATEGORYNO", new DataValue(categoryNO, Types.VARCHAR));
			
			this.doUpdate("OC_mappingcategory", values1, conditions1);
			HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步分类回写分类映射表】完成!任务状态TRANS_FLG:"+trans_flg+" 任务ID："
					+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+erpShopNO+" 商品分类categoryNO："+categoryNO, jddjLogFileName);
			
		}
		
	}
	
	/**
	 * 分类修改
	 * @param transType
	 * @param transID
	 * @param eId
	 * @param erpShopNO
	 * @param loadDocType
	 * @param categoryNO
	 * @param catesModel
	 * @throws Exception
	 */
	private void CallCatesUpdateAPIFun(String transType,String transID,String eId,String erpShopNO,String loadDocType,String categoryNO,DCP_OrderPlatformCategoryQueryRes.level1Elm catesModel) throws Exception
	{
		String orderCategoryName = catesModel.getOrderCategoryName();	//聚宝盆分类编号==分类名称
		String categoryName = catesModel.getCategoryName();
		String priority = catesModel.getOrderPriority();
		int sequence = 0;
		try 
		{
			
			sequence = Integer.parseInt(priority);
	  } 
		catch (Exception e) 
		{
		
	  }
   
		
		StringBuilder errorMessage = new StringBuilder();
		boolean nRet = false;
		String trans_flg = "2";
		nRet = WMJBPProductService.updateCat(eId, erpShopNO, orderCategoryName, categoryName, sequence, errorMessage);
		HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步分类更新商品分类】调用第三方接口完成!第三方返回:"+errorMessage.toString()+" 任务ID："
				+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+erpShopNO+" 商品分类categoryNO："+categoryNO, jddjLogFileName);
		if(nRet)
		{
			trans_flg = "1";
		}
		
	
				
		//更新任务状态
	  // values
		Map<String, DataValue> values = new HashMap<String, DataValue>();
		DataValue v = new DataValue(trans_flg, Types.VARCHAR);
		values.put("TRANS_FLG", v);
		DataValue v1 = new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()) , Types.VARCHAR);
		values.put("UPDATE_TIME", v1);

		// condition
		Map<String, DataValue> conditions = new HashMap<String, DataValue>();
		DataValue c1 = new DataValue(eId, Types.VARCHAR);
		conditions.put("EID", c1);
		DataValue c2 = new DataValue(loadDocType, Types.VARCHAR);
		conditions.put("LOAD_DOCTYPE", c2);
		DataValue c3 = new DataValue(transType, Types.VARCHAR);
		conditions.put("TRANS_TYPE", c3);
		DataValue c4 = new DataValue(transID, Types.VARCHAR);
		conditions.put("TRANS_ID", c4);
		DataValue c5 = new DataValue(erpShopNO, Types.VARCHAR);
		conditions.put("SHOPID", c5);
		DataValue c6 = new DataValue(categoryNO, Types.VARCHAR);
		conditions.put("CATEGORYNO", c6);

		this.doUpdate("OC_transtask", values, conditions);
		HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步分类更新任务表】完成!任务状态TRANS_FLG:"+trans_flg+" 任务ID："
				+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+erpShopNO+" 商品分类categoryNO："+categoryNO, jddjLogFileName);
		
		
		if(nRet)
		{
			HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步分类回写分类映射表】开始!任务状态TRANS_FLG:"+trans_flg+" 任务ID："
					+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+erpShopNO+" 商品分类categoryNO："+categoryNO, jddjLogFileName);
			
		 // values
			Map<String, DataValue> values1 = new HashMap<String, DataValue>();
			
			values1.put("ORDER_CATEGORYNO",  new DataValue(categoryName, Types.VARCHAR));//美团聚宝盆 分类编码和名称一样
			values1.put("ORDER_CATEGORYNAME",  new DataValue(categoryName, Types.VARCHAR));
			values1.put("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

			// condition
			Map<String, DataValue> conditions1 = new HashMap<String, DataValue>();
			conditions1.put("EID", new DataValue(eId, Types.VARCHAR));
			conditions1.put("LOAD_DOCTYPE", new DataValue(loadDocType, Types.VARCHAR));				
			conditions1.put("SHOPID", new DataValue(erpShopNO, Types.VARCHAR));					
			conditions1.put("CATEGORYNO", new DataValue(categoryNO, Types.VARCHAR));
			
			this.doUpdate("OC_mappingcategory", values1, conditions1);
			HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步分类回写分类映射表】完成!任务状态TRANS_FLG:"+trans_flg+" 任务ID："
					+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+erpShopNO+" 商品分类categoryNO："+categoryNO, jddjLogFileName);
			
			
		}
		
	}
	
	
	/**
	 * 分类删除
	 * @param transType
	 * @param transID
	 * @param eId
	 * @param erpShopNO
	 * @param loadDocType
	 * @param categoryNO
	 * @param catesModel
	 * @throws Exception
	 */
	private void CallCatesDeleteAPIFun(String transType,String transID,String eId,String erpShopNO,String loadDocType,String categoryNO,DCP_OrderPlatformCategoryQueryRes.level1Elm catesModel) throws Exception
	{
		String orderCategoryNO = catesModel.getOrderCategoryNO();	
   
		
		StringBuilder errorMessage = new StringBuilder();
		boolean nRet = false;
		String trans_flg = "2";
		
		nRet = WMJBPProductService.deleteCat(eId, erpShopNO, orderCategoryNO, errorMessage);
		HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步分类删除分类】调用第三方接口完成!第三方返回:"+errorMessage.toString()+" 任务ID："
				+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+erpShopNO+" 商品分类categoryNO："+categoryNO, jddjLogFileName);
		if(nRet)
		{
			trans_flg = "1";
		}
		
				
		//更新任务状态
	  // values
		Map<String, DataValue> values = new HashMap<String, DataValue>();
		DataValue v = new DataValue(trans_flg, Types.VARCHAR);
		values.put("TRANS_FLG", v);
		DataValue v1 = new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()) , Types.VARCHAR);
		values.put("UPDATE_TIME", v1);

		// condition
		Map<String, DataValue> conditions = new HashMap<String, DataValue>();
		DataValue c1 = new DataValue(eId, Types.VARCHAR);
		conditions.put("EID", c1);
		DataValue c2 = new DataValue(loadDocType, Types.VARCHAR);
		conditions.put("LOAD_DOCTYPE", c2);
		DataValue c3 = new DataValue(transType, Types.VARCHAR);
		conditions.put("TRANS_TYPE", c3);
		DataValue c4 = new DataValue(transID, Types.VARCHAR);
		conditions.put("TRANS_ID", c4);
		DataValue c5 = new DataValue(erpShopNO, Types.VARCHAR);
		conditions.put("SHOPID", c5);
		DataValue c6 = new DataValue(categoryNO, Types.VARCHAR);
		conditions.put("CATEGORYNO", c6);

		this.doUpdate("OC_transtask", values, conditions);
		HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步分类更新任务表】完成!任务状态TRANS_FLG:"+trans_flg+" 任务ID："
				+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+erpShopNO+" 商品分类categoryNO："+categoryNO, jddjLogFileName);
		
		
		if(nRet)
		{
			HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步分类回写分类映射表】开始!任务状态TRANS_FLG:"+trans_flg+" 任务ID："
					+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+erpShopNO+" 商品分类categoryNO："+categoryNO, jddjLogFileName);
			
		// values
			Map<String, DataValue> values1 = new HashMap<String, DataValue>();
			
			values1.put("STATUS",  new DataValue("N", Types.VARCHAR));
			values1.put("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

			// condition
			Map<String, DataValue> conditions1 = new HashMap<String, DataValue>();
			conditions1.put("EID", new DataValue(eId, Types.VARCHAR));
			conditions1.put("LOAD_DOCTYPE", new DataValue(loadDocType, Types.VARCHAR));				
			conditions1.put("SHOPID", new DataValue(erpShopNO, Types.VARCHAR));					
			conditions1.put("CATEGORYNO", new DataValue(categoryNO, Types.VARCHAR));
			
			this.doUpdate("OC_mappingcategory", values1, conditions1);
			HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步分类回写分类映射表】完成!任务状态TRANS_FLG:"+trans_flg+" 任务ID："
					+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+erpShopNO+" 商品分类categoryNO："+categoryNO, jddjLogFileName);
			
		}
			
			
	}
	
	
	/**
	 * 商品新增
	 * @param transType
	 * @param transID
	 * @param eId
	 * @param erpShopNO
	 * @param loadDocType
	 * @param pluNO
	 * @param categoryNO
	 * @param goodsModel
	 * @throws Exception
	 */
	private void CallGoodsAddAPIFun(String transType,String transID,String eId,String erpShopNO,String loadDocType,String pluNO,String categoryNO,DCP_OrderPlatformGoodsQueryRes.level1Elm goodsModel) throws Exception
	{
		try 
		{
			
		
	  } 
		catch (Exception e) 
		{
		
	  }
		
		
		List<WMJBPGoodsUpdate> goodsList = new ArrayList<WMJBPGoodsUpdate>();
		WMJBPGoodsUpdate goods = new WMJBPGoodsUpdate();
		String orderPluNO = goodsModel.getOrderPluNO();
		String orderPluName = goodsModel.getOrderPluName();
		String orderCategoryNO = goodsModel.getOrderCategoryNO();
		String orderCategoryName = goodsModel.getOrderCategoryName();
		String pluName = goodsModel.getPluName();
		String picture = goodsModel.getOrderImageUrl();	//这里取的是文件名
		String priority = goodsModel.getOrderPriority();
		String unit = goodsModel.getOrderUnit();
		
		String epoiId = eId + "_" + erpShopNO;
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
		String surl=PosPub.getDCP_URL(eId);
		surl=surl.replace("sposWeb/services/jaxrs/sposService/invoke", "ordergoods");
		surl+="/"+picture;
		HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步商品】【新增】调用第三方接口！图片地址:"+surl+" 任务ID："
				+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+erpShopNO+" 商品分类categoryNO："+categoryNO+" 商品pluNO："+pluNO, jddjLogFileName);
		
		String description = goodsModel.getOrderDescription();//商品描述
		/*goods.setBoxNum(1);
		goods.setBoxPrice(0);*/
		goods.setCategoryName(orderCategoryName);
		goods.setDescription(description);
		goods.setDishName(pluName);
		goods.setEDishCode(pluNO);
		goods.setePoiId(epoiId);
		goods.setIsSoldOut(0);
		goods.setMinOrderCount(1);
		goods.setUnit(unit);
		if (surl != null && surl.isEmpty() == false)
		{
			goods.setPicture(surl);
		}
		if(isSequence)
		{
			goods.setSequence(sequence);
		}
		goods.setUnit(unit);
		goods.setSkus(new ArrayList<WMJBPGoodsUpdate.Skus>());
			
		for (DCP_OrderPlatformGoodsQueryRes.level2Spec oneData : goodsModel.getSpecs()) 
		{																
			String orderSpecNO = oneData.getOrderSpecID();
			String orderSpecName = oneData.getOrderSpecName();
			String price = oneData.getOrderPrice();
			String stockQty = oneData.getOrderStock();
			String packageFee = oneData.getOrderPackingFee();
			String onShelf = oneData.getOrderOnShelf();
			String specNO = oneData.getPluBarcode();
			String specName = oneData.getPluSpecName();
			int onShelf_i = 1;							
			if (onShelf != null && onShelf.equals("N"))
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
			WMJBPGoodsUpdate.Skus spec = new WMJBPGoodsUpdate.Skus();
			spec.setSkuId(specNO);
			spec.setSpec(specName);
			spec.setStock(stockQty_i);
			spec.setPrice(price_d);
			spec.setBoxNum(1);
			spec.setBoxPrice(0);
			
			goods.getSkus().add(spec);

    }
		
		goodsList.add(goods);
		
		StringBuilder errorMessage = new StringBuilder();
		boolean nRet = false;
		String trans_flg = "2";
			
		nRet = WMJBPProductService.batchUpload(eId, erpShopNO, goodsList, errorMessage);
		HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步商品】【新增】调用第三方接口！第三方返回:"+errorMessage.toString()+" 任务ID："
				+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+erpShopNO+" 商品分类categoryNO："+categoryNO+" 商品pluNO："+pluNO, jddjLogFileName);
		
		
		
		if(nRet)
		{
			trans_flg = "1";
		}
			
		//更新任务状态
	  // values
		Map<String, DataValue> values = new HashMap<String, DataValue>();
		DataValue v = new DataValue(trans_flg, Types.VARCHAR);
		values.put("TRANS_FLG", v);
		DataValue v1 = new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()) , Types.VARCHAR);
		values.put("UPDATE_TIME", v1);

		// condition
		Map<String, DataValue> conditions = new HashMap<String, DataValue>();
		DataValue c1 = new DataValue(eId, Types.VARCHAR);
		conditions.put("EID", c1);
		DataValue c2 = new DataValue(loadDocType, Types.VARCHAR);
		conditions.put("LOAD_DOCTYPE", c2);
		DataValue c3 = new DataValue(transType, Types.VARCHAR);
		conditions.put("TRANS_TYPE", c3);
		DataValue c4 = new DataValue(transID, Types.VARCHAR);
		conditions.put("TRANS_ID", c4);
		DataValue c5 = new DataValue(erpShopNO, Types.VARCHAR);
		conditions.put("SHOPID", c5);
		DataValue c6 = new DataValue(pluNO, Types.VARCHAR);
		conditions.put("PLUNO", c6);

		this.doUpdate("OC_transtask", values, conditions);
		
		HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步商品】【新增】任务完成！【更新任务表】完成!任务状态TRANS_FLG:"+trans_flg+" 任务ID："
				+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+erpShopNO+" 商品分类categoryNO："+categoryNO+" 商品pluNO："+pluNO, jddjLogFileName);
		
		
		//回写 平台商品ID 规格ID 
		if(nRet ==false)
		{
			return;
		}
		
		
		
		UptBean ub1 = null;	
		ub1 = new UptBean("OC_mappinggoods");
		ub1.addUpdateValue("ORDER_PLUNO", new DataValue(pluNO,Types.VARCHAR));
		ub1.addUpdateValue("ORDER_PLUNAME", new DataValue(pluName,Types.VARCHAR));
		ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

		ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		ub1.addCondition("LOAD_DOCTYPE", new DataValue(loadDocType, Types.VARCHAR));
		ub1.addCondition("SHOPID", new DataValue(erpShopNO, Types.VARCHAR));
		ub1.addCondition("PLUNO", new DataValue(pluNO, Types.VARCHAR));
									
		ArrayList<DataProcessBean> DPB = new ArrayList<DataProcessBean>();
		DPB.add(new DataProcessBean(ub1));	
		for (WMJBPGoodsUpdate.Skus nRetSpec : goods.getSkus()) 
		{
			String specNO = nRetSpec.getSkuId();
			String specName = nRetSpec.getSpec();
			UptBean ub2 = null;	
			ub2 = new UptBean("OC_mappinggoods_spec");
			ub2.addUpdateValue("ORDER_PLUNO", new DataValue(pluNO,Types.VARCHAR));
			ub2.addUpdateValue("ORDER_SPECNO", new DataValue(specNO,Types.VARCHAR));
			ub2.addUpdateValue("ORDER_SPECNAME", new DataValue(specName,Types.VARCHAR));
			ub2.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

			ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			ub2.addCondition("LOAD_DOCTYPE", new DataValue(loadDocType, Types.VARCHAR));
			ub2.addCondition("SHOPID", new DataValue(erpShopNO, Types.VARCHAR));
			ub2.addCondition("PLUNO", new DataValue(pluNO, Types.VARCHAR));
			DPB.add(new DataProcessBean(ub2));
		
	  }
	
		
		try 
		{
			HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步商品】【新增】任务完成！【回写OC_mappinggoods与sepc规格表】开始!任务状态TRANS_FLG:"+trans_flg+" 任务ID："
					+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+erpShopNO+" 商品分类categoryNO："+categoryNO+" 商品pluNO："+pluNO, jddjLogFileName);
			
			this.doExecuteDataToDB(DPB);
			HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步商品】【新增】任务完成！【回写OC_mappinggoods与sepc规格表】成功!任务状态TRANS_FLG:"+trans_flg+" 任务ID："
					+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+erpShopNO+" 商品分类categoryNO："+categoryNO+" 商品pluNO："+pluNO, jddjLogFileName);
	  } 
		catch (Exception e) 
		{
			HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步商品】【新增】任务完成！【回写OC_mappinggoods与sepc规格表】异常："+e.getMessage()+"任务状态TRANS_FLG:"+trans_flg+" 任务ID："
					+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+erpShopNO+" 商品分类categoryNO："+categoryNO+" 商品pluNO："+pluNO, jddjLogFileName);
		
	  }
		
			
	}
	
	/**
	 * 商品更新调用第三方接口，并更新任务表
	 * @param transType
	 * @param transID
	 * @param eId
	 * @param erpShopNO
	 * @param loadDocType
	 * @param pluNO
	 * @param categoryNO
	 * @param goodsModel
	 * @throws Exception
	 */
	private void CallGoodsUpdateAPIFun(String transType,String transID,String eId,String erpShopNO,String loadDocType,String pluNO,String categoryNO,DCP_OrderPlatformGoodsQueryRes.level1Elm goodsModel) throws Exception
	{
		
		List<WMJBPGoodsUpdate> goodsList = new ArrayList<WMJBPGoodsUpdate>();
		WMJBPGoodsUpdate goods = new WMJBPGoodsUpdate();
		String orderPluNO = goodsModel.getOrderPluNO();
		String orderPluName = goodsModel.getOrderPluName();
		String orderCategoryNO = goodsModel.getOrderCategoryNO();
		String orderCategoryName = goodsModel.getOrderCategoryName();
		String pluName = goodsModel.getPluName();
		String picture = goodsModel.getOrderImageUrl();	
		String priority = goodsModel.getOrderPriority();
		String unit = goodsModel.getOrderUnit();
		 
		
		int isOnShelf_PluNO = 0;//0-未售完，1-售完
		String epoiId = eId + "_" + erpShopNO;
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
		
		String description = goodsModel.getOrderDescription();//商品描述
		/*goods.setBoxNum(1);
		goods.setBoxPrice(0);*/
		goods.setCategoryName(orderCategoryName);
		goods.setDescription(description);
		goods.setDishName(pluName);
		goods.setEDishCode(pluNO);
		goods.setePoiId(epoiId);
		//goods.setIsSoldOut(0);
		goods.setMinOrderCount(1);
		if (picture != null && picture.isEmpty() == false)
		{
			goods.setPicture(picture);
		}
		if(isSequence)
		{
			goods.setSequence(sequence);
		}
		goods.setUnit(unit);
		goods.setSkus(new ArrayList<WMJBPGoodsUpdate.Skus>());
			
		for (DCP_OrderPlatformGoodsQueryRes.level2Spec oneData : goodsModel.getSpecs()) 
		{																
			String orderSpecNO = oneData.getOrderSpecID();
			String orderSpecName = oneData.getOrderSpecName();
			String price = oneData.getOrderPrice();
			String stockQty = oneData.getOrderStock();
			String packageFee = oneData.getOrderPackingFee();
			String onShelf = oneData.getOrderOnShelf();
			String specNO = oneData.getPluBarcode();
			String specName = oneData.getPluSpecName();
			int onShelf_i = 0;		//0-未售完，1-售完					
			if (onShelf != null && onShelf.equals("N"))
			{
				onShelf_i = 1;
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
			isOnShelf_PluNO = onShelf_i;
			WMJBPGoodsUpdate.Skus spec = new WMJBPGoodsUpdate.Skus();
			spec.setSkuId(specNO);
			spec.setSpec(specName);
			spec.setStock(stockQty_i);
			spec.setPrice(price_d);
			spec.setBoxNum(1);
			spec.setBoxPrice(0);
			
			goods.getSkus().add(spec);

    }
		goods.setIsSoldOut(isOnShelf_PluNO);
		goodsList.add(goods);
		
		StringBuilder errorMessage = new StringBuilder();
		boolean nRet = false;
		String trans_flg = "2";
			
		nRet = WMJBPProductService.batchUpload(eId, erpShopNO, goodsList, errorMessage);
		HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步商品】【更新】调用第三方接口！第三方返回:"+errorMessage.toString()+" 任务ID："
				+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+erpShopNO+" 商品分类categoryNO："+categoryNO+" 商品pluNO："+pluNO, jddjLogFileName);
		
		
		if(nRet)
		{
			trans_flg = "1";
		}
		//更新任务状态
	  // values
		Map<String, DataValue> values = new HashMap<String, DataValue>();
		DataValue v = new DataValue(trans_flg, Types.VARCHAR);
		values.put("TRANS_FLG", v);
		DataValue v1 = new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()) , Types.VARCHAR);
		values.put("UPDATE_TIME", v1);

		// condition
		Map<String, DataValue> conditions = new HashMap<String, DataValue>();
		DataValue c1 = new DataValue(eId, Types.VARCHAR);
		conditions.put("EID", c1);
		DataValue c2 = new DataValue(loadDocType, Types.VARCHAR);
		conditions.put("LOAD_DOCTYPE", c2);
		DataValue c3 = new DataValue(transType, Types.VARCHAR);
		conditions.put("TRANS_TYPE", c3);
		DataValue c4 = new DataValue(transID, Types.VARCHAR);
		conditions.put("TRANS_ID", c4);
		DataValue c5 = new DataValue(erpShopNO, Types.VARCHAR);
		conditions.put("SHOPID", c5);
		DataValue c6 = new DataValue(pluNO, Types.VARCHAR);
		conditions.put("PLUNO", c6);

		this.doUpdate("OC_transtask", values, conditions);
		
		HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步商品】【更新】任务完成！【更新任务表】完成!任务状态TRANS_FLG:"+trans_flg+" 任务ID："
				+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+erpShopNO+" 商品分类categoryNO："+categoryNO+" 商品pluNO："+pluNO, jddjLogFileName);
		
		
		UptBean ub1 = null;	
		ub1 = new UptBean("OC_mappinggoods");
		ub1.addUpdateValue("ORDER_PLUNAME", new DataValue(orderPluName,Types.VARCHAR));
		ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

		ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		ub1.addCondition("LOAD_DOCTYPE", new DataValue(loadDocType, Types.VARCHAR));
		ub1.addCondition("SHOPID", new DataValue(erpShopNO, Types.VARCHAR));
		ub1.addCondition("ORDER_PLUNO", new DataValue(orderPluNO, Types.VARCHAR));
									
		ArrayList<DataProcessBean> DPB = new ArrayList<DataProcessBean>();
		DPB.add(new DataProcessBean(ub1));	
		for (WMJBPGoodsUpdate.Skus nRetSpec : goods.getSkus()) 
		{
			String specNO = nRetSpec.getSkuId();
			String specName = nRetSpec.getSpec();
		  
			
			UptBean ub2 = null;	
			String isOn = "Y";
			if(goods.getIsSoldOut()==1)
			{
				isOn="N";
			}
			ub2 = new UptBean("OC_mappinggoods_spec");
			ub2.addUpdateValue("ORDER_SPECNAME", new DataValue(specName,Types.VARCHAR));
			ub2.addUpdateValue("PRICE", new DataValue(nRetSpec.getPrice(),Types.FLOAT));
			ub2.addUpdateValue("STOCKQTY", new DataValue(nRetSpec.getStock(),Types.FLOAT));
			ub2.addUpdateValue("PACKAGEFEE", new DataValue(nRetSpec.getPrice(),Types.FLOAT));
			ub2.addUpdateValue("ISONSHELF", new DataValue(isOn,Types.VARCHAR));
			ub2.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

			ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			ub2.addCondition("LOAD_DOCTYPE", new DataValue(loadDocType, Types.VARCHAR));
			ub2.addCondition("SHOPID", new DataValue(erpShopNO, Types.VARCHAR));
			ub2.addUpdateValue("ORDER_SPECNO", new DataValue(specNO,Types.VARCHAR));
			
			DPB.add(new DataProcessBean(ub2));
		
	  }
	
		
		try 
		{
			HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步商品】【更新】任务完成！【回写OC_mappinggoods与sepc规格表】开始!任务状态TRANS_FLG:"+trans_flg+" 任务ID："
					+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+erpShopNO+" 商品分类categoryNO："+categoryNO+" 商品pluNO："+pluNO, jddjLogFileName);
			
			this.doExecuteDataToDB(DPB);
			HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步商品】【更新】任务完成！【回写OC_mappinggoods与sepc规格表】开始!任务状态TRANS_FLG:"+trans_flg+" 任务ID："
					+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+erpShopNO+" 商品分类categoryNO："+categoryNO+" 商品pluNO："+pluNO, jddjLogFileName);
			
	  } 
		catch (Exception e) 
		{
			HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步商品】【更新】任务完成！【回写OC_mappinggoods与sepc规格表】异常："+e.getMessage()+"任务状态TRANS_FLG:"+trans_flg+" 任务ID："
					+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+erpShopNO+" 商品分类categoryNO："+categoryNO+" 商品pluNO："+pluNO, jddjLogFileName);
		
		
	  }
			
	}
	

	/**
	 * 商品删除
	 * @param transType
	 * @param transID
	 * @param eId
	 * @param erpShopNO
	 * @param loadDocType
	 * @param pluNO
	 * @param categoryNO
	 * @param goodsModel
	 * @throws Exception
	 */
	private void CallGoodsDeleteAPIFun(String transType,String transID,String eId,String erpShopNO,String loadDocType,String pluNO,String categoryNO,DCP_OrderPlatformGoodsQueryRes.level1Elm goodsModel) throws Exception
	{
		
		String orderPluNO = goodsModel.getOrderPluNO();
		String orderPluName = goodsModel.getOrderPluName();
		String orderCategoryNO = goodsModel.getOrderCategoryNO();
		String pluName = goodsModel.getPluName();
		
		StringBuilder errorMessage = new StringBuilder();
		boolean nRet = false;
		String trans_flg = "2";
		
		
		nRet = WMJBPProductService.deletePlu(eId, erpShopNO, orderPluNO, errorMessage);
		HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步商品】【删除】调用第三方接口！第三方返回:"+errorMessage.toString()+" 任务ID："
				+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+erpShopNO+" 商品分类categoryNO："+categoryNO+" 商品pluNO："+pluNO, jddjLogFileName);
		
		
		if(nRet)
		{
			trans_flg = "1";
		}
			
		//更新任务状态
	  // values
		Map<String, DataValue> values = new HashMap<String, DataValue>();
		DataValue v = new DataValue(trans_flg, Types.VARCHAR);
		values.put("TRANS_FLG", v);
		DataValue v1 = new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()) , Types.VARCHAR);
		values.put("UPDATE_TIME", v1);

		// condition
		Map<String, DataValue> conditions = new HashMap<String, DataValue>();
		DataValue c1 = new DataValue(eId, Types.VARCHAR);
		conditions.put("EID", c1);
		DataValue c2 = new DataValue(loadDocType, Types.VARCHAR);
		conditions.put("LOAD_DOCTYPE", c2);
		DataValue c3 = new DataValue(transType, Types.VARCHAR);
		conditions.put("TRANS_TYPE", c3);
		DataValue c4 = new DataValue(transID, Types.VARCHAR);
		conditions.put("TRANS_ID", c4);
		DataValue c5 = new DataValue(erpShopNO, Types.VARCHAR);
		conditions.put("SHOPID", c5);
		DataValue c6 = new DataValue(pluNO, Types.VARCHAR);
		conditions.put("PLUNO", c6);

		this.doUpdate("OC_transtask", values, conditions);
		HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步商品】【删除】任务完成！【更新任务表】完成!任务状态TRANS_FLG:"+trans_flg+" 任务ID："
				+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+erpShopNO+" 商品分类categoryNO："+categoryNO+" 商品pluNO："+pluNO, jddjLogFileName);

		if(nRet)
		{
			HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步商品】【删除】任务完成！【回写OC_mappinggoods与sepc规格表】开始!任务状态TRANS_FLG:"+trans_flg+" 任务ID："
					+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+erpShopNO+" 商品分类categoryNO："+categoryNO+" 商品pluNO："+pluNO, jddjLogFileName);
			
      Map<String, DataValue> values1 = new HashMap<String, DataValue>();
			
			values1.put("STATUS",  new DataValue("N", Types.VARCHAR));
			values1.put("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

			// condition
			Map<String, DataValue> conditions1 = new HashMap<String, DataValue>();
			conditions1.put("EID", new DataValue(eId, Types.VARCHAR));
			conditions1.put("LOAD_DOCTYPE", new DataValue(loadDocType, Types.VARCHAR));				
			conditions1.put("SHOPID", new DataValue(erpShopNO, Types.VARCHAR));					
			conditions1.put("PLUNO", new DataValue(pluNO, Types.VARCHAR));
			
			this.doUpdate("OC_mappinggoods_spec", values1, conditions1);
			HelpTools.writelog_fileName("【同步任务orderTranTasklog】【同步商品】【删除】任务完成！【回写OC_mappinggoods与sepc规格表】完成!任务状态TRANS_FLG:"+trans_flg+" 任务ID："
					+ transID + " 平台类型loadDocType：" + loadDocType + " 门店shopId:"+erpShopNO+" 商品分类categoryNO："+categoryNO+" 商品pluNO："+pluNO, jddjLogFileName);
			
		
		}
			
	}
	
	
}
