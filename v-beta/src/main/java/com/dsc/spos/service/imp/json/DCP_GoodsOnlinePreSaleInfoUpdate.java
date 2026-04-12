package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.time.temporal.IsoFields;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.joda.time.format.ISOPeriodFormat;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_GoodsOnlinePreSaleInfoUpdateReq;
import com.dsc.spos.json.cust.req.DCP_GoodsOnlinePreSaleInfoUpdateReq.level1Plu;
import com.dsc.spos.json.cust.req.DCP_GoodsOnlinePreSaleInfoUpdateReq.level1PreSaleInfo;
import com.dsc.spos.json.cust.res.DCP_GoodsOnlinePreSaleInfoUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsOnlinePreSaleInfoUpdate extends SPosAdvanceService<DCP_GoodsOnlinePreSaleInfoUpdateReq, DCP_GoodsOnlinePreSaleInfoUpdateRes> {

	@Override
	protected void processDUID(DCP_GoodsOnlinePreSaleInfoUpdateReq req, DCP_GoodsOnlinePreSaleInfoUpdateRes res) throws Exception {
	// TODO Auto-generated method stub			
		String lastmoditime = null;//req.getRequset().getLastmoditime();
		if(lastmoditime==null||lastmoditime.isEmpty())
		{
			lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		}

		String curLangType = req.getLangType();
		if(curLangType==null||curLangType.isEmpty())
		{
			curLangType = "zh_CN";
		}
		
		String preSale = req.getRequest().getPreSaleInfo().getPreSale();//是否预订，需提前预订0-否1-是
		String deliveryDateType = req.getRequest().getPreSaleInfo().getDeliveryDateType();//发货时机类型1：付款成功后发货2：指定日期发货
		String deliveryDateType2 = req.getRequest().getPreSaleInfo().getDeliveryDateType2();//发货时间类型1：小时 2：天
		String deliveryDateValue = req.getRequest().getPreSaleInfo().getDeliveryDateValue();//付款后%S天/小时后发货，发货时机类型为1时必须传入
		String deliveryDate = req.getRequest().getPreSaleInfo().getDeliveryDate();//预计发货日期，发货时机类型为2时必须传入，YYYY-MM-DD
				
		String classType = "ONLINE";
		
		List<level1Plu> pluList = req.getRequest().getPluList();
		
		for (level1Plu par : pluList) 
		{
			
			UptBean ub1 = new UptBean("DCP_GOODS_ONLINE");			
			ub1.addCondition("EID",new DataValue(req.geteId(), Types.VARCHAR));
			ub1.addCondition("PLUNO", new DataValue(par.getPluNo(), Types.VARCHAR));

		
			ub1.addUpdateValue("PRESALE",new DataValue(preSale, Types.VARCHAR)); 
			ub1.addUpdateValue("DELIVERYDATETYPE",new DataValue(deliveryDateType, Types.VARCHAR)); 
			ub1.addUpdateValue("DELIVERYDATETYPE2",new DataValue(deliveryDateType2, Types.VARCHAR)); 
			
		  if(deliveryDateValue!=null&&deliveryDateValue.isEmpty()==false)
		  {
		  	ub1.addUpdateValue("DELIVERYDATEVALUE",new DataValue(deliveryDateValue, Types.VARCHAR)); 
		  }
		  if(deliveryDate!=null&&deliveryDate.isEmpty()==false)
		  {
		  	ub1.addUpdateValue("DELIVERYDATE",new DataValue(deliveryDate, Types.DATE)); 
		  }
			
		
			
			ub1.addUpdateValue("LASTMODIOPID",new DataValue(req.getOpNO(), Types.VARCHAR)); 
			ub1.addUpdateValue("LASTMODIOPNAME",new DataValue(req.getOpName(), Types.VARCHAR));
			ub1.addUpdateValue("LASTMODITIME",new DataValue(lastmoditime, Types.DATE)); 
		
			this.addProcessData(new DataProcessBean(ub1));
		
	
		}

		this.doExecuteDataToDB();
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");
		
	
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_GoodsOnlinePreSaleInfoUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_GoodsOnlinePreSaleInfoUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_GoodsOnlinePreSaleInfoUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_GoodsOnlinePreSaleInfoUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return false;
	}

	@Override
	protected TypeToken<DCP_GoodsOnlinePreSaleInfoUpdateReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_GoodsOnlinePreSaleInfoUpdateReq>(){};
	}

	@Override
	protected DCP_GoodsOnlinePreSaleInfoUpdateRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_GoodsOnlinePreSaleInfoUpdateRes();
	}
	
}
