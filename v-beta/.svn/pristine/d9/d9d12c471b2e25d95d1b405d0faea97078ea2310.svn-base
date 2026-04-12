package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_StockTaskQueryReq;
import com.dsc.spos.json.cust.res.DCP_StockTaskQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_StockTaskQuery extends SPosBasicService<DCP_StockTaskQueryReq,DCP_StockTaskQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_StockTaskQueryReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();
		String beginDate = req.getRequest().getBeginDate();
		String endDate =req.getRequest().getEndDate();
		if(Check.Null(beginDate)){
			errMsg.append("开始日期不可为空值, ");
			isFail = true;
		}

		if(Check.Null(endDate)){
			errMsg.append("截止日期不可为空值, ");
			isFail = true;
		} 		

		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_StockTaskQueryReq> getRequestType() {
		return new TypeToken<DCP_StockTaskQueryReq>(){};
	}

	@Override
	protected DCP_StockTaskQueryRes getResponseType() {
		return new DCP_StockTaskQueryRes();
	}

	@Override
	protected DCP_StockTaskQueryRes processJson(DCP_StockTaskQueryReq req) throws Exception {
		//查詢條件		
		String organizationNO = req.getOrganizationNO();
		String eId = req.geteId();
		String langType = req.getLangType();
		String beginDate = req.getRequest().getBeginDate();
		String endDate =req.getRequest().getEndDate();
		String Status=req.getRequest().getStatus();
		String keyTxt = req.getRequest().getKeyTxt();
		String getType = req.getRequest().getGetType();
        String dateType = req.getRequest().getDateType();

        int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();

		//計算起啟位置
		int startRow = ((pageNumber - 1) * pageSize);
		startRow = ((pageNumber - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
		startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料
		//try
		//{
			DCP_StockTaskQueryRes res = null;
			res = this.getResponse();
			StringBuffer sb = new StringBuffer(" select * from (");
            sb.append( " select count (*) over() NUM,row_number() over(order by a.bdate desc, A.STOCKTASKNO desc ) as RN, A.*,B.WAREHOUSE_NAME,C.STOCKTAKENO,d.PTEMPLATE_NAME as ptemplatename," +
					" e1.name as employeename ,d1.departname as departname,e2.name as createbyname,e3.name as modifyname,e4.name as submitbyname,e5.name as confirmbyname,e6.name as cancelbyname,e7.name as accountbyname from DCP_STOCKTASK A ");
            sb.append( " left JOIN DCP_WAREHOUSE_LANG B ON A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO AND A.WAREHOUSE=B.WAREHOUSE and B.LANG_TYPE='"+ langType +"' ");
            sb.append( " LEFT JOIN DCP_STOCKTAKE C ON A.EID=C.EID AND A.ORGANIZATIONNO=C.ORGANIZATIONNO AND A.STOCKTASKNO=C.OFNO");
			sb.append(" left join DCP_PTEMPLATE d on d.eid=a.eid and d.ptemplateno=a.ptemplateno ");
			sb.append(" left join dcp_employee e1 on e1.eid=a.eid and e1.employeeno=a.employeeid ");
			sb.append(" left join DCP_DEPARTMENT_LANG d1 on d1.eid=a.eid and d1.departno=a.departid and d1.lang_type='"+langType+"' ");
			sb.append(" left join dcp_employee e2 on e2.eid=a.eid and e2.employeeno=a.createby ");
			sb.append(" left join dcp_employee e3 on e3.eid=a.eid and e3.employeeno=a.modifyby ");
			sb.append(" left join dcp_employee e4 on e4.eid=a.eid and e4.employeeno=a.submitby ");
			sb.append(" left join dcp_employee e5 on e5.eid=a.eid and e5.employeeno=a.confirmby ");
			sb.append(" left join dcp_employee e6 on e6.eid=a.eid and e6.employeeno=a.cancelby ");
			sb.append(" left join dcp_employee e7 on e7.eid=a.eid and e7.employeeno=a.accountby ");

			sb.append( " where A.EID='"+ eId +"' AND A.ORGANIZATIONNO='"+ organizationNO +"' ");

            if("bDate".equals(dateType)){
                sb.append( " and a.BDATE between "+beginDate +" and "+endDate+"    ");
            }

            if("sDate".equals(dateType)){
                sb.append( " and a.SDATE between "+beginDate +" and "+endDate+"    ");
            }

			if (Status != null && Status.length() > 0)
			{
                sb.append(" AND A.STATUS='" + Status + "'");
			}

			if (getType != null && getType.length() > 0 )
			{
				if (getType.equals("0")){
                    sb.append( " AND C.STOCKTAKENO is null   " );
				}
				else {
                    sb.append( " AND C.STOCKTAKENO is not null   ") ;
				}	
			}




			if (keyTxt != null && !keyTxt.isEmpty())
			{
                sb.append( " AND ( A.STOCKTASKNO like '%%"+ keyTxt +"%%' OR A.LOAD_DOCNO LIKE '%%"+keyTxt+"%%' OR A.MEMO LIKE '%%"+keyTxt+"%%')");
			}
            sb.append( " ) WHERE  (rn > " + startRow + " AND rn <= " + (startRow+pageSize) + " ) ");

			List<Map<String, Object>> getQData_Count = this.doQueryData(sb.toString(), null);
			int totalRecords;								//总笔数
			int totalPages;									//总页数
			if (getQData_Count != null && !getQData_Count.isEmpty())
			{ 
				Map<String, Object> oneData_Count = getQData_Count.get(0);
				String num = oneData_Count.get("NUM").toString();
				totalRecords=Integer.parseInt(num);
				//算總頁數
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
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

			if (getQData_Count != null && !getQData_Count.isEmpty())
			{ // 有資料，取得詳細內容
				res.setDatas(new ArrayList<DCP_StockTaskQueryRes.level1Elm>());
				for (Map<String, Object> oneData : getQData_Count) 
				{
					DCP_StockTaskQueryRes.level1Elm oneLv1 = res.new level1Elm();

					// 取得第一層資料庫搜尋結果
					String shopId = oneData.get("SHOPID").toString();
					String stockTaskNO = oneData.get("STOCKTASKNO").toString();
					//String stockTaskId = oneData.get("STOCKTASKID").toString();
					String sDate = oneData.get("SDATE").toString();
					String pTemplateNo = oneData.get("PTEMPLATENO").toString();
					String pTemplateName = oneData.get("PTEMPLATENAME").toString();
					String bDate = oneData.get("BDATE").toString();
					String memo = oneData.get("MEMO").toString();
					String status2 = oneData.get("STATUS").toString();
					String docType = oneData.get("DOC_TYPE").toString();				
					String isBTake = oneData.get("IS_BTAKE").toString();
					String loadDocType = oneData.get("LOAD_DOCTYPE").toString();
					String loadDocNO = oneData.get("LOAD_DOCNO").toString();
					String warehouse = oneData.get("WAREHOUSE").toString();
					String warehouseName = oneData.get("WAREHOUSE_NAME").toString();			
					String taskWay = oneData.get("TASKWAY").toString();		
					String notGoodsMode = oneData.get("NOTGOODSMODE").toString();	
					String stockTakeNO = oneData.get("STOCKTAKENO").toString();				
					String submitStatus = oneData.get("SUBMITSTATUS").toString();

					String createBy =oneData.get("CREATEBY").toString();
					String createByName =oneData.get("CREATEBYNAME").toString() ;
					String createDate =oneData.get("CREATE_DATE").toString();
					String createTime =oneData.get("CREATE_TIME").toString();
					String modifyBy =oneData.get("MODIFYBY").toString();
					String modifyByName =oneData.get("MODIFYNAME").toString() ;
					String modifyDate =oneData.get("MODIFY_DATE").toString();
					String modifyTime =oneData.get("MODIFY_TIME").toString();
					String submitBy =oneData.get("SUBMITBY").toString();
					String submitByName =oneData.get("SUBMITBYNAME").toString() ;
					String submitDate =oneData.get("SUBMIT_DATE").toString();
					String submitTime =oneData.get("SUBMIT_TIME").toString();
					String confirmBy =oneData.get("CONFIRMBY").toString();
					String confirmByName =oneData.get("CONFIRMBYNAME").toString() ;
					String confirmDate =oneData.get("CONFIRM_DATE").toString();
					String confirmTime =oneData.get("CONFIRM_TIME").toString();
					String cancelBy =oneData.get("CANCELBY").toString();
					String cancelByName =oneData.get("CANCELBYNAME").toString() ;
					String cancelDate =oneData.get("CANCEL_DATE").toString();
					String cancelTime =oneData.get("CANCEL_TIME").toString();
					String accountBy =oneData.get("ACCOUNTBY").toString();
					String accountByName =oneData.get("ACCOUNTBYNAME").toString();
					String accountDate =oneData.get("ACCOUNT_DATE").toString();
					String accountTime =oneData.get("ACCOUNT_TIME").toString();

					String createType = oneData.get("CREATETYPE").toString();
					String totSubTaskQty = oneData.get("TOTSUBTASKQTY").toString();
					String totCqty = oneData.get("TOTCQTY").toString();
					String warehouseType = oneData.get("WAREHOUSETYPE").toString();
					String employeeId = oneData.get("EMPLOYEEID").toString();
					String employeeName = oneData.get("EMPLOYEENAME").toString();
					String departId = oneData.get("DEPARTID").toString();
					String departName = oneData.get("DEPARTNAME").toString();
					String isAdjustStock = oneData.get("IS_ADJUST_STOCK").toString();

					// 處理調整回傳值；
					oneLv1.setShopId(shopId);
					oneLv1.setStockTaskNo(stockTaskNO);
					oneLv1.setbDate(bDate);
					oneLv1.setMemo(memo);
					oneLv1.setStatus(status2);
					oneLv1.setDocType(docType);				
					oneLv1.setIsBTake(isBTake);				
					oneLv1.setLoadDocType(loadDocType);
					oneLv1.setLoadDocNo(loadDocNO);				
					oneLv1.setWarehouse(warehouse);
					oneLv1.setWarehouseName(warehouseName);
					oneLv1.setTaskWay(taskWay);
					oneLv1.setNotGoodsMode(notGoodsMode);
					oneLv1.setIsAdjustStock(isAdjustStock);
					oneLv1.setStockTakeNo(stockTakeNO);
					oneLv1.setSubmitStatus(submitStatus);

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

					oneLv1.setpTemplateNo(pTemplateNo);
					oneLv1.setpTemplateName(pTemplateName);
					oneLv1.setsDate(sDate);
					//oneLv1.setStockTaskID(stockTaskId);
					oneLv1.setCreateType(createType);
					oneLv1.setTotCqty(totCqty);
					oneLv1.setTotSubTaskQty(totSubTaskQty);
					oneLv1.setWarehouseType(warehouseType);
					oneLv1.setEmployeeId(employeeId);
					oneLv1.setEmployeeName(employeeName);
					oneLv1.setDepartId(departId);
					oneLv1.setDepartName(departName);

					res.getDatas().add(oneLv1);
				}
			}
			else
			{
				res.setDatas(new ArrayList<DCP_StockTaskQueryRes.level1Elm>());
			}

			return res;
		//}
		//catch(Exception e)
		//{
		//	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		//}
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	}

	@Override
	protected String getQuerySql(DCP_StockTaskQueryReq req) throws Exception {
		return null;
	}

}
