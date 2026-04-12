package com.dsc.spos.service.imp.json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_GuQingDetailReq;
import com.dsc.spos.json.cust.res.DCP_GuQingDetailRes;
import com.dsc.spos.json.cust.res.DCP_GuQingQueryRes;
import com.dsc.spos.json.cust.res.DCP_GuQingDetailRes;
import com.dsc.spos.json.cust.res.DCP_GuQingDetailRes.level2Elm;
import com.dsc.spos.json.cust.res.DCP_GuQingDetailRes.level3Elm;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.DateUtils;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_GuQingDetail extends SPosBasicService<DCP_GuQingDetailReq, DCP_GuQingDetailRes> {
	
	@Override
	protected boolean isVerifyFail(DCP_GuQingDetailReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_GuQingDetailReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_GuQingDetailReq>(){};
	}

	@Override
	protected DCP_GuQingDetailRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_GuQingDetailRes();
	}

	@Override
	protected DCP_GuQingDetailRes processJson(DCP_GuQingDetailReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_GuQingDetailRes res = null;
		res = this.getResponse();
		try {

			String sql = "";
			sql = this.getQuerySql(req);
			List<Map<String, Object>> queryDatas = this.doQueryData(sql, null);
			
			SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMdd");  
			DateUtils du = DateUtils.getInstance();
			
			SimpleDateFormat format3 = new SimpleDateFormat("HHmmss");  
			DateUtils du3 = DateUtils.getInstance();
			
			res.setDatas(res.new level1Elm());
			
			DCP_GuQingDetailRes.level1Elm lv1 = res.new level1Elm();
			lv1.setPluList(new ArrayList<DCP_GuQingDetailRes.level2Elm>());
			
			if (queryDatas != null && queryDatas.size() > 0) {
				
				// 单头主键字段
				Map<String, Boolean> condition = new HashMap<String, Boolean>(); // 查询条件
				condition.put("GUQINGNO", true);
				// 调用过滤函数
				List<Map<String, Object>> getQHeader = MapDistinct.getMap(queryDatas, condition);
				
				// 单头主键字段
				Map<String, Boolean> condition2 = new HashMap<String, Boolean>(); // 查询条件
				condition2.put("GUQINGNO", true);
				condition2.put("PLUNO", true);
				// 调用过滤函数
				List<Map<String, Object>> pluDatas = MapDistinct.getMap(queryDatas, condition2);
				
				for (Map<String, Object> map : getQHeader) {
					
					String guQingNo = map.get("GUQINGNO").toString();
					lv1.setGuQingNo(guQingNo);
					String rDate = map.get("RDATE").toString();
					if(!Check.Null(rDate)) rDate = du.format(format2.parse(rDate), "yyyy-MM-dd") ;
					
					lv1.setrDate(rDate);
					
					int item = 1;
					for (Map<String, Object> map2 : pluDatas) {
					
						DCP_GuQingDetailRes.level2Elm lv2 = res.new level2Elm();
						// 数据库里没有 item 字段，顺序排吧
//						String item = map2.getOrDefault("ITEM","").toString();
						String pluNo = map2.get("PLUNO").toString();

						String udLength = map2.getOrDefault("UDLENGTH", "0").toString();
						String pUnitName = map2.getOrDefault("PUNITNAME", "").toString();
						lv2.setpUnitName(pUnitName);
						lv2.setUdLength(udLength);
						
						lv2.setItem(item+"");
						lv2.setPluNo(pluNo);
						lv2.setPluName(map2.get("PLUNAME").toString());
						lv2.setFileName(map2.get("FILENAME").toString());
						lv2.setpUnit(map2.get("PUNIT").toString());
						lv2.setGuQingType(map2.get("GUQINGTYPE").toString());
						lv2.setPrice(map2.get("PRICE").toString());
						lv2.setPfNo(map2.get("PFNO").toString());
						lv2.setPfOrderType(map2.get("PFORDERTYPE").toString());
						lv2.setMealData(new ArrayList<DCP_GuQingDetailRes.level3Elm>());
						
						for (Map<String, Object> map3 : queryDatas) {
							if (guQingNo.equals(map3.get("GUQINGNO").toString()) && pluNo.equals(map3.get("PLUNO").toString())) {
								
								if (Check.Null(map3.getOrDefault("DTNO", "").toString())) {
									continue;
								}
								
								DCP_GuQingDetailRes.level3Elm lv3 = res.new level3Elm();
								lv3.setDtNo(map3.get("DTNO").toString());
								lv3.setDtName(map3.get("DTNAME").toString());
								
								String beginTime = map3.get("BEGIN_TIME").toString();
								String endTime = map3.get("END_TIME").toString();
								
								if(!Check.Null(beginTime)) beginTime = du3.format(format3.parse(beginTime), "HH:mm:ss");
								if(!Check.Null(endTime)) endTime = du3.format(format3.parse(endTime), "HH:mm:ss");
								
								lv3.setBeginTime(beginTime);
								lv3.setEndTime(endTime);
								lv3.setkQty(map3.get("KQTY").toString());
								lv3.setQty(map3.get("QTY").toString());
								lv3.setSaleQty(map3.get("SALEQTY").toString());
								lv3.setRestQty(map3.get("RESTQTY").toString());
								lv3.setIsClear(map3.get("ISCLEAR").toString());
								
								lv2.getMealData().add(lv3);
								
							}
	
						}
						lv1.getPluList().add(lv2);
						item = item + 1 ;
						
					}
					
				}
				
				res.setDatas(lv1);
				
			}

			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
//			res.setTotalRecords(totalRecords);
//			res.setTotalPages(totalPages);

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
	protected String getQuerySql(DCP_GuQingDetailReq req) throws Exception {
		// TODO Auto-generated method stub
		StringBuffer sqlbuf = new StringBuffer();
		String sql = "";
		//分页处理
		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();
		int startRow=(pageNumber-1) * pageSize;
		
		String langType = req.getLangType();
		
		sqlbuf.append(" select * from (  "
				+ " SELECT a.guqingNo , a.rDate, b.pluNo , d.plu_name AS pluName , e.filename , b.punit , nvl(b.guqingtype,0) guQingType , b.price , "
				+ " tgd.dtno , tgd.dtname , tgd.begin_time , tgd.end_time , c.kqty , c.qty  , c.saleQty , c.restQty , c.isClear , b.pfNo, b.pfOrderType  "
				+ " ,u.udlength , u.uname AS pUnitName "
				+ " FROM DCP_GUQINGORDER a "
				+ " LEFT JOIN DCP_GUQINGORDER_detail b ON a.EID = b.EID AND a.SHOPID = b.SHOPID AND a.guqingNo = b.guqingNo "
				+ " LEFT JOIN ("
				+ " select distinct EID , SHOPID , guqingNo , dtNo , dtName ,begin_time , end_time   from DCP_GUQINGORDER_DINNERTIME "
				+ " WHERE  EID = '"+req.geteId()+"' and  SHOPID = '"+req.getShopId()+"' AND guQingNo = '"+req.getRequest().getGuQingNo()+"'   "
				+ " ) tgd ON b.EID = tgd.EID  and b.SHOPID = tgd.SHOPID   AND b.guqingno = tgd.guqingno"
				+ " left join DCP_guqingorder_dinnertime c  on c.EID =tgd.EID   and c.SHOPID = tgd.SHOPID  and c.guqingno = tgd.guqingno  and c.pluno = b.pluno  AND c.dtNo = tgd.dtNo  "
				+ " LEFT JOIN DCP_GOODS_lang d ON b.EID = d.EID AND b.pluNo = d.pluNo AND d.lang_type = '"+langType+"'   AND d.status='100' "
				+ " LEFT JOIN DCP_GOODS e ON  b.EID = e.EID AND b.pluNo = e.pluNo  AND e.status='100' "
				+ " LEFT JOIN DCP_UNIT u ON u.EID = b.EID AND u.unit = b.punit AND u.status='100' "
				+ " WHERE a.EID = '"+req.geteId()+"' and a.SHOPID = '"+req.getShopId()+"' AND a.guQingNo = '"+req.getRequest().getGuQingNo()+"' "
				+ " "
				);

		sqlbuf.append("  )  ORDER BY guqingNo DESC , pluNo , dtNo   " );
		sql = sqlbuf.toString();
		return sql;
	}

}
