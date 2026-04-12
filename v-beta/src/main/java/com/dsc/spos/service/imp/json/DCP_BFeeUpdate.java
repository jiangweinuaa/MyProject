package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
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
import com.dsc.spos.json.cust.req.DCP_BFeeUpdateReq;
import com.dsc.spos.json.cust.res.DCP_BFeeUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_BFeeUpdate extends SPosAdvanceService<DCP_BFeeUpdateReq,DCP_BFeeUpdateRes>
{

	@Override
	protected void processDUID(DCP_BFeeUpdateReq req, DCP_BFeeUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
		String shopId = req.getShopId();
		String organizationNO = req.getOrganizationNO();
		String eId = req.geteId();
		String modifyBy = req.getOpNO();
		Calendar cal = Calendar.getInstance();//获得当前时间
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String modifyDate = df.format(cal.getTime());
		df = new SimpleDateFormat("HHmmss");
		String modifyTime = df.format(cal.getTime());	 	
		String bFeeNO = req.getRequest().getbFeeNo();
		String bDate = req.getRequest().getbDate();
		String memo = req.getRequest().getMemo();
		String status = req.getRequest().getStatus();
		String docType = req.getRequest().getDocType();
		try
		{
			String sql = "select status from DCP_BFEE "
					+ "where eId='"+eId+"' and organizationno='"+organizationNO+"' and BFEENO='"+bFeeNO+"' and status='0' ";
			List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)	
			{
				//删除原有单身
				DelBean db1 = new DelBean("DCP_BFEE_DETAIL");
				db1.addCondition("BFEENO", new DataValue(bFeeNO, Types.VARCHAR));
				db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db1.addCondition("organizationNO", new DataValue(organizationNO, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));

				BigDecimal totAmt =  new BigDecimal("0");

				//新增新的单身（多条记录）
				List<Map<String, String>> datas = req.getRequest().getDatas();
				for (Map<String, String> par : datas) {
					int insColCt = 0;
					String[] columnsName = {
							"BFEENO", "SHOPID", "ITEM", "FEE", "AMT",
							"MEMO", "EID", "organizationNO"
					};

					DataValue[] columnsVal = new DataValue[columnsName.length];
					for (int i = 0; i < columnsVal.length; i++) { 
						String keyVal = null;
						switch (i) { 
						case 0:
							keyVal = bFeeNO;
							break;
						case 1:
							keyVal = shopId;
							break;
						case 2:
							keyVal = par.get("item"); //item
							break;
						case 3:  
							keyVal = par.get("fee"); //fee
							break;
						case 4:  
							keyVal = par.get("amt"); //amt 			            
							totAmt = totAmt.add(new BigDecimal(keyVal));
							break;
						case 5:  
							keyVal = par.get("memo"); //memo
							break;
						case 6:
							keyVal = eId;
							break;
						case 7:
							keyVal = organizationNO;
							break;
						default:
							break;
						}

						if (keyVal != null) {
							insColCt++;
							if (i == 2){
								columnsVal[i] = new DataValue(keyVal, Types.INTEGER);	
							}else if (i == 4){
								columnsVal[i] = new DataValue(keyVal, Types.FLOAT);
							}else{
								columnsVal[i] = new DataValue(keyVal, Types.VARCHAR);
							}
						}
						else {
							columnsVal[i] = null;
						}
					}

					String[] columns2  = new String[insColCt];
					DataValue[] insValue2 = new DataValue[insColCt];
					// 依照傳入參數組譯要insert的欄位與數值；
					insColCt = 0;

					for (int i=0;i<columnsVal.length;i++){
						if (columnsVal[i] != null){
							columns2[insColCt] = columnsName[i];
							insValue2[insColCt] = columnsVal[i];
							insColCt ++;
							if (insColCt >= insValue2.length) 
								break;
						}
					}
					InsBean ib2 = new InsBean("DCP_BFEE_DETAIL", columns2);
					ib2.addValues(insValue2);
					this.addProcessData(new DataProcessBean(ib2));
				}

				//更新单头
				UptBean ub1 = null;	
				ub1 = new UptBean("DCP_BFEE");
				//add Value
				ub1.addUpdateValue("bDate", new DataValue(bDate, Types.VARCHAR));
				ub1.addUpdateValue("MODIFY_CHATUSERID", new DataValue(req.getChatUserId(), Types.VARCHAR));
				ub1.addUpdateValue("MODIFYBY", new DataValue(modifyBy, Types.VARCHAR));
				ub1.addUpdateValue("MODIFY_DATE", new DataValue(modifyDate, Types.VARCHAR));
				ub1.addUpdateValue("MODIFY_TIME", new DataValue(modifyTime, Types.VARCHAR));
				ub1.addUpdateValue("TOT_AMT", new DataValue(totAmt.toString(), Types.VARCHAR));
				ub1.addUpdateValue("memo", new DataValue(memo, Types.VARCHAR));		
				ub1.addUpdateValue("status", new DataValue(status, Types.VARCHAR));		
				ub1.addUpdateValue("doc_Type", new DataValue(docType, Types.VARCHAR));		
				ub1.addUpdateValue("WORKNO", new DataValue(req.getRequest().getSquadNo(), Types.VARCHAR));
				ub1.addUpdateValue("TAXCODE", new DataValue(req.getRequest().getTaxCode(), Types.VARCHAR));
				ub1.addUpdateValue("TAXRATE", new DataValue(req.getRequest().getTaxRate(), Types.DOUBLE));
				ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

				//condition
				ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				ub1.addCondition("BFEENO", new DataValue(bFeeNO, Types.VARCHAR));
				ub1.addCondition("organizationNO", new DataValue(organizationNO, Types.VARCHAR));		
				this.addProcessData(new DataProcessBean(ub1));

				this.doExecuteDataToDB();

				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");
			}
			else
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据不存在或已确认！");
			}
		} 
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_BFeeUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_BFeeUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_BFeeUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_BFeeUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();
		//必传值不为空
		String bDate = req.getRequest().getbDate();
		String status = req.getRequest().getStatus();
		String docType = req.getRequest().getDocType();
		String bFeeNO = req.getRequest().getbFeeNo();
		List<Map<String, String>> datas = req.getRequest().getDatas();

		if(Check.Null(bDate)){
			errMsg.append("费用年月不可为空值, ");
			isFail = true;
		}

		if(Check.Null(status)){
			errMsg.append("状态不可为空值, ");
			isFail = true;
		}

		if(Check.Null(docType)){
			errMsg.append("单据类型不可为空值, ");
			isFail = true;
		}

		if(Check.Null(bFeeNO)){
			errMsg.append("费用单号不可为空值, ");
			isFail = true;
		}

		for(Map<String, String> par : datas){

			if (Check.Null(par.get("item"))) 
			{
				errMsg.append("项次不可为空值, ");
				isFail = true;
			}

			if (Check.Null(par.get("fee"))) 
			{
				errMsg.append("费用项不可为空值, ");
				isFail = true;
			}      

			if (Check.Null(par.get("amt"))) 
			{
				errMsg.append("金额不可为空值, ");
				isFail = true;
			}

			if (isFail)
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}
		}

		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}    
		return isFail;
	}

	@Override
	protected TypeToken<DCP_BFeeUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_BFeeUpdateReq>(){};
	}

	@Override
	protected DCP_BFeeUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_BFeeUpdateRes();
	}

}
