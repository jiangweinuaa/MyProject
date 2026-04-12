package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.json.cust.req.DCP_BillDateQueryReq;
import com.dsc.spos.json.cust.res.DCP_BillDateQueryRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.SUtil;
import com.google.gson.reflect.TypeToken;

public class DCP_BillDateQuery extends SPosBasicService<DCP_BillDateQueryReq, DCP_BillDateQueryRes > {
	
	Logger logger = LogManager.getLogger(SPosAdvanceService.class);
	public List<DataProcessBean> pData = new ArrayList<DataProcessBean>();
	
	@Override
	protected boolean isVerifyFail(DCP_BillDateQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_BillDateQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_BillDateQueryReq>(){};
	}

	@Override
	protected DCP_BillDateQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_BillDateQueryRes();
	}

	@Override
	protected DCP_BillDateQueryRes processJson(DCP_BillDateQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_BillDateQueryRes res = null;
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
			String eID="";
			String billNo="";
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
			{
				res.setDatas(new ArrayList<DCP_BillDateQueryRes.BillDate>());      
				
				for (Map<String, Object> oneData : getQDataDetail) 
				{
					DCP_BillDateQueryRes.BillDate oneLv1 = res.new BillDate();
					eID = oneData.get("EID").toString(); 
					billNo = oneData.get("BILLDATENO").toString();
					oneLv1.setBillDateNo(billNo); 
					oneLv1.setBillDateType(oneData.get("BILLTYPE").toString()); 
					oneLv1.setBillDateName(oneData.get("NAME").toString()); 
					oneLv1.setFDate(oneData.get("FDATE").toString()); 
					oneLv1.setAddMonths( oneData.get("ADDMONTHS").toString());
					oneLv1.setStatus(oneData.get("STATUS").toString());
					oneLv1.setAddDays( oneData.get("ADDDAYS").toString());
					
					oneLv1.setCreatorID(oneData.get("CREATEOPID").toString());
					oneLv1.setCreatorName(oneData.get("CREATEOPNAME").toString());
					oneLv1.setCreatorDeptID(oneData.get("CREATEDEPTID").toString());
					oneLv1.setCreatorDeptName(oneData.get("CREATEDEPTNAME").toString());
					oneLv1.setCreate_Datetime (oneData.get("CREATETIME").toString());
					oneLv1.setLastModifyID(oneData.get("LASTMODIOPID").toString());
					oneLv1.setLastModifyName(oneData.get("LASTMODIOPNAME").toString());
					oneLv1.setLastModify_Datetime(oneData.get("LASTMODITIME").toString());
					String id = eID;
					String bill = billNo;
					
					//多语言 先筛选出符合当前条件的数据
					getQLangData = getQLangDataDetail.stream()
							.filter(LangData -> id.equals(LangData.get("EID").toString())
									&& bill.equals(LangData.get("BILLDATENO").toString()) )
							.collect(Collectors.toList());
					
					oneLv1.setLang_List(new ArrayList<DCP_BillDateQueryRes.LangName>());
					for (Map<String, Object> oneData2 : getQLangData) {
						DCP_BillDateQueryRes.LangName oneLv2 = res.new LangName();
						oneLv2.setName(oneData2.get("NAME").toString());
				        oneLv2.setLangType(oneData2.get("LANG_TYPE").toString());
				        oneLv1.getLang_List().add(oneLv2);
				        oneLv2 = null;
					}
					res.getDatas().add(oneLv1);
					oneLv1=null;
				}
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			res.setDatas(new ArrayList<DCP_BillDateQueryRes.BillDate>());
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());//add by 01029 20240703
		}
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_BillDateQueryReq req) throws Exception {
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
		
		if(req.getRequest()!=null)
		{
			key =  req.getRequest().getKeyTxt();
			status = req.getRequest().getStatus();
			
		}
		
		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append("SELECT * FROM ("
					+ " SELECT ROWNUM AS rn ,  a.*  "
					+ "  , p.NAME as CREATEOPNAME,d.DEPARTNAME as CREATEDEPTNAME,c.NAME as lastModiOpName "
					+ " FROM DCP_BillDate a"
					+ " left join DCP_EMPLOYEE  p on a.eid=p.eid and p.EMPLOYEENO=a.CREATEOPID "
					+ " left join DCP_EMPLOYEE  c on a.eid=c.eid and c.EMPLOYEENO=a.LASTMODIOPID "
					+"  left join DCP_DEPARTMENT_LANG d  on p.eid=d.eid and p.DEPARTMENTNO=d.DEPARTNO and d.lang_type='"+langType+"' "
					+"  left join DCP_BillDate_LANG e  on a.eid=e.eid and a.BILLDATENO=e.BILLDATENO and e.lang_type='"+langType+"' "
					+ " WHERE a.EID = '"+eId+"' " );  
		
		if(key != null && key.length() > 0){
			sqlbuf.append(" and ( a.BILLDATENO  like  "+SUtil.RetLikeStr(key)); 
			sqlbuf.append(" or e.NAME  like  "+SUtil.RetLikeStr(key)+" )"); 
		}
 
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
	protected String getCountSql(DCP_BillDateQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String key = null;
		String status = null; 
		String eId = req.geteId();
		String langType =req.getLangType();
		if(langType==null||langType.isEmpty())
		{
			langType="zh_CN";
		}
		if(req.getRequest()!=null)
		{
			key =  req.getRequest().getKeyTxt();
			status = req.getRequest().getStatus();
			
		}
				
		StringBuffer sqlbuf=new StringBuffer("");
		String sql = "";
		
		sqlbuf.append("select num from( select count(*) AS num  from DCP_BillDate a "
				+"  left join DCP_BillDate_LANG e  on a.eid=e.eid and a.BILLDATENO=e.BILLDATENO and e.lang_type='"+langType+"' "
				
				+ "where a.EID='"+eId+"'  " );
		
		if(key != null && key.length() > 0){
 
			sqlbuf.append(" and ( a.BILLDATENO  like  "+SUtil.RetLikeStr(key)); 
			sqlbuf.append(" or e.NAME  like  "+SUtil.RetLikeStr(key)+" )"); 
		}
		if(status != null && status.length() > 0)
			sqlbuf.append(" and  a.STATUS = '"+status+"' ");
		sqlbuf.append(" )");
		
		sql= sqlbuf.toString();
		return sql;
	}
	
	
	 
	protected String getLangTypeQuerySql(DCP_BillDateQueryReq req) throws Exception {
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
		
		if(req.getRequest()!=null)
		{
			key =  req.getRequest().getKeyTxt();
			status = req.getRequest().getStatus();
			
		}
		
		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append("SELECT DBL.*,b.* FROM ("
					+ " SELECT ROWNUM AS rn   "
					+ "  , a.eid,a.BILLDATENO   "
					+ " FROM DCP_BillDate a"
					
					+ " left join DCP_EMPLOYEE  p on a.eid=p.eid and p.EMPLOYEENO=a.CREATEOPID "
					+ " left join DCP_EMPLOYEE  c on a.eid=c.eid and c.EMPLOYEENO=a.LASTMODIOPID "
					+"  left join DCP_DEPARTMENT_LANG d  on p.eid=d.eid and p.DEPARTMENTNO=d.DEPARTNO and d.lang_type='"+langType+"' "
					+"  left join DCP_BillDate_LANG e  on a.eid=e.eid and a.BILLDATENO=e.BILLDATENO and e.lang_type='"+langType+"' "
					+ " WHERE a.EID = '"+eId+"' " );  
		
		if(key != null && key.length() > 0){
			sqlbuf.append(" and ( a.BILLDATENO  like  "+SUtil.RetLikeStr(key)); 
			sqlbuf.append(" or e.NAME  like  "+SUtil.RetLikeStr(key)+" )"); 
		}
		if(status != null && status.length() > 0)
			sqlbuf.append(" and a.STATUS = '"+status+"' ");
		sqlbuf.append("    and  ROWNUM > "+startRow +" AND ROWNUM  <= "+(startRow + pageSize)+" ");
		sqlbuf.append(" ) DBL   ");  //这里先查出和主数据一样的数据 然后关联语言档
		sqlbuf.append("  left join DCP_BillDate_LANG b  on DBL.eid=b.eid and DBL.BILLDATENO=b.BILLDATENO   ");
		sql = sqlbuf.toString();
		//logger.info(sql);   
		return sql;
	}
	
	
}
