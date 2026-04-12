package com.dsc.spos.scheduler.job;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import com.dsc.spos.config.SPosConfig.ProdInterface;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.InsBean;

import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.EncryptUtils;
import com.dsc.spos.utils.Ftp;
import com.dsc.spos.utils.FtpInterface;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.MapDistinct;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

/**
 * 定时从ftp 导 Excel 文件到 订单表
 * @author yuayy 2019-04-16
 *
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class FTPExcelImport extends InitJob 
{
	//******兼容即时服务的,只查询指定的那张单据******
	String pEId="";
	String pShop="";	
	String pOrganizationNO="";
	String pBillNo="";

	public FTPClient ftpClient = null;

	static boolean bRun=false;//标记此服务是否正在执行中

	public FTPExcelImport()
	{

	}

	public FTPExcelImport(String eId,String shopId,String organizationNO, String billNo)
	{
		pEId=eId;
		pShop=shopId;
		pOrganizationNO=organizationNO;
		pBillNo=billNo;
	}

	public String doExe() throws Exception
	{
		//返回信息
		String sReturnInfo="";
		//此服务是否正在执行中
		if (bRun && pEId.equals(""))
		{		
			WriteFTPExcelLog("\r\n*********定時任務FTPExcelImport從FTP導Excel正在執行中,本次調用取消:************\r\n");

			sReturnInfo="定時傳輸任務-從FTP導Excel正在執行中！";
			return sReturnInfo;
		}

		bRun=true;//			

		WriteFTPExcelLog("\r\n*********從FTP導Excel定時任務調用Start:************\r\n");

		//记录一下能导入系统的订单号
		StringBuffer sbOrderno=new StringBuffer();

		String sql = "";

		try 
		{
			Calendar cal = Calendar.getInstance();// 获得当前时间
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
			String sDate = df.format(cal.getTime());
			df = new SimpleDateFormat("HHmmss");
			String sTime = df.format(cal.getTime());

			//訂單日誌時間
			df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			String orderStatusLogTimes=df.format(cal.getTime());

			String langtype="zh_CN";
			List<ProdInterface> lstProd=StaticInfo.psc.getT100Interface().getProdInterface();
			if(lstProd!=null&&!lstProd.isEmpty())
			{
				langtype=lstProd.get(0).getHostLang().getValue();
			}			

			String ecPlatformSql = "select distinct a.eId,a.ecPlatformNo,a.ECPLATFORMNAME   from OC_ECOMMERCE a "

					+ " where a.status='100' order by eId,ecplatformno ";
			//所有的电商平台
			List<Map<String, Object>> ecPlatformDatas=this.doQueryData(ecPlatformSql, null);

			String eId = ""; //这里应该传 pEId , 登陆用户的企业编码
			String shopId = "";
			String shopName = "";
			String warehouse="";
			String eccustomerno="";

			//订单导入格式
			String orderFormatSql = this.getEcImportFormat(langtype);
			List<Map<String, Object>> formatDatas = this.doQueryData(orderFormatSql, null);

			if(ecPlatformDatas != null && ecPlatformDatas.isEmpty() == false)
			{
				for (Map<String, Object> map1 : ecPlatformDatas) 
				{
					eId = map1.get("EID").toString();								

					String ecPlatformNo = map1.get("ECPLATFORMNO").toString();
					String ecPlatformName = map1.get("ECPLATFORMNAME").toString();

					String paySql = this.getMappingPaymentSQL(eId,ecPlatformNo);
					List<Map<String, Object>> payData = this.doQueryData(paySql, null);

					if (payData==null || payData.isEmpty()) 
					{
						WriteFTPExcelLog(ecPlatformNo +" " + ecPlatformName +":找不到該電商平台支付方式映射信息");
						continue;
					}


					String ftp_uId = "";
					String ftp_pwd = "";
					String ftp_filePath = "";

					FtpInterface ftpInterface = new Ftp();

					Map<String, Object> cond1 =new HashMap<String, Object>();
					cond1.put("EID", eId);
					cond1.put("ECPLATFORMNO", ecPlatformNo);
					List<Map<String, Object>> listHeader=MapDistinct.getWhereMap(formatDatas, cond1, true);
					cond1.clear();

					//格式單頭過濾，一個電商代碼 會員多個格式
					Map<String, Boolean> mCond = new HashMap<String, Boolean>(); //查询条件
					mCond.put("KEY", true);
					List<Map<String, Object>> getHeaderFormatdata=MapDistinct.getMap(listHeader, mCond);
					mCond.clear();

					if (getHeaderFormatdata != null && getHeaderFormatdata.isEmpty() == false)
					{						
						for (Map<String, Object> map : getHeaderFormatdata) 
						{ 						

							//归属门店、仓库、客户
							shopId = map.get("SHOPID").toString();
							shopName = map.get("SHOPNAME").toString();	
							warehouse = map.get("WAREHOUSE").toString();
							eccustomerno = map.get("ECCUSTOMERNO").toString();
							String orderFormatNo = map.get("ORDERFORMATNO").toString();
							String orderFormatName = map.get("ORDERFORMATNAME").toString();

							if(shopId.trim().equals("") || warehouse.trim().equals("") || eccustomerno.trim().equals(""))
							{
								WriteFTPExcelLog("\r\n*********定時任務FTPExcelImport，导入格式 "+orderFormatNo+" "+orderFormatName+" SHOPID、WAREHOUSE、ECCUSTOMERNO信息為空跳出************\r\n");
								sReturnInfo=sReturnInfo + " 導入格式 " +orderFormatNo+" "+orderFormatName+" SHOPID、WAREHOUSE、ECCUSTOMERNO信息為空！！";
								continue;
							}							

							//FILEPATH路径的最后一级
							// fileSuffix 专用于pchome ，不同的文件夹区分不同的格式， 都是pchome 平台
							String fileSuffix = "";
							fileSuffix = map.get("FILEPATH").toString();
							int beginIndex = fileSuffix.lastIndexOf("/");
							fileSuffix = fileSuffix.substring(beginIndex + 1);

							if(fileSuffix.equals("import"))
							{
								fileSuffix = ""; 
							}							

							ftp_uId = map.get("FTP_UID").toString();
							ftp_pwd = map.get("FTP_PWD").toString();
							ftp_filePath = map.get("FILEPATH").toString();

							//这里加上验证信息, 验证这三个参数是否为空
							if(ftp_uId.trim().equals("") || ftp_pwd.trim().equals("") || ftp_filePath.trim().equals(""))
							{
								WriteFTPExcelLog("\r\n*********定時任務FTPExcelImport，"+ecPlatformNo+" "+ecPlatformName+" FTP_UID、FTP_PWD、FILEPATH信息為空跳出************\r\n");
								sReturnInfo=sReturnInfo + " " +ecPlatformNo+" "+ecPlatformName+" 的FTP賬號、密碼、文件路徑為空！！";
								continue;
							}

							//
							if (ftp_filePath.startsWith("ftp://")==false) 
							{
								WriteFTPExcelLog("\r\n*********定時任務FTPExcelImport，"+ecPlatformNo+" "+ecPlatformName+" FTP文件路徑FILEPATH必須以ftp://開頭************\r\n");
								sReturnInfo=sReturnInfo + " " +ecPlatformNo+" "+ecPlatformName+" 的FTP賬號、密碼、文件路徑為空！！";

								continue;
							}

							//明細過濾			
							Map<String, Object> condition =new HashMap<String, Object>();
							condition.put("EID", eId);
							condition.put("ECPLATFORMNO", ecPlatformNo);
							condition.put("ORDERFORMATNO", orderFormatNo);
							//调用过滤函数
							List<Map<String, Object>> lstDetailFormatDatas=MapDistinct.getWhereMap(listHeader, condition, true);
							condition.clear();

							String filePath2 = ftp_filePath.substring(6); // 截取6位之后的字符串, 前六位是固定的   ftp://
							String Ip = StringUtils.substringBefore(filePath2,"/" ); 
							String workFile = StringUtils.substringAfter(filePath2, Ip); // 要导出的文件 所在位置

							if(ecPlatformNo.equals("shopee"))
							{
								// 登录ftp，获取事件
								FTPClient ftpClient = ftpInterface.ftp(Ip, ftp_uId, ftp_pwd);//这里是ip，用户名，密码

								ftpClient.setControlEncoding("GBK"); // 中文支持
								ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);//设置文件类型				

								ftpClient.connect(Ip); //连接ftp服务器
								boolean bOkLogin=ftpClient.login(ftp_uId, ftp_pwd ); //登录ftp服务器
								if (bOkLogin==false) 
								{
									WriteFTPExcelLog("\r\n*********" + "FTP登錄失敗:電商EXCEL導入格式編碼=" +orderFormatNo +",電商平台="+ecPlatformNo + "************\r\n");
								}

								try
								{
									ftpClient.enterLocalPassiveMode();//设置ftp 模式，有被动模式和活动模式，这里设置为被动模式
									FTPFile [] files = ftpClient.listFiles(workFile);			

									for (int f = 0; f < files.length; f++)
									{
										// 下载Ftp文件到本地，用于读取excel 文件的内容
										//	String localFilePath = "D:\\Download\\";
										String localFilePath = System.getProperty("catalina.home")+"\\webapps\\EC\\"+ecPlatformNo+"\\import\\";
										String fileName = files[f].getName();

										File localFile = new File(localFilePath + fileName);//设置本地下载的目录
										boolean flag1 = false ;
										boolean flag2 = false ;

										flag1 =  localFile.getName().toLowerCase().endsWith(".xls");
										flag2 =  localFile.getName().toLowerCase().endsWith(".xlsx");
										if(flag1 || flag2)
										{ //如果当前文件是 excel 文件
											ftpClient.changeWorkingDirectory(workFile);  // 这里应该填客户定义的地址 
											File fileparent = localFile.getParentFile();//本地下载目录下的文件夹，如果不存在则创建
											if (!fileparent.exists())
											{
												fileparent.mkdirs();
											}
											OutputStream os = new FileOutputStream(localFile);//输出到本地文件流
											ftpClient.retrieveFile(files[f].getName(), os);//下载文件到本地
											os.close();
											// 下载到本地完成，之后要进行excel导入到OC_order 
											//FileInputStream in = new FileInputStream(new File(localFilePath + fileName));
											InputStream inputStream = new FileInputStream(localFilePath + fileName);
											Workbook workbook = Workbook.getWorkbook(inputStream);
											Sheet sheet = workbook.getSheet(0);
											int rows = sheet.getRows();
											int columns = sheet.getColumns();


											Cell[] headRow = sheet.getRow(0);
											int detailItem = 1;  // 子表item
																	
											List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();		
											// i 是行号 , j 是列号
											for (int i = 1; i < rows; i++) 
											{
												int num = 0; // 这里声明num，用来记录主表插入的次数， 一个订单多个商品，主表数据只能 插入一次

												ArrayList<String> columnsFName = new ArrayList<String>();
												ArrayList<String> columnsFValue = new ArrayList<String>();

												ArrayList<String> columnsFName2 = new ArrayList<String>();
												ArrayList<String> columnsFValue2 = new ArrayList<String>();

												// OC_ORDER_AGIO 订单折扣表（目前只有虾皮有折扣， 可能折扣有多种 ），格式设置里：设置商家承担金额、 平台承担金额  即可。
												// 折扣总金额（agioAmt）由程序计算，不需要在格式里填写该字段 
												ArrayList<String> agioColumnsFName = new ArrayList<String>();
												ArrayList<String> agioColumnsFValue = new ArrayList<String>();

												// OC_ORDER_PAY 付款方式
												ArrayList<String> payColumnsFName = new ArrayList<String>();
												ArrayList<String> payColumnsFValue = new ArrayList<String>();


												columnsFName.add("EID");
												columnsFName.add("CUSTOMERNO");
												columnsFName.add("ORGANIZATIONNO");
												columnsFName.add("SHOPID");
												columnsFName.add("SHOPNAME");
												columnsFName.add("LOAD_DOCTYPE");
												columnsFName.add("STATUS");
												columnsFName.add("REFUNDSTATUS");
												columnsFName.add("CREATE_DATETIME");
												columnsFName.add("ECCUSTOMERNO");

												columnsFValue.add(eId);
												columnsFValue.add(" ");
												columnsFValue.add(shopId);
												columnsFValue.add(shopId);
												columnsFValue.add(shopName);
												columnsFValue.add(ecPlatformNo);
												columnsFValue.add("2"); // 2 已接单
												columnsFValue.add("1"); // 退货退订状态  ：   1 未申请
												columnsFValue.add(sDate+sTime);
												columnsFValue.add(eccustomerno);


												columnsFName2.add("EID");
												columnsFName2.add("CUSTOMERNO");
												columnsFName2.add("ORGANIZATIONNO");
												columnsFName2.add("SHOPID");
												columnsFName2.add("LOAD_DOCTYPE");

												columnsFValue2.add(eId);
												columnsFValue2.add(" ");
												columnsFValue2.add(shopId);
												columnsFValue2.add(shopId);
												columnsFValue2.add(ecPlatformNo);

												agioColumnsFName.add("EID");
												agioColumnsFName.add("CUSTOMERNO");
												agioColumnsFName.add("ORGANIZATIONNO");
												agioColumnsFName.add("SHOPID");

												agioColumnsFValue.add(eId);
												agioColumnsFValue.add(" ");
												agioColumnsFValue.add(shopId);
												agioColumnsFValue.add(shopId);


												Cell[] rowValue = sheet.getRow(i);
												// 防止excel 出现第一列不为空， 且值不是需要插入到表中的值
												String orderNo = ""; 
												String contMan = "";
												String contTel = "";
												String memberGet = "0"; // 0不抓取， 1抓取， 默认给0
												memberGet = map.get("MEMBERGET").toString();
												orderNo = rowValue[0].getContents();

												BigDecimal sellerDisc = new BigDecimal(0);
												BigDecimal platformDisc = new BigDecimal(0);
												BigDecimal agioAmt = new BigDecimal(0);

												for (Map<String, Object> oneData : lstDetailFormatDatas) 
												{
													String columnItem = oneData.get("ITEM").toString();
													String tableName = oneData.get("TABLENAME").toString();
													String columnName = oneData.get("COLUMNNAME").toString(); // excel列名
													String fieldName = oneData.get("FIELDNAME").toString().toUpperCase(); // 数据库列名，转换为大写

													//System.out.println(columnName + ":"+ fieldName);

													for (int j = 0; j < columns; j++) 
													{
														String columnIndex = (j + 1) + "";
														String headRowName = headRow[j].getContents() ;
														String fieldValue = "";
														fieldValue = rowValue[j].getContents();

														if (headRowName.equals(columnName) && tableName.equals("OC_ORDER") ) 
														{
															columnsFName.add(fieldName);
															columnsFValue.add(fieldValue);

															if(fieldName.equals("ORDERNO"))
															{
																orderNo = fieldValue;

																agioColumnsFName.add("ORDERNO");
																agioColumnsFValue.add(orderNo);

															}
															// 获取订购人，订购人电话信息
															if(fieldName.equals("CONTMAN"))
															{
																contMan = fieldValue;
															}
															if(fieldName.equals("CONTTEL"))
															{
																contTel = fieldValue;
															}

														}
														if (columnIndex.equals(columnItem) && headRowName.equals(columnName) && tableName.equals("OC_ORDER_DETAIL") ) {
															if(headRowName.equals("商品資訊") || headRowName.equals("商品资讯"))
															{
																// 计算总共有多少行
																int fromIndex = 0;
																int countIndex = 0;
																while (true) 
																{
																	int index = fieldValue.indexOf("[", fromIndex);
																	if (-1 != index) 
																	{
																		fromIndex = index + 1;
																		countIndex++;
																	} 
																	else 
																	{
																		break;
																	}
																}

																String pluList[] = fieldValue.trim().split(";");//// 这里必须去空格：
																//// 表格中存在换行，换行后会出现空格行
																int totalNum = pluList.length; // 得到该单元格总个数，用于下面得到每行每个元素的值
																int avgNum = totalNum / countIndex; // 每一行元素个数
																// "ITEM","PLUNAME","QTY","PRICE","SPECNAME"

																for (int n = 1; n < countIndex + 1; n++) 
																{
																	String item = n + ""; // 得到明细表中序号
																	columnsFName2.add("ITEM");
																	columnsFValue2.add(item);
																	int insColCt = 0;
																	for (int m = 1; m < avgNum + 1; m++) 
																	{
																		String keyVal = "";
																		int index = 0;
																		String tempStr = "";
																		switch (m) 
																		{
																		case 1: // pluName
																			index = (n - 1) * 6 + 0;
																			tempStr = pluList[index];
																			String pluName = tempStr.substring(tempStr.indexOf(":") + 1);
																			keyVal = pluName;
																			columnsFName2.add("PLUNAME");
																			columnsFValue2.add(pluName);
																			break;
																		case 2: // specName
																			index = (n - 1) * 6 + 1;
																			tempStr = pluList[index];
																			String specName = tempStr.substring(tempStr.indexOf(":") + 1);
																			keyVal = specName;
																			columnsFName2.add("SPECNAME");
																			columnsFValue2.add(specName);
																			break;
																		case 3: // price
																			index = (n - 1) * 6 + 2;
																			tempStr = pluList[index];
																			String price = tempStr.substring(tempStr.indexOf(":") + 1);
																			price = price.replace("$", "");
																			keyVal = price.trim().replace("￥", "");
																			columnsFName2.add("PRICE");
																			columnsFValue2.add(price);
																			break;
																		case 4: // qty
																			index = (n - 1) * 6 + 3;
																			tempStr = pluList[index];
																			String qty = tempStr.substring(tempStr.indexOf(":") + 1);
																			keyVal = qty.trim();
																			columnsFName2.add("QTY");
																			columnsFValue2.add(qty);
																			break;
																		default:
																			break;
																		}

																	}
																}
															}
															else
															{
																columnsFName2.add(fieldName);
																columnsFValue2.add(fieldValue);
															}

														} // OC_ORDER_DETAIL 结束

														if (headRowName.equals(columnName) && tableName.equals("OC_ORDER_AGIO")) 
														{
															// 折扣表 加折扣金额， AGIOAMT 计算总额即可
															// 将所有的折扣金额都加起来
															if(fieldName.equals("SELLER_DISC"))
															{
																if(fieldValue == null || fieldValue.trim().equals(""))
																{
																	fieldValue = "0";
																}
																sellerDisc = new BigDecimal(fieldValue);
																sellerDisc.add(sellerDisc);

															}
															sellerDisc = sellerDisc.setScale(2, RoundingMode.HALF_UP); // 保留两位小数
															agioAmt = agioAmt.add(sellerDisc); // 总折扣额 = 商家折扣额 + 平台折扣额

															if(fieldName.equals("PLATFORM_DISC"))
															{
																if(fieldValue == null || fieldValue.trim().equals(""))
																{
																	fieldValue = "0";
																}
																platformDisc = new BigDecimal(fieldValue);
																platformDisc.add(platformDisc);
															}
															platformDisc = platformDisc.setScale(2, RoundingMode.HALF_UP);
															agioAmt = agioAmt.add(platformDisc);
														} // OC_ORDER_PAY 结束

													}

												}
												columnsFName2.add("ORDERNO");
												columnsFValue2.add(orderNo);
												DataValue[] columnsVal = new DataValue[columnsFName.size()];
												int insColCt = columnsFName.size();
												String[] columns1  = new String[insColCt];
												DataValue[] insValue = new DataValue[insColCt];
												// 依照傳入參數組譯要insert的欄位與數值；
												int ENO1 = 0; 
												for (int k=0;k<columnsVal.length;k++)
												{
													String keyValue = columnsFName.get(k).toString();
													if (keyValue != null) 
													{
														columnsVal[k] = new DataValue(keyValue, Types.VARCHAR);
													} 
													else 
													{
														columnsVal[k] = null;
													}

													if (columnsVal[k] != null)
													{
														columns1[ENO1] = columnsFName.get(k).toString();
														String fValue = columnsFValue.get(k).toString();
														columnsVal[k] = new DataValue(fValue, Types.VARCHAR) ;
														insValue[ENO1] = columnsVal[k];
														ENO1 ++;
														if (ENO1 >= insValue.length) 
															break;
													}
												}
												// 是否按照表行来设置插入值， 可以根据正常情况下的值，
												InsBean ib1 = new InsBean("OC_ORDER", columns1);
												ib1.addValues(insValue);
												lstData.add(new DataProcessBean(ib1));	

												DataValue[] columnsVal2 = new DataValue[columnsFName2.size()];
												int insColCt2 = columnsFName2.size();
												String[] columns2  = new String[insColCt2];
												DataValue[] insValue2 = new DataValue[insColCt2];
												// 依照傳入參數組譯要insert的欄位與數值；
												int ENO2 = 0; 
												for (int k=0;k<columnsVal2.length;k++)
												{
													String keyValue = columnsFName2.get(k).toString();
													if (keyValue != null) 
													{
														columnsVal2[k] = new DataValue(keyValue, Types.VARCHAR);
													} 
													else 
													{
														columnsVal2[k] = null;
													}

													if (columnsVal2[k] != null)
													{
														columns2[ENO2] = columnsFName2.get(k).toString();
														String fValue = columnsFValue2.get(k).toString();
														columnsVal2[k] = new DataValue(fValue, Types.VARCHAR) ;
														insValue2[ENO2] = columnsVal2[k];
														ENO2 ++;
														if (ENO2 >= insValue2.length) 
															break;
													}
												}

												InsBean ib2 = new InsBean("OC_ORDER_DETAIL", columns2);
												ib2.addValues(insValue2);
												lstData.add(new DataProcessBean(ib2));	


												// ************ 折扣信息 ************
												agioColumnsFName.add("ITEM");
												agioColumnsFValue.add("1");
												agioColumnsFName.add("SELLER_DISC");
												agioColumnsFValue.add(sellerDisc.toString());
												agioColumnsFName.add("PLATFORM_DISC");
												agioColumnsFValue.add(platformDisc.toString());
												agioColumnsFName.add("AGIOAMT");
												agioColumnsFValue.add(agioAmt.toString());

												DataValue[] agiocolumnsVal = new DataValue[agioColumnsFName.size()];
												int insColCtAgio = agioColumnsFName.size();
												String[] columnsAgio = new String[insColCtAgio];
												DataValue[] insValueAgio = new DataValue[insColCtAgio];
												// 依照傳入參數組譯要insert的欄位與數值；
												int ENO = 0;
												for (int k = 0; k < agiocolumnsVal.length; k++) 
												{
													String keyValue = agioColumnsFName.get(k).toString();
													if (keyValue != null) 
													{
														agiocolumnsVal[k] = new DataValue(keyValue, Types.VARCHAR);
													} 
													else 
													{
														agiocolumnsVal[k] = null;
													}

													if (agiocolumnsVal[k] != null) 
													{
														columnsAgio[ENO] = agioColumnsFName.get(k).toString();
														String fValue = agioColumnsFValue.get(k).toString();
														agiocolumnsVal[k] = new DataValue(fValue, Types.VARCHAR);
														insValueAgio[ENO] = agiocolumnsVal[k];
														ENO++;
														if (ENO >= insValueAgio.length)
															break;
													}
												}

												InsBean ibAgio = new InsBean("OC_ORDER_AGIO", columnsAgio);
												ibAgio.addValues(insValueAgio);
												//this.addProcessData(new DataProcessBean(ibAgio));
												lstData.add(new DataProcessBean(ibAgio));	


											}

											inputStream.close();
											// 批处理执行插入
											StaticInfo.dao.useTransactionProcessData(lstData);

											String pathname = workFile + "/bak/";
											File bakFile = new File(pathname);
											if (bakFile.exists()) 
											{
												if (bakFile.isDirectory()) 
												{
													//System.out.println("目录已存在");
												} 
												else 
												{
													//System.out.println("目录下文件已存在, 不盘它了 ...");
												}
											} 
											else 
											{
												//System.out.println("目录不存在, 盘它 ...");
												//boolean make = false;
												//make = bakFile.mkdir();
											}

											try 
											{
												// 上传下载的文件到备份位置
												ftpClient.makeDirectory(pathname);
												ftpClient.changeWorkingDirectory(pathname);
												FileInputStream upInputStream = new FileInputStream( new File(localFilePath + fileName));
												ftpClient.storeFile(fileName, upInputStream);
												upInputStream.close();

												// 删除ftp下载地址的文件
												ftpClient.changeWorkingDirectory(workFile);
												ftpClient.deleteFile(fileName);

												File myDelFile = new File(localFilePath + fileName);

												//									        	java.io.File myDelFile = new java.io.File(localFilePath + fileName);
												myDelFile.delete();

												//System.out.println("本地缓存Excel文件删除成功!!!");
											}
											catch (Exception e) 
											{

											}

										}

									}

									ftpClient.logout();

								}
								catch (Exception e) 
								{

								}

							} // shopee 结束

							else if(ecPlatformNo.equals("pchome") && fileSuffix.equals("allDay"))
							{

								// 登录ftp，获取事件
								FTPClient ftpClient = ftpInterface.ftp(Ip, ftp_uId, ftp_pwd);//这里是ip，用户名，密码

								ftpClient.setControlEncoding("GBK"); // 中文支持
								ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);//设置文件类型	

								ftpClient.connect(Ip); //连接ftp服务器
								boolean bOkLogin= ftpClient.login(ftp_uId, ftp_pwd ); //登录ftp服务器
								if (bOkLogin==false) 
								{
									WriteFTPExcelLog("\r\n*********" + "FTP登錄失敗:電商EXCEL導入格式編碼=" +orderFormatNo +",電商平台="+ecPlatformNo + "************\r\n");
								}

								try
								{
									ftpClient.enterLocalPassiveMode();//设置ftp 模式，有被动模式和活动模式，这里设置为被动模式
									FTPFile [] files = ftpClient.listFiles(workFile);
									for (int f = 0; f < files.length; f++)
									{
										// 下载Ftp文件到本地，用于读取excel 文件的内容
										//								    	String localFilePath = "D:\\Download\\";
										String localFilePath = System.getProperty("catalina.home")+"\\webapps\\EC\\"+ecPlatformNo+"\\import\\"+fileSuffix+"\\";
										String fileName = files[f].getName();
										File localFile = new File(localFilePath + fileName);//设置本地下载的目录
										//只过滤excel文件
										boolean flag1 = false ;
										boolean flag2 = false ;
										flag1 =  localFile.getName().toLowerCase().endsWith(".xls");
										flag2 =  localFile.getName().toLowerCase().endsWith(".xlsx");
										if(flag1 || flag2)
										{ //如果当前文件是 excel 文件
											ftpClient.changeWorkingDirectory(workFile);  
											File fileparent = localFile.getParentFile();//本地下载目录下的文件夹，如果不存在则创建
											if (!fileparent.exists())
											{
												fileparent.mkdirs();
											}
											OutputStream os = new FileOutputStream(localFile);//输出到本地文件流
											ftpClient.retrieveFile(files[f].getName(), os);//下载文件到本地
											os.close();
											// 下载到本地完成，之后要进行excel导入到OC_order 

											InputStream inputStream = new FileInputStream(localFilePath+fileName);
											Workbook workbook = Workbook.getWorkbook(inputStream);
											Sheet sheet = workbook.getSheet(0);
											int rows = sheet.getRows();
											int columns = sheet.getColumns();

											List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();		
											//Cell[] headRow = sheet.getRow(5);

											int detailItem = 1;  // 子表item
											String startLine = map.get("STARTLINE").toString();
											int startIndex = Integer.parseInt(startLine);
											// 循环行 ， 从第七行开始， 前面几行为固定行
											// 得到 标题行，用于和 导入格式中的 columnName 做比较
											Cell[] headRow = sheet.getRow(startIndex - 1);

											int qtyIndex = 0;
											int amtIndex = 0;
											int pluNameIndex = 0;

											for (int i = 0; i < columns; i++) 
											{
												if (headRow[i].getContents().equals("數量")) 
												{
													qtyIndex = i;
												}
												if (headRow[i].getContents().equals("金額小計")) 
												{
													amtIndex = i;
												}
												if (headRow[i].getContents().equals("商品名稱")) 
												{
													pluNameIndex = i;
												}

											}

											// 最少四行为一单，用 isNext 来区分是否是新单
											boolean isNext = false;
											// 定義當前行是商品、折扣、運費
											boolean isPlu = false;
											boolean isAgio = false;
											boolean isTransFee = false;

											//OC_ORDER
											ArrayList<String> columnsFName = new ArrayList<String>();
											ArrayList<String> columnsFValue = new ArrayList<String>();

											//OC_ORDER_DETAIl
											ArrayList<String> columnsFName2 = new ArrayList<String>();
											ArrayList<String> columnsFValue2 = new ArrayList<String>();

											//OC_ORDER_AGIO
											ArrayList<String> columnsFName3 = new ArrayList<String>();
											ArrayList<String> columnsFValue3 = new ArrayList<String>();

											//OC_ORDER_PAY
											ArrayList<String> columnsFName4 = new ArrayList<String>();
											ArrayList<String> columnsFValue4 = new ArrayList<String>();


											String price = "0";
											String pluAmt = "0";
											String pluDisc = "0";
											String pluQty = "0";
											String agioAmt = "0";
											String orderNo = "";
											String contMan = "";
											String contTel = "";
											String totAmt = "0";
											int payRowIndex = -1;
											int payColIndex = -1;

											String memberGet = "0"; // 0不抓取， 1抓取， 默认给0

											// i 是行号 , j 是列号
											for (int i = startIndex; i < rows; i++) 
											{

												Cell cVal = sheet.getCell(4, i);// 固定第五列存在 "總計" 這兩個字，
												// 若該行為總計， 則下一行為空
												String ss = cVal.getContents();

												String order_payName = "";
												String payCode = "";
												String payCodeERP = "";
												String payNameERP = "";
												String order_payCode = "";

												boolean isTotal = false;

												if (ss.equals("總計")) 
												{
													totAmt = sheet.getCell(amtIndex, i).getContents();
													isTotal = true;
												}

												if (isNext == false) 
												{
													// 定義當前行是商品、折扣、運費
													isPlu = false;
													isAgio = false;
													isTransFee = false;

													Cell[] rowValue = sheet.getRow(i);
													boolean qtyIsMax = false;
													boolean amtIsMax = false;

													String promName = "";
													String qty = rowValue[qtyIndex].getContents();
													String amtStr = rowValue[amtIndex].getContents();

													if(qty.isEmpty()){
														qty = "0";
													}
													if(amtStr.isEmpty()){
														amtStr = "0";
													}

													if (!qty.isEmpty() && Integer.parseInt(qty) > 0) 
													{
														qtyIsMax = true;
													} 
													else 
													{
														qtyIsMax = false;
													}

													if (!amtStr.isEmpty() && Integer.parseInt(amtStr) > 0 ) 
													{
														amtIsMax = true;
													} 
													else 
													{
														amtIsMax = false;
													}

													if (qtyIsMax && amtIsMax) 
													{
														isPlu = true;
														payRowIndex = i;

													} 
													else if (qtyIsMax == false && amtIsMax == false && !amtStr.isEmpty() && amtStr.length() > 0 ) 
													{
														isAgio = true;
														pluDisc = amtStr;

													} else if (qtyIsMax == false && amtIsMax == true && isTotal == false ) 
													{ // 這個很特殊， 必須加上當前行不爲 “總計”的驗證
														isTransFee = true;
														promName = rowValue[pluNameIndex].getContents();
													}
													else
													{
														isPlu = false;
														isAgio = false;
														isTransFee = false;
													}

													int rowColumns = sheet.getRow(i).length;
													memberGet = formatDatas.get(0).get("MEMBERGET").toString();

													if (isPlu) 
													{
														columnsFName.add("EID");
														columnsFName.add("CUSTOMERNO");
														columnsFName.add("ORGANIZATIONNO");
														columnsFName.add("SHOPID");
														columnsFName.add("SHOPNAME");
														columnsFName.add("LOAD_DOCTYPE");
														columnsFName.add("STATUS");
														columnsFName.add("REFUNDSTATUS");
														columnsFName.add("CREATE_DATETIME");
														columnsFName.add("ECCUSTOMERNO");

														columnsFValue.add(eId);
														columnsFValue.add(" ");
														columnsFValue.add(shopId);
														columnsFValue.add(shopId);
														columnsFValue.add(shopName);
														columnsFValue.add(ecPlatformNo);
														columnsFValue.add("2"); // 2 已接单
														columnsFValue.add("1"); // 退货退订状态 ： 1 未申请
														columnsFValue.add(sDate+sTime);
														columnsFValue.add(eccustomerno);

														columnsFName2.add("EID");
														columnsFName2.add("CUSTOMERNO");
														columnsFName2.add("ORGANIZATIONNO");
														columnsFName2.add("SHOPID");
														columnsFName2.add("LOAD_DOCTYPE");

														columnsFValue2.add(eId);
														columnsFValue2.add(" ");
														columnsFValue2.add(shopId);
														columnsFValue2.add(shopId);
														columnsFValue2.add(ecPlatformNo);

														BigDecimal amtBD = new BigDecimal(0);
														BigDecimal qtyBD = new BigDecimal(0);
														amtBD = new BigDecimal(amtStr);
														qtyBD = new BigDecimal(qty);
														pluAmt = amtBD.toString();
														pluQty = qtyBD.toString();
														// amt / qty 就是單價， 保留兩位小數
														BigDecimal priceBD = amtBD.divide(qtyBD, 2, RoundingMode.HALF_UP);
														price = priceBD.toString();

													}
													if (isTransFee) 
													{

														columnsFName.add("MEMO");
														columnsFValue.add(promName);

														columnsFName.add("SHIPFEE");
														columnsFValue.add(amtStr);

														columnsFName.add("TOTSHIPFEE");
														columnsFValue.add(amtStr);
													}
													if (isAgio) 
													{

														columnsFName3.add("EID");
														columnsFName3.add("CUSTOMERNO");
														columnsFName3.add("ORGANIZATIONNO");
														columnsFName3.add("SHOPID");
														columnsFName3.add("ORDERNO");
														columnsFName3.add("ITEM");
														columnsFName3.add("PROMNAME");
														columnsFName3.add("AGIOAMT");
														columnsFName3.add("LOAD_DOCTYPE");

														columnsFValue3.add(eId);
														columnsFValue3.add(" ");
														columnsFValue3.add(shopId);
														columnsFValue3.add(shopId);
														columnsFValue3.add(orderNo);
														columnsFValue3.add("1");

														int amtInt = 0;
														if (amtStr.trim().equals("") || amtStr == null) 
														{
															amtInt = 0;
														}
														else
														{
															amtInt = Math.abs(Integer.parseInt(amtStr));
														}
														agioAmt = amtInt + "";
														pluDisc = amtInt + "";

														columnsFValue3.add(promName);
														columnsFValue3.add(agioAmt);
														columnsFValue3.add(ecPlatformNo);
													}

													for (Map<String, Object> oneData : lstDetailFormatDatas) 
													{
														String item = map.get("ITEM").toString();
														String columnName = map.get("COLUMNNAME").toString();
														String fieldName = map.get("FIELDNAME").toString().toUpperCase(); // 数据库列名，转换为大写
														String tableName = map.get("TABLENAME").toString().toUpperCase(); // 表名，转换为大写

														if(columnName.equals("付款方式")){
															payColIndex = Integer.parseInt(item) - 1 ;
														}

														for (int j = 0; j < rowColumns; j++) 
														{
															String index = (j + 1) + "";
															String headRowName = headRow[j].getContents();

															// 需要加個判斷， 控制每個商品行在第一行上
															// 當前行為商品行
															if (isPlu) 
															{

																if (index.equals(item) && headRowName.equals(columnName)) 
																{
																	if (tableName.equals("OC_ORDER_DETAIL")) 
																	{
																		columnsFName2.add(fieldName);
																		String fieldValue = rowValue[j].getContents();
																		columnsFValue2.add(fieldValue);
																	} 
																	else if (tableName.equals("OC_ORDER")) 
																	{
																		columnsFName.add(fieldName);
																		String fieldValue = rowValue[j].getContents();

																		// 日期需要转换格式， 传的日期是 2010/03/13
																		// ，需要转换为 20190313 形式
																		if (fieldName.equals("SDATE")) 
																		{
																			if (!fieldValue.isEmpty()) 
																			{
																				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
																				Date date = sdf.parse(fieldValue);
																				sdf = new SimpleDateFormat("yyyyMMdd");
																				fieldValue = sdf.format(date);
																			}
																		}
																		columnsFValue.add(fieldValue);

																		if (fieldName.equals("ORDERNO")) 
																		{
																			orderNo = fieldValue;
																		}

																		// 获取订购人，订购人电话信息
																		if (fieldName.equals("CONTMAN")) 
																		{
																			contMan = fieldValue;
																		}
																		if (fieldName.equals("CONTTEL")) 
																		{
																			contTel = fieldValue;
																		}

																	}
																}



															}

															isPlu = false;
															isAgio = false;
															isTransFee = false;

														}

													}

												} 
												else 
												{

													columnsFName = new ArrayList<String>();
													columnsFValue = new ArrayList<String>();

													columnsFName2 = new ArrayList<String>();
													columnsFValue2 = new ArrayList<String>();

													columnsFName3 = new ArrayList<String>();
													columnsFValue3 = new ArrayList<String>();

													columnsFName4 = new ArrayList<String>();
													columnsFValue4 = new ArrayList<String>();
												}

												String nextValue = "";
												if (i < rows - 1) 
												{
													Cell[] memoCol = sheet.getRow(i + 1);
													nextValue = memoCol[0].getContents().trim();

													if (nextValue.trim().isEmpty() == false || (i + 2 == rows)) 
													{
														isNext = true;

														columnsFName2.add("ORDERNO");
														columnsFValue2.add(orderNo);
														columnsFName2.add("PRICE");
														columnsFValue2.add(price);

														columnsFName2.add("DISC");
														columnsFValue2.add(agioAmt);

														BigDecimal totAmtBD = new BigDecimal(totAmt);
														BigDecimal agioAmtBD = new BigDecimal(agioAmt);
														BigDecimal pluAmtBD = new BigDecimal(pluAmt);

														BigDecimal oldAmt = totAmtBD.add(agioAmtBD); //加法
														BigDecimal pluAfterAmt = pluAmtBD.subtract(agioAmtBD); //减法

														columnsFName2.add("AMT");
														columnsFValue2.add(pluAfterAmt.toString());

														columnsFName.add("TOT_OLDAMT"); // 订单原价：不计算折扣额
														// 商品总价+餐盒费+配送费
														columnsFValue.add(oldAmt.toString());


														columnsFName.add("TOT_QTY"); 
														columnsFValue.add(pluQty);

														columnsFName.add("TOT_AMT"); // 实际支付金额
														columnsFValue.add(totAmt);
														columnsFName.add("INCOMEAMT"); // 商家实收金额
														columnsFValue.add(totAmt);
														columnsFName.add("TOT_DISC"); // 订单折扣总额， 商家优惠金额
														// 和 平台优惠金额
														// PCHOME无法区分
														columnsFValue.add(agioAmt);

														columnsFName.add("PAYSTATUS"); // 支付状态 ，默认给3，
														// 已付清
														columnsFValue.add("3");
														columnsFName.add("PAYAMT"); // 已付金额
														columnsFValue.add(totAmt);

														// pchome 该判断放在商品行验证中，得到该行的索引， 从而得到 付款方式的值
														//														String tot = sheet.getCell(4, i).getContents();
														//														if (columnName.equals("付款方式")) {

														// 索引 == -1，说明表格中没有付款档信息
														if(payColIndex != -1 && payRowIndex != -1)
														{
															order_payName = sheet.getCell(payColIndex, payRowIndex).getContents().trim();

															//过滤付款方式映射
															Map<String, Object> map_condition = new HashMap<String, Object>();
															map_condition.put("ORDER_PAYCODE",order_payName );//这里采用包含的关系,因为那些信用卡和银行太多了		
															List<Map<String, Object>> getQPAY=MapDistinct.getWhereMap(payData,map_condition,false,2);	

															if (getQPAY==null || getQPAY.size()==0) 
															{
																map_condition = new HashMap<String, Object>();
																map_condition.put("ORDER_PAYCODE", "ALL");		
																getQPAY=MapDistinct.getWhereMap(payData,map_condition,false);	
																if (getQPAY!=null && getQPAY.size()>0) 
																{
																	payCode=getQPAY.get(0).get("PAYCODE").toString();
																	payNameERP=getQPAY.get(0).get("PAYNAME").toString();
																	payCodeERP=getQPAY.get(0).get("PAYCODEERP").toString();
																	order_payCode=getQPAY.get(0).get("ORDER_PAYCODE").toString();
																}
																else 
																{
																	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,
																			order_payName + ":不存在该支付方式映射信息");
																}
															}
															else 
															{
																payCode=getQPAY.get(0).get("PAYCODE").toString();
																payNameERP=getQPAY.get(0).get("PAYNAME").toString();
																payCodeERP=getQPAY.get(0).get("PAYCODEERP").toString();
																order_payCode=getQPAY.get(0).get("ORDER_PAYCODE").toString();
															}

															columnsFName4.add("EID");
															columnsFValue4.add(eId);

															columnsFName4.add("CUSTOMERNO");
															columnsFValue4.add(" ");

															columnsFName4.add("ORGANIZATIONNO");
															columnsFValue4.add(shopId);

															columnsFName4.add("SHOPID");
															columnsFValue4.add(shopId);

															columnsFName4.add("ORDERNO");
															columnsFValue4.add(orderNo);

															columnsFName4.add("ITEM");
															columnsFValue4.add("1");

															columnsFName4.add("LOAD_DOCTYPE");
															columnsFValue4.add(ecPlatformNo);

															columnsFName4.add("PAYCODE");
															columnsFValue4.add(payCode);

															columnsFName4.add("PAYNAME");
															columnsFValue4.add(payNameERP);

															columnsFName4.add("PAYCODEERP");
															columnsFValue4.add(payCodeERP);

															columnsFName4.add("ORDER_PAYCODE"); // 是否平台支付
															columnsFValue4.add(order_payCode);
															//								}

															columnsFName4.add("PAY");
															columnsFValue4.add(totAmt);

															columnsFName4.add("ISORDERPAY"); //是否定金
															columnsFValue4.add("N");

															columnsFName4.add("ISONLINEPAY"); // 是否平台支付
															columnsFValue4.add("Y");

															columnsFName4.add("RCPAY"); // 已拆单金额
															columnsFValue4.add("0");																	

														}

														DataValue[] columnsVal = new DataValue[columnsFName.size()];
														int insColCt = columnsFName.size();
														String[] columns1 = new String[insColCt];
														DataValue[] insValue = new DataValue[insColCt];
														// 依照傳入參數組譯要insert的欄位與數值；
														int ENO1 = 0;
														for (int k = 0; k < columnsVal.length; k++) 
														{
															String keyValue = columnsFName.get(k).toString();
															if (keyValue != null) 
															{
																columnsVal[k] = new DataValue(keyValue, Types.VARCHAR);
															} 
															else 
															{
																columnsVal[k] = null;
															}

															if (columnsVal[k] != null) 
															{
																columns1[ENO1] = columnsFName.get(k).toString();
																String fValue = columnsFValue.get(k).toString();
																columnsVal[k] = new DataValue(fValue, Types.VARCHAR);
																insValue[ENO1] = columnsVal[k];
																ENO1++;
																if (ENO1 >= insValue.length)
																	break;
															}
														}

														InsBean ib1 = new InsBean("OC_ORDER", columns1);
														ib1.addValues(insValue);
														lstData.add(new DataProcessBean(ib1));	

														DataValue[] columnsVal2 = new DataValue[columnsFName2.size()];
														int insColCt2 = columnsFName2.size();
														String[] columns2 = new String[insColCt2];
														DataValue[] insValue2 = new DataValue[insColCt2];
														// 依照傳入參數組譯要insert的欄位與數值；
														int ENO2 = 0;
														for (int k = 0; k < columnsVal2.length; k++) 
														{
															String keyValue = columnsFName2.get(k).toString();
															if (keyValue != null) 
															{
																columnsVal2[k] = new DataValue(keyValue, Types.VARCHAR);
															} 
															else 
															{
																columnsVal2[k] = null;
															}

															if (columnsVal2[k] != null) 
															{
																columns2[ENO2] = columnsFName2.get(k).toString();
																String fValue = columnsFValue2.get(k).toString();
																columnsVal2[k] = new DataValue(fValue, Types.VARCHAR);
																insValue2[ENO2] = columnsVal2[k];
																ENO2++;
																if (ENO2 >= insValue2.length)
																	break;
															}
														}

														InsBean ib2 = new InsBean("OC_ORDER_DETAIL", columns2);
														ib2.addValues(insValue2);
														lstData.add(new DataProcessBean(ib2));	

														if (columnsFName3.size() > 0) 
														{

															DataValue[] columnsVal3 = new DataValue[columnsFName3.size()];
															int insColCt3 = columnsFName3.size();
															String[] columns3 = new String[insColCt3];
															DataValue[] insValue3 = new DataValue[insColCt3];
															// 依照傳入參數組譯要insert的欄位與數值；
															int ENO3 = 0;
															for (int k = 0; k < columnsVal3.length; k++) 
															{
																String keyValue = columnsFName3.get(k).toString();
																if (keyValue != null) 
																{
																	columnsVal3[k] = new DataValue(keyValue, Types.VARCHAR);
																} 
																else 
																{
																	columnsVal3[k] = null;
																}

																if (columnsVal3[k] != null) 
																{
																	columns3[ENO3] = columnsFName3.get(k).toString();
																	String fValue = columnsFValue3.get(k).toString();
																	columnsVal3[k] = new DataValue(fValue, Types.VARCHAR);
																	insValue3[ENO3] = columnsVal3[k];
																	ENO3++;
																	if (ENO3 >= insValue3.length)
																		break;
																}
															}

															InsBean ib3 = new InsBean("OC_ORDER_AGIO", columns3);
															ib3.addValues(insValue3);
															lstData.add(new DataProcessBean(ib3));	
														}


														// 插入付款档， OC_ORDER_PAY
														if (columnsFName4.size() > 0) 
														{

															DataValue[] columnsVal4 = new DataValue[columnsFName4.size()];
															int insColCt4 = columnsFName4.size();
															String[] columns4 = new String[insColCt4];
															DataValue[] insValue4 = new DataValue[insColCt4];
															// 依照傳入參數組譯要insert的欄位與數值；
															int ENO4 = 0;
															for (int k = 0; k < columnsVal4.length; k++) 
															{
																String keyValue = columnsFName4.get(k).toString();
																if (keyValue != null) 
																{
																	columnsVal4[k] = new DataValue(keyValue, Types.VARCHAR);
																} 
																else 
																{
																	columnsVal4[k] = null;
																}

																if (columnsVal4[k] != null) 
																{
																	columns4[ENO4] = columnsFName4.get(k).toString();
																	String fValue = columnsFValue4.get(k).toString();
																	columnsVal4[k] = new DataValue(fValue, Types.VARCHAR);
																	insValue4[ENO4] = columnsVal4[k];
																	ENO4++;
																	if (ENO4 >= insValue4.length)
																		break;
																}
															}

															InsBean ib4 = new InsBean("OC_ORDER_PAY", columns4);
															ib4.addValues(insValue4);
															lstData.add(new DataProcessBean(ib4));	
														}


														columnsFName = new ArrayList<String>();
														columnsFValue = new ArrayList<String>();

														columnsFName2 = new ArrayList<String>();
														columnsFValue2 = new ArrayList<String>();

														columnsFName3 = new ArrayList<String>();
														columnsFValue3 = new ArrayList<String>();

														columnsFName4 = new ArrayList<String>();
														columnsFValue4 = new ArrayList<String>();

														isNext = false;
														price = "0";
														pluAmt = "0";
														pluDisc = "0";
														pluQty = "0";

														orderNo = "";
														contMan = "";
														contTel = "";
														totAmt = "0";
														payRowIndex = -1;
														payColIndex = -1;
														agioAmt = "0";

														// 定義當前行是商品、折扣、運費
														isPlu = false;
														isAgio = false;
														isTransFee = false;

													} 
													else 
													{
														isNext = false;
													}
												}

											}
											StaticInfo.dao.useTransactionProcessData(lstData);
											String pathname = workFile + "/bak/";
											File bakFile = new File(pathname);
											if (bakFile.exists()) 
											{
												if (bakFile.isDirectory()) 
												{
													//System.out.println("目录已存在");
												} 
												else 
												{
													//System.out.println("目录下文件已存在, 不盘它了 ...");
												}
											} 
											else 
											{
												//System.out.println("目录不存在, 盘它 ...");
												//boolean make = false;
												//make = bakFile.mkdir();
											}

											// 上传下载的文件到备份位置
											ftpClient.makeDirectory(pathname);
											ftpClient.changeWorkingDirectory(pathname);
											FileInputStream upInputStream = new FileInputStream( new File(localFilePath + fileName));
											ftpClient.storeFile(fileName, upInputStream);
											upInputStream.close();

											try 
											{
												// 删除ftp下载地址的文件
												ftpClient.changeWorkingDirectory(workFile);
												ftpClient.deleteFile(fileName);

												File myDelFile = new File(localFilePath + fileName);
												myDelFile.delete();

												//System.out.println("本地缓存Excel文件删除成功!!!");
											} 
											catch (Exception e) 
											{

											}
										}

									}

									ftpClient.logout();

								}
								catch (Exception e) 
								{

								}


							}


							//***********PCHOME电商平台   normal 文件夹下 
							else if(ecPlatformNo.equals("pchome") && fileSuffix.equals("normal"))
							{

								// 登录ftp，获取事件
								FTPClient ftpClient = ftpInterface.ftp(Ip, ftp_uId, ftp_pwd);//这里是ip，用户名，密码

								ftpClient.setControlEncoding("GBK"); // 中文支持
								ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);//设置文件类型


								ftpClient.connect(Ip); //连接ftp服务器
								boolean bOkLogin=ftpClient.login(ftp_uId, ftp_pwd ); //登录ftp服务器

								if (bOkLogin==false) 
								{
									WriteFTPExcelLog("\r\n*********" + "FTP登錄失敗：電商EXCEL導入格式編碼=" +orderFormatNo +",電商平台="+ecPlatformNo + "************\r\n");
								}
								try
								{
									ftpClient.enterLocalPassiveMode();//设置ftp 模式，有被动模式和活动模式，这里设置为被动模式
									FTPFile [] files = ftpClient.listFiles(workFile);
									for (int f = 0; f < files.length; f++)
									{
										// 下载Ftp文件到本地，用于读取excel 文件的内容
										//								    	String localFilePath = "D:\\Download\\";
										String localFilePath = System.getProperty("catalina.home")+"\\webapps\\EC\\"+ecPlatformNo+"\\import\\"+fileSuffix+"\\";
										String fileName = files[f].getName();
										File localFile = new File(localFilePath + fileName);//设置本地下载的目录
										//只过滤excel文件
										boolean flag1 = false ;
										boolean flag2 = false ;
										flag1 =  localFile.getName().toLowerCase().endsWith(".xls");
										flag2 =  localFile.getName().toLowerCase().endsWith(".xlsx");
										if(flag1 || flag2)
										{ //如果当前文件是 excel 文件
											ftpClient.changeWorkingDirectory(workFile);  
											File fileparent = localFile.getParentFile();//本地下载目录下的文件夹，如果不存在则创建
											if (!fileparent.exists())
											{
												fileparent.mkdirs();
											}
											OutputStream os = new FileOutputStream(localFile);//输出到本地文件流
											ftpClient.retrieveFile(files[f].getName(), os);//下载文件到本地
											os.close();
											// 下载到本地完成，之后要进行excel导入到OC_order 

											InputStream inputStream = new FileInputStream(localFilePath+fileName);
											Workbook workbook = Workbook.getWorkbook(inputStream);
											Sheet sheet = workbook.getSheet(0);
											int rows = sheet.getRows();
											int columns = sheet.getColumns();

											List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();		
											//Cell[] headRow = sheet.getRow(5);

											int detailItem = 1;  // 子表item
											String startLine = formatDatas.get(0).get("STARTLINE").toString();
											int startIndex = Integer.parseInt(startLine);
											// 循环行 ， 从第七行开始， 前面几行为固定行
											// 得到 标题行，用于和 导入格式中的 columnName 做比较
											Cell[] headRow = sheet.getRow(startIndex - 1);


											for (int i = startIndex; i < rows; i++) 
											{
												int num = 0; // 这里声明num，用来记录主表插入的次数， 一个订单多个商品，主表数据只能
												// 插入一次

												ArrayList<String> columnsFName = new ArrayList<String>();
												ArrayList<String> columnsFValue = new ArrayList<String>();

												columnsFName.add("EID");
												columnsFName.add("CUSTOMERNO");
												columnsFName.add("ORGANIZATIONNO");
												columnsFName.add("SHOPID");
												columnsFName.add("SHOPNAME");
												columnsFName.add("LOAD_DOCTYPE");
												columnsFName.add("STATUS");
												columnsFName.add("REFUNDSTATUS");
												columnsFName.add("CREATE_DATETIME");
												columnsFName.add("ECCUSTOMERNO");

												columnsFValue.add(eId);
												columnsFValue.add(" ");
												columnsFValue.add(shopId);
												columnsFValue.add(shopId);
												columnsFValue.add(shopName);
												columnsFValue.add(ecPlatformNo);
												columnsFValue.add("2"); // 2 已接单
												columnsFValue.add("1"); // 退货退订状态 ： 1 未申请
												columnsFValue.add(sDate+sTime);
												columnsFValue.add(eccustomerno);

												ArrayList<String> columnsFName2 = new ArrayList<String>();
												ArrayList<String> columnsFValue2 = new ArrayList<String>();

												columnsFName2.add("EID");
												columnsFName2.add("CUSTOMERNO");
												columnsFName2.add("ORGANIZATIONNO");
												columnsFName2.add("SHOPID");
												columnsFName2.add("LOAD_DOCTYPE");

												columnsFValue2.add(eId);
												columnsFValue2.add(" ");
												columnsFValue2.add(shopId);
												columnsFValue2.add(shopId);
												columnsFValue2.add(ecPlatformNo);

												Cell[] rowValue = sheet.getRow(i);
												String vTest = rowValue[0].getContents();
												// pchome 的导入格式 不设置备注字段， 程序中控制即可
												String memo = "";
												String nextValue = "";
												// 防止excel 出现第一列不为空， 且值不是需要插入到表中的值
												if (i + 1 < rows) 
												{
													Cell[] memoCol = sheet.getRow(i + 1);
													nextValue = memoCol[0].getContents().trim();
													if (nextValue.trim().equals("订单备注") || nextValue.trim().equals("訂單備註")) 
													{
														memo = memoCol[1].getContents().trim();
													}
												}

												Cell[] preCol = sheet.getRow(i - 1); // 获取上一行 ， 用于验证 ：一
												// :一笔订单只有一个商品；
												// 二：
												// 一笔订单多个商品的最后一个
												String preValue = preCol[0].getContents().trim();
												columnsFName.add("MEMO");
												columnsFValue.add(memo);
												String orderNo = "";
												String contMan = "";
												String contTel = "";
												String memberGet = "0"; // 0不抓取， 1抓取， 默认给0
												memberGet = formatDatas.get(0).get("MEMBERGET").toString();
												if (!vTest.trim().equals("") && !vTest.trim().equals("订单备注") && !vTest.trim().equals("訂單備註")) 
												{ // 当前单元格不为空，且单元格不是“订单备注”，说明是订单行
													// ，取该行的值
													if (vTest == nextValue) 
													{ // 当该单元格的值 与
														// 下一个单元格的值一样的时候， 说明是
														// 同一个订单号， 有多个商品

														for (Map<String, Object> oneData : formatDatas) 
														{
															String item = oneData.get("ITEM").toString();
															String columnName = oneData.get("COLUMNNAME").toString(); // excel列名
															String fieldName = oneData.get("FIELDNAME").toString().toUpperCase(); // 数据库列名，转换为大写
															String tableName = oneData.get("TABLENAME").toString().toUpperCase(); // 表名

															if (orderNo.equals(preValue)) 
															{
																for (int j = 0; j < columns; j++) 
																{
																	String index = (j + 1) + "";
																	String headRowName = headRow[j].getContents();
																	String fieldValue = "";
																	fieldValue = rowValue[j].getContents();
																	if (index.equals(item) && headRowName.equals(columnName)
																			&& tableName.equals("OC_ORDER_DETAIL")) 
																	{
																		columnsFName2.add(fieldName);
																		columnsFValue2.add(fieldValue);
																	}
																}
																num = num + 1;
															}

															for (int j = 0; j < columns; j++) 
															{
																String index = (j + 1) + "";
																String headRowName = headRow[j].getContents();

																if (index.equals(item) && headRowName.equals(columnName)) {
																	if (tableName.equals("OC_ORDERDETAIL")) 
																	{
																		columnsFName2.add(fieldName);
																		// columnsName1 =
																		// insert(columnsName1,
																		// fieldName);
																		String fieldValue = rowValue[j].getContents();
																		columnsFValue2.add(fieldValue);
																	} 
																	else if (tableName.equals("OC_ORDER")) 
																	{
																		columnsFName.add(fieldName);
																		String fieldValue = rowValue[j].getContents();
																		columnsFValue.add(fieldValue);
																		if (fieldName.equals("ORDERNO")) 
																		{
																			orderNo = fieldValue;
																		}

																		// 获取订购人，订购人电话信息
																		if (fieldName.equals("CONTMAN")) 
																		{
																			contMan = fieldValue;
																		}
																		if (fieldName.equals("CONTTEL")) 
																		{
																			contTel = fieldValue;
																		}

																	}
																}

															}

														}
														columnsFName2.add("ITEM");
														columnsFValue2.add(detailItem + "");
														columnsFName2.add("ORDERNO");
														columnsFValue2.add(orderNo);

														detailItem = detailItem + 1;
														if (num == 0) 
														{ // 主表数据只插入一次
															DataValue[] columnsVal = new DataValue[columnsFName.size()];
															int insColCt = columnsFName.size();
															String[] columns1 = new String[insColCt];
															DataValue[] insValue = new DataValue[insColCt];
															// 依照傳入參數組譯要insert的欄位與數值；
															int ENO1 = 0;
															for (int k = 0; k < columnsVal.length; k++) 
															{
																String keyValue = columnsFName.get(k).toString();
																if (keyValue != null) 
																{
																	columnsVal[k] = new DataValue(keyValue, Types.VARCHAR);
																} 
																else 
																{
																	columnsVal[k] = null;
																}

																if (columnsVal[k] != null) 
																{
																	columns1[ENO1] = columnsFName.get(k).toString();
																	String fValue = columnsFValue.get(k).toString();
																	columnsVal[k] = new DataValue(fValue, Types.VARCHAR);
																	insValue[ENO1] = columnsVal[k];
																	ENO1++;
																	if (ENO1 >= insValue.length)
																		break;
																}
															}

															InsBean ib1 = new InsBean("OC_ORDER", columns1);
															ib1.addValues(insValue);

															lstData.add(new DataProcessBean(ib1));	

															num = num + 1;
														}

														DataValue[] columnsVal2 = new DataValue[columnsFName2.size()];
														int insColCt2 = columnsFName2.size();
														String[] columns2 = new String[insColCt2];
														DataValue[] insValue2 = new DataValue[insColCt2];
														// 依照傳入參數組譯要insert的欄位與數值；
														int ENO1 = 0;
														for (int k = 0; k < columnsVal2.length; k++) 
														{
															String keyValue = columnsFName2.get(k).toString();
															if (keyValue != null) 
															{
																columnsVal2[k] = new DataValue(keyValue, Types.VARCHAR);
															} 
															else 
															{
																columnsVal2[k] = null;
															}

															if (columnsVal2[k] != null) 
															{
																columns2[ENO1] = columnsFName2.get(k).toString();
																String fValue = columnsFValue2.get(k).toString();
																columnsVal2[k] = new DataValue(fValue, Types.VARCHAR);
																insValue2[ENO1] = columnsVal2[k];
																ENO1++;
																if (ENO1 >= insValue2.length)
																	break;
															}
														}

														InsBean ib2 = new InsBean("OC_ORDER_DETAIL", columns2);
														ib2.addValues(insValue2);
														lstData.add(new DataProcessBean(ib2));	

													} 
													else 
													{
														/**
														 * 当前单元格的值与 下方单元格的值不同 ，有两种可能： 一：一个订单只有一个商品； 二
														 * ：该行是多个商品的最后一个
														 */
														if (vTest.equals(preValue)) 
														{
															//															// 如果当前行与上一行相同，
															//																						// 则当前行是
															//																						// 多个商品的最后一个
															//															for (Map<String, Object> oneData : getDatas) {
															//																String item = oneData.get("ITEM").toString();
															//																String columnName = oneData.get("COLUMNNAME").toString();
															//																String fieldName = oneData.get("FIELDNAME").toString().toUpperCase(); // 数据库列名，转换为大写
															//																String tableName = oneData.get("TABLENAME").toString().toUpperCase(); // 表名，转换为大写
															//																for (int j = 0; j < columns; j++) {
															//																	String index = (j + 1) + "";
															//																	String headRowName = headRow[j].getContents();
															//																	if (index.equals(item) && headRowName.equals(columnName)) {
															//																		if (j == 0 || tableName.equals("OC_ORDERDETAIL")) {
															//																			columnsFName2.add(fieldName);
															//																			String fieldValue = rowValue[j].getContents();
															//																			columnsFValue2.add(fieldValue);
															//																		}
															//																	}
															//																}
															//
															//															}
															//															columnsFName2.add("ITEM");
															//															columnsFValue2.add(detailItem + "");
															//
															//															DataValue[] columnsVal2 = new DataValue[columnsFName2.size()];
															//															int insColCt2 = columnsFName2.size();
															//															String[] columns2 = new String[insColCt2];
															//															DataValue[] insValue2 = new DataValue[insColCt2];
															//															// 依照傳入參數組譯要insert的欄位與數值；
															//															int ENO2 = 0;
															//															for (int k = 0; k < columnsVal2.length; k++) {
															//																String keyValue = columnsFName2.get(k).toString();
															//																if (keyValue != null) {
															//																	columnsVal2[k] = new DataValue(keyValue, Types.VARCHAR);
															//																} else {
															//																	columnsVal2[k] = null;
															//																}
															//
															//																if (columnsVal2[k] != null) {
															//																	columns2[ENO2] = columnsFName2.get(k).toString();
															//																	String fValue = columnsFValue2.get(k).toString();
															//																	columnsVal2[k] = new DataValue(fValue, Types.VARCHAR);
															//																	insValue2[ENO2] = columnsVal2[k];
															//																	ENO2++;
															//																	if (ENO2 >= insValue2.length)
															//																		break;
															//																}
															//															}
															//
															//															InsBean ib2 = new InsBean("OC_ORDER_DETAIL", columns2);
															//															ib2.addValues(insValue2);
															//															this.addProcessData(new DataProcessBean(ib2));
															//															detailItem = 1;

														} 
														else 
														{
															detailItem = 1;

															for (Map<String, Object> oneData : formatDatas) 
															{
																String item = oneData.get("ITEM").toString();
																String columnName = oneData.get("COLUMNNAME").toString();
																String fieldName = oneData.get("FIELDNAME").toString().toUpperCase(); // 数据库列名，转换为大写
																String tableName = oneData.get("TABLENAME").toString().toUpperCase(); // 表名，转换为大写

																for (int j = 0; j < columns; j++) 
																{
																	String index = (j + 1) + "";
																	String headRowName = headRow[j].getContents();

																	if (index.equals(item) && headRowName.equals(columnName)) 
																	{
																		if (tableName.equals("OC_ORDERDETAIL")) 
																		{
																			// if( j == 5 || j == 6 || j
																			// == 7 || j ==8 ){
																			columnsFName2.add(fieldName);
																			String fieldValue = rowValue[j].getContents();
																			columnsFValue2.add(fieldValue);
																		} 
																		else if (tableName.equals("OC_ORDER")) 
																		{
																			columnsFName.add(fieldName);
																			String fieldValue = rowValue[j].getContents();
																			columnsFValue.add(fieldValue);
																			if (fieldName.equals("ORDERNO")) 
																			{
																				orderNo = fieldValue;
																			}
																			// 获取订购人，订购人电话信息
																			if (fieldName.equals("CONTMAN")) 
																			{
																				contMan = fieldValue;
																			}
																			if (fieldName.equals("CONTTEL")) 
																			{
																				contTel = fieldValue;
																			}

																		}
																	}
																}
															}
															columnsFName2.add("ITEM");
															columnsFValue2.add("1");
															columnsFName2.add("ORDERNO");
															columnsFValue2.add(orderNo);

															DataValue[] columnsVal = new DataValue[columnsFName.size()];
															int insColCt = columnsFName.size();
															String[] columns1 = new String[insColCt];
															DataValue[] insValue = new DataValue[insColCt];
															// 依照傳入參數組譯要insert的欄位與數值；
															int ENO1 = 0;
															for (int k = 0; k < columnsVal.length; k++) 
															{
																String keyValue = columnsFName.get(k).toString();
																if (keyValue != null) 
																{
																	columnsVal[k] = new DataValue(keyValue, Types.VARCHAR);
																} 
																else 
																{
																	columnsVal[k] = null;
																}

																if (columnsVal[k] != null) 
																{
																	columns1[ENO1] = columnsFName.get(k).toString();
																	String fValue = columnsFValue.get(k).toString();
																	columnsVal[k] = new DataValue(fValue, Types.VARCHAR);
																	insValue[ENO1] = columnsVal[k];
																	ENO1++;
																	if (ENO1 >= insValue.length)
																		break;
																}
															}

															InsBean ib1 = new InsBean("OC_ORDER", columns1);
															ib1.addValues(insValue);
															lstData.add(new DataProcessBean(ib1));	

															DataValue[] columnsVal2 = new DataValue[columnsFName2.size()];
															int insColCt2 = columnsFName2.size();
															String[] columns2 = new String[insColCt2];
															DataValue[] insValue2 = new DataValue[insColCt2];
															// 依照傳入參數組譯要insert的欄位與數值；
															int ENO2 = 0;
															for (int k = 0; k < columnsVal2.length; k++) 
															{
																String keyValue = columnsFName2.get(k).toString();
																if (keyValue != null) 
																{
																	columnsVal2[k] = new DataValue(keyValue, Types.VARCHAR);
																} 
																else 
																{
																	columnsVal2[k] = null;
																}

																if (columnsVal2[k] != null) 
																{
																	columns2[ENO2] = columnsFName2.get(k).toString();
																	String fValue = columnsFValue2.get(k).toString();
																	columnsVal2[k] = new DataValue(fValue, Types.VARCHAR);
																	insValue2[ENO2] = columnsVal2[k];
																	ENO2++;
																	if (ENO2 >= insValue2.length)
																		break;
																}
															}

															InsBean ib2 = new InsBean("OC_ORDER_DETAIL", columns2);
															ib2.addValues(insValue2);
															lstData.add(new DataProcessBean(ib2));	
														}
													} // 判断 vTest == nextValue else 结束

												}


											}

											StaticInfo.dao.useTransactionProcessData(lstData);
											String pathname = workFile + "/bak/";
											File bakFile = new File(pathname);
											if (bakFile.exists()) 
											{
												if (bakFile.isDirectory()) 
												{
													//System.out.println("目录已存在");
												} 
												else 
												{
													//System.out.println("目录下文件已存在, 不盘它了 ...");
												}
											} 
											else 
											{
												//System.out.println("目录不存在, 盘它 ...");
												//								            	boolean make = false;
												//								            	make = bakFile.mkdir();
											}

											// 上传下载的文件到备份位置
											ftpClient.makeDirectory(pathname);
											ftpClient.changeWorkingDirectory(pathname);
											FileInputStream upInputStream = new FileInputStream( new File(localFilePath + fileName));
											ftpClient.storeFile(fileName, upInputStream);
											upInputStream.close();

											try 
											{
												// 删除ftp下载地址的文件
												ftpClient.changeWorkingDirectory(workFile);
												ftpClient.deleteFile(fileName);

												File myDelFile = new File(localFilePath + fileName);
												myDelFile.delete();

												//System.out.println("本地缓存Excel文件删除成功!!!");
											} 
											catch (Exception e) 
											{

											}
										}

									}

									ftpClient.logout();

								}
								catch (Exception e) 
								{

								}

							}

							else if(ecPlatformNo.equals("yahoosuper"))
							{
								// 登录ftp，获取事件
								FTPClient ftpClient = ftpInterface.ftp(Ip, ftp_uId, ftp_pwd);//这里是ip，用户名，密码

								ftpClient.setControlEncoding("GBK"); // 中文支持
								ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);//设置文件类型								

								ftpClient.connect(Ip); //连接ftp服务器
								boolean bOkLogin=ftpClient.login(ftp_uId, ftp_pwd ); //登录ftp服务器
								if (bOkLogin==false) 
								{
									WriteFTPExcelLog("\r\n*********" + "FTP登錄失敗：電商EXCEL導入格式編碼=" +orderFormatNo +",電商平台="+ecPlatformNo + "************\r\n");
								}

								try
								{
									ftpClient.enterLocalPassiveMode();//设置ftp 模式，有被动模式和活动模式，这里设置为被动模式
									FTPFile [] files = ftpClient.listFiles(workFile);
									for (int f = 0; f < files.length; f++)
									{
										// 下载Ftp文件到本地，用于读取excel 文件的内容
										//								    	String localFilePath = "D:\\Download\\";
										String localFilePath = System.getProperty("catalina.home")+"\\webapps\\EC\\"+ecPlatformNo+"\\import\\";
										String fileName = files[f].getName();
										File localFile = new File(localFilePath + fileName);//设置本地下载的目录

										boolean flag1 = false ;
										boolean flag2 = false ;
										flag1 =  localFile.getName().toLowerCase().endsWith(".xls");
										flag2 =  localFile.getName().toLowerCase().endsWith(".xlsx");
										if(flag1 || flag2)
										{ //如果当前文件是 excel 文件
											File fileparent = localFile.getParentFile();//本地下载目录下的文件夹，如果不存在则创建
											if (!fileparent.exists())
											{
												fileparent.mkdirs();
											}
											OutputStream os = new FileOutputStream(localFile);//输出到本地文件流
											ftpClient.retrieveFile(files[f].getName(), os);//下载文件到本地
											os.close();
											// 下载到本地完成，之后要进行excel导入到OC_order 
											ftpClient.changeWorkingDirectory(workFile);  // 这里应该填客户定义的地址 


											InputStream inputStream = new FileInputStream(localFilePath+fileName);
											Workbook workbook = Workbook.getWorkbook(inputStream);
											Sheet sheet = workbook.getSheet(0);
											int rows = sheet.getRows();
											int columns = sheet.getColumns();

											Cell[] headRow = sheet.getRow(0); 
											int detailItem = 1;  // 子表item

											List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();		

											// 循环行 ， 从第二行开始， 前面为固定行
											for (int i = 1; i < rows; i++) 
											{ 
												int num = 0; // 这里声明num，用来记录主表插入的次数， 一个订单多个商品，主表数据只能 插入一次

												ArrayList<String> columnsFName = new ArrayList<String>();
												ArrayList<String> columnsFValue = new ArrayList<String>();

												ArrayList<String> columnsFName2 = new ArrayList<String>();
												ArrayList<String> columnsFValue2 = new ArrayList<String>();

												columnsFName.add("EID");
												columnsFName.add("CUSTOMERNO");
												columnsFName.add("ORGANIZATIONNO");
												columnsFName.add("SHOPID");
												columnsFName.add("SHOPNAME");
												columnsFName.add("LOAD_DOCTYPE");
												columnsFName.add("STATUS");
												columnsFName.add("REFUNDSTATUS");
												columnsFName.add("CREATE_DATETIME");
												columnsFName.add("ECCUSTOMERNO");

												columnsFValue.add(eId);
												columnsFValue.add(" ");
												columnsFValue.add(shopId);
												columnsFValue.add(shopId);
												columnsFValue.add(shopName);
												columnsFValue.add(ecPlatformNo);
												columnsFValue.add("2");
												columnsFValue.add("1"); // 退货退订状态 ： 1 未申请
												columnsFValue.add(sDate+sTime);
												columnsFValue.add(eccustomerno);

												columnsFName2.add("EID");
												columnsFName2.add("CUSTOMERNO");
												columnsFName2.add("ORGANIZATIONNO");
												columnsFName2.add("SHOPID");
												columnsFName2.add("LOAD_DOCTYPE");

												columnsFValue2.add(eId);
												columnsFValue2.add(" ");
												columnsFValue2.add(shopId);
												columnsFValue2.add(shopId);
												columnsFValue2.add(ecPlatformNo);

												Cell[] rowValue = sheet.getRow(i);

												Cell[] preCol = sheet.getRow(i - 1); // 获取上一行 ， 用于验证 ：一 :一笔订单只有一个商品； 二： 一笔订单多个商品的最后一个
												//												String preValue = preCol[0].getContents().trim();

												String orderNo = ""; 
												String contMan = "";
												String contTel = "";
												String memberGet = "0"; // 0不抓取， 1抓取， 默认给0
												memberGet = map.get("MEMBERGET").toString();
												orderNo = rowValue[1].getContents();
												String preValue = preCol[1].getContents().trim();

												String shippingShop = "";
												String shippingShopName = "";
												String address = "";
												for (Map<String, Object> oneData : lstDetailFormatDatas) 
												{
													String item = oneData.get("ITEM").toString();
													String tableName = oneData.get("TABLENAME").toString();
													String columnName = oneData.get("COLUMNNAME").toString(); // excel列名
													String fieldName = oneData.get("FIELDNAME").toString().toUpperCase(); // 数据库列名，转换为大写
													String yahooPickupWay = oneData.get("PICKUPWAY").toString(); //  1：超取 2：宅配

													if(orderNo.equals(preValue) )
													{
														for(int j = 0; j < columns; j++)
														{
															String index = (j + 1) + "";
															String headRowName = headRow[j].getContents() ;
															String fieldValue = "";
															fieldValue = rowValue[j].getContents();
															if (index.equals(item) && headRowName.equals(columnName) && tableName.equals("OC_ORDER_DETAIL") ) 
															{
																columnsFName2.add(fieldName);
																columnsFValue2.add(fieldValue);
															}
														}
														num = num + 1;
													}
													else
													{
														for (int j = 0; j < columns; j++) 
														{
															String index = (j + 1) + "";
															String headRowName = headRow[j].getContents() ;
															String fieldValue = "";
															fieldValue = rowValue[j].getContents();

															if (index.equals(item) && headRowName.equals(columnName) && tableName.equals("OC_ORDER") ) 
															{
																/**
																 * yahoo 收货地址 (address)必须以 “|”分割，且只能有两个： 门店编号 | 门店名称 | 地址 
																 * yahoo 超取， 地址栏： 包括取货门店编号， 名称， 地址 这三种 
																 */
																if(yahooPickupWay.equals("1") && fieldName.equals("ADDRESS") )
																{
																	// F000648｜全家中和中和店｜新北市中和區中和路４８０號１Ｆ  
																	// 我勒个去 ， “ | ” 和 “ ｜ ” 不一样。
																	String[] splitArr = fieldValue.trim().split("｜");
																	if(splitArr.length > 0)
																	{
																		shippingShop = splitArr[0].toString();
																		shippingShopName = splitArr[1].toString();
																		address = splitArr[2].toString();
																	}
																	columnsFName.add("SHIPPINGSHOP");
																	columnsFName.add("SHIPPINGSHOPNAME");
																	columnsFName.add("ADDRESS");

																	columnsFValue.add(shippingShop);
																	columnsFValue.add(shippingShopName);
																	columnsFValue.add(address);

																}
																if(!fieldName.equals("ADDRESS"))
																{
																	columnsFName.add(fieldName);
																	columnsFValue.add(fieldValue);
																}
																if(fieldName.equals("ORDERNO"))
																{
																	orderNo = fieldValue;
																}
																// 获取订购人，订购人电话信息
																if(fieldName.equals("CONTMAN"))
																{
																	contMan = fieldValue;
																}
																if(fieldName.equals("CONTTEL"))
																{
																	contTel = fieldValue;
																}



															}
															if (index.equals(item) && headRowName.equals(columnName) && tableName.equals("OC_ORDER_DETAIL") ) 
															{
																columnsFName2.add(fieldName);
																columnsFValue2.add(fieldValue);
															}


														}
														//														isRepeat = false;
														num = 0;
													}

												}
												columnsFName2.add("ORDERNO");
												columnsFValue2.add(orderNo);
												columnsFName2.add("ITEM");
												columnsFValue2.add(detailItem+"");

												if(num == 0)
												{
													DataValue[] columnsVal = new DataValue[columnsFName.size()];
													int insColCt = columnsFName.size();
													String[] columns1  = new String[insColCt];
													DataValue[] insValue = new DataValue[insColCt];
													// 依照傳入參數組譯要insert的欄位與數值；
													int ENO1 = 0; 
													for (int k=0;k<columnsVal.length;k++)
													{
														String keyValue = columnsFName.get(k).toString();
														if (keyValue != null) 
														{
															columnsVal[k] = new DataValue(keyValue, Types.VARCHAR);
														} 
														else 
														{
															columnsVal[k] = null;
														}

														if (columnsVal[k] != null)
														{
															columns1[ENO1] = columnsFName.get(k).toString();
															String fValue = columnsFValue.get(k).toString();
															columnsVal[k] = new DataValue(fValue, Types.VARCHAR) ;
															insValue[ENO1] = columnsVal[k];
															ENO1 ++;
															if (ENO1 >= insValue.length) 
																break;
														}
													}

													InsBean ib1 = new InsBean("OC_ORDER", columns1);
													ib1.addValues(insValue);
													lstData.add(new DataProcessBean(ib1));	
												}


												DataValue[] columnsVal2 = new DataValue[columnsFName2.size()];
												int insColCt2 = columnsFName2.size();
												String[] columns2  = new String[insColCt2];
												DataValue[] insValue2 = new DataValue[insColCt2];
												// 依照傳入參數組譯要insert的欄位與數值；
												int ENO2 = 0; 
												for (int k=0;k<columnsVal2.length;k++)
												{
													String keyValue = columnsFName2.get(k).toString();
													if (keyValue != null) 
													{
														columnsVal2[k] = new DataValue(keyValue, Types.VARCHAR);
													} 
													else 
													{
														columnsVal2[k] = null;
													}

													if (columnsVal2[k] != null)
													{
														columns2[ENO2] = columnsFName2.get(k).toString();
														String fValue = columnsFValue2.get(k).toString();
														columnsVal2[k] = new DataValue(fValue, Types.VARCHAR) ;
														insValue2[ENO2] = columnsVal2[k];
														ENO2 ++;
														if (ENO2 >= insValue2.length) 
															break;
													}
												}

												InsBean ib2 = new InsBean("OC_ORDER_DETAIL", columns2);
												ib2.addValues(insValue2);
												lstData.add(new DataProcessBean(ib2));	

												detailItem = detailItem +1;

											}
											StaticInfo.dao.useTransactionProcessData(lstData);
											String pathname = workFile + "/bak/";
											File bakFile = new File(pathname);
											if (bakFile.exists()) 
											{
												if (bakFile.isDirectory()) 
												{
													//System.out.println("目录已存在");
												} 
												else 
												{
													//System.out.println("目录下文件已存在, 不盘它了 ...");
												}
											} 
											else 
											{
												//System.out.println("目录不存在, 盘它 ...");
												//								            	boolean make = false;
												//								            	make = bakFile.mkdir();
											}

											// 上传下载的文件到备份位置
											ftpClient.makeDirectory(pathname);
											ftpClient.changeWorkingDirectory(pathname);
											FileInputStream upInputStream = new FileInputStream( new File(localFilePath + fileName));
											ftpClient.storeFile(fileName, upInputStream);
											upInputStream.close();

											try 
											{
												// 删除ftp下载地址的文件
												ftpClient.changeWorkingDirectory(workFile);
												ftpClient.deleteFile(fileName);

												File myDelFile = new File(localFilePath + fileName);
												myDelFile.delete();

												//System.out.println("本地缓存Excel文件删除成功!!!");
											} 
											catch (Exception e) 
											{

											}
										}

									}

									ftpClient.logout();

								}
								catch (Exception e) 
								{

								}

							}

							else if(ecPlatformNo.equals("momo"))
							{
								// 登录ftp，获取事件
								FTPClient ftpClient = ftpInterface.ftp(Ip, ftp_uId, ftp_pwd);//这里是ip，用户名，密码

								ftpClient.setControlEncoding("GBK"); // 中文支持
								ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);//设置文件类型								

								ftpClient.connect(Ip); //连接ftp服务器
								boolean bOkLogin=ftpClient.login(ftp_uId, ftp_pwd ); //登录ftp服务器

								if (bOkLogin==false) 
								{
									WriteFTPExcelLog("\r\n*********" + "FTP登錄失敗：電商EXCEL導入格式編碼=" +orderFormatNo +",電商平台="+ecPlatformNo + "************\r\n");
								}

								try
								{
									ftpClient.enterLocalPassiveMode();//设置ftp 模式，有被动模式和活动模式，这里设置为被动模式
									FTPFile [] files = ftpClient.listFiles(workFile);
									for (int f = 0; f < files.length; f++)
									{
										//下载Ftp文件到本地，用于读取excel 文件的内容
										//String localFilePath = "D:\\Download\\";
										String localFilePath = System.getProperty("catalina.home")+"\\webapps\\EC\\"+ecPlatformNo+"\\import\\";
										String fileName = files[f].getName();
										File localFile = new File(localFilePath + fileName);//设置本地下载的目录

										boolean flag1 = false ;
										boolean flag2 = false ;
										flag1 =  localFile.getName().toLowerCase().endsWith(".xls");
										flag2 =  localFile.getName().toLowerCase().endsWith(".xlsx");
										if(flag1 || flag2)
										{ 
											//如果当前文件是 excel 文件
											File fileparent = localFile.getParentFile();//本地下载目录下的文件夹，如果不存在则创建
											if (!fileparent.exists())
											{
												fileparent.mkdirs();
											}

											//不存在就下载
											if (!localFile.exists()) 
											{
												OutputStream os = new FileOutputStream(localFile);//输出到本地文件流
												ftpClient.retrieveFile(files[f].getName(), os);//下载文件到本地
												os.close();
											}

											// 下载到本地完成，之后要进行excel导入到OC_order 
											ftpClient.changeWorkingDirectory(workFile);  // 这里应该填客户定义的地址 

											InputStream inputStream = new FileInputStream(localFilePath+fileName);
											Workbook workbook = Workbook.getWorkbook(inputStream);
											Sheet sheet = workbook.getSheet(0);
											int rows = sheet.getRows();
											int columns = sheet.getColumns();

											Cell[] headRow = sheet.getRow(0); 
											int detailItem = 1;  // 子表item

											List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();		

											// 循环行 ， 从第二行开始， 前面为固定行
											for (int i = 1; i < rows; i++) 
											{ 
												//												int num = 0; // 这里声明num，用来记录主表插入的次数， 一个订单多个商品，主表数据只能 插入一次

												ArrayList<String> columnsFName = new ArrayList<String>();
												ArrayList<String> columnsFValue = new ArrayList<String>();

												ArrayList<String> columnsFName2 = new ArrayList<String>();
												ArrayList<String> columnsFValue2 = new ArrayList<String>();

												columnsFName.add("EID");
												columnsFName.add("CUSTOMERNO");
												columnsFName.add("ORGANIZATIONNO");
												columnsFName.add("SHOPID");
												columnsFName.add("SHOPNAME");
												columnsFName.add("LOAD_DOCTYPE");
												columnsFName.add("STATUS");
												columnsFName.add("REFUNDSTATUS");
												columnsFName.add("CREATE_DATETIME");
												columnsFName.add("ECCUSTOMERNO");

												columnsFValue.add(eId);
												columnsFValue.add(" ");
												columnsFValue.add(shopId);
												columnsFValue.add(shopId);
												columnsFValue.add(shopName);
												columnsFValue.add(ecPlatformNo);
												columnsFValue.add("2");
												columnsFValue.add("1"); // 退货退订状态 ： 1 未申请
												columnsFValue.add(sDate+sTime);
												columnsFValue.add(eccustomerno);

												columnsFName2.add("EID");
												columnsFName2.add("CUSTOMERNO");
												columnsFName2.add("ORGANIZATIONNO");
												columnsFName2.add("SHOPID");
												columnsFName2.add("LOAD_DOCTYPE");

												columnsFValue2.add(eId);
												columnsFValue2.add(" ");
												columnsFValue2.add(shopId);
												columnsFValue2.add(shopId);
												columnsFValue2.add(ecPlatformNo);

												Cell[] rowValue = sheet.getRow(i);

												String orderNo = "";
												String contMan = "";
												String contTel = "";
												String memberGet = "0"; // 0不抓取， 1抓取， 默认给0
												memberGet = map.get("MEMBERGET").toString();
												orderNo = rowValue[1].getContents();

												for (Map<String, Object> oneData : lstDetailFormatDatas) 
												{
													String item = oneData.get("ITEM").toString();
													String tableName = oneData.get("TABLENAME").toString();
													String columnName = oneData.get("COLUMNNAME").toString(); // excel列名
													String fieldName = oneData.get("FIELDNAME").toString().toUpperCase(); // 数据库列名，转换为大写


													for (int j = 0; j < columns; j++) 
													{
														String index = (j + 1) + "";
														String headRowName = headRow[j].getContents() ;
														String fieldValue = "";
														fieldValue = rowValue[j].getContents();

														if (headRowName.equals(columnName) && tableName.equals("OC_ORDER") ) 
														{
															columnsFName.add(fieldName);
															columnsFValue.add(fieldValue);

															if(fieldName.equals("ORDERNO"))
															{
																orderNo = fieldValue;
															}
															// 获取订购人，订购人电话信息
															if(fieldName.equals("CONTMAN"))
															{
																contMan = fieldValue;
															}
															if(fieldName.equals("CONTTEL"))
															{
																contTel = fieldValue;
															}

														}
														if (index.equals(item) && headRowName.equals(columnName) && tableName.equals("OC_ORDER_DETAIL") ) 
														{
															columnsFName2.add(fieldName);
															columnsFValue2.add(fieldValue);
														}


													}

												}
												columnsFName2.add("ORDERNO");
												columnsFValue2.add(orderNo);
												columnsFName2.add("ITEM");
												columnsFValue2.add("1");
												DataValue[] columnsVal = new DataValue[columnsFName.size()];
												int insColCt = columnsFName.size();
												String[] columns1  = new String[insColCt];
												DataValue[] insValue = new DataValue[insColCt];
												// 依照傳入參數組譯要insert的欄位與數值；
												int ENO1 = 0; 
												for (int k=0;k<columnsVal.length;k++)
												{
													String keyValue = columnsFName.get(k).toString();
													if (keyValue != null) 
													{
														columnsVal[k] = new DataValue(keyValue, Types.VARCHAR);
													} 
													else 
													{
														columnsVal[k] = null;
													}

													if (columnsVal[k] != null)
													{
														columns1[ENO1] = columnsFName.get(k).toString();
														String fValue = columnsFValue.get(k).toString();
														columnsVal[k] = new DataValue(fValue, Types.VARCHAR) ;
														insValue[ENO1] = columnsVal[k];
														ENO1 ++;
														if (ENO1 >= insValue.length) 
															break;
													}
												}

												InsBean ib1 = new InsBean("OC_ORDER", columns1);
												ib1.addValues(insValue);
												lstData.add(new DataProcessBean(ib1));	


												DataValue[] columnsVal2 = new DataValue[columnsFName2.size()];
												int insColCt2 = columnsFName2.size();
												String[] columns2  = new String[insColCt2];
												DataValue[] insValue2 = new DataValue[insColCt2];
												// 依照傳入參數組譯要insert的欄位與數值；
												int ENO2 = 0; 
												for (int k=0;k<columnsVal2.length;k++)
												{
													String keyValue = columnsFName2.get(k).toString();
													if (keyValue != null) 
													{
														columnsVal2[k] = new DataValue(keyValue, Types.VARCHAR);
													} 
													else 
													{
														columnsVal2[k] = null;
													}

													if (columnsVal2[k] != null)
													{
														columns2[ENO2] = columnsFName2.get(k).toString();
														String fValue = columnsFValue2.get(k).toString();
														columnsVal2[k] = new DataValue(fValue, Types.VARCHAR) ;
														insValue2[ENO2] = columnsVal2[k];
														ENO2 ++;
														if (ENO2 >= insValue2.length) 
															break;
													}
												}

												InsBean ib2 = new InsBean("OC_ORDER_DETAIL", columns2);
												ib2.addValues(insValue2);
												lstData.add(new DataProcessBean(ib2));	

											}
											StaticInfo.dao.useTransactionProcessData(lstData);
											String pathname = workFile + "/bak/";
											File bakFile = new File(pathname);
											if (bakFile.exists()) 
											{
												if (bakFile.isDirectory()) 
												{
													//System.out.println("目录已存在");
												} 
												else 
												{
													//System.out.println("目录下文件已存在, 不盘它了 ...");
												}
											} 
											else 
											{
												//System.out.println("目录不存在, 盘它 ...");
											}

											// 上传下载的文件到备份位置
											ftpClient.makeDirectory(pathname);
											ftpClient.changeWorkingDirectory(pathname);
											FileInputStream upInputStream = new FileInputStream( new File(localFilePath + fileName));
											ftpClient.storeFile(fileName, upInputStream);
											upInputStream.close();

											try 
											{
												// 删除ftp下载地址的文件
												ftpClient.changeWorkingDirectory(workFile);
												ftpClient.deleteFile(fileName);

												File myDelFile = new File(localFilePath + fileName);
												myDelFile.delete();

												//System.out.println("本地缓存Excel文件删除成功!!!");
											} 
											catch (Exception e) 
											{

											}
										}

									}

									ftpClient.logout();

								}
								catch (Exception e) 
								{

								}

							}

							else if(ecPlatformNo.equals("general") && fileSuffix.equals("shopline"))
							{
								// 登录ftp，获取事件
								FTPClient ftpClient = ftpInterface.ftp(Ip, ftp_uId, ftp_pwd);//这里是ip，用户名，密码
								try
								{
									ftpClient.enterLocalPassiveMode();
									FTPFile [] files = ftpClient.listFiles(workFile);
									for (int f = 0; f < files.length; f++)
									{
										//下载Ftp文件到本地，用于读取excel 文件的内容
										//String localFilePath = "D:\\Download\\";
										String localFilePath = System.getProperty("catalina.home")+"\\webapps\\EC\\"+ecPlatformNo+"\\import\\shopline\\";
										String fileName = files[f].getName();
										File localFile = new File(localFilePath + fileName);//设置本地下载的目录

										boolean flag1 = false ;
										boolean flag2 = false ;
										flag1 =  localFile.getName().toLowerCase().endsWith(".xls");
										flag2 =  localFile.getName().toLowerCase().endsWith(".xlsx");
										if(flag1 || flag2)
										{ 
											//如果当前文件是 excel 文件
											File fileparent = localFile.getParentFile();//本地下载目录下的文件夹，如果不存在则创建
											if (!fileparent.exists())
											{
												fileparent.mkdirs();
											}

											ftpClient.changeWorkingDirectory(workFile);
											//被动模式
											ftpClient.enterLocalPassiveMode();

											//不存在就下载
											if (!localFile.exists()) 
											{
												OutputStream os = new FileOutputStream(localFile); 
												String encodeName=new String(files[f].getName().getBytes("UTF-8"),"iso-8859-1");
												boolean bok=ftpClient.retrieveFile(encodeName, os); 						
												if (bok==false) 
												{
													encodeName=new String(files[f].getName().getBytes("GBK"),"iso-8859-1");
													bok=ftpClient.retrieveFile(encodeName, os); 
												}
												os.close(); 
											}																				

											// 下载到本地完成，之后要进行excel导入到OC_order 
											ftpClient.changeWorkingDirectory(workFile);  // 这里应该填客户定义的地址 

											InputStream inputStream = new FileInputStream(localFilePath+fileName);
											Workbook workbook = Workbook.getWorkbook(inputStream);
											Sheet sheet = workbook.getSheet(0);
											int rows = sheet.getRows();
											int columns = sheet.getColumns();

											Cell[] headRow = sheet.getRow(0); 

											List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();		

											// 2019-08-21 設置 preOrderNo 字段，記錄每一行的單號，如果當前行的單號 和 上一次循環的單號一樣， 就説明是同一個訂單， 不再插入當前行主表數據
											String preOrderNo = "";
											int detailItem = 1;

											// 循环行 ， 从第二行开始， 前面为固定行
											for (int i = 1; i < rows; i++) 
											{
												Cell[] curRow = sheet.getRow(i);
												int curLen = curRow.length;

												// OC_ORDER
												ArrayList<String> columnsFName = new ArrayList<String>();
												ArrayList<String> columnsFValue = new ArrayList<String>();

												// OC_ORDER_DETAIL
												ArrayList<String> columnsFName2 = new ArrayList<String>();
												ArrayList<String> columnsFValue2 = new ArrayList<String>();

												// OC_ORDER_AGIO 订单折扣表
												ArrayList<String> agioColumnsFName = new ArrayList<String>();
												ArrayList<String> agioColumnsFValue = new ArrayList<String>();

												// OC_ORDER_PAY
												ArrayList<String> columnsFName4 = new ArrayList<String>();
												ArrayList<String> columnsFValue4 = new ArrayList<String>();

												columnsFName.add("EID");
												columnsFName.add("CUSTOMERNO");
												columnsFName.add("ORGANIZATIONNO");
												columnsFName.add("SHOPID");
												columnsFName.add("SHOPNAME");
												columnsFName.add("LOAD_DOCTYPE");
												columnsFName.add("STATUS");
												columnsFName.add("REFUNDSTATUS");
												columnsFName.add("CREATE_DATETIME");
												columnsFName.add("ECCUSTOMERNO");

												columnsFValue.add(eId);
												columnsFValue.add(eccustomerno);
												columnsFValue.add(shopId);
												columnsFValue.add(shopId);
												columnsFValue.add(shopName);
												columnsFValue.add(ecPlatformNo);
												columnsFValue.add("2");
												columnsFValue.add("1"); // 退货退订状态 ： 1 未申请
												columnsFValue.add(sDate+sTime);
												columnsFValue.add(eccustomerno);

												columnsFName2.add("EID");
												columnsFName2.add("CUSTOMERNO");
												columnsFName2.add("ORGANIZATIONNO");
												columnsFName2.add("SHOPID");
												columnsFName2.add("LOAD_DOCTYPE");

												columnsFValue2.add(eId);
												columnsFValue2.add(" ");
												columnsFValue2.add(shopId);
												columnsFValue2.add(shopId);
												columnsFValue2.add(ecPlatformNo);


												Cell[] rowValue = sheet.getRow(i);

												String payAmt = "0";
												BigDecimal payAmtBD = new BigDecimal(0);
												String orderNo = "";
												orderNo = rowValue[0].getContents();

												//记录每一行的单价、数量、折扣额，用于计算商品金额（amt）
												String price = "0"; 
												String qty = "0";
												String disc = "0";
												//找不到商品的异常
												String sExceptionStatus="N";
												String sExceptionMemo="";
												String pluNo = "";
												String pluName = "";

												String totDisc = "0"; //订单优惠总额
												BigDecimal totDiscBD = new BigDecimal(0);

												String shipType = ""; // 配送方式 （默认给空，前端空值显示的是“其他”）
												String deliveryType = ""; //物流类型
												// 黑猫 /新竹 ：shipType 传2 (宅配)
												// 其他： shipType 传 6 (超商)

												String contMan = "";
												String contTel = "";
												String memberGet = "0"; // 0不抓取， 1抓取， 默认给0
												memberGet = map.get("MEMBERGET").toString();

												for (Map<String, Object> oneData : lstDetailFormatDatas) 
												{
													System.out.println(oneData.get("ORDERFORMATNAME").toString());																								
													//													String item = oneData.get("ITEM").toString();
													String tableName = oneData.get("TABLENAME").toString();
													String columnName = oneData.get("COLUMNNAME").toString(); // excel列名
													String fieldName = oneData.get("FIELDNAME").toString().toUpperCase(); // 数据库列名，转换为大写

													nextCol:for (int j = 0; j < columns; j++) 
													{
														if(j >= curLen)
														{
															break nextCol;
														}

														//														String index = (j + 1) + "";
														String headExcelRowName = headRow[j].getContents();
														String fieldValue = "";
														fieldValue = rowValue[j].getContents();

														//單頭表處理
														if (headExcelRowName.equals(columnName) && tableName.equals("OC_ORDER")) 
														{
															if (fieldName.equals("SDATE")) 
															{
																String date2 = "";

																if(fieldValue.contains("/"))
																{   // mad, 这日期格式变来变去， 单元格显示2019-07-01,点进去看 是 2019-7-1 13:00:00，读取的时候是 2019/07/01

																	String strDate = fieldValue;
																	SimpleDateFormat sdf1=new SimpleDateFormat("yyyy/MM/dd");
																	SimpleDateFormat sdf2=new SimpleDateFormat("yyyyMMdd");
																	//必须捕获异常
																	try
																	{ 
																		Date date=sdf1.parse(strDate);
																		date2 = sdf2.format(date);
																	}
																	catch(ParseException px)
																	{

																	}
																}
																columnsFName.add(fieldName);
																columnsFValue.add(date2);
															}
															else if(fieldName.equals("DELIVERYTYPE"))
															{

																if(fieldValue.contains("新竹"))
																{
																	deliveryType = "15";
																	shipType = "2";
																}
																if(fieldValue.contains("黑貓"))
																{
																	deliveryType = "9";
																	shipType = "2";
																}
																if(fieldValue.contains("7-11"))
																{
																	deliveryType = "7";
																	shipType = "6";
																}
																if(fieldValue.contains("全家"))
																{
																	deliveryType = "8";
																	shipType = "6";
																}
																if(fieldValue.contains("萊爾富"))
																{
																	deliveryType = "10";
																	shipType = "6";
																}
																if(fieldValue.contains("OK"))
																{
																	deliveryType = "11";
																	shipType = "6";
																}

															}
															else
															{
																columnsFName.add(fieldName);
																columnsFValue.add(fieldValue);
															}

															if (fieldName.equals("ORDERNO")) 
															{
																orderNo = fieldValue;
															}
															if (fieldName.equals("PAYAMT"))
															{
																if(!fieldValue.equals(""))
																{
																	payAmtBD = payAmtBD.add(new BigDecimal(fieldValue));
																}
															}
															// 获取订购人，订购人电话信息
															if (fieldName.equals("CONTMAN")) 
															{
																contMan = fieldValue;
															}
															if (fieldName.equals("CONTTEL")) 
															{
																contTel = fieldValue;
															}

															if((fieldName.equals("SELLER_DISC") || fieldName.equals("PLATFORM_DISC")) && !fieldValue.equals("") )
															{
																totDiscBD = totDiscBD.add(new BigDecimal(fieldValue));
															}

															break nextCol;

														}
														//單身表處理
														if (headExcelRowName.equals(columnName) && tableName.equals("OC_ORDER_DETAIL")) 
														{
															if(fieldName.equals("PLUNO"))
															{
																pluNo = fieldValue;
															}
															if(fieldName.equals("PLUNAME"))
															{
																pluName = fieldValue;
															}

															if(fieldName.equals("DISC") && !fieldValue.equals(""))
															{
																disc = fieldValue;
															}
															if(fieldName.equals("QTY") && !fieldValue.equals(""))
															{
																qty = fieldValue;
															}
															if(fieldName.equals("PRICE"))
															{
																price = fieldValue;
															}

															// 加个验证，如果数字字段为空， 就设置值为0 （目前就这么几个，可以通用）
															if(fieldName.equals("DISC") || fieldName.equals("QTY") || fieldName.equals("PRICE"))
															{
																if(fieldValue.equals(""))
																{
																	fieldValue = "0";
																}
															}

															columnsFName2.add(fieldName);
															columnsFValue2.add(fieldValue);

															break nextCol;
														}


														if (headExcelRowName.equals(columnName) && tableName.equals("OC_ORDER_PAY")) 
														{
															String order_payName = fieldValue;
															String payNameERP = "";
															String payCode = "";
															String payCodeERP = "";
															String order_payCode = "";

															//过滤付款方式映射
															Map<String, Object> map_condition = new HashMap<String, Object>();
															map_condition.put("ORDER_PAYCODE",order_payName );//这里采用包含的关系,因为那些信用卡和银行太多了		
															List<Map<String, Object>> getQPAY=MapDistinct.getWhereMap(payData,map_condition,false,2);	

															if (getQPAY==null || getQPAY.size()==0) 
															{
																map_condition = new HashMap<String, Object>();
																map_condition.put("ORDER_PAYCODE", "ALL");		
																getQPAY=MapDistinct.getWhereMap(payData,map_condition,false);	
																if (getQPAY!=null && getQPAY.size()>0) 
																{
																	payCode=getQPAY.get(0).get("PAYCODE").toString();
																	payNameERP=getQPAY.get(0).get("PAYNAME").toString();
																	payCodeERP=getQPAY.get(0).get("PAYCODEERP").toString();
																	order_payCode=getQPAY.get(0).get("ORDER_PAYCODE").toString();
																}
																else 
																{
																	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,
																			order_payName + ":不存在该支付方式映射信息");
																}
															}
															else 
															{
																payCode=getQPAY.get(0).get("PAYCODE").toString();
																payNameERP=getQPAY.get(0).get("PAYNAME").toString();
																payCodeERP=getQPAY.get(0).get("PAYCODEERP").toString();
																order_payCode=getQPAY.get(0).get("ORDER_PAYCODE").toString();
															}


															columnsFName4.add("EID");
															columnsFValue4.add(eId);

															columnsFName4.add("CUSTOMERNO");
															columnsFValue4.add(" ");

															columnsFName4.add("ORGANIZATIONNO");
															columnsFValue4.add(shopId);

															columnsFName4.add("SHOPID");
															columnsFValue4.add(shopId);

															columnsFName4.add("ORDERNO");
															columnsFValue4.add(orderNo);

															columnsFName4.add("ITEM");
															columnsFValue4.add("1");

															columnsFName4.add("LOAD_DOCTYPE");
															columnsFValue4.add(ecPlatformNo);

															columnsFName4.add("PAYCODE");
															columnsFValue4.add(payCode);

															columnsFName4.add("PAYNAME");
															columnsFValue4.add(payNameERP);

															columnsFName4.add("PAYCODEERP");
															columnsFValue4.add(payCodeERP);

															columnsFName4.add("ORDER_PAYCODE"); // 是否平台支付
															columnsFValue4.add(order_payCode);	

															columnsFName4.add("ISORDERPAY"); // 是否定金
															columnsFValue4.add("N");

															columnsFName4.add("ISONLINEPAY"); // 是否平台支付
															columnsFValue4.add("Y");

															columnsFName4.add("RCPAY"); // 已拆单金额
															columnsFValue4.add("0");
														}

													}

												}


												//判斷一下單據是否存在，如果存在就沒必要插入了
												String sqlOrder="select * from OC_order where EID='"+eId+"' and SHOPID='"+shopId+"' and orderno='"+orderNo+"' ";
												List<Map<String, Object>> getOrder=this.doQueryData(sqlOrder, null);
												if (getOrder!=null && getOrder.size()>0) 
												{
													WriteFTPExcelLog("訂單號="+ orderNo +"在EID=" +eId+",SHOPID="+shopId+"已經存在！跳過" );
													continue;//
												}											

												//異常商品處理
												String sExeptionSql="select a.pluNo ,  a.sunit, b.plubarcode  from DCP_GOODS a "
														+ " left join DCP_BARCODE b on a.EID = b.EID and a.pluNo = b.pluNo and b.STATUS='100' "
														+ " where a.EID = '"+eId+"'  "
														+ " and a.pluNo = '"+pluNo+"' "
														+ " and a.STATUS='100' ";

												List<Map<String, Object>> sqlExceptionList=this.doQueryData(sExeptionSql, null);
												if (sqlExceptionList == null || sqlExceptionList.isEmpty())
												{			
													//只需一次异常赋值
													if (sExceptionStatus.equals("Y")==false) 
													{
														sExceptionStatus="Y";
													}

													sExceptionMemo += pluNo+"__("+ pluName+"),\r\n";												
												}
												sqlExceptionList=null;

												if(!preOrderNo.equals(orderNo))
												{

													columnsFName4.add("PAY");
													columnsFValue4.add(payAmtBD.toString());

													columnsFName.add("TOT_DISC");
													columnsFValue.add(totDiscBD.toString());

													columnsFName.add("DELIVERYTYPE");
													columnsFValue.add(deliveryType);
													columnsFName.add("SHIPTYPE");
													columnsFValue.add(shipType);

													columnsFName.add("EXCEPTIONSTATUS");
													columnsFValue.add(sExceptionStatus);
													columnsFName.add("EXCEPTIONMEMO");
													columnsFValue.add(sExceptionMemo);

													detailItem = 1;
													payAmtBD = new BigDecimal(0);

												}
												else
												{
													detailItem = detailItem + 1 ;
												}

												BigDecimal amtBD = new BigDecimal(0);

												BigDecimal priceBD = new BigDecimal(price);
												BigDecimal qtyBD = new BigDecimal(qty);
												BigDecimal discBD = new BigDecimal(disc);
												amtBD = priceBD.multiply(qtyBD).subtract(discBD);
												//变成金钱格式,留   .00
												DecimalFormat format = new DecimalFormat("0.00");
												String amt = format.format(amtBD);

												columnsFName2.add("ORDERNO");
												columnsFValue2.add(orderNo);
												columnsFName2.add("ITEM");
												columnsFValue2.add(detailItem+"");

												columnsFName2.add("AMT");
												columnsFValue2.add(amt);
												columnsFName2.add("BOXNUM");
												columnsFValue2.add("0");
												columnsFName2.add("BOXPRICE");
												columnsFValue2.add("0");


												// 当前行的单号和上一行的单号不一样的时候，才能插入当前行的主表数据
												if(!preOrderNo.equals(orderNo))
												{
													DataValue[] columnsVal = new DataValue[columnsFName.size()];
													int insColCt = columnsFName.size();
													String[] columns1 = new String[insColCt];
													DataValue[] insValue = new DataValue[insColCt];
													// 依照傳入參數組譯要insert的欄位與數值；
													int ENO1 = 0;
													for (int k = 0; k < columnsVal.length; k++) 
													{
														String keyValue = columnsFName.get(k).toString();
														if (keyValue != null) 
														{
															columnsVal[k] = new DataValue(keyValue, Types.VARCHAR);
														} 
														else 
														{
															columnsVal[k] = null;
														}

														if (columnsVal[k] != null) 
														{
															columns1[ENO1] = columnsFName.get(k).toString();
															String fValue = columnsFValue.get(k).toString();
															columnsVal[k] = new DataValue(fValue, Types.VARCHAR);
															insValue[ENO1] = columnsVal[k];
															ENO1++;
															if (ENO1 >= insValue.length)
																break;
														}
													}

													InsBean ib1 = new InsBean("OC_ORDER", columns1);
													ib1.addValues(insValue);
													//													this.addProcessData(new DataProcessBean(ib1));
													lstData.add(new DataProcessBean(ib1));	

													//
													sbOrderno.append("準備導入的訂單號="+orderNo + ",EID="+eId+",SHOPID=" + shopId + "\r\n");

													// 插入付款档信息
													if (columnsFName4.size() > 0) 
													{

														DataValue[] columnsVal4 = new DataValue[columnsFName4.size()];
														int insColCt4 = columnsFName4.size();
														String[] columns4 = new String[insColCt4];
														DataValue[] insValue4 = new DataValue[insColCt4];
														// 依照傳入參數組譯要insert的欄位與數值；
														int ENO4 = 0;
														for (int k = 0; k < columnsVal4.length; k++) 
														{
															String keyValue = columnsFName4.get(k).toString();
															if (keyValue != null) 
															{
																columnsVal4[k] = new DataValue(keyValue, Types.VARCHAR);
															} 
															else 
															{
																columnsVal4[k] = null;
															}

															if (columnsVal4[k] != null)
															{
																columns4[ENO4] = columnsFName4.get(k).toString();
																String fValue = columnsFValue4.get(k).toString();
																columnsVal4[k] = new DataValue(fValue, Types.VARCHAR);
																insValue4[ENO4] = columnsVal4[k];
																ENO4++;
																if (ENO4 >= insValue4.length)
																	break;
															}
														}

														InsBean ib4 = new InsBean("OC_ORDER_PAY", columns4);
														ib4.addValues(insValue4);
														//														this.addProcessData(new DataProcessBean(ib4));
														lstData.add(new DataProcessBean(ib4));	
													}

													//插入接單日誌信息
													String[] columnsORDER_STATUSLOG = 
														{ 
																"EID", 
																"ORGANIZATIONNO", 
																"SHOPID", 
																"ORDERNO", 
																"LOAD_DOCTYPE",
																"STATUSTYPE", 
																"STATUSTYPENAME", 
																"STATUS", 
																"STATUSNAME", 
																"NEED_NOTIFY", 
																"NOTIFY_STATUS",
																"NEED_CALLBACK", 
																"CALLBACK_STATUS", 
																"OPNO", 
																"OPNAME", 
																"UPDATE_TIME",
																"MEMO", 
																"STATUS" 
														};

													//接單日誌
													DataValue[] insValueOrderStatus_LOG = new DataValue[] 
															{ 
																	new DataValue(eId, Types.VARCHAR),
																	new DataValue(shopId, Types.VARCHAR), // 组织编号=门店编号
																	new DataValue(shopId, Types.VARCHAR), // 映射后的门店
																	new DataValue(orderNo, Types.VARCHAR), //
																	new DataValue("general", Types.VARCHAR), //電商平台
																	new DataValue("1", Types.VARCHAR), // 状态类型 // 1-订单状态，2-配送状态，3-退单状态，4-其他
																	new DataValue("訂單狀態", Types.VARCHAR), // 状态类型名称
																	new DataValue("2", Types.VARCHAR), // 状态 0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
																	new DataValue("已接單", Types.VARCHAR), // 状态名称
																	new DataValue("N", Types.VARCHAR), // 是否通知云pos,N-不需要调用，Y-需要
																	new DataValue("0", Types.VARCHAR), // 通知云pos状态返回，0-未通知，1-已通知
																	new DataValue("N", Types.VARCHAR), // 是否调用第三方接口，N-不需要调用，Y-需要
																	new DataValue("0", Types.VARCHAR), // 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
																	new DataValue("admin", Types.VARCHAR), //操作員編碼
																	new DataValue("管理員", Types.VARCHAR), //操作員名稱
																	new DataValue(orderStatusLogTimes, Types.VARCHAR), //yyyyMMddHHmmssSSS
																	new DataValue("訂單狀態-->已接單", Types.VARCHAR), //類型名稱+"-->"+狀態名稱
																	new DataValue("100", Types.VARCHAR) 
															};
													InsBean ibOrderStatusLog = new InsBean("OC_ORDER_STATUSLOG", columnsORDER_STATUSLOG);
													ibOrderStatusLog.addValues(insValueOrderStatus_LOG);
													//													this.addProcessData(new DataProcessBean(ibOrderStatusLog));
													lstData.add(new DataProcessBean(ibOrderStatusLog));	

												}

												DataValue[] columnsVal2 = new DataValue[columnsFName2.size()];
												int insColCt2 = columnsFName2.size();
												String[] columns2 = new String[insColCt2];
												DataValue[] insValue2 = new DataValue[insColCt2];
												// 依照傳入參數組譯要insert的欄位與數值；
												int ENO2 = 0;
												for (int k = 0; k < columnsVal2.length; k++) 
												{
													String keyValue = columnsFName2.get(k).toString();
													if (keyValue != null) 
													{
														columnsVal2[k] = new DataValue(keyValue, Types.VARCHAR);
													} 
													else 
													{
														columnsVal2[k] = null;
													}

													if (columnsVal2[k] != null) 
													{
														columns2[ENO2] = columnsFName2.get(k).toString();
														String fValue = columnsFValue2.get(k).toString();
														columnsVal2[k] = new DataValue(fValue, Types.VARCHAR);
														insValue2[ENO2] = columnsVal2[k];
														ENO2++;
														if (ENO2 >= insValue2.length)
															break;
													}
												}

												InsBean ib2 = new InsBean("OC_ORDER_DETAIL", columns2);
												ib2.addValues(insValue2);
												//												this.addProcessData(new DataProcessBean(ib2));
												lstData.add(new DataProcessBean(ib2));	

												preOrderNo = orderNo;

											}
											StaticInfo.dao.useTransactionProcessData(lstData);
											String pathname = workFile + "/bak/";
											File bakFile = new File(pathname);
											if (bakFile.exists()) 
											{
												if (bakFile.isDirectory()) 
												{
													//System.out.println("目录已存在");
												} 
												else 
												{
													//System.out.println("目录下文件已存在, 不盘它了 ...");
												}
											} 
											else 
											{
												//System.out.println("目录不存在, 盘它 ...");
											}

											// 上传下载的文件到备份位置
											ftpClient.makeDirectory(pathname);
											ftpClient.changeWorkingDirectory(pathname);
											FileInputStream upInputStream = new FileInputStream( new File(localFilePath + fileName));

											ftpClient.enterLocalPassiveMode();
											ftpClient.storeFile(fileName, upInputStream);
											upInputStream.close();

											try 
											{
												// 删除ftp下载地址的文件
												ftpClient.changeWorkingDirectory(workFile);
												ftpClient.deleteFile(fileName);

												File myDelFile = new File(localFilePath + fileName);
												myDelFile.delete();

												//System.out.println("本地缓存Excel文件删除成功!!!");
											} 
											catch (Exception e) 
											{

											}
										}

									}

									ftpClient.logout();

								}
								catch (Exception e) 
								{

								}

							}
							//general平台，金財通
							else if (ecPlatformNo.equals("general") && (orderFormatName.contains("金財通")||fileSuffix.equals("jincaitong"))) 
							{
								// 登录ftp，获取事件
								FTPClient ftpClient = ftpInterface.ftp(Ip, ftp_uId, ftp_pwd);//这里是ip，用户名，密码

								try 
								{									
									ftpClient.enterLocalPassiveMode();
									FTPFile [] files = ftpClient.listFiles(workFile);
									for (int f = 0; f < files.length; f++)
									{
										//下载Ftp文件到本地，用于读取excel 文件的内容
										//String localFilePath = "D:\\Download\\";
										String localFilePath = System.getProperty("catalina.home")+"\\webapps\\EC\\"+ecPlatformNo+"\\import\\jincaitong\\";
										String fileName = files[f].getName();
										File localFile = new File(localFilePath + fileName);//设置本地下载的目录

										boolean flag1 = false ;
										boolean flag2 = false ;
										flag1 =  localFile.getName().toLowerCase().endsWith(".xls");
										flag2 =  localFile.getName().toLowerCase().endsWith(".xlsx");
										if(flag1 || flag2)
										{ 
											//如果当前文件是 excel 文件
											File fileparent = localFile.getParentFile();//本地下载目录下的文件夹，如果不存在则创建
											if (!fileparent.exists())
											{
												fileparent.mkdirs();
											}

											ftpClient.changeWorkingDirectory(workFile);
											//被动模式
											ftpClient.enterLocalPassiveMode();

											//不存在就下载
											if (!localFile.exists()) 
											{
												OutputStream os = new FileOutputStream(localFile); 
												String encodeName=new String(files[f].getName().getBytes("UTF-8"),"iso-8859-1");
												boolean bok=ftpClient.retrieveFile(encodeName, os); 						
												if (bok==false) 
												{
													encodeName=new String(files[f].getName().getBytes("GBK"),"iso-8859-1");
													bok=ftpClient.retrieveFile(encodeName, os); 
												}
												os.close(); 
											}																				

											//被动模式
											ftpClient.enterLocalPassiveMode();
											// 下载到本地完成，之后要进行excel导入到OC_order 
											ftpClient.changeWorkingDirectory(workFile);  // 这里应该填客户定义的地址 

											InputStream inputStream = new FileInputStream(localFilePath+fileName);
											Workbook workbook = Workbook.getWorkbook(inputStream);
											Sheet sheet = workbook.getSheet(0);
											int rows = sheet.getRows();
											int columns = sheet.getColumns();

											Cell[] headRow = sheet.getRow(0); 

											List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();		

											// 2019-08-21 設置 preOrderNo 字段，記錄每一行的單號，如果當前行的單號 和 上一次循環的單號一樣， 就説明是同一個訂單， 不再插入當前行主表數據
											String preOrderNo = "";
											int detailItem = 1;

											//读取excel订单记录
											List<Map<String, Object>> lstOrders=new ArrayList<>();

											String order_payName = "现金/現金";
											String payNameERP = "";
											String payCode = "";
											String payCodeERP = "";
											String order_payCode = "";
											//过滤付款方式映射
											Map<String, Object> map_condition = new HashMap<String, Object>();
											map_condition.put("PAYNAME",order_payName );//这里采用包含的关系,因为那些信用卡和银行太多了		
											List<Map<String, Object>> getQPAY=MapDistinct.getWhereMap(payData,map_condition,false,2);	

											if (getQPAY==null || getQPAY.size()==0) 
											{
												map_condition = new HashMap<String, Object>();
												map_condition.put("ORDER_PAYCODE", "ALL");		
												getQPAY=MapDistinct.getWhereMap(payData,map_condition,false);	
												if (getQPAY!=null && getQPAY.size()>0) 
												{
													payCode=getQPAY.get(0).get("PAYCODE").toString();
													payNameERP=getQPAY.get(0).get("PAYNAME").toString();
													payCodeERP=getQPAY.get(0).get("PAYCODEERP").toString();
													order_payCode=getQPAY.get(0).get("ORDER_PAYCODE").toString();
													if (order_payCode.equals("")) 
													{
														order_payCode="jincaitong";
													}
													order_payName=getQPAY.get(0).get("ORDER_PAYNAME").toString();
												}
												else 
												{
													throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,
															order_payName + ":不存在该支付方式映射信息");
												}
											}
											else 
											{
												payCode=getQPAY.get(0).get("PAYCODE").toString();
												payNameERP=getQPAY.get(0).get("PAYNAME").toString();
												payCodeERP=getQPAY.get(0).get("PAYCODEERP").toString();
												order_payCode=getQPAY.get(0).get("ORDER_PAYCODE").toString();
												if (order_payCode.equals("")) 
												{
													order_payCode="jincaitong";
												}
												order_payName=getQPAY.get(0).get("ORDER_PAYNAME").toString();
											}

											// 循环行 ， 从第二行开始， 前面为固定行
											for (int i = 1; i < rows; i++) 
											{
												Cell[] cellValue = sheet.getRow(i);

												//订单信息
												Map<String, Object> mapOrder=new HashMap<String, Object>();	

												for (Map<String, Object> oneData : lstDetailFormatDatas) 
												{
													String tableName = oneData.get("TABLENAME").toString().toUpperCase();
													String columnName = oneData.get("COLUMNNAME").toString(); // excel列名
													String fieldName = oneData.get("FIELDNAME").toString().toUpperCase(); // 数据库列名，转换为大写

													for (int j = 0; j < cellValue.length; j++) 
													{										
														String headRowName = headRow[j].getContents();

														if (headRowName.equals(columnName)) 
														{
															System.out.println("j:"+j + " 列名："+headRowName);

															String fieldValue = "";
															fieldValue = cellValue[j].getContents();
															System.out.println("j:"+j + " 列值："+fieldValue);

															mapOrder.put(tableName+"."+fieldName, fieldValue);
														}									
													}
												}	

												//
												lstOrders.add(mapOrder);
											}

											
											//
											sheet=null;
											workbook=null;
											inputStream.close();
											
											//
											Map<String, Boolean> conditionD1 = new HashMap<String, Boolean>(); //查詢條件				
											conditionD1.put("OC_ORDER.ORDERNO", true);  
											//调用过滤函数			
											List<Map<String, Object>> getHeader=MapDistinct.getMap(lstOrders, conditionD1);

											for (Map<String, Object> oneData : getHeader) 
											{
												try 
												{
													String orderno = oneData.get("OC_ORDER.ORDERNO").toString();

													//判斷一下單據是否存在，如果存在就沒必要插入了
													String sqlOrder="select * from OC_order where EID='"+eId+"' and SHOPID='"+shopId+"' and orderno='"+orderno+"' ";
													List<Map<String, Object>> getOrder=this.doQueryData(sqlOrder, null);
													if (getOrder!=null && getOrder.size()>0) 
													{
														WriteFTPExcelLog("訂單號="+ orderno +"在EID=" +eId+",SHOPID="+shopId+"已經存在！跳過" );
														continue;//
													}											


													//找不到商品的异常
													String sExceptionStatus="N";
													String sExceptionMemo="";
													String pluNo = "";
													String pluName = "";


													String tot_oldAMT="0";
													String tot_qty="0";
													BigDecimal bdlOldAMT=new BigDecimal(tot_oldAMT);
													BigDecimal bdlTotQty=new BigDecimal(tot_qty);

													Map<String, Object> conditionD2 = new HashMap<String, Object>(); //查詢條件				
													conditionD2.put("OC_ORDER.ORDERNO", orderno); 
													//调用过滤函数			
													List<Map<String, Object>> getDetailDatas=MapDistinct.getWhereMap(lstOrders, conditionD2, true);

													int mydetailItem=0;
													for (Map<String, Object> oneData2 : getDetailDatas) 
													{				
														try 
														{
															mydetailItem+=1;

															String AMT=oneData2.get("OC_ORDER_DETAIL.AMT").toString();
															BigDecimal bdlAMT=new BigDecimal(AMT);
															bdlOldAMT=bdlOldAMT.add(bdlAMT);	

															String QTY=oneData2.get("OC_ORDER_DETAIL.QTY").toString();
															BigDecimal bdlQTY=new BigDecimal(QTY);
															bdlTotQty=bdlTotQty.add(bdlQTY);		

															// OC_ORDER_DETAIL
															ArrayList<String> columnsFName2 = new ArrayList<String>();
															ArrayList<String> columnsFValue2 = new ArrayList<String>();

															//OC_ORDER_DETAIL
															columnsFName2.add("EID");
															columnsFName2.add("CUSTOMERNO");
															columnsFName2.add("ORGANIZATIONNO");
															columnsFName2.add("SHOPID");
															columnsFName2.add("LOAD_DOCTYPE");
															columnsFName2.add("ORDERNO");
															columnsFName2.add("ITEM");
															columnsFName2.add("DISC");
															columnsFName2.add("BOXNUM");


															columnsFValue2.add(eId);
															columnsFValue2.add(" ");
															columnsFValue2.add(shopId);
															columnsFValue2.add(shopId);
															columnsFValue2.add(ecPlatformNo);		
															columnsFValue2.add(orderno);	
															columnsFValue2.add(mydetailItem+"");	
															columnsFValue2.add("0");	
															columnsFValue2.add("1");

															//添加明细的字段
															for(Map.Entry<String, Object> entry : oneData2.entrySet())
															{
																String mapKey = entry.getKey();
																Object mapValue = entry.getValue();

																if (mapKey.startsWith("OC_ORDER_DETAIL.")) 
																{
																	columnsFName2.add(mapKey.substring(16));
																	columnsFValue2.add(mapValue.toString());

																	if (mapKey.equals("OC_ORDER_DETAIL.PLUNO")) 
																	{
																		pluNo=mapValue.toString();
																	}
																	else
																	{
																		if (mapKey.equals("OC_ORDER_DETAIL.PLUNAME")) 
																		{
																			pluName=mapValue.toString();
																		}
																	}
																}								   
															}

															//
															DataValue[] columnsVal2 = new DataValue[columnsFName2.size()];
															int insColCt2 = columnsFName2.size();
															String[] columns2 = new String[insColCt2];
															DataValue[] insValue2 = new DataValue[insColCt2];
															// 依照傳入參數組譯要insert的欄位與數值；
															int ENO2 = 0;

															for (int k = 0; k < columnsVal2.length; k++) 
															{
																String keyValue = columnsFName2.get(k).toString();
																if (keyValue != null) 
																{
																	columnsVal2[k] = new DataValue(keyValue, Types.VARCHAR);
																} 
																else 
																{
																	columnsVal2[k] = null;
																}

																if (columnsVal2[k] != null) 
																{
																	columns2[ENO2] = columnsFName2.get(k).toString();
																	String fValue = columnsFValue2.get(k).toString();																		

																	columnsVal2[k] = new DataValue(fValue, Types.VARCHAR);
																	insValue2[ENO2] = columnsVal2[k];
																	ENO2++;
																	if (ENO2 >= insValue2.length)
																		break;
																}
															}

															//異常商品處理
															String sExeptionSql="select a.pluNo ,  a.sunit, b.plubarcode  from DCP_GOODS a "
																	+ " left join DCP_BARCODE b on a.EID = b.EID and a.pluNo = b.pluNo and b.STATUS='100' "
																	+ " where a.EID = '"+eId+"'  "
																	+ " and a.pluNo = '"+pluNo+"' "
																	+ " and a.STATUS='100' ";

															List<Map<String, Object>> sqlExceptionList=this.doQueryData(sExeptionSql, null);
															if (sqlExceptionList == null || sqlExceptionList.isEmpty())
															{			
																//只需一次异常赋值
																if (sExceptionStatus.equals("Y")==false) 
																{
																	sExceptionStatus="Y";
																}

																sExceptionMemo += pluNo+"__("+ pluName+"),\r\n";												
															}
															sqlExceptionList=null;

															//
															InsBean ib2 = new InsBean("OC_ORDER_DETAIL", columns2);
															ib2.addValues(insValue2);
															lstData.add(new DataProcessBean(ib2));

														} 
														catch (Exception e) 
														{
															WriteFTPExcelLog("金财通Excel插入订单明细异常：" + e.getMessage());

															throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "金财通Excel插入订单明细异常："+e.getMessage());
														}							

													}	

													// OC_ORDER
													ArrayList<String> columnsFName = new ArrayList<String>();
													ArrayList<String> columnsFValue = new ArrayList<String>();
													//OC_ORDER
													columnsFName.add("EID");
													columnsFName.add("CUSTOMERNO");
													columnsFName.add("ORGANIZATIONNO");
													columnsFName.add("SHOPID");
													columnsFName.add("SHOPNAME");
													columnsFName.add("LOAD_DOCTYPE");
													columnsFName.add("STATUS");
													columnsFName.add("PICKUPWAY");
													columnsFName.add("CREATE_DATETIME");
													columnsFName.add("ECCUSTOMERNO");
													columnsFName.add("PAYSTATUS");//1.未支付 2.部分支付 3.付清
													columnsFName.add("SHIPPINGSHOP");
													columnsFName.add("SHIPPINGSHOPNAME");
													columnsFName.add("EXCEPTIONSTATUS");
													columnsFName.add("EXCEPTIONMEMO");

													columnsFValue.add(eId);
													columnsFValue.add(" ");
													columnsFValue.add(shopId);
													columnsFValue.add(shopId);
													columnsFValue.add(shopName);
													columnsFValue.add(ecPlatformNo);
													columnsFValue.add("2");
													columnsFValue.add("2");//1：超取 2：宅配
													columnsFValue.add(sDate +sTime);
													columnsFValue.add(eccustomerno);													
													columnsFValue.add("3");//1.未支付 2.部分支付 3.付清
													columnsFValue.add(shopId);
													columnsFValue.add(shopName);
													columnsFValue.add(sExceptionStatus);
													columnsFValue.add(sExceptionMemo);

													//通过计算得到总金额总数量
													columnsFName.add("TOT_OLDAMT");
													columnsFName.add("TOT_AMT");
													columnsFName.add("TOT_QTY");
													columnsFName.add("INCOMEAMT");
													columnsFName.add("TOT_DISC");
													columnsFName.add("SELLER_DISC");
													columnsFName.add("PLATFORM_DISC");
													columnsFName.add("SHIPFEE");
													columnsFName.add("TOTSHIPFEE");
													columnsFName.add("RSHIPFEE");

													columnsFValue.add(bdlOldAMT.toString());
													columnsFValue.add(bdlOldAMT.toString());
													columnsFValue.add(bdlTotQty.toString());
													columnsFValue.add(bdlOldAMT.toString());
													columnsFValue.add("0");
													columnsFValue.add("0");
													columnsFValue.add("0");
													columnsFValue.add("0");
													columnsFValue.add("0");
													columnsFValue.add("0");

													// OC_ORDER_PAY
													ArrayList<String> columnsFName4 = new ArrayList<String>();
													ArrayList<String> columnsFValue4 = new ArrayList<String>();						

													//添加单头的字段
													for(Map.Entry<String, Object> entry : oneData.entrySet())
													{
														String mapKey = entry.getKey();
														Object mapValue = entry.getValue();

														if (mapKey.startsWith("OC_ORDER.")) 
														{
															columnsFName.add(mapKey.substring(9));
															columnsFValue.add(mapValue.toString());
														}		
														else  if (mapKey.startsWith("OC_ORDER_PAY.")) 
														{
															columnsFName4.add(mapKey.substring(13));
															columnsFValue4.add(mapValue.toString());
														}		
													}								

													DataValue[] columnsVal = new DataValue[columnsFName.size()];
													int insColCt = columnsFName.size();
													String[] columns1 = new String[insColCt];
													DataValue[] insValue = new DataValue[insColCt];
													// 依照傳入參數組譯要insert的欄位與數值；
													int ENO1 = 0;
													for (int k = 0; k < columnsVal.length; k++) 
													{
														String keyValue = columnsFName.get(k).toString();
														if (keyValue != null) 
														{
															columnsVal[k] = new DataValue(keyValue, Types.VARCHAR);
														} 
														else 
														{
															columnsVal[k] = null;
														}

														if (columnsVal[k] != null) 
														{
															columns1[ENO1] = columnsFName.get(k).toString();
															String fValue = columnsFValue.get(k).toString();
															columnsVal[k] = new DataValue(fValue, Types.VARCHAR);
															insValue[ENO1] = columnsVal[k];
															ENO1++;
															if (ENO1 >= insValue.length)
																break;
														}
													}

													InsBean ib1 = new InsBean("OC_ORDER", columns1);
													ib1.addValues(insValue);
													lstData.add(new DataProcessBean(ib1));		




													//OC_ORDER_PAY
													columnsFName4.add("EID");
													columnsFValue4.add(eId);

													columnsFName4.add("CUSTOMERNO");
													columnsFValue4.add(" ");

													columnsFName4.add("ORGANIZATIONNO");
													columnsFValue4.add(shopId);

													columnsFName4.add("SHOPID");
													columnsFValue4.add(shopId);						

													columnsFName4.add("ITEM");
													columnsFValue4.add("1");

													columnsFName4.add("LOAD_DOCTYPE");
													columnsFValue4.add(ecPlatformNo);

													columnsFName4.add("PAYCODE");
													columnsFValue4.add(payCode);

													columnsFName4.add("PAYNAME");
													columnsFValue4.add(payNameERP);

													columnsFName4.add("PAYCODEERP");
													columnsFValue4.add(payCodeERP);

													columnsFName4.add("ORDER_PAYCODE"); // 是否平台支付
													columnsFValue4.add(order_payCode);

													columnsFName4.add("ISORDERPAY"); // 是否定金
													columnsFValue4.add("N");

													columnsFName4.add("ISONLINEPAY"); // 是否平台支付
													columnsFValue4.add("Y");

													columnsFName4.add("PAY"); // 付款金额
													columnsFValue4.add(bdlOldAMT.toString());

													columnsFName4.add("RCPAY"); // 已拆单金额
													columnsFValue4.add("0");
													//付款
													columnsFName4.add("ORDERNO");
													columnsFValue4.add(orderno);

													DataValue[] columnsVal4 = new DataValue[columnsFName4.size()];
													int insColCt4 = columnsFName4.size();
													String[] columns4 = new String[insColCt4];
													DataValue[] insValue4 = new DataValue[insColCt4];
													// 依照傳入參數組譯要insert的欄位與數值；
													int ENO4 = 0;
													for (int k = 0; k < columnsVal4.length; k++) 
													{
														String keyValue = columnsFName4.get(k).toString();
														if (keyValue != null)
														{
															columnsVal4[k] = new DataValue(keyValue, Types.VARCHAR);
														} 
														else 
														{
															columnsVal4[k] = null;
														}

														if (columnsVal4[k] != null) 
														{
															columns4[ENO4] = columnsFName4.get(k).toString();
															String fValue = columnsFValue4.get(k).toString();
															columnsVal4[k] = new DataValue(fValue, Types.VARCHAR);
															insValue4[ENO4] = columnsVal4[k];
															ENO4++;
															if (ENO4 >= insValue4.length)
																break;
														}
													}

													InsBean ib4 = new InsBean("OC_ORDER_PAY", columns4);
													ib4.addValues(insValue4);
													lstData.add(new DataProcessBean(ib4));	


													//插入接單日誌信息
													String[] columnsORDER_STATUSLOG = 
														{ 
																"EID", 
																"ORGANIZATIONNO", 
																"SHOPID", 
																"ORDERNO", 
																"LOAD_DOCTYPE",
																"STATUSTYPE", 
																"STATUSTYPENAME", 
																"STATUS", 
																"STATUSNAME", 
																"NEED_NOTIFY", 
																"NOTIFY_STATUS",
																"NEED_CALLBACK", 
																"CALLBACK_STATUS", 
																"OPNO", 
																"OPNAME", 
																"UPDATE_TIME",
																"MEMO", 
																"STATUS" 
														};

													//接單日誌
													DataValue[] insValueOrderStatus_LOG = new DataValue[] 
															{ 
																	new DataValue(eId, Types.VARCHAR),
																	new DataValue(shopId, Types.VARCHAR), // 组织编号=门店编号
																	new DataValue(shopId, Types.VARCHAR), // 映射后的门店
																	new DataValue(orderno, Types.VARCHAR), //
																	new DataValue("general", Types.VARCHAR), //電商平台
																	new DataValue("1", Types.VARCHAR), // 状态类型 // 1-订单状态，2-配送状态，3-退单状态，4-其他
																	new DataValue("訂單狀態", Types.VARCHAR), // 状态类型名称
																	new DataValue("2", Types.VARCHAR), // 状态 0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
																	new DataValue("已接單", Types.VARCHAR), // 状态名称
																	new DataValue("N", Types.VARCHAR), // 是否通知云pos,N-不需要调用，Y-需要
																	new DataValue("0", Types.VARCHAR), // 通知云pos状态返回，0-未通知，1-已通知
																	new DataValue("N", Types.VARCHAR), // 是否调用第三方接口，N-不需要调用，Y-需要
																	new DataValue("0", Types.VARCHAR), // 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
																	new DataValue("admin", Types.VARCHAR), //操作員編碼
																	new DataValue("管理員", Types.VARCHAR), //操作員名稱
																	new DataValue(orderStatusLogTimes, Types.VARCHAR), //yyyyMMddHHmmssSSS
																	new DataValue("訂單狀態-->已接單", Types.VARCHAR), //類型名稱+"-->"+狀態名稱
																	new DataValue("100", Types.VARCHAR) 
															};
													InsBean ibOrderStatusLog = new InsBean("OC_ORDER_STATUSLOG", columnsORDER_STATUSLOG);
													ibOrderStatusLog.addValues(insValueOrderStatus_LOG);
													lstData.add(new DataProcessBean(ibOrderStatusLog));	
												} 
												catch (Exception e) 
												{
													WriteFTPExcelLog("金财通Excel插入订单单头异常：" + e.getMessage());
													throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "金财通Excel插入订单单头异常："+e.getMessage());
												}
											}
											
											StaticInfo.dao.useTransactionProcessData(lstData);
											String pathname = workFile + "/bak";							

											//被动模式
											ftpClient.enterLocalPassiveMode();
											// 上传下载的文件到备份位置
											ftpClient.makeDirectory(pathname);
											
											FileInputStream upInputStream = new FileInputStream(localFile);

											ftpClient.setFileType(ftpClient.BINARY_FILE_TYPE);
											ftpClient.changeWorkingDirectory(pathname);
											ftpClient.enterLocalPassiveMode();
											boolean bUpload=ftpClient.storeFile(new String(fileName.getBytes("UTF-8"),"iso-8859-1"), upInputStream);
											upInputStream.close();
											
											System.out.println("上传:"+bUpload);

											try 
											{
												ftpClient.enterLocalPassiveMode();
												// 删除ftp下载地址的文件
												ftpClient.changeWorkingDirectory(workFile);
												ftpClient.dele(new String(fileName.getBytes("UTF-8"),"iso-8859-1"));

												File myDelFile = new File(localFilePath + fileName);
												myDelFile.delete();
												myDelFile=null;

												//System.out.println("本地缓存Excel文件删除成功!!!");
											} 
											catch (Exception e) 
											{

											}
										}

									}

									ftpClient.logout();

								}
								catch (Exception e) 
								{

								}

							}

						}
					}
				}

			}
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			try 
			{
				StringWriter errors = new StringWriter();
				PrintWriter pw=new PrintWriter(errors);
				e.printStackTrace(pw);	

				pw.flush();
				pw.close();			

				errors.flush();
				errors.close();

				WriteFTPExcelLog("\r\n******讀取FTP上的Excel文件到訂單表報錯信息" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");

				pw=null;
				errors=null;
			} 
			catch (IOException e1) 
			{					
				WriteFTPExcelLog("\r\n******讀取FTP上的Excel文件到訂單表報錯信息" + e.getMessage() + "******\r\n");
			}		

			//
			sReturnInfo="錯誤信息：" + e.getMessage();
		}
		finally 
		{
			String sOrdernoStr=sbOrderno.toString();
			if (sOrdernoStr.equals("")==false) 
			{
				WriteFTPExcelLog(sOrdernoStr);
			}

			bRun=false;//
			WriteFTPExcelLog("\r\n*********從FTP導Excel到訂單表定時調用End:************\r\n");
		}

		return sReturnInfo;
	}

	/**
	 * 正则获取字符串中的IP地址
	 * @param str
	 * @return
	 */
	//	private static String getIps(String str){
	//		String IP = "";
	//		if(!TextUtils.isEmpty(str)){
	//			// 有可能带端口号
	//			// Matcher m = Pattern.compile("((\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\:\\d{1,5})").matcher(res);
	//			Matcher m = Pattern.compile("((\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3}))").matcher(str);
	//			//System.out.println(m);
	//			while(m.find()){
	//				IP = m.group(1);
	//			}
	//		}
	//		return IP;
	//	}
	/**
	 * 上传文件
	 * @param pathname ftp服务保存地址
	 * @param fileName 上传到ftp的文件名
	 *  @param originfilename 待上传文件的名称（绝对地址） * 
	 * @return
	 */
	public boolean uploadFile( String pathname, String fileName,String originfilename)
	{
		boolean flag = false;
		InputStream inputStream = null;
		try
		{
			//System.out.println("开始上传文件");
			inputStream = new FileInputStream(new File(originfilename));
			//             initFtpClient();
			ftpClient.setFileType(ftpClient.BINARY_FILE_TYPE);
			//             CreateDirecroty(pathname);
			ftpClient.makeDirectory(pathname);
			ftpClient.changeWorkingDirectory(pathname);
			ftpClient.storeFile(fileName, inputStream);
			inputStream.close();
			ftpClient.logout();
			flag = true;
			//System.out.println("上传文件成功");
		}
		catch (Exception e) 
		{

		}
		finally
		{
			if(ftpClient.isConnected())
			{ 
				try
				{
					ftpClient.disconnect();
				}
				catch(IOException e)
				{

				}
			} 
			if(null != inputStream)
			{
				try 
				{
					inputStream.close();
				} 
				catch (IOException e) 
				{

				} 
			} 
		}
		return true;
	}

	/**
	 * 初始化ftp服务器
	 */
	//     public void initFtpClient() {
	//         ftpClient = new FTPClient();
	//         ftpClient.setControlEncoding("GBK");
	//         try {
	//             //System.out.println("connecting...ftp服务器:"+this.hostname+":"+this.port); 
	//             ftpClient.connect(hostname, port); //连接ftp服务器
	//             ftpClient.login(username, password); //登录ftp服务器
	//             int replyCode = ftpClient.getReplyCode(); //是否成功登录服务器
	//             if(!FTPReply.isPositiveCompletion(replyCode)){
	//                 //System.out.println("connect failed...ftp服务器:"+this.hostname+":"+this.port); 
	//             }
	//             //System.out.println("connect successfu...ftp服务器:"+this.hostname+":"+this.port); 
	//         }catch (MalformedURLException e) { 

	//         }catch (IOException e) { 

	//         } 
	//     }

	/**
	 * 查询导入格式
	 * 
	 * @param req
	 * @return
	 */
	private String getEcImportFormat(String langtype) 
	{
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer();
		sqlbuf.append("SELECT a.EID||'_'||a.ecplatformNO||'_'||a.orderFormatNo KEY, a.EID , a.orderFormatNo , a.orderformatName , a.ecplatformNO  , a.ecplatformname  ,"
				+ " b.item , b.tableName , b.fieldName , b.fromvalue AS columnName , a.pickupway ,"
				+ " a.FTP_UID, a.FTP_PWD, a.FILEPATH , a.memberGet , a.startLine ,a.filepath,a.SHOPID,a.warehouse,a.eccustomerno,c.org_name shopname " 
				+ " FROM OC_ECORDERFORMAT a  "
				+ " left join DCP_ORG_lang c ON a.EID = c.EID AND a.SHOPID = c.organizationNo AND c.lang_type = '"+langtype+"' "
				+ " LEFT JOIN OC_ECORDERFORMAT_DETAIL b   ON a.EID = b.EID AND a.ORDERFORMATNO = b.ORDERFORMATNO ");

		sqlbuf.append( " order by a.orderformatno  , b.item ");

		sql = sqlbuf.toString();
		return sql;
	}


	/**
	 * 查询 会员接口参数
	 * @param req
	 * @return
	 * @throws Exception
	 */
	protected String getMobileParamSQL(String eId) throws Exception 
	{
		String sql = "select t.item,t.def from platform_basesettemp t where 1=1 and  "
				+ " (ITEM LIKE '%YC%' OR ITEM in ('EmailAddress', 'EmailHost' , 'EmailPassword') )"
				+ " and t.eId='" + eId + "'  and t.status='100' ";

		return sql;
	}

	protected String getMappingPaymentSQL(String eId, String ecPlatformNo) throws Exception 
	{
		String sql = "   SELECT eId , load_docType ,  payCode,  payName ,  payCodeERP , ORDER_payCode , "
				+ " order_payname ， order_PayType , customerno " + " FROM OC_mappingpayment WHERE eId = '"
				+ eId + "'  AND STATUS='100' " + " AND load_docType = '" + ecPlatformNo
				+ "'  ";
		return sql;
	}

	public void WriteFTPExcelLog(String str) throws IOException
	{
		//生成文件路径
		String sdFormat = new SimpleDateFormat("yyyyMMdd") .format(new Date());//当天日期
		String path= System.getProperty("catalina.home")+"\\myLog\\FTPEXCEL"+sdFormat+".txt";
		File file =new File(path);

		String dirpath= System.getProperty("catalina.home")+"\\myLog";
		File dirfile =new File(dirpath);
		if(!dirfile.exists())
		{
			dirfile.mkdir();
		}
		if(!file.exists())
		{
			file.createNewFile();
		}

		FileOutputStream writerStream = new FileOutputStream(file,true);
		OutputStreamWriter osw=new OutputStreamWriter(writerStream, "UTF-8");
		BufferedWriter writer = new BufferedWriter(osw);	

		//前面加上时间
		String stFormat = new SimpleDateFormat("HH:mm:ss.SSS") .format(new Date());//当天日期
		String slog=stFormat+" "+str+"\r\n";

		stFormat=null;
		sdFormat=null;

		writer.append(slog);			
		writer.close();
		writer=null;

		osw.close();
		osw=null;

		writerStream.close();
		writerStream=null;

		file=null;

		sdFormat=null;
	}




}
