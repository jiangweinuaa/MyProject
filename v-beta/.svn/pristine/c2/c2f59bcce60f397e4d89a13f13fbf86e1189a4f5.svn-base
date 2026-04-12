package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_SupPriceTemplateQueryReq;
import com.dsc.spos.json.cust.res.DCP_SupPriceTemplateQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.MyCommon;
import com.google.gson.reflect.TypeToken;

public class DCP_SupPriceTemplateQuery extends SPosBasicService<DCP_SupPriceTemplateQueryReq,DCP_SupPriceTemplateQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_SupPriceTemplateQueryReq req) throws Exception {
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
	protected TypeToken<DCP_SupPriceTemplateQueryReq> getRequestType() {
		return new TypeToken<DCP_SupPriceTemplateQueryReq>() {};
	}

	@Override
	protected DCP_SupPriceTemplateQueryRes getResponseType() {
		return new DCP_SupPriceTemplateQueryRes();
	}

	@Override
	protected DCP_SupPriceTemplateQueryRes processJson(DCP_SupPriceTemplateQueryReq req) throws Exception {
		DCP_SupPriceTemplateQueryRes res=this.getResponse();

		int totalRecords = 0; //总笔数
		int totalPages = 0;	

		String sql=getQuerySql(req);
		List<Map<String , Object>> getData=this.doQueryData(sql, null);

		res.setDatas(new ArrayList<>());

		if (getData!=null && !getData.isEmpty()) {
			String num = getData.get(0).get("NUM").toString();
			totalRecords=Integer.parseInt(num);

			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;	

			
			StringBuffer sJoinA=new StringBuffer();
            StringBuffer sJoinB=new StringBuffer();
			for (Map<String, Object> mapAll : getData) {
				sJoinA.append(mapAll.get("EID").toString()+",");
				sJoinB.append(mapAll.get("TEMPLATEID").toString()+",");
			}
			
			Map<String, String> map= new HashMap<>();

			map.put("EID", sJoinA.toString());
			map.put("TEMPLATEID", sJoinB.toString());

			MyCommon cm=new MyCommon();
			String withasSql_Pluno=cm.getFormatSourceMultiColWith(map);
			map.clear();
			
			String sqlRange="with p1 as ("+ withasSql_Pluno +") "
					+ "select * from DCP_SUPPRICETEMPLATE_RANGE a inner join p1 "
					+ "on a.eid=p1.eid and a.templateid=p1.templateid ";

			List<Map<String , Object>> getDataRange=this.doQueryData(sqlRange, null);
			
			String sqlLang="with p1 as ("+ withasSql_Pluno +") "
					+ "select * from DCP_SUPPRICETEMPLATE_LANG a inner join p1 "
					+ "on a.eid=p1.eid and a.templateid=p1.templateid ";

			List<Map<String , Object>> getDataLang=this.doQueryData(sqlLang, null);


			for (Map<String, Object> oneData : getData) {
				DCP_SupPriceTemplateQueryRes.level1Elm lv1=res.new level1Elm();
				lv1.setCreateopid(oneData.get("CREATEOPID").toString());
				lv1.setCreateopname(oneData.get("CREATEOPNAME").toString());
				lv1.setCreatetime(oneData.get("CREATETIME").toString());
				lv1.setLastmodiname(oneData.get("LASTMODIOPNAME").toString());
				lv1.setLastmodiopid(oneData.get("LASTMODIOPID").toString());
				lv1.setLastmoditime(oneData.get("LASTMODITIME").toString());
				lv1.setMemo(oneData.get("MEMO").toString());
				lv1.setReceiverType(oneData.get("RECEIVERTYPE").toString());
				lv1.setReceiverIdRange(oneData.get("RECEIVERIDRANGE").toString());
				lv1.setSupplierType(oneData.get("SUPPLIERTYPE").toString());
				lv1.setSupplierId(oneData.get("SUPPLIERID").toString());
				lv1.setSupplierName(oneData.get("ORG_NAME").toString());
				lv1.setStatus(oneData.get("STATUS").toString());
				lv1.setTemplateId(oneData.get("TEMPLATEID").toString());
				lv1.setTemplateName(oneData.get("TEMPLATENAME").toString());

				
				Map<String, Object> condiV= new HashMap<>();
				condiV.put("TEMPLATEID", oneData.get("TEMPLATEID").toString());				
				List<Map<String, Object>> rangeList= MapDistinct.getWhereMap(getDataRange, condiV, true);
				List<Map<String, Object>> langList= MapDistinct.getWhereMap(getDataLang, condiV, true);
				
				lv1.setRangeList(new ArrayList<>());
				for (Map<String, Object> map2 : rangeList) {
					DCP_SupPriceTemplateQueryRes.Range rg=res.new Range();
					rg.setId(map2.get("ID").toString());
					rg.setName(map2.get("NAME").toString());

					lv1.getRangeList().add(rg);
				}

				
				lv1.setTemplateName_lang(new ArrayList<>());
				for (Map<String, Object> map3 : langList) {
					DCP_SupPriceTemplateQueryRes.TemplateLang lg=res.new TemplateLang();
					lg.setLangType(map3.get("LANG_TYPE").toString());
					lg.setName(map3.get("TEMPLATENAME").toString());

					lv1.getTemplateName_lang().add(lg);
				}

				res.getDatas().add(lv1);
			}
		}

		res.setPageNumber(req.getPageNumber());
		res.setPageSize(req.getPageSize());
		res.setTotalRecords(totalRecords);
		res.setTotalPages(totalPages);

		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	}

	@Override
	protected String getQuerySql(DCP_SupPriceTemplateQueryReq req) throws Exception {
		String eId = req.geteId();
		String langType = req.getLangType();
		String ketTxt = req.getRequest().getKeyTxt();
		String status = req.getRequest().getStatus();
		StringBuffer sqlbuf=new StringBuffer();
		//【ID1028343】【意诺V3.0.1.6】未启用要货模版需从云中台，不需要在云中台显示 by jinzma 20221130
		String beginDate = req.getRequest().getBeginDate();
		String endDate = req.getRequest().getEndDate();

		//計算起啟位置
		int pageSize = req.getPageSize();
		int startRow=(req.getPageNumber()-1) * pageSize;
		
		
		sqlbuf.append(""
				+ " select * from ( "
				+ " select count(*) over() num, rownum rn,a.EID,a.templateid,"
				+ " b.templatename,a.SUPPLIERTYPE,a.SUPPLIERID,c.ORG_NAME,a.RECEIVERTYPE,a.RECEIVERIDRANGE, "
				+ " a.memo,a.status,to_char(a.createtime,'yyyy-MM-dd HH24:mi:ss') createtime,a.createopid,a.createopname,"
				+ " to_char(a.lastmoditime,'yyyy-MM-dd HH24:mi:ss') lastmoditime,a.lastmodiopid,a.lastmodiopname "
				+ " from DCP_SUPPRICETEMPLATE a "
				+ " left join DCP_SUPPRICETEMPLATE_LANG b on a.eid=b.eid and a.templateid=b.templateid and b.lang_type='"+langType+"' "
				+ " left join DCP_ORG_LANG c on a.eid=c.eid and a.SUPPLIERID=c.ORGANIZATIONNO and c.LANG_TYPE='"+langType+"' "
				+ " where a.eid='"+eId+"' ");

		if(status != null && status.length() >0) {
			sqlbuf.append(" and a.status="+status +" ");
		}

		if(ketTxt != null && ketTxt.length() >0) {
			sqlbuf.append(" and (b.templateid like '%%"+ketTxt+"%%' or b.templatename like '%%"+ketTxt+"%%') ");
		}
		
		//【ID1028343】【意诺V3.0.1.6】未启用要货模版需从云中台，不需要在云中台显示 by jinzma 20221130
		if (!Check.Null(beginDate) && !Check.Null(endDate)){
			sqlbuf.append(" and to_char(a.createtime,'yyyyMMdd')>='"+beginDate+"' and to_char(a.createtime,'yyyyMMdd')<='"+endDate+"' ");
		}
  

		sqlbuf.append(" ) where rn>"+startRow+" and rn<="+(startRow+pageSize));
		
		return sqlbuf.toString();
	}



}
