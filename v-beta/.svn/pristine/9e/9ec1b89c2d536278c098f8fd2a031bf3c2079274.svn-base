package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderCombineReq;
import com.dsc.spos.json.cust.res.DCP_OrderCombineRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderCombine extends SPosAdvanceService<DCP_OrderCombineReq, DCP_OrderCombineRes>
{

	@Override
	protected void processDUID(DCP_OrderCombineReq req, DCP_OrderCombineRes res) throws Exception {
	// TODO Auto-generated method stub
		String sqldcount="select * from OC_order where EID='"+req.getoEId()+"' and LOAD_DOCTYPE='"+req.getDocType()+"' and HEADORDERNO='"+req.getOrderNO()+"'  "; 
		List<Map<String, Object>> listcountdata=this.doQueryData(sqldcount, null);
		if(listcountdata!=null&&!listcountdata.isEmpty())
		{
		//先删除掉原单，从新记录一笔原单
			for (Map<String, Object> map : listcountdata) 
			{
				String detailorder=map.get("ORDERNO").toString();
				String STATUS=map.get("STATUS").toString();
				if(!STATUS.equals("2"))
				{
					this.pData.clear();
					res.setSuccess(false);
					res.setServiceDescription("当前单据状态不能合并！");
					return;
				}
				
				DelBean del=new DelBean("OC_ORDER");
				del.addCondition("EID", new DataValue(req.getoEId(), Types.VARCHAR));
				del.addCondition("LOAD_DOCTYPE", new DataValue(req.getDocType(), Types.VARCHAR));
				del.addCondition("ORDERNO", new DataValue(detailorder, Types.VARCHAR));
				this.pData.add(new DataProcessBean(del));
				
				DelBean del1=new DelBean("OC_ORDER_DETAIL");
				del1.addCondition("EID", new DataValue(req.getoEId(), Types.VARCHAR));
				del1.addCondition("LOAD_DOCTYPE", new DataValue(req.getDocType(), Types.VARCHAR));
				del1.addCondition("ORDERNO", new DataValue(detailorder, Types.VARCHAR));
				this.pData.add(new DataProcessBean(del1));
				
				DelBean del2=new DelBean("OC_ORDER_PAY");
				del2.addCondition("EID", new DataValue(req.getoEId(), Types.VARCHAR));
				del2.addCondition("LOAD_DOCTYPE", new DataValue(req.getDocType(), Types.VARCHAR));
				del2.addCondition("ORDERNO", new DataValue(detailorder, Types.VARCHAR));
				this.pData.add(new DataProcessBean(del2));
				
				DelBean del3=new DelBean("OC_ORDER_DETAIL_MEMO");
				del3.addCondition("EID", new DataValue(req.getoEId(), Types.VARCHAR));
				del3.addCondition("LOAD_DOCTYPE", new DataValue(req.getDocType(), Types.VARCHAR));
				del3.addCondition("ORDERNO", new DataValue(detailorder, Types.VARCHAR));
				this.pData.add(new DataProcessBean(del3));
		  }
		}
		else
		{
		}
		UptBean up1=new UptBean("OC_ORDER");
		up1.addUpdateValue("DETAILTYPE", new DataValue("1", Types.VARCHAR));
		
		up1.addCondition("EID", new DataValue(req.getoEId(), Types.VARCHAR));
		up1.addCondition("LOAD_DOCTYPE", new DataValue(req.getDocType(), Types.VARCHAR));
		up1.addCondition("ORDERNO", new DataValue(req.getOrderNO(), Types.VARCHAR));
		this.pData.add(new DataProcessBean(up1));
		
		UptBean up2=new UptBean("OC_ORDER_DETAIL");
		up2.addUpdateValue("RCQTY", new DataValue("0", Types.VARCHAR));
		
		up2.addCondition("EID", new DataValue(req.getoEId(), Types.VARCHAR));
		up2.addCondition("LOAD_DOCTYPE", new DataValue(req.getDocType(), Types.VARCHAR));
		up2.addCondition("ORDERNO", new DataValue(req.getOrderNO(), Types.VARCHAR));
		this.pData.add(new DataProcessBean(up2));
		
		UptBean up3=new UptBean("OC_ORDER_PAY");
		up3.addUpdateValue("RCPAY", new DataValue("0", Types.VARCHAR));
		
		up3.addCondition("EID", new DataValue(req.getoEId(), Types.VARCHAR));
		up3.addCondition("LOAD_DOCTYPE", new DataValue(req.getDocType(), Types.VARCHAR));
		up3.addCondition("ORDERNO", new DataValue(req.getOrderNO(), Types.VARCHAR));
		this.pData.add(new DataProcessBean(up3));
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderCombineReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderCombineReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderCombineReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderCombineReq req) throws Exception {
	// TODO Auto-generated method stub
	return false;
	}

	@Override
	protected TypeToken<DCP_OrderCombineReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_OrderCombineReq>(){};
	}

	@Override
	protected DCP_OrderCombineRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_OrderCombineRes();
	}

}
