package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_ReceivingOrgQueryReq;
import com.dsc.spos.json.cust.res.DCP_ReceivingOrgQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_ReceivingOrgQuery extends SPosBasicService<DCP_ReceivingOrgQueryReq, DCP_ReceivingOrgQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_ReceivingOrgQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_ReceivingOrgQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_ReceivingOrgQueryReq>(){};
	}

	@Override
	protected DCP_ReceivingOrgQueryRes getResponseType() {
		// TODO Auto-generated method stub

		return new DCP_ReceivingOrgQueryRes();
	}

	@Override
	protected DCP_ReceivingOrgQueryRes processJson(DCP_ReceivingOrgQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		try 
		{
			DCP_ReceivingOrgQueryRes res= this.getResponse();
			String sql = this.getQuerySql(req);
			List<Map<String, Object>> getQData=this.doQueryData(sql, null);
			int totalRecords;								//总笔数
			int totalPages;									//总页数
			res.setDatas(new ArrayList<DCP_ReceivingOrgQueryRes.level1Elm>());
			if (getQData != null && getQData.isEmpty() == false)
			{
				//算總頁數
				String num = getQData.get(0).get("NUM").toString();
				totalRecords=Integer.parseInt(num);
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

				for (Map<String, Object> oneDate : getQData) 
				{
					DCP_ReceivingOrgQueryRes.level1Elm oneLv1=res.new level1Elm();
					String orgNo = oneDate.get("ORGANIZATIONNO").toString();
					String orgName = oneDate.get("ORG_NAME").toString();
					String orgForm = oneDate.get("ORG_FORM").toString();

					oneLv1.setOrgNo(orgNo);
					oneLv1.setOrgName(orgName);
					oneLv1.setOrgForm(orgForm);

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
	protected String getQuerySql(DCP_ReceivingOrgQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String eId = req.geteId();
		String keyTxt=req.getRequest().getKeyTxt();
		String langType = req.getLangType();
		String orgForm = req.getRequest().getOrgForm();
		//計算起啟位置
		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();
		int startRow = (pageNumber - 1) * pageSize ;
		startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
		startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料
		String sql="";
		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append(""
				+ " select * from ("
				+ " select count(*) over() num,row_number() over (order by a.organizationno ) rn,"
				+ " a.organizationno,a.org_form,b.org_name from dcp_org a"
				+ " left join dcp_org_lang b on a.eid=b.eid and a.organizationno=b.organizationno and b.lang_type='"+langType+"'"
				+ " where a.eid='"+eId+"' and a.status='100' and a.isdistbr='Y' "    //20200902 按红艳要求discentre修改为isdistbr  收货组织只能选总部 
				+ " ");
		
		if (!Check.Null(orgForm))
		{
			sqlbuf.append(" and a.org_form='"+orgForm+"' ");
		}
		if (!Check.Null(keyTxt))
		{
			sqlbuf.append(" and (a.organizationno like '%%"+keyTxt+"%%' or b.org_name like '%%"+keyTxt+"%%') ");
		}
		sqlbuf.append(""
				+ " ) tbl"
				+ " where rn >" + startRow + " and rn <=" + (startRow+pageSize) );		

		sql=sqlbuf.toString();	
		return sql;
	}

}
