package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_DingTemplateQueryReq;
import com.dsc.spos.json.cust.res.DCP_DingTemplateQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
/**
 * 服务函数：DingTemplateGet
 * 服务说明：钉钉审批模板查询
 * @author jinzma
 * @since  2019-10-31
 */
public class DCP_DingTemplateQuery extends SPosBasicService<DCP_DingTemplateQueryReq,DCP_DingTemplateQueryRes>{

	@Override
	protected boolean isVerifyFail(DCP_DingTemplateQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		return false;
	}

	@Override
	protected TypeToken<DCP_DingTemplateQueryReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_DingTemplateQueryReq>(){};
	}

	@Override
	protected DCP_DingTemplateQueryRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_DingTemplateQueryRes();
	}

	@Override
	protected DCP_DingTemplateQueryRes processJson(DCP_DingTemplateQueryReq req) throws Exception {
		// TODO 自动生成的方法存根

		DCP_DingTemplateQueryRes res = this.getResponse();
		try
		{
			String sql=this.getQuerySql(req);	
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
				res.setDatas(new ArrayList<DCP_DingTemplateQueryRes.level1Elm>());

				for (Map<String, Object> oneData : getQData) {
					DCP_DingTemplateQueryRes.level1Elm oneLv1 = new DCP_DingTemplateQueryRes().new level1Elm(); 
					String templateNO = oneData.get("TEMPLATENO").toString();
					String templateName = oneData.get("TEMPLATENAME").toString();
					String status = oneData.get("STATUS").toString();

					oneLv1.setStatus(status);
					oneLv1.setTemplateName(templateName);
					oneLv1.setTemplateNo(templateNO);

					res.getDatas().add(oneLv1);		
					oneLv1 = null;
				}
			}
			else
			{
				res.setDatas(new ArrayList<DCP_DingTemplateQueryRes.level1Elm>());				
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
	protected String getQuerySql(DCP_DingTemplateQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		String sql=null;	
		StringBuffer sqlbuf=new StringBuffer("");
		String eId = req.geteId();
		String keyTxt="";
		String status="";
		if (req.getRequest()!=null)
		{
			keyTxt=req.getRequest().getKeyTxt();
			status=req.getRequest().getStatus();
		}
		//分页处理
		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();
		int startRow=(pageNumber-1) * pageSize;
		sqlbuf.append(" select * from ( "
				+ " select count(*) over() num,row_number() over (order by a.TEMPLATENO) rn, "
				+ " a.* from DCP_DING_TEMPLATE a where a.EID='"+eId+"' " );

		if (!Check.Null(status))
		{
			sqlbuf.append(" and a.status='"+status+"'   ");
		}
		if (!Check.Null(keyTxt))
		{
			sqlbuf.append(" and (a.TEMPLATENO like '%%"+keyTxt+"%%'  or a.TEMPLATENAME like '%%"+keyTxt+"%%') ");
		}

		sqlbuf.append( " ) where  rn>"+startRow+" and rn<="+(startRow+pageSize) );

		sql = sqlbuf.toString();
		return sql;


	}

}
