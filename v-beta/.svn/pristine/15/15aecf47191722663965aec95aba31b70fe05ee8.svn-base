package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.scannotation.archiveiterator.IteratorFactory;

import com.dsc.spos.json.cust.req.DCP_TagQueryReq;
import com.dsc.spos.json.cust.res.DCP_TagQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.MyCommon;
import com.google.gson.reflect.TypeToken;

import eleme.openapi.sdk.api.enumeration.finance.init;

public class DCP_TagQuery extends SPosBasicService<DCP_TagQueryReq,DCP_TagQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_TagQueryReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_TagQueryReq> getRequestType() 
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_TagQueryReq>() {};
	}

	@Override
	protected DCP_TagQueryRes getResponseType() 
	{
		// TODO Auto-generated method stub
		return new DCP_TagQueryRes();
	}

	@Override
	protected DCP_TagQueryRes processJson(DCP_TagQueryReq req) throws Exception 
	{
		String eId = req.geteId();		
		String ketTxt = req.getRequest().getKeyTxt();
		String tagGrouptype = req.getRequest().getTagGroupType();
		String tagGroupno = req.getRequest().getTagGroupNo();
		
		DCP_TagQueryRes res = this.getResponse();
		String curLangType = req.getLangType();
		if(curLangType==null||curLangType.isEmpty())
		{
			curLangType = "zh_CN";
		}
		res.setDatas(new ArrayList<DCP_TagQueryRes.level1Elm>());
		

		String sql=getQuerySql(req);			
		List<Map<String , Object>> getData_Detail=this.doQueryData(sql, null);
		sql ="";
		sql = " select * from ( select count(*) num,TAGGROUPTYPE,TAGNO from dcp_tagtype_detail "
				+ "where eid='"+eId+"' ";
		if(tagGrouptype != null && tagGrouptype.length() >0)
		{
			sql +=" and taggrouptype='"+tagGrouptype +"' ";
		}

		if(tagGroupno != null && tagGroupno.length() >0)
		{
			sql +=" and taggroupno='"+tagGroupno +"' ";
		}
		
		sql += " group by TAGGROUPTYPE,TAGNO )";
		
		if(getData_Detail!=null&&getData_Detail.isEmpty()==false)
		{
			List<Map<String , Object>> getSubDetailCount=this.doQueryData(sql, null);
			Map<String, Boolean> condV = new HashMap<String, Boolean>(); //查詢條件
			condV.put("TAGGROUPTYPE", true);	
			condV.put("TAGNO", true);	
			List<Map<String, Object>> tagList= MapDistinct.getMap(getData_Detail, condV);
			
			condV.clear(); //查詢條件
			condV.put("TAGGROUPTYPE", true);	
			condV.put("TAGNO", true);
			condV.put("LANG_TYPE", true);
			List<Map<String, Object>> tagGroup_LangList= MapDistinct.getMap(getData_Detail, condV);

			for (Map<String, Object> oneData : tagList) 
			{
				DCP_TagQueryRes.level1Elm lv1=res.new level1Elm();
				String tagGroupType = oneData.get("TAGGROUPTYPE").toString();
				String tagNo = oneData.get("TAGNO").toString();
				
				lv1.setMemo(oneData.get("MEMO").toString());
				lv1.setSortId(oneData.get("SORTID").toString());				
				lv1.setStatus(oneData.get("STATUS").toString());				
				lv1.setTagGroupNo(oneData.get("TAGGROUPNO").toString());
				lv1.setTagGroupType(tagGroupType);
				lv1.setTagName(oneData.get("TAGNAME").toString());
				lv1.setTagNo(tagNo);
				lv1.setIsSingleProduce(oneData.get("ISSINGLEPRODUCE").toString());

				//
				lv1.setTagName_lang(new ArrayList<DCP_TagQueryRes.levelTagTypeLang>());
				for (Map<String, Object> map : tagGroup_LangList) 
				{
					String langType = map.get("LANG_TYPE").toString();
					String name = map.get("TAGNAME").toString();
					if(langType==null||langType.isEmpty())
					{
						continue;
					}
					String tagGroupType_detail = map.get("TAGGROUPTYPE").toString();
					String tagNo_detail = map.get("TAGNO").toString();
					if(tagGroupType_detail.equals(tagGroupType)&&tagNo_detail.equals(tagNo))
					{
						DCP_TagQueryRes.levelTagTypeLang tagtype_lang=res.new levelTagTypeLang();
						tagtype_lang.setLangType(langType);
						tagtype_lang.setName(name);
						lv1.getTagName_lang().add(tagtype_lang);
						
						if(curLangType.equals(langType))
						{
							lv1.setTagName(name);
						}
						tagtype_lang=null;
					}
					
				}							
								
				lv1.setSubDataCount("0");
				
				if(getSubDetailCount!=null&&getSubDetailCount.isEmpty()==false)
				{
					for (Map<String, Object> subDetail : getSubDetailCount)
					{
						String tagGroupType_detail = subDetail.get("TAGGROUPTYPE").toString();
						String tagNo_detail = subDetail.get("TAGNO").toString();
						if(tagGroupType_detail.equals(tagGroupType)&&tagNo_detail.equals(tagNo_detail))
						{
							lv1.setSubDataCount(subDetail.get("NUM").toString());
						}
					}
					
				}
				

				res.getDatas().add(lv1);
				lv1=null;
			}
			
			
		}

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");

		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub

	}

	private String getQuerySql_Detail(DCP_TagQueryReq req,String withasSql) throws Exception 
	{
		String eId = req.geteId();

		String sql = null;
		StringBuffer sqlbuf=new StringBuffer(""
				+ "with p1 AS ( "+withasSql+" ) "
				+ "SELECT a.taggrouptype, a.taggroupno,a.sortid,a.tagno,a.memo,a.status,b.tagname,b.lang_type,c.taggroupno C_TAGGROUPNO,c.tagno C_TAGNO,c.id C_ID,c.name C_NAME "
				+ "FROM p1 "
				+ "inner join dcp_tagtype a on a.taggrouptype=p1.taggrouptype and a.tagno=p1.tagno "
				+ "left  join dcp_tagtype_lang b on a.eid=b.eid and a.taggrouptype = b.taggrouptype and a.tagno = b.tagno "
				+ "left  join dcp_tagtype_detail c on a.eid=c.eid and a.taggrouptype = c.taggrouptype and a.tagno = c.tagno "
				+ "where a.eid='"+eId+"' "
				+ "order by a.tagno ");

		sql = sqlbuf.toString();
		return sql;		
	}

	@Override
	protected String getQuerySql(DCP_TagQueryReq req) throws Exception 
	{
		String eId = req.geteId();
		String langtype = req.getLangType();
		String ketTxt = req.getRequest().getKeyTxt();
		String tagGrouptype = req.getRequest().getTagGroupType();
		String tagGroupno = req.getRequest().getTagGroupNo();
	

		String sql = null;
		StringBuffer sqlbuf=new StringBuffer(""
				+ "select * from ("		
				+ " SELECT a.taggrouptype, a.taggroupno,a.sortid,a.tagno,a.memo,a.status,b.tagname,b.lang_type,a.ISSINGLEPRODUCE"
				+ " from dcp_tagtype a "
				+ " left  join dcp_tagtype_lang b on a.eid=b.eid and a.taggrouptype = b.taggrouptype and a.tagno = b.tagno "
				
				+ "where a.eid='"+eId+"' "); 

		if(tagGrouptype != null && tagGrouptype.length() >0)
		{
			sqlbuf.append("and a.taggrouptype='"+tagGrouptype +"' ");
		}

		if(tagGroupno != null && tagGroupno.length() >0)
		{
			sqlbuf.append("and a.taggroupno='"+tagGroupno +"' ");
		}

		if(ketTxt != null && ketTxt.length() >0)
		{
			sqlbuf.append("and (b.tagno like '%%"+ketTxt+"%%' or b.tagname like '%%"+ketTxt+"%%') ");
		}

		sqlbuf.append(" ORDER BY a.tagno ) ");


		sql = sqlbuf.toString();
		return sql;
	}

}
