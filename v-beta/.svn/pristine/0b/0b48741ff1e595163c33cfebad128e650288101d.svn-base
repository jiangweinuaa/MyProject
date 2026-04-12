package com.dsc.spos.service.imp.json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dsc.spos.json.cust.req.DCP_StockTakeQueryReq;
import com.dsc.spos.json.cust.res.DCP_StockTakeQueryRes;
import com.dsc.spos.scheduler.job.FeeCreate;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
/**
 * 服務函數：StockTakeGet
 *    說明：盘点单查询
 * 服务说明：盘点单查询
 * @author JZMA
 * @since  2018-11-21
 */
public class DCP_StockTakeQuery extends SPosBasicService<DCP_StockTakeQueryReq, DCP_StockTakeQueryRes>
{
	Logger logger = LogManager.getLogger(FeeCreate.class.getName());
	@Override
	protected boolean isVerifyFail(DCP_StockTakeQueryReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		String beginDate = req.getRequest().getBeginDate();
		String endDate = req.getRequest().getEndDate();
		if (Check.Null(beginDate)) {
			errMsg.append("开始日期不可为空值, ");
			isFail = true;
		}
		if (Check.Null(endDate)) {
			errMsg.append("截止日期不可为空值, ");
			isFail = true;
		}

		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_StockTakeQueryReq> getRequestType() {
		return new TypeToken<DCP_StockTakeQueryReq>(){};
	}

	@Override
	protected DCP_StockTakeQueryRes getResponseType() {
		return new DCP_StockTakeQueryRes();
	}

	@Override
	protected DCP_StockTakeQueryRes processJson(DCP_StockTakeQueryReq req) throws Exception {
		DCP_StockTakeQueryRes res = this.getResponse();
		try
		{
            String  sql = this.getQuerySql(req);				//查询盘点单单头数据
            List<Map<String, Object>> getQData = this.doQueryData(sql,null);

            //给分页字段赋值
			//String sql = this.getQuerySql_Count(req);				  												//查询总笔数
			//List<Map<String, Object>> getQData_Count = this.doQueryData(sql, null);
			int totalRecords;								//总笔数
			int totalPages;									//总页数
			res.setDatas(new ArrayList<DCP_StockTakeQueryRes.level1Elm>());
			if (getQData != null && getQData.isEmpty() == false)
			{
				String num = getQData.get(0).get("NUM").toString();
				totalRecords=Integer.parseInt(num);
				//算總頁數
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
				//单头查询
				//sql = this.getQuerySql(req);				//查询盘点单单头数据
				//List<Map<String, Object>> getQData = this.doQueryData(sql,null);
				if (getQData != null && getQData.isEmpty() == false)  // 有資料，取得詳細內容
				{
					for (Map<String, Object> oneData : getQData) {
						DCP_StockTakeQueryRes.level1Elm oneLv1 = res.new level1Elm();

						// 取得第一層資料庫搜尋結果
						String shop1 = oneData.get("SHOPID").toString();
						String stocktakeNO = oneData.get("STOCKTAKENO").toString();
						String processERPNo = oneData.get("PROCESSERPNO").toString();
						String bDate = oneData.get("BDATE").toString();
						String memo = oneData.get("MEMO").toString();
						String status = oneData.get("STATUS").toString();
						String docType = oneData.get("DOCTYPE").toString();
						String oType = oneData.get("OTYPE").toString();
						String ofNO = oneData.get("OFNO").toString();
						String isBTake = oneData.get("ISBTAKE").toString();
						String loadDocType = oneData.get("LOADDOCTYPE").toString();
						String loadDocNO = oneData.get("LOADDOCNO").toString();
						String pTemplateNO = oneData.get("PTEMPLATENO").toString();
						String pTemplateName = oneData.get("PTEMPLATENAME").toString();
						String plStatus = oneData.get("PLSTATUS").toString();
						String warehouse = oneData.get("WAREHOUSE").toString();
						String warehouseName = oneData.get("WAREHOUSE_NAME").toString();
						String taskWay = oneData.get("TASKWAY").toString();
						String notGoodsMode = oneData.get("NOTGOODSMODE").toString();
						String totPqty = oneData.get("TOTPQTY").toString();
						String totAmt = oneData.get("TOTAMT").toString();
						String totCqty = oneData.get("TOTCQTY").toString();
						String totDistriAmt = oneData.get("TOT_DISTRIAMT").toString();
						String submitStatus = oneData.get("SUBMITSTATUS").toString();


						//【ID1039808】【金贝儿3403】盘点单审核增加处理不异动库存情况 by jinzma 20240327
						String isAdjustStock = oneData.get("IS_ADJUST_STOCK").toString();   //是否调整库存Y/N/X Y转库存 N转销售 X不异动
						if (Check.Null(isAdjustStock)) {
							isAdjustStock = "Y";
						}
						String createBy =oneData.get("CREATEBY").toString();
						String createByName =oneData.get("CREATENAME").toString();
						String createDate =oneData.get("CREATE_DATE").toString();
						String createTime =oneData.get("CREATE_TIME").toString();
						String modifyBy =oneData.get("MODIFYBY").toString();
						String modifyByName =oneData.get("MODIFYNAME").toString();
						String modifyDate =oneData.get("MODIFY_DATE").toString();
						String modifyTime =oneData.get("MODIFY_TIME").toString();
						String submitBy =oneData.get("SUBMITBY").toString();
						String submitByName =oneData.get("SUBMITNAME").toString();
						String submitDate =oneData.get("SUBMIT_DATE").toString();
						String submitTime =oneData.get("SUBMIT_TIME").toString();
						String confirmBy =oneData.get("CONFIRMBY").toString();
						String confirmByName =oneData.get("CONFIRMNAME").toString();
						String confirmDate =oneData.get("CONFIRM_DATE").toString();
						String confirmTime =oneData.get("CONFIRM_TIME").toString();
						String cancelBy =oneData.get("CANCELBY").toString();
						String cancelByName =oneData.get("CANCELNAME").toString();
						String cancelDate =oneData.get("CANCEL_DATE").toString();
						String cancelTime =oneData.get("CANCEL_TIME").toString();
						String accountBy =oneData.get("ACCOUNTBY").toString();
						String accountByName =oneData.get("ACCOUNTNAME").toString();
						String accountDate =oneData.get("ACCOUNT_DATE").toString();
						String accountTime =oneData.get("ACCOUNT_TIME").toString();
						String substockimport = oneData.get("SUBSTOCKIMPORT").toString();

                        String employeeid = oneData.get("EMPLOYEEID").toString();
                        String employeeName = oneData.get("EMPLOYEENAME").toString();
                        String departid = oneData.get("DEPARTID").toString();
                        String departName = oneData.get("DEPARTNAME").toString();

                        if (totPqty == null || totPqty.length()==0 ){
							totPqty="0";
						}
						if (totAmt == null || totAmt.length()==0 ){
							totAmt="0";
						}
						if (totCqty == null || totCqty.length()==0 ){
							totCqty="0";
						}
						if (totDistriAmt == null || totDistriAmt.length()==0 ){
							totDistriAmt="0";
						}


						// 處理調整回傳值；
						oneLv1.setShopId(shop1);
						oneLv1.setStockTakeNo(stocktakeNO);
						oneLv1.setProcessERPNo(processERPNo);
						oneLv1.setbDate(bDate);
						oneLv1.setMemo(memo);
						oneLv1.setStatus(status);
						oneLv1.setDocType(docType);
						oneLv1.setoType(oType);
						oneLv1.setOfNo(ofNO);
						oneLv1.setIsBTake(isBTake);
						oneLv1.setpTemplateNo(pTemplateNO);
						oneLv1.setpTemplateName(pTemplateName);
						oneLv1.setLoadDocType(loadDocType);
						oneLv1.setLoadDocNo(loadDocNO);
						oneLv1.setCreateByName(createByName);
						oneLv1.setPlStatus(plStatus);
						oneLv1.setWarehouse(warehouse);
						oneLv1.setWarehouseName(warehouseName);
						oneLv1.setTaskWay(taskWay);
						oneLv1.setTotPqty(totPqty);
						oneLv1.setTotAmt(totAmt);
						oneLv1.setTotCqty(totCqty);
						oneLv1.setTotDistriAmt(totDistriAmt);
						oneLv1.setNotGoodsMode(notGoodsMode);
						oneLv1.setSubmitStatus(submitStatus);
						String UPDATE_TIME;
						SimpleDateFormat simptemp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						SimpleDateFormat allsimptemp=new SimpleDateFormat("yyyyMMddHHmmssSSS");
						if(oneData.get("UPDATE_TIME")==null||oneData.get("UPDATE_TIME").toString().isEmpty())
						{
							UPDATE_TIME=allsimptemp.format(Calendar.getInstance().getTime());
						}
						else
						{
							UPDATE_TIME=oneData.get("UPDATE_TIME").toString();
						}
						oneLv1.setUpdate_time(simptemp.format(allsimptemp.parse(UPDATE_TIME)));
						oneLv1.setProcess_status(oneData.get("PROCESS_STATUS").toString());
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
						oneLv1.setIsAdjustStock(isAdjustStock);
						oneLv1.setSubStockImport(substockimport);
                        oneLv1.setEmployeeId(employeeid);
                        oneLv1.setEmployeeName(employeeName);
                        oneLv1.setDepartId(departid);
                        oneLv1.setDepartName(departName);

						res.getDatas().add(oneLv1);
					}
				}
				else
				{
					totalRecords = 0;
					totalPages = 0;
				}
			}
			else
			{
				totalRecords = 0;
				totalPages = 0;
			}

			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);
			return res;
		}
		catch (Exception e)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		//調整查出來的資料
	}

	@Override
	protected String getQuerySql(DCP_StockTakeQueryReq req) throws Exception {

		StringBuffer sqlbuf = new StringBuffer();
		String status = req.getRequest().getStatus();
		String keyTxt = req.getRequest().getKeyTxt();
		String beginDate = req.getRequest().getBeginDate();
		String endDate = req.getRequest().getEndDate();
		int pageSize = req.getPageSize();
		String eId = req.geteId();
		String shopId = req.getShopId();
		String langType = req.getLangType();

		//計算起啟位置
		int startRow = ((req.getPageNumber() - 1) * req.getPageSize());
		startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
		startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料

		sqlbuf.append(" "
				+" select * from ("
				+ " select count(*) over () num, row_number() over (order by rw asc,status asc,bdate desc,dno desc) as rn,"
				+ " a.* from ("
				//+ " SELECT 0 as rw, stocktaskno as dno,to_number(cast('' as nvarchar2(30))) as plStatus,cast('' as nvarchar2(30)) as processERPNO,"
				//+ " cast('' as nvarchar2(30)) as stocktakeNO,cast('' as nvarchar2(30)) as pTemplateNO,cast('' as nvarchar2(30)) as pTemplateName,"
			//	+ " A.SHOPID as SHOPID,to_number(A.BDATE) as bDate,A.MEMO as memo,A.STATUS as status,A.DOC_TYPE as docType, "
			//	+ " A.DOC_TYPE as otype ,A.stocktaskno as ofno,A.LOAD_DOCTYPE as loadDocType,A.LOAD_DOCNO as loadDocNO,"
				//+ " createBy, modifyby,SubmitBy,ConfirmBy,CancelBy,AccountBy,Create_Time,Create_Date,Modify_Date,Modify_TIME,"
			//	+ " Submit_Date,Submit_Time,Confirm_Date,Confirm_Time, "
			///	+ " Cancel_Date,Cancel_Time,Account_Date,Account_TIME,cast('' as nvarchar2(10)) as createName, "
			//	+ " cast('' as nvarchar2(10)) as modifyname, "
			//	+ " cast('' as nvarchar2(10)) as Submitname,cast('' as nvarchar2(10)) as Confirmname,cast('' as nvarchar2(10)) as Cancelname, "
			//	+ " cast('' as nvarchar2(10)) as Accountname,   "
			//	+ " 0 as totpqty,0 as totamt,0 as TOT_DISTRIAMT,0 as totcqty,is_btake as isBTake ,a.warehouse,a.taskway as taskway ,a.notgoodsmode as notgoodsmode , "
			//	+ " taw.warehouse_name, Translate('N' USING NCHAR_CS)  PROCESS_STATUS,A.UPDATE_TIME,A.SUBMITSTATUS,A.IS_ADJUST_STOCK,N'0' as substockimport,   "
             //   + " cast('' as nvarchar2(10)) as employeeid,cast('' as nvarchar2(10)) as  employeename,cast('' as nvarchar2(10)) as departid,cast('' as nvarchar2(10)) as departname"
			//	+ " FROM DCP_STOCKTASK A  left join DCP_WAREHOUSE_lang taw on a.EID=taw.EID and a.organizationno=taw.organizationno "
			//	+ " and a.warehouse=taw.warehouse and taw.lang_type='"+ langType +"' "
            //    + " where 1=1 "
        );

		//if (status != null && status.length()!=0){
		//	sqlbuf.append("  AND A.status = '"+ status +"' ");
		//}else{
		//	sqlbuf.append(" AND A.status = '6' ");
		//}

		//if (keyTxt != null && keyTxt.length() > 0)
		//{
		//	sqlbuf.append(" AND ( A.STOCKTASKNO like '%%"+ keyTxt +"%%' and a.LOAD_DOCNO LIKE '%%"+keyTxt+"%%' OR a.MEMO LIKE '%%"+keyTxt+"%%')");
		//}

		//sqlbuf.append(" AND a.SHOPID='"+ shopId +"' ");
		//sqlbuf.append(" AND a.EID='"+ eId +"'");
		//sqlbuf.append(" AND a.STOCKTASKNO NOT IN (SELECT OFNO FROM DCP_STOCKTAKE WHERE SHOPID='"+ shopId +"' and EID='"+ eId +"' and ofno is not null  )");
		//sqlbuf.append(" union  all	");
		sqlbuf.append(" "
				+ " SELECT 1 as rw, stocktakeno as dno, D.STATUS AS plStatus, a.process_Erp_no as processERPNo, A.stocktakeNO as stocktakeNO,A.pTemplateNO,C.pTemplate_Name as pTemplateName,A.SHOPID as SHOPID,to_number(A.BDATE) as bDate,A.MEMO as memo,A.STATUS as status,A.DOC_TYPE as docType "
				+ " ,A.OTYPE as otype ,A.OFNO as ofno,A.LOAD_DOCTYPE as loadDocType,A.LOAD_DOCNO as loadDocNO, "
				+ "  A.createBy, A.modifyby,A.SubmitBy,A.ConfirmBy,A.CancelBy,A.AccountBy,A.Create_Time,A.Create_Date,A.Modify_Date,A.Modify_TIME, "
				+ "  A.Submit_Date,A.Submit_Time,A.Confirm_Date,A.Confirm_Time,A.Cancel_Date,A.Cancel_Time,A.Account_Date,A.Account_TIME, "
				+ "  b.name as createName, b1.name as modifyname,b2.name as Submitname,b3.name as Confirmname,b4.name as Cancelname, "
				+ "  b5.name as Accountname,   "
				+ " A.TOT_PQTY as totpqty,A.TOT_AMT as totamt,A.TOT_DISTRIAMT, A.TOT_CQTY as totcqty ,a.is_btake as isBTake,a.warehouse,  A.TASKWAY as taskway , A.NOTGOODSMODE as notgoodsmode ,taw.warehouse_name,A.PROCESS_STATUS,"
				+ " A.UPDATE_TIME,Translate('1' USING NCHAR_CS) SUBMITSTATUS,A.IS_ADJUST_STOCK,A.substockimport,a.employeeid,e1.name as employeename,a.departid,d1.departname   "
				+ " FROM DCP_STOCKTAKE A "
				+ " LEFT JOIN dcp_employee B  ON A.EID=B.EID  AND A.CREATEBY=B.employeeno  "
				+ " LEFT JOIN dcp_employee b1 ON a.EID=b1.EID AND a.modifyby=b1.employeeno  "
				+ " LEFT JOIN dcp_employee b2 ON a.EID=b2.EID AND a.SubmitBy=b2.employeeno  "
				+ " LEFT JOIN dcp_employee b3 ON a.EID=b3.EID AND a.ConfirmBy=b3.employeeno "
				+ " LEFT JOIN dcp_employee b4 ON a.EID=b4.EID AND a.CancelBy=b4.employeeno "
				+ " LEFT JOIN dcp_employee b5 ON a.EID=b5.EID AND a.AccountBy=b5.employeeno  "
				+ " LEFT JOIN DCP_WAREHOUSE_LANG TAW ON A.EID=TAW.EID AND A.ORGANIZATIONNO=TAW.ORGANIZATIONNO AND A.WAREHOUSE=TAW.WAREHOUSE AND   TAW.LANG_TYPE='"+ langType +"' "
				+ " LEFT JOIN DCP_PTEMPLATE C ON A.PTEMPLATENO=C.PTEMPLATENO AND A.EID=C.EID AND C.DOC_TYPE='1' "
                + " left join dcp_employee e1 on e1.eid=a.eid and e1.employeeno=a.employeeid "
                + " left join dcp_department_lang d1 on d1.eid=a.eid and d1.departno=a.departid and d1.lang_type='"+langType+"'"
                + " LEFT JOIN DCP_ADJUST D ON A.EID=D.EID AND A.SHOPID=D.SHOPID AND A.STOCKTAKENO=D.OFNO  WHERE 1=1 ");

		sqlbuf.append(" AND a.BDATE between "+beginDate +" and "+endDate+" ");

		if (status != null && status.length()!=0)
		{
			if (status.equals("6"))
			{
				sqlbuf.append(" AND A.status = '0'  ");
			}
			else
			{
				sqlbuf.append(" AND A.status = '"+ status +"'  ");
			}

		}

		if (keyTxt != null && keyTxt.length() > 0)
		{
			sqlbuf.append(" AND (a.TOT_AMT LIKE '%%"+keyTxt+"%%' OR a.TOT_PQTY LIKE '%%"+keyTxt+"%%' OR a.STOCKTAKENO LIKE '%%"+keyTxt+"%%' OR a.LOAD_DOCNO LIKE '%%"+keyTxt+"%%' OR a.MEMO LIKE '%%"+keyTxt+"%%' "
					+ "  or a.process_ERP_No like '%%"+keyTxt+"%%'  )");
		}
		sqlbuf.append(" AND a.SHOPID='"+ shopId +"' ");
		sqlbuf.append(" AND a.EID='"+ eId +"' ) a ");
		sqlbuf.append(" ) where  rn > " + startRow + " AND rn <= " + (startRow+pageSize) );
		return sqlbuf.toString();

	}

	protected String getQuerySql_Count(DCP_StockTakeQueryReq req) throws Exception {
		String sql = "";
		StringBuffer sqlbuf = new StringBuffer("");
		String beginDate = req.getRequest().getBeginDate();
		String endDate = req.getRequest().getEndDate();
		String status = req.getRequest().getStatus();
		String keyTxt = req.getRequest().getKeyTxt();
		String shopid = req.getShopId();
		String eId = req.geteId();
		sqlbuf.append(""
				+ " SELECT count(*) as num "
				+ " FROM ("
				+ " SELECT N'' as stocktakeNO"
				+ " FROM DCP_STOCKTASK A "
				+ " WHERE A.SHOPID='"+shopid+"'  AND A.EID='"+eId+"'  AND A.STOCKTASKNO NOT IN ("
				+ " SELECT OFNO FROM DCP_STOCKTAKE WHERE SHOPID='"+shopid+"' and EID='"+eId+"' and ofno is not null )"
		);
		if (status != null && status.length()!=0)
		{
			sqlbuf.append(" AND A.status = '"+ status +"'  ");
		}
		else
		{
			sqlbuf.append(" AND A.status = '6'  "  	);
		}

		if (keyTxt != null && keyTxt.length()!=0) {
			sqlbuf.append(""
					+ " AND (A.STOCKTASKNO like '%%"+ keyTxt +"%%'  "
					+ " OR  A.MEMO like '%%"+ keyTxt +"%%'   "
					+ " OR  A.LOAD_DOCNO like '%%"+ keyTxt +"%%')   "
			);
		}
		sqlbuf.append(" union all ");
		sqlbuf.append(" SELECT A.stocktakeNO"
				+ " FROM DCP_STOCKTAKE A "
				//2018-11-09 添加日期查询条件
				+ " WHERE  A.BDATE between "+beginDate +" AND "+endDate+" "
				+ " AND A.SHOPID='"+shopid+"'   AND A.EID='"+eId+"'  ");
		if (status != null && status.length()!=0){
			if (status.equals("6")){
				sqlbuf.append(" AND A.status = '0'  ");
			}else{
				sqlbuf.append(" AND A.status = '"+ status +"'  ");
			}
		}

		if (keyTxt != null && keyTxt.length()!=0) {
			sqlbuf.append(""
					+ " AND (A.TOT_AMT like '%%"+ keyTxt +"%%'  "
					+ " OR  A.TOT_PQTY like '%%"+ keyTxt +"%%'   "
					+ " OR  A.stocktakeNO like '%%"+ keyTxt +"%%'   "
					+ " OR  A.MEMO like '%%"+ keyTxt +"%%'   "
					+ " OR  A.LOAD_DOCNO like '%%"+ keyTxt +"%%' "
					+ " OR  A.process_ERP_No like '%%"+keyTxt+"%%' )   "
			);
		}
		sqlbuf.append(" ) ");
		sql = sqlbuf.toString();
		//	logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"stocktakeqerycount_sql:"+sql);
		return sql;
	}

}
