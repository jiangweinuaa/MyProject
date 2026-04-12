package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ShopSettBillDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_ShopSettBillDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_ShopSettBillDetailQuery extends SPosBasicService<DCP_ShopSettBillDetailQueryReq, DCP_ShopSettBillDetailQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_ShopSettBillDetailQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_ShopSettBillDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_ShopSettBillDetailQueryReq>() {
        };
    }

    @Override
    protected DCP_ShopSettBillDetailQueryRes getResponseType() {
        return new DCP_ShopSettBillDetailQueryRes();
    }

    @Override
    protected DCP_ShopSettBillDetailQueryRes processJson(DCP_ShopSettBillDetailQueryReq req) throws Exception {

        DCP_ShopSettBillDetailQueryRes res = this.getResponseType();

        res.setDatas(new ArrayList<>());
        List<Map<String, Object>> qData = doQueryData(getQuerySql(req), null);
        List<Map<String, Object>> qReceive = doQueryData(getQueryReceiveSql(req), null);
        if (CollectionUtils.isNotEmpty(qData)) {

            Map<String, Boolean> distinct = new HashMap<>();
            distinct.put("EID", true);
            distinct.put("RECONNO", true);
            List<Map<String, Object>> master = MapDistinct.getMap(qData, distinct);
            for (Map<String, Object> oneMaster : master) {
                DCP_ShopSettBillDetailQueryRes.Datas oneData = res.new Datas();
                res.getDatas().add(oneData);
                oneData.setShopSettList(new ArrayList<>());
                oneData.setShopRecList(new ArrayList<>());

                oneData.setStatus(oneMaster.get("STATUS").toString());
                oneData.setBDate(oneMaster.get("BDATE").toString());
                oneData.setReconNo(oneMaster.get("RECONNO").toString());
                oneData.setShopId(oneMaster.get("SHOPID").toString());
                oneData.setSourceType(oneMaster.get("SOURCETYPE").toString());
                oneData.setBizPartnerNo(oneMaster.get("BIZPARTNERNO").toString());
                oneData.setReceiver(oneMaster.get("RECEIVER").toString());
                oneData.setPayDateNo(oneMaster.get("PAYDATENO").toString());
                oneData.setCurrency(oneMaster.get("CURRENCY").toString());
                oneData.setExRate(oneMaster.get("EXRATE").toString());
                oneData.setTaxCode(oneMaster.get("TAXCODE").toString());
                oneData.setTaxRate(oneMaster.get("TAXRATE").toString());
                oneData.setTot_Amt(oneMaster.get("TOT_AMT").toString());
                oneData.setPayAmt(oneMaster.get("PAYAMT").toString());
                oneData.setArNo(oneMaster.get("ARNO").toString());
                oneData.setArNo2(oneMaster.get("ARNO2").toString());

                Map<String, Object> condition = new HashMap<>();
                condition.put("EID", oneMaster.get("EID").toString());
                condition.put("RECONNO", oneMaster.get("RECONNO").toString());

                List<Map<String, Object>> detailData = MapDistinct.getWhereMap(qData, condition, true);
                for (Map<String, Object> oneDetail : detailData) {
                    DCP_ShopSettBillDetailQueryRes.ShopSettList oneShopSett = res.new ShopSettList();
                    oneData.getShopSettList().add(oneShopSett);

                    oneShopSett.setShopId(oneDetail.get("SHOPID").toString());
                    oneShopSett.setCorp(oneDetail.get("CORP").toString());
                    oneShopSett.setReconNo(oneDetail.get("RECONNO").toString());
                    oneShopSett.setItem(oneDetail.get("ITEM").toString());
                    oneShopSett.setSourceNo(oneDetail.get("SOURCENO").toString());
                    oneShopSett.setSourceItem(oneDetail.get("SOURCEITEM").toString());
                    oneShopSett.setPluBarcode(oneDetail.get("PLUBARCODE").toString());
                    oneShopSett.setPluNo(oneDetail.get("PLUNO").toString());
                    oneShopSett.setFeatureNo(oneDetail.get("FEATURENO").toString());
                    oneShopSett.setTaxCode(oneDetail.get("TAXCODE").toString());
                    oneShopSett.setOldPrice(oneDetail.get("OLDPRICE").toString());
                    oneShopSett.setPrice(oneDetail.get("PRICE").toString());
                    oneShopSett.setQty(oneDetail.get("QTY").toString());
                    oneShopSett.setSUnit(oneDetail.get("SUNIT").toString());
                    oneShopSett.setDiscAmt(oneDetail.get("DISCAMT").toString());
                    oneShopSett.setAmt(oneDetail.get("AMT").toString());
                    oneShopSett.setBtAmt(oneDetail.get("BTAMT").toString());
                    oneShopSett.setLCYTATAmt(oneDetail.get("LCYTATAMT").toString());
                    oneShopSett.setIsGift(oneDetail.get("ISGIFT").toString());
                    oneShopSett.setCardNo(oneDetail.get("CARDNO").toString());
                    oneShopSett.setSendPay(oneDetail.get("SENDPAY").toString());
                    oneShopSett.setShopQty(oneDetail.get("SHOPQTY").toString());

                }

                List<Map<String, Object>> receiveData = MapDistinct.getWhereMap(qReceive, condition, true);
                for (Map<String, Object> oneDetail : detailData) {
                    DCP_ShopSettBillDetailQueryRes.ShopRecList oneRec = res.new ShopRecList();
                    oneData.getShopRecList().add(oneRec);

                    oneRec.setShopId(oneDetail.get("SHOPID").toString());
                    oneRec.setCorp(oneDetail.get("CORP").toString());
                    oneRec.setReconNo(oneDetail.get("RECONNO").toString());
                    oneRec.setItem(oneDetail.get("ITEM").toString());
                    oneRec.setAccItem(oneDetail.get("ITEM1").toString());
                    oneRec.setPayType(oneDetail.get("PAYTYPE").toString());
                    oneRec.setPayCode(oneDetail.get("PAYCODE").toString());
                    oneRec.setAmt(oneDetail.get("AMT").toString());
                    oneRec.setCardNo(oneDetail.get("CARDNO").toString());
                    oneRec.setCouponQty(oneDetail.get("COUPONQTY").toString());
                    oneRec.setIschanged(oneDetail.get("ISCHANGED").toString());
                    oneRec.setExtra(oneDetail.get("EXTRA").toString());
                    oneRec.setDescore(oneDetail.get("DESCORE").toString());
                    oneRec.setRefundType(oneDetail.get("REFUNDTYPE").toString());
                    oneRec.setAccDate(oneDetail.get("ACCDATE").toString());
                    oneRec.setIsWrtOff(oneDetail.get("ISWRTOFF").toString());
                    oneRec.setIsAcc(oneDetail.get("ISACC").toString());
                    oneRec.setIsOrderPay(oneDetail.get("ISORDERPAY").toString());
                    oneRec.setTotChanged(oneDetail.get("TOTCHANGED").toString());
                    oneRec.setSendPay(oneDetail.get("SENDPAY").toString());
                    oneRec.setOrderNo(oneDetail.get("ORDERNO").toString());
                    oneRec.setSellerDisc(oneDetail.get("SELLERDISC").toString());
                    oneRec.setThirdDiscount(oneDetail.get("THIRDDISCOUNT").toString());
                    oneRec.setCouponMarketPrice(oneDetail.get("COUPONMARKETPRICE").toString());
                    oneRec.setCouponPrice(oneDetail.get("COUPONPRICE").toString());
                    oneRec.setBDate(oneDetail.get("BDATE").toString());
                    oneRec.setEraseAmt(oneDetail.get("ERASEAMT").toString());
                    oneRec.setSquadNo(oneDetail.get("SQUADNO").toString());
                    oneRec.setMachineNo(oneDetail.get("MACHINENO").toString());
                    oneRec.setOpNo(oneDetail.get("OPNO").toString());


                }


            }


        }

        res.setSuccess(true);

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    protected String getQueryReceiveSql(DCP_ShopSettBillDetailQueryReq req) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT ")
                .append(" b.* ")
                .append(" FROM DCP_SHOPSETTBILL a ")
                .append(" LEFT JOIN DCP_SHOPRECEIVE b on a.eid=b.eid and a.RECONNO=b.RECONNO  ");

        sb.append(" WHERE a.EID='").append(req.geteId()).append("' ");

        if (StringUtils.isNotEmpty(req.getRequest().getShopId())) {
            sb.append(" AND a.SHOPID='").append(req.getRequest().getShopId()).append("' ");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getReconNo())) {
            sb.append(" AND a.RECONNO='").append(req.getRequest().getReconNo()).append("' ");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getSourceType())) {
            sb.append(" AND a.SOURCETYPE='").append(req.getRequest().getSourceType()).append("' ");
        }

        return sb.toString();
    }

    @Override
    protected String getQuerySql(DCP_ShopSettBillDetailQueryReq req) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT ")
                .append(" b.* " +
                        " ,a.BDATE,a.BIZPARTNERNO,a.RECEIVER,a.PAYDATENO,a.CURRENCY" +
                        " ,a.EXRATE,a.TAXCODE,a.TAXRATE,a.SOURCETYPE,a.TOT_AMT,a.PAYAMTPAYAMT" +
                        " ,a.ARNO,a.ARNO2 ")
                .append(" FROM DCP_SHOPSETTBILL a ")
                .append(" LEFT JOIN DCP_SHOPSETTBILLDETAIL b on a.eid=b.eid and a.RECONNO=b.RECONNO  ");

        sb.append(" WHERE a.EID='").append(req.geteId()).append("' ");

        if (StringUtils.isNotEmpty(req.getRequest().getShopId())) {
            sb.append(" AND a.SHOPID='").append(req.getRequest().getShopId()).append("' ");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getReconNo())) {
            sb.append(" AND a.RECONNO='").append(req.getRequest().getReconNo()).append("' ");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getSourceType())) {
            sb.append(" AND a.SOURCETYPE='").append(req.getRequest().getSourceType()).append("' ");
        }

        return sb.toString();
    }
}
