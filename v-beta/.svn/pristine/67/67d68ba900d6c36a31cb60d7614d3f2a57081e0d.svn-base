package com.dsc.spos.waimai;

import com.dsc.spos.dao.*;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.dsc.spos.waimai.entity.orderStatusLog;
import org.json.JSONObject;

import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

public class WMSGShippingService extends SWaimaiBasicService {

	@Override
	public String execute(String json) throws Exception {
		// TODO Auto-generated method stub
		String res_json = HelpTools.GetSGMTShippingResponse(json);
		if(res_json ==null ||res_json.length()==0)
		{
			return null;
		}
		Map<String, Object> res = new HashMap<String, Object>();
		this.processDUID(res_json, res);
		return null;
	}

	@Override
	protected void processDUID(String req, Map<String, Object> res) throws Exception {
		// TODO Auto-generated method stub
		try 
		{
			JSONObject obj = new JSONObject(req);
			//配送状态 ：0配送单发往配送、5	已经分配骑手，等待骑手接单、10配送单已确认(骑手接单)、15骑手已到店、20骑手已取餐、40骑手已送达、100配送单已取消
			String shippingStatus = obj.get("logistics_status").toString();
			String orderNo = obj.get("order_id").toString();//订单号
			String dispatcherName = obj.get("dispatcher_name").toString();//配送员
			String dispatcherMobile = obj.get("dispatcher_mobile").toString();//配送员电话
			String loadDocType = orderLoadDocType.MTSG;
			String eId = "99";
			String shopNo = "";
			String channelId = "";
			String loadDocOrderNo = "";
			String loadDocBillType = "";
			
			

			String deliveryStatus = "0";//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 
			if(shippingStatus.equals("10"))//10配送单已确认(骑手接单)
			{
				deliveryStatus = "1";
			}
			else if(shippingStatus.equals("15"))//15骑手已到店
			{
				deliveryStatus = "6";
			}
			else if(shippingStatus.equals("20"))//20骑手已取餐
			{
				deliveryStatus = "2";
			}
			else if(shippingStatus.equals("40"))//40骑手已送达
			{
				deliveryStatus = "3";
			}
			else if(shippingStatus.equals("100"))//100配送单已取消
			{
				deliveryStatus = "4";
			}

			UptBean ub1 = null;	
			ub1 = new UptBean("DCP_ORDER");
			ub1.addUpdateValue("DELIVERYSTATUS", new DataValue(deliveryStatus,Types.VARCHAR));
			ub1.addUpdateValue("DELNAME", new DataValue(dispatcherName,Types.VARCHAR));
			if (dispatcherMobile != null && dispatcherMobile.trim().length() > 0)
			 {
				ub1.addUpdateValue("DELTELEPHONE", new DataValue(dispatcherMobile,Types.VARCHAR));
			 }
			
			ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
			ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

			ub1.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
			ub1.addCondition("LOADDOCTYPE", new DataValue(loadDocType, Types.VARCHAR));							
			this.addProcessData(new DataProcessBean(ub1));

			this.doExecuteDataToDB();	
			HelpTools.writelog_waimai("【MT更新配送状态DeliveryStutas成功】"+" 订单号orderNo:"+orderNo+" 配送状态DeliveryStutas="+deliveryStatus);
			this.pData.clear();

			String sql = "select * from dcp_order where LOADDOCTYPE='"+loadDocType+"' and orderno='"+orderNo+"' and rownum=1";
			List<Map<String, Object>> getQDataDetail=this.doQueryData(sql,null);
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
			{			
				eId = getQDataDetail.get(0).get("EID").toString();				
				shopNo = getQDataDetail.get(0).get("SHOP").toString();
				channelId = getQDataDetail.get(0).get("CHANNELID").toString();
				loadDocBillType = getQDataDetail.get(0).get("LOADDOCBILLTYPE").toString();
				loadDocOrderNo = getQDataDetail.get(0).get("LOADDOCORDERNO").toString();
			}
			
			if (eId == null || eId.isEmpty())
			{
				eId = "99";
			}
			
			if (loadDocOrderNo == null || loadDocOrderNo.isEmpty())
			{
				loadDocOrderNo = orderNo;
			}

			//region 写日志
			try
			{						
				List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
				orderStatusLog onelv1 = new orderStatusLog();
				onelv1.setLoadDocType(loadDocType);
				onelv1.setChannelId(channelId);
				onelv1.setLoadDocBillType(loadDocBillType);
				onelv1.setLoadDocOrderNo(loadDocOrderNo);
				onelv1.seteId(eId);
				String opNO = "";				
				String o_opName = "骑士：" + dispatcherName;
				

				onelv1.setOpName(o_opName);
				onelv1.setOpNo(opNO);				
				onelv1.setShopNo(shopNo);
				onelv1.setOrderNo(orderNo);
				onelv1.setMachShopNo(shopNo);
				onelv1.setShippingShopNo(shopNo);
				String statusType = "2";//配送状态
				String updateStaus = deliveryStatus;
							
				onelv1.setStatusType(statusType);
				onelv1.setStatus(updateStaus);
				StringBuilder statusTypeNameObj = new StringBuilder();
				String statusName = HelpTools.GetOrderStatusName(statusType, updateStaus, statusTypeNameObj);
				String statusTypeName = statusTypeNameObj.toString();
				onelv1.setStatusTypeName(statusTypeName);
				onelv1.setStatusName(statusName);
				
				String memo = "";
				memo += statusName;

				if (dispatcherMobile != null && dispatcherMobile.isEmpty() == false)
				{
					memo += " 配送电话-->" + dispatcherMobile;
				}
				onelv1.setMemo(memo);
				onelv1.setDisplay("1");

				String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
				onelv1.setUpdate_time(updateDatetime);

				orderStatusLogList.add(onelv1);
				
				StringBuilder errorStatusLogMessage = new StringBuilder();
				boolean nRet = HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogList, errorStatusLogMessage);
				if (nRet) {
					HelpTools.writelog_waimai("【写表tv_orderStatuslog保存成功】" + " 订单号orderNO:" + orderNo);
				} else {
					HelpTools.writelog_waimai(
							"【写表tv_orderStatuslog异常】" + errorStatusLogMessage.toString() + " 订单号orderNO:" + orderNo);
				}
				this.pData.clear();							
				//endregion

			}
			catch (Exception  e)
			{

			}
			//endregion


		} 
		catch (SQLException e) 
		{
			HelpTools.writelog_waimai("【MT执行语句】异常："+e.getMessage()+ "\r\n req请求内容:" + req);		
		}
		catch (Exception e) 
		{
			HelpTools.writelog_waimai("【MT执行语句】异常："+e.getMessage()+ "\r\n req请求内容:" + req);		
		}

	}

	@Override
	protected List<InsBean> prepareInsertData(String req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(String req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(String req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
