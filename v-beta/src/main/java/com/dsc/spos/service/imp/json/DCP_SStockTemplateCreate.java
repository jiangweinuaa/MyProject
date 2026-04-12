package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_SStockTemplateCreateReq;
import com.dsc.spos.json.cust.req.DCP_SStockTemplateCreateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_SStockTemplateCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DCP_SStockTemplateCreate extends SPosAdvanceService<DCP_SStockTemplateCreateReq, DCP_SStockTemplateCreateRes> {
	
	@Override
	protected boolean isVerifyFail(DCP_SStockTemplateCreateReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();
		if(req.getRequest()==null) {
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		List<level1Elm> jsonDatas = req.getRequest().getDatas();
		String templateID = req.getRequest().getTemplateID();
		String templateName = req.getRequest().getTemplateName();
		String timeType = req.getRequest().getTimeType();
		String v_status = req.getRequest().getStatus();
		
		if (Check.Null(templateID)) {
			errMsg.append("模板ID不可为空值, ");
			isFail = true;
		}
		
		if (Check.Null(templateName)) {
			errMsg.append("模板名称不可为空值, ");
			isFail = true;
		}
		
		if (Check.Null(timeType)) {
			errMsg.append("周期类型不可为空值, ");
			isFail = true;
		}
		
		if (Check.Null(v_status)) {
			errMsg.append("模板状态不可为空值, ");
			isFail = true;
		}
		
		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		for (level1Elm par : jsonDatas) {
			//必传值不为空
			String item = par.getItem();
			String pluNO = par.getPluNo();
			String punit = par.getPunit();
			String price = par.getPrice();
			String status = par.getStatus();
			
			if (Check.Null(item)) {
				errMsg.append("项次不可为空值, ");
				isFail = true;
			}
			
			if (Check.Null(pluNO)) {
				errMsg.append("商品编码不可为空值, ");
				isFail = true;
			}
			
			if (Check.Null(punit)) {
				errMsg.append("单位不可为空值, ");
				isFail = true;
			}
			
			if (!PosPub.isNumericType(price)) {
				errMsg.append("单价必须是数字类型, ");
				isFail = true;
			}
			
			if (Check.Null(status)) {
				errMsg.append("明细状态不可为空值, ");
				isFail = true;
			}
			
			if (isFail){
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}
		}
		return false;
	}
	
	@Override
	protected TypeToken<DCP_SStockTemplateCreateReq> getRequestType() {
		return new TypeToken<DCP_SStockTemplateCreateReq>(){};
	}
	
	@Override
	protected DCP_SStockTemplateCreateRes getResponseType() {
		return new DCP_SStockTemplateCreateRes();
	}
	
	@Override
	protected void processDUID(DCP_SStockTemplateCreateReq req,DCP_SStockTemplateCreateRes res) throws Exception {
		String eId = req.geteId();
		String templateName = req.getRequest().getTemplateName();
		String timeType = req.getRequest().getTimeType();
		String timeValue = req.getRequest().getTimeValue();
		String status = req.getRequest().getStatus();
		String createBy = req.getOpNO();
		String templateID = req.getRequest().getTemplateID();
		String supplier = req.getRequest().getSupplier();
		String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
		String sTime = new SimpleDateFormat("HHmmss").format(new Date());
		String templateNO ="";
		String shopType = req.getRequest().getShopType();
		//向下兼容，前端可能没更新，默认值2
		if(shopType==null||shopType.isEmpty()) {
			shopType = "2";
		}
		
		//解析日期值使用 BY JZMA 20200407
		if (timeType.equals("3")&&timeValue.length()>1) {
			if (!timeValue.substring(0, 1).equals(";")) {
				timeValue = ";" + timeValue;
			}
			if (!timeValue.substring(timeValue.length()-1, timeValue.length()).equals(";")) {
				timeValue = timeValue+";" ;
			}
			int i =0 ;
			while (timeValue.contains(";;")) {
				timeValue=timeValue.replaceAll(";;", ";");
				if (i>=100) break;
				i++;
			}
		}
		
		try {
			if (checkGuid(req) == false){
				templateNO = gettemplateNO(req);
				String[] columns1 = {
						"EID", "PTEMPLATENO","DOC_TYPE","PTEMPLATE_NAME","PTEMPLATE_ID", "TIME_TYPE",
						"TIME_VALUE","TOT_CQTY", "PRE_DAY", "CREATEBY", "CREATE_DATE",
						"CREATE_TIME", "STATUS","SUPPLIER","SHOPTYPE"
				};
				DataValue[] insValue = null;
				
				//新增單身 (多筆)
				List<level1Elm> jsonDatas = req.getRequest().getDatas();
				for (level1Elm par : jsonDatas) {
					int insColCt = 0;
					String[] columnsName = {
							"EID", "PTEMPLATENO", "DOC_TYPE", "ITEM", "PLUNO",
							"PUNIT", "MIN_QTY", "MAX_QTY", "MUL_QTY",
							"STATUS","PRICE"
					};
					
					DataValue[] columnsVal = new DataValue[columnsName.length];
					for (int i = 0; i < columnsVal.length; i++) {
						String keyVal = null;
						switch (i) {
							case 0:
								keyVal = eId;
								break;
							case 1:
								keyVal = templateNO;
								break;
							case 2:
								keyVal = "3";
								break;
							case 3:
								keyVal = par.getItem();
								break;
							case 4:
								keyVal = par.getPluNo(); //pluNO
								break;
							case 5:
								keyVal = par.getPunit();
								break;
							case 6:
								keyVal = "0";
								break;
							case 7:
								keyVal = "0";
								break;
							case 8:
								keyVal = "0";
								break;
							case 9:
								keyVal = par.getStatus();
								break;
							case 10:
								keyVal = par.getPrice();
								break;
							default:
								break;
						}
						
						if (keyVal != null) {
							insColCt++;
							if (i == 3 ){
								columnsVal[i] = new DataValue(keyVal, Types.INTEGER);
							}else if (i == 6 || i == 7 || i == 8 ){
								columnsVal[i] = new DataValue(keyVal, Types.FLOAT);
							}else{
								columnsVal[i] = new DataValue(keyVal, Types.VARCHAR);
							}
						} else {
							columnsVal[i] = null;
						}
					}
					
					String[] columns  = new String[insColCt];
					insValue = new DataValue[insColCt];
					// 依照傳入參數組譯要insert的欄位與數值；
					insColCt = 0;
					for (int i = 0; i < columnsVal.length; i++){
						if (columnsVal[i] != null){
							columns[insColCt] = columnsName[i];
							insValue[insColCt] = columnsVal[i];
							insColCt ++;
							if (insColCt >= insValue.length) break;
						}
					}
					InsBean ib = new InsBean("DCP_PTEMPLATE_DETAIL", columns);
					ib.addValues(insValue);
					this.addProcessData(new DataProcessBean(ib));
				}
				
				insValue = new DataValue[]{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(templateNO, Types.VARCHAR),
						new DataValue("3", Types.VARCHAR),
						new DataValue(templateName, Types.VARCHAR),
						new DataValue(templateID, Types.VARCHAR),
						new DataValue(timeType, Types.VARCHAR),
						new DataValue(timeValue, Types.VARCHAR),
						new DataValue("0", Types.FLOAT),
						new DataValue("0", Types.INTEGER),
						new DataValue(createBy, Types.VARCHAR),
						new DataValue(sDate, Types.VARCHAR),
						new DataValue(sTime, Types.VARCHAR),
						new DataValue(status, Types.VARCHAR),
						new DataValue(supplier, Types.VARCHAR),
						new DataValue(shopType, Types.VARCHAR)
					
				};
				
				InsBean ib1 = new InsBean("DCP_PTEMPLATE", columns1);
				ib1.addValues(insValue);
				this.addProcessData(new DataProcessBean(ib1)); // 新增單頭
			}
			
			this.doExecuteDataToDB();
			
			res.setTemplateNo(templateNO);
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
			
		} catch (Exception e) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
		}
		
	}
	
	@Override
	protected List<InsBean> prepareInsertData(DCP_SStockTemplateCreateReq req) throws Exception {
		return null;
	}
	
	@Override
	protected List<UptBean> prepareUpdateData(DCP_SStockTemplateCreateReq req) throws Exception {
		return null;
	}
	
	@Override
	protected List<DelBean> prepareDeleteData(DCP_SStockTemplateCreateReq req) throws Exception {
		return null;
	}
	
	@Override
	protected String getQuerySql(DCP_SStockTemplateCreateReq req) throws Exception {
		return null;
	}
	
	private String gettemplateNO(DCP_SStockTemplateCreateReq req) throws Exception {
		/*
		 * 单据编号在后台按规格生成(固定编码+年月日+5位流水号(比如TBCK201607010001)，流水号取门店该单据最大流水号+1)
		 * 注意固定编码：如果是调拨出库则为DBCK 如果是退货出库则为THCK 如果是次品出库则为CPCK
		 */
		
		String eId = req.geteId();
		String templateNO = "CGMB" + new SimpleDateFormat("yyyyMMdd").format(new Date());
		String sql = "select max(PTEMPLATENO) as PTEMPLATENO from DCP_PTEMPLATE "
				+ " where DOC_TYPE='3' and EID = '"+eId+"' "
				+ " and PTEMPLATENO like '%%" + templateNO + "%%' ";
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		if (getQData != null && !getQData.isEmpty()) {
			templateNO = (String) getQData.get(0).get("PTEMPLATENO");
			if (templateNO != null && templateNO.length() > 0) {
				long i;
				templateNO = templateNO.substring(4);
				i = Long.parseLong(templateNO) + 1;
				templateNO = i + "";
				templateNO = "CGMB" + templateNO;
			} else {
				templateNO = "CGMB" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "00001";
			}
		} else {
			templateNO = "CGMB" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "00001";
		}
		
		return templateNO;
	}
	
	private boolean checkGuid(DCP_SStockTemplateCreateReq req) throws Exception {
		String sql = null;
		String guid = req.getRequest().getTemplateID();
		boolean existGuid;
		sql = "select *  from DCP_PTEMPLATE  where PTEMPLATE_ID = '"+guid+"' ";
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		if (getQData != null && getQData.isEmpty() == false) {
			existGuid = true;
		} else {
			existGuid =  false;
		}
		return existGuid;
	}
	
}
