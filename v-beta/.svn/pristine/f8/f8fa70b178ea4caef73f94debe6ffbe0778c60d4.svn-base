package com.dsc.spos.scheduler.job;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.ExecBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.utils.Mail;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;

//********************  表空间获取    **************************
//***********************************************************
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class TableSpaceGet extends InitJob  {

	Logger logger = LogManager.getLogger(TableSpaceGet.class.getName());
	static boolean bRun = false;// 标记此服务是否正在执行中

	//一天只发一次邮件
	static String myDate="";

	public TableSpaceGet()
	{

	}
	public String doExe() 
	{
		//返回信息
		String sReturnInfo="";
		//		//此服务是否正在执行中
		//		if (bRun)
		//		{		
		//			logger.info("\r\n*********表空间获取TableSpaceGet正在执行中,本次调用取消:************\r\n");
		//			sReturnInfo="表空间获取TableSpaceGet正在执行中！";
		//			return sReturnInfo;
		//		}
		//		bRun=true;//			
		//
		//		Calendar cal = Calendar.getInstance();// 获得当前时间
		//		SimpleDateFormat dfDate = new SimpleDateFormat("yyyyMMdd");
		//		//系统日期 
		//		String sysDate=dfDate.format(cal.getTime());
		//		
		//		logger.info("\r\n*********表空间获取TableSpaceGet定时调用Start:************\r\n");
		//		try 
		//		{
		//			List<DataProcessBean> data = new ArrayList<DataProcessBean>();
		//			//先删后插
		//			ExecBean exec=new ExecBean("delete from PLATFORM_TABLESPACE ");
		//			data.add(new DataProcessBean(exec)); 
		//	
		//			//表空间计算
		//			//sql = " select username,default_tablespace from dba_users where username = 'POS_PRD'  OR username = 'POS'  OR username = 'C##POS'  OR username = 'CRM' ";
		//			String sql = " select username,default_tablespace from dba_users where username like '%POS%' OR username like '%CRM%'  "; 
		//
		//			List<Map<String, Object>> getUsersQData=this.doQueryData(sql, null);   
		//			if (getUsersQData != null && getUsersQData.isEmpty() == false)
		//			{
		//				StringBuffer sb = new StringBuffer();
		//				String defaultTableSpace="";
		//				for (Map<String, Object> oneUsersData : getUsersQData) 
		//				{
		//					sb.append("'"+ oneUsersData.get("DEFAULT_TABLESPACE").toString()+"'," );
		//				}
		//				defaultTableSpace=sb.toString();
		//				defaultTableSpace = defaultTableSpace.substring(0,defaultTableSpace.length()-1);
		//
		//				//查询总的表空间容量
		//				sql = " SELECT * FROM ( "
		//						+ " select tablespace_name, sum(bytes)/1024/1024 as TOTMB from dba_data_files a "	
		//						+ " where a.tablespace_name in (" + defaultTableSpace+ " ) "
		//						+ " group by a.tablespace_name ORDER BY  a.tablespace_name ) " ;
		//				List<Map<String, Object>> getSumQData=this.doQueryData(sql, null);   
		//
		//				//查询剩余的表空间容量
		//				PosPub.iTimeoutTime=300;
		//				sql = " SELECT tablespace_name,sum(FREEMB) FREEMB FROM ( "
		//						+ " select tablespace_name,bytes/1024/1024 as FREEMB from dba_free_space a "
		//						+ " where a.tablespace_name in (" + defaultTableSpace+ ")  ) group by tablespace_name ";
		//				List<Map<String, Object>> getFreeQData=this.doQueryData(sql, null);
		//				PosPub.iTimeoutTime=30;
		//				
		//				if (getSumQData != null && getSumQData.isEmpty() == false)
		//				{
		//					//
		//					boolean bNeedmail=false;
		//					
		//					for (Map<String, Object> oneSumData : getSumQData) 
		//					{
		//						String tableSpaceName = oneSumData.get("TABLESPACE_NAME").toString();
		//						String totBytes =  oneSumData.get("TOTMB").toString();
		//						if (!PosPub.isNumericType(totBytes)) totBytes="0";
		//						BigDecimal totBytes_B = new BigDecimal(totBytes);
		//						BigDecimal freeBytes_B = new BigDecimal(0);
		//						BigDecimal usedBytes_B = new BigDecimal(0);
		//						for (Map<String, Object> oneFreeData : getFreeQData) 
		//						{
		//							String free_TableSpaceName = oneFreeData.get("TABLESPACE_NAME").toString();
		//							String freeBytes =  oneFreeData.get("FREEMB").toString();
		//							if (!PosPub.isNumericType(freeBytes)) freeBytes="0";
		//							if (free_TableSpaceName.equals(tableSpaceName))
		//							{
		//								freeBytes_B = freeBytes_B.add( new BigDecimal(freeBytes)    ) ; 
		//							}		
		//						}
		//						//表空间取整
		//						totBytes_B = totBytes_B.setScale(0,RoundingMode.HALF_UP);
		//						freeBytes_B = freeBytes_B.setScale(0,RoundingMode.HALF_UP);
		//
		//						//计算已用表空间并取整
		//						usedBytes_B = totBytes_B.subtract(freeBytes_B);
		//						usedBytes_B = usedBytes_B.setScale(0,RoundingMode.HALF_UP);
		//
		//						String[] ColumnsName = {
		//								"TABLESPACENAME","TOTSPACE",
		//								"FREESPACE","USEDSPACE","STATUS"
		//						};
		//
		//						DataValue[] dataValue = new DataValue[]{
		//								new DataValue(tableSpaceName, Types.VARCHAR), 
		//								new DataValue(totBytes_B.toString(), Types.VARCHAR), 
		//								new DataValue(freeBytes_B.toString(), Types.VARCHAR),
		//								new DataValue(usedBytes_B.toString(), Types.VARCHAR), 
		//								new DataValue("100", Types.VARCHAR) };
		//						InsBean IB = new InsBean("PLATFORM_TABLESPACE", ColumnsName);
		//						IB.addValues(dataValue);
		//						data.add(new DataProcessBean(IB));
		//						
		//						//POS_DATA/CRM_TS
		//						if (freeBytes_B.intValue()<1024 && (tableSpaceName.equals("POS_DATA") || tableSpaceName.equals("CRM_TS"))) 
		//						{
		//							bNeedmail=true;
		//						}
		//					}
		//					
		//					//停止发送邮件  BY JZMA 20200325
		//					
		//					bNeedmail=false;
		//					if (bNeedmail) 
		//					{
		//						if (myDate.equals(sysDate)==false) 
		//						{
		//							String[] strSplit= StaticInfo.sOrgTopName.split("<br>");
		//							List<Map<String, String>> rows=new ArrayList<Map<String, String>>();
		//							for (int i = 0; i < strSplit.length; i++) 
		//							{
		//								Map<String, String> map=new LinkedHashMap<>();
		//								String[] strTemp=strSplit[i].split(",");
		//								for (int j = 0; j < strTemp.length; j++) 
		//								{
		//									String[] str=strTemp[j].split("=");
		//									if (str.length>1) 
		//									{					
		//										map.put(str[0], str[1]);
		//										
		//									}				
		//								}	
		//								if (map.size()>0) 
		//								{
		//									rows.add(map);
		//								}			
		//							}
		//									
		//							MyCommon myCommon=new MyCommon();
		//							
		//							Mail mail=new Mail();
		//							String[] receiverEmail={"418056790@qq.com","37501820@qq.com","382498008@qq.com","123309649@qq.com"};
		//							String[] filenames=null;
		//							mail.sendMail(receiverEmail, "表空间不足1G,请尽快处理", myCommon.getTableFormatContent("表空间不足1G,请尽快处理", rows), filenames);
		//							mail=null;
		//							myCommon=null;
		//							
		//							//赋值
		//							myDate=sysDate;
		//						}			
		//					}
		//				}
		//				
		//				getFreeQData.clear();
		//				getFreeQData=null;
		//				
		//				getSumQData.clear();
		//				getSumQData=null;	
		//				
		//			}
		//
		//			getUsersQData.clear();
		//			getUsersQData=null;
		//			
		//			StaticInfo.dao.useTransactionProcessData(data);
		//			
		//			//清理
		//			data.clear();
		//			data=null;
		//			
		//			//删除JOB异常日志    BY JZMA 20190527
		//			InsertWSLOG.delete_JOBLOG(" "," ", "TableSpaceGet");
		//		}
		//		catch (Exception e) 
		//		{
		//			try 
		//			{
		//				StringWriter errors = new StringWriter();
		//				PrintWriter pw=new PrintWriter(errors);
		//				e.printStackTrace(pw);		
		//				
		//				pw.flush();
		//				pw.close();			
		//				
		//				errors.flush();
		//				errors.close();
		//				
		//				logger.error("\r\n******表空间获取TableSpaceGet报错信息" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");
		//
		//				//记录JOB异常日志  BY JZMA 20190527
		//				InsertWSLOG.insert_JOBLOG(" "," ","TableSpaceGet", "表空间查询", e.getMessage());
		//
		//				pw=null;
		//				errors=null;
		//			} 
		//			catch (IOException e1) 
		//			{					
		//				logger.error("\r\n******表空间获取TableSpaceGet报错信息" + e.getMessage() + "******\r\n");
		//			}
		//			catch (Exception e1) 
		//			{					
		//				logger.error("\r\n******表空间获取TableSpaceGet报错信息" + e.getMessage() + "******\r\n");
		//			}
		//
		//			sReturnInfo="错误信息:" + e.getMessage();
		//		}
		//		finally 
		//		{
		//			PosPub.iTimeoutTime=30;
		//			bRun=false;
		//			logger.info("\r\n*********表空间获取TableSpaceGet定时调用End:************\r\n");
		//		}			
		//
		return sReturnInfo;
	}

}
