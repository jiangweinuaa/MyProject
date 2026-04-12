package com.dsc.spos.scheduler.job;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.ProcedureBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;

//********************自动查询加盟店信用信用充值付款结果查询及产生调用ERP增加信用额度**************************
//***********************************************************
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class EveryDayCallSP extends InitJob
{
	Logger logger = LogManager.getLogger(EveryDayCallSP.class.getName());

	static boolean bRun=false;

	public String doExe() 
	{
		//此服务是否正在执行中
		//返回信息
		String sReturnInfo="";
		if (bRun )
		{		
			logger.info("\r\n*********EveryDayCallSP正在执行中,本次调用取消:************\r\n");
			sReturnInfo="EveryDayCallSP正在执行中！";
			return sReturnInfo;
		}
		bRun=true;//			
		logger.info("\r\n*********EveryDayCallSP定时调用Start:************\r\n");
		try 
		{
			List<DataProcessBean> data = new ArrayList<>();
			String procedure="SP_DO_EVERYDAY";
			Map<Integer,Object> inputParameter = new HashMap<Integer, Object>();
			String sql="select DISTINCT EID from DCP_ORG WHERE STATUS=100  ";
			List<Map<String, Object>> list=this.doQueryData(sql, null);
			if(list!=null&&!list.isEmpty())
			{
				for (Map<String, Object> map: list) 
				{
					String eid     =map.get("EID").toString();
					inputParameter.put(1,eid);                                       
					ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
					data.add(new DataProcessBean(pdb));
					StaticInfo.dao.useTransactionProcessData(data);
				}
			}
		}catch(Exception e)
		{
			logger.info("\r\n******EveryDayCallSP报错信息" + e.getMessage()+"\r\n******\r\n");
			sReturnInfo="错误信息:" + e.getMessage();
		}
		finally 
		{
			bRun=false;//
			logger.info("\r\n*********EveryDayCallSP定时调用End:************\r\n");
		}		
		return sReturnInfo;
	}
}
