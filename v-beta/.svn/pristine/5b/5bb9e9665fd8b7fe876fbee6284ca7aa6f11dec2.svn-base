
package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.json.cust.req.DCP_GoodsUnitConvertQueryReq;
import com.dsc.spos.json.cust.res.DCP_GoodsUnitConvertQueryRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;

/**
 * 查询商品单位换算信息
 * 2018-09-21
 * @author yuanyy
 *
 */
public class DCP_GoodsUnitConvertQuery extends SPosBasicService<DCP_GoodsUnitConvertQueryReq, DCP_GoodsUnitConvertQueryRes> {

	Logger logger = LogManager.getLogger(SPosAdvanceService.class);
	public List<DataProcessBean> pData = new ArrayList<DataProcessBean>();
	
	@Override
	protected boolean isVerifyFail(DCP_GoodsUnitConvertQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_GoodsUnitConvertQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_GoodsUnitConvertQueryReq>(){};
	}

	@Override
	protected DCP_GoodsUnitConvertQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_GoodsUnitConvertQueryRes();
	}

	@Override
	protected DCP_GoodsUnitConvertQueryRes processJson(DCP_GoodsUnitConvertQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_GoodsUnitConvertQueryRes res = null;
		res = this.getResponse();
		
		String sql = null;
		int totalRecords = 0; //总笔数
		int totalPages = 0;	
		
		sql = this.getQuerySql(req);
		String[] conditionValues = {};
		List<Map<String, Object>> getGoodsUnitConvertDatas = this.doQueryData(sql, conditionValues);
		res.setDatas(new ArrayList<DCP_GoodsUnitConvertQueryRes.level1Elm>());
		
		if(getGoodsUnitConvertDatas != null && getGoodsUnitConvertDatas.size() > 0){
			String num = getGoodsUnitConvertDatas.get(0).get("NUM").toString();
			totalRecords=Integer.parseInt(num);
			
			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;	
			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);
			
			for (Map<String, Object> oneData : getGoodsUnitConvertDatas) 
			{
				DCP_GoodsUnitConvertQueryRes.level1Elm oneLv1 = res.new level1Elm();
				String pluNO = oneData.get("PLUNO").toString();
				String pluName = oneData.get("PLUNAME").toString();
				String oUnit = oneData.get("OUNIT").toString();
				String unit = oneData.get("UNIT").toString();
				String oQty = oneData.get("OQTY").toString();
				String qty = oneData.get("QTY").toString();
				String status = oneData.get("STATUS").toString();
				oneLv1.setPluNo(pluNO);
				oneLv1.setPluName(pluName);
				oneLv1.setoUnit(oUnit);
				oneLv1.setUnit(unit);
				oneLv1.setOqty(oQty);
				oneLv1.setQty(qty);
				oneLv1.setStatus(status);
				res.getDatas().add(oneLv1);
				
				oneLv1 = null;
			}
		}
		else{
			res.setDatas(new ArrayList<DCP_GoodsUnitConvertQueryRes.level1Elm>());
		}
		
		res.setPageNumber(req.getPageNumber());
		res.setPageSize(req.getPageSize());
		res.setTotalRecords(totalRecords);
		res.setTotalPages(totalPages);
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_GoodsUnitConvertQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql = null;
		String eId = req.geteId();
		String pluNO = req.getRequest().getPluNo();
		String pluName = req.getRequest().getPluName();
		String ounit = req.getRequest().getoUnit();
		String unit = req.getRequest().getUnit();
		String status = req.getRequest().getStatus();
		String ketTxt = req.getRequest().getKeyTxt();
		
		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();
		
		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;
		StringBuffer sqlbuf=new StringBuffer(""); 
		
		sqlbuf.append(" SELECT * FROM ("
				    + " SELECT count(*) over() num, rn, pluno,pluname, ounit,unit,oqty, qty, status FROM ("
					+ " SELECT ROW_NUMBER() OVER(ORDER BY PLUNO) rn ,  pluno , pluName , ounit, unit, oqty, qty, status FROM ("
					+ " SELECT  b.pluNO, b.pluName , a.ounit, a.unit, a.oqty , a.qTY , a.status  ,a.EID "
					+ " FROM DCP_UNITconvert_goods a RIGHT JOIN DCP_GOODS  b ON a.pluno = b.pluNo AND a.EID = b.EID "
					+ " )"
					+ " WHERE EID = '"+eId+"' " );
		if(pluNO != null && pluNO.length() >0 ){
			sqlbuf.append("AND pluno = '"+pluNO+"' ");
		}
		if(pluName != null && pluName.length() >0 ){
			sqlbuf.append("AND pluName like '%%"+pluName+"%%' ");
		}
		
		if(ketTxt != null && ketTxt.length() >0 ){
			sqlbuf.append("AND ( pluName like '%%"+ketTxt+"%%' or pluNO  like '%%"+ketTxt+"%%'  ) ");
		}
		
		if(ounit != null && ounit.length() >0 ){
			sqlbuf.append("AND ounit = '"+ounit+"' ");
		}
		if(unit != null && unit.length() >0 ){
			sqlbuf.append("AND unit = '"+unit+"' ");
		}
		if(status != null && status.length() >0 ){
			sqlbuf.append("AND status = '"+status+"' ");
		}
		
		sqlbuf.append(" ORDER BY pluno) "
			//		+ " WHERE   rn> "+startRow+" AND rn < "+(startRow+pageSize)+" "
					+ "  order by pluno   ");
		sqlbuf.append(") "
				+ " WHERE   rn> "+startRow+" AND rn <= "+(startRow+pageSize)+" "
				+ "  order by pluno   ");
		sql = sqlbuf.toString();
		return sql;
	}
	
}
