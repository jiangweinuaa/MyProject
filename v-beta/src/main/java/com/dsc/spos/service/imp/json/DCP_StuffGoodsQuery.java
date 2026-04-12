package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_StuffGoodsQueryReq;
import com.dsc.spos.json.cust.res.DCP_StuffGoodsQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import com.google.gson.reflect.TypeToken;

public class DCP_StuffGoodsQuery extends SPosBasicService<DCP_StuffGoodsQueryReq,DCP_StuffGoodsQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_StuffGoodsQueryReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
		}

		//String groupId = req.getRequest().getGroupId();

		/*if (Check.Null(groupId)) 
		{
			isFail = true;
			errMsg.append("口味分组编码不能为空 ");			
		}*/

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_StuffGoodsQueryReq> getRequestType() 
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_StuffGoodsQueryReq>() {};
	}

	@Override
	protected DCP_StuffGoodsQueryRes getResponseType() 
	{
		// TODO Auto-generated method stub
		return new DCP_StuffGoodsQueryRes();
	}

	@Override
	protected DCP_StuffGoodsQueryRes processJson(DCP_StuffGoodsQueryReq req) throws Exception 
	{
		String eId = req.geteId();
		String curLangType = req.getLangType();
		if(curLangType==null||curLangType.isEmpty())
		{
			curLangType = "zh_CN";
		}
		int totalRecords = 0; //总笔数
		int totalPages = 0;	
		DCP_StuffGoodsQueryRes res=this.getResponse();

		String sql=getQuerySql(req);
		List<Map<String , Object>> getData=this.doQueryData(sql, null);

		res.setDatas(new ArrayList<DCP_StuffGoodsQueryRes.level1Elm>());
		for (Map<String, Object> oneData : getData) 
		{
			String num = getData.get(0).get("NUM").toString();
			totalRecords=Integer.parseInt(num);

			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;	
			
			DCP_StuffGoodsQueryRes.level1Elm lv1=res.new level1Elm();
			String stuffId = oneData.get("STUFFID").toString();
			lv1.setStuffId(stuffId);
			lv1.setStuffName(oneData.get("STUFFNAME").toString());						
			lv1.setPluNo(oneData.get("PLUNO").toString());
			lv1.setPluName(oneData.get("PLU_NAME").toString());
			lv1.setStatus(oneData.get("STATUS").toString());
			res.getDatas().add(lv1);
			lv1=null;
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
	protected String getQuerySql(DCP_StuffGoodsQueryReq req) throws Exception 
	{
		String eId = req.geteId();
		String langtype = req.getLangType();
		String stuffId = req.getRequest().getStuffId();
		String status = req.getRequest().getStatus();
		String keyTxt = req.getRequest().getKeyTxt();

		String sql = null;
		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();

		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;	

		StringBuffer sqlbuf=new StringBuffer("select * from ( "
				+ " select  count(*) over() num, dense_rank() over( order by a.stuffid,a.pluno) rn, A.PLUNO,A.STUFFID,B.PLU_NAME stuffname,C.PLU_NAME,D.STATUS "
				+ " from dcp_stuff_goods A "
				+ " LEFT JOIN DCP_GOODS_LANG B ON A.EID=B.EID AND A.stuffid=B.pluno AND B.lang_type='"+langtype+"' "
				+ " LEFT JOIN DCP_GOODS_LANG C ON A.EID=C.EID AND A.pluno=C.PLUNO AND C.LANG_TYPE='"+langtype+"' "
				+ " LEFT JOIN DCP_GOODS  D ON A.EID=D.EID AND A.stuffid=D.PLUNO "
				+ "where a.eid='"+eId+"' "); 
		
		if(stuffId != null && stuffId.length() >0)
		{
			sqlbuf.append("and A.STUFFID='"+stuffId +"' ");
		}

		if(status != null && status.length() >0)
		{
			sqlbuf.append("and D.status='"+status +"' ");
		}
		
		if(keyTxt != null && keyTxt.length() >0)
		{
			sqlbuf.append("and (A.pluno like '%%"+keyTxt+"%%' or C.PLU_NAME like '%%"+keyTxt+"%%') ");
		}
		
		sqlbuf.append(" )  where rn>"+startRow+" and rn<="+(startRow+pageSize));

		sql = sqlbuf.toString();
		return sql;
	}
	
}
