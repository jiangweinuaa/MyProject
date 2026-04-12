package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_OrderMenuQueryReq;
import com.dsc.spos.json.cust.res.DCP_OrderMenuQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderMenuQuery extends SPosBasicService<DCP_OrderMenuQueryReq,DCP_OrderMenuQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_OrderMenuQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return false;
	}

	@Override
	protected TypeToken<DCP_OrderMenuQueryReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_OrderMenuQueryReq>(){};
	}

	@Override
	protected DCP_OrderMenuQueryRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_OrderMenuQueryRes();
	}

	@Override
	protected DCP_OrderMenuQueryRes processJson(DCP_OrderMenuQueryReq req) throws Exception {
	// TODO Auto-generated method stub
		
		DCP_OrderMenuQueryRes res = this.getResponse();
		res.setDatas(new ArrayList<DCP_OrderMenuQueryRes.level1Elm>());
		String sql = this.getQuerySql(req);
		List<Map<String, Object>> getQDataDetail=this.doQueryData(sql,null);
		if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
		{
			for (Map<String, Object> map : getQDataDetail) 
			{
				DCP_OrderMenuQueryRes.level1Elm oneLv1 = res.new level1Elm();
				String menuID = map.get("MENUID").toString();
				String menuName = map.get("MENUNAME").toString();
				String menuDescription = map.get("DESCRIPTION").toString();
				String menuMemo = map.get("MEMO").toString();
				String updateTime = map.get("UPDATE_TIME").toString();
				oneLv1.setMenuID(menuID);
				oneLv1.setMenuName(menuName);
				oneLv1.setMenuDescription(menuDescription);
				oneLv1.setMenuMemo(menuMemo);
				oneLv1.setUpdateTime(updateTime);	
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
	protected String getQuerySql(DCP_OrderMenuQueryReq req) throws Exception {
	// TODO Auto-generated method stub
		String eId = req.geteId();
		String keyTxt = req.getKeyTxt();
		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append("select * from OC_menu ");
		sqlbuf.append(" where EID='"+eId+"'");
		
		if(keyTxt!=null&&keyTxt.trim().length()>0)
		{
			sqlbuf.append(" and (menuid like '%%"+keyTxt+"%%' or menuname like '%%"+keyTxt+"%%')");
		}
		
		sqlbuf.append(" order by tran_time desc");
		
	return sqlbuf.toString();
	}

}
