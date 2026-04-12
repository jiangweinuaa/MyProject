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
import com.dsc.spos.json.cust.req.DCP_PayInUpdateReq;
import com.dsc.spos.json.cust.req.DCP_PayInUpdateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_PayInUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import java.util.Calendar;
public class DCP_PayInUpdate extends SPosAdvanceService<DCP_PayInUpdateReq,DCP_PayInUpdateRes>
{

	@Override
	protected void processDUID(DCP_PayInUpdateReq req, DCP_PayInUpdateRes res) throws Exception
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
		String payInNo = req.getRequest().getPayInNo();		
		
		String memo = req.getRequest().getMemo();
		
		
		
		List<Map<String, Object>> getPayIn = this.getExistPayIn(req);
		if(getPayIn==null||getPayIn.isEmpty())
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据不存在，无法修改！ ");
		}
		
		String status = getPayIn.get(0).get("STATUS").toString();//状态（0新增 1已提交 2已上传  3已同意  4已驳回）
		if(!"0".equals(status))
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据状态非新增状态，无法修改！ ");
		}
		
			
		DelBean db1 = new DelBean("DCP_PAYIN_DETAIL");
		db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
		db1.addCondition("PAYINNO", new DataValue(payInNo, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db1)); 
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
		
		UptBean up1 = new UptBean("DCP_PAYIN");
		up1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		up1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
		up1.addCondition("PAYINNO", new DataValue(payInNo, Types.VARCHAR));
		
		
		up1.addUpdateValue("BDATE", new DataValue(bDate, Types.VARCHAR));
		up1.addUpdateValue("RDATE", new DataValue(rDate, Types.VARCHAR));
		up1.addUpdateValue("TOTAMT", new DataValue(totAmt, Types.VARCHAR));
		up1.addUpdateValue("MEMO", new DataValue(memo, Types.VARCHAR));
		up1.addUpdateValue("LASTMODIOPID", new DataValue(createBy, Types.VARCHAR));
		up1.addUpdateValue("LASTMODIOPNAME", new DataValue(createByName, Types.VARCHAR));
		up1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
		up1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
		up1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

		this.addProcessData(new DataProcessBean(up1)); 
		
		this.doExecuteDataToDB();
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PayInUpdateReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PayInUpdateReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PayInUpdateReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PayInUpdateReq req) throws Exception
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if (req.getRequest() == null)
		{
			errMsg.append("requset不能为空值 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		String payInNo = req.getRequest().getPayInNo();
		String bDate = req.getRequest().getbDate();
		String rDate = req.getRequest().getrDate();
		String totAmt = req.getRequest().getTotAmt();

		if (Check.Null(payInNo))
		{
			errMsg.append("单据payInNo不能为空值， ");
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
	protected TypeToken<DCP_PayInUpdateReq> getRequestType()
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PayInUpdateReq>(){};
	}

	@Override
	protected DCP_PayInUpdateRes getResponseType()
	{
		// TODO Auto-generated method stub
		return new DCP_PayInUpdateRes();
	}
	
	private List<Map<String, Object>> getExistPayIn(DCP_PayInUpdateReq req) throws Exception {
		String eId = req.geteId();
		String shopId = req.getShopId();
		String payInNo = req.getRequest().getPayInNo();
		 
		String sql = "select * from DCP_PAYIN where EID='"+ eId+"' and SHOPID='"+shopId+"' and PAYINNO = '"+payInNo+"' " ; 
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);

		return getQData;
	}

	
	
	
}
