package com.dsc.spos.scheduler.job;

import java.io.PrintWriter;
import java.io.StringWriter;

import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.InterruptableJob;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import com.dsc.spos.dao.DataValue;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class InitJob  implements Job,InterruptableJob 
{ 
	Logger loger = LogManager.getLogger(InitJob.class.getName());
	
	
	public void execute(JobExecutionContext context) throws JobExecutionException  
	{
		
		try 
		{
			//context.getJobDetail().getJobDataMap().put("threadName", Thread.currentThread().getName());
			//JobDetail JB= context.getJobDetail();				
			
			if (context.getJobDetail().getJobDataMap().size()>0) 
			{		
				doExe(context.getJobDetail().getJobDataMap());				
			}
			else
			{				
				loger.info("**********InitJob START*****************");

				doExe();

				loger.info("**********InitJob END*****************");
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
				
				loger.error("InitJob error StackTrace: " + errors.toString());
				
				pw=null;
				errors=null;
			} 
			catch (Exception e2) 
			{
				loger.error("InitJob error StackTrace2: " + e2.toString());
			}			
			
		}
	}

	/**
	 * 複寫此方法完成JOB該做的事情
	 * @throws Exception 
	 */
	public String doExe() throws Exception 
	{
		return null;		 
	}
	
	/**
	 * 複寫此方法完成JOB該做的事情
	 */
	public String doExe(Object param) throws Exception 
	{
		return null;
	}
	
	@Override
	public void interrupt() 
	{
		//System.out.println("Job1 was interrupted! Time is " + new Date());
	}
	
	protected List<Map<String, Object>> doQueryData(String sql, String[] conditionValues) throws Exception 
	{
		return StaticInfo.dao.executeQuerySQL(sql, conditionValues);	
	}
	
	protected boolean doUpdate(String tableName, Map<String, DataValue> values, Map<String, DataValue> conditions) throws Exception 
	{
		return StaticInfo.dao.update(tableName, values, conditions);
	}	
	
	protected int doInsert(String tableName, String[] columns, DataValue[] values) throws Exception 
	{
		return StaticInfo.dao.insert(tableName, columns, values);
	}

    protected List<Map<String, Object>> executeQuerySQL_BindSQL(String sql, final List<DataValue> values) throws Exception
    {
        return StaticInfo.dao.executeQuerySQL_BindSQL(sql,values);
    }

}
