package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_ReasonQueryReq;
import com.dsc.spos.json.cust.res.DCP_ReasonQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_ReasonQuery extends SPosBasicService<DCP_ReasonQueryReq, DCP_ReasonQueryRes>
{
	@Override
	protected boolean isVerifyFail(DCP_ReasonQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_ReasonQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_ReasonQueryReq>(){};
	}

	@Override
	protected DCP_ReasonQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_ReasonQueryRes();
	}

	@Override
	protected DCP_ReasonQueryRes processJson(DCP_ReasonQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		try 
		{
			DCP_ReasonQueryRes res = this.getResponse();
			int totalRecords;	//总笔数
			int totalPages;		//总页数

			//给分页字段赋值
			String sql = this.getQuerySql(req);	//查询总笔数
			List<Map<String, Object>> getQData = this.doQueryData(sql, null);

			res.setDatas(new ArrayList<DCP_ReasonQueryRes.level1Elm>());
			if (getQData != null && getQData.isEmpty() == false)
			{
				//算總頁數
				String num = getQData.get(0).get("NUM").toString();
				totalRecords=Integer.parseInt(num);
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

				for (Map<String, Object> oneData : getQData) 
				{
					DCP_ReasonQueryRes.level1Elm oneLv1 = res.new level1Elm();
					String bsNo = oneData.get("BSNO").toString();
					String bsName = oneData.get("REASON_NAME").toString();
					String bsType = oneData.get("BSTYPE").toString();

					oneLv1.setBsNo(bsNo);
					oneLv1.setBsName(bsName);
					oneLv1.setBsType(bsType);

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

			return res;
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_ReasonQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String eId = req.geteId();
		String keyTxt = req.getRequest().getKeyTxt();
		String bsType = req.getRequest().getBsType();
		String langType = req.getLangType();
		String sql="";

		//计算起始位置
		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();
		int startRow = ((pageNumber - 1) * pageSize);
		startRow = ((pageNumber - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
		startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料

		//理由码查询
		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append(""
				+ " select * from ("
				+ " select count(*) over() num,row_number() over (order by a.bsno ) rn,"
				+ " a.*,b.reason_name from dcp_reason a"
				+ " left join dcp_reason_lang b on a.eid=b.eid and a.bsno=b.bsno and a.bstype=b.bstype and b.lang_type='"+langType+"'"
				+ " where a.eid='"+eId+"' and a.status='100'"
				+ " ");
		if (!Check.Null(keyTxt))
		{
			sqlbuf.append(" and (a.bsno like '%%"+ keyTxt +"%%' or b.reason_name like '%%"+ keyTxt +"%%')");
		}
		if (!Check.Null(bsType))
		{
			sqlbuf.append(" and a.bstype='"+bsType+"'");
		}
		sqlbuf.append(" ) tbl");
		sqlbuf.append(" where rn>" + startRow + " and rn <="+(startRow+pageSize) );

		sql = sqlbuf.toString();
		return sql;
	}

}
