package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import cn.hutool.core.collection.CollectionUtil;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_TransferCreateReq;
import com.dsc.spos.json.cust.req.DCP_TransferCreateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_TransferCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_TransferCreate extends SPosAdvanceService<DCP_TransferCreateReq, DCP_TransferCreateRes>{
	
	@Override
	protected void processDUID(DCP_TransferCreateReq req, DCP_TransferCreateRes res) throws Exception {
		try {
			String eId = req.geteId();
			String shopId = req.getShopId();
			String accountDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
			String sTime = new SimpleDateFormat("HHmmss").format(new Date());
			// 参数是否启用批号==N，库存流水的批号和日期字段不给值
			String isBatchPara = PosPub.getPARA_SMS(dao, eId, "", "Is_BatchNO");
			if (Check.Null(isBatchPara) || !isBatchPara.equals("Y")){
				isBatchPara="N";
			}
			//检查单据是否存在，避免ERP重复调用
			String sql = " select stockoutno from dcp_stockout "
					+ " where eid='"+eId+"' and shopid='"+shopId+"' and load_docno='"+req.getLoadDocNo()+"' ";
			List<Map<String, Object>> getQData = this.doQueryData(sql, null);
			if (!CollectionUtil.isEmpty(getQData)){
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "ERP重复调用,调出门店:"+shopId+" 对应的调拨单已存在(来源单号:"+req.getLoadDocNo()+")");
			}
			String stockOutNo = getStockOutNo(req,eId,shopId,accountDate);
			
			String[] columns = {
					"EID","ORGANIZATIONNO","SHOPID","STOCKOUTNO","BDATE",
					"DOC_TYPE","WAREHOUSE","MEMO","LOAD_DOCTYPE","LOAD_DOCNO",
					"TRANSFER_SHOP","TRANSFER_WAREHOUSE",
					"TOT_CQTY","TOT_PQTY","TOT_AMT","TOT_DISTRIAMT",
					"CREATEBY","CREATE_DATE","CREATE_TIME",
					"STATUS","PROCESS_STATUS","UPDATE_TIME","TRAN_TIME"
			};
			//获取调出门店仓库
			sql = "select out_cost_warehouse from dcp_org where eid='"+eId+"' and organizationno='"+shopId+"'";
			getQData = this.doQueryData(sql, null);
			if (CollectionUtil.isEmpty(getQData)){
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "调出门店:"+shopId+" 在dcp_org表中不存在 ");
			}
			String warehouse = getQData.get(0).get("OUT_COST_WAREHOUSE").toString(); //默认出货成本仓
			
			//获取调入门店仓库
			sql = "select in_cost_warehouse from dcp_org where eid='"+eId+"' and organizationno='"+req.getTransferShop()+"'";
			getQData = this.doQueryData(sql, null);
			if (CollectionUtil.isEmpty(getQData)){
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "调入门店:"+req.getTransferShop()+" 在dcp_org表中不存在 ");
			}
			String transferWarehouse = getQData.get(0).get("IN_COST_WAREHOUSE").toString();   //默认收货成本仓
			
			int totCqty = 0;
			BigDecimal totPqty = new BigDecimal("0");
			BigDecimal totAmt = new BigDecimal("0");
			BigDecimal totDistriAmt = new BigDecimal("0");
			
			String[] columnsDetail = {
					"EID","ORGANIZATIONNO","SHOPID","STOCKOUTNO",
					"ITEM","PLUNO","FEATURENO","BATCH_NO","PROD_DATE",
					"PQTY","PUNIT","BASEQTY","BASEUNIT","UNIT_RATIO",
					"PRICE","AMT","DISTRIPRICE","DISTRIAMT",
					"BDATE","WAREHOUSE","STOCKQTY"
			};
			
			for (level1Elm par : req.getDatas()) {
				String featureNo = par.getFeatureNo();
				String batchNo = par.getBatchNo();
				String prodDate = par.getProdDate();
				
				totPqty = totPqty.add(new BigDecimal(par.getPqty()));
				totAmt = totAmt.add(new BigDecimal(par.getAmt()));
				totDistriAmt = totDistriAmt.add(new BigDecimal(par.getDistriAmt()));
				
				if (Check.Null(featureNo)){
					featureNo = " ";
				}
				if (isBatchPara.equals("N")){
					batchNo = "";
					prodDate = "";
				}
    
				
				//【ID1031948】【饰一派3.0】门店1004调拨单DBCK2023031500001点确定报错  by jinzma 20230317
				//检查ERP下发的资料是否异常
				sql = " select pluno,plutype,baseunit from dcp_goods where eid='"+eId+"' and pluno='"+par.getPluNo()+"' and status='100' ";
				List<Map<String, Object>> checkPlu = this.doQueryData(sql, null);
				if (CollectionUtil.isEmpty(checkPlu)) {
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "商品:" + par.getPluNo() + " 在dcp_goods表中不存在或已失效 ");
				}else {
					String pluType = checkPlu.get(0).get("PLUTYPE").toString();
					String baseUnit = checkPlu.get(0).get("BASEUNIT").toString();
					
					if (pluType.equals("FEATURE") && featureNo.equals(" ")){
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "商品:" + par.getPluNo() + " 是特征码商品但是未给特征值 ");
					}
					if (!pluType.equals("FEATURE") && !featureNo.equals(" ")){
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "商品:" + par.getPluNo() + " 是普通商品但是传入了特征值: "+featureNo);
					}
					if (!baseUnit.equals(par.getBaseUnit())){
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "商品:" + par.getPluNo() + " 传入的基准单位: "+par.getBaseUnit()+"和商品资料表的基准单位: "+baseUnit+"不一致 ");
					}
				}
				
				DataValue[] insValue = new DataValue[]{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(shopId, Types.VARCHAR),
						new DataValue(shopId, Types.VARCHAR),
						new DataValue(stockOutNo, Types.VARCHAR),
						new DataValue(par.getItem(), Types.VARCHAR),
						new DataValue(par.getPluNo(), Types.VARCHAR),
						new DataValue(featureNo, Types.VARCHAR),
						new DataValue(batchNo, Types.VARCHAR),
						new DataValue(prodDate, Types.VARCHAR),
						new DataValue(par.getPqty(), Types.VARCHAR),
						new DataValue(par.getPunit(), Types.VARCHAR),
						new DataValue(par.getBaseQty(), Types.VARCHAR),
						new DataValue(par.getBaseUnit(), Types.VARCHAR),
						new DataValue(par.getUnitRatio(), Types.VARCHAR),
						new DataValue(par.getPrice(), Types.VARCHAR),
						new DataValue(par.getAmt(), Types.VARCHAR),
						new DataValue(par.getDistriPrice(), Types.VARCHAR),
						new DataValue(par.getDistriAmt(), Types.VARCHAR),
						new DataValue(accountDate, Types.VARCHAR),   //BDATE
						new DataValue(warehouse, Types.VARCHAR),
						new DataValue("999999", Types.VARCHAR)  //ERP下发的调拨单库存特殊处理
				};
				
				InsBean ib = new InsBean("DCP_STOCKOUT_DETAIL", columnsDetail);
				ib.addValues(insValue);
				this.addProcessData(new DataProcessBean(ib));
				
				totCqty++;
				
			}
			
			//DCP_STOCKOUT
			DataValue[] insValue = new DataValue[] {
					new DataValue(eId, Types.VARCHAR),
					new DataValue(shopId, Types.VARCHAR),
					new DataValue(shopId, Types.VARCHAR),
					new DataValue(stockOutNo, Types.VARCHAR),
					new DataValue(accountDate, Types.VARCHAR),
					new DataValue("1", Types.VARCHAR),
					new DataValue(warehouse, Types.VARCHAR),
					new DataValue(req.getMemo(), Types.VARCHAR),
					new DataValue(req.getLoadDocType(), Types.VARCHAR),
					new DataValue(req.getLoadDocNo(), Types.VARCHAR),
					new DataValue(req.getTransferShop(), Types.VARCHAR),
					new DataValue(transferWarehouse, Types.VARCHAR),
					new DataValue(totCqty, Types.VARCHAR),
					new DataValue(totPqty.toString(), Types.VARCHAR),
					new DataValue(totAmt.toString(), Types.VARCHAR),
					new DataValue(totDistriAmt.toString(), Types.VARCHAR),
					new DataValue("admin", Types.VARCHAR),
					new DataValue(accountDate, Types.VARCHAR),
					new DataValue(sTime, Types.VARCHAR),
					new DataValue("0", Types.VARCHAR),
					new DataValue("N", Types.VARCHAR),
					new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
					new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
			};
			
			InsBean ib = new InsBean("DCP_STOCKOUT", columns);
			ib.addValues(insValue);
			this.addProcessData(new DataProcessBean(ib));
			
			this.doExecuteDataToDB();
			
			
			res.setDatas(new ArrayList<>());
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
			
		} catch (Exception e) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500,e.getMessage());
		}
	}
	
	private String getStockOutNo(DCP_TransferCreateReq req,String eId,String shopId,String accountDate) throws Exception {
		
		String stockOutNo =  "DBCK" + accountDate;
		String sql = " select max(stockoutno) as stockoutno from dcp_stockout"
				+ " where eid='"+eId+"' and shopid='"+shopId+"' and stockoutno like '%"+stockOutNo+"%' ";
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		stockOutNo = getQData.get(0).get("STOCKOUTNO").toString();
		if (Check.Null(stockOutNo)){
			stockOutNo =  "DBCK" + accountDate + "00001";
		}else{
			stockOutNo = stockOutNo.substring(4);
			long i = Long.parseLong(stockOutNo) + 1;
			stockOutNo = i + "";
			stockOutNo = "DBCK" + stockOutNo;
		}
		return stockOutNo;
	}
	
	@Override
	protected List<InsBean> prepareInsertData(DCP_TransferCreateReq req) throws Exception {
		return null;
	}
	
	@Override
	protected List<UptBean> prepareUpdateData(DCP_TransferCreateReq req) throws Exception {
		return null;
	}
	
	@Override
	protected List<DelBean> prepareDeleteData(DCP_TransferCreateReq req) throws Exception {
		return null;
	}
	
	@Override
	protected boolean isVerifyFail(DCP_TransferCreateReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();
		
		if (Check.Null(req.geteId())) {
			errMsg.append("企业编号不可为空值, ");
			isFail = true;
		}
		if (Check.Null(req.getShopId())) {
			errMsg.append("门店编号不可为空值, ");
			isFail = true;
		}
		if (Check.Null(req.getTransferShop())) {
			errMsg.append("调入门店不可为空值, ");
			isFail = true;
		}
		if (req.getDatas() == null){
			errMsg.append("调拨单商品明细不可为空值, ");
			isFail = true;
		}
		
		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		
		for (level1Elm par:req.getDatas()) {
			
			if (Check.Null(par.getItem())) {
				errMsg.append("项次不可为空值, ");
				isFail = true;
			}
			if (Check.Null(par.getPluNo())) {
				errMsg.append("商品编码不可为空值, ");
				isFail = true;
			}
			if (Check.Null(par.getPunit())) {
				errMsg.append("包装单位不可为空值, ");
				isFail = true;
			}
			if (Check.Null(par.getPqty())) {
				errMsg.append("包装数量不可为空值, ");
				isFail = true;
			}else{
				if (!PosPub.isNumericType(par.getPqty())){
					errMsg.append("包装数量必须为数值, ");
					isFail = true;
				}
			}
			if (Check.Null(par.getBaseUnit())) {
				errMsg.append("基本单位不可为空值, ");
				isFail = true;
			}
			if (Check.Null(par.getBaseQty())) {
				errMsg.append("基本数量不可为空值, ");
				isFail = true;
			}else{
				if (!PosPub.isNumericType(par.getBaseQty())){
					errMsg.append("基本数量必须为数值, ");
					isFail = true;
				}
			}
			if (Check.Null(par.getUnitRatio())) {
				errMsg.append("单位转化率不可为空值, ");
				isFail = true;
			}
			if (Check.Null(par.getPrice())) {
				errMsg.append("单价不可为空值, ");
				isFail = true;
			}else{
				if (!PosPub.isNumericType(par.getPrice())){
					errMsg.append("单价必须为数值, ");
					isFail = true;
				}
			}
			if (Check.Null(par.getAmt())) {
				errMsg.append("金额不可为空值, ");
				isFail = true;
			}else{
				if (!PosPub.isNumericType(par.getAmt())){
					errMsg.append("金额必须为数值, ");
					isFail = true;
				}
			}
			if (Check.Null(par.getDistriPrice())) {
				errMsg.append("进货单价不可为空值, ");
				isFail = true;
			}else{
				if (!PosPub.isNumericType(par.getDistriPrice())){
					errMsg.append("进货单价必须为数值, ");
					isFail = true;
				}
			}
			if (Check.Null(par.getDistriAmt())) {
				errMsg.append("进货金额不可为空值, ");
				isFail = true;
			}else{
				if (!PosPub.isNumericType(par.getDistriAmt())){
					errMsg.append("进货金额必须为数值, ");
					isFail = true;
				}
			}
			
			if (isFail) {
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}
		}
		
		return false;
	}
	
	@Override
	protected TypeToken<DCP_TransferCreateReq> getRequestType() {
		return new TypeToken<DCP_TransferCreateReq>(){};
	}
	
	@Override
	protected DCP_TransferCreateRes getResponseType() {
		return new DCP_TransferCreateRes();
	}
}
