package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_OrderECFormatQueryReq;
import com.dsc.spos.json.cust.res.DCP_OrderECFormatQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

/**
 * 电商订单导入格式查询
 * @author yuanyy
 *
 */
public class DCP_OrderECFormatQuery extends SPosBasicService<DCP_OrderECFormatQueryReq, DCP_OrderECFormatQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_OrderECFormatQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_OrderECFormatQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderECFormatQueryReq>(){};
	}

	@Override
	protected DCP_OrderECFormatQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderECFormatQueryRes();
	}

	@Override
	protected DCP_OrderECFormatQueryRes processJson(DCP_OrderECFormatQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_OrderECFormatQueryRes res = null;
		res = this.getResponse();
		int totalRecords = 0; //总笔数
		int totalPages = 0;	
		try {
			String sql = null;
			sql = this.getQuerySql(req);
			String[] conditionValues = {};
			List<Map<String , Object>> getDatas = this.doQueryData(sql, conditionValues);
			
			if(getDatas.size() > 0){
				res.setDatas(new ArrayList<DCP_OrderECFormatQueryRes.level1Elm>());
				String num = getDatas.get(0).get("NUM").toString();
				totalRecords=Integer.parseInt(num);

				//算總頁數
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;	
				
				Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
				condition.put("ORDERFORMATNO", true);		
				//调用过滤函数
				List<Map<String, Object>> getQHeader=MapDistinct.getMap(getDatas, condition);
				
				for (Map<String, Object> oneData : getQHeader) 
				{
					DCP_OrderECFormatQueryRes.level1Elm lev1 = res.new level1Elm();
					lev1.setDatas(new ArrayList<DCP_OrderECFormatQueryRes.level2Elm>());
					
					String orderFormatNo = oneData.get("ORDERFORMATNO").toString();
					String orderFormatName = oneData.get("ORDERFORMATNAME").toString();
					String ecplatformNo = oneData.get("ECPLATFORMNO").toString();
					String ecplatformName = oneData.get("ECPLATFORMNAME").toString();
					String pickupWay = oneData.get("PICKUPWAY").toString();
					String pickupWayName = oneData.get("PICKUPWAYNAME").toString();
					String startLine = oneData.get("STARTLINE").toString();
					String fileFrom = oneData.get("FILEFROM").toString();
					String ftp_uid = oneData.get("FTP_UID").toString();
					String ftp_pwd = oneData.get("FTP_PWD").toString();
					String filePath = oneData.get("FILEPATH").toString();
					String memberGet = oneData.get("MEMBERGET").toString();
					String orderShop = oneData.get("ORDERSHOP").toString();
					String orderWarehouse = oneData.get("ORDERWAREHOUSE").toString();
					String currencyNo = oneData.get("CURRENCYNO").toString();
					String status = oneData.get("STATUS").toString();
					
					String orderShopName = oneData.get("ORDERSHOPNAME").toString();
					String orderWarehouseName = oneData.get("ORDERWAREHOUSENAME").toString();
					String customerNo = oneData.get("CUSTOMERNO").toString();
					String customerName = oneData.get("CUSTOMERNAME").toString();
					String canInvoice = oneData.get("CANINVOICE").toString();
					
					lev1.setOrderFormatNo(orderFormatNo);
					lev1.setOrderFormatName(orderFormatName);
					lev1.setEcplatformNo(ecplatformNo);
					lev1.setEcplatformName(ecplatformName);
					lev1.setPickupWay(pickupWay);
					lev1.setPickupWayName(pickupWayName);
					lev1.setStartLine(startLine);
					lev1.setFileFrom(fileFrom);
					lev1.setFtp_uid(ftp_uid);
					lev1.setFtp_pwd(ftp_pwd);
					lev1.setFilePath(filePath);
					lev1.setMemberGet(memberGet);
					lev1.setOrderShop(orderShop);
					lev1.setOrderShopName(orderShopName);
					lev1.setOrderWarehouseName(orderWarehouseName);
					lev1.setOrderWarehouse(orderWarehouse);
					lev1.setCurrencyNo(currencyNo);
					lev1.setStatus(status);
					lev1.setCustomerNO(customerNo);
					lev1.setCustomerName(customerName);
					lev1.setCanInvoice(canInvoice);
					
					//接下来是第二层 datas 
					for (Map<String, Object> oneData2 : getDatas) 
					{
						//过滤属于此单头的明细
						if(orderFormatNo.equals(oneData2.get("ORDERFORMATNO")))
						{	
							DCP_OrderECFormatQueryRes.level2Elm lev2 = res.new level2Elm();
							
							String item = oneData2.get("ITEM").toString();
							String tableName = oneData2.get("TABLENAME").toString();
							String fieldName = oneData2.get("FIELDNAME").toString();
							String fieldMemo = oneData2.get("FIELDMEMO").toString();
							String fromType = oneData2.get("FROMTYPE").toString();
							String fromValue = oneData2.get("FROMVALUE").toString();
							String splitColumn = oneData2.get("SPLITCOLUMN").toString();
							String status2 = oneData2.get("STATUS2").toString();
							lev2.setItem(item);
							lev2.setTableName(tableName);
							lev2.setFieldName(fieldName);
							lev2.setFieldMemo(fieldMemo);
							lev2.setFromType(fromType);
							lev2.setFromValue(fromValue);
							lev2.setSplitColumn(splitColumn);
							lev2.setStatus(status2);
							lev1.getDatas().add(lev2);
							lev2 = null;
									
							
						}
					}
					res.getDatas().add(lev1);
					lev1 = null;
				}
				
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
	protected String getQuerySql(DCP_OrderECFormatQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		String langType = req.getLangType();
		String eId = req.geteId();
		String keyTxt = req.getKeyTxt();
		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();
		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;
		
		sqlbuf.append("SELECT * FROM ( "
				 + " SELECT COUNT(DISTINCT a.orderFormatNo ) OVER() NUM ,dense_rank() over(ORDER BY a.orderFormatNo) rn ,"
				 + " a.orderFormatNo , a.orderFormatName , a.ecplatformNo , a.ecplatformName , a.pickupWay,"
				 + " a.pickupWayName, a.startLine , a.fileFrom , a.ftp_uid, a.ftp_pwd ,  a.filePath,  a.memberGet , a.SHOPID AS  orderShop  ,"
				 + " a.warehouse AS orderWarehouse , a.currencyNo , a.status , "
				 + " b.item ,  b.tableName , b.fieldname , b.fieldMemo ,  b.fromType , b.fromValue , b.splitColumn , b.status as status2, "
				 + " c.org_name as orderShopName,    d.warehouse_name as orderWarehouseName  ,"
				 + " a.eccustomerNo as customerNo , e.customer_name as customerName ,"
				 + " a.ISINVOICE as canInvoice "
				 + " FROM   OC_ECORDERFORMAT a "
				 + " LEFT JOIN OC_ECORDERFORMAT_detail b ON a.EID = b.EID "
				 + " AND a.ORDERFORMATNO = b.ORDERFORMATNO  "
				 
				 + " LEFT JOIN DCP_ORG_lang c ON a.EID = c.EID and a.SHOPID = c.organizationNO and c.lang_type = '"+langType+"' "
				 + " LEFT JOIN DCP_WAREHOUSE_lang d ON a.EID = d.EID and a.SHOPID = d.organizationNO and a.warehouse = d.warehouse "
				 + " and d.lang_Type = '"+langType+"' "
				 
				 + " LEFT JOIN DCP_CUSTOMER_LANG e on a.EID = e.EID and a.ecCustomerNo = e.Customer "
				 + " and e.lang_type = '"+langType+"' "
				 + " WHERE a.EID = '"+eId+"' " );
		
		if (keyTxt != null && keyTxt.length()!=0) { 	
			sqlbuf.append( " AND ( a.ORDERFORMATNO LIKE '%%"+keyTxt+"%%' or a.orderFormatName LIKE '%%"+keyTxt+"%%' ) ");
		}
		
		sqlbuf.append( "  ORDER BY a.orderFormatNo, b.item  )  "
				+  " where rn > "+startRow+" and rn <= "+(startRow+pageSize)+ " order by orderFormatNo ,item ");
		sql = sqlbuf.toString();
		return sql;
	}
	
}
