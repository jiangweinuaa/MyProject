package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_MainErrorInfoQueryReq;
import com.dsc.spos.json.cust.res.DCP_MainErrorInfoQueryRes;
import com.dsc.spos.json.cust.res.DCP_MainErrorInfoQueryRes.level1Elm_WsError;
import com.dsc.spos.scheduler.job.InsertWSLOG;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.spreada.utils.chinese.ZHConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_MainErrorInfoQuery extends SPosBasicService<DCP_MainErrorInfoQueryReq,DCP_MainErrorInfoQueryRes>  {
	
	@Override
	protected boolean isVerifyFail(DCP_MainErrorInfoQueryReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();
		
		//必传值不为空
		String dataType = req.getRequest().getDataType();
		String wsType = req.getRequest().getWsType();
		
		if(Check.Null(dataType)){
			errMsg.append("数据分类不可为空值, ");
			isFail = true;
		}
		
		if(Check.Null(wsType)){
			errMsg.append("WS类型不可为空值, ");
			isFail = true;
		}
		
		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return false;
	}
	
	@Override
	protected TypeToken<DCP_MainErrorInfoQueryReq> getRequestType() {
		return new TypeToken<DCP_MainErrorInfoQueryReq>(){} ;
	}
	
	@Override
	protected DCP_MainErrorInfoQueryRes getResponseType() {
		return new DCP_MainErrorInfoQueryRes();
	}
	
	@Override
	protected DCP_MainErrorInfoQueryRes processJson(DCP_MainErrorInfoQueryReq req) throws Exception {
		
		DCP_MainErrorInfoQueryRes res = this.getResponse();
		String wsType = req.getRequest().getWsType();  //0.ERP->云中台  1.云中台->ERP
		try {
			StringBuilder errorMessage = new StringBuilder();
			res.setWsError(new ArrayList<>());
			List<level1Elm_WsError> wsError = new ArrayList<>();
			boolean isFail = true;
			if (wsType.equals("0")) {
				isFail = getWsErrorDown(wsError,req,errorMessage) ;
			} else if (wsType.equals("1")) {
				isFail = getWsErrorUp(wsError,req,errorMessage) ;
			}
			
			if (!isFail) {
				if (!wsError.isEmpty()) {
					res.setWsError(wsError);
				} else {
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500,"未查询到异常信息");
				}
			} else {
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,errorMessage.toString());
			}
			
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功！");
			return res;
		}
		
		catch (Exception e) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
	}
	
	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	}
	
	@Override
	protected String getQuerySql(DCP_MainErrorInfoQueryReq req) throws Exception {
		return null;
	}
	
	private boolean getWsErrorDown(List<level1Elm_WsError>wsError,DCP_MainErrorInfoQueryReq req,StringBuilder errorMessage) {
		boolean isFail=false;
		String eId = req.geteId();
		String dataType = req.getRequest().getDataType();
		String langType = req.getLangType();
		String serviceName =req.getRequest().getServiceName();  ///pos.receiving.create
		
		if (Check.Null(serviceName)) {
			switch(dataType) {
				case "门店收货通知单新增" :
					serviceName="pos.receiving.create";
					break;
				case "門店收貨通知單新增" :
					serviceName="pos.receiving.create";
					break;
				case "门店盘点计划单新增" :
					serviceName="pos.counting.create";
					break;
				case "門店盤點計畫單新增" :
					serviceName="pos.counting.create";
					break;
				case "门店退货出库单更新" :
					serviceName="pos.return.update";
					break;
				case "門店退貨出庫單更新" :
					serviceName="pos.return.update";
					break;
				case "门店日结检核" :
					serviceName="pos.day.end.check";
					break;
				case "門店日結檢核" :
					serviceName="pos.day.end.check";
					break;
				case "门店库存调整单新增" :
					serviceName="pos.adjust.create";
					break;
				case "門店庫存調整單新增" :
					serviceName="pos.adjust.create";
					break;
				case "门店上传异常查询" :
					serviceName="pos.up.error.log.get";
					break;
				case "門店上傳異常查詢" :
					serviceName="pos.up.error.log.get";
					break;
				case "门店报损单更新" :
					serviceName="pos.scrap.update";
					break;
				case "門店報損單更新" :
					serviceName="pos.scrap.update";
					break;
				case "门店调拨通知单新增" :
					serviceName="pos.transfer.create";
					break;
				case "門店調撥通知單新增" :
					serviceName="pos.transfer.create";
					break;
				case "门店单据驳回新增" :
					serviceName="pos.reject.create";
					break;
				case "門店單據駁回新增" :
					serviceName="pos.reject.create";
					break;
				case "节日订单审核" :
					serviceName="pos.orgorder.update";
					break;
				case "節日訂單審核" :
					serviceName="pos.orgorder.update";
					break;
				case "门店单据撤销" :
					serviceName="pos.undo.create";
					break;
				case "門店單據撤銷" :
					serviceName="pos.undo.create";
					break;
				case "总部订单状态变更" :
					serviceName="pos.orderstatus.update";
					break;
				case "總部訂單狀態變更" :
					serviceName="pos.orderstatus.update";
					break;
				case "云中台总部订单修改" :
					serviceName="pos.orgorder.modify";
					break;
				case "雲中臺總部訂單修改" :
					serviceName="pos.orgorder.modify";
					break;
				case "要货单审核/反审核" :
					serviceName="pos.requisition.update";
					break;
				case "要貨單審核/反審核" :
					serviceName="pos.requisition.update";
					break;
				case "费用单状态更新" :
					serviceName="pos.fee.update";
					break;
				case "費用單狀態更新" :
					serviceName="pos.fee.update";
					break;
				case "要货单新增" :
					serviceName="pos.requisition.create";
					break;
				case "要货单结案" :
					serviceName="pos.requisition.ecsflg";
					break;
				case "接口名称未能解析" :
					serviceName=" ";
					break;
				case "接口名稱未能解析" :
					serviceName=" ";
					break;
			}
		}
		if (Check.Null(serviceName)) {
			serviceName=" ";
		}
		
		try {
			String sql = " select * from ( "
					+ " select rownum rn,a.organizationno,a.docno,b.org_name,a.error_msg,a.request_XML,a.response_XML, "
					+ " a.modify_date,a.modify_time,a.create_date,a.create_time from DCP_wslog a "
					+ " left join DCP_ORG_lang b on a.EID=b.EID and a.organizationno=b.organizationno and b.lang_type='"+langType+"' "
					+ " where (a.EID='"+eId+"' or a.EID=' ') and a.process_status='N' and a.service_type='2' and a.service_name='"+serviceName+"' "
					+ " ) where rn<=100 order by organizationno,docno ";
			
			List<Map<String, Object>> getQData = this.doQueryData(sql, null);
			if (getQData != null && !getQData.isEmpty()) {
				int item = 1;
				for (Map<String,Object>oneData : getQData ) {
					level1Elm_WsError oneLv1 = new DCP_MainErrorInfoQueryRes().new level1Elm_WsError();
					String shopId = oneData.get("ORGANIZATIONNO").toString();
					String docNO = oneData.get("DOCNO").toString();
					String errorMsg = oneData.get("ERROR_MSG").toString();
					String modifyDate = oneData.get("MODIFY_DATE").toString();
					String modifyTime= oneData.get("MODIFY_TIME").toString();
					String shopName=  oneData.get("ORG_NAME").toString();
					
					String requestXML= "" ;
					try {
						if (oneData.get("REQUEST_XML")!=null && !Check.Null(oneData.get("REQUEST_XML").toString())) {
							requestXML = oneData.get("REQUEST_XML").toString();
						}
					} catch(Exception e) {
						requestXML="" ;
					}
					
					String responseXML="" ;
					try {
						if  (oneData.get("RESPONSE_XML")!=null && !Check.Null(oneData.get("RESPONSE_XML").toString())) {
							responseXML = oneData.get("RESPONSE_XML").toString();
						}
					} catch(Exception e) {
						responseXML="" ;
					}
					
					oneLv1.setItem(String.valueOf(item));
					oneLv1.setShopId(shopId);
					oneLv1.setDocNo(docNO);
					oneLv1.setErrorMsg(errorMsg);
					oneLv1.setRequestXML(requestXML);
					oneLv1.setResponseXML(responseXML);
					oneLv1.setModifyDate(modifyDate);
					oneLv1.setModifyTime(modifyTime);
					oneLv1.setShopName(shopName);
					wsError.add(oneLv1);
					item++;
				}
			}
		} catch (Exception e) {
			errorMessage.append(e.getMessage());
			return true;
		}
		return false;
	}
	
	private boolean getWsErrorUp(List<level1Elm_WsError>wsError,DCP_MainErrorInfoQueryReq req,StringBuilder errorMessage) {
		boolean isFail=false;
		String eId = req.geteId();
		String langType = req.getLangType();
		String dataType = req.getRequest().getDataType();
		String jobName = req.getRequest().getJobName();
		String serviceName="";
		String leftJoin = "";
		String sql;
		String dataType_CN = ZHConverter.convert(req.getRequest().getDataType(),ZHConverter.SIMPLIFIED);
		String dataType_TW = ZHConverter.convert(req.getRequest().getDataType(),ZHConverter.TRADITIONAL);
		
		try {
			if (Check.Null(jobName)) {
				sql =" select JOB_NAME from job_quartz where job_discretion='"+dataType+"' "
						+ " OR job_discretion='"+dataType_TW+"' OR job_discretion='"+dataType_CN+"' " ;
				List<Map<String, Object>> getQData_JOB_NAME = this.doQueryData(sql, null);
				if (getQData_JOB_NAME !=null && !getQData_JOB_NAME.isEmpty()) {
					jobName=getQData_JOB_NAME.get(0).get("JOB_NAME").toString();
				} else {
					errorMessage.append( "在job_quartz表中未查询到"+dataType+"对应的JOB_NAME");
					return true;
				}
			}
			
			switch(jobName){
				case "RequisitionCreate" :
					serviceName="requisition.create";
					leftJoin=" LEFT JOIN DCP_porder c on a.EID=c.EID and a.organizationno=c.SHOPID and a.docno=c.porderno ";
					break;
				case "DifferenceCreate" :
					serviceName="difference.create";
					leftJoin=" LEFT JOIN DCP_difference c on a.EID=c.EID and a.organizationno=c.SHOPID and a.docno=c.differenceno ";
					break;
				case "ReturnCreate" :
					serviceName="return.create";
					leftJoin = " LEFT JOIN DCP_stockout c on a.EID=c.EID and a.organizationno=c.SHOPID and a.docno=c.stockoutno ";
					break;
				case "TransferCreate" :
					serviceName="transfer.create";
					leftJoin = " LEFT JOIN DCP_stockout c on a.EID=c.EID and a.organizationno=c.SHOPID and a.docno=c.stockoutno " ;
					break;
				case "TransferUpdate" :
					serviceName="transfer.update";
					leftJoin = " LEFT JOIN DCP_stockin c on a.EID=c.EID and a.organizationno=c.SHOPID and a.docno=c.stockinno  ";
					break;
				case "IntegrateCountingCreate" :
					serviceName="integrate.counting.create";
					leftJoin=" LEFT JOIN DCP_stocktake c on a.EID=c.EID and a.organizationno=c.SHOPID and a.docno=c.stocktakeno ";
					break;
				case "CompletionProcess" :
					serviceName="completion.process";
					leftJoin=" LEFT JOIN DCP_pstockin c on a.EID=c.EID and a.organizationno=c.SHOPID and a.docno=c.pstockinno ";
					break;
				case "DisassemblyProcess" :
					serviceName="disassembly.process";
					leftJoin=" LEFT JOIN DCP_pstockin c on a.EID=c.EID and a.organizationno=c.SHOPID and a.docno=c.pstockinno ";
					break;
				case "InventoryAdjustCreateLoss" :
					serviceName="inventory.adjust.create";
					leftJoin=" INNER JOIN DCP_lstockout c on a.EID=c.EID and a.organizationno=c.SHOPID and a.docno=c.lstockoutno ";
					break;
				case "InventoryAdjustCreateOtherIn" :
					serviceName="inventory.adjust.create";
					leftJoin = " INNER JOIN DCP_stockin c on a.EID=c.EID and a.organizationno=c.SHOPID and a.docno=c.stockinno ";
					break;
				case "InventoryAdjustCreateOtherOut" :
					serviceName="inventory.adjust.create";
					leftJoin = " INNER JOIN DCP_stockout c on a.EID=c.EID and a.organizationno=c.SHOPID and a.docno=c.stockoutno " ;
					break;
				case "ReceiptUpdate" :
					serviceName="receipt.update";
					leftJoin=" LEFT JOIN DCP_sstockin c on a.EID=c.EID and a.organizationno=c.SHOPID and a.docno=c.sstockinno ";
					break;
				case "PurchaseReturnCreate" :
					serviceName="purchase.return.create";
					leftJoin=" LEFT JOIN DCP_sstockout c on a.EID=c.EID and a.organizationno=c.SHOPID and a.docno=c.sstockoutno ";
					break;
				case "FeeCreate" :
					serviceName="fee.create";
					leftJoin=" LEFT JOIN DCP_bfee c on a.EID=c.EID and a.organizationno=c.SHOPID and a.docno=c.bfeeno ";
					break;
				case "ReceiptEcsflg" :
					serviceName="receipt.ecsflg";
					leftJoin=" LEFT JOIN DCP_receiving c on a.EID=c.EID and a.organizationno=c.SHOPID and a.docno=c.receivingno ";
					break;
				case "HolidayShoporderCreate_V3" :
					serviceName="holidayorder.create";
					leftJoin=" LEFT JOIN DCP_ORDER c on a.EID=c.EID and a.organizationno=c.SHOP and a.docno=c.orderno ";
					break;
				case "OrderPayRefundCreate_V3" :
					serviceName="orderpayrefund.create";
					leftJoin=" LEFT JOIN DCP_ORDER_PAY c on a.EID=c.EID and a.organizationno=c.SHOPID and (a.docno=c.BILLNO or a.docno=c.SOURCEBILLNO) ";
					break;
			}
			
			//WS接口inventory.adjust.create包含了三个JOB，上述JOB异常查询时只能用INNER JOIN，下面这段是删除有异常日志但是单据不存在的
			if (serviceName.equals("inventory.adjust.create")) {
				sql =" select a.EID,a.organizationno,a.docno from DCP_wslog  a "
						+" left  JOIN DCP_lstockout b on a.EID=b.EID and a.organizationno=b.SHOPID and a.docno=b.lstockoutno "
						+" left  JOIN DCP_stockin   c on a.EID=c.EID and a.organizationno=c.SHOPID and a.docno=c.stockinno and c.Doc_Type='3' "
						+" left  JOIN DCP_stockout  d on a.EID=d.EID and a.organizationno=d.SHOPID and a.docno=d.stockoutno and d.Doc_Type='3' "
						+" where a.EID='"+eId+"' and a.service_name='inventory.adjust.create' and b.lstockoutno is null and c.stockinno is null and d.stockoutno is null " ;
				List<Map<String, Object>> getQData=this.doQueryData(sql, null);
				for (Map<String, Object> oneData :getQData  ) {
					String errorShopNO = oneData.get("ORGANIZATIONNO").toString();
					String docNO = oneData.get("DOCNO").toString();
					InsertWSLOG.delete_WSLOG(eId, errorShopNO,"1",docNO);
				}
			}
			
			sql = " select * from ( "
					+ " select ROWNUM rn, a.organizationno,a.docno,b.org_name,a.error_msg,a.request_XML,a.response_XML, "
					+ " c.process_status,a.modify_date,a.modify_time,a.create_date,a.create_time  from DCP_wslog a "
					+ " left join DCP_ORG_lang b "
					+ " on a.EID=b.EID and a.organizationno=b.organizationno and b.status='100' "
					+ " and b.lang_type='"+langType+"' "
					+ leftJoin   //查询所有在WSLOG未删除但是单据已传输完成的，后面代码会有删除
					+ " where a.process_status='N' and a.service_type='1' and a.service_name='"+serviceName+"' and a.EID='"+eId+"' "
					+ " ) where rn<=100 ";
			
			List<Map<String, Object>> getQData=this.doQueryData(sql, null);
			
			int i =1 ;
			for (Map<String, Object> oneData :getQData) {
				level1Elm_WsError oneLv1 = new DCP_MainErrorInfoQueryRes().new  level1Elm_WsError();
				String errorShopNO = oneData.get("ORGANIZATIONNO").toString();
				String docNO = oneData.get("DOCNO").toString();
				String errorMsg = oneData.get("ERROR_MSG").toString();
				String errorShopName = oneData.get("ORG_NAME").toString();
				String modifyDate=  oneData.get("MODIFY_DATE").toString();
				String modifyTime=  oneData.get("MODIFY_TIME").toString();
				String createDate=  oneData.get("CREATE_DATE").toString();
				String createTime=  oneData.get("CREATE_TIME").toString();
				
				if (Check.Null(modifyDate)) {
					modifyDate=createDate ;
					modifyTime=createTime ;
				}
				
				//删除WSLOG日志（单据传输标志已完成） BY JZMA 20190722
				String process_status = oneData.get("PROCESS_STATUS").toString();
				if (Check.Null(process_status) || process_status.equals("Y")|| process_status.equals("E")) {
					InsertWSLOG.delete_WSLOG(eId, errorShopNO,"1",docNO);
				} else {
					String requestXML="" ;
					try {
						if  (oneData.get("REQUEST_XML")!=null && !Check.Null(oneData.get("REQUEST_XML").toString())) {
							requestXML=oneData.get("REQUEST_XML").toString();
							//java.sql.Clob clob = oneData.get("REQUEST_XML");
							//requestXML = clob.getSubString((long)1,(int)clob.length());
							//														Reader inStream = null;
							//														inStream = clob.getCharacterStream();
							//														char[] c = new char[(int) clob.length()];
							//														inStream.read(c);
							//														requestXML = new String(c);
							//														inStream.close();
						}
					} catch(Exception e) {
						requestXML="" ;
					}
					
					String responseXML="" ;
					try {
						if  (oneData.get("RESPONSE_XML")!=null && !Check.Null(oneData.get("RESPONSE_XML").toString())) {
							responseXML =oneData.get("RESPONSE_XML").toString();
							//java.sql.Clob clob = (Clob) oneData.get("RESPONSE_XML");
							//responseXML=clob.getSubString((long)1,(int)clob.length());
							//							Reader inStream = null;
							//							inStream = clob.getCharacterStream();
							//							char[] c = new char[(int) clob.length()];
							//							inStream.read(c);
							//							responseXML = new String(c);
							//							inStream.close();
						}
					} catch(Exception e) {
						responseXML="" ;
					}
					
					
					oneLv1.setItem(String.valueOf(i));
					oneLv1.setShopId(errorShopNO);
					oneLv1.setShopName(errorShopName);
					oneLv1.setDocNo(docNO);
					oneLv1.setErrorMsg(errorMsg);
					oneLv1.setRequestXML(requestXML);
					oneLv1.setResponseXML(responseXML);
					oneLv1.setModifyDate(modifyDate);
					oneLv1.setModifyTime(modifyTime);
					wsError.add(oneLv1);
					i++;
				}
			}
			
		} catch (Exception e) {
			errorMessage.append(e.getMessage());
			return true;
		}
		return false;
		
	}
	
	
}
