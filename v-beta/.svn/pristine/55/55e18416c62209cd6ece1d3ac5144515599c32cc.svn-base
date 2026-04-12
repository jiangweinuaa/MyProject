package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_CustomerPOrderQueryReq;
import com.dsc.spos.json.cust.res.DCP_CustomerPOrderQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_CustomerPOrderQuery extends SPosBasicService<DCP_CustomerPOrderQueryReq, DCP_CustomerPOrderQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_CustomerPOrderQueryReq req) throws Exception {
        boolean isFail = false;
        return isFail;
    }

    @Override
    protected TypeToken<DCP_CustomerPOrderQueryReq> getRequestType() {
        return new TypeToken<DCP_CustomerPOrderQueryReq>() {
        };
    }

    @Override
    protected DCP_CustomerPOrderQueryRes getResponseType() {
        return new DCP_CustomerPOrderQueryRes();
    }

    @Override
    protected DCP_CustomerPOrderQueryRes processJson(DCP_CustomerPOrderQueryReq req) throws Exception {
        //查询资料
        DCP_CustomerPOrderQueryRes res = this.getResponse();
        try {
            List<Map<String, Object>> getQData = this.doQueryData(getQuerySql(req), null);
            int totalRecords = 0;    //总笔数
            int totalPages = 0;        //总页数
            if (getQData != null && !getQData.isEmpty()) {
                String num = getQData.get(0).get("NUM").toString();
                totalRecords = Integer.parseInt(num);
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

                res.setDatas(res.new Datas());
                res.getDatas().setDataList(new ArrayList<>());

                for (Map<String, Object> oneData : getQData) {
                    DCP_CustomerPOrderQueryRes.DataList oneLv1 = res.new DataList();
                    oneLv1.setTotDiscAmt(StringUtils.toString(oneData.get("TOTDISCAMT"), ""));
                    oneLv1.setMemo(StringUtils.toString(oneData.get("MEMO"), ""));
                    oneLv1.setTotTaxAmt(StringUtils.toString(oneData.get("TOTTAXAMT"), ""));
                    oneLv1.setDeliverWarehouse(StringUtils.toString(oneData.get("DELIVERWAREHOUSE"), ""));
                    oneLv1.setPayType(StringUtils.toString(oneData.get("PAYTYPE"), ""));
                    oneLv1.setBillDateNo(StringUtils.toString(oneData.get("BILLDATENO"), ""));
                    oneLv1.setOrgNo(StringUtils.toString(oneData.get("ORGANIZATIONNO"), ""));
                    oneLv1.setContact(StringUtils.toString(oneData.get("CONTACT"), ""));
                    oneLv1.setCloseByName(StringUtils.toString(oneData.get("CLOSEBYNAME"), ""));
                    oneLv1.setDepartName(StringUtils.toString(oneData.get("DEPARTNAME"), ""));
                    oneLv1.setLastModiOpName(StringUtils.toString(oneData.get("LASTMODIOPNAME"), ""));
                    oneLv1.setEmployeeName(StringUtils.toString(oneData.get("EMPLOYEENAME"), ""));
                    oneLv1.setOrgNo(StringUtils.toString(oneData.get("ORGANIZATIONNAME"), ""));
                    oneLv1.setPayOrgName(StringUtils.toString(oneData.get("PAYORGNAME"), ""));
                    oneLv1.setRDate(StringUtils.toString(oneData.get("RDATE"), ""));
                    oneLv1.setTelephone(StringUtils.toString(oneData.get("TELEPHONE"), ""));
                    oneLv1.setConfirmTime(StringUtils.toString(oneData.get("CONFIRMTIME"), ""));
                    oneLv1.setInvoiceCode(StringUtils.toString(oneData.get("INVOICECODE"), ""));
                    oneLv1.setCustomerName(StringUtils.toString(oneData.get("CUSTOMERNAME"), ""));
                    oneLv1.setDiscRate(StringUtils.toString(oneData.get("DISCRATE"), ""));
                    oneLv1.setDeliverOrgName(StringUtils.toString(oneData.get("DELIVERORGNAME"), ""));
                    oneLv1.setDeliverWarehouseName(StringUtils.toString(oneData.get("DELIVERWAREHOUSENAME"), ""));
                    oneLv1.setPayOrgNo(StringUtils.toString(oneData.get("PAYORGNO"), ""));
                    oneLv1.setBDate(StringUtils.toString(oneData.get("BDATE"), ""));
                    oneLv1.setConfirmOpId(StringUtils.toString(oneData.get("CONFIRMOPID"), ""));
                    oneLv1.setStatus(StringUtils.toString(oneData.get("STATUS"), ""));
                    oneLv1.setDeliverOrgNo(StringUtils.toString(oneData.get("DELIVERORGNO"), ""));
                    oneLv1.setInvoiceName(StringUtils.toString(oneData.get("INVOICENAME"), ""));
                    oneLv1.setCreateOpName(StringUtils.toString(oneData.get("CREATEOPNAME"), ""));
                    oneLv1.setSalesManName(StringUtils.toString(oneData.get("SALESMANNAME"), ""));
                    oneLv1.setCloseBy(StringUtils.toString(oneData.get("CLOSEBY"), ""));
                    oneLv1.setPayDateName(StringUtils.toString(oneData.get("PAYDATENAME"), ""));
                    oneLv1.setSalesDepartName(StringUtils.toString(oneData.get("SALEDEPARTNAME"), ""));
                    oneLv1.setTotCqty(StringUtils.toString(oneData.get("TOT_CQTY"), ""));
                    oneLv1.setCurrencyName(StringUtils.toString(oneData.get("CURRENCYNAME"), ""));
                    oneLv1.setPOrderNo(StringUtils.toString(oneData.get("PORDERNO"), ""));
                    oneLv1.setCloseTime(StringUtils.toString(oneData.get("CLOSETIME"), ""));
                    oneLv1.setTotQty(StringUtils.toString(oneData.get("TOT_PQTY"), ""));
                    oneLv1.setDepartId(StringUtils.toString(oneData.get("DEPARTID"), ""));
                    oneLv1.setCancelBy(StringUtils.toString(oneData.get("CANCELBY"), ""));
                    oneLv1.setCancelByName(StringUtils.toString(oneData.get("CANCELBYNAME"), ""));
                    oneLv1.setCurrency(StringUtils.toString(oneData.get("CURRENCY"), ""));
                    oneLv1.setConfirmOpName(StringUtils.toString(oneData.get("CONFIRMOPNAME"), ""));
                    oneLv1.setBillDateName(StringUtils.toString(oneData.get("BILLDATENAME"), ""));
                    oneLv1.setLastModiOpId(StringUtils.toString(oneData.get("LASTMODIOPID"), ""));
                    oneLv1.setTemplateNo(StringUtils.toString(oneData.get("TEMPLATENO"), ""));
                    oneLv1.setCreateOpId(StringUtils.toString(oneData.get("CREATEOPID"), ""));
                    oneLv1.setAddress(StringUtils.toString(oneData.get("ADDRESS"), ""));
                    oneLv1.setSalesManNo(StringUtils.toString(oneData.get("SALESMANNO"), ""));
                    oneLv1.setEmployeeId(StringUtils.toString(oneData.get("EMPLOYEEID"), ""));
                    oneLv1.setTotAmt(StringUtils.toString(oneData.get("TOT_AMT"), ""));
                    oneLv1.setTotPrexTaxAmt(StringUtils.toString(oneData.get("TOTPRETAXAMT"), ""));
                    oneLv1.setLastModiTime(StringUtils.toString(oneData.get("LASTMODITIME"), ""));
                    oneLv1.setSalesDepartId(StringUtils.toString(oneData.get("SALEDEPARTID"), ""));
                    oneLv1.setTemplateName(StringUtils.toString(oneData.get("TEMPLATENAME"), ""));
                    oneLv1.setCreateTime(StringUtils.toString(oneData.get("CREATETIME"), ""));
                    oneLv1.setCancelTime(StringUtils.toString(oneData.get("CANCELTIME"), ""));
                    oneLv1.setPayDateNo(StringUtils.toString(oneData.get("PAYDATENO"), ""));
                    oneLv1.setCustomerNo(StringUtils.toString(oneData.get("CUSTOMERNO"), ""));
                    oneLv1.setPayer(StringUtils.toString(oneData.get("PAYER"), ""));
                    oneLv1.setPayerName(StringUtils.toString(oneData.get("PAYERNAME"), ""));
                    res.getDatas().getDataList().add(oneLv1);
                }
            }
            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);

            return res;
        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {


    }

    @Override
    protected String getQuerySql(DCP_CustomerPOrderQueryReq req) throws Exception {
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        StringBuilder builder = new StringBuilder();
        builder.append(" SELECT * FROM( ");
        builder.append(" select count(*) over() num, row_number() over (order by a.BDATE DESC,a.PORDERNO DESC) rn,  ")
                .append(" A.*,ee0.NAME EMPLOYEENAME,ee1.NAME CREATEOPNAMENEW,ee2.NAME LASTMODIOPNAMENEW, ")
                .append(" ee3.NAME CLOSEBYNAME,ee4.NAME CANCELBYNAME,dp0.DEPARTNAME,")
                .append(" dp1.DEPARTNAME SALEDEPARTNAME,dp0.DEPARTNAME CREATEDEPTNAME, ")
                .append(" cl0.NAME CURRENCYNAME,ol0.ORG_NAME ORGANIZATIONNAME,ol1.ORG_NAME PAYORGNAME, ")
                .append(" ol2.ORG_NAME DELIVERORGNAME,bl0.NAME BILLDATENAME,bl1.NAME PAYDATENAME,")
                .append(" il0.INVOICE_NAME INVOICENAME,wl0.WAREHOUSE_NAME DELIVERWAREHOUSENAME,f.sname as payername ")
                .append(" FROM DCP_CUSTOMERPORDER a ")
                .append(" LEFT JOIN DCP_EMPLOYEE ee0 ON ee0.EID=a.EID and ee0.EMPLOYEENO=a.EMPLOYEEID")
                .append(" LEFT JOIN DCP_EMPLOYEE ee1 ON ee1.EID=a.EID and ee1.EMPLOYEENO=a.CREATEOPID")
                .append(" LEFT JOIN DCP_EMPLOYEE ee2 ON ee2.EID=a.EID and ee2.EMPLOYEENO=a.LASTMODIOPID")
                .append(" LEFT JOIN DCP_EMPLOYEE ee3 ON ee3.EID=a.EID and ee3.EMPLOYEENO=a.CLOSEBY")
                .append(" LEFT JOIN DCP_EMPLOYEE ee4 ON ee2.EID=a.EID and ee4.EMPLOYEENO=a.CANCELBY")
                .append(" LEFT JOIN DCP_DEPARTMENT_LANG dp0 on dp0.EID=a.EID AND dp0.DEPARTNO=a.DEPARTID AND dp0.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_DEPARTMENT_LANG dp1 on dp1.EID=a.EID AND dp1.DEPARTNO=a.SALEDEPARTID AND dp1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_DEPARTMENT_LANG dp2 on dp2.EID=a.EID AND dp2.DEPARTNO=a.CREATEDEPTID AND dp1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_CURRENCY_LANG cl0 ON cl0.eid = a.eid AND cl0.CURRENCY = a.CURRENCY and nation='CN' AND cl0.lang_type='").append(req.getLangType()).append("'")
                .append(" LEFT join DCP_ORG_LANG ol0 on ol0.eid=a.eid and ol0.ORGANIZATIONNO=a.ORGANIZATIONNO and ol0.lang_type='").append(req.getLangType()).append("'")
                .append(" LEFT join DCP_ORG_LANG ol1 on ol1.eid=a.eid and ol1.ORGANIZATIONNO=a.PAYORGNO and ol1.lang_type='").append(req.getLangType()).append("'")
                .append(" LEFT join DCP_ORG_LANG ol2 on ol2.eid=a.eid and ol2.ORGANIZATIONNO=a.DELIVERORGNO and ol2.lang_type='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_BILLDATE_LANG bl0 ON bl0.eid = a.eid AND bl0.BILLDATENO = a.BILLDATENO AND bl0.lang_type ='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_BILLDATE_LANG bl1 ON bl1.eid = a.eid AND bl1.BILLDATENO = a.PAYDATENO AND bl1.lang_type ='").append(req.getLangType()).append("'")
                .append(" left join DCP_INVOICETYPE_LANG il0 on il0.eid =a.eid and il0.INVOICECODE=a.INVOICECODE and il0.lang_type='").append(req.getLangType()).append("'")
                .append(" left join dcp_warehouse_lang wl0 on wl0.eid=a.eid and wl0.warehouse=a.DELIVERWAREHOUSE and wl0.lang_type='").append(req.getLangType()).append("'")
                .append(" left join dcp_bizpartner f on f.eid=a.eid and f.bizpartnerno=a.payer ")
                .append(" WHERE a.EID='").append(req.geteId()).append("'")

        ;
        builder.append(" AND a.ORGANIZATIONNO='").append(req.getOrganizationNO()).append("'");

        if (StringUtils.isNotEmpty(req.getRequest().getStatus())) {
            builder.append(" AND a.STATUS='").append(req.getRequest().getStatus()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getShopId())) {
            builder.append(" AND a.SHOPNO='").append(req.getRequest().getShopId()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getDateType())) {
//            bdate：单据日期,rdate
            if (StringUtils.equals("bdate", req.getRequest().getDateType())) {
                if (!Check.Null(req.getRequest().getBeginDate())) {
                    builder.append("and a.bdate>='" + req.getRequest().getBeginDate() + "' ");
                }
                if (!Check.Null(req.getRequest().getEndDate())) {
                    builder.append("and a.bdate<='" + req.getRequest().getEndDate() + "' ");
                }
            } else if (StringUtils.equals("rdate", req.getRequest().getDateType())) {
                if (!Check.Null(req.getRequest().getBeginDate())) {
                    builder.append("and a.RDATE>='" + req.getRequest().getBeginDate() + "' ");
                }
                if (!Check.Null(req.getRequest().getEndDate())) {
                    builder.append("and a.RDATE<='" + req.getRequest().getEndDate() + "' ");
                }
            }

        }else {
            if (!Check.Null(req.getRequest().getBeginDate())) {
                builder.append("and a.bdate>='" + req.getRequest().getBeginDate() + "' ");
            }
            if (!Check.Null(req.getRequest().getEndDate())) {
                builder.append("and a.bdate<='" + req.getRequest().getEndDate() + "' ");
            }
        }



        if (StringUtils.isNotEmpty(req.getRequest().getKeyTxt())){
            String keyTxt = req.getRequest().getKeyTxt();
            builder.append(" AND ( a.PORDERNO like '%%")
                    .append(keyTxt)
                    .append("%%' OR a.CUSTOMERNO like '%%")
                    .append(keyTxt)
                    .append("%%' OR a.CUSTOMERNAME like '%%")
                    .append(keyTxt).append("%%' )");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getSearchScope()) && "1".equals(req.getRequest().getSearchScope()) ){

            builder.append(" and exists(" +
                    " SELECT 1 FROM DCP_CUSTOMERPORDER_DETAIL pd " +
                    " WHERE pd.QTY-NVL(pd.STOCKOUTNOQTY,0) >0  and a.eid=pd.eid and a.SHOPNO=pd.SHOPNO and a.PORDERNO=pd.PORDERNO  ) ");

        }

        builder.append(" order by a.BDATE DESC,a.PORDERNO DESC ");
        builder.append(" ) a ");

        builder.append(" WHERE rn> ")
                .append(startRow)
                .append(" and rn<= ")
                .append(startRow + pageSize)
                .append(" ORDER BY a.BDATE DESC,a.PORDERNO DESC ");

        return builder.toString();
    }

}
