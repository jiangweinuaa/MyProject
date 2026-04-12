package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_InvoiceDeleteReq;
import com.dsc.spos.json.cust.res.DCP_InvoiceDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 服務函數：InvoiceDeleteDCP
 * 服务说明：发票簿删除
 * @author jinzma 
 * @since  2019-03-05
 */
public class DCP_InvoiceDelete extends SPosAdvanceService <DCP_InvoiceDeleteReq,DCP_InvoiceDeleteRes> {

	@Override
	protected void processDUID(DCP_InvoiceDeleteReq req, DCP_InvoiceDeleteRes res) throws Exception {
	// TODO 自动生成的方法存根
		String eId = req.geteId();
		DCP_InvoiceDeleteReq.levelElm request = req.getRequest();
		String sellerGuiNO = request.getSellerGuiNo();
		String year=request.getYear();
		String startMonth=request.getStartMonth();
		String endMonth=request.getEndMonth();
		String invStartNO=request.getInvStartNo();
		String invEndNO=request.getInvEndNo();
		String invType = request.getInvType();
		
		try 
		{
		  //DCP_INVOICEBOOK
			DelBean db1 = new DelBean("DCP_INVOICEBOOK");
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db1.addCondition("SELLERGUINO", new DataValue(sellerGuiNO, Types.VARCHAR));
			db1.addCondition("YEAR", new DataValue(Integer.valueOf(year), Types.INTEGER));
			db1.addCondition("STARTMONTH", new DataValue(Integer.valueOf(startMonth), Types.INTEGER));
			db1.addCondition("ENDMONTH", new DataValue(Integer.valueOf(endMonth), Types.INTEGER));
			db1.addCondition("INVSTARTNO", new DataValue(invStartNO, Types.VARCHAR));
			db1.addCondition("INVENDNO", new DataValue(invEndNO, Types.VARCHAR));
			db1.addCondition("INVTYPE", new DataValue(invType, Types.VARCHAR));
			
			this.addProcessData(new DataProcessBean(db1));
			this.doExecuteDataToDB();	
			
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		}
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_InvoiceDeleteReq req) throws Exception {
	// TODO 自动生成的方法存根
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_InvoiceDeleteReq req) throws Exception {
	// TODO 自动生成的方法存根
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_InvoiceDeleteReq req) throws Exception {
	// TODO 自动生成的方法存根
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_InvoiceDeleteReq req) throws Exception {
	// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		DCP_InvoiceDeleteReq.levelElm request = req.getRequest();
		String sellerGuiNO = request.getSellerGuiNo();
		String year=request.getYear();
		String startMonth=request.getStartMonth();
		String endMonth=request.getEndMonth();
		String invStartNO=request.getInvStartNo();
		String invEndNO=request.getInvEndNo();
		String invType = request.getInvType();

		if (Check.Null(sellerGuiNO)) 
		{
			errMsg.append("统一编号不可为空值, ");
			isFail = true;
		}
		if (Check.Null(year) ) 
		{
			errMsg.append("发票年度不可为空值, ");
			isFail = true;
		}
		else
		{
			if (!PosPub.isNumeric(year))
			{
				errMsg.append("发票年度必须为数值, ");
				isFail = true;
			}
		}
		if (Check.Null(startMonth) ) 
		{
			errMsg.append("起始月份不可为空值, ");
			isFail = true;
		}
		else
		{
			if (!PosPub.isNumeric(startMonth))
			{
				errMsg.append("起始月份必须为数值, ");
				isFail = true;
			}
		}
		if (Check.Null(endMonth) ) 
		{
			errMsg.append("截止月份不可为空值, ");
			isFail = true;
		}
		else
		{
			if (!PosPub.isNumeric(endMonth))
			{
				errMsg.append("截止月份必须为数值, ");
				isFail = true;
			}
		}
		if (Check.Null(invStartNO) ) 
		{
			errMsg.append("起始发票号码不可为空值, ");
			isFail = true;
		}
		if (Check.Null(invEndNO) ) 
		{
			errMsg.append("截止发票号码不可为空值, ");
			isFail = true;
		}
		if (Check.Null(invType) ) 
		{
			errMsg.append("发票联数不可为空值, ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_InvoiceDeleteReq> getRequestType() {
	// TODO 自动生成的方法存根
		return new TypeToken<DCP_InvoiceDeleteReq>(){};
	}

	@Override
	protected DCP_InvoiceDeleteRes getResponseType() {
	// TODO 自动生成的方法存根
		return new DCP_InvoiceDeleteRes();
	}

	
}
