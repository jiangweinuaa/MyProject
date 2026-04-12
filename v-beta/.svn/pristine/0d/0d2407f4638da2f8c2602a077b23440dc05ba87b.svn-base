package com.dsc.spos.thirdpart.youzan;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.crypto.Cipher;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.parser.Feature;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DsmDAO;
import com.dsc.spos.dao.ExecBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.JsonBasicRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.ninetyone.util.ColumnDataValue;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.thirdpart.youzan.request.YouZanNotifyBasicReq;
import com.dsc.spos.thirdpart.youzan.request.YouZanNotifyDataReq;
import com.dsc.spos.thirdpart.youzan.request.YouZanNotifyRefundSuccessReq;
import com.dsc.spos.thirdpart.youzan.request.YouZanNotifyTradeBuyerPayReq;
import com.dsc.spos.thirdpart.youzan.request.YouZanNotifyTradeCloseReq;
import com.dsc.spos.thirdpart.youzan.request.YouZanNotifyTradeSuccessReq;
import com.dsc.spos.thirdpart.youzan.response.YouZanBasicRes;
import com.dsc.spos.thirdpart.youzan.response.YouZanMultistoreListRes;
import com.dsc.spos.thirdpart.youzan.response.YouZanNotifyBasicRes;
import com.dsc.spos.thirdpart.youzan.response.YouZanRefundSellerRes;
import com.dsc.spos.thirdpart.youzan.response.YouZanTradeGetRes;
import com.dsc.spos.thirdpart.youzan.util.YouZanUtils;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.SWaimaiBasicService;
import com.dsc.spos.waimai.entity.order;
import com.dsc.spos.waimai.entity.orderGoodsItem;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.dsc.spos.waimai.entity.orderPay;
import com.dsc.spos.waimai.entity.orderRedisKeyInfo;
import com.dsc.spos.waimai.entity.orderStatusLog;

public class YouZanCallBackService
{
	static String LogFileNameAll = "YouZanNotify";//记录原始推送消息
	static String LogFileName = "YouZanPost";//解析记录
	
	public static String langType="zh_CN";

	Logger logger = LogManager.getLogger(SWaimaiBasicService.class.getName());

	public YouZanCallBackService() 
	{
	}
//	public static void main(String[] args) {
//		String newurl="/api/youzan.ump.thirdpartyactivity.create/1.0.0?access_token=123456";
//		System.out.print(newurl.substring(newurl.indexOf("?access_token")));
		
//		System.out.println(DigestUtils.md5Hex(DigestUtils.md5Hex("tmall"+"15011077963"+"4qjnmY")));
//		System.out.print(DigestUtils.md5Hex(DigestUtils.md5Hex("tmall"+"15011077963"+"QfRDdB")));
//		
//	}
	
//	public static void getToken(String shopId) throws Exception {
//		String thisToken="";
//		String url="https://open.youzanyun.com/auth/token";
//		Map<String, Object> params = new HashMap<String, Object>();
//		params.put("client_id", "cf109ebe0670a1af57");
//		params.put("client_secret", "45bcda7a3339ff7488cf2aad38ee8072");
//		params.put("authorize_type", "silent");
//		params.put("grant_id", shopId);
//		Map<String, Object> headers = new HashMap<String, Object>();
//		headers.put("Content-Type", "application/json;charset=UTF-8");
//		OrderPostClient opc=new OrderPostClient();
//		String resStr=opc.sendSoapPost("", url, headers, JSON.toJSONString(params));
//		System.out.print(resStr);
//	}
	
	public void Log(String logFileName,String log) throws Exception{
		YouZanUtils utils=new YouZanUtils();
		utils.writelogFileName(logFileName, log);
	}
	
		
	public JsonBasicRes execute2(HttpServletRequest request,String json,DsmDAO dao,String callType) throws Exception{
		JsonBasicRes res=new JsonBasicRes();
//		String sql="SELECT A.* FROM DCP_ECOMMERCE A WHERE A.LOAD_DOCTYPE='"+orderLoadDocType.YOUZAN+"'";
//		List<Map<String, Object>> basicList = StaticInfo.dao.executeQuerySQL(sql, null);
//		if(basicList==null||basicList.size()==0){
//			res.setSuccess(false);
//			res.setServiceStatus("100");
//			res.setServiceDescription("DCP_ECOMMERCE有赞平台未配置或未生效");
//			Log(LogFileName,"\r\n***********DCP_ECOMMERCE有赞平台未配置或未生效,入参:\r\ncallType-"+callType+"\r\njson-"+json);
//			return res;
//		}else if(basicList.size()==1){
//			Map<String, Object> basicMap=basicList.get(0);
//			//核销
//			if("CANCEL".toUpperCase().equals(callType.toUpperCase())){
//				cancelCode();
//			}
//		}
//		
//		
		return res;
	}
	
	public Map<String, Object> getYouZanMap(String channelId) throws Exception {
		Map<String, Object> map=new HashMap<String, Object>();
		String sql="SELECT A.* FROM DCP_ECOMMERCE A WHERE A.ECPLATFORMNO='"+orderLoadDocType.YOUZAN+"' AND A.SHOPSN='"+channelId+"'";
		List<Map<String, Object>> basicList = StaticInfo.dao.executeQuerySQL(sql, null);
		if(basicList!=null&&basicList.size()>0){
			map=basicList.get(0);
		}
		return map;
	}
	
	public List<Map<String, Object>> getYouZanList(Map<String, Object> map) throws Exception {
		StringBuffer sql=new StringBuffer("SELECT A.*"
				+ " ,DE.APIKEY AS DEAPIKEY,DE.APISECRET AS DEAPISECRET,DE.APISIGN AS DEAPISIGN "
				+ " ,DE.TOKEN AS DETOKEN,DE.PUBLICKEY AS DEPUBLICKEY "
				+ " FROM DCP_ECOMMERCE A "
				+ " LEFT JOIN DCP_ECOMMERCE_DETAIL DE ON A.EID=DE.EID AND A.LOADDOCTYPE=DE.LOADDOCTYPE AND A.CHANNELID=DE.CHANNELID "
				+ " ");
		if(map==null){
			map=new HashMap<String, Object>();
		}
		if((map.get("shopId")!=null&&map.get("shopId").toString().trim().length()>0)||(map.get("orderShop")!=null&&map.get("orderShop").toString().trim().length()>0)){
			sql.append(" LEFT JOIN DCP_MAPPINGSHOP DS ON DE.EID=DS.EID AND DE.LOADDOCTYPE=DS.LOAD_DOCTYPE AND DE.APIKEY=DS.APPKEY ");
			
		}
		sql.append(" WHERE A.LOADDOCTYPE='"+orderLoadDocType.YOUZAN+"'");
		if(map.get("kdtId")!=null&&map.get("kdtId").toString().trim().length()>0){
			sql.append(" AND DE.DEAPIKEY='"+map.get("kdtId").toString().trim()+"'");
		}
		if(map.get("shopId")!=null&&map.get("shopId").toString().trim().length()>0){
			sql.append(" AND DS.SHOPID='"+map.get("shopId").toString().trim()+"'");
		}
		if(map.get("orderShop")!=null&&map.get("orderShop").toString().trim().length()>0){
			sql.append(" AND DS.ORDERSHOPNO='"+map.get("orderShop").toString().trim()+"'");
		}
		List<Map<String, Object>> basicList = null;
		try{
			basicList = StaticInfo.dao.executeQuerySQL(sql.toString(), null);
		}catch(Exception e){
			YouZanUtils utils=new YouZanUtils();
			utils.ErrorLog("[SQL执行异常]:\r\n"+sql.toString()+"\r\n错误详情:\r\n"+YouZanUtils.getTrace(e));
		}
		if(basicList==null){
			basicList=new ArrayList<Map<String, Object>>();
		}else{
			Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
            condition.put("DEAPIKEY", true);
            //调用过滤函数
            basicList = MapDistinct.getMap(basicList, condition);
		}
		return basicList;
	}
	
	/**
	 * 拒单
	 */
	public JsonBasicRes refuseProcess(String orderNo,String shopNo,String orderShop,Map<String, Object> basicMap) throws Exception{
		JsonBasicRes thisRes=new JsonBasicRes();
		thisRes.setSuccess(false);
		//商家主动退款
		String serviceId="ORDER_SELLER_REFUSE_BIZ";
		Map<String, Object> params1 = new HashMap<String, Object>();
		
		YouZanUtils utils=new YouZanUtils();
		if(basicMap==null||basicMap.isEmpty()){
			Map<String, Object> map1=new HashMap<String, Object>();
			map1.put("shopId", shopNo);
			map1.put("orderShop", orderShop);
			List<Map<String, Object>> elmAppKeyList = getYouZanList(map1);
			if(elmAppKeyList!=null&&elmAppKeyList.size()==1){
				basicMap=elmAppKeyList.get(0);
			}else{
				utils.ErrorLog("[门店"+shopNo+"]渠道配置信息异常");
				thisRes.setServiceDescription("[门店"+shopNo+"]渠道配置信息异常");
				return null;
			}
		}
		
		params1.put("tid", orderNo);//tid 订单号
		String resStr1=utils.PostData(serviceId,basicMap, params1);
		
		YouZanBasicRes res1=new YouZanBasicRes();
		res1=com.alibaba.fastjson.JSON.parseObject(resStr1, YouZanBasicRes.class);
		if(res1!=null){
			if("TRUE".equalsIgnoreCase(res1.getSuccess())){
				thisRes.setSuccess(true);
				thisRes.setServiceDescription("核销成功");
				return thisRes;
			}else{
//				if(yzErrorRes!=null){
//					String message="不能重复核销订单";
//					if(yzErrorRes.getMsg().startsWith(message)){
//						//
//						Log("YouZanSelfFetchCode","\r\n"+new com.google.gson.Gson().toJson(params1));
//						//已退款
//						thisRes.setSuccess(true);
//						thisRes.setServiceDescription(yzErrorRes.getMsg());
//						return thisRes;
//					}else{
//						thisRes.setServiceDescription(yzErrorRes.getMsg());
//						return thisRes;
//					}
//				}else{
//					thisRes.setServiceDescription("有赞核销失败");
//					return thisRes;
//				}
			}
		}else{
			thisRes.setServiceDescription("有赞自提核销失败");
		}
		return thisRes;
	}
	
	/**
	 * 获取有赞原始数据中的配送方式，中台配送方式可改
	 */
	public String getExpressType(String orderNo,String shopNo,String orderShop,Map<String, Object> basicMap) throws Exception{
		String expressType="";
		YouZanTradeGetRes res=getTrade(orderNo, shopNo, orderShop, basicMap);
		if(res!=null){
			try{
				//有赞 配送方式（物流类型）， 0:快递发货; 1:到店自提; 2:同城配送; 9:无需发货（虚拟商品订单）
				expressType=res.getFull_order_info().getOrder_info().getExpress_type();
			}catch(Exception e){
				
			}
		}
		return expressType;
	}
	
	/**
	 * 获取有赞原始数据中的配送方式，中台配送方式可改
	 */
	public String getSelffetchCode(String orderNo,String shopNo,String orderShop,Map<String, Object> basicMap) throws Exception{
		String selffetchCode="";
		YouZanTradeGetRes res=getTrade(orderNo, shopNo, orderShop, basicMap);
		if(res!=null){
			try{
				//自提码
				selffetchCode=res.getFull_order_info().getOrder_info().getSelffetch_code();
			}catch(Exception e){
				
			}
		}
		return selffetchCode;
	}
	
	/**
	 * 交易订单详情4.0接口
	 * https://doc.youzanyun.com/doc#/content/API/1-305/detail/api/0/120
	 * https://open.youzanyun.com/api/youzan.trade.get/4.0.0
	 */
	public YouZanTradeGetRes getTrade(String orderNo,String shopNo,String orderShop,Map<String, Object> basicMap) throws Exception{
		String serviceId="ORDER_DETAIL_QUERY_BIZ";
		Map<String, Object> params1 = new HashMap<String, Object>();
		
		YouZanUtils utils=new YouZanUtils();
		if(basicMap==null||basicMap.isEmpty()){
			Map<String, Object> map1=new HashMap<String, Object>();
			map1.put("shopId", shopNo);
			map1.put("orderShop", orderShop);
			List<Map<String, Object>> elmAppKeyList = getYouZanList(map1);
			if(elmAppKeyList!=null&&elmAppKeyList.size()==1){
				basicMap=elmAppKeyList.get(0);
			}else{
				utils.ErrorLog("[门店"+shopNo+"]渠道配置信息异常");
				return null;
			}
		}
		
		params1.put("tid", orderNo);//tid 订单号
		String resStr1=utils.PostData(serviceId,basicMap, params1);
		YouZanTradeGetRes res1=new YouZanTradeGetRes();
		if(resStr1!=null){
			com.alibaba.fastjson.JSONObject reSJson=com.alibaba.fastjson.JSONObject.parseObject(resStr1);
			if(reSJson!=null&&"TRUE".equalsIgnoreCase(reSJson.getString("success"))){
				res1=com.alibaba.fastjson.JSON.parseObject(reSJson.getString("responseDTO"), YouZanTradeGetRes.class);
				res1.setSuccess("TRUE");
			}
		}
		return res1;
	}
	
	/**
	 * 店铺列表   用于同步店铺
	 */
	public YouZanMultistoreListRes offlineSearch(List<YouZanMultistoreListRes.ResponseDTO.Item> dataList,Map<String, Object> map1,int pageNo,int pageSize)throws Exception{
		String serviceId="ORGANIZATION_LIST_QUERY_BIZ";
		YouZanMultistoreListRes thisRes=new YouZanMultistoreListRes();
		if(pageSize>10){
			pageSize=10;
		}
		Map<String, Object> params1 = new HashMap<String, Object>();
		params1.put("pageIndex", pageNo);//
		params1.put("pageSize", pageSize);//
		YouZanUtils utils=new YouZanUtils();
		String resStr1=utils.PostData(serviceId,map1, params1);
		
		YouZanTradeGetRes res2=getTrade("E20210825155652039004137", "01","59194022",null);
				
				
		YouZanMultistoreListRes res1=null;
		try{
			if(resStr1!=null){
				res1=com.alibaba.fastjson.JSON.parseObject(resStr1, new com.alibaba.fastjson.TypeReference<YouZanMultistoreListRes>(){});
			}
		}catch(Exception e){
			
		}
		if(res1!=null&&res1.getSuccess()!=null&&"TRUE".equalsIgnoreCase(res1.getSuccess())){
			YouZanMultistoreListRes.ResponseDTO resDTO=res1.getResponseDTO();
			dataList.addAll(resDTO.getData());
			thisRes.setSuccess(res1.getSuccess());
			thisRes.setErrorCode(res1.getErrorCode());
			thisRes.getResponseDTO().setData(dataList);
			int count=Integer.valueOf(resDTO.getTotal());
			if(count>pageNo*pageSize){
				thisRes=offlineSearch(dataList, map1, pageNo+1, pageSize);
			}
		}else{
			thisRes.setSuccess("FALSE");
			return thisRes;
		}
		return thisRes;
	}
	
	/**
	 * 自提核销
	 */
	public JsonBasicRes selfFetchCodeApply(String orderNo,String shopNo,String orderShop,String code,String desc,Map<String, Object> basicMap)throws Exception{
		JsonBasicRes thisRes=new JsonBasicRes();
		thisRes.setSuccess(false);
		//自提单订单核销
		String serviceId="ORDER_SELFFETCH_APPLY_BIZ";
		Map<String, Object> params1 = new HashMap<String, Object>();
		
		YouZanUtils utils=new YouZanUtils();
		if(basicMap==null||basicMap.isEmpty()){
			Map<String, Object> map1=new HashMap<String, Object>();
			map1.put("shopId", shopNo);
			map1.put("orderShop", orderShop);
			List<Map<String, Object>> elmAppKeyList = getYouZanList(map1);
			if(elmAppKeyList!=null&&elmAppKeyList.size()==1){
				basicMap=elmAppKeyList.get(0);
			}else{
				utils.ErrorLog("[门店"+shopNo+"]渠道配置信息异常");
				return null;
			}
		}
		
		params1.put("tid", orderNo);//tid 订单号
		if(code!=null&&code.trim().length()>0){
			params1.put("code", code.trim());//自提码
		}
		params1.put("extra_info", desc);
		String resStr1=utils.PostData(serviceId,basicMap, params1);
		
		YouZanBasicRes res1=new YouZanBasicRes();
		res1=com.alibaba.fastjson.JSON.parseObject(resStr1, YouZanBasicRes.class);
		if(res1!=null){
			if("TRUE".equalsIgnoreCase(res1.getSuccess())){
				thisRes.setSuccess(true);
				thisRes.setServiceDescription("核销成功");
				return thisRes;
			}else{
//				if(yzErrorRes!=null){
//					String message="不能重复核销订单";
//					if(yzErrorRes.getMsg().startsWith(message)){
//						//
//						Log("YouZanSelfFetchCode","\r\n"+new com.google.gson.Gson().toJson(params1));
//						//已退款
//						thisRes.setSuccess(true);
//						thisRes.setServiceDescription(yzErrorRes.getMsg());
//						return thisRes;
//					}else{
//						thisRes.setServiceDescription(yzErrorRes.getMsg());
//						return thisRes;
//					}
//				}else{
//					thisRes.setServiceDescription("有赞核销失败");
//					return thisRes;
//				}
			}
		}else{
			thisRes.setServiceDescription("有赞自提核销失败");
		}
		return thisRes;
	}
	
	/**
	 * 商家自配送
	 */
//	public JsonBasicRes localConfirm(String kdtId,Map<String, Object> basicMap,String accessToken,Map<String, Object> orderMap)throws Exception{
//		JsonBasicRes thisRes=new JsonBasicRes();
//		thisRes.setSuccess(false);
//		String url="https://open.youzanyun.com";
//		if(basicMap!=null&&!basicMap.isEmpty()){
//			url=basicMap.get("API_URL")==null?"":basicMap.get("API_URL").toString();
//		}
//		url=url+"/api/youzan.logistics.online.local.confirm/3.0.0?access_token="+accessToken;
//		String tid=orderMap.get("ORDERNO")==null?"0":orderMap.get("ORDERNO").toString();
//		Map<String, Object> headers = new HashMap<String, Object>();
//		headers.put("Content-Type", "application/x-www-form-urlencoded");
//		Map<String, Object> params1 = new HashMap<String, Object>();
//		params1.put("order_no", tid);//tid 订单号
//		params1.put("channel", 0);//三方配送渠道（目前仅支持默认值0：商家自配送）
////		Log("YouZanLocalConfirm","\r\n***********request:"+new com.google.gson.Gson().toJson(params1));
//		String resStr1=YouZanUtils.PostData(kdtId, basicMap, url, params1);
////		Log("YouZanLocalConfirm","\r\n***********response:"+new com.google.gson.Gson().toJson(resStr1));
//		YouZanLocalConfirmRes res1=new YouZanLocalConfirmRes();
//		res1=com.alibaba.fastjson.JSON.parseObject(resStr1, YouZanLocalConfirmRes.class);
//		
//		if(res1!=null){
//			if("TRUE".equals(res1.getSuccess().toUpperCase())){
//				YouZanLocalConfirmRes.Data rsData=res1.getData();
//				if(rsData!=null){
//					if("TRUE".equals(rsData.getIs_success().toUpperCase())){
//						thisRes.setSuccess(true);
//						thisRes.setServiceDescription("有赞商家自配送发货成功");
//						return thisRes;
//					}else{
//						thisRes.setServiceDescription("有赞商家自配送发货失败");
//						return thisRes;
//					}
//				}else{
//					thisRes.setServiceDescription("有赞商家自配送发货失败");
//					return thisRes;
//				}
//			}else{
//				String message="订单没有可发货的商品";
//				if(res1.getMessage().startsWith(message)){
//					thisRes.setSuccess(true);
//					thisRes.setServiceDescription(res1.getMessage());
//					return thisRes;
//				}else{
//					thisRes.setServiceDescription(res1.getMessage());
//					return thisRes;
//				}
//			}
//		}else{
//			thisRes.setServiceDescription("有赞商家自配送发货失败");
//		}
//		
//		return thisRes;
//	}
	
	/**
	 * 同城三方配送呼叫,仅适用于三方配送呼叫，同城自配送请勿使用。
	 */
//	public JsonBasicRes localConfirmThird(String kdtId,Map<String, Object> basicMap,String accessToken,Map<String, Object> orderMap)throws Exception{
//		JsonBasicRes thisRes=new JsonBasicRes();
//		thisRes.setSuccess(false);
//		String url="https://open.youzanyun.com";
//		if(basicMap!=null&&!basicMap.isEmpty()){
//			url=basicMap.get("API_URL")==null?"":basicMap.get("API_URL").toString();
//		}
//		url=url+"/api/youzan.logistics.online.local.confirm/3.0.1?access_token="+accessToken;
//		String tid=orderMap.get("ORDERNO")==null?"0":orderMap.get("ORDERNO").toString();
//		String selfDelChannel=orderMap.get("YouZan_SelfDel_Channel")==null?"":orderMap.get("YouZan_SelfDel_Channel").toString();
//		Map<String, Object> headers = new HashMap<String, Object>();
//		headers.put("Content-Type", "application/x-www-form-urlencoded");
//		Map<String, Object> params1 = new HashMap<String, Object>();
//		params1.put("tid", tid);//tid 有赞订单号
//		params1.put("channel", selfDelChannel);//同城配送渠道，1-达达，2-蜂鸟，3-点我达，4-顺丰同城，5-美团配送，9-同城上云（uu跑腿，快服务等）
//		Log("YouZanLocalConfirmThrid","\r\n***********request:"+new com.google.gson.Gson().toJson(params1));
//		String resStr1=YouZanUtils.PostData(kdtId, basicMap, url, params1);
//		Log("YouZanLocalConfirmThrid","\r\n***********response:"+new com.google.gson.Gson().toJson(resStr1));
//		YouZanBasicRes res1=new YouZanBasicRes();
//		res1=com.alibaba.fastjson.JSON.parseObject(resStr1, YouZanBasicRes.class);
//		if(res1!=null){
//			if("TRUE".equals(res1.getSuccess().toUpperCase())){
//				thisRes.setSuccess(true);
//				thisRes.setServiceDescription("有赞商家同城三方配送发货成功");
//				return thisRes;
//			}else{
//				String message="订单没有可发货的商品";
//				if(res1.getMessage().startsWith(message)){
//					thisRes.setSuccess(true);
//					thisRes.setServiceDescription(res1.getMessage());
//					return thisRes;
//				}else{
//					thisRes.setServiceDescription(res1.getMessage());
//					return thisRes;
//				}
//			}
//		}else{
//			thisRes.setServiceDescription("有赞商家同城三方配送发货失败");
//		}
//		return thisRes;
//	}
	
	/**
	 * 接单通知有赞端
	 */
//	public JsonBasicRes agreeProcess(String kdtId,Map<String, Object> basicMap,String accessToken,Map<String, Object> orderMap) throws Exception{
//		return agreeProcess(kdtId, basicMap, accessToken, orderMap,"Y");
//	}
	
	/**
	 * 接单通知有赞端
	 */
//	public JsonBasicRes agreeProcess(String kdtId,Map<String, Object> basicMap,String accessToken,Map<String, Object> orderMap,String dealType) throws Exception{
//		JsonBasicRes thisRes=new JsonBasicRes();
//		thisRes.setSuccess(false);
//		String companyNo=basicMap.get("COMPANYNO")==null?"":basicMap.get("COMPANYNO").toString();
//		//调用有赞接口
//		Map<String, Object> requestMap=new HashMap<String, Object>();
//		//CHANNELID	存放ktdid
//		String orderNo=orderMap.get("ORDERNO")==null?"":orderMap.get("ORDERNO").toString();
//		String channelId=orderMap.get("CHANNELID")==null?"":orderMap.get("CHANNELID").toString();
//		if(channelId==null||channelId.trim().length()<1){
//			channelId=kdtId;
//		}
//		if(channelId==null||channelId.trim().length()<1){
//			thisRes.setServiceDescription("单据中有赞渠道资料异常,无法处理!");
//			return thisRes;
//		}
//		String custType=PosPub.getPARA_SMS(StaticInfo.dao, companyNo, "", "YouZan_Cust_Type");
//		//绿姿 人工接单，无论何种情况，都调用此接口
//		if("lvzi".equals(custType)){
//			//绿姿项目保持原样，调用此接口
//		}else{
//			//超港项目
//			String shipDate=basicMap.get("SHIPDATE")==null?"":basicMap.get("SHIPDATE").toString();
//			if(shipDate!=null&&shipDate.trim().length()>0){
//				//配送时间不为空，则不是及时单，不调用此接口
//				thisRes.setSuccess(true);
//				thisRes.setServiceDescription("无需调用此接口,按成功处理!");
//				return thisRes;
//			}
//			
//			//配送方式（物流类型）， 0:快递发货; 1:到店自提; 2:同城配送; 9:无需发货（虚拟商品订单）
//			String expressType=getExpressType(kdtId, orderNo);
//			if("1".equals(expressType)){
//				thisRes.setServiceDescription("自提订单");
//				thisRes.setSuccess(true);
//				return thisRes;
//			}
//		}
//		
//		
//		requestMap.put("tid", orderNo);
//		requestMap.put("kdtId", channelId);
//		requestMap.put("dealType", dealType);
//		//https://lvzixiannaihongb.isv-dev.youzan.com/order/compute
//		String url=PosPub.getPARA_SMS(StaticInfo.dao, companyNo, "", "YouZan_Compute_Url");
//		url=url+"/order/refund";
//		//3jw8H3sE86ZidjpAKi
//		String secretKey=PosPub.getPARA_SMS(StaticInfo.dao, companyNo, "", "YouZan_Compute_SecretKey");
////		Log("YouZanAgreeProcess","\r\nurl:"+url+"\r\nrequest:"+new com.google.gson.Gson().toJson(requestMap));
//		String serialNo="";
//		if(dealType.equals("Y")){
//			serialNo=orderNo+"_AGREE";
//		}else if(dealType.equals("S")){
//			serialNo=orderNo+"_POST"+dealType;
//		}
//		String resStr=YouZanUtils.PostDataCust("YouZanAgreeProcess", url, secretKey,serialNo, requestMap);
////		Log("YouZanAgreeProcess","\r\nresponse:"+new com.google.gson.Gson().toJson(resStr));
//		YouZanBasicRes res1=new YouZanBasicRes();
//		res1=com.alibaba.fastjson.JSON.parseObject(resStr, YouZanBasicRes.class);
//		if(res1!=null){
//			if("TRUE".equalsIgnoreCase(res1.getSuccess())){
//				com.alibaba.fastjson.JSONObject dpsResJson = new com.alibaba.fastjson.JSONObject();
//				dpsResJson = JSON.parseObject(resStr,Feature.OrderedField);
//				String dataMsg = dpsResJson.getString("data"); // 0
//				if(dataMsg!=null&&"自提订单".equalsIgnoreCase(dataMsg)){
//					thisRes.setServiceDescription("自提订单");
//				}else{
//					thisRes.setServiceDescription("接单呼叫有赞成功");
//				}
//				thisRes.setSuccess(true);
//				
//				return thisRes;
//			}else{
//				thisRes.setServiceDescription(res1.getMessage());
//			}
//		}else{
//			thisRes.setServiceDescription("接单呼叫有赞失败");
//		}
//		return thisRes;
//	}
	
	
	
	/**
	 * 商家主动退款API
	 */
	public JsonBasicRes refundSeller(String kdtId,Map<String, Object> basicMap,Map<String, Object> orderMap) throws Exception{
		JsonBasicRes thisRes=new JsonBasicRes();
		thisRes.setSuccess(false);
		thisRes.setServiceDescription("有赞退款失败!");
		
		String eId=orderMap.get("EID")==null?"":orderMap.get("EID").toString();
		String docType=orderMap.get("LOADDOCTYPE")==null?"":orderMap.get("LOADDOCTYPE").toString();
		String orderNo=orderMap.get("ORDERNO")==null?"":orderMap.get("ORDERNO").toString();
		String serviceId="ORDER_SELLER_REFUSE_BIZ";
		if(basicMap==null||basicMap.isEmpty()){
			if(kdtId==null||kdtId.isEmpty()){
				thisRes.setServiceDescription("有赞退单参数异常!");
				return thisRes;
			}
			List<Map<String, Object>> elmAppKeyList = PosPub.getWaimaiAppConfig(StaticInfo.dao, eId, docType);
			if (elmAppKeyList == null || elmAppKeyList.isEmpty()){
				thisRes.setServiceDescription("有赞渠道参数未配置!");
				return thisRes;
			}
			elmAppKeyList =elmAppKeyList.stream().filter(g->g.get("CHANNELID").toString().equals(kdtId)).collect(Collectors.toList());
			if (elmAppKeyList == null || elmAppKeyList.isEmpty()){
				thisRes.setServiceDescription("有赞渠道["+kdtId+"]参数未配置!");
				return thisRes;
			}
			basicMap=elmAppKeyList.get(0);
		}
		Map<String, Object> params1 = new HashMap<String, Object>();
		params1.put("tid", orderNo);//
		YouZanUtils utils=new YouZanUtils();
		String resStr1=utils.PostData(serviceId,basicMap, params1);
		YouZanRefundSellerRes res1=null;
		try{
			res1=com.alibaba.fastjson.JSON.parseObject(resStr1, new com.alibaba.fastjson.TypeReference<YouZanRefundSellerRes>(){});
			
		}catch(Exception e){
			
		}
		if(res1!=null){
			if(res1!=null&&"TRUE".equals(res1.getSuccess().toUpperCase())){
				
			}else{
				return thisRes;
			}
		}
		
		
		return thisRes;
	}
	

	//交易消息使用指南
	//https://doc.youzanyun.com/doc#/content/35701/38866/47527
	public YouZanNotifyBasicRes execute(HttpServletRequest request,String json,DsmDAO dao) throws Exception{
		// TODO Auto-generated method stub
		YouZanNotifyBasicRes res=new YouZanNotifyBasicRes();
		Log(LogFileNameAll,"\r\n***********有赞消息推送-入参:\r\n"+json);
		try{
			YouZanNotifyDataReq bas1=com.alibaba.fastjson.JSON.parseObject(json, YouZanNotifyDataReq.class);
			if(bas1==null||bas1.getMessage()==null||bas1.getMessage().getData()==null){
				res.setDescription("消息推送格式异常");
				Log(LogFileName,"\r\n***********有赞平台消息推送-消息推送格式异常\r\n推送消息:"+json);
				return res;
			}
			
			YouZanNotifyBasicReq notifyBas=com.alibaba.fastjson.JSON.parseObject(bas1.getMessage().getData(), YouZanNotifyBasicReq.class);
			if(notifyBas==null){
				res.setDescription("消息推送格式异常");
				Log(LogFileName,"\r\n***********有赞平台消息推送-消息推送格式异常\r\n推送消息:"+json);
				return res;
			}
			
			//门店
			String kdtId=notifyBas.getKdt_id();
			String sql="SELECT A.*,"
					+ " C.ORG_NAME AS SHOPNAME, B.MACHORGANIZATIONNO AS MACHORGANIZATIONNO, "
					+ " D.ORG_NAME AS MACHORGANIZATIONNAME "
					+ " FROM DCP_ECOMMERCE A "
					+ " LEFT JOIN DCP_ORG B ON A.EID=B.EID AND A.SHIPPINGSHOPNO=B.ORGANIZATIONNO "
					+ " LEFT JOIN DCP_ORG_LANG C ON B.EID=C.EID AND B.ORGANIZATIONNO=C.ORGANIZATIONNO AND C.LANG_TYPE='"+langType+"' AND C.STATUS='100' "
					+ " LEFT JOIN DCP_ORG_LANG D ON B.EID=D.EID AND B.MACHORGANIZATIONNO=D.ORGANIZATIONNO AND D.LANG_TYPE='"+langType+"' AND D.STATUS='100' "
					+ " LEFT JOIN DCP_ECOMMERCE_DETAIL DE ON A.EID=DE.EID AND A.LOADDOCTYPE=DE.LOADDOCTYPE AND A.CHANNELID=DE.CHANNELID "
					+ " WHERE A.LOADDOCTYPE='"+orderLoadDocType.YOUZAN+"' "
					+ " AND A.STATUS='100' AND A.APIURL IS NOT NULL "
					+ " AND TO_CHAR(DE.APIKEY)='"+kdtId+"' ";
			Log(LogFileName,"\r\n***********有赞平台消息推送-有赞平台门店:"+kdtId+"中台未配置或未生效\r\n执行SQL:"+sql+"\r\n推送消息:"+json);
			List<Map<String, Object>> basicList = StaticInfo.dao.executeQuerySQL(sql, null);
			if(basicList==null||basicList.size()==0){
				res.setDescription("有赞平台门店:"+kdtId+"中台未配置或未生效");
				Log(LogFileName,"\r\n***********有赞平台消息推送-有赞平台门店:"+kdtId+"中台未配置或未生效\r\n执行SQL:"+sql+"\r\n推送消息:"+json);
				return res;
			}
			else if(basicList.size()==1){
				Map<String, Object> basicMap=basicList.get(0);
				String type=notifyBas.getType();
				if(type==null||type.trim().length()==0){
					res.setDescription("有赞消息推送TYPE参数异常");
					return res;
				}
				//1.trade_TradeBuyerPay 买家付款完成创建消息
				//https://doc.youzanyun.com/detail/MSG/302
				//trade_TradePaid(交易支付)
				//https://doc.youzanyun.com/detail/MSG/301
				if("trade_TradePaid".toUpperCase().equals(type.toUpperCase())){
					res=TradeBuyerPay(notifyBas,basicMap, dao);
				}
				//交易关闭
				//买家或卖家取消订单、订单全额退款
				//https://doc.youzanyun.com/doc#/content/MSG/20-交易消息/detail/msg/trade_TradeClose
				else if("trade_TradeClose".toUpperCase().equals(type.toUpperCase())){
					res=TradeClose(type,notifyBas,basicMap, dao);
				}
				//交易成功
				//买家确认收货或系统自动确认收货且主订单状态变为「交易成功」时触发
				//https://doc.youzanyun.com/doc#/content/MSG/20-交易消息/detail/msg/trade_TradeSuccess
//					else if("trade_TradeSuccess".toUpperCase().equals(type.toUpperCase())){
//						res=TradeSuccess(type,notifyBas,basicMap, dao);
//					}
				//同城配送包裹状态变更
				//https://doc.youzanyun.com/doc#/content/MSG/20-同城配送订单
				else if("delivery_takeoutOrderUpdate".toUpperCase().equals(type.toUpperCase())){
					res=DeliveryTakeoutOrderUpdate(type,json,basicMap, dao);
				}
				//trade_TradeSellerShip
				//卖家对所有商品发货完成
				//https://doc.youzanyun.com/doc#/content/MSG/20-交易消息/detail/msg/trade_TradeSellerShip
				else if("trade_TradeSellerShip".toUpperCase().equals(type.toUpperCase())){
//					res=TradeSellerShip(type,notifyBas,basicMap, dao);
				}
				//卖家同意退货消息
				//https://doc.youzanyun.com/doc#/content/MSG/20-交易退款/detail/msg/trade_refund_RefundSuccess
				else if("trade_refund_RefundSuccess".toUpperCase().equals(type.toUpperCase())){
					res=TradeRefundSuccess(type,notifyBas,basicMap, dao);
				}
				//trade_refund_RefundSellerAgree
				//https://doc.youzanyun.com/doc#/content/MSG/20-交易退款/detail/msg/trade_refund_RefundSellerAgree
				else if("trade_refund_RefundSellerAgree".toUpperCase().equals(type.toUpperCase())){
					res=TradeRefundSuccess(type,notifyBas,basicMap, dao);
				}
				//trade_refund_RefundSellerCreated
				//https://doc.youzanyun.com/doc#/content/MSG/20-交易退款/detail/msg/trade_refund_RefundSellerCreated
				else if("trade_refund_RefundSellerCreated".toUpperCase().equals(type.toUpperCase())){
					res=TradeRefundSuccess(type,notifyBas,basicMap, dao);
				}
				
//					//trade_refund_RefundSellerAgree 卖家主动退款
//					//https://doc.youzanyun.com/doc#/content/MSG/20-%E4%BA%A4%E6%98%93%E9%80%80%E6%AC%BE/detail/msg/trade_refund_RefundSellerAgree
//					else if("trade_refund_RefundSellerAgree".toUpperCase().equals(type.toUpperCase())){
//						res=RefundSuccess(type,notifyBas,basicMap, dao);
//					}
				else{
					res.setDescription(type+"消息推送未接入");
				}
			}
			//
			else{
				Log(LogFileName,"\r\n***********有赞平台消息推送-有赞平台门店:"+kdtId+"配置存在多笔!\r\n执行SQL:"+sql+"\r\n推送消息:"+json);
			}
			
		}catch(Exception e){
			Log(LogFileName,"\r\n***********有赞消息推送保存失败-推送消息:\r\n"+json+"\r\n"+YouZanUtils.getTrace(e));
			throw e;
		}finally{
		}
		return res;

	}
	
	/**
	 * 卖家发货
	 * 
	 * {"update_time":"2020-10-19+10:54:59","tid":"E20201019105338053904125"}
	 */
//	public YouZanNotifyBasicRes TradeSellerShip(String notifyType,YouZanNotifyBasicReq notifyBas,Map<String, Object> basicMap,DsmDAO dao) throws Exception{
//		YouZanNotifyBasicRes res=new YouZanNotifyBasicRes();
//		String msg=decode(notifyBas.getMsg());
//		Log(LogFileName,"\r\n***********有赞消息推送-入参:\r\n"+com.alibaba.fastjson.JSON.toJSONString(notifyBas));
//		Log(LogFileName,"\r\n***********有赞消息推送-入参 [UrlDecode(UTF-8)]解码:\r\n"+msg);
//		Log(LogFileName,"\r\n***********DCP_ECOMMERCE-当前配置资料:\r\n"+com.alibaba.fastjson.JSON.toJSONString(basicMap));
//		com.alibaba.fastjson.JSONObject reqJson=com.alibaba.fastjson.JSONObject.parseObject(msg);
//		//单号
//		String kdtId=notifyBas.getKdt_id();
//		String orderNo=reqJson.getString("tid");
//		String companyNo=basicMap.get("COMPANYNO")==null?"":basicMap.get("COMPANYNO").toString();
//		//更新为待配送
////		updateOrderStatus("卖家发货", orderNo, companyNo, "", "", "1", "9");// 订单状态9-待配送
//		//配送状态
//		//有赞 配送方式（物流类型）， 0:快递发货; 1:到店自提; 2:同城配送; 9:无需发货（虚拟商品订单）
//		String expressType=getExpressType(kdtId, orderNo);
//		if("1".equals(expressType)){
//			updateOrderStatus("有赞推送卖家发货", orderNo, companyNo, "", "有赞消息推送-卖家发货(自提核销)", "2", "0");// 配送状态0-物流已下单
//		}
//		
//		res.setCode(0);
//		res.setMsg("success");
//		return res;
//	}
	
	/**
	 * 同城配送包裹状态变更
	 * 
	 * {"store_id":0,"cancel_from":4,"type":"delivery_takeoutOrderUpdate","dm_mobile":"","tid":"E20201016173727025504103",
	 * "delivery_fee":385,"dist_id":"202010161738270000140255","kdt_id":90445053,"update_time":1602841710,
	 * "cancel_reason":"订单长时间未分配","delivery_type":1,"deduct_fee":0,"status":5}
	 * 
	 * https://doc.youzanyun.com/doc#/content/MSG/20-同城配送订单
	 */
	public YouZanNotifyBasicRes DeliveryTakeoutOrderUpdate(String notifyType,String json,Map<String, Object> basicMap,DsmDAO dao) throws Exception{
		YouZanNotifyBasicRes res=new YouZanNotifyBasicRes();
//		String msg=decode(notifyBas.getMsg());
		//
		Log(LogFileName,"\r\n***********有赞消息推送-入参:\r\n"+json);
		Log(LogFileName,"\r\n***********DCP_ECOMMERCE-当前配置资料:\r\n"+com.alibaba.fastjson.JSON.toJSONString(basicMap));
		com.alibaba.fastjson.JSONObject reqJson=com.alibaba.fastjson.JSONObject.parseObject(json);
		//状态：
		//一般状态（待接单：1 ，待取货：2， 配送中：3， 已完成：4， 已取消：5， 已过期-十分钟骑手未接单：7 ），
		//异常状态（取消异常：6，客户方异常终止-配送方无责任的异常终止，比如客人联系不上，无需退款：
		//8，配送方异常终止-配送方有责任的异常终止，比如骑士丢件，需要退款：
		//9，自己追溯初始态；30，第三方返回异常码：90，服务器异常：1000，调用第三方下单失败：-2，第三方反馈异常：10）
		String status=reqJson.getString("status");
		String kdtId=reqJson.getString("kdt_id");
		String cancelReason=reqJson.getString("cancel_reason");
		//单号
		String orderNo=reqJson.getString("tid");
		//订单取消来源（1:配送员取消；2:商家主动取消；3:第三方配送公司取消；4:有赞系统取消）其他状态下默认值为0
		String cancelFrom=reqJson.getString("cancel_from");
		String companyNo=basicMap.get("COMPANYNO")==null?"":basicMap.get("COMPANYNO").toString();
		//已过期-十分钟骑手未接单：7
		res.setCode(0);
		res.setDescription("成功!");
		res.setMsg("success");
		if("7".equals(status)||("5".equals(status)&&"4".equals(cancelFrom))){
			String memo="有赞消息推送-物流取消";
			if(cancelReason!=null&&cancelReason.length()>0){
				memo+="(原因:"+cancelReason+")\r\n";
			}
			
			String sqlOrder="SELECT * FROM TV_ORDER where ORDERNO='"+orderNo+"'  "
					+ "and COMPANYNO='"+companyNo+"' "
					+ "and LOAD_DOCTYPE='"+orderLoadDocType.YOUZAN+"' ";
			List<Map<String, Object>> listSqlOrder=StaticInfo.dao.executeQuerySQL(sqlOrder, null, false);
			if(listSqlOrder==null||listSqlOrder.size()==0){
				res.setCode(-1);
				res.setMsg("failure");
				res.setDescription("单据["+orderNo+"]未接入!");
				return res;
			}
			Map<String, Object> orderMap=listSqlOrder.get(0);
			String shopNo=orderMap.get("SHOP")==null?"":orderMap.get("SHOP").toString();
			String shipDate=orderMap.get("SHIPDATE")==null?"":orderMap.get("SHIPDATE").toString();
			basicMap.put("SHIPDATE", shipDate);
			updateOrderStatus(notifyType, orderNo, companyNo, shopNo, memo,"2","4");//2-物流状态；4-物流异常或取消
			
			int count=0;
			try{
				count=Integer.valueOf(orderMap.get("SHOPEE_BRANCH_ID")==null?"0":orderMap.get("SHOPEE_BRANCH_ID").toString());
				count++;
			}catch(Exception e){
				
			}
			memo+="，第"+count+"次重新呼叫有赞平台进行物流配送！\r\n";
			int allCount=5;
			String allCountStr=PosPub.getPARA_SMS(StaticInfo.dao, companyNo, "", "YouZan_Delivery_Count");
			if(allCountStr!=null&&allCountStr.trim().length()>0){
				try{
					allCount=Integer.valueOf(allCountStr);
				}catch(Exception e){
					
				}
			}
			//呼叫达到5次
			if(count>=allCount){
				res.setCode(-1);
				res.setMsg("failure");
				res.setDescription("单据["+orderNo+"]重复呼叫有赞平台接单已达"+allCount+"次,不再执行重复呼叫!");
				return res;
			}
			JsonBasicRes thisRes=new JsonBasicRes();
//			thisRes=agreeProcess(kdtId, basicMap, null, orderMap);
			if(thisRes.isSuccess()){
				//借用栏位 记录重复次数
				//SHOPEE_BRANCH_ID	虾皮发货分支代码
				List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
				UptBean ub1 = new UptBean("TV_ORDER");
				ub1.addUpdateValue("SHOPEE_BRANCH_ID", new DataValue(count, Types.VARCHAR));
				
				ub1.addCondition("LOAD_DOCTYPE", new DataValue(orderLoadDocType.YOUZAN, Types.VARCHAR));
				ub1.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
				ub1.addCondition("COMPANYNO", new DataValue(companyNo, Types.VARCHAR));
				lstData.add(new DataProcessBean(ub1));
				try{
					StaticInfo.dao.useTransactionProcessData(lstData);
				}catch(Exception e){
					
				}
				updateErrDelivery(orderNo, companyNo);
				//2-配送状态  0-物流已下单
				updateOrderStatus("同城配送包裹状态推送", orderNo, companyNo, shopNo, memo,"2","0");
			}else{
				updateErrDelivery(orderNo, companyNo);
			}
				
		}
		//已取消：5
		else if("5".equals(status)){
			String memo="有赞消息推送-物流取消";
			if(cancelReason!=null&&cancelReason.length()>0){
				memo+="(原因:"+cancelReason+")\r\n";
			}
			updateOrderStatus("同城配送包裹状态推送", orderNo, companyNo, "", memo,"2","5");//可查看对应 HelpTools.GetOrderStatusName
		}
		//待取货：2
		else if("2".equals(status)){
			String memo="有赞消息推送-待取货\r\n";
			//1 接单
			List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
			UptBean ub1 = new UptBean("TV_ORDER");
			ub1.addUpdateValue("DELIVERYSTUTAS", new DataValue("1", Types.VARCHAR));
			String delname=reqJson.getString("dm_name");
			if(delname!=null&&delname.trim().length()>0){
				//DELNAME	配送员姓名
				ub1.addUpdateValue("DELNAME", new DataValue(delname, Types.VARCHAR));
				memo+=" 配送员："+delname;
			}
			String delphone=reqJson.getString("dm_mobile");
			if(delphone!=null&&delphone.trim().length()>0){
				//DELTELEPHONE	配送员手机
				ub1.addUpdateValue("DELTELEPHONE", new DataValue(delphone, Types.VARCHAR));
				memo+=" 配送员电话："+delphone;
			}
			
			
			ub1.addCondition("LOAD_DOCTYPE", new DataValue(orderLoadDocType.YOUZAN, Types.VARCHAR));
			ub1.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
			ub1.addCondition("COMPANYNO", new DataValue(companyNo, Types.VARCHAR));
			lstData.add(new DataProcessBean(ub1));
			
			
			//相当于 中台 1-物流已接单
			updateOrderStatus("同城配送包裹状态推送", orderNo, companyNo, "", memo,"2","1");
			try{
				StaticInfo.dao.useTransactionProcessData(lstData);
			}catch(Exception e){
				
			}
		}
		//配送中：3
		else if("3".equals(status)){
			//相当于 中台 2-物流已取件
			updateOrderStatus("同城配送包裹状态推送", orderNo, companyNo, "", "有赞消息推送-配送中","2","2");
//			//1-订单状态  10-已发货
//			updateOrderStatus("同城配送包裹状态推送", orderNo, companyNo, "", "","1","10");
			//10配送中 
			List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
			UptBean ub1 = new UptBean("TV_ORDER");
			ub1.addUpdateValue("DELIVERYSTUTAS", new DataValue("10", Types.VARCHAR));
			ub1.addCondition("LOAD_DOCTYPE", new DataValue(orderLoadDocType.YOUZAN, Types.VARCHAR));
			ub1.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
			ub1.addCondition("COMPANYNO", new DataValue(companyNo, Types.VARCHAR));
			lstData.add(new DataProcessBean(ub1));
			try{
				StaticInfo.dao.useTransactionProcessData(lstData);
			}catch(Exception e){
				
			}
		}
		//已完成：4
		else if("4".equals(status)){
			//2-配送状态  3-用户签收
			updateOrderStatus("同城配送包裹状态推送", orderNo, companyNo, "", "有赞消息推送-配送完成","2","3");
			List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
			UptBean ub1 = new UptBean("TV_ORDER");
			ub1.addUpdateValue("DELIVERYSTUTAS", new DataValue("3", Types.VARCHAR));
			ub1.addCondition("LOAD_DOCTYPE", new DataValue(orderLoadDocType.YOUZAN, Types.VARCHAR));
			ub1.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
			ub1.addCondition("COMPANYNO", new DataValue(companyNo, Types.VARCHAR));
			lstData.add(new DataProcessBean(ub1));
			try{
				StaticInfo.dao.useTransactionProcessData(lstData);
			}catch(Exception e){
				
			}
		}
		//待接单：1 
		else if("1".equals(status)){
			//2-配送状态  0-物流已下单
			updateOrderStatus("同城配送包裹状态推送", orderNo, companyNo, "", "有赞消息推送-待接单","2","0");
//			//1-订单状态  9-待配送
//			updateOrderStatus("同城配送包裹状态推送", orderNo, companyNo, "", "","1","9");

			//0 已下单
			List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
			UptBean ub1 = new UptBean("TV_ORDER");
			ub1.addUpdateValue("DELIVERYSTUTAS", new DataValue("0", Types.VARCHAR));
			ub1.addCondition("LOAD_DOCTYPE", new DataValue(orderLoadDocType.YOUZAN, Types.VARCHAR));
			ub1.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
			ub1.addCondition("COMPANYNO", new DataValue(companyNo, Types.VARCHAR));
			lstData.add(new DataProcessBean(ub1));
			try{
				StaticInfo.dao.useTransactionProcessData(lstData);
			}catch(Exception e){
				
			}
		}
		else{
			res.setCode(-1);
			res.setMsg("failure");
			res.setDescription("状态码["+status+"]未接入!");
			return res;
		}
		return res;
	}
	
	public void updateErrDelivery(String orderNo,String companyNo )throws Exception{
		List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
		UptBean ub1 = new UptBean("TV_ORDER");
		ub1.addUpdateValue("DELIVERYSTUTAS", new DataValue("4", Types.VARCHAR));
		ub1.addCondition("LOAD_DOCTYPE", new DataValue(orderLoadDocType.YOUZAN, Types.VARCHAR));
		ub1.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
		ub1.addCondition("COMPANYNO", new DataValue(companyNo, Types.VARCHAR));
		lstData.add(new DataProcessBean(ub1));
		try{
			StaticInfo.dao.useTransactionProcessData(lstData);
		}catch(Exception e){
			
		}
	}
	
	public void updateOrderStatus(String type,String orderNo,String companyNo,String shopNo,String memo,
			String modiType,String status) throws Exception{
		com.alibaba.fastjson.JSONObject statusJson=new com.alibaba.fastjson.JSONObject();
		statusJson.put("orderNO", orderNo);
		statusJson.put("o_companyNO", companyNo);
		statusJson.put("o_shopNO", shopNo);
		statusJson.put("opNo", "有赞");
		statusJson.put("modiType", modiType);
		//1、订单状态
		//2-物流状态
		if("2".equals(modiType)){
			statusJson.put("deliveryStutas", status);//
		}else{
			statusJson.put("status", status);//2、已接单
		}
		statusJson.put("docType", new String[]{orderLoadDocType.YOUZAN});
		if(memo==null||memo.trim().length()<1){
			memo="有赞\r\n";
		}
		statusJson.put("memo",memo);
		statusJson.put("serviceId", "OrderStatusUpdate");
		
		
		DispatchService ds = DispatchService.getInstance();
		String statusRes = ds.callService(statusJson.toString(), StaticInfo.dao);
		Log(LogFileName,"["+type+"]"
		 		+ "\r\n更新单据["+companyNo+"-"+orderNo+"]"
		 		+ "\r\nrequest:"+statusJson.toString()
		 		+ "\r\nresponse"+statusRes);
	}
	
	/**
	 * 交易成功
	 * 买家确认收货或系统自动确认收货且主订单状态变为「交易成功」时触发
	 * https://doc.youzanyun.com/doc#/content/MSG/20-交易消息/detail/msg/trade_TradeSuccess
	 */
	public YouZanNotifyBasicRes TradeSuccess(String notifyType,YouZanNotifyBasicReq notifyBas,Map<String, Object> basicMap,DsmDAO dao) throws Exception{
		YouZanNotifyBasicRes res=new YouZanNotifyBasicRes();
		String msg=decode(notifyBas.getMsg());
		Log(LogFileName,"\r\n***********有赞消息推送-入参:\r\n"+com.alibaba.fastjson.JSON.toJSONString(notifyBas));
		Log(LogFileName,"\r\n***********有赞消息推送-入参 [UrlDecode(UTF-8)]解码:\r\n"+msg);
		Log(LogFileName,"\r\n***********DCP_ECOMMERCE-当前配置资料:\r\n"+com.alibaba.fastjson.JSON.toJSONString(basicMap));
		YouZanNotifyTradeSuccessReq data=com.alibaba.fastjson.JSON.parseObject(msg, new com.alibaba.fastjson.TypeReference<YouZanNotifyTradeSuccessReq>(){});
		if(data!=null){
			String loadDoctype=orderLoadDocType.YOUZAN;
			String companyNo=basicMap.get("COMPANYNO")==null?"":basicMap.get("COMPANYNO").toString();
			String orderNo=data.getTid();
			String shopNo="";
			String sqlOrder="SELECT A.* FROM TV_ORDER A "
					+ " WHERE A.COMPANYNO=? AND A.LOAD_DOCTYPE=? and A.ORDERNO=? ";
			String[] conditionValues=new String[]{companyNo,loadDoctype,orderNo};
			List<Map<String, Object>> listSqlOrder=StaticInfo.dao.executeQuerySQL(sqlOrder, conditionValues);
			if(listSqlOrder!=null&&listSqlOrder.size()>0){
				Map<String, Object> orderMap=listSqlOrder.get(0);
				shopNo=orderMap.get("SHOP")==null?"":orderMap.get("SHOP").toString();
				//11-已完成 12-已退单
				String status=orderMap.get("STATUS")==null?"":orderMap.get("STATUS").toString();
				if("11".equals(status)||"12".equals(status)){
					String des="单号:"+orderNo+"单据状态为"+status+",无需接入!";
					res.setCode(-1);
					res.setMsg("failure");
					res.setDescription(des);
					Log(LogFileName,"\r\n***********"+des+"");
					return res;
				}
			}else{
				res.setCode(-1);
				res.setMsg("failure");
				res.setDescription("单号:"+orderNo+"尚未接入");
				Log(LogFileName,"\r\n***********单号:"+orderNo+"尚未接入");
				return res;
			}
			String[] docType=new String[]{loadDoctype};
			com.alibaba.fastjson.JSONObject orderStr=new com.alibaba.fastjson.JSONObject();
			orderStr.put("serviceId", "OrderStatusUpdate");
			orderStr.put("orderNO", orderNo);
			orderStr.put("o_companyNO", companyNo);
			orderStr.put("o_shopNO", shopNo);
			orderStr.put("o_opNO", loadDoctype);//操作人
			orderStr.put("modiType", "1");//1.订单状态修改 ；2、物流状态变更；3、退货状态变更；4、订单修改（订货人、收货人、自提改配送）
			orderStr.put("docType", docType);
			orderStr.put("status","11");//1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单
			
			DispatchService ds = DispatchService.getInstance();
			String payResStr = ds.callService(orderStr.toJSONString(), StaticInfo.dao);
			com.alibaba.fastjson.JSONObject payResJson = new com.alibaba.fastjson.JSONObject();
			payResJson = JSON.parseObject(payResStr,Feature.OrderedField);
			String paySuccess = payResJson.getString("success").toUpperCase();
			if(!Check.Null(paySuccess) && paySuccess.equals("TRUE")){
				res.setCode(0);
				res.setMsg("success");
			}else{
				res.setCode(-1);
				res.setMsg("failure");
				res.setDescription("交易状态更新失败:"+payResJson.getString("serviceDescription"));
				Log(LogFileName,"\r\n***********单号:"+orderNo+"交易状态更新失败"+payResJson.getString("serviceDescription"));
				return res;
			}
		}
		return res;
	}
	
	
	/**
	 * 交易关闭
	 * 买家或卖家取消订单、订单全额退款
	 * https://doc.youzanyun.com/doc#/content/MSG/20-交易消息/detail/msg/trade_TradeClose
	 */
	public YouZanNotifyBasicRes TradeClose(String notifyType,YouZanNotifyBasicReq notifyBas,Map<String, Object> basicMap,DsmDAO dao) throws Exception{
		YouZanNotifyBasicRes res=new YouZanNotifyBasicRes();
		String TradeCloseFN= "YouZanTradeClose";
		String TradeCloseErrorFN= "YouZanTradeCloseError";
		String msg=decode(notifyBas.getMsg());
		Log(TradeCloseFN,"\r\n***********有赞消息推送-入参:\r\n"+com.alibaba.fastjson.JSON.toJSONString(notifyBas));
		Log(TradeCloseFN,"\r\n***********有赞消息推送-入参 [UrlDecode(UTF-8)]解码:\r\n"+msg);
		Log(TradeCloseFN,"\r\n***********DCP_ECOMMERCE-当前配置资料:\r\n"+com.alibaba.fastjson.JSON.toJSONString(basicMap));
		YouZanNotifyTradeCloseReq data=com.alibaba.fastjson.JSON.parseObject(msg, new com.alibaba.fastjson.TypeReference<YouZanNotifyTradeCloseReq>(){});
		if(data!=null){
			String loadDoctype=orderLoadDocType.YOUZAN;
			String companyNo=basicMap.get("COMPANYNO")==null?"":basicMap.get("COMPANYNO").toString();
			String orderNo=data.getTid();
			
			String sqlTdSale = "select saleno from td_sale where companyno='"+companyNo+"' and ofno='"+orderNo+"' ";
			List<Map<String, Object>> listTdSale=StaticInfo.dao.executeQuerySQL(sqlTdSale, null);
			//如果已经订转销的订单，不再产生退TD_ORDER
			if(listTdSale!=null&&listTdSale.size()>0){
				Log(LogFileName,"\r\n***********单据["+orderNo+"]已经订转销,不再产生负向TD_ORDER!");
			}else{
				ReturnTdOrder(companyNo, orderNo);
			}
			
			
			String shopNo="";
			String sqlOrder="SELECT A.* FROM TV_ORDER A "
					+ " WHERE A.COMPANYNO=? AND A.LOAD_DOCTYPE=? and A.ORDERNO=? ";
			String[] conditionValues=new String[]{companyNo,loadDoctype,orderNo};
			List<Map<String, Object>> listSqlOrder=StaticInfo.dao.executeQuerySQL(sqlOrder, conditionValues);
			if(listSqlOrder!=null&&listSqlOrder.size()>0){
				Map<String, Object> orderMap=listSqlOrder.get(0);
				shopNo=orderMap.get("SHOP")==null?"":orderMap.get("SHOP").toString();
				String status=orderMap.get("STATUS")==null?"":orderMap.get("STATUS").toString();
				//12-已退单
				if("12".equals(status)){
					res.setCode(0);
					res.setMsg("success");
					res.setDescription("单号:"+orderNo+"已退单");
					return res;
				}
			}else{
				res.setCode(-1);
				res.setMsg("failure");
				res.setDescription("单号:"+orderNo+"未接入,无需进行交易关闭");
				Log(TradeCloseErrorFN,"\r\n***********单号:"+orderNo+"未接入,不需要进行交易关闭");
				return res;
			}
			
			com.alibaba.fastjson.JSONObject OrderCancelStr=new com.alibaba.fastjson.JSONObject();
			com.alibaba.fastjson.JSONArray ja=new com.alibaba.fastjson.JSONArray();
			com.alibaba.fastjson.JSONObject orderStr=new com.alibaba.fastjson.JSONObject();
			orderStr.put("orderNO", orderNo);
			orderStr.put("o_companyNO", companyNo);
			orderStr.put("o_shopNO", shopNo);
			orderStr.put("opNo", loadDoctype);
			orderStr.put("docType", loadDoctype);
			orderStr.put("isInvoiceNo","N");
			ja.add(orderStr);
			OrderCancelStr.put("datas", ja);
			OrderCancelStr.put("type", "1");//1-退单  2-物流撤销
			OrderCancelStr.put("loadType", "0");//内部处理，跳过验证
			OrderCancelStr.put("serviceId", "OrderCancelUpdateDCPServiceImp");
			OrderCancelStr.put("isChkToken", "N");
			
			DispatchService ds = DispatchService.getInstance();
			String payResStr = ds.callService(OrderCancelStr.toJSONString(), StaticInfo.dao);
			com.alibaba.fastjson.JSONObject payResJson = new com.alibaba.fastjson.JSONObject();
			payResJson = JSON.parseObject(payResStr,Feature.OrderedField);
			String paySuccess = payResJson.getString("success").toUpperCase();
			if(!Check.Null(paySuccess) && paySuccess.equals("TRUE")){
				res.setCode(0);
				res.setMsg("success");
			}else{
				res.setCode(-1);
				res.setMsg("failure");
				res.setDescription("交易关闭失败:"+payResJson.getString("serviceDescription"));
				Log(TradeCloseErrorFN,"\r\n***********单号:"+orderNo+"交易关闭失败"+payResJson.getString("serviceDescription"));
				return res;
			}
		}
		return res;
	}
	
	/**
	 * 存在一笔TD_ORDER，且非负向单据时，生成负向TD_ORDER
	 */
	public void ReturnTdOrder(String companyNo,String orderId) throws Exception{
		String sql="";
		try{
			String sqlOrder="SELECT A.* FROM TD_ORDER A "
					+ " WHERE A.COMPANYNO='"+companyNo+"' AND A.ORDER_ID='"+orderId+"' ";
			List<Map<String, Object>> listSqlOrder=StaticInfo.dao.executeQuerySQL(sqlOrder, null);
			//大于1，表示已经存在负向单据，无需写入TD_ORDER负向
			//小于1表示未产生TD_ORDER，无需写入TD_ORDER负向
			if(listSqlOrder!=null&&listSqlOrder.size()>0){
				//4-负向单据
				String sqlOrder2=sqlOrder+" AND A.TYPE=4";
				List<Map<String, Object>> listSqlOrder2=StaticInfo.dao.executeQuerySQL(sqlOrder2, null);
				if(listSqlOrder2!=null&&listSqlOrder2.size()>0){
					Log(LogFileName,"\r\n***********单据["+orderId+"]已生成负向TD_ORDER,无需再次生成负向TD_ORDER!\r\n相关SQL-"+sqlOrder);
				}else{
					String orderNo=listSqlOrder.get(0).get("ORDERNO").toString();
					String type=listSqlOrder.get(0).get("TYPE").toString();
					//负向单据
					if("4".equals(type)){
						return;
					}
					String newOrderno=orderNo+"_RE";
					//需要插入的表
					String[] arrayA = new String[] { "TD_ORDER", "TD_ORDER_DETAIL", "TD_ORDER_PAY","TD_STATISTIC_INFO"};  
				    List<String> tableStrList = Arrays.asList(arrayA);
				    List<DataProcessBean> lstData1=new ArrayList<DataProcessBean>();
				    for(String tableName:tableStrList){
				    	Map<String,String> newValueMap=new HashMap<String,String>();
				    	newValueMap.put("ORDERNO", newOrderno);
				    	//各表需要变更的栏位及新值
				    	if("TD_ORDER".equals(tableName)){
				    		newValueMap.put("TYPE", "4");
				    		newValueMap.put("OFNO", orderNo);
				    		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmssSSS");
							Date dateNow=new Date();
							//当前时间
							String transTime=sdf1.format(dateNow);
				    		newValueMap.put("TRAN_TIME", transTime);
				    	}
				    	//表栏位信息，用于组sql
				    	String tdOrderSql="select COLUMN_NAME,DATA_TYPE from user_tab_cols where table_name='"+tableName+"' ";
				    	List<String> columnNameList = new ArrayList<String>();
				    	List<String> columnValueList = new ArrayList<String>();
				    	if(newValueMap!=null&&!newValueMap.isEmpty()){
				    		for (Entry<String, String> itemEntry : newValueMap.entrySet()) {
				    			String key=itemEntry.getKey();
				    			columnNameList.add(key);
				    			columnValueList.add("'"+itemEntry.getValue()+"'");
				    		}
				    		if(columnNameList!=null&&columnNameList.size()>0){
				    			tdOrderSql+=" and column_name not in('"+String.join("','", columnNameList)+"') ";
				    		}
				    	}
				    	String insertSql="";
				    	String selectSql="";
				    	String insertCustSql1="";
				    	String selectCustSql1="";
				    	List<Map<String, Object>> tdOrderList=StaticInfo.dao.executeQuerySQL(tdOrderSql, null);
				    	if(tdOrderList!=null&&tdOrderList.size()>0){
				    		List<String> columnNameStrList = tdOrderList.stream()
				    				.map(g->fetchGroupKey(tableName,g)).collect(Collectors.toList()); 
				    		insertCustSql1=String.join(",", columnNameStrList);
				    	}
				    	String insertCustColumnSql="";
				    	String insertCustValueSql="";
				    	if(columnNameList!=null&&columnNameList.size()>0){
				    		insertCustColumnSql=String.join(",", columnNameList);
				    		insertSql+=insertCustColumnSql+",";
				    	}
				    	if(columnValueList!=null&&columnValueList.size()>0){
				    		insertCustValueSql=String.join(",", columnValueList);
				    		selectSql+=insertCustValueSql+",";
				    	}
				    	selectCustSql1=insertCustSql1;
				    	if("TD_STATISTIC_INFO".equals(tableName)){
				    		insertCustSql1=insertCustSql1.replace("[AMT]", "AMT");
				    		selectCustSql1=selectCustSql1.replace("[AMT]", "-AMT");
				    	}
				    	
				    	insertSql+=insertCustSql1;
				    	selectSql+=selectCustSql1;
				    	String companyNoName="COMPANYNO";
				    	if("TD_STATISTIC_INFO".equals(tableName)){
				    		companyNoName="COMPANY";//TD_STATISTIC_INFO 表中是COMPANY，其他几张表是COMPANYNO
				    	}
				    	String sql1="INSERT INTO "+tableName+" ("+insertSql+") "
				    			+ "SELECT "+selectSql+" FROM "+tableName+" "
				    			+ "WHERE "+companyNoName+"='"+companyNo+"' AND ORDERNO='"+orderNo+"' ";
				    	ExecBean ex1=new ExecBean(sql1);
					    lstData1.add(new DataProcessBean(ex1));
					    
					    sql+=sql1+";/n";
				    }
				    StaticInfo.dao.useTransactionProcessData(lstData1);
				    Log(LogFileName,"\r\n***********单据["+orderId+"]生成负向TD_ORDER,负向单号["+newOrderno+"]!\r\n相关SQL-"+sql);
					
				}
			}else{
				Log(LogFileName,"\r\n***********单据["+orderId+"]未生成TD_ORDER,无需生成负向TD_ORDER!\r\n相关SQL-"+sqlOrder);
			}
		}catch(Exception e){
			Log(LogFileName,"\r\n***********有赞交易关闭-新增TD_ORDER异常:\r\nsql-"+sql+"\r\n"+YouZanUtils.getTrace(e));
		}
		
		
	}
	
	private String fetchGroupKey(String tableName,Map<String, Object> g){
		String str1=g.get("COLUMN_NAME").toString();

		if("TD_STATISTIC_INFO".equals(tableName)&&"AMT".equals(str1)){
			str1="[AMT]";
		}
	    return str1;
	}
	
	
	public YouZanNotifyBasicRes TradeRefundSuccess(String notifyType,YouZanNotifyBasicReq notifyBas,Map<String, Object> basicMap,DsmDAO dao) throws Exception{
		YouZanNotifyBasicRes res=new YouZanNotifyBasicRes();
		String msg=decode(notifyBas.getMsg());
		Log(LogFileName,"\r\n***********有赞消息推送-入参:\r\n"+com.alibaba.fastjson.JSON.toJSONString(notifyBas));
		Log(LogFileName,"\r\n***********有赞消息推送-入参 [UrlDecode(UTF-8)]解码:\r\n"+msg);
		Log(LogFileName,"\r\n***********DCP_ECOMMERCE-当前配置资料:\r\n"+com.alibaba.fastjson.JSON.toJSONString(basicMap));
		YouZanNotifyRefundSuccessReq data=com.alibaba.fastjson.JSON.parseObject(msg, new com.alibaba.fastjson.TypeReference<YouZanNotifyRefundSuccessReq>(){});
		if(data!=null){
			String orderNo=data.getTid();
			String oid=data.getOids();
			String companyNo=basicMap.get("COMPANYNO")==null?"":basicMap.get("COMPANYNO").toString();
			String shopNo=basicMap.get("SHOP")==null?"":basicMap.get("SHOP").toString();
			
			String sqlOrder="SELECT B.*,"
					+ " A.REFUNDAMT AS AREFUNDAMT,A.REFUNDREASON AS AREFUNDREASON, "
					+ " A.PARTREFUNDAMT AS APARTREFUNDAMT,A.REFUNDAMT AS AREFUNDAMT, "
					+ " A.MEMO AS AMEMO,A.SHIPFEE AS ASHIPFEE,A.INCOMEAMT AS AINCOMEAMT, "
					+ " A.STATUS AS ASTATUS "
					+ " FROM TV_ORDER A "
					+ " LEFT JOIN TV_ORDER_DETAIL B ON A.COMPANYNO=B.COMPANYNO AND A.SHOP=B.SHOP AND A.ORDERNO=B.ORDERNO "
					+ " WHERE A.ORDERNO='"+orderNo+"'  "
					+ " AND A.COMPANYNO='"+companyNo+"' "
					+ " AND A.ORGANIZATIONNO='"+shopNo+"' "
					+ " AND A.SHOP='"+shopNo+"' "
					+ " AND A.LOAD_DOCTYPE='"+orderLoadDocType.YOUZAN+"' ";
			List<Map<String, Object>> listSqlOrder=StaticInfo.dao.executeQuerySQL(sqlOrder, null, false);
			if(listSqlOrder!=null&&listSqlOrder.size()>0){
				List<Map<String, Object>> orderSNList= listSqlOrder.stream().filter(g->oid.equals(g.get("ORDER_SN"))&&g.get("QTY")!=null&&Integer.valueOf(g.get("QTY").toString())>0).collect(Collectors.toList());
				if(orderSNList!=null&&orderSNList.size()==1){
					Map<String, Object> detailMap=orderSNList.get(0);
					String status=detailMap.get("ASTATUS")==null?"":detailMap.get("ASTATUS").toString().trim();
					//STATUS 12.已退单
					if("12".equals(status)){
						res.setCode(-1);
						res.setMsg("failure");
						res.setDescription("单据"+orderNo+"已退单,不再接入!");
						return res;
					}else if("11".equals(status)){
						res.setCode(-1);
						res.setMsg("failure");
						res.setDescription("单据"+orderNo+"已完成,不再接入!");
						return res;
					}else if("10".equals(status)){
						res.setCode(-1);
						res.setMsg("failure");
						res.setDescription("单据"+orderNo+"已发货,不再接入!");
						return res;
					}else if("9".equals(status)){
						res.setCode(-1);
						res.setMsg("failure");
						res.setDescription("单据"+orderNo+"待配送,不再接入!");
						return res;
					}
					String item=detailMap.get("ITEM")==null?"":detailMap.get("ITEM").toString().trim();
					String pluName=detailMap.get("PLUNAME")==null?"":detailMap.get("PLUNAME").toString().trim();
					String memoSql="SELECT A.* FROM TV_ORDER_DETAIL_MEMO A WHERE A.ORDERNO='"+orderNo+"'  "
					+ " AND A.COMPANYNO='"+companyNo+"' "
					+ " AND A.ORGANIZATIONNO='"+shopNo+"' "
					+ " AND A.SHOP='"+shopNo+"' "
					+ " AND A.LOAD_DOCTYPE='"+orderLoadDocType.YOUZAN+"' "
					+ " AND A.ITEM='"+item+"'";
					List<Map<String, Object>> memoSqlOrder=StaticInfo.dao.executeQuerySQL(memoSql, null, false);
					if(memoSqlOrder==null){
						memoSqlOrder=new ArrayList<Map<String, Object>>();
					}
					String updateTime=data.getUpdate_time();
					SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMdd");
					SimpleDateFormat sdf4 = new SimpleDateFormat("HHmmss");
					Date dateNow=new Date();
					//列表SQL
					List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
					ColumnDataValue cdvDetail = new ColumnDataValue();
					cdvDetail.Add("ORDERNO", orderNo, Types.VARCHAR); //订单编号
					cdvDetail.Add("CNFFLG", "Y", Types.VARCHAR); //有效否 CNFFLG
					cdvDetail.Add("TRAN_TIME", updateTime, Types.VARCHAR);//TRAN_TIME	时间标记
					cdvDetail.Add("LOAD_DOCTYPE", orderLoadDocType.YOUZAN, Types.VARCHAR); //LOAD_DOCTYPE	平台类型
					cdvDetail.Add("COMPANYNO", companyNo, Types.VARCHAR); //企业编号
					cdvDetail.Add("ORGANIZATIONNO", shopNo, Types.VARCHAR); //组织编号
					cdvDetail.Add("SHOP", shopNo, Types.VARCHAR); //门店编号 组织门店给相同值
					cdvDetail.Add("OITEM", item, Types.INTEGER); //OITEM	来源项次
					cdvDetail.Add("ITEM", memoSqlOrder.size()+1, Types.INTEGER); //ITEM	项次
					
					cdvDetail.Add("MEMONAME", "退单", Types.VARCHAR);//MEMONAME	备注名称
					//MEMOTYPE	备注类型
					cdvDetail.Add("MEMO", "商品["+pluName+"]已退", Types.VARCHAR);//MEMO	备注内容
					cdvDetail.Add("SDATE", sdf3.format(dateNow), Types.VARCHAR);//SDATE	系统日期
					cdvDetail.Add("STIME", sdf4.format(dateNow), Types.VARCHAR);//STIME	系统时间
					
					String[] columnsDetail = cdvDetail.Columns.toArray(new String[0]);
					DataValue[] insValueDetail = cdvDetail.DataValues.toArray(new DataValue[0]);
					InsBean ibDetail = new InsBean("TV_ORDER_DETAIL_MEMO", columnsDetail);
					ibDetail.addValues(insValueDetail);
					lstData.add(new DataProcessBean(ibDetail));
					StaticInfo.dao.useTransactionProcessData(lstData);
					res.setCode(0);
					res.setMsg("success");
					return res;
					
				}
			}
		}
		return res;
	}
	
	/**
	 * trade_refund_RefundSuccess 买家申请退款
	 */
//	public YouZanNotifyBasicRes RefundSuccess(String notifyType,YouZanNotifyBasicReq notifyBas,Map<String, Object> basicMap,DsmDAO dao) throws Exception{
//		YouZanNotifyBasicRes res=new YouZanNotifyBasicRes();
//		String msg=decode(notifyBas.getMsg());
//		Log(LogFileName,"\r\n***********有赞消息推送-入参:\r\n"+com.alibaba.fastjson.JSON.toJSONString(notifyBas));
//		Log(LogFileName,"\r\n***********有赞消息推送-入参 [UrlDecode(UTF-8)]解码:\r\n"+msg);
//		Log(LogFileName,"\r\n***********DCP_ECOMMERCE-当前配置资料:\r\n"+com.alibaba.fastjson.JSON.toJSONString(basicMap));
//		YouZanNotifyRefundSuccessReq data=com.alibaba.fastjson.JSON.parseObject(msg, new com.alibaba.fastjson.TypeReference<YouZanNotifyRefundSuccessReq>(){});
//		if(data!=null){
//			String orderNo=data.getTid();
//			String oid=data.getOids();
//			//退款金额
//			BigDecimal refundedFee=new BigDecimal(0);
//			if(data.getRefunded_fee()!=null&&data.getRefunded_fee().trim().length()>0){
//				refundedFee=new BigDecimal(data.getRefunded_fee());
//			}
//			
//			String companyNo=basicMap.get("COMPANYNO")==null?"":basicMap.get("COMPANYNO").toString();
//			String shopNo=basicMap.get("SHOP")==null?"":basicMap.get("SHOP").toString();
//			//REFUNDAMT	已退金额
//			String sqlOrder="SELECT B.*,"
//					+ " A.REFUNDAMT AS AREFUNDAMT,A.REFUNDREASON AS AREFUNDREASON, "
//					+ " A.PARTREFUNDAMT AS APARTREFUNDAMT,A.REFUNDAMT AS AREFUNDAMT, "
//					+ " A.MEMO AS AMEMO,A.SHIPFEE AS ASHIPFEE,A.INCOMEAMT AS AINCOMEAMT, "
//					+ " A.STATUS AS ASTATUS "
//					+ " FROM TV_ORDER A "
//					+ " LEFT JOIN TV_ORDER_DETAIL B ON A.COMPANYNO=B.COMPANYNO AND A.SHOP=B.SHOP AND A.ORDERNO=B.ORDERNO "
//					+ " WHERE A.ORDERNO='"+orderNo+"'  "
//					+ " AND A.COMPANYNO='"+companyNo+"' "
//					+ " AND A.CUSTOMERNO='"+customerNo+"' "
//					+ " AND A.ORGANIZATIONNO='"+shopNo+"' "
//					+ " AND A.SHOP='"+shopNo+"' "
//					+ " AND A.LOAD_DOCTYPE='"+orderLoadDocType.YOUZAN+"' ";
//			List<Map<String, Object>> listSqlOrder=StaticInfo.dao.executeQuerySQL(sqlOrder, null, false);
//			if(listSqlOrder!=null&&listSqlOrder.size()>0){
//				//列表SQL
//				List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
//				UptBean ub1 = new UptBean("TV_ORDER");
//				//REFUNDREASON 退货原因
//				//REFUNDSTATUS 退订状态
//				//REFUND_DATETIME 退订时间
//				SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//				SimpleDateFormat sdf5 = new SimpleDateFormat("yyyyMMddHHmmssSSS");
//				Date dateNow=new Date();
//				//当前时间
//				String transTime=sdf5.format(dateNow);
//				String updateTime=data.getUpdate_time();
//				//更新时间
//				if(updateTime!=null&&updateTime.trim().length()>0){
//					//REFUND_DATETIME 退订时间
//					ub1.addUpdateValue("REFUND_DATETIME", new DataValue(sdf5.format(sdf1.parse(updateTime)), Types.VARCHAR));
//				}
//				String reasonDes=data.getRefund_reason_des();
//				if(reasonDes!=null&&reasonDes.trim().length()>0){
//					ub1.addUpdateValue("REFUNDREASON", new DataValue(reasonDes, Types.VARCHAR));
//				}
//				ub1.addUpdateValue("TRAN_TIME", new DataValue(transTime, Types.VARCHAR));
//				ub1.addUpdateValue("UPDATE_TIME", new DataValue(transTime, Types.VARCHAR));
//				
//				String memo="";
//				String refundStatus="10";//10.部分退款成功
//				List<Map<String, Object>> orderSNList= listSqlOrder.stream().filter(g->oid.equals(g.get("ORDER_SN"))&&g.get("QTY")!=null&&Integer.valueOf(g.get("QTY").toString())>0).collect(Collectors.toList());
//				if(orderSNList!=null&&orderSNList.size()==1){
//					Map<String, Object> detailMap=orderSNList.get(0);
//					String status=detailMap.get("ASTATUS")==null?"":detailMap.get("ASTATUS").toString().trim();
//					//STATUS 12.已退单
//					if("12".equals(status)){
//						Log(LogFileName,"\r\n单据"+orderNo+"已完成退单,无可退金额!");
//						res.setDescription("单据"+orderNo+"已完成退单,无可退金额!");
//						return res;
//					}
//					memo=detailMap.get("AMEMO")==null?"":detailMap.get("AMEMO").toString().trim();
//					String returnKey=data.getRefund_id()+"/"+oid+"退款:"+String.valueOf(refundedFee)+"("+updateTime+")";
//					if(memo.contains(returnKey)){
//						Log(LogFileName,"\r\n退款消息重复推送");
//						res.setDescription("退款消息重复推送");
//						return res;
//					}else{
//						memo=memo+"\r\n"+returnKey;
//					}
//					
//					//单个商品金额(单价*数量)
//					BigDecimal detailAmt=new BigDecimal(0);
//					if(detailMap.get("AMT")!=null&&detailMap.get("AMT").toString().trim().length()>0){
//						detailAmt=new BigDecimal(detailMap.get("AMT").toString().trim());
//					}
//					//实际配送费
//					BigDecimal shipFee=new BigDecimal(0);
//					if(detailMap.get("ASHIPFEE")!=null&&detailMap.get("ASHIPFEE").toString().trim().length()>0){
//						shipFee=new BigDecimal(detailMap.get("ASHIPFEE").toString().trim());
//					}
//					//判断商品退款金额是否超出
//					if(detailAmt.add(shipFee).subtract(refundedFee).compareTo(new BigDecimal(0))<0){
//						Log(LogFileName,"\r\n商品"+oid+"可退金额已超出");
//						res.setDescription("商品"+oid+"可退金额已超出");
//						return res;
//					}
//					//INCOMEAMT 商家实收金额
//					BigDecimal incomeAmt=new BigDecimal(0);
//					if(detailMap.get("AINCOMEAMT")!=null&&detailMap.get("AINCOMEAMT").toString().trim().length()>0){
//						incomeAmt=new BigDecimal(detailMap.get("AINCOMEAMT").toString().trim());
//					}
//					BigDecimal refundAmt=new BigDecimal(0);
//					if(detailMap.get("AREFUNDAMT")!=null&&detailMap.get("AREFUNDAMT").toString().trim().length()>0){
//						refundAmt=new BigDecimal(detailMap.get("AREFUNDAMT").toString().trim());
//					}
//					//已收-已退-本次退款  判断是否超出
//					BigDecimal canRefundAmt=incomeAmt.subtract(refundAmt).subtract(refundAmt);
//					if(canRefundAmt.compareTo(new BigDecimal(0))<0){
//						Log(LogFileName,"\r\n商品"+oid+"可退金额已超出");
//						res.setDescription("商品"+oid+"可退金额已超出");
//						return res;
//					}else if(canRefundAmt.compareTo(new BigDecimal(0))==0){
//						refundStatus="13";//全部退款
//						//STATUS 12.已退单
//						ub1.addUpdateValue("STATUS", new DataValue("12", Types.VARCHAR));
//					}
//					
//					String thisMemo="";
//					//refund_type REFUND_ONLY 仅退款 REFUND_AND_RETURN 退货退款
//					String refundType=data.getRefund_type();
//					if("REFUND_ONLY".equals(refundType)){
//						//REFUND_ONLY 仅退款
//						thisMemo=thisMemo+"仅退款:";
//					}else if("REFUND_AND_RETURN".equals(refundType)){
//						//REFUND_AND_RETURN 退货退款
//						thisMemo=thisMemo+"退货退款:";
//						ColumnDataValue cdvDetail = new ColumnDataValue();
//						cdvDetail.Add("ORDERNO", orderNo, Types.VARCHAR); //订单编号
//						cdvDetail.Add("CNFFLG", "Y", Types.VARCHAR); //有效否 CNFFLG
//						cdvDetail.Add("TRAN_TIME", updateTime, Types.VARCHAR);//TRAN_TIME	时间标记
//						cdvDetail.Add("LOAD_DOCTYPE", orderLoadDocType.YOUZAN, Types.VARCHAR); //LOAD_DOCTYPE	平台类型
//						cdvDetail.Add("COMPANYNO", companyNo, Types.VARCHAR); //企业编号
//						cdvDetail.Add("CUSTOMERNO", customerNo, Types.VARCHAR); //客户编号 参照HelpTools中逻辑
//						cdvDetail.Add("ORGANIZATIONNO", shopNo, Types.VARCHAR); //组织编号
//						cdvDetail.Add("SHOP", shopNo, Types.VARCHAR); //门店编号 组织门店给相同值
//						cdvDetail.Add("ITEM", orderSNList.size()+1, Types.INTEGER); //ITEM	项次
//						cdvDetail.Add("ORDER_SN", oid, Types.VARCHAR); //ORDER_SN	电商交易流水号
//						//PLUNAME 商品名称
//						//num;//商品数量
//						BigDecimal qty=new BigDecimal(-1);
//						cdvDetail.Add("QTY", qty, Types.DECIMAL);//QTY	数量
//						cdvDetail.Add("AMT", qty.multiply(refundedFee), Types.DECIMAL);//金额 AMT	 NUMBER(23,8) 金额 = 原价 * 数量 - 折扣额
//						String[] columnsDetail = cdvDetail.Columns.toArray(new String[0]);
//						DataValue[] insValueDetail = cdvDetail.DataValues.toArray(new DataValue[0]);
//						InsBean ibDetail = new InsBean("TV_ORDER_DETAIL", columnsDetail);
//						ibDetail.addValues(insValueDetail);
//						lstData.add(new DataProcessBean(ibDetail));
//					}else{
//						Log(LogFileName,"\r\n未识别的refund_type-"+refundType);
//						res.setDescription("未识别的refund_type-"+refundType);
//						return res;
//					}
//				}else{
//					Log(LogFileName,"\r\n单号:"+orderNo+",oids:"+oid+"商品不存在");
//					res.setDescription("单号:"+orderNo+",oids:"+oid+"商品不存在");
//					return res;
//				}
//				
//				//1.未申请 2.用户申请退单 3.拒绝退单 4.客服仲裁中 
//				//5.退单失败 6.退单成功 7.用户申请部分退款 8.拒绝部分退款 9 部分退款失败 10.部分退款成功 11.用户申请取消订单 12.取消订单失败 13.取消订单成功 14.拒绝取消订单
//				ub1.addUpdateValue("REFUNDSTATUS", new DataValue(refundStatus, Types.VARCHAR));//13.取消订单成功
//				ub1.addUpdateValue("MEMO", new DataValue(memo, Types.VARCHAR));
//				//condition
//				ub1.addCondition("LOAD_DOCTYPE", new DataValue(orderLoadDocType.YOUZAN, Types.VARCHAR));
//				ub1.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
//				ub1.addCondition("COMPANYNO", new DataValue(companyNo, Types.VARCHAR));
//				ub1.addCondition("SHOP", new DataValue(shopNo, Types.VARCHAR));
//				
//				lstData.add(new DataProcessBean(ub1));
//				
//				StaticInfo.dao.useTransactionProcessData(lstData);
//				res.setCode(0);
//				res.setMsg("success");
//				return res;
//			}else{
//				Log(LogFileName,"\r\n***********单号:"+orderNo+",单据不存在");
//				res.setDescription("单号:"+orderNo+",单据不存在");
//			}
//		}
//		return res;
//	}
	
	
	
	/**
	 * 新增订单
	 */
	public YouZanNotifyBasicRes TradeBuyerPay(YouZanNotifyBasicReq notifyBas,Map<String, Object> basicMap,DsmDAO dao) throws Exception{
		YouZanNotifyBasicRes res=new YouZanNotifyBasicRes();
		
		try{
			String msg=decode(notifyBas.getMsg());
			Log(LogFileName,"\r\n***********有赞消息推送-入参:\r\n"+com.alibaba.fastjson.JSON.toJSONString(notifyBas));
			Log(LogFileName,"\r\n***********有赞消息推送-入参 [UrlDecode(UTF-8)]解码:\r\n"+msg);
			Log(LogFileName,"\r\n***********DCP_ECOMMERCE-当前配置资料:\r\n"+com.alibaba.fastjson.JSON.toJSONString(basicMap));
			YouZanNotifyTradeBuyerPayReq data=com.alibaba.fastjson.JSON.parseObject(msg, new com.alibaba.fastjson.TypeReference<YouZanNotifyTradeBuyerPayReq>(){});
			if(data!=null&&!Check.Null(data.getFull_order_info().getOrder_info().getTid())){
				String kdtId=notifyBas.getKdt_id();
				data.setKdt_id(kdtId);
				data.setKdt_name(notifyBas.getKdt_name());
				String orderNo=data.getFull_order_info().getOrder_info().getTid();
				String companyNo=basicMap.get("EID")==null?"":basicMap.get("EID").toString();
				
				String sqlOrder="SELECT * FROM DCP_ORDER where ORDERNO='"+orderNo+"'  "
						+ "and EID='"+companyNo+"' "
						+ "and BILLTYPE='1' "//单据类型（1：订单；-1：退订单）
						+ "and LOADDOCTYPE='"+orderLoadDocType.YOUZAN+"' ";
				List<Map<String, Object>> listSqlOrder=StaticInfo.dao.executeQuerySQL(sqlOrder, null, false);
				if(listSqlOrder!=null&&listSqlOrder.size()>0){
					Log(LogFileName,"\r\n***********单号:"+orderNo+",单据已存在");
					res.setCode(0);
					res.setMsg("success");
					res.setDescription("单号["+orderNo+"]已存在,本次为重复推送不做更新!");
					return res;
				}else{
					//列表SQL
					List<order> orderList=new ArrayList<order>();
					order order=new order();
					order=getTradePaidTvOrder(data, basicMap);
					orderList.add(order);
					
					List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
					
					String shopNo=order.getShopNo();
					
					StringBuffer errorMessage=new StringBuffer();
					ArrayList<DataProcessBean> DPB = com.dsc.spos.waimai.HelpTools.GetInsertOrderCreat(orderList,errorMessage,null);
					if (DPB != null && DPB.size() > 0)
					{
						if(DPB.size()==1){					
							res.setCode(-1);
							res.setMsg("false");
							res.setDescription("单号["+orderNo+"]新增订单异常!");
							return res;
						}
						
						lstData.addAll(DPB);				
						
						try {
							ParseJson pj = new ParseJson();
							String Response_json = pj.beanToJson(order);
							String redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + companyNo + ":" + shopNo;
							String hash_key = orderNo;
							HelpTools.writelog_waimai(
									"渠道类型loadDocType="+orderLoadDocType.YOUZAN+",【开始写缓存】" + "redis_key:" + redis_key + " hash_key:" + hash_key + " hash_value:" + Response_json);
							
							RedisPosPub redis = new RedisPosPub();
							boolean isexistHashkey = redis.IsExistHashKey(redis_key, hash_key);
							if (isexistHashkey) {
								
								redis.DeleteHkey(redis_key, hash_key);//
								HelpTools.writelog_waimai("【删除存在hash_key的缓存】成功！" + " redis_key:" + redis_key + " hash_key:" + hash_key);
							}
							boolean nret = redis.setHashMap(redis_key, hash_key, Response_json);
							if (nret) {
								HelpTools.writelog_waimai("【写缓存】OK" + " redis_key:" + redis_key + " hash_key:" + hash_key);
							} else {
								HelpTools.writelog_waimai("【写缓存】Error" + " redis_key:" + redis_key + " hash_key:" + hash_key);
							}
							//redis.Close();
							
						} catch (Exception e) {
							try {
								StringWriter errors = new StringWriter();
								PrintWriter pw=new PrintWriter(errors);
								e.printStackTrace(pw);	
								pw.flush();
								pw.close();			
								errors.flush();
								errors.close();
								HelpTools.writelog_waimai("\r\n单号orderNo="+ orderNo+",渠道类型loadDocType="+orderLoadDocType.YOUZAN+",【写缓存】Exception:" + e.getMessage() +"\r\n" + errors.getBuffer().toString()+ "******\r\n");
								
								pw=null;
								errors=null;
							}
							catch (IOException e1) {					
								HelpTools.writelog_waimai("\r\n单号orderNo="+ orderNo+",渠道类型loadDocType="+orderLoadDocType.YOUZAN+",【写缓存】Exception:" + e.getMessage());
							}
						}
						
						
						//写订单日志
						orderStatusLog onelv1 = new orderStatusLog();
						onelv1.setCallback_status("0");
						onelv1.setLoadDocType(order.getLoadDocType());//
						
						onelv1.setNeed_callback("N");
						onelv1.setNeed_notify("Y");
						String o_companyNO = order.geteId();
						
						onelv1.seteId(o_companyNO);
						onelv1.setOpName("有赞用户");
						onelv1.setOpNo("");
						onelv1.setOrderNo(orderNo);
						
						String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
						onelv1.setUpdate_time(updateDatetime);
						
						String statusType = "1";
						String orderstatus=order.getStatus();
						String updateStaus = orderstatus;
						
						onelv1.setStatusType(statusType);				 					
						onelv1.setStatus(updateStaus);
						
						StringBuilder statusTypeNameObj = new StringBuilder();
						String statusName = HelpTools.GetOrderStatusName(statusType, updateStaus,statusTypeNameObj);				 			 
						String statusTypeName = statusTypeNameObj.toString();
						onelv1.setStatusTypeName(statusTypeName);
						onelv1.setStatusName(statusName);
						String memo = "";
						memo += statusName;
						onelv1.setMemo(memo);
						
						
						List<orderStatusLog> statusLogList=new ArrayList<orderStatusLog>();
						statusLogList.add(onelv1);
						
						StringBuilder errorMessage2=new StringBuilder();
						boolean nRet = HelpTools.InsertOrderStatusLog(StaticInfo.dao, statusLogList, errorMessage2);
						
						String shipDate = order.getShipDate();
						Map<String, Object> basicMap1=new HashMap<String, Object>();
						basicMap1.put("COMPANYNO", o_companyNO);
						basicMap1.put("SHIPDATE", shipDate);
						Map<String, Object> orderMap1=new HashMap<String, Object>();
						orderMap1.put("ORDERNO", orderNo);
						orderMap1.put("CHANNELID", kdtId);
						//3.顾客自提
						String shipType = order.getShipType();
						//配送时间不为空时，不调用此接口
						if("2".equals(orderstatus)){
//							agreeProcess(kdtId,basicMap1, null, orderMap1);
						}
						if(nRet)
						{		  		 
							HelpTools.writelog_waimai("【写表DCP_ORDER_STATUSLOG保存成功】"+" 订单号orderNO:"+orderNo);
						}
						else
						{			  		 
							HelpTools.writelog_waimai("【写表DCP_ORDER_STATUSLOG异常】"+errorMessage.toString()+" 订单号orderNO:"+orderNo);
						}
						
						
						StaticInfo.dao.useTransactionProcessData(lstData);
						HelpTools.writelog_waimai("【YOUZAN订单保存数据库成功】"+" 订单号orderNO:"+orderNo);
						res.setCode(0);
						res.setMsg("success");
						res.setDescription("保存成功");
					}
					
				}
			}
			else{
				Log(LogFileName,"\r\n***********有赞消息推送异常-转换异常或订单号为空\r\n入参:\r\n"+com.alibaba.fastjson.JSON.toJSONString(notifyBas));
			}
		}
		catch(Exception e){
			Log(LogFileName,"\r\n***********有赞消息推送保存失败-入参:\r\n"+com.alibaba.fastjson.JSON.toJSONString(notifyBas)+"\r\n"+YouZanUtils.getTrace(e));
			throw e;
		}finally{
		}
		return res;
	}
	
	
	
//	public com.alibaba.fastjson.JSONObject getOrderCompute(YouZanNotifyTradeBuyerPayReq data,Map<String, Object> basicMap,String computeAgain) throws Exception{
////		com.alibaba.fastjson.JSONObject orderStr=getTradePaidTvOrder(data, basicMap,computeAgain);
//		com.alibaba.fastjson.JSONObject orderStr=new com.alibaba.fastjson.JSONObject();
//		String orderNo=data.getFull_order_info().getOrder_info().getTid();
//		String kdtId=data.getKdt_id();
//		String companyNo=basicMap.get("COMPANYNO")==null?"":basicMap.get("COMPANYNO").toString();
//		//调用有赞接口重算折扣、支付金额
//		Map<String, Object> requestMap=new HashMap<String, Object>();
//		requestMap.put("orderNo", orderNo);
//		requestMap.put("kdtId", kdtId);
//		//https://lvzixiannaihongb.isv-dev.youzan.com/order/compute
//		String url=PosPub.getPARA_SMS(StaticInfo.dao, companyNo, "", "YouZan_Compute_Url");
//		url=url+"/order/compute";
//		//3jw8H3sE86ZidjpAKi
//		String secretKey=PosPub.getPARA_SMS(StaticInfo.dao, companyNo, "", "YouZan_Compute_SecretKey");
//		String serialNo=orderNo+"_COMPUTE";
//		String resStr=YouZanUtils.PostDataCust("OrderCompute", url, secretKey, serialNo,requestMap);
//		try{
//			YouZanOrderComputeRes cmp=com.alibaba.fastjson.JSON.parseObject(resStr, new com.alibaba.fastjson.TypeReference<YouZanOrderComputeRes>(){});
//		
//			if(cmp!=null&&cmp.getCode()!=null&&cmp.getCode().equals("200")){
//				//商家实收金额
//				BigDecimal incomeAmt=new BigDecimal(0);
//				//SHIPFEE	实际配送费
//				BigDecimal shipFee=new BigDecimal(0);
//				//支付信息
//				YouZanNotifyTradeBuyerPayReq.FullOrderInfo fullOrderInfo=data.getFull_order_info();
//				YouZanNotifyTradeBuyerPayReq.FullOrderInfo.PayInfo payInfo=fullOrderInfo.getPay_info();
//				//邮费
//				String postFee=payInfo.getPost_fee();
//				if(postFee!=null&&postFee.trim().length()>0){
//					shipFee=new BigDecimal(postFee);
//				}
//				
//				YouZanOrderComputeRes.Data cmpData=cmp.getData();
//				
//				//新的支付信息
//				YouZanOrderComputeRes.Data.PayInfo payInfo2=cmpData.getPayInfo();
//				BigDecimal deductionPay=getDefaultDecimal(payInfo2.getDeductionPay());//deductionPay		储值卡支付金额
////				deductionPay=deductionPay.divide(new BigDecimal(100));
//				BigDecimal pointsPay=getDefaultDecimal(payInfo2.getPointsPay());//pointsPay		积分兑换金额
//				BigDecimal couponPay=getDefaultDecimal(payInfo2.getCouponPay());//couponPay		优惠卷支付金额
//				BigDecimal pointDeductionPay=getDefaultDecimal(payInfo2.getPointDeductionPay());//pointDeductionPay		积分抵现支付金额
//				BigDecimal tuanPay=getDefaultDecimal(payInfo2.getTuanPay());//tuanPay		团购返现
//				BigDecimal cashBackPay=getDefaultDecimal(payInfo2.getCashBackPay());//cashBackPay		订单返现
//				
//				BigDecimal deductionRealPay=getDefaultDecimal(payInfo2.getDeduction_real_pay());
//				
//				JSONArray pays=new JSONArray();
//				//订单付款方式映射
//				String sqlTvMappingPay="select * from tv_mappingpayment t where t.load_doctype='"+orderLoadDocType.YOUZAN+"' and t.cnfflg='Y' and t.companyno='"+companyNo+"' ";
//				//查询付款方式映射
//				List<Map<String, Object>> payMapList=StaticInfo.dao.executeQuerySQL(sqlTvMappingPay, null,false);
//				
//				//有赞推送接口接收到的支付金额数据
//				JSONArray oldPays=orderStr.getJSONArray("pay");
//				com.alibaba.fastjson.JSONObject oldPay=oldPays.getJSONObject(0);
//				//重算后加总数据
////				BigDecimal payMent=deductionPay.add(pointsPay).add(couponPay).add(pointDeductionPay).add(tuanPay).add(cashBackPay).add(deductionRealPay);
////				BigDecimal lastPayMent=new BigDecimal(oldPay.getString("pay")).subtract(payMent);
////				//原支付——重算后加总数据，大于0则保留
////				if(lastPayMent.compareTo(BigDecimal.ZERO)>0){
////					oldPay.put("pay", lastPayMent);
////					pays.add(oldPay);
////					incomeAmt=incomeAmt.add(lastPayMent);
////				}
//				
//				//除会员卡支付以外的支付金额(微信、支付宝、银行卡、有赞E卡、有赞零钱)  详细拆分
//				//此处加总==deduction_real_pay栏位
//				List<YouZanOrderComputeRes.Data.PayInfo.Payment> payments=payInfo2.getPaymentsList();
//				if(payments!=null&&payments.size()>0){
//					BigDecimal totPayment=new BigDecimal(0);
//					//存在拆分明细 
//					for(YouZanOrderComputeRes.Data.PayInfo.Payment payment:payments){
//						BigDecimal pay2=getDefaultDecimal(payment.getPayment());
//						if(pay2.compareTo(BigDecimal.ZERO)>0){
//							totPayment=totPayment.add(pay2);
//							pays=getPayArray(payMapList,pays,oldPay, pay2, payment.getPay_type_name(), "");
//						}
//					}
//					if(totPayment.compareTo(deductionRealPay)!=0){
//						String eDes1="支付详情定制接口中，PayInfo中deduction_real_pay与paymentsList加总不平";
//						Log(LogFileName,"\r\n"+eDes1);
//						throw new Exception(eDes1);
//					}
//					
//				}else{
//					pays=getPayArray(payMapList,pays,oldPay, deductionRealPay, "DEDUCTIONREAL", "储值卡以外的支付");
//				}
//				
//				
//				
//				
//				pays=getPayArray(payMapList,pays,oldPay, deductionPay, "CARD_PAY", "储值卡支付");
//				pays=getPayArray(payMapList,pays,oldPay, pointsPay, "POINTS", "积分兑换金额");
//				pays=getPayArray(payMapList,pays,oldPay, couponPay, "COUPONS", "优惠券支付金额");
//				pays=getPayArray(payMapList,pays,oldPay, pointDeductionPay, "POINTDEDUCTION", "积分抵现支付金额");
//				pays=getPayArray(payMapList,pays,oldPay, tuanPay, "TUANPAY", "团购返现");
//				pays=getPayArray(payMapList,pays,oldPay, cashBackPay, "CASHBACKPAY", "订单返现");
//				
//				orderStr.put("pay", pays);
//				
//				String manualNO=cmpData.getSelffetchCode();
//				orderStr.put("manualNO", manualNO);
//						
//				incomeAmt=incomeAmt.add(deductionPay).add(pointsPay).add(couponPay).add(pointDeductionPay).add(tuanPay).add(cashBackPay).add(deductionRealPay);
//				
//				//优惠信息
//				YouZanOrderComputeRes.Data.DiscountInfo disInfo2=cmpData.getDiscountInfo();
////				BigDecimal groupOnPromotion=getDefaultDecimal(disInfo2.getGroupOnPromotion());//groupOnPromotion		多人拼团
////				BigDecimal seckillPronmotion=getDefaultDecimal(disInfo2.getSeckillPronmotion());//seckillPronmotion		秒杀
////				BigDecimal presentExchangePromotion=getDefaultDecimal(disInfo2.getPresentExchangePromotion());//presentExchangePromotion		赠品
////				BigDecimal customerDiscountPromotion=getDefaultDecimal(disInfo2.getCustomerDiscountPromotion());//customerDiscountPromotion		会员折扣金额
////				BigDecimal timelimitedDiscountPromotion=getDefaultDecimal(disInfo2.getTimelimitedDiscountPromotion());//timelimitedDiscountPromotion		限时折扣金额
////				BigDecimal meetReducePromotion=getDefaultDecimal(disInfo2.getMeetReducePromotion());//meetReducePromotion		满减送金额
//				
//				List<YouZanOrderComputeRes.Data.Good> detailList=cmpData.getGoods();
//				com.alibaba.fastjson.JSONArray detailArray = new com.alibaba.fastjson.JSONArray();
////				BigDecimal totDisc=new BigDecimal(0);
//				BigDecimal totOldAmt=new BigDecimal(0);
//				BigDecimal detailTotDisc=new BigDecimal(0);
//				if(detailList!=null&&detailList.size()>0){
//					int detailItem=1;
//					//单身 begin
//					for(YouZanOrderComputeRes.Data.Good detail:detailList){
//						com.alibaba.fastjson.JSONObject goodObj = new com.alibaba.fastjson.JSONObject();
//						goodObj.put("orderNO", orderNo); //订单编号
//						goodObj.put("item", detailItem); //ITEM	项次
//						String barCode=detail.getOuterSkuId();
//						if(barCode==null||barCode.trim().length()<=0){
//							barCode=detail.getOuterItemId();
//						}
//						String pluNo="";
//						String pluName="";
//						String unit="";
//						String pluNoSql="SELECT A.* FROM TB_GOODS A LEFT JOIN TB_BARCODE B"
//								+ " ON A.COMPANYNO=B.COMPANYNO AND A.PLUNO=B.PLUNO "
//								+ " WHERE A.COMPANYNO='"+companyNo+"' AND B.PLUBARCODE='"+barCode+"'";
//						List<Map<String, Object>> pluNoList = StaticInfo.dao.executeQuerySQL(pluNoSql, null);
//						if(pluNoList!=null&&pluNoList.size()>0){
//							Map<String, Object> pluNoMap=pluNoList.get(0);
//							pluNo=pluNoMap.get("PLUNO")==null?"":pluNoMap.get("PLUNO").toString();
//							pluName=pluNoMap.get("PLUNAME")==null?"":pluNoMap.get("PLUNAME").toString();
//							unit=pluNoMap.get("WUNIT")==null?"":pluNoMap.get("WUNIT").toString();
//						}
//						if(pluName==null||pluName.trim().length()==0){
//							pluName=detail.getTitle();
//						}
//						goodObj.put("packageType", "1"); ////套餐类型 1、正常商品 2、套餐主商品  3、套餐子商品
//						goodObj.put("pluBarcode", barCode); //
//						goodObj.put("pluNO", pluNo); //
//						//title;//商品名称
//						goodObj.put("pluName", pluName); //
//						String specName="";
//						//有赞传输格式 "sku_properties_name":"[{"k":"规格","k_id":14,"v":"csc","v_id":3815911}]"
//						if(detail.getSkuPropertiesName()!=null){
//							try{
//								com.alibaba.fastjson.JSONArray pluNameArray = com.alibaba.fastjson.JSONArray.parseArray(detail.getSkuPropertiesName());
//								if(pluNameArray!=null&&pluNameArray.size()>0){
//									List<String> pluNameL=new ArrayList<String>();
//									for(int i=0;i<pluNameArray.size();i++){
//										com.alibaba.fastjson.JSONObject jo=pluNameArray.getJSONObject(i);
//										String vl=jo.getString("v");
//										if(vl!=null&&vl.trim().length()>0){
//											pluNameL.add(vl);
//										}
//									}
//									if(pluNameL!=null&&pluNameL.size()>0){
//										specName=String.join(";", pluNameL);
//									}
//								}
//							}catch(Exception e){
//								
//							}
//							
//						}
//						goodObj.put("specName", specName);//SPECNAME	规格名称
//						goodObj.put("attrName", ""); //
//						goodObj.put("unit", unit); //
//						goodObj.put("goodsGroup", ""); //
//						//price;//单商品原价
//						BigDecimal price=new BigDecimal(0);
//						if(YouZanUtils.isNotEmpty(detail.getPrice())){
//							price=new BigDecimal(detail.getPrice());
//						}
//						goodObj.put("price", price);//零售价 PRICE 商品原价
//						//total_fee;//商品优惠后总价
//						BigDecimal amt=new BigDecimal(0);
//						if(YouZanUtils.isNotEmpty(detail.getTotalFee())){
//							amt=new BigDecimal(detail.getTotalFee());
//						}
//						goodObj.put("amt", amt);//金额 AMT	 NUMBER(23,8) 金额 = 原价 * 数量 - 折扣额
//						totOldAmt=totOldAmt.add(amt);
//						//num;//商品数量
//						BigDecimal qty=new BigDecimal(0);
//						if(YouZanUtils.isNotEmpty(detail.getNum())){
//							qty=new BigDecimal(detail.getNum());
//						}
//						goodObj.put("qty", qty);//QTY	数量
//						BigDecimal disc=price.multiply(qty).subtract(amt);
//						detailTotDisc=detailTotDisc.add(disc);
//						goodObj.put("disc", disc);//折扣额		DISC	NUMBER(23,8)
//						goodObj.put("boxNum", "0");
//						goodObj.put("boxPrice", "0");
//						goodObj.put("isMemo", "N");
//						goodObj.put("orderSn", detail.getOid());
//						detailItem++;
//						detailArray.add(goodObj);
//					}
//				}
//				orderStr.put("goods", detailArray);
//				//totOldAmt  商品金额加总+商品折扣加总，等价于商品单价*数量加总
//				totOldAmt=totOldAmt.add(shipFee).add(detailTotDisc);
//
//				BigDecimal totDisc=new BigDecimal(0);
//				//总折扣   商品单价*数量加总-总收入，商品折扣加总可能小于总折扣(存在订单级折扣)
//				totDisc=totOldAmt.subtract(incomeAmt);
//				orderStr.put("tot_oldAmt", totOldAmt); //订单原价
//				orderStr.put("tot_Amt", incomeAmt); //订单总价
//				orderStr.put("totDisc", totDisc); //订单优惠总额
//				orderStr.put("sellerDisc", totDisc); //商户优惠总额
//				orderStr.put("payAmt", incomeAmt); //PAYAMT	已付金额
//				orderStr.put("incomeAmt", incomeAmt); //incomeAmt	实付
//				
//			}
//		
//		}catch(Exception e){
////			/
//			throw e;
//		}
//		return orderStr;
//	}
	
	public JSONArray getPayArray(List<Map<String, Object>> payMapList,JSONArray pays,com.alibaba.fastjson.JSONObject pay,BigDecimal big,String orderPayCode,String payName) throws Exception{
		if(big.compareTo(BigDecimal.ZERO)>0){
			
			Map<String, Object> payMap=new HashMap<String, Object>();
			if(payMapList!=null&&payMapList.size()>0){
				//过滤付款方式映射
				List<Map<String, Object>> getQPAY=payMapList.stream().filter(g->orderPayCode.equals(g.get("ORDER_PAYCODE"))).collect(Collectors.toList());
				if(getQPAY!=null&&getQPAY.size()>0){
					payMap=getQPAY.get(0);
					
				}
				else{
					getQPAY=payMapList.stream().filter(g->"ALL".equals(g.get("ORDER_PAYCODE"))).collect(Collectors.toList());
					if (getQPAY!=null && getQPAY.size()>0) {
						payMap=getQPAY.get(0);
					}
				}
			}
			
			String paycode=payMap.get("PAYCODE")==null?"":payMap.get("PAYCODE").toString();
			String paycodeERP=payMap.get("PAYCODEERP")==null?"":payMap.get("PAYCODEERP").toString();
			payName=payMap.get("PAYNAME")==null?payName:payMap.get("PAYNAME").toString();
			String bdate=pay.getString("bdate");
			
			//储值卡支付金额
//			if("CARD_PAY".equals(orderPayCode)){
//				pays=new JSONArray();
//				String payMentStr=pay.getString("pay");
//				BigDecimal payMent=getDefaultDecimal(payMentStr);
//				BigDecimal lastPayMent=payMent.subtract(big);
//				if(lastPayMent.compareTo(BigDecimal.ZERO)>0){
//					pay.put("pay", lastPayMent);
//					pays.add(pay);
//				}
//			}
			
			com.alibaba.fastjson.JSONObject payObj=new com.alibaba.fastjson.JSONObject();
			payObj.put("item", pays.size()+1);
			payObj.put("payCode", paycode);
			payObj.put("payCodeerp", paycodeERP);
			payObj.put("payName", payName);
			payObj.put("cardNO", "");//CARDNO	支付卡券号
			payObj.put("ctType", "");//CTTYPE	卡券类型
			payObj.put("paySernum", "");//PAYSERNUM 支付订单号
			payObj.put("serialNO", "");//SERIALNO	银联卡流水号
			payObj.put("refNO", "");//REFNO	银联卡交易参考号
			payObj.put("teriminalNO", "");//TERIMINALNO	银联终端号
			payObj.put("descore", "");//DESCORE	积分抵现扣减
			payObj.put("pay", big);//PAY	金额
			payObj.put("extra", 0);//EXTRA	溢收金额
			payObj.put("changed", 0);//CHANGED	找零
			payObj.put("bdate", bdate);//BDATE	收银营业日期
			payObj.put("isOrderpay", "N");//ISORDERPAY	是否订金
			payObj.put("order_PayCode", orderPayCode);//ORDER_PAYCODE	平台支付方式编码
			pays.add(payObj);
		}
		
		return pays;
	}
	
	public order getTradePaidTvOrder
	(YouZanNotifyTradeBuyerPayReq data,Map<String, Object> basicMap) throws Exception{
		order orderStr=new order();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdf4 = new SimpleDateFormat("HHmmss");
		SimpleDateFormat sdf5 = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		SimpleDateFormat sdf6 = new SimpleDateFormat("yyyy年MM月dd日");
		SimpleDateFormat sdf7 = new SimpleDateFormat("MMdd");
		SimpleDateFormat sdf8 = new SimpleDateFormat("HH:mm");
		
		String kdtId=data.getKdt_id();
		
		Date dateNow=new Date();
		String updateTime=sdf5.format(dateNow);
		YouZanNotifyTradeBuyerPayReq.FullOrderInfo fullOrderInfo=data.getFull_order_info();
		YouZanNotifyTradeBuyerPayReq.FullOrderInfo.OrderInfo orderInfo= fullOrderInfo.getOrder_info();
		YouZanNotifyTradeBuyerPayReq.FullOrderInfo.RemarkInfo remarkInfo=fullOrderInfo.getRemark_info();
		String memo="";
		if(remarkInfo!=null){
			memo=remarkInfo.getBuyer_message();
		}
		
		orderStr.setMemo(memo);
		
		String orderNo=orderInfo.getTid();
		
		//SHIPTYPE 配送方式
		//1.外卖平台配送 2.门店配送(台湾显示为宅配) 3.顾客自提 5 总部配送 6.超商
		String shipType="";
		//有赞 物流类型 0:快递发货; 1:到店自提; 2:同城配送; 9:无需发货（虚拟商品订单）
		String expressType=orderInfo.getExpress_type();
		if("1".equals(expressType)){//1:到店自提
			shipType="3";
		}else if("2".equals(expressType)){//2:同城配送
			shipType="1";
		}else if("0".equals(expressType)){//0:快递发货
			shipType="2";
		}else if("9".equals(expressType)){//9:无需发货
			shipType="";
		}
		//此处修改 OrderProcessServiceImp需做相应修改
		
		//订单收货地址信息结构体
		YouZanNotifyTradeBuyerPayReq.FullOrderInfo.AddressInfo addInfo=fullOrderInfo.getAddress_info();
		String shipDate="";
		String shipSTime="";//配送开始时间
		String shipETime="";//配送结束时间
		//自提点ID
		String selfFetchId="";
		//自提时
		if("3".equals(shipType)){
			String selfFetchInfo=addInfo.getSelf_fetch_info();//自提信息
			if(selfFetchInfo!=null&&selfFetchInfo.trim().length()>0){
				com.alibaba.fastjson.JSONObject selfFetchJson = new com.alibaba.fastjson.JSONObject();
				selfFetchJson = JSON.parseObject(selfFetchInfo,Feature.OrderedField);
				//有赞提供的日期没有年，此处需要补上，注意跨年的情况
				String userTime = selfFetchJson.getString("user_time");
				selfFetchId = selfFetchJson.getString("id");
				Boolean isContinueCheckTime=true;
				if(userTime!=null&&userTime.length()>6){
					try{
						//yyyy-MM-dd HH:mm:ss格式不需要处理
						Date shipDateDate=sdf1.parse(userTime);
						shipDate=sdf3.format(shipDateDate);
						shipSTime=sdf4.format(shipDateDate);
						shipETime=sdf4.format(shipDateDate);
						isContinueCheckTime=false;
					}catch(Exception e){
						
					}
				}
				if(isContinueCheckTime){
					try{
						if(userTime.contains("年")){
							//处理格式为:"2020年08月27日 19:00-20:00"的数据
							shipDate=userTime.substring(0,11);
							Date shipDateDate=sdf6.parse(shipDate);
							shipDate=sdf3.format(shipDateDate);//转换为yyyyMMdd
							//得到19:00-20:00
							String shipTime=userTime.substring(userTime.length()-11, userTime.length());
							shipSTime=shipTime.substring(0,2)+shipTime.substring(3,5);
							shipETime=shipTime.substring(6,8)+shipTime.substring(9,11);
							isContinueCheckTime=false;
						}else{
							//处理格式为:"08月27日 19:00-20:00"的数据
							shipDate=updateTime.substring(0,4)+"年"+userTime.substring(0,6);
							Date shipDateDate=sdf6.parse(shipDate);
							//有赞传过来的日期，补上当前年份后的时间
							Calendar calendar1 = Calendar.getInstance();
							calendar1.setTime(shipDateDate);
							//当前时间
							Calendar calendar2 = Calendar.getInstance();
							long diffDays = (calendar2.getTimeInMillis() - calendar1.getTimeInMillis())
									/ (1000 * 60 * 60 * 24);
							if(diffDays>30){
								calendar1.add(Calendar.YEAR, 1);
							}
							shipDate=sdf3.format(calendar1.getTime());//转换为yyyyMMdd
							//得到19:00-20:00
							String shipTime=userTime.substring(userTime.length()-11, userTime.length());
							shipSTime=shipTime.substring(0,2)+shipTime.substring(3,5);
							isContinueCheckTime=false;
						}
						
					}catch(Exception e){
						
					}
				}
					
				if(shipSTime==null||shipSTime.trim().length()<1){
					shipSTime="000000";
					shipETime="235959";
				}
			}
			
			String selffetchCode=getSelffetchCode(orderNo, null, null, basicMap);
			orderStr.setManualNo(selffetchCode);
		}else{
			//格式 2020-08-27 18:00:00
			String deliveryStartTime=addInfo.getDelivery_start_time();
			String deliveryEndTime=addInfo.getDelivery_end_time();
			try{
				Date shipDateDate1=sdf1.parse(deliveryStartTime);
				Date shipDateDate2=sdf1.parse(deliveryEndTime);
				shipDate=sdf3.format(shipDateDate1);
				shipSTime=sdf4.format(shipDateDate1);
				shipETime=sdf4.format(shipDateDate2);
			}catch(Exception e){
				
			}
		}
		//配送时
		
		if(shipDate!=null&&shipDate.trim().length()>8){
			shipDate="";
		}
		
		orderStr.setShipType(shipType);
		orderStr.setShipDate(shipDate); //配送日期
		orderStr.setShipStartTime(shipSTime);; //配送时间（格式HHmmss）
		orderStr.setShipEndTime(shipETime);; //配送时间（格式HHmmss）
		orderStr.setSn("");
				
		String orderShop="";
		//网点门店
		String offlineId=orderInfo.getOffline_id();
		String companyNo=basicMap.get("EID")==null?"":basicMap.get("EID").toString();
		if((offlineId==null||offlineId.trim().length()<=0)&&selfFetchId!=null&&selfFetchId.length()>0){
			offlineId=selfFetchId;
		}
		if(offlineId==null||offlineId.trim().length()<=0){
			String shopSn=basicMap.get("SHIPPINGSHOPNO")==null?"":basicMap.get("SHIPPINGSHOPNO").toString();
			offlineId=shopSn;
			orderShop=kdtId;
		}else{
			orderShop=offlineId;
		}
		String shopSql="SELECT A.*,C.ORG_NAME AS CSHOPNAME, " 
				+ " B.MACHORGANIZATIONNO AS MACHORGANIZATIONNO,"
				+ " D.ORG_NAME AS MACHORGANIZATIONNAME "
				+ " FROM DCP_MAPPINGSHOP A "
				+ " LEFT JOIN DCP_ORG B ON A.EID=B.EID AND A.SHOPID=B.ORGANIZATIONNO "
				+ " LEFT JOIN DCP_ORG_LANG C ON B.EID=C.EID AND B.ORGANIZATIONNO=C.ORGANIZATIONNO AND C.LANG_TYPE='"+langType+"' AND C.STATUS='100' "
				+ " LEFT JOIN DCP_ORG_LANG D ON B.EID=D.EID AND B.MACHORGANIZATIONNO=D.ORGANIZATIONNO AND D.LANG_TYPE='"+langType+"' AND D.STATUS='100' "
				+ " WHERE A.EID='"+companyNo+"' "
				+ " AND INSTR(LOWER(A.LOAD_DOCTYPE),'"+orderLoadDocType.YOUZAN+"')>0 "
				+ " AND A.ORDERSHOPNO='"+offlineId+"'";
		List<Map<String, Object>> shopMaps=StaticInfo.dao.executeQuerySQL(shopSql, null, false);
		if(shopMaps!=null&&shopMaps.size()>0){
			Map<String, Object> shopMap=shopMaps.get(0);
			String thisShop=shopMap.get("SHIPPINGSHOPNO")==null?"":shopMap.get("SHIPPINGSHOPNO").toString();
			String thisShopName=shopMap.get("CSHOPNAME")==null?"":shopMap.get("CSHOPNAME").toString();
			String thisMachNo=shopMap.get("MACHORGANIZATIONNO")==null?"":shopMap.get("MACHORGANIZATIONNO").toString();
			String thisMachName=shopMap.get("MACHORGANIZATIONNAME")==null?"":shopMap.get("MACHORGANIZATIONNAME").toString();
			basicMap.put("SHIPPINGSHOPNO", thisShop);
			basicMap.put("SHOPNAME", thisShopName);
			basicMap.put("MACHORGANIZATIONNO", thisMachNo);
			basicMap.put("MACHORGANIZATIONNAME", thisMachName);
		}
		orderStr.setChannelId(kdtId);
		orderStr.setOrderShop(orderShop);
		
		String machNo=basicMap.get("MACHORGANIZATIONNO")==null?"":basicMap.get("MACHORGANIZATIONNO").toString();
		String machName=basicMap.get("MACHORGANIZATIONNAME")==null?"":basicMap.get("MACHORGANIZATIONNAME").toString();
		orderStr.seteId(companyNo);;//企业编号
		String shopNo=basicMap.get("SHIPPINGSHOPNO")==null?"":basicMap.get("SHIPPINGSHOPNO").toString();
		String shopName=basicMap.get("SHOPNAME")==null?"":basicMap.get("SHOPNAME").toString();
		orderStr.setShopNo(shopNo);; //门店编号 组织门店给相同值
		if(shopNo==null||shopNo.trim().length()<1){
			throw new Exception("门店ID/网点编号["+offlineId+"]未配置");
		}
		orderStr.setShopName(shopName); //门店编号 组织门店给相同值
		orderStr.setOrderNo(orderNo);; //订单编号
		orderStr.setLoadDocOrderNo(orderNo);//渠道单号
		orderStr.setLoadDocType(orderLoadDocType.YOUZAN); //来源类型
		//订单创建时间-2018-01-01 00:00:00
		String created=orderInfo.getCreated();
		Date tempDate = sdf1.parse(created);
		//下单时间		CREATE_DATETIME 格式参照数据库
		orderStr.setCreateDatetime(sdf2.format(tempDate));
		
		//商家实收金额
		BigDecimal incomeAmt=new BigDecimal(0);
		//SHIPFEE	实际配送费
		BigDecimal shipFee=new BigDecimal(0);
		//支付信息
		YouZanNotifyTradeBuyerPayReq.FullOrderInfo.PayInfo payInfo=fullOrderInfo.getPay_info();
		//最终支付价格 payment=orders.payment的总和
		String payment=payInfo.getPayment();
		if(payment!=null&&payment.toString().trim().length()>0){
			incomeAmt=new BigDecimal(payment.toString().trim());
		}
		//邮费
		String postFee=payInfo.getPost_fee();
		if(postFee!=null&&postFee.trim().length()>0){
			shipFee=new BigDecimal(postFee);
		}
		orderStr.setTot_shipFee(shipFee.toPlainString());; //TOTSHIPFEE	总配送费
		orderStr.setRshipFee("0");//RSHIPFEE	配送费减免
		orderStr.setShipFee(shipFee.doubleValue());; //SHIPFEE	实际配送费
		//优惠信息
		YouZanNotifyTradeBuyerPayReq.OrderPromotion promot=data.getOrder_promotion();
		
		String receiverName=addInfo.getReceiver_name();
		if(receiverName!=null&&receiverName.length()>20){
			receiverName=receiverName.substring(0,20);
		}
		orderStr.setGetMan(receiverName);//GETMAN	取货人/收货人
		orderStr.setGetManTel(addInfo.getReceiver_tel());//GETMANTEL	取货人电话
		orderStr.setProvince(addInfo.getDelivery_province());//PROVINCE	省份
		orderStr.setCity(addInfo.getDelivery_city());//CITY	城市
		orderStr.setCounty(addInfo.getDelivery_district());//COUNTY	国家 文档注释为区
		orderStr.setAddress(addInfo.getDelivery_address());//ADDRESS	配送地址
		
		//订单买家信息结构体
		YouZanNotifyTradeBuyerPayReq.FullOrderInfo.BuyerInfo buyerInfo=fullOrderInfo.getBuyer_info();
		//fans_nickname 粉丝昵称
		//buyer_phone 买家手机号
		String contMan=buyerInfo.getFans_nickname();
		if(contMan==null||contMan.trim().length()==0){
			contMan=addInfo.getReceiver_name();
		}
		if(contMan!=null&&contMan.length()>20){
			contMan=contMan.substring(0,20);
		}
		orderStr.setContMan(contMan);//CONTMAN	联系人/订购人
		orderStr.setContTel(buyerInfo.getBuyer_phone());//CONTTEL	联系电话/订购人 
		
		
		//TOT_QTY	总数量
		BigDecimal totQty=new BigDecimal(0);
		
		//单身 begin
		List<orderGoodsItem> detailArray= new ArrayList<orderGoodsItem>();
		List<YouZanNotifyTradeBuyerPayReq.FullOrderInfo.Orders> detailList=fullOrderInfo.getOrders();
		if(detailList!=null&&detailList.size()>0){
			int detailItem=1;
			for(YouZanNotifyTradeBuyerPayReq.FullOrderInfo.Orders detail:detailList){
				orderGoodsItem goodObj = new orderGoodsItem();
				goodObj.setItem(String.valueOf(detailItem)); //ITEM	项次
				goodObj.setPackageType("1"); ////套餐类型 1、正常商品 2、套餐主商品  3、套餐子商品
				String barCode=detail.getOuter_sku_id();
				if(barCode==null||barCode.trim().length()<=0){
					barCode=detail.getOuter_item_id();
				}
				String pluNo="";
				String pluName="";
				String unit="";
				
				String pluNoSql="SELECT A.*,C.PLU_NAME AS PLUNAME "
						+ " FROM DCP_GOODS A "
						+ " LEFT JOIN DCP_GOODS_BARCODE B ON A.EID=B.EID AND A.PLUNO=B.PLUNO "
						+ " LEFT JOIN DCP_GOODS_LANG C ON A.EID=C.EID AND A.PLUNO=C.PLUNO AND C.LANG_TYPE='"+langType+"'"
						+ " WHERE A.EID='"+companyNo+"' AND B.PLUBARCODE='"+barCode+"'";
				List<Map<String, Object>> pluNoList = StaticInfo.dao.executeQuerySQL(pluNoSql, null);
				if(pluNoList!=null&&pluNoList.size()>0){
					Map<String, Object> pluNoMap=pluNoList.get(0);
					pluNo=pluNoMap.get("PLUNO")==null?"":pluNoMap.get("PLUNO").toString();
					pluName=pluNoMap.get("PLUNAME")==null?"":pluNoMap.get("PLUNAME").toString();
					unit=pluNoMap.get("WUNIT")==null?"":pluNoMap.get("WUNIT").toString();
				}
				if(pluName==null||pluName.trim().length()==0){
					pluName=detail.getTitle();
				}
				goodObj.setPluBarcode(barCode);
				goodObj.setPluNo(pluNo);
				//title;//商品名称
				goodObj.setPluName(pluName);
				String specName="";
				//有赞传输格式 "sku_properties_name":"[{"k":"规格","k_id":14,"v":"csc","v_id":3815911}]"
				if(detail.getSku_properties_name()!=null){
					try{
						com.alibaba.fastjson.JSONArray pluNameArray = com.alibaba.fastjson.JSONArray.parseArray(detail.getSku_properties_name());
						if(pluNameArray!=null&&pluNameArray.size()>0){
							List<String> pluNameL=new ArrayList<String>();
							for(int i=0;i<pluNameArray.size();i++){
								com.alibaba.fastjson.JSONObject jo=pluNameArray.getJSONObject(i);
								String vl=jo.getString("v");
								if(vl!=null&&vl.trim().length()>0){
									pluNameL.add(vl);
								}
							}
							if(pluNameL!=null&&pluNameL.size()>0){
								specName=String.join(";", pluNameL);
							}
						}
					}catch(Exception e){
						
					}
					
				}
				goodObj.setSpecName(specName);//SPECNAME	规格名称
				goodObj.setAttrName(""); //
				goodObj.setsUnit(unit);; //
				goodObj.setGoodsGroup(""); //
				//price;//单商品原价
				BigDecimal price=new BigDecimal(0);
				if(YouZanUtils.isNotEmpty(detail.getPrice())){
					price=new BigDecimal(detail.getPrice());
				}
				goodObj.setPrice(price.doubleValue());//零售价 PRICE 商品原价
				//total_fee;//商品优惠后总价
				BigDecimal amt=new BigDecimal(0);
				if(YouZanUtils.isNotEmpty(detail.getPayment())){
					amt=new BigDecimal(detail.getPayment());
				}
				goodObj.setAmt(amt.doubleValue());//金额 AMT	 NUMBER(23,8) 金额 = 原价 * 数量 - 折扣额
				
				//num;//商品数量
				BigDecimal qty=new BigDecimal(0);
				if(YouZanUtils.isNotEmpty(detail.getNum())){
					qty=new BigDecimal(detail.getNum());
				}
				totQty=totQty.add(qty);
				goodObj.setQty(qty.doubleValue());//QTY	数量
				BigDecimal disc=price.multiply(qty).subtract(amt);
				goodObj.setDisc(disc.doubleValue());;//折扣额		DISC	NUMBER(23,8)
				goodObj.setBoxNum(0);
				goodObj.setBoxPrice(0);
				goodObj.setIsMemo("N");
//				goodObj.put("orderSn", detail.getOid());
				detailItem++;
				detailArray.add(goodObj);
			}
		}
		orderStr.setGoodsList(detailArray);

		//单身 end
		
		List<Map<String, Object>> payMapList=new ArrayList<Map<String, Object>>();
		//付款信息
		List<orderPay> payArray=new ArrayList<orderPay>();
		{
			String sqlTvMappingPay="select * from DCP_MAPPINGPAYMENT t where t.load_doctype='"+orderLoadDocType.YOUZAN+"' and t.STATUS='100' and t.EID='"+companyNo+"' ";
			//查询付款方式映射
			payMapList=StaticInfo.dao.executeQuerySQL(sqlTvMappingPay, null,false);
			//对应的ERP支付方式
			String paycode="";
			String payName="";
			String paycodeERP="";

			String payTransaction="";
			if(payInfo.getTransaction()!=null&&payInfo.getTransaction().size()>0){
				payTransaction=String.join("|", payInfo.getTransaction());
			}
			if(payTransaction!=null&&payTransaction.length()>99){
				payTransaction=payTransaction.substring(0,99);
			}
			//支付类型 0:默认值,未支付; 1:微信自有支付; 2:支付宝wap; 3:支付宝wap; 
			//5:财付通; 7:代付; 8:联动优势; 9:货到付款; 10:大账号代销; 11:受理模式; 
			//12:百付宝; 13:sdk支付; 14:合并付货款; 15:赠品; 16:优惠兑换; 17:自动付货款; 
			//18:爱学贷; 19:微信wap; 20:微信红包支付; 21:返利; 22:ump红包; 24:易宝支付; 
			//25:储值卡; 27:qq支付; 28:有赞E卡支付; 29:微信条码; 30:支付宝条码; 33:礼品卡支付; 
			//35:会员余额; 36:信用卡银联支付; 37:储蓄卡银联支付; 40:分期支付; 
			//72:微信扫码二维码支付; 80:待结算&余额支付; 90:礼品卡支付; 100:代收账户; 
			//300:储值账户; 400:保证金账户; 101:收款码; 102:微信; 103:支付宝; 104:刷卡; 
			//105:二维码台卡; 106:储值卡; 107:有赞E卡; 110:标记收款-自有微信支付; 
			//111:标记收款-自有支付宝; 112:标记收款-自有POS刷卡; 113:通联刷卡支付; 
			//114:标记收款-自定义; 115:有赞零钱支付; 200:记账账户; 201:现金; 202:组合支付; 203:外部支付;
			String payType=orderInfo.getPay_type();
			String payTypeName=orderInfo.getPay_type_name();
			//过滤付款方式映射
			List<Map<String, Object>> getQPAY=payMapList.stream().filter(g->payTypeName.equals(g.get("ORDER_PAYCODE"))).collect(Collectors.toList());
			if (getQPAY==null || getQPAY.size()==0) 
			{
				getQPAY=payMapList.stream().filter(g->"ALL".equals(g.get("ORDER_PAYCODE"))).collect(Collectors.toList());
				if (getQPAY!=null && getQPAY.size()>0) 
				{
					paycode=getQPAY.get(0).get("PAYCODE").toString();
					payName=getQPAY.get(0).get("PAYNAME").toString();
					paycodeERP=getQPAY.get(0).get("PAYCODEERP").toString();
				}
			}
			else 
			{
				paycode=getQPAY.get(0).get("PAYCODE").toString();
				payName=getQPAY.get(0).get("PAYNAME").toString();
				paycodeERP=getQPAY.get(0).get("PAYCODEERP").toString();
			}
			
			orderPay payObj = new orderPay();
			payObj.setItem(String.valueOf(payArray.size()+1));
			payObj.setPayCode(paycode);
			payObj.setPayCodeErp(paycodeERP);
			payObj.setPayName(payName);
			payObj.setCardNo("");//CARDNO	支付卡券号
			payObj.setCtType("");//CTTYPE	卡券类型
			payObj.setPaySerNum(payTransaction);//PAYSERNUM 支付订单号
			payObj.setSerialNo("");//SERIALNO	银联卡流水号
			payObj.setRefNo("");//REFNO	银联卡交易参考号
			payObj.setTeriminalNo("");//TERIMINALNO	银联终端号
			payObj.setDescore("");//DESCORE	积分抵现扣减
			payObj.setPay(incomeAmt.toPlainString());//PAY	金额
			payObj.setExtra("0");//EXTRA	溢收金额
			payObj.setChanged("0");//CHANGED	找零
			payObj.setbDate(sdf3.format(tempDate));//BDATE	收银营业日期
			payObj.setIsOrderPay("N");//ISORDERPAY	是否订金
			payObj.setOrder_payCode(payType);//ORDER_PAYCODE	平台支付方式编码
			payArray.add(payObj);
		}
		

		

		
		
		String isinvoicedStr = "N";//是否开发票
		String invoiceTitle = "";//发票抬头
		String invoiceType="";//1.个人 2.企业
		String taxpayerId="";//税号
		//InvoiceInfo 发票信息
		YouZanNotifyTradeBuyerPayReq.FullOrderInfo.InvoiceInfo invoiceInfo=fullOrderInfo.getInvoice_info();
		if(invoiceInfo!=null){
			invoiceTitle=invoiceInfo.getUser_name();
			if(invoiceTitle!=null&&invoiceTitle.trim().length()>0){
				isinvoicedStr = "Y";
			}
			//raise_type;//抬头类型（个人/企业） * personal 个人 * enterprise 企业
			String raiseType=invoiceInfo.getRaise_type();
			if(raiseType!=null&&raiseType.trim().length()>0){
				if("personal".toUpperCase().equals(raiseType)){
					invoiceType="1";
				}else if("enterprise".toUpperCase().equals(raiseType)){
					invoiceType="2";
				}
			}
			taxpayerId=invoiceInfo.getTaxpayer_id();
		}
//		orderStr.put("isInvoice", isinvoicedStr);//是否开发票
//		orderStr.put("invoiceType", invoiceType);//发票类型
//		orderStr.put("invoiceTitle", invoiceTitle);//发票抬头
//		orderStr.put("taxRegnumber", taxpayerId);//纳税人识别号
		
		
		orderStr.setIsShipCompany("N");//总部配送 Y/N	
		String selfPickDispatching="N";//自提是否启用调度
		String enableDispatching = "N";//配送调度
		String status = "2";// 订单状态 默认成2-已接单
		//自提
		if("3".equals(shipType)){
			Object dispa = basicMap.get("SELFPICKDISPATCH");//自提是否启用调度
			if (dispa != null && dispa.toString().trim().equals("Y")) {
				selfPickDispatching = "Y";
			}
			// 启用调度
			if ("Y".equals(selfPickDispatching)) {
				status = "0";// 0-需调度
			}
		}else{
			// 非自提时，判断ENABLE_DISPATCHING ：配送是否启用调度
			Object dispa = basicMap.get("ENABLE_DISPATCHING");//ENABLE_DISPATCHING	配送是否启用调度
			if (dispa != null && dispa.toString().trim().equals("Y")) {
				enableDispatching = "Y";
			}
			// 启用调度
			if ("Y".equals(enableDispatching)) {
				status = "0";// 0-需调度
			}
		}
		
		String orderTakeSql="SELECT A.* FROM DCP_ORG_ORDERTAKESET A "
				+ " WHERE A.EID='"+companyNo+"' "
				+ " AND INSTR(LOWER(A.LOADDOCTYPE),'"+orderLoadDocType.YOUZAN+"')>0 "
				+ " AND A.ORGANIZATIONNO='"+shopNo+"'";
		List<Map<String, Object>> listOrdertakeSet=StaticInfo.dao.executeQuerySQL(orderTakeSql, null,false);
		if (listOrdertakeSet != null && listOrdertakeSet.size() > 0) {
			Object iSOrdertakeObj = listOrdertakeSet.get(0).get("ISORDERTAKE");
			// 为Y，默认单据状态为1.订单开立
			if (iSOrdertakeObj != null && iSOrdertakeObj.toString().trim().equals("Y")) {
				status = "1";
			}
		}
		orderStr.setStatus(status);
		//"1.未申请 2.用户申请退单 3.拒绝退单 4.客服仲裁中 
		//5.退单失败 6.退单成功 7.用户申请部分退款 8.拒绝部分退款 
		//9 部分退款失败 10.部分退款成功 11.用户申请取消订单 
		//12.取消订单失败 13.取消订单成功 14.拒绝取消订单"
		orderStr.setRefundStatus("1");

			
		String shippingShopNO="";
		String shippingShopName="";
		String machShopNO="";
		String machShopName="";
		//门店处理
		shippingShopNO=shopNo;
		shippingShopName=shopName;
		machShopNO=machNo;
		machShopName=machName;
		//订单开立或已接单时，生产门店为空，默认给下定门店
		if(("1".equals(status)||"2".equals(status))&&(machShopNO==null||machShopNO.trim().length()<1)){
			machShopNO=shopNo;
			machShopName=shopName;
		}
		
		
		orderStr.setShippingShopNo(shippingShopNO); // 配送门店
		orderStr.setShippingShopName(shippingShopName); // 配送门店
		orderStr.setMachShopNo(machShopNO); // 生产门店
		orderStr.setMachShopName(machShopName); // 生产门店名称 
		
		//订单改价金额
		BigDecimal adjustFee=new BigDecimal(0);
		if(promot.getAdjust_fee()!=null&&promot.getAdjust_fee().trim().length()>0){
			adjustFee=new BigDecimal(promot.getAdjust_fee().trim());
		}
		//商品优惠总金额
		BigDecimal itemDiscountFee=new BigDecimal(0);
		if(promot.getItem_discount_fee()!=null&&promot.getItem_discount_fee().trim().length()>0){
			itemDiscountFee=new BigDecimal(promot.getItem_discount_fee().trim());
		}
		BigDecimal orderDiscountFee=new BigDecimal(0);
		if(promot.getOrder_discount_fee()!=null&&promot.getOrder_discount_fee().trim().length()>0){
			orderDiscountFee=new BigDecimal(promot.getOrder_discount_fee().trim());
		}
				

		BigDecimal totDisc=adjustFee.add(itemDiscountFee).add(orderDiscountFee);
		
		orderStr.setPay(payArray);
		
		orderStr.setIsBook("Y");//有赞全部给Y  常自飞提的需求
		orderStr.setTot_oldAmt(incomeAmt.add(totDisc).doubleValue()); //订单原价
		orderStr.setTot_Amt(incomeAmt.doubleValue()); //订单总价
		orderStr.setTotDisc(totDisc.doubleValue()); //订单优惠总额
		orderStr.setServiceCharge(0); //服务费
		orderStr.setSellerDisc(0);//商户优惠总额
		orderStr.setPlatformDisc(0);//平台优惠总额
		orderStr.setPayAmt(incomeAmt.doubleValue()); //PAYAMT	已付金额
		orderStr.setIncomeAmt(incomeAmt.doubleValue());//incomeAmt	实付
		String payStatus = "3";//默认都是已支付				
		orderStr.setPayStatus(payStatus);//支付状态 1.未支付 2.部分支付 3.付清
		
		
		
		return orderStr;
	}
	
	/**
	 * 折扣信息
	 * @throws Exception
	 */
	public DataProcessBean getTvOrderAgio(String companyNo,String shopNo,String tranTime,String orderNo,
			int size,String promName,BigDecimal agioAmt) throws Exception{
		SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdf4 = new SimpleDateFormat("HHmmss");
		ColumnDataValue columns = new ColumnDataValue();
		columns.Add("COMPANYNO", companyNo, Types.VARCHAR); //企业编号
		columns.Add("ORGANIZATIONNO", shopNo, Types.VARCHAR); //组织编号	Y	ORGANIZATIONNO
		columns.Add("SHOP", shopNo, Types.VARCHAR); //门店编号	Y	SHOP
		columns.Add("ORDERNO", orderNo, Types.VARCHAR); //订单编号	Y	ORDERNO
		columns.Add("ITEM", size+1, Types.INTEGER); //项次	Y	ITEM
		columns.Add("LOAD_DOCTYPE", orderLoadDocType.YOUZAN, Types.VARCHAR); //来源类型	Y	LOAD_DOCTYPE
		columns.Add("CNFFLG", "Y", Types.VARCHAR); //有效否		CNFFLG	NVARCHAR2(1)	Y	
		columns.Add("TRAN_TIME", tranTime, Types.VARCHAR); //时间标记		TRAN_TIME	NVARCHAR2(20)
		Date dateNow=new Date();
		columns.Add("SDATE", sdf3.format(dateNow), Types.VARCHAR);//系统日期
		columns.Add("STIME", sdf4.format(dateNow), Types.VARCHAR);//系统时间
		//活动名称		PROMNAME	NVARCHAR2(255)		例：满30减10元
		columns.Add("PROMNAME", promName, Types.VARCHAR);
		//折扣金额		AGIOAMT	NUMBER(23,8)
		columns.Add("AGIOAMT", agioAmt, Types.DECIMAL);
		
		String[] columns1 = columns.Columns.toArray(new String[0]);
		DataValue[] insValue1 = columns.DataValues.toArray(new DataValue[0]);
		InsBean ib1 = new InsBean("TV_ORDER_AGIO", columns1);
		ib1.addValues(insValue1);
		return new DataProcessBean(ib1);
	}
	
	public DataProcessBean getTvOrderPay(String companyNo,String shopNo,String tranTime,String orderNo,
			int size,BigDecimal pay,String payCode,String payName,String payCodeErp) throws Exception{
		ColumnDataValue columns = new ColumnDataValue();
		columns.Add("COMPANYNO", companyNo, Types.VARCHAR); //企业编号
		columns.Add("ORGANIZATIONNO", shopNo, Types.VARCHAR); //组织编号	Y	ORGANIZATIONNO
		columns.Add("SHOP", shopNo, Types.VARCHAR); //门店编号	Y	SHOP
		columns.Add("ORDERNO", orderNo, Types.VARCHAR); //订单编号	Y	ORDERNO
		columns.Add("ITEM", size+1, Types.INTEGER); //项次	Y	ITEM
		columns.Add("PAY", pay, Types.DECIMAL); //金额		PAY
		columns.Add("EXTRA", BigDecimal.ZERO, Types.DECIMAL); //EXTRA	溢收金额
		columns.Add("CHANGED", BigDecimal.ZERO, Types.DECIMAL); //CHANGED	找零
		columns.Add("CNFFLG", "Y", Types.VARCHAR);//有效否		CNFFLG
		columns.Add("LOAD_DOCTYPE", orderLoadDocType.YOUZAN, Types.VARCHAR);//
		columns.Add("ISONLINEPAY", "Y", Types.VARCHAR);//是否平台支付		ISONLINEPAY
		columns.Add("TRAN_TIME", tranTime, Types.VARCHAR);//时间标记		TRAN_TIME

		columns.Add("PAYNAME", payName, Types.VARCHAR); //付款名称		PAYNAME
		columns.Add("PAYCODE", payCode, Types.VARCHAR); //支付方式(小类)		PAYCODE  
		columns.Add("PAYCODEERP", payCodeErp, Types.VARCHAR); //支付类型(大类)		PAYCODEERP
		
		String[] columns1 = columns.Columns.toArray(new String[0]);
		DataValue[] insValue1 = columns.DataValues.toArray(new DataValue[0]);
		InsBean ib1 = new InsBean("TV_ORDER_PAY", columns1);
		ib1.addValues(insValue1);
		
		return new DataProcessBean(ib1);
	}
	
	/**
	 * 订单历程表(日志)
	 * @return
	 * @throws Exception
	 */
	public DataProcessBean getTvOrderStatusLog(String companyNo,String shopNo,String tranTime,String orderNo,
			String type,String typeName,String status,String statusName,String display) throws Exception{
		SimpleDateFormat sdf5 = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		Date dateNow=new Date();
		ColumnDataValue columns = new ColumnDataValue();
		columns.Add("COMPANYNO", companyNo, Types.VARCHAR); //企业编号
		columns.Add("CUSTOMERNO", " ", Types.VARCHAR); //客户编号
		columns.Add("ORGANIZATIONNO", shopNo, Types.VARCHAR); //组织编号	Y	ORGANIZATIONNO
		columns.Add("SHOP", shopNo, Types.VARCHAR); //门店编号	Y	SHOP
		columns.Add("ORDERNO", orderNo, Types.VARCHAR); //订单编号	Y	ORDERNO
		columns.Add("LOAD_DOCTYPE", orderLoadDocType.YOUZAN, Types.VARCHAR);//
		
		
		columns.Add("OPNO", "admin", Types.VARCHAR); //订单编号	Y	ORDERNO
		columns.Add("OPNAME", "管理员", Types.VARCHAR);//
		columns.Add("NEED_NOTIFY", "N", Types.VARCHAR); //NEED_NOTIFY 是否通知云pos,N-不需要调用，Y-需要
		columns.Add("NOTIFY_STATUS", "0", Types.VARCHAR);//NOTIFY_STATUS 通知云pos状态返回，0-未通知，1-已通知
		columns.Add("NEED_CALLBACK", "N", Types.VARCHAR);//NEED_CALLBACK 是否调用第三方接口，N-不需要调用，Y-需要
		columns.Add("CALLBACK_STATUS", "0", Types.VARCHAR);//CALLBACK_STATUS 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
		columns.Add("CNFFLG", "Y", Types.VARCHAR);//CNFFLG
		columns.Add("TRAN_TIME", tranTime, Types.VARCHAR);//TRAN_TIME
		columns.Add("UPDATE_TIME", sdf5.format(dateNow), Types.VARCHAR);//UPDATE_TIME  yyyyMMddHHmmssSSS
		
		columns.Add("STATUSTYPE", type, Types.VARCHAR);//STATUSTYPE 状态类型 // 1-订单状态，2-配送状态，3-退单状态，4-其他
		columns.Add("STATUSTYPENAME", typeName, Types.VARCHAR);//STATUSTYPENAME 状态类型名称
		columns.Add("STATUS", status, Types.VARCHAR);//STATUSTYPENAME 状态类型名称
		//STATUS  状态 0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 
		//8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
		columns.Add("STATUSNAME", statusName, Types.VARCHAR);//STATUSNAME 状态名称
		String memo = statusName;
		columns.Add("MEMO", memo, Types.VARCHAR);//MEMO 類型名稱+"-->"+狀態名稱
		columns.Add("DISPLAY", display, Types.VARCHAR);//1:对外给买家看的 否则写0
		
		String[] columns1 = columns.Columns.toArray(new String[0]);
		DataValue[] insValue1 = columns.DataValues.toArray(new DataValue[0]);
		InsBean ib1 = new InsBean("TV_ORDER_STATUSLOG", columns1);
		ib1.addValues(insValue1);
		return new DataProcessBean(ib1);
	}
	
	/**
	 * UrlEncode解码
	 * @param codeString
	 * @return
	 * @throws Exception
	 */
	public String decode(String codeString) throws Exception{
		String str="";
		if(codeString!=null){
			str=java.net.URLDecoder.decode(codeString,"UTF-8");
		}
		return str;
	}
	
	/** 
	 * RSA私钥解密
	 *  
	 * @param str 
	 *            加密字符串
	 * @param privateKey 
	 *            私钥 
	 * @return 铭文
	 * @throws Exception 
	 *             解密过程中的异常信息 
	 */  
	public static String decrypt(String str, String privateKey) throws Exception{
		//64位解码加密后的字符串
		byte[] inputByte = Base64.decodeBase64(str.getBytes("UTF-8"));
		//base64编码的私钥
		byte[] decoded = Base64.decodeBase64(privateKey);  
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));  
		//RSA解密
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, priKey);
		String outStr = new String(cipher.doFinal(inputByte));
		return outStr;
	}

	// 产生UUID  用于ORDER_ID赋值
	public static String getUUID() {
		UUID uuid = UUID.randomUUID();
		String str = uuid.toString();
		String uuidStr = str.replace("-", "");
		return uuidStr;
	}
	
	public BigDecimal getDefaultDecimal(Object str) throws Exception{
		BigDecimal dec=new BigDecimal(0);
		if(str!=null&&str.toString().trim().length()>0){
			dec=new BigDecimal(str.toString().trim());
		}
		return dec;
	}



}
