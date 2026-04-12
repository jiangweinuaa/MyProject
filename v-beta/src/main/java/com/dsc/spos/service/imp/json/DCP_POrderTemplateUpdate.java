package com.dsc.spos.service.imp.json;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.Calendar;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_BizPartnerCreateReq;
import com.dsc.spos.json.cust.req.DCP_POrderTemplateUpdateReq;
import com.dsc.spos.json.cust.req.DCP_POrderTemplateUpdateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_POrderTemplateUpdateRes;
import com.dsc.spos.ninetyone.util.ColumnDataValue;
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
public class DCP_POrderTemplateUpdate extends SPosAdvanceService<DCP_POrderTemplateUpdateReq, DCP_POrderTemplateUpdateRes> 
{
	@Override
	protected boolean isVerifyFail(DCP_POrderTemplateUpdateReq req) throws Exception {

		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		List<level1Elm> jsonDatas = req.getRequest().getDatas();	
		String templateNO = req.getRequest().getTemplateNo();
		String templateName = req.getRequest().getTemplateName();
		String timeType = req.getRequest().getTimeType();
		String preday=req.getRequest().getPreday();
		String optionalTime =req.getRequest().getOptionalTime();
		String receiptOrgNO = req.getRequest().getReceiptOrgNo();

		//		
		String rdate_add=req.getRequest().getRdate_Add();		
		String rdate_times=req.getRequest().getRdate_Times();

		if (Check.Null(templateNO)) {
			errCt++;
			errMsg.append("模板编号不可为空值, ");
			isFail = true;
		} 

		if (Check.Null(templateName)) {
			errCt++;
			errMsg.append("模板名称不可为空值, ");
			isFail = true;
		} 

		if (Check.Null(timeType)) {
			errCt++;
			errMsg.append("周期类型不可为空值, ");
			isFail = true;
		} 

		if ( !PosPub.isNumeric(preday) ) {
			errCt++;
			errMsg.append("采购前置期不可为空值且必须为数值, ");
			isFail = true;
		} 

		if (!PosPub.isNumeric(optionalTime)) {
			errCt++;
			errMsg.append("订货截止时间不可为空值, ");
			isFail = true;
		} 

		if (Check.Null(receiptOrgNO)) {
			errCt++;
			errMsg.append("发货组织不可为空值, ");
			isFail = true;
		} 

		if (Check.Null(rdate_add)) 
		{
			rdate_add="0";
			req.getRequest().setRdate_Add("0");
		}
		else 
		{
			if (PosPub.isNumeric(rdate_add)==false) 
			{
				errCt++;
				errMsg.append("间隔周期必须为数字类型， ");
				isFail = true;
			} 
		}

		if (Check.Null(rdate_times)) 
		{
			rdate_times="0";
			req.getRequest().setRdate_Times("0");
		}
		else 
		{
			if (PosPub.isNumeric(rdate_times)==false) 
			{
				errCt++;
				errMsg.append("周期要货次数必须为数字类型， ");
				isFail = true;
			} 
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
			String minQty = par.getMinQty();
			String maxQty =par.getMaxQty();
			String mulQty =par.getMulQty();

			if (Check.Null(item)) {
				errCt++;
				errMsg.append("项次不可为空值, ");
				isFail = true;
			}

			if (Check.Null(pluNO)) {
				errCt++;
				errMsg.append("商品编码不可为空值, ");
				isFail = true;
			}

			if (Check.Null(punit)) {
				errCt++;
				errMsg.append("单位不可为空值, ");
				isFail = true;
			}

			if (Check.Null(status)) {
				errCt++;
				errMsg.append("状态不可为空值, ");
				isFail = true;
			}	

			if (!PosPub.isNumericType(minQty)) {
				errCt++;
				errMsg.append("最小要货量不可为空值且必须为数值, ");
				isFail = true;
			}	


			if (!PosPub.isNumericType(maxQty)) {
				errCt++;
				errMsg.append("最大要货量不可为空值且必须为数值, ");
				isFail = true;
			}	

			if (!PosPub.isNumericType(mulQty)) {
				errCt++;
				errMsg.append("要货倍量不可为空值且必须为数值, ");
				isFail = true;
			}

			if (isFail){
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}
		}
		return isFail;


	}

	@Override
	protected TypeToken<DCP_POrderTemplateUpdateReq> getRequestType() {
		return new TypeToken<DCP_POrderTemplateUpdateReq>(){};
	}

	@Override
	protected DCP_POrderTemplateUpdateRes getResponseType() {
		return new DCP_POrderTemplateUpdateRes();
	}

	@Override
	protected void processDUID(DCP_POrderTemplateUpdateReq req,DCP_POrderTemplateUpdateRes res) throws Exception {
		StringBuffer errMsg = new StringBuffer("");
		String templateNO = req.getRequest().getTemplateNo();
		String eId = req.geteId();
		String templateName = req.getRequest().getTemplateName();
		String timeType = req.getRequest().getTimeType();
		String timeValue = req.getRequest().getTimeValue();
		String preday =req.getRequest().getPreday();
		String optionalTime = req.getRequest().getOptionalTime();
		String receiptOrgNO =req.getRequest().getReceiptOrgNo();		
		String status = req.getRequest().getStatus();
		String modifyBy = req.getOpNO();
		String hqPorder= req.getRequest().getHqPorder();
		Calendar cal = Calendar.getInstance();//获得当前时间
		SimpleDateFormat df=new SimpleDateFormat("yyyyMMdd");
		String modifyDate = df.format(cal.getTime());
		df = new SimpleDateFormat("HHmmss");
		String modifyTime = df.format(cal.getTime());
		String shopType = req.getRequest().getShopType();
		String rdate_type=req.getRequest().getRdate_Type();		
		String rdate_add=req.getRequest().getRdate_Add();
		String rdate_values=req.getRequest().getRdate_Values();		
		String revoke_day=req.getRequest().getRevoke_Day();
		String revoke_time=req.getRequest().getRevoke_Time();
		String rdate_times=req.getRequest().getRdate_Times();
		String isAddGoods=req.getRequest().getIsAddGoods();
		if (Check.Null(isAddGoods)) isAddGoods="N";   //允许新增商品

		//向下兼容，前端可能没更新，默认值2
		if(shopType==null||shopType.isEmpty()){
			shopType = "2";
		}
		String isShowHeadStockQty = req.getRequest().getIsShowHeadStockQty();
		if (Check.Null(isShowHeadStockQty) || !isShowHeadStockQty.equals("Y"))
			isShowHeadStockQty="N";
		//解析日期值  BY JZMA 20200407
		if (timeType.equals("3")&&timeValue.length()>1)
		{
			if (!timeValue.substring(0, 1).equals(";"))
			{
				timeValue = ";" + timeValue;
			}
			if (!timeValue.substring(timeValue.length()-1, timeValue.length()).equals(";"))
			{
				timeValue = timeValue+";" ;
			}
			int i =0 ;
			while (timeValue.contains(";;")) {
				timeValue=timeValue.replaceAll(";;", ";");
				if (i>=100) break;
				i++;
			}
		}

		try 
		{
			String sql = null;
			sql = this.getQuerySql(req);
			String[] conditionValues = {eId,templateNO}; //查詢條件
			List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);
			if (getQData != null && getQData.isEmpty() == false) {
				//删除原来单身
				DelBean db1 = new DelBean("DCP_PTEMPLATE_DETAIL");
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db1.addCondition("PTEMPLATENO", new DataValue(templateNO, Types.VARCHAR));
				db1.addCondition("DOC_TYPE", new DataValue("0", Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));

				DataValue[] insValue = null;
				//新增單身 (多筆)
				List<level1Elm> jsonDatas = req.getRequest().getDatas();
				for (level1Elm par : jsonDatas) {
					int insColCt = 0;	
					String[] columnsName = {
							"EID", "PTEMPLATENO", "DOC_TYPE", "ITEM", "PLUNO",
							"PUNIT", "MIN_QTY", "MAX_QTY", "MUL_QTY",
							"STATUS","DEFAULT_QTY","PORDER_GROUP","SUPPLIER"
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
							keyVal = "0";
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
							keyVal = par.getMinQty();
							break;
						case 7:  
							keyVal = par.getMaxQty();
							break;
						case 8:  
							keyVal = par.getMulQty();
							break;
						case 9:  
							keyVal = par.getStatus();
							break;
						case 10:  
							keyVal = par.getDefQty();
							break;
						case 11:  
							keyVal = par.getGroupNo()==null?"":par.getGroupNo();
							break;
						case 12:  
							keyVal = par.getSupplier();
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


				 // 交易对象主数据档_组织范围
				ColumnDataValue columns = new ColumnDataValue();
				String[] columns1 = null;
	        	DataValue[] insValue1 =null;
	            List<DCP_POrderTemplateUpdateReq.OrgList> orgLists = req.getRequest().getOrgList();
	            DelBean db2 = new DelBean("DCP_PTEMPLATE_SHOP");
	            db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
	            db2.addCondition("PTEMPLATENO", new DataValue(templateNO, Types.VARCHAR));
	            db2.addCondition("DOC_TYPE", new DataValue("0", Types.VARCHAR));
	            this.addProcessData(new DataProcessBean(db2));
	            if (orgLists != null && orgLists.size() > 0) {
	                for (DCP_POrderTemplateUpdateReq.OrgList par : orgLists) {
	                	 columns.Columns.clear();
		                    columns.DataValues.clear();
		                    columns.Add("EID", eId, Types.VARCHAR);
		            		columns.Add("PTEMPLATENO", templateNO, Types.VARCHAR);
		            		columns.Add("SHOPID", par.getOrgNo(), Types.VARCHAR);
		            		columns.Add("DOC_TYPE", "0", Types.VARCHAR);
		            		columns.Add("TRAN_TIME", "", Types.VARCHAR);
		            		columns.Add("MATERIALWAREHOUSE", "", Types.VARCHAR);
		            		
		            		columns.Add("GOODSWAREHOUSE","", Types.VARCHAR);
		            		columns.Add("STATUS",Integer.valueOf(par.getStatus()),Types.INTEGER);           	              
		            		columns.Add("ISMUSTALLOT", par.getIsMustAllot(), Types.VARCHAR);
		            		columns.Add("SORTID", par.getSortId(), Types.VARCHAR);
		            		 columns1 = columns.Columns.toArray(new String[0]);
		            		 insValue1 = columns.DataValues.toArray(new DataValue[0]);
		                    // 交易对象主数据档_组织范围
		                    InsBean ib2 = new InsBean("DCP_PTEMPLATE_SHOP", columns1);
		                    ib2.addValues(insValue1);
		                    this.addProcessData(new DataProcessBean(ib2));
	                }
	            }
	            
	            List<DCP_POrderTemplateUpdateReq.EmpList> empLists = req.getRequest().getEmpList();
	            DelBean db3 = new DelBean("DCP_PTEMPLATE_EMPLOYEE");
	            db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
	            db3.addCondition("PTEMPLATENO", new DataValue(templateNO, Types.VARCHAR));
	            db3.addCondition("DOC_TYPE", new DataValue("0", Types.VARCHAR));
	            this.addProcessData(new DataProcessBean(db3));
	            if (orgLists != null && orgLists.size() > 0) {
	                for (DCP_POrderTemplateUpdateReq.EmpList par : empLists) {
	                	 columns.Columns.clear();
		                    columns.DataValues.clear();
		                    columns.Columns.clear();
		                    columns.DataValues.clear();
		                    columns.Add("EID", eId, Types.VARCHAR);
		            		columns.Add("PTEMPLATENO", templateNO, Types.VARCHAR);
		            		columns.Add("EMPLOYEEID", par.getEmployeeNo(), Types.VARCHAR);
		            		columns.Add("DOC_TYPE", "0", Types.VARCHAR);
		            		 
		            		columns.Add("STATUS",Integer.valueOf(par.getStatus()),Types.INTEGER); 
		            		 columns1 = columns.Columns.toArray(new String[0]);
		            		 insValue1 = columns.DataValues.toArray(new DataValue[0]);
		                    // DCP_PTEMPLATE_EMPLOYEE
		                    InsBean ib2 = new InsBean("DCP_PTEMPLATE_EMPLOYEE", columns1);
		                    ib2.addValues(insValue1);
		                    this.addProcessData(new DataProcessBean(ib2));
	                }
	            }
	            
				//更新单头
				UptBean ub1 = null;	
				ub1 = new UptBean("DCP_PTEMPLATE");
				ub1.addUpdateValue("PTEMPLATE_NAME", new DataValue(templateName, Types.VARCHAR));
				ub1.addUpdateValue("TIME_TYPE", new DataValue(timeType, Types.VARCHAR));
				ub1.addUpdateValue("TIME_VALUE", new DataValue(timeValue, Types.VARCHAR));
				ub1.addUpdateValue("PRE_DAY", new DataValue(preday, Types.INTEGER));
				ub1.addUpdateValue("OPTIONAL_TIME", new DataValue(optionalTime, Types.VARCHAR));
				ub1.addUpdateValue("RECEIPT_ORG", new DataValue(receiptOrgNO, Types.VARCHAR));
				ub1.addUpdateValue("MODIFYBY", new DataValue(modifyBy, Types.VARCHAR));
				ub1.addUpdateValue("MODIFY_DATE", new DataValue(modifyDate, Types.VARCHAR));
				ub1.addUpdateValue("MODIFY_TIME", new DataValue(modifyTime, Types.VARCHAR));
				ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
				ub1.addUpdateValue("HQPORDER", new DataValue(hqPorder, Types.VARCHAR));
				ub1.addUpdateValue("SHOPTYPE", new DataValue(shopType, Types.VARCHAR));
				ub1.addUpdateValue("RDATE_TYPE", new DataValue(rdate_type, Types.VARCHAR));
				ub1.addUpdateValue("RDATE_ADD", new DataValue(rdate_add, Types.INTEGER));
				ub1.addUpdateValue("RDATE_VALUES", new DataValue(rdate_values, Types.VARCHAR));
				ub1.addUpdateValue("REVOKE_DAY", new DataValue(revoke_day, Types.VARCHAR));
				ub1.addUpdateValue("REVOKE_TIME", new DataValue(revoke_time, Types.VARCHAR));
				ub1.addUpdateValue("RDATE_TIMES", new DataValue(rdate_times, Types.INTEGER));
				ub1.addUpdateValue("ISADDGOODS", new DataValue(isAddGoods, Types.VARCHAR));
				ub1.addUpdateValue("ISSHOWHEADSTOCKQTY", new DataValue(isShowHeadStockQty, Types.VARCHAR));
				
				ub1.addUpdateValue("SUPPLIERTYPE", new DataValue(req.getRequest().getSupplierType(), Types.VARCHAR));
				ub1.addUpdateValue("ALLOTTYPE", new DataValue(req.getRequest().getAllotType(), Types.VARCHAR));
				ub1.addUpdateValue("FLOATSCALE", new DataValue(req.getRequest().getFloatScale(), Types.VARCHAR));
				ub1.addUpdateValue("SUPPLIER", new DataValue(req.getRequest().getSupplier(), Types.VARCHAR));
				// condition
				ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				ub1.addCondition("PTEMPLATENO", new DataValue(templateNO, Types.VARCHAR));
				ub1.addCondition("DOC_TYPE", new DataValue("0", Types.VARCHAR));

				this.addProcessData(new DataProcessBean(ub1));

				this.doExecuteDataToDB();

				//如果选择了 全部门店，删除之前设置的生效门店，不用放在一个事务
				if(shopType.equals("1"))
				{
					try
					{

						this.pData.clear();
						DelBean db_shop = new DelBean("DCP_PTEMPLATE_SHOP");
						db_shop.addCondition("EID", new DataValue(eId, Types.VARCHAR));
						db_shop.addCondition("PTEMPLATENO", new DataValue(templateNO, Types.VARCHAR));
						db_shop.addCondition("DOC_TYPE", new DataValue("0", Types.VARCHAR));
						this.addProcessData(new DataProcessBean(db_shop));
						this.doExecuteDataToDB();

					} 
					catch (Exception e) 
					{

					}

				}


				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");
			}
			else
			{
				errMsg.append("模板不存在，请重新输入！");
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}	
		}
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}



	}	

	@Override
	protected List<InsBean> prepareInsertData(DCP_POrderTemplateUpdateReq req) throws Exception {
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_POrderTemplateUpdateReq req) throws Exception {
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_POrderTemplateUpdateReq req) throws Exception {
		return null;
	}

	@Override
	protected String getQuerySql(DCP_POrderTemplateUpdateReq req) throws Exception {
		String sql = null;
		sql= " select *  from DCP_PTEMPLATE  where EID= ? and DOC_TYPE='0' and PTEMPLATENO = ?  ";
		return sql;
	}

}
