package com.dsc.spos.service.imp.json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderStatusLogQueryReq;
import com.dsc.spos.json.cust.res.DCP_OrderStatusLogQueryRes;
import com.dsc.spos.json.cust.res.DCP_OrderStatusLogQueryRes.levelDatas;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderStatusLogQuery extends SPosAdvanceService<DCP_OrderStatusLogQueryReq,DCP_OrderStatusLogQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_OrderStatusLogQueryReq req) throws Exception {
	// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
		 if(req.getRequest()==null)
		    {
		    	errMsg.append("requset不能为空值 ");
		    	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		    }
		if (Check.Null(req.getRequest().geteId())) {
			errCt++;
			errMsg.append("企业编号eId不可为空值, ");
			isFail = true;
		} 
		
		if (Check.Null(req.getRequest().getOrderNo())) {
			errCt++;
			errMsg.append("订单单号orderNo不可为空值, ");
			isFail = true;
		} 	
		
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
	
		return false;
	}

	@Override
	protected TypeToken<DCP_OrderStatusLogQueryReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_OrderStatusLogQueryReq>(){};
	}

	@Override
	protected DCP_OrderStatusLogQueryRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_OrderStatusLogQueryRes();
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	// TODO Auto-generated method stub
	
	}

	@Override
	protected String getQuerySql(DCP_OrderStatusLogQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}
	
	@Override
	protected void processDUID(DCP_OrderStatusLogQueryReq req, DCP_OrderStatusLogQueryRes res) throws Exception 
	{		
		String eId = req.getRequest().geteId();
		String orderNo = req.getRequest().getOrderNo();
        String display = req.getRequest().getDisplay();
		
		String errorMessage = "查询条件,单号orderNO=" + orderNo;
		String sql = " select * from dcp_order_statuslog  where orderNO='"+orderNo+"'  ";			
		if (eId != null && eId.isEmpty() == false)
		{
			sql += " and EID='"+eId+"' ";
			errorMessage += " 企业编号EID：" + eId;
		}
        if (display != null && display.isEmpty() == false)
        {
            if (display.equals("1"))
            {
                sql += "  and display='1' ";
                errorMessage += " 是否显示字段display：" + display;
            }
        }
		sql +=" order by update_time asc";
		
		List<Map<String, Object>> getQDataDetail=this.doQueryData(sql,null);
		
		
		levelDatas datas = res.new levelDatas();
		
		if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
		{	
			
			datas.setOrderNo(orderNo);
			datas.setChannelId(getQDataDetail.get(0).get("CHANNELID").toString());
			datas.setLoadDocBillType(getQDataDetail.get(0).get("LOADDOCBILLTYPE").toString());
			datas.setLoadDocOrderNo(getQDataDetail.get(0).get("LOADDOCORDERNO").toString());
			datas.setLoadDocType(getQDataDetail.get(0).get("LOADDOCTYPE").toString());
			datas.setStatusList(new ArrayList<DCP_OrderStatusLogQueryRes.level1Elm>());
			
			
			for (Map<String, Object> oneData : getQDataDetail) 
			{
				DCP_OrderStatusLogQueryRes.level1Elm oneLv1 = res.new level1Elm();

				String statusType = oneData.get("STATUSTYPE").toString();
				String statusTypeName = oneData.get("STATUSTYPENAME").toString();
				String status = oneData.get("STATUS").toString();
				String statusName = oneData.get("STATUSNAME").toString();
				String opNo = oneData.get("OPNO").toString();
				String opName = oneData.get("OPNAME").toString();
				String memo = oneData.get("MEMO").toString();
				String update_time = oneData.get("UPDATE_TIME").toString();

				oneLv1.setStatus(status);
				oneLv1.setStatusName(statusName);
				oneLv1.setStatusType(statusType);
				oneLv1.setStatusTypeName(statusTypeName);
				oneLv1.setMemo(memo);
				oneLv1.setOpName(opName);
				oneLv1.setOpNo(opNo);
				oneLv1.setUpdate_time(update_time);

				SimpleDateFormat simptemp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				SimpleDateFormat allsimptemp = new SimpleDateFormat("yyyyMMddHHmmssSSS");
				String UPDATE_TIME = "";
				try
				{
					UPDATE_TIME = simptemp.format(allsimptemp.parse(update_time));

				} catch (Exception e)
				{
					UPDATE_TIME = update_time;
				}

				oneLv1.setUpdate_time(UPDATE_TIME);

				datas.getStatusList().add(oneLv1);
				oneLv1 = null;
			}
			
			res.setDatas(datas);
											
		}
		else
		{
			res.setDatas(datas);
		}
		
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");
	
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderStatusLogQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderStatusLogQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderStatusLogQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

}
