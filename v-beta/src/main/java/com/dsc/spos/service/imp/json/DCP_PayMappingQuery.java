package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_PayMappingQueryReq;
import com.dsc.spos.json.cust.res.DCP_PayMappingQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 平台映射方式查询	
 * @author yuanyy 2019-04-24
 *
 */
public class DCP_PayMappingQuery extends SPosBasicService<DCP_PayMappingQueryReq, DCP_PayMappingQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_PayMappingQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_PayMappingQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PayMappingQueryReq>(){};
	}

	@Override
	protected DCP_PayMappingQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_PayMappingQueryRes();
	}

	@Override
	protected DCP_PayMappingQueryRes processJson(DCP_PayMappingQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		
		DCP_PayMappingQueryRes res = null;
		res = this.getResponse();
		String sql = null;
		int totalRecords = 0;								//总笔数
		int totalPages = 0;
		if (req.getPageNumber()==0)
		{
			req.setPageNumber(1);
		}
		if (req.getPageSize()==0)
		{
			req.setPageSize(10);
		}

		//单头查询
		sql=this.getQuerySql(req);	
		List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, null);
		res.setDatas(new ArrayList<DCP_PayMappingQueryRes.level1Elm>());
		if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
		{
			Map<String, Object> oneData_Count = getQDataDetail.get(0);
			String num = oneData_Count.get("NUM").toString();
			totalRecords=Integer.parseInt(num);
			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
			for (Map<String, Object> oneData : getQDataDetail) 
			{
				DCP_PayMappingQueryRes.level1Elm oneLv1 = res.new level1Elm();
				
				String channelType = oneData.get("CHANNELTYPE").toString();
				String channelTypeName = oneData.get("CHANNELTYPENAME").toString();
				String channelId = oneData.get("CHANNELID").toString();
				String channelIdName = oneData.get("CHANNELIDNAME").toString();
				String payName = oneData.get("PAYNAME").toString();
				String payType = oneData.get("PAYTYPE").toString();
				String order_paycode = oneData.get("ORDER_PAYCODE").toString();
				String order_payname = oneData.get("ORDER_PAYNAME").toString();
                String createBy = oneData.getOrDefault("CREATEOPNAME","").toString();
                String create_date = oneData.getOrDefault("CREATETIME","").toString();
                String modifyBy = oneData.getOrDefault("LASTMODIOPNAME","").toString();
                String modify_date = oneData.getOrDefault("LASTMODITIME","").toString();

				oneLv1.setChannelType(channelType);
				oneLv1.setChannelTypeName(channelTypeName);
				oneLv1.setChannelId(channelId);
				oneLv1.setChannelIdName(channelIdName);
				oneLv1.setPayType(payType);
				oneLv1.setPayName(payName);
				oneLv1.setOrder_paycode(order_paycode);
				oneLv1.setOrder_payname(order_payname);
                oneLv1.setCreateBy(createBy);
                oneLv1.setCreate_date(create_date);
                oneLv1.setModifyBy(modifyBy);
                oneLv1.setModify_date(modify_date);

				res.getDatas().add(oneLv1);
				
			}
		}
		else
		{
			totalRecords = 0;
			totalPages = 0;
		}	
		
		res.setPageNumber(req.getPageNumber());
		res.setPageSize(req.getPageSize());
		res.setTotalRecords(totalRecords);
		res.setTotalPages(totalPages);
		
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");
	

		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_PayMappingQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql=null;			
		String eId= req.geteId();
		String keyTxt = "";
		String docType = "";
		String channelId = "";
		if(req.getRequest()!=null)
		{
			keyTxt = req.getRequest().getKeyTxt();
			docType = req.getRequest().getChannelType();
			channelId = req.getRequest().getChannelId();
		}
		
		
		int pageSize = req.getPageSize(); 
		//計算起啟位置
		int startRow = ((req.getPageNumber() - 1) * req.getPageSize());
		startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
		startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料
		
		if(pageSize == 0){
			pageSize = 10;
		}
		int endRow = startRow+pageSize;
		
		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append( " SELECT * FROM ("
			+ " SELECT count(*) over () NUM  , ROWNUM AS rn ,  a.*  "
			+ " FROM DCP_PAYMENTMAPPING a "
			+ " WHERE EID = '"+eId+"' " );
		
		if (keyTxt != null && keyTxt.length()>0)
		{
			sqlbuf.append(" AND (a.PAYTYPE like '%%"+ keyTxt +"%%' OR a.PAYNAME LIKE '%%"+ keyTxt +"%%' OR a.ORDER_PAYCODE like '%%"+keyTxt+"%%' OR a.ORDER_PAYNAME like '%%"+keyTxt+"%%')  ");
		}
		
		if (docType != null && docType.length()>0)
		{
			sqlbuf.append(" AND a.CHANNELTYPE = '"+ docType +"'  ");
		}
		if (channelId != null && channelId.length()>0)
		{
			sqlbuf.append(" AND a.CHANNELID = '"+ channelId +"'  ");
		}
		
		sqlbuf.append( " order by a.CHANNELTYPE ) WHERE rn>" + startRow + " and rn<=" + endRow);
		sql = sqlbuf.toString();
		return sql;
	}

}
