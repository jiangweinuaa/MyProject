package com.dsc.spos.service.imp.json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_PlanQueryReq;
import com.dsc.spos.json.cust.res.DCP_PlanQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

/**
 * 生产计划查询
 * @author yuanyy 
 *
 */
public class DCP_PlanQuery extends SPosBasicService<DCP_PlanQueryReq, DCP_PlanQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_PlanQueryReq req) throws Exception {
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
	    if (isFail){
	    	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}		    
	    return isFail;
	}

	@Override
	protected TypeToken<DCP_PlanQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PlanQueryReq>(){};
	}

	@Override
	protected DCP_PlanQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_PlanQueryRes();
	}

	@Override
	protected DCP_PlanQueryRes processJson(DCP_PlanQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_PlanQueryRes res = this.getResponse();
		String sql = "";
		try {
			sql = this.getQuerySql(req);
			
			List<Map<String, Object>> detailDatas = this.doQueryData(sql, null);
			res.setDatas(new ArrayList<DCP_PlanQueryRes.level1Elm>());
			int totalRecords;		//总笔数				
			int totalPages;   	//总页数
			if (detailDatas != null && detailDatas.isEmpty() == false)
			{ 
				Map<String, Object> oneData_Count = detailDatas.get(0);
				String num = oneData_Count.get("NUM").toString();
				totalRecords=Integer.parseInt(num);
				//算總頁數
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
			}
			else
			{
				totalRecords = 0;
				totalPages = 0;
			}
			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);
			
			Calendar cal = Calendar.getInstance();//获得当前时间
			SimpleDateFormat dfDate = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat dfTime = new SimpleDateFormat("HHmmss");
			String sysDate = dfDate.format(cal.getTime());
			String sysTime = dfTime.format(cal.getTime());
			
			if(detailDatas.size() > 0){
				//单头主键字段
				Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查询条件
				condition.put("PLANNO", true);
				//调用过滤函数
				List<Map<String, Object>> getQHeader=MapDistinct.getMap(detailDatas, condition);
				
				//单头主键字段
				Map<String, Boolean> condition2 = new HashMap<String, Boolean>(); //查询条件
				condition2.put("PLANNO", true);
				condition2.put("FTYPE", true);
				condition2.put("FNO", true);
				condition2.put("PLUNO", true);
				//调用过滤函数
				List<Map<String, Object>> mDatas=MapDistinct.getMap(detailDatas, condition2);
				
				//单头主键字段
				Map<String, Boolean> condition3 = new HashMap<String, Boolean>(); //查询条件
				condition3.put("PLANNO", true);
				condition3.put("FTYPE", true);
				condition3.put("FNO", true);
				//调用过滤函数
				List<Map<String, Object>> fDatas=MapDistinct.getMap(detailDatas, condition3);
				
				for (Map<String, Object> map : getQHeader) {
					
					DCP_PlanQueryRes.level1Elm lv1 = res.new level1Elm();
					
					String planNo = map.getOrDefault("PLANNO", "").toString();
					
					if(planNo.equals("") || planNo == null){
						continue;
					}
				
					String status = map.getOrDefault("STATUS", "0").toString();
					String bDate = map.getOrDefault("BDATE", "").toString();
//					String holiday = map.getOrDefault("HOLIDAY", "").toString();
					String modifRatio = map.getOrDefault("MODIFRATIO", "1").toString();
					
					lv1.setPlanNo(planNo);
					lv1.setStatus(status);
					lv1.setbDate(bDate);
//					lv1.setHoliday(holiday);
					lv1.setModifRatio(modifRatio); 
					lv1.seteNo1(map.getOrDefault("ENO1", "").toString());
					lv1.seteName1(map.getOrDefault("ENAME1", "").toString());
					lv1.seteRatio1(map.getOrDefault("ERATIO1", "0").toString());
					lv1.seteNo2(map.getOrDefault("ENO2", "").toString());
					lv1.seteName2(map.getOrDefault("ENAME2", "").toString());
					lv1.seteRatio2(map.getOrDefault("ERATIO2", "0").toString());
//					lv1.seteDatas(new ArrayList<PlanGetDCPRes.level2Elm>());
					
					lv1.seteNo3(map.getOrDefault("ENO3", "").toString());
					lv1.seteName3(map.getOrDefault("ENAME3", "").toString());
					lv1.seteRatio3(map.getOrDefault("ERATIO3", "0").toString());
					lv1.setMaxAmt(map.getOrDefault("MAXAMT", "99999").toString());
					lv1.setfDatas(new ArrayList<DCP_PlanQueryRes.level3Elm>());
					
//					for (Map<String, Object> eMap : eDatas) {
//						if(planNo.equals(eMap.getOrDefault("PLANNO", "").toString())){
//							
//							PlanGetDCPRes.level2Elm lv2 = new PlanGetDCPRes.level2Elm();
//							
//							String eType = eMap.getOrDefault("ETYPE", "0").toString();
//							String eNo = eMap.getOrDefault("ENO", "").toString();
//							String eName = eMap.getOrDefault("ENAME", "").toString();
//							String eRatio = eMap.getOrDefault("ERATIO", "0").toString();
//							lv2.seteType(eType);
//							lv2.seteNo(eNo);
//							lv2.seteName(eName);
//							lv2.seteRatio(eRatio);
//							
//							lv1.geteDatas().add(lv2);
//						}
//						
//					}
//					
					int item = 0;
					for (Map<String, Object> fMap : fDatas) {
						if(planNo.equals(fMap.getOrDefault("PLANNO", "").toString())){
							
							DCP_PlanQueryRes.level3Elm lv3 = res.new level3Elm();
							
							String fType = fMap.getOrDefault("FTYPE", "").toString();
							String fNo = fMap.getOrDefault("FNO", "").toString();
							
							if(fType.equals("") || fType == null || fNo.equals("") || fNo == null ){
								continue;
							}
							
							String priority = "";
									//fMap.getOrDefault("PRIORITY", "0").toString();
							
							if(fType.equals("0")){
								priority = (item + 1) + "";
								item++;
							}else if (fType.equals("1")){
								priority = fMap.getOrDefault("PRIORITY", "0").toString();
							}
							
							String fName = fMap.getOrDefault("FNAME", "").toString();
							String beginTime = fMap.getOrDefault("BEGINTIME", "").toString();
							String endTime = fMap.getOrDefault("ENDTIME", "").toString();
							String avgAmt = fMap.getOrDefault("AVGAMT", "0").toString();
							String predictAmt = fMap.getOrDefault("PREDICTAMT", "0").toString();
							
							//由前端控制，POS机和服务器所在机器系统时间可能不一样，存在时差
//							String detailStatus = "0";
//							/**
//							 * 判断状态
//							 * 一：单据日期和系统日期不同，则 status = 1 。
//							 * 二：单据日期和系统日期相同，再判断时间差，如果 sysTime - beginTime <= 1,则status = 0 ，允许改。
//							 *                                如果sysTime - beginTime >1, 则 status = 1， 不允许改。
//							 */
//							
//							if(sysDate == bDate && Integer.parseInt(sysTime) - Integer.parseInt(beginTime) <= 10000  ){
//								detailStatus = "0";
//							}
//							else{
//								detailStatus = "1";
//							}
							
							lv3.setfType(fType);
							lv3.setfNo(fNo);
							lv3.setfName(fName);
							lv3.setBeginTime(beginTime);
							lv3.setEndTime(endTime);
							lv3.setAvgAmt(avgAmt);
							lv3.setPredictAmt(predictAmt);
							lv3.setPriority(priority);
							lv1.getfDatas().add(lv3);
							lv3=null;
							
						}
						
					}
					
					res.getDatas().add(lv1);
					lv1=null;
					
				}
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败："+e.getMessage());

		}
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_PlanQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql = "";
		StringBuffer sqlbuf = new StringBuffer();
		
		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();
		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;
		
		String beginDate = req.getBeginDate();
		String endDate = req.getEndDate();
		String keyTxt = req.getKeyTxt();
		
		
		sqlbuf.append(" SELECT * FROM ("
				+ " SELECT count(DISTINCT p.planNO ) over() num, dense_rank() OVER( ORDER BY  p.planno ) rn , p.* "
				+ " FROM ("
				+ " SELECT a.planNo , a.SHOPID , a.bDate , a.modifRatio , "
				+ " a.e_No1 AS eNo1 , d.e_name AS eName1, d.e_ratio AS eRatio1 , "
				+ " a.e_No2 AS eNo2 , e.e_name AS eName2, e.e_ratio AS eRatio2, "
				+ " a.e_No3 AS eNo3 , ho.e_name AS eName3, ho.e_ratio AS eRatio3, "
				+ " a.status, b.ftype, b.fNo , f.workname AS fName  , "
				+ " RPAD(CAST(f.btime AS nvarchar2(10)) , 6 , '0' ) as beginTime, "
				+ " RPAD(CAST(f.etime AS nvarchar2(10)) , 6 , '0' ) as endTime,"
				// 1 as priority 这里不能cast 为nvarchar类型， 字符类型排序是按照字母方式排序， 数字类型才是按照大小排序
				+ " b.predictamt , b.avgAmt  , 1 AS priority , a.maxAmt "  
				+ " FROM DCP_plan a "
				+ " LEFT JOIN DCP_plan_detail b ON a.EID = b.EID AND a.SHOPID = b.SHOPID AND a.planno = b.planNo "
				+ " LEFT JOIN DCP_ELEMENT d ON a.EID = d.EID AND a.e_no1 = d.e_no AND d.e_type = '0' " // 0:天气
				+ " LEFT JOIN DCP_ELEMENT e ON a.EID = e.EID AND  a.e_no2 = e.e_no AND e.e_type = '1'" // 1:特殊事件
				
				+ " LEFT JOIN DCP_ELEMENT ho ON a.EID = ho.EID AND  a.e_no3 = ho.e_no AND ho.e_type = '2'" // 2:节假日
				
				+ " LEFT JOIN dcp_planwork f ON b.EID = f.EID AND b.fNo = f.workno "
				+ " WHERE a.EID = '"+req.geteId()+"' AND a.SHOPID = '"+req.getShopId()+"'  AND b.ftype = '0' " );
		sqlbuf.append(" and a.bDate between '"+beginDate+"' and '"+endDate+"'" );
		if(keyTxt != null && keyTxt.length() > 0)
			sqlbuf.append(" and ( a.planNo like '%%"+keyTxt+"%%' OR a.e_No1 like '%%"+keyTxt+"%%' "
					+ " OR d.e_Name like '%%"+keyTxt+"%%' OR a.e_no2 like '%%"+keyTxt+"%%' "
					+ " OR e.e_Name like '%%"+keyTxt+"%%' )   ");
		
		sqlbuf.append( " UNION "
				
				+ " SELECT a.planNo , a.SHOPID , a.bDate ,a.modifRatio , "
				+ " a.e_No1 AS eNo1 , d.e_name AS eName1, d.e_ratio AS eRatio1 ,  "
				+ " a.e_No2 AS eNo2 , e.e_name AS eName2, e.e_ratio AS eRatio2 , "
				+ " a.e_No3 AS eNo3 , ho.e_name AS eName3, ho.e_ratio AS eRatio3, "
				+ " a.status, b.ftype, b.fNo , f.dtName AS fName  , f.begin_time AS beginTime , f.end_time AS endTime , b.predictamt , b.avgAmt,  f.priority  AS priority, a.maxAmt   "  
				+ " FROM DCP_plan a "
				+ " LEFT JOIN DCP_plan_detail b ON a.EID = b.EID AND a.SHOPID = b.SHOPID AND a.planno = b.planNo "
				+ " LEFT JOIN DCP_ELEMENT d ON a.EID = d.EID AND a.e_no1 = d.e_no AND d.e_type = '0' " // 0:天气
				+ " LEFT JOIN DCP_ELEMENT e ON a.EID = e.EID AND  a.e_no2 = e.e_no AND e.e_type = '1'" // 1:特殊事件
				+ " LEFT JOIN DCP_ELEMENT ho ON a.EID = ho.EID AND  a.e_no3 = ho.e_no AND ho.e_type = '2'" // 2:节假日

				+ " LEFT JOIN DCP_DINNERtime f ON b.EID = f.EID AND b.fno = f.dtno and b.SHOPID = f.SHOPID  "
				
				+ " WHERE a.EID = '"+req.geteId()+"' AND a.SHOPID = '"+req.getShopId()+"' AND b.ftype = '1'" );
		sqlbuf.append(" and a.bDate between '"+beginDate+"' and '"+endDate+"'" );
		if(keyTxt != null && keyTxt.length() > 0)
			sqlbuf.append(" and ( a.planNo like '%%"+keyTxt+"%%' OR a.e_No1 like '%%"+keyTxt+"%%' "
					+ " OR d.e_Name like '%%"+keyTxt+"%%' OR a.e_no2 like '%%"+keyTxt+"%%' "
					+ " OR e.e_Name like '%%"+keyTxt+"%%' ) ");
		sqlbuf.append( " ) p "
				+ " ORDER BY   planNO DESC , bdate DESC  ) "
				+ " WHERE rn > "+startRow +" AND rn  <= "+(startRow + pageSize) 
				+ "  order by   planno desc, bdate desc, ftype , priority ,beginTime   "
				+ "");
		
		sql = sqlbuf.toString();
		return sql;
	}
	
}
