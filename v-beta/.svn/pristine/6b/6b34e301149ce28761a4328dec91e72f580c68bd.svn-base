package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.dsc.spos.json.cust.req.DCP_StockOutDetailQueryReq;
import com.dsc.spos.json.cust.req.DCP_StockOutDetailQueryReq.levelElm;
import com.dsc.spos.json.cust.res.DCP_StockOutDetailQueryRes;
import com.dsc.spos.json.cust.res.DCP_StockOutQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 服务名：DCP_StockOutDetailQuery
 * 服务说明：出库单明细查询
 * @author jinzma
 * @since 2020-06-23
 */
public class DCP_StockOutDetailQuery  extends SPosBasicService<DCP_StockOutDetailQueryReq,DCP_StockOutDetailQueryRes> {
	
	@Override
	protected boolean isVerifyFail(DCP_StockOutDetailQueryReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();
		levelElm request = req.getRequest();
		String stockOutNo = request.getStockOutNo();
		
		if (Check.Null(stockOutNo)) {
			errMsg.append("单号不可为空, ");
			isFail = true;
		}
		
		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		return false;
	}
	
	@Override
	protected TypeToken<DCP_StockOutDetailQueryReq> getRequestType() {
		return new TypeToken<DCP_StockOutDetailQueryReq>(){};
	}
	
	@Override
	protected DCP_StockOutDetailQueryRes getResponseType() {
		return new DCP_StockOutDetailQueryRes();
	}
	
	@Override
	protected DCP_StockOutDetailQueryRes processJson(DCP_StockOutDetailQueryReq req) throws Exception {
		DCP_StockOutDetailQueryRes res = this.getResponse();
		try
		{
			//查询

            res.setDatas(new ArrayList<>());
            String mainSql=this.getStockoutSql(req);
            List<Map<String, Object>> getMData = this.doQueryData(mainSql,null);
            if(getMData.size()>0){
                Map<String, Object> oneData = getMData.get(0);
                String isBatchManager = PosPub.getPARA_SMS(dao, req.geteId(), "", "IS_BatchNo");

                String shopId = oneData.get("SHOPID").toString();
                String stockOutNO = oneData.get("STOCKOUTNO").toString();
                String bDate = oneData.get("BDATE").toString();
                String processERPNo = oneData.get("PROCESSERPNO").toString();
                String memo = oneData.get("MEMO").toString();
                String status = oneData.get("STATUS").toString();
                String docType = oneData.get("DOCTYPE").toString();
                String transferShop=oneData.get("TRANSFERSHOP").toString();
                String transferShopName=oneData.get("TRANSFERSHOPNAME").toString();
                String oType = oneData.get("OTYPE").toString();
                String ofNO = oneData.get("OFNO").toString();
                String bsNO = oneData.get("BSNO").toString();
                String bsName = oneData.get("BSNAME").toString();
                String warehouse = oneData.get("WAREHOUSE").toString();
                String warehouseName = oneData.get("WAREHOUSENAME").toString();
                String transferWarehouse = oneData.get("TRANSFERWAREHOUSE").toString();
                String transferWarehouseName = oneData.get("TRANSFERWAREHOUSENAME").toString();
                String loadDocType = oneData.get("LOADDOCTYPE").toString();
                String loadDocNO = oneData.get("LOADDOCNO").toString();
                String createBy = oneData.get("CREATEBY").toString();
                String createDate = oneData.get("CREATEDATE").toString();
                String createTime = oneData.get("CREATETIME").toString();
                String createByName = oneData.get("CREATEBYNAME").toString();
                String confirmBy = oneData.get("CONFIRMBY").toString();
                String confirmDate = oneData.get("CONFIRMDATE").toString();
                String confirmTime = oneData.get("CONFIRMTIME").toString();
                String confirmByName = oneData.get("CONFIRMBYNAME").toString();
                String accountBy = oneData.get("ACCOUNTBY").toString();
                String accountDate = oneData.get("ACCOUNTDATE").toString();
                String accountTime = oneData.get("ACCOUNTTIME").toString();
                String accountByName = oneData.get("ACCOUNTBYNAME").toString();
                String cancelBy = oneData.get("CANCELBY").toString();
                String cancelDate = oneData.get("CANCELDATE").toString();
                String cancelTime = oneData.get("CANCELTIME").toString();
                String cancelByName = oneData.get("CANCELBYNAME").toString();
                String modifyBy = oneData.get("MODIFYBY").toString();
                String modifyDate = oneData.get("MODIFYDATE").toString();
                String modifyTime = oneData.get("MODIFYTIME").toString();
                String modifyByName = oneData.get("MODIFYBYNAME").toString();
                String submitBy = oneData.get("SUBMITBY").toString();
                String submitDate = oneData.get("SUBMITDATE").toString();
                String submitTime = oneData.get("SUBMITTIME").toString();
                String submitByName = oneData.get("SUBMITBYNAME").toString();
                String receiptOrg = oneData.get("RECEIPTORG").toString();
                String receiptOrgName = oneData.get("ORGNAME").toString();
                String totPqty = oneData.get("TOTPQTY").toString();
                String totAmt = oneData.get("TOTAMT").toString();
                String totCqty = oneData.get("TOTCQTY").toString();
                String totDistriAmt = oneData.get("TOT_DISTRIAMT").toString();
                String diffStatus = oneData.get("DIFFSTATUS").toString();
                String deliveryNO = oneData.get("DELIVERYNO").toString();
                String pTemplateNo = oneData.get("PTEMPLATENO").toString();
                String pTemplateName = oneData.get("PTEMPLATENAME").toString();
                String sourceMenu = oneData.get("SOURCEMENU").toString();
                String stockoutno_origin = oneData.get("STOCKOUTNO_ORIGIN").toString();
                String stockoutno_refund = oneData.get("STOCKOUTNO_REFUND").toString();

                String employeeid = oneData.get("EMPLOYEEID").toString();
                String employeename = oneData.get("EMPLOYEENAME").toString();
                String departid = oneData.get("DEPARTID").toString();
                String departname = oneData.get("DEPARTNAME").toString();
                String receiptdate = oneData.get("RECEIPTDATE").toString();
                String packingno = oneData.get("PACKINGNO").toString();
                String invwarehouse = oneData.get("INVWAREHOUSE").toString();
                String invwarehousename = oneData.get("INVWAREHOUSENAME").toString();
                String islocation = oneData.get("ISLOCATION").toString();

                String deliverydate = oneData.get("DELIVERYDATE").toString();
                String ootype = oneData.get("OOTYPE").toString();
                String oofno = oneData.get("OOFNO").toString();
                String istraninconfirm = oneData.get("ISTRANINCONFIRM").toString();
                String stockInNo = oneData.get("STOCKINNO").toString();
                String receiptwhislocation = oneData.get("RECEIPTWHISLOCATION").toString();

                DCP_StockOutDetailQueryRes.Datas oneLv1 = res.new Datas();

                oneLv1.setCorp(oneData.get("CORP").toString());
                oneLv1.setReceiptCorp(oneData.get("RECEIPTCORP").toString());
                // 處理調整回傳值；
                oneLv1.setShopId(shopId);
                oneLv1.setStockOutNo(stockOutNO);
                oneLv1.setBDate(bDate);
                oneLv1.setProcessERPNo(processERPNo);
                oneLv1.setMemo(memo);
                oneLv1.setStatus(status);
                oneLv1.setDocType(docType);
                oneLv1.setBsNo(bsNO);
                oneLv1.setBsName(bsName);
                oneLv1.setWarehouse(warehouse);
                oneLv1.setWarehouseName(warehouseName);
                oneLv1.setTransferWarehouse(transferWarehouse);
                oneLv1.setTransferWarehouseName(transferWarehouseName);
                oneLv1.setTransferShop(transferShop);
                oneLv1.setTransferShopName(transferShopName);
                oneLv1.setOType(oType);
                oneLv1.setOfNo(ofNO);
                oneLv1.setLoadDocType(loadDocType);
                oneLv1.setLoadDocNo(loadDocNO);
                oneLv1.setCreateBy(createBy);
                oneLv1.setCreateDate(createDate);
                oneLv1.setCreateTime(createTime);
                oneLv1.setCreateByName(createByName);
                oneLv1.setAccountBy(accountBy);
                oneLv1.setAccountDate(accountDate);
                oneLv1.setAccountTime(accountTime);
                oneLv1.setAccountByName(accountByName);
                oneLv1.setReceiptOrg(receiptOrg);
                oneLv1.setReceiptOrgName(receiptOrgName);
                oneLv1.setConfirmBy(confirmBy);
                oneLv1.setConfirmDate(confirmDate);
                oneLv1.setConfirmTime(confirmTime);
                oneLv1.setConfirmByName(confirmByName);
                oneLv1.setAccountBy(accountBy);
                oneLv1.setAccountDate(accountDate);
                oneLv1.setAccountTime(accountTime);
                oneLv1.setAccountByName(accountByName);
                oneLv1.setCancelBy(cancelBy);
                oneLv1.setCancelDate(cancelDate);
                oneLv1.setCancelTime(cancelTime);
                oneLv1.setCancelByName(cancelByName);
                oneLv1.setModifyBy(modifyBy);
                oneLv1.setModifyDate(modifyDate);
                oneLv1.setModifyTime(modifyTime);
                oneLv1.setModifyByName(modifyByName);
                oneLv1.setSubmitBy(submitBy);
                oneLv1.setSubmitDate(submitDate);
                oneLv1.setSubmitTime(submitTime);
                oneLv1.setSubmitByName(submitByName);
                oneLv1.setTotPqty(totPqty);
                oneLv1.setTotAmt(totAmt);
                oneLv1.setTotCqty(totCqty);
                oneLv1.setTotDistriAmt(totDistriAmt);
                oneLv1.setDeliveryNo(deliveryNO);
                oneLv1.setDiffStatus(diffStatus);
                oneLv1.setUpdate_time(oneData.get("UPDATE_TIME").toString());
                oneLv1.setProcess_status(oneData.get("PROCESS_STATUS").toString());
                oneLv1.setPTemplateNo(pTemplateNo);
                oneLv1.setPTemplateName(pTemplateName);
                oneLv1.setSourceMenu(sourceMenu);
                oneLv1.setStockOutNo_origin(stockoutno_origin);
                oneLv1.setStockOutNo_refund(stockoutno_refund);

                //【ID1036371】【浙江意诺V9203】‘个案评估’ 退货出库，驳回增加原因，并可以让门店知道这个驳回原因，需要有报表查询----中台服务端 by jinzma 20231010
                oneLv1.setRejectReason(oneData.get("REJECTREASON").toString());

                //【ID1037888】[红房子3.0]订单和调拨单记录配送员，配送员电话，统计出来后用于计算配送人员工资，之前易成用的是（易成用的要货发货单功能）--服务端 by jinzma 20231215
                oneLv1.setDeliveryBy(oneData.get("DELIVERYBY").toString());
                oneLv1.setDeliveryName(oneData.get("DELIVERYNAME").toString());
                oneLv1.setDeliveryTel(oneData.get("DELIVERYTEL").toString());
                oneLv1.setEmployeeId(employeeid);
                oneLv1.setEmployeeName(employeename);
                oneLv1.setDepartId(departid);
                oneLv1.setDepartName(departname);
                oneLv1.setReceiptDate(receiptdate);
                oneLv1.setPackingNo(packingno);
                oneLv1.setInvWarehouse(invwarehouse);
                oneLv1.setInvWarehouseName(invwarehousename);
                oneLv1.setIsLocation(islocation);
                oneLv1.setIsBatchManage(isBatchManager);
                oneLv1.setDeliveryDate(deliverydate);
                oneLv1.setOoType(ootype);
                oneLv1.setOofNo(oofno);
                oneLv1.setIsTranInConfirm(istraninconfirm);

                oneLv1.setCreateDateTime(PosPub.combineDateTime(oneLv1.getCreateDate(), oneLv1.getCreateTime()));
                oneLv1.setAccountDateTime(PosPub.combineDateTime(oneLv1.getAccountDate(), oneLv1.getAccountTime()));
                oneLv1.setConfirmDateTime(PosPub.combineDateTime(oneLv1.getConfirmDate(), oneLv1.getConfirmTime()));
                oneLv1.setCancelDateTime(PosPub.combineDateTime(oneLv1.getCancelDate(), oneLv1.getCancelTime()));
                oneLv1.setModifyDateTime(PosPub.combineDateTime(oneLv1.getModifyDate(), oneLv1.getModifyTime()));
                oneLv1.setSubmitDateTime(PosPub.combineDateTime(oneLv1.getSubmitDate(), oneLv1.getSubmitTime()));
                oneLv1.setStockInNo(new ArrayList<>());
                oneLv1.setReceiptWHIsLocation(receiptwhislocation);

                oneLv1.setDetail(new ArrayList<>());
                oneLv1.setBatchList(new ArrayList<>());

                String stockInSql= " select a.stockinno,a.STOCKINNO_ORIGIN  " +
                        " from dcp_stockin a " +
                        " where a.eid='"+req.geteId()+"' and a.oofno='"+req.getRequest().getStockOutNo()+"' ";
                List<Map<String, Object>> stockInList = this.doQueryData(stockInSql, null);
                for (Map<String, Object> singleRows : stockInList){
                    DCP_StockOutDetailQueryRes.StockInInfo stockInInfo = res.new StockInInfo();
                    stockInInfo.setStockInNo(singleRows.get("STOCKINNO").toString());
                    stockInInfo.setStockInNo_origin(singleRows.get("STOCKINNO_ORIGIN").toString());
                    oneLv1.getStockInNo().add(stockInInfo);
                }


                String sql = this.getQuerySql(req);				//查询明细数据
                List<Map<String, Object>> getQData = this.doQueryData(sql,null);

                String transferSql=this.getTransferQuerySql(req);
                List<Map<String, Object>> transList = this.doQueryData(transferSql, null);

                if (getQData != null && !getQData.isEmpty())
                {
                    // 有資料，取得詳細內容
                    // 拼接返回图片路径  by jinzma 20210705
                    String isHttps=PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
                    String httpStr=isHttps.equals("1")?"https://":"http://";
                    String domainName=PosPub.getPARA_SMS(dao, req.geteId(), "", "DomainName");
                    if (domainName.endsWith("/")) {
                        domainName = httpStr + domainName + "resource/image/";
                    }else{
                        domainName = httpStr + domainName + "/resource/image/";
                    }
                    //单头主键字段
                    Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查询条件
                    condition.put("ITEM", true);
                    //调用过滤函数
                    List<Map<String, Object>> getQHeader = MapDistinct.getMap(getQData, condition);

                    condition.put("G_ITEM", true);
                    List<Map<String, Object>> getQDataImage = MapDistinct.getMap(getQData, condition);

                    for (Map<String, Object> oneDetailData : getQHeader) {
                        DCP_StockOutDetailQueryRes.level1Elm oneDetailLv1 = res.new level1Elm();
                        oneDetailLv1.setImageList(new ArrayList<>());
                        String item = oneDetailData.get("ITEM").toString();
                        String oItem = oneDetailData.get("OITEM").toString();
                        String pluNO = oneDetailData.get("PLUNO").toString();
                        String pluName = oneDetailData.get("PLUNAME").toString();
                        String spec = oneDetailData.get("SPEC").toString();
                        String bsNODetail = oneDetailData.get("BSNO").toString();
                        String bsNameDetail=oneDetailData.get("BSNAME").toString();
                        String punit = oneDetailData.get("PUNIT").toString();
                        String punitName = oneDetailData.get("PUNITNAME").toString();
                        String pqty = oneDetailData.get("PQTY").toString();
                        String baseunit = oneDetailData.get("BASEUNIT").toString();
                        String baseunitName = oneDetailData.get("BASEUNITNAME").toString();
                        String baseqty = oneDetailData.get("BASEQTY").toString();
                        String unitRatio = oneDetailData.get("UNITRATIO").toString();
                        String price = oneDetailData.get("PRICE").toString();
                        String amt = oneDetailData.get("AMT").toString();
                        String distriAmt = oneDetailData.get("DISTRIAMT").toString();
                        String listImage = oneDetailData.get("LISTIMAGE").toString();
                        if (!Check.Null(listImage)){
                            listImage = domainName+listImage;
                        }
                        String pluBarcode = oneDetailData.get("PLUBARCODE").toString();
                        String FPUNIT=oneDetailData.get("FPUNIT").toString();
                        String RETWQTY = oneDetailData.get("RETWQTY").toString();
                        String FUNIT_RATIO=oneDetailData.get("FUNIT_RATIO").toString();
                        String FPQTY = oneDetailData.get("FPQTY").toString();
                        String RQTY = oneDetailData.get("RQTY").toString();
                        String pluMemo = oneDetailData.get("PLUMEMO").toString();
                        String oTypeDetail = oneDetailData.get("OTYPE").toString();
                        /////返回收货单取已退货的单位和数量
                        if (Check.Null(RETWQTY))
                            RETWQTY="0";
                        if (Check.Null(FUNIT_RATIO))
                            FUNIT_RATIO="1";
                        float fRETWQTY=new BigDecimal(RETWQTY).divide(new BigDecimal(FUNIT_RATIO), RoundingMode.HALF_UP).floatValue();

                        String detailWarehouse = oneDetailData.get("WAREHOUSE").toString();
                        String detailWarehouseName = oneDetailData.get("WAREHOUSENAME").toString();
                        String batchNO = oneDetailData.get("BATCH_NO").toString();
                        String prodDate = oneDetailData.get("PROD_DATE").toString();
                        String distriPrice = oneDetailData.get("DISTRIPRICE").toString();
                        String isBatch = oneDetailData.get("ISBATCH").toString();
                        String punitUDLength = oneDetailData.get("PUNIT_UDLENGTH").toString();
                        String featureNo = oneDetailData.get("FEATURENO").toString();
                        String featureName = oneDetailData.get("FEATURENAME").toString();
                        String gunitName = oneDetailData.get("GUNITNAME").toString();
                        String stockmanagetype = oneDetailData.get("STOCKMANAGETYPE").toString();
                        String stockqty = oneDetailData.get("STOCKQTY").toString();
                        String stockoutQty = oneDetailData.get("STOCKOUTQTY").toString();
                        String noticePqty = oneDetailData.get("NOTICEPQTY").toString();
                        if(!PosPub.isNumericTypeMinus(stockqty)) {
                            stockqty="999999";
                        }

                        // 處理調整回傳值；
                        oneDetailLv1.setItem(item);
                        oneDetailLv1.setOItem(oItem);
                        oneDetailLv1.setPluNo(pluNO);
                        oneDetailLv1.setPluName(pluName);
                        oneDetailLv1.setSpec(spec);
                        oneDetailLv1.setListImage(listImage);
                        oneDetailLv1.setPunit(punit);
                        oneDetailLv1.setPunitName(punitName);
                        oneDetailLv1.setPqty(pqty);
                        oneDetailLv1.setBaseUnit(baseunit);
                        oneDetailLv1.setBaseUnitName(baseunitName);
                        oneDetailLv1.setBaseQty(baseqty);
                        oneDetailLv1.setUnitRatio(unitRatio);
                        oneDetailLv1.setPrice(price);
                        oneDetailLv1.setAmt(amt);
                        oneDetailLv1.setDistriAmt(distriAmt);
                        oneDetailLv1.setPluBarcode(pluBarcode);
                        oneDetailLv1.setRoutunit(FPUNIT);
                        oneDetailLv1.setRoutqty(String.valueOf(fRETWQTY));
                        oneDetailLv1.setOqty(FPQTY);
                        oneDetailLv1.setRqty(RQTY);
                        oneDetailLv1.setRoutunitName(gunitName);
                        oneDetailLv1.setBsNo(bsNODetail);
                        oneDetailLv1.setBsName(bsNameDetail);
                        oneDetailLv1.setPluMemo(pluMemo);
                        oneDetailLv1.setBatchNo(batchNO);
                        oneDetailLv1.setProdDate(prodDate);
                        oneDetailLv1.setDistriPrice(distriPrice);
                        oneDetailLv1.setIsBatch(isBatch);
                        oneDetailLv1.setPunitUdLength(punitUDLength);
                        oneDetailLv1.setFeatureNo(featureNo);
                        oneDetailLv1.setFeatureName(featureName);
                        oneDetailLv1.setStockManageType(stockmanagetype);
                        oneDetailLv1.setStockqty(stockqty);
                        //【ID1027675】【荷家3.0】200001门店，完工单号WGRK2022070600001传到ERP完工数量是0   by jinzma 20221109
                        oneDetailLv1.setBaseUnitUdLength(oneDetailData.get("BASEUNITUDLENGTH").toString());
                        oneDetailLv1.setItem_origin(oneDetailData.get("ITEM_ORIGIN").toString());
                        oneDetailLv1.setPqty_origin(oneDetailData.get("PQTY_ORIGIN").toString());
                        oneDetailLv1.setPqty_refund(oneDetailData.get("PQTY_REFUND").toString());
                        oneDetailLv1.setWarehouse(detailWarehouse);
                        oneDetailLv1.setWarehouseName(detailWarehouseName);
                        oneDetailLv1.setOType(oTypeDetail);

                        if("3".equals(oType)||"4".equals(oType)||"5".equals(oType)||"6".equals(oType)){//通知单
                            BigDecimal subtract = new BigDecimal(noticePqty).subtract(new BigDecimal(stockoutQty));
                            oneDetailLv1.setOqty(subtract.toString());
                        }


                        //指定商品的图片
                        if (getQDataImage!=null&&!getQDataImage.isEmpty())
                        {
                            Map<String, Object> condDetailImage=new HashMap<>();
                            condDetailImage.put("ITEM", item);
                            List<Map<String, Object>> getDetailImage = MapDistinct.getWhereMap(getQDataImage, condDetailImage, true);
                            for (Map<String, Object> mapImage : getDetailImage)
                            {
                                String g_image = mapImage.getOrDefault("G_IMAGE","").toString();
                                String g_item = mapImage.getOrDefault("G_ITEM","").toString();
                                if (g_image.isEmpty())
                                {
                                    continue;
                                }
                                DCP_StockOutDetailQueryRes.level2Elm onelv2 = res.new level2Elm();
                                onelv2.setItem(g_item);
                                onelv2.setImage(g_image);
                                oneDetailLv1.getImageList().add(onelv2);
                            }

                        }

                        oneDetailLv1.setTransInLocationList(new ArrayList<>());
                        List<Map<String, Object>> transInList = transList.stream().filter(x -> x.get("OITEM").toString().equals(oneDetailLv1.getItem())).collect(Collectors.toList());
                        for (Map<String, Object> transInData : transInList)
                        {
                            DCP_StockOutDetailQueryRes.TransInLocationList trans = res.new TransInLocationList();
                            trans.setItem(transInData.get("ITEM").toString());
                            trans.setOItem(oneDetailLv1.getItem());
                            trans.setTransInLocation(transInData.get("TRANSINLOCATION").toString());
                            trans.setTransInLocationName(transInData.get("TRANSINLOCATIONNAME").toString());
                            trans.setTransInQty(transInData.get("TRANSINQTY").toString());
                            trans.setPUnit(oneDetailLv1.getPunit());
                            trans.setPUnitName(oneDetailLv1.getPunitName());
                            trans.setBaseUnit(oneDetailLv1.getBaseUnit());
                            trans.setBaseUnitName(oneDetailLv1.getBaseUnitName());
                            trans.setBaseQty(transInData.get("BASEQTY").toString());
                            trans.setUnitRatio(transInData.get("UNITRATIO").toString());
                            oneDetailLv1.getTransInLocationList().add(trans);
                        }

                        oneDetailLv1.setPackingNo(oneDetailData.get("PACKINGNO").toString());
                        oneDetailLv1.setLocation(oneDetailData.get("LOCATION").toString());
                        oneDetailLv1.setLocationName(oneDetailData.get("LOCATIONNAME").toString());
                        oneDetailLv1.setExpDate(oneDetailData.get("EXPDATE").toString());
                        oneDetailLv1.setOfNo(oneDetailData.get("OFNO").toString());
                        oneDetailLv1.setOoType(oneDetailData.get("OOTYPE").toString());
                        oneDetailLv1.setOofNo(oneDetailData.get("OOFNO").toString());
                        oneDetailLv1.setOoItem(oneDetailData.get("OOITEM").toString());
                        oneDetailLv1.setStoctOutNoQty(oneDetailData.get("STOCKOUTNOQTY").toString());
                        oneDetailLv1.setTransferBatchNo(oneDetailData.get("TRANSFERBATCHNO").toString());
                        oneLv1.getDetail().add(oneDetailLv1);
                    }
                }
                else
                {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"未查询到明细资料，请重新查询！");
                }

                //查询batch信息
                String stockoutBatchSql = this.getStockoutBatchSql(req);
                List<Map<String, Object>> getQDataBatch = this.doQueryData(stockoutBatchSql,null);
                if(getQDataBatch!=null&&getQDataBatch.size()>0){
                    for (Map<String, Object> oneBatchData : getQDataBatch)
                    {
                        DCP_StockOutDetailQueryRes.BatchList batchData = res.new BatchList();
                        batchData.setItem(oneBatchData.get("ITEM").toString());
                        batchData.setItem(oneBatchData.get("ITEM").toString());
                        batchData.setItem2(oneBatchData.get("ITEM2").toString());
                        batchData.setPluNo(oneBatchData.get("PLUNO").toString());
                        batchData.setPluName(oneBatchData.get("PLUNAME").toString());
                        batchData.setFeatureNo(oneBatchData.get("FEATURENO").toString());
                        batchData.setFeatureName(oneBatchData.get("FEATURENAME").toString());
                        batchData.setWarehouse(oneBatchData.get("WAREHOUSE").toString());
                        batchData.setWarehouseName(oneBatchData.get("WAREHOUSENAME").toString());
                        batchData.setLocation(oneBatchData.get("LOCATION").toString());
                        batchData.setLocationName(oneBatchData.get("LOCATIONNAME").toString());
                        batchData.setBatchNo(oneBatchData.get("BATCHNO").toString());
                        batchData.setProdDate(oneBatchData.get("PRODDATE").toString());
                        batchData.setExpDate(oneBatchData.get("EXPDATE").toString());
                        batchData.setPUnit(oneBatchData.get("PUNIT").toString());
                        batchData.setPUnitName(oneBatchData.get("PUNITNAME").toString());
                        batchData.setPQty(oneBatchData.get("PQTY").toString());
                        batchData.setBaseUnit(oneBatchData.get("BASEUNIT").toString());
                        batchData.setBaseUnitName(oneBatchData.get("BASEUNITNAME").toString());
                        batchData.setBaseQty(oneBatchData.get("BASEQTY").toString());
                        batchData.setSpec(oneBatchData.get("SPEC").toString());
                        oneLv1.getBatchList().add(batchData);


                    }
                }


                res.getDatas().add(oneLv1);
            }

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
			return res;
			
		}
		catch (Exception e)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
		
	}

    private String getTransferQuerySql(DCP_StockOutDetailQueryReq req) {
        String stockOutNo = req.getRequest().getStockOutNo();

        StringBuffer sb=new StringBuffer();
        sb.append("select a.item,a.oitem,a.location as transinlocation,c.locationname as transinlocationname,a.pqty as transinqty,a.baseqty,a.unit_ratio as unitratio   from DCP_STOCKOUT_DETAIL_LOCATION a " +
                " left join dcp_stockout_detail b on b.eid=a.eid and b.organizationno=a.organizationno and a.stockoutno=b.stockoutno and a.oitem =b.item " +
                "   left join dcp_stockout d on d.eid=b.eid and d.organizationno=b.organizationno and d.stockoutno=b.stockoutno " +
                " left join dcp_location c on a.eid=c.eid and a.location=c.location and d.transfer_warehouse=c.warehouse and a.organizationno=c.organizationno " +
                " where a.eid='"+req.geteId()+"' and a.organizationno='"+req.getOrganizationNO()+"' " +
                " and a.stockoutno='"+stockOutNo+"' ");

        return sb.toString();
    }

    @Override
	protected void processRow(Map<String, Object> row) throws Exception {
	}
	
	@Override
	protected String getQuerySql(DCP_StockOutDetailQueryReq req) throws Exception {
		StringBuffer sqlbuf = new StringBuffer();
		levelElm request = req.getRequest();
		String eId=req.geteId();
		String shopId = req.getShopId();
		String stockoutNo = request.getStockOutNo();
		String ofNo =request.getOfNo();
		String langType=req.getLangType();
		
		//这里如果有ofno 需要关联一下收货单取已退货的单位和数量
		sqlbuf.append(""
				+ " SELECT a.ITEM,a.OITEM ,a.PLUNO ,c.PLU_NAME as pluName ,gul.SPEC ,image.listimage,b.ISBATCH,a.baseUNIT, "
				+ " a.PUNIT,a.PQTY,a.baseQTY,"
				+ " a.UNIT_RATIO as unitRatio,a.PRICE,a.AMT,a.DISTRIAMT,a.PLU_BARCODE as pluBarcode, "
				+ " d.uname as baseUnitName,e.uname as punitName,f.punit FPUNIT,f.RETWQTY RETWQTY,f.UNIT_RATIO FUNIT_RATIO,"
				+ " f.pqty FPQTY,g.uname gunitname,a.rqty ,a.BSNO,L.REASON_NAME as bsname, a.plu_Memo as pluMemo,  "
				+ " a.BATCH_NO,a.PROD_DATE,a.DISTRIPRICE,H.udlength as PUNIT_UDLENGTH,a.featureno,fn.featurename,a.STOCKQTY,b.STOCKMANAGETYPE,"
				+ " bul.udlength as baseunitudlength,a.PQTY_ORIGIN,a.PQTY_REFUND,a.ITEM_ORIGIN, "
                + " IMG.IMAGE as G_IMAGE,IMG.ITEM as G_ITEM ,IMG.OITEM as G_OITEM,i.warehouse ,i.warehouse_name as warehousename,a.packingno,a.mes_location as location,a.ofno,a.otype,a.ootype,a.expdate,  "
                + " a.oofno,a.ooitem,a.STOCKOUTNOQTY,j.LOCATIONNAME,nvl(k.stockoutqty,0) as stockoutqty,nvl(k.pqty,0) as noticepqty,a.TRANSFER_BATCHNO as transferBatchNo "
				+ " from DCP_STOCKOUT_DETAIL a "
				+ " INNER JOIN DCP_GOODS b ON a.PLUNO=b.PLUNO AND a.EID=b.EID "
				+ " left JOIN DCP_GOODS_LANG  c ON a.PLUNO=c.PLUNO AND a.EID=c.EID "
				+ " left JOIN DCP_UNIT_LANG d ON a.baseUNIT=d.UNIT AND a.EID=d.EID  AND d.LANG_TYPE ='"+langType+"'  "
				+ " left JOIN DCP_UNIT_LANG e ON a.PUNIT=e.UNIT AND a.EID=e.EID  AND e.LANG_TYPE = '"+langType+"'  "
				+ " left join DCP_STOCKIN_DETAIL f on f.STOCKINNO='"+ofNo+"' and a.EID=f.EID and a.SHOPID=f.SHOPID  and a.oitem=f.oitem "
				+ " left join DCP_UNIT_lang g on f.EID=g.EID and f.punit=g.unit and g.lang_type='"+langType+"' "
				+ " left join DCP_REASON_LANG L ON A.EID=L.EID AND A.BSNO=L.BSNO AND L.BSTYPE='2' and L.lang_type= '" + langType + "'"
				+ " left JOIN DCP_UNIT h ON a.PUNIT=h.UNIT AND a.EID=h.EID  "
                + " left JOIN DCP_warehouse_lang i ON a.warehouse=i.warehouse AND a.EID=i.EID and a.organizationno=i.organizationno and i.lang_type='"+langType+"' "
				+ " left join dcp_unit bul on a.eid=bul.eid and a.baseunit=bul.unit"
				+ " left join dcp_goodsimage image on image.eid=a.eid and image.pluno=a.pluno and image.apptype='ALL' "
				+ " left join DCP_GOODS_UNIT_LANG gul on a.eid=gul.eid and a.pluno=gul.pluno and a.punit=gul.ounit and gul.lang_type='"+langType+"' "
				+ " left join DCP_GOODS_FEATURE_LANG fn on a.eid=fn.eid and a.pluno=fn.pluno and a.featureno=fn.featureno  and fn.lang_type='"+req.getLangType()+"' "
                + " left join DCP_STOCKOUT_DETAIL_IMAGE IMG on a.eid=IMG.eid and a.SHOPID=IMG.SHOPID and a.item=IMG.oitem and a.STOCKOUTNO=IMG.STOCKOUTNO  "
                + " left join DCP_LOCATION j on j.eid=a.eid and j.organizationno=a.organizationno and j.warehouse=a.warehouse and j.location=a.mes_location " +
                " left join DCP_STOCKOUTNOTICE_DETAIL k on k.eid=a.eid and k.billno=a.ofno and k.item=a.oitem "
				+ " where a.SHOPID = '"+shopId+"' AND a.STOCKOUTNO = '"+stockoutNo+"'  AND a.EID = '"+eId+"' "
				+ " AND c.LANG_TYPE = '"+langType+"'  "
				+ " ORDER BY a.Item "
		);
		
		return sqlbuf.toString();
		
	}



    private String getStockoutSql(DCP_StockOutDetailQueryReq req) throws Exception {
        DCP_StockOutDetailQueryReq.levelElm request = req.getRequest();
        String sql = null;
        StringBuffer sqlbuf = new StringBuffer("");
        String langType = req.getLangType();
        String stockOutNo = request.getStockOutNo();
        //計算起啟位置

        sqlbuf.append(" SELECT A.STOCKOUTNO as stockOutNO,A.BSNO,L.REASON_NAME as bsName,a.receipt_org as receiptOrg,"
                + " z.org_name as orgName,A.SHOPID as SHOPID,A.EID as EID, A.BDATE as bDate,A.MEMO as memo,"
                + " A.STATUS as status,A.DOC_TYPE as docType, "
                + " A.OTYPE as oType,A.OFNO as ofNO,A.LOAD_DOCTYPE as loadDocType,A.LOAD_DOCNO as loadDocNO,A.CREATEBY as createBy, "
                + "	A.TRANSFER_SHOP as transferShop, "
                + " B.op_name as createByName,C.SHOPNAME as transferShopName,A.TOT_PQTY as totPqty,A.TOT_AMT as totAmt,"
                + " A.TOT_CQTY as totCqty,A.TOT_DISTRIAMT,A.DELIVERY_NO as deliveryNO,I.status as diffStatus,"
                + " A.warehouse as warehouse,w.warehouse_Name as warehouseName,A.transfer_Warehouse as transferWarehouse,"
                + " t.warehouse_Name as transferWarehouseName,A.UPDATE_TIME,A.PROCESS_STATUS, "
                + " a.pTemplateNo , p.pTemplate_Name as pTemplateName, "
                + " A.create_Date as createDate, A.create_Time as createTime,"
                + " a.ModifyBy , a.modify_Date as modifyDate ,  a.modify_Time as modifyTime , "
                + " a.confirmBy, a.confirm_Date as confirmDate , a.Confirm_Time as ConfirmTime,   "
                + " a.cancelBy , a.cancel_Date as cancelDate , a.cancel_Time as cancelTime , "
                + " a.accountBY ,a.account_Date as accountDate, a.account_Time as accountTime,"
                + " a.submitBy, a.submit_Date as submitDate, a.submit_Time as submitTime , "
                + " f1.op_name as modifyByName ,f2.op_name as cancelByName , f3.op_name as ConfirmByName ,  "
                + " f4.op_name as submitByName,  f5.OP_name as accountByName, "
                + " a.process_erp_no as processERPNo,a.sourcemenu,A.STOCKOUTNO_ORIGIN,A.STOCKOUTNO_REFUND,a.rejectreason,"
                + " a.deliveryby,dm.opname as deliveryname,dm.phone as deliverytel,a.employeeid,em0.name as employeename,a.departid,dd0.departname," +
                " a.RECEIPTDATE,a.PACKINGNO ,a.INVWAREHOUSE,iwl.warehouse_name as invWarehouseName,w1.ISLOCATION,a.deliveryDate,a.ooType,a.oofNo,a.isTranInConfirm,ds.stockinno,t1.islocation as receiptWHIsLocation,a.corp,a.receiptcorp   "
                + " FROM DCP_STOCKOUT A "
                + " LEFT JOIN platform_staffs_lang B ON A.EID=B.EID AND A.CREATEBY=B.opno and b.lang_type='"+req.getLangType()+"' "
                + " LEFT JOIN platform_staffs_lang f1 ON A .EID = f1.EID AND A .modifyBy = f1.opno and f1.lang_type='"+req.getLangType()+"' "
                + " LEFT JOIN platform_staffs_lang f2 ON A .EID = f2.EID AND A .cancelby = f2.opno and f2.lang_type='"+req.getLangType()+"' "
                + " LEFT JOIN platform_staffs_lang f3 ON A .EID = f3.EID AND A .confirmby = f3.opno and f3.lang_type='"+req.getLangType()+"' "
                + " LEFT JOIN platform_staffs_lang f4 ON A .EID = f4.EID AND A .submitby = f4.opno and f4.lang_type='"+req.getLangType()+"' "
                + " LEFT JOIN platform_staffs_lang f5 ON A .EID = f5.EID AND A .accountby = f5.opno and f5.lang_type='"+req.getLangType()+"' "
                + " LEFT JOIN DCP_PTEMPLATE p ON a.EID = p.EID and a.ptemplateNO = p.ptemplateNo "
                + " left join dcp_deliveryman dm on a.eid=dm.eid and a.deliveryby=dm.opno" +
                " left join dcp_employee em0 on em0.eid=a.eid and em0.employeeno=a.employeeid  " +
                " left join DCP_DEPARTMENT_LANG dd0 on dd0.eid=a.eid and dd0.departno=a.departid and dd0.lang_type='"+langType+"' " +
                " left join dcp_warehouse iw on iw.eid=a.eid and a.INVWAREHOUSE=iw.WAREHOUSE and iw.organizationno=a.ORGANIZATIONNO " +
                " left join DCP_WAREHOUSE_LANG iwl on iwl.eid=a.eid and iwl.WAREHOUSE=a.INVWAREHOUSE and iwl.organizationno=a.ORGANIZATIONNO and iwl.lang_type='"+langType+"'"+
                " left join dcp_stockin ds on ds.eid=a.eid and ds.oofno=a.stockoutno "

        );

        sqlbuf.append(" LEFT JOIN ");
        sqlbuf.append(""
                + "("
                + "SELECT A.EID,A.ORGANIZATIONNO SHOPID,B.ORG_NAME SHOPNAME,B.LANG_TYPE FROM DCP_ORG A "
                + "LEFT JOIN DCP_ORG_LANG B ON A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO AND B.LANG_TYPE='"+req.getLangType()+"' "
                + "WHERE A.EID='"+req.geteId()+"'  AND A.status='100' " //AND A.ORG_FORM='2'
                + ") C ON A.EID=C.EID AND A.TRANSFER_SHOP=C.SHOPID AND C.LANG_TYPE = '"+ langType +"'"
                + " LEFT JOIN DCP_DIFFERENCE I ON A.EID=I.EID AND A.STOCKOUTNO=I.LOAD_DOCNO AND A.TRANSFER_SHOP=I.SHOPID "
                + " left join DCP_ORG_lang z on a.EID=z.EID and a.receipt_org=z.organizationno and z.lang_type='"+ langType +"'"
                + " left join DCP_WAREHOUSE_lang w on a.warehouse=w.warehouse and a.EID=w.EID and a.OrganizationNO=w.OrganizationNO and w.lang_type='"+ langType +"'"
                + " left join DCP_WAREHOUSE w1 on a.warehouse=w1.warehouse and a.EID=w1.EID and a.OrganizationNO=w1.OrganizationNO " );

            sqlbuf.append(""
                    + " left join DCP_WAREHOUSE_lang t on a.TRANSFER_WAREHOUSE=t.warehouse and a.EID=t.EID and a.OrganizationNO=t.OrganizationNO and t.lang_type='"+ langType +"'"
                    + " left join DCP_WAREHOUSE t1 on a.TRANSFER_WAREHOUSE=t1.warehouse and a.EID=t1.EID and a.OrganizationNO=t1.OrganizationNO "
                    + " left join DCP_REASON_LANG L ON A.EID=L.EID AND A.BSNO=L.BSNO and L.lang_type= '" + langType + "'" );

            sqlbuf.append(" where a.STOCKOUTNO='"+stockOutNo+"' and a.eid='"+req.geteId()+"' " +
                    " and a.organizationno='"+req.getOrganizationNO()+"' ");
        sql = sqlbuf.toString();

        return sql;

    }


    private String getStockoutBatchSql(DCP_StockOutDetailQueryReq req) throws Exception{
        StringBuffer sb=new StringBuffer("");
        sb.append("select a.item,a.item2,a.pluno,b.plu_name as pluname,a.featureno,c.featurename,a.warehouse ,d.warehouse_name as warehousename,a.location,e.locationname," +
                " a.batchno,a.PRODDATE,a.expdate,a.punit,a.baseunit,f.uname as punitname,g.uname as baseunitname,a.pqty,a.baseqty,f1.spec  " +
                " from DCP_STOCKOUT_BATCH a " +
                " left join dcp_goods_lang b on a.eid=b.eid and a.pluno=b.pluno and b.lang_type='"+req.getLangType()+"' " +
                " left join DCP_GOODS_FEATURE_LANG c on c.eid =a.eid and c.featureno=a.featureno and c.pluno=a.pluno and c.lang_type='"+req.getLangType()+"' " +
                " left join dcp_warehouse_lang d on a.eid=d.eid and d.organizationno=a.organizationno and d.warehouse=a.warehouse and d.lang_type='"+req.getLangType()+"' " +
                " left join dcp_location  e on e.eid=a.eid and e.organizationno=a.organizationno and e.warehouse=a.warehouse and e.location=a.location " +
                " left join dcp_unit_lang f on f.eid=a.eid and f.unit=a.punit and f.lang_type='"+req.getLangType()+"' " +
                " left join dcp_goods_unit_lang f1 on f1.eid=a.eid and f1.ounit=a.punit and f1.pluno=a.pluno and f.lang_type='"+req.getLangType()+"' " +
                " left join dcp_unit_lang g on g.eid =a.eid and g.unit=a.baseunit and g.lang_type='"+req.getLangType()+"'" +
                " where a.eid='"+req.geteId()+"' and a.organizationno='"+req.getOrganizationNO()+"' " +
                " and a.stockoutno='"+req.getRequest().getStockOutNo()+"'" +
                " order by a.item asc  ");

        return sb.toString();
    }

}
