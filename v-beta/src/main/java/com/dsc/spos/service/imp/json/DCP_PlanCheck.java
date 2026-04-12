package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_PlanCheckReq;
import com.dsc.spos.json.cust.res.DCP_PlanCheckRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;

/**
 * 营业预估删除
 * @author yuanyy
 *
 */
public class DCP_PlanCheck extends SPosBasicService<DCP_PlanCheckReq, DCP_PlanCheckRes> {


	@Override
	protected boolean isVerifyFail(DCP_PlanCheckReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_PlanCheckReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PlanCheckReq>(){};
	}

	@Override
	protected DCP_PlanCheckRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_PlanCheckRes();
	}

	@Override
	protected DCP_PlanCheckRes processJson(DCP_PlanCheckReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_PlanCheckRes res = this.getResponse();
		
		try {
			String eId = req.geteId();
			String shopId = req.getShopId();
			String bDate = req.getbDate();
			
			String docType = req.getDocType(); 
			
			String sql = "";
			StringBuffer sqlbuf = new StringBuffer();
			sqlbuf.append("select * from ");
			if(docType.equals("0")){
				sqlbuf.append(" DCP_PORDER_FORECAST ");
			}
			else{
				sqlbuf.append(" DCP_PLAN");
			}
			sqlbuf.append(" where EID = '"+eId+"' "
					+ " and SHOPID = '"+shopId+"' and bDate = '"+bDate+"'");
			
			sql = sqlbuf.toString();
			
			List<Map<String ,Object>> pfDatas = this.doQueryData(sql, null);
			res.setDatas(new ArrayList<DCP_PlanCheckRes.level1Elm>());
			String allowCreate = "Y";
			if(pfDatas.size() > 0){
				allowCreate = "N";
				for (Map<String, Object> map : pfDatas) {
					
					String docNo = "";
					
					DCP_PlanCheckRes.level1Elm lv1 = res.new level1Elm();
					String planNo = map.getOrDefault("PLANNO","").toString();
					String pfNo =  map.getOrDefault("PFNO","").toString();
					
					if(docType.equals("0")){ // 0: 营业预估
						docNo = pfNo;
					}
					else { // 1：生产计划
						docNo = planNo;
					}
					
					lv1.setDocNo(docNo);
					
					res.getDatas().add(lv1);
					lv1=null;
				}
				
			}
			res.setAllowCreate(allowCreate);
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");	
			
		} catch (Exception e) {

			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败");	
		}
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_PlanCheckReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	
}
