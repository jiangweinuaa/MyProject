package com.dsc.spos.waimai;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.dsc.spos.waimai.kdniao.query.RequestData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.DsmDAO;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderShippingReq;
import com.dsc.spos.json.cust.req.DCP_OrderShipping_OpenReq;
import com.dsc.spos.json.cust.req.DCP_OrderShippingReq.levelRequest;
import com.dsc.spos.json.cust.res.DCP_LoginRetailRes;
import com.dsc.spos.json.cust.res.DCP_OrderShipping_OpenRes;
import com.dsc.spos.json.cust.res.DCP_LoginRetailRes.level1Elm;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.imp.json.DCP_OrderShipping_Open;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.TokenManagerRetail;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.thirdpart.ThirdpartConstants;
import com.dsc.spos.thirdpart.ThirdpartService;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.XmlAndJsonConvert;
import com.dsc.spos.waimai.dianwoda.DianwodaClientResponse;
import com.dsc.spos.waimai.dianwoda.DianwodaDataCityCodeItem;
import com.dsc.spos.waimai.dianwoda.DianwodaDataCityCodeResult;
import com.dsc.spos.waimai.dianwoda.DianwodaHttpClientUtil;
import com.dsc.spos.waimai.dianwoda.DianwodaMessageOrderStatusUpdate;
import com.dsc.spos.waimai.dianwoda.DianwodaOrderCreateParam;
import com.dsc.spos.waimai.dianwoda.DianwodaOrderCreateResult;
import com.dsc.spos.waimai.dianwoda.DianwodaService;
import com.dsc.spos.waimai.entity.orderStatusLog;
import com.google.gson.reflect.TypeToken;
import com.sun.xml.bind.v2.runtime.XMLSerializer;

public class WULIUCallBackService extends SWaimaiBasicService 
{
	String wuliutype = "";
	String ssres = "";
	static String LogFileName = "WULIUCallBack";

	Logger logger = LogManager.getLogger(SWaimaiBasicService.class.getName());

	public WULIUCallBackService(String wltype) 
	{
		this.wuliutype = wltype;
	}

	@Override
	public String execute(String json) throws Exception {
		// TODO Auto-generated method stub
		String res_json = json;
		HelpTools.writelog_fileName("【物流回调WULIUCallBack】类型(WULIUTYPE):"+wuliutype+",入参:"+res_json,LogFileName);
		if (res_json == null || res_json.length() == 0) {
			return null;
		}
		
		if(wuliutype.equals("YTO"))
		{
			String[] ytoResquest = res_json.split("&");//
			if (ytoResquest == null || ytoResquest.length == 0) {
				HelpTools.writelog_fileName("【物流回调WULIUCallBack】类型(WULIUTYPE):"+wuliutype+",解析圆通发送的请求格式有误！",LogFileName);
				return null;
			}
	
			Map<String, String> map_ytoResquest = new HashMap<String, String>();
			String urlDecodeString = "";
			for (String string_mt : ytoResquest) {
	
				try {
					int indexofSpec = string_mt.indexOf("=");
					String s1 = string_mt.substring(0, indexofSpec);
					String s2 = string_mt.substring(indexofSpec + 1, string_mt.length());					
					String s2_decode = HelpTools.getURLDecoderString(s2);			
					
					map_ytoResquest.put(s1, s2_decode);
					urlDecodeString +=s1+"="+s2_decode+"&";
				} catch (Exception e) {
					// TODO: handle exception
					continue;
				}
			}
	
			HelpTools.writelog_fileName("【圆通物流URL转码后2】" +urlDecodeString,LogFileName);
			
			String xmlReq = map_ytoResquest.getOrDefault("logistics_interface", "");
			res_json = XmlAndJsonConvert.xml2json(xmlReq);
			
		}
		else if(wuliutype.startsWith("MTPAOTUI"))
		{
			String[] ytoResquest = res_json.split("&");//
			if (ytoResquest == null || ytoResquest.length == 0) {
				HelpTools.writelog_fileName("【物流回调WULIUCallBack】类型(WULIUTYPE):"+wuliutype+",解析美团跑腿发送的请求格式有误！",LogFileName);
				return null;
			}

			Map<String, String> map_ytoResquest = new HashMap<String, String>();
			String urlDecodeString = "";
			for (String string_mt : ytoResquest) {

				try {
					int indexofSpec = string_mt.indexOf("=");
					String s1 = string_mt.substring(0, indexofSpec);
					String s2 = string_mt.substring(indexofSpec + 1, string_mt.length());
					String s2_decode = HelpTools.getURLDecoderString(s2);

					map_ytoResquest.put(s1, s2_decode);
					urlDecodeString +=s1+"="+s2_decode+"&";
				} catch (Exception e) {
					// TODO: handle exception
					continue;
				}
			}

			HelpTools.writelog_fileName("【美团跑腿物流URL转码后2】" +urlDecodeString,LogFileName);
			res_json = com.alibaba.fastjson.JSON.toJSONString(map_ytoResquest);

		}
		else if(wuliutype.equals("KDNQGP")||wuliutype.equals("KDNTCP"))
		{
			String[] ytoResquest = res_json.split("&");//
			if (ytoResquest == null || ytoResquest.length == 0) {
				HelpTools.writelog_fileName("【物流回调WULIUCallBack】类型(WULIUTYPE):"+wuliutype+",解析快递鸟回调内容格式有误！",LogFileName);
				return null;
			}

			Map<String, String> map_ytoResquest = new HashMap<String, String>();
			String urlDecodeString = "";
			for (String string_mt : ytoResquest) {

				try {
					int indexofSpec = string_mt.indexOf("=");
					String s1 = string_mt.substring(0, indexofSpec);
					String s2 = string_mt.substring(indexofSpec + 1, string_mt.length());
					String s2_decode = HelpTools.getURLDecoderString(s2);

					map_ytoResquest.put(s1, s2_decode);
					urlDecodeString +=s1+"="+s2_decode+"&";
				} catch (Exception e) {
					// TODO: handle exception
					continue;
				}
			}

			HelpTools.writelog_fileName("【快递鸟物流URL转码后】" +urlDecodeString,LogFileName);
			res_json = com.alibaba.fastjson.JSON.toJSONString(map_ytoResquest);
		}
		
		
		Map<String, Object> res = new HashMap<String, Object>();
		this.processDUID(res_json, res);
		// JSONObject.parseObject(res);
		return ssres;

	}

	@Override
	protected void processDUID(String req, Map<String, Object> res) throws Exception 
	{
		// TODO Auto-generated method stub

		try 
		{
			//订单中心 DeliveryStutas 物流配送状态
			//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 
			String delstatus = "";
			String orderNO = "";
			String deltype = "";
			String delorderno = "";
			boolean isNeedOrderToSale = false;//是否需要订转销  乐莎尔的 达达 和圆通 快递上门取件后，自动订转销
			// region 更新本地
			UptBean ub1 = null;
			ub1 = new UptBean("DCP_ORDER");


			//台湾绿界内容非JSON,单独处理greenworld
			JSONObject obj =null;
			if (wuliutype.equals("greenworld")) 
			{}
			else 
			{
				req=HttpSend.unicodeToCn(req);
				HelpTools.writelog_fileName("【物流回调WULIUCallBack】类型(WULIUTYPE):"+wuliutype+",转化json后入参:"+req,LogFileName);
				obj = new JSONObject(req);
			}

			
			//点我达状态变更消息
			if(wuliutype.equals("DIANWODA"))
			{		
				String messageType = obj.get("type").toString();
				if(messageType==null||messageType.equals("dianwoda.order.status-update")==false)
				{
					HelpTools.writelog_fileName("【点我达非订单状态变更消息】:"+messageType+",暂不处理直接返回成功!"+req,LogFileName);
					ssres = "{\"code\":\"success\"}";
					return;
				}
				String opName="点我达配送";
				deltype = "20";
				delstatus = "";
				String content = obj.get("content").toString();//消息内容
				
				DianwodaMessageOrderStatusUpdate statusModel =	JSON.parseObject(content,DianwodaMessageOrderStatusUpdate.class);
				String order_original_id = statusModel.getOrder_original_id();//订单编号
				String order_status = statusModel.getOrder_status();//订单状态
				String action_code = statusModel.getAction_code();//变更事件
				orderNO = order_original_id;//给到点我达的快递单号 =订单的单号-时间戳
				int lastIndexOf = orderNO.lastIndexOf("-");
				if (lastIndexOf > 0)
				{
					orderNO = orderNO.substring(0, lastIndexOf);
				}
				HelpTools.writelog_fileName("【点我达订单状态变更消息】处理后orderNO="+orderNO,LogFileName);
				//下面的节点 不一定必传
				String action_sub_code = statusModel.getAction_sub_code()==null?"": statusModel.getAction_sub_code();//变更事件原因码
				String action_detail = statusModel.getAction_detail()==null?"":statusModel.getAction_detail();//变更事件说明
				
				switch (order_status) {
				case "created":
					delstatus = "0";//已下单
					break;
				case "dispatched":
					delstatus = "1";//已接单
					break;
				case "arrived":
					delstatus = "6";//已到店
					break;
				case "obtained":
					delstatus = "2";//已离店
					break;
				case "completed":
					delstatus = "3";//已完成
					break;
				case "abnormal":
					delstatus = "4";//异常（完结）
					break;
				case "canceled":
					delstatus = "4";//异常（完结）
					if(action_sub_code.equals("rider_at_merchant_request"))//骑手取消订单 - 商家要求取消
					{
						delstatus = "5";
					}
					
					break;
					
				default:
					break;
		
				}
				
			 
				String delName = statusModel.getRider_name()==null?"":statusModel.getRider_name();//配送员姓名
				
				String deltElephone=statusModel.getRider_mobile()==null?"":statusModel.getRider_mobile();
				//配送员姓名
				if(delName!=null&&!delName.trim().isEmpty()){
					ub1.addUpdateValue("DELNAME", new DataValue(delName, Types.VARCHAR));
					opName+=" 骑士："+delName.toString();
				}
				//配送员手机号
				if(deltElephone!=null&&!deltElephone.trim().isEmpty()){
					ub1.addUpdateValue("DELTELEPHONE", new DataValue(deltElephone, Types.VARCHAR));
				}
				
				String memo = "";
				if(action_detail.trim().isEmpty()==false)
				{
					memo = action_detail;
				}
				
				ssres = "{\"code\":\"success\"}";
				
				HelpTools.save(this.dao,orderNO, deltype, "2", delstatus, deltElephone, opName, memo);
				
				
			}
			
      //用于测试点我达接口
			if(wuliutype.equals("DIANWODATEST"))
			{
				String IsSandBox = "Y";
				String appKey = obj.get("appKey").toString();
				String appSecret = obj.get("appSecret").toString();
				String accessToken = obj.get("accessToken").toString();			
				String api =  obj.get("api").toString();
				String apiJson = obj.get("apiJson").toString();
				
				try {
					IsSandBox = obj.optString("isTest");
		
				} catch (Exception e) {
			// TODO: handle exception
		}
				StringBuilder errorStr = new StringBuilder();
				if(api.equals("dianwoda.order.create"))
				{
					
					
					DianwodaOrderCreateParam param = JSON.parseObject(apiJson,DianwodaOrderCreateParam.class);
					param.setOrder_create_time(System.currentTimeMillis());
					/*DianwodaOrderCreateParam param = new DianwodaOrderCreateParam();
					param.setOrder_original_id(apiJson_obj.get("order_original_id").toString());
					param.setOrder_create_time(System.currentTimeMillis());
					------金额信息------
					param.setOrder_price(5000);
					------商家信息------
					param.setCity_code("330100");
					param.setSeller_id(apiJson_obj.get("seller_id").toString());
					param.setSeller_lat(30.2764454);
					param.setSeller_lng(120.111227);
					
					------客人信息------
					param.setConsignee_lat(30.2764454);
					param.setConsignee_lng(120.111227);
					------订单货品信息------
					param.setCargo_type("0801");
					param.setCargo_weight(1000);
					param.setCargo_num(1);
					*/
				  DianwodaOrderCreateResult res_model =	DianwodaService.OrderCreate(appKey, appSecret, accessToken, IsSandBox, param, errorStr);
					
				  return;
				}
				
		    String result =	DianwodaHttpClientUtil.sendPostRequest(appKey, appSecret, accessToken, IsSandBox, api, apiJson);	   
		    HelpTools.writelog_fileName("点我达城市代码返回："+result, "dianwodaCityCode");
		    
		    DianwodaClientResponse clientRes = JSON.parseObject(result,DianwodaClientResponse.class);
		    String res_code = clientRes.getCode();
		    String res_data= clientRes.getData();
				if (api.equals("dianwoda.data.city.code")) {
					DianwodaDataCityCodeResult codeRes = JSON.parseObject(res_data, DianwodaDataCityCodeResult.class);
					List<DianwodaDataCityCodeItem> codeList = codeRes.getCities();
				}
			
		    return;
			}

			// 人人
			if (wuliutype.equals("RRKD")) {
				deltype = "5";
				orderNO = obj.get("businessNo").toString();
				delorderno = obj.get("orderNo").toString();
				delstatus = obj.get("msgType").toString();
				if (delstatus.equals("4") || delstatus.equals("5") || delstatus.equals("6") || delstatus.equals("7")
						|| delstatus.equals("8") || delstatus.equals("9")) {
					delstatus = "4";
				}
				JSONObject resobject = new JSONObject();
				resobject.put("status", "success");
				ssres = resobject.toString();
			}
			// 达达
			if (wuliutype.equals("DADA")) {
				deltype = ThirdpartConstants.dada_deliveryType;
				orderNO = obj.get("order_id").toString();
				//client_id	String	是	返回达达运单号，默认为空
				delorderno = obj.get("client_id").toString();
				String order_status = obj.get("order_status").toString();//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 
				//来自于达达开放平台
				//order_status	int	是	订单状态(待接单＝1,待取货＝2,配送中＝3,
				//已完成＝4,已取消＝5, 已过期＝7,指派单=8,妥投异常之物品返回中=9, 
				//妥投异常之物品返回完成=10,骑士到店=100,创建达达运单失败=1000 可参考文末的状态说明）
				if (order_status.equals("1")) {
					delstatus = "0";
				} else if (order_status.equals("2")) {
					delstatus = "1";
				} else if (order_status.equals("3")) {
					delstatus = "2";
					isNeedOrderToSale = true;
				} else if (order_status.equals("4")) {
					delstatus = "3";
				} else if (order_status.equals("5")) {
					delstatus = "5";
				} else if (order_status.equals("8")) {//指派单=8
					delstatus = "1";//1 接单
				} else if (order_status.equals("100")) {
					delstatus = "6";
				} else {//已过期＝7 妥投异常之物品返回中=9, 	//妥投异常之物品返回完成=10
					//4=物流取消或异常
					delstatus = "4";
				}

				String opName="达达配送";
				String cancelReason = "";
				if (obj.has("cancel_reason"))
				{
					cancelReason = obj.get("cancel_reason").toString();
				}

				//dm_name	String	否	配送员姓名，接单以后会传
				String delName= "";
				if(obj.has("dm_name"))
				{
					delName=obj.get("dm_name").toString();
				}
				
				//dm_mobile	String	否	配送员手机号，接单以后会传
				String deltElephone ="";
				if(obj.has("dm_mobile"))
				{
					deltElephone=obj.get("dm_mobile").toString();
				}
				
				//配送员姓名
				if(delName!=null&&!delName.toString().trim().isEmpty()){
					ub1.addUpdateValue("DELNAME", new DataValue(delName, Types.VARCHAR));
					opName+=" 骑士："+delName.toString();
				}
				//配送员手机号
				if(deltElephone!=null&&!deltElephone.toString().trim().isEmpty()){
					ub1.addUpdateValue("DELTELEPHONE", new DataValue(deltElephone, Types.VARCHAR));
				}
				//配送单号
				if(delorderno!=null&&!delorderno.trim().isEmpty()){
					ub1.addUpdateValue("DELIVERYNO", new DataValue(delorderno, Types.VARCHAR));
				}
				JSONObject resobject = new JSONObject();
				resobject.put("status", "success");
				ssres = resobject.toString();

				//HelpTools.save(this.dao,orderNO, deltype, "2", delstatus, deltElephone, opName, "");
				ThirdpartService ts=new ThirdpartService();
				ts.save(this.dao,orderNO, deltype, "2", delstatus, deltElephone, opName, cancelReason);
			}
			//圆通
			if (wuliutype.equals("YTO"))
			{
				deltype = ThirdpartConstants.yto_deliveryType;
				orderNO = obj.get("txLogisticID").toString();
				// 运单号 String 运单号，默认为空
				delorderno = obj.optString("mailNo", "");
				
				String logisticProviderID = obj.optString("logisticProviderID", "YTO");
            	String txLogisticID = orderNO;
            	
            	JSONObject resobject = new JSONObject();
				resobject.put("logisticProviderID", logisticProviderID);
				resobject.put("txLogisticID", txLogisticID);
				resobject.put("success", true);				
				ssres = resobject.toString();

				String order_status = obj.get("infoContent").toString();
				String otherDes = "";
				// 圆通对应物流状态:ACCEPT 接单;GOT 已收件;NOT_SEND 揽收失败;ARRIVAL
				// 已收入;DEPARTURE 已发出;PACKAGE 已打包;SENT_SCAN 派件;INBOUND
				// 自提柜入柜;SIGNED 签收成功;FAILED 签收失败
				// 订单中心物流状态：-1预下单 0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销 6 到店
				// 7重下单
				if (order_status.equals("ACCEPT"))
				{
					delstatus = "1";
				} else if (order_status.equals("GOT"))
				{
					delstatus = "2";	
					isNeedOrderToSale = true; 
				} else if (order_status.equals("ARRIVAL"))
				{
					delstatus = "2";
					otherDes = "已收入";
				} else if (order_status.equals("DEPARTURE"))
				{
					delstatus = "2";
					otherDes = "已发出";
				} else if (order_status.equals("PACKAGE"))
				{
					delstatus = "2";
					otherDes = "已打包";
				} else if (order_status.equals("SENT_SCAN"))
				{
					delstatus = "2";
					otherDes = "派件中";
				} else if (order_status.equals("INBOUND"))
				{
					delstatus = "2";
					otherDes = "自提柜入库";
				} else if (order_status.equals("NOT_SEND"))
				{
					delstatus = "4";
					otherDes = "揽收失败";
				} else if (order_status.equals("SIGNED"))
				{
					delstatus = "3";
					otherDes = "签收成功";
				} else if (order_status.equals("FAILED"))
				{
					delstatus = "4";
					otherDes = "签收失败";
				} else
				{
					delstatus = "";
				}

				String opName = "圆通配送";
				String remark = "";

				if (obj.has("remark"))
				{
					remark = obj.get("remark").toString();
				}
				
				if(!remark.isEmpty())
				{
					otherDes = remark;
				}

			
				String delName = obj.optString("deliveryName", "");				
				String deltElephone = obj.optString("contactInfo", "");
				// 配送员姓名
				if (delName != null && !delName.toString().trim().isEmpty())
				{
					ub1.addUpdateValue("DELNAME", new DataValue(delName, Types.VARCHAR));
					opName += " 骑士：" + delName.toString();
				}
				// 配送员手机号
				if (deltElephone != null && !deltElephone.toString().trim().isEmpty())
				{
					ub1.addUpdateValue("DELTELEPHONE", new DataValue(deltElephone, Types.VARCHAR));
				}
				// 配送单号
				if (delorderno != null && !delorderno.trim().isEmpty())
				{
					ub1.addUpdateValue("DELIVERYNO", new DataValue(delorderno, Types.VARCHAR));
				}
				

				// HelpTools.save(this.dao,orderNO, deltype, "2", delstatus,
				// deltElephone, opName, "");
				ThirdpartService ts = new ThirdpartService();
				ts.save(this.dao, orderNO, deltype, "2", delstatus, deltElephone, opName, otherDes);
			}
			// 百度
			if (wuliutype.equals("BAIDUJD")) {
				deltype = "3";
				orderNO = obj.get("out_order_id").toString();
				delorderno = obj.get("order_id").toString();
				delstatus = "1";

				JSONObject resobject = new JSONObject();
				resobject.put("error_no", 0);
				ssres = resobject.toString();
			}
			// 百度
			if (wuliutype.equals("BAIDUQC")) {
				deltype = "3";
				orderNO = obj.get("out_order_id").toString();
				delorderno = obj.get("order_id").toString();
				delstatus = "2";

				JSONObject resobject = new JSONObject();
				resobject.put("error_no", 0);
				ssres = resobject.toString();
			}
			// 百度
			if (wuliutype.equals("BAIDUWC")) {
				deltype = "3";
				orderNO = obj.get("out_order_id").toString();
				delorderno = obj.get("order_id").toString();
				delstatus = "3";

				JSONObject resobject = new JSONObject();
				resobject.put("error_no", 0);
				ssres = resobject.toString();
			}
			// 闪送
			if (wuliutype.equals("SHANSONG")) {
				//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单
				// status 状态 20 派单中（转单改派中） 30 配送员已经接单  40 取件，配送中 50 配送完成 60 取消订单
				deltype = "6";
				orderNO = obj.get("orderNo").toString();
				delorderno = obj.get("issOrderNo").toString();
				delstatus = obj.get("status").toString();
				if (delstatus.equals("20")) {
					delstatus = "0";
				} else if (delstatus.equals("30")) {
					delstatus = "1";
				} else if (delstatus.equals("40")) {
					delstatus = "2";
				} else if (delstatus.equals("50")) {
					delstatus = "3";
				} else if (delstatus.equals("60")) {
					delstatus = "4";
				}  else {
					// 给的状态没有就抛异常
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "状态匹配失败！");
				}
				JSONObject resobject = new JSONObject();
				resobject.put("status", 200);
				ssres = resobject.toString();

				deltype = ThirdpartConstants.ss_deliveryType;
				String opName="闪送跑腿";
				String deltElephone = "";
				String abortReason = "";
				if (obj.has("abortReason"))
				{
					abortReason = obj.get("abortReason").toString();
				}

				if(obj.has("courier")){
					JSONObject courier = obj.getJSONObject("courier");
					//name	string	空	是	配送员姓名
					String delName=courier.optString("name");
					//mobile	string	空	是	配送员电话
					 deltElephone=courier.optString("mobile");

					//配送员姓名
					if(delName!=null&&!delName.toString().trim().isEmpty()){
						ub1.addUpdateValue("DELNAME", new DataValue(delName, Types.VARCHAR));
						opName+=" 骑士："+delName.toString();
					}
					//配送员手机号
					if(deltElephone!=null&&!deltElephone.toString().trim().isEmpty()){
						ub1.addUpdateValue("DELTELEPHONE", new DataValue(deltElephone, Types.VARCHAR));
					}
				}

				ThirdpartService ts = new ThirdpartService();
				ts.save(this.dao, orderNO, deltype, "2", delstatus, deltElephone, opName, abortReason);

			}

			// 闪送
			if (wuliutype.equals("SHANSONGPS")) {
				deltype = "2";
				orderNO = obj.get("shop_order_id").toString();
				delorderno = obj.get("sf_order_id").toString();
				delstatus = obj.get("order_status").toString();
				if (delstatus.equals("10")) {
					delstatus = "1";
				}
				if (delstatus.equals("12")) {
					delstatus = "6";
				}
				if (delstatus.equals("15")) {
					delstatus = "2";
				}
				JSONObject resobject = new JSONObject();
				resobject.put("error_no", 0);
				ssres = resobject.toString();
			}
			// 闪送
			if (wuliutype.equals("SHANSONGWC")) {
				deltype = "2";
				orderNO = obj.get("shop_order_id").toString();
				delorderno = obj.get("sf_order_id").toString();
				delstatus = obj.get("order_status").toString();
				if (delstatus.equals("17")) {
					delstatus = "3";
				}
				JSONObject resobject = new JSONObject();
				resobject.put("error_no", 0);
				ssres = resobject.toString();
			}
			// 闪送
			if (wuliutype.equals("SHANSONGQX")) {
				deltype = "2";
				orderNO = obj.get("shop_order_id").toString();
				delorderno = obj.get("sf_order_id").toString();
				delstatus = obj.get("order_status").toString();
				if (delstatus.equals("2")) {
					delstatus = "4";
				}
				JSONObject resobject = new JSONObject();
				resobject.put("error_no", 0);
				ssres = resobject.toString();
			}
			//顺丰同城  配送状态更改
			if (wuliutype.equals("SFTC")) {

				JSONObject resobject = new JSONObject();
				resobject.put("error_code", 0);
				resobject.put("error_msg", "success");
				ssres = resobject.toString();

				deltype = ThirdpartConstants.sftc_deliveryType;
				if (obj.has("shop_order_id"))
				{
					orderNO = obj.get("shop_order_id").toString();
				}
				//delorderno = obj.get("sf_order_id").toString();
				//String statusDesc = obj.optString("status_desc");
				//顺丰同城开发平台状态
				//order_status  1-订单创建;2-订单取消;10-配送员确认;12:配送员到店;15:配送员配送中;17-订单完成;22-配送员撤单;28-可疑单被据单
				if (obj.has("order_status"))
				{
					delstatus = obj.get("order_status").toString();
				}
				//订单中心 DeliveryStutas 物流配送状态
				//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 
				if (delstatus.equals("10")) {
					delstatus = "1";
				}else if (delstatus.equals("12")) {
					delstatus = "6";
				}else if (delstatus.equals("15")) {
					delstatus = "2";
					isNeedOrderToSale = true;
				}
				else if (delstatus.equals("17")) {
					delstatus = "3";
				}
				else if (delstatus.equals("2")) {
					delstatus = "4";
				}
				else if (delstatus.equals("28")) {
					delstatus = "4";
				}
				else if (delstatus.equals("22")) {
					delstatus = "4";
				}

				String opName="顺丰同城";
				String cancelReason = "";
				if (obj.has("cancel_reason"))
				{
					cancelReason = obj.get("cancel_reason").toString();
				}
				/**************当订单发生异常操作的时候，顺丰同城会将订单相关的异常进行回调通知**********************/
				//由于没有返回 order_status节点
				String url_index = "";
				if (obj.has("url_index"))
				{
					url_index = obj.get("url_index").toString();
				}
				if ("rider_exception".equals(url_index))
				{
					delstatus = "4";

					if (obj.has("ex_content"))
					{
						cancelReason = obj.get("ex_content").toString();
					}
				}

				//operator_name	string	空	是	配送员姓名	 
				String delName=obj.optString("operator_name");
				//operator_phone	string	空	是	配送员电话
				String deltElephone=obj.optString("operator_phone");
				//配送员姓名
				if(delName!=null&&!delName.toString().trim().isEmpty()){
					ub1.addUpdateValue("DELNAME", new DataValue(delName, Types.VARCHAR));
					opName+=" 骑士："+delName.toString();
				}
				//配送员手机号
				if(deltElephone!=null&&!deltElephone.toString().trim().isEmpty()){
					ub1.addUpdateValue("DELTELEPHONE", new DataValue(deltElephone, Types.VARCHAR));
				}
				ThirdpartService ts = new ThirdpartService();
				ts.save(this.dao, orderNO, deltype, "2", delstatus, deltElephone, opName, cancelReason);
			}
			//https://peisong.meituan.com/open/doc#section2-5
			//美团跑腿 订单状态回调
			if ("MTPAOTUI".equals(wuliutype)||"MTPAOTUIA".equals(wuliutype)) {
				try{
					//deliveryType
					deltype = ThirdpartConstants.pt_deliveryType;
					orderNO = obj.optString("order_id");
					delorderno = obj.optString("mt_peisong_id");
					
					//配送单号
					if(delorderno!=null&&delorderno.trim().length()>0){
						ub1.addUpdateValue("DELIVERYNO", new DataValue(delorderno, Types.VARCHAR));
					}
					
					String opName="美团跑腿";
					
					//配送员姓名（已接单，已取货状态的订单，配送员信息可能改变）
					String delName=obj.optString("courier_name");
					//配送员电话（已接单，已取货状态的订单，配送员信息可能改变）
					String deltElephone=obj.optString("courier_phone");
					//配送员姓名
					if(delName!=null&&!delName.toString().trim().isEmpty()){
						ub1.addUpdateValue("DELNAME", new DataValue(delName, Types.VARCHAR));
						opName+=" 骑士："+delName.toString();
					}
					//配送员手机号
					if(deltElephone!=null&&!deltElephone.toString().trim().isEmpty()){
						ub1.addUpdateValue("DELTELEPHONE", new DataValue(deltElephone, Types.VARCHAR));
					}
					
					//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 
					delstatus = obj.optString("status","");
					//状态代码，可选值为 0：待调度 20：已接单 30：已取货 50：已送达 99：已取消
					if("0".equals(delstatus)){ //0：待调度
						delstatus="0";//0 已下单
					}else if("20".equals(delstatus)){//20：已接单
						delstatus="1";//1 接单
					}else if("30".equals(delstatus)){//30：已取货
						delstatus="2";//2=取件
					}else if("50".equals(delstatus)){//50：已送达
						delstatus="3";//3=签收
					}else if("99".equals(delstatus)){//99：已取消
						delstatus="5";//5=手动撤销    3.0展示"客服取消订单"
					}
					String cancelReason = obj.optString("cancel_reason");

					//增加兼容配送异常的回调，防止
					if (delstatus==null||delstatus.isEmpty())
					{
						String exception_id = obj.optString("exception_id","");
						String exception_code = obj.optString("exception_code","");

						if (!exception_id.isEmpty()&&!exception_code.isEmpty())
						{
							delstatus="4";//异常
							cancelReason = obj.optString("exception_descr","");
						}

					}
					
					
					JSONObject resobject = new JSONObject();
					resobject.put("code", 0);
					ssres = resobject.toString();
					ThirdpartService ts=new ThirdpartService();
					ts.save(this.dao,orderNO, deltype, "2", delstatus, deltElephone, opName, cancelReason);
					
				}catch(Exception e){
					JSONObject resobject = new JSONObject();
					resobject.put("code", -1);
					ssres = resobject.toString();
				}
			}
			
			//美团跑腿 订单异常回调
			if ("MTPAOTUIB".equals(wuliutype)) {
				try{
					//deliveryType
					deltype = ThirdpartConstants.pt_deliveryType;
					orderNO = obj.optString("order_id");
					delorderno = obj.optString("mt_peisong_id");
					
					String opName="美团跑腿";
					
					//配送员姓名（已接单，已取货状态的订单，配送员信息可能改变）
					String delName=obj.optString("courier_name");
					//配送员电话（已接单，已取货状态的订单，配送员信息可能改变）
					String deltElephone=obj.optString("courier_phone");
					//配送员姓名
					if(delName!=null&&!delName.toString().trim().isEmpty()){
						ub1.addUpdateValue("DELNAME", new DataValue(delName, Types.VARCHAR));
						opName+=" 骑士："+delName.toString();
					}
					//配送员手机号
					if(deltElephone!=null&&!deltElephone.toString().trim().isEmpty()){
						ub1.addUpdateValue("DELTELEPHONE", new DataValue(deltElephone, Types.VARCHAR));
					}
					
					//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 
					delstatus = "4";//4=物流取消或异常  3.0展示"超时"
					String cancelReason = obj.optString("exception_descr");
					
					JSONObject resobject = new JSONObject();
					resobject.put("code", 0);
					
					ThirdpartService ts=new ThirdpartService();
					ts.save(this.dao,orderNO, deltype, "2", delstatus, deltElephone, opName, cancelReason);
					ssres = resobject.toString();
				}catch(Exception e){
					/*ThirdpartService ts=new ThirdpartService();
					ts.writelogFileName(LogFileName,ts.getTrace(e));*/
					HelpTools.writelog_fileName(e.getMessage(),LogFileName);
					JSONObject resobject = new JSONObject();
					resobject.put("code", -1);
					ssres = resobject.toString();
				}
			}

			//快递鸟全国配送
			if ("KDNQGP".equals(wuliutype))
			{
				/*{
			    "RequestData":JSON格式
			    "DataSign":"NDJjYTAyNTFhNmZkNDM3ZTk5ZGY5MDE1ZjE1YjY0YWU=",
			    "RequestType":103
				}*/
				deltype = ThirdpartConstants.kdn_deliveryType;
				boolean isNeedNotifyCrm = false;//商城的订单，是否需要通知商城
				String channelId = "";//订单的渠道编码
				String loadDocType = "";//订单的渠道类型
				String shippingShop = "";//配送门店
				String eId = "";

				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				JSONObject resobject = new JSONObject();
				resobject.put("EBusinessID", "");
				resobject.put("Success", true);
				resobject.put("UpdateTime", df.format(new Date()));
				resobject.put("Reason", "成功");

				String RequestDataStr = obj.optString("RequestData");
				JSONObject RequestData = new JSONObject(RequestDataStr);

				String EBusinessID = RequestData.optString("EBusinessID","");
				resobject.put("EBusinessID", EBusinessID);
				resobject.put("UpdateTime", df.format(new Date()));
				ssres = resobject.toString();
				//只会有一条记录
				JSONArray Data = RequestData.optJSONArray("Data");
				JSONObject dataObj = Data.getJSONObject(0);
				String OrderCode = dataObj.optString("OrderCode");
				String State = dataObj.optString("State");
				String CreateTime = dataObj.optString("CreateTime");//推送时间，格式：yyyy-MM-dd HH:mm:ss
				String Reason = dataObj.optString("Reason","");
				String ShipperCode = dataObj.optString("ShipperCode","");//快递公司编码
				String LogisticCode = dataObj.optString("LogisticCode","");//运单号
				String PickupCode = "";
				if (OrderCode==null||OrderCode.isEmpty())
				{
					return;
				}
				String ShipperCodeName = "";
				if (ShipperCode!=null&&!ShipperCode.trim().isEmpty())
				{
					ShipperCodeName = this.getExpressTypeName(ShipperCode);
				}
				Map<String,Object> orderDeliveryMap = this.getOrderNoByRefDeliveryNo(OrderCode,deltype);
				if (orderDeliveryMap==null||orderDeliveryMap.isEmpty())
				{
					HelpTools.writelog_fileName("【快递鸟物流回调消息】根据推送的商家物流订单号OrderCode="+OrderCode+",查询获取对应的订单信息为空！",LogFileName);
					return;
				}
				orderNO = orderDeliveryMap.getOrDefault("ORDERNO","").toString();
				loadDocType = orderDeliveryMap.getOrDefault("LOADDOCTYPE","").toString();
				channelId = orderDeliveryMap.getOrDefault("CHANNELID","").toString();
				shippingShop = orderDeliveryMap.getOrDefault("SHIPPINGSHOP","").toString();
				eId = orderDeliveryMap.getOrDefault("EID","").toString();
				HelpTools.writelog_fileName("【快递鸟物流回调消息】根据推送的商家物流订单号OrderCode="+OrderCode+",查询获取对应的订单单号orderNo="+orderNO+"，渠道类型loadDocType="+loadDocType+",渠道编码channelId="+channelId,LogFileName);
				if (orderNO==null||orderNO.isEmpty())
				{
					HelpTools.writelog_fileName("【快递鸟物流回调消息】根据推送的商家物流订单号OrderCode="+OrderCode+",查询获取对应的订单单号orderNO为空！",LogFileName);
					return;
				}
				String otherDes = "";
				String opName="快递鸟物流";
				String delName = "";
				String deltElephone = "";
				UptBean ub2 = new UptBean("DCP_ORDER_DELIVERY");;
				ub2.addCondition("ORDERNO", new DataValue(orderNO, Types.VARCHAR));
				ub2.addCondition("REF_DELIVERYNO", new DataValue(OrderCode, Types.VARCHAR));

				if ("99".equals(State))
				{
					//5.1推送调度失败通知
					//快递鸟向快递公司下单失败，快递鸟推送下单失败状态给客户。
					delstatus = "4";
					otherDes = "调度失败";
				}
				else if ("102".equals(State))
				{
					//5.2推送网点信息
					//下单成功后，快递公司分配网点，快递鸟接收到网点信息后将网点信息推送给客户。
					delstatus = "1";
					otherDes = "分配网点信息";
					if (Reason!=null&&!Reason.isEmpty())
					{
						otherDes = Reason;
					}
					//快递员上门取件码
					try
					{

						JSONArray PickerInfoArray = dataObj.optJSONArray("PickerInfo");
						if (PickerInfoArray!=null&&PickerInfoArray.length()>0)
						{
							PickupCode = PickerInfoArray.getJSONObject(0).optString("PickupCode");
						}
					}
					catch (Exception e)
					{

					}

					if (PickupCode!=null&&!PickupCode.isEmpty())
					{
						otherDes = otherDes+"<br>快递员取件码:"+PickupCode;
						ub1.addUpdateValue("PICKUPCODE", new DataValue(PickupCode, Types.VARCHAR));

						ub2.addUpdateValue("PICKUPCODE", new DataValue(PickupCode, Types.VARCHAR));
					}
					isNeedNotifyCrm = true;

				}
				else if ("103".equals(State))
				{
					//5.3推送快递员信息
					//分配网点成功后，快递公司分配快递员，快递鸟接收到快递员信息后将快递员信息推送给客户。
					delstatus = "1";
					otherDes = "分配快递员";
					if (Reason!=null&&!Reason.isEmpty())
					{
						otherDes = Reason;
					}

					//快递员上门取件码
					try
					{

						JSONArray PickerInfoArray = dataObj.optJSONArray("PickerInfo");
						if (PickerInfoArray!=null&&PickerInfoArray.length()>0)
						{
							PickupCode = PickerInfoArray.getJSONObject(0).optString("PickupCode");
							delName = PickerInfoArray.getJSONObject(0).optString("PersonName");
							deltElephone = PickerInfoArray.getJSONObject(0).optString("PersonTel");
						}
					}
					catch (Exception e)
					{

					}

					if (PickupCode!=null&&!PickupCode.isEmpty())
					{
						if (!ShipperCodeName.isEmpty())
						{
							otherDes = otherDes+"<br>快递员公司:"+ShipperCodeName;
						}
						otherDes = otherDes+"<br>快递员取件码:"+PickupCode;
						ub1.addUpdateValue("PICKUPCODE", new DataValue(PickupCode, Types.VARCHAR));

						ub2.addUpdateValue("PICKUPCODE", new DataValue(PickupCode, Types.VARCHAR));
					}

					//配送员姓名
					if(delName!=null&&!delName.trim().isEmpty()){
						ub1.addUpdateValue("DELNAME", new DataValue(delName, Types.VARCHAR));
						opName+=" 骑士："+delName.toString();
					}
					//配送员手机号
					if(deltElephone!=null&&!deltElephone.toString().trim().isEmpty()){
						ub1.addUpdateValue("DELTELEPHONE", new DataValue(deltElephone, Types.VARCHAR));
					}
					isNeedNotifyCrm = true;

				}
				else if ("104".equals(State))
				{
					//5.4推送取件状态
					//分配快递员成功后，快递员上门取件，快递鸟接收到取件状态后推送给客户。
					//1.此状态是一个中间状态，取件的重量并不会作为结算重量，不能以此作为结算的依据，104只是一个取件动作说明。
					//2.此状态不是必推的，部分渠道没有此回调状态推送，平台无需强校验。
					delstatus = "6";
					otherDes = "快递员上门取件";
					if (Reason!=null&&!Reason.isEmpty())
					{
						otherDes = Reason;
					}
					if (!ShipperCodeName.isEmpty())
					{
						otherDes = otherDes+"<br>快递员公司:"+ShipperCodeName;
					}
					isNeedNotifyCrm = true;
				}
				else if ("301".equals(State))
				{
					//5.5推送揽件状态
					//快递员揽件后，快递鸟接收到揽件状态和重量后将状态及重量推送给客户。
					//快递员揽件后，快递鸟接收到揽件状态和重量后，将运费及重量信息推送给客户。
					//正常订单结算费用快递鸟和平台默认以state:301状态中TotalFee为准(指定申通下单产品除外，指定申通下单产品按state:601中的TotalFee进行结算)。
					delstatus = "2";
					otherDes = "已揽件";
					if (Reason!=null&&!Reason.isEmpty())
					{
						if ("订单已完成".equals(Reason))
						{
							//避免歧义，这个是物流推送过来可能
						}
						else
						{
							otherDes = Reason;
						}

					}
					String Cost = dataObj.optString("Cost");
					String TotalFee = dataObj.optString("TotalFee");
					if (Cost!=null&&!Cost.isEmpty())
					{
						try
						{
							BigDecimal delivery_fee = new BigDecimal(Cost).setScale(2,BigDecimal.ROUND_DOWN);

							ub1.addUpdateValue("DELIVERY_FEE", new DataValue(delivery_fee, Types.VARCHAR));
							//物流表
							ub2.addUpdateValue("DELIVERY_FEE", new DataValue(delivery_fee, Types.VARCHAR));

						}
						catch (Exception e)
						{

						}

					}
					if (TotalFee!=null&&!TotalFee.isEmpty())
					{
						try
						{
							BigDecimal delivery_totalfee = new BigDecimal(TotalFee).setScale(2,BigDecimal.ROUND_DOWN);

							ub1.addUpdateValue("DELIVERY_TOTALFEE", new DataValue(delivery_totalfee, Types.VARCHAR));
							//物流表
							ub2.addUpdateValue("DELIVERY_TOTALFEE", new DataValue(delivery_totalfee, Types.VARCHAR));

						}
						catch (Exception e)
						{

						}

					}
					isNeedOrderToSale = true;
					isNeedNotifyCrm = true;
				}
				else if ("109".equals(State))
				{
					//5.6推送调派通知
					//快递鸟更换快递公司后，将新快递公司信息推送给到客户。
					//收到此状态会更新订单的全部的信息，包括预约取件时间，快递公司编码，取件码，运单号等信息。
					delstatus = "";
					otherDes = "快递鸟调派";
					if (Reason!=null&&!Reason.isEmpty())
					{
						otherDes = Reason;
					}
					String OldShipperCode = dataObj.optString("OldShipperCode","");//快递公司编码
					String OldLogisticCode = dataObj.optString("OldLogisticCode","");//运单号
					if (OldShipperCode!=null&&!OldShipperCode.isEmpty())
					{
						otherDes +="<br>快递公司编码:"+OldShipperCode+this.getExpressTypeName(OldShipperCode)+"-->"+ShipperCode+ShipperCodeName;
					}
					if (OldLogisticCode!=null&&!OldLogisticCode.isEmpty())
					{
						otherDes +="<br>快递公司运单号:"+OldLogisticCode+"-->"+LogisticCode;
					}
					isNeedNotifyCrm = true;
				}
				else if ("203".equals(State))
				{
					//5.7推送取消状态
					//当订单无法服务时，快递公司将取消订单，同时将状态推送给到客户。
					delstatus = "4";
					otherDes = "物流取消";
					if (Reason!=null&&!Reason.isEmpty())
					{
						otherDes = Reason;
					}
					isNeedNotifyCrm = true;
				}
				else if ("206".equals(State))
				{
					//5.8推送虚假揽件状态
					//当订单无法服务时，快递公司将取消订单，同时将状态推送给到客户。
					delstatus = "4";
					otherDes = "虚假揽件";
					if (Reason!=null&&!Reason.isEmpty())
					{
						otherDes = Reason;
					}
				}
				else if ("302".equals(State))
				{
					//5.11推送更换运单号信息
					//当快递公司更换运单号后，快递鸟将新运单号推送给客户。
					//此状态会更新运单号，推送最新运单号给客户
					delstatus = "";
					otherDes = "更换运单号";
					if (Reason!=null&&!Reason.isEmpty())
					{
						otherDes = Reason;
					}
					otherDes +="<br>更新运单号:"+LogisticCode;
					isNeedNotifyCrm = true;
				}
				else if ("402".equals(State))
				{
					//5.14推送修改订单结果
					//寄件人主动修改订单基本信息，快递鸟将订单修改结果推送给客户。
					//state=402仅代表修改状态，修改是成功，还是失败，需要看402状态中的UpdateStatus字段，
					//UpdateStatus=0代表修改订单成功，修改成功，会更换新的KDN单号、接单公司，物流单号进行走件
					//UpdateStatus=1代表修改订单失败，修改失败，还是原KDN单号和接单公司进行走件
					delstatus = "";
					otherDes = "配送基本信息修改";
					if (Reason!=null&&!Reason.isEmpty())
					{
						otherDes = Reason;
					}
					String UpdateStatus = dataObj.optString("UpdateStatus","");
					String KdnOrderCodeNew = dataObj.optString("KdnOrderCodeNew","");
					if ("0".equals(UpdateStatus))
					{
						if (KdnOrderCodeNew!=null&&!KdnOrderCodeNew.isEmpty())
						{
							otherDes +="<br>更新快递鸟单号:"+KdnOrderCodeNew;
							ub1.addUpdateValue("DELIVERYNO", new DataValue(KdnOrderCodeNew, Types.VARCHAR));
                            //物流表
							ub2.addUpdateValue("OUT_DELIVERYNO", new DataValue(KdnOrderCodeNew, Types.VARCHAR));
						}
						if (ShipperCode!=null&&!ShipperCode.isEmpty())
						{
							otherDes +="<br>更新快递公司编码:"+ShipperCode+ShipperCodeName;
						}
						if (LogisticCode!=null&&!LogisticCode.isEmpty())
						{
							otherDes +="<br>更新运单号:"+LogisticCode;

							isNeedNotifyCrm = true;
						}
					}
					else
					{
						otherDes +="<br>修改失败";
					}
				}
				else if ("3".equals(State))
				{
					//5.15推送签收状态
					//订单签收后，快递鸟推送签收状态给客户。
					delstatus = "3";
					otherDes = "已签收";
					if (Reason!=null&&!Reason.isEmpty())
					{
						otherDes = Reason;
					}
					isNeedNotifyCrm = true;
				}
				else if ("2".equals(State))
				{
					//5.16推送在途中状态
					//订单发车产生第一条运输轨迹后，快递鸟会推送在途中状态给客户告知用户这个订单已经进入运输中。(运单的完整物流轨迹需要单独通过物流轨迹接口进行获取)
					delstatus = "2";
					otherDes = "配送中";
					if (Reason!=null&&!Reason.isEmpty())
					{
						otherDes = Reason;
					}
					isNeedNotifyCrm = true;
				}
				else if ("601".equals(State))
				{
					//5.18推送订单结算费用状态
					//1.客户订阅该状态后，推送订单的实际费用给客户。
					//仅支持指定申通下单产品（其他的产品在揽件节点state:301中推送结算费用）。
					//正常订单结算费用快递鸟和平台默认以state:301状态中TotalFee为准(指定申通下单产品除外，指定申通下单产品按state:601中的TotalFee进行结算)。
					delstatus = "";
					otherDes = "推送结算费用";
					if (Reason!=null&&!Reason.isEmpty())
					{
						if ("订单已完成".equals(Reason))
						{
							//避免歧义，这个是物流推送过来可能
						}
						else
						{
							otherDes = Reason;
						}

					}
					String Cost = dataObj.optString("Cost");
					String TotalFee = dataObj.optString("TotalFee");
					if (Cost!=null&&!Cost.isEmpty())
					{
						try
						{
							BigDecimal delivery_fee = new BigDecimal(Cost).setScale(2,BigDecimal.ROUND_DOWN);

							ub1.addUpdateValue("DELIVERY_FEE", new DataValue(delivery_fee, Types.VARCHAR));
							//物流表
							ub2.addUpdateValue("DELIVERY_FEE", new DataValue(delivery_fee, Types.VARCHAR));

						}
						catch (Exception e)
						{

						}

					}
					if (TotalFee!=null&&!TotalFee.isEmpty())
					{
						try
						{
							BigDecimal delivery_totalfee = new BigDecimal(TotalFee).setScale(2,BigDecimal.ROUND_DOWN);

							ub1.addUpdateValue("DELIVERY_TOTALFEE", new DataValue(delivery_totalfee, Types.VARCHAR));
							//物流表
							ub2.addUpdateValue("DELIVERY_TOTALFEE", new DataValue(delivery_totalfee, Types.VARCHAR));

						}
						catch (Exception e)
						{

						}

					}
				}
				else
				{
					HelpTools.writelog_fileName("【快递鸟物流回调消息】状态码State="+State+"无需处理,商家物流订单号OrderCode="+OrderCode+",对应的订单单号orderNO="+orderNO,LogFileName);
					return;
				}

				resobject.put("UpdateTime", df.format(new Date()));
				ssres = resobject.toString();

				//物流表
				ub2.addUpdateValue("STATE", new DataValue(State, Types.VARCHAR));
				ub2.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
				if(delstatus!=null&&delstatus.isEmpty()==false)
				{
					ub2.addUpdateValue("DELIVERYSTATUS", new DataValue(delstatus, Types.VARCHAR));
				}
				//快递公司编码
				if(ShipperCode!=null&&!ShipperCode.toString().trim().isEmpty()){
					//订单表
					ub1.addUpdateValue("SUBDELIVERYCOMPANYNO", new DataValue(ShipperCode, Types.VARCHAR));
					ub1.addUpdateValue("SUBDELIVERYCOMPANYNAME", new DataValue(ShipperCodeName, Types.VARCHAR));
					//物流表
					ub2.addUpdateValue("SHIPPERCODE", new DataValue(ShipperCode, Types.VARCHAR));
				}
				//配送员手机号
				if(LogisticCode!=null&&!LogisticCode.toString().trim().isEmpty()){
					//订单表
					ub1.addUpdateValue("LOGISTICSNO", new DataValue(LogisticCode, Types.VARCHAR));
					ub1.addUpdateValue("DELIVERYNO", new DataValue(LogisticCode, Types.VARCHAR));//更新下，后面订转销会传给商城
					//物流表
					ub2.addUpdateValue("LOGISTICSNO", new DataValue(LogisticCode, Types.VARCHAR));
				}

				this.addProcessData(new DataProcessBean(ub2));


				ThirdpartService ts = new ThirdpartService();
				ts.save(this.dao, orderNO, deltype, "2", delstatus, deltElephone, opName, otherDes);

				//通知下商城，更新运单号，用商城物流查询
				if (isNeedNotifyCrm)
				{
					if (orderLoadDocType.MINI.equals(loadDocType)||orderLoadDocType.WECHAT.equals(loadDocType)||orderLoadDocType.LINE.equals(loadDocType))
					{
						try
						{
							org.json.JSONObject js=new org.json.JSONObject();
							js.put("serviceId", "OrderStatusUpdate");
							js.put("orderNo", orderNO);
							js.put("statusType", "2");//状态类型 1=交易状态变更 2=物流状态变更 3=其他 4= 退单状态变更 5=推送状态变更 6=开票状态变更
							js.put("status", "1");//交易状态 0=未配送 1=配送中 2=已配送 3=确认收货 4=已取消 5=已下单6=已接单
							//delstatus中台物流状态 -1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单
							if ("1".equals(delstatus))
							{
								js.put("status", "6");
							}
							else if ("2".equals(delstatus)||"6".equals(delstatus))
							{
								js.put("status", "1");
							}
							else if ("3".equals(delstatus))
							{
								js.put("status", "2");
							}
							else if ("4".equals(delstatus))
							{
								js.put("status", "4");
							}
							js.put("description", otherDes);
							js.put("oprId", "admin");
							js.put("orgType", "2");
							js.put("orgId", shippingShop);
							js.put("updateTime", df.format(new Date()));

							if (LogisticCode!=null&&!LogisticCode.isEmpty())
							{
								org.json.JSONArray deliverInfo = new org.json.JSONArray();
								org.json.JSONObject body = new org.json.JSONObject();
								//body.put("expressType", deltype);//快递鸟 商城对应的字段是INT，暂时注释
								if (!"KDN".equals(deltype))
								{
									try
									{
										int deltype_i = Integer.parseInt(deltype);
										body.put("expressType", deltype);
									} catch (Exception e) {
									}
								}
								body.put("expressTypeCode", ShipperCode);//真正的物流公司编码
								body.put("expressTypeName", ShipperCodeName);//真正的物流公司名称
								body.put("expressBillNo", LogisticCode);
								body.put("deliverPerson", delName);
								body.put("deliverPhone", deltElephone);
								body.put("remark", otherDes);
								deliverInfo.put(body);

								js.put("deliverInfo", deliverInfo);
							}


							String req_crm = js.toString();
							HelpTools.writelog_fileName("【快递鸟物流回调消息】推送运单号,通知商城接口请求req:"+req_crm+",对应的订单单号orderNO="+orderNO,LogFileName);
							String result_crm = HttpSend.MicroMarkSend(req_crm, eId, "OrderStatusUpdate",channelId);
							HelpTools.writelog_fileName("【快递鸟物流回调消息】推送运单号,通知商城接口返回res:"+result_crm+",对应的订单单号orderNO="+orderNO,LogFileName);

						}
						catch (Exception e)
						{

						}


					}
				}

			}
			//快递鸟同城配送
			if ("KDNTCP".equals(wuliutype))
			{
				deltype = ThirdpartConstants.kdn_deliveryType;
				boolean isNeedNotifyCrm = false;//商城的订单，是否需要通知商城
				String channelId = "";//订单的渠道编码
				String loadDocType = "";//订单的渠道类型
				String shippingShop = "";//配送门店
				String eId = "";

				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				JSONObject resobject = new JSONObject();
				resobject.put("reason", "接收成功");
				resobject.put("resultCode", 100);
				resobject.put("responseTime", df.format(new Date()));
				resobject.put("kdnOrderCode", "");

				String RequestDataStr = obj.optString("requestData");
				JSONObject RequestData = new JSONObject(RequestDataStr);
				String kdnOrderCode = RequestData.optString("kdnOrderCode");
				resobject.put("kdnOrderCode", kdnOrderCode);
				ssres = resobject.toString();
				String orderCode = RequestData.optString("orderCode");
				String orderStatus = RequestData.optString("orderStatus");
				String requestTime = RequestData.optString("requestTime");//请求时间毫秒级时间戳
				String remark = RequestData.optString("remark","");
				String ShipperCode = RequestData.optString("intraCode","");//快递公司编码
				String LogisticCode = RequestData.optString("channelOrderCode","");//运单号
				String PickupCode = "";
				if (orderCode==null||orderCode.isEmpty())
				{
					return;
				}
				String ShipperCodeName = "";
				if (ShipperCode!=null&&!ShipperCode.trim().isEmpty())
				{
					ShipperCodeName = this.getTCExpressTypeName(ShipperCode);
				}
				Map<String,Object> orderDeliveryMap = this.getOrderNoByRefDeliveryNo(orderCode,deltype);
				if (orderDeliveryMap==null||orderDeliveryMap.isEmpty())
				{
					HelpTools.writelog_fileName("【快递鸟物流回调消息】根据推送的商家物流订单号OrderCode="+orderCode+",查询获取对应的订单信息为空！",LogFileName);
					return;
				}
				orderNO = orderDeliveryMap.getOrDefault("ORDERNO","").toString();
				loadDocType = orderDeliveryMap.getOrDefault("LOADDOCTYPE","").toString();
				channelId = orderDeliveryMap.getOrDefault("CHANNELID","").toString();
				shippingShop = orderDeliveryMap.getOrDefault("SHIPPINGSHOP","").toString();
				eId = orderDeliveryMap.getOrDefault("EID","").toString();
				HelpTools.writelog_fileName("【快递鸟物流回调消息】根据推送的商家物流订单号OrderCode="+orderCode+",查询获取对应的订单单号orderNo="+orderNO+"，渠道类型loadDocType="+loadDocType+",渠道编码channelId="+channelId,LogFileName);
				if (orderNO==null||orderNO.isEmpty())
				{
					HelpTools.writelog_fileName("【快递鸟物流回调消息】根据推送的商家物流订单号OrderCode="+orderCode+",查询获取对应的订单单号orderNO为空！",LogFileName);
					return;
				}
				String otherDes = "";
				String opName="快递鸟同城";
				String delName = "";
				String deltElephone = "";
				UptBean ub2 = new UptBean("DCP_ORDER_DELIVERY");;
				ub2.addCondition("ORDERNO", new DataValue(orderNO, Types.VARCHAR));
				ub2.addCondition("REF_DELIVERYNO", new DataValue(orderCode, Types.VARCHAR));

				if ("600".equals(orderStatus))
				{
					//4.2.1推送下单失败状态
					//代表即时配公司没有服务运力，订单无法配送，推送此状态。
					delstatus = "4";
					otherDes = "下单失败";
				}
				else if ("100".equals(orderStatus))
				{
					//4.2.2推送待接单状态
					//代表即时配公司下单成功，可正常进行配送，推送此状态
					delstatus = "1";
					otherDes = "待骑手接单";
					if (remark!=null&&!remark.isEmpty())
					{
						otherDes = remark;
					}
					//快递员上门取件码
					PickupCode = RequestData.optString("pickupCode","");
					if (PickupCode!=null&&!PickupCode.isEmpty())
					{
						otherDes = otherDes+"<br>快递员取件码:"+PickupCode;
						ub1.addUpdateValue("PICKUPCODE", new DataValue(PickupCode, Types.VARCHAR));

						ub2.addUpdateValue("PICKUPCODE", new DataValue(PickupCode, Types.VARCHAR));
					}

					String Cost = RequestData.optString("actualTotalFee");//实际支付的费用，单位：元（总金额-优惠费用-限时补贴）
					String TotalFee = RequestData.optString("totalAmount");//总费用，单位：元（常规配送费+加价费+拒收扣费+特殊时段费+增值费用总和—优惠总费用）
					if (Cost!=null&&!Cost.isEmpty())
					{
						try
						{
							BigDecimal delivery_fee = new BigDecimal(Cost).setScale(2,BigDecimal.ROUND_DOWN);

							ub1.addUpdateValue("DELIVERY_FEE", new DataValue(delivery_fee, Types.VARCHAR));
							//物流表
							ub2.addUpdateValue("DELIVERY_FEE", new DataValue(delivery_fee, Types.VARCHAR));

						}
						catch (Exception e)
						{

						}

					}
					if (TotalFee!=null&&!TotalFee.isEmpty())
					{
						try
						{
							BigDecimal delivery_totalfee = new BigDecimal(TotalFee).setScale(2,BigDecimal.ROUND_DOWN);

							ub1.addUpdateValue("DELIVERY_TOTALFEE", new DataValue(delivery_totalfee, Types.VARCHAR));
							//物流表
							ub2.addUpdateValue("DELIVERY_TOTALFEE", new DataValue(delivery_totalfee, Types.VARCHAR));

						}
						catch (Exception e)
						{

						}

					}
					isNeedNotifyCrm = true;

				}
				else if ("201".equals(orderStatus))
				{
					//4.2.3推送业务员接单状态
					//代表即时配已分配业务员进行订单配送，将业务员信息同步给平台，推送此状态
					delstatus = "1";
					otherDes = "分配快递员";
					if (remark!=null&&!remark.isEmpty())
					{
						otherDes = remark;
					}
					if (!ShipperCodeName.isEmpty())
					{
						otherDes = otherDes+"<br>快递员公司:"+ShipperCodeName;
					}
					//快递员上门取件码
					try
					{

						PickupCode = RequestData.optString("pickupCode","");
						JSONObject courierInfo = RequestData.optJSONObject("courierInfo");
						if (courierInfo!=null&&courierInfo.length()>0)
						{
							delName = courierInfo.optString("courierName");
							deltElephone = courierInfo.optString("courierMobile");
						}
					}
					catch (Exception e)
					{

					}

					if (PickupCode!=null&&!PickupCode.isEmpty())
					{

						otherDes = otherDes+"<br>快递员取件码:"+PickupCode;
						ub1.addUpdateValue("PICKUPCODE", new DataValue(PickupCode, Types.VARCHAR));
						ub2.addUpdateValue("PICKUPCODE", new DataValue(PickupCode, Types.VARCHAR));
					}

					//配送员姓名
					if(delName!=null&&!delName.trim().isEmpty()){
						ub1.addUpdateValue("DELNAME", new DataValue(delName, Types.VARCHAR));
						opName+=" 骑士："+delName.toString();
					}
					//配送员手机号
					if(deltElephone!=null&&!deltElephone.toString().trim().isEmpty()){
						ub1.addUpdateValue("DELTELEPHONE", new DataValue(deltElephone, Types.VARCHAR));
					}
					isNeedNotifyCrm = true;

				}
				else if ("202".equals(orderStatus))
				{
					//4.2.4推送业务员到达状态
					//代表业务员已到达取货地址，准备联系寄件人拿取包裹，推送此状态
					delstatus = "6";
					otherDes = "快递员已到店";
					if (remark!=null&&!remark.isEmpty())
					{
						otherDes = remark;
					}
					if (!ShipperCodeName.isEmpty())
					{
						otherDes = otherDes+"<br>快递员公司:"+ShipperCodeName;
					}

					//快递员上门取件码
					try
					{

						PickupCode = RequestData.optString("pickupCode","");
						JSONObject courierInfo = RequestData.optJSONObject("courierInfo");
						if (courierInfo!=null&&courierInfo.length()>0)
						{
							delName = courierInfo.optString("courierName");
							deltElephone = courierInfo.optString("courierMobile");
						}
					}
					catch (Exception e)
					{

					}

					if (PickupCode!=null&&!PickupCode.isEmpty())
					{
						otherDes = otherDes+"<br>快递员取件码:"+PickupCode;
						ub1.addUpdateValue("PICKUPCODE", new DataValue(PickupCode, Types.VARCHAR));

						ub2.addUpdateValue("PICKUPCODE", new DataValue(PickupCode, Types.VARCHAR));
					}

					//配送员姓名
					if(delName!=null&&!delName.trim().isEmpty()){
						ub1.addUpdateValue("DELNAME", new DataValue(delName, Types.VARCHAR));
						opName+=" 骑士："+delName.toString();
					}
					//配送员手机号
					if(deltElephone!=null&&!deltElephone.toString().trim().isEmpty()){
						ub1.addUpdateValue("DELTELEPHONE", new DataValue(deltElephone, Types.VARCHAR));
					}
					isNeedNotifyCrm = true;
				}
				else if ("300".equals(orderStatus))
				{
					//4.2.5推送配送中状态
					//代表业务员已拿到包裹，进行订单配送中，推送此状态
					delstatus = "2";
					otherDes = "配送中";
					if (remark!=null&&!remark.isEmpty())
					{
						if ("订单已完成".equals(remark))
						{
							//避免歧义，这个是物流推送过来可能
						}
						else
						{
							otherDes = remark;
						}
					}

					if (!ShipperCodeName.isEmpty())
					{
						otherDes = otherDes+"<br>快递员公司:"+ShipperCodeName;
					}

					try
					{
						JSONObject courierInfo = RequestData.optJSONObject("courierInfo");
						if (courierInfo!=null&&courierInfo.length()>0)
						{
							delName = courierInfo.optString("courierName");
							deltElephone = courierInfo.optString("courierMobile");
						}
					}
					catch (Exception e)
					{

					}


					//配送员姓名
					if(delName!=null&&!delName.trim().isEmpty()){
						ub1.addUpdateValue("DELNAME", new DataValue(delName, Types.VARCHAR));
						opName+=" 骑士："+delName.toString();
					}
					//配送员手机号
					if(deltElephone!=null&&!deltElephone.toString().trim().isEmpty()){
						ub1.addUpdateValue("DELTELEPHONE", new DataValue(deltElephone, Types.VARCHAR));
					}

					isNeedOrderToSale = true;
					isNeedNotifyCrm = true;
				}
				else if ("401".equals(orderStatus))
				{
					//4.2.6推送签收状态
					//代表业务员已将订单完成配送，流程终结，推送此状态
					delstatus = "3";
					otherDes = "已签收";
					if (remark!=null&&!remark.isEmpty())
					{
						otherDes = remark;
					}
					isNeedNotifyCrm = true;
				}
				else if ("500".equals(orderStatus))
				{
					//5.7推送取消状态
					//当订单无法服务时，快递公司将取消订单，同时将状态推送给到客户。
					delstatus = "4";
					otherDes = "物流取消";
					if (remark!=null&&!remark.isEmpty())
					{
						otherDes = remark;
					}
					String cancelReason = RequestData.optString("cancelReason");
					if (cancelReason!=null&&!cancelReason.trim().isEmpty())
					{
						otherDes = otherDes +"<br>"+cancelReason;
					}
					isNeedNotifyCrm = true;
				}
				else if ("206".equals(orderStatus))
				{
					//4.2.8推送订单费用
					//当客户订阅费用推送节点待取货、配送中、签收后，如果接收到对应订单状态，系统将会主动推送费用信息给客户
					delstatus = "";
					otherDes = "推送结算费用";
					if (remark!=null&&!remark.isEmpty())
					{
						otherDes = remark;
					}
					String Cost = RequestData.optString("actualTotalFee");//实际支付的费用，单位：元（总金额-优惠费用-限时补贴）
					String TotalFee = RequestData.optString("totalAmount");//总费用，单位：元（常规配送费+加价费+拒收扣费+特殊时段费+增值费用总和—优惠总费用）
					if (Cost!=null&&!Cost.isEmpty())
					{
						try
						{
							BigDecimal delivery_fee = new BigDecimal(Cost).setScale(2,BigDecimal.ROUND_DOWN);

							ub1.addUpdateValue("DELIVERY_FEE", new DataValue(delivery_fee, Types.VARCHAR));
							//物流表
							ub2.addUpdateValue("DELIVERY_FEE", new DataValue(delivery_fee, Types.VARCHAR));

						}
						catch (Exception e)
						{

						}

					}
					if (TotalFee!=null&&!TotalFee.isEmpty())
					{
						try
						{
							BigDecimal delivery_totalfee = new BigDecimal(TotalFee).setScale(2,BigDecimal.ROUND_DOWN);

							ub1.addUpdateValue("DELIVERY_TOTALFEE", new DataValue(delivery_totalfee, Types.VARCHAR));
							//物流表
							ub2.addUpdateValue("DELIVERY_TOTALFEE", new DataValue(delivery_totalfee, Types.VARCHAR));

						}
						catch (Exception e)
						{

						}

					}

				}
				else
				{
					HelpTools.writelog_fileName("【快递鸟物流(同城)回调消息】状态码orderStatus="+orderStatus+"无需处理,商家物流订单号OrderCode="+orderCode+",对应的订单单号orderNO="+orderNO,LogFileName);
					return;
				}

				resobject.put("responseTime", df.format(new Date()));
				ssres = resobject.toString();

				//物流表
				ub2.addUpdateValue("STATE", new DataValue(orderStatus, Types.VARCHAR));
				ub2.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
				if(delstatus!=null&&delstatus.isEmpty()==false)
				{
					ub2.addUpdateValue("DELIVERYSTATUS", new DataValue(delstatus, Types.VARCHAR));
				}
				//快递公司编码
				if(ShipperCode!=null&&!ShipperCode.toString().trim().isEmpty()){
					//订单表
					ub1.addUpdateValue("SUBDELIVERYCOMPANYNO", new DataValue(ShipperCode, Types.VARCHAR));
					ub1.addUpdateValue("SUBDELIVERYCOMPANYNAME", new DataValue(ShipperCodeName, Types.VARCHAR));
					//物流表
					ub2.addUpdateValue("SHIPPERCODE", new DataValue(ShipperCode, Types.VARCHAR));
				}
				//配送员手机号
				if(LogisticCode!=null&&!LogisticCode.toString().trim().isEmpty()){
					//订单表
					ub1.addUpdateValue("LOGISTICSNO", new DataValue(LogisticCode, Types.VARCHAR));
					ub1.addUpdateValue("DELIVERYNO", new DataValue(LogisticCode, Types.VARCHAR));//更新下，后面订转销会传给商城
					//物流表
					ub2.addUpdateValue("LOGISTICSNO", new DataValue(LogisticCode, Types.VARCHAR));
				}

				this.addProcessData(new DataProcessBean(ub2));


				ThirdpartService ts = new ThirdpartService();
				ts.save(this.dao, orderNO, deltype, "2", delstatus, deltElephone, opName, otherDes);

				//通知下商城，更新运单号，用商城物流查询
				if (isNeedNotifyCrm)
				{
					if (orderLoadDocType.MINI.equals(loadDocType)||orderLoadDocType.WECHAT.equals(loadDocType)||orderLoadDocType.LINE.equals(loadDocType))
					{
						try
						{
							org.json.JSONObject js=new org.json.JSONObject();
							js.put("serviceId", "OrderStatusUpdate");
							js.put("orderNo", orderNO);
							js.put("statusType", "2");//状态类型 1=交易状态变更 2=物流状态变更 3=其他 4= 退单状态变更 5=推送状态变更 6=开票状态变更
							js.put("status", "1");//交易状态 0=未配送 1=配送中 2=已配送 3=确认收货 4=已取消 5=已下单6=已接单
							//delstatus中台物流状态 -1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单
							if ("1".equals(delstatus))
							{
								js.put("status", "6");
							}
							else if ("2".equals(delstatus)||"6".equals(delstatus))
							{
								js.put("status", "1");
							}
							else if ("3".equals(delstatus))
							{
								js.put("status", "2");//2=已配送 已签收
							}
							else if ("4".equals(delstatus))
							{
								js.put("status", "4");//物流取消
							}
							js.put("description", otherDes);
							js.put("oprId", "admin");
							js.put("orgType", "2");
							js.put("orgId", shippingShop);
							js.put("updateTime", df.format(new Date()));

							if (LogisticCode!=null&&!LogisticCode.isEmpty())
							{
								org.json.JSONArray deliverInfo = new org.json.JSONArray();
								org.json.JSONObject body = new org.json.JSONObject();
								//body.put("expressType", deltype);//快递鸟 商城对应的字段是INT，暂时注释
								if (!"KDN".equals(deltype))
								{
									try
									{
										int deltype_i = Integer.parseInt(deltype);
										body.put("expressType", deltype);
									} catch (Exception e) {
									}
								}

								body.put("expressTypeCode", ShipperCode);//真正的物流公司编码
								body.put("expressTypeName", ShipperCodeName);//真正的物流公司名称
								body.put("expressBillNo", LogisticCode);
								body.put("deliverPerson", delName);
								body.put("deliverPhone", deltElephone);
								body.put("remark", otherDes);
								deliverInfo.put(body);

								js.put("deliverInfo", deliverInfo);
							}


							String req_crm = js.toString();
							HelpTools.writelog_fileName("【快递鸟物流回调消息】推送运单号,通知商城接口请求req:"+req_crm+",对应的订单单号orderNO="+orderNO,LogFileName);
							String result_crm = HttpSend.MicroMarkSend(req_crm, eId, "OrderStatusUpdate",channelId);
							HelpTools.writelog_fileName("【快递鸟物流回调消息】推送运单号,通知商城接口返回res:"+result_crm+",对应的订单单号orderNO="+orderNO,LogFileName);

						}
						catch (Exception e)
						{

						}


					}
				}

			}
			 //餐道回来
			if ("CANDAO".equals(wuliutype))
			{
				deltype = ThirdpartConstants.cangdao_deliveryType;
				boolean isNeedNotifyCrm = false;//商城的订单，是否需要通知商城
				String channelId = "";//订单的渠道编码
				String loadDocType = "";//订单的渠道类型
				String shippingShop = "";//配送门店
				String eId = "";
				JSONObject resobject = new JSONObject();
				resobject.put("status", 1);
				resobject.put("msg", "操作成功");
				ssres = resobject.toString();
				String actionName = obj.optString("actionName");
				if (!"candao.order.updateDeliveryStatus".equals(actionName))
				{
					HelpTools.writelog_fileName("【餐道回流回调】actionName"+actionName+",不是配送状态回调不做处理！",LogFileName);
					return ;
				}
				JSONObject RequestData = obj.getJSONObject("data");
				String orderId = RequestData.optString("orderId");//下单接口返回的，
				String extOrderId = RequestData.optString("extOrderId");//下单接口传入的对应订单表orderNo
				String extOrderNo = RequestData.optString("extOrderNo");//下单接口传入对应订单表orderNo
				orderNO = "";//dcp_order订单表orderNo
				if (extOrderNo!=null&&!extOrderNo.isEmpty())
				{
					orderNO = extOrderNo;
				}
				else
				{
					orderNO =  extOrderId;//目前是一样的，后面不一样，就需要查询下数据库了。
				}

				if (orderNO==null||orderNO.isEmpty())
				{
					Map<String,Object> orderDeliveryMap = null;
					if (orderId!=null&&!orderId.isEmpty())
					{
						orderDeliveryMap = this.getOrderNoByOutDeliveryNo(orderId,deltype);
					}

					if (orderDeliveryMap==null||orderDeliveryMap.isEmpty())
					{
						HelpTools.writelog_fileName("【餐道物流回调消息】商家物流单号extOrderId="+extOrderId+",根据推送的餐道物流订单号orderId="+orderId+",查询获取对应的订单信息为空！",LogFileName);
						return;
					}
					orderNO = orderDeliveryMap.getOrDefault("ORDERNO","").toString();
					loadDocType = orderDeliveryMap.getOrDefault("LOADDOCTYPE","").toString();
					channelId = orderDeliveryMap.getOrDefault("CHANNELID","").toString();
					shippingShop = orderDeliveryMap.getOrDefault("SHIPPINGSHOP","").toString();
					eId = orderDeliveryMap.getOrDefault("EID","").toString();
					HelpTools.writelog_fileName("【快递鸟物流回调消息】商家物流单号extOrderId="+extOrderId+",根据推送的餐道物流订单号orderId="+orderId+",查询获取对应的订单单号orderNo="+orderNO+"，渠道类型loadDocType="+loadDocType+",渠道编码channelId="+channelId,LogFileName);
				}
				if (orderNO==null||orderNO.isEmpty())
				{
					HelpTools.writelog_fileName("【快递鸟物流回调消息】商家物流单号extOrderId="+extOrderId+",根据推送的餐道物流订单号orderId="+orderId+",查询获取对应的订单单号orderNO为空！",LogFileName);
					return;
				}
				String otherDes = "";
				String opName="餐道配送";
				String delName = "";
				String deltElephone = "";
				UptBean ub2 = new UptBean("DCP_ORDER_DELIVERY");;
				ub2.addCondition("ORDERNO", new DataValue(orderNO, Types.VARCHAR));
				if (orderId!=null&&!orderId.isEmpty())
				{
					ub2.addCondition("OUT_DELIVERYNO", new DataValue(orderId, Types.VARCHAR));
				}
				if (extOrderId!=null&&!extOrderId.isEmpty())
				{
					ub2.addCondition("REF_DELIVERYNO", new DataValue(extOrderId, Types.VARCHAR));
				}


				String driverStatus = RequestData.optString("driverStatus");
				String deliverySysType = RequestData.optString("deliverySysType");
				String deliverySysName = RequestData.optString("deliverySysName");
				String deliveryOrderNo = RequestData.optString("deliveryOrderNo");//三方物流单号
				delName = RequestData.optString("riderName");
				deltElephone = RequestData.optString("riderPhone");
				String remark = RequestData.optString("remark");
				/**
				 * 骑手状态
				 * 1：待推送（用于配送系统不支持预约单情况下，开放平台保留订单延迟推送的状态）；
				 * 2：待接单；3：骑手系统已确认；5：已分配；6：已到店；7：配送中；10：已送达；15：已取消；
				 * 21：妥投异常收货；-7：妥投异常操作失败；22：妥投异常操作完成；23：待商家确认收货；24：商家确认收货；25：线下支付完成（吉野家定制）；
				 * -1：呼叫失败；-2：配送异常；-3：取消失败；4：待转派（15、-1，-2 需要重新呼叫骑手）；-4：分配超时；-5：取餐超时；-6：妥投异常；
				 * 8：转派中（表示DMS当前转派的第三方骑手公司失败，正转派下一家骑手公司 或 DMS出现配送异常，正转派其他骑手）；9：门店自送；-100：未知状态
				 */
				//我们的配送状态 -1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单
				if ("1".equals(driverStatus)||"2".equals(driverStatus)||"3".equals(driverStatus)||"5".equals(driverStatus))
				{
					delstatus = "1";
					otherDes = "待骑手接单";
					if ("1".equals(driverStatus))
					{
						otherDes = "待推送";
					}
					else if ("2".equals(driverStatus))
					{
						otherDes = "待接单";
					}
					else if ("3".equals(driverStatus))
					{
						otherDes = "骑手系统已确认";
					}
					else if ("5".equals(driverStatus))
					{
						otherDes = "已分配";
					}

					if (remark!=null&&!remark.isEmpty())
					{
						otherDes = remark;
					}
				}
				else if ("21".equals(driverStatus)||"22".equals(driverStatus)||"23".equals(driverStatus)||"24".equals(driverStatus))
				{
					delstatus = "";
					otherDes = "妥投异常";
					if ("21".equals(driverStatus))
					{
						otherDes = "妥投异常收货";
					}
					else if ("22".equals(driverStatus))
					{
						otherDes = "妥投异常操作完成";
					}
					else if ("23".equals(driverStatus))
					{
						otherDes = "待商家确认收货";
					}
					else if ("24".equals(driverStatus))
					{
						otherDes = "商家确认收货";
					}

					if (remark!=null&&!remark.isEmpty())
					{
						otherDes = remark;
					}
				}
				else if ("-3".equals(driverStatus)||"-4".equals(driverStatus)||"-5".equals(driverStatus)||"-6".equals(driverStatus)||"-7".equals(driverStatus))
				{
					//-3：取消失败；-4：分配超时；-5：取餐超时；-6：妥投异常；-7：妥投异常操作失败
					delstatus = "";
					otherDes = "异常";
					if ("-3".equals(driverStatus))
					{
						otherDes = "取消失败";
					}
					else if ("-4".equals(driverStatus))
					{
						otherDes = "分配超时";
					}
					else if ("-5".equals(driverStatus))
					{
						otherDes = "取餐超时";
					}
					else if ("-6".equals(driverStatus))
					{
						otherDes = "妥投异常";
					}
					else if ("-7".equals(driverStatus))
					{
						otherDes = "妥投异常操作失败";
					}
					if (remark!=null&&!remark.isEmpty())
					{
						otherDes = remark;
					}
				}
				else if ("-1".equals(driverStatus)||"-2".equals(driverStatus)||"15".equals(driverStatus))
				{
					//（15、-1，-2 需要重新呼叫骑手）
					delstatus = "4";
					otherDes = "下单失败";
					if (remark!=null&&!remark.isEmpty())
					{
						otherDes = remark;
					}
				}
				else if ("4".equals(driverStatus)||"8".equals(driverStatus)||"9".equals(driverStatus))
				{
					delstatus = "";
					otherDes = "";
					if ("4".equals(driverStatus))
					{
						otherDes = "待转派";
					}
					else if ("8".equals(driverStatus))
					{
						otherDes = "转派中";
					}
					else if ("9".equals(driverStatus))
					{
						otherDes = "门店自送";
					}
					if (remark!=null&&!remark.isEmpty())
					{
						otherDes = remark;
					}
				}
				else if ("6".equals(driverStatus))
				{
					delstatus = "6";
					otherDes = "骑手已到店";
					/*if (remark!=null&&!remark.isEmpty())
					{
						otherDes = remark;
					}*/
				}
				else if ("7".equals(driverStatus))
				{
					delstatus = "2";
					otherDes = "配送中";
					/*if (remark!=null&&!remark.isEmpty())
					{
						otherDes = remark;
					}*/
				}
				else if ("10".equals(driverStatus))
				{
					delstatus = "3";
					otherDes = "已送达";
				}
				else
				{
					delstatus = "";
					otherDes = "未知状态";
					HelpTools.writelog_fileName("【快递鸟物流回调消息】商家物流单号extOrderId="+extOrderId+",餐道物流订单号orderId="+orderId+",状态码driverStatus="+driverStatus+"，未知状态,无需处理,对应的订单单号orderNO="+orderNO,LogFileName);
					return;
				}

				//物流表
				ub2.addUpdateValue("STATE", new DataValue(driverStatus, Types.VARCHAR));//三方物流状态
				ub2.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
				if(delstatus!=null&&delstatus.isEmpty()==false)
				{
					ub2.addUpdateValue("DELIVERYSTATUS", new DataValue(delstatus, Types.VARCHAR));
				}

				//快递公司编码
				if(deliverySysType!=null&&!deliverySysType.toString().trim().isEmpty()){
					otherDes = otherDes+"<br>骑手配送平台:"+deliverySysType+deliverySysName;
					//订单表
					ub1.addUpdateValue("SUBDELIVERYCOMPANYNO", new DataValue(deliverySysType, Types.VARCHAR));
					ub1.addUpdateValue("SUBDELIVERYCOMPANYNAME", new DataValue(deliverySysName, Types.VARCHAR));
					//物流表
					ub2.addUpdateValue("SHIPPERCODE", new DataValue(deliverySysType, Types.VARCHAR));
				}
				//配送员手机号
				if(deliveryOrderNo!=null&&!deliveryOrderNo.toString().trim().isEmpty()){
					otherDes = otherDes+"<br>配送平台单号:"+deliveryOrderNo;
					//订单表
					ub1.addUpdateValue("LOGISTICSNO", new DataValue(deliveryOrderNo, Types.VARCHAR));
					//ub1.addUpdateValue("DELIVERYNO", new DataValue(LogisticCode, Types.VARCHAR));//更新下，后面订转销会传给商城
					//物流表
					ub2.addUpdateValue("LOGISTICSNO", new DataValue(deliveryOrderNo, Types.VARCHAR));
				}

				String TotalFee = RequestData.optString("fee");//配送系统配送费用（基础配送费+小费金额），单位：元
				if (TotalFee!=null&&!TotalFee.isEmpty())
				{
					try
					{
						BigDecimal delivery_totalfee = new BigDecimal(TotalFee).setScale(2,BigDecimal.ROUND_DOWN);

						ub1.addUpdateValue("DELIVERY_TOTALFEE", new DataValue(delivery_totalfee, Types.VARCHAR));
						//物流表
						ub2.addUpdateValue("DELIVERY_TOTALFEE", new DataValue(delivery_totalfee, Types.VARCHAR));

					}
					catch (Exception e)
					{

					}

				}

				this.addProcessData(new DataProcessBean(ub2));

				//配送员姓名
				if(delName!=null&&!delName.trim().isEmpty()){
					ub1.addUpdateValue("DELNAME", new DataValue(delName, Types.VARCHAR));
					opName+=" 骑手："+delName.toString();
				}
				//配送员手机号
				if(deltElephone!=null&&!deltElephone.toString().trim().isEmpty()){
					ub1.addUpdateValue("DELTELEPHONE", new DataValue(deltElephone, Types.VARCHAR));
				}

				ThirdpartService ts = new ThirdpartService();
				ts.save(this.dao, orderNO, deltype, "2", delstatus, deltElephone, opName, otherDes);

			}


			if(delstatus!=null&&delstatus.isEmpty()==false)
			{
				ub1.addUpdateValue("DELIVERYSTATUS", new DataValue(delstatus, Types.VARCHAR));
                if ("2".equals(delstatus))
                {
                    //物流状态变成开始配送中状态的时间 日期格式： 2020/11/1 16:36:40
                    ub1.addUpdateValue("DELIVERY_SENDTIME", new DataValue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), Types.DATE));
                }
			}
					
			ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
			ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));			
			ub1.addCondition("ORDERNO", new DataValue(orderNO, Types.VARCHAR));
			
			ub1.addCondition("DeliveryType", new DataValue(deltype, Types.VARCHAR));

			
			this.addProcessData(new DataProcessBean(ub1));
			this.doExecuteDataToDB();

			if(isNeedOrderToSale)
			{
				orderTosale(orderNO,deltype);
			}
			
			

		} catch (Exception e) {
			HelpTools.writelog_fileName("【物流回调WULIUCallBack】异常：" + e.getMessage() + "\r\n req请求内容:" + req,LogFileName);
		}

	}



	@Override
	protected List<InsBean> prepareInsertData(String req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(String req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(String req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	


	private void DeleteRedis(String redis_key, String hash_key) throws Exception {
		try {
			RedisPosPub redis = new RedisPosPub();
			HelpTools.writelog_waimai("【开始删除缓存】" + " redis_key:" + redis_key + " hash_key:" + hash_key);
			redis.DeleteHkey(redis_key, hash_key);//
			HelpTools.writelog_waimai("【删除存在hash_key的缓存】成功！" + " redis_key:" + redis_key + " hash_key:" + hash_key);
			//redis.Close();

		} catch (Exception e) {
			HelpTools.writelog_waimai(
					"【删除存在hash_key的缓存】异常" + e.getMessage() + " redis_key:" + redis_key + " hash_key:" + hash_key);
		}
	}



	/**
	 * 將 byte array 資料做 hash md5或 sha256 運算，並回傳 hex值的字串資料
	 * @param data
	 * @param isMD5
	 * @return string
	 */
	private String hash(byte data[], String mode)
	{
		MessageDigest md = null;
		try
		{
			if(mode == "MD5")
			{
				md = MessageDigest.getInstance("MD5");
			}
			else if(mode == "SHA-256")
			{
				md = MessageDigest.getInstance("SHA-256");
			}
		} 
		catch(NoSuchAlgorithmException e)
		{

		}

		byte[] bytes=md.digest(data);

		md = null;

		return bytesToHex(bytes);
	}

	private char[] hexArray = "0123456789ABCDEF".toCharArray();

	/**
	 * 將 byte array 資料轉換成 hex字串值
	 * @param bytes
	 * @return string
	 */
	private String bytesToHex(byte[] bytes) 
	{
		char[] hexChars = new char[bytes.length * 2];
		for ( int j = 0; j < bytes.length; j++ ) 
		{
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	/**
	 * 配送中，配送门店自动订转销
	 * @param orderNo
	 * @param deliveryType
	 * @throws Exception
	 */
	private void orderTosale (String orderNo,String deliveryType) throws Exception
	{
		HelpTools.writelog_fileName("【物流回调WULIUCallBack】物流类型(WULIUTYPE):" + wuliutype + ",订单上deliveryType="
				+ deliveryType + ",订单号orderNo=" + orderNo + "自动订单传销开始", LogFileName);
		try
		{
			String sqlTvOrder = "select * from DCP_ORDER where ORDERNO='" + orderNo + "' and DELIVERYTYPE='"
					+ deliveryType + "' ";
			List<Map<String, Object>> listTvOrder = this.dao.executeQuerySQL(sqlTvOrder, null);
			if (listTvOrder == null || listTvOrder.size() == 0)
			{
				listTvOrder = this.dao.executeQuerySQL("select * from DCP_ORDER where ORDERNO=? ", new String[]
				{ orderNo });
			}
			if (listTvOrder != null && listTvOrder.size() > 0)
			{

				// 门店
				String eId = listTvOrder.get(0).get("EID").toString();
				String shippingShop = listTvOrder.get(0).get("SHIPPINGSHOP").toString();
				String ORDERTOSALE_DATETIME = listTvOrder.get(0).getOrDefault("ORDERTOSALE_DATETIME","").toString();
				if (ORDERTOSALE_DATETIME==null||ORDERTOSALE_DATETIME.trim().isEmpty())
				{
					HelpTools.orderToSale(this.dao, eId, shippingShop, orderNo, LogFileName);

				}
				else
				{
					HelpTools.writelog_fileName("【物流回调WULIUCallBack】物流类型(WULIUTYPE):" + wuliutype + ",订单上deliveryType="
							+ deliveryType + ",订单号orderNo" + orderNo + "已订转销。", LogFileName);
				}


			} else
			{
				HelpTools.writelog_fileName("【物流回调WULIUCallBack】物流类型(WULIUTYPE):" + wuliutype + ",订单上deliveryType="
						+ deliveryType + ",订单号orderNo" + orderNo + "查无资料", LogFileName);
			}

		} catch (Exception e)
		{
			// TODO: handle exception
			HelpTools.writelog_fileName("【物流回调WULIUCallBack】物流类型(WULIUTYPE):" + wuliutype + ",订单上deliveryType="
					+ deliveryType + ",订单号orderNo=" + orderNo + "自动订单传销异常:" + e.getMessage(), LogFileName);

		}
	}

	/**
	 * 根据回调的商家物流单号查询对应的订单号
	 * @param ref_deliveryNo
	 * @param deliveryType
	 * @return
	 * @throws Exception
	 */
	private Map<String,Object> getOrderNoByRefDeliveryNo(String ref_deliveryNo,String deliveryType) throws Exception
	{
		try
		{
			String sqlTvOrder = "select * from dcp_order_delivery where REF_DELIVERYNO='" + ref_deliveryNo + "' and DELIVERYTYPE='"
					+ deliveryType + "' ";
			List<Map<String, Object>> listTvOrder = this.dao.executeQuerySQL(sqlTvOrder, null);
			if (listTvOrder == null || listTvOrder.size() == 0)
			{
				return null;
			}
			return listTvOrder.get(0);
		}
		catch (Exception e)
		{
			HelpTools.writelog_fileName("【物流回调WULIUCallBack】物流回调类型(WULIUTYPE):" + wuliutype + ",物流类型deliveryType="
					+ deliveryType + ",商家的物流订单号ref_deliveryNo=" + ref_deliveryNo + "，获取对应的订单异常:" + e.getMessage(), LogFileName);

		}
		return null;
	}

	/**
	 * 快递鸟物流公司编码对应的物流公司名称
	 * @param expressTypeCode
	 * @return
	 * @throws Exception
	 */
	private String getExpressTypeName (String expressTypeCode) throws Exception
	{
		String expressTypeName = "";
		try
		{
			switch (expressTypeCode)
			{
				case "HTKY":
					expressTypeName = "百世快递";
					break;
				case "ZTO":
					expressTypeName = "中通快递";
					break;
				case "STO":
					expressTypeName = "申通快递";
					break;
				case "YTO":
					expressTypeName = "圆通速递";
					break;
				case "YD":
					expressTypeName = "韵达速递";
					break;
				case "YZPY":
					expressTypeName = "邮政快递包裹";
					break;
				case "EMS":
					expressTypeName = "EMS";
					break;
				case "SF":
					expressTypeName = "顺丰速运";
					break;
				case "JD":
					expressTypeName = "京东快递";
					break;
				case "UC":
					expressTypeName = "优速快递";
					break;
				case "DBL":
					expressTypeName = "德邦快递";
					break;
				case "JTSD":
					expressTypeName = "极兔速递";
					break;
				case "CNSD":
					expressTypeName = "菜鸟直送";
					break;
				case "CNCY":
					expressTypeName = "菜鸟橙运";
					break;
				case "ZJS":
					expressTypeName = "宅急送";
					break;
				default:
					expressTypeName = "未知快递";
					break;

			}
		}
		catch (Exception e)
		{

		}
		return expressTypeName;
	}

	/**
	 * 快递鸟同城物流公司编码对应的物流公司名称
	 * @param expressTypeCode
	 * @return
	 * @throws Exception
	 */
	private String getTCExpressTypeName (String expressTypeCode) throws Exception
	{
		String expressTypeName = "";
		try
		{
			switch (expressTypeCode)
			{
				case "ZXQD":
					expressTypeName = "智选渠道";
					break;
				case "ZNDD":
					expressTypeName = "智能调度";
					break;
				case "SFTC":
					expressTypeName = "顺丰同城";
					break;
				case "UU":
					expressTypeName = "UU跑腿";
					break;
				case "SS":
					expressTypeName = "闪送";
					break;
				case "DD":
					expressTypeName = "达达快送";
					break;
				case "MTPS":
					expressTypeName = "美团配送";
					break;
				case "FN":
					expressTypeName = "蜂鸟配送";
					break;
				default:
					expressTypeName = "未知同城快递";
					break;

			}
		}
		catch (Exception e)
		{

		}
		return expressTypeName;
	}

	/**
	 * 根据回调的三方物流单号查询对应的订单号
	 * @param out_deliveryNo
	 * @param deliveryType
	 * @return
	 * @throws Exception
	 */
	private Map<String,Object> getOrderNoByOutDeliveryNo(String out_deliveryNo,String deliveryType) throws Exception
	{
		try
		{
			String sqlTvOrder = "select * from dcp_order_delivery where OUT_DELIVERYNO='" + out_deliveryNo + "' and DELIVERYTYPE='"
					+ deliveryType + "' ";
			List<Map<String, Object>> listTvOrder = this.dao.executeQuerySQL(sqlTvOrder, null);
			if (listTvOrder == null || listTvOrder.size() == 0)
			{
				return null;
			}
			return listTvOrder.get(0);
		}
		catch (Exception e)
		{
			HelpTools.writelog_fileName("【物流回调WULIUCallBack】物流回调类型(WULIUTYPE):" + wuliutype + ",物流类型deliveryType="
					+ deliveryType + ",三方物流订单号out_deliveryNo=" + out_deliveryNo + "，获取对应的订单异常:" + e.getMessage(), LogFileName);

		}
		return null;
	}
	
}
