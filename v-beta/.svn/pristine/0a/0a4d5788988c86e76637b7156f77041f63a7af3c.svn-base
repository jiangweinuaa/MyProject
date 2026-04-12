package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.ExecBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.ProcedureBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_LStockOutErpUpdateReq;
import com.dsc.spos.json.cust.req.DCP_LStockOutErpUpdateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_LStockOutErpUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_LStockOutErpUpdate extends SPosAdvanceService<DCP_LStockOutErpUpdateReq, DCP_LStockOutErpUpdateRes> {
	@Override
	protected void processDUID(DCP_LStockOutErpUpdateReq req, DCP_LStockOutErpUpdateRes res) throws Exception {
		String eId = req.geteId();
		String shopId = req.getShopId();
		String lstockOutNO = req.getLstockOutNO();
		String opType = req.getOpType(); // 2.整单收货 3.部分收货 4.整单未收
		
		String sdate=new SimpleDateFormat("yyyyMMdd").format(new Date());
		String stime=new SimpleDateFormat("HHmmss").format(new Date());
		try {
			//获取报损单明细商品
			String sql = this.getDCP_LStockOut_Sql(req);
			List<Map<String, Object>> getQData = this.doQueryData(sql, null);
			if (getQData != null && !getQData.isEmpty()) {
				String status = getQData.get(0).get("STATUS").toString();
				//判断下，是不是已经更新过了，防止ERP 重复调用  //0-新建     1-待过账    2-待签收   3-已作废   4-全部签收 5-部分签收  6-全部驳回
				if (status.equals("2")) {
					///处理单价和金额小数位数  BY JZMA 20200401
					String amtLength = PosPub.getPARA_SMS(dao, eId, shopId, "amtLength");
					String distriAmtLength = PosPub.getPARA_SMS(dao, eId, shopId, "distriAmtLength");
					if (Check.Null(amtLength)||!PosPub.isNumeric(amtLength)) {
						amtLength="2";
					}
					if (Check.Null(distriAmtLength)||!PosPub.isNumeric(distriAmtLength)) {
						distriAmtLength="2";
					}
					int amtLengthInt = Integer.parseInt(amtLength);
					int distriAmtLengthInt = Integer.parseInt(distriAmtLength);
					
					String accountDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
					String Enable_InTransit = PosPub.getPARA_SMS(dao, eId, shopId, "Enable_InTransit");
					if (Check.Null(Enable_InTransit)|| !Enable_InTransit.equals("Y")) {
						Enable_InTransit = "N";
					}
					String invWareHouse ="";//门店在途仓
					//启用在途的，必须检查在途仓是否存在
					if(Enable_InTransit.equals("Y")) {
						sql = " select inv_cost_warehouse from dcp_org "
								+ " where status='100' and eid='"+eId+"' and organizationno='"+shopId+"' ";
						List<Map<String, Object>> getQDataInvWareHouse = this.doQueryData(sql, null);
						if(getQDataInvWareHouse !=null && !getQDataInvWareHouse.isEmpty()) {
							invWareHouse  =getQDataInvWareHouse.get(0).get("INV_COST_WAREHOUSE").toString();
						}
					}
					
					//报损单单据状态处理
					//opType: 2.整单收货   3.部分收货    4.整单未收
					//status: 4-全部签收   5-部分签收    6-全部驳回
					if (opType.equals("2")) {
						status = "4";
					}
					if (opType.equals("3")) {
						status = "5";
					}
					if (opType.equals("4")) {
						status="6";
					}
					
					//变更报损单状态
					UptBean stockoutUpdate = new UptBean("DCP_LSTOCKOUT");
					stockoutUpdate.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
					stockoutUpdate.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
					// condition
					stockoutUpdate.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					stockoutUpdate.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
					stockoutUpdate.addCondition("LSTOCKOUTNO", new DataValue(lstockOutNO, Types.VARCHAR));
					this.addProcessData(new DataProcessBean(stockoutUpdate));
					
					//启用在途的，先把在途的扣减掉 
					if (Enable_InTransit.equals("Y") && !Check.Null(invWareHouse) ){
						for (Map<String, Object> oneData : getQData) {
							String item=oneData.get("ITEM").toString();
							String pluno=oneData.get("PLUNO").toString();
							String pqty=oneData.get("PQTY").toString();
							String punit=oneData.get("PUNIT").toString();
							String baseQty=oneData.get("BASEQTY").toString();
							String baseUnit=oneData.get("BASEUNIT").toString();
							String unitRatio=oneData.get("UNIT_RATIO").toString();
							String price= oneData.get("PRICE").toString();
							String amt=oneData.get("AMT").toString();
							String batchNO=oneData.get("BATCH_NO").toString();
							String prodDate=oneData.get("PROD_DATE").toString();
							String distriPrice = oneData.getOrDefault("DISTRIPRICE", "0").toString();
							String distriAmt = oneData.getOrDefault("DISTRIAMT", "0").toString();
							String featureNo=oneData.get("FEATURENO").toString();
							if (Check.Null(featureNo)) {
								featureNo = " ";
							}
							String bdate = oneData.get("BDATE").toString();
							
							//减少在途仓库存
							String procedure="SP_DCP_StockChange";
							Map<Integer,Object> inputParameter = new HashMap<>();
							inputParameter.put(1,eId);              //--企业ID
							inputParameter.put(2,shopId);           //--组织
							inputParameter.put(3,"13");             //--单据类型
							inputParameter.put(4,lstockOutNO);	    //--单据号
							inputParameter.put(5,item);             //--单据行号
							inputParameter.put(6,"-1");             //--异动方向 1=加库存 -1=减库存
							inputParameter.put(7,bdate);            //--营业日期 yyyy-MM-dd
							inputParameter.put(8,pluno);            //--品号
							inputParameter.put(9,featureNo);        //--特征码
							inputParameter.put(10,invWareHouse);    //--仓库	
							inputParameter.put(11,batchNO);         //--批号
							inputParameter.put(12,punit);           //--交易单位
							inputParameter.put(13,pqty);            //--交易数量
							inputParameter.put(14,baseUnit);        //--基准单位
							inputParameter.put(15,baseQty);         //--基准数量	
							inputParameter.put(16,unitRatio);       //--换算比例	
							inputParameter.put(17,price);           //--零售价
							inputParameter.put(18,amt);             //--零售金额
							inputParameter.put(19,distriPrice);     //--进货价
							inputParameter.put(20,distriAmt);       //--进货金额
							inputParameter.put(21,accountDate);     //--入账日期 yyyy-MM-dd
							inputParameter.put(22,prodDate);        //--批号的生产日期 yyyy-MM-dd
							inputParameter.put(23,bdate);           //--单据日期
							inputParameter.put(24,"");              //--异动原因
							inputParameter.put(25,"报损单ERP更新");  //--异动描述
							inputParameter.put(26,"admin");              //--操作员
							
							ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
							this.addProcessData(new DataProcessBean(pdb));
							
						}
					}
					
					//2.全部收货  回写原单的签收数量 
					if (opType.equals("2")) {
						sql = " UPDATE DCP_LSTOCKOUT_DETAIL SET RQTY=PQTY "
								+ " WHERE EID = '"+eId +"'    "
								+ " AND SHOPID= '"+shopId +"'  "
								+ " AND LSTOCKOUTNO= '"+lstockOutNO +"'  " ;
						ExecBean eb = new ExecBean("DCP_LSTOCKOUT_DETAIL");
						eb.setExecsql(sql);
						this.addProcessData(new DataProcessBean(eb));
					}
					
					
					
					//3.部分收货 或全部拒收 
					if (opType.equals("3")||opType.equals("4")) {
						String adjustno=getAdjustNo(req);		//取库存调整单单号	
						BigDecimal totPqty= new BigDecimal("0");
						BigDecimal totAmt= new BigDecimal("0");
						BigDecimal totDistriAmt= new BigDecimal("0");
						int totCqty=1;
						String warehouse ="";
						for (Map<String, Object> oneData : getQData) {
							String item=oneData.get("ITEM").toString();
							String pluno=oneData.get("PLUNO").toString();
							String pqty=oneData.get("PQTY").toString();
							String punit=oneData.get("PUNIT").toString();
							String baseQty=oneData.get("BASEQTY").toString();
							String baseUnit=oneData.get("BASEUNIT").toString();
							String unitRatio=oneData.get("UNIT_RATIO").toString();
							String price= oneData.get("PRICE").toString();
							String batchNO=oneData.get("BATCH_NO").toString();
							String prodDate=oneData.get("PROD_DATE").toString();
							String distriPrice = oneData.getOrDefault("DISTRIPRICE", "0").toString();
							String featureNo=oneData.get("FEATURENO").toString();
							if (Check.Null(featureNo)) {
								featureNo = " ";
							}
							warehouse=oneData.get("WAREHOUSE").toString();
							
							//ERP签收数量
							String packing_qty="0";
							String base_qty="0";
							
							if (opType.equals("3")) {
								boolean errorPlu=true;
								for (level1Elm par : req.getDatas()) {
									if (par.getItem().equals(item) && par.getPluNO().equals(pluno)) {
										packing_qty = par.getPqty();
										base_qty = par.getBaseQty();
										errorPlu= false;
										break;
									}
								}
								if (errorPlu) {
									throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "报损单更新明细和原单不一致");
								}
							}
							//使用BigDecimal处理，防止出现精度问题
							BigDecimal adjust_pqty= new BigDecimal(pqty).subtract(new BigDecimal(packing_qty));
							BigDecimal adjust_baseQty= new BigDecimal(baseQty).subtract(new BigDecimal(base_qty));
							
							//回写原单的签收数量
							UptBean stockoutdetailUpdate = new UptBean("DCP_LSTOCKOUT_DETAIL");
							stockoutdetailUpdate.addUpdateValue("RQTY", new DataValue(packing_qty, Types.VARCHAR));
							// condition
							stockoutdetailUpdate.addCondition("EID", new DataValue(eId, Types.VARCHAR));
							stockoutdetailUpdate.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
							stockoutdetailUpdate.addCondition("LSTOCKOUTNO", new DataValue(lstockOutNO, Types.VARCHAR));
							stockoutdetailUpdate.addCondition("ITEM", new DataValue(item, Types.VARCHAR));
							this.addProcessData(new DataProcessBean(stockoutdetailUpdate));
							
							if(adjust_pqty.compareTo(BigDecimal.ZERO) !=0 && adjust_baseQty.compareTo(BigDecimal.ZERO) !=0) {
								//增加库存调整单单身
								String[] columnsAdjustDetail ={
										"SHOPID","ADJUSTNO","ITEM","OITEM","PLUNO","PLU_BARCODE","PUNIT","PQTY",
										"BASEUNIT","BASEQTY","UNIT_RATIO",
										"PRICE","AMT","EID","ORGANIZATIONNO","WAREHOUSE",
										"BATCH_NO","PROD_DATE","DISTRIPRICE","DISTRIAMT","BDATE","FEATURENO"
								};
								
								BigDecimal amt = adjust_pqty.multiply(new BigDecimal(price)).setScale(amtLengthInt, RoundingMode.HALF_UP);
								BigDecimal distriAmt = adjust_pqty.multiply(new BigDecimal(distriPrice)).setScale(distriAmtLengthInt, RoundingMode.HALF_UP);
								totAmt = totAmt.add(amt);
								totDistriAmt=totDistriAmt.add(distriAmt);
								totPqty = totPqty.add(adjust_pqty);
								
								DataValue[] insAdjustDetailValue = new DataValue[]{
										new DataValue(shopId, Types.VARCHAR),
										new DataValue(adjustno, Types.VARCHAR),
										new DataValue(totCqty, Types.INTEGER),
										new DataValue(item, Types.INTEGER),
										new DataValue(pluno, Types.VARCHAR),
										new DataValue("", Types.VARCHAR),
										new DataValue(punit, Types.VARCHAR),
										new DataValue(adjust_pqty.toPlainString(), Types.VARCHAR),
										new DataValue(baseUnit, Types.VARCHAR),
										new DataValue(adjust_baseQty.toPlainString(), Types.VARCHAR),
										new DataValue(unitRatio, Types.VARCHAR),
										new DataValue(price, Types.VARCHAR),
										new DataValue(amt.toPlainString(), Types.VARCHAR),
										new DataValue(eId, Types.VARCHAR),
										new DataValue(shopId, Types.VARCHAR),
										new DataValue(warehouse, Types.VARCHAR),
										new DataValue(batchNO, Types.VARCHAR),
										new DataValue(prodDate, Types.VARCHAR),
										new DataValue(distriPrice, Types.VARCHAR),
										new DataValue(distriAmt.toPlainString(), Types.VARCHAR),
										new DataValue(sdate, Types.VARCHAR),
										new DataValue(featureNo, Types.VARCHAR),
								};
								InsBean ibAdjustDetail = new InsBean("DCP_ADJUST_DETAIL", columnsAdjustDetail);
								ibAdjustDetail.addValues(insAdjustDetailValue);
								this.addProcessData(new DataProcessBean(ibAdjustDetail));
								
								String procedure="SP_DCP_StockChange";
								Map<Integer,Object> inputParameter = new HashMap<>();
								inputParameter.put(1,eId);                              //--企业ID
								inputParameter.put(2,shopId);                           //--组织
								inputParameter.put(3,"34");                             //--单据类型
								inputParameter.put(4,adjustno);	                        //--单据号
								inputParameter.put(5,totCqty);                          //--单据行号
								inputParameter.put(6,"1");                              //--异动方向 1=加库存 -1=减库存
								inputParameter.put(7,sdate);                            //--营业日期 yyyy-MM-dd
								inputParameter.put(8,pluno);                            //--品号
								inputParameter.put(9,featureNo);                        //--特征码
								inputParameter.put(10,warehouse);                       //--仓库
								inputParameter.put(11,batchNO);                         //--批号
								inputParameter.put(12,punit);                           //--交易单位
								inputParameter.put(13,adjust_pqty.toPlainString());     //--交易数量
								inputParameter.put(14,baseUnit);                        //--基准单位
								inputParameter.put(15,adjust_baseQty.toPlainString());  //--基准数量
								inputParameter.put(16,unitRatio);                       //--换算比例
								inputParameter.put(17,price);                           //--零售价
								inputParameter.put(18,amt.toPlainString());             //--零售金额
								inputParameter.put(19,distriPrice);                     //--进货价
								inputParameter.put(20,distriAmt.toPlainString());       //--进货金额
								inputParameter.put(21,accountDate);                     //--入账日期 yyyy-MM-dd
								inputParameter.put(22,prodDate);                        //--批号的生产日期 yyyy-MM-dd
								inputParameter.put(23,sdate);                           //--单据日期
								inputParameter.put(24,"");                              //--异动原因
								inputParameter.put(25,"报损单ERP更新");                  //--异动描述
								inputParameter.put(26,"admin");                         //--操作员
								
								ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
								this.addProcessData(new DataProcessBean(pdb));
								
								totCqty++;
							}
						}
						//单身有资料才写单头
						if (totCqty>1) {
							//插入库存调整单单头
							String[] columnsAdjust = {
									"SHOPID","ADJUSTNO","BDATE","MEMO","DOC_TYPE","OTYPE","OFNO","STATUS",
									"CREATEBY","CREATE_DATE","CREATE_TIME",
									"MODIFYBY","MODIFY_DATE","MODIFY_TIME",
									"CONFIRMBY","CONFIRM_DATE","CONFIRM_TIME",
									"ACCOUNTBY","ACCOUNT_DATE","ACCOUNT_TIME",
									"CANCELBY","CANCEL_DATE","CANCEL_TIME",
									"SUBMITBY","SUBMIT_DATE","SUBMIT_TIME",
									"TOT_PQTY","TOT_AMT","TOT_CQTY",
									"LOAD_DOCTYPE","LOAD_DOCNO",
									"EID","ORGANIZATIONNO","WAREHOUSE",
									"PROCESS_STATUS","TOT_DISTRIAMT",
									"UPDATE_TIME","TRAN_TIME"
							};
							DataValue[] insValueAdjust = new DataValue[] {
									new DataValue(shopId, Types.VARCHAR),
									new DataValue(adjustno, Types.VARCHAR),
									new DataValue(sdate, Types.VARCHAR),
									new DataValue("报损库存调整", Types.VARCHAR),
									new DataValue("7", Types.VARCHAR),
									new DataValue("7", Types.VARCHAR),
									new DataValue(lstockOutNO, Types.VARCHAR),
									new DataValue("2", Types.VARCHAR),      //STATUS
									new DataValue("admin", Types.VARCHAR),  //CREATEBY
									new DataValue(sdate, Types.VARCHAR),
									new DataValue(stime, Types.VARCHAR),
									new DataValue("", Types.VARCHAR),       //MODIFYBY
									new DataValue("", Types.VARCHAR),
									new DataValue("", Types.VARCHAR),
									new DataValue("", Types.VARCHAR),        //CONFIRMBY
									new DataValue(sdate, Types.VARCHAR),
									new DataValue(stime, Types.VARCHAR),
									new DataValue("admin", Types.VARCHAR),   //ACCOUNTBY
									new DataValue(accountDate, Types.VARCHAR),
									new DataValue(stime, Types.VARCHAR),
									new DataValue("", Types.VARCHAR),        //CANCELBY
									new DataValue("", Types.VARCHAR),
									new DataValue("", Types.VARCHAR),
									new DataValue("admin", Types.VARCHAR),    //SUBMITBY
									new DataValue(sdate, Types.VARCHAR),
									new DataValue(stime, Types.VARCHAR),
									new DataValue(totPqty.toPlainString(), Types.VARCHAR),    //TOT_PQTY
									new DataValue(totAmt.toPlainString(), Types.VARCHAR),     //TOT_AMT
									new DataValue(totCqty-1, Types.VARCHAR),        //TOT_CQTY 明细条数
									new DataValue("", Types.VARCHAR),
									new DataValue("", Types.VARCHAR),
									new DataValue(eId, Types.VARCHAR),
									new DataValue(shopId, Types.VARCHAR),
									new DataValue(warehouse, Types.VARCHAR),
									new DataValue("Y", Types.VARCHAR),
									new DataValue(totDistriAmt.toPlainString(), Types.VARCHAR),
									new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
									new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
							};
							InsBean  ibAdjust = new InsBean("DCP_ADJUST", columnsAdjust);
							ibAdjust.addValues(insValueAdjust);
							this.addProcessData(new DataProcessBean(ibAdjust)); // 新增單頭
							
						}
					}
				}
				
				this.doExecuteDataToDB();
				
				res.setDatas(new ArrayList<>());
				DCP_LStockOutErpUpdateRes.level1Elm lv1 = new DCP_LStockOutErpUpdateRes().new level1Elm();
				lv1.setDoc_no(lstockOutNO);
				lv1.setOrg_no(shopId);
				res.getDatas().add(lv1);
				
				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");
				
			} else {
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "front_no=" + lstockOutNO +"的单据不存在！");
			}
		} catch (Exception e) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
	}
	
	@Override
	protected List<InsBean> prepareInsertData(DCP_LStockOutErpUpdateReq req) throws Exception {
		return null;
	}
	
	@Override
	protected List<UptBean> prepareUpdateData(DCP_LStockOutErpUpdateReq req) throws Exception {
		return null;
	}
	
	@Override
	protected List<DelBean> prepareDeleteData(DCP_LStockOutErpUpdateReq req) throws Exception {
		return null;
	}
	
	@Override
	protected boolean isVerifyFail(DCP_LStockOutErpUpdateReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();
		String lstockOutNO = req.getLstockOutNO();
		String opType = req.getOpType();
		List<level1Elm> scrapdetail = req.getDatas();
		
		if (Check.Null(lstockOutNO)) {
			errMsg.append("前端单号(front_no)不可为空值, ");
			isFail = true;
		}
		
		if (Check.Null(opType)) {
			errMsg.append("操作类型operation_type不可为空值, ");
			isFail = true;
		} else {
			if (!(opType.equals("2")||opType.equals("3")||opType.equals("4"))) {
				errMsg.append("操作类型operation_type给值错误, ");
				isFail = true;
			}
		}
		
		for (level1Elm par :scrapdetail) {
			String item=par.getItem() ;
			String pluno=par.getPluNO();
			String pqty=par.getPqty();
			String baseQty=par.getBaseQty();
			
			if (Check.Null(item)) {
				errMsg.append("项次(seq)不可为空值, ");
				isFail = true;
			}
			if (Check.Null(pluno)) {
				errMsg.append("商品编号(item_no)不可为空值, ");
				isFail = true;
			}
			if (Check.Null(pqty)) {
				errMsg.append("包装数量(packing_qty)不可为空值, ");
				isFail = true;
			}
			if (Check.Null(baseQty)) {
				errMsg.append("基本单位数量(base_qty)不可为空值, ");
				isFail = true;
			}
			
		}
		
		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return false;
	}
	
	@Override
	protected TypeToken<DCP_LStockOutErpUpdateReq> getRequestType() {
		return new TypeToken<DCP_LStockOutErpUpdateReq>(){};
	}
	
	@Override
	protected DCP_LStockOutErpUpdateRes getResponseType() {
		return new DCP_LStockOutErpUpdateRes();
	}
	
	protected String getDCP_LStockOut_Sql(DCP_LStockOutErpUpdateReq req) {
		String sql =" "
				+ " select a.status,b.item,b.pluno,b.punit,b.pqty,b.baseunit,b.unit_ratio,b.baseqty,b.price,"
				+ " b.amt,a.warehouse,b.batch_no,b.prod_date,b.distriprice,b.distriamt,b.featureno,a.bdate"
				+ " from dcp_lstockout a"
				+ " inner join dcp_lstockout_detail b on a.lstockoutno=b.lstockoutno and a.eid=b.eid"
				+ " and a.organizationno=b.organizationno and a.bdate=b.bdate"
				+ " where a.eid='"+req.geteId()+"' "
				+ " and a.organizationno='"+req.getShopId()+"' "
				+ " and a.lstockoutno='"+req.getLstockOutNO()+"' "
				+ " ";
		return sql;
	}
	
	protected String getAdjustNo(DCP_LStockOutErpUpdateReq req) throws Exception {
		/*
		  库存调整单生成规则：
		  固定编码KCTZ+年月日+5位流水号
		  KCTZ2016120900001
		 */
		String adjustno;
		String sql;
		String shopId = req.getShopId();
		String organizationNO = req.getShopId();
		String eId = req.geteId();
		String bDate= PosPub.getAccountDate_SMS(dao, eId, shopId);
		StringBuffer sqlbuf=new StringBuffer();
		
		String[] conditionValues = {shopId, eId,organizationNO }; // 查询条件
		String ajustnoHead="KCTZ" + bDate;
		sqlbuf.append("select ADJUSTNO from (select MAX(ADJUSTNO) ADJUSTNO from DCP_adjust "
				+ " where SHOPID=? "
				+ " and EID=? "
				+ " and organizationno=? "
				+ " and adjustno like '"+ ajustnoHead+"%%')");
		sql=sqlbuf.toString();
		List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);
		if (getQData != null && !getQData.isEmpty()) {
			adjustno = (String) getQData.get(0).get("ADJUSTNO");
			if (adjustno != null && adjustno.length() > 0) {
				long i;
				adjustno = adjustno.substring(4);
				i = Long.parseLong(adjustno) + 1;
				adjustno = i + "";
				adjustno = "KCTZ" + adjustno;
			} else {
				adjustno = "KCTZ" + bDate + "00001";
			}
		} else {
			adjustno = "KCTZ" + bDate + "00001";
		}
		return adjustno;
	}
	
	
	
}
