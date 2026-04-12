package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.Calendar;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_POrderTemplateCreateReq;
import com.dsc.spos.json.cust.req.DCP_POrderTemplateCreateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_POrderTemplateCreateRes;
import com.dsc.spos.ninetyone.util.ColumnDataValue;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 服務函數：STakeTemplateCreateDCP
 *    說明：盘点模板新增
 * 服务说明：盘点模板新增
 * @author panjing 
 * @since  2016-09-20
 */
public class DCP_POrderTemplateCreate extends SPosAdvanceService<DCP_POrderTemplateCreateReq, DCP_POrderTemplateCreateRes> 
{

	@Override
	protected boolean isVerifyFail(DCP_POrderTemplateCreateReq req) throws Exception 
	{
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
		String templateID = req.getRequest().getTemplateID();
		String templateName = req.getRequest().getTemplateName();
		String timeType = req.getRequest().getTimeType();
		String preday=req.getRequest().getPreday();
		String optionalTime =req.getRequest().getOptionalTime();
		String receiptOrgNO = req.getRequest().getReceiptOrgNo();

		//			
		String rdate_add=req.getRequest().getRdate_Add();		
		String rdate_times=req.getRequest().getRdate_Times();
		String v_status = req.getRequest().getStatus();

		if ("2".equals(req.getRequest().getShopType()))
		if (req.getRequest().getOrgList() == null) {
            errMsg.append("传入参数shopType 指定组织 时，传入orgList[]不可为空！ ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
		
		if (Check.Null(templateID)) {
			errCt++;
			errMsg.append("模板ID不可为空值, ");
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

		if (Check.Null(v_status)) {
			errCt++;
			errMsg.append("模板状态不可为空值, ");
			isFail = true;
		} 


		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}		

		for (level1Elm par : jsonDatas) 
		{
			//必传值不为空		
			String item = par.getItem();
			String pluNO = par.getPluNo();
			String punit = par.getPunit();
			String status = par.getStatus();
			String minQty = par.getMinQty();
			String maxQty =par.getMaxQty();
			String mulQty =par.getMulQty();
			String defQty = par.getDefQty();

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

			if (Check.Null(status)) 
			{
				errCt++;
				errMsg.append("模板明细状态不可为空值, ");
				isFail = true;
			}	

			if (!PosPub.isNumericType(minQty)) {
				errCt++;
				errMsg.append("最小要货量不可为空值且必须为数值, ");
				isFail = true;
			}	

			if (!PosPub.isNumericType(defQty)) {
				errCt++;
				errMsg.append("默认要货量不可为空值且必须为数值, ");
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
	protected TypeToken<DCP_POrderTemplateCreateReq> getRequestType() {
		return new TypeToken<DCP_POrderTemplateCreateReq>(){};
	}

	@Override
	protected DCP_POrderTemplateCreateRes getResponseType() {
		return new DCP_POrderTemplateCreateRes();
	}

	@Override
	protected void processDUID(DCP_POrderTemplateCreateReq req,DCP_POrderTemplateCreateRes res) throws Exception {		
		String eId = req.geteId();
		String templateName = req.getRequest().getTemplateName();
		String timeType = req.getRequest().getTimeType();
		String timeValue = req.getRequest().getTimeValue();
		String preday =req.getRequest().getPreday();
		String optionalTime = req.getRequest().getOptionalTime();
		String receiptOrgNO =req.getRequest().getReceiptOrgNo();
		String hqPorder = req.getRequest().getHqPorder();
		String status = req.getRequest().getStatus();
		String createBy = req.getOpNO();
		String templateID = req.getRequest().getTemplateID();
		Calendar cal = Calendar.getInstance();//获得当前时间
		SimpleDateFormat df=new SimpleDateFormat("yyyyMMdd");
		String createDate = df.format(cal.getTime());
		df = new SimpleDateFormat("HHmmss");
		String createTime = df.format(cal.getTime());
		String templateNO ="";
		String shopType = req.getRequest().getShopType();
		String rdate_type=req.getRequest().getRdate_Type();		
		String rdate_add=req.getRequest().getRdate_Add();
		String rdate_values=req.getRequest().getRdate_Values();		
		String revoke_day=req.getRequest().getRevoke_Day();
		String revoke_time=req.getRequest().getRevoke_Time();
		String rdate_times=req.getRequest().getRdate_Times();
		String isAddGoods = req.getRequest().getIsAddGoods();
		if (Check.Null(isAddGoods)) isAddGoods="N";   //允许新增商品

		//向下兼容，前端可能没更新，默认值2
		if(shopType==null||shopType.isEmpty()){
			shopType = "2";
		}
		String isShowHeadStockQty = req.getRequest().getIsShowHeadStockQty();
		if (Check.Null(isShowHeadStockQty) || !isShowHeadStockQty.equals("Y"))
			isShowHeadStockQty="N";

		//解析日期值 BY JZMA 20200407
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
			if (checkGuid(req) == false)
			{
				templateNO = gettemplateNO(req);
				String[] columns1 =  null;
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
				
				ColumnDataValue columns = new ColumnDataValue();
	        	
	        	DataValue[] insValue1 =null;
				// 交易对象主数据档_组织范围
	            List<DCP_POrderTemplateCreateReq.OrgList> orgLists = req.getRequest().getOrgList();

	            if (orgLists != null && orgLists.size() > 0) {
	                for (DCP_POrderTemplateCreateReq.OrgList par : orgLists) {
	                	 
	                    columns.Columns.clear();
	                    columns.DataValues.clear();
	                    columns.Add("EID", eId, Types.VARCHAR);
	            		columns.Add("PTEMPLATENO", templateNO, Types.VARCHAR);
	            		columns.Add("SHOPID", par.getOrgNo(), Types.VARCHAR);
	            		columns.Add("DOC_TYPE", "0", Types.VARCHAR);
	            		columns.Add("TRAN_TIME", timeValue, Types.VARCHAR);
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
	            
	         // employee
	            List<DCP_POrderTemplateCreateReq.EmpList> empLists = req.getRequest().getEmpList();

	            if (empLists != null && empLists.size() > 0) {
	                for (DCP_POrderTemplateCreateReq.EmpList par : empLists) {
	                	 
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
	            };
				
	            String[] columns2 =   {
						"EID", "PTEMPLATENO","DOC_TYPE","PTEMPLATE_NAME","PTEMPLATE_ID", "TIME_TYPE", 
						"TIME_VALUE","TOT_CQTY", "PRE_DAY","OPTIONAL_TIME","RECEIPT_ORG", "CREATEBY", "CREATE_DATE",
						"CREATE_TIME", "STATUS","HQPORDER","SHOPTYPE",
						"RDATE_TYPE","RDATE_ADD","RDATE_VALUES","REVOKE_DAY","REVOKE_TIME","RDATE_TIMES",
						"ISADDGOODS","ISSHOWHEADSTOCKQTY"
						,"CREATEDEPTID","SUPPLIERTYPE","ALLOTTYPE","FLOATSCALE"
				};
				insValue = new DataValue[]{
						new DataValue(eId, Types.VARCHAR), 
						new DataValue(templateNO, Types.VARCHAR), 
						new DataValue("0", Types.VARCHAR),
						new DataValue(templateName, Types.VARCHAR), 
						new DataValue(templateID, Types.VARCHAR), 
						new DataValue(timeType, Types.VARCHAR), 
						new DataValue(timeValue, Types.VARCHAR),
						new DataValue("0", Types.FLOAT),
						new DataValue(preday, Types.INTEGER), 
						new DataValue(optionalTime, Types.VARCHAR), 
						new DataValue(receiptOrgNO, Types.VARCHAR), 						
						new DataValue(createBy, Types.VARCHAR),
						new DataValue(createDate, Types.VARCHAR), 
						new DataValue(createTime, Types.VARCHAR),
						new DataValue(status, Types.VARCHAR),
						new DataValue(hqPorder, Types.VARCHAR),
						new DataValue(shopType, Types.VARCHAR),
						new DataValue(rdate_type, Types.VARCHAR),
						new DataValue(rdate_add, Types.INTEGER),
						new DataValue(rdate_values, Types.VARCHAR),
						new DataValue(revoke_day, Types.VARCHAR),
						new DataValue(revoke_time, Types.VARCHAR),
						new DataValue(rdate_times, Types.INTEGER),
						new DataValue(isAddGoods, Types.VARCHAR),
						new DataValue(isShowHeadStockQty, Types.VARCHAR),
						
						new DataValue(req.getEmployeeNo(), Types.VARCHAR),
						new DataValue(req.getRequest().getSupplierType(), Types.VARCHAR),
						new DataValue(req.getRequest().getAllotType(), Types.VARCHAR),
						new DataValue(req.getRequest().getFloatScale(), Types.VARCHAR),
						 	
				};

				InsBean ib1 = new InsBean("DCP_PTEMPLATE", columns2);
				ib1.addValues(insValue);
				this.addProcessData(new DataProcessBean(ib1)); // 新增單頭		
				this.doExecuteDataToDB();	
				
				res.setTemplateNo(templateNO);
				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");
			}
			else 
			{
				res.setTemplateNo("");
				res.setSuccess(false);
				res.setServiceStatus("100");
				res.setServiceDescription("请求GUID重复！");
			}
		}
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}

	}	

	@Override
	protected List<InsBean> prepareInsertData(DCP_POrderTemplateCreateReq req) throws Exception {
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_POrderTemplateCreateReq req) throws Exception {
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_POrderTemplateCreateReq req) throws Exception {
		return null;
	}	

	@Override
	protected String getQuerySql(DCP_POrderTemplateCreateReq req) throws Exception {
		String sql = null;
		return sql;
	}

	private String gettemplateNO(DCP_POrderTemplateCreateReq req) throws Exception 
	{

		/*
		 * 单据编号在后台按规格生成(固定编码+年月日+5位流水号(比如TBCK201607010001)，流水号取门店该单据最大流水号+1)
		 * 注意固定编码：如果是调拨出库则为DBCK 如果是退货出库则为THCK 如果是次品出库则为CPCK
		 */
		String sql = null;
		String templateNO = null;
		String eId = req.geteId();
		StringBuffer sqlbuf = new StringBuffer("");
		String[] conditionValues = {eId}; 

		Date dt = new Date();
		SimpleDateFormat matter = new SimpleDateFormat("yyyyMMdd");
		templateNO = "YHMB" + matter.format(dt);

		sqlbuf.append("" + "select PTEMPLATENO  from ( " + "select max(PTEMPLATENO) as  PTEMPLATENO "
				+ "  from DCP_PTEMPLATE " + " where DOC_TYPE='0' and  EID = ? "
				+ " and PTEMPLATENO like '%%" + templateNO + "%%' "); // 假資料
		sqlbuf.append(" ) TBL ");
		sql = sqlbuf.toString();

		List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);
		if (getQData != null && getQData.isEmpty() == false) {

			templateNO = (String) getQData.get(0).get("PTEMPLATENO");

			if (templateNO != null && templateNO.length() > 0) {
				long i;
				templateNO = templateNO.substring(4, templateNO.length());
				i = Long.parseLong(templateNO) + 1;
				templateNO = i + "";
				templateNO = "YHMB" + templateNO;
			} else {
				templateNO = "YHMB" + matter.format(dt) + "00001";
			}
		} else {
			templateNO = "YHMB" + matter.format(dt) + "00001";
		}

		return templateNO;
	}

	private boolean checkGuid(DCP_POrderTemplateCreateReq req) throws Exception {
		String sql = null;
		String guid = req.getRequest().getTemplateID();
		boolean existGuid;
		String[] conditionValues = { guid }; 		
		sql = "select *  from DCP_PTEMPLATE  where PTEMPLATE_ID = ? ";

		List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);
		if (getQData != null && getQData.isEmpty() == false) {
			existGuid = true;
		} else {
			existGuid =  false;
		}
		return existGuid;
	}

}
