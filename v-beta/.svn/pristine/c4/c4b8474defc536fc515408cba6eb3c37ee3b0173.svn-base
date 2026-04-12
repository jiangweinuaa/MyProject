package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_StockOutTemplateCreateReq;
import com.dsc.spos.json.cust.req.DCP_StockOutTemplateCreateReq.level2Elm;
import com.dsc.spos.json.cust.res.DCP_StockOutTemplateCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 服務函數：DCP_StockOutTemplateCreate
 *    說明：调拨模板新增
 * 服务说明：调拨模板新增
 * @author 袁云洋 
 * @since  2019-12-18
 */
public class DCP_StockOutTemplateCreate extends SPosAdvanceService<DCP_StockOutTemplateCreateReq, DCP_StockOutTemplateCreateRes> 
{

	@Override
	protected boolean isVerifyFail(DCP_StockOutTemplateCreateReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		String templateName = req.getRequest().getTemplateName();
		List<DCP_StockOutTemplateCreateReq.level2Elm> lv2 = req.getRequest().getDatas();

		if (Check.Null(templateName)) {
			errCt++;
			errMsg.append("模板名称不可为空值, ");
			isFail = true;
		} 

		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}		

		for (level2Elm par : lv2) {
			//必传值不为空		

			String item = par.getItem();
			String pluNO = par.getPluNo();
			String punit = par.getPunit();

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

			if (isFail){
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_StockOutTemplateCreateReq> getRequestType() {
		return new TypeToken<DCP_StockOutTemplateCreateReq>(){};
	}

	@Override
	protected DCP_StockOutTemplateCreateRes getResponseType() {
		return new DCP_StockOutTemplateCreateRes();
	}

	@Override
	protected void processDUID(DCP_StockOutTemplateCreateReq req,DCP_StockOutTemplateCreateRes res) throws Exception {		
		String eId = req.geteId();
		String templateName = req.getRequest().getTemplateName();
		String status = req.getRequest().getStatus();
		String createBy = req.getOpNO();

		Calendar cal = Calendar.getInstance();//获得当前时间
		SimpleDateFormat df=new SimpleDateFormat("yyyyMMdd");
		String createDate = df.format(cal.getTime());
		df = new SimpleDateFormat("HHmmss");
		String createTime = df.format(cal.getTime());
		String templateNO ="";
		String shopType = req.getRequest().getShopType();
		//向下兼容，前端可能没更新，默认值2
		if(shopType==null||shopType.isEmpty())
		{
			shopType = "2";
		}

		try 
		{
			templateNO = gettemplateNO(req);
			String[] columns1 = {
					"EID", "PTEMPLATENO","DOC_TYPE","PTEMPLATE_NAME", 
					"CREATEBY", "CREATE_DATE", "CREATE_TIME", "STATUS", "SHOPTYPE","TIME_TYPE"
			};
			DataValue[] insValue = null;

			//新增單身 (多筆)
			List<level2Elm> jsonDatas = req.getRequest().getDatas();
			for (level2Elm par : jsonDatas) {
				int insColCt = 0;	
				String[] columnsName = {
						"EID", "PTEMPLATENO", "DOC_TYPE", "ITEM", "PLUNO",
						"PUNIT", "MIN_QTY", "MAX_QTY", "MUL_QTY", "STATUS"
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
						keyVal = "5";
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
					new DataValue("5", Types.VARCHAR),
					new DataValue(templateName, Types.VARCHAR), 
					new DataValue(createBy, Types.VARCHAR),
					new DataValue(createDate, Types.VARCHAR), 
					new DataValue(createTime, Types.VARCHAR),
					new DataValue(status, Types.VARCHAR),
					new DataValue(shopType, Types.VARCHAR),
					new DataValue("0", Types.VARCHAR)

			};

			InsBean ib1 = new InsBean("DCP_PTEMPLATE", columns1);
			ib1.addValues(insValue);
			this.addProcessData(new DataProcessBean(ib1)); // 新增單頭		
			this.doExecuteDataToDB();	

			res.setTemplateNo(templateNO);
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		}
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}

	}	

	@Override
	protected List<InsBean> prepareInsertData(DCP_StockOutTemplateCreateReq req) throws Exception {
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_StockOutTemplateCreateReq req) throws Exception {
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_StockOutTemplateCreateReq req) throws Exception {
		return null;
	}	

	@Override
	protected String getQuerySql(DCP_StockOutTemplateCreateReq req) throws Exception {
		String sql = null;
		return sql;
	}

	private String gettemplateNO(DCP_StockOutTemplateCreateReq req) throws Exception {

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
		templateNO = "DBMB" + matter.format(dt);

		sqlbuf.append("" + "select PTEMPLATENO  from ( " + "select max(PTEMPLATENO) as  PTEMPLATENO "
				+ "  from DCP_PTEMPLATE " + " where DOC_TYPE='5' and  EID = ? "
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
				templateNO = "DBMB" + templateNO;
			} else {
				templateNO = "DBMB" + matter.format(dt) + "00001";
			}
		} else {
			templateNO = "DBMB" + matter.format(dt) + "00001";
		}

		return templateNO;
	}

}
