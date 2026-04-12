package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_AttributionQueryReq;
import com.dsc.spos.json.cust.res.DCP_AttributionQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;


public class DCP_AttributionQuery extends SPosBasicService<DCP_AttributionQueryReq,DCP_AttributionQueryRes>{

	@Override
	protected boolean isVerifyFail(DCP_AttributionQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return false;
	}

	@Override
	protected TypeToken<DCP_AttributionQueryReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_AttributionQueryReq>(){};
	}

	@Override
	protected DCP_AttributionQueryRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_AttributionQueryRes();
	}

	@Override
	protected DCP_AttributionQueryRes processJson(DCP_AttributionQueryReq req) throws Exception {
	// TODO Auto-generated method stub
    String sql = null;		
		
		//查詢資料
    DCP_AttributionQueryRes res = null;
		res = this.getResponse();
	//获取当前登陆用户的语言类型
			String langType_cur = req.getLangType();
		//单头总数
		sql = this.getQuerySql(req);
    List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		
		int totalRecords;								//总笔数
		int totalPages;	
		res.setDatas(new ArrayList<DCP_AttributionQueryRes.level1Elm>());
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
			
			//属性
			Map<String, Boolean> condition_attr = new HashMap<String, Boolean>(); //查詢條件
			condition_attr.put("ATTRID", true);		
			//调用过滤函数
			List<Map<String, Object>> attrDatas=MapDistinct.getMap(getQData, condition_attr);
			
			//属性多语言
			Map<String, Boolean> condition_attrLang = new HashMap<String, Boolean>(); //查詢條件
			condition_attrLang.put("ATTRID", true);		
			condition_attrLang.put("LANGTYPE", true);		
			//调用过滤函数
			List<Map<String, Object>> attrLangDatas=MapDistinct.getMap(getQData, condition_attrLang);
			
			//属性分组
			Map<String, Boolean> condition_attrGroup = new HashMap<String, Boolean>(); //查詢條件
			condition_attrGroup.put("ATTRID", true);	
			condition_attrGroup.put("ATTRGROUPID", true);
			//调用过滤函数
			List<Map<String, Object>> attrGroupDatas=MapDistinct.getMap(getQData, condition_attrGroup);
			
			//
			Map<String, Boolean> condition_attrValue = new HashMap<String, Boolean>(); //查詢條件
			condition_attrValue.put("ATTRID", true);	
			condition_attrValue.put("ATTRVALUEID", true);
			//调用过滤函数
			List<Map<String, Object>> attrValueDatas=MapDistinct.getMap(getQData, condition_attrValue);
			
			
			res.setDatas(new ArrayList<DCP_AttributionQueryRes.level1Elm>());
			
			for (Map<String, Object> oneData : attrDatas) 
			{
				DCP_AttributionQueryRes.level1Elm oneLv1 = res.new level1Elm();
				String attrId = oneData.get("ATTRID").toString();	
				String sortId = oneData.get("SORTID").toString();	
	
				oneLv1.setAttrId(attrId);
				oneLv1.setSortId(sortId);
				oneLv1.setStatus(oneData.get("STATUS").toString());
				oneLv1.setProductParam(oneData.get("PRODUCTPARAM").toString());
				oneLv1.setMultiSpec(oneData.get("MULTISPEC").toString());
				oneLv1.setMemo(oneData.get("MEMO").toString());
				oneLv1.setAttrName_lang(new ArrayList<DCP_AttributionQueryRes.level2Elm>());
				
				for (Map<String, Object> langDatas : attrLangDatas) 
				{
					//过滤属于此单头的明细
					if(attrId.equals(langDatas.get("ATTRID")) == false)
						continue;
					
					DCP_AttributionQueryRes.level2Elm fstLang = res.new level2Elm();
					
					String langType = langDatas.get("LANGTYPE").toString();
					String attrName = langDatas.get("ATTRNAME").toString();				
					if(langType.equals(langType_cur))
					{
					  oneLv1.setAttrName(attrName);
					}
					
					fstLang.setLangType(langType);
					fstLang.setName(attrName);
					
					
					oneLv1.getAttrName_lang().add(fstLang);
					fstLang = null;
				}
				
				oneLv1.setAttrGroup(new ArrayList<DCP_AttributionQueryRes.attrGroup>());
				for (Map<String, Object> groupData : attrGroupDatas) 
				{
					if(attrId.equals(groupData.get("ATTRID").toString()) == false)
						continue;
					String attrGroupId = groupData.get("ATTRGROUPID").toString();
					if(attrGroupId==null||attrGroupId.isEmpty())
					{
						continue;
					}				
					DCP_AttributionQueryRes.attrGroup group = res.new attrGroup();
					group.setAttrGroupId(attrGroupId);
					group.setAttrGroupName(groupData.get("ATTRGROUPNAME").toString());
					
					oneLv1.getAttrGroup().add(group);
					
				}
				
				
				oneLv1.setAttrValue(new ArrayList<DCP_AttributionQueryRes.attrValue>());
				for (Map<String, Object> attrValueData : attrValueDatas) 
				{
					if(attrId.equals(attrValueData.get("ATTRID").toString()) == false)
						continue;
					String attrValueId = attrValueData.get("ATTRVALUEID").toString();
					if(attrValueId==null||attrValueId.isEmpty())
					{
						continue;
					}				
					DCP_AttributionQueryRes.attrValue value = res.new attrValue();
					value.setAttrValueId(attrValueId);
					value.setAttrValueName(attrValueData.get("ATTRVALUENAME").toString());
					
					oneLv1.getAttrValue().add(value);
					
				}
				
				
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
	protected String getQuerySql(DCP_AttributionQueryReq req) throws Exception {
	// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer(); 
		
		String eId = req.geteId();
		String curLangType = req.getLangType();
		String keyTxt = null;
		String status = null;
		String attrGroupId = null;
		if(req.getRequest()!=null)
		{
			keyTxt = req.getRequest().getKeyTxt();
			status = req.getRequest().getStatus();
			attrGroupId = req.getRequest().getAttrGroupId();
		}
		
		/*int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();
		//分页起始位置
		int startRow=(pageNumber-1) * pageSize;
		sqlbuf.append(" select *  from (");
		
		sqlbuf.append(" select P.*,B.ATTRVALUEID,BL.ATTRVALUENAME,C.ATTRGROUPID,CL.ATTRGROUPNAME from (");
		
		sqlbuf.append(" SELECT * FROM ("
		 + " SELECT  COUNT(DISTINCT a.ATTRID ) OVER() NUM ,dense_rank() over(ORDER BY a.ATTRID) rn, a. *  FROM "
		 + " ( SELECT a.ATTRID ,a.ATTRTYPE,a.memo, b.ATTRNAME AS ATTRNAME, a.status,a.SORTID, b.lang_Type AS langType ,"
		 + "  a.EID  FROM DCP_Attribution a  "
		 + "  LEFT JOIN DCP_Attribution_LANG b ON a.EID = b.EID AND a.ATTRID = b.ATTRID ");
		
		if(attrGroupId!=null&&attrGroupId.length()>0)//如果传了 ，属性分组编码，需要关联这个分组下面的属性
		{
			sqlbuf.append(" inner join DCP_ATTRGROUP_DETAIL C on a.eid = c.eid and a.ATTRID = C.ATTRID  and C.ATTRGROUPID='"+attrGroupId+"' ");
		}
		
		sqlbuf.append(" )   a ");
		 
		sqlbuf.append(" WHERE  EID = '"+eId+"'  " );
		if (keyTxt != null && keyTxt.length()>0)
		{
			sqlbuf.append(" AND (ATTRID like '%%"+keyTxt+"%%' or ATTRNAME LIKE '%%"+keyTxt+"%%' )  ");
		}
		if(status!=null && status.length()>0)
		{
			sqlbuf.append(" and status='" + status + "' ");
		}
		sqlbuf.append( " )   WHERE rn >  "+startRow+" and rn < "+(startRow + pageSize)+" " );
		
		sqlbuf.append( " ) P");
		
		sqlbuf.append( " left join DCP_ATTRIBUTION_VALUE B on P.eid = B.eid and P.ATTRID = B.ATTRID ");
		sqlbuf.append( " left join DCP_ATTRIBUTION_VALUE_LANG BL on P.eid = BL.eid  and B.ATTRVALUEID=BL.ATTRVALUEID and BL.LANG_TYPE='"+curLangType+"' ");
		sqlbuf.append( " left join DCP_ATTRGROUP_DETAIL C on p.eid = C.eid and p.ATTRID = C.ATTRID ");
		sqlbuf.append( " left join DCP_ATTRGROUP_LANG CL on p.eid = CL.eid  and CL.attrgroupid=C.ATTRGROUPID and CL.LANG_TYPE='"+curLangType+"' ");
		sqlbuf.append(" WHERE  P.EID = '"+eId+"'  " );
		sqlbuf.append( " )");*/
					
		sqlbuf.append(" select *  from (");
		sqlbuf.append(" SELECT a.eid,a.attrid,a.productparam,a.multispec,a.sortid,a.memo,a.status,b.ATTRNAME, b.lang_Type AS langType ,");
		sqlbuf.append(" C.ATTRVALUEID,CL.ATTRVALUENAME,D.ATTRGROUPID,DL.ATTRGROUPNAME ");
		sqlbuf.append(" FROM  DCP_Attribution a ");
		sqlbuf.append(" LEFT JOIN DCP_Attribution_LANG b ON a.EID = b.EID AND a.ATTRID = b.ATTRID ");
		if(attrGroupId!=null&&attrGroupId.length()>0)//如果传了 ，属性分组编码，需要关联这个分组下面的属性
		{
			sqlbuf.append(" inner join DCP_ATTRGROUP_DETAIL K on a.eid = K.eid and a.ATTRID = K.ATTRID  and K.ATTRGROUPID='"+attrGroupId+"' ");
		}
		sqlbuf.append(" left join DCP_ATTRIBUTION_VALUE C on a.eid = C.eid and a.ATTRID = c.ATTRID");
		sqlbuf.append(" left join DCP_ATTRIBUTION_VALUE_LANG CL on a.eid = CL.eid and C.ATTRID = CL.ATTRID and C.ATTRVALUEID=CL.ATTRVALUEID and CL.LANG_TYPE='"+curLangType+"' ");
		sqlbuf.append(" left join DCP_ATTRGROUP_DETAIL D on a.eid = D.eid and a.ATTRID = D.ATTRID");
		sqlbuf.append(" left join DCP_ATTRGROUP_LANG DL on a.eid = DL.eid  and DL.attrgroupid=D.ATTRGROUPID and DL.LANG_TYPE='"+curLangType+"' ");
		sqlbuf.append(" where a.eid ='"+eId+"' ");
		if (keyTxt != null && keyTxt.length()>0)
		{
			sqlbuf.append(" AND (a.ATTRID like '%%"+keyTxt+"%%' or b.ATTRNAME LIKE '%%"+keyTxt+"%%' )  ");
		}
		if(status!=null && status.length()>0)
		{
			sqlbuf.append(" and a.status='" + status + "' ");
		}
		sqlbuf.append(" ORDER BY a.sortid,c.sortid");
			
		sqlbuf.append(" )");
		
		
		
		sql = sqlbuf.toString();
		return sql;
	
	
	}

}
