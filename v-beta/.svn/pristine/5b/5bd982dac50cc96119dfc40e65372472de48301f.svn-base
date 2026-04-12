package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PayFuncClassTreeQueryReq;
import com.dsc.spos.json.cust.res.DCP_PayFuncClassTreeQueryRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_PayFuncClassTreeQuery extends SPosAdvanceService<DCP_PayFuncClassTreeQueryReq, DCP_PayFuncClassTreeQueryRes>
{

	@Override
	protected void processDUID(DCP_PayFuncClassTreeQueryReq req, DCP_PayFuncClassTreeQueryRes res) throws Exception 
	{
		String sql = null;
		sql = this.getPayFuncClassTree_SQL(req);

		List<Map<String, Object>> getQData = this.doQueryData(sql,null);
		
		res.setDatas(new ArrayList<DCP_PayFuncClassTreeQueryRes.level1Elm>());
		
		if(getQData!=null && getQData.isEmpty()==false)
		{
			Map<String, Boolean> map_condition=new HashMap<String, Boolean>();
			map_condition.put("CLASSNO", true);
			
			List<Map<String, Object>> getQDataFunc =MapDistinct.getMap(getQData, map_condition);			
			if(getQDataFunc!=null && getQDataFunc.isEmpty()==false)
			{
				for (Map<String, Object> oneData1 : getQDataFunc) 
				{
					DCP_PayFuncClassTreeQueryRes.level1Elm lv1=res.new level1Elm();
					lv1.setClassName(oneData1.get("CLASSNAME").toString());
					lv1.setClassNO(oneData1.get("CLASSNO").toString());
					lv1.setPriority(oneData1.get("PAYFUNCCLASS_PRIORITY").toString());
					lv1.setDatas(new ArrayList<DCP_PayFuncClassTreeQueryRes.level2Elm>());
					
					Map<String, Object> condition=new HashMap<String, Object>();
					condition.put("CLASSNO", oneData1.get("CLASSNO").toString());					
					
					List<Map<String, Object>> getQDataClass =MapDistinct.getWhereMap(getQData, condition, true);
					
					//
					map_condition=new HashMap<String, Boolean>();
//					map_condition.put("FUNCNO", true);	
//					map_condition.put("PAYCODE", true);
					
					map_condition.put("PAYCODEPOS", true);	
					
					List<Map<String, Object>> getQDataPaycode =MapDistinct.getMap(getQDataClass, map_condition);		
					
					if(getQDataPaycode!=null && getQDataPaycode.isEmpty()==false)
					{
						for (Map<String, Object> oneData2 : getQDataPaycode) 
						{
							//
							if(oneData2.get("FUNCNO").toString().trim().equals(""))
								continue;
							
							DCP_PayFuncClassTreeQueryRes.level2Elm lv2=res.new level2Elm();
							lv2.setMaxCh(oneData2.get("MAXCH").toString());
							lv2.setIsTurnover(oneData2.get("ISTURNOVER").toString());
							lv2.setCanOpenCasher(oneData2.get("CANOPENCASHER").toString());
							lv2.setCanOpenInvoice(oneData2.get("CANOPENINVOICE").toString());
							lv2.setCanReverse(oneData2.get("CANREVERSE").toString());
							lv2.setIsVoucherInput(oneData2.get("ISVOUCHERINPUT").toString());
							lv2.setOnlinePay(oneData2.get("ONLINEPAY").toString());
							lv2.setPayCode(oneData2.get("PAYCODE").toString());
							lv2.setPayName(oneData2.get("PAYNAME").toString());
							lv2.setPos_Use(oneData2.get("POS_USE").toString());
							lv2.setApp_Use(oneData2.get("APP_USE").toString());
							lv2.setApplet_Use(oneData2.get("APPLET_USE").toString());
							lv2.setOutSale_Use(oneData2.get("OUTSALE_USE").toString());
							lv2.setCanCharge(oneData2.get("CANCHARGE").toString());
							lv2.setCanSpill(oneData2.get("CANSPILL").toString());
							lv2.setCanScore(oneData2.get("CANSCORE").toString());
							lv2.setCanDiscount(oneData2.get("CANDISCOUNT").toString());
							lv2.setCanRecharge(oneData2.get("CANRECHARGE").toString());
							lv2.setFuncNO(oneData2.get("FUNCNO").toString());
							lv2.setFuncName(oneData2.get("FUNCNAME").toString());
							lv2.setPriority(oneData2.get("PAYFUNCNOINFO_PRIORITY").toString());
							lv2.setStatus(oneData2.get("PAYFUNCNOINFO_STATUS").toString());
							lv2.setClassNO(oneData1.get("CLASSNO").toString());
							lv2.setClassName(oneData1.get("CLASSNAME").toString());
							lv2.setCanSaleCard(oneData2.getOrDefault("CANSALECARD", "1").toString());
							lv2.setCanSaleTicket(oneData2.getOrDefault("CANSALETICKET", "1").toString());
							lv2.setPayCodePOS(oneData2.getOrDefault("PAYCODEPOS", "").toString());
							lv2.setPayNamePOS(oneData2.getOrDefault("PAYNAMEPOS", "").toString());
							lv2.setScanCode(oneData2.getOrDefault("SCANCODE", "1").toString());
							lv2.setShopType(oneData2.getOrDefault("SHOPTYPE", "2").toString());
							
							if(lv1.getDatas().contains(lv2))//表结构问题，暂时除去多余数据
							{
								continue;
							}
							
							lv1.getDatas().add(lv2);
							lv2=null;
						}
					}				
					
					res.getDatas().add(lv1);
					lv1=null;
					
				}
				
			}
			
		}
		
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PayFuncClassTreeQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PayFuncClassTreeQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PayFuncClassTreeQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PayFuncClassTreeQueryReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_PayFuncClassTreeQueryReq> getRequestType() 
	{
		return new TypeToken<DCP_PayFuncClassTreeQueryReq>(){};
	}

	@Override
	protected DCP_PayFuncClassTreeQueryRes getResponseType() 
	{
		return new DCP_PayFuncClassTreeQueryRes();
	}
	
	
	protected String getPayFuncClassTree_SQL(DCP_PayFuncClassTreeQueryReq req)
	{
		String sql = null;

		StringBuffer sqlbuf = new StringBuffer("");

		sqlbuf.append("SELECT A.CLASSNO,A.CLASSNAME,A.PRIORITY PAYFUNCCLASS_PRIORITY,B.FUNCNO,B.FUNCNAME, "
				+"B.Paycode,C.PAYNAME,B.CANREVERSE,B.Pos_Use,B.APP_USE,B.APPLET_USE,B.OUTSALE_USE,B.ONLINEPAY,B.ISVOUCHERINPUT,B.CANOPENINVOICE,B.CANOPENCASHER,"
				+"B.CANCHARGE,B.CANSPILL,B.CANSCORE,B.CANDISCOUNT,B.CANRECHARGE,B.PRIORITY PAYFUNCNOINFO_PRIORITY,B.status PAYFUNCNOINFO_STATUS "
				+ ", b.ISTURNOVER,b.maxCh , b.payCodePOS, b.payNamePOS, b.canSaleCard, b.canSaleTicket , b.scanCode,b.SHOPTYPE "
				+"FROM DCP_PAYFUNCCLASS A  "
				+"LEFT JOIN DCP_PAYFUNCNOINFO B ON A.CLASSNO=B.CLASSNO AND B.EID='"+req.geteId()+"' "
				+"LEFT JOIN DCP_PAYMENT C ON B.PAYCODE=C.PAYCODE AND C.EID='"+req.geteId()+"' "
				+"WHERE A.EID='"+req.geteId()+"' "
				+"ORDER BY A.PRIORITY,B.PRIORITY  ");
 
		sql = sqlbuf.toString();

		return sql;	
	}
	
	
	
}
