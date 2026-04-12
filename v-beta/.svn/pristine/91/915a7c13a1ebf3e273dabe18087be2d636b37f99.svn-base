package com.dsc.spos.scheduler.job;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.ExecBean;
import com.dsc.spos.utils.PosPub;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class DCP_ClearAuthExpired extends InitJob
{
	Logger logger = LogManager.getLogger(DCP_ClearAuthExpired.class.getName());
	static boolean bRun=false;	
	public String doExe() 
	{
		//此服务是否正在执行中
		//返回信息
		String sReturnInfo="";
		if (bRun )
		{		
			logger.info("\r\n*********DCP_ClearAuthExpired‌正在执行中,本次调用取消:************\r\n");
			sReturnInfo="DCP_ClearAuthExpired‌正在执行中！";
			return sReturnInfo;
		}
		bRun=true;//			
		logger.info("\r\n*********DCP_ClearAuthExpired‌定时调用Start:************\r\n");
		try 
		{
    		String sql ="UPDATE PLATFORM_CREGISTERDETAIL SET TOKEN='',LASTMODITIME='' "   //SECOND 秒  MINUTE 分
    				  + "WHERE LASTMODITIME<=SYSDATE - INTERVAL '"+PosPub.iAuthExpiredTime+"' SECOND AND TOKEN IS NOT NULL ";
    		ExecBean exec = new ExecBean(sql);
    		List<DataProcessBean> data = new ArrayList<DataProcessBean>();
    		data.add(new DataProcessBean(exec));
    		sql="DELETE  FROM DCP_MODULAR_WORKING_AUTH "
    		  + "WHERE LASTMODITIME<=SYSDATE - INTERVAL '"+PosPub.iAuthExpiredTime+"' SECOND AND TOKEN IS NOT NULL ";
    		ExecBean execDelete = new ExecBean(sql);
    		data.add(new DataProcessBean(execDelete));
    		StaticInfo.dao.useTransactionProcessData(data);            
		}catch(Exception e)
		{
			logger.info("\r\n******DCP_ClearAuthExpired‌报错信息" + e.getMessage()+"\r\n******\r\n");
			sReturnInfo="错误信息:" + e.getMessage();
		}
		finally 
		{
			bRun=false;//
			logger.info("\r\n*********DCP_ClearAuthExpired‌定时调用End:************\r\n");
		}		
		return sReturnInfo;
	}
}
