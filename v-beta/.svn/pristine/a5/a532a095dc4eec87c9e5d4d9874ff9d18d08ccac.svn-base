package com.dsc.spos.scheduler.job;

import java.net.URLEncoder;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import com.dsc.spos.dao.UptBean;

import com.dsc.spos.json.cust.req.DCP_OrderStatusLogCreateReq;

import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.utils.OrderUtil;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.GaoDeGeoModel;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.JDUtil;
import com.google.gson.reflect.TypeToken;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class JDOrderGet extends InitJob 
{
	//查询舞像的订单
	Logger logger = LogManager.getLogger(JDOrderGet.class.getName());
	static boolean bRun=false;//标记此服务是否正在执行中
	String jddjLogFileName = "JDOrderGet";
	
	public String doExe() throws Exception
	{
		String sReturnInfo = "";
		//官网物流推送单
		HelpTools.writelog_fileName("【同步任务JDOrderGet】同步START！",jddjLogFileName);
		//此服务是否正在执行中
		if (bRun)
		{		
			logger.info("\r\n*********同步任务JDOrderGet同步正在执行中,本次调用取消:************\r\n");
			HelpTools.writelog_fileName("【同步任务JDOrderGet】同步正在执行中,本次调用取消！",jddjLogFileName);
			return sReturnInfo;
		}
		bRun=true;//	
		//从缓存里面查询一下时间戳
		try
		{
			//这里开始查找舞像的定义
			String sql="select * from OC_ECOMMERCE where ECPLATFORMNO='10' ";
			List<Map<String, Object>> lisdate=this.doQueryData(sql, null);
			if(lisdate!=null&&!lisdate.isEmpty())
			{
				for (Map<String, Object> ECOMmap : lisdate) 
				{
					String PARTNER_ID=ECOMmap.get("PARTNER_ID").toString();
					String OTHER_NO=ECOMmap.get("OTHER_NO").toString();
					String eId=ECOMmap.get("EID").toString();

					String API_URL=ECOMmap.get("API_URL").toString();
					String API_KEY=ECOMmap.get("API_KEY").toString();
					String API_SECRET=ECOMmap.get("API_SECRET").toString();
					String TOKEN=ECOMmap.get("TOKEN").toString();

					RedisPosPub redis = new RedisPosPub();
					String redis_key = "WMORDER" + "_" + "99" + "_" + "JD";
					Map<String, String> ordermap = redis.getALLHashMap(redis_key);
					redis.Close();
					//String store_code="wdmbj000";
					String store_code=OTHER_NO;

					//这里减少2秒防止漏单
					Calendar cl= Calendar.getInstance();
					cl.add(Calendar.SECOND, -60);

					String enddate=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cl.getTime());

					String sdate=new SimpleDateFormat("yyyy-MM-dd").format(cl.getTime());
					sdate+=" 00:00:00";
					String saledate=sdate;
					String returndate=sdate;

					if(ordermap!=null)
					{
						for (Map.Entry<String, String> entry : ordermap.entrySet())
						{
							if(entry.getKey().equals("OrderSale_"+store_code))
							{
								saledate=entry.getValue();
							}
							if(entry.getKey().equals("OrderReturn_"+store_code))
							{
								returndate=entry.getValue();
							}
						}
					}

					try
					{
						//开始组织京东到家发送的
						int i=1;
						int page_size=20;

						boolean issussce=true;


						while(true)
						{
							try
							{	
								JSONObject js=new JSONObject();
								js.put("page", i);
								js.put("pageSize", page_size);
								js.put("beginDate", saledate);
								js.put("endDate", enddate);
								//发送的内容
								HelpTools.writelog_fileName("【同步任务发送：" +"salesList "+ js.toString(),jddjLogFileName);
								//使用京东商城的API调用接口
								String res= JDUtil.SendJD("GET", js.toString(), API_URL,"jingdong.dropship.dps.searchoutboundorder",TOKEN,API_KEY,API_SECRET);
								HelpTools.writelog_fileName("【同步任务返回：" +"salesList"+ res,jddjLogFileName);
								ParseJson pj=new ParseJson();

								JDOder oslist=pj.jsonToBean(res,  new TypeToken<JDOder>(){});

								if(oslist.getJingdong_dropship_dps_searchoutboundorder_responce().getCode().equals("0"))
								{
									//调用成功，循环orders
									if(oslist.getJingdong_dropship_dps_searchoutboundorder_responce().getSearchResult()==null)
									{
										break;
									}
									for (ResultDtoList map : oslist.getJingdong_dropship_dps_searchoutboundorder_responce().getSearchResult().getResultDtoList()) 
									{
										//开始处理逻辑，需要循环取订单，然后插入数据库
										String orderno=map.getCustomOrderId();
										//这里需要判断一下数据库里面有没有该单据，如果有可以直接continue掉
										String sqlorder="select * from OC_order where orderno='"+orderno+"'  and eId='"+eId+"' and LOAD_DOCTYPE='10'  ";
										List<Map<String, Object>> listsqlorder=this.doQueryData(sqlorder, null);
										if(listsqlorder!=null&&!listsqlorder.isEmpty())
										{
											HelpTools.writelog_fileName("【同步任务返回：" + orderno+"订单已存在！" ,jddjLogFileName);
											continue;
										}

										String res_json = GetWuXiangOrderResponse(map,eId,PARTNER_ID);
										if(res_json==null||res_json.isEmpty())
										{
											HelpTools.writelog_fileName("【同步任务返回：" + orderno+"解析失败" ,jddjLogFileName);
											continue;
										}
										Map<String, Object> resobj = new HashMap<String, Object>();
										if( this.processDUID(res_json, resobj)==false )
										{
											//插入单据失败需要重新拉单
											//调用失败
											HelpTools.writelog_fileName("【同步任务任返回：插入单据失败，需要重新拉单!" ,jddjLogFileName);
											//								  			bRun=false;
											//												return sReturnInfo;
											issussce=false;
											break;
										}
									}
									if(Integer.parseInt(oslist.getJingdong_dropship_dps_searchoutboundorder_responce().getSearchResult().getRecordCount())<20)
									{	
										break;
									}
									i++;

								}
								else
								{
									//调用失败
									HelpTools.writelog_fileName("【同步任务任返回：" + oslist.getJingdong_dropship_dps_searchoutboundorder_responce().getSearchResult().getErrorMessage(),jddjLogFileName);
									//					  			bRun=false;
									//									return sReturnInfo;
									issussce=false;
									break;
								}

								pj=null;

							}
							catch(Exception ex)
							{
								HelpTools.writelog_fileName("【同步失败：" + ex.toString(),jddjLogFileName);
								//								bRun=false;
								//								return sReturnInfo;
								issussce=false;
								break;
							}
						}
						//这里更新一下redis缓存	
						//String hash_key = orderid + "&" + orderStatus;
						if(issussce==false)
						{
							HelpTools.writelog_fileName("【同步失败跳出一个循环：" + store_code,jddjLogFileName);
							break;
						}

						String hash_key = "OrderSale_"+store_code;
						HelpTools.writelog_waimai("【开始写缓存】"+"redis_key:"+redis_key+" hash_key:"+hash_key+" hash_value:"+enddate);
						try 
						{

							redis = new RedisPosPub();
							boolean isexistHashkey = redis.IsExistHashKey(redis_key, hash_key);
							if(isexistHashkey)
							{

								redis.DeleteHkey(redis_key, hash_key);//
								HelpTools.writelog_waimai("【删除存在hash_key的缓存】成功！"+" redis_key:"+redis_key+" hash_key:"+hash_key);
							}
							boolean nret = redis.setHashMap(redis_key, hash_key, enddate);
							if(nret)
							{
								HelpTools.writelog_waimai("【写缓存】OK"+" redis_key:"+redis_key+" hash_key:"+hash_key);
							}
							else
							{
								HelpTools.writelog_waimai("【写缓存】Error"+" redis_key:"+redis_key+" hash_key:"+hash_key);
							}
							redis.Close();

						} 
						catch (Exception e) 
						{
							HelpTools.writelog_waimai("【写缓存】Exception:"+e.getMessage());	
						}
						//查询舞像的退货接口
						i=1;
						//开始处理京东的退单
						while(true)
						{
							try
							{
								JSONObject js=new JSONObject();
								js.put("page", i);
								js.put("pageSize", page_size);
								js.put("beginDate", saledate);
								js.put("endDate", enddate);
								//发送的内容
								HelpTools.writelog_fileName("【同步任务发送："+"salesReturnList" + js.toString(),jddjLogFileName);
								String res= JDUtil.SendJD("GET", js.toString(), API_URL,"jingdong.dropship.dps.searchpre",TOKEN,API_KEY,API_SECRET);
								HelpTools.writelog_fileName("【同步任务返回："+"salesReturnList" + res,jddjLogFileName);
								ParseJson pj=new ParseJson();

								JDOder oslist=pj.jsonToBean(res,  new TypeToken<JDOder>(){});

								if(oslist.getJingdong_dropship_dps_searchpre_responce().getCode().equals("0"))
								{
									//调用成功，循环orders
									if(oslist.getJingdong_dropship_dps_searchpre_responce().getSearchPreResult()==null)
									{
										break;
									}

									List<DataProcessBean> data = new ArrayList<DataProcessBean>();
									for (ResultDtoList map : oslist.getJingdong_dropship_dps_searchpre_responce().getSearchPreResult().getResultDtoList()) 
									{
										//开始处理逻辑，需要循环取订单，然后插入数据库
										String order=map.getCustomOrderId();
										String id=map.getId();
										String roReason=map.getRoReason();

										//这里需要更新门店的缓存，以及数据库
										UptBean up=new UptBean("OC_ORDER");
										up.addCondition("LOAD_DOCTYPE", new DataValue("10", Types.VARCHAR));
										up.addCondition("ORDERNO", new DataValue(order, Types.VARCHAR));

										//up.addUpdateValue("STATUS", new DataValue("12", Types.VARCHAR));
										up.addUpdateValue("REFUNDSTATUS", new DataValue("2", Types.VARCHAR));
										up.addUpdateValue("RETURNSN", new DataValue(id, Types.VARCHAR));
										up.addUpdateValue("REFUNDREASON", new DataValue(roReason, Types.VARCHAR));

										data.add(new DataProcessBean(up));

										StaticInfo.dao.useTransactionProcessData(data);

										try
										{/*
											DCP_OrderStatusLogCreateReq req_log = new DCP_OrderStatusLogCreateReq();
											req_log.setDatas(new ArrayList<DCP_OrderStatusLogCreateReq.level1Elm>());

											DCP_OrderStatusLogCreateReq req =  new DCP_OrderStatusLogCreateReq();
											//region订单状态
											DCP_OrderStatusLogCreateReq.level1Elm onelv1 = req.new level1Elm();

											onelv1.setCallback_status("0");
											onelv1.setLoadDocType("10");
											onelv1.setNeed_callback("Y");
											onelv1.setNeed_notify("N");
											onelv1.setoEId("99");
											String opNO = "京东";
											String o_opName = "京东";
											onelv1.setO_opName(o_opName);
											onelv1.setO_opNO(opNO);
											//舞像没推门店
											String oShopId = " ";
											onelv1.setO_organizationNO(oShopId);
											onelv1.setoShopId(oShopId);
											onelv1.setOrderNO(order);
											String statusType = "3";//退单状态
											String updateStaus = "2";//已退单

											onelv1.setStatusType(statusType);				 					
											onelv1.setStatus(updateStaus);
											StringBuilder statusTypeNameObj = new StringBuilder();
											String statusName = HelpTools.GetOrderStatusName(statusType, updateStaus,statusTypeNameObj);				 			 
											String statusTypeName = statusTypeNameObj.toString();
											onelv1.setStatusTypeName(statusTypeName);
											onelv1.setStatusName(statusName);

											String memo = "";
											memo += statusTypeName+"-->" + statusName;
											onelv1.setMemo(memo);

											String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
											onelv1.setUpdate_time(updateDatetime);


											req_log.getDatas().add(onelv1);
											onelv1=null;


											String req_log_json ="";
											try
											{							
												req_log_json = pj.beanToJson(req_log);
											}
											catch(Exception e)
											{

											}			   			   			  	
											StringBuilder errorMessage = new StringBuilder();
											boolean nRet = HelpTools.InsertOrderStatusLog(StaticInfo.dao, req_log_json, errorMessage);
											if(nRet)
											{		  		 
												HelpTools.writelog_waimai("【写表OC_orderStatuslog保存成功】"+" 订单号orderNO:"+order);
											}
											else
											{			  		 
												HelpTools.writelog_waimai("【写表OC_orderStatuslog异常】"+errorMessage.toString()+" 订单号orderNO:"+ order);
											}
										*/}
										catch (Exception  e)
										{

										}

									}


									if(Integer.parseInt(oslist.getJingdong_dropship_dps_searchpre_responce().getSearchPreResult().getRecordCount())<20)
									{	
										break;
									}
									i++;
								}
								else
								{
									//调用失败
									HelpTools.writelog_fileName("【同步任务任返回：" + oslist.getJingdong_dropship_dps_searchoutboundorder_responce().getSearchResult().getErrorMessage() ,jddjLogFileName);
									bRun=false;
									return sReturnInfo;
								}

								pj=null;
							}
							catch(Exception ex)
							{
								HelpTools.writelog_fileName("【同步失败：" + ex.toString(),jddjLogFileName);
								bRun=false;
								return sReturnInfo;
							}
						}
						//这里更新一下redis缓存	
						//String hash_key = orderid + "&" + orderStatus;
						hash_key = "OrderReturn_"+store_code;
						HelpTools.writelog_waimai("【开始写缓存】"+"redis_key:"+redis_key+" hash_key:"+hash_key+" hash_value:"+enddate);
						try 
						{

							redis = new RedisPosPub();
							boolean isexistHashkey = redis.IsExistHashKey(redis_key, hash_key);
							if(isexistHashkey)
							{

								redis.DeleteHkey(redis_key, hash_key);//
								HelpTools.writelog_waimai("【删除存在hash_key的缓存】成功！"+" redis_key:"+redis_key+" hash_key:"+hash_key);
							}
							boolean nret = redis.setHashMap(redis_key, hash_key, enddate);
							if(nret)
							{
								HelpTools.writelog_waimai("【写缓存】OK"+" redis_key:"+redis_key+" hash_key:"+hash_key);
							}
							else
							{
								HelpTools.writelog_waimai("【写缓存】Error"+" redis_key:"+redis_key+" hash_key:"+hash_key);
							}
							redis.Close();

						} 
						catch (Exception e) 
						{
							HelpTools.writelog_waimai("【写缓存】Exception:"+e.getMessage());	
						}


					}
					catch(Exception ex)
					{
						// TODO: handle exception
						HelpTools.writelog_fileName("【同步失败：" + ex.toString(),jddjLogFileName);
						bRun=false;
					}

				}
			}

			bRun=false;
		}
		catch(Exception ex)
		{
			HelpTools.writelog_fileName("redis报错：" + ex.toString(),jddjLogFileName);
			bRun=false;
		}
		return sReturnInfo;
	}



	//舞像订单主动查询接口
	public  String GetWuXiangOrderResponse(ResultDtoList osdetail,String eId,String belfirm) throws Exception
	{

		//Resultdetail curreginfo=osdetail.getResult();

		//解析收到的舞像请求 
		try 
		{
			JSONObject jsonobjresponse = new JSONObject();
			String shopId =" ";//主键不能为空，所以默认空格
			String shopname = "";

			jsonobjresponse.put("eId", eId);
			jsonobjresponse.put("customerNO", "");						

			jsonobjresponse.put("status", "1");

			jsonobjresponse.put("loadDocType", "10");//1.饿了么 2.美团外卖 3.微商城							
			jsonobjresponse.put("isShipcompany", "N");//总部配送 Y/N						
			jsonobjresponse.put("sn", "0");
			jsonobjresponse.put("isInvoice", "N");//是否开发票
			jsonobjresponse.put("invoiceType", "");
			jsonobjresponse.put("invoiceTitle", "");
			jsonobjresponse.put("taxRegnumber", "");
			jsonobjresponse.put("packageFee", "0");//餐盒费				
			jsonobjresponse.put("serviceCharge", "0");//服务费
			jsonobjresponse.put("belfirm", belfirm);//所属公司

			jsonobjresponse.put("totDisc", "0");//订单优惠总额
			jsonobjresponse.put("sellerDisc", "0");//商户优惠总额
			jsonobjresponse.put("platformDisc", "0");//平台优惠总额
			jsonobjresponse.put("isBook", "Y");//外卖预定单

			//JSONObject datasobj = jsonobj.getJSONObject("datas");
			String orderNo = osdetail.getCustomOrderId();
			jsonobjresponse.put("orderNO", orderNo);
			//String address = datasobj.get("address").toString();//
			String address=osdetail.getAddress();

			//String contactName = datasobj.get("contactName").toString();//联系人

			jsonobjresponse.put("contMan", osdetail.getConsigneeName());
			//String contactTelephone = datasobj.get("contactTelephone").toString();//
			jsonobjresponse.put("contTel", osdetail.getPhone());

			jsonobjresponse.put("getMan", osdetail.getConsigneeName());
			//String contactTelephone = datasobj.get("contactTelephone").toString();//
			jsonobjresponse.put("getMantel", osdetail.getPhone());

			//先给死成配送
			String deliverType = "2";//微商城取货方式 1=自提 2=配送	

			//对门店做处理
			String wuxiangshop=" ";
			String shopwuxiangname="";

			//这里加一段查询商品
			String stemp="";
			for (OrderDetailList goods : osdetail.getOrderDetailList()) 
			{
				stemp+="  B.PluBarcode='"+goods.getUpc() + "' or  ";
			}
			stemp+= " 1=2 ";
			String sgoodString="select A.* from DCP_GOODS A left join DCP_BARCODE B on A.eId=B.eId and A.pluno=B.pluno"
					+ " where A.eId='"+eId+"' and ("+stemp+") ";
			List<Map<String, Object>> lgoodsList=this.doQueryData(sgoodString, null);
			String isPROTAGNO="";
			String isDELTAGNO="";
			if(lgoodsList!=null&&!lgoodsList.isEmpty())
			{
				for (Map<String, Object> map : lgoodsList) 
				{
					String PROTAGNO=map.get("PROTAGNO").toString();
					String DELTAGNO=map.get("DELTAGNO").toString();
					if(PROTAGNO.equals("1"))
					{
						isPROTAGNO=PROTAGNO;
					}
					if(DELTAGNO.equals("1"))
					{
						isDELTAGNO=PROTAGNO;
					}
				}
			}
			if(isPROTAGNO.equals("1"))
			{
				jsonobjresponse.put("orderType", "1");
			}

			jsonobjresponse.put("PSHOPREMIND", "");
			if(isDELTAGNO.equals("1"))
			{
				jsonobjresponse.put("PSHOPREMIND", "2");
			}


			String status="";

			{
				deliverType="2";
				jsonobjresponse.put("status", "0");
				status="0";
				try
				{
					//这里省 市 区 使用逗号隔开的
					jsonobjresponse.put("PROVINCE", osdetail.getProvinceName());
					jsonobjresponse.put("oCITY", osdetail.getCityName());
					jsonobjresponse.put("COUNTY", osdetail.getCountyName());
					String addresstemp=address;
					jsonobjresponse.put("address", addresstemp);
					//这里可以调用一下地图获取一下经纬度
					String adress=URLEncoder.encode(addresstemp, "utf-8" );

					String url="https://restapi.amap.com/v3/geocode/geo?address="+adress+"&output=JSON&key=575f128b5da35246777d2c5c24f9b68b";
					String responseStr= OrderUtil.Sendcom("",url,"GET");
					//解析返回的内容
					ParseJson pj = new ParseJson();

					GaoDeGeoModel curreginfomap=pj.jsonToBean(responseStr, new TypeToken<GaoDeGeoModel>(){});
					if(curreginfomap.getStatus().equals("1"))
					{
						//默认是第一条位置的经纬度
						String jinwei=curreginfomap.getGeocodes().get(0).getLocation();
						String[] listjinwei=jinwei.split(",");

						//				req.setLONGITUDE(listjinwei[0]);
						//				req.setLATITUDE(listjinwei[1]);
						//存一下省市区等
						jsonobjresponse.put("LONGITUDE", listjinwei[0]);
						jsonobjresponse.put("LATITUDE", listjinwei[1]);
						//直接在这里加一段智能匹配门店的过程

					Map<String, Object> mshoptemp= matchshop(jsonobjresponse, listjinwei[0], listjinwei[1]);
						if(mshoptemp!=null&&!mshoptemp.isEmpty())
						{
							wuxiangshop=mshoptemp.get("ORGANIZATIONNO").toString();
							shopwuxiangname=mshoptemp.get("ORG_NAME").toString();
						}

					}

					pj=null;
				}
				catch(Exception ex)
				{
					//地图获取失败
				}
			}

			jsonobjresponse.put("shipType", deliverType);//配送方式 1.外卖平台配送 2.配送 3.顾客自提
			String cdate= osdetail.getOrderCreateDate(); //下单时间 2018-08-29 16:57:13
			String orderDateTime="";
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = null; 
			try 
			{ 
				Calendar cl=Calendar.getInstance();
				cl.setTimeInMillis(Long.parseLong(cdate));
				orderDateTime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(cl.getTime());
			} 
			catch (Exception e) 
			{ 

			} 
			jsonobjresponse.put("createDatetime", orderDateTime);//下单时间，格式yyyyMMddHHmmssSSS

			Calendar cal = Calendar.getInstance();//获得当前时间
			SimpleDateFormat df=new SimpleDateFormat("yyyyMMdd");
			String createDate = df.format(cal.getTime());	

			df = new SimpleDateFormat("HHmmss");
			String createTime = df.format(cal.getTime());
			jsonobjresponse.put("sDate", createDate);
			jsonobjresponse.put("sTime", createTime);

			try 
			{
				shopId = wuxiangshop;//这个节点可能不返回				
			} 
			catch (Exception e) 
			{
				shopId =" ";	
			}
			try 
			{
				shopname = "";//这个节点可能不返回	
			} 
			catch (Exception e) 
			{
				shopname ="";		
			}
			//主键不能为空
			if (shopId == null || shopId.isEmpty() || shopId.length() == 0)
			{
				shopId =" ";
			}

			//查询门店名称
			String sqlshop="select * from DCP_ORG_lang where organizationNO='"+wuxiangshop+"'  and eId='"+eId+"' and lang_type='zh_CN' ";
			List<Map<String, Object>> listsql=this.doQueryData(sqlshop, null);
			if(listsql!=null &&!listsql.isEmpty())
			{
				shopwuxiangname=listsql.get(0).get("ORG_NAME").toString();
			}

			//这里可以启用推荐门店
			jsonobjresponse.put("shopId", wuxiangshop);//下单门店
			jsonobjresponse.put("organizationNO", wuxiangshop);
			jsonobjresponse.put("shopName", shopwuxiangname);
			jsonobjresponse.put("shippingShopNO", wuxiangshop);//配送门店=下单门店
			jsonobjresponse.put("shippingShopName", shopwuxiangname);
			jsonobjresponse.put("machShopNO", wuxiangshop);//生产门店，先默认 后面去指定
			jsonobjresponse.put("machShopName", shopwuxiangname);


			if(osdetail.getExpectedDeliveryTime()!=null&&!osdetail.getExpectedDeliveryTime().isEmpty())
			{
				String wuxiangdate=osdetail.getExpectedDeliveryTime();
				Calendar cl=Calendar.getInstance();
				cl.setTimeInMillis(Long.parseLong(wuxiangdate));
				wuxiangdate = new SimpleDateFormat("yyyyMMdd HHmmss").format(cl.getTime());

				String needDate = wuxiangdate.split(" ")[0];//微商城格式 2018-08-08
				jsonobjresponse.put("shipDate", needDate);//配送日期格式yyyyMMdd 20180808

				jsonobjresponse.put("shipTime", wuxiangdate.split(" ")[1]);//配送时间 HHmmss 170000 
			}

			String message =osdetail.getOrderRemark();//买家留言
			//			try 
			//			{
			//				message = curreginfo.getDelivery_desc();//买家留言
			//		  } 
			//			catch (Exception e) 
			//			{
			//				message = "";		
			//		  }
			jsonobjresponse.put("memo", message);//单头备注
			//String totalAmount = datasobj.get("totalAmount").toString();//微商城 订单总应付金额

			jsonobjresponse.put("tot_oldAmt", osdetail.getPay());//订单原价
			jsonobjresponse.put("tot_Amt", osdetail.getPay());//订单金额
			jsonobjresponse.put("incomeAmt", osdetail.getPay());//商家实收金额

			//折扣金额
			jsonobjresponse.put("totDisc", 0);//商家实收金额
			jsonobjresponse.put("sellerDisc",0);//商户优惠总额

			jsonobjresponse.put("manualNO",orderNo);//手工单号赋值订单号

			//String payedAmount = datasobj.get("payedAmount").toString();//微商城 已付金额
			jsonobjresponse.put("payAmt", osdetail.getPay() );//用户已支付金额

			//String deliverAmount = datasobj.get("deliverAmount").toString();//微商城 运费
			jsonobjresponse.put("shipFee", 0);//配送费
			//String goodsAmount = datasobj.get("goodsAmount").toString();//微商城 商品金额		

			//jsonobjresponse.put("status", "0");//订单状态 默认成已接单

			String payStatus = "3";//1.未支付 2.部分支付 3.付清

			jsonobjresponse.put("payStatus", payStatus);//支付状态 1.未支付 2.部分支付 3.付清
			jsonobjresponse.put("refundStatus", "1");////退单状态 1.未申请 2.用户申请退单 3.拒绝退单 4.客服仲裁中 5.退单失败 6.退单成功
			jsonobjresponse.put("shopShareDeliveryFee", "0");//商家替用户承担的配送费
			jsonobjresponse.put("partRefundAmt", "0");//部分退单 的退款金额
			//解析goods
			//JSONArray goodsarray = datasobj.getJSONArray("goods");

			String sno="";
			JSONArray array = new JSONArray();
			double totamt=0;
			double totincome=0;
			for(int i=0; i< osdetail.getOrderDetailList().size();i++)
			{				
				try 
				{
					OrderDetailList oDetail= osdetail.getOrderDetailList().get(i);
					JSONObject goodsobj = new JSONObject();

					//JSONObject job = goodsarray.getJSONObject(i);
					double price = 0;
					double quantity = 0;
					String price_str = "0";
					String quantity_str = "0";
					try 
					{
						price =Double.parseDouble(oDetail.getJdPrice());
					} 
					catch (Exception e) 
					{

					}
					try 
					{
						quantity_str = oDetail.getCommodityNum()+"";
						quantity = Double.parseDouble(quantity_str);

					} 
					catch (Exception e) 
					{

					}
					double disc=Double.parseDouble(oDetail.getDiscount());
					double income=Double.parseDouble(oDetail.getCost());
					totincome+=income*quantity;

					double amt = price*quantity;

					double goodsamt=amt-disc;
					totamt+=goodsamt;

					String pluno=oDetail.getUpc();
					goodsobj.put("item", i+1);
					goodsobj.put("pluNO", pluno);
					goodsobj.put("pluBarcode", pluno);
					//goodsobj.put("pluName", curreginfo.getGoods().get(i).goods_name);
					//处理下把规格放名称里面处理
					goodsobj.put("pluName", oDetail.getCommodityName() );

					goodsobj.put("specName", "" );
					goodsobj.put("attrName", "");
					goodsobj.put("unit", "");
					goodsobj.put("price", price);
					goodsobj.put("qty", quantity_str);
					goodsobj.put("goodsGroup", "");
					goodsobj.put("disc", disc);
					goodsobj.put("boxNum", "0");
					goodsobj.put("boxPrice", "0");
					goodsobj.put("amt", goodsamt);

					goodsobj.put("isMemo", "N");

					String isMemo ="N";
					//					JSONArray messagesarray = job.getJSONArray("messages");
					//					if(messagesarray!=null &&messagesarray.length()>0)
					//					{
					//						isMemo = "Y";
					//						goodsobj.put("messages", messagesarray);
					//					}
					//					goodsobj.put("isMemo", isMemo);

					array.put(goodsobj);	

				} 
				catch (Exception e) 
				{
					HelpTools.writelog_waimaiException("解析舞像goods节点失败："+e.getMessage());
					continue;			
				}		
			}
			jsonobjresponse.put("goods", array);
			//			//这里处理一下蛋糕还是西点订单
			//			if(sno.startsWith("2"))
			//			{
			//				//蛋糕订单,需要调度
			//				jsonobjresponse.put("orderType", "1");
			//			}
			//			else
			//			{
			//				jsonobjresponse.put("orderType", "2");
			//			}

			jsonobjresponse.put("tot_oldAmt", totincome+"");//订单原价
			jsonobjresponse.put("tot_Amt", totamt+"");//订单金额
			jsonobjresponse.put("incomeAmt", totamt+"");//商家实收金额
			jsonobjresponse.put("payAmt", totamt+"" );//用户已支付金额

			//订单添加一条付款方式
			JSONObject payobj = new JSONObject();
			payobj.put("item", 1);
			payobj.put("payCode", "1004");
			payobj.put("payCodeerp", "1004");
			payobj.put("pay", totamt+"");
			payobj.put("isOrderpay", "Y");
			payobj.put("payName", "京东商城");

			//			if(curreginfo.getPay_type()==0)
			//			{
			//			 payobj.put("payName", "优惠支付");
			//			}
			//			if(curreginfo.getPay_type()==1)
			//			{
			//			 payobj.put("payName", "支付宝");
			//			}
			//			if(curreginfo.getPay_type()==2)
			//			{
			//			 payobj.put("payName", "微信");
			//			}
			//			if(curreginfo.getPay_type()==3)
			//			{
			//			 payobj.put("payName", "翼支付");
			//			}
			//			if(curreginfo.getPay_type()==4)
			//			{
			//			 payobj.put("payName", "货到付款,");
			//			}
			//			if(curreginfo.getPay_type()==5)
			//			{
			//			 payobj.put("payName", "余额支付");
			//			}

			payobj.put("cardNO", "");
			payobj.put("ctType", "");
			payobj.put("paySernum", "");
			payobj.put("serialNO", "");
			payobj.put("refNO", "");
			payobj.put("teriminalNO", "");
			payobj.put("descore", 0);
			payobj.put("extra", 0);
			payobj.put("changed", 0);
			payobj.put("bdate", "");
			payobj.put("isOrderpay", "Y");

			JSONArray paylist=new JSONArray();
			paylist.put(payobj);
			jsonobjresponse.put("pay", paylist);

			String  Response_json = jsonobjresponse.toString();
			//Response_json = Response_json.replace("\"[{", "[{").replace("}]\"", "}]").replace("\"{", "{").replace("}\"", "}");


			String redis_key = "WMORDER" + "_" + eId + "_" + shopId;
			String hash_key = orderNo;
			if(status.equals("1"))
			{
				HelpTools.writelog_waimai("【开始写缓存】"+"redis_key:"+redis_key+" hash_key:"+hash_key+" hash_value:"+Response_json);
				try 
				{

					RedisPosPub redis = new RedisPosPub();
					boolean isexistHashkey = redis.IsExistHashKey(redis_key, hash_key);
					if(isexistHashkey)
					{

						redis.DeleteHkey(redis_key, hash_key);//
						HelpTools.writelog_waimai("【删除存在hash_key的缓存】成功！"+" redis_key:"+redis_key+" hash_key:"+hash_key);
					}
					boolean nret = redis.setHashMap(redis_key, hash_key, Response_json);
					if(nret)
					{
						HelpTools.writelog_waimai("【写缓存】OK"+" redis_key:"+redis_key+" hash_key:"+hash_key);
					}
					else
					{
						HelpTools.writelog_waimai("【写缓存】Error"+" redis_key:"+redis_key+" hash_key:"+hash_key);
					}
					redis.Close();

				} 
				catch (Exception e) 
				{
					HelpTools.writelog_waimai("【写缓存】Exception:"+e.getMessage());	
				}
			}

			return Response_json;						
		} 
		catch (Exception e) 
		{
			HelpTools.writelog_waimaiException("舞像发送的请求格式有误！"+e.getMessage());
			return null;
		}														 		 		  		 				
	}

	Map<String, Object> matchshop(JSONObject req,String lon,String lat) throws Exception
	{
		//是否大蛋糕
		try
		{
			boolean isda=false;

			if(req.getString("PSHOPREMIND")!=null&&req.getString("PSHOPREMIND").equals("2"))
			{
				isda=true;
			}

			String sda="";
			String scur="";
			String jzpp="  and C.SHOPID is not null  ";

			boolean isshopmach=false;
			if(isda==true)
			{
				//大蛋糕只有2个门店有
				sda=" and (A.OrganizationNo='BJZ0037' or A.OrganizationNo='BJZ0027') ";
			}
			//本门店的订单放在最上匹配
			else
			{
				if(req.getString("loadDocType").equals("4")||req.getString("loadDocType").equals("9"))
				{
					isshopmach=true;

					scur="select * from (  select a.*,b.org_name ,F_CRM_GetDistance("+lat+","+lon +",a.LATITUDE,a.LONGITUDE) DISTANCE from DCP_ORG a "	
							+ " left join DCP_ORG_Lang B on A.eId=B.eId and A.OrganizationNo=B.OrganizationNo and B.Lang_Type='zh_CN' "
							+ " where a.eId='"+ req.getString("eId") +"' and a.BELFIRM='"+ req.getString("belfirm") +"' and A.OrganizationNo='"+req.getString("belfirm") +"'  )"
							+ " union all  ";
				}
			}

			//这里可以加一个参数  1 不开启智能匹配  2开启距离匹配  3 开启范围匹配  4 开启范围集中点匹配
			String isOpenMatch=PosPub.getPARA_SMS(StaticInfo.dao, req.getString("eId"), "", "isOpenMatch");

			if(isOpenMatch==null||isOpenMatch.isEmpty())
			{
				return null;
			}
			else
			{
				if(isOpenMatch.equals("1"))
				{
					return null;
				}
				if(isOpenMatch.equals("2")||isOpenMatch.equals("3"))
				{
					sda="";
					scur="";
					jzpp=" ";
				}
			}

			//String dsql="select * from (  select * from (  select a.*,b.org_name ,F_CRM_GetDistance("+lon+","+lat +",a.LONGITUDE,a.LATITUDE) DISTANCE from DCP_ORG a "
			String dsql="select * from (  select * from (  select a.*,b.org_name ,F_CRM_GetDistance("+lat+","+lon +",a.LATITUDE,a.LONGITUDE) DISTANCE from DCP_ORG a "
					+ " left join DCP_ORG_Lang B on A.eId=B.eId and A.OrganizationNo=B.OrganizationNo and B.Lang_Type='zh_CN' "
					+ " left join DCP_SHOP_ORDERSet C on A.eId=C.eId and A.OrganizationNo=C.SHOPID and C.OrdersetType='3' and C.OrdersetValue='1'  "
					+ "  where a.eId='"+ req.getString("eId") +"' and a.status='100' and a.BELFIRM='"+ req.getString("belfirm") +"'  " +jzpp +sda+"  )"
					+ " order by DISTANCE,ORGANIZATIONNO ) ";
			dsql= scur+dsql;
			List<Map<String, Object>> dsqldate=this.doQueryData(dsql, null);
			if(dsqldate==null||dsqldate.isEmpty())
			{
				return null;
			}
			//开始匹配，选取10个距离比较近的门店做匹配
			int i=0;
			for (Map<String, Object> map : dsqldate) 
			{
				if(isOpenMatch.equals("2"))
				{
					return map;
				}
				if(isda==true)
				{
					return map;
				}

				try
				{
					String RANGE_TYPE=map.get("RANGE_TYPE").toString();
					String RANGE=map.get("RANGE").toString();
					String DISTANCE=map.get("DISTANCE").toString();
					if(RANGE_TYPE.isEmpty()||RANGE.isEmpty()||DISTANCE.isEmpty())
					{
						continue;
					}
					if(RANGE_TYPE.equals("0"))
					{
						//圆形匹配,到门店的距离
						double A1=Double.parseDouble(DISTANCE);
						//门店的配送半径
						double A2=Double.parseDouble(RANGE);
						if(A2>=A1)
						{
							HelpTools.writelog_fileName("订单:"+req.getString("orderNO")+"职能调度匹配门店1:"+map.get("ORGANIZATIONNO").toString() , "MAPCALL");
							return map;
						}
					}
					else
					{
						//开始使用地图匹配，组下经纬度
						double px=Double.parseDouble(lon);
						double py=Double.parseDouble(lat);
						List<Double> listlon=new ArrayList<Double>();
						List<Double> listlat=new ArrayList<Double>();
						String[] listjw=RANGE.split("\\|");
						for (String onejw : listjw) 
						{
							String[] listonejw=onejw.split(",");
							listlon.add(Double.parseDouble(listonejw[0]));
							listlat.add(Double.parseDouble(listonejw[1]));
						}
						if(OrderUtil.isinside1(px, py, listlon, listlat) )
						{
							HelpTools.writelog_fileName("订单:"+req.getString("orderNO")+"职能调度匹配门店2:"+map.get("ORGANIZATIONNO").toString() , "MAPCALL");
							return map;
						}
					}
				}
				catch(Exception ex)
				{}
				//匹配10次
				i++;
				if(i==10)
				{
					break;
				}
			}

			if(isshopmach==true&&dsqldate.size()>=2&&isOpenMatch.equals("4"))
			{
				HelpTools.writelog_fileName("订单:"+req.getString("orderNO")+"职能调度匹配门店3:"+dsqldate.get(1).get("ORGANIZATIONNO").toString() , "MAPCALL");
				return dsqldate.get(1);
			}
			HelpTools.writelog_fileName("订单:"+req.getString("orderNO")+"职能调度匹配门店4:"+dsqldate.get(0).get("ORGANIZATIONNO").toString() , "MAPCALL");
			return dsqldate.get(0);

		}
		catch(Exception ex)
		{
			//匹配报错
		}
		//没有最后就用距离匹配
		return null;
	}


	//订单保存不成功需要怎么处理，否则会漏单
	protected boolean processDUID(String req, Map<String, Object> res) throws Exception {
		// TODO Auto-generated method stub
		try 
		{
			//直接使用实体类
			JSONObject obj = new JSONObject(req);
			String orderstatus = obj.get("status").toString();//我们自己的订单状态 微商城默认已接单 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单
			String eId = obj.get("eId").toString();
			String shopId = obj.optString("shopId").toString();
			String orderNO = obj.get("orderNO").toString();
			if(orderstatus !=null)
			{
				if(orderstatus.equals("1")||orderstatus.equals("0"))//订单新建
				{
					List<DataProcessBean> data = new ArrayList<DataProcessBean>();

					ArrayList<DataProcessBean> DPB = null;//HelpTools.GetInsertOrder(obj);
					if (DPB != null && DPB.size() > 0)
					{
						for (DataProcessBean dataProcessBean : DPB) 
						{
							data.add(dataProcessBean);
							//this.addProcessData(dataProcessBean);			
						}					
						//this.doExecuteDataToDB();
						StaticInfo.dao.useTransactionProcessData(data);
					}

					//写订单日志
					/*
					DCP_OrderStatusLogCreateReq req_log = new DCP_OrderStatusLogCreateReq();
					req_log.setDatas(new ArrayList<DCP_OrderStatusLogCreateReq.level1Elm>());

					DCP_OrderStatusLogCreateReq DCP_OrderStatusLogCreateReq = new DCP_OrderStatusLogCreateReq();
					DCP_OrderStatusLogCreateReq.level1Elm onelv1 = DCP_OrderStatusLogCreateReq.new level1Elm();


					onelv1.setCallback_status("0");
					onelv1.setLoadDocType("7");

					onelv1.setNeed_callback("N");
					if(orderstatus.equals("1"))
					{
						onelv1.setNeed_notify("Y");
					}
					else
					{
						onelv1.setNeed_notify("N");
					}

					onelv1.setoEId(eId);
					String o_opName = "未知用户";
					o_opName="舞像";

					onelv1.setO_opName(o_opName);
					onelv1.setO_opNO("");
					String oShopId = shopId;
					onelv1.setO_organizationNO(oShopId);
					onelv1.setoShopId(oShopId);
					onelv1.setOrderNO(orderNO);
					String statusType = "1";
					onelv1.setStatusType(statusType);				 					
					onelv1.setStatus(orderstatus);
					StringBuilder statusTypeNameObj = new StringBuilder();
					String statusName = HelpTools.GetOrderStatusName(statusType, orderstatus,statusTypeNameObj);				 			 
					String statusTypeName = statusTypeNameObj.toString();
					onelv1.setStatusTypeName(statusTypeName);
					onelv1.setStatusName(statusName);

					String memo = "收到新订单：";
					memo += statusTypeName+"-->" + statusName;
					onelv1.setMemo(memo);

					String createDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
					onelv1.setUpdate_time(createDatetime);

					req_log.getDatas().add(onelv1);
					onelv1=null;

					ParseJson pj = new ParseJson();	  	
					String req_log_json ="";

					try
					{
						req_log_json = pj.beanToJson(req_log);
					}
					catch(Exception e)
					{

					}

					StringBuilder errorMessage = new StringBuilder();
					boolean nRet = HelpTools.InsertOrderStatusLog(StaticInfo.dao, req_log_json, errorMessage);
					if(nRet)
					{		  		 
						HelpTools.writelog_waimai("【写表OC_orderStatuslog保存成功】"+" 订单号orderNO:"+orderNO);
					}
					else
					{			  		 
						HelpTools.writelog_waimai("【写表OC_orderStatuslog异常】"+errorMessage.toString()+" 订单号orderNO:"+orderNO);
					}

					HelpTools.writelog_waimai("【保存数据库成功】"+ " 企业编号eId=" + eId + " 门店编号shopId=" + shopId + " 订单号orderNO=" + orderNO);

					pj=null;*/
				}			
				else//更新 数据库状态 微商城目前 其他状态不会推送过来
				{

				}

			}


		} 
		catch (SQLException e) 
		{
			HelpTools.writelog_waimai("【执行语句】异常："+e.getMessage() + "\r\n req请求内容:" + req);	
			return false;
		}
		catch (Exception e) 
		{
			HelpTools.writelog_waimai("【执行语句】异常："+e.getMessage() + "\r\n req请求内容:" + req);		
			return false;
		}
		return true;

	}



	public class JDOder {

		private Jingdong_dropship_dps_searchoutboundorder_response jingdong_dropship_dps_searchoutboundorder_responce;
		private Jingdong_dropship_dps_searchoutboundorder_response jingdong_dropship_dps_searchpre_responce;

		public Jingdong_dropship_dps_searchoutboundorder_response getJingdong_dropship_dps_searchoutboundorder_responce() {
			return jingdong_dropship_dps_searchoutboundorder_responce;
		}
		public void setJingdong_dropship_dps_searchoutboundorder_responce(
				Jingdong_dropship_dps_searchoutboundorder_response jingdong_dropship_dps_searchoutboundorder_responce) {
			this.jingdong_dropship_dps_searchoutboundorder_responce = jingdong_dropship_dps_searchoutboundorder_responce;
		}
		public Jingdong_dropship_dps_searchoutboundorder_response getJingdong_dropship_dps_searchpre_responce() {
			return jingdong_dropship_dps_searchpre_responce;
		}
		public void setJingdong_dropship_dps_searchpre_responce(Jingdong_dropship_dps_searchoutboundorder_response jingdong_dropship_dps_searchpre_responce) {
			this.jingdong_dropship_dps_searchpre_responce = jingdong_dropship_dps_searchpre_responce;
		}


	}


	public class OrderDetailList {

		private String commodityNum;
		private String skuId;
		private String jdPrice;
		private String commodityName;
		private String cost;
		private String upc;
		private String discount;
		public void setCommodityNum(String commodityNum) {
			this.commodityNum = commodityNum;
		}
		public String getCommodityNum() {
			return commodityNum;
		}

		public void setSkuId(String skuId) {
			this.skuId = skuId;
		}
		public String getSkuId() {
			return skuId;
		}

		public void setJdPrice(String jdPrice) {
			this.jdPrice = jdPrice;
		}
		public String getJdPrice() {
			return jdPrice;
		}

		public void setCommodityName(String commodityName) {
			this.commodityName = commodityName;
		}
		public String getCommodityName() {
			return commodityName;
		}

		public void setCost(String cost) {
			this.cost = cost;
		}
		public String getCost() {
			return cost;
		}

		public void setUpc(String upc) {
			this.upc = upc;
		}
		public String getUpc() {
			return upc;
		}

		public void setDiscount(String discount) {
			this.discount = discount;
		}
		public String getDiscount() {
			return discount;
		}

	}


	public class ResultDtoList {

		private String id;
		private String roReason;

		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		private String phone;
		private String townName;
		private String cityName;
		private String orderCreateDate;
		private String provinceName;
		private String paymentCategoryDispName;
		private String orderRemark;
		private String sendPay;
		private String countyName;
		private String parentOrderId;
		private String isNotice;
		private String pay;
		private String vendorStoreId;
		private String createDate;
		private String consigneeName;
		private String orderSource;
		private String paymentCategory;
		private String expectedDeliveryTime;
		private String orderTime;
		private String postcode;
		private String customOrderId;
		private String refundSourceFlag;
		private String vendorStoreName;
		private String address;
		private String email;
		private String pin;
		private List<OrderDetailList> orderDetailList;
		private String memoByVendor;
		private String telephone;
		private String operatorState;
		public void setPhone(String phone) {
			this.phone = phone;
		}
		public String getPhone() {
			return phone;
		}

		public void setTownName(String townName) {
			this.townName = townName;
		}
		public String getTownName() {
			return townName;
		}

		public void setCityName(String cityName) {
			this.cityName = cityName;
		}
		public String getCityName() {
			return cityName;
		}

		public void setOrderCreateDate(String orderCreateDate) {
			this.orderCreateDate = orderCreateDate;
		}
		public String getOrderCreateDate() {
			return orderCreateDate;
		}

		public void setProvinceName(String provinceName) {
			this.provinceName = provinceName;
		}
		public String getProvinceName() {
			return provinceName;
		}

		public void setPaymentCategoryDispName(String paymentCategoryDispName) {
			this.paymentCategoryDispName = paymentCategoryDispName;
		}
		public String getPaymentCategoryDispName() {
			return paymentCategoryDispName;
		}

		public void setOrderRemark(String orderRemark) {
			this.orderRemark = orderRemark;
		}
		public String getOrderRemark() {
			return orderRemark;
		}

		public void setSendPay(String sendPay) {
			this.sendPay = sendPay;
		}
		public String getSendPay() {
			return sendPay;
		}

		public void setCountyName(String countyName) {
			this.countyName = countyName;
		}
		public String getCountyName() {
			return countyName;
		}

		public void setParentOrderId(String parentOrderId) {
			this.parentOrderId = parentOrderId;
		}
		public String getParentOrderId() {
			return parentOrderId;
		}

		public void setIsNotice(String isNotice) {
			this.isNotice = isNotice;
		}
		public String getIsNotice() {
			return isNotice;
		}

		public void setPay(String pay) {
			this.pay = pay;
		}
		public String getPay() {
			return pay;
		}

		public void setVendorStoreId(String vendorStoreId) {
			this.vendorStoreId = vendorStoreId;
		}
		public String getVendorStoreId() {
			return vendorStoreId;
		}

		public void setCreateDate(String createDate) {
			this.createDate = createDate;
		}
		public String getCreateDate() {
			return createDate;
		}

		public void setConsigneeName(String consigneeName) {
			this.consigneeName = consigneeName;
		}
		public String getConsigneeName() {
			return consigneeName;
		}

		public void setOrderSource(String orderSource) {
			this.orderSource = orderSource;
		}
		public String getOrderSource() {
			return orderSource;
		}

		public void setPaymentCategory(String paymentCategory) {
			this.paymentCategory = paymentCategory;
		}
		public String getPaymentCategory() {
			return paymentCategory;
		}

		public void setExpectedDeliveryTime(String expectedDeliveryTime) {
			this.expectedDeliveryTime = expectedDeliveryTime;
		}
		public String getExpectedDeliveryTime() {
			return expectedDeliveryTime;
		}

		public void setOrderTime(String orderTime) {
			this.orderTime = orderTime;
		}
		public String getOrderTime() {
			return orderTime;
		}

		public void setPostcode(String postcode) {
			this.postcode = postcode;
		}
		public String getPostcode() {
			return postcode;
		}

		public void setCustomOrderId(String customOrderId) {
			this.customOrderId = customOrderId;
		}
		public String getCustomOrderId() {
			return customOrderId;
		}

		public void setRefundSourceFlag(String refundSourceFlag) {
			this.refundSourceFlag = refundSourceFlag;
		}
		public String getRefundSourceFlag() {
			return refundSourceFlag;
		}

		public void setVendorStoreName(String vendorStoreName) {
			this.vendorStoreName = vendorStoreName;
		}
		public String getVendorStoreName() {
			return vendorStoreName;
		}

		public void setAddress(String address) {
			this.address = address;
		}
		public String getAddress() {
			return address;
		}

		public void setEmail(String email) {
			this.email = email;
		}
		public String getEmail() {
			return email;
		}

		public void setPin(String pin) {
			this.pin = pin;
		}
		public String getPin() {
			return pin;
		}

		public void setOrderDetailList(List<OrderDetailList> orderDetailList) {
			this.orderDetailList = orderDetailList;
		}
		public List<OrderDetailList> getOrderDetailList() {
			return orderDetailList;
		}

		public void setMemoByVendor(String memoByVendor) {
			this.memoByVendor = memoByVendor;
		}
		public String getMemoByVendor() {
			return memoByVendor;
		}

		public void setTelephone(String telephone) {
			this.telephone = telephone;
		}
		public String getTelephone() {
			return telephone;
		}

		public void setOperatorState(String operatorState) {
			this.operatorState = operatorState;
		}
		public String getOperatorState() {
			return operatorState;
		}
		public String getRoReason() {
			return roReason;
		}
		public void setRoReason(String roReason) {
			this.roReason = roReason;
		}

	}



	public class SearchResult {

		private String recordCount;
		private String errorMessage;
		private String status;
		private String errorCode;
		private List<ResultDtoList> resultDtoList;
		public void setRecordCount(String recordCount) {
			this.recordCount = recordCount;
		}
		public String getRecordCount() {
			return recordCount;
		}

		public void setErrorMessage(String errorMessage) {
			this.errorMessage = errorMessage;
		}
		public String getErrorMessage() {
			return errorMessage;
		}

		public void setStatus(String status) {
			this.status = status;
		}
		public String getStatus() {
			return status;
		}

		public void setErrorCode(String errorCode) {
			this.errorCode = errorCode;
		}
		public String getErrorCode() {
			return errorCode;
		}

		public void setResultDtoList(List<ResultDtoList> resultDtoList) {
			this.resultDtoList = resultDtoList;
		}
		public List<ResultDtoList> getResultDtoList() {
			return resultDtoList;
		}

	}


	public class Jingdong_dropship_dps_searchoutboundorder_response {

		private SearchResult searchResult;

		private SearchResult searchPreResult;
		private String code;

		public void setSearchResult(SearchResult searchResult) {
			this.searchResult = searchResult;
		}
		public SearchResult getSearchResult() {
			return searchResult;
		}
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public SearchResult getSearchPreResult() {
			return searchPreResult;
		}
		public void setSearchPreResult(SearchResult searchPreResult) {
			this.searchPreResult = searchPreResult;
		}

	}

}
