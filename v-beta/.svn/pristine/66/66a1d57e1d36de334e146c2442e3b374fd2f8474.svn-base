package com.dsc.spos.service.imp.json;


import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.json.cust.req.DCP_BizPartnerQueryReq;
import com.dsc.spos.json.cust.res.DCP_BizPartnerBillQueryRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.SUtil;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_BizPartnerBillQuery extends SPosBasicService<DCP_BizPartnerQueryReq, DCP_BizPartnerBillQueryRes> {

    Logger logger = LogManager.getLogger(SPosAdvanceService.class);
    public List<DataProcessBean> pData = new ArrayList<DataProcessBean>();

    @Override
    protected boolean isVerifyFail(DCP_BizPartnerQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected TypeToken<DCP_BizPartnerQueryReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_BizPartnerQueryReq>() {
        };
    }

    @Override
    protected DCP_BizPartnerBillQueryRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_BizPartnerBillQueryRes();
    }

    @Override
    protected DCP_BizPartnerBillQueryRes processJson(DCP_BizPartnerQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        DCP_BizPartnerBillQueryRes res = null;
        res = this.getResponse();
        String sql = null;
        try {

            sql = this.getCountSql(req);
            String[] condCountValues = {}; //查詢條件
            List<Map<String, Object>> getReasonCount = this.doQueryData(sql, condCountValues);
            int totalRecords;                                //总笔数
            int totalPages;                                    //总页数
            if (getReasonCount != null && getReasonCount.isEmpty() == false) {
                Map<String, Object> total = getReasonCount.get(0);
                String num = total.get("NUM").toString();
                totalRecords = Integer.parseInt(num);
                //算總頁數
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
            } else {
                totalRecords = 0;
                totalPages = 0;
            }

            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);

            sql = this.getQuerySql(req);

            String[] conditionValues1 = {}; //查詢條件

            List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, conditionValues1);
            sql = this.getOrgQuerySql(req);
            List<Map<String, Object>> getQLangDataDetail = this.doQueryData(sql, conditionValues1);
            sql = this.getBillQuerySql(req);
            List<Map<String, Object>> getBillDataDetail = this.doQueryData(sql, conditionValues1);
            List<Map<String, Object>> getQLangData = null;
            String eID = "";
            String billNo = "";
            String payType = "";
            if (getQDataDetail != null && getQDataDetail.isEmpty() == false) {
                res.setDatas(new ArrayList<DCP_BizPartnerBillQueryRes.DataDetail>());

                for (Map<String, Object> oneData : getQDataDetail) {
                    DCP_BizPartnerBillQueryRes.DataDetail oneLv1 = res.new DataDetail();
                    eID = oneData.get("EID").toString();
                    billNo = oneData.get("BIZPARTNERNO").toString();
                    payType = oneData.get("BIZTYPE").toString();
                    oneLv1.setBizPartnerNo(oneData.get("BIZPARTNERNO").toString());
                    oneLv1.setSName(oneData.get("SNAME").toString());

                    oneLv1.setFName(oneData.get("FNAME").toString());
                    oneLv1.setBizType(oneData.get("BIZTYPE").toString());
                    oneLv1.setCorpType(oneData.get("CORPTYPE").toString());
                    oneLv1.setRegisterNo(oneData.get("REGISTERNO").toString());
                    oneLv1.setLegalPerson(oneData.get("LEGALPERSON").toString());
                    oneLv1.setTaxPayerNo(oneData.get("TAXPAYER_NO").toString());
                    oneLv1.setMainCategory(oneData.get("MAINCATEGORY").toString());
                    oneLv1.setMainBrands(oneData.get("MAINBRAND").toString());
                    oneLv1.setPurEmpNo(oneData.get("PUREMPNO").toString());

                    oneLv1.setPurEmpName(oneData.get("PUREMPNAME").toString());
                    oneLv1.setPurDeptNo(oneData.get("PUREDEPTNO").toString());
                    oneLv1.setPurDeptName(oneData.get("PUREDEPTNAME").toString());
                    oneLv1.setLifeValue(oneData.get("LIFEVALUE").toString());
                    oneLv1.setGrade(oneData.get("GRADE").toString());
                    oneLv1.setRole(oneData.get("ROLE").toString());
                    oneLv1.setEnableContract(oneData.get("ENABLECONTRACT").toString());
                    oneLv1.setPayType(oneData.get("PAYTYPE").toString());
                    oneLv1.setPayCenter(oneData.get("PAYCENTER").toString());
                    oneLv1.setPayCenter_desc(oneData.get("PAYCENTER_DESC").toString());
                    oneLv1.setBillDateNo(oneData.get("BILLDATENO").toString());
                    oneLv1.setBillDate_desc(oneData.get("BILLDATE_DESC").toString());
                    oneLv1.setTaxCode(oneData.get("TAXCODE").toString());
                    oneLv1.setTaxRate(oneData.get("TAXRATE").toString());
                    oneLv1.setTaxName(oneData.get("TAXNAME").toString());
                    oneLv1.setInvoiceCode(oneData.get("INVOICECODE").toString());
                    oneLv1.setInvoiceName(oneData.get("INVOICENAME").toString());
                    oneLv1.setPayDateNo(oneData.get("PAYDATENO").toString());
                    oneLv1.setPayDate_desc(oneData.get("PAYDATE_DESC").toString());
                    oneLv1.setMainCurrency(oneData.get("MAINCURRENCY").toString());
                    oneLv1.setCurrName(oneData.get("CURRNAME").toString());
                    oneLv1.setBeginDate(oneData.get("BEGINDATE").toString());
                    oneLv1.setEndDate(oneData.get("ENDDATE").toString());
                    oneLv1.setMemo(oneData.get("MEMO").toString());
                    oneLv1.setStatus(oneData.get("STATUS").toString());
                    oneLv1.setSaleEmpNo(oneData.get("SALEEMPNO").toString());
                    oneLv1.setSaleEmpName(oneData.get("SALEEMPNAME").toString());
                    oneLv1.setSaleDeptNo(oneData.get("SALEDEPTNO").toString());
                    oneLv1.setSaleEmpName(oneData.get("SALEDEPTNAME").toString());
                    oneLv1.setCustGrade(oneData.get("CUSTGRADE").toString());
                    oneLv1.setCustPayType(oneData.get("CUSTPAYTYPE").toString());
                    oneLv1.setCustPayCenter(oneData.get("CUSTPAYCENTER").toString());
                    oneLv1.setCustPayCenterName(oneData.get("CUSTPAYCENTERNAME").toString());
                    oneLv1.setCustBillDateNo(oneData.get("CUSTBILLDATENO").toString());
                    oneLv1.setCustBillDate_desc(oneData.get("CUSTBILLDATENAME").toString());
                    oneLv1.setCustPayDateNo(oneData.get("CUSTPAYDATENO").toString());
                    oneLv1.setCustPayDate_desc(oneData.get("CUSTPAYDATENAME").toString());
                    oneLv1.setSaleInvoiceCode(oneData.get("SALEINVOICECODE").toString());
                    oneLv1.setSaleInvoiceName(oneData.get("SALEINVOICENAME").toString());

                    oneLv1.setCreatorID(oneData.get("CREATEOPID").toString());
                    oneLv1.setCreatorName(oneData.get("CREATEOPNAME").toString());
                    oneLv1.setCreatorDeptID(oneData.get("CREATEDEPTID").toString());
                    oneLv1.setCreatorDeptName(oneData.get("CREATEDEPTNAME").toString());
                    oneLv1.setCreate_Datetime(oneData.get("CREATETIME").toString());
                    oneLv1.setLastModifyID(oneData.get("LASTMODIOPID").toString());
                    oneLv1.setLastModifyName(oneData.get("LASTMODIOPNAME").toString());
                    oneLv1.setLastModify_Datetime(oneData.get("LASTMODITIME").toString());
                    String id = eID;
                    String bill = billNo;
                    String pType = payType;
                    //组织先筛选出符合当前条件的数据
                    getQLangData = getQLangDataDetail.stream()
                            .filter(LangData -> id.equals(LangData.get("EID").toString())
                                    && bill.equals(LangData.get("BIZPARTNERNO").toString()))
                            .collect(Collectors.toList());

                    oneLv1.setOrgList(new ArrayList<DCP_BizPartnerBillQueryRes.OrgList>());
                    for (Map<String, Object> oneData2 : getQLangData) {
                        DCP_BizPartnerBillQueryRes.OrgList oneLv2 = res.new OrgList();
                        oneLv2.setOrgNo(oneData2.get("ORGANIZATIONNO").toString());
                        oneLv2.setOrgName(oneData2.get("ORG_NAME").toString());
                        oneLv2.setStatus(oneData2.get("STATUS").toString());
                        oneLv1.getOrgList().add(oneLv2);
                        oneLv2 = null;
                    }

                    //账期先筛选出符合当前条件的数据
                    getQLangData = getBillDataDetail.stream()
                            .filter(LangData -> id.equals(LangData.get("EID").toString())
                                    && bill.equals(LangData.get("BIZPARTNERNO").toString()))
                            .collect(Collectors.toList());

                    oneLv1.setBillList(new ArrayList<DCP_BizPartnerBillQueryRes.BillList>());
                    for (Map<String, Object> oneData2 : getQLangData) {
                        DCP_BizPartnerBillQueryRes.BillList oneLv2 = res.new BillList();
                        oneLv2.setOrgNo(oneData2.get("ORGANIZATIONNO").toString());
                        oneLv2.setOrgName(oneData2.get("ORG_NAME").toString());
                        oneLv2.setContractNo(oneData2.get("CONTRACTNO").toString());
                        oneLv2.setItem(oneData2.get("ITEM").toString());
                        oneLv2.setBDate(oneData2.get("BDATE").toString());
                        oneLv2.setEDate(oneData2.get("EDATE").toString());
                        oneLv2.setIsCheck(oneData2.get("ISCHECK").toString());
                        oneLv2.setBillNo(oneData2.get("BILLNO").toString());
                        oneLv2.setBillType(oneData2.get("BILLTYPE").toString());

                        oneLv1.getBillList().add(oneLv2);
                        oneLv2 = null;
                    }
                    res.getDatas().add(oneLv1);
                    oneLv1 = null;
                }
            }


        } catch (Exception e) {
            // TODO Auto-generated catch block
            res.setDatas(new ArrayList<DCP_BizPartnerBillQueryRes.DataDetail>());
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());//add by 01029 20240703
        }

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    protected String getQuerySql(DCP_BizPartnerQueryReq req) throws Exception {
        // TODO Auto-generated method stub

        String sql = null;
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();
        String langType = req.getLangType();
        if (langType == null || langType.isEmpty()) {
            langType = "zh_CN";
        }

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        String key = null;
        String type = null;
        String status = null;
        String isBillQuery = null;
        String eId = req.geteId();

        if (req.getRequest() != null) {
            key = req.getRequest().getKeyTxt();
            status = req.getRequest().getStatus();
            type = req.getRequest().getBizType();
            isBillQuery = req.getRequest().getIsBillQuery();
        }

        StringBuilder sqlbuf = new StringBuilder();
        sqlbuf.append(" SELECT DBL.*  ")
                .append(" FROM ( ")
                .append(" SELECT ROWNUM AS rn ,  " +
                        " a.*  " +
                        "  , p.NAME as CREATEOPNAME,d.DEPARTNAME as CREATEDEPTNAME,c.NAME as lastModiOpName " +
                        "  ,e.NAME as PUREMPNAME,e4.NAME SALEEMPNAME,d.DEPARTNAME as PUREDEPTNAME " +
                        "  ,f.taxRate,TX1.TAXNAME , ff.ORG_NAME as PAYCENTER_DESC " +
                        "  ,BD.NAME AS BILLDATE_DESC ,PD.NAME AS PAYDATE_DESC,ie2.NAME SALEINVOICENAME  " +
                        "  ,IE.NAME AS INVOICENAME,cc.name as CURRNAME ,d2.DEPARTNAME SALEDEPTNAME" +
                        "  ,ol2.ORG_NAME CUSTPAYCENTERNAME,bd2.NAME CUSTBILLDATENAME,pd.NAME CUSTPAYDATENAME  ")
                .append(" FROM DCP_BIZPARTNER a ")
                .append(" left join DCP_EMPLOYEE  p on a.eid=p.eid and p.EMPLOYEENO=a.CREATEOPID ")
                .append(" left join DCP_EMPLOYEE  c on a.eid=c.eid and c.EMPLOYEENO=a.LASTMODIOPID ")
                .append(" left join DCP_EMPLOYEE  e on a.eid=e.eid and e.EMPLOYEENO=a.PUREMPNO ")
                .append(" left join DCP_EMPLOYEE e4 on a.eid=e4.eid and e4.EMPLOYEENO=a.SALEEMPNO ")
                .append(" left join DCP_TAXCATEGORY f on a.eid=f.eid and a.TAXCODE=f.TAXCODE and f.TAXAREA='CN' ")
                .append(" left join DCP_TAXCATEGORY_LANG TX1  on TX1.eid=f.eid and f.TAXCODE=TX1.TAXCODE and TX1.TAXAREA='CN'  and TX1.lang_type='").append(langType).append("' ")
                .append(" left join DCP_DEPARTMENT_LANG d on p.eid=d.eid and p.DEPARTMENTNO=d.DEPARTNO and d.lang_type='").append(langType).append("' ")
                .append(" left join DCP_DEPARTMENT_LANG d2  on d2.eid=a.eid and a.SALEDEPTNO=d2.DEPARTNO and d2.lang_type='").append(langType).append("' ")
                .append(" left join DCP_ORG_LANG ff on ff.eid=a.eid and ff.ORGANIZATIONNO=a.PAYCENTER and ff.lang_type='").append(langType).append("' ")
                .append(" left join DCP_ORG_LANG ol2 on ol2.eid=a.eid and ol2.ORGANIZATIONNO=a.CUSTPAYCENTER and ol2.lang_type='").append(langType).append("' ")
                .append(" left join DCP_BILLDATE  BD on a.eid=BD.eid and BD.BILLDATENO=a.BILLDATENO   ")
                .append(" left join DCP_BILLDATE  BD2 on a.eid=BD2.eid and BD2.BILLDATENO=a.CUSTBILLDATENO   ")
                .append(" left join DCP_PAYDATE  PD on a.eid=PD.eid and PD.PAYDATENO=a.PAYDATENO  ")
                .append(" left join DCP_PAYDATE  PD2 on a.eid=PD2.eid and PD2.PAYDATENO=a.CUSTPAYDATENO  ")
                .append(" left join DCP_INVOICETYPE  IE on a.eid=IE.eid and IE.INVOICECODE=a.INVOICECODE  ").append("  left join DCP_CURRENCY_LANG cc on cc.eid=a.eid and cc.CURRENCY=a.MAINCURRENCY  and cc.lang_type='").append(langType).append("' ")
                .append(" left join DCP_INVOICETYPE  IE2 on a.eid=IE2.eid and IE2.INVOICECODE=a.SALEINVOICECODE  ").append("  left join DCP_CURRENCY_LANG cc on cc.eid=a.eid and cc.CURRENCY=a.MAINCURRENCY  and cc.lang_type='").append(langType).append("' ")
        ;
        sqlbuf.append(" WHERE a.EID = '").append(eId).append("' ");

        if (key != null && !key.isEmpty()) {
            sqlbuf.append(" and  ( a.BIZPARTNERNO  =  " + SUtil.RetTrimStr(key));
            sqlbuf.append(" or    a.SName  =  " + SUtil.RetTrimStr(key));
            sqlbuf.append(" or    a.FName  =  " + SUtil.RetTrimStr(key));
            sqlbuf.append(" )  ");
        }
        if (type != null && !type.isEmpty())
            sqlbuf.append(" and a.BIZTYPE = " + SUtil.RetTrimStr(type));

        if (status != null && !status.isEmpty())
            sqlbuf.append(" and a.STATUS = '" + status + "' ");
        sqlbuf.append(" ) DBL  ");


        sqlbuf.append(" WHERE rn > " + startRow + " AND rn  <= " + (startRow + pageSize) + " ");
        sqlbuf.append(" order by BIZTYPE,BIZPARTNERNO ");


        sql = sqlbuf.toString();
        //logger.info(sql);
        return sql;
    }


    /**
     * 新增要處理的資料(先加入的, 先處理)
     *
     * @param row
     */
    protected final void addProcessData(DataProcessBean row) {
        this.pData.add(row);
    }

    /**
     * 计算总数
     *
     * @param req
     * @return
     * @throws Exception
     */
    protected String getCountSql(DCP_BizPartnerQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        String key = null;
        String status = null;
        String eId = req.geteId();
        String type = null;
        if (req.getRequest() != null) {
            key = req.getRequest().getKeyTxt();
            status = req.getRequest().getStatus();
            type = req.getRequest().getBizType();
        }

        StringBuffer sqlbuf = new StringBuffer("");
        String sql = "";

        sqlbuf.append("select num from( select count(*) AS num  from DCP_BIZPARTNER a "
                + "where a.EID='" + eId + "'  ");

        if (key != null && key.length() > 0) {
            sqlbuf.append(" and  ( a.BIZPARTNERNO  =  " + SUtil.RetTrimStr(key));
            sqlbuf.append(" or    a.SName  =  " + SUtil.RetTrimStr(key));
            sqlbuf.append(" or    a.FName  =  " + SUtil.RetTrimStr(key));
            sqlbuf.append(" )  ");
        }
        if (type != null && type.length() > 0)
            sqlbuf.append(" and  a.BIZTYPE like " + SUtil.RetLikeStr(type));
        if (status != null && status.length() > 0)
            sqlbuf.append(" and a.STATUS = '" + status + "' ");
        sqlbuf.append(" )");

        sql = sqlbuf.toString();
        return sql;
    }


    protected String getOrgQuerySql(DCP_BizPartnerQueryReq req) throws Exception {
        // TODO Auto-generated method stub

        String sql = null;
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();
        String langType = req.getLangType();
        if (langType == null || langType.isEmpty()) {
            langType = "zh_CN";
        }

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        String key = null;
        String status = null;
        String eId = req.geteId();
        String type = null;
        if (req.getRequest() != null) {
            key = req.getRequest().getKeyTxt();
            status = req.getRequest().getStatus();
            type = req.getRequest().getBizType();
        }

        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT DBL.*,b.* ,f.org_name  FROM ("
                + " SELECT ROWNUM AS rn   "
                + "  , a.eid,a.BIZPARTNERNO,a.BIZTYPE   "
                + " FROM DCP_BIZPARTNER a"
                + " WHERE a.EID = '" + eId + "' ");

        if (key != null && key.length() > 0) {
            sqlbuf.append(" and  ( a.BIZPARTNERNO  =  " + SUtil.RetTrimStr(key));
            sqlbuf.append(" or    a.SName  =  " + SUtil.RetTrimStr(key));
            sqlbuf.append(" or    a.FName  =  " + SUtil.RetTrimStr(key));
            sqlbuf.append(" )  ");
        }
        if (type != null && type.length() > 0)
            sqlbuf.append(" and a.BIZTYPE like " + SUtil.RetLikeStr(type));
        if (status != null && status.length() > 0)
            sqlbuf.append(" and a.STATUS = '" + status + "' ");
        sqlbuf.append("    and  ROWNUM > " + startRow + " AND ROWNUM  <= " + (startRow + pageSize) + " ");
        sqlbuf.append(" ) DBL   ");  //这里先查出和主数据一样的数据 然后关联语言档
        sqlbuf.append("  left join DCP_BIZPARTNER_ORG b  on DBL.eid=b.eid and DBL.BIZPARTNERNO=b.BIZPARTNERNO    ");
        sqlbuf.append("  left join DCP_ORG_LANG f on f.eid=b.eid and f.ORGANIZATIONNO=b.ORGANIZATIONNO and f.lang_type='" + langType + "' ");
        sql = sqlbuf.toString();
        //logger.info(sql);
        return sql;
    }

    protected String getBillQuerySql(DCP_BizPartnerQueryReq req) throws Exception {
        // TODO Auto-generated method stub

        String sql = null;
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();
        String langType = req.getLangType();
        if (langType == null || langType.isEmpty()) {
            langType = "zh_CN";
        }

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        String key = null;
        String status = null;
        String eId = req.geteId();
        String type = null;
        if (req.getRequest() != null) {
            key = req.getRequest().getKeyTxt();
            status = req.getRequest().getStatus();
            type = req.getRequest().getBizType();
        }

        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT DBL.*,b.* ,f.org_name  FROM ("
                + " SELECT ROWNUM AS rn   "
                + "  , a.eid,a.BIZPARTNERNO,a.BIZTYPE   "
                + " FROM DCP_BIZPARTNER a"
                + " WHERE a.EID = '" + eId + "' ");

        if (key != null && key.length() > 0) {
            sqlbuf.append(" and  ( a.BIZPARTNERNO  =  " + SUtil.RetTrimStr(key));
            sqlbuf.append(" or    a.SName  =  " + SUtil.RetTrimStr(key));
            sqlbuf.append(" or    a.FName  =  " + SUtil.RetTrimStr(key));
            sqlbuf.append(" )  ");
        }
        if (type != null && type.length() > 0)
            sqlbuf.append(" and a.BIZTYPE like " + SUtil.RetLikeStr(type));
        if (status != null && status.length() > 0)
            sqlbuf.append(" and a.STATUS = '" + status + "' ");
        sqlbuf.append("    and  ROWNUM > " + startRow + " AND ROWNUM  <= " + (startRow + pageSize) + " ");
        sqlbuf.append(" ) DBL   ");  //这里先查出和主数据一样的数据 然后关联语言档
        sqlbuf.append("  inner join DCP_BIZPARTNER_BILL b  on DBL.eid=b.eid and DBL.BIZPARTNERNO=b.BIZPARTNERNO    ");
        sqlbuf.append("  left join DCP_ORG_LANG f on f.eid=b.eid and f.ORGANIZATIONNO=b.ORGANIZATIONNO and f.lang_type='" + langType + "' ");
        sqlbuf.append(" order by BILLTYPE,ITEM,b.ORGANIZATIONNO ");
        sql = sqlbuf.toString();
        //logger.info(sql);
        return sql;
    }


}
