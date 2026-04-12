package com.dsc.spos.service.imp.json;
 
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.json.cust.req.DCP_SupLicenseApplyDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_SupLicenseApplyDetailQueryRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.SUtil;
import com.google.gson.reflect.TypeToken;

 
public class DCP_SupLicenseApplyDetailQuery extends SPosBasicService<DCP_SupLicenseApplyDetailQueryReq, DCP_SupLicenseApplyDetailQueryRes > {
	
	Logger logger = LogManager.getLogger(SPosAdvanceService.class);
	public List<DataProcessBean> pData = new ArrayList<DataProcessBean>();
	
	@Override
	protected boolean isVerifyFail(DCP_SupLicenseApplyDetailQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_SupLicenseApplyDetailQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_SupLicenseApplyDetailQueryReq>(){};
	}

	@Override
	protected DCP_SupLicenseApplyDetailQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_SupLicenseApplyDetailQueryRes();
	}

	@Override
	protected DCP_SupLicenseApplyDetailQueryRes processJson(DCP_SupLicenseApplyDetailQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_SupLicenseApplyDetailQueryRes res = null;
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
			sql = this.getOrgQuerySql(req);
			List<Map<String, Object>> getQLangDataDetail=this.doQueryData(sql, conditionValues1);
			List<Map<String, Object>> getQLangData = null;
			String eID="";
			String billNo="";
			String payType="";
			res.setDatas(new ArrayList<DCP_SupLicenseApplyDetailQueryRes.DataDetail>());    
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
			{				 			
				for (Map<String, Object> oneData : getQDataDetail) 
				{
					DCP_SupLicenseApplyDetailQueryRes.DataDetail oneLv1 = res.new DataDetail();
					eID = oneData.get("EID").toString(); 
					billNo = oneData.get("BILLNO").toString();
					//payType = oneData.get("BIZTYPE").toString();
					oneLv1.setStatus(oneData.get("STATUS").toString());
					oneLv1.setOpType(oneData.get("OPTYPE").toString());
					oneLv1.setBillNo(oneData.get("BILLNO").toString());
					oneLv1.setOrgNo(oneData.get("ORGANIZATIONNO").toString());
					oneLv1.setOrgName(oneData.get("ORGANIZATIONNAME").toString());
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
					oneLv1.setCreate_datetime(oneData.get("CREATETIME").toString());
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
 
					String id = eID;
					String bill = billNo;
					String pType = payType;
					//组织先筛选出符合当前条件的数据
					getQLangData = getQLangDataDetail.stream()
							.filter(LangData -> id.equals(LangData.get("EID").toString())
									&& bill.equals(LangData.get("BILLNO").toString()))
							.collect(Collectors.toList());
					
					oneLv1.setDetailList(new ArrayList<DCP_SupLicenseApplyDetailQueryRes.DetailList>());
					for (Map<String, Object> oneData2 : getQLangData) {
						DCP_SupLicenseApplyDetailQueryRes.DetailList oneLv2 = res.new DetailList();
						oneLv2.setItem (oneData2.get("ITEM").toString());
						oneLv2.setSupplierNo (oneData2.get("SUPPLIER").toString());
						oneLv2.setSupplierName (oneData2.get("SNAME").toString());
						oneLv2.setLicenseType (oneData2.get("LICENSETYPE").toString());
						oneLv2.setLicenseNo (oneData2.get("LICENSENO").toString());
						oneLv2.setBeginDate (oneData2.get("BEGINDATE").toString());
						oneLv2.setEndDate (oneData2.get("ENDDATE").toString());
						//oneLv2.setStatus (oneData2.get("STATUS").toString());
						oneLv2.setLicenseImg (oneData2.get("LICENSEIMG").toString());

				        oneLv1.getDetailList().add(oneLv2);
				        oneLv2 = null;
					}
					res.getDatas().add(oneLv1);
					oneLv1=null;
				}
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			res.setDatas(new ArrayList<DCP_SupLicenseApplyDetailQueryRes.DataDetail>());
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());//add by 01029 20240703
		}
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_SupLicenseApplyDetailQueryReq req) throws Exception {
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
		String type = null;
		String status = null; 
		String isBillQuery = null;
		String eId = req.geteId();
		
		if(req.getRequest()!=null)
		{
			key =  req.getRequest().getBillNo();
 
		}
		
		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append("SELECT * FROM ("
					+ " SELECT ROWNUM AS rn ,  a.*,ORG_NAME AS ORGANIZATIONNAME ,b.NAME as EMPLOYEENAME, e.DEPARTNAME as DEPARTNAME  "
					+ "  , p.NAME as CREATEOPNAME,d.DEPARTNAME as CREATEDEPTNAME,c.NAME as lastModiOpName "
					+ "  , p1.NAME as EMPLOYEENAME1,d1.DEPARTNAME as DEPARTNAME1, p2.NAME as   CONFIRMBYNAME, p3.NAME as   CANCELBYNAME"
					+ "  , p4.NAME as OWNOPNAME,d4.DEPARTNAME as OWNDEPTNAME "
					+ " FROM DCP_SUPLICENSECHANGE a"
					+ " left join DCP_EMPLOYEE  p on a.eid=p.eid and p.EMPLOYEENO=a.CREATEOPID "
					+ " left join DCP_EMPLOYEE  b on a.eid=b.eid and b.EMPLOYEENO=a.EMPLOYEEID "
					+ " left join DCP_EMPLOYEE  c on a.eid=c.eid and c.EMPLOYEENO=a.LASTMODIOPID "
					+"  left join DCP_DEPARTMENT_LANG e  on b.eid=e.eid and b.DEPARTMENTNO=e.DEPARTNO and e.lang_type='"+langType+"' "
					+"  left join DCP_DEPARTMENT_LANG d  on p.eid=d.eid and p.DEPARTMENTNO=d.DEPARTNO and d.lang_type='"+langType+"' "
					+"   LEFT JOIN DCP_ORG_LANG j on j.EID=a.EID AND j.ORGANIZATIONNO=a.ORGANIZATIONNO AND j.LANG_TYPE='" + langType +"' " 
					+ " left join DCP_EMPLOYEE  p1 on a.eid=p1.eid and p1.EMPLOYEENO=a.EMPLOYEEID "
					+ "  left join DCP_DEPARTMENT_LANG d1  on a.eid=d1.eid and a.DEPARTID=d1.DEPARTNO and d1.lang_type='"+langType+"' "
					+ " left join DCP_EMPLOYEE  p2 on a.eid=p2.eid and p2.EMPLOYEENO=a.CONFIRMBY "
					+ " left join DCP_EMPLOYEE  p3 on a.eid=p3.eid and p3.EMPLOYEENO=a.CANCELBY "
					+ " left join DCP_EMPLOYEE  p4 on a.eid=p4.eid and p4.EMPLOYEENO=a.OWNOPID "
					+"  left join DCP_DEPARTMENT_LANG d4  on p4.eid=d4.eid and p4.DEPARTMENTNO=d4.DEPARTNO and d4.lang_type='"+langType+"' "
					+ " WHERE a.EID = '"+eId+"' " );  
		
		if(key != null && key.length() > 0){
			sqlbuf.append(" and  a.BILLNO  like  "+SUtil.RetLikeStr(key)); 
		}
 
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
	protected String getCountSql(DCP_SupLicenseApplyDetailQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String key = null;
		String status = null; 
		String eId = req.geteId();
		String type =null;
		if(req.getRequest()!=null)
		{
			key =  req.getRequest().getBillNo();
 
		}
 	
		StringBuffer sqlbuf=new StringBuffer("");
		String sql = "";
		
		sqlbuf.append("select num from( select count(*) AS num   "	
				+ " FROM DCP_SUPLICENSECHANGE a "
				+ " where a.EID='"+eId+"'  " );
		
		if(key != null && key.length() > 0){
			sqlbuf.append(" and a.BILLNO  like  "+SUtil.RetLikeStr(key)); 
 
		}
		sqlbuf.append(" )DBL  ");
		
		sql= sqlbuf.toString();
		return sql;
	}
	
	
	 
	protected String getOrgQuerySql(DCP_SupLicenseApplyDetailQueryReq req) throws Exception {
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
		String status = null; 
		String eId = req.geteId();
		String type = null;
		if(req.getRequest()!=null)
		{
			key =  req.getRequest().getBillNo();
 
		}
		
		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append("SELECT DBL.*,b.* ,c.SNAME,c.FNAME  FROM ("
					+ " SELECT ROWNUM AS rn   "
					+ "  , a.eid ,a.BILLNO  "
					+ " FROM DCP_SUPLICENSECHANGE a "
					
					
					+ " WHERE a.EID = '"+eId+"' " );  
		
		if(key != null && key.length() > 0){
			sqlbuf.append(" and  a.BILLNO  like  "+SUtil.RetLikeStr(key)); 
 
		}
 
		sqlbuf.append("    and  ROWNUM > "+startRow +" AND ROWNUM  <= "+(startRow + pageSize)+" ");
		sqlbuf.append(" ) DBL   ");  //这里先查出和主数据一样的数据 然后关联语言档
		sqlbuf.append(" left join DCP_SUPLICENSECHANGE_DETAIL  b on DBL.eid=b.eid and b.BILLNO=DBL.BILLNO   ");
		sqlbuf.append(" inner join DCP_bizpartner  c on c.eid=b.eid and c.BIZPARTNERNO=b.SUPPLIER  and c.BIZTYPE='1' ");
		sql = sqlbuf.toString();
		//logger.info(sql);   
		return sql;
	}
	
	
}
