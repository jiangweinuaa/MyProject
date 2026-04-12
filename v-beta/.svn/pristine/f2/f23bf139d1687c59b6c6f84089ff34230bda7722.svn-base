package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.util.log.Log;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_DosageQueryReq;
import com.dsc.spos.json.cust.req.DCP_DosageQueryReq.level2Elm;
import com.dsc.spos.json.cust.res.DCP_DosageQueryRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 需求日营业计算， 直接把数据插入到表中，不让在页面上显示(无理)
 * @author yuanyy
 * 
 */
public class DCP_DosageQuery extends SPosAdvanceService<DCP_DosageQueryReq, DCP_DosageQueryRes> {

	String mSql = ""; //商品sql 
	String aSql = ""; // 金额sql
	Logger logger = LogManager.getLogger(this.getClass());
	@Override
	protected void processDUID(DCP_DosageQueryReq req, DCP_DosageQueryRes res) throws Exception {
		// TODO Auto-generated method stub
		
		String eId = req.geteId();
		String shopId = req.getShopId();
		String adjustWay = req.getRequest().getAdjustWay();
		String adjustValue = req.getRequest().getAdjustValue();
		String pfNo = req.getRequest().getPfNo();
		
		// 如果有单号，就是修改（先删再插）。 如果没单号就是新增
		if(Check.Null(pfNo)){ 
			pfNo = this.getPFNO(req);
		}
		
		DelBean db1 = new DelBean("DCP_PORDER_FORECAST");
		db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		db1.addCondition("SHOPID", new DataValue(req.getShopId(), Types.VARCHAR));
		db1.addCondition("PFNO", new DataValue(pfNo, Types.VARCHAR));
		
		DelBean db2 = new DelBean("DCP_PORDER_FORECAST_DETAIL");
		db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		db2.addCondition("SHOPID", new DataValue(req.getShopId(), Types.VARCHAR));
		db2.addCondition("PFNO", new DataValue(pfNo, Types.VARCHAR));
		
		DelBean db3 = new DelBean("DCP_PORDER_FORECAST_DINNERTIME");
		db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		db3.addCondition("SHOPID", new DataValue(req.getShopId(), Types.VARCHAR));
		db3.addCondition("PFNO", new DataValue(pfNo, Types.VARCHAR));
		
		DelBean db4 = new DelBean("DCP_PORDER_FORECASTCALCULATE");
		db4.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		db4.addCondition("SHOPID", new DataValue(req.getShopId(), Types.VARCHAR));
		db4.addCondition("PFNO", new DataValue(pfNo, Types.VARCHAR));
		
		DelBean db5 = new DelBean("DCP_PORDER_FORECAST_MATERIAL");
		db5.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		db5.addCondition("SHOPID", new DataValue(req.getShopId(), Types.VARCHAR));
		db5.addCondition("PFNO", new DataValue(pfNo, Types.VARCHAR));
		
		this.addProcessData(new DataProcessBean(db5)); 
		this.addProcessData(new DataProcessBean(db4));  
		this.addProcessData(new DataProcessBean(db3));  
		this.addProcessData(new DataProcessBean(db2));  
		this.addProcessData(new DataProcessBean(db1)); 
		this.doExecuteDataToDB();
		
		String pfId = req.getRequest().getPfId();
		
		String bDate = req.getRequest().getbDate();
		String rDate = req.getRequest().getrDate();
		String pfOrderType = req.getRequest().getPfOrderType();
		String dateReferType = req.getRequest().getDateReferType(); // 枚举: avg：平均,fix：固定（默认）
		String memo = req.getRequest().getMemo();
		
		Calendar cal = Calendar.getInstance();// 获得当前时间
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String _SysDATE = df.format(cal.getTime());

		String createBy = req.getOpNO();
		String createDate = df.format(cal.getTime());
		df=new SimpleDateFormat("HHmmss");
		String createTime = df.format(cal.getTime());
		
		String beginDate = "";
		String endDate = "";
		String saleAmt = "0";
		String selectedSaleAmt = "0";
		String pfWeather = "";
		String pfHoliday = "";
		String pfMatter = "";
		String pfDay = "";
		String langType = req.getLangType();
		
		double totPqty = 0;
		double totAmt = 0;
		double totCqty = 0;
		double wqty = 0;
		BigDecimal unitRatio = new BigDecimal(1);
		
		List<level2Elm> fDatas = req.getRequest().getDatas(); 
		
		// 如果是平均值计算方式， 取datas 中第一个 开始截止日期 。
		if(fDatas != null){
			if(dateReferType.equals("avg")){
				
				for (level2Elm lv2 : fDatas) {
					String dataType = lv2.getDataType();
					
					if(!Check.Null(dataType) && dataType.equals("true")){
						pfWeather = lv2.getPfWeather();
						pfHoliday = lv2.getLunarPfHoliday();
						pfMatter = lv2.getPfMatter();
						pfDay = lv2.getPfDay();
						selectedSaleAmt = lv2.getSaleAmt();
					}
					
				}
				
				beginDate = fDatas.get(0).getBeginDate();
				endDate = fDatas.get(0).getEndDate();
				beginDate = beginDate.replace("-", "");
				endDate = endDate.replace("-", "");
				
			}
			// 如果是 参考固定日期， 需要查该日期的商品 ，营业额用户自行调整，不用查
			if(dateReferType.equals("fix")){
				for (level2Elm lv2 : fDatas) {
					String selected = lv2.getSelected();
					String dataType = lv2.getDataType();
					
					if(!Check.Null(selected) && selected.equals("yes") && dataType.equals("refer")){ 
						saleAmt = lv2.getSaleAmt();
						beginDate = lv2.getBeginDate();
						endDate = lv2.getEndDate();
						beginDate = beginDate.replace("-", "");
						endDate = endDate.replace("-", "");
					}
					
					if(dataType.equals("true")){
						pfWeather = lv2.getPfWeather();
						pfHoliday = lv2.getLunarPfHoliday();
						pfMatter = lv2.getPfMatter();
						pfDay = lv2.getPfDay();
						selectedSaleAmt = lv2.getSaleAmt();
					}
				}
			}
		}
		
		// 查询 批次编码,计算 
		String workSql = " select FNO  ,FNAME, BTIME , ETIME from ( "
				+ " SELECT   dtNo as FNO , dtName as FNAME ,  begin_time AS bTime , end_Time AS eTime "
				+ " from DCP_DINNERTIme where EID = '" + req.geteId() + "' and SHOPID = '" + req.getShopId()
				+ "' and status='100' " + " ) ";

		List<Map<String, Object>> workDatas = this.doQueryData(workSql, null);

		String materialSql = "";
		StringBuffer mSqlBuf = new StringBuffer();
		mSqlBuf.append(
				" SELECT  FNO, beginTime, endTime, p.pluno ,"
//							+ " bc.plubarcode,"
				+ " totqty, p.EID , p.SHOPID , c.punit , nvl(u.udlength, 2) as punitUdLength ,  "
						+ " c.wunit AS unit , d.min_qty AS minQty  , d.mul_qty  AS mulQty , e.plu_name  AS pluName , "
						+ " nvl(f.distriPrice, 0) as distriPrice , nvl(g.Price1 , 0 ) AS price ,"
						+ " NVL(pe.sale_qty, 0) as presaleqty , saleTime   FROM ( ");

		StringBuffer mSqlBuf1 = new StringBuffer();
		String mSql1 = "";

		if (workDatas.size() > 0) {
			StringBuffer sqlbuf = new StringBuffer();
			sqlbuf.append(" SELECT  FNO ,time ,totAmt    FROM (  ");
			String amtSql = "";

			for (int i = 0; i < workDatas.size(); i++) {
				String FNO = workDatas.get(i).getOrDefault("FNO", "").toString();
				String beginTime = workDatas.get(i).getOrDefault("BTIME", "000000").toString();
				String endTime = workDatas.get(i).getOrDefault("ETIME", "000000").toString();
				// 需要判断 时间段是否跨天, 如果跨天,需要分两段来设置

				mSqlBuf1.append(" SELECT  '" + FNO + "' as FNO, "
						+ " '" + beginTime + "' as beginTime ,  '" + endTime + "' As endTime, "
						+ " pluno, totqty, EID , SHOPID , unit , saleTime FROM ( "
						+ " SELECT a.EID , a.SHOPID ,b.pluno ,  b.unit ,  max(a.bdate || a.stime) as saletime ,  NVL(sum(CASE when type = '1' or type = '2' or type = '4' THEN  -qty ELSE qty end), 0) as totQty "
						+ " FROM DCP_SALE a "
						+ " LEFT JOIN DCP_SALE_detail b ON a.EID = b.EID AND a.SHOPID = b.SHOPID AND a.saleNO = b.saleNo "
						+ " WHERE a.EID = '" + req.geteId() + "' AND a.SHOPID = '" + req.getShopId() + "' "
						+ " and a.bDate between '" + beginDate + "' and '" + endDate + "' and   ");

				sqlbuf.append(" SELECT  '" + FNO + "' as FNO ,'"
						+ beginTime + "'||'" + endTime
						+ "' as time,nvl(sum(case WHEN type='1' or type='2' OR type='4' then -Pay_Amt else Pay_Amt end ) ,0)as totamt "
						+ " FROM DCP_SALE a   " + " WHERE    a.EID = '" + eId + "' AND a.SHOPID  = '" + shopId + "' "
						+ "  AND  ");


				sqlbuf.append(" bDate  between '"+beginDate+"' and '"+endDate+"' AND stime BETWEEN '" + beginTime + "' AND '"
						+ endTime + "' ");

				mSqlBuf1.append(" ( a.stime BETWEEN '" + beginTime + "' AND '" + endTime
						+ "' ) group by a.EID , a.SHOPID ,b.pluno ,  b.unit ) ");


				if (workDatas.size() > 1 && i < workDatas.size() - 1) {
					sqlbuf.append("union");
					mSqlBuf1.append(" union ");
				}

			}

			sqlbuf.append("  )   ");
			amtSql = sqlbuf.toString();

			mSql1 = mSqlBuf1.toString();

			mSqlBuf.append(mSql1);
			mSqlBuf.append(" )  p   ");

			mSqlBuf.append(
					" " + " LEFT JOIN DCP_GOODS c ON p.EID = c.EID AND   p.pluNO = c.pluNo AND p.unit = c.sunit and c.status='100' and (c.isForecast = 'Y' or c.isForecast is null ) "
							+ " LEFT JOIN DCP_GOODS_shop d ON p.EID = d.EID AND p.pluNo = d.pluNo AND p.SHOPID = d.organizationNO  "
							+ " LEFT JOIN DCP_GOODS_lang e ON p.EID = e.EID AND p.pluNO = e.pluNo and e.status='100' "
//										+ " LEFT JOIN DCP_BARCODE bc on p.EID = bc.EID and p.pluNo = bc.pluNO and p.unit = bc.unit "
							+ " LEFT JOIN DCP_UNIT u on p.EID = u.EID and p.unit = u.unit ");

			mSqlBuf.append("" + "   LEFT JOIN ( " + " select * from ( "
					+ " select a1.EID,a1.pluno,a1.unit,a1.distriPrice,IDX, row_number() over(partition by a1.EID,a1.pluno order by IDX) rn "
					+ " from ("
					+ " select EID,pluno,unit,price as distriPrice,Trade_Object,1 as IDX from DCP_GOODS_price_org"
					+ " where EID='" + eId + "' and organizationno='" + shopId
					+ "' and status='100' and EFFDate<='" + _SysDATE + "' and LEDate>='" + _SysDATE
					+ "' and Trade_type='0'" + " union"
					+ " select EID,pluno,unit,price as distriPrice,Trade_Object,2 as IDX  from DCP_GOODS_price "
					+ " where EID='" + eId + "' and status='100' and EFFDate<='" + _SysDATE
					+ "' and LEDate>='" + _SysDATE + "' and Trade_type='0' " + " ) a1  " + " ) WHERE rn = 1"
					+ " ) f ON c.pluNO = f.PLUNO AND c.EID=f.EID  " + " left join (select * from "
					+ " (select EID,pluno,unit,price1,IDX,row_number() over(partition by EID,pluno order by IDX) rn from "
					+ " (select EID,pluno,unit,price1,1 as IDX " + " from DCP_PRICE_shop " + " where EID='"
					+ eId + "' and  organizationNO ='" + shopId + "' and effdate<='" + _SysDATE + "'  "
					+ " and (LEDate>='" + _SysDATE + "' or LEDate is null) AND status='100' " + " union "
					+ " select EID,pluno,unit,price1,2 as IDX from DCP_PRICE " + " where EID='" + eId
					+ "' and effdate<='" + _SysDATE + "'  and (LEDate>='" + _SysDATE
					+ "' or LEDate is null) AND status='100') " + " ) WHERE rn = 1 "
					+ " ) g on c.EID=g.EID and c.pluNO = g.pluno   "
					+ ""
					+ " left join ( select a.EID, a.SHOPID, a.bdate, b.pluno, c.pluname, b.unit,sum(case"
                    + " when a.type = '1' or a.type = '2' or a.type = '4' then    -b.qty   ELSE  b.qty  end) as sale_qty, "
                    + " sum(CASE when a.type = '1' or a.type = '2' or a.type = '4' THEN     -b.amt  ELSE   b.amt end) as sale_amt"
                    + " from DCP_SALE a"
                    + " inner join DCP_SALE_detail b  on a.EID = b.EID and a.SHOPID = b.SHOPID  and a.saleno = b.saleno"
                    + " inner join DCP_GOODS c  on b.pluno = c.pluno and b.EID = c.EID"
                    + " where a.EID = '"+req.geteId()+"'  and a.bdate = '"+_SysDATE+"'   and a.type != 16   and (b.packagemaster != 'Y' or b.packagemaster is null) "
                    + " AND B.DISHESSTATUS != '3' AND B.DISHESSTATUS !='4'    "
                    + " group by a.EID, a.SHOPID, a.bdate, b.pluno, c.pluname, b.unit "
                    + " order by a.SHOPID ) pe "
                    + " on p.EID = pe.EID and p.SHOPID = pe.SHOPID and p.pluno = pe.pluno"
					+ ""
					+ ""
					+ "" + " WHERE p.EID = '" + eId
					+ "' AND c.EID = '" + eId + "' AND d.organizationNo  = '" + shopId
					+ "' AND e.lang_type = '" + langType + "' "

					+ " order by   fno ,   PLUNO ");

			materialSql = mSqlBuf.toString();

//			System.out.println("商品SQL：" + materialSql);
//			System.out.println("金额SQL：" + amtSql);
			
			mSql = materialSql;
			aSql = amtSql;

//			// String totAmt = "0";
//			List<Map<String, Object>> amtDatas = this.doQueryData(amtSql, null);
			
			
			// *************** 往 DCP_PLAN_MATERIAL 中插入数据 ****************
			SimpleDateFormat difsimDate = new SimpleDateFormat("yyyyMMdd");
			java.util.Date date1 = difsimDate.parse(beginDate);
			java.util.Date date2 = difsimDate.parse(endDate);
			int difdate = PosPub.differentDaysByMillisecond(date1, date2) + 1;

			// 计算期间营业额(如果是更新服务，预估营业额 和 参考营业额 需传参数设置)
			
			double avgAmt = Double.parseDouble(saleAmt);
			double predictAmt = Double.parseDouble(selectedSaleAmt);
			
			double rrate = 0;
			if(avgAmt == 0){
				rrate = 1;
			}else{
				rrate = predictAmt / avgAmt ;
			}
			
			double trate = 0;
			trate = predictAmt / 1000;
			
			// pluNoStr 查BOM的时候用 
			String pluNoStr = "''"; 
			List<Map<String,Object>> mainDatas = new CopyOnWriteArrayList<>(); // 不能用arrayList，remove 的时候会报错（序列化）
			List<Map<String, Object>> pluDatas = this.doQueryData(materialSql, null);
			
			Map<String, Boolean> conditionPlu = new HashMap<String, Boolean>(); //查詢條件
			conditionPlu.put("PLUNO", true);	
			//调用过滤函数
			List<Map<String, Object>> pluMainDatas = MapDistinct.getMap(pluDatas,conditionPlu);
			
			if (pluDatas.size() > 0) {
				
				String[] sModular = { "EID", "SHOPID", "PFNO", "DTNO","DTNAME", "PLUNO", "BEGIN_TIME","END_TIME",
						"PUNIT", "QTY", "LASTSALETIME", "KQTY"};
				
				for (Map<String, Object> pluMap : pluMainDatas) {
					String pluNo = pluMap.get("PLUNO").toString();
					String pUnit = pluMap.getOrDefault("PUNIT", "").toString();
					for(Map<String, Object> workMap : workDatas){
						String fNo = workMap.get("FNO").toString();
						String beginTime = workMap.get("BTIME").toString();
						String endTime = workMap.get("ETIME").toString();
						String dtName = workMap.get("FNAME").toString();	
						double sqtyDou = 0;
						double sqty = 0;
						double evQty = 0;
						
						String saleTime = "";
						for (Map<String, Object> map : pluDatas) {
							if(pluNo.equals(map.get("PLUNO").toString()) && fNo.equals(map.get("FNO").toString())){
								
//								BigDecimal unitRatio;
								double wunitRatio = 1;
								BigDecimal unitRatio2 = new BigDecimal(1);
								double punitRatio = 1; // 要货单位
								if(!map.get("UNIT").toString().equals(map.get("PUNIT").toString())){

									List<Map<String, Object>> getQData_Ratio = PosPub.getUnit_Ratio_Middle(dao, req.geteId(),
											map.get("PLUNO").toString(), map.get("UNIT").toString());
									
									if (getQData_Ratio == null || getQData_Ratio.isEmpty()) {
										throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品 " + map.get("PLUNO").toString() + "  单位："
												+ map.get("UNIT").toString() + " 找不到对应的库存单位换算关系");
									}
		
									unitRatio = (BigDecimal) getQData_Ratio.get(0).get("UNIT_RATIO");
									wunitRatio = unitRatio.doubleValue();
									
									List<Map<String, Object>> getQData_Ratio2 = PosPub.getUnit_Ratio_Middle(dao, req.geteId(),
											map.getOrDefault("PLUNO", "").toString(), map.getOrDefault("PUNIT", "").toString());
		
									if (getQData_Ratio2 == null || getQData_Ratio2.isEmpty()) {
										throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,
												"商品 " + map.getOrDefault("PLUNO", "").toString() + "  单位："
														+ map.getOrDefault("PUNIT", "").toString() + " 找不到对应的换算关系");
									}
		
									unitRatio2 = (BigDecimal) getQData_Ratio2.get(0).get("UNIT_RATIO");
		
									punitRatio = unitRatio2.doubleValue(); // 要货单位
									
								}
								
								// 这里设置数量保留小数位数, 默认为 0 ，即不保留小数，都是整数
								int length = Integer.parseInt(map.getOrDefault("PUNITUDLENGTH", "0").toString());
								sqtyDou = Double.parseDouble(map.getOrDefault("TOTQTY", "0").toString()) * rrate / difdate;
	
								sqty = Double.parseDouble(PosPub.GetdoubleScale(sqtyDou, length));
								
								// evQty 最终记录到商品 kQty 预估参考量上：如果 参照某一天就是 当天该商品该时段销售总量 除以1， 参照期间平均值，就除以日期差
								evQty = Double.parseDouble(map.getOrDefault("TOTQTY", "0").toString()) / difdate;
								
								saleTime = map.get("SALETIME").toString();
								if(saleTime != null){
									SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMddHHmmss");  
									Date duan = format2.parse(saleTime);
									
									Calendar calendar2 = Calendar.getInstance();
									calendar2.setTime(duan);
									Date gang = calendar2.getTime() ;
									
									SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
									saleTime = format.format(gang);
								}
								
							}	
							
						}
						

						DataValue[] sValue1 = null;
						sValue1 = new DataValue[] {
								// qty实际数量， kqty 参考数量 。 新增时 qty 默认给参考数量
								new DataValue(eId, Types.VARCHAR), 
								new DataValue(shopId, Types.VARCHAR),
								new DataValue(pfNo, Types.VARCHAR), 
								new DataValue(fNo, Types.VARCHAR),
								new DataValue(dtName, Types.VARCHAR),
								new DataValue(pluNo, Types.VARCHAR), 
								new DataValue(beginTime, Types.VARCHAR),
								new DataValue(endTime, Types.VARCHAR),
								new DataValue(pUnit, Types.VARCHAR),
								new DataValue(sqty, Types.VARCHAR), 
								new DataValue(saleTime, Types.VARCHAR), //最近消费时间
								new DataValue(evQty+"", Types.VARCHAR)  //KQTY 要除以天数差
						};
						
						InsBean ib2 = new InsBean("DCP_PORDER_FORECAST_DINNERTIME", sModular);
						ib2.addValues(sValue1);
						this.addProcessData(new DataProcessBean(ib2)); // 新增單頭
						
						
					}
				
				
				}
				
				
				
				//******************** 添加完 子表（DCP_PORDER_FORECAST_DETAIL）之后， 在原料表（DCP_PORDER_FORECAST_MATERIAL）中添加数据 ************8
				String[] columnsName = {
						"EID", "SHOPID", "ORGANIZATIONNO", "PFNO", "ITEM", "PLUNO", "PUNIT", "WUNIT", 
						"WQTY", "PRICE", "AMT",  "MEMO", "ISCLEAR", "PRESALEQTY", "TRUEQTY" ,"PQTY","KQTY","GUQINGTYPE"
				};
				

//				Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
//				condition.put("PLUNO", true);	
//				//调用过滤函数
//				List<Map<String, Object>> pluMainDatas = MapDistinct.getMap(pluDatas,condition);
				
				int item = 1;

				for (Map<String, Object> map : pluMainDatas) {
					
					Map<String, Object> main = new HashMap<String, Object>();
					
					int insColCt = 0;	
					DataValue[] columnsVal = new DataValue[columnsName.length];
					
					String pluNO = "";
					String punit = "";
					String wunit = "";
					double pluQty = 0;
					double pluKQty = 0;

					String price = "0";
					for (int i = 0; i < columnsVal.length; i++) { 
						String keyVal = null;
						switch (i) {
						case 0:
							keyVal = eId;	
							totCqty = totCqty + 1;
							break;
						case 1:
							keyVal = shopId;
							break;
						case 2:
							keyVal = shopId;
							break;
						case 3:  
							keyVal = pfNo;
							break;
						case 4:  
							keyVal = item+"";
							item = item + 1;
							break;
						case 5:  
							keyVal = map.get("PLUNO").toString();
							pluNO = keyVal;
							pluNoStr = pluNoStr + " ,'"+keyVal+"'";
							main.put("pluNo", pluNO);
							
							//过滤 pluDatas ，计算该商品总应报数量

							for(Map<String, Object> map2 : pluDatas){
								if(pluNO.equals(map2.get("PLUNO").toString())){

									int length = Integer.parseInt(map2.getOrDefault("PUNITUDLENGTH", "0").toString());
									double sqtyDou = Double.parseDouble(map2.getOrDefault("TOTQTY", "0").toString()) * rrate / difdate;
									
									double sqty = Double.parseDouble(PosPub.GetdoubleScale(sqtyDou, length));
									
									double kQtyDou = Double.parseDouble(map2.getOrDefault("TOTQTY", "0").toString()) / difdate;
									pluKQty = pluKQty + kQtyDou;
									
									pluQty = pluQty + sqty;
									
								}
							}
							
							break;
						case 6:  
							keyVal = map.get("PUNIT").toString();
							punit = keyVal;
							main.put("PUNIT", punit);
							break;			
						case 7:  
							keyVal = map.get("UNIT").toString();
							main.put("WUNIT", keyVal);
							break;
						case 8:  
							keyVal = String.format("%.2f", wqty);
							main.put("WQTY", keyVal);
							break;
						case 9:  
							price = PosPub.GetdoubleScale(Double.parseDouble(map.get("PRICE").toString()), 2);
							keyVal = price;    //price
							main.put("PRICE", price);
							break;
						case 10:  
							keyVal = PosPub.GetdoubleScale(Double.parseDouble(price) * pluQty,2);
							totAmt = totAmt + Float.parseFloat(keyVal);
							break;
						case 11:  
							keyVal = ""; //memo
							break;
						case 12:  
							keyVal = "";//ISCLEAR
							break;
						case 13:  
							keyVal = map.getOrDefault("PRESALEQTY", "0").toString(); //preSaleQty 
							main.put("PRESALEQTY", keyVal);
							break;
						case 14:  
							keyVal = pluQty+""; //trueQty
							main.put("qty", pluQty);
							mainDatas.add(main);
							break;	
							
						case 15:  
							keyVal = pluQty+""; //PQTY
							totPqty = pluQty + totPqty;
							break;	
						case 16:  
							keyVal = pluKQty+""; //KQTY
							break;	
						case 17:  
							keyVal = map.getOrDefault("GUQINGTYPE", "0").toString();
							break;	
							
						default:
							break;
						}

						if (keyVal != null) {
							insColCt++;
//						if (i == 2 ){
//							columnsVal[i] = new DataValue(keyVal, Types.INTEGER);	
//						}else if (i == 5 || i == 7 || i == 8 || i == 9 || i == 10|| i == 14|| i == 15|| i == 16|| i == 17|| i == 18 || i == 19 || i == 20|| i == 21  ){
//							columnsVal[i] = new DataValue(keyVal, Types.FLOAT);
//						}else{
								columnsVal[i] = new DataValue(keyVal, Types.VARCHAR);
//						}
						} else {
							columnsVal[i] = null;
						}
					}
					
					String[] columns2 = new String[insColCt];
					DataValue[] insValue2 = new DataValue[insColCt];
					// 依照傳入參數組譯要insert的欄位與數值；
					insColCt = 0;

					for (int i=0;i<columnsVal.length;i++){
						if (columnsVal[i] != null){
							columns2[insColCt] = columnsName[i];
							insValue2[insColCt] = columnsVal[i];
							insColCt ++;
							if (insColCt >= insValue2.length) break;
						}
					}

					InsBean ib2 = new InsBean("DCP_PORDER_FORECAST_DETAIL", columns2);
					ib2.addValues(insValue2);
					this.addProcessData(new DataProcessBean(ib2));
					
					mainDatas.add(main);
					
				}

			}
			
			else{ //如果没有商品信息，提示没有商品信息可供参考
				
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "参考日期没有可供参考的商品销售记录，请重新选择");
				
			}
			
			//*******成品根据BOM查到商品原料，插入第三张原料表（DCP_PORDER_FORECAST_MATERIAL）
			String bomSql = this.getBomDatas(req, pluNoStr);
			// 这个sql语句很长，这里经常出现 原料计算不对的问题，所以加上sql日志。 后期可以去掉
			logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"********* DCP_DosageQuery 查询原料sql:" + bomSql );
			
			List<Map<String, Object>> bomDatas = this.doQueryData(bomSql, null);
			if(!bomDatas.isEmpty()){
				
				//第一步：过滤出所有的原料
				Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
				condition.put("MATERIAL_PLUNO", true);		
				//调用过滤函数
				List<Map<String, Object>> materilDatas=MapDistinct.getMap(bomDatas, condition);
				
				//过滤出有BOM信息的成品，这些成品不能插入到原料表， 没有BOM的成品需要插入到原料表
				Map<String, Boolean> condition2 = new HashMap<String, Boolean>(); //查詢條件
				condition2.put("PLUNO", true);		
				//调用过滤函数
				List<Map<String, Object>> mainPluDatas = MapDistinct.getMap(bomDatas, condition2);
				
				int item = 0; 
				for (int i = 0; i < materilDatas.size(); i++) {
					
					String material_pluNo = materilDatas.get(i).get("MATERIAL_PLUNO").toString();
					String material_pluName = materilDatas.get(i).get("MATERIAL_PLUNAME").toString();
					
					String material_pUnit = materilDatas.get(i).get("MATERIAL_PUNIT").toString(); //原料配方单位
					String material_wUnit = materilDatas.get(i).get("MATERIAL_WUNIT").toString(); //原料库存单位
					String punit = materilDatas.get(i).get("PUNIT").toString(); //原料要货单位
					String pUnitUdLengthStr = materilDatas.get(i).get("PUNITUDLENGTH").toString(); //原料要货单位保留位数
					int pUnitUdLength = Integer.parseInt(pUnitUdLengthStr);
					
					double bomUnitRatio = 1; // 配方单位到库存单位的单位转换率
					double pUnitRatio = 1; // 要货单位到库存单位的单位转换率
					
					//2019-12-03 需要判断原料的配方单位和库存单位是否相等，如果不等，先转换为库存单位，再转换为要货单位。
					//需要计算不同单位对应数量和进货价。
					if(!material_pUnit.equals(material_wUnit)){ 
						
						List<Map<String, Object>> getQData_Ratio = PosPub.getUnit_Ratio_Middle(dao, req.geteId(),
							material_pluNo, material_pUnit); 
						
						if (getQData_Ratio == null || getQData_Ratio.isEmpty()) {
							throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "配方商品 " + material_pluNo + " 找不到对应的 "+material_pUnit+" 到"+material_wUnit+" 的换算关系");
						}
						
						bomUnitRatio = Double.parseDouble(getQData_Ratio.get(0).getOrDefault("UNIT_RATIO","1").toString());
						
						//第一步：如果配方单位和库存单位相等， 就直接查库存单位到要货单位的转换率
						List<Map<String, Object>> getQData_Ratio2 = PosPub.getUnit_Ratio_Middle(dao, req.geteId(),
								material_pluNo, punit);
						
						if (getQData_Ratio2 == null || getQData_Ratio2.isEmpty()) {
							throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "配方商品 " + material_pluNo + " 找不到对应的 "+punit+" 到"+material_wUnit+" 的换算关系");
						}
						
						pUnitRatio = Double.parseDouble(getQData_Ratio2.get(0).getOrDefault("UNIT_RATIO","1").toString());
					}
					
						
					String sQty = materilDatas.get(i).getOrDefault("SQTY","0").toString();
					String material_price = materilDatas.get(i).getOrDefault("MATERIAL_PRICE", "0").toString();
//					double minQty = 0; // 最小要货量（每个成品单位1对应的原料数 相加得到） 不能这么算，不合逻辑
					double pqty = 0; // 所有成品所需的原料数相加得到该值
					double material_wqty = 0;
					double amt = 0;
					double unitRatio2 = 0;
					double reqWQty = 0;
					String mainPluStr = "";
					
					String mwunit = "";
					//第二步：计算原料的应要数量
					for (Map<String, Object> pluMap : bomDatas) {
						
						if(material_pluNo.equals(pluMap.get("MATERIAL_PLUNO").toString())){
							String pluNo = pluMap.get("PLUNO").toString();
							
							String pluName = pluMap.get("PLUNAME").toString();
							String qty = "0";
							double mainWQty  = 0;  
							// 下面这个循环体从上面的成品中提取
							for (Map<String, Object> mainMap : mainDatas) {
								if(pluNo.equals(mainMap.get("pluNo").toString())){
									mainWQty = Double.parseDouble(mainMap.getOrDefault("qty","0").toString()); //要货单位对应的数量
								}
							}
							String material_baseQty = pluMap.getOrDefault("MATERIAL_BASEQTY","0").toString();
							String baseQty = pluMap.getOrDefault("BASEQTY","0").toString();
							
							double epqty = 0 ;
							if(Double.parseDouble(baseQty) != 0){
								epqty = Double.parseDouble(material_baseQty)/Double.parseDouble(baseQty) * mainWQty;
							}
							
							pqty = pqty + epqty;
							
//							minQty = Float.parseFloat(material_baseQty) / Float.parseFloat(baseQty); 
							mainPluStr = mainPluStr+ pluName+"("+mainWQty+")\r";
							
						}
					}
					
					material_wqty = pqty * pUnitRatio;
					
					amt = pqty * Float.parseFloat(material_price);
					
					double materialPqty = 0;
					
					// 举个栗子： 配方单位是g， 库存单位是 BAO， 要货单位是xiang 
					// 单位转换信息： 克==》包 （1000：1） ，箱==》包 （1：10），设置不同的单位到库存单位的转换信息即可
					materialPqty = pqty * bomUnitRatio / pUnitRatio ;
					
					Double materialKQty = materialPqty; //materialKQty 对应原料表的KQty 字段， 是根据成品用量计算出原料的bom 用量，不经过倍量和最小要货量计算的数值。
					
					//2019-12-23 根据返回的最小要货量，最大要货量，要货倍量，根据二分法再算一遍要货量
					//举个例子： 如 商品最小要货量为0，最大要货量为10000， 倍量为5， 
					//千元用量计算出的预估数为12， 最接近12的5的倍数为10， 所以10就是最终预估量。
					//千元用量计算出的预估数为13， 最接近13的5的倍数为15， 所以15就是最终预估量。
					
					String minQty = materilDatas.get(i).get("MINQTY").toString(); //最小要货量
					String maxQty = materilDatas.get(i).get("MAXQTY").toString(); //最大要货量
					String mulQty = materilDatas.get(i).get("MULQTY").toString(); //要货倍量
					
					int num = 1 ;
					
					double mulQtyDou = Double.parseDouble(mulQty);
					double minQtyDou = Double.parseDouble(minQty);
					if(materialPqty <= minQtyDou){ //如果 预估数量 < 最小要货量， 预估数量==最小要货量。
						materialPqty = minQtyDou;
					}else{
						
						if(mulQtyDou > 0){ //倍量有可能为0 ，表示任意数都可以。

							double zh = materialPqty / mulQtyDou ;
							BigDecimal zhb = new BigDecimal(zh);
							double maxNum = zhb.setScale(0, BigDecimal.ROUND_DOWN).doubleValue();
							String a = new DecimalFormat("0").format(maxNum);
							num = Integer.parseInt(a);
							
							Double[] dArr = new Double[num];
							if(num > 0 ){
								for (int j = 0; j < num ; j++) {
									double d2 = mulQtyDou * j ;
									BigDecimal b = new BigDecimal(d2);
							        /*setScale 第一个参数为保留位数 第二个参数为舍入机制
							         BigDecimal.ROUND_DOWN 表示不进位 
							         BigDecimal.ROUND_UP表示进位*/
							        d2 = b.setScale(pUnitUdLength, BigDecimal.ROUND_HALF_UP).doubleValue();
									dArr[j] = new Double(d2);
								}
								
							}
							
							MyCommon mc = new MyCommon();
							materialPqty = mc.getNumberThree(dArr, materialPqty);
							mc = null;
						}
						
						
						if(materialPqty > Double.parseDouble(maxQty)){ //如果预估量 > 最大要货量， 预估量==最大要货量即可
							materialPqty = Double.parseDouble(maxQty);
						}
						
					}
					
					if (unitRatio2 > 0){
						reqWQty = Float.parseFloat(sQty) * pUnitRatio; // sQty 是库存单位对应的数量， 需要转换为要货单位对应的数量
					}
					else{
						reqWQty = 0 ;  // 防止出现查不到换算率、 或者  换算率设置为 0  这种情况，属于资料不正确
					}
					
					String[] columns2 = {
							"SHOPID", "OrganizationNO","EID","PFNO","ITEM", "MATERIAL_PLUNO",   "PUNIT", 
							"WUNIT", "WQTY", "PQTY", "MAX_QTY", "MIN_QTY","MUL_QTY", "PRICE", 
							"AMT" , "KQTY", "KADJQTY" , "REQ_WQTY", "REF_AMT" , "ADJ_AMT" , "MEMO",
							"MAINPLU" , "BDATE" ,
							// 2019-10-28 增加以下字段，用于物料报单
							"DBYQTY","YWQTY","RQTY","UQTY","DQTY","TQTY","STATUS"
							
					};
					DataValue[] insValue2 = null;
					
					// 插入主表数据
					insValue2 = new DataValue[]{
							new DataValue(shopId, Types.VARCHAR), 
							new DataValue(shopId, Types.VARCHAR), 
							new DataValue(eId, Types.VARCHAR),
							new DataValue(pfNo, Types.VARCHAR), 
							new DataValue(item + 1 +"", Types.VARCHAR), 
							new DataValue(material_pluNo, Types.VARCHAR),
							new DataValue(punit, Types.VARCHAR),
							new DataValue(material_wUnit,  Types.VARCHAR), // status 0:新建
							new DataValue(String.format("%.2f", material_wqty), Types.VARCHAR),
//							new DataValue(String.format("%."+pUnitUdLengthStr+"f", materialPqty - reqWQty), Types.VARCHAR),  // 实报数量 = 应报数量 - 实存
							new DataValue(String.format("%."+pUnitUdLengthStr+"f", materialPqty ), Types.VARCHAR),  // 实报数量 = 应报数量 - 实存
							new DataValue(maxQty, Types.VARCHAR), //最大最小要货量， 给默认值
							new DataValue(minQty, Types.VARCHAR), 
							new DataValue(mulQty, Types.VARCHAR), 
							new DataValue(String.format("%.2f", Double.parseDouble(material_price)), Types.VARCHAR), 
							new DataValue(String.format("%.2f", amt), Types.VARCHAR),
							new DataValue(String.format("%."+pUnitUdLengthStr+"f", materialKQty), Types.VARCHAR), // 应报数量（千元用量）
							new DataValue("0", Types.VARCHAR), // 默认给 0 ，调整量 和 调整金额都默认给0， 以后如果客户需要添加试菜/员工餐，这个就能用到
							new DataValue(String.format("%.2f", reqWQty), Types.VARCHAR), // req_wqty 参考库存量
							new DataValue(String.format("%.2f", amt), Types.VARCHAR),
							new DataValue("0", Types.VARCHAR),
							new DataValue(memo, Types.VARCHAR),
							new DataValue(mainPluStr, Types.VARCHAR),
							new DataValue(bDate.replace("-", ""), Types.VARCHAR),
							new DataValue("0", Types.VARCHAR),
							new DataValue("0", Types.VARCHAR),
							new DataValue("0", Types.VARCHAR),
							new DataValue("0", Types.VARCHAR),
							new DataValue("0", Types.VARCHAR),
							new DataValue("0", Types.VARCHAR),
							new DataValue("", Types.VARCHAR)
							
					};

					InsBean ib2 = new InsBean("DCP_PORDER_FORECAST_MATERIAL", columns2);
					ib2.addValues(insValue2);
					this.addProcessData(new DataProcessBean(ib2)); // 新增單頭
					
					item = item + 1;
					
				}
				
				
				
				// 先去重，去掉有BOM的成品，剩下没有BOM的成品， 需要插入到子表中
				for (Map<String,Object> mainPluMap :  mainDatas) {
					String mainPluNo = mainPluMap.get("pluNo").toString();
					
					if(Check.Null(mainPluNo)){
						continue;
					}
					
					if( mainPluDatas != null && !mainPluDatas.isEmpty() ){
						for (Map<String, Object> map : mainPluDatas) {
//							if(mainPluNo.equals(map.get("PLUNO").toString())){ // 如果请求传进来的 pluNo 里有 BOM资料， 该pluNo就不插入原料表
//								mainDatas.remove(mainPluMap);
//							}
							
							Iterator<Map<String, Object>> it = mainDatas.iterator();
					        while (it.hasNext()){
					        	Map<String, Object> s = it.next();
					        	if(mainPluNo.equals(map.get("PLUNO").toString())){ // 如果请求传进来的 pluNo 里有 BOM资料， 该pluNo就不插入原料表
					            	mainDatas.remove(s); //注意这里调用的是集合的方法
					            }
					        }
							
						}
					}
					
				}
				
				//开始过滤没有BOM 的商品，这些也需要插入到 DCP_PORDER_FORECAST_MATERIAL 中
				for (Map<String,Object> mainPluMap :  mainDatas) {
					
					String mainPluNo = mainPluMap.get("pluNo").toString();
					if(Check.Null(mainPluNo)){
						continue;
					}
					
					String[] columns2 = {
							"SHOPID", "OrganizationNO","EID","PFNO","ITEM", "MATERIAL_PLUNO",   "PUNIT", 
							"WUNIT", "WQTY", "PQTY", "MAX_QTY", "MIN_QTY", "PRICE", 
							"AMT" , "KQTY", "KADJQTY" , "REQ_WQTY", "REF_AMT" , "ADJ_AMT" , "MEMO",
							"MAINPLU" , "BDATE" ,
							// 2019-10-28 增加以下字段，用于物料报单
							"DBYQTY","YWQTY","RQTY","UQTY","DQTY","TQTY","STATUS"
							
					};
					DataValue[] insValue2 = null;
					
					// 插入主表数据
					insValue2 = new DataValue[]{
							new DataValue(shopId, Types.VARCHAR), 
							new DataValue(shopId, Types.VARCHAR), 
							new DataValue(eId, Types.VARCHAR),
							new DataValue(pfNo, Types.VARCHAR), 
							new DataValue(item + 1, Types.VARCHAR), 
							new DataValue(mainPluNo, Types.VARCHAR),
							new DataValue(mainPluMap.get("PUNIT").toString(), Types.VARCHAR),
							new DataValue(mainPluMap.getOrDefault("WUNIT","").toString(),  Types.VARCHAR), 
							new DataValue(mainPluMap.getOrDefault("WQTY","").toString(), Types.VARCHAR), //WQTY
							new DataValue(mainPluMap.get("qty").toString(), Types.VARCHAR), 
							new DataValue("999999", Types.VARCHAR), //最大最小要货量， 给默认值
							new DataValue("0", Types.VARCHAR), 
							new DataValue(mainPluMap.get("PRICE").toString(), Types.VARCHAR), 
							new DataValue(mainPluMap.get("KAMT").toString(), Types.VARCHAR),
							new DataValue(mainPluMap.get("qty").toString(), Types.VARCHAR), // 应报数量（千元用量）
							new DataValue("0", Types.VARCHAR), // 默认给 0 ，调整量 和 调整金额都默认给0， 以后如果客户需要添加试菜/员工餐，这个就能用到
							new DataValue("0", Types.VARCHAR), // req_wqty 参考库存量
							new DataValue(mainPluMap.get("KADJAMT").toString(), Types.VARCHAR),
							new DataValue("0", Types.VARCHAR),
							new DataValue("", Types.VARCHAR),
							new DataValue(mainPluNo + "("+mainPluMap.get("qty").toString()+")", Types.VARCHAR),
							new DataValue(bDate.replace("-", ""), Types.VARCHAR),
							new DataValue("0", Types.VARCHAR),
							new DataValue("0", Types.VARCHAR),
							new DataValue("0", Types.VARCHAR),
							new DataValue("0", Types.VARCHAR),
							new DataValue("0", Types.VARCHAR),
							new DataValue("0", Types.VARCHAR),
							new DataValue("", Types.VARCHAR)
							
					};

					InsBean ib2 = new InsBean("DCP_PORDER_FORECAST_MATERIAL", columns2);
					ib2.addValues(insValue2);
					this.addProcessData(new DataProcessBean(ib2)); // 新增單頭
					
					item = item + 1;
					
				}
				
				
			}
			else{
				

				
				//开始过滤没有BOM 的商品，这些也需要插入到 DCP_PORDER_FORECAST_MATERIAL 中
				int item = 0;
				for (Map<String,Object> mainPluMap :  mainDatas) {
					
					String mainPluNo = mainPluMap.get("pluNo").toString();
					if(Check.Null(mainPluNo)){
						continue;
					}
					
					String[] columns2 = {
							"SHOPID", "OrganizationNO","EID","PFNO","ITEM", "MATERIAL_PLUNO",   "PUNIT", 
							"WUNIT", "WQTY", "PQTY", "MAX_QTY", "MIN_QTY", "PRICE", 
							"AMT" , "KQTY", "KADJQTY" , "REQ_WQTY", "REF_AMT" , "ADJ_AMT" , "MEMO",
							"MAINPLU" , "BDATE" ,
							// 2019-10-28 增加以下字段，用于物料报单
							"DBYQTY","YWQTY","RQTY","UQTY","DQTY","TQTY","STATUS"
							
					};
					DataValue[] insValue2 = null;
					
					// 插入主表数据
					insValue2 = new DataValue[]{
							new DataValue(shopId, Types.VARCHAR), 
							new DataValue(shopId, Types.VARCHAR), 
							new DataValue(eId, Types.VARCHAR),
							new DataValue(pfNo, Types.VARCHAR), 
							new DataValue(item + 1, Types.VARCHAR), 
							new DataValue(mainPluNo, Types.VARCHAR),
							new DataValue(mainPluMap.get("PUNIT").toString(), Types.VARCHAR),
							new DataValue(mainPluMap.getOrDefault("WUNIT","").toString(),  Types.VARCHAR), 
							new DataValue(mainPluMap.getOrDefault("WQTY","").toString(), Types.VARCHAR), //WQTY
							new DataValue(mainPluMap.get("qty").toString(), Types.VARCHAR), 
							new DataValue("999999", Types.VARCHAR), //最大最小要货量， 给默认值
							new DataValue("0", Types.VARCHAR), 
							new DataValue(mainPluMap.get("PRICE").toString(), Types.VARCHAR), 
							new DataValue(mainPluMap.get("KAMT").toString(), Types.VARCHAR),
							new DataValue(mainPluMap.get("qty").toString(), Types.VARCHAR), // 应报数量（千元用量）
							new DataValue("0", Types.VARCHAR), // 默认给 0 ，调整量 和 调整金额都默认给0， 以后如果客户需要添加试菜/员工餐，这个就能用到
							new DataValue("0", Types.VARCHAR), // req_wqty 参考库存量
							new DataValue(mainPluMap.get("KADJAMT").toString(), Types.VARCHAR),
							new DataValue("0", Types.VARCHAR),
							new DataValue("", Types.VARCHAR),
							new DataValue(mainPluNo + "("+mainPluMap.get("qty").toString()+")", Types.VARCHAR),
							new DataValue(bDate.replace("-", ""), Types.VARCHAR),
							new DataValue("0", Types.VARCHAR),
							new DataValue("0", Types.VARCHAR),
							new DataValue("0", Types.VARCHAR),
							new DataValue("0", Types.VARCHAR),
							new DataValue("0", Types.VARCHAR),
							new DataValue("0", Types.VARCHAR),
							new DataValue("", Types.VARCHAR)
							
					};

					InsBean ib2 = new InsBean("DCP_PORDER_FORECAST_MATERIAL", columns2);
					ib2.addValues(insValue2);
					this.addProcessData(new DataProcessBean(ib2)); // 新增單頭
					
					item = item + 1;
				}
				
			}
			
			
		}
		
		
		
		
		//********* DCP_PORDER_FORECASTCALCULATE 预估参照表  开始*******
		String predictAmt = "0";
		if(fDatas != null){
			for (level2Elm lv2 : fDatas) {
				String eDate = lv2.geteDate();
				String item = lv2.getItem();
				String dataType = lv2.getDataType();
				String evbeginDate = lv2.getBeginDate();
				String evendDate = lv2.getEndDate();
				String evpfDay = lv2.getPfDay();
				String city = lv2.getCity();
				String lunarPfHoliday = lv2.getLunarPfHoliday();
				String lunar = lv2.getLunar();
				String evpfWeather = lv2.getPfWeather();
				String lowClimate = lv2.getLowClimate();
				String highClimate = lv2.getHighClimate();
				String evsaleAmt = lv2.getSaleAmt();
				String dinnerAmt = lv2.getDinnerAmt();
				String tableAmt = lv2.getTableAmt();
				String evpfMatter = lv2.getPfMatter();
				String selected = lv2.getSelected();
				
				if(dataType.equals("true")){
					predictAmt = evsaleAmt;
				}
				
				String[] columns1 = { "EID","ORGANIZATIONNO","SHOPID","PFNO","DATATYPE","ITEM","EDATE","BEGINDATE","ENDDATE",
						"PFDAY","CITY","LUNARPFHOLIDAY","LUNAR","PFWEATHER","LOWCLIMATE","HIGHCLIMATE","PFMATTER","SALEAMT",
						"DINNERAMT","TABLEAMT","SELECTED","MEMO" };
				DataValue[] insValue1 = null;
				
				insValue1 = new DataValue[]{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(shopId, Types.VARCHAR),
						new DataValue(shopId, Types.VARCHAR),
						new DataValue(pfNo, Types.VARCHAR),
						new DataValue(dataType, Types.VARCHAR),
						new DataValue(item, Types.VARCHAR),
						new DataValue(eDate.replace("-", ""), Types.VARCHAR),
						new DataValue(evbeginDate.replace("-", ""), Types.VARCHAR),
						new DataValue(evendDate.replace("-", ""), Types.VARCHAR),
						new DataValue(evpfDay, Types.VARCHAR),
						new DataValue(city, Types.VARCHAR),
						new DataValue(lunarPfHoliday, Types.VARCHAR),
						new DataValue(lunar, Types.VARCHAR),
						new DataValue(evpfWeather, Types.VARCHAR),
						new DataValue(lowClimate, Types.VARCHAR),
						new DataValue(highClimate, Types.VARCHAR),
						new DataValue(evpfMatter, Types.VARCHAR),
						new DataValue(evsaleAmt, Types.VARCHAR),
						new DataValue(dinnerAmt, Types.VARCHAR),
						new DataValue(tableAmt, Types.VARCHAR),
						new DataValue(selected, Types.VARCHAR),
						new DataValue(memo, Types.VARCHAR)
					};
				
				InsBean ib1 = new InsBean("DCP_PORDER_FORECASTCALCULATE", columns1);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1)); // 新增單頭
				
			}
		}
				
		//********* DCP_PORDER_FORECASTCALCULATE 预估参照表  结束 ***********
		
		
		//********* DCP_PORDER_FORECAST 营业预估主表 *********
		String[] columnsMain = {
				"SHOPID", "OrganizationNO","EID","PFNO","BDATE", "RDATE",   "MEMO", 
				"STATUS", "CREATEBY","CREATE_DATE", "CREATE_TIME", "TOT_PQTY", "TOT_AMT", "TOT_CQTY", 
				"PF_ID" , "BEGINDATE" , "ENDDATE" , 
				"PFWEATHER" , "PFDAY", "PFHOLIDAY", "PFMATTER" ,"ADJUSTWAY" , "ADJUSTVALUE" , "DATEREFERTYPE" , "PFORDERTYPE",
				"MODIFYBY","MODIFY_DATE", "MODIFY_TIME","PREDICTAMT"
		};
		
		DataValue[] insValueMain = null;
		
		// 插入主表数据
		insValueMain = new DataValue[]{
				new DataValue(shopId, Types.VARCHAR), 
				new DataValue(shopId, Types.VARCHAR), 
				new DataValue(eId, Types.VARCHAR),
				new DataValue(pfNo, Types.VARCHAR), 
				new DataValue(bDate.replace("-", ""), Types.VARCHAR), 
				new DataValue(rDate.replace("-", ""), Types.VARCHAR),
				new DataValue(memo, Types.VARCHAR),
				new DataValue("0", Types.VARCHAR), // status 0:新建
				new DataValue(createBy, Types.VARCHAR),
				new DataValue(createDate, Types.VARCHAR), 
				new DataValue(createTime, Types.VARCHAR),
				new DataValue(String.format("%.2f", totPqty), Types.VARCHAR), 
				new DataValue(String.format("%.2f", totAmt), Types.VARCHAR), 
				new DataValue(totCqty, Types.VARCHAR),
				new DataValue(pfId, Types.VARCHAR),
//				new DataValue(String.format("%.2f", Double.parseDouble(predictAmt)), Types.VARCHAR),
				new DataValue(beginDate, Types.VARCHAR),
				new DataValue(endDate, Types.VARCHAR),
//				new DataValue(String.format("%.2f", Double.parseDouble(avgSaleAmt)), Types.VARCHAR),
//				new DataValue(String.format("%.2f", Double.parseDouble(modifRatio)), Types.VARCHAR),
				new DataValue(pfWeather, Types.VARCHAR),
				new DataValue(pfDay, Types.VARCHAR),
				new DataValue(pfHoliday, Types.VARCHAR),
				new DataValue(pfMatter, Types.VARCHAR),
				
				new DataValue(adjustWay, Types.VARCHAR),
				new DataValue(adjustValue, Types.VARCHAR),
				new DataValue(dateReferType, Types.VARCHAR),
				new DataValue(pfOrderType, Types.VARCHAR),
				new DataValue(createBy, Types.VARCHAR),
				new DataValue(createDate, Types.VARCHAR), 
				new DataValue(createTime, Types.VARCHAR),
				new DataValue(predictAmt, Types.VARCHAR)
				
		};

		InsBean ibMain = new InsBean("DCP_PORDER_FORECAST", columnsMain);
		ibMain.addValues(insValueMain);
		this.addProcessData(new DataProcessBean(ibMain)); // 新增單頭
		
		
		
		this.doExecuteDataToDB();
		
		res.setDatas(new ArrayList<DCP_DosageQueryRes.level1Elm>());
		DCP_DosageQueryRes.level1Elm pfLev = res.new level1Elm();
		pfLev.setPfNo(pfNo);
		res.getDatas().add(pfLev);
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_DosageQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_DosageQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_DosageQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_DosageQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_DosageQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_DosageQueryReq> (){};
	}

	@Override
	protected DCP_DosageQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_DosageQueryRes();
	}


	protected String getQuerySql2(DCP_DosageQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String eId = req.geteId();
		String shopId = req.getShopId();
		String langType = req.getLangType();
		
		String beginDate = "";
		String endDate = "";
		
		String dateReferType = req.getRequest().getDateReferType();
		if(dateReferType.equals("avg")){
			beginDate = req.getRequest().getDatas().get(0).getBeginDate();
			endDate = req.getRequest().getDatas().get(0).getEndDate();
		}
		
		String bDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
		
		Calendar calendar = Calendar.getInstance();  
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");  
		Date day = format.parse(bDate);
		calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) );  
	    day = calendar.getTime();  
	    String sysDate = format.format(day);  
	    
	    calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - 1);  
	    day = calendar.getTime();  
	    String preDate = format.format(day);  // 昨天（取前日库存）
		
		StringBuffer sqlbuf = new StringBuffer();
		String sql = "";
		sqlbuf.append( "SELECT a.pluNo , a.pluname , featureNo , g1.price1 as price  ,  a.unit , a.amt ,  a.qty , a.saleTime  as lastSaleTime, a.beginDate ,a.endDate ,   "
				+ " to_date(a.endDate ,'yyyymmdd')-to_date(a.beginDate ,'yyyymmdd')  AS  dateDiff , "
			+ " nvl(sum(f.sale_qty), 0) AS preSaleQty , nvl( SUM(f.sale_amt) , 0 ) AS preSaleAmt ,  a.wunit AS wunit "
//			+ " , h.state AS isClear "
			+ " , nvl(u.udlength ,2 ) AS punitudlength "
//			+ " , nvl(ph.out_stock , 'N' )AS isClear  "
			+ " " );
		
		sqlbuf.append( "   FROM ( "
            + " SELECT EID , SHOPID  , pluNo , featureNo , unit , baseunit , pluName ,   SUM(amt ) AS amt  , SUM(qty)  AS qty , "
            + " max(a.bDate) AS endDate ,   MIN(a.bDate) AS beginDate , MAX(a.sdate || a.sTime) AS saleTime  "
            + " FROM "
            + " (  SELECT a.EID , a.SHOPID , a.type  , b.pluno , b.featureNo,  b.unit ,b.baseunit ,  e.pluName AS pluName , "
            + " CASE WHEN a.type = '0' THEN b.amt ELSE -b.amt END  AS amt , CASE WHEN a.type = '0' THEN b.qty ELSE -b.qty END AS qty   , "
            + " a.bDate ,  a.sDate , a.stime "
            + " FROM DCP_SALE a "
            + " LEFT JOIN DCP_SALE_detail b ON a.EID = b.EID AND a.saleno = b.saleNo AND a.SHOPID = b.SHOPID  "
            // 下架菜品不预估 
            // 商品上下架，取DCP_GOODS_shop 是否可售， 不再取DCP_PRICE_shop 里的生失效日期
            + " INNER JOIN ("
            + "  SELECT a.EID , a.pluNo , b.plu_name AS pluName "
                  + "  FROM DCP_GOODS a "
                  + " INNER JOIN DCP_GOODS_lang b  ON a.EID = b.EID AND a.pluNo = b.pluNo"
                  + " Inner Join DCP_GOODS_shop c on a.EID = c.EID and a.pluNo = c.pluNo "
                  + " WHERE a.EID = '"+eId+"'  AND a.status='100'  AND b.status='100'  and c.status='100'  "
                  + " AND b.lang_type = '"+langType+"' and c.organizationNO = '"+shopId+"' "
                  + " AND c.fsal = 'Y'  "
                  + " ) e  ON b.EID = e.EID AND b.pluNo = e.pluNo   "
            + " WHERE a.EID = '"+eId+"' AND a.SHOPID = '"+shopId+"'  AND a.sDate BETWEEN '"+beginDate+"'  AND '"+endDate+"'   "
            + " AND B.DISHESSTATUS != '3' AND B.DISHESSTATUS !='4'    "
            + " GROUP BY   a.EID , a.SHOPID , b.pluno ,  b.unit , b.wunit , a.type  , b.amt , b.qty  ,  "
            + " a.bDate ,  a.sDate , a.stime , e.pluName  "
            + " ORDER BY pluNo  "
            + " )  a " );
            
		sqlbuf.append( " GROUP BY EID , SHOPID ,  pluNo , featureNo  ,pluName , unit , wunit  "
            + " ORDER BY pluNo "
            + " )  a  " );
                
 			
        sqlbuf.append(" LEFT JOIN "
        		+ " ("
        		+ " select a.EID, a.SHOPID,a.bdate ,b.PLUNO,c.PluName, b.unit , "
        		+ " sum(case when a.type='1' or a.type='2' or a.type='4' then -b.Qty else b.Qty end ) as sale_qty ,"
        		+ " sum(case when a.type='1' or a.type='2' or a.type='4' then -b.AMT else b.Amt end ) as sale_Amt  "
        		+ " from  DCP_SALE a "
        		+ " inner join DCP_SALE_detail b on a.EID=b.EID and a.SHOPID=b.SHOPID and a.saleno=b.saleno"
        		+ " inner join DCP_GOODS c on b.PLUNO=c.PLUNO and  b.EID=c.EID"
        		+ " where a.EID='"+eId+"'"
        		+ " and a.bdate = '"+preDate+"'  "
        		+ " and a.type!=16 and (b.packagemaster!='Y' or b.packagemaster is null ) "
        		+ " AND B.DISHESSTATUS != '3' AND B.DISHESSTATUS !='4'    "
        		+ " group by a.EID, a.SHOPID,   a.bdate ,b.PLUNO,c.PluName , b.unit "
        		+ " order by a.SHOPID   "
        		+ ") f ON a.EID = f.EID and a.SHOPID = f.SHOPID    and a.pluno = f.pluno "
        + " LEFT JOIN  (select * "
                   + " from (  select EID, pluno, unit, price1, idx, row_number() over(partition by EID, pluno, unit order by idx) rn "
                            + " from (select EID, pluno, unit, price1, 1 as idx "
                                     + " from DCP_PRICE_shop " 
                                     + " where EID = '"+eId+"' "
                                     + " and organizationno = '"+shopId+"' "
                                     + " and effdate <= '"+sysDate+"' "
                                     + " and (ledate >= '"+sysDate+"' or "
                                     + " ledate is null) "
                                     + " and status='100' "
                                     + " union "
                                     + " select EID, pluno, unit, price1, 2 as idx "
                                     + " from DCP_PRICE "
                                     + " where EID = '"+eId+"' "
                                     + " and effdate <= '"+sysDate+"' "
                                     + " and (ledate >= '"+sysDate+"' or "
                                     + " ledate is null) "
                                     + " and status='100'  )  ) "
                  + " where rn = 1) g1  "
                  + " ON a.EID = g1.EID AND a.pluNo = g1.pluNo AND g1.unit = a.unit  "
                  // 2019-11-21 增加查单位保留位数
                  + "   LEFT JOIN DCP_UNIT u ON a.EID = u.EID AND a.unit = u.unit "
        		  + " ) ph  "
        		  + " ON a.EID = ph.EID AND a.pluNo = ph.pluNo "
                  + " GROUP BY a.pluNo , a.pluname  ,  g1.price1, a.unit , a.amt , a.qty , a.saleTime , a.beginDate , "
                  + " a.endDate , a.wunit , u.udlength "
//                  + ",ph.out_stock   " 
                  );
            	
        sqlbuf.append(" ORDER BY a.pluNO  " );
		sql = sqlbuf.toString();
		return sql;
	}
	

	/**
	 * 生成单号
	 * @param req
	 * @return
	 * @throws Exception 
	 */
	private String getPFNO(DCP_DosageQueryReq req) throws Exception{
		
		String PFNO = "";
		String sql = "";
		String eId = req.geteId();
		String shopId = req.getShopId();
		String bDate = req.getRequest().getbDate(); //单据日期（营业日期），不必再查询， 前端能获取到
		
		bDate = bDate.replace("-", "");
		sql = "select MAX(PFNO) AS PFNO from DCP_porder_forecast where EID = '"+eId+"' and SHOPID = '"+shopId+"' and PFNO like '%%"+bDate+"%%'";
		List<Map<String, Object>> getDatas = this.doQueryData(sql, null);
		if(getDatas.size() > 0 ){
			PFNO = (String) getDatas.get(0).get("PFNO");

			if (PFNO != null && PFNO.length() > 0) {
				long i;
				PFNO = PFNO.substring(4, PFNO.length());
				i = Long.parseLong(PFNO) + 1;
				PFNO = i + "";
				PFNO = "YGYH" + PFNO;
				
			} else {
				PFNO = "YGYH" + bDate + "00001";
			}
		
		}
		
		return PFNO;
		
	}
	
	
	/**
	 * 获取原料数据
	 * @param req
	 * @param pluNoStr
	 * @return
	 */
	protected String getBomDatas(DCP_DosageQueryReq req ,String pluNoStr){
		String eId = req.geteId();
		String shopId = req.getShopId();
		String langType = req.getLangType();
		
		String sql;
		try {
			Calendar cal = Calendar.getInstance();//获得当前时间
			SimpleDateFormat df=new SimpleDateFormat("yyyyMMdd");
			String sysDate = df.format(cal.getTime());
			
			Calendar calendar = Calendar.getInstance();//获得当前时间
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");  
			calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - 1);  
			String preDate = format.format(calendar.getTime());  // 昨天 (取昨天日结后该商品的库存)
			
			StringBuffer sqlbuf = new StringBuffer();
			sql = "";
			sqlbuf.append( "select * "
  + " from (select distinct a.pluno , gn.pluName, a.baseqty, a.material_pluno, a.material_punit, a.material_baseqty   ,   "
  + " c.pluname as material_pluname, d.unit_name as material_punitname, a.bom_type, c.wunit as material_wunit, c.punit ,"
  + " e.unit_name as material_wunitname, "
  + " c.isbatch as material_isbatch, nvl(b1.price1, 0 ) as material_price  , nvl(SUM(f.qty) , '0')AS sQty "
  + " , nvl(gs.min_qty, 0 ) AS minQty, nvl(gs.max_qty , 99999) AS maxQty ,NVL( gs.mul_qty , 0 ) AS mulQty "
  + " , nvl(u.udlength,'2') pUnitUdLength  "
  + " from (select a.EID, a.pluno, a.unit, a.material_pluno, a.material_qty as material_baseqty, "
  + " a.material_unit as material_punit, a.qty as baseqty, a.bom_type "
  + " from DCP_BOM a "
  + " left join DCP_BOM_shop b"
  + " on a.EID = b.EID"
  + " and a.pluno = b.pluno " 
  + " and b.EID = '"+eId+"' "
  + " and b.material_bdate <= '"+sysDate+"' "
  + " and (b.material_edate >= '"+sysDate+"' or b.material_edate is null) "
  + " and b.organizationno = '"+shopId+"' and b.status='100' "
  + " and a.bom_type = b.bom_type "
  + " where a.EID = '"+eId+"' "
  + " and a.effdate <= '"+sysDate+"'" 
  + " and a.material_bdate <= '"+sysDate+"'"
  + " and (a.material_edate >= '"+sysDate+"' or a.material_edate is null)"
  + " and a.status='100' "
  + " and a.bom_type = '0' "
  + " and b.pluno is null "
  + " and a.pluno in ("+pluNoStr+") "
  + " union all "
  + " select EID, pluno, unit, material_pluno, material_qty as material_baseqty, material_unit as material_punit, "
  + " qty as baseqty, bom_type "
  + " from DCP_BOM_shop"
  + " where EID = '"+eId+"' "
  + " and material_bdate <= '"+sysDate+"' "
  + " and (material_edate >= '"+sysDate+"' or material_edate is null) "
  + " and organizationno = '"+shopId+"'"
  + " and status='100' "
  + " and bom_type = '0' "
  + " and pluno in ("+pluNoStr+")) a "
  + " inner join DCP_GOODS c "
  + " on a.EID = c.EID "
  + " and a.material_pluno = c.pluno "
  + " and c.status='100' "
  + " LEFT JOIN DCP_GOODS gn ON a.EID = gn.EID AND a.pluNO = gn.pluNo "
  
  // 2019-12-19 增加查询商品适用门店， 查商品最大、最小、要货倍量
  + " LEFT JOIN DCP_GOODS_shop gs ON a.EID = gs.EID AND  a.material_pluNo = gs.pluNo  "
  + " AND nvl(gs.fpod, 'Y' ) = 'Y' AND gs.status='100' AND gs.organizationNO = '"+req.getShopId()+"'  "
  + " LEFT JOIN DCP_UNIT u ON c.punit = u.unit AND c.EID = u.EID AND u.status='100' "
  
  + " left join (select * "
  + " 		from (select EID, pluno, unit, price1, idx, row_number() over(partition by EID, pluno, unit order by idx) rn "
  + " 			from (select EID, pluno, unit, price1, 1 as idx "
  + " 						from DCP_PRICE_shop "
			                    + " where EID = '"+eId+"' "
			                    + " and organizationno = '"+shopId+"' "
			                    + " and effdate <= '"+sysDate+"'" 
			                    + " and (ledate >= '"+sysDate+"' or ledate is null) "
			                    + " and status='100' "
			                    + " union"
			                    + " select EID, pluno, unit, price1, 2 as idx "
			                    + " from DCP_PRICE "
			                    + " where EID = '"+eId+"'"
			                    + " and effdate <= '"+sysDate+"'"
			                    + " and (ledate >= '"+sysDate+"' or  ledate is null) "
			                    + " and status='100' )) "
			              + " where rn = 1) b1 "
			     + " on a.EID = b1.EID "
			     + " and a.material_pluno = b1.pluno "
			     + " and (a.material_punit = b1.unit) " 
			     + " left join DCP_UNIT_lang d "
			     + " on d.EID = a.EID " 
			     + " and a.material_punit = d.unit " 
			     + " and d.lang_type = '"+langType+"' "
			     + " and d.status='100' "
			     + " left join DCP_UNIT_lang e "
			     + " on e.EID = a.EID "
			     + " and c.wunit = e.unit "
			     + " and e.lang_type = '"+langType+"' "
			     + " and e.status='100'  "
			     + " LEFT JOIN DCP_stock_day_static f ON a.EID = f.EID AND a.material_pluno = f.pluNo "

			     + " AND f.eDate = '"+preDate+"' AND f.organizationNo = '"+shopId+"' "
			     + " LEFT JOIN DCP_WAREHOUSE g ON f.EID=g.EID and f.warehouse=g.warehouse AND g.WAREHOUSE_TYPE<>'3'    "
			     + " GROUP BY  a.pluno , gn.pluName, a.baseqty, a.material_pluno, a.material_punit, a.material_baseqty   ,   "
			     + " c.pluname , d.unit_name  , a.bom_type, c.wunit , e.unit_name  , "
			     + " c.isbatch , b1.price1 , c.punit  "
			     + " ,  gs.min_qty , gs.max_qty , gs.mul_qty , u.udlength ) dtl  ");
			sql = sqlbuf.toString();
			
			return sql;
			
		} catch (Exception e) {

			return "";
		}
	}
	
	
	
}
