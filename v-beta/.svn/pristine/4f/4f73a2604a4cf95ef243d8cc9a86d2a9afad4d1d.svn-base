package com.dsc.spos.scheduler.job;

import java.net.URLEncoder;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.sql.Select;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.json.cust.req.DCP_OrderStatusLogCreateReq;
import com.dsc.spos.json.cust.req.DCP_OrderStatusLogCreateReq.level1Elm;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.scheduler.job.GuanYiOrderGet.Goods;
import com.dsc.spos.scheduler.job.GuanYiOrderGet.OrderSalelist;
import com.dsc.spos.scheduler.job.GuanYiOrderGet.Orderdetail;
import com.dsc.spos.scheduler.job.GuanYiOrderGet.Orders;
import com.dsc.spos.scheduler.job.GuanYiOrderGet.Result;
import com.dsc.spos.scheduler.job.GuanYiOrderGet.Resultdetail;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.OrderUtil;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.GaoDeGeoModel;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.WUXIANGUtil;
import com.google.gson.reflect.TypeToken;

public class GuanYiGoodsUpdate extends InitJob
{
	//查询舞像的订单
	Logger logger = LogManager.getLogger(GuanYiGoodsUpdate.class.getName());

	static boolean bRun=false;//标记此服务是否正在执行中
	String jddjLogFileName = "GuanYiGoodsUpdate";
	public String doExe() throws Exception
	{
		String sReturnInfo = "";
		//官网物流推送单
		HelpTools.writelog_fileName("【同步任务"+jddjLogFileName+"】同步START！",jddjLogFileName);
		//此服务是否正在执行中
		if (bRun)
		{		
			logger.info("\r\n*********同步任务"+jddjLogFileName+"同步正在执行中,本次调用取消:************\r\n");
			HelpTools.writelog_fileName("【同步任务"+jddjLogFileName+"】同步正在执行中,本次调用取消！",jddjLogFileName);
			return sReturnInfo;
		}
		bRun=true;//	
		//从缓存里面查询一下时间戳
		try
		{
			//这里开始查找舞像的定义
			String sql="select * from OC_ECOMMERCE where ECPLATFORMNO='13' ";
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
					String redis_key = "WDMGOODS" + "_" + "99" + "_" + "GUANYI";
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
							if(entry.getKey().equals("OrderGoods_"+store_code))
							{
								saledate=entry.getValue();
							}
						}
					}
					
					String newsqlgoods="select A.*,B.PLU_NAME  from DCP_GOODS A  left join DCP_GOODS_lang B on A.eId=B.eId and A.PLUNO=B.PLUNO and B.lang_type='zh_CN'  "
							+ " where A.eId='"+eId+"'  and A.tran_time>='"+sdate.replace(" ", "").replace("-", "").replace(":", "")+"000"+"' and A.tran_time<='"+enddate.replace(" ", "").replace("-", "").replace(":", "")+"000"+"'  ";
					List<Map<String, Object>> listnewgoods=this.doQueryData(newsqlgoods, null);
					
					try
					{
						//开始组织舞像发送的
						int i=0;
						int page_size=20;

						boolean issussce=true;
						
						if(listnewgoods!=null&&!listnewgoods.isEmpty())
						{
							for (Map<String, Object> map : listnewgoods) 
							{
								String pluno=map.get("PLUNO").toString();
								String status=map.get("STATUS").toString();
								String PLU_NAME=map.get("PLU_NAME").toString(); 
								
								JSONObject js=new JSONObject();
								//加密签名这段可以写公用方法
								js.put("appkey", API_KEY);
								js.put("sessionkey",TOKEN);
								js.put("method","gy.erp.items.get");
								
								js.put("page_no", i);
								js.put("page_size", page_size);
								js.put("code", pluno);
								//处理下sign
								String sign=PosPub.encodeMD5(API_SECRET +js.toString() +API_SECRET).toUpperCase();
								js.put("sign", sign);
								
								//发送的内容
								StringBuffer sb=new StringBuffer();
								HelpTools.writelog_fileName("【同步任务发送：" +"orderList "+ js.toString(),jddjLogFileName);
								String res= HttpSend.SendWuXiang("", js.toString(), API_URL,sb);
								HelpTools.writelog_fileName("【同步任务返回：" +"orderList"+ res,jddjLogFileName);
								JSONObject jsres=new JSONObject(res);
								if(jsres.getBoolean("success"))
								{
									//成功，说明商品存在能查询到商品，直接走商品更新，更新有2中一种是update一种是失效
									if(jsres.getJSONArray("items").length()>0)
									{
										//有商品
										if(status.equals("100"))
										{
											//更新商品
										    js=new JSONObject();
										    js.put("appkey", API_KEY);
											js.put("sessionkey",TOKEN);
											js.put("method","gy.erp.item.update");
											
											js.put("code", pluno);
											js.put("name", PLU_NAME);
											//处理下sign
										    sign=PosPub.encodeMD5(API_SECRET +js.toString() +API_SECRET).toUpperCase();
											js.put("sign", sign);
											
											//发送的内容
										    sb=new StringBuffer();
											HelpTools.writelog_fileName("【同步任务发送：" +"orderList "+ js.toString(),jddjLogFileName);
										    res= HttpSend.SendWuXiang("", js.toString(), API_URL,sb);
											HelpTools.writelog_fileName("【同步任务返回：" +"orderList"+ res,jddjLogFileName);
										    jsres=new JSONObject(res);
											if(jsres.getBoolean("success"))
											{
												//新建成功
											}
										}
										else
										{
											//失效商品
										    js=new JSONObject();
										    js.put("appkey", API_KEY);
											js.put("sessionkey",TOKEN);
											js.put("method","gy.erp.item.delete");
											
											js.put("code", pluno);
											js.put("operater", "admin");
											//处理下sign
										    sign=PosPub.encodeMD5(API_SECRET +js.toString() +API_SECRET).toUpperCase();
											js.put("sign", sign);
											
											//发送的内容
										    sb=new StringBuffer();
											HelpTools.writelog_fileName("【同步任务发送：" +"orderList "+ js.toString(),jddjLogFileName);
										    res= HttpSend.SendWuXiang("", js.toString(), API_URL,sb);
											HelpTools.writelog_fileName("【同步任务返回：" +"orderList"+ res,jddjLogFileName);
										    jsres=new JSONObject(res);
											if(jsres.getBoolean("success"))
											{
												//新建成功
											}
											
										
										}
									}
									else
									{
										//无商品
										if(status.equals("100"))
										{
											//新建商品
											js=new JSONObject();
											js.put("appkey", API_KEY);
											js.put("sessionkey",TOKEN);
											js.put("method","gy.erp.item.add");
											
											js.put("code", pluno);
											js.put("name", PLU_NAME);
											//处理下sign
										    sign=PosPub.encodeMD5(API_SECRET +js.toString() +API_SECRET).toUpperCase();
											js.put("sign", sign);
											
											//发送的内容
										    sb=new StringBuffer();
											HelpTools.writelog_fileName("【同步任务发送：" +"orderList "+ js.toString(),jddjLogFileName);
										    res= HttpSend.SendWuXiang("", js.toString(), API_URL,sb);
											HelpTools.writelog_fileName("【同步任务返回：" +"orderList"+ res,jddjLogFileName);
										    jsres=new JSONObject(res);
											if(jsres.getBoolean("success"))
											{
												//新建成功
											}
											
										}
										else
										{
											//不做任何操作
										}
									}
								}
								else
								{
									//接口调用失败
								    issussce=false;
								    bRun=false;
								}
								
							}
						}
						
						
						//这里更新一下redis缓存	
						//String hash_key = orderid + "&" + orderStatus;
						if(issussce==false)
						{
							HelpTools.writelog_fileName("【同步失败跳出一个循环：" + store_code,jddjLogFileName);
							break;
						}

						String hash_key = "OrderGoods_"+store_code;
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
	public  String GetWuXiangOrderResponse(Orderdetail osdetail,String eId,String belfirm) throws Exception
	{

		Resultdetail curreginfo=osdetail.getResult();

		//解析收到的舞像请求 
		try 
		{
			JSONObject jsonobjresponse = new JSONObject();
			String shopId =" ";//主键不能为空，所以默认空格

			jsonobjresponse.put("eId", eId);
			jsonobjresponse.put("customerNO", curreginfo.getCard_no());						
			jsonobjresponse.put("status", "1");
			jsonobjresponse.put("loadDocType", "7");//1.饿了么 2.美团外卖 3.微商城							
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
			String orderNo = curreginfo.getOrder_no();
			jsonobjresponse.put("orderNO", orderNo);
			//String address = datasobj.get("address").toString();//
			String address=curreginfo.getReceiver_detail();

			//String contactName = datasobj.get("contactName").toString();//联系人

			jsonobjresponse.put("contMan", curreginfo.getUser_name());
			//String contactTelephone = datasobj.get("contactTelephone").toString();//
			jsonobjresponse.put("contTel", curreginfo.getMobile());

			jsonobjresponse.put("getMan", curreginfo.getReceiver_name());
			//String contactTelephone = datasobj.get("contactTelephone").toString();//
			jsonobjresponse.put("getMantel", curreginfo.getReceiver_phone());

			//先给死成配送
			String deliverType = "2";//微商城取货方式 1=自提 2=配送	

			//对门店做处理
			String wuxiangshop="";
			if(curreginfo.getPost_type().equals("0"))
			{
				//卡劵的，直接不接
				return null;
			}

			String status="";
			if(curreginfo.getPost_type().equals("2"))
			{
				deliverType="3";
				wuxiangshop=curreginfo.getSelf_store_code();
				jsonobjresponse.put("status", "1");
				status="1";
			}
			else
			{
				deliverType="2";
				jsonobjresponse.put("status", "0");
				status="0";

				wuxiangshop=curreginfo.getSelf_store_code();
				try
				{
					//这里省 市 区 使用逗号隔开的
					String[] addlist=address.split("，");
					jsonobjresponse.put("PROVINCE", addlist[0]);
					jsonobjresponse.put("oCITY", addlist[1]);
					jsonobjresponse.put("COUNTY", addlist[2]);
					String addresstemp=address.replace("，", "");
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
						//顺便更新一下推荐的门店
						//String dsql="select * from (  select a.*,b.org_name ,F_CRM_GetDistance("+listjinwei[0]+","+listjinwei[1] +",a.LONGITUDE,a.LATITUDE) DISTANCE from DCP_ORG a "
						String dsql="select * from (  select a.*,b.org_name ,F_CRM_GetDistance("+listjinwei[1]+","+listjinwei[0] +",a.LATITUDE,a.LONGITUDE) DISTANCE from DCP_ORG a "
								+ " left join DCP_ORG_Lang B on A.eId=B.eId and A.OrganizationNo=B.OrganizationNo and B.Lang_Type='zh_CN'  where a.eId='"+eId+"' and a.BELFIRM='"+belfirm+"' )"
								+ " order by DISTANCE,ORGANIZATIONNO ";
						List<Map<String, Object>> dsqldate=this.doQueryData(dsql, null);
						String MACHSHOP="";
						String MACHSHOPNAME="";
						if(dsqldate!=null&&!dsqldate.isEmpty())
						{
							//								MACHSHOP=dsqldate.get(0).get("ORGANIZATIONNO").toString();
							//								MACHSHOPNAME=dsqldate.get(0).get("ORG_NAME").toString();
							wuxiangshop=dsqldate.get(0).get("ORGANIZATIONNO").toString();
							MACHSHOPNAME=dsqldate.get(0).get("ORG_NAME").toString();
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
			String cdate= curreginfo.getCreate_time(); //下单时间 2018-08-29 16:57:13
			String orderDateTime="";
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = null; 
			try 
			{ 
				date = format.parse(cdate); 
				orderDateTime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(date);
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

			//主键不能为空
			if (shopId == null || shopId.isEmpty() || shopId.length() == 0)
			{
				shopId =" ";
			}

			//查询门店名称
			String sqlshop="select * from DCP_ORG_lang where organizationNO='"+wuxiangshop+"'  and eId='"+eId+"' and lang_type='zh_CN' ";
			List<Map<String, Object>> listsql=this.doQueryData(sqlshop, null);
			String shopwuxiangname="";
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


			if(curreginfo.getExpect_date_detail()!=null&&!curreginfo.getExpect_date_detail().isEmpty())
			{
				String wuxiangdate=curreginfo.getExpect_date_detail();
				String needDate = wuxiangdate.split(" ")[0];//微商城格式 2018-08-08
				try 
				{
					date = new SimpleDateFormat("yyyy-MM-dd").parse(needDate);
					needDate = new SimpleDateFormat("yyyyMMdd").format(date);		
				} 
				catch (Exception e) 
				{

				}
				jsonobjresponse.put("shipDate", needDate);//配送日期格式yyyyMMdd 20180808

				//时间不知道怎么处理
				String needTime ="";//微商城格式 17:00
				try 
				{
					String[] needTimetemp=wuxiangdate.split(" ")[1].split("-");
					String sneed1=needTimetemp[0].replace(":", "");
					String sneed2=needTimetemp[1].replace(":", "");

					needTime=sneed1+"00-"+sneed2+"00";

				} 
				catch (Exception e) 
				{

				}
				jsonobjresponse.put("shipTime", needTime);//配送时间 HHmmss 170000 
			}

			String message =curreginfo.getDelivery_desc();//买家留言
			try 
			{
				message = curreginfo.getDelivery_desc();//买家留言
			} 
			catch (Exception e) 
			{
				message = "";		
			}
			jsonobjresponse.put("memo", message);//单头备注
			//String totalAmount = datasobj.get("totalAmount").toString();//微商城 订单总应付金额

			jsonobjresponse.put("tot_oldAmt", curreginfo.getOrder_amount()+curreginfo.getFreight_amount());//订单原价
			jsonobjresponse.put("tot_Amt", curreginfo.getOrder_amount()+curreginfo.getFreight_amount()-curreginfo.getFee_amount());//订单金额
			jsonobjresponse.put("incomeAmt", curreginfo.getOrder_amount()+curreginfo.getFreight_amount()-curreginfo.getFee_amount());//商家实收金额

			//折扣金额
			jsonobjresponse.put("totDisc", curreginfo.getFee_amount());//商家实收金额
			jsonobjresponse.put("sellerDisc",curreginfo.getFee_amount());//商户优惠总额

			jsonobjresponse.put("manualNO",orderNo);//手工单号赋值订单号

			//String payedAmount = datasobj.get("payedAmount").toString();//微商城 已付金额
			jsonobjresponse.put("payAmt", curreginfo.getOrder_amount()+curreginfo.getFreight_amount()-curreginfo.getFee_amount() );//用户已支付金额

			//String deliverAmount = datasobj.get("deliverAmount").toString();//微商城 运费
			jsonobjresponse.put("shipFee", curreginfo.getFreight_amount());//配送费
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
			for(int i=0; i< curreginfo.getGoods().size();i++)
			{				
				try 
				{
					JSONObject goodsobj = new JSONObject();

					//JSONObject job = goodsarray.getJSONObject(i);
					double price = 0;
					double quantity = 0;
					String price_str = "0";
					String quantity_str = "0";
					try 
					{
						price =curreginfo.getGoods().get(i).getGoods_price();
					} 
					catch (Exception e) 
					{

					}
					try 
					{
						quantity_str = curreginfo.getGoods().get(i).getQuantity()+"";
						quantity = Double.parseDouble(quantity_str);

					} 
					catch (Exception e) 
					{

					}
					double amt = price*quantity;
					double goodsamt=curreginfo.getGoods().get(i).getPaid_amount();
					String pluno=curreginfo.getGoods().get(i).getGoods_sku_code();
					//查找一下PLUNO的商品品类
					if(sno.isEmpty())
					{
						String sqlpluno="select * from DCP_GOODS where eId='"+eId+"' and pluno='"+pluno+"' ";
						List<Map<String, Object>> listsqlpluno=this.doQueryData(sqlpluno, null);
						if(listsqlpluno!=null&&!listsqlpluno.isEmpty())
						{
							sno=listsqlpluno.get(0).get("SNO").toString();
						}
					}

					goodsobj.put("item", i+1);
					goodsobj.put("pluNO", curreginfo.getGoods().get(i).getGoods_sku_code());
					goodsobj.put("pluBarcode", curreginfo.getGoods().get(i).getGoods_sku_code());
					//goodsobj.put("pluName", curreginfo.getGoods().get(i).goods_name);
					//处理下把规格放名称里面处理
					goodsobj.put("pluName", curreginfo.getGoods().get(i).goods_name+curreginfo.getGoods().get(i).getGoods_sku_name() );

					goodsobj.put("specName", curreginfo.getGoods().get(i).getGoods_sku_name() );
					goodsobj.put("attrName", "");
					goodsobj.put("unit", "");
					goodsobj.put("price", price);
					goodsobj.put("qty", quantity_str);
					goodsobj.put("goodsGroup", "");
					goodsobj.put("disc", amt-goodsamt);
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
			//这里处理一下蛋糕还是西点订单
			if(sno.startsWith("2"))
			{
				//蛋糕订单,需要调度
				jsonobjresponse.put("orderType", "1");
			}
			else
			{
				jsonobjresponse.put("orderType", "2");
			}

			//订单添加一条付款方式
			JSONObject payobj = new JSONObject();
			payobj.put("item", 1);
			payobj.put("payCode", curreginfo.getPay_type());
			payobj.put("payCodeerp", curreginfo.getPay_type());
			payobj.put("pay", curreginfo.getOrder_amount()+curreginfo.getFreight_amount()-curreginfo.getFee_amount());
			payobj.put("isOrderpay", "Y");
			payobj.put("payName", "");
			if(curreginfo.getPay_type()==0)
			{
				payobj.put("payName", "优惠支付");
			}
			if(curreginfo.getPay_type()==1)
			{
				payobj.put("payName", "支付宝");
			}
			if(curreginfo.getPay_type()==2)
			{
				payobj.put("payName", "微信");
			}
			if(curreginfo.getPay_type()==3)
			{
				payobj.put("payName", "翼支付");
			}
			if(curreginfo.getPay_type()==4)
			{
				payobj.put("payName", "货到付款,");
			}
			if(curreginfo.getPay_type()==5)
			{
				payobj.put("payName", "余额支付");
			}

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
			//String hash_key = orderid + "&" + orderStatus;
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

					DCP_OrderStatusLogCreateReq OReq = new DCP_OrderStatusLogCreateReq();
					DCP_OrderStatusLogCreateReq.level1Elm onelv1 =  OReq.new level1Elm();
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


	//订单列表查询
	class OrderSalelist {

		private String code;
		private String message;
		private Result result;
		public void setCode(String code) {
			this.code = code;
		}
		public String getCode() {
			return code;
		}

		public void setMessage(String message) {
			this.message = message;
		}
		public String getMessage() {
			return message;
		}

		public void setResult(Result result) {
			this.result = result;
		}
		public Result getResult() {
			return result;
		}
	}

	class Orders {

		private String order_no;
		private String store_code;
		private String create_time;
		private String mobile;
		private String pay_time;
		private int order_status;
		private float fee_amount;
		private float freight_amount;
		private String pay_no;
		private float order_amount;
		private float paid_amount;
		private String master_order_no;
		private int pay_type;
		private String other_order_no;
		public void setOrder_no(String order_no) {
			this.order_no = order_no;
		}
		public String getOrder_no() {
			return order_no;
		}

		public void setStore_code(String store_code) {
			this.store_code = store_code;
		}
		public String getStore_code() {
			return store_code;
		}

		public void setCreate_time(String create_time) {
			this.create_time = create_time;
		}
		public String getCreate_time() {
			return create_time;
		}

		public void setMobile(String mobile) {
			this.mobile = mobile;
		}
		public String getMobile() {
			return mobile;
		}

		public void setPay_time(String pay_time) {
			this.pay_time = pay_time;
		}
		public String getPay_time() {
			return pay_time;
		}

		public void setOrder_status(int order_status) {
			this.order_status = order_status;
		}
		public int getOrder_status() {
			return order_status;
		}

		public void setFee_amount(float fee_amount) {
			this.fee_amount = fee_amount;
		}
		public float getFee_amount() {
			return fee_amount;
		}

		public void setFreight_amount(float freight_amount) {
			this.freight_amount = freight_amount;
		}
		public float getFreight_amount() {
			return freight_amount;
		}

		public void setPay_no(String pay_no) {
			this.pay_no = pay_no;
		}
		public String getPay_no() {
			return pay_no;
		}

		public void setOrder_amount(float order_amount) {
			this.order_amount = order_amount;
		}
		public float getOrder_amount() {
			return order_amount;
		}

		public void setPaid_amount(float paid_amount) {
			this.paid_amount = paid_amount;
		}
		public float getPaid_amount() {
			return paid_amount;
		}

		public void setMaster_order_no(String master_order_no) {
			this.master_order_no = master_order_no;
		}
		public String getMaster_order_no() {
			return master_order_no;
		}

		public void setPay_type(int pay_type) {
			this.pay_type = pay_type;
		}
		public int getPay_type() {
			return pay_type;
		}

		public void setOther_order_no(String other_order_no) {
			this.other_order_no = other_order_no;
		}
		public String getOther_order_no() {
			return other_order_no;
		}

	}

	class Result {

		private List<Orders> orders;

		private List<Orders> orderSales;

		public void setOrders(List<Orders> orders) {
			this.orders = orders;
		}
		public List<Orders> getOrders() {
			return orders;
		}
		public List<Orders> getOrderSales() {
			return orderSales;
		}
		public void setOrderSales(List<Orders> orderSales) {
			this.orderSales = orderSales;
		}

	}

	//订单详情查询
	class  Orderdetail{

		private String code;
		private String message;
		private Resultdetail result;
		public void setCode(String code) {
			this.code = code;
		}
		public String getCode() {
			return code;
		}

		public void setMessage(String message) {
			this.message = message;
		}
		public String getMessage() {
			return message;
		}

		public void setResult(Resultdetail result) {
			this.result = result;
		}
		public Resultdetail getResult() {
			return result;
		}
	}


	class Goods {

		private String goods_sku_code;
		private String goods_sku_name;
		private String goods_image_url;
		private String goods_code;
		private String goods_name;
		private double goods_price;
		private int quantity;
		private String coupon_name;
		private double coupon_amount;
		private int point_name;
		private double point_amount;
		private String marketing_name;
		private double marketing_amount;
		private double fee_amount;
		private double paid_amount;
		public void setGoods_sku_code(String goods_sku_code) {
			this.goods_sku_code = goods_sku_code;
		}
		public String getGoods_sku_code() {
			return goods_sku_code;
		}

		public void setGoods_sku_name(String goods_sku_name) {
			this.goods_sku_name = goods_sku_name;
		}
		public String getGoods_sku_name() {
			return goods_sku_name;
		}

		public void setGoods_image_url(String goods_image_url) {
			this.goods_image_url = goods_image_url;
		}
		public String getGoods_image_url() {
			return goods_image_url;
		}

		public void setGoods_code(String goods_code) {
			this.goods_code = goods_code;
		}
		public String getGoods_code() {
			return goods_code;
		}

		public void setGoods_name(String goods_name) {
			this.goods_name = goods_name;
		}
		public String getGoods_name() {
			return goods_name;
		}

		public void setGoods_price(double goods_price) {
			this.goods_price = goods_price;
		}
		public double getGoods_price() {
			return goods_price;
		}

		public void setQuantity(int quantity) {
			this.quantity = quantity;
		}
		public int getQuantity() {
			return quantity;
		}

		public void setCoupon_name(String coupon_name) {
			this.coupon_name = coupon_name;
		}
		public String getCoupon_name() {
			return coupon_name;
		}

		public void setCoupon_amount(double coupon_amount) {
			this.coupon_amount = coupon_amount;
		}
		public double getCoupon_amount() {
			return coupon_amount;
		}

		public void setPoint_name(int point_name) {
			this.point_name = point_name;
		}
		public int getPoint_name() {
			return point_name;
		}

		public void setPoint_amount(double point_amount) {
			this.point_amount = point_amount;
		}
		public double getPoint_amount() {
			return point_amount;
		}

		public void setMarketing_name(String marketing_name) {
			this.marketing_name = marketing_name;
		}
		public String getMarketing_name() {
			return marketing_name;
		}

		public void setMarketing_amount(double marketing_amount) {
			this.marketing_amount = marketing_amount;
		}
		public double getMarketing_amount() {
			return marketing_amount;
		}

		public void setFee_amount(double fee_amount) {
			this.fee_amount = fee_amount;
		}
		public double getFee_amount() {
			return fee_amount;
		}

		public void setPaid_amount(double paid_amount) {
			this.paid_amount = paid_amount;
		}
		public double getPaid_amount() {
			return paid_amount;
		}

	}


	class Resultdetail {

		private String user_name;
		private String mobile;
		private String card_no;
		private String off_card_no;
		private String order_no;
		private String master_order_no;
		private String other_order_no;
		private String pay_no;
		private String create_time;
		private String pay_time;
		private String store_code;
		private int pay_type;
		private int order_status;
		private double order_amount;
		private double fee_amount;
		private double freight_amount;
		private double paid_amount;
		private double point_amount;
		private double coupon_amount;
		private String coupon_code;
		private String coupon_name;
		private String logistics_code;
		private String logistics_no;
		private String receiver_name;
		private String receiver_phone;
		private String receiver_detail;
		private String zipcode;
		private String post_type;
		private String self_store_code;
		private String expect_date_detail;
		private String delivery_desc;

		public String getExpect_date_detail() {
			return expect_date_detail;
		}
		public void setExpect_date_detail(String expect_date_detail) {
			this.expect_date_detail = expect_date_detail;
		}
		private List<Goods> goods;
		public void setUser_name(String user_name) {
			this.user_name = user_name;
		}
		public String getUser_name() {
			return user_name;
		}

		public void setMobile(String mobile) {
			this.mobile = mobile;
		}
		public String getMobile() {
			return mobile;
		}

		public void setCard_no(String card_no) {
			this.card_no = card_no;
		}
		public String getCard_no() {
			return card_no;
		}

		public void setOff_card_no(String off_card_no) {
			this.off_card_no = off_card_no;
		}
		public String getOff_card_no() {
			return off_card_no;
		}

		public void setOrder_no(String order_no) {
			this.order_no = order_no;
		}
		public String getOrder_no() {
			return order_no;
		}

		public void setMaster_order_no(String master_order_no) {
			this.master_order_no = master_order_no;
		}
		public String getMaster_order_no() {
			return master_order_no;
		}

		public void setOther_order_no(String other_order_no) {
			this.other_order_no = other_order_no;
		}
		public String getOther_order_no() {
			return other_order_no;
		}

		public void setPay_no(String pay_no) {
			this.pay_no = pay_no;
		}
		public String getPay_no() {
			return pay_no;
		}

		public void setCreate_time(String create_time) {
			this.create_time = create_time;
		}
		public String getCreate_time() {
			return create_time;
		}

		public void setPay_time(String pay_time) {
			this.pay_time = pay_time;
		}
		public String getPay_time() {
			return pay_time;
		}

		public void setStore_code(String store_code) {
			this.store_code = store_code;
		}
		public String getStore_code() {
			return store_code;
		}

		public void setPay_type(int pay_type) {
			this.pay_type = pay_type;
		}
		public int getPay_type() {
			return pay_type;
		}

		public void setOrder_status(int order_status) {
			this.order_status = order_status;
		}
		public int getOrder_status() {
			return order_status;
		}

		public void setOrder_amount(double order_amount) {
			this.order_amount = order_amount;
		}
		public double getOrder_amount() {
			return order_amount;
		}

		public void setFee_amount(double fee_amount) {
			this.fee_amount = fee_amount;
		}
		public double getFee_amount() {
			return fee_amount;
		}

		public void setFreight_amount(double freight_amount) {
			this.freight_amount = freight_amount;
		}
		public double getFreight_amount() {
			return freight_amount;
		}

		public void setPaid_amount(double paid_amount) {
			this.paid_amount = paid_amount;
		}
		public double getPaid_amount() {
			return paid_amount;
		}

		public void setPoint_amount(double point_amount) {
			this.point_amount = point_amount;
		}
		public double getPoint_amount() {
			return point_amount;
		}

		public void setCoupon_amount(double coupon_amount) {
			this.coupon_amount = coupon_amount;
		}
		public double getCoupon_amount() {
			return coupon_amount;
		}

		public void setCoupon_code(String coupon_code) {
			this.coupon_code = coupon_code;
		}
		public String getCoupon_code() {
			return coupon_code;
		}

		public void setCoupon_name(String coupon_name) {
			this.coupon_name = coupon_name;
		}
		public String getCoupon_name() {
			return coupon_name;
		}

		public void setLogistics_code(String logistics_code) {
			this.logistics_code = logistics_code;
		}
		public String getLogistics_code() {
			return logistics_code;
		}

		public void setLogistics_no(String logistics_no) {
			this.logistics_no = logistics_no;
		}
		public String getLogistics_no() {
			return logistics_no;
		}

		public void setReceiver_name(String receiver_name) {
			this.receiver_name = receiver_name;
		}
		public String getReceiver_name() {
			return receiver_name;
		}

		public void setReceiver_phone(String receiver_phone) {
			this.receiver_phone = receiver_phone;
		}
		public String getReceiver_phone() {
			return receiver_phone;
		}

		public void setReceiver_detail(String receiver_detail) {
			this.receiver_detail = receiver_detail;
		}
		public String getReceiver_detail() {
			return receiver_detail;
		}

		public void setZipcode(String zipcode) {
			this.zipcode = zipcode;
		}
		public String getZipcode() {
			return zipcode;
		}

		public void setGoods(List<Goods> goods) {
			this.goods = goods;
		}
		public List<Goods> getGoods() {
			return goods;
		}
		public String getPost_type() {
			return post_type;
		}
		public void setPost_type(String post_type) {
			this.post_type = post_type;
		}
		public String getSelf_store_code() {
			return self_store_code;
		}
		public void setSelf_store_code(String self_store_code) {
			this.self_store_code = self_store_code;
		}
		public String getDelivery_desc() {
			return delivery_desc;
		}
		public void setDelivery_desc(String delivery_desc) {
			this.delivery_desc = delivery_desc;
		}

	}

}
