package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_PayInDetailReq;
import com.dsc.spos.json.cust.res.DCP_PayInDetailRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_PayInDetail extends SPosBasicService<DCP_PayInDetailReq,DCP_PayInDetailRes>
{

	@Override
	protected boolean isVerifyFail(DCP_PayInDetailReq req) throws Exception
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

		String payInNo = req.getRequest().getPayInNo();
		
		if (Check.Null(payInNo))
		{
			errMsg.append("单据编号payInNo不能为空值， ");
			isFail = true;

		}
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		return isFail;
	}

	@Override
	protected TypeToken<DCP_PayInDetailReq> getRequestType()
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PayInDetailReq>(){};
	}

	@Override
	protected DCP_PayInDetailRes getResponseType()
	{
		// TODO Auto-generated method stub
		return new DCP_PayInDetailRes();
	}

	@Override
	protected DCP_PayInDetailRes processJson(DCP_PayInDetailReq req) throws Exception
	{
		// TODO Auto-generated method stub
		String langType_cur = req.getLangType();
		
		DCP_PayInDetailRes res = this.getResponse();
		
		DCP_PayInDetailRes.level1Elm datas = res.new level1Elm();
		
			
		String sql = this.getQuerySql(req);
		
		
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		if (getQData != null && getQData.isEmpty() == false) 
		{		
			Map<String, Object> map = getQData.get(0);
			String payInNo = map.get("PAYINNO").toString();
			String bDate = map.get("BDATE").toString();
			String rDate = map.get("RDATE").toString();
			String status = map.get("STATUS").toString();
			String process_status = map.get("PROCESS_STATUS").toString();
			String totAmt = map.get("TOTAMT").toString();
			String memo = map.get("MEMO").toString();
			String comment = map.get("COMMENT_ERP").toString();
			
			datas.setPayInNo(payInNo);
			datas.setbDate(bDate);
			datas.setrDate(rDate);
			datas.setStatus(status);
			datas.setProcess_status(process_status);
			datas.setTotAmt(totAmt);
			datas.setMemo(memo);
			datas.setComment(comment);
			
			datas.setCreateOpId(map.get("CREATEOPID").toString());
			datas.setCreateOpName(map.get("CREATEOPNAME").toString());
			datas.setCreateTime(map.get("CREATETIME").toString());
			datas.setLastModiOpId(map.get("LASTMODIOPID").toString());
			datas.setLastModiOpName(map.get("LASTMODIOPNAME").toString());
			datas.setLastModiTime(map.get("LASTMODITIME").toString());
			
			datas.setCheckOpId(map.get("CHECKOPID").toString());
			datas.setCheckOpName(map.get("CHECKOPNAME").toString());
			datas.setCheckTime(map.get("CHECKTIME").toString());
			datas.setPayInList(new ArrayList<DCP_PayInDetailRes.level2Elm>());
			
			for (Map<String, Object> mapDetail : getQData)
			{				
				DCP_PayInDetailRes.level2Elm oneLv1 = res.new level2Elm();
				oneLv1.setAccount(mapDetail.get("ACCOUNT").toString());
				oneLv1.setAmt(mapDetail.get("AMT").toString());
				oneLv1.setBankNo(mapDetail.get("BANKNO").toString());
				oneLv1.setBankName(mapDetail.get("BANKNAME").toString());
				oneLv1.setBankDocNo(mapDetail.get("BANKDOCNO").toString());
				oneLv1.setCertificate(mapDetail.get("CERTIFICATE").toString());
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
	protected String getQuerySql(DCP_PayInDetailReq req) throws Exception
	{
		// TODO Auto-generated method stub
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer(); 
		
		String langType_cur = req.getLangType();
		String eId = req.geteId();		
		String shopId = req.getShopId();
		String payInNo = req.getRequest().getPayInNo();
		
		sqlbuf.append(" select *  from (");
		sqlbuf.append(" select A.*,B.ACCOUNT,B.BANKNO,B.BANKDOCNO,B.CERTIFICATE,B.AMT,BL.BANKNAME from DCP_PAYIN A ");		
		sqlbuf.append(" left join Dcp_Payin_Detail B on A.EID=B.EID AND A.SHOPID=B.SHOPID AND A.PAYINNO=B.PAYINNO ");
		sqlbuf.append(" left join dcp_bank_lang BL on BL.EID=B.EID AND BL.BANKNO=B.BANKNO AND BL.LANG_TYPE='"+langType_cur+"' ");
		sqlbuf.append(" WHERE A.EID='"+eId+"'  and A.SHOPID='"+shopId+"' and A.PAYINNO='"+payInNo+"' ");						
		sqlbuf.append(")");
		
		sql = sqlbuf.toString();
		
		return sql;
	}

}
