package com.dsc.spos.thirdpart.youzan;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.json.JsonBasicRes;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.thirdpart.youzan.response.YouZanBasicRes;
import com.dsc.spos.thirdpart.youzan.response.YouZanMultistoreListRes;
import com.dsc.spos.thirdpart.youzan.response.YouZanTradeGetRes;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.dsc.spos.waimai.entity.orderStatusLog;

public class YouZanCallBackServiceV3
{
	static String LogFileNameAll = "YouZanNotify";//记录原始推送消息
	static String LogFileName = "YouZanPost";//解析记录
	
	public static YouZanCallBackServiceV3 getInstance() throws Exception {
		return new YouZanCallBackServiceV3();
	}
	
	/**
	 * 交易订单详情4.0接口
	 * https://doc.youzanyun.com/doc#/content/API/1-305/detail/api/0/120
	 * https://open.youzanyun.com/api/youzan.trade.get/4.0.0
	 */
	public YouZanTradeGetRes getTrade(String eId,String orderNo,String shopNo,Map<String, Object> otherMap,Map<String, Object> basicMap) throws Exception{
		//订单详情
		String serviceId="TRADE_GET";
		Map<String, Object> params1 = new HashMap<String, Object>();
		params1.put("tid", orderNo);//tid 订单号
		YouZanUtilsV3 utils=new YouZanUtilsV3();
		if(basicMap==null||basicMap.isEmpty()){
			basicMap= getBasicParams(eId);
		}
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
	 * 商家主动退款
	 */
	public JsonBasicRes refundSeller(String eId,String orderNo,String shopNo,Map<String, Object> otherMap,Map<String, Object> basicMap)throws Exception{
		JsonBasicRes thisRes=new JsonBasicRes();
		thisRes.setSuccess(false);
		YouZanUtilsV3 utils=new YouZanUtilsV3();
		String desc=otherMap.get("DESC")==null?"退单":otherMap.get("DESC").toString();
		String refundFee=otherMap.get("PAYAMT")==null?"0":otherMap.get("PAYAMT").toString();
//		orderNo="E20220210172820064706157";
		utils.Log("\r\n"+orderNo+"***"+refundFee);
		//商家主动退款
		String serviceId="REFUND_SELLER_ACTIVE";
		Map<String, Object> params1 = new HashMap<String, Object>();
		params1.put("tid", orderNo);//tid 订单号
		params1.put("desc", desc);//
		params1.put("refund_fee", refundFee);//
		Map<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("EID", eId);
		if(basicMap==null||basicMap.isEmpty()){
			basicMap= utils.getYouZanList(queryMap).get(0);
		}
		String resStr1=utils.PostData(serviceId,basicMap, params1);
		
		YouZanBasicRes res1=new YouZanBasicRes();
		res1=com.alibaba.fastjson.JSON.parseObject(resStr1, YouZanBasicRes.class);
		if(res1!=null){
			if("TRUE".equalsIgnoreCase(res1.getSuccess())){
				thisRes.setSuccess(true);
				thisRes.setServiceDescription("操作成功");
				return thisRes;
			}else{
				if(res1.getErrorMsg()!=null){
					thisRes.setServiceDescription(res1.getErrorMsg());
					String message="不能同时发起两笔相同的退款";
					if(res1.getErrorMsg().indexOf(message)>0){
						thisRes.setSuccess(true);
						thisRes.setServiceDescription(res1.getErrorMsg());
						return thisRes;
					}else{
						thisRes.setServiceDescription(res1.getErrorMsg());
						return thisRes;
					}
				}else{
					thisRes.setServiceDescription("操作失败");
					return thisRes;
				}
			}
		}else{
			thisRes.setServiceDescription("操作失败");
		}
		return thisRes;
	}
	
	/**
	 * 订转销调用
	 */
	public JsonBasicRes OrderToSale(String eId,String orderNo,String shopNo,Map<String, Object> otherMap,Map<String, Object> basicMap)throws Exception{
//		orderNo="E20220210172825064706201";
		JsonBasicRes thisRes=new JsonBasicRes();
		String expressType="";
		if(basicMap==null||basicMap.isEmpty()){
			YouZanUtilsV3 utils=new YouZanUtilsV3();
			Map<String, Object> queryMap = new HashMap<String, Object>();
			queryMap.put("EID", eId);
			basicMap= utils.getYouZanList(queryMap).get(0);
		}
		YouZanTradeGetRes res=getTrade(eId, orderNo, shopNo, otherMap, basicMap);
		if(res!=null){
			try{
				//有赞 配送方式（物流类型）， 0:快递发货; 1:到店自提; 2:同城配送; 9:无需发货（虚拟商品订单）
				expressType=res.getFull_order_info().getOrder_info().getExpress_type();
			}catch(Exception e){
				
			}
		}
		if("1".equals(expressType)){
			//1-到店自提
			thisRes=selfFetchCodeApply(eId, orderNo, shopNo, otherMap, basicMap);
		}
		else if("0".equals(expressType)){
			//0-快递发货
			thisRes=localConfirmThird(eId, orderNo, shopNo, otherMap, basicMap);
		}
		else if("2".equals(expressType)){
			//2-同城配送
			thisRes=localDelivery(eId, orderNo, shopNo, otherMap, basicMap);
		}
		return thisRes;
	}


	/**
	 * 目前只处理同城配送的(补传)。
	 * @param eId
	 * @param orderNo
	 * @param shopNo
	 * @param otherMap
	 * @param basicMap
	 * @return
	 * @throws Exception
	 */
	public JsonBasicRes OrderToSaleRetry(String eId,String orderNo,String shopNo,Map<String, Object> otherMap,Map<String, Object> basicMap)throws Exception{
//		orderNo="E20220210172825064706201";
		JsonBasicRes thisRes=new JsonBasicRes();
		String expressType="";
		if(basicMap==null||basicMap.isEmpty()){
			YouZanUtilsV3 utils=new YouZanUtilsV3();
			Map<String, Object> queryMap = new HashMap<String, Object>();
			queryMap.put("EID", eId);
			basicMap= utils.getYouZanList(queryMap).get(0);
		}
		YouZanTradeGetRes res=getTrade(eId, orderNo, shopNo, otherMap, basicMap);
		if(res!=null){
			try{
				//有赞 配送方式（物流类型）， 0:快递发货; 1:到店自提; 2:同城配送; 9:无需发货（虚拟商品订单）
				expressType=res.getFull_order_info().getOrder_info().getExpress_type();
			}catch(Exception e){

			}
		}
		if("1".equals(expressType)){
			//1-到店自提
			//thisRes=selfFetchCodeApply(eId, orderNo, shopNo, otherMap, basicMap);
			thisRes.setSuccess(false);
			thisRes.setServiceDescription("查询有赞订单返回的配送方式(express_type="+expressType+")非同城配送，无需调用!");
		}
		else if("0".equals(expressType)){
			//0-快递发货
			//thisRes=localConfirmThird(eId, orderNo, shopNo, otherMap, basicMap);
			thisRes.setSuccess(false);
			thisRes.setServiceDescription("查询有赞订单返回的配送方式(express_type="+expressType+")非同城配送，无需调用!");
		}
		else if("2".equals(expressType)){
			//2-同城配送
			thisRes=localDelivery(eId, orderNo, shopNo, otherMap, basicMap);
		}
		return thisRes;
	}

	
	/**
	 * 同城配送三方配送发货（三方自配送）
	 */
	public JsonBasicRes localDelivery(String eId,String orderNo,String shopNo,Map<String, Object> otherMap,Map<String, Object> basicMap)throws Exception{
		JsonBasicRes thisRes=new JsonBasicRes();
		thisRes.setSuccess(false);
		//详见：有赞同城配送三方配送发货接口文档
		//同城配送三方配送发货（三方自配送）
		String serviceId="THIRD_LOGISTIC_SEND";
		Map<String, Object> params1 = new HashMap<String, Object>();
		params1.put("order_no", orderNo);//
//		params1.put("tid", orderNo);//tid 订单号
//		params1.put("update_time", new Date().getTime()/1000);//更新时间戳（毫秒）
//		params1.put("order_status", 4);//订单状态；待接单＝1；待取货＝2；配送中＝3；已完成＝4；已取消＝5；
		YouZanUtilsV3 utils=new YouZanUtilsV3();
		Map<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("EID", eId);
		if(basicMap==null||basicMap.isEmpty()){
			basicMap= utils.getYouZanList(queryMap).get(0);
		}
		String resStr1=utils.PostData(serviceId,basicMap, params1);
		
		YouZanBasicRes res1=new YouZanBasicRes();
		res1=com.alibaba.fastjson.JSON.parseObject(resStr1, YouZanBasicRes.class);
		if(res1!=null){
			if("TRUE".equalsIgnoreCase(res1.getSuccess())){
				thisRes.setSuccess(true);
				thisRes.setServiceDescription("操作成功");
				return thisRes;
			}else{
				if(res1.getErrorMsg()!=null){
					thisRes.setServiceDescription(res1.getErrorMsg());
					String message="重复";
					if(res1.getErrorMsg().indexOf(message)>0){
						thisRes.setSuccess(true);
						thisRes.setServiceDescription(res1.getErrorMsg());
						return thisRes;
					}else{
						thisRes.setServiceDescription(res1.getErrorMsg());
						return thisRes;
					}
				}else{
					thisRes.setServiceDescription("操作失败");
					return thisRes;
				}
			}
		}else{
			thisRes.setServiceDescription("操作失败");
		}
		return thisRes;
	}
	
	
	/**
	 * 物流订单发货
	 */
	public JsonBasicRes localConfirmThird(String eId,String orderNo,String shopNo,Map<String, Object> otherMap,Map<String, Object> basicMap)throws Exception{
		JsonBasicRes thisRes=new JsonBasicRes();
		thisRes.setSuccess(false);
		//物流订单发货
		String serviceId="ORDER_ITEM_SEND";
		Map<String, Object> params1 = new HashMap<String, Object>();
		params1.put("tid", orderNo);//tid 订单号
		params1.put("is_no_express", "1");//是否需要物流发货，0或者空：物流发货，1：无需物流。默认为空
		//params1.put("admin_id", otherMap.get("extra_info")==null?"":otherMap.get("extra_info").toString());//核销人（开发者根据自己业务规则传，一般为网点号或手机号）
		long admin_id = 0;
		try {

			String extra_info = otherMap.get("extra_info")==null?"":otherMap.get("extra_info").toString();
			if (!extra_info.isEmpty())
			{
				admin_id = Long.parseLong(extra_info);
			}

		}
		catch (Exception e)
		{

		}
		params1.put("admin_id", admin_id+"");
		YouZanUtilsV3 utils=new YouZanUtilsV3();
		Map<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("EID", eId);
		if(basicMap==null||basicMap.isEmpty()){
			basicMap= utils.getYouZanList(queryMap).get(0);
		}
		String resStr1=utils.PostData(serviceId,basicMap, params1);
		
		YouZanBasicRes res1=new YouZanBasicRes();
		res1=com.alibaba.fastjson.JSON.parseObject(resStr1, YouZanBasicRes.class);
		if(res1!=null){
			if("TRUE".equalsIgnoreCase(res1.getSuccess())){
				thisRes.setSuccess(true);
				thisRes.setServiceDescription("操作成功");
				return thisRes;
			}else{
				if(res1.getErrorMsg()!=null){
					thisRes.setServiceDescription(res1.getErrorMsg());
					String message="重复";
					if(res1.getErrorMsg().indexOf(message)>0){
						thisRes.setSuccess(true);
						thisRes.setServiceDescription(res1.getErrorMsg());
						return thisRes;
					}else{
						thisRes.setServiceDescription(res1.getErrorMsg());
						return thisRes;
					}
				}else{
					thisRes.setServiceDescription("操作失败");
					return thisRes;
				}
			}
		}else{
			thisRes.setServiceDescription("操作失败");
		}
		return thisRes;
	}
	
	
	/**
	 * 自提单订单核销
	 */
	public JsonBasicRes selfFetchCodeApply(String eId,String orderNo,String shopNo,Map<String, Object> otherMap,Map<String, Object> basicMap)throws Exception{
		JsonBasicRes thisRes=new JsonBasicRes();
		thisRes.setSuccess(false);
		//自提单订单核销
		String serviceId="SELF_FETCH_CODE";
		Map<String, Object> params1 = new HashMap<String, Object>();
		params1.put("tid", orderNo);//tid 订单号
		params1.put("extra_info", otherMap.get("extra_info")==null?"":otherMap.get("extra_info").toString());//核销人（开发者根据自己业务规则传，一般为网点号或手机号）
		YouZanUtilsV3 utils=new YouZanUtilsV3();
		Map<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("EID", eId);
		if(basicMap==null||basicMap.isEmpty()){
			basicMap= utils.getYouZanList(queryMap).get(0);
		}
		String resStr1=utils.PostData(serviceId,basicMap, params1);
		
		YouZanBasicRes res1=new YouZanBasicRes();
		res1=com.alibaba.fastjson.JSON.parseObject(resStr1, YouZanBasicRes.class);
		if(res1!=null){
			if("TRUE".equalsIgnoreCase(res1.getSuccess())){
				thisRes.setSuccess(true);
				thisRes.setServiceDescription("核销成功");
				return thisRes;
			}else{
				if(res1.getErrorMsg()!=null){
					thisRes.setServiceDescription(res1.getErrorMsg());
					String message="不能重复核销订单";
					if(res1.getErrorMsg().indexOf(message)>0){
						thisRes.setSuccess(true);
						thisRes.setServiceDescription(res1.getErrorMsg());
						return thisRes;
					}else{
						thisRes.setServiceDescription(res1.getErrorMsg());
						return thisRes;
					}
				}else{
					thisRes.setServiceDescription("有赞核销失败");
					return thisRes;
				}
			}
		}else{
			thisRes.setServiceDescription("有赞自提核销失败");
		}
		return thisRes;
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
		YouZanUtilsV3 utils=new YouZanUtilsV3();
		String resStr1=utils.PostData(serviceId,map1, params1);
				
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
	 * 目前用于 ExpressOrderCreate调用
	 * @param eId
	 * @param orderNo
	 * @param orderMap
	 * @param otherMap
	 * @throws Exception
	 */
	public void sendLogistics(String eId,String orderNo,Map<String, Object> basicMap,Map<String, Object> orderMap,Map<String, Object> otherMap)throws Exception{
		try{
			//获取有赞对接配置参数
			if(basicMap==null||basicMap.isEmpty()){
				basicMap= getBasicParams(eId);
			}
			if(orderMap!=null&&!orderMap.isEmpty()){
				if(orderNo==null||orderNo.length()<1){
					orderNo=orderMap.get("ORDERNO")==null?"":orderMap.get("ORDERNO").toString();
				}
				String loaddoctype=orderMap.get("LOADDOCTYPE")==null?"":orderMap.get("LOADDOCTYPE").toString();
				if(!orderLoadDocType.YOUZAN.equals(loaddoctype)){
					YouZanUtilsV3.getInstance().Log("YouZanCallBackServiceV3-sendLogistics:单据["+orderNo+"]非有赞单据,无需更新发货状态至有赞!");
				}
			}
			if(orderNo==null||orderNo.length()<1){
				YouZanUtilsV3.getInstance().ErrorLog("YouZanCallBackServiceV3-sendLogistics异常"
						+"\r\neId:"+eId
						+ "\r\norderNo:"+orderNo
						+ "\r\nbasicMap:"+com.alibaba.fastjson.JSON.toJSONString(basicMap)
						+ "\r\norderMap:"+com.alibaba.fastjson.JSON.toJSONString(orderMap)
						+ "\r\notherMap:\r\n"+com.alibaba.fastjson.JSON.toJSONString(otherMap));
				return ;
			}
			String expressType="";
			YouZanTradeGetRes res=getTrade(eId, orderNo, null, otherMap, basicMap);
			if(res!=null){
				try{
					//有赞 配送方式（物流类型）， 0:快递发货; 1:到店自提; 2:同城配送; 9:无需发货（虚拟商品订单）
					expressType=res.getFull_order_info().getOrder_info().getExpress_type();
				}catch(Exception e){
					
				}
			}
			
			if("1".equals(expressType)){
				//1-到店自提
				selfFetchCodeApply(eId, orderNo, null, otherMap, basicMap);
			}
			else if("0".equals(expressType)){
				//0-快递发货
				localConfirmThird(eId, orderNo, null, otherMap, basicMap);
			}
			else if("2".equals(expressType)){
				//2-同城配送
				localDelivery(eId, orderNo, null, otherMap, basicMap);
			}
		}catch(Exception e){
			YouZanUtilsV3.getInstance().ErrorLog("YouZanCallBackServiceV3-sendLogistics异常"+YouZanUtilsV3.getInstance().getTrace(e));
		}
		
		
	}
	
	
	/**
	 * @param eId
	 * @param orderNo
	 * @param orderMap
	 * @param otherMap
	 * @throws Exception
	 */
	public void updateDeliveryInfo(String eId,String orderNo,Map<String, Object> basicMap,Map<String, Object> orderMap,Map<String, Object> otherMap)throws Exception{
		try{
			//获取有赞对接配置参数
			if(basicMap==null||basicMap.isEmpty()){
				basicMap= getBasicParams(eId);
			}
			if(orderMap!=null&&!orderMap.isEmpty()){
				if(orderNo==null||orderNo.length()<1){
					orderNo=orderMap.get("ORDERNO")==null?"":orderMap.get("ORDERNO").toString();
				}
				String loaddoctype=orderMap.get("LOADDOCTYPE")==null?"":orderMap.get("LOADDOCTYPE").toString();
				if(!orderLoadDocType.YOUZAN.equals(loaddoctype)){
					YouZanUtilsV3.getInstance().Log("YouZanCallBackServiceV3-updateDeliveryInfo:单据["+orderNo+"]非有赞单据,无需更新物流信息至有赞!");
				}
			}
			if(orderNo==null||orderNo.length()<1){
				YouZanUtilsV3.getInstance().ErrorLog("YouZanCallBackServiceV3-updateDeliveryInfo异常"
						+"\r\neId:"+eId
						+ "\r\norderNo:"+orderNo
						+ "\r\nbasicMap:"+com.alibaba.fastjson.JSON.toJSONString(basicMap)
						+ "\r\norderMap:"+com.alibaba.fastjson.JSON.toJSONString(orderMap)
						+ "\r\notherMap:\r\n"+com.alibaba.fastjson.JSON.toJSONString(otherMap));
				return ;
			}
			String expressType="";
			YouZanTradeGetRes res=getTrade(eId, orderNo, null, otherMap, basicMap);
			if(res!=null){
				try{
					//有赞 配送方式（物流类型）， 0:快递发货; 1:到店自提; 2:同城配送; 9:无需发货（虚拟商品订单）
					expressType=res.getFull_order_info().getOrder_info().getExpress_type();
				}catch(Exception e){
					
				}
			}
			
			if("1".equals(expressType)){
				//1-到店自提
				YouZanUtilsV3.getInstance().Log("YouZanCallBackServiceV3-updateDeliveryInfo:订单["+orderNo+"]为自提单,无需更新物流信息!");
			}
			else if("0".equals(expressType)){
				//0-快递发货
				YouZanUtilsV3.getInstance().Log("YouZanCallBackServiceV3-updateDeliveryInfo:订单["+orderNo+"]为快递单,无需更新物流信息!");
			}
			else if("2".equals(expressType)){
				//2-同城配送
				localDeliveryUpdateDeliveryInfo(eId, orderNo, null, otherMap, basicMap);
			}
		}catch(Exception e){
			YouZanUtilsV3.getInstance().ErrorLog("YouZanCallBackServiceV3-updateDeliveryInfo异常"+YouZanUtilsV3.getInstance().getTrace(e));
		}
	}
	
	/**
	 * 用于更新物流中间节点
	 * 同城配送更新订单配送信息
	 */
	public JsonBasicRes localDeliveryUpdateDeliveryInfo(String eId,String orderNo,String shopNo,Map<String, Object> otherMap,Map<String, Object> basicMap)throws Exception{
		JsonBasicRes thisRes=new JsonBasicRes();
		thisRes.setSuccess(false);
		//同城配送更新订单配送信息
		//适用场景：同城配送线下呼叫三方骑手
		String serviceId="UPDATE_DELIVERY_STATUS";
		Map<String, Object> params1 = new HashMap<String, Object>();
		params1.put("tid", orderNo);//三方订单号
		
		
		YouZanUtilsV3.getInstance().Log("YouZanCallBackServiceV3-localDeliveryUpdateDeliveryInfo入参"
				+"\r\neId:"+eId
				+ "\r\norderNo:"+orderNo
				+ "\r\nbasicMap:"+com.alibaba.fastjson.JSON.toJSONString(basicMap)
				+ "\r\notherMap:\r\n"+com.alibaba.fastjson.JSON.toJSONString(otherMap));
		
		////-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 8已分配骑手
		//物流状态
		String newDeliverystatus=otherMap.get("DELIVERYSTATUS")==null?"":otherMap.get("DELIVERYSTATUS").toString();
		//状态描述
		String distributionStatusName=otherMap.get("DELIVERYSTATUSNAME")==null?"":otherMap.get("DELIVERYSTATUSNAME").toString();
		//配送员
		String delname=otherMap.get("DELNAME")==null?"":otherMap.get("DELNAME").toString();
		////配送员手机号
		String deltelephone=otherMap.get("DELTELEPHONE")==null?"":otherMap.get("DELTELEPHONE").toString();
        String orderStatus="";
        if("0".equals(newDeliverystatus)){
        	orderStatus="1";//待接单＝1
        }else if("1".equals(newDeliverystatus)){
        	orderStatus="2";//待取货＝2
        }else if("2".equals(newDeliverystatus)){
        	orderStatus="3";//配送中＝3
        }else if("3".equals(newDeliverystatus)){
        	orderStatus="4";//已完成＝4
        }else if("4".equals(newDeliverystatus)){
        	orderStatus="5";//已取消＝5
        }else if("5".equals(newDeliverystatus)){
        	orderStatus="5";//已取消＝5
        }else if("6".equals(newDeliverystatus)){
        	orderStatus="2";//待取货＝2
        }else if("8".equals(newDeliverystatus)){
        	orderStatus="1";//待接单＝1
        }else{
        	YouZanUtilsV3.getInstance().Log("YouZanCallBackServiceV3-localDeliveryUpdateDeliveryInfo:单号["+orderNo+"]物流状态["+newDeliverystatus+"]映射失败!");
        }
        
		params1.put("order_status", orderStatus);//订单状态；待接单＝1；待取货＝2；配送中＝3；已完成＝4；已取消＝5；
		if(delname!=null&&delname.length()>0){
			params1.put("transporter_name", delname);//配送员姓名
		}
		if(deltelephone!=null&&deltelephone.length()>0){
			params1.put("transporter_phone", deltelephone);//配送员手机号
		}
		YouZanUtilsV3 utils=new YouZanUtilsV3();
		if(basicMap==null||basicMap.isEmpty()){
			basicMap= getBasicParams(eId);
		}
		String resStr1=utils.PostData(serviceId,basicMap, params1);
		
		YouZanBasicRes res1=new YouZanBasicRes();
		res1=com.alibaba.fastjson.JSON.parseObject(resStr1, YouZanBasicRes.class);
		if(res1!=null){
			if("TRUE".equalsIgnoreCase(res1.getSuccess())){

				try{
					String status=otherMap.get("STATUS")==null?"":otherMap.get("STATUS").toString();
					String shippingId=otherMap.get("SHIPPINGSHOP")==null?"":otherMap.get("SHIPPINGSHOP").toString();
					String shippingName=otherMap.get("SHIPPINGSHOPNAME")==null?"":otherMap.get("SHIPPINGSHOPNAME").toString();
					String machShopId=otherMap.get("MACHSHOP")==null?"":otherMap.get("MACHSHOP").toString();
					String machShopName=otherMap.get("MACHSHOPNAME")==null?"":otherMap.get("MACHSHOPNAME").toString();
					String shopId=otherMap.get("SHOP")==null?"":otherMap.get("SHOP").toString();
					String shopName=otherMap.get("SHOPNAME")==null?"":otherMap.get("SHOPNAME").toString();
					String order_chanelid=otherMap.get("CHANNELID")==null?"":otherMap.get("CHANNELID").toString();
					String order_load_doctype=otherMap.get("LOADDOCTYPE")==null?"":otherMap.get("LOADDOCTYPE").toString();
					
					{
						//执行
						List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
						//写订单日志
						String memoLog="同步至有赞-->物流:" + distributionStatusName;
						String LogStatus="0";
						orderStatusLog oslog=new orderStatusLog();
						oslog.setCallback_status("N");
						oslog.setChannelId(order_chanelid);
						oslog.setDisplay("1");
						oslog.seteId(eId);
						oslog.setLoadDocBillType(order_load_doctype);
						oslog.setLoadDocOrderNo(orderNo);
						oslog.setLoadDocType(orderLoadDocType.YOUZAN);
						oslog.setMachShopName(machShopName);
						oslog.setMachShopNo(machShopId);
						oslog.setMemo(memoLog);
						oslog.setNeed_callback("N");
						oslog.setNeed_notify("N");
						oslog.setNotify_status("N");
						oslog.setOpName("admin");
						oslog.setOpNo("admin");
						oslog.setOrderNo(orderNo);
						oslog.setShippingShopName(shippingName);
						oslog.setShippingShopNo(shippingId);
						oslog.setShopName(shopName);
						oslog.setShopNo(shopId);
						oslog.setStatus(status);//LogStatus不能是0和1，否则里面display重新赋值0啦
						//
						String statusType="2";
						StringBuilder statusTypeName=new StringBuilder();
						String statusName= HelpTools.GetOrderStatusName(statusType, LogStatus, statusTypeName);
						oslog.setStatusName(statusName);
						oslog.setStatusType(statusType);
						oslog.setStatusTypeName(statusTypeName.toString());
						oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
						InsBean ib_DCP_ORDER_STATUSLOG=HelpTools.InsertOrderStatusLog(oslog);
						lstData.add(new DataProcessBean(ib_DCP_ORDER_STATUSLOG));
						
						StaticInfo.dao.useTransactionProcessData(lstData);
					}
				}catch(Exception e){
					YouZanUtilsV3.getInstance().ErrorLog("YouZanCallBackServiceV3-localDeliveryUpdateDeliveryInfo statuslog写入异常"+YouZanUtilsV3.getInstance().getTrace(e));
				}
				
				
				thisRes.setSuccess(true);
				thisRes.setServiceDescription("操作成功");
				return thisRes;
			}else{
				if(res1.getErrorMsg()!=null){
					thisRes.setServiceDescription(res1.getErrorMsg());
					String message="重复";
					if(res1.getErrorMsg().indexOf(message)>0){
						thisRes.setSuccess(true);
						thisRes.setServiceDescription(res1.getErrorMsg());
						return thisRes;
					}else{
						thisRes.setServiceDescription(res1.getErrorMsg());
						return thisRes;
					}
				}else{
					thisRes.setServiceDescription("操作失败");
					return thisRes;
				}
			}
		}else{
			thisRes.setServiceDescription("操作失败");
		}
		return thisRes;
	}
	
	public Map<String, Object> getBasicParams(String eId)throws Exception{
		YouZanUtilsV3 utils=new YouZanUtilsV3();
		//获取有赞对接配置参数
		Map<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("EID", eId);
		return utils.getYouZanList(queryMap).get(0);
	}


	/**
	 * 同意退款
	 * @param eId
	 * @param orderNo
	 * @param shopNo
	 * @param otherMap
	 * @param basicMap
	 * @return
	 * @throws Exception
	 */
	public JsonBasicRes refundAgree(String eId,String orderNo,String shopNo,Map<String, Object> otherMap,Map<String, Object> basicMap)throws Exception{
		JsonBasicRes thisRes=new JsonBasicRes();
		thisRes.setSuccess(false);
		YouZanUtilsV3 utils=new YouZanUtilsV3();

		utils.Log("\r\n"+orderNo+"同意退款");
		//商家主动退款
		String serviceId="REFUND_AGREE";
		Map<String, Object> params1 = new HashMap<String, Object>();
		params1.put("outOrderNo", orderNo);//tid 订单号
		Map<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("EID", eId);
		if(basicMap==null||basicMap.isEmpty()){
			basicMap= utils.getYouZanList(queryMap).get(0);
		}
		String resStr1=utils.PostData(serviceId,basicMap, params1);

		YouZanBasicRes res1=new YouZanBasicRes();
		res1=com.alibaba.fastjson.JSON.parseObject(resStr1, YouZanBasicRes.class);
		if(res1!=null){
			if("TRUE".equalsIgnoreCase(res1.getSuccess())){
				thisRes.setSuccess(true);
				thisRes.setServiceDescription("操作成功");
				return thisRes;
			}else{
				if(res1.getErrorMsg()!=null){
					thisRes.setServiceDescription(res1.getErrorMsg());
					String message="已退款";
					//和有赞沟通，已经退款成功的再次请求返回，code=200
					if(res1.getErrorMsg().indexOf(message)>0||"200".equals(res1.getErrorCode())){
						thisRes.setSuccess(true);
						thisRes.setServiceDescription(res1.getErrorMsg());
						return thisRes;
					}else{
						thisRes.setServiceDescription(res1.getErrorMsg());
						return thisRes;
					}
				}else{
					thisRes.setServiceDescription("操作失败");
					return thisRes;
				}
			}
		}else{
			thisRes.setServiceDescription("操作失败");
		}
		return thisRes;
	}

	/**
	 * 拒绝退款
	 * @param eId
	 * @param orderNo
	 * @param shopNo
	 * @param otherMap
	 * @param basicMap
	 * @return
	 * @throws Exception
	 */
	public JsonBasicRes refundRefuse(String eId,String orderNo,String shopNo,Map<String, Object> otherMap,Map<String, Object> basicMap)throws Exception{
		JsonBasicRes thisRes=new JsonBasicRes();
		thisRes.setSuccess(false);
		YouZanUtilsV3 utils=new YouZanUtilsV3();

		utils.Log("\r\n"+orderNo+"拒绝退款");
		//商家主动退款
		String serviceId="REFUND_REFUSE";
		Map<String, Object> params1 = new HashMap<String, Object>();
		params1.put("outOrderNo", orderNo);//tid 订单号
		params1.put("remark", "商家不同意退款，详情请联系商家");
		Map<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("EID", eId);
		if(basicMap==null||basicMap.isEmpty()){
			basicMap= utils.getYouZanList(queryMap).get(0);
		}
		String resStr1=utils.PostData(serviceId,basicMap, params1);

		YouZanBasicRes res1=new YouZanBasicRes();
		res1=com.alibaba.fastjson.JSON.parseObject(resStr1, YouZanBasicRes.class);
		if(res1!=null){
			if("TRUE".equalsIgnoreCase(res1.getSuccess())){
				thisRes.setSuccess(true);
				thisRes.setServiceDescription("操作成功");
				return thisRes;
			}else{
				if(res1.getErrorMsg()!=null){
					thisRes.setServiceDescription(res1.getErrorMsg());
					String message="已拒绝退款";
					//和有赞沟通，已经退款成功的再次请求返回，code=200
					if(res1.getErrorMsg().indexOf(message)>0||"200".equals(res1.getErrorCode())){
						thisRes.setSuccess(true);
						thisRes.setServiceDescription(res1.getErrorMsg());
						return thisRes;
					}else{
						thisRes.setServiceDescription(res1.getErrorMsg());
						return thisRes;
					}
				}else{
					thisRes.setServiceDescription("操作失败");
					return thisRes;
				}
			}
		}else{
			thisRes.setServiceDescription("操作失败");
		}
		return thisRes;
	}

}
