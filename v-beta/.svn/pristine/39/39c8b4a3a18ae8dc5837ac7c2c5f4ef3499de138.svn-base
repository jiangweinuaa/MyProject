package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_PurOrderBookingListQueryReq;
import com.dsc.spos.json.cust.res.DCP_PurOrderBookingListQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_PurOrderBookingListQuery extends SPosBasicService<DCP_PurOrderBookingListQueryReq, DCP_PurOrderBookingListQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_PurOrderBookingListQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_PurOrderBookingListQueryReq> getRequestType() {
        return new TypeToken<DCP_PurOrderBookingListQueryReq>() {

        };
    }

    @Override
    protected DCP_PurOrderBookingListQueryRes getResponseType() {
        return new DCP_PurOrderBookingListQueryRes();
    }

    @Override
    protected DCP_PurOrderBookingListQueryRes processJson(DCP_PurOrderBookingListQueryReq req) throws Exception {
        DCP_PurOrderBookingListQueryRes res = this.getResponseType();

            String sql = this.getQuerySql(req);
            String[] conditionValues1 = {}; //查詢條件
            int totalRecords;                //总笔数
            int totalPages;
            List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, conditionValues1);
            res.setDatas(new ArrayList<>());
            if (getQDataDetail != null && !getQDataDetail.isEmpty()) {
                totalRecords = Integer.parseInt(getQDataDetail.get(0).get("NUM").toString());

                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

                res.setPageNumber(req.getPageNumber());
                res.setPageSize(req.getPageSize());
                res.setTotalRecords(totalRecords);
                res.setTotalPages(totalPages);

                for (Map<String, Object> oneData : getQDataDetail) {
                    DCP_PurOrderBookingListQueryRes.Data data = res.new Data();
                    data.setPurOrderNo(StringUtils.toString(oneData.get("PURORDERNO"), ""));
                    data.setItem(StringUtils.toString(oneData.get("ITEM"), ""));
                    data.setItem2(StringUtils.toString(oneData.get("ITEM2"), ""));
                    data.setPluNo(StringUtils.toString(oneData.get("PLUNO"), ""));
                    data.setPluName(StringUtils.toString(oneData.get("PLUNAME"), ""));
                    data.setSpec(StringUtils.toString(oneData.get("SPEC"), " "));
                    data.setPluBarcode(StringUtils.toString(oneData.get("PLUBARCODE"), " "));
                    data.setFeatureNo(StringUtils.toString(oneData.get("FEATURENO"), " "));
                    data.setFeatureName(StringUtils.toString(oneData.get("FEATURENAME"), " "));
                    data.setPUnit(StringUtils.toString(oneData.get("PURUNIT"), " "));
                    data.setPUnitName(StringUtils.toString(oneData.get("PUNITNAME"), " "));

                    data.setPurQty(StringUtils.toString(oneData.get("PURQTY"), ""));
                    data.setBookQty(StringUtils.toString(oneData.get("BOOKQTY"), ""));
                    data.setCanBookQty(StringUtils.toString(oneData.get("CANBOOKQTY"), ""));
                    data.setRefPurPrice(StringUtils.toString(oneData.get("PURPRICE"), ""));
                    data.setRefPurAmt(StringUtils.toString(oneData.get("PURAMT"), ""));
                    data.setSupplier(StringUtils.toString(oneData.get("SUPPLIER"), ""));
                    data.setSupplierName(StringUtils.toString(oneData.get("SUPPLIERNAME"), ""));
                    data.setBDate(StringUtils.toString(oneData.get("BDATE"), ""));
                    data.setDistriCenter(StringUtils.toString(oneData.get("DISTRICENTER"), ""));
                    data.setDistriCenterName(StringUtils.toString(oneData.get("DISTRICENTERNAME"), ""));
                    data.setReceiptOrgNo(StringUtils.toString(oneData.get("RECEIPTORGNO"), ""));
                    data.setReceiptOrgName(StringUtils.toString(oneData.get("RECEIPTORGNAME"), ""));
                    data.setPurType(StringUtils.toString(oneData.get("PURTYPE"), ""));
                    data.setReceivePrice(StringUtils.toString(oneData.get("RECEIVEPRICE"), ""));
                    BigDecimal receiveAmt = (new BigDecimal(Check.Null(data.getReceivePrice())?"0":data.getReceivePrice()).multiply(new BigDecimal(data.getCanBookQty()))).setScale(2, BigDecimal.ROUND_HALF_UP);
                    data.setReceiveAmt(receiveAmt.toString());

                    data.setCategory(oneData.get("CATEGORY").toString());
                    data.setCategoryName(oneData.get("CATEGORYNAME").toString());
                    data.setTaxCode(oneData.get("TAXCODE").toString());
                    data.setTaxRate(oneData.get("TAXRATE").toString());
                    data.setInclTax(oneData.get("INCLTAX").toString());
                    data.setTaxCalType(oneData.get("TAXCALTYPE").toString());


                    res.getDatas().add(data);
                }
            }

            res.setSuccess(true);

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_PurOrderBookingListQueryReq req) throws Exception {
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();
        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        StringBuilder querySql = new StringBuilder();
        querySql.append(
                " SELECT * FROM (" +
                        " SELECT COUNT(1) over() NUM, dense_rank() over(ORDER BY a.PURORDERNO,a.PLUNO) rn, " +
                        " a.*," +
                        " b.SNAME SUPPLIERNAME,c.PLU_NAME PLUNAME,d.SPEC," +
                        " e.FEATURENAME, " +
                        " f.SNAME RECEIPTORGNAME,g.SNAME DISTRICENTERNAME,d.MAINBARCODE PLUBARCODE,h.category_name as categoryname  "+
                        " FROM (" +
                        "   SELECT DISTINCT  a.EID,a.ORGANIZATIONNO, " +
                        "   a.PURORDERNO,a.BDATE, " +
                        "   a.SUPPLIER,a.STATUS,b.ITEM,b.ITEM2,b.PLUNO,b.FEATURENO," +
                        "   b.PURUNIT,e.UNAME as PUNITNAME,NVL(b.PURQTY,0) as PURQTY,NVL(c.PQTY,0) BOOKQTY, " +
                        "   b.PURPRICE,b.PURAMT,a.DISTRICENTER,a.RECEIPTORGNO,a.PURTYPE,b.PURQTY-NVL(c.PQTY,0) AS CANBOOKQTY,b.receiveprice,f.category," +
                        "   b.taxcode,b.taxrate,b.incltax,b.taxcaltype " +
                        "   FROM DCP_PURORDER a " +
                        "   LEFT JOIN DCP_PURORDER_DELIVERY b ON a.EID=b.EID  AND a.PURORDERNO=b.PURORDERNO " +
                        "   LEFT JOIN DCP_PURORDER_DETAIL F ON a.EID=F.EID  AND a.PURORDERNO=F.PURORDERNO AND b.item2=f.item " +
                        "   LEFT JOIN ( SELECT a.EID,a.OFNO,a.OITEM,SUM(CASE WHEN b.STATUS=6 THEN PQTY ELSE 0 END) PQTY FROM DCP_RECEIVING_DETAIL a " +
                        "  left join dcp_receiving b on a.eid=b.eid and a.organizationno=b.organizationno and a.RECEIVINGNO=b.RECEIVINGNO  " +
                        "   GROUP BY a.EID,a.OFNO,a.OITEM ) c ON a.EID=c.EID AND c.OFNO=a.PURORDERNO AND c.OITEM=b.ITEM " +
//                        "   LEFT JOIN DCP_RECEIVING d ON d.EID=c.EID  AND d.RECEIVINGNO=c.RECEIVINGNO " +
                        "   LEFT JOIN DCP_UNIT_LANG e ON e.EID=a.EID ANd e.UNIT=b.PURUNIT and e.LANG_TYPE='" + req.getLangType() + "'" +
                        "   ) a" +
                        "   LEFT JOIN DCP_BIZPARTNER b on b.EID=a.EID AND b.BIZPARTNERNO=a.SUPPLIER " +
                        "   LEFT JOIN DCP_GOODS_LANG c ON a.EID=c.EID and a.PLUNO=c.PLUNO AND c.LANG_TYPE='" + req.getLangType() + "'" +
                        "   LEFT JOIN DCP_GOODS d ON a.EID=d.EID and a.PLUNO=d.PLUNO " +
                        "   LEFT JOIN DCP_GOODS_FEATURE_LANG e ON a.EID=e.EID and a.PLUNO=e.PLUNO AND e.FEATURENO=a.FEATURENO AND e.LANG_TYPE='" + req.getLangType() + "'" +
                        "   LEFT JOIN DCP_ORG f ON a.EID=f.EID AND a.RECEIPTORGNO=f.ORGANIZATIONNO"+
                        "   LEFT JOIN DCP_ORG g ON a.EID=g.EID AND a.DISTRICENTER=g.ORGANIZATIONNO" +
                        "   left join DCP_CATEGORY_LANG h on h.eid=a.eid and h.category=a.category and h.lang_type='"+req.getLangType()+"' "+
                        " WHERE  a.EID='" + req.geteId() + "' AND CANBOOKQTY >0 "
        );

        if (StringUtils.isNotEmpty(req.getRequest().getSupplierNo())){
            querySql.append(" AND a.SUPPLIER='").append(req.getRequest().getSupplierNo()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getPurOrderNo())){
            querySql.append(" AND a.PURORDERNO='").append(req.getRequest().getPurOrderNo()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getReceiptOrgNo())) {
            querySql.append(" AND a.RECEIPTORGNO='").append(req.getRequest().getReceiptOrgNo()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getKeyTxt())){
            String keyTxt = req.getRequest().getKeyTxt();
            querySql.append(" AND ( a.PLUNO like '%%")
                    .append(keyTxt)
                    .append("%%' OR c.PLU_NAME like '%%")
                    .append(keyTxt)
                    .append("%%' OR d.MAINBARCODE like '%%")
                    .append(keyTxt).append("%%' )");
        }

        querySql.append(" ) temp ");


        querySql.append(" WHERE rn> ")
                .append(startRow)
                .append(" and rn<= ")
                .append(startRow + pageSize)
                .append(" ORDER BY rn ");

        return querySql.toString();
    }
}
