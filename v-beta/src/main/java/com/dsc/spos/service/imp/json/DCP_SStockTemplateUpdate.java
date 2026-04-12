package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_SStockTemplateUpdateReq;
import com.dsc.spos.json.cust.req.DCP_SStockTemplateUpdateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_SStockTemplateUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import java.text.SimpleDateFormat;

/**
 * 服務函數：StockTakeUpdate
 *    說明：库存盘点修改
 * 服务说明：库存盘点修改
 * @author panjing
 * @since  2016-09-20
 */
public class DCP_SStockTemplateUpdate extends SPosAdvanceService<DCP_SStockTemplateUpdateReq, DCP_SStockTemplateUpdateRes> {
	@Override
	protected boolean isVerifyFail(DCP_SStockTemplateUpdateReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();
		if(req.getRequest()==null) {
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		List<level1Elm> jsonDatas = req.getRequest().getDatas();
		String templateNO = req.getRequest().getTemplateNo();
		String templateName = req.getRequest().getTemplateName();
		String timeType = req.getRequest().getTimeType();
		String v_status = req.getRequest().getStatus();
		
		if (Check.Null(templateNO)) {
			errMsg.append("模板编号不可为空值, ");
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
			String status = par.getStatus();
			String price = par.getPrice();
			
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
			
			if (Check.Null(status)) {
				errMsg.append("明细状态不可为空值, ");
				isFail = true;
			}
			
			if (!PosPub.isNumericType(price)) {
				errMsg.append("单价必须是数字类型, ");
				isFail = true;
			}
			
			if (isFail){
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}
		}
		return isFail;
	}
	
	@Override
	protected TypeToken<DCP_SStockTemplateUpdateReq> getRequestType() {
		return new TypeToken<DCP_SStockTemplateUpdateReq>(){};
	}
	
	@Override
	protected DCP_SStockTemplateUpdateRes getResponseType() {
		return new DCP_SStockTemplateUpdateRes();
	}
	
	@Override
	protected void processDUID(DCP_SStockTemplateUpdateReq req,DCP_SStockTemplateUpdateRes res) throws Exception {
		String eId = req.geteId();
		String templateNO = req.getRequest().getTemplateNo();
		String templateName = req.getRequest().getTemplateName();
		String timeType = req.getRequest().getTimeType();
		String timeValue = req.getRequest().getTimeValue();
		String status = req.getRequest().getStatus();
		String supplier = req.getRequest().getSupplier();
		String modifyBy = req.getOpNO();
		String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
		String sTime = new SimpleDateFormat("HHmmss").format(new Date());
		String shopType = req.getRequest().getShopType();
		//向下兼容，前端可能没更新，默认值2
		if(shopType==null || shopType.isEmpty()) {
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
			String sql = " select *  from DCP_PTEMPLATE "
					+ " where EID= '"+eId+"' and DOC_TYPE='3' and PTEMPLATENO = '"+templateNO+"'  ";
			List<Map<String, Object>> getQData = this.doQueryData(sql, null);
			if (getQData != null && !getQData.isEmpty()) {
				//删除原来单身
				DelBean db1 = new DelBean("DCP_PTEMPLATE_DETAIL");
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db1.addCondition("PTEMPLATENO", new DataValue(templateNO, Types.VARCHAR));
				db1.addCondition("DOC_TYPE", new DataValue("3", Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));
				
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
					DataValue[] insValue = new DataValue[insColCt];
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
				
				
				//更新单头
				UptBean ub1 = new UptBean("DCP_PTEMPLATE");
				ub1.addUpdateValue("PTEMPLATE_NAME", new DataValue(templateName, Types.VARCHAR));
				ub1.addUpdateValue("TIME_TYPE", new DataValue(timeType, Types.VARCHAR));
				ub1.addUpdateValue("TIME_VALUE", new DataValue(timeValue, Types.VARCHAR));
				ub1.addUpdateValue("MODIFYBY", new DataValue(modifyBy, Types.VARCHAR));
				ub1.addUpdateValue("MODIFY_DATE", new DataValue(sDate, Types.VARCHAR));
				ub1.addUpdateValue("MODIFY_TIME", new DataValue(sTime, Types.VARCHAR));
				ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
				ub1.addUpdateValue("SUPPLIER", new DataValue(supplier, Types.VARCHAR));
				ub1.addUpdateValue("SHOPTYPE", new DataValue(shopType, Types.VARCHAR));
				
				// condition
				ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				ub1.addCondition("PTEMPLATENO", new DataValue(templateNO, Types.VARCHAR));
				ub1.addCondition("DOC_TYPE", new DataValue("3", Types.VARCHAR));
				
				this.addProcessData(new DataProcessBean(ub1));
				
				this.doExecuteDataToDB();
				
				
				//如果选择了 全部门店，删除之前设置的生效门店，不用放在一个事务
				if(shopType.equals("1")) {
					try {
						this.pData.clear();
						DelBean db_shop = new DelBean("DCP_PTEMPLATE_SHOP");
						db_shop.addCondition("EID", new DataValue(eId, Types.VARCHAR));
						db_shop.addCondition("PTEMPLATENO", new DataValue(templateNO, Types.VARCHAR));
						db_shop.addCondition("DOC_TYPE", new DataValue("3", Types.VARCHAR));
						this.addProcessData(new DataProcessBean(db_shop));
						this.doExecuteDataToDB();
						
					} catch (Exception ignored) {
					
					}
					
				}
				
				
				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");
				
			} else {
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "模板不存在，请重新输入！");
			}
			
		} catch (Exception e) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
		}
		
	}
	
	@Override
	protected List<InsBean> prepareInsertData(DCP_SStockTemplateUpdateReq req) throws Exception {
		return null;
	}
	
	@Override
	protected List<UptBean> prepareUpdateData(DCP_SStockTemplateUpdateReq req) throws Exception {
		return null;
	}
	
	@Override
	protected List<DelBean> prepareDeleteData(DCP_SStockTemplateUpdateReq req) throws Exception {
		return null;
	}
	
	@Override
	protected String getQuerySql(DCP_SStockTemplateUpdateReq req) throws Exception {
		return null;
	}
	
}
