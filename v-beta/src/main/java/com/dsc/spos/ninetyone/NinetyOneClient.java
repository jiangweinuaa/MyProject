package com.dsc.spos.ninetyone;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.dsc.spos.ninetyone.response.NOBasicReq;
import com.dsc.spos.ninetyone.util.NinetyOneUtils;
import com.dsc.spos.ninetyone.util.NinetyOneWebUtils;

/**
 * 客户端
 * 
 * @author LN 08546
 */
public class NinetyOneClient {
	
	/**
	 * 日志文件名称
	 */
	public String logFileName = NinetyOneConstants.clientLogFileName;
	
	Logger logger = LogManager.getLogger(NinetyOneClient.class.getName());
	
	/**
	 * 获取基础参数
	 */
	public NinetyOneClient(){
		
	}
	
	public String getNinetyOneResult(String mainUrl,String apiName,String xApiKey,Map<String, Object> map) throws Exception{
		String result="";
		NinetyOneUtils nu=new NinetyOneUtils();
		try{
			String request=nu.formatRequestStr(map);
			Map<String, Object> headers = new HashMap<String, Object>();
			headers.put("x-api-key", xApiKey);
			headers.put("Content-Type", "application/json");
			result = NinetyOneWebUtils.sendNineApp(apiName, mainUrl+apiName, headers, request);
			if(result!=null&&!result.trim().isEmpty()){
				NOBasicReq noRes=JSON.parseObject(result, NOBasicReq.class);
				//Success:成功； Failure:失敗
				if(noRes!=null&&"success".equals(noRes.getStatus())){
					//执行成功
					NinetyOneUtils.writelogFileName(logFileName, "\r\n******服务" +apiName+":请求91App执行成功:" +result+"******\r\n");
				}else{
					NinetyOneUtils.writelogFileName(logFileName, "\r\n******服务" +apiName+":请求91App返回结果异常-"
							+ "request:url-"+mainUrl+apiName+"headers-"+JSON.toJSONString(headers)+"request-"+JSON.toJSONString(request)
							+ "result:" +result+"******\r\n");
				}
			}else{
				NinetyOneUtils.writelogFileName(logFileName, "\r\n******服务" +apiName+":请求91App返回结果异常,为空值!-"
						+ "request:url-"+mainUrl+apiName+"headers-"+JSON.toJSONString(headers)+"request-"+JSON.toJSONString(request)
						+ "result:" +result+"******\r\n");
//				throw new Exception("请求91App返回结果异常,为空值!");
			}
		}catch(Exception e){
			throw e;
		}
		return result;
	}

	/**
	 * 25. 訂單清單查詢(列表)
	 * /V2/SalesOrder/GetList
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public String getSalesOrderList(String mainUrl,String xApiKey,Map<String, Object> map) throws Exception{
		return getNinetyOneResult(mainUrl, "/V2/SalesOrder/GetList", xApiKey, map);
	}
	
	/**
	 * 
	 * 26. 訂單查詢(详情)
	 * /V2/SalesOrder/Get
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public String getSalesOrderDetail(String mainUrl,String xApiKey,Map<String, Object> map) throws Exception{
		return getNinetyOneResult(mainUrl, "/V2/SalesOrder/Get", xApiKey, map);
	}
	
	/**
	 * 27. 退貨清單查詢(列表)
	 * /V2/ReturnGoodsOrder/GetList
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public String getReturnOrderList(String mainUrl,String xApiKey,Map<String, Object> map) throws Exception{
		return getNinetyOneResult(mainUrl, "/V2/ReturnGoodsOrder/GetList", xApiKey, map);
	}
	
	/**
	 * 28. 退貨單查詢(详情)
	 * /V2/ReturnGoodsOrder/Get
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public String getReturnOrderDetail(String mainUrl,String xApiKey,Map<String, Object> map) throws Exception{
		return getNinetyOneResult(mainUrl, "/V2/ReturnGoodsOrder/Get", xApiKey, map);
	}
	
	/**
	 * 29. 換貨清單查詢(列表)
	 * /V2/ChangeGoodsOrder/GetList
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public String getChangeOrderList(String mainUrl,String xApiKey,Map<String, Object> map) throws Exception{
		return getNinetyOneResult(mainUrl, "/V2/ChangeGoodsOrder/GetList", xApiKey, map);
	}
	
	/**
	 * 30. 換貨單查詢(详情)
	 * /V2/ChangeGoodsOrder/Get
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public String getChangeOrderDetail(String mainUrl,String xApiKey,Map<String, Object> map) throws Exception{
		return getNinetyOneResult(mainUrl, "/V2/ChangeGoodsOrder/Get", xApiKey, map);
	}
	
	/**
	 * 31. 貨運單查詢
	 * /V2/ShippingOrder/Get
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public String getShippingOrder(String mainUrl,String xApiKey,Map<String, Object> map) throws Exception{
		return getNinetyOneResult(mainUrl, "/V2/ShippingOrder/Get", xApiKey, map);
	}
	
	/**
	 * 32. 超取訂單配號
	 * /V2/Store/Shipping
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public String getStoreShipping(String mainUrl,String xApiKey,Map<String, Object> map) throws Exception{
		return getNinetyOneResult(mainUrl, "/V2/Store/Shipping", xApiKey, map);
	}
	   
	/**
	 * 33. 超取訂單取消配號
	 * /V1/Order/CancelShippingOrderCode
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public String shippingOrderCancel(String mainUrl,String xApiKey,Map<String, Object> map) throws Exception{
		return getNinetyOneResult(mainUrl, "/V1/Order/CancelShippingOrderCode", xApiKey, map);
	}
	
	/**
	 * 34. 超取訂單出貨
	 * /V1/Order/ShippingOrderConfirm
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public String shippingOrderConfirm(String mainUrl,String xApiKey,Map<String, Object> map) throws Exception{
		return getNinetyOneResult(mainUrl, "/V1/Order/ShippingOrderConfirm", xApiKey, map);
	}
	
	/**
	 * 35. 宅配出貨 – 宅配配號及出貨
	 * /V1/Order/DeliveryShipment
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public String deliveryShipment(String mainUrl,String xApiKey,Map<String, Object> map) throws Exception{
		return getNinetyOneResult(mainUrl, "/V1/Order/DeliveryShipment", xApiKey, map);
	}
	

}
