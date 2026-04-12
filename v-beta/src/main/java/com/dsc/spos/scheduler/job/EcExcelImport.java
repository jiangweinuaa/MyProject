package com.dsc.spos.scheduler.job;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;
import com.dsc.spos.utils.FtpUtils;
import com.dsc.spos.utils.MapDistinct;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

//
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class EcExcelImport extends InitJob
{
	//******兼容即时服务的,只查询指定的那张单据******
	String pEId="";
	String pShop="";	
	String pOrganizationNO="";
	String pBillNo="";

	Logger logger = LogManager.getLogger(EcExcelImport.class.getName());

	static boolean bRun=false;//标记此服务是否正在执行中

	public EcExcelImport() 
	{

	}


	public EcExcelImport(String eId,String shopId,String organizationNO, String billNo) 
	{
		pEId=eId;
		pShop=shopId;
		pOrganizationNO=organizationNO;
		pBillNo=billNo;
	}


	public String doExe()
	{
		//返回信息
		String sReturnInfo="";
		//此服务是否正在执行中
		if (bRun && pEId.equals(""))
		{		
			logger.info("\r\n*********电商EXCEL订单抓取EcExcelImport正在执行中,本次调用取消:************\r\n");

			sReturnInfo="定时传输任务-电商EXCEL订单抓取EcExcelImport正在执行中！";
			return sReturnInfo;
		}

		bRun=true;//			

		logger.info("\r\n*********电商EXCEL订单抓取EcExcelImport定时调用Start:************\r\n");

		try 
		{
			//System.out.println(orderStatusLogTimes);

			//查询所有有效格式
			String orderFormatSql = this.getEcImportFormat();
			List<Map<String, Object>> formatDatas = this.doQueryData(orderFormatSql, null);
			if (formatDatas.size() > 0) 
			{				
						//单头主键字段
				Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查询条件
				condition.put("EID", true);
				condition.put("ORDERFORMATNO", true);
				//调用过滤函数
				List<Map<String, Object>> getQHeader=MapDistinct.getMap(formatDatas, condition);
				for (Map<String, Object> map : getQHeader) 
				{
					//这个就是企业编码
					String eId = map.get("EID").toString();
					//门店编码如果没值,下面会取系统参数归属门店
		
					String inEccustomerno = map.get("ECCUSTOMERNO").toString();
					String orderFormatNo = map.get("ORDERFORMATNO").toString();
					String orderFormatName = map.get("ORDERFORMATNAME").toString();
					String ecPlatformNo = map.get("ECPLATFORMNO").toString();
					//1:FTP 2:網絡磁碟機
					String fileFrom = map.get("FILEFROM").toString();
					String ftp_uId = map.get("FTP_UID").toString();
					String ftp_pwd = map.get("FTP_PWD").toString();
					String ftp_filePath = map.get("FILEPATH").toString();

					//谁在有的表里面干的主键
					if (inEccustomerno.equals("")) 
					{
						inEccustomerno=" ";
					}					

					//过滤得到此格式明细记录
					Map<String, Object> conditionValues=new HashMap<String, Object>();
					conditionValues.put("EID", eId);
					conditionValues.put("ORDERFORMATNO", orderFormatNo);					
					List<Map<String, Object>> getQDetail=MapDistinct.getWhereMap(formatDatas, conditionValues, true, 0);

					//有明细,才是有效资料,再进行下载文件处理
					if (getQDetail!=null && getQDetail.size()>0) 
					{
						//1:FTP 2:網絡磁碟機
						if (fileFrom.equals("1")) 
						{
							String filePath2 = ftp_filePath.substring(6); // 截取6位之后的字符串, 前六位是固定的   ftp://
							String Ip = StringUtils.substringBefore(filePath2,"/" ); 
							String workFile = StringUtils.substringAfter(filePath2, Ip); // 要导出的文件 所在位置

							FtpUtils ftp=new FtpUtils();
							ftp.setHostname(Ip);
							//默认端口
							ftp.setPort(21);
							ftp.setUsername(ftp_uId);
							ftp.setPassword(ftp_pwd);									

							String localFilePath = System.getProperty("catalina.home")+"\\webapps\\EC\\"+ecPlatformNo+"\\import\\";
							//这个地址需要处理一下,如果是其他电商平台
							//需要在路径下安装归属客户,再建一层目录(可以支持同时存在多个格式,分别都属于其他电商电商)
							if (ecPlatformNo.equals("general")) 
							{
								localFilePath = System.getProperty("catalina.home")+"\\webapps\\EC\\"+ecPlatformNo+"\\"+inEccustomerno+"\\import\\";
							}

							File localFile = new File(localFilePath);//设置本地下载的目录
							if (localFile.exists()==false) 
							{
								localFile.mkdirs();
							}
							//只下载excel格式文件
							boolean bRet=ftp.downloadFile(workFile, "", localFilePath,".xls",".xlsx");
							if (bRet) 
							{								
								File[] lstFiles=localFile.listFiles();
								//如果存在多个文件，一个一个导入
								for (int li = 0; li < lstFiles.length; li++) 
								{
									if (ecPlatformNo.equals("pchome"))//
									{										

									}
									else if (ecPlatformNo.equals("momo"))//
									{

									}
									else if (ecPlatformNo.equals("yahoosuper"))//有API暂时先不处理
									{

									}
									else if (ecPlatformNo.equals("91app"))//有API暂时先不处理
									{

									}
									else if (ecPlatformNo.equals("shopee"))//有API暂时先不处理
									{

									}
									else if (ecPlatformNo.equals("letian"))//有API暂时先不处理
									{

									}
									else //general普通的EXCEL格式 
									{
										InputStream inputStream = new FileInputStream(localFilePath + lstFiles[li].getName());
										Workbook workbook = Workbook.getWorkbook(inputStream);
										//暂时只解析第一个sheet
										Sheet sheet = workbook.getSheet(0);
										//EXCEL行数
										int rowsCount = sheet.getRows();										
										//默认标题行是第一行
										Cell[] headRow = sheet.getRow(0);

										//获取标题对应的列索引
										Map<String, Object> MapExcelColumnIndex=getExcelColumnIndex(headRow);
										
										//循环EXCEL行记录
										for (int ei = 0; ei < rowsCount; ei++) 
										{
											//注意这里1张订单会有多行记录(默认根据单号认定是同1张订单)										
											
											//以格式明细配置的字段为准,excel文件字段会有多余
											for (Map<String, Object> mapDetail : getQDetail) 
											{							
												//1：EXCEL栏位 2：固定值 3：基础资料(新零售)
												String detailFromtype = mapDetail.get("FROMTYPE").toString();
												//FROMTYPE=1 就是EXCEL列名称，FROMTYPE=2 固定值
												String detailFromvalue = mapDetail.get("FROMVALUE").toString();

												//1：EXCEL栏位
												if (detailFromtype.equals("1"))
												{
													if (MapExcelColumnIndex.containsKey(detailFromvalue)) 
													{
													}
													else 
													{
														logger.error("\r\n******电商EXCEL订单抓取EcExcelImport，EXCEL列名称【"+detailFromvalue+"】找不到******\r\n");
													}
												}											
												else 
												{

												}

											}												
										}
										//
										MapExcelColumnIndex=null;
									}
								}							
							}

							localFile=null;
							ftp=null;
						}
						else 
						{
							//需补充

						}						


					}
					else 
					{
						logger.info("\r\n******电商EXCEL订单抓取EcExcelImport,格式："+orderFormatNo+"【"+orderFormatName+"】，未设置明细资料******\r\n");
					}						

				}			

			}
			else 
			{
				logger.info("\r\n******电商EXCEL订单抓取EcExcelImport,未设置要导入的EXCEL格式资料******\r\n");
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

				logger.error("\r\n******电商EXCEL订单抓取EcExcelImport报错信息" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");
				
				pw=null;
				errors=null;
			} 
			catch (IOException e1) 
			{					
				logger.error("\r\n******电商EXCEL订单抓取EcExcelImport报错信息" + e.getMessage() + "******\r\n");
			}		

			//
			sReturnInfo="错误信息:" + e.getMessage();
		}
		finally 
		{

			bRun=false;//
			logger.info("\r\n*********电商EXCEL订单抓取EcExcelImport定时调用End:************\r\n");
		}
		return sReturnInfo;


	}



	/**
	 * 查询导入格式，必须有归属客户的，没有归属客户ERP无法对账
	 * @return
	 */
	private String getEcImportFormat() 
	{
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer();
		sqlbuf.append("SELECT * FROM OC_ECORDERFORMAT a  "
				+ " LEFT JOIN OC_ECORDERFORMAT_DETAIL b   ON a.eid = b.eid AND a.ORDERFORMATNO = b.ORDERFORMATNO AND b.status='100' "
				+ " where a.status='100' and length(ECCUSTOMERNO)>0 "
				);

		sqlbuf.append(" order by a.orderformatno, b.item ");

		sql = sqlbuf.toString();
		return sql;
	}


	/**
	 * 获取标题对应的列索引
	 * @param headRow
	 * @return
	 */
	private Map<String, Object> getExcelColumnIndex(Cell[] headRow)
	{
		Map<String, Object> MapExcel=new HashMap<String, Object>();

		for (int i = 0; i < headRow.length; i++) 
		{
			//重复标题不添加，理论上这里不会出现重复
			if (MapExcel.containsKey(headRow[i].getContents())==false) 
			{
				MapExcel.put(headRow[i].getContents(), headRow[i].getColumn());			
			}			
		}

		return MapExcel;
	}



}
