package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_WarningQueryReq;
import com.dsc.spos.json.cust.req.DCP_WarningQueryReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_WarningQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_WarningQuery extends SPosBasicService<DCP_WarningQueryReq, DCP_WarningQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_WarningQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return false;
	}

	@Override
	protected TypeToken<DCP_WarningQueryReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_WarningQueryReq>(){};
	}

	@Override
	protected DCP_WarningQueryRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_WarningQueryRes();
	}

	@Override
	protected DCP_WarningQueryRes processJson(DCP_WarningQueryReq req) throws Exception {
	// TODO Auto-generated method stub
		try 
		{
			DCP_WarningQueryRes res = this.getResponse();
			String sql=this.getQuerySql(req);	
			List<Map<String, Object>> getQData=this.doQueryData(sql, null);
			int totalRecords=0;								//总笔数
			int totalPages=0;	
			res.setDatas(new ArrayList<DCP_WarningQueryRes.level1Elm>());
			if (getQData != null && getQData.isEmpty() == false) 
			{
				String num = getQData.get(0).get("NUM").toString();
				totalRecords=Integer.parseInt(num);
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
			  //单头主键字段
				Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
				condition.put("BILLNO", true);	
				List<Map<String, Object>> getQHeader=MapDistinct.getMap(getQData, condition);
				for (Map<String, Object> oneData : getQHeader) 
				{
					DCP_WarningQueryRes.level1Elm oneLv1 = res.new level1Elm();
					oneLv1.setPushManList(new ArrayList<DCP_WarningQueryRes.level2PushMan>());
					String billNo = oneData.get("BILLNO").toString();
					oneLv1.setBillDate(oneData.get("BILLDATE").toString());
					oneLv1.setBillName(oneData.get("BILLNAME").toString());
					oneLv1.setBillNo(billNo);
					oneLv1.setCreateopid(oneData.get("CREATEOPID").toString());
					oneLv1.setCreateTime(oneData.get("CREATETIME").toString());
					oneLv1.setEndTime(oneData.get("ENDTIME").toString());
					oneLv1.setLastmodiopid(oneData.get("LASTMODIOPID").toString());
					oneLv1.setLastmoditime(oneData.get("LASTMODITIME").toString());
					oneLv1.setOrderAmt(oneData.get("ORDERAMT").toString());
					oneLv1.setOrderQty(oneData.get("ORDERQTY").toString());
					oneLv1.setPointQty(oneData.get("POINTQTY").toString());
					oneLv1.setPushTime(oneData.get("PUSHTIME").toString());
					oneLv1.setPushTimeType(oneData.get("PUSHTIMETYPE").toString());
					oneLv1.setRestrictShop(oneData.get("RESTRICTSHOP").toString());
					oneLv1.setStartTime(oneData.get("STARTTIME").toString());
					String status = oneData.get("STATUS").toString();
					oneLv1.setStatus(status);
					oneLv1.setTemplateType(oneData.get("TEMPLATETYPE").toString());
					String warningItem = oneData.get("WARNINGITEM").toString();
					String warningItemDescription = "";
					if(warningItem.equals("order_inTime"))
					{
						warningItemDescription ="每日%点后的零售单";
						warningItemDescription = warningItemDescription.replace("%",oneData.get("STARTTIME").toString());
						
					}
					else if (warningItem.equals("order_value")) 
					{
						warningItemDescription ="每日单笔金额超过%元的零售单";		
						warningItemDescription = warningItemDescription.replace("%",oneData.get("ORDERAMT").toString());
					}
					else if (warningItem.equals("order_period")) 
					{
						warningItemDescription ="每日A%点至B%点的零售单";		
						warningItemDescription = warningItemDescription.replace("A%",oneData.get("STARTTIME").toString());
						warningItemDescription = warningItemDescription.replace("B%",oneData.get("ENDTIME").toString());
					}
					else if (warningItem.equals("point_sum")) 
					{
						warningItemDescription ="同一会员的每日交易单超过%笔";		
						warningItemDescription = warningItemDescription.replace("%",oneData.get("ORDERQTY").toString());
					}			
					else if (warningItem.equals("point_over")) 
					{
						warningItemDescription ="同一会员的每日总积分超过%分";		
						warningItemDescription = warningItemDescription.replace("%",oneData.get("POINTQTY").toString());
					}
					else if (warningItem.equals("point_close")) 
					{
						warningItemDescription ="每日闭店后的会员积分";		
					}
					else if (warningItem.equals("card_sum")) 
					{
						warningItemDescription ="同一储值卡的每日交易数超过%笔";		
						warningItemDescription = warningItemDescription.replace("%",oneData.get("ORDERQTY").toString());
					}
					else if (warningItem.equals("card_close")) 
					{
						warningItemDescription ="每日闭店后发生的储值卡交易";		
					}
					oneLv1.setWarningItem(warningItem);
					oneLv1.setWarningItemDescription(warningItemDescription);
					oneLv1.setWarningType(oneData.get("WARNINGTYPE").toString());
					String warningStatus = "inValid";//status=0 inValid：暂停中  status=100 valid：执行中
					if(status!=null)
					{
						if(status.equals("0"))
						{
							warningStatus = "inValid";							
						}
						else if (status.equals("100")) 
						{
							warningStatus = "valid";									
						}
						
					}
					oneLv1.setWarningStatus(warningStatus);
					//
					for (Map<String, Object> oneData_detail : getQData)
					{
						String billNo_detail = oneData_detail.get("BILLNO").toString();
						String opNo = oneData_detail.get("OPNO").toString();
						String serialNo = oneData_detail.get("SERIALNO").toString();
						String opName = oneData_detail.get("OPNAME").toString();
						if(opNo!=null&&opNo.isEmpty()==false&&billNo_detail.equals(billNo))
						{
							DCP_WarningQueryRes.level2PushMan oneLv2PushMan = res.new level2PushMan();
							oneLv2PushMan.setOpNo(opNo);
							oneLv2PushMan.setOpName(opName);
							oneLv2PushMan.setSerialNo(serialNo);
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
	protected String getQuerySql(DCP_WarningQueryReq req) throws Exception {
	// TODO Auto-generated method stub
		String eId = req.geteId();
		String langType = req.getLangType();
		level1Elm requestModel =	req.getRequest();		
		String warningType = requestModel.getWarningType();//枚举: null：全部,order：零售单,point：会员积分,card：储值卡
		String warningStatus = requestModel.getWarningStatus();//枚举: null：全部,valid：执行中,inValid：暂停中
		
		String billNo = requestModel.getBillNo();
		String keyTxt = requestModel.getSearchString();
		String sql= "";
		StringBuffer sqlbuf=new StringBuffer("");
		//分页处理
		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();
		int startRow=(pageNumber-1) * pageSize;
		int endRow = startRow+pageSize;
		
		sqlbuf.append("select * from (");	
		sqlbuf.append(" select count(DISTINCT a.billno) over() num,dense_rank() over(ORDER BY a.billno desc) rn, a.billno,a.billname,a.billtype,to_char(a.billdate, 'yyyy-MM-dd') as billdate,a.warningtype,a.warningitem,a.starttime,a.endtime,a.orderqty,a.orderamt,a.pointqty,a.templatetype,a.pushtimetype,a.pushtime,a.restrictshop,a.memo,a.status,"
			+ "a.createopid,a.createopname,to_char(a.createtime, 'yyyy-MM-dd hh24:mi:ss') as createtime,a.lastmodiopid,a.lastmodiopname,to_char(a.lastmoditime, 'yyyy-MM-dd hh24:mi:ss') as lastmoditime,");
		sqlbuf.append(" b.serialno,b.opno,b.mobilephone,b.email,s.op_name as opname");
		sqlbuf.append(" from dcp_warning a left join dcp_warning_pushman b on a.eid=b.eid and a.billno=b.billno");
		sqlbuf.append(" left join platform_staffs_lang s on b.opno=s.opno and s.EID='"+eId+"' and s.lang_type='"+langType+"'");
		sqlbuf.append(" where a.eid='"+eId+"'");
		if (warningType != null && warningType.trim().isEmpty() == false)
		{
			if(!warningType.toLowerCase().equals("all"))
			{
				sqlbuf.append(" and a.warningtype='"+warningType+"'");
			}						
		}
		if (warningStatus != null && warningStatus.trim().isEmpty() == false)
		{
			if(!warningStatus.toLowerCase().equals("all"))
			{
				
				String status = "0";//status=0 inValid：暂停中  status=100 valid：执行中
				if(warningStatus.equals("valid"))
				{
					status = "100";
				}
				else if (warningStatus.equals("inValid")) 
				{
					status = "0";			
				}
				sqlbuf.append(" and a.status='"+status+"'");				
			}
			
		}
		if (billNo != null && billNo.trim().isEmpty() == false)
		{
			sqlbuf.append(" and a.billno='"+billNo+"'");
			
		}
		if (keyTxt != null && keyTxt.trim().isEmpty() == false)
		{
			sqlbuf.append(" and (a.billno like '%%"+keyTxt+"%%' or a.billname like '%%"+keyTxt+"%%')");
		}
		
		sqlbuf.append(" order by a.billno desc,b.serialno asc");
		
		sqlbuf.append(" ) aa where aa.rn>"+startRow+" and aa.rn<="+endRow);

		sql = sqlbuf.toString();
		return sql;
	}

}
