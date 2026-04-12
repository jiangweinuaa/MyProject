package com.dsc.spos.scheduler.job;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.utils.Check;


//********************自动删除历史数据资料**********
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class DCP_ClearHisData extends InitJob
{
	Logger logger = LogManager.getLogger(DCP_ClearHisData.class.getName());
	static boolean bRun=false;	
	public String doExe() 
	{
		//此服务是否正在执行中
		//返回信息
		String sReturnInfo="";
		if (bRun )
		{		
			logger.info("\r\n*********DCP_ClearHisData正在执行中,本次调用取消:************\r\n");
			sReturnInfo="DCP_ClearHisData正在执行中！";
			return sReturnInfo;
		}
		bRun=true;//			
		logger.info("\r\n*********DCP_ClearHisData定时调用Start:************\r\n");
		try 
		{
			Date currentDate = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(currentDate);
			calendar.add(Calendar.DAY_OF_YEAR, -7);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String minDate = sdf.format(calendar.getTime());
			
		    String  sql="select distinct PARTITION_DATE AS BDATE from DCP_PROCESSTASK "
		    		+ " WHERE PARTITION_DATE<"+minDate 
		    		+ " ORDER BY PARTITION_DATE ";
			List<Map<String, Object>> getQDatas = this.doQueryData(sql, null);
			if (getQDatas != null && getQDatas.isEmpty() == false) 
			{
				for (Map<String, Object> Data: getQDatas) 
				{
					String bDate=Data.get("BDATE").toString();
					if(Check.Null(bDate))
						continue;
		            List<DataProcessBean> lstData = new ArrayList<DataProcessBean>();
		            DelBean db1 = new DelBean("DCP_PROCESSTASK_DETAIL");
		            db1.addCondition("PARTITION_DATE", new DataValue(bDate, Types.INTEGER));
		            lstData.add(new DataProcessBean(db1));
		            DelBean db2 = new DelBean("DCP_PROCESSTASK");
		            db2.addCondition("PARTITION_DATE", new DataValue(bDate, Types.INTEGER));
		            lstData.add(new DataProcessBean(db2));
		            StaticInfo.dao.useTransactionProcessData(lstData);
				}
			}


            
		}catch(Exception e)
		{
			logger.info("\r\n******DCP_ClearHisData报错信息" + e.getMessage()+"\r\n******\r\n");
			sReturnInfo="错误信息:" + e.getMessage();
		}
		finally 
		{
			bRun=false;//
			logger.info("\r\n*********DCP_ClearHisData定时调用End:************\r\n");
		}		
		return sReturnInfo;
	}
}
