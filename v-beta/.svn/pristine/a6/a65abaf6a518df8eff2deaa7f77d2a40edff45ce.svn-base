package com.dsc.spos.service.imp.json;

import java.text.SimpleDateFormat;
import java.util.*;
import com.dsc.spos.json.cust.req.DCP_BatchPStockInQueryReq;
import com.dsc.spos.json.cust.res.DCP_BatchPStockInQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
/**
 * 服务函数：DCP_BatchPStockInQuery
 * 說明：分批出数单头查询
 * @author JZMA
 * @since  2020-07-07
 */
public class DCP_BatchPStockInQuery extends SPosBasicService<DCP_BatchPStockInQueryReq, DCP_BatchPStockInQueryRes>{
	
	@Override
	protected boolean isVerifyFail(DCP_BatchPStockInQueryReq req) throws Exception {
		boolean isFail = false ;
		StringBuffer errMsg = new StringBuffer();
		if (Check.Null(req.getRequest().getBeginDate())) {
			errMsg.append("开始日期不可为空值, ");
			isFail = true;
		}
		if (Check.Null(req.getRequest().getEndDate())) {
			errMsg.append("结束日期不可为空值, ");
			isFail = true;
		}
		
		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		return false;
	}
	
	@Override
	protected TypeToken<DCP_BatchPStockInQueryReq> getRequestType() {
		return new TypeToken<DCP_BatchPStockInQueryReq>(){};
	}
	
	@Override
	protected DCP_BatchPStockInQueryRes getResponseType() {
		return new DCP_BatchPStockInQueryRes();
	}
	
	@Override
	protected DCP_BatchPStockInQueryRes processJson(DCP_BatchPStockInQueryReq req) throws Exception {
		DCP_BatchPStockInQueryRes res =this.getResponse();
		try {
			String sql = this.getQuerySql(req);
			List<Map<String, Object>> getQData = this.doQueryData(sql, null);
			int totalRecords = 0;	//总笔数
			int totalPages = 0;		//总页数
			res.setDatas(new ArrayList<DCP_BatchPStockInQueryRes.level1Elm>());
			if (getQData != null && !getQData.isEmpty()) {
				//算總頁數
				String num = getQData.get(0).get("NUM").toString();
				totalRecords=Integer.parseInt(num);
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

				/*  endDate 不知道干嘛用的，暂时注释掉了 BY JZMA 20200706
				String endDate= "";	
				String sql_getEDate = this.getQuerySql_EDate();
				String[] condition_getEDate = {eId,organizationNO,eId,organizationNO}; //查詢條件
				List<Map<String, Object>> getQData_getEDate = this.doQueryData(sql_getEDate,condition_getEDate);
				if (getQData_getEDate != null && getQData_getEDate.isEmpty() == false) { 
					endDate=getQData_getEDate.get(0).get("MAX(BDATE)").toString();
				}
				else
				{
					Calendar cal = Calendar.getInstance();//获得当前时间
					SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
					endDate = df.format(cal.getTime());
				}
				 */
                
     
				
				for (Map<String, Object> oneData : getQData) {
					DCP_BatchPStockInQueryRes.level1Elm oneLv1 = res.new level1Elm();
                    
                    String warehouse = oneData.get("WAREHOUSE").toString();
                    String materialWarehouseNo = oneData.get("MATERIALWAREHOUSE").toString();
                    if (Check.Null(warehouse)){
                        warehouse = req.getIn_cost_warehouse();
                    }
                    if (Check.Null(materialWarehouseNo)){
                        materialWarehouseNo = req.getOut_cost_warehouse();
                    }
                    
					oneLv1.setpTemplateNo(oneData.get("PTEMPLATENO").toString());
					oneLv1.setpTemplateName(oneData.get("PTEMPLATE_NAME").toString());
					oneLv1.setWarehouse(warehouse);
					oneLv1.setMaterialWarehouseNo(materialWarehouseNo);
					oneLv1.setTotCqty(oneData.get("TOT_CQTY").toString());
					oneLv1.setTotPqty(oneData.get("TOT_PQTY").toString());
					oneLv1.setTotAmt(oneData.get("TOT_AMT").toString());
					oneLv1.setTotDistriAmt(oneData.get("TOT_DISTRIAMT").toString());
					oneLv1.setbDate(oneData.get("BDATE").toString());
					oneLv1.setStatus("6");
					oneLv1.setOfNo(oneData.get("OFNO").toString());
					
					
					/*
					oneLv1.setProcess_status(oneData.get("PROCESS_STATUS").toString());
					oneLv1.setoType(oneData.get("OTYPE").toString());
					oneLv1.setLoadDocType(oneData.get("LOADDOCTYPE").toString());
					oneLv1.setLoadDocNo(oneData.get("LOADDOCNO").toString());
					oneLv1.setCreateBy(oneData.get("CREATEBY").toString());
					oneLv1.setCreateByName(oneData.get("CREATEBYNAME").toString());
					oneLv1.setCreateDate(oneData.get("CREATEDATE").toString());
					oneLv1.setCreateTime(oneData.get("CREATETIME").toString());
					oneLv1.setAccountBy(oneData.get("ACCOUNTBY").toString());
					oneLv1.setAccountByName(oneData.get("ACCOUNTBYNAME").toString());
					oneLv1.setAccountDate(oneData.get("ACCOUNTDATE").toString());
					oneLv1.setAccountTime(oneData.get("ACCOUNTTIME").toString());
					oneLv1.setConfirmBy(oneData.get("CONFIRMBY").toString());
					oneLv1.setConfirmByName(oneData.get("CONFIRMBYNAME").toString());
					oneLv1.setConfirmDate(oneData.get("CONFIRMDATE").toString());
					oneLv1.setConfirmTime(oneData.get("CONFIRMTIME").toString());
					oneLv1.setCancelBy(oneData.get("CANCELBY").toString());
					oneLv1.setCancelByName(oneData.get("CANCELBYNAME").toString());
					oneLv1.setCancelDate(oneData.get("CANCELDATE").toString());
					oneLv1.setCancelTime(oneData.get("CANCELTIME").toString());
					oneLv1.setModifyBy(oneData.get("MODIFYBY").toString());
					oneLv1.setModifyByName(oneData.get("MODIFYBYNAME").toString());
					oneLv1.setModifyDate(oneData.get("MODIFYDATE").toString());
					oneLv1.setModifyTime(oneData.get("MODIFYTIME").toString());
					oneLv1.setSubmitBy(oneData.get("SUBMITBY").toString());
					oneLv1.setSubmitByName(oneData.get("SUBMITBYNAME").toString());
					oneLv1.setSubmitDate(oneData.get("SUBMITDATE").toString());
					oneLv1.setSubmitTime(oneData.get("SUBMITTIME").toString());
					oneLv1.setUpdate_time(oneData.get("UPDATE_TIME").toString());
					*/
					
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
	protected String getQuerySql(DCP_BatchPStockInQueryReq req) throws Exception {
		StringBuffer sqlbuf = new StringBuffer();
		String eId = req.geteId();
		String shopId = req.getShopId();
		String beginDate = req.getRequest().getBeginDate();
		String endDate = req.getRequest().getEndDate();
		String keyTxt = req.getRequest().getKeyTxt();
		String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
		//計算起啟位置
		int pageSize = req.getPageSize();
		int startRow = ((req.getPageNumber() - 1) * pageSize);
		sqlbuf.append(" "
				+ " select * from ("
				+ " select count(*) over() num,row_number() over (order by a.rw desc,a.ptemplateno,a.bdate,a.ofno) rn,"
				+ " a.* from ("
				+ " select 1 as rw,a.eid,a.pdate as bdate,N'' as ptemplateno,N'' as ptemplate_name,a.processtasksumno as ofno,"
				+ " a.tot_cqty,a.tot_pqty,a.tot_amt,a.tot_distriamt,a.warehouse,a.materialwarehouse"
				+ " from dcp_processtasksum a"
				+ " where a.eid='"+eId+"' and a.shopid='"+shopId+"' and a.pdate>='"+beginDate+"' and a.pdate<='"+endDate+"'"
				+ "");
		
		if(endDate.equals(sDate)) {
			sqlbuf.append(" "
					+ " union all"
					+ " select 4 as rw,a.eid,cast(to_char(sysdate,'yyyymmdd') as nvarchar2(8)) as bdate,"
					+ " a.ptemplateno,a.ptemplate_name,N'' as ofno,"
					+ " 0 as tot_cqty,0 as tot_pqty,0 as tot_amt,0 as tot_distriamt,"
					+ " c.goodswarehouse as warehouse,c.materialwarehouse"
					+ " from dcp_ptemplate a"
					+ " left join dcp_ptemplate_shop c on a.eid=c.eid and a.ptemplateno=c.ptemplateno and a.doc_type=c.doc_type and c.shopid='"+shopId+"'"
					+ " where a.eid='"+eId+"' and a.doc_type='2' and (c.shopid is not null or a.shoptype='1')"
					+ " ");
			
			if (!Check.Null(keyTxt)) {
				sqlbuf.append(" and (a.ptemplateno like '%"+keyTxt+"%' or a.ptemplate_name like '%"+keyTxt+"%') ");
			}
		}
		sqlbuf.append(""
				+ " )a"
				+ " ) where  rn > "+startRow+" and rn <="+(startRow+pageSize)
		);
		
		return sqlbuf.toString();
	}
	
}
