package com.dsc.spos.service.imp.json;
import java.sql.Date;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DsmDAO;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CreditQRcodeQueryReq;
import com.dsc.spos.json.cust.res.DCP_BFeeQueryRes;
import com.dsc.spos.json.cust.res.DCP_CreditQRcodeQueryRes;
import com.dsc.spos.json.cust.res.DCP_BFeeQueryRes.level1Elm;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.scheduler.job.InsertWSLOG;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.EncryptUtils;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.MySpringContext;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.utils.PosPub;

public class DCP_CreditQRcodeQuery extends SPosBasicService<DCP_CreditQRcodeQueryReq,DCP_CreditQRcodeQueryRes>
{
	Logger logger = LogManager.getLogger(DCP_CreditQRcodeQuery.class.getName());
    @Override
    protected boolean isVerifyFail(DCP_CreditQRcodeQueryReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if(req.getRequest()==null)
        {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (Check.Null(req.getRequest().getPay_type()))
        {
            errMsg.append("支付方式不可为空值, ");
            isFail = true;
        }

        if (Check.Null(req.getRequest().getPay_amt()))
        {
            errMsg.append("付款金额不可为空值, ");
            isFail = true;
        }

        if (PosPub.isNumericType(req.getRequest().getPay_amt())==false)
        {
            errMsg.append("付款金额必须为数字类型, ");
            isFail = true;
        }

        BigDecimal data1 = new BigDecimal(req.getRequest().getPay_amt());
        BigDecimal data2 = new BigDecimal("0");

        if (data1.compareTo(data2)<= 0)
        {
            errMsg.append("付款金额必须大于0, ");
            isFail = true;
        }


        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_CreditQRcodeQueryReq> getRequestType()
    {
        return new TypeToken<DCP_CreditQRcodeQueryReq>(){};
    }

    @Override
    protected DCP_CreditQRcodeQueryRes getResponseType()
    {
        return new DCP_CreditQRcodeQueryRes();
    }

    @Override
    protected DCP_CreditQRcodeQueryRes processJson(DCP_CreditQRcodeQueryReq req) throws Exception
    {
        Logger logger = LogManager.getLogger(DCP_CreditQRcodeQuery.class.getName());

        Calendar cal = Calendar.getInstance();//获得当前时间
        SimpleDateFormat df=new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String mySysTime = df.format(cal.getTime());

        //查询资料
        DCP_CreditQRcodeQueryRes res = null;
        res = this.getResponse();
        res.setSuccess(false);
        res.setServiceDescription("付款二维码获取失败!");

        String code_url="";//二维码内容
        String order_id="";//商户单号
        String pay_type=req.getRequest().getPay_type();
        if(Check.Null(pay_type)|| pay_type.isEmpty())
        {
        	pay_type="#P1";
        }
        String operation_id=req.getRequest().getOpenid();
        if(Check.Null(operation_id)|| operation_id.isEmpty())
        {
        	operation_id="system";
        }
        try
        {
            String Mobile_Url="";
            String Mobile_Key="";
            String Mobile_Sign_Key="";
            String sqlDef = this.getMobileParamSQL(req);
            List<Map<String, Object>> getQData = this.doQueryData(sqlDef, null);
            if (getQData != null && getQData.isEmpty() == false)
            {
                for (Map<String, Object> par : getQData)
                {
                    if(par.get("ITEM").toString().equals("Mobile_Url"))
                    {
                        Mobile_Url=par.get("ITEMVALUE").toString();
                    }

                    if(par.get("ITEM").toString().equals("ApiUserCode"))
                    {
                        Mobile_Key=par.get("ITEMVALUE").toString();
                    }

                    if(par.get("ITEM").toString().equals("ApiUserKey"))
                    {
                        Mobile_Sign_Key=par.get("ITEMVALUE").toString();
                    }

                }
            }
            getQData=null;
            Mobile_Url=PosPub.getPAY_INNER_URL(req.geteId());
            if(Mobile_Url.trim().equals("") || Mobile_Key.trim().equals("") ||Mobile_Sign_Key.trim().equals(""))
            {
            	res.setServiceDescription( "移动支付接口参数Mobile_Url(支付服务地址)、ApiUserCode(接入账号)、ApiUserKey(接入密钥)未设置!");
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "移动支付接口参数Mobile_Url(支付服务地址)、ApiUserCode(接入账号)、ApiUserKey(接入密钥)未设置!");
            }
            sqlDef = this.getQuerySql(req);
            getQData = this.doQueryData(sqlDef, null);
            String thirdShop="";
            if (getQData != null && getQData.isEmpty() == false)
            {
            	thirdShop=getQData.get(0).get("THIRD_SHOP").toString();
            }
            getQData=null;
            if( Check.Null(thirdShop) || thirdShop.isEmpty() || thirdShop.trim().equals("") || thirdShop.trim().equals(" ") )
            {
            	res.setServiceDescription( "组织信息-本门店加盟支付店号未配置!");
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "组织信息-本门店加盟支付店号未配置!");
            }
            JSONObject payReq = new JSONObject();

            JSONObject reqheader = new JSONObject();
            JSONObject signheader = new JSONObject();

            String req_pos_code="888888";
//            String req_order_id=req.geteId()+req.getShopId()+req_pos_code+mySysTime;
            String req_order_id=UUID.randomUUID().toString().replace("-", "");
            // 给单头赋值
            reqheader.put("pay_type", pay_type);//支付方式 #P1=微信 #P2=支付宝 #P103=LinePay
            reqheader.put("shop_code", thirdShop);//门店交易机构
            reqheader.put("pos_code", req_pos_code);//终端设备编号
            reqheader.put("order_id",req_order_id );//商户单号
            reqheader.put("order_name", "要货-信用额度充值");//订单标题
            reqheader.put("order_des", "要货-信用额度充值");//订单描述
            reqheader.put("pay_amt", req.getRequest().getPay_amt());// 支付金额
            reqheader.put("pay_nodiscountamt", "0");// 不可打折金额
            reqheader.put("ip", "127.0.0.1");// 终端IP，微信需要
            reqheader.put("operation_id", operation_id);//操作员
            reqheader.put("notify_url", "http://www.aaa.com/");//回调地址
            reqheader.put("allow_pay_type", ""); // 逗号隔开，可空，空表示都允许 当pay_type为空时，POS端有促销时跟支 付方式互斥时填写。
            reqheader.put("trade_type", req.getRequest().getTrade_type());// NATIVE -Native支付 JSAPI -JSAPI支付  空默认是NATIVE
            reqheader.put("appid", req.getRequest().getAppid());// 微信appid，微信JSAPI时必填
            reqheader.put("openid", req.getRequest().getOpenid()); // 微信openid微信JSAPI时必填
            //
            String req_sign=reqheader.toString() + Mobile_Sign_Key;
            req_sign=DigestUtils.md5Hex(req_sign);
            //
            signheader.put("key", Mobile_Key);//
            signheader.put("sign", req_sign);//md5


            payReq.put("serviceId", "CreatePay");

            payReq.put("request", reqheader);
            payReq.put("sign", signheader);

            String str = payReq.toString();
            String	resbody = "";
            str=URLEncoder.encode(str,"UTF-8");
            logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******获取付款二维码请求移动支付接口请求参数：  "+ "\r\n" + "门店=" +req.getShopId() + "\r\n 地址:"+Mobile_Url+"\r\n 内容:" + str + "******\r\n");
            resbody=HttpSend.Sendcom(str, Mobile_Url);
            logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******获取付款二维码请求移动支付接口返回参数：  "+ "\r\n" + "门店=" +req.getShopId() + "\r\n" + resbody + "******\r\n");

            JSONObject jsonres = new JSONObject();
            jsonres=JSON.parseObject(resbody);            
            String serviceDescription=jsonres.get("serviceDescription").toString();
            String serviceStatus=jsonres.get("serviceStatus").toString();

            if (jsonres.get("success").toString().equals("true"))
            {
                res.setDatas(new ArrayList<DCP_CreditQRcodeQueryRes.level1Elm>());
            	DCP_CreditQRcodeQueryRes.level1Elm   datas =res.new level1Elm();
            	DCP_CreditQRcodeQueryRes.levelPrepay prepay =res.new levelPrepay();


                JSONObject std_data_res = jsonres.getJSONObject("datas");
                code_url=std_data_res.get("code_url").toString();//二维码数据
                order_id=req_order_id;//商户单号
                JSONObject prepay_res = std_data_res.getJSONObject("prepay");
                
                SimpleDateFormat dfa=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String now = dfa.format(cal.getTime());
            	datas.setTrade_type(std_data_res.get("trade_type").toString());
            	datas.setCode_url(std_data_res.get("code_url").toString());
            	datas.setPrepay_id(std_data_res.get("prepay_id").toString());
            	datas.setPay_type(std_data_res.get("pay_type").toString());
            	String timeStamp="";
            	String out_trade_no="";
            	String mchid="";
            	if(std_data_res.get("mchid")!=null)
            	{
            		mchid=std_data_res.get("mchid").toString();
            	}
            	if(prepay_res!=null)
            	{
            		prepay.setNonceStr(prepay_res.get("nonceStr").toString());
            		prepay.setOut_trade_no(prepay_res.get("out_trade_no").toString());
            		prepay.setPackageStr(prepay_res.get("packageStr").toString());
            		//           	prepay.setPaymentAccessToken(prepay_res.get("paymentAccessToken").toString());
            		//            	prepay.setPaymentUrl_app(prepay_res.get("paymentUrl_app").toString());
            		//            	prepay.setPaymentUrl_web(prepay_res.get("paymentUrl_web").toString());
            		prepay.setPaySign(prepay_res.get("paySign").toString());
            		//            	prepay.setPrepay_id(prepay_res.get("prepay_id").toString());
            		prepay.setSignType(prepay_res.get("signType").toString());
            		prepay.setTimeStamp(prepay_res.get("timeStamp").toString());
            		//            	prepay.setTransactionId(prepay_res.get("transactionId").toString());
            		timeStamp   = prepay_res.get("timeStamp").toString();
            		out_trade_no= prepay_res.get("out_trade_no").toString();
            	}
            	datas.setPrepay(prepay);
            	res.getDatas().add(datas); 
        		res.setSuccess(true);
                res.setServiceDescription("服务执行成功!");
                String[] columns1 = {
                		"EID","SHOPID","POSID","ORDERID","DESCRIPTION","PAYTYPE",
                		"AMOUNT","OPID","CREATETIME","MCHID",
                		"TRADETYPE","PREPAYID","CODEURL",
                		"TIMESTAMP","TRADENO","STATUS","PROCESS_STATUS"};
                DataValue[] insValue1 = null;
                insValue1 = new DataValue[]{
                        new DataValue(req.geteId().toString(), Types.VARCHAR),
                        new DataValue(req.getShopId(), Types.VARCHAR),
                        new DataValue(req_pos_code, Types.VARCHAR),
                        new DataValue(req_order_id, Types.VARCHAR),
                        new DataValue("要货-信用额度充值", Types.VARCHAR),
                        new DataValue(pay_type, Types.VARCHAR),
                        new DataValue(req.getRequest().getPay_amt(), Types.DOUBLE),
                        new DataValue(operation_id, Types.VARCHAR),
                        new DataValue(now,Types.DATE),
                        new DataValue(mchid,Types.VARCHAR),
                        new DataValue(std_data_res.get("trade_type").toString(), Types.VARCHAR),
                        new DataValue(std_data_res.get("prepay_id").toString(), Types.VARCHAR),
                        new DataValue(code_url, Types.VARCHAR),
                        new DataValue(timeStamp, Types.VARCHAR),
                        new DataValue(out_trade_no, Types.VARCHAR),
                        new DataValue(0, Types.INTEGER),//单据状态0.支付创建 1.支付处理中 2.支付成功
                        new DataValue("N", Types.VARCHAR),//
                        };
                
                this.dao.insert("DCP_CREDIT_PAY",columns1,insValue1);
                /**
                 *   调用CreatePay接口 成功后 发起一个线程来轮询查询Query 服务 查询是否支付成功 若成功则 调用调用增加ERP信用额度接口服务
                 */
                // 调用线程
    	    	try {
    	            Thread thread = new Thread(new QueryPay(req.geteId().toString(),req.getShopId().toString(),pay_type, thirdShop,req_pos_code,req_order_id
    	            		,operation_id,Mobile_Sign_Key,Mobile_Key,Mobile_Url,req));
    	           // thread.setDaemon(true);
    	            thread.start();
    	        } catch (Exception ex) {
    	            logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"SendMessage1: " + ex.getMessage());
    	        }
            }
            else
            {
                logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"*********"+"获取付款二维码请求移动支付接口>>>返回错误信息:" + serviceDescription + "************\r\n");
                res.setServiceDescription("返回错误信息:" + resbody);
            }
        }
        catch (Exception e)
        {
        	res.setServiceDescription("\r\n******获取付款二维码请求移动支付接口异常：门店=" +req.getShopId()+",组织编码=" + req.getOrganizationNO() + ",公司编码=" +req.geteId()  + "\r\n报错信息："+e.getMessage()+"******\r\n");
            logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******获取付款二维码请求移动支付接口异常：门店=" +req.getShopId()+",组织编码=" + req.getOrganizationNO() + ",公司编码=" +req.geteId()  + "\r\n报错信息："+e.getMessage()+"******\r\n");
        }
        
        res.setCode_url(code_url);
        res.setOrder_id(order_id);
        
        return res;
    }
    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    protected String getQuerySql(DCP_CreditQRcodeQueryReq req) throws Exception {
        // TODO Auto-generated method stub
    	
        String sql="select NVL(THIRD_SHOP,'') AS THIRD_SHOP FROM DCP_ORG where EID='"+req.geteId()+"' AND ORGANIZATIONNO='"+req.getShopId()+"' ";
        return sql;
    }

    protected String getMobileParamSQL(DCP_CreditQRcodeQueryReq req) throws Exception
    {
        String sql="select t.item,t.ITEMVALUE from platform_basesettemp t where (t.item ='Mobile_Url' or t.item='ApiUserKey' or t.item='ApiUserCode') and t.EID='"+req.geteId()+"'  and t.status='100' ";

        return sql;
    }


	private static class QueryPay implements Runnable {
		private String eId;
		private String shopId;
		private String pay_type;
        private String shop_code;
        private String pos_code;
        private String order_id;
        private String operation_id;
        private String Mobile_Sign_Key;
        private String Mobile_Key;
        private String Mobile_Url;
        private int    queryNumber=0;
        private DCP_CreditQRcodeQueryReq req;
        public QueryPay(String p_eid,String p_shopId,String p_pay_type, String p_shop_code,String p_pos_code,String p_order_id
        		,String p_operation_id,String p_Mobile_Sign_Key,String p_Mobile_Key,String p_Mobile_Url
        		,DCP_CreditQRcodeQueryReq p_req) {
        	eId      = p_eid ;
        	shopId   = p_shopId;
        	pay_type = p_pay_type;
        	shop_code = p_shop_code;
        	pos_code = p_pos_code;
        	order_id = p_order_id;
        	operation_id=p_operation_id;
        	Mobile_Sign_Key=p_Mobile_Sign_Key;
        	Mobile_Key     =p_Mobile_Key;
        	Mobile_Url     =p_Mobile_Url;
        	req            =p_req;
        }
        Logger logger = LogManager.getLogger(QueryPay.class.getName());
        public void run() {
            try {
        		while (true) {
        				if(queryNumber>36)
        					break;
        				JSONObject QueryReq = new JSONObject();
        				QueryReq.put("serviceId", "Query");
        				JSONObject payReq = new JSONObject();
        				payReq.put("pay_type", pay_type);
        				payReq.put("shop_code", shop_code);
        				payReq.put("pos_code", pos_code);
        				payReq.put("order_id", order_id);
        				payReq.put("trade_no", "");
        				payReq.put("operation_id", operation_id);
        				payReq.put("ip", "127.0.0.1");
        				QueryReq.put("request", payReq);
        				String queryReqStr = payReq.toString();
        				String querySign = DigestUtils.md5Hex(queryReqStr + Mobile_Sign_Key);
        				JSONObject querySignJson = new JSONObject();
        				querySignJson.put("sign", querySign);
        				querySignJson.put("key", Mobile_Key);
                        QueryReq.put("sign", querySignJson);
//                        //********** 已经准备好Query的json，开始调用 *************
                        logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"单号:"+order_id+"已经准备好Query的json，开始调用:"+QueryReq.toString());
                        String queryResStr = HttpSend.Sendcom(QueryReq.toString(), Mobile_Url);
                        logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"单号:"+order_id+"已经准备好Query的json，调用完成:"+queryResStr);
                        JSONObject queryResJson = new JSONObject();
                        queryResJson=JSON.parseObject(queryResStr);
                        String querySuccess = queryResJson.getString("success").toUpperCase(); // TRUE 或 FALSE
                        String queryStatus = queryResJson.getString("serviceStatus").toUpperCase();
                        String queryServiceDescription = queryResJson.getString("serviceDescription").toUpperCase();                        
                        if (querySuccess.toUpperCase().equals("TRUE")) {
                        	    // 查询支付成功后 填充Pay{RefNo} 银联卡交易流水号
                        	    logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"支付成功:"+order_id);
                        	    Calendar cal = Calendar.getInstance();
                                SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String now = df.format(cal.getTime());
                                
                        		String queryResStr2 = queryResJson.get("datas").toString();
                        		JSONObject queryResJson2 = new JSONObject();
                        		queryResJson2=JSON.parseObject(queryResStr2);
                        		String trade_no_query = queryResJson2.get("trade_no").toString();
								List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();	
								UptBean ubecOrder=new UptBean("DCP_CREDIT_PAY");
								ubecOrder.addCondition("EID", new DataValue(eId, Types.VARCHAR));
								ubecOrder.addCondition("ORDERID", new DataValue(order_id, Types.VARCHAR));
								ubecOrder.addCondition("STATUS", new DataValue("0", Types.VARCHAR));
								ubecOrder.addUpdateValue("STATUS", new DataValue("2", Types.VARCHAR));//2支付成功
								ubecOrder.addUpdateValue("LASTMODITIME", new DataValue(now, Types.DATE));
								ubecOrder.addUpdateValue("TRADENO", new DataValue(trade_no_query, Types.VARCHAR));
								lstData.add(new DataProcessBean(ubecOrder));
								StaticInfo.dao.useTransactionProcessData(lstData);  
								//呼叫接口增加信用额度
		                        JSONObject request = new JSONObject();
		                        request.put("order_id", order_id);
		                        request.put("pay_type", pay_type);
		                        request.put("pay_amt", req.getRequest().getPay_amt());
		                        request.put("eId", eId);
		                        request.put("shopId",shopId);
		                        SimpleDateFormat dft=new SimpleDateFormat("yyyyMMdd");
		                        String mySysTime = dft.format(cal.getTime());
		                        request.put("payDate",mySysTime);
		                        
		                        JSONObject DCP_CreditERPCreateReq = new JSONObject();
		                        DCP_CreditERPCreateReq.put("token", req.getToken());
		                        DCP_CreditERPCreateReq.put("serviceId", "DCP_CreditERPCreate");
		                        DCP_CreditERPCreateReq.put("request",request);
		    					DispatchService ds = DispatchService.getInstance();
		    					String resbody = ds.callService(DCP_CreditERPCreateReq.toString(), StaticInfo.dao);	
		    					JSONObject jsonres = new JSONObject();
		    					jsonres=JSONObject.parseObject(resbody);
		    					boolean success = jsonres.getBoolean("success");
		    					if(success)
		    					{
		    						logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"" + "增加ERP信用额度成功,单号="+order_id);
		    					}
		    					else
		    					{
		    						logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"" + "增加ERP信用额度失败,单号="+ order_id + "\r\n" + resbody + "\r\n");
		    					}
		                        break;
                        }else
                        {
                            if (queryStatus.equals("008") || queryServiceDescription.equals("已关闭")) { //008,已关闭。    写两个条件的原因是， 防止CRM乱TM改状态、描述
                            	//更改单据状态
                            	break;
                            }
                            if (queryStatus.equals("NOTPAY") || queryServiceDescription.equals("未支付")) { //NOTPAY,未支付。    写两个条件的原因是， 防止CRM乱TM改状态、描述
                            	// 设置每隔多长时间执行一次
                            	Thread.sleep(10 * 1000);
                            	queryNumber++;
                            	continue;
                             }
                        	Thread.sleep(10 * 1000);
                        	queryNumber++;
                        	continue;
                        }
        		}
            } catch (Exception ex) {
            }
        }
    }


}
