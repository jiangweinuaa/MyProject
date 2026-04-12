package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_KeyQueryReq;
import com.dsc.spos.json.cust.res.DCP_KeyQueryRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_KeyQuery extends SPosBasicService<DCP_KeyQueryReq,DCP_KeyQueryRes>
{	
	@Override
	public boolean needTokenVerify() 
	{
		return Boolean.TRUE;
	}
	
	@Override
	protected boolean isVerifyFail(DCP_KeyQueryReq req) throws Exception 
	{
		return false;
	}
	
	@Override
	protected TypeToken<DCP_KeyQueryReq> getRequestType() 
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_KeyQueryReq>(){};
	}
	
	@Override
	protected DCP_KeyQueryRes getResponseType() 
	{
		// TODO Auto-generated method stub
		return new DCP_KeyQueryRes();
	}
	
	@Override
	protected DCP_KeyQueryRes processJson(DCP_KeyQueryReq req) throws Exception 
	{		
		return null;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
	
	}
	
	@Override
	protected String getQuerySql(DCP_KeyQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String execute(String json) throws Exception 
	{
		ParseJson pj = new ParseJson();
		DCP_KeyQueryReq req = pj.jsonToBean(json, new TypeToken<DCP_KeyQueryReq>(){});
		
		DCP_KeyQueryRes res = this.getResponse();
		
		//取得 SQL
		String sql = null;
	  
		//查詢條件
		String langtype= req.getLangType();
		String status=req.getStatus();
		String eId=req.geteId();
		String oShopId=req.getoShopId();
		String keyTxt=req.getkeyTxt();
		String opno=req.getOpNO();
		
		//组一个SQL语句
		String sstatus="";
		if (!Check.Null(status)) 
		{ 
			sstatus=String.format(" a.status='%s' and ", status);
		} 
		String sEId= String.format(" a.eid='%s' ", eId);
		String skshop="";
		String sshop="";
		if (!Check.Null(oShopId)) 
		{ 
			skshop=String.format(" and a.SHOPID='%s' ", oShopId);
		}
		else 
		{
			sshop=String.format(" and a.SHOPID in (select SHOPID  from platform_staffs_shop where EID='%s' and opno='%s'   ) ", sEId,opno);
		}
		
		String skeyTxt="";
		if (!Check.Null(keyTxt)) 
		{ 
			skeyTxt=String.format(" and ( a.kbtype like '%s%' or a.kbName like '%s%' ) ", keyTxt,keyTxt);
		} 
		sql = String.format("select a.key_ID as keyID,a.SHOPID,a.kbType,a.kbName,a.status,b.SHOPNAME from ta_key a inner join "
				+ "("
				+ "SELECT A.EID,A.ORGANIZATIONNO SHOPID,B.ORG_NAME SHOPNAME,B.LANG_TYPE FROM DCP_ORG A "
				+ "LEFT JOIN DCP_ORG_LANG B ON A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO AND B.LANG_TYPE='"+langtype+"' AND B.status='100' "
				+ "WHERE A.EID='"+req.geteId()+"' AND A.ORG_FORM='2' AND A.status='100' "
				+ ") b on a.EID=b.EID and a.SHOPID=b.SHOPID and b.lang_type='%s' where ",langtype);
		
		sql += sstatus+sEId+skshop+skeyTxt+sshop;
		String[] conditionValues1 = { }; //查詢條件
		List<Map<String, Object>> getQData1 = this.doQueryData(sql,conditionValues1);	
		res.setdatas(new ArrayList<DCP_KeyQueryRes.level1Elm>());
		if (getQData1 != null && getQData1.isEmpty() == false)  // 有資料，取得詳細內容
		{
			for (Map<String, Object> oneData1 : getQData1)
			{
				DCP_KeyQueryRes.level1Elm oneLv1= res.new level1Elm();
				String rkeyID = oneData1.get("KEYID").toString();
				String shopId = oneData1.get("SHOPID").toString();
				String rshopName = oneData1.get("SHOPNAME").toString();
				String rkbType = oneData1.get("KBTYPE").toString();
				String rkbName = oneData1.get("KBNAME").toString();
				String rstatus = oneData1.get("STATUS").toString();
				
				oneLv1.setkeyID(rkeyID);
				oneLv1.setShopId(shopId);
				oneLv1.setshopName(rshopName);
				oneLv1.setkbType(rkbType);
				oneLv1.setkbName(rkbName);
				oneLv1.setStatus(rstatus);
				
				res.getdatas().add(oneLv1);
			}
		}
		
		String sRes=pj.beanToJson(res);
		pj=null;
		
		return sRes;
	}	
}
