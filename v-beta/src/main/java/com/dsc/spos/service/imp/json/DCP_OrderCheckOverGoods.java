package com.dsc.spos.service.imp.json;
import java.util.Calendar;
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
import com.dsc.spos.json.cust.req.DCP_OrderCheckOverGoodsReq;
import com.dsc.spos.json.cust.res.DCP_OrderCheckOverGoodsRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderCheckOverGoods extends SPosAdvanceService<DCP_OrderCheckOverGoodsReq,DCP_OrderCheckOverGoodsRes>
{

	@Override
	protected void processDUID(DCP_OrderCheckOverGoodsReq req, DCP_OrderCheckOverGoodsRes res) throws Exception
	{
		String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		String eId=req.geteId();

		res.setDatas(res.new level1Elm());
		res.getDatas().setErrorOrderList(new ArrayList<DCP_OrderCheckOverGoodsRes.Order>());

		boolean bOk=true;

		List<DCP_OrderCheckOverGoodsReq.Order> orderList=req.getRequest().getOrderList();
		for (DCP_OrderCheckOverGoodsReq.Order order : orderList)
		{
			try
			{
				String sqlpackage="select a.* from dcp_shippackageset a where a.eid='"+eId+"' and a.packageno='"+order.getPackageNo()+"' ";
				List<Map<String , Object>> getData_package=this.doQueryData(sqlpackage, null);
				if (getData_package==null || getData_package.isEmpty())
				{
					DCP_OrderCheckOverGoodsRes.Order od=res.new Order();
					od.setOrderNo(order.getOrderNo());
					od.setErrorDesc("包裹编号:"+order.getPackageNo()+"的资料未设置！");					
					res.getDatas().getErrorOrderList().add(od);

					//
					bOk=false;					
					continue;
				}
				else 
				{
					String sqlOrder="select a.* from DCP_ORDER a where a.eid='"+eId+"' and a.ORDERNO='"+order.getOrderNo()+"' ";
					List<Map<String , Object>> getData_Order=this.doQueryData(sqlOrder, null);
					if (getData_Order==null || getData_Order.isEmpty())
					{
						DCP_OrderCheckOverGoodsRes.Order od=res.new Order();
						od.setOrderNo(order.getOrderNo());
						od.setErrorDesc("查不到订单资料！");					
						res.getDatas().getErrorOrderList().add(od);

						//
						bOk=false;
						continue;
					}					
					else 
					{
						String status=getData_Order.get(0).get("STATUS").toString();

						//0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
						if (status.equals("3") || status.equals("10")|| status.equals("11")||status.equals("12") ||status.equals("13"))
						{							
							DCP_OrderCheckOverGoodsRes.Order od=res.new Order();
							od.setOrderNo(order.getOrderNo());
							od.setErrorDesc("此订单状态不允许操作点货！");					
							res.getDatas().getErrorOrderList().add(od);

							//
							bOk=false;
							continue;
						}	
						else 
						{
							String packageName=getData_package.get(0).get("PACKAGENAME").toString();
							String measureNo=getData_package.get(0).get("MEASURENO").toString();
							String measureName=getData_package.get(0).get("MEASURENAME").toString();
							String temperateNo=getData_package.get(0).get("TEMPERATENO").toString();
							String temperateName=getData_package.get(0).get("TEMPERATENAME").toString();

							UptBean ub1 = new UptBean("DCP_ORDER");
							ub1.addUpdateValue("STATUS", new DataValue("13",Types.VARCHAR));
							ub1.addUpdateValue("PACKAGENO", new DataValue(order.getPackageNo(),Types.VARCHAR));
							ub1.addUpdateValue("PACKAGENAME", new DataValue(packageName,Types.VARCHAR));
							ub1.addUpdateValue("MEASURENO", new DataValue(measureNo,Types.VARCHAR));
							ub1.addUpdateValue("MEASURENAME", new DataValue(measureName,Types.VARCHAR));
							ub1.addUpdateValue("TEMPERATELAYERNO", new DataValue(temperateNo,Types.VARCHAR));
							ub1.addUpdateValue("TEMPERATELAYERNAME", new DataValue(temperateName,Types.VARCHAR));
			                ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
			                ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

							ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
							ub1.addCondition("ORDERNO", new DataValue(order.getOrderNo(), Types.VARCHAR));
							this.addProcessData(new DataProcessBean(ub1));
							//
							this.doExecuteDataToDB();
						}						
					}					
				}
			} 
			catch (Exception e)
			{
				//
				bOk=false;
			}
		}

		//
		res.setSuccess(bOk);
		res.setServiceStatus(bOk?"000":"100");
		res.setServiceDescription(bOk?"服务执行成功！":"服务执行失败！");
		return;

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderCheckOverGoodsReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderCheckOverGoodsReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderCheckOverGoodsReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderCheckOverGoodsReq req) throws Exception
	{		
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		List<DCP_OrderCheckOverGoodsReq.Order> orderList=req.getRequest().getOrderList();

		if (orderList==null || orderList.size()==0)
		{
			isFail = true;
			errMsg.append("订单列表orderList不能为空 ");
		}

		for (DCP_OrderCheckOverGoodsReq.Order order : orderList)
		{
			if (Check.Null(order.getOrderNo()))
			{
				isFail = true;
				errMsg.append("订单号不能为空 ");
			}
			if (Check.Null(order.getPackageNo()))
			{
				isFail = true;
				errMsg.append("包裹编号不能为空 ");
			}
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderCheckOverGoodsReq> getRequestType()
	{
		return new TypeToken<DCP_OrderCheckOverGoodsReq>() {};
	}

	@Override
	protected DCP_OrderCheckOverGoodsRes getResponseType()
	{
		return new DCP_OrderCheckOverGoodsRes();
	}




}
