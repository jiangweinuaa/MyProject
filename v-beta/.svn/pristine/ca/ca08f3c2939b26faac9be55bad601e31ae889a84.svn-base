package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_AttributionValueQueryReq;
import com.dsc.spos.json.cust.res.DCP_AttributionValueQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;


public class DCP_AttributionValueQuery extends SPosBasicService<DCP_AttributionValueQueryReq,DCP_AttributionValueQueryRes>{

	@Override
	protected boolean isVerifyFail(DCP_AttributionValueQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return false;
	}

	@Override
	protected TypeToken<DCP_AttributionValueQueryReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_AttributionValueQueryReq>(){};
	}

	@Override
	protected DCP_AttributionValueQueryRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_AttributionValueQueryRes();
	}

	@Override
	protected DCP_AttributionValueQueryRes processJson(DCP_AttributionValueQueryReq req) throws Exception {
	// TODO Auto-generated method stub
    String sql = null;		
		
		//查詢資料
    DCP_AttributionValueQueryRes res = null;
		res = this.getResponse();
	//获取当前登陆用户的语言类型
			String langType_cur = req.getLangType();
		//单头总数
		sql = this.getQuerySql(req);
    List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		
		int totalRecords;								//总笔数
		int totalPages;	
		res.setDatas(new ArrayList<DCP_AttributionValueQueryRes.level1Elm>());
		if (getQData != null && getQData.isEmpty() == false) 
		{
			
			/*String num = getQData.get(0).get("NUM").toString();
			totalRecords=Integer.parseInt(num);

			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;	
			
			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);*/
			
			//规格
			Map<String, Boolean> condition_attrValue = new HashMap<String, Boolean>(); //查詢條件	
			condition_attrValue.put("ATTRID", true);
			condition_attrValue.put("ATTRVALUEID", true);
		  //调用过滤函数
			List<Map<String, Object>> attrValueDatas=MapDistinct.getMap(getQData, condition_attrValue);
			
		  //规格多语言
			Map<String, Boolean> condition_attrValueLang = new HashMap<String, Boolean>(); //查詢條件
			condition_attrValueLang.put("ATTRID", true);
			condition_attrValueLang.put("ATTRVALUEID", true);		
			condition_attrValueLang.put("LANGTYPE", true);		
			//调用过滤函数
			List<Map<String, Object>> attrValueLangDatas=MapDistinct.getMap(getQData, condition_attrValueLang);
			
				
			/*//属性分组
			Map<String, Boolean> condition_attrGroup = new HashMap<String, Boolean>(); //查詢條件
			condition_attrGroup.put("ATTRID", true);	
			condition_attrGroup.put("ATTRGROUPID", true);
			//调用过滤函数
			List<Map<String, Object>> attrGroupDatas=MapDistinct.getMap(getQData, condition_attrGroup);*/
					
			res.setDatas(new ArrayList<DCP_AttributionValueQueryRes.level1Elm>());
			
			for (Map<String, Object> oneData : attrValueDatas) 
			{
				DCP_AttributionValueQueryRes.level1Elm oneLv1 = res.new level1Elm();
				String attrValueId = oneData.get("ATTRVALUEID").toString();			
				String attrId = oneData.get("ATTRID").toString();	
				String sortId = oneData.get("SORTID").toString();	
	
				oneLv1.setAttrId(attrId);
				oneLv1.setSortId(sortId);
				oneLv1.setStatus(oneData.get("STATUS").toString());			
				oneLv1.setAttrName(oneData.get("ATTRNAME").toString());
				oneLv1.setAttrValueId(attrValueId);			
				
				oneLv1.setAttrValueName_lang(new ArrayList<DCP_AttributionValueQueryRes.level2Elm>());
				
				for (Map<String, Object> langDatas : attrValueLangDatas) 
				{
					//过滤属于此单头的明细
					if(attrId.equals(langDatas.get("ATTRID")) == false)
						continue;
					if(attrValueId.equals(langDatas.get("ATTRVALUEID")) == false)
						continue;
																		
					String langType = langDatas.get("LANGTYPE").toString();
					if(langType ==null ||langType.isEmpty())
					{						
					   continue;
					}						
					String attrValueName = langDatas.get("ATTRVALUENAME").toString();
					
					DCP_AttributionValueQueryRes.level2Elm fstLang = res.new level2Elm();
					if(langType.equals(langType_cur))
					{
					  oneLv1.setAttrValueName(attrValueName);
					}
					
					fstLang.setLangType(langType);
					fstLang.setName(attrValueName);
					
					
					oneLv1.getAttrValueName_lang().add(fstLang);
					fstLang = null;
				}
				
				/*oneLv1.setAttrGroup(new ArrayList<DCP_AttributionValueQueryRes.attrGroup>());
				for (Map<String, Object> groupData : attrGroupDatas) 
				{
					if(attrId.equals(groupData.get("ATTRID").toString()) == false)
						continue;
					String attrGroupId = groupData.get("ATTRGROUPID").toString();
					if(attrGroupId==null||attrGroupId.isEmpty())
					{
						continue;
					}				
					DCP_AttributionValueQueryRes.attrGroup group = res.new attrGroup();
					group.setAttrGroupId(attrGroupId);
					group.setAttrGroupName(groupData.get("ATTRGROUPNAME").toString());
					
					oneLv1.getAttrGroup().add(group);
					
				}*/
							
				res.getDatas().add(oneLv1);
				oneLv1 = null;
			}
		}
		
	 return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	// TODO Auto-generated method stub
	
	}

	@Override
	protected String getQuerySql(DCP_AttributionValueQueryReq req) throws Exception {
	// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer(); 
		
		String eId = req.geteId();
		String curLangType = req.getLangType();
		String keyTxt = null;
		String status = null;
		String attrId = null;
		if(req.getRequest()!=null)
		{
			keyTxt = req.getRequest().getKeyTxt();
			status = req.getRequest().getStatus();
			attrId = req.getRequest().getAttrId();
		}
		
		/*int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();
		//分页起始位置
		int startRow=(pageNumber-1) * pageSize;
		sqlbuf.append(" select *  from (");
		
		sqlbuf.append(" select P.*,BL.ATTRNAME,C.ATTRGROUPID,CL.ATTRGROUPNAME from (");
		
		sqlbuf.append(" SELECT * FROM ("
		 + " SELECT  COUNT(DISTINCT a.ATTRVALUEID ) OVER() NUM ,dense_rank() over(ORDER BY a.ATTRID,a.ATTRVALUEID) rn, a. *  FROM "
		 + " ( SELECT a.ATTRID ,a.ATTRVALUEID, b.ATTRVALUENAME, a.status,a.SORTID, b.lang_Type AS langType ,"
		 + "  a.EID  FROM DCP_ATTRIBUTION_VALUE a  "
		 + "  LEFT JOIN DCP_ATTRIBUTION_VALUE_LANG b ON a.EID = b.EID AND a.ATTRID = b.ATTRID and a.ATTRVALUEID=b.ATTRVALUEID "
		 + "  inner join DCP_ATTRIBUTION C on a.eid = c.eid and a.ATTRID = C.ATTRID ");
		
		if(attrId!=null&&attrId.length()>0)//如果传了 ，属性分组编码，需要关联这个分组下面的属性
		{
			sqlbuf.append("  and C.ATTRID='"+attrId+"' ");
		}
		
		sqlbuf.append(" )   a ");
		 
		sqlbuf.append(" WHERE  EID = '"+eId+"'  " );
		if (keyTxt != null && keyTxt.length()>0)
		{
			sqlbuf.append(" AND (ATTRVALUEID like '%%"+keyTxt+"%%' or ATTRVALUENAME LIKE '%%"+keyTxt+"%%' )  ");
		}
		if(status!=null && status.length()>0)
		{
			sqlbuf.append(" and status='" + status + "' ");
		}
		sqlbuf.append( " )   WHERE rn >  "+startRow+" and rn < "+(startRow + pageSize)+" " );
		
		sqlbuf.append( " ) P");
		
		sqlbuf.append( " left join DCP_ATTRIBUTION_LANG BL on P.eid = BL.eid and P.ATTRID = BL.ATTRID  and BL.LANG_TYPE='"+curLangType+"' ");
		sqlbuf.append( " left join DCP_ATTRGROUP_DETAIL C on p.eid = C.eid and p.ATTRID = C.ATTRID");
		sqlbuf.append( " left join DCP_ATTRGROUP_LANG CL on p.eid = CL.eid  and CL.attrgroupid=C.ATTRGROUPID and CL.LANG_TYPE='"+curLangType+"' ");
		
		sqlbuf.append(" WHERE  P.EID = '"+eId+"'  " );
		sqlbuf.append( " )");*/
		
		sqlbuf.append(" select *  from (");
		sqlbuf.append(" SELECT a.ATTRID ,a.ATTRVALUEID, b.ATTRVALUENAME, a.status,a.SORTID, b.lang_Type AS langType ,a.EID,C.ATTRNAME ");
		sqlbuf.append(" FROM DCP_ATTRIBUTION_VALUE a  ");
		sqlbuf.append(" LEFT JOIN DCP_ATTRIBUTION_VALUE_LANG b ON a.EID = b.EID and a.ATTRID=b.ATTRID and a.ATTRVALUEID=b.ATTRVALUEID ");		
		sqlbuf.append(" LEFT JOIN DCP_ATTRIBUTION_LANG c ON a.EID = c.EID  and a.ATTRID=c.ATTRID and c.LANG_TYPE='"+curLangType+"' " );
		sqlbuf.append(" WHERE  a.EID = '"+eId+"'  " );
		
		if(attrId!=null&&attrId.length()>0)//如果传了 ，属性分组编码，需要关联这个分组下面的属性
		{
			sqlbuf.append("  and a.ATTRID='"+attrId+"' ");
		}
		if (keyTxt != null && keyTxt.length()>0)
		{
			sqlbuf.append(" AND (a.ATTRVALUEID like '%%"+keyTxt+"%%' or b.ATTRVALUENAME LIKE '%%"+keyTxt+"%%' )  ");
		}
		if(status!=null && status.length()>0)
		{
			sqlbuf.append(" and a.status='" + status + "' ");
		}
		sqlbuf.append( " )");
		
		sql = sqlbuf.toString();
		return sql;
	
	
	}

}
