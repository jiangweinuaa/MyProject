package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_ReqCustomerQueryReq;
import com.dsc.spos.json.cust.res.DCP_ReqCustomerQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_ReqCustomerQuery extends SPosBasicService<DCP_ReqCustomerQueryReq, DCP_ReqCustomerQueryRes> 
{
	@Override
	protected boolean isVerifyFail(DCP_ReqCustomerQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return false;
	}

	@Override
	protected TypeToken<DCP_ReqCustomerQueryReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_ReqCustomerQueryReq>(){};
	}

	@Override
	protected DCP_ReqCustomerQueryRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_ReqCustomerQueryRes();
	}

	@Override
	protected DCP_ReqCustomerQueryRes processJson(DCP_ReqCustomerQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	
		String sql=null;
		//查询条件
		String eId = req.geteId();		
		DCP_ReqCustomerQueryRes res=null;
		res=this.getResponse();
		
		//给分页字段赋值
		sql = this.getQuerySql_Count(req);			//查询总笔数
    
		String[] conditionValues_Count = {eId};			//查詢條件
		List<Map<String, Object>> getQData_Count = this.doQueryData(sql, conditionValues_Count);
		int totalRecords;								//总笔数
		int totalPages;									//总页数
		if (getQData_Count != null && getQData_Count.isEmpty() == false)
		{ 
			Map<String, Object> oneData_Count = getQData_Count.get(0);
			String num = oneData_Count.get("NUM").toString();
			totalRecords=Integer.parseInt(num);
			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
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
	  
		sql=null;
		sql=this.getQuerySql(req);	
		List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, null);	
		if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
		{
			//单头主键字段
			Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
			condition.put("A.CUSTOMER_ID", true);		
			//调用过滤函数
			List<Map<String, Object>> getQHeader=MapDistinct.getMap(getQDataDetail, condition);
			res.setDatas(new ArrayList<DCP_ReqCustomerQueryRes.level1Elm>());
			for (Map<String, Object> oneData : getQHeader) 
			{
				DCP_ReqCustomerQueryRes.level1Elm oneLv1=res.new level1Elm();
				oneLv1.setDatas(new ArrayList<DCP_ReqCustomerQueryRes.level2Elm>());
				String customerID=oneData.get("A.CUSTOMER_ID").toString();
				
				//region 组装返回数据
				oneLv1.setCustomerID(customerID);
				oneLv1.setCustomerName(oneData.get("CUSTOMER_NAME").toString());
				oneLv1.setAbbr(oneData.get("ABBR").toString());
				oneLv1.setSaler(oneData.get("SALER").toString());
				oneLv1.setAbbrNO(oneData.get("ABBRNO").toString());
				oneLv1.setTaxNO(oneData.get("TAXNO").toString());
				oneLv1.setDistrict(oneData.get("DISTRICT").toString());
				oneLv1.setRegisterAmt(oneData.get("REGISTER_AMT").toString());
				oneLv1.setAnnualAmt(oneData.get("ANNUAL_AMT").toString());
				oneLv1.setOpenDate(oneData.get("OPEN_DATE").toString());
				oneLv1.setStaffsQty(oneData.get("STAFFS_QTY").toString());
				oneLv1.setLegalPerson(oneData.get("LEGAL_PERSON").toString());
				oneLv1.setAccountPerson(oneData.get("ACCOUNT_PERSON").toString());
				oneLv1.setAccountTelephone(oneData.get("ACCOUNT_TELEPHONE").toString());
				oneLv1.seteMail(oneData.get("EMAIL").toString());
				oneLv1.setFax(oneData.get("FAX").toString());
				oneLv1.setMemo(oneData.get("MEMO").toString());
				oneLv1.setUrl(oneData.get("URL").toString());
				oneLv1.setOfficeAddress(oneData.get("OFFICE_ADDRESS").toString());
				oneLv1.setDeliveryAddress(oneData.get("DELIVERY_ADDRESS").toString());
				oneLv1.setInvoiceAddress(oneData.get("INVOICE_ADDRESS").toString());
				oneLv1.setTelephone(oneData.get("TELEPHONE").toString());
				oneLv1.setAddress(oneData.get("ADDRESS").toString());
				oneLv1.setLinkPerson(oneData.get("LINK_PERSON").toString());
				oneLv1.setCreditType(oneData.get("CREDIT_TYPE").toString());
				oneLv1.setCreditAmt(oneData.get("CREDIT_AMT").toString());
				oneLv1.setPayCustomer(oneData.get("PAY_CUSTOMER").toString());
				oneLv1.setCollectObject(oneData.get("COLLECT_OBJECT").toString());
				oneLv1.setCollectShop(oneData.get("COLLECT_SHOP").toString());
				oneLv1.setPayType(oneData.get("PAY_TYPE").toString());
				oneLv1.setPayDay(oneData.get("PAY_DAY").toString());
				oneLv1.setPayMonth(oneData.get("PAY_MONTH").toString());
				oneLv1.setCreditMonth(oneData.get("CREDIT_MONTH").toString());
				oneLv1.setCreditDay(oneData.get("CREDIT_DAY").toString());
				oneLv1.setIsCretail(oneData.get("IS_CRETAIL").toString());
				oneLv1.setIsCorder(oneData.get("IS_CORDER").toString());
				oneLv1.setStatus(oneData.get("STATUS").toString());
				//endregion
				for (Map<String, Object> oneData2 : getQDataDetail) 
				{
					if(customerID.equals(oneData2.get("A.CUSTOMER_ID").toString())==false)
						continue;
					DCP_ReqCustomerQueryRes.level2Elm oneLv2=res.new level2Elm();
					oneLv2.setShopId(oneData2.get("SHOPID").toString());
					oneLv2.setShopName(oneData2.get("SHOP_NAME").toString());
					//添加单身
					oneLv1.getDatas().add(oneLv2);
					oneLv2=null;
				}
				//添加单头
				res.getDatas().add(oneLv1);
				oneLv1=null;
			}
		}
		else 
		{
		 res.setDatas(new ArrayList<DCP_ReqCustomerQueryRes.level1Elm>());
	  }
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
	
	}

	@Override
	protected String getQuerySql(DCP_ReqCustomerQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql=null;
		//查询条件
		String eId = req.geteId();;
		String langType = req.getLangType();
		
		String keyTxt = req.getKeyTxt();
		String status=req.getStatus();
		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();
	  
		//計算起啟位置
		int startRow = ((pageNumber - 1) * pageSize);
		startRow = ((pageNumber - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
		startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料
		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append("select a.CUSTOMER_ID,CUSTOMER_NAME,ABBR,SALER,ABBRNO,TAXNO,DISTRICT,REGISTER_AMT,ANNUAL_AMT,OPEN_DATE,STAFFS_QTY,LEGAL_PERSON,ACCOUNT_PERSON,ACCOUNT_TELEPHONE,EMAIL,FAX,MEMO,URL,OFFICE_ADDRESS,"
                   +"DELIVERY_ADDRESS,INVOICE_ADDRESS,TELEPHONE,ADDRESS,LINK_PERSON,CREDIT_TYPE,CREDIT_AMT,PAY_CUSTOMER,COLLECT_OBJECT,COLLECT_SHOP,"
                   +"PAY_TYPE,PAY_DAY,PAY_MONTH,CREDIT_MONTH,CREDIT_DAY,IS_CRETAIL,IS_CORDER,STATUS,b.SHOPID,c.SHOPNAME"
                   +" from TA_REQCUSTOMER a inner join TA_REQCUSTOMER_SHOP b on a.EID=b.EID and a.customer_id=b.customer_id"
                   +" inner join "
                   + "("
                   + "SELECT A.EID,A.ORGANIZATIONNO SHOPID,B.ORG_NAME SHOPNAME,B.LANG_TYPE FROM DCP_ORG A "
                   + "LEFT JOIN DCP_ORG_LANG B ON A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO AND B.LANG_TYPE='"+langType+"' AND B.status='100' "
                   + "WHERE A.EID='"+req.geteId()+"' AND A.ORG_FORM='2' AND A.status='100' "
                   + ") c on b.EID=c.EID and b.SHOPID=c.SHOPID and c.lang_type='"+langType+"'"
                   +" where a.EID='"+eId+"'"
				            );
		if (status != null && status.length()>0)
		{
			sqlbuf.append(" and a.STATUS='"+status+"'");
		}	
		if (keyTxt != null && keyTxt.length()>0)
		{
			sqlbuf.append(" and(a.CUSTOMER_NAME like '%%"+ keyTxt +"%%' or a.ABBR like '%%"+ keyTxt +"%%' or a.ABBRNO like '%%"+ keyTxt +"%%')");
		}
		
		sqlbuf.append(" and a.customer_id in ( select customer_id  from ("
				+"select rn,customer_id from ("
				+"select rownum rn,customer_id from ("
				+"select customer_id from TA_REQCUSTOMER where EID='"+eId+"'"		
				);
		if (status != null && status.length()>0)
		{
			sqlbuf.append(" and STATUS='"+status+"'");
		}
		if (keyTxt != null && keyTxt.length()>0)
		{
			sqlbuf.append(" and(CUSTOMER_NAME like '%%"+ keyTxt +"%%' or ABBR like '%%"+ keyTxt +"%%' or ABBRNO like '%%"+ keyTxt +"%%')");
		}
		sqlbuf.append(" order by status asc,modify_date desc,create_date desc)");
		sqlbuf.append(") where rn>" + startRow + " AND rn <= " + (startRow+pageSize) + "");
		sqlbuf.append(")");
		sqlbuf.append(")");
		sql=sqlbuf.toString();
		return sql;
	}
	
	protected String getQuerySql_Count(DCP_ReqCustomerQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		
		sqlbuf.append("select NUM from (SELECT COUNT(*) NUM FROM TA_REQCUSTOMER WHERE EID=?");

		String keyTxt = req.getKeyTxt();
		String status=req.getStatus();
		if (status != null && status.length()>0)
		{
			sqlbuf.append(" and STATUS='"+status+"'");
		}
		if (keyTxt != null && keyTxt.length()>0)
		{
			sqlbuf.append(" and(CUSTOMER_NAME like '%%"+ keyTxt +"%%' or ABBR like '%%"+ keyTxt +"%%' or ABBRNO like '%%"+ keyTxt +"%%')");
		}
		sqlbuf.append(")");
		sql=sqlbuf.toString();
		
		return sql;
	}	
}
