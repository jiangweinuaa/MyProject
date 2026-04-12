package com.dsc.spos.scheduler.job;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;
import com.dsc.spos.waimai.HelpTools;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class ELMOrderGet extends InitJob  
{
	Logger logger = LogManager.getLogger(ELMOrderGet.class.getName());
	
	static boolean bRun=false;//标记此服务是否正在执行中
	 String jddjLogFileName = "ELMOrderGetlog";
	
	public ELMOrderGet()
	{
		
	}
	
	public String doExe() throws Exception
	{
		String sReturnInfo = "";
		logger.info("\r\n***************JDDJOrderGet同步START****************\r\n");
		HelpTools.writelog_fileName("【JDDJOrderGet循环处理订单消息】同步START！",jddjLogFileName);
		try 
		{
		  //此服务是否正在执行中
			if (bRun)
			{		
				logger.info("\r\n*********JDDJOrderGet同步正在执行中,本次调用取消:************\r\n");
				HelpTools.writelog_fileName("【JDDJOrderGet循环处理订单消息】同步正在执行中,本次调用取消！",jddjLogFileName);
				return sReturnInfo;
			}

			bRun=true;//
			
			try 
			{
				String sql = this.getQuerySql();
				
				List<Map<String, Object>> getQData = this.doQueryData(sql, null);
				if (getQData != null && getQData.isEmpty() == false)
				{
					
				}
				else 
				{
					//
					sReturnInfo="无符合要求的数据！";
					HelpTools.writelog_fileName("【JDDJOrderGet循环处理订单消息】没有需要处理的订单消息！",jddjLogFileName);
					logger.info("\r\n******JDDJOrderGet没有需要获取的订单ID******\r\n");
				}
				
		  } 
			catch (Exception e) 
			{
				logger.error("\r\n******JDDJOrderGet获取订单报错信息" + e.getMessage() + "******\r\n");
				HelpTools.writelog_fileName("【JDDJOrderGet循环处理订单消息】获取订单消息异常："+e.getMessage(),jddjLogFileName);
				sReturnInfo="错误信息:" + e.getMessage();
		
		  }
			
		
	  } 
		catch (Exception e) 
		{
			logger.error("\r\n***************JDDJOrderGet获取订单异常"+e.getMessage()+"****************\r\n");
			sReturnInfo="错误信息:" + e.getMessage();
	  }
		finally 
		{
			bRun=false;//
		}
		
		logger.info("\r\n***************JDDJOrderGet同步END****************\r\n");
		
		return sReturnInfo;
	}
	
	
	protected String getQuerySql()
	{
		String sql = "select A.* from OC_mappingshop A inner join DCP_ORG B on A.SHOPID=B.Organizationno where A.Load_Doctype='1'";
		
		return sql;
	}

}
