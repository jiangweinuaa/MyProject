package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_ParaModularQueryReq;
import com.dsc.spos.json.cust.res.DCP_ParaModularQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;

/**
 * 可选作业代号查询
 * @author Huawei
 *
 */
public class DCP_ParaModularQuery extends SPosBasicService<DCP_ParaModularQueryReq, DCP_ParaModularQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_ParaModularQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_ParaModularQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_ParaModularQueryReq>(){};
	}

	@Override
	protected DCP_ParaModularQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_ParaModularQueryRes();
	}

	@Override
	protected DCP_ParaModularQueryRes processJson(DCP_ParaModularQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_ParaModularQueryRes res = null;
		res = this.getResponse();
		try {
			
			String sql = this.getQuerySql(req);
			List<Map<String, Object>> mDatas = this.doQueryData(sql, null);
			res.setDatas(new ArrayList<DCP_ParaModularQueryRes.level1Elm>());
			if(mDatas !=null && !mDatas.isEmpty()){
				
				for (Map<String, Object> map : mDatas) {
					DCP_ParaModularQueryRes.level1Elm lv1 = res.new level1Elm();
					String modularId = map.get("MODULARID").toString();
					String modularName = map.get("MODULARNAME").toString();
					lv1.setModularId(modularId);
					lv1.setModularName(modularName);
					res.getDatas().add(lv1);
				}
			}
			
		} catch (Exception e) {
			// TODO: handle exception
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
	protected String getQuerySql(DCP_ParaModularQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		
		String sql = "";
		StringBuffer sqlbuf = new StringBuffer();
		sqlbuf.append(" select modularId, modularName  from PLATFORM_MODULAR_FIXED order by modularId ");
		
		sql = sqlbuf.toString();
		return sql;
	}
	
}
