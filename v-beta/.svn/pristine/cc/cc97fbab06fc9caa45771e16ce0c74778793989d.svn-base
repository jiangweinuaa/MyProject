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
import com.dsc.spos.json.cust.req.DCP_PStockInRefundCreateReq.level1Elm;
import com.dsc.spos.json.cust.req.DCP_PStockInRefundCreateReq.level2Elm;
import com.dsc.spos.json.cust.res.DCP_PStockInRefundCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_PStockInRefundCreate extends SPosAdvanceService<DCP_PStockInRefundCreateReq, DCP_PStockInRefundCreateRes>{

	@Override
	protected void processDUID(DCP_PStockInRefundCreateReq req, DCP_PStockInRefundCreateRes res) throws Exception {
		String pStockInNO = "";
		String originPStockInNO = req.getRequest().getpStockInNo_origin(); //原完工入库单号
		String shopId = req.getShopId();
		String organizationNO = req.getOrganizationNO();
		String eId = req.geteId();
		String warehouse = req.getRequest().getWarehouse();
		String materialWarehouse = req.getRequest().getMaterialWarehouseNo();
		String memo = req.getRequest().getMemo();
		String status = req.getRequest().getStatus();
		String process_Status = "N";
		String pTemplateNO = req.getRequest().getpTemplateNo();
		String bDate = req.getRequest().getbDate();

		//2018-08-07 添加DOC_TYPE
		String doc_Type = req.getRequest().getDocType();
		String createBy = req.getOpNO();
		Calendar cal = Calendar.getInstance();//获得当前时间
		SimpleDateFormat dfDate = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat dfTime = new SimpleDateFormat("HHmmss");
		String createDate = dfDate.format(cal.getTime());
		String createTime = dfTime.format(cal.getTime());
		String totPqty = req.getRequest().getTotPqty();
		String totAmt = req.getRequest().getTotAmt();
		String totCqty = req.getRequest().getTotCqty();
		String totDistriAmt = req.getRequest().getTotDistriAmt();

		try {
			if (checkGuid(req) == false) {
				///完工入库，单据日期新增如果前端没有给值，后端取系统日期  BY JZMA 20200427
				if (Check.Null(bDate))
					bDate=createDate;
				//pStockInNO = getPStockInNO(req);

                String preFix="";
                switch (req.getRequest().getDocType()) {
                    case "1":
                        preFix = "ZHRK" ; //组合入库1
                        break;
                    case "2":
                        preFix = "CJCK" ; //拆解出库2
                        break;
                    case "3":
                        preFix = "ZHHB" ; //转换合并
                        break;
                    // 2019-08-19 若docType==4， 转换拆解单据，
                    case "4":  //转换拆解
                        preFix = "ZHCJ" ;
                        break;
                    default:
                        preFix = "WGRK" ; //完工入库0
                        break;
                }
                pStockInNO = this.getOrderNO(req,preFix);

                String pStockInID = req.getRequest().getpStockInID();
				String ofNO = req.getRequest().getOfNo();
				String oType = req.getRequest().getoType();
				String[] columns1 = {
						"SHOPID", "ORGANIZATIONNO","BDATE","PSTOCKIN_ID","CREATEBY", "CREATE_DATE", "CREATE_TIME","TOT_PQTY",
						"TOT_AMT", "TOT_CQTY", "EID","PSTOCKINNO", "MEMO", "STATUS", "PROCESS_STATUS","OFNO","PTEMPLATENO",
						"ACCOUNTBY","ACCOUNT_DATE","ACCOUNT_TIME","OTYPE","WAREHOUSE",
						"DOC_TYPE","PSTOCKINNO_ORIGIN","PSTOCKINNO_REFUND","REFUNDSTATUS",
						"TOT_DISTRIAMT","CREATE_CHATUSERID","UPDATE_TIME","TRAN_TIME","EMPLOYEEID","DEPARTID","CREATEDEPTID","PRODTYPE","OOTYPE","OOFNO"
				};
				DataValue[] insValue1 = null;
				List<level1Elm> datas = req.getRequest().getDatas();
				if(datas.isEmpty()) {
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单身商品或数量为空!!");
				}
				int mItem = 1;//原料项次自增长，不取前端
				int item_refund =1 ;//前端给了，报废的，会出现不连续，
				//新增单身（多笔）

                int batchItem=0;
				for (level1Elm par : datas) {
					//过滤前端给的合格红冲 和 报废红冲都是0的 垃圾数据
					String pqty_str = par.getPqty();
					float pqty = 0;
					try {
						pqty = Float.parseFloat(pqty_str);
					} catch (Exception ignored) {
					}
					String scrapQty_str = par.getScrapQty();
					float scrapQty = 0;
					try {
						scrapQty = Float.parseFloat(scrapQty_str);
					} catch (Exception ignored) {
					}

					if(pqty==0&&scrapQty==0) {//过滤前端给的合格红冲 和 报废红冲都是0的 垃圾数据
						continue;
					}


					int insColCt = 0;
					String[] columnsName = {
							"PSTOCKINNO", "SHOPID", "item", "oItem", "pluNO",
							"punit", "pqty", "BASEUNIT", "BASEQTY", "unit_Ratio",
							"price", "amt", "EID", "organizationNO",
							"task_qty", "scrap_qty", "mul_Qty", "bsNO",
							"memo", "gDate", "gTime", "WAREHOUSE",
							"BATCH_NO","PROD_DATE","DISTRIPRICE","pqty_origin",
							"pqty_refund","DISTRIAMT","ACCOUNT_DATE","scrap_qty_origin","scrap_qty_refund",
							"FEATURENO","LOCATION","EXPDATE","DISPTYPE","OOITEM"

							//							PSTOCKINNO,SHOPID,item,oItem,pluNO,
							//							punit,pqty,BASEUNIT,BASEQTY,unit_Ratio,
							//							price,amt,EID,organizationNO,
							//							task_qty,scrap_qty,mul_Qty,bsNO,
							//							WAREHOUSE,BATCH_NO,PROD_DATE,DISTRIPRICE,pqty_origin,pqty_refund,DISTRIAMT,ACCOUNT_DATE,
							//							scrap_qty_origin,scrap_qty_refund,FEATURENO  

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
								keyVal = item_refund+""; //item
								break;
							case 3:
								keyVal = par.getoItem();//oItem
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
								keyVal = par.getTaskQty();	//taskQty
								break;
							case 15:
								keyVal = par.getScrapQty();	//scrapQty
								break;
							case 16:
								keyVal = par.getMulQty();
								break;
							case 17:
								keyVal = par.getBsNo();
								break;
							case 18:
								keyVal = par.getMemo();
								break;
							case 19:
								keyVal = par.getgDate();
								break;
							case 20:
								keyVal = par.getgTime();
								break;
							case 21:
								keyVal = par.getWarehouse();
								break;
							case 22:
								keyVal = par.getBatchNo();
								break;
							case 23:
								keyVal = par.getProdDate();
								break;
							case 24:
								keyVal=par.getDistriPrice();
								break;
							case 25:
								keyVal = par.getPqty_origin();
								if(Check.Null(keyVal))
									keyVal = "0";
								break;
							case 26:
								keyVal = "0";//默认就是0，不用取前端， 前端给错值
								break;
							case 27:
								keyVal = par.getDistriAmt();
								if (Check.Null(keyVal))
									keyVal="0";
								break;
							case 28:
								keyVal = createDate;  //这里给默认当天 康志才要求的
								break;
							case 29:
								keyVal = par.getScrapQty_origin();//关联原单的报废数
								if(keyVal==null||keyVal.trim().isEmpty())
								{
									keyVal = "0";
								}
								break;
							case 30:
								keyVal = "0";
								break;
							case 31:
								keyVal = par.getFeatureNo();
								if (Check.Null(keyVal))
									keyVal=" ";
								break;
                            case 32:
                                keyVal = par.getLocation();
                                if (Check.Null(keyVal))
                                    keyVal=" ";
                                break;
                            case 33:
                                keyVal = par.getExpDate();
                                break;
                            case 34:
                                keyVal = par.getDispType();
                                break;
                            case 35:
                                keyVal = par.getoOItem();
                                break;
							default:
								break;
						}

						if (keyVal != null) {
							insColCt++;
							if (i == 2 || i == 3)
							{
								columnsVal[i] = new DataValue(keyVal, Types.INTEGER);
							}
							else
							{
								columnsVal[i] = new DataValue(keyVal, Types.VARCHAR);
							}
						}
						else {
							columnsVal[i] = null;
						}
					}

					String[] columns2  = new String[insColCt];
					DataValue[] insValue2 = new DataValue[insColCt];
					// 依照傳入參數組譯要insert的欄位與數值；
					insColCt = 0;

					for (int i=0;i<columnsVal.length;i++){
						if (columnsVal[i] != null){
							columns2[insColCt] = columnsName[i];
							insValue2[insColCt] = columnsVal[i];
							insColCt ++;
							if (insColCt >= insValue2.length)
								break;
						}
					}
					InsBean ib2 = new InsBean("DCP_PSTOCKIN_DETAIL", columns2);
					ib2.addValues(insValue2);
					this.addProcessData(new DataProcessBean(ib2));

					item_refund++;
					//新增原料单身（多笔） 		
					List<level2Elm> material = par.getMaterial();
					if(material == null )
					{
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品原料为空!!");
					}
					else
					{
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
										matKeyVal = materialWarehouse;
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
										if(Check.Null(matKeyVal))
										{
											matKeyVal="0";
										}
										break;
									case 7:
										matKeyVal = mat.getMaterial_amt();
										if (Check.Null(matKeyVal))
											matKeyVal="0";
										BigDecimal matKeyVal_b = new BigDecimal(matKeyVal);
										//前端给值有问题，红冲都是负数
										if(matKeyVal_b.compareTo(BigDecimal.ZERO)>0)
										{
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
										matKeyVal = createDate;
										break;
									case 23:
										matKeyVal = mat.getIsBuckle();
										if (Check.Null(matKeyVal)||!matKeyVal.equals("N"))
											matKeyVal="Y";
										break;
									case 24:
										matKeyVal = mat.getMaterial_featureNo();
										if (Check.Null(matKeyVal))
											matKeyVal = " ";
									default:
										break;
								}

								if (matKeyVal != null) {
									insColCt2++;
									if (j == 5 ||j == 6 || j == 7 || j == 16)
									{
										//matColumnsVal[j] = new DataValue(Float.parseFloat(matKeyVal), Types.FLOAT);
										matColumnsVal[j] = new DataValue(matKeyVal, Types.FLOAT);
									}
									else{
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

                            List<DCP_PStockInRefundCreateReq.materialBatchList> materialBatchList = mat.getMaterialBatchList();
                            if(CollUtil.isNotEmpty(materialBatchList)){
                                for (DCP_PStockInRefundCreateReq.materialBatchList materialBatch : materialBatchList){

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

				insValue1 = new DataValue[]{
						new DataValue(shopId, Types.VARCHAR),
						new DataValue(organizationNO, Types.VARCHAR),
						new DataValue(bDate, Types.VARCHAR),
						new DataValue(pStockInID, Types.VARCHAR),
						new DataValue(createBy, Types.VARCHAR),
						new DataValue(createDate, Types.VARCHAR),
						new DataValue(createTime, Types.VARCHAR),
						new DataValue(totPqty, Types.VARCHAR),
						new DataValue(totAmt, Types.VARCHAR),
						new DataValue(totCqty, Types.VARCHAR),
						new DataValue(eId, Types.VARCHAR),
						new DataValue(pStockInNO, Types.VARCHAR),
						new DataValue(memo, Types.VARCHAR),
						new DataValue(status, Types.VARCHAR),
						new DataValue(process_Status, Types.VARCHAR),
						new DataValue(ofNO, Types.VARCHAR),
						new DataValue(pTemplateNO, Types.VARCHAR),
						new DataValue(createBy, Types.VARCHAR),
						new DataValue(createDate, Types.VARCHAR),
						new DataValue(createTime, Types.VARCHAR),
						new DataValue(oType, Types.VARCHAR),
						new DataValue(warehouse, Types.VARCHAR),
						new DataValue(doc_Type, Types.VARCHAR),
						new DataValue(originPStockInNO, Types.VARCHAR),
						new DataValue("", Types.VARCHAR),
						new DataValue("", Types.VARCHAR),
						new DataValue(totDistriAmt, Types.VARCHAR),
						new DataValue(req.getChatUserId(), Types.VARCHAR),
						new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
						new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                        new DataValue(req.getRequest().getEmployeeId(), Types.VARCHAR),
                        new DataValue(req.getRequest().getDepartId(), Types.VARCHAR),
                        new DataValue(req.getDepartmentNo(), Types.VARCHAR),
                        new DataValue(req.getRequest().getProdType(), Types.VARCHAR),
                        new DataValue(req.getRequest().getoOType(), Types.VARCHAR),
                        new DataValue(req.getRequest().getoOfNo(), Types.VARCHAR),

				};

				InsBean ib1 = new InsBean("DCP_PSTOCKIN", columns1);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1)); // 新增單頭


				//红冲的单子，需要回写原单状态		
				if(!Check.Null(originPStockInNO)) {
					UptBean ub2 = null;
					ub2 = new UptBean("DCP_PSTOCKIN");
					//add Value PSTOCKINNO_REFUND
					ub2.addUpdateValue("PSTOCKINNO_REFUND", new DataValue(pStockInNO, Types.VARCHAR));//更新原单
					ub2.addUpdateValue("refundstatus", new DataValue("0", Types.VARCHAR));//0-新建		
					ub2.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
					ub2.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
					//condition
					ub2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
					ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					ub2.addCondition("PSTOCKINNO", new DataValue(originPStockInNO, Types.VARCHAR));
					ub2.addCondition("organizationNO", new DataValue(organizationNO, Types.VARCHAR));
					this.addProcessData(new DataProcessBean(ub2));
				}

				this.doExecuteDataToDB();

				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");
			} else {
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据已存在，请重新确认！ ");
			}
		}
		catch(Exception e) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PStockInRefundCreateReq req) throws Exception {
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PStockInRefundCreateReq req) throws Exception {
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PStockInRefundCreateReq req) throws Exception {
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PStockInRefundCreateReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();
		//必传值不为空
		String status = req.getRequest().getStatus();
		String pStockInID = req.getRequest().getpStockInID();
		String warehouse = req.getRequest().getWarehouse();
		String materialWarehouse = req.getRequest().getMaterialWarehouseNo();
		String docType = req.getRequest().getDocType();
		String totPqty = req.getRequest().getTotPqty();
		String totAmt = req.getRequest().getTotAmt();
		String totDistriAmt=req.getRequest().getTotDistriAmt();
		String totCqty = req.getRequest().getTotCqty();
		String pStockInNO_origin = req.getRequest().getpStockInNo_origin();
		String bDate = req.getRequest().getbDate();
		List<level1Elm> datas = req.getRequest().getDatas();

		if(Check.Null(docType)){
			errMsg.append("单据类型不可为空值, ");
			isFail = true;
		}
		if(Check.Null(pStockInNO_origin)){
			errMsg.append("原完工入库单号（pStockInNO_origin）不可为空值, ");
			isFail = true;
		}
		if(Check.Null(status)){
			errMsg.append("状态不可为空值, ");
			isFail = true;
		}
		if(Check.Null(pStockInID)){
			errMsg.append("红冲单guid不可为空值, ");
			isFail = true;
		}
		if(Check.Null(warehouse)||warehouse.equals(" ")){
			errMsg.append("成品仓库不可为空值或空格, ");
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

		for(level1Elm par : datas) {
			String pluNo = par.getPluNo();
			String baseUnit =par.getBaseUnit();
			String baseQty = par.getBaseQty();
			String unitRatio = par.getUnitRatio();
			String price=par.getPrice();
			String distriPrice=par.getDistriPrice();
			String amt=par.getAmt();
			String distriAmt=par.getDistriAmt();
			List<level2Elm> material = par.getMaterial();
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
			if(pqty<0&&scrapQty<0){    //同时，传2个PQTY scrapqty数量 数据有问题
				errMsg.append("商品编码"+pluNo+" 项次"+par.getItem()+" 同时存在(合格、报废)红冲数量, 前端传入的数据有误，");
				isFail = true;
			}
			if(pqty>0){   //pqty=0是报废的，
				errMsg.append("商品编码"+pluNo+" 项次"+par.getItem()+" (合格)红冲数量（pqty="+pqty+"）不可以为正数, ");
				isFail = true;
			}
			if(scrapQty>0) {  //pqty=0是报废的，
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
			if (Check.Null(par.getPluNo())) {
				errMsg.append("商品编码不可为空值, ");
				isFail = true;
			}
			if (Check.Null(par.getPunit())) {
				errMsg.append("录入单位不可为空值, ");
				isFail = true;
			}
			if (Check.Null(par.getPqty())) {
				errMsg.append("录入数量不可为空值, ");
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
			if (Check.Null(par.getPqty_origin())) {
				errMsg.append("原单的pqty_origin不可为空值, ");
				isFail = true;
			}

			if (isFail) {
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}

			for(level2Elm materialPar : material) {
				String mItem = materialPar.getmItem();
				String material_item = materialPar.getMaterial_item();
				String material_pluNo = materialPar.getMaterial_pluNo();
				String material_punit = materialPar.getMaterial_punit();
				String material_pqty = materialPar.getMaterial_pqty();
				String material_baseUnit = materialPar.getMaterial_baseUnit();
				String material_baseQty = materialPar.getMaterial_baseQty();
				String material_unitRatio = materialPar.getMaterial_unitRatio();
				String material_price = materialPar.getMaterial_price();
				String material_distriPrice = materialPar.getMaterial_distriPrice();
				String material_amt = materialPar.getMaterial_amt();
				String material_distriAmt = materialPar.getMaterial_distriAmt();
				String material_finalProdBaseQty = materialPar.getMaterial_finalProdBaseQty();
				String material_rawMaterialBaseQty = materialPar.getMaterial_rawMaterialBaseQty();

				if ( mItem== null) {
					errMsg.append("原料"+material_pluNo+"主项次不可为空值, ");
					isFail = true;
				}
				if ( material_item== null) {
					errMsg.append("原料"+material_pluNo+"项次不可为空值, ");
					isFail = true;
				}
				if ( material_pluNo== null) {
					errMsg.append("原料"+material_pluNo+"编码不可为空值, ");
					isFail = true;
				}
				if ( material_punit== null) {
					errMsg.append("原料"+material_pluNo+"单位不可为空值, ");
					isFail = true;
				}
				if ( material_pqty== null) {
					errMsg.append("原料"+material_pluNo+"数量不可为空值, ");
					isFail = true;
				}
				if ( material_baseUnit== null) {
					errMsg.append("原料"+material_pluNo+"基准单位不可为空值, ");
					isFail = true;
				}
				if ( material_baseQty== null) {
					errMsg.append("原料"+material_pluNo+"基准单位数量不可为空值, ");
					isFail = true;
				}
				if ( material_unitRatio== null) {
					errMsg.append("原料"+material_pluNo+"单位转换率不可为空值, ");
					isFail = true;
				}
				if ( material_price== null) {
					errMsg.append("原料"+material_pluNo+"零售价不可为空值, ");
					isFail = true;
				}
				if ( material_distriPrice== null) {
					errMsg.append("原料"+material_pluNo+"进货价不可为空值, ");
					isFail = true;
				}
				if ( material_amt== null) {
					errMsg.append("原料"+material_pluNo+"金额不可为空值, ");
					isFail = true;
				}
				if ( material_distriAmt== null) {
					errMsg.append("原料"+material_pluNo+"进货金额不可为空值, ");
					isFail = true;
				}
				if ( material_finalProdBaseQty== null) {
					errMsg.append("原料"+material_pluNo+"成品基础量不可为空值, ");
					isFail = true;
				}
				if ( material_rawMaterialBaseQty== null) {
					errMsg.append("原料"+material_pluNo+"原料基础用量不可为空值, ");
					isFail = true;
				}
				if (isFail) {
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
				}
			}
		}

		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_PStockInRefundCreateReq> getRequestType() {
		return new TypeToken<DCP_PStockInRefundCreateReq>(){};
	}

	@Override
	protected DCP_PStockInRefundCreateRes getResponseType() {
		return new DCP_PStockInRefundCreateRes();
	}

	@Override
	protected String getQuerySql(DCP_PStockInRefundCreateReq req) throws Exception {
		return null ;
	}

	private String getPStockInNO(DCP_PStockInRefundCreateReq req) throws Exception  {
		/*
		 * 单据编号在后台按规格生成(固定编码+年月日+5位流水号(比如TBCK201607010001)，流水号取门店该单据最大流水号+1)
		 * 注意固定编码：如果是调拨出库则为DBCK 如果是退货出库则为THCK 如果是次品出库则为CPCK
		 */
		String sql = null;
		String pStockInNO = null;
		String shopId = req.getShopId();
		String eId = req.geteId();
		String bDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
		StringBuffer sqlbuf = new StringBuffer();
		String docType = req.getRequest().getDocType();
		//新增服务时  docType是1的时候 单号开头字母换成ZHRK；2的时候换成CJCK
		switch (docType) {
			case "1":
				pStockInNO = "ZHRK" + bDate;
				break;
			case "2":
				pStockInNO = "CJCK" + bDate;
				break;
			// 2019-05-29 若docType==3， 转换合并单，
			case "3":
				pStockInNO = "ZHHB" + bDate;
				break;
			// 2019-08-19 若docType==4， 转换拆解单据，
			case "4":
				pStockInNO = "ZHCJ" + bDate;
				break;
			default:
				pStockInNO = "WGRK" + bDate;
				break;
		}
		sqlbuf.append("" + "select PSTOCKINNO  from ( " + "select max(PSTOCKINNO) as PSTOCKINNO "
				+ "  from DCP_PSTOCKIN " + " where EID = ? " + " and SHOPID = ? "
				+ " and PSTOCKINNO like '%%" + pStockInNO + "%%' "); // 假資料
		sqlbuf.append(" ) TBL ");
		sql = sqlbuf.toString();
		String[] conditionValues = {eId, shopId };
		List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);
		if (getQData != null && getQData.isEmpty() == false) {

			pStockInNO = (String) getQData.get(0).get("PSTOCKINNO");
			if (pStockInNO != null && pStockInNO.length() > 0) {
				long i;
				pStockInNO = pStockInNO.substring(4);
				i = Long.parseLong(pStockInNO) + 1;
				pStockInNO = i + "";
				switch (docType) {
					case "1":
						pStockInNO = "ZHRK" + pStockInNO; //组合入库1
						break;
					case "2":
						pStockInNO = "CJCK" + pStockInNO; //拆解出库2
						break;
					case "3":
						pStockInNO = "ZHHB" + pStockInNO; //转换合并3
						break;
					// 2019-08-19 若docType==4， 转换拆解单据，
					case "4":
						pStockInNO = "ZHCJ" + pStockInNO;
						break;
					default:
						pStockInNO = "WGRK" + pStockInNO; //完工入库0
						break;
				}
			}
			else {
				switch (docType) {
					case "1":
						pStockInNO = "ZHRK" + bDate + "00001"; //组合入库1
						break;
					case "2":
						pStockInNO = "CJCK" + bDate + "00001"; //拆解出库2
						break;
					case "3":
						pStockInNO = "ZHHB" + bDate + "00001"; //拆解出库2
						break;
					// 2019-08-19 若docType==4， 转换拆解单据，
					case "4":
						pStockInNO = "ZHCJ" + bDate + "00001";
						break;
					default:
						pStockInNO = "WGRK" + bDate + "00001"; //完工入库0
						break;
				}

			}
		}
		else {
			switch (docType) {
				case "1":
					pStockInNO = "ZHRK" + bDate + "00001"; //组合入库1
					break;
				case "2":
					pStockInNO = "CJCK" + bDate + "00001"; //拆解出库2
					break;
				case "3":
					pStockInNO = "ZHHB" + bDate + "00001"; //转换合并
					break;
				// 2019-08-19 若docType==4， 转换拆解单据，
				case "4":  //转换拆解
					pStockInNO = "ZHCJ" + bDate + "00001";
					break;
				default:
					pStockInNO = "WGRK" + bDate + "00001"; //完工入库0
					break;
			}
		}

		return pStockInNO;
	}

	private boolean checkGuid(DCP_PStockInRefundCreateReq req) throws Exception {
		String guid = req.getRequest().getpStockInID();
		boolean existGuid;
		StringBuffer sqlbuf = new StringBuffer();
		sqlbuf.append(""
				+ "select pStockIn_ID "
				+ " from DCP_pStockIn "
				+ " where pStockIn_ID = '"+guid+"' "
		);
		List<Map<String, Object>> getQData = this.doQueryData(sqlbuf.toString(), null);
		if (getQData != null && getQData.isEmpty() == false) {
			existGuid = true;
		} else {
			existGuid =  false;
		}
		return existGuid;
	}

}
