package com.dsc.spos.scheduler.job;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import com.dsc.spos.utils.CPUMonitorCalc;
import com.dsc.spos.utils.Mail;


//本进程的CPU占用情况
//CPU使用率
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class CPUThreadPercentMonitor extends InitJob
{

	Logger logger = LogManager.getLogger(CPUThreadPercentMonitor.class.getName());
	
	public CPUThreadPercentMonitor()
	{

	}

	public String doExe() 
	{
		//返回信息
		String sReturnInfo="";

		try 
		{
			
			int percent=CPUMonitorCalc.getInstance().getProcessCpu();		
			
			if (percent>60) 
			{
				Mail mail=new Mail();
				String[] receiverEmail={"418056790@qq.com","37501820@qq.com","382498008@qq.com"};
				String[] filenames=null;
				mail.sendMail(receiverEmail, "中台程序CPU过高", StaticInfo.sOrgTopName, filenames);
				mail=null;
			}
			
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

				logger.error("\r\n******CPU使用率CPUThreadPercentMonitor报错信息" + e.getMessage() +"\r\n" + errors.toString()+ "******\r\n");

				pw=null;
				errors=null;
			} 
			catch (IOException e1) 
			{					
				logger.error("\r\n******CPU使用率CPUThreadPercentMonitor报错信息" + e.getMessage() + "******\r\n");
			}		

			//
			sReturnInfo="错误信息:" + e.getMessage();
		}
		
		//
		return sReturnInfo;
	}
	

}
