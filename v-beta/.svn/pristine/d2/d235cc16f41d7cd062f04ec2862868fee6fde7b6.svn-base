package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_CustomerPriceAdjustDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_CustomerPriceAdjustDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.DateFormatUtils;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.StringUtils;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_CustomerPriceAdjustDetailQuery extends SPosBasicService<DCP_CustomerPriceAdjustDetailQueryReq, DCP_CustomerPriceAdjustDetailQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_CustomerPriceAdjustDetailQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CustomerPriceAdjustDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_CustomerPriceAdjustDetailQueryReq>() {
        };
    }

    @Override
    protected DCP_CustomerPriceAdjustDetailQueryRes getResponseType() {
        return new DCP_CustomerPriceAdjustDetailQueryRes();
    }

    @Override
    protected DCP_CustomerPriceAdjustDetailQueryRes processJson(DCP_CustomerPriceAdjustDetailQueryReq req) throws Exception {
        DCP_CustomerPriceAdjustDetailQueryRes res = this.getResponseType();

        res.setDatas(new ArrayList<>());

        List<Map<String, Object>> getQData = this.doQueryData(getQuerySql(req), null);

        Map<String, Boolean> conditionValue = new HashMap<>();
        conditionValue.put("EID", true);
        conditionValue.put("BILLNO", true);

        List<Map<String, Object>> headData = MapDistinct.getMap(getQData, conditionValue);
        List<Map<String, Object>> cpData = this.doQueryData(getQueryCustomerSql(req), null);

        int totalRecords = 0;    //总笔数
        int totalPages = 0;        //总页数
        if (headData != null && !headData.isEmpty()) {
            String num = headData.get(0).get("NUM").toString();
            totalRecords = Integer.parseInt(num);
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            Map<String, Object> conditionDetail = new HashMap<>();
            Map<String, Object> conditionCustomer = new HashMap<>();
            Map<String, Object> conditionRange = new HashMap<>();

            res.setDatas(new ArrayList<>());
            for (Map<String, Object> oneData : headData) {

                DCP_CustomerPriceAdjustDetailQueryRes.Datas oneEntity = res.new Datas();

                oneEntity.setCreateDeptId(StringUtils.toString(oneData.get("CREATEDEPTID"), ""));
                oneEntity.setEndDate(StringUtils.toString(oneData.get("ENDDATE"), ""));
                oneEntity.setCreateOpName(StringUtils.toString(oneData.get("CREATEOPNAME"), ""));
                oneEntity.setMemo(StringUtils.toString(oneData.get("MEMO"), ""));
                oneEntity.setOwnOpName(StringUtils.toString(oneData.get("OWNOPNAME"), ""));
                oneEntity.setTotCqty(StringUtils.toString(oneData.get("TOTCQTY"), ""));
                oneEntity.setModifyTime(StringUtils.toString(oneData.get("MODIFYTIME"), ""));
                oneEntity.setOwnDeptName(StringUtils.toString(oneData.get("OWNDEPTNAME"), ""));
                oneEntity.setDepartId(StringUtils.toString(oneData.get("DEPARTID"), ""));
                oneEntity.setCancelBy(StringUtils.toString(oneData.get("CANCELBY"), ""));
                oneEntity.setCancelByName(StringUtils.toString(oneData.get("CANCELBYNAME"), ""));
                oneEntity.setOwnOpId(StringUtils.toString(oneData.get("OWNOPID"), ""));
                oneEntity.setModifyByName(StringUtils.toString(oneData.get("MODIFYBYNAME"), ""));
                oneEntity.setBillNo(StringUtils.toString(oneData.get("BILLNO"), ""));
                oneEntity.setDepartName(StringUtils.toString(oneData.get("DEPARTNAME"), ""));
                oneEntity.setEmployeeName(StringUtils.toString(oneData.get("EMPLOYEENAME"), ""));
                oneEntity.setModifyBy(StringUtils.toString(oneData.get("MODIFYBY"), ""));
                oneEntity.setCreateOpId(StringUtils.toString(oneData.get("CREATEOPID"), ""));
                oneEntity.setTotCustQty(StringUtils.toString(oneData.get("CustQty"), "0"));
                oneEntity.setConfirmByName(StringUtils.toString(oneData.get("CONFIRMBYNAME"), ""));
                oneEntity.setConfirmBy(StringUtils.toString(oneData.get("CONFIRMBY"), ""));
                oneEntity.setEmployeeId(StringUtils.toString(oneData.get("EMPLOYEEID"), ""));
                oneEntity.setConfirmTime(StringUtils.toString(oneData.get("CONFIRMTIME"), ""));
                oneEntity.setBeginDate(StringUtils.toString(oneData.get("BEGINDATE"), ""));
                oneEntity.setOwnDeptId(StringUtils.toString(oneData.get("OWNDEPTID"), ""));
                oneEntity.setBDate(StringUtils.toString(oneData.get("BDATE"), ""));
                oneEntity.setCreateTime(StringUtils.toString(oneData.get("CREATETIME"), ""));
                oneEntity.setCancelTime(StringUtils.toString(oneData.get("CANCELTIME"), ""));
                oneEntity.setStatus(StringUtils.toString(oneData.get("STATUS"), ""));
                oneEntity.setCreateDeptName(StringUtils.toString(oneData.get("CREATEDEPTNAME"),""));

                oneEntity.setDetail(new ArrayList<>());
                oneEntity.setCustomerList(new ArrayList<>());
                oneEntity.setCustRange(new ArrayList<>());

                conditionDetail.put("EID", oneData.get("EID"));
                conditionDetail.put("BILLNO", oneData.get("BILLNO"));

                List<Map<String, Object>> detailData = MapDistinct.getWhereMap(getQData, conditionDetail, true);

                for (Map<String, Object> detail : detailData) {
                    DCP_CustomerPriceAdjustDetailQueryRes.Detail oneDetail = res.new Detail();

                    oneDetail.setItem(StringUtils.toString(detail.get("ITEM"), ""));
                    oneDetail.setUnit(StringUtils.toString(detail.get("UNIT"), ""));
                    oneDetail.setUnitName(StringUtils.toString(detail.get("UNAME"), ""));
                    oneDetail.setPrice(StringUtils.toString(detail.get("PRICE"), ""));
                    oneDetail.setPluNo(StringUtils.toString(detail.get("PLUNO"), ""));
                    oneDetail.setPluBarcode(StringUtils.toString(detail.get("PLUBARCODE"), ""));
                    oneDetail.setCategory(StringUtils.toString(detail.get("CATEGORY"), ""));
                    oneDetail.setCategoryName(StringUtils.toString(detail.get("CATEGORY_NAME"), ""));
                    oneDetail.setRetailPrice(StringUtils.toString(detail.get("RETAILPRICE"), "0"));
                    oneDetail.setSpec(StringUtils.toString(detail.get("SPEC"), ""));
                    oneDetail.setPluName(StringUtils.toString(detail.get("PLUNAME"), ""));
                    oneDetail.setDiscRate(StringUtils.toString(detail.get("DISCRATE"), ""));


                    oneEntity.getDetail().add(oneDetail);
                }

                conditionCustomer = Maps.newHashMap();
                conditionCustomer.put("EID", oneData.get("EID"));
                conditionCustomer.put("BILLNO", oneData.get("BILLNO"));

                List<Map<String, Object>> customerData = MapDistinct.getWhereMap(cpData, conditionCustomer, true);


                for (Map<String, Object> customer : customerData) {
                    DCP_CustomerPriceAdjustDetailQueryRes.CustomerList oneCustomer = res.new CustomerList();

                    oneCustomer.setBeginDate(StringUtils.toString(customer.get("BEGINDATE"), ""));
                    oneCustomer.setFName(StringUtils.toString(customer.get("FNAME"), ""));
                    oneCustomer.setEndDate(StringUtils.toString(customer.get("ENDDATE"), ""));
                    oneCustomer.setSName(StringUtils.toString(customer.get("SNAME"), ""));
                    oneCustomer.setCustomerNo(StringUtils.toString(customer.get("CUSTOMERNO"), ""));
                    oneCustomer.setStatus(StringUtils.toString(customer.get("STATUS"), ""));

                    oneEntity.getCustomerList().add(oneCustomer);
                }


                List<Map<String, Object>> rangeData = doQueryData(getQueryCustomerRange(req), null);

                for (Map<String, Object> range : rangeData) {
                    DCP_CustomerPriceAdjustDetailQueryRes.CustRange oneRange = res.new CustRange();

                    Map<String, Object> condition = new HashMap<>();
                    condition.put("EID", range.get("EID"));
                    condition.put("BILLNO", range.get("BILLNO"));
                    condition.put("ITEM", range.get("ITEM"));

                    List<Map<String, Object>> countData = MapDistinct.getWhereMap(customerData, condition, true);

                    oneRange.setItem(StringUtils.toString(range.get("ITEM"), ""));
                    oneRange.setCustomerType(StringUtils.toString(range.get("CUSTOMERTYPE"), ""));
                    oneRange.setCustQty(StringUtils.toString(countData.size(), "0"));
                    oneRange.setCustomerNo(StringUtils.toString(range.get("CUSTOMERNO"), ""));
                    oneRange.setCustomerName(StringUtils.toString(range.get("CUSTOMERNAME"), ""));

                    oneEntity.getCustRange().add(oneRange);
                }

                res.getDatas().add(oneEntity);

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

    private String getQueryCustomerRange(DCP_CustomerPriceAdjustDetailQueryReq req) throws Exception {
        StringBuilder builder = new StringBuilder();

        builder.append(" SELECT a.EID,a.ORGANIZATIONNO,a.BILLNO,a.ITEM,a.CUSTOMERTYPE,a.CUSTOMERNO ")
                .append(" ,CASE WHEN a.CUSTOMERTYPE='1' THEN gp.CUSTGROUPNAME ELSE b.SNAME end  as CUSTOMERNAME  ")
                .append(" FROM DCP_CUSTPRICEADJUST_RANGE a ")
                .append(" LEFT JOIN DCP_CUSTGROUP gp on gp.eid = a.EID and gp.CUSTGROUPNO=a.CUSTOMERNO and a.CUSTOMERTYPE='1'  ")
                .append(" LEFT JOIN DCP_BIZPARTNER b on a.EID=b.EID and a.CUSTOMERNO=b.BIZPARTNERNO and a.CUSTOMERTYPE='2' ")
                .append(" WHERE a.EID='").append(req.geteId()).append("'");

        if (StringUtils.isNotEmpty(req.getRequest().getBillNo())) {
            builder.append(" AND a.BILLNO='").append(req.getRequest().getBillNo()).append("'");
        }

        return builder.toString();
    }


    protected String getQueryCustomerSql(DCP_CustomerPriceAdjustDetailQueryReq req) throws Exception {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT a.*,b.SNAME,b.FNAME,b.STATUS,b.BEGINDATE,b.ENDDATE FROM ( ")
                .append("SELECT a.EID,a.BILLNO,a.ITEM, " +
                        "       a.CUSTOMERTYPE, " +
                        "       CASE WHEN a.CUSTOMERTYPE=1 and b.ATTRTYPE=1 then c.ID " +
                        "           WHEN a.CUSTOMERTYPE=1 and b.ATTRTYPE=2 then b.ATTRID " +
                        "           else a.CUSTOMERNO end as CUSTOMERNO " +
                        " FROM DCP_CUSTPRICEADJUST_RANGE a " +
                        " LEFT JOIN DCP_CUSTGROUP_DETAIL b on a.EID=b.EID and a.CUSTOMERTYPE=1 and a.CUSTOMERNO=b.CUSTGROUPNO " +
                        " LEFT JOIN DCP_TAGTYPE_DETAIL c ON b.EID=c.EID and b.ATTRID=c.TAGNO and b.ATTRTYPE=1 and c.TAGGROUPTYPE='CUST' ")
                .append(" WHERE a.EID='").append(req.geteId()).append("'");

        if (StringUtils.isNotEmpty(req.getRequest().getBillNo())) {
            builder.append(" AND a.BILLNO='").append(req.getRequest().getBillNo()).append("'");
        }
        builder.append(" ) a ")
                .append(" LEFT JOIN DCP_BIZPARTNER b on a.EID=b.EID and a.CUSTOMERNO=b.BIZPARTNERNO ")
                .append(" WHERE b.STATUS=100 and TO_CHAR(b.ENDDATE,'yyyy-mm-dd')>='").append(DateFormatUtils.getNowDate()).append("'");
        return builder.toString();
    }

    @Override
    protected String getQuerySql(DCP_CustomerPriceAdjustDetailQueryReq req) throws Exception {
        StringBuilder builder = new StringBuilder();

        builder.append(" SELECT * FROM( ")
                .append("  select count(*) over() num, row_number() over (order by a.BILLNO) rn, ")
                .append("  a.*,ee0.NAME as CREATEOPNAME,ee1.NAME as OWNOPNAME,ee2.NAME as MODIFYBYNAME, ")
                .append("  ee3.NAME as CONFIRMBYNAME,ee4.NAME as CANCELBYNAME,ee5.NAME as EMPLOYEENAME,dp0.DEPARTNAME as CREATEDEPTNAME, ")
                .append("  dp1.DEPARTNAME as OWNDEPTNAME,b.ITEM,b.PLUBARCODE,b.PLUNO,b.CATEGORY,b.UNIT, ")
                .append("  b.PRICE,b.RETAILPRICE,b.DISCRATE,c.SPEC,d.PLU_NAME PLUNAME,ul1.UNAME ")
                .append( " ,cl1.CATEGORY_NAME,dp2.DEPARTNAME ")
                .append(" FROM DCP_CUSTPRICEADJUST a ")
                .append(" left join DCP_CUSTPRICEADJUST_DETAIL b on a.eid=b.eid and a.BILLNO=b.BILLNO ")
//                .append(" left join DCP_CUSTPRICEADJUST_RANGE c on a.eid=c.eid and a.billno=c.billno ")
//                .append(" left join DCP_CUSTOMER_PRICE d on c.EID=d.EID and c.CUSTOMERNO=d.CUSTOMERNO and c.CUSTOMERTYPE=d.CUSTOMERTYPE  ")
                .append(" LEFT JOIN DCP_GOODS c ON c.eid=b.eid and c.PLUNO=b.PLUNO ")
                .append(" LEFT JOIN DCP_GOODS_LANG d ON b.EID = d.EID AND b.PLUNO = d.PLUNO AND d.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_UNIT_LANG ul1 on ul1.eid=b.eid and ul1.UNIT=b.UNIT and ul1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_EMPLOYEE ee0 ON ee0.EID=a.EID and ee0.EMPLOYEENO=a.CREATEOPID")
                .append(" LEFT JOIN DCP_EMPLOYEE ee1 ON ee1.EID=a.EID and ee1.EMPLOYEENO=a.OWNOPID")
                .append(" LEFT JOIN DCP_EMPLOYEE ee2 ON ee2.EID=a.EID and ee2.EMPLOYEENO=a.MODIFYBY")
                .append(" LEFT JOIN DCP_EMPLOYEE ee3 ON ee3.EID=a.EID and ee3.EMPLOYEENO=a.CONFIRMBY")
                .append(" LEFT JOIN DCP_EMPLOYEE ee4 ON ee4.EID=a.EID and ee4.EMPLOYEENO=a.CANCELBY")
                .append(" LEFT JOIN DCP_EMPLOYEE ee5 ON ee5.EID=a.EID and ee5.EMPLOYEENO=a.EMPLOYEEID")
                .append(" LEFT JOIN DCP_DEPARTMENT_LANG dp0 on dp0.EID=a.EID AND dp0.DEPARTNO=a.CREATEDEPTID AND dp0.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_DEPARTMENT_LANG dp1 on dp1.EID=a.EID AND dp1.DEPARTNO=a.OWNDEPTID AND dp1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_DEPARTMENT_LANG dp2 on dp2.EID=a.EID AND dp2.DEPARTNO=a.DEPARTID AND dp2.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_GOODS_UNIT_LANG gul on gul.EID=b.EID and gul.PLUNO=b.PLUNO and gul.OUNIT=b.UNIT and gul.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_CATEGORY_LANG cl1 on cl1.eid =b.eid and cl1.CATEGORY=b.CATEGORY and cl1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" WHERE a.EID='").append(req.geteId()).append("'")
        ;
        if (StringUtils.isNotEmpty(req.getRequest().getBillNo())) {
            builder.append(" AND a.BILLNO='").append(req.getRequest().getBillNo()).append("'");
        }

        builder.append(" ) a ");
        return builder.toString();
    }
}
