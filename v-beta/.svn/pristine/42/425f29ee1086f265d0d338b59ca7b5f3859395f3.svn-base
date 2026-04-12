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
import com.dsc.spos.json.cust.req.DCP_TaxCategoryUpdateReq;
import com.dsc.spos.json.cust.req.DCP_TaxCategoryUpdateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_TaxCategoryUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_TaxCategoryUpdate extends SPosAdvanceService<DCP_TaxCategoryUpdateReq, DCP_TaxCategoryUpdateRes> {

	@Override
	protected void processDUID(DCP_TaxCategoryUpdateReq req, DCP_TaxCategoryUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
		String sql = null;
		try {
			String eId = req.geteId();
			String taxCode = req.getRequest().getTaxCode();	
			String taxType = req.getRequest().getTaxType();
			String taxRate = req.getRequest().getTaxRate();
			String inclTax = req.getRequest().getInclTax();
			String status = req.getRequest().getStatus();
            String taxArea = req.getRequest().getTaxArea();
            String taxProp = req.getRequest().getTaxProp();
            String taxCalType = req.getRequest().getTaxCalType();
            String lastmoditime = null;//req.getRequest().getLastmoditime();
			if(lastmoditime==null||lastmoditime.isEmpty())
			{
				lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			}
			String employeeNo = req.getEmployeeNo();
			String employeeName = req.getEmployeeName();

			UptBean ub1 = null;	
			ub1 = new UptBean("DCP_TAXCATEGORY");
			
			ub1.addUpdateValue("TAXTYPE", new DataValue(taxType, Types.VARCHAR));
			ub1.addUpdateValue("TAXRATE", new DataValue(taxRate, Types.VARCHAR));	
			ub1.addUpdateValue("INCLTAX", new DataValue(inclTax, Types.VARCHAR));
			ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
            ub1.addUpdateValue("TAXPROP", new DataValue(taxProp, Types.VARCHAR));
			ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
			ub1.addUpdateValue("LASTMODIOPID", new DataValue(employeeNo, Types.VARCHAR));
			ub1.addUpdateValue("LASTMODIOPNAME", new DataValue(employeeName, Types.VARCHAR));
            ub1.addUpdateValue("TAXCALTYPE", new DataValue(taxCalType, Types.VARCHAR));
			// condition
			ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			ub1.addCondition("TAXCODE", new DataValue(taxCode, Types.VARCHAR));
            ub1.addCondition("TAXAREA", new DataValue(taxArea, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(ub1));
			
			
			DelBean db2 = new DelBean("DCP_TAXCATEGORY_LANG");
			db2.addCondition("TAXCODE", new DataValue(taxCode, Types.VARCHAR));
			db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db2.addCondition("TAXAREA", new DataValue(taxArea, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db2));
			
			List<level1Elm> getLangDatas = req.getRequest().getTaxName_lang();
			for(level1Elm oneLv1: getLangDatas){
								
				String[] columnsName = {
				  		"TAXCODE","LANG_TYPE","TAXNAME","EID","LASTMODITIME","TAXAREA"
					};
				
				String langType = oneLv1.getLangType();
				String taxName = oneLv1.getName();				
				DataValue[] insValueDetail = new DataValue[] 
						{
							new DataValue(taxCode, Types.VARCHAR),
							new DataValue(langType, Types.VARCHAR),
							new DataValue(taxName, Types.VARCHAR),									
							new DataValue(eId, Types.VARCHAR),
							new DataValue(lastmoditime, Types.DATE),
							new DataValue(taxArea, Types.VARCHAR)
						};
				InsBean ib2 = new InsBean("DCP_TAXCATEGORY_LANG", columnsName);
				ib2.addValues(insValueDetail);
				this.addProcessData(new DataProcessBean(ib2));							
			}
			

			this.doExecuteDataToDB();
			
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败:"+e.getMessage());
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_TaxCategoryUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_TaxCategoryUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_TaxCategoryUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_TaxCategoryUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		if(req.getRequest()==null)
    {
    	errMsg.append("requset不能为空值 ");
    	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
    }
		String taxCode = req.getRequest().getTaxCode();
		String taxType = req.getRequest().getTaxType();
        String taxArea=req.getRequest().getTaxArea();
        String taxCalType = req.getRequest().getTaxCalType();

        if (Check.Null(taxCode))
		{
			errMsg.append("税别编码不可为空值, ");
			isFail = true;
		} 
		if (Check.Null(taxType)) 
		{
			errMsg.append("税别类型不可为空值, ");
			isFail = true;
		}
        if(Check.Null(taxArea)){
            errMsg.append("税区不可为空值, ");
            isFail = true;
        }
        if (Check.Null(taxCalType))
        {
            errMsg.append("计税方式不可为空值, ");
            isFail = true;
        }

        List<level1Elm> datas = req.getRequest().getTaxName_lang();
		
		if(datas==null||datas.isEmpty())
		{
			errMsg.append("多语言资料不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
	 	for (level1Elm oneData : datas) 
		{
    		String langType = oneData.getLangType();
    		
    		if(Check.Null(langType)){
    		   	errMsg.append("多语言类型不能为空值 ");
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
	protected TypeToken<DCP_TaxCategoryUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_TaxCategoryUpdateReq>(){};
	}

	@Override
	protected DCP_TaxCategoryUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_TaxCategoryUpdateRes();
	}

}
