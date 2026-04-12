package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_MappingPaymentQueryReq;
import com.dsc.spos.json.cust.res.DCP_MappingPaymentQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;

/**
 * 平台映射方式查询	
 * @author yuanyy 2019-04-24
 *
 */
public class DCP_MappingPaymentQuery extends SPosBasicService<DCP_MappingPaymentQueryReq, DCP_MappingPaymentQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_MappingPaymentQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_MappingPaymentQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_MappingPaymentQueryReq>(){};
	}

	@Override
	protected DCP_MappingPaymentQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_MappingPaymentQueryRes();
	}

	@Override
	protected DCP_MappingPaymentQueryRes processJson(DCP_MappingPaymentQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		
		DCP_MappingPaymentQueryRes res = null;
		res = this.getResponse();
		String sql = null;
		int totalRecords;								//总笔数
		int totalPages;

		//单头查询
		sql=this.getQuerySql(req);	
		List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, null);
		DCP_MappingPaymentQueryRes.responseDatas datas = res.new responseDatas();
		datas.setDatas(new ArrayList<DCP_MappingPaymentQueryRes.level1Elm>());
		if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
		{
			Map<String, Object> oneData_Count = getQDataDetail.get(0);
			String num = oneData_Count.get("NUM").toString();
			totalRecords=Integer.parseInt(num);
			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
			for (Map<String, Object> oneData : getQDataDetail) 
			{
				DCP_MappingPaymentQueryRes.level1Elm oneLv1 = res.new level1Elm();
				
				String docType = oneData.get("DOCTYPE").toString();
				String ERP_payCode = oneData.get("ERP_PAYCODE").toString();
				String ERP_payName = oneData.get("ERP_PAYNAME").toString();
				String ERP_payType = oneData.get("ERP_PAYTYPE").toString();
				String order_payCode = oneData.get("ORDER_PAYCODE").toString();
				String order_payName = oneData.get("ORDER_PAYNAME").toString();
				String order_payType = oneData.get("ORDER_PAYTYPE").toString();
				String status = oneData.get("STATUS").toString();
				
				oneLv1.setLoadDocType(docType);
				oneLv1.setERP_payCode(ERP_payCode);
				oneLv1.setERP_payName(ERP_payName);
				oneLv1.setERP_payType(ERP_payType);
				oneLv1.setOrder_payCode(order_payCode);
				oneLv1.setOrder_payName(order_payName);
				oneLv1.setOrder_payType(order_payType);
				oneLv1.setStatus(status);
				datas.getDatas().add(oneLv1);
				
			}
		}
		else
		{
			totalRecords = 0;
			totalPages = 0;
		}	
		
		res.setPageNumber(req.getPageNumber());
		res.setPageSize(req.getPageSize());
		res.setTotalRecords(totalRecords);
		res.setTotalPages(totalPages);
		
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");
	
		res.setDatas(datas);
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_MappingPaymentQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql=null;			
		String eId= req.geteId();
		String keyTxt = "";
		String docType = "";
		if(req.getRequest()!=null)
		{
			keyTxt = req.getRequest().getKeyTxt();
			docType = req.getRequest().getLoadDocType();
		}
		
		
		int pageSize = req.getPageSize(); 
		//計算起啟位置
		int startRow = ((req.getPageNumber() - 1) * req.getPageSize());
		startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
		startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料
		
		if(pageSize == 0){
			pageSize = 10;
		}
		
		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append( " SELECT * FROM ("
			+ " SELECT count(*) over () NUM  , ROWNUM AS rn ,  load_docType AS docType , payCode AS ERP_payCode , "
			+ " payName AS ERP_payName , paycodeERP AS ERP_payType , "
			+ " order_payCode, order_payName , order_payType AS order_payType , status   "
			+ " FROM DCP_mappingpayment a "
			+ " WHERE EID = '"+eId+"' " );
		
		if (keyTxt != null && keyTxt.length()>0)
		{
			sqlbuf.append(" AND (a.payCode like '%%"+ keyTxt +"%%' OR a.payName LIKE '%%"+ keyTxt +"%%')  ");
		}
		
		if (docType != null && docType.length()>0)
		{
			sqlbuf.append(" AND a.load_docType = '"+ docType +"'   ");
		}
		
		sqlbuf.append( " order by load_docType ) WHERE rn>" + startRow + " and rn<=" + (startRow+pageSize) + "  "
				+ " order by  docType   ");
		sql = sqlbuf.toString();
		return sql;
	}

}
