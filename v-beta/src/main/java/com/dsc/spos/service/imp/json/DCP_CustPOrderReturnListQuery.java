package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_CustPOrderReturnListQueryReq;
import com.dsc.spos.json.cust.res.DCP_CustPOrderReturnListQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.DateFormatUtils;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_CustPOrderReturnListQuery extends SPosBasicService<DCP_CustPOrderReturnListQueryReq, DCP_CustPOrderReturnListQueryRes> {


    @Override
    protected boolean isVerifyFail(DCP_CustPOrderReturnListQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CustPOrderReturnListQueryReq> getRequestType() {
        return new TypeToken<DCP_CustPOrderReturnListQueryReq>() {
        };
    }

    @Override
    protected DCP_CustPOrderReturnListQueryRes getResponseType() {
        return new DCP_CustPOrderReturnListQueryRes();
    }

    @Override
    protected DCP_CustPOrderReturnListQueryRes processJson(DCP_CustPOrderReturnListQueryReq req) throws Exception {
        DCP_CustPOrderReturnListQueryRes res = this.getResponseType();

        res.setDatas(new ArrayList<>());

        List<Map<String, Object>> getQData = this.doQueryData(getQuerySql(req), null);
        int totalRecords = 0;    //总笔数
        int totalPages = 0;        //总页数
        if (getQData != null && !getQData.isEmpty()) {
            String num = getQData.get(0).get("NUM").toString();
            totalRecords = Integer.parseInt(num);
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
            for (Map<String, Object> oneData : getQData) {

                DCP_CustPOrderReturnListQueryRes.Datas datas = res.new Datas();

                datas.setItem(StringUtils.toString(oneData.get("ITEM"), ""));
                datas.setUnitName(StringUtils.toString(oneData.get("UNAME"), ""));
                datas.setFeatureName(StringUtils.toString(oneData.get("FEATURENAME"), ""));
                datas.setReturnQty(StringUtils.toString(oneData.get("RETURNQTY"), ""));
                datas.setPluNo(StringUtils.toString(oneData.get("PLUNO"), ""));
                datas.setPluName(StringUtils.toString(oneData.get("PLU_NAME"), ""));
                datas.setPluBarcode(StringUtils.toString(oneData.get("PLUBARCODE"), ""));
                datas.setCanReturnQty(StringUtils.toString(oneData.get("CANRETURNQTY"), ""));
                datas.setStockOutQty(StringUtils.toString(oneData.get("STOCKOUTQTY"), ""));
                datas.setTaxCode(StringUtils.toString(oneData.get("TAXCODE"), ""));
                datas.setListImage(StringUtils.toString(oneData.get("LISTIMAGE"), ""));
                datas.setSpec(StringUtils.toString(oneData.get("SPEC"), ""));
                datas.setTaxRate(StringUtils.toString(oneData.get("TAXRATE"), ""));
                datas.setBaseUnit(StringUtils.toString(oneData.get("BASEUNIT"), ""));
                datas.setUnit(StringUtils.toString(oneData.get("UNIT"), ""));
                datas.setReceivingQty(StringUtils.toString(oneData.get("RECEIVINGQTY"), ""));
                datas.setBDate(StringUtils.toString(oneData.get("BDATE"), ""));
                datas.setInclTax(StringUtils.toString(oneData.get("INCLTAX"), ""));
                datas.setPOrderNo(StringUtils.toString(oneData.get("PORDERNO"), ""));
                datas.setQty(StringUtils.toString(oneData.get("QTY"), ""));
                datas.setFeatureNo(StringUtils.toString(oneData.get("FEATURENO"), ""));
                datas.setIsGift(StringUtils.toString(oneData.get("ISGIFT"), ""));
                datas.setRefPurPrice(StringUtils.toString(oneData.get("REFPURPRICE"), ""));
                datas.setTaxCalType(StringUtils.toString(oneData.get("TAXCALTYPE"), ""));
                datas.setUnitRatio(StringUtils.toString(oneData.get("UNITRATIO"), ""));
                datas.setRetailPrice(StringUtils.toString(oneData.get("RETAILPRICE"), ""));
                datas.setPrice(StringUtils.toString(oneData.get("PRICE"), ""));

                res.getDatas().add(datas);
            }
        }

        res.setSuccess(true);
        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        res.setTotalRecords(totalRecords);
        res.setTotalPages(totalPages);

        return res;
    }


    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_CustPOrderReturnListQueryReq req) throws Exception {

        //計算起啟位置
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();
        int startRow = (pageNumber - 1) * pageSize;

        StringBuilder sb = new StringBuilder();
        sb.append("select * from (")
                .append(" SELECT count(*) over() num, row_number() over (order by a.BDATE DESC,b.PORDERNO DESC,b.ITEM ) rn, ")
                .append(" a.bDate,b.*,d.LISTIMAGE,gl1.PLU_NAME,fl1.FEATURENAME,ul1.UNAME ")
                .append(" ,c.SPEC,NVL(rs.PQTY,0)-NVL(rs.STOCKIN_QTY,0) as receivingQty,")
                .append(" NVL(b.STOCKOUTQTY,0)-NVL(b.RETURNQTY,0)-(NVL(rs.PQTY,0)-NVL(rs.STOCKIN_QTY,0)) as canReturnQty ")
                .append(" FROM DCP_CUSTOMERPORDER a ")
                .append(" LEFT JOIN DCP_CUSTOMERPORDER_DETAIL b on a.eid=b.eid and a.SHOPNO=b.SHOPNO and a.PORDERNO=b.PORDERNO ")
                .append(" LEFT JOIN DCP_GOODS c on c.eid=b.eid and c.PLUNO=b.PLUNO  ")
                .append(" LEFT JOIN DCP_GOODSIMAGE d ON d.EID=b.EID AND d.PLUNO=b.PLUNO ")
                .append(" LEFT JOIN DCP_UNIT_LANG ul1 on ul1.eid=b.eid and ul1.UNIT=b.UNIT ")
                .append(" LEFT JOIN DCP_GOODS_LANG gl1 on gl1.eid=b.eid and gl1.PLUNO=b.PLUNO and gl1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_GOODS_FEATURE_LANG fl1 on fl1.eid=b.eid and fl1.PLUNO=b.PLUNO and fl1.FEATURENO=b.FEATURENO and fl1.LANG_TYPE=' ").append(req.getLangType()).append("'")
                .append(" LEFT JOIN ( ")
                .append("           SELECT EID,OTYPE,OFNO,OITEM,SUM(PQTY) PQTY,SUM(STOCKIN_QTY) STOCKIN_QTY  ")
                .append("           FROM  DCP_RECEIVING_DETAIL ")
                .append("           GROUP BY EID,OTYPE,OFNO,OITEM ")
                .append(" ) rs ON rs.EID=b.EID and rs.OFNO=b.PORDERNO and rs.OITEM=b.ITEM and rs.OTYPE='6' ")

        ;
        sb.append(" WHERE a.EID='").append(req.geteId()).append("'");
        sb.append(" AND NVL(b.STOCKOUTQTY,0)-NVL(b.RETURNQTY,0)-(NVL(rs.PQTY,0)-NVL(rs.STOCKIN_QTY,0)) >0 ");

        if (StringUtils.isNotEmpty(req.getRequest().getPOrderNo())) {
            sb.append(" and a.PORDERNO ='").append(req.getRequest().getPOrderNo()).append("' ");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getCustomer())) {
            sb.append(" and a.CUSTOMERNO ='").append(req.getRequest().getCustomer()).append("' ");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getBeginDate())) {
            sb.append(" and a.BDATE >= '").append(DateFormatUtils.getPlainDate(req.getRequest().getBeginDate())).append("' ");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getEndDate())) {
            sb.append(" and a.BDATE <= '").append(DateFormatUtils.getPlainDate(req.getRequest().getEndDate())).append("' ");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getKeyTxt())) {
//            订单号/品号/品名/条码
            sb.append(" and ( a.PORDERNO like '%%").append(req.getRequest().getKeyTxt()).append("%%' ")
                    .append(" OR b.PLUNO like '%% ").append(req.getRequest().getKeyTxt()).append("%%' ")
                    .append(" OR gl1.PLU_NAME like '%% ").append(req.getRequest().getKeyTxt()).append("%%' ")
                    .append(" OR b.PLUBARCODE like '%% ").append(req.getRequest().getKeyTxt()).append("%%' ")
                    .append(" ) ")
            ;
        }


        sb.append(") temp  ");

        sb.append(" where  rn>").append(startRow).append(" and rn<=").append(startRow + pageSize).append("  ").append(" ");
        sb.append(" ORDER BY rn ");
        return sb.toString();
    }
}
