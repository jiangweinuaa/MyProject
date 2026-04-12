package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.json.cust.req.DCP_UnitConvertQueryReq;
import com.dsc.spos.json.cust.res.DCP_UnitConvertQueryRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.google.gson.reflect.TypeToken;

public class DCP_UnitConvertQuery extends SPosBasicService<DCP_UnitConvertQueryReq, DCP_UnitConvertQueryRes > {
	
	Logger logger = LogManager.getLogger(SPosAdvanceService.class);
	public List<DataProcessBean> pData = new ArrayList<DataProcessBean>();
	
	@Override
	protected boolean isVerifyFail(DCP_UnitConvertQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_UnitConvertQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_UnitConvertQueryReq>(){};
	}

	@Override
	protected DCP_UnitConvertQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_UnitConvertQueryRes();
	}

	@Override
	protected DCP_UnitConvertQueryRes processJson(DCP_UnitConvertQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_UnitConvertQueryRes res = null;
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
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
			{
				res.setDatas(new ArrayList<DCP_UnitConvertQueryRes.level1Elm>());      
				
				for (Map<String, Object> oneData : getQDataDetail) 
				{
					DCP_UnitConvertQueryRes.level1Elm oneLv1 = res.new level1Elm();
					
					String oUnit = oneData.get("OUNIT").toString();
					String unit = oneData.get("UNIT").toString();
					String unitRatio = oneData.get("UNITRATIO").toString();
					String status = oneData.get("STATUS").toString(); 
					String createId =  oneData.get("CREATEOPID").toString();// add by 01029 20240703
					String createDep = " ";
					String createTime = oneData.get("CREATETIME").toString();
					String modiId = oneData.get("LASTMODIOPID").toString();
					String modiTime = oneData.get("LASTMODITIME").toString();
					oneLv1.setoUnit(oUnit);
					oneLv1.setUnit(unit);
					oneLv1.setUnitRatio(unitRatio);
					oneLv1.setStatus(status);
					oneLv1.setoUnitName(oneData.get("OUNIT_NAME").toString());
					oneLv1.setUnitName(oneData.get("UNIT_NAME").toString());
					
					oneLv1.setCreateId(createId);
					oneLv1.setCreateIdName(oneData.get("CREATEOPNAME").toString());//add by 01029 20240703
					
					oneLv1.setCreateTime(createTime);
					oneLv1.setModiId(modiId);
					oneLv1.setModiIdName(oneData.get("LASTMODIOPNAME").toString());
					oneLv1.setModiTime(modiTime);
					oneLv1.setCreateDep(oneData.get("DEPARTMENTNO").toString());
					oneLv1.setCreateDepName(oneData.get("DEPARTNAME").toString());
					res.getDatas().add(oneLv1);
					oneLv1=null;
				}
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			res.setDatas(new ArrayList<DCP_UnitConvertQueryRes.level1Elm>());
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());//add by 01029 20240703
		}
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_UnitConvertQueryReq req) throws Exception {
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
		
		String unit = null;
		String oUnit = null;
		String status = null; 
		String eId = req.geteId();
		
		if(req.getRequest()!=null)
		{
			unit =  req.getRequest().getUnit();
			oUnit = req.getRequest().getoUnit();
			status = req.getRequest().getStatus();
			
		}
		
		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append("SELECT * FROM ("
					+ " SELECT ROWNUM AS rn , a.ounit, a.unit , a.unit_ratio  AS unitRatio , a.status,b.UNAME as ounit_NAME,C.UNAME AS unit_NAME  "
					+ "  ,a.createTime,a.createOpId,p.DEPARTMENTNO,d.DEPARTNAME,p.name as createOpName,a.lastModiOpId,e.name as lastModiOpName,a.lastModiTime "
					+ " FROM DCP_unitconvert a"
					+ " left join DCP_EMPLOYEE  p on a.eid=p.eid and a.createOpId=p.EMPLOYEENO "
					+ " left join DCP_EMPLOYEE  e on a.eid=e.eid and a.lastModiOpId=e.EMPLOYEENO "
					+"  left join DCP_unit_lang b on a.eid=b.eid and a.ounit=b.unit and b.lang_type='"+langType+"' "
					+"  left join DCP_unit_lang c  on a.eid=c.eid and a.unit=c.unit and c.lang_type='"+langType+"' "
					+"  left join DCP_DEPARTMENT_LANG d  on p.eid=d.eid and p.DEPARTMENTNO=d.DEPARTNO and d.lang_type='"+langType+"' "
					+ " WHERE a.EID = '"+eId+"' " );  
		
		if(unit != null && unit.length() > 0)
			sqlbuf.append(" and a.unit = '"+unit+"' ");
		
		if(oUnit != null && oUnit.length() > 0)
			sqlbuf.append(" and a.oUnit = '"+oUnit+"' ");
		
		if(status != null && status.length() > 0)
			sqlbuf.append(" and a.status = '"+status+"' ");
		
		sqlbuf.append(" ) DBL  WHERE rn > "+startRow +" AND rn  <= "+(startRow + pageSize)+" ");
		
		sql = sqlbuf.toString();
		logger.info(sql);  
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
	protected String getCountSql(DCP_UnitConvertQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String unit = null;
		String oUnit = null;
		String status = null; 
		String eId = req.geteId();
		
		if(req.getRequest()!=null)
		{
			unit =  req.getRequest().getUnit();
			oUnit = req.getRequest().getoUnit();
			status = req.getRequest().getStatus();
			
		}
		
		
		StringBuffer sqlbuf=new StringBuffer("");
		String sql = "";
		
		sqlbuf.append("select num from( select count(*) AS num  from DCP_UNITCONVERT "
				+ "where EID='"+eId+"'  " );
		
		if(unit != null && unit.length() > 0)
			sqlbuf.append(" and unit = '"+unit+"' ");
		
		if(oUnit != null && oUnit.length() > 0)
			sqlbuf.append(" and oUnit = '"+oUnit+"' ");
		
		if(status != null && status.length() > 0)
			sqlbuf.append(" and STATUS = '"+status+"' ");
		
		sqlbuf.append(" )");
		
		sql= sqlbuf.toString();
		return sql;
	}
	
	
}
