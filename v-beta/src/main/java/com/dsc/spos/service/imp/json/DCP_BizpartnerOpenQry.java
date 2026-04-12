package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_BizpartnerOpenQryReq;
import com.dsc.spos.json.cust.res.DCP_BizpartnerOpenQryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_BizpartnerOpenQry extends SPosBasicService<DCP_BizpartnerOpenQryReq, DCP_BizpartnerOpenQryRes> {
    @Override
    protected boolean isVerifyFail(DCP_BizpartnerOpenQryReq req) throws Exception {
        boolean isFail = false;
        DCP_BizpartnerOpenQryReq.levelElm request = req.getRequest();
        StringBuffer errMsg = new StringBuffer("");
        if (request == null) {
            isFail = true;
            errMsg.append("request不能为空 ");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }


        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_BizpartnerOpenQryReq> getRequestType() {
        return new TypeToken<DCP_BizpartnerOpenQryReq>() {
        };
    }

    @Override
    protected DCP_BizpartnerOpenQryRes getResponseType() {
        return new DCP_BizpartnerOpenQryRes();
    }

    @Override
    protected DCP_BizpartnerOpenQryRes processJson(DCP_BizpartnerOpenQryReq req) throws Exception {
        DCP_BizpartnerOpenQryRes res = null;
        res = this.getResponse();

        int totalRecords = 0;                                //总笔数
        int totalPages = 0;

        res.setDatas(new ArrayList<DCP_BizpartnerOpenQryRes.level1Elm>());
        // try {
        String sql = "";

        String ischeck_restrictgroup = req.getRequest().getIscheck_restrictgroup();
        String groupType = req.getRequest().getGroupType();
        if ("Y".equals(ischeck_restrictgroup) && "1".equals(groupType)) {
            List bizNos = getBizNos(req);
            if (bizNos.size() <= 0) {
                res.setSuccess(false);
                res.setServiceStatus("200");
                res.setServiceDescription("用户在当前组织据点下无可操作的交易对象");
                return res;
            }
            sql = this.getQuerySqlWithNo(req, bizNos);
        } else {
            sql = this.getQuerySql(req);
        }
        List<Map<String, Object>> getBizList = this.doQueryData(sql, null);
        if (!CollectionUtils.isEmpty(getBizList)) {

            String num = getBizList.get(0).get("NUM").toString();
            totalRecords = Integer.parseInt(num);

            // 计算页数
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            List<String> bizNos = new ArrayList();
            for (Map<String, Object> biz : getBizList) {
                DCP_BizpartnerOpenQryRes.level1Elm level1Elm = res.new level1Elm();
                String bizpartnerno = biz.get("BIZPARTNERNO").toString();
                if (!bizNos.contains(bizpartnerno)) {
                    bizNos.add(bizpartnerno);
                }
                level1Elm.setStatus(biz.get("STATUS").toString());
                level1Elm.setBizpartnerNo(biz.get("BIZPARTNERNO").toString());
                level1Elm.setSName(biz.get("SNAME").toString());
                level1Elm.setFullName(biz.get("FNAME").toString());
                level1Elm.setMainContact(biz.get("MAINCONTACT").toString());
                level1Elm.setTaxCode(biz.get("TAXCODE").toString());
                level1Elm.setTaxName(biz.get("TAXNAME").toString());
                level1Elm.setPayType(biz.get("PAYTYPE").toString());
                level1Elm.setBillDateNo(biz.get("BILLDATENO").toString());
                level1Elm.setBillDate_desc(biz.get("BILLDATE_DESC").toString());
                level1Elm.setPayCenter(biz.get("PAYCENTER").toString());
                level1Elm.setPayCenterName(biz.get("PAYCENTERNAME").toString());
                level1Elm.setPayDateNo(biz.get("PAYDATENO").toString());
                level1Elm.setPayDate_desc(biz.get("PAYDATE_DESC").toString());
                level1Elm.setInvoiceCode(StringUtils.toString(biz.get("INVOICECODE"), ""));
                level1Elm.setCustPayDateName(StringUtils.toString(biz.get("CUSTPAYDATENAME"), ""));
                level1Elm.setInvoiceName(biz.get("INVOICE_NAME").toString());
                level1Elm.setCurrency(biz.get("CURRENCY").toString());
                level1Elm.setSaleInvoiceCode(StringUtils.toString(biz.get("SALEINVOICECODE"), ""));

                level1Elm.setSaleInvoiceName(StringUtils.toString(biz.get("SALEINVOICENAME"), ""));
                level1Elm.setCurrencyName(biz.get("CURRENCYNAME").toString());
                level1Elm.setTaxRate(biz.get("TAXRATE").toString());
                level1Elm.setInclTax(biz.get("INCLTAX").toString());
                level1Elm.setTaxCalType(biz.get("TAXCALTYPE").toString());

                level1Elm.setSaleEmpNo(StringUtils.toString(biz.get("SALEEMPNO"), ""));
                level1Elm.setSaleEmpName(StringUtils.toString(biz.get("SALEEMPNAME"), ""));
                level1Elm.setSaleDeptNo(StringUtils.toString(biz.get("SALEDEPTNO"), ""));
                level1Elm.setSaleDeptName(StringUtils.toString(biz.get("SALEDEPTNAME"), ""));
                level1Elm.setCustPayType(StringUtils.toString(biz.get("CUSTPAYTYPE"), ""));
                level1Elm.setCustPayCenter(StringUtils.toString(biz.get("CUSTPAYCENTER"), ""));
                level1Elm.setCustPayCenterName(StringUtils.toString(biz.get("CUSTPAYCENTERNAME"), ""));
                level1Elm.setCustBillDateNo(StringUtils.toString(biz.get("CUSTBILLDATENO"), ""));
                level1Elm.setCustBillDateName(StringUtils.toString(biz.get("CUSTBILLDATENAME"), ""));
                level1Elm.setCustPayDateNo(StringUtils.toString(biz.get("CUSTPAYDATENO"), ""));

                level1Elm.setAddress(StringUtils.toString(biz.get("ADDRESS"), ""));
                level1Elm.setDeliveryAddress(StringUtils.toString(biz.get("DELIVERY_ADDRESS"), ""));
                level1Elm.setOfficeAddress(StringUtils.toString(biz.get("OFFICE_ADDRESS"), ""));
                level1Elm.setInvoiceAddress(StringUtils.toString(biz.get("INVOICE_ADDRESS"), ""));

                if ("Y".equals(StringUtils.toString(biz.get("ISMAINCONTACT"), ""))) {
                    level1Elm.setMainConMan(StringUtils.toString(biz.get("MAINCONMAN"), ""));
                } else {
                    level1Elm.setMainConMan("");
                }

                if ("2".equals(StringUtils.toString(biz.get("CONTYPE"), ""))) {
                    level1Elm.setTelephone(StringUtils.toString(biz.get("CONTENT"), ""));
                } else {
                    level1Elm.setTelephone("");
                }

                level1Elm.setTelephone(StringUtils.toString(biz.get("TELEPHONE"), ""));
                level1Elm.setBeginDate(StringUtils.toString(biz.get("BEGINDATE1"), ""));
                level1Elm.setEndDate(StringUtils.toString(biz.get("ENDDATE1"), ""));

                level1Elm.setPayer(StringUtils.toString(biz.get("PAYER"), ""));
                level1Elm.setPayerName(StringUtils.toString(biz.get("PAYERNAME"), ""));
                level1Elm.setPayee(StringUtils.toString(biz.get("PAYEE"), ""));
                level1Elm.setPayeeName(StringUtils.toString(biz.get("PAYEENAME"), ""));

                res.getDatas().add(level1Elm);
            }

            //查询明细
            if (bizNos.size() > 0) {
                String orgSql = getOrgSql(bizNos, req);
                List<Map<String, Object>> getOrgList = this.doQueryData(orgSql, null);
                if (!CollectionUtils.isEmpty(getOrgList)) {
                    for (DCP_BizpartnerOpenQryRes.level1Elm level1Elm : res.getDatas()) {
                        level1Elm.setOrglist(new ArrayList<>());
                        for (Map<String, Object> org : getOrgList) {
                            if (level1Elm.getBizpartnerNo().equals(org.get("BIZPARTNERNO").toString())) {
                                DCP_BizpartnerOpenQryRes.OrgList level2Elm = res.new OrgList();
                                level2Elm.setOrgNo(org.get("ORGNO").toString());
                                level2Elm.setOrgName(org.get("ORGNAME").toString());
                                level1Elm.getOrglist().add(level2Elm);
                            }
                        }
                    }
                }
            }

        }

        res.setTotalRecords(totalRecords);
        res.setTotalPages(totalPages);
        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
        //} catch (Exception e) {
        //   res.setSuccess(false);
        //    res.setServiceStatus("200");
        //    res.setServiceDescription("服务执行失败!"+e.getMessage());
        // }
        return res;
    }

    private String getQuerySqlWithNo(DCP_BizpartnerOpenQryReq req, List<String> bizNos) throws Exception {

        String langType = req.getLangType();
        String eid = req.geteId();
        String keyTxt = req.getRequest().getKeyTxt();
        String status = req.getRequest().getStatus();
        String[] bizType = req.getRequest().getBizType();

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //分页起始位置
        int startRow = (pageNumber - 1) * pageSize;

        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");

        StringBuffer sJoinNo = new StringBuffer("");
        for (String bizNo : bizNos) {
            sJoinNo.append(bizNo + ",");
        }
        Map<String, String> mapOrder = new HashMap<String, String>();
        mapOrder.put("BIZPARTNERNO", sJoinNo.toString());

        MyCommon cm = new MyCommon();
        String withasSql_bizno = cm.getFormatSourceMultiColWith(mapOrder);

        sqlbuf.append("  with p AS ( " + withasSql_bizno + " )  " +
                "SELECT * FROM ( " +
                " SELECT COUNT(DISTINCT BIZPARTNERNO) OVER () AS num, DENSE_RANK() OVER (ORDER BY BIZPARTNERNO) AS rn , a.* FROM ( " +
                " SELECT distinct a.STATUS, a.BIZPARTNERNO,a.SNAME, a.FNAME,c.CONTENT as MAINCONTACT,a.TAXCODE,d.TAXNAME,a.paytype,a.BILLDATENO,e.name as BILLDATE_DESC," +
                " a.paycenter,f.org_name as paycentername," +
                " a.CUSTPAYCENTER,l.org_name CUSTPAYCENTERNAME, " +
                " g.INVOICECODE,g.INVOICE_NAME,h.CURRENCY,h.name as CURRENCYNAME,i.name as paydate_desc,i.PAYDATENO,j.taxrate,j.INCLTAX，" +
                " dd0.DEPARTNAME AS SALEDEPTNAME,db1.name CUSTBILLDATENAME, " +
                " it1.INVOICE_NAME as SALEINVOICENAME,j.TAXCALTYPE,to_char(a.begindate,'yyyy-MM-dd') as begindate1,to_char(a.enddate,'yyyy-MM-dd') as enddate1    " +
                " ,bp1.SNAME PAYERNAME,bp2.SNAME PAYEENAME "+
                " FROM DCP_BIZPARTNER a " +
                " inner join p on p.BIZPARTNERNO=a.BIZPARTNERNO " +
                " LEFT JOIN DCP_BIZPARTNER_CONTACT c ON c.EID=a.EID AND a.bizpartnerNo = c.bizpartnerNo and c.ISMAINCONTACT='Y' " +
                " LEFT JOIN DCP_TAXCATEGORY_LANG d on d.EID=a.eid and d.TAXCODE=a.TAXCODE and d.lang_type='" + req.getLangType() + "' " +
                " left join DCP_BILLDATE_LANG e on e.eid=a.eid and e.billdateno=a.billdateno and e.lang_type='" + req.getLangType() + "' " +
                " left join DCP_BILLDATE_LANG db1 on db1.eid=a.eid and db1.billdateno=a.CUSTBILLDATENO and db1.lang_type='" + req.getLangType() + "' " +
                " left join DCP_ORG_LANG f on f.eid=a.eid and f.ORGANIZATIONNO=a.paycenter and f.lang_type='" + req.getLangType() + "' " +
                " left join DCP_ORG_LANG l on l.eid=a.eid and l.ORGANIZATIONNO=a.CUSTPAYCENTER and l.lang_type='" + req.getLangType() + "' " +
                " left join DCP_INVOICETYPE_LANG g on g.eid =a.eid and g.INVOICECODE=a.INVOICECODE and g.lang_type='" + req.getLangType() + "' " +
                " left join DCP_INVOICETYPE_LANG it1 on it1.eid =a.eid and it1.INVOICECODE=a.SALEINVOICECODE and it1.lang_type='" + req.getLangType() + "' " +
                " left join DCP_CURRENCY_LANG h on h.eid=a.eid and h.currency=a.MAINCURRENCY and h.lang_type='" + req.getLangType() + "' " +
                " left join DCP_TAXCATEGORY j on j.eid=a.eid and j.taxcode=a.taxcode " +
                " left join DCP_PAYDATE_LANG PD0 on PD0.eid=a.eid and PD0.PAYDATENO=a.CUSTPAYDATENO and PD0.lang_type='" + req.getLangType() + "' " +
                " left join DCP_PAYDATE_LANG i on i.eid=a.eid and i.PAYDATENO=a.PAYDATENO and i.lang_type='" + req.getLangType() + "' " +
                " left join DCP_BIZPARTNER bp1 on bp1.eid=a.eid and bp1.BIZPARTNERNO=a.PAYER " +
                " left join DCP_BIZPARTNER bp2 on bp2.eid=a.eid and bp2.BIZPARTNERNO=a.PAYEE " +


                " WHERE a.EID = '" + eid + "'   " +// and a.organizationno='"+req.getOrganizationNO()+"'
                " ");

        sqlbuf.append(" and trunc(a.BEGINDATE)<=trunc(sysdate)   and trunc(a.enddate)>=trunc(sysdate) ");

        if (!Check.Null(status)) {
            sqlbuf.append(" AND a.status = '" + status + "'");

        }
        if (!Check.Null(keyTxt)) {
            sqlbuf.append(" AND (a.bizpartnerNo LIKE '%%" + keyTxt + "%%' " +
                    " or a.SNAME LIKE '%%" + keyTxt + "%%' " +
                    " or a.FNAME LIKE '%%" + keyTxt + "%%' " +
                    ")");
        }
        String bizTypeStr = "";
        for (String type : bizType) {
            bizTypeStr += "'" + type + "',";
        }
        if (bizTypeStr.length() > 0) {
            bizTypeStr = bizTypeStr.substring(0, bizTypeStr.length() - 1);
            sqlbuf.append(" AND a.BIZTYPE IN (" + bizTypeStr + ")");
        }


        sqlbuf.append("  ) a ) WHERE RN > " + startRow + " AND rn <= " + (startRow + pageSize) + " ORDER BY BIZPARTNERNO DESC ");

        sql = sqlbuf.toString();
        return sql;
    }

    private List getBizNos(DCP_BizpartnerOpenQryReq req) throws Exception {

        // //1、根据传参【检查控制组】、【控制组类型】过滤用户在当前据点可使用交易对象范围
        //            //【检查控制组】=Y，【控制组类型】='1-采购'，则
        //            //① 查询用户是否存在有效的采购控制组，不存在代表不设限，直接返回有效交易对象数据（用员工编号以及所属部门条件分别检索符合条件的控制组，允许有多个控制组，多个控制组取并集）
        //            //
        //            //② 检查用户所在控制组范围关联【适用组织】数据，判断：
        //            //- 若【适用组织】为空，则代表该控制组对所有组织据点不设限，控制组编号保留；
        //            //- 若【适用组织】不为空，判断传入组织据点是否存在控制组【限定组织】范围内，不存在则代表当前组织据点不在控制组可用组织范围内，排除该控制组；存在则保留该控制组，继续第③步判断
        //            //
        //            //③ 用户所在控制组不为空，则查询关联【限定交易对象】，多组数据去重合并后返回可用交易对象数据集。用户所在控制组为空，则返回空；
        //            //
        //            //若最后返回数据集为空，则提示用户在当前组织据点下无可操作的交易对象；
        String employeeNo = req.getEmployeeNo();
        String departmentNo = req.getDepartmentNo();
        String eid = req.geteId();
        String organizationNO = req.getOrganizationNO();
        String sql = "select a.GROUPNO from DCP_RESTRICTGROUP_EMP a where a.eid='" + eid + " and a.EMPLOYEENO='" + employeeNo + "' and a.status='100' and a.grouptype='1' ";
        sql += " union all ";
        sql += "select a.GROUPNO from DCP_RESTRICTGROUP_DEPT a where a.eid='" + eid + " and a.DEPARTMENTNO='" + departmentNo + "' and a.status='100' and a.grouptype='1'  ";
        List<Map<String, Object>> list = this.doQueryData(sql, null);
        List<String> groupno = list.stream().map(var -> var.get("GROUPNO").toString()).collect(Collectors.toList());
        List<String> validGroupNos = new ArrayList();
        if (organizationNO == null || organizationNO.equals("")) {
            validGroupNos = groupno;
        } else {
            sql = "select a.GROUPNO from DCP_RESTRICTGROUP_ORG a where a.eid='" + eid + " and a.ORGANIZATIONNO='" + organizationNO + "' and a.status='100' and a.grouptype='1' ";
            List<Map<String, Object>> list2 = this.doQueryData(sql, null);
            List<String> groupno2 = list2.stream()
                    .map(var -> var.get("GROUPNO").toString())
                    .collect(Collectors.toList());

            validGroupNos = groupno.stream()
                    .filter(groupNo -> groupno2.contains(groupNo))
                    .collect(Collectors.toList());
        }

        if (validGroupNos != null && validGroupNos.size() > 0) {
            String sqlNos = "";
            for (String validNo : validGroupNos) {
                sqlNos += "'" + validNo + "',";
            }
            sqlNos = sqlNos.substring(0, sqlNos.length() - 1);
            sql = "select a.BIZPARTNERNO from DCP_RESTRICTGROUP_BIZPARTNER a where a.eid='" + eid + "' and a.status='100' and a.grouptype='1' and a.groupno in (" + sqlNos + ") ";
            List<Map<String, Object>> list3 = this.doQueryData(sql, null);
            List<String> bizpartnerno = list3.stream().map(var -> var.get("BIZPARTNERNO").toString()).collect(Collectors.toList());
            return bizpartnerno;
        }

        return new ArrayList();
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_BizpartnerOpenQryReq req) throws Exception {
        String langType = req.getLangType();
        String eid = req.geteId();
        String keyTxt = req.getRequest().getKeyTxt();
        String status = req.getRequest().getStatus();
        String[] bizType = req.getRequest().getBizType();

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //分页起始位置
        int startRow = (pageNumber - 1) * pageSize;

        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");


        sqlbuf.append("SELECT * FROM ( " +
                " SELECT COUNT(DISTINCT BIZPARTNERNO) OVER () AS num, DENSE_RANK() OVER (ORDER BY BIZPARTNERNO) AS rn , a.* FROM ( " +
                " SELECT distinct a.*,c.CONTENT as MAINCONTACT,d.TAXNAME,e.name as BILLDATE_DESC,f.org_name as paycentername," +
                " g.INVOICE_NAME,h.CURRENCY,h.name as CURRENCYNAME,i.name as paydate_desc,j.taxrate,j.INCLTAX," +
                " c.ISMAINCONTACT,c.CONTACT as mainConMan, c.CONTYPE,c.CONTENT  as Telephone, " +
                " l.org_name CUSTPAYCENTERNAME,PD0.name as CUSTPAYDATENAME,emp0.name as SALEEMPNAME," +
                " dd0.DEPARTNAME AS SALEDEPTNAME,db1.name CUSTBILLDATENAME, " +
                " it1.INVOICE_NAME as SALEINVOICENAME,j.TAXCALTYPE,to_char(a.begindate,'yyyy-MM-dd') as begindate1,to_char(a.enddate,'yyyy-MM-dd') as enddate1    " +
                " ,bp1.SNAME PAYERNAME,bp2.SNAME PAYEENAME "+
                " FROM DCP_BIZPARTNER a " +
                " LEFT JOIN DCP_BIZPARTNER_CONTACT c ON c.EID=a.EID AND a.bizpartnerNo = c.bizpartnerNo and c.ISMAINCONTACT='Y' " +
                " LEFT JOIN DCP_TAXCATEGORY_LANG d on d.EID=a.eid and d.TAXCODE=a.TAXCODE and d.lang_type='" + req.getLangType() + "' " +
                " left join DCP_BILLDATE_LANG e on e.eid=a.eid and e.billdateno=a.billdateno and e.lang_type='" + req.getLangType() + "' " +
                " left join DCP_BILLDATE_LANG db1 on db1.eid=a.eid and db1.billdateno=a.CUSTBILLDATENO and db1.lang_type='" + req.getLangType() + "' " +
                " left join DCP_ORG_LANG f on f.eid=a.eid and f.ORGANIZATIONNO=a.paycenter and f.lang_type='" + req.getLangType() + "' " +
                " left join DCP_ORG_LANG l on l.eid=a.eid and l.ORGANIZATIONNO=a.CUSTPAYCENTER and l.lang_type='" + req.getLangType() + "' " +
                " left join DCP_INVOICETYPE_LANG g on g.eid =a.eid and g.INVOICECODE=a.INVOICECODE and g.lang_type='" + req.getLangType() + "' " +
                " left join DCP_INVOICETYPE_LANG it1 on it1.eid =a.eid and it1.INVOICECODE=a.SALEINVOICECODE and it1.lang_type='" + req.getLangType() + "' " +
                " left join DCP_CURRENCY_LANG h on h.eid=a.eid and h.currency=a.MAINCURRENCY and h.lang_type='" + req.getLangType() + "' " +
                " left join DCP_PAYDATE_LANG i on i.eid=a.eid and i.PAYDATENO=a.PAYDATENO and i.lang_type='" + req.getLangType() + "' " +
                " left join DCP_TAXCATEGORY j on j.eid=a.eid and j.taxcode=a.taxcode " +
                " left join DCP_PAYDATE_LANG PD0 on PD0.eid=a.eid and PD0.PAYDATENO=a.CUSTPAYDATENO and PD0.lang_type='" + req.getLangType() + "' " +
                " LEFT JOIN DCP_EMPLOYEE emp0 ON emp0.EID=a.EID and emp0.EMPLOYEENO=a.SALEEMPNO " +
                " LEFT JOIN dcp_department_lang dd0 ON dd0.eid = a.eid AND dd0.departno = a.SALEDEPTNO AND dd0.lang_type='" + req.getLangType() + "'" +

                " left join DCP_BIZPARTNER bp1 on bp1.eid=a.eid and bp1.BIZPARTNERNO=a.PAYER " +
                " left join DCP_BIZPARTNER bp2 on bp2.eid=a.eid and bp2.BIZPARTNERNO=a.PAYEE " +

                " WHERE a.EID = '" + eid + "'  " +//and a.organizationno='"+req.getOrganizationNO()+"'
                " ");

        sqlbuf.append(" and trunc(a.BEGINDATE)<=trunc(sysdate)   and trunc(a.enddate)>=trunc(sysdate) ");

        if (!Check.Null(status)) {
            sqlbuf.append(" AND a.status = '" + status + "'");

        }
        if (!Check.Null(keyTxt)) {
            sqlbuf.append(" AND (a.bizpartnerNo LIKE '%%" + keyTxt + "%%' " +
                    " or a.SNAME LIKE '%%" + keyTxt + "%%' " +
                    " or a.FNAME LIKE '%%" + keyTxt + "%%' " +
                    " ) ");
        }

        String bizTypeStr = "";
        if (null != bizType) {
            for (String type : bizType) {
                bizTypeStr += "'" + type + "',";
            }
        }

        if (bizTypeStr.length() > 0) {
            bizTypeStr = bizTypeStr.substring(0, bizTypeStr.length() - 1);
            sqlbuf.append(" AND a.BIZTYPE IN (" + bizTypeStr + ")");
        }


        sqlbuf.append("  ) a ) WHERE RN > " + startRow + " AND rn <= " + (startRow + pageSize) + " ORDER BY BIZPARTNERNO DESC ");

        sql = sqlbuf.toString();
        return sql;
    }

    private String getOrgSql(List<String> bizNos, DCP_BizpartnerOpenQryReq req) throws Exception {
        StringBuffer sJoinNo = new StringBuffer("");
        for (String bizNo : bizNos) {
            sJoinNo.append(bizNo + ",");
        }
        Map<String, String> mapOrder = new HashMap<String, String>();
        mapOrder.put("BIZPARTNERNO", sJoinNo.toString());

        MyCommon cm = new MyCommon();
        String withasSql_bizno = cm.getFormatSourceMultiColWith(mapOrder);
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("  with p AS ( " + withasSql_bizno + " )  " +
                " select a.BIZPARTNERNO,b.ORGANIZATIONNO as orgno,b.org_name as orgname from DCP_BIZPARTNER_ORG a " +
                " inner join p on a.BIZPARTNERNO=p.BIZPARTNERNO " +
                " left join dcp_org_lang b on a.eid=b.eid and a.ORGANIZATIONNO=b.ORGANIZATIONNO and b.lang_type='" + req.getLangType() + "'" +
                " where a.eid= '" + req.geteId() + "' and a.status='100'");

        return sqlbuf.toString();
    }

}
