package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_UnitQueryReq;
import com.dsc.spos.json.cust.res.DCP_UnitQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 服務函數：UnitGet
 *    說明：单位查询
 * 服务说明：单位查询
 * @author y 
 * @since  2017-02-24
 */
public class DCP_UnitQuery extends SPosBasicService<DCP_UnitQueryReq, DCP_UnitQueryRes> 
{
	@Override
	protected boolean isVerifyFail(DCP_UnitQueryReq req) throws Exception {
		boolean isFail = false;    
		return isFail;
	}

	@Override
	protected TypeToken<DCP_UnitQueryReq> getRequestType() {
		return new TypeToken<DCP_UnitQueryReq>(){};
	}

	@Override
	protected DCP_UnitQueryRes getResponseType() {
		return new DCP_UnitQueryRes();
	}	

	@Override
	protected DCP_UnitQueryRes processJson(DCP_UnitQueryReq req) throws Exception {
		//取得 SQL
		String sql = null;
		
		//查詢條件
		String eId = req.geteId();;
		String langType = req.getLangType();
		String keyTxt = req.getKeyTxt();
				
		//查詢資料
		DCP_UnitQueryRes res = null;
        res = this.getResponse();
    
        //给分页字段赋值
        sql = this.getQuerySql_Count(req);			//查询总笔数
		String[] conditionValues_Count = {langType,eId}; 			//查詢條件
		List<Map<String, Object>> getQData_Count = this.doQueryData(sql, conditionValues_Count);
		int totalRecords;								//总笔数
		int totalPages;									//总页数
		if (getQData_Count != null && getQData_Count.isEmpty() == false) { 
			Map<String, Object> oneData2 = getQData_Count.get(0);
			String num = oneData2.get("NUM").toString();
			totalRecords=Integer.parseInt(num);
			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
		}else{
			totalRecords = 0;
			totalPages = 0;
		}

		res.setPageNumber(req.getPageNumber());
		res.setPageSize(req.getPageSize());
	    res.setTotalRecords(totalRecords);
	    res.setTotalPages(totalPages);
	    
		//查詢資料
		sql = this.getQuerySql1(req);
		
		String[] conditionValues1 = {langType,eId}; 		//查詢條件	
        List<Map<String, Object>> getQData1 = this.doQueryData(sql, conditionValues1);
	    
		if (getQData1 != null && getQData1.isEmpty() == false) { // 有資料，取得詳細內容
			res.setDatas(new ArrayList<DCP_UnitQueryRes.level1Elm>());
			for (Map<String, Object> oneData1 : getQData1) {
				DCP_UnitQueryRes.level1Elm oneLv1 = res.new level1Elm();

				// 取得第一層資料庫搜尋結果
				String unit       = oneData1.get("UNIT").toString();
				String unitName   = oneData1.get("UNITNAME").toString();
				String unit_ratio = oneData1.get("UNIT_RATIO").toString();
				String wunit      = oneData1.get("WUNIT").toString();
				
				String punitUDLength = "2";//默认2
				String punitUDLength_db = oneData1.get("PUNIT_UDLENGTH").toString();
				try 
				{
					int punitUDLength_i = Integer.parseInt(punitUDLength_db);					
					punitUDLength = punitUDLength_i+"";
				} 
				catch (Exception e2) 
				{
			// TODO: handle exception
		
				}
				
				// 處理調整回傳值；
				oneLv1.setUnit(unit);
				oneLv1.setUnitName(unitName);
				oneLv1.setWunit(wunit);
				oneLv1.setUnit_ratio(unit_ratio);
				oneLv1.setUnitUdLength(punitUDLength);
				
				res.getDatas().add(oneLv1);
				
				oneLv1=null;
			}			
		}
		else{
			res.setDatas(new ArrayList<DCP_UnitQueryRes.level1Elm>());
		}
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		//調整查出來的資料
	}

	@Override
	protected String getQuerySql(DCP_UnitQueryReq req) throws Exception {
		return null;
	}

	protected String getQuerySql1(DCP_UnitQueryReq req) throws Exception {
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		
		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();
		
		String keyTxt = req.getKeyTxt();
		String pluNO = req.getPluNO();
		String eId = req.geteId();;
		String langType = req.getLangType();
		
		//計算起啟位置
		int startRow = ((req.getPageNumber() - 1) * req.getPageSize());
		startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
		startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料
				   
		sqlbuf.append(""
		    	+ "SELECT rn,unit,unitName ,unit_ratio,wunit,PUNIT_UDLENGTH "
				+ " from ("
		    	+ " SELECT ROWNUM rn,unit,unitName,unit_ratio,wunit,PUNIT_UDLENGTH "
				+ " from ("
				+ " select distinct a.unit,b.unit_name as unitName,a.unit_ratio,wunit, C.UDLENGTH AS PUNIT_UDLENGTH from ("
				+ " select wunit as unit,EID,1 as unit_ratio,wunit from DCP_GOODS where EID='"+eId+"' and pluno='"+pluNO+"' and status='100' "
				+ " union all "
				+ " select ounit as unit,EID,unit_ratio,unit as wunit  from DCP_UNITconvert where EID='"+eId+"' and status='100'  and unit in (select wunit from DCP_GOODS where EID='"+eId+"' and pluno='"+pluNO+"' and status='100') "
				+ " and ounit not in (select ounit from DCP_UNITconvert_goods where EID='"+eId+"' and pluno='"+pluNO+"' and status='100' ) "
				+ " union all "
				+ " select ounit as unit,EID,qty/oqty as unit_ratio,unit as wunit  from DCP_UNITconvert_goods where EID='"+eId+"' and pluno='"+pluNO+"' and status='100' and  ounit not in ( select wunit from DCP_GOODS where EID='"+eId+"' and pluno='"+pluNO+"' and status='100'  ) and  unit  in ( select wunit from DCP_GOODS where EID='"+eId+"' and pluno='"+pluNO+"' and status='100'  ) ) a  "
				+ " INNER JOIN DCP_UNIT_LANG  B ON A.UNIT=B.UNIT AND A.EID=B.EID "
				+ " left JOIN DCP_UNIT  C ON A.UNIT=C.UNIT AND A.EID=C.EID "
				+ " WHERE   B.status='100' AND B.lang_Type='"+langType+"'  AND a.EID='"+eId+"'"
			);
		
		if (keyTxt != null && keyTxt.length()!=0) { 		
			sqlbuf.append(""
		    	+ "AND (a.unit like '%%"+ keyTxt +"%%'  "
		    	+ "OR  b.unit_name like '%%"+ keyTxt +"%%')   "
    		);
		}
		
		sqlbuf.append(" order by unit ");
		sqlbuf.append(" ) TBL ");
		sqlbuf.append(") where 1=1 AND (rn > " + startRow + " AND rn <= " + (startRow+pageSize) + " ) ");
		//sqlbuf.append(" limit  "+startRow+","+pageSize+" ");  MYSQL
	  		
		sql = sqlbuf.toString();
	    
		return sql;
	}	
	
	protected String getQuerySql_Count(DCP_UnitQueryReq req) throws Exception {
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		String keyTxt = req.getKeyTxt();
		String pluNO = req.getPluNO();
		String eId = req.geteId();;
		String langType = req.getLangType();

		sqlbuf.append(""
				+ "SELECT num "
				+ " from ("
				+ " SELECT count(*) as num "
				+ " from ("
		    	+ "SELECT rn,unit,unitName,unit_ratio,wunit"
				+ " from ("
		    	+ " SELECT ROWNUM rn,unit,unitName,unit_ratio,wunit "
				+ " from ("
				+ " select distinct a.unit,b.unit_name as unitName,a.unit_ratio,wunit from ("
				+ " select wunit as unit,EID,1 as unit_ratio,wunit from DCP_GOODS where EID='"+eId+"' and pluno='"+pluNO+"' and status='100'  "
				+ " union all "
				+ " select ounit as unit,EID,unit_ratio,unit as wunit  from DCP_UNITconvert where EID='"+eId+"' and status='100'  and unit in (select wunit from DCP_GOODS where EID='"+eId+"' and pluno='"+pluNO+"' and status='100') "
				+ " union all "
				+ " select ounit as unit,EID,oqty/qty as unit_ratio,unit as wunit from DCP_UNITconvert_goods where EID='"+eId+"' and pluno='"+pluNO+"' and status='100' and  ounit not in ( select wunit from DCP_GOODS where EID='"+eId+"' and pluno='"+pluNO+"' and status='100'  ) and  unit  in ( select wunit from DCP_GOODS where EID='"+eId+"' and pluno='"+pluNO+"' and status='100'  )  ) a  "
				+ " INNER JOIN DCP_UNIT_LANG  B ON A.UNIT=B.UNIT AND A.EID=B.EID "
				+ " WHERE B.status='100' AND B.lang_Type='"+langType+"' AND a.EID='"+eId+"'"
			);
	
		if (keyTxt != null && keyTxt.length()!=0) { 		
			sqlbuf.append(""
	    	+ "AND (a.unit like '%%"+ keyTxt +"%%'  "
	    	+ "OR  b.unit_name like '%%"+ keyTxt +"%%')   "
    		);
		}
		
		sqlbuf.append(" order by unit ");
		sqlbuf.append(" )))) TBL ");
		 
		sql = sqlbuf.toString();
		
		return sql;
	}
}
