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
import com.dsc.spos.json.cust.req.DCP_StockOutTemplateUpdateReq;
import com.dsc.spos.json.cust.req.DCP_StockOutTemplateUpdateReq.level2Elm;
import com.dsc.spos.json.cust.res.DCP_StockOutTemplateUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 服務函數：DCP_StockOutTemplateUpdate 說明：调拨模板新增 服务说明：调拨模板新增
 * 
 * @author 袁云洋
 * @since 2019-12-18
 */
public class DCP_StockOutTemplateUpdate
		extends SPosAdvanceService<DCP_StockOutTemplateUpdateReq, DCP_StockOutTemplateUpdateRes> {

	@Override
	protected boolean isVerifyFail(DCP_StockOutTemplateUpdateReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0; // 考慮當錯誤很多時則直接顯示格式錯誤；
		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		String templateName = req.getRequest().getTemplateName();
		String templateNo = req.getRequest().getTemplateNo();
		List<DCP_StockOutTemplateUpdateReq.level2Elm> lv2 = req.getRequest().getDatas();

		if (Check.Null(templateNo)) {
			errCt++;
			errMsg.append("模板编码不可为空值, ");
			isFail = true;
		}
		
		if (Check.Null(templateName)) {
			errCt++;
			errMsg.append("模板名称不可为空值, ");
			isFail = true;
		}

		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		for (level2Elm par : lv2) {
			// 必传值不为空

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
				errMsg.append("要货单位不可为空值, ");
				isFail = true;
			}

			if (isFail) {
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_StockOutTemplateUpdateReq> getRequestType() {
		return new TypeToken<DCP_StockOutTemplateUpdateReq>() {
		};
	}

	@Override
	protected DCP_StockOutTemplateUpdateRes getResponseType() {
		return new DCP_StockOutTemplateUpdateRes();
	}

	@Override
	protected void processDUID(DCP_StockOutTemplateUpdateReq req, DCP_StockOutTemplateUpdateRes res) throws Exception {
		String eId = req.geteId();
		String templateName = req.getRequest().getTemplateName();
		String status = req.getRequest().getStatus();

		String modifyBy = req.getOpNO();
		Calendar cal = Calendar.getInstance();// 获得当前时间
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String modifyDate = df.format(cal.getTime());
		df = new SimpleDateFormat("HHmmss");
		String modifyTime = df.format(cal.getTime());

		String templateNO = "";
		String shopType = req.getRequest().getShopType();
		// 向下兼容，前端可能没更新，默认值2
		if (shopType == null || shopType.isEmpty()) {
			shopType = "2";
		}

		try {
			templateNO = req.getRequest().getTemplateNo();

			// 删除原来单身
			DelBean db1 = new DelBean("DCP_PTEMPLATE_DETAIL");
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db1.addCondition("PTEMPLATENO", new DataValue(templateNO, Types.VARCHAR));
			db1.addCondition("DOC_TYPE", new DataValue("5", Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));
			DataValue[] insValue = null;
			// 新增單身 (多筆)
			List<level2Elm> jsonDatas = req.getRequest().getDatas();
			for (level2Elm par : jsonDatas) {
				int insColCt = 0;
				String[] columnsName = { "EID", "PTEMPLATENO", "DOC_TYPE", "ITEM", "PLUNO", "PUNIT", "MIN_QTY",
						"MAX_QTY", "MUL_QTY", "STATUS" };
				
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
						keyVal = par.getPluNo(); // pluNO
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
						keyVal = "100";
						break;
					default:
						break;
					}

					if (keyVal != null) {
						insColCt++;
						if (i == 3) {
							columnsVal[i] = new DataValue(keyVal, Types.INTEGER);
						} else if (i == 6 || i == 7 || i == 8) {
							columnsVal[i] = new DataValue(keyVal, Types.FLOAT);
						} else {
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

			// 更新单头
			UptBean ub1 = null;
			ub1 = new UptBean("DCP_PTEMPLATE");
			ub1.addUpdateValue("PTEMPLATE_NAME", new DataValue(templateName, Types.VARCHAR));
			ub1.addUpdateValue("MODIFYBY", new DataValue(modifyBy, Types.VARCHAR));
			ub1.addUpdateValue("MODIFY_DATE", new DataValue(modifyDate, Types.VARCHAR));
			ub1.addUpdateValue("MODIFY_TIME", new DataValue(modifyTime, Types.VARCHAR));
			ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
			ub1.addUpdateValue("SHOPTYPE", new DataValue(shopType, Types.VARCHAR));

			// condition
			ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			ub1.addCondition("PTEMPLATENO", new DataValue(templateNO, Types.VARCHAR));
			ub1.addCondition("DOC_TYPE", new DataValue("5", Types.VARCHAR));

			this.addProcessData(new DataProcessBean(ub1));

			this.doExecuteDataToDB();

			// 如果选择了 全部门店，删除之前设置的生效门店，不用放在一个事务
			if (shopType.equals("1")) {
				try {

					this.pData.clear();
					DelBean db_shop = new DelBean("DCP_PTEMPLATE_SHOP");
					db_shop.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					db_shop.addCondition("PTEMPLATENO", new DataValue(templateNO, Types.VARCHAR));
					db_shop.addCondition("DOC_TYPE", new DataValue("5", Types.VARCHAR));
					this.addProcessData(new DataProcessBean(db_shop));
					this.doExecuteDataToDB();

				} catch (Exception e) {

				}

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
	protected List<InsBean> prepareInsertData(DCP_StockOutTemplateUpdateReq req) throws Exception {
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_StockOutTemplateUpdateReq req) throws Exception {
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_StockOutTemplateUpdateReq req) throws Exception {
		return null;
	}

	@Override
	protected String getQuerySql(DCP_StockOutTemplateUpdateReq req) throws Exception {
		String sql = null;
		return sql;
	}

}
