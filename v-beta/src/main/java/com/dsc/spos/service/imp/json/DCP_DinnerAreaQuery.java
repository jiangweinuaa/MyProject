package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_DinnerAreaQueryReq;
import com.dsc.spos.json.cust.res.DCP_DinnerAreaQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;

public class DCP_DinnerAreaQuery extends SPosBasicService<DCP_DinnerAreaQueryReq,DCP_DinnerAreaQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_DinnerAreaQueryReq req) throws Exception 
	{
		
		return false;
	}

	@Override
	protected TypeToken<DCP_DinnerAreaQueryReq> getRequestType() 
	{
		return new TypeToken<DCP_DinnerAreaQueryReq>(){};
	}

	@Override
	protected DCP_DinnerAreaQueryRes getResponseType() 
	{
		return new DCP_DinnerAreaQueryRes();
	}

	@Override
	protected DCP_DinnerAreaQueryRes processJson(DCP_DinnerAreaQueryReq req) throws Exception
	{
		String sql = null;		
		
		//查詢資料
		DCP_DinnerAreaQueryRes res = null;
		res = this.getResponse();

		boolean bshopId = false; // ShopList 传值时才返回 shopID
		if(!CollectionUtils.isEmpty(req.getRequest().getShopList())){
			bshopId = true;
		}
		//单头总数
		sql = this.getQuerySql(req);			
		
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		
		int totalRecords;								//总笔数
		int totalPages;									//总页数
		res.setDatas(new ArrayList<DCP_DinnerAreaQueryRes.level1Elm>());
		if (getQData != null && getQData.isEmpty() == false) 
		{
			
			String num = getQData.get(0).get("NUM").toString();
			totalRecords=Integer.parseInt(num);

			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;			
			
			for (Map<String, Object> oneData : getQData) 
			{
				
				DCP_DinnerAreaQueryRes.level1Elm oneLv1 = new DCP_DinnerAreaQueryRes().new level1Elm();
				oneLv1.setDinnerGroup(oneData.get("DINNERGROUP").toString());
				oneLv1.setDinnerGroupName(oneData.get("DINNERGROUPNAME").toString());
				oneLv1.setStatus(oneData.get("STATUS").toString());
				oneLv1.setUpdateTime(oneData.get("UPDATE_TIME").toString());
				oneLv1.setPriority(oneData.get("PRIORITY").toString());
				oneLv1.setGroupType(oneData.get("GROUPTYPE").toString());
				oneLv1.setRestrictGroup(oneData.get("RESTRICTGROUP").toString());
				if(bshopId){
					oneLv1.setShopId(oneData.get("ORGANIZATIONNO").toString());
				}
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
	protected String getQuerySql(DCP_DinnerAreaQueryReq req) throws Exception 
	{
		String sql=null;

		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();

		//分页起始位置
		int startRow=(pageNumber-1) * pageSize;

		String status=req.getRequest().getStatus();
		String keyTxt=req.getRequest().getKeyTxt();
        List<String> shopList = req.getRequest().getShopList();

        StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append("select * from "
				+ "("
				+ "select count(*) over() num,DINNERGROUP,DINNERGROUPNAME,status,priority ,ORGANIZATIONNO,UPDATE_TIME,GROUPTYPE,RESTRICTGROUP,row_number() over (order by DINNERGROUP) rn from  DCP_DINNER_AREA where EID='"+req.geteId()+"' ");

		if(CollectionUtils.isEmpty(shopList)){

			sqlbuf.append("AND ORGANIZATIONNO='"+req.getShopId()+"'");
		}
		if(status!=null && status.length()>0)
		{
			sqlbuf.append(" and status='" + status + "' ");
		}
		if (keyTxt != null && keyTxt.length()>0)
		{
			sqlbuf.append(" AND (DINNERGROUP LIKE '%%"+ keyTxt +"%%' OR DINNERGROUPNAME LIKE '%%"+ keyTxt +"%%' ) ");
		}
		if(!CollectionUtils.isEmpty(shopList)){
			sqlbuf.append("AND ORGANIZATIONNO IN (");
            shopList.forEach(shop->{
				sqlbuf.append("'"+shop+"',");
			});
			sqlbuf.deleteCharAt(sqlbuf.length() - 1);
			sqlbuf.append(")");
        }

		sqlbuf.append(" ) ");
		sqlbuf.append(" where rn>"+startRow+" and rn<="+(startRow+pageSize) + "  order by ORGANIZATIONNO,priority  " );

		sql=sqlbuf.toString();
		return sql;
	}

}
