package com.dsc.spos.service.imp.json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_PFOrderDetailReq;
import com.dsc.spos.json.cust.res.DCP_PFOrderDetailRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.DateUtils;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;
 
/**
 * 计划报单详情查询
 * @author yuanyy
 *
 */
public class DCP_PFOrderDetail extends SPosBasicService<DCP_PFOrderDetailReq, DCP_PFOrderDetailRes> {

	@Override
	protected boolean isVerifyFail(DCP_PFOrderDetailReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_PFOrderDetailReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PFOrderDetailReq>(){};
	}

	@Override
	protected DCP_PFOrderDetailRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_PFOrderDetailRes();
	}

	@Override
	protected DCP_PFOrderDetailRes processJson(DCP_PFOrderDetailReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_PFOrderDetailRes res = null;
		res = this.getResponse();
		
		try {
			String sql = "";
			sql = this.getQuerySql(req);
			List<Map<String, Object>> queryDatas = this.doQueryData(sql, null);
			
			res.setDatas(new ArrayList<DCP_PFOrderDetailRes.level1Elm>());
			if(queryDatas != null && queryDatas.size() > 0){
				
				//单头主键字段
				Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查询条件
				condition.put("PFNO", true);
				//调用过滤函数
				List<Map<String, Object>> getQHeader=MapDistinct.getMap(queryDatas, condition);
				
				//单头主键字段
				Map<String, Boolean> condition2 = new HashMap<String, Boolean>(); //查询条件
				condition2.put("PFNO", true);
				condition2.put("PLUNO", true);
				//调用过滤函数
				List<Map<String, Object>> pluDatas = MapDistinct.getMap(queryDatas, condition2);
				
				//单头主键字段
				Map<String, Boolean> condition3 = new HashMap<String, Boolean>(); //查询条件
				condition3.put("PFNO", true);
				condition3.put("ITEM", true);
				//调用过滤函数
				List<Map<String, Object>> forecastDatas = MapDistinct.getMap(queryDatas, condition3);
				
				//单头主键字段
				Map<String, Boolean> condition4 = new HashMap<String, Boolean>(); //查询条件
				condition4.put("PFNO", true);
				condition4.put("PLUNO", true);
				condition4.put("DTNO", true);
				//调用过滤函数
				List<Map<String, Object>> workDatas = MapDistinct.getMap(queryDatas, condition4);

				for (Map<String, Object> map : getQHeader) {
					DCP_PFOrderDetailRes.level1Elm lv1 = res.new level1Elm();
					String createBy = map.getOrDefault("CREATEBY", "").toString();
					String createByName = map.getOrDefault("CREATEBYNAME", "").toString();
					String createDate = map.getOrDefault("CREATE_DATE", "").toString();
					String createTime = map.getOrDefault("CREATE_TIME", "").toString();
					String modifyBy = map.getOrDefault("MODIFYBY", "").toString();
					String modifyByName = map.getOrDefault("MODIFYBYNAME", "").toString();
					String modifyDate = map.getOrDefault("MODIFYDATE", "").toString();
					String modifyTime = map.getOrDefault("MODIFYTIME", "").toString();
					
					String memo = map.getOrDefault("MEMO", "").toString();
					String pfNo = map.getOrDefault("PFNO", "").toString();
					String bDate = map.getOrDefault("BDATE", "").toString();
					String rDate = map.getOrDefault("RDATE", "").toString();
					
					String pfOrderType = map.getOrDefault("PFORDERTYPE", "").toString();
					String status = map.getOrDefault("STATUS", "").toString();
					String totAmt = map.getOrDefault("TOTAMT", "").toString();
					String totCQty = map.getOrDefault("TOTCQTY", "").toString();
					String totPQty = map.getOrDefault("TOTPQTY", "").toString();
					
					String dateReferType = map.getOrDefault("DATEREFERTYPE", "").toString();
					String adjustWay = map.getOrDefault("ADJUSTWAY", "null").toString();
					String adjustValue = map.getOrDefault("ADJUSTVALUE", "0").toString();
					
//					lv1.setCreateBy(createBy);
//					lv1.setCreateByName(createByName);
//					lv1.setCreateDate(createDate);
//					lv1.setCreateTime(createTime);
//					lv1.setModifyBy(modifyBy);
//					lv1.setModifyByName(modifyByName);
//					lv1.setModifyDate(modifyDate);
//					lv1.setModifyTime(modifyTime);
//					lv1.setMemo(memo);
//					lv1.setPfNo(pfNo);
//					lv1.setbDate(bDate);
//					lv1.setrDate(rDate);
//					lv1.setPfOrderType(pfOrderType);
//					lv1.setStatus(status);
//					lv1.setTotAmt(totAmt);
//					lv1.setTotCQty(totCQty);
//					lv1.setTotPQty(totPQty);
					lv1.setDateReferType(dateReferType);
					lv1.setAdjustValue(adjustValue);
					lv1.setAdjustWay(adjustWay);
					lv1.setPluList(new ArrayList<DCP_PFOrderDetailRes.level2Elm>());
					lv1.setForecastList(new ArrayList<DCP_PFOrderDetailRes.level3Elm>());
					
					for (Map<String, Object> map2 : pluDatas) {
						
						DCP_PFOrderDetailRes.level2Elm lv2 = res.new level2Elm();
						String pluNo = map2.getOrDefault("PLUNO", "").toString();
						String pluName = map2.getOrDefault("PLUNAME", "").toString();
						String fileName = map2.getOrDefault("FILENAME", "").toString();
						String pUnit = map2.getOrDefault("PUNIT", "").toString();
						String guQingType = map2.getOrDefault("GUQINGTYPE", "").toString();
						String kQty = map2.getOrDefault("KQTY", "0").toString();
						String kAmt = map2.getOrDefault("KAMT", "0").toString();
						String price = map2.getOrDefault("PRICE", "0").toString();
						String preSaleQty = map2.getOrDefault("PRESALEQTY", "0").toString();
						String qty = map2.getOrDefault("QTY", "0").toString();
						String amt = map2.getOrDefault("AMT", "0").toString();
						
						String udLength = map2.getOrDefault("UDLENGTH", "0").toString();
						String pUnitName = map2.getOrDefault("PUNITNAME", "").toString();
						lv2.setpUnitName(pUnitName);
						lv2.setUdLength(udLength);
						
						lv2.setPluNo(pluNo);
						lv2.setPluName(pluName);
						lv2.setFileName(fileName);
						lv2.setpUnit(pUnit);
						lv2.setGuQingType(guQingType);
						lv2.setkQty(kQty);
						lv2.setkAmt(kAmt);
						lv2.setPrice(price);
						lv2.setPreSaleQty(preSaleQty);
						lv2.setQty(qty);
						lv2.setkAdjAmt(amt);
						
						lv2.setMealData(new ArrayList<DCP_PFOrderDetailRes.level4Elm>());
						
						for (Map<String, Object> map3 : workDatas) {
							if(pluNo.equals(map3.getOrDefault("PLUNO", "").toString()) ){
								
								DCP_PFOrderDetailRes.level4Elm lv4 = res.new level4Elm();
								String dtNo = map3.getOrDefault("DTNO", "").toString();
								
								if(dtNo.equals("") || dtNo == null){
									continue;
								}
								
								String dtName = map3.getOrDefault("DTNAME", "").toString();
								String beginTime = map3.getOrDefault("BEGINTIME", "").toString();
								String endTime = map3.getOrDefault("ENDTIME", "").toString();
								String dtkQty = map3.getOrDefault("DTKQTY", "0").toString();
								String lastSaleTime = map3.getOrDefault("LASTSALETIME", "").toString();
								String dtQty = map3.getOrDefault("DTQTY", "0").toString();
								lv4.setDtNo(dtNo);
								lv4.setDtName(dtName);
								lv4.setBeginTime(beginTime);
								lv4.setEndTime(endTime);
								lv4.setkQty(dtkQty);
								lv4.setLastSaleTime(lastSaleTime);
								lv4.setQty(dtQty);
								lv2.getMealData().add(lv4);
							}
							
						}
						
						
						lv1.getPluList().add(lv2);
						
					}
					
					
					for (Map<String, Object> map3 : forecastDatas) {
						DCP_PFOrderDetailRes.level3Elm lv3 = res.new level3Elm();
						String eDate = map3.getOrDefault("EDATE", "").toString();
						String dataType = map3.getOrDefault("DATATYPE", "").toString();
						String item = map3.getOrDefault("ITEM", "").toString();

						if(item.equals("") || item == null){
							continue;
						}
						
						String beginDate = map3.getOrDefault("BEGINDATE", "").toString();
						String endDate = map3.getOrDefault("ENDDATE", "").toString();
						String dayAmt = map3.getOrDefault("DAYAMT", "0").toString();
						String pfDay = map3.getOrDefault("PFDAY", "").toString();
						String city = map3.getOrDefault("CITY", "").toString();
						String pfHoliday = map3.getOrDefault("PFHOLIDAY", "").toString();
						String lunarPfHoliday = map3.getOrDefault("LUNARPFHOLIDAY", "").toString();
						String lunar = map3.getOrDefault("LUNAR", "").toString();
//						String pfHolidayNo = map3.getOrDefault("PFHOLIDAYNO", "").toString();
						String pfWeather = map3.getOrDefault("PFWEATHER", "").toString();
//						String pfWeatherNo = map3.getOrDefault("PFWEATHERNO", "").toString();
						String climate = map3.getOrDefault("CLIMATE", "").toString();
						String lowClimate = map3.getOrDefault("LOWCLIMATE", "").toString();
						String highClimate = map3.getOrDefault("HIGHCLIMATE", "").toString();
						String saleAmt = map3.getOrDefault("SALEAMT", "0").toString();
						String dinnerAmt = map3.getOrDefault("DINNERAMT", "0").toString();
						String tableAmt = map3.getOrDefault("TABLEAMT", "0").toString();
						String selected = map3.getOrDefault("SELECTED", "no").toString();
						
						SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMdd");  
						DateUtils du = DateUtils.getInstance();
						Date duan = format2.parse(eDate);
						
						if(!Check.Null(eDate)) eDate = du.format(duan, "yyyy-MM-dd") ;
						if(!Check.Null(beginDate)) beginDate = du.format(format2.parse(beginDate), "yyyy-MM-dd") ;
						if(!Check.Null(endDate)) endDate = du.format(format2.parse(endDate), "yyyy-MM-dd") ;
						
						lv3.seteDate(eDate);
						lv3.setDataType(dataType);
						lv3.setItem(item);
						lv3.setBeginDate(beginDate);
						lv3.setEndDate(endDate);
						lv3.setDayAmt(dayAmt);
						lv3.setPfDay(pfDay);
						lv3.setCity(city);
						lv3.setPfHoliday(pfHoliday);
						lv3.setLunarPfHoliday(lunarPfHoliday);
						lv3.setLunar(lunar);
//						lv3.setPfHolidayNo(pfHolidayNo);
						lv3.setPfWeather(pfWeather);
//						lv3.setPfWeatherNo(pfWeatherNo);
						lv3.setClimate(climate);
						lv3.setLowClimate(lowClimate);
						lv3.setHighClimate(highClimate);
						lv3.setSaleAmt(saleAmt);
						lv3.setDinnerAmt(dinnerAmt);
						lv3.setTableAmt(tableAmt);
						lv3.setSelected(selected);
						
						lv1.getForecastList().add(lv3);
						
					}
					
					res.getDatas().add(lv1);
				}
			}
			
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行异常！");
		}
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_PFOrderDetailReq req) throws Exception {
		// TODO Auto-generated method stub
		StringBuffer sqlbuf = new StringBuffer();
		String sql = "";
		//分页处理
		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();
		int startRow=(pageNumber-1) * pageSize;
		
		String pfNo = req.getRequest().getPfNo();
		
		String langType = req.getLangType();
		
		/**
		 * 
SELECT a.daterefertype , b.pluno , f.plu_name AS pluName , e.filename , b.punit , e.guqingtype , b.kqty , b.price ,
b.price*b.kqty AS kAmt , b.presaleqty , b.pqty AS qty , b.trueqty , b.kadjqty , b.amt , 
c.dtno , c.dtname , c.begin_time AS beginTime , c.end_time AS endTime , c.kqty AS dtKQTY , c.lastsaletime , c.qty AS dtQty , 
d.datatype , d.edate , d.item , d.begindate , d.enddate , d.dayamt , d.pfday , d.city , d.pfholiday  , 
d.lunar , d.lunarpfholiday   , d.pfweather , d.pfmatter , d.climate , d.saleamt , d.selected 
FROM DCP_porder_forecast a 
LEFT JOIN DCP_porder_forecast_detail b ON a.EID = b.EID AND a.SHOPID = b.SHOPID AND a.pfNo = b.pfNo 
LEFT JOIN DCP_porder_forecast_dinnertime c ON  a.EID = c.EID AND a.SHOPID = c.SHOPID AND a.pfNo = c.pfNo 
LEFT JOIN DCP_porder_forecastcalculate d ON  a.EID = d.EID AND a.SHOPID = d.SHOPID AND a.pfNo = d.pfNo 
LEFT JOIN DCP_GOODS e ON b.EID = e.EID AND b.pluno = e.pluNo AND e.status='100' 
LEFT JOIN DCP_GOODS_lang f ON b.EID = f.EID AND b.pluno = f.pluNo AND f.status='100' AND f.lang_type = 'zh_CN'

WHERE a.EID = '99' AND a.pfNo = 'YGYH2020021800001'


		 */
		sqlbuf.append(" "
				+ " SELECT a.daterefertype , b.Item as pluItem , b.pluno , f.plu_name AS pluName , e.filename , b.punit , e.guqingtype , b.kqty , b.price , " 
				+ " b.price*b.kqty AS kAmt , b.presaleqty , b.pqty AS qty , b.trueqty , b.kadjqty , b.amt , "
				+ " c.dtno , c.dtname , c.begin_time AS beginTime , c.end_time AS endTime , c.kqty AS dtKQTY , c.lastsaletime , c.qty AS dtQty , " 
				+ " d.datatype , d.edate , d.item , d.begindate , d.enddate , d.dayamt , d.pfday , d.city , d.pfholiday   , "
				+ " d.lunar , d.lunarpfholiday  , d.pfweather , d.pfmatter , d.climate , d.lowClimate , d.HIGHCLIMATE, d.saleamt , "
				+ " d.dinnerAmt, d.tableAmt , d.selected ,"
				+ " u.udlength , u.uname AS pUnitName , nvl(a.adjustWay, 'null') as adjustWay , nvl(a.adjustValue, '0') as adjustValue "
				+ " FROM DCP_porder_forecast a "
				+ " LEFT JOIN DCP_porder_forecast_detail b ON a.EID = b.EID AND a.SHOPID = b.SHOPID AND a.pfNo = b.pfNo "
				+ " LEFT JOIN DCP_porder_forecast_dinnertime c ON  b.EID = c.EID AND b.SHOPID = c.SHOPID AND b.pfNo = c.pfNo  AND b.pluNo = c.pluNo  " 
				+ " LEFT JOIN DCP_porder_forecastcalculate d ON  a.EID = d.EID AND a.SHOPID = d.SHOPID AND a.pfNo = d.pfNo "
				+ " LEFT JOIN DCP_GOODS e ON b.EID = e.EID AND b.pluno = e.pluNo AND e.status='100' "
				+ " LEFT JOIN DCP_GOODS_lang f ON b.EID = f.EID AND b.pluno = f.pluNo AND f.status='100' AND f.lang_type = '"+req.getLangType()+"' "
				+ " LEFT JOIN DCP_UNIT u ON u.EID = b.EID AND u.unit = b.punit AND u.status='100'  "
				
				+ " WHERE a.EID = '"+req.geteId()+"' and a.SHOPID = '"+req.getShopId()+"' AND a.pfNo = '"+req.getRequest().getPfNo()+"'"
				+ "  "
				);

		if (!Check.Null(pfNo))
		{
			sqlbuf.append(" and a.pfNo = '"+pfNo+"' ");
		}
		sqlbuf.append(" order by a.pfNo , b.item, b.pluNo , c.dtNo , d.item  " );
		sql = sqlbuf.toString();
		return sql;
	}

}
