package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.dsc.spos.json.cust.req.DCP_WarningDetailReq;
import com.dsc.spos.json.cust.req.DCP_WarningDetailReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_WarningDetailRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_WarningDetail extends SPosBasicService<DCP_WarningDetailReq, DCP_WarningDetailRes> {

	@Override
	protected boolean isVerifyFail(DCP_WarningDetailReq req) throws Exception {
	// TODO Auto-generated method stub
	return false;
	}

	@Override
	protected TypeToken<DCP_WarningDetailReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_WarningDetailReq>(){};
	}

	@Override
	protected DCP_WarningDetailRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_WarningDetailRes();
	}

	@Override
	protected DCP_WarningDetailRes processJson(DCP_WarningDetailReq req) throws Exception {
	// TODO Auto-generated method stub
		try 
		{
			DCP_WarningDetailRes res = this.getResponse();
			String sql=this.getQuerySql(req);	
			List<Map<String, Object>> getQData=this.doQueryData(sql, null);		
			res.setDatas(res.new level1Elm());
			if (getQData != null && getQData.isEmpty() == false) 
			{			
				
				Map<String, Object> oneData = getQData.get(0);
				DCP_WarningDetailRes.level1Elm oneLv1 = res.new level1Elm();
				oneLv1.setPushManList(new ArrayList<DCP_WarningDetailRes.level2PushMan>());
				oneLv1.setShopList(new ArrayList<DCP_WarningDetailRes.level2Shop>());
			
				
				String billNo = oneData.get("BILLNO").toString();
				oneLv1.setBillDate(oneData.get("BILLDATE").toString());
				oneLv1.setBillName(oneData.get("BILLNAME").toString());
				oneLv1.setBillNo(billNo);
				oneLv1.setBillType(oneData.get("BILLTYPE").toString());
				oneLv1.setCompanyId(oneData.get("COMPANYID").toString());
				oneLv1.setCompanyName("");
				oneLv1.setShopId(oneData.get("SHOPID").toString());
				oneLv1.setShopName("");
				oneLv1.setChannelId(oneData.get("CHANNELID").toString());
				oneLv1.setChannelName("");
				oneLv1.setEmployeeId(oneData.get("EMPLOYEEID").toString());
				oneLv1.setEmployeeName("");
				oneLv1.setDepartId(oneData.get("DEPARTID").toString());
				oneLv1.setDepartName("");
				
				oneLv1.setWarningType(oneData.get("WARNINGTYPE").toString());
				oneLv1.setWarningItem(oneData.get("WARNINGITEM").toString());
				oneLv1.setWarningItemDescription("");
				oneLv1.setStartTime(oneData.get("STARTTIME").toString());
				oneLv1.setEndTime(oneData.get("ENDTIME").toString());
				//oneLv1.setLastmodiopid(oneData.get("LASTMODIOPID").toString());
				//oneLv1.setLastmoditime(oneData.get("LASTMODITIME").toString());
				oneLv1.setOrderAmt(oneData.get("ORDERAMT").toString());
				oneLv1.setOrderQty(oneData.get("ORDERQTY").toString());
				oneLv1.setPointQty(oneData.get("POINTQTY").toString());
				oneLv1.setPushTime(oneData.get("PUSHTIME").toString());
				oneLv1.setPushTimeType(oneData.get("PUSHTIMETYPE").toString());
				oneLv1.setRestrictShop(oneData.get("RESTRICTSHOP").toString());
			
				String status = oneData.get("STATUS").toString();
				oneLv1.setStatus(status);
				oneLv1.setTemplateType(oneData.get("TEMPLATETYPE").toString());
				oneLv1.setTemplateTypeTitle(oneData.get("TEMPLATETITLE").toString());
				oneLv1.setMsgBegin(oneData.get("MSGBEGIN").toString());
				oneLv1.setMsgMiddle(oneData.get("MSGMIDDLE").toString());
				oneLv1.setMsgEnd(oneData.get("MSGEND").toString());
				oneLv1.setLinkUrl(oneData.get("LINKURL").toString());
				oneLv1.setBatchNo(oneData.get("BATCHNO").toString());
				
				
			  //推送门店
				Map<String, Boolean> condition_pickShop = new HashMap<String, Boolean>(); //查詢條件
				condition_pickShop.put("PICKSHOP_SERIALNO", true);	
				List<Map<String, Object>> getData_pickShop=MapDistinct.getMap(getQData, condition_pickShop);
				
				for (Map<String, Object> pickShop : getData_pickShop)
				{
					String serialNo = pickShop.get("PICKSHOP_SERIALNO").toString();
					String shopId = pickShop.get("PICKSHOP_SHOPID").toString();
					String shopName = pickShop.get("PICKSHOP_SHOPNAME").toString();
					if(serialNo!=null&&serialNo.isEmpty()==false)
					{
						DCP_WarningDetailRes.level2Shop oneLv2PickShop = res.new level2Shop();
						
						oneLv2PickShop.setSerialNo(serialNo);
						oneLv2PickShop.setShopId(shopId);
						oneLv2PickShop.setShopName(shopName);
						oneLv1.getShopList().add(oneLv2PickShop);	
						
					}
								
					
				}
				
			  //推送人
				Map<String, Boolean> condition_pushMan = new HashMap<String, Boolean>(); //查詢條件
				condition_pushMan.put("PUSHMAN_SERIALNO", true);	
				List<Map<String, Object>> getData_pushMan=MapDistinct.getMap(getQData, condition_pushMan);
				//推送方式
				Map<String, Boolean> condition_pushWay = new HashMap<String, Boolean>(); //查詢條件
				condition_pushWay.put("PUSHWAY_SERIALNO", true);	
				condition_pushWay.put("PUSHWAY_OPNO", true);
				List<Map<String, Object>> getData_pushWay=MapDistinct.getMap(getQData, condition_pushWay);
				
				for (Map<String, Object> pushMan : getData_pushMan)
				{				
					String opNo = pushMan.get("PUSHMAN_OPNO").toString();
					String serialNo = pushMan.get("PUSHMAN_SERIALNO").toString();
					String opName = pushMan.get("PUSHMAN_OPNAME").toString();
					if(serialNo!=null&&serialNo.isEmpty()==false)
					{
						DCP_WarningDetailRes.level2PushMan oneLv2PushMan = res.new level2PushMan();
						oneLv2PushMan.setPushWayList(new ArrayList<DCP_WarningDetailRes.level3PushWay>());
						oneLv2PushMan.setOpNo(opNo);
						oneLv2PushMan.setOpName(opName);
						oneLv2PushMan.setSerialNo(serialNo);
						oneLv2PushMan.setMobilePhone(pushMan.get("MOBILEPHONE").toString());
						oneLv2PushMan.setEmail(pushMan.get("EMAIL").toString());
						oneLv2PushMan.setUserId(pushMan.get("PUSHMAN_USERID").toString());
						oneLv2PushMan.setUserName(pushMan.get("PUSHMAN_USERNAME").toString());
						
						for (Map<String, Object> pushWay : getData_pushWay)
						{
							String opNo_pushWay = pushWay.get("PUSHWAY_OPNO").toString();
							String serialNo_pushWay = pushWay.get("PUSHWAY_SERIALNO").toString();
							String pushWay_pushWay = pushWay.get("PUSHWAY").toString();
							String pushWayName = "";
							if(serialNo_pushWay!=null&&serialNo_pushWay.isEmpty()==false&&opNo_pushWay!=null&&opNo_pushWay.isEmpty()==false&&opNo_pushWay.equals(opNo))
							{
								DCP_WarningDetailRes.level3PushWay oneLv3PushWay = res.new level3PushWay();
								oneLv3PushWay.setSerialNo(serialNo_pushWay);
								oneLv3PushWay.setPushWay(pushWay_pushWay);
								if(pushWay_pushWay.equals("PHONE"))
								{
									pushWayName = "手机";									
								}
								else if (pushWay_pushWay.equals("EMAIL")) 
								{
									pushWayName = "邮箱";			
								}
								else if (pushWay_pushWay.equals("DING")) 
								{
									pushWayName = "钉钉";								
								}
								oneLv3PushWay.setPushWayName(pushWayName);
								
								oneLv2PushMan.getPushWayList().add(oneLv3PushWay);
								
							}
							
						}
						
						
						oneLv1.getPushManList().add(oneLv2PushMan);
						
					}
				}
				
				res.setDatas(oneLv1);
					
				
			}
			
			return res;					
	
		} 
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());			
		}
		
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	// TODO Auto-generated method stub
	
	}

	@Override
	protected String getQuerySql(DCP_WarningDetailReq req) throws Exception {
	// TODO Auto-generated method stub
		String eId = req.geteId();
		String langType = req.getLangType();
		level1Elm requestModel =	req.getRequest();		

		String billNo = requestModel.getBillNo();

		String sql= "";
		StringBuffer sqlbuf=new StringBuffer("");
		//分页处理
		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();
		int startRow=(pageNumber-1) * pageSize;
		int endRow = startRow+pageSize;
		
		sqlbuf.append("select * from (");	
		
		sqlbuf.append(" select  a.billno,a.billname,a.billtype,to_char(a.billdate, 'yyyy-MM-dd') as billdate,a.companyid,a.shopid,a.channelid,a.employeeid,a.departid,a.warningtype,a.warningitem,a.starttime,a.endtime,a.orderqty,a.orderamt,a.pointqty,a.templatetype,a.pushtimetype,a.pushtime,a.restrictshop,a.memo,a.status,"
			+ "a.createopid,a.createopname,a.createtime,a.lastmodiopid,a.lastmodiopname,a.lastmoditime,");		
		sqlbuf.append(" b.serialno as pushman_serialno,b.opno as pushman_opno,b.mobilephone,b.email,s.op_name as pushman_opname ,");
		sqlbuf.append("  c.serialno as pushway_serialno,c.opno as pushway_opno,c.pushway,");
		sqlbuf.append(" d.serialno as pickshop_serialno,d.shopid as pickshop_shopid,e.org_name as pickshop_shopname,");
		sqlbuf.append(" f.templatetypename,f.templatetitle,f.msgbegin,f.msgmiddle,f.msgend,f.linkurl,f.batchno,");
		sqlbuf.append(" g.userid as pushman_userid,g.username as pushman_username ");
		sqlbuf.append(" from dcp_warning a ");
		sqlbuf.append(" left join dcp_warning_pushman b on a.eid=b.eid and a.billno=b.billno ");
		sqlbuf.append(" left join platform_staffs_lang s on b.opno=s.opno and s.EID='"+eId+"' and s.lang_type='"+langType+"'");
		sqlbuf.append(" left join dcp_warning_pushway c on a.eid=c.eid and a.billno=c.billno and c.opno=b.opno");
		sqlbuf.append(" left join dcp_warning_pickshop d on a.eid=d.eid and a.billno=d.billno");
		sqlbuf.append(" left join DCP_ORG_lang e  on a.eid=e.EID and d.shopid=e.organizationno and e.lang_type='"+langType+"'");
		sqlbuf.append(" left join dcp_warning_msgtemplate f on  a.eid=f.eid and a.templatetype=f.templatetype");
		sqlbuf.append(" left join DCP_DING_USERSET g on a.eid=g.EID and g.opno=b.opno and g.status='100'");
		sqlbuf.append(" where a.eid='"+eId+"' and a.billno='"+billNo+"'");
		//sqlbuf.append("");
		
		sqlbuf.append(" )");

		sql = sqlbuf.toString();
		return sql;
	}

}
