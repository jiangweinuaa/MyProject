package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ConcModQueryReq;
import com.dsc.spos.json.cust.res.DCP_ConcModQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.ESBUtils;
import com.dsc.spos.utils.PageQueryInfo;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Map;

public class DCP_ConcModQuery extends SPosBasicService<DCP_ConcModQueryReq,DCP_ConcModQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_ConcModQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();
		if(req.getRequest()==null) {
			isFail = true;
			errMsg.append("request不能为空 ");
		}
		if (isFail) {
			throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_ConcModQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_ConcModQueryReq>() {};
	}

	@Override
	protected DCP_ConcModQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_ConcModQueryRes();
	}
	@Override
	protected boolean AuthCheck(DCP_ConcModQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	protected DCP_ConcModQueryRes processJson(DCP_ConcModQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_ConcModQueryRes res= this.getResponse();
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
		res.setDatas(new ArrayList<DCP_ConcModQueryRes.level1Elm>());
		if(PageData.getDatas()!=null && !PageData.getDatas().isEmpty()) {
			for (Map<String, Object> map : PageData.getDatas()) {
				DCP_ConcModQueryRes.level1Elm lv1=res.new level1Elm();
				lv1.setRFuncNo(map.get("RFUNCNO").toString());
				lv1.setRFuncName(map.get("RFUNC_NAME").toString());
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
	protected String getQuerySql(DCP_ConcModQueryReq req) throws Exception {
		String sql="select A.RFUNCNO,B.RFUNC_NAME from DCP_REGEDISTMODULAR A left join DCP_REGEDISTMODULAR_LANG B "
				+ " on A.RFUNCNO=B.RFUNCNO "
				+ " where A.RTYPEINFO='4' AND  B.lang_type='"+req.getLangType()+"'  "
				+ " and exists (select 8 from PLATFORM_CREGISTERDETAIL c where a.RFUNCNO=c.PRODUCTTYPE and  c.BDATE<=TO_CHAR(SYSDATE,'YYYYMMDD')  AND c.EDATE>=TO_CHAR(SYSDATE,'YYYYMMDD'))"
				+ " order by A.RMODULARNO,cast(A.RFUNCNO as int)  ";
		return sql;
	}

}
