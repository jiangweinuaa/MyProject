package com.dsc.spos.service.imp.json;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_STakeTemplateQueryReq;
import com.dsc.spos.json.cust.res.DCP_STakeTemplateQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：DCP_STakeTemplateQuery
 * 服务说明：盘点模板查询
 * @author Jinzma
 * @since  2017-03-09
 */
public class DCP_STakeTemplateQuery extends SPosBasicService<DCP_STakeTemplateQueryReq,DCP_STakeTemplateQueryRes>  {
	
	@Override
	protected boolean isVerifyFail(DCP_STakeTemplateQueryReq req) throws Exception {
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
	protected TypeToken<DCP_STakeTemplateQueryReq> getRequestType() {
		return new TypeToken<DCP_STakeTemplateQueryReq>(){};
	}
	
	@Override
	protected DCP_STakeTemplateQueryRes getResponseType() {
		return new DCP_STakeTemplateQueryRes();
	}
	
	@Override
	protected DCP_STakeTemplateQueryRes processJson(DCP_STakeTemplateQueryReq req) throws Exception {
		DCP_STakeTemplateQueryRes res = this.getResponse();
		
		try {
			int totalRecords = 0;
			int totalPages = 0;
			
			String sql=this.getQuerySql(req);
			List<Map<String, Object>> getQData=this.doQueryData(sql, null);
			if (getQData != null && !getQData.isEmpty()) {
				
				//算總頁數
				String num = getQData.get(0).get("NUM").toString();
				totalRecords=Integer.parseInt(num);
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
				
				res.setDatas(new ArrayList<>());
				for (Map<String, Object> oneData : getQData) {
					DCP_STakeTemplateQueryRes.level1Elm oneLv1 = res.new level1Elm();

					//【ID1039808】【金贝儿3403】盘点单审核增加处理不异动库存情况 by jinzma 20240327
					String isAdjustStock = oneData.get("IS_ADJUST_STOCK").toString();   //是否调整库存Y/N/X Y转库存 N转销售 X不异动
					if (Check.Null(isAdjustStock)){
						isAdjustStock="Y" ;
					}
					String rangeWay = oneData.get("RANGEWAY").toString();
					if (Check.Null(rangeWay)){
						rangeWay="0" ;
					}
					String isShowZStock =oneData.get("ISSHOWZSTOCK").toString();
					if (Check.Null(isShowZStock) || !isShowZStock.equals("0")){
						isShowZStock="1" ;
					}
					
					//设置响应
					oneLv1.setTemplateNo(oneData.get("PTEMPLATENO").toString());
					oneLv1.setTemplateName(oneData.get("PTEMPLATE_NAME").toString());
					oneLv1.setStatus(oneData.get("STATUS").toString());
					oneLv1.setIsBtake(oneData.get("IS_BTAKE").toString());
					oneLv1.setTaskWay(oneData.get("TASKWAY").toString());
					oneLv1.setTimeType(oneData.get("TIME_TYPE").toString());
					oneLv1.setTimeValue(oneData.get("TIME_VALUE").toString());
					oneLv1.setStockTakeCheck(oneData.get("STOCKTAKECHECK").toString());
					oneLv1.setShopType(oneData.get("SHOPTYPE").toString());
					oneLv1.setIsAdjustStock(isAdjustStock);
					oneLv1.setRangeWay(rangeWay);
					oneLv1.setIsShowZStock(isShowZStock);
					oneLv1.setCreateDate(oneData.get("CREATE_DATE").toString());
                    oneLv1.setCreateTime(oneData.get("CREATETIME").toString());
                    oneLv1.setCreateBy(oneData.get("CREATEBY").toString());
                    oneLv1.setCreateByName(oneData.get("CREATEBYNAME").toString());
                    oneLv1.setCreateDeptId(oneData.get("CREATEDEPTID").toString());
                    oneLv1.setCreateDeptName(oneData.get("CREATEDEPTNAME").toString());
                    oneLv1.setModifyDate(oneData.get("MODIFYDATE").toString());
                    oneLv1.setModifyTime(oneData.get("MODIFYTIME").toString());
                    oneLv1.setModifyBy(oneData.get("MODIFYBY").toString());
                    oneLv1.setModifyByName(oneData.get("MODIFYBYNAME").toString());
					
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
	protected String getQuerySql(DCP_STakeTemplateQueryReq req) throws Exception {
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
				+ " where a.eid='"+eId+"' and a.doc_type='1'"
		);
		if (!Check.Null(keyTxt)) {
			sqlbuf.append(" and (a.ptemplateno like '%"+keyTxt+"%' or a.ptemplate_name like '%"+keyTxt+"%'"
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
		
		sqlbuf.append(" "
				+ " select * from ("
				+ " select count(*) over () num,row_number() over (order by a.tran_time desc) as rn,"
				+ " a.ptemplateno,a.ptemplate_name,a.is_btake,a.taskway,a.time_type,a.time_value,"
				+ " a.status,a.stocktakecheck,a.shoptype,a.is_adjust_stock,a.rangeway,a.isshowzstock,a.create_date,a.CREATE_TIME as createtime,"
                + " a.CREATEBY,e1.name as createbyname,a.CREATEDEPTID,d1.departname as CREATEDEPTNAME,a.modify_date as modifydate,a.modify_time as modifytime ,"
                + " a.MODIFYBY,e2.name as MODIFYBYNAME "
				+ " from dcp_ptemplate a"
				+ " inner join ptemplate b on a.ptemplateno=b.ptemplateno "
                + " left join dcp_employee e1 on e1.eid=a.eid and e1.employeeno=a.CREATEBY "
                + " left join dcp_department_lang d1 on d1.eid=a.eid and d1.departno=a.CREATEDEPTID "
                + " left join dcp_employee e2 on e2.eid=a.eid and e2.employeeno=a.MODIFYBY "
				+ " where a.eid='"+eId+"' and a.doc_type='1'"
				+ " ) a where a.rn>"+startRow+" and a.rn<="+(startRow+pageSize)+" order by a.rn "
				+ " ");
		
		return sqlbuf.toString();
	}
	
	
}


