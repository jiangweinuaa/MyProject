package com.dsc.spos.waimai.isv;

import com.dsc.spos.dao.*;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.SWaimaiBasicService;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import org.json.JSONObject;

import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ISV_WMJBPTokenReleaseBindingService extends SWaimaiBasicService {

	String logFileName = "MTReqestShopReleaseBindingLog";
	@Override
	public String execute(String responseStr) throws Exception {
		// TODO Auto-generated method stub
		if (responseStr == null || responseStr.length() == 0) {
			ISV_HelpTools.writelog_waimaiException("美团【门店解绑】发送的请求为空！");
			return null;
		}
		String requestId = UUID.randomUUID().toString().replace("-","");
		String timestamp = System.currentTimeMillis()+"";
		String[] MTResquest = responseStr.split("&");//
		if (MTResquest == null || MTResquest.length == 0) {
			ISV_HelpTools.writelog_waimaiException("解析美团【门店解绑】发送的请求格式有误！");
			return null;
		}
		String ReqlogFileName = logFileName;
		Map<String, Object> map_MTResquest = new HashMap<String, Object>();
		map_MTResquest.put("requestId",requestId);
		map_MTResquest.put("timestamp",timestamp);
		map_MTResquest.put("developerId", ISV_WMUtils.mt_developerId);
		String urlDecodeString = "";
		String ePoiId = "";//ERP方门店id 最大长度100  生成规则=客户标识_企业ID_门店编码
		String clientNo = "";//客户唯一标识
		boolean isCallBack_developerId = false;//有没有回传开发者ID：developerId
		for (String string_mt : MTResquest) {

			try {
				int indexofSpec = string_mt.indexOf("=");
				String s1 = string_mt.substring(0, indexofSpec);
				String s2 = string_mt.substring(indexofSpec + 1, string_mt.length());
				if ("ePoiId".equals(s1))
				{
					ePoiId = s2;
				}
				if ("developerId".equals(s1))
				{
					isCallBack_developerId = true;
				}
				String s2_decode = ISV_HelpTools.getURLDecoderString(s2);
				map_MTResquest.put(s1, s2_decode);
				urlDecodeString +=s1+"="+s2_decode+"&";
			} catch (Exception e) {
				// TODO: handle exception
				continue;
			}
		}
		if (urlDecodeString.endsWith("&"))
		{
			urlDecodeString = urlDecodeString.substring(0,urlDecodeString.length()-1);
		}
		/***************这个很重要，煞笔美团没有回传开发者ID*********************/
		if (!isCallBack_developerId)
		{
			urlDecodeString = "developerId="+ISV_WMUtils.mt_developerId+"&"+urlDecodeString;
		}

		clientNo = ISV_HelpTools.getJBPClientNo(ePoiId);
		if (clientNo==null||clientNo.isEmpty())
		{
			clientNo = " ";//不能为空,历史数据，比如美团闪惠绑定
		}
		/*if (clientNo != null && !clientNo.isEmpty())
		{
			ReqlogFileName = clientNo+"-"+logFileName;
		}*/
		ISV_HelpTools.writelog_fileName("【美团【门店解绑】回传消息URL转码后2】" +urlDecodeString+",消息id="+requestId,ReqlogFileName);
		saveMTMessage(clientNo,map_MTResquest,urlDecodeString);
		String req_json = getJBPTokenJsonString(map_MTResquest);
		Map<String, Object> res = new HashMap<String, Object>();
		this.processDUID(req_json, res);
		String msgType = "ShopReleaseBinding";
		//ISV_HelpTools.writelog_fileName("开始多线程 时间:"+System.currentTimeMillis(),"ssss");
		ISV_WM_WebHookService.sendMTMsgToClient(clientNo,msgType,map_MTResquest);
		//ISV_HelpTools.writelog_fileName("主线程结束 时间:"+System.currentTimeMillis(),"ssss");
		return null;

	}

	@Override
	protected void processDUID(String req, Map<String, Object> res) throws Exception {
	// TODO Auto-generated method stub
		String shopLogFileName = logFileName;
		try 
		{
			JSONObject obj = new JSONObject(req);
			String businessId = obj.get("businessId").toString();
			String mappingShopNo = obj.get("mappingShopNo").toString();
			String clientNo = obj.optString("customerNo"," ");
			String loadDocType = orderLoadDocType.MEITUAN;//美团渠道类型
			if (mappingShopNo==null||mappingShopNo.trim().isEmpty())
			{
				HelpTools.writelog_fileName("【解绑美团门店映射】失败:"+"映射的门店编号mappingShopNO为空",shopLogFileName);
				return;
			}
			DelBean db1 = null;	
			db1 = new DelBean("ISV_WM_MAPPINGSHOP");

			db1.addCondition("CLIENTNO", new DataValue(clientNo, Types.VARCHAR));
			db1.addCondition("LOAD_DOCTYPE", new DataValue(loadDocType, Types.VARCHAR));
			db1.addCondition("MAPPINGSHOPNO", new DataValue(mappingShopNo, Types.VARCHAR));
			db1.addCondition("BUSINESSID", new DataValue(businessId, Types.VARCHAR));
			db1.addCondition("ISJBP", new DataValue("Y", Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));
									
			this.doExecuteDataToDB();	
			HelpTools.writelog_fileName("【解绑美团门店映射数据库删除成功】"+" 映射的门店编号mappingShopNO="+mappingShopNo,shopLogFileName);
		
	  } 
		catch (SQLException e) 
		{
			HelpTools.writelog_fileName("【解绑美团门店映射执行语句】异常：" + e.getMessage() + "\r\n req请求内容:" + req,shopLogFileName);
	  }
		catch (Exception e) 
		{
		  // TODO: handle exception
			HelpTools.writelog_fileName("【解绑美团门店映射执行语句】异常：" + e.getMessage() + "\r\n req请求内容:" + req,shopLogFileName);
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

	private boolean saveMTMessage(String clientNo, Map<String, Object> messageMap,String originMessage) throws Exception
	{
		boolean nRet = false;
		String ReqlogFileName = logFileName;
		if (messageMap==null||messageMap.isEmpty())
		{
			return false;
		}
		if (originMessage==null||originMessage.isEmpty())
		{
			return false;
		}
		/*if (clientNo != null && !clientNo.isEmpty())
		{
			ReqlogFileName = clientNo+"-"+logFileName;
		}*/
		String requestId = messageMap.get("requestId").toString();
		String timestamp = messageMap.get("timestamp").toString();
		try
		{

			String messageType = "ShopReleaseBinding";
			String messageTypeStatus = messageMap.getOrDefault("businessId","").toString();//
			String orderNo = "";
			String shopId = messageMap.getOrDefault("ePoiId","").toString();
			String process_status = "Y";//是否推送到客户端(Y是；N否) ，默认Y，推送失败更新N


			String[] columns1 =
					{"ID", "CLIENTNO","SHOPID", "ORDERNO", "MESSAGETYPE", "MESSAGETYPESTATUS", "MESSAGE", "TIMESTAMP",
							"PROCESS_STATUS"};
			DataValue[] insValue1 = null;
			insValue1 = new DataValue[]
					{
							new DataValue(requestId, Types.VARCHAR),
							new DataValue(clientNo, Types.VARCHAR),
							new DataValue(shopId, Types.VARCHAR),
							new DataValue(orderNo, Types.VARCHAR),
							new DataValue(messageType, Types.VARCHAR),
							new DataValue(messageTypeStatus, Types.VARCHAR),
							new DataValue(originMessage, Types.CLOB),
							new DataValue(timestamp, Types.VARCHAR),
							new DataValue(process_status, Types.VARCHAR)
					};

			InsBean ib1 = new InsBean("ISV_WM_MT_MESSAGE", columns1);
			ib1.addValues(insValue1);
			this.addProcessData(new DataProcessBean(ib1));
			this.doExecuteDataToDB();
			nRet = true;
			ISV_HelpTools.writelog_fileName("【美团【门店解绑】回传信息保存数据库】成功,消息id="+requestId,ReqlogFileName);

		}
		catch (Exception e)
		{
			ISV_HelpTools.writelog_fileName("【美团【门店解绑】回传信息保存数据库】异常:"+e.getMessage()+",消息id="+requestId,ReqlogFileName);
		}

		return nRet;
	}

	private String getJBPTokenJsonString (Map<String, Object> map_MTResquest) throws Exception
	{
		String res = "";
		try {
			boolean isDigiwinISV = false;
			String developerId = map_MTResquest.getOrDefault("developerId","100146").toString();//开发者id
			String SignKey = "";
			if ("100146".equals(developerId))
			{
				isDigiwinISV = true;
				SignKey = "jevw2dkj37mb8pun";
			}
			String appAuthToken = map_MTResquest.getOrDefault("appAuthToken","").toString();// 门店绑定的授权token，将来的门店业务操作必须要传
			String ePoiId = map_MTResquest.getOrDefault("ePoiId","").toString();// 门店绑定时，传入的ERP厂商分配给门店的唯一标识 // 99_10001
			String poiId = map_MTResquest.getOrDefault("poiId","").toString();// 美团门店id
			String poiName = map_MTResquest.getOrDefault("poiName","").toString();// 美团门店名称
			String shopno_poi = ePoiId;//后面解析用
			String businessId = map_MTResquest.getOrDefault("businessId","").toString();// 1团购、2外卖、3闪惠、5支付、7预定、8全渠道会员
			String eId = "99";
			String erpShopNo = ePoiId;
			String channelId = orderLoadDocType.MEITUAN+"001";//默认渠道ID
			String customerNo = " ";//主键不能为
			//外卖门店绑定，解析下
			if ("2".equals(businessId))
			{
				try {

					if (isDigiwinISV)
					{
						int indexofSpec = shopno_poi.indexOf("_");//客户编码_企业编码_门店编码
						String s1 = shopno_poi.substring(0, indexofSpec);
						String s2 = shopno_poi.substring(indexofSpec + 1, shopno_poi.length());//企业编码_门店编码
						if(indexofSpec>0)
						{
							customerNo = s1;
							int indexofSpec_new = s2.indexOf("_");//99_LS_1001这种兼容下
							String s1_new = s2.substring(0, indexofSpec_new);
							String s2_new = s2.substring(indexofSpec_new + 1, s2.length());
							if(indexofSpec_new>0)
							{
								eId = s1_new;
								erpShopNo = s2_new;
							}
							else
							{
								erpShopNo = shopno_poi;
							}
						}
						else
						{
							erpShopNo = shopno_poi;
						}
					}
					else
					{
						//非鼎捷的服务商开发这id,兼容下，比如味多美万一升级3.0呢
						int indexofSpec = shopno_poi.indexOf("_");//企业编码_门店编码
						String s1 = shopno_poi.substring(0, indexofSpec);
						String s2 = shopno_poi.substring(indexofSpec + 1, shopno_poi.length());
						if(indexofSpec>0)
						{
							eId = s1;
							erpShopNo = s2;
						}
						else
						{
							erpShopNo = shopno_poi;
						}
					}



				} catch (Exception e) {

				}
			}

			JSONObject obj = new JSONObject();
			obj.put("customerNo", customerNo);
			obj.put("channelId", channelId);
			obj.put("orderShopNo", poiId);
			obj.put("orderShopName", poiName);
			obj.put("erpShopNo", erpShopNo);
			obj.put("erpShopName", "");
			obj.put("appAuthToken", appAuthToken);
			obj.put("eId", eId);
			obj.put("businessId", businessId);// 美团聚宝盆才有 默认2代表外卖
			obj.put("appKey", developerId);
			obj.put("appName", "");
			if ("1".equals(businessId))
			{
				obj.put("appName", "美团团购(服务商)");
			}
			else if ("2".equals(businessId))
			{
				obj.put("appName", "美团外卖(服务商)");
			}
			else if ("3".equals(businessId))
			{
				obj.put("appName", "美团闪惠(服务商)");
			}
			else
			{

			}

			obj.put("appSecret", SignKey);
			obj.put("isTest", "N");
			obj.put("isJbp", "Y");
			obj.put("mappingShopNo", ePoiId);//真正的唯一标识
			res = obj.toString();
			return res;

		} catch (Exception e) {
			//writelog_fileName("聚宝盆门店绑定后Token回传消息发送的请求格式有误！",logFileName);
			return res;
		}


	}

}
