package com.dsc.spos.service.imp.json;


import com.dsc.spos.dao.*;
import com.dsc.spos.json.JsonBasicRes;
import com.dsc.spos.json.cust.req.DCP_OrderModify_YouzanRetryReq;
import com.dsc.spos.json.cust.res.DCP_OrderModify_YouzanRetryRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.thirdpart.youzan.YouZanCallBackServiceV3;
import com.dsc.spos.utils.Check;

import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;


public class DCP_OrderModify_YouzanRetry extends SPosAdvanceService<DCP_OrderModify_YouzanRetryReq, DCP_OrderModify_YouzanRetryRes> {

	String logFileName = "YouzanRetry";
	@Override
	protected void processDUID(DCP_OrderModify_YouzanRetryReq req, DCP_OrderModify_YouzanRetryRes res) throws Exception
	{
		String eId = req.getRequest().geteId();
        if(eId==null||eId.isEmpty())
        {
            eId = req.geteId();
        }
        if (eId==null||eId.isEmpty())
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "获取eId失败，");
        }
        req.seteId(eId);
        req.getRequest().seteId(eId);

		String loadDocType = orderLoadDocType.YOUZAN;

		YouZanCallBackServiceV3 ycb=new YouZanCallBackServiceV3();
		//传了单号，就不管了，直接处理
		if (!Check.Null(req.getRequest().getOrderNo()))
		{
			String orderNo = req.getRequest().getOrderNo();
			String sql_one = "select * from DCP_order where EID='" + eId + "' and orderno='" + orderNo + "'";
			List<Map<String, Object>> getQData = this.doQueryData(sql_one, null);
			if (getQData == null || getQData.isEmpty())
			{
				res.setSuccess(false);
				res.setServiceStatus("100");
				res.setServiceDescription("该订单不存在");
				HelpTools.writelog_fileName("查询返回该订单不存在！ 单号orderNo=" + orderNo,logFileName);
				return;
			}
			String orderStatus = getQData.get(0).getOrDefault("STATUS","").toString();
			String shippingshop = getQData.get(0).getOrDefault("SHIPPINGSHOP","").toString();
			if (!"11".equals(orderStatus))
			{
				res.setSuccess(false);
				res.setServiceStatus("100");
				res.setServiceDescription("该订单状态非已完成不能处理");
				HelpTools.writelog_fileName("查询返回订单状态非已完成不能处理,订单状态status="+orderStatus+",单号orderNo=" + orderNo,logFileName);
				return;
			}
			JsonBasicRes thisRes=new JsonBasicRes();
			Map<String, Object> otherMap = new HashMap<String, Object>();
			//otherMap.put("extra_info", otpReq.getOpOpNo()==null?otpReq.getOpShopId():otpReq.getOpOpNo());//操作人
			try {
				thisRes=ycb.OrderToSaleRetry(eId, orderNo, shippingshop,otherMap,null);
			}
			catch (Exception e)
			{
				thisRes.setServiceDescription(e.getMessage());
			}
			String youzanResStatus = "F";
			if (!thisRes.isSuccess())
			{
				youzanResStatus = "F";
				res.setSuccess(false);
				res.setServiceStatus("100");
				res.setServiceDescription("调用有赞接口失败:"+thisRes.getServiceDescription());
				HelpTools.writelog_fileName("调用有赞接口失败:"+thisRes.getServiceDescription()+",单号orderNo=" + orderNo,logFileName);
				//return;
			}
			else
			{
				youzanResStatus = "T";
				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("处理成功");
				HelpTools.writelog_fileName("调用有赞处理成功,单号orderNo=" + orderNo,logFileName);
			}
			//更新下数据库
			try {
				this.pData.clear();
				UptBean ub1 = null;
				ub1 = new UptBean("DCP_ORDER");

				// condition
				ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				ub1.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));

				ub1.addUpdateValue("YOUZAN_RES",new DataValue(youzanResStatus, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(ub1));
				this.doExecuteDataToDB();
			}
			catch (Exception e)
			{
				this.pData.clear();
			}
			return;
		}
		else
		{
			StringBuffer sqlbuf = new StringBuffer("");
			String beginDate = req.getRequest().getBeginDate();
			String endDate = req.getRequest().getEndDate();
			sqlbuf.append(" select ORDERNO,SHOP,SHIPPINGSHOP from dcp_order ");
			sqlbuf.append(" where eid='"+eId+"' and loaddoctype='"+loadDocType+"' and outdoctype is null and STATUS='11' and shiptype='6' ");
			sqlbuf.append(" and YOUZAN_RES is null ");
			if (!Check.Null(beginDate))
			{
				sqlbuf.append(" and COMPLETE_DATETIME>='"+beginDate+"000000000"+"'");
			}
			if (!Check.Null(endDate))
			{
				sqlbuf.append(" and COMPLETE_DATETIME<='"+endDate+"000000000"+"'");
			}
			String sql = sqlbuf.toString();
			HelpTools.writelog_fileName("查询需要处理的有赞同城配送订单,sql语句="+sql,logFileName);
			List<Map<String, Object>> getQDataHead = this.doQueryData(sql, null);
			if (getQDataHead==null||getQDataHead.isEmpty())
			{
				HelpTools.writelog_fileName("查询需要处理的有赞同城配送订单,查询返回为空,无需处理！",logFileName);
				return;
			}

			HelpTools.writelog_fileName("查询需要处理的有赞同城配送订单,订单总数:"+getQDataHead.size(),logFileName);
			int sucessCount = 0;
			int faileCount = 0;
			for (Map<String, Object> map : getQDataHead)
			{
				String orderId = map.getOrDefault("ORDERNO","").toString();
				String shop = map.getOrDefault("SHOP","").toString();
				String shop_shipping = map.getOrDefault("SHIPPINGSHOP","").toString();
				try
				{

					JsonBasicRes thisRes=new JsonBasicRes();
					Map<String, Object> otherMap = new HashMap<String, Object>();
					//otherMap.put("extra_info", otpReq.getOpOpNo()==null?otpReq.getOpShopId():otpReq.getOpOpNo());//操作人
					try {
						thisRes=ycb.OrderToSaleRetry(eId, orderId, shop_shipping,otherMap,null);
					}
					catch (Exception e)
					{
						thisRes.setServiceDescription(e.getMessage());
					}
					String youzanResStatus = "F";
					if (!thisRes.isSuccess())
					{
						faileCount++;
						youzanResStatus = "F";
						res.setSuccess(false);
						res.setServiceStatus("100");
						res.setServiceDescription("调用有赞接口失败:"+thisRes.getServiceDescription());
						HelpTools.writelog_fileName("调用有赞接口失败:"+thisRes.getServiceDescription()+",单号orderNo=" + orderId,logFileName);
						//return;
					}
					else
					{
						sucessCount ++;
						youzanResStatus = "T";
						res.setSuccess(true);
						res.setServiceStatus("000");
						res.setServiceDescription("处理成功");
						HelpTools.writelog_fileName("调用有赞处理成功,单号orderNo=" + orderId,logFileName);
					}

					//更新下数据库
					try {
						this.pData.clear();
						UptBean ub1 = null;
						ub1 = new UptBean("DCP_ORDER");

						// condition
						ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
						ub1.addCondition("ORDERNO", new DataValue(orderId, Types.VARCHAR));

						ub1.addUpdateValue("YOUZAN_RES",new DataValue(youzanResStatus, Types.VARCHAR));
						this.addProcessData(new DataProcessBean(ub1));
						this.doExecuteDataToDB();
					}
					catch (Exception e)
					{
						this.pData.clear();
					}

				}
				catch (Exception e)
				{

				}
			}
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("查询需要处理的有赞同城配送订单,订单总数:"+getQDataHead.size()+",处理成功总数:"+sucessCount+",处理失败总数:"+faileCount);
			HelpTools.writelog_fileName("查询需要处理的有赞同城配送订单,订单总数:"+getQDataHead.size()+",处理成功总数:"+sucessCount+",处理失败总数:"+faileCount,logFileName);
		}


	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderModify_YouzanRetryReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderModify_YouzanRetryReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderModify_YouzanRetryReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderModify_YouzanRetryReq req) throws Exception
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if(req.getRequest()==null)
		{
			errMsg.append("requset不能为空值 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		String eId = req.getRequest().geteId();

		if(Check.Null(eId)){
			errMsg.append("企业编码不能为空值 ");
			isFail = true;

		}
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}


	/**
	 * 判断下，是否已上传ERP
	 * @param eId
	 * @param orderNo
	 * @return
	 * @throws Exception
	 */
	private boolean isUploadErp(String eId ,String orderNo) throws Exception
	{
		String sqlSourceno = "select PROCESS_STATUS from dcp_order where eid='"+eId+"' and orderno='"+orderNo+"' ";
		try
		{
			List<Map<String, Object>> data_Source = this.doQueryData(sqlSourceno, null);

			if (data_Source!=null && data_Source.size()>0)
			{
				if (data_Source.get(0).get("PROCESS_STATUS")!=null && data_Source.get(0).get("PROCESS_STATUS").toString().equals("Y"))
				{
					return true;
				}
				else
				{
					return false;
				}
			}
		}
		catch (Exception e)
		{

		}
		return false;
	}


	@Override
	protected TypeToken<DCP_OrderModify_YouzanRetryReq> getRequestType()
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderModify_YouzanRetryReq>(){};
	}

	@Override
	protected DCP_OrderModify_YouzanRetryRes getResponseType()
	{
		// TODO Auto-generated method stub
		return new DCP_OrderModify_YouzanRetryRes();
	}

}



