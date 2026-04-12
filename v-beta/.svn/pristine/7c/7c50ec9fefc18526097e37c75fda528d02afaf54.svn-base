package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_JobSetUpdateReq;
import com.dsc.spos.json.cust.res.DCP_JobSetUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_JobSetUpdate extends SPosAdvanceService<DCP_JobSetUpdateReq, DCP_JobSetUpdateRes>
{

	@Override
	protected void processDUID(DCP_JobSetUpdateReq req, DCP_JobSetUpdateRes res) throws Exception 
	{
		String sql = null;
		sql = this.getCLASSFNO_SQL(req);

		List<Map<String, Object>> getQData_check = this.doQueryData(sql,null);		

		if(getQData_check==null || getQData_check.isEmpty())
		{			
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("找不到此JOB名称信息！");			
		}
		else
		{
			
			String jobclass="com.dsc.spos.scheduler.job." + req.getRequest().getJobName();
			String jobgroupname=req.getRequest().getJobName() + "_groupname";
			String jobtriggername=req.getRequest().getJobName() + "_triggername";
			if(req.getRequest().getKettle().equals("Y"))
			{
				jobclass="com.dsc.spos.scheduler.job.ETLJOB";
				jobgroupname="ETLJOB_groupname";
				jobtriggername="ETLJOB_triggername";				
			}
			
			
			//更新单头
			UptBean ub1 = new UptBean("JOB_QUARTZ");			
			ub1.addCondition("JOB_NAME",new DataValue(req.getRequest().getJobName(), Types.VARCHAR));
						
			ub1.addUpdateValue("JOB_CLASS",new DataValue(jobclass, Types.VARCHAR)); 
			ub1.addUpdateValue("JOB_GROUPNAME",new DataValue(jobgroupname, Types.VARCHAR)); 
			ub1.addUpdateValue("JOB_TRIGGERNAME",new DataValue(jobtriggername, Types.VARCHAR));
			ub1.addUpdateValue("JOB_TIME",new DataValue(req.getRequest().getJobTime(), Types.INTEGER));
			ub1.addUpdateValue("JOB_TYPE",new DataValue(0, Types.INTEGER));
			ub1.addUpdateValue("STATUS",new DataValue(req.getRequest().getStatus(), Types.VARCHAR));
			ub1.addUpdateValue("JOB_DISCRETION",new DataValue(req.getRequest().getJobDescription(), Types.VARCHAR));
			ub1.addUpdateValue("ETL_KETTLE",new DataValue(req.getRequest().getKettle(), Types.VARCHAR));
			ub1.addUpdateValue("JOB_FOLDER",new DataValue(req.getRequest().getKettleFolder(), Types.VARCHAR));			
			ub1.addUpdateValue("MAINTYPE",new DataValue(req.getRequest().getMainType(), Types.VARCHAR));
			ub1.addUpdateValue("ONSALE",new DataValue(req.getRequest().getOnSale(), Types.VARCHAR));
			ub1.addUpdateValue("INIT_JOB_TIME",new DataValue(req.getRequest().getInitJobTime(), Types.VARCHAR));
			ub1.addUpdateValue("INIT_CNFFLG",new DataValue(req.getRequest().getInitCnfflg(), Types.VARCHAR));

			this.addProcessData(new DataProcessBean(ub1));		
			
			
			this.doExecuteDataToDB();

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");			
		}
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_JobSetUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_JobSetUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_JobSetUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_JobSetUpdateReq req) throws Exception 
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
	protected TypeToken<DCP_JobSetUpdateReq> getRequestType() 
	{
		return new TypeToken<DCP_JobSetUpdateReq>(){};
	}

	@Override
	protected DCP_JobSetUpdateRes getResponseType() 
	{
		return new DCP_JobSetUpdateRes();
	}
	
	protected String getCLASSFNO_SQL(DCP_JobSetUpdateReq req)
	{		
		String sql = null;

		StringBuffer sqlbuf = new StringBuffer("");

		sqlbuf.append("SELECT * FROM JOB_QUARTZ WHERE JOB_NAME='"+req.getRequest().getJobName()+"' ");

		sql = sqlbuf.toString();

		return sql;	
	}
	
	

}
