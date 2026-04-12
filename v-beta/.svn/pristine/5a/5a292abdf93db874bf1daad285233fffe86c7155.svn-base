package com.dsc.spos.scheduler.job;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.dsc.spos.waimai.candao.candaoService;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.dsc.spos.waimai.kdniao.kdnQGService;
import com.dsc.spos.waimai.kdniao.kdnTCService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.model.ShangyouOrder;
import com.dsc.spos.thirdpart.PAOTUIService;
import com.dsc.spos.thirdpart.ThirdpartConstants;
import com.dsc.spos.thirdpart.youzan.YouZanCallBackServiceV3;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.utils.ec.shangyou;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.dada.DadaService;
import com.dsc.spos.waimai.entity.orderStatusLog;
import com.dsc.spos.waimai.sftc.sftcService;
import com.dsc.spos.waimai.shansong.SHANSONGService;
import com.dsc.spos.waimai.yto.ytoService;

import cn.hutool.core.convert.Convert;


/**
 * @author 08546
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class ExpressOrderCreate extends InitJob 
{
	//******兼容即时服务的,只查询指定的那张单据******
	String pCompanyNo="";
	String pShop="";	
	String pOrganizationNO="";
	String pBillNo="";

	Logger logger = LogManager.getLogger(ExpressOrderCreate.class.getName());

	static boolean bRun=false;//标记此服务是否正在执行中

	public ExpressOrderCreate()
	{

	}

	public ExpressOrderCreate(String companyNo,String shop,String organizationNO, String billNo)
	{
		pCompanyNo=companyNo;
		pShop=shop;
		pOrganizationNO=organizationNO;
		pBillNo=billNo;
	}

	public String doExe() 
	{
		//测试用 begin
//		ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext("classpath*:dsmServiceModule.xml");
//        MySpringContext mySpringContext = new MySpringContext();
//        mySpringContext.setApplicationContext(classPathXmlApplicationContext);
//        DsmDAO dsmDao=(DsmDAO) mySpringContext.getContext().getBean("sposDao");
//        StaticInfo.dao=dsmDao;
        //测试用 end
        
		//返回信息
		String sReturnInfo="";
		//此服务是否正在执行中
		if (bRun && pCompanyNo.equals(""))
		{		
			logger.info("\r\n*********自動發快遞ExpressOrderCreate正在执行中,本次调用取消:************\r\n");

			sReturnInfo="定时传输任务-自動發快遞ExpressOrderCreate正在执行中！";
			return sReturnInfo;
		}

		bRun=true;//			

		logger.info("\r\n*********自動發快遞ExpressOrderCreate定时调用Start:************\r\n");
		this.Log("*********自動發快遞ExpressOrderCreate定时调用Start:************");

		try
		{
            boolean runTimeFlag = this.jobRunTimeFlag();
            if(!runTimeFlag)
            {
                sReturnInfo= "【同步任务ExpressOrderCreate】不在job设置的运行时间内！";
                this.Log(sReturnInfo+",自動發快遞ExpressOrderCreate定时调用定时调用End");
                return sReturnInfo;
            }
			//status 1-订单开立   2-已结单
			StringBuffer sqlOrder= new StringBuffer("select * from dcp_order a where  a.billtype=1  and a.paystatus='3' and (a.status='1' or a.status='2') and a.autodelivery='Y' and (a.deliveryno='' or a.deliveryno is null) "
					+ " and ((a.deliverystatus not in ('-2','-1','0') and a.deliverystatus is not null) or a.deliverystatus is null) ");

			String sdate = new SimpleDateFormat("yyyyMMdd").format(new Date());
			//没配送日期也兼容
			sqlOrder.append(" and ((shipdate>='"+sdate+"' and deliverytype<>'21' and deliverytype<>'25' ) or deliverytype='21' or deliverytype='25' ) ");
			//根据条件
			if (pBillNo.equals("")==false) 
			{
				sqlOrder.append(" and a.eid='"+pCompanyNo+"' and a.orderno='"+pBillNo+"' and a.shop='"+pShop+"' ");
			}					
			this.Log("ExpressOrderCreate定时任务。查询sql="+sqlOrder.toString());
			List<Map<String, Object>> getQData=this.doQueryData(sqlOrder.toString(), null);
			if (getQData != null && getQData.isEmpty() == false)
			{
				String sqlOutSaleset="select * from dcp_outsaleset where status='100' ";
				List<Map<String, Object>> getQData_OutSaleset=this.doQueryData(sqlOutSaleset, null);
				if (getQData_OutSaleset != null && getQData_OutSaleset.isEmpty() == false)
				{
												
					for (Map<String, Object> map_order : getQData)
					{
						try
						{
                            String eId = map_order.get("EID").toString();
                            String channelId = map_order.get("CHANNELID").toString();
                            String deliveryType = map_order.get("DELIVERYTYPE").toString();
                            String machShopId = map_order.get("MACHSHOP").toString();
                            String machShopName = map_order.get("MACHSHOPNAME").toString();
                            String memo = map_order.get("MEMO").toString();
                            String shippingId = map_order.get("SHIPPINGSHOP").toString();
                            String shippingName = map_order.get("SHIPPINGSHOPNAME").toString();
                            String shopId = map_order.get("SHOP").toString();
                            String shopName = map_order.get("SHOPNAME").toString();
                            String loadDocType = map_order.get("LOADDOCTYPE").toString();
                            String orderNo_DB = map_order.get("ORDERNO").toString();
                            String DELIVERY_CREATETIME=map_order.get("DELIVERY_CREATETIME").toString();//后加字段，防止没修改成功，直接报错

                            boolean invokeCreateRes = false;//调用三方物流接口接口，是否成功
							if (deliveryType.equals("21"))//管易物流
							{
                                Map<String, Object> outSalesetMap=this.getDeliverySetByShippingShop(eId,deliveryType,shippingId);
                                if(outSalesetMap==null||outSalesetMap.isEmpty())
                                {
                                    this.Log("ExpressOrderCreate定时任务。查询配送门店对应的物流设置参数为空，请检查物流参数设置，配送门店:"+shippingId+",物流类型:"+deliveryType);
                                    continue;
                                }
								//List<Map<String, Object>> tempOutSaleset=MapDistinct.getWhereMap(getQData_OutSaleset, condi, false);
								if (outSalesetMap != null && outSalesetMap.isEmpty() == false)
								{
									String apiUrl=outSalesetMap.get("APIURL").toString();//"https://v2.api.guanyierp.com/rest/erp_open";
									String appkey=outSalesetMap.get("APPID").toString();
									String secret= outSalesetMap.get("APPSECRET").toString();
									String sessionkey=outSalesetMap.get("APPSIGNKEY").toString();
									String shop_code=outSalesetMap.get("SHOPCODE").toString();

									//
									String order_companyno=eId;
									String order_shopno=map_order.get("SHOP").toString();
									String order_load_doctype=map_order.get("LOADDOCTYPE").toString();
									String order_no=map_order.get("ORDERNO").toString();
									String vip_code=map_order.get("GETMANTEL").toString();

									Map<String, Object> mapOrderA = new LinkedHashMap<String, Object>();
									mapOrderA.put("appkey", appkey);
									mapOrderA.put("sessionkey", sessionkey);
									mapOrderA.put("method", "gy.erp.trade.add");
									mapOrderA.put("shop_code", shop_code);//店铺代码(必填)
									mapOrderA.put("vip_code", shopId);//会员代码(必填)---填手机号码
									//订单类型 Sales-销售订单 Return-换货订单 Charge-费用订单 Delivery-补发货订单 Invoice-补发票订单
									mapOrderA.put("order_type_code", "Sales");//(必填)
									mapOrderA.put("platform_code", order_no);//平台单号
									mapOrderA.put("seller_memo", memo);//卖家备注
									SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
									mapOrderA.put("deal_datetime", sdf.format(System.currentTimeMillis()));//拍单时间(必填)

									mapOrderA.put("receiver_name", map_order.get("GETMAN").toString());//收货人(必填)
									mapOrderA.put("receiver_phone", map_order.get("GETMANTEL").toString());//收货人电话(电话二选一必填)
									mapOrderA.put("receiver_mobile", map_order.get("GETMANTEL").toString());//收货人手机(电话二选一必填)
									mapOrderA.put("receiver_address", map_order.get("ADDRESS").toString());//收货地址(必填)
									mapOrderA.put("receiver_province", map_order.get("PROVINCE").toString());//(必填)
									mapOrderA.put("receiver_city", map_order.get("CITY").toString());//(必填)
									mapOrderA.put("receiver_district", map_order.get("COUNTY").toString());//(必填)


									String sqlOrderDetail="select * from dcp_order_detail where eid='"+eId+"' and orderno='"+order_no+"' ";
									List<Map<String, Object>> getDetailDatas=this.doQueryData(sqlOrderDetail, null);

									if (getDetailDatas==null ||getDetailDatas.size()==0)
									{
										//
										logger.info("\r\n*********自動發快遞ExpressOrderCreate eid="+order_companyno+",SHOP="+order_shopno+",LOADDOCTYPE="+order_load_doctype+",ORDERNO="+order_no+"没有商品明细跳过************\r\n");
										continue;//没有商品明细不用上传
									}
									else
									{
										//商品明细
										List<Map<String, Object>> details = new ArrayList<Map<String, Object>>();
										for (Map<String, Object> oneData2 : getDetailDatas)
										{
											Map<String, Object> detail = new LinkedHashMap<String, Object>();

											String bqty=oneData2.get("QTY").toString();
											if (PosPub.isNumericType(bqty)==false)
											{
												bqty="1";
											}
											detail.put("item_code", oneData2.get("PLUNO").toString());//商品代码(必填)
											//detail.put("sku_code", oneData2.get("SUNIT").toString());//规格代码
											//麻蛋，嘉华死鸭子嘴硬，一直都是单位，你要改成品号，后面挂了别找我
											detail.put("sku_code", oneData2.get("PLUNO").toString());
											detail.put("price", oneData2.get("PRICE").toString());//实际单价
											detail.put("qty", Integer.parseInt(bqty));//数量
											//detail.put("refund", 0);//退款状态 0:未退款 1:退款完成 2:退款中
											//detail.put("note", "");
											details.add(detail);
										}
										mapOrderA.put("details", details);
									}


									//
									StringBuffer sb=new StringBuffer();
									String resbody=HttpSend.SendGuanyiyuan(apiUrl, secret, "gy.erp.trade.add", mapOrderA, order_companyno, order_shopno, order_shopno, order_no, sb);

									if (resbody.equals("")==false)
									{
										JSONObject resjsobject= JSONObject.parseObject(resbody);
										boolean Result=resjsobject.getBoolean("success");

										//
										String errorCode=resjsobject.containsKey("errorCode")?resjsobject.getString("errorCode"):"";//错误代码
										String subErrorCode=resjsobject.containsKey("subErrorCode")?resjsobject.getString("subErrorCode"):"";//子错误diam
										String errorDesc=resjsobject.containsKey("errorDesc")?resjsobject.getString("errorDesc"):"";//错误描述
										String subErrorDesc=resjsobject.containsKey("subErrorDesc")?resjsobject.getString("subErrorDesc"):"";//子错误描述

										//
										if (Result)
										{
											String id=resjsobject.getString("id");//订单ID
											String code=resjsobject.getString("code");//订单单据编号
											String created=resjsobject.getString("created");//订单创建时间

											//执行
											List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
											UptBean ubecOrder=new UptBean("dcp_order");
											ubecOrder.addCondition("EID", new DataValue(order_companyno, Types.VARCHAR));
											ubecOrder.addCondition("ORDERNO", new DataValue(order_no, Types.VARCHAR));

											ubecOrder.addUpdateValue("DELIVERYSTATUS", new DataValue("-1", Types.VARCHAR));//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 8货到物流中心 9消费者七天未取件
                                            ubecOrder.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                                            ubecOrder.addUpdateValue("DELIVERY_CREATETIME", new DataValue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), Types.DATE));//记录物流下单时间
                                            lstData.add(new DataProcessBean(ubecOrder));

											//写订单日志
											String LogStatus="-1";
											orderStatusLog oslog=new orderStatusLog();
											oslog.setCallback_status("N");
											oslog.setChannelId(channelId);
											oslog.setDisplay("1");
											oslog.seteId(eId);
											oslog.setLoadDocBillType(order_load_doctype);
											oslog.setLoadDocOrderNo(order_no);
											oslog.setLoadDocType(order_load_doctype);
											oslog.setMachShopName(machShopName);
											oslog.setMachShopNo(machShopId);
											oslog.setMemo("已上传物流");
											oslog.setNeed_callback("N");
											oslog.setNeed_notify("N");
											oslog.setNotify_status("N");
											oslog.setOpName("admin");
											oslog.setOpNo("admin");
											oslog.setOrderNo(order_no);
											oslog.setShippingShopName(shippingName);
											oslog.setShippingShopNo(shippingId);
											oslog.setShopName(shopName);
											oslog.setShopNo(shopId);
											oslog.setStatus(LogStatus);
											//
											String statusType="2";
											StringBuilder statusTypeName=new StringBuilder();
											String statusName=HelpTools.GetOrderStatusName(statusType, LogStatus, statusTypeName);
											oslog.setStatusName(statusName);
											oslog.setStatusType(statusType);
											oslog.setStatusTypeName(statusTypeName.toString());
											oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
											InsBean	ib_DCP_ORDER_STATUSLOG=HelpTools.InsertOrderStatusLog(oslog);
											lstData.add(new DataProcessBean(ib_DCP_ORDER_STATUSLOG));


											StaticInfo.dao.useTransactionProcessData(lstData);
                                            invokeCreateRes = true;
											logger.info("\r\n***************自動發快遞ExpressOrderCreate 订单编号:"+order_no+"发单成功,管易云返回单号信息:id="+id+",code="+code+",created="+created+"****************\r\n");
										}
										else
										{
											logger.info("\r\n*********自動發快遞ExpressOrderCreate eid="+order_companyno+",SHOP="+order_shopno+",LOADDOCTYPE="+order_load_doctype+",ORDERNO="+order_no+"调用管易云接口地址"+apiUrl+"返回错误：errorCode="+errorCode+",subErrorCode="+subErrorCode+",errorDesc="+errorDesc+",subErrorDesc="+subErrorDesc+"************\r\n");
										}
									}
									else
									{
										String tempError=sb.toString();
										logger.info("\r\n*********自動發快遞ExpressOrderCreate eid="+order_companyno+",SHOP="+order_shopno+",LOADDOCTYPE="+order_load_doctype+",ORDERNO="+order_no+"调用管易云接口地址"+apiUrl+"返回错误："+tempError+"************\r\n");
									}
									sb.setLength(0);
									sb=null;
								}
								else
								{
									logger.info("\r\n*********自動發快遞ExpressOrderCreate物流廠商接口表dcp_outsaleset管易物流未設置資料:************\r\n");

									continue;
								}

							}
							//美团跑腿
							else if(ThirdpartConstants.pt_deliveryType.equals(deliveryType)){
								List<Map<String, Object>> outSalesetList=getQData_OutSaleset.stream().filter(g->(g.get("DELIVERYTYPE")==null?"":g.get("DELIVERYTYPE").toString()).equals(ThirdpartConstants.pt_deliveryType)).collect(Collectors.toList());
								if(outSalesetList==null||outSalesetList.size()==0){
									continue;
								}
								//Map<String, Object> outSalesetMap=outSalesetList.get(0);
                                Map<String, Object> outSalesetMap=this.getDeliverySetByShippingShop(eId,deliveryType,shippingId);
                                if(outSalesetMap==null||outSalesetMap.isEmpty())
                                {
                                    this.Log("ExpressOrderCreate定时任务。查询配送门店对应的物流设置参数为空，请检查物流参数设置，配送门店:"+shippingId+",物流类型:"+deliveryType);
                                    continue;
                                }
								//单号
								String orderNo=map_order.get("ORDERNO").toString();
								String orderShopNo=map_order.get("SHOP").toString();

								String orderLoadDoctype=map_order.get("LOADDOCTYPE").toString();
								String vip_code=map_order.get("GETMANTEL").toString();


								String sqlOrderDetail="select * from dcp_order_detail where eid='"+eId+"' and orderno='"+orderNo+"' ";
								List<Map<String, Object>> getDetailDatas=this.doQueryData(sqlOrderDetail, null);

								PAOTUIService pts=new PAOTUIService();
								if (getDetailDatas==null||getDetailDatas.size()==0){
									//没有商品明细不用上传
									pts.Log("\r\n***eid="+eId+",SHOP="+orderShopNo+",LOADDOCTYPE="+orderLoadDoctype+",ORDERNO="+orderNo+"没有商品明细 跳过************\r\n");
									continue;
								}
								JSONObject json1=pts.addOrder(outSalesetMap, map_order, getDetailDatas);
								if (json1==null)
                                {
                                    continue;
                                }
								String code=json1.getString("code");
								Boolean result=false;
								String peisongId="";//物流单号
								if("0".equals(code)){
									JSONObject dataJson=json1.getJSONObject("data");
									peisongId=dataJson.getString("mt_peisong_id");
									result=true;
								}
								if(result){
//								String id=resjsobject.getString("id");//订单ID
//								String code=resjsobject.getString("code");//订单单据编号
//								String created=resjsobject.getString("created");//订单创建时间

									//执行
									List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
									UptBean ubecOrder=new UptBean("dcp_order");
									ubecOrder.addCondition("EID", new DataValue(eId, Types.VARCHAR));
									ubecOrder.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
									ubecOrder.addUpdateValue("DELIVERYNO", new DataValue(peisongId, Types.VARCHAR));
									ubecOrder.addUpdateValue("DELIVERYSTATUS", new DataValue("0", Types.VARCHAR));//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 8货到物流中心 9消费者七天未取件
									ubecOrder.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                                    ubecOrder.addUpdateValue("DELIVERY_CREATETIME", new DataValue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), Types.DATE));//记录物流下单时间
									lstData.add(new DataProcessBean(ubecOrder));

									//写订单日志
									String LogStatus="0";
									orderStatusLog oslog=new orderStatusLog();
									oslog.setCallback_status("N");
									oslog.setChannelId(channelId);
									oslog.setDisplay("1");
									oslog.seteId(eId);
									oslog.setLoadDocType(orderLoadDoctype);
									oslog.setMachShopName(machShopName);
									oslog.setMachShopNo(machShopId);
									oslog.setMemo("已上传物流");
									oslog.setNeed_callback("N");
									oslog.setNeed_notify("N");
									oslog.setNotify_status("N");
									oslog.setOpName("定时任务");
									oslog.setOpNo("JOB");
									oslog.setOrderNo(orderNo);
									oslog.setShippingShopName(shippingName);
									oslog.setShippingShopNo(shippingId);
									oslog.setShopName(shopName);
									oslog.setShopNo(shopId);
									oslog.setStatus(LogStatus);
									//
									String statusType="2";
									StringBuilder statusTypeName=new StringBuilder();
									String statusName=HelpTools.GetOrderStatusName(statusType, LogStatus, statusTypeName);
									oslog.setStatusName(statusName);
									oslog.setStatusType(statusType);
									oslog.setStatusTypeName(statusTypeName.toString());
									oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
									InsBean	ib_DCP_ORDER_STATUSLOG=HelpTools.InsertOrderStatusLog(oslog);
									if(ib_DCP_ORDER_STATUSLOG!=null){
										lstData.add(new DataProcessBean(ib_DCP_ORDER_STATUSLOG));
									}

									StaticInfo.dao.useTransactionProcessData(lstData);
                                    invokeCreateRes = true;
								}
								else {

                                    String error_msg = "";
                                    if (json1.containsKey("message"))
                                    {
                                        error_msg = json1.get("message").toString();
                                    }
                                    //写订单日志
                                    List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
                                    String LogStatus="0";
                                    orderStatusLog oslog=new orderStatusLog();
                                    oslog.setCallback_status("N");
                                    oslog.setChannelId(channelId);
                                    oslog.setDisplay("1");
                                    oslog.seteId(eId);
                                    oslog.setLoadDocType(orderLoadDoctype);
                                    oslog.setMachShopName(machShopName);
                                    oslog.setMachShopNo(machShopId);
                                    oslog.setMemo(error_msg);

                                    oslog.setNeed_callback("N");
                                    oslog.setNeed_notify("N");
                                    oslog.setNotify_status("N");
                                    oslog.setOpName("定时任务");
                                    oslog.setOpNo("JOB");
                                    oslog.setOrderNo(orderNo);
                                    oslog.setShippingShopName(shippingName);
                                    oslog.setShippingShopNo(shippingId);
                                    oslog.setShopName(shopName);
                                    oslog.setShopNo(shopId);
                                    oslog.setStatus(LogStatus);
                                    //

                                    String statusType = "999";// 其他状态
                                    String updateStaus = "999";// 订单修改

                                    oslog.setStatusType(statusType);
                                    oslog.setStatus(updateStaus);

                                    String statusName = "物流下单失败";
                                    String statusTypeName = "呼叫物流";

                                    oslog.setStatusName(statusName);
                                    oslog.setStatusType(statusType);
                                    oslog.setStatusTypeName(statusTypeName.toString());
                                    oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
                                    orderStatusLogList.add(oslog);
                                    StringBuilder errorMessage = new StringBuilder();
                                    boolean nRet_s = HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorMessage);
                                }

							}
							//达达物流
							else if(ThirdpartConstants.dada_deliveryType.equals(deliveryType)){
								//门店管理的服务地址
								String callback="";

								// 取得门店管理的服务地址
								callback = PosPub.getDCP_URL(eId);
								//callback = callback.replace("invoke", "");
								List<Map<String, Object>> outSalesetList=getQData_OutSaleset.stream().filter(g->(g.get("DELIVERYTYPE")==null?"":g.get("DELIVERYTYPE").toString()).equals(ThirdpartConstants.dada_deliveryType)).collect(Collectors.toList());
								if(outSalesetList==null||outSalesetList.size()==0){
									continue;
								}
								//Map<String, Object> outSalesetMap=outSalesetList.get(0);
                                Map<String, Object> outSalesetMap=this.getDeliverySetByShippingShop(eId,deliveryType,shippingId);
                                if(outSalesetMap==null||outSalesetMap.isEmpty())
                                {
                                    this.Log("ExpressOrderCreate定时任务。查询配送门店对应的物流设置参数为空，请检查物流参数设置，配送门店:"+shippingId+",物流类型:"+deliveryType);
                                    continue;
                                }
								//单号
								String orderNo=map_order.get("ORDERNO").toString();
								String orderShopNo=map_order.get("SHOP").toString();

								String orderLoadDoctype=map_order.get("LOADDOCTYPE").toString();

								/*******************判断下配送日期，是否需要调用***********************/
								boolean isInvokeDada = true;
								StringBuffer isInvokeDadaMess = new StringBuffer("");
								String pickupTime = "";
								String shipDate = map_order.get("SHIPDATE").toString();
								String sdtime = map_order.get("SHIPSTARTTIME").toString();//SHIPENDTIME
								sdtime = sdtime.replace("-", "");
                                if (sdtime.isEmpty())
                                {
                                    sdtime = new SimpleDateFormat("HHmmss").format(new Date());
                                }
								pickupTime = shipDate+ sdtime;// 日期格式如"20181223110438"使用yyyyMMddHHmmss

								// 1-72小时为预约单 超过72小时不能发 1小时之内不为预约单
								long longcur = System.currentTimeMillis();
								Date dateSta =new SimpleDateFormat("yyyyMMddHHmmss").parse(pickupTime);
								long longsta = dateSta.getTime();
								// 差别到分钟
								long diff = (longsta - longcur) / (1000 * 60);
								if(diff>72*60)
								{
									isInvokeDada = false;
									isInvokeDadaMess.append("达达预约单超过72小时不能发！");
								}
								/*if(diff<0)
								{
									isInvokeDada = false;
									isInvokeDadaMess.append("订单的配送时间小于当前系统时间！");
								}*/

								DadaService dada=new DadaService();
								if(!isInvokeDada)
								{
									dada.Log("自动发快递ExpressOrderCreate开始调用dada物流,"+isInvokeDadaMess.toString()+"***eid="+eId+",LOADDOCTYPE="+orderLoadDoctype+",ORDERNO="+orderNo+"无需配送************");
									this.Log("自动发快递ExpressOrderCreate开始调用dada物流,"+isInvokeDadaMess.toString()+"***eid="+eId+",LOADDOCTYPE="+orderLoadDoctype+",ORDERNO="+orderNo+"无需配送************");
									continue;

								}


								String sqlOrderDetail="select * from dcp_order_detail where eid='"+eId+"' and orderno='"+orderNo+"' ";
								List<Map<String, Object>> getDetailDatas=this.doQueryData(sqlOrderDetail, null);

								dada.Log("***自动发快递ExpressOrderCreate开始调用dada物流,eid="+eId+",LOADDOCTYPE="+orderLoadDoctype+",订单号orderNo="+orderNo+"************");
								if (getDetailDatas==null||getDetailDatas.size()==0){
									//没有商品明细不用上传
									dada.Log("***eid="+eId+",LOADDOCTYPE="+orderLoadDoctype+",ORDERNO="+orderNo+"没有商品明细 跳过************");
									continue;
								}
								//预下单接口，才返回达达物流平台单号
								//JSONObject json1=dada.addOrder(outSalesetMap, map_order, getDetailDatas,callback,"1");
								JSONObject json1=dada.addOrder(outSalesetMap, map_order, getDetailDatas,callback,"2");
								if(json1 ==null)
								{
									continue;
								}
								String code=json1.getString("code");

								String deliveryNo="";//物流单号
								if("0".equals(code))
								{

									//执行
									List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
									UptBean ubecOrder=new UptBean("dcp_order");
									ubecOrder.addCondition("EID", new DataValue(eId, Types.VARCHAR));
									ubecOrder.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
									if(!deliveryNo.isEmpty())
									{
										ubecOrder.addUpdateValue("DELIVERYNO", new DataValue(deliveryNo, Types.VARCHAR));
									}
									ubecOrder.addUpdateValue("DELIVERYSTATUS", new DataValue("0", Types.VARCHAR));//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 8货到物流中心 9消费者七天未取件
									ubecOrder.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                                    ubecOrder.addUpdateValue("DELIVERY_CREATETIME", new DataValue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), Types.DATE));//记录物流下单时间
									lstData.add(new DataProcessBean(ubecOrder));

									StaticInfo.dao.useTransactionProcessData(lstData);

                                    invokeCreateRes = true;
									//写订单日志
									List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
									String LogStatus="0";
									orderStatusLog oslog=new orderStatusLog();
									oslog.setCallback_status("N");
									oslog.setChannelId(channelId);
									oslog.setDisplay("1");
									oslog.seteId(eId);
									oslog.setLoadDocType(orderLoadDoctype);
									oslog.setMachShopName(machShopName);
									oslog.setMachShopNo(machShopId);
									oslog.setMemo("已上传物流");
									oslog.setNeed_callback("N");
									oslog.setNeed_notify("N");
									oslog.setNotify_status("N");
									oslog.setOpName("定时任务");
									oslog.setOpNo("JOB");
									oslog.setOrderNo(orderNo);
									oslog.setShippingShopName(shippingName);
									oslog.setShippingShopNo(shippingId);
									oslog.setShopName(shopName);
									oslog.setShopNo(shopId);
									oslog.setStatus(LogStatus);
									//
									String statusType="2";
									StringBuilder statusTypeName=new StringBuilder();
									String statusName=HelpTools.GetOrderStatusName(statusType, LogStatus, statusTypeName);
									oslog.setStatusName(statusName);
									oslog.setStatusType(statusType);
									oslog.setStatusTypeName(statusTypeName.toString());
									oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
									orderStatusLogList.add(oslog);
									StringBuilder errorMessage = new StringBuilder();
									boolean nRet_s = HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorMessage);



								}


							}
							//圆通物流
							else if(ThirdpartConstants.yto_deliveryType.equals(deliveryType)){

								List<Map<String, Object>> outSalesetList=getQData_OutSaleset.stream().filter(g->(g.get("DELIVERYTYPE")==null?"":g.get("DELIVERYTYPE").toString()).equals(ThirdpartConstants.yto_deliveryType)).collect(Collectors.toList());
								if(outSalesetList==null||outSalesetList.size()==0){
									continue;
								}

                                //单号
                                String orderNo=map_order.get("ORDERNO").toString();
                                String orderShopNo=map_order.get("SHOP").toString();


                                String orderLoadDoctype=map_order.get("LOADDOCTYPE").toString();

								/***************查询下必传参数*********************/
								String sql_shippingShop = " select * from dcp_org where eid='"+eId+"' and organizationno='"+shippingId+"'";
								this.Log("自动发快递ExpressOrderCreate开始调用yto圆通物流,查询配送门店sql="+sql_shippingShop+",订单号orderNo="+orderNo);
								List<Map<String, Object>> getShippingShopData = this.doQueryData(sql_shippingShop, null);
								if(getShippingShopData==null||getShippingShopData.isEmpty())
								{
									continue;
								}


								/*****************根据配送门店ID，先查询指定门店对应的物流配置***********************************/
								//Map<String, Object> outSalesetMap=outSalesetList.get(0);
								Map<String, Object> outSalesetMap=this.getDeliverySetByShippingShop(eId,deliveryType,shippingId);
								if(outSalesetMap==null||outSalesetMap.isEmpty())
								{
                                    this.Log("ExpressOrderCreate定时任务。查询配送门店对应的物流设置参数为空，请检查物流参数设置，配送门店:"+shippingId+",物流类型:"+deliveryType);
                                    continue;
								}

								boolean isInvokeYTO = true;
								StringBuffer isInvokeDadaMess = new StringBuffer("");
								Map<String, Object> shippingShopInfo = getShippingShopData.get(0);
								String PROVINCE = shippingShopInfo.getOrDefault("PROVINCE", "").toString();//寄件人省名称
								String CITY = shippingShopInfo.getOrDefault("CITY", "").toString();//寄件人市名称
								String ADDRESS = shippingShopInfo.getOrDefault("ADDRESS", "").toString();//寄件人详细地址
								String PHONE = shippingShopInfo.getOrDefault("PHONE", "").toString();//寄件人联系电话

								String messStr = "配送门店"+shippingId+"的基本信息:";

								if(PROVINCE.isEmpty())
								{
									isInvokeYTO = false;
									isInvokeDadaMess.append("省份未维护,");
								}
								if(CITY.isEmpty())
								{
									isInvokeYTO = false;
									isInvokeDadaMess.append("城市未维护,");
								}
								if(ADDRESS.isEmpty())
								{
									isInvokeYTO = false;
									isInvokeDadaMess.append("详细地址未维护,");
								}
								if(PHONE.isEmpty())
								{
									isInvokeYTO = false;
									isInvokeDadaMess.append("联系电话未维护,");
								}

								if(isInvokeYTO==false)
								{
									messStr = messStr+isInvokeDadaMess.toString();
									isInvokeDadaMess = new StringBuffer("");
									isInvokeDadaMess.append(messStr);
								}

								/*******************判断下配送日期，是否需要调用***********************/
                                /*

								String pickupTime = "";
								String shipDate = map_order.get("SHIPDATE").toString();
								String sdtime = map_order.get("SHIPSTARTTIME").toString();//SHIPENDTIME
								sdtime = sdtime.replace("-", "");
                                if (sdtime.isEmpty())
                                {
                                    sdtime = new SimpleDateFormat("HHmmss").format(new Date());
                                }
								pickupTime = shipDate+ sdtime;// 日期格式如"20181223110438"使用yyyyMMddHHmmss

								// 1-72小时为预约单 超过72小时不能发 1小时之内不为预约单
								long longcur = System.currentTimeMillis();
								Date dateSta =new SimpleDateFormat("yyyyMMddHHmmss").parse(pickupTime);
								long longsta = dateSta.getTime();
								// 差别到分钟
								long diff = (longsta - longcur) / (1000 * 60);

								if(diff<0)
								{
									isInvokeYTO = false;
									isInvokeDadaMess.append(",订单的配送时间小于当前系统时间！");
								}
                               */

								ytoService yto=new ytoService();
								if(!isInvokeYTO)
								{
									//dada.Log("自动发快递ExpressOrderCreate开始调用dada物流,"+isInvokeDadaMess.toString()+"***eid="+eId+",LOADDOCTYPE="+orderLoadDoctype+",ORDERNO="+orderNo+"无需配送************");
									this.Log("自动发快递ExpressOrderCreate开始调用yto圆通物流,"+isInvokeDadaMess.toString()+",订单orderNo="+orderNo+"无需配送************");
									continue;

								}


								String sqlOrderDetail="select * from dcp_order_detail where (PACKAGETYPE='1' or PACKAGETYPE='2' or PACKAGETYPE is null) and  eid='"+eId+"' and orderno='"+orderNo+"' ";
								List<Map<String, Object>> getDetailDatas=this.doQueryData(sqlOrderDetail, null);

								//this.Log("***自动发快递ExpressOrderCreate开始调用yto圆通物流,eid="+eId+",LOADDOCTYPE="+orderLoadDoctype+",订单号orderNo="+orderNo+"************");
								if (getDetailDatas==null||getDetailDatas.size()==0){
									//没有商品明细不用上传
									this.Log("***自动发快递ExpressOrderCreate开始调用yto圆通物流,没有商品明细 跳过,订单ORDERNO="+orderNo+"************");
									continue;
								}

								String res=yto.generateKOrderCreate(outSalesetMap, map_order, shippingShopInfo, getDetailDatas);
								if(res ==null||res.isEmpty())
								{
									continue;
								}
								JSONObject json1 = JSONObject.parseObject(res);

								String code="";
								String logisticsNo = "";
								String deliveryNo = "";
								String shortAddress = "";
								if(json1.containsKey("logisticsNo"))
								{
									code = "0";
									logisticsNo = json1.get("logisticsNo").toString();
									if(json1.containsKey("mailNo"))
									{
										deliveryNo = json1.get("mailNo").toString();
									}
									if(json1.containsKey("shortAddress"))
									{
										shortAddress = json1.get("shortAddress").toString();
									}
								}


								if("0".equals(code))
								{

									//执行
									List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
									UptBean ubecOrder=new UptBean("dcp_order");
									ubecOrder.addCondition("EID", new DataValue(eId, Types.VARCHAR));
									ubecOrder.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
									if(!deliveryNo.isEmpty())
									{
										ubecOrder.addUpdateValue("DELIVERYNO", new DataValue(deliveryNo, Types.VARCHAR));
									}
									if(!shortAddress.isEmpty())
									{
										ubecOrder.addUpdateValue("SHORTADDRESS", new DataValue(shortAddress, Types.VARCHAR));
									}
									ubecOrder.addUpdateValue("DELIVERYSTATUS", new DataValue("0", Types.VARCHAR));//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 8货到物流中心 9消费者七天未取件
									ubecOrder.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                                    ubecOrder.addUpdateValue("DELIVERY_CREATETIME", new DataValue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), Types.DATE));//记录物流下单时间
									lstData.add(new DataProcessBean(ubecOrder));

									StaticInfo.dao.useTransactionProcessData(lstData);

                                    invokeCreateRes = true;
									//写订单日志
									List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
									String LogStatus="0";
									orderStatusLog oslog=new orderStatusLog();
									oslog.setCallback_status("N");
									oslog.setChannelId(channelId);
									oslog.setDisplay("1");
									oslog.seteId(eId);
									oslog.setLoadDocType(orderLoadDoctype);
									oslog.setMachShopName(machShopName);
									oslog.setMachShopNo(machShopId);
									oslog.setMemo("已上传物流");
									if (!deliveryNo.isEmpty())
									{
										oslog.setMemo("已上传物流<br>物流单号:"+deliveryNo);
										if(!shortAddress.isEmpty())
										{
											oslog.setMemo("已上传物流<br>物流单号:"+deliveryNo+"<br>三段码:"+shortAddress);
										}
									}
									oslog.setNeed_callback("N");
									oslog.setNeed_notify("N");
									oslog.setNotify_status("N");
									oslog.setOpName("定时任务");
									oslog.setOpNo("JOB");
									oslog.setOrderNo(orderNo);
									oslog.setShippingShopName(shippingName);
									oslog.setShippingShopNo(shippingId);
									oslog.setShopName(shopName);
									oslog.setShopNo(shopId);
									oslog.setStatus(LogStatus);
									//
									String statusType="2";
									StringBuilder statusTypeName=new StringBuilder();
									String statusName=HelpTools.GetOrderStatusName(statusType, LogStatus, statusTypeName);
									oslog.setStatusName(statusName);
									oslog.setStatusType(statusType);
									oslog.setStatusTypeName(statusTypeName.toString());
									oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
									orderStatusLogList.add(oslog);
									StringBuilder errorMessage = new StringBuilder();
									boolean nRet_s = HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorMessage);



								}
								else
                                {
                                    try
                                    {
                                        String reason = "";
                                        if(json1.containsKey("reason"))
                                        {
                                            reason = json1.get("reason").toString();
                                        }
                                        if (!reason.isEmpty())
                                        {
                                            //写订单日志
                                            List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
                                            String LogStatus="0";
                                            orderStatusLog oslog=new orderStatusLog();
                                            oslog.setCallback_status("N");
                                            oslog.setChannelId(channelId);
                                            oslog.setDisplay("1");
                                            oslog.seteId(eId);
                                            oslog.setLoadDocType(orderLoadDoctype);
                                            oslog.setMachShopName(machShopName);
                                            oslog.setMachShopNo(machShopId);
                                            oslog.setMemo(reason);

                                            oslog.setNeed_callback("N");
                                            oslog.setNeed_notify("N");
                                            oslog.setNotify_status("N");
                                            oslog.setOpName("定时任务");
                                            oslog.setOpNo("JOB");
                                            oslog.setOrderNo(orderNo);
                                            oslog.setShippingShopName(shippingName);
                                            oslog.setShippingShopNo(shippingId);
                                            oslog.setShopName(shopName);
                                            oslog.setShopNo(shopId);
                                            oslog.setStatus(LogStatus);
                                            //

                                            String statusType = "999";// 其他状态
                                            String updateStaus = "999";// 订单修改

                                            oslog.setStatusType(statusType);
                                            oslog.setStatus(updateStaus);

                                            String statusName = "物流下单失败";
                                            String statusTypeName = "呼叫物流";

                                            oslog.setStatusName(statusName);
                                            oslog.setStatusType(statusType);
                                            oslog.setStatusTypeName(statusTypeName.toString());
                                            oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
                                            orderStatusLogList.add(oslog);
                                            StringBuilder errorMessage = new StringBuilder();
                                            boolean nRet_s = HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorMessage);

                                        }

                                    }
                                    catch (Exception e)
                                    {

                                    }

                                }


							}
							//物流—商有云管家
							else if(ThirdpartConstants.sy_deliveryType.equals(deliveryType))
							{
                                Map<String, Object> outSalesetMap=this.getDeliverySetByShippingShop(eId,deliveryType,shippingId);
                                if(outSalesetMap==null||outSalesetMap.isEmpty())
                                {
                                    //this.Log("ExpressOrderCreate定时任务。查询配送门店对应的物流设置参数为空，请检查物流参数设置，配送门店:"+shippingId);
                                    this.Log("ExpressOrderCreate定时任务。查询配送门店对应的物流设置参数为空，请检查物流参数设置，配送门店:"+shippingId+",物流类型:"+deliveryType);
                                    continue;
                                }
								//List<Map<String, Object>> tempOutSaleset=MapDistinct.getWhereMap(getQData_OutSaleset, condi, false);
								if (outSalesetMap != null && outSalesetMap.isEmpty() == false)
								{
									String apiUrl=outSalesetMap.get("APIURL").toString();//http://steward-qa.syoo.cn 测试环境
									String authToken= outSalesetMap.get("APPSECRET").toString();
									String signKey=outSalesetMap.get("APPSIGNKEY").toString();
									//
									String order_companyno=eId;
									String order_shopno=map_order.get("SHOP").toString();
									String order_load_doctype=map_order.get("LOADDOCTYPE").toString();
									String order_no=map_order.get("ORDERNO").toString();
									String vip_code=map_order.get("GETMANTEL").toString();

									shangyou sy=new shangyou();
									//上商城订单+已接单状态，新建订单
									//目前只搞必传字段
									ShangyouOrder order=new ShangyouOrder();
									order.setOrderNo(order_no);//第三方订单id
									String daySeq=map_order.get("ORDER_SN").toString();

									if (order_load_doctype.equals("POS"))   daySeq="A"+daySeq;
									if (order_load_doctype.equals("POSANDROID"))   daySeq="B"+daySeq;
									if (order_load_doctype.equals("WECHAT"))   daySeq="C"+daySeq;
									if (order_load_doctype.equals("MINI"))   daySeq="D"+daySeq;
									if (order_load_doctype.equals("LINE"))   daySeq="E"+daySeq;
									if (order_load_doctype.equals("POSSELF"))   daySeq="F"+daySeq;
									if (order_load_doctype.equals("SCAN"))   daySeq="G"+daySeq;
									if (order_load_doctype.equals("PADGUIDE"))   daySeq="H"+daySeq;
									if (order_load_doctype.equals("WAIMAI"))   daySeq="I"+daySeq;
									if (order_load_doctype.equals("APPDISH"))   daySeq="J"+daySeq;
									if (order_load_doctype.equals("PADDISH"))   daySeq="K"+daySeq;
									if (order_load_doctype.equals("MEITUAN"))   daySeq="L"+daySeq;
									if (order_load_doctype.equals("ELEME"))   daySeq="M"+daySeq;
									if (order_load_doctype.equals("JDDJ"))   daySeq="N"+daySeq;
									if (order_load_doctype.equals("YOUZAN"))   daySeq="O"+daySeq;
									if (order_load_doctype.equals("GUANYIYUN"))   daySeq="P"+daySeq;
									if (order_load_doctype.equals("WUXIANG"))   daySeq="Q"+daySeq;
									if (order_load_doctype.equals("OFFICIAL"))   daySeq="R"+daySeq;
									if (order_load_doctype.equals("JDMALL"))   daySeq="S"+daySeq;
									if (order_load_doctype.equals("LETIAN"))   daySeq="T"+daySeq;
									if (order_load_doctype.equals("XIAPI"))   daySeq="U"+daySeq;
									if (order_load_doctype.equals("PCHOME"))   daySeq="V"+daySeq;
									if (order_load_doctype.equals("MOMO"))   daySeq="W"+daySeq;
									if (order_load_doctype.equals("91APP"))   daySeq="X"+daySeq;
									if (order_load_doctype.equals("YAHOO"))   daySeq="Y"+daySeq;
									if (order_load_doctype.equals("SELFDEFINE"))   daySeq="Z"+daySeq;

									order.setDaySeq(daySeq);//店铺当日订单流水,最大6位
									order.setMemberId(vip_code);//会员id

									/*******************判断下配送日期***********************/
                                    String pickupTime = "";
                                    String shipDate = map_order.get("SHIPDATE").toString();
                                    String sdtime = map_order.get("SHIPSTARTTIME").toString();//SHIPENDTIME

									//这帮吊人，无组织无纪律，瞎逼干，我擦你大爷的，受不了了
									if (Check.Null(shipDate)) shipDate=new SimpleDateFormat("yyyyMMdd").format(new Date());
									shipDate = shipDate.replace("-", "");
									shipDate = shipDate.replace("/", "");
									if (shipDate.length()!=8) shipDate=new SimpleDateFormat("yyyyMMdd").format(new Date());

									if (Check.Null(sdtime)) sdtime=new SimpleDateFormat("HHmmss").format(new Date());
									sdtime = sdtime.replace("-", "");
									sdtime = sdtime.replace(":", "");
									if (sdtime.length()!=6) sdtime=new SimpleDateFormat("HHmmss").format(new Date());

									pickupTime = shipDate+ sdtime;// 日期格式如"20181223110438"使用yyyyMMddHHmmss

									// 1-72小时为预约单 超过72小时不能发 1小时之内不为预约单
									long longcur = System.currentTimeMillis();
									Date dateSta =new SimpleDateFormat("yyyyMMddHHmmss").parse(pickupTime);
									long longsta = dateSta.getTime();
									// 差别到分钟
									long diff = (longsta - longcur) / (1000 * 60);
									//if(diff>72*60 || diff<0)
									if(diff<-30)
									{
										//配送时间(SHIPDATE+SHIPSTARTTIME)小于当前时间30分钟或大于当前时间72小时的跳过
										logger.info("\r\n*********自動發快遞ExpressOrderCreate eid="+order_companyno+",SHOP="+order_shopno+",LOADDOCTYPE="+order_load_doctype+",ORDERNO="+order_no+"配送时间(SHIPDATE+SHIPSTARTTIME)小于当前时间30分钟或大于当前时间72小时的跳过************\r\n");
										continue;
									}
									//1小时之内不为预约单
									order.setSalesType(diff>60?1:2);//1。预约单 2。现售单
									if (order.getSalesType() == 1)
									{
										order.setDeliveryTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Convert.toDate(pickupTime)));//预计送达时间（预约单必传）
									}
									//配送方式 1.外卖平台配送 ,2.配送 ,3.顾客自提 ,5总部配送
									if (map_order.get("SHIPTYPE").toString().equals("3"))
									{
										order.setDeliveryType(2);//1.外送 2.自提
									}
									else
									{
										order.setDeliveryType(1);//1.外送 2.自提
									}
									if (order.getDeliveryType() == 2)
									{
										/***************查询下必传参数*********************/
										String sql_shippingShop = " select * from dcp_org where eid='"+eId+"' and organizationno='"+shippingId+"'";
										List<Map<String, Object>> getShippingShopData = this.doQueryData(sql_shippingShop, null);
										if(getShippingShopData==null||getShippingShopData.isEmpty())
										{
											//查询配送门店对应的组织表没数据
											logger.info("\r\n*********自動發快遞ExpressOrderCreate eid="+order_companyno+",SHOP="+order_shopno+",LOADDOCTYPE="+order_load_doctype+",ORDERNO="+order_no+"查询配送门店对应的组织表没数据跳过************\r\n");
											continue;
										}
										order.setPickupAddress(getShippingShopData.get(0).get("ADDRESS").toString());//自提点地址
										order.setPickupTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Convert.toDate(pickupTime)));//用户自提时间
									}
									order.setPayType(1);//1.在线 2.货到付款
									order.setPayStatus(2);//1.未支付 2.已支付
									order.setRecipientPhone(map_order.get("GETMANTEL").toString());//收货人电话
									order.setCustomerName(map_order.get("GETMAN").toString());//收货人名称
									order.setOrderStartTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Convert.toDate(map_order.get("CREATE_DATETIME").toString())));//下单时间

									//优惠相关--这个是支付信息，不是折扣，先不給吧
									order.setActivityTotal(new BigDecimal(0));//活动优惠总价(订单维度 元)
									order.setOrderActivities(new ArrayList<>());
									//ShangyouOrder.Activities activitie=new ShangyouOrder.Activities();
									//activitie.setRemark("折扣1");
									//activitie.setPrice("1");
									//order.getOrderActivities().add(activitie);
									//已接单
									order.setStatus(3);
									order.setDescription(memo);
									order.setAddress(map_order.get("ADDRESS").toString());//送餐地址
									//order.setDeliverFee(new BigDecimal(0));//配送费 元
									//order.setMerchantDeliverySubsidy(new BigDecimal(0));//商家承担配送费 元
									//order.setVipDeliveryFeeDiscount(new BigDecimal(0));//会员减免运费 元
									//order.setMerchantPhone("13120511712");//商家电话
									//order.setBoxNum(1);//餐盒数量
									order.setTotalPrice(new BigDecimal(map_order.get("TOT_AMT").toString()));//订单总价(实付 元)
									order.setOriginalPrice(new BigDecimal(map_order.get("TOT_OLDAMT").toString()));//订单原价 元
									order.setFoodNum(Integer.parseInt(map_order.get("TOT_QTY").toString()));//sku数量
									String mealNumber=map_order.get("MEALNUMBER")==null?"1":map_order.get("MEALNUMBER").toString();
									order.setDinnersNumber(Integer.parseInt(mealNumber));//用餐人数
									order.setOrderReceiveTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Convert.toDate(map_order.get("CREATE_DATETIME").toString())));//商户收到时间
									order.setOrderConfirmTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Convert.toDate(map_order.get("CREATE_DATETIME").toString())));//商户接单时间
									String packageFee=map_order.get("PACKAGEFEE")==null?"0":map_order.get("PACKAGEFEE").toString();
									order.setPackageFee(new BigDecimal(packageFee));//餐盒费(总价 元)
									order.setLongitude(map_order.get("LONGITUDE").toString());//收货地址经度
									order.setLatitude(map_order.get("LATITUDE").toString());//收货地址纬度
									order.setLalType(1);//经纬度类型(默认) 1为高德(GCJ02) 2 百度(BD09) 3 WGS84

									String sqlOrderDetail="select * from dcp_order_detail where eid='"+eId+"' and orderno='"+order_no+"' ";
									List<Map<String, Object>> getDetailDatas=this.doQueryData(sqlOrderDetail, null);

									if (getDetailDatas==null ||getDetailDatas.size()==0)
									{
										//没有商品明细不用上传
										logger.info("\r\n*********自動發快遞ExpressOrderCreate eid="+order_companyno+",SHOP="+order_shopno+",LOADDOCTYPE="+order_load_doctype+",ORDERNO="+order_no+"没有商品明细跳过************\r\n");
										continue;//
									}
									else
									{
										//商品明细
										order.setFoodDtoList(new ArrayList<>());
										//过滤掉子商品
										List<Map<String, Object>> tempDetals=getDetailDatas.stream().filter(p->p.get("PACKAGETYPE").toString().equals("1") || p.get("PACKAGETYPE").toString().equals("2") ).collect(Collectors.toList());
										for (Map<String, Object> oneData2 : tempDetals)
										{
											ShangyouOrder.Food food=new ShangyouOrder.Food();
											food.setFoodId(Long.parseLong(oneData2.get("PLUNO").toString()));//第三方商品id
											food.setName(oneData2.get("PLUNAME").toString());
											food.setQuantity(Integer.parseInt(oneData2.get("QTY").toString()));//商品数量
											food.setUnit(oneData2.get("SUNIT").toString());//单位
											food.setPrice(new BigDecimal(oneData2.get("OLDPRICE").toString()));//商品价格（单价 元）
											food.setUserPrice(new BigDecimal(oneData2.get("PRICE").toString()));//用户侧价格（商品总价,去掉商品活动之后的总价 元
											food.setShopPrice(new BigDecimal(oneData2.get("PRICE").toString()));//商户侧价格（商品总价 元）
											String boxNum=oneData2.get("BOXNUM")==null?"0":oneData2.get("BOXNUM").toString();
											food.setBoxNum(Integer.parseInt(boxNum));//餐盒数量
											String boxPrice=oneData2.get("BOXPRICE")==null?"0":oneData2.get("BOXPRICE").toString();
											food.setBoxPrice(new BigDecimal(boxPrice));//餐盒价格 元
											String goodType=oneData2.get("PACKAGETYPE").toString().equals("1")?"1":"2";
											food.setGoodType(Integer.parseInt(goodType));//1.单品 2套餐 3 加菜
											//套餐子商品
											food.setChildFoodList(new ArrayList<>());
											if (food.getGoodType()==2)
											{
												List<Map<String, Object>> tempSubDetals=getDetailDatas.stream().filter(p->p.get("PACKAGEMITEM").toString().equals(oneData2.get("ITEM").toString())).collect(Collectors.toList());
												for (Map<String, Object> tempSubDetal : tempSubDetals)
												{
													ShangyouOrder.Food food1=new ShangyouOrder.Food();
													food1.setFoodId(Long.parseLong(tempSubDetal.get("PLUNO").toString()));//第三方商品id
													food1.setName(tempSubDetal.get("PLUNAME").toString());
													food1.setQuantity(Integer.parseInt(tempSubDetal.get("QTY").toString()));//商品数量
													food1.setUnit(tempSubDetal.get("SUNIT").toString());//单位
													food1.setPrice(new BigDecimal(tempSubDetal.get("OLDPRICE").toString()));//商品价格（单价 元）
													food1.setUserPrice(new BigDecimal(tempSubDetal.get("PRICE").toString()));//用户侧价格（商品总价,去掉商品活动之后的总价 元
													food1.setShopPrice(new BigDecimal(tempSubDetal.get("PRICE").toString()));//商户侧价格（商品总价 元）
													String sboxNum=tempSubDetal.get("BOXNUM")==null?"0":tempSubDetal.get("BOXNUM").toString();
													food1.setBoxNum(Integer.parseInt(sboxNum));//餐盒数量
													String sboxPrice=tempSubDetal.get("BOXPRICE")==null?"0":tempSubDetal.get("BOXPRICE").toString();
													food1.setBoxPrice(new BigDecimal(sboxPrice));//餐盒价格 元
													food1.setGoodType(1);//1.单品 2套餐 3 加菜
													food.getChildFoodList().add(food1);
												}
											}
											order.getFoodDtoList().add(food);
										}
									}

									//新建订单
                                    if (shippingId!=null&&!shippingId.trim().isEmpty())
                                    {
                                        order_shopno = shippingId;
                                    }
									String resbody=sy.saveNewOrder(apiUrl,authToken,signKey,Long.parseLong(order_shopno),order);
									JSONObject resjsobject= JSONObject.parseObject(resbody);
									//
									String errorCode=resjsobject.containsKey("errorCode")?resjsobject.getString("errorCode"):"";//错误代码
									String errorDesc=resjsobject.containsKey("errorMsg")?resjsobject.getString("errorMsg"):"";//错误原因
									String syOrderno=resjsobject.containsKey("data")?resjsobject.getString("data"):"";//商有订单号
									//成功000
									if (errorCode.equals("000"))
									{
										//执行
										List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
										UptBean ubecOrder=new UptBean("dcp_order");
										ubecOrder.addCondition("EID", new DataValue(order_companyno, Types.VARCHAR));
										ubecOrder.addCondition("ORDERNO", new DataValue(order_no, Types.VARCHAR));

										ubecOrder.addUpdateValue("OUTDOCORDERNO", new DataValue(syOrderno, Types.VARCHAR));//
										ubecOrder.addUpdateValue("DELIVERYSTATUS", new DataValue("-1", Types.VARCHAR));//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 8货到物流中心 9消费者七天未取件
                                        ubecOrder.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                                        ubecOrder.addUpdateValue("DELIVERY_CREATETIME", new DataValue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), Types.DATE));//记录物流下单时间
                                        lstData.add(new DataProcessBean(ubecOrder));

										//写订单日志
										String LogStatus="-1";
										orderStatusLog oslog=new orderStatusLog();
										oslog.setCallback_status("N");
										oslog.setChannelId(channelId);
										oslog.setDisplay("1");
										oslog.seteId(eId);
										oslog.setLoadDocBillType(order_load_doctype);
										oslog.setLoadDocOrderNo(order_no);
										oslog.setLoadDocType(order_load_doctype);
										oslog.setMachShopName(machShopName);
										oslog.setMachShopNo(machShopId);
										oslog.setMemo("已上传物流");
										oslog.setNeed_callback("N");
										oslog.setNeed_notify("N");
										oslog.setNotify_status("N");
										oslog.setOpName("admin");
										oslog.setOpNo("admin");
										oslog.setOrderNo(order_no);
										oslog.setShippingShopName(shippingName);
										oslog.setShippingShopNo(shippingId);
										oslog.setShopName(shopName);
										oslog.setShopNo(shopId);
										oslog.setStatus(LogStatus);
										//
										String statusType="2";
										StringBuilder statusTypeName=new StringBuilder();
										String statusName=HelpTools.GetOrderStatusName(statusType, LogStatus, statusTypeName);
										oslog.setStatusName(statusName);
										oslog.setStatusType(statusType);
										oslog.setStatusTypeName(statusTypeName.toString());
										oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
										InsBean	ib_DCP_ORDER_STATUSLOG=HelpTools.InsertOrderStatusLog(oslog);
										lstData.add(new DataProcessBean(ib_DCP_ORDER_STATUSLOG));


										StaticInfo.dao.useTransactionProcessData(lstData);

                                        invokeCreateRes = true;
										//同步发货状态至有赞
										YouZanCallBackServiceV3.getInstance().sendLogistics(order_companyno, order_no, null, map_order, null);

										logger.info("\r\n***************自動發快遞ExpressOrderCreate 订单编号:"+order_no+"发单成功,商有云管家返回单号信息****************\r\n");
									}
									else
									{
										logger.info("\r\n*********自動發快遞ExpressOrderCreate eid="+order_companyno+",SHOP="+order_shopno+",LOADDOCTYPE="+order_load_doctype+",ORDERNO="+order_no+"调用商有云管家接口地址"+apiUrl+"返回错误：errorCode="+errorCode+",errorDesc="+errorDesc+"************\r\n");
									}
								}
								else
								{
									logger.info("\r\n*********自動發快遞ExpressOrderCreate物流廠商接口表dcp_outsaleset商有云管家物流未設置資料:************\r\n");

									continue;
								}

							}
							//顺丰同城，
                            else if(ThirdpartConstants.sftc_deliveryType.equals(deliveryType))
                            {
                                List<Map<String, Object>> outSalesetList=getQData_OutSaleset.stream().filter(g->(g.get("DELIVERYTYPE")==null?"":g.get("DELIVERYTYPE").toString()).equals(ThirdpartConstants.sftc_deliveryType)).collect(Collectors.toList());
                                if(outSalesetList==null||outSalesetList.size()==0){
                                    continue;
                                }

                                //Map<String, Object> outSalesetMap=outSalesetList.get(0);
                                Map<String, Object> outSalesetMap=this.getDeliverySetByShippingShop(eId,deliveryType,shippingId);
                                if(outSalesetMap==null||outSalesetMap.isEmpty())
                                {
                                    this.Log("ExpressOrderCreate定时任务。查询配送门店对应的物流设置参数为空，请检查物流参数设置，配送门店:"+shippingId+",物流类型:"+deliveryType);
                                    continue;
                                }

                                //单号
                                String orderNo=map_order.get("ORDERNO").toString();
                                String orderShopNo=map_order.get("SHOP").toString();


                                String orderLoadDoctype=map_order.get("LOADDOCTYPE").toString();

                                /***************除了饿了么 其他不需要 查询下必传参数*********************/
                                String sql_shippingShop = " select * from dcp_org where eid='"+eId+"' and organizationno='"+shippingId+"'";
                                this.Log("自动发快递ExpressOrderCreate开始调用sftc顺丰同城物流,查询配送门店sql="+sql_shippingShop+",订单号orderNo="+orderNo);
                                List<Map<String, Object>> getShippingShopData = this.doQueryData(sql_shippingShop, null);
                                Map<String, Object> shippingShopInfo = new HashMap<>();
                                if(getShippingShopData==null||getShippingShopData.isEmpty())
                                {
                                    //continue;
                                }
                                else
                                {
                                    shippingShopInfo = getShippingShopData.get(0);
                                }

                                String sqlOrderDetail="select * from dcp_order_detail where eid='"+eId+"' and orderno='"+orderNo+"' ";
                                List<Map<String, Object>> getDetailDatas=this.doQueryData(sqlOrderDetail, null);

                                //this.Log("***自动发快递ExpressOrderCreate开始调用yto圆通物流,eid="+eId+",LOADDOCTYPE="+orderLoadDoctype+",订单号orderNo="+orderNo+"************");
                                if (getDetailDatas==null||getDetailDatas.size()==0){
                                    //没有商品明细不用上传
                                    this.Log("***自动发快递ExpressOrderCreate开始调用sftc顺丰同城物流,没有商品明细 跳过,订单ORDERNO="+orderNo+"************");
                                    continue;
                                }
                                sftcService sftc = new sftcService();

                                String res = sftc.sftcOrderCreate(outSalesetMap, map_order, shippingShopInfo, getDetailDatas);
                                if(res ==null||res.isEmpty())
                                {
                                    continue;
                                }
								JSONObject json1 = JSONObject.parseObject(res);
								String code=json1.get("error_code").toString();
								if ("0".equals(code))
								{
									String deliveryNo = "";
									String sf_order_id = "";//顺丰订单号
									String sf_bill_id = "";//顺丰运单号（需要设置）
									String shop_order_id = "";//商家订单号
									JSONObject resultmsg = json1.getJSONObject("result");
									if (resultmsg.containsKey("sf_order_id"))
									{
										sf_order_id = resultmsg.get("sf_order_id").toString();//顺丰订单号(标准默认为int，可以设置为string)
									}
									if (resultmsg.containsKey("sf_bill_id"))
									{
										sf_bill_id = resultmsg.get("sf_bill_id").toString();//顺丰运单号（需要设置）
									}
									if (resultmsg.containsKey("shop_order_id"))
									{
										shop_order_id = resultmsg.get("shop_order_id").toString();
									}
									deliveryNo = sf_order_id;
									//执行
									List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
									UptBean ubecOrder=new UptBean("dcp_order");
									ubecOrder.addCondition("EID", new DataValue(eId, Types.VARCHAR));
									ubecOrder.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
									if(!deliveryNo.isEmpty())
									{
										ubecOrder.addUpdateValue("DELIVERYNO", new DataValue(deliveryNo, Types.VARCHAR));
									}

									ubecOrder.addUpdateValue("DELIVERYSTATUS", new DataValue("0", Types.VARCHAR));//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 8货到物流中心 9消费者七天未取件
									ubecOrder.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                                    ubecOrder.addUpdateValue("DELIVERY_CREATETIME", new DataValue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), Types.DATE));//记录物流下单时间
									lstData.add(new DataProcessBean(ubecOrder));

									StaticInfo.dao.useTransactionProcessData(lstData);

                                    invokeCreateRes = true;
									//写订单日志
									List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
									String LogStatus="0";
									orderStatusLog oslog=new orderStatusLog();
									oslog.setCallback_status("N");
									oslog.setChannelId(channelId);
									oslog.setDisplay("1");
									oslog.seteId(eId);
									oslog.setLoadDocType(orderLoadDoctype);
									oslog.setMachShopName(machShopName);
									oslog.setMachShopNo(machShopId);
									oslog.setMemo("已上传物流");
									if (!deliveryNo.isEmpty())
									{
										oslog.setMemo("已上传物流-->顺丰同城<br>物流单号:"+deliveryNo);
									}
									oslog.setNeed_callback("N");
									oslog.setNeed_notify("N");
									oslog.setNotify_status("N");
									oslog.setOpName("定时任务");
									oslog.setOpNo("JOB");
									oslog.setOrderNo(orderNo);
									oslog.setShippingShopName(shippingName);
									oslog.setShippingShopNo(shippingId);
									oslog.setShopName(shopName);
									oslog.setShopNo(shopId);
									oslog.setStatus(LogStatus);
									//
									String statusType="2";
									StringBuilder statusTypeName=new StringBuilder();
									String statusName=HelpTools.GetOrderStatusName(statusType, LogStatus, statusTypeName);
									oslog.setStatusName(statusName);
									oslog.setStatusType(statusType);
									oslog.setStatusTypeName(statusTypeName.toString());
									oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
									orderStatusLogList.add(oslog);
									StringBuilder errorMessage = new StringBuilder();
									boolean nRet_s = HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorMessage);
								}
								else
								{

									String error_msg = "";
									if (json1.containsKey("error_msg"))
									{
										error_msg = json1.get("error_msg").toString();
									}
									//写订单日志
									List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
									String LogStatus="0";
									orderStatusLog oslog=new orderStatusLog();
									oslog.setCallback_status("N");
									oslog.setChannelId(channelId);
									oslog.setDisplay("1");
									oslog.seteId(eId);
									oslog.setLoadDocType(orderLoadDoctype);
									oslog.setMachShopName(machShopName);
									oslog.setMachShopNo(machShopId);
									oslog.setMemo(error_msg);

									oslog.setNeed_callback("N");
									oslog.setNeed_notify("N");
									oslog.setNotify_status("N");
									oslog.setOpName("定时任务");
									oslog.setOpNo("JOB");
									oslog.setOrderNo(orderNo);
									oslog.setShippingShopName(shippingName);
									oslog.setShippingShopNo(shippingId);
									oslog.setShopName(shopName);
									oslog.setShopNo(shopId);
									oslog.setStatus(LogStatus);
									//

									String statusType = "999";// 其他状态
									String updateStaus = "999";// 订单修改

									oslog.setStatusType(statusType);
									oslog.setStatus(updateStaus);

									String statusName = "物流下单失败";
									String statusTypeName = "呼叫物流";
									if ("fffff".equals(code))//不用发物流，模拟顺丰同城返回
									{
										 statusName = "其他";
										 statusTypeName = "其他状态";
									}

									oslog.setStatusName(statusName);
									oslog.setStatusType(statusType);
									oslog.setStatusTypeName(statusTypeName.toString());
									oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
									orderStatusLogList.add(oslog);
									StringBuilder errorMessage = new StringBuilder();
									boolean nRet_s = HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorMessage);
								}

                            }
                            // 闪送跑腿
                            else if(ThirdpartConstants.ss_deliveryType.equals(deliveryType)){
                                List<Map<String, Object>> outSalesetList=getQData_OutSaleset.stream().filter(g->(g.get("DELIVERYTYPE")==null?"":g.get("DELIVERYTYPE").toString()).equals(ThirdpartConstants.ss_deliveryType)).collect(Collectors.toList());
                                if(outSalesetList==null||outSalesetList.size()==0){
                                    continue;
                                }
                                //Map<String, Object> outSalesetMap=outSalesetList.get(0);
                                Map<String, Object> outSalesetMap=this.getDeliverySetByShippingShop(eId,deliveryType,shippingId);
                                if(outSalesetMap==null||outSalesetMap.isEmpty())
                                {
                                    this.Log("ExpressOrderCreate定时任务。查询配送门店对应的物流设置参数为空，请检查物流参数设置，配送门店:"+shippingId+",物流类型:"+deliveryType);
                                    continue;
                                }
                                //单号
                                String orderNo=map_order.get("ORDERNO").toString();
                                String orderShopNo=map_order.get("SHOP").toString();

                                String orderLoadDoctype=map_order.get("LOADDOCTYPE").toString();
								/***************查询下门店映射闪送物流门店ID有无配置************/
								String sql_mapShop = " select * from DCP_OUTSALESET_MAPSHOP where eid = '"+eId+"' and SHOPID = '"+shippingId+"'";
								this.Log("自动发快递ExpressOrderCreate开始调用闪送物流,查询门店映射关系sql="+sql_mapShop+",订单号orderNo="+orderNo);
								List<Map<String, Object>> getMapShopData = this.doQueryData(sql_mapShop, null);
								if(getMapShopData==null||getMapShopData.isEmpty())
								{
									//没有配置映射关系不用上传
									this.Log("***自动发快递ExpressOrderCreate开始调用闪送物流,门店"+shippingId+"没有配置闪送映射门店 跳过,订单ORDERNO="+orderNo+"************");
									continue;
								}
								else
								{
									String deliveryShopId = getMapShopData.get(0).get("DELIVERYSHOPID").toString(); // 闪送物流门店ID
									outSalesetMap.put("DELIVERYSHOPID",deliveryShopId);

								}
                                /*******************判断下配送日期***********************/
                                String pickupTime = "";
                                String shipDate = map_order.get("SHIPDATE").toString();
                                String sdtime = map_order.get("SHIPSTARTTIME").toString();//SHIPENDTIME

                                if (Check.Null(shipDate)) shipDate=new SimpleDateFormat("yyyyMMdd").format(new Date());
                                shipDate = shipDate.replace("-", "");
                                shipDate = shipDate.replace("/", "");
                                if (shipDate.length()!=8) shipDate=new SimpleDateFormat("yyyyMMdd").format(new Date());

                                if (Check.Null(sdtime)) sdtime=new SimpleDateFormat("HHmmss").format(new Date());
                                sdtime = sdtime.replace("-", "");
                                sdtime = sdtime.replace(":", "");
                                if (sdtime.length()!=6) sdtime=new SimpleDateFormat("HHmmss").format(new Date());

                                pickupTime = shipDate+ sdtime;// 日期格式如"20181223110438"使用yyyyMMddHHmmss

                                // 2-48小时为预约单 超过48小时不能发 2小时之内不为预约单
                                long longcur = System.currentTimeMillis();
                                Date dateSta =new SimpleDateFormat("yyyyMMddHHmmss").parse(pickupTime);
                                long longsta = dateSta.getTime();

                                // 差别到分钟
                                long diff = (longsta - longcur) / (1000 * 60);
                                if (diff>2880) {
                                    this.Log("***自动发快递ExpressOrderCreate开始调用闪送物流 订单ORDERNO="+orderNo+" 配送时间(SHIPDATE+SHIPSTARTTIME)大于当前时间48小时的跳过************");
                                    continue;
                                }

								/*// 2小时内的订单，预留40分钟再推送  10点的订单，9点20再推送
								if((diff>40&&diff<120)){
									this.Log("***自动发快递ExpressOrderCreate开始调用闪送物流 订单ORDERNO="+orderNo+" 配送时间(SHIPDATE+SHIPSTARTTIME)2小时内的订单超过配送时间40分钟的跳过************");
									continue;
								}*/

                                String appointType = "0";
                                if(diff<=120){
                                    // 2小时以内为立即单
                                    appointType = "0";
                                }else if(diff >= 120&&diff<=2880){
                                    //  2-48小时为预约单
                                    appointType = "1";
                                }

                                /***************查询下必传参数*********************/
                                String sql_shippingShop = " select * from dcp_org where eid='"+eId+"' and organizationno='"+shippingId+"'";
                                this.Log("自动发快递ExpressOrderCreate开始调用闪送物流,查询配送门店sql="+sql_shippingShop+",订单号orderNo="+orderNo);
                                List<Map<String, Object>> getShippingShopData = this.doQueryData(sql_shippingShop, null);
                                Map<String, Object> shippingShopInfo = new HashMap<>();
                                if(getShippingShopData==null||getShippingShopData.isEmpty())
                                {
                                    //continue;
                                }
                                else
                                {
                                    shippingShopInfo = getShippingShopData.get(0);
                                }

                                String sqlOrderDetail="select * from dcp_order_detail where eid='"+eId+"' and orderno='"+orderNo+"' ";
                                List<Map<String, Object>> getDetailDatas=this.doQueryData(sqlOrderDetail, null);

                                //this.Log("***自动发快递ExpressOrderCreate开始调用yto圆通物流,eid="+eId+",LOADDOCTYPE="+orderLoadDoctype+",订单号orderNo="+orderNo+"************");
                                if (getDetailDatas==null||getDetailDatas.size()==0){
                                    //没有商品明细不用上传
                                    this.Log("***自动发快递ExpressOrderCreate开始调用闪送物流,没有商品明细 跳过,订单ORDERNO="+orderNo+"************");
                                    continue;
                                }
                                SHANSONGService shansongService = new SHANSONGService();
                                String res =  shansongService.ssOrderCreate(outSalesetMap,map_order,shippingShopInfo);
                                if(res ==null||res.isEmpty())
                                {
                                    continue;
                                }
                                JSONObject json1 = JSONObject.parseObject(res);
                                String deliveryNo = "";
                                String code = "";
                                if(json1.containsKey("status"))
                                {
                                    String status = json1.getString("status").toString();
                                    if("200".equals(status)){
                                        code = "0";
                                        JSONObject data_res = json1.getJSONObject("data");
                                        if (data_res.containsKey("orderNumber")) {
                                            deliveryNo = data_res.get("orderNumber").toString();
                                        }
                                    }
                                    else
                                    {
                                        code = status;
                                    }
                                }

                                if("0".equals(code))
                                {
//执行
                                    List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
                                    UptBean ubecOrder=new UptBean("dcp_order");
                                    ubecOrder.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                    ubecOrder.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
                                    if(!deliveryNo.isEmpty())
                                    {
                                        ubecOrder.addUpdateValue("DELIVERYNO", new DataValue(deliveryNo, Types.VARCHAR));
                                    }
                                    if("0".equals(appointType)){
                                        ubecOrder.addUpdateValue("DELIVERYSTATUS", new DataValue("0", Types.VARCHAR));//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 8货到物流中心 9消费者七天未取件
                                    }else if("1".equals(appointType)){
                                        ubecOrder.addUpdateValue("DELIVERYSTATUS", new DataValue("-1", Types.VARCHAR));//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 8货到物流中心 9消费者七天未取件
                                    }
                                    ubecOrder.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                                    ubecOrder.addUpdateValue("DELIVERY_CREATETIME", new DataValue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), Types.DATE));//记录物流下单时间
                                    lstData.add(new DataProcessBean(ubecOrder));

                                    StaticInfo.dao.useTransactionProcessData(lstData);

                                    invokeCreateRes = true;
                                    //写订单日志
                                    List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
                                    String LogStatus="0";
                                    orderStatusLog oslog=new orderStatusLog();
                                    oslog.setCallback_status("N");
                                    oslog.setChannelId(channelId);
                                    oslog.setDisplay("1");
                                    oslog.seteId(eId);
                                    oslog.setLoadDocType(orderLoadDoctype);
                                    oslog.setMachShopName(machShopName);
                                    oslog.setMachShopNo(machShopId);
                                    oslog.setMemo("已上传物流");
                                    if (!deliveryNo.isEmpty())
                                    {
                                        oslog.setMemo("已上传物流<br>物流单号:"+deliveryNo);
                                    }
                                    oslog.setNeed_callback("N");
                                    oslog.setNeed_notify("N");
                                    oslog.setNotify_status("N");
                                    oslog.setOpName("定时任务");
                                    oslog.setOpNo("JOB");
                                    oslog.setOrderNo(orderNo);
                                    oslog.setShippingShopName(shippingName);
                                    oslog.setShippingShopNo(shippingId);
                                    oslog.setShopName(shopName);
                                    oslog.setShopNo(shopId);
                                    oslog.setStatus(LogStatus);
                                    //
                                    String statusType="2";
                                    StringBuilder statusTypeName=new StringBuilder();
                                    String statusName=HelpTools.GetOrderStatusName(statusType, LogStatus, statusTypeName);
                                    oslog.setStatusName(statusName);
                                    oslog.setStatusType(statusType);
                                    oslog.setStatusTypeName(statusTypeName.toString());
                                    oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
                                    orderStatusLogList.add(oslog);
                                    StringBuilder errorMessage = new StringBuilder();
                                    boolean nRet_s = HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorMessage);
                                }else {
                                    try {
                                        String msg = "";
                                        if(json1.containsKey("msg"))
                                        {
                                            msg = json1.get("msg").toString();
                                        }

                                        if (!msg.isEmpty())
                                        {
                                            //写订单日志
                                            List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
                                            String LogStatus="0";
                                            orderStatusLog oslog=new orderStatusLog();
                                            oslog.setCallback_status("N");
                                            oslog.setChannelId(channelId);
                                            oslog.setDisplay("1");
                                            oslog.seteId(eId);
                                            oslog.setLoadDocType(orderLoadDoctype);
                                            oslog.setMachShopName(machShopName);
                                            oslog.setMachShopNo(machShopId);
                                            oslog.setMemo(msg);

                                            oslog.setNeed_callback("N");
                                            oslog.setNeed_notify("N");
                                            oslog.setNotify_status("N");
                                            oslog.setOpName("定时任务");
                                            oslog.setOpNo("JOB");
                                            oslog.setOrderNo(orderNo);
                                            oslog.setShippingShopName(shippingName);
                                            oslog.setShippingShopNo(shippingId);
                                            oslog.setShopName(shopName);
                                            oslog.setShopNo(shopId);
                                            oslog.setStatus(LogStatus);
                                            //

                                            String statusType = "999";// 其他状态
                                            String updateStaus = "999";// 订单修改

                                            oslog.setStatusType(statusType);
                                            oslog.setStatus(updateStaus);

                                            String statusName = "物流下单失败";
                                            String statusTypeName = "呼叫物流";
                                            if ("fffff".equals(code))//不用发物流，模拟顺丰同城返回
                                            {
                                                statusName = "其他";
                                                statusTypeName = "其他状态";
                                            }

                                            oslog.setStatusName(statusName);
                                            oslog.setStatusType(statusType);
                                            oslog.setStatusTypeName(statusTypeName.toString());
                                            oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
                                            orderStatusLogList.add(oslog);
                                            StringBuilder errorMessage = new StringBuilder();
                                            boolean nRet_s = HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorMessage);
                                        }
                                    } catch (Exception e) {
                                    }
                                }


                            }
                            //快递鸟聚合物流
                            else if(ThirdpartConstants.kdn_deliveryType.equals(deliveryType)){

                                List<Map<String, Object>> outSalesetList=getQData_OutSaleset.stream().filter(g->(g.get("DELIVERYTYPE")==null?"":g.get("DELIVERYTYPE").toString()).equals(ThirdpartConstants.kdn_deliveryType)).collect(Collectors.toList());
                                if(outSalesetList==null||outSalesetList.size()==0){
                                    continue;
                                }

                                //单号
                                String orderNo=map_order.get("ORDERNO").toString();
                                String orderShopNo=map_order.get("SHOP").toString();


                                String orderLoadDoctype=map_order.get("LOADDOCTYPE").toString();

								String shipType = map_order.get("SHIPTYPE").toString();//配送方式( 1.订单来源渠道配送 2.全国快递 3.顾客自提 5ERP总部配送 6同城配送)
								String shipMode = "";//kdn 物流产品类型 0全国快递，1同城快递
								if ("2".equals(shipType))
								{
									shipMode = "0";
								}
								else if ("6".equals(shipType))
								{
									shipMode = "1";
									//this.Log("自动发快递ExpressOrderCreate开始调用kdn快递鸟物流,配送方式shipType="+shipType+"(同城快递)还未实现,订单号orderNo="+orderNo);
									//continue;
								}
                                else
								{
									continue;
								}

                                /***************查询下必传参数*********************/
                                String sql_shippingShop = " select * from dcp_org where eid='"+eId+"' and organizationno='"+shippingId+"'";
                                this.Log("自动发快递ExpressOrderCreate开始调用kdn快递鸟物流,查询配送门店sql="+sql_shippingShop+",订单号orderNo="+orderNo);
                                List<Map<String, Object>> getShippingShopData = this.doQueryData(sql_shippingShop, null);
                                if(getShippingShopData==null||getShippingShopData.isEmpty())
                                {
                                    continue;
                                }


                                /*****************根据配送门店ID，先查询指定门店对应的物流配置***********************************/
                                //Map<String, Object> outSalesetMap=outSalesetList.get(0);
                                Map<String, Object> outSalesetMap=this.getKDNDeliverySetByShippingShop(eId,deliveryType,shippingId,shipMode);
                                if(outSalesetMap==null||outSalesetMap.isEmpty())
                                {
                                    this.Log("ExpressOrderCreate定时任务。查询配送门店对应的物流设置参数为空，请检查物流参数设置，配送门店:"+shippingId+",物流类型:"+deliveryType);
                                    continue;
                                }


                                boolean isInvokeKDN = true;
                                StringBuffer isInvokeDadaMess = new StringBuffer("");
                                Map<String, Object> shippingShopInfo = getShippingShopData.get(0);
                                String PROVINCE = shippingShopInfo.getOrDefault("PROVINCE", "").toString();//寄件人省名称
                                String CITY = shippingShopInfo.getOrDefault("CITY", "").toString();//寄件人市名称
                                String ADDRESS = shippingShopInfo.getOrDefault("ADDRESS", "").toString();//寄件人详细地址
                                String PHONE = shippingShopInfo.getOrDefault("PHONE", "").toString();//寄件人联系电话
                                String LONGITUDE = shippingShopInfo.getOrDefault("LONGITUDE", "").toString();
                                String LATITUDE = shippingShopInfo.getOrDefault("LATITUDE", "").toString();

                                String messStr_send = "";

                                if(PROVINCE.isEmpty())
                                {
									isInvokeKDN = false;
									messStr_send += "省份未维护,";
                                }
                                if(CITY.isEmpty())
                                {
									isInvokeKDN = false;
									messStr_send += "城市未维护,";
                                }
                                if(ADDRESS.isEmpty())
                                {
									isInvokeKDN = false;
									messStr_send += "详细地址未维护,";
                                }
                                if(PHONE.isEmpty())
                                {
									isInvokeKDN = false;
									messStr_send += "联系电话未维护";
                                }
                                //同城物流还要检查发件人的经纬度
                                if ("1".equals(shipMode))
                                {
                                	//同城配送，预约单预约三天内
									String pickupTime = "";
									String shipDate = map_order.get("SHIPDATE").toString();
									String sdtime = map_order.get("SHIPSTARTTIME").toString();//SHIPENDTIME
									sdtime = sdtime.replace("-", "");
									if (sdtime.isEmpty())
									{
										sdtime = new SimpleDateFormat("HHmmss").format(new Date());
									}
									pickupTime = shipDate+ sdtime;// 日期格式如"20181223110438"使用yyyyMMddHHmmss

									// 1-72小时为预约单 超过72小时不能发 2小时之内不为预约单
									long longcur = System.currentTimeMillis();
									Date dateSta =new SimpleDateFormat("yyyyMMddHHmmss").parse(pickupTime);
									long longsta = dateSta.getTime();
									// 差别到分钟
									long diff = (longsta - longcur) / (1000 * 60);
									if(diff>72*60)
									{
										//isInvokeDada = false;
										//isInvokeDadaMess.append("预约单超过72小时不能发！");
										this.Log("自动发快递ExpressOrderCreate开始调用kdn快递鸟物流,配送方式shipType="+shipType+"(同城快递)配送时间大于当前时间72小时暂不发送,订单号orderNo="+orderNo);
										continue;
									}


                                    if (LONGITUDE.trim().isEmpty()||LATITUDE.trim().isEmpty())
                                    {
                                        isInvokeKDN = false;
                                        messStr_send += "经纬度未维护";
                                    }
                                    else
                                    {
                                        BigDecimal lat = new BigDecimal("0");
                                        BigDecimal lng = new BigDecimal("0");
                                        try
                                        {
                                            lat = new BigDecimal(LATITUDE);
                                            lng = new BigDecimal(LONGITUDE);
                                            if (lat.compareTo(BigDecimal.ZERO)==1&&lng.compareTo(BigDecimal.ZERO)==1)
                                            {
                                                //门店经度（精确到小数点后5位）
                                                shippingShopInfo.put("LATITUDE",lat.setScale(5,BigDecimal.ROUND_HALF_UP)+"");
                                                shippingShopInfo.put("LONGITUDE",lng.setScale(5,BigDecimal.ROUND_HALF_UP)+"");
                                            }
                                            else
                                            {
                                                isInvokeKDN = false;
                                                messStr_send += "经纬度维护的不正确,";
                                            }
                                        }
                                        catch (Exception e)
                                        {
                                            isInvokeKDN = false;
                                            messStr_send += "经纬度维护的不正确,";
                                        }
                                    }

                                }

                                if(isInvokeKDN==false)
                                {
									messStr_send = "<br>配送门店("+shippingId+")信息异常:"+messStr_send;
                                    /*isInvokeDadaMess = new StringBuffer("");
                                    isInvokeDadaMess.append(messStr);*/
									isInvokeDadaMess.append(messStr_send);
                                }

                                /*******************判断下收件人信息***********************/

								String orderProvinceName = map_order.getOrDefault("PROVINCE","").toString();
								String orderCityName = map_order.getOrDefault("CITY","").toString();
								String orderExpAreaName = map_order.getOrDefault("COUNTY","").toString();
								String orderAddress = map_order.getOrDefault("ADDRESS","").toString();
								String recipientName = map_order.getOrDefault("GETMAN", "").toString();
                                String recipient_lng = map_order.getOrDefault("LONGITUDE", "").toString();
                                String recipient_lat = map_order.getOrDefault("LATITUDE", "").toString();
								if (recipientName.isEmpty())
								{
									recipientName = map_order.getOrDefault("CONTMAN", "").toString();
								}
								String recipientMobile = map_order.getOrDefault("GETMANTEL", "").toString().trim();
								if (recipientMobile.isEmpty())
								{
									recipientMobile = map_order.getOrDefault("CONTTEL", "").toString().trim();
								}

								String messStr_recipien = "";
								if(orderProvinceName.isEmpty())
								{
									isInvokeKDN = false;
									messStr_recipien += "收件省份为空,";
								}
								if(orderCityName.isEmpty())
								{
									isInvokeKDN = false;
									messStr_recipien += "收件市为空,";
								}
								/*if(orderExpAreaName.isEmpty())
								{
									isInvokeKDN = false;
									messStr_recipien += "收件区/县为空,";
								}*/
								if(orderAddress.isEmpty())
								{
									isInvokeKDN = false;
									messStr_recipien += "收件详细地址为空,";
								}
								if(recipientName.isEmpty())
								{
									isInvokeKDN = false;
									messStr_recipien += "收件人姓名为空,";
								}
								if(recipientMobile.isEmpty())
								{
									isInvokeKDN = false;
									messStr_recipien += "收件人联系电话为空,";
								}
								if (isInvokeKDN == false)
								{
									if (!messStr_recipien.isEmpty())
									{
										messStr_recipien = "<br>收件人信息异常:"+messStr_recipien;
										isInvokeDadaMess.append(messStr_recipien);
									}

								}
                                //同城物流还要检查发件人的经纬度
                                if ("1".equals(shipMode))
                                {
                                    if (recipient_lng.trim().isEmpty()||recipient_lat.trim().isEmpty())
                                    {
                                        isInvokeKDN = false;
                                        messStr_send += "经纬度为空";
                                    }
                                    else
                                    {
                                        BigDecimal lat = new BigDecimal("0");
                                        BigDecimal lng = new BigDecimal("0");
                                        try
                                        {
                                            lat = new BigDecimal(recipient_lat);
                                            lng = new BigDecimal(recipient_lng);
                                            if (lat.compareTo(BigDecimal.ZERO)==1&&lng.compareTo(BigDecimal.ZERO)==1)
                                            {
                                                //门店经度（精确到小数点后5位）
                                                map_order.put("LATITUDE",lat.setScale(5,BigDecimal.ROUND_HALF_UP)+"");
                                                map_order.put("LONGITUDE",lng.setScale(5,BigDecimal.ROUND_HALF_UP)+"");
                                            }
                                            else
                                            {
                                                isInvokeKDN = false;
                                                messStr_send += "经纬度不正确,";
                                            }
                                        }
                                        catch (Exception e)
                                        {
                                            isInvokeKDN = false;
                                            messStr_send += "经纬度不正确,";
                                        }
                                    }

                                }




                                if(!isInvokeKDN)
                                {
                                    //dada.Log("自动发快递ExpressOrderCreate开始调用dada物流,"+isInvokeDadaMess.toString()+"***eid="+eId+",LOADDOCTYPE="+orderLoadDoctype+",ORDERNO="+orderNo+"无需配送************");
									//写订单日志
									List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();

									orderStatusLog oslog=new orderStatusLog();
									oslog.setCallback_status("N");
									oslog.setChannelId(channelId);
									oslog.setDisplay("1");
									oslog.seteId(eId);
									oslog.setLoadDocType(orderLoadDoctype);
									oslog.setMachShopName(machShopName);
									oslog.setMachShopNo(machShopId);
									oslog.setMemo("收寄方信息不完整"+isInvokeDadaMess.toString());

									oslog.setNeed_callback("N");
									oslog.setNeed_notify("N");
									oslog.setNotify_status("N");
									oslog.setOpName("定时任务");
									oslog.setOpNo("JOB");
									oslog.setOrderNo(orderNo);
									oslog.setShippingShopName(shippingName);
									oslog.setShippingShopNo(shippingId);
									oslog.setShopName(shopName);
									oslog.setShopNo(shopId);
									//

									String statusType = "999";// 其他状态
									String updateStaus = "999";// 订单修改

									oslog.setStatusType(statusType);
									oslog.setStatus(updateStaus);

									String statusName = "物流下单失败";
									String statusTypeName = "呼叫物流";

									oslog.setStatusName(statusName);
									oslog.setStatusType(statusType);
									oslog.setStatusTypeName(statusTypeName.toString());
									oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
									orderStatusLogList.add(oslog);
									StringBuilder errorMessage = new StringBuilder();
									boolean nRet_s = HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorMessage);


									this.Log("自动发快递ExpressOrderCreate开始调用kdn快递鸟物流,"+isInvokeDadaMess.toString()+",订单orderNo="+orderNo+"，收寄方信息不完整无法配送************");
                                    continue;

                                }


                                String sqlOrderDetail="select * from dcp_order_detail where (PACKAGETYPE='1' or PACKAGETYPE='2' or PACKAGETYPE is null) and  eid='"+eId+"' and orderno='"+orderNo+"' ";
                                List<Map<String, Object>> getDetailDatas=this.doQueryData(sqlOrderDetail, null);

								String res= "";
								long timestamp = System.currentTimeMillis()/1000;
                                String ref_deliveryNo = orderNo+"-"+timestamp;//传入的物流单号,最多50位，这个先这样吧一般不会重复
                                if ("0".equals(shipMode))
                                {
                                    //全国快递，商家订单号最多30
                                    if (ref_deliveryNo.length()>30)
                                    {
                                        ref_deliveryNo = PosPub.getGUID(false).substring(0,30);//传入的物流单号,最多30位，这个先这样吧一般不会重复
                                    }
                                }
                                else
                                {
                                    //同城快递，商家订单号最多50
                                    if (ref_deliveryNo.length()>50)
                                    {
                                        ref_deliveryNo = PosPub.getGUID(false);
                                    }
                                }


                                StringBuffer apiErrorMessage = new StringBuffer("");
								boolean resultSuccess = false;
								String out_deliveryNo = "";//KDN返回的物流单号，不是真正的运单号
								if ("0".equals(shipMode))
								{
									//kdnQGService kdn = new kdnQGService(true);
                                    kdnQGService kdn = null;
                                    String EBusinessID = outSalesetMap.get("APPSIGNKEY").toString();//用户ID
                                    if ("350238".equals(EBusinessID))
                                    {
                                        kdn = new kdnQGService(true);
                                    }
                                    else
                                    {
                                        kdn = new kdnQGService();
                                    }
                                    //先调用超区校验接口
									res = kdn.checkDeliveryRange(outSalesetMap,map_order,shippingShopInfo,apiErrorMessage);
									if (res==null||res.isEmpty())
									{
										//后面统一写异常的订单历程

									}
									else
									{
										JSONObject jsonRes1 = JSONObject.parseObject(res);
										String Success1 = jsonRes1.get("Success").toString();
										String Reason1 = jsonRes1.getOrDefault("Reason","")==null?"":jsonRes1.getOrDefault("Reason","").toString();
										if (!"true".equalsIgnoreCase(Success1))
										{
											//后面统一写异常的订单历程
											apiErrorMessage.append("超区校验接口返回异常:"+Reason1);
										}
										else
										{
											//调用下单接口
											res = "";
											res = kdn.kdnOrderCreate(ref_deliveryNo,outSalesetMap, map_order, shippingShopInfo, getDetailDatas,apiErrorMessage);
											if (res==null||res.isEmpty())
											{
												//后面统一写异常的订单历程
											}
											else
											{
												JSONObject jsonRes2 = JSONObject.parseObject(res);
												String Success2 = jsonRes2.get("Success").toString();
												String Reason2 = jsonRes2.getOrDefault("Reason","")==null?"":jsonRes2.getOrDefault("Reason","").toString();
												if ("true".equalsIgnoreCase(Success2))
												{
													resultSuccess = true;
													String kdnOrderStr = jsonRes2.getOrDefault("Order","")==null?"":jsonRes2.getOrDefault("Order","").toString();
													try {
														JSONObject kdnOrderJson = JSONObject.parseObject(kdnOrderStr);
														out_deliveryNo = kdnOrderJson.getOrDefault("KDNOrderCode","")==null?"":kdnOrderJson.getOrDefault("KDNOrderCode","").toString();
													}
													catch (Exception e)
													{

													}
												}
												else
												{
													//后面统一写异常的订单历程
													apiErrorMessage.append("下单接口返回异常:"+Reason2);
												}
											}
										}


									}

								}
								else
								{
                                    //kdnQGService kdn = new kdnQGService(true);
                                    kdnTCService kdn = null;
                                    String EBusinessID = outSalesetMap.get("APPSIGNKEY").toString();//用户ID
                                    if ("1663339".equals(EBusinessID))
                                    {
                                        kdn = new kdnTCService(true);
                                    }
                                    else
                                    {
                                        kdn = new kdnTCService();
                                    }

                                    //调用下单接口
                                    res = "";
                                    res = kdn.kdnOrderCreate(ref_deliveryNo,outSalesetMap, map_order, shippingShopInfo, getDetailDatas,apiErrorMessage);
                                    if (res==null||res.isEmpty())
                                    {
                                        //后面统一写异常的订单历程
										apiErrorMessage.append("下单接口返回为空");
                                    }
                                    else
                                    {
                                        JSONObject jsonRes2 = JSONObject.parseObject(res);
                                        String resultCode = jsonRes2.get("resultCode").toString();
                                        String reason = jsonRes2.getOrDefault("reason","")==null?"":jsonRes2.getOrDefault("reason","").toString();
                                        if ("100".equalsIgnoreCase(resultCode))
                                        {
                                            resultSuccess = true;
                                            JSONObject dataJson =  jsonRes2.getJSONObject("data");
                                            out_deliveryNo = dataJson.getOrDefault("kdnOrderCode","")==null?"":dataJson.getOrDefault("kdnOrderCode","").toString();

                                        }
                                        else
                                        {
                                            //后面统一写异常的订单历程
                                            apiErrorMessage.append(reason);
                                        }
                                    }
								}

                                if(resultSuccess)
                                {
									//执行
									List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
									UptBean ubecOrder=new UptBean("dcp_order");
									ubecOrder.addCondition("EID", new DataValue(eId, Types.VARCHAR));
									ubecOrder.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
									if(!ref_deliveryNo.isEmpty())
									{
										ubecOrder.addUpdateValue("REF_DELIVERYNO", new DataValue(ref_deliveryNo, Types.VARCHAR));

										//插入订单对应物流表
										String[] columns_delivery = { "EID", "ORDERNO", "REF_DELIVERYNO", "OUT_DELIVERYNO",
												"DELIVERYTYPE","LOADDOCTYPE","CHANNELID","SHIPPINGSHOP", "SHIPPERCODE", "LOGISTICSNO", "STATE", "DELIVERYSTATUS"};

										DataValue[] insValue1 = new DataValue[]
												{
														new DataValue(eId, Types.VARCHAR),
														new DataValue(orderNo, Types.VARCHAR),
														new DataValue(ref_deliveryNo, Types.VARCHAR),
														new DataValue(out_deliveryNo, Types.VARCHAR),
														new DataValue(deliveryType, Types.VARCHAR),
														new DataValue(orderLoadDoctype, Types.VARCHAR),
                                                        new DataValue(channelId, Types.VARCHAR),
														new DataValue(shippingId, Types.VARCHAR),
														new DataValue("", Types.VARCHAR),//SHIPPERCODE
														new DataValue("", Types.VARCHAR),//LOGISTICSNO
														new DataValue("", Types.VARCHAR),
														new DataValue("0", Types.VARCHAR) //-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 8货到物流中心 9消费者七天未取件
												};


										InsBean ib1 = new InsBean("dcp_order_delivery",columns_delivery);
										ib1.addValues(insValue1);
										lstData.add(new DataProcessBean(ib1));
									}
									if(!out_deliveryNo.isEmpty())
									{
										ubecOrder.addUpdateValue("DELIVERYNO", new DataValue(out_deliveryNo, Types.VARCHAR));//暂时先给快递鸟单号，后续回调更新运单单号
                                        ubecOrder.addUpdateValue("OUTDOCORDERNO", new DataValue(out_deliveryNo, Types.VARCHAR));//使用才哥搞的 商有云字段
									}
									ubecOrder.addUpdateValue("DELIVERYSTATUS", new DataValue("0", Types.VARCHAR));//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 8货到物流中心 9消费者七天未取件
									ubecOrder.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                                    ubecOrder.addUpdateValue("DELIVERY_CREATETIME", new DataValue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), Types.DATE));//记录物流下单时间
									lstData.add(new DataProcessBean(ubecOrder));


									StaticInfo.dao.useTransactionProcessData(lstData);

                                    invokeCreateRes = true;
									//写订单日志
									List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
									String LogStatus="0";
									orderStatusLog oslog=new orderStatusLog();
									oslog.setCallback_status("N");
									oslog.setChannelId(channelId);
									oslog.setDisplay("1");
									oslog.seteId(eId);
									oslog.setLoadDocType(orderLoadDoctype);
									oslog.setMachShopName(machShopName);
									oslog.setMachShopNo(machShopId);
									oslog.setMemo("已上传物流");
									if (!ref_deliveryNo.isEmpty())
									{
										oslog.setMemo("已上传物流<br>商家物流单号:"+ref_deliveryNo+"<br>快递鸟订单号:"+out_deliveryNo);
									}
									oslog.setNeed_callback("N");
									oslog.setNeed_notify("N");
									oslog.setNotify_status("N");
									oslog.setOpName("定时任务");
									oslog.setOpNo("JOB");
									oslog.setOrderNo(orderNo);
									oslog.setShippingShopName(shippingName);
									oslog.setShippingShopNo(shippingId);
									oslog.setShopName(shopName);
									oslog.setShopNo(shopId);
									oslog.setStatus(LogStatus);
									//
									String statusType="2";
									StringBuilder statusTypeName=new StringBuilder();
									String statusName=HelpTools.GetOrderStatusName(statusType, LogStatus, statusTypeName);
									oslog.setStatusName(statusName);
									oslog.setStatusType(statusType);
									oslog.setStatusTypeName(statusTypeName.toString());
									oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
									orderStatusLogList.add(oslog);
									StringBuilder errorMessage = new StringBuilder();
									boolean nRet_s = HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorMessage);

								}
                                else
								{
									//写订单日志
									List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();

									orderStatusLog oslog=new orderStatusLog();
									oslog.setCallback_status("N");
									oslog.setChannelId(channelId);
									oslog.setDisplay("1");
									oslog.seteId(eId);
									oslog.setLoadDocType(orderLoadDoctype);
									oslog.setMachShopName(machShopName);
									oslog.setMachShopNo(machShopId);
									oslog.setMemo("呼叫物流异常:"+apiErrorMessage.toString());

									oslog.setNeed_callback("N");
									oslog.setNeed_notify("N");
									oslog.setNotify_status("N");
									oslog.setOpName("定时任务");
									oslog.setOpNo("JOB");
									oslog.setOrderNo(orderNo);
									oslog.setShippingShopName(shippingName);
									oslog.setShippingShopNo(shippingId);
									oslog.setShopName(shopName);
									oslog.setShopNo(shopId);
									//

									String statusType = "999";// 其他状态
									String updateStaus = "999";// 订单修改

									oslog.setStatusType(statusType);
									oslog.setStatus(updateStaus);

									String statusName = "物流下单失败";
									String statusTypeName = "呼叫物流";

									oslog.setStatusName(statusName);
									oslog.setStatusType(statusType);
									oslog.setStatusTypeName(statusTypeName.toString());
									oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
									orderStatusLogList.add(oslog);
									StringBuilder errorMessage = new StringBuilder();
									boolean nRet_s = HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorMessage);
									continue;
								}

                            }
                            else if(ThirdpartConstants.cangdao_deliveryType.equals(deliveryType))
                            {
                                List<Map<String, Object>> outSalesetList=getQData_OutSaleset.stream().filter(g->(g.get("DELIVERYTYPE")==null?"":g.get("DELIVERYTYPE").toString()).equals(ThirdpartConstants.cangdao_deliveryType)).collect(Collectors.toList());
                                if(outSalesetList==null||outSalesetList.size()==0){
                                    continue;
                                }

                                //单号
                                String orderNo=map_order.get("ORDERNO").toString();
                                String orderShopNo=map_order.get("SHOP").toString();


                                String orderLoadDoctype=map_order.get("LOADDOCTYPE").toString();

                                /***************查询下必传参数*********************/
                                String sql_shippingShop = " select * from dcp_org where eid='"+eId+"' and organizationno='"+shippingId+"'";
                                this.Log("自动发快递ExpressOrderCreate开始调用candao餐道物流,查询配送门店sql="+sql_shippingShop+",订单号orderNo="+orderNo);
                                List<Map<String, Object>> getShippingShopData = this.doQueryData(sql_shippingShop, null);
                                if(getShippingShopData==null||getShippingShopData.isEmpty())
                                {
                                    continue;
                                }

                                Map<String, Object> shippingShopInfo = getShippingShopData.get(0);

                                /*****************根据配送门店ID，先查询指定门店对应的物流配置***********************************/
                                //Map<String, Object> outSalesetMap=outSalesetList.get(0);
                                Map<String, Object> outSalesetMap=this.getDeliverySetByShippingShop(eId,deliveryType,shippingId);
                                if(outSalesetMap==null||outSalesetMap.isEmpty())
                                {
                                    this.Log("ExpressOrderCreate定时任务。查询配送门店对应的物流设置参数为空，请检查物流参数设置，配送门店:"+shippingId+",物流类型:"+deliveryType);
                                    continue;
                                }
                                String accessKey = outSalesetMap.get("APPSIGNKEY").toString();//用户ID
								String isTest = outSalesetMap.getOrDefault("ISTEST","").toString();
                                candaoService candao= null;
                                if ("Y".equals(isTest))
                                {
                                    candao = new candaoService(true);
                                }
                                else
                                {
                                    candao = new candaoService();
                                }

                                String sqlOrderDetail="select * from dcp_order_detail where (PACKAGETYPE='1' or PACKAGETYPE='2' or PACKAGETYPE is null) and  eid='"+eId+"' and orderno='"+orderNo+"' ";
                                List<Map<String, Object>> getDetailDatas=this.doQueryData(sqlOrderDetail, null);

                                //this.Log("***自动发快递ExpressOrderCreate开始调用yto圆通物流,eid="+eId+",LOADDOCTYPE="+orderLoadDoctype+",订单号orderNo="+orderNo+"************");
                                if (getDetailDatas==null||getDetailDatas.size()==0){
                                    //没有商品明细不用上传
                                    this.Log("***自动发快递ExpressOrderCreate开始调用candao餐道物流,没有商品明细 跳过,订单ORDERNO="+orderNo+"************");
                                    continue;
                                }
                                String ref_deliveryNo = orderNo;//商家物流单号，默认订单号
                                StringBuffer apiErrorMessage = new StringBuffer();

                                String res = candao.candaoOrderCreate(ref_deliveryNo,outSalesetMap, map_order, shippingShopInfo, getDetailDatas,apiErrorMessage);
                                if(res == null||res.isEmpty())
                                {
                                    continue;
                                }

                                JSONObject json1 = JSONObject.parseObject(res);

                                String code=json1.get("status").toString();//1成功，其他失败
                                if("1".equals(code))
                                {
									JSONObject data = json1.getJSONObject("data");
									String out_deliveryNo = data.get("orderId").toString();
									//执行
									List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
									UptBean ubecOrder=new UptBean("dcp_order");
									ubecOrder.addCondition("EID", new DataValue(eId, Types.VARCHAR));
									ubecOrder.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
									if(!ref_deliveryNo.isEmpty())
									{
										ubecOrder.addUpdateValue("REF_DELIVERYNO", new DataValue(ref_deliveryNo, Types.VARCHAR));

										//插入订单对应物流表
										String[] columns_delivery = { "EID", "ORDERNO", "REF_DELIVERYNO", "OUT_DELIVERYNO",
												"DELIVERYTYPE","LOADDOCTYPE","CHANNELID","SHIPPINGSHOP", "SHIPPERCODE", "LOGISTICSNO", "STATE", "DELIVERYSTATUS"};

										DataValue[] insValue1 = new DataValue[]
												{
														new DataValue(eId, Types.VARCHAR),
														new DataValue(orderNo, Types.VARCHAR),
														new DataValue(ref_deliveryNo, Types.VARCHAR),
														new DataValue(out_deliveryNo, Types.VARCHAR),
														new DataValue(deliveryType, Types.VARCHAR),
														new DataValue(orderLoadDoctype, Types.VARCHAR),
														new DataValue(channelId, Types.VARCHAR),
														new DataValue(shippingId, Types.VARCHAR),
														new DataValue("", Types.VARCHAR),//SHIPPERCODE
														new DataValue("", Types.VARCHAR),//LOGISTICSNO
														new DataValue("", Types.VARCHAR),
														new DataValue("0", Types.VARCHAR) //-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 8货到物流中心 9消费者七天未取件
												};


										InsBean ib1 = new InsBean("dcp_order_delivery",columns_delivery);
										ib1.addValues(insValue1);
										lstData.add(new DataProcessBean(ib1));
									}
									if(!out_deliveryNo.isEmpty())
									{
										ubecOrder.addUpdateValue("DELIVERYNO", new DataValue(out_deliveryNo, Types.VARCHAR));//暂时先给餐道返回的单号，后续回调更新运单单号
										ubecOrder.addUpdateValue("OUTDOCORDERNO", new DataValue(out_deliveryNo, Types.VARCHAR));//使用才哥搞的 商有云字段
									}
									ubecOrder.addUpdateValue("DELIVERYSTATUS", new DataValue("0", Types.VARCHAR));//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 8货到物流中心 9消费者七天未取件
									ubecOrder.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
									ubecOrder.addUpdateValue("DELIVERY_CREATETIME", new DataValue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), Types.DATE));//记录物流下单时间
									lstData.add(new DataProcessBean(ubecOrder));


									StaticInfo.dao.useTransactionProcessData(lstData);

                                    invokeCreateRes = true;
									//写订单日志
									List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
									String LogStatus="0";
									orderStatusLog oslog=new orderStatusLog();
									oslog.setCallback_status("N");
									oslog.setChannelId(channelId);
									oslog.setDisplay("1");
									oslog.seteId(eId);
									oslog.setLoadDocType(orderLoadDoctype);
									oslog.setMachShopName(machShopName);
									oslog.setMachShopNo(machShopId);
									oslog.setMemo("已上传物流");
									if (!ref_deliveryNo.isEmpty())
									{
										oslog.setMemo("已上传物流<br>商家物流单号:"+ref_deliveryNo+"<br>餐道订单号:"+out_deliveryNo);
									}
									oslog.setNeed_callback("N");
									oslog.setNeed_notify("N");
									oslog.setNotify_status("N");
									oslog.setOpName("定时任务");
									oslog.setOpNo("JOB");
									oslog.setOrderNo(orderNo);
									oslog.setShippingShopName(shippingName);
									oslog.setShippingShopNo(shippingId);
									oslog.setShopName(shopName);
									oslog.setShopNo(shopId);
									oslog.setStatus(LogStatus);
									//
									String statusType="2";
									StringBuilder statusTypeName=new StringBuilder();
									String statusName=HelpTools.GetOrderStatusName(statusType, LogStatus, statusTypeName);
									oslog.setStatusName(statusName);
									oslog.setStatusType(statusType);
									oslog.setStatusTypeName(statusTypeName.toString());
									oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
									orderStatusLogList.add(oslog);
									StringBuilder errorMessage = new StringBuilder();
									boolean nRet_s = HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorMessage);

								}
                                else
                                {
									String reason = "";
									if(json1.containsKey("msg"))
									{
										reason = json1.get("msg").toString();
									}
									if (!reason.isEmpty())
									{
										//写订单日志
										List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
										String LogStatus="0";
										orderStatusLog oslog=new orderStatusLog();
										oslog.setCallback_status("N");
										oslog.setChannelId(channelId);
										oslog.setDisplay("1");
										oslog.seteId(eId);
										oslog.setLoadDocType(orderLoadDoctype);
										oslog.setMachShopName(machShopName);
										oslog.setMachShopNo(machShopId);
										oslog.setMemo(reason);

										oslog.setNeed_callback("N");
										oslog.setNeed_notify("N");
										oslog.setNotify_status("N");
										oslog.setOpName("定时任务");
										oslog.setOpNo("JOB");
										oslog.setOrderNo(orderNo);
										oslog.setShippingShopName(shippingName);
										oslog.setShippingShopNo(shippingId);
										oslog.setShopName(shopName);
										oslog.setShopNo(shopId);
										oslog.setStatus(LogStatus);
										//

										String statusType = "999";// 其他状态
										String updateStaus = "999";// 订单修改

										oslog.setStatusType(statusType);
										oslog.setStatus(updateStaus);

										String statusName = "物流下单失败";
										String statusTypeName = "呼叫物流";

										oslog.setStatusName(statusName);
										oslog.setStatusType(statusType);
										oslog.setStatusTypeName(statusTypeName.toString());
										oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
										orderStatusLogList.add(oslog);
										StringBuilder errorMessage = new StringBuilder();
										boolean nRet_s = HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorMessage);

									}
                                }

                            }
							else//其他物流
							{
                                this.Log("【自动发物流定时任务ExpressOrderCreate】物流类型deliveryType="+deliveryType+",暂未对接,订单号orderNo="+orderNo_DB);
                                continue;
							}

                            if (invokeCreateRes)
                            {
                                if (orderLoadDocType.WECHAT.equals(loadDocType)||orderLoadDocType.MINI.equals(loadDocType)||orderLoadDocType.WECHAT.equals(loadDocType))
                                {
                                    try
                                    {
                                        org.json.JSONObject js=new org.json.JSONObject();
                                        js.put("serviceId", "OrderStatusUpdate");
                                        js.put("orderNo", orderNo_DB);
                                        js.put("statusType", "2");//状态类型 1=交易状态变更 2=物流状态变更 3=其他 4= 退单状态变更 5=推送状态变更 6=开票状态变更
                                        js.put("status", "5");//交易状态 0=未配送 1=配送中 2=已配送 3=确认收货 4=已取消 5=已下单6=已接单
                                        //delstatus中台物流状态 -1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单
                                        js.put("description", "物流已下单");
                                        js.put("oprId", "admin");
                                        js.put("orgType", "2");
                                        js.put("orgId", shippingId);
                                        js.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                                        org.json.JSONArray deliverInfo = new org.json.JSONArray();
                                        org.json.JSONObject body = new org.json.JSONObject();
                                        //body.put("expressType", deltype);//快递鸟 商城对应的字段是INT，暂时注释
                                        if (!"KDN".equals(deliveryType))
                                        {
                                            try
                                            {
                                                int deltype_i = Integer.parseInt(deliveryType);
                                                body.put("expressType", deliveryType);
                                            } catch (Exception e) {
                                            }
                                        }
                                        body.put("expressTypeCode", "");//真正的物流公司编码
                                        body.put("expressTypeName", "");//真正的物流公司名称
                                        body.put("expressBillNo", "");
                                        body.put("deliverPerson", "");
                                        body.put("deliverPhone", "");
                                        body.put("remark", "物流已下单");
                                        deliverInfo.put(body);

                                        js.put("deliverInfo", deliverInfo);

                                        String req_crm = js.toString();
                                        this.Log("【自动发物流定时任务ExpressOrderCreate】物流下单成功,通知商城接口请求req:"+req_crm+",对应的订单单号orderNO="+orderNo_DB);
                                        String result_crm = HttpSend.MicroMarkSend(req_crm, eId, "OrderStatusUpdate",channelId);
                                        this.Log("【自动发物流定时任务ExpressOrderCreate】物流下单成功,通知商城接口返回res:"+result_crm+",对应的订单单号orderNO="+orderNo_DB);

                                    }
                                    catch (Exception e)
                                    {

                                    }

                                }

                            }
						}
						catch (Exception e)
						{
							try
							{
								StringWriter errors = new StringWriter();
								PrintWriter pw=new PrintWriter(errors);
								e.printStackTrace(pw);

								pw.flush();
								pw.close();

								errors.flush();
								errors.close();
								this.Log("ExpressOrderCreate定时任务报错信息:"+ e.getMessage()+"\r\n异常原因:" + errors.toString());
								logger.error("\r\n******自動發快遞ExpressOrderCreate报错信息" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");

								pw=null;
								errors=null;
							}
							catch (IOException e1)
							{
								logger.error("\r\n******自動發快遞ExpressOrderCreate报错信息" + e1.getMessage() + "******\r\n");
								this.Log("ExpressOrderCreate定时任务报错信息:"+ e1.getMessage());
							}
						}
					}	
				}
				else 
				{
					logger.info("\r\n*********自動發快遞ExpressOrderCreate物流廠商接口表dcp_outsaleset未設置資料:************\r\n");
					this.Log("自動發快遞ExpressOrderCreate物流廠商接口表dcp_outsaleset未設置資料。");
					return "";
				}			
			}
			else 
			{
				logger.info("\r\n*********自動發快遞ExpressOrderCreate無記錄SQL: "+sqlOrder+"************\r\n");
				this.Log("自動發快遞ExpressOrderCreate查询没有数据。");
			}
		} 
		catch (Exception e) 
		{
			try 
			{
				StringWriter errors = new StringWriter();
				PrintWriter pw=new PrintWriter(errors);
				e.printStackTrace(pw);			

				pw.flush();
				pw.close();			

				errors.flush();
				errors.close();

				logger.error("\r\n******自動發快遞ExpressOrderCreate报错信息" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");

				pw=null;
				errors=null;
			} 
			catch (IOException e1) 
			{					
				logger.error("\r\n******自動發快遞ExpressOrderCreate报错信息" + e1.getMessage() + "******\r\n");
			}		

			//
			sReturnInfo="自動發快遞ExpressOrderCreate错误信息:" + e.getMessage();
		}
		finally 
		{
			bRun=false;//
			logger.info("\r\n*********自動發快遞ExpressOrderCreate定时调用End:************\r\n");
			this.Log("自動發快遞ExpressOrderCreate定时调用End:");
		}
		return sReturnInfo;

	}
	
	public void Log(String log)  {
		try
		{
			HelpTools.writelog_fileName(log, "ExpressOrderCreate");
			
		} catch (Exception e)
		{
			// TODO: handle exception
		}
		
	}

	/**
	 * 根据物流类型以及配送门店，获取相应的物流类型设置的参数（先查询配送门店为指定生效门店的物流，再查询默认生效全部门店的物流）
	 * @param eId 企业ID
	 * @param deliveryType 物流类型
	 * @param shopId 配送门店ID
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getDeliverySetByShippingShop(String eId,String deliveryType,String shopId) throws  Exception
	{
		Map<String, Object> resultMap = null;
		try {
			StringBuffer sqlBuffer = new StringBuffer("");
			sqlBuffer.append(" select * from (");
			//先查询适用于指定门店的参数
			sqlBuffer.append(" select  CAST('2' AS NVARCHAR2(1)) IDX, A.* from dcp_outsaleset A "
					+ " LEFT JOIN dcp_outsaleset_shop B  on A.EID=B.EID AND A.APPID=B.APPID AND A.DELIVERYTYPE=B.DELIVERYTYPE"
					+ " WHERE A.EID='" + eId + "' AND A.DELIVERYTYPE='" + deliveryType + "'  and A.SHOPTYPE='2' and A.STATUS='100'  AND B.SHOPID='" + shopId + "' ");

			sqlBuffer.append(" UNION ALL ");
			//查询下适用全部门店
			sqlBuffer.append(" select  CAST('1' AS NVARCHAR2(1)) IDX, A.* from dcp_outsaleset A "
					+ " WHERE A.EID='" + eId + "' AND A.DELIVERYTYPE='" + deliveryType + "'  and A.SHOPTYPE='1' and A.STATUS='100' ");

			sqlBuffer.append(") A ORDER BY A.IDX DESC");
			String sql = sqlBuffer.toString();
			this.Log("ExpressOrderCreate定时任务。【物流配置是否指定门店】查询物流设置参数sql=" + sql+",企业eId=" +eId+",物流类型deliveryType="+deliveryType+",配送门店shopId="+shopId);
			List<Map<String, Object>> getDatas = this.doQueryData(sql, null);
			if (getDatas != null && getDatas.isEmpty() == false) {
				resultMap = getDatas.get(0);
				this.Log("ExpressOrderCreate定时任务。【物流配置是否指定门店】查询物流设置参数内容=" + resultMap.toString());
			}
			else
			{
				this.Log("ExpressOrderCreate定时任务。【物流配置是否指定门店】查询物流设置参数为空！企业eId=" +eId+",物流类型deliveryType="+deliveryType+",配送门店shopId="+shopId);
			}
		}

		catch (Exception e)
		{
			this.Log("ExpressOrderCreate定时任务。【物流配置是否指定门店】查询物流设置参数，异常:"+e.getMessage());
		}

		return resultMap;

	}

	/**
	 * kdn快递鸟物流参数(全国快递和同城配送不一致)
	 * @param eId
	 * @param deliveryType
	 * @param shopId
	 * @param shipMode
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getKDNDeliverySetByShippingShop(String eId,String deliveryType,String shopId,String shipMode) throws  Exception
	{
		Map<String, Object> resultMap = null;
		if (!"1".equals(shipMode))
		{
			shipMode = "0";//0全国快递，1同城快递
		}
		try {
			StringBuffer sqlBuffer = new StringBuffer("");
			sqlBuffer.append(" select * from (");
			//先查询适用于指定门店的参数
			sqlBuffer.append(" select  CAST('2' AS NVARCHAR2(1)) IDX, A.* from dcp_outsaleset A "
					+ " LEFT JOIN dcp_outsaleset_shop B  on A.EID=B.EID AND A.APPID=B.APPID AND A.DELIVERYTYPE=B.DELIVERYTYPE"
					+ " WHERE A.STATUS='100' AND A.EID='" + eId + "' AND A.DELIVERYTYPE='" + deliveryType + "'  and A.SHOPTYPE='2'  AND B.SHOPID='" + shopId + "' AND SHIPMODE='"+shipMode+"' ");

			sqlBuffer.append(" UNION ALL ");
			//查询下适用全部门店
			sqlBuffer.append(" select  CAST('1' AS NVARCHAR2(1)) IDX, A.* from dcp_outsaleset A "
					+ " WHERE A.STATUS='100' AND A.EID='" + eId + "' AND A.DELIVERYTYPE='" + deliveryType + "'  and A.SHOPTYPE='1' AND SHIPMODE='"+shipMode+"' ");

			sqlBuffer.append(") A ORDER BY A.IDX DESC");
			String sql = sqlBuffer.toString();
			this.Log("ExpressOrderCreate定时任务。【快递鸟物流类型】查询物流设置参数sql=" + sql+",企业eId=" +eId+",物流类型deliveryType="+deliveryType+",配送门店shopId="+shopId+",配送方式(0全国；1同城)shipMode="+shipMode);
			List<Map<String, Object>> getDatas = this.doQueryData(sql, null);
			if (getDatas != null && getDatas.isEmpty() == false) {
				resultMap = getDatas.get(0);
				this.Log("ExpressOrderCreate定时任务。【快递鸟物流类型】查询物流设置参数内容=" + resultMap.toString());
			}
			else
			{
				this.Log("ExpressOrderCreate定时任务。【快递鸟物流类型】查询物流设置参数为空！企业eId=" +eId+",物流类型deliveryType="+deliveryType+",配送门店shopId="+shopId+",配送方式(0全国；1同城)shipMode="+shipMode);
			}
		}

		catch (Exception e)
		{
			this.Log("ExpressOrderCreate定时任务。【快递鸟物流类型】查询物流设置参数，异常:"+e.getMessage());
		}

		return resultMap;
	}

    /**
     * job运行时间，（如果没有设置，默认一直运行）
     * @return
     * @throws Exception
     */
    private boolean jobRunTimeFlag() throws Exception
    {
        boolean flag = true;
        String sdate = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
        String stime = new SimpleDateFormat("HHmmss").format(Calendar.getInstance().getTime());

        // 先查 job 执行时间，然后再执行后续操作
        String getTimeSql = "select * from job_quartz_detail where job_name = 'ExpressOrderCreate'  and cnfflg = 'Y' ";
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
        else// 如果没设置执行时间，一直运行
        {
            return true;
        }
        return flag;
    }

}
