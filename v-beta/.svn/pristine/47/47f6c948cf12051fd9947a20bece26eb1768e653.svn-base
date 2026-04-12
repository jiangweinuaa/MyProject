package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_EInvoiceQueryReq;
import com.dsc.spos.json.cust.res.DCP_EInvoiceQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 开票记录查询
 * @author: wangzyc
 * @create: 2022-03-15
 */
public class DCP_EInvoiceQuery extends SPosBasicService<DCP_EInvoiceQueryReq, DCP_EInvoiceQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_EInvoiceQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_EInvoiceQueryReq> getRequestType() {
        return new TypeToken<DCP_EInvoiceQueryReq>(){};
    }

    @Override
    protected DCP_EInvoiceQueryRes getResponseType() {
        return new DCP_EInvoiceQueryRes();
    }

    @Override
    protected DCP_EInvoiceQueryRes processJson(DCP_EInvoiceQueryReq req) throws Exception {
        DCP_EInvoiceQueryRes res = this.getResponseType();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            int totalRecords = 0; // 总笔数
            int totalPages = 0;

            res.setDatas(new ArrayList<>());
            String sql = this.getQuerySql(req);
            List<Map<String, Object>> getInvoiceDetalis = this.doQueryData(sql, null);
            if(!CollectionUtils.isEmpty(getInvoiceDetalis)){
                String num = getInvoiceDetalis.get(0).getOrDefault("NUM", "0").toString();
                totalRecords = Integer.parseInt(num);
                // 算總頁數
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

                // 过滤发票单号
                Map<String, Boolean> condition = new HashMap<String, Boolean>(); // 查詢條件
                condition.put("INVOICEBILLNO", true);
                // 调用过滤函数
                List<Map<String, Object>> getInvoice = MapDistinct.getMap(getInvoiceDetalis, condition);

                // 过滤业务单号
                Map<String, Boolean> condition2 = new HashMap<String, Boolean>(); // 查詢條件
                condition2.put("INVOICEBILLNO", true);
                condition2.put("SOURCEBILLNO", true);
                // 调用过滤函数
                List<Map<String, Object>> getInvoiceSourceBillNos = MapDistinct.getMap(getInvoiceDetalis, condition2);

                // 过滤业务单号明细
                Map<String, Boolean> condition3 = new HashMap<String, Boolean>(); // 查詢條件
                condition3.put("INVOICEBILLNO", true);
                condition3.put("ITEM", true);
                // 调用过滤函数
                List<Map<String, Object>> getInvoiceSourceDetails = MapDistinct.getMap(getInvoiceDetalis, condition3);

                for (Map<String, Object> map1 : getInvoice) {
                    String invoicebillNo = map1.get("INVOICEBILLNO").toString();
                    String sourcebillType = map1.get("SOURCEBILLTYPE").toString();
                    String platformtype = map1.get("PLATFORMTYPE").toString();
                    String platformname = map1.get("PLATFORMNAME").toString();
                    String invoicekind = map1.get("INVOICEKIND").toString();
                    String invoicetype = map1.get("INVOICETYPE").toString();
                    String amt = map1.get("AMT").toString();
                    String taxamt = map1.get("TAXAMT").toString();
                    String extaxamt = map1.get("EXTAXAMT").toString();
                    String drawer = map1.get("DRAWER").toString();
                    String receiver = map1.get("RECEIVER").toString();
                    String reviewer = map1.get("REVIEWER").toString();
                    String saletaxnum = map1.get("SALETAXNUM").toString();
                    String saletel = map1.get("SALETEL").toString();
                    String saleaddress = map1.get("SALEADDRESS").toString();
                    String salebank = map1.get("SALEBANK").toString();
                    String saleaccount = map1.get("SALEACCOUNT").toString();
                    String buyername = map1.get("BUYERNAME").toString();
                    String buyertaxnum = map1.get("BUYERTAXNUM").toString();
                    String buyertel = map1.get("BUYERTEL").toString();
                    String buyeraddress = map1.get("BUYERADDRESS").toString();
                    String buyerbank = map1.get("BUYERBANK").toString();
                    String buyeraccount = map1.get("BUYERACCOUNT").toString();
                    String buyerphone = map1.get("BUYERPHONE").toString();
                    String email = map1.get("EMAIL").toString();
                    String isapply = map1.get("ISAPPLY").toString();
                    String applydate = map1.get("APPLYDATE").toString();
                    String applystatusmsg_platform = map1.get("APPLYSTATUSMSG_PLATFORM").toString();
                    String status = map1.get("STATUS").toString();
                    String statusmsg_platform = map1.get("STATUSMSG_PLATFORM").toString();
                    String invoicedate = map1.get("INVOICEDATE").toString();
                    String invoiceserialnum = map1.get("INVOICESERIALNUM").toString();
                    String invoicecode = map1.get("INVOICECODE").toString();
                    String invoiceno = map1.get("INVOICENO").toString();
                    String pdfurl = map1.get("PDFURL").toString();
                    String pictureurl = map1.get("PICTUREURL").toString();
                    String ismanual = map1.get("ISMANUAL").toString();
                    String opno = map1.get("OPNO").toString();
                    String opname = map1.get("OPNAME").toString();

                    DCP_EInvoiceQueryRes.level1Elm lv1 = res.new level1Elm();
                    lv1.setInvoiceBillNo(invoicebillNo);
                    lv1.setSourBillType(sourcebillType);
                    lv1.setPlatformType(platformtype);
                    lv1.setPlatformName(platformname);
                    lv1.setInvoiceType(invoicetype);
                    lv1.setInvoiceKind(invoicekind);
                    lv1.setAmt(amt);
                    lv1.setTaxAmt(taxamt);
                    lv1.setExTaxAmt(extaxamt);
                    lv1.setDrawer(drawer);
                    lv1.setReceiver(receiver);
                    lv1.setReviewer(reviewer);
                    lv1.setSaleTaxNum(saletaxnum);
                    lv1.setSaleTel(saletel);
                    lv1.setSaleAddress(saleaddress);
                    lv1.setSaleBank(salebank);
                    lv1.setSaleAccount(saleaccount);
                    lv1.setBuyerName(buyername);
                    lv1.setBuyerTaxNum(buyertaxnum);
                    lv1.setBuyerTel(buyertel);
                    lv1.setBuyerAddress(buyeraddress);
                    lv1.setBuyerBank(buyerbank);
                    lv1.setBuyerAccount(buyeraccount);
                    lv1.setBuyerPhone(buyerphone);
                    lv1.setEmail(email);
                    lv1.setIsApply(isapply);
                    lv1.setApplyDate(applydate);
                    lv1.setApplyStatusMsg_platforml(applystatusmsg_platform);
                    lv1.setStatus(status);
                    lv1.setStatusMsg_platform(statusmsg_platform);
                    lv1.setInvoiceDate(invoicedate);
                    lv1.setInvoiceSerialNum(invoiceserialnum);
                    lv1.setInvoiceCode(invoicecode);
                    lv1.setInvoiceNo(invoiceno);
                    lv1.setPdfUrl(pdfurl);
                    lv1.setPictureUrl(pictureurl);
                    lv1.setIsManual(ismanual);
                    lv1.setOpNo(opno);
                    lv1.setOpName(opname);

                    lv1.setBusinessList(new ArrayList<>());
                    for (Map<String, Object> map2 : getInvoiceSourceBillNos) {
                        String invoicebillNo2 = map2.get("INVOICEBILLNO").toString();
                        // 过滤不属于次单头单明细
                        if(!invoicebillNo.equals(invoicebillNo2)){
                            continue;
                        }
                        String sourcebillno = map2.get("SOURCEBILLNO").toString();
                        String sourceshopid = map2.get("SOURCESHOPID").toString();
                        String org_name = map2.get("ORG_NAME").toString();

                        DCP_EInvoiceQueryRes.level2Elm lv2 = res.new level2Elm();
                        lv2.setSourceBillNo(sourcebillno);
                        lv2.setSourceShopId(sourceshopid);
                        lv2.setSourceShopName(org_name);
                        lv1.getBusinessList().add(lv2);

                    }
                    lv1.setDetailList(new ArrayList<>());
                    for (Map<String, Object> map3 : getInvoiceSourceDetails) {
                        String invoicebillNo3 = map3.get("INVOICEBILLNO").toString();
                        if(!invoicebillNo.equals(invoicebillNo3)){
                            continue;
                        }
                        String item = map3.get("ITEM").toString();
                        if(Check.Null(item)){
                            continue;
                        }
                        String goodsname = map3.get("GOODSNAME").toString();
                        String anEnum = map3.get("ENUM").toString();
                        String price = map3.get("PRICE").toString();
                        String espbm = map3.get("ESPBM").toString();
                        String etaxrate = map3.get("ETAXRATE").toString();
                        String eamt = map3.get("EAMT").toString();
                        String etaxamt = map3.get("ETAXAMT").toString();
                        String eextaxamt = map3.get("EEXTAXAMT").toString();
                        DCP_EInvoiceQueryRes.level3Elm lv3 = res.new level3Elm();
                        lv3.setItem(item);
                        lv3.setGoodsName(goodsname);
                        lv3.setAmt(eamt);
                        lv3.setNum(anEnum);
                        lv3.setPrice(price);
                        lv3.setSpbm(espbm);
                        lv3.setTaxAmt(etaxamt);
                        lv3.setTaxRate(etaxrate);
                        lv3.setExTaxAmt(eextaxamt);
                        lv1.getDetailList().add(lv3);
                    }
                    res.getDatas().add(lv1);
                }
            }
            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功！");
        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("100");
            res.setServiceDescription("服务执行异常:" + e.getMessage());
        }
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_EInvoiceQueryReq req) throws Exception {
        String sql = "";
        DCP_EInvoiceQueryReq.level1Elm request = req.getRequest();
        String langType = req.getLangType();
        String eId = req.geteId();
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();
        String sourBillType = request.getSourBillType();
        String sourceBillNo = request.getSourceBillNo();
        String applyStatus = request.getApplyStatus();
        String status = request.getStatus();
        String invoiceBillNo = request.getInvoiceBillNo();
        String applyDateBegin = request.getApplyDateBegin();
        String applyDateEnd = request.getApplyDateEnd();
        String invoiceDateBegin = request.getInvoiceDateBegin();
        String invoiceDateEnd = request.getInvoiceDateEnd();

        // 計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT * FROM (  " +
                " SELECT count(DISTINCT a.INVOICEBILLNO) OVER () AS num, DENSE_RANK() OVER (ORDER BY a.INVOICEBILLNO desc,a.CREATETIME desc) AS rn , " +
                " a.INVOICEBILLNO, a.SOURCEBILLTYPE, a.PLATFORMTYPE, b.PLATFORMNAME, a.INVOICEKIND , a.INVOICETYPE, a.AMT, a.TAXAMT, " +
                " a.EXTAXAMT, a.DRAWER , a.RECEIVER, a.REVIEWER,a.SALETAXNUM, a.SALETEL, a.SALEADDRESS , a.SALEBANK, a.SALEACCOUNT, " +
                " a.BUYERNAME, a.BUYERTAXNUM, a.BUYERTEL , a.BUYERADDRESS, a.BUYERBANK, a.BUYERACCOUNT, a.BUYERPHONE, a.EMAIL , a.ISAPPLY, " +
                " a.APPLYDATE, a.applyStatusMsg_platform, a.status, a.STATUSMSG_PLATFORM , a.INVOICEDATE, a.INVOICESERIALNUM, a.INVOICECODE, " +
                " a.INVOICENO, a.PDFURL , a.PICTUREURL, a.ISMANUAL, a.OPNO, a.OPNAME, c.SOURCEBILLNO , c.SOURCESHOPID, d.ORG_NAME, e.ITEM, " +
                " e.GOODSNAME, e.NUM AS enum , e.PRICE,e.SPBM AS espbm, e.TAXRATE AS etaxrate, e.AMT AS eamt, e.TAXAMT AS etaxamt , " +
                " e.EXTAXAMT AS eextaxamt  " +
                " FROM DCP_EINVOICE a  " +
                " LEFT JOIN DCP_FAPIAO_PLATFORM b ON a.PLATFORMTYPE = b.PLATFORMTYPE " +
                " LEFT JOIN DCP_EINVOICE_BUSINESS c ON a.EID = c.EID AND a.INVOICEBILLNO = c.INVOICEBILLNO " +
                " LEFT JOIN DCP_ORG_LANG d ON a.EID = d.EID AND c.SOURCESHOPID = d.ORGANIZATIONNO AND d.STATUS = '100' AND d.LANG_TYPE = '"+langType+"' " +
                " LEFT JOIN DCP_EINVOICE_DETAIL e ON a.EID = e.EID AND a.INVOICEBILLNO = e.INVOICEBILLNO WHERE a.eid = '"+eId+"' ");

        if(!Check.Null(invoiceBillNo)){
            sqlbuf.append(" and a.invoiceBillNo = '"+invoiceBillNo+"'");
        }
        if(!Check.Null(sourBillType)){
            sqlbuf.append(" and a.SOURCEBILLTYPE = '"+sourBillType+"'");
        }
        if(!Check.Null(sourceBillNo)){
            sqlbuf.append(" and c.SOURCEBILLNO = '"+sourceBillNo+"'");
        }
        if(!Check.Null(applyStatus)){
            sqlbuf.append(" and a.ISAPPLY = '"+applyStatus+"'");
        }
        if(!Check.Null(status)){
            sqlbuf.append(" and a.status = '"+status+"'");
        }
        if (!Check.Null(applyDateEnd) && !Check.Null(applyDateBegin)) {
            sqlbuf.append(" AND (trunc(a.APPLYDATE) >= to_date('" + applyDateBegin + "','YYYY-MM-DD') AND trunc(a.APPLYDATE) <= to_date('" + applyDateEnd + "','YYYY-MM-DD'))");
        }
        if (!Check.Null(invoiceDateBegin) && !Check.Null(invoiceDateEnd)) {
            sqlbuf.append(" AND ( trunc(a.INVOICEDATE) >= to_date('" + invoiceDateBegin + "','YYYY-MM-DD') AND trunc(a.INVOICEDATE) <= to_date('" + invoiceDateEnd + "','YYYY-MM-DD'))");
        }
        sqlbuf.append(" order by a.CREATETIME desc) WHERE rn > " + startRow + " AND rn <= " + (startRow + pageSize) + " ");
        sql = sqlbuf.toString();
        return sql;
    }

}
