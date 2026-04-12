package com.dsc.spos.service.imp.json;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.json.cust.req.DCP_PayDateQueryReq;
import com.dsc.spos.json.cust.res.DCP_PayDateQueryRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.SUtil;
import com.google.gson.reflect.TypeToken;

import cn.hutool.core.util.StrUtil;

public class DCP_PayDateQuery extends SPosBasicService<DCP_PayDateQueryReq, DCP_PayDateQueryRes > {
	
	Logger logger = LogManager.getLogger(SPosAdvanceService.class);
	public List<DataProcessBean> pData = new ArrayList<DataProcessBean>();
	
	@Override
	protected boolean isVerifyFail(DCP_PayDateQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_PayDateQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PayDateQueryReq>(){};
	}

	@Override
	protected DCP_PayDateQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_PayDateQueryRes();
	}

	@Override
	protected DCP_PayDateQueryRes processJson(DCP_PayDateQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_PayDateQueryRes res = null;
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
			String payType="";
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
			{
				res.setDatas(new ArrayList<DCP_PayDateQueryRes.PayDate>());      
				
				for (Map<String, Object> oneData : getQDataDetail) 
				{
					DCP_PayDateQueryRes.PayDate oneLv1 = res.new PayDate();
					eID = oneData.get("EID").toString(); 
					billNo = oneData.get("PAYDATENO").toString();
					payType = oneData.get("PAYDATE_TYPE").toString();
					oneLv1.setPayDateNo(billNo); 
					oneLv1.setPayDateBase(oneData.get("PAYDATEBASE").toString()); 
					oneLv1.setPayDateType(oneData.get("PAYDATE_TYPE").toString());
					oneLv1.setPayDateName(oneData.get("NAME").toString());
					oneLv1.setPSeasons(oneData.get("PSEASONS").toString()); 
					oneLv1.setPMonths(oneData.get("PMONTHS").toString()); 
					oneLv1.setPDays(oneData.get("PDAYS").toString()); 
					oneLv1.setDueDateBase(oneData.get("DUEDATEBASE").toString()); 
					oneLv1.setDSeasons(oneData.get("DSEASONS").toString());
					oneLv1.setDMonths(oneData.get("DMONTHS").toString());
					oneLv1.setDDays(oneData.get("DDAYS").toString());
					oneLv1.setStatus(oneData.get("STATUS").toString());
					
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
					String pType = payType;
					//多语言 先筛选出符合当前条件的数据
					getQLangData = getQLangDataDetail.stream()
							.filter(LangData -> id.equals(LangData.get("EID").toString())
									&& bill.equals(LangData.get("PAYDATENO").toString())
									&& pType.equals(LangData.get("PAYDATE_TYPE").toString()))
							.collect(Collectors.toList());
					
					oneLv1.setLang_List(new ArrayList<DCP_PayDateQueryRes.LangName>());
					for (Map<String, Object> oneData2 : getQLangData) {
						DCP_PayDateQueryRes.LangName oneLv2 = res.new LangName();
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
			res.setDatas(new ArrayList<DCP_PayDateQueryRes.PayDate>());
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());//add by 01029 20240703
		}
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_PayDateQueryReq req) throws Exception {
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
		String eId = req.geteId();
		
		if(req.getRequest()!=null)
		{
			key =  req.getRequest().getKeyTxt();
			status = req.getRequest().getStatus();
			type = req.getRequest().getPayDateType();
		}
		
		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append("SELECT * FROM ("
					+ " SELECT ROWNUM AS rn ,  a.*  "
					+ "  , p.NAME as CREATEOPNAME,d.DEPARTNAME as CREATEDEPTNAME,c.NAME as lastModiOpName "
					+ " FROM DCP_PayDate a"
					+ " left join DCP_EMPLOYEE  p on a.eid=p.eid and p.EMPLOYEENO=a.CREATEOPID "
					+ " left join DCP_EMPLOYEE  c on a.eid=c.eid and c.EMPLOYEENO=a.LASTMODIOPID "
					+"  left join DCP_DEPARTMENT_LANG d  on p.eid=d.eid and p.DEPARTMENTNO=d.DEPARTNO and d.lang_type='"+langType+"' "
					+"  left join DCP_PayDate_LANG e  on a.eid=e.eid and a.PAYDATENO=e.PAYDATENO and a.PAYDATE_TYPE=e.PAYDATE_TYPE and e.lang_type='"+langType+"' "
					+ " WHERE a.EID = '"+eId+"' " );  
		
		if(key != null && key.length() > 0){
			sqlbuf.append(" and ( a.PayDateNO  like  "+SUtil.RetLikeStr(key)); 
			sqlbuf.append(" or e.NAME  like  "+SUtil.RetLikeStr(key)+" )"); 
		}
		if(type != null && type.length() > 0)
			sqlbuf.append(" and a.PAYDATE_TYPE like "+  SUtil.RetLikeStr(type));
 
		if(status != null && status.length() > 0)
			sqlbuf.append(" and a.STATUS = '"+status+"' ");
		sqlbuf.append(" ) DBL  WHERE rn > "+startRow +" AND rn  <= "+(startRow + pageSize)+" ");
		sqlbuf.append(" order by PAYDATE_TYPE,PAYDATENO " );
		
		
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
	protected String getCountSql(DCP_PayDateQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String key = null;
		String status = null; 
		String eId = req.geteId();
		String type =null;
		if(req.getRequest()!=null)
		{
			key =  req.getRequest().getKeyTxt();
			status = req.getRequest().getStatus();
			type = req.getRequest().getPayDateType();
		}
		String langType =req.getLangType();
		if(langType==null||langType.isEmpty())
		{
			langType="zh_CN";
		}		
		StringBuffer sqlbuf=new StringBuffer("");
		String sql = "";
		
		sqlbuf.append("select num from( select count(*) AS num  from DCP_PayDate a "
				+"  left join DCP_PayDate_LANG e  on a.eid=e.eid and a.PAYDATENO=e.PAYDATENO and a.PAYDATE_TYPE=e.PAYDATE_TYPE and e.lang_type='"+langType+"' "				
				+ "where a.EID='"+eId+"'  " );
		
		if(key != null && key.length() > 0){
			sqlbuf.append(" and ( a.PayDateNO  like  "+SUtil.RetLikeStr(key)); 
			sqlbuf.append(" or e.NAME  like  "+SUtil.RetLikeStr(key)+" )"); 
		}
		if(type != null && type.length() > 0)
			sqlbuf.append(" and  a.PAYDATE_TYPE like "+SUtil.RetLikeStr(type));
		if(status != null && status.length() > 0)
			sqlbuf.append(" and a.STATUS = '"+status+"' ");
		sqlbuf.append(" )");
		
		sql= sqlbuf.toString();
		return sql;
	}
	
	
	 
	protected String getLangTypeQuerySql(DCP_PayDateQueryReq req) throws Exception {
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
			key =  req.getRequest().getKeyTxt();
			status = req.getRequest().getStatus();
			type = req.getRequest().getPayDateType();
		}
		
		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append("SELECT DBL.*,b.* FROM ("
					+ " SELECT ROWNUM AS rn   "
					+ "  , a.eid,a.PayDateNO,a.PAYDATE_TYPE   "
					+ " FROM DCP_PayDate a"
					
					+ " left join DCP_EMPLOYEE  p on a.eid=p.eid and p.EMPLOYEENO=a.CREATEOPID "
					+ " left join DCP_EMPLOYEE  c on a.eid=c.eid and c.EMPLOYEENO=a.LASTMODIOPID "
					+"  left join DCP_DEPARTMENT_LANG d  on p.eid=d.eid and p.DEPARTMENTNO=d.DEPARTNO and d.lang_type='"+langType+"' "
					+"  left join DCP_PayDate_LANG e  on a.eid=e.eid and a.PAYDATENO=e.PAYDATENO and a.PAYDATE_TYPE=e.PAYDATE_TYPE and e.lang_type='"+langType+"' "
					+ " WHERE a.EID = '"+eId+"' " );  
		
		if(key != null && key.length() > 0){
			sqlbuf.append(" and ( a.PayDateNO  like  "+SUtil.RetLikeStr(key)); 
			sqlbuf.append(" or e.NAME  like  "+SUtil.RetLikeStr(key)+" )"); 
		}
		if(type != null && type.length() > 0)
			sqlbuf.append(" and a.PAYDATE_TYPE like "+SUtil.RetLikeStr(type));
		if(status != null && status.length() > 0)
			sqlbuf.append(" and a.STATUS = '"+status+"' ");
		sqlbuf.append("    and  ROWNUM > "+startRow +" AND ROWNUM  <= "+(startRow + pageSize)+" ");
		sqlbuf.append(" ) DBL   ");  //这里先查出和主数据一样的数据 然后关联语言档
		sqlbuf.append("  left join DCP_PayDate_LANG b  on DBL.eid=b.eid and DBL.PayDateNO=b.PayDateNO and DBL.PAYDATE_TYPE=b.PAYDATE_TYPE   ");
		sql = sqlbuf.toString();
		//logger.info(sql);   
		return sql;
	}
	
	
}
