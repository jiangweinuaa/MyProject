package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_EInvoiceTempShopQueryReq;
import com.dsc.spos.json.cust.res.DCP_EInvoiceTempShopQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_EInvoiceTempShopQuery extends SPosBasicService<DCP_EInvoiceTempShopQueryReq, DCP_EInvoiceTempShopQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_EInvoiceTempShopQueryReq req) throws Exception {
	// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuilder errMsg = new StringBuilder();
		
		if(Check.Null(req.getRequest().getTemplateNo()))
		{
			isFail = true;
			errMsg.append("模板编号不能为空！");
		}
		
		if(isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
	return isFail;
	}

	@Override
	protected TypeToken<DCP_EInvoiceTempShopQueryReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_EInvoiceTempShopQueryReq>(){};
	}

	@Override
	protected DCP_EInvoiceTempShopQueryRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_EInvoiceTempShopQueryRes();
	}

	@Override
	protected DCP_EInvoiceTempShopQueryRes processJson(DCP_EInvoiceTempShopQueryReq req) throws Exception {
	// TODO Auto-generated method stub
		DCP_EInvoiceTempShopQueryRes res = this.getResponse();
		String eId = req.geteId();
		String templateNo = req.getRequest().getTemplateNo();
		String langType = req.getLangType();
		
		StringBuilder sqlBur = new StringBuilder();
		sqlBur.append("select * from (");
		sqlBur.append(" select A.*,B.ORG_NAME as SHOPNAME from DCP_EINVOICESET_shop A left join DCP_ORG_lang  b on  A.EID=B.EID  and A.SHOPID=B.ORGANIZATIONNO ");
		sqlBur.append(" where A.EID='"+eId+"' and A.Ptemplateno='"+templateNo+"' and B.Lang_Type='"+langType+"'");
		sqlBur.append(")");
		
		String sql = sqlBur.toString();
		try 
		{
			List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);
			res.setDatas(new ArrayList<DCP_EInvoiceTempShopQueryRes.level1Elm>());
			
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
			{
				for (Map<String, Object> map : getQDataDetail) 
				{
					DCP_EInvoiceTempShopQueryRes.level1Elm oneLv1 = res.new level1Elm();
					oneLv1.setShopId(map.get("SHOPID").toString());
					oneLv1.setShopName(map.get("SHOPNAME").toString());
					res.getDatas().add(oneLv1);
					oneLv1 = null;
			
				}
				
			}
		
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		} 
		catch (Exception e) 
		{
		// TODO: handle exception
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	// TODO Auto-generated method stub
	
	}

	@Override
	protected String getQuerySql(DCP_EInvoiceTempShopQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

}
