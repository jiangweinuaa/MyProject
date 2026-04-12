
package com.dsc.spos.service.imp.json;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderECImportCreateReq;
import com.dsc.spos.json.cust.res.DCP_OrderECImportCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

/**
 * 电商订单导入功能
 * @author yuanyy 2019-03-08
 *
 */
public class DCP_OrderECImportCreateOld extends SPosAdvanceService<DCP_OrderECImportCreateReq, DCP_OrderECImportCreateRes> {
 
	@Override
	protected void processDUID(DCP_OrderECImportCreateReq req, DCP_OrderECImportCreateRes res) throws Exception {
		// TODO Auto-generated method stub
		Logger logger = LogManager.getLogger(DCP_OrderECImportCreateOld.class.getName());

		try {
			String sql = null;
			String eId = req.geteId();
			String shopId = req.getShopId();
			String shopName = req.getShopName();
			
			String orderFormatNo = req.getOrderFormatNo();
			String orderFormatName = req.getOrderFormatName();

			String ecPlatformNo = req.getEcplatformNo();
			String ecPlatformName = req.getEcplatformName();
			String pickupWay = req.getPickupWay();
			String pickupWayName = req.getPickupWayName();
			String memberGet = req.getMemberGet();
			String orderShop = req.getOrderShop();
			String orderWarehouse = req.getOrderWarehouse();
			String currencyNo = req.getCurrencyNo();
			
			String fileName = req.getExcelFileName();
			// 传进来的文件名 + 绝对路径 ， 组合成文件完成路径
			String filePath = System.getProperty("catalina.home")+"\\webapps\\EC\\" + ecPlatformNo + "\\import\\";
			// 暂且设置为 固定 EC + 平台名 + 文件名， 如果后期规划需要变更 平台编码，修改服务器文件夹名即可
			File file = new File(filePath);
			// 如果文件夹不存在则创建 
			if(!file.exists()&& !file.isDirectory())      
			{				
				boolean bl=file.mkdirs();
				//System.out.println(bl);				
			}			
			file=null;

			// File file = new File("C:\\Users\\Huawei\\Desktop\\shopee.xls");
			InputStream inputStream = new FileInputStream(filePath+fileName);
			Workbook workbook = Workbook.getWorkbook(inputStream);
			Sheet sheet = workbook.getSheet(0);
			int rows = sheet.getRows();
			int columns = sheet.getColumns();

			Calendar cal = Calendar.getInstance();// 获得当前时间
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
			String sDate = df.format(cal.getTime());
			df = new SimpleDateFormat("HHmmss");
			String sTime = df.format(cal.getTime());

			// 下面需要写五种特殊 电商格式，每一家电商导出的格式都不一样
			if (ecPlatformNo.equals("shopee")) {
				// i 是行号 , j 是列号
				for (int i = 1; i < rows; i++) {
					String[] columnsName1 = { "EID", "CUSTOMERNO", "ORGANIZATIONNO", "SHOPID", "SHOPNAME",
							"ORDERNO", "LOAD_DOCTYPE", // 6
							// 以上为固定插入字段， 下面的是非固定插入字段， 字段值来源是导入格式
							"STATUS", "REFUNDSTATUS", "CARDNO", "CREATE_DATETIME", "SDATE", "STIME", // 12
							"TOT_AMT", "SHIPFEE", "TOT_OLDAMT", "ADDRESS", "CITY", "COUNTY", // 18
							"GETMAN", "GETMANTEL", "SHIPTYPE", "PICKUPWAY", "MEMO" };
					String[] columnsName2 = { "EID", "CUSTOMERNO", "ORGANIZATIONNO", "SHOPID", "ORDERNO", "ITEM",
							// 以上为固定插入字段， 下面的是非固定插入字段， 字段来源是导入格式
							"PLUNAME", "SPECNAME", "PRICE", "QTY" };

					DataValue[] columnsVal1 = new DataValue[columnsName1.length];
					DataValue[] columnsVal2 = new DataValue[columnsName2.length];

					columnsVal1[0] = new DataValue(eId, Types.VARCHAR);
					columnsVal1[1] = new DataValue(" ", Types.VARCHAR);// 电商导入订单，
					// CUSTOMERNO
					// 都给固定值。
					columnsVal1[2] = new DataValue(shopId, Types.VARCHAR);
					columnsVal1[3] = new DataValue(shopId, Types.VARCHAR);
					columnsVal1[4] = new DataValue(shopName, Types.VARCHAR);

					columnsVal2[0] = new DataValue(eId, Types.VARCHAR);
					columnsVal2[1] = new DataValue(" ", Types.VARCHAR);// 电商导入订单，
					// CUSTOMERNO
					// 都给固定值。
					columnsVal2[2] = new DataValue(shopId, Types.VARCHAR);
					columnsVal2[3] = new DataValue(shopId, Types.VARCHAR);

					for (int j = 0; j < columns - 1; j++) {
						// 根据列号，获取列对应的 表名、列名? 疑问：可以不需要导入格式， EXCEL的格式需要固定，
						// 做成模板的形式， 固定列
						// 暂时先不验证 表中主键和非空列 的值是否为空 ， 这里必须加上 控制

						Cell cellOrderNo = sheet.getCell(0, i);
						String orderNo = cellOrderNo.getContents(); // 得到每一行的订单编号

						columnsVal1[5] = new DataValue(orderNo, Types.VARCHAR); // 单头订单号
						columnsVal2[4] = new DataValue(orderNo, Types.VARCHAR); // 单身订单号

						Cell cell = sheet.getCell(j, i);
						String dataValue = cell.getContents(); //// 得到单元格的值

						columnsVal1[6] = new DataValue(ecPlatformNo, Types.VARCHAR); // 电商平台编码
						// 对应
						// 来源单类型
						switch (j) {
						case 1: // 订单状态
							// if (dataValue.equals("待出货")) {
							// dataValue = "9";
							// } else if (dataValue.equals("已发货")) {
							// dataValue = "10";
							// } else if (dataValue.equals("已完成")) {
							// dataValue = "11";
							// } else if (dataValue.equals("已退单")) {
							// dataValue = "12";
							// } else {
							// dataValue = "0"; // 需调度
							// }
							dataValue = "1";
							columnsVal1[7] = new DataValue(dataValue, Types.VARCHAR);
							break;
						case 2: // 退货/退款状态
							if (dataValue.equals("未申请") || dataValue.equals("")) {
								dataValue = "1"; // 未申请
							} else if (dataValue.equals("申请退单中")) {
								dataValue = "2";
							} else if (dataValue.equals("拒绝退单")) {
								dataValue = "3";
							} else if (dataValue.equals("客服仲裁中")) {
								dataValue = "4";
							} else if (dataValue.equals("退单失败")) {
								dataValue = "5";
							} else if (dataValue.equals("退单成功")) {
								dataValue = "6";
							} else {
								dataValue = "1"; // 默认未申请
							}
							columnsVal1[8] = new DataValue(dataValue, Types.VARCHAR);
							break;
						case 3:
							columnsVal1[9] = new DataValue(dataValue, Types.VARCHAR);
							break;
						case 4: // create_datetime
							// 这里应该填Excel里的日期， 格式有问题， 先写个固定值
							columnsVal1[10] = new DataValue("20190326121110", Types.VARCHAR);
							break;
						case 5:
							columnsVal1[11] = new DataValue(sDate, Types.VARCHAR);
							columnsVal1[12] = new DataValue(sTime, Types.VARCHAR);
							break;
						case 6: // 订单小计 tot_amt
							columnsVal1[13] = new DataValue(dataValue, Types.VARCHAR);
							break;
						case 7: // 买家支付的运费 shipee
							columnsVal1[14] = new DataValue(dataValue, Types.VARCHAR);
							break;
						case 8: // 订单总金额 TOT_OLDAMT
							columnsVal1[15] = new DataValue(dataValue, Types.VARCHAR);
							break;
						case 12:
							// 第12列 单元格中存的是 明细信息
							// columnsVal2[9] = new DataValue(keyVal,
							// Types.VARCHAR);
							// ---------- 以下为解析“商品资讯”单元格的内容-------------
							/*
							 * 虾皮 "商品资讯" 需要注意： 一： 每一行的序号必须以 [] 开头，里面填写行序号；
							 * 二：key:value之间必须以英文冒号分隔， 且以英文分号结尾
							 */

							/// 列 ， 行
							Cell pluDatas = sheet.getCell(12, 1);/// 商品资讯
							String allValue = pluDatas.getContents();
							// 计算总共有多少行
							int fromIndex = 0;
							int countIndex = 0;
							while (true) {
								int index = allValue.indexOf("[", fromIndex);
								if (-1 != index) {
									fromIndex = index + 1;
									countIndex++;
								} else {
									break;
								}
							}

							String pluList[] = allValue.trim().split(";");//// 这里必须去空格：
							//// 表格中存在换行，换行后会出现空格行
							int totalNum = pluList.length; // 得到该单元格总个数，用于下面得到每行每个元素的值
							int avgNum = totalNum / countIndex; // 每一行元素个数
							// "ITEM","PLUNAME","QTY","PRICE","SPECNAME"

							for (int n = 1; n < countIndex + 1; n++) {
								String item = n + ""; // 得到明细表中序号
								columnsVal2[5] = new DataValue(item, Types.VARCHAR);
								int insColCt = 0;
								for (int m = 1; m < avgNum + 1; m++) {
									String keyVal = "";
									int index = 0;
									String tempStr = "";
									switch (m) {
									case 1: // pluName
										index = (n - 1) * 6 + 0;
										tempStr = pluList[index];
										String pluName = tempStr.substring(tempStr.indexOf(":") + 1);
										keyVal = pluName;
										columnsVal2[6] = new DataValue(keyVal, Types.VARCHAR);
										break;
									case 2: // specName
										index = (n - 1) * 6 + 1;
										tempStr = pluList[index];
										String specName = tempStr.substring(tempStr.indexOf(":") + 1);
										keyVal = specName;
										columnsVal2[7] = new DataValue(keyVal, Types.VARCHAR);
										break;
									case 3: // price
										index = (n - 1) * 6 + 2;
										tempStr = pluList[index];
										String price = tempStr.substring(tempStr.indexOf(":") + 1);
										price = price.replace("$", "");
										keyVal = price.trim().replace("￥", "");
										columnsVal2[8] = new DataValue(keyVal, Types.VARCHAR);
										break;
									case 4: // qty
										index = (n - 1) * 6 + 3;
										tempStr = pluList[index];
										String qty = tempStr.substring(tempStr.indexOf(":") + 1);
										keyVal = qty.trim();
										columnsVal2[9] = new DataValue(keyVal, Types.VARCHAR);
										break;
									default:
										break;
									}

								}
								// 添加每笔订单明细
								insColCt = columnsName2.length;
								String[] columns2 = new String[insColCt];
								DataValue[] insValue2 = new DataValue[insColCt];
								// 依照傳入參數組譯要insert的欄位與數值；
								insColCt = 0;

								for (int k = 0; k < columnsVal2.length; k++) {
									if (columnsVal2[k] != null) {
										columns2[insColCt] = columnsName2[k];
										insValue2[insColCt] = columnsVal2[k];
										insColCt++;
										if (insColCt >= insValue2.length)
											break;
									}
								}

								InsBean ib2 = new InsBean("OC_ORDER_DETAIL", columns2);
								ib2.addValues(insValue2);
								this.addProcessData(new DataProcessBean(ib2));
								// 添加结束
							}
							break;

						case 15: // 地址+门店编号 ， 需要去掉空格和换行
							String wait = dataValue.replaceAll("\r\n|\r|\n", "");
							columnsVal1[16] = new DataValue(wait, Types.VARCHAR);
							break;

						case 17: // 城市 city
							columnsVal1[17] = new DataValue(dataValue, Types.VARCHAR);
							break;
						case 18: // 区 县
							columnsVal1[18] = new DataValue(dataValue, Types.VARCHAR);
							break;

						case 20: // 收货人 getman
							columnsVal1[19] = new DataValue(dataValue, Types.VARCHAR);
							break;
						case 21: // 收货人电话 getManTel
							columnsVal1[20] = new DataValue(dataValue, Types.VARCHAR);
							break;

						case 22: // 配送方式 shipType 先给 2： 配送 pickupWay 2 ：宅配
							columnsVal1[21] = new DataValue("2", Types.VARCHAR);
							columnsVal1[22] = new DataValue("2", Types.VARCHAR);
							break;
						case 31: // 买家备注 memo
							columnsVal1[23] = new DataValue(dataValue, Types.VARCHAR);
							break;
							// 这里不能设置 default, 跳出循环可能会导致 后续case 无法赋值
							// default :
							// break;
						}

					}

					int insColCt1 = columnsName1.length;
					String[] columns1 = new String[insColCt1];
					DataValue[] insValue1 = new DataValue[insColCt1];
					// 依照傳入參數組譯要insert的欄位與數值；
					insColCt1 = 0;

					for (int k = 0; k < columnsVal1.length; k++) {
						if (columnsVal1[k] != null) {
							columns1[insColCt1] = columnsName1[k];
							insValue1[insColCt1] = columnsVal1[k];
							insColCt1++;
							if (insColCt1 >= insValue1.length)
								break;
						}
					}

					// ---------- 接下来插入主表的数据------------
					InsBean ib1 = new InsBean("OC_ORDER", columns1);
					ib1.addValues(insValue1);
					this.addProcessData(new DataProcessBean(ib1));

					this.doExecuteDataToDB();
				}
			} // shopee 类型到此结束
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败");	
		}
		
		
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderECImportCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderECImportCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderECImportCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderECImportCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
	    StringBuffer errMsg = new StringBuffer("");
	    int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
	    String orderFormatNo = req.getOrderFormatNo();
	    if(Check.Null(orderFormatNo))
    	{
	      errCt++;
	      errMsg.append("格式编号不可为空值, ");
	      isFail = true;
	    }
		
		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderECImportCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderECImportCreateReq>(){};
	}

	@Override
	protected DCP_OrderECImportCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderECImportCreateRes();
	}
	
	/**
	 * 查询导入格式
	 * 
	 * @param req
	 * @return
	 */
	private String getEcImportFormat(DCP_OrderECImportCreateReq req) {
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer();
		String eId = req.geteId();
		String orderFormatNo = req.getOrderFormatNo();

		sqlbuf.append("SELECT a.EID , a.orderFormatNo , a.orderformatName , a.ecplatformNO  , a.ecplatformname  ,"
				+ " b.item , b.tableName , b.fieldName , b.fromvalue AS columnName , a.pickupway ,a.memberget  " 
				+ " FROM OC_ECORDERFORMAT a  "
				+ " LEFT JOIN OC_ECORDERFORMAT_DETAIL b   ON a.EID = b.EID AND a.ORDERFORMATNO = b.ORDERFORMATNO "
				+ " WHERE a.EID = '" + eId + "'  AND a.orderformatNo = '" + orderFormatNo + "'"
				+ " order by b.item ");

		sql = sqlbuf.toString();
		return sql;
	}
	
	
	/**
	 * 验证字段类型 
	 * @param columnName
	 * @param tableName
	 * @return
	 */
	private String getColumnType(String tableName){
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer();
		
		sqlbuf.append("select COLUMN_NAME,DATA_TYPE,DATA_PRECISION,DATA_SCALE,NULLABLE,data_default "
				+ " from user_tab_columns  "
				+ " where table_name ='"+tableName+"'  AND NULLABLE = 'N' "
		);
		
		sql = sqlbuf.toString();
		return sql;
	}
	
	
}
