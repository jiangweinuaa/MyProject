package com.dsc.spos.scheduler.job;


import java.io.File;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import java.util.Map;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import com.dsc.spos.utils.ETL_Kettle;
import com.dsc.spos.utils.PosPub;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class ETLJOB extends InitJob
{

	public ETLJOB()
	{		

	}	

	Logger logger = LogManager.getLogger(ETLJOB.class.getName());

	

	//0-代表成功  其他返回失败信息
	public String doExe(Object param)  throws Exception 
	{
		Map mapJob=(Map)param;
		
		String JOBNAME=mapJob.get("JOBNAME").toString();

		String JOB_FOLDER=mapJob.get("JOB_FOLDER").toString();
		
		//返回信息
		String sReturnInfo="";		

		logger.info("\r\n*********门店ETLJOB定时调用Start:************\r\n");

		try 
		{		
			//TOMCAT下ETL_KETTLE文件夹是否存在
			String dirpath= System.getProperty("catalina.home")+"\\ETL_KETTLE";
			File dirfile =new File(dirpath);
			if(!dirfile.exists())
			{
				dirfile.mkdir();
			}
			
			//ETL作业文件是否存在
			String pathFile=System.getProperty("catalina.home") + "\\ETL_KETTLE\\" + JOB_FOLDER + "\\" + JOBNAME + ".kjb";
			dirfile =new File(pathFile);
			if(!dirfile.exists())
			{
				PosPub.WriteETLJOBLog("******ETL作业文件不存在：" + pathFile+"******");
			}
			else 
			{
				ETL_Kettle ETL=new ETL_Kettle();
				
				ETL.runETLJobs(pathFile);		
				
				//ETL.runETLJobs("C:\\123\\123.kjb");
				
			}		
			
			PosPub.WriteETLJOBLog("******ETL作业文件完成：" + pathFile+"******");
			
		} 
		catch (Exception e) 
		{
			logger.error("\r\n******门店ETLJOB------》"+JOBNAME+"报错信息" + e.getMessage()+ "******\r\n");
			
			try 
			{
				StringWriter errors = new StringWriter();
				PrintWriter pw=new PrintWriter(errors);
				e.printStackTrace(pw);		
				
				pw.flush();
				pw.close();			
				
				errors.flush();
				errors.close();

				logger.error("\r\n******门店ETLJOB------》"+JOBNAME+"报错信息" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");

				pw=null;
				errors=null;
			} 
			catch (IOException e1) 
			{					
				logger.error("\r\n************门店ETLJOB------》"+JOBNAME+"报错信息" + e.getMessage() + "******\r\n");
			}

			//
			sReturnInfo="错误信息:" + e.getMessage();
			
			
		}
		finally 
		{
			logger.info("\r\n*********门店ETLJOB定时调用END:************\r\n");
		}		

		//
		return sReturnInfo;

	}	
	
	

	
	
	
	
}
