package com.dsc.spos.utils;

import java.io.IOException;
import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

public class CPUMonitorCalc 
{
	private static CPUMonitorCalc instance = new CPUMonitorCalc();

	private OperatingSystemMXBean osMxBean;
	private ThreadMXBean threadBean;
	private long preTime = System.nanoTime();
	private long preUsedTime = 0;

	public CPUMonitorCalc() 
	{
		osMxBean = ManagementFactory.getOperatingSystemMXBean();
		threadBean = ManagementFactory.getThreadMXBean();
	}

	public static CPUMonitorCalc getInstance() 
	{
		return instance;
	}


	public int getProcessCpu() 
	{		
		try 
		{
			long totalTime = 0;
			long[] allThreadIDS= threadBean.getAllThreadIds();

			for (long id : allThreadIDS) 
			{
				totalTime += threadBean.getThreadCpuTime(id);

				if (allThreadIDS.length>70)// 
				{
					ThreadInfo threadInfo = threadBean.getThreadInfo(id, Integer.MAX_VALUE);  
					if (threadInfo != null) 
					{  
						StringBuffer sb = new StringBuffer("");
						sb.append("blockcount=" + threadInfo.getBlockedCount() + ",blocktime=" + threadInfo.getBlockedTime());
						sb.append(",waitedcount=" + threadInfo.getWaitedCount() + ",waitedtime=" + threadInfo.getWaitedTime());

						PosPub.WriteETLJOBLog(sb.toString());

						sb.setLength(0);
						sb=null;

						PosPub.WriteETLJOBLog(getThreadInfo(threadInfo));  
					}
				}

			}

			//
			long[] deadlock_ids = threadBean.findDeadlockedThreads();  
			if (deadlock_ids != null) 
			{  
				PosPub.WriteETLJOBLog("####################死锁信息####################");  
				for (long id : deadlock_ids) 
				{  
					PosPub.WriteETLJOBLog("死锁的线程号：" + id);  
				}  
			} 
			deadlock_ids=null;

			long curtime = System.nanoTime();
			long usedTime = totalTime - preUsedTime;
			long totalPassedTime = curtime - preTime;
			preTime = curtime;
			preUsedTime = totalTime;
			Double dbValue=(((double) usedTime) / totalPassedTime / osMxBean.getAvailableProcessors()) * 100;
			//return (((double) usedTime) / totalPassedTime / osMxBean.getAvailableProcessors()) * 100;
			int iPer=dbValue.intValue();
			if (iPer>100 || iPer<0) 
			{
				iPer=0;
			}        

			PosPub.WriteETLJOBLog("进程数：" +allThreadIDS.length+",CPU："  +iPer +"%");			

			allThreadIDS=null;

			return iPer;
		} 
		catch (IOException e) 
		{
			return 0;
		}


		//return "进程数：" +allThreadIDS.length+",CPU："  +iPer +"" +"%";
	}

	private String getThreadInfo(ThreadInfo t) 
	{    	  
		try 
		{  
			StringBuffer sb = new StringBuffer("\"" + t.getThreadName() + "\"" + " Id=" + t.getThreadId() + " " + t.getThreadState());  
			if (t.getLockName() != null) 
			{  
				sb.append(" on " + t.getLockName());  
			}  
			if (t.getLockOwnerName() != null) 
			{  
				sb.append(" owned by \"" + t.getLockOwnerName() + "\" Id=" + t.getLockOwnerId());  
			}  
			if (t.isSuspended()) {  
				sb.append(" (suspended)");  
			}  
			if (t.isInNative()) 
			{  
				sb.append(" (in native)");  
			}  
			sb.append('\n');  
			int i = 0;  
			for (StackTraceElement ste : t.getStackTrace()) 
			{  
				sb.append("\tat " + ste.toString());  
				sb.append('\n');  
				if (i == 0 && t.getLockInfo() != null) 
				{  
					Thread.State ts = t.getThreadState();  
					switch (ts) 
					{  
					case BLOCKED:  
						sb.append("\t-  blocked on " + t.getLockInfo());  
						sb.append('\n');  
						break;  
					case WAITING:  
						sb.append("\t-  waiting on " + t.getLockInfo());  
						sb.append('\n');  
						break;  
					case TIMED_WAITING:  
						sb.append("\t-  waiting on " + t.getLockInfo());  
						sb.append('\n');  
						break;  
					default:  
					}  
				}  

				for (MonitorInfo mi : t.getLockedMonitors()) 
				{  
					if (mi.getLockedStackDepth() == i) 
					{  
						sb.append("\t-  locked " + mi);  
						sb.append('\n');  
					}  
				}  
			}  
			if (i < t.getStackTrace().length) 
			{  
				sb.append("\t...");  
				sb.append('\n');  
			}  

			LockInfo[] locks = t.getLockedSynchronizers();  
			if (locks.length > 0) 
			{  
				sb.append("\n\tNumber of locked synchronizers = " + locks.length);  
				sb.append('\n');  
				for (LockInfo li : locks) 
				{  
					sb.append("\t- " + li);  
					sb.append('\n');  
				}  
			}  
			sb.append('\n');  

			String res=sb.toString();
			sb.setLength(0);
			sb=null;

			return res;  
		} 
		catch (Exception e) 
		{  
			e.printStackTrace();  
		}  
		return "";  
	} 


}
