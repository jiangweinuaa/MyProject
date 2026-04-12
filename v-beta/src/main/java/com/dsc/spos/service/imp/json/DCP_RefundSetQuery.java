package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_RefundSetQueryReq;
import com.dsc.spos.json.cust.res.DCP_RefundSetQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_RefundSetQuery extends SPosBasicService<DCP_RefundSetQueryReq, DCP_RefundSetQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_RefundSetQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		DCP_RefundSetQueryReq.Level1Elm request = req.getRequest();

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		if (request == null)
		{
			errMsg.append("request不可为空, ");
			isFail = true;
			
		}else{
			if (Check.Null(request.getAppId())) 
			{
				errMsg.append("公众号代号不可为空值, ");
				isFail = true;
			} 
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}	

		return isFail;	
	}

	@Override
	protected TypeToken<DCP_RefundSetQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_RefundSetQueryReq>() {
		};
	}

	@Override
	protected DCP_RefundSetQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_RefundSetQueryRes();
	}

	List<Map<String, Object>> allFuncDatas = new ArrayList<Map<String, Object>>();

	@Override
	protected DCP_RefundSetQueryRes processJson(DCP_RefundSetQueryReq req) throws Exception {
		// TODO Auto-generated method stub

		
		// 查詢資料
		DCP_RefundSetQueryRes res = new DCP_RefundSetQueryRes();
		List<String> params = new ArrayList<>();
		String sql=getQuerySql(req);
		params.add(req.geteId());
		params.add(req.getRequest().getAppId());
		List<Map<String, Object>> result = this.doQueryData(sql,params.toArray(new String[0]));
		
		if (result == null || result.isEmpty()) {
			res.setDatas(new DCP_RefundSetQueryRes().new Level1Elm());
			res.setSuccess(true);
	        res.setServiceStatus("000");
	        res.setServiceDescription("服务执行成功");
			return res;
		}
		
		
		
		Map<String, Object> map = result.get(0);
		DCP_RefundSetQueryRes.Level1Elm level1Elm = new DCP_RefundSetQueryRes().new Level1Elm();
		level1Elm.setApplyRefund(Integer.valueOf(map.get("APPLYREFUND")==null?"0":map.get("APPLYREFUND").toString()));
		level1Elm.setApplyRefundDelivery(Integer.valueOf(map.get("APPLYREFUNDDELIVERY")==null?"0":map.get("APPLYREFUNDDELIVERY").toString()));
		level1Elm.setApplyRefundLimit(Integer.valueOf(map.get("APPLYREFUNDLIMIT")==null?"0":map.get("APPLYREFUNDLIMIT").toString()));
		level1Elm.setAutoRefund(map.get("AUTOREFUND")==null?"0":map.get("AUTOREFUND").toString());
		level1Elm.setCancelLimit(Integer.valueOf(map.get("CANCELLIMIT")==null?"0":map.get("CANCELLIMIT").toString()));
		level1Elm.setRefundMember(map.get("REFUNDMEMBER")==null?"0":map.get("REFUNDMEMBER").toString());
		level1Elm.seteId(req.geteId());
		level1Elm.setAppId(req.getRequest().getAppId());
		res.setDatas(level1Elm);
		res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
		return res;

	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_RefundSetQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append("select a.*")
		.append(" from CRM_REFUNDSET a  ")
		.append(" where a.EID = ? and a.APPID = ? ");
		sql = sqlbuf.toString();
		return sql;
	}


}
