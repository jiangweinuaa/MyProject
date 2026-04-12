package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.Calendar;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_STakeTemplateCreateReq;
import com.dsc.spos.json.cust.req.DCP_STakeTemplateCreateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_STakeTemplateCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
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
public class DCP_STakeTemplateCreate extends SPosAdvanceService<DCP_STakeTemplateCreateReq, DCP_STakeTemplateCreateRes> {

	@Override
	protected boolean isVerifyFail(DCP_STakeTemplateCreateReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();

        if(req.getRequest()==null) {
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		List<level1Elm> jsonDatas = req.getRequest().getDatas();		
		String templateID = req.getRequest().getTemplateID();
		String templateName = req.getRequest().getTemplateName();
		String taskWay = req.getRequest().getTaskWay();
		String isBtake = req.getRequest().getIsBtake();
		String timeType = req.getRequest().getTimeType();
		String rangeWay = req.getRequest().getRangeWay();  //0.按商品盘点 1.按分类盘点
		String v_status = req.getRequest().getStatus();
		
		if (Check.Null(rangeWay)) rangeWay="0";
		
		if (Check.Null(templateID)) {
            errMsg.append("模板ID不可为空值, ");
			isFail = true;
		} 

		if (Check.Null(templateName)) {
            errMsg.append("模板名称不可为空值, ");
			isFail = true;
		} 

		if (Check.Null(taskWay)) {
            errMsg.append("盘点方式不可为空值, ");
			isFail = true;
		} 

		if (Check.Null(isBtake)) {
            errMsg.append("是否盲盘不可为空值, ");
			isFail = true;
		} 

		if (Check.Null(timeType)) {
            errMsg.append("周期类型不可为空值, ");
			isFail = true;
		} 
		
		if (Check.Null(v_status)) {
            errMsg.append("状态不可为空值, ");
			isFail = true;
		}

        List<DCP_STakeTemplateCreateReq.OrgList> orgList = req.getRequest().getOrgList();
        if(CollUtil.isNotEmpty(orgList)) {
            for (DCP_STakeTemplateCreateReq.OrgList org : orgList) {
                String orgNo = org.getOrganizationNo();
                if (Check.Null(orgNo)) {
                    errMsg.append("组织编码不可为空值, ");
                    isFail = true;
                }
                String warehouse = org.getWarehouse();
                if (Check.Null(warehouse)) {
                    errMsg.append("仓库不可为空值, ");
                    isFail = true;
                }
                String status = org.getStatus();
                if (Check.Null(status)) {
                    errMsg.append("状态不可为空值, ");
                    isFail = true;
                }
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
			String categoryNo = par.getCategoryNo();

			if (rangeWay.equals("0"))  //商品盘点
			{
				if (Check.Null(pluNO)) {
                    errMsg.append("商品编码不可为空值, ");
					isFail = true;
				}

				if (Check.Null(punit)) {
                    errMsg.append("单位不可为空值, ");
					isFail = true;
				}
			}
			else if (rangeWay.equals("1"))  //分类盘点
			{
				if (Check.Null(categoryNo)) {
                    errMsg.append("商品分类不可为空值, ");
					isFail = true;
				}
			}

			if (Check.Null(item)) {
                errMsg.append("项次不可为空值, ");
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
	protected TypeToken<DCP_STakeTemplateCreateReq> getRequestType() {
		return new TypeToken<DCP_STakeTemplateCreateReq>(){};
	}

	@Override
	protected DCP_STakeTemplateCreateRes getResponseType() {
		return new DCP_STakeTemplateCreateRes();
	}

	@Override
	protected void processDUID(DCP_STakeTemplateCreateReq req,DCP_STakeTemplateCreateRes res) throws Exception {		
		String eId = req.geteId();
		String templateName = req.getRequest().getTemplateName();
		String taskWay = req.getRequest().getTaskWay();
		String isBtake = req.getRequest().getIsBtake();
		String timeType = req.getRequest().getTimeType();
		if (Check.Null(timeType)) timeType="0";
		String timeValue = req.getRequest().getTimeValue();
		String status = req.getRequest().getStatus();
		String createBy = req.getEmployeeNo();
		String templateID = req.getRequest().getTemplateID();
		Calendar cal = Calendar.getInstance();//获得当前时间
		SimpleDateFormat df=new SimpleDateFormat("yyyyMMdd");
		String createDate = df.format(cal.getTime());
		df = new SimpleDateFormat("HHmmss");
		String createTime = df.format(cal.getTime());
		String templateNO ="";
		String stockTakeCheck = req.getRequest().getStockTakeCheck();
		String shopType = req.getRequest().getShopType();
		//向下兼容，前端可能没更新，默认值2
		if(shopType==null||shopType.isEmpty())
		{
			shopType = "2";
		}

		//解析日期值使用 BY JZMA 20200407
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

		//是否调整库存直接给Y  BY JZMA 20200102
		//【ID1030281】 //【大万3.0】车销业务场景下系统改造评估-----盘点差异生成销售单服务端 by jinzma 20221219
		//【ID1039808】【金贝儿3403】盘点单审核增加处理不异动库存情况 by jinzma 20240327
		String isAdjustStock = req.getRequest().getIsAdjustStock();  //是否调整库存Y/N/X Y转库存 N转销售 X不异动
		if (Check.Null(isAdjustStock)){
			isAdjustStock="Y";
		}


		//盘点范围选择 默认给0  BY JZMA 20200115
		String rangeWay = req.getRequest().getRangeWay();  //0.按商品盘点 1.按分类盘点
		if (Check.Null(rangeWay)) rangeWay="0";

		//是否显示零库存商品  by jzma 20200205 
		String isShowZStock = req.getRequest().getIsShowZStock();
		if (Check.Null(isShowZStock)|| !isShowZStock.equals("0")) isShowZStock="1";

		try 
		{
			if (!checkGuid(req)){
				templateNO = gettemplateNO(req);
				String[] columns1 = {
						"EID", "PTEMPLATENO","DOC_TYPE","PTEMPLATE_NAME","PTEMPLATE_ID", "TIME_TYPE", 
						"TIME_VALUE","TOT_CQTY", "PRE_DAY", "TASKWAY", "IS_BTAKE", "CREATEBY", "CREATE_DATE",
						"CREATE_TIME", "STATUS","STOCKTAKECHECK","SHOPTYPE","IS_ADJUST_STOCK","RANGEWAY","ISSHOWZSTOCK"
				};
				DataValue[] insValue = null;

				//新增單身 (多筆)
				List<level1Elm> jsonDatas = req.getRequest().getDatas();
				for (level1Elm par : jsonDatas) {
					int insColCt = 0;	
					String[] columnsName = {
							"EID", "PTEMPLATENO", "DOC_TYPE", "ITEM", "PLUNO",
							"PUNIT", "MIN_QTY", "MAX_QTY", "MUL_QTY",
							"STATUS","CATEGORYNO"
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
							keyVal = "1";
							break;
						case 3:  
							keyVal = par.getItem();
							break;
						case 4: 
							if (rangeWay.equals("0"))   //0.按商品盘点 1.按分类盘点
							{
								keyVal = par.getPluNo(); //pluNO
							}
							else 
							{
								keyVal = par.getCategoryNo();
							}
							break;
						case 5:  
							keyVal = "";
							if (rangeWay.equals("0"))   //0.按商品盘点 1.按分类盘点
							{
								keyVal = par.getPunit();
							}
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
							keyVal = "";
							if (rangeWay.equals("1"))   //0.按商品盘点 1.按分类盘点
							{
								keyVal = par.getCategoryNo(); 
							}							
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
						new DataValue("1", Types.VARCHAR),
						new DataValue(templateName, Types.VARCHAR), 
						new DataValue(templateID, Types.VARCHAR), 
						new DataValue(timeType, Types.VARCHAR), 
						new DataValue(timeValue, Types.VARCHAR),
						new DataValue("0", Types.FLOAT),
						new DataValue("0", Types.INTEGER),
						new DataValue(taskWay, Types.VARCHAR), 
						new DataValue(isBtake, Types.VARCHAR), 
						new DataValue(createBy, Types.VARCHAR),
						new DataValue(createDate, Types.VARCHAR), 
						new DataValue(createTime, Types.VARCHAR),
						new DataValue(status, Types.VARCHAR),
						new DataValue(stockTakeCheck, Types.VARCHAR),
						new DataValue(shopType, Types.VARCHAR),
						new DataValue(isAdjustStock, Types.VARCHAR),
						new DataValue(rangeWay, Types.VARCHAR),
						new DataValue(isShowZStock, Types.VARCHAR),
				};

				InsBean ib1 = new InsBean("DCP_PTEMPLATE", columns1);
				ib1.addValues(insValue);
				this.addProcessData(new DataProcessBean(ib1)); // 新增單頭

                List<DCP_STakeTemplateCreateReq.OrgList> orgList = req.getRequest().getOrgList();
                if(CollUtil.isNotEmpty(orgList)){
                    for (DCP_STakeTemplateCreateReq.OrgList org : orgList) {
                        ColumnDataValue orgColumns=new ColumnDataValue();
                        orgColumns.add("EID", DataValues.newString(eId));
                        orgColumns.add("PTEMPLATENO", DataValues.newString(templateNO));
                        orgColumns.add("SHOPID", DataValues.newString(org.getOrganizationNo()));
                        orgColumns.add("DOC_TYPE", DataValues.newString("1"));
                        orgColumns.add("ORGANIZATIONNO", DataValues.newString(org.getOrganizationNo()));
                        orgColumns.add("WAREHOUSE", DataValues.newString(org.getWarehouse()));
                        orgColumns.add("STATUS", DataValues.newString(org.getStatus()));

                        String[] orgColumnNames = orgColumns.getColumns().toArray(new String[0]);
                        DataValue[] orgDataValues = orgColumns.getDataValues().toArray(new DataValue[0]);
                        InsBean ib2=new InsBean("DCP_PTEMPLATE_SHOP",orgColumnNames);
                        ib2.addValues(orgDataValues);
                        this.addProcessData(new DataProcessBean(ib2));
                    }
                }

                this.doExecuteDataToDB();
			}

			res.setTemplateNo(templateNO);
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		
		} catch (Exception e) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}

	}	

	@Override
	protected List<InsBean> prepareInsertData(DCP_STakeTemplateCreateReq req) throws Exception {
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_STakeTemplateCreateReq req) throws Exception {
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_STakeTemplateCreateReq req) throws Exception {
		return null;
	}	

	@Override
	protected String getQuerySql(DCP_STakeTemplateCreateReq req) throws Exception {
		return null;
	}

	private String gettemplateNO(DCP_STakeTemplateCreateReq req) throws Exception {

		/*
		 * 单据编号在后台按规格生成(固定编码+年月日+5位流水号(比如TBCK201607010001)，流水号取门店该单据最大流水号+1)
		 * 注意固定编码：如果是调拨出库则为DBCK 如果是退货出库则为THCK 如果是次品出库则为CPCK
		 */
		String sql = null;
		String templateNO = null;
		String eId = req.geteId();
		StringBuffer sqlbuf = new StringBuffer("");
		String[] conditionValues = {eId}; // 查询盘点单号

		Date dt = new Date();
		SimpleDateFormat matter = new SimpleDateFormat("yyyyMMdd");
		templateNO = "PDMB" + matter.format(dt);

		sqlbuf.append("" + "select PTEMPLATENO  from ( " + "select max(PTEMPLATENO) as  PTEMPLATENO "
				+ "  from DCP_PTEMPLATE " + " where DOC_TYPE='1' and  EID = ? "
				+ " and PTEMPLATENO like '%%" + templateNO + "%%' "); // 假資料
		sqlbuf.append(" ) TBL ");
		sql = sqlbuf.toString();

		List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);
		if (getQData != null && !getQData.isEmpty()) {

			templateNO = (String) getQData.get(0).get("PTEMPLATENO");

			if (templateNO != null && !templateNO.isEmpty()) {
				long i;
				templateNO = templateNO.substring(4);
				i = Long.parseLong(templateNO) + 1;
				templateNO = i + "";
				templateNO = "PDMB" + templateNO;
			} else {
				templateNO = "PDMB" + matter.format(dt) + "00001";
			}
		} else {
			templateNO = "PDMB" + matter.format(dt) + "00001";
		}

		return templateNO;
	}

	private boolean checkGuid(DCP_STakeTemplateCreateReq req) throws Exception {
		String guid = req.getRequest().getTemplateID();
		boolean existGuid;
		String[] conditionValues = { guid };
		String sql = "select *  from DCP_PTEMPLATE  where PTEMPLATE_ID = ? ";

		List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);
		if (getQData != null && !getQData.isEmpty()) {
			existGuid = true;
		} else {
			existGuid =  false;
		}
		return existGuid;
	}

}
