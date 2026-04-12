package com.dsc.spos.scheduler.job;

import java.io.IOException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import com.dsc.spos.dao.DataValue;
import com.dsc.spos.waimai.HelpTools;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class RegisterAuto extends InitJob
{
	Logger logger = LogManager.getLogger(RegisterAuto.class.getName());

	String jddjLogFileName = "RegisterAuto";

	static boolean bRun=false;//标记此服务是否正在执行中

	public String doExe() throws IOException 
	{
		//返回信
		String sReturnInfo="";
		//此服务是否正在执行中
		if (bRun )
		{		
			logger.info("\r\n*********自动注册服务正在执行中************\r\n");

			sReturnInfo="自动注册服务正在执行中！";
			return sReturnInfo;
		}

		bRun=true;//			

		logger.info("\r\n*********自动注册服务RegisterAuto定时调用Start:************\r\n");

		HelpTools.writelog_fileName("自动注册服务RegisterAuto定时调用Start " ,jddjLogFileName);
		//查找订单表，如果已经下单通知物流接口了的，并且已经取货了的，直接通知第三方,目前主要用于通知舞像以及味多美官网
		try {
			//加入一段反注册的功能
			//查找需要一下有多少需要注册并且是可以注册的
			String sdate=new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());

			String sqldelet= "select A.*,rownum ARN from platform_cregisterdetail A " + 
					"left join  DCP_ORG B on A.EID=B.EID and A.SHOPID=B.Organizationno " + 
					"where  A.Producttype='1'  and A.EID='10' and  A.SHOPID is not null and  (B.Organizationno is null or B.STATUS='0'  "
					+ "  or A.bdate > '"+sdate+"' or '"+sdate+"' > A.EDATE ) " +  
					"order by A.EID,A.SHOPID " ;

			List<Map<String, Object>> listdorderdetail=this.doQueryData(sqldelet, null);
			if(listdorderdetail!=null&&!listdorderdetail.isEmpty())
			{
				//开始组JSON
				for (Map<String, Object> map : listdorderdetail) 
				{
					//成功需要更新一下订单的物流回写状态
					Map<String, DataValue> values = new HashMap<String, DataValue>();				  	
					DataValue v1 = new DataValue("" , Types.VARCHAR);
					values.put("SHOPID", v1);
					DataValue v2 = new DataValue("N", Types.VARCHAR);
					values.put("IsRegister", v2);

					// condition
					Map<String, DataValue> conditions = new HashMap<String, DataValue>();
					DataValue c1 = new DataValue(map.get("MACHINECODE").toString(), Types.VARCHAR);
					conditions.put("MACHINECODE", c1);

					this.doUpdate("Platform_CregisterDetail", values, conditions);
				}

			}


			//查找需要一下有多少需要注册并且是可以注册的，这里企业编号需要修改一下，目前是写死取10
			String sql="select AA.*,BB.TerminalLicence,BB.MACHINECODE from " + 
					"( " + 
					"select A.* ,rownum ARN from DCP_ORG A " + 
					"left join  platform_cregisterdetail B on A.EID=B.EID and A.Organizationno=B.SHOPID  and B.Producttype='1'   " + 
					"where A.Org_Form='2'  and B.SHOPID is null and A.status='100' "
					+ " and A.EID='10'  and ( A.ISMANUALREG='N' or A.ISMANUALREG is null  ) " + 
					"order by A.EID,A.Organizationno " + 
					")  AA left join  " + 
					"( " + 
					"select A.*,rownum BRN from platform_cregisterdetail A " + 
					"where A.Producttype='1' and A.bdate<='"+sdate+"' and '"+sdate+"'<=A.EDATE and A.IsRegister='N' and A.SHOPID is null " + 
					"order by A.TerminalLicence  " + 
					") BB on AA.ARN=BB.BRN  where BB.BRN IS NOT NULL  ";

			List<Map<String, Object>> listdorder=this.doQueryData(sql, null);
			if(listdorder!=null&&!listdorder.isEmpty())
			{
				//开始组JSON
				for (Map<String, Object> map : listdorder) 
				{
					//成功需要更新一下订单的物流回写状态
					Map<String, DataValue> values = new HashMap<String, DataValue>();
					DataValue v = new DataValue(map.get("EID").toString() , Types.VARCHAR);
					values.put("EID", v);
					DataValue v1 = new DataValue(map.get("ORGANIZATIONNO").toString() , Types.VARCHAR);
					values.put("SHOPID", v1);
					DataValue v2 = new DataValue("Y", Types.VARCHAR);
					values.put("IsRegister", v2);

					// condition
					Map<String, DataValue> conditions = new HashMap<String, DataValue>();
					DataValue c1 = new DataValue(map.get("MACHINECODE").toString(), Types.VARCHAR);
					conditions.put("MACHINECODE", c1);

					this.doUpdate("Platform_CregisterDetail", values, conditions);
				}

			}

		} catch (Exception e) 
		{
			// TODO: handle exception

			bRun=false;
		}
		bRun=false;

		return "";
	}


}
