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
import com.dsc.spos.json.cust.req.DCP_ReqCustomerCreateReq;
import com.dsc.spos.json.cust.res.DCP_ReqCustomerCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_ReqCustomerCreate extends SPosAdvanceService<DCP_ReqCustomerCreateReq, DCP_ReqCustomerCreateRes> 
{
	@Override
	protected void processDUID(DCP_ReqCustomerCreateReq req, DCP_ReqCustomerCreateRes res) throws Exception {
	// TODO Auto-generated method stub
		//region 判断ID是否存在，存在直接返回成功
		if(checkGuid(req))
		{
			res.setSuccess(true);
		}
		//endregion
		//region 不存在时插入数据
		else
		{
			String[] columns1={"EID","CUSTOMER_ID","CUSTOMER_NAME","ABBR","SALER","ABBRNO","TAXNO","DISTRICT","REGISTER_AMT",
					"ANNUAL_AMT","OPEN_DATE","STAFFS_QTY","LEGAL_PERSON","ACCOUNT_PERSON","ACCOUNT_TELEPHONE","EMAIL","FAX",
					"MEMO","URL","OFFICE_ADDRESS","DELIVERY_ADDRESS","INVOICE_ADDRESS","TELEPHONE","ADDRESS","LINK_PERSON",
					"CREDIT_TYPE","CREDIT_AMT","PAY_CUSTOMER","COLLECT_OBJECT","COLLECT_SHOP","PAY_TYPE","PAY_DAY","PAY_MONTH",
					"CREDIT_MONTH","CREDIT_DAY","IS_CRETAIL","IS_CORDER","STATUS","CREATEBY","CREATE_DATE","CREATE_TIME",
					"MODIFYBY","MODIFY_DATE","MODIFY_TIME","SUBMITBY","SUBMIT_DATE","SUBMIT_TIME"};	
			String eId=req.geteId();;
			String opNO=req.getOpNO();
			Calendar cal = Calendar.getInstance();// 获得当前时间
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
			String createDate = df.format(cal.getTime());
			df = new SimpleDateFormat("HHmmss");
			String createTime = df.format(cal.getTime());
			List<Map<String, String>> datas=req.getDatas();
			//region 单身数据（子表数据）
			for (Map<String, String> par : datas)
			{
				String[] columns2 = {"EID","CUSTOMER_ID","SHOPID"};
				DataValue[] insValue2 = null;
				insValue2 = new DataValue[] 
						{
								new DataValue(eId, Types.VARCHAR),
								new DataValue(req.getCustomerID(), Types.VARCHAR),
								new DataValue(par.get("shopId"), Types.VARCHAR)							
						};
				InsBean ib2 = new InsBean("DCP_REQCUSTOMER_SHOP", columns2);
				ib2.addValues(insValue2);
				this.addProcessData(new DataProcessBean(ib2)); // 新增单身
      
			}
			//endregion
			//region 单头数据（主表）
			DataValue[] insValue1 = null;
			insValue1 = new DataValue[]
					{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(req.getCustomerID(), Types.VARCHAR),
						new DataValue(req.getCustomerName(), Types.VARCHAR),
						new DataValue(req.getAbbr(), Types.VARCHAR),
						new DataValue(req.getSaler(), Types.VARCHAR),
						new DataValue(req.getAbbrNO(), Types.VARCHAR),
						new DataValue(req.getTaxNO(), Types.VARCHAR),
						new DataValue(req.getDistrict(), Types.VARCHAR),
						new DataValue(Float.parseFloat(req.getRegisterAmt()), Types.FLOAT),
						new DataValue(Float.parseFloat(req.getAnnualAmt()), Types.FLOAT),
						new DataValue(req.getOpenDate(), Types.VARCHAR),
						new DataValue(Float.parseFloat(req.getStaffsQty()), Types.FLOAT),
						new DataValue(req.getLegalPerson(), Types.VARCHAR),
						new DataValue(req.getAccountPerson(), Types.VARCHAR),
						new DataValue(req.getAccountTelephone(), Types.VARCHAR),
						new DataValue(req.geteMail(), Types.VARCHAR),
						new DataValue(req.getFax(), Types.VARCHAR),
						new DataValue(req.getMemo(), Types.VARCHAR),
						new DataValue(req.getUrl(), Types.VARCHAR),
						new DataValue(req.getOfficeAddress(), Types.VARCHAR),
						new DataValue(req.getDeliveryAddress(), Types.VARCHAR),
						new DataValue(req.getInvoiceAddress(), Types.VARCHAR),
						new DataValue(req.getTelephone(), Types.VARCHAR),
						new DataValue(req.getAddress(), Types.VARCHAR),
						new DataValue(req.getLinkPerson(), Types.VARCHAR),
						new DataValue(req.getCreditType(), Types.VARCHAR),
						new DataValue(Float.parseFloat(req.getCreditAmt()), Types.FLOAT),
						new DataValue(req.getPayCustomer(), Types.VARCHAR),
						new DataValue(req.getCollectObject(), Types.VARCHAR),
						new DataValue(req.getCollectShop(), Types.VARCHAR),
						new DataValue(req.getPayType(), Types.VARCHAR),
						new DataValue(Float.parseFloat(req.getPayDay()), Types.FLOAT),
						new DataValue(Float.parseFloat(req.getPayMonth()), Types.FLOAT),
						new DataValue(Float.parseFloat(req.getCreditMonth()), Types.FLOAT),
						new DataValue(Float.parseFloat(req.getCreditDay()), Types.FLOAT),
						new DataValue(req.getIsCretail(), Types.VARCHAR),
						new DataValue(req.getIsCorder(), Types.VARCHAR),
						new DataValue(req.getStatus(), Types.VARCHAR),
						new DataValue(opNO, Types.VARCHAR),
						new DataValue(createDate, Types.VARCHAR),
						new DataValue(createTime, Types.VARCHAR),
						new DataValue("", Types.VARCHAR),
						new DataValue("", Types.VARCHAR),
						new DataValue("", Types.VARCHAR),
						new DataValue("", Types.VARCHAR),
						new DataValue("", Types.VARCHAR),
						new DataValue("", Types.VARCHAR)						
					};
			InsBean ib1 = new InsBean("DCP_REQCUSTOMER", columns1);
			ib1.addValues(insValue1);
			this.addProcessData(new DataProcessBean(ib1)); // 新增单头	
			//endregion
			
			this.doExecuteDataToDB();
			if (res.isSuccess()) 
			{
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");						
			} 
		}
	 //endregion
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_ReqCustomerCreateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_ReqCustomerCreateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_ReqCustomerCreateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_ReqCustomerCreateReq req) throws Exception {
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
	protected TypeToken<DCP_ReqCustomerCreateReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_ReqCustomerCreateReq>(){};
	}

	@Override
	protected DCP_ReqCustomerCreateRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_ReqCustomerCreateRes();
	}
	
	private boolean checkGuid(DCP_ReqCustomerCreateReq req) throws Exception {
    String sql = null;
    String guid = req.getCustomerID();
    String eId = req.geteId();;
    boolean existGuid; 
    sql = "select * from ta_reqcustomer where customer_id=? and EID=?";
    String[] conditionValues={guid,eId};
    List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);
    if (getQData != null && getQData.isEmpty() == false) {
      existGuid = true;
    } else {
      existGuid =  false;
    }
    return existGuid;
}

}
