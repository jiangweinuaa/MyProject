package com.dsc.spos.scheduler.job;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.utils.Check;

/**
 * 计划报单沽清： 此Job主要作用是不定时更新沽清表的数量，
 * 如果餐段结束时间到了当前系统时间，要把该餐段的数量加到下一餐段的数量上，同时更新该餐段沽清状态，并写沽清历史记录表
 *
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class SellClearProcess extends InitJob
{

	//******兼容即时服务的,只查询指定的那张单据******
	String pEId="";
	String pShop="";	
	String pOrganizationNO="";
	String pBillNo="";

	public SellClearProcess()
	{

	}

	public SellClearProcess(String eId,String shopId,String organizationNO, String billNo)
	{				
		pEId=eId;
		pShop=shopId;
		pOrganizationNO=organizationNO;
		pBillNo=billNo;
	}

	Logger logger = LogManager.getLogger(SellClearProcess.class.getName());

	static boolean bRun=false;//标记此服务是否正在执行中

	//0-代表成功  其他返回失败信息
	public String doExe() 
	{

		//返回信息
		String sReturnInfo="";

		//此服务是否正在执行中
		if (bRun && pEId.equals(""))
		{		
			logger.info("\r\n*********计划报单沽清SellClearProcess正在执行中,本次调用取消:************\r\n");

			sReturnInfo="定时传输任务-计划报单沽清SellClearProcess正在执行中！";
			return sReturnInfo;
		}

		bRun=true;//			

		logger.info("\r\n*********计划报单沽清SellClearProcess定时调用Start:************\r\n");

		try 
		{
			Calendar cal = Calendar.getInstance();//获得当前时间		
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
			String sysDate = df.format(cal.getTime());
			df = new SimpleDateFormat("HHmmss");
			String sysTime = df.format(cal.getTime());

			Calendar beforeTime = Calendar.getInstance();
			beforeTime.add(Calendar.MINUTE, -1);// 一分钟之前的时间
			Date beforeD = beforeTime.getTime();
			String before5 = new SimpleDateFormat("HHmmss").format(beforeD);  // 前一分钟时间

			Calendar afterTime = Calendar.getInstance();
			afterTime.add(Calendar.MINUTE, +1);// 一分钟之后的时间
			Date afterD = afterTime.getTime();
			String after5 = new SimpleDateFormat("HHmmss").format(afterD);  // 后一分钟时间

			// 设置沽清时间节点的前后一分钟时间， 防止出现 因数据库查询慢而导致无法沽清的情况
			String timeSql = " SELECT  eId , SHOPID , dtNO , dtName , begin_Time  AS beginTime , end_time AS endTime ,workNO , priority  "
					+ " FROM DCP_DINNERTime "
					+ " WHERE STATUS = '100' "
					+ " ORDER BY eId , SHOPID ,priority, dtNO ";

			List<Map<String, Object>> timeDatas = this.doQueryData(timeSql, null);

			if(timeDatas != null && timeDatas.isEmpty() == false){
				for (Map<String, Object> timeMap : timeDatas) {

					String dinnerEndTime = timeMap.getOrDefault("ENDTIME", "000000").toString();
					String dinnerCompanyNo = timeMap.getOrDefault("EID", "").toString();
					String dinnerShop = timeMap.getOrDefault("SHOPID", "").toString();
					String timeDtNo = timeMap.getOrDefault("DTNO", "000000").toString();
					String priorityStr = timeMap.getOrDefault("PRIORITY", "0").toString();

					int nextPriority = Integer.parseInt(priorityStr) + 1;
					SimpleDateFormat sd = new SimpleDateFormat("HHmmss");    
					long difTime =  Math.abs((sd.parse(dinnerEndTime).getTime() - sd.parse(sysTime).getTime())) /1000;  

					// 时差在一分钟之内才可以执行 该任务
					if(difTime <= 60){

						// 获取下一餐段的编码等主键信息。（不想根据优先级，数据库可能会有人改优先级或者优先级初始化不对都会造成沽清不正确 ， 还是根据时间来判断比较好）
						String nextDtNo = "";
						String nextDtName = "";
						for (Map<String, Object> map : timeDatas) { 

							if(map.get("PRIORITY").toString().equals(nextPriority+"") 
									&& dinnerCompanyNo.equals(map.getOrDefault("EID", "").toString()) 
									&& dinnerShop.equals(map.getOrDefault("SHOPID", "").toString() )){

								nextDtNo = map.getOrDefault("DTNO", "").toString();
								nextDtName = map.getOrDefault("DTNAME", "").toString();
							}

						}

						if(Check.Null(nextDtNo)){
							continue;
						}

						//查询沽清时段数据 DCP_porder_forecast_dinnertime 
						String sql = this.getGuQingSql();

						logger.info("\r\n******计划报单沽清SellClearProcess 执行SQL语句："+sql+"******\r\n");

						List<Map<String, Object>> guQingDatas = this.doQueryData(sql, null);

						if (guQingDatas != null && guQingDatas.isEmpty() == false) 
						{
							List<DataProcessBean> data = new ArrayList<DataProcessBean>();
							//单头主键字段							
							String guQingSql = " select nvl(max(item),'0') + 1 as item , EID , SHOPID , guqingNo  "
									+ " from DCP_GUQINGORDER_PLQTYUPRECORD "
									+ " GROUP BY EID , SHOPID , guqingNo ";
							List<Map<String , Object>> allDatas = this.doQueryData(guQingSql, null);
							//CopyOnWriteArrayList

							String maxItemStr = "1";

							for (Map<String, Object> GQMap : guQingDatas) 
							{
								if(!timeDtNo.equals(GQMap.get("DTNO").toString())){
									continue;
								}

								String shopId = GQMap.get("SHOPID") == null ? "" : GQMap.get("SHOPID").toString();
								String eId = GQMap.get("EID") == null ? "" : GQMap.get("EID").toString();
								String dtNo = GQMap.get("DTNO") == null ? "" : GQMap.get("DTNO").toString();
								String dtName = GQMap.get("DTNAME") == null ? "" : GQMap.get("DTNAME").toString();
								String guQingNo = GQMap.get("GUQINGNO") == null ? "" : GQMap.get("GUQINGNO").toString();
								String beginTime = GQMap.get("BEGINTIME") == null ? "" : GQMap.get("BEGINTIME").toString();
								String endTime = GQMap.get("ENDTIME") == null ? "" : GQMap.get("ENDTIME").toString();
								String restQty = GQMap.get("RESTQTY") == null ? "0" : GQMap.get("RESTQTY").toString();
								String qty = GQMap.get("QTY") == null ? "0" : GQMap.get("QTY").toString();
								String saleQty = GQMap.get("SALEQTY") == null ? "0" : GQMap.get("SALEQTY").toString();
								String pluNo = GQMap.get("PLUNO") == null ? "" : GQMap.get("PLUNO").toString();
								String pUnit = GQMap.get("PUNIT") == null ? "" : GQMap.get("PUNIT").toString();
								String guQingType = GQMap.get("GUQINGTYPE") == null ? "" : GQMap.get("GUQINGTYPE").toString(); //枚举: 0 不估清（默认） ,1 当餐 ,2 当天

								String pfNo = GQMap.get("PFNO") == null ? "" : GQMap.get("PFNO").toString();
								String pfOrderType = GQMap.get("PFORDERTYPE") == null ? "" : GQMap.get("PFORDERTYPE").toString();

								double restQtyDou = Double.parseDouble(restQty);

								//************* 调整记录 DCP_GUQINGORDER_PLQTYUPRECORD ***********
								String[] columns_hm ={"EID","SHOPID","GUQINGNO","PLUNO","ITEM","UPDATETYPE","DTNO","DTNAME",
										"BEGIN_TIME","END_TIME","PFNO","PFORDERTYPE","QTY","MODIFYBY","MODIFYBYNAME","MODIFY_DATE","MODIFY_TIME","PUNIT"
								};

								if(guQingType.equals("2")){  //枚举: 0 不估清（默认） ,1 当餐 ,2 当天
									//当天沽清，需要把上一餐段未卖完的数量加到下一餐段
									//当餐沽清，上一餐段未卖完的数量  不能  加到下一餐段
									//调整记录记调整绝对量，不是增量 ： 上一餐段剩余3，下一餐段计划数为 5， 调整量就是 8.
									for (Map<String, Object> NeMap : guQingDatas) {
										if(eId.equals(NeMap.get("EID").toString()) && shopId.equals(NeMap.get("SHOPID").toString())
												&& guQingNo.equals(NeMap.get("GUQINGNO").toString())
												&& pluNo.equals(NeMap.get("PLUNO").toString()) && nextDtNo.equals(NeMap.get("DTNO").toString())){

											String nextQty = NeMap.get("QTY").toString();
											double nextQtyDou = Double.parseDouble(nextQty);
											double nextTrueQty = nextQtyDou + restQtyDou;
											String nextBeginTime = NeMap.get("BEGINTIME").toString();
											String nextEndTime = NeMap.get("ENDTIME").toString();


											//******* 更新下一餐段数量 qty = qty(也就是nextQty) + 上一餐段剩余数量
											UptBean ub1 =new UptBean("DCP_GUQINGORDER_DINNERTIME");
											ub1.addUpdateValue("RESTQTY", new DataValue(nextTrueQty+"", Types.VARCHAR));
											ub1.addUpdateValue("QTY", new DataValue(nextTrueQty+"", Types.VARCHAR));

											ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
											ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
											ub1.addCondition("GUQINGNO", new DataValue(guQingNo, Types.VARCHAR));
											ub1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
											ub1.addCondition("DTNO", new DataValue( nextDtNo , Types.VARCHAR)); //上一餐段结束时间要和下一餐段开始时间一致

											data.add(new DataProcessBean(ub1));  // 新增單頭

											if(allDatas != null && !allDatas.isEmpty()){

												for (int i = 0; i < allDatas.size(); i++) {
													if(guQingNo.equals(allDatas.get(i).get("GUQINGNO").toString()) && eId.equals(allDatas.get(i).get("EID").toString())
															&& shopId.equals(allDatas.get(i).get("SHOPID").toString())){
														maxItemStr = allDatas.get(i).get("ITEM").toString();
														//每次获取当前沽清单号的item 之后都要更新 map 中 最大 item 的值，不用每次都查数据库
														allDatas.remove(allDatas.get(i)); //注意这里调用的是集合的方法

														Map<String,Object> newMap = new HashMap<>();
														newMap.put("EID", eId);
														newMap.put("SHOPID", shopId);
														newMap.put("GUQINGNO", guQingNo);
														newMap.put("ITEM",  Integer.parseInt(maxItemStr) + 1);
														allDatas.add(newMap);
													}
												}

											}

											int maxItem = Integer.parseInt(maxItemStr);

											//************* 调整记录 DCP_GUQINGORDER_PLQTYUPRECORD ***********
											DataValue[] insValue_hm = new DataValue[] 
													{
															new DataValue(eId, Types.VARCHAR),
															new DataValue(shopId, Types.VARCHAR), 
															new DataValue(guQingNo, Types.VARCHAR),
															new DataValue(pluNo, Types.VARCHAR),
															new DataValue(maxItem+"" ,Types.VARCHAR),
															new DataValue("guQing", Types.VARCHAR),//枚举: initial：初始化,guQing：沽清,backEnd：后台,pos：POS端
															new DataValue(nextDtNo, Types.VARCHAR),
															new DataValue(nextDtName , Types.VARCHAR),
															new DataValue(nextBeginTime.replace(":", ""), Types.VARCHAR),
															new DataValue(nextEndTime.replace(":", ""), Types.VARCHAR),
															new DataValue(pfNo, Types.VARCHAR),
															new DataValue(pfOrderType, Types.VARCHAR),
															new DataValue(nextTrueQty, Types.VARCHAR),
															new DataValue("", Types.VARCHAR),
															new DataValue("", Types.VARCHAR),
															new DataValue(sysDate, Types.VARCHAR),
															new DataValue(sysTime, Types.VARCHAR),
															new DataValue(pUnit, Types.VARCHAR)
													};

											InsBean ib_hm = new InsBean("DCP_GUQINGORDER_PLQTYUPRECORD", columns_hm);
											ib_hm.addValues(insValue_hm);
											data.add(new DataProcessBean(ib_hm));

										}

									}

									//**** 更新当前餐段可售数量为0 ， isClear = Y
									UptBean ub1 =new UptBean("DCP_GUQINGORDER_DINNERTIME");
									ub1.addUpdateValue("RESTQTY", new DataValue("0", Types.VARCHAR));
									ub1.addUpdateValue("ISCLEAR", new DataValue("Y", Types.VARCHAR));

									ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
									ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
									ub1.addCondition("GUQINGNO", new DataValue(guQingNo, Types.VARCHAR));
									ub1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
									ub1.addCondition("DTNO", new DataValue(dtNo, Types.VARCHAR)); 

									data.add(new DataProcessBean(ub1));

								}


								if(guQingType.equals("1")){  //枚举: 0 不估清（默认） ,1 当餐 ,2 当天
									//当天沽清，需要把上一餐段未卖完的数量加到下一餐段
									//当餐沽清，上一餐段未卖完的数量  不能  加到下一餐段
									//调整记录记调整绝对量，不是增量 ： 上一餐段剩余3，下一餐段计划数为 5， 调整量就是 8.

									//**** 更新当前餐段可售数量为0 ， isClear = Y
									UptBean ub1 =new UptBean("DCP_GUQINGORDER_DINNERTIME");
									ub1.addUpdateValue("RESTQTY", new DataValue("0", Types.VARCHAR));
									ub1.addUpdateValue("ISCLEAR", new DataValue("Y", Types.VARCHAR));

									ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
									ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
									ub1.addCondition("GUQINGNO", new DataValue(guQingNo, Types.VARCHAR));
									ub1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
									ub1.addCondition("DTNO", new DataValue(dtNo, Types.VARCHAR)); 

									data.add(new DataProcessBean(ub1));
									//************* 调整记录 DCP_GUQINGORDER_PLQTYUPRECORD ***********

									if(allDatas != null && !allDatas.isEmpty()){

										for (int i = 0; i < allDatas.size(); i++) {
											if(guQingNo.equals(allDatas.get(i).get("GUQINGNO").toString()) && eId.equals(allDatas.get(i).get("EID").toString())
													&& shopId.equals(allDatas.get(i).get("SHOPID").toString())){

												maxItemStr = allDatas.get(i).get("ITEM").toString();
												//每次获取当前沽清单号的item 之后都要更新 map 中 最大 item 的值，不用每次都查数据库

												allDatas.remove(allDatas.get(i)); //注意这里调用的是集合的方法

												Map<String,Object> newMap = new HashMap<>();
												newMap.put("EID", eId);
												newMap.put("SHOPID", shopId);
												newMap.put("GUQINGNO", guQingNo);
												newMap.put("ITEM",  Integer.parseInt(maxItemStr) + 1);
												allDatas.add(newMap);
											}
										}


									}

									int maxItem = Integer.parseInt(maxItemStr);
									DataValue[] insValue_hm = new DataValue[] 
											{
													new DataValue(eId, Types.VARCHAR),
													new DataValue(shopId, Types.VARCHAR), 
													new DataValue(guQingNo, Types.VARCHAR),
													new DataValue(pluNo, Types.VARCHAR),
													new DataValue(maxItem+"" ,Types.VARCHAR),
													new DataValue("guQing", Types.VARCHAR),//枚举: initial：初始化,guQing：沽清,backEnd：后台,pos：POS端
													new DataValue(dtNo, Types.VARCHAR),
													new DataValue(dtName , Types.VARCHAR),
													new DataValue(beginTime.replace(":", ""), Types.VARCHAR),
													new DataValue(endTime.replace(":", ""), Types.VARCHAR),
													new DataValue(pfNo, Types.VARCHAR),
													new DataValue(pfOrderType, Types.VARCHAR),
													new DataValue(qty, Types.VARCHAR),
													new DataValue("", Types.VARCHAR),
													new DataValue("", Types.VARCHAR),
													new DataValue(sysDate, Types.VARCHAR),
													new DataValue(sysTime, Types.VARCHAR),
													new DataValue(pUnit, Types.VARCHAR)
											};

									InsBean ib_hm = new InsBean("DCP_GUQINGORDER_PLQTYUPRECORD", columns_hm);
									ib_hm.addValues(insValue_hm);
									data.add(new DataProcessBean(ib_hm));

								}					

							}   

							StaticInfo.dao.useTransactionProcessData(data);
							data.clear();

						}
						else 
						{
							logger.info("\r\n***计划报单沽清SellClearProcess单头没数据****SQL="+sql+"********************************************************\r\n");
						}

					}
				}

			}

		} 
		catch (Exception e) 
		{				
			try 
			{
				StringWriter errors = new StringWriter();
				PrintWriter pw=new PrintWriter(errors);
				e.printStackTrace(pw);		

				pw.flush();
				pw.close();			

				errors.flush();
				errors.close();

				logger.error("\r\n******计划报单沽清SellClearProcess报错信息" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");

				pw=null;
				errors=null;
			} 
			catch (IOException e1) 
			{					
				logger.error("\r\n******计划报单沽清SellClearProcess报错信息" + e.getMessage() + "******\r\n");
			}

			//
			sReturnInfo="错误信息:" + e.getMessage();
		}
		finally 
		{
			bRun=false;//
			logger.info("\r\n*********计划报单沽清SellClearProcess定时调用End:************\r\n");
		}			

		//
		return sReturnInfo;

	}

	protected String getGuQingSql() throws Exception 
	{
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");

		Calendar cal = Calendar.getInstance();// 获得当前时间
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String _SysDATE = df.format(cal.getTime());

		sqlbuf.append(" SELECT a.EID ,  a.SHOPID , a.guqingNo , a.bdate , a.rDate , b.pluNO , b.pUnit  , b.guqingType, b.pfNo, b.pfOrderType , "
				+ " c.dtNo , c.dtName ,c.begin_Time AS beginTime  , c.end_time AS endTime , c.qty , c.saleQty , c.restqty , c.isClear, "
				+ " c.modify_date , c.modify_time  "
				+ " FROM DCP_guqingorder a "
				+ " LEFT JOIN DCP_guqingorder_detail b ON a.EID = b.EID AND a.SHOPID = b.SHOPID AND a.guqingNo = b.guqingNo "
				+ " LEFT JOIN DCP_guqingorder_dinnertime c ON b.EID = c.EID AND b.SHOPID = c.SHOPID AND b.guqingNo = c.guqingNo AND b.pluNo = c.pluNo "
				+ " where a.rDate = '"+_SysDATE+"'  " //查询需求日是当天的沽清数据
				+ " and c.isClear = 'N' "
				+ " ORDER BY  a.EID ,  a.SHOPID , a.guqingNo  , c.modify_date desc , c.modify_time desc "
				+ " " 
				);

		sql = sqlbuf.toString();
		return sql;
	}

}
