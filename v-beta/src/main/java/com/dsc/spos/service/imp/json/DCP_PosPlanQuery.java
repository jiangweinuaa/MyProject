package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_PosPlanQueryReq;
import com.dsc.spos.json.cust.res.DCP_PosPlanQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

/**
 * POS调用   查询生产计划信息
 * @author yuanyy 2019-10-29
 *
 */
public class DCP_PosPlanQuery extends SPosBasicService<DCP_PosPlanQueryReq, DCP_PosPlanQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_PosPlanQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		//必传值不为空
		String eId = req.getoEId();
		String oShopId = req.getoShopId();
		String bDate = req.getbDate();
		String fType = req.getfType();
		/** 必傳，門店編號，僅允許為單筆 */
		if (Check.Null(bDate)) {
			errMsg.append("营业日期不可为空值, ");
			isFail = true;
		}

		if (Check.Null(eId)) {
			errMsg.append("企业编码不可为空值, ");
			isFail = true;
		}
		
		if (Check.Null(oShopId)) {
			errMsg.append("门店编码不可为空值, ");
			isFail = true;
		}
		if (Check.Null(fType)) {
			errMsg.append("类型不可为空值, ");
			isFail = true;
		}

		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}		

		return isFail;
	}

	@Override
	protected TypeToken<DCP_PosPlanQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PosPlanQueryReq>(){};
	}

	@Override
	protected DCP_PosPlanQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_PosPlanQueryRes();
	}

	@Override
	protected DCP_PosPlanQueryRes processJson(DCP_PosPlanQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_PosPlanQueryRes res = this.getResponse();
		try {
			
			String sql = this.getQuerySql(req);
			List<Map<String, Object>> detailDatas = this.doQueryData(sql, null);
			res.setbDate(req.getbDate());
			res.setDatas(new ArrayList<DCP_PosPlanQueryRes.level1Elm>());
			if(detailDatas.size() > 0){
				//单头主键字段
				Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查询条件
				condition.put("FTYPE", true);
				condition.put("FNO", true);
				//调用过滤函数
				List<Map<String, Object>> getQHeader = MapDistinct.getMap(detailDatas, condition);
				
				for (Map<String, Object> map : getQHeader) {
					DCP_PosPlanQueryRes.level1Elm lv1 = res.new level1Elm();
					String fType = map.getOrDefault("FTYPE", "").toString();
					String fNo = map.getOrDefault("FNO", "").toString();
					
					lv1.setfType(map.getOrDefault("FTYPE", "").toString());
					lv1.setfNo(map.getOrDefault("FNO", "").toString());
					lv1.setfName(map.getOrDefault("FNAME", "").toString());
					lv1.setBeginTime(map.getOrDefault("BEGINTIME", "").toString());
					lv1.setEndTime(map.getOrDefault("ENDTIME", "").toString());
					lv1.setPredictAmt(map.getOrDefault("PREDICTAMT", "0").toString());
					
					lv1.setDatas(new ArrayList<DCP_PosPlanQueryRes.level2Elm>());
					
					for (Map<String, Object> map2 : detailDatas) {
						DCP_PosPlanQueryRes.level2Elm lv2 = res.new level2Elm();
						if(fNo.equals(map2.getOrDefault("FNO", "").toString()) && fType.equals(map2.getOrDefault("FTYPE", "").toString()) ){
							
//							lv2.setItem(map2.getOrDefault("ITEM", "").toString());
							lv2.setPluNo(map2.getOrDefault("PLUNO", "").toString());
							lv2.setPluName(map2.getOrDefault("PLUNAME", "").toString());
							lv2.setQty(map2.getOrDefault("QTY", "0").toString());
							lv2.setPrice(map2.getOrDefault("PRICE", "0").toString());
							lv2.setDistriPrice(map2.getOrDefault("DISTRIPRICE", "0").toString());
							lv2.setAmt(map2.getOrDefault("AMT", "0").toString());
							lv2.setDistriAmt(map2.getOrDefault("DISTRIAMT", "0").toString());
							
							lv1.getDatas().add(lv2);
							lv2=null;
						}
					}
					
					res.getDatas().add(lv1);
					lv1=null;
					
				}
			}
			
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败：" + e.getMessage());
		}
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_PosPlanQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql = "";
		StringBuffer sqlbuf = new StringBuffer();
		String maxOutTime = "";
		String maxTimeSql = "select max(outTime) as outTime from DCP_plan_material where EID = '"+req.getoEId()+"' "
				+ " and SHOPID = '"+req.getoShopId()+"' and bdate = '"+req.getbDate()+"' and isOut = 'Y' "
						+ " and fType = '"+req.getfType()+"' " ;
		
		List<Map<String, Object>> timeDatas = this.doQueryData(maxTimeSql, null);
		if(timeDatas != null && timeDatas.size() > 0){
			maxOutTime = timeDatas.get(0).get("OUTTIME").toString();
		}
		
		try {
			String eId = req.getoEId();
			String oShopId = req.getoShopId();
			String bDate = req.getbDate();
			String fType = req.getfType();
			String fNo = req.getfNo();
			
			sqlbuf.append(" SELECT  a.planNO, a.SHOPID , a.bdate , a.ftype , a.fno ,"
					+ " CASE WHEN a.ftype = '0' THEN c.workname ELSE d.dtname END AS fName , "
					+ " RPAD(CAST(  CASE WHEN a.ftype = '0' THEN c.btime ELSE d.begin_time END AS  nvarchar2(10)) , 6 , '0' )  AS beginTime  , "
					+ " RPAD(CAST(  CASE WHEN a.ftype = '0' THEN c.etime ELSE d.end_time END AS  nvarchar2(10)) , 6 , '0' )  AS endTime  , "
					+ " a.pluNo , b.plu_name as pluName , a.punit , a.avgQty , "
					+ " a.predictQty, a.residueQty, a.outQty , a.changeQty , a.actQty as qty , "
                   	+ " a.price , a.distriPrice , a.totAmt as amt , a.distriAmt ,e.predictAmt "
                   	+ " FROM DCP_plan_detail e "
                    + " LEFT JOIN DCP_plan_material a ON a.EID = e.EID AND a.SHOPID = e.SHOPID AND a.planno = e.Planno AND a.ftype = e.fType AND a.fNo = e.fNo  "
                   	+ " LEFT JOIN DCP_GOODS_lang b ON a.EID = b.EID AND a.pluNo = b.pluNo AND b.lang_type = '"+req.getLangType()+"' "
                   	+ " LEFT JOIN dcp_PLANwork c ON a.EID = c.EID AND a.fno = c.workno "
                   	+ " LEFT JOIN DCP_DINNERTime d ON a.EID = d.EID AND a.SHOPID = d.SHOPID AND a.fno = d.dtno "
					+ " WHERE a.EID = '"+eId+"' AND a.SHOPID = '"+oShopId+"' AND a.bdate = '"+bDate+"' "
							+ " and a.isOut = 'Y' "
							+ "and outTime = '"+maxOutTime+"'" 
							);

			if(!fType.equals("") ){
				sqlbuf.append(" and a.fType = '"+fType+"' ");
			}
			
//			if(!fNo.equals("") ){
//				sqlbuf.append(" and a.fNo = '"+fNo+"' ");
//			}
			
			sqlbuf.append(" order by a.ftype , a.fNO , a.pluNO ");
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		sql = sqlbuf.toString();
		return sql;
	}
		
}
