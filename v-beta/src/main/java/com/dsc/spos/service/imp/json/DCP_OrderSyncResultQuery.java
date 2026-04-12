package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_OrderSyncResultQueryReq;
import com.dsc.spos.json.cust.res.DCP_OrderSyncResultQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：OrderSyncResultGet
 * 服务说明：同步结果查询
 * @author jinzma 
 * @since  2019-04-11
 */
public class DCP_OrderSyncResultQuery  extends SPosBasicService <DCP_OrderSyncResultQueryReq,DCP_OrderSyncResultQueryRes > {

	@Override
	protected boolean isVerifyFail(DCP_OrderSyncResultQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		return false;
	}

	@Override
	protected TypeToken<DCP_OrderSyncResultQueryReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_OrderSyncResultQueryReq>(){};
	}

	@Override
	protected DCP_OrderSyncResultQueryRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_OrderSyncResultQueryRes();
	}

	@Override
	protected DCP_OrderSyncResultQueryRes processJson(DCP_OrderSyncResultQueryReq req) throws Exception {
		// TODO 自动生成的方法存根

		String sql=null;			
		DCP_OrderSyncResultQueryRes res = this.getResponse();	
		try
		{
			sql=this.getQuerySql(req);	
			List<Map<String, Object>> getQData=this.doQueryData(sql, null);
			int totalRecords;								//总笔数
			int totalPages;									//总页数
			if (getQData != null && getQData.isEmpty() == false) 
			{
				//算總頁數
				String num = getQData.get(0).get("NUM").toString();
				totalRecords=Integer.parseInt(num);
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

				res.setDatas(new ArrayList<DCP_OrderSyncResultQueryRes.level1Elm>());
				for (Map<String, Object> oneData : getQData) {
					DCP_OrderSyncResultQueryRes.level1Elm oneLv1 = res.new level1Elm(); 
					String erpShopNO= oneData.get("SHOPID").toString();
					String erpShopName = oneData.get("ORG_NAME").toString();
					String transID = oneData.get("TRANS_ID").toString();
					String loadDocType = oneData.get("LOAD_DOCTYPE").toString();
					String transType = oneData.get("TRANS_TYPE").toString();
					String createByName = oneData.get("CREATEBYNAME").toString();
					String transDate = oneData.get("TRANS_DATE").toString();
					String transTime = oneData.get("TRANS_TIME").toString();
					String transFlg = oneData.get("TRANS_FLG").toString();
					String descripition = oneData.get("DESCRIPTION").toString();

					oneLv1.setCreateByName(createByName);
					oneLv1.setDescripition(descripition);
					oneLv1.setErpShopName(erpShopName);
					oneLv1.setErpShopNO(erpShopNO);
					oneLv1.setLoadDocType(loadDocType);
					oneLv1.setTransDate(transDate);
					oneLv1.setTransFlg(transFlg);
					oneLv1.setTransID(transID);
					oneLv1.setTransTime(transTime);
					oneLv1.setTransType(transType);

					res.getDatas().add(oneLv1);
					oneLv1 = null;
				}
			}
			else
			{
				res.setDatas(new ArrayList<DCP_OrderSyncResultQueryRes.level1Elm>());				
				totalRecords = 0;
				totalPages = 0;			
			}

			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);
			return res;		
		}
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}


	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO 自动生成的方法存根

	}

	@Override
	protected String getQuerySql(DCP_OrderSyncResultQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		String sql=null;	
		StringBuffer sqlbuf=new StringBuffer("");
		String eId = req.geteId();
		String langType=req.getLangType();		
		String beginDate = req.getBeginDate();
		String endDate = req.getEndDate();
		String[] loadDocTypes = req.getLoadDocType();
		String loadDocType="";
		if (loadDocTypes.length > 0) 
		{
			loadDocType=loadDocTypes[0].toString();
		}		
		String erpShopNO= req.getErpShopNO();
		String transType = req.getTransType();
		String transFlg= req.getTransFlg();

		//分页处理
		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();
		int startRow=(pageNumber-1) * pageSize;

		sqlbuf.append( "select * from ("
				+ " select count(*) over() num,row_number() over( order by a.trans_date desc ,a.trans_time desc ) rn, "
				+ " a.LOAD_DOCTYPE,a.TRANS_TYPE,a.TRANS_ID,a.SHOPID,b.ORG_NAME,a.TRANS_DATE,a.TRANS_TIME,a.TRANS_FLG, "
				+ " a.CREATEBYNAME,a.DESCRIPTION from  OC_transtask a  "
				+ " left join DCP_ORG_lang b on a.EID=b.EID and a.SHOPID=b.organizationno and b.status='100' and b.lang_type='"+langType+"' "
				+ " where a.EID='"+eId+"'  ");

		if (!Check.Null(beginDate)&&!Check.Null(endDate))
		{
			sqlbuf.append(" and trans_date >='"+ beginDate +"' and trans_date<='"+ endDate +"'   ");
		}

		if (!Check.Null(loadDocType))
		{
			sqlbuf.append(" and load_DocType='"+loadDocType+"'   ");
		}	

		if (!Check.Null(erpShopNO))
		{
			sqlbuf.append(" and SHOPID='"+erpShopNO+"'   ");
		}			 

		if (!Check.Null(transType))
		{
			sqlbuf.append(" and trans_Type='"+transType+"'  ");
		}			

		if (!Check.Null(transFlg))
		{
			sqlbuf.append(" and trans_Flg='"+transFlg+"'  ");
		}	
		sqlbuf.append(" ) ");
		sqlbuf.append(" where rn>"+startRow+" and rn<="+(startRow+pageSize));			

		sql = sqlbuf.toString();
		return sql;

	}



}
