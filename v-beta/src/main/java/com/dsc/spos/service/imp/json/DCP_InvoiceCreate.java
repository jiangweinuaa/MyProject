package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_InvoiceCreateReq;
import com.dsc.spos.json.cust.res.DCP_InvoiceCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：InvoiceCreateDCP
 * 服务说明：发票簿新增
 * @author jinzma	 
 * @since  2019-03-04
 */
public class DCP_InvoiceCreate extends SPosAdvanceService<DCP_InvoiceCreateReq,DCP_InvoiceCreateRes> {

	@Override
	protected void processDUID(DCP_InvoiceCreateReq req, DCP_InvoiceCreateRes res) throws Exception {
		// TODO 自动生成的方法存根
		String eId = req.geteId();
		DCP_InvoiceCreateReq.levelElm request = req.getRequest();
		String sellerGuiNO = request.getSellerGuiNo();
		String year = request.getYear();
		String startMonth =request.getStartMonth();
		String endMonth = request.getEndMonth();
		String invStartNO =request.getInvStartNo();
		String invEndNO = request.getInvEndNo();
		String invType = request.getInvType();
		String invCount = request.getInvCount();
		String invRecipient = request.getInvRecipient();
		try 
		{
			if (checkExist(req) == false)
			{
				/**
				 * 2019-11-18 yyy 增加以下备注信息
				 * 开始和结束号码字轨一致：前两位必须相同。
				 * 前两位之后必须全部是数字
				 * 发票张数 = 结束 - 开始 +1 
				 */

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

				DataValue[] insValue = null;
				String[] columns = {
						"EID","SELLERGUINO","YEAR","STARTMONTH","ENDMONTH","INVSTARTNO","INVENDNO","INVTYPE","SHOPID",
						"MACHINE","ISUSED","INVCOUNT","INVLASTNO","INVRECIPIENT","INVLOAD","STATUS" 
				};
				insValue = new DataValue[]{
						new DataValue(eId, Types.VARCHAR), 
						new DataValue(sellerGuiNO, Types.VARCHAR), 
						new DataValue(Integer.valueOf(year), Types.INTEGER),
						new DataValue(Integer.valueOf(startMonth), Types.INTEGER),								
						new DataValue(Integer.valueOf(endMonth), Types.INTEGER),
						new DataValue(invStartNO, Types.VARCHAR),
						new DataValue(invEndNO, Types.VARCHAR),
						new DataValue(invType, Types.VARCHAR),
						new DataValue("", Types.VARCHAR),
						new DataValue("", Types.VARCHAR),
						new DataValue("N", Types.VARCHAR),
						new DataValue(Integer.valueOf(invCount), Types.INTEGER),
						new DataValue("", Types.VARCHAR),
						new DataValue(invRecipient, Types.VARCHAR),
						new DataValue("2", Types.VARCHAR),
						new DataValue("100", Types.VARCHAR),
				};
				InsBean ib = new InsBean("DCP_INVOICEBOOK", columns);
				ib.addValues(insValue);
				this.addProcessData(new DataProcessBean(ib)); 
				this.doExecuteDataToDB();	
			}
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
	protected List<InsBean> prepareInsertData(DCP_InvoiceCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_InvoiceCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_InvoiceCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_InvoiceCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		DCP_InvoiceCreateReq.levelElm request = req.getRequest();
		String sellerGuiNO = request.getSellerGuiNo();
		String year=request.getYear();
		String startMonth=request.getStartMonth();
		String endMonth=request.getEndMonth();
		String invStartNO=request.getInvStartNo();
		String invEndNO=request.getInvEndNo();
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
	protected TypeToken<DCP_InvoiceCreateReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_InvoiceCreateReq>(){} ;
	}

	@Override
	protected DCP_InvoiceCreateRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_InvoiceCreateRes();
	}

	private boolean checkExist(DCP_InvoiceCreateReq req)  throws Exception {

		String sql = null;
		boolean exist = false;
		String eId = req.geteId();
		DCP_InvoiceCreateReq.levelElm request = req.getRequest();
		String sellerGuiNO = request.getSellerGuiNo();
		String year=request.getYear();
		String startMonth=request.getStartMonth();
		String endMonth=request.getEndMonth();
		String invStartNO=request.getInvStartNo();
		String invEndNO=request.getInvEndNo();
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
