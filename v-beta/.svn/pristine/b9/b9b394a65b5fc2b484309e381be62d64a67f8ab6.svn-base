package com.dsc.spos.waimai.yto;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.waimai.HelpTools;

public class ytoService
{
	 //订单创建
    private static final String KORDER_CREATE_ADAPTER = "korder_create_adapter";
    //订单取消
    private static final String KORDER_CANCEL_ADAPTER = "korder_cancel_adapter";
  
    private static final String QA_HOST = "http://openuat.yto56.com.cn/open";//测试域名
    
    private static final String ONLINE_HOST = "http://openapi.yto.net.cn/open";//线上域名
    //版本
    private static final String VERSION = "v1";
    //
    private static final String FORMAT = "json";
    
    private static final String logFileName = "ytoLog";
    
    private  String HOST = "http://openapi.yto.net.cn/open";

	private static final String NEW_ONLINE_HOST = "https://openapi.yto.net.cn:11443/open";//新得接口
	//订单创建
	private static final String NEW_KORDER_CREATE_ADAPTER = "privacy_create_adapter";
	//订单取消
	private static final String NEW_KORDER_CANCEL_ADAPTER = "korder_cancel_adapter";
    
    public ytoService()
	{
    	HOST = ONLINE_HOST;
	}
	public ytoService(boolean isTest)
	{
		if(isTest)
		{
			HOST = QA_HOST;			
		}
		else
		{
			HOST = ONLINE_HOST;
		}
	}
    
	 /**
     * K订单创建接口
     * @param format
     * @return
     */
    public String generateKOrderCreate(Map<String, Object> setMap,Map<String, Object> orderMap,Map<String, Object> SendShopInfoMap,
			List<Map<String, Object>> detailList) throws Exception {
    	String method = "korder_create_adapter";
    	String orderNo = orderMap.get("ORDERNO").toString();
    	String ytoType = setMap.getOrDefault("YTOTYPE","").toString();//Y-新的接口
    	if ("Y".equals(ytoType))
		{
			method = "privacy_create_adapter";
		}
    	try
		{   		
        	String secret = setMap.get("APPSECRET").toString();//"u2Z1F7Fh";//客户秘钥
        	String key = setMap.get("APPSIGNKEY").toString();//"K21000119";//客户K码，由圆通分配。
        	String appId = setMap.get("SHOPCODE").toString();//"oDZKFb";//客户标识
        	
        	
        	String url = HOST+"/"+method+"/"+VERSION+"/"+appId+"/"+key;//http://openuat.yto56.com.cn/open/korder_cancel_adapter/v1/oDZKFb/K21000119";
			if ("Y".equals(ytoType))
			{
				url = NEW_ONLINE_HOST+"/"+method+"/"+VERSION+"/"+appId+"/"+key;
			}


        	/*****************寄件人必传参数***********************/
        	String senderName = orderMap.getOrDefault("SHIPPINGSHOPNAME", "").toString();//寄件人姓名--》配送门店名称
        	if(senderName.isEmpty())
        	{
        		senderName = orderMap.getOrDefault("SHIPPINGSHOP", "").toString();
        	}
        	
        	String senderProvinceName = SendShopInfoMap.getOrDefault("PROVINCE", "").toString();//寄件人省名称
        	String senderCityName = SendShopInfoMap.getOrDefault("CITY", "").toString();//寄件人市名称
        	
        	String senderCountyName = SendShopInfoMap.getOrDefault("COUNTY", "").toString();//寄件人区县名称
        	String senderAddress = SendShopInfoMap.getOrDefault("ADDRESS", "").toString();//寄件人详细地址
        	String senderMobile = SendShopInfoMap.getOrDefault("PHONE", "").toString();//寄件人联系电话
        	
        	/***********************收件人信息********************************/
        	String recipientName = orderMap.getOrDefault("GETMAN", "").toString();
        	if (recipientName.isEmpty())
			{
				recipientName = orderMap.getOrDefault("CONTMAN", "").toString();
			}

        	String recipientProvinceName = orderMap.getOrDefault("PROVINCE", "").toString();
        	String recipientCityName = orderMap.getOrDefault("CITY", "").toString();
        	String recipientCountyName = orderMap.getOrDefault("COUNTY", "").toString();
        	String recipientAddress = orderMap.getOrDefault("ADDRESS", "").toString();
        	String recipientMobile = orderMap.getOrDefault("GETMANTEL", "").toString();
			if (recipientMobile.isEmpty())
			{
				recipientMobile = orderMap.getOrDefault("CONTTEL", "").toString();
			}
        	String remark = orderMap.getOrDefault("DELMEMO", "").toString();//配送备注
        	
        	String cstBusinessType = orderMap.getOrDefault("LOADDOCTYPE", "").toString();//客户业务类型；可以对客户订单进行业务或渠道区分
        	String cstOrderNo = orderNo;//客户的订单号
        	
        	
        	
        	KOrderCreateEntity kOrderCreateEntity = new KOrderCreateEntity();
            kOrderCreateEntity.setCustomerCode(key);
            kOrderCreateEntity.setMode(1);//1-同步，2-异步
            kOrderCreateEntity.setLogisticsNo(orderNo);//自己生成的物流单号
            kOrderCreateEntity.setSenderName(senderName);
            kOrderCreateEntity.setSenderProvinceName(senderProvinceName);
            kOrderCreateEntity.setSenderCityName(senderCityName);
            kOrderCreateEntity.setSenderCountyName(senderCountyName);
            kOrderCreateEntity.setSenderAddress(senderAddress);
            kOrderCreateEntity.setSenderMobile(senderMobile);
            
            kOrderCreateEntity.setRecipientName(recipientName);
            kOrderCreateEntity.setRecipientProvinceName(recipientProvinceName);
            kOrderCreateEntity.setRecipientCityName(recipientCityName);
            kOrderCreateEntity.setRecipientCountyName(recipientCountyName);
            kOrderCreateEntity.setRecipientAddress(recipientAddress);
            kOrderCreateEntity.setRecipientMobile(recipientMobile);
            kOrderCreateEntity.setRemark(remark);
            
            kOrderCreateEntity.setCstBusinessType(cstBusinessType);
            kOrderCreateEntity.setCstOrderNo(cstOrderNo);
            
            /********************配送时间**************************/
        	String pickupTime = "";
      		String shipDate = orderMap.get("SHIPDATE").toString();
      		String sdtime = orderMap.get("SHIPSTARTTIME").toString();//SHIPENDTIME
      		sdtime = sdtime.replace("-", "");
			if (sdtime.isEmpty())
			{
				sdtime = new SimpleDateFormat("HHmmss").format(new Date());
			}
      		pickupTime = shipDate+ sdtime;// 日期格式如"20181223110438"使用yyyyMMddHHmmss
      		
      		long longcur = System.currentTimeMillis();
    		Date dateSta = new SimpleDateFormat("yyyyMMddHHmmss").parse(pickupTime);
    		long longsta = dateSta.getTime();
    		// 差别到分钟
    		long diff = (longsta - longcur) / (1000 * 60);
    		/*if(diff<0)
    		{
    			return "";
    		}*/
    		if (diff >= 60) {
    			Calendar cal = Calendar.getInstance();
    			cal.setTime(dateSta);
    			//设置秒为0
    			cal.set(Calendar.SECOND,00);
    			//前退20分钟
    			cal.add(Calendar.MINUTE, -60);
    			kOrderCreateEntity.setStartTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime()));//预约上门取件开始时间
    			kOrderCreateEntity.setEndTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dateSta));//预约上门取件结束时间
    			
    		}
    		
    		if(detailList!=null&&detailList.isEmpty()==false)
    		{
    			List<KOrderCreateEntity.OrderGoodsDto> goods = new ArrayList<KOrderCreateEntity.OrderGoodsDto>();
    			for (Map<String, Object> mapGoods : detailList)
				{
    				String pluName = mapGoods.getOrDefault("PLUNAME", "").toString();
					if(pluName.isEmpty())
    				{
    					pluName = mapGoods.getOrDefault("PLUNO", "").toString();
    				}
					String featureName = mapGoods.getOrDefault("FEATURENAME", "").toString();
					if (!featureName.isEmpty())
					{
						pluName += " "+featureName;
					}
    				String qty = mapGoods.getOrDefault("QTY", "1").toString();
    				int qty_i = 1;
    				try
					{
    					qty_i = Integer.parseInt(qty);
					} catch (Exception e)
					{
						// TODO: handle exception
					}
					
    				KOrderCreateEntity.OrderGoodsDto orderGoodsDto = new KOrderCreateEntity.OrderGoodsDto();
    				orderGoodsDto.setName(pluName);
    				orderGoodsDto.setQuantity(qty_i);
    				goods.add(orderGoodsDto);
				}
    			kOrderCreateEntity.setGoods(goods);
    		}
          
            
           /* kOrderCreateEntity.setCustomerCode(key)
                    .setMode(2)
                    .setLogisticsNo("1111111111111111111")
                    .setSenderName("open-test")
                    .setSenderProvinceName("上海市")
                    .setSenderCityName("上海市")
                    .setSenderCountyName("青浦区")
                    .setSenderTownName("华新镇")
                    .setSenderAddress("华徐公路328号")
                    .setSenderMobile("12348575901")
                    .setRecipientName("open")
                    .setRecipientProvinceName("上海市")
                    .setRecipientCityName("上海市")
                    .setRecipientCountyName("青浦区")
                    .setRecipientTownName("华新镇")
                    .setRecipientAddress("华徐公路328号")
                    .setRecipientMobile("18348575902")
                    .setRemark("remark-test")
                    .setGotCode("123")
                    .setCstBusinessType("cstype")
                    .setCstOrderNo("csorderno")
                    .setStartTime(new Date())
                    .setEndTime(new Date())
                    .setWeight(new BigDecimal(0.12))
                    .setSettlementType(1);

            KOrderCreateEntity.OrderGoodsDto orderGoodsDto = new KOrderCreateEntity.OrderGoodsDto();
            orderGoodsDto.setName("mobile")
                    .setPrice(new BigDecimal(100))
                    .setQuantity(1)
                    .setWeight(new BigDecimal(0.18))
                    .setLength(new BigDecimal(10))
                    .setWidth(new BigDecimal(20))
                    .setHeight(new BigDecimal(5));
            kOrderCreateEntity.setGoods(Arrays.asList(orderGoodsDto));

            KOrderCreateEntity.OrderIncrementDto orderIncrementDto = new KOrderCreateEntity.OrderIncrementDto();
            orderIncrementDto.setType(2)
                    .setAmount(new BigDecimal(100))
                    .setPremium(new BigDecimal(0));
            kOrderCreateEntity.setIncrements(Arrays.asList(orderIncrementDto));

            */
            String param = JSONObject.toJSONString(kOrderCreateEntity);
            DispatchEntity dispatchEntity = buildReq(param, method, FORMAT, VERSION, secret, url);

    		String req = JSONObject.toJSONString(dispatchEntity);

    		String res = ytoHttpClientUtil.postRequest(url, req);

    		return res;
			
		} 
    	catch (Exception e)
		{
			// TODO: handle exception
    		HelpTools.writelog_fileName("【调用物流yto圆通】"+method+"异常:"+e.getMessage()+",订单号orderNo="+orderNo,logFileName);
			return "";
		}
    	
       
    }

    /**
     * K订单取消接口
     * @param format
     * @return
     */
	public String generateKOrderCancel(Map<String, Object> setMap, Map<String, Object> orderMap) throws Exception
	{

		String method = "korder_cancel_adapter";
		String orderNo = orderMap.get("ORDERNO").toString();
		String ytoType = setMap.getOrDefault("YTOTYPE","").toString();//Y-新的接口
		String logStart = "";
		if ("Y".equals(ytoType))
		{
			method = "korder_cancel_adapter";
		}
		try
		{
			String secret = setMap.get("APPSECRET").toString();//"u2Z1F7Fh";//客户秘钥
	    	String key = setMap.get("APPSIGNKEY").toString();//"K21000119";//客户K码，由圆通分配。
	    	String appId = setMap.get("SHOPCODE").toString();//"oDZKFb";//客户标识
	    	
	    	String cancelDesc =  orderMap.getOrDefault("REFUNDREASON", "").toString();
	    	if(cancelDesc.isEmpty())
	    	{
	    		cancelDesc = "顾客不想要了";
	    	}
					
			String url = HOST + "/" + method + "/" + VERSION + "/" + appId + "/" + key;// http://openuat.yto56.com.cn/open/korder_cancel_adapter/v1/oDZKFb/K21000119;
			if ("Y".equals(ytoType))
			{
				url = NEW_ONLINE_HOST+"/"+method+"/"+VERSION+"/"+appId+"/"+key;
			}
			KOrderCancelEntity kOrderCancelEntity = new KOrderCancelEntity();

			kOrderCancelEntity.setCustomerCode(key).setLogisticsNo(orderNo).setCancelDesc(cancelDesc);

			String param = JSONObject.toJSONString(kOrderCancelEntity);

			DispatchEntity dispatchEntity = buildReq(param, method, FORMAT, VERSION, secret, url);

			String req = JSONObject.toJSONString(dispatchEntity);

			String res = ytoHttpClientUtil.postRequest(url, req);

			return res;
			
		} catch (Exception e)
		{
			HelpTools.writelog_fileName("【调用物流yto圆通】接口"+method+"异常:"+e.getMessage()+",订单号orderNo="+orderNo,logFileName);
			return "";
		}
		
	}


	 /**
     * 报文组装
     * @param format
     * @return
     */
    private DispatchEntity buildReq(String param, String method, String format, String version, String secret, String url){
        DispatchEntity dispatchEntity = new DispatchEntity();
        dispatchEntity
                .setFormat(format)
                .setParam(param)
                .setTimestamp(String.valueOf(System.currentTimeMillis()));
        //加密(客户编码)
                  
        dispatchEntity.setSign(this.encryptSignForOpen(param+method+version,secret));
        //dispatchEntity.setUrl(url);
        return dispatchEntity;
    }
    
    /**
     * 加密
     * @param data
     * @param secret
     * @return
     */
    private  String encryptSignForOpen(String data, String secret) {
        String sign;
        try {
        	//HelpTools.writelog_fileName("【调用物流yto圆通】加密前data + secret="+data + secret,logFileName);
            byte[] signByte = DigestUtils.md5(data + secret);
            sign = Base64.encodeBase64String(signByte);
            //HelpTools.writelog_fileName("【调用物流yto圆通】加密后sign="+sign,logFileName);
        } 
        catch (Exception e) 
        {
            //log.error("加密失败.e:{}.", e.toString());
            sign = "ERROR";
        }
        return sign;
    }
}
