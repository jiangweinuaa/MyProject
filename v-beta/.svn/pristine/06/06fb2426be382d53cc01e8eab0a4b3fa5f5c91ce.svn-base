package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PayInCreateReq;
import com.dsc.spos.json.cust.req.DCP_SStockInCreateReq;
import com.dsc.spos.json.cust.req.DCP_PayInCreateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_PayInCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import java.util.Calendar;
public class DCP_PayInCreate extends SPosAdvanceService<DCP_PayInCreateReq,DCP_PayInCreateRes>
{

	@Override
	protected void processDUID(DCP_PayInCreateReq req, DCP_PayInCreateRes res) throws Exception
	{
		// TODO Auto-generated method stub
		String eId = req.geteId();
		String shopId = req.getShopId();
		String createBy = req.getOpNO();
		String createByName = req.getOpName();
		String lastmoditime = null;//req.getRequest().getLastmoditime();
		if(lastmoditime==null||lastmoditime.isEmpty())
		{
			lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		}
		String bDate = req.getRequest().getbDate();
		String rDate = req.getRequest().getrDate();
		String totAmt = req.getRequest().getTotAmt();
		String status = "0";//状态（0新增 1已提交 2已上传  3已同意  4已驳回）
		String memo = req.getRequest().getMemo();
		
		if (checkGuid(req))
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据已存在，请重新确认！ ");
		}
		String payInNo = this.getPayInNo(req);
		String[] columns_head ={"EID","SHOPID","PAYINNO","BDATE","RDATE","TOTAMT","MEMO","COMMENT_ERP","STATUS","PROCESS_STATUS",
				"PAYINID","CREATEOPID","CREATEOPNAME","CREATETIME","UPDATE_TIME","TRAN_TIME"};
		
		List<level1Elm> payInList = req.getRequest().getPayInList();
		String[] columns_detail ={"EID","SHOPID","PAYINNO","ITEM","ACCOUNT","BANKNO","BANKDOCNO","CERTIFICATE","AMT"};
		int item = 0;
		for (level1Elm payIn : payInList)
		{
			item++;
			String certificate = payIn.getCertificate();
			if(certificate!=null&&certificate.length()>64)
			{
				certificate = certificate.substring(0, 64);
			}
			DataValue[] insValue1 = null;			
			insValue1 = new DataValue[]{
					new DataValue(eId, Types.VARCHAR),					
					new DataValue(shopId, Types.VARCHAR),
					new DataValue(payInNo, Types.VARCHAR),
					new DataValue(item, Types.VARCHAR),					
					new DataValue(payIn.getAccount(), Types.VARCHAR),
					new DataValue(payIn.getBankNo(), Types.VARCHAR),
					new DataValue(payIn.getBankDocNo(), Types.VARCHAR),
					new DataValue(certificate, Types.VARCHAR),
					new DataValue(payIn.getAmt(), Types.VARCHAR)
				};
			
			InsBean ib1 = new InsBean("DCP_PAYIN_DETAIL", columns_detail);
			ib1.addValues(insValue1);
			this.addProcessData(new DataProcessBean(ib1)); 
		}
		
		
		DataValue[] insValue_head = new DataValue[] 
				{
					new DataValue(eId, Types.VARCHAR),											
					new DataValue(shopId, Types.VARCHAR),					
					new DataValue(payInNo ,Types.VARCHAR),
					new DataValue(bDate ,Types.VARCHAR),
					new DataValue(rDate, Types.VARCHAR),
					new DataValue(totAmt, Types.VARCHAR),
					new DataValue(memo, Types.VARCHAR),
					new DataValue("", Types.VARCHAR),
					new DataValue(status, Types.VARCHAR),
					new DataValue("N", Types.VARCHAR),
					new DataValue(req.getRequest().getPayInId(), Types.VARCHAR),
					new DataValue(createBy, Types.VARCHAR),
					new DataValue(createByName, Types.VARCHAR),
					new DataValue(lastmoditime , Types.DATE) 	,
					new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
					new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
				};
		
		InsBean ib_head = new InsBean("DCP_PAYIN", columns_head);
		ib_head.addValues(insValue_head);
		this.addProcessData(new DataProcessBean(ib_head)); 
		
		this.doExecuteDataToDB();
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PayInCreateReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PayInCreateReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PayInCreateReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PayInCreateReq req) throws Exception
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if (req.getRequest() == null)
		{
			errMsg.append("requset不能为空值 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		String payInId = req.getRequest().getPayInId();
		String bDate = req.getRequest().getbDate();
		String rDate = req.getRequest().getrDate();
		String totAmt = req.getRequest().getTotAmt();

		if (Check.Null(payInId))
		{
			errMsg.append("单据payInId不能为空值， ");
			isFail = true;

		}
		if (Check.Null(bDate))
		{
			errMsg.append("单据日期bDate不能为空值， ");
			isFail = true;

		}
		if (Check.Null(rDate))
		{
			errMsg.append("缴款日期rDate不能为空值， ");
			isFail = true;

		}
		if (Check.Null(totAmt))
		{
			errMsg.append("缴款金额合计totAmt不能为空值， ");
			isFail = true;

		}
		List<level1Elm> payInList = req.getRequest().getPayInList();
		if (payInList == null || payInList.isEmpty())
		{
			errMsg.append("缴款明细payInList不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());

		}

		for (level1Elm oneData : payInList)
		{
			String account = oneData.getAccount();
			String bankNo = oneData.getBankNo();
			String bankDocNo = oneData.getBankDocNo();
			String amt = oneData.getAmt();

			if (Check.Null(account))
			{
				errMsg.append("缴款账号account不能为空值 ,");
				isFail = true;
			}
			if (Check.Null(bankNo))
			{
				errMsg.append("银行编码bankNo不能为空值 ,");
				isFail = true;
			}
			if (Check.Null(bankDocNo))
			{
				errMsg.append("ERP账户编码bankDocNo不能为空值 ,");
				isFail = true;
			}
			if (Check.Null(amt))
			{
				errMsg.append("缴款金额amt不能为空值 ,");
				isFail = true;
			}
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_PayInCreateReq> getRequestType()
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PayInCreateReq>(){};
	}

	@Override
	protected DCP_PayInCreateRes getResponseType()
	{
		// TODO Auto-generated method stub
		return new DCP_PayInCreateRes();
	}
	
	private boolean checkGuid(DCP_PayInCreateReq req) throws Exception {		
		String guid = req.getRequest().getPayInId();
		boolean existGuid;
		String sql = "select PAYINID from DCP_PAYIN where PAYINID = '"+guid+"' " ; 
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);

		if (getQData != null && getQData.isEmpty() == false) {
			existGuid = true;
		} else {
			existGuid =  false;
		}

		return existGuid;
	}

	
	private String getPayInNo(DCP_PayInCreateReq req) throws Exception
	{
		/*
		 * 单据编号在后台按规格生成(固定编码+年月日+5位流水号(比如TBCK201607010001)，流水号取门店该单据最大流水号+1)
		 * 注意固定编码：如果是调拨出库则为DBCK 如果是退货出库则为THCK 如果是次品出库则为CPCK
		 */
		String sql = null;
		String payInNo = null;
		String shopId = req.getShopId();
		String eId = req.geteId();
		StringBuffer sqlbuf = new StringBuffer();
		String bDate = PosPub.getAccountDate_SMS(dao, eId, shopId);

		String[] conditionValues =
		{ eId, shopId }; // 查询要货单号

		payInNo = "JKDJ" + bDate;// 缴款单据

		sqlbuf.append("" + "select PAYINNO  from ( " + "select max(PAYINNO) as PAYINNO " + "  from DCP_PAYIN "
				+ " where EID = ? " + " and SHOPID = ? " + " and PAYINNO like '%%" + payInNo + "%%' "); // 假資料
		sqlbuf.append(" ) TBL ");
		sql = sqlbuf.toString();

		List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);
		if (getQData != null && getQData.isEmpty() == false)
		{
			payInNo = (String) getQData.get(0).get("PAYINNO");

			if (payInNo != null && payInNo.length() > 0)
			{
				long i;
				payInNo = payInNo.substring(4);
				i = Long.parseLong(payInNo) + 1;
				payInNo = i + "";
				payInNo = "JKDJ" + payInNo;

			} else
			{
				payInNo = "JKDJ" + bDate + "00001";
			}
		} else
		{
			payInNo = "JKDJ" + bDate + "00001";
		}

		return payInNo;
	}
	
}
