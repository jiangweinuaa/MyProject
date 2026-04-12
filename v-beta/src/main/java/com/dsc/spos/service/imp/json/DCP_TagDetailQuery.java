package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_TagDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_TagDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_TagDetailQuery extends SPosBasicService<DCP_TagDetailQueryReq,DCP_TagDetailQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_TagDetailQueryReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
		}

		String status= req.getRequest().getStatus();
		String tagGroupType= req.getRequest().getTagGroupType();
		String tagNo= req.getRequest().getTagNo();	

		if (Check.Null(tagGroupType)) 
		{
			errMsg.append("标签类型不能为空值 ");
			isFail = true;
		}
		if (Check.Null(tagNo)) 
		{
			errMsg.append("标签编码不能为空值 ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_TagDetailQueryReq> getRequestType()
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_TagDetailQueryReq>() {};
	}

	@Override
	protected DCP_TagDetailQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_TagDetailQueryRes();
	}

	@Override
	protected DCP_TagDetailQueryRes processJson(DCP_TagDetailQueryReq req) throws Exception 
	{
		String eId=req.geteId();
		String langtype=req.getLangType();
		String status= req.getRequest().getStatus();
		String tagGroupType= req.getRequest().getTagGroupType();
		String tagNo= req.getRequest().getTagNo();	

		DCP_TagDetailQueryRes res=this.getResponse();

		int totalRecords = 0; //总笔数
		int totalPages = 0;	

		String sql=getQuerySql(req);
		List<Map<String , Object>> getData=this.doQueryData(sql, null);

		res.setDatas(new ArrayList<DCP_TagDetailQueryRes.level1Elm>());

		if (getData!=null && getData.isEmpty()==false) 
		{
			String num = getData.get(0).get("NUM").toString();
			totalRecords=Integer.parseInt(num);

			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;	

			for (Map<String, Object> oneData : getData) 
			{
				DCP_TagDetailQueryRes.level1Elm lv1=res.new level1Elm();

				lv1.setId(oneData.get("ID").toString());
				lv1.setName(oneData.get("NAME").toString());
				lv1.setStatus(oneData.get("STATUS").toString());
				lv1.setTagGroupType(oneData.get("TAGGROUPTYPE").toString());
				lv1.setTagName(oneData.get("TAGNAME").toString());
				lv1.setTagNo(oneData.get("TAGNO").toString());

				res.getDatas().add(lv1);
				lv1=null;
			}
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
	protected String getQuerySql(DCP_TagDetailQueryReq req) throws Exception 
	{
		String eId = req.geteId();
		String langtype = req.getLangType();
		String status= req.getRequest().getStatus();
		String tagGroupType= req.getRequest().getTagGroupType();
		String tagNo= req.getRequest().getTagNo();	

		String sql = null;

		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();

		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;		


		StringBuffer sqlbuf=new StringBuffer(""); 
		sqlbuf.append("select * from ( "
				+ "select count(*) over() num, rownum rn,a.taggrouptype,a.tagno,b.tagname,c.id,c.name,d.status   "
				+ "from dcp_tagtype a "
				+ "left join dcp_tagtype_lang b on a.eid=b.eid and a.taggrouptype=b.taggrouptype and a.taggroupno=b.taggroupno and a.tagno=b.tagno and b.lang_type='"+langtype+"' "
				+ "inner join dcp_tagtype_detail c on a.eid=c.eid and a.taggrouptype=c.taggrouptype and a.taggroupno=c.taggroupno and a.tagno=c.tagno ");

		if (tagGroupType.equals("SHOP"))//门店标签 
		{
			sqlbuf.append("left join dcp_org d on c.eid=d.eid and c.id=d.organizationno ");
		}
		else if (tagGroupType.equals("CUST"))//客户标签
		{
			//sqlbuf.append("left join dcp_customer d on c.eid=d.eid and c.id=d.customer  ");
			sqlbuf.append("left join DCP_BIZPARTNER d on c.eid=d.eid and c.id=d.BIZPARTNERNO  ");
		}
		else //一堆商品标签
		{
			sqlbuf.append("left join dcp_goods d on c.eid=d.eid and c.id=d.pluno  ");
		}

		sqlbuf.append("where a.eid='"+eId+"' ");

		if(tagGroupType != null && tagGroupType.length() >0 )
		{
			sqlbuf.append("AND a.taggrouptype='"+tagGroupType+"' ");
		}

		if(tagNo != null && tagNo.length() >0 )
		{
			sqlbuf.append("AND a.tagno='"+tagNo+"' ");
		}

		if(status != null && status.length() >0 )
		{
			sqlbuf.append("AND a.status="+status+" ");
		}

		sqlbuf.append(" ) where rn>"+startRow+" and rn<="+(startRow+pageSize));


		sql = sqlbuf.toString();
		return sql;
	}




}
