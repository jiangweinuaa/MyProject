package com.dsc.spos.service.imp.json;

import java.net.URLEncoder;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderECDeliveryNoticeCreateReq;
import com.dsc.spos.json.cust.res.DCP_OrderECDeliveryNoticeCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.EncryptUtils;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 取货通知 （短信、Email）
 * 
 * @author yuanyy 2019-04-12
 *
 */
public class DCP_OrderECDeliveryNoticeCreate
		extends SPosAdvanceService<DCP_OrderECDeliveryNoticeCreateReq, DCP_OrderECDeliveryNoticeCreateRes> {

	@Override
	protected void processDUID(DCP_OrderECDeliveryNoticeCreateReq req, DCP_OrderECDeliveryNoticeCreateRes res)
			throws Exception {
		// TODO Auto-generated method stub
		
		Logger logger = LogManager.getLogger(DCP_OrderECDeliveryNoticeCreate.class.getName());

		Calendar cal = Calendar.getInstance();//获得当前时间
		SimpleDateFormat df=new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String mySysTime = df.format(cal.getTime());
		
		SimpleDateFormat df2=new SimpleDateFormat("yyyyMMdd");
		String noticeDate = df2.format(cal.getTime());
		df2 = new SimpleDateFormat("HHmmss");
		String noticeTime = df2.format(cal.getTime());
		
		String sql = null;
		
		String eId =req.geteId();
		String shopId =req.getShopId();
		String opType = req.getOpType();   // 1：短信通知 2：Email通知
		String shipmentNo = req.getShipmentNo();
		String ecOrderNo = req.getEcOrderNo();
		String ecPlatformNo = req.getEcPlatformNo(); // 电商
		String ecPlatformName = req.getEcPlatformName();
		String receiver = req.getReceiver();
		String receiverMobile = req.getReceiverMobile();
		String receiverEmail = req.getReceiverEmail();
		String lgPlatformNo = req.getLgPlatformNo(); //物流
		String lgPlatformName = req.getLgPlatformName();
		String getShopNo = req.getGetshopNo();
		String getShopName = req.getGetshopName();
		String receiverAddress = req.getReceiverAddress();
		// 主题：【91APP】取货通知
		// 通知内容： 【91APP】亲爱的老板，你的订单：XP0000000123，商品已到达取货门店【全家台中5店】，请尽快前往取货，谢谢！
		String title = "【"+ecPlatformName +"】 取货通知" ;
		String message = "【 "+ecPlatformName + "】亲爱的老板， 您的订单："+ ecOrderNo + ", "
				+ " 商品已到达取货门店 【 " +getShopName+" 】, 请尽快前往取货，谢谢！";
		try 
		{			
			String Mobile_Url="";
			String Mobile_Key="";
			String Mobile_Sign_Key="";
			String paraEmailAddress = "";
			String emailHost = "";
			String paraEmailPassword = "";
			
			String sqlDef = this.getMobileParamSQL(req);				
			List<Map<String, Object>> getQData = this.doQueryData(sqlDef, null);

			if (getQData != null && getQData.isEmpty() == false) 
			{
				for (Map<String, Object> par : getQData) 
				{
					if(par.get("ITEM").toString().equals("YCUrl"))
					{
						Mobile_Url=par.get("ITEMVALUE").toString();
					}
					
					if(par.get("ITEM").toString().equals("YCKey"))
					{
						Mobile_Key=par.get("ITEMVALUE").toString();
					}
					
					if(par.get("ITEM").toString().equals("YCSignKey"))
					{
						Mobile_Sign_Key=par.get("ITEMVALUE").toString();
					}
					
					if(par.get("ITEM").toString().equals("EmailAddress"))
					{
						paraEmailAddress=par.get("ITEMVALUE").toString();
					}
					
					
					if(par.get("ITEM").toString().equals("EmailHost"))
					{
						emailHost=par.get("ITEMVALUE").toString();
					}
					
					
					if(par.get("ITEM").toString().equals("EmailPassword"))
					{
						paraEmailPassword=par.get("ITEMVALUE").toString();
					}
					
					
				}						
			}				
			getQData=null;
			Mobile_Url=PosPub.getPAY_INNER_URL(eId);
			if(opType.equals("1")){//短信通知
				
				if(Mobile_Url.trim().equals("") || Mobile_Url.trim().equals("") ||Mobile_Url.trim().equals(""))
				{				
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "取货通知接口参数未设置!");
				}

				JSONObject payReq = new JSONObject();

				JSONObject reqheader = new JSONObject();
				JSONObject signheader = new JSONObject();

				// 给单头赋值
				reqheader.put("mobile", receiverMobile);
				reqheader.put("message", message);
				reqheader.put("messageType", "2");

				//
				String req_sign=reqheader.toString() + Mobile_Sign_Key;
				EncryptUtils eu = new EncryptUtils();
				req_sign=eu.encodeMD5(req_sign);
				eu=null;

				//
				signheader.put("key", Mobile_Key);//
				signheader.put("sign", req_sign);//md5
				payReq.put("serviceId", "SendMobileMessage");
				payReq.put("request", reqheader);
				payReq.put("sign", signheader);
				
				String str = payReq.toString();
				String	resbody = "";		
				
				//编码处理
				str=URLEncoder.encode(str,"UTF-8");
				resbody=HttpSend.Sendcom(str, Mobile_Url);

				logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******取货通知接口返回参数：  "+ "\r\n" + "门店=" +req.getShopId() + "\r\n" + resbody + "******\r\n");
				
				JSONObject jsonres = new JSONObject(resbody);

				String serviceDescription=jsonres.get("serviceDescription").toString();
				String serviceStatus=jsonres.get("serviceStatus").toString();
				
				if (jsonres.get("success").toString().equals("true"))
				{				
//					JSONObject std_data_res = jsonres.getJSONObject("datas");
					res.setSuccess(true);
					res.setServiceDescription("服务执行成功!");				
					sql = this.getShipmentDatasSql(req);
					List<Map<String, Object>> itemData = this.doQueryData(sql, null);
					String item = "1";
					if(!itemData.isEmpty()){
						item = itemData.get(0).get("ITEM").toString();
					}
					String[] columns1 = {"EID" , "SHOPID" ,"SHIPMENTNO", 
							"LGPLATFORMNO" , "LGPLATFORMNAME" , "ITEM",
							"NOTICEDATE" ,"NOTICETIME" , "DESCRIPTION",
							"NOTICEWAY" , "STATUS"};
					DataValue[] insValue1 = null;
					insValue1 = new DataValue[]
							{
								new DataValue(eId, Types.VARCHAR),
								new DataValue(shopId, Types.VARCHAR),
								new DataValue(shipmentNo, Types.VARCHAR),
								new DataValue(lgPlatformNo, Types.VARCHAR),
								new DataValue(lgPlatformName, Types.VARCHAR),
								new DataValue(item, Types.VARCHAR),
								new DataValue(noticeDate, Types.VARCHAR),
								new DataValue(noticeTime, Types.VARCHAR),
								new DataValue(message, Types.VARCHAR),
								new DataValue(opType, Types.VARCHAR),
								new DataValue("100", Types.VARCHAR)
								
							};
					InsBean ib1 = new InsBean("OC_SHIPMENT_NOTICE", columns1);
					ib1.addValues(insValue1);
					this.addProcessData(new DataProcessBean(ib1)); // 新增单头
					this.doExecuteDataToDB();
					if (res.isSuccess()) 
					{
						res.setServiceStatus("000");
						res.setServiceDescription("服务执行成功");						
					} 
				}
				else
				{ 
					res.setSuccess(false);
					res.setServiceDescription("服务执行失败： "+serviceDescription);	
					logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"*********"+"取货通知接口>>>返回错误信息:" + serviceDescription + "************\r\n");
					
					
			}
			}
			
			else if(opType.equals("2")){ //Email 通知
				if(paraEmailAddress.trim().equals("") || emailHost.trim().equals("") || paraEmailPassword.trim().equals(""))
				{				
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "取货通知接口邮箱参数未设置!");
				}
				
				// 获取系统属性
			    Properties properties = System.getProperties();
			 
			    // 设置邮件服务器
			    properties.setProperty("mail.smtp.host", emailHost);
			    
			    properties.put("mail.smtp.auth", "true");
			    // 获取默认session对象 
			    //Session session = Session.getDefaultInstance(properties);
			      
			    final String emailAddress = paraEmailAddress;
			    final String emailPassword = paraEmailPassword;
			    Session session = Session.getDefaultInstance(properties, new Authenticator() {
			    public PasswordAuthentication getPasswordAuthentication()
			    {
			    	return new PasswordAuthentication(emailAddress, emailPassword); //发件人邮件用户名、授权码
			    }
			    	  
				});
			 
			    try{
			    	// 创建默认的 MimeMessage 对象
				    MimeMessage sendMessage = new MimeMessage(session);
 
         			// Set From: 头部头字段
     				sendMessage.setFrom(new InternetAddress(paraEmailAddress));
					// Set To: 头部头字段
					sendMessage.addRecipient(Message.RecipientType.TO,
                      new InternetAddress(receiverEmail));
					// Set Subject: 头部头字段
					sendMessage.setSubject(title);
 
 					// 设置消息体
					sendMessage.setText(message);
 
 					// 发送消息
					Transport.send(sendMessage);
					//System.out.println( receiverEmail + " 邮件发送成功！！ ");

			    } 
			    catch (MessagingException mex) {
		
			    }
			}
			
		} 
		catch (Exception e) 
		{
			//System.out.println(e.toString());
			logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******取货通知接口异常：门店=" +req.getShopId()+",组织编码=" + req.getOrganizationNO() + ",公司编码=" +req.geteId()  + "\r\n报错信息："+e.getMessage()+"******\r\n");
		}
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderECDeliveryNoticeCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderECDeliveryNoticeCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderECDeliveryNoticeCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderECDeliveryNoticeCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_OrderECDeliveryNoticeCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderECDeliveryNoticeCreateReq>() {
		};
	}

	@Override
	protected DCP_OrderECDeliveryNoticeCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderECDeliveryNoticeCreateRes();
	}

	protected String getMobileParamSQL(DCP_OrderECDeliveryNoticeCreateReq req) throws Exception {
		String sql = "select t.item,t.ITEMVALUE from platform_basesettemp t where 1=1 and  "
				+ " (ITEM LIKE '%YC%' OR ITEM in ('EmailAddress', 'EmailHost' , 'EmailPassword') )"
				+ " and t.EID='" + req.geteId() + "'  and t.status='100' ";

		return sql;
	}

	protected String getShipmentDatasSql(DCP_OrderECDeliveryNoticeCreateReq req) {
		String eId = req.geteId();
		String shopId = req.getShopId();
		String shipmentNo = req.getShipmentNo();

		String sql = null;
		StringBuffer sqlbuf = new StringBuffer();
		sqlbuf.append("  SELECT NVL( MAX(item) , 0 )+1  AS item FROM DCP_shipment_notice " + " where EID = '"
				+ eId + "' and SHOPID = '" + shopId + "' " + " and shipmentNo = '" + shipmentNo + "' ");

		sql = sqlbuf.toString();
		return sql;
	}

}
