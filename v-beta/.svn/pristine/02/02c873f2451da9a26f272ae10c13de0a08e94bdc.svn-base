package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_SStockOutQueryReq;
import com.dsc.spos.json.cust.res.DCP_SStockOutQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_SStockOutQuery extends SPosBasicService<DCP_SStockOutQueryReq, DCP_SStockOutQueryRes>{
	@Override
	protected boolean isVerifyFail(DCP_SStockOutQueryReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();
		//必传值不为空
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
		return false;
	}
	
	@Override
	protected TypeToken<DCP_SStockOutQueryReq> getRequestType() {
		return new TypeToken<DCP_SStockOutQueryReq>(){};
	}
	
	@Override
	protected DCP_SStockOutQueryRes getResponseType() {
		return new DCP_SStockOutQueryRes();
	}
	
	@Override
	protected DCP_SStockOutQueryRes processJson(DCP_SStockOutQueryReq req) throws Exception {
		String eId = req.geteId();
		//try {
			DCP_SStockOutQueryRes res =this.getResponse();
			int totalRecords = 0;	//总笔数
			int totalPages = 0;		//总页数
			//查询资料
			String sql = this.getQuerySql(req);
			List<Map<String, Object>> getQData = this.doQueryData(sql, null);
			//查询图片
			String imageSql = this.getImageQuerySql(req);
			List<Map<String, Object>> getImageQData = this.doQueryData(imageSql, null);
			
			res.setDatas(new ArrayList<>());
			if (getQData != null && !getQData.isEmpty()) {
				//算總頁數
				String num = getQData.get(0).get("NUM").toString();
				totalRecords=Integer.parseInt(num);
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
				
				// 拼接返回图片路径  by jinzma 20210705
				String isHttps= PosPub.getPARA_SMS(dao, eId, "", "ISHTTPS");
				String httpStr=isHttps.equals("1")?"https://":"http://";
				String domainName=PosPub.getPARA_SMS(dao, eId, "", "DomainName");
				if (domainName.endsWith("/")) {
					domainName = httpStr + domainName + "resource/image/";
				}else{
					domainName = httpStr + domainName + "/resource/image/";
				}
				
				//单头主键字段
				Map<String, Boolean> condition = new HashMap<>(); //查询条件
				condition.put("SSTOCKOUTNO", true);
				//调用过滤函数
				List<Map<String, Object>> getQHeader=MapDistinct.getMap(getQData, condition);
				for (Map<String, Object> oneData : getQHeader) {
					DCP_SStockOutQueryRes.level1Elm oneLv1 = res.new level1Elm();
					oneLv1.setDatas(new ArrayList<>());
					oneLv1.setImage_list(new ArrayList<>());
					
					String sStockOutNo = oneData.get("SSTOCKOUTNO").toString();
					//设置响应
					oneLv1.setsStockOutNo(sStockOutNo);
					oneLv1.setbDate(oneData.get("BDATE").toString());
					oneLv1.setOfNo(oneData.get("OFNO").toString());
					oneLv1.setStatus(oneData.get("STATUS").toString());
					oneLv1.setMemo(oneData.get("MEMO").toString());
					oneLv1.setWarehouse(oneData.get("WAREHOUSE").toString());
					oneLv1.setWarehouseName(oneData.get("WAREHOUSE_NAME").toString());
					oneLv1.setTaxCode(oneData.get("TAXCODE").toString());
					oneLv1.setTaxName(oneData.get("TAXNAME").toString());
					oneLv1.setSupplier(oneData.get("SUPPLIER").toString());
					oneLv1.setAbbr(oneData.get("ABBR").toString());
					oneLv1.setStockOutAllowType(oneData.get("STOCKOUTALLOWTYPE").toString());
					oneLv1.setTotCqty(oneData.get("TOT_CQTY").toString());
					oneLv1.setTotPqty(oneData.get("TOT_PQTY").toString());
					oneLv1.setTotAmt(oneData.get("TOT_AMT").toString());
					oneLv1.setTotDistriAmt(oneData.get("TOT_DISTRIAMT").toString());
					
					oneLv1.setCreateBy(oneData.get("CREATEBY").toString());
					oneLv1.setCreateByName(oneData.get("CREATENAME").toString());
					oneLv1.setCreateDate(oneData.get("CREATE_DATE").toString());
					oneLv1.setCreateTime(oneData.get("CREATE_TIME").toString());
					oneLv1.setModifyBy(oneData.get("MODIFYBY").toString());
					oneLv1.setModifyByName(oneData.get("MODIFYNAME").toString());
					oneLv1.setModifyDate(oneData.get("MODIFY_DATE").toString());
					oneLv1.setModifyTime(oneData.get("MODIFY_TIME").toString());
					oneLv1.setSubmitBy(oneData.get("SUBMITBY").toString());
					oneLv1.setSubmitByName(oneData.get("SUBMITNAME").toString());
					oneLv1.setSubmitDate(oneData.get("SUBMIT_DATE").toString());
					oneLv1.setSubmitTime(oneData.get("SUBMIT_TIME").toString());
					oneLv1.setConfirmBy(oneData.get("CONFIRMBY").toString());
					oneLv1.setConfirmByName(oneData.get("CONFIRMNAME").toString());
					oneLv1.setConfirmDate(oneData.get("CONFIRM_DATE").toString());
					oneLv1.setConfirmTime(oneData.get("CONFIRM_TIME").toString());
					oneLv1.setCancelBy(oneData.get("CANCELBY").toString());
					oneLv1.setCancelByName(oneData.get("CANCELNAME").toString());
					oneLv1.setCancelDate(oneData.get("CANCEL_DATE").toString());
					oneLv1.setCancelTime(oneData.get("CANCEL_TIME").toString());
					oneLv1.setAccountBy(oneData.get("ACCOUNTBY").toString());
					oneLv1.setAccountByName(oneData.get("ACCOUNTNAME").toString());
					oneLv1.setAccountDate(oneData.get("ACCOUNT_DATE").toString());
					oneLv1.setAccountTime(oneData.get("ACCOUNT_TIME").toString());
					
					oneLv1.setUpdate_time(oneData.get("UPDATE_TIME").toString());
					oneLv1.setProcess_status(oneData.get("PROCESS_STATUS").toString());
					oneLv1.setProcessERPNo(oneData.get("PROCESS_ERP_NO").toString());

					oneLv1.setEmployeeId(oneData.get("EMPLOYEEID").toString());
					oneLv1.setEmployeeName(oneData.get("EMPLOYEENAME").toString());
					oneLv1.setDepartId(oneData.get("DEPARTID").toString());
					oneLv1.setDepartName(oneData.get("DEPARTNAME").toString());
					oneLv1.setOrderOrgNo(oneData.get("ORDERORGNO").toString());
					oneLv1.setOrderOrgName(oneData.get("ORDERORGNAME").toString());
					oneLv1.setPayType(oneData.get("PAYTYPE").toString());
					oneLv1.setPayOrgNo(oneData.get("PAYORGNO").toString());
					oneLv1.setPayOrgName(oneData.get("PAYORGNAME").toString());
					oneLv1.setBillDateNo(oneData.get("BILLDATENO").toString());
					oneLv1.setBillDateDesc(oneData.get("BILLDATEDESC").toString());
					oneLv1.setPayDateNo(oneData.get("PAYDATENO").toString());
					oneLv1.setPayDateDesc(oneData.get("PAYDATEDESC").toString());
					oneLv1.setInvoiceCode(oneData.get("INVOICECODE").toString());
					oneLv1.setInvoiceName(oneData.get("INVOICENAME").toString());
					oneLv1.setCurrency(oneData.get("CURRENCY").toString());
					oneLv1.setCurrencyName(oneData.get("CURRENCYNAME").toString());
					oneLv1.setStockOutType(oneData.get("STOCKOUTTYPE").toString());
					oneLv1.setCustomer(oneData.get("CUSTOMER").toString());
					oneLv1.setCustomerName(oneData.get("CUSTOMERNAME").toString());
                    oneLv1.setSupplierName(oneData.get("SUPPLIERNAME").toString());
					oneLv1.setoType(oneData.get("OTYPE").toString());
                    oneLv1.setOriginNo(oneData.get("ORIGINNO").toString());
                    oneLv1.setPayee(oneData.get("PAYEE").toString());
                    oneLv1.setPayeeName(oneData.get("PAYEENAME").toString());
                    oneLv1.setPayer(oneData.get("PAYER").toString());
                    oneLv1.setPayerName(oneData.get("PAYERNAME").toString());

                    oneLv1.setTaxPayerType(oneData.get("TAXPAYERTYPE").toString());
                    oneLv1.setInputTaxCode(oneData.get("INPUTTAXCODE").toString());
                    oneLv1.setInputTaxRate(oneData.get("INPUTTAXRATE").toString());
                    oneLv1.setOutputTaxCode(oneData.get("OUTPUTTAXCODE").toString());
                    oneLv1.setOutputTaxRate(oneData.get("OUTPUTTAXRATE").toString());

					//添加图片 //【ID1021201】【霸王3.0】门店管理，移动门店采购退货增加图片上传 --服务 by jinzma 20211013
					if (getImageQData!=null && !getImageQData.isEmpty()) {
						for (Map<String, Object> oneDataImage : getImageQData) {
							String image = oneDataImage.get("IMAGE").toString();
							if (sStockOutNo.equals(oneDataImage.get("SSTOCKOUTNO"))) {
								DCP_SStockOutQueryRes.level3Elm oneLv3 = res.new level3Elm();
								oneLv3.setImage(image);
								//添加单身
								oneLv1.getImage_list().add(oneLv3);
							}
						}
					}
					//添加单头
					res.getDatas().add(oneLv1);
				}
			}
			
			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);
			
			return res;
			
            //} catch (Exception e) {
			//throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
		//}
	}
	
	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	}
	
	@Override
	protected String getQuerySql(DCP_SStockOutQueryReq req) throws Exception {
		StringBuffer sqlbuf = new StringBuffer();
		String eId = req.geteId();
		String shopId = req.getShopId();
		String beginDate = req.getRequest().getBeginDate();
		String endDate =req.getRequest().getEndDate();
		String status = req.getRequest().getStatus();
		String keyTxt = req.getRequest().getKeyTxt();
		String supplier = req.getRequest().getSupplier();
		String langType = req.getLangType();
        String stockOutType = req.getRequest().getsStockOutType();
        String customer = req.getRequest().getCustomer();

        //计算起始位置
		int pageSize = req.getPageSize();
		int startRow = ((req.getPageNumber() - 1) * pageSize);
		
		
		sqlbuf.append(" "
				+ " with sstockout as("
				+ " select * from ("
				+ " select count(*) over () as num,row_number() over (order by a.status asc,a.bdate desc,a.sstockoutno desc) as rn,a.*"
				+ " from dcp_sstockout a"
				+ " where a.eid='"+eId+"' and a.shopid='"+shopId+"' and a.bdate >='"+beginDate+"' and a.bdate<='"+endDate+"'"
		);
		
		if (!Check.Null(status)) {
			sqlbuf.append(" and a.status='"+status+"'");
		}
		if (!Check.Null(supplier)) {
			sqlbuf.append(" and a.supplier='"+supplier+"'");
		}
        if(Check.NotNull(stockOutType)){
            sqlbuf.append(" and a.stockouttype='"+stockOutType+"' ");
        }
        if(Check.NotNull(customer)){
            sqlbuf.append(" and a.customer='"+customer+"' ");
        }
		if (!Check.Null(keyTxt)) {
			sqlbuf.append(" "
					+ " and (a.tot_amt like '%"+keyTxt+"%' or a.tot_pqty like '%"+keyTxt+"%' "
					+ " or a.sstockoutno like '%"+keyTxt+"%' "
                    + " or a.ofno like '%"+keyTxt+"%' "
					+ " or a.memo like '%"+keyTxt+"%' or a.process_erp_no like '%"+keyTxt+"%')");
		}
		
		sqlbuf.append(" ) where rn>"+startRow+" and rn<="+(startRow+pageSize));
		sqlbuf.append(" )");
		
		sqlbuf.append(" "
				+ " select a.num,a.rn,a.eid,a.shopid,a.sstockoutno,a.ofno,a.warehouse,a.supplier,a.bdate,a.taxcode,a.memo,a.status,"
				+ " a.tot_cqty,a.tot_pqty,a.tot_amt,a.tot_distriamt,"
				+ " a.createby,a.create_date,a.create_time,a.modifyby,a.modify_date,a.modify_time,"
				+ " a.submitby,a.submit_date,a.submit_time,a.confirmby,a.confirm_date,a.confirm_time,"
				+ " a.cancelby,a.cancel_date,a.cancel_time,a.accountby,a.account_date,a.account_time,"
				+ " a.process_erp_no,a.process_status,a.update_time,"
				+ " e.stockoutallowtype,f.abbr,f.supplier_name,"//d.plu_name,
				+ " h.taxname,i.warehouse_name,"//gul.spec,image.listimage as listimage,fn.featurename,
				+ " p1.op_name as createname,p2.op_name as modifyname,p3.op_name as submitname,"
				+ " p4.op_name as confirmname,p5.op_name as cancelname,p6.op_name as accountname,"
				+ " a.employeeid,e1.name as employeename,a.departid,d1.departname,a.orderorgno,o1.org_name as orderorgname,a.paytype,o2.org_name as payorgname,a.payorgno," +//bul.udlength as baseunitudlength,
				"   a.billdateno,dbl.name as billdatedesc,a.paydateno,dpl.name as paydatedesc,a.invoicecode,dil.invoice_name as invoicename,a.currency ,dcl.name as currencyname ,a.stockouttype,a.customer,db.sname as customername,a.otype,db1.sname as suppliername," +
                " a.originno,a.payee,a.payer,j.sname as payeename,k.sname as payername,a.taxpayer_type as taxpayertype,a.input_taxcode as inputtaxcode,a.input_taxrate as inputtaxrate,a.output_taxcode as outputtaxcode,a.output_taxrate as outputtaxrate    "
				+ " from sstockout a"
				+ " inner join dcp_sstockout_detail b on a.eid=b.eid and a.shopid=b.shopid and a.sstockoutno=b.sstockoutno"
				+ " left  join dcp_supplier e on a.eid=e.eid and a.supplier=e.supplier"
				+ " left  join dcp_supplier_lang f on a.eid=f.eid and a.supplier=f.supplier and f.lang_type='"+langType+"'"
				+ " left  join dcp_sstockin_detail g on a.eid=g.eid and a.shopid=g.shopid and a.ofno=g.sstockinno and b.oitem=g.item"
				+ " left  join dcp_taxcategory_lang h on a.eid=h.eid and a.taxcode=h.taxcode and h.lang_type='"+langType+"'"
				+ " left  join dcp_warehouse_lang i on a.eid=i.eid and a.organizationno=i.organizationno and a.warehouse=i.warehouse and i.lang_type='"+langType+"'"
				+ " left  join PLATFORM_STAFFS_LANG p1 on a.eid=p1.eid and a.createby=p1.opno and p1.lang_type='"+req.getLangType()+"' "
				+ " left  join PLATFORM_STAFFS_LANG p2 on a.eid=p2.eid and a.modifyby=p2.opno and p2.lang_type='"+req.getLangType()+"' "
				+ " left  join PLATFORM_STAFFS_LANG p3 on a.eid=p3.eid and a.submitby=p3.opno and p3.lang_type='"+req.getLangType()+"' "
				+ " left  join PLATFORM_STAFFS_LANG p4 on a.eid=p4.eid and a.confirmby=p4.opno and p4.lang_type='"+req.getLangType()+"' "
				+ " left  join PLATFORM_STAFFS_LANG p5 on a.eid=p5.eid and a.cancelby=p5.opno and p5.lang_type='"+req.getLangType()+"' "
				+ " left  join PLATFORM_STAFFS_LANG p6 on a.eid=p6.eid and a.accountby=p6.opno and p6.lang_type='"+req.getLangType()+"' "
				+ " left join DCP_EMPLOYEE e1 on e1.eid=a.eid and e1.employeeno=a.employeeid " +
				" left join dcp_department_lang d1 on d1.eid=a.eid and d1.departno=a.departid and d1.lang_type='"+langType+"' " +
				" left join DCP_ORG_LANG o1 on o1.eid=a.eid and o1.organizationno =a.orderorgno and o1.lang_type='"+langType+"' " +
				" left join DCP_ORG_LANG o2 on o2.eid=a.eid and o2.organizationno =a.payorgno and o2.lang_type='"+langType+"' " +
				" left join DCP_BILLDATE_LANG dbl on dbl.eid=a.eid and dbl.BILLDATENO=a.billdateno and dbl.lang_type='"+langType+"' " +
				" left join DCP_PAYDATE_LANG dpl on dpl.eid=a.eid and dpl.paydateno=a.paydateno and dpl.lang_type='"+langType+"' " +
				" left join DCP_INVOICETYPE_LANG dil on dil.eid=a.eid and dil.invoicecode =a.invoicecode and dil.lang_type='"+langType+"' " +
				" left join DCP_CURRENCY_LANG dcl on dcl.eid=a.eid and dcl.currency=a.currency and dcl.lang_type='"+langType+"' " +
				" left join dcp_bizpartner db on db.eid=a.eid  and db.BIZPARTNERNO=a.customer " +//and db.organizationno=a.organizationno
                " left join dcp_bizpartner db1 on db1.eid=a.eid and db1.BIZPARTNERNO=a.supplier " +
                " left join dcp_bizpartner j on j.eid=a.eid and j.bizpartnerno=a.payee " +
                " left join dcp_bizpartner k on k.eid=a.eid and k.bizpartnerno=a.payer " +//and db1.organizationno=a.organizationno
				"order by rn"
				+ " ");
		
		return sqlbuf.toString();
	}
	
	private String getImageQuerySql(DCP_SStockOutQueryReq req){
		StringBuffer sqlbuf = new StringBuffer();
		String eId = req.geteId();
		String shopId = req.getShopId();
		String beginDate = req.getRequest().getBeginDate();
		String endDate =req.getRequest().getEndDate();
		String status = req.getRequest().getStatus();
		String keyTxt = req.getRequest().getKeyTxt();
		String supplier = req.getRequest().getSupplier();
		
		
		//计算起始位置
		int pageSize = req.getPageSize();
		int startRow = ((req.getPageNumber() - 1) * pageSize);
		
		sqlbuf.append(" "
				+ " with sstockout as("
				+ " select * from ("
				+ " select count(*) over () as num,row_number() over (order by a.status asc,a.bdate desc,a.sstockoutno desc) as rn,a.*"
				+ " from dcp_sstockout a"
				+ " where a.eid='"+eId+"' and a.shopid='"+shopId+"' and a.bdate >='"+beginDate+"' and a.bdate<='"+endDate+"'"
		);
		
		if (!Check.Null(status)) {
			sqlbuf.append(" and a.status='"+status+"'");
		}
		if (!Check.Null(supplier)) {
			sqlbuf.append(" and a.supplier='"+supplier+"'");
		}
		if (!Check.Null(keyTxt)) {
			sqlbuf.append(" "
					+ " and (a.tot_amt like '%"+keyTxt+"%' or a.tot_pqty like '%"+keyTxt+"%' "
					+ " or a.sstockoutno like '%"+keyTxt+"%' "
					+ " or a.memo like '%"+keyTxt+"%' or a.process_erp_no like '%"+keyTxt+"%')");
		}
		
		sqlbuf.append(" ) where rn>"+startRow+" and rn<="+(startRow+pageSize));
		sqlbuf.append(" )");
		
		sqlbuf.append(" select b.sstockoutno,b.item,b.image from sstockout a");
		sqlbuf.append(" inner join dcp_sstockout_image b on a.eid=b.eid and a.shopid=b.shopid and a.sstockoutno=b.sstockoutno");
		sqlbuf.append(" order by b.sstockoutno,b.item");
		
		return sqlbuf.toString();
	}
	
	//以下保存历史查询语句，方便后续排查  by jinzma 20220615
	private String getOldQuerySql(DCP_SStockOutQueryReq req) {
		/*StringBuffer sqlbuf = new StringBuffer();
		String status = req.getRequest().getStatus();
		String keyTxt = req.getRequest().getKeyTxt();
		String supplier = req.getRequest().getSupplier();
		String beginDate = req.getRequest().getBeginDate();
		String endDate =req.getRequest().getEndDate();
		sqlbuf.append(" "
				+ " select COUNT(DISTINCT a.sStockOutno) AS num from DCP_SStockOut a  "+
				" INNER JOIN DCP_SStockOut_DETAIL d ON a.EID = d.EID AND a.SHOPID = d.SHOPID AND a.sStockOutno = d.sStockOutno AND a.BDATE = d.BDATE" +
				" INNER JOIN DCP_GOODS f ON d.EID = f.EID AND d.pluno = f.pluno"
				+ " WHERE a.BDATE between "+beginDate +" and "+endDate+" "
		);
		if (status != null && status.length() > 0) {
			sqlbuf.append(" AND a.STATUS='"+status+"'");
		}
		if (keyTxt != null && keyTxt.length() > 0) {
			sqlbuf.append(" AND (a.TOT_AMT LIKE '%%"+keyTxt+"%%' OR a.TOT_PQTY LIKE '%%"+keyTxt+"%%' OR a.SStockOutNO LIKE '%%"+keyTxt+"%%' OR a.MEMO LIKE '%%"+keyTxt+"%%' "
					+ " or a.process_ERP_No like '%%"+keyTxt+"%%'  )");
		}
		if (supplier != null && supplier.length() > 0) {
			sqlbuf.append(" AND a.SUPPLIER='"+supplier+"'");
		}
		sqlbuf.append(" AND a.SHOPID='"+req.getShopId()+"'");
		sqlbuf.append(" AND a.EID='"+req.geteId()+"'");
		String sql = sqlbuf.toString();
		
		//单身查询语句
		{
			//StringBuffer sqlbuf = new StringBuffer();
			sqlbuf.append(" "
					+ " select processERPNO,A_sStockOutNO,A_SHOPID,A_supplier,B_bDate,A_memo,A_status,"
					+ " createBy, modifyby,SubmitBy,ConfirmBy,CancelBy,AccountBy,Create_Time,Create_Date,Modify_Date,Modify_TIME, "
					+ " Submit_Date,Submit_Time,Confirm_Date,Confirm_Time,Cancel_Date,Cancel_Time,Account_Date,Account_TIME,createName, "
					+ " modifyname,Submitname,Confirmname,Cancelname,Accountname, "
					+ " A_totqty,A_totamt,TOT_DISTRIAMT,A_totcqty,C_abbr,C_supplierName,D_item,D_pluNO,"
					+ " E_pluName,E_spec,listimage,D_baseunit,D_punit,D_pqty,D_baseqty,D_unitRatio,D_price,D_amt,DISTRIAMT,H_punitName,"
					+ " j_baseunitName, "
					+ " warehouse_main, warehouse_detail,warehouse_main_name,FPUNIT,RETWQTY,FUNIT_RATIO,FPQTY,OFNO,DOITEM,IUNITNAME,"
					+ " UPDATE_TIME,PROCESS_STATUS,featureno,featurename,image, "
					+ " BATCH_NO,PROD_DATE,DISTRIPRICE,ISBATCH,"
					+ " STOCKINVALIDDAY,STOCKOUTVALIDDAY,SHELFLIFE,STOCKOUTALLOWTYPE,PUNIT_UDLENGTH,ATAXCODE,TTYTAXNAME  "
					+ " from ("
					+ " select a.process_erp_no as processERPNO ,a.sStockOutNO as A_sStockOutNO,a.SHOPID as A_SHOPID,a.supplier as A_supplier,"
					+ " a.bDate as B_bDate,a.TAXCODE as ATAXCODE,tty.TAXNAME as TTYTAXNAME,"
					+ " a.memo as A_memo,"
					+ " a.status as A_status, "
					+ " A.CREATEBY as createBy,a.modifyby as modifyby,a.SubmitBy as SubmitBy,a.ConfirmBy as ConfirmBy,a.CancelBy as CancelBy, "
					+ " a.AccountBy as AccountBy,A.Create_Time,A.Create_Date,A.Modify_Date,A.Modify_TIME,A.Submit_Date,A.Submit_Time, "
					+ " A.Confirm_Date,A.Confirm_Time,A.Cancel_Date,A.Cancel_Time,A.Account_Date,A.Account_TIME,b.op_name as createName, "
					+ " b1.op_name as modifyname,b2.op_name as Submitname,b3.op_name as Confirmname,b4.op_name as Cancelname, "
					+ " b5.op_name as Accountname, "
					+ " a.TOT_PQTY as A_totqty,"
					+ " a.TOT_AMT as A_totamt,a.TOT_DISTRIAMT,a.TOT_CQTY as A_totcqty,c.abbr as C_abbr,c.supplier_name as C_supplierName,"
					+ " d.iTEM as D_item,d.pluNO as D_pluNO,"
					+ " e.PLU_NAME as E_pluName,"
					+ " gul.spec as E_spec,image.listimage as listimage,d.baseunit as D_baseunit,d.punit as D_punit,d.pqty as D_pqty,"
					+ " d.baseqty as D_baseqty,"
					+ " d.UNIT_RATIO as D_unitRatio,"
					+ " d.price as D_price,d.amt as D_amt,d.DISTRIAMT,h.UNAME as H_punitName,j.UNAME as j_baseunitName,"
					+ " a.warehouse as warehouse_main,d.warehouse as warehouse_detail,taw.warehouse_name as warehouse_main_name,g.punit FPUNIT,"
					+ " g.RETWQTY RETWQTY,g.UNIT_RATIO FUNIT_RATIO,g.pqty FPQTY,a.ofno,d.oitem doitem,i.UNAME IUNITNAME,A.UPDATE_TIME,"
					+ " A.PROCESS_STATUS,  "
					+ " d.BATCH_NO,d.PROD_DATE,d.DISTRIPRICE,f.ISBATCH, "
					+ " f.STOCKINVALIDDAY,f.STOCKOUTVALIDDAY,f.SHELFLIFE,c1.STOCKOUTALLOWTYPE,HH.UDLENGTH AS PUNIT_UDLENGTH, "
					+ " d.featureno,fn.featurename,"
					+ " d1.image"
					+ " from DCP_SStockOut a "
					+ " left join DCP_TAXCATEGORY_LANG tty on a.EID=tty.EID and a.TAXCODE=tty.TAXCODE and tty.lang_type='"+langType+"' "
					+ " LEFT JOIN PLATFORM_STAFFS_LANG b ON a.EID=b.EID AND a.CREATEBY=b.OPNO AND b.LANG_TYPE='"+langType+"' "
					+ " LEFT JOIN PLATFORM_STAFFS_LANG b1 ON a.EID=b1.EID AND a.modifyby=b1.OPNO AND b1.LANG_TYPE='"+langType+"'  "
					+ " LEFT JOIN PLATFORM_STAFFS_LANG b2 ON a.EID=b2.EID AND a.SubmitBy=b2.OPNO AND b2.LANG_TYPE='"+langType+"'  "
					+ " LEFT JOIN PLATFORM_STAFFS_LANG b3 ON a.EID=b3.EID AND a.ConfirmBy=b3.OPNO AND b3.LANG_TYPE='"+langType+"'  "
					+ " LEFT JOIN PLATFORM_STAFFS_LANG b4 ON a.EID=b4.EID AND a.CancelBy=b4.OPNO AND b4.LANG_TYPE='"+langType+"' "
					+ " LEFT JOIN PLATFORM_STAFFS_LANG b5 ON a.EID=b5.EID AND a.AccountBy=b5.OPNO AND b5.LANG_TYPE='"+langType+"' "
					+ " left join DCP_SUPPLIER_lang c on a.EID=c.EID and a.supplier=c.supplier AND c.LANG_TYPE='"+langType+"'"
					+ " left join DCP_SUPPLIER c1 on a.EID=c1.EID and a.supplier=c1.supplier AND c1.status='100' "
					+ " left join DCP_WAREHOUSE_lang taw on a.EID=taw.EID and a.organizationno=taw.organizationno and a.warehouse=taw.warehouse and taw.lang_type='"+langType+"'"
					+ " inner join DCP_SStockOut_DETAIL d on a.EID=d.EID and a.SHOPID=d.SHOPID and a.sStockOutno=d.sStockOutno and a.BDATE=d.BDATE "
					+ " left  join dcp_sstockout_image d1 on a.eid=d1.eid and a.shopid=d1.shopid and a.sstockoutno=d1.sstockoutno"
					+ " left join DCP_GOODS_lang e on d.EID=e.EID and d.pluno=e.pluno and e.LANG_TYPE='"+langType+"'"
					+ " inner join DCP_GOODS f  on d.EID=f.EID and d.pluno=f.pluno"
					+ " left join DCP_SSTOCKIN_DETAIL g on g.SSTOCKINNO=a.ofno and a.EID=g.EID and a.SHOPID=g.SHOPID  and d.oitem=g.item "
					+ " left join DCP_UNIT_lang h on d.EID=h.EID and d.punit=h.unit and h.lang_type='"+langType+"' "
					+ " left join DCP_UNIT_lang i on g.EID=i.EID and g.punit=i.unit and i.lang_type='"+langType+"' "
					+ " left join DCP_UNIT_lang j on d.EID=j.EID and d.baseunit=j.unit and j.lang_type='"+langType+"' "
					+ " LEFT JOIN DCP_UNIT HH ON HH.EID = d.EID AND HH.unit = d.punit "
					+ " left join DCP_GOODS_FEATURE_LANG fn on a.eid=fn.eid and d.pluno=fn.pluno and d.featureno=fn.featureno  and fn.lang_type='"+req.getLangType()+"' "
					+ " left join DCP_GOODS_UNIT_LANG gul on a.eid=gul.eid and d.pluno=gul.pluno and d.punit=gul.ounit and gul.lang_type='"+langType+"'"
					+ " left join dcp_goodsimage image on image.eid=a.eid and image.pluno=d.pluno and image.apptype='ALL' "
			);
			
			//2018-11-20 添加 日期查询条件
			sqlbuf.append(" WHERE a.BDATE between "+beginDate +" and "+endDate+" ");
			
			if(status != null && status.length() > 0) {
				sqlbuf.append(" and a.STATUS='"+status+"'");
			}
			if(keyTxt != null && keyTxt.length() > 0) {
				sqlbuf.append(" and (a.TOT_AMT LIKE '%%"+keyTxt+"%%' OR a.TOT_DISTRIAMT LIKE '%%"+keyTxt+"%%' OR a.TOT_PQTY LIKE '%%"+keyTxt+"%%' OR a.SStockOutNO LIKE '%%"+keyTxt+"%%' OR a.MEMO LIKE '%%"+keyTxt+"%%' "
						+ "  or a.process_ERP_No like '%%"+keyTxt+"%%'  )");
			}
			if(supplier != null && supplier.length() > 0) {
				sqlbuf.append(" and a.SUPPLIER='"+supplier+"'");
			}
			
			sqlbuf.append(" and a.SHOPID='"+shopId+"'");
			sqlbuf.append(" and a.EID='"+eId+"'");
			
			sqlbuf.append(" and a.SStockOutNO in ("
					+" select SStockOutNO from"
					+" (select dense_rank() over (order BY aa.SStockOutNO) rn,aa.SStockOutNO FROM DCP_SStockOut aa " +
					" INNER JOIN DCP_SStockOut_DETAIL d ON aa.EID = d.EID AND aa.SHOPID = d.SHOPID AND aa.sStockOutno = d.sStockOutno AND aa.BDATE = d.BDATE" +
					" INNER JOIN DCP_GOODS f ON d.EID = f.EID AND d.pluno = f.pluno"
			);
			//2018-11-20 添加 日期查询条件
			sqlbuf.append(" WHERE aa.BDATE between "+beginDate +" and "+endDate+" ");
			
			if(status != null && status.length() > 0) {
				sqlbuf.append(" and aa.STATUS='"+status+"'");
			}
			if(keyTxt != null && keyTxt.length() > 0) {
				sqlbuf.append(" and (aa.TOT_AMT LIKE '%%"+keyTxt+"%%' OR aa.TOT_PQTY LIKE '%%"+keyTxt+"%%' OR aa.SStockOutNO LIKE '%%"+keyTxt+"%%' OR aa.MEMO LIKE '%%"+keyTxt+"%%' "
						+ "   or aa.process_ERP_No like '%%"+keyTxt+"%%'  )");
			}
			if(supplier != null && supplier.length() > 0) {
				sqlbuf.append(" and aa.SUPPLIER='"+supplier+"'");
			}
			
			sqlbuf.append(" and aa.SHOPID='"+shopId+"'");
			sqlbuf.append(" and aa.EID='"+eId+"'"
					+ " order by aa.status asc ,aa.bdate desc ,aa.sstockoutno desc ");
			sqlbuf.append(" ) where rn>" + startRow + " AND rn <= " + (startRow+pageSize));
			sqlbuf.append(" )");
			sqlbuf.append(" order by A_status asc ,B_bDate desc ,A_sStockOutNO desc ");
			sqlbuf.append(" ) tbl");
			sql=sqlbuf.toString();
		}*/
		
		return "";
	}
	
	
}
