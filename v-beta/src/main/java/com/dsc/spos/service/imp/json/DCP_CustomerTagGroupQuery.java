package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_CustomerTagGroupQueryReq;
import com.dsc.spos.json.cust.res.DCP_CustomerTagGroupQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

/**
 * 客户标签
 * @author yuanyy
 *
 */
public class DCP_CustomerTagGroupQuery extends SPosBasicService<DCP_CustomerTagGroupQueryReq, DCP_CustomerTagGroupQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_CustomerTagGroupQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_CustomerTagGroupQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_CustomerTagGroupQueryReq>(){};
	}

	@Override
	protected DCP_CustomerTagGroupQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_CustomerTagGroupQueryRes();
	}

	@Override
	protected DCP_CustomerTagGroupQueryRes processJson(DCP_CustomerTagGroupQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_CustomerTagGroupQueryRes res = null;
		res = this.getResponse();
		
		try {
			String sql = "";
			sql = this.getQuerySql(req);
			List<Map<String, Object>> queryDatas = this.doQueryData(sql, null);

			int totalRecords = 0;								//总笔数
			int totalPages = 0;			
			res.setDatas(new ArrayList<DCP_CustomerTagGroupQueryRes.level1Elm>());
			
			if(queryDatas != null && queryDatas.size() > 0){
				String num = queryDatas.get(0).get("NUM").toString();
				totalRecords=Integer.parseInt(num);
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
				
				//单头主键字段
				Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查询条件
				condition.put("TAGGROUPNO", true);
				//调用过滤函数
				List<Map<String, Object>> getQHeader=MapDistinct.getMap(queryDatas, condition);
				
				for (Map<String, Object> map : getQHeader) {
					DCP_CustomerTagGroupQueryRes.level1Elm lv1 = res.new level1Elm();
					String tagGroupNo = map.get("TAGGROUPNO").toString();
					String tagGroupName = map.get("TAGGROUPNAME").toString();
					String mutualExclusion = map.get("MUTUALEXCLUSION").toString();
					String status = map.get("STATUS").toString();
					lv1.setTagGroupNo(tagGroupNo);
					lv1.setTagGroupName(tagGroupName);
					lv1.setMutualExclusion(mutualExclusion);
					lv1.setStatus(status);
					
					lv1.setChildren(new ArrayList<DCP_CustomerTagGroupQueryRes.level2Elm>());
					
					for (Map<String, Object> map2 : queryDatas) {
						DCP_CustomerTagGroupQueryRes.level2Elm lv2 = res.new level2Elm();
						if(tagGroupNo.equals(map2.get("TAGGROUPNO").toString())){
							String tagNo = map2.get("TAGNO").toString();
							String tagName = map2.get("TAGNAME").toString();
							String syStatus = map2.get("SYSTATUS").toString();
							if(Check.Null(tagNo)){
								continue;
							}
							
							lv2.setTagNo(tagNo);
							lv2.setTagName(tagName);
							lv2.setStatus(syStatus);
							lv1.getChildren().add(lv2);
							
						}
						
					}
					res.getDatas().add(lv1);
					
				}
			}
			
			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);
			
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败！");
		}
		
		
		
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_CustomerTagGroupQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		StringBuffer sqlbuf = new StringBuffer();
		String sql = "";
		//分页处理
		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();
		int startRow=(pageNumber-1) * pageSize;
		String eId = req.geteId();
		
		String keyTxt = req.getRequest().getKeyTxt();
		
		sqlbuf.append(" SELECT * FROM ( "
				+ " SELECT  count(distinct a.tagGroupNo ) OVER() AS NUM,  dense_rank() over (order BY  a.tagGroupNo ) rn,"
				+ " a.tagGroupNo , a.Taggroupname , a.mutualexclusion , a.STATUS , "
				+ "  to_char( a.createtime , 'yyyy-MM-dd hh24:mi:ss' ) AS createtime , "
				+ "  to_char( a.lastmoditime , 'yyyy-MM-dd hh24:mi:ss' ) AS lastmoditime, "
				+ " b.tagNo , b.tagName , b.STATUS AS sySTATUS "
				+ " FROM DCP_customertaggroup a "
				+ " LEFT JOIN DCP_customertaggroup_detail b ON a.eid = b.eid AND a.tagGroupNo = b.taggroupno "
				+ " WHERE a.eid = '"+eId+"' " );
	
		if (!Check.Null(keyTxt))
		{
			sqlbuf.append(" and ( a.tagGroupNo like '%%"+keyTxt+"%%' or a.Taggroupname like '%%"+keyTxt+"%%') ");
		}
		
		sqlbuf.append(" order by a.tagGroupNo "
				+ " ) t where t.rn > " + startRow + " and t.rn<=" + (startRow+pageSize)
				+ " order BY tagGroupNo");
		sql = sqlbuf.toString();
		return sql;
	}
	
}
