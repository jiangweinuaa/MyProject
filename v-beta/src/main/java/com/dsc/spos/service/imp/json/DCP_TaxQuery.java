package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_TaxQueryReq;
import com.dsc.spos.json.cust.res.DCP_TaxQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;

public class DCP_TaxQuery extends SPosBasicService<DCP_TaxQueryReq, DCP_TaxQueryRes> 
{
	@Override
	protected boolean isVerifyFail(DCP_TaxQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return false;
	}

	@Override
	protected TypeToken<DCP_TaxQueryReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_TaxQueryReq>(){};
	}

	@Override
	protected DCP_TaxQueryRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_TaxQueryRes();
	}

	@Override
	protected DCP_TaxQueryRes processJson(DCP_TaxQueryReq req) throws Exception {
	// TODO Auto-generated method stub
		DCP_TaxQueryRes res=new DCP_TaxQueryRes();
		
		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();
		
		//計算起啟位置
		int startRow = ((pageNumber - 1) * pageSize);
		startRow = ((pageNumber - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
		startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料
		
		String sql=" select * from ( select count(*) over() num ,ROWNUM rn,A.* from  DCP_TAXCATEGORY A where EID='"+req.geteId()+"' order by A.TaxCode ) where rn>"+startRow+" and rn<="+(startRow+pageSize)+"  ";
		List<Map<String, Object>> taxlist=this.doQueryData(sql, null);
		int totalRecords;								//总笔数
		int totalPages;									//总页数
		res.setDatas(new ArrayList<DCP_TaxQueryRes.leve1>());
		
		if (taxlist != null && taxlist.isEmpty() == false)
		{ 
			Map<String, Object> oneData_Count = taxlist.get(0);
			String num = oneData_Count.get("NUM").toString();
			totalRecords=Integer.parseInt(num);
			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
			
			for (Map<String, Object> map : taxlist)
			{
				DCP_TaxQueryRes.leve1 leveltemp = res.new leve1();
				leveltemp.setTaxCode(map.get("TAXCODE").toString());
				leveltemp.setTaxName(map.get("TAXNAME").toString());
				leveltemp.setTaxRate(map.get("TAXRATE").toString());
				res.getDatas().add(leveltemp);
				
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
		
	return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	// TODO Auto-generated method stub
	
	}

	@Override
	protected String getQuerySql(DCP_TaxQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

}
