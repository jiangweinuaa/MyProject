package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_LangLabelQueryReq;
import com.dsc.spos.json.cust.res.DCP_LangLabelQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;

/**
 * 多语言标签查询服务
 * @author yuanyy 2019-01-17
 *
 */
public class DCP_LangLabelQuery extends SPosBasicService<DCP_LangLabelQueryReq, DCP_LangLabelQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_LangLabelQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_LangLabelQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_LangLabelQueryReq>(){};
	}

	@Override
	protected DCP_LangLabelQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_LangLabelQueryRes();
	}

	@Override
	protected DCP_LangLabelQueryRes processJson(DCP_LangLabelQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_LangLabelQueryRes res = null;
		res = this.getResponse();
		int totalRecords = 0; //总笔数
		int totalPages = 0;	
		
		String sql = null;
		sql = this.getQuerySql(req);
		List<Map<String , Object>> getLangLabelDatas = this.doQueryData(sql, null);
		res.setDatas(new ArrayList<DCP_LangLabelQueryRes.level1Elm>());
		if(getLangLabelDatas.size() > 0){
			String num = getLangLabelDatas.get(0).get("NUM").toString();
			totalRecords=Integer.parseInt(num);

			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;	
			
			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);
			
			for (Map<String, Object> oneData : getLangLabelDatas) 
			{
				DCP_LangLabelQueryRes.level1Elm oneLv1 = res.new level1Elm();
				String labelID = oneData.get("LABELID").toString();
				String label_cn = oneData.get("LABEL_CN").toString();
				String label_tw = oneData.get("LABEL_TW").toString();
				String label_en = oneData.get("LABEL_EN").toString();
				String createBy = oneData.get("CREATEBY").toString();
				String createByName = oneData.get("CREATEBYNAME").toString();
				String createTime = oneData.get("CREATETIME").toString();
				String lastModifyBy = oneData.get("LASTMODIFYBY").toString();
				String lastModifyByName = oneData.get("LASTMODIFYBYNAME").toString();
				String lastModifyTime = oneData.get("LASTMODIFYTIME").toString();
				
				oneLv1.setLabelID(labelID);
				oneLv1.setLabel_cn(label_cn);
				oneLv1.setLabel_tw(label_tw);
				oneLv1.setLabel_en(label_en);
				oneLv1.setCreateBy(createBy);
				oneLv1.setCreateByName(createByName);
				oneLv1.setCreateTime(createTime);
				oneLv1.setLastModifyBy(lastModifyBy);
				oneLv1.setLastModifyByName(lastModifyByName);
				oneLv1.setLastModifyTime(lastModifyTime);
				res.getDatas().add(oneLv1);
			}
		}
		else{
			res.setDatas(new ArrayList<DCP_LangLabelQueryRes.level1Elm>());
		}
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_LangLabelQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer();
		String langType = req.getLangType();
		String eId = req.geteId();
		String keyTxt = req.getRequest().getKeyTxt();
		
		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();
		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;
		
		sqlbuf.append(""
				+ "select * from ("
				+ "SELECT COUNT(*) OVER() NUM , row_number() OVER(ORDER BY LABELID) rn , b.* from ("
				+ " select  LABELID   ,   LABEL_CN    ,   LABEL_TW  , LABEL_EN ,  "
				+ " CREATEOPID  AS createBY   , f1.op_name AS createBYName  ,  CREATETIME   ,   "
				+ " LASTMODIOPID  AS lastModifyBy   ,  f2.op_name AS lastModifyByName  ,    LASTMODITIME  AS lastModifyTime "
				+ " FROM   PLATFORM_LANG_LABEL a "
				+ " LEFT JOIN PLATFORM_STAFFS_LANG f1 ON  A .CREATEOPID = f1.opno "
				+ " AND F1.LANG_TYPE  = '" + langType + "' AND  f1.EID = '"+eId+"'  "
				+ " LEFT JOIN PLATFORM_STAFFS_LANG f2 ON   A .LASTMODIOPID = f2.opno"
				+ " AND F2.LANG_TYPE = '" + langType + "' and f2.EID = '"+eId+"' "
				+ " ) b "
				+ " WHERE 1 = 1 " );
		if(keyTxt != null && keyTxt.length() > 0){
			sqlbuf.append("AND  ( labelID LIKE '%%"+keyTxt+"%%%'  OR label_cn LIKE '%%"+keyTxt+"%%' OR label_tw LIKE '%%"+keyTxt+"%%'  "
					+ " OR label_en LIKE '%%"+keyTxt+"%%' )  ");
		}
			
		sqlbuf.append(" ) TBL where rn > "+startRow+" and rn <= "+(startRow+pageSize)+" order by LABELID   "); 
		
		sql = sqlbuf.toString();
		return sql;
	}
	
}
