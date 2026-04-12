package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PlanCreateReq;
import com.dsc.spos.json.cust.res.DCP_PlanCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 生产计划新增
 * @author yuanyy 2019-10-21
 *
 */
public class DCP_PlanCreate extends SPosAdvanceService<DCP_PlanCreateReq, DCP_PlanCreateRes> {
	Logger logger = LogManager.getLogger(DCP_PlanCreate.class.getName());
	String mSql = "";
	String aSql = "";
	
	@Override
	protected void processDUID(DCP_PlanCreateReq req, DCP_PlanCreateRes res) throws Exception {
		try {
			// TODO Auto-generated method stub

			String eId = req.geteId();
			String shopId = req.getShopId();
			String langType = req.getLangType();
			String bDate = req.getbDate();

			String planNo = this.queryPlanNo(req); // 获取新的单据编号

			String wENo = req.geteNo1(); // 天气编码
			String sENo = req.geteNo2(); // 特殊事件编码
			String hENo = req.geteNo3(); // 节假日编码
			
			/**
			 * 有关大额订单备注：
			 * 超过该金额的订单不纳入计算。 默认99999
			 * 大额订单不纳入平均营业额计算， 不统计该订单的商品销售数量。
			 * 有关sql，统计计算 DCP_SALE 中 tot_amt(整单金额) < maxAmt 的单子。 
			 */
			String maxAmt = req.getMaxAmt()== null||req.getMaxAmt().equals("") ?"99999":req.getMaxAmt();
			
			String eRatio1 = "0";
			String eRatio2 = "0";
			String eRatio3 = "0";

			if (req.geteRatio1() != null && !req.geteRatio1().equals("")) {
				eRatio1 = req.geteRatio1();
			}

			if (req.geteRatio2() != null && !req.geteRatio2().equals("")) {
				eRatio2 = req.geteRatio2();
			}

			if (req.geteRatio3() != null && !req.geteRatio3().equals("")) {
				eRatio3 = req.geteRatio3();
			}

			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			Date day = new Date();

			String result = "";
			Date day2 = new Date();
		
			String nextResult = "";

			Calendar cal = Calendar.getInstance();// 获得当前时间
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
			String _SysDATE = df.format(cal.getTime());

			// 查询同期四周的销售额
			String dateStr = "' '";
			String nextDayStr = "'  '";
			List<Map<String, Object>> dayDatas = new ArrayList<>();
			
			for (int i = 0; i < 4; i++) {
				calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - 7);
				day = calendar.getTime();

				calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 1);
				day2 = calendar.getTime();

				nextResult = format.format(day2);
				nextDayStr = nextDayStr + " , " + nextResult;

				result = format.format(day);
				dateStr = dateStr + " , " + result;

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("start", result);
				map.put("end", nextResult);
				dayDatas.add(map);
			}

			double ratio = 0;
			ratio = (Double.parseDouble(eRatio1) + Double.parseDouble(eRatio2) + Double.parseDouble(eRatio3))/100;
			// BigDecimal ratio = new BigDecimal("0");

			// if (reqDatas.size() > 0) {
			// for (level1Elm par : reqDatas) {
			//
			// String eType = par.geteType();
			// String eNo = par.geteNo();
			// String eName = par.geteName();
			// String eRatio = par.geteRatio();
			// if(eRatio == null || eRatio.length() < 1){
			// eRatio = "0";
			// }
			//
			// ratio = ratio + Double.parseDouble(eRatio);
			//// ratio = ratio.add(new BigDecimal(eRatio));
			//
			// if (eType.equals("0")) {
			// wENo = eNo;
			// }
			// if (eType.equals("1")) {
			// sENo = eNo;
			// }
			//
			// }
			// }

			String[] columnsModular = { "EID", "SHOPID", "ORGANIZATIONNO", "PLANNO", "BDATE", "E_NO1", "E_NO2", "E_NO3",
					"MODIFRATIO", "STATUS" };

			DataValue[] insValue1 = null;

			insValue1 = new DataValue[] { new DataValue(eId, Types.VARCHAR), new DataValue(shopId, Types.VARCHAR),
					new DataValue(shopId, Types.VARCHAR), new DataValue(planNo, Types.VARCHAR),
					new DataValue(bDate, Types.VARCHAR), new DataValue(wENo, Types.VARCHAR),
					new DataValue(sENo, Types.VARCHAR), new DataValue(hENo, Types.VARCHAR),
					new DataValue(req.getModifRatio(), Types.VARCHAR), new DataValue("0", Types.VARCHAR) };

			InsBean ib1 = new InsBean("DCP_PLAN", columnsModular);
			ib1.addValues(insValue1);
			this.addProcessData(new DataProcessBean(ib1)); // 新增單頭

			// 查询班次编码 和 批次编码,计算后 插入到DCP_plan_detail 和 DCP_plan_material
			String workSql = " select FNO , FTYPE,FNAME, BTIME , ETIME from (   "
					+ " select '0' as FTYPE, workNo as FNO, workName as FNAME, "
					+ " RPAD(CAST(btime AS nvarchar2(10)) , 6 , '0' ) as bTime, "
					+ " RPAD(CAST(etime AS nvarchar2(10)) , 6 , '0' ) as eTime " + " from dcp_planwork where EID = '"
					+ eId + "' and status = '100' " + " union all "
					+ " SELECT '1' as FTYPE, dtNo as FNO , dtName as FNAME ,  begin_time AS bTime , end_Time AS eTime "
					+ " from DCP_DINNERTIme where EID = '" + req.geteId() + "' and SHOPID = '" + req.getShopId()
					+ "' and status = '100' " + " ) ";

			List<Map<String, Object>> workDatas = this.doQueryData(workSql, null);

			String materialSql = "";
			StringBuffer mSqlBuf = new StringBuffer();
			mSqlBuf.append(
					" SELECT  NUM, ftype, FNO, beginTime, endTime, p.pluno ,"
//					+ " bc.plubarcode,"
					+ " totqty, p.EID , p.SHOPID , c.punit , nvl(u.udlength, 2) as punitUdLength ,  "
							+ " c.wunit AS unit , d.min_qty AS minQty  , d.mul_qty  AS mulQty , e.plu_name  AS pluName , "
							+ " nvl(f.distriPrice, 0) as distriPrice , nvl(g.Price1 , 0 ) AS price  FROM ( ");

			StringBuffer mSqlBuf1 = new StringBuffer();
			String mSql1 = "";

			if (workDatas.size() > 0) {
				StringBuffer sqlbuf = new StringBuffer();
				sqlbuf.append(" SELECT NUM , FTYPE, FNO ,time ,totAmt    FROM (  ");
				String amtSql = "";

				for (int i = 0; i < workDatas.size(); i++) {
					String FNO = workDatas.get(i).getOrDefault("FNO", "").toString();
					String beginTime = workDatas.get(i).getOrDefault("BTIME", "000000").toString();
					String endTime = workDatas.get(i).getOrDefault("ETIME", "000000").toString();
					String fType = workDatas.get(i).getOrDefault("FTYPE", "").toString();
					// 需要判断 时间段是否跨天, 如果跨天,需要分两段来设置

					mSqlBuf1.append(" SELECT  '" + (i + 1) + "' as num,  '" + fType + "' as fType , '" + FNO + "' as FNO, "
							+ " '" + beginTime + "' as beginTime ,  '" + endTime + "' As endTime, "
							+ " pluno, totqty, EID , SHOPID , unit FROM ( "
							+ " SELECT a.EID , a.SHOPID ,b.pluno ,  b.unit ,  NVL(sum(CASE when type = '1' or type = '2' or type = '4' THEN  -qty ELSE qty end), 0) as totQty "
							+ " FROM DCP_SALE a "
							+ " LEFT JOIN DCP_SALE_detail b ON a.EID = b.EID AND a.SHOPID = b.SHOPID AND a.saleNO = b.saleNo "
							+ " WHERE a.EID = '" + req.geteId() + "' AND a.SHOPID = '" + req.getShopId() + "' "
							+ " and a.tot_amt <= "+ maxAmt  
							+ " and a.bDate between '" + req.getBeginDate() + "' and '" + req.getEndDate() + "' and   ");

					sqlbuf.append(" SELECT '" + (i + 1) + "'  AS NUM, '" + fType + "' as FTYPE , '" + FNO + "' as FNO ,'"
							+ beginTime + "'||'" + endTime
							+ "' as time,nvl(sum(case WHEN type='1' or type='2' OR type='4' then -Pay_Amt else Pay_Amt end ) ,0)as totamt "
							+ " FROM DCP_SALE a   " + " WHERE    a.EID = '" + eId + "' AND a.SHOPID  = '" + shopId + "' "
							+ " and a.tot_amt <= "+ maxAmt  
							+ "  AND  ");

					if (Integer.parseInt(beginTime) > Integer.parseInt(endTime)) {

						mSqlBuf1.append(
								" ( a.stime between '" + beginTime + "' and '240000' or a.stime between '000000' and '"
										+ endTime + "' )group by a.EID , a.SHOPID ,b.pluno ,  b.unit ) ");

						sqlbuf.append("  ( (  a.bdate IN  ("+dateStr+" ) AND stime BETWEEN '"+beginTime+"' AND '240000'  ) "
								+" OR "  
								+ " ( a.bdate IN  ("+nextDayStr+") AND stime BETWEEN '000000' AND '"+endTime+"'  ) )  ");
						
//						for (int j = 0; j < dayDatas.size(); j++) {
//							String startDate = dayDatas.get(j).getOrDefault("start", "").toString();
//							String endDate = dayDatas.get(j).getOrDefault("end", "").toString();
//
//							sqlbuf.append(" " + " ( ( bdate = '" + startDate + "' AND stime BETWEEN '" + beginTime
//									+ "' AND '240000' ) " + " OR " + " ( bdate = '" + endDate
//									+ "' AND stime BETWEEN '000000' AND '" + endTime + "' ) ) ");
//
//							if (j < dayDatas.size() - 1) {
//								sqlbuf.append(" OR ");
//							}
//
//						}
					}

					else {
						sqlbuf.append(" bDate IN (" + dateStr + ") " + " AND stime BETWEEN '" + beginTime + "' AND '"
								+ endTime + "' ");

						mSqlBuf1.append(" ( a.stime BETWEEN '" + beginTime + "' AND '" + endTime
								+ "' ) group by a.EID , a.SHOPID ,b.pluno ,  b.unit ) ");

					}

					if (workDatas.size() > 1 && i < workDatas.size() - 1) {
						sqlbuf.append("union");
						mSqlBuf1.append(" union ");
					}

				}

				sqlbuf.append("  ) order by num ");
				amtSql = sqlbuf.toString();

				mSql1 = mSqlBuf1.toString();

				mSqlBuf.append(mSql1);
				mSqlBuf.append(" )  p   ");

				mSqlBuf.append(
						" " + " LEFT JOIN DCP_GOODS c ON p.EID = c.EID AND   p.pluNO = c.pluNo AND p.unit = c.sunit and c.status = '100'  "
								+ " LEFT JOIN DCP_GOODS_shop d ON p.EID = d.EID AND p.pluNo = d.pluNo AND p.SHOPID = d.organizationNO  "
								+ " LEFT JOIN DCP_GOODS_lang e ON p.EID = e.EID AND p.pluNO = e.pluNo and e.status = '100' "
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
						+ " ) g on c.EID=g.EID and c.pluNO = g.pluno   " + " WHERE p.EID = '" + eId
						+ "' AND c.EID = '" + eId + "' AND d.organizationNo  = '" + shopId
						+ "' AND e.lang_type = '" + langType + "' "

						+ " order by  ftype, fno , NUM , PLUNO ");

				materialSql = mSqlBuf.toString();

				System.out.println("商品SQL：" + materialSql);
				System.out.println("金额SQL：" + amtSql);
				
				mSql = materialSql;
				aSql = amtSql;

				// String totAmt = "0";
				List<Map<String, Object>> amtDatas = this.doQueryData(amtSql, null);
				if (amtDatas.size() > 0) {
					for (Map<String, Object> map : amtDatas) {
						
						int insColCt = 0;
						String[] columnsName = { "EID", "ORGANIZATIONNO", "SHOPID", "PLANNO", "BDATE", "FTYPE", "FNO",
								"PREDICTAMT", "AVGAMT" };

						DataValue[] columnsVal = new DataValue[columnsName.length];
						for (int i = 0; i < columnsVal.length; i++) {
							// totAmt 是每个班次或餐段的营业额
							String totAmtStr = map.getOrDefault("TOTAMT", "0").toString();
							BigDecimal totAmt = new BigDecimal(totAmtStr);
							// 除以4是因为计算的是同期四周的总营业额，除以后得到预估今天营业额
							totAmt = totAmt.divide(new BigDecimal("4"), 2, BigDecimal.ROUND_HALF_UP);

							String keyVal = null;
							switch (i) {
							case 0:
								keyVal = eId;
								break;
							case 1:
								keyVal = shopId;
								break;
							case 2:
								keyVal = shopId;
								break;
							case 3:
								keyVal = planNo;
								break;
							case 4:
								keyVal = bDate;
								break;
							case 5:
								keyVal = map.getOrDefault("FTYPE", "0").toString();
								; // fType 0:班次 1:批次
								break;
							case 6:
								keyVal = map.getOrDefault("FNO", "").toString();
								break;
							case 7:
								keyVal = totAmt + "";
								break;
							case 8:
								keyVal = totAmt + "";
								break;
							default:
								break;
							}
							/// 字段数值和字符判断后重新赋值
							if (keyVal != null) {
								insColCt++;
								columnsVal[i] = new DataValue(keyVal, Types.VARCHAR);
							} else {
								columnsVal[i] = null;
							}
						}

						String[] columns2 = new String[insColCt];
						DataValue[] insValue2 = new DataValue[insColCt];

						insColCt = 0;
						for (int i = 0; i < columnsVal.length; i++) {
							if (columnsVal[i] != null) {
								columns2[insColCt] = columnsName[i];
								insValue2[insColCt] = columnsVal[i];
								insColCt++;
								if (insColCt >= insValue2.length)
									break;
							}
						}
						InsBean ib2 = new InsBean("DCP_PLAN_DETAIL", columns2);
						ib2.addValues(insValue2);
						this.addProcessData(new DataProcessBean(ib2));// 增加单身

					}
				}
			}

			// *************** 往 DCP_PLAN_MATERIAL 中插入数据 ****************
			SimpleDateFormat difsimDate = new SimpleDateFormat("yyyyMMdd");
			java.util.Date date1 = difsimDate.parse(req.getBeginDate());
			java.util.Date date2 = difsimDate.parse(req.getEndDate());
			int difdate = PosPub.differentDaysByMillisecond(date1, date2) + 1;

			// 计算期间营业额(如果是更新服务，预估营业额 和 参考营业额 需传参数设置)
			double avgAmt = 1;
			double predictAmt = 1;
			double modifRatio = Double.parseDouble(req.getModifRatio());
			ratio = ratio + modifRatio;
			
			double rrate = 0;
			rrate = predictAmt / avgAmt * ratio;
			double trate = 0;
			trate = predictAmt / 1000;
			
			List<Map<String, Object>> pluDatas = this.doQueryData(materialSql, null);
			if (pluDatas.size() > 0) {
				String[] sModular = { "EID", "ORGANIZATIONNO", "SHOPID", "PLANNO", "BDATE", "FTYPE", "FNO", "PLUNO",
						"PUNIT", "AVGQTY", "PREDICTQTY", "RESIDUEQTY", "OUTQTY", "CHANGEQTY", "ACTQTY", "PRICE",
						"DISTRIPRICE", "ISOUT", "TOTAMT", "DISTRIAMT" };

				for (Map<String, Object> map : pluDatas) {

					String fType = map.get("FTYPE").toString();
					String fNo = map.get("FNO").toString();
					// String beginTime = map.get("BEGINTIME").toString();
					// String endTime = map.get("ENDTIME").toString();
					String pluNo = map.get("PLUNO").toString();
					BigDecimal unitRatio;
					double wunitRatio;
					List<Map<String, Object>> getQData_Ratio = PosPub.getUnit_Ratio_Middle(dao, req.geteId(),
							map.get("PLUNO").toString(), map.get("UNIT").toString());
					
					if (getQData_Ratio == null || getQData_Ratio.isEmpty()) {
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品 " + map.get("PLUNO").toString() + "  单位："
								+ map.get("UNIT").toString() + " 找不到对应的库存单位换算关系");
					}

					unitRatio = (BigDecimal) getQData_Ratio.get(0).get("UNIT_RATIO");
					wunitRatio = unitRatio.doubleValue();
					BigDecimal unitRatio2;
					double punitRatio = 0; // 要货单位

					List<Map<String, Object>> getQData_Ratio2 = PosPub.getUnit_Ratio_Middle(dao, req.geteId(),
							map.getOrDefault("PLUNO", "").toString(), map.getOrDefault("PUNIT", "").toString());

					if (getQData_Ratio2 == null || getQData_Ratio2.isEmpty()) {
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,
								"商品 " + map.getOrDefault("PLUNO", "").toString() + "  单位："
										+ map.getOrDefault("PUNIT", "").toString() + " 找不到对应的换算关系");
					}

					unitRatio2 = (BigDecimal) getQData_Ratio2.get(0).get("UNIT_RATIO");

					punitRatio = unitRatio2.doubleValue(); // 要货单位

					String totQtyStr = map.getOrDefault("TOTQTY", "0").toString();
					BigDecimal totQtyBT = new BigDecimal(totQtyStr);

					// 这里设置数量保留小数位数, 默认为 0 ，即不保留小数，都是整数
					int length = Integer.parseInt(map.getOrDefault("PUNITUDLENGTH", "0").toString());
					
					double sqtyDou = Double.parseDouble(map.getOrDefault("TOTQTY", "0").toString()) * rrate / difdate;

					double sqty = Double.parseDouble(PosPub.GetdoubleScale(sqtyDou, length));

					// String ppropqty = PosPub.GetdoubleScale(sqty / punitRatio,
					// 2); // 预估量用要货单位计算
					// String kQty = PosPub.GetdoubleScale((sqty)/trate, 2); //千元量
					// (库存单位) // 如果用要货单位， sqty/换算率 即可

					String price = PosPub.GetdoubleScale(Double.parseDouble(map.getOrDefault("PRICE", "0").toString()), 2);
					String distriPrice = PosPub
							.GetdoubleScale(Double.parseDouble(map.getOrDefault("DISTRIPRICE", "0").toString()), 2);
					// String totAmt =
					// PosPub.GetdoubleScale(Double.parseDouble(ppropqty) *
					// Double.parseDouble(price) ,2 );
					// String distriAmt =
					// PosPub.GetdoubleScale(Double.parseDouble(ppropqty) *
					// Double.parseDouble(distriPrice) ,2 );

					String totAmt = PosPub.GetdoubleScale(sqty * Double.parseDouble(price), 2);
					String distriAmt = PosPub.GetdoubleScale(sqty * Double.parseDouble(distriPrice), 2);

					DataValue[] sValue1 = null;
					sValue1 = new DataValue[] {
							new DataValue(eId, Types.VARCHAR), new DataValue(shopId, Types.VARCHAR),
							new DataValue(shopId, Types.VARCHAR), new DataValue(planNo, Types.VARCHAR),
							new DataValue(bDate, Types.VARCHAR), new DataValue(fType, Types.VARCHAR),
							new DataValue(fNo, Types.VARCHAR),
							new DataValue(map.getOrDefault("PLUNO", "").toString(), Types.VARCHAR),
							new DataValue(map.getOrDefault("PUNIT", "").toString(), Types.VARCHAR),
							new DataValue(sqty, Types.VARCHAR), new DataValue(sqty, Types.VARCHAR),
							new DataValue("0", Types.VARCHAR), new DataValue("0", Types.VARCHAR),
							new DataValue("0", Types.VARCHAR), new DataValue(sqty, Types.VARCHAR),
							new DataValue(price, Types.VARCHAR), new DataValue(distriPrice, Types.VARCHAR),
							new DataValue("N", Types.VARCHAR), new DataValue(totAmt, Types.VARCHAR),
							new DataValue(distriAmt, Types.VARCHAR) };

					InsBean ib2 = new InsBean("DCP_PLAN_MATERIAL", sModular);
					ib2.addValues(sValue1);
					this.addProcessData(new DataProcessBean(ib2)); // 新增單頭

				}

			}
		
			mSql = "";
			aSql = "";
			
		} catch (Exception e) {

			logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"门店:"+req.getShopId()+"生产计划新增PlanCreateDCP查商品SQL:"+mSql);
			logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"门店:"+req.getShopId()+"生产计划新增PlanCreateDCP查金额SQL:"+aSql);
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败："+e.getMessage());
		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PlanCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PlanCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PlanCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PlanCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;

		if (Check.Null(req.getBeginDate())) {
			isFail = true;
			errCt++;
			errMsg.append("开始日期不可为空值, ");
		}

		if (Check.Null(req.getEndDate())) {
			isFail = true;
			errCt++;
			errMsg.append("结束日期不可为空值, ");
		}

		if (Check.Null(req.getbDate())) {
			isFail = true;
			errCt++;
			errMsg.append("单据日期不可为空值, ");
		}

		if (Check.Null(req.getModifRatio())) {
			isFail = true;
			errCt++;
			errMsg.append("调整系数不可为空值, ");
		}

		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_PlanCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PlanCreateReq>() {
		};
	}

	@Override
	protected DCP_PlanCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_PlanCreateRes();
	}

	/**
	 * 获取单据编号
	 * 
	 * @param req
	 * @return
	 * @throws Exception
	 */
	private String queryPlanNo(DCP_PlanCreateReq req) throws Exception {

		String sql = null;

		String shopId = req.getShopId();
		String eId = req.geteId();

		String bDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
		String planNo = "SCJH" + bDate;
		StringBuffer sqlbuf = new StringBuffer("");

		sqlbuf.append("" + "select planNo  from ( " + "select max(planNo) as  planNo " + "  from DCP_PLAN "
				+ " where OrganizationNO = '" + shopId + "'" + " and EID = '" + eId + "' " + " and SHOPID = '"
				+ shopId + "'" + " and planNo like '%%" + planNo + "%%' "); // 假資料
		sqlbuf.append(" ) TBL ");
		sql = sqlbuf.toString();
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);

		if (getQData != null && getQData.isEmpty() == false) {
			planNo = (String) getQData.get(0).get("PLANNO");
			if (planNo != null && planNo.length() > 0) {
				long i;
				planNo = planNo.substring(4, planNo.length());
				i = Long.parseLong(planNo) + 1;
				planNo = i + "";

				planNo = "SCJH" + planNo;

			} else {
				planNo = "SCJH" + bDate + "00001";
			}
		} else {

			planNo = "SCJH" + bDate + "00001";

		}

		return planNo;
	}

	protected String getQuerySql(DCP_PlanCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		String eId = req.geteId();
		String shopId = req.getShopId();

		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		Date day = new Date();
		
		String maxAmt = req.getMaxAmt()== null||req.getMaxAmt().equals("") ?"99999":req.getMaxAmt();
		
		String result = "";

		// 查询同期四周的销售额
		String dateStr = "' '";

		for (int i = 0; i < 4; i++) {
			calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - 7);
			day = calendar.getTime();
			result = format.format(day);
			dateStr = dateStr + " , " + result;
		}

		String sql = "";
		StringBuffer sqlbuf = new StringBuffer();
		sqlbuf.append(" SELECT NUM , totAmt    FROM (   " + " SELECT   "
				+ " '1'  AS NUM  ,  sum(case WHEN type='1' or type='2' OR type='4' then -Pay_Amt else Pay_Amt end ) as totamt "
				+ " FROM DCP_SALE   " + " WHERE    EID = '" + eId + "' AND SHOPID  = '" + shopId + "'   "
				+ " and tot_amt <= "+ maxAmt  
				+ " AND bDate IN (" + dateStr + ") " + " AND stime BETWEEN '100000' AND '240000' "

				+ " UNION "

				+ " SELECT   "
				+ " '2' AS NUM,  sum(case WHEN type='1' or type='2' OR type='4' then -Pay_Amt else Pay_Amt end ) as totamt "
				+ " FROM DCP_SALE   " + " WHERE    EID = '" + eId + "' AND SHOPID  = '" + shopId + "'  "
				+ " and tot_amt <= "+ maxAmt  
				+ " AND bDate IN (" + dateStr + ") " + " AND stime BETWEEN '000000' AND '100000' " + " ) ");

		sql = sqlbuf.toString();

		return sql;
	}

	/**
	 * 动态往字符串数组中添加元素
	 * 
	 * @param arr
	 * @param str
	 * @return
	 */
	private static String[] insert(String[] arr, String str) {
		int size = arr.length;
		String[] tmp = new String[size + 1];
		System.arraycopy(arr, 0, tmp, 0, size);
		tmp[size] = str;
		return tmp;
	}

}
