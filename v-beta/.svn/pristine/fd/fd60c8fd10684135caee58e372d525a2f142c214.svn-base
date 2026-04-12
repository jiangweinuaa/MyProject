package com.dsc.spos.service.imp.json;



import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.dsc.spos.utils.*;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.json.cust.req.DCP_StockOutNoticeQueryReq;
import com.dsc.spos.json.cust.res.DCP_StockOutNoticeQueryRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.google.gson.reflect.TypeToken;
 

public class DCP_StockOutNoticeQuery extends SPosBasicService<DCP_StockOutNoticeQueryReq, DCP_StockOutNoticeQueryRes > {
	
	Logger logger = LogManager.getLogger(SPosAdvanceService.class);
	public List<DataProcessBean> pData = new ArrayList<DataProcessBean>();
	
	@Override
	protected boolean isVerifyFail(DCP_StockOutNoticeQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_StockOutNoticeQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_StockOutNoticeQueryReq>(){};
	}

	@Override
	protected DCP_StockOutNoticeQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_StockOutNoticeQueryRes();
	}

	@Override
	protected DCP_StockOutNoticeQueryRes processJson(DCP_StockOutNoticeQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_StockOutNoticeQueryRes res = null;
		res = this.getResponse();
		String sql = null;
		//try {
			
			sql = this.getQuerySql(req);
			logger.info(sql);
			List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, null);
	 
			int totalRecords;								//总笔数
			int totalPages;									//总页数
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false) 
			{ 			
				Map<String, Object> total = getQDataDetail.get(0);
				String num = total.get("NUM").toString();
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

			//List<Map<String, Object>> getQLangData = null;
			res.setDatas(new ArrayList<DCP_StockOutNoticeQueryRes.DataDetail>());
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
			{

				MyCommon cm=new MyCommon();
				StringBuffer sJoinBillNo=new StringBuffer("");
				for (Map<String, Object> oneData : getQDataDetail) 
				{
					DCP_StockOutNoticeQueryRes.DataDetail oneLv1 = res.new DataDetail();
 
					oneLv1.setStatus(oneData.get("STATUS").toString());
					oneLv1.setOrgNo(oneData.get("ORGANIZATIONNO").toString());
					oneLv1.setOrgName(oneData.get("ORGNAME").toString());
					oneLv1.setBillNo(oneData.get("BILLNO").toString());
					oneLv1.setSourceBillNo(oneData.get("SOURCEBILLNO").toString());
					oneLv1.setDeliverOrgNo(oneData.get("DELIVERORGNO").toString());
					oneLv1.setDeliverOrgName(oneData.get("DELIVERORGNAME").toString());
					oneLv1.setBDate(oneData.get("BDATE").toString());
					oneLv1.setObjectType(oneData.get("OBJECTTYPE").toString());
					oneLv1.setObjectId(oneData.get("OBJECTID").toString());
					oneLv1.setObjectName(oneData.get("OBJECTNAME").toString());
					oneLv1.setPayType(oneData.get("PAYTYPE").toString());
					oneLv1.setPayOrgNo(oneData.get("PAYORGNO").toString());
					oneLv1.setPayOrgName(oneData.get("ORGNAME").toString());
					oneLv1.setBillDateNo(oneData.get("BILLDATENO").toString());
					oneLv1.setBillDateDesc(oneData.get("BILLDATEDESC").toString());
					oneLv1.setPayDateNo(oneData.get("PAYDATENO").toString());
					oneLv1.setPayDateDesc(oneData.get("PAYDATEDESC").toString());
					oneLv1.setInvoiceCode(oneData.get("INVOICECODE").toString());
					oneLv1.setInvoiceName(oneData.get("INVOICENAME").toString());
					oneLv1.setCurrency(oneData.get("CURRENCY").toString());
					oneLv1.setCurrencyName(oneData.get("CURRNAME").toString());
					oneLv1.setReturnType(oneData.get("RETURNTYPE").toString());
					oneLv1.setWareHouse(oneData.get("WAREHOUSE").toString());
					oneLv1.setWareHouseName(oneData.get("WAREHOUSENAME").toString());
					oneLv1.setMemo(oneData.get("MEMO").toString());
					oneLv1.setTotCqty(oneData.get("TOTCQTY").toString());
					oneLv1.setTotPqty(oneData.get("TOTPQTY").toString());
					oneLv1.setTotAmt(oneData.get("TOTAMT").toString());
					oneLv1.setTotPreTaxAmt(oneData.get("TOTPRETAXAMT").toString());
					oneLv1.setTotTaxAmt(oneData.get("TOTTAXAMT").toString());
					oneLv1.setTotStockOutQty(oneData.get("TOTSTOCKOUTQTY").toString());
					//oneLv1.setTotCanStockOutCqty(oneData.get("TOTCANSTOCKOUTCQTY").toString());
					//oneLv1.setTotCanStockOutPqty(oneData.get("TOTCANSTOCKOUTPQTY").toString());
					oneLv1.setTotCanStockOutCqty(oneData.get("TOTCQTY").toString());
					oneLv1.setTotCanStockOutPqty(oneData.get("TOTPQTY").toString());
					oneLv1.setEmployeeID(oneData.get("EMPLOYEEID").toString());
					oneLv1.setEmployeeName(oneData.get("EMPLOYEENAME").toString());
					oneLv1.setDepartID(oneData.get("DEPARTID").toString());
					oneLv1.setDepartName(oneData.get("DEPARTNAME").toString());
					oneLv1.setCreateOpID(oneData.get("CREATEOPID").toString());
					oneLv1.setCreateOpName(oneData.get("CREATEOPNAME").toString());
					oneLv1.setCreateDateTime(oneData.get("CREATETIME").toString());
					oneLv1.setLastModiOpID(oneData.get("LASTMODIOPID").toString());
					oneLv1.setLastModiOpName(oneData.get("LASTMODIOPNAME").toString());
					oneLv1.setLastModiDateTime(oneData.get("LASTMODITIME").toString());
					oneLv1.setConfirmBy(oneData.get("CONFIRMBY").toString());
					oneLv1.setConfirmByName(oneData.get("CONFIRMBYNAME").toString());
					oneLv1.setConfirmDateTime(oneData.get("CONFIRMTIME").toString());
					oneLv1.setCancelBy(oneData.get("CANCELBY").toString());
					oneLv1.setCancelByName(oneData.get("CANCELBYNAME").toString());
					oneLv1.setCancelDateTime(oneData.get("CANCELTIME").toString());
					oneLv1.setOwnOpID(oneData.get("OWNOPID").toString());
					oneLv1.setOwnOpName(oneData.get("OWNOPNAME").toString());
					oneLv1.setOwnDeptID(oneData.get("OWNDEPTID").toString());
					oneLv1.setOwnDeptName(oneData.get("OWNDEPTNAME").toString());
					
					oneLv1.setCreateDeptId(oneData.get("CREATEDEPTID").toString());
					oneLv1.setCreateDeptName(oneData.get("CREATEDEPTNAME").toString());
					oneLv1.setCloseBy(oneData.get("CLOSEBY").toString());
					oneLv1.setCloseByName(oneData.get("CLOSEBYNAME").toString());
					oneLv1.setCloseTime(oneData.get("CLOSETIME").toString());
					oneLv1.setDeliveryDate(oneData.get("DELIVERYDATE").toString());
					oneLv1.setTotRetailAmt(oneData.get("TOTRETAILAMT").toString());
					oneLv1.setRDate(oneData.get("RDATE").toString());

					oneLv1.setIsTranInConfirm(oneData.get("ISTRANINCONFIRM").toString());
					oneLv1.setReceiptWarehouse(oneData.get("RECEIPTWAREHOUSE").toString());
					oneLv1.setReceiptWarehouseName(oneData.get("RECEIPTWAREHOUSENAME").toString());
					oneLv1.setInvWarehouse(oneData.get("INVWAREHOUSE").toString());
					oneLv1.setInvWarehouseName(oneData.get("INVWAREHOUSENAME").toString());
					oneLv1.setTemplateNo(oneData.get("TEMPLATENO").toString());
					oneLv1.setTemplateName(oneData.get("TEMPLATENAME").toString());
					oneLv1.setPayee(oneData.get("PAYEE").toString());
					oneLv1.setPayer(oneData.get("PAYER").toString());
					oneLv1.setPayeeName(oneData.get("PAYEENAME").toString());
					oneLv1.setPayerName(oneData.get("PAYERNAME").toString());

					oneLv1.setCorp(oneData.get("CORP").toString());
					oneLv1.setCorpName(oneData.get("CORPNAME").toString());
					oneLv1.setReceiptCorp(oneData.get("RECEIPTCORP").toString());
					oneLv1.setReceiptCorpName(oneData.get("RECEIPTCORPNAME").toString());
					oneLv1.setDeliveryCorp(oneData.get("DELIVERYCORP").toString());
					oneLv1.setDeliveryCorpName(oneData.get("DELIVERYCORPNAME").toString());

					sJoinBillNo.append(oneLv1.getBillNo()+",");
					res.getDatas().add(oneLv1);
				}

				Map<String, String> mapBill=new HashMap<String, String>();
				mapBill.put("BILLNO", sJoinBillNo.toString());

				String withasSql_BILL="";
				withasSql_BILL=cm.getFormatSourceMultiColWith(mapBill);
				mapBill=null;

				if(!Check.Null(withasSql_BILL)){
					String detailSql = this.getDetailSql(req,withasSql_BILL);
					List<Map<String, Object>> detailList = this.doQueryData(detailSql, null);
					res.getDatas().forEach(x->{
						String billNo = x.getBillNo();
						List<Map<String, Object>> collect = detailList.stream().filter(z -> z.get("BILLNO").toString().equals(billNo)).collect(Collectors.toList());
						int totCanStockOutCqty=0;
						BigDecimal totCanStockOutPqty=BigDecimal.ZERO;
						for (Map<String, Object> oneData : collect){
							BigDecimal pqty=new BigDecimal(oneData.get("PQTY").toString());
							BigDecimal stockoutqty = new BigDecimal(oneData.get("STOCKOUTQTY").toString());

							if(pqty.compareTo(stockoutqty)>0){
								totCanStockOutCqty++;
							}
							BigDecimal subtract = pqty.subtract(stockoutqty);
							totCanStockOutPqty=totCanStockOutPqty.add(subtract);

						}
						x.setTotCanStockOutCqty(String.valueOf(totCanStockOutCqty));
						x.setTotCanStockOutPqty(totCanStockOutPqty.toString());
					});
				}

			}
			
			
		//} catch (Exception e) {
			// TODO Auto-generated catch block
		//	res.setDatas(new ArrayList<DCP_StockOutNoticeQueryRes.DataDetail>());
		//	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());//add by 01029 20240703
		//}
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_StockOutNoticeQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		
		String sql=null;
		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();
		String langType =req.getLangType();
		if(langType==null||langType.isEmpty())
		{
			langType="zh_CN";
		}
		
		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;
		
		String key = null;
		String billType = null;
		String dateType = null;
		String status = null; 
		String beginDate = null;
		String endDate = null;
		String deliverOrgNo = null;
		String searchScope = null;
		String getType=null;
		
		String eId = req.geteId();
		
		if(req.getRequest()!=null)
		{
			key =  req.getRequest().getKeyTxt();
			status = req.getRequest().getStatus();
			billType = req.getRequest().getBillType();
			dateType = req.getRequest().getDateType();
			if(Check.Null(dateType)){
				dateType="bDate";
			}
			beginDate = req.getRequest().getBeginDate();
			endDate = req.getRequest().getEndDate();
			deliverOrgNo = req.getRequest().getDeliverOrgNo();
			searchScope = req.getRequest().getSearchScope();
			getType=req.getRequest().getGetType();
			if (StringUtils.isEmpty(beginDate))
				beginDate ="20241001";
			if (StringUtils.isEmpty(endDate))
				endDate ="20641001";
		}
		
		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append("SELECT * FROM ("
					+ " SELECT COUNT( 1 ) OVER() NUM ,row_number()  over (order by a.bdate desc,a.BILLNO desc ) rn,a.* from " +
						"(select distinct "
					+ "  a.*  ,J.ORG_NAME AS ORGNAME ,J1.ORG_NAME AS DELIVERORGNAME,case when a.objecttype='3' then b1.org_name   else b.SNAME end AS OBJECTNAME "
					+ "  , p.op_NAME as CREATEOPNAME,d.DEPARTNAME as CREATEDEPTNAME1,c.op_NAME as lastModiOpName "
					+ "  ,BD.NAME AS BILLDATEDESC ,PD.NAME AS PAYDATEDESC ,IE.NAME AS INVOICENAME,W.WAREHOUSE_NAME AS WAREHOUSENAME "
					+ "  , p1.NAME as EMPLOYEENAME,d6.DEPARTNAME as DEPARTNAME, p2.op_NAME as   CONFIRMBYNAME, p3.op_NAME as   CANCELBYNAME"
					+ "  , p4.op_NAME as OWNOPNAME,d4.DEPARTNAME as OWNDEPTNAME,cc.name as CURRNAME ,d5.DEPARTNAME as CREATEDEPTNAME , p5.op_NAME as CLOSEBYNAME,w1.warehouse_name as receiptWarehouseName,w2.warehouse_name as invwarehousename,pt.PTEMPLATE_NAME as templatename," +
				" m.sname as payeename,n.sname as payername,j2.org_name as corpName,j3.org_name as receiptCorpName,j4.org_name as deliveryCorpName "
					+ " FROM DCP_STOCKOUTNOTICE a   "
					//+ " left join DCP_STOCKOUTNOTICE_DETAIL  detail on a.eid=detail.eid and detail.BILLNO=a.BILLNO "
					+ " left join DCP_bizpartner  b on a.eid=b.eid and a.OBJECTID=b.BIZPARTNERNO   "
				    + " left join dcp_org_lang b1 on b1.eid=a.eid and b1.organizationno=a.objectid and a.objecttype='3' "
					+ " left join PLATFORM_STAFFS_LANG  p on a.eid=p.eid and p.opno=a.CREATEOPID and p.lang_type='"+langType+"'"
					+ " left join PLATFORM_STAFFS_LANG  c on a.eid=c.eid and c.opno=a.LASTMODIOPID and c.lang_type='"+langType+"'"
					+"  left join DCP_DEPARTMENT_LANG d  on p.eid=d.eid and a.CREATEDEPTID=d.DEPARTNO and d.lang_type='"+langType+"' "
					+"   LEFT JOIN DCP_ORG_LANG j on j.EID=a.EID AND j.ORGANIZATIONNO=a.ORGANIZATIONNO AND j.LANG_TYPE='" + langType +"' " 
					+"   LEFT JOIN DCP_ORG_LANG j1 on j1.EID=a.EID AND j1.ORGANIZATIONNO=a.DELIVERORGNO AND j1.LANG_TYPE='" + langType +"' "

				+"   LEFT JOIN DCP_ORG_LANG j2 on j2.EID=a.EID AND j2.ORGANIZATIONNO=a.corp AND j2.LANG_TYPE='" + langType +"' "
				+"   LEFT JOIN DCP_ORG_LANG j3 on j3.EID=a.EID AND j3.ORGANIZATIONNO=a.receiptCorp AND j3.LANG_TYPE='" + langType +"' "
				+"   LEFT JOIN DCP_ORG_LANG j4 on j4.EID=a.EID AND j4.ORGANIZATIONNO=a.deliveryCorp AND j4.LANG_TYPE='" + langType +"' "



				+ " left join DCP_BILLDATE  BD on a.eid=BD.eid and BD.BILLDATENO=a.BILLDATENO "
					+ " left join DCP_PAYDATE  PD on a.eid=PD.eid and PD.PAYDATENO=a.PAYDATENO "
					+ " left join DCP_INVOICETYPE  IE on a.eid=IE.eid and IE.INVOICECODE=a.INVOICECODE "
					+"   LEFT JOIN DCP_WAREHOUSE_LANG W on W.EID=a.EID AND W.WAREHOUSE=a.WAREHOUSE AND W.ORGANIZATIONNO=a.ORGANIZATIONNO AND w.LANG_TYPE='" + langType +"' "
					+ " left join DCP_EMPLOYEE  p1 on a.eid=p1.eid and p1.EMPLOYEENO=a.EMPLOYEEID "
					+ "  left join DCP_DEPARTMENT_LANG d1  on p1.eid=d1.eid and p1.DEPARTMENTNO=d1.DEPARTNO and d1.lang_type='"+langType+"' "
					+ " left join PLATFORM_STAFFS_LANG  p2 on a.eid=p2.eid and p2.opno=a.CONFIRMBY and p2.lang_type='"+langType+"'"
					+ " left join PLATFORM_STAFFS_LANG  p3 on a.eid=p3.eid and p3.opno=a.CANCELBY and p3.lang_type='"+langType+"' "
					+ " left join PLATFORM_STAFFS_LANG  p4 on a.eid=p4.eid and p4.opno=a.OWNOPID and p4.lang_type='"+langType+"' "
					+ " left join PLATFORM_STAFFS_LANG  p5 on a.eid=p5.eid and p5.opno=a.CLOSEBY and p5.lang_type='"+langType+"' "
					+"  left join DCP_DEPARTMENT_LANG d4  on a.eid=d4.eid and a.OWNDEPTID=d4.DEPARTNO and d4.lang_type='"+langType+"' "
					+"  left join DCP_DEPARTMENT_LANG d5  on a.eid=d5.eid and a.CREATEDEPTID=d5.DEPARTNO and d5.lang_type='"+langType+"' "
						+"  left join DCP_DEPARTMENT_LANG d6  on a.eid=d6.eid and a.departid=d6.DEPARTNO and d6.lang_type='"+langType+"' "

				+"   LEFT JOIN DCP_WAREHOUSE_LANG W1 on W1.EID=a.EID AND W1.WAREHOUSE=a.receiptWarehouse AND W1.ORGANIZATIONNO=a.ORGANIZATIONNO AND w1.LANG_TYPE='" + langType +"' "
				+"   LEFT JOIN DCP_WAREHOUSE_LANG W2 on W2.EID=a.EID AND W2.WAREHOUSE=a.invWarehouse AND W2.ORGANIZATIONNO=a.ORGANIZATIONNO AND w2.LANG_TYPE='" + langType +"' "
				+" left join DCP_PTEMPLATE  pt on pt.eid=a.eid and pt.ptemplateno=a.templateno and pt.doc_type='0' " +
				" left join dcp_bizpartner m on m.eid=a.eid and m.bizpartnerno=a.payee " +
				" left join dcp_bizpartner n on n.eid=a.eid and n.bizpartnerno=a.payer"


		);
		if("1".equals(searchScope)){
			sqlbuf.append(" inner join (select * from DCP_STOCKOUTNOTICE_DETAIL a where a.eid='"+eId+"' " +
					" and a.organizationno='"+req.getOrganizationNO()+"' " +
					"and nvl(a.PQTY,0)>nvl(a.STOCKOUTQTY,0) ) p on p.eid=a.eid and p.organizationno=p.organizationno and p.billno=a.billno");
		}
		sqlbuf.append("  left join DCP_CURRENCY_LANG cc on cc.eid=a.eid and cc.CURRENCY=a.CURRENCY  and cc.lang_type='"+langType+"' ");
		sqlbuf.append(" WHERE a.EID = '"+eId+"' ");
		if(Check.Null(getType)||"0".equals(getType)) {
			sqlbuf.append(" and a.ORGANIZATIONNO = '" + req.getOrganizationNO() + "' ");
		}else if("1".equals(getType)){
			sqlbuf.append(" and a.DELIVERORGNO = '" + req.getOrganizationNO() + "' ");
		}
		
		if(key != null && key.length() > 0){
			sqlbuf.append(" and  ( a.BILLNO  like  "+SUtil.RetLikeStr(key)); 
		    sqlbuf.append(" or   a.OBJECTID  like  "+SUtil.RetLikeStr(key));
			sqlbuf.append(" or   a.SOURCEBILLNO  like  "+SUtil.RetLikeStr(key));
			sqlbuf.append(" or     b.sNAME  like  "+SUtil.RetLikeStr(key));
	    	sqlbuf.append(" ) " );
		}
		if(billType != null && billType.length() > 0)
			sqlbuf.append(" and a.billType = '"+billType+"' ");
		if ("rDate".equals(dateType)){
			sqlbuf.append(" and a.RDATE>='"+beginDate+"' and a.rdate<='"+endDate+"'");
		}
		if ("bDate".equals(dateType)){
			sqlbuf.append(" and a.bDATE>='"+beginDate+"' and a.bdate<='"+endDate+"'");
		}

		if(status != null && status.length() > 0)
			sqlbuf.append(" and a.STATUS = '"+status+"' ");
		if(deliverOrgNo != null && deliverOrgNo.length() > 0)
			sqlbuf.append(" and a.DELIVERORGNO = '"+deliverOrgNo+"' ");
		sqlbuf.append(" ) a ) DBL  WHERE rn > "+startRow +" AND rn  <= "+(startRow + pageSize)+" ");
		sqlbuf.append(" order by  bdate desc,BILLNO desc  " );
		
		
		sql = sqlbuf.toString();
		//logger.info(sql);  
		return sql;
	}
	
	
	
	/**
	 * 新增要處理的資料(先加入的, 先處理)
	 * 
	 * @param row
	 */
	protected final void addProcessData(DataProcessBean row) {
		this.pData.add(row);
	}
	
	/**
	 * 计算总数
	 * @param req
	 * @return
	 * @throws Exception
	 */
	protected String getCountSql(DCP_StockOutNoticeQueryReq req) throws Exception {
		// TODO Auto-generated method stub
 
		String key = null;
		String billType = null;
		String dateType = null;
		String status = null; 
		String beginDate = null;
		String endDate = null;
		String deliverOrgNo = null;
		String searchScope = null;
		
		String eId = req.geteId();
 
		
		if(req.getRequest()!=null)
		{
			key =  req.getRequest().getKeyTxt();
			status = req.getRequest().getStatus();
			billType = req.getRequest().getBillType();
			dateType = req.getRequest().getDateType();
			beginDate = req.getRequest().getBeginDate();
			endDate = req.getRequest().getEndDate();
			deliverOrgNo = req.getRequest().getDeliverOrgNo();
			searchScope = req.getRequest().getSearchScope();
			if (StringUtils.isEmpty(beginDate))
				beginDate ="20241001";
			if (StringUtils.isEmpty(endDate))
				endDate ="20641001";
		}
 	
		StringBuffer sqlbuf=new StringBuffer("");
		String sql = "";
		
		sqlbuf.append("select num from( select count(* ) AS num    "	
				+ " FROM DCP_STOCKOUTNOTICE a s "
				//+ " left join DCP_STOCKOUTNOTICE_DETAIL  b on a.eid=b.eid and b.BILLNO=a.BILLNO "
				+ " inner join DCP_bizpartner  b on a.eid=b.eid and a.OBJECTID=b.BIZPARTNERNO  and b.BIZTYPE=a.OBJECTTYPE "
				 );

		if("1".equals(searchScope)){
			sqlbuf.append(" inner join (select * from DCP_STOCKOUTNOTICE_DETAIL a where a.eid='"+eId+"' " +
					" and a.organizationno='"+req.getOrganizationNO()+"' " +
					"and nvl(a.PQTY,0)>nvl(a.STOCKOUTQTY,0) ) p on p.eid=a.eid and p.organizationno=p.organizationno and p.billno=a.billno");
		}

		sqlbuf.append("where a.EID='"+eId+"'  ");
		
		if(key != null && key.length() > 0){
			sqlbuf.append(" and  ( a.BILLNO  like  "+SUtil.RetLikeStr(key)); 
		sqlbuf.append(" or   a.OBJECTID  like  "+SUtil.RetLikeStr(key)); 
		sqlbuf.append(" or     b.sNAME  like  "+SUtil.RetLikeStr(key)); 
		sqlbuf.append(" ) " ); 
		}
		if(billType != null && billType.length() > 0)
			sqlbuf.append(" and a.billType = '"+billType+"' ");
		if ("rDate".equals(dateType))
			  sqlbuf.append( SUtil.RetDateCon("a.RDATE",beginDate,endDate)) ;
			if ("bdate".equals(dateType))
				  sqlbuf.append(SUtil.RetDateCon("a.BDATE",beginDate,endDate));
		if(status != null && status.length() > 0)
			sqlbuf.append(" and a.STATUS = '"+status+"' ");
		if(deliverOrgNo != null && deliverOrgNo.length() > 0)
			sqlbuf.append(" and a.DELIVERORGNO = '"+deliverOrgNo+"' ");
		
		sql= sqlbuf.toString();
		return sql;
	}
	

	private String getDetailSql(DCP_StockOutNoticeQueryReq req,String withasSql_BILL) throws Exception{
		StringBuffer sb=new StringBuffer();

		sb.append("with p AS ( " + withasSql_BILL + ") " +
				"select a.billno ,nvl(a.pqty,0) as pqty,nvl(a.STOCKOUTQTY,0) as STOCKOUTQTY from DCP_STOCKOUTNOTICE_DETAIL a " +
				" inner join p on p.billno=a.billno " +
				" where a.eid='"+req.geteId()+"' " +
				" and a.organizationno='"+req.getOrganizationNO()+"' " +
				" ");


		return sb.toString();
	}
	  
	
	
}
