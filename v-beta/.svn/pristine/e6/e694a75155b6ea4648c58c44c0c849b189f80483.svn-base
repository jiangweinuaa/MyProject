package com.dsc.spos.service.dataaux;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopRecData {

    public enum TicketType {
        Sale,
        CardSale,
        CouponSale,
        EquityCardSale
    }

    public void insertShopSettBill(List<DataProcessBean> dbs, TicketType type, List<Map<String, Object>> detail) throws Exception {

        switch (type) {
            case Sale:insertShopSettBillForSale(dbs,detail);
                break;
            case CardSale:insertShopSettBillForCardSale(dbs,detail);
                break;
            case CouponSale:
                break;
            case EquityCardSale:
                break;
        }

    }

    private void insertShopSettBillForCardSale(List<DataProcessBean> dbs, List<Map<String, Object>> detail) throws Exception {

    }

    private void insertShopSettBillForSale(List<DataProcessBean> dbs, List<Map<String, Object>> detail) throws Exception {

        Map<String, Boolean> distinct = new HashMap<String, Boolean>();
        distinct.put("EID", true);
        distinct.put("SHOPID", true);
        distinct.put("TYPE", true);
        distinct.put("BDATE", true);
        distinct.put("CUSTOMERNO", true);

        List<Map<String, Object>> masterData = MapDistinct.getMap(detail, distinct);

        int dir = 1;
        int item = 0;
        for (Map<String, Object> oneMaster : masterData) {
            if ("1".equals(oneMaster.get("TYPE").toString())) {
                dir = 1;
            }else {
                dir = -1;
            }

            String eid = oneMaster.get("EID").toString();
            String shopId = oneMaster.get("SHOPID").toString();

            String reconNo = getReconNo(eid, shopId);

            ColumnDataValue dcp_ShopSettBill = new ColumnDataValue();
            dcp_ShopSettBill.add("EID", DataValues.newString(oneMaster.get("EID")));
            dcp_ShopSettBill.add("SHOPID", DataValues.newString(oneMaster.get("SHOPID")));
            dcp_ShopSettBill.add("CORP", DataValues.newString(oneMaster.get("CORP")));
            dcp_ShopSettBill.add("RECONNO", DataValues.newString(reconNo));
            dcp_ShopSettBill.add("BDATE", DataValues.newDate(DateFormatUtils.getNowDate()));
            dcp_ShopSettBill.add("BIZPARTNERNO", DataValues.newString(oneMaster.get("CUSTOMERNO")));
//            dcp_ShopSettBill.add("RECEIVER", DataValues.newString(oneMaster.get("EID")));
//            dcp_ShopSettBill.add("PAYDATENO", DataValues.newString(oneMaster.get("EID")));
            dcp_ShopSettBill.add("CURRENCY", DataValues.newString("RMB"));
            dcp_ShopSettBill.add("EXRATE", DataValues.newString(1));
//            dcp_ShopSettBill.add("TAXCODE", DataValues.newString(oneMaster.get("EID")));
//            dcp_ShopSettBill.add("TAXRATE", DataValues.newString(oneMaster.get("EID")));
//            dcp_ShopSettBill.add("INCLTAX", DataValues.newString(oneMaster.get("EID")));
            dcp_ShopSettBill.add("SOURCETYPE", DataValues.newString(oneMaster.get("TYPE")));
            dcp_ShopSettBill.add("SOURCEDATE", DataValues.newString(oneMaster.get("BDATE")));
            dcp_ShopSettBill.add("TOT_AMT", DataValues.newString(oneMaster.get("TOT_AMT")));
            dcp_ShopSettBill.add("PAYAMT", DataValues.newString(oneMaster.get("PAY_AMT")));

            dbs.add(new DataProcessBean(DataBeans.getInsBean("DCP_SHOPSETTBILLDETAIL", dcp_ShopSettBill)));

            Map<String, Object> condition = new HashMap<>();
            condition.put("EID", oneMaster.get("EID"));
            condition.put("SHOPID", oneMaster.get("SHOPID"));
            condition.put("TYPE", oneMaster.get("TYPE"));
            condition.put("BDATE", oneMaster.get("BDATE"));
            condition.put("CUSTOMERNO", oneMaster.get("CUSTOMERNO"));

            List<Map<String, Object>> detailData = MapDistinct.getWhereMap(detail, condition, true);

            for (Map<String, Object> oneDetail : detailData) {
                ColumnDataValue dcp_shopSettBillDetail = new ColumnDataValue();
                dcp_shopSettBillDetail.add("EID", DataValues.newString(oneDetail.get("EID").toString()));
                dcp_shopSettBillDetail.add("SHOPID", DataValues.newString(oneDetail.get("SHOPID").toString()));
                dcp_shopSettBillDetail.add("CORP", DataValues.newString(oneDetail.get("CORP").toString()));
                dcp_shopSettBillDetail.add("RECONNO", DataValues.newString(reconNo));
                dcp_shopSettBillDetail.add("ITEM", DataValues.newString(++item));
                dcp_shopSettBillDetail.add("SOURCENO", DataValues.newString(oneDetail.get("SALENO").toString()));
                dcp_shopSettBillDetail.add("SOURCEITEM", DataValues.newString(oneDetail.get("ITEM").toString()));
                dcp_shopSettBillDetail.add("PLUBARCODE", DataValues.newString(oneDetail.get("PLUBARCODE").toString()));
                dcp_shopSettBillDetail.add("PLUNO", DataValues.newString(oneDetail.get("PLUNO").toString()));
                dcp_shopSettBillDetail.add("FEATURENO", DataValues.newString(oneDetail.get("FEATURENO").toString()));
                dcp_shopSettBillDetail.add("TAXCODE", DataValues.newString(oneDetail.get("TAXCODE").toString()));

                dcp_shopSettBillDetail.add("OLDPRICE", DataValues.newString(tryGetDirNumber(dir,oneDetail.get("OLDPRICE").toString())));
                dcp_shopSettBillDetail.add("PRICE", DataValues.newString(oneDetail.get("PRICE").toString()));
                dcp_shopSettBillDetail.add("QTY", DataValues.newString(tryGetDirNumber(dir,oneDetail.get("QTY").toString()) ));
                dcp_shopSettBillDetail.add("SUNIT", DataValues.newString(oneDetail.get("UNIT").toString()));
                dcp_shopSettBillDetail.add("DISCAMT", DataValues.newString(oneDetail.get("DISC").toString()));
                dcp_shopSettBillDetail.add("AMT", DataValues.newString(tryGetDirNumber(dir,oneDetail.get("AMT").toString())));
                dcp_shopSettBillDetail.add("BTAMT", DataValues.newString(tryGetDirNumber(dir,oneDetail.get("AMT").toString())));
                dcp_shopSettBillDetail.add("LCYTATAMT", DataValues.newString(tryGetDirNumber(dir,oneDetail.get("AMT").toString())));
                dcp_shopSettBillDetail.add("ISGIFT", DataValues.newString(oneDetail.get("ISGIFT").toString()));
//                dcp_shopSettBillDetail.add("CARDNO", DataValues.newString(oneDetail.get("EID").toString()));
//                dcp_shopSettBillDetail.add("SENDPAY", DataValues.newString(oneDetail.get("EID").toString()));
                dcp_shopSettBillDetail.add("SHOPQTY", DataValues.newString(tryGetDirNumber(dir,oneDetail.get("QTY").toString())));

                dbs.add(new DataProcessBean(DataBeans.getInsBean("DCP_SHOPSETTBILLDETAIL", dcp_shopSettBillDetail)));
            }

            ColumnDataValue uptCondition = new ColumnDataValue();
            ColumnDataValue uptValue = new ColumnDataValue();
            uptCondition.add("EID", oneMaster.get("EID").toString());
            uptCondition.add("SHOPID", oneMaster.get("SHOPID").toString());
            uptCondition.add("TYPE", oneMaster.get("TYPE").toString());
            uptCondition.add("BDATE", oneMaster.get("BDATE").toString());
            uptCondition.add("CUSTOMERNO", oneMaster.get("CUSTOMERNO").toString());

            uptValue.add("OFNO", reconNo);
            uptValue.add("IS_CREATE", "Y");

            dbs.add(new DataProcessBean(DataBeans.getUptBean("DCP_SALE", uptCondition, uptValue)));

        }

    }

    private double tryGetDirNumber(int dir,String d){
        return Double.parseDouble(d) * dir;
    }

    private String getReconNo(String eId, String shopId) throws Exception {

        String templateNo = null;

        String sql = "select F_DCP_GETBILLNO('" + eId + "','" + shopId + "' ) TEMPLATENO FROM dual";
        List<Map<String, Object>> getQData = StaticInfo.dao.executeQuerySQL(sql, null);

        if (getQData != null && !getQData.isEmpty()) {
            templateNo = (String) getQData.get(0).get("TEMPLATENO");
        } else {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "取加工任务单号失败！");
        }
        return templateNo;

    }


}