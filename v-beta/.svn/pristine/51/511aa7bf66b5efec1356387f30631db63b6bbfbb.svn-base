package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_PriceAdjustQueryReq;
import com.dsc.spos.json.cust.res.DCP_PriceAdjustQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.DateFormatUtils;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_PriceAdjustQuery extends SPosBasicService<DCP_PriceAdjustQueryReq, DCP_PriceAdjustQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_PriceAdjustQueryReq req) throws Exception {
//        String[]bType = new String[]{"1","2"};
//        if (!ArrayUtils.contains(bType,req.getRequest().getBType())){
//            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400,"单据类型 1-采购价 2-零售价");
//        }
        return false;
    }

    @Override
    protected TypeToken<DCP_PriceAdjustQueryReq> getRequestType() {
        return new TypeToken<DCP_PriceAdjustQueryReq>() {

        };
    }


    @Override
    protected DCP_PriceAdjustQueryRes getResponseType() {
        return new DCP_PriceAdjustQueryRes();
    }

    @Override
    protected DCP_PriceAdjustQueryRes processJson(DCP_PriceAdjustQueryReq req) throws Exception {
        DCP_PriceAdjustQueryRes res = getResponseType();
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
                    DCP_PriceAdjustQueryRes.Data data = res.new Data();

                    data.setBillNo(oneData.get("BILLNO").toString());
                    data.setBType(oneData.get("BTYPE").toString());
                    data.setBDate(DateFormatUtils.getDate(oneData.get("BDATE").toString()));
                    data.setOrgNo(oneData.get("ORGANIZATIONNO").toString());
                    data.setOrgName(oneData.get("ORGNAME").toString());
                    data.setEmployeeID(oneData.get("EMPLOYEEID").toString());
                    data.setEmployeeName(oneData.get("EMPLOYEENAME").toString());
                    data.setDepartID(oneData.get("DEPARTID").toString());
                    data.setDepartName(oneData.get("DEPARTNAME").toString());
                    data.setSupplier(oneData.get("SUPPLIER").toString());
                    data.setSupplierName(oneData.get("SUPPLIERNAME").toString());
                    data.setIsUpdate_DefpurPrice(oneData.get("UPDATE_DEFPURPRICE").toString());
                    data.setTotcQty(oneData.get("TOTCQTY").toString());
                    data.setMemo(oneData.get("MEMO").toString());
                    data.setStatus(oneData.get("STATUS").toString());
                    data.setEffectiveDate(DateFormatUtils.getDate(oneData.get("EFFECTIVEDATE").toString()));

                    data.setTemplateNo(StringUtils.toString(oneData.get("TEMPLATENO"), ""));
                    data.setTemplateName(StringUtils.toString(oneData.get("TEMPLATENAME"), ""));

                    data.setPurTemplateNo(StringUtils.toString(oneData.get("TEMPLATENO"), ""));
                    data.setPurTemplateName(StringUtils.toString(oneData.get("TEMPLATENAME"), ""));

                    data.setOwnerID(StringUtils.toString(oneData.get("OWNOPID"), ""));
                    data.setOwnerName(StringUtils.toString(oneData.get("OWNERNAME"), ""));
                    data.setOwnDeptID(StringUtils.toString(oneData.get("OWNDEPTID"), ""));
                    data.setOwnDeptName(StringUtils.toString(oneData.get("OWNDEPTNAME"), ""));
                    data.setCreatorID(StringUtils.toString(oneData.get("CREATEOPID"), ""));
                    data.setCreatorName(StringUtils.toString(oneData.get("CREATOPNAME"), ""));
                    data.setCreatorDeptID(StringUtils.toString(oneData.get("CREATEDEPTID"), ""));
                    data.setCreatorDeptName(StringUtils.toString(oneData.get("CREATEDEPTNAME"), ""));
                    data.setCreate_datetime(StringUtils.toString(oneData.get("CREATETIME"), ""));
                    data.setLastmodifyID(StringUtils.toString(oneData.get("LASTMODIOPID"), ""));
                    data.setLastmodifyName(StringUtils.toString(oneData.get("LASTMODIOPNAME"), ""));
                    data.setLastmodify_datetime(StringUtils.toString(oneData.get("LASTMODITIME"), ""));
                    data.setConfirmBy(StringUtils.toString(oneData.get("CONFIRMBY"), ""));
                    data.setConfirmByName(StringUtils.toString(oneData.get("CONFIRMBYNAME"), ""));
                    data.setConfirm_datetime(StringUtils.toString(oneData.get("CONFIRMTIME"), ""));
                    data.setCancelBy(StringUtils.toString(oneData.get("CANCELBY"), ""));
                    data.setCancelByName(StringUtils.toString(oneData.get("CANCELBYNAME"), ""));
                    data.setCancel_datetime(StringUtils.toString(oneData.get("CANCELTIME"), ""));
                    data.setIsUpdateSdPrice(StringUtils.toString(oneData.get("isUpdateSdPrice"), ""));
                    data.setInvalidDate(StringUtils.toString(oneData.get("INVALIDDATE"), ""));

                    res.getDatas().add(data);
                }
            }

            res.setSuccess(true);
            res.setServiceStatus(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E200.getCodeType());
        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400.getCodeType());
            res.setServiceDescription(e.getMessage());
        }
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_PriceAdjustQueryReq req) throws Exception {
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();
        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        StringBuilder querySql = new StringBuilder();
        querySql.append(
                " SELECT * FROM (" +
                        " SELECT COUNT(1) over() NUM, row_number() over(ORDER BY a.BDATE DESC, a.BILLNO DESC) rn, " +
                        " a.*,b.TOTCQTY" +
                        " FROM (" +
                        "   SELECT a.EID,a.ORGANIZATIONNO,a.BTYPE,a.BILLNO,a.BDATE, " +
                        "   a.SUPPLIER,a.TEMPLATENO,a.EFFECTIVEDATE,a.UPDATE_DEFPURPRICE, " +
                        "   a.EMPLOYEEID,a.STATUS,a.MEMO,a.DEPARTID,a.OWNOPID,a.OWNDEPTID, " +
                        "   a.CREATEOPID,a.CREATEDEPTID,a.CREATETIME,a.LASTMODIOPID,a.LASTMODITIME, " +
                        "   a.CONFIRMBY,a.CONFIRMTIME,a.CANCELBY,a.CANCELTIME,b.SNAME as ORGNAME, " +
                        "   em2.NAME EMPLOYEENAME,i.DEPARTNAME DEPARTNAME,f.SNAME SUPPLIERNAME,g.PTEMPLATE_NAME, " +
                        "   h.NAME OWNERNAME,j.DEPARTNAME OWNERDEPTNAME,d.NAME CREATEOPNAME, " +
                        "   k.DEPARTNAME CREATEDEPTNAME,e.NAME LASTMODIOPNAME,c.NAME CONFIRMBYNAME, " +
                        "   CASE WHEN a.BTYPE='1' THEN t1.NAME ELSE t2.TEMPLATENAME END TEMPLATENAME," +
                        "   l.NAME CANCELBYNAME,a.INVALIDDATE,a.TOTCNT " +
                        "   FROM DCP_PRICEADJUST a " +
                        "   LEFT JOIN DCP_ORG b on a.ORGANIZATIONNO=b.ORGANIZATIONNO  " +
                        "   LEFT JOIN DCP_EMPLOYEE c ON c.EID=a.EID and c.EMPLOYEENO=a.CONFIRMBY " +
                        "   LEFT JOIN DCP_EMPLOYEE d ON d.EID=a.EID and d.EMPLOYEENO=a.CREATEOPID " +
                        "   LEFT JOIN DCP_EMPLOYEE e ON e.EID=a.EID and e.EMPLOYEENO=a.LASTMODIOPID " +
                        "   LEFT JOIN DCP_EMPLOYEE l ON l.EID=a.EID and l.EMPLOYEENO=a.CANCELBY " +
                        "   LEFT JOIN DCP_EMPLOYEE em2 ON em2.EID=a.EID and em2.EMPLOYEENO=a.EMPLOYEEID " +
                        "   LEFT JOIN DCP_BIZPARTNER f on f.EID=a.EID AND f.BIZPARTNERNO=a.SUPPLIER " +
                        "   LEFT JOIN DCP_PTEMPLATE g ON g.EID=a.EID AND g.PTEMPLATENO=a.TEMPLATENO " +
                        "   LEFT JOIN DCP_EMPLOYEE h ON h.EID=a.EID and h.EMPLOYEENO=a.OWNOPID " +
                        "   LEFT JOIN DCP_PURCHASETEMPLATE_LANG t1 ON a.EID=t1.EID and a.TEMPLATENO=t1.PURTEMPLATENO and a.BTYPE='1'  AND t1.LANG_TYPE='" + req.getLangType() + "'" +
                        "   LEFT JOIN DCP_SALEPRICETEMPLATE_LANG t2 ON a.EID=t2.EID and a.TEMPLATENO=t2.TEMPLATEID and a.BTYPE='2'  AND t2.LANG_TYPE='" + req.getLangType() + "'" +
                        "   LEFT JOIN DCP_DEPARTMENT_LANG i on i.EID=a.EID AND i.DEPARTNO=a.DEPARTID AND i.LANG_TYPE='" + req.getLangType() + "'" +
                        "   LEFT JOIN DCP_DEPARTMENT_LANG j on j.EID=a.EID AND j.DEPARTNO=a.OWNDEPTID AND j.LANG_TYPE='" + req.getLangType() + "'" +
                        "   LEFT JOIN DCP_DEPARTMENT_LANG k on k.EID=a.EID AND k.DEPARTNO=a.CREATEDEPTID AND k.LANG_TYPE='" + req.getLangType() + "'");

        querySql.append(" WHERE 1=1 ");

        querySql.append(" AND a.ORGANIZATIONNO='").append(req.getOrganizationNO()).append("'");

        if (StringUtils.isNotEmpty(req.getRequest().getBillNo())) {
            querySql.append(" AND a.BILLNO='" + req.getRequest().getBillNo() + "'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getStatus())) {
            querySql.append(" AND a.STATUS='" + req.getRequest().getStatus() + "'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getBType())) {
            querySql.append(" AND a.BTYPE='" + req.getRequest().getBType() + "'");
        }

        if (!Check.Null(req.getRequest().getDateType())) {
//            bdate：单据日期,receiptDate
            if (StringUtils.equals("bdate", req.getRequest().getDateType().toLowerCase())) {
                if (!Check.Null(req.getRequest().getBeginDate())) {
                    querySql.append("and TO_CHAR(a.bdate,'YYYYMMDD')>='" + DateFormatUtils.getPlainDate(req.getRequest().getBeginDate()) + "' ");

                }
                if (!Check.Null(req.getRequest().getEndDate())) {
                    querySql.append("and TO_CHAR(a.bdate,'YYYYMMDD')<='" + DateFormatUtils.getPlainDate(req.getRequest().getEndDate()) + "' ");
                }
            } else if (StringUtils.equals("effectiveDate", req.getRequest().getDateType().toLowerCase())) {
                if (!Check.Null(req.getRequest().getBeginDate())) {
                    querySql.append("and TO_CHAR(a.EFFECTIVEDATE,'YYYYMMDD')>='" + DateFormatUtils.getPlainDate(req.getRequest().getBeginDate()) + "' ");
                }
                if (!Check.Null(req.getRequest().getEndDate())) {
                    querySql.append("and TO_CHAR(a.EFFECTIVEDATE,'YYYYMMDD')<='" + DateFormatUtils.getPlainDate(req.getRequest().getEndDate()) + "' ");
                }
            }
        }

        if (StringUtils.isNotEmpty(req.getRequest().getKeyTxt())) {
            String keyTxt = req.getRequest().getKeyTxt();
            querySql.append(" AND ( a.SUPPLIER like '%%")
                    .append(keyTxt)
                    .append("%%' OR a.BILLNO like '%%")
                    .append(keyTxt)
                    .append("%%' OR f.SNAME like '%%")
                    .append(keyTxt).append("%%' )");
        }

        querySql.append(
                "  ORDER BY BDATE DESC, BILLNO DESC ) a" +
                        " LEFT JOIN ( SELECT EID,BILLNO,COUNT(1) TOTCQTY FROM DCP_PriceAdjust_detail GROUP BY EID,BILLNO ) b ON a.EID=b.EID AND a.BILLNO=b.BILLNO " +
                        " WHERE  a.EID='" + req.geteId() + "' ");

        querySql.append(" ) temp ");

        querySql.append(" WHERE rn> ")
                .append(startRow)
                .append(" and rn<= ")
                .append(startRow + pageSize)
                .append(" ORDER BY BDATE DESC, BILLNO DESC ");

        return querySql.toString();
    }
}
