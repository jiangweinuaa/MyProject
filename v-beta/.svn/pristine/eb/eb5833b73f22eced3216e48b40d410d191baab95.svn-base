package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;

import com.dsc.spos.json.JsonBasicRes;
import com.dsc.spos.json.cust.req.DCP_InterSettleDataGenerateReq;
import com.dsc.spos.json.cust.req.DCP_POrderCreateReq;
import com.dsc.spos.json.cust.req.DCP_POrderStatusUpdateReq;
import com.dsc.spos.json.cust.res.DCP_InterSettleDataGenerateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.utils.ServiceAgentUtils;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 
 * @date 2024-09-19
 * @author 01029
 */
public class DCP_POrderStatusUpdate extends SPosAdvanceService<DCP_POrderStatusUpdateReq, JsonBasicRes> {

	@Override
	protected void processDUID(DCP_POrderStatusUpdateReq req, JsonBasicRes res) throws Exception {

		//try {
		String isMustPtemplate=PosPub.getPARA_SMS(dao, req.geteId(), req.getOrganizationNO(), "Is_Must_PTemplate");
		String eId = req.geteId();

		String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		String sql = null;
		String oprType = req.getRequest().getOprType();
		if(req.getRequest().getConfirmList()==null){
			req.getRequest().setConfirmList(new ArrayList<>());
		}
		Calendar cal = Calendar.getInstance();//获得当前时间
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String modifyDate = df.format(cal.getTime());
		df = new SimpleDateFormat("HHmmss");
		String modifyTime = df.format(cal.getTime());

		StringBuffer errMsg = new StringBuffer("");

		String billNo = req.getRequest().getPOrderNo();
		sql = " SELECT a.* ,b.OPTIONAL_TIME " +
				"from  DCP_PORDER a left join DCP_PTEMPLATE b on a.eid=b.eid "
						+" and a.PTEMPLATENO=b.PTEMPLATENO and b.DOC_TYPE ='0' WHERE a.EID='%s'   AND a.PORDERNO='%s'   ";
		sql = String.format(sql, req.geteId(), billNo);
		List<Map<String, Object>> mDatasQry = this.doQueryData(sql, null);
		if (SUtil.EmptyList(mDatasQry)) {
			errMsg.append("单号:" + billNo + "不存在！");
			res.setSuccess(false);
			res.setServiceStatus("000");
			res.setServiceDescription(errMsg.toString());
			return;
					 
		}


		String orderStatus = mDatasQry.get(0).get("STATUS").toString();
		String supplierType = mDatasQry.get(0).get("SUPPLIERTYPE").toString();
		String mDate =new SimpleDateFormat("yyyyMMdd").format(new Date());
		String mTime = new SimpleDateFormat("HHmmss").format(new Date());
		if (("cancel".equals(oprType))){
			if (!"0".equals( mDatasQry.get(0).get("STATUS").toString())){
				errMsg.append("单据状态非【0-新建】不可作废！");
			}
		}
		if ( ("submit".equals(oprType))){
			if (!"0".equals( mDatasQry.get(0).get("STATUS").toString())){
				errMsg.append("单据状态非【0-新建】不可提交！");
			}
			if("Y".equals(isMustPtemplate)){
				if ("".equals( mDatasQry.get(0).get("PTEMPLATENO").toString())){
					errMsg.append("要货模板不为空！");
				}
			}
			if(Check.NotNull(mDatasQry.get(0).get("OPTIONAL_TIME").toString())) {
				if (mTime.compareTo(mDatasQry.get(0).get("OPTIONAL_TIME").toString()) > 0) {
					errMsg.append("系统时间>订货截止时间【OPTIONAL_TIME】不可提交！");
				}
			}
		}
		if ( ("unsubmit".equals(oprType))){
					//if ("".equals( mDatasQry.get(0).get("PTEMPLATENO").toString())){
					//	errMsg.append("要货模板不为空！");
					//}
					
			if (!"2".equals( mDatasQry.get(0).get("STATUS").toString())){
				errMsg.append("单据状态非【2-已提交】不可撤销！");
			}
		}

		if(Check.NotNull(errMsg.toString())){
			res.setSuccess(false);
			res.setServiceStatus("000");
			res.setServiceDescription(errMsg.toString() + "服务执行异常");
			return;
		}


		//审核  追加
		if("confirm".equals(oprType)||"confirm and send".equals(oprType)){
			if(CollUtil.isNotEmpty(req.getRequest().getConfirmList())){
				List<DCP_POrderStatusUpdateReq.Detail1> collect = req.getRequest().getConfirmList().stream().filter(x -> "Y".equals(x.getIsAdditional())).collect(Collectors.toList());
				if(CollUtil.isNotEmpty(collect)){
					String detailSql="select * from dcp_porder_detail where a.eid='"+req.geteId()+"' " +
							" and a.porderno='"+req.getRequest().getPOrderNo()+"' ";
					List<Map<String, Object>> detailDatasQry = this.doQueryData(detailSql, null);

					collect.forEach(x->{
						if(Check.Null(x.getDistriPrice())){
							x.setDistriPrice("0");
						}
						if(Check.Null(x.getPrice())){
							x.setPrice("0");
						}
						x.setDistriAmt(new BigDecimal(x.getDistriPrice()).multiply(new BigDecimal(x.getPQty())).toString());
						x.setAmt(new BigDecimal(x.getPrice()).multiply(new BigDecimal(x.getPQty())).toString());
					});
					Map<String, Object> stringObjectMap = mDatasQry.get(0);

					Integer detailItem = detailDatasQry.stream().map(x -> Integer.valueOf(x.get("ITEM").toString())).distinct().collect(Collectors.toList()).stream()
							.max(Integer::compare).orElse(0);

					List<String> detailCollect = detailDatasQry.stream().map(x -> x.get("PLUNO").toString() + "|" + x.get("FEATURENO").toString()).distinct().collect(Collectors.toList());


					BigDecimal totPqty=stringObjectMap.get("TOTPQTY")==null?new BigDecimal("0"):new BigDecimal(stringObjectMap.get("TOTPQTY").toString());
					BigDecimal totAmt=stringObjectMap.get("TOTAMT")==null?new BigDecimal("0"):new BigDecimal(stringObjectMap.get("TOTAMT").toString());
					BigDecimal totDistriAmt=stringObjectMap.get("TOTDISTRIPQTY")==null?new BigDecimal("0"):new BigDecimal(stringObjectMap.get("TOTDISTRIPQTY").toString());
					BigDecimal totCqty=stringObjectMap.get("TOTCQTY")==null?new BigDecimal("0"):new BigDecimal(stringObjectMap.get("TOTCQTY").toString());

					for(DCP_POrderStatusUpdateReq.Detail1 x:req.getRequest().getConfirmList()){
						detailItem++;

						totPqty.add(new BigDecimal(x.getPQty()));
						totAmt.add(new BigDecimal(x.getAmt()));
						totDistriAmt.add(new BigDecimal(x.getDistriAmt()));

						if(!detailCollect.contains(x.getPluNo()+"|"+x.getFeatureNo())){
							detailCollect.add(x.getPluNo()+"|"+x.getFeatureNo());
						}

						Map<String, Object> baseMap = PosPub.getBaseQty(dao, eId, x.getPluNo(), x.getPUnit(), x.getPQty());

						com.dsc.spos.utils.ColumnDataValue dcp_porder_detail = new ColumnDataValue();
						dcp_porder_detail.add("porderNO", DataValues.newString(req.getRequest().getPOrderNo()));
						dcp_porder_detail.add("SHOPID", DataValues.newString(stringObjectMap.get("SHOPID").toString()));
						dcp_porder_detail.add("item", DataValues.newString(detailItem));
						dcp_porder_detail.add("pluNO", DataValues.newString(x.getPluNo()));
						dcp_porder_detail.add("punit", DataValues.newString(x.getPUnit()));
						dcp_porder_detail.add("pqty", DataValues.newString(x.getPQty()));
						dcp_porder_detail.add("BASEUNIT", DataValues.newString(baseMap.get("baseUnit").toString()));
						dcp_porder_detail.add("BASEQTY", DataValues.newString(baseMap.get("baseQty").toString()));
						dcp_porder_detail.add("unit_Ratio", DataValues.newString(baseMap.get("unitRatio").toString()));
						dcp_porder_detail.add("price", DataValues.newString(x.getPrice()));
						dcp_porder_detail.add("amt", DataValues.newString(x.getAmt()));
						dcp_porder_detail.add("EID", DataValues.newString(eId));
						dcp_porder_detail.add("organizationNO", DataValues.newString(stringObjectMap.get("ORGANIZATIONNO").toString()));
						dcp_porder_detail.add("Detail_status", DataValues.newString("0"));
						dcp_porder_detail.add("REF_WQTY", DataValues.newString("0"));
						dcp_porder_detail.add("REF_SQTY", DataValues.newString("0"));
						dcp_porder_detail.add("REF_PQTY", DataValues.newString("0"));
						dcp_porder_detail.add("SO_QTY", DataValues.newString("0"));
						dcp_porder_detail.add("MUL_QTY", DataValues.newString("0"));
						dcp_porder_detail.add("MIN_QTY", DataValues.newString("0"));
						dcp_porder_detail.add("MAX_QTY", DataValues.newString("0"));
						dcp_porder_detail.add("propQty", DataValues.newString(""));
						dcp_porder_detail.add("MEMO", DataValues.newString("要货督审添加"));
						dcp_porder_detail.add("KQTY", DataValues.newString("0"));
						dcp_porder_detail.add("KADJQTY", DataValues.newString("0"));
						dcp_porder_detail.add("PROPADJQTY", DataValues.newString("0"));
						dcp_porder_detail.add("DISTRIPRICE", DataValues.newString(x.getDistriPrice()));
						dcp_porder_detail.add("DISTRIAMT", DataValues.newString(x.getDistriAmt()));
						dcp_porder_detail.add("BDATE", DataValues.newString(stringObjectMap.get("BDATE").toString()));
						dcp_porder_detail.add("HEADSTOCKQTY", DataValues.newString("0"));
						dcp_porder_detail.add("FEATURENO", DataValues.newString(x.getFeatureNo()));
						dcp_porder_detail.add("UDISTRIPRICE", DataValues.newString(""));
						dcp_porder_detail.add("ISNEWGOODS", DataValues.newString("N"));
						dcp_porder_detail.add("ISHOTGOODS", DataValues.newString("N"));
						dcp_porder_detail.add("SUPPLIERTYPE", DataValues.newString("FACTORY"));
						dcp_porder_detail.add("SUPPLIERID", DataValues.newString(req.getOrganizationNO()));

						this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_PORDER_DETAIL", dcp_porder_detail)));


					}

					totCqty=new BigDecimal(detailCollect.size());

					//更新要货单
					UptBean ub1 = new UptBean("DCP_PORDER");
					ub1.addUpdateValue("MODIFY_CHATUSERID", new DataValue(req.getChatUserId(), Types.VARCHAR));
					ub1.addUpdateValue("modifyBy", new DataValue(req.getOpNO(), Types.VARCHAR));
					ub1.addUpdateValue("modify_Date", new DataValue(modifyDate, Types.VARCHAR));
					ub1.addUpdateValue("modify_Time", new DataValue(modifyTime, Types.VARCHAR));
					ub1.addUpdateValue("tot_Pqty", new DataValue(totPqty, Types.VARCHAR));
					ub1.addUpdateValue("tot_Amt", new DataValue(totAmt, Types.VARCHAR));
					ub1.addUpdateValue("TOT_DISTRIAMT", new DataValue(totDistriAmt, Types.VARCHAR));
					// 更新原进货金额合计

					ub1.addUpdateValue("tot_Cqty", new DataValue(totCqty, Types.VARCHAR));
					ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

					ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

					// condition
					ub1.addCondition("OrganizationNO", new DataValue(stringObjectMap.get("ORGANIZATIONNO").toString(), Types.VARCHAR));
					ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					ub1.addCondition("SHOPID", new DataValue(stringObjectMap.get("SHOPID").toString(), Types.VARCHAR));
					ub1.addCondition("porderNO", new DataValue(req.getRequest().getPOrderNo(), Types.VARCHAR));

					this.addProcessData(new DataProcessBean(ub1));

					//先更新要货单
					this.doExecuteDataToDB();

					//调用内部结算

					//判断收货组织法人<>供货组织法人才需要生成内部结算
					Map<String, String> corpData = PosPub.getCorpByOrgNo(stringObjectMap.get("ORGANIZATIONNO").toString(), stringObjectMap.get("RECEIPT_ORG").toString());
					if (!StringUtils.equals(corpData.get(stringObjectMap.get("ORGANIZATIONNO").toString()),
							corpData.get(stringObjectMap.get("RECEIPT_ORG").toString()))) {

						detailDatasQry = this.doQueryData(detailSql, null);

						DCP_InterSettleDataGenerateReq generateReq = new DCP_InterSettleDataGenerateReq();
						generateReq.setRequest(generateReq.new Request());
						generateReq.setToken(req.getToken());
						generateReq.seteId(req.geteId());
						generateReq.setServiceId("DCP_InterSettleDataGenerate");
						generateReq.getRequest().setOrganizationNo(stringObjectMap.get("ORGANIZATIONNO").toString());
						generateReq.getRequest().setBillNo(req.getRequest().getPOrderNo());
						generateReq.getRequest().setSupplyOrgNo(stringObjectMap.get("RECEIPT_ORG").toString());
						generateReq.getRequest().setDetail(new ArrayList<>());
						generateReq.getRequest().setReturnSupplyPrice("Y");
						generateReq.getRequest().setBillType(DCP_InterSettleDataGenerate.BillType.BillType10000.getType());
						ServiceAgentUtils<DCP_InterSettleDataGenerateReq, DCP_InterSettleDataGenerateRes> agentUtils = new ServiceAgentUtils<>();

						for (Map<String, Object> detail:detailDatasQry){
							DCP_InterSettleDataGenerateReq.Detail genDetail = generateReq.new Detail();
							generateReq.getRequest().getDetail().add(genDetail);
							genDetail.setReceiveOrgNo(detail.get("ORGANIZATIONNO").toString());
							genDetail.setItem(detail.get("ITEM").toString());
							genDetail.setPluNo(detail.get("PLUNO").toString());
							genDetail.setFeatureNo(StringUtils.toString(detail.get("FEATURENO").toString(), " "));
							genDetail.setPUnit(detail.get("PUNIT").toString());
							genDetail.setPQty(detail.get("PQTY").toString());
							genDetail.setReceivePrice(detail.get("DISTRIPRICE").toString());
							genDetail.setReceiveAmt(detail.get("DISTRIAMT").toString());
							generateReq.getRequest().getDetail().add(genDetail);
						}

						DCP_InterSettleDataGenerateRes dcp_interSettleDataGenerateRes = agentUtils.agentService(generateReq, new TypeToken<DCP_InterSettleDataGenerateRes>() {
						});

						if(dcp_interSettleDataGenerateRes.isSuccess()){
							List<DCP_InterSettleDataGenerateRes.SupPriceDetail> supPriceDetail = dcp_interSettleDataGenerateRes.getSupPriceDetail();
							if(supPriceDetail.size()>0){
								for(DCP_InterSettleDataGenerateRes.SupPriceDetail supd:supPriceDetail) {
									String item = supd.getItem();
									String receivePrice = supd.getReceivePrice();
									List<Map<String, Object>> itemFilterRows = detailDatasQry.stream().filter(x -> x.get("ITEM").toString().equals(item)).collect(Collectors.toList());

									if(CollUtil.isNotEmpty(itemFilterRows)){
										BigDecimal pQty = new BigDecimal(itemFilterRows.get(0).get("PQTY").toString());
										BigDecimal receiveAmt = pQty.multiply(new BigDecimal(receivePrice));

										UptBean ub12 = new UptBean("DCP_PORDER_DETAIL");
										ub12.addUpdateValue("DISTRIPRICE", DataValues.newString(receivePrice));
										ub12.addUpdateValue("DISTRIAMT",DataValues.newString(receiveAmt));

										ub12.addCondition("EID", DataValues.newString(eId));
										ub12.addCondition("PORDERNO",DataValues.newString(req.getRequest().getPOrderNo()));
										ub12.addCondition("ITEM",DataValues.newString(item));
										this.addProcessData(new DataProcessBean(ub12));
									}
								}
							}
							this.doExecuteDataToDB();
						}else{
							res.setSuccess(true);
							res.setServiceStatus("000");
							res.setServiceDescription("内部交易结算执行失败");
						}

						//if (!agentUtils.agentServiceSuccess(generateReq, new TypeToken<DCP_InterSettleDataGenerateRes>() {

						//})) {
						//    res.setSuccess(true);
						//    res.setServiceStatus("000");
						//   res.setServiceDescription("内部交易结算执行失败");
						// }
					}

				}
			}
		}


		if ("cancel".equals(oprType)) {
			UptBean ub1 = new UptBean("DCP_PORDER");

			ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			ub1.addCondition("PORDERNO", new DataValue(billNo, Types.VARCHAR));
			ub1.addUpdateValue("STATUS", new DataValue("7", Types.VARCHAR));
			ub1.addUpdateValue("CANCELBY", new DataValue(req.getEmployeeNo(), Types.VARCHAR));
			ub1.addUpdateValue("CANCEL_DATE", new DataValue(mDate, Types.VARCHAR));
			ub1.addUpdateValue("CANCEL_TIME", new DataValue(mTime, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(ub1));
			this.doExecuteDataToDB();
		}
		else if ("submit".equals(oprType)){
			UptBean ub1 = new UptBean("DCP_PORDER");

			ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			ub1.addCondition("PORDERNO", new DataValue(billNo, Types.VARCHAR));
			ub1.addUpdateValue("STATUS", new DataValue("2", Types.VARCHAR));
			ub1.addUpdateValue("SUBMITBY", new DataValue(req.getEmployeeNo(), Types.VARCHAR));
			ub1.addUpdateValue("SUBMIT_DATE", new DataValue(mDate, Types.VARCHAR));
			ub1.addUpdateValue("SUBMIT_TIME", new DataValue(mTime, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(ub1));

			//生成底稿
			this.confirmOPorder(req,mDatasQry);

			this.doExecuteDataToDB();
		}
		else if ("unsubmit".equals(oprType)){
			UptBean ub1 = new UptBean("DCP_PORDER");

			ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			ub1.addCondition("PORDERNO", new DataValue(billNo, Types.VARCHAR));
			ub1.addUpdateValue("STATUS", new DataValue("0", Types.VARCHAR));
			ub1.addUpdateValue("SUBMITBY", new DataValue("", Types.VARCHAR));
			ub1.addUpdateValue("SUBMIT_DATE", new DataValue("", Types.VARCHAR));
			ub1.addUpdateValue("SUBMIT_TIME", new DataValue("", Types.VARCHAR));
			ub1.addUpdateValue("MODIFYBY", new DataValue(req.getEmployeeNo(), Types.VARCHAR));
			ub1.addUpdateValue("MODIFY_DATE", new DataValue(mDate, Types.VARCHAR));
			ub1.addUpdateValue("MODIFY_TIME", new DataValue(mTime, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(ub1));

			DelBean db1 = new DelBean("DCP_DEMAND");
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db1.addCondition("ORGANIZATIONNO", new DataValue(req.getOrganizationNO(), Types.VARCHAR));
			db1.addCondition("ORDERNO", new DataValue(billNo, Types.VARCHAR));
			//db1.addCondition("ORDERTYPE", new DataValue("1", Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));

			this.doExecuteDataToDB();
		}
		else if("confirm".equals(oprType)) {
			if (!orderStatus.equals("2")) {
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "要货申请单据状态非【已提交】不可核准！");
			}
			List<DCP_POrderStatusUpdateReq.Detail1> confirmList = req.getRequest().getConfirmList();


					//查询明细
			String detailSql = "select a.*,u1.UNITRATIO as punitratio,u2.UNITRATIO as wunitratio,u3.udlength as pudlength,u4.udlength as wudlength," +
					" o1.IN_COST_WAREHOUSE,o1.OUT_COST_WAREHOUSE,g.wunit  from " +
					" dcp_porder_detail a" +
					" left join dcp_porder b on a.eid=b.eid and a.organizationno=b.organizationno and a.porderno=b.porderno " +
					" inner join dcp_goods g on a.pluno=g.pluno and a.eid=g.eid " +
					" left join DCP_GOODS_UNIT u1 on u1.eid=a.eid and u1.pluno=a.pluno and u1.ounit=a.punit and u1.unit=a.baseunit" +
					" left join DCP_GOODS_UNIT u2 on u2.eid=a.eid and u2.pluno=a.pluno and u2.ounit=g.wunit and u2.unit=a.baseunit" +
					" left join DCP_UNIT u3 on u3.eid=a.eid and   u3.unit=a.punit" +
					" left join DCP_UNIT u4 on u4.eid=a.eid and   u4.unit=g.wunit" +
					" left join dcp_org o1 on o1.eid=a.eid and o1.organizationno=b.organizationno " +
					" where a.eid='" + eId + "'  and a.porderno='" + billNo + "' ";
			List<Map<String, Object>> detailDatas = this.doQueryData(detailSql, null);
			for (Map<String, Object> detailData : detailDatas) {
				String detailItem = detailData.get("ITEM").toString();
				String detailPluNo = detailData.get("PLUNO").toString();
				String detailFeatureNo = detailData.get("FEATURENO").toString();
				String detailPUnit = detailData.get("PUNIT").toString();
				BigDecimal detailPQty = new BigDecimal(detailData.get("PQTY").toString());
				List<DCP_POrderStatusUpdateReq.Detail1> validCollect = confirmList.stream().filter(x -> x.getItem().equals(detailItem)).collect(Collectors.toList());
				if (CollUtil.isNotEmpty(validCollect)) {
					DCP_POrderStatusUpdateReq.Detail1 singleDetail = validCollect.get(0);
					if (!singleDetail.getPluNo().equals(detailPluNo)) {
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "要货申请明细【" + detailItem + "】与审核【" + singleDetail.getItem() + "】品号不匹配！");
					}

					if (!singleDetail.getFeatureNo().equals(detailFeatureNo)) {
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "要货申请明细【" + detailItem + "】与审核【" + singleDetail.getItem() + "】特征码不匹配！");
					}

					if (!singleDetail.getPUnit().equals(detailPUnit)) {
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "要货申请明细【" + detailItem + "】与审核【" + singleDetail.getItem() + "】单位不匹配！");
					}
					if (detailPQty.compareTo(new BigDecimal(singleDetail.getPQty())) != 0) {
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "要货申请明细【" + detailItem + "】与审核【" + singleDetail.getItem() + "】数量不匹配！");
					}
				}
			}

			this.cjPOrder(req);

			//return;
		}
		else if("unconfirm".equals(oprType)){
			if(!(orderStatus.equals("6")||orderStatus.equals("9"))){
				errMsg.append("要货申请单据状态非【已审核】或【部分审核】不可撤回核准！");
			}

					//查询需求底稿
			String demandSql="select * from DCP_DEMAND a where a.eid='"+eId+"' and a.organizationno='"+req.getOrganizationNO()+"' " +
					" and a.ordertype='1' and a.orderno='"+billNo+"' and a.DISTRISTATUS !='00'";
			List<Map<String, Object>> demandDatas = this.doQueryData(demandSql, null);
			List<Map<String, Object>> distriDmands = demandDatas.stream().filter(x -> x.get("DISTRI_STATUS") != "00").collect(Collectors.toList());
			if(CollUtil.isNotEmpty(distriDmands)){
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500,"需求底稿状态非【未分配】不可驳回！");
			}
			UptBean ub5 = new UptBean("DCP_PORDER_DETAIL");
			ub5.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			ub5.addCondition("PORDERNO", new DataValue(billNo, Types.VARCHAR));
			ub5.addUpdateValue("DETAIL_STATUS", new DataValue("0", Types.VARCHAR));
			ub5.addUpdateValue("REVIEW_QTY",new DataValue("0",Types.VARCHAR));
			this.addProcessData(new DataProcessBean(ub5));

			UptBean ub6 = new UptBean("DCP_PORDER");
			ub6.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			ub6.addCondition("PORDERNO", new DataValue(billNo, Types.VARCHAR));

			ub6.addUpdateValue("STATUS", new DataValue("2", Types.VARCHAR));
			ub6.addUpdateValue("CONFIRMBY", new DataValue("", Types.VARCHAR));
			ub6.addUpdateValue("CONFIRM_DATE", new DataValue("", Types.VARCHAR));
			ub6.addUpdateValue("CONFIRM_TIME", new DataValue("", Types.VARCHAR));
			ub6.addUpdateValue("MODIFYBY", new DataValue(req.getEmployeeNo(), Types.VARCHAR));
			ub6.addUpdateValue("MODIFY_DATE", new DataValue(mDate, Types.VARCHAR));
			ub6.addUpdateValue("MODIFY_TIME", new DataValue(mTime, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(ub6));



					//if("SUPPLIER".equals(supplierType)){

					//	UptBean ub7 = new UptBean("DCP_PORDER");
					//	ub7.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					//	ub7.addCondition("PORDERNO", new DataValue(billNo, Types.VARCHAR));
					//	ub7.addUpdateValue("SUBMITBY", new DataValue("", Types.VARCHAR));
					//	ub7.addUpdateValue("SUBMIT_DATE", new DataValue("", Types.VARCHAR));
					//	ub7.addUpdateValue("SUBMIT_TIME", new DataValue("", Types.VARCHAR));
					//	ub7.addUpdateValue("STATUS", new DataValue("0", Types.VARCHAR));

					//	this.addProcessData(new DataProcessBean(ub7));

					//}
			this.doExecuteDataToDB();
			//return;
		}
		else if("reject".equals(oprType)){
			//要货申请单据状态非【2-已提交】不可驳回！
			if(!orderStatus.equals("2")){
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "单据状态非【已提交】不可驳回！");
			}
			this.cjPOrder(req);

			return;
		}



		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription(errMsg.toString() + "服务执行成功");

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_POrderStatusUpdateReq req) throws Exception {

		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_POrderStatusUpdateReq req) throws Exception {

		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_POrderStatusUpdateReq req) throws Exception {

		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_POrderStatusUpdateReq req) throws Exception {

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
 

		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_POrderStatusUpdateReq> getRequestType() {

		return new TypeToken<DCP_POrderStatusUpdateReq>() {
		};
	}

	@Override
	protected JsonBasicRes getResponseType() {

		return new JsonBasicRes();
	}

	private void confirmOPorder(DCP_POrderStatusUpdateReq req,List<Map<String, Object>> mDatasQry) throws Exception{
		String eId=req.geteId();
		String mDate =new SimpleDateFormat("yyyyMMdd").format(new Date());
		String mTime = new SimpleDateFormat("HHmmss").format(new Date());
		String billNo = req.getRequest().getPOrderNo();
		Map<String, Object> singleMap = mDatasQry.get(0);
		String orderStatus = singleMap.get("STATUS").toString();
		String organizationNo = singleMap.get("ORGANIZATIONNO").toString();
		String employeeID = singleMap.get("EMPLOYEEID").toString();
		String departID = singleMap.get("DEPARTID").toString();
		String rDate = singleMap.get("RDATE").toString();
		String receipt_org = singleMap.get("RECEIPT_ORG").toString();
		String pTemplateNo = singleMap.get("PTEMPLATENO").toString();


		//查询所有组织
		String orgSql="select * from dcp_org where eid='"+req.geteId()+"'";
		List<Map<String, Object>> allOrgDatas = this.doQueryData(orgSql, null);
		//查询明细
		String detailSql="select a.*,u1.UNITRATIO as punitratio,u2.UNITRATIO as wunitratio,u3.udlength as pudlength,u4.udlength as wudlength," +
					" o1.IN_COST_WAREHOUSE,o1.OUT_COST_WAREHOUSE,g.wunit  from " +
					" dcp_porder_detail a" +
					" left join dcp_porder b on a.eid=b.eid and a.organizationno=b.organizationno and a.porderno=b.porderno " +
					" inner join dcp_goods g on a.pluno=g.pluno and a.eid=g.eid " +
					" left join DCP_GOODS_UNIT u1 on u1.eid=a.eid and u1.pluno=a.pluno and u1.ounit=a.punit and u1.unit=a.baseunit" +
					" left join DCP_GOODS_UNIT u2 on u2.eid=a.eid and u2.pluno=a.pluno and u2.ounit=g.wunit and u2.unit=a.baseunit" +
					" left join DCP_UNIT u3 on u3.eid=a.eid and   u3.unit=a.punit" +
					" left join DCP_UNIT u4 on u4.eid=a.eid and   u4.unit=g.wunit" +
					" left join dcp_org o1 on o1.eid=a.eid and o1.organizationno=b.organizationno "+
					" where a.eid='"+eId+"' and a.organizationno='"+organizationNo+"' and a.porderno='"+billNo+"' ";
		List<Map<String, Object>> detailDatas = this.doQueryData(detailSql, null);

		String bDate = singleMap.get("BDATE").toString();
		String isUrgent = singleMap.get("ISURGENTORDER").toString();
		String submitTime = singleMap.get("SUBMIT_TIME").toString();
		String isMustAllot="";
		String rank="";
		String templateShopSql="select * from DCP_PTEMPLATE_SHOP where eid='"+eId+"' and PTEMPLATENO='"+pTemplateNo+"'";
		List<Map<String, Object>> templateShopDatas = this.doQueryData(templateShopSql, null);
		List<Map<String, Object>> ps = templateShopDatas.stream().filter(x -> x.get("SHOPID").toString().equals(organizationNo)).collect(Collectors.toList());
		if(CollUtil.isNotEmpty(ps)){
			isMustAllot=ps.get(0).get("ISMUSTALLOT").toString();
			rank=ps.get(0).get("SORTID").toString();
		}

		String purTemplateSql="select b.item,b.pluno,b.SUPPLIER,c.PURTYPE,c.PURCENTER,a.RECEIPT_ORG " +
					"from DCP_PTEMPLATE a " +
					"inner join DCP_PTEMPLATE_detail b on a.eid = b.eid and a.PTEMPLATENO = b.PTEMPLATENO " +
					"left join DCP_PURCHASETEMPLATE c on b.eid=c.EID and b.SUPPLIER=c.SUPPLIERNO and a.RECEIPT_ORG=c.PURCENTER " +
					"left join DCP_PURCHASETEMPLATE_goods d on c.eid=d.eid and c.PURTEMPLATENO=d.PURTEMPLATENO and d.PLUNO=b.PLUNO " +
					"where a.eid = '"+eId+"' " +
					"  and a.DOC_TYPE = '0' " +
					"  and a.SUPPLIERTYPE = 'SUPPLIER'" +
					"and b.STATUS='100' " ;
		if(Check.NotNull(pTemplateNo)){
			purTemplateSql+=" and a.PTEMPLATENO='"+pTemplateNo+"'";
		}
		purTemplateSql+=" order by item ";

		List<Map<String, Object>> purTemplateDatas = this.doQueryData(purTemplateSql, null);

		for (Map<String, Object> detailData : detailDatas) {
			String detailItem = detailData.get("ITEM").toString();
			String detailPluNo = detailData.get("PLUNO").toString();
			String detailPluBarCode = detailData.get("PLUBARCODE").toString();
			String detailFeatureNo = detailData.get("FEATURENO").toString();
			String detailPUnit = detailData.get("PUNIT").toString();
			String detailBaseUnit = detailData.get("BASEUNIT").toString();
			String detailOrgNo = detailData.get("ORGANIZATIONNO").toString();
			BigDecimal detailPQty = new BigDecimal(detailData.get("PQTY").toString());
			String detailWUnit = detailData.get("WUNIT").toString();
			String detailSupplierType = detailData.get("SUPPLIERTYPE").toString();
			String detailSupplierID = detailData.get("SUPPLIERID").toString();//统配的时候这边是组织
			BigDecimal pUnitRatio = new BigDecimal(detailData.get("PUNITRATIO").toString());
			BigDecimal wUnitRatio = new BigDecimal(detailData.get("WUNITRATIO").toString());
			int pUdLength = Integer.parseInt(detailData.get("PUDLENGTH").toString());
			int wUdLength = Integer.parseInt(detailData.get("WUDLENGTH").toString());
			String inCostWarehouse = detailData.get("IN_COST_WAREHOUSE").toString();
			String outCostWarehouse = detailData.get("OUT_COST_WAREHOUSE").toString();
				//生成需求底稿
			com.dsc.spos.utils.ColumnDataValue demandColumns=new com.dsc.spos.utils.ColumnDataValue();
			demandColumns.add("EID",eId,Types.VARCHAR);
			demandColumns.add("ORGANIZATIONNO",organizationNo,Types.VARCHAR);
			demandColumns.add("BDATE",bDate,Types.VARCHAR);
			demandColumns.add("ORDERTYPE","1",Types.VARCHAR);
			demandColumns.add("ORDERNO",billNo,Types.VARCHAR);
			demandColumns.add("ITEM",detailItem,Types.VARCHAR);
			demandColumns.add("PLUBARCODE",detailPluBarCode,Types.VARCHAR);
			demandColumns.add("PLUNO",detailPluNo,Types.VARCHAR);
			demandColumns.add("FEATURENO",detailFeatureNo,Types.VARCHAR);
			demandColumns.add("PUNIT",detailPUnit,Types.VARCHAR);
			demandColumns.add("POQTY",detailPQty,Types.VARCHAR);
			BigDecimal pQty=detailPQty;
			BigDecimal baseQty=BigDecimal.ZERO;
			BigDecimal wQty=BigDecimal.ZERO;
			baseQty=pQty.multiply(pUnitRatio).setScale(pUdLength, RoundingMode.HALF_UP);
			wQty=baseQty.divide(wUnitRatio, wUdLength, RoundingMode.HALF_UP);

			demandColumns.add("PQTY",pQty,Types.VARCHAR);
			demandColumns.add("BASEUNIT",detailBaseUnit,Types.VARCHAR);
			demandColumns.add("BASEQTY",baseQty.toString(),Types.VARCHAR);
			demandColumns.add("WUNIT",detailWUnit,Types.VARCHAR);
			demandColumns.add("WQTY",wQty.toString(),Types.VARCHAR);
			demandColumns.add("RDATE",rDate,Types.VARCHAR);
			demandColumns.add("OBJECTTYPE","1",Types.VARCHAR);
			demandColumns.add("OBJECTID",detailOrgNo,Types.VARCHAR);
			demandColumns.add("EMPLOYEEID",employeeID,Types.VARCHAR);
			demandColumns.add("DEPARTID",departID,Types.VARCHAR);
			demandColumns.add("RECEIPTWAREHOUSE",inCostWarehouse,Types.VARCHAR);
			demandColumns.add("SUPPLIERTYPE",detailSupplierType,Types.VARCHAR);
			if(detailSupplierType.equals("SUPPLIER")){
				demandColumns.add("SUPPLIER",detailSupplierID,Types.VARCHAR);
				Stream<Map<String, Object>> mapStream = purTemplateDatas.stream().filter(x -> x.get("PLUNO").toString().equals(detailPluNo) &&
						x.get("RECEIPT_ORG").toString().equals(receipt_org) &&
						x.get("SUPPLIER").toString().equals(detailSupplierID));
				List<Map<String, Object>> templateColl = mapStream.collect(Collectors.toList());
				if(CollUtil.isNotEmpty(templateColl)){
					String purCenter = templateColl.get(0).get("PURCENTER").toString();
					String purType = templateColl.get(0).get("PURTYPE").toString().toString();

					demandColumns.add("PURCENTER",purCenter,Types.VARCHAR);
					demandColumns.add("PURTYPE",purType,Types.VARCHAR);
					demandColumns.add("DELIVERYORGNO",purCenter,Types.VARCHAR);

					if("0".equals(purType)||"1".equals(purType)){
						demandColumns.add("DELIVERYWAREHOUSE","",Types.VARCHAR);
					}else{
						List<Map<String, Object>> deliveryOrgS = allOrgDatas.stream().filter(x -> x.get("ORGANIZATIONNO").toString().equals(purCenter)).collect(Collectors.toList());
						if(deliveryOrgS.size()>0){
							demandColumns.add("DELIVERYWAREHOUSE",deliveryOrgS.get(0).get("OUT_COST_WAREHOUSE").toString(),Types.VARCHAR);
						}
					}
				}
			}
			else if(detailSupplierType.equals("FACTORY")){
				demandColumns.add("DELIVERYORGNO",detailSupplierID,Types.VARCHAR);
				List<Map<String, Object>> deliveryOrgS = allOrgDatas.stream().filter(x -> x.get("ORGANIZATIONNO").toString().equals(detailSupplierID)).collect(Collectors.toList());
				if(deliveryOrgS.size()>0){
					demandColumns.add("DELIVERYWAREHOUSE",deliveryOrgS.get(0).get("OUT_COST_WAREHOUSE").toString(),Types.VARCHAR);
				}
			}

			demandColumns.add("STOCKOUTNOQTY","0",Types.VARCHAR);
			demandColumns.add("PURQTY","0",Types.VARCHAR);
			demandColumns.add("STOCKINQTY","0",Types.VARCHAR);
			demandColumns.add("STOCKOUTQTY","0",Types.VARCHAR);
			demandColumns.add("CLOSESTATUS","0",Types.VARCHAR);
			demandColumns.add("DISTRISTATUS","00",Types.VARCHAR);
			demandColumns.add("ISURGENT",isUrgent,Types.VARCHAR);
			demandColumns.add("SUBMITTIME",submitTime,Types.VARCHAR);
			demandColumns.add("TEMPLATENO",pTemplateNo,Types.VARCHAR);
			demandColumns.add("ISMUSTALLOT",isMustAllot,Types.VARCHAR);
			demandColumns.add("RANK",rank,Types.VARCHAR);

			String[] mainColumnNames = demandColumns.getColumns().toArray(new String[0]);
			DataValue[] mainDataValues = demandColumns.getDataValues().toArray(new DataValue[0]);
			InsBean ib1=new InsBean("DCP_DEMAND",mainColumnNames);
			ib1.addValues(mainDataValues);
			this.addProcessData(new DataProcessBean(ib1));
		}


	}

	private void cjPOrder(DCP_POrderStatusUpdateReq req) throws Exception {
		String oprType = req.getRequest().getOprType();
		String mDate =new SimpleDateFormat("yyyyMMdd").format(new Date());
		String mTime = new SimpleDateFormat("HHmmss").format(new Date());
		List<DCP_POrderStatusUpdateReq.Detail1> confirmList = req.getRequest().getConfirmList();
		// 需求底稿PQTY=核准量，需求底稿状态STATUS=1-已核准
		//● 要货申请REVIEW_QTY=核准量，要货单状态=6-已审核，明细行状态status（根据核准量判断，>0更新1-已核准，=0或null更新2-已驳回）

		String detailSql="select a.* from " +
				" dcp_porder_detail a" +
				" where a.eid='"+req.geteId()+"'  and a.porderno='"+req.getRequest().getPOrderNo()+"' ";
		List<Map<String, Object>> detailDatas = this.doQueryData(detailSql, null);

		if(CollUtil.isNotEmpty(confirmList)) {

			List<String> confirmItems = confirmList.stream().map(x -> x.getItem()).distinct().collect(Collectors.toList());
			List<Map<String, Object>> otherItems = detailDatas.stream().filter(x -> !confirmItems.contains(x.get("ITEM").toString())).collect(Collectors.toList());

			List detailStatusList = new ArrayList();
			for (Map<String, Object> detail : otherItems) {
				String detailStatus = detail.get("DETAIL_STATUS").toString();
				if (!detailStatusList.contains(detailStatus)) {
					detailStatusList.add(detailStatus);
				}
			}
			for (DCP_POrderStatusUpdateReq.Detail1 detail : confirmList) {
				UptBean ub5 = new UptBean("DCP_DEMAND");
				ub5.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
				ub5.addCondition("ORDERNO", new DataValue(req.getRequest().getPOrderNo(), Types.VARCHAR));
				ub5.addCondition("ITEM", new DataValue(detail.getItem(), Types.VARCHAR));

				BigDecimal reviewQtyDecimal = new BigDecimal(Check.Null(detail.getReviewQty()) ? "0" : detail.getReviewQty());

				if("confirm".equals(oprType)&&reviewQtyDecimal.compareTo(BigDecimal.ZERO)>0) {
					ub5.addUpdateValue("STATUS", new DataValue("1", Types.VARCHAR));
					ub5.addUpdateValue("PQTY", new DataValue(detail.getReviewQty(), Types.VARCHAR));
				}else{
					ub5.addUpdateValue("STATUS", new DataValue("2", Types.VARCHAR));
				}

				if("reject".equals(oprType)){
					ub5.addUpdateValue("STATUS", new DataValue("2", Types.VARCHAR));
				}

				this.addProcessData(new DataProcessBean(ub5));


				UptBean ub6 = new UptBean("DCP_PORDER_DETAIL");
				ub6.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
				ub6.addCondition("PORDERNO", new DataValue(req.getRequest().getPOrderNo(), Types.VARCHAR));
				ub6.addCondition("ITEM", new DataValue(detail.getItem(), Types.VARCHAR));

				if("confirm".equals(oprType)&&reviewQtyDecimal.compareTo(BigDecimal.ZERO)>0) {
					ub6.addUpdateValue("DETAIL_STATUS", new DataValue("1", Types.VARCHAR));
					ub6.addUpdateValue("REVIEW_QTY", new DataValue(detail.getReviewQty(), Types.VARCHAR));
				}else{
					ub6.addUpdateValue("DETAIL_STATUS", new DataValue("2", Types.VARCHAR));
				}
				if("reject".equals(oprType)){
					ub6.addUpdateValue("DETAIL_STATUS", new DataValue("2", Types.VARCHAR));
				}

				ub6.addUpdateValue("REASON", new DataValue(detail.getReason(), Types.VARCHAR));


				this.addProcessData(new DataProcessBean(ub6));

				if("confirm".equals(oprType)&&reviewQtyDecimal.compareTo(BigDecimal.ZERO)>0) {
					if (!detailStatusList.contains("1")) {
						detailStatusList.add("1");
					}
				}else{
					if (!detailStatusList.contains("2")) {
						detailStatusList.add("2");
					}
				}
				if("reject".equals(oprType)) {
					if (!detailStatusList.contains("2")) {
						detailStatusList.add("2");
					}
				}
			}

			UptBean ub6 = new UptBean("DCP_PORDER");
			ub6.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
			ub6.addCondition("PORDERNO", new DataValue(req.getRequest().getPOrderNo(), Types.VARCHAR));
			ub6.addUpdateValue("CONFIRMBY", new DataValue(req.getEmployeeNo(), Types.VARCHAR));
			ub6.addUpdateValue("CONFIRM_DATE", new DataValue(mDate, Types.VARCHAR));
			ub6.addUpdateValue("CONFIRM_TIME", new DataValue(mTime, Types.VARCHAR));
			ub6.addUpdateValue("MODIFYBY", new DataValue(req.getEmployeeNo(), Types.VARCHAR));
			ub6.addUpdateValue("MODIFY_DATE", new DataValue(mDate, Types.VARCHAR));
			ub6.addUpdateValue("MODIFY_TIME", new DataValue(mTime, Types.VARCHAR));
			if(detailStatusList.size()==1){
				String singleStatus = detailStatusList.get(0).toString();
				if(singleStatus.equals("1")){
					ub6.addUpdateValue("STATUS", new DataValue("6", Types.VARCHAR));
				}else{
					ub6.addUpdateValue("STATUS", new DataValue("5", Types.VARCHAR));
				}
			}
			else if(detailStatusList.size()>1){
				ub6.addUpdateValue("STATUS", new DataValue("9", Types.VARCHAR));
			}
			ub6.addUpdateValue("REASON", new DataValue(req.getRequest().getReason(), Types.VARCHAR));

			this.addProcessData(new DataProcessBean(ub6));
		}
		else{
			//整单审核  或者驳回
			if("confirm".equals(oprType)){
				for (Map<String, Object> detail : detailDatas){
					String item = detail.get("ITEM").toString();
					String pQty = detail.get("PQTY").toString();
					UptBean ub5 = new UptBean("DCP_DEMAND");
					ub5.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
					ub5.addCondition("ORDERNO", new DataValue(req.getRequest().getPOrderNo(), Types.VARCHAR));
					ub5.addCondition("ITEM", new DataValue(item, Types.VARCHAR));
					ub5.addUpdateValue("STATUS", new DataValue("1", Types.VARCHAR));
					ub5.addUpdateValue("PQTY", new DataValue(pQty, Types.VARCHAR));
					this.addProcessData(new DataProcessBean(ub5));


					UptBean ub6 = new UptBean("DCP_PORDER_DETAIL");
					ub6.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
					ub6.addCondition("PORDERNO", new DataValue(req.getRequest().getPOrderNo(), Types.VARCHAR));
					ub6.addCondition("ITEM", new DataValue(item, Types.VARCHAR));
					ub6.addUpdateValue("DETAIL_STATUS", new DataValue("1", Types.VARCHAR));
					ub6.addUpdateValue("REVIEW_QTY", new DataValue(pQty, Types.VARCHAR));
					this.addProcessData(new DataProcessBean(ub6));
				}
			}
			if("reject".equals(oprType)){
				for (Map<String, Object> detail : detailDatas){
					String item = detail.get("ITEM").toString();

					UptBean ub6 = new UptBean("DCP_PORDER_DETAIL");
					ub6.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
					ub6.addCondition("PORDERNO", new DataValue(req.getRequest().getPOrderNo(), Types.VARCHAR));
					ub6.addCondition("ITEM", new DataValue(item, Types.VARCHAR));
					ub6.addUpdateValue("REASON", new DataValue(req.getRequest().getReason(), Types.VARCHAR));
					ub6.addUpdateValue("DETAIL_STATUS", new DataValue("2", Types.VARCHAR));
					this.addProcessData(new DataProcessBean(ub6));
				}
			}

			UptBean ub6 = new UptBean("DCP_PORDER");
			ub6.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
			ub6.addCondition("PORDERNO", new DataValue(req.getRequest().getPOrderNo(), Types.VARCHAR));
			ub6.addUpdateValue("CONFIRMBY", new DataValue(req.getEmployeeNo(), Types.VARCHAR));
			ub6.addUpdateValue("CONFIRM_DATE", new DataValue(mDate, Types.VARCHAR));
			ub6.addUpdateValue("CONFIRM_TIME", new DataValue(mTime, Types.VARCHAR));
			ub6.addUpdateValue("MODIFYBY", new DataValue(req.getEmployeeNo(), Types.VARCHAR));
			ub6.addUpdateValue("MODIFY_DATE", new DataValue(mDate, Types.VARCHAR));
			ub6.addUpdateValue("MODIFY_TIME", new DataValue(mTime, Types.VARCHAR));
			if("confirm".equals(oprType)){
				ub6.addUpdateValue("REASON", new DataValue(req.getRequest().getReason(), Types.VARCHAR));
				ub6.addUpdateValue("STATUS", new DataValue("6", Types.VARCHAR));
			}
		    if("reject".equals(oprType)){
				ub6.addUpdateValue("REASON", new DataValue(req.getRequest().getReason(), Types.VARCHAR));
				ub6.addUpdateValue("STATUS", new DataValue("5", Types.VARCHAR));
	    	}

			this.addProcessData(new DataProcessBean(ub6));

		}

		this.doExecuteDataToDB();



	}

}
