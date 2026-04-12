package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_POrderSTSaleAmtQueryReq;
import com.dsc.spos.json.cust.res.DCP_POrderSTSaleAmtQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;

/**
 * 查询最近两个盘点日期之间营业额
 * @author yuanyy 2019-03-25
 *
 */
public class DCP_POrderSTSaleAmtQuery extends SPosBasicService<DCP_POrderSTSaleAmtQueryReq, DCP_POrderSTSaleAmtQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_POrderSTSaleAmtQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_POrderSTSaleAmtQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_POrderSTSaleAmtQueryReq>(){};
	}

	@Override
	protected DCP_POrderSTSaleAmtQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_POrderSTSaleAmtQueryRes();
	}

	@Override
	protected DCP_POrderSTSaleAmtQueryRes processJson(DCP_POrderSTSaleAmtQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql = null;
		DCP_POrderSTSaleAmtQueryRes res = this.getResponse();
		String eId = req.geteId();
		String shopId = req.getShopId();
		
		sql = this.getQuerySql(req);
		List<Map<String, Object>> getQData1 = this.doQueryData(sql, null);
		String beginDate = "";
		String endDate = "";
		if(!getQData1.isEmpty()){
			beginDate = getQData1.get(0).get("BDATE").toString();
			endDate = getQData1.get(1).get("BDATE").toString();
		}
		res.setBeginDate(beginDate);
		res.setEndDate(endDate);
		
		sql = " select * from ( select  sum(case when type=0 then tot_amt else -tot_amt end) TOT_AMT from DCP_SALE "
				+ " where EID = '"+eId+"'"
				+ " and bdate>='"+beginDate+"' "
				+ " and bdate<='"+endDate+"'   "
				+ " and  SHOPID='"+shopId+"' "
				+ " ) where TOT_AMT is not null";   
				
		List<Map<String, Object>> getAMTData = this.doQueryData(sql,null);
		BigDecimal saleAmt= new BigDecimal("0");
		if(getAMTData != null && !getAMTData.isEmpty())
		{
			saleAmt= new BigDecimal(getAMTData.get(0).get("TOT_AMT").toString());
			saleAmt = saleAmt.setScale(2, RoundingMode.HALF_UP);
		}
		res.setTotAmt(saleAmt.toString());
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_POrderSTSaleAmtQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql = "";
		StringBuffer sqlbuf = new StringBuffer();
		
		String eId = req.geteId();
		String shopId = req.getShopId();
		
		sqlbuf.append("select bdate from ( "
					+ " select  stocktakeno,bdate  from DCP_stocktake a  "
					+ "where a.EID = '"+eId+"'  "
					+ "and a.SHOPID = '"+shopId+"' "
					+ "and a.status = '2' order by bdate desc ) where rownum < 3");
		
		sql = sqlbuf.toString();
		return sql;
	}
	
}