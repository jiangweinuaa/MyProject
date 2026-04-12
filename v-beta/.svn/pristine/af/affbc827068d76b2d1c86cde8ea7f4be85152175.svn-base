package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_TaxCategoryQueryReq;
import com.dsc.spos.json.cust.res.DCP_TaxCategoryQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

/**
 * 税别信息查询 
 * @author yuanyy 2019-03-12 
 *
 */
public class DCP_TaxCategoryQuery extends SPosBasicService<DCP_TaxCategoryQueryReq, DCP_TaxCategoryQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_TaxCategoryQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_TaxCategoryQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_TaxCategoryQueryReq>(){};
	}

	@Override
	protected DCP_TaxCategoryQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_TaxCategoryQueryRes();
	}

	@Override
	protected DCP_TaxCategoryQueryRes processJson(DCP_TaxCategoryQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql = null;
		DCP_TaxCategoryQueryRes res = null;
		res = this.getResponse();
		String langType_cur = req.getLangType();
		int totalRecords = 0; //总笔数
		int totalPages = 0;	
		 
		sql = this.getQuerySql(req);
		List<Map<String, Object>> getDatas = this.doQueryData(sql, null);
		
		if(!getDatas.isEmpty() && getDatas != null ){
			res.setDatas(new ArrayList<DCP_TaxCategoryQueryRes.level1Elm>());
			String num = getDatas.get(0).get("NUM").toString();
			totalRecords=Integer.parseInt(num);
			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;	
			
			Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
			condition.put("TAXCODE", true);		
			//调用过滤函数
			List<Map<String, Object>> headDatas=MapDistinct.getMap(getDatas, condition);
			
			for (Map<String, Object> oneData : headDatas) {
				
				DCP_TaxCategoryQueryRes.level1Elm lev1 = res.new level1Elm();
				String taxCode = oneData.get("TAXCODE").toString();
				//String taxName = oneData.get("TAXNAME").toString();
				String taxType = oneData.get("TAXTYPE").toString();
				String taxRate = oneData.get("TAXRATE").toString();
				String inclTax = oneData.get("INCLTAX").toString();
				String status = oneData.get("STATUS").toString();
                String taxarea = oneData.get("TAXAREA").toString();
                String taxprop = oneData.get("TAXPROP").toString();
                String taxcaltype = oneData.get("TAXCALTYPE").toString();
//CREATEOPID  CREATEOPNAME CREATETIME LASTMODIOPID LASTMODIOPNAME LASTMODITIME CREATEDEPTID
				String createopid = oneData.get("CREATEOPID").toString();
				String createopname = oneData.get("CREATEOPNAME").toString();
				String createtime = oneData.get("CREATETIME").toString();
				String lastmodiopid = oneData.get("LASTMODIOPID").toString();
				String lastmodiopname = oneData.get("LASTMODIOPNAME").toString();
				String lastmoditime = oneData.get("LASTMODITIME").toString();
				String createdeptid = oneData.get("CREATEDEPTID").toString();
				String createdeptname = oneData.get("CREATEDEPTNAME").toString();

				lev1.setTaxCode(taxCode);
				lev1.setTaxType(taxType);
				lev1.setTaxRate(taxRate);
				lev1.setInclTax(inclTax);
				lev1.setStatus(status);
                lev1.setTaxArea(taxarea);
                lev1.setTaxProp(taxprop);
                lev1.setTaxCalType(taxcaltype);
				lev1.setCreatorID(createopid);
				lev1.setCreatorName(createopname);
				lev1.setCreate_datetime(createtime);
				lev1.setCreatorDeptID(createdeptid);
				lev1.setLastmodifyID(lastmodiopid);
				lev1.setLastmodifyName(lastmodiopname);
				lev1.setLastmodify_datetime(lastmoditime);
				lev1.setCreatorDeptName(createdeptname);

				lev1.setTaxName_lang(new ArrayList<DCP_TaxCategoryQueryRes.level2Elm>());
				for (Map<String, Object> langDatas : getDatas) 
				{							
					//过滤属于此单头的明细
					if(taxCode.equals(langDatas.get("TAXCODE")) == false||taxarea.equals(langDatas.get("TAXAREA"))==false)
						continue;
					
					DCP_TaxCategoryQueryRes.level2Elm lev2 = res.new level2Elm();
					String langType = langDatas.get("LANGTYPE").toString();
					String lTaxName = langDatas.get("TAXNAME").toString();				
					if(langType.equals(langType_cur))
					{
						lev1.setTaxName(lTaxName);
					}
					
					lev2.setLangType(langType);
					lev2.setName(lTaxName);
					lev1.getTaxName_lang().add(lev2);
				}
						
				res.getDatas().add(lev1);
			}
			
		}
		else
		{
			res.setDatas(new ArrayList<DCP_TaxCategoryQueryRes.level1Elm>());
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
	protected String getQuerySql(DCP_TaxCategoryQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer();
		String eId = req.geteId();
	
		
		String keyTxt = null;
		String status = null;
		String taxType = null;
		if(req.getRequest()!=null)
		{
			keyTxt = req.getRequest().getKeyTxt();
			status = req.getRequest().getStatus();
			taxType = req.getRequest().getTaxType();
		}
		
		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();
		
		//計算起啟位置
		int startRow = ((pageNumber - 1) * pageSize);
		startRow = ((pageNumber - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
		startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料
	
		sqlbuf.append(" SELECT * FROM ("
				 + " SELECT  COUNT(DISTINCT a.taxCode ) OVER() NUM ,dense_rank() over(ORDER BY a.taxCode,a.taxArea) rn, a. *  FROM "
				 + " ( SELECT a.taxCode ,a.taxType , a.taxRate, a.inclTax , a.status, b.TAXNAME, b.lang_Type AS langType ,"
				 + "   a.EID,a.TAXAREA,a.TAXPROP,a.TAXCALTYPE,"
                 + "  a.CREATEOPID,e1.name as  CREATEOPNAME,to_char(a.CREATETIME,'yyyy-MM-dd HH:mm:ss') CREATETIME,a.LASTMODIOPID, e2.name as LASTMODIOPNAME,to_char(a.LASTMODITIME,'yyyy-MM-dd HH:mm:ss') LASTMODITIME,a.CREATEDEPTID,d.departname as CREATEDEPTNAME  FROM DCP_TAXCATEGORY a   "
				 + " LEFT JOIN DCP_TAXCATEGORY_lang b ON a.EID = b.EID AND a.taxCode = b.taxCode AND a.TAXAREA=b.TAXAREA " +
                " left join dcp_employee e1 on e1.employeeno=a.createopid and a.eid=e1.eid  " +
                " left join dcp_employee e2 on e2.employeeno=a.lastmodiopid and a.eid=e2.eid  " +
                " left join dcp_department_lang d on d.eid=a.eid and d.departno=a.createdeptid and d.lang_type='"+req.getLangType()+" '  "+

                "" +
                ")   a "
				 + " WHERE  EID = '"+eId+"'  " );
		
		if (keyTxt != null && keyTxt.length() > 0)
		{
			sqlbuf.append(" AND ( taxCode like '%%"+keyTxt+"%%' or taxName like '%%"+keyTxt+"%%' or taxArea like '%%"+keyTxt+"%%'   ) ");
		}
		if (status != null && status.length() > 0)
		{
			sqlbuf.append(" AND status = '"+status+"' ");
		}
		if (taxType != null && taxType.length() > 0)
		{
			sqlbuf.append(" AND taxType = '"+taxType+"' ");
		}
		sqlbuf.append( " ) WHERE rn > " + startRow + " AND rn <= " + (startRow+pageSize) + "  order by taxCode ");
		sql= sqlbuf.toString();
		return sql;
	}
	
}
