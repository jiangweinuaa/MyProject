package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_SupplierQueryReq;
import com.dsc.spos.json.cust.res.DCP_SupplierQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_SupplierQuery extends SPosBasicService<DCP_SupplierQueryReq, DCP_SupplierQueryRes> {
	@Override
	protected boolean isVerifyFail(DCP_SupplierQueryReq req) throws Exception {
		return false;
	}
	
	@Override
	protected TypeToken<DCP_SupplierQueryReq> getRequestType() {
		return new TypeToken<DCP_SupplierQueryReq>(){};
	}
	
	@Override
	protected DCP_SupplierQueryRes getResponseType() {
		return new DCP_SupplierQueryRes();
	}
	
	@Override
	protected DCP_SupplierQueryRes processJson(DCP_SupplierQueryReq req) throws Exception {
		try {
			DCP_SupplierQueryRes res =  this.getResponse();
			int totalRecords;								//总笔数
			int totalPages;									//总页数
			res.setDatas(new ArrayList<DCP_SupplierQueryRes.level1Elm>());
			//查詢資料
			String sql = this.getQuerySql(req);
			List<Map<String, Object>> getQData = this.doQueryData(sql, null);
			if (getQData != null && !getQData.isEmpty()) {
				//算總頁數
				String num = getQData.get(0).get("NUM").toString();
				totalRecords=Integer.parseInt(num);
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
				
				for (Map<String, Object> oneData : getQData) {
					DCP_SupplierQueryRes.level1Elm oneLv1 = res.new level1Elm();
					
					//设置响应
					oneLv1.setSupplier(oneData.get("SUPPLIER").toString());
					oneLv1.setSupplierName(oneData.get("SUPPLIER_NAME").toString());
					oneLv1.setAbbr(oneData.get("ABBR").toString());
					oneLv1.setMobile(oneData.get("MOBILE").toString());
					oneLv1.setStockInAllowType(oneData.get("STOCKINALLOWTYPE").toString());
					oneLv1.setStockOutAllowType(oneData.get("STOCKOUTALLOWTYPE").toString());
					oneLv1.setTaxCode(oneData.get("TAXCODE").toString());
					oneLv1.setTaxCodeName(oneData.get("TAXNAME").toString());
					oneLv1.setRangeType(oneData.get("RANGETYPE").toString());
					oneLv1.setMemo(oneData.get("MEMO").toString());
					oneLv1.setStatus(oneData.get("STATUS").toString());
					oneLv1.setSelfBuiltShopId(oneData.get("SELFBUILTSHOPID").toString());
					
					//添加
					res.getDatas().add(oneLv1);
				}
			} else {
				totalRecords = 0;
				totalPages = 0;
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
	protected String getQuerySql(DCP_SupplierQueryReq req) throws Exception {
		String eId = req.geteId();
		String langType=req.getLangType();
		String keyTxt = req.getRequest().getKeyTxt();
		String status = req.getRequest().getStatus();
		StringBuffer sqlbuf = new StringBuffer();
		
		//searchScope：0、全部 1、总部和当前自建门店 2、仅总部 3、全部自建门店 4、仅当前自建门店  by jinzma 20220310
		String searchScope = req.getRequest().getSearchScope();
		if (Check.Null(searchScope)){
			searchScope="0";
		}
		String selfBuiltShopId = req.getRequest().getSelfBuiltShopId();
		
		//分页处理
		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();
		int startRow=(pageNumber-1) * pageSize;
		
		sqlbuf.append(""
				+ " select * from ("
				+ " select count(*) over() num,row_number() over (order by a.supplier) rn,"
				+ " a.*,b.supplier_name,b.abbr,b.address,c.taxname"
				+ " from dcp_supplier a"
				+ " left join dcp_supplier_lang b on a.eid=b.eid and a.supplier=b.supplier and b.lang_type='"+langType+"'"
				+ " left join dcp_taxcategory_lang c on a.eid=c.eid and a.taxcode=c.taxcode and TAXAREA='CN' and c.lang_type='"+langType+"'"
				+ " where a.eid='"+eId+"'"
				+ " and a.type='0'"
				+ " and (a.rangetype='0' or a.rangetype='1')"
				+ " ");
		if (!Check.Null(keyTxt)) {
			sqlbuf.append(" and (a.supplier like '%%"+ keyTxt +"%%' or b.supplier_name like '%%"+ keyTxt +"%%' or b.abbr like '%%"+ keyTxt +"%%')");
		}
		if (!Check.Null(status)) {
			sqlbuf.append(" and a.status='"+status+"'");
		}
		
		//searchScope by jinzma 20220310
		switch (searchScope){
			case "0":    //0、全部
				break;
			case "1":    //1、总部和当前自建门店
				sqlbuf.append(" and (a.selfbuiltshopid is null or a.selfbuiltshopid='"+selfBuiltShopId+"')");
				break;
			case "2":    //2、仅总部
				sqlbuf.append(" and a.selfbuiltshopid is null");
				break;
			case "3":    //3、全部自建门店
				sqlbuf.append(" and a.selfbuiltshopid is not null");
				break;
			case "4":    //4、仅当前自建门店
				sqlbuf.append(" and a.selfbuiltshopid='"+selfBuiltShopId+"'");
				break;
		}
		
		sqlbuf.append(" )tbl");
		sqlbuf.append(" where rn>" + startRow + " and rn <="+(startRow+pageSize) );
		sqlbuf.append(" ");
		
		return sqlbuf.toString();
	}
	
	
}
