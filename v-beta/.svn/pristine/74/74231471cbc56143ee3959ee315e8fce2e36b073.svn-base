package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_SupLicenseApplyStatusUpdateReq;
import com.dsc.spos.json.cust.res.DCP_SupLicenseApplyRes;
import com.dsc.spos.ninetyone.util.ColumnDataValue;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.SUtil;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 供应商证照异动单状态变更
 * 
 * @date 2024-09-19
 * @author 01029
 */
public class DCP_SupLicenseApplyStatusUpdate
		extends SPosAdvanceService<DCP_SupLicenseApplyStatusUpdateReq, DCP_SupLicenseApplyRes> {

	@Override
	protected void processDUID(DCP_SupLicenseApplyStatusUpdateReq req, DCP_SupLicenseApplyRes res) throws Exception {

		try {

			String eId = req.geteId();
			String status = "1";// 状态：-1未启用100已启用 0已禁用
			if (!"confirm".equals(req.getRequest().getOprType())) {
				status = "2";
			}

			String sql = " SELECT * FROM DCP_SUPLICENSECHANGE WHERE EID='%s'   AND BILLNO='%s'  ";
			String sCode = req.getRequest().getBillNo();
			StringBuffer errMsg = new StringBuffer("");
			sql = String.format(sql, req.geteId(), sCode);
			String opType = null;
			List<Map<String, Object>> mDatas = this.doQueryData(sql, null);
			if (SUtil.EmptyList(mDatas)) {
				errMsg.append("单据不存在");

			} else {
				Map<String, Object> bill = mDatas.get(0);
				if (!"0".equals(bill.get("STATUS"))) {
					errMsg.append("单据状态非【0-新建】不可更改！");

				}
				opType = bill.get("OPTYPE").toString();
			}
			if (!"".equals(errMsg.toString())) {
				res.setSuccess(false);
				res.setServiceStatus("000");
				res.setServiceDescription(errMsg.toString());
				return;
			}
			String keyNo = req.getRequest().getBillNo();

			String oPId = req.getEmployeeNo();
			UptBean up1 = new UptBean("DCP_SUPLICENSECHANGE");
			up1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			up1.addCondition("BILLNO", new DataValue(keyNo, Types.VARCHAR));
			up1.addUpdateValue("LASTMODIOPID", new DataValue(oPId, Types.VARCHAR));

			String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			up1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
			if ("1".equals(status)) {
				up1.addUpdateValue("CONFIRMBY", new DataValue(oPId, Types.VARCHAR));
				up1.addUpdateValue("CONFIRMTIME", new DataValue(lastmoditime, Types.DATE));
			} else {
				up1.addUpdateValue("CANCELBY", new DataValue(oPId, Types.VARCHAR));
				up1.addUpdateValue("CANCELTIME", new DataValue(lastmoditime, Types.DATE));
			}

			up1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(up1));

			if ("1".equals(status)) {
				sql = " SELECT * FROM DCP_SUPLICENSECHANGE_DETAIL WHERE EID='%s'   AND BILLNO='%s' ";
		        sql = String.format(sql, eId,keyNo);
				List<Map<String, Object>> mDatas1 = this.doQueryData(sql, null);
				if (!SUtil.EmptyList(mDatas1)) {
					for (Map<String, Object> oneData : mDatas1) {						
						ColumnDataValue columns = new ColumnDataValue();
						String[] columns1 = null;
						DataValue[] insValue1 = null;
						String license = "";
						if (StringUtils.isEmpty(oneData.get("LICENSENO").toString())){
							license = " ";
						}
						if ("I".equals(opType)) {
							DelBean db1 = new DelBean("DCP_SUPPLIER_LICENSE");
							db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
							db1.addCondition("SUPPLIER", new DataValue(oneData.get("SUPPLIER").toString(), Types.VARCHAR));
							db1.addCondition("IMGTYPE", new DataValue(oneData.get("LICENSETYPE").toString(), Types.VARCHAR));
							db1.addCondition("LICENSENO", new DataValue(oneData.get("LICENSENO").toString(), Types.VARCHAR));
							this.addProcessData(new DataProcessBean(db1));
							columns.Columns.clear();
							columns.DataValues.clear();
							columns.Add("EID", eId, Types.VARCHAR);
							columns.Add("SUPPLIER", oneData.get("SUPPLIER").toString(), Types.VARCHAR);
							columns.Add("ITEM", "1", Types.VARCHAR);
							
							columns.Add("IMGTYPE", oneData.get("LICENSETYPE").toString(), Types.VARCHAR);
							columns.Add("LICENSEIMG", oneData.get("LICENSEIMG").toString(), Types.VARCHAR);
							//columns.Add("TRAN_TIME", "", Types.VARCHAR);
							 
							columns.Add("LICENSENO", oneData.get("LICENSENO").toString()+license, Types.VARCHAR);
							columns.Add("BEGINDATE", oneData.get("BEGINDATE").toString(), Types.DATE);
							columns.Add("ENDDATE", oneData.get("ENDDATE").toString(), Types.DATE);
							
							columns.Add("STATUS", "100", Types.VARCHAR);

							columns.Add("CREATEOPID", req.getEmployeeNo(), Types.VARCHAR);
							columns.Add("CREATEDEPTID", req.getDepartmentNo(), Types.VARCHAR);
							columns.Add("CREATETIME", lastmoditime, Types.DATE);
							

							columns1 = columns.Columns.toArray(new String[0]);
							insValue1 = columns.DataValues.toArray(new DataValue[0]);
							InsBean ib1 = new InsBean("DCP_SUPPLIER_LICENSE", columns1);
							ib1.addValues(insValue1);
							this.addProcessData(new DataProcessBean(ib1));
						} else if ("U".equals(opType)) {
							UptBean up2 = new UptBean("DCP_SUPPLIER_LICENSE");
							up2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
							up2.addCondition("SUPPLIER", new DataValue(oneData.get("SUPPLIER").toString(), Types.VARCHAR));
							up2.addCondition("IMGTYPE", new DataValue(oneData.get("LICENSETYPE").toString(), Types.VARCHAR));
							up2.addCondition("LICENSENO", new DataValue(oneData.get("LICENSENO").toString()+license, Types.VARCHAR));
							
							up2.addUpdateValue("LICENSEIMG", new DataValue(oneData.get("LICENSEIMG").toString(), Types.VARCHAR));
							up2.addUpdateValue("BEGINDATE", new DataValue(oneData.get("BEGINDATE").toString(), Types.DATE));
							up2.addUpdateValue("ENDDATE", new DataValue(oneData.get("ENDDATE").toString(), Types.DATE));
							up2.addUpdateValue("STATUS", new DataValue("100", Types.VARCHAR));
							
							up2.addUpdateValue("LASTMODIOPID", new DataValue(oPId, Types.VARCHAR));
							up2.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
							
							this.addProcessData(new DataProcessBean(up2));
						}

					}
				}
			}
			this.doExecuteDataToDB();

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");

		} catch (Exception e) {

			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_SupLicenseApplyStatusUpdateReq req) throws Exception {

		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_SupLicenseApplyStatusUpdateReq req) throws Exception {

		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_SupLicenseApplyStatusUpdateReq req) throws Exception {

		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_SupLicenseApplyStatusUpdateReq req) throws Exception {

		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		if (req.getRequest() == null) {
			errMsg.append("request不能为空值 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		if (req.getRequest().getOprType() == null) {
			errMsg.append("操作类型不能为空值 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		String sCode = req.getRequest().getBillNo();

		if (Check.Null(sCode)) {
			errMsg.append("单据编号不能为空值 ");
			isFail = true;
		}

		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_SupLicenseApplyStatusUpdateReq> getRequestType() {

		return new TypeToken<DCP_SupLicenseApplyStatusUpdateReq>() {
		};
	}

	@Override
	protected DCP_SupLicenseApplyRes getResponseType() {

		return new DCP_SupLicenseApplyRes();
	}

}
