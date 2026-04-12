package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_OrderShippingReq;
import com.dsc.spos.json.cust.req.DCP_OrderShipping_OpenReq;
import com.dsc.spos.json.cust.res.DCP_OrderShippingRes;
import com.dsc.spos.json.cust.res.DCP_OrderShipping_OpenRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.orderStatusLog;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 说明：出货接口只传了订单号，这种是最简单的订转销，订单必须是全款已付的普通订单，其他情况订单不能使用此接口
 */
public class DCP_OrderShipping extends SPosAdvanceService<DCP_OrderShippingReq,DCP_OrderShippingRes>
{

	@Override
	protected void processDUID(DCP_OrderShippingReq req, DCP_OrderShippingRes res) throws Exception
	{
		String eId = req.geteId();
		String opType = req.getRequest().getOpType(); // 1:发货 2:安排取件 
		String[] orderList=req.getRequest().getOrderList();
		String bDate = req.getRequest().getBdate();
		String opNo = req.getOpNO();
		String opName = req.getOpName();
        if (opName==null)
        {
            opName = "";
        }
		String plantType = req.getPlantType();


		for (String orderno : orderList)
		{
			RedisPosPub rpp = new RedisPosPub();
			String checkKey="";
			checkKey="DCP_OrderShipping:"+orderno;
			try{	
				if(!rpp.IsExistStringKey(checkKey))
				{
					Calendar cal = Calendar.getInstance();// 获得当前时间
					SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String createDate = df1.format(cal.getTime());
					rpp.setEx(checkKey,300,createDate);
				}else
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E422, "订单:"+orderno+"正在处理中...请等候");
				}
				if (opType.equals("1"))// 1:发货 
				{
					//内部调外部
					//**内部接口与外部接口代码共用,外部服务里面用到apiUser接口帐号相关的要进行特殊处理，
					//**可以通过此节点apiUser判断是外部还是内部服务

					//1.深拷贝一份源服务请求
					ParseJson pj = new ParseJson();
					String jsonReq=pj.beanToJson(req);
					DCP_OrderShipping_OpenReq dcp_OrderShipping_OpenReq=pj.jsonToBean(jsonReq, new TypeToken<DCP_OrderShipping_OpenReq>(){});
					//2.目标服务部分字段需重新给值
					dcp_OrderShipping_OpenReq.setServiceId("DCP_OrderShipping_Open");
					dcp_OrderShipping_OpenReq.getRequest().seteId(req.geteId());
					dcp_OrderShipping_OpenReq.getRequest().setOpShopId(req.getShopId());
					if (opNo!=null)
                    {
                        dcp_OrderShipping_OpenReq.getRequest().setOpNo(opNo);
                    }
					if (opName!=null)
					{
						dcp_OrderShipping_OpenReq.getRequest().setOpName(opName);
					}
					if (plantType!=null)
					{
						dcp_OrderShipping_OpenReq.setPlantType(plantType);
					}

					//3.调用目标服务
					DCP_OrderShipping_Open dcp_OrderShipping_Open=new DCP_OrderShipping_Open();
					DCP_OrderShipping_OpenRes dcp_OrderShipping_OpenRes=new DCP_OrderShipping_OpenRes();
					dcp_OrderShipping_Open.setDao(this.dao);
					dcp_OrderShipping_Open.processDUID(dcp_OrderShipping_OpenReq,dcp_OrderShipping_OpenRes);

					//4.处理服务返回
					//*注意
					//*res不能重新初始化，会导致赋值返回无效，就像下面这句不能用
					//res=pj.jsonToBean(jsonRes,new TypeToken<DCP_OrderShippingRes>(){});
					res.setSuccess(dcp_OrderShipping_OpenRes.isSuccess());
					res.setServiceStatus(dcp_OrderShipping_OpenRes.getServiceStatus());
					res.setServiceDescription(dcp_OrderShipping_OpenRes.getServiceDescription());
					dcp_OrderShipping_Open=null;
					pj=null;
					dcp_OrderShipping_OpenRes=null;
				}
				else //***********2:安排取件 ******************************************************************************************
				{
					List<DataProcessBean> data = new ArrayList<DataProcessBean>();
					//订单SQL
					String sqlOrder="select * from dcp_order a where a.eid='"+eId+"' and a.orderno='"+orderno+"' ";
					List<Map<String, Object>> getData_Order=this.doQueryData(sqlOrder, null);
					String status=getData_Order.get(0).get("STATUS").toString();
					String deliveryStatus=getData_Order.get(0).get("DELIVERYSTATUS").toString();
					String loaddoctype=getData_Order.get(0).get("LOADDOCTYPE").toString();
					String channelId=getData_Order.get(0).get("CHANNELID").toString();
					String deliveryType=getData_Order.get(0).get("DELIVERYTYPE").toString();
					String deliveryNo=getData_Order.get(0).get("DELIVERYNO").toString();
					String detailType=getData_Order.get(0).get("DETAILTYPE").toString();
					String headorderno=getData_Order.get(0).get("HEADORDERNO").toString();
					String shopId=getData_Order.get(0).get("SHOP").toString();
					String shippingNo=getData_Order.get(0).get("SHIPPINGSHOP").toString();
					String machshopNo=getData_Order.get(0).get("MACHSHOP").toString();

					if (status.equals("10") && deliveryStatus.equals("1")==false)//已发货的才能安排取件
					{
						//更新订单主表DCP_ORDER
						UptBean ub_DCP_ORDER = new UptBean("DCP_ORDER");
						//更新值
						//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 8货到物流中心 9消费者七天未取件
						ub_DCP_ORDER.addUpdateValue("DELIVERYSTATUS", new DataValue("1", Types.VARCHAR));
						ub_DCP_ORDER.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
						ub_DCP_ORDER.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));

						//更新条件
						ub_DCP_ORDER.addCondition("EID", new DataValue(eId, Types.VARCHAR));
						ub_DCP_ORDER.addCondition("ORDERNO", new DataValue(orderno, Types.VARCHAR));
						data.add(new DataProcessBean(ub_DCP_ORDER));

						//写订单日志
						String LogStatus="1";
						orderStatusLog oslog=new orderStatusLog();
						oslog.setCallback_status("N");
						oslog.setChannelId(channelId);
						oslog.setDisplay("0");
						oslog.seteId(eId);
						oslog.setLoadDocBillType(loaddoctype);
						oslog.setLoadDocOrderNo(orderno);
						oslog.setLoadDocType(loaddoctype);
						oslog.setMachShopName(machshopNo);
						oslog.setMachShopNo(machshopNo);
						oslog.setMemo("");
						oslog.setNeed_callback("N");
						oslog.setNeed_notify("N");
						oslog.setNotify_status("N");
						oslog.setOpName(req.getOpName());
						oslog.setOpNo(req.getOpNO());
						oslog.setOrderNo(orderno);
						oslog.setShippingShopName(shippingNo);
						oslog.setShippingShopNo(shippingNo);
						oslog.setShopName(shopId);
						oslog.setShopNo(shopId);
						oslog.setStatus(LogStatus);
						//
						String statusType="2";
						StringBuilder statusTypeName=new StringBuilder();
						String statusName=HelpTools.GetOrderStatusName(statusType, LogStatus, statusTypeName);
						oslog.setStatusName(statusName);
						oslog.setStatusType(statusType);
						oslog.setStatusTypeName(statusTypeName.toString());
						oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
						InsBean	ib_DCP_ORDER_STATUSLOG=HelpTools.InsertOrderStatusLog(oslog);
						data.add(new DataProcessBean(ib_DCP_ORDER_STATUSLOG));

						//最后执行SQL
						dao.useTransactionProcessData(data);

					}
					else
					{
						res.setSuccess(false);
						res.setServiceStatus("100");
						res.setServiceDescription("当前订单状态不允许此操作,已发货的才能安排取件！");
						return;
					}
				}

				//有异常就退出
				if (res.isSuccess()==false)
				{
					return;
				}
			}
			finally
			{
                rpp.DeleteKey(checkKey);
			}
		}

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");
		return;
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderShippingReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderShippingReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderShippingReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderShippingReq req) throws Exception
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		if (Check.Null(req.getRequest().getOpType()))
		{
			isFail = true;
			errMsg.append("操作类型opType不能为空 ");
		}

		String[] orderList=req.getRequest().getOrderList();

		if (orderList==null || orderList.length==0)
		{
			isFail = true;
			errMsg.append("订单列表orderList不能为空 ");
		}

		for (String orderno : orderList)
		{
			if (Check.Null(orderno))
			{
				isFail = true;
				errMsg.append("订单号不能为空 ");
			}
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderShippingReq> getRequestType()
	{
		return new TypeToken<DCP_OrderShippingReq>(){};
	}

	@Override
	protected DCP_OrderShippingRes getResponseType()
	{
		return new DCP_OrderShippingRes();
	}



}
