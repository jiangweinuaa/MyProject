package com.dsc.spos.scheduler.job;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;

import com.dsc.spos.scheduler.util.QuartzUtil;
import com.dsc.spos.utils.MapDistinct;
import org.quartz.Scheduler;  

//排程监控
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class JOBTIMER extends InitJob
{
	public JOBTIMER()
	{	   

	}


	Logger logger = LogManager.getLogger(JOBTIMER.class.getName());

	//0-代表成功  其他返回失败信息
	public String doExe() 
	{ 
		//返回信息
		String sReturnInfo="";		

		logger.info("\r\n*********排程监控JOBTIMER定时调用Start:************\r\n");

		try 
		{

			//只查询ETL工具类作业
			String sqlquartz="select * from job_quartz ORDER BY JOB_TIME,JOB_NAME";//where ETL_KETTLE='Y'

			List<Map<String, Object>> getQuartz = this.doQueryData(sqlquartz, null);
			if (getQuartz != null && getQuartz.isEmpty() == false) 
			{			   
				//ETL排程记录中JOB_GROUPNAME会被JOB_NAME替换，索引这里是不会重复的
				List<String> lstGroup=QuartzUtil.getScheduler().getJobGroupNames();			   
				//
				if (lstGroup.size()>=getQuartz.size()) 
				{
					for (String jobname : lstGroup) 
					{	
						JobKey jobkey=JobKey.jobKey("ETLJOB", jobname);	
						TriggerKey triggerkey=TriggerKey.triggerKey(jobname, jobname);		
						JobDetail jd=QuartzUtil.getScheduler().getJobDetail(jobkey);	//jd==null说明排程不是ETL的			   

						boolean bExist=false;					   
						String status="";
						BigDecimal time=new BigDecimal(60000);

						String JOB_NAME="";
						String ETL_KETTLE="";
						String groupName = ""; 
						String JOB_TRIGGERNAME="";

						for (Map<String, Object> oneData: getQuartz) 
						{
							JOB_NAME=oneData.get("JOB_NAME").toString();
							ETL_KETTLE=oneData.get("ETL_KETTLE").toString();
							groupName = oneData.get("JOB_GROUPNAME").toString(); 
							JOB_TRIGGERNAME=oneData.get("JOB_TRIGGERNAME").toString();

							if (ETL_KETTLE.equals("Y")) 
							{
								if (jobname.equals(JOB_NAME)) 
								{
									bExist=true;
									status=oneData.get("STATUS").toString();
									time = (BigDecimal)oneData.get("JOB_TIME"); 
									break;
								}
							}
							else
							{
								if (jobname.equals(groupName)) 
								{
									bExist=true;
									status=oneData.get("STATUS").toString();
									time = (BigDecimal)oneData.get("JOB_TIME"); 
									break;
								}
							}						 
						}

						if (bExist) 
						{
							if (jd==null) 
							{
								jobkey=JobKey.jobKey(JOB_NAME, groupName);	
								triggerkey=TriggerKey.triggerKey(JOB_TRIGGERNAME, groupName);		
							}

							//看是否需要更改触发间隔时间或者暂停此排程						   
							CronTrigger croTrigger=(CronTrigger)QuartzUtil.getScheduler().getTrigger(triggerkey);
							String oldConExp=croTrigger.getCronExpression();	
							String cronExp = "";
							if(time.intValue()/1000 >= 60)
							{
								//分鐘
								//"0 0/2 * * * ?"
								cronExp = "0 0/" + time.intValue() / 1000 / 60 + " * * * ?";					
							} 
							else 
							{
								//秒
								//"0/3 * * * * ?"
								cronExp = "0/" + time.intValue() / 1000  + " * * * * ?";
							}
							if(JOB_NAME.equals("EveryDayCallSP"))
							{
								cronExp = "0 0 4 * * ?";
							}
							if(JOB_NAME.equals("DCP_ClearHisData"))
							{
								cronExp = "0 0 2 * * ?";
							}
							if (cronExp.equals(oldConExp)==false) 
							{
								//修改
								modifyJobTime("ETLJOB",jobname,jobname,cronExp);
							}

							TriggerState triggerState = QuartzUtil.getScheduler().getTriggerState(triggerkey);
							if (status.equals("100")||jobname.equals("RegularClearlogs")||jobname.equals("ZipLogFile")||jobname.equals("JOBTIMER")) 
							{ 							   
								if (triggerState.name().equals("PAUSED"))
								{
									//重启
									QuartzUtil.getScheduler().resumeJob(jobkey);
									QuartzUtil.getScheduler().resumeTrigger(triggerkey);
								}							  
							}			
							else
							{
								if (triggerState.name().equals("PAUSED")==false)
								{
									//暂停
									QuartzUtil.getScheduler().pauseJob(jobkey);		   
									QuartzUtil.getScheduler().pauseTrigger(triggerkey);
								}
							}
							
							//清理							
							triggerState=null;
							croTrigger=null;
							
						}
						else
						{						   
							//需要删除此排程
							if (jd!=null) 
							{							   
								removeJob("ETLJOB",jobname,jobname);
							}						   
						}	
						
						//清理						
						time=null;
						jd=null;
						triggerkey=null;
						jobkey=null;
						
					}   
				}
				else
				{
					for (Map<String, Object> oneData: getQuartz) 
					{
						String JOB_NAME=oneData.get("JOB_NAME").toString();
						String ETL_KETTLE=oneData.get("ETL_KETTLE").toString();
						String jobClass = oneData.get("JOB_CLASS").toString();					      
						String status=oneData.get("STATUS").toString();
						BigDecimal time=(BigDecimal)oneData.get("JOB_TIME"); 
						String groupName = oneData.get("JOB_GROUPNAME").toString(); 

						boolean bExist=false;					

						for (String jobname : lstGroup) 
						{						   
							if (ETL_KETTLE.equals("Y")) 
							{
								if (jobname.equals(JOB_NAME)) 
								{
									bExist=true;							  
									break;
								}			
							}
							else
							{
								if (jobname.equals(groupName)) 
								{
									bExist=true;							  
									break;
								}			
							}						  			   
						}					   


						TriggerState triggerState;
						TriggerKey triggerkey;
						JobKey jobkey;	
						if (ETL_KETTLE.equals("Y")) 
						{
							jobkey=JobKey.jobKey("ETLJOB", oneData.get("JOB_NAME").toString());	
							triggerkey=TriggerKey.triggerKey(oneData.get("JOB_NAME").toString(), oneData.get("JOB_NAME").toString());				   
						}
						else
						{
							jobkey=JobKey.jobKey("ETLJOB", oneData.get("JOB_GROUPNAME").toString());	
							triggerkey=TriggerKey.triggerKey(oneData.get("JOB_TRIGGERNAME").toString(), oneData.get("JOB_GROUPNAME").toString());	
						}
						triggerState = QuartzUtil.getScheduler().getTriggerState(triggerkey);		


						String cronExp = "";
						if(time.intValue()/1000 >= 60)
						{
							//分鐘
							//"0 0/2 * * * ?"
							cronExp = "0 0/" + time.intValue() / 1000 / 60 + " * * * ?";					
						} 
						else 
						{
							//秒
							//"0/3 * * * * ?"
							cronExp = "0/" + time.intValue() / 1000  + " * * * * ?";
						}
						if(JOB_NAME.equals("EveryDayCallSP"))
						{
							cronExp = "0 0 4 * * ?";
						}
						if(JOB_NAME.equals("DCP_ClearHisData"))
						{
							cronExp = "0 0 2 * * ?";
						}
						if (bExist) 
						{
							//看是否需要更改触发间隔时间或者暂停此排程						   
							CronTrigger croTrigger=(CronTrigger)QuartzUtil.getScheduler().getTrigger(triggerkey);
							String oldConExp=croTrigger.getCronExpression();							  

							if (cronExp.equals(oldConExp)==false) 
							{
								//修改
								if (ETL_KETTLE.equals("Y")) 
								{
									modifyJobTime("ETLJOB",oneData.get("JOB_NAME").toString(),oneData.get("JOB_NAME").toString(),cronExp);
								}
								else
								{								   
									modifyJobTime(oneData.get("JOB_NAME").toString(),oneData.get("JOB_GROUPNAME").toString(),oneData.get("JOB_TRIGGERNAME").toString(),cronExp);
								}							   
							}



							if (status.equals("100")||JOB_NAME.equals("RegularClearlogs")||JOB_NAME.equals("ZipLogFile")||JOB_NAME.equals("JOBTIMER")) 
							{ 							   
								if (triggerState.name().equals("PAUSED"))
								{
									//重启
									QuartzUtil.getScheduler().resumeJob(jobkey);
									QuartzUtil.getScheduler().resumeTrigger(triggerkey);
								}							  
							}			
							else
							{
								if (triggerState.name().equals("PAUSED")==false)
								{
									//暂停
									QuartzUtil.getScheduler().pauseJob(jobkey);		   
									QuartzUtil.getScheduler().pauseTrigger(triggerkey);
								}
							}
						}
						else
						{						
							//有效性
							if (status.equals("100")) 
							{
								//新增排程
								if (ETL_KETTLE.equals("Y")) 
								{							   
									addJob("ETLJOB",oneData.get("JOB_NAME").toString(),oneData.get("JOB_NAME").toString(),jobClass,cronExp);
								}
								else
								{
									addJob(oneData.get("JOB_NAME").toString(),oneData.get("JOB_GROUPNAME").toString(),oneData.get("JOB_TRIGGERNAME").toString(),jobClass,cronExp);
								}
							}						  
						}	


					}			   

				}
				
				//清理
				lstGroup.clear();
				lstGroup=null;
			}
			
			//清理
			getQuartz=null;

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

				logger.error("\r\n******排程监控JOBTIMER------》报错信息" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");

				pw=null;
				errors=null;
			} 
			catch (IOException e1) 
			{					
				logger.error("\r\n************排程监控JOBTIMER------》报错信息" + e.getMessage() + "******\r\n");
			}

			//
			sReturnInfo="错误信息:" + e.getMessage();
		}

		//
		return sReturnInfo;

	}





	/**  
	 * 功能：修改一个任务的触发时间 
	 * @param jobName  
	 * @param jobGroupName 
	 * @param triggerName 触发器名 
	 * @param cron   时间设置，参考quartz说明文档    
	 */    
	public void modifyJobTime(String jobName, String jobGroupName, String triggerName, String cron) 
	{    
		try 
		{      
			TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, jobGroupName);  
			CronTrigger trigger = (CronTrigger) QuartzUtil.getScheduler().getTrigger(triggerKey);    
			if (trigger == null) 
			{    
				return;    
			}    
			String oldTime = trigger.getCronExpression();    
			if (!oldTime.equalsIgnoreCase(cron)) 
			{   
				// 触发器    
				TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();  
				// 触发器名,触发器组    
				triggerBuilder.withIdentity(triggerName, jobGroupName);  
				triggerBuilder.startNow();  
				// 触发器时间设定    
				triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron));  
				// 创建Trigger对象  
				trigger = (CronTrigger) triggerBuilder.build();  
				// 方式一 ：修改一个任务的触发时间  
				QuartzUtil.getScheduler().rescheduleJob(triggerKey, trigger);
				
				triggerBuilder=null;
			}    
			
			//清理
			trigger=null;
			triggerKey=null;
		} 
		catch (Exception e) 
		{    
			loger.error("修改任务" +jobName + "报错:" + e.getMessage());
		}    
	}   


	/**  
	 * 功能: 移除一个任务  
	 * @param jobName  
	 * @param jobGroupName  
	 * @param triggerName  
	 */    
	public void removeJob(String jobName, String jobGroupName,String triggerName) 
	{    
		try 
		{    
			TriggerKey triggerKey = TriggerKey.triggerKey(triggerName,jobGroupName);  
			// 停止触发器    
			QuartzUtil.getScheduler().pauseTrigger(triggerKey);  
			// 移除触发器    
			QuartzUtil.getScheduler().unscheduleJob(triggerKey);  
			// 删除任务    
			QuartzUtil.getScheduler().deleteJob(JobKey.jobKey(jobName,jobGroupName));  

			triggerKey=null;
			
		} 
		catch (Exception e) 
		{    
			loger.error("删除任务" +jobName + "报错:" + e.getMessage());
		}    
	}    


	/** 
	 * @Description: 添加一个定时任务 
	 *  
	 * @param jobName 任务名 
	 * @param jobGroupName  任务组名 
	 * @param triggerName 触发器名 
	 * @param triggerGroupName 触发器组名 
	 * @param jobClass  任务 
	 * @param cron   时间设置，参考quartz说明文档  
	 */  
	@SuppressWarnings({ "unchecked", "rawtypes" })  
	public void addJob(String jobName, String jobGroupName,String triggerName, String jobClass, String cron) 
	{  
		try 
		{  
			Class job = Class.forName(jobClass);

			// 任务名，任务组，任务执行类
			JobDetail jobDetail= JobBuilder.newJob(job).withIdentity(jobName, jobGroupName).build();

			// 触发器  
			TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
			// 触发器名,触发器组  
			triggerBuilder.withIdentity(triggerName, jobGroupName);
			triggerBuilder.startNow();
			// 触发器时间设定  
			triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron));
			// 创建Trigger对象
			CronTrigger trigger = (CronTrigger) triggerBuilder.build();

			// 调度容器设置JobDetail和Trigger
			QuartzUtil.getScheduler().scheduleJob(jobDetail, trigger);  

			// 启动  
			if (!QuartzUtil.getScheduler().isShutdown()) 
			{  
				QuartzUtil.getScheduler().start();  
			}  
			
			//清理
			trigger=null;
			triggerBuilder=null;
			jobDetail=null;
			job=null;
			
		} 
		catch (Exception e) 
		{  
			loger.error("新增任务" +jobName + "报错:" + e.getMessage());
		}  
	}   



}
