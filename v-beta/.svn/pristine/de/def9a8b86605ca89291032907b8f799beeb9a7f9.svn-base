package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.json.cust.req.DCP_StockInDetailQueryReq;
import com.dsc.spos.json.cust.req.DCP_StockInQueryReq;
import com.dsc.spos.json.cust.res.DCP_StockInDetailQueryRes;
import com.dsc.spos.json.cust.res.DCP_StockInQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class DCP_StockInDetailQuery extends SPosBasicService<DCP_StockInDetailQueryReq, DCP_StockInDetailQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_StockInDetailQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (isFail){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return false;
    }

    @Override
    protected TypeToken<DCP_StockInDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_StockInDetailQueryReq>(){};
    }

    @Override
    protected DCP_StockInDetailQueryRes getResponseType() {
        return new DCP_StockInDetailQueryRes();
    }

    @Override
    protected DCP_StockInDetailQueryRes processJson(DCP_StockInDetailQueryReq req) throws Exception {
        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();
        String docType = req.getRequest().getDocType();
        DCP_StockInDetailQueryRes res =this.getResponse();
        //查询资料
        String sql = this.getQuerySql(req);
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);

        //查询图片
        res.setDatas(new ArrayList<>());
        if (getQData != null && !getQData.isEmpty()) {
            String isBatchManager = PosPub.getPARA_SMS(dao, eId, "", "IS_BatchNo");
            String isHttps= PosPub.getPARA_SMS(dao, eId, "", "ISHTTPS");
            String httpStr=isHttps.equals("1")?"https://":"http://";
            String domainName=PosPub.getPARA_SMS(dao, eId, "", "DomainName");
            if (domainName.endsWith("/")) {
                domainName = httpStr + domainName + "resource/image/";
            }else{
                domainName = httpStr + domainName + "/resource/image/";
            }


            for (Map<String, Object> oneData : getQData){
                DCP_StockInDetailQueryRes.Datas oneLv1 = res.new Datas();
                String shop1 = oneData.get("SHOPID").toString();
                String stockInNO = oneData.get("STOCKINNO").toString();
                String processERPNo = oneData.get("PROCESSERPNO").toString();
                String dNO = oneData.get("DNO").toString();
                String bDate = oneData.get("BDATE").toString();
                String memo = oneData.get("MEMO").toString();
                String bsNO = oneData.get("BSNO").toString();
                String bsName = oneData.get("BSNAME").toString();
                String status1 = oneData.get("STATUS").toString();
                String docType1 = oneData.get("DOCTYPE").toString();
                String transferShop=oneData.get("TRANSFERSHOP").toString();
                String transferShopName=oneData.get("TRANSFERSHOPNAME").toString();
                String oType = oneData.get("OTYPE").toString();
                String ofNO = oneData.get("OFNO").toString();
                String pTemplateNO = oneData.get("PTEMPLATENO").toString();
                String pTemplateName = "";
                if(!"4".equals(docType)){
                    pTemplateName = oneData.get("PTEMPLATENAME").toString();
                }
                String loadDocType = oneData.get("LOADDOCTYPE").toString();
                String loadDocNO = oneData.get("LOADDOCNO").toString();
                String createByName = oneData.get("CREATEBYNAME").toString();
                String diffStatus = oneData.get("DIFFSTATUS").toString();
                String differenceNO = oneData.get("DIFFERENCENO").toString();
                String createBy = oneData.get("CREATEBY").toString();
                String createDate = oneData.get("CREATEDATE").toString();
                String createTime = oneData.get("CREATETIME").toString();
                String submitBy = oneData.get("SUBMITBY").toString();
                String submitDate = oneData.get("SUBMITDATE").toString();
                String submitTime = oneData.get("SUBMITTIME").toString();
                String submitByName = oneData.get("SUBMITBYNAME").toString();
                String cancelBy = oneData.get("CANCELBY").toString();
                String cancelDate = oneData.get("CANCELDATE").toString();
                String cancelTime = oneData.get("CANCELTIME").toString();
                String cancelByName = oneData.get("CANCELBYNAME").toString();
                String confirmBy = oneData.get("CONFIRMBY").toString();
                String confirmDate = oneData.get("CONFIRMDATE").toString();
                String confirmTime = oneData.get("CONFIRMTIME").toString();
                String confirmByName = oneData.get("CONFIRMBYNAME").toString();
                String modifyBy = oneData.get("MODIFYBY").toString();
                String modifyDate = oneData.get("MODIFYDATE").toString();
                String modifyTime = oneData.get("MODIFYTIME").toString();
                String modifyByName = oneData.get("MODIFYBYNAME").toString();
                String accountBy = oneData.get("ACCOUNTBY").toString();
                String accountDate = oneData.get("ACCOUNTDATE").toString();
                String accountTime = oneData.get("ACCOUNTTIME").toString();
                String accountByName = oneData.get("ACCOUNTBYNAME").toString();
                String totPqty = oneData.get("TOTPQTY").toString();
                String totCqty = oneData.get("TOTCQTY").toString();
                String warehouse     = oneData.get("WAREHOUSE").toString();
                String warehouseName = oneData.get("WAREHOUSENAME").toString();
                String receiptDate   = oneData.get("RECEIPTDATE").toString();
                String deliveryNO = oneData.get("DELIVERY_NO").toString();
                String totAmt = oneData.get("TOTAMT").toString();
                String totDistriAmt = oneData.get("TOT_DISTRIAMT").toString();
                String packingNo = oneData.get("PACKINGNO").toString();
                String receiving_rdate = oneData.get("RECEIVING_RDATE").toString();
                String stockinno_origin = oneData.get("STOCKINNO_ORIGIN").toString();
                String stockinno_refund = oneData.get("STOCKINNO_REFUND").toString();
                String invwarehouse = oneData.get("INVWAREHOUSE").toString();
                String invwarehousename = oneData.get("INVWAREHOUSENAME").toString();
                String employeeid = oneData.get("EMPLOYEEID").toString();
                String employeename = oneData.get("EMPLOYEENAME").toString();
                String departid = oneData.get("DEPARTID").toString();
                String departname = oneData.get("DEPARTNAME").toString();
                String is_location = oneData.get("IS_LOCATION").toString();
                String reason = oneData.get("REASON").toString();
                String ootype = oneData.get("OOTYPE").toString();
                String oofno = oneData.get("OOFNO").toString();
                String transferwarehouse = oneData.get("TRANSFERWAREHOUSE").toString();
                String transferwarehousename = oneData.get("TRANSFERWAREHOUSENAME").toString();

                oneLv1.setShopId(shop1);
                oneLv1.setStockInNo(stockInNO);
                oneLv1.setProcessERPNo(processERPNo);
                oneLv1.setBDate(bDate);
                oneLv1.setMemo(memo);
                oneLv1.setStatus(status1);
                oneLv1.setDocType(docType1);
                oneLv1.setBsNo(bsNO);
                oneLv1.setBsName(bsName);
                oneLv1.setTransferShop(transferShop);
                oneLv1.setTransferShopName(transferShopName);
                oneLv1.setOType(oType);
                oneLv1.setOfNo(ofNO);
                oneLv1.setPTemplateNo(pTemplateNO);
                oneLv1.setPTemplateName(pTemplateName);
                oneLv1.setLoadDocType(loadDocType);
                oneLv1.setLoadDocNo(loadDocNO);
                oneLv1.setCreateByName(createByName);
                oneLv1.setCreateBy(createBy);
                oneLv1.setCreateDate(createDate);
                oneLv1.setCreateTime(createTime);
                oneLv1.setCancelBy(cancelBy);
                oneLv1.setCancelByName(cancelByName);
                oneLv1.setCancelDate(cancelDate);
                oneLv1.setCancelTime(cancelTime);
                oneLv1.setConfirmBy(confirmBy);
                oneLv1.setConfirmByName(confirmByName);
                oneLv1.setConfirmDate(confirmDate);
                oneLv1.setConfirmTime(confirmTime);
                oneLv1.setAccountBy(accountBy);
                oneLv1.setAccountByName(accountByName);
                oneLv1.setAccountDate(accountDate);
                oneLv1.setAccountTime(accountTime);
                oneLv1.setSubmitBy(submitBy);
                oneLv1.setSubmitByName(submitByName);
                oneLv1.setSubmitDate(submitDate);
                oneLv1.setSubmitTime(submitTime);
                oneLv1.setModifyBy(modifyBy);
                oneLv1.setModifyByName(modifyByName);
                oneLv1.setModifyDate(modifyDate);
                oneLv1.setModifyTime(modifyTime);
                oneLv1.setDiffStatus(diffStatus);
                oneLv1.setDifferenceNo(differenceNO);
                oneLv1.setTotPqty(totPqty);
                oneLv1.setTotAmt(totAmt);
                oneLv1.setTotDistriAmt(totDistriAmt);
                oneLv1.setTotCqty(totCqty);
                oneLv1.setWarehouse(warehouse);
                oneLv1.setWarehouseName(warehouseName);

                oneLv1.setInvWarehouse(invwarehouse);
                oneLv1.setInvWarehouseName(invwarehousename);
                oneLv1.setEmployeeId(employeeid);
                oneLv1.setEmployeeName(employeename);
                oneLv1.setDepartId(departid);
                oneLv1.setDepartName(departname);
                oneLv1.setReason(reason);
                oneLv1.setOoType(ootype);
                oneLv1.setOofNo(oofno);
                oneLv1.setTransferWarehouse(transferwarehouse);
                oneLv1.setTransferWarehouseName(transferwarehousename);
                oneLv1.setIsLocation(is_location);

                oneLv1.setRDate(receiving_rdate);

                oneLv1.setReceiptDate(receiptDate);
                oneLv1.setDeliveryNo(deliveryNO);
                oneLv1.setPackingNo(packingNo);
                oneLv1.setCreateDateTime(PosPub.combineDateTime(oneLv1.getCreateDate(), oneLv1.getCreateTime()));
                oneLv1.setAccountDateTime(PosPub.combineDateTime(oneLv1.getAccountDate(), oneLv1.getAccountTime()));
                oneLv1.setConfirmDateTime(PosPub.combineDateTime(oneLv1.getConfirmDate(), oneLv1.getConfirmTime()));
                oneLv1.setCancelDateTime(PosPub.combineDateTime(oneLv1.getCancelDate(), oneLv1.getCancelTime()));
                oneLv1.setModifyDateTime(PosPub.combineDateTime(oneLv1.getModifyDate(), oneLv1.getModifyTime()));
                oneLv1.setSubmitDateTime(PosPub.combineDateTime(oneLv1.getSubmitDate(), oneLv1.getSubmitTime()));



                String UPDATE_TIME;
                SimpleDateFormat simptemp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat allsimptemp=new SimpleDateFormat("yyyyMMddHHmmssSSS");
                if(oneData.get("UPDATE_TIME")==null||oneData.get("UPDATE_TIME").toString().isEmpty()) {
                    UPDATE_TIME=allsimptemp.format(Calendar.getInstance().getTime());
                } else {
                    UPDATE_TIME=oneData.get("UPDATE_TIME").toString();
                }
                oneLv1.setUpdate_time(simptemp.format(allsimptemp.parse(UPDATE_TIME)));
                oneLv1.setProcess_status(oneData.get("PROCESS_STATUS").toString());
                oneLv1.setStockInNo_origin(stockinno_origin);
                oneLv1.setStockInNo_refund(stockinno_refund);
                oneLv1.setDeliveryBy(oneData.get("DELIVERYBY").toString());
                oneLv1.setDeliveryName(oneData.get("DELIVERYNAME").toString());
                oneLv1.setDeliveryTel(oneData.get("DELIVERYTEL").toString());
                oneLv1.setCorp(oneData.get("CORP").toString());
                oneLv1.setDeliveryCorp(oneData.get("DELIVERYCORP").toString());
                oneLv1.setDatas(new ArrayList<>());

                //获取明细数据
                String detailQuerySql = this.getDetailQuerySql(req, stockInNO);
                List<Map<String, Object>> detailData = this.doQueryData(detailQuerySql, null);
                for (Map<String, Object> oneData2 : detailData) {

                    DCP_StockInDetailQueryRes.Detail oneLv2 = res.new Detail();
                    String item = oneData2.get("ITEM").toString();
                    String oType2 = oneData2.get("OTYPE").toString();
                    String ofNO2 = oneData2.get("OFNO").toString();
                    String oItem = oneData2.get("OITEM").toString();
                    String ooType = oneData2.get("OOTYPE").toString();
                    String oofNO = oneData2.get("OOFNO").toString();
                    String ooItem = oneData2.get("OOITEM").toString();
                    int i_item;
                    int i_oItem;
                    if (item == null || item.length()==0 ){
                        i_item=0;
                    } else {
                        float f= Float.parseFloat(item);
                        i_item=(int) Math.floor(f);
                    }

                    if (oItem == null || oItem.length()==0 ){
                        i_oItem=0;
                    } else {
                        float f= Float.parseFloat(oItem);
                        i_oItem=(int) Math.floor(f);
                    }
                    String pluNO = oneData2.get("PLUNO").toString();
                    String pluMemo = oneData2.get("PLUMEMO").toString();
                    String pluName = oneData2.get("PLUNAME").toString();
                    String spec = oneData2.get("SPEC").toString();
                    String punit = oneData2.get("PUNIT").toString();
                    String punitName = oneData2.get("PUNITNAME").toString();
                    String pqty = oneData2.get("PQTY").toString();
                    float f_pqty;
                    if (pqty == null || pqty.length()==0 ){
                        f_pqty=0;
                    } else 	f_pqty =  Float.parseFloat(pqty);

                    String receivingqty = oneData2.get("RECEIVINGQTY").toString();
                    float f_receivingqty;
                    if (receivingqty == null || receivingqty.length()==0 ){
                        f_receivingqty=0;
                    } else 	f_receivingqty =  Float.parseFloat(receivingqty);
                    String poQty = oneData2.get("POQTY").toString();
                    float f_poQty;
                    if(poQty == null || poQty.length()==0){
                        f_poQty=0;
                    }else f_poQty = Float.parseFloat(poQty);

                    String diffReqQty = oneData2.get("DIFFREQQTY").toString();
                    float f_diffReqQty;
                    if(diffReqQty == null || diffReqQty.length()==0){
                        f_diffReqQty=0;
                    }else f_diffReqQty = Float.parseFloat(diffReqQty);

                    String diffQty = oneData2.get("DIFFQTY").toString();
                    float f_diffQty;
                    if(diffQty == null || diffQty.length()==0){
                        f_diffQty=0;
                    }else f_diffQty = Float.parseFloat(diffQty);

                    String price = oneData2.get("PRICE").toString();
                    float f_price;
                    if (price == null || price.length()==0 ){
                        f_price=0;
                    } else 	f_price =  Float.parseFloat(price);

                    String amt = oneData2.get("AMT").toString();
                    if (amt == null || amt.length()==0 ){
                        amt="0";
                    }
                    String distriAmt = oneData2.get("DISTRIAMT").toString();
                    if (Check.Null(distriAmt)) distriAmt="0";

                    String pluBarcode = oneData2.get("PLUBARCODE").toString();
                    String unitRatio = oneData2.get("UNITRATIO").toString();
                    String detail_warehouse = oneData2.get("WAREHOUSE").toString();
                    String RETWQTY = oneData2.get("RETWQTY").toString()==null||oneData2.get("RETWQTY").toString().isEmpty()?"0":oneData2.get("RETWQTY").toString();
                    //换算成已退货的包装单位和包装数量
                    float fRETWQTY=Float.parseFloat("0");
                    if(!"0".equals(unitRatio)){
                        fRETWQTY=(new BigDecimal(RETWQTY)).divide(new BigDecimal(unitRatio), RoundingMode.HALF_UP).floatValue();
                    }
                    String procRate = oneData2.get("PROCRATE").toString()==null||oneData2.get("PROCRATE").toString().isEmpty()?"0":oneData2.get("PROCRATE").toString();
                    String batchNO = oneData2.get("BATCHNO").toString();
                    String isBatch = oneData2.get("ISBATCH").toString();

                    // 【ID1017769】【大连大万3.0】总部发货后，门店没有收到  by jinzma 20210517 isBatch为空抛异常，未关联到dcp_goods
                    if (Check.Null(isBatch)){
                        isBatch="N";   //以上处理是避免查询走不下去，先给N，在DCP_StockInProcess服务里面加管控
                    }

                    String prodDate = oneData2.get("PRODDATE").toString();
                    String distriPrice = oneData2.get("DISTRIPRICE").toString();

                    String punitUDLength = "2";//默认2
                    String punitUDLength_db = oneData2.get("PUNITUDLENGTH").toString();

                    try {
                        int punitUDLength_i = Integer.parseInt(punitUDLength_db);
                        punitUDLength = punitUDLength_i+"";
                    } catch (Exception ignored) {
                    }

                    String baseQty = oneData2.get("BASEQTY").toString();
                    String baseUnit = oneData2.get("BASEUNIT").toString();
                    String baseUnitName = oneData2.get("BASEUNITNAME").toString();
                    String featureName = oneData2.get("FEATURENAME").toString();
                    String featureNo = oneData2.get("FEATURENO").toString();
                    String listImage = "";//oneData2.get("LISTIMAGE").toString();
                    if (!Check.Null(listImage)){
                        listImage = domainName+listImage;
                    }

                    String detailPackingNo = oneData2.get("PACKINGNO").toString();

                    String item_origin = oneData2.get("ITEM_ORIGIN").toString();
                    String pqty_origin = oneData2.get("PQTY_ORIGIN").toString();
                    String pqty_refund = oneData2.get("PQTY_REFUND").toString();
                    String originno = oneData2.get("ORIGINNO").toString();
                    String originitem = oneData2.get("ORIGINITEM").toString();
                    String location = oneData2.get("LOCATION").toString();
                    String locationname = oneData2.get("LOCATIONNAME").toString();
                    String transferBatchNo = oneData2.get("TRANSFERBATCHNO").toString();
                    String shelfLife = oneData2.get("SHELFLIFE").toString();
                    String detailBsNo = oneData2.get("BSNO").toString();
                    String detailBsName = oneData2.get("BSNAME").toString();

                    // 處理調整回傳值；
                    oneLv2.setItem(item);
                    oneLv2.setOType(oType2);
                    oneLv2.setOfNo(ofNO2);
                    oneLv2.setOItem(oItem);
                    oneLv2.setOoType(ooType);
                    oneLv2.setOofNo(oofNO);
                    oneLv2.setOoItem(ooItem);
                    oneLv2.setPluNo(pluNO);
                    oneLv2.setPluMemo(pluMemo);
                    oneLv2.setPluName(pluName);
                    oneLv2.setSpec(spec);
                    oneLv2.setPunit(punit);
                    oneLv2.setPunitName(punitName);
                    oneLv2.setPqty(String.valueOf(f_pqty));
                    oneLv2.setPoQty(String.valueOf(f_poQty));
                    oneLv2.setDiffReqQty(String.valueOf(f_diffReqQty));
                    oneLv2.setDiffQty(String.valueOf(f_diffQty));
                    oneLv2.setPrice(String.valueOf(f_price));
                    oneLv2.setAmt(amt);
                    oneLv2.setDistriAmt(distriAmt);
                    oneLv2.setReceivingQty(String.valueOf(f_receivingqty));
                    oneLv2.setPluBarcode(pluBarcode);
                    oneLv2.setUnitRatio(unitRatio);
                    oneLv2.setWarehouse(detail_warehouse);
                    oneLv2.setRoutunit(punit);
                    oneLv2.setRoutqty(String.valueOf(fRETWQTY));
                    oneLv2.setRoutunitName(punitName);
                    oneLv2.setProcRate(procRate);
                    oneLv2.setBatchNo(batchNO);
                    oneLv2.setIsBatch(isBatch);
                    oneLv2.setProdDate(prodDate);
                    oneLv2.setDistriPrice(distriPrice);
                    oneLv2.setPunitUdLength(punitUDLength);
                    oneLv2.setBaseQty(baseQty);
                    oneLv2.setBaseUnit(baseUnit);
                    oneLv2.setBaseUnitName(baseUnitName);
                    oneLv2.setFeatureNo(featureNo);
                    oneLv2.setFeatureName(featureName);
                    oneLv2.setListImage(listImage);
                    oneLv2.setPackingNo(detailPackingNo);
                    //【ID1027675】【荷家3.0】200001门店，完工单号WGRK2022070600001传到ERP完工数量是0   by jinzma 20221109
                    oneLv2.setBaseUnitUdLength(oneData2.get("BASEUNITUDLENGTH").toString());
                    oneLv2.setItem_origin(item_origin);
                    oneLv2.setPqty_origin(pqty_origin);
                    oneLv2.setPqty_refund(pqty_refund);
                    oneLv2.setOriginNo(originno);
                    oneLv2.setOriginItem(originitem);
                    oneLv2.setLocation(location);
                    oneLv2.setLocationName(locationname);
                    oneLv2.setTransferBatchNo(transferBatchNo);
                    oneLv2.setShelfLife(shelfLife);
                    oneLv2.setExpDate(oneData2.get("EXPDATE").toString());
                    oneLv2.setBsNo(detailBsNo);
                    oneLv2.setBsName(detailBsName);
                    oneLv1.getDatas().add(oneLv2);
                }

                if("5".equals(docType)){
                    //oneLv1.getOofNo();
                    String imageSql="select a.* from DCP_STOCKOUT_DETAIL_IMAGE a where a.eid='"+eId+"' and a.stockoutno='"+oneLv1.getOofNo()+"'";
                    List<Map<String, Object>> imageList = this.doQueryData(imageSql,null);
                    if(CollUtil.isNotEmpty(imageList)){
                        String finalDomainName = domainName;
                        oneLv1.getDatas().forEach(x->{
                            x.setImageList(new ArrayList<>());
                            List<Map<String, Object>> detailImageList = imageList.stream().filter(y -> y.get("OITEM").toString().equals(x.getOoItem())).collect(Collectors.toList());
                            if(CollUtil.isNotEmpty(detailImageList)){
                                DCP_StockInDetailQueryRes.ImageList singleImage = res.new ImageList();

                                String image = detailImageList.get(0).get("IMAGE").toString();
                                String item = detailImageList.get(0).get("ITEM").toString();
                                if(Check.NotNull(image)){
                                    image= finalDomainName +image;
                                }

                                singleImage.setImage(image);
                                singleImage.setItem(item);
                                x.getImageList().add(singleImage);
                            }
                        });
                    }

                }

                res.getDatas().add(oneLv1);
            }


        }

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(DCP_StockInDetailQueryReq req) throws Exception {
        StringBuffer sqlbuf = new StringBuffer();
        String eId = req.geteId();
        String shopId = req.getShopId();
        String organizationNO = req.getOrganizationNO();
        String stockInNo = req.getRequest().getStockInNo();
        String langType = req.getLangType();
        String status = req.getRequest().getStatus();
        String docType = req.getRequest().getDocType();
        String ofNo = req.getRequest().getOfNo();


        sqlbuf.append(" SELECT  case when a.status=0 then 0 else 2 end  as rw,a.EID,a.ORGANIZATIONNO,a.bsNO,a.stockinno as dno ,b.REASON_NAME as bsname, "
                + " a.process_ERP_NO as processERPNo, A.stockInNO,A.STOCKINNO_ORIGIN,A.STOCKINNO_REFUND,a.PTEMPLATENO,a.SHOPID,to_number(a.bDate) as bdate,A.memo,"
                + " A.status,a.DOC_TYPE as docType,A.oType "
                + " ,A.ofNO,A.LOAD_DOCTYPE as loadDocType,A.LOAD_DOCNO as loadDocNO,A.CREATEBY as createBy,A.TRANSFER_SHOP as transferShop,c.org_name as TRANSFERSHOPname  "
                + " ,A.TOT_PQTY AS totPqty,a.tot_cqty as totCqty,a.tot_amt as totAmt,a.TOT_DISTRIAMT,cast('' as nvarchar2(40)) as DELIVERY_NO,a.PACKINGNO "
                + " ,a.receiptDate"
                + " ,a.warehouse as warehouse, "
                + " A.PROCESS_STATUS,A.UPDATE_TIME,0 as PROCRATE"
                + " ,A.CREATE_DATE as createDate,A.CREATE_TIME as createTime ,f.op_name as createbyname,"
                + " a.ModifyBy , a.modify_Date  AS modifyDate ,  a.modify_time AS modifyTime ,f1.op_name as modifybyname , "
                + " a.confirmBy, a.confirm_Date AS  confirmDate , a.confirm_Time AS ConfirmTime,   "
                + " a.cancelBy , a.cancel_date AS cancelDate ,f2.op_name as cancelbyname ,f3.op_name as confirmbyname,f4.op_name as submitbyname,f5.op_name as accountbyname, a.cancel_Time  AS  cancelTime , "
                + " a.accountBY ,a.account_date AS accountDate, a.account_time AS accountTime ,"
                + " a.submitBy, a.submit_date AS submitDate , a.submit_time AS submitTime,"
                + " g.rdate as receiving_rdate,a.deliveryby,dm.opname as deliveryname,dm.phone as deliverytel,a.invwarehouse,dw.warehouse_name as invwarehousename,a.employeeid,de.name as employeename,a.departid,dd.departname,a.ootype,a.oofno,a.bsno as reason,dw1.islocation as is_location,dw2.warehouse as transferwarehouse ,dw2.warehouse_name as transferwarehousename ," +
                " i.status as diffstatus ,i.differenceno ,w0.islocation as islocation ,w.warehouse_name as warehousename,d.PTEMPLATE_NAME as ptemplatename,a.corp,a.deliverycorp  "
                + " FROM DCP_STOCKIN A "+
                " left join DCP_REASON_LANG b ON A.EID = b.EID AND A.BSNO=b.BSNO and b.lang_type='"+langType+"' "+
                " left join dcp_org_lang c on c.eid=a.eid and c.organizationno=a.TRANSFER_SHOP and c.lang_type='"+langType+"'"+
                " left join DCP_ptemplate d on a.EID=d.EID and a.ptemplateno=d.ptemplateno and d.doc_type='0' "+
                 " LEFT JOIN DCP_DIFFERENCE I ON A.EID=I.EID  AND A.STOCKINNO=I.OFNO AND A.SHOPID=I.SHOPID  "+
                " left join dcp_warehouse_lang w on w.eid=a.eid and w.organizationno=a.organizationno and w.warehouse=a.warehouse and w.lang_type='"+langType+"'"+
                " left join dcp_warehouse w0 on w0.eid=a.eid and w0.warehouse=a.warehouse and w0.organizationno=a.organizationno "+

                //" left join dcp_warehouse_lang w1 on w1.eid=a.eid and w1.warehouse=a.invWarehouse and w1.organizationno=a.transfer_shop and w1.lang_type='"+langType+"'"+
                  //      " left join dcp_warehouse_lang w2 on w2.eid=a.eid and w2.warehouse=a.transfer_Warehouse and w2.organizationno=a.transfer_shop and w2.lang_type='"+langType+"'"

                         " left join dcp_receiving g on a.eid=g.eid and a.shopid=g.shopid and a.ofno=g.receivingno ");
        sqlbuf.append(" left join dcp_deliveryman dm on a.eid=dm.eid and a.deliveryby=dm.opno");
        sqlbuf.append(" "
                + " LEFT JOIN platform_staffs_lang f ON A .EID = f.EID AND A .createby = f.opno  and f.lang_type='"+req.getLangType()+"' "
                + " LEFT JOIN platform_staffs_lang f1 ON A .EID = f1.EID AND A .modifyBy = f1.opno and f1.lang_type='"+req.getLangType()+"' "
                + " LEFT JOIN platform_staffs_lang f2 ON A .EID = f2.EID AND A .cancelby = f2.opno and f2.lang_type='"+req.getLangType()+"' "
                + " LEFT JOIN platform_staffs_lang f3 ON A .EID = f3.EID AND A .confirmby = f3.opno  and f3.lang_type='"+req.getLangType()+"' "
                + " LEFT JOIN platform_staffs_lang f4 ON A .EID = f4.EID AND A .submitby = f4.opno and f4.lang_type='"+req.getLangType()+"' "
                + " LEFT JOIN platform_staffs_lang f5 ON A .EID = f5.EID AND A .accountby = f5.opno and f5.lang_type='"+req.getLangType()+"' "+
                " left join dcp_warehouse_lang dw on dw.eid=A.eid and dw.organizationno=A.transfer_shop and dw.warehouse=A.invwarehouse and dw.lang_type='"+langType+"' " +
                " left join dcp_warehouse dw1 on dw1.eid=a.eid and dw1.organizationno=a.organizationno and dw1.warehouse=a.warehouse " +
                " left join dcp_warehouse_lang dw2 on dw2.eid=A.eid and dw2.organizationno=A.transfer_shop and dw2.warehouse=A.transfer_warehouse and dw2.lang_type='"+langType+"' " +
                " left join dcp_employee de on de.eid=A.eid and de.employeeno=A.employeeid " +
                " left join DCP_DEPARTMENT_LANG dd on dd.eid=A.eid and dd.DEPARTNO=A.departid and dd.lang_type='"+langType+"' " +
                " "
        );

        sqlbuf.append(" where   a.SHOPID='"+shopId+"' and a.EID='"+eId+"' and a.organizationno='"+organizationNO+"' ");

        if (status != null && status.length()!=0) {
            sqlbuf.append(" AND A.status = '"+status+"'  ");
        } else {
            //sqlbuf.append(" AND a.status <> '8'  ");  //by jzma 20190610 过滤已作废的收货通知单 (8-已作废)
        }
        if(!Check.Null(stockInNo)){
            sqlbuf.append(" and a.stockinno='"+stockInNo+"' ");
        }
        if(!Check.Null(docType)){
            sqlbuf.append(" and a.DOC_TYPE='"+docType+"' ");
        }
        if(!Check.Null(ofNo)){
            sqlbuf.append(" and a.ofno='"+ofNo+"' ");
        }

        return sqlbuf.toString();
    }

    private String getDetailQuerySql(DCP_StockInDetailQueryReq req,String stockInNo){
        DCP_StockInDetailQueryReq.LevelReq request = req.getRequest();
        String sql = null;
        StringBuffer sqlbuf = new StringBuffer();
        String eId = req.geteId();
        String langType = req.getLangType();

        sqlbuf.append("" +
                " select a.item,a.otype,a.ofno,a.oitem,a.ooitem,a.ooType,a.oofno,a.pluno,b1.plu_name as pluname,c.spec,a.punit,d.uname as punitname," +
                " a.pqty,a.price,a.amt,a.distriprice,a.distriamt,a.batch_no as batchno ,a.baseunit,a.baseqty,d1.uname as baseunitname,a.featureno,f.featurename,a.packingno," +
                " a.item_origin,a.pqty_origin,a.Pqty_refund,a.MES_LOCATION as location,a.PROD_DATE as proddate,a.EXPDATE,g.LOCATIONNAME,a.UNIT_RATIO as unitratio," +
                " a.PLU_BARCODE as plubarcode,a.PLU_MEMO as plumemo,a.originItem,a.originno,d2.udlength as punitUdLength,d3.udlength as baseunitudlength,a.warehouse," +
                " 0 as diffReqQty,0 as diffQty,a.receiving_qty,a.RETWQTY,a.poQty,b.ISBATCH,0 as procRate,a.pqty as realqty,a.RECEIVING_QTY as RECEIVINGQTY,a.PACKINGNO,a.TRANSFER_BATCHNO as transferBatchNo,b.shelfLife,h.bsno,h.reason_name as bsname  " +
                " from dcp_stockin_detail a " +
                " left join dcp_goods b on a.eid=b.eid and a.pluno=b.pluno " +
                " left join dcp_goods_lang b1 on a.eid=b1.eid and a.pluno=b1.pluno and b1.lang_type='"+langType+"'" +
                " left join  DCP_GOODS_UNIT_LANG c on c.eid=a.eid and c.pluno=a.pluno and c.ounit=a.punit and c.lang_type='"+langType+"'" +
                " left join dcp_unit_lang d on d.eid=a.eid and d.unit=a.punit and d.lang_type='"+langType+"'" +
                " left join dcp_unit_lang d1 on d1.eid=a.eid and d1.unit=a.baseunit and d1.lang_type='"+langType+"'" +
                " left join dcp_unit d2 on d2.eid=a.eid and d2.unit=a.punit " +
                " left join dcp_unit d3 on d3.eid=a.eid and d3.unit=a.baseunit " +
                " left join DCP_GOODS_FEATURE_LANG f on f.eid=a.eid and f.pluno=a.pluno and f.featureno=a.featureno and f.lang_type='"+langType+"' "+
                " left join DCP_LOCATION g on g.eid=a.eid and g.organizationno=a.organizationno and g.warehouse=a.warehouse and g.location=a.mes_location " +
                " left join dcp_reason_lang h on h.eid=a.eid and h.bsno=a.bsno and h.bstype='17' and h.lang_type='"+req.getLangType()+"' " +
                "");

        sqlbuf.append(" where a.stockinno='"+stockInNo+"' and a.organizationno='"+ req.getOrganizationNO()+"' and a.eid='"+eId+"'");




        sql = sqlbuf.toString();

        return sql;
    }
    private String getImageQuerySql(DCP_StockInDetailQueryReq req){
        StringBuffer sqlbuf = new StringBuffer();
        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();
        String sStockOutNo = req.getRequest().getStockInNo();

        sqlbuf.append("select * from dcp_sstockout_image a" +
                "where a.eid='"+eId+"' and a.organizationno='"+organizationNO+"' " +
                " and a.sstockoutno='"+sStockOutNo+"' ");
        return sqlbuf.toString();
    }


}
