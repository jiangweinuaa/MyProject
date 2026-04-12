package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.json.cust.req.DCP_SaleTargetQueryReq;
import com.dsc.spos.json.cust.res.DCP_SaleTargetQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_SaleTargetQuery extends SPosBasicService<DCP_SaleTargetQueryReq,DCP_SaleTargetQueryRes> {
	@Override
	protected boolean isVerifyFail(DCP_SaleTargetQueryReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();
		if(req.getRequest()==null) {
			isFail = true;
			errMsg.append("request不能为空,");
		}else{
			String beginDate = req.getRequest().getBeginDate();
			String endDate = req.getRequest().getEndDate();
			if (Check.Null(beginDate)){
				isFail = true;
				errMsg.append("beginDate不能为空,");
			}
			if (Check.Null(endDate)){
				isFail = true;
				errMsg.append("endDate不能为空,");
			}
		}
		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return false;
	}
	
	@Override
	protected TypeToken<DCP_SaleTargetQueryReq> getRequestType() {
		return new TypeToken<DCP_SaleTargetQueryReq>() {};
	}
	
	@Override
	protected DCP_SaleTargetQueryRes getResponseType() {
		return new DCP_SaleTargetQueryRes();
	}
	
	@Override
	protected DCP_SaleTargetQueryRes processJson(DCP_SaleTargetQueryReq req) throws Exception {
		try {
			//查询时间段内的销售目标
			DCP_SaleTargetQueryRes res = this.getResponse();
			res.setDatas(new ArrayList<>());
			
			String sql = getQuerySql(req);
			List<Map<String, Object>> lissqList = this.doQueryData(sql, null);
			if (lissqList != null && !lissqList.isEmpty()) {
				for (Map<String, Object> map : lissqList) {
					DCP_SaleTargetQueryRes.level1Elm lv1 = res.new level1Elm();
					lv1.setShopId(map.get("SHOPID").toString());
					lv1.setShopName(map.get("ORG_NAME").toString());
					lv1.setDateType(map.get("DATETYPE").toString());
					lv1.setDateValue(map.get("DATEVALUE").toString());
					lv1.setSaleAMT(map.get("SALEAMT").toString());
					lv1.setSaleType(map.get("SALETYPE").toString());
					res.getDatas().add(lv1);
				}
			}
			
			return res;
			
		}catch(Exception e){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
	}
	
	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	}
	
	@Override
	protected String getQuerySql(DCP_SaleTargetQueryReq req) throws Exception {
		
		String oShopId = req.getRequest().getoShopId();
		String beginDate = req.getRequest().getBeginDate();
		String endDate = req.getRequest().getEndDate();
		
		String sql = " "
				+ " select nvl(a.saletype,b.saletype) as saletype,nvl(a.datetype,b.datetype) as datetype,"
				+ " nvl(a.datevalue,b.datevalue) as datevalue,nvl(a.saleamt,b.saleamt) as saleamt,nvl(a.shopid,b.shopid) as shopid,"
				+ " c.org_name"
				+ " from dcp_saletarget_shop a "
				+ " full join dcp_saletarget b on a.eid=b.eid and a.shopid=b.shopid "
				+ " and a.saletype=b.saletype and a.datetype=b.datetype and a.datevalue=b.datevalue "
				+ " left join dcp_org_lang c on a.eid=c.eid and a.shopid=c.organizationno and c.lang_type='"+req.getLangType()+"'"
				+ " where a.eid ='"+req.geteId()+"' and ((a.datevalue>='"+beginDate+"' and a.datevalue<='"+endDate+"') "
				+ " or (a.datevalue >='"+beginDate.substring(0,6)+"' and a.datevalue<='"+endDate.substring(0, 6)+"'))";
		
		if (!Check.Null(oShopId)) {
			sql += " and (a.shopid='"+oShopId+"' or b.shopid='"+oShopId+"')";
		}
		
		sql += " order by a.datetype desc,a.datevalue desc";
		
		return sql;
	}
	
}
