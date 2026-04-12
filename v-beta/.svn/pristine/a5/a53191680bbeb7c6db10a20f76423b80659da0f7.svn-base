package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_DualPlayTemQueryReq;
import com.dsc.spos.json.cust.res.DCP_DualPlayTemQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 服務函數：DualPlayGetDCP
 *   說明：双屏播放查询DCP
 * 服务说明：双屏播放查询DCP
 * @author Jinzma 
 * @since  2017-03-09
 */
public class DCP_DualPlayTemQuery extends SPosBasicService<DCP_DualPlayTemQueryReq,DCP_DualPlayTemQueryRes>  {

	@Override
	protected boolean isVerifyFail(DCP_DualPlayTemQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		return false;
	}

	@Override
	protected TypeToken<DCP_DualPlayTemQueryReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_DualPlayTemQueryReq>(){};
	}

	@Override
	protected DCP_DualPlayTemQueryRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_DualPlayTemQueryRes();
	}

	@Override
	protected DCP_DualPlayTemQueryRes processJson(DCP_DualPlayTemQueryReq req) throws Exception 
	{
		// TODO 自动生成的方法存根
		String sql=null;			
		//查詢資料
		DCP_DualPlayTemQueryRes res = null;
		res = this.getResponse();
		int totalRecords = 0;                                //总笔数
		int totalPages = 0;
		if (req.getPageNumber()==0)
		{
			req.setPageNumber(1);
		}
		if (req.getPageSize()==0)
		{
			req.setPageSize(10);
		}
		try
		{
			//单头查询
			sql=this.getQuerySql(req);	
			List<Map<String, Object>> getQData=this.doQueryData(sql, null);
			res.setDatas(new ArrayList<DCP_DualPlayTemQueryRes.level1Elm>());

			if (getQData != null && getQData.isEmpty() == false)
			{
				String num = getQData.get(0).get("NUM").toString();
				totalRecords = Integer.parseInt(num);
				//算總頁數
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
				for (Map<String, Object> oneData1 : getQData)
				{
					DCP_DualPlayTemQueryRes.level1Elm oneLv1 = res.new level1Elm();

					String templateNo = oneData1.get("TEMPLATENO").toString();
					String templateName = oneData1.get("TEMPLATENAME").toString();
					String platformType = oneData1.get("PLATFORMTYPE").toString();
					String shopType = oneData1.get("SHOPTYPE").toString();
					String timeType= oneData1.get("TIMETYPE").toString();
					String memo = oneData1.get("MEMO").toString();
					String pollTime = oneData1.get("POLLTIME").toString();
					String status = oneData1.get("STATUS").toString();

					//设置响应
					oneLv1.setTemplateNo(templateNo);
					oneLv1.setTemplateName(templateName);
					oneLv1.setPlatformType(platformType);
					oneLv1.setShopType(shopType);
					oneLv1.setTimeType(timeType);
					oneLv1.setMemo(memo);
					oneLv1.setStatus(status);
					oneLv1.setPollTime(pollTime);

					res.getDatas().add(oneLv1);
					oneLv1 = null;
				}
			}

			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");

		}
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO 自动生成的方法存根

	}

	@Override
	protected String getQuerySql(DCP_DualPlayTemQueryReq req) throws Exception {
		String sql=null;			
		String eId = req.geteId();
		String keyTxt = "";
		String status = "";
		if (req.getRequest()!=null)
		{
			keyTxt = req.getRequest().getKeyTxt();
			status = req.getRequest().getStatus();
		}
		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();
		int startRow=(pageNumber-1) * pageSize;
		int endRow = startRow+pageSize;

		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append(" select * from (");
		sqlbuf.append(" select count(*)over() num ,row_number() over(order by a.templateno desc ) as rn, a.* from DCP_DUALPLAY_TEMPLATE a "
				+ " where a.EID='"+ eId +"' ");
		if (keyTxt!=null&&keyTxt.trim().length()>0)
		{
			sqlbuf.append(" and (a.templateno like '%%"+keyTxt+"%%' or a.TEMPLATENAME like '%%"+keyTxt+"%%' ) ");
		}
		if (status!=null&&status.trim().length()>0)
		{
			sqlbuf.append(" and a.STATUS='"+status+"' ");
		}
		sqlbuf.append(" ) where rn>"+startRow+" and rn<="+endRow);
		sql = sqlbuf.toString();
		return sql;

	}

}


