package com.dsc.spos.service.imp.json;
import com.dsc.spos.json.cust.req.DCP_ConcModDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_ConcModDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ESBUtils;
import com.dsc.spos.utils.PageQueryInfo;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Map;

public class DCP_ConcModDetailQuery extends SPosBasicService<DCP_ConcModDetailQueryReq,DCP_ConcModDetailQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_ConcModDetailQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();
		if(req.getRequest()==null) {
			isFail = true;
			errMsg.append("request不能为空 ");
		}else
		{
			if(Check.isEmpty(req.getRequest().getRFuncNo()))
			{
				isFail = true;
				errMsg.append("[rFuncNo]不能为空 ");
			}
		}
		if (isFail) {
			throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}
	@Override
	protected boolean AuthCheck(DCP_ConcModDetailQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return true;
	}
	@Override
	protected TypeToken<DCP_ConcModDetailQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_ConcModDetailQueryReq>() {};
	}

	@Override
	protected DCP_ConcModDetailQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_ConcModDetailQueryRes();
	}

	@Override
	protected DCP_ConcModDetailQueryRes processJson(DCP_ConcModDetailQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_ConcModDetailQueryRes res= this.getResponse();
		String sql=this.getQuerySql(req);
		int totalRecords=0;								//总笔数
		int totalPages=0;									//总页数
		int pageSize = req.getPageSize();
		int pageNumber= req.getPageNumber();
		PageQueryInfo PageData = ESBUtils.getPageData(sql, null, dao, pageNumber, pageSize);
		totalRecords = PageData.getTotalRecords();
		totalPages  =PageData.getTotalPages();
		res.setPageNumber(req.getPageNumber());
		res.setPageSize(req.getPageSize());
		res.setTotalRecords(totalRecords);
		res.setTotalPages(totalPages);
		res.setDatas(new ArrayList<DCP_ConcModDetailQueryRes.level1Elm>());
		if(PageData.getDatas()!=null && !PageData.getDatas().isEmpty()) {
			for (Map<String, Object> map : PageData.getDatas()) {
				DCP_ConcModDetailQueryRes.level1Elm lv1=res.new level1Elm();
				lv1.setRFuncNo(map.get("PRODUCTTYPE").toString());
				lv1.setToken(map.get("TOKEN").toString());
				lv1.setOpNo(map.get("OPNO").toString());
				lv1.setMachineCode(map.get("MACHINECODE").toString());
				lv1.setOpName(map.get("OPNAME").toString());
				lv1.setLastModiTime(map.get("LASTMODITIME").toString());
				lv1.setRfuncName(map.get("RFUNC_NAME").toString());
				res.getDatas().add(lv1);
			}
		}
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_ConcModDetailQueryReq req) throws Exception {
		String sql="select c.EID,c.PRODUCTTYPE,c.MACHINECODE,c.TOKEN,c.OPNO,c.OPNAME,c.LASTMODITIME,d.RFUNC_NAME from PLATFORM_CREGISTERDETAIL c "
				+ " left join DCP_REGEDISTMODULAR_LANG d on c.PRODUCTTYPE=d.RFUNCNO AND d.LANG_TYPE='"+req.getLangType()+"' "
				+ " where c.BDATE<=TO_CHAR(SYSDATE,'YYYYMMDD')  AND c.EDATE>=TO_CHAR(SYSDATE,'YYYYMMDD') "
				+ " AND c.PRODUCTTYPE='"+req.getRequest().getRFuncNo()+"'  AND  TOKEN IS NOT NULL"
				+ " ORDER BY c.PRODUCTTYPE ASC,c.OPNO ";
		return sql;
	}
}
