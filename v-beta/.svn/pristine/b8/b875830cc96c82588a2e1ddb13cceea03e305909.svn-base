package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_ParaClassQueryReq;
import com.dsc.spos.json.cust.res.DCP_ParaClassQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;

/**
 * 服務函數：DCP_ParaClassQuery 說明：参数分类查询
 * @author yuanyy
 * @since 2020-05-29
 */
public class DCP_ParaClassQuery extends SPosBasicService<DCP_ParaClassQueryReq, DCP_ParaClassQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_ParaClassQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_ParaClassQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_ParaClassQueryReq>() {};
	}

	@Override
	protected DCP_ParaClassQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_ParaClassQueryRes();
	}

	@Override
	protected DCP_ParaClassQueryRes processJson(DCP_ParaClassQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_ParaClassQueryRes res = null;
		res = this.getResponse();
		try {
			String sql = "select * from PLATFORM_PARAMCLASS ";
			List<Map<String, Object>> queryDatas = this.doQueryData(sql, null);
			res.setDatas(new ArrayList<DCP_ParaClassQueryRes.level1Elm>());
			
			String langType = req.getLangType();
			
			if(queryDatas != null && !queryDatas.isEmpty()){
				for (Map<String, Object> map : queryDatas) {
					DCP_ParaClassQueryRes.level1Elm lv1 = res.new level1Elm();
					String classNo = map.get("CLASSNO").toString();
					String className = map.get("CLASSNAME").toString();
					String className_TW = map.get("CLASSNAME_TW").toString();
					String className_EN = map.get("CLASSNAME_EN").toString();
					
					if(langType.equals("zh_TW")){
						className = className_TW;
					}
					
					if(langType.equals("zh_EN")){
						className = className_EN;
					} 
					
					lv1.setClassNo(classNo);
					lv1.setClassName(className);
					res.getDatas().add(lv1);
				}
			}
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败！"+e.getMessage());
		}
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_ParaClassQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
}
