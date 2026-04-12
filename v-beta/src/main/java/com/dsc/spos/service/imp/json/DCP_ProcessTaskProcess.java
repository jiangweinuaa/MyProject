package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ProcessTaskProcessReq;
import com.dsc.spos.json.cust.res.DCP_ProcessTaskProcessRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

import static com.dsc.spos.dao.DataValue.DataExpression.UpdateSelf;

public class DCP_ProcessTaskProcess extends SPosAdvanceService<DCP_ProcessTaskProcessReq, DCP_ProcessTaskProcessRes> {
	
	@Override
	protected void processDUID(DCP_ProcessTaskProcessReq req, DCP_ProcessTaskProcessRes res) throws Exception {
		String eId = req.geteId();
		String shopId = req.getShopId();
		String opNo = req.getOpNO();
		String accountDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
		String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
		String sTime = new SimpleDateFormat("HHmmss").format(new Date());
		String processTaskNo = req.getRequest().getProcessTaskNo();
        String status = req.getRequest().getStatus();

		String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());


        //try {
        //审核
        if("6".equals(status)) {
            String sql = ""
                    + " select a.eid,a.shopid,a.processtaskno,a.pdate,a.warehouse,a.materialwarehouse,"
                    + " a.tot_cqty,a.tot_pqty,a.tot_amt,a.tot_distriamt,"
                    + " b.pluno,b.featureno,b.pqty,b.punit,b.baseqty,b.baseunit,b.unit_ratio,b.price,b.distriprice,b.amt,b.distriamt"
                    + " from dcp_processtask a"
                    + " inner join dcp_processtask_detail b on a.eid=b.eid and a.shopid=b.shopid and a.processtaskno=b.processtaskno"
                    + " where a.eid='" + eId + "' and a.organizationno='" + shopId + "' and a.processtaskno='" + processTaskNo + "' and a.status='5' ";
            List<Map<String, Object>> getQData = this.doQueryData(sql, null);
            if (getQData != null && !getQData.isEmpty()) {
                UptBean ub1 = new UptBean("DCP_PROCESSTASK");
                //add Value
                ub1.addUpdateValue("STATUS", new DataValue("6", Types.VARCHAR));
                ub1.addUpdateValue("ACCOUNT_CHATUSERID", new DataValue(req.getChatUserId(), Types.VARCHAR));
                ub1.addUpdateValue("CONFIRMBY", new DataValue(opNo, Types.VARCHAR));
                ub1.addUpdateValue("CONFIRM_DATE", new DataValue(sDate, Types.VARCHAR));
                ub1.addUpdateValue("CONFIRM_TIME", new DataValue(sTime, Types.VARCHAR));
                ub1.addUpdateValue("SUBMITBY", new DataValue(opNo, Types.VARCHAR));
                ub1.addUpdateValue("SUBMIT_DATE", new DataValue(sDate, Types.VARCHAR));
                ub1.addUpdateValue("SUBMIT_TIME", new DataValue(sTime, Types.VARCHAR));
                ub1.addUpdateValue("ACCOUNTBY", new DataValue(opNo, Types.VARCHAR));
                ub1.addUpdateValue("ACCOUNT_DATE", new DataValue(accountDate, Types.VARCHAR));
                ub1.addUpdateValue("ACCOUNT_TIME", new DataValue(sTime, Types.VARCHAR));
                ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

                //condition
                ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub1.addCondition("PROCESSTASKNO", new DataValue(processTaskNo, Types.VARCHAR));
                ub1.addCondition("ORGANIZATIONNO", new DataValue(shopId, Types.VARCHAR));

                this.addProcessData(new DataProcessBean(ub1));

                //【ID1023852】【詹记】门店分批出数作业调整（拉出生产日期所有为当天的加工单据，目前是筛选单据生产。） by jinzma 20220331
                String pDate = getQData.get(0).get("PDATE").toString();
                String warehouse = getQData.get(0).get("WAREHOUSE").toString();
                String materialWarehouse = getQData.get(0).get("MATERIALWAREHOUSE").toString();

                sql = " select a.tot_cqty,a.tot_pqty,a.tot_amt,a.tot_distriamt,b.*,c.udlength"
                        + " from dcp_processtasksum a"
                        + " inner join dcp_processtasksum_detail b on a.eid=b.eid and a.shopid=b.shopid and a.processtasksumno=b.processtasksumno"
                        + " left  join dcp_unit c on a.eid=c.eid and b.punit=c.unit"
                        + " where a.eid='" + eId + "' and a.shopid='" + shopId + "' and a.pdate='" + pDate + "' "
                        + " and a.warehouse='" + warehouse + "' and a.materialwarehouse='" + materialWarehouse + "' ";
                List<Map<String, Object>> getQDataSum = this.doQueryData(sql, null);
                if (getQDataSum != null && !getQDataSum.isEmpty()) {
                    //有单头
                    updateTaskSum(req, getQData, getQDataSum);
                } else {
                    //无单头
                    insertTaskSum(req, getQData);
                }

            } else {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据不存在或已确认！");
            }

            this.doExecuteDataToDB();
        }

        //反审核
        if("5".equals(status)){
            //入参status=5，执行审核逆向流程
            //1.校验单据来源OTYPE，仅来源为SP或为空为NULL值的单据允许反审核，提示“当前单据来源不可反审核”
            //2.校验单据STATUS=6，且不存在单身GOODSSTATUS不为0，
            // 且不存在DCP_PSTOCKIN.OFNO=当前任务单，
            // 且不存在单身DISPATCHQTY不为0的数据，如满足条件则可执行反审核逻辑，不满足则提示“单据已执行后续流程，不可反审核”
            String sql="select a.otype,a.status,b.goodSstatus,b.dispatchqty,c.pstockinno,a.pdate,a.warehouse,a.materialwarehouse,b.pqty,b.pluno " +
					" from dcp_processtask a " +
					" inner join dcp_processtask_detail b on a.eid=b.eid and a.shopid=b.shopid and a.processtaskno=b.processtaskno " +
					" left join DCP_PSTOCKIN c on c.eid=a.eid and c.ofno=a.processtaskno "+
                    " where a.eid='"+eId+"' and a.organizationno='"+req.getOrganizationNO()+"' " +
                    " and a.processTaskNo='"+processTaskNo+"' ";
            List<Map<String, Object>> getQData = this.doQueryData(sql, null);
            if(getQData != null && !getQData.isEmpty()){
                String oType = getQData.get(0).get("OTYPE").toString();
				String docStatus = getQData.get(0).get("STATUS").toString();
				if(!("SP".equals(oType)||Check.Null(oType))){
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "当前单据来源不可反审核!");
				}
				if(!"6".equals(docStatus)){
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "当前单据状态不可反审核!");
				}
				List<Map<String, Object>> filterRows = getQData.stream().filter(x -> (Check.NotNull(x.get("GOODSSTATUS").toString()))&&!"0".equals(x.get("GOODSSTATUS").toString())
								|| Check.NotNull(x.get("PSTOCKINNO").toString())
								|| !(new BigDecimal(Check.Null(x.get("DISPATCHQTY").toString())?"0":x.get("DISPATCHQTY").toString())).equals(BigDecimal.ZERO))
						.collect(Collectors.toList());
				if(filterRows.size()>0){
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "单据已执行后续流程，不可反审核!");
				}

				//3.查询dcp_processtasksum和dcp_processtasksum_detail，
				// 且对应商品的PQTY-STOCKIN_QTY需大于等于当前任务单明细的PQTY，
				// 如不满足条件，则提示“加工任务汇总不存在或已完成”，满足条件则更新加工任务汇总减去当前任务单明细数量
				String pDate = getQData.get(0).get("PDATE").toString();
				String warehouse = getQData.get(0).get("WAREHOUSE").toString();
				String materialWarehouse = getQData.get(0).get("MATERIALWAREHOUSE").toString();

				sql = " select b.pluno,sum(b.pqty-nvl(b.stockin_qty,0) ) as lastqty"
						+ " from dcp_processtasksum a"
						+ " inner join dcp_processtasksum_detail b on a.eid=b.eid and a.shopid=b.shopid and a.processtasksumno=b.processtasksumno"
						+ " left  join dcp_unit c on a.eid=c.eid and b.punit=c.unit"
						+ " where a.eid='" + eId + "' and a.shopid='" + shopId + "' and a.pdate='" + pDate + "' "
						+ " and a.warehouse='" + warehouse + "' and a.materialwarehouse='" + materialWarehouse + "' " +
						"group by b.pluno ";
				List<Map<String, Object>> getQDataSum = this.doQueryData(sql, null);
				if(getQDataSum.size()>0){
					getQData.forEach(y->{
						String pluno = y.get("PLUNO").toString();
						BigDecimal pqty = new BigDecimal(y.get("PQTY").toString());
						List<Map<String, Object>> f1 = getQDataSum.stream().filter(z -> z.get("PLUNO").toString().equals(pluno)).collect(Collectors.toList());
						if(f1.size()>0){
							BigDecimal lastqty = new BigDecimal(f1.get(0).get("LASTQTY").toString());
							if(lastqty.compareTo(pqty)<0){
								try {
									throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "加工任务汇总不存在或已完成!");
								} catch (SPosCodeException e) {
									throw new RuntimeException(e);
								}
							}
						}
						else{
							try {
								throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "加工任务汇总不存在或已完成!");
							} catch (SPosCodeException e) {
								throw new RuntimeException(e);
							}
						}
					});
				}
				else{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "加工任务汇总不存在或已完成!");
				}

				//2.更新DCP_PROCESSTASK.STATUS=5，清空CONFIRMBY，CONFIRM_DATE，CONFIRM_TIME，更新UPDATE_TIME为当前系统时间
				UptBean ub1 = new UptBean("DCP_PROCESSTASK");
				ub1.addUpdateValue("STATUS", new DataValue("5", Types.VARCHAR));
				//更新CANCELBY，CANCEL_DATE，CANCEL_TIME，UPDATE_TIME
				ub1.addUpdateValue("CONFIRMBY", new DataValue("", Types.VARCHAR));
				ub1.addUpdateValue("CONFIRM_DATE", new DataValue("", Types.VARCHAR));
				ub1.addUpdateValue("CONFIRM_TIME", new DataValue("", Types.VARCHAR));
				ub1.addUpdateValue("UPDATE_TIME", new DataValue(lastmoditime, Types.VARCHAR));
				ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				ub1.addCondition("PROCESSTASKNO", new DataValue(processTaskNo, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(ub1));
			}
			this.doExecuteDataToDB();
        }
			
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
			
		//} catch (Exception e) {
		//	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
		//}
	}
	
	@Override
	protected List<InsBean> prepareInsertData(DCP_ProcessTaskProcessReq req) throws Exception {
		return null;
	}
	
	@Override
	protected List<UptBean> prepareUpdateData(DCP_ProcessTaskProcessReq req) throws Exception {
		return null;
	}
	
	@Override
	protected List<DelBean> prepareDeleteData(DCP_ProcessTaskProcessReq req) throws Exception {
		return null;
	}
	
	@Override
	protected boolean isVerifyFail(DCP_ProcessTaskProcessReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();
		String processTaskNO = req.getRequest().getProcessTaskNo();
		
		if(Check.Null(processTaskNO)){
			errMsg.append("加工任务单单号不可为空值, ");
			isFail = true;
		}
		
		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return false;
	}
	
	@Override
	protected TypeToken<DCP_ProcessTaskProcessReq> getRequestType() {
		return new TypeToken<DCP_ProcessTaskProcessReq>(){};
	}
	
	@Override
	protected DCP_ProcessTaskProcessRes getResponseType() {
		return new DCP_ProcessTaskProcessRes();
	}
	
	private void insertTaskSum(DCP_ProcessTaskProcessReq req,List<Map<String, Object>> getQData) throws Exception{
		String eId = req.geteId();
		String shopId = req.getShopId();
		String opNo = req.getOpNO();
		String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
		String sTime = new SimpleDateFormat("HHmmss").format(new Date());
		String processTaskNo = req.getRequest().getProcessTaskNo();
		String processTaskSumNo = processTaskNo.replace("JGRW", "JGHZ");
		
		//DCP_PROCESSTASKSUM_DETAIL
		{
			String[] detailColumns = {
					"EID", "SHOPID", "PROCESSTASKSUMNO",
					"PLUNO", "FEATURENO", "PQTY", "PUNIT", "BASEQTY", "BASEUNIT", "UNIT_RATIO",
					"STOCKIN_QTY", "SCRAP_QTY",
					"PRICE", "DISTRIPRICE", "AMT", "DISTRIAMT"
			};
			InsBean detailIb = new InsBean("DCP_PROCESSTASKSUM_DETAIL", detailColumns);
			for (Map<String, Object> oneData : getQData) {
				DataValue[] detailInsValue = new DataValue[]{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(shopId, Types.VARCHAR),
						new DataValue(processTaskSumNo, Types.VARCHAR),
						new DataValue(oneData.get("PLUNO").toString(), Types.VARCHAR),
						new DataValue(oneData.get("FEATURENO").toString(), Types.VARCHAR),
						new DataValue(oneData.get("PQTY").toString(), Types.VARCHAR),
						new DataValue(oneData.get("PUNIT").toString(), Types.VARCHAR),
						new DataValue(oneData.get("BASEQTY").toString(), Types.VARCHAR),
						new DataValue(oneData.get("BASEUNIT").toString(), Types.VARCHAR),
						new DataValue(oneData.get("UNIT_RATIO").toString(), Types.VARCHAR),
						new DataValue("0", Types.VARCHAR),
						new DataValue("0", Types.VARCHAR),
						new DataValue(oneData.get("PRICE").toString(), Types.VARCHAR),
						new DataValue(oneData.get("DISTRIPRICE").toString(), Types.VARCHAR),
						new DataValue(oneData.get("AMT").toString(), Types.VARCHAR),
						new DataValue(oneData.get("DISTRIAMT").toString(), Types.VARCHAR),
				};
				detailIb.addValues(detailInsValue);
			}
			this.addProcessData(new DataProcessBean(detailIb));
		}
		
		//DCP_PROCESSTASKSUM
		{
			String[] columns = {
					"EID", "SHOPID", "PROCESSTASKSUMNO", "PDATE", "WAREHOUSE", "MATERIALWAREHOUSE",
					"TOT_CQTY", "TOT_PQTY", "TOT_AMT", "TOT_DISTRIAMT",
					"CREATEBY", "CREATE_DATE", "CREATE_TIME"
			};
			InsBean ib = new InsBean("DCP_PROCESSTASKSUM", columns);
			DataValue[] insValue = new DataValue[]{
					new DataValue(eId, Types.VARCHAR),
					new DataValue(shopId, Types.VARCHAR),
					new DataValue(processTaskSumNo, Types.VARCHAR),
					new DataValue(getQData.get(0).get("PDATE").toString(), Types.VARCHAR),
					new DataValue(getQData.get(0).get("WAREHOUSE").toString(), Types.VARCHAR),
					new DataValue(getQData.get(0).get("MATERIALWAREHOUSE").toString(), Types.VARCHAR),
					new DataValue(getQData.get(0).get("TOT_CQTY"), Types.VARCHAR),
					new DataValue(getQData.get(0).get("TOT_PQTY"), Types.VARCHAR),
					new DataValue(getQData.get(0).get("TOT_AMT"), Types.VARCHAR),
					new DataValue(getQData.get(0).get("TOT_DISTRIAMT"), Types.VARCHAR),
					new DataValue(opNo, Types.VARCHAR),
					new DataValue(sDate, Types.VARCHAR),
					new DataValue(sTime, Types.VARCHAR),
			};
			ib.addValues(insValue);
			this.addProcessData(new DataProcessBean(ib));
		}
	}
	
	private void updateTaskSum(DCP_ProcessTaskProcessReq req,List<Map<String, Object>> getQData,List<Map<String, Object>> getQDataSum) throws Exception{
		String eId = req.geteId();
		String shopId = req.getShopId();
		String opNo = req.getOpNO();
		String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
		String sTime = new SimpleDateFormat("HHmmss").format(new Date());
		String sum_processTaskSumNo =getQDataSum.get(0).get("PROCESSTASKSUMNO").toString();
		//String sum_totCqty = getQDataSum.get(0).get("TOT_CQTY").toString();
		//String sum_totPqty = getQDataSum.get(0).get("TOT_PQTY").toString();
		//String sum_totAmt = getQDataSum.get(0).get("TOT_AMT").toString();
		//String sum_totDistriAmt = getQDataSum.get(0).get("TOT_DISTRIAMT").toString();
		
		BigDecimal totCqty_b = new BigDecimal(getQDataSum.get(0).get("TOT_CQTY").toString());
		BigDecimal totPqty_b = new BigDecimal(getQDataSum.get(0).get("TOT_PQTY").toString());
		BigDecimal totAmt_b = new BigDecimal(getQDataSum.get(0).get("TOT_AMT").toString());
		BigDecimal totDistriAmt_b = new BigDecimal(getQDataSum.get(0).get("TOT_DISTRIAMT").toString());
		
		///处理单价和金额小数位数  BY JZMA 20200401
		String amtLength = PosPub.getPARA_SMS(dao, eId, shopId, "amtLength");
		String distriAmtLength = PosPub.getPARA_SMS(dao, eId, shopId, "distriAmtLength");
		if (!PosPub.isNumeric(amtLength)) {
			amtLength="2";
		}
		if (!PosPub.isNumeric(distriAmtLength)) {
			distriAmtLength="2";
		}
		
		for (Map<String, Object> oneData : getQData) {
			String pluNo = oneData.get("PLUNO").toString();
			String featureNo = oneData.get("FEATURENO").toString();
			//加工商品是否存在
			Map<String, Object> condiV=new HashMap<String, Object>();
			condiV.put("PLUNO",pluNo);
			condiV.put("FEATURENO",featureNo);
			List<Map<String, Object>> isExist= MapDistinct.getWhereMap(getQDataSum, condiV, false);
			if(isExist!=null && !isExist.isEmpty() ) {
				//修改加工商品
				String pQty = oneData.get("PQTY").toString();
				String pUnit = oneData.get("PUNIT").toString();
				String baseQty = oneData.get("BASEQTY").toString();
				String baseUnit = oneData.get("BASEUNIT").toString();
				BigDecimal pQty_b = new BigDecimal(pQty);
				BigDecimal baseQty_b = new BigDecimal(baseQty);
				
				//录入单位一致性处理
				String sum_price = isExist.get(0).get("PRICE").toString();
				String sum_distriPrice = isExist.get(0).get("DISTRIPRICE").toString();
				String sum_pUnit = isExist.get(0).get("PUNIT").toString();
				String sum_unitRatio = isExist.get(0).get("UNIT_RATIO").toString();
				String sum_udLength = isExist.get(0).get("UDLENGTH").toString();
				
				BigDecimal sum_price_b = new BigDecimal(sum_price);
				BigDecimal sum_distriPrice_b = new BigDecimal(sum_distriPrice);
				BigDecimal sum_unitRatio_b = new BigDecimal(sum_unitRatio);
				BigDecimal sum_udLength_b = new BigDecimal(sum_udLength);
				
				if (!pUnit.equals(sum_pUnit)){
					//  1箱 = 10公斤 转换率10
					//  1瓶 = 15公斤 转换率15
					pQty_b = baseQty_b.divide(sum_unitRatio_b,Integer.parseInt(sum_udLength), RoundingMode.HALF_UP);
				}
				
				BigDecimal amt_b = sum_price_b.multiply(pQty_b).setScale(Integer.parseInt(amtLength),RoundingMode.HALF_UP);
				BigDecimal distriAmt_b = sum_distriPrice_b.multiply(pQty_b).setScale(Integer.parseInt(distriAmtLength),RoundingMode.HALF_UP);
				totPqty_b = totPqty_b.add(pQty_b);
				totAmt_b = totAmt_b.add(amt_b);
				totDistriAmt_b = totDistriAmt_b.add(distriAmt_b);
				
				// 更新
				UptBean ub = new UptBean("DCP_PROCESSTASKSUM_DETAIL");
				// add value
				ub.addUpdateValue("PQTY", new DataValue(pQty_b.toPlainString(), Types.VARCHAR,UpdateSelf));
				ub.addUpdateValue("BASEQTY", new DataValue(baseQty, Types.VARCHAR,UpdateSelf));
				ub.addUpdateValue("AMT", new DataValue(amt_b.toPlainString(), Types.VARCHAR,UpdateSelf));
				ub.addUpdateValue("DISTRIAMT", new DataValue(distriAmt_b.toPlainString(), Types.VARCHAR,UpdateSelf));
				
				// condition
				ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				ub.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
				ub.addCondition("PROCESSTASKSUMNO", new DataValue(sum_processTaskSumNo, Types.VARCHAR));
				ub.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
				ub.addCondition("FEATURENO", new DataValue(featureNo, Types.VARCHAR));
				
				this.addProcessData(new DataProcessBean(ub));
				
				
			} else {
				//新增加工商品
				String[] detailColumns = {
						"EID", "SHOPID", "PROCESSTASKSUMNO",
						"PLUNO", "FEATURENO", "PQTY", "PUNIT", "BASEQTY", "BASEUNIT", "UNIT_RATIO",
						"STOCKIN_QTY", "SCRAP_QTY",
						"PRICE", "DISTRIPRICE", "AMT", "DISTRIAMT"
				};
				InsBean detailIb = new InsBean("DCP_PROCESSTASKSUM_DETAIL", detailColumns);
				DataValue[] detailInsValue = new DataValue[]{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(shopId, Types.VARCHAR),
						new DataValue(sum_processTaskSumNo, Types.VARCHAR),
						new DataValue(oneData.get("PLUNO").toString(), Types.VARCHAR),
						new DataValue(oneData.get("FEATURENO").toString(), Types.VARCHAR),
						new DataValue(oneData.get("PQTY").toString(), Types.VARCHAR),
						new DataValue(oneData.get("PUNIT").toString(), Types.VARCHAR),
						new DataValue(oneData.get("BASEQTY").toString(), Types.VARCHAR),
						new DataValue(oneData.get("BASEUNIT").toString(), Types.VARCHAR),
						new DataValue(oneData.get("UNIT_RATIO").toString(), Types.VARCHAR),
						new DataValue("0", Types.VARCHAR),
						new DataValue("0", Types.VARCHAR),
						new DataValue(oneData.get("PRICE").toString(), Types.VARCHAR),
						new DataValue(oneData.get("DISTRIPRICE").toString(), Types.VARCHAR),
						new DataValue(oneData.get("AMT").toString(), Types.VARCHAR),
						new DataValue(oneData.get("DISTRIAMT").toString(), Types.VARCHAR),
				};
				detailIb.addValues(detailInsValue);
				this.addProcessData(new DataProcessBean(detailIb));
				
				totCqty_b = totCqty_b.add(new BigDecimal("1"));
				totPqty_b = totPqty_b.add(new BigDecimal(oneData.get("PQTY").toString()));
				totAmt_b = totAmt_b.add(new BigDecimal(oneData.get("AMT").toString()));
				totDistriAmt_b = totDistriAmt_b.add(new BigDecimal(oneData.get("DISTRIAMT").toString()));
				
			}
		}
		
		// 更新
		UptBean ub = new UptBean("DCP_PROCESSTASKSUM");
		// add value
		ub.addUpdateValue("TOT_CQTY", new DataValue(totCqty_b.toPlainString(), Types.VARCHAR));
		ub.addUpdateValue("TOT_PQTY", new DataValue(totPqty_b.toPlainString(), Types.VARCHAR));
		ub.addUpdateValue("TOT_AMT", new DataValue(totAmt_b.toPlainString(), Types.VARCHAR));
		ub.addUpdateValue("TOT_DISTRIAMT", new DataValue(totDistriAmt_b.toPlainString(), Types.VARCHAR));
		ub.addUpdateValue("MODIFYBY", new DataValue(opNo, Types.VARCHAR));
		ub.addUpdateValue("MODIFY_DATE", new DataValue(sDate, Types.VARCHAR));
		ub.addUpdateValue("MODIFY_TIME", new DataValue(sTime, Types.VARCHAR));
		
		// condition
		ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		ub.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
		ub.addCondition("PROCESSTASKSUMNO", new DataValue(sum_processTaskSumNo, Types.VARCHAR));
		
		this.addProcessData(new DataProcessBean(ub));
		
	}
	
	
}
