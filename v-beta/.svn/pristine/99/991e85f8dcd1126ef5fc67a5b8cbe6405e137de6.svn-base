package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_LStockOutTemplateQueryReq;
import com.dsc.spos.json.cust.res.DCP_LStockOutTemplateQueryRes;
import com.dsc.spos.json.cust.res.DCP_LStockOutTemplateQueryRes.level1Elm;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 查询 报损模板 2019-01-08
 * @author yuanyy
 *
 */
public class DCP_LStockOutTemplateQuery extends SPosBasicService<DCP_LStockOutTemplateQueryReq, DCP_LStockOutTemplateQueryRes> {
	
	@Override
	protected boolean isVerifyFail(DCP_LStockOutTemplateQueryReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();
		if(req.getRequest()==null) {
			isFail = true;
			errMsg.append("request不能为空 ");
		}
		
		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return false;
	}
	
	@Override
	protected TypeToken<DCP_LStockOutTemplateQueryReq> getRequestType() {
		return new TypeToken<DCP_LStockOutTemplateQueryReq>(){};
	}
	
	@Override
	protected DCP_LStockOutTemplateQueryRes getResponseType() {
		return new DCP_LStockOutTemplateQueryRes();
	}
	
	@Override
	protected DCP_LStockOutTemplateQueryRes processJson(DCP_LStockOutTemplateQueryReq req) throws Exception {
		//查詢資料
		DCP_LStockOutTemplateQueryRes res = this.getResponse();
		
		try {
			int totalRecords = 0;
			int totalPages = 0;
			
			String sql=this.getQuerySql(req);
			List<Map<String, Object>> getQData=this.doQueryData(sql, null);
			res.setDatas(new ArrayList<>());
			if (getQData != null && !getQData.isEmpty()) {
				//算總頁數
				String num = getQData.get(0).get("NUM").toString();
				totalRecords=Integer.parseInt(num);
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
				
				for (Map<String, Object> oneData : getQData) {
					level1Elm oneLv1 = res.new level1Elm();
					
					oneLv1.setTemplateNo(oneData.get("PTEMPLATENO").toString());
					oneLv1.setTemplateName(oneData.get("PTEMPLATE_NAME").toString());
					oneLv1.setTimeType(oneData.get("TIME_TYPE").toString());
					oneLv1.setTimeValue(oneData.get("TIME_VALUE").toString());
					oneLv1.setShopType(oneData.get("SHOPTYPE").toString());
					oneLv1.setStatus(oneData.get("STATUS").toString());
					oneLv1.setCreateDate(oneData.get("CREATE_DATE").toString());
					
					res.getDatas().add(oneLv1);
				}
			}
			
			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);
			
			return res;
			
		} catch (Exception e) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
		}
		
	}
	
	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	}
	
	@Override
	protected String getQuerySql(DCP_LStockOutTemplateQueryReq req) throws Exception {
		
		String eId = req.geteId();
		String keyTxt = req.getRequest().getKeyTxt();
		String langType = req.getLangType();
		StringBuffer sqlbuf=new StringBuffer();
		//【ID1028343】【意诺V3.0.1.6】未启用要货模版需从云中台，不需要在云中台显示 by jinzma 20221130
		String beginDate = req.getRequest().getBeginDate();
		String endDate = req.getRequest().getEndDate();
		String status = req.getRequest().getStatus();
		//分页处理
		int pageSize=req.getPageSize();
		int startRow=(req.getPageNumber()-1) * pageSize;
		
		sqlbuf.append(""
				+ " with ptemplate as ("
				+ " select a.ptemplateno from dcp_ptemplate a"
				+ " left join dcp_ptemplate_detail b on a.eid=b.eid and a.ptemplateno=b.ptemplateno and a.doc_type=b.doc_type"
				+ " left join dcp_goods_lang d on a.eid=d.eid and b.pluno=d.pluno and d.lang_type='"+langType+"'"
				+ " where a.eid='"+eId+"' and a.doc_type='4'"
		);
		if (!Check.Null(keyTxt)) {
			sqlbuf.append(" and (a.ptemplateno like '%"+keyTxt+"%' or a.ptemplate_name like '%"+keyTxt+"%' "
					+ " or b.pluno like '%"+keyTxt+"%' or d.plu_name like '%"+keyTxt+"%') ");
		}
		
		//【ID1028343】【意诺V3.0.1.6】未启用要货模版需从云中台，不需要在云中台显示 by jinzma 20221130
		if (!Check.Null(beginDate) && !Check.Null(endDate)){
			sqlbuf.append(" and a.create_date>='"+beginDate+"' and a.create_date<='"+endDate+"' ");
		}
		if (!Check.Null(status)){
			sqlbuf.append(" and a.status='"+status+"' ");
		}
		
		sqlbuf.append(" group by a.ptemplateno");
		sqlbuf.append(" )");
		
		
		sqlbuf.append(""
				+ " select * from ("
				+ " select count(*) over () num,row_number() over (order by a.tran_time desc) as rn,"
				+ " a.ptemplateno,a.ptemplate_name,a.time_type,a.time_value,a.status,a.shoptype,a.create_date"
				+ " from dcp_ptemplate a"
				+ " inner join ptemplate b on a.ptemplateno=b.ptemplateno"
				+ " where a.eid='"+eId+"' and a.doc_type='4'"
				+ " ) a where a.rn>"+startRow+" and a.rn<="+(startRow+pageSize)+" order by a.rn");
		
		return sqlbuf.toString();
	}
	
	
}
