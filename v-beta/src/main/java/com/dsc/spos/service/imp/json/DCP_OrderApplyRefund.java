package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderApplyRefundReq;
import com.dsc.spos.json.cust.res.DCP_OrderApplyRefundRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.order;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.dsc.spos.waimai.entity.orderRedisKeyInfo;
import com.dsc.spos.waimai.entity.orderStatusLog;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderApplyRefund extends SPosAdvanceService<DCP_OrderApplyRefundReq, DCP_OrderApplyRefundRes>
{

	@Override
	protected void processDUID(DCP_OrderApplyRefundReq req, DCP_OrderApplyRefundRes res) throws Exception
	{
		// TODO Auto-generated method stub
		/*************必传的节点******************/
		String eId_para = req.getRequest().geteId();//请求传入的eId		
		//退单类型 0：全退
		String refundType = req.getRequest().getRefundType();
		String orderNo = req.getRequest().getOrderNo();
		
		String refundReasonNo =  req.getRequest().getRefundReasonNo();
		String refundReasonName =  req.getRequest().getRefundReasonName();
		String refundReason = req.getRequest().getRefundReason();
		String opNo = req.getRequest().getOpNo();
		String opName = req.getRequest().getOpName();
		
		 if (refundType.equals("0")==false) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "退单类型"+refundType+"暂不支持");
		}
		
		 String sql = "select * from dcp_order where eid='"+eId_para+"' and orderno='"+orderNo+"' ";
			HelpTools.writelog_waimai("【调用订单申请退单DCP_OrderApplyRefund】单号orderNo="+orderNo+"  查询语句："+sql);
			List<Map<String, Object>> getQHead = this.doQueryData(sql, null);
			if(getQHead==null||getQHead.isEmpty())
			{
				
				HelpTools.writelog_waimai("【调用订单退单DCP_OrderApplyRefund】查询完成,单号orderNo="+orderNo+"该订单不存在！");
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该订单不存在！");
			}
			String status = getQHead.get(0).get("STATUS").toString();
			String refundStatus = getQHead.get(0).get("REFUNDSTATUS").toString();
			HelpTools.writelog_waimai("【调用订单申请退单DCP_OrderApplyRefund】查询完成,单号orderNo="+orderNo+" 状态status="+status);
			if(status.equals("3"))
			{			
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该订单状态为已取消！");
			}
			if(status.equals("12"))
			{				
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "订单状态为已退单！");				
			}
			
			if(refundStatus.equals("2"))
			{				
				res.setSuccess(true);
				res.setServiceDescription("该订单已经是申请退单状态无须再次申请！");
				res.setServiceStatus("100");
				return;
			}
				
			String loadDocType = getQHead.get(0).get("LOADDOCTYPE").toString();
			String channelId = getQHead.get(0).get("CHANNELID").toString();
			String loadDocBillType = getQHead.get(0).get("LOADDOCBILLTYPE").toString();
			String loadDocOrderNo = getQHead.get(0).get("LOADDOCORDERNO").toString();
			String shopId = getQHead.get(0).get("SHOP").toString();
			
			UptBean up1 = new UptBean("DCP_ORDER");
			up1.addCondition("EID", new DataValue(eId_para,Types.VARCHAR));
			up1.addCondition("ORDERNO", new DataValue(orderNo,Types.VARCHAR));
			
			//更新updatetime
			up1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
			up1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
			//更新订单状态 		
			up1.addUpdateValue("REFUNDSTATUS", new DataValue("2",Types.VARCHAR));
			if(refundReasonNo!=null&&refundReasonNo.isEmpty()==false)
			{
				if(refundReasonNo.length()>50)
				{
					refundReasonNo = refundReasonNo.substring(0,50);
				}
				up1.addUpdateValue("REFUNDREASONNO", new DataValue(refundReasonNo,Types.VARCHAR));
			}
			
			if(refundReasonName!=null&&refundReasonName.isEmpty()==false)
			{
				if(refundReasonName.length()>255)
				{
					refundReasonName = refundReasonName.substring(0,255);
				}
				up1.addUpdateValue("REFUNDREASONNAME", new DataValue(refundReasonName,Types.VARCHAR));
			}
			
			if(refundReason!=null&&refundReason.isEmpty()==false)
			{
				if(refundReason.length()>255)
				{
					refundReason = refundReason.substring(0,255);
				}
				up1.addUpdateValue("REFUNDREASON", new DataValue(refundReason,Types.VARCHAR));
			}
			
			this.addProcessData(new DataProcessBean(up1));

			this.doExecuteDataToDB();
			res.setSuccess(true);
			
			//写日志
			try
			{
				List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
				orderStatusLog onelv1 = new orderStatusLog();
				onelv1.setLoadDocType(loadDocType);
				onelv1.setChannelId(channelId);
				onelv1.setLoadDocBillType(loadDocBillType);
				onelv1.setLoadDocOrderNo(loadDocOrderNo);
				onelv1.seteId(eId_para);
				
				

				onelv1.setOpName(opName);
				onelv1.setOpNo(opNo);				
				onelv1.setShopNo(shopId);
				onelv1.setOrderNo(orderNo);
				onelv1.setMachShopNo("");
				onelv1.setShippingShopNo("");
				String statusType = "3";
				String updateStaus = "2";					
				onelv1.setStatusType(statusType);
				onelv1.setStatus(updateStaus);
				StringBuilder statusTypeNameObj = new StringBuilder();
				String statusName = HelpTools.GetOrderStatusName(statusType, updateStaus, statusTypeNameObj);
				String statusTypeName = statusTypeNameObj.toString();
				onelv1.setStatusTypeName(statusTypeName);
				onelv1.setStatusName(statusName);

				String memo = "";
				memo += statusName;
				if(refundReason!=null&&refundReason.isEmpty()==false)
				{
					memo +="<br>退单原因:"+refundReason;
				}
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
			} catch (Exception e)
			{
				// TODO: handle exception
			}

			//写缓存
		try
		{
			if (orderLoadDocType.MINI.equals(loadDocType)||orderLoadDocType.WECHAT.equals(loadDocType)||orderLoadDocType.LINE.equals(loadDocType))
			{
				HelpTools.writelog_waimai("【商城订单】【申请退单】开始写缓存,订单号orderNo=" + orderNo);
				order dcpOrder = HelpTools.GetOrderInfoByOrderNO(StaticInfo.dao, eId_para, loadDocType, orderNo);
				StringBuffer errorMessage = new StringBuffer();
				HelpTools.writeOrderRedisByAllShop(dcpOrder,"",errorMessage);
			}
           else if (orderLoadDocType.WAIMAI.equals(loadDocType))
            {
                HelpTools.writelog_waimai("【鼎捷外卖】【申请退单】开始写缓存,订单号orderNo=" + orderNo);
                order dcpOrder = HelpTools.GetOrderInfoByOrderNO(StaticInfo.dao, eId_para, loadDocType, orderNo);
                String redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + eId_para + ":" + shopId;;
                String hash_key = dcpOrder.getOrderNo();
                ParseJson pj = new ParseJson();
                String Response_json = pj.beanToJson(dcpOrder);
                RedisPosPub redis = new RedisPosPub();
                boolean nret = redis.setHashMap(redis_key, hash_key, Response_json);
                if (nret) {
                    HelpTools.writelog_waimai("【鼎捷外卖】【申请退单】【下单门店】【写缓存】OK" + " redis_key:" + redis_key + " hash_key:" + hash_key);
                } else {
                    HelpTools.writelog_waimai("【鼎捷外卖】【申请退单】【下单门店】【写缓存】Error" + " redis_key:" + redis_key + " hash_key:" + hash_key);
                }
            }

		}
		catch (Exception e)
		{

		}


			
			
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderApplyRefundReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderApplyRefundReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderApplyRefundReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderApplyRefundReq req) throws Exception
	{
		boolean isFail = false; 
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；

		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		if (Check.Null(req.getRequest().geteId())) 
		{
			errCt++;
			errMsg.append("企业编号eId不可为空值, ");
			isFail = true;
		} 
		
		if (Check.Null(req.getRequest().getRefundType())) 
		{
			errCt++;
			errMsg.append("退单类型refundType不可为空值, ");
			isFail = true;
		} 
		
		if (Check.Null(req.getRequest().getOrderNo())) 
		{
			errCt++;
			errMsg.append("订单单号orderNo不可为空值, ");
			isFail = true;
		} 

		

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderApplyRefundReq> getRequestType()
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderApplyRefundReq>(){};
	}

	@Override
	protected DCP_OrderApplyRefundRes getResponseType()
	{
		// TODO Auto-generated method stub
		return new DCP_OrderApplyRefundRes();
	}
	
	

}
