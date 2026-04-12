package com.dsc.spos.service.imp.json;


import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.json.cust.req.DCP_BizPartnerQueryReq;
import com.dsc.spos.json.cust.res.DCP_BizPartnerQueryRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.SUtil;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_BizPartnerQuery extends SPosBasicService<DCP_BizPartnerQueryReq, DCP_BizPartnerQueryRes> {

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
    protected DCP_BizPartnerQueryRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_BizPartnerQueryRes();
    }

    @Override
    protected DCP_BizPartnerQueryRes processJson(DCP_BizPartnerQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        DCP_BizPartnerQueryRes res = null;
        res = this.getResponse();
        String sql = null;
        try {

            sql = this.getCountSql(req);
            String[] condCountValues = {}; //查詢條件
            List<Map<String, Object>> getReasonCount = this.doQueryData(sql, condCountValues);
            int totalRecords;                                //总笔数
            int totalPages;                                    //总页数
            if (getReasonCount != null && !getReasonCount.isEmpty()) {
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
            sql = this.getContactQuerySql(req);
            List<Map<String, Object>> getContactDataDetail = this.doQueryData(sql, conditionValues1);
            sql = this.getLicenseQuerySql(req);
            List<Map<String, Object>> getLicenseDataDetail = this.doQueryData(sql, conditionValues1);
            sql = this.getBillQuerySql(req);
            List<Map<String, Object>> getBillDataDetail = this.doQueryData(sql, conditionValues1);
            sql = this.getCustTagQuerySql(req);
            List<Map<String, Object>> getCustTagDataDetail = this.doQueryData(sql, conditionValues1);
            List<Map<String, Object>> getQLangData = null;
            String eID = "";
            String billNo = "";
            String payType = "";
            if (getQDataDetail != null && getQDataDetail.isEmpty() == false) {
                res.setDatas(new ArrayList<>());

                for (Map<String, Object> oneData : getQDataDetail) {
                    DCP_BizPartnerQueryRes.Datas oneLv1 = res.new Datas();
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
                    oneLv1.setPayCenterName(oneData.get("PAYCENTERDESC").toString());
                    oneLv1.setBillDateNo(oneData.get("BILLDATENO").toString());
                    oneLv1.setBillDateDesc(oneData.get("BILLDATEDESC").toString());
                    oneLv1.setTaxCode(oneData.get("TAXCODE").toString());

                    oneLv1.setTaxName(oneData.get("TAXNAME").toString());
                    oneLv1.setInvoiceCode(oneData.get("INVOICECODE").toString());
                    oneLv1.setInvoiceName(oneData.get("INVOICENAME").toString());
                    oneLv1.setPayDateNo(oneData.get("PAYDATENO").toString());
                    oneLv1.setPayDateDesc(oneData.get("PAYDATEDESC").toString());
                    oneLv1.setMainCurrency(oneData.get("MAINCURRENCY").toString());
                    oneLv1.setCurrName(oneData.get("CURRNAME").toString());
                    oneLv1.setBeginDate(oneData.get("BEGINDATE").toString());
                    oneLv1.setEndDate(oneData.get("ENDDATE").toString());
                    oneLv1.setMemo(oneData.get("MEMO").toString());
                    oneLv1.setStatus(oneData.get("STATUS").toString());

                    oneLv1.setSaleEmpNo(oneData.get("SALEEMPNO").toString());
                    oneLv1.setSaleEmpName(oneData.get("SALEEMPNAME").toString());
                    oneLv1.setSaleDeptNo(oneData.get("SALEDEPTNO").toString());
                    oneLv1.setSaleDeptName(oneData.get("SALEDEPTNAME").toString());
                    oneLv1.setCustGrade(oneData.get("CUSTGRADE").toString());
                    oneLv1.setCustPayType(oneData.get("CUSTPAYTYPE").toString());
                    oneLv1.setCustPayCenter(oneData.get("CUSTPAYCENTER").toString());
                    oneLv1.setCustPayCenterName(oneData.get("CUSTPAYCENTERDESC").toString());
                    oneLv1.setCustBillDateNo(oneData.get("CUSTBILLDATENO").toString());
                    oneLv1.setCustBillDateDesc(oneData.get("CUSTBILLDATEDESC").toString());
                    oneLv1.setCustPayDateNo(oneData.get("CUSTPAYDATENO").toString());
                    oneLv1.setCustPayDateDesc(oneData.get("CUSTPAYDATEDESC").toString());
                    oneLv1.setSaleInvoiceCode(oneData.get("SALEINVOICECODE").toString());
                    oneLv1.setSaleInvoiceName(oneData.get("SALEINVOICENAME").toString());

                    oneLv1.setOfficeAddress(StringUtils.toString(oneData.get("OFFICE_ADDRESS"), ""));
                    oneLv1.setDeliveryAddress(StringUtils.toString(oneData.get("DELIVERY_ADDRESS"), ""));
                    oneLv1.setInvoiceAddress(StringUtils.toString(oneData.get("INVOICE_ADDRESS"), ""));
                    oneLv1.setAddress(StringUtils.toString(oneData.get("ADDRESS"), ""));
                    oneLv1.setIsCredit(StringUtils.toString(oneData.get("IS_CREDIT"), ""));
                    oneLv1.setCreditAmt(StringUtils.toString(oneData.get("CREDIT_AMT"), ""));
                    oneLv1.setCreditType(StringUtils.toString(oneData.get("CREDIT_TYPE"), ""));
                    oneLv1.setRestrictShop(StringUtils.toString(oneData.get("RESTRICTSHOP"), ""));
                    oneLv1.setCollectObject(StringUtils.toString(oneData.get("COLLECT_OBJECT"), ""));
                    oneLv1.setCollectShop(StringUtils.toString(oneData.get("COLLECT_SHOP"), ""));
                    oneLv1.setCollectShopName(StringUtils.toString(oneData.get("COLLECT_SHOPNAME"), ""));

                    oneLv1.setPayer(StringUtils.toString(oneData.get("PAYER"), ""));
                    oneLv1.setPayerName(StringUtils.toString(oneData.get("PAYERNAME"), ""));
                    oneLv1.setPayee(StringUtils.toString(oneData.get("PAYEE"), ""));
                    oneLv1.setPayeeName(StringUtils.toString(oneData.get("PAYEENAME"), ""));

                    oneLv1.setCreatorID(oneData.get("CREATEOPID").toString());
                    oneLv1.setCreatorName(oneData.get("CREATEOPNAME").toString());
                    oneLv1.setCreatorDeptID(oneData.get("CREATEDEPTID").toString());
                    oneLv1.setCreatorDeptName(oneData.get("CREATEDEPTNAME").toString());
                    oneLv1.setCreate_datetime(oneData.get("CREATETIME").toString());
                    oneLv1.setLastmodifyID(oneData.get("LASTMODIOPID").toString());
                    oneLv1.setLastmodifyName(oneData.get("LASTMODIOPNAME").toString());
                    oneLv1.setLastmodify_datetime(oneData.get("LASTMODITIME").toString());
                    String id = eID;
                    String bill = billNo;
                    String pType = payType;
                    //组织先筛选出符合当前条件的数据
                    getQLangData = getQLangDataDetail.stream()
                            .filter(LangData -> id.equals(LangData.get("EID").toString())
                                    && bill.equals(LangData.get("BIZPARTNERNO").toString()))
                            .collect(Collectors.toList());

                    oneLv1.setOrglist(new ArrayList<>());
                    for (Map<String, Object> oneData2 : getQLangData) {
                        DCP_BizPartnerQueryRes.OrgList oneLv2 = res.new OrgList();
                        oneLv2.setOrgNo(oneData2.get("ORGANIZATIONNO").toString());
                        oneLv2.setOrgName(oneData2.get("ORG_NAME").toString());
                        oneLv2.setStatus(oneData2.get("STATUS").toString());
                        oneLv1.getOrglist().add(oneLv2);
                        oneLv2 = null;
                    }


                    oneLv1.setRestrictShopList(new ArrayList<>());
                    List<Map<String, Object>> restrictShopList = doQueryData(getRestrictShopSql(req), null);
                    if (null != restrictShopList && !restrictShopList.isEmpty()) {
                        List<Map<String, Object>> customerShop = restrictShopList.stream().filter(shopData -> id.equals(StringUtils.toString(shopData.get("EID"),""))
                                && bill.equals(StringUtils.toString(shopData.get("CUSTOMERNO"),""))).collect(Collectors.toList());

                        for (Map<String, Object> oneShop : customerShop) {
                            DCP_BizPartnerQueryRes.RestrictShopList shopList = res.new RestrictShopList();
                            shopList.setShopId(StringUtils.toString(oneShop.get("SHOPID"), ""));
                            shopList.setShopName(StringUtils.toString(oneShop.get("SHOPNAME"), ""));

                            oneLv1.getRestrictShopList().add(shopList);
                        }
                    }


                    //conatct先筛选出符合当前条件的数据
                    getQLangData = getContactDataDetail.stream()
                            .filter(LangData -> id.equals(LangData.get("EID").toString())
                                    && bill.equals(LangData.get("BIZPARTNERNO").toString()))
                            .collect(Collectors.toList());

                    oneLv1.setContactlist(new ArrayList<>());
                    for (Map<String, Object> oneData2 : getQLangData) {
                        DCP_BizPartnerQueryRes.Contactlist oneLv2 = res.new Contactlist();
                        oneLv2.setItem(oneData2.get("ITEM").toString());
                        oneLv2.setConType(oneData2.get("CONTYPE").toString());
                        oneLv2.setContent(oneData2.get("CONTENT").toString());
                        oneLv2.setIsmainContact(oneData2.get("ISMAINCONTACT").toString());
                        oneLv2.setStatus(oneData2.get("STATUS").toString());
                        oneLv2.setMemo(oneData2.get("MEMO").toString());
                        oneLv2.setContact(oneData2.get("CONTACT").toString());
                        oneLv1.getContactlist().add(oneLv2);
                        oneLv2 = null;
                    }

                    //license先筛选出符合当前条件的数据
                    getQLangData = getLicenseDataDetail.stream()
                            .filter(LangData -> id.equals(LangData.get("EID").toString())
                                    && bill.equals(LangData.get("BIZPARTNERNO").toString()))
                            .collect(Collectors.toList());

                    oneLv1.setLicenseList(new ArrayList<DCP_BizPartnerQueryRes.LicenseList>());
                    for (Map<String, Object> oneData2 : getQLangData) {
                        DCP_BizPartnerQueryRes.LicenseList oneLv2 = res.new LicenseList();
                        oneLv2.setItem(oneData2.get("ITEM").toString());
                        oneLv2.setImgType(oneData2.get("IMGTYPE").toString());
                        oneLv2.setLicenseNo(oneData2.get("LICENSENO").toString());
                        oneLv2.setBeginDate(oneData2.get("BEGINDATE").toString());
                        oneLv2.setEndDate(oneData2.get("ENDDATE").toString());
                        oneLv2.setLicenseStatus(oneData2.get("STATUS").toString());
                        oneLv2.setStatus(oneData2.get("STATUS").toString());
                        oneLv2.setLicenseImg(oneData2.get("LICENSEIMG").toString());

                        oneLv1.getLicenseList().add(oneLv2);
                        oneLv2 = null;
                    }

                    //bill先筛选出符合当前条件的数据
                    getQLangData = getBillDataDetail.stream()
                            .filter(LangData -> id.equals(LangData.get("EID").toString())
                                    && bill.equals(LangData.get("BIZPARTNERNO").toString()))
                            .collect(Collectors.toList());

                    oneLv1.setBillList(new ArrayList<DCP_BizPartnerQueryRes.BillList>());
                    for (Map<String, Object> oneData2 : getQLangData) {
                        DCP_BizPartnerQueryRes.BillList oneLv2 = res.new BillList();
                        oneLv2.setOrgNo(oneData2.get("ORGANIZATIONNO").toString());
                        oneLv2.setOrgName(oneData2.get("ORG_NAME").toString());
                        oneLv2.setContractNO(oneData2.get("CONTRACTNO").toString());
                        oneLv2.setItem(oneData2.get("ITEM").toString());
                        oneLv2.setBdate(oneData2.get("BDATE").toString());
                        oneLv2.setEdate(oneData2.get("EDATE").toString());
                        oneLv2.setIsCheck(oneData2.get("ISCHECK").toString());
                        oneLv2.setBillDocno(oneData2.get("BILLNO").toString());
                        oneLv2.setBillType(oneData2.get("BILLTYPE").toString());

                        oneLv1.getBillList().add(oneLv2);
                        oneLv2 = null;
                    }

                    //custtag先筛选出符合当前条件的数据
                    getQLangData = getCustTagDataDetail.stream()
                            .filter(LangData -> id.equals(LangData.get("EID").toString())
                                    && bill.equals(LangData.get("ID").toString()))
                            .collect(Collectors.toList());

                    oneLv1.setCustTagList(new ArrayList<DCP_BizPartnerQueryRes.CustTagList>());
                    for (Map<String, Object> oneData2 : getQLangData) {
                        DCP_BizPartnerQueryRes.CustTagList oneLv2 = res.new CustTagList();
                        oneLv2.setTagNo(oneData2.get("TAGNO").toString());
                        oneLv2.setTagName(oneData2.get("NAME").toString());

                        oneLv1.getCustTagList().add(oneLv2);
                        oneLv2 = null;
                    }

                    res.getDatas().add(oneLv1);
                    oneLv1 = null;
                }
            }


        } catch (Exception e) {
            // TODO Auto-generated catch block
            res.setDatas(new ArrayList<>());
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

        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT * FROM ("
                + " SELECT row_number()  over (order by a.BIZTYPE ,a.BIZPARTNERNO) AS rn ,  a.*  "
                + "  , p.NAME as CREATEOPNAME,d.DEPARTNAME as CREATEDEPTNAME,c.NAME as lastModiOpName,e.NAME as PUREMPNAME "
                + "  , d.DEPARTNAME as PUREDEPTNAME,f.NAME as SALEEMPNAME, g.DEPARTNAME as SALEDEPTNAME"
                + " ,BD.NAME AS BILLDATEDESC ,PD.NAME AS PAYDATEDESC  ,IE.NAME AS INVOICENAME ,OG.SNAME AS PAYCENTERDESC ,TAXNAME "
                + " ,CBD.NAME AS CUSTBILLDATEDESC ,CPD.NAME AS CUSTPAYDATEDESC  ,CIE.NAME AS SALEINVOICENAME ,COG.SNAME AS CUSTPAYCENTERDESC  "
                + " ,CS.SNAME AS COLLECT_SHOPNAME,bp1.SNAME PAYERNAME,bp2.SNAME PAYEENAME,h.name as currname  "
                + " FROM DCP_BIZPARTNER a"
                + " left join DCP_EMPLOYEE  p on a.eid=p.eid and p.EMPLOYEENO=a.CREATEOPID "
                + " left join DCP_EMPLOYEE  c on a.eid=c.eid and c.EMPLOYEENO=a.LASTMODIOPID "
                + " left join DCP_EMPLOYEE  e on a.eid=e.eid and e.EMPLOYEENO=a.PUREMPNO "
                + " left join DCP_EMPLOYEE  f on a.eid=f.eid and f.EMPLOYEENO=a.SALEEMPNO "

                + " left join DCP_ORG  OG on a.eid=OG.eid and OG.ORGANIZATIONNO=a.PAYCENTER "
                + " left join DCP_ORG  CS on a.eid=CS.eid and CS.ORGANIZATIONNO=a.COLLECT_SHOP "
                + " left join DCP_BILLDATE  BD on a.eid=BD.eid and BD.BILLDATENO=a.BILLDATENO "
                + " left join DCP_PAYDATE  PD on a.eid=PD.eid and PD.PAYDATENO=a.PAYDATENO "
                + " left join DCP_INVOICETYPE  IE on a.eid=IE.eid and IE.INVOICECODE=a.INVOICECODE "

                + " left join DCP_ORG  COG on a.eid=COG.eid and COG.ORGANIZATIONNO=a.CUSTPAYCENTER "
                + " left join DCP_BILLDATE  CBD on a.eid=CBD.eid and CBD.BILLDATENO=a.CUSTBILLDATENO "
                + " left join DCP_PAYDATE  CPD on a.eid=CPD.eid and CPD.PAYDATENO=a.CUSTPAYDATENO "
                + " left join DCP_INVOICETYPE  CIE on a.eid=CIE.eid and CIE.INVOICECODE=a.SALEINVOICECODE "

                + " left join DCP_BIZPARTNER bp1 on bp1.eid=a.eid and bp1.BIZPARTNERNO=a.PAYER "
                + " left join DCP_BIZPARTNER bp2 on bp2.eid=a.eid and bp2.BIZPARTNERNO=a.PAYEE "

                + " left join DCP_TAXCATEGORY  TX on a.eid=TX.eid and TX.TAXCODE=a.TAXCODE "
                + "  left join DCP_TAXCATEGORY_LANG TX1  on TX1.eid=TX.eid and TX.TAXCODE=TX1.TAXCODE   and TX1.lang_type='" + langType + "' "
                + "  left join DCP_DEPARTMENT_LANG d  on p.eid=d.eid and p.DEPARTMENTNO=d.DEPARTNO and d.lang_type='" + langType + "' "
                + "  left join DCP_DEPARTMENT_LANG g  on f.eid=g.eid and f.DEPARTMENTNO=g.DEPARTNO and d.lang_type='" + langType + "' " +
                "  left join DCP_CURRENCY_LANG h on h.eid=a.eid and h.CURRENCY=a.MAINCURRENCY and h.lang_type='"+req.getLangType()+"' "

                + " WHERE a.EID = '" + eId + "' ");

        if (key != null && key.length() > 0) {
            sqlbuf.append(" and  ( a.BIZPARTNERNO  like  " + SUtil.RetLikeStr(key));
            sqlbuf.append(" or    a.SName  like  " + SUtil.RetLikeStr(key));
            sqlbuf.append(" or    a.FName  like  " + SUtil.RetLikeStr(key));
            sqlbuf.append(" )  ");
        }
        if (type != null && type.length() > 0)
            sqlbuf.append(" and a.BIZTYPE like " + SUtil.RetLikeStr(type));

        if (status != null && status.length() > 0)
            sqlbuf.append(" and a.STATUS = '" + status + "' ");
        sqlbuf.append(" ) DBL  WHERE rn > " + startRow + " AND rn  <= " + (startRow + pageSize) + " ");
        //sqlbuf.append(" order by BIZTYPE,BIZPARTNERNO " );


        sql = sqlbuf.toString();
        //logger.info(sql);
        return sql;
    }


    private String getRestrictShopSql(DCP_BizPartnerQueryReq req) throws Exception {

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
        StringBuilder builder = new StringBuilder();

        builder.append(" SELECT * FROM ( " +
                " SELECT row_number()  over (order by a.BIZTYPE,a.BIZPARTNERNO) AS rn,b.EID,b.CUSTOMERNO,B.SHOPID,C.ORG_NAME SHOPNAME FROM" +
                " DCP_CUSTOMER_SHOP b " +
                " LEFT JOIN  DCP_BIZPARTNER a  ON a.EID=b.EID AND a.BIZPARTNERNO=b.CUSTOMERNO  " +
                " LEFT JOIN DCP_ORG_LANG c ON b.EID=c.EID and c.ORGANIZATIONNO=b.SHOPID AND c.LANG_TYPE='"+langType + "'"+
                " WHERE a.EID='").append(req.geteId()).append("'");

        if (key != null && !key.isEmpty()) {
            builder.append(" and  ( a.BIZPARTNERNO  like  " + SUtil.RetLikeStr(key));
            builder.append(" or    b.SHOPID  like  " + SUtil.RetLikeStr(key));
            builder.append(" or    a.FName  like  " + SUtil.RetLikeStr(key));
            builder.append(" )  ");
        }
        if (type != null && !type.isEmpty())
            builder.append(" and a.BIZTYPE like " + SUtil.RetLikeStr(type));

        if (status != null && !status.isEmpty())
            builder.append(" and a.STATUS = '" + status + "' ");
        builder.append(" ) DBL  WHERE rn > " + startRow + " AND rn  <= " + (startRow + pageSize) + " ");
        //sqlbuf.append(" order by BIZTYPE,BIZPARTNERNO " );


        return builder.toString();
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
            sqlbuf.append(" and  ( a.BIZPARTNERNO  like  " + SUtil.RetLikeStr(key));
            sqlbuf.append(" or    a.SName  like  " + SUtil.RetLikeStr(key));
            sqlbuf.append(" or    a.FName  like  " + SUtil.RetLikeStr(key));
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
        sqlbuf.append("SELECT DBL.*,b.* ,f.org_name FROM ("
                + " SELECT  *  FROM ( SELECT  row_number()  over (order by BIZTYPE ,BIZPARTNERNO) AS rn  "
                + "  , a.eid,a.BIZPARTNERNO,a.BIZTYPE   "
                + " FROM DCP_BIZPARTNER a"
                + " WHERE a.EID = '" + eId + "' ");

        if (key != null && key.length() > 0) {
            sqlbuf.append(" and  ( a.BIZPARTNERNO  like  " + SUtil.RetLikeStr(key));
            sqlbuf.append(" or    a.SName  like  " + SUtil.RetLikeStr(key));
            sqlbuf.append(" or    a.FName  like  " + SUtil.RetLikeStr(key));
            sqlbuf.append(" )  ");
        }
        if (type != null && type.length() > 0)
            sqlbuf.append(" and a.BIZTYPE like " + SUtil.RetLikeStr(type));
        if (status != null && status.length() > 0)
            sqlbuf.append(" and a.STATUS = '" + status + "' ");
        sqlbuf.append("  )aa    where  rn > " + startRow + " AND rn  <= " + (startRow + pageSize) + " ");
        sqlbuf.append(" ) DBL   ");  //这里先查出和主数据一样的数据 然后关联语言档
        sqlbuf.append("  inner join DCP_BIZPARTNER_ORG b  on DBL.eid=b.eid and DBL.BIZPARTNERNO=b.BIZPARTNERNO    ");
        sqlbuf.append("  inner join DCP_ORG_LANG f on f.eid=b.eid and f.ORGANIZATIONNO=b.ORGANIZATIONNO and f.lang_type='" + langType + "' ");
        sql = sqlbuf.toString();
        //logger.info(sql);
        return sql;
    }

    protected String getContactQuerySql(DCP_BizPartnerQueryReq req) throws Exception {
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
        sqlbuf.append("SELECT DBL.*,b.*  FROM ("
                + " SELECT  *  FROM ( SELECT  row_number()  over (order by BIZTYPE ,BIZPARTNERNO) AS rn  "
                + "  , a.eid,a.BIZPARTNERNO,a.BIZTYPE   "
                + " FROM DCP_BIZPARTNER a"
                + " WHERE a.EID = '" + eId + "' ");

        if (key != null && key.length() > 0) {
            sqlbuf.append(" and  ( a.BIZPARTNERNO  like  " + SUtil.RetLikeStr(key));
            sqlbuf.append(" or    a.SName  like  " + SUtil.RetLikeStr(key));
            sqlbuf.append(" or    a.FName  like  " + SUtil.RetLikeStr(key));
            sqlbuf.append(" )  ");
        }
        if (type != null && type.length() > 0)
            sqlbuf.append(" and a.BIZTYPE like " + SUtil.RetLikeStr(type));
        if (status != null && status.length() > 0)
            sqlbuf.append(" and a.STATUS = '" + status + "' ");
        sqlbuf.append("  )aa    where  rn > " + startRow + " AND rn  <= " + (startRow + pageSize) + " ");
        sqlbuf.append(" ) DBL   ");  //这里先查出和主数据一样的数据 然后关联语言档
        sqlbuf.append("  inner join DCP_BIZPARTNER_CONTACT b  on DBL.eid=b.eid and DBL.BIZPARTNERNO=b.BIZPARTNERNO    ");
        sql = sqlbuf.toString();
        //logger.info(sql);
        return sql;
    }

    protected String getLicenseQuerySql(DCP_BizPartnerQueryReq req) throws Exception {
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
        sqlbuf.append("SELECT DBL.*,b.*  FROM ("
                + " SELECT  *  FROM ( SELECT  row_number()  over (order by BIZTYPE ,BIZPARTNERNO) AS rn  "
                + "  , a.eid,a.BIZPARTNERNO,a.BIZTYPE   "
                + " FROM DCP_BIZPARTNER a"
                + " WHERE a.EID = '" + eId + "' ");

        if (key != null && key.length() > 0) {
            sqlbuf.append(" and  ( a.BIZPARTNERNO  like  " + SUtil.RetLikeStr(key));
            sqlbuf.append(" or    a.SName  like  " + SUtil.RetLikeStr(key));
            sqlbuf.append(" or    a.FName  like  " + SUtil.RetLikeStr(key));
            sqlbuf.append(" )  ");
        }
        if (type != null && type.length() > 0)
            sqlbuf.append(" and a.BIZTYPE like " + SUtil.RetLikeStr(type));
        if (status != null && status.length() > 0)
            sqlbuf.append(" and a.STATUS = '" + status + "' ");
        sqlbuf.append("  )aa    where  rn > " + startRow + " AND rn  <= " + (startRow + pageSize) + " ");
        sqlbuf.append(" ) DBL   ");  //这里先查出和主数据一样的数据 然后关联语言档
        sqlbuf.append("  inner join DCP_SUPPLIER_LICENSE b  on DBL.eid=b.eid and DBL.BIZPARTNERNO=b.SUPPLIER    ");
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
        String isBillQry = "N";
        if (req.getRequest() != null) {
            key = req.getRequest().getKeyTxt();
            status = req.getRequest().getStatus();
            type = req.getRequest().getBizType();
            isBillQry = req.getRequest().getIsBillQuery();
        }

        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT DBL.*,b.*,f.org_name  FROM ("
                + " SELECT  *  FROM ( SELECT  row_number()  over (order by BIZTYPE ,BIZPARTNERNO) AS rn  "
                + "  , a.eid,a.BIZPARTNERNO,a.BIZTYPE   "
                + " FROM DCP_BIZPARTNER a"
                + " WHERE a.EID = '" + eId + "' ");

        if (key != null && key.length() > 0) {
            sqlbuf.append(" and  ( a.BIZPARTNERNO  like  " + SUtil.RetLikeStr(key));
            sqlbuf.append(" or    a.SName  like  " + SUtil.RetLikeStr(key));
            sqlbuf.append(" or    a.FName  like  " + SUtil.RetLikeStr(key));
            sqlbuf.append(" )  ");
        }
        if (type != null && type.length() > 0)
            sqlbuf.append(" and a.BIZTYPE like " + SUtil.RetLikeStr(type));
        if (status != null && status.length() > 0)
            sqlbuf.append(" and a.STATUS = '" + status + "' ");
        if ("N".equals(isBillQry))
            sqlbuf.append(" and 1<>1 ");
        sqlbuf.append("  )aa    where  rn > " + startRow + " AND rn  <= " + (startRow + pageSize) + " ");
        sqlbuf.append(" ) DBL   ");  //这里先查出和主数据一样的数据 然后关联语言档
        sqlbuf.append("  inner join DCP_BIZPARTNER_BILL b  on DBL.eid=b.eid and DBL.BIZPARTNERNO=b.BIZPARTNERNO    ");
        sqlbuf.append("  inner join DCP_ORG_LANG f on f.eid=b.eid and f.ORGANIZATIONNO=b.ORGANIZATIONNO and f.lang_type='" + langType + "' ");
        sql = sqlbuf.toString();
        //logger.info(sql);
        return sql;
    }

    protected String getCustTagQuerySql(DCP_BizPartnerQueryReq req) throws Exception {
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
        sqlbuf.append("SELECT DBL.*,b.*  FROM ("
                + " SELECT  *  FROM ( SELECT  row_number()  over (order by BIZTYPE ,BIZPARTNERNO) AS rn  "
                + "  , a.eid,a.BIZPARTNERNO,a.BIZTYPE   "
                + " FROM DCP_BIZPARTNER a"
                + " WHERE a.EID = '" + eId + "' ");

        if (key != null && key.length() > 0) {
            sqlbuf.append(" and  ( a.BIZPARTNERNO  like  " + SUtil.RetLikeStr(key));
            sqlbuf.append(" or    a.SName  like  " + SUtil.RetLikeStr(key));
            sqlbuf.append(" or    a.FName  like  " + SUtil.RetLikeStr(key));
            sqlbuf.append(" )  ");
        }
        if (type != null && type.length() > 0)
            sqlbuf.append(" and a.BIZTYPE like " + SUtil.RetLikeStr(type));
        if (status != null && status.length() > 0)
            sqlbuf.append(" and a.STATUS = '" + status + "' ");
        sqlbuf.append("  )aa    where  rn > " + startRow + " AND rn  <= " + (startRow + pageSize) + " ");
        sqlbuf.append("  ) DBL   ");  //这里先查出和主数据一样的数据 然后关联语言档
        sqlbuf.append("  inner join DCP_TAGTYPE_DETAIL b  on DBL.eid=b.eid and DBL.BIZPARTNERNO=b.ID    ");
        sql = sqlbuf.toString();
        //logger.info(sql);
        return sql;
    }

}
