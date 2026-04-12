package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_ServiceAddressQuery_OpenReq;
import com.dsc.spos.json.cust.res.DCP_ServiceAddressQuery_OpenRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 服务地址查询：扫码点餐专用， 这服务完全没用,胡扯。 有问题找王欢
 *
 */
public class DCP_ServiceAddressQuery_Open extends SPosBasicService<DCP_ServiceAddressQuery_OpenReq, DCP_ServiceAddressQuery_OpenRes> {

	@Override
	protected boolean isVerifyFail(DCP_ServiceAddressQuery_OpenReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_ServiceAddressQuery_OpenReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_ServiceAddressQuery_OpenReq>(){};
	}

	@Override
	protected DCP_ServiceAddressQuery_OpenRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_ServiceAddressQuery_OpenRes();
	}

	@Override
	protected DCP_ServiceAddressQuery_OpenRes processJson(DCP_ServiceAddressQuery_OpenReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_ServiceAddressQuery_OpenRes res = null;
		res = this.getResponse();
		try {
			
//			String channelId = req.getApiUser().getChannelId();
//			System.out.println("CRM_ApiUser 表 :"+channelId  );
//			
//			String apiUserCode = req.getApiUserCode();
			String keyTxt = req.getRequest().getKeyTxt(); 
			int pageSize = req.getPageSize();
			int pageNumber = req.getPageNumber();
			
			res.setDatas(new ArrayList<DCP_ServiceAddressQuery_OpenRes.level1Elm>());
			DCP_ServiceAddressQuery_OpenRes.level1Elm lv1 = res.new level1Elm();
			lv1.setCrmService(PosPub.getCRM_URL(req.geteId()));
			lv1.setPayService(PosPub.getPAY_URL(req.geteId()));
			lv1.setDcpService(PosPub.getDCP_URL(req.geteId()));
			lv1.setPosService(PosPub.getPOS_URL(req.geteId()));
			lv1.setPromService(PosPub.getPROM_URL(req.geteId()));
			lv1.setPictureGetAddress(PosPub.getPicture_URL(req.geteId()));
			
			res.getDatas().add(lv1);
			
			res.setSuccess(true);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行成功！");
			
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
	protected String getQuerySql(DCP_ServiceAddressQuery_OpenReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql = "";
		return sql;
	}
	
}
