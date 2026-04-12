package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_SaleAmtOldQueryReq;
import com.dsc.spos.json.cust.res.DCP_SaleAmtOldQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;

public class DCP_SaleAmtOldQuery extends SPosBasicService<DCP_SaleAmtOldQueryReq, DCP_SaleAmtOldQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_SaleAmtOldQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_SaleAmtOldQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_SaleAmtOldQueryReq>(){};
	}

	@Override
	protected DCP_SaleAmtOldQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_SaleAmtOldQueryRes();
	}

	@Override
	protected DCP_SaleAmtOldQueryRes processJson(DCP_SaleAmtOldQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		
		DCP_SaleAmtOldQueryRes res = null;
		res = this.getResponse();
		String sql = "";
		
		try {
			
			Calendar calendar = Calendar.getInstance();  
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");  
			ParsePosition pos = new ParsePosition(0);
			Date day = format.parse(req.geteDate(), pos);
			calendar.setTime(day);
			String result = "";
			
			// 查询同期五周的销售额 
			String dateStr = "' '";
			List<Map<String, Object>> dateList = new ArrayList<>();
			
			for (int i = 0; i < 5; i++) {
				calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - 7);  
			    day = calendar.getTime();  
			    result = format.format(day);
			    dateStr =  dateStr + " , "+ result  ;
			    Map<String, Object> map = new HashMap<String, Object>();
			    map.put("EDATE", result);
			    dateList.add(0,map);
			}
			
			BigDecimal totalAmt = new BigDecimal(0);
			
			sql = this.getQuerySql(req);
			List<Map<String, Object>> getDatas = this.doQueryData(sql, null);
			res.setDatas(new ArrayList<DCP_SaleAmtOldQueryRes.level1Elm>());
			
			int count = 0;
			
			for (Map<String, Object> map : dateList) {
				DCP_SaleAmtOldQueryRes.level1Elm lv1 = res.new level1Elm();
				String eDate = map.get("EDATE").toString();
				String saleAmt = "0";
				
				for (Map<String, Object> map2 : getDatas) {
					if(eDate.equals(map2.get("EDATE").toString())){
						saleAmt = map2.getOrDefault("SALEAMT", "0").toString();
						BigDecimal saleAmtDe = new BigDecimal(saleAmt);
						totalAmt = totalAmt.add(saleAmtDe);
						
						if(saleAmtDe.compareTo(new BigDecimal(0)) != 0){
							count = count + 1;
						}
						
					}
				}
				
				lv1.seteDate(eDate);
				lv1.setSaleAmt(Double.parseDouble(saleAmt)+"");
				res.getDatas().add(lv1);
				lv1 = null;
				
			}
			
			if(count == 0){
				count = 1; // 避免出现 除数为0 的情况；
			}
			
			Double avgAmtDe = totalAmt.divide(new BigDecimal(count),2,BigDecimal.ROUND_HALF_UP).doubleValue(); 
			DCP_SaleAmtOldQueryRes.level1Elm lv1 = res.new level1Elm();
			lv1.seteDate(req.geteDate());
			lv1.setSaleAmt(avgAmtDe.toString());
			res.getDatas().add(lv1);
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行异常");
		}
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_SaleAmtOldQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String eId = req.geteId();
		String shopId = req.getShopId();
		
		String eDate = req.geteDate();
		
		Calendar calendar = Calendar.getInstance();  
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");  
		ParsePosition pos = new ParsePosition(0);
		Date day = format.parse(eDate, pos);
		calendar.setTime(day);
		
		String result = "";
		
		// 查询同期五周的销售额 
		String dateStr = "' '";
		
		for (int i = 0; i < 5; i++) {
			calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - 7);  
		    day = calendar.getTime();  
		    result = format.format(day);
		    dateStr =  dateStr + " , "+ result  ;
		}
		
		String sql = "";
		StringBuffer sqlbuf = new StringBuffer();
		sqlbuf.append(" SELECT eDate,  sum(sale_amt)  AS saleAmt "
					+ " FROM DCP_stock_Day_Static "
					+ " WHERE EID = '"+eId+"' AND organizationNo = '"+shopId+"'  "
					+ " AND eDate IN ("+dateStr+") "
					+ " GROUP BY eDate  "
					+ " order by eDate asc ");
		
		sql = sqlbuf.toString();
		return sql;
	}
	
}
