package com.dsc.spos.waimai;

import com.dsc.spos.dao.*;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.dsc.spos.waimai.entity.orderStatusLog;
import org.json.JSONObject;

import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

public class WMSGShippingExceptionService extends SWaimaiBasicService {

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
			//配送异常
			String orderNo = obj.get("order_id").toString();//订单号
			String exception_reason = obj.get("exception_reason").toString();//配送员
			String loadDocType = orderLoadDocType.MTSG;
			String eId = "99";
			String shopNo = "";
			String channelId = "";
			String loadDocOrderNo = "";
			String loadDocBillType = "";

			HelpTools.writelog_waimai("【MTSG配送异常】原因:"+exception_reason+",订单号orderNo:"+orderNo);

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
				String o_opName = "";
				

				onelv1.setOpName(o_opName);
				onelv1.setOpNo(opNO);				
				onelv1.setShopNo(shopNo);
				onelv1.setOrderNo(orderNo);
				onelv1.setMachShopNo(shopNo);
				onelv1.setShippingShopNo(shopNo);
				String statusType = "2";//配送状态
				String updateStaus = "4";
							
				onelv1.setStatusType(statusType);
				onelv1.setStatus(updateStaus);
				StringBuilder statusTypeNameObj = new StringBuilder();
				String statusName = HelpTools.GetOrderStatusName(statusType, updateStaus, statusTypeNameObj);
				String statusTypeName = statusTypeNameObj.toString();
				onelv1.setStatusTypeName(statusTypeName);
				onelv1.setStatusName(statusName);
				
				String memo = "配送异常<br>"+exception_reason;
				onelv1.setMemo(memo);
				onelv1.setDisplay("1");

				String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
				onelv1.setUpdate_time(updateDatetime);

				orderStatusLogList.add(onelv1);
				
				StringBuilder errorStatusLogMessage = new StringBuilder();
				HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogList, errorStatusLogMessage);
				this.pData.clear();							
				//endregion

			}
			catch (Exception  e)
			{

			}
			//endregion


		}
		catch (Exception e) 
		{
			HelpTools.writelog_waimai("【MTSG执行语句】异常："+e.getMessage()+ "\r\n req请求内容:" + req);
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
