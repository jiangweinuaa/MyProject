package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_InterSettlementQueryReq;
import com.dsc.spos.json.cust.res.DCP_InterSettlementQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.DateFormatUtils;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_InterSettlementQuery extends SPosBasicService<DCP_InterSettlementQueryReq, DCP_InterSettlementQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_InterSettlementQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_InterSettlementQueryReq> getRequestType() {
        return new TypeToken<DCP_InterSettlementQueryReq>() {
        };
    }

    @Override
    protected DCP_InterSettlementQueryRes getResponseType() {
        return new DCP_InterSettlementQueryRes();
    }

    @Override
    protected DCP_InterSettlementQueryRes processJson(DCP_InterSettlementQueryReq req) throws Exception {
        DCP_InterSettlementQueryRes res = this.getResponseType();
        int totalRecords;                //总笔数
        int totalPages;
        String[] conditionValues1 = {}; //查詢條件
        List<Map<String, Object>> getData = this.doQueryData(getQuerySql(req), conditionValues1);
        res.setRequest(res.new Request());
        if (getData != null && !getData.isEmpty()) {
            //总页数
            String num = getData.get(0).get("NUM").toString();
            totalRecords = Integer.parseInt(num);

            //算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);
            res.getRequest().setInterList(new ArrayList<>());
            for (Map<String, Object> data : getData) {

                DCP_InterSettlementQueryRes.InterList oneInter = res.new InterList();
                res.getRequest().getInterList().add(oneInter);

                oneInter.setCorp(data.get("CORP").toString());
                oneInter.setYear(data.get("YEAR").toString());
                oneInter.setMonth(data.get("MONTH").toString());
                oneInter.setDataType(data.get("DATATYPE").toString());
                oneInter.setInterTradeType(data.get("INTERTRANSTYPE").toString());
                oneInter.setBDate(DateFormatUtils.getDate(data.get("BDATE").toString()));
                oneInter.setBillNo(data.get("BILLNO").toString());
                oneInter.setItem(data.get("ITEM").toString());
                oneInter.setPluNo(data.get("PLUNO").toString());
                oneInter.setPluName(data.get("PLU_NAME").toString());
                oneInter.setFeatureNo(data.get("FEATURENO").toString());
                oneInter.setFeatureName(data.get("FEATURENAME").toString());
                oneInter.setPriceUnit(data.get("PRICEUNIT").toString());
                oneInter.setPriceUnitName(data.get("PRICEUNITNAME").toString());
                oneInter.setBillQty(data.get("BILLQTY").toString());
                oneInter.setFee(data.get("FEE").toString());
                oneInter.setFeeName(data.get("FEE_NAME").toString());
                oneInter.setCurrency(data.get("CURRENCY").toString());
                oneInter.setCurrencyName(data.get("CURRENCYNAME").toString());
                oneInter.setTaxRate(data.get("TAXRATE").toString());
                oneInter.setBillPrice(data.get("BILLPRICE").toString());
                oneInter.setDirection(data.get("DIRECTION").toString());
                oneInter.setPreTaxAmt(data.get("PRETAXAMT").toString());
                oneInter.setAmt(data.get("AMT").toString());
                oneInter.setDemandOrg(data.get("DEMANDORG").toString());
                oneInter.setDemandOrgName(data.get("DEMANDORGNAME").toString());
                oneInter.setDemandCorp(data.get("DEMANDCORP").toString());
                oneInter.setDemandCorpName(data.get("DEMANDCORPNAME").toString());
                oneInter.setSupplyOrg(data.get("SUPPLYORG").toString());
                oneInter.setSupplyOrgName(data.get("SUPPLYORGNAME").toString());
                oneInter.setSupplyCorp(data.get("SUPPLYCORP").toString());
                oneInter.setSupplyCorpName(data.get("SUPPLYCORPNAME").toString());
                oneInter.setDistribuOrg(data.get("DISTRIBUORG").toString());
                oneInter.setDistribuOrgName(data.get("DISTRIBUORGNAME").toString());
                oneInter.setDistribuCorp(data.get("DISTRIBUCORP").toString());
                oneInter.setDistribuCorpName(data.get("DISTRIBUCORPNAME").toString());
                oneInter.setOutInDirection(data.get("OUTINDIRECTION").toString());
                oneInter.setOrganizationNo(data.get("ORGANIZATIONNO").toString());
                oneInter.setOrganizationNoName(data.get("ORGANIZATIONNAME").toString());
                oneInter.setIntertradeOrg(data.get("INTERTRADEORG").toString());
                oneInter.setIntertradeOrgName(data.get("INTERTRADEORGNAME").toString());
                oneInter.setIntertradeCorp(data.get("INTERTRADECORP").toString());
                oneInter.setIntertradeCorpName(data.get("INTERTRADECORPNAME").toString());
                oneInter.setProcessNo(data.get("PROCESSNO").toString());
                oneInter.setStatus(data.get("STATUS").toString());
                oneInter.setUnsettAmt(data.get("UNSETTAMT").toString());
                oneInter.setSettledAmt(data.get("SETTLEDAMT").toString());
                oneInter.setUnpostedAmt(data.get("UNPOSTEDAMT").toString());
                oneInter.setPostedAmt(data.get("POSTEDAMT").toString());
                oneInter.setInvoiceQty(data.get("INVOICEQTY").toString());
                oneInter.setInvoiceAmt(data.get("INVOICEAMT").toString());
                oneInter.setTaxCode(data.get("TAXCODE").toString());
                oneInter.setTaxName(data.get("TAXNAME").toString());
                oneInter.setSourceType(data.get("SOURCETYPE").toString());

            }

        }

        res.setSuccess(true);
        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_InterSettlementQueryReq req) throws Exception {
        StringBuilder sb = new StringBuilder();

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        sb.append(" SELECT * FROM( ")
                .append(" SELECT row_number() OVER (ORDER BY a.BILLNO,a.ITEM) AS RN, ")
                .append(" COUNT(*) OVER ( ) NUM,")
                .append(" a.* ")
                .append(" ,f.FEE_NAME,gl1.PLU_NAME,ul1.UNAME PRICEUNITNAME,gfl1.FEATURENAME ")
                .append(" ,cl1.NAME CURRENCYNAME ")
                .append(" ,ol1.SNAME CORPNAME,ol2.SNAME ORGANIZATIONNAME ")
                .append(" ,ol3.SNAME DEMANDORGNAME,ol4.SNAME DEMANDCORPNAME ")
                .append(" ,ol5.SNAME SUPPLYORGNAME,ol6.SNAME SUPPLYCORPNAME ")
                .append(" ,ol7.SNAME DISTRIBUORGNAME,ol8.SNAME DISTRIBUCORPNAME ")
                .append(" ,ol9.SNAME INTERTRADEORGNAME,ol10.SNAME INTERTRADECORPNAME ")
                .append(" ,tl1.TAXNAME ")
                .append(" FROM DCP_INTERSETTLEMENT a ")
                .append(" LEFT JOIN DCP_GOODS_LANG gl1 on gl1.eid=a.eid and gl1.PLUNO=a.PLUNO and gl1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_FEE f on a.eid=f.eid and a.FEE=f.FEE ")
                .append(" LEFT JOIN DCP_GOODS_FEATURE_LANG gfl1 on a.eid=gfl1.eid AND a.PLUNO=gfl1.PLUNO and a.FEATURENO=gfl1.FEATURENO and gfl1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_UNIT_LANG ul1 on a.eid=ul1.eid and a.PRICEUNIT=ul1.UNIT and ul1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_CURRENCY_LANG cl1 on a.eid=cl1.eid and a.CURRENCY=cl1.CURRENCY AND NATION='CN' and cl1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_TAXCATEGORY_LANG tl1 on a.eid=tl1.eid and a.TAXCODE=tl1.TAXCODE AND TAXAREA='CN' and tl1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_ORG_LANG ol1 on a.eid=ol1.eid and a.CORP=ol1.ORGANIZATIONNO and ol1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_ORG_LANG ol2 on a.eid=ol2.eid and a.ORGANIZATIONNO=ol2.ORGANIZATIONNO and ol2.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_ORG_LANG ol3 on a.eid=ol3.eid and a.DEMANDORG=ol3.ORGANIZATIONNO and ol3.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_ORG_LANG ol4 on a.eid=ol4.eid and a.DEMANDCORP=ol4.ORGANIZATIONNO and ol4.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_ORG_LANG ol5 on a.eid=ol5.eid and a.SUPPLYORG=ol5.ORGANIZATIONNO and ol5.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_ORG_LANG ol6 on a.eid=ol6.eid and a.SUPPLYCORP=ol6.ORGANIZATIONNO and ol6.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_ORG_LANG ol7 on a.eid=ol7.eid and a.DISTRIBUORG=ol7.ORGANIZATIONNO and ol7.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_ORG_LANG ol8 on a.eid=ol8.eid and a.DISTRIBUCORP=ol8.ORGANIZATIONNO and ol8.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_ORG_LANG ol9 on a.eid=ol9.eid and a.INTERTRADEORG=ol9.ORGANIZATIONNO and ol9.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_ORG_LANG ol10 on a.eid=ol10.eid and a.INTERTRADECORP=ol10.ORGANIZATIONNO and ol10.LANG_TYPE='").append(req.getLangType()).append("'")

        ;

        sb.append(" WHERE a.EID='").append(req.geteId()).append("'");
        if (StringUtils.isNotEmpty(req.getRequest().getCorp())) {
            sb.append(" and a.CORP='").append(req.getRequest().getCorp()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getCorpName())) {
            sb.append(" and ( ol1.ORG_NAME like '%%").append(req.getRequest().getCorpName()).append("%%'")
                    .append(" OR ol2.ORG_NAME like '%%").append(req.getRequest().getCorpName()).append("%%'")
                    .append(")");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getYear())) {
            sb.append(" and a.YEAR='").append(req.getRequest().getYear()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getMonth())) {
            sb.append(" and a.MONTH='").append(req.getRequest().getMonth()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getDemandCorp())) {
            sb.append(" AND a.DEMANDCORP='").append(req.getRequest().getDemandCorp()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getSupplyOrg())) {
            sb.append(" AND a.SUPPLYORG='").append(req.getRequest().getSupplyOrg()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getBillNo())) {
            sb.append(" AND a.BILLNO='").append(req.getRequest().getBillNo()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getPluNo())) {
            sb.append(" AND a.PLUNO='").append(req.getRequest().getPluNo()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getPluName())) {
            sb.append(" AND gl1.PLU_NAME like '%%").append(req.getRequest().getPluName()).append("%%'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getFee())) {
            sb.append(" AND a.FEE='").append(req.getRequest().getFee()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getFeeName())) {
            sb.append(" AND f.FEE_NAME like '%%").append(req.getRequest().getFeeName()).append("%%'");
        }
        sb.append("  ) a "
                + "    WHERE rn> " + startRow + " and rn<= " + (startRow + pageSize)
                + " ORDER BY rn ");

        return sb.toString();
    }
}
