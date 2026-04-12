package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_EDateQueryReq;
import com.dsc.spos.json.cust.res.DCP_EDateQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 服務函數：EDateGet
 *    說明：营业日查询查询
 * 服务说明：营业日查询查询
 * @author panjing 
 * @since  2016-09-22
 */

public class DCP_EDateQuery extends SPosBasicService<DCP_EDateQueryReq, DCP_EDateQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_EDateQueryReq req) throws Exception {
		boolean isFail = false;

		return isFail;
	}

	@Override
	protected TypeToken<DCP_EDateQueryReq> getRequestType() {
		return new TypeToken<DCP_EDateQueryReq>(){};
	}

	@Override
	protected DCP_EDateQueryRes getResponseType() {
		return new DCP_EDateQueryRes();
	}
	

	@Override
	protected DCP_EDateQueryRes processJson(DCP_EDateQueryReq req) throws Exception {
		// 取得 SQL
		String sql = null;
		String shopId;
		String organizationNO;

		// 查詢條件
		String eId = req.geteId();
		
	  ///新增指定门店日结，门店由前端JSON传入   BY JZMA 20181205 
		if (Check.Null(req.getoShopId())) 
		{	
			shopId = req.getShopId();
			organizationNO = req.getOrganizationNO();
		}
		else
		{
			shopId = req.getoShopId();
			organizationNO = req.getoShopId();
		}		
		

		// 查詢資料
		DCP_EDateQueryRes res = null;
		res = this.getResponse();

		sql = this.getQuerySql(req);

		String[] conditionValues = { organizationNO, eId, shopId }; // 查詢條件

		List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);

		if (getQData != null && getQData.isEmpty() == false) { // 有資料，取得詳細內容
			res.setDatas(new ArrayList<DCP_EDateQueryRes.level2Elm>());

			Map<String, Object> oneData = getQData.get(0);
			DCP_EDateQueryRes.level2Elm oneLv2 = res.new level2Elm();

			// 取得第一層資料庫搜尋結果
			String maxEDate = oneData.get("MAXEDATE").toString();

			oneLv2.setMaxEDate(maxEDate);

			res.getDatas().add(oneLv2);
		}

		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		//調整查出來的資料
	}

	@Override
	protected String getQuerySql(DCP_EDateQueryReq req) throws Exception {
		String sql = null;
		//String conditionSql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		
		sqlbuf.append(""
				+ "select maxEDate "
				+ " from ( "
				+ " select Max(EDate) as maxEDate from DCP_stock_day where "
				+ " OrganizationNO = ? AND EID = ?  "

				); 
		
		 sqlbuf.append(" ) TBL ");
    
    	sql = sqlbuf.toString();
    
    	return sql;
	}
	
}
