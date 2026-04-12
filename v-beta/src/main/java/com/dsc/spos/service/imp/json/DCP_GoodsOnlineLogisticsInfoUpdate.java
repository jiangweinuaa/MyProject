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
import com.dsc.spos.json.cust.req.DCP_GoodsOnlineLogisticsInfoUpdateReq;
import com.dsc.spos.json.cust.req.DCP_GoodsOnlineLogisticsInfoUpdateReq.level1Plu;
import com.dsc.spos.json.cust.req.DCP_GoodsOnlineLogisticsInfoUpdateReq.level1LogisticsInfo;
import com.dsc.spos.json.cust.res.DCP_GoodsOnlineLogisticsInfoUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsOnlineLogisticsInfoUpdate extends SPosAdvanceService<DCP_GoodsOnlineLogisticsInfoUpdateReq, DCP_GoodsOnlineLogisticsInfoUpdateRes> {

	@Override
	protected void processDUID(DCP_GoodsOnlineLogisticsInfoUpdateReq req, DCP_GoodsOnlineLogisticsInfoUpdateRes res) throws Exception {
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
		
		String shopPickUp = req.getRequest().getLogisticsInfo().getShopPickUp();//是否支持自提0-否1-是
		String cityDeliver = req.getRequest().getLogisticsInfo().getCityDeliver();//是否支持同城配送0-否1-是
		String expressDeliver = req.getRequest().getLogisticsInfo().getExpressDeliver();//是否支持全国快递0-否1-是
		String freightFree = req.getRequest().getLogisticsInfo().getFreightFree();//是否包邮0-否1-是
		String freightTemplateId = req.getRequest().getLogisticsInfo().getFreightTemplateId();//运费模板编码
		
		
		String classType = "ONLINE";
		
		List<level1Plu> pluList = req.getRequest().getPluList();
		
		for (level1Plu par : pluList) 
		{
			
			UptBean ub1 = new UptBean("DCP_GOODS_ONLINE");			
			ub1.addCondition("EID",new DataValue(req.geteId(), Types.VARCHAR));
			ub1.addCondition("PLUNO", new DataValue(par.getPluNo(), Types.VARCHAR));

		
			ub1.addUpdateValue("SHOPPICKUP",new DataValue(shopPickUp, Types.VARCHAR));
			ub1.addUpdateValue("CITYDELIVER",new DataValue(cityDeliver, Types.VARCHAR)); 
			ub1.addUpdateValue("EXPRESSDELIVER",new DataValue(expressDeliver, Types.VARCHAR)); 
			ub1.addUpdateValue("FREIGHTFREE",new DataValue(freightFree, Types.VARCHAR)); 
			 			
		  if(freightTemplateId!=null)
		  {
		  	ub1.addUpdateValue("FREIGHTTEMPLEID",new DataValue(freightTemplateId, Types.VARCHAR)); 
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
	protected List<InsBean> prepareInsertData(DCP_GoodsOnlineLogisticsInfoUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_GoodsOnlineLogisticsInfoUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_GoodsOnlineLogisticsInfoUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_GoodsOnlineLogisticsInfoUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return false;
	}

	@Override
	protected TypeToken<DCP_GoodsOnlineLogisticsInfoUpdateReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_GoodsOnlineLogisticsInfoUpdateReq>(){};
	}

	@Override
	protected DCP_GoodsOnlineLogisticsInfoUpdateRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_GoodsOnlineLogisticsInfoUpdateRes();
	}
	
}
