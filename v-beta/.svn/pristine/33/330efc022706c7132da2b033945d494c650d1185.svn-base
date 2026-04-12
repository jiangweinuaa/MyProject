package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.*;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_InterSettleDataGenerateReq;
import com.dsc.spos.json.cust.req.DCP_StockOutCreateReq;
import com.dsc.spos.json.cust.req.DCP_StockOutCreateReq.levelElm;
import com.dsc.spos.json.cust.req.DCP_StockOutCreateReq.level1Elm;
import com.dsc.spos.json.cust.req.DCP_StockOutCreateReq.level2Elm;
import com.dsc.spos.json.cust.req.DCP_StockOutNoticeCreateReq;
import com.dsc.spos.json.cust.res.DCP_InterSettleDataGenerateRes;
import com.dsc.spos.json.cust.res.DCP_StockOutCreateRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.redis.DocSubmitStop;
import com.dsc.spos.redis.IRedisCacheName;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import java.text.SimpleDateFormat;

/**
 * 服務函數：StockOutCreate
 *    說明：出货单新增保存           调拨出库时传1   换季退货时传0    次品退货时传2   其它出库传3 移仓出库传4
 * 服务说明：出货单新增保存
 * @author panjing
 * @since  2016-09-20
 */
public class DCP_StockOutCreate extends SPosAdvanceService<DCP_StockOutCreateReq, DCP_StockOutCreateRes> {
	@Override
	protected boolean isVerifyFail(DCP_StockOutCreateReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();
		levelElm request = req.getRequest();
		List<level1Elm> jsonDatas = request.getDatas();
		
		//必传值不为空
		String bDate = request.getbDate();
		String status = request.getStatus();
		String docType = request.getDocType();
		String warehouse = request.getWarehouse();
		String transferWarehouse = request.getTransferWarehouse();
		
		//必传值可以为空
		String memo = request.getMemo();
		String oType = request.getoType();
		String ofNO = request.getOfNo();
		String loadDocType = request.getLoadDocType();
		String loadDocNO = request.getLoadDocNo();
		String deliveryNO = request.getDeliveryNo();
		String transferShop = request.getTransferShop();
		String totPqty = request.getTotPqty();
		String totAmt = request.getTotAmt();
		String totDistriAmt=request.getTotDistriAmt();
		String totCqty = request.getTotCqty();
		
		if (docType.equals("1")){
			if (Check.Null(transferShop)) {
				errMsg.append("调入门店不能为空值！");
				isFail = true;
			}
		}
		if (docType.equals("1")||docType.equals("4")) {
			if (transferWarehouse == null) {
				errMsg.append("调入仓库不可为空值, ");
				isFail = true;
			}
		}
		if (Check.Null(bDate)) {
			errMsg.append("营业日期不可为空值, ");
			isFail = true;
		}
		if (Check.Null(status)) {
			errMsg.append("状态不可为空值, ");
			isFail = true;
		}
		if (Check.Null(docType)) {
			errMsg.append("单据类型不可为空值, ");
			isFail = true;
		}
		if (warehouse == null) {
			errMsg.append("拨出仓库不可为空值, ");
			isFail = true;
		}
		
		if (oType == null) {
			errMsg.append("来源单据类型不可为空值, ");
			isFail = true;
		}
		if (ofNO == null) {
			errMsg.append("来源单据单号不可为空值, ");
			isFail = true;
		}
		if (memo == null) {
			errMsg.append("备注不可为空值, ");
			isFail = true;
		}
		if (loadDocType == null) {
			errMsg.append("转入来源单据类型不可为空值, ");
		}
		if (loadDocNO == null) {
			errMsg.append("转入来源单据单号不可为空值, ");
		}
		if (deliveryNO == null) {
			errMsg.append("物流单号不可为空值, ");
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
		
		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		for (level1Elm par : jsonDatas) {
			//必传值不为空		
			String item = par.getItem();
			String pluNo = par.getPluNo();
			String punit = par.getPunit();
			String pqty = par.getPqty();
			String baseUnit = par.getBaseUnit();
			String baseQty = par.getBaseQty();
			String warehouseD = par.getWarehouse();
			String unitRatio = par.getUnitRatio();
			
			//必传值可以为空
			String oItem = par.getoItem();
			String price = par.getPrice();
			String amt = par.getAmt();
			
			if (Check.Null(item)) {
				errMsg.append("商品"+pluNo+"项次不可为空值, ");
				isFail = true;
			}
			if (oItem == null) {
				//errMsg.append("商品"+pluNo+"来源项次不可为空值, ");
				//isFail = true;
			}
			if (Check.Null(pluNo)) {
				errMsg.append("商品编码不可为空值, ");
				isFail = true;
			}
			if (Check.Null(punit)) {
				errMsg.append("商品"+pluNo+"商品单位不可为空值, ");
				isFail = true;
			}
			if (Check.Null(pqty)) {
				errMsg.append("商品"+pluNo+"商品数量不可为空值, ");
				isFail = true;
			}
			if (price == null) {
				errMsg.append("商品"+pluNo+"单价不可为空值, ");
				isFail = true;
			}
			if (amt == null) {
				errMsg.append("商品"+pluNo+"金额不可为空值, ");
				isFail = true;
			}
			if (warehouseD == null) {
				errMsg.append("商品"+pluNo+"拨出仓库不可为空值, ");
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

			List<DCP_StockOutCreateReq.BatchList> batchList = par.getBatchList();
			if(CollUtil.isNotEmpty(batchList)){
				for (DCP_StockOutCreateReq.BatchList batch : batchList){
					if (Check.Null(batch.getItem())) {
						errMsg.append("商品"+pluNo+"批次项次不可为空值, ");
						isFail = true;
					}

					if (Check.Null(batch.getItem2())) {
						errMsg.append("商品"+pluNo+"批次item2不可为空值, ");
						isFail = true;
					}

					if (Check.Null(batch.getPUnit())) {
						errMsg.append("商品"+pluNo+"批次单位不可为空值, ");
						isFail = true;
					}

					if (Check.Null(batch.getPQty())) {
						errMsg.append("商品"+pluNo+"批次数量不可为空值, ");
						isFail = true;
					}

					if (Check.Null(batch.getUnitRatio())) {
						errMsg.append("商品"+pluNo+"批次单位换算率不可为空值, ");
						isFail = true;
					}
					if (Check.Null(batch.getBaseUnit())) {
						errMsg.append("商品"+pluNo+"批次基准单位不可为空值, ");
						isFail = true;
					}
				}
			}


			if (isFail){
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}
		}
		return isFail;
	}
	
	@Override
	protected TypeToken<DCP_StockOutCreateReq> getRequestType() {
		return new TypeToken<DCP_StockOutCreateReq>(){};
	}
	
	@Override
	protected DCP_StockOutCreateRes getResponseType() {
		return new DCP_StockOutCreateRes();
	}
	
	@Override
	protected void processDUID(DCP_StockOutCreateReq req,DCP_StockOutCreateRes res) throws Exception {
		//try {
			levelElm request = req.getRequest();
			if (checkGuid(req) == false){
				String shopId = req.getShopId();
				String organizationNO = req.getOrganizationNO();
				String eId = req.geteId();
				String bDate = request.getbDate();
				String memo = request.getMemo();
				String status = request.getStatus();
				String docType = request.getDocType();
				String oType = request.getoType();
				String ofNO = request.getOfNo();
				String receiptOrg = request.getReceiptOrg();
				String createBy = req.getOpNO();
				Calendar cal = Calendar.getInstance();//获得当前时间
				SimpleDateFormat df=new SimpleDateFormat("yyyyMMdd");
				String createDate = df.format(cal.getTime());
				df=new SimpleDateFormat("HHmmss");
				String createTime = df.format(cal.getTime());
				String loadDocType = request.getLoadDocType();
				String loadDocNO = request.getLoadDocNo();
				String stockOutNO = getStockoutNoNew(req);
                res.setStockOutNo(stockOutNO);
				String totPqty = request.getTotPqty();
				String totAmt =request.getTotAmt();
				String totDistriAmt = request.getTotDistriAmt();
				String totCqty = request.getTotCqty();
				String sourceMenu = request.getSourceMenu();  //	0其他出库单  1试吃出库单  2赠送出库单
                String lastModiTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());
                String createByName = req.getOpName();

                String corp="";
                String receiptCorp="";
                String orgSql1="select * from dcp_org where eid='"+req.geteId()+"' and organizationno='"+req.getOrganizationNO()+"' ";
                String orgSql2="select * from dcp_org where eid='"+req.geteId()+"' and organizationno='"+request.getReceiptOrg()+"' ";
                List<Map<String, Object>> orgList1 = this.doQueryData(orgSql1, null);
                if(CollUtil.isNotEmpty(orgList1)){
                    corp = orgList1.get(0).get("CORP").toString();
                }
                List<Map<String, Object>> orgList2 = this.doQueryData(orgSql2, null);
                if(CollUtil.isNotEmpty(orgList2)){
                    receiptCorp = orgList2.get(0).get("CORP").toString();
                }


                if(Check.Null(request.getTransferWarehouse())&&!Check.Null(request.getTransferShop())){
                    String orgSql="select * from dcp_org a where a.eid='"+eId+"' and a.organizationno='"+request.getTransferShop()+"' ";
                    List<Map<String, Object>> orgList = this.doQueryData(orgSql, null);
                    if(CollUtil.isNotEmpty(orgList)){
                        request.setTransferWarehouse(orgList.get(0).get("IN_COST_WAREHOUSE").toString());
                    }
                }

                if("4".equals(docType)){
                    if(Check.NotNull(request.getTransferWarehouse())){
                        String warehouseSql=" select * from dcp_warehouse where eid='"+eId+"' and organizationno='"+organizationNO+"' and warehouse='"+request.getTransferWarehouse()+"'";
                        List<Map<String, Object>> warehouseList = this.doQueryData(warehouseSql, null);
                        if(CollUtil.isNotEmpty(warehouseList)){
                            String islocation = warehouseList.get(0).get("ISLOCATION").toString();
                            if("Y".equals(islocation)){
                                List<level1Elm> datas = request.getDatas();
                                for(level1Elm level1Elm:datas){
                                    BigDecimal dPqty = new BigDecimal(level1Elm.getPqty());
                                    List<DCP_StockOutCreateReq.TransInLocationList> transInLocationList = level1Elm.getTransInLocationList();
                                    BigDecimal totDqty=new BigDecimal(0);
                                    for (DCP_StockOutCreateReq.TransInLocationList singleTrans:transInLocationList){
                                        if(Check.Null(singleTrans.getTransInLocation())){
                                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品"+level1Elm.getPluNo()+"移入库位编号不可为空值 ");
                                        }
                                        if(Check.Null(singleTrans.getTransInQty())){
                                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品"+level1Elm.getPluNo()+"移入数量不可为空值 ");
                                        }
                                        totDqty=totDqty.add(new BigDecimal(singleTrans.getTransInQty()));
                                    }
                                    if(dPqty.compareTo(totDqty)!=0){
                                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品"+level1Elm.getPluNo()+"移入库位数量不等于明细数量 ");
                                    }
                                }
                            }
                            if("N".equals(islocation)){
                                request.getDatas().forEach(x->{
                                    x.setTransInLocationList(new ArrayList<>());
                                });
                            }
                        }
                    }
                    else{
                        request.getDatas().forEach(x->{
                            x.setTransInLocationList(new ArrayList<>());
                        });
                    }

                }


				//2019-12-19 增加模板编码
				String pTemplateNo = "";
				pTemplateNo = request.getpTemplateNo();
				
				String deliveryNO = request.getDeliveryNo();
				String transferShop = request.getTransferShop();
				String stockOutID = request.getStockOutID();
				
				String bsNO = Check.Null(request.getBsNo())?"":request.getBsNo();
				
				String warehouse = request.getWarehouse();
				String transferWarehouse = request.getTransferWarehouse();

                String id=req.geteId()+"_"+req.getOrganizationNO()+"_"+req.getRequest().getOfNo();
                String key_redis= IRedisCacheName.stockOutCreateKey+id;
				//出库单新增：来源单号类型为出货通知单（包括3配货通知 4退配通知 5调拨通知 6移仓通知），创建成功需锁定通知单状态=4出货中；服务；/DCP_StockOutCreate
				if(oType.equals("3")||oType.equals("4")||oType.equals("5")||oType.equals("6")){
					if(DocSubmitStop.isStop(key_redis)){//先去掉  保存报错删不掉缓存
						//throw new SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "该通知单正在使用中，请稍后重试");
					}

					UptBean ub1 = new UptBean("DCP_STOCKOUTNOTICE");
					//add Value
					ub1.addUpdateValue("STATUS", new DataValue("4", Types.VARCHAR));

					//condition
					ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					ub1.addCondition("BILLNO", new DataValue(request.getOfNo(), Types.VARCHAR));
					//ub1.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
					this.addProcessData(new DataProcessBean(ub1));
				}


				String[] columns1 = {
						"SHOPID", "OrganizationNO","EID","stockOutNO","BDate", "MEMO",  "Status",
						"DOC_TYPE","oType","ofNO","CreateBy", "Create_Date", "Create_time","TOT_PQTY",
						"TOT_AMT", "TOT_CQTY", "LOAD_DOCTYPE", "LOAD_DOCNO","DELIVERY_NO","transfer_shop",
						"stockOut_ID","bsNO","receipt_org","WAREHOUSE","TRANSFER_WAREHOUSE","TOT_DISTRIAMT",
						"PTEMPLATENO","SOURCEMENU","CREATE_CHATUSERID","UPDATE_TIME","TRAN_TIME","DELIVERYBY",
                        "EMPLOYEEID","DEPARTID","RDATE","PACKINGNO","INVWAREHOUSE","ISTRANINCONFIRM","OOTYPE","OOFNO",
                        "RECEIPTDATE","DELIVERYDATE","CORP","RECEIPTCORP"
				};


				DataValue[] insValue1 = null;
				//新增單身 (多筆)
				List<level1Elm> jsonDatas = request.getDatas();
                int batchItem=0;
				int locationItem=0;
				for (level1Elm par : jsonDatas) {

					if(Check.Null(par.getFeatureNo())){
						par.setFeatureNo(" ");
					}
					if(Check.Null(par.getLocation())){
						par.setLocation(" ");
					}
					if(Check.Null(par.getBatchNo())){
						par.setBatchNo(" ");
					}

					int insColCt = 0;
					String[] columnsName = {
							"STOCKOUTNO", "SHOPID", "item", "oItem", "pluNO",
							"punit", "pqty", "BASEUNIT", "BASEQTY", "unit_Ratio", "PLU_BARCODE",
							"price", "amt", "EID", "organizationNO", "WAREHOUSE","BSNO"
							,"PLU_MEMO","BATCH_NO","PROD_DATE","DISTRIPRICE","DISTRIAMT","BDATE","FEATURENO","STOCKQTY","PACKINGNO","OFNO","MES_LOCATION","EXPDATE",
                            "OTYPE","OOTYPE","OOFNO","OOITEM","STOCKOUTNOQTY","TRANSFER_BATCHNO"
					};

					
					DataValue[] columnsVal = new DataValue[columnsName.length];
					for (int i = 0; i < columnsVal.length; i++) {
						String keyVal = null;
						switch (i) {
							case 0:
								keyVal = stockOutNO;
								break;
							case 1:
								keyVal = shopId;
								break;
							case 2:
								keyVal = par.getItem(); //item
								break;
							case 3:
								String oItem = par.getoItem();
								if (Check.Null(oItem)){
									oItem="0";
								}
								keyVal = oItem; //oItem
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
								keyVal = par.getPluBarcode();    //pluBarcode
								break;
							case 11:
								keyVal = par.getPrice();    //price
								if(par.getPrice()==null || par.getPrice().isEmpty()){
									keyVal = "0";
								}
								break;
							case 12:
								keyVal = par.getAmt();    //amt
								break;
							case 13:
								keyVal = eId;
								break;
							case 14:
								keyVal = organizationNO;
								break;
							case 15:
								keyVal = request.getWarehouse();
								break;
							case 16:
                            	if(!Check.Null(docType) && docType.equals("3") &&
                            			!Check.Null(sourceMenu) && 
                            			(sourceMenu.equals("0") ||
                            			 sourceMenu.equals("1") ||
                            			 sourceMenu.equals("2"))  )
                            	{
                            		keyVal = bsNO;
                            	}else
                            	{
                            		keyVal = par.getBsNo();
                            	}
								break;
							case 17:
								if(par.getPluMemo() == null){
									keyVal = "";
								}
								else{
									keyVal = par.getPluMemo();
								}
								break;
							case 18:
								keyVal = par.getBatchNo();
								break;
							case 19:
								keyVal = par.getProdDate();
								break;
							case 20:
								if(par.getDistriPrice()==null || Check.Null(par.getDistriPrice()) ){
									keyVal = "0";
								}
								else {
									keyVal=par.getDistriPrice();
								}
								break;
							case 21:
								keyVal = par.getDistriAmt();
								if (Check.Null(keyVal))
									keyVal="0";
								break;
							case 22:
								keyVal = bDate;
								break;
							case 23:
								keyVal = par.getFeatureNo();
								if (Check.Null(keyVal))
									keyVal = " ";
								break;
                            case 24:
                                keyVal = par.getStockqty();
                                if (PosPub.isNumericTypeMinus(keyVal)==false)
                                    keyVal = "999999";
                                break;
                            case 25:
                                keyVal = par.getPackingNo();
                                break;
                            case 26:
                                keyVal = par.getOfNo();
                                break;
                            case 27:
                                keyVal = par.getLocation();
                                break;
                            case 28:
                                keyVal = par.getExpDate();
                                break;
                            case 29:
                                keyVal = par.getoType();
                                break;
                            case 30:
                                keyVal = par.getOoType();
                                break;
                            case 31:
                                keyVal = par.getOofNo();
                                break;
                            case 32:
                                keyVal = par.getOoItem();
                                break;
                            case 33:
                                keyVal = par.getStoctOutNoQty();
                                if(Check.Null(keyVal)){
                                    keyVal="0";
                                }
                                break;
                            case 34:
                                keyVal = par.getTransferBatchNo();
                                break;
							default:
								break;
						}
						
						if (keyVal != null) {
							insColCt++;
							if (i == 2 || i == 3 ){
								columnsVal[i] = new DataValue(keyVal, Types.INTEGER);
							}else if (i == 6 || i == 8 || i == 9 || i == 11 || i == 12 || i==20|| i==24 ){
								columnsVal[i] = new DataValue(keyVal, Types.FLOAT);
							}else{
								columnsVal[i] = new DataValue(keyVal, Types.VARCHAR);
							}
							
						} else {
							columnsVal[i] = null;
						}
					}
					String[]    columns2  = new String[insColCt];
					DataValue[] insValue2 = new DataValue[insColCt];
					// 依照傳入參數組譯要insert的欄位與數值；
					insColCt = 0;
					
					for (int i=0;i<columnsVal.length;i++){
						if (columnsVal[i] != null){
							columns2[insColCt] = columnsName[i];
							insValue2[insColCt] = columnsVal[i];
							insColCt ++;
							if (insColCt >= insValue2.length) break;
						}
					}
					
					InsBean ib2 = new InsBean("DCP_STOCKOUT_DETAIL", columns2);
					ib2.addValues(insValue2);
					this.addProcessData(new DataProcessBean(ib2));
                    //商品明细图片列表
					List<level2Elm> imageList = par.getImageList();
					if (imageList!=null&&!imageList.isEmpty())
                    {
                        String[] columnsName_image = {
                                "EID", "SHOPID","STOCKOUTNO", "item", "oItem", "IMAGE",
                                "CREATEOPID", "CREATEOPNAME", "CREATETIME"
                        };
                        InsBean ib_image = new InsBean("DCP_STOCKOUT_DETAIL_IMAGE", columnsName_image);
                        int imageItem = 0;
                        for (level2Elm imageInfo : imageList)
                        {
                            String image = imageInfo.getImage();
                            if (image==null||image.trim().isEmpty())
                            {
                                continue;
                            }
                            imageItem++;
                            DataValue[] columnsImage = new DataValue[]{
                                    new DataValue(eId, Types.VARCHAR),
                                    new DataValue(shopId, Types.VARCHAR),
                                    new DataValue(stockOutNO, Types.VARCHAR),
                                    new DataValue(imageItem, Types.VARCHAR),//item
                                    new DataValue(par.getItem(), Types.VARCHAR),//oItem
                                    new DataValue(image, Types.VARCHAR),
                                    new DataValue(createBy, Types.VARCHAR),
                                    new DataValue(createByName, Types.VARCHAR),
                                    new DataValue(lastModiTime, Types.DATE)
                            };
                            ib_image.addValues(columnsImage);
                        }

                        //防止异常没有图片的垃圾数据
                        if (!ib_image.getValues().isEmpty())
                        {
                            this.addProcessData(new DataProcessBean(ib_image));
                        }

                    }

                    List<DCP_StockOutCreateReq.BatchList> batchList = par.getBatchList();
                    if(CollUtil.isNotEmpty(batchList)){
                        for (DCP_StockOutCreateReq.BatchList batch : batchList){
                            batchItem++;

							if(Check.Null(batch.getBatchNo())){
								batch.setBatchNo(" ");
							}

                            ColumnDataValue batchColumns=new ColumnDataValue();
                            batchColumns.add("EID", DataValues.newString(eId));
                            batchColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                            batchColumns.add("SHOPID", DataValues.newString(shopId));
                            batchColumns.add("STOCKOUTNO", DataValues.newString(stockOutNO));
                            batchColumns.add("ITEM", DataValues.newString(batchItem));
                            batchColumns.add("ITEM2", DataValues.newString(par.getItem()));
                            batchColumns.add("PLUNO", DataValues.newString(par.getPluNo()));
                            batchColumns.add("FEATURENO", DataValues.newString(par.getFeatureNo()));
                            batchColumns.add("WAREHOUSE", DataValues.newString(par.getWarehouse()));
                            batchColumns.add("LOCATION", DataValues.newString(batch.getLocation()));
                            batchColumns.add("BATCHNO", DataValues.newString(batch.getBatchNo()));
                            batchColumns.add("PRODDATE", DataValues.newString(batch.getProdDate()));
                            batchColumns.add("EXPDATE", DataValues.newString(batch.getExpDate()));
                            batchColumns.add("PUNIT", DataValues.newString(batch.getPUnit()));
                            batchColumns.add("PQTY", DataValues.newString(batch.getPQty()));
                            batchColumns.add("BASEUNIT", DataValues.newString(batch.getBaseUnit()));

                            if(Check.Null(batch.getBaseQty())){
                                BigDecimal baseQtyDecimal = new BigDecimal(batch.getPQty()).multiply(new BigDecimal(batch.getUnitRatio()));
                                batch.setBaseQty(baseQtyDecimal.toString());
                            }

                            batchColumns.add("BASEQTY", DataValues.newString(batch.getBaseQty()));

                            String[] batchColumnNames = batchColumns.getColumns().toArray(new String[0]);
                            DataValue[] batchDataValues = batchColumns.getDataValues().toArray(new DataValue[0]);
                            InsBean ibb=new InsBean("DCP_STOCKOUT_BATCH",batchColumnNames);
                            ibb.addValues(batchDataValues);
                            this.addProcessData(new DataProcessBean(ibb));
                        }
                    }
                    else{
                        batchItem++;

                        ColumnDataValue batchColumns=new ColumnDataValue();
                        batchColumns.add("EID", DataValues.newString(eId));
                        batchColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                        batchColumns.add("SHOPID", DataValues.newString(shopId));
                        batchColumns.add("STOCKOUTNO", DataValues.newString(stockOutNO));
                        batchColumns.add("ITEM", DataValues.newString(batchItem));
                        batchColumns.add("ITEM2", DataValues.newString(par.getItem()));
                        batchColumns.add("PLUNO", DataValues.newString(par.getPluNo()));
                        batchColumns.add("FEATURENO", DataValues.newString(par.getFeatureNo()));
                        batchColumns.add("WAREHOUSE", DataValues.newString(par.getWarehouse()));
                        batchColumns.add("LOCATION", DataValues.newString(par.getLocation()));
                        batchColumns.add("BATCHNO", DataValues.newString(par.getBatchNo()));
                        batchColumns.add("PRODDATE", DataValues.newString(par.getProdDate()));
                        batchColumns.add("EXPDATE", DataValues.newString(par.getExpDate()));
                        batchColumns.add("PUNIT", DataValues.newString(par.getPunit()));
                        batchColumns.add("PQTY", DataValues.newString(par.getPqty()));
                        batchColumns.add("BASEUNIT", DataValues.newString(par.getBaseUnit()));
                        batchColumns.add("BASEQTY", DataValues.newString(par.getBaseQty()));
                        batchColumns.add("UNITRATIO", DataValues.newString(par.getUnitRatio()));

                        String[] batchColumnNames = batchColumns.getColumns().toArray(new String[0]);
                        DataValue[] batchDataValues = batchColumns.getDataValues().toArray(new DataValue[0]);
                        InsBean ibb=new InsBean("DCP_STOCKOUT_BATCH",batchColumnNames);
                        ibb.addValues(batchDataValues);
                        this.addProcessData(new DataProcessBean(ibb));
                    }

					List<DCP_StockOutCreateReq.TransInLocationList> transInLocationList = par.getTransInLocationList();
					if(CollUtil.isNotEmpty(transInLocationList)){

						for (DCP_StockOutCreateReq.TransInLocationList transInLocation : transInLocationList){
							locationItem++;

							ColumnDataValue locationColumns=new ColumnDataValue();
							locationColumns.add("EID", DataValues.newString(eId));
							locationColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
							locationColumns.add("STOCKOUTNO", DataValues.newString(stockOutNO));
							locationColumns.add("ITEM", DataValues.newString(locationItem));
							locationColumns.add("OITEM", DataValues.newString(par.getItem()));
							locationColumns.add("LOCATION", DataValues.newString(transInLocation.getTransInLocation()));

                            BigDecimal tBaseQty = new BigDecimal(transInLocation.getTransInQty()).multiply(new BigDecimal(par.getUnitRatio()));
                            locationColumns.add("PQTY", DataValues.newString(transInLocation.getTransInQty()));
                            locationColumns.add("PUNIT", DataValues.newString(par.getPunit()));
                            locationColumns.add("BASEQTY", DataValues.newString(tBaseQty));
                            locationColumns.add("BASEUNIT", DataValues.newString(par.getBaseUnit()));
                            locationColumns.add("UNIT_RATIO", DataValues.newString(par.getUnitRatio()));
                            String[] locationColumnNames = locationColumns.getColumns().toArray(new String[0]);
							DataValue[] locationDataValues = locationColumns.getDataValues().toArray(new DataValue[0]);
							InsBean ibl=new InsBean("DCP_STOCKOUT_DETAIL_LOCATION",locationColumnNames);
							ibl.addValues(locationDataValues);
							this.addProcessData(new DataProcessBean(ibl));


						}

					}


				}
				//收货组织
				if(transferShop==null ||transferShop.trim().length()==0){
					transferShop=receiptOrg;
				}
				// STOCKOUT查询的时候用transferShop关联组织表，如果不赋值会查询不出资料 BY JZMA 20200107
				if (Check.Null(transferShop) && docType.equals("3")) {
					transferShop = shopId;
				}
    
				//【ID1032371】//[饰一派3.0]门店1008，3月28日做退货出库单（THCK2023032800001），收货组织选的是SITE-08义乌之源，报文是1 by jinzma 20230406
				if (docType.equals("0")){
					transferShop = receiptOrg;
				}
				
				insValue1 = new DataValue[]{
						new DataValue(shopId, Types.VARCHAR),
						new DataValue(organizationNO, Types.VARCHAR),
						new DataValue(eId, Types.VARCHAR),
						new DataValue(stockOutNO, Types.VARCHAR),
						new DataValue(bDate, Types.VARCHAR),
						new DataValue(memo, Types.VARCHAR),
						new DataValue(status, Types.VARCHAR),
						new DataValue(docType, Types.VARCHAR),
						new DataValue(oType, Types.VARCHAR),
						new DataValue(ofNO, Types.VARCHAR),
						new DataValue(createBy, Types.VARCHAR),
						new DataValue(createDate, Types.VARCHAR),
						new DataValue(createTime, Types.VARCHAR),
						new DataValue(totPqty, Types.VARCHAR),
						new DataValue(totAmt, Types.VARCHAR),
						new DataValue(totCqty, Types.VARCHAR),
						new DataValue(loadDocType, Types.VARCHAR),
						new DataValue(loadDocNO, Types.VARCHAR),
						new DataValue(deliveryNO, Types.VARCHAR),
						new DataValue(transferShop, Types.VARCHAR),
						new DataValue(stockOutID, Types.VARCHAR),
						new DataValue(bsNO, Types.VARCHAR),
						new DataValue(receiptOrg, Types.VARCHAR),
						new DataValue(warehouse, Types.VARCHAR),
						new DataValue(transferWarehouse, Types.VARCHAR),
						new DataValue(totDistriAmt, Types.VARCHAR),
						new DataValue(pTemplateNo, Types.VARCHAR),
						new DataValue(sourceMenu, Types.VARCHAR),
						new DataValue(req.getChatUserId(), Types.VARCHAR),
						new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
						new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
						//【ID1037888】[红房子3.0]订单和调拨单记录配送员，配送员电话，统计出来后用于计算配送人员工资，之前易成用的是（易成用的要货发货单功能）--服务端 by jinzma 20231215
						new DataValue(req.getRequest().getDeliveryBy(), Types.VARCHAR),
                        new DataValue(req.getRequest().getEmployeeId(), Types.VARCHAR),
                        new DataValue(req.getRequest().getDepartId(), Types.VARCHAR),
                        new DataValue(req.getRequest().getrDate(), Types.VARCHAR),
                        new DataValue(req.getRequest().getPackingNo(), Types.VARCHAR),
                        new DataValue(req.getRequest().getInvWarehouse(), Types.VARCHAR),
                        new DataValue(req.getRequest().getIsTranInConfirm(), Types.VARCHAR),
                        new DataValue(req.getRequest().getOoType(), Types.VARCHAR),
                        new DataValue(req.getRequest().getOofNo(), Types.VARCHAR),
                        new DataValue(req.getRequest().getReceiptDate(), Types.VARCHAR),
                        new DataValue(req.getRequest().getDeliveryDate(), Types.VARCHAR),
                        new DataValue(corp, Types.VARCHAR),
                        new DataValue(receiptCorp, Types.VARCHAR),
				};

				
				InsBean ib1 = new InsBean("DCP_STOCKOUT", columns1);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1)); // 新增單頭


				
				this.doExecuteDataToDB();
				DocSubmitStop.endStop(key_redis);
				
				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");

                if(!corp.equals(receiptCorp)){
                    //单据类型(0退货出库 1调拨出库 3其他出库 4移仓出库 5配送出库)
                    //配货出库
                    if("5".equals(docType)) {

                        DCP_InterSettleDataGenerateReq inReq = new DCP_InterSettleDataGenerateReq();
                        inReq.setServiceId("DCP_InterSettleDataGenerate");
                        inReq.setToken(req.getToken());
                        DCP_InterSettleDataGenerateReq.Request request1 = inReq.new Request();
                        request1.setOrganizationNo(req.getOrganizationNO());
                        request1.setBillNo(stockOutNO);
                        request1.setSupplyOrgNo(req.getOrganizationNO());
                        request1.setReturnSupplyPrice("Y");
                        request1.setBillType(DCP_InterSettleDataGenerate.BillType.BillType10003.getType());
                        request1.setDetail(new ArrayList<>());
                        for (DCP_StockOutCreateReq.level1Elm par : req.getRequest().getDatas()) {
							DCP_InterSettleDataGenerateReq.Detail detail = inReq.new Detail();
							detail.setReceiveOrgNo(req.getRequest().getReceiptOrg());
                            detail.setSourceBillNo(par.getOfNo());
                            detail.setSourceItem(par.getoItem());
                            detail.setItem(String.valueOf(par.getItem()));
                            detail.setPluNo(par.getPluNo());
                            detail.setFeatureNo(par.getFeatureNo());
                            detail.setPUnit(par.getPunit());
                            detail.setPQty(String.valueOf(par.getPqty()));
                            detail.setReceivePrice(par.getDistriPrice());
                            detail.setReceiveAmt(par.getDistriAmt());
                            detail.setSupplyPrice("");
                            detail.setSupplyAmt("");
                            request1.getDetail().add(detail);
                        }
                        inReq.setRequest(request1);
                        ParseJson pj = new ParseJson();
                        String jsontemp = pj.beanToJson(inReq);

                        DispatchService ds = DispatchService.getInstance();
                        String resXml = ds.callService(jsontemp, StaticInfo.dao);
                        DCP_InterSettleDataGenerateRes resserver = pj.jsonToBean(resXml, new TypeToken<DCP_InterSettleDataGenerateRes>() {
                        });
                        if (resserver.isSuccess() == false) {
                            res.setSuccess(true);
                            res.setServiceStatus("000");
                            res.setServiceDescription("内部结算失败！");
                            return;
                            //throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "内部结算失败！");
                        }
                    }

                    if("0".equals(docType)){

                        DCP_InterSettleDataGenerateReq inReq = new DCP_InterSettleDataGenerateReq();
                        inReq.setServiceId("DCP_InterSettleDataGenerate");
                        inReq.setToken(req.getToken());
                        //DCP_InterSettleDataGenerateReq.Detail detail = inReq.new Detail();
                        DCP_InterSettleDataGenerateReq.Request request1 = inReq.new Request();
                        request1.setOrganizationNo(req.getOrganizationNO());
                        request1.setBillNo(stockOutNO);
                        request1.setSupplyOrgNo(req.getRequest().getReceiptOrg());
                        request1.setReturnSupplyPrice("Y");
                        request1.setBillType(DCP_InterSettleDataGenerate.BillType.BillType_10003.getType());
                        request1.setDetail(new ArrayList<>());
                        for (DCP_StockOutCreateReq.level1Elm par : req.getRequest().getDatas()) {
							DCP_InterSettleDataGenerateReq.Detail detail = inReq.new Detail();
							detail.setReceiveOrgNo(req.getOrganizationNO());
                            detail.setSourceBillNo(par.getOfNo());
                            detail.setSourceItem(par.getoItem());
                            detail.setItem(String.valueOf(par.getItem()));
                            detail.setPluNo(par.getPluNo());
                            detail.setFeatureNo(par.getFeatureNo());
                            detail.setPUnit(par.getPunit());
                            detail.setPQty(String.valueOf(par.getPqty()));
                            detail.setReceivePrice(par.getDistriPrice());
                            detail.setReceiveAmt(par.getDistriAmt());
                            detail.setSupplyPrice("");
                            detail.setSupplyAmt("");
                            request1.getDetail().add(detail);
                        }
                        inReq.setRequest(request1);
                        ParseJson pj = new ParseJson();
                        String jsontemp = pj.beanToJson(inReq);

                        DispatchService ds = DispatchService.getInstance();
                        String resXml = ds.callService(jsontemp, StaticInfo.dao);
                        DCP_InterSettleDataGenerateRes resserver = pj.jsonToBean(resXml, new TypeToken<DCP_InterSettleDataGenerateRes>() {
                        });
                        if (resserver.isSuccess() == false) {
                            res.setSuccess(true);
                            res.setServiceStatus("000");
                            res.setServiceDescription("内部结算失败！");
                            return;
                            //throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "内部结算失败！");
                        }
                    }

					if("1".equals(docType)){
						DCP_InterSettleDataGenerateReq inReq = new DCP_InterSettleDataGenerateReq();
						inReq.setServiceId("DCP_InterSettleDataGenerate");
						inReq.setToken(req.getToken());
						//DCP_InterSettleDataGenerateReq.Detail detail = inReq.new Detail();
						DCP_InterSettleDataGenerateReq.Request request1 = inReq.new Request();
						request1.setOrganizationNo(req.getOrganizationNO());
						request1.setBillNo(stockOutNO);
						request1.setSupplyOrgNo("");
						request1.setReturnSupplyPrice("Y");
						request1.setBillType(DCP_InterSettleDataGenerate.BillType.BillType10005.getType());
						request1.setDetail(new ArrayList<>());
						for (DCP_StockOutCreateReq.level1Elm par : req.getRequest().getDatas()) {
							DCP_InterSettleDataGenerateReq.Detail detail = inReq.new Detail();
							detail.setReceiveOrgNo(req.getOrganizationNO());
							detail.setSourceBillNo(par.getOfNo());
							detail.setSourceItem(par.getoItem());
							detail.setItem(String.valueOf(par.getItem()));
							detail.setPluNo(par.getPluNo());
							detail.setFeatureNo(par.getFeatureNo());
							detail.setPUnit(par.getPunit());
							detail.setPQty(String.valueOf(par.getPqty()));
							detail.setReceivePrice("");
							detail.setReceiveAmt("");
							detail.setSupplyPrice("");
							detail.setSupplyAmt("");
							request1.getDetail().add(detail);

							if(!req.getOrganizationNO().equals(req.getRequest().getReceiptOrg())){
								detail = inReq.new Detail();

								detail.setReceiveOrgNo(req.getRequest().getReceiptOrg());
								detail.setSourceBillNo(par.getOfNo());
								detail.setSourceItem(par.getoItem());
								detail.setItem(String.valueOf(par.getItem()));
								detail.setPluNo(par.getPluNo());
								detail.setFeatureNo(par.getFeatureNo());
								detail.setPUnit(par.getPunit());
								detail.setPQty(String.valueOf(par.getPqty()));
								detail.setReceivePrice("");
								detail.setReceiveAmt("");
								detail.setSupplyPrice("");
								detail.setSupplyAmt("");
								request1.getDetail().add(detail);
							}

						}
						inReq.setRequest(request1);
						ParseJson pj = new ParseJson();
						String jsontemp = pj.beanToJson(inReq);

						DispatchService ds = DispatchService.getInstance();
						String resXml = ds.callService(jsontemp, StaticInfo.dao);
						DCP_InterSettleDataGenerateRes resserver = pj.jsonToBean(resXml, new TypeToken<DCP_InterSettleDataGenerateRes>() {
						});
						if (resserver.isSuccess() == false) {
							res.setSuccess(true);
							res.setServiceStatus("000");
							res.setServiceDescription("内部结算失败！");
							return;
							//throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "内部结算失败！");
						}
					}
                }

				
			} else {
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据已存在，请重新确认！ ");
			}
			
		//} catch (Exception e) {
		//	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		//}
		
	}
	
	@Override
	protected List<InsBean> prepareInsertData(DCP_StockOutCreateReq req) throws Exception {
		return null;
	}
	
	@Override
	protected List<UptBean> prepareUpdateData(DCP_StockOutCreateReq req) throws Exception {
		return null;
	}
	
	@Override
	protected List<DelBean> prepareDeleteData(DCP_StockOutCreateReq req) throws Exception {
		return null;
	}
	
	@Override
	protected String getQuerySql(DCP_StockOutCreateReq req) throws Exception {
		String sql = null;
		levelElm request = req.getRequest();
		StringBuffer sqlbuf = new StringBuffer();
		String bDate = PosPub.getAccountDate_SMS(dao, req.geteId(), req.getShopId());
		String stockOutNO = "";
		String docType = request.getDocType();
		if (docType.equals("1")) {
			stockOutNO = "DBCK" + bDate;//matter.format(dt);
		}
		if (docType.equals("0") || docType.equals("2") ) {
			stockOutNO = "THCK" + bDate;//matter.format(dt);
		}
		if (docType.equals("3")) {
			stockOutNO = "QTCK" + bDate;
		}
		if (docType.equals("4")) {
			stockOutNO = "YCCK" + bDate;
		}
		sqlbuf.append("" + "select stockOutNO  from ( " + "select max(stockOutNO) as  stockOutNO "
				+ "  from DCP_STOCKOUT " + " where OrganizationNO = ? " + " and EID = ? " + " and SHOPID = ? "
				+ " and stockOutNO like '%%" + stockOutNO + "%%' "); // 假資料
		sqlbuf.append(" ) TBL ");
		
		if (sqlbuf.length() > 0)
			sql = sqlbuf.toString();
		
		return sql;
	}

	private String getStockoutNoNew(DCP_StockOutCreateReq req) throws Exception {
		levelElm request = req.getRequest();

		String stockOutNO = null;
		String docType = request.getDocType();
		if (docType.equals("1")) {
			stockOutNO=getOrderNO(req,"DBCK");
		}
		if (docType.equals("0") || docType.equals("2")) {
			stockOutNO=getOrderNO(req,"THCK");
		}
		if (docType.equals("3")) {
			stockOutNO=getOrderNO(req,"QTCK");
		}
		if (docType.equals("4")) {
			stockOutNO=getOrderNO(req,"YCCK");
		}

		return stockOutNO;

	}
	
	private String getStockOutNO(DCP_StockOutCreateReq req) throws Exception {
		levelElm request = req.getRequest();
		String sql = null;
		String stockOutNO = null;
		String shopId = req.getShopId();
		String organizationNO = req.getOrganizationNO();
		String eId = req.geteId();
		String docType = request.getDocType();
		String bDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
		String[] conditionValues = { organizationNO, eId, shopId }; // 查询要货单号
		sql = this.getQuerySql(req);
		List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);
		
		if (getQData != null && getQData.isEmpty() == false) {
			
			stockOutNO = (String) getQData.get(0).get("STOCKOUTNO");
			
			if (stockOutNO != null && stockOutNO.length() > 0) {
				long i;
				stockOutNO = stockOutNO.substring(4);
				i = Long.parseLong(stockOutNO) + 1;
				stockOutNO = i + "";
				if (docType.equals("1")) {
					stockOutNO = "DBCK" + stockOutNO;
				}
				if (docType.equals("0") || docType.equals("2")) {
					stockOutNO = "THCK" + stockOutNO;
				}
				if (docType.equals("3")) {
					stockOutNO = "QTCK" + stockOutNO;
				}
				if (docType.equals("4")) {
					stockOutNO = "YCCK" + stockOutNO;
				}
			} else {
				if (docType.equals("1")) {
					stockOutNO = "DBCK" + bDate + "00001";
				}
				if (docType.equals("0") || docType.equals("2")) {
					stockOutNO = "THCK" + bDate + "00001";
				}
				if (docType.equals("3")) {
					stockOutNO = "QTCK" + bDate + "00001";
				}
				if (docType.equals("4")) {
					stockOutNO = "YCCK" + bDate + "00001";
				}
			}
		} else {
			if (docType.equals("1")) {
				stockOutNO = "DBCK" + bDate + "00001";
			}
			if (docType.equals("0") || docType.equals("2") ) {
				stockOutNO = "THCK" + bDate + "00001";
			}
			if (docType.equals("3")) {
				stockOutNO = "QTCK" + bDate + "00001";
			}
			if (docType.equals("4")) {
				stockOutNO = "YCCK" + bDate + "00001";
			}
		}
		
		return stockOutNO;
	}
	
	private boolean checkGuid(DCP_StockOutCreateReq req) throws Exception {
		levelElm request = req.getRequest();
		String guid = request.getStockOutID();
		boolean existGuid;
		String	sql = "select StockOut_ID "
				+ " from DCP_StockOut "
				+ " where StockOut_ID = '"+guid+"' ";
		
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		if (getQData != null && getQData.isEmpty() == false) {
			existGuid = true;
		} else {
			existGuid =  false;
		}
		
		return existGuid;
	}
	
}
