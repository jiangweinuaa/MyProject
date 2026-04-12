package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PStockInRefundCreateReq;
import com.dsc.spos.json.cust.req.DCP_PStockInRefundUpdateReq;
import com.dsc.spos.json.cust.req.DCP_PStockInRefundUpdateReq.level2Elm;
import com.dsc.spos.json.cust.req.DCP_PStockInRefundUpdateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_PStockInRefundUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_PStockInRefundUpdate extends SPosAdvanceService<DCP_PStockInRefundUpdateReq, DCP_PStockInRefundUpdateRes> {
	
	private String accountDate="";
	@Override
	protected void processDUID(DCP_PStockInRefundUpdateReq req, DCP_PStockInRefundUpdateRes res) throws Exception {
		String shopId = req.getShopId();
		String organizationNO = req.getOrganizationNO();
		String eId = req.geteId();
		String bDate = req.getRequest().getbDate();
		///新增原料仓 BY JZMA 20181221
		String materialWarehouseNO = req.getRequest().getMaterialWarehouseNo();
		String warehouse = req.getRequest().getWarehouse();
		String modifyBy = req.getOpNO();
		Calendar cal = Calendar.getInstance();//获得当前时间   
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String modifyDate = df.format(cal.getTime());
		df = new SimpleDateFormat("HHmmss");
		String modifyTime = df.format(cal.getTime());
		///完工入库，单据日期新增如果前端没有给值，后端取系统日期  BY JZMA 20200427
		if (Check.Null(bDate))
			bDate=modifyDate;
		String pStockInNO = req.getRequest().getpStockInNo();
		String memo = req.getRequest().getMemo();
		//2018-08-10新增docType
		String docType = req.getRequest().getDocType();
		String pTemplateNO = req.getRequest().getpTemplateNo();
		String totPqty = req.getRequest().getTotPqty();
		String totAmt = req.getRequest().getTotAmt();
		String totCqty = req.getRequest().getTotCqty();
		String totDistriAmt = req.getRequest().getTotDistriAmt();
		
		try {
			if (checkGuid(req)) {
				//删除原有单身
				DelBean db1 = new DelBean("DCP_PSTOCKIN_DETAIL");
				db1.addCondition("pStockInNO", new DataValue(pStockInNO, Types.VARCHAR));
				db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db1.addCondition("organizationNO", new DataValue(organizationNO, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));
				
				//2018-08-10新增以下代码，同理，删除成品明细表中的数据，然后再新增该数据，不做具体的修改操作，完成修改的目的。
				DelBean db2 = new DelBean("DCP_PSTOCKIN_MATERIAL");
				db2.addCondition("pStockInNO", new DataValue(pStockInNO, Types.VARCHAR));
				db2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
				db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db2.addCondition("organizationNO", new DataValue(organizationNO, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db2));

                DelBean db3 = new DelBean("DCP_PSTOCKOUT_BATCH");
                db3.addCondition("pStockInNO", new DataValue(pStockInNO, Types.VARCHAR));
                db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db3.addCondition("organizationNO", new DataValue(organizationNO, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db3));
				
				//新增新的单身（多条记录）
				int mItem = 1;//原料项次自增长，不取前端
                int batchItem=0;
				List<level1Elm> datas = req.getRequest().getDatas();
				for (level1Elm par : datas) {
					int insColCt = 0;
					String[] columnsName = {
							"PSTOCKINNO", "SHOPID", "item", "oItem", "pluNO",
							"punit", "pqty", "BASEUNIT", "BASEQTY", "unit_Ratio",
							"price", "amt", "EID", "organizationNO",
							"task_Qty", "scrap_Qty", "mul_Qty", "bsNO", "WAREHOUSE",
							"BATCH_NO","PROD_DATE","DISTRIPRICE","pqty_origin","pqty_refund",
							"DISTRIAMT","ACCOUNT_DATE","scrap_Qty_origin","scrap_Qty_refund",
							"FEATURENO","MEMO","LOCATION","EXPDATE","DISPTYPE","OOITEM"
					};
					DataValue[] columnsVal = new DataValue[columnsName.length];
					
					for (int i = 0; i < columnsVal.length; i++) {
						String keyVal = null;
						switch (i) {
							case 0:
								keyVal = pStockInNO;
								break;
							case 1:
								keyVal = shopId;
								break;
							case 2:
								keyVal = par.getItem(); //item
								break;
							case 3:
								keyVal = par.getoItem();
								if (!PosPub.isNumeric(keyVal)) {
									keyVal="0";
								}
								break;
							case 4:
								keyVal = par.getPluNo(); //pluNO
								break;
							case 5:
								keyVal = par.getPunit(); //punit
								break;
							case 6:
								keyVal = par.getPqty(); //pqty
								break;
							case 7:
								keyVal = par.getBaseUnit();     //wunit
								break;
							case 8:
								keyVal = par.getBaseQty();   //wqty
								break;
							case 9:
								keyVal = par.getUnitRatio();     //unitRatio
								break;
							case 10:
								keyVal = par.getPrice();    //price
								break;
							case 11:
								keyVal = par.getAmt();    //amt
								break;
							case 12:
								keyVal = eId;
								break;
							case 13:
								keyVal = organizationNO;
								break;
							case 14:
								keyVal = par.getTaskQty(); //taskQty
								if (!PosPub.isNumeric(keyVal)) {
									keyVal="0";
								}
								break;
							case 15:
								keyVal = par.getScrapQty();	//scrapQty
								if(Check.Null(keyVal))
									keyVal = "0";
								break;
							case 16:
								keyVal = par.getMulQty(); //mulQty
								if (!PosPub.isNumeric(keyVal)) {
									keyVal="0";
								}
								break;
							case 17:
								keyVal = par.getBsNo();
								break;
							case 18:
								keyVal = par.getWarehouse();
								break;
							case 19:
								keyVal = par.getBatchNo();
								break;
							case 20:
								keyVal = par.getProdDate();
								break;
							case 21:
								keyVal=par.getDistriPrice();
								if (Check.Null(keyVal))
									keyVal = "0";
								break;
							case 22:
								keyVal = par.getPqty_origin();
								if (Check.Null(keyVal))
									keyVal = "0";
								break;
							case 23:
								keyVal = "0";
								break;
							case 24:
								keyVal = par.getDistriAmt();
								if (Check.Null(keyVal))
									keyVal="0";
								break;
							case 25:
								keyVal = accountDate;
								break;
							case 26:
								keyVal = par.getScrapQty_origin();
								if (Check.Null(keyVal))
									keyVal = "0";
								break;
							case 27:
								keyVal = "0";
								break;
							case 28:
								keyVal = par.getFeatureNo();
								if (Check.Null(keyVal))
									keyVal=" ";
								break;
							case 29:
								keyVal = par.getMemo();
								break;
                            case 30:
                                keyVal = par.getLocation();
                                if(Check.Null(keyVal)){
                                    keyVal=" ";
                                }
                                break;
                            case 31:
                                keyVal = par.getExpDate();
                                break;
                            case 32:
                                keyVal = par.getDispType();
                                break;
                            case 33:
                                keyVal = par.getoOItem();
                                break;
							default:
								break;
						}
						if (keyVal != null) {
							insColCt++;
							if (i == 2 || i == 3){
								columnsVal[i] = new DataValue(keyVal, Types.INTEGER);
							} else {
								columnsVal[i] = new DataValue(keyVal, Types.VARCHAR);
							}
						}
						else {
							columnsVal[i] = null;
						}
					}
					
					String[] columns2  = new String[insColCt];
					DataValue[] insValue2 = new DataValue[insColCt];
					
					insColCt = 0;
					for (int i = 0; i < columnsVal.length; i++){
						if (columnsVal[i] != null){
							columns2[insColCt] = columnsName[i];
							insValue2[insColCt] = columnsVal[i];
							insColCt ++;
							if (insColCt >= insValue2.length) break;
						}
					}
					
					InsBean ib2 = new InsBean("DCP_PSTOCKIN_DETAIL", columns2);
					ib2.addValues(insValue2);
					this.addProcessData(new DataProcessBean(ib2));//增加单身
					
					//新增原料明细（多笔）//加上一个管控，若没有成品明细，直接break;	
					List<level2Elm> material = par.getMaterial();
					if(material == null) {
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品原料为空!!");
					} else {
						for (level2Elm mat : material) {
							int insColCt2 = 0;
							String[] matColumnsName = {
									"MITEM", "ITEM", "WAREHOUSE",
									"PLUNO","PUNIT",
									"PQTY","PRICE","AMT",
									"FINALPRODBASEQTY", "RAWMATERIALBASEQTY",
									"EID","ORGANIZATIONNO","PSTOCKINNO",
									"SHOPID","MPLUNO","BASEUNIT",
									"BASEQTY","UNIT_RATIO",
									"BATCH_NO","PROD_DATE","DISTRIPRICE","DISTRIAMT","ACCOUNT_DATE","ISBUCKLE","FEATURENO"
							};
							DataValue[] matColumnsVal = new DataValue[matColumnsName.length];
							for (int j = 0; j < matColumnsVal.length; j++) {
								String matKeyVal = null;
								switch (j) {
									case 0:
										matKeyVal = par.getItem();
										break;
									case 1:
										matKeyVal = String.valueOf(mItem) ;
										break;
									case 2:
										matKeyVal = materialWarehouseNO;
										break;
									case 3:
										matKeyVal = mat.getMaterial_pluNo();
										break;
									case 4:
										matKeyVal = mat.getMaterial_punit();
										break;
									case 5:
										matKeyVal = mat.getMaterial_pqty();
										break;
									case 6:
										matKeyVal = mat.getMaterial_price();
										break;
									case 7:
										matKeyVal = mat.getMaterial_amt();	//前台给的错误
										if (Check.Null(matKeyVal))
											matKeyVal="0";
										BigDecimal matKeyVal_b = new BigDecimal(matKeyVal);
										//前端给值有问题，红冲都是负数
										if(matKeyVal_b.compareTo(BigDecimal.ZERO)>0) {
											matKeyVal="-"+matKeyVal;
										}
										break;
									case 8:
										matKeyVal = mat.getMaterial_finalProdBaseQty();
										break;
									case 9:
										matKeyVal = mat.getMaterial_rawMaterialBaseQty();
										break;
									case 10:
										matKeyVal = eId;
										break;
									case 11:
										matKeyVal = organizationNO;
										break;
									case 12:
										matKeyVal = pStockInNO;
										break;
									case 13:
										matKeyVal = shopId;
										break;
									case 14:
										matKeyVal = par.getPluNo();
										break;
									case 15:
										matKeyVal = mat.getMaterial_baseUnit();
										break;
									case 16:
										matKeyVal = mat.getMaterial_baseQty();
										break;
									case 17:
										matKeyVal = mat.getMaterial_unitRatio();
										break;
									case 18:
										matKeyVal = mat.getMaterial_batchNo();
										break;
									case 19:
										matKeyVal = mat.getMaterial_prodDate();
										break;
									case 20:
										matKeyVal=mat.getMaterial_distriPrice();
										if (Check.Null(matKeyVal))
											matKeyVal="0";
										break;
									case 21:
										matKeyVal = mat.getMaterial_distriAmt();
										if (Check.Null(matKeyVal))
											matKeyVal="0";
										break;
									case 22:
										matKeyVal = accountDate;
										break;
									case 23:
										String isBuckle = mat.getIsBuckle();
										if (Check.Null(isBuckle)||!isBuckle.equals("N")) {
											isBuckle="Y";
										}
										matKeyVal=isBuckle;
										break;
									case 24:
										matKeyVal = mat.getMaterial_featureNo();
										if (Check.Null(matKeyVal))
											matKeyVal=" ";
										break;
									default:
										break;
								}
								
								if (matKeyVal != null) {
									insColCt2++;
									if (j == 5 || j == 6){
										matColumnsVal[j] = new DataValue(matKeyVal, Types.FLOAT);
									}else{
										matColumnsVal[j] = new DataValue(matKeyVal, Types.VARCHAR);
									}
								}
								else {
									matColumnsVal[j] = null;
								}
							}
							String[] columns3  = new String[insColCt2];
							DataValue[] insValue3 = new DataValue[insColCt2];
							// 依照傳入參數組譯要insert的欄位與數值；
							insColCt2 = 0;
							for (int k=0;k<matColumnsVal.length;k++){
								if(matColumnsVal[k] != null){
									columns3[insColCt2] = matColumnsName[k];
									insValue3[insColCt2] = matColumnsVal[k];
									insColCt2 ++;
									if (insColCt2 >= insValue3.length)
										break;
								}
							}
							InsBean ib3 = new InsBean("DCP_PSTOCKIN_MATERIAL", columns3);
							ib3.addValues(insValue3);
							this.addProcessData(new DataProcessBean(ib3));

                            List<DCP_PStockInRefundUpdateReq.materialBatchList> materialBatchList = mat.getMaterialBatchList();
                            if(CollUtil.isNotEmpty(materialBatchList)){
                                for (DCP_PStockInRefundUpdateReq.materialBatchList materialBatch : materialBatchList){
                                    batchItem++;

                                    ColumnDataValue batchColumns=new ColumnDataValue();
                                    batchColumns.add("EID", DataValues.newString(eId));
                                    batchColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                                    batchColumns.add("PSTOCKINNO", DataValues.newString(pStockInNO));
                                    batchColumns.add("ITEM", DataValues.newString(batchItem));
                                    batchColumns.add("OITEM", DataValues.newString(mItem));
                                    batchColumns.add("PLUNO", DataValues.newString(mat.getMaterial_pluNo()));
                                    batchColumns.add("FEATURENO", DataValues.newString(mat.getMaterial_featureNo()));
                                    batchColumns.add("PUNIT", DataValues.newString(mat.getMaterial_punit()));
                                    batchColumns.add("PQTY", DataValues.newString(mat.getMaterial_pqty()));
                                    batchColumns.add("BASEUNIT", DataValues.newString(materialBatch.getBaseUnit()));
                                    batchColumns.add("BASEQTY", DataValues.newString(materialBatch.getBaseQty()));
                                    batchColumns.add("UNIT_RATIO", DataValues.newString(materialBatch.getUnitRatio()));
                                    batchColumns.add("WAREHOUSE", DataValues.newString(mat.getMaterial_warehouse()));
                                    batchColumns.add("LOCATION", DataValues.newString(materialBatch.getLocation()));
                                    batchColumns.add("BATCHNO", DataValues.newString(materialBatch.getBatchNo()));
                                    batchColumns.add("PRODDATE", DataValues.newString(materialBatch.getProdDate()));
                                    batchColumns.add("EXPDATE", DataValues.newString(materialBatch.getExpDate()));


                                    String[] batchColumnNames = batchColumns.getColumns().toArray(new String[0]);
                                    DataValue[] batchDataValues = batchColumns.getDataValues().toArray(new DataValue[0]);
                                    InsBean ib=new InsBean("DCP_PSTOCKOUT_BATCH",batchColumnNames);
                                    ib.addValues(batchDataValues);
                                    this.addProcessData(new DataProcessBean(ib));

                                }
                            }


                            mItem++;
						}
					}
					//新增原料单身到此为止
				}
				
				//更新单头
				UptBean ub1 = new UptBean("DCP_PSTOCKIN");
				//add Value
				ub1.addUpdateValue("bDate", new DataValue(bDate, Types.VARCHAR));
				ub1.addUpdateValue("MODIFY_CHATUSERID", new DataValue(req.getChatUserId(), Types.VARCHAR));
				ub1.addUpdateValue("MODIFYBY", new DataValue(modifyBy, Types.VARCHAR));
				ub1.addUpdateValue("MODIFY_DATE", new DataValue(modifyDate, Types.VARCHAR));
				ub1.addUpdateValue("MODIFY_TIME", new DataValue(modifyTime, Types.VARCHAR));
				ub1.addUpdateValue("TOT_PQTY", new DataValue(totPqty, Types.VARCHAR));
				ub1.addUpdateValue("TOT_AMT", new DataValue(totAmt, Types.VARCHAR));
				ub1.addUpdateValue("TOT_CQTY", new DataValue(totCqty, Types.VARCHAR));
				ub1.addUpdateValue("memo", new DataValue(memo, Types.VARCHAR));
				ub1.addUpdateValue("WAREHOUSE", new DataValue(warehouse, Types.VARCHAR));
				ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
				ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
				//2018-08-10  更新单头部分加上docType;
				ub1.addUpdateValue("DOC_TYPE", new DataValue(docType, Types.VARCHAR));
				//2019-07-31  更新单头部分加上pTemplateNO BY JZMA 前端允许变更模板;
				ub1.addUpdateValue("pTemplateNO", new DataValue(pTemplateNO, Types.VARCHAR));
				ub1.addUpdateValue("TOT_DISTRIAMT", new DataValue(totDistriAmt, Types.VARCHAR));
                ub1.addUpdateValue("EMPLOYEEID", new DataValue(req.getRequest().getEmployeeId(), Types.VARCHAR));
                ub1.addUpdateValue("DEPARTID", new DataValue(req.getRequest().getDepartId(), Types.VARCHAR));
                ub1.addUpdateValue("PRODTYPE", new DataValue(req.getRequest().getProdType(), Types.VARCHAR));
                ub1.addUpdateValue("OOTYPE", new DataValue(req.getRequest().getoOType(), Types.VARCHAR));
                ub1.addUpdateValue("OOFNO", new DataValue(req.getRequest().getoOfNo(), Types.VARCHAR));

				//condition
				ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				ub1.addCondition("pStockInNO", new DataValue(pStockInNO, Types.VARCHAR));
				ub1.addCondition("organizationNO", new DataValue(organizationNO, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(ub1));
			} else {
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据不存在或已确认！");
			}
			
			this.doExecuteDataToDB();
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
			
		} catch(Exception e) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
	}
	
	@Override
	protected List<InsBean> prepareInsertData(DCP_PStockInRefundUpdateReq req) throws Exception {
		return null;
	}
	
	@Override
	protected List<UptBean> prepareUpdateData(DCP_PStockInRefundUpdateReq req) throws Exception {
		return null;
	}
	
	@Override
	protected List<DelBean> prepareDeleteData(DCP_PStockInRefundUpdateReq req) throws Exception {
		return null;
	}
	
	@Override
	protected boolean isVerifyFail(DCP_PStockInRefundUpdateReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();
		String warehouse = req.getRequest().getWarehouse();
		String materialWarehouse = req.getRequest().getMaterialWarehouseNo();
		String pStockInNo = req.getRequest().getpStockInNo();
		String totPqty = req.getRequest().getTotPqty();
		String totAmt = req.getRequest().getTotAmt();
		String totDistriAmt=req.getRequest().getTotDistriAmt();
		String totCqty = req.getRequest().getTotCqty();
		List<level1Elm> datas = req.getRequest().getDatas();
		String pStockInNO_origin = req.getRequest().getpStockInNo_origin();
		String bDate = req.getRequest().getbDate();
		
		if(Check.Null(pStockInNO_origin)){
			errMsg.append("原完工入库单号（pStockInNO_origin）不可为空值, ");
			isFail = true;
		}
		if(Check.Null(warehouse)){
			errMsg.append("仓库不可为空值, ");
			isFail = true;
		}
		if(Check.Null(materialWarehouse)||materialWarehouse.equals(" ")){
			errMsg.append("原料仓库不可为空值或空格, ");
			isFail = true;
		}
		if (Check.Null(totPqty)) {
			errMsg.append("合计录入数量不可为空值, ");
			isFail = true;
		}
		if (Check.Null(totAmt)) {
			errMsg.append("合计录入数量不可为空值, ");
			isFail = true;
		}
		if (Check.Null(totDistriAmt)) {
			errMsg.append("合计进货金额可为空值, ");
			isFail = true;
		}
		if (Check.Null(totCqty)) {
			errMsg.append("合计品种数量不可为空值, ");
			isFail = true;
		}
		if (Check.Null(pStockInNo)) {
			errMsg.append("单号不可为空值, ");
			isFail = true;
		}
		
		//【ID1021919】【嘉华3.0】完工入库BDATE传值有问题  by jinzma 20211108
		if (Check.Null(bDate)){
			errMsg.append("单据日期不可为空值, ");
			isFail = true;
		}else{
			if (bDate.length() != 8){
				errMsg.append("单据日期格式错误, ");
				isFail = true;
			}
		}
		
		for(level1Elm par : datas){
			String pluNo = par.getPluNo();
			String baseUnit =par.getBaseUnit();
			String baseQty = par.getBaseQty();
			String unitRatio = par.getUnitRatio();
			String price=par.getPrice();
			String distriPrice=par.getDistriPrice();
			String amt=par.getAmt();
			String distriAmt=par.getDistriAmt();
			String pqty_str = par.getPqty();
			String scrapQty_str = par.getScrapQty();
			float pqty = 0;
			try {
				pqty = Float.parseFloat(pqty_str);
			} catch (Exception ignored) {
			}
			float scrapQty = 0;
			try {
				scrapQty = Float.parseFloat(scrapQty_str);
			} catch (Exception ignored) {
			}
			if (pqty<0&&scrapQty<0) {     //同时，传2个PQTY scrapqty数量 数据有问题
				errMsg.append("商品编码"+pluNo+" 项次"+par.getItem()+" 同时存在(合格、报废)红冲数量, 前端传入的数据有误，");
				isFail = true;
			}
			if (pqty>0){   //pqty=0是报废的，
				errMsg.append("商品编码"+pluNo+" 项次"+par.getItem()+" (合格)红冲数量（pqty="+pqty+"）不可以为正数, ");
				isFail = true;
			}
			if (scrapQty>0){  //pqty=0是报废的，
				errMsg.append("商品编码"+pluNo+" 项次"+par.getItem()+" (报废)红冲数量（scrapQty="+scrapQty+"）不可以为正数, ");
				isFail = true;
			}
			if (Check.Null(par.getItem())) {
				errMsg.append("项次不可为空值, ");
				isFail = true;
			}
			if (Check.Null(par.getoItem())) {
				errMsg.append("来源项次不可为空值, ");
				isFail = true;
			}
			if (Check.Null(pluNo)) {
				errMsg.append("商品编码不可为空值, ");
				isFail = true;
			}
			if (Check.Null(par.getPunit())) {
				errMsg.append("录入单位不可为空值, ");
				isFail = true;
			}
			if (Check.Null(par.getWarehouse())) {
				errMsg.append("仓库不可为空值, ");
				isFail = true;
			}
			if (Check.Null(par.getPqty_origin())) {
				errMsg.append("原单的pqty_origin不可为空值, ");
				isFail = true;
			}
			if (baseUnit == null) {
				errMsg.append("商品"+pluNo+"基本单位不可为空值, ");
				isFail = true;
			}
			if (baseQty == null) {
				errMsg.append("商品"+pluNo+"基本数量不可为空值, ");
				isFail = true;
			}
			if (unitRatio == null) {
				errMsg.append("商品"+pluNo+"单位转换率不可为空值, ");
				isFail = true;
			}
			if (price== null) {
				errMsg.append("商品"+pluNo+"零售价不可为空值, ");
				isFail = true;
			}
			if (distriPrice== null) {
				errMsg.append("商品"+pluNo+"进货价不可为空值, ");
				isFail = true;
			}
			if (amt== null) {
				errMsg.append("商品"+pluNo+"金额不可为空值, ");
				isFail = true;
			}
			if (distriAmt== null) {
				errMsg.append("商品"+pluNo+"进货金额不可为空值, ");
				isFail = true;
			}
			if (isFail) {
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}
		}
		
		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}
	
	@Override
	protected TypeToken<DCP_PStockInRefundUpdateReq> getRequestType() {
		return new TypeToken<DCP_PStockInRefundUpdateReq>(){};
	}
	
	@Override
	protected DCP_PStockInRefundUpdateRes getResponseType() {
		return new DCP_PStockInRefundUpdateRes();
	}
	
	private boolean checkGuid(DCP_PStockInRefundUpdateReq req) throws Exception {
		String eId= req.geteId();
		String shopId=req.getShopId();
		String pStockInNO = req.getRequest().getpStockInNo();
		boolean existGuid=true;
		String sql = "select pstockinno,ACCOUNT_DATE from DCP_pStockIn "
				+ " where EID='"+eId+"' and SHOPID='"+shopId+"' and pstockinno='"+pStockInNO+"' and status='0' " ;
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		if (getQData == null || getQData.isEmpty()) {
			existGuid = false;
		} else {
			accountDate=getQData.get(0).get("ACCOUNT_DATE").toString();
		}
		return existGuid;
	}
}
