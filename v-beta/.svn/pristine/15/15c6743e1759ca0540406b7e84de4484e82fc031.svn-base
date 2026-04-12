package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;

import com.dsc.spos.json.JsonBasicRes;
import com.dsc.spos.json.cust.req.DCP_QualityCheckStateUpdateReq;
import com.dsc.spos.ninetyone.util.ColumnDataValue;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.SUtil;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @date 2024-09-19
 * @author 01029
 */
public class DCP_QualityCheckStateUpdate extends SPosAdvanceService<DCP_QualityCheckStateUpdateReq, JsonBasicRes> {

	@Override
	protected void processDUID(DCP_QualityCheckStateUpdateReq req, JsonBasicRes res) throws Exception {

		try {

			String eId = req.geteId();
			String status = "100";// 状态：-1未启用100已启用 0已禁用
			String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			String sql = null;
			String oprType = req.getRequest().getOprType();
			String sourceBill = null;
			String sourceItem = null;
			String sourceItem1 = null;
			if ("confirm".equals(oprType)) {
				status = "1";
			} else {
				status = "0";
			}
			List<Map<String, Object>> mDatas = null;
			if (SUtil.EmptyList(mDatas)) {
				ColumnDataValue columns = new ColumnDataValue();
				UptBean ub1 = null;
				String billNo = null;
				double rejectQty = 0; // 验退不良量
				double deliQty = 0; // 送验量
				double passQty = 0; // 允收合格量BigDecimal BigDecimal qty = new//
									// BigDecimal(par.getPqty()) ;
				String qcType = null;
				ArrayList<String> billList = new ArrayList<String>();
				List<DCP_QualityCheckStateUpdateReq.Detail1> detailLists = req.getRequest().getDataList();
				StringBuffer errMsg = new StringBuffer("");
				ArrayList<String> listQ = new ArrayList<>();
				HashMap<String, ArrayList<String>> hashMapQ = new HashMap<>();
				if (!SUtil.EmptyList(detailLists)) {
					for (DCP_QualityCheckStateUpdateReq.Detail1 par : detailLists) {
						//sql = " SELECT * FROM DCP_SSTOCKIN_DETAIL b inner join   DCP_QUALITYCHECK a  "
						//		+" on a.SOURCEBILLNO=b.OFNO and a.OITEM=b.OITEM and b.STOCKINTYPE='1' "
						//		+" WHERE EID='%s'   AND QCBILLNO='%s' and  b.STOCKINTYPE='1' and a.STATUS<>'2'";
						//sql = String.format(sql, req.geteId(), billNo); //tod
			
						
						
						billNo = par.getQcBillNo();
						sql = " SELECT * FROM DCP_QUALITYCHECK WHERE EID='%s'   AND QCBILLNO='%s'  ";
						sql = String.format(sql, req.geteId(), billNo);
						List<Map<String, Object>> mDatasQry = this.doQueryData(sql, null);
						if (SUtil.EmptyList(mDatasQry)) {

							errMsg.append("单号:" + billNo + "不存在！");
							continue;
						} 
               
							qcType = mDatasQry.get(0).get("QCTYPE").toString();
							//String deliver =  mDatasQry.get(0).get("DELIVERQTY").toString();
							deliQty = Double.valueOf(mDatasQry.get(0).get("DELIVERQTY").toString());
							sourceBill = mDatasQry.get(0).get("SOURCEBILLNO").toString();
							sourceItem = mDatasQry.get(0).get("OITEM").toString();
							//sourceItem1 = mDatasQry.get(0).get("OITEM2").toString();
							rejectQty = Double.valueOf(par.getRejectQty());
							passQty = deliQty - rejectQty;
							if (rejectQty < 0 || rejectQty > deliQty) {
								errMsg.append("单号:" + billNo + "验退不良量 数量不可小于0或者大于送验量！");
								continue;
							}
							if (passQty < 0 || passQty > deliQty) {
								errMsg.append("单号:" + billNo + "允收合格量 数量不可小于0或者大于送验量！");
								continue;
							}
						 

						ub1 = new UptBean("DCP_QUALITYCHECK");

						ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
						ub1.addCondition("QCBILLNO", new DataValue(billNo, Types.VARCHAR));
						if ("1".equals(status)) {
							ub1.addUpdateValue("PASSQTY", new DataValue(String.valueOf(passQty), Types.VARCHAR));
							ub1.addUpdateValue("REJECTQTY", new DataValue(par.getRejectQty(), Types.VARCHAR));
						} else {
							ub1.addUpdateValue("PASSQTY", new DataValue("0", Types.VARCHAR));
							ub1.addUpdateValue("REJECTQTY", new DataValue("0", Types.VARCHAR));
						} 
						ub1.addUpdateValue("RESULT", new DataValue(par.getResult(), Types.VARCHAR));
						ub1.addUpdateValue("MEMO", new DataValue(par.getMemo(), Types.VARCHAR));
						ub1.addUpdateValue("INSPECTOR", new DataValue(par.getInspector(), Types.VARCHAR));
						ub1.addUpdateValue("INSPECT_DATE", new DataValue(par.getInspectDate(), Types.VARCHAR));
						ub1.addUpdateValue("INSPECT_TIME", new DataValue(par.getInspectTime(), Types.VARCHAR));
						ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getEmployeeNo(), Types.VARCHAR));
						ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
						ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
						
						//ub1.addCondition("STATUS", new DataValue(status, Types.VARCHAR));
						this.addProcessData(new DataProcessBean(ub1)); // update

						if ("1".equals(qcType)) {
							ub1 = new UptBean("DCP_PURRECEIVE_DETAIL");
							ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
							ub1.addCondition("BILLNO", new DataValue(sourceBill, Types.VARCHAR));
							ub1.addCondition("ITEM", new DataValue(sourceItem, Types.VARCHAR));
							//ub1.addCondition("ITEM2", new DataValue(sourceItem1, Types.VARCHAR));
							ub1.addUpdateValue("PASSQTY", new DataValue(String.valueOf(passQty), Types.VARCHAR));
							ub1.addUpdateValue("REJECTQTY", new DataValue(par.getRejectQty(), Types.VARCHAR));
							if ("1".equals(status)) {
								ub1.addUpdateValue("PASSQTY", new DataValue(String.valueOf(passQty), Types.VARCHAR));
								ub1.addUpdateValue("REJECTQTY", new DataValue(par.getRejectQty(), Types.VARCHAR));
								ub1.addUpdateValue("QCSTATUS", new DataValue("2", Types.VARCHAR));
							} else {
								ub1.addUpdateValue("PASSQTY", new DataValue("0", Types.VARCHAR));
								ub1.addUpdateValue("REJECTQTY", new DataValue("0", Types.VARCHAR));
								ub1.addUpdateValue("QCSTATUS", new DataValue("1", Types.VARCHAR));
							}
							//ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getEmployeeNo(), Types.VARCHAR));
							//ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
							//ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getEmployeeNo(), Types.VARCHAR));
							//ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
							this.addProcessData(new DataProcessBean(ub1)); // update
//							// 如果单号序号存在的 就把数量加上，不存在的 就新增当前
//							listQ = hashMapQ.get(sourceBill + sourceItem + sourceItem1); 
//																							 
//							if (null == listQ) { // 每个品号批号 只更新一次，是汇总更新，所以要加上判断
//								listQ = new ArrayList<String>();
//								listQ.add(sourceBill);
//								listQ.add(sourceItem);
//								listQ.add(sourceItem1);
//								listQ.add(String.valueOf(passQty));
//								listQ.add(par.getRejectQty());	
//								hashMapQ.put(sourceBill + sourceItem + sourceItem1, listQ);
//							} else { //汇总
//								listQ.set(3, String.valueOf(passQty + Double.valueOf(listQ.get(3))));
//								listQ.set(4, String
//										.valueOf(Double.valueOf(par.getRejectQty()) + Double.valueOf(listQ.get(4))));
//							}

						} 
					}
				}

				if (null != hashMapQ) {
					for (ArrayList<String> list : hashMapQ.values()) {
						ub1 = new UptBean("DCP_PURSTOCKIN_DETAIL");
						ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
						ub1.addCondition("PSTOCKINNO", new DataValue(list.get(0), Types.VARCHAR));
						ub1.addCondition("ITEM", new DataValue(list.get(1), Types.VARCHAR));
						ub1.addCondition("ITEM2", new DataValue(list.get(2), Types.VARCHAR));
						ub1.addUpdateValue("PASSQTY", new DataValue(list.get(3), Types.VARCHAR));
						ub1.addUpdateValue("REJECTQTY", new DataValue(list.get(4), Types.VARCHAR));
						ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getEmployeeNo(), Types.VARCHAR));
						ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
						ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getEmployeeNo(), Types.VARCHAR));
						ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
						this.addProcessData(new DataProcessBean(ub1)); // update
					}
				}
				this.doExecuteDataToDB();
				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription(errMsg.toString() + "服务执行成功");
			} else {
				res.setSuccess(false);
				res.setServiceStatus("200");
				res.setServiceDescription("服务执行失败");
				return;
			}

		} catch (Exception e) {

			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_QualityCheckStateUpdateReq req) throws Exception {

		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_QualityCheckStateUpdateReq req) throws Exception {

		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_QualityCheckStateUpdateReq req) throws Exception {

		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_QualityCheckStateUpdateReq req) throws Exception {

		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		if (req.getRequest() == null) {
			errMsg.append("request不能为空值 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		String oprType = null;
		oprType = req.getRequest().getOprType();
		if (oprType == null) {
			errMsg.append("操作类型不能为空值 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		if (!"confirm".equals(oprType) && !"unconfirm".equals(oprType)) {
			errMsg.append("操作类型参数异常 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_QualityCheckStateUpdateReq> getRequestType() {

		return new TypeToken<DCP_QualityCheckStateUpdateReq>() {
		};
	}

	@Override
	protected JsonBasicRes getResponseType() {

		return new JsonBasicRes();
	}

}
