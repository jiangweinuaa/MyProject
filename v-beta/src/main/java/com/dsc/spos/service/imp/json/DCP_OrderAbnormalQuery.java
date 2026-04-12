package com.dsc.spos.service.imp.json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_OrderAbnormalQueryReq;
import com.dsc.spos.json.cust.res.DCP_OrderAbnormalQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderAbnormalQuery extends SPosBasicService<DCP_OrderAbnormalQueryReq,DCP_OrderAbnormalQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_OrderAbnormalQueryReq req) throws Exception
	{		
		boolean isFail = false;
		  StringBuffer errMsg = new StringBuffer("");

		  if(req.getRequest()==null)
		  {
		  	errMsg.append("request不能为空值 ");
		  	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		  }
		  
		  String eId = req.getRequest().geteId();
		  String orderNo = req.getRequest().getOrderNo();
		      	  
		  if(Check.Null(eId)){
		   	errMsg.append("企业id不能为空值 ");
		   	isFail = true;

		  }
		  if(Check.Null(orderNo)){
			   	errMsg.append("订单号不能为空值 ");
			   	isFail = true;

			  }
		   
		 if (isFail)
		 {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		 }
		  
		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderAbnormalQueryReq> getRequestType()
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderAbnormalQueryReq>(){};
	}

	@Override
	protected DCP_OrderAbnormalQueryRes getResponseType()
	{
		// TODO Auto-generated method stub
		return new DCP_OrderAbnormalQueryRes();
	}

	@Override
	protected DCP_OrderAbnormalQueryRes processJson(DCP_OrderAbnormalQueryReq req) throws Exception
	{
		// TODO Auto-generated method stub
		DCP_OrderAbnormalQueryRes res = this.getResponse();
		DCP_OrderAbnormalQueryRes.level1Elm datas = res.new level1Elm();
		datas.setAbnormalList(new ArrayList<DCP_OrderAbnormalQueryRes.abnormal>());
		String eId = req.getRequest().geteId();
		String orderNo = req.getRequest().getOrderNo();
		
		String sql = "";
		sql += " select * from (";
		sql += " select A.*,B.OITEM,B.MEMO MEMO_DETAIL,B.Status status_Detail,C.PLUNAME from DCP_ORDER_ABNORMALINFO A "
			 + " left join DCP_ORDER_ABNORMALINFO_DETAIL B on  A.eid=B.eid and A.orderno=B.orderno and A.Abnormaltype=b.abnormaltype"
			 + " left join dcp_order_detail  C on C.eid=b.eid and c.orderno=b.orderno and c.item = B.OITEM " ;  	  
		sql += " where A.eid='"+eId+"' and A.orderno='"+orderNo+"' ";
		sql += " )";
		
		List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);
		if(getQDataDetail!=null&&getQDataDetail.isEmpty()==false)
		{
			Map<String, Boolean> condition = new HashMap<String, Boolean>();
			condition.put("ABNORMALTYPE", true);
			
			List<Map<String, Object>> getQHeader = MapDistinct.getMap(getQDataDetail, condition);
			
			condition.put("OITEM", true);
			List<Map<String, Object>> getQDeatil = MapDistinct.getMap(getQDataDetail, condition);
			
			
			for (Map<String, Object> map : getQHeader)
			{
				DCP_OrderAbnormalQueryRes.abnormal  abnormal = res.new abnormal();
				abnormal.setDetail(new ArrayList<DCP_OrderAbnormalQueryRes.abnormalDetail>());
				
				String abnormalType = map.get("ABNORMALTYPE").toString();
				String abnormalTime = map.get("ABNORMALTIME").toString();
				try
				{
					Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(abnormalTime);
					abnormalTime = new SimpleDateFormat("yyyyMMddHHmmss").format(date1);
					
				} catch (Exception e)
				{
					// TODO: handle exception
				}
				
				abnormal.setAbnormalType(abnormalType);
				abnormal.setAbnormalTypeName(map.get("ABNORMALTYPENAME").toString());
				abnormal.setAbnormalTime(abnormalTime);
				abnormal.setMemo(map.get("MEMO").toString());
				abnormal.setStatus(map.get("STATUS").toString());
				for (Map<String, Object> mapDetail : getQDeatil)
				{
					String oItem = mapDetail.get("OITEM").toString();
					String abnormalType_detail = mapDetail.get("ABNORMALTYPE").toString();
					String pluName = "";
					if(oItem==null||oItem.isEmpty())
					{
						continue;
					}
					if(abnormalType_detail.equals(abnormalType)==false)
					{
						continue;
					}
					try
					{
						pluName = mapDetail.get("PLUNAME").toString();
					} catch (Exception e)
					{
						// TODO: handle exception
					}
					DCP_OrderAbnormalQueryRes.abnormalDetail  abnormalDetail = res.new abnormalDetail();
					abnormalDetail.setItem(oItem);
					abnormalDetail.setMemo(mapDetail.get("MEMO_DETAIL").toString());
					abnormalDetail.setStatus(mapDetail.get("STATUS_DETAIL").toString());
					abnormalDetail.setPluName(pluName);
					
					abnormal.getDetail().add(abnormalDetail);
					
				}
				
				datas.getAbnormalList().add(abnormal);
				
			}
			
			
			
		}
		res.setDatas(datas);
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_OrderAbnormalQueryReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

}
