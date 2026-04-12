package com.dsc.spos.scheduler.job;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.ProcedureBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;

//********************自动查询加盟店信用信用充值付款结果查询及产生调用ERP增加信用额度**************************
//***********************************************************
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class AutoCreditPayResultQuery extends InitJob
{
	Logger logger = LogManager.getLogger(AutoCreditPayResultQuery.class.getName());

	static boolean bRun=false;

	public String doExe() 
	{
		//此服务是否正在执行中
		//返回信息
		String sReturnInfo="";
		if (bRun )
		{		
			logger.info("\r\n*********AutoCreditPayResultQuery正在执行中,本次调用取消:************\r\n");
			sReturnInfo="AutoCreditPayResultQuery正在执行中！";
			return sReturnInfo;
		}

		bRun=true;//			

		logger.info("\r\n*********AutoCreditPayResultQuery定时调用Start:************\r\n");
		try 
		{
			
			Calendar cal = Calendar.getInstance();
			//查询所有未支付成功及支付成功未呼叫ERP增加信用额度的记录
			String sql="select * from DCP_CREDIT_PAY where (status=0 and to_char(CREATETIME,'yyyy-mm-dd HH24:MI:SS') <to_char (sysdate-10/24/60,'yyyy-mm-dd HH24:MI:SS')"
					+ " and to_char(CREATETIME,'yyyy-mm-dd HH24:MI:SS') > to_char (sysdate-1,'yyyy-mm-dd HH24:MI:SS')) or (process_status='N'  AND status=2) order by CREATETIME   ";
			List<Map<String, Object>> list=this.doQueryData(sql, null);
			if(list!=null&&!list.isEmpty())
			{
				for (Map<String, Object> map: list) 
				{
					try{
						String eid     =map.get("EID").toString();
						String pay_type=map.get("PAYTYPE").toString();
						String order_id=map.get("ORDERID").toString();
						String trade_no=map.get("TRADENO").toString();
						String shop_code=map.get("SHOPID").toString();
						String appId    =map.get("APPID").toString();
						String pay_amt  =map.get("AMOUNT").toString();
						JSONObject request = new JSONObject();
						request.put("eid", eid)	;
						request.put("appId", appId)	;
						request.put("shop_code", shop_code)	;						
						request.put("shop_code", shop_code)	;					
						request.put("trade_no", trade_no);
						request.put("order_id", order_id);
						request.put("pay_type", pay_type);
						request.put("pay_amt", pay_amt);
						SimpleDateFormat dft=new SimpleDateFormat("yyyyMMdd");
						String mySysTime = dft.format(cal.getTime());
						request.put("payDate",mySysTime);

						JSONObject CreditPayResultQueryReq = new JSONObject();
						//这个token是无意义的
						CreditPayResultQueryReq.put("token", "abecbc7b42eb286a0d1f8587a9df97e5");
						CreditPayResultQueryReq.put("serviceId", "DCP_CreditPayResultQuery");
						CreditPayResultQueryReq.put("request",request);
						DispatchService ds = DispatchService.getInstance();
						logger.info("\r\nJOB增加ERP信用额度单号:"+order_id+"请求参数:"+ CreditPayResultQueryReq.toString());
						String resbody = ds.callService(CreditPayResultQueryReq.toString(), StaticInfo.dao);
						logger.info("\r\nJOB增加ERP信用额度单号:"+order_id+"返回信息:"+ resbody);
						JSONObject jsonres = new JSONObject();
						jsonres=JSONObject.parseObject(resbody);
						boolean success = jsonres.getBoolean("success");
						if(success)
						{
							logger.info("\r\n" + "JOB增加ERP信用额度成功,单号:"+order_id);
						}
						else
						{
							logger.info("\r\n" + "JOB增加ERP信用额度失败,单号="+ order_id + "\r\n" + resbody + "\r\n");
						}
					}catch(Exception e)
					{	
						logger.error("\r\n******AutoCreditPayResultQuery报错信息" + e.getMessage()+"\r\n******\r\n");
						sReturnInfo="错误信息:" + e.getMessage();
					}
				}
			}
		//PosPub.repairStock_detail(StaticInfo.dao,"66","xb0003","20210927","20211022");
		}
		catch (Exception e) 
		{
			logger.error("\r\n******AutoCreditPayResultQuery报错信息" + e.getMessage()+"\r\n******\r\n");
			sReturnInfo="错误信息:" + e.getMessage();
		}
		finally 
		{
			bRun=false;//
			logger.info("\r\n*********AutoCreditPayResultQuery定时调用End:************\r\n");
		}		
		return sReturnInfo;
	}
}
