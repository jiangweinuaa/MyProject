package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.ExecBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_StockChannelWhiteDeleteReq;
import com.dsc.spos.json.cust.req.DCP_StockChannelWhiteDeleteReq.PluList;
import com.dsc.spos.json.cust.res.DCP_StockChannelWhiteDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.google.gson.reflect.TypeToken;

/**
 * 渠道商品白名单删除
 * @author 2020-06-05
 *
 */
public class DCP_StockChannelWhiteDelete extends SPosAdvanceService<DCP_StockChannelWhiteDeleteReq, DCP_StockChannelWhiteDeleteRes> {

	@Override
	protected void processDUID(DCP_StockChannelWhiteDeleteReq req, DCP_StockChannelWhiteDeleteRes res)
			throws Exception {
		// TODO Auto-generated method stub
		
		try {
			
			String eId = req.geteId();
			
			String deleteSql = "";
			StringBuffer sqlbuf = new StringBuffer();
			sqlbuf.append( "  delete from DCP_STOCK_CHANNEL_WHITE a "
					+ " where a.Eid = '"+eId+"' " );
			List<PluList> pluDatas = req.getRequest().getPluList();
			
			if(pluDatas != null && pluDatas.size() > 0 && !pluDatas.isEmpty() ){
				sqlbuf.append(" and  ( " );
				int totSize = pluDatas.size();
				for (int i = 0; i < pluDatas.size(); i++) {
					String channelId = pluDatas.get(i).getChannelId();
					String pluNo = pluDatas.get(i).getPluNo();
					
					//以下五个字段 组成主键， 任意其中一个都可能会重复
					sqlbuf.append(" ( a.channelId = '"+channelId+"' and  a.pluNo = '"+pluNo+"'  )   " );
					
					if(i+1 < totSize ){
						sqlbuf.append(" or ");
					}
					
				}
				sqlbuf.append(" ) " );
			}
			deleteSql = sqlbuf.toString();
			ExecBean exc = new ExecBean(deleteSql);
			this.addProcessData(new DataProcessBean(exc));
			
			this.doExecuteDataToDB();
			
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败:"+e.getMessage());
		}
		
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_StockChannelWhiteDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_StockChannelWhiteDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_StockChannelWhiteDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_StockChannelWhiteDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_StockChannelWhiteDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_StockChannelWhiteDeleteReq>(){};
	}

	@Override
	protected DCP_StockChannelWhiteDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_StockChannelWhiteDeleteRes();
	}
	
}
