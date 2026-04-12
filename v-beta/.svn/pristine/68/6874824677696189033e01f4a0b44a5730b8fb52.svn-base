package com.dsc.spos.utils;

import java.io.IOException;

import org.pentaho.di.core.KettleEnvironment;  
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.plugins.PluginFolder;
import org.pentaho.di.core.plugins.StepPluginType;
import org.pentaho.di.core.util.EnvUtil;  
import org.pentaho.di.job.Job;  
import org.pentaho.di.job.JobMeta;  




public class ETL_Kettle 
{

	
	public ETL_Kettle()
	{		
		
	}
	
	/**
	 * 执行ETL JOB作业
	 * @param fileURL 
	 * @throws IOException 
	 */
	public void runETLJobs(String fileURL)  throws KettleException, IOException 
	{  
		//PosPub.WriteETLJOBLog("******ETL作业：" + fileURL +" 开始******");
		try 
		{  
	
			//D:\ETL\kettle\pdi-ce-7.1.0.0-12\data-integration\plugins
			//StepPluginType.getInstance().getPluginFolders().add(new PluginFolder("/home/ali/data-integration/plugins", false, true));
			//StepPluginType.getInstance().getPluginFolders().add(new PluginFolder("D:\\ETL\\kettle\\pdi-ce-7.1.0.0-12\\data-integration\\plugins", false, true));
			// 初始化kettle环境  
			//System.setProperty("javax.xml.parsers.DocumentBuilderFactory","com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
			//String KETTLE_PLUGIN_BASE_FOLDER = "D:\\ETL\\kettle\\pdi-ce-7.1.0.0-12\\data-integration\\plugins";
			//System.setProperty("KETTLE_PLUGIN_BASE_FOLDERS", KETTLE_PLUGIN_BASE_FOLDER);

			String KETTLE_HOME = this.getClass().getResource("/config/").getPath();
			System.setProperty("KETTLE_HOME", KETTLE_HOME);
			
			EnvUtil.environmentInit();  
			KettleEnvironment.init();  

			// 创建kjb元对象  
			JobMeta jobMeta = new JobMeta(fileURL, null);  
			// 创建kjb  
			Job job = new Job(null, jobMeta);  
			job.start();
			//等待执行完毕  
			job.waitUntilFinished();  
			if (job.getErrors() > 0) 
			{  
				
				String xx=job.getResult().getLogText();
				
				PosPub.WriteETLJOBLog("******ETL作业：" + fileURL +" 运行时错误"+xx+"******");

				String xy="";
			}  
			KettleEnvironment.shutdown();

		} 
		catch (KettleException e) 
		{  
			PosPub.WriteETLJOBLog("******ETL作业：" + fileURL +" 失败******" + e.getMessage());

		} 
		finally 
		{  
			//PosPub.WriteETLJOBLog("******ETL作业：" + fileURL +" 结束******");
		}  
	} 
	
	
	
	
	public static void main(String[] args) throws Exception 
	{		
		//runETLJobs("D:\\apache\\apache-tomcat-8.0.35\\apache-tomcat-8.0.35\\ETL_KETTLE\\test1\\test.kjb");
		//runETLJobs("D:\\apache\\apache-tomcat-8.0.35\\apache-tomcat-8.0.35\\ETL_KETTLE\\test2\\test.kjb");
		
		//runETLJobs("D:\\ETL\\kettle\\manual\\job\\test.kjb");
		
		
		
		ETL_Kettle ETL=new ETL_Kettle();
		
		//ETL.runETLJobs("C:\\123\\123.kjb");
		ETL.runETLJobs("D:\\apache\\apache-tomcat-8.0.35\\apache-tomcat-8.0.35\\ETL_KETTLE\\test1\\ETLJOB1.kjb");
		ETL.runETLJobs("D:\\apache\\apache-tomcat-8.0.35\\apache-tomcat-8.0.35\\ETL_KETTLE\\test1\\ETLJOB1.kjb");
		
		//ETL.runETLJobs("D:\\apache\\apache-tomcat-8.0.35\\apache-tomcat-8.0.35\\ETL_KETTLE\\test2\\test.kjb");
		
	}
	
	   
	
}
