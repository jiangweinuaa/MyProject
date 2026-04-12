package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_InvoiceUpdateReq;
import com.dsc.spos.json.cust.res.DCP_InvoiceUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：InvoiceUpdateDCP
 * 服务说明：发票簿修改
 * @author jinzma	 
 * @since  2019-03-04
 */
public class DCP_InvoiceUpdate extends SPosAdvanceService<DCP_InvoiceUpdateReq,DCP_InvoiceUpdateRes> {

	@Override
	protected void processDUID(DCP_InvoiceUpdateReq req, DCP_InvoiceUpdateRes res) throws Exception {
		// TODO 自动生成的方法存根
		String eId = req.geteId();
		DCP_InvoiceUpdateReq.levelElm request = req.getRequest();
		String sellerGuiNO = request.getSellerGuiNo();
		String year = request.getYear();
		String startMonth =request.getStartMonth();
		String endMonth = request.getEndMonth();
		String invStartNO =request.getInvStartNo();
		String invEndNO = request.getInvEndNO();
		String invType = request.getInvType();
		String invCount = request.getInvCount();
		String invRecipient = request.getInvRecipient();		
		String oldInvStartNO = request.getOldInvStartNo();
		String oldInvEndNO = request.getOldInvEndNo();
		String oldInvType = request.getOldInvType();

		try 
		{
			if (checkExist(req) == true)
			{

				//起始和结束号码的字轨必须一致
				String zgstart = invStartNO.substring(0, 2);
				String zgend = invEndNO.substring(0, 2);
				if ( !zgstart.equals(zgend) )
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "发票起始号码和结束号码的字轨不一致 ");

				//发票张数检核
				Integer invstart= Integer.valueOf(invStartNO.substring(2)) ;
				Integer invend= Integer.valueOf(invEndNO.substring(2)) ;
				Integer count = invend - invstart + 1;
				if ( count.compareTo(Integer.valueOf(invCount))!=0 )
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "发票张数计算错误 ");
				}


				//更新
				UptBean ub = null;	
				ub = new UptBean("DCP_INVOICEBOOK");				
				ub.addUpdateValue("INVSTARTNO", new DataValue(invStartNO, Types.VARCHAR));
				ub.addUpdateValue("INVENDNO", new DataValue(invEndNO, Types.VARCHAR));
				ub.addUpdateValue("INVTYPE", new DataValue(invType, Types.VARCHAR));
				ub.addUpdateValue("INVCOUNT", new DataValue(Integer.valueOf(invCount), Types.INTEGER));
				ub.addUpdateValue("INVRECIPIENT", new DataValue(invRecipient, Types.VARCHAR));

				// condition
				ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				ub.addCondition("SELLERGUINO", new DataValue(sellerGuiNO, Types.VARCHAR));
				ub.addCondition("YEAR", new DataValue(Integer.valueOf(year), Types.INTEGER));
				ub.addCondition("STARTMONTH", new DataValue(Integer.valueOf(startMonth), Types.INTEGER));
				ub.addCondition("ENDMONTH", new DataValue(Integer.valueOf(endMonth), Types.INTEGER));
				ub.addCondition("INVSTARTNO", new DataValue(oldInvStartNO, Types.VARCHAR));
				ub.addCondition("INVENDNO", new DataValue(oldInvEndNO, Types.VARCHAR));
				ub.addCondition("INVTYPE", new DataValue(oldInvType, Types.VARCHAR));


				this.addProcessData(new DataProcessBean(ub));
				this.doExecuteDataToDB();

				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");

			}
			else
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "资料不存在，请重新输入！");
			}

		}
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_InvoiceUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_InvoiceUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_InvoiceUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_InvoiceUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		DCP_InvoiceUpdateReq.levelElm request = req.getRequest();
		String sellerGuiNO = request.getSellerGuiNo();
		String year=request.getYear();
		String startMonth=request.getStartMonth();
		String endMonth=request.getEndMonth();
		String invStartNO=request.getInvStartNo();
		String invEndNO=request.getInvEndNO();
		String invType = request.getInvType();
		String invCount =request.getInvCount();

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
		if (Check.Null(invCount) ) 
		{
			errMsg.append("发票张数不可为空值, ");
			isFail = true;
		}
		else
		{
			if (!PosPub.isNumeric(invCount))
			{
				errMsg.append("发票张数必须为数值, ");
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
	protected TypeToken<DCP_InvoiceUpdateReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_InvoiceUpdateReq>(){};
	}

	@Override
	protected DCP_InvoiceUpdateRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_InvoiceUpdateRes(); 
	}

	private boolean checkExist(DCP_InvoiceUpdateReq req)  throws Exception  {
		String sql = null;
		boolean exist = false;
		String eId = req.geteId();
		DCP_InvoiceUpdateReq.levelElm request = req.getRequest();
		String sellerGuiNO = request.getSellerGuiNo();
		String year=request.getYear();
		String startMonth=request.getStartMonth();
		String endMonth=request.getEndMonth();
		String invStartNO=request.getInvStartNo();
		String invEndNO=request.getInvEndNO();
		String invType = request.getInvType();
		String[] conditionValues = {eId,sellerGuiNO,year,startMonth,endMonth,invStartNO,invEndNO,invType }; 				
		sql = " select * from DCP_INVOICEBOOK "
				+ " where  EID=?  and SELLERGUINO=? and YEAR=? and STARTMONTH=? "
				+ " and ENDMONTH=? and INVSTARTNO=? and INVENDNO=? and INVTYPE=?  " ;
		List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);
		if (getQData != null && getQData.isEmpty() == false) {
			exist = true;
		}

		return exist;
	}

}
