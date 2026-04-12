package com.dsc.spos.scheduler.job;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.service.utils.DispatchService;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class NRCRestfulStatus extends InitJob 
{

	Logger logger = LogManager.getLogger(NRCRestfulStatus.class.getName());	
	
	public NRCRestfulStatus()
	{
		
	}
	
	public String doExe() throws IOException 
	{
		
		logger.info("\r\n***************NRCRestfulStatus中台监控调用START****************\r\n");

		//返回信息
		String sReturnInfo="";
		
		ParseJson pj=new ParseJson();

		String json="";
		
		try 
		{
			Map<String,Object> jsonMap=new HashMap<String,Object>();
			jsonMap.put("serviceId", "DCP_NRCRestfulStatusQuery");
			//这个token是无意义的
			jsonMap.put("token", "abecbc7b42eb286a0d1f8587a9df97e5");
			jsonMap.put("disHistory", "N");
			
			//json
			json=pj.beanToJson(jsonMap);			

			DispatchService ds = DispatchService.getInstance();
			String resXml = ds.callService(json, StaticInfo.dao);
			
			jsonMap=null;
		} 
		catch (Exception e) 
		{
			StringWriter errors = new StringWriter();
			PrintWriter pw=new PrintWriter(errors);
			e.printStackTrace(pw);		
			
			pw.flush();
			pw.close();			
			
			errors.flush();
			errors.close();
			
			logger.error("\r\n***********NRCRestfulStatus中台监控调用异常="+ errors.toString() + "\r\n Json=" + json + "\r\n"+ e.getMessage());
		
			pw=null;
			errors=null;
		}
			
		
		logger.info("\r\n***************NRCRestfulStatus中台监控调用END****************\r\n");

		return sReturnInfo;		
		
	}
	
	
}
