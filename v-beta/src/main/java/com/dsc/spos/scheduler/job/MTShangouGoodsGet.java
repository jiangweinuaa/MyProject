package com.dsc.spos.scheduler.job;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.ExecBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.WMSGProductService;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import java.sql.SQLSyntaxErrorException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 美团闪购平台商品获取
 * @author 86187
 *
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class MTShangouGoodsGet extends InitJob
{
	Logger logger = LogManager.getLogger(MTShangouGoodsGet.class.getName());

	static boolean bRun=false;//标记此服务是否正在执行中
	String goodsLogFileName = "MTShangouGoodsGet";
	String KeyName = "MTShangouGoodsGet";
	public MTShangouGoodsGet()
	{
		
	}
	public String doExe() throws Exception
	{
		String sReturnInfo = "";
		
		HelpTools.writelog_fileName("【同步任务MTShangouGoodsGet】同步START！",goodsLogFileName);
		//此服务是否正在执行中
		if (bRun)
		{		
			logger.info("\r\n*********同步任务MTShangouGoodsGet同步正在执行中,本次调用取消:************\r\n");
			HelpTools.writelog_fileName("【同步任务MTShangouGoodsGet】同步正在执行中,本次调用取消！",goodsLogFileName);
			return sReturnInfo;
		}
		bRun=true;//
		String loadDocType = orderLoadDocType.MTSG;
		try
		{
			boolean runTimeFlag = this.jobRunTimeFlag();
			if(!runTimeFlag)
			{
				sReturnInfo= "【同步任务MTShangouGoodsGet】不在job运行时间内！";
				HelpTools.writelog_fileName(sReturnInfo+"定时调用End",goodsLogFileName);
				return sReturnInfo;
			}
			String sql_shopList = "select a.* from dcp_mappingshop a inner join dcp_org B on a.eid=b.eid and a.shopid=b.organizationno  where a.businessid='2' and a.load_doctype='"+loadDocType+"' ";
			HelpTools.writelog_fileName("【同步任务MTShangouGoodsGet】查询美团映射门店sql:"+sql_shopList,goodsLogFileName);
			List<Map<String, Object>> shopList=this.doQueryData(sql_shopList, null);
			if(shopList!=null&&shopList.isEmpty()==false)
			{
				for (Map<String, Object> map : shopList)
				{
					try
					{
						String eId = map.getOrDefault("EID", "").toString();
						String shopId = map.getOrDefault("SHOPID", "").toString();
						String order_shopId= map.getOrDefault("ORDERSHOPNO", "").toString();
						String order_shopName= map.getOrDefault("ORDERSHOPNAME", "").toString();
						String isJbp = map.getOrDefault("ISJBP", "").toString();
						HelpTools.writelog_fileName("【同步商品资料到本地】循环获取该门店商品资料开始，企业EID="+eId+",平台类型LoadDocType="+loadDocType+",是否isJbp="+isJbp+",平台门店ID="+order_shopId+",门店shopId="+shopId, goodsLogFileName);
						if(isJbp.equals("Y"))
						{
							continue;
						}
						String syncDate = "";
						try
						{
							RedisPosPub redis = new RedisPosPub();
							String redis_key = KeyName+":"+eId;
							String hash_key = shopId;
						    syncDate = redis.getHashMap(redis_key, hash_key);
						} catch (Exception e)
						{
							// TODO: handle exception

						}
						HelpTools.writelog_fileName("【同步商品资料到本地】循环获取该门店商品资料开始,获取redis缓存中同步日期="+syncDate, goodsLogFileName);
					    String sdate = new SimpleDateFormat("yyyyMMdd").format(new Date());
					    /*if(syncDate==null||syncDate.isEmpty())
					    {
					    	HelpTools.writelog_fileName("【同步商品资料到本地】循环获取该门店商品资料开始,获取redis缓存中同步日期为空，暂不同步！", goodsLogFileName);
					    	continue;
					    }*/
						if(sdate.equals(syncDate))
						{
							HelpTools.writelog_fileName("【同步商品资料到本地】循环获取该门店商品资料开始,获取redis缓存中同步日期=当前系统日期"+sdate+",无须同步！", goodsLogFileName);
					    	continue;
						}
						JSONArray products = new JSONArray();
						StringBuilder errorMessage = new StringBuilder("");
						int pageCount = 0;
						int pageSize = 100;
						products = WMSGProductService.queryListByEPoiId(order_shopId,pageCount,pageSize,errorMessage);
						if(products == null ||products.length()==0)
						{
							HelpTools.writelog_fileName("【同步商品资料到本地】获取该门店商品资料完成！该门店没有商品资料！企业EID="+eId+",平台类型LoadDocType="+loadDocType+",平台门店ID="+order_shopId+",门店shopId="+shopId, goodsLogFileName);
							continue;
						}
						boolean isExist = false;//商品个数是否大于pageSize
						if(products.length()>=pageSize)
						{
							isExist = true;
						}
						//如果pageIndex=0 查询的结果大于pageSize，那么进行循环
						while(isExist)
						{
							try
							{
								pageCount +=pageSize;
								JSONArray oItems = WMSGProductService.queryListByEPoiId(order_shopId,pageCount,pageSize,errorMessage);
								if(oItems == null ||oItems.length()==0)
								{
									isExist = false;
									break;
								}
								for (int i=0;i<oItems.length();i++)
								{
									products.put(oItems.getJSONObject(i));
								}
								if (oItems.length() >= pageSize)
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


						List<level1Elm> goodsData = new ArrayList<level1Elm>();
						//HelpTools.writelog_fileName("【同步商品资料到本地】【MT查询门店菜品列表】获取该门店商品资料完成！ 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+order_shopId+" 门店shopId:"+shopId+" 获取的详细商品资料总个数："+products.size(), goodsLogFileName);	
						HelpTools.writelog_fileName("【同步商品资料到本地】【MT查询门店菜品列表】获取该门店商品资料完成！解析该门店商品资料并保存到本地开始！ 平台类型LoadDocType="+loadDocType+",平台门店ID="+order_shopId+",门店shopId="+shopId+",获取的商品资料总个数="+products.length(), goodsLogFileName);
						for (int i=0;i<products.length();i++)
						{
							JSONObject oItem = products.getJSONObject(i);
							try 
							{
								level1Elm oneLv1 = new level1Elm();
								oneLv1.setSpecs(new ArrayList<level2Spec>());
								oneLv1.setAttributes(new ArrayList<level2Attribute>());
								String orderPluNO = oItem.optString("app_spu_code");//String.valueOf(oItem.getFood_id());//菜品id
								String PluNO = oItem.optString("app_spu_code");;//APP方菜品id
								String orderPluName = oItem.optString("name");;//商品名
								String orderCategoryNO = oItem.optString("category_name");//菜品分类名
								String orderDescription = oItem.optString("description");
								String orderUnit =  oItem.optString("unit");
								String orderImageUrl =  oItem.optString("picture");
								String is_sold_out = oItem.optString("is_sold_out","1");//0-上架；1-下架
								String orderOnShelf = "Y";
								if ("1".equals(is_sold_out))
								{
									orderOnShelf = "N";
								}
								//美团没有返回商品在平台的ID，只返回了第三方的ID，如果没有通过接口或者工具去绑定，那么值为空,所以默认给商品名称
								if(orderPluNO==null||orderPluNO.trim().isEmpty())
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

								String skus = oItem.optString("skus");
								JSONArray oSpecs = new JSONArray(skus);
								if (oSpecs != null && oSpecs.length() > 0)
								{
									List<String> skuIdList = new ArrayList<>();
									for (int j=0;j<oSpecs.length();j++)
									{
										JSONObject oSpec = oSpecs.getJSONObject(j);
										level2Spec orderSpec = new level2Spec();
										String orderSpecID = oSpec.optString("sku_id");//规格Id
										if (skuIdList.contains(orderSpecID))
										{
											HelpTools.writelog_fileName("【同步商品资料到本地】【存在重复sku_id】平台商品名称="+orderPluName+",平台类型LoadDocType="+loadDocType+",平台门店ID="+order_shopId+",门店shopId="+shopId, goodsLogFileName);
											continue;
										}
										String orderSpecpluBarcode = oSpec.optString("sku_id");//商品扩展码 (ERP的商品条码)真正映射的商品条码
										String orderSpecName = oSpec.optString("spec");
										String orderPrice = oSpec.optString("price");
										String orderStock = oSpec.optString("stock","");//商品sku的库存量，字段信息如返回为空，则表示目前此商品在门店内库存为不限
										if (orderStock.isEmpty())
										{
											orderStock = "9999";
										}
										
									 //美团没有返回商品在平台的SKUID，只返回了第三方的SKUID，如果没有通过接口或者工具去绑定，那么值为空,所以默认给规格名称
										/*if(orderSpecID==null||orderSpecID.trim().isEmpty())
										{
											orderSpecID = orderSpecName;
										}

										//这个平台没有控制，可以设置重复。。
										if (skuIdList.contains(orderSpecID))
										{
											orderSpecID +="-"+orderSpecName;
										}
										else
										{
											skuIdList.add(orderSpecID);
										}*/
										
										float boxPrice = 0;
										float boxNum = 0;
										try {
											boxPrice = Float.parseFloat(oSpec.optString("box_price","0"));
										} catch (Exception e) {
											boxPrice = 0;
										}
										try {
											boxNum = Float.parseFloat(oSpec.optString("box_num","0"));
										} catch (Exception e) {
											boxNum = 0;
										}
										
										float orderPackingFee = boxPrice * boxNum;
										
										
										float  netWeight = 0;//商品sku的重量，单位是克/g。

										try {
											netWeight = Float.parseFloat(oSpec.optString("weight","0"));
										} catch (Exception e) {
											netWeight = 0;
										}
				
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
										skuIdList.add(orderSpecID);
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
							
								goodsData.add(oneLv1);		

							} 
							catch (Exception e) 
							{		    	
								continue;		
							}
						
						}
						
						
						
						//region 开始保存数据库
						this.SaveOnlineGoods(eId, loadDocType, shopId, order_shopId,order_shopName,goodsData );
						//endregion
						
					} 
					catch (Exception e)
					{
						// TODO: handle exception
						HelpTools.writelog_fileName("【同步商品资料到本地】循环获取该门店商品资料开始，异常："+e.getMessage(), goodsLogFileName);
						continue;
					}
														
				}
				
			}
			else
			{
				HelpTools.writelog_fileName("【同步任务MTShangouGoodsGet】查询美团映射门店资料为空！",goodsLogFileName);
			}
			
			bRun=false;
		} 
		catch (Exception e)
		{
			HelpTools.writelog_fileName("【同步任务MTShangouGoodsGet】异常：" + e.toString(),goodsLogFileName);
			bRun=false;
		}
		finally 
		{
			bRun=false;//
		}
		return sReturnInfo;
	}
	
	
	private void SaveOnlineGoods(String eId,String loadDocType,String shopId,String order_shopId,String orderShopName,List<level1Elm> goods) throws Exception
	{				
		if(eId==null||eId.isEmpty())
		{
			return;
		}
		if(loadDocType==null||loadDocType.isEmpty())
		{
			return;
		}
		if(shopId==null||shopId.isEmpty())
		{
			return;
		}
		if(order_shopId==null||order_shopId.isEmpty())
		{
			return;
		}
		if(goods==null||goods.isEmpty())
		{
			return;
		}
		HelpTools.writelog_fileName("【同步商品资料到本地】开始组装SQL语句！ 平台类型LoadDocType="+loadDocType+",平台门店ID="+order_shopId+",门店shopId="+shopId+",获取的商品资料总个数="+goods.size(), goodsLogFileName);
		List<DataProcessBean> DPB = new ArrayList<DataProcessBean>();
		
		//三张表先删后插
		String execsql1 = "delete from DCP_MAPPINGGOODS where EID='"+eId+"' and LOAD_DOCTYPE='"+loadDocType+"' and ORDER_SHOP='"+order_shopId+"'";
		String execsql2 = "delete from DCP_MAPPINGGOODS_SPEC where EID='"+eId+"' and LOAD_DOCTYPE='"+loadDocType+"' and ORDER_SHOP='"+order_shopId+"'";
		String execsql3 = "delete from DCP_MAPPINGGOODS_ATTR where EID='"+eId+"' and LOAD_DOCTYPE='"+loadDocType+"' and ORDER_SHOP='"+order_shopId+"'";
		ExecBean exc1 = new ExecBean(execsql1);
		ExecBean exc2 = new ExecBean(execsql2);
		ExecBean exc3 = new ExecBean(execsql3);
		DPB.add(new DataProcessBean(exc1));
		DPB.add(new DataProcessBean(exc2));
		DPB.add(new DataProcessBean(exc3));
		
		
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

				if (orderImageUrl.length()>255)
				{
					orderImageUrl = orderImageUrl.substring(0,255);
				}
				if (orderDescription.length()>255)
				{
					orderDescription = orderDescription.substring(0,255);
				}

				List<level2Spec> specs = oneData1.getSpecs();
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

					/*for (Map<String,Object> map : getQData )
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
					}*/

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
							new DataValue(shopId, Types.VARCHAR),//组织编号=门店编号
							new DataValue(shopId, Types.VARCHAR),//ERP门店				
							new DataValue(pluNO, Types.VARCHAR),//商品编码
							new DataValue(pluBarcode, Types.VARCHAR),//ERP条码
							new DataValue(pluSpecName, Types.VARCHAR),//规格名称
							new DataValue(order_shopId, Types.VARCHAR),//外卖平台门店ID
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

					InsBean ib2 = new InsBean("DCP_MAPPINGGOODS_SPEC", columns2);
					ib2.addValues(insValue2);
					DPB.add(new DataProcessBean(ib2));	
					//this.doExecuteDataToDB();
			 /* HelpTools.writelog_fileName("【同步商品资料到本地】开始组装SQL语句！ COMPANYNO=" + companyNo + " LOAD_DOCTYPE：" + loadDocType
				+ " SHOP=" + erpShopNO + " ORGANIZATIONNO=" + erpShopNO + " PLUNO=" + pluNO + " SPECNO=" + pluBarcode
				+ " SPECNAME=" + pluSpecName + " ORDER_SHOP=" + orderShopNO + " ORDER_SHOPNAME=" + orderShopName
				+ " ORDER_PLUNO=" + orderPluNO + " ORDER_SPECNO=" + orderSpecID+" ORDER_SPECNAME="+orderSpecName, "GoodsKey");
        */
				}


				List<level2Attribute> attributes = oneData1.getAttributes();
				if (attributes != null && attributes.size() > 0)
				{
					HelpTools.writelog_fileName("【同步商品资料到本地(有属性值)】开始组装SQL语句！ 平台类型LoadDocType="+loadDocType+",平台门店ID="+order_shopId+",门店shopId="+shopId+",获取的商品资料总个数="+goods.size(), goodsLogFileName);
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
								new DataValue(shopId, Types.VARCHAR),//组织编号=门店编号
								new DataValue(shopId, Types.VARCHAR),//ERP门店				
								new DataValue(pluNO, Types.VARCHAR),//商品编码
								new DataValue("", Types.VARCHAR),//属性名称
								new DataValue("", Types.VARCHAR),//属性值
								new DataValue(order_shopId, Types.VARCHAR),//外卖平台门店ID
								new DataValue(orderShopName, Types.VARCHAR),//外卖平台门店名称
								new DataValue(orderPluNO, Types.VARCHAR),//外卖平台商品ID
								new DataValue(attriName, Types.VARCHAR),//外卖平台属性名称
								new DataValue(attriValue, Types.VARCHAR),//外卖平台属性值
								new DataValue("100", Types.VARCHAR)						
						};

						InsBean ib2 = new InsBean("DCP_MAPPINGGOODS_ATTR", columns2);
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
						new DataValue(shopId, Types.VARCHAR),//组织编号=门店编号
						new DataValue(shopId, Types.VARCHAR),//ERP门店				
						new DataValue(pluNO, Types.VARCHAR),//ERP商品编码
						new DataValue(pluName, Types.VARCHAR),//ERP商品名称
						new DataValue(categoryNO, Types.VARCHAR),//ERP商品分类编码
						new DataValue(order_shopId, Types.VARCHAR),//外卖平台门店ID
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

				InsBean ib1 = new InsBean("DCP_MAPPINGGOODS", columns1);
				ib1.addValues(insValue1);
				DPB.add(new DataProcessBean(ib1));
			} 

			catch (Exception e) 
			{
				HelpTools.writelog_fileName("【同步商品资料到本地】开始组装SQL语句！有异常:"+e.getMessage()+",平台类型LoadDocType="+loadDocType+",平台门店ID="+order_shopId+",门店shopId="+shopId+",异常的商品平台ID/名称="+orderPluNO+"/"+orderPlunName, goodsLogFileName);
				continue;		
			}


		}

		try 
		{
			HelpTools.writelog_fileName("【同步商品资料到本地】开始执行SQL语句！ 平台类型LoadDocType="+loadDocType+",平台门店ID="+order_shopId+",门店shopId="+shopId+",获取的商品资料总个数="+goods.size(), goodsLogFileName);  
			this.doExecuteDataToDB(DPB);
			HelpTools.writelog_fileName("【同步商品资料到本地】开始执行SQL语句！成功！ 平台类型LoadDocType="+loadDocType+",平台门店ID="+order_shopId+",门店shopId="+shopId+",获取的商品资料总个数="+goods.size(), goodsLogFileName);  
            String sdate = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String redis_key = KeyName + ":" + eId;							 
			String hash_key = shopId;
			this.SaveOrderRedis(redis_key, hash_key, sdate);
			
		} 
		catch (SQLSyntaxErrorException e)
		{
			HelpTools.writelog_fileName("【同步商品资料到本地】开始执行SQL语句！异常："+e.getMessage()+",平台类型LoadDocType:"+loadDocType+",平台门店ID："+order_shopId+",门店shopId:"+shopId+",获取的商品资料总个数："+goods.size(), goodsLogFileName); 		
		}
		catch (Exception e) 
		{
			HelpTools.writelog_fileName("【同步商品资料到本地】开始执行SQL语句！异常："+e.getMessage()+",平台类型LoadDocType:"+loadDocType+",平台门店ID："+order_shopId+",门店shopId:"+shopId+",获取的商品资料总个数："+goods.size(), goodsLogFileName); 		
		}
		

	}

	/**
	 * job运行时间，（如果没有设置，默认早上6点到晚上23点）
	 * @return
	 * @throws Exception
	 */
	private boolean jobRunTimeFlag() throws Exception
	{
		boolean flag = true;
		String sdate = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
		String stime = new SimpleDateFormat("HHmmss").format(Calendar.getInstance().getTime());

		// 先查 job 执行时间，然后再执行后续操作
		String getTimeSql = "select * from job_quartz_detail where job_name = 'MTSGAutoUpdateStock'  and cnfflg = 'Y' ";
		List<Map<String, Object>> getTimeDatas = this.doQueryData(getTimeSql, null);
		if (getTimeDatas != null && !getTimeDatas.isEmpty())
		{
			boolean isTime = false;
			for (Map<String, Object> map : getTimeDatas)
			{
				String beginTime = map.get("BEGIN_TIME").toString();
				String endTime = map.get("END_TIME").toString();

				// 如果当前时间在 执行时间范围内， 就执行
				if (stime.compareTo(beginTime) >= 0 && stime.compareTo(endTime) < 0)
				{
					isTime = true;
					break;
				}
			}
			if (!isTime)
			{
				return false;
			}

		}
		else// 如果没设置执行时间， 默认凌晨1点到晚上7点执行
		{

			if (stime.compareTo("010000") >= 0 && stime.compareTo("070000") < 0)
			{

			}
			else
			{
				HelpTools.writelog_fileName("【同步任务MTShangouGoodsGet】当前时间["+stime+"];不在job默认执行时间内，默认执行时间[01:00:00]-[07:00:00]",goodsLogFileName);
				flag = false;
			}

		}
		return flag;
	}
	
	protected void doExecuteDataToDB(List<DataProcessBean> pData) throws Exception {
		if (pData == null || pData.size() == 0) {
			return;
		}
		StaticInfo.dao.useTransactionProcessData(pData);
	}
	
	private void SaveOrderRedis(String redis_key,String hash_key,String hash_value) throws Exception
	{
		//region 先写缓存
		try 
		{	
			HelpTools.writelog_fileName("【同步商品资料到本地】【开始写缓存】"+"redis_key:"+redis_key+",hash_key:"+hash_key+",hash_value:"+hash_value,goodsLogFileName);	  	
			RedisPosPub redis = new RedisPosPub();
			boolean isexistHashkey = redis.IsExistHashKey(redis_key, hash_key);
			if (isexistHashkey) {

				redis.DeleteHkey(redis_key, hash_key);//
				HelpTools.writelog_fileName("【同步商品资料到本地】【删除存在hash_key的缓存】成功！" + ",redis_key:" + redis_key + ",hash_key:" + hash_key,goodsLogFileName);
			}
			boolean nret = redis.setHashMap(redis_key, hash_key, hash_value);
			if (nret) {
				HelpTools.writelog_fileName("【同步商品资料到本地】【开始写缓存】OK" + ",redis_key:" + redis_key + ",hash_key:" + hash_key,goodsLogFileName);
			} else {
				HelpTools.writelog_fileName("【同步商品资料到本地】【开始写缓存】Error" + ",redis_key:" + redis_key + ",hash_key:" + hash_key,goodsLogFileName);
			}
			
		} 
		catch (Exception e) 
		{
			HelpTools.writelog_fileName("【同步商品资料到本地】【开始写缓存】Exception:"+e.getMessage()+",redis_key:"+redis_key+",hash_key:"+hash_key,goodsLogFileName);	
		}
		//endregion

	}
	
	public class level1Elm
	{
		private String orderPluNO;
		private String orderPluName;
		private String orderCategoryNO;
		private String orderCategoryName;
		private String categoryNO;
		private String categoryName;
		private String pluNO;
		private String pluName;
		private String orderDescription;//商品描述
		private String orderImageUrl;//商品主图片
		private String orderUnit;//商品单位 
		private String orderPriority;//优先级
		private String resultMapping;//是否映射成功 Y/N
		private String resultMappingDescription;//映射结果描述
		private String materialID1;
		private String materialID2;
		private String materialID3;
		private String materialID4;
		private String materialID5;
		private String materialID6;
		private String materialID7;
		private String materialID8;
		private String materialID9;
		private String materialID10;

		private String material1;
		private String material2;
		private String material3;
		private String material4;
		private String material5;
		private String material6;
		private String material7;
		private String material8;
		private String material9;
		private String material10;	
		private String isAllTimeSell ;
		private String beginDate ;
		private String endDate ;
		private String sellWeek ;
		private String sellTime ;
		
		private String companyNO;//job用到
		private String loadDocType;//job用到
		private String shopNO;//job用到
		private String orderShopNO;//平台门店ID
		private String spuOnShelf;//是否上架 job用到
		
		private List<level2Spec> specs;
		private List<level2Attribute> attributes;

		public String getOrderPluNO() {
			return orderPluNO;
		}
		public void setOrderPluNO(String orderPluNO) {
			this.orderPluNO = orderPluNO;
		}
		public String getOrderPluName() {
			return orderPluName;
		}
		public void setOrderPluName(String orderPluName) {
			this.orderPluName = orderPluName;
		}			
		public String getPluNO() {
			return pluNO;
		}
		public void setPluNO(String pluNO) {
			this.pluNO = pluNO;
		}		
		public String getPluName() {
			return pluName;
		}
		public void setPluName(String pluName) {
			this.pluName = pluName;
		}
		public String getOrderCategoryNO() {
			return orderCategoryNO;
		}
		public void setOrderCategoryNO(String orderCategoryNO) {
			this.orderCategoryNO = orderCategoryNO;
		}
		public String getOrderCategoryName() {
			return orderCategoryName;
		}
		public void setOrderCategoryName(String orderCategoryName) {
			this.orderCategoryName = orderCategoryName;
		}
		public String getIsAllTimeSell() {
			return isAllTimeSell;
		}
		public void setIsAllTimeSell(String isAllTimeSell) {
			this.isAllTimeSell = isAllTimeSell;
		}
		public String getBeginDate() {
			return beginDate;
		}
		public void setBeginDate(String beginDate) {
			this.beginDate = beginDate;
		}
		public String getEndDate() {
			return endDate;
		}
		public void setEndDate(String endDate) {
			this.endDate = endDate;
		}
		public String getSellWeek() {
			return sellWeek;
		}
		public void setSellWeek(String sellWeek) {
			this.sellWeek = sellWeek;
		}
		public String getSellTime() {
			return sellTime;
		}
		public void setSellTime(String sellTime) {
			this.sellTime = sellTime;
		}
		public String getCategoryNO() {
			return categoryNO;
		}
		public void setCategoryNO(String categoryNO) {
			this.categoryNO = categoryNO;
		}
		public String getCategoryName() {
			return categoryName;
		}
		public void setCategoryName(String categoryName) {
			this.categoryName = categoryName;
		}
		public String getOrderDescription() {
			return orderDescription;
		}
		public void setOrderDescription(String orderDescription) {
			this.orderDescription = orderDescription;
		}
		public String getOrderImageUrl() {
			return orderImageUrl;
		}
		public void setOrderImageUrl(String orderImageUrl) {
			this.orderImageUrl = orderImageUrl;
		}
		public String getOrderUnit() {
			return orderUnit;
		}
		public void setOrderUnit(String orderUnit) {
			this.orderUnit = orderUnit;
		}
		public List<level2Spec> getSpecs() {
			return specs;
		}
		public void setSpecs(List<level2Spec> specs) {
			this.specs = specs;
		}
		public List<level2Attribute> getAttributes() {
			return attributes;
		}
		public void setAttributes(List<level2Attribute> attributes) {
			this.attributes = attributes;
		}
		public String getOrderPriority() {
			return orderPriority;
		}
		public void setOrderPriority(String orderPriority) {
			this.orderPriority = orderPriority;
		}
		public String getResultMapping() {
			return resultMapping;
		}
		public void setResultMapping(String resultMapping) {
			this.resultMapping = resultMapping;
		}
		public String getResultMappingDescription() {
			return resultMappingDescription;
		}
		public void setResultMappingDescription(String resultMappingDescription) {
			this.resultMappingDescription = resultMappingDescription;
		}
		public String getMaterialID1() {
			return materialID1;
		}
		public void setMaterialID1(String materialID1) {
			this.materialID1 = materialID1;
		}
		public String getMaterialID2() {
			return materialID2;
		}
		public void setMaterialID2(String materialID2) {
			this.materialID2 = materialID2;
		}
		public String getMaterialID3() {
			return materialID3;
		}
		public void setMaterialID3(String materialID3) {
			this.materialID3 = materialID3;
		}
		public String getMaterialID4() {
			return materialID4;
		}
		public void setMaterialID4(String materialID4) {
			this.materialID4 = materialID4;
		}
		public String getMaterialID5() {
			return materialID5;
		}
		public void setMaterialID5(String materialID5) {
			this.materialID5 = materialID5;
		}
		public String getMaterialID6() {
			return materialID6;
		}
		public void setMaterialID6(String materialID6) {
			this.materialID6 = materialID6;
		}
		public String getMaterialID7() {
			return materialID7;
		}
		public void setMaterialID7(String materialID7) {
			this.materialID7 = materialID7;
		}
		public String getMaterialID8() {
			return materialID8;
		}
		public void setMaterialID8(String materialID8) {
			this.materialID8 = materialID8;
		}
		public String getMaterialID9() {
			return materialID9;
		}
		public void setMaterialID9(String materialID9) {
			this.materialID9 = materialID9;
		}
		public String getMaterialID10() {
			return materialID10;
		}
		public void setMaterialID10(String materialID10) {
			this.materialID10 = materialID10;
		}
		public String getMaterial1() {
			return material1;
		}
		public void setMaterial1(String material1) {
			this.material1 = material1;
		}
		public String getMaterial2() {
			return material2;
		}
		public void setMaterial2(String material2) {
			this.material2 = material2;
		}
		public String getMaterial3() {
			return material3;
		}
		public void setMaterial3(String material3) {
			this.material3 = material3;
		}
		public String getMaterial4() {
			return material4;
		}
		public void setMaterial4(String material4) {
			this.material4 = material4;
		}
		public String getMaterial5() {
			return material5;
		}
		public void setMaterial5(String material5) {
			this.material5 = material5;
		}
		public String getMaterial6() {
			return material6;
		}
		public void setMaterial6(String material6) {
			this.material6 = material6;
		}
		public String getMaterial7() {
			return material7;
		}
		public void setMaterial7(String material7) {
			this.material7 = material7;
		}
		public String getMaterial8() {
			return material8;
		}
		public void setMaterial8(String material8) {
			this.material8 = material8;
		}
		public String getMaterial9() {
			return material9;
		}
		public void setMaterial9(String material9) {
			this.material9 = material9;
		}
		public String getMaterial10() {
			return material10;
		}
		public void setMaterial10(String material10) {
			this.material10 = material10;
		}
	public String getLoadDocType() {
		return loadDocType;
	}
	public void setLoadDocType(String loadDocType) {
		this.loadDocType = loadDocType;
	}
	public String getShopNO() {
		return shopNO;
	}
	public void setShopNO(String shopNO) {
		this.shopNO = shopNO;
	}
	public String getOrderShopNO() {
		return orderShopNO;
	}
	public void setOrderShopNO(String orderShopNO) {
		this.orderShopNO = orderShopNO;
	}
	public String getCompanyNO() {
		return companyNO;
	}
	public void setCompanyNO(String companyNO) {
		this.companyNO = companyNO;
	}
	public String getSpuOnShelf() {
		return spuOnShelf;
	}
	public void setSpuOnShelf(String spuOnShelf) {
		this.spuOnShelf = spuOnShelf;
	}
	

	}

	public class level2Spec
	{
		private String orderSpecID;
		private String orderSpecName;
		private String orderPrice;//商品价格		
		private String orderStock;//库存量
		private String ordermaxStock;//库存量
		private String orderPackingFee;//包装费
		private String orderOnShelf;//是否上架
		private String pluBarcode;
		private String pluSpecName;
		private double netWeight;

		public String getOrderSpecID() {
			return orderSpecID;
		}
		public void setOrderSpecID(String orderSpecID) {
			this.orderSpecID = orderSpecID;
		}
		public String getOrderSpecName() {
			return orderSpecName;
		}
		public void setOrderSpecName(String orderSpecName) {
			this.orderSpecName = orderSpecName;
		}
		public String getOrderPrice() {
			return orderPrice;
		}
		public void setOrderPrice(String orderPrice) {
			this.orderPrice = orderPrice;
		}
		public String getOrderStock() {
			return orderStock;
		}
		public void setOrderStock(String orderStock) {
			this.orderStock = orderStock;
		}
		public String getOrderPackingFee() {
			return orderPackingFee;
		}
		public void setOrderPackingFee(String orderPackingFee) {
			this.orderPackingFee = orderPackingFee;
		}
		public String getOrderOnShelf() {
			return orderOnShelf;
		}
		public void setOrderOnShelf(String orderOnShelf) {
			this.orderOnShelf = orderOnShelf;
		}
		public String getPluBarcode() {
			return pluBarcode;
		}
		public void setPluBarcode(String pluBarcode) {
			this.pluBarcode = pluBarcode;
		}
		public String getPluSpecName() {
			return pluSpecName;
		}
		public void setPluSpecName(String pluSpecName) {
			this.pluSpecName = pluSpecName;
		}
		public double getNetWeight() {
			return netWeight;
		}
		public void setNetWeight(double netWeight) {
			this.netWeight = netWeight;
		}
		public String getOrdermaxStock() {
			return ordermaxStock;
		}
		public void setOrdermaxStock(String ordermaxStock) {
			this.ordermaxStock = ordermaxStock;
		}



	}

	public class level2Attribute
	{
		private String name;
		private List<String> details;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public List<String> getDetails() {
			return details;
		}
	
		public void setDetails(List<String> details) {
			this.details = details;
		}


	}
}
