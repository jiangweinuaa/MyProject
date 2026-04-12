package com.dsc.spos.service.imp.json;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.dsc.spos.json.cust.req.DCP_OrderECExpShippingQueryReq;
import com.dsc.spos.json.cust.res.DCP_OrderECExpShippingQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

/**
 * 电商订单导出Excel 查询
 * @author yuanyy 2019-03-19
 *
 */
public class DCP_OrderECExpShippingQuery extends SPosBasicService<DCP_OrderECExpShippingQueryReq, DCP_OrderECExpShippingQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_OrderECExpShippingQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_OrderECExpShippingQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderECExpShippingQueryReq>(){};
	}

	@Override
	protected DCP_OrderECExpShippingQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderECExpShippingQueryRes();
	}

	@Override
	protected DCP_OrderECExpShippingQueryRes processJson(DCP_OrderECExpShippingQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_OrderECExpShippingQueryRes res = null;
		res = this.getResponse();
		String eId = req.geteId();
		String ecPlatformNo = req.getEcPlatformNo();
		Workbook wb = new XSSFWorkbook();
		//1.1、设置表格的格式----居中
		CellStyle cs = wb.createCellStyle();
		cs.setAlignment(HorizontalAlignment.CENTER);
		//2.1、创建工作表
		Calendar cal = Calendar.getInstance();// 获得当前时间
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String createDate = df.format(cal.getTime());
		df = new SimpleDateFormat("HHmmss");
		String createTime = df.format(cal.getTime());

		String sheetName = ecPlatformNo+createDate+createTime;
		Sheet sheet = wb.createSheet(sheetName);
		//2.2、合并单元格
		//		sheet.addMergedRegion(new CellRangeAddress(4, 8, 5, 9));
		//3.1、创建行----表头行
		Row row = sheet.createRow(0);

		int totalRecords = 0; //总笔数
		int totalPages = 0;	
		try {
			String sql = null;
			sql = this.getQuerySql(req); // 查询导出格式 
			String[] conditionValues = {};
			List<Map<String , Object>> getDatas = this.doQueryData(sql, conditionValues);
			if(!getDatas.isEmpty()){

				String columnName = null; 
				//过滤出 列名,作为excel 的单头第一行
				Map<String, Boolean> condition1 = new HashMap<String, Boolean>(); //查詢條件
				condition1.put("COLUMNNAME", true);	
				List<Map<String, Object>> getColumnDatas = MapDistinct.getMap(getDatas, condition1);

				//				String tableName = null; 
				//过滤出 列名,作为excel 的单头第一行
				//				Map<String, Boolean> condition2 = new HashMap<String, Boolean>(); //查詢條件
				//				condition2.put("TABLENAME", true);	
				//				List<Map<String, Object>> getTableDatas = MapDistinct.getMap(getDatas, condition2);
				//				
				//				ArrayList<String> fieldNameList = new ArrayList<String>();
				//				String fieldNameStr = ""; // fieldNameStr 用于记录每个表的列名, 插入到查询语句中

				int columnCount = getColumnDatas.size();

				// 创建第一行单头
				for (int i = 0; i < getColumnDatas.size(); i++) {
					columnName = getColumnDatas.get(i).get("COLUMNNAME").toString();
					//4、创建格
					Cell cell = row.createCell(i);
					cell.setCellValue(columnName);
					cell.setCellStyle(cs);
				}


				sql = this.getExcelDatas(eId, ecPlatformNo); // 查询 电商平台订单， 填充excel表单数据
				List<Map<String , Object>> getDetailDatas = this.doQueryData(sql, null);

				if(!getDetailDatas.isEmpty()){

					Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查询条件
					condition.put("ORDERNO", true);// 按照订单号过滤
					List<Map<String, Object>> getQHeader=MapDistinct.getMap(getDetailDatas, condition);

					Map<String, Boolean> condition2 = new HashMap<String, Boolean>(); //查询条件
					condition2.put("SHIPMENTNO", true);// 按照货运单号过滤，传递出去
					List<Map<String, Object>> shipmentDatas=MapDistinct.getMap(getDetailDatas, condition2);

					String eShipmentNo = "";
					String shipmentNo[] = {};
					for (Map<String, Object> map : shipmentDatas) {
						eShipmentNo = map.get("SHIPMENTNO").toString();
						shipmentNo = insert(shipmentNo, eShipmentNo);
					}	

					int rowCount = getQHeader.size(); // 总行数

					for (int i = 0; i < getQHeader.size(); i++) {
						String orderNo = getQHeader.get(i).get("ORDERNO").toString();
						String status = getQHeader.get(i).get("STATUS").toString();
						String refundStatus = getQHeader.get(i).get("REFUNDSTATUS").toString();
						String cardNo = getQHeader.get(i).get("CARDNO").toString();
						String createDateTime = getQHeader.get(i).get("CREATE_DATETIME").toString();
						String sDate = getQHeader.get(i).get("SDATE").toString();
						String sTime = getQHeader.get(i).get("STIME").toString();
						String totAmt = getQHeader.get(i).get("TOT_AMT").toString();
						String shipee = getQHeader.get(i).get("SHIPFEE").toString();
						String totOldAmt = getQHeader.get(i).get("TOT_OLDAMT").toString();
						String address = getQHeader.get(i).get("ADDRESS").toString();
						String orderShop = getQHeader.get(i).get("SHOPID").toString();
						String city = getQHeader.get(i).get("CITY").toString();
						String county = getQHeader.get(i).get("COUNTY").toString();
						String getMan = getQHeader.get(i).get("GETMAN").toString();
						String getManTel = getQHeader.get(i).get("GETMANTEL").toString();
						String shipType = getQHeader.get(i).get("SHIPTYPE").toString();
						String pickupWay = getQHeader.get(i).get("PICKUPWAY").toString();
						String memo = getQHeader.get(i).get("MEMO").toString();

						String shopeePluColumn = ""; // 虾皮专用
						for (Map<String, Object> oneData2 : getDetailDatas) 
						{
							//过滤属于此单头的明细
							if(orderNo.equals(oneData2.get("ORDERNO")))
							{
								String item = oneData2.get("ITEM").toString();
								//								String pluNo = oneData2.get("PLUNO").toString();
								String pluName = oneData2.get("PLUNAME").toString();
								String specName = oneData2.get("SPECNAME").toString();
								String price = oneData2.get("PRICE").toString();
								String qty = oneData2.get("QTY").toString();

								shopeePluColumn += "["+item+"] 商品名称:"+pluName+"; 商品选项名称:"+specName +"; 价格:"+price+ "; 数量:"+qty +";\n";
							}

						}

						row = sheet.createRow(i+1); // 从第二行开始, 第一行是固定标题行

						for (Map<String, Object> expData : getDatas) 
						{
							String columnNo = expData.get("COLUMNNO").toString();
							//							int columnNoInt = Integer.parseInt(columnNo);
							// 确定列号 
							for (int j = 0; j < columnCount; j++) {
								switch (j){
								case 0:
									row.createCell(j).setCellValue(orderNo);
									break;
								case 1:
									row.createCell(j).setCellValue(status);
									break;
								case 2:
									row.createCell(j).setCellValue(refundStatus);
									break;
								case 3:
									row.createCell(j).setCellValue(cardNo);
									break;
								case 4:
									row.createCell(j).setCellValue(createDateTime);
									break;
								case 5:
									row.createCell(j).setCellValue(sDate+" "+sTime);
									break;
								case 6:
									row.createCell(j).setCellValue(totAmt);
									break;
								case 7:
									row.createCell(j).setCellValue(shipee);
									break;
								case 8:
									row.createCell(j).setCellValue(totOldAmt);
									break;
								case 9:
									row.createCell(j).setCellValue(shopeePluColumn);
									break;
								case 10:
									row.createCell(j).setCellValue(address + "["+orderShop+"]");
									break;
								case 11:
									row.createCell(j).setCellValue(city);
									break;
								case 12:
									row.createCell(j).setCellValue(county);
									break;
								case 13:
									row.createCell(j).setCellValue(getMan);
									break;
								case 14:
									row.createCell(j).setCellValue(getManTel);
									break;
								case 15:
									row.createCell(j).setCellValue(shipType);
									break;
								case 16:
									row.createCell(j).setCellValue(pickupWay);
									break;
								case 17:
									row.createCell(j).setCellValue(memo);
									break;

								default:
									break;
								}
							}
						}
					}

					try {

						String fileName = ecPlatformNo+createDate+createTime+".xls";
						// 传进来的文件名 + 绝对路径 ， 组合成文件完成路径
						String filePath = System.getProperty("catalina.home")+"\\webapps\\EC\\" + ecPlatformNo + "\\export\\";
						// 暂且设置为 固定 EC + 平台名 + 文件名， 如果后期规划需要变更 平台编码，修改服务器文件夹名即可
						File file = new File(filePath);
						//如果文件夹不存在则创建 
						if(!file.exists()&& !file.isDirectory())      
						{				
							boolean bl=file.mkdirs();
							//System.out.println(bl);				
						}			
						file=null;

						filePath = filePath + fileName;

						FileOutputStream fout = new FileOutputStream(filePath);

						//						FileOutputStream fout = new FileOutputStream("C:\\Users\\Huawei\\Desktop\\测试.xls");
						wb.write(fout);
						fout.close();
						res.setExcelFileName(fileName);
						res.setShipmentNo(shipmentNo);

					} catch (IOException e) {

					}


				}
				else{
					res.setShipmentNo(null);
					res.setExcelFileName("");
				}

			}
			else{ //当查询导出格式列为空的时候
				res.setSuccess(false);
				res.setServiceStatus("100");
				res.setServiceDescription("当前选择的导出格式存在异常！");
			}

		}
		catch (Exception e) {


		}
		res.setPageNumber(req.getPageNumber());
		res.setPageSize(req.getPageSize());
		res.setTotalRecords(totalRecords);
		res.setTotalPages(totalPages);

		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_OrderECExpShippingQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		String eId = req.geteId();
		String expOrderFormatNo = req.getExpOrderFormatNo(); // 导出格式编码
		String ecPlatformNo = req.getEcPlatformNo(); //电商平台编码

		sqlbuf.append(" SELECT a.expformatno , a. ecplatformno , b.item , b.columnno, b.columnName , b.tablename , b.fieldname "
				+ " FROM OC_ecexporderFormat a  "
				+ " LEFT JOIN OC_ecexporderFormat_detail b ON a.EID = b.EID AND a.EXPFORMATNO = b.EXPFORMATNO "
				+ " WHERE a.EID = '"+eId+"' AND a.EXPFORMATNO = '"+expOrderFormatNo+"' "
				);

		//		if (ecPlatformNo != null && ecPlatformNo.length()!=0) { 	
		//			sqlbuf.append( " AND  a.ecPlatformNo = '"+ecPlatformNo+"' ");
		//		}

		sqlbuf.append(" order by a.expformatno , b.item , b.columnNo , b.tableName , b.fieldName "	);

		sql = sqlbuf.toString();
		return sql;
	}

	/**
	 * 查找每一列的数据
	 * @param eId
	 * @param ecPlatformNo
	 * @param fieldNameStr
	 * @return
	 */
	private String getExcelDatas(String eId, String ecPlatformNo ){

		//// 查出主表的所有列,以及子表不同于主表的列.  不同的导出格式,获取不同的列即可
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		// 下面的sql 要查询的列是动态变化的，根据导出格式的中的列变化
		sqlbuf.append(" SELECT a.shipmentNo , a.EXPORTDOC , a.ECPLATFORMNO ,  a.ORIGINALTYPE , a.ORIGINALNO,  "
				+ " OC_ORDER.* , OC_ORDER_DETAIL.ITEM , "
				+ " OC_ORDER_DETAIL.ORDER_SN, OC_ORDER_DETAIL.PLUNO , OC_ORDER_DETAIL.PLUNAME,"
				+ " OC_ORDER_DETAIL.SPECNAME , OC_ORDER_DETAIL.PLUBARCODE,OC_ORDER_DETAIL.ATTRNAME,"
				+ " OC_ORDER_DETAIL.ECPLUNO, OC_ORDER_DETAIL.UNIT, OC_ORDER_DETAIL.PRICE, "
				+ " OC_ORDER_DETAIL.QTY, OC_ORDER_DETAIL.GOODSGROUP , OC_ORDER_DETAIL.DISC,"
				+ " OC_ORDER_DETAIL.BOXNUM , OC_ORDER_DETAIL.BOXPRICE, OC_ORDER_DETAIL.AMT,"
				+ " OC_ORDER_DETAIL.ISMEMO  "
				+ "FROM DCP_shipment  a "
				+ " LEFT JOIN OC_ORDER OC_ORDER  ON a.EID = OC_ORDER.EID AND a.SHOPID = OC_ORDER.SHOPID  AND a.ec_OrderNo = OC_ORDER.orderNo "
				+ " LEFT JOIN OC_ORDER_DETAIL OC_ORDER_DETAIL on OC_ORDER.EID = OC_ORDER_DETAIL.EID and OC_ORDER.SHOPID = OC_ORDER_DETAIL.SHOPID and OC_ORDER.orderNo = OC_ORDER_DETAIL.orderNO "
				+ " where a.EID = '"+eId+"' " );
		if (ecPlatformNo != null && ecPlatformNo.length()!=0) { 	
			sqlbuf.append( " AND  a.ecPlatformNo = '"+ecPlatformNo+"' ");
		}

		sqlbuf.append( " order by a.shipmentNO, OC_ORDER.orderNO , OC_ORDER_DETAIL.item ");

		sql = sqlbuf.toString();
		return sql;
	}

	/**
	 * 往数组中插入数据
	 * @param arr
	 * @param str
	 * @return
	 */
	private static String[] insert(String[] arr, String str)
	{
		int size = arr.length;
		String[] tmp = new String[size + 1];
		System.arraycopy(arr, 0, tmp, 0, size);
		tmp[size] = str;
		return tmp;
	}



}
