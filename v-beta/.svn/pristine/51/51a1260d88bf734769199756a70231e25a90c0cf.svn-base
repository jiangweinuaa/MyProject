package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderStatusUpdateJhReq;
import com.dsc.spos.json.cust.res.DCP_OrderStatusUpdateJhRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.orderStatusLog;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：DCP_OrderStatusUpdateJh
 * 服务说明：订单状态修改
 * @author jinzma 
 * @since  2020-10-30
 */
public class DCP_OrderStatusUpdateJh extends SPosAdvanceService<DCP_OrderStatusUpdateJhReq,DCP_OrderStatusUpdateJhRes >{
	Logger logger = LogManager.getLogger(DCP_AdjustCreate.class.getName());
	@Override
	protected void processDUID(DCP_OrderStatusUpdateJhReq req, DCP_OrderStatusUpdateJhRes res) throws Exception {
		// TODO 自动生成的方法存根
		
		try 
		{
			String eId = req.getRequest().geteId();
			String orderNo = req.getRequest().getOrderNo();
			String opNo = "";
			String opName = "";
			String status = req.getRequest().getStatus();
			
			String update_time = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());

			HelpTools.writelog_waimai("DCP2.0调用【DCP_OrderStatusUpdateJh订单状态修改接口】传入的eId="+eId+",订单号orderNo="+orderNo+",修改状态status="+status);
			String sql = " select * from dcp_order where eid='"+eId+"' and orderno='"+orderNo+"' ";
			HelpTools.writelog_waimai("DCP2.0调用【DCP_OrderStatusUpdateJh订单状态修改接口】查询sql="+sql+",订单号orderNo="+orderNo);
			List<Map<String, Object>> getQData = this.doQueryData(sql, null);
			if (getQData != null && getQData.isEmpty() == false)
			{

				/*if (status.equals("12"))
				{
					logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******订单状态修改失败," + "订单单号："+orderNo+"已退单，不允许修改 ！" + "******\r\n");
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"订单单号："+orderNo+"已退单，不允许修改 ！");
				}
				if (status.equals("3"))
				{
					logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******订单状态修改失败," + "订单单号："+orderNo+"已取消，不允许修改 ！" + "******\r\n");
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"订单单号："+orderNo+"已取消，不允许修改 ！");
				}
				if (status.equals("11"))
				{
					logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******订单状态修改失败," + "订单单号："+orderNo+"已完成，不允许修改 ！" + "******\r\n");
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"订单单号："+orderNo+"已完成，不允许修改 ！");
				}*/

				////修改订单单头
				UptBean ub = new UptBean("DCP_ORDER");
			
				ub.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));				
				if (status.equals("12"))
				{
					ub.addUpdateValue("REFUNDSTATUS", new DataValue("6", Types.VARCHAR));
				}
					
	            ub.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
				ub.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));

				ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				ub.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(ub));				
				this.doExecuteDataToDB();
				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");
				HelpTools.writelog_waimai("DCP2.0调用【DCP_OrderStatusUpdateJh订单状态修改接口】更新状态成功！,订单号orderNo="+orderNo);
							
			    //插入订单日志表   
				List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
				orderStatusLog onelv1 = new orderStatusLog();
				onelv1.setLoadDocType(getQData.get(0).get("LOADDOCTYPE").toString());
				onelv1.setChannelId(getQData.get(0).get("CHANNELID").toString());
				onelv1.setLoadDocBillType(getQData.get(0).get("LOADDOCBILLTYPE").toString());
				onelv1.setLoadDocOrderNo(getQData.get(0).get("LOADDOCORDERNO").toString());
				onelv1.seteId(eId);
				
				

				onelv1.setOpName(opName);
				onelv1.setOpNo(opNo);				
				onelv1.setShopNo(getQData.get(0).get("SHOP").toString());
				onelv1.setShopName(getQData.get(0).get("SHOPNAME").toString());
				onelv1.setOrderNo(orderNo);
				onelv1.setMachShopNo("");
				onelv1.setShippingShopNo("");
				String statusType = "1";
				String updateStaus = status;					
				onelv1.setStatusType(statusType);
				onelv1.setStatus(updateStaus);
				StringBuilder statusTypeNameObj = new StringBuilder();
				String statusName = HelpTools.GetOrderStatusName(statusType, updateStaus, statusTypeNameObj);
				String statusTypeName = statusTypeNameObj.toString();
				onelv1.setStatusTypeName(statusTypeName);
				onelv1.setStatusName(statusName);

				String memo = "";
				memo += statusName;
				onelv1.setMemo(memo);
				onelv1.setDisplay("1");

				String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
				onelv1.setUpdate_time(updateDatetime);

				orderStatusLogList.add(onelv1);
				
				StringBuilder errorStatusLogMessage = new StringBuilder();
				boolean nRet = HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogList, errorStatusLogMessage);
				if (nRet) {
					HelpTools.writelog_waimai("【写表dcp_orderStatuslog保存成功】" + " 订单号orderNO:" + orderNo);
				} else {
					HelpTools.writelog_waimai(
							"【写表dcp_orderStatuslog异常】" + errorStatusLogMessage + " 订单号orderNO:" + orderNo);
				}
				this.pData.clear();

				
				
			}
			else
			{
				HelpTools.writelog_waimai("DCP2.0调用【DCP_OrderStatusUpdateJh订单状态修改接口】订单不存在！" + "订单单号："+orderNo);
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"订单单号："+orderNo+"不存在 ！");
			}

		} catch (Exception e) {
			// TODO: handle exception
			
			HelpTools.writelog_waimai("DCP2.0调用【DCP_OrderStatusUpdateJh订单状态修改接口】异常:"+ e.getMessage());
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());	
		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderStatusUpdateJhReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderStatusUpdateJhReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderStatusUpdateJhReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderStatusUpdateJhReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String status = req.getRequest().getStatus();
	

		if(req.getRequest()==null)
	    {
	    	errMsg.append("requset不能为空值 ");
	    	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
	    }

		if (Check.Null(status))
		{
			errMsg.append("订单修改状态不可为空值, ");
			isFail = true;
		}
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;

	}

	@Override
	protected TypeToken<DCP_OrderStatusUpdateJhReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_OrderStatusUpdateJhReq>(){};
	}

	@Override
	protected DCP_OrderStatusUpdateJhRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_OrderStatusUpdateJhRes();
	}

}
