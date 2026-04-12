package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_PriceAdjustPlansQueryReq;
import com.dsc.spos.json.cust.res.DCP_PriceAdjustPlansQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.DateFormatUtils;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_PriceAdjustPlansQuery extends SPosBasicService<DCP_PriceAdjustPlansQueryReq, DCP_PriceAdjustPlansQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_PriceAdjustPlansQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_PriceAdjustPlansQueryReq> getRequestType() {
        return new TypeToken<DCP_PriceAdjustPlansQueryReq>() {

        };
    }

    @Override
    protected DCP_PriceAdjustPlansQueryRes getResponseType() {
        return new DCP_PriceAdjustPlansQueryRes();
    }

    @Override
    protected DCP_PriceAdjustPlansQueryRes processJson(DCP_PriceAdjustPlansQueryReq req) throws Exception {
        DCP_PriceAdjustPlansQueryRes res = this.getResponseType();

        try {
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
                    DCP_PriceAdjustPlansQueryRes.Data data = res.new Data();

                    data.setBillNo(StringUtils.toString(oneData.get("BILLNO"), ""));
                    data.setBDate(DateFormatUtils.getDate(StringUtils.toString(oneData.get("BDATE"), "")));
                    data.setEffectiveDate(DateFormatUtils.getDate(StringUtils.toString(oneData.get("EFFECTIVEDATE"), "")));
                    data.setStatus(StringUtils.toString(oneData.get("STATUS"), ""));
                    data.setItem(StringUtils.toString(oneData.get("ITEM"), ""));
                    data.setPluNo(StringUtils.toString(oneData.get("PLUNO"), ""));
                    data.setPluName(StringUtils.toString(oneData.get("PLUNAME"), ""));
                    data.setSpec(StringUtils.toString(oneData.get("SPEC"), ""));
                    data.setPluBarcode(StringUtils.toString(oneData.get("MAINBARCODE"), ""));
                    data.setCategory(StringUtils.toString(oneData.get("CATEGORY"), ""));
                    data.setCategoryName(StringUtils.toString(oneData.get("CATEGORY_NAME"), ""));
                    data.setPriceUnit(StringUtils.toString(oneData.get("PRICEUNIT"), ""));
                    data.setPriceUnitName(StringUtils.toString(oneData.get("PRICEUNITNAME"), ""));
                    data.setPurpriceType(StringUtils.toString(oneData.get("PURPRICETYPE"), ""));
                    data.setPurPrice(StringUtils.toString(oneData.get("PURPRICE"), ""));
                    data.setIsUpdate_DefpurPrice(StringUtils.toString(oneData.get("UPDATE_DEFPRICE"), ""));
                    data.setBeginDate(DateFormatUtils.getDate(StringUtils.toString(oneData.get("BEGINDATE"), "")));
                    data.setEndDate(DateFormatUtils.getDate(StringUtils.toString(oneData.get("ENDDATE"), "")));
                    data.setEmployeeID(StringUtils.toString(oneData.get("EMPLOYEEID"), ""));
                    data.setEmployeeName(StringUtils.toString(oneData.get("EMPLOYEENAME"), ""));
                    data.setDepartID(StringUtils.toString(oneData.get("DEPARTID"), ""));
                    data.setDepartName(StringUtils.toString(oneData.get("DEPARTNAME"), ""));
                    data.setConfirmBy(StringUtils.toString(oneData.get("CONFIRMBY"), ""));
                    data.setConfirmByName(StringUtils.toString(oneData.get("CONFIRMBYNAME"), ""));
                    data.setConfirm_datetime(DateFormatUtils.getDateTime(StringUtils.toString(oneData.get("CONFIRMTIME"), "")));
                    data.setSupplier(StringUtils.toString(oneData.get("SUPPLIER"), ""));
                    data.setSupplierName(StringUtils.toString(oneData.get("SUPPLIERNAME"), ""));

                    res.getDatas().add(data);
                }

            }
            res.setSuccess(true);
        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceDescription(e.getMessage());
        }
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_PriceAdjustPlansQueryReq req) throws Exception {
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();
        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        StringBuilder querySql = new StringBuilder();

        querySql.append(
                " SELECT * FROM (" +
                        " SELECT COUNT(*) OVER() NUM ,row_number() over(ORDER BY STATUS, EFFECTIVEDATE,BILLNO) rn, " +
                        " temp.* " +
                        " FROM (" +
                        "   SELECT " +
                        "   a.EID,a.ADTYPE,a.TEMPLATENO,a.BILLNO,a.ITEM,a.PLUBARCODE,a.PLUNO,a.PRICEUNIT," +
                        "   a.PURPRICETYPE,a.PURPRICE,a.PRICE,a.UPDATE_DEFPURPRICE,b.ORGANIZATIONNO, " +
                        "   a.UPDATE_DEFPRICE,a.SUPPLIER,a.BEGINDATE,a.ENDDATE,a.EMPLOYEEID,a.DEPARTID, " +
                        "   a.CONFIRMBY,a.CONFIRMTIME,a.STATUS,a.LASTEXEDATE,a.MEMO,e.PLU_NAME PLUNAME, " +
                        "   c.NAME AS CONFIRMBYNAME,d.NAME AS EMPLOYEENAME,b.EFFECTIVEDATE," +
                        "   f.SPEC,f.MAINBARCODE,f.CATEGORY,g.CATEGORY_NAME,h.UNAME AS PRICEUNITNAME, " +
                        "   i.DEPARTNAME,k.SNAME SUPPLIERNAME,b.BDATE " +
                        "   FROM DCP_PRICEADJUSTPLAN a " +
                        "   LEFT JOIN DCP_PRICEADJUST b ON a.EID=b.EID AND a.BILLNO=b.BILLNO " +
                        "   LEFT JOIN DCP_EMPLOYEE c ON c.EID=a.EID and c.EMPLOYEENO=a.CONFIRMBY " +
                        "   LEFT JOIN DCP_EMPLOYEE d ON d.EID=a.EID and d.EMPLOYEENO=a.EMPLOYEEID " +
                        "   LEFT JOIN DCP_GOODS_LANG e ON a.EID=E.EID and a.PLUNO=e.PLUNO AND e.LANG_TYPE='" + req.getLangType() + "'" +
                        "   LEFT JOIN DCP_GOODS f ON a.EID=f.EID and a.PLUNO=f.PLUNO " +
                        "   LEFT JOIN DCP_CATEGORY_LANG g on g.EID=f.EID AND g.CATEGORY=f.CATEGORY AND g.LANG_TYPE='" + req.getLangType() + "'" +
                        "   LEFT JOIN DCP_UNIT_LANG h on h.EID=a.EID AND h.UNIT=a.PRICEUNIT AND h.LANG_TYPE='" + req.getLangType() + "'" +
                        "   LEFT JOIN DCP_BIZPARTNER k on k.EID=a.EID AND k.BIZPARTNERNO=a.SUPPLIER " +
                        "   LEFT JOIN DCP_DEPARTMENT_LANG i on i.EID=a.EID AND i.DEPARTNO=a.DEPARTID AND i.LANG_TYPE='" + req.getLangType() + "'" +
                        "   WHERE a.eID='" + req.geteId() + "' and b.ORGANIZATIONNO ='" + req.getOrganizationNO() + "'");

        if (StringUtils.isNotEmpty(req.getRequest().getAdType())) {
            querySql.append(" AND a.ADTYPE='").append(req.getRequest().getAdType()).append("'");
        }


        if (StringUtils.isNotEmpty(req.getRequest().getDateType())) {
            if ("effectiveDate".equals(req.getRequest().getDateType())) {
                querySql.append(" AND ( TO_CHAR(EFFECTIVEDATE,'YYYYMMDD') BETWEEN '")
                        .append(DateFormatUtils.getPlainDate(req.getRequest().getBeginDate()))
                        .append("' AND '")
                        .append(DateFormatUtils.getPlainDate(req.getRequest().getEndDate()))
                        .append("')");

            } else if ("bDate".equals(req.getRequest().getDateType())) {
                querySql.append(" AND (TO_CHAR(BDATE,'YYYYMMDD') BETWEEN '")
                        .append(DateFormatUtils.getPlainDate(req.getRequest().getBeginDate()))
                        .append("' AND '")
                        .append(DateFormatUtils.getPlainDate(req.getRequest().getEndDate()))
                        .append("')");
            }
        }

        if (StringUtils.isNotEmpty(req.getRequest().getKeytxt())) {
            querySql.append(" AND ( a.BILLNO like '%%")
                    .append(req.getRequest().getKeytxt())
                    .append("%%' or a.TEMPLATENO like '%%")
                    .append(req.getRequest().getKeytxt())
                    // .append("%%' or EFFECTIVEDATE like '%%")
                    // .append(req.getRequest().getKeytxt())
                    .append("%%' ) ");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getStatus()))
            querySql.append(" AND (a.status= ").append(req.getRequest().getStatus()).append(")");
        querySql.append(" ) temp  ");
        querySql.append(" )  a " + "  WHERE rn> ")
                .append(startRow)
                .append(" and rn<= ")
                .append(startRow + pageSize)
                .append(" ORDER BY rn ");

        return querySql.toString();
    }
}
