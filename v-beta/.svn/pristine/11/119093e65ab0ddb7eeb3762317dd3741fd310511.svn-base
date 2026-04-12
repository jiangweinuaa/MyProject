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
import com.dsc.spos.json.cust.req.DCP_CustomerShopUpdateReq;
import com.dsc.spos.json.cust.res.DCP_CustomerShopUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_CustomerShopUpdate extends SPosAdvanceService<DCP_CustomerShopUpdateReq,DCP_CustomerShopUpdateRes> {

	@Override
	protected void processDUID(DCP_CustomerShopUpdateReq req, DCP_CustomerShopUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
		try 
		{
			String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			String eId = req.geteId();
			String customerNO = req.getRequest().getCustomerNo();
			if(!getdataExist(req))
			{
				res.setSuccess(false);
				res.setServiceDescription(customerNO+" 该客户编码不存在！"); 
				return;
			}
			//删除原有单身
			DelBean db1 = new DelBean("DCP_CUSTOMER_SHOP");
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db1.addCondition("CUSTOMERNO", new DataValue(customerNO, Types.VARCHAR));		
			this.addProcessData(new DataProcessBean(db1));

			List<DCP_CustomerShopUpdateReq.Range> rangelist=req.getRequest().getRangeList();

			if (rangelist != null && rangelist.isEmpty() == false && rangelist.size() > 0)
			{
				for (DCP_CustomerShopUpdateReq.Range item : rangelist) 
				{
					String[] columns2 = {"EID","CUSTOMERNO","SHOPID","LASTMODITIME"};
					DataValue[] insValue2 = null;

					insValue2 = new DataValue[]{
							new DataValue(eId, Types.VARCHAR),
							new DataValue(customerNO, Types.VARCHAR),
							new DataValue(item.getShopId(), Types.VARCHAR),
							new DataValue(lastmoditime, Types.DATE)			
							
					};

					InsBean ib2 = new InsBean("DCP_CUSTOMER_SHOP", columns2);
					ib2.addValues(insValue2);
					this.addProcessData(new DataProcessBean(ib2));
				}

			}

			this.doExecuteDataToDB();

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");


		} 
		catch (Exception e) 
		{
			// TODO: handle exception
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_CustomerShopUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_CustomerShopUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_CustomerShopUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_CustomerShopUpdateReq req) throws Exception 
	{	
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；

		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		if (Check.Null(req.getRequest().getCustomerNo())) 
		{
			errCt++;
			errMsg.append("客户编号不可为空值, ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;	
	}

	@Override
	protected TypeToken<DCP_CustomerShopUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_CustomerShopUpdateReq>(){};
	}

	@Override
	protected DCP_CustomerShopUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_CustomerShopUpdateRes();
	}

	protected boolean getdataExist(DCP_CustomerShopUpdateReq req) throws Exception
	{
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		String eId=req.geteId();
		String customerNO=req.getRequest().getCustomerNo();


		sqlbuf.append("select CUSTOMERNO from DCP_CUSTOMER "
				+ "WHERE EID='"+eId +"'  "
				+ "AND CUSTOMERNO='"+customerNO +"'");		

		sql = sqlbuf.toString(); 	

		List<Map<String, Object>> getQData_checkNO = this.doQueryData(sql,null);
		if(getQData_checkNO!=null && !getQData_checkNO.isEmpty())
		{
			return true;
		}
		else
		{
			return false;
		}

	}

}
