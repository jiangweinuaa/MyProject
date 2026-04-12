package com.dsc.spos.service.imp.json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_StockAdjustQueryReq;
import com.dsc.spos.json.cust.res.DCP_StockAdjustQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_StockAdjustQuery extends SPosBasicService<DCP_StockAdjustQueryReq, DCP_StockAdjustQueryRes> {
	@Override
	protected boolean isVerifyFail(DCP_StockAdjustQueryReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();
		
		String beginDate = req.getRequest().getBeginDate();
		String endDate = req.getRequest().getEndDate();
		if(Check.Null(beginDate)) {
			errMsg.append("开始日期不可为空值,");
			isFail=true;
		}
		if(Check.Null(endDate)) {
			errMsg.append("截止日期不可为空值,");
			isFail=true;
		}
		
		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		return isFail;
	}
	
	@Override
	protected TypeToken<DCP_StockAdjustQueryReq> getRequestType() {
		return new TypeToken<DCP_StockAdjustQueryReq>(){};
	}
	
	@Override
	protected DCP_StockAdjustQueryRes getResponseType() {
		return new DCP_StockAdjustQueryRes();
	}
	
	@Override
	protected DCP_StockAdjustQueryRes processJson(DCP_StockAdjustQueryReq req) throws Exception {
		
		//try
		//{
			//查詢資料
			DCP_StockAdjustQueryRes res = this.getResponse();
			//单头总数
			String sql = this.getCountSql(req);
			List<Map<String, Object>> getQData_Count = this.doQueryData(sql,null);
			int totalRecords;								//总笔数
			int totalPages;									//总页数
			
			res.setDatas(new ArrayList<DCP_StockAdjustQueryRes.level1Elm>());
			if (getQData_Count != null && getQData_Count.isEmpty() == false)
			{
				Map<String, Object> oneData_Count = getQData_Count.get(0);
				String num = oneData_Count.get("NUM").toString();
				totalRecords=Integer.parseInt(num);
				
				//算總頁數
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
				
				sql=this.getQuerySql(req);
				List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, null);
				if (getQDataDetail != null && getQDataDetail.isEmpty() == false) {
					// 拼接返回图片路径  by jinzma 20210705
					String isHttps= PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
					String httpStr=isHttps.equals("1")?"https://":"http://";
					String domainName=PosPub.getPARA_SMS(dao, req.geteId(), "", "DomainName");
					if (domainName.endsWith("/")) {
						domainName = httpStr + domainName + "resource/image/";
					}else{
						domainName = httpStr + domainName + "/resource/image/";
					}
					
					//单头主键字段
					Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
					condition.put("DCP_ADJUST_ADJUSTNO", true);
					//调用过滤函数
					List<Map<String, Object>> getQHeader=MapDistinct.getMap(getQDataDetail, condition);
					for (Map<String, Object> oneData : getQHeader) {
						DCP_StockAdjustQueryRes.level1Elm oneLv1 = res.new level1Elm();
						// 取出第一层
						String adjustNO = oneData.get("DCP_ADJUST_ADJUSTNO").toString();
						String bDate = oneData.get("DCP_ADJUST_BDATE").toString();
						String memo = oneData.get("DCP_ADJUST_MEMO").toString();
						String status = oneData.get("DCP_ADJUST_STATUS").toString();
						String docType = oneData.get("DCP_ADJUST_DOC_TYPE").toString();
						String oType = oneData.get("DCP_ADJUST_OTYPE").toString();
						String ofNO = oneData.get("DCP_ADJUST_OFNO").toString();
						String loadDocType = oneData.get("DCP_ADJUST_LOAD_DOCTYPE").toString();
						String loadDocNO = oneData.get("DCP_ADJUST_LOAD_DOCNO").toString();
						String warehouse = oneData.get("WAREHOUSE_MAIN").toString();
						String warehouseName = oneData.get("WAREHOUSE_MAIN_NAME").toString();
						String createBy =oneData.get("CREATEBY").toString();
						String createByName =oneData.get("CREATENAME").toString();
						String createDate =oneData.get("CREATEDATE").toString();
						String createTime =oneData.get("CREATETIME").toString();
						String modifyBy =oneData.get("MODIFYBY").toString();
						String modifyByName =oneData.get("MODIFYNAME").toString();
						String modifyDate =oneData.get("MODIFYDATE").toString();
						String modifyTime =oneData.get("MODIFYTIME").toString();
						String submitBy =oneData.get("SUBMITBY").toString();
						String submitByName =oneData.get("SUBMITNAME").toString();
						String submitDate =oneData.get("SUBMITDATE").toString();
						String submitTime =oneData.get("SUBMITTIME").toString();
						String confirmBy =oneData.get("CONFIRMBY").toString();
						String confirmByName =oneData.get("CONFIRMNAME").toString();
						String confirmDate =oneData.get("CONFIRMDATE").toString();
						String confirmTime =oneData.get("CONFIRMTIME").toString();
						String cancelBy =oneData.get("CANCELBY").toString();
						String cancelByName =oneData.get("CANCELNAME").toString();
						String cancelDate =oneData.get("CANCELDATE").toString();
						String cancelTime =oneData.get("CANCELTIME").toString();
						String accountBy =oneData.get("ACCOUNTBY").toString();
						String accountByName =oneData.get("ACCOUNTNAME").toString();
						String accountDate =oneData.get("ACCOUNTDATE").toString();
						String accountTime =oneData.get("ACCOUNTTIME").toString();
						String totPqty = oneData.get("TOTPQTY").toString();
						String totAmt = oneData.get("TOTAMT").toString();
						String totCqty = oneData.get("TOTCQTY").toString();
						String totDistriAmt = oneData.get("TOT_DISTRIAMT").toString();
						
						
						//设置响应
						oneLv1.setAdjustNo(adjustNO);
						oneLv1.setBDate(bDate);
						oneLv1.setMemo(memo);
						oneLv1.setStatus(status);
						oneLv1.setDocType(docType);
						oneLv1.setOType(oType);
						oneLv1.setOfNo(ofNO);
						oneLv1.setLoadDocType(loadDocType);
						oneLv1.setLoadDocNo(loadDocNO);
						oneLv1.setWarehouse(warehouse);
						oneLv1.setWarehouseName(warehouseName);
						oneLv1.setCreateBy(createBy);
						oneLv1.setCreateByName(createByName);
						oneLv1.setCreateDate(createDate);
						oneLv1.setCreateTime(createTime);
						oneLv1.setModifyBy(modifyBy);
						oneLv1.setModifyByName(modifyByName);
						oneLv1.setModifyDate(modifyDate);
						oneLv1.setModifyTime(modifyTime);
						oneLv1.setSubmitBy(submitBy);
						oneLv1.setSubmitByName(submitByName);
						oneLv1.setSubmitDate(submitDate);
						oneLv1.setSubmitTime(submitTime);
						oneLv1.setConfirmBy(confirmBy);
						oneLv1.setConfirmByName(confirmByName);
						oneLv1.setConfirmDate(confirmDate);
						oneLv1.setConfirmTime(confirmTime);
						oneLv1.setCancelBy(cancelBy);
						oneLv1.setCancelByName(cancelByName);
						oneLv1.setCancelDate(cancelDate);
						oneLv1.setCancelTime(cancelTime);
						oneLv1.setAccountBy(accountBy);
						oneLv1.setAccountByName(accountByName);
						oneLv1.setAccountDate(accountDate);
						oneLv1.setAccountTime(accountTime);
						oneLv1.setTotPqty(totPqty);
						oneLv1.setTotAmt(totAmt);
						oneLv1.setTotCqty(totCqty);
						oneLv1.setTotDistriAmt(totDistriAmt);

                        oneLv1.setEmployeeId(oneData.get("EMPLOYEEID").toString());
                        oneLv1.setEmployeeName(oneData.get("EMPLOYEENAME").toString());
                        oneLv1.setDepartId(oneData.get("DEPARTID").toString());
                        oneLv1.setDepartName(oneData.get("DEPARTNAME").toString());


                        String UPDATE_TIME;
						SimpleDateFormat simptemp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						SimpleDateFormat allsimptemp=new SimpleDateFormat("yyyyMMddHHmmssSSS");
						if(oneData.get("UPDATE_TIME")==null||oneData.get("UPDATE_TIME").toString().isEmpty()) {
							UPDATE_TIME=allsimptemp.format(Calendar.getInstance().getTime());
						} else {
							UPDATE_TIME=oneData.get("UPDATE_TIME").toString();
						}
						oneLv1.setUpdate_time(simptemp.format(allsimptemp.parse(UPDATE_TIME)));
						oneLv1.setProcess_status(oneData.get("PROCESS_STATUS").toString());

						//添加
						res.getDatas().add(oneLv1);
					}
				} else {
					totalRecords = 0;
					totalPages = 0;
				}
			} else {
				totalRecords = 0;
				totalPages = 0;
			}
			
			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);
			
			return res;
			
		//} catch (Exception e) {
		//	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		//}
	}
	
	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	}
	
	@Override
	protected String getQuerySql(DCP_StockAdjustQueryReq req) throws Exception {
		String sql=null;
		String beginDate = req.getRequest().getBeginDate();
		String endDate = req.getRequest().getEndDate();
		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();
		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;
		
		String docType=req.getRequest().getDocType();
		String keyTxt=req.getRequest().getKeyTxt();
		String langType=req.getLangType();
		StringBuffer sqlbuf=new StringBuffer();
		sqlbuf.append(""
				+ " SELECT a.adjustno as DCP_ADJUST_ADJUSTNO,a.bdate as DCP_ADJUST_BDATE,a.memo as DCP_ADJUST_Memo,"
				+ " a.status as DCP_ADJUST_Status,"
				+ " a.doc_type as DCP_ADJUST_Doc_Type,a.otype as DCP_ADJUST_Otype,a.ofno as DCP_ADJUST_Ofno,"
				+ " a.load_doctype as DCP_ADJUST_Load_Doctype,"
				+ " a.load_docno as DCP_ADJUST_Load_Docno,"
				+ " A.WAREHOUSE AS WAREHOUSE_MAIN,TAW.WAREHOUSE_NAME AS WAREHOUSE_MAIN_NAME, "
				+ " A.TOT_PQTY AS TOTPQTY,A.TOT_CQTY as TOTCQTY,A.TOT_AMT as TOTAMT,A.TOT_DISTRIAMT, "
				+ " A.UPDATE_TIME as UPDATE_TIME,A.PROCESS_STATUS as PROCESS_STATUS,"
				+ " A.CREATEBY AS CREATEBY,A.CREATE_DATE AS CREATEDATE,A.CREATE_TIME AS CREATETIME,b0.op_name as CREATENAME,"
				+ " A.MODIFYBY AS MODIFYBY,A.MODIFY_DATE AS MODIFYDATE,A.MODIFY_TIME AS MODIFYTIME,b1.op_name as MODIFYNAME,"
				+ " A.SUBMITBY AS SUBMITBY,A.SUBMIT_DATE AS SUBMITDATE,A.SUBMIT_TIME AS SUBMITTIME,b2.op_name as SUBMITNAME,"
				+ " A.CONFIRMBY AS CONFIRMBY,A.CONFIRM_DATE AS CONFIRMDATE,A.CONFIRM_TIME AS CONFIRMTIME,b3.op_name as CONFIRMNAME,"
				+ " A.CANCELBY AS CANCELBY,A.CANCEL_DATE AS CANCELDATE,A.CANCEL_TIME AS CANCELTIME,b4.op_name as CANCELNAME,"
				+ " A.ACCOUNTBY AS ACCOUNTBY,A.ACCOUNT_DATE AS ACCOUNTDATE,A.ACCOUNT_TIME AS ACCOUNTTIME,b5.op_name as ACCOUNTNAME, "
                + " a.employeeid,a.departid,e0.name as employeename,d0.departname "
                + " FROM DCP_ADJUST a "
				+ " LEFT JOIN DCP_WAREHOUSE_LANG TAW ON A.EID=TAW.EID AND A.ORGANIZATIONNO=TAW.ORGANIZATIONNO AND "
				+ " A.WAREHOUSE=TAW.WAREHOUSE AND  TAW.LANG_TYPE ='"+langType+"' "
				+ " LEFT JOIN platform_staffs_lang b0 ON A.EID=b0.EID AND A.CREATEBY=b0.opno  and b0.lang_type='"+req.getLangType()+"' "
				+ " LEFT JOIN platform_staffs_lang b1 ON A.EID=b1.EID AND A.modifyby=b1.opno  and b1.lang_type='"+req.getLangType()+"' "
				+ " LEFT JOIN platform_staffs_lang b2 ON A.EID=b2.EID AND A.SubmitBy=b2.opno  and b2.lang_type='"+req.getLangType()+"' "
				+ " LEFT JOIN platform_staffs_lang b3 ON A.EID=b3.EID AND A.ConfirmBy=b3.opno  and b3.lang_type='"+req.getLangType()+"' "
				+ " LEFT JOIN platform_staffs_lang b4 ON A.EID=b4.EID AND A.CancelBy=b4.opno  and b4.lang_type='"+req.getLangType()+"' "
				+ " LEFT JOIN platform_staffs_lang b5 ON A.EID=b5.EID AND A.AccountBy=b5.opno and b5.lang_type='"+req.getLangType()+"'  "
                + " LEFT JOIN dcp_employee e0 ON A.EID=e0.EID AND A.employeeid=e0.employeeno  "
                + " left join DCP_DEPARTMENT_LANG d0 on d0.eid=a.eid and d0.departno=a.departid and d0.lang_type='"+req.getLangType()+"'"

                + " WHERE "
				+ " a.SHOPID='"+req.getShopId()+"' "
				+ " AND a.EID='"+req.geteId()+"' "
				+ " AND a.adjustno in ("
				+ " select ADJUSTNO  from( "
				+ " SELECT ADJUSTNO,row_number() over (order by bDate desc, adjustNo   desc) as rn   "  //,ROWNUM rn
				+ " FROM DCP_adjust where "
				+ " BDATE  between "+beginDate+" and "+endDate+" " );
		
		if (keyTxt != null && keyTxt.length()>0) {
			sqlbuf.append(" AND (TOT_AMT LIKE '%%"+ keyTxt +"%%' OR TOT_PQTY LIKE '%%"+ keyTxt +"%%'  "
					+ " OR adjustNO LIKE '%%"+ keyTxt +"%%'  OR MEMO LIKE '%%"+ keyTxt +"%%'  "
					+ " OR OFNO LIKE '%%"+ keyTxt +"%%'  or load_docno LIKE '%%"+ keyTxt +"%%') ");
		}
		
		if(docType!=null && docType.length()>0) {
			sqlbuf.append(" AND DOC_TYPE='" + docType + "' ");
		}
		sqlbuf.append(" AND SHOPID='"+req.getShopId()+"'  "
				+ "AND EID='"+req.geteId()+"' "
				+ ") where rn>" + startRow + " and rn<=" + (startRow+pageSize) + ""
				+ ") "
				+ " order by a.bDate desc, a.adjustNo desc  ");
		
		sql=sqlbuf.toString();
		return sql;
	}
	
	protected String getCountSql(DCP_StockAdjustQueryReq req) throws Exception{
		String sql=null;
		String docType=req.getRequest().getDocType();
		String keyTxt=req.getRequest().getKeyTxt();
		String beginDate = req.getRequest().getBeginDate();
		String endDate = req.getRequest().getEndDate();
		StringBuffer sqlbuf=new StringBuffer();
		sqlbuf.append(" SELECT count(*) as num FROM DCP_adjust where BDATE between "+beginDate+" and "+endDate+" ");
		if (keyTxt != null && keyTxt.length()>0) {
			sqlbuf.append("AND (TOT_AMT LIKE '%%"+ keyTxt +"%%' OR TOT_PQTY LIKE '%%"+ keyTxt +"%%' "
					+ " OR adjustNO LIKE '%%"+ keyTxt +"%%'  OR MEMO LIKE '%%"+ keyTxt +"%%' "
					+ " OR OFNO LIKE '%%"+ keyTxt +"%%'  or load_docno LIKE '%%"+ keyTxt +"%%') ");
		}
		if(docType!=null && docType.length()>0) {
			sqlbuf.append("AND DOC_TYPE='" + docType + "' ");
		}
		sqlbuf.append(" AND SHOPID='"+req.getShopId()+"' "
				+ "AND EID='"+req.geteId()+"' ");
		sql=sqlbuf.toString();
		return sql;
	}
	
	
	
}
