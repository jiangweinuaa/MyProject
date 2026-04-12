package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_WarningLogQueryReq;
import com.dsc.spos.json.cust.req.DCP_WarningLogQueryReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_WarningLogQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_WarningLogQuery extends SPosBasicService<DCP_WarningLogQueryReq, DCP_WarningLogQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_WarningLogQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return false;
	}

	@Override
	protected TypeToken<DCP_WarningLogQueryReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_WarningLogQueryReq>(){};
	}

	@Override
	protected DCP_WarningLogQueryRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_WarningLogQueryRes();
	}

	@Override
	protected DCP_WarningLogQueryRes processJson(DCP_WarningLogQueryReq req) throws Exception {
	// TODO Auto-generated method stub
		try 
		{
			//DCP_WarningLogQueryRes res = new DCP_WarningLogQueryRes();
			DCP_WarningLogQueryRes res = this.getResponse();
			String sql=this.getQuerySql(req);	
			List<Map<String, Object>> getQData=this.doQueryData(sql, null);
			int totalRecords=0;								//总笔数
			int totalPages=0;	
			res.setDatas(new ArrayList<DCP_WarningLogQueryRes.level1Elm>());
			if (getQData != null && getQData.isEmpty() == false) 
			{
				String num = getQData.get(0).get("NUM").toString();
				totalRecords=Integer.parseInt(num);
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
			  //单头主键字段
				Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
				condition.put("ID", true);	
				List<Map<String, Object>> getQHeader=MapDistinct.getMap(getQData, condition);
				condition.put("OPNO", true);	
				List<Map<String, Object>> getQData_pushMan=MapDistinct.getMap(getQData, condition);
				
				for (Map<String, Object> oneData : getQHeader) 
				{
					DCP_WarningLogQueryRes.level1Elm oneLv1 = res.new level1Elm();
					oneLv1.setPushManList(new ArrayList<DCP_WarningLogQueryRes.level2PushMan>());
					String billId = oneData.get("ID").toString();
					String billNo = oneData.get("BILLNO").toString();	
					oneLv1.setBatchNo(billId);
					oneLv1.setBillNo(billNo);	
					oneLv1.setBillName(oneData.get("BILLNAME").toString());				
					oneLv1.setWarningType(oneData.get("WARNINGTYPE").toString());
					oneLv1.setWarningItemDescription(oneData.get("WARNINGITEMDESCRIPTION").toString());								
					//oneLv1.setPushTimeType(oneData.get("PUSHTIMETYPE").toString());
					oneLv1.setPushTimeTypeDescription(oneData.get("PUSHTIMEDESCRIPTION").toString());
				  oneLv1.setPushMan(oneData.get("PUSHMAN").toString());
				  oneLv1.setPushTime(oneData.get("PUSHTIME").toString());
					oneLv1.setTemplateType(oneData.get("TEMPLATETYPE").toString());
					oneLv1.setTemplateTypeTitle(oneData.get("TEMPLATETITLE").toString());
					oneLv1.setMsgBegin(oneData.get("MSGBEGIN").toString());
					oneLv1.setMsgMiddle(oneData.get("MSGMIDDLE").toString());
					oneLv1.setMsgEnd(oneData.get("MSGEND").toString());
					oneLv1.setLinkUrl(oneData.get("LINKURL").toString());
				
					
					//
					for (Map<String, Object> oneData_detail : getQData_pushMan)
					{
						String billId_detail = oneData_detail.get("ID").toString();
						String billNo_detail = oneData_detail.get("BILLNO").toString();
						String opNo = oneData_detail.get("OPNO").toString();
						String serialNo = oneData_detail.get("SERIALNO").toString();
						String opName = oneData_detail.get("OPNAME").toString();
						if(opNo!=null&&opNo.isEmpty()==false&&billId_detail.equals(billId))
						{
							DCP_WarningLogQueryRes.level2PushMan oneLv2PushMan = res.new level2PushMan();
							oneLv2PushMan.setPushWayList(new ArrayList<DCP_WarningLogQueryRes.level3PushWay>(){});
							oneLv2PushMan.setOpNo(opNo);
							oneLv2PushMan.setOpName(opName);
							oneLv2PushMan.setSerialNo(serialNo);
							
							for (Map<String, Object> map_pushWay : getQData) 
							{
								String billId_detail_pushWay = map_pushWay.get("ID").toString();
								String billNo_detail_pushWay = map_pushWay.get("BILLNO").toString();
								String opNo_pushWay = map_pushWay.get("PUSHWAY_OPNO").toString();
								String serialNo_pushWay= map_pushWay.get("PUSHWAY_SERIALNO").toString();
								String pushWay_pushWay = map_pushWay.get("PUSHWAY_PUSHWAY").toString();
								if(opNo_pushWay!=null&&opNo_pushWay.isEmpty()==false&&billId_detail_pushWay.equals(billId_detail)&&opNo_pushWay.equals(opNo))
								{
									DCP_WarningLogQueryRes.level3PushWay oneLv3PushWay = res.new level3PushWay();
									oneLv3PushWay.setSerialNo(serialNo_pushWay);
									oneLv3PushWay.setPushWay(pushWay_pushWay);
									oneLv3PushWay.setEmail(map_pushWay.get("EMAIL").toString());
									oneLv3PushWay.setUserId(map_pushWay.get("USERID").toString());
									oneLv3PushWay.setUserName(map_pushWay.get("USERNAME").toString());
									oneLv3PushWay.setSuccessFlag(map_pushWay.get("PUSHFLAG").toString());
									oneLv3PushWay.setFailReason(map_pushWay.get("FAILMSG").toString());
									
									oneLv2PushMan.getPushWayList().add(oneLv3PushWay);
									
								}								
				
							}
							
							oneLv1.getPushManList().add(oneLv2PushMan);
							
						}
					}
					res.getDatas().add(oneLv1);
					
				}
			}
			else
			{	
				totalRecords = 0;
				totalPages = 0;			
			}
			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);
			return res;					
	
		} 
		catch (Exception e) 
		{
	
	
		}
		
		
	return null;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	// TODO Auto-generated method stub
	
	}

	@Override
	protected String getQuerySql(DCP_WarningLogQueryReq req) throws Exception {
	// TODO Auto-generated method stub
		String eId = req.geteId();
		String langType = req.getLangType();
		level1Elm requestModel =	req.getRequest();		
		String warningType = requestModel.getWarningType();//枚举: all：全部,order：零售单,point：会员积分,card：储值卡
		String billId = requestModel.getBatchNo();//主键ID
		String billNo = requestModel.getBillNo();
		String beginDate = requestModel.getBeginDate();
		String endDate = requestModel.getEndDate();
		String keyTxt = requestModel.getSearchString();
		String sql= "";
		StringBuffer sqlbuf=new StringBuffer("");
		//分页处理
		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();
		int startRow=(pageNumber-1) * pageSize;
		int endRow = startRow+pageSize;
		
		sqlbuf.append("select * from (");	
		sqlbuf.append(" select count(distinct a.id) over() num, dense_rank() over (order by a.pushtime desc, a.id desc ) rn,a.*,"
			+ " b.serialno ,b.opno,b.opname,");
		sqlbuf.append(" c.serialno as pushway_serialno,c.opno as pushway_opno,c.pushway as pushway_pushway,c.pushwayname,c.mobilephone,c.email,c.userid,c.username,c.pushflag,c.failmsg");
		sqlbuf.append(" from dcp_warninglog a left join dcp_warninglog_pushman b on a.eid=b.eid and a.id=b.id");
		sqlbuf.append(" left join dcp_warninglog_pushway c on b.eid=c.eid and b.id=c.id and b.opno=c.opno ");
		sqlbuf.append(" where a.eid='"+eId+"'");
		if (warningType != null && warningType.trim().isEmpty() == false)
		{
			if(!warningType.equals("all"))
			{
				sqlbuf.append(" and a.warningtype='"+warningType+"'");
			}						
		}
		if (billId != null && billId.trim().isEmpty() == false)
		{		
				sqlbuf.append(" and a.id='"+billId+"'");									
		}
		if (billNo != null && billNo.trim().isEmpty() == false)
		{
			sqlbuf.append(" and a.billno='"+billNo+"'");
			
		}
		if (keyTxt != null && keyTxt.trim().isEmpty() == false)
		{
			sqlbuf.append(" and (a.billno like '%%"+keyTxt+"%%' or a.billname like '%%"+keyTxt+"%%')");
		}
		
		sqlbuf.append(" order by a.pushtime desc, a.id desc,b.serialno asc");
		
		sqlbuf.append(" ) aa where aa.rn>"+startRow+" and aa.rn<="+endRow);

		sql = sqlbuf.toString();
		return sql;
	}

}
