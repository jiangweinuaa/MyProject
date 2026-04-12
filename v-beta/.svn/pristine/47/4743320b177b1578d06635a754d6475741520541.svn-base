package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_AVGSaleAMTQueryReq;
import com.dsc.spos.json.cust.res.DCP_AVGSaleAMTQueryRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

public class DCP_AVGSaleAMTQuery extends SPosAdvanceService<DCP_AVGSaleAMTQueryReq, DCP_AVGSaleAMTQueryRes> {
	@Override
	protected void processDUID(DCP_AVGSaleAMTQueryReq req, DCP_AVGSaleAMTQueryRes res) throws Exception {
		try {
			//查找销售额
			String sql = " select * from ( "
					+ " select sum(case when type=0 then tot_amt else -tot_amt end ) TOT_AMT from DCP_SALE "
					+ " where bdate>='" + req.getRequest().getBeginDate() + "' and bdate<='" + req.getRequest().getEndDate() + "' and SHOPID='" + req.getShopId() + "' ) "
					+ " where TOT_AMT is not null   ";
			//这里计算日期差rdate 单据上的需求日期    submitDate 当前的日期
			SimpleDateFormat difsimDate = new SimpleDateFormat("yyyyMMdd");
			java.util.Date date1 = difsimDate.parse(req.getRequest().getBeginDate());
			java.util.Date date2 = difsimDate.parse(req.getRequest().getEndDate());
			int difdate = PosPub.differentDaysByMillisecond(date1, date2);
			List<Map<String, Object>> getAMTData = this.doQueryData(sql, null);
			double saleamt = 0;
			if (getAMTData != null && !getAMTData.isEmpty()) {
				saleamt = Double.parseDouble(getAMTData.get(0).get("TOT_AMT").toString());
			}
			double avgsamt = saleamt / (difdate + 1);
			BigDecimal b = new BigDecimal(avgsamt);
			avgsamt = b.setScale(2, RoundingMode.HALF_UP).doubleValue();
			
			res.setAvgSaleAMT(avgsamt);
			
			//增加参考天气处理
			res.setDatas(new ArrayList<>());
			
			String isReferenceWeather = req.getRequest().getIsReferenceWeather();
			if (Check.Null(isReferenceWeather)) {
				isReferenceWeather = "N";
			}
			
			if (isReferenceWeather.equals("Y")) {
				sql = this.getWeatherInfoSQL(req, difdate);
				List<Map<String, Object>> getQData1 = this.doQueryData(sql, null);
				if (getQData1 != null && !getQData1.isEmpty()) {
					for (Map<String, Object> oneData1 : getQData1) {
						DCP_AVGSaleAMTQueryRes.level1Elm oneLv1 = res.new level1Elm();
						
						String cITY = oneData1.get("CITY").toString();
						String dAY_POWER = oneData1.get("DAY_POWER").toString();
						String dAY_TEMPERATURE = oneData1.get("DAY_TEMPERATURE").toString();
						String dAY_WEATHER = oneData1.get("DAY_WEATHER").toString();
						String dAY_WIND = oneData1.get("DAY_WIND").toString();
						String dISTRICT = oneData1.get("DISTRICT").toString();
						String nIGHT_POWER = oneData1.get("NIGHT_POWER").toString();
						String nIGHT_TEMPERATURE = oneData1.get("NIGHT_TEMPERATURE").toString();
						String nIGHT_WEATHER = oneData1.get("NIGHT_WEATHER").toString();
						String nIGHT_WIND = oneData1.get("NIGHT_WIND").toString();
						String sDATE = oneData1.get("SDATE").toString();
						String wEEK = oneData1.get("WEEK").toString();
						
						oneLv1.setCITY(cITY);
						oneLv1.setDAY_POWER(dAY_POWER);
						oneLv1.setDAY_TEMPERATURE(dAY_TEMPERATURE);
						oneLv1.setDAY_WEATHER(dAY_WEATHER);
						oneLv1.setDAY_WIND(dAY_WIND);
						oneLv1.setDISTRICT(dISTRICT);
						oneLv1.setNIGHT_POWER(nIGHT_POWER);
						oneLv1.setNIGHT_TEMPERATURE(nIGHT_TEMPERATURE);
						oneLv1.setNIGHT_WEATHER(nIGHT_WEATHER);
						oneLv1.setNIGHT_WIND(nIGHT_WIND);
						oneLv1.setSDATE(sDATE);
						oneLv1.setWEEK(wEEK);
						
						res.getDatas().add(oneLv1);
					}
				}
			}
		}catch (Exception e){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500,e.getMessage());
		}
	}
	
	@Override
	protected List<InsBean> prepareInsertData(DCP_AVGSaleAMTQueryReq req) throws Exception {
		return null;
	}
	
	@Override
	protected List<UptBean> prepareUpdateData(DCP_AVGSaleAMTQueryReq req) throws Exception {
		return null;
	}
	
	@Override
	protected List<DelBean> prepareDeleteData(DCP_AVGSaleAMTQueryReq req) throws Exception {
		return null;
	}
	
	@Override
	protected boolean isVerifyFail(DCP_AVGSaleAMTQueryReq req) throws Exception {
		return false;
	}
	
	@Override
	protected TypeToken<DCP_AVGSaleAMTQueryReq> getRequestType() {
		return new TypeToken<DCP_AVGSaleAMTQueryReq>(){};
	}
	
	@Override
	protected DCP_AVGSaleAMTQueryRes getResponseType() {
		return new DCP_AVGSaleAMTQueryRes();
	}
	
	
	/**
	 * 获取天气的SQL
	 */
	protected String getWeatherInfoSQL(DCP_AVGSaleAMTQueryReq req,int difdate) {
		
		String multi_Date="";
		for (int i = 0; i <= difdate; i++) {
			multi_Date = multi_Date + "'" +PosPub.GetStringDate(req.getRequest().getBeginDate(),i)  + "',";
		}
		
		if (multi_Date.length()>0) {
			multi_Date=multi_Date.substring(0,multi_Date.length()-1);
		}
		
		StringBuffer sqlbuf = new StringBuffer();
		sqlbuf.append("select * from DCP_areaweather "
				+ " where city='"+req.getCITY()+"' and district='"+req.getDISTRICT()+"' "
				+ " and sdate in ("+multi_Date+")");
		
		return sqlbuf.toString();
	}
	
	
	
	
	
}
