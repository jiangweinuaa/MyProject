package com.dsc.spos.service.imp.json;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_POrderTemplateQueryReq;
import com.dsc.spos.json.cust.res.DCP_POrderTemplateQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：DCP_POrderTemplateQuery
 * 服务说明：要货模板查询
 * @author Jinzma
 * @since  2017-03-09
 */
public class DCP_POrderTemplateQuery extends SPosBasicService<DCP_POrderTemplateQueryReq,DCP_POrderTemplateQueryRes>  {
	
	@Override
	protected boolean isVerifyFail(DCP_POrderTemplateQueryReq req) throws Exception {
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
	protected TypeToken<DCP_POrderTemplateQueryReq> getRequestType() {
		return new TypeToken<DCP_POrderTemplateQueryReq>(){};
	}
	
	@Override
	protected DCP_POrderTemplateQueryRes getResponseType() {
		return new DCP_POrderTemplateQueryRes();
	}
	
	@Override
	protected DCP_POrderTemplateQueryRes processJson(DCP_POrderTemplateQueryReq req) throws Exception {
		//查詢資料
		DCP_POrderTemplateQueryRes res = this.getResponse();
		try {
			int totalRecords = 0;								//总笔数
			int totalPages = 0;			                        //总页数
			//单头查询
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
					DCP_POrderTemplateQueryRes.level1Elm oneLv1 = res.new level1Elm();
					
					String isAddGoods = oneData.get("ISADDGOODS").toString();
					if (Check.Null(isAddGoods)) {
						isAddGoods = "N";
					}
					String isShowHeadStockQty = oneData.get("ISSHOWHEADSTOCKQTY").toString();
					if (Check.Null(isShowHeadStockQty)||!isShowHeadStockQty.equals("Y")) {
						isShowHeadStockQty = "N";
					}
					
					oneLv1.setTemplateNo(oneData.get("PTEMPLATENO").toString());
					oneLv1.setTemplateName(oneData.get("PTEMPLATE_NAME").toString());
					oneLv1.setReceiptOrgName(oneData.get("ORG_NAME").toString());
					oneLv1.setReceiptOrgNo(oneData.get("RECEIPT_ORG").toString());
					oneLv1.setTimeType(oneData.get("TIME_TYPE").toString());
					oneLv1.setTimeValue(oneData.get("TIME_VALUE").toString());
					oneLv1.setHqPorder(oneData.get("HQPORDER").toString());
					oneLv1.setShopType(oneData.get("SHOPTYPE").toString());
					oneLv1.setRdate_Type(oneData.get("RDATE_TYPE").toString());
					oneLv1.setRdate_Add(oneData.get("RDATE_ADD").toString());
					oneLv1.setRdate_Values(oneData.get("RDATE_VALUES").toString());
					oneLv1.setRevoke_Day(oneData.get("REVOKE_DAY").toString());
					oneLv1.setRevoke_Time(oneData.get("REVOKE_TIME").toString());
					oneLv1.setRdate_Times(oneData.get("RDATE_TIMES").toString());
					oneLv1.setStatus(oneData.get("STATUS").toString());
					oneLv1.setOptionalTime(oneData.get("OPTIONAL_TIME").toString());
					oneLv1.setPreDay(oneData.get("PRE_DAY").toString());
					oneLv1.setIsAddGoods(isAddGoods);
					oneLv1.setIsShowHeadStockQty(isShowHeadStockQty);
					 
					oneLv1.setSupplierType(oneData.get("SUPPLIERTYPE").toString());
					oneLv1.setAllotType(oneData.get("ALLOTTYPE").toString());
					oneLv1.setFloatScale(oneData.get("FLOATSCALE").toString());
					oneLv1.setCreateBy(oneData.get("CREATEBY").toString());
					oneLv1.setCreateByName(oneData.get("CREATEBYNAME").toString());
					oneLv1.setCreateDeptId(oneData.get("CREATEDEPTID").toString());
					oneLv1.setCreateDeptName(oneData.get("CREATEDEPTNAME").toString());
					oneLv1.setCreateDate(oneData.get("CREATE_DATE").toString());
					oneLv1.setCreateTime(oneData.get("CREATE_TIME").toString());
					oneLv1.setModifyBy(oneData.get("MODIFYBY").toString());
					oneLv1.setModifyByName(oneData.get("MODIFYBYNAME").toString());
					oneLv1.setModifyDate(oneData.get("MODIFY_DATE").toString());
					oneLv1.setModifyTime(oneData.get("MODIFY_TIME").toString());

					
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
	protected String getQuerySql(DCP_POrderTemplateQueryReq req) throws Exception {
		
		String eId = req.geteId();
		String keyTxt = req.getRequest().getKeyTxt();
		String langType= req.getLangType();
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
		 
				+ " where a.eid='"+eId+"' and a.doc_type='0'"
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
        sqlbuf.append(" and a.doc_type='0' ");
		
		sqlbuf.append(" group by a.ptemplateno");
		sqlbuf.append(" )");
		sqlbuf.append(""
				+ " select * from ("
				+ " select count(*) over () num,row_number() over (order by a.tran_time desc) as rn,"
				+ " a.ptemplateno,a.ptemplate_name,a.pre_day,a.optional_time,a.time_type,a.time_value,"
				+ " a.receipt_org,a.status,a.hqporder,a.shoptype,a.rdate_type,a.rdate_add,a.rdate_values,"
				+ " a.revoke_day,a.revoke_time,a.rdate_times,a.isaddgoods,a.isshowheadstockqty,"
				+ " c.org_name,a.create_date"
				+ " ,a.SUPPLIERTYPE,a.ALLOTTYPE,a.FLOATSCALE,a.CREATEBY,a.CREATEDEPTID,a.CREATE_TIME"
				+ " ,a.MODIFYBY,a.MODIFY_DATE,a.MODIFY_TIME "
				+"  ,q.op_NAME as MODIFYBYNAME  , p.op_NAME as CREATEBYNAME,d1.DEPARTNAME as CREATEDEPTNAME  "
				+ " from dcp_ptemplate a"
				+ " inner join ptemplate b on a.ptemplateno=b.ptemplateno"
				+ " left join dcp_org_lang c on a.eid=c.eid and a.receipt_org=c.organizationno and c.lang_type='"+langType+"'"
				+ " left join platform_staffs_lang  p on a.eid=p.eid and p.opno=a.CREATEBY and p.lang_type='"+req.getLangType()+"' "
					+ " left join platform_staffs_lang  q on a.eid=q.eid and q.opno=a.MODIFYBY and q.lang_type='"+req.getLangType()+"' "
					+"  left join DCP_DEPARTMENT_LANG d1  on p.eid=d1.eid and a.CREATEDEPTID=d1.DEPARTNO and d1.lang_type='"+langType+"' "
				+ " where a.eid='"+eId+"' and a.doc_type='0'"
				+ " ) a where a.rn>"+startRow+" and a.rn<="+(startRow+pageSize)+" order by a.rn");
		
		return sqlbuf.toString();
	}
	
	
}


