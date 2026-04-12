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
import com.dsc.spos.json.cust.req.DCP_PayInDeleteReq;
import com.dsc.spos.json.cust.res.DCP_PayInDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_PayInDelete extends SPosAdvanceService<DCP_PayInDeleteReq,DCP_PayInDeleteRes>
{

	@Override
	protected void processDUID(DCP_PayInDeleteReq req, DCP_PayInDeleteRes res) throws Exception
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
		
		String payInNo = req.getRequest().getPayInNo();		
		
	
		
		List<Map<String, Object>> getPayIn = this.getExistPayIn(req);
		if(getPayIn==null||getPayIn.isEmpty())
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据不存在，无法删除！ ");
		}
		
		String status = getPayIn.get(0).get("STATUS").toString();//状态（0新增 1已提交 2已上传  3已同意  4已驳回）
		if(!"0".equals(status))
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据状态非新增状态，无法删除！ ");
		}
		
			
		DelBean db1 = new DelBean("DCP_PAYIN_DETAIL");
		db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
		db1.addCondition("PAYINNO", new DataValue(payInNo, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db1)); 
		
		db1 = new DelBean("DCP_PAYIN");
		db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
		db1.addCondition("PAYINNO", new DataValue(payInNo, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db1)); 

						
		this.doExecuteDataToDB();
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PayInDeleteReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PayInDeleteReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PayInDeleteReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PayInDeleteReq req) throws Exception
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if (req.getRequest() == null)
		{
			errMsg.append("requset不能为空值 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		String payInNo = req.getRequest().getPayInNo();
		

		if (Check.Null(payInNo))
		{
			errMsg.append("单据payInNo不能为空值， ");
			isFail = true;

		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_PayInDeleteReq> getRequestType()
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PayInDeleteReq>(){};
	}

	@Override
	protected DCP_PayInDeleteRes getResponseType()
	{
		// TODO Auto-generated method stub
		return new DCP_PayInDeleteRes();
	}
	
	private List<Map<String, Object>> getExistPayIn(DCP_PayInDeleteReq req) throws Exception {
		String eId = req.geteId();
		String shopId = req.getShopId();
		String payInNo = req.getRequest().getPayInNo();
		 
		String sql = "select * from DCP_PAYIN where EID='"+ eId+"' and SHOPID='"+shopId+"' and PAYINNO = '"+payInNo+"' " ; 
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);

		return getQData;
	}

	
	
	
}
