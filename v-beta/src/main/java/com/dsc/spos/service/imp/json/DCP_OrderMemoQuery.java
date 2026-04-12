package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_OrderMemoQueryReq;
import com.dsc.spos.json.cust.res.DCP_OrderMemoQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderMemoQuery extends SPosBasicService<DCP_OrderMemoQueryReq, DCP_OrderMemoQueryRes> {

	//BomGetServiceImp extends SPosBasicService<BomGetReq, BomGetRes>{
	@Override
	protected boolean isVerifyFail(DCP_OrderMemoQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；			
		if (Check.Null(req.getoEId())) {
			errCt++;
			errMsg.append("企业编号oEId不可为空值, ");
			isFail = true;
		} 

		if(req.getDocType()==null)
		{
			errCt++;
			errMsg.append("单据类型不可为空值, ");
			isFail = true;
		}

		if(req.getOrderNO()==null)
		{
			errCt++;
			errMsg.append("订单单号不可为空值, ");
			isFail = true;
		}

		if(req.getItem()==null)
		{
			errCt++;
			errMsg.append("订单项次不可为空值, ");
			isFail = true;
		}


		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;

	}

	@Override
	protected TypeToken<DCP_OrderMemoQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderMemoQueryReq>(){};
	}

	@Override
	protected DCP_OrderMemoQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderMemoQueryRes();
	}

	@Override
	protected DCP_OrderMemoQueryRes processJson(DCP_OrderMemoQueryReq req) throws Exception {
		//查询资料
		DCP_OrderMemoQueryRes res = this.getResponse();
		String sql = null;
		sql = this.getQuerySql(req);
		String[] condCountValues={};		
		List<Map<String, Object>> getQDataDetail=this.doQueryData(sql,condCountValues);
		if (getQDataDetail != null && getQDataDetail.isEmpty() == false){
			res.setDatas(new ArrayList<DCP_OrderMemoQueryRes.level1Elm>());
			for (Map<String, Object> oneData : getQDataDetail) 
			{
				DCP_OrderMemoQueryRes.level1Elm oneLv1 = res.new level1Elm();
				//取出数据
				String msgName = oneData.get("MEMONAME").toString();
				String msgType = oneData.get("MEMOTYPE").toString();
				String message = oneData.get("MEMO").toString();

				oneLv1.setMessage(message);
				oneLv1.setMsgName(msgName);
				oneLv1.setMsgType(msgType);
				//添加单头
				res.getDatas().add(oneLv1);
			}

		}
		else {
			res.setDatas(new ArrayList<DCP_OrderMemoQueryRes.level1Elm>());
		}

		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_OrderMemoQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		//查询条件  ：企业编号+门店编号+订单单号+来源项次			
		String oEId = req.getoEId();
		String oShopId = req.getoShopId();
		String orderNO = req.getOrderNO();
		String item = req.getItem();
		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append(" select memoname,memotype,memo from OC_ORDER_DETAIL_MEMO "
			+ "where EID = '"+oEId+"'  AND orderNO = '"+orderNO+"'  AND oitem ='"+item+"'  ");
		
		if (oShopId != null && oShopId.length() > 0)
		{
			sqlbuf.append(" AND SHOPID = '"+oShopId+"'");
		}
		else
		{
			sqlbuf.append(" AND (SHOPID = ' ' or SHOPID is null  ) ");
		}

		return sqlbuf.toString();
	}

}
