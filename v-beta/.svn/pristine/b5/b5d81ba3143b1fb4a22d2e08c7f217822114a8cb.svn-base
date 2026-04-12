package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_ShopInfoQueryReq;
import com.dsc.spos.json.cust.res.DCP_ShopInfoQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;

public class DCP_ShopInfoQuery extends SPosBasicService<DCP_ShopInfoQueryReq, DCP_ShopInfoQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_ShopInfoQueryReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_ShopInfoQueryReq> getRequestType() 
	{
		return new TypeToken<DCP_ShopInfoQueryReq>(){};
	}

	@Override
	protected DCP_ShopInfoQueryRes getResponseType() 
	{
		return new DCP_ShopInfoQueryRes();
	}

	@Override
	protected DCP_ShopInfoQueryRes processJson(DCP_ShopInfoQueryReq req) throws Exception 
	{
		String sql = null;		
		
		//查詢資料
		DCP_ShopInfoQueryRes res = null;
		res = this.getResponse();
		//单头总数
		sql = this.getQuerySql(req);			
		
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		
		int totalRecords;								//总笔数
		int totalPages;									//总页数
		res.setDatas(new ArrayList<DCP_ShopInfoQueryRes.level1Elm>());
		if (getQData != null && getQData.isEmpty() == false) 
		{
			
			String num = getQData.get(0).get("NUM").toString();
			totalRecords=Integer.parseInt(num);

			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;			
			
			for (Map<String, Object> oneData : getQData) 
			{
				
				DCP_ShopInfoQueryRes.level1Elm oneLv1 = new DCP_ShopInfoQueryRes().new level1Elm();
				oneLv1.setShopId(oneData.get("SHOPID").toString());
				oneLv1.setShopName(oneData.get("SHOPNAME").toString());
				oneLv1.setPhone(oneData.get("PHONE").toString());
				oneLv1.setAddress(oneData.get("ADDRESS").toString());
				oneLv1.setSellerGuiNo(oneData.get("SELLERGUINO").toString());
                oneLv1.setProvince(oneData.get("PROVINCE").toString());
                oneLv1.setCity(oneData.get("CITY").toString());
                oneLv1.setCounty(oneData.get("COUNTY").toString());
                oneLv1.setLatitude(oneData.get("LATITUDE").toString());
                oneLv1.setLongitude(oneData.get("LONGITUDE").toString());
				res.getDatas().add(oneLv1);
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
	protected void processRow(Map<String, Object> row) throws Exception 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_ShopInfoQueryReq req) throws Exception 
	{
		String sql=null;

		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();

		//分页起始位置
		int startRow=(pageNumber-1) * pageSize;

		String keyTxt=req.getRequest().getKeyTxt();

		String eId=req.geteId();
		if(eId==null||eId.isEmpty())
		{
			eId="99";
		}
		String langtype=req.getLangType();
		if(langtype==null||langtype.isEmpty())
		{
			langtype="zh_CN";
		}
		
		String getOrgForm=req.getRequest().getGetOrgForm();
		String sqlorgformString="  AND A.ORG_FORM='2' "; 
		if(getOrgForm!=null&&!getOrgForm.isEmpty() &&getOrgForm.equals("1") )
		{
			sqlorgformString="";
		}
		if(getOrgForm!=null&&!getOrgForm.isEmpty()&&getOrgForm.equals("2") )
		{
			sqlorgformString="  AND A.ORG_FORM='2' "; 
		}
		if(getOrgForm!=null&&!getOrgForm.isEmpty()&&getOrgForm.equals("3") )
		{
			sqlorgformString="  AND A.ORG_FORM='0' "; 
		}
		
		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append("select * from "
				+ "("
				+ "SELECT row_number() over(ORDER BY A.Organizationno) rn, count(*) over() num, A.EID,A.ORGANIZATIONNO SHOPID,B.ORG_NAME SHOPNAME,A.PHONE,A.ADDRESS,A.SELLERGUINO,A.PROVINCE,A.CITY,A.COUNTY,A.LATITUDE,A.LONGITUDE FROM DCP_ORG A "
				+ "LEFT JOIN DCP_ORG_LANG B ON A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO AND B.LANG_TYPE='"+langtype+"' AND B.status='100'"
				+ "WHERE A.EID='"+eId+"' "+sqlorgformString+" AND A.status='100'  ");

		if (keyTxt != null && keyTxt.length()>0)
		{
			sqlbuf.append(" AND (A.ORGANIZATIONNO LIKE '%%"+ keyTxt +"%%' OR B.ORG_NAME LIKE '%%"+ keyTxt +"%%' ) ");
		}

		sqlbuf.append(" ) ");
		sqlbuf.append(" where rn>"+startRow+" and rn<="+(startRow+pageSize));

		sql=sqlbuf.toString();
		return sql;
	}
	
	
	

}
