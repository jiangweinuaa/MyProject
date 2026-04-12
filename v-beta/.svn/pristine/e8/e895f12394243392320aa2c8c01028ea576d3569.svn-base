package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_PayClassQueryReq;
import com.dsc.spos.json.cust.res.DCP_PayClassQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_PayClassQuery extends SPosBasicService<DCP_PayClassQueryReq,DCP_PayClassQueryRes>{

	@Override
	protected boolean isVerifyFail(DCP_PayClassQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return false;
	}

	@Override
	protected TypeToken<DCP_PayClassQueryReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_PayClassQueryReq>(){};
	}

	@Override
	protected DCP_PayClassQueryRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_PayClassQueryRes();
	}

	@Override
	protected DCP_PayClassQueryRes processJson(DCP_PayClassQueryReq req) throws Exception {
	// TODO Auto-generated method stub
    String sql = null;		
		
		//查詢資料
    DCP_PayClassQueryRes res = null;
		res = this.getResponse();
	//获取当前登陆用户的语言类型
			String langType_cur = req.getLangType();
		//单头总数
		sql = this.getQuerySql(req);
    List<Map<String, Object>> getQData = this.doQueryData(sql, null);
			
		res.setDatas(new ArrayList<DCP_PayClassQueryRes.level1Elm>());
		if (getQData != null && getQData.isEmpty() == false) 
		{		
			
			Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
			condition.put("CLASSNO", true);			
			//调用过滤函数
			List<Map<String, Object>> getHeader=MapDistinct.getMap(getQData, condition);
						
			res.setDatas(new ArrayList<DCP_PayClassQueryRes.level1Elm>());
			
			for (Map<String, Object> oneData : getHeader) 
			{
				DCP_PayClassQueryRes.level1Elm oneLv1 = res.new level1Elm();
				String classNo = oneData.get("CLASSNO").toString();	
				String sortId = oneData.get("SORTID").toString();	
	
				oneLv1.setClassNo(classNo);
				oneLv1.setSortId(sortId);
				oneLv1.setStatus(oneData.get("STATUS").toString());
				oneLv1.setMemo(oneData.get("MEMO").toString());
				oneLv1.setCreateopid(oneData.get("CREATEOPID").toString());
				oneLv1.setCreateopname(oneData.get("CREATEOPNAME").toString());
				oneLv1.setCreatetime(oneData.get("CREATETIME").toString());
				oneLv1.setLastmodiopid(oneData.get("LASTMODIOPID").toString());
				oneLv1.setLastmodiname(oneData.get("LASTMODIOPNAME").toString());
				oneLv1.setLastmoditime(oneData.get("LASTMODITIME").toString());
				
				oneLv1.setClassName_lang(new ArrayList<DCP_PayClassQueryRes.className>());
				
			
				for (Map<String, Object> langDatas : getQData) 
				{
					//过滤属于此单头的明细
					if(classNo.equals(langDatas.get("CLASSNO").toString()) == false)
						continue;
					
					DCP_PayClassQueryRes.className fstLang = res.new className();
					
					String langType = langDatas.get("LANGTYPE").toString();
					String className = langDatas.get("CLASSNAME").toString();				
					if(langType.equals(langType_cur))
					{
					  oneLv1.setClassName(className);
					}
					
					fstLang.setLangType(langType);
					fstLang.setName(className);
					
					
					oneLv1.getClassName_lang().add(fstLang);
					fstLang = null;
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
	protected String getQuerySql(DCP_PayClassQueryReq req) throws Exception {
	// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer(); 
		
		String eId = req.geteId();
		String keyTxt = null;
		String status = null;
		if(req.getRequest()!=null)
		{
			keyTxt = req.getRequest().getKeyTxt();
			status = req.getRequest().getStatus();
			
		}
		
		String langType = req.getLangType();
		if(langType==null||langType.isEmpty())
		{
			langType = "zh_CN";
		}
		
		
		
		sqlbuf.append(" SELECT * FROM ("
				+" SELECT A.*, b.lang_Type AS langType ,B.CLASSNAME "
				+" FROM DCP_PAYCLASS a"
				+" LEFT JOIN DCP_PAYCLASS_LANG b ON a.EID = b.EID AND a.CLASSNO = b.CLASSNO"			
				+ " WHERE a.EID='"+eId+"'  ");
		if (keyTxt != null && keyTxt.length()>0)
		{
			sqlbuf.append(" AND (a.CLASSNO like '%%"+keyTxt+"%%' or b.CLASSNAME LIKE '%%"+keyTxt+"%%' )  ");
		}
		if(status!=null && status.length()>0)
		{
			sqlbuf.append(" and a.status='" + status + "' ");
			
		}
		sqlbuf.append( " ) order by  SORTID ");
		sql = sqlbuf.toString();
		return sql;
	
	
	}

}
