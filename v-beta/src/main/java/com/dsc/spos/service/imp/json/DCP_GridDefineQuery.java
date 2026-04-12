package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_GridDefineQueryReq;
import com.dsc.spos.json.cust.res.DCP_GridDefineQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

/**
 * 自定义表格查询
 * @author yuanyy 2019-02-16
 *
 */
public class DCP_GridDefineQuery extends SPosBasicService<DCP_GridDefineQueryReq, DCP_GridDefineQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_GridDefineQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_GridDefineQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_GridDefineQueryReq>(){};
	}

	@Override
	protected DCP_GridDefineQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_GridDefineQueryRes();
	}

	@Override
	protected DCP_GridDefineQueryRes processJson(DCP_GridDefineQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		
		DCP_GridDefineQueryRes res = null;
		res = this.getResponse();
		
		try {
			
			String sql = "";
			sql = this.getQuerySql(req);
			List<Map<String, Object>> queryDatas = this.doQueryData(sql, null);

			int totalRecords = 0;								//总笔数
			int totalPages = 0;			
			res.setDatas(new ArrayList<DCP_GridDefineQueryRes.level1Elm>());
			
			if(queryDatas != null && queryDatas.size() > 0){
				String num = queryDatas.get(0).get("NUM").toString();
				totalRecords=Integer.parseInt(num);
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
				
				//单头主键字段
				Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查询条件
				condition.put("MODULARNO", true);
				//调用过滤函数
				List<Map<String, Object>> getQHeader=MapDistinct.getMap(queryDatas, condition);
				
				//单头主键字段
				Map<String, Boolean> condition2 = new HashMap<String, Boolean>(); //查询条件
				condition2.put("MODULARNO", true);
				condition2.put("GRIDNO", true);
				//调用过滤函数
				List<Map<String, Object>> gridsDatas = MapDistinct.getMap(queryDatas, condition2);
				
				//单头主键字段
				Map<String, Boolean> condition3 = new HashMap<String, Boolean>(); //查询条件
				condition3.put("MODULARNO", true);
				condition3.put("GRIDNO", true);
				condition3.put("FIELDNAME", true);
				//调用过滤函数
				List<Map<String, Object>> fieldsDatas = MapDistinct.getMap(queryDatas, condition3);
				
				for (Map<String, Object> map : getQHeader) {
					DCP_GridDefineQueryRes.level1Elm lv1 = res.new level1Elm();
					
					String modularNo = map.getOrDefault("MODULARNO", "").toString();
					String modularName = map.getOrDefault("MODULARNAME", "").toString();
					String status = map.getOrDefault("STATUS", "").toString();
					
					lv1.setModularNo(modularNo);
					lv1.setModularName(modularName);
					lv1.setStatus(status);
					
					
					lv1.setGrids(new ArrayList<DCP_GridDefineQueryRes.level2Elm>());
					for (Map<String, Object> map2 : gridsDatas) {
						DCP_GridDefineQueryRes.level2Elm lv2 = res.new level2Elm();
						if(modularNo.equals(map2.getOrDefault("MODULARNO", "").toString())){
							String gridNo = map2.getOrDefault("GRIDNO", "").toString();
							lv2.setGridNo(gridNo);
							
							lv2.setFields(new ArrayList<DCP_GridDefineQueryRes.level3Elm>());
							
							for (Map<String, Object> map3 : fieldsDatas) {

								DCP_GridDefineQueryRes.level3Elm lv3 = res.new level3Elm();
								
								if(modularNo.equals(map3.getOrDefault("MODULARNO", "").toString()) 
									&& gridNo.equals(map3.getOrDefault("GRIDNO", "").toString())){
									
									String fieldName = map3.getOrDefault("FIELDNAME", "").toString();
									String isShow = map3.getOrDefault("ISSHOW", "").toString();
									String isMove = map3.getOrDefault("ISMOVE", "").toString();
									String priority = map3.getOrDefault("PRIORITY", "").toString();
									
									lv3.setFieldName(fieldName);
									lv3.setIsShow(isShow);
									lv3.setIsMove(isMove);
									lv3.setPriority(priority);
									
									lv2.getFields().add(lv3);
									
								}
								
							}
							
						}
						
						lv1.getGrids().add(lv2);
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
			res.setServiceDescription("服务执行异常！");
			
		}
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_GridDefineQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		StringBuffer sqlbuf = new StringBuffer();
		String sql = "";
		//分页处理
		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();
		int startRow=(pageNumber-1) * pageSize;
		String eId = req.geteId();
		
		String modularNo = req.getRequest().getModularNo();
		String gridNo = req.getRequest().getGridNo();
		String fieldName = req.getRequest().getFieldName();
		String isShow = req.getRequest().getIsShow();
		String isMove = req.getRequest().getIsMove();
		
		String langType = req.getLangType();
		
		sqlbuf.append(" SELECT * from ( "
				+ " select   count(distinct  a.MODULARNO ) OVER() AS NUM,  dense_rank() over (order BY  a.modularNo ) rn , " );
		
		if(langType.equals("zh_CN")){
			sqlbuf.append(" b.chsmsg AS modularName,  ");
		}
		else if(langType.equals("zh_TW")){
			sqlbuf.append(" b.chtmsg AS modularName,  ");
		}else{
			sqlbuf.append(" b.engmsg AS modularName,  ");
		}
		
		sqlbuf.append(" a.EID  ,  a.MODULARNO  ,a.GRIDNO  , a.FIELDNAME    ,  a.ISSHOW,  a.ISMOVE   ,  a.PRIORITY  , "
		+ " a.LASTMODIOPID ,  a.LASTMODIOPNAME ,  a.LASTMODITIME  ,  a.STATUS   "
		+ " FROM DCP_GRIDDEFINE a"
		+ " LEFT JOIN DCP_MODULAR b ON a.eid = b.EID AND a.modularNo = b.modularno AND b.status='100'   "
		+ " WHERE a.eid = '"+eId+"' " );
		
		if (!Check.Null(modularNo))
		{
			sqlbuf.append(" and  a.modularNo = '"+modularNo+"'");
		}
		
		if (!Check.Null(gridNo))
		{
			sqlbuf.append(" and a.gridNo = '"+gridNo+"'  ");
		}
		
		if (!Check.Null(fieldName))
		{
			sqlbuf.append(" and  a.fieldName like '%%"+fieldName+"%%'  ");
		}
		
		if (!Check.Null(isShow))
		{
			sqlbuf.append(" and  a.isShow = '"+isShow+"'  ");
		}
		
		if (!Check.Null(isMove))
		{
			sqlbuf.append(" and  a.isMove = '"+isMove+"'  ");
		}
		
		sqlbuf.append(" order by a.priority   "
				+ " ) t where t.rn > " + startRow + " and t.rn<=" + (startRow+pageSize)
				+ " order BY priority");
		sql = sqlbuf.toString();
		return sql;
	}

}
