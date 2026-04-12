package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_PayInQueryReq;
import com.dsc.spos.json.cust.res.DCP_PayInQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_PayInQuery extends SPosBasicService<DCP_PayInQueryReq,DCP_PayInQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_PayInQueryReq req) throws Exception
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		String beginDate = req.getRequest().getBeginDate();
		String endDate = req.getRequest().getEndDate();
		String dateType = req.getRequest().getDateType();//日期类型（bDate单据日期；rDate缴款日期）
		if (Check.Null(beginDate))
		{
			errMsg.append("起始日期beginDate不能为空值， ");
			isFail = true;

		}
		if (Check.Null(endDate))
		{
			errMsg.append("截止日期endDate不能为空值， ");
			isFail = true;

		}
		if (Check.Null(dateType))
		{
			errMsg.append("日期类型dateType不能为空值， ");
			isFail = true;

		}
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		return isFail;
	}

	@Override
	protected TypeToken<DCP_PayInQueryReq> getRequestType()
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PayInQueryReq>(){};
	}

	@Override
	protected DCP_PayInQueryRes getResponseType()
	{
		// TODO Auto-generated method stub
		return new DCP_PayInQueryRes();
	}

	@Override
	protected DCP_PayInQueryRes processJson(DCP_PayInQueryReq req) throws Exception
	{
		// TODO Auto-generated method stub
		String langType_cur = req.getLangType();
		
		DCP_PayInQueryRes res = this.getResponse();
		
		DCP_PayInQueryRes.level1Elm datas = res.new level1Elm();
		datas.setPayInList(new ArrayList<DCP_PayInQueryRes.level2Elm>());
		
		int totalRecords;								//总笔数
		int totalPages;
		
		String sql = this.getQuerySql(req);
		
		
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		if (getQData != null && getQData.isEmpty() == false) 
		{
			String num = getQData.get(0).get("NUM").toString();
			totalRecords=Integer.parseInt(num);

			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;	
			
			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);
					
			for (Map<String, Object> map : getQData)
			{
				DCP_PayInQueryRes.level2Elm oneLv1 = res.new level2Elm();
				String payInNo = map.get("PAYINNO").toString();
				String bDate = map.get("BDATE").toString();
				String rDate = map.get("RDATE").toString();
				String status = map.get("STATUS").toString();
				String process_status = map.get("PROCESS_STATUS").toString();
				String totAmt = map.get("TOTAMT").toString();
				String memo = map.get("MEMO").toString();
				String comment = map.get("COMMENT_ERP").toString();
				
				oneLv1.setPayInNo(payInNo);
				oneLv1.setbDate(bDate);
				oneLv1.setrDate(rDate);
				oneLv1.setStatus(status);
				oneLv1.setProcess_status(process_status);
				oneLv1.setTotAmt(totAmt);
				oneLv1.setMemo(memo);
				oneLv1.setComment(comment);
				
				oneLv1.setCreateOpId(map.get("CREATEOPID").toString());
				oneLv1.setCreateOpName(map.get("CREATEOPNAME").toString());
				oneLv1.setCreateTime(map.get("CREATETIME").toString());
				oneLv1.setLastModiOpId(map.get("LASTMODIOPID").toString());
				oneLv1.setLastModiOpName(map.get("LASTMODIOPNAME").toString());
				oneLv1.setLastModiTime(map.get("LASTMODITIME").toString());
				
				
				
				datas.getPayInList().add(oneLv1);
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
	protected String getQuerySql(DCP_PayInQueryReq req) throws Exception
	{
		// TODO Auto-generated method stub
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer(); 
		
		String eId = req.geteId();		
		String shopId = req.getShopId();
		String beginDate = req.getRequest().getBeginDate();
		String endDate = req.getRequest().getEndDate();
		String dateType = req.getRequest().getDateType();//日期类型（bDate单据日期；rDate缴款日期）
		
		String keyTxt = req.getRequest().getKeyTxt();
		String status = req.getRequest().getStatus();//0：新建， 1：已确定 ，2：已上传，3：已同意，4：已驳回，空：查全部
		
		
		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();
		//分页起始位置
		int startRow=(pageNumber-1) * pageSize;
		int endRow = startRow+pageSize;
		
		sqlbuf.append(" select *  from (");
		sqlbuf.append(" select COUNT(*) OVER() NUM,rank() over(ORDER BY payInno ) rn ,A.*  from DCP_PAYIN A ");		
		sqlbuf.append(" WHERE A.EID='"+eId+"'  and A.SHOPID='"+shopId+"' ");
		
		if (dateType.toLowerCase().equals("rdate"))
		{
			sqlbuf.append(" and A.RDATE>='"+beginDate+"' and A.RDATE<='"+endDate+"' ");
		}
		else
		{
			sqlbuf.append(" and A.BDATE>='"+beginDate+"' and A.BDATE<='"+endDate+"' ");
		}
		
		if(keyTxt!=null&&!keyTxt.isEmpty())
		{
			sqlbuf.append(" and ( A.PAYINNO like '%%"+keyTxt+"%%')");
		}
		if(status!=null&&!status.isEmpty())
		{
			sqlbuf.append(" and A.STATUS='"+status+"' ");
		}
		if("bDate".equals(dateType)){
			sqlbuf.append(" order by a.bdate,a.PAYINNO ");
		}
		if("rDate".equals(dateType)){
			sqlbuf.append(" order by a.rdate,a.PAYINNO ");
		}
				
		sqlbuf.append(") WHERE rn >"+startRow+" and rn <="+endRow);
		
		sql = sqlbuf.toString();
		
		return sql;
	}

}
