package com.dsc.spos.service.imp.json;



import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.json.cust.req.DCP_SupLicenseApplyQueryReq;
import com.dsc.spos.json.cust.res.DCP_SupLicenseApplyQueryRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.DateFormatUtils;
import com.dsc.spos.utils.DateUtils;
import com.dsc.spos.utils.SUtil;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;
 

public class DCP_SupLicenseApplyQuery extends SPosBasicService<DCP_SupLicenseApplyQueryReq, DCP_SupLicenseApplyQueryRes > {
	
	Logger logger = LogManager.getLogger(SPosAdvanceService.class);
	public List<DataProcessBean> pData = new ArrayList<DataProcessBean>();
	
	@Override
	protected boolean isVerifyFail(DCP_SupLicenseApplyQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_SupLicenseApplyQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_SupLicenseApplyQueryReq>(){};
	}

	@Override
	protected DCP_SupLicenseApplyQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_SupLicenseApplyQueryRes();
	}

	@Override
	protected DCP_SupLicenseApplyQueryRes processJson(DCP_SupLicenseApplyQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_SupLicenseApplyQueryRes res = null;
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
	 
			List<Map<String, Object>> getQLangData = null;
			res.setDatas(new ArrayList<DCP_SupLicenseApplyQueryRes.DataDetail>());
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
			{
				      
				
				for (Map<String, Object> oneData : getQDataDetail) 
				{
					DCP_SupLicenseApplyQueryRes.DataDetail oneLv1 = res.new DataDetail();
 
					oneLv1.setStatus(oneData.get("STATUS").toString());
					oneLv1.setOpType(oneData.get("OPTYPE").toString());
					oneLv1.setBillNo(oneData.get("BILLNO").toString());
					oneLv1.setOrgNo(oneData.get("ORGANIZATIONNO").toString());
					oneLv1.setOrgName(oneData.get("ORGNAME").toString());
					oneLv1.setBDate(oneData.get("BDATE").toString());
					oneLv1.setEmployeeID(oneData.get("EMPLOYEEID").toString());
					oneLv1.setEmployeeName(oneData.get("EMPLOYEENAME").toString());
					oneLv1.setDepartID(oneData.get("DEPARTID").toString());
					oneLv1.setDepartName(oneData.get("DEPARTNAME").toString());
					oneLv1.setMemo(oneData.get("MEMO").toString());
					oneLv1.setCreatorID(oneData.get("CREATEOPID").toString());
					oneLv1.setCreatorName(oneData.get("CREATEOPNAME").toString());
					oneLv1.setCreatorDeptID(oneData.get("CREATEDEPTID").toString());
					oneLv1.setCreatorDeptName(oneData.get("CREATEDEPTNAME").toString());
					oneLv1.setCreate_DateTime(oneData.get("CREATETIME").toString());
					oneLv1.setLastModifyID(oneData.get("LASTMODIOPID").toString());
					oneLv1.setLastModifyName(oneData.get("LASTMODIOPNAME").toString());
					oneLv1.setLastModify_DateTime(oneData.get("LASTMODITIME").toString());
					oneLv1.setOwnOPID(oneData.get("OWNOPID").toString());
					oneLv1.setOwnOPName(oneData.get("OWNOPNAME").toString());
					oneLv1.setOwnDeptID(oneData.get("OWNDEPTID").toString());
					oneLv1.setOwnDeptName(oneData.get("OWNDEPTNAME").toString());
					oneLv1.setConfirmBy(oneData.get("CONFIRMBY").toString());
					oneLv1.setConfirmByName(oneData.get("CONFIRMBYNAME").toString());
					oneLv1.setConfirm_DateTime(oneData.get("CONFIRMTIME").toString());
					oneLv1.setCancelBy(oneData.get("CANCELBY").toString());
					oneLv1.setCancelByName(oneData.get("CANCELBYNAME").toString());
					oneLv1.setCancel_DateTime(oneData.get("CANCELTIME").toString());
					 
					res.getDatas().add(oneLv1);
					oneLv1=null;
				}
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			res.setDatas(new ArrayList<DCP_SupLicenseApplyQueryRes.DataDetail>());
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"DCP_SupLicenseApplyQuery"+ e.getCause().toString());//add by 01029 20240703
		}
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_SupLicenseApplyQueryReq req) throws Exception {
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
		String supplier = null;
		String status = null; 
		String beginDate = null;
		String endDate = null;
		String eId = req.geteId();
		
		if(req.getRequest()!=null)
		{
			key =  req.getRequest().getBillNo();
			status = req.getRequest().getStatus();
			supplier = req.getRequest().getSupplier();
			beginDate = req.getRequest().getBeginDate();
			endDate = req.getRequest().getEndDate();
	 
		}
		
		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append("SELECT * FROM ("
					+ " SELECT ROWNUM AS rn ,  a.* ,J.ORG_NAME AS ORGNAME "
					+ "  , p.NAME as CREATEOPNAME,d.DEPARTNAME as CREATEDEPTNAME,c.NAME as lastModiOpName "
					+ "  , p1.NAME as EMPLOYEENAME,d1.DEPARTNAME as DEPARTNAME, p2.NAME as   CONFIRMBYNAME, p3.NAME as   CANCELBYNAME"
					+ "  , p4.NAME as OWNOPNAME,d4.DEPARTNAME as OWNDEPTNAME "
					+ " FROM DCP_SUPLICENSECHANGE a "
					//+ " left join DCP_SUPLICENSECHANGE_DETAIL  b on a.eid=b.eid and b.BILLNO=a.BILLNO "
					+ " left join DCP_EMPLOYEE  p on a.eid=p.eid and p.EMPLOYEENO=a.CREATEOPID "
					+ " left join DCP_EMPLOYEE  c on a.eid=c.eid and c.EMPLOYEENO=a.LASTMODIOPID "
					+"  left join DCP_DEPARTMENT_LANG d  on p.eid=d.eid and p.DEPARTMENTNO=d.DEPARTNO and d.lang_type='"+langType+"' "
					+"   LEFT JOIN DCP_ORG_LANG j on j.EID=a.EID AND j.ORGANIZATIONNO=a.ORGANIZATIONNO AND j.LANG_TYPE='" + langType +"' " 
					+ " left join DCP_EMPLOYEE  p1 on a.eid=p1.eid and p1.EMPLOYEENO=a.EMPLOYEEID "
					+ "  left join DCP_DEPARTMENT_LANG d1  on a.eid=d1.eid and a.DEPARTID=d1.DEPARTNO and d1.lang_type='"+langType+"' "
					+ " left join DCP_EMPLOYEE  p2 on a.eid=p2.eid and p2.EMPLOYEENO=a.CONFIRMBY "
					+ " left join DCP_EMPLOYEE  p3 on a.eid=p3.eid and p3.EMPLOYEENO=a.CANCELBY "
					+ " left join DCP_EMPLOYEE  p4 on a.eid=p4.eid and p4.EMPLOYEENO=a.OWNOPID "
					+"  left join DCP_DEPARTMENT_LANG d4  on p4.eid=d4.eid and p4.DEPARTMENTNO=d4.DEPARTNO and d4.lang_type='"+langType+"' "
					+ " WHERE a.EID = '"+eId+"' " );  
		sqlbuf.append(" and a.ORGANIZATIONNO = '"+req.getOrganizationNO()+"' "); 
		if(key != null && key.length() > 0){
			sqlbuf.append(" and  a.BILLNO  like  "+SUtil.RetLikeStr(key)); 
		}
		if(supplier != null && supplier.length() > 0)
			sqlbuf.append(" and a.SUPPLIER like "+  SUtil.RetLikeStr(supplier));
		sqlbuf.append(SUtil.RetDateCon("a.BDATE",beginDate,endDate));
		if(status != null && status.length() > 0)
			sqlbuf.append(" and a.STATUS = '"+status+"' ");
		sqlbuf.append(" ) DBL  WHERE rn > "+startRow +" AND rn  <= "+(startRow + pageSize)+" ");
		sqlbuf.append(" order by  BDate DESC , BILLNO DESC  " );
		
		
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
	protected String getCountSql(DCP_SupLicenseApplyQueryReq req) throws Exception {
		// TODO Auto-generated method stub
 
		String eId = req.geteId();
		String key = null;
		String supplier = null;
		String status = null; 
		String beginDate = null;
		String endDate = null;
 
		
		if(req.getRequest()!=null)
		{
			key =  req.getRequest().getBillNo();
			status = req.getRequest().getStatus();
			supplier = req.getRequest().getSupplier();
			beginDate = req.getRequest().getBeginDate();
			endDate = req.getRequest().getEndDate();
		}
 	
		StringBuffer sqlbuf=new StringBuffer("");
		String sql = "";
		
		sqlbuf.append("select num from( select count(* ) AS num    "	
				+ " FROM DCP_SUPLICENSECHANGE a "
				//+ " left join DCP_SUPLICENSECHANGE_DETAIL  b on a.eid=b.eid and b.BILLNO=a.BILLNO "
				+ " where a.EID='"+eId+"'  " );
		sqlbuf.append(" and a.ORGANIZATIONNO = '"+req.getOrganizationNO()+"' "); 
		if(key != null && key.length() > 0){
			sqlbuf.append(" and   a.BILLNO  like  "+SUtil.RetLikeStr(key)); 
		}
		if(supplier != null && supplier.length() > 0)
			sqlbuf.append(" and a.SUPPLIER like "+  SUtil.RetLikeStr(supplier));
		sqlbuf.append(SUtil.RetDateCon("a.BDATE",beginDate,endDate));
		if(status != null && status.length() > 0)
			sqlbuf.append(" and a.STATUS = '"+status+"' ");
		sqlbuf.append(" )");
		
		sql= sqlbuf.toString();
		return sql;
	}
	
	
	  
	
	
}
