package com.dsc.spos.waimai;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dsc.spos.config.SPosConfig.ProdInterface;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.waimai.entity.order;
import com.dsc.spos.waimai.entity.orderGoodsItem;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.dsc.spos.waimai.entity.orderRedisKeyInfo;
import com.dsc.spos.waimai.entity.orderStatusLog;

public class MicroMarketService extends SWaimaiBasicService {

	private boolean IsSaveOK = true;
	@Override
	public String execute(String json) throws Exception {
		
		//数据库保存失败，返回给微商城false
		String response_microMarket = "{\"success\":true,\"serviceStatus\":\"000\",\"serviceDescription\":\"服务执行成功\"}";
		
		if (json == null || json.length() == 0) {
			HelpTools.writelog_waimai("微商城发送的请求为空！");
			response_microMarket = "{\"success\":false,\"serviceStatus\":\"100\",\"serviceDescription\":\"微商城发送的请求为空\"}";
			HelpTools.writelog_waimai("【返回给手机商城json】" + response_microMarket);			
			return response_microMarket;		
		}
		HelpTools.writelog_waimai("【微商城请求内容】" + json);

		JSONObject jsonobj = new JSONObject(json);
		
		JSONObject datasobj = jsonobj.getJSONObject("datas");	
		String eId = datasobj.get("eId").toString();// 企业编号
		String orderNo = datasobj.get("orderNo").toString();
		String channelId = "";//下单渠道（公众号appid）
		if(!datasobj.isNull("channelId"))
		{
			channelId = datasobj.optString("channelId");//下单渠道（公众号appid）
		}

		

		//根据channelId查询渠道类型，
		String sql = "select * from  crm_channel where eid='"+eId+"' and CHANNELID='"+channelId+"' ";
		HelpTools.writelog_waimai("【手机商城】【根据channelId查询渠道类型】sql=" +sql+",单号orderNo="+orderNo);
		List<Map<String, Object>> getAppNoList = this.doQueryData(sql, null);
		if(getAppNoList==null||getAppNoList.isEmpty())
		{
			response_microMarket = "{\"success\":false,\"serviceStatus\":\"100\",\"serviceDescription\":\"根据channelId查询渠道类型为空!\"}";
			HelpTools.writelog_waimai("【返回给手机商城json】" + response_microMarket+",单号orderNo="+orderNo);			
			return response_microMarket;
		}

		String appNo = getAppNoList.get(0).get("APPNO").toString();
		if(appNo==null||appNo.isEmpty())
		{
			response_microMarket = "{\"success\":false,\"serviceStatus\":\"100\",\"serviceDescription\":\"根据channelId查询渠道类型APPNO为空!\"}";
			HelpTools.writelog_waimai("【根据channelId查询渠道类型】返回APPNO=" + appNo+"为空,单号orderNo="+orderNo);			
			return response_microMarket;
		}
		
		//通过配置文件读取
		String langtype="zh_CN";
		List<ProdInterface> lstProd=StaticInfo.psc.getT100Interface().getProdInterface();
		if(lstProd!=null&&!lstProd.isEmpty())
		{
			langtype=lstProd.get(0).getHostLang().getValue();
			
		}
		

		
		StringBuffer errorSB = new StringBuffer();
		order res_order = HelpTools.GetMicroMarketResponse(json,appNo, errorSB);		
		if (res_order == null) 
		{
			response_microMarket = "{\"success\":false,\"serviceStatus\":\"100\",\"serviceDescription\":\"解析json失败\"}";
			HelpTools.writelog_waimai("【返回给手机商城json】" + response_microMarket);			
			return response_microMarket;
		}
		//20220721嘉华克制需求，针对饼屋小程序wx649d626839c11887，只要商城订单给过来的物流类型是3 全国快递   dcp订单就转成总部发货
		if ("wx649d626839c11887".equals(channelId)||"wx17343e197df8aebf".equals(channelId))
		{
			if ("2".equals(res_order.getShipType()))
			{
				res_order.setShipType("5");
				HelpTools.writelog_waimai("【嘉华客制】(针对饼屋小程序wx649d626839c11887,wx17343e197df8aebf,全国配送转成总部配送)，订单号orderNo="+orderNo+",【配送方式】shipType="+res_order.getShipType());
			}
		}
		
		//调用查询 plubarcode
		errorSB = new StringBuffer();
		this.getOrderDetailPlubarcode(res_order, langtype, errorSB);
		
		//调用下订单接入的参数设置逻辑
		errorSB = new StringBuffer();
		HelpTools.updateOrderFunction(res_order, errorSB);
		
		HelpTools.updateOrderWithPackage(res_order, "", errorSB);
		
		Map<String, Object> res = new HashMap<String, Object>();
		//this.processDUID(res_json, res);
		orderNo = res_order.getOrderNo();
		String loadDocType = res_order.getLoadDocType();
		String loadDocBillType = res_order.getLoadDocBillType();
		String loadDocOrderNo = res_order.getLoadDocOrderNo();
		String shopNo = res_order.getShopNo();
		String machShopNo = res_order.getMachShopNo();
		String shippingShopNo = res_order.getShippingShopNo();
		HelpTools.writelog_waimai("订单号orderNo="+orderNo+",订单状态status="+res_order.getStatus()+",【配送门店】shippingShopNo="+shippingShopNo+",下订门店shopNo="+shopNo+",生产门店machShopNo="+machShopNo);
		//订单中心接到商城订单时，如果下单门店、配送门店有编码，没有名称，查询下本地数据库赋值相应的门店名称字段。如果查询不到直接赋值相应的门店编码
        this.getShopNameByShopNo(res_order,"");
		
		ParseJson pj = new ParseJson();
		String Response_json = pj.beanToJson(res_order);
		String redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + eId + ":" + shippingShopNo;
		// String hash_key = orderid + "&" + orderStatus;
		String hash_key = orderNo;
		HelpTools.writelog_waimai(
				"【配送门店】【开始写缓存】" + "redis_key:" + redis_key + " hash_key:" + hash_key + " hash_value:" + Response_json);
		try
		{
			RedisPosPub redis = new RedisPosPub();
			boolean nret = redis.setHashMap(redis_key, hash_key, Response_json);
			if (nret) {
				HelpTools.writelog_waimai("【配送门店】【写缓存】OK" + " redis_key:" + redis_key + " hash_key:" + hash_key);
			} else {
				HelpTools.writelog_waimai("【配送门店】【写缓存】Error" + " redis_key:" + redis_key + " hash_key:" + hash_key);
			}
		} 
		catch (Exception e)
		{
			// TODO: handle exception
			HelpTools.writelog_waimai(
					"【配送门店】【写缓存】Exception:" + e.getMessage() + " redis_key:" + redis_key + " hash_key:" + hash_key);
		}
		
		//下订门店与配送门店不一致 
		if(shopNo!=null&&shopNo.isEmpty()==false&&shopNo.equals(shippingShopNo)==false)
		{
		    redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + eId + ":" + shopNo;
			
			HelpTools.writelog_waimai(
					"【下订门店】【开始写缓存】" + "redis_key:" + redis_key + " hash_key:" + hash_key + " hash_value:" + Response_json);
			try
			{
				RedisPosPub redis = new RedisPosPub();
				boolean nret = redis.setHashMap(redis_key, hash_key, Response_json);
				if (nret) {
					HelpTools.writelog_waimai("【下订门店】【写缓存】OK" + " redis_key:" + redis_key + " hash_key:" + hash_key);
				} else {
					HelpTools.writelog_waimai("【下订门店】【写缓存】Error" + " redis_key:" + redis_key + " hash_key:" + hash_key);
				}
			} 
			catch (Exception e)
			{
				// TODO: handle exception
				HelpTools.writelog_waimai(
						"【下订门店】【写缓存】Exception:" + e.getMessage() + " redis_key:" + redis_key + " hash_key:" + hash_key);
			}
			
		}
		
		//生产门店与其他2个门店不一致
		if(machShopNo!=null&&machShopNo.isEmpty()==false&&machShopNo.equals(shippingShopNo)==false&&machShopNo.equals(shopNo)==false)
		{
		    redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + eId + ":" + machShopNo;
			
			HelpTools.writelog_waimai(
					"【生产门店】【开始写缓存】" + "redis_key:" + redis_key + " hash_key:" + hash_key + " hash_value:" + Response_json);
			try
			{
				RedisPosPub redis = new RedisPosPub();
				boolean nret = redis.setHashMap(redis_key, hash_key, Response_json);
				if (nret) {
					HelpTools.writelog_waimai("【生产门店】【写缓存】OK" + " redis_key:" + redis_key + " hash_key:" + hash_key);
				} else {
					HelpTools.writelog_waimai("【生产门店】【写缓存】Error" + " redis_key:" + redis_key + " hash_key:" + hash_key);
				}
			} 
			catch (Exception e)
			{
				// TODO: handle exception
				HelpTools.writelog_waimai(
						"【生产门店】【写缓存】Exception:" + e.getMessage() + " redis_key:" + redis_key + " hash_key:" + hash_key);
			}
			
		}
		 
		 
		
		//存库
		StringBuilder errorMessage = new StringBuilder();
		List<order> orderList = new ArrayList<order>();
		orderList.add(res_order);
		StringBuffer insertMessage = new StringBuffer();
		ArrayList<DataProcessBean> DPB = HelpTools.GetInsertOrderCreat(orderList, insertMessage,null);
		if (DPB == null || DPB.isEmpty()) 
		{
			response_microMarket = "{\"success\":false,\"serviceStatus\":\"100\",\"serviceDescription\":\"服务执行失败\"}";
			return response_microMarket;
		}


		for (DataProcessBean dataProcessBean : DPB) {
			this.addProcessData(dataProcessBean);
		}
		try
		{
			this.doExecuteDataToDB();
			IsSaveOK = true;
			HelpTools.writelog_waimai("【保存数据库成功】" + " 企业编号eId=" + eId + " 门店编号shopNO=" + res_order.getShopNo()
					+ " 订单号orderNO=" + orderNo);
			
			//调用下商城接口，告知商城订单中心已收到
			try 
			{
				JSONObject js=new JSONObject();
				js.put("serviceId", "OrderStatusUpdate");
				js.put("orderNo", orderNo);
				js.put("statusType", "1");//交易状态																					
				js.put("status", "3");
				js.put("description", "自动接单");
				js.put("oprId", "AUTO");
				js.put("orgType", "2");
				js.put("orgId", res_order.getShopNo());
				js.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
				String request = js.toString();

				String microMarkServiceName = "OrderStatusUpdate";

				HelpTools.writelog_waimai("【调用微商城订单已接单接口】开始，订单号=" + orderNo);
				String result_wsc = HttpSend.MicroMarkSend(request, eId, microMarkServiceName,channelId);
				HelpTools.writelog_waimai("【调用微商城订单已接单接口】完成，订单号=" + orderNo);

			} 
			catch (Exception e) 
			{

			}
			
			//开始写日志
			try
			{
				List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
				
				//region 写微商城推送之前的订单追踪日志
				try
				{
					/**
					 * "orderTrace": [
				      {
				        "changeTime": "2019-08-05 17:27:37",
				        "changeStatus": "1",
				        "description": "客户下单"
				      }]
					 */
					JSONArray orderTraceArray = datasobj.getJSONArray("orderTrace");
					if(orderTraceArray!=null)
					{
						for (int i = 0; i < orderTraceArray.length(); i++) 
						{
							try 
							{
								JSONObject item_obj = orderTraceArray.getJSONObject(i);
								String changeTime = item_obj.get("changeTime").toString();																																	
								String changeStatus = item_obj.get("changeStatus").toString();
								String description = item_obj.get("description").toString();
								orderStatusLog onelv1_order = new orderStatusLog();								
								onelv1_order.setLoadDocType(loadDocType);
								onelv1_order.setLoadDocBillType(loadDocBillType);
								onelv1_order.setLoadDocOrderNo(loadDocOrderNo);
								onelv1_order.setChannelId(channelId);
								onelv1_order.seteId(eId);															
								String opNO = "";
								String o_opName = "微商城用户";
								if(langtype.equals("zh_TW"))
								{
									o_opName = "Line商城用户";
								}

								onelv1_order.setOpName(o_opName);
								onelv1_order.setOpNo(opNO);								
								onelv1_order.setShopNo(shopNo);
								
								onelv1_order.setOrderNo(orderNo);
								onelv1_order.setMachShopNo(machShopNo);
								onelv1_order.setShippingShopNo(shippingShopNo);
								String statusType = "";
								String updateStaus = changeStatus;
								statusType = "99";// 其他状态
								String statusName = "其他";

								onelv1_order.setStatusType(statusType);
								onelv1_order.setStatus(updateStaus);
								//StringBuilder statusTypeNameObj = new StringBuilder();
								//String statusName = HelpTools.GetOrderStatusName(statusType, updateStaus, statusTypeNameObj);
								String statusTypeName = "其他状态";
								statusName = description;//重新给下值
								onelv1_order.setStatusTypeName(statusTypeName);
								onelv1_order.setStatusName(statusName);

								String memo = "";
								memo += statusName;
								onelv1_order.setMemo(memo);
								onelv1_order.setDisplay("1");

								String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());

								try 
								{
									SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
									Date	date_change = format.parse(changeTime);
									updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(date_change);
								} catch (Exception e) {

								}
								onelv1_order.setUpdate_time(updateDatetime);

								orderStatusLogList.add(onelv1_order);
							} 
							catch (Exception e) 
							{
								continue;			
							}


						}

					}

				} 
				catch (Exception e) 
				{
					// TODO: handle exception

				}
				//endregion
				
				
				// region订单状态
				orderStatusLog onelv1 = new orderStatusLog();				
				onelv1.setLoadDocType(loadDocType);
				onelv1.setChannelId(channelId);
				onelv1.setLoadDocBillType(loadDocBillType);
				onelv1.setLoadDocOrderNo(loadDocOrderNo);
				onelv1.seteId(eId);
				String opNO = "";
				String o_opName = "微商城用户";
				if(langtype.equals("zh_TW"))
				{
					o_opName = "Line商城用户";
				}
				

				onelv1.setOpName(o_opName);
				onelv1.setOpNo(opNO);				
				onelv1.setShopNo(shopNo);
				onelv1.setOrderNo(orderNo);
				onelv1.setMachShopNo(machShopNo);
				onelv1.setShippingShopNo(shippingShopNo);
				String statusType = "";
				String updateStaus = res_order.getStatus() ;
				statusType = "1";// 订单状态				
				onelv1.setStatusType(statusType);
				onelv1.setStatus(updateStaus);
				StringBuilder statusTypeNameObj = new StringBuilder();
				String statusName = HelpTools.GetOrderStatusName(statusType, updateStaus, statusTypeNameObj);
				String statusTypeName = statusTypeNameObj.toString();
				onelv1.setStatusTypeName(statusTypeName);
				onelv1.setStatusName(statusName);

				String memo = "";
				memo += statusName;
				onelv1.setMemo(memo);
				onelv1.setDisplay("1");

				String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
				onelv1.setUpdate_time(updateDatetime);

				orderStatusLogList.add(onelv1);
				
				StringBuilder errorStatusLogMessage = new StringBuilder();
				boolean nRet = HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogList, errorStatusLogMessage);
				if (nRet) {
					HelpTools.writelog_waimai("【写表tv_orderStatuslog保存成功】" + " 订单号orderNO:" + orderNo);
				} else {
					HelpTools.writelog_waimai(
							"【写表tv_orderStatuslog异常】" + errorMessage.toString() + " 订单号orderNO:" + orderNo);
				}
				this.pData.clear();
						
				
			} 
			catch (Exception e)
			{
				// TODO: handle exception
			}
			
			/*//嘉华上线克制
			if(channelId.equals("wx3270f60cf33de1d0")||channelId.equals("wx8bf8731232d30570") ||channelId.equals("wx0903fac91bfae104"))
			{
				jiahuaPostOldDcp(json);
			}*/
			
			
		} 
		catch (SQLException e) 
		{
			IsSaveOK = false;
			HelpTools.writelog_waimai("【执行语句】异常：" + e.getMessage() + "\r\n 单号orderNo=" + orderNo);
		}
		catch (Exception e)
		{
			// TODO: handle exception
			IsSaveOK = false;
			HelpTools.writelog_waimai("【执行语句】异常：" + e.getMessage() + "\r\n 单号orderNo=" + orderNo);
		}		
	
		if(!IsSaveOK)
		{
			response_microMarket = "{\"success\":false,\"serviceStatus\":\"100\",\"serviceDescription\":\"服务执行失败\"}";		
		}

		HelpTools.writelog_waimai("【返回给手机商城json】" + response_microMarket);


		return response_microMarket;
	}

	@Override
	protected void processDUID(String req, Map<String, Object> res) throws Exception {}

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

	protected List<Map<String, Object>> GetMachShopNO(String companyNO, String shopNO, String orderNO,String langtype) throws Exception {
		String machShopNO = "";
		List<Map<String, Object>> getDetail=null;

		try {
			String sql = "Select A.MACHORGANIZATIONNO,B.ORG_NAME  from ta_org A left join ta_org_lang B on A.companyno=B.companyno and A.MACHORGANIZATIONNO=B.ORGANIZATIONNO and B.lang_type='"+langtype+"'  "
					+ "  where A.COMPANYNO='" + companyNO + "' AND A.ORGANIZATIONNO='" + shopNO + "'";
			getDetail = this.doQueryData(sql, null);
			if (getDetail != null && getDetail.isEmpty() == false && getDetail.size() != 0) {
				machShopNO = getDetail.get(0).get("MACHORGANIZATIONNO").toString();
			} else {
				HelpTools.writelog_waimai("查询生产门店为空，该生产门店没有维护！" + " 企业编号companyNO=" + companyNO + " 门店编号shopNO="
						+ shopNO + " 单号orderNO=" + orderNO);
			}

		} catch (Exception e) {
			HelpTools.writelog_waimai("查询生产门店报错！" + e.getMessage() + " 企业编号companyNO=" + companyNO + " 门店编号shopNO="
					+ shopNO + " 单号orderNO=" + orderNO);
		}

		if (machShopNO == null || machShopNO.length() == 0) {
			HelpTools.writelog_waimai("查询生产门店为空！");
		} else {
			HelpTools.writelog_waimai("查询生产门店=" + machShopNO);
		}

		return getDetail;
	}

	protected String GetCreateShopName(String companyNO, String shopNO, String orderNO,String langtype) throws Exception {

		String ShopName = "";
		Map<String, Object> shopMap = null;
		List<Map<String, Object>> getDetail=null;

		try {
			String sql = "Select A.ORGANIZATIONNO,B.ORG_NAME  from ta_org A left join ta_org_lang B on A.companyno=B.companyno and A.ORGANIZATIONNO=B.ORGANIZATIONNO and B.lang_type='"+langtype+"'  "
					+ "  where A.COMPANYNO='" + companyNO + "' AND A.ORGANIZATIONNO='" + shopNO + "'";
			getDetail = this.doQueryData(sql, null);
			if (getDetail != null && getDetail.isEmpty() == false && getDetail.size() != 0) {
				shopMap = getDetail.get(0);
				ShopName = shopMap.get("ORG_NAME").toString();
			} else {
				HelpTools.writelog_waimai("查询下单门店名称，" + " 企业编号companyNO=" + companyNO + " 门店编号shopNO="
						+ shopNO + " 单号orderNO=" + orderNO);
			}

		} catch (Exception e) {
			HelpTools.writelog_waimai("查询查询下单门店名称报错！" + e.getMessage() + " 企业编号companyNO=" + companyNO + " 门店编号shopNO="
					+ shopNO + " 单号orderNO=" + orderNO);
		}

		if (ShopName == null || ShopName.length() == 0) {
			ShopName = "";
			HelpTools.writelog_waimai("查询下单门店名称为空！");
		} else {
			HelpTools.writelog_waimai("查询下单门店名称=" + ShopName);
		}

		return ShopName;
	}
	
	private  void getOrderDetailPlubarcode(order dcpOrder,String langType,StringBuffer errorMessage) throws Exception
	{
		if(errorMessage==null)
		{
			errorMessage = new StringBuffer();
		}
		if(dcpOrder==null)
		{
			errorMessage.append("order对象为null");
			return;
		}

		String eId = dcpOrder.geteId();
		String orderNo = dcpOrder.getOrderNo();
		String loadDocType = dcpOrder.getLoadDocType();
		String channelId = dcpOrder.getChannelId();		
		String sDate  = new SimpleDateFormat("yyyyMMdd").format(new Date());
		 if(langType==null||langType.isEmpty())
		 {
			 langType = "zh_CN";
		 }



		if(loadDocType.equals(orderLoadDocType.POS))
		{
			errorMessage.append("渠道类型="+loadDocType+"无需处理商品资料映射！ 单号orderNo="+orderNo);
			return;

		}

		if(dcpOrder.getGoodsList()==null||dcpOrder.getGoodsList().isEmpty())
		{			
			HelpTools.writelog_waimai("【获取商品映射资料】【获取条码】渠道类型="+loadDocType+"商品列表为空，无须获取获取商品映射， 单号orderNo="+orderNo);
			return;					
		}

		HelpTools.writelog_waimai("【获取商品映射资料】【获取条码】循环开始， 单号orderNo="+orderNo);//先不管效能问题，后续再优化
		try
		{
			for (orderGoodsItem goodsItem : dcpOrder.getGoodsList())
			{
				String pluNo = goodsItem.getPluNo();
				if (pluNo==null)
				{
					pluNo = "";
				}
				String unit = goodsItem.getsUnit();
				String featureNo = goodsItem.getFeatureNo();								
				String sql = "SELECT A.PLUBARCODE,A.PLUNO,A.UNIT,A.FEATURENO,FL.FEATURENAME,UL.UNAME FROM DCP_GOODS_BARCODE A "
						+ " left join  DCP_GOODS_FEATURE_LANG FL on A.EID =FL.EID AND A.PLUNO=FL.PLUNO AND A.FEATURENO=FL.FEATURENO and FL.Lang_Type='"+langType+"' "
						+ " left join dcp_unit_lang UL on A.EID =UL.EID AND A.UNIT=UL.UNIT and UL.Lang_Type='"+langType+"' "
						+ " where  A.status=100 and  A.EID='"+eId+"' and A.PLUNO ='"+pluNo.replaceAll("'","''")+"' ";
				if(unit!=null&&unit.isEmpty()==false)
				{
					sql += " and A.UNIT='"+unit+"'";
				}
				if(featureNo!=null&&featureNo.isEmpty()==false)
				{
					sql += " and A.FEATURENO='"+featureNo+"'";
				}
				
				HelpTools.writelog_waimai("【获取商品映射资料】【获取条码】循环开始，查询资料sql="+ sql+",单号orderNo="+orderNo);
				if(pluNo==null||pluNo.isEmpty())
				{
					continue;
				}
				List<Map<String, Object>> getPluInfo = StaticInfo.dao.executeQuerySQL(sql, null);

				if(getPluInfo==null||getPluInfo.isEmpty())
				{
					HelpTools.writelog_waimai("【获取商品映射资料】【获取条码】循环，编码pluNo="+ pluNo+",单位unit="+unit+",特征码featureNo="+featureNo+",查无资料,单号orderNo="+orderNo);
					continue;
				}
				if(getPluInfo!=null&&getPluInfo.isEmpty()==false)
				{
					String pluBarcode = getPluInfo.get(0).get("PLUBARCODE").toString();
					String featureNo_db = getPluInfo.get(0).get("FEATURENO").toString();
					String featureName = getPluInfo.get(0).get("FEATURENAME").toString();
					String unit_db = getPluInfo.get(0).get("UNIT").toString();
					String unitName = getPluInfo.get(0).get("UNAME").toString();

					goodsItem.setPluBarcode(pluBarcode);
					goodsItem.setFeatureNo(featureNo_db);
					goodsItem.setFeatureName(featureName);
					goodsItem.setsUnit(unit_db);
					if(unitName!=null&&unitName.isEmpty()==false)
					{
						goodsItem.setsUnitName(unitName);
					}
					HelpTools.writelog_waimai("【获取商品映射资料】【获取条码】循环，编码pluNo="+ pluNo+",对应pluBarcode="+pluBarcode+"，对应featureNo="+featureNo_db+",对应featureName="+featureName+",对应unit="+unit_db+",对应unitName="+unitName+",单号orderNo="+orderNo);
					continue;
				}

			}
		} 
		catch (Exception e)
		{
			// TODO: handle exception
			errorMessage.append(e.getMessage());
			HelpTools.writelog_waimai("【获取商品映射资料】【获取条码】异常："+e.getMessage()+"， 单号orderNo="+orderNo);
		}


	}
	
	private void jiahuaPostOldDcp(String json) throws Exception
	{
		String url = "http://47.106.178.75/dcpService/sposWeb/services/jaxrs/sposService/MicroMarket/Order";
		HelpTools.writelog_waimai("调用DCP2.0手机商城推送地址:" + url);
		HelpTools.writelog_waimai("调用DCP2.0手机商城推送内容:" + json);
		try
		{
			String res =  HttpSend.doPost(url, json, null,"");
			
			HelpTools.writelog_waimai("调用DCP2.0返回结果:"+res);
		} 
		catch (Exception e)
		{
			// TODO: handle exception
			HelpTools.writelog_waimai("调用DCP2.0返回异常:"+e.getMessage());
		}
		
	}

	/**
	 * 订单中心接到商城订单时，如果下单门店、配送门店有编码，没有名称，查询下本地数据库赋值相应的门店名称字段。如果查询不到直接赋值相应的门店编码
	 * @param dcpOrder
	 * @param langType
	 * @throws Exception
	 */
	private  void getShopNameByShopNo(order dcpOrder,String langType) throws Exception
	{
		if(dcpOrder==null)
		{
			return;
		}

		String eId = dcpOrder.geteId();
		String orderNo = dcpOrder.getOrderNo();
		String loadDocType = dcpOrder.getLoadDocType();
		String channelId = dcpOrder.getChannelId();
		String shopNo = dcpOrder.getShopNo();
		String shopName = dcpOrder.getShopName();
		String machShopNo = dcpOrder.getMachShopNo();
		String machShopName = dcpOrder.getMachShopName();
		String shippingShopNo = dcpOrder.getShippingShopNo();
		String shippingShopName = dcpOrder.getShippingShopName();
		if(langType==null||langType.isEmpty())
		{
			langType = "zh_CN";
		}
		HelpTools.writelog_waimai("【商城订单的门店编码不为空但门店名称为空】开始检查,订单号orderNo=" + orderNo);
		List<String> needGetShopNameList = new ArrayList<String>();
		if (shopNo!=null&&shopNo.trim().length()>0)
		{
			if (shopName==null||shopName.isEmpty())
			{
				dcpOrder.setShopName(shopNo);//给个默认值，后续查询数据库
				needGetShopNameList.add(shopNo);
			}
		}

		if (machShopNo!=null&&machShopNo.trim().length()>0)
		{
			if (machShopName==null||machShopName.isEmpty())
			{
				dcpOrder.setMachShopName(machShopNo);//给个默认值，后续查询数据库
				if (!needGetShopNameList.contains(machShopNo))
				{
					needGetShopNameList.add(machShopNo);
				}

			}
		}

		if (shippingShopNo!=null&&shippingShopNo.trim().length()>0)
		{
			if (shippingShopName==null||shippingShopName.isEmpty())
			{
				dcpOrder.setShippingShopName(shippingShopNo);//给个默认值，后续查询数据库
				if (!needGetShopNameList.contains(shippingShopNo))
				{
					needGetShopNameList.add(shippingShopNo);
				}

			}
		}

		if (needGetShopNameList!=null&&needGetShopNameList.size()>0)
		{
			String shopNoList_sqlCon = "";
			for (String ss : needGetShopNameList)
			{
				shopNoList_sqlCon = "'"+ss+"'"+","+shopNoList_sqlCon;
			}
			shopNoList_sqlCon = shopNoList_sqlCon.substring(0,shopNoList_sqlCon.length()-1);

			try {
				String sql = " Select A.ORGANIZATIONNO,B.ORG_NAME  from dcp_org A left join dcp_org_lang B on A.EID=B.EID and A.ORGANIZATIONNO=B.ORGANIZATIONNO and B.lang_type='"+langType+"'  "
						+ "  where A.EID='" + eId + "' AND A.ORGANIZATIONNO in (" + shopNoList_sqlCon + ")";
				HelpTools.writelog_waimai("【商城订单存在门店名称为空】【根据门店编码查询门店名称】sql=" +sql+ ",订单号orderNo=" + orderNo);
				List<Map<String, Object>> getDetail = this.doQueryData(sql, null);
				if (getDetail != null && getDetail.isEmpty() == false)
				{
					HelpTools.writelog_waimai("【商城订单存在门店名称为空】【根据门店编码查询门店名称】查询有数据,订单号orderNo=" + orderNo);
					for (Map<String, Object> par : getDetail)
					{
						String ORGANIZATIONNO = par.getOrDefault("ORGANIZATIONNO","").toString();
						String ORG_NAME = par.getOrDefault("ORG_NAME","").toString();
						if (ORGANIZATIONNO.isEmpty()||ORG_NAME.isEmpty())
						{
							continue;
						}
						if (shopNo!=null&&shopNo.trim().length()>0&&ORGANIZATIONNO.equals(shopNo))
						{
							if (shopName==null||shopName.isEmpty())
							{
								shopName = ORG_NAME;
								dcpOrder.setShopName(ORG_NAME);//给个默认值，后续查询数据库
								HelpTools.writelog_waimai("【商城订单存在门店名称为空】查询【下单门店】shopNo="+shopNo+",对应【下单门店名称】shopName="+ORG_NAME+",订单号orderNo=" + orderNo);
							}
						}
						if (machShopNo!=null&&machShopNo.trim().length()>0&&ORGANIZATIONNO.equals(machShopNo))
						{
							if (machShopName==null||machShopName.isEmpty())
							{
								machShopName = ORG_NAME;
								dcpOrder.setMachShopName(ORG_NAME);//给个默认值，后续查询数据库
								HelpTools.writelog_waimai("【商城订单存在门店名称为空】查询【生产门店】machShopNo="+machShopNo+",对应的【生产门店名称】machShopName="+ORG_NAME+",订单号orderNo=" + orderNo);
							}
						}
						if (shippingShopNo!=null&&shippingShopNo.trim().length()>0&&ORGANIZATIONNO.equals(shippingShopNo))
						{
							if (shippingShopName==null||shippingShopName.isEmpty())
							{
								shippingShopName = ORG_NAME;
								dcpOrder.setShippingShopName(ORG_NAME);//给个默认值，后续查询数据库
								HelpTools.writelog_waimai("【商城订单存在门店名称为空】查询【配送门店】shippingShopNo="+shippingShopNo+",对应的【配送门店名称】shippingShopName="+ORG_NAME+",订单号orderNo=" + orderNo);
							}
						}
					}

				}
				else
				{
					HelpTools.writelog_waimai("【商城订单存在门店名称为空】【根据门店编码查询门店名称】查询没有数据，只能门店名称=门店编号，订单号orderNo=" + orderNo);
				}


			} catch (Exception e) {
				HelpTools.writelog_waimai("【商城订单存在门店名称为空】【根据门店编码查询门店名称】查询异常:"+e.getMessage()+",只能门店名称=门店编号，订单号orderNo=" + orderNo);
			}
		}

	}

}
