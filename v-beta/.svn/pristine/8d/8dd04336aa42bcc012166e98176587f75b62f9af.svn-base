package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_PaymentShopQueryReq;
import com.dsc.spos.json.cust.res.DCP_PaymentShopQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_PaymentShopQuery extends SPosBasicService<DCP_PaymentShopQueryReq,DCP_PaymentShopQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_PaymentShopQueryReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		String FUNCNO =req.getFuncNO();		
		String payCodePOS = req.getPayCodePOS();

		if (Check.Null(FUNCNO)) 
		{
			errMsg.append("POS支付编码不可为空值, ");
			isFail = true;
		} 

		if (Check.Null(payCodePOS)) 
		{
			errMsg.append("款别编码不可为空值, ");
			isFail = true;
		} 
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_PaymentShopQueryReq> getRequestType() 
	{
		return new TypeToken<DCP_PaymentShopQueryReq>(){};
	}

	@Override
	protected DCP_PaymentShopQueryRes getResponseType()
	{
		return new DCP_PaymentShopQueryRes();
	}

	@Override
	protected DCP_PaymentShopQueryRes processJson(DCP_PaymentShopQueryReq req) throws Exception 
	{

		String sql = null;		

		//查詢資料
		DCP_PaymentShopQueryRes res = null;
		res = this.getResponse();
		//单头总数
		sql = this.getQuery_selectedSql(req);			

		//已选门店
		String shopType="";
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		res.setDatas(new ArrayList<DCP_PaymentShopQueryRes.level1Elm>());		
		if (getQData != null && getQData.isEmpty() == false) 
		{			
			for (Map<String, Object> oneData : getQData) 
			{

				DCP_PaymentShopQueryRes.level1Elm oneLv1 = res.new level1Elm();
				oneLv1.setShopId(oneData.get("SHOPID").toString());
				oneLv1.setShopName(oneData.get("SHOPNAME").toString());
				shopType=oneData.get("SHOPTYPE").toString();
				
				res.getDatas().add(oneLv1);
				oneLv1=null;
			}
		}

		res.setShopType(shopType);
		
		getQData=null;

		return res;

	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception 
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_PaymentShopQueryReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		return null;
	}	
	
	
	protected String getQuery_selectedSql(DCP_PaymentShopQueryReq req) throws Exception 
	{
		String sql=null;		

		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append("SELECT A.SHOPID SHOPID,B.SHOPNAME,NVL(C.SHOPTYPE,2) SHOPTYPE FROM DCP_PAYFUNCNOINFO_SHOP A " 
				+ "INNER JOIN "
				+ "("
				+ "SELECT A.EID,A.ORGANIZATIONNO,B.ORG_NAME SHOPNAME FROM DCP_ORG A "
				+ "LEFT JOIN DCP_ORG_LANG B ON A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO AND B.LANG_TYPE='"+req.getLangType()+"' AND B.status='100' "
				+ "WHERE A.EID='"+req.geteId()+"' AND A.ORG_FORM='2' AND A.status='100' "
				+ ") B ON A.EID=B.EID AND A.SHOPID=B.ORGANIZATIONNO "
				+ "LEFT JOIN DCP_PAYFUNCNOINFO C ON A.EID=C.EID AND A.PAYCODEPOS=C.PAYCODEPOS "
				+ "WHERE A.EID='"+req.geteId()+"' "
				+ "and a.payCodePOS = '"+req.getPayCodePOS()+"'"
				+ "order by A.SHOPID ");

		sql=sqlbuf.toString();
		return sql;
	}
	



}
