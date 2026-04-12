package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_OrderDeliverySchedQueryReq;
import com.dsc.spos.json.cust.res.DCP_OrderDeliverySchedQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderDeliverySchedQuery extends SPosBasicService<DCP_OrderDeliverySchedQueryReq, DCP_OrderDeliverySchedQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_OrderDeliverySchedQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return false;
	}

	@Override
	protected TypeToken<DCP_OrderDeliverySchedQueryReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_OrderDeliverySchedQueryReq>(){};
	}

	@Override
	protected DCP_OrderDeliverySchedQueryRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_OrderDeliverySchedQueryRes();
	}

	@Override
	protected DCP_OrderDeliverySchedQueryRes processJson(DCP_OrderDeliverySchedQueryReq req) throws Exception {
	// TODO Auto-generated method stub
		DCP_OrderDeliverySchedQueryRes res=new DCP_OrderDeliverySchedQueryRes();
		if(req.getOptype().equals("2"))
		{
		//查找DCP_SHOP_ORDERSet查找本门店的配送方式
			String sqldeliv="select  distinct EID,ordersetvalue  from DCP_SHOP_ORDERSet where EID='"+req.geteId()+"'  and OrdersetType='1' "
			//	+ " order by PRIORITY ";
			+ "  ";
			List<Map<String, Object>> listsqldeliv=this.doQueryData(sqldeliv, null);
			if(listsqldeliv!=null&&!listsqldeliv.isEmpty())
			{
				res.setDatas(new ArrayList<DCP_OrderDeliverySchedQueryRes.level1Elm>());
				for (Map<String, Object> map : listsqldeliv) 
				{
					DCP_OrderDeliverySchedQueryRes.level1Elm lv1=res.new level1Elm();
					lv1.setDeliveryType(map.get("ORDERSETVALUE").toString());
					lv1.setoEId(map.get("EID").toString());

					res.getDatas().add(lv1);
		    }
			}
			return res;
			
		}
		
		
		String orderno=req.getDatas().get(0).getOrderNO();
		String doctype=req.getDatas().get(0).getDocType();
		String sql="select * from OC_ORDER where EID='"+req.geteId()+"'  and ORDERNO='"+orderno+"' and LOAD_DOCTYPE='"+doctype+"' ";
		List<Map<String, Object>> listsqldate=this.doQueryData(sql, null);
		if(listsqldate!=null&&!listsqldate.isEmpty())
		{
			String shopId=listsqldate.get(0).get("MACHSHOP").toString();
			if(shopId==null||shopId.isEmpty())
			{
				res.setSuccess(false);
				res.setServiceDescription("请先设置生产门店！");
				return res;
			}
			//查找DCP_SHOP_ORDERSet查找本门店的配送方式
			String sqldeliv="select * from DCP_SHOP_ORDERSet where EID='"+req.geteId()+"' and SHOPID='"+shopId+"' and OrdersetType='1' "
				+ " order by PRIORITY ";
			List<Map<String, Object>> listsqldeliv=this.doQueryData(sqldeliv, null);
			if(listsqldeliv!=null&&!listsqldeliv.isEmpty())
			{
				res.setDatas(new ArrayList<DCP_OrderDeliverySchedQueryRes.level1Elm>());
				for (Map<String, Object> map : listsqldeliv) 
				{
					DCP_OrderDeliverySchedQueryRes.level1Elm lv1=res.new level1Elm();
					lv1.setDeliveryType(map.get("ORDERSETVALUE").toString());
					lv1.setoEId(map.get("EID").toString());
					lv1.setoShopId(map.get("SHOPID").toString());
					lv1.setPriority(map.get("PRIORITY").toString());
					res.getDatas().add(lv1);
					lv1 = null;
		    }
			}
			
		}
		else
		{
			res.setSuccess(false);
			res.setServiceDescription("查询订单失败！");
		}
		
	return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	// TODO Auto-generated method stub
	
	}

	@Override
	protected String getQuerySql(DCP_OrderDeliverySchedQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

}
