package com.dsc.spos.service.imp.json;


 
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
 

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.json.cust.req.DCP_QualityCheckProQueryReq;
import com.dsc.spos.json.cust.req.DCP_QualityCheckProQueryReq.levelRequest;
import com.dsc.spos.json.cust.req.DCP_SStockInQueryReq.levelElm;
import com.dsc.spos.json.cust.res.DCP_QualityCheckProQueryRes;
import com.dsc.spos.json.cust.res.DCP_QualityCheckProQueryRes.QCDetail;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.SUtil;
import com.google.gson.reflect.TypeToken;

 

public class DCP_QualityCheckProQuery extends SPosBasicService<DCP_QualityCheckProQueryReq, DCP_QualityCheckProQueryRes > {
	
	Logger logger = LogManager.getLogger(SPosAdvanceService.class);
	public List<DataProcessBean> pData = new ArrayList<DataProcessBean>();
	
	@Override
	protected boolean isVerifyFail(DCP_QualityCheckProQueryReq req) throws Exception {
		boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        levelRequest request=req.getRequest();
		String beginDate = request.getBeginDate();
        String endDate =request.getEndDate();
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
	protected TypeToken<DCP_QualityCheckProQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_QualityCheckProQueryReq>(){};
	}

	@Override
	protected DCP_QualityCheckProQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_QualityCheckProQueryRes();
	}

	@Override
	protected DCP_QualityCheckProQueryRes processJson(DCP_QualityCheckProQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_QualityCheckProQueryRes res = null;
		res = this.getResponse();
		String sql = null;
		try {
			sql = this.getQuerySql(req);
			
			String[] conditionValues1 = { }; //查詢條件
			
			List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, conditionValues1);
			//sql = this.getCountSql(req);	
			//String[] condCountValues = { }; //查詢條件
			//List<Map<String, Object>> getReasonCount = this.doQueryData(sql, condCountValues);
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
			
			
 
			res.setDatas(new ArrayList<DCP_QualityCheckProQueryRes.DataDetail>()); 
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
			{	DCP_QualityCheckProQueryRes.DataDetail oneLv1 = res.new DataDetail();			     			
				for (Map<String, Object> oneData : getQDataDetail) 
				{
					 
					DCP_QualityCheckProQueryRes.QCDetail oneLv2 =  res.new QCDetail();
					 List<QCDetail> proList = new ArrayList()   ;
					oneLv1.setProList(proList);
					
					oneLv2.setQcType(oneData.get("QCTYPE").toString());
					oneLv2.setPendingCqty(oneData.get("QCQTY").toString());
 					
					oneLv1.getProList().add(oneLv2);
					res.getDatas().add(oneLv1);
					oneLv1=null;
					oneLv2=null;
				}
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			res.setDatas(new ArrayList<DCP_QualityCheckProQueryRes.DataDetail>());
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());//add by 01029 20240703
		}
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_QualityCheckProQueryReq req) throws Exception {
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
		 
		String beginDate = null;
		String endDate = null;
		String eId = req.geteId();
		String type =null;
		if(req.getRequest()!=null)
		{
			beginDate = req.getRequest().getBeginDate();
			endDate = req.getRequest().getEndDate();
			
		}
		
		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append("SELECT COUNT( 1 ) OVER() NUM ,DBL.* FROM ("
					+ " SELECT COUNT( 1 )     as QCQTY ,'1' as QCTYPE  "
					//+ "  , p.NAME as CREATEOPNAME,d.DEPARTNAME as CREATEDEPTNAME,c.NAME as lastModiOpName "
					+ " FROM DCP_PURRECEIVE a"
					+ " inner join  DCP_PURRECEIVE_DETAIL b on a.eid=b.eid and a.BILLNO=b.BILLNO "
					
					//+ " left join DCP_EMPLOYEE  p on a.eid=p.eid and p.EMPLOYEENO=a.CREATEOPID "
					//+ " left join DCP_EMPLOYEE  c on a.eid=c.eid and c.EMPLOYEENO=a.LASTMODIOPID "
					//+"  left join DCP_DEPARTMENT_LANG d  on p.eid=d.eid and p.DEPARTMENTNO=d.DEPARTNO and d.lang_type='"+langType+"' "
					+ " WHERE a.EID = '"+eId+"' " );  
		   sqlbuf.append(" and a.BDATE>='"+beginDate+"' and a.BDATE<='"+endDate+"' ");
 
 
			sqlbuf.append(" and b.QCSTATUS =  "+  SUtil.RetTrimStr("1"));
 
		sqlbuf.append(" ) DBL  WHERE ROWNUM > "+startRow +" AND ROWNUM  <= "+(startRow + pageSize)+" ");
		//sqlbuf.append(" order by SUPPLIER,IMGTYPE,ENDDATE " );
		
		
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
	protected String getCountSql(DCP_QualityCheckProQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String beginDate = null;
		String endDate = null;
		String eId = req.geteId();
		String type =null;
		if(req.getRequest()!=null)
		{
			beginDate = req.getRequest().getBeginDate();
			endDate = req.getRequest().getEndDate();
		}
 	
		StringBuffer sqlbuf=new StringBuffer("");
		String sql = "";
		
		sqlbuf.append("select num from( select count(*) AS num  from DCP_BIZPARTNER a "			
				+ "where a.EID='"+eId+"'  " );
				 
		sqlbuf.append(" )");
		
		sql= sqlbuf.toString();
		return sql;
	}
	
	
	 
	 
	
	
}
