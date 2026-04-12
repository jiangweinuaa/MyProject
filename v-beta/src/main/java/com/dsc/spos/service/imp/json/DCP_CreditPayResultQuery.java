package com.dsc.spos.service.imp.json;

import java.net.URLEncoder;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CreditPayResultQueryReq;
import com.dsc.spos.json.cust.res.DCP_CreditPayResultQueryRes;
import com.dsc.spos.scheduler.job.InsertWSLOG;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.EncryptUtils;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_CreditPayResultQuery extends SPosBasicService<DCP_CreditPayResultQueryReq,DCP_CreditPayResultQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_CreditPayResultQueryReq req) throws Exception 
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

		if (Check.Null(req.getRequest().getOrder_id()))
		{
			errMsg.append("商户单号不可为空值, ");
			isFail = true;
		} 


		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_CreditPayResultQueryReq> getRequestType() 
	{
		return new TypeToken<DCP_CreditPayResultQueryReq>(){};
	}

	@Override
	protected DCP_CreditPayResultQueryRes getResponseType() 
	{
		return new DCP_CreditPayResultQueryRes();
	}

	@Override
	protected DCP_CreditPayResultQueryRes processJson(DCP_CreditPayResultQueryReq req) throws Exception 
	{
		Logger logger = LogManager.getLogger(DCP_CreditQRcodeQuery.class.getName());

		//查询资料
		DCP_CreditPayResultQueryRes res = null;
		res = this.getResponse();
		res.setSuccess(false);
		res.setServiceDescription("付款未完成，请稍等!");
		String pay_type="#P1";
		String totalAmount="";//付款金额
		String trade_no="";//交易流水号

		try 
		{			

			String Mobile_Url="";
			String Mobile_Key="";
			String Mobile_Sign_Key="";
			if(req.geteId()==null)
			{
				req.seteId(req.getRequest().getEid());
				req.setShopId(req.getRequest().getShop_code());
			}
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
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "移动支付接口参数Mobile_Url、Mobile_Key、Mobile_Sign_Key未设置!");
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

			// 给单头赋值
			reqheader.put("pay_type", pay_type);//支付方式
			reqheader.put("shop_code", thirdShop);//门店
			reqheader.put("pos_code", req_pos_code);//POS机台
			reqheader.put("order_id",req.getRequest().getOrder_id() );//商户单号

			//
			String req_sign=reqheader.toString() + Mobile_Sign_Key;
//			EncryptUtils eu = new EncryptUtils();
//			req_sign=eu.encodeMD5(req_sign);
//			eu=null;
		    req_sign=DigestUtils.md5Hex(req_sign);
			//
			signheader.put("key", Mobile_Key);//
			signheader.put("sign", req_sign);//md5


			payReq.put("serviceId", "Query");

			payReq.put("request", reqheader);
			payReq.put("sign", signheader);


			String str = payReq.toString();

			String	resbody = "";			

			//编码处理
			str=URLEncoder.encode(str,"UTF-8");
			logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******单号:"+req.getRequest().getOrder_id()+"付款结果查询请求移动支付接口请求参数：  "+ "\r\n" + "门店=" +req.getShopId() + "\r\n" + str + "******\r\n");
			resbody=HttpSend.Sendcom(str, Mobile_Url);
			logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******单号:"+req.getRequest().getOrder_id()+"付款结果查询请求移动支付接口返回参数：  "+ "\r\n" + "门店=" +req.getShopId() + "\r\n" + resbody + "******\r\n");

			JSONObject jsonres = new JSONObject();
			jsonres=JSONObject.parseObject(resbody);
			String serviceDescription=jsonres.get("serviceDescription").toString();
			String serviceStatus=jsonres.get("serviceStatus").toString();

			if (jsonres.get("success").toString().equals("true"))
			{				
				JSONObject std_data_res = jsonres.getJSONObject("datas");				
				trade_no=std_data_res.get("trade_no").toString();//
				totalAmount=std_data_res.get("totalAmount").toString();//
				Calendar cal = Calendar.getInstance();//获得当前时间
                SimpleDateFormat dfa=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String now = dfa.format(cal.getTime());
				//if(req.getRequest().getShop_code()!=null)
				if(1==1)
				{
					List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();	
					UptBean ubecOrder=new UptBean("DCP_CREDIT_PAY");
					ubecOrder.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
					ubecOrder.addCondition("ORDERID", new DataValue( req.getRequest().getOrder_id(), Types.VARCHAR));
					ubecOrder.addCondition("STATUS", new DataValue("0", Types.VARCHAR));
					ubecOrder.addUpdateValue("STATUS", new DataValue("2", Types.VARCHAR));//2支付成功
					ubecOrder.addUpdateValue("LASTMODITIME", new DataValue(now, Types.DATE));
					ubecOrder.addUpdateValue("TRADENO", new DataValue(trade_no, Types.VARCHAR));
					lstData.add(new DataProcessBean(ubecOrder));
					StaticInfo.dao.useTransactionProcessData(lstData);  
					res.setSuccess(true);
					res.setServiceDescription("服务执行成功!");		
					res.setServiceStatus("0");//支付成功
					//呼叫接口增加信用额度
					JSONObject request = new JSONObject();
					request.put("order_id", req.getRequest().getOrder_id());
					request.put("pay_type", pay_type);
					request.put("pay_amt", totalAmount);
					request.put("payDate",new SimpleDateFormat("yyyyMMdd").format(new Date()));
					request.put("eId", req.geteId());
					request.put("shopId", req.getShopId());
					
					JSONObject DCP_CreditERPCreateReq = new JSONObject();
					DCP_CreditERPCreateReq.put("token", req.getToken());
					DCP_CreditERPCreateReq.put("serviceId", "DCP_CreditERPCreate");
					DCP_CreditERPCreateReq.put("request",request);
					DispatchService ds = DispatchService.getInstance();
					String resbody1 = ds.callService(DCP_CreditERPCreateReq.toString(), StaticInfo.dao);						JSONObject jsonres1 = new JSONObject();
					jsonres1=JSONObject.parseObject(resbody1);
					boolean success = jsonres1.getBoolean("success");
					serviceDescription=jsonres.get("serviceDescription").toString();
					if(success)
					{
						res.setServiceStatus("3");//增加信用额度成功
						res.setServiceDescription(serviceDescription);
						//logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"" + "增加ERP信用额度成功,单号="+req.getRequest().getOrder_id());
					}
					else
					{
						res.setServiceStatus("4");//增加信息额度失败
						res.setServiceDescription(serviceDescription);
						//logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"" + "增加ERP信用额度失败,单号="+ req.getRequest().getOrder_id() + "\r\n" + resbody + "\r\n");
					}
				}
			}
			else
			{ 
				if(serviceStatus.equals("008")||serviceDescription.equals("已关闭"))
				{
					Calendar cal = Calendar.getInstance();//获得当前时间
	                SimpleDateFormat dfa=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	                String now = dfa.format(cal.getTime());
					List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();	
					UptBean ubecOrder=new UptBean("DCP_CREDIT_PAY");
					ubecOrder.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
					ubecOrder.addCondition("ORDERID", new DataValue( req.getRequest().getOrder_id(), Types.VARCHAR));
					ubecOrder.addCondition("STATUS", new DataValue("0", Types.VARCHAR));
					ubecOrder.addUpdateValue("STATUS", new DataValue("1", Types.VARCHAR));//2支付成功
					ubecOrder.addUpdateValue("LASTMODITIME", new DataValue(now, Types.DATE));
					ubecOrder.addUpdateValue("TRADENO", new DataValue(trade_no, Types.VARCHAR));
					lstData.add(new DataProcessBean(ubecOrder));
					StaticInfo.dao.useTransactionProcessData(lstData);  
					
				}else if (serviceStatus.equals("NOTPAY") || serviceDescription.equals("未支付")) { //NOTPAY,未支付。    写两个条件的原因是， 防止CRM乱TM改状态、描述
                	res.setServiceStatus("2");
                	res.setServiceDescription(serviceDescription);
                 }else
                 {
                 	res.setServiceStatus("1");
                 	res.setServiceDescription(serviceDescription);
                 }
				logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"*********单号:"+req.getRequest().getOrder_id()+"付款结果查询请求移动支付接口>>>返回错误信息:" + serviceDescription + "************\r\n");
				//写数据库
				InsertWSLOG.insert_WSLOG("CreatePay","门店=" +req.getShopId(),req.geteId(),req.getShopId(),"1",str,resbody,serviceStatus,serviceDescription) ;
                res.setServiceDescription( "单号:"+req.getRequest().getOrder_id()+"付款结果查询请求移动支付接口>>>返回错误信息:" + serviceStatus + "," + serviceDescription);
			}
		} 
		catch (Exception e) 
		{
			//System.out.println(e.toString());

			logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******付款结果查询请求移动支付接口异常：门店=" +req.getShopId()+",组织编码=" + req.getOrganizationNO() + ",公司编码=" +req.geteId()  + "\r\n报错信息："+e.getMessage()+"******\r\n");
		}

		res.setTotalAmount(totalAmount);
		res.setTrade_no(trade_no);
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_CreditPayResultQueryReq req) throws Exception
    {
		String sql="select NVL(THIRD_SHOP,' ') AS THIRD_SHOP FROM DCP_ORG where EID='"+req.geteId()+"' AND ORGANIZATIONNO='"+req.getShopId()+"' ";

		return sql;
	}

	protected String getMobileParamSQL(DCP_CreditPayResultQueryReq req) throws Exception
	{
        String sql="select t.item,t.ITEMVALUE from platform_basesettemp t where (t.item ='Mobile_Url' or t.item='ApiUserKey' or t.item='ApiUserCode') and t.EID='"+req.geteId()+"'  and t.status='100' ";

        return sql;
	}




}
