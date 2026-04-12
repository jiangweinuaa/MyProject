package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DsmDAO;
import com.dsc.spos.json.cust.req.DCP_StockQtyQueryReq;
import com.dsc.spos.json.cust.req.DCP_StockQtyQueryReq.PluList;
import com.dsc.spos.json.cust.res.DCP_StockQtyQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 可用性库存数量查询
 * @author 2020-06-11
 *
 */
public class DCP_StockQtyQuery extends SPosBasicService<DCP_StockQtyQueryReq, DCP_StockQtyQueryRes> {
	
	@Override
	protected boolean isVerifyFail(DCP_StockQtyQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_StockQtyQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_StockQtyQueryReq>(){};
	}

	@Override
	protected DCP_StockQtyQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_StockQtyQueryRes();
	}

	@Override
	protected DCP_StockQtyQueryRes processJson(DCP_StockQtyQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_StockQtyQueryRes res = null;
		res = this.getResponse();
		
		try {
			String eId = req.geteId();
			List< PluList> reqPluDatas = req.getRequest().getPluList(); 
			DCP_StockQtyQueryRes.levelRes lvRes = res.new levelRes();
			String channelId = req.getRequest().getChannelId();
			String orgNo = req.getRequest().getOrganizationNo()== null ? "": req.getRequest().getOrganizationNo();
			lvRes.setPluList(new ArrayList<DCP_StockQtyQueryRes.PluList>()); 
			
			if(reqPluDatas != null && !reqPluDatas.isEmpty()){
				
				for (PluList map : reqPluDatas) {
					
					DCP_StockQtyQueryRes.PluList lvPlu = res.new PluList();
					
					String pluNo = map.getPluNo();
					String featureNo = " "; //特征码 默认给空格
					if(!Check.Null(map.getFeatureNo())){
						featureNo = map.getFeatureNo();
					}
					String warehouse = "";
					if(!Check.Null(map.getWarehouse())){
						warehouse = map.getWarehouse();
					}
				
					String sUnit = map.getsUnit();
					String qty = PosPub.queryStockQty(dao, eId, pluNo, featureNo, orgNo, channelId, warehouse, sUnit);
					
//					lvPlu.setChannelId(channelId);
//					lvPlu.setOrganizationNo(orgNo);
					lvPlu.setPluNo(pluNo);
					lvPlu.setFeatureNo(featureNo);
					lvPlu.setsUnit(sUnit);
//					lvPlu.setWarehouse(warehouse);
					lvPlu.setQty(qty);
					lvRes.getPluList().add(lvPlu);
					
				}
				
			}
			
			res.setDatas(lvRes);
			
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败："+e.getMessage());
			
		}
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_StockQtyQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
