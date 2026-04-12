package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_PayFuncQueryReq;
import com.dsc.spos.json.cust.res.DCP_PayFuncQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_PayFuncQuery extends SPosBasicService<DCP_PayFuncQueryReq,DCP_PayFuncQueryRes>{

	@Override
	protected boolean isVerifyFail(DCP_PayFuncQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return false;
	}

	@Override
	protected TypeToken<DCP_PayFuncQueryReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_PayFuncQueryReq>(){};
	}

	@Override
	protected DCP_PayFuncQueryRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_PayFuncQueryRes();
	}

	@Override
	protected DCP_PayFuncQueryRes processJson(DCP_PayFuncQueryReq req) throws Exception {
	// TODO Auto-generated method stub
    String sql = null;		
		
		//查詢資料
    DCP_PayFuncQueryRes res = null;
		res = this.getResponse();
	//获取当前登陆用户的语言类型			
		String curLangtype = req.getLangType();
		if(curLangtype==null||curLangtype.isEmpty())
		{
			curLangtype = "zh_CN";
		}
		//单头总数
		sql = this.getQuerySql(req);
    List<Map<String, Object>> getQData = this.doQueryData(sql, null);
			
		res.setDatas(new ArrayList<DCP_PayFuncQueryRes.level1Elm>());
		if (getQData != null && getQData.isEmpty() == false) 
		{		
			
			Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
			condition.put("FUNCNO", true);			
			//调用过滤函数
			List<Map<String, Object>> getHeader=MapDistinct.getMap(getQData, condition);
			
			condition.put("LANGTYPE", true);	
			//调用过滤函数
			List<Map<String, Object>> getLang=MapDistinct.getMap(getQData, condition);
						
			res.setDatas(new ArrayList<DCP_PayFuncQueryRes.level1Elm>());
			
			for (Map<String, Object> oneData : getHeader) 
			{
				DCP_PayFuncQueryRes.level1Elm oneLv1 = res.new level1Elm();
				String funcNo = oneData.get("FUNCNO").toString();
				String funcName = oneData.get("FUNCNAME").toString();
				oneLv1.setFuncNo(funcNo);
				oneLv1.setFuncName(funcName);
				oneLv1.setMemo(oneData.get("MEMO").toString());
				oneLv1.setStatus(oneData.get("STATUS").toString());
                oneLv1.setEnableRecharge(oneData.get("ENABLE_RECHARGE").toString());
                oneLv1.setEnableSaleCard(oneData.get("ENABLE_SALECARD").toString());
                oneLv1.setEnableSaleTicket(oneData.get("ENABLE_SALETICKET").toString());
                oneLv1.setEnableCustReturn(oneData.get("ENABLE_CUSTRETURN").toString());
                oneLv1.setEnableTableRsv(oneData.get("ENABLE_TABLERSV").toString());

                oneLv1.setFuncName_lang(new ArrayList<DCP_PayFuncQueryRes.funcNameLang>());
				for (Map<String, Object> map : getLang) 
				{
					String funcNo_lang = map.get("FUNCNO").toString();
					String langType = map.get("LANGTYPE").toString();
					String name = map.get("FUNCNAME").toString();
					if(langType==null||langType.isEmpty())
					{
						continue;
					}
					
					if(funcNo_lang.equals(funcNo))
					{
						DCP_PayFuncQueryRes.funcNameLang oneLv2 = res.new funcNameLang();
						oneLv2.setLangType(langType);
						oneLv2.setName(name);
						oneLv1.getFuncName_lang().add(oneLv2);
						if(curLangtype.equals(langType))
						{
							oneLv1.setFuncName(name);
						}
						
					}
			
		
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
	protected String getQuerySql(DCP_PayFuncQueryReq req) throws Exception {
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
				+" SELECT A.*, b.lang_Type AS langType ,B.FUNCNAME "
				+" FROM DCP_PAYFUNC a"
				+" LEFT JOIN DCP_PAYFUNC_lang b ON a.EID = b.EID AND a.FUNCNO = b.FUNCNO and B.lang_Type='"+langType+"' "		
				+ " WHERE a.EID='"+eId+"'  ");
		if (keyTxt != null && keyTxt.length()>0)
		{
			sqlbuf.append(" AND (a.FUNCNO like '%%"+keyTxt+"%%' or b.FUNCNAME LIKE '%%"+keyTxt+"%%' )  ");
		}
		if(status!=null && status.length()>0)
		{
			sqlbuf.append(" and a.status='" + status + "' ");
			
		}
		sqlbuf.append( " ) order by  FUNCNO ");
		sql = sqlbuf.toString();
		return sql;
	
	
	}

}
