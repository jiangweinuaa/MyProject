package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_OrderECExpFormatQueryReq;
import com.dsc.spos.json.cust.res.DCP_OrderECExpFormatQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderECExpFormatQuery extends SPosBasicService<DCP_OrderECExpFormatQueryReq, DCP_OrderECExpFormatQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_OrderECExpFormatQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_OrderECExpFormatQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderECExpFormatQueryReq>(){};
	}

	@Override
	protected DCP_OrderECExpFormatQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderECExpFormatQueryRes();
	}

	@Override
	protected DCP_OrderECExpFormatQueryRes processJson(DCP_OrderECExpFormatQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_OrderECExpFormatQueryRes res = null;
		res = this.getResponse();
		int totalRecords = 0; //总笔数
		int totalPages = 0;	
		try {
			String sql = null;
			sql = this.getQuerySql(req);
			String[] conditionValues = {};
			List<Map<String , Object>> getDatas = this.doQueryData(sql, conditionValues);
			
			if(getDatas.size() > 0){
				res.setDatas(new ArrayList<DCP_OrderECExpFormatQueryRes.level1Elm>());
				String num = getDatas.get(0).get("NUM").toString();
				totalRecords=Integer.parseInt(num);

				//算總頁數
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;	
				
				Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
				condition.put("EXPORDERFORMATNO", true);		
				//调用过滤函数
				List<Map<String, Object>> getQHeader=MapDistinct.getMap(getDatas, condition);
				
				for (Map<String, Object> oneData : getQHeader) 
				{
					DCP_OrderECExpFormatQueryRes.level1Elm lev1 = res.new level1Elm();
					lev1.setDatas(new ArrayList<DCP_OrderECExpFormatQueryRes.level2Elm>());
					
					String expOrderFormatNo = oneData.get("EXPORDERFORMATNO").toString();
					String expOrderFormatName = oneData.get("EXPORDERFORMATNAME").toString();
					String ecplatformNo = oneData.get("ECPLATFORMNO").toString();
					String ecplatformName = oneData.get("ECPLATFORMNAME").toString();
					String pickupWay = oneData.get("PICKUPWAY").toString();
					String exportType = oneData.get("EXPORTTYPE").toString();
					String ftp_uid = oneData.get("FTP_UID").toString();
					String ftp_pwd = oneData.get("FTP_PWD").toString();
					String filePath = oneData.get("FILEPATH").toString();
					String memo = oneData.get("MEMO").toString();
					String status = oneData.get("STATUS").toString();
					
					lev1.setExpOrderFormatNo(expOrderFormatNo);
					lev1.setExpOrderFormatName(expOrderFormatName);
					lev1.setEcplatformNo(ecplatformNo);
					lev1.setEcplatformName(ecplatformName);
					lev1.setPickupWay(pickupWay);
					lev1.setExportType(exportType);
					lev1.setFtp_uid(ftp_uid);
					lev1.setFtp_pwd(ftp_pwd);
					lev1.setFilePath(filePath);
					lev1.setMemo(memo);
					lev1.setStatus(status);
					
					//接下来是第二层 datas 
					for (Map<String, Object> oneData2 : getDatas) 
					{
						//过滤属于此单头的明细
						if(expOrderFormatNo.equals(oneData2.get("EXPORDERFORMATNO")))
						{	
							DCP_OrderECExpFormatQueryRes.level2Elm lev2 = res.new level2Elm();
							
							String item = oneData2.get("ITEM").toString();
							String excelColumnNo = oneData2.get("EXCELCOLUMNNO").toString();
							String excelColumnTitle = oneData2.get("EXCELCOLUMNTITLE").toString();
							String dataType = oneData2.get("DATATYPE").toString();
							String tableName = oneData2.get("TABLENAME").toString();
							String fieldName = oneData2.get("FIELDNAME").toString();
							String fieldMemo = oneData2.get("FIELDMEMO").toString();
							String fixValue = oneData2.get("FIXVALUE").toString();
							String status2 = oneData2.get("STATUS2").toString();
							
							lev2.setItem(item);
							lev2.setExcelColumnNo(excelColumnNo);
							lev2.setExcelColumnTitle(excelColumnTitle);
							lev2.setDataType(dataType);
							lev2.setTableName(tableName);
							lev2.setFieldName(fieldName);
							lev2.setFieldMemo(fieldMemo);
							lev2.setFixValue(fixValue);
							lev2.setStatus(status2);
							lev1.getDatas().add(lev2);
							lev2 = null;
							
						}
					}
					res.getDatas().add(lev1);
					lev1 = null;
				}
				
			}
			else{
				
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
	protected String getQuerySql(DCP_OrderECExpFormatQueryReq req) throws Exception {
		// TODO Auto-generated method stub

		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		String eId = req.geteId();
		String keyTxt = req.getKeyTxt();
		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();
		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;
		
		sqlbuf.append("SELECT * FROM ("
				+ " SELECT COUNT(DISTINCT a.EXPFORMATNO ) OVER() NUM ,dense_rank() over(ORDER BY a.EXPFORMATNO) rn , "
				+ " a.EXPFORMATNO as expOrderFormatNo ,a.EXPFORMATNAME as expOrderFormatName ,a. ECPLATFORMNO,a. ECPLATFORMNAME ,a. PICKUPWAY ,a. EXPORTTYPE ,"
				+ " a. FTP_UID ,a. FTP_PWD ,a. FILEPATH ,a.MEMO ,a.status , "
				+ " b.item , b.COLUMNNO AS excelCOLUMNNO ,  b.COLUMNNAME AS excelCOLUMNTITLE ,  b.DATAFROM AS datatype ,  "
				+ "b.tableName, b.fieldname , b.fieldMemo ,"
				+ " b.fixValue , b.status AS status2 "
				+ " FROM  OC_ECEXPORDERFORMAT A  "
				+ " LEFT JOIN  OC_ECEXPORDERFORMAT_DETAIL B  ON a.EID = b.EID AND a.EXPFORMATNO = b.EXPFORMATNO "
				+ " WHERE a.EID = '"+eId+"' "
				);
		
		if (keyTxt != null && keyTxt.length()!=0) { 	
			sqlbuf.append( "  AND ( a.EXPFORMATNO LIKE '%%"+keyTxt+"%%' or a.EXPFORMATNAME LIKE '%%"+keyTxt+"%%' )  ");
		}
		
		sqlbuf.append( "  ORDER BY  b.item , a.EXPFORMATNO  )  "
				+  " where rn > "+startRow+" and rn <= "+(startRow+pageSize)+ " order by  item ,  expOrderFormatNo ");
		sql = sqlbuf.toString();
		return sql;
	}

}
