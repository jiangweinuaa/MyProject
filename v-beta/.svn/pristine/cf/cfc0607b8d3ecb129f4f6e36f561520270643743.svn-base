package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.dsc.spos.json.cust.req.DCP_StockOutQueryReq;
import com.dsc.spos.json.cust.req.DCP_StockOutQueryReq.levelElm;
import com.dsc.spos.json.cust.res.DCP_StockOutQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 服務函數：StockOutGet
 *    說明：出货单查询
 * 服务说明：出货单查询
 * @author panjing 
 * @since  2016-09-20
 */
public class DCP_StockOutQuery extends SPosBasicService<DCP_StockOutQueryReq, DCP_StockOutQueryRes>
{
	@Override
	protected boolean isVerifyFail(DCP_StockOutQueryReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		levelElm request = req.getRequest();
		String beginDate = request.getBeginDate();
		String endDate = request.getEndDate();
		String[] docType= request.getDocType();

		if (Check.Null(beginDate)) {
			errMsg.append("开始日期不可为空值, ");
			isFail = true;
		} 
		if (Check.Null(endDate)) {
			errMsg.append("截止日期不可为空值, ");
			isFail = true;
		} 
		if (docType==null||docType.length<=0) {
			errMsg.append("单据类型不可为空值, ");
			isFail = true;
		} 				

		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_StockOutQueryReq> getRequestType() {
		return new TypeToken<DCP_StockOutQueryReq>(){};
	}

	@Override
	protected DCP_StockOutQueryRes getResponseType() {
		return new DCP_StockOutQueryRes();
	}	

	@Override
	protected DCP_StockOutQueryRes processJson(DCP_StockOutQueryReq req) throws Exception {

		//查詢資料
		DCP_StockOutQueryRes res = this.getResponse();
		int totalRecords;								//总笔数
		int totalPages;									//总页数

		//try
		//{
		String sql = this.getQuerySql(req);				  												//查询总笔数
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		res.setDatas(new ArrayList<DCP_StockOutQueryRes.level1Elm>());

        String isBatchManager = PosPub.getPARA_SMS(dao, req.geteId(), "", "IS_BatchNo");
		if (getQData != null && getQData.isEmpty() == false) // 有資料，取得詳細內容
		{
			String num = getQData.get(0).get("NUM").toString();
			totalRecords=Integer.parseInt(num);
			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
            StringBuffer sJoinNo=new StringBuffer("");
			for (Map<String, Object> oneData : getQData) {
				DCP_StockOutQueryRes.level1Elm oneLv1 = res.new level1Elm();

				// 取得第一層資料庫搜尋結果
				String shopId = oneData.get("SHOPID").toString();
				String stockOutNO = oneData.get("STOCKOUTNO").toString();
                sJoinNo.append(stockOutNO+",");
				String bDate = oneData.get("BDATE").toString();
				String processERPNo = oneData.get("PROCESSERPNO").toString();
				String memo = oneData.get("MEMO").toString();
				String status = oneData.get("STATUS").toString();
				String docType = oneData.get("DOCTYPE").toString();
				String transferShop=oneData.get("TRANSFERSHOP").toString();
				String transferShopName=oneData.get("TRANSFERSHOPNAME").toString();
				String oType = oneData.get("OTYPE").toString();
				String ofNO = oneData.get("OFNO").toString();
				String bsNO = oneData.get("BSNO").toString();
				String bsName = oneData.get("BSNAME").toString();
				String warehouse = oneData.get("WAREHOUSE").toString();
				String warehouseName = oneData.get("WAREHOUSENAME").toString();
				String transferWarehouse = oneData.get("TRANSFERWAREHOUSE").toString();
				String transferWarehouseName = oneData.get("TRANSFERWAREHOUSENAME").toString();
				String loadDocType = oneData.get("LOADDOCTYPE").toString();
				String loadDocNO = oneData.get("LOADDOCNO").toString();
				String createBy = oneData.get("CREATEBY").toString();
				String createDate = oneData.get("CREATEDATE").toString();
				String createTime = oneData.get("CREATETIME").toString();
				String createByName = oneData.get("CREATEBYNAME").toString();
				String confirmBy = oneData.get("CONFIRMBY").toString();
				String confirmDate = oneData.get("CONFIRMDATE").toString();
				String confirmTime = oneData.get("CONFIRMTIME").toString();
				String confirmByName = oneData.get("CONFIRMBYNAME").toString();
				String accountBy = oneData.get("ACCOUNTBY").toString();
				String accountDate = oneData.get("ACCOUNTDATE").toString();
				String accountTime = oneData.get("ACCOUNTTIME").toString();
				String accountByName = oneData.get("ACCOUNTBYNAME").toString();
				String cancelBy = oneData.get("CANCELBY").toString();
				String cancelDate = oneData.get("CANCELDATE").toString();
				String cancelTime = oneData.get("CANCELTIME").toString();
				String cancelByName = oneData.get("CANCELBYNAME").toString();
				String modifyBy = oneData.get("MODIFYBY").toString();
				String modifyDate = oneData.get("MODIFYDATE").toString();
				String modifyTime = oneData.get("MODIFYTIME").toString();
				String modifyByName = oneData.get("MODIFYBYNAME").toString();
				String submitBy = oneData.get("SUBMITBY").toString();
				String submitDate = oneData.get("SUBMITDATE").toString();
				String submitTime = oneData.get("SUBMITTIME").toString();
				String submitByName = oneData.get("SUBMITBYNAME").toString();
				String receiptOrg = oneData.get("RECEIPTORG").toString();
				String receiptOrgName = oneData.get("ORGNAME").toString();
				String totPqty = oneData.get("TOTPQTY").toString();
				String totAmt = oneData.get("TOTAMT").toString();
				String totCqty = oneData.get("TOTCQTY").toString();
				String totDistriAmt = oneData.get("TOT_DISTRIAMT").toString();
				String diffStatus = oneData.get("DIFFSTATUS").toString();
				String deliveryNO = oneData.get("DELIVERYNO").toString();
				String pTemplateNo = oneData.get("PTEMPLATENO").toString();
				String pTemplateName = oneData.get("PTEMPLATENAME").toString();
				String sourceMenu = oneData.get("SOURCEMENU").toString();
				String stockoutno_origin = oneData.get("STOCKOUTNO_ORIGIN").toString();
				String stockoutno_refund = oneData.get("STOCKOUTNO_REFUND").toString();

				String employeeid = oneData.get("EMPLOYEEID").toString();
				String employeename = oneData.get("EMPLOYEENAME").toString();
				String departid = oneData.get("DEPARTID").toString();
				String departname = oneData.get("DEPARTNAME").toString();
				String receiptdate = oneData.get("RECEIPTDATE").toString();
				String packingno = oneData.get("PACKINGNO").toString();
				String invwarehouse = oneData.get("INVWAREHOUSE").toString();
				String invwarehousename = oneData.get("INVWAREHOUSENAME").toString();
				String islocation = oneData.get("ISLOCATION").toString();
                String deliverydate = oneData.get("DELIVERYDATE").toString();
                String ootype = oneData.get("OOTYPE").toString();
                String oofno = oneData.get("OOFNO").toString();
                String istraninconfirm = oneData.get("ISTRANINCONFIRM").toString();
                //String stockInNo = oneData.get("STOCKINNO").toString();

                String corp = oneData.get("CORP").toString();
                String receiptCorp = oneData.get("RECEIPTCORP").toString();
                oneLv1.setCorp(corp);
                oneLv1.setReceiptCorp(receiptCorp);

                // 處理調整回傳值；
				oneLv1.setShopId(shopId);
				oneLv1.setStockOutNo(stockOutNO);
				oneLv1.setBDate(bDate);
				oneLv1.setProcessERPNo(processERPNo);
				oneLv1.setMemo(memo);
				oneLv1.setStatus(status);
				oneLv1.setDocType(docType);
				oneLv1.setBsNo(bsNO);
				oneLv1.setBsName(bsName);
				oneLv1.setWarehouse(warehouse);
				oneLv1.setWarehouseName(warehouseName);
				oneLv1.setTransferWarehouse(transferWarehouse);
				oneLv1.setTransferWarehouseName(transferWarehouseName);
				oneLv1.setTransferShop(transferShop);
				oneLv1.setTransferShopName(transferShopName);
				oneLv1.setOType(oType);
				oneLv1.setOfNo(ofNO);
				oneLv1.setLoadDocType(loadDocType);
				oneLv1.setLoadDocNo(loadDocNO);
				oneLv1.setCreateBy(createBy);
				oneLv1.setCreateDate(createDate);
				oneLv1.setCreateTime(createTime);
				oneLv1.setCreateByName(createByName);
				oneLv1.setAccountBy(accountBy);
				oneLv1.setAccountDate(accountDate);
				oneLv1.setAccountTime(accountTime);
				oneLv1.setAccountByName(accountByName);
				oneLv1.setReceiptOrg(receiptOrg);
				oneLv1.setReceiptOrgName(receiptOrgName);
				oneLv1.setConfirmBy(confirmBy);
				oneLv1.setConfirmDate(confirmDate);
				oneLv1.setConfirmTime(confirmTime);
				oneLv1.setConfirmByName(confirmByName);
				oneLv1.setAccountBy(accountBy);
				oneLv1.setAccountDate(accountDate);
				oneLv1.setAccountTime(accountTime);
				oneLv1.setAccountByName(accountByName);
				oneLv1.setCancelBy(cancelBy);
				oneLv1.setCancelDate(cancelDate);
				oneLv1.setCancelTime(cancelTime);
				oneLv1.setCancelByName(cancelByName);
				oneLv1.setModifyBy(modifyBy);
				oneLv1.setModifyDate(modifyDate);
				oneLv1.setModifyTime(modifyTime);
				oneLv1.setModifyByName(modifyByName);
				oneLv1.setSubmitBy(submitBy);
				oneLv1.setSubmitDate(submitDate);
				oneLv1.setSubmitTime(submitTime);
				oneLv1.setSubmitByName(submitByName);
				oneLv1.setTotPqty(totPqty);
				oneLv1.setTotAmt(totAmt);
				oneLv1.setTotCqty(totCqty);
				oneLv1.setTotDistriAmt(totDistriAmt);
				oneLv1.setDeliveryNo(deliveryNO);
				oneLv1.setDiffStatus(diffStatus);
				oneLv1.setUpdate_time(oneData.get("UPDATE_TIME").toString());
				oneLv1.setProcess_status(oneData.get("PROCESS_STATUS").toString());
				oneLv1.setPTemplateNo(pTemplateNo);
				oneLv1.setPTemplateName(pTemplateName);
				oneLv1.setSourceMenu(sourceMenu);
				oneLv1.setStockOutNo_origin(stockoutno_origin);
				oneLv1.setStockOutNo_refund(stockoutno_refund);
					
				//【ID1036371】【浙江意诺V9203】‘个案评估’ 退货出库，驳回增加原因，并可以让门店知道这个驳回原因，需要有报表查询----中台服务端 by jinzma 20231010
				oneLv1.setRejectReason(oneData.get("REJECTREASON").toString());
     
				//【ID1037888】[红房子3.0]订单和调拨单记录配送员，配送员电话，统计出来后用于计算配送人员工资，之前易成用的是（易成用的要货发货单功能）--服务端 by jinzma 20231215
				oneLv1.setDeliveryBy(oneData.get("DELIVERYBY").toString());
				oneLv1.setDeliveryName(oneData.get("DELIVERYNAME").toString());
				oneLv1.setDeliveryTel(oneData.get("DELIVERYTEL").toString());
				oneLv1.setEmployeeId(employeeid);
				oneLv1.setEmployeeName(employeename);
				oneLv1.setDepartId(departid);
				oneLv1.setDepartName(departname);
				oneLv1.setReceiptDate(receiptdate);
				oneLv1.setPackingNo(packingno);
				oneLv1.setInvWarehouse(invwarehouse);
				oneLv1.setInvWarehouseName(invwarehousename);
				oneLv1.setIsLocation(islocation);
				oneLv1.setIsBatchManage(isBatchManager);
                oneLv1.setDeliveryDate(deliverydate);
                oneLv1.setOoType(ootype);
                oneLv1.setOofNo(oofno);
                oneLv1.setIsTranInConfirm(istraninconfirm);
                oneLv1.setStockInNo(new ArrayList<>());
					
				res.getDatas().add(oneLv1);
			}

            Map<String, String> mapStockoutNo=new HashMap<String, String>();
            mapStockoutNo.put("STOCKOUTNO", sJoinNo.toString());
            MyCommon cm=new MyCommon();
            String withasSql_stockoutno="";
            withasSql_stockoutno=cm.getFormatSourceMultiColWith(mapStockoutNo);
            mapStockoutNo=null;

            String stockInSql="with p AS ( " + withasSql_stockoutno + ") "+
                    " select a.stockinno,a.STOCKINNO_ORIGIN,p.stockoutno  from dcp_stockin a " +
                    " inner join p on p.stockoutno=a.oofno " +
                    " where a.eid='"+req.geteId()+"' ";
            List<Map<String, Object>> stockInList = this.doQueryData(stockInSql, null);

            if(stockInList.size()>0){
                res.getDatas().forEach(x->{
                    String stockOutNo = x.getStockOutNo();
                    List<Map<String, Object>> filterRows = stockInList.stream().filter(z -> z.get("STOCKOUTNO").toString().equals(stockOutNo)).distinct().collect(Collectors.toList());
                    if(filterRows.size()>0){
                        for (Map<String, Object> singleRows : filterRows){
                            DCP_StockOutQueryRes.StockInInfo stockInInfo = res.new StockInInfo();
                            stockInInfo.setStockInNo(singleRows.get("STOCKINNO").toString());
                            stockInInfo.setStockInNo_origin(singleRows.get("STOCKINNO_ORIGIN").toString());
                            x.getStockInNo().add(stockInInfo);
                        }

                    }
                });
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

	//	}
		//catch (Exception e)
		//{
		//	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		//}
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		//調整查出來的資料
	}

	@Override
	protected String getQuerySql(DCP_StockOutQueryReq req) throws Exception {
		levelElm request = req.getRequest();
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		String langType = req.getLangType();
		String status = request.getStatus();
		String keyTxt = request.getKeyTxt();		
		String beginDate = request.getBeginDate();
		String endDate = request.getEndDate();		
		int pageSize = req.getPageSize();
		String[] docType = request.getDocType();
		String docTypes = getString(docType); 
		String sourceMenu = request.getSourceMenu();
		//計算起啟位置
		int startRow = ((req.getPageNumber() - 1) * req.getPageSize());
		startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
		startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料

		sqlbuf.append(""
				+ " select num,rn,stockOutNO,SHOPID,EID,bsNO,bsName,receiptOrg,orgName,bDate,memo,status,docType,oType,ofNO,loadDocType,"
				+ " loadDocNO,"
				+ " createBy,createDate,createTime,confirmBy,confirmDate,confirmTime,"
				+ " accountBy,accountDate,accountTime,  cancelBy,cancelDate,cancelTime,"
				+ " submitBY,submitDate,submitTime,ModifyBy,modifyDate,modifyTime  ,   "
				+ " transferShop,createByName,transferShopName,totPqty,totAmt,totCqty,TOT_DISTRIAMT,deliveryNO,diffStatus,"
				+ " warehouse,warehouseName,transferWarehouse,transferWarehouseName,UPDATE_TIME,PROCESS_STATUS, "
				+ " modifyByName,cancelByName,ConfirmByName,  "
				+ " submitByName,accountByName,processERPNO,pTemplateNo,pTemplateName,sourcemenu,STOCKOUTNO_ORIGIN,STOCKOUTNO_REFUND,rejectreason,"
				+ " deliveryby,deliveryname,deliverytel,employeeId ,employeeName ,departId ,departName ,receiptDate ,packingNo ,invWarehouse ,invWarehouseName,isLocation,deliveryDate,ooType,oofNo,isTranInConfirm,corp,receiptcorp "
				+ " from("
				+ " SELECT count(*) over() as num,ROWNUM rn,stockOutNO,SHOPID,bsNO,bsName,receiptOrG,orgName,EID,bDate,memo,status,"
				+ " docType,oType ,ofNO,loadDocType,loadDocNO,"
				+ " createBy,createDate,createTime,confirmBy,confirmDate,confirmTime,"
				+ " accountBy,accountDate,accountTime, "
				+ " cancelBy,cancelDate,cancelTime,"
				+ " submitBY, submitDate, submitTime , "
				+ " ModifyBy , modifyDate , modifyTime  ,"
				+ " modifyByName ,cancelByName , ConfirmByName ,  "
				+ " submitByName, accountByName , processERPNO, "
				+ " transferShop,createByName,transferShopName,totPqty,totAmt,totCqty,TOT_DISTRIAMT,deliveryNO,diffStatus,"
				+ " warehouse,warehouseName,transferWarehouse,transferWarehouseName,UPDATE_TIME,PROCESS_STATUS,"
				+ " pTemplateNo ,pTemplateName,sourcemenu,STOCKOUTNO_ORIGIN,STOCKOUTNO_REFUND,rejectreason,"
				+ " deliveryby,deliveryname,deliverytel,employeeId ,employeeName ,departId ,departName ,receiptDate ,packingNo ,invWarehouse ,invWarehouseName,isLocation,deliveryDate,ooType,oofNo,isTranInConfirm,corp,receiptcorp "
				+ " from ("
				+ " SELECT A.STOCKOUTNO as stockOutNO,A.BSNO,L.REASON_NAME as bsName,a.receipt_org as receiptOrg,"
				+ " z.org_name as orgName,A.SHOPID as SHOPID,A.EID as EID, A.BDATE as bDate,A.MEMO as memo,"
				+ " A.STATUS as status,A.DOC_TYPE as docType, "
				+ " A.OTYPE as oType,A.OFNO as ofNO,A.LOAD_DOCTYPE as loadDocType,A.LOAD_DOCNO as loadDocNO,A.CREATEBY as createBy, "
				+ "	A.TRANSFER_SHOP as transferShop, "
				+ " B.op_name as createByName,C.SHOPNAME as transferShopName,A.TOT_PQTY as totPqty,A.TOT_AMT as totAmt,"
				+ " A.TOT_CQTY as totCqty,A.TOT_DISTRIAMT,A.DELIVERY_NO as deliveryNO,I.status as diffStatus,"
				+ " A.warehouse as warehouse,w.warehouse_Name as warehouseName,A.transfer_Warehouse as transferWarehouse,"
				+ " t.warehouse_Name as transferWarehouseName,A.UPDATE_TIME,A.PROCESS_STATUS, "
				+ " a.pTemplateNo , p.pTemplate_Name as pTemplateName, "
				+ " A.create_Date as createDate, A.create_Time as createTime,"
				+ " a.ModifyBy , a.modify_Date as modifyDate ,  a.modify_Time as modifyTime , "
				+ " a.confirmBy, a.confirm_Date as confirmDate , a.Confirm_Time as ConfirmTime,   "
				+ " a.cancelBy , a.cancel_Date as cancelDate , a.cancel_Time as cancelTime , "
				+ " a.accountBY ,a.account_Date as accountDate, a.account_Time as accountTime,"
				+ " a.submitBy, a.submit_Date as submitDate, a.submit_Time as submitTime , "
				+ " f1.op_name as modifyByName ,f2.op_name as cancelByName , f3.op_name as ConfirmByName ,  "
				+ " f4.op_name as submitByName,  f5.op_name as accountByName, "
				+ " a.process_erp_no as processERPNo,a.sourcemenu,A.STOCKOUTNO_ORIGIN,A.STOCKOUTNO_REFUND,a.rejectreason,"
				+ " a.deliveryby,dm.opname as deliveryname,dm.phone as deliverytel,a.employeeid,em0.name as employeename,a.departid,dd0.departname," +
                " a.RECEIPTDATE,a.packingNo ,a.INVWAREHOUSE,iwl.warehouse_name as invWarehouseName,w1.ISLOCATION ,a.deliveryDate,a.ooType,a.oofNo,a.isTranInConfirm,a.corp,a.receiptcorp  "//,ds.stockinno
				+ " FROM DCP_STOCKOUT A "
				+ " LEFT JOIN platform_staffs_lang B ON A.EID=B.EID AND A.CREATEBY=B.opno and b.lang_type='"+req.getLangType()+"' "
				+ " LEFT JOIN platform_staffs_lang f1 ON A .EID = f1.EID AND A .modifyBy = f1.opno and f1.lang_type='"+req.getLangType()+"' "
				+ " LEFT JOIN platform_staffs_lang f2 ON A .EID = f2.EID AND A .cancelby = f2.opno and f2.lang_type='"+req.getLangType()+"' "
				+ " LEFT JOIN platform_staffs_lang f3 ON A .EID = f3.EID AND A .confirmby = f3.opno and f3.lang_type='"+req.getLangType()+"' "
				+ " LEFT JOIN platform_staffs_lang f4 ON A .EID = f4.EID AND A .submitby = f4.opno and f4.lang_type='"+req.getLangType()+"' "
				+ " LEFT JOIN platform_staffs_lang f5 ON A .EID = f5.EID AND A .accountby = f5.opno and f5.lang_type='"+req.getLangType()+"' "
				+ " LEFT JOIN DCP_PTEMPLATE p ON a.EID = p.EID and a.ptemplateNO = p.ptemplateNo "
				+ " left join dcp_deliveryman dm on a.eid=dm.eid and a.deliveryby=dm.opno" +
                " left join dcp_employee em0 on em0.eid=a.eid and em0.employeeno=a.employeeid  " +
                " left join DCP_DEPARTMENT_LANG dd0 on dd0.eid=a.eid and dd0.departno=a.departid and dd0.lang_type='"+langType+"' " +
                " left join dcp_warehouse iw on iw.eid=a.eid and a.INVWAREHOUSE=iw.WAREHOUSE and iw.organizationno=a.ORGANIZATIONNO " +
                " left join DCP_WAREHOUSE_LANG iwl on iwl.eid=a.eid and iwl.WAREHOUSE=a.INVWAREHOUSE and iwl.organizationno=a.ORGANIZATIONNO and iwl.lang_type='"+langType+"'" +
                //" left join dcp_stockin ds on ds.eid=a.eid and ds.oofno=a.stockoutno " +
                "");
		
		

		// 关键字查询时，输入单号会查不到单据   BY JZMA 注释  20200724 	
		//		if (keyTxt != null && keyTxt.length()!=0) 
		//		{
		//			sqlbuf.append(""
		//					+ " INNER JOIN ");
		//		} else
		//		{
		//			sqlbuf.append(""
		//					+ " LEFT JOIN ");
		//		}

		sqlbuf.append(" LEFT JOIN ");
		sqlbuf.append(""
				+ "("
				+ "SELECT A.EID,A.ORGANIZATIONNO SHOPID,B.ORG_NAME SHOPNAME,B.LANG_TYPE FROM DCP_ORG A "
				+ "LEFT JOIN DCP_ORG_LANG B ON A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO AND B.LANG_TYPE='"+req.getLangType()+"' "
				+ "WHERE A.EID='"+req.geteId()+"'  AND A.status='100' "//AND A.ORG_FORM='2'
				+ ") C ON A.EID=C.EID AND A.TRANSFER_SHOP=C.SHOPID AND C.LANG_TYPE = '"+ langType +"'"				
				+ " LEFT JOIN DCP_DIFFERENCE I ON A.EID=I.EID AND A.STOCKOUTNO=I.LOAD_DOCNO AND A.TRANSFER_SHOP=I.SHOPID "
				+ " left join DCP_ORG_lang z on a.EID=z.EID and a.receipt_org=z.organizationno and z.lang_type='"+ langType +"'"
				+ " left join DCP_WAREHOUSE_lang w on a.warehouse=w.warehouse and a.EID=w.EID and a.OrganizationNO=w.OrganizationNO and w.lang_type='"+ langType +"'"
        + " left join DCP_WAREHOUSE w1 on a.warehouse=w1.warehouse and a.EID=w1.EID and a.OrganizationNO=w1.OrganizationNO " );

		if(docTypes.contains("4"))
		{
			sqlbuf.append(""
					+ " left join DCP_WAREHOUSE_lang t on a.transfer_warehouse=t.warehouse and a.EID=t.EID and a.OrganizationNO=t.OrganizationNO and t.lang_type='"+ langType +"'"
					+ " left join DCP_REASON_LANG L ON A.EID=L.EID AND A.BSNO=L.BSNO and L.lang_type= '" + langType + "'" );
		}
		else{
			sqlbuf.append(""
					+ " left join DCP_WAREHOUSE_lang t on a.warehouse=t.warehouse and a.EID=t.EID and a.OrganizationNO=t.OrganizationNO and t.lang_type='"+ langType +"'"
					+ " left join DCP_REASON_LANG L ON A.EID=L.EID AND A.BSNO=L.BSNO and L.lang_type= '" + langType + "'" );
		}

		//docType(0:退货出库backOut 1:调拨出库stockOut 3:其他出库otherOut)
		if(docTypes.contains("0") || docTypes.contains("1")){
			sqlbuf.append(" and L.bstype = '2' ");
		}
		else if(docTypes.contains("3")){
			sqlbuf.append(" and (L.bstype = '4' or l.bstype = '12' or l.bstype = '13' )");  // 12试吃出库 13赠送出库 BY JZMA 2020/10/19 add
		}

		sqlbuf.append(" WHERE A.DOC_TYPE in ('"+ docTypes+"') AND A.SHOPID ='"+req.getShopId()+"' "
				+ "AND A.OrganizationNO ='"+req.getShopId()+"' AND A.EID = '"+req.geteId()+"' "
				+ " AND a.BDate BETWEEN "+beginDate+" AND "+endDate+" ");

		if (status != null&&status.length()>0) {
			sqlbuf.append("  AND a.status = '"+status+"' ");
		}		
		if (!Check.Null(sourceMenu) && docTypes.contains("3")){
			sqlbuf.append("  AND a.sourcemenu = '"+sourceMenu+"' ");
		}

		if (keyTxt != null && keyTxt.length()!=0) { 		
			sqlbuf.append(""
					+ "AND (A.TOT_AMT like '%%"+ keyTxt +"%%'  "
					+ "OR A.TOT_PQTY like '%%"+ keyTxt +"%%'   "
					+ "OR A.STOCKOUTNO like '%%"+ keyTxt +"%%' "
                    + "OR A.OFNO like '%%"+ keyTxt +"%%' "
					+ "OR A.MEMO like '%%"+ keyTxt +"%%' "
					+ "OR A.PROCESS_ERP_NO like '%%"+ keyTxt +"%%'"
					+ "OR C.SHOPNAME like '%%"+ keyTxt +"%%'"
					+ ") "
					);
		}
		sqlbuf.append(" order by bDate DESC,stockOutNO DESC ");
		sqlbuf.append(" ) TBL ");
		sqlbuf.append(") where rn > " + startRow + " AND rn <= " + (startRow+pageSize) );

		sql = sqlbuf.toString();

		return sql;

	}	

	protected String getString(String[] str){
		String str2 = "";
		if (str!=null&&str.length>0)
		{
			for (String s:str){
				str2 = str2 + s + "','";
			}
		}
		if (str2.length()>0){
			str2=str2.substring(0,str2.length()-3);
		}
		return str2;
	}	




}
