package com.dsc.spos.scheduler.util;

import com.dsc.spos.scheduler.eventlistener.QuartzJobListener;
import com.dsc.spos.scheduler.eventlistener.QuartzSchedulerListener;
import com.dsc.spos.scheduler.eventlistener.QuartzTriggerListener;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Properties;


public class QuartzUtil {

	static Properties properties;
	static Scheduler sched;
	static SchedulerFactory sf;
	public static String QUARTZ_PARAM = "quartzParam";
	private static SchedulerFactory schedulerFactory = new org.quartz.impl.StdSchedulerFactory();

	// static QuartzService quartzService =
	// (QuartzService)StringUtils.getBean("quartzService");

	/**
	 * 初始化Schedule
	 * 
	 * @throws SchedulerException
	 */
	public static void initSchedule() throws SchedulerException {
		if (sched == null) {
			sched = schedulerFactory.getScheduler();
			// properties = initProperties();
			// sf = new StdSchedulerFactory(properties);
			// sched = sf.getScheduler();
		}
	}

	/**
	 * 檢查是否存在Schedule
	 * 
	 * @throws SchedulerException
	 */
	public static boolean isExistsSchedule() {
		if (sched == null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 啟動Schedule
	 * 
	 * @throws SchedulerException
	 */
	public static void doStartSchedule() throws SchedulerException {
		if (sched != null) {
			sched.start();
		}
	}

	/**
	 * 檢查是否啟動Schedule
	 * 
	 * @throws SchedulerException
	 */
	public static boolean doIsStarted() {
		try {
			if (sched.isStarted()) {
				return true;
				
			} else {
				return false;
			}
		} catch (SchedulerException e) 
		{

			return false;
		}
	}
	/**
	 * 添加QuartzJobListener及QuartzTriggerListener
	 * 
	 * @throws SchedulerException
	 */
	public static void doAddListenerToSchedule() throws SchedulerException {
		if (sched != null) {
			sched.getListenerManager().addJobListener(new QuartzJobListener());
			sched.getListenerManager().addTriggerListener(new QuartzTriggerListener());
			sched.getListenerManager().addSchedulerListener(new QuartzSchedulerListener());
		}
	}

	/**
	 * 取得Schedule
	 * 
	 * @throws SchedulerException
	 * @returnScheduler
	 */
	public static Scheduler getScheduler() throws SchedulerException {
		if (sched != null) {
			return sched;
		} else {
			initSchedule();
			return sched;
		}
	}

	/**
	 * 檢查資料庫內排程內容 依據policyType處理當前資料庫內已存在的排程作業 將所有狀態為S的工作轉換為E
	 * 0為啟動後排程所有JOB且錯過的工作依據misfirePolicy執行 1為清除所有JOB
	 * 
	 * @throws Exception
	 * @throwsException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void doCheckScheduler(int policyType) throws Exception {
		// List<B2BJobScheduleDetail> detailList
		// =quartzService.getPauseJobScheduleDetail();
		// List newDetailList=new ArrayList();
		// for(B2BJobScheduleDetail detail:detailList){
		// detail.setExeStatus("E");
		// newDetailList.add(detail);
		// }
		// quartzService.doUpdate(newDetailList);
		// switch(policyType){
		// case 0:
		// break;
		// case 1:
		// sched.clear();
		// break;
		// default:
		//
		// }
	}

	/**
	 * 暫停groupName底下所有工作
	 * 
	 * @throws SchedulerException
	 * @paramgroupName
	 */
	public static void pauseGroup(String groupName) throws SchedulerException {
		if (sched.getJobKeys(GroupMatcher.jobGroupEquals(groupName)).size() > 0) {
			for (JobKey jobKey : sched.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
				sched.pauseJob(jobKey);
			}
		}
	}

	/**
	 * 開始groupName底下所有工作
	 * 
	 * @throws SchedulerException
	 * @paramgroupName
	 */
	public static void resumeGroup(String groupName) throws SchedulerException {
		if (sched.getJobKeys(GroupMatcher.jobGroupEquals(groupName)).size() > 0) {
			for (JobKey jobKey : sched.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
				sched.resumeJob(jobKey);
			}
		}
	}

	/**
	 * 關閉Schedule
	 * 
	 * @throws SchedulerException
	 */
	public static void shutdownScheduler() throws SchedulerException {
		if (sched != null) {
			sched.shutdown();
		}
	}

	/**
	 * 移除排程作業
	 * 
	 * @param groupName
	 * @throws SchedulerException
	 */
	public static boolean removeGroup(String groupName) throws SchedulerException {
		boolean result = false;
		if (sched.getJobKeys(GroupMatcher.jobGroupEquals(groupName)).size() > 0) {
			for (JobKey jobKey : sched.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
				if (sched.deleteJob(jobKey)) {
					result = true;
				} else {
					result = false;
				}
			}
		} else {
			result = true;
		}
		return result;
	}

	/**
	 * 移除Job
	 * 
	 * @param jobKey
	 * @throws SchedulerException
	 */
	public static boolean removeJob(JobKey jobKey) throws SchedulerException {
		boolean result = false;
		if (sched.checkExists(jobKey)) {
			result = sched.deleteJob(jobKey);
		}
		return result;
	}

    /**
     * @Description 重置一个任务(使用默认的任务组名，触发器名，触发器组名)
     * @param jobName
     */
    public static void resetJob(String triggerName, String groupName, String jobClass, String jobName,
								String cronExp, int misFirePolicy, JobDataMap map) throws Exception
    {
		Class job = Class.forName(jobClass);
		JobDetail jobDetail = createJob(jobName, groupName, job, map);
		Trigger trigger = createTrigger(triggerName, groupName, cronExp, misFirePolicy);
		if (checkExists(jobDetail)) {
			// 已經存在
			sched.pauseTrigger(trigger.getKey());;// 停止触发器
			sched.unscheduleJob(trigger.getKey());// 移除触发器
			sched.deleteJob(jobDetail.getKey());// 删除任务
		}
		sched.scheduleJob(jobDetail, trigger);
    }

	/**
	 * 設定排程作業重新執行
	 * 
	 * @param jobScheduleDetailCode
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static boolean doResetJob(String jobScheduleDetailCode, JobDataMap map) throws Exception {
		// B2BJobScheduleDetail detail
		// =quartzService.getJobScheduleDetail(jobScheduleDetailCode);
		// B2BJob job = quartzService.getJob(detail.getJobCode());
		// Class jobClass =Class.forName(job.getJobClass());
		// JobDetail jobDetail =
		// createJob("reset"+jobClass.getName()+quartzService.getNextTriggerNo(),
		// detail.getJobScheduleCode(), jobClass,map);
		// Trigger trigger = TriggerBuilder.newTrigger()
		// .withIdentity("reset"+jobClass.getName()+quartzService.getNextTriggerNo(),
		// detail.getJobScheduleCode())
		// .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
		// .build();
		// sched.scheduleJob(jobDetail,trigger);
		return true;
	}

	/**
	 * 設定排程作業 且於前景執行
	 * 
	 * @param groupName
	 * @param jobClass
	 * @param map
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static boolean doJobNow(String groupName, String jobClass, JobDataMap map) throws Exception {
		Class job = Class.forName(jobClass);
		JobDetail jobDetail = createJob(job.getName(), groupName, job, map);
		sched.addJob(jobDetail, true, true);
		sched.triggerJob(jobDetail.getKey());
		while (sched.checkExists(jobDetail.getKey())) {
		}
		return true;
	}

	/**
	 * 設定排程作業 且於背景執行
	 * 
	 * @param groupName
	 * @param jobClass
	 * @param map
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static boolean doJobAtBackground(String groupName, String jobClass, JobDataMap map) throws Exception {
		Class job = Class.forName(jobClass);
		JobDetail jobDetail = createJob(job.getName(), groupName, job, map);
		if (!sched.checkExists(jobDetail.getKey())) {
			sched.addJob(jobDetail, true, true);
		}
		sched.triggerJob(jobDetail.getKey());
		return true;
	}

	/**
	 * 設定排程作業 且於指定時間執行
	 * 
	 * @param triggerName
	 * @param groupName
	 * @param jobClass
	 * @param specificDate
	 * @param map
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("rawtypes")
	public static boolean doJobAtSpecificDate(String triggerName, String groupName, String jobClass, Date specificDate,
			JobDataMap map) throws Exception {
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerName, groupName).startAt(specificDate)
				.build();
		Class job = Class.forName(jobClass);
		JobDetail jobDetail = createJob(job.getName(), groupName, job, map);
		sched.scheduleJob(jobDetail, trigger);
		return true;
	}

	/**
	 * 設定排程作業 透過給予的cronExpression建立排程工作
	 * 
	 * @param triggerName
	 * @param groupName
	 * @param jobClass
	 * @param jobName
	 * @param cronExp
	 *            用來產生排程的cronExpression
	 * @param misFirePolicy
	 *            設定misfire的策略
	 * @param map
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static boolean addSchedule(String triggerName, String groupName, String jobClass, String jobName,
			String cronExp, int misFirePolicy, JobDataMap map) throws Exception {
		Class job = Class.forName(jobClass);
		JobDetail jobDetail = createJob(jobName, groupName, job, map);
		Trigger trigger = createTrigger(triggerName, groupName, cronExp, misFirePolicy);
		if (checkExists(jobDetail)) {
			// 已經存在
		} else {
			sched.scheduleJob(jobDetail, trigger);
		}
		return true;
	}

	/**
	 * 設定排程作業 透過給予的cronExpression建立simpleTrigger達成有日期且有時間區間的排程工作
	 * 
	 * @param triggerName
	 * @param groupName
	 * @param jobClass
	 * @param jobName
	 * @param cronExp
	 *            用來產生排程的cronExpression
	 * @param misFirePolicy
	 *            設定misfire的策略
	 * @param map
	 *            用來儲存傳送到Simpletrigger去的資料 map內容應包含: startTime endTime
	 *            internalTime triggerName
	 *            =jobSchedule.getJobScheduleCode()+i(i為時間區間的分別) jobName
	 *            =jobSchedule.getJobScheduleCode()+i(i為時間區間的分別) triggerGroup
	 *            =jobSchedule.getJobCode() jobClass
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static boolean addMixSchedule(String triggerName, String groupName, String jobClass, String jobName,
			String cronExp, int misFirePolicy, JobDataMap map) throws Exception {
		Class job = Class.forName(jobClass);
		JobDetail jobDetail = createJob(jobName, groupName, job, map);
		Trigger trigger = createTrigger(triggerName, groupName, cronExp, misFirePolicy);
		sched.scheduleJob(jobDetail, trigger);
		return true;
	}

	/**
	 * 透過groupName搜尋B2BJobScheduleState的資料，比對是否需寄信給當前傳入的state
	 * 
	 * @param groupName
	 * @param status
	 */
	public static void sendEmail(String groupName, String status) {
		// List<B2BJobScheduleState> stateList =
		// quartzService.getJobScheduleState(groupName);
		// if(stateList!=null){
		// if(stateList.size()>0){
		// for(int i =0;i<stateList.size();i++){
		// B2BJobScheduleState state =stateList.get(i);
		// if(status.equals(state.getId().getExecuteState())){
		// if(state.getReceiverEmailAddress()!=null){
		// EmailUtil.sendEmail(Constants.getProperty("quartz.sendMailFrom"),
		// state.getReceiverEmailAddress(),
		// state.getMailSubject(), state.getMailContent());
		// }
		// }
		// }
		// }
		// }
	}

	/**
	 * 設定Quartz的properties
	 */
	private static Properties initProperties() {
		// Properties properties = new Properties();
		// properties.put("org.quartz.scheduler.instanceName", "MyScheduler01");
		// properties.put("org.quartz.scheduler.instanceId", "AUTO");
		// properties.put("org.quartz.scheduler.skipUpdateCheck", "true");
		// properties.put("org.quartz.threadPool.class",
		// "org.quartz.simpl.SimpleThreadPool");
		// properties.put("org.quartz.threadPool.threadCount", "2");
		// properties.put("org.quartz.threadPool.threadPriority", "5");
		// properties.put("org.quartz.jobStore.misfireThreshold","1000" );
		// properties.put("org.quartz.jobStore.class",
		// "org.quartz.impl.jdbcjobstore.JobStoreTX");
		// properties.put("org.quartz.jobStore.driverDelegateClass",
		// "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
		// properties.put("org.quartz.jobStore.useProperties", "true");
		// properties.put("org.quartz.jobStore.dataSource","myDS" );
		// properties.put("org.quartz.jobStore.tablePrefix", "QRTZ_");
		// properties.put("org.quartz.jobStore.isClustered", "false");
		// properties.put("org.quartz.dataSource.myDS.driver","oracle.jdbc.driver.OracleDriver");
		// properties.put("org.quartz.dataSource.myDS.URL","jdbc:oracle:thin:@10.40.41.217:1521:orcl");
		// properties.put("org.quartz.dataSource.myDS.user","B2B_DEV");
		// properties.put("org.quartz.dataSource.myDS.password","b2bdev");
		// properties.put("org.quartz.dataSource.myDS.maxConnections","5");

		Properties properties = new Properties();
		// String instanceNameSP =
		// SystemPropertiesUtil.getString("quartz.instanceName");
		// if(StringUtils.isBlank(instanceNameSP)){
		// instanceNameSP = "MyScheduler01";
		// }
		//
		// properties.put("org.quartz.scheduler.instanceName", instanceNameSP);
		// properties.put("org.quartz.scheduler.instanceId", "AUTO");
		// properties.put("org.quartz.scheduler.skipUpdateCheck", "true");
		// properties.put("org.quartz.threadPool.class",
		// "org.quartz.simpl.SimpleThreadPool");
		// properties.put("org.quartz.threadPool.threadCount",
		// Constants.getProperty("threadPool.threadCount"));
		// properties.put("org.quartz.threadPool.threadPriority", "5");
		// properties.put("org.quartz.jobStore.misfireThreshold",
		// Constants.getProperty("jobStore.misfireThreshold"));
		// properties.put("org.quartz.jobStore.class",
		// "org.quartz.impl.jdbcjobstore.JobStoreTX");
		// properties.put("org.quartz.jobStore.driverDelegateClass",
		// "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
		// properties.put("org.quartz.jobStore.useProperties", "true");
		// properties.put("org.quartz.jobStore.dataSource",
		// Constants.getProperty("jobStore.dataSource"));
		// properties.put("org.quartz.jobStore.tablePrefix", "QRTZ_");
		// properties.put("org.quartz.jobStore.isClustered", "false");
		// properties.put("org.quartz.dataSource.myDS.driver",Constants.getProperty("myDS.driver"));
		// properties.put("org.quartz.dataSource.myDS.URL",Constants.getProperty("myDS.URL"));
		// properties.put("org.quartz.dataSource.myDS.user",Constants.getProperty("myDS.user"));
		// properties.put("org.quartz.dataSource.myDS.password",Constants.getProperty("myDS.password"));
		// properties.put("org.quartz.dataSource.myDS.maxConnections",
		// Constants.getProperty("myDS.maxConnections"));
		return properties;
	}

	/**
	 * 創建JobDetail
	 * 
	 * @param jobName
	 * @param jobGroup
	 * @param jobClass
	 * @param jobDataMap(必須有管理欄位的資訊)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static JobDetail createJob(String jobName, String jobGroup, Object jobClass, JobDataMap jobDataMap) {
		JobDetail job = null;
		if (jobDataMap == null) {
			job = JobBuilder.newJob((Class) jobClass).withIdentity(jobName, jobGroup).build();
		} else {
			job = JobBuilder.newJob((Class) jobClass).withIdentity(jobName, jobGroup).setJobData(jobDataMap).build();
		}
		return job;
	}

	/**
	 * 創建Trigger
	 * 
	 * @param triggerName
	 * @param triggerGroup
	 * @param cronExpression
	 * @param misfirePolicy(misfire的策略
	 *            使用CronTrigger.MISFIRE_INSTRUCTION_SMART_POLICY之類的設定)
	 */
	public static Trigger createTrigger(String triggerName, String triggerGroup, String cronExpression,
			int misfirePolicy) {
		Trigger trigger;
		switch (misfirePolicy) {
		case CronTrigger.MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY:
			trigger = TriggerBuilder.newTrigger().withIdentity(triggerName, triggerGroup).withSchedule(
					CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionIgnoreMisfires())
					.build();
			break;
		case CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING:
			trigger = TriggerBuilder.newTrigger().withIdentity(triggerName, triggerGroup)
					.withSchedule(
							CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionDoNothing())
					.build();
			break;
		case CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW:
			trigger = TriggerBuilder.newTrigger().withIdentity(triggerName, triggerGroup).withSchedule(
					CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionFireAndProceed())
					.build();
			break;
		case CronTrigger.MISFIRE_INSTRUCTION_SMART_POLICY:
			trigger = TriggerBuilder.newTrigger().withIdentity(triggerName, triggerGroup)
					.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).build();
			break;
		default:
			trigger = TriggerBuilder.newTrigger().withIdentity(triggerName, triggerGroup)
					.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).build();
			break;
		}
		return trigger;
	}

	public static boolean checkExists(JobDetail jobDetail) throws Exception {
		// return sched.checkExists(jobDetail.getKey());
		boolean result = false;
		if (sched.checkExists(jobDetail.getKey())) {
			result = true;
		}
		return result;
	}

	/**
	 * 仅一次
	 * @param dateStr
	 * @return
	 */
	public static String onlyOnce(String dateStr){
		LocalDateTime time = LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		String cronStr = time.format(DateTimeFormatter.ofPattern("ss mm HH dd MM ? yyyy"));
		return cronStr;
	}
}
