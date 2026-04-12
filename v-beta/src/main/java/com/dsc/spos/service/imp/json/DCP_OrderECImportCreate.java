package com.dsc.spos.service.imp.json;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderECImportCreateReq;
import com.dsc.spos.json.cust.res.DCP_OrderECImportCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.EncryptUtils;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

/**
 * 电商订单导入功能
 * 
 * @author yuanyy 2019-03-08
 *
 */
public class DCP_OrderECImportCreate
extends SPosAdvanceService<DCP_OrderECImportCreateReq, DCP_OrderECImportCreateRes> {

	Logger logger = LogManager.getLogger(DCP_OrderECImportCreate.class.getName());

	@SuppressWarnings("resource")
	@Override
	protected void processDUID(DCP_OrderECImportCreateReq req, DCP_OrderECImportCreateRes res) throws Exception {
		// TODO Auto-generated method stub
		Logger logger = LogManager.getLogger(DCP_OrderECImportCreate.class.getName());

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Date zero = calendar.getTime();

		calendar = Calendar.getInstance();
		SimpleDateFormat dfDatetime = new SimpleDateFormat("yyyyMMdd");
		String sysDate=dfDatetime.format(calendar.getTime());
		dfDatetime = new SimpleDateFormat("HHmmss");
		String sysTime=dfDatetime.format(calendar.getTime());

		//訂單日誌時間
		dfDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String orderStatusLogTimes=dfDatetime.format(calendar.getTime());

		String sql = "";
		try {

			String eId = req.geteId();
			String shopId = req.getShopId();
			String shopName = req.getShopName();
			String orderFormatNo = req.getOrderFormatNo();
			String orderFormatName = req.getOrderFormatName();
			String ecPlatformNo = req.getEcplatformNo();
			// String ecPlatformName = req.getEcplatformName();
			// String pickupWay = req.getPickupWay();
			// String pickupWayName = req.getPickupWayName();
			// String memberGet = req.getMemberGet();
			// String orderShop = req.getOrderShop();
			// String orderWarehouse = req.getOrderWarehouse();
			// String currencyNo = req.getCurrencyNo();

			String paySql = this.getMappingPaymentSQL(req);
			List<Map<String, Object>> payData = this.doQueryData(paySql, null);

			if (payData==null || payData.isEmpty()) 
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,
						req.getEcplatformName() + ":找不到該電商平台支付方式映射信息");	
			}

			String fileName = req.getExcelFileName();

			sql = this.getEcImportFormat(req);
			String[] conditionValues = {};
			List<Map<String, Object>> getDatas = this.doQueryData(sql, conditionValues);
			String fileSuffix = "";
			String ecCustomerNo = "";
			String pickUpWay = "1";
			if (getDatas.size() > 0) {
				fileSuffix = getDatas.get(0).get("FILEPATH").toString();
				ecCustomerNo = getDatas.get(0).get("ECCUSTOMERNO").toString();
				int beginIndex = fileSuffix.lastIndexOf("/");
				fileSuffix = fileSuffix.substring(beginIndex + 1);

				pickUpWay = getDatas.get(0).get("PICKUPWAY").toString();

			}



			// 传进来的文件名 + 绝对路径 ， 组合成文件完成路径
			String filePath = System.getProperty("catalina.home") + "\\webapps\\EC\\" + ecPlatformNo + "\\import\\"
					+ fileSuffix + "\\";
			// 暂且设置为 固定 EC + 平台名 + 文件名， 如果后期规划需要变更 平台编码，修改服务器文件夹名即可
			File file = new File(filePath);
			// 如果文件夹不存在则创建
			if (!file.exists() && !file.isDirectory()) 
			{
				boolean bl = file.mkdirs();
			}
			file = null;

			// File file = new File("C:\\Users\\Huawei\\Desktop\\shopee.xls");
			InputStream inputStream = new FileInputStream(filePath + fileName);
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
			if (ecPlatformNo.equals("shopee")) 
			{
				// 得到 标题行，用于和 导入格式中的 columnName 做比较
				Cell[] headRow = sheet.getRow(0);
				int detailItem = 1; // 子表item
				if (getDatas.size() > 0) {
					// 循环行 ， 从第七行开始， 前面几行为固定行

					for (int i = 1; i < rows; i++) {
						int num = 0; // 这里声明num，用来记录主表插入的次数， 一个订单多个商品，主表数据只能
						// 插入一次

						// OC_ORDER
						ArrayList<String> columnsFName = new ArrayList<String>();
						ArrayList<String> columnsFValue = new ArrayList<String>();

						// OC_ORDER_DETAIL
						ArrayList<String> columnsFName2 = new ArrayList<String>();
						ArrayList<String> columnsFValue2 = new ArrayList<String>();

						// OC_ORDER_AGIO 订单折扣表（目前只有虾皮有折扣， 可能折扣有多种
						// ），格式设置里：设置商家承担金额、 平台承担金额 即可。
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
						columnsFValue.add("1"); // 退货退订状态 ： 1 未申请
						columnsFValue.add(sysDate+sysTime);
						columnsFValue.add(ecCustomerNo);

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
						memberGet = getDatas.get(0).get("MEMBERGET").toString();
						orderNo = rowValue[0].getContents();

						BigDecimal sellerDisc = new BigDecimal(0);
						BigDecimal platformDisc = new BigDecimal(0);
						BigDecimal agioAmt = new BigDecimal(0);

						for (Map<String, Object> oneData : getDatas) {
							String columnItem = oneData.get("ITEM").toString();
							String tableName = oneData.get("TABLENAME").toString();
							String columnName = oneData.get("COLUMNNAME").toString(); // excel列名
							String fieldName = oneData.get("FIELDNAME").toString().toUpperCase(); // 数据库列名，转换为大写

							for (int j = 0; j < columns; j++) {
								String columnIndex = (j + 1) + "";
								String headRowName = headRow[j].getContents();
								String fieldValue = "";
								fieldValue = rowValue[j].getContents();

								if (headRowName.equals(columnName) && tableName.equals("OC_ORDER")) {

									columnsFName.add(fieldName);
									columnsFValue.add(fieldValue);

									if (fieldName.equals("ORDERNO")) {
										orderNo = fieldValue;
										agioColumnsFName.add("ORDERNO");
										agioColumnsFValue.add(orderNo);

									}
									// 获取订购人，订购人电话信息
									if (fieldName.equals("CONTMAN")) {
										contMan = fieldValue;
									}
									if (fieldName.equals("CONTTEL")) {
										contTel = fieldValue;
									}

								}
								if (columnIndex.equals(columnItem) && headRowName.equals(columnName)
										&& tableName.equals("OC_ORDER_DETAIL")) {
									if (headRowName.equals("商品資訊") || headRowName.equals("商品资讯")) {
										// 计算总共有多少行
										int fromIndex = 0;
										int countIndex = 0;
										while (true) {
											int index = fieldValue.indexOf("[", fromIndex);
											if (-1 != index) {
												fromIndex = index + 1;
												countIndex++;
											} else {
												break;
											}
										}

										String pluList[] = fieldValue.trim().split(";");//// 这里必须去空格：
										//// 表格中存在换行，换行后会出现空格行
										int totalNum = pluList.length; // 得到该单元格总个数，用于下面得到每行每个元素的值
										int avgNum = totalNum / countIndex; // 每一行元素个数
										// "ITEM","PLUNAME","QTY","PRICE","SPECNAME"

										for (int n = 1; n < countIndex + 1; n++) {
											String item = n + ""; // 得到明细表中序号
											columnsFName2.add("ITEM");
											columnsFValue2.add(item);
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
									} else {
										columnsFName2.add(fieldName);
										columnsFValue2.add(fieldValue);
									}

								} // OC_ORDER_DETAIL 结束

								if (headRowName.equals(columnName) && tableName.equals("OC_ORDER_AGIO")) {
									// 折扣表 加折扣金额， AGIOAMT 计算总额即可
									// 将所有的折扣金额都加起来
									if (fieldName.equals("SELLER_DISC")) {
										if (fieldValue == null || fieldValue.trim().equals("")) {
											fieldValue = "0";
										}
										sellerDisc = new BigDecimal(fieldValue);
										sellerDisc.add(sellerDisc);

									}
									sellerDisc = sellerDisc.setScale(2, RoundingMode.HALF_UP); // 保留两位小数
									agioAmt = agioAmt.add(sellerDisc); // 总折扣额 =
									// 商家折扣额
									// +
									// 平台折扣额

									if (fieldName.equals("PLATFORM_DISC")) {
										if (fieldValue == null || fieldValue.trim().equals("")) {
											fieldValue = "0";
										}
										platformDisc = new BigDecimal(fieldValue);
										platformDisc.add(platformDisc);
									}
									platformDisc = platformDisc.setScale(2, RoundingMode.HALF_UP);
									agioAmt = agioAmt.add(platformDisc);

								}

							}

						}
						columnsFName2.add("ORDERNO");
						columnsFValue2.add(orderNo);

						DataValue[] columnsVal = new DataValue[columnsFName.size()];
						int insColCt = columnsFName.size();
						String[] columns1 = new String[insColCt];
						DataValue[] insValue = new DataValue[insColCt];
						// 依照傳入參數組譯要insert的欄位與數值；
						int ENO1 = 0;
						for (int k = 0; k < columnsVal.length; k++) {
							String keyValue = columnsFName.get(k).toString();
							if (keyValue != null) {
								columnsVal[k] = new DataValue(keyValue, Types.VARCHAR);
							} else {
								columnsVal[k] = null;
							}

							if (columnsVal[k] != null) {
								columns1[ENO1] = columnsFName.get(k).toString();
								String fValue = columnsFValue.get(k).toString();
								columnsVal[k] = new DataValue(fValue, Types.VARCHAR);
								insValue[ENO1] = columnsVal[k];
								ENO1++;
								if (ENO1 >= insValue.length)
									break;
							}
						}
						// 是否按照表行来设置插入值， 可以根据正常情况下的值，
						InsBean ib1 = new InsBean("OC_ORDER", columns1);
						ib1.addValues(insValue);
						this.addProcessData(new DataProcessBean(ib1));

						DataValue[] columnsVal2 = new DataValue[columnsFName2.size()];
						int insColCt2 = columnsFName2.size();
						String[] columns2 = new String[insColCt2];
						DataValue[] insValue2 = new DataValue[insColCt2];
						// 依照傳入參數組譯要insert的欄位與數值；
						int ENO2 = 0;
						for (int k = 0; k < columnsVal2.length; k++) {
							String keyValue = columnsFName2.get(k).toString();
							if (keyValue != null) {
								columnsVal2[k] = new DataValue(keyValue, Types.VARCHAR);
							} else {
								columnsVal2[k] = null;
							}

							if (columnsVal2[k] != null) {
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
						this.addProcessData(new DataProcessBean(ib2));

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
						for (int k = 0; k < agiocolumnsVal.length; k++) {
							String keyValue = agioColumnsFName.get(k).toString();
							if (keyValue != null) {
								agiocolumnsVal[k] = new DataValue(keyValue, Types.VARCHAR);
							} else {
								agiocolumnsVal[k] = null;
							}

							if (agiocolumnsVal[k] != null) {
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
						this.addProcessData(new DataProcessBean(ibAgio));

					}
				}

			} // shopee 类型到此结束

			// ----------- 下面判断其他类型 ----------
			else if (ecPlatformNo.equals("pchome") && fileSuffix.equals("allDay")) 
			{
				if (getDatas.size() > 0) {

					String startLine = getDatas.get(0).get("STARTLINE").toString();
					int startIndex = Integer.parseInt(startLine);
					// 循环行 ， 从第七行开始， 前面几行为固定行
					// 得到 标题行，用于和 导入格式中的 columnName 做比较
					Cell[] headRow = sheet.getRow(startIndex - 1);
					int detailItem = 1; // 子表item

					int qtyIndex = 0;
					int amtIndex = 0;
					int pluNameIndex = 0;

					for (int i = 0; i < columns; i++) {
						if (headRow[i].getContents().equals("數量")) {
							qtyIndex = i;
						}
						if (headRow[i].getContents().equals("金額小計")) {
							amtIndex = i;
						}
						if (headRow[i].getContents().equals("商品名稱")) {
							pluNameIndex = i;
						}

					}

					// 最少四行为一单，用 isNext 来区分是否是新单
					boolean isNext = false;

					// OC_ORDER
					ArrayList<String> columnsFName = new ArrayList<String>();
					ArrayList<String> columnsFValue = new ArrayList<String>();

					// OC_ORDER_DETAIl
					ArrayList<String> columnsFName2 = new ArrayList<String>();
					ArrayList<String> columnsFValue2 = new ArrayList<String>();

					// OC_ORDER_AGIO
					ArrayList<String> columnsFName3 = new ArrayList<String>();
					ArrayList<String> columnsFValue3 = new ArrayList<String>();

					// OC_ORDER_PAY
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

					for (int i = startIndex; i < rows; i++) {

						Cell cVal = sheet.getCell(4, i);// 固定第五列存在 "總計" 這兩個字，
						// 若該行為總計， 則下一行為空
						String ss = cVal.getContents();

						String order_payName = "";
						String payCode = "";
						String payCodeERP = "";
						String payNameERP = "";
						String order_payCode = "";

						boolean isTotal = false;

						if (ss.equals("總計")) {
							totAmt = sheet.getCell(amtIndex, i).getContents();
							isTotal = true;
						}

						if (isNext == false) {
							// 定義當前行是商品、折扣、運費
							boolean isPlu = false;
							boolean isAgio = false;
							boolean isTransFee = false;

							Cell[] rowValue = sheet.getRow(i);
							boolean qtyIsMax = false;
							boolean amtIsMax = false;

							String promName = "";
							String qty = rowValue[qtyIndex].getContents();
							String amtStr = rowValue[amtIndex].getContents();

							if (!qty.isEmpty() && Integer.parseInt(qty) > 0) {
								qtyIsMax = true;
							} else {
								qtyIsMax = false;
							}

							if (!amtStr.isEmpty() && Integer.parseInt(amtStr) > 0) {
								amtIsMax = true;
							} else {
								amtIsMax = false;
							}

							if (qtyIsMax && amtIsMax) {
								isPlu = true;
								payRowIndex = i;

							} else if (qtyIsMax == false && amtIsMax == false && !amtStr.isEmpty()
									&& amtStr.length() > 0) {
								isAgio = true;
								pluDisc = amtStr;

							} else if (qtyIsMax == false && amtIsMax == true && isTotal == false) { // 這個很特殊，
								// 必須加上當前行不爲
								// “總計”的驗證
								isTransFee = true;
								promName = rowValue[pluNameIndex].getContents();
							} else {
								isPlu = false;
								isAgio = false;
								isTransFee = false;
							}

							int rowColumns = sheet.getRow(i).length;
							memberGet = getDatas.get(0).get("MEMBERGET").toString();

							if (isPlu) {
								columnsFName.add("EID");
								columnsFName.add("CUSTOMERNO");
								columnsFName.add("ORGANIZATIONNO");
								columnsFName.add("SHOPID");
								columnsFName.add("SHOPNAME");
								columnsFName.add("LOAD_DOCTYPE");
								columnsFName.add("STATUS");
								columnsFName.add("DELIVERYTYPE");
								columnsFName.add("SHIPTYPE");
								columnsFName.add("CREATE_DATETIME");
								columnsFName.add("ECCUSTOMERNO");

								columnsFValue.add(eId);
								columnsFValue.add(" ");
								columnsFValue.add(shopId);
								columnsFValue.add(shopId);
								columnsFValue.add(shopName);
								columnsFValue.add(ecPlatformNo);
								columnsFValue.add("2"); // 2 已接单
								columnsFValue.add("14"); // 卖家宅配
								columnsFValue.add("6"); // 超商
								columnsFValue.add(sysDate+sysTime);
								columnsFValue.add(ecCustomerNo);

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
							if (isTransFee) {

								columnsFName.add("MEMO");
								columnsFValue.add(promName);

								columnsFName.add("SHIPFEE");
								columnsFValue.add(amtStr);

								columnsFName.add("TOTSHIPFEE");
								columnsFValue.add(amtStr);
							}
							if (isAgio) {

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
								if (amtStr.trim().equals("") || amtStr == null) {
									amtInt = 0;
								} else {
									amtInt = Math.abs(Integer.parseInt(amtStr));
								}
								agioAmt = amtInt + "";
								pluDisc = amtInt + "";

								columnsFValue3.add(promName);
								columnsFValue3.add(agioAmt);
								columnsFValue3.add(ecPlatformNo);
							}

							for (Map<String, Object> oneData : getDatas) {
								String item = oneData.get("ITEM").toString();
								String columnName = oneData.get("COLUMNNAME").toString();
								String fieldName = oneData.get("FIELDNAME").toString().toUpperCase(); // 数据库列名，转换为大写
								String tableName = oneData.get("TABLENAME").toString().toUpperCase(); // 表名，转换为大写

								if (columnName.equals("付款方式")) {
									payColIndex = Integer.parseInt(item) - 1;
								}

								for (int j = 0; j < rowColumns; j++) {
									String index = (j + 1) + "";
									String headRowName = headRow[j].getContents();

									// 需要加個判斷， 控制每個商品行在第一行上
									// 當前行為商品行
									if (isPlu) {

										if (index.equals(item) && headRowName.equals(columnName)) {
											if (tableName.equals("OC_ORDER_DETAIL")) {
												columnsFName2.add(fieldName);
												String fieldValue = rowValue[j].getContents();
												columnsFValue2.add(fieldValue);
											} else if (tableName.equals("OC_ORDER")) {
												columnsFName.add(fieldName);
												String fieldValue = rowValue[j].getContents();

												// 日期需要转换格式， 传的日期是 2010/03/13
												// ，需要转换为 20190313 形式
												if (fieldName.equals("SDATE")) {
													if (!fieldValue.isEmpty()) {
														SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
														Date date = sdf.parse(fieldValue);
														sdf = new SimpleDateFormat("yyyyMMdd");
														fieldValue = sdf.format(date);
													}
												}
												columnsFValue.add(fieldValue);

												if (fieldName.equals("ORDERNO")) {
													orderNo = fieldValue;
												}

												// 获取订购人，订购人电话信息
												if (fieldName.equals("CONTMAN")) {
													contMan = fieldValue;
												}
												if (fieldName.equals("CONTTEL")) {
													contTel = fieldValue;
												}

											}
										}

									}

								}

							}

						} else {

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
						if (i < rows - 1) {
							Cell[] memoCol = sheet.getRow(i + 1);
							nextValue = memoCol[0].getContents().trim();

							if (nextValue.trim().isEmpty() == false || (i + 2 == rows)) {
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

								BigDecimal oldAmt = totAmtBD.add(agioAmtBD); // 加法
								BigDecimal pluAfterAmt = pluAmtBD.subtract(agioAmtBD); // 减法

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
								// String tot = sheet.getCell(4,
								// i).getContents();
								// if (columnName.equals("付款方式")) {

								// 索引 == -1，说明表格中没有付款档信息
								if (payColIndex != -1 && payRowIndex != -1) 
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
									// }

									columnsFName4.add("PAY");
									columnsFValue4.add(totAmt);

									columnsFName4.add("ISORDERPAY"); // 是否定金
									columnsFValue4.add("N");

									columnsFName4.add("ISONLINEPAY"); // 是否平台支付
									columnsFValue4.add("Y");

									columnsFName4.add("RCPAY"); // 已拆单金额
									columnsFValue4.add("0");


								}

								/**
								 * 验证订单是否存在这一步不应该出现， 一个excel 表中的订单， 要么导入全部成功，要么都不成功 
								 */
								//验证该订单是否已经存在
								String isRepeatSql = this.isRepeatOrder(req, orderNo);
								List<Map<String, Object>> getRepeatDatas = this.doQueryData(isRepeatSql, null);
								if (getRepeatDatas.size() > 0) {

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

									continue;
								}

								DataValue[] columnsVal = new DataValue[columnsFName.size()];
								int insColCt = columnsFName.size();
								String[] columns1 = new String[insColCt];
								DataValue[] insValue = new DataValue[insColCt];
								// 依照傳入參數組譯要insert的欄位與數值；
								int ENO1 = 0;
								for (int k = 0; k < columnsVal.length; k++) {
									String keyValue = columnsFName.get(k).toString();
									if (keyValue != null) {
										columnsVal[k] = new DataValue(keyValue, Types.VARCHAR);
									} else {
										columnsVal[k] = null;
									}

									if (columnsVal[k] != null) {
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
								this.addProcessData(new DataProcessBean(ib1));


								//--------------2019-07-19 增加订单日志
								//接單日誌
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
												new DataValue("pchome", Types.VARCHAR), //電商平台
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
								//								lstData.add(new DataProcessBean(ibOrderStatusLog));	
								this.addProcessData(new DataProcessBean(ibOrderStatusLog));


								DataValue[] columnsVal2 = new DataValue[columnsFName2.size()];
								int insColCt2 = columnsFName2.size();
								String[] columns2 = new String[insColCt2];
								DataValue[] insValue2 = new DataValue[insColCt2];
								// 依照傳入參數組譯要insert的欄位與數值；
								int ENO2 = 0;
								for (int k = 0; k < columnsVal2.length; k++) {
									String keyValue = columnsFName2.get(k).toString();
									if (keyValue != null) {
										columnsVal2[k] = new DataValue(keyValue, Types.VARCHAR);
									} else {
										columnsVal2[k] = null;
									}

									if (columnsVal2[k] != null) {
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
								this.addProcessData(new DataProcessBean(ib2));

								if (columnsFName3.size() > 0) {

									DataValue[] columnsVal3 = new DataValue[columnsFName3.size()];
									int insColCt3 = columnsFName3.size();
									String[] columns3 = new String[insColCt3];
									DataValue[] insValue3 = new DataValue[insColCt3];
									// 依照傳入參數組譯要insert的欄位與數值；
									int ENO3 = 0;
									for (int k = 0; k < columnsVal3.length; k++) {
										String keyValue = columnsFName3.get(k).toString();
										if (keyValue != null) {
											columnsVal3[k] = new DataValue(keyValue, Types.VARCHAR);
										} else {
											columnsVal3[k] = null;
										}

										if (columnsVal3[k] != null) {
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
									this.addProcessData(new DataProcessBean(ib3));
								}

								// 插入付款档， OC_ORDER_PAY
								if (columnsFName4.size() > 0) {

									DataValue[] columnsVal4 = new DataValue[columnsFName4.size()];
									int insColCt4 = columnsFName4.size();
									String[] columns4 = new String[insColCt4];
									DataValue[] insValue4 = new DataValue[insColCt4];
									// 依照傳入參數組譯要insert的欄位與數值；
									int ENO4 = 0;
									for (int k = 0; k < columnsVal4.length; k++) {
										String keyValue = columnsFName4.get(k).toString();
										if (keyValue != null) {
											columnsVal4[k] = new DataValue(keyValue, Types.VARCHAR);
										} else {
											columnsVal4[k] = null;
										}

										if (columnsVal4[k] != null) {
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
									this.addProcessData(new DataProcessBean(ib4));
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

							} else {
								isNext = false;
							}
						}

					}
				}
				this.doExecuteDataToDB();
			}

			else if (ecPlatformNo.equals("pchome") && fileSuffix.equals("normal")) 
			{
				// 得到 标题行，用于和 导入格式中的 columnName 做比较

				String startLine = getDatas.get(0).get("STARTLINE").toString();
				int startIndex = Integer.parseInt(startLine);
				// 循环行 ， 从第七行开始， 前面几行为固定行
				// 得到 标题行，用于和 导入格式中的 columnName 做比较
				Cell[] headRow = sheet.getRow(startIndex - 1);

				String price = "0";
				String pluAmt = "0";
				String pluQty = "0";

				BigDecimal totQty = new BigDecimal(0);
				BigDecimal totAmt = new BigDecimal(0);

				//				String totQty = "0";
				//				String totAmt = "0";

				int detailItem = 1; // 子表item
				if (getDatas.size() > 0) {

					for (int i = startIndex; i < rows; i++) {
						int num = 0; // 这里声明num，用来记录主表插入的次数， 一个订单多个商品，主表数据只能
						// 插入一次
						String order_payName = "";
						String payCode = "";
						String payCodeERP = "";
						String payNameERP = "";
						String order_payCode = "";

						ArrayList<String> columnsFName = new ArrayList<String>();
						ArrayList<String> columnsFValue = new ArrayList<String>();

						columnsFName.add("EID");
						columnsFName.add("CUSTOMERNO");
						columnsFName.add("ORGANIZATIONNO");
						columnsFName.add("SHOPID");
						columnsFName.add("SHOPNAME");
						columnsFName.add("LOAD_DOCTYPE");
						columnsFName.add("STATUS");

						columnsFName.add("DELIVERYTYPE");
						columnsFName.add("SHIPTYPE"); // 超商
						columnsFName.add("CREATE_DATETIME");
						columnsFName.add("ECCUSTOMERNO");

						columnsFValue.add(eId);
						columnsFValue.add(" ");
						columnsFValue.add(shopId);
						columnsFValue.add(shopId);
						columnsFValue.add(shopName);
						columnsFValue.add(ecPlatformNo);
						columnsFValue.add("2"); // 2 已接单
						columnsFValue.add("14"); // 卖家宅配
						columnsFValue.add("2"); // 宅配
						columnsFValue.add(sysDate + sysTime);
						columnsFValue.add(ecCustomerNo);

						ArrayList<String> columnsFName2 = new ArrayList<String>();
						ArrayList<String> columnsFValue2 = new ArrayList<String>();

						// OC_ORDER_PAY  默认给现金支付方式
						ArrayList<String> columnsFName4 = new ArrayList<String>();
						ArrayList<String> columnsFValue4 = new ArrayList<String>();

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
						if (i + 1 < rows) {
							Cell[] memoCol = sheet.getRow(i + 1);
							nextValue = memoCol[0].getContents().trim();
							if (nextValue.trim().equals("订单备注") || nextValue.trim().equals("訂單備註")) {
								memo = memoCol[1].getContents().trim();
								;
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
						memberGet = getDatas.get(0).get("MEMBERGET").toString();
						if (!vTest.trim().equals("") && !vTest.trim().equals("订单备注") && !vTest.trim().equals("訂單備註")) { // 当前单元格不为空，且单元格不是“订单备注”，说明是订单行
							// ，取该行的值
							if (vTest != nextValue) { // 当前行单据号和 下一行单据号不同的时候， 插入主表和子表的数据

								for (Map<String, Object> oneData : getDatas) {
									String item = oneData.get("ITEM").toString();
									String columnName = oneData.get("COLUMNNAME").toString(); // excel列名
									String fieldName = oneData.get("FIELDNAME").toString().toUpperCase(); // 数据库列名，转换为大写
									String tableName = oneData.get("TABLENAME").toString().toUpperCase(); // 表名

									for (int j = 0; j < columns; j++) {
										String index = (j + 1) + "";
										String headRowName = headRow[j].getContents();
										String fieldValue = rowValue[j].getContents();
										if (index.equals(item) && headRowName.equals(columnName)) {
											if (tableName.equals("OC_ORDER_DETAIL")) {
												columnsFName2.add(fieldName);
												columnsFValue2.add(fieldValue);

												if (fieldName.equals("AMT")) {
													pluAmt = fieldValue;
												}

												if (fieldName.equals("QTY")) {
													pluQty = fieldValue;
												}


											} else if (tableName.equals("OC_ORDER")) {
												columnsFName.add(fieldName);
												if (fieldName.equals("ORDERNO")) {
													orderNo = fieldValue;
												}

												// 日期需要转换格式， 传的日期是 2010/03/13
												// ，需要转换为 20190313 形式
												if (fieldName.equals("SDATE")) {
													if (!fieldValue.isEmpty()) {
														SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
														Date date = sdf.parse(fieldValue);
														sdf = new SimpleDateFormat("yyyyMMdd");
														fieldValue = sdf.format(date);
													}
												}
												columnsFValue.add(fieldValue);

												// 获取订购人，订购人电话信息
												if (fieldName.equals("CONTMAN")) {
													contMan = fieldValue;
												}
												if (fieldName.equals("CONTTEL")) {
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

								// 与金额相关的字段
								BigDecimal amtBD = new BigDecimal(0);
								BigDecimal qtyBD = new BigDecimal(0);
								amtBD = new BigDecimal(pluAmt);
								qtyBD = new BigDecimal(pluQty);

								// 累加得到订单总价 和 数量
								totAmt = totAmt.add(amtBD);
								totQty = totQty.add(qtyBD);

								// amt / qty 就是單價， 保留兩位小數
								BigDecimal priceBD = amtBD.divide(qtyBD, 2, RoundingMode.HALF_UP);
								price = priceBD.toString();

								columnsFName2.add("PRICE"); //单价
								columnsFValue2.add(price);

								columnsFName2.add("DISC"); // 折扣
								columnsFValue2.add("0");

								columnsFName2.add("ISMEMO"); // 是否有备注
								columnsFValue2.add("N");

								columnsFName.add("TOT_OLDAMT"); // 订单原价
								columnsFValue.add(totAmt.toString());

								columnsFName.add("TOT_AMT"); // 订单金额
								columnsFValue.add(totAmt.toString());

								columnsFName.add("TOT_QTY"); // 数量
								columnsFValue.add(totQty.toString());

								columnsFName.add("INCOMEAMT"); // 商家实收金额
								columnsFValue.add(totQty.toString());

								columnsFName.add("PAYSTATUS"); // 支付状态 ， 默认 付清 
								columnsFValue.add("3");

								columnsFName.add("PAYAMT"); // 已付金额
								columnsFValue.add(totAmt.toString());

								columnsFName.add("PACKAGEFEE"); // 餐盒费
								columnsFValue.add("0");

								columnsFName.add("SHIPFEE"); // 实际配送费
								columnsFValue.add("0");

								columnsFName.add("TOTSHIPFEE"); // 总配送费
								columnsFValue.add("0");

								columnsFName.add("RSHIPFEE"); // 配送费减免
								columnsFValue.add("0");

								columnsFName.add("SERVICECHARGE"); // 服务费
								columnsFValue.add("0");


								columnsFName.add("TOT_DISC"); // 优惠总金额
								columnsFValue.add("0");

								columnsFName.add("SELLER_DISC"); // 商家优惠金额
								columnsFValue.add("0");

								columnsFName.add("PLATFORM_DISC"); // 平台优惠金额
								columnsFValue.add("0");

								detailItem = detailItem + 1;

								DataValue[] columnsVal = new DataValue[columnsFName.size()];
								int insColCt = columnsFName.size();
								String[] columns1 = new String[insColCt];
								DataValue[] insValue = new DataValue[insColCt];
								// 依照傳入參數組譯要insert的欄位與數值；
								int ENO1 = 0;
								for (int k = 0; k < columnsVal.length; k++) {
									String keyValue = columnsFName.get(k).toString();
									if (keyValue != null) {
										columnsVal[k] = new DataValue(keyValue, Types.VARCHAR);
									} else {
										columnsVal[k] = null;
									}

									if (columnsVal[k] != null) {
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
								this.addProcessData(new DataProcessBean(ib1));

								order_payName = "ALL";//这里先这样写吧，EXCEL没有支付方式，等实际客户调试再看看
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
								//插入付款方式
								String[] columnsOrderPay = {
										"EID",
										"ORGANIZATIONNO",
										"SHOPID",
										"ORDERNO",									
										"CUSTOMERNO",
										"ITEM",
										"PAYCODE",
										"PAYCODEERP",
										"PAYNAME",
										"CARDNO",
										"CTTYPE",
										"PAYSERNUM",
										"SERIALNO",
										"REFNO",
										"TERIMINALNO",
										"DESCORE",
										"PAY",
										"EXTRA",
										"CHANGED",
										"BDATE",
										"ISORDERPAY",
										"LOAD_DOCTYPE",
										"ORDER_PAYCODE",
										"ISONLINEPAY",
										"RCPAY",									
										"STATUS",

								};

								//订单付款
								DataValue[] insValueOrderPay =new DataValue[]
										{
												new DataValue(eId, Types.VARCHAR), 
												new DataValue(shopId, Types.VARCHAR), 
												new DataValue(shopId, Types.VARCHAR), 
												new DataValue(orderNo, Types.VARCHAR), 
												new DataValue(ecCustomerNo, Types.VARCHAR), //归属客户
												new DataValue(1, Types.INTEGER), //ITME
												new DataValue(payCode, Types.VARCHAR), //paycode
												new DataValue(payCodeERP, Types.VARCHAR), //paycodeerp
												new DataValue(payNameERP, Types.VARCHAR), //paycodename
												new DataValue("", Types.VARCHAR), //cardno
												new DataValue("", Types.VARCHAR), //cttype
												new DataValue("", Types.VARCHAR), //paysernum
												new DataValue("", Types.VARCHAR), //serialno
												new DataValue("", Types.VARCHAR), //refno
												new DataValue("", Types.VARCHAR), //teriminalno
												new DataValue(0, Types.DOUBLE), //descore
												new DataValue(totAmt, Types.DOUBLE), //pay
												new DataValue(0, Types.DOUBLE), //extra
												new DataValue(0, Types.DOUBLE), //changed
												new DataValue(sysDate, Types.VARCHAR), //bdate
												new DataValue("N", Types.VARCHAR), //isorderpay
												new DataValue("pchome", Types.VARCHAR), //load_doctype
												new DataValue(order_payCode, Types.VARCHAR), //order_paycode
												new DataValue("N", Types.VARCHAR), //isonlinepay
												new DataValue("0", Types.DOUBLE), //rcpay
												new DataValue("100", Types.VARCHAR), //status
										};

								InsBean ibOrderPay = new InsBean("OC_ORDER_PAY", columnsOrderPay);
								ibOrderPay.addValues(insValueOrderPay);
								//									lstData.add(new DataProcessBean(ibOrderPay));
								this.addProcessData(new DataProcessBean(ibOrderPay));

								//------------2019-07-19 增加订单日志

								//接單日誌
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
												new DataValue("pchome", Types.VARCHAR), //電商平台
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
								//								lstData.add(new DataProcessBean(ibOrderStatusLog));	
								this.addProcessData(new DataProcessBean(ibOrderStatusLog));


								DataValue[] columnsVal2 = new DataValue[columnsFName2.size()];
								int insColCt2 = columnsFName2.size();
								String[] columns2 = new String[insColCt2];
								DataValue[] insValue2 = new DataValue[insColCt2];
								// 依照傳入參數組譯要insert的欄位與數值；
								int ENO2 = 0;
								for (int k = 0; k < columnsVal2.length; k++) {
									String keyValue = columnsFName2.get(k).toString();
									if (keyValue != null) {
										columnsVal2[k] = new DataValue(keyValue, Types.VARCHAR);
									} else {
										columnsVal2[k] = null;
									}

									if (columnsVal2[k] != null) {
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
								this.addProcessData(new DataProcessBean(ib2));

								// 重新初始化金额参数
								price = "0";
								pluAmt = "0";
								pluQty = "0";

								totQty = new BigDecimal(0);
								totAmt = new BigDecimal(0);

								detailItem = 1;

							} else {  // 若当前行单据号 == 下一行单据号， 说明这是一张单， 只需要插入子表数据即可
								orderNo = "";
								//								if (vTest.equals(preValue)) {
								for (Map<String, Object> oneData : getDatas) {
									String item = oneData.get("ITEM").toString();
									String columnName = oneData.get("COLUMNNAME").toString();
									String fieldName = oneData.get("FIELDNAME").toString().toUpperCase(); // 数据库列名，转换为大写
									String tableName = oneData.get("TABLENAME").toString().toUpperCase(); // 表名，转换为大写
									for (int j = 0; j < columns; j++) {
										String index = (j + 1) + "";
										String headRowName = headRow[j].getContents();
										String fieldValue = rowValue[j].getContents();
										if (index.equals(item) && headRowName.equals(columnName)) {

											if ( tableName.equals("OC_ORDER_DETAIL")) {

												columnsFName2.add(fieldName);
												columnsFValue2.add(fieldValue);

												if (fieldName.equals("AMT")) {
													pluAmt = fieldValue;
												}

												if (fieldName.equals("QTY")) {
													pluQty = fieldValue;
												}

											}
											if (tableName.equals("OC_ORDER")) {
												if (fieldName.equals("ORDERNO")) {
													orderNo = fieldValue;
												}
											}


										}
									}

								}
								columnsFName2.add("ITEM");
								columnsFValue2.add(detailItem + "");

								columnsFName2.add("ORDERNO");
								columnsFValue2.add(orderNo);

								// 与金额相关的字段
								BigDecimal amtBD = new BigDecimal(0);
								BigDecimal qtyBD = new BigDecimal(0);
								amtBD = new BigDecimal(pluAmt);
								qtyBD = new BigDecimal(pluQty);

								// 累加得到订单总价 和 数量
								totAmt = totAmt.add(amtBD);
								totQty = totQty.add(qtyBD);

								// amt / qty 就是單價， 保留兩位小數
								BigDecimal priceBD = amtBD.divide(qtyBD, 2, RoundingMode.HALF_UP);
								price = priceBD.toString();

								columnsFName2.add("PRICE"); //单价
								columnsFValue2.add(price);

								columnsFName2.add("DISC"); // 折扣
								columnsFValue2.add("0");

								columnsFName2.add("ISMEMO"); // 是否有备注
								columnsFValue2.add("N");

								DataValue[] columnsVal2 = new DataValue[columnsFName2.size()];
								int insColCt2 = columnsFName2.size();
								String[] columns2 = new String[insColCt2];
								DataValue[] insValue2 = new DataValue[insColCt2];
								// 依照傳入參數組譯要insert的欄位與數值；
								int ENO2 = 0;
								for (int k = 0; k < columnsVal2.length; k++) {
									String keyValue = columnsFName2.get(k).toString();
									if (keyValue != null) {
										columnsVal2[k] = new DataValue(keyValue, Types.VARCHAR);
									} else {
										columnsVal2[k] = null;
									}

									if (columnsVal2[k] != null) {
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
								this.addProcessData(new DataProcessBean(ib2));
								detailItem = detailItem + 1;


							} // 判断 vTest == nextValue else 结束

						}

					}
				}
			}

			else if (ecPlatformNo.equals("yahoosuper")) 
			{
				// 得到 标题行，用于和 导入格式中的 columnName 做比较
				Cell[] headRow = sheet.getRow(0);
				int detailItem = 1; // 子表item
				if (getDatas.size() > 0) {
					// 循环行 ， 从第二行开始， 前面为固定行
					for (int i = 1; i < rows; i++) {
						int num = 0; // 这里声明num，用来记录主表插入的次数， 一个订单多个商品，主表数据只能
						// 插入一次

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
						columnsFName.add("CREATE_DATETIME");
						columnsFName.add("ECCUSTOMERNO");

						columnsFValue.add(eId);
						columnsFValue.add(" ");
						columnsFValue.add(shopId);
						columnsFValue.add(shopId);
						columnsFValue.add(shopName);
						columnsFValue.add(ecPlatformNo);
						columnsFValue.add("2");
						columnsFValue.add(sysDate +sysTime);
						columnsFValue.add(ecCustomerNo);

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

						Cell[] preCol = sheet.getRow(i - 1); // 获取上一行 ， 用于验证 ：一
						// :一笔订单只有一个商品；
						// 二：
						// 一笔订单多个商品的最后一个
						// String preValue = preCol[0].getContents().trim();

						String orderNo = "";
						String contMan = "";
						String contTel = "";
						String memberGet = "0"; // 0不抓取， 1抓取， 默认给0
						memberGet = getDatas.get(0).get("MEMBERGET").toString();
						orderNo = rowValue[1].getContents();
						String preValue = preCol[1].getContents().trim();

						String shippingShop = "";
						String shippingShopName = "";
						String address = "";
						for (Map<String, Object> oneData : getDatas) {
							String item = oneData.get("ITEM").toString();
							String tableName = oneData.get("TABLENAME").toString();
							String columnName = oneData.get("COLUMNNAME").toString(); // excel列名
							String fieldName = oneData.get("FIELDNAME").toString().toUpperCase(); // 数据库列名，转换为大写
							String yahooPickupWay = oneData.get("PICKUPWAY").toString(); // 1：超取
							// 2：宅配

							if (orderNo.equals(preValue)) {
								for (int j = 0; j < columns; j++) {
									String index = (j + 1) + "";
									String headRowName = headRow[j].getContents();
									String fieldValue = "";
									fieldValue = rowValue[j].getContents();
									if (index.equals(item) && headRowName.equals(columnName)
											&& tableName.equals("OC_ORDER_DETAIL")) {
										columnsFName2.add(fieldName);
										columnsFValue2.add(fieldValue);
									}
								}
								num = num + 1;
							} else {
								for (int j = 0; j < columns; j++) {
									String index = (j + 1) + "";
									String headRowName = headRow[j].getContents();
									String fieldValue = "";
									fieldValue = rowValue[j].getContents();

									if (index.equals(item) && headRowName.equals(columnName)
											&& tableName.equals("OC_ORDER")) {
										/**
										 * yahoo 收货地址 (address)必须以 “|”分割，且只能有两个：
										 * 门店编号 | 门店名称 | 地址 yahoo 超取， 地址栏：
										 * 包括取货门店编号， 名称， 地址 这三种
										 */
										if (yahooPickupWay.equals("1") && fieldName.equals("ADDRESS")) {
											// F000648｜全家中和中和店｜新北市中和區中和路４８０號１Ｆ
											// 我勒个去 ， “ | ” 和 “ ｜ ” 不一样。
											String[] splitArr = fieldValue.trim().split("｜");
											if (splitArr.length > 0) {
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
										if (!fieldName.equals("ADDRESS")) {
											columnsFName.add(fieldName);
											columnsFValue.add(fieldValue);
										}
										if (fieldName.equals("ORDERNO")) {
											orderNo = fieldValue;
										}
										// 获取订购人，订购人电话信息
										if (fieldName.equals("CONTMAN")) {
											contMan = fieldValue;
										}
										if (fieldName.equals("CONTTEL")) {
											contTel = fieldValue;
										}

									}
									if (index.equals(item) && headRowName.equals(columnName)
											&& tableName.equals("OC_ORDER_DETAIL")) {
										columnsFName2.add(fieldName);
										columnsFValue2.add(fieldValue);
									}

								}
								// isRepeat = false;
								num = 0;
							}

						}
						columnsFName2.add("ORDERNO");
						columnsFValue2.add(orderNo);
						columnsFName2.add("ITEM");
						columnsFValue2.add(detailItem + "");

						if (num == 0) {
							DataValue[] columnsVal = new DataValue[columnsFName.size()];
							int insColCt = columnsFName.size();
							String[] columns1 = new String[insColCt];
							DataValue[] insValue = new DataValue[insColCt];
							// 依照傳入參數組譯要insert的欄位與數值；
							int ENO1 = 0;
							for (int k = 0; k < columnsVal.length; k++) {
								String keyValue = columnsFName.get(k).toString();
								if (keyValue != null) {
									columnsVal[k] = new DataValue(keyValue, Types.VARCHAR);
								} else {
									columnsVal[k] = null;
								}

								if (columnsVal[k] != null) {
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
							this.addProcessData(new DataProcessBean(ib1));
						}

						DataValue[] columnsVal2 = new DataValue[columnsFName2.size()];
						int insColCt2 = columnsFName2.size();
						String[] columns2 = new String[insColCt2];
						DataValue[] insValue2 = new DataValue[insColCt2];
						// 依照傳入參數組譯要insert的欄位與數值；
						int ENO2 = 0;
						for (int k = 0; k < columnsVal2.length; k++) {
							String keyValue = columnsFName2.get(k).toString();
							if (keyValue != null) {
								columnsVal2[k] = new DataValue(keyValue, Types.VARCHAR);
							} else {
								columnsVal2[k] = null;
							}

							if (columnsVal2[k] != null) {
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
						this.addProcessData(new DataProcessBean(ib2));

						detailItem = detailItem + 1;

					}

				}
			} // yahoosuper 结束

			else if (ecPlatformNo.equals("momo")) 
			{
				Cell[] headRow = sheet.getRow(0);

				if (getDatas.size() > 0) 
				{
					// 循环行 ， 从第二行开始， 前面为固定行
					for (int i = 1; i < rows; i++) {
						// int num = 0; // 这里声明num，用来记录主表插入的次数， 一个订单多个商品，主表数据只能
						// 插入一次

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
						columnsFName.add("CREATE_DATETIME");
						columnsFName.add("ECCUSTOMERNO");

						columnsFValue.add(eId);
						columnsFValue.add(" ");
						columnsFValue.add(shopId);
						columnsFValue.add(shopId);
						columnsFValue.add(shopName);
						columnsFValue.add(ecPlatformNo);
						columnsFValue.add("2");
						columnsFValue.add(sysDate + sysTime);
						columnsFValue.add(ecCustomerNo);

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
						memberGet = getDatas.get(0).get("MEMBERGET").toString();
						orderNo = rowValue[1].getContents();

						for (Map<String, Object> oneData : getDatas) {
							String item = oneData.get("ITEM").toString();
							String tableName = oneData.get("TABLENAME").toString();
							String columnName = oneData.get("COLUMNNAME").toString(); // excel列名
							String fieldName = oneData.get("FIELDNAME").toString().toUpperCase(); // 数据库列名，转换为大写

							for (int j = 0; j < columns; j++) {
								String index = (j + 1) + "";
								String headRowName = headRow[j].getContents();
								String fieldValue = "";
								fieldValue = rowValue[j].getContents();

								if (headRowName.equals(columnName) && tableName.equals("OC_ORDER")) {
									columnsFName.add(fieldName);
									columnsFValue.add(fieldValue);

									if (fieldName.equals("ORDERNO")) {
										orderNo = fieldValue;
									}
									// 获取订购人，订购人电话信息
									if (fieldName.equals("CONTMAN")) {
										contMan = fieldValue;
									}
									if (fieldName.equals("CONTTEL")) {
										contTel = fieldValue;
									}

								}
								if (index.equals(item) && headRowName.equals(columnName)
										&& tableName.equals("OC_ORDER_DETAIL")) {
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
						String[] columns1 = new String[insColCt];
						DataValue[] insValue = new DataValue[insColCt];
						// 依照傳入參數組譯要insert的欄位與數值；
						int ENO1 = 0;
						for (int k = 0; k < columnsVal.length; k++) {
							String keyValue = columnsFName.get(k).toString();
							if (keyValue != null) {
								columnsVal[k] = new DataValue(keyValue, Types.VARCHAR);
							} else {
								columnsVal[k] = null;
							}

							if (columnsVal[k] != null) {
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
						this.addProcessData(new DataProcessBean(ib1));

						DataValue[] columnsVal2 = new DataValue[columnsFName2.size()];
						int insColCt2 = columnsFName2.size();
						String[] columns2 = new String[insColCt2];
						DataValue[] insValue2 = new DataValue[insColCt2];
						// 依照傳入參數組譯要insert的欄位與數值；
						int ENO2 = 0;
						for (int k = 0; k < columnsVal2.length; k++) {
							String keyValue = columnsFName2.get(k).toString();
							if (keyValue != null) {
								columnsVal2[k] = new DataValue(keyValue, Types.VARCHAR);
							} else {
								columnsVal2[k] = null;
							}

							if (columnsVal2[k] != null) {
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
						this.addProcessData(new DataProcessBean(ib2));

						// detailItem = detailItem +1;

					}
				}
			}

			//general平台，金財通
			else if (ecPlatformNo.equals("general") && (orderFormatName.contains("金財通")||fileSuffix.equals("jincaitong"))) 
			{
				//金財通的EXCEL没有付款方式
				try 
				{										
					//读取excel订单记录
					List<Map<String, Object>> lstOrders=new ArrayList<>();

					String startLine = getDatas.get(0).get("STARTLINE").toString();
					int startIndex = Integer.parseInt(startLine);
					Cell[] headRow = sheet.getRow(startIndex - 1);
					if (getDatas.size() > 0) 
					{					
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
						for (int i = startIndex; i < rows; i++)
						{								
							Cell[] cellValue = sheet.getRow(i);

							//订单信息
							Map<String, Object> mapOrder=new HashMap<String, Object>();	

							for (Map<String, Object> oneData : getDatas) 
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
						Map<String, Boolean> conditionD1 = new HashMap<String, Boolean>(); //查詢條件				
						conditionD1.put("OC_ORDER.ORDERNO", true);  
						//调用过滤函数			
						List<Map<String, Object>> getHeader=MapDistinct.getMap(lstOrders, conditionD1);

						for (Map<String, Object> oneData : getHeader) 
						{
							try 
							{
								String orderno = oneData.get("OC_ORDER.ORDERNO").toString();

								//验证该订单是否已经存在
								String isRepeatSql = this.isRepeatOrder(req, orderno);
								List<Map<String, Object>> getRepeatDatas = this.doQueryData(isRepeatSql, null);
								if (getRepeatDatas == null || getRepeatDatas.size() < 1) 
								{
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
									
									int detailItem=0;
									for (Map<String, Object> oneData2 : getDetailDatas) 
									{				
										try 
										{
											detailItem+=1;
											
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
											columnsFValue2.add(detailItem+"");	
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
													+ " left join DCP_BARCODE b on a.EID = b.EID and a.pluNo = b.pluNo and b.status='100'"
													+ " where a.EID = '"+eId+"'  "
													+ " and a.pluNo = '"+pluNo+"' "
													+ " and a.status='100'";

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
											
											InsBean ib2 = new InsBean("OC_ORDER_DETAIL", columns2);
											ib2.addValues(insValue2);
											this.addProcessData(new DataProcessBean(ib2));
											
										} 
										catch (Exception e) 
										{
											logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"金财通Excel插入订单明细异常：" + e.getMessage());
											
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
									columnsFValue.add(pickUpWay);
									columnsFValue.add(sysDate +sysTime);
									columnsFValue.add(ecCustomerNo);
									columnsFValue.add("3");
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
									this.addProcessData(new DataProcessBean(ib1));		


									
									
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
									this.addProcessData(new DataProcessBean(ib4));	
									
									
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
									this.addProcessData(new DataProcessBean(ibOrderStatusLog));

									
								}	
							} 
							catch (Exception e) 
							{
								logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"金财通Excel插入订单单头异常：" + e.getMessage());
								throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "金财通Excel插入订单单头异常："+e.getMessage());
							}
						}						
					}		
					
					//
					if (lstOrders!=null) 
					{
						lstOrders.clear();
						lstOrders=null;
					}					
					this.doExecuteDataToDB();
				} 
				catch (Exception e) 
				{					
					res.setSuccess(false);
					res.setServiceDescription("服务执行失败："+e.getMessage());
					res.setServiceStatus("200");
				}

			}
			//general平台，非shopLine
			else if (ecPlatformNo.equals("general") && !orderFormatName.equals("shopLine")) 
			{
				try 
				{
					String startLine = getDatas.get(0).get("STARTLINE").toString();
					int startIndex = Integer.parseInt(startLine);
					Cell[] headRow = sheet.getRow(startIndex - 1);
					if (getDatas.size() > 0) 
					{

						// 2019-08-21 設置 preOrderNo 字段，記錄每一行的單號，如果當前行的單號 和 上一次循環的單號一樣， 就説明是同一個訂單， 不再插入當前行主表數據
						String preOrderNo = "";
						int detailItem = 1;

						// 循环行 ， 从第二行开始， 前面为固定行
						for (int i = startIndex; i < rows; i++) {
							// int num = 0; // 这里声明num，用来记录主表插入的次数， 一个订单多个商品，主表数据只能
							// 插入一次

							// OC_ORDER
							ArrayList<String> columnsFName = new ArrayList<String>();
							ArrayList<String> columnsFValue = new ArrayList<String>();

							// OC_ORDER_DETAIL
							ArrayList<String> columnsFName2 = new ArrayList<String>();
							ArrayList<String> columnsFValue2 = new ArrayList<String>();

							// OC_ORDER_AGIO 订单折扣表
							ArrayList<String> agioColumnsFName = new ArrayList<String>();
							ArrayList<String> agioColumnsFValue = new ArrayList<String>();

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

							columnsFValue.add(eId);
							columnsFValue.add(" ");
							columnsFValue.add(shopId);
							columnsFValue.add(shopId);
							columnsFValue.add(shopName);
							columnsFValue.add(ecPlatformNo);
							columnsFValue.add("2");
							columnsFValue.add(pickUpWay);
							columnsFValue.add(sysDate +sysTime);
							columnsFValue.add(ecCustomerNo);

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
							String shipType = ""; // 配送方式 （默认给空，前端空值显示的是“其他”）

							// 2019-12-11 汉记青哥反应导入有错， rowValue[1] , 改为 rowValue[0]
							//orderNo = rowValue[1].getContents();
							try 
							{
								for (Map<String, Object> oneData : getDatas)
								{

									String tableName = oneData.get("TABLENAME").toString().toUpperCase();
									String columnName = oneData.get("COLUMNNAME").toString(); // excel列名
									String fieldName = oneData.get("FIELDNAME").toString().toUpperCase(); // 数据库列名，转换为大写

									if(oneData.get("FIELDNAME").toString().toUpperCase().equals("ORDERNO") && tableName.equals("OC_ORDER") )
									{
										//									orderNo  = rowValue[j].getContents();
										for (int j = 0; j < rowValue.length; j++) {
											String index = (j + 1) + "";
											String headRowName = headRow[j].getContents();

											System.out.println("j:"+j + " 列名："+headRowName);

											String fieldValue = "";
											fieldValue = rowValue[j].getContents();
											System.out.println("j:"+j + " 列名："+fieldValue);

											if (headRowName.equals(columnName) && tableName.equals("OC_ORDER")) {

												if(fieldName.equals("ORDERNO")){
													System.out.println("到订单了" + j + "  列值："+fieldValue);
													orderNo = fieldValue;
												}

											}
										}

									}
								}
							} 
							catch (Exception e1)
							{
								throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "获取单头信息失败!"+e1.getMessage());
							}
							//							orderNo = rowValue[0].getContents();
							//验证该订单是否已经存在
							String isRepeatSql = this.isRepeatOrder(req, orderNo);
							List<Map<String, Object>> getRepeatDatas = this.doQueryData(isRepeatSql, null);
							if (getRepeatDatas == null || getRepeatDatas.size() < 1) 
							{

								try 
								{
									String contMan = "";
									String contTel = "";
									String memberGet = "0"; // 0不抓取， 1抓取， 默认给0
									memberGet = getDatas.get(0).get("MEMBERGET").toString();

									for (Map<String, Object> oneData : getDatas) 
									{
										String item = oneData.get("ITEM").toString();
										String tableName = oneData.get("TABLENAME").toString().toUpperCase();
										String columnName = oneData.get("COLUMNNAME").toString(); // excel列名
										String fieldName = oneData.get("FIELDNAME").toString().toUpperCase(); // 数据库列名，转换为大写

										try 
										{
											for (int j = 0; j < rowValue.length; j++) 
											{
												String index = (j + 1) + "";
												String headRowName = headRow[j].getContents();
												String fieldValue = "";
												fieldValue = rowValue[j].getContents();

												if (headRowName.equals(columnName) && tableName.equals("OC_ORDER")) 
												{
													if(fieldName.equals("SHIPDATE"))
													{
														if(fieldValue != null && fieldValue.length() > 10 && fieldValue.length() < 18)
														{
															SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
															ParsePosition pos = new ParsePosition(0);
															Date strtodate = formatter.parse(fieldValue, pos);

															SimpleDateFormat formatter2 = new SimpleDateFormat("yyyyMMdd");
															String dateString = formatter2.format(strtodate);
															fieldValue = dateString;
														}

														if(fieldValue != null && fieldValue.length() == 18){
															SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
															ParsePosition pos = new ParsePosition(0);
															Date strtodate = formatter.parse(fieldValue, pos);

															SimpleDateFormat formatter2 = new SimpleDateFormat("yyyyMMdd");
															String dateString = formatter2.format(strtodate);
															fieldValue = dateString;
														}

														if(fieldValue != null && fieldValue.length() == 10){
															SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
															ParsePosition pos = new ParsePosition(0); 
															Date strtodate = formatter.parse(fieldValue, pos);

															SimpleDateFormat formatter2 = new SimpleDateFormat("yyyyMMdd");
															String dateString = formatter2.format(strtodate);

															fieldValue = dateString;
														}

													}


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

													columnsFName.add(fieldName);
													columnsFValue.add(fieldValue);
												}
												if (
														//index.equals(item) && 
														headRowName.equals(columnName)
														&& tableName.equals("OC_ORDER_DETAIL")) 
												{
													columnsFName2.add(fieldName);
													columnsFValue2.add(fieldValue);
												}

											}
										}
										catch (Exception e) 
										{
											// TODO Auto-generated catch block
											throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "获取订单明细异常："+e.getMessage());
										}

									}

									//*****************
									if(!preOrderNo.equals(orderNo))
									{
										detailItem = 1;
									}
									else
									{
										detailItem = detailItem + 1 ;
									}

									columnsFName2.add("ORDERNO");
									columnsFValue2.add(orderNo);
									columnsFName2.add("ITEM");
									columnsFValue2.add(detailItem + "");

									// 当前行 单号 不等于上一行单号，  或者 当前行 == 上一行单号 且 是最后一行的时候， 可以插入 OC_order 数据
									if(!preOrderNo.equals(orderNo) )
									{
										//								if( (!preOrderNo.equals(orderNo)   ) || ( preOrderNo.equals(orderNo) && i + 1 == rows )){
										DataValue[] columnsVal = new DataValue[columnsFName.size()];
										int insColCt = columnsFName.size();
										String[] columns1 = new String[insColCt];
										DataValue[] insValue = new DataValue[insColCt];
										// 依照傳入參數組譯要insert的欄位與數值；
										int ENO1 = 0;
										for (int k = 0; k < columnsVal.length; k++) 
										{
											String keyValue = columnsFName.get(k).toString();
											if (keyValue != null) {
												columnsVal[k] = new DataValue(keyValue, Types.VARCHAR);
											} else {
												columnsVal[k] = null;
											}

											if (columnsVal[k] != null) 
											{
												columns1[ENO1] = columnsFName.get(k).toString();

												if(k==13){
													System.out.println("到13了");
												}

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
										this.addProcessData(new DataProcessBean(ib1));

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
										this.addProcessData(new DataProcessBean(ibOrderStatusLog));


									}


									DataValue[] columnsVal2 = new DataValue[columnsFName2.size()];
									int insColCt2 = columnsFName2.size();
									String[] columns2 = new String[insColCt2];
									DataValue[] insValue2 = new DataValue[insColCt2];
									// 依照傳入參數組譯要insert的欄位與數值；
									int ENO2 = 0;
									try 
									{
										for (int k = 0; k < columnsVal2.length; k++) 
										{
											String keyValue = columnsFName2.get(k).toString();
											if (keyValue != null) 
											{
												columnsVal2[k] = new DataValue(keyValue, Types.VARCHAR);
											} else {
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
									} 
									catch (Exception e) 
									{
										throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "插入订单明细异常："+e.getMessage());
									}

									InsBean ib2 = new InsBean("OC_ORDER_DETAIL", columns2);
									ib2.addValues(insValue2);
									this.addProcessData(new DataProcessBean(ib2));

									preOrderNo = orderNo;
								} 
								catch (Exception e) 
								{
									throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "插入订单异常："+e.getMessage());
								}
							}

							else
							{
								throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "订单已存在："+orderNo);
							}

						}
					}
				} 
				catch (Exception e) 
				{					
					res.setSuccess(false);
					res.setServiceDescription("服务执行失败："+e.getMessage());
					res.setServiceStatus("200");
				}

			}

			// shopLine 格式專用 (general代表的是通用格式，實際上不是的，)
			else if(ecPlatformNo.equals("general") && orderFormatName.equals("shopLine"))
			{

				String startLine = getDatas.get(0).get("STARTLINE").toString();
				int startIndex = Integer.parseInt(startLine);
				// 循环行 ， 从第七行开始， 前面几行为固定行
				// 得到 标题行，用于和 导入格式中的 columnName 做比较
				Cell[] headRow = sheet.getRow(startIndex - 1);
				if (getDatas.size() > 0) 
				{
					// 2019-08-21 設置 preOrderNo 字段，記錄每一行的單號，如果當前行的單號 和 上一次循環的單號一樣， 就説明是同一個訂單， 不再插入當前行主表數據
					String preOrderNo = "";
					int detailItem = 1;
					// 循环行 ， 从第二行开始， 前面为固定行
					for (int i = startIndex; i < rows; i++) 
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
						columnsFName.add("CREATE_DATETIME");
						columnsFName.add("ECCUSTOMERNO");

						columnsFValue.add(eId);
						columnsFValue.add(" ");
						columnsFValue.add(shopId);
						columnsFValue.add(shopId);
						columnsFValue.add(shopName);
						columnsFValue.add(ecPlatformNo);
						columnsFValue.add("2");
						columnsFValue.add(sysDate +sysTime);
						columnsFValue.add(ecCustomerNo);

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
						memberGet = getDatas.get(0).get("MEMBERGET").toString();

						for (Map<String, Object> oneData : getDatas) 
						{
							//String item = oneData.get("ITEM").toString();
							String tableName = oneData.get("TABLENAME").toString();
							String columnName = oneData.get("COLUMNNAME").toString(); // excel列名
							String fieldName = oneData.get("FIELDNAME").toString().toUpperCase(); // 数据库列名，转换为大写

							nextCol:for (int j = 0; j < columns; j++) 
							{
								if(j >= curLen)
								{
									break nextCol;
								}

								//String index = (j + 1) + "";
								String headRowName = headRow[j].getContents();
								String fieldValue = "";
								fieldValue = rowValue[j].getContents();

								if (headRowName.equals(columnName) && tableName.equals("OC_ORDER")) 
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

										if(fieldValue.contains("新竹")){
											deliveryType = "15";
											shipType = "2";
										}
										if(fieldValue.contains("黑貓")){
											deliveryType = "9";
											shipType = "2";
										}
										if(fieldValue.contains("7-11")){
											deliveryType = "7";
											shipType = "6";
										}
										if(fieldValue.contains("全家")){
											deliveryType = "8";
											shipType = "6";
										}
										if(fieldValue.contains("萊爾富")){
											deliveryType = "10";
											shipType = "6";
										}
										if(fieldValue.contains("OK")){
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
								if ( headRowName.equals(columnName) && tableName.equals("OC_ORDER_DETAIL")) 
								{

									if(fieldName.equals("PLUNO"))
									{
										pluNo = fieldValue;
									}
									if(fieldName.equals("PLUNAME"))
									{
										pluName = fieldValue;
									}

									if(fieldName.equals("DISC") && !fieldValue.equals("")){
										disc = fieldValue;
									}
									if(fieldName.equals("QTY") && !fieldValue.equals("")){
										qty = fieldValue;
									}
									if(fieldName.equals("PRICE")){
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

								if (headRowName.equals(columnName) && tableName.equals("OC_ORDER_PAY"))
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
									// }


									columnsFName4.add("ISORDERPAY"); // 是否定金
									columnsFValue4.add("N");

									columnsFName4.add("ISONLINEPAY"); // 是否平台支付
									columnsFValue4.add("Y");

									columnsFName4.add("RCPAY"); // 已拆单金额
									columnsFValue4.add("0");
								}

							}

						}

						String sExeptionSql="select a.pluNo ,  a.sunit, b.plubarcode  from DCP_GOODS a "
								+ " left join DCP_BARCODE b on a.EID = b.EID and a.pluNo = b.pluNo and b.status='100'"
								+ " where a.EID = '"+eId+"'  "
								+ " and a.pluNo = '"+pluNo+"' "
								+ " and a.status='100'";

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

						if(!preOrderNo.equals(orderNo)){

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
						else{
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
						if(!preOrderNo.equals(orderNo)){
							DataValue[] columnsVal = new DataValue[columnsFName.size()];
							int insColCt = columnsFName.size();
							String[] columns1 = new String[insColCt];
							DataValue[] insValue = new DataValue[insColCt];
							// 依照傳入參數組譯要insert的欄位與數值；
							int ENO1 = 0;
							for (int k = 0; k < columnsVal.length; k++) {
								String keyValue = columnsFName.get(k).toString();
								if (keyValue != null) {
									columnsVal[k] = new DataValue(keyValue, Types.VARCHAR);
								} else {
									columnsVal[k] = null;
								}

								if (columnsVal[k] != null) {
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
							this.addProcessData(new DataProcessBean(ib1));

							// 插入付款档信息
							if (columnsFName4.size() > 0) {

								DataValue[] columnsVal4 = new DataValue[columnsFName4.size()];
								int insColCt4 = columnsFName4.size();
								String[] columns4 = new String[insColCt4];
								DataValue[] insValue4 = new DataValue[insColCt4];
								// 依照傳入參數組譯要insert的欄位與數值；
								int ENO4 = 0;
								for (int k = 0; k < columnsVal4.length; k++) {
									String keyValue = columnsFName4.get(k).toString();
									if (keyValue != null) {
										columnsVal4[k] = new DataValue(keyValue, Types.VARCHAR);
									} else {
										columnsVal4[k] = null;
									}

									if (columnsVal4[k] != null) {
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
								this.addProcessData(new DataProcessBean(ib4));
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
							this.addProcessData(new DataProcessBean(ibOrderStatusLog));

						}

						DataValue[] columnsVal2 = new DataValue[columnsFName2.size()];
						int insColCt2 = columnsFName2.size();
						String[] columns2 = new String[insColCt2];
						DataValue[] insValue2 = new DataValue[insColCt2];
						// 依照傳入參數組譯要insert的欄位與數值；
						int ENO2 = 0;
						for (int k = 0; k < columnsVal2.length; k++) {
							String keyValue = columnsFName2.get(k).toString();
							if (keyValue != null) {
								columnsVal2[k] = new DataValue(keyValue, Types.VARCHAR);
							} else {
								columnsVal2[k] = null;
							}

							if (columnsVal2[k] != null) {
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
						this.addProcessData(new DataProcessBean(ib2));				

						preOrderNo = orderNo;

					}
				}
			}
			else 
			{

			}




		} catch (Exception e) {
			// TODO Auto-generated catch block
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败：" + e.getMessage());
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
		int errCt = 0; // 考慮當錯誤很多時則直接顯示格式錯誤；
		String orderFormatNo = req.getOrderFormatNo();
		if (Check.Null(orderFormatNo)) {
			errCt++;
			errMsg.append("格式编号不可为空值, ");
			isFail = true;
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderECImportCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderECImportCreateReq>() {
		};
	}

	@Override
	protected DCP_OrderECImportCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderECImportCreateRes();
	}

	/**
	 * 验证字段类型
	 * 
	 * @param columnName
	 * @param tableName
	 * @return
	 */
	private String getColumnType(String tableName) {
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer();

		sqlbuf.append("select COLUMN_NAME,DATA_TYPE,DATA_PRECISION,DATA_SCALE,NULLABLE,data_default "
				+ " from user_tab_columns  " + " where table_name ='" + tableName + "'  AND NULLABLE = 'N' ");

		sql = sqlbuf.toString();
		return sql;
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
				+ " b.item , b.tableName , b.fieldName , b.fromvalue AS columnName , a.pickupway ,a.memberget , a.STARTLINE , a.filePath ,a.ecCustomerNo "
				+ " FROM OC_ECORDERFORMAT a  "
				+ " LEFT JOIN OC_ECORDERFORMAT_DETAIL b   ON a.EID = b.EID AND a.ORDERFORMATNO = b.ORDERFORMATNO "
				+ " WHERE a.EID = '" + eId + "'  AND a.orderformatNo = '" + orderFormatNo + "'"
				+ " order by b.item ");

		sql = sqlbuf.toString();
		return sql;
	}

	private static String[] insert(String[] arr, String str) {
		int size = arr.length;
		String[] tmp = new String[size + 1];
		System.arraycopy(arr, 0, tmp, 0, size);
		tmp[size] = str;
		return tmp;
	}

	/**
	 * 查询 会员接口参数
	 * 
	 * @param req
	 * @return
	 * @throws Exception
	 */
	protected String getMobileParamSQL(DCP_OrderECImportCreateReq req) throws Exception {
		String sql = "select t.item,t.ITEMVALUE from platform_basesettemp t where 1=1 and  "
				+ " (ITEM LIKE '%YC%' OR ITEM in ('EmailAddress', 'EmailHost' , 'EmailPassword') )"
				+ " and t.EID='" + req.geteId() + "'  and t.status='100' ";

		return sql;
	}

	/**
	 * 
	 * @param req
	 * @return
	 * @throws Exception
	 */
	protected String getMappingPaymentSQL(DCP_OrderECImportCreateReq req) throws Exception {
		String sql = "   SELECT EID , load_docType ,  payCode,  payName ,  payCodeERP , ORDER_payCode , "
				+ " order_payname , order_PayType , customerno " + " FROM OC_mappingpayment WHERE EID = '"
				+ req.geteId() + "'  AND status='100' " + " AND load_docType = '" + req.getEcplatformNo()
				+ "' ";
		return sql;
	}


	/**
	 * 用于验证改订单是否已经存在， 如果已经存在，允许接着导入后面的单子
	 * @param req
	 * @param orderNo
	 * @return
	 */
	protected String isRepeatOrder(DCP_OrderECImportCreateReq req, String orderNo ){

		String sql = "";
		StringBuffer sqlbuf = new StringBuffer();
		sqlbuf.append("select OrderNo from OC_ORDER  where EID = '"+req.geteId()+"'  "
				+ " and SHOPID = '"+req.getShopId()+"' and orderNo = '"+orderNo+"'"
				+ "");
		sql = sqlbuf.toString();
		return sql;
	}







}
