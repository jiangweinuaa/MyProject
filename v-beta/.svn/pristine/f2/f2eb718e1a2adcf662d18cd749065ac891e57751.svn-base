package com.dsc.spos.service.imp.json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dsc.spos.json.cust.req.DCP_PluGuQingQueryReq;
import com.dsc.spos.json.cust.req.DCP_PluGuQingQueryReq.level2Elm;
import com.dsc.spos.json.cust.res.DCP_PluGuQingQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_PluGuQingQuery extends SPosBasicService<DCP_PluGuQingQueryReq, DCP_PluGuQingQueryRes> {
	
	Logger logger = LogManager.getLogger(this.getClass().getName());
	
	@Override
	protected boolean isVerifyFail(DCP_PluGuQingQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_PluGuQingQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PluGuQingQueryReq>(){};
	}

	@Override
	protected DCP_PluGuQingQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_PluGuQingQueryRes();
	}

	@Override
	protected DCP_PluGuQingQueryRes processJson(DCP_PluGuQingQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_PluGuQingQueryRes res = null;
		res = this.getResponse();
		try {

			String sql = "";
			sql = this.getQuerySql(req);
			logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"商品沽清查询sql:"+sql);
			List<Map<String, Object>> queryDatas = this.doQueryData(sql, null);
			
			Calendar cal = Calendar.getInstance();// 获得当前时间
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

			df=new SimpleDateFormat("HHmmss");
			String sysTime = df.format(cal.getTime());
			res.setDatas(res.new level1Elm());
			
			int totalRecords = 0; //总笔数
			int totalPages = 0;		
			
			DCP_PluGuQingQueryRes.level1Elm lv1 = res.new level1Elm();
//			lv1.setMealData(new ArrayList<DCP_PluGuQingQueryRes.level2Elm>());
			
			if (queryDatas != null && queryDatas.size() > 0) {
				
				String num = queryDatas.get(0).get("NUM").toString();
				totalRecords=Integer.parseInt(num);
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
				
				// 单头主键字段
				Map<String, Boolean> condition = new HashMap<String, Boolean>(); // 查询条件
				condition.put("GUQINGNO", true);
				condition.put("PLUNO", true);
				// 调用过滤函数
				List<Map<String, Object>> getQHeader = MapDistinct.getMap(queryDatas, condition);
				
				Map<String, Boolean> condition2 = new HashMap<String, Boolean>(); // 查询条件
				condition2.put("GUQINGNO", true);
//				condition.put("PLUNO", true);
				// 调用过滤函数
				List<Map<String, Object>> headDatas = MapDistinct.getMap(queryDatas, condition2);
				
				for (Map<String, Object> map : headDatas) {
					
					String guQingNo = map.get("GUQINGNO").toString();
					if(Check.Null(guQingNo)){
						continue;
					}
					
					lv1.setGuQingNo(guQingNo);
					lv1.setPluList(new ArrayList<DCP_PluGuQingQueryRes.level3Elm >());
					
					for (Map<String, Object> pluMap : getQHeader) {
						
						if(guQingNo.equals(pluMap.get("GUQINGNO").toString())){
							DCP_PluGuQingQueryRes.level3Elm lv3 = res.new level3Elm();
							
							String pluNo = pluMap.get("PLUNO").toString();
							String guQingType = pluMap.get("GUQINGTYPE").toString();
							String pfNo = pluMap.get("PFNO").toString();
							String pfOrderType = pluMap.get("PFORDERTYPE").toString();
							
							if(Check.Null(guQingType)) guQingType = "0";
							
							String udLength = pluMap.getOrDefault("UDLENGTH", "0").toString();
							String pUnitName = pluMap.getOrDefault("PUNITNAME", "").toString();
							String pUnit = pluMap.getOrDefault("PUNIT", "").toString();
							lv3.setpUnitName(pUnitName);
							lv3.setUdLength(udLength);
							lv3.setpUnit(pUnit);
							
							lv3.setGuQingNo(guQingNo);
							lv3.setPluNo(pluNo);
							lv3.setGuQingType(guQingType);
							lv3.setPfNo(pfNo);
							lv3.setPfOrderType(pfOrderType);
							lv3.setMealData(new ArrayList<DCP_PluGuQingQueryRes.level2Elm>());
							
							for (Map<String, Object> map2 : queryDatas) {
								
								if(pluNo.equals(map2.get("PLUNO").toString()) && guQingNo.equals(map2.get("GUQINGNO").toString())){
									// 查的是具体某个商品，所以只会有一个商品的餐段信息存在，不必判断品号相等
									DCP_PluGuQingQueryRes.level2Elm lv2 = res.new level2Elm();
									// 数据库里没有 item 字段，顺序排吧
									
									String dtNo = map2.getOrDefault("DTNO","").toString();
									String dtName = map2.getOrDefault("DTNAME","").toString();
									String beginTime = map2.getOrDefault("BEGINTIME","").toString();
									String endTime = map2.getOrDefault("ENDTIME","").toString();
									String kQty = map2.getOrDefault("KQTY","0").toString();
									String qty = map2.getOrDefault("QTY","0").toString();
									String saleQty = map2.getOrDefault("SALEQTY","0").toString();
									String restQty = map2.getOrDefault("RESTQTY","0").toString();
									String isClear = map2.getOrDefault("ISCLEAR","N").toString();
									
									String selected = "no"; //默认给no 表示当前时间不在该 餐段时间段内
									if(!Check.Null(beginTime) && !Check.Null(endTime)){
										beginTime = beginTime.replace(":", "");
										endTime = endTime.replace(":", "");
										
										int start = Integer.parseInt(beginTime);
										int end = Integer.parseInt(endTime);
										int sysTimeInt = Integer.parseInt(sysTime);
										if(start <= sysTimeInt && sysTimeInt <= end){
											selected = "yes";
										}
										
									}
									
									lv2.setDtNo(dtNo);
									lv2.setDtName(dtName);
									lv2.setBeginTime(beginTime);
									lv2.setEndTime(endTime);
									lv2.setkQty(kQty);
									lv2.setQty(qty);
									lv2.setSaleQty(saleQty);
									lv2.setRestQty(restQty);
									lv2.setIsClear(isClear);
									lv2.setSelected(selected);
									lv3.getMealData().add(lv2);
								}
								
								
							}
							lv1.getPluList().add(lv3);
						}
						
					}
					
					
				}
				
				res.setDatas(lv1);
				
			}

			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);

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
	protected String getQuerySql(DCP_PluGuQingQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		StringBuffer sqlbuf = new StringBuffer();
		String sql = "";
		//分页处理
		String companyNo = req.getRequest().getO_companyNo();
		String shop = req.getRequest().getO_shop();
		
		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();
		int startRow=(pageNumber-1) * pageSize;
		
		String rDate = req.getRequest().getrDate();
		rDate = rDate.replace("-", "");
		
//		String pluNo = req.getRequest().getPluNo();
		List<level2Elm> pluList = req.getRequest().getPluList();
		String pluNo = "";
		if(pluList != null && !pluList.isEmpty()){
			for (int i = 0; i < pluList.size(); i++) {
				pluNo = pluNo + "'"+pluList.get(i).getPluNo()+"'"+",";
			}
			pluNo = pluNo.substring(0,pluNo.length() - 1);
		}
		
		String langType = req.getLangType();
		
		sqlbuf.append(" select * from ( "
				+ " SELECT  count(distinct a.guQingNo ) OVER() AS NUM,  dense_rank() over (order BY  a.guqingNo ) rn,  "
				+ " a.guqingNo , b.pfNO , b.pfOrderType, b.pluNo, e.guQingType , c.dtNO , c.dtName , "
				+ " c.begin_time as beginTime , c.end_time as endTime , c.Kqty , c.qty , c.saleQty , c.restQty , c.isClear,"
				+ " u.udlength , u.uname AS pUnitName , b.pUnit   "
				+ " FROM DCP_GUQINGORDER a "
				+ " LEFT JOIN DCP_GUQINGORDER_detail b ON a.companyNo = b.companyNo AND a.shop = b.shop AND a.guQingNo = b.guQingNo "
				+ " LEFT JOIN DCP_GUQINGORDER_DINNERTIME c ON c.companyNo = b.companyNo AND c.shop = b.shop AND c.guqingNo = b.guqingNO AND c.pluno = b.pluno "
				+ " LEFT JOIN tb_unit u ON u.companyno = b.companyno AND u.unit = b.punit AND u.cnfflg = 'Y' "
				+ " LEFT JOIN tb_goods e ON b.companyNo = e.companyNo AND b.pluno = e.pluNo "
				+ " WHERE a.companyNo = '"+companyNo+"' AND a.shop = '"+shop+"' and a.rDate = '"+rDate+"' AND b.pluNo in( "+pluNo+") "
				+ " order by a.guQingNo ,b.pluNo   "
				+ " ) t WHERE t.rn > " + startRow + " and t.rn<=" + (startRow+pageSize) 
				+ " order by guqingNo , pluNo "
				);

		sql = sqlbuf.toString();
		return sql;
	}

}
