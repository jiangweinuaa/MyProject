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
import com.dsc.spos.json.cust.req.DCP_TaxCategoryCreateReq;
import com.dsc.spos.json.cust.req.DCP_TaxCategoryCreateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_TaxCategoryCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 税别信息新增
 * @author yuanyy 2019-03-12 
 *
 */
public class DCP_TaxCategoryCreate extends SPosAdvanceService<DCP_TaxCategoryCreateReq, DCP_TaxCategoryCreateRes> {

	@Override
	protected void processDUID(DCP_TaxCategoryCreateReq req, DCP_TaxCategoryCreateRes res) throws Exception {
		// TODO Auto-generated method stub
		String sql = null;
		try {
			String eId = req.geteId();
			String taxCode = req.getRequest().getTaxCode();
			//String taxName = req.getRequest().getTaxName();
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
			String departmentNo = req.getDepartmentNo();
			String employeeName = req.getEmployeeName();
			String departmentName = req.getDepartmentName();

			sql = this.isRepeat(eId, taxCode,taxArea);
			List<Map<String, Object>> taxDatas = this.doQueryData(sql, null);
			if(taxDatas.isEmpty()){
				String[] columns1 = { "TAXCODE", "TAXTYPE", "TAXRATE", "INCLTAX","STATUS", "EID","CREATETIME","TAXAREA","TAXPROP","CREATEOPID","CREATEDEPTID","TAXCALTYPE",
				"CREATEOPNAME","LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME","CREATEDEPTNAME"};
				DataValue[] insValue1 = null;
				insValue1 = new DataValue[] { 
						new DataValue(taxCode, Types.VARCHAR), 						
						new DataValue(taxType, Types.VARCHAR), 
						new DataValue(taxRate, Types.VARCHAR),
						new DataValue(inclTax, Types.VARCHAR), 
						new DataValue(status, Types.VARCHAR),
						new DataValue(eId, Types.VARCHAR),
						new DataValue(lastmoditime, Types.DATE),
                        new DataValue(taxArea, Types.VARCHAR),
                        new DataValue(taxProp, Types.VARCHAR),
						new DataValue(employeeNo, Types.VARCHAR),
						new DataValue(departmentNo, Types.VARCHAR),
                        new DataValue(taxCalType, Types.VARCHAR),

						new DataValue(employeeName, Types.VARCHAR),
						new DataValue(employeeNo, Types.VARCHAR),
						new DataValue(employeeName, Types.VARCHAR),
						new DataValue(lastmoditime, Types.DATE),
						new DataValue(departmentName, Types.VARCHAR),
                };
	
				InsBean ib1 = new InsBean("DCP_TAXCATEGORY", columns1);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1)); // 新增單頭
				
				
				List<level1Elm> getLangDatas = req.getRequest().getTaxName_lang();
				for(level1Elm oneLv1: getLangDatas){
									
					String[] columnsName = {
					  		"TAXCODE","LANG_TYPE","TAXNAME","EID","TAXAREA"
						};
					
					String langType = oneLv1.getLangType();
					String taxName = oneLv1.getName();
								
					sql = this.isRepeatLang(taxCode, eId, langType,taxArea);
					List<Map<String, Object>> detailDatas = this.doQueryData(sql, null);
					
					if(detailDatas.isEmpty()){
						
						DataValue[] insValueDetail = new DataValue[] 
								{
									new DataValue(taxCode, Types.VARCHAR),
									new DataValue(langType, Types.VARCHAR),
									new DataValue(taxName, Types.VARCHAR),									
									new DataValue(eId, Types.VARCHAR),
                                    new DataValue(taxArea, Types.VARCHAR)
                                };
						InsBean ib2 = new InsBean("DCP_TAXCATEGORY_LANG", columnsName);
						ib2.addValues(insValueDetail);
						this.addProcessData(new DataProcessBean(ib2));	
				
					}else{
						res.setSuccess(false);
						res.setServiceStatus("200");
						res.setServiceDescription("服务执行失败: 税别编码为  "+taxCode+" , 多语言类型为 "+langType+" 的信息已存在");	
						return;
					}
				}
			
				
				
				
				this.doExecuteDataToDB();
				
				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");
				
			}else{
				res.setSuccess(false);
				res.setServiceStatus("200");
				res.setServiceDescription("服务执行失败: 税别编码："+taxCode +"已存在 ");
				return;
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败");
		}
		
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_TaxCategoryCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_TaxCategoryCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_TaxCategoryCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_TaxCategoryCreateReq req) throws Exception {
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
        if(Check.Null(taxCalType)){
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
	protected TypeToken<DCP_TaxCategoryCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_TaxCategoryCreateReq>(){};
	}

	@Override
	protected DCP_TaxCategoryCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_TaxCategoryCreateRes();
	}
	
	/**
	 * 验证是否已存在
	 * @param eId
	 * @param taxCode
	 * @return
	 */
	private String isRepeat(String eId, String taxCode,String taxArea){
		String sql = "select taxCode from DCP_TaxCategory where EID = '"+eId+"' and taxCode = '"+taxCode+"' and taxArea='"+taxArea+"'";
		return sql;
	}
	
	/**
	 * 验证多语言信息是否重复 
	 * @param taxCode
	 * @param eId
	 * @param langType
	 * @return
	 */
	private String isRepeatLang(String taxCode, String eId, String langType,String taxArea ){
		String sql = null;
		sql = "SELECT * FROM DCP_TaxCategory_LANG WHERE "
				+ " TAXCODE = '"+taxCode+"' "
				+ " and EID = '"+eId+"' "
				+ " and lang_Type = '"+langType+"' and TAXAREA='"+taxArea+"'" ;
		return sql;
	}
	
}
