package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ReqCustomerUpdateReq;
import com.dsc.spos.json.cust.res.DCP_ReqCustomerUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_ReqCustomerUpdate extends SPosAdvanceService<DCP_ReqCustomerUpdateReq, DCP_ReqCustomerUpdateRes> 
{

	@Override
	protected void processDUID(DCP_ReqCustomerUpdateReq req, DCP_ReqCustomerUpdateRes res) throws Exception {
	// TODO Auto-generated method stub
		String eId=req.geteId();;
		String modifBy = req.getOpNO();
		Calendar cal = Calendar.getInstance();// 获得当前时间
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String modifDate = df.format(cal.getTime());
		df = new SimpleDateFormat("HHmmss");
		String modifTime = df.format(cal.getTime());
	  //region 更新单头 删除单身
		UptBean ub1 = null;	
		ub1 = new UptBean("DCP_REQCUSTOMER");
		ub1.addUpdateValue("CUSTOMER_NAME", new DataValue(req.getCustomerName(), Types.VARCHAR));
		ub1.addUpdateValue("ABBR", new DataValue(req.getAbbr(), Types.VARCHAR));
		ub1.addUpdateValue("SALER", new DataValue(req.getSaler(), Types.VARCHAR));
		ub1.addUpdateValue("ABBRNO", new DataValue(req.getAbbrNO(), Types.VARCHAR));
		ub1.addUpdateValue("TAXNO", new DataValue(req.getTaxNO(), Types.VARCHAR));
		ub1.addUpdateValue("DISTRICT", new DataValue(req.getDistrict(), Types.VARCHAR));
		ub1.addUpdateValue("REGISTER_AMT", new DataValue(Float.parseFloat(req.getRegisterAmt()), Types.FLOAT));
		ub1.addUpdateValue("ANNUAL_AMT", new DataValue(Float.parseFloat(req.getAnnualAmt()), Types.FLOAT));
		ub1.addUpdateValue("OPEN_DATE", new DataValue(req.getOpenDate(), Types.VARCHAR));
		ub1.addUpdateValue("STAFFS_QTY", new DataValue(Float.parseFloat(req.getStaffsQty()), Types.FLOAT));
		ub1.addUpdateValue("LEGAL_PERSON", new DataValue(req.getLegalPerson(), Types.VARCHAR));
		ub1.addUpdateValue("ACCOUNT_PERSON", new DataValue(req.getAccountPerson(), Types.VARCHAR));
		ub1.addUpdateValue("ACCOUNT_TELEPHONE", new DataValue(req.getAccountTelephone(), Types.VARCHAR));
		ub1.addUpdateValue("EMAIL", new DataValue(req.geteMail(), Types.VARCHAR));
		ub1.addUpdateValue("FAX", new DataValue(req.getFax(), Types.VARCHAR));
		ub1.addUpdateValue("MEMO", new DataValue(req.getMemo(), Types.VARCHAR));
		ub1.addUpdateValue("URL", new DataValue(req.getUrl(), Types.VARCHAR));
		ub1.addUpdateValue("OFFICE_ADDRESS", new DataValue(req.getOfficeAddress(), Types.VARCHAR));
		ub1.addUpdateValue("DELIVERY_ADDRESS", new DataValue(req.getDeliveryAddress(), Types.VARCHAR));
		ub1.addUpdateValue("INVOICE_ADDRESS", new DataValue(req.getFax(), Types.VARCHAR));
		ub1.addUpdateValue("TELEPHONE", new DataValue(req.getTelephone(), Types.VARCHAR));
		ub1.addUpdateValue("ADDRESS", new DataValue(req.getAddress(), Types.VARCHAR));
		ub1.addUpdateValue("LINK_PERSON", new DataValue(req.getLinkPerson(), Types.VARCHAR));
		ub1.addUpdateValue("CREDIT_TYPE", new DataValue(req.getCreditType(), Types.VARCHAR));
		ub1.addUpdateValue("CREDIT_AMT", new DataValue(Float.parseFloat(req.getCreditAmt()), Types.FLOAT));
		ub1.addUpdateValue("PAY_CUSTOMER", new DataValue(req.getPayCustomer(), Types.VARCHAR));
		ub1.addUpdateValue("COLLECT_OBJECT", new DataValue(req.getCollectObject(), Types.VARCHAR));
		ub1.addUpdateValue("COLLECT_SHOP", new DataValue(req.getCollectShop(), Types.VARCHAR));
		ub1.addUpdateValue("PAY_TYPE", new DataValue(req.getPayType(), Types.VARCHAR));
		ub1.addUpdateValue("PAY_DAY", new DataValue(Float.parseFloat(req.getPayDay()), Types.FLOAT));
		ub1.addUpdateValue("PAY_MONTH", new DataValue(Float.parseFloat(req.getPayMonth()), Types.FLOAT));
		ub1.addUpdateValue("CREDIT_MONTH", new DataValue(Float.parseFloat(req.getCreditMonth()), Types.FLOAT));
		ub1.addUpdateValue("CREDIT_DAY", new DataValue(Float.parseFloat(req.getCreditDay()), Types.FLOAT));
		ub1.addUpdateValue("IS_CRETAIL", new DataValue(req.getIsCretail(), Types.VARCHAR));
		ub1.addUpdateValue("IS_CORDER", new DataValue(req.getIsCorder(), Types.VARCHAR));
		ub1.addUpdateValue("STATUS", new DataValue(req.getStatus(), Types.VARCHAR));
				
		ub1.addUpdateValue("MODIFYBY", new DataValue(modifBy,Types.VARCHAR));
		ub1.addUpdateValue("MODIFY_DATE", new DataValue(modifDate,Types.VARCHAR));
		ub1.addUpdateValue("MODIFY_TIME", new DataValue(modifTime,Types.VARCHAR));
		
		
		ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		ub1.addCondition("CUSTOMER_ID", new DataValue(req.getCustomerID(), Types.VARCHAR));
    this.addProcessData(new DataProcessBean(ub1));
    
    DelBean db1 = new DelBean("DCP_REQCUSTOMER_SHOP");
		db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		db1.addCondition("CUSTOMER_ID", new DataValue(req.getCustomerID(), Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db1));		
		//endregion
		//region 新增单身
		List<Map<String, String>> datas=req.getDatas();
		for (Map<String, String> par : datas)
		{
			String[] columns2 = {"EID","CUSTOMER_ID","SHOPID"};
			DataValue[] insValue2 = null;
			insValue2 = new DataValue[] 
					{
							new DataValue(eId, Types.VARCHAR),
							new DataValue(req.getCustomerID(), Types.VARCHAR),
							new DataValue(par.get("SHOPID"), Types.VARCHAR)							
					};
			InsBean ib2 = new InsBean("DCP_REQCUSTOMER_SHOP", columns2);
			ib2.addValues(insValue2);
			this.addProcessData(new DataProcessBean(ib2)); // 新增单身
    
		}
		//endregion
    this.doExecuteDataToDB();
		
    if (res.isSuccess()) 
		{
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		}	
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_ReqCustomerUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_ReqCustomerUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_ReqCustomerUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_ReqCustomerUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
		boolean isFail = false;
    StringBuffer errMsg = new StringBuffer("");
    int errCt = 0;    
    if (Check.Null(req.getCustomerID())) {
      isFail = true;
      errCt++;
      errMsg.append("客户ID不可为空值, ");
    }  
    if (Check.Null(req.getCustomerName())) {
      isFail = true;
      errCt++;
      errMsg.append("客户名称不可为空值, ");
    }  
    if (Check.Null(req.getDeliveryAddress())) {
      isFail = true;
      errCt++;
      errMsg.append("送货地点不可为空值, ");
    }
    if (Check.Null(req.getTelephone())) {
      isFail = true;
      errCt++;
      errMsg.append("联系电话不可为空值, ");
    }
    if (Check.Null(req.getLinkPerson())) {
      isFail = true;
      errCt++;
      errMsg.append("联系人不可为空值, ");
    }
    if (Check.Null(req.getCreditType())) {
      isFail = true;
      errCt++;
      errMsg.append("额度管控不可为空值, ");
    }
    if (Check.Null(req.getCollectObject())) {
      isFail = true;
      errCt++;
      errMsg.append("回款对象不可为空值, ");
    }
    if (Check.Null(req.getPayType())) {
      isFail = true;
      errCt++;
      errMsg.append("结算方式不可为空值, ");
    }
    if (Check.Null(req.getCreditMonth())) {
      isFail = true;
      errCt++;
      errMsg.append("回款周期不可为空值, ");
    }
    if (Check.Null(req.getCreditDay())) {
      isFail = true;
      errCt++;
      errMsg.append("回款结算日不可为空值, ");
    }
    if (Check.Null(req.getIsCretail())) {
      isFail = true;
      errCt++;
      errMsg.append("是否允许赊销零售单不可为空值, ");
    }
    if (Check.Null(req.getIsCorder())) {
      isFail = true;
      errCt++;
      errMsg.append("是否允许赊销门店订单不可为空值, ");
    }
    if (Check.Null(req.getStatus())) {
      isFail = true;
      errCt++;
      errMsg.append("状态不可为空值, ");
    }
    if(Check.Null(req.getRegisterAmt()))
		{
			req.setRegisterAmt("0");
		}
    if(Check.Null(req.getAnnualAmt()))
		{
			req.setAnnualAmt("0");
		}
    if(Check.Null(req.getStaffsQty()))
		{
			req.setStaffsQty("0");
		}
    if(Check.Null(req.getCreditAmt()))
		{
			req.setCreditAmt("0");
		}
    if(Check.Null(req.getPayDay()))
		{
			req.setPayDay("0");
		}
    if(Check.Null(req.getPayMonth()))
		{
			req.setPayMonth("0");
		}
    if (isFail){
      throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
    }
    
    return isFail;
	}

	@Override
	protected TypeToken<DCP_ReqCustomerUpdateReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_ReqCustomerUpdateReq>(){};
	}

	@Override
	protected DCP_ReqCustomerUpdateRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_ReqCustomerUpdateRes();
	}

}
