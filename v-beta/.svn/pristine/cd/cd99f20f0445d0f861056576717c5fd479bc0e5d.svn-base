package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_DayEndCheckReq;
import com.dsc.spos.json.cust.res.DCP_DayEndCheckRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * DayEndChec 專用的 response json
 *   說明：日结检核
 * 服务说明：日结检核
 * @author panjing 
 * @since  2016-12-09
 */

public class DCP_DayEndCheck extends SPosAdvanceService<DCP_DayEndCheckReq, DCP_DayEndCheckRes> {

	@Override
	protected boolean isVerifyFail(DCP_DayEndCheckReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		List<Map<String, String>> jsonDatas_List = req.getDayEndShops();
		if (jsonDatas_List==null || jsonDatas_List.isEmpty())
		{
			errMsg.append("查询单身(day_end_shops)不可为空值, ");
			isFail = true;
		}
		else 
		{
			for (Map<String, String> par : jsonDatas_List)
			{
				if (Check.Null(par.get("shopId")))
				{
					errMsg.append("门店(shop_no)不可为空值, ");
					isFail = true;
				}
				if (Check.Null(par.get("eDate")))
				{
					errMsg.append("日结日期(posted_date)不可为空值, ");
					isFail = true;
				}
				if (isFail)
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
				}
			}
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_DayEndCheckReq> getRequestType() {
		return new TypeToken<DCP_DayEndCheckReq>(){};
	}

	@Override
	protected DCP_DayEndCheckRes getResponseType() {
		return new DCP_DayEndCheckRes();
	}

	@Override
	protected void processDUID(DCP_DayEndCheckReq req,DCP_DayEndCheckRes res) throws Exception {		
		try 
		{
			String eId = req.geteId();
			res.setDayEndCheckDatas(new ArrayList<DCP_DayEndCheckRes.level2Elm>());
			List<Map<String, String>> jsonDatas_List = req.getDayEndShops();
			for (Map<String, String> par_List : jsonDatas_List) {
				String shopId = par_List.get("shopId");
				String eDate = par_List.get("eDate");
				String sql = this.getQuerySql_Check01(shopId,eDate);			
				List<Map<String, Object>> getQData1 = this.doQueryData(sql,null);
				if (getQData1 != null && getQData1.isEmpty() == false) {
					if (getQData1.get(0).get("NUM").toString().equals("0") == true){
						DCP_DayEndCheckRes.level2Elm oneLv2 = res.new level2Elm();
						oneLv2.setShopId(shopId);
						oneLv2.setStatus("N");
						oneLv2.setCode("day_end_01");
						oneLv2.setDescription("该门店日结信息未上传POS总部！");
						res.getDayEndCheckDatas().add(oneLv2);
						oneLv2 = null;
					}
					else
					{
						sql = this.getQuerySql_Check02();			
						String[] conditionValues2 = {shopId,eId,eDate,shopId,eId,eDate,shopId,eId,eDate}; 
						List<Map<String, Object>> getQData2 = this.doQueryData(sql,conditionValues2);
						//System.out.println(getQData2.get(0).get("NUM"));
						if (getQData2.get(0).get("NUM").toString().equals("0") == false){
							DCP_DayEndCheckRes.level2Elm oneLv2 = res.new level2Elm();
							oneLv2.setShopId(shopId);
							oneLv2.setStatus("N");
							oneLv2.setCode("day_end_02");
							oneLv2.setDescription("该门店存在未上传出入库单据！");
							res.getDayEndCheckDatas().add(oneLv2);
							oneLv2 = null;
						}
						else
						{
							DCP_DayEndCheckRes.level2Elm oneLv2 = res.new level2Elm();
							oneLv2.setShopId(shopId);
							oneLv2.setStatus("Y");
							oneLv2.setCode("00");
							oneLv2.setDescription("");
							res.getDayEndCheckDatas().add(oneLv2);	
							oneLv2=null;
						}
					}
				}				
			}

		} catch (Exception e) {
			// TODO: handle exception
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_DayEndCheckReq req) throws Exception {
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_DayEndCheckReq req) throws Exception {
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_DayEndCheckReq req) throws Exception {

		return null;
	}

	@Override
	protected String getQuerySql(DCP_DayEndCheckReq req) throws Exception {
		return null;
	}

	protected String getQuerySql_Check01(String shopId,String eDate ) throws Exception {
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");

		sqlbuf.append("select count(*) as num from DCP_DATEEND "
				+ " where SHOPID='"+shopId+"' and edate='"+eDate+"' "); 

		sql = sqlbuf.toString();

		return sql;
	}	

	protected String getQuerySql_Check02() throws Exception {
		String sql = null;

		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append(" "
				+ " select sum(num) as num "
				+ " from ( "
				+ " SELECT count(*) as num FROM DCP_SALE  WHERE  TYPE in('0','1','2','5','6') AND PROCESS_STATUS='N'  and SHOPID=? and EID=? and bdate<=? "
				+ " union all "
				+ " SELECT count(*) as num FROM DCP_STOCKOUT  WHERE STATUS in('2','3') AND PROCESS_STATUS='N'  and SHOPID=? and EID=? and account_date<=? "
				+ " union all "
				+ " SELECT count(*) as num FROM DCP_STOCKIN WHERE STATUS='2' AND PROCESS_STATUS='N' and SHOPID=? and EID=? and account_date<=? "
				+ " )" ); 

		if (sqlbuf.length() > 0)	
			sql = sqlbuf.toString();

		return sql;
	}	






}


