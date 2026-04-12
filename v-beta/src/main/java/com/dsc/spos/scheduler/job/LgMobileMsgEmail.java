package com.dsc.spos.scheduler.job;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

//超商取货短信通知(大智通、便利达康)
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class LgMobileMsgEmail extends InitJob
{
	//******兼容即时服务的,只查询指定的那张单据******
	String pEId="";
	String pShop="";	
	String pOrganizationNO="";
	String pBillNo="";

	Logger logger = LogManager.getLogger(EcShopee.class.getName());

	static boolean bRun=false;//标记此服务是否正在执行中


	public LgMobileMsgEmail()
	{

	}


	public LgMobileMsgEmail(String eId,String shopId,String organizationNO, String billNo)
	{
		pEId=eId;
		pShop=shopId;
		pOrganizationNO=organizationNO;
		pBillNo=billNo;
	}

	public String doExe()
	{

		//返回信息
		String sReturnInfo="";
		//此服务是否正在执行中
		if (bRun && pEId.equals(""))
		{		
			logger.info("\r\n*********电商订单短信通知正在执行中,本次调用取消:************\r\n");

			sReturnInfo="定时传输任务-电商订单短信通知正在执行中！";
			return sReturnInfo;
		}

		bRun=true;//			

		logger.info("\r\n*********电商订单短信通知定时调用Start:************\r\n");

		try 
		{
				
			
		} 
		catch (Exception e) 
		{
			try 
			{
				StringWriter errors = new StringWriter();
				PrintWriter pw=new PrintWriter(errors);
				e.printStackTrace(pw);		
				
				pw.flush();
				pw.close();			
				
				errors.flush();
				errors.close();

				logger.error("\r\n******电商订单短信通知报错信息" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");
				
				pw=null;
				errors=null;
			} 
			catch (IOException e1) 
			{					
				logger.error("\r\n******电商订单短信通知报错信息" + e.getMessage() + "******\r\n");
			}		

			//
			sReturnInfo="错误信息:" + e.getMessage();

		}
		finally 
		{
			bRun=false;//
			logger.info("\r\n*********电商订单短信通知定时调用End:************\r\n");
		}

		return sReturnInfo;
	}



}
