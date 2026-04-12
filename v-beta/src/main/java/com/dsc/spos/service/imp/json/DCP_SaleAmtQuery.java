package com.dsc.spos.service.imp.json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_SaleAmtQueryReq;
import com.dsc.spos.json.cust.req.DCP_SaleAmtQueryReq.level2Elm;
import com.dsc.spos.json.cust.res.DCP_SaleAmtQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.LunarUtil;
import com.google.gson.reflect.TypeToken;

/**
 * 营业金额查询
 * @author yuanyy
 *	
 */
public class DCP_SaleAmtQuery extends SPosBasicService<DCP_SaleAmtQueryReq, DCP_SaleAmtQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_SaleAmtQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_SaleAmtQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new  TypeToken<DCP_SaleAmtQueryReq>(){};
	}

	@Override
	protected DCP_SaleAmtQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_SaleAmtQueryRes();
	}

	@Override
	protected DCP_SaleAmtQueryRes processJson(DCP_SaleAmtQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_SaleAmtQueryRes res = null;
		res = this.getResponse();
		try {
			String dateReferType = req.getRequest().getDateReferType(); //日期参考类型   fix:固定值, avg 平均值   
			String city = req.getCITY();
			String rDate = "";
			if(!Check.Null( req.getRequest().getrDate()) ){
				rDate = req.getRequest().getrDate().replace("-", "");
			}
			String sql = "";
			
			List<Map<String, Object>> queryDatas = null;
			
			String dateStr = "";
			List<DCP_SaleAmtQueryReq.level2Elm> dateDatas = req.getRequest().getDatas();
			if(dateDatas != null && dateDatas.size() > 0){
				
				sql = this.getQuerySql(req);
				queryDatas = this.doQueryData(sql, null);
				
				for (int i = 0; i < dateDatas.size(); i++) {
					String beginDate = dateDatas.get(i).getBeginDate();
					
					beginDate = beginDate.replace("-", "");
					dateStr = dateStr + "'"+beginDate+"',";
				}
			}
			
			dateStr = dateStr + "'"+rDate+"'";
			
			StringBuffer sqlbuf = new StringBuffer();
			
			String weatherSql = "";
			sqlbuf.append( "select city, sDate, district , day_weather , night_weather ,"
					+ " day_temperature , night_temperature "
					+ " FROM DCP_areaweather   where city like '%%"+city+"%%' "
					+ " and ( sDate = '"+rDate+"' " );
			if(dateDatas != null && dateDatas.size() > 0){
				
				for (int i = 0; i < dateDatas.size(); i++) {
					String beginDate = dateDatas.get(i).getBeginDate();
					String endDate = dateDatas.get(i).getEndDate(); 
					beginDate = beginDate.replace("-", "");
					endDate = endDate.replace("-", "");
					sqlbuf.append(" or sDate between '"+beginDate+"' and '"+endDate+"'  ");
				}
				
			}
			
//					+ " and sDate in ("+dateStr+") "
			sqlbuf.append( " ) order by sDate desc  ");
			
			weatherSql = sqlbuf.toString();
			List<Map<String, Object>> weatherDatas = this.doQueryData(weatherSql, null);
			
			res.setDatas(new ArrayList<DCP_SaleAmtQueryRes.level1Elm>());
			
			String defSaleAmt = "0";
			String defDinnerAmt = "0";
			String defTableAmt = "0";
			String defPfWeather = "";
			String defLowClimate = "";
			String defHighClimate = "";
			int itemInt = 1;
			if(dateDatas != null && dateReferType.equals("fix")){
				
				for (level2Elm dateMap : dateDatas) {
					String beginDate = dateMap.getBeginDate();
					String endDate = dateMap.getEndDate();
					String item = dateMap.getItem();
					
					DCP_SaleAmtQueryRes.level1Elm lv1 = res.new level1Elm();
					
					String saleAmt = "0";
					String dinnerAmt = "0";
					String tableAmt = "0";
					String pfWeather = "";
	//				String climate = "";
					String lowClimate = "";
					String highClimate = "";
					
					String beginDateShort = beginDate.replace("-", "");
					
					if(queryDatas != null && queryDatas.size() > 0){
						for (Map<String, Object> map : queryDatas) {
							String eDate = map.getOrDefault("BDATE", "").toString();
							
							if(eDate.equals("")){
								continue;
							}
							
							if(eDate.equals(beginDateShort)){

								saleAmt = map.getOrDefault("TOT_AMT", "0").toString();
								dinnerAmt = map.getOrDefault("TOTMEALNUM", "0").toString();
								tableAmt = map.getOrDefault("TC", "0").toString();
								
								if(item.equals("1")){ //前端默认选中第一个， 所以需求日期也默认返回第一个日期的值
									defSaleAmt = saleAmt;
									defDinnerAmt = dinnerAmt;
									defTableAmt = tableAmt;
								}
								
							}
						}
					}
					
					if(weatherDatas != null && weatherDatas.size() > 0){
						for (Map<String, Object> map : weatherDatas) {
							String sDate = map.get("SDATE").toString();
							if(beginDateShort.equals(sDate)){
								
								if(sDate.equals(rDate)){
									defPfWeather = map.get("DAY_WEATHER").toString();
									defLowClimate = map.get("NIGHT_TEMPERATURE").toString(); // 
									defHighClimate = map.get("DAY_TEMPERATURE").toString();
								}else{
									pfWeather = map.get("DAY_WEATHER").toString();
									lowClimate = map.get("NIGHT_TEMPERATURE").toString(); // 
									highClimate = map.get("DAY_TEMPERATURE").toString();
								}
							}
						}
					}
					
					
					String eDate = beginDate;

					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Date date =sdf.parse(eDate);
					
					Calendar cal = Calendar.getInstance();
					cal.setTime(date);
					LunarUtil lunar = new LunarUtil();
					lunar.getLunar(cal);
					String lunarStr = lunar.toString();
					String pfDay = lunar.getWeek(date);
					String lunarPfHoliday = lunar.GetHolidayInfo(eDate);
					
					lv1.seteDate(eDate);
					lv1.setDataType("refer");
					lv1.setItem(item);
	//				lv1.setBeginDate(beginDate);
					lv1.setPfDay(pfDay);
					lv1.setCity(city);
	//				lv1.setPfHoliday(pfHoliday);
					lv1.setLunarPfHoliday(lunarPfHoliday);
					lv1.setLunar(lunarStr);
					lv1.setPfWeather(pfWeather);
	//				lv1.setClimate(climate);
					lv1.setLowClimate(lowClimate);
					lv1.setHighClimate(highClimate);
					lv1.setSaleAmt(saleAmt);
					lv1.setDinnerAmt(dinnerAmt);
					lv1.setTableAmt(tableAmt);
					
					res.getDatas().add(lv1);
					
					if(!Check.Null(item)){
						itemInt = Integer.parseInt(item);
						itemInt = itemInt + 1;
					}
					
				}
			}
			
			
			// 日期平均值
			int totDay = 0;
			if(dateDatas != null && dateReferType.equals("avg")){ //日期平均值
				
				String beginDate = dateDatas.get(0).getBeginDate();
				String endDate = dateDatas.get(0).getEndDate();
				
				double totSaleAmt = 0;
				double totDinnerAmt = 0;
				double totTableAmt = 0;
				
				if(queryDatas != null && queryDatas.size() > 0){
//					int itemInt = 1;
					for (Map<String, Object> map : queryDatas) {
						
						DCP_SaleAmtQueryRes.level1Elm lv1 = res.new level1Elm();
						
						String eDate = map.getOrDefault("BDATE", "").toString();
						
						if(eDate.equals("")){
							continue;
						}
						
						String saleAmt = map.getOrDefault("TOT_AMT", "0").toString();
						String dinnerAmt = map.getOrDefault("TOTMEALNUM", "0").toString();
						String tableAmt = map.getOrDefault("TC", "0").toString();
						
						double saleAmtDou = Double.parseDouble(saleAmt);
						
						totSaleAmt += Double.parseDouble(saleAmt);
						totDinnerAmt += Double.parseDouble(dinnerAmt);
						totTableAmt += Double.parseDouble(tableAmt);
						
						if(saleAmtDou - 0 != 0){ //营业额为0 的日期不纳入计算平均值
							totDay = totDay + 1 ;
						}
						
						String pfWeather = "";
						String lowClimate = "";
						String highClimate = "";
						boolean isRefer = true;
						if(weatherDatas != null && weatherDatas.size() > 0){
							for (Map<String, Object> weaMap : weatherDatas) {
								String sDate = weaMap.get("SDATE").toString();
									
								if(sDate.equals(rDate)){ //需求日期对应的天气等信息
									defPfWeather = weaMap.get("DAY_WEATHER").toString();
									defLowClimate = weaMap.get("NIGHT_TEMPERATURE").toString(); // 
									defHighClimate = weaMap.get("DAY_TEMPERATURE").toString();
									isRefer = false;
								}else if(eDate.equals(sDate) ) {//参考日期对应的天气等信息
									pfWeather = weaMap.get("DAY_WEATHER").toString();
									lowClimate = weaMap.get("NIGHT_TEMPERATURE").toString(); // 
									highClimate = weaMap.get("DAY_TEMPERATURE").toString();
								}
							}
						}
						
//						if(isRefer == false ){
//							continue;
//						}
						// eDate 需要转换为 yyyy-MM-dd 的格式
						
						SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
						Date date =sdf.parse(eDate);
						System.out.println(date);
						SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
						String eDateStr = sdf2.format(date);
						
						Calendar cal = Calendar.getInstance();
						cal.setTime(date);
						LunarUtil lunar = new LunarUtil();
						lunar.getLunar(cal);
						String lunarStr = lunar.toString();
						String pfDay = lunar.getWeek(date);
						String lunarPfHoliday = lunar.GetHolidayInfo(eDate);
						
						lv1.seteDate(eDateStr);
						lv1.setDataType("refer");
						lv1.setItem(itemInt+"");
						lv1.setPfDay(pfDay);
						lv1.setCity(city);
						lv1.setLunarPfHoliday(lunarPfHoliday);
						lv1.setLunar(lunarStr);
						lv1.setPfWeather(pfWeather);
						lv1.setLowClimate(lowClimate);
						lv1.setHighClimate(highClimate);
						lv1.setSaleAmt(saleAmt);
						lv1.setDinnerAmt(dinnerAmt);
						lv1.setTableAmt(tableAmt);
						
						res.getDatas().add(lv1);
						
						itemInt = itemInt + 1;
						
					}
				}
				if(totDay == 0 ){
					defSaleAmt = "0";
					defDinnerAmt = "0";
					defTableAmt = "0"; 
				}else{
					defSaleAmt = String.format("%.2f", totSaleAmt / totDay );//金额四舍五入保留两位
					defDinnerAmt = Math.round(totDinnerAmt / totDay) + ""; //人数和桌数四舍五入取整
					defTableAmt = Math.round(totTableAmt / totDay) + ""; 
				}

//				
				
//				res.setAvgSaleAmt(defSaleAmt);
//				res.setAvgDinnerAmt(defDinnerAmt);
//				res.setAvgTableAmt(defTableAmt);
//				res.setTotDay(totDay+"");
//				
//				DCP_SaleAmtQueryRes.level1Elm lv1 = res.new level1Elm();
//				lv1.seteDate(beginDate+ "到" + endDate + ",共"+totDay+"天平均值"); //这一行设置为天数差
//				lv1.setDataType("refer");
//				lv1.setItem( (itemInt + 1 )+ "");//
//				lv1.setPfDay("");
//				lv1.setCity(city);
//				lv1.setLunarPfHoliday("");
//				lv1.setLunar("");
//				lv1.setPfWeather("");
//				lv1.setLowClimate("");
//				lv1.setHighClimate("");
//				lv1.setSaleAmt(defSaleAmt);
//				lv1.setDinnerAmt(defDinnerAmt);
//				lv1.setTableAmt(defTableAmt);
//				
//				res.getDatas().add(lv1);
				
			}
			
			DCP_SaleAmtQueryRes.level1Elm lv1 = res.new level1Elm();
			rDate = req.getRequest().getrDate();
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date =sdf.parse(rDate);
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			LunarUtil lunar = new LunarUtil();
			lunar.getLunar(cal);
			String lunarStr = lunar.toString();
			String pfDay = lunar.getWeek(date);
			String lunarPfHoliday = lunar.GetHolidayInfo(rDate);
			String dataType = "true";
			
			lv1.seteDate(rDate);
			lv1.setDataType(dataType);
			lv1.setItem( itemInt +  "");//
			lv1.setPfDay(pfDay);
			lv1.setCity(city);
			lv1.setLunarPfHoliday(lunarPfHoliday);
			lv1.setLunar(lunarStr);
			lv1.setPfWeather(defPfWeather);
			lv1.setLowClimate(defLowClimate);
			lv1.setHighClimate(defHighClimate);
			lv1.setSaleAmt(defSaleAmt);
			lv1.setDinnerAmt(defDinnerAmt);
			lv1.setTableAmt(defTableAmt);
			
			res.getDatas().add(lv1);
			
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setServiceDescription("服务执行失败！！"+e.getMessage());
			res.setSuccess(false);
			res.setServiceStatus("200");
		}
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_SaleAmtQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		
		String bDate = req.getRequest().getbDate();// 下单日期
		String rDate = req.getRequest().getrDate();// 需求日期
		String dateReferType = req.getRequest().getDateReferType();
		
		List<DCP_SaleAmtQueryReq.level2Elm> dateDatas = req.getRequest().getDatas();
		
		String sql = "";
		StringBuffer sqlbuf = new StringBuffer();
		sqlbuf.append(" SELECT sd.*  FROM ( "
				+ " select  a.EID, a.SHOPID, a.bDate , sum(case when a.type='1' or a.type='2' or a.type='4'  then 0 else 1 end) as SC ,"
				+ " sum(case when a.type='1' or a.type='2' or a.type='4'  then -pay_amt else pay_amt end ) as pay_amt,"
				+ " sum(case when a.type='1' or a.type='2' or a.type='4'  then -tot_amt else tot_amt end) as tot_amt ,"
				+ " COUNT( DISTINCT a.saleNO ) AS tc , "
				+ "  sum(case  when a.type = '1' or a.type = '2' or a.type = '4' then  0  else  mealNumber end) AS totMealNUm  "
				+ " from DCP_SALE a "
				+ " "
				+ " where a.EID='"+req.geteId()+"' and a.SHOPID = '"+req.getShopId()+"'  AND "
				+ " ( "  );
		if(dateDatas != null && dateDatas.size() > 0){
			for (int i = 0; i < dateDatas.size(); i++) {
				String beginDate = dateDatas.get(i).getBeginDate();
				String endDate =  dateDatas.get(i).getEndDate();
				
				beginDate = beginDate.replace("-", "");
				endDate = endDate.replace("-", ""); 
				
				sqlbuf.append(" a.bDate between '"+beginDate+"' and '"+endDate+"' ");  
				if(i < dateDatas.size() - 1){
					sqlbuf.append(" or ");
				}
			}
		}
		
		sqlbuf.append( "  )"
				+ " group by a.EID, a.SHOPID, a.bDate "
				+ " ) sd "
				+ "   "
        );
		
		sql = sqlbuf.toString();
		return sql;
	}
	
}
