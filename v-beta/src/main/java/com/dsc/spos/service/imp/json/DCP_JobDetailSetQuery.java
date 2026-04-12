package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_JobDetailSetQueryReq;
import com.dsc.spos.json.cust.res.DCP_JobDetailSetQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;

/**
 * job 执行时间查询
 * @author yuanyy 2019-06-24
 *
 */
public class DCP_JobDetailSetQuery extends SPosBasicService<DCP_JobDetailSetQueryReq, DCP_JobDetailSetQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_JobDetailSetQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_JobDetailSetQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_JobDetailSetQueryReq>(){};
	}

	@Override
	protected DCP_JobDetailSetQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_JobDetailSetQueryRes();
	}

	@Override
	protected DCP_JobDetailSetQueryRes processJson(DCP_JobDetailSetQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_JobDetailSetQueryRes res = null;
		res = this.getResponse();
		try {
			
			String sql = "";
			sql = this.getQuerySql(req);
			String[] conditionValues = {};
			List<Map<String , Object>> getTimeDatas = this.doQueryData(sql, conditionValues);
			res.setDatas(new ArrayList<DCP_JobDetailSetQueryRes.level1Elm>());
			
			if(getTimeDatas.size() > 0){
				for (Map<String, Object> map : getTimeDatas) {
					
					DCP_JobDetailSetQueryRes.level1Elm lv1 = res.new level1Elm();
					String item = map.get("ITEM").toString();
					String beginTime = map.get("BEGIN_TIME").toString();
					String endTime = map.get("END_TIME").toString();
					
					lv1.setItem(item);
					lv1.setBeginTime(beginTime);
					lv1.setEndTime(endTime);
					
					res.getDatas().add(lv1);
				}
				
			}
		} catch (Exception e) {

		}
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_JobDetailSetQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		
		String sql = "";
		StringBuffer sqlbuf = new StringBuffer();
		
		sqlbuf.append("select * from JOB_QUARTZ_DETAIL where job_NAME = '"+req.getRequest().getJobName()+"'  order by item ");
		
		sql = sqlbuf.toString();
		return sql;
	}
	
}
