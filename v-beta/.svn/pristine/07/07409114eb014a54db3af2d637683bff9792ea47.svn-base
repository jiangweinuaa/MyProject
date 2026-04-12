package com.dsc.spos.service.imp.json;
import java.util.*;
import java.sql.Types;
import java.text.SimpleDateFormat;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.dao.DataValue.DataExpression;
import com.dsc.spos.json.cust.req.DCP_OrderPrintUpdateReq;
import com.dsc.spos.json.cust.req.DCP_OrderPickUpGoodsUpdateReq.order;
import com.dsc.spos.json.cust.res.DCP_OrderPrintUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.orderStatusLog;
import com.google.gson.reflect.TypeToken;

/**
 * 订单打印次数更新
 * @author yuanyy 2019-12-24
 *
 */
public class DCP_OrderPrintUpdate extends SPosAdvanceService<DCP_OrderPrintUpdateReq, DCP_OrderPrintUpdateRes> {

	@Override
	protected void processDUID(DCP_OrderPrintUpdateReq req, DCP_OrderPrintUpdateRes res) throws Exception 
	{
		try 
		{
			List<DCP_OrderPrintUpdateReq.Order> orderList = req.getRequest().getOrderList();

			for (DCP_OrderPrintUpdateReq.Order order : orderList)
			{
				UptBean ub1 = null;
				ub1 = new UptBean("DCP_ORDER");
				ub1.addUpdateValue("PRINTCOUNT", new DataValue(1, Types.VARCHAR,DataExpression.UpdateSelf));
                ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));


				// condition
				ub1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
				ub1.addCondition("ORDERNO", new DataValue(order.getOrderNo(), Types.VARCHAR));

				this.addProcessData(new DataProcessBean(ub1));
			}

			this.doExecuteDataToDB();
			//写下订单历程
            String eId = req.geteId();
            String opNo = req.getOpNO();
            String opName = req.getOpName();
            String opShopId = req.getShopId();
            String opShopName = req.getShopName();
            String sql = "";
            for (DCP_OrderPrintUpdateReq.Order order : orderList)
            {
                sql = "";
                String orderNo = order.getOrderNo();
                sql = "select SHOP,LOADDOCTYPE,CHANNELID,PRINTCOUNT from dcp_order where eId='"+eId+"' and orderNo='"+orderNo+"'";
                List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);
                if (getQDataDetail==null||getQDataDetail.isEmpty())
                {
                    continue;
                }
                String shop = getQDataDetail.get(0).getOrDefault("SHOP","").toString();
                String loadDocType = getQDataDetail.get(0).getOrDefault("LOADDOCTYPE","").toString();
                String channelId = getQDataDetail.get(0).getOrDefault("CHANNELID","").toString();
                String printCount = getQDataDetail.get(0).getOrDefault("PRINTCOUNT","0").toString();
                int printCount_int = 0;
                try {
                    printCount_int = Integer.parseInt(printCount);
                }
                catch (Exception e)
                {

                }

                try
                {
                    List<orderStatusLog> orderStatusLogErrorList = new ArrayList<orderStatusLog>();
                    orderStatusLog onelv1 = new orderStatusLog();

                    onelv1.setLoadDocType(loadDocType);
                    onelv1.setChannelId(channelId);

                    onelv1.setNeed_callback("N");
                    onelv1.setNeed_notify("N");

                    onelv1.seteId(eId);
                    onelv1.setOpNo(opNo);
                    onelv1.setOpName(opName);
                    onelv1.setOrderNo(orderNo);
                    onelv1.setLoadDocBillType("");
                    onelv1.setLoadDocOrderNo(orderNo);

                    String statusType_log = "99";// 其他状态
                    String updateStaus_log = "99";// 订单修改

                    onelv1.setStatusType(statusType_log);
                    onelv1.setStatus(updateStaus_log);
                    String statusName_log = "订单打印";
                    String statusTypeName_log = "其他状态";
                    onelv1.setStatusTypeName(statusTypeName_log);
                    onelv1.setStatusName(statusName_log);

                    StringBuffer memo = new StringBuffer("");
                    memo.append( statusTypeName_log + "-->" + statusName_log + "<br>");
                    memo.append( "操作打印的门店-->(" + opShopId+")"+opShopName+ "<br>");
                    memo.append( "打印次数-->第"+printCount_int+"次");
                    onelv1.setMemo(memo.toString());
                    String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                    onelv1.setUpdate_time(updateDatetime);
                    orderStatusLogErrorList.add(onelv1);

                    StringBuilder errorMessage = new StringBuilder();
                    HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogErrorList, errorMessage);

                }
                catch (Exception e)
                {
                    continue;
                }

            }

		} 
		catch (Exception e) 
		{
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败！");
		}



	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderPrintUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderPrintUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderPrintUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderPrintUpdateReq req) throws Exception {
		// TODO Auto-generated method stub

		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0; // 考慮當錯誤很多時則直接顯示格式錯誤；

		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		List<DCP_OrderPrintUpdateReq.Order> orderList = req.getRequest().getOrderList();

		for (DCP_OrderPrintUpdateReq.Order order : orderList)
		{
			if (Check.Null(order.getOrderNo())) {
				errCt++;
				errMsg.append("订单单号不可为空值, ");
				isFail = true;
			}
		}

		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderPrintUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderPrintUpdateReq>(){};
	}

	@Override
	protected DCP_OrderPrintUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderPrintUpdateRes();
	}

}
