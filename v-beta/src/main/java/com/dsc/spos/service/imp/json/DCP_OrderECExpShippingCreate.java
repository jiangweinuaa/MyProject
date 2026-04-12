package com.dsc.spos.service.imp.json;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.dao.DataValue.DataExpression;
import com.dsc.spos.json.cust.req.DCP_OrderECExpShippingCreateReq;
import com.dsc.spos.json.cust.res.DCP_OrderECExpShippingCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

/**
 * 电商订单导出
 * 
 * @author yuanyy 2019-03-21
 *
 */
public class DCP_OrderECExpShippingCreate
		extends SPosAdvanceService<DCP_OrderECExpShippingCreateReq, DCP_OrderECExpShippingCreateRes> {

	@Override
	protected void processDUID(DCP_OrderECExpShippingCreateReq req, DCP_OrderECExpShippingCreateRes res)
			throws Exception {
		// TODO Auto-generated method stub
		// 更新导出状态
		String eId = req.geteId();
		String shopId = req.getShopId();
		String expOrderFormatNo = req.getExpOrderFormatNo();
		String ecPlatformNo = req.getEcPlatformNo();

		String list[] = req.getShipmentNo();
		for (int i = 0; i < list.length; i++) {
			list[i] = list[i].replaceAll(list[i], "'" + list[i] + "'");
		}
		String shipmentNo = StringUtils.join(list, ",");

		// 1、创建工作簿
		Workbook wb = new XSSFWorkbook();
		// 1.1、设置表格的格式----居中
		CellStyle cs = wb.createCellStyle();
		cs.setAlignment(HorizontalAlignment .CENTER);
	
		// 2.1、创建工作表
		Calendar cal = Calendar.getInstance();// 获得当前时间
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String createDate = df.format(cal.getTime());
		df = new SimpleDateFormat("HHmmss");
		String createTime = df.format(cal.getTime());

		String sheetName = ecPlatformNo + createDate + createTime;
		Sheet sheet = wb.createSheet(sheetName);
		// 2.2、合并单元格
		// sheet.addMergedRegion(new CellRangeAddress(4, 8, 5, 9));
		// 3.1、创建行----表头行
		Row row = sheet.createRow(0);

		try {
			String sql = null;
			sql = this.getFormatDatas(req); // 查询导出格式
			String[] conditionValues = {};
			List<Map<String, Object>> getDatas = this.doQueryData(sql, conditionValues);
			if (!getDatas.isEmpty()) {

				String columnName = null;
				// 过滤出 列名,作为excel 的单头第一行
				Map<String, Boolean> condition1 = new HashMap<String, Boolean>(); // 查詢條件
				condition1.put("COLUMNNAME", true);
				List<Map<String, Object>> getColumnDatas = MapDistinct.getMap(getDatas, condition1);
				int columnCount = getColumnDatas.size();
				// 创建第一行单头
				for (int i = 0; i < getColumnDatas.size(); i++) {
					columnName = getColumnDatas.get(i).get("COLUMNNAME").toString();
					// 4、创建格
					Cell cell = row.createCell(i);
					cell.setCellValue(columnName);
					cell.setCellStyle(cs);
				}

				sql = this.getExcelDatas(eId, ecPlatformNo, shipmentNo); // 查询
																				// 电商平台订单，
																				// 填充excel表单数据
				List<Map<String, Object>> getDetailDatas = this.doQueryData(sql, null);

				if (!getDetailDatas.isEmpty()) {

					Map<String, Boolean> condition = new HashMap<String, Boolean>(); // 查询条件
					condition.put("ORDERNO", true);// 按照订单号过滤
					List<Map<String, Object>> getQHeader = MapDistinct.getMap(getDetailDatas, condition);

					for (int i = 0; i < getQHeader.size(); i++) {
						
						String shopeePluColumn = ""; // 虾皮专用
						//下面这个for循环不能删， 以后虾皮可能会用到， 特殊处理
						row = sheet.createRow(i + 1); // 从第二行开始, 第一行是固定标题行

						for (Map<String, Object> expData : getDatas) {
							String columnNo = expData.get("COLUMNNO").toString();
							String fieldName = expData.get("FIELDNAME").toString();
							
							int columnNoInt = Integer.parseInt(columnNo);
							// 确定列号
							for (int j = 0; j < columnCount; j++) {
								if(j+1 == columnNoInt){
									String fieldValue = getQHeader.get(i).get(fieldName).toString();
									row.createCell(j).setCellValue(fieldValue);
								}

							}
						}
					}

					try {

						String fileName = ecPlatformNo + createDate + createTime + ".xls";
						// 传进来的文件名 + 绝对路径 ， 组合成文件完成路径
						String filePath = System.getProperty("catalina.home") + "\\webapps\\EC\\" + ecPlatformNo
								+ "\\export\\";
						// 暂且设置为 固定 EC + 平台名 + 文件名， 如果后期规划需要变更 平台编码，修改服务器文件夹名即可
						File file = new File(filePath);
						// 如果文件夹不存在则创建
						if (!file.exists() && !file.isDirectory()) {
							boolean bl = file.mkdirs();
							//System.out.println(bl);
						}
						file = null;

						filePath = filePath + fileName;

						FileOutputStream fout = new FileOutputStream(filePath);

						wb.write(fout);
						fout.close();

						// 导出之后， 更新货运单导出状态
						UptBean ub1 = new UptBean("OC_SHIPMENT");
						// 条件
						ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
						ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
						ub1.addCondition("SHIPMENTNO", new DataValue(shipmentNo, Types.VARCHAR, DataExpression.IN));
						// 值
						ub1.addUpdateValue("EXPORTDOC", new DataValue("Y", Types.VARCHAR));

						this.addProcessData(new DataProcessBean(ub1));
						this.doExecuteDataToDB();

						res.setExcelFileName(fileName);

					} catch (IOException e) {
			
					}

				} else {
					res.setExcelFileName("");
					res.setSuccess(true);
					res.setServiceDescription("查询货运单对应的订单信息为空！");
					return;
				}

			} else { // 当查询导出格式列为空的时候
				res.setExcelFileName("");
				res.setSuccess(true);
				res.setServiceDescription("查询当前格式信息为空");
			}

		} catch (Exception e) {
		
			res.setSuccess(false);
			res.setServiceDescription("服务执行失败！");
		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderECExpShippingCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderECExpShippingCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderECExpShippingCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderECExpShippingCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0; // 考慮當錯誤很多時則直接顯示格式錯誤；

		String[] shipmentNo = req.getShipmentNo();

		if (shipmentNo.length < 1 || shipmentNo.equals("")) {
			errCt++;
			errMsg.append("货运单号不可为空值  ");
			isFail = true;
		}
		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderECExpShippingCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderECExpShippingCreateReq>() {
		};
	}

	@Override
	protected DCP_OrderECExpShippingCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderECExpShippingCreateRes();
	}

	/**
	 * 查找每一列的数据
	 * 
	 * @param eId
	 * @param ecPlatformNo
	 * @param fieldNameStr
	 * @return
	 */
	private String getExcelDatas(String eId, String ecPlatformNo, String shipmentNo) {

		//// 查出主表的所有列,以及子表不同于主表的列. 不同的导出格式,获取不同的列即可
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
				+ " OC_ORDER_DETAIL.ISMEMO  ,"
				+ " OC_ORDER.CONTMAN , OC_ORDER.CONTTEL "
				+ " FROM DCP_shipment  a "
				+ " LEFT JOIN OC_ORDER OC_ORDER  ON a.EID = OC_ORDER.EID AND a.SHOPID = OC_ORDER.SHOPID  AND a.ec_OrderNo = OC_ORDER.orderNo "
				+ " LEFT JOIN OC_ORDER_DETAIL OC_ORDER_DETAIL on OC_ORDER.EID = OC_ORDER_DETAIL.EID and OC_ORDER.SHOPID = OC_ORDER_DETAIL.SHOPID and OC_ORDER.orderNo = OC_ORDER_DETAIL.orderNO "
				+ " where a.EID = '" + eId + "' ");
		// if (ecPlatformNo != null && ecPlatformNo.length()!=0) {
		// sqlbuf.append( " AND a.ecPlatformNo = '"+ecPlatformNo+"' ");
		// }
		// if (exportDoc != null && exportDoc.length()!=0) {
		// sqlbuf.append( " AND a.exportDoc = '"+exportDoc+"' ");
		// }
		if (shipmentNo != null && shipmentNo.length() != 0) {
			sqlbuf.append(" AND  a.shipmentNo in (" + shipmentNo + ") ");
		}

		sqlbuf.append(" order by a.shipmentNO, OC_ORDER.orderNO , OC_ORDER_DETAIL.item ");

		sql = sqlbuf.toString();
		return sql;
	}

	private String getFormatDatas(DCP_OrderECExpShippingCreateReq req) {

		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		String eId = req.geteId();
		String expOrderFormatNo = req.getExpOrderFormatNo(); // 导出格式编码
		// String ecPlatformNo = req.getEcPlatformNo(); //电商平台编码

		sqlbuf.append(
				" SELECT a.expformatno , a. ecplatformno , b.item , b.columnno, b.columnName , b.tablename , b.fieldname "
						+ " FROM OC_ecexporderFormat a  "
						+ " LEFT JOIN OC_ecexporderFormat_detail b ON a.EID = b.EID AND a.EXPFORMATNO = b.EXPFORMATNO "
						+ " WHERE a.EID = '" + eId + "' AND a.EXPFORMATNO = '" + expOrderFormatNo + "' ");

		// if (ecPlatformNo != null && ecPlatformNo.length()!=0) {
		// sqlbuf.append( " AND a.ecPlatformNo = '"+ecPlatformNo+"' ");
		// }

		sqlbuf.append(" order by a.expformatno , b.item , b.columnNo , b.tableName , b.fieldName ");

		sql = sqlbuf.toString();
		return sql;

	}

}
