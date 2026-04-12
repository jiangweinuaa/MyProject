package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_JobSetCreateReq;
import com.dsc.spos.json.cust.res.DCP_JobSetCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_JobSetCreate extends SPosAdvanceService<DCP_JobSetCreateReq, DCP_JobSetCreateRes>
{

	@Override
	protected void processDUID(DCP_JobSetCreateReq req, DCP_JobSetCreateRes res) throws Exception 
	{
		String sql = null;
		sql = this.getCLASSFNO_SQL(req);

		List<Map<String, Object>> getQData_check = this.doQueryData(sql,null);		

		if(getQData_check==null || getQData_check.isEmpty())
		{

			//JOB_QUARTZ
			String[] columnsModular = 
				{
						"JOB_NAME",
						"JOB_CLASS",
						"JOB_GROUPNAME",
						"JOB_TRIGGERNAME",
						"JOB_TIME",
						"JOB_TYPE",
						"STATUS",
						"JOB_DISCRETION",
						"ETL_KETTLE",
						"JOB_FOLDER",
						"MAINTYPE",
						"ONSALE",
						"INIT_JOB_TIME",
						"INIT_CNFFLG"
				};

			String jobclass="com.dsc.spos.scheduler.job." + req.getRequest().getJobName();
			String jobgroupname=req.getRequest().getJobName() + "_groupname";
			String jobtriggername=req.getRequest().getJobName() + "_triggername";
			
			if(req.getRequest().getKettle().equals("Y"))
			{
				jobclass="com.dsc.spos.scheduler.job.ETLJOB";
				jobgroupname="ETLJOB_groupname";
				jobtriggername="ETLJOB_triggername";				
			}
			
			DataValue[] insValue1 = null;

			insValue1 = new DataValue[] 
					{ 
							new DataValue(req.getRequest().getJobName(), Types.VARCHAR),
							new DataValue(jobclass, Types.VARCHAR), 
							new DataValue(jobgroupname, Types.VARCHAR),
							new DataValue(jobtriggername, Types.VARCHAR),
							new DataValue(req.getRequest().getJobTime(), Types.INTEGER), 
							new DataValue(0, Types.INTEGER), 
							new DataValue(req.getRequest().getStatus(), Types.VARCHAR), 
							new DataValue(req.getRequest().getJobDescription(), Types.VARCHAR), 
							new DataValue(req.getRequest().getKettle(), Types.VARCHAR), 
							new DataValue(req.getRequest().getKettleFolder(), Types.VARCHAR),
							new DataValue(req.getRequest().getMainType(), Types.VARCHAR),
							new DataValue(req.getRequest().getOnSale(),Types.VARCHAR),
							new DataValue(req.getRequest().getInitJobTime(),Types.VARCHAR),
							new DataValue(req.getRequest().getInitCnfflg(),Types.VARCHAR),
					};

			InsBean ib1 = new InsBean("JOB_QUARTZ", columnsModular);
			ib1.addValues(insValue1);
			this.addProcessData(new DataProcessBean(ib1)); // 新增單頭

			this.doExecuteDataToDB();

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");			
		}
		else
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("JOB名称已经存在！");			
		}		
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_JobSetCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_JobSetCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_JobSetCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_JobSetCreateReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		String JobName =req.getRequest().getJobName();
		String JobDescription =req.getRequest().getJobDescription();
		String JobTime=req.getRequest().getJobTime();
		String status =req.getRequest().getStatus();
		String Kettle =req.getRequest().getKettle();
		String KettleFolder =req.getRequest().getKettleFolder();
		String mainType =req.getRequest().getMainType();

		if (Check.Null(JobName)) 
		{
			errMsg.append("JOB名称不可为空值, ");
			isFail = true;
		} 

		if (Check.Null(JobDescription)) 
		{
			errMsg.append("JOB描述不可为空值, ");
			isFail = true;
		} 
		
		if (Check.Null(JobTime)) 
		{
			errMsg.append("轮询时间不可为空值, ");
			isFail = true;
		} 

		if (Check.Null(status)) 
		{
			errMsg.append("启用状态不可为空值, ");
			isFail = true;
		}
		
		if (Check.Null(Kettle)) 
		{
			errMsg.append("Kettle任务不可为空值, ");
			isFail = true;
		}
		
		if(Kettle.equals("Y"))
		{			
			if (Check.Null(KettleFolder)) 
			{
				errMsg.append("脚本相对路径不可为空值, ");
				isFail = true;
			}
		}		
		if (Check.Null(mainType)) 
		{
			errMsg.append("JOB类型不可为空值, ");
			isFail = true;
		} 
		

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_JobSetCreateReq> getRequestType() 
	{
		return new TypeToken<DCP_JobSetCreateReq>(){};
	}

	@Override
	protected DCP_JobSetCreateRes getResponseType() 
	{
		return new DCP_JobSetCreateRes();
	}

	protected String getCLASSFNO_SQL(DCP_JobSetCreateReq req)
	{		
		String sql = null;

		StringBuffer sqlbuf = new StringBuffer("");

		sqlbuf.append("SELECT * FROM JOB_QUARTZ WHERE JOB_NAME='"+req.getRequest().getJobName()+"' ");

		sql = sqlbuf.toString();

		return sql;	
	}
	
	
	
	
}
