package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_FlavorCategoryQueryReq;
import com.dsc.spos.json.cust.res.DCP_FlavorCategoryQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_FlavorCategoryQuery extends SPosBasicService<DCP_FlavorCategoryQueryReq,DCP_FlavorCategoryQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_FlavorCategoryQueryReq req) throws Exception 
	{		
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
		}

		String flavorId = req.getRequest().getFlavorId();

		/*if (Check.Null(flavorId)) 
		{
			isFail = true;
			errMsg.append("口味编码不能为空 ");			
		}*/

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_FlavorCategoryQueryReq> getRequestType() 
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_FlavorCategoryQueryReq>() {};
	}

	@Override
	protected DCP_FlavorCategoryQueryRes getResponseType() 
	{
		// TODO Auto-generated method stub
		return new DCP_FlavorCategoryQueryRes();
	}

	@Override
	protected DCP_FlavorCategoryQueryRes processJson(DCP_FlavorCategoryQueryReq req) throws Exception
	{
		DCP_FlavorCategoryQueryRes res=this.getResponse();

		int totalRecords = 0; //总笔数
		int totalPages = 0;	

		String sql=getQuerySql(req);
		List<Map<String , Object>> getData=this.doQueryData(sql, null);

		res.setDatas(new ArrayList<DCP_FlavorCategoryQueryRes.level1Elm>());

		if (getData!=null && getData.isEmpty()==false) 
		{
			String num = getData.get(0).get("NUM").toString();
			totalRecords=Integer.parseInt(num);

			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;	

			for (Map<String, Object> oneData : getData) 
			{
				DCP_FlavorCategoryQueryRes.level1Elm lv1=res.new level1Elm();

				lv1.setCategory(oneData.get("CATEGORY").toString());
				lv1.setCategoryName(oneData.get("CATEGORY_NAME").toString());
				lv1.setFlavorId(oneData.get("FLAVORID").toString());
				lv1.setFlavorName(oneData.get("FLAVORNAME").toString());
				lv1.setStatus(oneData.get("STATUS").toString());

				res.getDatas().add(lv1);
				lv1=null;
			}
		}

		res.setPageNumber(req.getPageNumber());
		res.setPageSize(req.getPageSize());
		res.setTotalRecords(totalRecords);
		res.setTotalPages(totalPages);

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");

		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_FlavorCategoryQueryReq req) throws Exception 
	{
		String eId = req.geteId();
		String langtype = req.getLangType();
		String flavorId = req.getRequest().getFlavorId();
		String status = req.getRequest().getStatus();
		String keyTxt = req.getRequest().getKeyTxt();

		String sql = null;

		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();

		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;		


		StringBuffer sqlbuf=new StringBuffer("select * from ( "
				+ "select count(*) over() num, rownum rn,a.flavorid,b.flavorname,a.category,c.status,d.category_name "
				+ "from dcp_flavor_category a "
				+ "left join dcp_flavor_lang b on a.eid=b.eid and a.flavorid=b.flavorid and b.lang_type='"+langtype+"' "
				+ "left join dcp_category c on a.eid=c.eid and a.category=c.category "
				+ "left join dcp_category_lang d on a.eid=d.eid and a.category=d.category and d.lang_type='"+langtype+"' "
				+ "where a.eid='"+eId+"' "); 

		if(flavorId != null && flavorId.length() >0)
		{
			sqlbuf.append("and a.flavorid='"+flavorId +"' ");
		}

		if(status != null && status.length() >0)
		{
			sqlbuf.append("and c.status="+status +" ");
		}
		
		if(keyTxt != null && keyTxt.length() >0)
		{
			sqlbuf.append("and (a.category like '%%"+keyTxt+"%%' or d.category_name like '%%"+keyTxt+"%%') ");
		}

		sqlbuf.append(" ) where rn>"+startRow+" and rn<="+(startRow+pageSize));

		sql = sqlbuf.toString();
		return sql;
	}



}
