package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.dsc.spos.json.cust.req.DCP_PackChargeQueryReq;
import com.dsc.spos.json.cust.res.DCP_PackChargeQueryRes;
import com.dsc.spos.json.cust.res.DCP_PackChargeQueryRes.Goods;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;

import cn.hutool.core.collection.CollectionUtil;

public class DCP_PackChargeQuery extends SPosBasicService<DCP_PackChargeQueryReq, DCP_PackChargeQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_PackChargeQueryReq req) throws Exception {
		return false;
	}

	@Override
	protected TypeToken<DCP_PackChargeQueryReq> getRequestType() {
		return new TypeToken<DCP_PackChargeQueryReq>(){};
	}

	@Override
	protected DCP_PackChargeQueryRes getResponseType() {
		return new DCP_PackChargeQueryRes();
	}

	@Override
	protected DCP_PackChargeQueryRes processJson(DCP_PackChargeQueryReq req) throws Exception {
		if(req.getPageSize() == 0){
			req.setPageSize(10);
		}
		if(req.getPageNumber() == 0){
			req.setPageNumber(1);
		}
		List<DCP_PackChargeQueryRes.level1Elm> datasList = new ArrayList<DCP_PackChargeQueryRes.level1Elm>();
		String sql=null;
		DCP_PackChargeQueryRes res = this.getResponse();
		//单头总数
		sql = this.getCountSql(req);
		List<Map<String, Object>> getQData_Count = this.doQueryData(sql, new String[]{});
		int totalRecords;								//总笔数
		int totalPages;									//总页数
		if (getQData_Count != null && getQData_Count.isEmpty() == false) 
		{ 			
			Map<String, Object> oneData_Count = getQData_Count.get(0);
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
		//==================================================================
		sql = this.getQuerySql(req);
		List<Map<String, Object>> list = this.doQueryData(sql, new String[]{});
		if(CollectionUtil.isNotEmpty(list))
		{
			for (Map<String, Object> map : list) {
				DCP_PackChargeQueryRes.level1Elm datas = JSONObject.parseObject(JSONObject.toJSONString(map), DCP_PackChargeQueryRes.level1Elm.class);
				String goodsSql = "SELECT a.*, b.PLU_NAME AS pluName, c.UNAME AS unitName "
						+ "FROM DCP_PACKCHARGE_GOODS a "
						+ "LEFT JOIN DCP_GOODS_LANG b ON a.EID = b.EID AND a.PLUNO = b.PLUNO "
						+ "LEFT JOIN DCP_UNIT_LANG c ON a.EID = c.EID AND a.UNITID = c.UNIT "
						+ "WHERE c.LANG_TYPE = ? AND b.LANG_TYPE = ? AND a.EID = ? AND a.PACKPLUNO = ?";
				String[] conditionValues = {req.getLangType(), req.getLangType(), req.geteId(), map.get("PACKPLUNO").toString()};
				List<Map<String, Object>> goodsMapList = this.doQueryData(goodsSql, conditionValues);
				if(CollectionUtil.isNotEmpty(goodsMapList)) {
					List<Goods> goodsList = JSONObject.parseObject(JSONObject.toJSONString(goodsMapList), new TypeReference<List<Goods>>() {});
					datas.setGoodsList(goodsList);
				}
				datasList.add(datas);
			}
		}
		res.setDatas(datasList);
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		
	}

	@Override
	protected String getQuerySql(DCP_PackChargeQueryReq req) throws Exception {
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer();
		
		//計算起啟位置
		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();
		int startRow = (pageNumber-1) * pageSize;

		String eId = req.geteId();
		String keyTxt = null;
		String status = null;
		if (req.getRequest() != null) {
			keyTxt = req.getRequest().getKeyTxt();
			status = req.getRequest().getStatus();

		}
		String langType = req.getLangType();
		if (langType == null || langType.isEmpty()) {
			langType = "zh_CN";
		}
		sqlbuf.append("select rn, packPluNo, memo, status, redisUpdateSuccess, packPluName, packUnitId, packUnitName, packPrice,packPluType,packBagNum from  "
				+ "( "
				+ " SELECT a.PACKPLUNO AS packPluNo, a.MEMO AS memo, a.STATUS AS status, a.REDISUPDATESUCCESS AS redisUpdateSuccess, b.PLU_NAME AS packPluName, c.SUNIT AS packUnitId, d.UNAME AS packUnitName, c.PRICE AS packPrice, rownum rn"
				+ " ,a.PACKPLUTYPE as packPluType,nvl(a.PACKBAGNUM,1) as packBagNum " 
				+ " FROM DCP_PACKCHARGE a"
				+ " LEFT JOIN DCP_GOODS_LANG b ON a.EID = b.EID AND a.PACKPLUNO = b.PLUNO" 
				+ " LEFT JOIN DCP_GOODS c ON b.EID = c.EID AND b.PLUNO = c.PLUNO"
				+ " LEFT JOIN DCP_UNIT_LANG d ON c.EID = d.EID AND c.SUNIT = d.UNIT"
				+ " WHERE d.LANG_TYPE = '"+langType+"' AND b.LANG_TYPE ='"+langType+"' AND a.EID='" + eId + "'  ");
		if (keyTxt != null && keyTxt.length() > 0) {
			sqlbuf.append(" AND (a.PACKPLUNO like '%%" + keyTxt + "%%' or b.PLU_NAME LIKE '%%" + keyTxt + "%%' )  ");
		}
		if (status != null && status.length() > 0) {
			sqlbuf.append(" and a.status='" + status + "' ");
		}
		sqlbuf.append( ") where rn>"+startRow+" and rn<="+(startRow+pageSize)+"");
		sql = sqlbuf.toString();
		return sql;
	}
	
	protected String getCountSql(DCP_PackChargeQueryReq req) throws Exception{
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer();
		
		String eId = req.geteId();
		String keyTxt = null;
		String status = null;
		if (req.getRequest() != null) {
			keyTxt = req.getRequest().getKeyTxt();
			status = req.getRequest().getStatus();
		}
		String langType = req.getLangType();
		if (langType == null || langType.isEmpty()) {
			langType = "zh_CN";
		}
		sqlbuf.append("select num from "
				+ " ( "
				+ " SELECT count(*) as num"
				+ " FROM DCP_PACKCHARGE a"
				+ " LEFT JOIN DCP_GOODS_LANG b ON a.EID = b.EID AND a.PACKPLUNO = b.PLUNO" 
				+ " LEFT JOIN DCP_GOODS c ON b.EID = c.EID AND b.PLUNO = c.PLUNO"
				+ " LEFT JOIN DCP_UNIT_LANG d ON c.EID = d.EID AND c.SUNIT = d.UNIT"
				+ " WHERE d.LANG_TYPE = '"+langType+"' AND b.LANG_TYPE ='"+langType+"' AND a.EID='" + eId + "'  ");
		if (keyTxt != null && keyTxt.length() > 0) {
			sqlbuf.append(" AND (a.PACKPLUNO like '%%" + keyTxt + "%%' or b.PLU_NAME LIKE '%%" + keyTxt + "%%' )  ");
		}
		if (status != null && status.length() > 0) {
			sqlbuf.append(" and a.status='" + status + "' ");
		}
		sqlbuf.append( ")");
		sql = sqlbuf.toString();
		return sql;
	}
    
}
