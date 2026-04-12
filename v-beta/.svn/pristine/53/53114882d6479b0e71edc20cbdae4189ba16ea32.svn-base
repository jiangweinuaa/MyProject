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
import com.dsc.spos.json.cust.req.DCP_PayInCheckReq;
import com.dsc.spos.json.cust.res.DCP_PayInCheckRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import java.util.Calendar;
public class DCP_PayInCheck extends SPosAdvanceService<DCP_PayInCheckReq,DCP_PayInCheckRes>
{

	@Override
	protected void processDUID(DCP_PayInCheckReq req, DCP_PayInCheckRes res) throws Exception
	{
		// TODO Auto-generated method stub
		String eId = req.getO_eId();
		String shopId = req.getO_shopId();
		String payInNo = req.getPayInNo();
		String createBy = req.getCheckOpId();
		String createByName = req.getCheckOpName();
		String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		String checkTime = req.getCheckTime();
		String checkStatus = req.getCheckStatus();
		String comment = req.getComment();
		if(checkTime==null||checkTime.isEmpty())
		{
			checkTime =	lastmoditime;
		}
		else
		{
			if(checkTime.length()==19)
			{
				checkTime = checkTime.replace("/", "-");
			}
			else
			{
				checkTime =	lastmoditime;
			}
		}
		
		if(Check.Null(checkStatus))
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "审核状态checkStatus不能为空！ ");
		}
		
		if(checkStatus.equals("3")||checkStatus.equals("4"))
		{
			
		}
		else
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "审核状态checkStatus传值不对！（3-同意; 4-驳回） ");
		}
		
		List<Map<String, Object>> getPayIn = this.getExistPayIn(eId, shopId, payInNo);
		
		if(getPayIn==null||getPayIn.isEmpty())
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据不存在！ ");
		}
		String status = getPayIn.get(0).get("STATUS").toString();//状态（0新增 1已提交 2已上传  3已同意  4已驳回）
		if(!"2".equals(status))
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据状态非已上传状态，无法提交！ ");
		}
		
		UptBean up1 = new UptBean("DCP_PAYIN");
		up1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		up1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
		up1.addCondition("PAYINNO", new DataValue(payInNo, Types.VARCHAR));

		up1.addUpdateValue("STATUS", new DataValue(checkStatus, Types.VARCHAR));
		up1.addUpdateValue("COMMENT_ERP", new DataValue(comment, Types.VARCHAR));
		up1.addUpdateValue("CHECKOPID", new DataValue(createBy, Types.VARCHAR));
		up1.addUpdateValue("CHECKOPNAME", new DataValue(createByName, Types.VARCHAR));
		up1.addUpdateValue("LASTMODITIME", new DataValue(checkTime, Types.DATE));
        up1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
        up1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

		this.addProcessData(new DataProcessBean(up1)); 
		
		this.doExecuteDataToDB();
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");
		
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PayInCheckReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PayInCheckReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PayInCheckReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PayInCheckReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_PayInCheckReq> getRequestType()
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PayInCheckReq>(){};
	}

	@Override
	protected DCP_PayInCheckRes getResponseType()
	{
		// TODO Auto-generated method stub
		return new DCP_PayInCheckRes();
	}
	
	private List<Map<String, Object>> getExistPayIn(String eId, String shopId,String payInNo) throws Exception 
	{		
		String sql = "select * from DCP_PAYIN where EID='"+ eId+"' and SHOPID='"+shopId+"' and PAYINNO = '"+payInNo+"' " ; 
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);

		return getQData;
	}

}
