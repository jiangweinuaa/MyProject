package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.json.cust.req.DCP_InvoiceTypeQueryReq;
import com.dsc.spos.json.cust.res.DCP_InvoiceTypeQueryRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.google.gson.reflect.TypeToken;

public class DCP_InvoiceTypeQuery extends SPosBasicService<DCP_InvoiceTypeQueryReq, DCP_InvoiceTypeQueryRes > {
	
	Logger logger = LogManager.getLogger(SPosAdvanceService.class);
	public List<DataProcessBean> pData = new ArrayList<DataProcessBean>();
	
	@Override
	protected boolean isVerifyFail(DCP_InvoiceTypeQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_InvoiceTypeQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_InvoiceTypeQueryReq>(){};
	}

	@Override
	protected DCP_InvoiceTypeQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_InvoiceTypeQueryRes();
	}

	@Override
	protected DCP_InvoiceTypeQueryRes processJson(DCP_InvoiceTypeQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_InvoiceTypeQueryRes res = null;
		res = this.getResponse();
		String sql = null;
		try {
			
			sql = this.getCountSql(req);	
			String[] condCountValues = { }; //查詢條件
			List<Map<String, Object>> getReasonCount = this.doQueryData(sql, condCountValues);
			int totalRecords;								//总笔数
			int totalPages;									//总页数
			if (getReasonCount != null && getReasonCount.isEmpty() == false) 
			{ 			
				Map<String, Object> total = getReasonCount.get(0);
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
			
			sql = this.getQuerySql(req);
			
			String[] conditionValues1 = { }; //查詢條件
			
			List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, conditionValues1);
			sql = this.getLangTypeQuerySql(req);
			List<Map<String, Object>> getQLangDataDetail=this.doQueryData(sql, conditionValues1);
			List<Map<String, Object>> getQLangData = null;
			String taxArea="";
			String invoiceCode="";
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
			{
				res.setDatas(new ArrayList<DCP_InvoiceTypeQueryRes.InvoiceType>());      
				
				for (Map<String, Object> oneData : getQDataDetail) 
				{
					DCP_InvoiceTypeQueryRes.InvoiceType oneLv1 = res.new InvoiceType();
					taxArea = oneData.get("TAXAREA").toString(); 
					invoiceCode = oneData.get("INVOICECODE").toString();
					oneLv1.setTaxArea(taxArea);
					oneLv1.setInvoiceCode(invoiceCode);
					oneLv1.setInvoiceName(oneData.get("NAME").toString());
					oneLv1.setInvoiceGenre(oneData.get("INVOICE_GENRE").toString());
					oneLv1.setInvoiceType(oneData.get("INVOICE_TYPE").toString());
					oneLv1.setStatus(oneData.get("STATUS").toString());
					oneLv1.setTaxCalType(oneData.get("TAXCALTYPE").toString());
					
					oneLv1.setCreatorID(oneData.get("CREATEOPID").toString());
					oneLv1.setCreatorName(oneData.get("CREATEOPNAME").toString());
					oneLv1.setCreatorDeptID(oneData.get("CREATEDEPTID").toString());
					oneLv1.setCreatorDeptName(oneData.get("CREATEDEPTNAME").toString());
					oneLv1.setCreateDatetime(oneData.get("CREATETIME").toString());
					oneLv1.setLastmodifyID(oneData.get("LASTMODIOPID").toString());
					oneLv1.setLastmodifyName(oneData.get("LASTMODIOPNAME").toString());
					oneLv1.setLastmodifyDatetime(oneData.get("LASTMODITIME").toString());
					String tax = taxArea;
					String invoice = invoiceCode;
					
					//多语言 先筛选出符合当前条件的数据
					getQLangData = getQLangDataDetail.stream()
							.filter(LangData -> tax.equals(LangData.get("TAXAREA").toString())
									&& invoice.equals(LangData.get("INVOICECODE").toString()) )
							.collect(Collectors.toList());
					
					oneLv1.setInvoiceName_lang(new ArrayList<DCP_InvoiceTypeQueryRes.InvoiceTypeName>());
					for (Map<String, Object> oneData2 : getQLangData) {
						DCP_InvoiceTypeQueryRes.InvoiceTypeName oneLv2 = res.new InvoiceTypeName();
						oneLv2.setName(oneData2.get("INVOICE_NAME").toString());
				        oneLv2.setLangType(oneData2.get("LANG_TYPE").toString());
				        oneLv1.getInvoiceName_lang().add(oneLv2);
				        oneLv2 = null;
					}
					res.getDatas().add(oneLv1);
					oneLv1=null;
				}
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			res.setDatas(new ArrayList<DCP_InvoiceTypeQueryRes.InvoiceType>());
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());//add by 01029 20240703
		}
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_InvoiceTypeQueryReq req) throws Exception {
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
		
		String invoice = null;
		String status = null; 
		String eId = req.geteId();
		
		if(req.getRequest()!=null)
		{
			invoice =  req.getRequest().getKeyTxt();
			status = req.getRequest().getStatus();
			
		}
		
		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append("SELECT * FROM ("
					+ " SELECT ROWNUM AS rn ,  a.*  "
					+ "  , p.NAME as CREATEOPNAME,d.DEPARTNAME as CREATEDEPTNAME,c.NAME as lastModiOpName "
					+ " FROM DCP_InvoiceType a"
					+ " left join DCP_EMPLOYEE  p on a.eid=p.eid and p.EMPLOYEENO=a.CREATEOPID "
					+ " left join DCP_EMPLOYEE  c on a.eid=c.eid and c.EMPLOYEENO=a.LASTMODIOPID "
					+"  left join DCP_DEPARTMENT_LANG d  on p.eid=d.eid and p.DEPARTMENTNO=d.DEPARTNO and d.lang_type='"+langType+"' "
					+ " WHERE a.EID = '"+eId+"' " );  
		
		if(invoice != null && invoice.length() > 0)
			sqlbuf.append(" and a.INVOICECODE = '"+invoice+"' ");
		
		if(status != null && status.length() > 0)
			sqlbuf.append(" and a.STATUS = '"+status+"' ");
		
		sqlbuf.append(" ) DBL  WHERE rn > "+startRow +" AND rn  <= "+(startRow + pageSize)+" ");
		
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
	protected String getCountSql(DCP_InvoiceTypeQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String invoice = null;
		String status = null; 
		String eId = req.geteId();
		
		if(req.getRequest()!=null)
		{
			invoice =  req.getRequest().getKeyTxt();
			status = req.getRequest().getStatus();
			
		}
				
		StringBuffer sqlbuf=new StringBuffer("");
		String sql = "";
		
		sqlbuf.append("select num from( select count(*) AS num  from DCP_InvoiceType "
				+ "where EID='"+eId+"'  " );
		
		if(invoice != null && invoice.length() > 0)
			sqlbuf.append(" and INVOICECODE = '"+invoice+"' ");
		
		if(status != null && status.length() > 0)
			sqlbuf.append(" and STATUS = '"+status+"' ");
		
		sqlbuf.append(" )");
		
		sql= sqlbuf.toString();
		return sql;
	}
	
	
	 
	protected String getLangTypeQuerySql(DCP_InvoiceTypeQueryReq req) throws Exception {
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
		
		String invoice = null;
		String status = null; 
		String eId = req.geteId();
		
		if(req.getRequest()!=null)
		{
			invoice =  req.getRequest().getKeyTxt();
			status = req.getRequest().getStatus();
			
		}
		
		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append("SELECT DBL.*,b.* FROM ("
					+ " SELECT ROWNUM AS rn   "
					+ "  , a.eid,a.TAXAREA,a.INVOICECODE  "
					+ " FROM DCP_InvoiceType a"
					
					+ " left join DCP_EMPLOYEE  p on a.eid=p.eid and p.EMPLOYEENO=a.CREATEOPID "
					+ " left join DCP_EMPLOYEE  c on a.eid=c.eid and c.EMPLOYEENO=a.LASTMODIOPID "
					+"  left join DCP_DEPARTMENT_LANG d  on p.eid=d.eid and p.DEPARTMENTNO=d.DEPARTNO and d.lang_type='"+langType+"' "
					+ " WHERE a.EID = '"+eId+"' " );  
		
		if(invoice != null && invoice.length() > 0)
			sqlbuf.append(" and a.INVOICECODE = '"+invoice+"' ");
		
		if(status != null && status.length() > 0)
			sqlbuf.append(" and a.STATUS = '"+status+"' ");
		sqlbuf.append("    and  ROWNUM > "+startRow +" AND ROWNUM  <= "+(startRow + pageSize)+" ");
		sqlbuf.append(" ) DBL   ");  //这里先查出和主数据一样的数据 然后关联语言档
		sqlbuf.append("  left join DCP_INVOICETYPE_LANG b  on DBL.eid=b.eid and DBL.TAXAREA=b.TAXAREA and DBL.INVOICECODE=b.INVOICECODE  ");
		sql = sqlbuf.toString();
		//logger.info(sql);   
		return sql;
	}
	
	
}
